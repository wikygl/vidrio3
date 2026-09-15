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
import android.provider.OpenableColumns
import android.provider.Settings
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
import kotlin.math.floor
import kotlin.math.roundToInt

// Módulos del proyecto - Catálogo y datos
import crystal.crystal.catalogo.CatalogoActivity
import crystal.crystal.databinding.ActivityMainBinding
import crystal.crystal.datos.DatabaseProvider
import crystal.crystal.datos.ListaActivity
import crystal.crystal.datos.Product as LocalProduct
import crystal.crystal.datos.ProductSearch

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
import crystal.crystal.pos.PresupuestoManager
import crystal.crystal.pos.RoleConfigManager

// Módulos del proyecto - Red, registro y taller
import crystal.crystal.red.ListChatActivity
import crystal.crystal.red.ChatIdentity
import crystal.crystal.red.interop.ChatInteropIntents
import crystal.crystal.registro.AyudaActivity
import crystal.crystal.registro.GestionDispositivosActivity
import crystal.crystal.registro.InicioActivity
import crystal.crystal.registro.UserProfileActivity
import crystal.crystal.registro.PinAuthActivity
import crystal.crystal.registro.Registro
import crystal.crystal.taller.MedidaActivity
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
    private val REQ_CATALOGO_ANEXAR = 7322
    private var colorSeleccionado: Int = 0

    // ─── Firebase / Autenticación ───
    private val auth = FirebaseAuth.getInstance()
    private lateinit var currentUserId: String
    private var db = Firebase.firestore

    // v1: ocultar (sin eliminar) toda la UI de Crystal Ventas / Patrón / Terminales.
    // Interruptor central en FeaturesV1 (poner en false vuelve a mostrar todo).
    private val OCULTAR_VENTAS_V1 = FeaturesV1.OCULTAR_VENTAS

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
    // Búsqueda inteligente del campo de producto.
    // - rellenando…: guard anti-recursión mientras la búsqueda rellena el campo.
    // - busquedaSuspendida: tras elegir un producto, no se vuelve a buscar hasta que el usuario
    //   vacíe el campo (así el autocompletado no pelea con lo que escribes y puedes buscar de nuevo).
    private var rellenandoProductoDesdeBusqueda: Boolean = false
    private var busquedaSuspendida: Boolean = false
    private val handlerBusquedaProducto = android.os.Handler(android.os.Looper.getMainLooper())
    private var runnableBusquedaProducto: Runnable? = null
    private var dialogoProductoVentas: androidx.appcompat.app.AlertDialog? = null

    // ─── Importador de medidas ───
    private lateinit var importadorMedidas: ImportadorMedidas
    private lateinit var presupuestoManager: PresupuestoManager

    // ─── Dictado por voz ───
    private var dictadoMedidas: DictadoMedidas? = null
    private var dictadoActivo = false
    private var backgroundOriginalMed1: Drawable? = null

    // ─── Chat ───
    private val unreadCountByChat = mutableMapOf<String, Int>()
    private var unreadChatsListener: ListenerRegistration? = null
    private var ultimaSincronizacion: Long = 0L

    @SuppressLint("NewApi", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Corta el paso si la versión instalada quedó por debajo de la mínima exigida (config/app).
        ControlVersion.verificar(this)

        // Inicializar SharedPreferences
        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // Portero de suscripción: engancha el listener de estado_servicio (Fase 1).
        Suscripcion.iniciar(this)
        // Pide "Acceso a todos los archivos" si falta (para leer medidas y respaldar proyectos en Descargas/Crystal).
        AccesoArchivos.pedirSiFalta(this)
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
        actualizar()
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
        posManager = PosManager(this, binding, sharedPreferences)
        posManager.setCallback(posCallback)
        posManager.inicializarControlesPOS()

        edicionMasivaManager = EdicionMasivaManager(this, lista)
        edicionMasivaManager.onListaModificada = { actualizar() }
        presupuestoManager = PresupuestoManager(
            activity = this,
            binding = binding,
            sharedPreferences = sharedPreferences,
            lista = lista
        ).also {
            it.onListaModificada = { actualizar() }
            it.obtenerCurrentUserId = { currentUserId }
            it.edicionMasivaManager = edicionMasivaManager
        }
        manejarPresupuestoRecibido()
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

        binding.txUser.setOnClickListener {
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
        manejarIntentCompartido(intent)

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

        // Wallet / planes / recargas: entrada visible cuando el cobro está abierto (flag propio).
        if (FeaturesV1.OCULTAR_WALLET) {
            binding.btWallet.visibility = View.GONE          // wallet / recargas / plan
        }
        if (OCULTAR_VENTAS_V1) {
            // Ocultar (no eliminar) las entradas de Crystal Ventas/POS para v1.
            binding.btnProcesarVenta.visibility = View.GONE  // procesar venta + diseño de ticket
            binding.btnGestionProductos.visibility = View.GONE // gestión de productos (Ventas)
            binding.btnGestionClientes.visibility = View.GONE  // gestión de clientes (Ventas)
            // El usuario (foto/nombre de Google) sigue visible, pero sin acceso a plan de Ventas.
            binding.btUser.setOnClickListener(null)
            binding.txUser.setOnClickListener(null)
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
                // OJO: reload() es una llamada de red. Estando OFFLINE (o si App Check no puede emitir
                // token sin conexión) falla SIEMPRE, y antes eso disparaba signOut() → se perdía la
                // sesión cacheada y aparecía el login aunque el usuario estuviera en FULL.
                // Solo cerramos sesión si la cuenta es REALMENTE inválida (deshabilitada/eliminada);
                // un fallo de red se ignora y se conserva la sesión local.
                if (tarea.exception is FirebaseAuthInvalidUserException) {
                    auth.signOut()
                    startActivity(Intent(this, InicioActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fotoUsuario()

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
    // Enruta la lista al optimizador que corresponde según el tipo de medida:
    //   • metro lineal (ml)   → corte de varillas (CorteActivity)
    //   • área (m2 / p2)      → corte de planchas de vidrio (OptimizacionPlanchasActivity)
    // Si la lista tiene de los dos tipos se pregunta a dónde enviar.
    private fun enviarACortes() {
        val lineales = lista.filter { it.escala == "ml" && (it.metli > 0f || it.medi1 > 0f) }
        val planos = lista.filter { esItemDeArea(it) }

        when {
            lineales.isEmpty() && planos.isEmpty() ->
                Toast.makeText(
                    this,
                    "No hay medidas en metro lineal (ml) ni de área (m2/p2) para enviar a cortes",
                    Toast.LENGTH_LONG
                ).show()
            planos.isEmpty() -> enviarLinealesACortes(lineales)
            lineales.isEmpty() -> enviarAreasAPlanchas(planos)
            else -> {
                val opciones = arrayOf(
                    "Corte lineal — ${lineales.size} medida(s) en ml",
                    "Corte de planchas — ${planos.size} medida(s) de vidrio"
                )
                AlertDialog.Builder(this)
                    .setTitle("¿A qué optimizador enviar?")
                    .setItems(opciones) { _, cual ->
                        if (cual == 0) enviarLinealesACortes(lineales) else enviarAreasAPlanchas(planos)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    // Un ítem es "de área" (vidrio/plancha) cuando su escala es m2 o p2 y tiene ancho y alto reales.
    private fun esItemDeArea(item: Listado): Boolean =
        (item.escala == "m2" || item.escala == "p2") && item.medi1 > 0f && item.medi2 > 0f

    // Convierte una medida guardada a centímetros usando la unidad con la que se registró el ítem
    // (no la unidad actual de la pantalla), porque la lista puede mezclar unidades.
    private fun medidaACm(valor: Float, unidad: String): Float = when (unidad) {
        "Centímetros" -> valor
        "Metros" -> valor * 100f
        "Milímetros" -> valor / 10f
        "Pulgadas" -> valor * 2.54f
        else -> valor
    }

    // Envía las medidas de área (vidrio) al optimizador de planchas, en centímetros.
    private fun enviarAreasAPlanchas(items: List<Listado>) {
        val arr = org.json.JSONArray()
        items.forEach { item ->
            val anchoCm = medidaACm(item.medi1, item.uni)
            val altoCm = medidaACm(item.medi2, item.uni)
            if (anchoCm <= 0f || altoCm <= 0f) return@forEach
            arr.put(org.json.JSONObject().apply {
                put("a", anchoCm)
                put("h", altoCm)
                put("c", item.canti.toInt().coerceAtLeast(1))
                put("r", item.producto.ifBlank { "-" })
            })
        }
        if (arr.length() == 0) {
            Toast.makeText(this, "Las medidas de vidrio no tienen ancho y alto válidos", Toast.LENGTH_LONG).show()
            return
        }
        val i = Intent(this, crystal.crystal.optimizadores.planchas.OptimizacionPlanchasActivity::class.java)
        i.putExtra("piezas_planchas_json", arr.toString())
        startActivity(i)
        Toast.makeText(this, "Enviando ${arr.length()} medida(s) de vidrio a corte de planchas…", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    // Envía los ítems en metro lineal (ml) recibidos al optimizador de varillas (CorteActivity).
    private fun enviarLinealesACortes(mlItems: List<Listado>) {
        if (mlItems.isEmpty()) {
            Toast.makeText(this, "No hay ítems en metro lineal (ml) para enviar a cortes", Toast.LENGTH_LONG).show()
            return
        }
        val arr = org.json.JSONArray()
        mlItems.forEach { item ->
            // metli = metros lineales TOTALES (largo × cantidad); canti = cantidad.
            // Largo por pieza (en metros) = metli / canti. Se usan valores guardados (no la UI).
            val cant = item.canti.toInt().coerceAtLeast(1)
            val largoMetros = if (item.canti > 0f) item.metli / item.canti else item.metli
            arr.put(org.json.JSONObject().apply {
                put("l", largoMetros * 100f) // metros -> cm (el optimizador trabaja en cm)
                put("c", cant)
                put("r", item.producto.ifBlank { "-" })
            })
        }
        val i = Intent(this, crystal.crystal.optimizadores.corte.CorteActivity::class.java)
        i.putExtra("piezas_cortes_json", arr.toString())
        startActivity(i)
        Toast.makeText(this, "Enviando ${mlItems.size} medida(s) en metro lineal a cortes…", Toast.LENGTH_SHORT).show()
    }

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

        binding.med1Editxt.hint = formatoMedidaSegunUnidad(medida1)
        binding.med2Editxt.hint = formatoMedidaSegunUnidad(medida2)
        binding.med3Editxt.hint = formatoMedidaSegunUnidad(medida3)

        binding.med1Editxt.setText(if (binding.usTxt.text=="uni"){"1"}else{""})
        binding.med2Editxt.setText(if (binding.usTxt.text=="ml"||binding.usTxt.text=="uni"){"1"}else{""})
        binding.med3Editxt.text?.clear()
        binding.cantEditxt.text?.clear()

        val medidas = Listado(
            escala, uni, medida1, medida2, medida3, cantidad, piescant, precio, costocantidad,
            producto, peri, metroscant, mlcant, cubcant, color, uri,
            // Solo si la línea salió del catálogo de Ventas Y sigue siendo ese producto: si el
            // usuario reescribió el nombre a mano, ya no corresponde descontarle stock a aquél.
            productoId = productoSeleccionado
                ?.takeIf { it.nombre.equals(producto.trim(), ignoreCase = true) }
                ?.id
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
            metroscuaTotal += metrosCuadradosConsistentes(medida)
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
    private fun metrosCuadradosConsistentes(medida: Listado): Float {
        return if (medida.escala == "p2" && medida.piescua > 0f) {
            medida.piescua / 11.1f
        } else {
            medida.metcua
        }
    }
    private fun adaptadores(): ArrayAdapter<SpannableString> {
        val clipCodigo = 0x1F4CE
        val clip = String(Character.toChars(clipCodigo))

        val adapter = ArrayAdapter(
            this, R.layout.lista_cal,
            lista.map { datos ->
                // Acortar el URI solo para mostrarlo en la interfaz
                val uriAcortado = resumenAnexo(datos.uri)

                val med1Txt = formatoMedidaSegunUnidad(datos.medi1, datos.uni)
                val med2Txt = formatoMedidaSegunUnidad(datos.medi2, datos.uni)
                val med3Txt = formatoMedidaSegunUnidad(datos.medi3, datos.uni)
                val text = when (datos.escala) {
                    "p2" -> {
                        "($med1Txt x $med2Txt x ${df1(datos.canti)} = " +
                                "${df1(datos.piescua)}(${datos.escala}) " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto} " +
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    "m2" -> {
                        "($med1Txt x $med2Txt x ${df1(datos.canti)} = " +
                                "${df1(datos.metcua)}(${datos.escala}) " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    "m3" -> {
                        "($med1Txt x $med2Txt x $med3Txt x ${df1(datos.canti)} = " +
                                "${df1(datos.metcub)}(${datos.escala}) " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    "ml" -> {
                        "($med1Txt x ${df1(datos.canti)} = ${df1(datos.metli)}(${datos.escala}) " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    "uni" -> {
                        "${df1(datos.canti)}(${datos.escala}) = " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    else -> {
                        "($med1Txt x $med2Txt x ${df1(datos.canti)} = ${df1(datos.piescua)} " +
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
        productoRepository = ProductoRepository(
            database.productoDao(), this, database.movimientoInventarioDao()
        )
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
    private fun resumenAnexo(anexo: String): String {
        val imagenes = imagenesAnexo(anexo)
        return when {
            imagenes.isEmpty() -> ""
            imagenes.size == 1 -> if (imagenes.first().length > 20) "...${imagenes.first().takeLast(20)}" else imagenes.first()
            else -> "${imagenes.size} imagenes"
        }
    }
    private fun imagenesAnexo(anexo: String): List<String> {
        return anexo
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()
    }
    private fun buscarProductoLocal(consulta: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val productos = DatabaseProvider.getInstance(this@MainActivity)
                    .productDao()
                    .getAllProducts()
                val resultados = ProductSearch.buscarSimilares(productos, consulta, 10)

                withContext(Dispatchers.Main) {
                    if (resultados.isNotEmpty() && modoBusqueda) {
                        modoBusqueda = false
                        binding.proEditxt.backgroundTintList =
                            ContextCompat.getColorStateList(this@MainActivity, R.color.color)
                    }

                    when (resultados.size) {
                        0 -> Toast.makeText(this@MainActivity, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                        1 -> seleccionarProductoLocal(resultados[0])
                        else -> mostrarListaProductosLocales(resultados)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error buscando en BD local: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun seleccionarProductoLocal(producto: LocalProduct) {
        binding.proEditxt.setText(producto.nombre)
        binding.precioEditxt.setText(formatoPrecioEntrada(producto.price))
        Toast.makeText(this, "${producto.nombre} - S/ ${String.format(Locale.US, "%.2f", producto.price)}", Toast.LENGTH_SHORT).show()
    }
    private fun mostrarListaProductosLocales(productos: List<LocalProduct>) {
        val items = productos.map {
            "${it.nombre} - S/ ${String.format(Locale.US, "%.2f", it.price)} - Imagenes: ${it.imagenes().size}"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Selecciona producto (${productos.size})")
            .setItems(items) { _, which ->
                seleccionarProductoLocal(productos[which])
                if (modoBusqueda) {
                    modoBusqueda = false
                    binding.proEditxt.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.color)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    private fun formatoPrecioEntrada(precio: Double): String {
        return if (precio == 0.0) "" else String.format(Locale.US, "%.2f", precio)
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
                if (rellenandoProductoDesdeBusqueda) return

                // Solo busca si está activo el botón de búsqueda; si no, el producto se escribe normal.
                if (!modoBusqueda) return

                val texto = s?.toString()?.trim().orEmpty()

                // Al vaciar el campo se levanta la suspensión: queda listo para una nueva búsqueda.
                if (texto.isEmpty()) {
                    busquedaSuspendida = false
                    cancelarBusquedaPendiente()
                    return
                }
                // Tras elegir un producto no se vuelve a buscar hasta que el usuario vacíe el campo.
                if (busquedaSuspendida) return
                if (texto.length < 3) return

                programarBusquedaProducto(texto)
            }
        })
    }

    private fun esModoVentas(): Boolean =
        ::roleConfigManager.isInitialized && roleConfigManager.obtenerEstadoUsuario() == "VENTAS"

    // Debounce: busca 300 ms después de que dejas de escribir (no en cada tecla).
    private fun programarBusquedaProducto(texto: String) {
        cancelarBusquedaPendiente()
        val r = Runnable { ejecutarBusquedaProducto(texto) }
        runnableBusquedaProducto = r
        handlerBusquedaProducto.postDelayed(r, 300)
    }

    private fun cancelarBusquedaPendiente() {
        runnableBusquedaProducto?.let { handlerBusquedaProducto.removeCallbacks(it) }
        runnableBusquedaProducto = null
    }

    private fun ejecutarBusquedaProducto(texto: String) {
        val palabras = texto.split(" ")
        val primera = palabras[0].lowercase()
        when {
            primera == "cliente" || primera == "c" -> {
                val consulta = texto.removePrefix("cliente").removePrefix("c").trim()
                if (consulta.length >= 2) buscarCliente(consulta)
            }
            primera == "producto" || primera == "p" -> {
                val consulta = texto.removePrefix("producto").removePrefix("p").trim()
                if (consulta.length >= 2) buscarProductoRuteado(consulta)
            }
            else -> buscarProductoRuteado(texto)
        }
    }

    // En Ventas busca en el catálogo de Ventas (productoRepository); en modo normal, en el de Datos.
    private fun buscarProductoRuteado(consulta: String) {
        if (esModoVentas()) buscarProductoVentas(consulta) else buscarProductoLocal(consulta)
    }

    private fun buscarProductoVentas(consulta: String) {
        if (!::productoRepository.isInitialized) return
        lifecycleScope.launch(Dispatchers.IO) {
            val resultados = runCatching { productoRepository.buscar(consulta, 10) }.getOrDefault(emptyList())
            withContext(Dispatchers.Main) {
                when (resultados.size) {
                    0 -> { /* silencioso: no molestar mientras escribes */ }
                    1 -> seleccionarProductoVentas(resultados[0])
                    else -> mostrarListaProductosVentas(resultados)
                }
            }
        }
    }

    // Rellena nombre + precio del producto elegido. El modo búsqueda SIGUE activo (solo lo apaga el
    // usuario con el botón). La búsqueda queda suspendida hasta que se vacíe el campo para la siguiente.
    private fun seleccionarProductoVentas(producto: Producto) {
        dialogoProductoVentas?.dismiss()
        productoSeleccionado = producto
        rellenandoProductoDesdeBusqueda = true
        binding.proEditxt.setText(producto.nombre)
        binding.proEditxt.setSelection(producto.nombre.length)
        binding.precioEditxt.setText(formatoPrecioEntrada(producto.precioVenta.toDouble()))
        rellenandoProductoDesdeBusqueda = false
        busquedaSuspendida = true

        val msg = when {
            !producto.tieneStock() -> "⚠️ ${producto.nombre} - Sin stock"
            producto.necesitaReabastecimiento() -> "⚠️ ${producto.nombre} - Stock bajo: ${producto.stock}"
            else -> "✅ ${producto.nombre} - S/ ${String.format(Locale.US, "%.2f", producto.precioVenta)}"
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarListaProductosVentas(productos: List<Producto>) {
        dialogoProductoVentas?.dismiss()
        val items = productos.map {
            "${it.nombre} - S/ ${String.format(Locale.US, "%.2f", it.precioVenta)} - Stock: ${it.stock}"
        }.toTypedArray()

        dialogoProductoVentas = AlertDialog.Builder(this)
            .setTitle("Producto (${productos.size})")
            .setItems(items) { _, which -> seleccionarProductoVentas(productos[which]) }
            .setNegativeButton("Cancelar", null)
            .create()
        dialogoProductoVentas?.setOnDismissListener { dialogoProductoVentas = null }
        dialogoProductoVentas?.show()
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
                val anexar = modelo.findViewById<Button>(R.id.btAbrir)
                val contrato = modelo.findViewById<Button>(R.id.btnContrato)
                val enviar = modelo.findViewById<Button>(R.id.btnEnviarJson)
                val medir = modelo.findViewById<Button>(R.id.btnMedir)
                val datos = modelo.findViewById<TextView>(R.id.tvEscala)
                val pro = modelo.findViewById<TextView>(R.id.etdProducto)
                datos.text = "${lista[position].producto} ${lista[position].escala} en ${lista[position].uni}"

                enviar.setOnClickListener {
                    enviarPresupuestoPorChat()
                }
                dialogo.setView(modelo)
                val dialogoPer = dialogo.create()
                dialogoPer.show()

                medir?.setOnClickListener {
                    enviarItemAMedida(position)
                    dialogoPer.dismiss()
                }

                modelo.findViewById<Button>(R.id.btnCortes)?.setOnClickListener {
                    enviarACortes()
                    dialogoPer.dismiss()
                }

                // Las opciones de ESTE ítem: el mismo producto medido, ofrecido en otros
                // materiales y a otro precio. De ahí sale la proforma de elección.
                modelo.findViewById<Button>(R.id.btnOpciones)?.setOnClickListener {
                    dialogoPer.dismiss()
                    opcionesManager.mostrar(position)
                }

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
                    // En edición solo quedan "Enviar" y "OK"; el resto se oculta (GONE para no comprimir).
                    irlista.visibility = View.GONE
                    eliminar.visibility = View.GONE
                    editar.visibility = View.GONE
                    contrato.visibility = View.GONE
                    anexar.visibility = View.GONE
                    medir?.visibility = View.GONE
                    modelo.findViewById<Button>(R.id.btnCortes)?.visibility = View.GONE
                    // La info de m2/p2 va en el título, no en un botón.
                    datos.text = "${lista[position].producto} ${lista[position].escala} en ${lista[position].uni}\n${resumenPiesMetrosItem(lista[position])}"
                    btnDiaOk.visibility = View.VISIBLE
                    enviar.setOnClickListener {
                        enviarElementoPorChat(position)
                    }

                    m1.text = formatoMedidaSegunUnidad(lista[position].medi1, lista[position].uni)
                    m2.text = formatoMedidaSegunUnidad(lista[position].medi2, lista[position].uni)
                    m3.text = formatoMedidaSegunUnidad(lista[position].medi3, lista[position].uni)
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
                            val medi1 = parseMedidaIngresada(m1.text.toString()) ?: 1f
                            val medi2 = parseMedidaIngresada(m2.text.toString()) ?: 1f
                            val medi3 = parseMedidaIngresada(m3.text.toString()) ?: 1f
                            val cantidad = ca.text.toString().toFloat()
                            val precio = pre.text.toString().toFloat()
                            val piesTotal = pies(medi1, medi2) * cantidad
                            val metrosTotal = metroCua(medi1, medi2) * cantidad
                            val mlTotal = mLineales(medi1, medi2) * cantidad
                            val cubTotal = mCubicos(medi1, medi2, medi3) * cantidad
                            var co = 0f

                            when (escala) {
                                "p2" -> {
                                    co = piesTotal * precio
                                }
                                "m2" -> {
                                    co = metrosTotal * precio
                                }
                                "ml" -> {
                                    co = mlTotal * precio
                                }
                                "m3" -> {
                                    co = cubTotal * precio
                                }
                                "uni" -> {
                                    co = cantidad * precio
                                }
                            }

                            lista[position].medi1 = medi1
                            lista[position].medi2 = medi2
                            lista[position].medi3 = medi3
                            lista[position].canti = cantidad
                            lista[position].precio = precio
                            lista[position].producto = pro.text.toString()
                            lista[position].piescua = piesTotal
                            lista[position].metcua = metrosTotal
                            lista[position].metli = mlTotal
                            lista[position].metcub = cubTotal
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
                anexar.setOnClickListener {
                    selectedPosition = position
                    mostrarOpcionesAnexar(position)
                    dialogoPer.dismiss()
                }
                contrato.setOnClickListener {
                    abrirContrato()
                    dialogoPer.dismiss()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error al mostrar el diálogo", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun resumenPiesMetrosItem(item: Listado): String {
        return "m2: ${df1(metrosCuadradosConsistentes(item))}\np2: ${df1(item.piescua)}"
    }

    private fun enviarItemAMedida(position: Int) {
        if (lista.isEmpty()) {
            Toast.makeText(this, "No hay medidas para enviar", Toast.LENGTH_SHORT).show()
            return
        }
        val cliente = binding.clienteEditxt.text?.toString()?.trim().orEmpty()
        val indiceInicial = if (position in lista.indices) position else 0
        val intent = Intent(this, MedidaActivity::class.java).apply {
            putExtra(MedidaActivity.EXTRA_CLIENTE, cliente)
            putExtra(MedidaActivity.EXTRA_LISTA, ArrayList(lista))
            putExtra(MedidaActivity.EXTRA_INDICE_INICIAL, indiceInicial)
        }
        startActivity(intent)
    }
    private fun abrirContrato() {
        if (lista.isEmpty()) {
            Toast.makeText(this, "No hay elementos para contrato", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, ContratoActivity::class.java).apply {
            putExtra(ContratoActivity.EXTRA_CLIENTE, binding.clienteEditxt.text.toString())
            putExtra(ContratoActivity.EXTRA_LISTA, ArrayList(lista))
            putExtra(ContratoActivity.EXTRA_TOTAL, binding.precioTotal.text.toString())
            putExtra(ContratoActivity.EXTRA_METROS, binding.metrosTotal.text.toString())
            putExtra(ContratoActivity.EXTRA_PIES, binding.piesTotal.text.toString())
        }
        startActivity(intent)
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
            runCatching {
                val gson = Gson()
                val tipoLista = object : TypeToken<List<Listado>>() {}.type
                val listaGuardada: List<Listado> = gson.fromJson(listaString, tipoLista)
                lista.clear()
                lista.addAll(listaGuardada)
            }.onFailure {
                Log.e("MainActivity", "Error cargando lista guardada", it)
            }
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
        if (paquete?.containsKey("lista") != true) return

        val li = paquete?.getSerializable("lista") as? List<*>
        val listaRecibida = li?.filterIsInstance<Listado>()?.toMutableList() ?: mutableListOf()
        if (listaRecibida.isEmpty()) return

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
        intent.removeExtra("lista")
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

        // Imagen elegida en el catálogo para anexar al ítem.
        if (requestCode == REQ_CATALOGO_ANEXAR && resultCode == RESULT_OK) {
            val uri = data?.getStringExtra("imagen_uri")
            if (!uri.isNullOrBlank() && selectedPosition in lista.indices) {
                if (uri.startsWith("http")) {
                    // Remota (Firebase): descargarla a un archivo local para que el PDF la pueda leer.
                    descargarImagenCatalogo(uri, selectedPosition)
                } else {
                    lista[selectedPosition].uri = uri
                    usoImagenUri(if (uri.length > 20) "...${uri.takeLast(20)}" else uri)
                }
            }
            return
        }

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
    private fun mostrarOpcionesAnexar(position: Int) {
        val opciones = arrayOf("Galeria", "Base de datos local", "Catalogo")
        AlertDialog.Builder(this)
            .setTitle("Anexar")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> anexarDesdeBaseLocal(position)
                    2 -> anexarDesdeCatalogo(position)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Anexa desde el catálogo: si el producto coincide con una categoría (p. ej. Nova) va directo a
    // su galería en modo selección; si no coincide, abre el catálogo principal. La imagen elegida
    // se adjunta al ítem (ver onActivityResult, REQ_CATALOGO_ANEXAR).
    private fun anexarDesdeCatalogo(position: Int) {
        selectedPosition = position
        val producto = lista.getOrNull(position)?.producto.orEmpty()
        val categoria = categoriaCatalogo(producto)
        val intent = if (categoria != null) {
            Intent(this, crystal.crystal.catalogo.Fotos::class.java).apply {
                putExtra("categoria", categoria)
                putExtra("modo_seleccion", true)
            }
        } else {
            Intent(this, CatalogoActivity::class.java).apply { putExtra("modo_seleccion", true) }
        }
        startActivityForResult(intent, REQ_CATALOGO_ANEXAR)
    }

    // Descarga la imagen del catálogo (Firebase) a un archivo local y la anexa al ítem.
    private fun descargarImagenCatalogo(url: String, position: Int) {
        Toast.makeText(this, "Descargando imagen…", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch(Dispatchers.IO) {
            val dir = File(filesDir, "anexos_catalogo").apply { mkdirs() }
            val destino = File(dir, "anexo_${System.currentTimeMillis()}.jpg")
            val ok = runCatching {
                val cache = Glide.with(applicationContext).asFile().load(url).submit().get()
                cache.copyTo(destino, overwrite = true)
                true
            }.getOrDefault(false)
            withContext(Dispatchers.Main) {
                if (ok && position in lista.indices) {
                    val local = Uri.fromFile(destino).toString()
                    lista[position].uri = local
                    usoImagenUri(if (local.length > 20) "...${local.takeLast(20)}" else local)
                    Toast.makeText(this@MainActivity, "Imagen anexada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "No se pudo descargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Empareja el producto del ítem con una categoría del catálogo (null = sin coincidencia).
    private fun categoriaCatalogo(producto: String): String? {
        val p = producto.lowercase(Locale.ROOT)
        return when {
            "nova" in p -> "Nova"
            "ducha" in p -> "Puertas ducha"
            "puerta" in p -> "Puertas aluminio"
            "mampara" in p -> "Mamparas"
            "pvc" in p -> "Ventanas PVC"
            "ventana" in p -> "Ventanas"
            "baranda" in p && "inox" in p -> "Baranda inox"
            "baranda" in p -> "Baranda aluminio"
            "pasamano" in p -> "Baranda inox"
            "muro" in p -> "Muro cortina"
            "espejo" in p -> "Espejos"
            "vitro" in p -> "Vitroven"
            "reja" in p -> "Rejas"
            "ladrillo" in p -> "Ladrillos"
            "mueble" in p -> "Muebles"
            "driwall" in p || "drywall" in p -> "Driwall"
            "cielo" in p -> "Cielorazo"
            "techo" in p -> "Techos"
            "melamina" in p -> "Melamina"
            "cuadro" in p -> "Cuadros"
            else -> null
        }
    }
    private fun anexarDesdeBaseLocal(position: Int) {
        val productoItem = lista.getOrNull(position)?.producto.orEmpty()
        val consultaPreferida = consultaAnexo(productoItem)

        lifecycleScope.launch(Dispatchers.IO) {
            val dao = DatabaseProvider.getInstance(this@MainActivity).productDao()
            val productos = dao.getAllProducts()
            val resultados = ProductSearch.buscarSimilares(
                productos,
                listOf(consultaPreferida, productoItem),
                10
            )

            withContext(Dispatchers.Main) {
                if (resultados.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No hay anexos en la base local para: $consultaPreferida", Toast.LENGTH_SHORT).show()
                } else {
                    mostrarProductosParaAnexar(position, resultados)
                }
            }
        }
    }
    private fun consultaAnexo(producto: String): String {
        val p = producto.lowercase(Locale.ROOT)
        return when {
            "puerta ducha" in p -> "puerta ducha"
            "puerta" in p -> "puerta"
            "mampara" in p -> "mampara"
            "muro" in p -> "muro cortina"
            "baranda" in p -> "baranda"
            "reja" in p -> "reja"
            "nova" in p -> "nova"
            "vitro" in p -> "vitroven"
            "ventana" in p -> "ventana"
            else -> producto.ifBlank { binding.proEditxt.text.toString() }.trim()
        }
    }
    private fun mostrarProductosParaAnexar(position: Int, productos: List<LocalProduct>) {
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(8), dp(14), dp(4))
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Base local")
            .setView(ScrollView(this).apply { addView(contenedor) })
            .setNegativeButton("Cancelar", null)
            .create()

        productos.forEach { producto ->
            val imagenes = producto.imagenes()
            val fila = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dp(8), 0, dp(8))
                isClickable = true
                isFocusable = true
                background = ContextCompat.getDrawable(
                    this@MainActivity,
                    android.R.drawable.list_selector_background
                )
            }

            val imagen = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(dp(76), dp(76))
                scaleType = ImageView.ScaleType.CENTER_CROP
                setBackgroundColor(0xFFECECEC.toInt())
                if (imagenes.isNotEmpty()) {
                    Glide.with(this@MainActivity)
                        .load(Uri.parse(imagenes.first()))
                        .centerCrop()
                        .into(this)
                }
            }

            val textos = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(12), 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            textos.addView(TextView(this).apply {
                text = producto.nombre
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.black))
            })
            textos.addView(TextView(this).apply {
                text = "S/ ${String.format(Locale.US, "%.2f", producto.price)}"
                textSize = 14f
            })
            textos.addView(TextView(this).apply {
                text = "Imagenes: ${imagenes.size}"
                textSize = 13f
            })

            fila.addView(imagen)
            fila.addView(textos)
            fila.setOnClickListener {
                dialog.dismiss()
                val imagenes = producto.imagenes()
                if (imagenes.isEmpty()) {
                    aplicarProductoLocalSinImagen(position, producto)
                } else if (imagenes.size == 1) {
                    aplicarAnexoLocal(position, producto, imagenes.first())
                } else {
                    mostrarImagenesProductoParaAnexar(position, producto, imagenes)
                }
            }
            contenedor.addView(fila)
        }

        dialog.show()
    }
    private fun mostrarImagenesProductoParaAnexar(position: Int, producto: LocalProduct, imagenes: List<String>) {
        val imagenesDisponibles = imagenes.distinct()
        val seleccionadas = LinkedHashSet<String>()
        val actuales = imagenesAnexo(lista.getOrNull(position)?.uri.orEmpty())
            .filter { it in imagenesDisponibles }
        if (actuales.isNotEmpty()) {
            seleccionadas.addAll(actuales.take(5))
        } else {
            seleccionadas.addAll(imagenesDisponibles.take(5))
        }

        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(12), dp(12), dp(12), dp(8))
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(producto.nombre)
            .setView(HorizontalScrollView(this).apply { addView(contenedor) })
            .setPositiveButton("Anexar seleccionadas", null)
            .setNegativeButton("Cancelar", null)
            .create()

        fun actualizarEstadoItem(item: LinearLayout, estado: TextView, imageUri: String) {
            val seleccionado = imageUri in seleccionadas
            item.setBackgroundColor(if (seleccionado) 0xFFE3F2FD.toInt() else 0x00000000)
            estado.text = if (seleccionado) "Seleccionada" else "Tocar"
        }

        imagenesDisponibles.forEachIndexed { index, imageUri ->
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(6), dp(4), dp(6), dp(4))
                isClickable = true
                isFocusable = true
                background = ContextCompat.getDrawable(
                    this@MainActivity,
                    android.R.drawable.list_selector_background
                )
            }

            item.addView(ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(dp(118), dp(118))
                scaleType = ImageView.ScaleType.CENTER_CROP
                setBackgroundColor(0xFFECECEC.toInt())
                Glide.with(this@MainActivity)
                    .load(Uri.parse(imageUri))
                    .centerCrop()
                    .into(this)
            })
            item.addView(TextView(this).apply {
                text = "Imagen ${index + 1}"
                gravity = Gravity.CENTER
                textSize = 13f
                setPadding(0, dp(6), 0, 0)
            })
            val estado = TextView(this).apply {
                gravity = Gravity.CENTER
                textSize = 12f
            }
            item.addView(estado)
            item.setOnClickListener {
                if (imageUri in seleccionadas) {
                    seleccionadas.remove(imageUri)
                } else if (seleccionadas.size < 5) {
                    seleccionadas.add(imageUri)
                } else {
                    Toast.makeText(this, "Maximo 5 imagenes por anexo", Toast.LENGTH_SHORT).show()
                }
                actualizarEstadoItem(item, estado, imageUri)
            }
            actualizarEstadoItem(item, estado, imageUri)
            contenedor.addView(item)
        }

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (seleccionadas.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dialog.dismiss()
            aplicarAnexoLocal(position, producto, seleccionadas.toList())
        }
    }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()
    private fun aplicarProductoLocalSinImagen(position: Int, producto: LocalProduct) {
        if (producto.price > 0.0 && lista[position].precio == 0f) {
            lista[position].precio = producto.price.toFloat()
        }
        actualizar()
        Toast.makeText(this, "${producto.nombre} no tiene imagenes guardadas", Toast.LENGTH_SHORT).show()
    }
    private fun aplicarAnexoLocal(position: Int, producto: LocalProduct, imageUri: String) {
        aplicarAnexoLocal(position, producto, listOf(imageUri))
    }
    private fun aplicarAnexoLocal(position: Int, producto: LocalProduct, imageUris: List<String>) {
        val imagenes = imageUris
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .take(5)
        if (imagenes.isEmpty()) {
            aplicarProductoLocalSinImagen(position, producto)
            return
        }

        lista[position].uri = imagenes.joinToString("\n")
        if (producto.price > 0.0 && lista[position].precio == 0f) {
            lista[position].precio = producto.price.toFloat()
        }
        actualizar()
        usoImagenUri(resumenAnexo(lista[position].uri))
        Toast.makeText(this, "${imagenes.size} imagen(es) anexadas desde ${producto.nombre}", Toast.LENGTH_SHORT).show()
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

    /** Las opciones de cada ítem: el mismo producto en otros materiales, cada uno con su precio. */
    private val opcionesManager by lazy {
        crystal.crystal.pos.OpcionesManager(this, lista).also {
            it.onListaModificada = { actualizar() }
        }
    }

    private fun openPdf() {
        val cliente = binding.clienteEditxt.text.toString()
        // Si algún ítem tiene opciones caben dos proformas distintas, y no se puede adivinar cuál
        // se quiere: la de siempre, que suma, o la de elección, que enseña cada medida con sus
        // materiales y NO suma, porque el cliente escoge uno.
        if (crystal.crystal.pos.OpcionesDeProforma.hayEnLaLista(lista)) {
            AlertDialog.Builder(this)
                .setTitle("¿Qué proforma?")
                .setMessage(
                    "Hay ítems con opciones. La proforma de opciones enseña cada medida con sus " +
                        "materiales y sus precios, sin sumar; la normal suma los ítems como siempre."
                )
                .setPositiveButton("De opciones") { _, _ ->
                    pdfGenerator.generarOpcionesYCompartir(cliente, lista)
                }
                .setNegativeButton("Normal") { _, _ ->
                    pdfGenerator.generarYCompartir(cliente, lista, binding.precioTotal.text.toString())
                }
                .setNeutralButton("Cancelar", null)
                .show()
            return
        }
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
        val jsonContent = ChatInteropIntents.consumeStringExtra(intent, ChatInteropIntents.EXTRA_LOAD_BUDGET_JSON)
        val nombreArchivo = intent.getStringExtra(ChatInteropIntents.EXTRA_LOAD_BUDGET_NAME)

        if (jsonContent != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Presupuesto Recibido")
            builder.setMessage("Has recibido el presupuesto: $nombreArchivo\n¿Deseas cargarlo?")
            builder.setPositiveButton("Cargar") { _, _ ->
                  presupuestoManager.cargarPresupuestoDesdeJson(jsonContent)
            }
            builder.setNegativeButton("Cancelar", null)
            builder.show()
            return
        }

        // Método anterior para URIs locales (mantener por compatibilidad)
        val presupuestoUri = ChatInteropIntents.consumeStringExtra(intent, ChatInteropIntents.EXTRA_LOAD_BUDGET_URI)
        val nombreArchivoUri = intent.getStringExtra(ChatInteropIntents.EXTRA_LOAD_BUDGET_NAME)

        if (presupuestoUri != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Presupuesto Recibido")
            builder.setMessage("Has recibido el presupuesto: $nombreArchivoUri\n¿Deseas cargarlo?")
            builder.setPositiveButton("Cargar") { _, _ ->
                val uri = Uri.parse(presupuestoUri)
                  presupuestoManager.manejarArchivoPresupuesto(uri)
            }
            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }
    }
    override fun onNewIntent(intent: Intent) {
        intent.let { super.onNewIntent(it) }
        setIntent(intent)
        manejarPresupuestoRecibido()
        manejarIntentCompartido(intent)
    }

    private fun manejarIntentCompartido(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND) return

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_STREAM)
        }

        if (uri == null) {
            val texto = intent.getStringExtra(Intent.EXTRA_TEXT)?.trim().orEmpty()
            if (texto.isNotEmpty()) {
                abrirSelectorChatParaTexto(texto)
                intent.action = null
                intent.removeExtra(Intent.EXTRA_TEXT)
            }
            return
        }

        val mimeType = intent.type ?: contentResolver.getType(uri).orEmpty()
        val fileName = obtenerNombreCompartido(uri)

        if (esArchivoMedidasCrystal(fileName, mimeType)) {
            abrirMedidasCrystal(uri, mimeType)
        } else if (mimeType == "application/json" || fileName.startsWith("presupuesto_")) {
            mostrarOpcionesArchivoCompartido(uri, fileName, mimeType, isBudget = true)
        } else {
            abrirSelectorChatParaArchivo(uri, fileName, mimeType)
        }

        intent.action = null
        intent.removeExtra(Intent.EXTRA_STREAM)
    }

    private fun mostrarOpcionesArchivoCompartido(uri: Uri, fileName: String, mimeType: String, isBudget: Boolean) {
        val opciones = if (isBudget) {
            arrayOf("Cargar presupuesto", "Enviar por chat")
        } else {
            arrayOf("Enviar por chat")
        }

        AlertDialog.Builder(this)
            .setTitle("Archivo compartido")
            .setMessage(fileName)
            .setItems(opciones) { _, which ->
                when {
                    isBudget && which == 0 -> presupuestoManager.manejarArchivoPresupuesto(uri)
                    else -> abrirSelectorChatParaArchivo(uri, fileName, mimeType)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun esArchivoMedidasCrystal(fileName: String, mimeType: String): Boolean {
        return mimeType == MedidaActivity.MIME_MEDIDAS_CRYSTAL ||
            fileName.endsWith(".${MedidaActivity.EXTENSION_MEDIDAS_CRYSTAL}", ignoreCase = true)
    }

    private fun abrirMedidasCrystal(uri: Uri, mimeType: String) {
        startActivity(Intent(this, MedidaActivity::class.java).apply {
            action = Intent.ACTION_SEND
            type = mimeType.ifBlank { MedidaActivity.MIME_MEDIDAS_CRYSTAL }
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        })
    }

    private fun abrirSelectorChatParaArchivo(uri: Uri, fileName: String, mimeType: String) {
        startActivity(Intent(this, ListChatActivity::class.java).apply {
            putExtra("usuario", currentUserId)
            putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_URI, uri.toString())
            putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_NAME, fileName)
            putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_MIME, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        })
    }

    private fun abrirSelectorChatParaTexto(texto: String) {
        startActivity(Intent(this, ListChatActivity::class.java).apply {
            putExtra("usuario", currentUserId)
            putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_TEXT, texto)
        })
    }

    private fun obtenerNombreCompartido(uri: Uri): String {
        val cursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        cursor?.use {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && it.moveToFirst()) {
                return it.getString(index) ?: "archivo"
            }
        }
        return uri.lastPathSegment?.substringAfterLast('/') ?: "archivo"
    }
    private fun mostrarMenuPresupuesto() {
        val opciones = if (lista.isEmpty()) {
            arrayOf("Cargar presupuesto desde archivo")
        } else {
            arrayOf(
                "Enviar por chat",
                "Edición masiva",
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
        if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.avanzadoActivo(),
                "Guardar el presupuesto como archivo es una función de pago.")) return
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
        if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.avanzadoActivo(),
                "Compartir el presupuesto como archivo es una función de pago.")) return
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
                        "Metros²: ${presupuesto.metrosTotal}\n" +
                        "Pies²: ${presupuesto.piesTotal}\n\n" +
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
        builder.setTitle("¿Cómo cargar?")
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

        prepararYEnviarPresupuesto(
            elementos = lista.toList(),
            precioTotal = binding.precioTotal.text.toString(),
            metrosTotal = binding.metrosTotal.text.toString(),
            piesTotal = binding.piesTotal.text.toString(),
            perimetroTotal = binding.per.text.toString(),
            prefijoArchivo = "presupuesto"
        )
    }

    private fun enviarElementoPorChat(position: Int) {
        val item = lista.getOrNull(position)
        if (item == null) {
            Toast.makeText(this, "No se encontró el elemento seleccionado", Toast.LENGTH_SHORT).show()
            return
        }

        prepararYEnviarPresupuesto(
            elementos = listOf(item.copy()),
            precioTotal = df2(item.costo),
            metrosTotal = df1(metrosCuadradosConsistentes(item)),
            piesTotal = df1(item.piescua),
            perimetroTotal = "${df1(item.peri)} m..",
            prefijoArchivo = "elemento"
        )
    }

    private fun prepararYEnviarPresupuesto(
        elementos: List<Listado>,
        precioTotal: String,
        metrosTotal: String,
        piesTotal: String,
        perimetroTotal: String,
        prefijoArchivo: String
    ) {
        if (elementos.isEmpty()) {
            Toast.makeText(this, "No hay elementos para enviar", Toast.LENGTH_SHORT).show()
            return
        }

        val cliente = binding.clienteEditxt.text.toString().takeIf { it.isNotEmpty() } ?: "Sin nombre"

        // Crear objeto completo del presupuesto
        val presupuesto = PresupuestoCompleto(
            cliente = cliente,
            fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            elementos = elementos,
            precioTotal = precioTotal,
            metrosTotal = metrosTotal,
            piesTotal = piesTotal,
            perimetroTotal = perimetroTotal
        )

        // Serializar a JSON
        val gson = Gson()
        val jsonPresupuesto = gson.toJson(presupuesto)

        // Crear archivo temporal
        val clienteArchivo = cliente.replace(Regex("[^A-Za-z0-9_-]"), "_")
        val fileName = "${prefijoArchivo}_${clienteArchivo}_${System.currentTimeMillis()}.json"
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
            intent.putExtra(ChatInteropIntents.EXTRA_SEND_BUDGET_URI, uri.toString())
            intent.putExtra(ChatInteropIntents.EXTRA_BUDGET_NAME, fileName)
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
        if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.avanzadoActivo(),
                "Cargar un presupuesto desde archivo es una función de pago.")) return
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
        unreadChatsListener?.remove()
        unreadCountByChat.clear()
        actualizarBadgeChat()

        val firestoreActorUid = ChatIdentity.resolveFirestoreChatActorUid(this, FirebaseAuth.getInstance(), currentUserId)
        val ownAliases = ChatIdentity.resolveChatIdentityAliases(this, FirebaseAuth.getInstance(), currentUserId).toSet()

        unreadChatsListener = db.collection("chats")
            .whereArrayContains("users", firestoreActorUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }

                unreadCountByChat.clear()

                snapshot.documents.forEach { chatDoc ->
                    val users = (chatDoc.get("users") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                    if (users.none { it in ownAliases }) return@forEach

                    val chatId = chatDoc.id
                    val unreadBy = chatDoc.get("unreadBy") as? Map<*, *> ?: emptyMap<Any, Any>()
                    val count = ownAliases
                        .mapNotNull { alias -> unreadBy[alias]?.toString()?.toIntOrNull() }
                        .firstOrNull()
                        ?: unreadBy[firestoreActorUid]?.toString()?.toIntOrNull()
                        ?: 0
                    unreadCountByChat[chatId] = count
                }

                actualizarBadgeChat()
            }
    }
    @SuppressLint("SetTextI18n")
    private fun actualizarBadgeChat() {
        val totalUnread = unreadCountByChat.values.sum()
        binding.btnChat.setImageResource(R.drawable.ic_mensajes)
        if (totalUnread > 0) {
            binding.tvBadge.text = totalUnread.toString()
            binding.tvBadge.visibility = View.VISIBLE
        } else {
            binding.tvBadge.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        unreadChatsListener?.remove()
        super.onDestroy()
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
    private fun formatoMedidaSegunUnidad(valor: Float, unidad: CharSequence? = binding.prTxt.text): String {
        return if (unidad.toString() == "Pulgadas") formatoPulgadaFraccion(valor) else df1(valor)
    }
    private fun formatoPulgadaFraccion(valor: Float): String {
        var entero = floor(valor).toInt()
        val fraccion = valor - entero
        var dieciseisavos = (fraccion * 16f).roundToInt()

        if (dieciseisavos == 16) {
            entero += 1
            dieciseisavos = 0
        }
        if (dieciseisavos == 0) return entero.toString()

        val divisor = when {
            dieciseisavos % 8 == 0 -> 2
            dieciseisavos % 4 == 0 -> 4
            dieciseisavos % 2 == 0 -> 8
            else -> 16
        }
        val numerador = dieciseisavos / (16 / divisor)
        val fraccionTexto = "$numerador/$divisor"

        return if (entero == 0) fraccionTexto else "$entero $fraccionTexto"
    }
    private fun parseMedidaIngresada(valor: String): Float? {
        val texto = valor.trim().replace(",", ".")
        if (texto.isEmpty()) return null

        texto.toFloatOrNull()?.let { return it }

        val partes = texto.split(Regex("\\s+")).filter { it.isNotBlank() }
        return when (partes.size) {
            1 -> parseFraccion(partes[0])
            2 -> {
                val entero = partes[0].toFloatOrNull() ?: return null
                val fraccion = parseFraccion(partes[1]) ?: return null
                entero + if (entero < 0f) -fraccion else fraccion
            }
            else -> null
        }
    }
    private fun parseFraccion(valor: String): Float? {
        val partes = valor.split("/")
        if (partes.size != 2) return null

        val numerador = partes[0].toFloatOrNull() ?: return null
        val divisor = partes[1].toFloatOrNull() ?: return null
        if (divisor == 0f) return null

        return numerador / divisor
    }
    // ═══════════════════════════════════════════════════
    // ─── SPINNERS DE UNIDADES ───
    // uni1, uni2
    // ═══════════════════════════════════════════════════
    private fun uni1() {
        val spinnerUsa = findViewById<Spinner?>(R.id.spinner_usa)
        if (spinnerUsa == null) {
            Log.e("MainActivity", "uni1: spinner_usa no encontrado en layout")
            return
        }
        usados = spinnerUsa
        val listaUsados = arrayOf("p2", "m2", "ml", "m3", "uni")
        val colores = arrayOf(R.color.color, R.color.fucsia, R.color.verde, R.color.violeta, R.color.naranja)
        val adaptadorU = object : ArrayAdapter<String>(
              this,
              android.R.layout.simple_spinner_item, listaUsados) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = layoutInflater.inflate(R.layout.lista_spinner, parent, false) as TextView
                view.text = getItem(position)
                view.setTextColor(getColor(position))
                view.gravity = Gravity.CENTER
                view.setPadding(24, 18, 24, 18)
                view.minHeight = 96
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = layoutInflater.inflate(R.layout.lista_spinner, parent, false) as TextView
                view.text = getItem(position)
                view.setTextColor(getColor(position))
                view.gravity = Gravity.CENTER
                view.setPadding(24, 18, 24, 18)
                view.minHeight = 96
                return view
            }

              fun getColor(position: Int): Int {
                  return ContextCompat.getColor(this@MainActivity, colores[position])
              }
          }
        adaptadorU.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
        val spinnerUni = findViewById<Spinner?>(R.id.spinner_uni)
        if (spinnerUni == null) {
            Log.e("MainActivity", "uni2: spinner_uni no encontrado en layout")
            return
        }
        unidades = spinnerUni
        val listaUnidades = arrayOf("Centímetros", "Metros", "Milímetros", "Pulgadas")
        val adaptador = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            listaUnidades
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = layoutInflater.inflate(R.layout.lista_spinner, parent, false) as TextView
                view.text = getItem(position)
                view.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.color))
                view.gravity = Gravity.CENTER
                view.setPadding(24, 18, 24, 18)
                view.minHeight = 96
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = layoutInflater.inflate(R.layout.lista_spinner, parent, false) as TextView
                view.text = getItem(position)
                view.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.color))
                view.gravity = Gravity.CENTER
                view.setPadding(24, 18, 24, 18)
                view.minHeight = 96
                return view
            }
        }
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
                            binding.med1Editxt.setText(formatoPulgadaFraccion(valor))
                            binding.med2Editxt.performClick()
                        }.mostrar()
                    }
                    binding.med2Editxt.setOnClickListener {
                        DialogoPulgadas(this@MainActivity) { valor ->
                            binding.med2Editxt.setText(formatoPulgadaFraccion(valor))
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
        return when (binding.prTxt.text) {
            "Centímetros" -> retaso
            "Metros" -> retaso / 100f
            "Milímetros" -> retaso * 10f
            "Pulgadas" -> retaso / 2.54f
            else -> retaso
        }
    }
    // ═══════════════════════════════════════════════════
    // ─── CÁLCULOS DE MEDIDAS ───
    // pies, metroCua, mCubicos, mLineales, perim,
    // med1, med2, med3, mostrarDialogoRetaso
    // ═══════════════════════════════════════════════════
    private fun pies(medida1: Float, medida2: Float): Float {
        // El retaso siempre se guarda en cm y se convierte a la unidad actual antes de sumarse.
        val r = retasoEnUnidad()
        val med1ConRetaso = medida1 + r
        val med2ConRetaso = medida2 + r
        return (conver(med1ConRetaso)) * (conver(med2ConRetaso)) * 11.1f
    }
    private fun metroCua(medida1: Float, medida2: Float): Float {
        // El retaso siempre se guarda en cm y se convierte a la unidad actual antes de sumarse.
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
        val medida1 = med1()
        val medida2 = med2()
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
        return parseMedidaIngresada(med) ?: 1f
    }
    private fun med2(modelo: View? = null): Float {
        val editText = if (modelo != null) {
            modelo.findViewById<EditText>(R.id.etdMed2)
        } else {
            findViewById(R.id.med2_editxt)
        }
        val med = editText.text.toString()
        return parseMedidaIngresada(med) ?: 1f
    }
    private fun med3(modelo: View? = null): Float {
        val editText = if (modelo != null) {
            modelo.findViewById<EditText>(R.id.etdMed3)
        } else {
            findViewById(R.id.med3_editxt)
        }
        val med = editText.text.toString()
        return parseMedidaIngresada(med) ?: 1f
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
            .setMessage("Este valor siempre se ingresa en centímetros y se convierte a la unidad elegida antes del cálculo.")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoValor = editText.text.toString().toFloatOrNull()
                if (nuevoValor != null) {
                    retaso = nuevoValor
                    binding.txtRetaso.text = "Retaso: $retaso cm"
                    Toast.makeText(this, "Retaso actualizado a $retaso cm", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
