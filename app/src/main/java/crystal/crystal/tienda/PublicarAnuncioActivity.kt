package crystal.crystal.tienda

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import crystal.crystal.R
import crystal.crystal.databinding.ActivityPublicarAnuncioBinding
import crystal.crystal.pagos.CanalPagos
import java.util.Locale
import kotlinx.coroutines.launch

/**
 * Publicar un anuncio en la Tienda —o editar uno propio—.
 *
 * Publicar cobra del saldo del wallet en el servidor (`publicarAnuncio`): antes se muestran el precio
 * y el saldo, y si no alcanza se lleva a Recargar. Editar no cobra: el dueño cambia el texto, el
 * precio, las fotos, la categoría y la ciudad directamente (las reglas de Firestore lo limitan).
 */
class PublicarAnuncioActivity : AppCompatActivity() {

    private lateinit var b: ActivityPublicarAnuncioBinding
    private val repo = TiendaRepositorio()
    private val prefs by lazy { getSharedPreferences(PREFS, MODE_PRIVATE) }

    /** El id se fija al abrir: las fotos se suben a su carpeta y un reintento no publica dos veces. */
    private lateinit var anuncioId: String
    private var editando: Anuncio? = null
    private var tipo = TiendaReglas.PRODUCTO
    private var condicion = TiendaReglas.NUEVO

    /** Las fotos del formulario: las que ya estaban en el anuncio y las nuevas del teléfono. */
    private sealed class Foto {
        data class Subida(val grande: String, val mini: String) : Foto()
        data class Nueva(val uri: Uri) : Foto()
    }
    private val fotos = mutableListOf<Foto>()

    private var precioCent = TiendaReglas.PRECIO_POR_DEFECTO_CENT
    private var dias = TiendaReglas.DIAS_POR_DEFECTO
    private var saldoCent = 0L
    /** Todas las tarifas: un servicio puede costar distinto que un producto. */
    private var tarifas = TiendaReglas.Tarifas()
    private var tarifasCargadas = false

    private val elegirFotos = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        val caben = TiendaReglas.MAX_FOTOS - fotos.size
        if (uris.size > caben) {
            Toast.makeText(this, "Son ${TiendaReglas.MAX_FOTOS} fotos como máximo", Toast.LENGTH_SHORT).show()
        }
        uris.take(caben.coerceAtLeast(0)).forEach { fotos += Foto.Nueva(it) }
        pintarFotos()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPublicarAnuncioBinding.inflate(layoutInflater)
        setContentView(b.root)
        if (repo.uid == null) {
            finish()
            return
        }
        val idEdicion = intent.getStringExtra(EXTRA_ID)
        anuncioId = idEdicion ?: savedInstanceState?.getString(ESTADO_ID) ?: repo.nuevoId()

        b.btnAtras.setOnClickListener { finish() }
        b.spCategoria.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, listOf("Categoría…") + TiendaReglas.CATEGORIAS
        )
        b.spUnidad.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, TiendaReglas.UNIDADES)
        b.btnProducto.setOnClickListener { ponerTipo(TiendaReglas.PRODUCTO) }
        b.btnServicio.setOnClickListener { ponerTipo(TiendaReglas.SERVICIO) }
        b.btnNuevo.setOnClickListener { ponerCondicion(TiendaReglas.NUEVO) }
        b.btnUsado.setOnClickListener { ponerCondicion(TiendaReglas.USADO) }
        b.cbATratar.setOnCheckedChangeListener { _, aTratar ->
            b.etPrecio.isEnabled = !aTratar
            b.spUnidad.isEnabled = !aTratar
            if (aTratar) b.etPrecio.setText("")
        }
        b.btnPublicar.setOnClickListener { if (editando != null) guardarCambios() else publicar() }

        ponerTipo(TiendaReglas.PRODUCTO)
        ponerCondicion(TiendaReglas.NUEVO)
        pintarFotos()

        if (idEdicion != null) {
            b.tvTituloPantalla.text = "Editar anuncio"
            b.btnPublicar.text = "Guardar cambios"
            b.tvCosto.visibility = View.GONE
            cargarParaEditar(idEdicion)
        } else {
            b.etCiudad.setText(prefs.getString(K_CIUDAD, "").orEmpty())
            cargarPrecio()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::anuncioId.isInitialized) outState.putString(ESTADO_ID, anuncioId)
    }

    private fun ponerTipo(nuevo: String) {
        tipo = nuevo
        val servicio = nuevo == TiendaReglas.SERVICIO
        elegir(b.btnProducto, !servicio)
        elegir(b.btnServicio, servicio)
        // Un servicio no es nuevo ni usado, y se ofrece en una zona, no "está" en un lugar.
        b.lyCondicion.visibility = if (servicio) View.GONE else View.VISIBLE
        b.lyZonas.visibility = if (servicio) View.VISIBLE else View.GONE
        b.tvRotuloCiudad.text = if (servicio) "CIUDAD DONDE ATIENDE" else "UBICACIÓN"
        // Un servicio se cobra por trabajo, por tiempo o por medida: el desplegable solo ofrece eso.
        val unidades = unidades()
        val antes = b.spUnidad.selectedItem as? String
        b.spUnidad.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unidades)
        b.spUnidad.setSelection(
            unidades.indexOf(antes).takeIf { it >= 0 } ?: unidades.indexOf(if (servicio) "servicio" else "c/u").coerceAtLeast(0)
        )
        pintarCosto()
    }

    private fun unidades() = if (tipo == TiendaReglas.SERVICIO) TiendaReglas.UNIDADES_SERVICIO else TiendaReglas.UNIDADES

    private fun ponerCondicion(nueva: String) {
        condicion = nueva
        elegir(b.btnNuevo, nueva == TiendaReglas.NUEVO)
        elegir(b.btnUsado, nueva == TiendaReglas.USADO)
    }

    /** Un botón de elección: el elegido, azul lleno; el otro, blanco. */
    private fun elegir(v: TextView, si: Boolean) {
        v.background = ContextCompat.getDrawable(this, if (si) R.drawable.bg_accion_principal else R.drawable.bg_accion_item)
        v.setTextColor(ContextCompat.getColor(this, if (si) R.color.blanco else R.color.azul))
    }

    // ───────────── Fotos ─────────────

    private fun pintarFotos() {
        val c = b.contenedorFotos
        c.removeAllViews()
        val lado = dp(92)
        fotos.forEachIndexed { i, f ->
            val marco = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(lado, lado).apply { marginEnd = dp(8) }
                background = ContextCompat.getDrawable(this@PublicarAnuncioActivity, R.drawable.bg_control_panel)
                outlineProvider = android.view.ViewOutlineProvider.BACKGROUND
                clipToOutline = true
            }
            val iv = ImageView(this).apply {
                layoutParams = FrameLayout.LayoutParams(lado, lado)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            when (f) {
                is Foto.Subida -> Glide.with(this).load(f.mini).centerCrop().into(iv)
                is Foto.Nueva -> Glide.with(this).load(f.uri).centerCrop().into(iv)
            }
            val quitar = TextView(this).apply {
                text = "✕"
                textSize = 14f
                gravity = Gravity.CENTER
                setTextColor(ContextCompat.getColor(this@PublicarAnuncioActivity, R.color.blanco))
                background = ContextCompat.getDrawable(this@PublicarAnuncioActivity, R.drawable.bg_chip_filtro_elegido)
                layoutParams = FrameLayout.LayoutParams(dp(26), dp(26), Gravity.TOP or Gravity.END).apply { setMargins(0, dp(4), dp(4), 0) }
                setOnClickListener { fotos.removeAt(i); pintarFotos() }
            }
            if (i == 0) {
                // La primera es la que sale en la lista: se dice, para que la elijan bien.
                marco.addView(iv)
                marco.addView(TextView(this).apply {
                    text = "Portada"
                    textSize = 10f
                    setTextColor(ContextCompat.getColor(this@PublicarAnuncioActivity, R.color.blanco))
                    setBackgroundColor(0x99000000.toInt())
                    gravity = Gravity.CENTER
                    layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM)
                })
            } else {
                marco.addView(iv)
            }
            marco.addView(quitar)
            c.addView(marco)
        }
        if (fotos.size < TiendaReglas.MAX_FOTOS) {
            c.addView(TextView(this).apply {
                text = "＋\nFoto"
                gravity = Gravity.CENTER
                textSize = 14f
                setTextColor(ContextCompat.getColor(this@PublicarAnuncioActivity, R.color.azul))
                background = ContextCompat.getDrawable(this@PublicarAnuncioActivity, R.drawable.bg_accion_item)
                layoutParams = LinearLayout.LayoutParams(lado, lado)
                setOnClickListener { elegirFotos.launch("image/*") }
            })
        }
    }

    // ───────────── Precio y saldo ─────────────

    private fun cargarPrecio() {
        lifecycleScope.launch {
            runCatching { repo.tarifasYSaldo() }.onSuccess { (t, s) ->
                tarifas = t; saldoCent = s; tarifasCargadas = true
                pintarCosto()
            }
        }
    }

    /** El costo del tipo elegido: al cambiar de producto a servicio puede cambiar. */
    private fun pintarCosto() {
        if (!tarifasCargadas || editando != null || intent.hasExtra(EXTRA_ID)) return
        val (p, d) = tarifas.publicar(tipo)
        precioCent = p; dias = d
        b.tvCosto.text = if (p > 0) {
            "Publicar cuesta ${soles(p)} y el anuncio dura $d días. Se descuenta de su saldo: ${soles(saldoCent)}."
        } else {
            "Publicar es gratis por ahora. El anuncio dura $d días."
        }
        b.btnPublicar.text = if (p > 0) "Publicar · ${soles(p)}" else "Publicar"
    }

    // ───────────── Leer el formulario ─────────────

    private data class Formulario(
        val categoria: String, val titulo: String, val descripcion: String, val precioCent: Long,
        val unidad: String, val condicion: String, val ciudad: String, val zonas: List<String>
    )

    /** Lo escrito, revisado con las mismas reglas que el servidor. Muestra el error y devuelve null. */
    private fun leer(): Formulario? {
        val categoria = (b.spCategoria.selectedItem as? String).orEmpty().takeIf { it in TiendaReglas.CATEGORIAS }.orEmpty()
        val titulo = b.etTitulo.text.toString().replace(Regex("\\s+"), " ").trim()
        val descripcion = b.etDescripcion.text.toString().trim()
        val aTratar = b.cbATratar.isChecked
        val precio = if (aTratar) 0L else TiendaRepositorio.aCentimos(b.etPrecio.text.toString())
        val unidad = if (aTratar) (if (tipo == TiendaReglas.SERVICIO) "servicio" else "c/u")
        else (b.spUnidad.selectedItem as? String).orEmpty()
        val cond = if (tipo == TiendaReglas.SERVICIO) "" else condicion
        val ciudad = b.etCiudad.text.toString().replace(Regex("\\s+"), " ").trim()
        val zonas = if (tipo == TiendaReglas.SERVICIO) TiendaReglas.zonas(b.etZonas.text.toString(), ciudad) else emptyList()

        val error = when {
            !aTratar && (precio == null || precio <= 0L) -> "Escriba el precio o marque «Precio a tratar»."
            zonas == null -> "Son ${TiendaReglas.MAX_ZONAS} zonas como máximo, de hasta ${TiendaReglas.MAX_ZONA} letras cada una."
            else -> TiendaReglas.error(tipo, categoria, titulo, descripcion, precio, unidad, cond, ciudad, fotos.size)
        }
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            return null
        }
        return Formulario(categoria, titulo, descripcion, precio ?: 0L, unidad, cond, ciudad, zonas.orEmpty())
    }

    // ───────────── Publicar ─────────────

    private fun publicar() {
        val f = leer() ?: return
        if (!CanalPagos.DISPONIBLE_EN_LA_APP) {
            avisarNoDisponible(this)
            return
        }
        // Una terminal no publica: el cobro saldría de una cuenta sin saldo y el chat de contacto
        // llegaría a una bandeja que nadie mira. Lo hace la cuenta del patrón.
        val patron = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("patron_uid", null)
        if (!patron.isNullOrBlank() && patron != repo.uid) {
            AlertDialog.Builder(this)
                .setTitle("Desde la cuenta del patrón")
                .setMessage("Los anuncios se publican desde la cuenta principal del negocio, no desde una terminal.")
                .setPositiveButton("Entendido", null)
                .show()
            return
        }
        if (prefs.getInt(K_REGLAS, 0) < TiendaReglas.REGLAS_VERSION) {
            AlertDialog.Builder(this)
                .setTitle("Reglas de la Tienda")
                .setMessage(TiendaReglas.TEXTO_REGLAS)
                .setPositiveButton("Acepto") { _, _ ->
                    prefs.edit().putInt(K_REGLAS, TiendaReglas.REGLAS_VERSION).apply()
                    confirmarCobro(f)
                }
                .setNegativeButton("Cancelar", null)
                .show()
            return
        }
        confirmarCobro(f)
    }

    /** Con el precio y el saldo al día: si alcanza, confirma; si no, lleva a recargar. */
    private fun confirmarCobro(f: Formulario) {
        lifecycleScope.launch {
            runCatching { repo.precioYSaldo(tipo) }
                .onSuccess { (p, d, s) -> precioCent = p; dias = d; saldoCent = s }
                .onFailure {
                    Toast.makeText(this@PublicarAnuncioActivity, "Sin conexión: ${TiendaRepositorio.mensaje(it)}", Toast.LENGTH_LONG).show()
                    return@launch
                }
            if (saldoCent < precioCent) {
                pedirRecarga(this@PublicarAnuncioActivity, precioCent, saldoCent)
                return@launch
            }
            val mensaje = if (precioCent > 0) {
                "Su anuncio se verá $dias días en la Tienda.\n\n" +
                    "Se descontarán ${soles(precioCent)} de su saldo: ${soles(saldoCent)} → ${soles(saldoCent - precioCent)}."
            } else {
                "Su anuncio se verá $dias días en la Tienda. Por ahora publicar es gratis."
            }
            AlertDialog.Builder(this@PublicarAnuncioActivity)
                .setTitle("Publicar anuncio")
                .setMessage(mensaje)
                .setPositiveButton("Publicar") { _, _ -> subirYPublicar(f) }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun subirYPublicar(f: Formulario) {
        val progreso = dialogoProgreso("Preparando…")
        b.btnPublicar.isEnabled = false
        lifecycleScope.launch {
            try {
                val (grandes, minis) = subirFotos { progreso.second.text = it }
                progreso.second.text = "Publicando…"
                val campos = mapOf(
                    "tipo" to tipo,
                    "categoria" to f.categoria,
                    "titulo" to f.titulo,
                    "descripcion" to f.descripcion,
                    "precioCent" to f.precioCent,
                    "unidad" to f.unidad,
                    "condicion" to f.condicion,
                    "ciudad" to f.ciudad,
                    "zonas" to f.zonas,
                    "fotos" to grandes,
                    "miniaturas" to minis,
                    "reglasVersion" to TiendaReglas.REGLAS_VERSION
                )
                repo.publicar(anuncioId, campos)
                prefs.edit().putString(K_CIUDAD, f.ciudad).apply()
                progreso.first.dismiss()
                Toast.makeText(this@PublicarAnuncioActivity, "¡Anuncio publicado!", Toast.LENGTH_LONG).show()
                startActivity(AnuncioActivity.intent(this@PublicarAnuncioActivity, anuncioId))
                finish()
            } catch (e: Exception) {
                progreso.first.dismiss()
                android.util.Log.e("PublicarAnuncio", "No se pudo publicar", e)
                if (TiendaRepositorio.esSaldoInsuficiente(e)) pedirRecarga(this@PublicarAnuncioActivity, precioCent, saldoCent)
                else mostrarError(e)
            } finally {
                b.btnPublicar.isEnabled = true
            }
        }
    }

    /**
     * Sube las fotos nuevas y devuelve todas, en orden, ya como enlaces. Las subidas quedan en la
     * lista: si después falla el cobro y se reintenta, no se vuelven a subir.
     */
    private suspend fun subirFotos(avance: (String) -> Unit): Pair<List<String>, List<String>> {
        val nuevas = fotos.count { it is Foto.Nueva }
        var hechas = 0
        for (i in fotos.indices) {
            val f = fotos[i]
            if (f is Foto.Nueva) {
                hechas++
                avance("Subiendo foto $hechas de $nuevas…")
                val (g, m) = repo.subirFoto(this, anuncioId, f.uri, i)
                fotos[i] = Foto.Subida(g, m)
            }
        }
        val subidas = fotos.filterIsInstance<Foto.Subida>()
        return subidas.map { it.grande } to subidas.map { it.mini }
    }

    // ───────────── Editar ─────────────

    private fun cargarParaEditar(id: String) {
        lifecycleScope.launch {
            val a = runCatching { repo.obtener(id) }.getOrNull()
            if (a == null || a.uid != repo.uid) {
                Toast.makeText(this@PublicarAnuncioActivity, "No se encontró el anuncio", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }
            editando = a
            ponerTipo(a.tipo)
            if (a.condicion.isNotBlank()) ponerCondicion(a.condicion)
            b.spCategoria.setSelection((TiendaReglas.CATEGORIAS.indexOf(a.categoria) + 1).coerceAtLeast(0))
            b.etTitulo.setText(a.titulo)
            b.etDescripcion.setText(a.descripcion)
            if (a.precioCent <= 0L) b.cbATratar.isChecked = true
            else b.etPrecio.setText(java.math.BigDecimal(a.precioCent).movePointLeft(2).toPlainString())
            unidades().indexOf(a.unidad).takeIf { it >= 0 }?.let { b.spUnidad.setSelection(it) }
            b.etCiudad.setText(a.ciudad)
            b.etZonas.setText(a.zonas.joinToString(", "))
            fotos.clear()
            a.fotos.forEachIndexed { i, g -> fotos += Foto.Subida(g, a.miniaturas.getOrElse(i) { g }) }
            pintarFotos()
        }
    }

    private fun guardarCambios() {
        val a = editando ?: return
        val f = leer() ?: return
        val progreso = dialogoProgreso("Guardando…")
        b.btnPublicar.isEnabled = false
        lifecycleScope.launch {
            try {
                val (grandes, minis) = subirFotos { progreso.second.text = it }
                progreso.second.text = "Guardando…"
                repo.guardarCambios(
                    a.id,
                    mapOf(
                        "tipo" to tipo,
                        "categoria" to f.categoria,
                        "titulo" to f.titulo,
                        "descripcion" to f.descripcion,
                        "precioCent" to f.precioCent,
                        "unidad" to f.unidad,
                        "condicion" to f.condicion,
                        "ciudad" to f.ciudad,
                        "zonas" to f.zonas,
                        "fotos" to grandes,
                        "miniaturas" to minis,
                        "palabras" to TiendaReglas.palabrasDe(f.titulo, f.categoria, f.ciudad, tipo, f.zonas)
                    )
                )
                // Las fotos que se quitaron ya no las usa nadie: se borran para no pagar por guardarlas.
                val quitadas = (a.fotos + a.miniaturas) - (grandes + minis).toSet()
                repo.borrarFotos(quitadas)
                progreso.first.dismiss()
                Toast.makeText(this@PublicarAnuncioActivity, "Cambios guardados", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                progreso.first.dismiss()
                android.util.Log.e("PublicarAnuncio", "No se pudo guardar", e)
                mostrarError(e)
            } finally {
                b.btnPublicar.isEnabled = true
            }
        }
    }

    // ───────────── Piezas ─────────────

    private fun mostrarError(e: Throwable) {
        AlertDialog.Builder(this)
            .setTitle("No se pudo completar")
            .setMessage(TiendaRepositorio.mensaje(e))
            .setPositiveButton("Entendido", null)
            .show()
    }

    /** Un diálogo que no se cierra solo, con una línea de avance. */
    private fun dialogoProgreso(texto: String): Pair<AlertDialog, TextView> {
        val tv = TextView(this).apply {
            this.text = texto
            textSize = 15f
            setPadding(dp(16), 0, 0, 0)
        }
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(24), dp(20), dp(24), dp(20))
            addView(ProgressBar(this@PublicarAnuncioActivity))
            addView(tv)
        }
        val d = AlertDialog.Builder(this).setView(fila).setCancelable(false).create()
        d.show()
        return d to tv
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    companion object {
        const val EXTRA_ID = "anuncio_id_editar"
        private const val ESTADO_ID = "anuncio_id_nuevo"
        private const val PREFS = "tienda"
        private const val K_CIUDAD = "ultima_ciudad"
        private const val K_REGLAS = "reglas_aceptadas"

        fun intentNuevo(ctx: Context) = Intent(ctx, PublicarAnuncioActivity::class.java)
        fun intentEditar(ctx: Context, id: String) =
            Intent(ctx, PublicarAnuncioActivity::class.java).putExtra(EXTRA_ID, id)

        fun soles(cent: Long) = "S/ " + "%,.2f".format(Locale.US, cent / 100.0)

        /** No alcanza el saldo: cuánto falta y un atajo a Recargar. */
        fun pedirRecarga(activity: Activity, precioCent: Long, saldoCent: Long) {
            AlertDialog.Builder(activity)
                .setTitle("Saldo insuficiente")
                .setMessage(
                    "El anuncio cuesta ${soles(precioCent)} y su saldo es ${soles(saldoCent)}.\n\n" +
                        "Recargue su saldo y vuelva a intentarlo: lo escrito no se pierde mientras no salga de esta pantalla."
                )
                .setPositiveButton("Recargar") { _, _ -> CanalPagos.abrirRecarga(activity) }
                .setNegativeButton("Ahora no", null)
                .show()
        }

        /** En la build sin pagos dentro de la app (Play) se ve la Tienda, pero no se publica. */
        fun avisarNoDisponible(activity: Activity) {
            AlertDialog.Builder(activity)
                .setTitle("Publicar anuncios")
                .setMessage(
                    "Publicar anuncios todavía no está disponible en esta versión de Crystal. " +
                        "Puede ver los anuncios y contactar a los vendedores."
                )
                .setPositiveButton("Entendido", null)
                .show()
        }
    }
}
