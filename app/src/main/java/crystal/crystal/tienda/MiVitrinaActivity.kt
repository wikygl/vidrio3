package crystal.crystal.tienda

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import crystal.crystal.databinding.ActivityMiVitrinaBinding
import crystal.crystal.pagos.CanalPagos
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

/**
 * La vitrina propia de un proveedor (docs/TIENDA_ANUNCIOS_PLAN.md, fase 2). Activar o renovar cobra
 * el período del saldo en el servidor (`suscribirProveedor`); cambiar la ficha no cobra
 * (`guardarProveedor`). La ficha la escribe siempre el servidor.
 */
class MiVitrinaActivity : AppCompatActivity() {

    private lateinit var b: ActivityMiVitrinaBinding
    private val repo = TiendaRepositorio()
    private var actual: Proveedor? = null
    private var tarifas = TiendaReglas.Tarifas()
    private var saldoCent = 0L
    /** El logo: el que ya estaba (URL) o uno nuevo elegido del teléfono, que se sube al guardar. */
    private var logoUrl = ""
    private var logoNuevo: Uri? = null
    private val casillas = mutableListOf<CheckBox>()

    private val elegirLogo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            logoNuevo = uri
            Glide.with(this).load(uri).centerCrop().into(b.ivLogo)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMiVitrinaBinding.inflate(layoutInflater)
        setContentView(b.root)
        if (repo.uid == null) {
            finish()
            return
        }
        b.btnAtras.setOnClickListener { finish() }
        b.ivLogo.setOnClickListener { elegirLogo.launch("image/*") }
        TiendaReglas.CATEGORIAS.forEach { cat ->
            val cb = CheckBox(this).apply { text = cat; textSize = 15f }
            casillas += cb
            b.contenedorRubros.addView(cb)
        }
        b.etCiudad.setText(MiCiudad.get(this))
        b.btnGuardar.setOnClickListener { guardar() }
        b.btnActivar.setOnClickListener { activar() }
        cargar()
    }

    private fun cargar() {
        lifecycleScope.launch {
            runCatching { repo.tarifasYSaldo() }.onSuccess { (t, s) -> tarifas = t; saldoCent = s }
            actual = runCatching { repo.miProveedor() }.getOrNull()
            actual?.let { p ->
                b.etNombre.setText(p.nombre)
                b.etCiudad.setText(p.ciudad)
                b.etZonas.setText(p.zonas.joinToString(", "))
                b.etDescripcion.setText(p.descripcion)
                casillas.forEach { it.isChecked = it.text.toString() in p.rubros }
                logoUrl = p.logo
                if (p.logo.isNotBlank()) Glide.with(this@MiVitrinaActivity).load(p.logo).centerCrop().into(b.ivLogo)
            }
            pintarEstado()
        }
    }

    private fun pintarEstado() {
        val p = actual
        val ahora = System.currentTimeMillis()
        val precio = tarifas.proveedorCent
        val dias = tarifas.proveedorDias
        b.tvEstado.text = when {
            p == null -> "Con su vitrina, los talleres de su ciudad lo ven en la Tienda y cuando envían medidas, " +
                "cortes o planos por el chat: un toque y le escriben a usted."
            p.estado == Proveedor.ESTADO_BLOQUEADO -> "Su vitrina está bloqueada por no cumplir las reglas de la tienda. Escríbanos por Soporte."
            p.vigente(ahora) -> "Su vitrina está activa hasta el ${fecha(p.vigenteHastaMs)}."
            else -> "Su vitrina venció el ${fecha(p.vigenteHastaMs)} y ya no se ve. Renuévela para volver a salir."
        }
        b.tvCosto.text = if (precio > 0) "La vitrina cuesta ${soles(precio)} por $dias días. Su saldo: ${soles(saldoCent)}."
        else "La vitrina es gratis por ahora ($dias días)."
        b.btnGuardar.visibility = if (p != null) View.VISIBLE else View.GONE
        b.btnActivar.visibility = if (p?.estado == Proveedor.ESTADO_BLOQUEADO) View.GONE else View.VISIBLE
        b.btnActivar.text = (if (p == null) "Activar" else "Renovar") + if (precio > 0) " · ${soles(precio)}" else ""
    }

    /** La ficha tal como la espera el servidor, o null (ya avisado) si falta algo. */
    private suspend fun leerFicha(): Map<String, Any>? {
        val nombre = b.etNombre.text.toString().replace(Regex("\\s+"), " ").trim()
        val ciudad = b.etCiudad.text.toString().replace(Regex("\\s+"), " ").trim()
        val zonas = TiendaReglas.zonas(b.etZonas.text.toString(), ciudad)
        val rubros = casillas.filter { it.isChecked }.map { it.text.toString() }
        val error = when {
            nombre.length < 3 -> "Escriba el nombre del negocio."
            ciudad.length < 2 -> "Indique la ciudad."
            zonas == null -> "Son ${TiendaReglas.MAX_ZONAS} zonas como máximo, de hasta ${TiendaReglas.MAX_ZONA} letras."
            rubros.isEmpty() -> "Elija al menos un rubro."
            else -> null
        }
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            return null
        }
        logoNuevo?.let { uri ->
            logoUrl = repo.subirLogo(this, uri)
            logoNuevo = null
        }
        return mapOf(
            "nombre" to nombre, "ciudad" to ciudad, "zonas" to zonas.orEmpty(), "rubros" to rubros,
            "descripcion" to b.etDescripcion.text.toString().trim(), "logo" to logoUrl
        )
    }

    private fun guardar() {
        b.btnGuardar.isEnabled = false
        lifecycleScope.launch {
            try {
                val ficha = leerFicha() ?: return@launch
                repo.guardarProveedor(ficha)
                Toast.makeText(this@MiVitrinaActivity, "Cambios guardados", Toast.LENGTH_SHORT).show()
                cargar()
            } catch (e: Exception) {
                Toast.makeText(this@MiVitrinaActivity, TiendaRepositorio.mensaje(e), Toast.LENGTH_LONG).show()
            } finally {
                b.btnGuardar.isEnabled = true
            }
        }
    }

    /** Cobra el período: primero se muestra cuánto cuesta y cómo queda el saldo. */
    private fun activar() {
        if (!CanalPagos.DISPONIBLE_EN_LA_APP) {
            AlertDialog.Builder(this)
                .setTitle("Vitrina de proveedores")
                .setMessage("Activar la vitrina todavía no está disponible en esta versión de Crystal.")
                .setPositiveButton("Entendido", null)
                .show()
            return
        }
        val patron = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("patron_uid", null)
        if (!patron.isNullOrBlank() && patron != repo.uid) {
            Toast.makeText(this, "La vitrina se activa desde la cuenta del patrón, no desde una terminal.", Toast.LENGTH_LONG).show()
            return
        }
        lifecycleScope.launch {
            val (t, saldo) = runCatching { repo.tarifasYSaldo() }.getOrElse {
                Toast.makeText(this@MiVitrinaActivity, TiendaRepositorio.mensaje(it), Toast.LENGTH_LONG).show()
                return@launch
            }
            tarifas = t; saldoCent = saldo
            val precio = t.proveedorCent
            if (saldo < precio) {
                PublicarAnuncioActivity.pedirRecarga(this@MiVitrinaActivity, precio, saldo)
                return@launch
            }
            val ficha = runCatching { leerFicha() }.getOrElse {
                Toast.makeText(this@MiVitrinaActivity, TiendaRepositorio.mensaje(it), Toast.LENGTH_LONG).show()
                return@launch
            } ?: return@launch
            AlertDialog.Builder(this@MiVitrinaActivity)
                .setTitle(if (actual == null) "Activar vitrina" else "Renovar vitrina")
                .setMessage(
                    "Su vitrina se verá ${t.proveedorDias} días" + (if (actual?.vigente(System.currentTimeMillis()) == true) " más" else "") + ".\n\n" +
                        "Cuesta ${if (precio > 0) soles(precio) else "nada"}. Su saldo: ${soles(saldo)} → ${soles(saldo - precio)}."
                )
                .setPositiveButton(if (actual == null) "Activar" else "Renovar") { _, _ ->
                    lifecycleScope.launch {
                        runCatching { repo.suscribirProveedor(ficha) }
                            .onSuccess { hasta ->
                                if (MiCiudad.get(this@MiVitrinaActivity).isBlank()) {
                                    MiCiudad.set(this@MiVitrinaActivity, ficha["ciudad"].toString())
                                }
                                Toast.makeText(this@MiVitrinaActivity, "Vitrina activa hasta el ${fecha(hasta)}", Toast.LENGTH_LONG).show()
                                cargar()
                            }
                            .onFailure { e ->
                                if (TiendaRepositorio.esSaldoInsuficiente(e)) PublicarAnuncioActivity.pedirRecarga(this@MiVitrinaActivity, precio, saldo)
                                else Toast.makeText(this@MiVitrinaActivity, TiendaRepositorio.mensaje(e), Toast.LENGTH_LONG).show()
                            }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun fecha(ms: Long) = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ms))
    private fun soles(cent: Long) = PublicarAnuncioActivity.soles(cent)

    companion object {
        fun intent(ctx: Context) = Intent(ctx, MiVitrinaActivity::class.java)
    }
}
