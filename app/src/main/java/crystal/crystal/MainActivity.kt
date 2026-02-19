package crystal.crystal

// Android Core
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

// AndroidX
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope

// Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

// Librerías externas
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// Java IO y utilidades
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Módulos del proyecto - Catálogo y datos
import crystal.crystal.catalogo.CatalogoActivity
import crystal.crystal.databinding.ActivityMainBinding
import crystal.crystal.datos.ListaActivity
import crystal.crystal.datos.Product

// Módulos del proyecto - Clientes
import crystal.crystal.clientes.Cliente
import crystal.crystal.clientes.ClienteDatabase
import crystal.crystal.clientes.ClienteRepository
import crystal.crystal.clientes.DialogoSeleccionClientes
import crystal.crystal.clientes.GestionClientesActivity
import crystal.crystal.clientes.SyncClientesWorker
import crystal.crystal.clientes.SyncInicialClientesWorker
import crystal.crystal.clientes.VoiceSearchManager

// Módulos del proyecto - Productos
import crystal.crystal.productos.GestionProductosActivity
import crystal.crystal.productos.Producto
import crystal.crystal.productos.ProductoDatabase
import crystal.crystal.productos.ProductoRepository
import crystal.crystal.productos.ProductoVoiceSearchManager
import crystal.crystal.productos.SyncProductosWorker

// Módulos del proyecto - Comprobantes y tickets
import crystal.crystal.comprobantes.*

// Módulos del proyecto - Dictado
import crystal.crystal.dictado.DictadoMedidas

// Módulos del proyecto - POS
import crystal.crystal.pos.EdicionMasivaManager
import crystal.crystal.pos.ImportadorMedidas
import crystal.crystal.pos.PosManager
import crystal.crystal.pos.RoleConfigManager

// Módulos del proyecto - Red, registro y taller
import crystal.crystal.red.ListChatActivity
import crystal.crystal.registro.AyudaActivity
import crystal.crystal.registro.GestionDispositivosActivity
import crystal.crystal.registro.InicioActivity
import crystal.crystal.registro.PinAuthActivity
import crystal.crystal.registro.Registro
import crystal.crystal.taller.Taller

@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("NewApi", "SetTextI18n")
@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "DEPRECATION")
class MainActivity : AppCompatActivity() {

    // ─── Constantes ───
    companion object {
        private const val RECEIVE_PRESUPUESTO_REQUEST = 3
        private const val DICTADO_REQUEST_CODE = 200
        private const val CODIGO_SOLICITUD_OCR = 300
    }

    // ─── Core / UI ───
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var lista: MutableList<Listado> = mutableListOf()
    private var selectedPosition: Int = -1
    private var colorSeleccionado: Int = 0

    // ─── Firebase / Autenticación ───
    private val auth = FirebaseAuth.getInstance()
    private lateinit var currentUserId: String
    private var db = Firebase.firestore

    // ─── Roles y dispositivo ───
    private var rolDispositivo: String = "TERMINAL"
    private var esPatron: Boolean = false
    private var nombreVendedor: String = "Vendedor"

    // ─── Spinners y unidades ───
    private lateinit var usados: Spinner
    private lateinit var unidades: Spinner
    private var retaso = 1.8f

    // ─── Cámara / Galería ───
    private val RECORD_REQUEST_CODE = 101
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2

    // ─── POS / Ventas (delegado a PosManager) ───
    private lateinit var posManager: PosManager
    private lateinit var edicionMasivaManager: EdicionMasivaManager
    private lateinit var roleConfigManager: RoleConfigManager

    private val posCallback = object : PosManager.PosCallback {
        override fun obtenerLista(): MutableList<Listado> = lista
        override fun obtenerNombreVendedor(): String = nombreVendedor
        override fun obtenerEsPatron(): Boolean = esPatron
        override fun obtenerRolDispositivo(): String = rolDispositivo
        override fun actualizar() = this@MainActivity.actualizar()
        override fun obtenerUidParaConsulta(): String? {
            val esTerminalPIN = sharedPreferences.getBoolean("es_terminal_pin", false)
            return if (esTerminalPIN) {
                sharedPreferences.getString("patron_uid", null)
            } else {
                FirebaseAuth.getInstance().currentUser?.uid
            }
        }
        override fun guardarDatos() = this@MainActivity.guardarDatos()
        override fun mostrarDialogoPatronNoEncontrado() = roleConfigManager.mostrarDialogoPatronNoEncontrado()
        override fun mostrarOpcionReconectar() = roleConfigManager.mostrarOpcionReconectar()
    }

    // ─── Clientes ───
    private lateinit var clienteRepository: ClienteRepository
    private lateinit var voiceSearchManager: VoiceSearchManager
    private var clienteSeleccionado: Cliente? = null

    // ─── Productos ───
    private lateinit var productoRepository: ProductoRepository
    private lateinit var productoVoiceSearchManager: ProductoVoiceSearchManager
    private var productoSeleccionado: Producto? = null
    private var modoBusqueda: Boolean = false

    // ─── Importador de medidas ───
    private lateinit var importadorMedidas: ImportadorMedidas

    // ─── Dictado por voz ───
    private var dictadoMedidas: DictadoMedidas? = null
    private var dictadoActivo = false
    private var backgroundOriginalMed1: Drawable? = null

    // ─── Chat ───
    private val unreadCountByChat = mutableMapOf<String, Int>()
    private var ultimaSincronizacion: Long = 0L

    @SuppressLint("NewApi", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar SharedPreferences
        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        fotoUsuario()
        // Inicializar RoleConfigManager
        roleConfigManager = RoleConfigManager(this, binding, sharedPreferences, auth, db)
        roleConfigManager.callback = object : RoleConfigManager.Callback {
            override fun onRoleConfigured(rolDispositivo: String, esPatron: Boolean, nombreVendedor: String) {
                this@MainActivity.rolDispositivo = rolDispositivo
                this@MainActivity.esPatron = esPatron
                this@MainActivity.nombreVendedor = nombreVendedor
            }
            override fun guardarDatos() = this@MainActivity.guardarDatos()
            override fun sincronizarDatosManualmente() = posManager.sincronizarDatosManualmente()
        }
        // Cargar y consultar estado del usuario
        roleConfigManager.cargarEstadoUsuario()
        lifecycleScope.launch { roleConfigManager.cargarRolYConfigurar() }
        cliente()
        uni1()
        uni2()
        eliminar()
        abrir()
        importadorMedidas = ImportadorMedidas(
            activity = this,
            binding = binding,
            obtenerLista = { lista },
            actualizar = { actualizar() },
            conversor = { conver(it) },
            calcPies = { m1, m2 -> pies(m1, m2) },
            calcMetroCua = { m1, m2 -> metroCua(m1, m2) },
            calcMLineales = { m1, m2 -> mLineales(m1, m2) },
            calcMCubicos = { m1, m2, m3 -> mCubicos(m1, m2, m3) }
        )
        manejarPresupuestoRecibido()
        posManager = PosManager(this, binding, sharedPreferences)
        posManager.setCallback(posCallback)
        posManager.inicializarControlesPOS()

        edicionMasivaManager = EdicionMasivaManager(this, lista)
        edicionMasivaManager.onListaModificada = { actualizar() }
        roleConfigManager.verificarAutorizacionTerminal()
        inicializarClientes()
        inicializarProductos()
        configurarBusqueda()
        inicializarDictadoMedidas()
        configurarRetrocesoEditTexts()

        binding.btnLimpiar.setOnClickListener {
            try {
                if (binding.usTxt.text != "uni") {
                    focusMed1()
                } else {
                    binding.cantEditxt.requestFocus()
                }

                binding.precioUnitario.text = "0.0"
                binding.precioCantidad.text = "0.0"
                binding.pcTxt.text = "0.0"
                binding.mcTxt.text = "0.0"
                binding.med1Editxt.setText(if (binding.usTxt.text=="uni"){"1"}else{""})
                binding.med2Editxt.setText(if (binding.usTxt.text=="ml"||binding.usTxt.text=="uni"){"1"}else{""})
                binding.cantEditxt.setText("")
                binding.precioEditxt.setText("")
                binding.proEditxt.setText("")
                binding.clienteEditxt.setText("")
                binding.med1Editxt.hint = ""
                binding.med2Editxt.hint = ""
                binding.cantEditxt.hint = ""
                binding.precioTotal.text = "0.0"
                binding.piesTotal.text = "0.0"
                binding.metrosTotal.text = "0.0"
                binding.per.text = "0.0"
                binding.prueTxt.text= ""
                binding.tvpCliente.text = "Cliente"
                lista.clear()
                binding.list.onRemoteAdapterConnected()
                actualizar()
            } catch (_: Exception) {
            }
        }

        binding.btBuscar.setOnClickListener {
            startActivity(Intent(this,BaulActivity::class.java))
        }

        binding.btnCalcular.setOnLongClickListener {
            openPdf()
            return@setOnLongClickListener true
        }

        binding.btUser.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }

        binding.precioTotal.setOnClickListener {
            startActivity(Intent(this, VendePapa::class.java).putExtra(
                "monto", binding.precioTotal.text.toString()))}

        binding.listadoTxt.setOnClickListener {
            startActivity(Intent(this, ListaActivity::class.java).
            putExtra("monto", binding.per.text.toString()))}

        binding.tallerCal.setOnClickListener {
            val intent = Intent(this, Taller::class.java)
            val cliente = binding.clienteEditxt.text.toString()
            if (cliente.isNotEmpty()) {
                intent.putExtra("cliente", cliente)
            }
            if (lista.isNotEmpty()) {
                intent.putExtra("lista_presupuesto", ArrayList(lista))
            }
            startActivity(intent)
        }

        // Supongamos que el usuario ya está autenticado
        currentUserId = roleConfigManager.obtenerIdUsuarioActual()

        setupUnreadMessagesListener()

        // Al presionar btnChat, se redirige a la ListChatActivity
        binding.btnChat.setOnClickListener {
            val intent = Intent(this, ListChatActivity::class.java)
            intent.putExtra("usuario", currentUserId)
            startActivity(intent)
        }

        binding.btnCatalogo.setOnClickListener {
            startActivity(Intent(this, CatalogoActivity::class.java))}

        // Agregar OnLongClickListener al textView prodtxt
        // MODIFICAR el listener existente de prodtxt:
        binding.prodtxt.setOnLongClickListener {
            voiceSearchManager.iniciarBusquedaPorVoz { resultado ->
                manejarResultadoBusqueda(resultado)
            }
            true
        }

        binding.btnCalcular.setOnClickListener {
            try {
                //Zona real
                if (binding.usTxt.text!="uni") {
                    focusMed1()} else {
                    binding.cantEditxt.requestFocus()
                }

                agregarListado()

                actualizar()

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "ingrese un número válido", Toast.LENGTH_LONG).show()
            }
        }

        binding.btWallet.setOnClickListener {
            // en MainActivity (donde tengas el onClick del botón Wallet)
            startActivity(Intent(this, PinAuthActivity::class.java))
        }

        /*binding.btScan.setOnClickListener {
            val opciones = arrayOf<CharSequence>(
                "📷 Tomar foto",
                "🖼️ Elegir de la galería",
                "🔍 OCR - Extraer medidas"  // ← NUEVA OPCIÓN
            )
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Elige una opción")
            builder.setItems(opciones) { dialog, item ->
                when (item) {
                    0 -> openCamera()                    // ← MANTENER tu función existente
                    1 -> openGallery()                   // ← MANTENER tu función existente
                    2 -> {                               // ← NUEVA FUNCIONALIDAD OCR
                        val intent = Intent(this, crystal.crystal.ocr.OcrActivity::class.java)
                        startActivityForResult(intent, CODIGO_SOLICITUD_OCR)
                    }
                }
            }
            builder.show()
        }*/

        binding.btAyuda.setOnClickListener {
            startActivity(Intent(this, AyudaActivity::class.java))
        }

        binding.txtRetaso.setOnClickListener {
            mostrarDialogoRetaso()
        }

        binding.tvpCliente.setOnLongClickListener {
            mostrarMenuPresupuesto()

            true
        }

        binding.listadoTxt.setOnLongClickListener {
            abrirSelectorPresupuesto()
            true
        }

        binding.btnProcesarVenta.setOnLongClickListener {
            // Abrir DisenoTicketActivity
            val intent = Intent(this, DisenoTicketActivity::class.java)
            startActivity(intent)
            true // Consumir el evento
        }
        // Click normal genera el ticket
        binding.btnProcesarVenta.setOnClickListener {
            // Obtener datos reales de la venta
            val cliente = binding.clienteEditxt.text.toString().trim()
            val total = binding.precioTotal.text.toString().trim()

            // Validar que no estén vacíos
            if (cliente.isEmpty()) {
                Toast.makeText(this, "Ingresa el nombre del cliente", Toast.LENGTH_SHORT).show()
                binding.clienteEditxt.requestFocus()
                return@setOnClickListener
            }
            clienteSeleccionado?.let { clienteBD ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val totalTexto = binding.precioTotal.text.toString()
                            .replace("S/", "")
                            .replace(" ", "")
                            .trim()

                        // ⭐ CLAVE: Reemplazar coma por punto ANTES de convertir
                        val totalFloat: Float = totalTexto.replace(",", ".").toFloatOrNull() ?: 0f

                        clienteRepository.registrarVenta(clienteBD.id, totalFloat)
                    } catch (_: Exception) {
                    }
                }
            }

            if (lista.isEmpty()) {
                Toast.makeText(this, "Agrega productos a la venta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            posManager.generarTicketVenta(cliente, total)
        }

        posManager.cargarConfiguracionTicket()
        posManager.cargarDatosEmpresa()
        roleConfigManager.verificarAutenticacionTerminal()
        // Cargar timestamp de última sincronización
        ultimaSincronizacion = sharedPreferences.getLong("empresa_ultima_sincronizacion", 0L)

        binding.txSincronizar.setOnClickListener {
            posManager.sincronizarDatosManualmente()
        }
    }

    override fun onPause() {
        super.onPause()
        // Guardar los datos antes de que la aplicación pase a segundo plano
        guardarDatos()
    }

    override fun onStart() {
        super.onStart()

        // ⭐ SI ES TERMINAL, NO VERIFICAR FIREBASE AUTH
        val tipoSesion = sharedPreferences.getString("session_type", null)
        if (tipoSesion == "TERMINAL") {
            // Terminal no usa FirebaseAuth
            return
        }

        // Usuario normal: verificar autenticación
        val auth = FirebaseAuth.getInstance()
        val usuarioActual = auth.currentUser
        usuarioActual?.reload()?.addOnCompleteListener { tarea ->
            if (!tarea.isSuccessful) {
                auth.signOut()
                startActivity(Intent(this, InicioActivity::class.java))
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val estadoUsuario = roleConfigManager.obtenerEstadoUsuario()
        if (estadoUsuario == "VENTAS") {
            lifecycleScope.launch {
                roleConfigManager.verificarAutorizacionVentas()
            }
        }
    }
    // ═══════════════════════════════════════════════════
    // ─── PERFIL DE USUARIO ───
    // ═══════════════════════════════════════════════════
    @SuppressLint("HardwareIds")
    private fun fotoUsuario(){
        val tipoSesion = sharedPreferences.getString("session_type", null)

        if (tipoSesion == "TERMINAL") {
            val nombreVendedor = sharedPreferences.getString("nombre_vendedor", "Terminal") ?: "Terminal"
            val patronUid = sharedPreferences.getString("patron_uid", null)

            binding.txUser.text = nombreVendedor

            if (patronUid != null) {
                val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

                db.collection("usuarios")
                    .document(patronUid)
                    .collection("plan_ventas")
                    .document("dispositivos")
                    .collection("autorizados")
                    .document(deviceId)
                    .get()
                    .addOnSuccessListener { doc ->
                        val fotoUrl = doc.getString("foto_url")

                        if (fotoUrl != null && fotoUrl.isNotEmpty()) {
                            Glide.with(this)
                                .load(fotoUrl)
                                .circleCrop()
                                .placeholder(R.drawable.ic_usuario4)
                                .error(R.drawable.ic_usuario4)
                                .into(binding.btUser)
                        } else {
                            Glide.with(this)
                                .load(R.drawable.ic_usuario4)
                                .circleCrop()
                                .into(binding.btUser)
                        }
                    }
                    .addOnFailureListener {
                        Glide.with(this)
                            .load(R.drawable.ic_usuario4)
                            .circleCrop()
                            .into(binding.btUser)
                    }
            } else {
                Glide.with(this)
                    .load(R.drawable.ic_usuario4)
                    .circleCrop()
                    .into(binding.btUser)
            }

            return
        }

        // Usuario normal con Google
        val usuarioActual = auth.currentUser

        if (usuarioActual?.photoUrl != null) {
            Glide.with(this)
                .load(usuarioActual.photoUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_usuario4)
                .error(R.drawable.ic_usuario4)
                .into(binding.btUser)
        } else {
            Glide.with(this)
                .load(R.drawable.ic_usuario4)
                .circleCrop()
                .into(binding.btUser)
        }

        if (usuarioActual?.displayName != null && usuarioActual.displayName!!.isNotEmpty()) {
            val primerNombre = usuarioActual.displayName!!.split(" ").firstOrNull() ?: "Usuario"
            binding.txUser.text = primerNombre
        } else {
            binding.txUser.text = "Usuario"
        }
    }
    // ═══════════════════════════════════════════════════
    // ─── LISTADO / CARRITO ───
    // agregarListado, actualizar, adaptadores, filtrarLista
    // ═══════════════════════════════════════════════════
    @SuppressLint("SetTextI18n")
    private fun agregarListado() {
        // escalas, unidades y colores
        val escala = binding.usTxt.text.toString()
        val uni   = binding.prTxt.text.toString()
        val color = colorSeleccionado
        // datos ingresados por el usuario
        val medida1 = med1()
        val medida2 = med2()
        val medida3 = med3()
        val cantidad = binding.cantEditxt.text.toString().toFloat()
        val precio = binding.precioEditxt.text.toString().toFloat()
        val producto = binding.proEditxt.text.toString().ifBlank { "..." }
        // resultados de calculos
        val piescua = pies(medida1,medida2)
        val metroscua = metroCua(medida1,medida2)
        val ml = mLineales(medida1,medida2)
        val cub = mCubicos(medida1,medida2,medida3)
        // calculo de cantidades
        val mlcant = ml * cantidad
        val piescant = piescua * cantidad
        val metroscant= metroCua(medida1,medida2) * cantidad
        val cubcant  = cub * cantidad
        val peri = perim()
        // vinculacion de fotos
        val uri = ""
        // calculo de precios

        val costounitario = when (escala){
            "p2" ->piescua * precio
            "m2" ->metroscua * precio
            "ml" ->ml * precio
            "m3" ->cub * precio
            else -> { precio}
        }
        val costocantidad = when (escala){
            "p2" ->piescant * precio
            "m2" ->metroscant * precio
            "ml" ->mlcant * precio
            "m3" ->cubcant * precio
            else -> {cantidad * precio}
        }

        binding.pcTxt.text = df1(piescant)
        binding.mcTxt.text = df1(metroscua)
        binding.precioUnitario.text = df2(costounitario)
        binding.precioCantidad.text = df2(costocantidad)
        binding.prueTxt.text= med3().toString()

        binding.med1Editxt.hint = df1(medida1)
        binding.med2Editxt.hint = df1(medida2)
        binding.med3Editxt.hint = df1(medida3)

        binding.med1Editxt.setText(if (binding.usTxt.text=="uni"){"1"}else{""})
        binding.med2Editxt.setText(if (binding.usTxt.text=="ml"||binding.usTxt.text=="uni"){"1"}else{""})
        binding.med3Editxt.text?.clear()
        binding.cantEditxt.text?.clear()

        val medidas = Listado(
            escala, uni, medida1, medida2, medida3, cantidad, piescant, precio, costocantidad,
            producto, peri, metroscua, mlcant, cub, color,uri
        )

        lista.add(medidas)
    }
    @SuppressLint("SetTextI18n")
    private fun actualizar() {
        // Creamos un nuevo adapter con los datos de la lista
        val adapter = adaptadores()

        // Configuramos el adapter para el ListView
        binding.list.adapter = adapter

        // Calculamos los totales
        var costoTotal = 0F
        var metroscuaTotal = 0F
        var piescuaTotal = 0F
        var periTotal = 0F
        for (medida in lista) {
            costoTotal += medida.costo
            metroscuaTotal += medida.metcua
            piescuaTotal += medida.piescua
            periTotal += medida.peri
        }

        // Actualizamos los TextView con los totales
        binding.precioTotal.text = df2(costoTotal)
        binding.metrosTotal.text = df1(metroscuaTotal)
        binding.piesTotal.text = df1(piescuaTotal)
        binding.per.text = "${df1(periTotal)} m.."

        // Notificamos al adapter que se actualizaron los datos
        adapter.notifyDataSetChanged()
    }
    private fun adaptadores(): ArrayAdapter<SpannableString> {
        val clipCodigo = 0x1F4CE
        val clip = String(Character.toChars(clipCodigo))

        val adapter = ArrayAdapter(
            this, R.layout.lista_cal,
            lista.map { datos ->
                // Acortar el URI solo para mostrarlo en la interfaz
                val uriAcortado = if (datos.uri.length > 20) "...${datos.uri.takeLast(20)}" else datos.uri

                val text = when (datos.escala) {
                    "p2" -> {
                        "(${df1(datos.medi1)} x ${df1(datos.medi2)} x ${df1(datos.canti)} = " +
                                "${df1(datos.piescua)}(${datos.escala}) " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto} " +
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    "m2" -> {
                        "(${df1(datos.medi1)} x ${df1(datos.medi2)} x ${df1(datos.canti)} = " +
                                "${df1(datos.metcua)}(${datos.escala}) " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    "m3" -> {
                        "(${df1(datos.medi1)} x ${df1(datos.medi2)} x ${df1(datos.medi3)} x ${df1(datos.canti)} = " +
                                "${df1(datos.metcub)}(${datos.escala}) " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    "ml" -> {
                        "(${df1(datos.medi1)} x ${df1(datos.canti)} = ${df1(datos.metli)}(${datos.escala}) " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    "uni" -> {
                        "${df1(datos.canti)}(${datos.escala}) = " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    else -> {
                        "(${df1(datos.medi1)} x ${df1(datos.medi2)} x ${df1(datos.canti)} = ${df1(datos.piescua)} " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                }

                val spannableString = SpannableString(text)
                spannableString.setSpan(
                    ForegroundColorSpan(datos.color),
                    text.indexOf("(${datos.escala})"),
                    text.indexOf("(${datos.escala})") + datos.escala.length + 2,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString
            }
        )

        return adapter
    }
    // ═══════════════════════════════════════════════════
    // ─── CLIENTES ───
    // cliente, inicializarClientes, buscarCliente,
    // seleccionarCliente, mostrarListaClientes,
    // manejarResultadoBusqueda
    // ═══════════════════════════════════════════════════
    @SuppressLint("SetTextI18n")
    private fun cliente() {
        val paqueteR = intent.extras
        val clienteIntent = paqueteR?.getString("rcliente")  // Cliente desde baulActivity
        val clienteRecup = cargarDatosGuardados()  // Cliente de SharedPreferences

        // ⭐ PRIORIDAD 1: Cliente desde baulActivity (presupuesto abierto)
        if (clienteIntent != null) {
            Log.d("MainActivity", "✅ Cliente desde presupuesto: $clienteIntent")

            // Actualizar UI
            binding.tvpCliente.text = "Presupuesto de $clienteIntent"
            binding.clienteEditxt.setText(clienteIntent)

            // Guardar en SharedPreferences para próxima sesión
            sharedPreferences.edit()
                .putString("cliente", clienteIntent)
                .putString("tvpCliente", "Presupuesto de $clienteIntent")
                .apply()

            // Mostrar cuerpo (área de trabajo)
            binding.lyCuello.visibility = View.GONE
            binding.lyCuerpo.visibility = View.VISIBLE
        }
        // ⭐ PRIORIDAD 2: Cliente guardado de sesión anterior
        else if (clienteRecup.isNotEmpty()) {
            Log.d("MainActivity", "✅ Cliente de sesión anterior: $clienteRecup")

            // Restaurar de SharedPreferences
            val tvpClienteGuardado = sharedPreferences.getString("tvpCliente", "Presupuesto de $clienteRecup")

            binding.tvpCliente.text = tvpClienteGuardado
            binding.clienteEditxt.setText(clienteRecup)

            binding.lyCuello.visibility = View.GONE
            binding.lyCuerpo.visibility = View.VISIBLE
        }
        // ⭐ PRIORIDAD 3: Sin cliente (nuevo presupuesto)
        else {
            Log.d("MainActivity", "📝 Nuevo presupuesto (sin cliente)")

            binding.tvpCliente.text = "Cliente"
            binding.clienteEditxt.setText("")

            // Mostrar cuello (header para ingresar cliente)
            binding.lyCuello.visibility = View.VISIBLE
            binding.lyCuerpo.visibility = View.GONE
        }

        // ========== LISTENER: Click en tvpCliente ==========
        // Permite cambiar de cliente tocando el título
        binding.tvpCliente.setOnClickListener {
            binding.lyCuello.visibility = View.VISIBLE
            binding.lyCuerpo.visibility = View.GONE
        }

        // ========== LISTENER: Click en btGo (GUARDAR CLIENTE) ==========
        binding.btGo.setOnClickListener {
            val clientet = binding.clienteEditxt.text.toString().trim()

            // Actualizar tvpCliente según haya o no cliente
            binding.tvpCliente.text = if (clientet.isNotEmpty()) {
                "Presupuesto de $clientet"
            } else {
                "Cliente"
            }

            if (clientet.isNotEmpty()) {
                // HAY CLIENTE: Guardar y mostrar área de trabajo

                binding.lyCuello.visibility = View.GONE
                binding.lyCuerpo.visibility = View.VISIBLE

                // Guardar en SharedPreferences
                sharedPreferences.edit()
                    .putString("cliente", clientet)
                    .putString("tvpCliente", "Presupuesto de $clientet")
                    .apply()

                Toast.makeText(this, "Cliente guardado correctamente", Toast.LENGTH_SHORT).show()

                Log.d("MainActivity", "💾 Cliente guardado: $clientet")
            } else {
                // SIN CLIENTE: Eliminar y mostrar área de trabajo vacía

                binding.lyCuello.visibility = View.GONE
                binding.lyCuerpo.visibility = View.VISIBLE

                // Eliminar de SharedPreferences
                sharedPreferences.edit()
                    .remove("cliente")
                    .remove("tvpCliente")
                    .apply()

                Log.d("MainActivity", "🗑️ Cliente eliminado")
            }
        }
    }
    private fun inicializarClientes() {
        val database = ClienteDatabase.getDatabase(this)
        clienteRepository = ClienteRepository(database.clienteDao(), this)
        voiceSearchManager = VoiceSearchManager(this, clienteRepository)

        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val primeraVez = prefs.getBoolean("clientes_primera_vez", true)

        if (primeraVez) {
            SyncInicialClientesWorker.descargarClientesIniciales(this)
            prefs.edit().putBoolean("clientes_primera_vez", false).apply()
        } else {
            SyncClientesWorker.programarSincronizacionPeriodica(this)
        }
    }
    private fun buscarCliente(consulta: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resultados = clienteRepository.buscar(consulta)

                withContext(Dispatchers.Main) {
                    when (resultados.size) {
                        0 -> {
                            Toast.makeText(this@MainActivity, "❌ Cliente no encontrado", Toast.LENGTH_SHORT).show()
                        }
                        1 -> {
                            seleccionarCliente(resultados[0])

                            // Desactivar modo búsqueda si está activo
                            if (modoBusqueda) {
                                modoBusqueda = false
                                binding.proEditxt.backgroundTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.color)
                                binding.proEditxt.setText("")
                            }
                        }
                        else -> {
                            mostrarListaClientes(resultados)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error: ${e.message}")
            }
        }
    }
    private fun seleccionarCliente(cliente: Cliente) {
        clienteSeleccionado = cliente

        // Actualizar UI
        binding.clienteEditxt.setText(cliente.getTextoCompleto())
        binding.tvpCliente.text = "Presupuesto de ${cliente.nombreCompleto}"

        // Guardar en SharedPreferences
        sharedPreferences.edit()
            .putString("cliente", cliente.nombreCompleto)
            .putString("tvpCliente", "Presupuesto de ${cliente.nombreCompleto}")
            .apply()

        Toast.makeText(this, "✅ ${cliente.nombreCompleto}", Toast.LENGTH_SHORT).show()

        Log.d("MainActivity", "✅ Cliente seleccionado: ${cliente.nombreCompleto}")
    }
    private fun mostrarListaClientes(clientes: List<Cliente>) {
        val items = clientes.map { it.getTextoCompleto() }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Selecciona cliente (${clientes.size})")
            .setItems(items) { _, which ->
                seleccionarCliente(clientes[which])

                // Desactivar modo búsqueda si está activo
                if (modoBusqueda) {
                    modoBusqueda = false
                    binding.proEditxt.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color)
                    binding.proEditxt.setText("")
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    private fun manejarResultadoBusqueda(resultado: VoiceSearchManager.ResultadoBusqueda) {
        when (resultado) {
            is VoiceSearchManager.ResultadoBusqueda.ClienteUnico -> {
                seleccionarCliente(resultado.cliente)
            }

            is VoiceSearchManager.ResultadoBusqueda.ClientesMultiples -> {
                mostrarListaClientes(resultado.clientes)
            }

            VoiceSearchManager.ResultadoBusqueda.NoEncontrado -> {
                Toast.makeText(this, "❌ Cliente no encontrado", Toast.LENGTH_SHORT).show()
            }

            VoiceSearchManager.ResultadoBusqueda.Cancelado -> {
                // Usuario canceló
            }

            is VoiceSearchManager.ResultadoBusqueda.Producto -> {
                val nombreProducto = resultado.nombre
                Log.d("MainActivity", "🔍 Texto original: '$nombreProducto'")

                // ⭐ LIMPIAR palabra "producto" o "p" de manera robusta
                val nombreLimpio = limpiarPrefijo(nombreProducto)

                Log.d("MainActivity", "🔍 Texto limpio: '$nombreLimpio'")

                if (nombreLimpio.isEmpty()) {
                    Toast.makeText(this, "❌ Debes decir el nombre del producto", Toast.LENGTH_SHORT).show()
                    return
                }

                // Buscar en productos
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val resultados = productoRepository.buscar(nombreLimpio, 5)

                        Log.d("MainActivity", "📦 Resultados: ${resultados.size}")

                        withContext(Dispatchers.Main) {
                            when (resultados.size) {
                                0 -> {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "❌ Producto no encontrado: $nombreLimpio",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                1 -> {
                                    seleccionarProducto(resultados[0])
                                }
                                else -> {
                                    mostrarListaProductos(resultados)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error: ${e.message}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "❌ Error buscando producto",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
    private fun limpiarPrefijo(texto: String): String {
        val textoLimpio = texto.lowercase().trim()

        // Lista de prefijos a eliminar (del más largo al más corto)
        val prefijos = listOf("productos", "producto", "p")

        for (prefijo in prefijos) {
            // Verificar si empieza con el prefijo seguido de espacio
            if (textoLimpio.startsWith("$prefijo ")) {
                return texto.substring(prefijo.length).trim()
            }
        }

        // Si no empieza con ningún prefijo, retornar el texto original
        return texto.trim()
    }
// PRODUCTOS
    // ═══════════════════════════════════════════════════
    // ─── PRODUCTOS ───
    // inicializarProductos, buscarProducto,
    // seleccionarProducto, mostrarListaProductos,
    // manejarResultadoBusquedaProducto, configurarBusqueda
    // ═══════════════════════════════════════════════════
    private fun inicializarProductos() {
        val database = ProductoDatabase.getDatabase(this)
        productoRepository = ProductoRepository(database.productoDao(), this)
        productoVoiceSearchManager = ProductoVoiceSearchManager(this, productoRepository)

        SyncProductosWorker.programarSincronizacionPeriodica(this)

        Log.d("MainActivity", "✅ Productos inicializados")
    }
    // ==================== DICTADO DE MEDIDAS ====================
    // ═══════════════════════════════════════════════════
    // ─── DICTADO POR VOZ ───
    // inicializarDictadoMedidas, activarDictado,
    // desactivarDictado, procesarResultadoDictado
    // ═══════════════════════════════════════════════════
    private fun inicializarDictadoMedidas() {
        dictadoMedidas = DictadoMedidas()
        backgroundOriginalMed1 = binding.med1Lay.background

        binding.med1Lay.setOnLongClickListener {
            if (dictadoActivo) {
                desactivarDictado()
            } else {
                activarDictado()
            }
            true
        }
    }
    private fun activarDictado() {
        dictadoActivo = true
        binding.med1Lay.setBackgroundResource(R.drawable.bg_dictado_activo)
        dictadoMedidas?.lanzar(this)
    }
    private fun desactivarDictado() {
        dictadoActivo = false
        binding.med1Lay.background = backgroundOriginalMed1
    }
    private fun procesarResultadoDictado(resultado: DictadoMedidas.Resultado) {
        when (resultado) {
            is DictadoMedidas.Resultado.Exito -> {
                val m = resultado.medida
                binding.med1Editxt.setText(m.medida1.toString())
                binding.med2Editxt.setText(m.medida2.toString())
                binding.cantEditxt.setText(m.cantidad.toString())
                if (!m.producto.isNullOrBlank()) binding.proEditxt.setText(m.producto)
                if (binding.precioEditxt.text.isNullOrBlank()) binding.precioEditxt.setText("0")

                try {
                    agregarListado()
                    actualizar()
                    Toast.makeText(this, "${m.medida1} x ${m.medida2} = ${m.cantidad}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                // Relanzar diálogo para seguir dictando
                if (dictadoActivo) {
                    dictadoMedidas?.lanzar(this)
                }
            }

            is DictadoMedidas.Resultado.Incompleto -> {
                // No dijo "siguiente", reabrir diálogo acumulando texto
                Toast.makeText(this, "Acumulado: ${resultado.textoAcumulado}", Toast.LENGTH_SHORT).show()
                if (dictadoActivo) {
                    dictadoMedidas?.lanzar(this)
                }
            }

            is DictadoMedidas.Resultado.Cancelado -> {
                desactivarDictado()
                dictadoMedidas?.resetear()
            }
        }
    }
    // ==================== FIN DICTADO DE MEDIDAS ====================
    private fun buscarProducto(consulta: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resultados = productoRepository.buscar(consulta, 5)

                withContext(Dispatchers.Main) {
                    when (resultados.size) {
                        0 -> {
                            Toast.makeText(this@MainActivity, "❌ Producto no encontrado", Toast.LENGTH_SHORT).show()
                        }
                        1 -> {
                            seleccionarProducto(resultados[0])

                            // Desactivar modo búsqueda si está activo
                            if (modoBusqueda) {
                                modoBusqueda = false
                                binding.proEditxt.backgroundTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.color)
                            }
                        }
                        else -> {
                            mostrarListaProductos(resultados)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error: ${e.message}")
            }
        }
    }
    private fun seleccionarProducto(producto: Producto) {
        productoSeleccionado = producto

        binding.proEditxt.setText(producto.nombre)

        when {
            !producto.tieneStock() -> {
                Toast.makeText(this, "⚠️ ${producto.nombre} - Sin stock", Toast.LENGTH_LONG).show()
            }
            producto.necesitaReabastecimiento() -> {
                Toast.makeText(this, "⚠️ ${producto.nombre} - Stock bajo: ${producto.stock}", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "✅ ${producto.nombre} - Stock: ${producto.stock}", Toast.LENGTH_SHORT).show()
            }
        }

        Log.d("MainActivity", "✅ Producto: ${producto.nombre} (Stock: ${producto.stock})")
    }
    private fun mostrarListaProductos(productos: List<Producto>) {
        val items = productos.map {
            "${it.nombre} - Stock: ${it.stock} - S/ ${it.precioVenta}"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Selecciona producto (${productos.size})")
            .setItems(items) { _, which ->
                seleccionarProducto(productos[which])

                // Desactivar modo búsqueda si está activo
                if (modoBusqueda) {
                    modoBusqueda = false
                    binding.proEditxt.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    private fun manejarResultadoBusquedaProducto(resultado: ProductoVoiceSearchManager.ResultadoBusqueda) {
        when (resultado) {
            is ProductoVoiceSearchManager.ResultadoBusqueda.ProductoUnico -> {
                seleccionarProducto(resultado.producto)
            }

            is ProductoVoiceSearchManager.ResultadoBusqueda.ProductosMultiples -> {
                mostrarListaProductos(resultado.productos)
            }

            ProductoVoiceSearchManager.ResultadoBusqueda.NoEncontrado -> {
                Toast.makeText(this, "❌ Producto no encontrado", Toast.LENGTH_SHORT).show()
            }

            ProductoVoiceSearchManager.ResultadoBusqueda.Cancelado -> {
                // Usuario canceló
            }
        }
    }
// BÚSQUEDA POR TEXTO (txBusqueda)
    private fun configurarBusqueda() {
        // Botón txBusqueda
        binding.txBusqueda.setOnClickListener {
            if (modoBusqueda) {
                // Desactivar modo búsqueda
                modoBusqueda = false
                binding.proEditxt.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color)
                binding.proEditxt.setText("")

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(binding.proEditxt.windowToken, 0)
            } else {
                // Activar modo búsqueda
                modoBusqueda = true
                binding.proEditxt.backgroundTintList = ContextCompat.getColorStateList(this, R.color.rojo)
                binding.proEditxt.setText("")
                binding.proEditxt.requestFocus()

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.showSoftInput(binding.proEditxt, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }
        }

        // TextWatcher para búsqueda por texto
        binding.proEditxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!modoBusqueda) return

                val texto = s?.toString()?.trim() ?: return
                if (texto.length < 3) return

                val palabras = texto.split(" ")
                val primeraPalabra = palabras[0].lowercase()

                when {
                    primeraPalabra == "cliente" || primeraPalabra == "c" -> {
                        val consulta = texto.removePrefix("cliente").removePrefix("c").trim()
                        if (consulta.length >= 2) {
                            buscarCliente(consulta)
                        }
                    }
                    primeraPalabra == "producto" || primeraPalabra == "p" -> {
                        val consulta = texto.removePrefix("producto").removePrefix("p").trim()
                        if (consulta.length >= 2) {
                            buscarProducto(consulta)
                        }
                    }
                }
            }
        })
    }
    // ═══════════════════════════════════════════════════
    // ─── ELIMINACIÓN Y EDICIÓN DE ITEMS ───
    // eliminar, filtrarLista
    // ═══════════════════════════════════════════════════
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "WrongViewCast", "MissingInflatedId")
    private fun eliminar() {
        binding.list.setOnItemClickListener { parent, view, position, id ->
            try {
                val dialogo = AlertDialog.Builder(this)
                val modelo = layoutInflater.inflate(R.layout.dialogo, null)
                val eliminar = modelo.findViewById<Button>(R.id.btn_dialogo_eliminar)
                val editar = modelo.findViewById<Button>(R.id.btn_dialogo_editar)
                val irlista = modelo.findViewById<Button>(R.id.btnGuardar)
                val abrir = modelo.findViewById<Button>(R.id.btAbrir)
                val enviar = modelo.findViewById<Button>(R.id.btnEnviarJson)
                val datos = modelo.findViewById<TextView>(R.id.tvEscala)
                val pro = modelo.findViewById<TextView>(R.id.etdProducto)
                datos.text = "${lista[position].producto} ${lista[position].escala} en ${lista[position].uni}"

                enviar.setOnClickListener {
                    enviarPresupuestoPorChat()
                }
                abrir.visibility = View.VISIBLE
                dialogo.setView(modelo)
                val dialogoPer = dialogo.create()
                dialogoPer.show()

                eliminar.setOnClickListener {
                    lista.removeAt(position)
                    actualizar()
                    dialogoPer.dismiss()
                }

                editar.setOnClickListener {
                    val otro = modelo.findViewById<View>(R.id.lyEdit)
                    val btnDiaOk = modelo.findViewById<View>(R.id.btnDiaOk)
                    val m1 = modelo.findViewById<TextView>(R.id.etdMed1)
                    val m2 = modelo.findViewById<TextView>(R.id.etdMed2)
                    val m3 = modelo.findViewById<TextView>(R.id.etdMed3)
                    val ca = modelo.findViewById<TextView>(R.id.etdCant)
                    val pre = modelo.findViewById<TextView>(R.id.etdPrecio)

                    otro.visibility = View.VISIBLE
                    irlista.visibility = View.GONE
                    eliminar.visibility = View.INVISIBLE
                    editar.visibility = View.INVISIBLE
                    btnDiaOk.visibility = View.VISIBLE

                    m1.text = df1(lista[position].medi1)
                    m2.text = df1(lista[position].medi2)
                    m3.text = df1(lista[position].medi3)
                    ca.text = df1(lista[position].canti)
                    pre.text = df1(lista[position].precio)
                    pro.text = lista[position].producto

                    val lyMed1 = modelo.findViewById<View>(R.id.lyMed1)
                    val lyMed2 = modelo.findViewById<View>(R.id.lyMed2)
                    val lyMed3 = modelo.findViewById<View>(R.id.lyMed3)

                    val escala = lista[position].escala

                    when (escala) {
                        "p2" -> {
                            lyMed3.visibility = View.GONE
                            lyMed1.visibility = View.VISIBLE
                            lyMed2.visibility = View.VISIBLE
                        }
                        "m2" -> {
                            lyMed3.visibility = View.GONE
                            lyMed1.visibility = View.VISIBLE
                            lyMed2.visibility = View.VISIBLE
                        }
                        "ml" -> {
                            lyMed1.visibility = View.VISIBLE
                            lyMed2.visibility = View.GONE
                            lyMed3.visibility = View.GONE
                        }
                        "m3" -> {
                            lyMed1.visibility = View.VISIBLE
                            lyMed2.visibility = View.VISIBLE
                            lyMed3.visibility = View.VISIBLE
                        }
                        "uni" -> {
                            lyMed1.visibility = View.GONE
                            lyMed2.visibility = View.GONE
                            lyMed3.visibility = View.GONE
                            eliminar.visibility=View.GONE
                        }
                    }

                    btnDiaOk.setOnClickListener {
                        try {
                            val medi1 = m1.text.toString().toFloat()
                            val medi2 = m2.text.toString().toFloat()
                            val medi3 = m3.text.toString().toFloat()
                            val cantidad = ca.text.toString().toFloat()
                            val precio = pre.text.toString().toFloat()
                            var pi = 0f
                            var co = 0f

                            when (escala) {
                                "p2" -> {
                                    pi = pies(medi1, medi2) * cantidad
                                    co = pi * precio
                                }
                                "m2" -> {
                                    pi = metroCua(medi1, medi2) * cantidad
                                    co = pi * precio
                                }
                                "ml" -> {
                                    pi = mLineales(medi1, medi2) * cantidad
                                    co = pi * precio
                                }
                                "m3" -> {
                                    pi = mCubicos(medi1, medi2, medi3) * cantidad
                                    co = pi * precio
                                }
                                "uni" -> {
                                    pi = cantidad
                                    co = pi * precio
                                }
                            }

                            lista[position].medi1 = medi1
                            lista[position].medi2 = medi2
                            lista[position].medi3 = medi3
                            lista[position].canti = cantidad
                            lista[position].precio = precio
                            lista[position].producto = pro.text.toString()
                            lista[position].piescua = pi
                            lista[position].metcua = pi
                            lista[position].metli = pi
                            lista[position].metcub = pi
                            lista[position].costo = co

                            actualizar()
                            dialogoPer.dismiss()
                            Toast.makeText(this, "Se editó correctamente", Toast.LENGTH_SHORT).show()
                        } catch (e: NumberFormatException) {
                            Toast.makeText(this, "Error: Ingresa valores numéricos válidos", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Cuando ya se tiene un URI seleccionado:
                    val uriCompleto = lista[position].uri
                    val uriAcortado = if (uriCompleto.length > 20) "...${uriCompleto.takeLast(20)}" else uriCompleto
                    usoImagenUri(uriAcortado)
                }

                irlista.setOnClickListener {
                    guardar()
                    dialogoPer.dismiss()
                }
                abrir.setOnClickListener {
                    selectedPosition = position // Guardar la posición seleccionada
                    openGallery()
                    dialogoPer.dismiss()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error al mostrar el diálogo", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun filtrarLista(criterio: String) {
        val listaFiltrada = lista.filter { item ->
            item.producto.contains(criterio, ignoreCase = true)
        }

        val adaptador = ArrayAdapter(this, R.layout.lista_cal, listaFiltrada.map { it.toString() })
        binding.list.adapter = adaptador
    }
    //shared
    // ═══════════════════════════════════════════════════
    // ─── PERSISTENCIA LOCAL ───
    // guardarDatos, cargarDatosGuardados, guardar, abrir
    // ═══════════════════════════════════════════════════
    private fun guardarDatos() {
        // Obtener los datos que deseas guardar
        val dato1 = binding.med1Editxt.text.toString()
        val dato2 = binding.med2Editxt.text.toString()
        val dato3 = binding.med3Editxt.text.toString()
        val cantidad = binding.cantEditxt.text.toString()
        val precio = binding.precioEditxt.text.toString()
        val producto = binding.proEditxt.text.toString()
        val cliente = binding.clienteEditxt.text.toString()

        // Guardar los datos en SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("dato1", dato1)
        editor.putString("dato2", dato2)
        editor.putString("dato3", dato3)
        editor.putString("cantidad", cantidad)
        editor.putString("precio", precio)
        editor.putString("producto", producto)
        editor.putString("cliente",cliente)
        editor.putString("rol_dispositivo", rolDispositivo)
        editor.putBoolean("es_patron", esPatron)
        editor.putString("nombre_vendedor", nombreVendedor)

        // Convertir la lista en una cadena JSON
        val gson = Gson()
        val listaString = gson.toJson(lista)
        editor.putString("lista", listaString)

        editor.apply()
    }
    private fun cargarDatosGuardados(): String {
        // Obtener los datos guardados desde SharedPreferences
        val dato1 = sharedPreferences.getString("dato1", "")
        val dato2 = sharedPreferences.getString("dato2", "")
        val dato3 = sharedPreferences.getString("dato3", "")
        val cantidad = sharedPreferences.getString("cantidad", "")
        val precio = sharedPreferences.getString("precio", "")
        val producto = sharedPreferences.getString("producto", "")
        val cliente = sharedPreferences.getString("cliente", "")

        // Actualizar los campos de la interfaz con los datos cargados (excepto clienteEditxt)
        binding.med1Editxt.setText(dato1)
        binding.med2Editxt.setText(dato2)
        binding.med3Editxt.setText(dato3)
        binding.cantEditxt.setText(cantidad)
        binding.precioEditxt.setText(precio)
        binding.proEditxt.setText(producto)
        // **Eliminar o comentar la siguiente línea para evitar sobrescribir clienteEditxt**
        // binding.clienteEditxt.setText(cliente)

        // Cargar la lista desde SharedPreferences
        val listaString = sharedPreferences.getString("lista", null)
        if (!listaString.isNullOrEmpty()) {
            val gson = Gson()
            val tipoLista = object : TypeToken<List<Listado>>() {}.type
            lista = gson.fromJson(listaString, tipoLista)
        }
        return cliente.toString()
    }
    // guardar completo
    @RequiresApi(Build.VERSION_CODES.O)
    private fun guardar() {
        val cliente = binding.clienteEditxt.text
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        val currentDateAndTime = sdf.format(Date())

        val nombreArchivo = "Presupuesto ->($cliente) ${currentDateAndTime}.dat"
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput(nombreArchivo, Context.MODE_PRIVATE)
            ObjectOutputStream(fileOutputStream).use { it.writeObject(lista) }
            Toast.makeText(this, "Archivo guardado con éxito", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar archivo", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    private fun abrir() {
        val paquete = intent.extras
        val li = paquete?.getSerializable("lista") as? List<*>
        val listaRecibida = li?.filterIsInstance<Listado>()?.toMutableList() ?: mutableListOf()

        if (lista.isNotEmpty()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmación")
            builder.setMessage("La lista actual no está vacía. ¿Deseas sumar los datos guardados a la lista existente?")
            builder.setPositiveButton("Sumar") { _, _ ->
                lista.addAll(listaRecibida)
                actualizar()
            }
            builder.setNegativeButton("Reemplazar") { _, _ ->
                lista.clear()
                lista.addAll(listaRecibida)
                actualizar()
            }
            builder.setNeutralButton("Cancelar", null)
            builder.show()
        } else {
            lista.addAll(listaRecibida)
            actualizar()
        }
    }
    //FUNCIONES DE DICTADO
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido. Puedes iniciar el dictado.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permiso denegado.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Dictado de medidas
        dictadoMedidas?.procesarResultado(requestCode, resultCode, data)?.let {
            procesarResultadoDictado(it)
            return
        }

        if (voiceSearchManager.procesarResultadoVoz(requestCode, resultCode, data)) {
            return
        }
        //Búsqueda por voz de PRODUCTOS
        if (requestCode == ProductoVoiceSearchManager.REQUEST_CODE_SPEECH_PRODUCTO) {
            if (resultCode == RESULT_OK && data != null) {
                lifecycleScope.launch {
                    productoVoiceSearchManager.procesarResultadoVoz(
                        requestCode,
                        resultCode,
                        data
                    ) { resultado ->
                        manejarResultadoBusquedaProducto(resultado)
                    }
                }
            }
            return
        }

        try {
            when (requestCode) {
                // ✅ MANTENER: Captura de imagen (tu código existente)
                REQUEST_IMAGE_CAPTURE -> {
                    if (resultCode == RESULT_OK) {
                        data?.extras?.get("data") as Bitmap
                    } else {
                        Toast.makeText(this, "Error al capturar la imagen", Toast.LENGTH_SHORT).show()
                    }
                }

                // ✅ MANTENER: Selección de galería (tu código existente)
                REQUEST_IMAGE_GALLERY -> {
                    if (resultCode == RESULT_OK && data != null) {
                        val selectedImageUri: Uri? = data.data

                        if (selectedImageUri != null) {
                            val imageUriString = selectedImageUri.toString()

                            // Guardar el URI completo
                            val uriCompleto = imageUriString
                            val uriAcortado = if (uriCompleto.length > 20) "...${uriCompleto.takeLast(20)}" else uriCompleto

                            // Guardar el URI completo en el objeto Listado
                            lista[selectedPosition].uri = uriCompleto

                            // Mostrar la versión acortada en la interfaz
                            usoImagenUri(uriAcortado)

                            // Cargar la imagen utilizando Glide en ivScan
                            Glide.with(this)
                                .load(selectedImageUri)

                        } else {
                            Toast.makeText(this, "Error: URI de imagen es nulo", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Error al seleccionar la imagen", Toast.LENGTH_SHORT).show()
                    }
                }

                // ✅ NUEVO: OCR - Solo agregar este caso
                CODIGO_SOLICITUD_OCR -> {
                    if (resultCode == RESULT_OK && data != null) {
                        @Suppress("UNCHECKED_CAST")
                        val elementosOcr = data.getSerializableExtra("elementos_ocr") as? ArrayList<Listado>

                        elementosOcr?.let { elementos ->
                            if (elementos.isNotEmpty()) {
                                // Agregar elementos a la lista principal
                                lista.addAll(elementos)

                                // Actualizar interfaz
                                actualizar()

                                // Mostrar confirmación
                                val mensaje = if (elementos.size == 1) {
                                    "✅ 1 elemento agregado desde imagen"
                                } else {
                                    "✅ ${elementos.size} elementos agregados desde imagen"
                                }

                                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

                                // Log para depuración
                                Log.d("OCR", "Elementos recibidos: ${elementos.size}")
                                elementos.forEachIndexed { indice, elemento ->
                                    Log.d("OCR", "Elemento $indice: ${elemento.medi1} x ${elemento.medi2} = ${elemento.canti}, ${elemento.producto}")
                                }
                            } else {
                                Toast.makeText(this, "No se encontraron elementos en la imagen", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "OCR cancelado", Toast.LENGTH_SHORT).show()
                    }
                }

                // ✅ MANTENER: Dictado (tu código existente)
                DICTADO_REQUEST_CODE -> {
                    if (resultCode == RESULT_OK && data != null) {
                        @Suppress("UNCHECKED_CAST")
                        val elementosDictados = data.getSerializableExtra("elementos_dictados") as? ArrayList<Listado>

                        elementosDictados?.let { elementos ->
                            lista.addAll(elementos)
                            actualizar()

                            val mensaje = if (elementos.size == 1) {
                                "✅ 1 elemento agregado por dictado"
                            } else {
                                "✅ ${elementos.size} elementos agregados por dictado"
                            }

                            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

                            Log.d("DICTADO", "Elementos recibidos: ${elementos.size}")
                            elementos.forEachIndexed { index, elemento ->
                                Log.d("DICTADO", "Elemento $index: ${elemento.medi1} x ${elemento.medi2} = ${elemento.canti}, ${elemento.producto}")
                            }
                        }
                    } else if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "Dictado cancelado", Toast.LENGTH_SHORT).show()
                    }
                }

                // ✅ MANTENER: Reconocimiento de voz anterior (tu código existente)
                RECORD_REQUEST_CODE -> {
                    if (resultCode == RESULT_OK && data != null) {
                        val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        binding.txMetroTot.text = result?.get(0)
                    }
                }

                // ✅ MANTENER: Presupuestos (tu código existente)
                RECEIVE_PRESUPUESTO_REQUEST -> {
                    if (resultCode == RESULT_OK && data != null) {
                        data.data?.let { uri ->
                            manejarArchivoPresupuesto(uri)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al procesar la solicitud: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    //FUNCIONES SCAN
    @SuppressLint("IntentReset")
    // ═══════════════════════════════════════════════════
    // ─── CÁMARA / GALERÍA ───
    // openGallery, usoImagenUri
    // ═══════════════════════════════════════════════════
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }
    private fun usoImagenUri(imageUriString: String) {
        if (selectedPosition != -1) {
            // Solo actualizar la interfaz con el URI acortado
            // No modifiques lista[selectedPosition].uriCompleto
            // Simplemente actualiza el texto o la representación acortada en la interfaz
            actualizar()
        }
        Log.d("Image URI", imageUriString)
    }
    // ─── GENERACIÓN DE PDF (delegado a PdfGenerator) ───
    private val pdfGenerator by lazy { crystal.crystal.pdf.PdfGenerator(this) }

    private fun openPdf() {
        val cliente = binding.clienteEditxt.text.toString()
        pdfGenerator.generarYCompartir(cliente, lista, binding.precioTotal.text.toString())
    }
    // FUNCIONES PARA ENVIAR Y ABRIR PERUSPUESTOS
    @SuppressLint("NewApi")
    // ═══════════════════════════════════════════════════
    // ─── PRESUPUESTOS ───
    // manejarPresupuestoRecibido, mostrarMenuPresupuesto,
    // guardarComoJSON, compartirPresupuesto,
    // cargarPresupuestoDesdeJson, mostrarOpcionesCargar,
    // cargarPresupuestoDirecto, enviarPresupuestoPorChat,
    // manejarArchivoPresupuesto, abrirSelectorPresupuesto
    // ═══════════════════════════════════════════════════
    private fun manejarPresupuestoRecibido() {
        importadorMedidas.manejarMensajeMedidas()
        // Manejar contenido JSON directo (nuevo método)
        val jsonContent = intent.getStringExtra("cargar_presupuesto_json")
        val nombreArchivo = intent.getStringExtra("cargar_presupuesto_nombre")

        if (jsonContent != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Presupuesto Recibido")
            builder.setMessage("Has recibido el presupuesto: $nombreArchivo\n¿Deseas cargarlo?")
            builder.setPositiveButton("Cargar") { _, _ ->
                cargarPresupuestoDesdeJson(jsonContent)
            }
            builder.setNegativeButton("Cancelar", null)
            builder.show()
            return
        }

        // Método anterior para URIs locales (mantener por compatibilidad)
        val presupuestoUri = intent.getStringExtra("cargar_presupuesto_uri")
        val nombreArchivoUri = intent.getStringExtra("cargar_presupuesto_nombre")

        if (presupuestoUri != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Presupuesto Recibido")
            builder.setMessage("Has recibido el presupuesto: $nombreArchivoUri\n¿Deseas cargarlo?")
            builder.setPositiveButton("Cargar") { _, _ ->
                val uri = Uri.parse(presupuestoUri)
                manejarArchivoPresupuesto(uri)
            }
            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }
    }
    override fun onNewIntent(intent: Intent) {
        intent.let { super.onNewIntent(it) }
        setIntent(intent)
        manejarPresupuestoRecibido()
    }
    private fun mostrarMenuPresupuesto() {
        val opciones = if (lista.isEmpty()) {
            arrayOf("Cargar presupuesto desde archivo")
        } else {
            arrayOf(
                "Enviar por chat",
                "🔧 Edición masiva",  // ✅ AGREGAR ESTA LÍNEA
                "Cargar presupuesto desde archivo",
                "Guardar como archivo JSON",
                "Compartir como archivo"
            )
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Opciones de Presupuesto")
        builder.setItems(opciones) { _, which ->
            Log.d("DEBUG", "Opción menú principal: $which")
            when (which) {
                0 -> if (lista.isEmpty()) {
                    abrirSelectorPresupuesto()
                } else {
                    enviarPresupuestoPorChat()
                }
                1 -> {
                    Log.d("DEBUG", "Llamando a mostrarMenuEdicionMasiva()")
                    edicionMasivaManager.mostrarMenuEdicionMasiva()
                }
                2 -> abrirSelectorPresupuesto()
                3 -> if (lista.isNotEmpty()) guardarComoJSON()
                4 -> if (lista.isNotEmpty()) compartirPresupuesto()
            }
        }

        builder.show()
    }
    private fun guardarComoJSON() {
        val cliente = binding.clienteEditxt.text.toString().takeIf { it.isNotEmpty() } ?: "Sin nombre"
        val presupuesto = PresupuestoCompleto(
            cliente = cliente,
            fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            elementos = lista.toList(),
            precioTotal = binding.precioTotal.text.toString(),
            metrosTotal = binding.metrosTotal.text.toString(),
            piesTotal = binding.piesTotal.text.toString(),
            perimetroTotal = binding.per.text.toString()
        )

        val gson = Gson()
        val jsonString = gson.toJson(presupuesto)

        val fileName = "presupuesto_${cliente}_${System.currentTimeMillis()}.json"

        // ✅ CREAR ESTRUCTURA DE CARPETAS:
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val crystalDir = File(downloadsDir, "Crystal")
        val presupuestosDir = File(crystalDir, "PresupuestosJ")

        // Crear las carpetas si no existen
        if (!presupuestosDir.exists()) {
            presupuestosDir.mkdirs() // Crea toda la estructura de carpetas
        }

        // Archivo final en la carpeta correcta
        val file = File(presupuestosDir, fileName)

        try {
            file.writeText(jsonString)
            Toast.makeText(this, "✅ Presupuesto guardado en:\nDescargas/Crystal/PresupuestosJ/\n$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun compartirPresupuesto() {
        val cliente = binding.clienteEditxt.text.toString().takeIf { it.isNotEmpty() } ?: "Sin nombre"
        val presupuesto = PresupuestoCompleto(
            cliente = cliente,
            fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            elementos = lista.toList(),
            precioTotal = binding.precioTotal.text.toString(),
            metrosTotal = binding.metrosTotal.text.toString(),
            piesTotal = binding.piesTotal.text.toString(),
            perimetroTotal = binding.per.text.toString()
        )

        val gson = Gson()
        val jsonString = gson.toJson(presupuesto)

        val fileName = "presupuesto_${cliente}_${System.currentTimeMillis()}.json"
        val file = File(cacheDir, fileName)

        try {
            file.writeText(jsonString)
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Presupuesto - $cliente")
                putExtra(Intent.EXTRA_TEXT, "Presupuesto generado desde Crystal App")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Compartir presupuesto"))

        } catch (e: Exception) {
            Toast.makeText(this, "Error al compartir: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun cargarPresupuestoDesdeJson(jsonString: String): Boolean {
        return try {
            val gson = Gson()
            val presupuesto = gson.fromJson(jsonString, PresupuestoCompleto::class.java)

            // Validar que el presupuesto sea válido
            if (presupuesto.elementos.isEmpty()) {
                Toast.makeText(this, "El presupuesto no contiene elementos válidos", Toast.LENGTH_SHORT).show()
                return false
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("📋 Cargar Presupuesto")
            builder.setMessage(
                "👤 Cliente: ${presupuesto.cliente}\n" +
                        "📅 Fecha: ${presupuesto.fechaCreacion}\n" +
                        "📦 Elementos: ${presupuesto.elementos.size}\n" +
                        "💰 Total: S/${presupuesto.precioTotal}\n" +
                        "📐 Metros²: ${presupuesto.metrosTotal}\n" +
                        "📏 Pies²: ${presupuesto.piesTotal}\n\n" +
                        "¿Deseas cargar este presupuesto?"
            )

            builder.setPositiveButton("✅ Cargar") { _, _ ->
                if (lista.isNotEmpty()) {
                    mostrarOpcionesCargar(presupuesto)
                } else {
                    cargarPresupuestoDirecto(presupuesto)
                }
            }

            builder.setNegativeButton("❌ Cancelar", null)
            builder.show()

            true
        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error al leer presupuesto: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }
    @SuppressLint("NewApi", "SetTextI18n")
    private fun mostrarOpcionesCargar(presupuesto: PresupuestoCompleto) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("⚙️ ¿Cómo cargar?")
        builder.setMessage("Ya tienes elementos en tu presupuesto actual.")

        builder.setPositiveButton("➕ Sumar") { _, _ ->
            lista.addAll(presupuesto.elementos)
            val clienteActual = binding.clienteEditxt.text.toString()
            val nuevoCliente = if (clienteActual.isNotEmpty()) {
                "$clienteActual + ${presupuesto.cliente}"
            } else {
                presupuesto.cliente
            }
            binding.clienteEditxt.setText(nuevoCliente)
            binding.tvpCliente.text = "Presupuesto de $nuevoCliente"

            // ⭐ AGREGAR ESTAS LÍNEAS (después de línea 1931):
            sharedPreferences.edit()
                .putString("cliente", nuevoCliente)
                .putString("tvpCliente", "Presupuesto de $nuevoCliente")
                .apply()

            // Ajustar visibilidad
            binding.lyCuello.visibility = View.GONE
            binding.lyCuerpo.visibility = View.VISIBLE

            actualizar()
            Toast.makeText(this, "✅ Presupuesto sumado correctamente", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("🔄 Reemplazar") { _, _ ->
            cargarPresupuestoDirecto(presupuesto)
        }

        builder.setNeutralButton("❌ Cancelar", null)
        builder.show()
    }
    @SuppressLint("NewApi", "SetTextI18n")
    private fun cargarPresupuestoDirecto(presupuesto: PresupuestoCompleto) {
        lista.clear()
        lista.addAll(presupuesto.elementos)
        binding.clienteEditxt.setText(presupuesto.cliente)
        binding.tvpCliente.text = "Presupuesto de ${presupuesto.cliente}"

        // ⭐ AGREGAR ESTAS LÍNEAS (después de línea 1948):
        sharedPreferences.edit()
            .putString("cliente", presupuesto.cliente)
            .putString("tvpCliente", "Presupuesto de ${presupuesto.cliente}")
            .apply()

        // Ajustar visibilidad
        binding.lyCuello.visibility = View.GONE
        binding.lyCuerpo.visibility = View.VISIBLE

        actualizar()
        Toast.makeText(this, "✅ Presupuesto cargado correctamente", Toast.LENGTH_SHORT).show()
    }
    private fun enviarPresupuestoPorChat() {
        if (lista.isEmpty()) {
            Toast.makeText(this, "No hay elementos en el presupuesto para enviar", Toast.LENGTH_SHORT).show()
            return
        }

        val cliente = binding.clienteEditxt.text.toString().takeIf { it.isNotEmpty() } ?: "Sin nombre"

        // Crear objeto completo del presupuesto
        val presupuesto = PresupuestoCompleto(
            cliente = cliente,
            fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            elementos = lista.toList(),
            precioTotal = binding.precioTotal.text.toString(),
            metrosTotal = binding.metrosTotal.text.toString(),
            piesTotal = binding.piesTotal.text.toString(),
            perimetroTotal = binding.per.text.toString()
        )

        // Serializar a JSON
        val gson = Gson()
        val jsonPresupuesto = gson.toJson(presupuesto)

        // Crear archivo temporal
        val fileName = "presupuesto_${cliente}_${System.currentTimeMillis()}.json"
        val file = File(cacheDir, fileName)

        try {
            file.writeText(jsonPresupuesto)
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )

            // Abrir ListChatActivity con el archivo
            val intent = Intent(this, ListChatActivity::class.java)
            intent.putExtra("usuario", currentUserId)
            intent.putExtra("enviar_presupuesto", uri.toString())
            intent.putExtra("nombre_presupuesto", fileName)
            startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(this, "Error al preparar presupuesto: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun manejarArchivoPresupuesto(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val jsonString = inputStream?.bufferedReader().use { it?.readText() }

            if (jsonString != null) {
                cargarPresupuestoDesdeJson(jsonString)
            } else {
                Toast.makeText(this, "No se pudo leer el archivo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al abrir archivo: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun abrirSelectorPresupuesto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            // ✅ AGREGAR ESTO para empezar en la carpeta correcta:
            putExtra("android.provider.extra.INITIAL_URI",
                Uri.parse("content://com.android.externalstorage.documents/document/primary%3ADownload%2FCrystal%2FPresupuestosJ"))
        }
        startActivityForResult(intent, RECEIVE_PRESUPUESTO_REQUEST)
    }
    //FUNCIONES DE CHAT
    // ═══════════════════════════════════════════════════
    // ─── CHAT / MENSAJES NO LEÍDOS ───
    // setupUnreadMessagesListener, actualizarBadgeChat
    // ═══════════════════════════════════════════════════
    private fun setupUnreadMessagesListener() {
        // Escuchar los chats de la colección personal del usuario
        val userChatsRef = db.collection("usuarios").document(currentUserId).collection("chats")
        userChatsRef.addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                // Para cada chat, establecer listener a la subcolección de mensajes para contar los no leídos
                for (chatDoc in snapshot.documents) {
                    val chatId = chatDoc.id
                    val chatRef = db.collection("chats").document(chatId)
                    chatRef.collection("messages")
                        .whereEqualTo("leido", false)
                        .whereNotEqualTo("from", currentUserId)
                        .addSnapshotListener { msgsSnap, _ ->
                            if (msgsSnap != null) {
                                val count = msgsSnap.size()
                                unreadCountByChat[chatId] = count
                                actualizarBadgeChat()
                            }
                        }
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun actualizarBadgeChat() {
        val totalUnread = unreadCountByChat.values.sum()
        if (totalUnread > 0) {
            // Cambiar el ícono a la versión roja y mostrar el badge
            binding.btnChat.setImageResource(R.drawable.ic_mensajesno) // Reemplaza por tu recurso de ícono rojo
            binding.tvBadge.text = totalUnread.toString()
            binding.tvBadge.visibility = View.VISIBLE
        } else {
            binding.btnChat.setImageResource(R.drawable.ic_mensajes) // Versión blanca por defecto
            binding.tvBadge.visibility = View.GONE
        }
    }
    //FUNCIONES GENERALES
    // ═══════════════════════════════════════════════════
    // ─── UTILIDADES DE FORMATO Y CONVERSIÓN ───
    // df1, df2, conver, focusMed1,
    // configurarRetrocesoEditTexts, retasoEnUnidad,
    // limpiarPrefijo
    // ═══════════════════════════════════════════════════
    private fun df1(defo: Float): String {
        val resultado =if ("$defo".endsWith(".0")) {"$defo".replace(".0", "")}
        else { "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }
    private fun df2(defo: Float): String {
        return "%.2f".format(defo).replace(".", ",")
    }
    // ═══════════════════════════════════════════════════
    // ─── SPINNERS DE UNIDADES ───
    // uni1, uni2
    // ═══════════════════════════════════════════════════
    private fun uni1() {
        usados = findViewById(R.id.spinner_usa)
        val listaUsados = arrayOf("p2", "m2", "ml", "m3", "uni")
        val colores = arrayOf(R.color.color, R.color.fucsia, R.color.verde, R.color.violeta, R.color.naranja)
        val adaptadorU = object : ArrayAdapter<String>(
            this,
            R.layout.lista_spinner, listaUsados) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(getColor(position))
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(getColor(position))
                return view
            }

            fun getColor(position: Int): Int {
                return ContextCompat.getColor(this@MainActivity, colores[position])
            }
        }
        usados.adapter = adaptadorU
        usados.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int, p3: Long) {
                binding.usTxt.text = usados.selectedItem.toString()
                colorSeleccionado = adaptadorU.getColor(position)
                when (binding.usTxt.text) {
                    "p2" -> {
                        binding.med3Lay.visibility = View.GONE
                        binding.med2Lay.visibility = View.VISIBLE
                        binding.med1Lay.visibility = View.VISIBLE
                        binding.spinnerUni.visibility = View.VISIBLE
                        binding.med1Editxt.setText("")
                        binding.med2Editxt.setText("")
                        focusMed1() }
                    "m2" -> {
                        binding.med3Lay.visibility = View.GONE
                        binding.med2Lay.visibility = View.VISIBLE
                        binding.med1Lay.visibility = View.VISIBLE
                        binding.spinnerUni.visibility = View.VISIBLE
                        binding.med1Editxt.setText("")
                        binding.med2Editxt.setText("")
                        focusMed1() }
                    "ml" -> {
                        binding.med3Lay.visibility = View.GONE
                        binding.med2Lay.visibility = View.GONE
                        binding.med1Lay.visibility = View.VISIBLE
                        binding.spinnerUni.visibility = View.VISIBLE
                        binding.med1Editxt.setText("")
                        binding.med2Editxt.setText("1")
                        focusMed1() }
                    "m3" -> {
                        binding.med3Lay.visibility = View.VISIBLE
                        binding.med2Lay.visibility = View.VISIBLE
                        binding.med1Lay.visibility = View.VISIBLE
                        binding.spinnerUni.visibility = View.VISIBLE
                        binding.med1Editxt.setText("")
                        binding.med2Editxt.setText("")
                        focusMed1() }
                    "uni" -> {
                        binding.med3Lay.visibility = View.GONE
                        binding.med2Lay.visibility = View.GONE
                        binding.med1Lay.visibility = View.GONE
                        binding.spinnerUni.visibility = View.GONE
                        binding.med1Editxt.setText("1")
                        binding.med2Editxt.setText("1")
                        binding.cantEditxt.requestFocus() }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.usTxt.text = listaUsados[0]
            }
        }
    }
    private fun uni2(){
        unidades = findViewById(R.id.spinner_uni)
        val listaUnidades = arrayOf("Centímetros", "Metros", "Milímetros", "Pulgadas")
        val adaptador: ArrayAdapter<String> = ArrayAdapter(
            this, R.layout.lista_spinner, listaUnidades)
        unidades.adapter = adaptador
        unidades.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int,
                p3: Long) { binding.prTxt.text = unidades.selectedItem.toString()
                if (unidades.selectedItem.toString() == "Pulgadas") {
                    binding.med1Editxt.isFocusable = false
                    binding.med1Editxt.isFocusableInTouchMode = false
                    binding.med2Editxt.isFocusable = false
                    binding.med2Editxt.isFocusableInTouchMode = false
                    binding.med1Editxt.setOnClickListener {
                        DialogoPulgadas(this@MainActivity) { valor ->
                            binding.med1Editxt.setText(valor.toString())
                            binding.med2Editxt.performClick()
                        }.mostrar()
                    }
                    binding.med2Editxt.setOnClickListener {
                        DialogoPulgadas(this@MainActivity) { valor ->
                            binding.med2Editxt.setText(valor.toString())
                        }.mostrar()
                    }
                } else {
                    binding.med1Editxt.isFocusable = true
                    binding.med1Editxt.isFocusableInTouchMode = true
                    binding.med2Editxt.isFocusable = true
                    binding.med2Editxt.isFocusableInTouchMode = true
                    binding.med1Editxt.setOnClickListener(null)
                    binding.med2Editxt.setOnClickListener(null)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.prTxt.text = listaUnidades[0] }
        }
    }
    private fun conver(med: Float?): Float {
        if (med == null || med.isNaN()) {
            return 1f
        }
        return when (binding.prTxt.text) {
            "Centímetros" -> med / 100
            "Metros" -> med
            "Milímetros" -> med / 1000
            "Pulgadas" -> med / 39.37f
            else -> med
        }
    }
    private fun focusMed1() {
        if (binding.prTxt.text == "Pulgadas") {
            binding.med1Editxt.performClick()
        } else {
            binding.med1Editxt.requestFocus()
        }
    }
    private fun configurarRetrocesoEditTexts() {
        val campos = listOf(
            binding.med1Editxt to null,
            binding.med2Editxt to binding.med1Editxt,
            binding.cantEditxt to binding.med2Editxt,
            binding.precioEditxt to binding.cantEditxt,
            binding.proEditxt to binding.precioEditxt
        )
        for ((editText, anterior) in campos) {
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL
                    && event.action == android.view.KeyEvent.ACTION_DOWN
                    && editText.text.isNullOrEmpty()
                    && anterior != null) {
                    if (anterior.visibility == View.VISIBLE ||
                        (anterior.parent as? View)?.visibility == View.VISIBLE) {
                        anterior.requestFocus()
                    }
                    true
                } else false
            }
        }
    }
    private fun retasoEnUnidad(): Float {
        return if (binding.prTxt.text == "Pulgadas") retaso / 2.54f else retaso
    }
    // ═══════════════════════════════════════════════════
    // ─── CÁLCULOS DE MEDIDAS ───
    // pies, metroCua, mCubicos, mLineales, perim,
    // med1, med2, med3, mostrarDialogoRetaso
    // ═══════════════════════════════════════════════════
    private fun pies(medida1: Float, medida2: Float): Float {
        // Suma retaso a cada medida para el cálculo de pies cuadrados
        val r = retasoEnUnidad()
        val med1ConRetaso = medida1 + r
        val med2ConRetaso = medida2 + r
        return (conver(med1ConRetaso)) * (conver(med2ConRetaso)) * 11.1f
    }
    private fun metroCua(medida1: Float, medida2: Float): Float {
        // Suma retaso a cada medida para el cálculo de metros cuadrados
        val r = retasoEnUnidad()
        val med1ConRetaso = conver(medida1 + r)
        val med2ConRetaso = conver(medida2 + r)
        return med1ConRetaso * med2ConRetaso
    }
    private fun mCubicos(medida1: Float, medida2: Float, medida3: Float): Float {

        return conver(medida1) * conver(medida2) * conver(medida3)
    }
    private fun mLineales(medida1: Float, medida2: Float): Float {
        return conver(medida1) * medida2
    }
    private fun perim(): Float {
        val medida1 = binding.med1Editxt.text.toString().toFloat()
        val medida2 = binding.med2Editxt.text.toString().toFloat()
        return when (binding.prTxt.text) {
            "Centímetros" -> (((medida1) * 2 + (medida2) * 2)) / 100
            "Metros" -> (medida1) * 2 + (medida2) * 2
            "Milímetros" -> (((medida1) * 2 + (medida2) * 2)) / 1000
            "Pulgadas" -> ((((medida1) * 2.54f) * 2 + ((medida2) * 2.54f) * 2)) / 100
            else -> {(((medida1) * 2 + (medida2) * 2)) / 100 }
        }
    }
    private fun med1(x: View? = null): Float {
        val editText = if (x!= null) {
            x.findViewById<EditText>(R.id.etdMed1)
        } else {
            findViewById(R.id.med1_editxt)
        }
        val med = editText.text.toString()
        return med.toFloatOrNull() ?: 1f
    }
    private fun med2(modelo: View? = null): Float {
        val editText = if (modelo != null) {
            modelo.findViewById<EditText>(R.id.etdMed2)
        } else {
            findViewById(R.id.med2_editxt)
        }
        val med = editText.text.toString()
        return med.toFloatOrNull() ?: 1f
    }
    private fun med3(modelo: View? = null): Float {
        val editText = if (modelo != null) {
            modelo.findViewById<EditText>(R.id.etdMed3)
        } else {
            findViewById(R.id.med3_editxt)
        }
        val med = editText.text.toString()
        return med.toFloatOrNull() ?: 1f
    }
    private fun mostrarDialogoRetaso() {
        val editText = EditText(this).apply {
            setText(retaso.toString())
            hint = "Valor de retaso"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                        android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setPadding(48, 32, 48, 32)
        }

        AlertDialog.Builder(this)
            .setTitle("Configurar Retaso")
            .setMessage("Este valor se sumará a med1 y med2 en los cálculos")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoValor = editText.text.toString().toFloatOrNull()
                if (nuevoValor != null) {
                    retaso = nuevoValor
                    binding.txtRetaso.text = "Retaso: $retaso"
                    Toast.makeText(this, "Retaso actualizado a $retaso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
