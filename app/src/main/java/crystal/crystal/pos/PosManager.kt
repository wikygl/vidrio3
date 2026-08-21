package crystal.crystal.pos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.print.PrintManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson

import crystal.crystal.Listado
import crystal.crystal.comprobantes.*
import crystal.crystal.databinding.ActivityMainBinding
import crystal.crystal.productos.P2_POR_M2
import crystal.crystal.productos.ProductoDatabase
import crystal.crystal.productos.ProductoRepository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

import java.io.File

class PosManager(
    private val activity: AppCompatActivity,
    private val binding: ActivityMainBinding,
    private val sharedPreferences: SharedPreferences
) {
    // ─── POS / Ventas ───
    private lateinit var spinnerFormaPago: Spinner
    private lateinit var spinnerTipoRecibo: Spinner
    private var formaPagoSeleccionada: String = "Efectivo"
    private var tipoReciboSeleccionado: String = "Boleta"

    // ─── Comprobantes / Tickets ───
    private val ticketGenerator by lazy { TicketGenerator(activity) }
    var configuracionTicket: ConfiguracionTicket? = null
    var datosEmpresa: DatosEmpresa? = null

    interface PosCallback {
        fun obtenerLista(): MutableList<Listado>
        fun obtenerNombreVendedor(): String
        fun obtenerEsPatron(): Boolean
        fun obtenerRolDispositivo(): String
        fun actualizar()
        fun obtenerUidParaConsulta(): String?
        fun guardarDatos()
        fun mostrarDialogoPatronNoEncontrado()
        fun mostrarOpcionReconectar()
    }

    private var callback: PosCallback? = null

    fun setCallback(callback: PosCallback) {
        this.callback = callback
    }

    // ═══════════════════════════════════════════════════
    // ─── POS / PUNTO DE VENTA ───
    // ═══════════════════════════════════════════════════

    fun inicializarControlesPOS() {
        spinnerFormaPago = binding.spinnerFormaPago
        spinnerTipoRecibo = binding.spinnerTipoRecibo

        val formasPago = arrayOf(
            "💵 Efectivo",
            "💳 Tarjeta",
            "💎 Crystal",
            "📱 Yape",
            "📱 Plin",
            "🔄 Múltiple"
        )

        val adapterPago = ArrayAdapter(
            activity,
            android.R.layout.simple_spinner_item,
            formasPago
        )
        adapterPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFormaPago.adapter = adapterPago

        spinnerFormaPago.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                formaPagoSeleccionada = formasPago[position].substringAfter(" ")
                Log.d("PosManager", "Forma de pago seleccionada: $formaPagoSeleccionada")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val tiposRecibo = arrayOf(
            "🧾 Boleta",
            "📄 Factura",
            "📝 Recibo",
            "💼 R.Honorario"
        )

        val adapterRecibo = ArrayAdapter(
            activity,
            android.R.layout.simple_spinner_item,
            tiposRecibo
        )
        adapterRecibo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoRecibo.adapter = adapterRecibo

        spinnerTipoRecibo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                tipoReciboSeleccionado = tiposRecibo[position].substringAfter(" ")
                Log.d("PosManager", "Tipo de recibo seleccionado: $tipoReciboSeleccionado")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        configurarBotonesPOS()
    }

    private fun configurarBotonesPOS() {
        binding.btnProcesarVenta.setOnClickListener {
            procesarVenta()
        }

        binding.btnGestionProductos.setOnClickListener {
            activity.startActivity(Intent(activity, crystal.crystal.productos.GestionProductosActivity::class.java))
        }

        binding.btnGestionClientes.setOnClickListener {
            activity.startActivity(Intent(activity, crystal.crystal.clientes.GestionClientesActivity::class.java))
        }

        binding.btnReportes.setOnClickListener {
            val uid = obtenerUidParaConsulta()
            if (uid == null) {
                Toast.makeText(activity, "Inicia sesión para ver reportes", Toast.LENGTH_SHORT).show()
            } else {
                activity.startActivity(
                    Intent(activity, ReportesActivity::class.java)
                        .putExtra(ReportesActivity.EXTRA_UID, uid)
                )
            }
        }
    }

    fun procesarVenta() {
        val lista = callback?.obtenerLista() ?: return
        if (lista.isEmpty()) {
            Toast.makeText(activity, "⚠️ El carrito está vacío", Toast.LENGTH_LONG).show()
            return
        }

        val cliente = binding.clienteEditxt.text.toString().trim()
        if (cliente.isEmpty()) {
            Toast.makeText(activity, "⚠️ Ingresa el nombre del cliente", Toast.LENGTH_LONG).show()
            binding.clienteEditxt.requestFocus()
            return
        }

        mostrarDialogoConfirmacionVenta(cliente)
    }

    private fun mostrarDialogoConfirmacionVenta(cliente: String) {
        val total = binding.precioTotal.text.toString()

        val mensaje = """
        Cliente: $cliente
        Items: ${callback?.obtenerLista()?.size ?: 0}
        Total: S/ $total

        Forma de Pago: $formaPagoSeleccionada
        Tipo de Recibo: $tipoReciboSeleccionado

        ¿Confirmar venta?
    """.trimIndent()

        AlertDialog.Builder(activity)
            .setTitle("💰 Confirmar Venta")
            .setMessage(mensaje)
            .setPositiveButton("✅ Confirmar") { _, _ ->
                confirmarVenta(cliente, total)
            }
            .setNegativeButton("❌ Cancelar", null)
            .show()
    }

    private fun confirmarVenta(cliente: String, total: String) {
        activity.lifecycleScope.launch {
            try {
                guardarVentaEnRoom(cliente, total)
                generarTicketVenta(cliente, total)
                limpiarCarritoPostVenta()

                Toast.makeText(
                    activity,
                    "✅ Venta procesada exitosamente",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                Log.e("PosManager", "Error procesando venta: ${e.message}", e)
                Toast.makeText(
                    activity,
                    "❌ Error al procesar venta: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private suspend fun guardarVentaEnRoom(cliente: String, total: String) {
        withContext(Dispatchers.IO) {
            Log.d("PosManager", """
            Guardando venta:
            Cliente: $cliente
            Total: $total
            Forma Pago: $formaPagoSeleccionada
            Tipo Recibo: $tipoReciboSeleccionado
            Items: ${callback?.obtenerLista()?.size ?: 0}
        """.trimIndent())
        }
    }

    // ─── Inventario ─────────────────────────────────────────────────────────────

    private val repoProductos by lazy {
        val db = ProductoDatabase.getDatabase(activity)
        ProductoRepository(db.productoDao(), activity, db.movimientoInventarioDao())
    }

    private fun repositorioProductos() = repoProductos

    /**
     * Cuánto stock consume cada línea, en la unidad en que se lleva el inventario. El vidrio se
     * guarda en pies cuadrados, así que las líneas en m² se convierten; lo que se vende por unidad
     * o por metro lineal consume su propia cantidad.
     */
    private fun cantidadParaStock(item: Listado): Float = when (item.escala) {
        "p2" -> item.piescua
        "m2" -> item.metcua * P2_POR_M2
        "ml" -> item.metli
        "m3" -> item.metcub
        else -> item.canti
    }.takeIf { it > 0f } ?: item.canti

    /**
     * Verifica el inventario para toda la venta. Devuelve los consumos a aplicar, o null si algo no
     * alcanza (ahí ya avisó al usuario y la venta no debe continuar). Las líneas sin producto del
     * catálogo —trabajos a medida— simplemente no consumen stock.
     */
    private suspend fun prepararConsumoStock(lista: List<Listado>): List<ProductoRepository.ConsumoStock>? {
        val porProducto = mutableMapOf<String, Float>()
        lista.forEach { item ->
            val id = item.productoId ?: return@forEach
            porProducto[id] = (porProducto[id] ?: 0f) + cantidadParaStock(item)
        }
        if (porProducto.isEmpty()) return emptyList()

        return repositorioProductos().prepararConsumo(porProducto).fold(
            onSuccess = { it },
            onFailure = { error ->
                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(activity)
                        .setTitle("No hay stock suficiente")
                        .setMessage("${error.message}\n\nCorrige la cantidad o repone el inventario antes de emitir el comprobante.")
                        .setPositiveButton("Entendido", null)
                        .show()
                }
                null
            }
        )
    }

    @SuppressLint("HardwareIds")
    fun generarTicketVenta(cliente: String, total: String) {
        // Candado (Fase 3): generar comprobante/ticket es de pago.
        if (!crystal.crystal.Suscripcion.exigir(activity, crystal.crystal.Suscripcion.puedeExportarPdf(),
                "Generar comprobantes es una función de pago.")) return
        if (configuracionTicket == null || datosEmpresa == null) {
            Toast.makeText(activity, "Cargando configuración...", Toast.LENGTH_SHORT).show()
            return
        }

        val lista = callback?.obtenerLista() ?: return
        val nombreVendedor = callback?.obtenerNombreVendedor() ?: "Vendedor"
        val esPatron = callback?.obtenerEsPatron() ?: false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val itemsVenta = lista.map { item ->
                    ItemVenta(
                        codigo = null,
                        nombre = item.producto,
                        cantidad = item.canti,
                        unidad = when (item.escala) {
                            "p2" -> "PC2"
                            "m2" -> "M2"
                            "m3" -> "M3"
                            "ml" -> "ML"
                            "uni" -> "UND"
                            else -> "UND"
                        },
                        precio = item.costo,
                        ancho = if (item.escala == "p2" || item.escala == "m2" || item.escala == "m3")
                            item.medi1 else null,
                        alto = if (item.escala == "p2" || item.escala == "m2" || item.escala == "m3")
                            item.medi2 else null,
                        etiqueta = null
                    )
                }

                val totalVenta = total
                    .replace("S/", "")
                    .replace(" ", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .trim()
                    .toFloatOrNull() ?: 0f
                val subtotal = totalVenta / 1.18f
                val igv = totalVenta - subtotal

                val serie = when (tipoReciboSeleccionado.uppercase()) {
                    "BOLETA" -> "B001"
                    "FACTURA" -> "F001"
                    "TICKET", "RECIBO" -> "T001"
                    else -> "T001"
                }
                // Registro UNIFICADO en el servidor: la Cloud Function `registrarVenta` valida que el
                // equipo esté autorizado y activo, asigna el correlativo atómico por serie y guarda la
                // venta bajo el patrón. Así todas las terminales comparten numeración y registro. Si no
                // hay red o falla, se usa correlativo/registro LOCAL de respaldo (puede dejar un hueco
                // en la numeración del servidor).
                val uidVenta = obtenerUidParaConsulta() ?: "local"
                val fechaVenta = System.currentTimeMillis()
                val deviceId = Settings.Secure.getString(
                    activity.contentResolver, Settings.Secure.ANDROID_ID
                )
                val itemsJson = Gson().toJson(itemsVenta)

                val payloadVenta = hashMapOf(
                    "serie" to serie,
                    "tipoComprobante" to tipoReciboSeleccionado.uppercase(),
                    "cliente" to cliente,
                    "subtotal" to subtotal.toDouble(),
                    "igv" to igv.toDouble(),
                    "total" to totalVenta.toDouble(),
                    "formaPago" to formaPagoSeleccionada,
                    "vendedor" to nombreVendedor,
                    "terminal" to (if (esPatron) null else Build.MODEL),
                    "itemsJson" to itemsJson,
                    "fecha" to fechaVenta
                )

                // Antes de emitir nada: verificar que el inventario alcance. Si a un solo ítem le
                // falta stock se aborta la venta completa, en vez de descontar a medias.
                val consumos = prepararConsumoStock(lista)
                if (consumos == null) return@launch

                var numeroComprobante: String
                var numero: Long
                var ventaId: String
                var registradaEnServidor: Boolean
                try {
                    val res = FirebaseFunctions.getInstance()
                        .getHttpsCallable("registrarVenta")
                        .call(
                            hashMapOf(
                                "patronUid" to uidVenta,
                                "deviceId" to deviceId,
                                "venta" to payloadVenta
                            )
                        )
                        .await()
                    val resp = res.data as? Map<*, *>
                    numeroComprobante = resp?.get("numeroComprobante") as? String
                        ?: throw IllegalStateException("Respuesta sin número de comprobante")
                    numero = (resp["numero"] as? Number)?.toLong() ?: 0L
                    ventaId = resp["id"] as? String ?: java.util.UUID.randomUUID().toString()
                    registradaEnServidor = true
                } catch (e: Exception) {
                    Log.w("PosManager", "registrarVenta falló; correlativo/registro local de respaldo: ${e.message}")
                    val dao = VentaDatabase.getDatabase(activity).ventaDao()
                    numero = (dao.ultimoNumero(uidVenta, serie) ?: 0L) + 1L
                    numeroComprobante = "$serie-${numero.toString().padStart(8, '0')}"
                    ventaId = java.util.UUID.randomUUID().toString()
                    registradaEnServidor = false
                }

                Log.d("PosManager", "=== DATOS PARA PDF ===")
                Log.d("PosManager", "Logo URL: ${datosEmpresa?.logoUrl}")
                Log.d("PosManager", "Logo Path: ${datosEmpresa?.logoPath}")
                Log.d("PosManager", "Path existe: ${datosEmpresa?.logoPath?.let { File(it).exists() }}")

                val archivoPDF = ticketGenerator.generarComprobante(
                    tipoComprobante = tipoReciboSeleccionado.uppercase(),
                    numeroComprobante = numeroComprobante,
                    cliente = cliente,
                    clienteDocumento = "",
                    clienteDireccion = "",
                    items = itemsVenta,
                    subtotal = subtotal,
                    igv = igv,
                    total = totalVenta,
                    empresa = datosEmpresa!!,
                    configuracion = configuracionTicket!!,
                    vendedor = nombreVendedor,
                    terminal = if (esPatron) null else Build.MODEL,
                    formaPago = formaPagoSeleccionada
                )

                // Copia local (respaldo offline / caché para reportes sin red). Si el servidor ya la
                // registró queda como sincronizada; si fue respaldo local, queda pendiente.
                runCatching {
                    val venta = Venta(
                        id = ventaId,
                        numeroComprobante = numeroComprobante,
                        serie = serie,
                        numero = numero,
                        tipoComprobante = tipoReciboSeleccionado.uppercase(),
                        cliente = cliente,
                        subtotal = subtotal,
                        igv = igv,
                        total = totalVenta,
                        formaPago = formaPagoSeleccionada,
                        vendedor = nombreVendedor,
                        terminal = if (esPatron) null else Build.MODEL,
                        itemsJson = itemsJson,
                        fecha = fechaVenta,
                        uidPatron = uidVenta,
                        pendienteSincronizar = !registradaEnServidor,
                        ultimaSincronizacion = if (registradaEnServidor) System.currentTimeMillis() else 0L
                    )
                    VentaDatabase.getDatabase(activity).ventaDao().insertar(venta)
                }.onFailure {
                    Log.e("PosManager", "No se pudo guardar copia local de la venta: ${it.message}", it)
                }

                // Recién ahora se descuenta el inventario, con el comprobante ya emitido como
                // referencia del movimiento. Cada descuento queda asentado en el libro.
                if (consumos.isNotEmpty()) {
                    repositorioProductos().aplicarConsumo(
                        consumos = consumos,
                        referencia = "VENTA-$numeroComprobante",
                        vendedor = nombreVendedor,
                        terminal = if (esPatron) null else Build.MODEL
                    ).onFailure {
                        Log.e("PosManager", "No se pudo descontar stock: ${it.message}", it)
                    }
                }

                withContext(Dispatchers.Main) {
                    mostrarOpcionesComprobante(archivoPDF, tipoReciboSeleccionado, numeroComprobante)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        activity,
                        "Error al generar comprobante: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun mostrarOpcionesComprobante(archivo: File, tipo: String, numero: String) {
        val opciones = arrayOf("📤 Compartir", "🖨️ Imprimir", "📄 Ver PDF", "❌ Cerrar")

        AlertDialog.Builder(activity)
            .setTitle("$tipo $numero generado")
            .setItems(opciones) { dialog, which ->
                when (which) {
                    0 -> compartirPDF(archivo)
                    1 -> imprimirPDF(archivo)
                    2 -> abrirPDF(archivo)
                    3 -> dialog.dismiss()
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun compartirPDF(archivo: File) {
        try {
            val uri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                archivo
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Comprobante de Venta")
                putExtra(Intent.EXTRA_TEXT, "Te envío el comprobante de tu compra")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            activity.startActivity(Intent.createChooser(intent, "Compartir comprobante por:"))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "Error al compartir: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun imprimirPDF(archivo: File) {
        try {
            val printManager = activity.getSystemService(Context.PRINT_SERVICE) as PrintManager
            printManager.print(
                "Comprobante Crystal ${archivo.nameWithoutExtension}",
                PdfFilePrintAdapter(archivo),
                null
            )
        } catch (e: Exception) {
            Log.e("PosManager", "Error al imprimir: ${e.message}", e)
            Toast.makeText(activity, "No se pudo imprimir: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirPDF(archivo: File) {
        try {
            val uri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                archivo
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            activity.startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "No hay app para abrir PDF", Toast.LENGTH_SHORT).show()
        }
    }

    fun limpiarCarritoPostVenta() {
        binding.precioUnitario.text = "0.0"
        binding.precioCantidad.text = "0.0"
        binding.pcTxt.text = "0.0"
        binding.mcTxt.text = "0.0"
        binding.med1Editxt.setText(if (binding.usTxt.text == "uni") "1" else "")
        binding.med2Editxt.setText(if (binding.usTxt.text == "ml" || binding.usTxt.text == "uni") "1" else "")
        binding.cantEditxt.setText("")
        binding.precioEditxt.setText("")
        binding.proEditxt.setText("")
        binding.med1Editxt.hint = ""
        binding.med2Editxt.hint = ""
        binding.cantEditxt.hint = ""
        binding.precioTotal.text = "0.0"
        binding.piesTotal.text = "0.0"
        binding.metrosTotal.text = "0.0"
        binding.per.text = "0.0"
        binding.prueTxt.text = ""

        callback?.obtenerLista()?.clear()
        callback?.actualizar()

        spinnerFormaPago.setSelection(0)
        spinnerTipoRecibo.setSelection(0)
    }

    fun habilitarModoVentas() {
        activity.lifecycleScope.launch {
            // Delegate back - habilitarModoVentas just calls cargarRolYConfigurar
            // which stays in MainActivity
        }
    }

    // ═══════════════════════════════════════════════════
    // ─── CONFIGURACIÓN DE TICKETS Y EMPRESA ───
    // ═══════════════════════════════════════════════════

    fun cargarConfiguracionTicket() {
        val disenoJson = sharedPreferences.getString("diseno_ticket_json", null)

        if (disenoJson != null) {
            try {
                val map = Gson().fromJson(disenoJson, Map::class.java) as Map<*, *>
                configuracionTicket = mapToConfiguracion(map)
                Log.d("PosManager", "✅ Configuración cargada desde memoria local")
                return
            } catch (e: Exception) {
                Log.e("PosManager", "Error parseando configuración local: ${e.message}")
            }
        }

        cargarConfiguracionDesdeFirestore()
    }

    fun cargarDatosEmpresa() {
        val ruc = sharedPreferences.getString("empresa_ruc", null)

        if (ruc != null) {
            datosEmpresa = DatosEmpresa(
                ruc = ruc,
                razonSocial = sharedPreferences.getString("empresa_razon_social", "") ?: "",
                nombreComercial = sharedPreferences.getString("empresa_nombre_comercial", null),
                direccion = sharedPreferences.getString("empresa_direccion", "") ?: "",
                telefono = sharedPreferences.getString("empresa_telefono", "") ?: "",
                email = sharedPreferences.getString("empresa_email", null),
                logoUrl = sharedPreferences.getString("empresa_logo_url", null),
                logoPath = sharedPreferences.getString("empresa_logo_path", null)
            )
            Log.d("PosManager", "✅ Empresa cargada desde memoria local: $ruc")
            return
        }

        cargarEmpresaDesdeFirestore()
    }

    private fun cargarConfiguracionDesdeFirestore() {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val uid = obtenerUidParaConsulta() ?: return@launch

                val documento = FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .get()
                    .await()

                val config = documento.get("empresa.disenoTicket") as? Map<*, *>

                withContext(Dispatchers.Main) {
                    if (config != null) {
                        configuracionTicket = mapToConfiguracion(config)
                        sharedPreferences.edit()
                            .putString("diseno_ticket_json", Gson().toJson(config))
                            .apply()
                        Log.d("PosManager", "✅ Configuración descargada de Firestore y guardada localmente")
                    } else {
                        configuracionTicket = ConfiguracionTicketDefecto.obtener()
                    }
                }
            } catch (e: Exception) {
                Log.e("PosManager", "Error cargando configuración de Firestore: ${e.message}")
                withContext(Dispatchers.Main) {
                    configuracionTicket = ConfiguracionTicketDefecto.obtener()
                }
            }
        }
    }

    private fun cargarEmpresaDesdeFirestore() {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val uid = obtenerUidParaConsulta() ?: return@launch

                val documento = FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .get()
                    .await()

                val empresaData = documento.get("empresa") as? Map<*, *>

                withContext(Dispatchers.Main) {
                    if (empresaData != null) {
                        datosEmpresa = DatosEmpresa(
                            ruc = empresaData["ruc"] as? String ?: "",
                            razonSocial = empresaData["razonSocial"] as? String ?: "",
                            nombreComercial = empresaData["nombreComercial"] as? String,
                            direccion = empresaData["direccion"] as? String ?: "",
                            telefono = empresaData["telefono"] as? String ?: "",
                            email = empresaData["email"] as? String,
                            logoUrl = empresaData["logoUrl"] as? String
                        )

                        guardarEmpresaEnMemoria(datosEmpresa!!)

                        Log.d("PosManager", "✅ Empresa descargada de Firestore y guardada localmente")
                    } else {
                        datosEmpresa = DatosEmpresa(
                            ruc = "00000000000",
                            razonSocial = "MI EMPRESA",
                            direccion = "Jr. Principal 123",
                            telefono = "999999999"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("PosManager", "Error cargando empresa de Firestore: ${e.message}")
            }
        }
    }

    suspend fun descargarYGuardarLogo(logoUrl: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("PosManager", "🔽 Iniciando descarga de logo desde: $logoUrl")

                val logosDir = File(activity.getExternalFilesDir(null), "logos")
                if (!logosDir.exists()) {
                    logosDir.mkdirs()
                    Log.d("PosManager", "📁 Directorio creado: ${logosDir.absolutePath}")
                }

                val archivoLogo = File(logosDir, "logo_empresa.jpg")
                Log.d("PosManager", "📄 Guardando en: ${archivoLogo.absolutePath}")

                val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(logoUrl)
                storageRef.getFile(archivoLogo).await()

                Log.d("PosManager", "✅ Logo descargado. Tamaño: ${archivoLogo.length()} bytes")

                archivoLogo.absolutePath

            } catch (e: Exception) {
                Log.e("PosManager", "❌ Error descargando logo: ${e.message}", e)
                null
            }
        }
    }

    fun sincronizarDatosManualmente() {
        Log.d("PosManager", "🔄 Iniciando sincronización manual...")

        activity.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val uid = obtenerUidParaConsulta()
                if (uid == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            activity,
                            "Error: Usuario no autenticado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                val documento = FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .get()
                    .await()

                if (!documento.exists()) {
                    Log.e("PosManager", "❌ Documento del patrón no existe")
                    withContext(Dispatchers.Main) {
                        callback?.mostrarDialogoPatronNoEncontrado()
                    }
                    return@launch
                }

                val empresaData = documento.get("empresa") as? Map<*, *>
                if (empresaData == null) {
                    throw Exception("No hay datos de empresa configurados")
                }

                // Descargar logo PRIMERO (si existe)
                val logoUrl = empresaData["logoUrl"] as? String
                var logoPathLocal: String? = null

                if (!logoUrl.isNullOrEmpty()) {
                    Log.d("PosManager", "📥 Descargando logo...")
                    logoPathLocal = descargarYGuardarLogo(logoUrl)
                    Log.d("PosManager", "✅ Logo descargado en: $logoPathLocal")
                }

                // Crear datosEmpresa CON logoPath
                datosEmpresa = DatosEmpresa(
                    ruc = empresaData["ruc"] as? String ?: "",
                    razonSocial = empresaData["razonSocial"] as? String ?: "",
                    nombreComercial = empresaData["nombreComercial"] as? String,
                    direccion = empresaData["direccion"] as? String ?: "",
                    telefono = empresaData["telefono"] as? String ?: "",
                    email = empresaData["email"] as? String,
                    logoUrl = logoUrl,
                    logoPath = logoPathLocal
                )

                // Guardar configuración de ticket
                val disenoTicket = empresaData["disenoTicket"] as? Map<*, *>
                if (disenoTicket != null) {
                    configuracionTicket = mapToConfiguracion(disenoTicket)
                    sharedPreferences.edit()
                        .putString("diseno_ticket_json", Gson().toJson(disenoTicket))
                        .apply()
                }

                // Guardar todo en SharedPreferences
                guardarEmpresaEnMemoria(datosEmpresa!!)

                val ultimaSincronizacion = System.currentTimeMillis()
                sharedPreferences.edit()
                    .putLong("empresa_ultima_sincronizacion", ultimaSincronizacion)
                    .apply()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        activity,
                        "✅ Datos sincronizados correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Log.d("PosManager", "✅ Sincronización completada")

            } catch (e: Exception) {
                Log.e("PosManager", "❌ Error en sincronización: ${e.message}", e)

                val esTerminalPIN = sharedPreferences.getBoolean("es_terminal_pin", false)

                withContext(Dispatchers.Main) {
                    if (esTerminalPIN &&
                        (e.message?.contains("permission", true) == true ||
                                e.message?.contains("PERMISSION_DENIED", true) == true)) {
                        callback?.mostrarOpcionReconectar()
                    } else {
                        Toast.makeText(
                            activity,
                            "❌ Error: ${e.message}\n\nVerifica tu conexión a internet",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    fun guardarEmpresaEnMemoria(empresa: DatosEmpresa) {
        sharedPreferences.edit().apply {
            putString("empresa_ruc", empresa.ruc)
            putString("empresa_razon_social", empresa.razonSocial)
            putString("empresa_nombre_comercial", empresa.nombreComercial)
            putString("empresa_direccion", empresa.direccion)
            putString("empresa_telefono", empresa.telefono)
            putString("empresa_email", empresa.email)
            putString("empresa_logo_url", empresa.logoUrl)
            putString("empresa_logo_path", empresa.logoPath)
            apply()
        }
    }

    fun obtenerUidParaConsulta(): String? {
        val esTerminalPIN = sharedPreferences.getBoolean("es_terminal_pin", false)
        return if (esTerminalPIN) {
            sharedPreferences.getString("patron_uid", null)
        } else {
            FirebaseAuth.getInstance().currentUser?.uid
        }
    }

    fun mapToConfiguracion(map: Map<*, *>): ConfiguracionTicket {
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
}
