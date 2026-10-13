package crystal.crystal.tienda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.R
import crystal.crystal.databinding.ActivityTiendaListaBinding
import crystal.crystal.databinding.ItemPedidoClienteBinding
import kotlinx.coroutines.launch

/**
 * Los pedidos que dejan los clientes finales en la web de la Tienda (fase 3): los de la ciudad del
 * taller y los que preguntan por sus anuncios. En "Tomados" quedan los que ya pagó, con el contacto.
 */
class PedidosClientesActivity : AppCompatActivity() {

    private lateinit var b: ActivityTiendaListaBinding
    private val repo = TiendaRepositorio()
    private val adaptador = AdaptadorPedidos { startActivity(PedidoClienteActivity.intent(this, it.id)) }
    private var verTomados = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTiendaListaBinding.inflate(layoutInflater)
        setContentView(b.root)
        if (repo.uid == null) {
            finish()
            return
        }
        verTomados = intent.getBooleanExtra(EXTRA_TOMADOS, false)
        b.tvTitulo.text = "Pedidos de clientes"
        b.btnAtras.setOnClickListener { finish() }
        b.rvLista.layoutManager = LinearLayoutManager(this)
        b.rvLista.adapter = adaptador
        b.swipe.setOnRefreshListener { cargar() }
        b.tvContexto.setOnClickListener {
            MiCiudad.pedir(this, "Le llegan los pedidos de clientes de su ciudad, con aviso en el teléfono.") { cargar() }
        }
        construirPastillas()
    }

    // Al volver de tomar uno, sale de "En mi ciudad" y aparece en "Tomados".
    override fun onResume() {
        super.onResume()
        if (repo.uid != null) {
            if (MiCiudad.get(this).isBlank() && !verTomados) {
                MiCiudad.pedir(this, "Le llegan los pedidos de clientes de su ciudad, con aviso en el teléfono.") { cargar() }
            }
            cargar()
        }
    }

    private fun construirPastillas() {
        b.contenedorPastillas.visibility = View.VISIBLE
        val pastillas = listOf("En mi ciudad" to false, "Tomados por mí" to true).map { (rotulo, tomados) ->
            TextView(this).apply {
                text = rotulo
                textSize = 14f
                gravity = Gravity.CENTER
                val p = (14 * resources.displayMetrics.density).toInt()
                setPadding(p, p / 2, p, p / 2)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    .apply { setMargins(p / 3, 0, p / 3, 0) }
                tag = tomados
            }
        }
        fun marcar() = pastillas.forEach { t ->
            val si = t.tag == verTomados
            t.background = ContextCompat.getDrawable(this, if (si) R.drawable.bg_chip_filtro_elegido else R.drawable.bg_chip_filtro)
            t.setTextColor(ContextCompat.getColor(this, if (si) R.color.blanco else R.color.azul))
        }
        pastillas.forEach { t ->
            t.setOnClickListener { verTomados = t.tag as Boolean; marcar(); adaptador.poner(emptyList()); cargar() }
            b.contenedorPastillas.addView(t)
        }
        marcar()
    }

    private fun cargar() {
        val ciudad = MiCiudad.get(this)
        b.tvContexto.visibility = if (verTomados) View.GONE else View.VISIBLE
        b.tvContexto.text = if (ciudad.isBlank()) "Elegir mi ciudad" else "En $ciudad · cambiar"
        b.pbCargando.visibility = if (adaptador.itemCount == 0) View.VISIBLE else View.GONE
        lifecycleScope.launch {
            try {
                val lista = if (verTomados) repo.pedidosTomados() else repo.pedidosPara(ciudad)
                adaptador.yo = repo.uid.orEmpty()
                adaptador.poner(lista)
                b.tvVacio.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                b.tvVacio.text = when {
                    verTomados -> "Todavía no tomó ningún pedido."
                    ciudad.isBlank() -> "Elija su ciudad para ver los pedidos de su zona."
                    else -> "No hay pedidos abiertos en $ciudad ahora.\nLe avisaremos cuando llegue uno."
                }
            } catch (e: Exception) {
                android.util.Log.e("PedidosClientes", "No se pudo cargar", e)
                Toast.makeText(this@PedidosClientesActivity, "No se pudo cargar: ${TiendaRepositorio.mensaje(e)}", Toast.LENGTH_LONG).show()
            } finally {
                b.pbCargando.visibility = View.GONE
                b.swipe.isRefreshing = false
            }
        }
    }

    companion object {
        const val EXTRA_TOMADOS = "pedidos_tomados"
        fun intent(ctx: Context) = Intent(ctx, PedidosClientesActivity::class.java)
    }
}

/** Las tarjetas de los pedidos. */
class AdaptadorPedidos(private val alTocar: (PedidoCliente) -> Unit) : RecyclerView.Adapter<AdaptadorPedidos.Tarjeta>() {

    var yo = ""
    private val lista = mutableListOf<PedidoCliente>()

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    fun poner(nueva: List<PedidoCliente>) {
        lista.clear()
        lista.addAll(nueva)
        notifyDataSetChanged()
    }

    class Tarjeta(val b: ItemPedidoClienteBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Tarjeta(ItemPedidoClienteBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(h: Tarjeta, position: Int) {
        val p = lista[position]
        val ahora = System.currentTimeMillis()
        h.b.tvQue.text = p.titulo
        h.b.tvDetalle.text = listOf(p.medidas, p.detalle).filter { it.isNotBlank() }.joinToString(" · ").ifBlank { "Sin detalle" }
        h.b.tvPie.text = listOf(
            p.nombreCorto, p.ciudad, TiendaReglas.hace(p.creadoEnMs, ahora),
            if (p.dirigido) "" else "${p.tomas} de ${p.maxTalleres} talleres"
        ).filter { it.isNotBlank() }.joinToString(" · ")
        val marca = when {
            yo in p.tomadoPor -> "Tomado"
            p.dirigido -> "Para usted"
            else -> ""
        }
        h.b.tvMarca.text = marca
        h.b.tvMarca.visibility = if (marca.isEmpty()) View.GONE else View.VISIBLE
        h.b.root.setOnClickListener { alTocar(p) }
    }
}
