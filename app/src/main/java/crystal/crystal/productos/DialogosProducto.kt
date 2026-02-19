package crystal.crystal.productos

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import crystal.crystal.databinding.DialogoAjustarStockBinding
import crystal.crystal.databinding.DialogoDetallesProductoBinding

/**
 * Diálogo para ajustar stock (agregar o quitar)
 * ⭐ ACTUALIZADO: Usa Parcelable para pasar producto correctamente
 */
class DialogoAjustarStock : DialogFragment() {

    private var _binding: DialogoAjustarStockBinding? = null
    private val binding get() = _binding!!

    private var producto: Producto? = null
    private var onAjustar: ((Int) -> Unit)? = null

    companion object {
        private const val ARG_PRODUCTO = "producto"

        fun newInstance(
            producto: Producto,
            onAjustar: (Int) -> Unit
        ): DialogoAjustarStock {
            return DialogoAjustarStock().apply {
                arguments = Bundle().apply {
                    // ⭐ USAR PARCELABLE en lugar de Serializable
                    putParcelable(ARG_PRODUCTO, producto)
                }
                this.onAjustar = onAjustar
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ⭐ OBTENER CON PARCELABLE
        producto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_PRODUCTO, Producto::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_PRODUCTO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogoAjustarStockBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        producto?.let { p ->
            binding.tvProducto.text = p.nombre
            binding.tvStockActual.text = "Stock actual: ${p.stock}"
        } ?: run {
            // Si producto es null, cerrar diálogo
            Toast.makeText(requireContext(), "Error cargando producto", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        // Botones
        binding.btnAgregar.setOnClickListener {
            ajustarStock(positivo = true)
        }

        binding.btnQuitar.setOnClickListener {
            ajustarStock(positivo = false)
        }

        binding.btnCancelar.setOnClickListener {
            dismiss()
        }
    }

    // ⭐ Ajustar tamaño del diálogo
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun ajustarStock(positivo: Boolean) {
        val cantidadTexto = binding.etCantidad.text.toString().trim()

        if (cantidadTexto.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa la cantidad", Toast.LENGTH_SHORT).show()
            return
        }

        val cantidad = cantidadTexto.toIntOrNull()
        if (cantidad == null || cantidad <= 0) {
            Toast.makeText(requireContext(), "Cantidad inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val ajuste = if (positivo) cantidad else -cantidad
        onAjustar?.invoke(ajuste)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Diálogo para mostrar detalles completos del producto
 * ⭐ ACTUALIZADO: Muestra precioCompra, precioVenta y margen
 */
class DialogoDetallesProducto : DialogFragment() {

    private var _binding: DialogoDetallesProductoBinding? = null
    private val binding get() = _binding!!

    private var producto: Producto? = null

    companion object {
        private const val ARG_PRODUCTO = "producto"

        fun newInstance(producto: Producto): DialogoDetallesProducto {
            return DialogoDetallesProducto().apply {
                arguments = Bundle().apply {
                    // ⭐ USAR PARCELABLE
                    putParcelable(ARG_PRODUCTO, producto)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ⭐ OBTENER CON PARCELABLE
        producto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_PRODUCTO, Producto::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_PRODUCTO)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogoDetallesProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        producto?.let { mostrarDetalles(it) }

        binding.btnCerrar.setOnClickListener {
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun mostrarDetalles(producto: Producto) {
        binding.apply {
            tvNombre.text = producto.nombre
            tvCategoria.text = producto.categoria.uppercase()

            // ⭐ ACTUALIZADO: Mostrar ambos precios
            tvPrecioCompra.text = "Compra: S/ ${String.format("%.2f", producto.precioCompra)}"
            tvPrecioVenta.text = "Venta: S/ ${String.format("%.2f", producto.precioVenta)}"

            // ⭐ NUEVO: Mostrar margen de ganancia
            val margen = producto.calcularMargen()
            if (margen > 0) {
                tvMargen.text = "Margen: ${String.format("%.1f", margen)}%"
                tvMargen.visibility = View.VISIBLE
            } else {
                tvMargen.visibility = View.GONE
            }

            tvStock.text = producto.stock.toString()
            tvStockMinimo.text = producto.stockMinimo.toString()
            tvUnidad.text = producto.unidad

            if (!producto.descripcion.isNullOrEmpty()) {
                tvDescripcion.text = producto.descripcion
                tvDescripcion.visibility = View.VISIBLE
            } else {
                tvDescripcion.visibility = View.GONE
            }

            if (!producto.espesor.isNullOrEmpty()) {
                tvEspesor.text = producto.espesor
                layoutEspesor.visibility = View.VISIBLE
            } else {
                layoutEspesor.visibility = View.GONE
            }

            if (!producto.tipo.isNullOrEmpty()) {
                tvTipo.text = producto.tipo
                layoutTipo.visibility = View.VISIBLE
            } else {
                layoutTipo.visibility = View.GONE
            }

            // Estado del stock
            when {
                producto.stock <= 0 -> {
                    tvEstadoStock.text = "⚠️ SIN STOCK"
                    tvEstadoStock.setTextColor(
                        resources.getColor(crystal.crystal.R.color.rojo, null)
                    )
                }
                producto.necesitaReabastecimiento() -> {
                    tvEstadoStock.text = "⚠️ STOCK BAJO"
                    tvEstadoStock.setTextColor(
                        resources.getColor(android.R.color.holo_orange_dark, null)
                    )
                }
                else -> {
                    tvEstadoStock.text = "✅ STOCK OK"
                    tvEstadoStock.setTextColor(
                        resources.getColor(crystal.crystal.R.color.verde, null)
                    )
                }
            }

            // ⭐ ACTUALIZADO: Valor total en inventario (precio de venta)
            val valorTotal = producto.stock * producto.precioVenta
            tvValorTotal.text = "Valor inventario: S/ ${String.format("%.2f", valorTotal)}"

            // ⭐ NUEVO: Ganancia potencial
            val gananciaPotencial = producto.calcularGanancia() * producto.stock
            if (gananciaPotencial > 0) {
                tvGananciaPotencial.text = "Ganancia potencial: S/ ${String.format("%.2f", gananciaPotencial)}"
                tvGananciaPotencial.visibility = View.VISIBLE
            } else {
                tvGananciaPotencial.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // ⭐ Ajustar tamaño del diálogo
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}