package crystal.crystal.clientes

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import crystal.crystal.R

/**
 * Diálogo para crear/editar clientes
 * Con validación de RUC/DNI
 */
class DialogoNuevoCliente : DialogFragment() {

    companion object {
        private const val ARG_CLIENTE = "cliente"

        fun newInstance(
            cliente: Cliente? = null,
            onGuardar: (Cliente) -> Unit
        ): DialogoNuevoCliente {
            val dialog = DialogoNuevoCliente()
            dialog.onGuardarCallback = onGuardar

            if (cliente != null) {
                val bundle = Bundle()
                bundle.putString("clienteId", cliente.id)
                bundle.putString("tipoDoc", cliente.tipoDocumento.name)
                bundle.putString("numDoc", cliente.numeroDocumento)
                bundle.putString("nombre", cliente.nombreCompleto)
                bundle.putString("direccion", cliente.direccion)
                bundle.putString("telefono", cliente.telefono)
                bundle.putString("email", cliente.email)
                bundle.putFloat("descuento", cliente.descuentoPorcentaje ?: 0f)
                bundle.putString("notas", cliente.notas)
                dialog.arguments = bundle
            }

            return dialog
        }
    }

    private var onGuardarCallback: ((Cliente) -> Unit)? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nuevo_cliente, null)

        // Referencias a vistas
        val spinnerTipoDoc = view.findViewById<Spinner>(R.id.spinnerTipoDocumento)
        val etNumDoc = view.findViewById<EditText>(R.id.etNumeroDocumento)
        val etNombre = view.findViewById<EditText>(R.id.etNombreCompleto)
        val etDireccion = view.findViewById<EditText>(R.id.etDireccion)
        val etTelefono = view.findViewById<EditText>(R.id.etTelefono)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etDescuento = view.findViewById<EditText>(R.id.etDescuento)
        val etNotas = view.findViewById<EditText>(R.id.etNotas)
        val tvValidacion = view.findViewById<TextView>(R.id.tvValidacion)

        // Configurar spinner de tipo de documento
        val tiposDocumento = arrayOf("DNI", "RUC", "CE", "PASAPORTE")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tiposDocumento)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoDoc.adapter = adapter

        // Validación en tiempo real
        etNumDoc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(s: Editable?) {
                validarDocumento(spinnerTipoDoc.selectedItem.toString(), s.toString(), tvValidacion)
            }
        })

        spinnerTipoDoc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                validarDocumento(tiposDocumento[position], etNumDoc.text.toString(), tvValidacion)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Cargar datos si es edición
        arguments?.let { args ->
            val tipoDoc = args.getString("tipoDoc", "DNI")
            spinnerTipoDoc.setSelection(tiposDocumento.indexOf(tipoDoc))
            etNumDoc.setText(args.getString("numDoc", ""))
            etNombre.setText(args.getString("nombre", ""))
            etDireccion.setText(args.getString("direccion", ""))
            etTelefono.setText(args.getString("telefono", ""))
            etEmail.setText(args.getString("email", ""))

            val descuento = args.getFloat("descuento", 0f)
            if (descuento > 0) {
                etDescuento.setText(descuento.toInt().toString())
            }

            etNotas.setText(args.getString("notas", ""))
        }

        val titulo = if (arguments == null) "Nuevo Cliente" else "Editar Cliente"

        return AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                guardarCliente(
                    clienteId = arguments?.getString("clienteId"),
                    tipoDoc = spinnerTipoDoc.selectedItem.toString(),
                    numDoc = etNumDoc.text.toString().trim(),
                    nombre = etNombre.text.toString().trim(),
                    direccion = etDireccion.text.toString().trim(),
                    telefono = etTelefono.text.toString().trim(),
                    email = etEmail.text.toString().trim(),
                    descuento = etDescuento.text.toString().toFloatOrNull(),
                    notas = etNotas.text.toString().trim()
                )
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    /**
     * Valida documento según tipo
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun validarDocumento(tipo: String, numero: String, tvValidacion: TextView) {
        val valido = when (tipo) {
            "DNI" -> numero.length == 8 && numero.all { it.isDigit() }
            "RUC" -> numero.length == 11 && numero.all { it.isDigit() }
            "CE" -> numero.length <= 12 && numero.isNotEmpty()
            "PASAPORTE" -> numero.length <= 12 && numero.isNotEmpty()
            else -> false
        }

        if (numero.isEmpty()) {
            tvValidacion.text = ""
            tvValidacion.visibility = android.view.View.GONE
        } else if (valido) {
            tvValidacion.text = "✅ Documento válido"
            tvValidacion.setTextColor(requireContext().getColor(android.R.color.holo_green_dark))
            tvValidacion.visibility = android.view.View.VISIBLE
        } else {
            val mensaje = when (tipo) {
                "DNI" -> "❌ DNI debe tener 8 dígitos"
                "RUC" -> "❌ RUC debe tener 11 dígitos"
                else -> "❌ Documento inválido"
            }
            tvValidacion.text = mensaje
            tvValidacion.setTextColor(requireContext().getColor(android.R.color.holo_red_dark))
            tvValidacion.visibility = android.view.View.VISIBLE
        }
    }

    /**
     * Guarda el cliente
     */
    private fun guardarCliente(
        clienteId: String?,
        tipoDoc: String,
        numDoc: String,
        nombre: String,
        direccion: String,
        telefono: String,
        email: String,
        descuento: Float?,
        notas: String
    ) {
        // Validación obligatoria: solo el nombre
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa nombre completo", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar formato de documento SOLO si se ingresó
        if (numDoc.isNotEmpty()) {
            val tipoDocEnum = TipoDocumento.fromString(tipoDoc)
            val valido = when (tipoDocEnum) {
                TipoDocumento.DNI -> numDoc.length == 8 && numDoc.all { it.isDigit() }
                TipoDocumento.RUC -> numDoc.length == 11 && numDoc.all { it.isDigit() }
                else -> numDoc.isNotEmpty()
            }

            if (!valido) {
                Toast.makeText(requireContext(), "Documento inválido. Déjalo en blanco si no lo tienes", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val tipoDocEnum = TipoDocumento.fromString(tipoDoc)

        // Crear/actualizar cliente (usar "S/N" si no hay documento)
        val cliente = if (clienteId != null) {
            // Editar existente
            Cliente(
                id = clienteId,
                tipoDocumento = tipoDocEnum,
                numeroDocumento = numDoc.ifEmpty { "S/N" },
                nombreCompleto = nombre,
                direccion = direccion.ifEmpty { null },
                telefono = telefono.ifEmpty { null },
                email = email.ifEmpty { null },
                descuentoPorcentaje = descuento,
                notas = notas.ifEmpty { null }
            ).marcarPendiente()
        } else {
            // Crear nuevo
            Cliente(
                tipoDocumento = tipoDocEnum,
                numeroDocumento = numDoc.ifEmpty { "S/N" },
                nombreCompleto = nombre,
                direccion = direccion.ifEmpty { null },
                telefono = telefono.ifEmpty { null },
                email = email.ifEmpty { null },
                descuentoPorcentaje = descuento,
                notas = notas.ifEmpty { null },
                pendienteSincronizar = true
            )
        }

        onGuardarCallback?.invoke(cliente)
    }
}