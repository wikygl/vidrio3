package crystal.crystal.productos

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import crystal.crystal.R
import crystal.crystal.databinding.DialogoNuevoProductoBinding

/**
 * Diálogo para crear o editar producto
 * VERSIÓN PANTALLA COMPLETA
 * ⭐ ACTUALIZADO: Usa Parcelable + incluye precioCompra y precioVenta
 */
@Suppress("DEPRECATION")
class DialogoNuevoProducto : DialogFragment() {

    private var _binding: DialogoNuevoProductoBinding? = null
    private val binding get() = _binding!!

    private var producto: Producto? = null
    private var onGuardar: ((Producto) -> Unit)? = null

    companion object {
        private const val ARG_PRODUCTO = "producto"

        fun newInstance(
            producto: Producto?,
            onGuardar: (Producto) -> Unit
        ): DialogoNuevoProducto {
            return DialogoNuevoProducto().apply {
                arguments = Bundle().apply {
                    // ⭐ USAR PARCELABLE en lugar de Serializable
                    producto?.let { putParcelable(ARG_PRODUCTO, it) }
                }
                this.onGuardar = onGuardar
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
        // ⭐ PANTALLA COMPLETA
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogoNuevoProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarToolbar()
        configurarSpinners()

        // Si es edición, prellenar datos
        producto?.let { prellenarDatos(it) }

        // Botones
        binding.btnGuardar.setOnClickListener {
            guardar()
        }

        binding.btnCancelar.setOnClickListener {
            dismiss()
        }
    }

    private fun configurarToolbar() {
        binding.toolbar.apply {
            title = if (producto == null) "Nuevo Producto" else "Editar Producto"
            setNavigationIcon(R.drawable.ic_close)
            setNavigationOnClickListener {
                dismiss()
            }
        }
    }

    private fun configurarSpinners() {
        // Spinner categoría
        val categorias = CategoriaProducto.values().map { it.nombre }
        val adapterCategorias = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categorias
        )
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = adapterCategorias

        // Spinner unidad
        val unidades = listOf("m2", "ml", "uni", "p2")
        val adapterUnidades = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            unidades
        )
        adapterUnidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUnidad.adapter = adapterUnidades
    }

    @SuppressLint("SetTextI18n")
    private fun prellenarDatos(producto: Producto) {
        binding.apply {
            etNombre.setText(producto.nombre)
            etDescripcion.setText(producto.descripcion ?: "")

            // ⭐ ACTUALIZADO: Separar precioCompra y precioVenta
            etPrecioCompra.setText(producto.precioCompra.toString())
            etPrecioVenta.setText(producto.precioVenta.toString())

            etStock.setText(producto.stock.toString())
            etStockMinimo.setText(producto.stockMinimo.toString())
            etEspesor.setText(producto.espesor ?: "")
            etTipo.setText(producto.tipo ?: "")

            // Seleccionar categoría
            val categoriaIndex = CategoriaProducto.values().indexOfFirst {
                it.nombre.equals(producto.categoria, ignoreCase = true)
            }
            if (categoriaIndex >= 0) {
                spinnerCategoria.setSelection(categoriaIndex)
            }

            // Seleccionar unidad
            val unidadIndex = (spinnerUnidad.adapter as ArrayAdapter<String>)
                .getPosition(producto.unidad)
            if (unidadIndex >= 0) {
                spinnerUnidad.setSelection(unidadIndex)
            }
        }
    }

    private fun guardar() {
        // Validar nombre
        val nombre = binding.etNombre.text.toString().trim()
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el nombre", Toast.LENGTH_SHORT).show()
            binding.etNombre.requestFocus()
            return
        }

        // ⭐ VALIDAR PRECIO DE COMPRA
        val precioCompraTexto = binding.etPrecioCompra.text.toString().trim()
        val precioCompra = if (precioCompraTexto.isNotEmpty()) {
            precioCompraTexto.toFloatOrNull()
        } else {
            0f  // Opcional
        }

        if (precioCompra == null || precioCompra < 0) {
            Toast.makeText(requireContext(), "Precio de compra inválido", Toast.LENGTH_SHORT).show()
            binding.etPrecioCompra.requestFocus()
            return
        }

        // ⭐ VALIDAR PRECIO DE VENTA (obligatorio)
        val precioVentaTexto = binding.etPrecioVenta.text.toString().trim()
        if (precioVentaTexto.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa el precio de venta", Toast.LENGTH_SHORT).show()
            binding.etPrecioVenta.requestFocus()
            return
        }

        val precioVenta = precioVentaTexto.toFloatOrNull()
        if (precioVenta == null || precioVenta <= 0) {
            Toast.makeText(requireContext(), "Precio de venta inválido", Toast.LENGTH_SHORT).show()
            binding.etPrecioVenta.requestFocus()
            return
        }

        // Validar que precio venta > precio compra (advertencia, no bloqueo)
        if (precioCompra > 0 && precioVenta < precioCompra) {
            Toast.makeText(
                requireContext(),
                "⚠️ Precio de venta menor que precio de compra",
                Toast.LENGTH_LONG
            ).show()
        }

        val stock = binding.etStock.text.toString().toIntOrNull() ?: 0
        val stockMinimo = binding.etStockMinimo.text.toString().toIntOrNull() ?: 10

        // ⭐ CREAR PRODUCTO CON AMBOS PRECIOS
        val productoNuevo = Producto(
            id = producto?.id ?: java.util.UUID.randomUUID().toString(),
            nombre = nombre,
            categoria = CategoriaProducto.values()[binding.spinnerCategoria.selectedItemPosition]
                .nombre.lowercase(),
            descripcion = binding.etDescripcion.text.toString().trim()
                .takeIf { it.isNotEmpty() },
            precioCompra = precioCompra,  // ⭐ NUEVO
            precioVenta = precioVenta,    // ⭐ RENOMBRADO
            stock = stock,
            stockMinimo = stockMinimo,
            unidad = binding.spinnerUnidad.selectedItem.toString(),
            espesor = binding.etEspesor.text.toString().trim()
                .takeIf { it.isNotEmpty() },
            tipo = binding.etTipo.text.toString().trim()
                .takeIf { it.isNotEmpty() }
        )

        onGuardar?.invoke(productoNuevo)
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        // ⭐ Asegurar pantalla completa
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}