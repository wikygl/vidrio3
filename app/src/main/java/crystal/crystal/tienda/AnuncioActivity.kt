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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import crystal.crystal.databinding.ActivityAnuncioBinding
import crystal.crystal.pagos.CanalPagos
import crystal.crystal.red.ChatActivity
import crystal.crystal.red.ChatIdentity
import crystal.crystal.red.data.ChatConversationRepository
import crystal.crystal.red.data.ChatDirectoryRepository
import crystal.crystal.red.data.ChatListRepository
import crystal.crystal.red.interop.ChatInteropIntents
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
        b.tvLugar.text = listOf(a.ciudad, TiendaReglas.hace(a.publicadoEnMs, ahora))
            .filter { it.isNotBlank() }.joinToString("  ·  ")
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
            else -> "$estado · ${TiendaReglas.vence(a.venceEnMs, ahora)} · ${a.vistas} vistas"
        }
        val editable = !vencido && !enRevision
        b.btnEditar.visibility = if (editable) View.VISIBLE else View.GONE
        b.btnPausar.visibility = if (editable) View.VISIBLE else View.GONE
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
        val auth = FirebaseAuth.getInstance()
        val yo = ChatIdentity.resolveChatIdentityUid(this, auth)
        if (yo.isBlank()) return
        if (yo == a.uid) {
            Toast.makeText(this, "Es su propio anuncio", Toast.LENGTH_SHORT).show()
            return
        }
        b.btnContactar.isEnabled = false
        lifecycleScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val vendedor = ChatDirectoryRepository(db).getUser(a.uid)
                if (vendedor == null) {
                    Toast.makeText(this@AnuncioActivity, "El vendedor ya no está disponible", Toast.LENGTH_LONG).show()
                    return@launch
                }
                val chatId = ChatConversationRepository(db, ChatListRepository(db)).getOrCreateDirectChat(
                    queryUserId = yo,
                    currentUserId = yo,
                    aliases = ChatIdentity.resolveChatIdentityAliases(this@AnuncioActivity, auth, yo),
                    otherUser = vendedor
                )
                val precio = TiendaReglas.precio(a.precioCent)
                startActivity(
                    Intent(this@AnuncioActivity, ChatActivity::class.java)
                        .putExtra("chatId", chatId)
                        .putExtra("usuario", yo)
                        .putExtra(
                            ChatInteropIntents.EXTRA_PREFILL_TEXT,
                            "Hola, me interesa su anuncio de la Tienda: «${a.titulo}» ($precio). ¿Sigue disponible?"
                        )
                )
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
            val (precio, dias, saldo) = runCatching { repo.precioYSaldo() }.getOrElse {
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
