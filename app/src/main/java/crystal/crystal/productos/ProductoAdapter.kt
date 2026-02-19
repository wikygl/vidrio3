package crystal.crystal.productos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.R
import crystal.crystal.databinding.ItemProductoBinding

/**
 * Adapter para lista de productos en RecyclerView
 * ⭐ ACTUALIZADO: Usa rol_dispositivo de SharedPreferences
 */
class ProductoAdapter(
    private val onProductoClick: (Producto) -> Unit,
    private val onEditClick: (Producto) -> Unit,
    private val onDeleteClick: (Producto) -> Unit,
    private val onStockClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    private var productos = listOf<Producto>()
    private var esTerminal = false

    fun actualizarLista(nuevaLista: List<Producto>) {
        productos = nuevaLista
        notifyDataSetChanged()
    }

    fun limpiarFiltro() {
        notifyDataSetChanged()
    }

    // ⭐ CORREGIDO: Lee rol_dispositivo
    fun configurarTipoUsuario(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val rolDispositivo = sharedPreferences.getString("rol_dispositivo", "PATRON")
        esTerminal = (rolDispositivo == "TERMINAL")

        android.util.Log.d("ProductoAdapter", "📱 Rol: $rolDispositivo, Terminal: $esTerminal")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(productos[position])
    }

    override fun getItemCount() = productos.size

    inner class ProductoViewHolder(
        private val binding: ItemProductoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Producto) {
            binding.apply {
                tvNombre.text = producto.nombre
                tvCategoria.text = producto.categoria.uppercase()
                tvPrecioVenta.text = "Venta: S/ ${String.format("%.2f", producto.precioVenta)}"

                if (producto.precioCompra > 0) {
                    val margen = producto.calcularMargen()
                    tvMargen.text = "Margen: ${String.format("%.1f", margen)}%"
                    tvMargen.visibility = View.VISIBLE

                    when {
                        margen < 10 -> tvMargen.setTextColor(
                            ContextCompat.getColor(itemView.context, R.color.rojo)
                        )
                        margen < 30 -> tvMargen.setTextColor(
                            ContextCompat.getColor(itemView.context, android.R.color.holo_orange_dark)
                        )
                        else -> tvMargen.setTextColor(
                            ContextCompat.getColor(itemView.context, R.color.verde)
                        )
                    }
                } else {
                    tvMargen.visibility = View.GONE
                }

                tvStock.text = "Stock: ${producto.stock}"

                when {
                    producto.stock <= 0 -> {
                        tvStock.setTextColor(
                            ContextCompat.getColor(itemView.context, R.color.rojo)
                        )
                        indicadorStock.setBackgroundColor(
                            ContextCompat.getColor(itemView.context, R.color.rojo)
                        )
                        tvEstadoStock.text = "SIN STOCK"
                        tvEstadoStock.visibility = View.VISIBLE
                    }
                    producto.necesitaReabastecimiento() -> {
                        tvStock.setTextColor(
                            ContextCompat.getColor(itemView.context, android.R.color.holo_orange_dark)
                        )
                        indicadorStock.setBackgroundColor(
                            ContextCompat.getColor(itemView.context, android.R.color.holo_orange_dark)
                        )
                        tvEstadoStock.text = "STOCK BAJO"
                        tvEstadoStock.visibility = View.VISIBLE
                    }
                    else -> {
                        tvStock.setTextColor(
                            ContextCompat.getColor(itemView.context, R.color.verde)
                        )
                        indicadorStock.setBackgroundColor(
                            ContextCompat.getColor(itemView.context, R.color.verde)
                        )
                        tvEstadoStock.visibility = View.GONE
                    }
                }

                if (!producto.espesor.isNullOrEmpty() || !producto.tipo.isNullOrEmpty()) {
                    val detalles = mutableListOf<String>()
                    producto.espesor?.let { detalles.add(it) }
                    producto.tipo?.let { detalles.add(it) }
                    tvDetalles.text = detalles.joinToString(" • ")
                    tvDetalles.visibility = View.VISIBLE
                } else {
                    tvDetalles.visibility = View.GONE
                }

                // ⭐ RESTRICCIONES PARA TERMINAL
                if (esTerminal) {
                    root.setOnClickListener { onProductoClick(producto) }
                    btnEditar.visibility = View.GONE
                    btnEliminar.visibility = View.GONE
                    layoutStock.isClickable = false
                    layoutStock.isFocusable = false
                } else {
                    root.setOnClickListener { onProductoClick(producto) }
                    btnEditar.visibility = View.VISIBLE
                    btnEditar.setOnClickListener { onEditClick(producto) }
                    btnEliminar.visibility = View.VISIBLE
                    btnEliminar.setOnClickListener { onDeleteClick(producto) }
                    layoutStock.isClickable = true
                    layoutStock.isFocusable = true
                    layoutStock.setOnClickListener { onStockClick(producto) }
                }
            }
        }
    }
}