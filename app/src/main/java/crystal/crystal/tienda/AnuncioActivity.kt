package crystal.crystal.tienda

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import crystal.crystal.databinding.ActivityAnuncioBinding
import crystal.crystal.pagos.CanalPagos
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

/**
 * Un anuncio de la Tienda. Al comprador le ofrece contactar al vendedor por el chat de Crystal —con
 * el mensaje ya escrito— y denunciarlo; al dueño, editarlo, pausarlo, renovarlo y retirarlo.
 */
class AnuncioActivity : AppCompatActivity() {

    private lateinit var b: ActivityAnuncioBinding
    private val repo = TiendaRepositorio()
    private lateinit var anuncioId: String
    private var anuncio: Anuncio? = null
    private var vistaContada = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAnuncioBinding.inflate(layoutInflater)
        setContentView(b.root)
        anuncioId = intent.getStringExtra(EXTRA_ID).orEmpty()
        if (anuncioId.isEmpty() || repo.uid == null) {
            finish()
            return
        }
        b.btnAtras.setOnClickListener { finish() }
        b.rvFotos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        PagerSnapHelper().attachToRecyclerView(b.rvFotos)
        b.rvFotos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) contarFoto()
            }
        })
        b.btnContactar.setOnClickListener { contactar() }
        b.btnDenunciar.setOnClickListener { denunciar() }
        b.btnEditar.setOnClickListener { startActivity(PublicarAnuncioActivity.intentEditar(this, anuncioId)) }
        b.btnPausar.setOnClickListener { pausarOReactivar() }
        b.btnRenovar.setOnClickListener { renovar() }
        b.btnRetirar.setOnClickListener { retirar() }
        b.btnDestacar.setOnClickListener { destacar() }
    }

    // Al volver de editarlo, se ve como quedó.
    override fun onResume() {
        super.onResume()
        cargar()
    }

    private fun cargar() {
        lifecycleScope.launch {
            val a = runCatching { repo.obtener(anuncioId) }.getOrNull()
            if (a == null) {
                Toast.makeText(this@AnuncioActivity, "El anuncio ya no está disponible", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }
            anuncio = a
            mostrar(a)
            if (!vistaContada && a.uid != repo.uid) {
                vistaContada = true
                repo.sumarVista(a.id)
            }
        }
    }

    private fun esMio(a: Anuncio) = a.uid == repo.uid

    private fun mostrar(a: Anuncio) {
        val ahora = System.currentTimeMillis()
        b.tvPrecio.text = TiendaReglas.precio(a.precioCent) +
            if (a.precioCent > 0 && a.unidad.isNotBlank()) " ${a.unidad}" else ""
        b.tvTitulo.text = a.titulo
        b.tvDatos.text = listOf(
            if (a.esServicio) "Servicio" else "Producto",
            when (a.condicion) { TiendaReglas.NUEVO -> "Nuevo"; TiendaReglas.USADO -> "Usado"; else -> "" },
            a.categoria
        ).filter { it.isNotBlank() }.joinToString("  ·  ")
        b.tvLugar.text = listOf(
            a.ciudad,
            if (a.zonas.isNotEmpty()) "atiende también " + a.zonas.joinToString(", ") else "",
            TiendaReglas.hace(a.publicadoEnMs, ahora)
        ).filter { it.isNotBlank() }.joinToString("  ·  ")
        // Un servicio se cotiza: el trabajo depende del lugar y de la medida.
        b.btnContactar.text = if (a.esServicio) "Pedir cotización" else "Contactar por chat"
        b.tvDescripcion.text = a.descripcion.ifBlank { "Sin descripción." }
        b.tvVendedor.text = a.vendedorNombre.ifBlank { "Usuario de Crystal" }

        val fotos = a.fotos.ifEmpty { a.miniaturas }
        b.rvFotos.adapter = AdaptadorFotos(fotos) { verFoto(it) }
        b.tvSinFotos.visibility = if (fotos.isEmpty()) View.VISIBLE else View.GONE
        b.tvContadorFotos.visibility = if (fotos.size > 1) View.VISIBLE else View.GONE
        contarFoto()

        val mio = esMio(a)
        b.barraComprador.visibility = if (mio) View.GONE else View.VISIBLE
        b.barraDueno.visibility = if (mio) View.VISIBLE else View.GONE
        b.tvEstado.visibility = if (mio) View.VISIBLE else View.GONE
        if (mio) mostrarParaElDueno(a, ahora)
    }

    /** Lo que el dueño puede hacer depende del estado: lo vencido solo se renueva; lo revisado, nada. */
    private fun mostrarParaElDueno(a: Anuncio, ahora: Long) {
        val vencido = a.estado == Anuncio.ESTADO_VENCIDO ||
            (a.venceEnMs <= ahora && a.estado in listOf(Anuncio.ESTADO_ACTIVO, Anuncio.ESTADO_PAUSADO))
        val enRevision = a.estado == Anuncio.ESTADO_REVISION || a.estado == Anuncio.ESTADO_BLOQUEADO
        val estado = if (vencido) "Vencido" else TiendaReglas.nombreEstado(a.estado)
        b.tvEstado.text = when {
            enRevision -> "$estado: el equipo de Crystal lo está revisando por denuncias. Mientras tanto no se ve en la Tienda."
            vencido -> "Vencido · ya no se ve en la Tienda. Renuévelo para que vuelva a salir."
            else -> "$estado · ${TiendaReglas.vence(a.venceEnMs, ahora)} · ${a.vistas} vistas" +
                if (a.destacado(ahora)) "\n★ Destacado hasta el ${fecha(a.destacadoHastaMs)}" else ""
        }
        val editable = !vencido && !enRevision
        b.btnEditar.visibility = if (editable) View.VISIBLE else View.GONE
        b.btnPausar.visibility = if (editable) View.VISIBLE else View.GONE
        // Solo se destaca lo que se está viendo en la Tienda.
        b.btnDestacar.visibility = if (editable && a.estado == Anuncio.ESTADO_ACTIVO) View.VISIBLE else View.GONE
        b.btnRetirar.visibility = if (editable && a.estado != Anuncio.ESTADO_RETIRADO) View.VISIBLE else View.GONE
        b.btnRenovar.visibility = if (enRevision) View.GONE else View.VISIBLE
        b.btnPausar.text = if (a.estado == Anuncio.ESTADO_ACTIVO) "Pausar" else "Volver a publicar"
    }

    private fun contarFoto() {
        val lm = b.rvFotos.layoutManager as? LinearLayoutManager ?: return
        val total = b.rvFotos.adapter?.itemCount ?: 0
        val actual = lm.findFirstCompletelyVisibleItemPosition().takeIf { it >= 0 }
            ?: lm.findFirstVisibleItemPosition().coerceAtLeast(0)
        b.tvContadorFotos.text = "${actual + 1} / $total"
    }

    /** La foto entera, a pantalla completa. */
    private fun verFoto(url: String) {
        val iv = ImageView(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundColor(Color.BLACK)
        }
        val d = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        d.setContentView(iv)
        d.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        iv.setOnClickListener { d.dismiss() }
        Glide.with(this).load(url).into(iv)
        d.show()
    }

    // ───────────── Comprador ─────────────

    /** Abre (o crea) el chat con el vendedor y deja escrito el primer mensaje. */
    private fun contactar() {
        val a = anuncio ?: return
        b.btnContactar.isEnabled = false
        lifecycleScope.launch {
            try {
                val precio = TiendaReglas.precio(a.precioCent)
                val miCiudad = MiCiudad.get(this@AnuncioActivity)
                val texto = if (a.esServicio) {
                    "Hola, vi su servicio en la Tienda: «${a.titulo}». " +
                        (if (miCiudad.isNotBlank()) "Estoy en $miCiudad. " else "") + "¿Me puede cotizar?"
                } else {
                    "Hola, me interesa su anuncio de la Tienda: «${a.titulo}» ($precio). ¿Sigue disponible?"
                }
                ChatDeTienda.abrir(this@AnuncioActivity, a.uid, texto)
            } catch (e: Exception) {
                Toast.makeText(this@AnuncioActivity, "No se pudo abrir el chat: ${TiendaRepositorio.mensaje(e)}", Toast.LENGTH_LONG).show()
            } finally {
                b.btnContactar.isEnabled = true
            }
        }
    }

    private fun denunciar() {
        val a = anuncio ?: return
        lifecycleScope.launch {
            if (repo.yaDenuncie(a.id)) {
                Toast.makeText(this@AnuncioActivity, "Ya denunció este anuncio. Gracias, lo revisaremos.", Toast.LENGTH_LONG).show()
                return@launch
            }
            val motivos = arrayOf(
                "Engañoso: el precio o las fotos no son del producto",
                "Prohibido, robado o ilegal",
                "No es del rubro",
                "Repetido",
                "Otro"
            )
            var elegido = -1
            val etDetalle = EditText(this@AnuncioActivity).apply {
                hint = "Cuéntenos qué pasa (opcional)"
                maxLines = 4
            }
            val cont = LinearLayout(this@AnuncioActivity).apply {
                orientation = LinearLayout.VERTICAL
                val p = (20 * resources.displayMetrics.density).toInt()
                setPadding(p, 0, p, 0)
                addView(etDetalle)
            }
            AlertDialog.Builder(this@AnuncioActivity)
                .setTitle("Denunciar anuncio")
                .setSingleChoiceItems(motivos, -1) { _, i -> elegido = i }
                .setView(cont)
                .setPositiveButton("Denunciar") { _, _ ->
                    if (elegido < 0) {
                        Toast.makeText(this@AnuncioActivity, "Elija el motivo", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    lifecycleScope.launch {
                        runCatching { repo.denunciar(a.id, motivos[elegido], etDetalle.text.toString().trim()) }
                            .onSuccess {
                                Toast.makeText(this@AnuncioActivity, "Gracias. Lo revisaremos.", Toast.LENGTH_LONG).show()
                            }
                            .onFailure {
                                Toast.makeText(this@AnuncioActivity, "No se pudo enviar: ${TiendaRepositorio.mensaje(it)}", Toast.LENGTH_LONG).show()
                            }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    // ───────────── Dueño ─────────────

    private fun pausarOReactivar() {
        val a = anuncio ?: return
        val nuevo = if (a.estado == Anuncio.ESTADO_ACTIVO) Anuncio.ESTADO_PAUSADO else Anuncio.ESTADO_ACTIVO
        lifecycleScope.launch {
            runCatching { repo.cambiarEstado(a.id, nuevo) }
                .onSuccess {
                    Toast.makeText(
                        this@AnuncioActivity,
                        if (nuevo == Anuncio.ESTADO_PAUSADO) "Pausado: no se ve en la Tienda hasta que lo vuelva a publicar"
                        else "Publicado de nuevo",
                        Toast.LENGTH_LONG
                    ).show()
                    cargar()
                }
                .onFailure { Toast.makeText(this@AnuncioActivity, TiendaRepositorio.mensaje(it), Toast.LENGTH_LONG).show() }
        }
    }

    private fun retirar() {
        val a = anuncio ?: return
        AlertDialog.Builder(this)
            .setTitle("Retirar anuncio")
            .setMessage("Deja de verse en la Tienda. Mientras no venza, puede volver a publicarlo sin pagar.")
            .setPositiveButton("Retirar") { _, _ ->
                lifecycleScope.launch {
                    runCatching { repo.cambiarEstado(a.id, Anuncio.ESTADO_RETIRADO) }
                        .onSuccess { cargar() }
                        .onFailure { Toast.makeText(this@AnuncioActivity, TiendaRepositorio.mensaje(it), Toast.LENGTH_LONG).show() }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Cobra otro período del saldo, como al publicar. */
    private fun renovar() {
        val a = anuncio ?: return
        if (!CanalPagos.DISPONIBLE_EN_LA_APP) {
            PublicarAnuncioActivity.avisarNoDisponible(this)
            return
        }
        lifecycleScope.launch {
            val (precio, dias, saldo) = runCatching { repo.precioYSaldo(a.tipo) }.getOrElse {
                Toast.makeText(this@AnuncioActivity, TiendaRepositorio.mensaje(it), Toast.LENGTH_LONG).show()
                return@launch
            }
            if (saldo < precio) {
                PublicarAnuncioActivity.pedirRecarga(this@AnuncioActivity, precio, saldo)
                return@launch
            }
            AlertDialog.Builder(this@AnuncioActivity)
                .setTitle("Renovar anuncio")
                .setMessage(
                    "Suma $dias días y vuelve a salir primero en la Tienda.\n\n" +
                        "Cuesta ${if (precio > 0) soles(precio) else "nada"}. " +
                        "Su saldo: ${soles(saldo)} → ${soles(saldo - precio)}."
                )
                .setPositiveButton("Renovar") { _, _ ->
                    lifecycleScope.launch {
                        runCatching { repo.renovar(a.id) }
                            .onSuccess { vence ->
                                val f = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(vence))
                                Toast.makeText(this@AnuncioActivity, "Renovado hasta el $f", Toast.LENGTH_LONG).show()
                                cargar()
                            }
                            .onFailure { e ->
                                if (TiendaRepositorio.esSaldoInsuficiente(e)) PublicarAnuncioActivity.pedirRecarga(this@AnuncioActivity, precio, saldo)
                                else Toast.makeText(this@AnuncioActivity, TiendaRepositorio.mensaje(e), Toast.LENGTH_LONG).show()
                            }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    /**
     * Cobra unos días de "Destacado": sale primero en la Tienda, con su marca. Si el anuncio vence
     * antes de que terminen esos días, el servidor pide renovarlo primero y aquí se dice tal cual.
     */
    private fun destacar() {
        val a = anuncio ?: return
        if (!CanalPagos.DISPONIBLE_EN_LA_APP) {
            PublicarAnuncioActivity.avisarNoDisponible(this)
            return
        }
        lifecycleScope.launch {
            val (t, saldo) = runCatching { repo.tarifasYSaldo() }.getOrElse {
                Toast.makeText(this@AnuncioActivity, TiendaRepositorio.mensaje(it), Toast.LENGTH_LONG).show()
                return@launch
            }
            val precio = t.destacarCent
            if (saldo < precio) {
                PublicarAnuncioActivity.pedirRecarga(this@AnuncioActivity, precio, saldo)
                return@launch
            }
            val ahora = System.currentTimeMillis()
            AlertDialog.Builder(this@AnuncioActivity)
                .setTitle("Destacar anuncio")
                .setMessage(
                    (if (a.destacado(ahora)) "Ya está destacado hasta el ${fecha(a.destacadoHastaMs)}; se suman ${t.destacarDias} días más.\n\n"
                    else "Sale primero en la Tienda, con la marca «Destacado», durante ${t.destacarDias} días.\n\n") +
                        "Cuesta ${if (precio > 0) soles(precio) else "nada"}. Su saldo: ${soles(saldo)} → ${soles(saldo - precio)}."
                )
                .setPositiveButton("Destacar") { _, _ ->
                    lifecycleScope.launch {
                        runCatching { repo.destacar(a.id) }
                            .onSuccess { hasta ->
                                Toast.makeText(this@AnuncioActivity, "Destacado hasta el ${fecha(hasta)}", Toast.LENGTH_LONG).show()
                                cargar()
                            }
                            .onFailure { e ->
                                if (TiendaRepositorio.esSaldoInsuficiente(e)) PublicarAnuncioActivity.pedirRecarga(this@AnuncioActivity, precio, saldo)
                                else AlertDialog.Builder(this@AnuncioActivity)
                                    .setTitle("No se pudo destacar")
                                    .setMessage(TiendaRepositorio.mensaje(e))
                                    .setPositiveButton("Entendido", null)
                                    .show()
                            }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun fecha(ms: Long) = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ms))

    private fun soles(cent: Long) = "S/ " + "%,.2f".format(Locale.US, cent / 100.0)

    companion object {
        const val EXTRA_ID = "anuncio_id"
        fun intent(ctx: Context, id: String) = Intent(ctx, AnuncioActivity::class.java).putExtra(EXTRA_ID, id)
    }
}

/** Las fotos del anuncio, una por pantalla, deslizando de lado. */
class AdaptadorFotos(
    private val urls: List<String>,
    private val alTocar: (String) -> Unit
) : RecyclerView.Adapter<AdaptadorFotos.Foto>() {

    class Foto(val iv: ImageView) : RecyclerView.ViewHolder(iv)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Foto {
        val iv = ImageView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(parent.width.takeIf { it > 0 } ?: ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return Foto(iv)
    }

    override fun getItemCount() = urls.size

    override fun onBindViewHolder(h: Foto, position: Int) {
        val url = urls[position]
        Glide.with(h.iv).load(url).centerCrop().into(h.iv)
        h.iv.setOnClickListener { alTocar(url) }
    }
}
