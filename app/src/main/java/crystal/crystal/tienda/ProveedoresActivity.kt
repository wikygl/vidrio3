package crystal.crystal.tienda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import crystal.crystal.R
import crystal.crystal.databinding.ActivityTiendaListaBinding
import crystal.crystal.databinding.ItemProveedorBinding
import kotlinx.coroutines.launch

/**
 * La vitrina de proveedores (docs/TIENDA_ANUNCIOS_PLAN.md, fase 2): los que pagan su vitrina y
 * atienden la ciudad del taller; si no hay ninguno, los de todo el país. Cada uno con "Escribir"
 * (el chat de Crystal) y "Ver sus anuncios". Abajo, el acceso a la vitrina propia.
 */
class ProveedoresActivity : AppCompatActivity() {

    private lateinit var b: ActivityTiendaListaBinding
    private val repo = TiendaRepositorio()
    private val adaptador = AdaptadorProveedores(
        alEscribir = { p -> escribir(p) },
        alVerAnuncios = { p -> startActivity(TiendaActivity.intentVendedor(this, p.uid, p.nombre)) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTiendaListaBinding.inflate(layoutInflater)
        setContentView(b.root)
        if (repo.uid == null) {
            finish()
            return
        }
        b.tvTitulo.text = "Proveedores"
        b.btnAtras.setOnClickListener { finish() }
        b.rvLista.layoutManager = LinearLayoutManager(this)
        b.rvLista.adapter = adaptador
        b.swipe.setOnRefreshListener { cargar() }
        b.tvContexto.setOnClickListener {
            MiCiudad.pedir(this, "Le mostramos primero los proveedores que atienden su ciudad.") { cargar() }
        }
        b.btnAccion.visibility = View.VISIBLE
        b.btnAccion.text = "¿Vende materiales? Su vitrina aquí"
        b.btnAccion.setOnClickListener { startActivity(MiVitrinaActivity.intent(this)) }
        cargar()
    }

    private fun cargar() {
        val ciudad = MiCiudad.get(this)
        b.pbCargando.visibility = if (adaptador.itemCount == 0) View.VISIBLE else View.GONE
        lifecycleScope.launch {
            try {
                val (lista, locales) = repo.proveedoresPara(ciudad)
                adaptador.poner(lista.sortedBy { it.nombre.lowercase() })
                b.tvContexto.text = when {
                    ciudad.isBlank() -> "Todo el país · elegir mi ciudad"
                    locales -> "En $ciudad · cambiar"
                    else -> "Todavía no hay proveedores en $ciudad; estos son de otras ciudades · cambiar"
                }
                b.tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                b.tvVacio.text = "Todavía no hay proveedores en la vitrina.\n¿Vende vidrio, aluminio o accesorios? Sea el primero."
            } catch (e: Exception) {
                android.util.Log.e("Proveedores", "No se pudo cargar", e)
                Toast.makeText(this@ProveedoresActivity, "No se pudo cargar: ${TiendaRepositorio.mensaje(e)}", Toast.LENGTH_LONG).show()
            } finally {
                b.pbCargando.visibility = View.GONE
                b.swipe.isRefreshing = false
            }
        }
    }

    private fun escribir(p: Proveedor) {
        lifecycleScope.launch {
            runCatching { ChatDeTienda.abrir(this@ProveedoresActivity, p.uid, "Hola, lo vi en la vitrina de proveedores de Crystal. ") }
                .onFailure { Toast.makeText(this@ProveedoresActivity, "No se pudo abrir el chat: ${TiendaRepositorio.mensaje(it)}", Toast.LENGTH_LONG).show() }
        }
    }

    companion object {
        fun intent(ctx: Context) = Intent(ctx, ProveedoresActivity::class.java)
    }
}

/** Las tarjetas de la vitrina. */
class AdaptadorProveedores(
    private val alEscribir: (Proveedor) -> Unit,
    private val alVerAnuncios: (Proveedor) -> Unit
) : RecyclerView.Adapter<AdaptadorProveedores.Tarjeta>() {

    private val lista = mutableListOf<Proveedor>()

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    fun poner(nueva: List<Proveedor>) {
        lista.clear()
        lista.addAll(nueva)
        notifyDataSetChanged()
    }

    class Tarjeta(val b: ItemProveedorBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Tarjeta(ItemProveedorBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(h: Tarjeta, position: Int) {
        val p = lista[position]
        h.b.tvNombre.text = p.nombre
        h.b.tvRubros.text = p.rubros.joinToString(" · ")
        h.b.tvLugar.text = p.lugar
        h.b.tvDescripcion.text = p.descripcion
        h.b.tvDescripcion.visibility = if (p.descripcion.isBlank()) View.GONE else View.VISIBLE
        if (p.logo.isNotBlank()) Glide.with(h.b.ivLogo).load(p.logo).centerCrop().into(h.b.ivLogo)
        else {
            Glide.with(h.b.ivLogo).clear(h.b.ivLogo)
            h.b.ivLogo.setImageResource(R.drawable.ic_tienda_vacio)
        }
        h.b.btnEscribir.setOnClickListener { alEscribir(p) }
        h.b.btnAnuncios.setOnClickListener { alVerAnuncios(p) }
        // La descripción completa al tocar la tarjeta.
        h.b.root.setOnClickListener {
            h.b.tvDescripcion.maxLines = if (h.b.tvDescripcion.maxLines == 3) Int.MAX_VALUE else 3
        }
    }
}
