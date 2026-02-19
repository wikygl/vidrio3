package crystal.crystal.comprobantes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import crystal.crystal.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Activity para configurar datos de empresa y diseño de tickets
 * VERSIÓN SIN PUSH NOTIFICATIONS - Solo sincronización manual
 */
class DisenoTicketActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    private var configuracion = ConfiguracionTicketDefecto.obtener()
    private var logoUri: Uri? = null
    private var logoUrlActual: String? = null

    // ========== VIEWS - DATOS DE EMPRESA ==========
    private lateinit var etRUC: EditText
    private lateinit var etRazonSocial: EditText
    private lateinit var etNombreComercial: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etEmail: EditText

    // ========== VIEWS - LOGO ==========
    private lateinit var switchMostrarLogo: CheckBox
    private lateinit var btnSeleccionarLogo: Button
    private lateinit var ivPreviewLogo: ImageView
    private lateinit var radioGroupTamanoLogo: RadioGroup
    private lateinit var radioGroupPosicionLogo: RadioGroup

    // ========== VIEWS - ENCABEZADO ==========
    private lateinit var switchMostrarNombre: CheckBox
    private lateinit var seekBarTamanoNombre: SeekBar
    private lateinit var tvTamanoNombre: TextView
    private lateinit var switchMostrarRUC: CheckBox
    private lateinit var switchMostrarDireccion: CheckBox
    private lateinit var switchMostrarTelefono: CheckBox
    private lateinit var switchMostrarEmail: CheckBox

    // ========== VIEWS - INFORMACIÓN DE VENTA ==========
    private lateinit var spinnerFormatoFecha: Spinner
    private lateinit var switchMostrarVendedor: CheckBox
    private lateinit var switchMostrarTerminal: CheckBox

    // ========== VIEWS - ITEMS ==========
    private lateinit var switchMostrarCodigo: CheckBox
    private lateinit var switchMostrarDimensiones: CheckBox

    // ========== VIEWS - TOTALES ==========
    private lateinit var switchMostrarSubtotal: CheckBox
    private lateinit var switchMostrarIGV: CheckBox
    private lateinit var switchDesglosarIGV: CheckBox
    private lateinit var seekBarTamanoTotal: SeekBar
    private lateinit var tvTamanoTotal: TextView

    // ========== VIEWS - PIE DE PÁGINA ==========
    private lateinit var switchMostrarDespedida: CheckBox
    private lateinit var etMensajeDespedida: EditText
    private lateinit var switchMostrarTextoPersonalizado: CheckBox
    private lateinit var etTextoPersonalizado: EditText

    // ========== VIEWS - PAPEL ==========
    private lateinit var radioGroupAnchoPapel: RadioGroup

    // ========== VIEWS - BOTONES ==========
    private lateinit var btnGuardar: Button
    private lateinit var btnRestaurar: Button

    // Launcher para seleccionar logo
    private val seleccionarLogoLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            logoUri = it
            ivPreviewLogo.setImageURI(it)
            ivPreviewLogo.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diseno_ticket)

        // Inicializar Firebase
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        // Configurar toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Configuración de Empresa"

        // Inicializar views
        inicializarViews()

        // Cargar datos guardados
        cargarDatosGuardados()

        // Configurar listeners
        configurarListeners()
    }

    private fun inicializarViews() {
        // Datos de empresa
        etRUC = findViewById(R.id.etRUC)
        etRazonSocial = findViewById(R.id.etRazonSocial)
        etNombreComercial = findViewById(R.id.etNombreComercial)
        etDireccion = findViewById(R.id.etDireccion)
        etTelefono = findViewById(R.id.etTelefono)
        etEmail = findViewById(R.id.etEmail)

        // Logo
        switchMostrarLogo = findViewById(R.id.switchMostrarLogo)
        btnSeleccionarLogo = findViewById(R.id.btnSeleccionarLogo)
        ivPreviewLogo = findViewById(R.id.ivPreviewLogo)
        radioGroupTamanoLogo = findViewById(R.id.radioGroupTamanoLogo)
        radioGroupPosicionLogo = findViewById(R.id.radioGroupPosicionLogo)

        // Encabezado
        switchMostrarNombre = findViewById(R.id.switchMostrarNombre)
        seekBarTamanoNombre = findViewById(R.id.seekBarTamanoNombre)
        tvTamanoNombre = findViewById(R.id.tvTamanoNombre)
        switchMostrarRUC = findViewById(R.id.switchMostrarRUC)
        switchMostrarDireccion = findViewById(R.id.switchMostrarDireccion)
        switchMostrarTelefono = findViewById(R.id.switchMostrarTelefono)
        switchMostrarEmail = findViewById(R.id.switchMostrarEmail)

        // Información de venta
        spinnerFormatoFecha = findViewById(R.id.spinnerFormatoFecha)
        switchMostrarVendedor = findViewById(R.id.switchMostrarVendedor)
        switchMostrarTerminal = findViewById(R.id.switchMostrarTerminal)

        // Items
        switchMostrarCodigo = findViewById(R.id.switchMostrarCodigo)
        switchMostrarDimensiones = findViewById(R.id.switchMostrarDimensiones)

        // Totales
        switchMostrarSubtotal = findViewById(R.id.switchMostrarSubtotal)
        switchMostrarIGV = findViewById(R.id.switchMostrarIGV)
        switchDesglosarIGV = findViewById(R.id.switchDesglosarIGV)
        seekBarTamanoTotal = findViewById(R.id.seekBarTamanoTotal)
        tvTamanoTotal = findViewById(R.id.tvTamanoTotal)

        // Pie de página
        switchMostrarDespedida = findViewById(R.id.switchMostrarDespedida)
        etMensajeDespedida = findViewById(R.id.etMensajeDespedida)
        switchMostrarTextoPersonalizado = findViewById(R.id.switchMostrarTextoPersonalizado)
        etTextoPersonalizado = findViewById(R.id.etTextoPersonalizado)

        // Papel
        radioGroupAnchoPapel = findViewById(R.id.radioGroupAnchoPapel)

        // Botones
        btnGuardar = findViewById(R.id.btnGuardar)
        btnRestaurar = findViewById(R.id.btnRestaurar)

        // Configurar spinner de formato de fecha
        val formatosFecha = arrayOf(
            "Solo fecha (31/12/2025)",
            "Solo hora (14:30)",
            "Completo (31/12/2025 14:30)",
            "Con segundos (31/12/2025 14:30:45)"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, formatosFecha)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFormatoFecha.adapter = adapter
    }

    private fun configurarListeners() {
        // Logo
        switchMostrarLogo.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarLogo = isChecked)
            actualizarVisibilidadOpcionesLogo()
        }

        btnSeleccionarLogo.setOnClickListener {
            seleccionarLogoLauncher.launch("image/*")
        }

        radioGroupTamanoLogo.setOnCheckedChangeListener { _, checkedId ->
            configuracion = configuracion.copy(
                tamanoLogo = when (checkedId) {
                    R.id.radioPequeno -> TamanoLogo.PEQUENO
                    R.id.radioMediano -> TamanoLogo.MEDIANO
                    R.id.radioGrande -> TamanoLogo.GRANDE
                    else -> TamanoLogo.MEDIANO
                }
            )
        }

        radioGroupPosicionLogo.setOnCheckedChangeListener { _, checkedId ->
            configuracion = configuracion.copy(
                posicionLogo = when (checkedId) {
                    R.id.radioIzquierda -> PosicionLogo.IZQUIERDA
                    R.id.radioCentro -> PosicionLogo.CENTRO
                    R.id.radioDerecha -> PosicionLogo.DERECHA
                    else -> PosicionLogo.CENTRO
                }
            )
        }

        // Encabezado
        switchMostrarNombre.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarNombreEmpresa = isChecked)
        }

        seekBarTamanoNombre.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val tamano = 12 + progress
                configuracion = configuracion.copy(tamanoNombre = tamano)
                tvTamanoNombre.text = "$tamano pt"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        switchMostrarRUC.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarRUC = isChecked)
        }

        switchMostrarDireccion.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarDireccion = isChecked)
        }

        switchMostrarTelefono.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarTelefono = isChecked)
        }

        switchMostrarEmail.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarEmail = isChecked)
        }

        // Información de venta
        spinnerFormatoFecha.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                configuracion = configuracion.copy(
                    formatoFechaHora = when (position) {
                        0 -> FormatoFechaHora.SOLO_FECHA
                        1 -> FormatoFechaHora.SOLO_HORA
                        2 -> FormatoFechaHora.COMPLETO
                        3 -> FormatoFechaHora.COMPLETO_CON_SEGUNDOS
                        else -> FormatoFechaHora.COMPLETO
                    }
                )
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        switchMostrarVendedor.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarVendedor = isChecked)
        }

        switchMostrarTerminal.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarTerminal = isChecked)
        }

        // Items
        switchMostrarCodigo.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarCodigo = isChecked)
        }

        switchMostrarDimensiones.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarDimensiones = isChecked)
        }

        // Totales
        switchMostrarSubtotal.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarSubtotal = isChecked)
        }

        switchMostrarIGV.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarIGV = isChecked)
        }

        switchDesglosarIGV.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(desglosarIGV = isChecked)
        }

        seekBarTamanoTotal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val tamano = 12 + progress
                configuracion = configuracion.copy(tamanoTotal = tamano)
                tvTamanoTotal.text = "$tamano pt"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Pie de página
        switchMostrarDespedida.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarMensajeDespedida = isChecked)
            etMensajeDespedida.isEnabled = isChecked
        }

        switchMostrarTextoPersonalizado.setOnCheckedChangeListener { _, isChecked ->
            configuracion = configuracion.copy(mostrarTextoPersonalizado = isChecked)
            etTextoPersonalizado.isEnabled = isChecked
        }

        // Papel
        radioGroupAnchoPapel.setOnCheckedChangeListener { _, checkedId ->
            configuracion = configuracion.copy(
                anchoPapel = when (checkedId) {
                    R.id.radio58mm -> AnchoPapel.TERMICO_58MM
                    R.id.radio80mm -> AnchoPapel.TERMICO_80MM
                    R.id.radioA4 -> AnchoPapel.A4
                    else -> AnchoPapel.TERMICO_80MM
                }
            )
        }

        // Botones
        btnGuardar.setOnClickListener {
            guardarTodo()
        }

        btnRestaurar.setOnClickListener {
            restaurarDefecto()
        }
    }

    /**
     * Carga datos guardados de Firestore
     */
    private fun cargarDatosGuardados() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val uid = auth.currentUser?.uid ?: return@launch

                val documento = firestore.collection("usuarios")
                    .document(uid)
                    .get()
                    .await()

                if (documento.exists()) {
                    val empresaData = documento.get("empresa") as? Map<*, *>

                    withContext(Dispatchers.Main) {
                        if (empresaData != null) {
                            // Cargar datos de empresa
                            etRUC.setText(empresaData["ruc"] as? String ?: "")
                            etRazonSocial.setText(empresaData["razonSocial"] as? String ?: "")
                            etNombreComercial.setText(empresaData["nombreComercial"] as? String ?: "")
                            etDireccion.setText(empresaData["direccion"] as? String ?: "")
                            etTelefono.setText(empresaData["telefono"] as? String ?: "")
                            etEmail.setText(empresaData["email"] as? String ?: "")

                            logoUrlActual = empresaData["logoUrl"] as? String

                            // Cargar configuración de diseño
                            val disenoTicket = empresaData["disenoTicket"] as? Map<*, *>
                            if (disenoTicket != null) {
                                configuracion = mapToConfiguracion(disenoTicket)
                                aplicarConfiguracionAViews()
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("DisenoTicket", "Error cargando datos: ${e.message}", e)
            }
        }
    }

    /**
     * Guarda TODO: datos de empresa + diseño + logo
     */
    private fun guardarTodo() {
        // Validar campos obligatorios
        if (etRUC.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa el RUC", Toast.LENGTH_SHORT).show()
            etRUC.requestFocus()
            return
        }

        if (etRazonSocial.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingresa la razón social", Toast.LENGTH_SHORT).show()
            etRazonSocial.requestFocus()
            return
        }

        // Actualizar configuración con datos del formulario
        val mensajeDespedida = etMensajeDespedida.text.toString()
        val textoPersonalizado = etTextoPersonalizado.text.toString()

        configuracion = configuracion.copy(
            mensajeDespedida = mensajeDespedida,
            textoPersonalizado = if (textoPersonalizado.isNotEmpty()) textoPersonalizado else null,
            fechaActualizacion = Timestamp.now()
        )

        // Mostrar progreso
        btnGuardar.isEnabled = false
        btnGuardar.text = "Guardando..."

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

                // 1. Subir logo si se seleccionó uno nuevo
                var logoUrl = logoUrlActual
                if (logoUri != null) {
                    logoUrl = subirLogo(uid, logoUri!!)
                }

                // 2. Preparar datos de empresa
                val empresaData = hashMapOf(
                    "ruc" to etRUC.text.toString().trim(),
                    "razonSocial" to etRazonSocial.text.toString().trim(),
                    "nombreComercial" to etNombreComercial.text.toString().trim().takeIf { it.isNotEmpty() },
                    "direccion" to etDireccion.text.toString().trim(),
                    "telefono" to etTelefono.text.toString().trim(),
                    "email" to etEmail.text.toString().trim().takeIf { it.isNotEmpty() },
                    "logoUrl" to logoUrl,
                    "disenoTicket" to configuracionToMap(configuracion),
                    "version" to FieldValue.increment(1),
                    "ultima_actualizacion" to FieldValue.serverTimestamp()
                )

                // 3. Guardar en Firestore
                firestore.collection("usuarios")
                    .document(uid)
                    .update("empresa", empresaData)
                    .await()

                Log.d("DisenoTicket", "✅ Datos guardados en Firestore")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DisenoTicketActivity,
                        "✅ Datos guardados correctamente\n\nRecuerda avisar a tus vendedores que sincronicen",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }

            } catch (e: Exception) {
                Log.e("DisenoTicket", "❌ Error guardando: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    btnGuardar.isEnabled = true
                    btnGuardar.text = "GUARDAR"
                    Toast.makeText(
                        this@DisenoTicketActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Sube el logo a Firebase Storage
     */
    private suspend fun subirLogo(uid: String, uri: Uri): String {
        return withContext(Dispatchers.IO) {
            val logoRef = storage.reference.child("empresas/$uid/logo.jpg")
            logoRef.putFile(uri).await()
            logoRef.downloadUrl.await().toString()
        }
    }

    private fun restaurarDefecto() {
        configuracion = ConfiguracionTicketDefecto.obtener()
        aplicarConfiguracionAViews()
        Toast.makeText(this, "Configuración restaurada", Toast.LENGTH_SHORT).show()
    }

    private fun aplicarConfiguracionAViews() {
        switchMostrarLogo.isChecked = configuracion.mostrarLogo

        when (configuracion.tamanoLogo) {
            TamanoLogo.PEQUENO -> radioGroupTamanoLogo.check(R.id.radioPequeno)
            TamanoLogo.MEDIANO -> radioGroupTamanoLogo.check(R.id.radioMediano)
            TamanoLogo.GRANDE -> radioGroupTamanoLogo.check(R.id.radioGrande)
        }

        when (configuracion.posicionLogo) {
            PosicionLogo.IZQUIERDA -> radioGroupPosicionLogo.check(R.id.radioIzquierda)
            PosicionLogo.CENTRO -> radioGroupPosicionLogo.check(R.id.radioCentro)
            PosicionLogo.DERECHA -> radioGroupPosicionLogo.check(R.id.radioDerecha)
        }

        switchMostrarNombre.isChecked = configuracion.mostrarNombreEmpresa
        seekBarTamanoNombre.progress = configuracion.tamanoNombre - 12
        tvTamanoNombre.text = "${configuracion.tamanoNombre} pt"
        switchMostrarRUC.isChecked = configuracion.mostrarRUC
        switchMostrarDireccion.isChecked = configuracion.mostrarDireccion
        switchMostrarTelefono.isChecked = configuracion.mostrarTelefono
        switchMostrarEmail.isChecked = configuracion.mostrarEmail

        spinnerFormatoFecha.setSelection(when (configuracion.formatoFechaHora) {
            FormatoFechaHora.SOLO_FECHA -> 0
            FormatoFechaHora.SOLO_HORA -> 1
            FormatoFechaHora.COMPLETO -> 2
            FormatoFechaHora.COMPLETO_CON_SEGUNDOS -> 3
        })
        switchMostrarVendedor.isChecked = configuracion.mostrarVendedor
        switchMostrarTerminal.isChecked = configuracion.mostrarTerminal

        switchMostrarCodigo.isChecked = configuracion.mostrarCodigo
        switchMostrarDimensiones.isChecked = configuracion.mostrarDimensiones

        switchMostrarSubtotal.isChecked = configuracion.mostrarSubtotal
        switchMostrarIGV.isChecked = configuracion.mostrarIGV
        switchDesglosarIGV.isChecked = configuracion.desglosarIGV
        seekBarTamanoTotal.progress = configuracion.tamanoTotal - 12
        tvTamanoTotal.text = "${configuracion.tamanoTotal} pt"

        switchMostrarDespedida.isChecked = configuracion.mostrarMensajeDespedida
        etMensajeDespedida.setText(configuracion.mensajeDespedida)
        etMensajeDespedida.isEnabled = configuracion.mostrarMensajeDespedida

        switchMostrarTextoPersonalizado.isChecked = configuracion.mostrarTextoPersonalizado
        etTextoPersonalizado.setText(configuracion.textoPersonalizado ?: "")
        etTextoPersonalizado.isEnabled = configuracion.mostrarTextoPersonalizado

        when (configuracion.anchoPapel) {
            AnchoPapel.TERMICO_58MM -> radioGroupAnchoPapel.check(R.id.radio58mm)
            AnchoPapel.TERMICO_80MM -> radioGroupAnchoPapel.check(R.id.radio80mm)
            AnchoPapel.A4 -> radioGroupAnchoPapel.check(R.id.radioA4)
        }

        actualizarVisibilidadOpcionesLogo()
    }

    private fun actualizarVisibilidadOpcionesLogo() {
        val mostrar = configuracion.mostrarLogo
        btnSeleccionarLogo.isEnabled = mostrar
        radioGroupTamanoLogo.visibility = if (mostrar) View.VISIBLE else View.GONE
        radioGroupPosicionLogo.visibility = if (mostrar) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun mapToConfiguracion(map: Map<*, *>): ConfiguracionTicket {
        return ConfiguracionTicket(
            mostrarLogo = map["mostrarLogo"] as? Boolean ?: true,
            logoUrl = map["logoUrl"] as? String,
            tamanoLogo = try { TamanoLogo.valueOf(map["tamanoLogo"] as? String ?: "MEDIANO") } catch (e: Exception) { TamanoLogo.MEDIANO },
            posicionLogo = try { PosicionLogo.valueOf(map["posicionLogo"] as? String ?: "CENTRO") } catch (e: Exception) { PosicionLogo.CENTRO },
            mostrarNombreEmpresa = map["mostrarNombreEmpresa"] as? Boolean ?: true,
            tamanoNombre = (map["tamanoNombre"] as? Long)?.toInt() ?: 16,
            mostrarRUC = map["mostrarRUC"] as? Boolean ?: true,
            mostrarDireccion = map["mostrarDireccion"] as? Boolean ?: true,
            mostrarTelefono = map["mostrarTelefono"] as? Boolean ?: true,
            mostrarEmail = map["mostrarEmail"] as? Boolean ?: false,
            formatoFechaHora = try { FormatoFechaHora.valueOf(map["formatoFechaHora"] as? String ?: "COMPLETO") } catch (e: Exception) { FormatoFechaHora.COMPLETO },
            mostrarVendedor = map["mostrarVendedor"] as? Boolean ?: true,
            mostrarTerminal = map["mostrarTerminal"] as? Boolean ?: false,
            mostrarCodigo = map["mostrarCodigo"] as? Boolean ?: false,
            mostrarDimensiones = map["mostrarDimensiones"] as? Boolean ?: true,
            mostrarSubtotal = map["mostrarSubtotal"] as? Boolean ?: true,
            mostrarIGV = map["mostrarIGV"] as? Boolean ?: true,
            desglosarIGV = map["desglosarIGV"] as? Boolean ?: true,
            tamanoTotal = (map["tamanoTotal"] as? Long)?.toInt() ?: 14,
            mensajeDespedida = map["mensajeDespedida"] as? String ?: "¡Gracias por su compra!",
            mostrarMensajeDespedida = map["mostrarMensajeDespedida"] as? Boolean ?: true,
            textoPersonalizado = map["textoPersonalizado"] as? String,
            mostrarTextoPersonalizado = map["mostrarTextoPersonalizado"] as? Boolean ?: false,
            anchoPapel = try { AnchoPapel.valueOf(map["anchoPapel"] as? String ?: "TERMICO_80MM") } catch (e: Exception) { AnchoPapel.TERMICO_80MM }
        )
    }

    private fun configuracionToMap(config: ConfiguracionTicket): Map<String, Any?> {
        return mapOf(
            "mostrarLogo" to config.mostrarLogo,
            "logoUrl" to config.logoUrl,
            "tamanoLogo" to config.tamanoLogo.name,
            "posicionLogo" to config.posicionLogo.name,
            "mostrarNombreEmpresa" to config.mostrarNombreEmpresa,
            "tamanoNombre" to config.tamanoNombre,
            "mostrarRUC" to config.mostrarRUC,
            "mostrarDireccion" to config.mostrarDireccion,
            "mostrarTelefono" to config.mostrarTelefono,
            "mostrarEmail" to config.mostrarEmail,
            "formatoFechaHora" to config.formatoFechaHora.name,
            "mostrarVendedor" to config.mostrarVendedor,
            "mostrarTerminal" to config.mostrarTerminal,
            "mostrarCodigo" to config.mostrarCodigo,
            "mostrarDimensiones" to config.mostrarDimensiones,
            "mostrarSubtotal" to config.mostrarSubtotal,
            "mostrarIGV" to config.mostrarIGV,
            "desglosarIGV" to config.desglosarIGV,
            "tamanoTotal" to config.tamanoTotal,
            "mensajeDespedida" to config.mensajeDespedida,
            "mostrarMensajeDespedida" to config.mostrarMensajeDespedida,
            "textoPersonalizado" to config.textoPersonalizado,
            "mostrarTextoPersonalizado" to config.mostrarTextoPersonalizado,
            "anchoPapel" to config.anchoPapel.name,
            "fechaActualizacion" to Timestamp.now()
        )
    }
}