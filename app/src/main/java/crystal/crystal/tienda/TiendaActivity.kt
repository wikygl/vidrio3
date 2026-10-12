package crystal.crystal.tienda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import crystal.crystal.R
import crystal.crystal.databinding.ActivityTiendaBinding
import crystal.crystal.databinding.ItemAnuncioBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * La Tienda de anuncios del rubro (docs/TIENDA_ANUNCIOS_PLAN.md): lo más nuevo primero, con buscador
 * y filtros por tipo y por categoría. Con [EXTRA_MIS], la misma pantalla muestra "Mis anuncios" en
 * cualquier estado.
 */
class TiendaActivity : AppCompatActivity() {

    private lateinit var b: ActivityTiendaBinding
    private val repo = TiendaRepositorio()
    private val adaptador = AdaptadorAnuncios { abrir(it) }

    private var modoMis = false
    private var filtroTipo: String? = null
    private var filtroCategoria: String? = null
    private var texto = ""

    private var ultimo: DocumentSnapshot? = null
    private var hayMas = true
    private var cargando = false
    private var consulta: Job? = null
    private var busquedaDiferida: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTiendaBinding.inflate(layoutInflater)
        setContentView(b.root)

        if (repo.uid == null) {
            Toast.makeText(this, "Inicie sesión para entrar a la Tienda", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        modoMis = intent.getBooleanExtra(EXTRA_MIS, false)
        adaptador.modoMis = modoMis

        b.btnAtras.setOnClickListener { finish() }
        if (modoMis) {
            b.tvTituloTienda.text = "Mis anuncios"
            b.tvMisAnuncios.visibility = View.GONE
            b.etBuscar.visibility = View.GONE
            b.scrollFiltros.visibility = View.GONE
        } else {
            b.tvMisAnuncios.setOnClickListener { startActivity(intentMis(this)) }
            construirFiltros()
            configurarBuscador()
        }

        b.rvAnuncios.layoutManager = GridLayoutManager(this, 2)
        b.rvAnuncios.adapter = adaptador
        b.rvAnuncios.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lm = rv.layoutManager as GridLayoutManager
                if (!modoMis && hayMas && !cargando && lm.findLastVisibleItemPosition() >= adaptador.itemCount - 6) {
                    cargar(siguiente = true)
                }
            }
        })
        b.swipe.setOnRefreshListener { cargar(siguiente = false) }
        b.btnPublicar.setOnClickListener {
            // Sin pagos dentro de la app (build de Play) se avisa antes de llenar el formulario.
            if (!crystal.crystal.pagos.CanalPagos.DISPONIBLE_EN_LA_APP) PublicarAnuncioActivity.avisarNoDisponible(this)
            else startActivity(PublicarAnuncioActivity.intentNuevo(this))
        }
    }

    // Al volver de un anuncio o de publicar, la lista se pone al día (otro precio, otro estado).
    override fun onResume() {
        super.onResume()
        if (repo.uid != null) cargar(siguiente = false)
    }

    private fun configurarBuscador() {
        b.etBuscar.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                texto = v.text.toString().trim()
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(v.windowToken, 0)
                cargar(siguiente = false)
                true
            } else false
        }
        // También busca solo al dejar de escribir: no hace falta pulsar la lupa del teclado.
        b.etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, c: Int, d: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, c: Int, d: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val nuevo = s?.toString()?.trim().orEmpty()
                if (nuevo == texto) return
                busquedaDiferida?.cancel()
                busquedaDiferida = lifecycleScope.launch {
                    delay(600)
                    texto = nuevo
                    cargar(siguiente = false)
                }
            }
        })
    }

    /** Pastillas de filtro: Todo, Productos, Servicios y cada categoría. Se elige una. */
    private fun construirFiltros() {
        val opciones = listOf<Pair<String, () -> Unit>>(
            "Todo" to { filtroTipo = null; filtroCategoria = null },
            "Productos" to { filtroTipo = TiendaReglas.PRODUCTO; filtroCategoria = null },
            "Servicios" to { filtroTipo = TiendaReglas.SERVICIO; filtroCategoria = null }
        ) + TiendaReglas.CATEGORIAS.map { cat -> cat to { filtroTipo = null; filtroCategoria = cat } }

        val chips = mutableListOf<TextView>()
        opciones.forEachIndexed { i, (rotulo, aplicar) ->
            val chip = TextView(this).apply {
                text = rotulo
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(dp(14), dp(7), dp(14), dp(7))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(dp(4), 0, dp(4), dp(4)) }
            }
            chip.setOnClickListener {
                aplicar()
                chips.forEach { marcar(it, it === chip) }
                cargar(siguiente = false)
            }
            marcar(chip, i == 0)
            chips += chip
            b.contenedorFiltros.addView(chip)
        }
    }

    private fun marcar(chip: TextView, elegido: Boolean) {
        chip.background = ContextCompat.getDrawable(
            this, if (elegido) R.drawable.bg_chip_filtro_elegido else R.drawable.bg_chip_filtro
        )
        chip.setTextColor(ContextCompat.getColor(this, if (elegido) R.color.blanco else R.color.azul))
    }

    /** Carga la primera página (o la siguiente, si [siguiente]). La consulta anterior se cancela. */
    private fun cargar(siguiente: Boolean) {
        if (siguiente && (cargando || !hayMas)) return
        consulta?.cancel()
        cargando = true
        if (!siguiente && adaptador.itemCount == 0) b.pbCargando.visibility = View.VISIBLE
        consulta = lifecycleScope.launch {
            try {
                if (modoMis) {
                    adaptador.poner(repo.misAnuncios())
                    hayMas = false
                } else {
                    val pagina = repo.buscar(filtroTipo, filtroCategoria, texto, if (siguiente) ultimo else null)
                    ultimo = pagina.ultimo
                    hayMas = pagina.hayMas
                    if (siguiente) adaptador.agregar(pagina.anuncios) else adaptador.poner(pagina.anuncios)
                    // Una página entera filtrada aquí puede quedar vacía aunque haya más: se pide la siguiente.
                    if (pagina.anuncios.isEmpty() && pagina.hayMas) {
                        cargando = false
                        cargar(siguiente = true)
                        return@launch
                    }
                }
                mostrarVacio()
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e("TiendaActivity", "No se pudo cargar la tienda", e)
                Toast.makeText(this@TiendaActivity, "No se pudo cargar: ${TiendaRepositorio.mensaje(e)}", Toast.LENGTH_LONG).show()
            } finally {
                cargando = false
                b.pbCargando.visibility = View.GONE
                b.swipe.isRefreshing = false
            }
        }
    }

    private fun mostrarVacio() {
        val vacio = adaptador.itemCount == 0
        b.tvVacio.visibility = if (vacio) View.VISIBLE else View.GONE
        b.tvVacio.text = when {
            modoMis -> "Todavía no publicó ningún anuncio.\nToque «Publicar anuncio» para empezar."
            texto.isNotBlank() -> "No hay anuncios con «$texto»."
            filtroCategoria != null || filtroTipo != null -> "No hay anuncios en este filtro todavía."
            else -> "Todavía no hay anuncios.\n¡Publique el primero!"
        }
    }

    private fun abrir(a: Anuncio) {
        startActivity(AnuncioActivity.intent(this, a.id))
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    companion object {
        const val EXTRA_MIS = "tienda_mis_anuncios"

        fun intent(ctx: Context) = Intent(ctx, TiendaActivity::class.java)
        fun intentMis(ctx: Context) = Intent(ctx, TiendaActivity::class.java).putExtra(EXTRA_MIS, true)
    }
}

/** Las tarjetas de la lista: miniatura, precio, título, ciudad y una marca. */
class AdaptadorAnuncios(private val alTocar: (Anuncio) -> Unit) :
    RecyclerView.Adapter<AdaptadorAnuncios.Tarjeta>() {

    var modoMis = false
    private val anuncios = mutableListOf<Anuncio>()

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    fun poner(lista: List<Anuncio>) {
        anuncios.clear()
        anuncios.addAll(lista)
        notifyDataSetChanged()
    }

    fun agregar(lista: List<Anuncio>) {
        val desde = anuncios.size
        val nuevos = lista.filter { n -> anuncios.none { it.id == n.id } }
        anuncios.addAll(nuevos)
        notifyItemRangeInserted(desde, nuevos.size)
    }

    class Tarjeta(val b: ItemAnuncioBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Tarjeta {
        val b = ItemAnuncioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Las esquinas redondeadas de la tarjeta recortan también la foto.
        b.root.outlineProvider = ViewOutlineProvider.BACKGROUND
        b.root.clipToOutline = true
        return Tarjeta(b)
    }

    override fun getItemCount() = anuncios.size

    override fun onBindViewHolder(h: Tarjeta, position: Int) {
        val a = anuncios[position]
        val ahora = System.currentTimeMillis()
        h.b.tvPrecio.text = TiendaReglas.precio(a.precioCent) +
            if (a.precioCent > 0 && a.unidad.isNotBlank() && a.unidad != "c/u") " ${a.unidad}" else ""
        h.b.tvTitulo.text = a.titulo
        h.b.tvLugar.text = if (modoMis) TiendaReglas.vence(a.venceEnMs, ahora) + " · ${a.vistas} vistas"
        else listOf(a.ciudad, TiendaReglas.hace(a.publicadoEnMs, ahora)).filter { it.isNotBlank() }.joinToString(" · ")

        val marca = when {
            modoMis -> TiendaReglas.nombreEstado(if (a.estado == Anuncio.ESTADO_ACTIVO && a.venceEnMs <= ahora) Anuncio.ESTADO_VENCIDO else a.estado)
            a.esServicio -> "Servicio"
            a.condicion == TiendaReglas.USADO -> "Usado"
            else -> ""
        }
        h.b.tvMarca.text = marca
        h.b.tvMarca.visibility = if (marca.isEmpty()) View.GONE else View.VISIBLE

        val foto = a.miniaturas.firstOrNull() ?: a.fotos.firstOrNull()
        if (foto != null) {
            h.b.ivFoto.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            Glide.with(h.b.ivFoto).load(foto).centerCrop().into(h.b.ivFoto)
        } else {
            Glide.with(h.b.ivFoto).clear(h.b.ivFoto)
            h.b.ivFoto.scaleType = android.widget.ImageView.ScaleType.CENTER
            h.b.ivFoto.setImageResource(R.drawable.ic_tienda_vacio)
        }
        h.b.root.setOnClickListener { alTocar(a) }
    }
}
