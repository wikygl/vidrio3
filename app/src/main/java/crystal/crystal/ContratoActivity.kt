package crystal.crystal

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import crystal.crystal.clientes.Cliente
import crystal.crystal.clientes.ClienteDatabase
import crystal.crystal.clientes.TipoDocumento
import crystal.crystal.contrato.ClausulasContrato
import crystal.crystal.contrato.CondicionesContrato
import crystal.crystal.contrato.ContratoPdf
import crystal.crystal.contrato.DatosDelContrato
import crystal.crystal.contrato.Igv
import crystal.crystal.contrato.ParteDelContrato
import crystal.crystal.pos.MembreteDeProforma
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * El contrato de fabricación e instalación —y el recibo de adelanto— de lo que hay en la lista.
 *
 * Llega desde la lista de medidas con los ítems, el cliente elegido del registro (si lo hay) y el
 * membrete de la tienda. Los datos del cliente entran por dos lados: del registro de clientes o
 * escritos aquí mismo. Lo demás —la obra, el pago, el plazo, las condiciones y las firmas— se
 * completa en esta pantalla, que enseña el contrato tal como va a salir.
 */
class ContratoActivity : AppCompatActivity() {

    // ---- Las partes ----
    private var cliente = ParteDelContrato()
    private var contratistaActual: DatosContratista? = null

    // ---- La obra y el pago de ESTE contrato ----
    private var direccionObra = ""
    private var plazoDias = 0
    /** null: el adelanto por defecto, el porcentaje de las condiciones sobre el total. */
    private var adelanto: Double? = null
    private var fechaSaldo = ""
    private var observaciones = ""

    /** Las que se repiten de un contrato a otro; se guardan. */
    private var condiciones = CondicionesContrato()

    // ---- Firmas y evidencia ----
    private var firmaCliente: Bitmap? = null
    private var evidenciaActual: EvidenciaPresupuesto? = null
    /** Uno por contrato: el de la pantalla es el que sale impreso y el que guarda la evidencia. */
    private val folio = EvidenciaManager.generarIdPresupuesto()
    private val fecha = Date()

    private val seleccionarPdfVerificar =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let { verificarPdf(it) }
        }

    private lateinit var filaCliente: TextView
    private lateinit var filaContratista: TextView
    private lateinit var filaObra: TextView
    private lateinit var filaCondiciones: TextView
    private lateinit var filaFirmas: TextView
    private lateinit var tvContrato: TextView
    private lateinit var tvCabeceraTitulo: TextView

    private val prefs by lazy { getSharedPreferences("ContratoPrefs", MODE_PRIVATE) }
    private val gson = Gson()
    private var lista: List<Listado> = emptyList()
    private var membrete: MembreteDeProforma? = null
    private var modoDocumento = ModoDocumento.CONTRATO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        condiciones = cargarCondiciones()
        plazoDias = condiciones.plazoDias
        cargarExtras()
        construirVista()
        cargarContratistaInicial()
        refrescar()
        cargarClienteInicial()
    }

    @Suppress("DEPRECATION")
    private fun cargarExtras() {
        val recibida = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_LISTA, ArrayList::class.java)
        } else {
            intent.getSerializableExtra(EXTRA_LISTA) as? ArrayList<*>
        }
        lista = recibida?.filterIsInstance<Listado>().orEmpty()
        cliente = cliente.copy(nombre = intent.getStringExtra(EXTRA_CLIENTE).orEmpty().trim())
        membrete = intent.getStringExtra(EXTRA_MEMBRETE)?.let {
            runCatching { gson.fromJson(it, MembreteDeProforma::class.java) }.getOrNull()
        }
    }

    // ═══════════════════════════ El contrato ═══════════════════════════

    /** Todo lo que dice el contrato ahora mismo, tal como se ve en la pantalla. */
    private fun datos(): DatosDelContrato {
        val c = contratistaActual
        val total = lista.sumOf { it.costo.toDouble() }
        return DatosDelContrato(
            fecha = fecha,
            folio = folio,
            cliente = cliente,
            contratista = ParteDelContrato(
                nombre = c?.nombre.orEmpty().trim(),
                documento = c?.documento.orEmpty().trim(),
                direccion = c?.direccion.orEmpty().trim(),
                telefono = c?.telefono.orEmpty().trim()
            ),
            direccionObra = direccionObra,
            lista = lista,
            plazoDias = plazoDias,
            adelanto = adelanto ?: (total * condiciones.adelantoPorcentaje / 100.0),
            fechaSaldo = fechaSaldo,
            observaciones = observaciones,
            condiciones = condiciones
        )
    }

    private fun firmaContratista(): Bitmap? =
        ClienteContratoStore.base64ABitmap(contratistaActual?.firmaBase64.orEmpty())

    /** Rehace el texto y los resúmenes de cada fila con los datos de ahora. */
    @SuppressLint("SetTextI18n")
    private fun refrescar() {
        val d = datos()
        tvContrato.text = when (modoDocumento) {
            ModoDocumento.CONTRATO -> ClausulasContrato.textoPlano(d)
            ModoDocumento.RECIBO -> textoRecibo(d)
        }
        val faltan = ClausulasContrato.faltantes(d)
        fun aviso(vararg campos: String) = if (faltan.any { it in campos }) "⚠ " else ""

        filaCliente.text = aviso(
            "Nombre del cliente", "DNI o RUC del cliente", "Representante del cliente",
            "DNI del representante"
        ) + "Cliente\n" + if (cliente.nombre.isBlank()) "Toque para ingresarlo"
        else listOf(cliente.nombre, cliente.identificacion()).joinToString("  ·  ")

        val k = d.contratista
        filaContratista.text = aviso("Nombre del contratista", "DNI o RUC del contratista") +
            "Contratista\n" + if (k.nombre.isBlank() && k.documento.isBlank()) "Toque para ingresar sus datos"
        else listOf(k.nombre.ifBlank { "(sin nombre)" }, k.identificacion()).joinToString("  ·  ")

        val saldo = if (fechaSaldo.isBlank()) "saldo a la entrega" else "saldo al $fechaSaldo"
        filaObra.text = aviso("Dirección de la obra", "Plazo de entrega") + "Obra y pago\n" +
            "${d.obra.ifBlank { "Sin dirección de obra" }}\n" +
            "${if (plazoDias > 0) "$plazoDias días hábiles" else "Sin plazo"}  ·  " +
            "adelanto S/ ${ClausulasContrato.soles(d.adelantoReal)}  ·  $saldo"

        val igv = when (condiciones.igv) {
            Igv.INCLUIDO -> "incluye IGV"
            Igv.NO_INCLUIDO -> "sin IGV"
            Igv.SIN_MENCION -> "IGV sin mención"
        }
        filaCondiciones.text = "Condiciones\n" +
            "Garantía ${condiciones.garantiaMeses} meses  ·  $igv" +
            (condiciones.ciudad.takeIf { it.isNotBlank() }?.let { "  ·  $it" } ?: "")

        filaFirmas.text = "Firmas\n" +
            "Cliente ${if (firmaCliente != null) "✔" else "—"}    " +
            "Contratista ${if (firmaContratista() != null) "✔" else "—"}"
    }

    /** El recibo como se lee en la pantalla. */
    private fun textoRecibo(d: DatosDelContrato): String = buildString {
        appendLine(ClausulasContrato.TITULO_RECIBO)
        appendLine("Folio ${d.folio} · ${ClausulasContrato.fechaCorta(d.fecha)}")
        appendLine()
        val parrafos = ClausulasContrato.recibo(d)
        appendLine(parrafos[0])
        appendLine()
        ClausulasContrato.detalle(d.lista).forEach { f ->
            appendLine("${f.numero}. ${f.descripcion} — ${f.medidas} — cant. ${f.cantidad} — S/ ${ClausulasContrato.soles(f.importe)}")
        }
        parrafos.drop(1).forEach { appendLine(); appendLine(it) }
        appendLine()
        appendLine("ENTREGA: ${ClausulasContrato.firmante(d.cliente)}")
        appendLine("RECIBE: ${ClausulasContrato.firmante(d.contratista)}")
    }

    // ═══════════════════════════ La pantalla ═══════════════════════════

    private fun construirVista() {
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(ContextCompat.getColor(this@ContratoActivity, R.color.sombra))
        }
        contenedor.addView(cabecera())

        val root = ScrollView(this).apply {
            isFillViewport = true
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(12))
        }
        root.addView(content)

        // Cada fila resume una parte del contrato; al tocarla se completa. La que falta, avisa.
        filaCliente = fila { dialogoCliente() }
        filaContratista = fila { mostrarPerfilesContratista() }
        filaObra = fila { dialogoObraYPago() }
        filaCondiciones = fila { dialogoCondiciones() }
        filaFirmas = fila { dialogoFirmas() }
        listOf(filaCliente, filaContratista, filaObra, filaCondiciones, filaFirmas)
            .forEach { content.addView(it) }

        val cardDoc = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = ContextCompat.getDrawable(this@ContratoActivity, R.drawable.bg_control_panel)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
        }
        tvContrato = TextView(this).apply {
            textSize = 13f
            setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.negro))
            setTextIsSelectable(true)
        }
        cardDoc.addView(tvContrato)
        content.addView(cardDoc)
        contenedor.addView(root)

        val barraInferior = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(6), dp(12), dp(10))
        }
        barraInferior.addView(boton("PDF / compartir") { generarPdfYCompartir() })
        contenedor.addView(barraInferior)

        setContentView(contenedor)
    }

    private fun fila(onClick: () -> Unit): TextView = TextView(this).apply {
        textSize = 14f
        setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.negro))
        background = ContextCompat.getDrawable(this@ContratoActivity, R.drawable.bg_control_panel)
        setPadding(dp(16), dp(10), dp(16), dp(10))
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, dp(3), 0, dp(3)) }
        setOnClickListener { onClick() }
    }

    private fun cabecera(): LinearLayout {
        val barra = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(ContextCompat.getColor(this@ContratoActivity, R.color.color))
            setPadding(dp(6), dp(6), dp(6), dp(6))
            elevation = dp(4).toFloat()
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(56))
        }
        barra.addView(iconoBarra(R.drawable.ic_arrow_back) { finish() })
        tvCabeceraTitulo = TextView(this).apply {
            text = tituloDocumento()
            textSize = 19f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.blanco))
            setPadding(dp(8), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        barra.addView(tvCabeceraTitulo)
        barra.addView(iconoBarra(R.drawable.ajustes_c) { abrirAjustes() })
        return barra
    }

    private fun iconoBarra(res: Int, onClick: () -> Unit): ImageButton = ImageButton(this).apply {
        setImageResource(res)
        setColorFilter(ContextCompat.getColor(this@ContratoActivity, R.color.blanco))
        background = null
        setPadding(dp(10), dp(10), dp(10), dp(10))
        layoutParams = LinearLayout.LayoutParams(dp(46), dp(46))
        setOnClickListener { onClick() }
    }

    private fun abrirAjustes() {
        val cambioModo = if (modoDocumento == ModoDocumento.RECIBO) "Cambiar a Contrato" else "Cambiar a Recibo de adelanto"
        val opciones = arrayOf(
            cambioModo,
            "Marcar como aceptado",
            "Verificar PDF",
            "Respaldar evidencias",
            "Guardar como texto",
            "Abrir Baúl"
        )
        AlertDialog.Builder(this)
            .setTitle("Ajustes")
            .setItems(opciones) { _, i ->
                when (i) {
                    0 -> cambiarModo(
                        if (modoDocumento == ModoDocumento.RECIBO) ModoDocumento.CONTRATO else ModoDocumento.RECIBO
                    )
                    1 -> marcarAceptado()
                    2 -> seleccionarPdfVerificar.launch(arrayOf("application/pdf"))
                    3 -> exportarEvidencias()
                    4 -> guardarDocumentoActual()
                    5 -> startActivity(Intent(this, BaulActivity::class.java))
                }
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun cambiarModo(modo: ModoDocumento) {
        modoDocumento = modo
        if (::tvCabeceraTitulo.isInitialized) tvCabeceraTitulo.text = tituloDocumento()
        refrescar()
    }

    private fun tituloDocumento(): String =
        if (modoDocumento == ModoDocumento.RECIBO) "Recibo de adelanto" else "Contrato de obra"

    // ═══════════════════════════ El cliente ═══════════════════════════

    /**
     * El cliente con el que se llegó. Si se eligió del registro, se trae completo de ahí; si solo
     * llegó el nombre, se busca entre los guardados del contrato y en el registro.
     */
    private fun cargarClienteInicial() {
        val id = intent.getStringExtra(EXTRA_CLIENTE_ID)
        val nombre = cliente.nombre
        lifecycleScope.launch {
            val dao = ClienteDatabase.getDatabase(applicationContext).clienteDao()
            val delRegistro = withContext(Dispatchers.IO) {
                runCatching {
                    when {
                        !id.isNullOrBlank() -> dao.obtenerPorId(id)
                        nombre.isNotBlank() -> dao.buscar(nombre, 5)
                            .firstOrNull { it.nombreCompleto.equals(nombre, ignoreCase = true) }
                        else -> null
                    }
                }.getOrNull()
            }
            if (isFinishing || isDestroyed) return@launch
            if (delRegistro != null) {
                usarClienteDelRegistro(delRegistro, nombreEscrito = nombre)
            } else if (nombre.isNotBlank()) {
                ClienteContratoStore.listar(this@ContratoActivity)
                    .firstOrNull { it.nombre.equals(nombre, ignoreCase = true) }
                    ?.let { usarClienteGuardado(it) }
            }
            refrescar()
            if (cliente.nombre.isBlank()) dialogoCliente()
        }
    }

    /** Los datos de un cliente del registro. El nombre escrito en la lista manda sobre el guardado. */
    private fun usarClienteDelRegistro(c: Cliente, nombreEscrito: String = "") {
        cliente = ParteDelContrato(
            nombre = nombreEscrito.ifBlank { c.nombreCompleto }.trim(),
            documento = documentoDe(c),
            direccion = c.direccion.orEmpty().trim(),
            telefono = c.telefono.orEmpty().trim()
        )
        // El representante no está en el registro: si ya se apuntó en un contrato, se recupera.
        ClienteContratoStore.listar(this).firstOrNull {
            it.documento.filter(Char::isDigit) == c.numeroDocumento.filter(Char::isDigit)
        }?.let {
            cliente = cliente.copy(
                representante = it.representante.orEmpty(),
                documentoRepresentante = it.documentoRepresentante.orEmpty()
            )
        }
    }

    /** DNI y RUC se reconocen por sus dígitos; los demás documentos llevan su tipo delante. */
    private fun documentoDe(c: Cliente): String = when (c.tipoDocumento) {
        TipoDocumento.DNI, TipoDocumento.RUC -> c.numeroDocumento.trim()
        else -> "${c.tipoDocumento.nombre} ${c.numeroDocumento}".trim()
    }

    /**
     * Un cliente guardado en los contratos. Su firma NO se carga: una firma vale para el contrato
     * en que se puso, y pegarla en otro sería hacerle firmar lo que no vio.
     */
    private fun usarClienteGuardado(c: ClienteContrato) {
        cliente = ParteDelContrato(
            nombre = c.nombre, documento = c.documento, direccion = c.direccion,
            telefono = c.telefono, representante = c.representante.orEmpty(),
            documentoRepresentante = c.documentoRepresentante.orEmpty()
        )
    }

    private fun dialogoCliente() {
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(8))
        }
        val guardados = ClienteContratoStore.listar(this)
        val etN = AutoCompleteTextView(this).apply {
            setText(cliente.nombre)
            hint = "Nombre o razón social"
            setSingleLine(true)
            textSize = 15f
            setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.negro))
            threshold = 1
            setAdapter(ArrayAdapter(this@ContratoActivity, android.R.layout.simple_dropdown_item_1line, guardados.map { it.nombre }))
        }
        val etD = campo("DNI o RUC", cliente.documento)
        val etDir = campo("Dirección", cliente.direccion)
        val etT = campo("Teléfono", cliente.telefono).apply { inputType = InputType.TYPE_CLASS_PHONE }
        val etRep = campo("Representante (si es empresa)", cliente.representante)
        val etRepDoc = campo("DNI del representante", cliente.documentoRepresentante)
            .apply { inputType = InputType.TYPE_CLASS_NUMBER }

        fun ponerEnCampos(p: ParteDelContrato) {
            etN.setText(p.nombre); etD.setText(p.documento); etDir.setText(p.direccion)
            etT.setText(p.telefono); etRep.setText(p.representante); etRepDoc.setText(p.documentoRepresentante)
        }
        etN.setOnItemClickListener { _, _, pos, _ ->
            val nombre = etN.adapter.getItem(pos) as? String ?: return@setOnItemClickListener
            guardados.firstOrNull { it.nombre == nombre }?.let {
                usarClienteGuardado(it); ponerEnCampos(cliente)
            }
        }

        // Los dos lados por donde entra un cliente: el registro de clientes y los guardados aquí.
        val origen = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        origen.addView(boton("Registro de clientes") {
            buscarEnRegistro { c -> usarClienteDelRegistro(c); ponerEnCampos(cliente) }
        })
        origen.addView(boton("Guardados") {
            elegirGuardado { c -> usarClienteGuardado(c); ponerEnCampos(cliente) }
        })
        cont.addView(origen)
        listOf(etN, etD, etDir, etT, etRep, etRepDoc).forEach { cont.addView(it) }

        fun leerCampos() {
            cliente = ParteDelContrato(
                nombre = etN.text.toString().trim(),
                documento = etD.text.toString().trim(),
                direccion = etDir.text.toString().trim(),
                telefono = etT.text.toString().trim(),
                representante = etRep.text.toString().trim(),
                documentoRepresentante = etRepDoc.text.toString().trim()
            )
        }
        AlertDialog.Builder(this)
            .setTitle("Datos del cliente")
            .setView(ScrollView(this).apply { addView(cont) })
            .setPositiveButton("Aceptar") { _, _ -> leerCampos(); refrescar() }
            .setNeutralButton("Aceptar y guardar") { _, _ ->
                leerCampos(); guardarCliente(); refrescar()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Busca en el registro de clientes (la base de la app) por nombre o documento. */
    private fun buscarEnRegistro(alElegir: (Cliente) -> Unit) {
        val dao = ClienteDatabase.getDatabase(applicationContext).clienteDao()
        val etBuscar = campo("Nombre o documento", cliente.nombre)
        val lv = ListView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(300))
        }
        var encontrados: List<Cliente> = emptyList()
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(8))
            addView(etBuscar); addView(lv)
        }
        val dialogo = AlertDialog.Builder(this)
            .setTitle("Registro de clientes")
            .setView(cont)
            .setNegativeButton("Cerrar", null)
            .create()
        var busqueda: Job? = null
        fun buscar(texto: String) {
            busqueda?.cancel()
            busqueda = lifecycleScope.launch {
                delay(250)
                encontrados = withContext(Dispatchers.IO) {
                    runCatching {
                        if (texto.isBlank()) dao.obtenerTodos().take(30) else dao.buscar(texto.trim(), 30)
                    }.getOrDefault(emptyList())
                }
                lv.adapter = ArrayAdapter(
                    this@ContratoActivity, android.R.layout.simple_list_item_1,
                    encontrados.map { "${it.nombreCompleto}\n${it.tipoDocumento.nombre} ${it.numeroDocumento}" }
                )
            }
        }
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) = buscar(s?.toString().orEmpty())
        })
        lv.setOnItemClickListener { _, _, pos, _ ->
            encontrados.getOrNull(pos)?.let { alElegir(it) }
            dialogo.dismiss()
        }
        dialogo.show()
        buscar(etBuscar.text.toString())
    }

    private fun elegirGuardado(alElegir: (ClienteContrato) -> Unit) {
        val clientes = ClienteContratoStore.listar(this)
        if (clientes.isEmpty()) {
            Toast.makeText(this, "No hay clientes guardados en contratos", Toast.LENGTH_SHORT).show()
            return
        }
        val etiquetas = clientes.map {
            it.nombre + if (it.documento.isNotBlank()) " — ${it.documento}" else ""
        }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Clientes guardados")
            .setItems(etiquetas) { _, i -> alElegir(clientes[i]) }
            .setNeutralButton("Eliminar…") { _, _ ->
                AlertDialog.Builder(this)
                    .setTitle("¿Cuál eliminar?")
                    .setItems(etiquetas) { _, i ->
                        ClienteContratoStore.eliminar(this, clientes[i].id)
                        Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun guardarCliente() {
        if (cliente.nombre.isBlank()) {
            Toast.makeText(this, "El cliente necesita al menos su nombre", Toast.LENGTH_SHORT).show()
            return
        }
        // La firma que ya tuviera guardada se respeta: aquí no se toca.
        val anterior = ClienteContratoStore.listar(this).firstOrNull {
            (cliente.documento.isNotBlank() && it.documento.equals(cliente.documento, true)) ||
                it.nombre.equals(cliente.nombre, true)
        }
        ClienteContratoStore.guardar(
            this,
            ClienteContrato(
                id = anterior?.id ?: ClienteContratoStore.nuevoId(),
                nombre = cliente.nombre,
                documento = cliente.documento,
                direccion = cliente.direccion,
                telefono = cliente.telefono,
                firmaBase64 = anterior?.firmaBase64.orEmpty(),
                representante = cliente.representante,
                documentoRepresentante = cliente.documentoRepresentante
            )
        )
        Toast.makeText(this, "Cliente guardado", Toast.LENGTH_SHORT).show()
    }

    // ═══════════════════════════ Obra, pago y condiciones ═══════════════════════════

    private fun dialogoObraYPago() {
        val d = datos()
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(8))
        }
        val etObra = campo("Dirección de la obra (vacío: la del cliente)", direccionObra)
        val etPlazo = campo("Plazo de entrega (días hábiles)", if (plazoDias > 0) plazoDias.toString() else "")
            .apply { inputType = InputType.TYPE_CLASS_NUMBER }
        val etAdelanto = campo("Adelanto S/ (total S/ ${ClausulasContrato.soles(d.total)})", "%.2f".format(Locale.US, d.adelantoReal))
            .apply { inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL }
        val cbEntrega = CheckBox(this).apply {
            text = "El saldo se paga a la entrega"
            isChecked = fechaSaldo.isBlank()
        }
        val etSaldo = campo("Fecha para pagar el saldo", fechaSaldo)
        configurarSelectorFecha(etSaldo)
        etSaldo.visibility = if (cbEntrega.isChecked) View.GONE else View.VISIBLE
        cbEntrega.setOnCheckedChangeListener { _, entrega ->
            etSaldo.visibility = if (entrega) View.GONE else View.VISIBLE
        }
        val etObs = campo("Acuerdos adicionales (opcional)", observaciones).apply {
            setSingleLine(false)
            minLines = 2
        }
        listOf(etObra, etPlazo, etAdelanto, cbEntrega, etSaldo, etObs).forEach { cont.addView(it) }
        AlertDialog.Builder(this)
            .setTitle("Obra y pago")
            .setView(ScrollView(this).apply { addView(cont) })
            .setPositiveButton("Aceptar") { _, _ ->
                direccionObra = etObra.text.toString().trim()
                plazoDias = etPlazo.text.toString().toIntOrNull() ?: 0
                adelanto = etAdelanto.text.toString().replace(",", ".").toDoubleOrNull()
                fechaSaldo = if (cbEntrega.isChecked) "" else etSaldo.text.toString().trim()
                observaciones = etObs.text.toString().trim()
                refrescar()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Las condiciones de siempre del taller: se apuntan una vez y valen para los contratos que vengan. */
    private fun dialogoCondiciones() {
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(8))
        }
        fun numero(hint: String, v: Int) = campo(hint, v.toString()).apply { inputType = InputType.TYPE_CLASS_NUMBER }
        val etGarantia = numero("Garantía (meses; 0 sin garantía)", condiciones.garantiaMeses)
        val etObservar = numero("Días para observar el trabajo", condiciones.diasParaObservar)
        val etPlazo = numero("Plazo de entrega por defecto (días hábiles)", condiciones.plazoDias)
        val etPorc = numero("Adelanto por defecto (% del total)", condiciones.adelantoPorcentaje)
        val etCiudad = campo("Ciudad (dónde se firma y qué jueces)", condiciones.ciudad)
        val igvs = arrayOf("No mencionar el IGV", "El precio incluye IGV", "El precio no incluye IGV")
        val valoresIgv = arrayOf(Igv.SIN_MENCION, Igv.INCLUIDO, Igv.NO_INCLUIDO)
        var igvElegido = condiciones.igv
        val btIgv = Button(this).apply {
            isAllCaps = false
            text = igvs[valoresIgv.indexOf(igvElegido)]
            setOnClickListener {
                AlertDialog.Builder(this@ContratoActivity)
                    .setTitle("IGV")
                    .setSingleChoiceItems(igvs, valoresIgv.indexOf(igvElegido)) { dlg, i ->
                        igvElegido = valoresIgv[i]; text = igvs[i]; dlg.dismiss()
                    }
                    .show()
            }
        }
        cont.addView(TextView(this).apply {
            text = "Valen para este contrato y los siguientes."
            setPadding(dp(4), 0, 0, dp(4))
        })
        listOf(etGarantia, etObservar, etPlazo, etPorc, etCiudad, btIgv).forEach { cont.addView(it) }
        AlertDialog.Builder(this)
            .setTitle("Condiciones del contrato")
            .setView(ScrollView(this).apply { addView(cont) })
            .setPositiveButton("Guardar") { _, _ ->
                val antes = condiciones
                condiciones = CondicionesContrato(
                    plazoDias = etPlazo.text.toString().toIntOrNull() ?: antes.plazoDias,
                    adelantoPorcentaje = (etPorc.text.toString().toIntOrNull() ?: antes.adelantoPorcentaje).coerceIn(0, 100),
                    garantiaMeses = etGarantia.text.toString().toIntOrNull() ?: antes.garantiaMeses,
                    diasParaObservar = etObservar.text.toString().toIntOrNull() ?: antes.diasParaObservar,
                    ciudad = etCiudad.text.toString().trim(),
                    igv = igvElegido
                )
                // Si en este contrato no se tocó el plazo, sigue al de por defecto.
                if (plazoDias == antes.plazoDias) plazoDias = condiciones.plazoDias
                prefs.edit().putString(CLAVE_CONDICIONES, gson.toJson(condiciones)).apply()
                refrescar()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cargarCondiciones(): CondicionesContrato =
        prefs.getString(CLAVE_CONDICIONES, null)?.let {
            runCatching { gson.fromJson(it, CondicionesContrato::class.java) }.getOrNull()
        } ?: CondicionesContrato()

    // ═══════════════════════════ Firmas ═══════════════════════════

    private fun dialogoFirmas() {
        val filas = arrayOf(
            "Firma del cliente  ${if (firmaCliente != null) "✔" else "—"}",
            "Firma del contratista  ${if (firmaContratista() != null) "✔" else "—"}"
        )
        AlertDialog.Builder(this)
            .setTitle("Firmas")
            .setItems(filas) { _, i ->
                if (i == 0) {
                    pedirFirma("Firma del cliente", firmaCliente != null) { firmaCliente = it; refrescar() }
                } else {
                    val c = contratistaActual
                    if (c == null) {
                        Toast.makeText(this, "Primero apunte los datos del contratista", Toast.LENGTH_SHORT).show()
                        mostrarPerfilesContratista()
                        return@setItems
                    }
                    pedirFirma("Firma del contratista", firmaContratista() != null) { bmp ->
                        // La del contratista es la suya en todos sus contratos: va con su perfil.
                        val nuevo = c.copy(firmaBase64 = bmp?.let { ClienteContratoStore.bitmapABase64(it) })
                        ContratistaStore.guardar(this, nuevo)
                        contratistaActual = nuevo
                        refrescar()
                    }
                }
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    /** Firma en un recuadro. Devuelve el dibujo, o null si se quitó. */
    private fun pedirFirma(titulo: String, yaHay: Boolean, alTerminar: (Bitmap?) -> Unit) {
        val firma = FirmaView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(180))
            setBackgroundColor(0xFFEFEFEF.toInt())
        }
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(8))
            addView(TextView(this@ContratoActivity).apply {
                text = if (yaHay) "Ya hay una firma. Firme de nuevo para cambiarla." else "Firme en el recuadro."
                setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.negro))
                setPadding(0, 0, 0, dp(6))
            })
            addView(firma)
            addView(boton("Limpiar") { firma.limpiar() }.apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(6) }
            })
        }
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setView(ScrollView(this).apply { addView(cont) })
            .setPositiveButton("Guardar firma") { _, _ ->
                if (firma.tieneFirma()) {
                    alTerminar(firma.exportarBitmap())
                    Toast.makeText(this, "Firma guardada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No se dibujó ninguna firma", Toast.LENGTH_SHORT).show()
                }
            }
            .setNeutralButton("Quitar firma") { _, _ -> alTerminar(null) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ═══════════════════════════ Contratista ═══════════════════════════

    /** El contratista predeterminado. Migra los datos antiguos (un solo perfil) si existían. */
    private fun cargarContratistaInicial() {
        if (ContratistaStore.listar(this).isEmpty()) {
            val nombre = prefs.getString("usuario_nombre", "").orEmpty()
            if (nombre.isNotBlank()) {
                ContratistaStore.guardar(
                    this,
                    DatosContratista(
                        id = ContratistaStore.nuevoId(),
                        etiqueta = "Mis datos",
                        nombre = nombre,
                        documento = prefs.getString("usuario_documento", "").orEmpty(),
                        direccion = prefs.getString("usuario_direccion", "").orEmpty(),
                        telefono = prefs.getString("usuario_telefono", "").orEmpty(),
                        predeterminado = true
                    )
                )
            }
        }
        contratistaActual = ContratistaStore.predeterminado(this)
    }

    private fun mostrarPerfilesContratista() {
        val perfiles = ContratistaStore.listar(this)
        // Sin ninguno, directo a crearlo: no hay nada que elegir.
        if (perfiles.isEmpty()) {
            editarPerfil(null); return
        }
        val etiquetas = perfiles.map {
            (if (it.id == contratistaActual?.id) "✔ " else "") +
                (if (it.predeterminado) "★ " else "") + it.etiqueta +
                (if (it.nombre.isNotBlank()) " — ${it.nombre}" else " — (sin nombre)")
        }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Contratista")
            .setItems(etiquetas) { _, i -> accionesPerfil(perfiles[i]) }
            .setPositiveButton("Nuevo") { _, _ -> editarPerfil(null) }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun accionesPerfil(p: DatosContratista) {
        val opciones = arrayOf("Usar en este documento", "Editar", "Establecer predeterminado", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle(p.etiqueta)
            .setItems(opciones) { _, i ->
                when (i) {
                    0 -> { contratistaActual = p; refrescar() }
                    1 -> editarPerfil(p)
                    2 -> {
                        ContratistaStore.establecerPredeterminado(this, p.id)
                        contratistaActual = p; refrescar()
                        Toast.makeText(this, "Predeterminado: ${p.etiqueta}", Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        ContratistaStore.eliminar(this, p.id)
                        if (contratistaActual?.id == p.id) contratistaActual = ContratistaStore.predeterminado(this)
                        refrescar()
                        Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun editarPerfil(p: DatosContratista?) {
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(8))
        }
        val etEtiqueta = campo("Etiqueta (ej: Personal, Empresa)", p?.etiqueta ?: "")
        val etNombre = campo("Nombre o razón social", p?.nombre ?: "")
        val etDoc = campo("DNI o RUC", p?.documento ?: "")
        val etDir = campo("Dirección", p?.direccion ?: "")
        val etTel = campo("Teléfono", p?.telefono ?: "").apply { inputType = InputType.TYPE_CLASS_PHONE }
        listOf(etEtiqueta, etNombre, etDoc, etDir, etTel).forEach { cont.addView(it) }
        AlertDialog.Builder(this)
            .setTitle(if (p == null) "Nuevo contratista" else "Editar contratista")
            .setView(ScrollView(this).apply { addView(cont) })
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                if (nombre.isBlank()) {
                    Toast.makeText(this, "Falta el nombre: sin él, el contrato no dice quién se obliga", Toast.LENGTH_LONG).show()
                }
                val nuevo = DatosContratista(
                    id = p?.id ?: ContratistaStore.nuevoId(),
                    etiqueta = etEtiqueta.text.toString().trim().ifBlank { nombre.ifBlank { "Mis datos" } },
                    nombre = nombre,
                    documento = etDoc.text.toString().trim(),
                    direccion = etDir.text.toString().trim(),
                    telefono = etTel.text.toString().trim(),
                    predeterminado = p?.predeterminado ?: false,
                    firmaBase64 = p?.firmaBase64
                )
                ContratistaStore.guardar(this, nuevo)
                if (contratistaActual == null || contratistaActual?.id == nuevo.id) contratistaActual = nuevo
                refrescar()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ═══════════════════════════ PDF y evidencia ═══════════════════════════

    private fun generarPdfYCompartir() {
        val d = datos()
        val faltan = ClausulasContrato.faltantes(d)
        if (faltan.isNotEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Faltan datos")
                .setMessage(
                    faltan.joinToString("\n") { "• $it" } +
                        "\n\nEn el papel saldrán con una línea en blanco para llenar a mano."
                )
                .setPositiveButton("Completar", null)
                .setNegativeButton("Generar igual") { _, _ -> escribirYCompartir(d) }
                .show()
            return
        }
        escribirYCompartir(d)
    }

    private fun escribirYCompartir(d: DatosDelContrato) {
        val prefijo = if (modoDocumento == ModoDocumento.RECIBO) "recibo" else "contrato"
        val clienteArchivo = d.cliente.nombre.ifBlank { "sin_cliente" }.replace(Regex("[^A-Za-z0-9_-]"), "_")
        val fechaArchivo = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val pdfDir = File(cacheDir, "contratos_pdf").apply { mkdirs() }
        val pdfFile = File(pdfDir, "${prefijo}_${clienteArchivo}_$fechaArchivo.pdf")
        val firmaK = firmaContratista()
        val modo = modoDocumento
        Toast.makeText(this, "Generando PDF…", Toast.LENGTH_SHORT).show()

        // Fuera del hilo de la pantalla: el logo del membrete puede venir de la red.
        lifecycleScope.launch {
            val hecho = withContext(Dispatchers.IO) {
                runCatching {
                    val pdf = ContratoPdf(applicationContext, membrete)
                    if (modo == ModoDocumento.RECIBO) pdf.recibo(pdfFile, d, firmaCliente, firmaK)
                    else pdf.contrato(pdfFile, d, firmaCliente, firmaK)
                }.onFailure { android.util.Log.e("ContratoActivity", "No se pudo armar el PDF", it) }
            }
            if (isFinishing || isDestroyed) return@launch
            hecho.onSuccess {
                // Evidencia de aceptación: folio + hash del PDF, guardada aquí y en la nube.
                runCatching {
                    val evidencia = EvidenciaManager.crearEvidenciaPresupuesto(
                        pdfFile, d.cliente.nombre, d.cliente.telefono, d.total.toFloat(), d.folio
                    )
                    EvidenciaManager.guardarEvidenciaJson(this@ContratoActivity, evidencia)
                    evidenciaActual = evidencia
                    EvidenciaManager.subirEvidenciaFirestore(this@ContratoActivity, evidencia) { ok ->
                        if (ok) Toast.makeText(this@ContratoActivity, "Evidencia respaldada en la nube", Toast.LENGTH_SHORT).show()
                    }
                }
                compartirPdf(pdfFile)
            }.onFailure {
                Toast.makeText(this@ContratoActivity, "No se pudo generar el PDF: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun compartirPdf(file: File) {
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Compartir documento"))
    }

    /** Registro manual de aceptación (presencial / WhatsApp / llamada). */
    private fun marcarAceptado() {
        val ev = evidenciaActual
        if (ev == null) {
            Toast.makeText(this, "Primero genere el documento (PDF)", Toast.LENGTH_SHORT).show()
            return
        }
        val canales = arrayOf("Presencial", "WhatsApp", "Llamada")
        AlertDialog.Builder(this)
            .setTitle("¿Cómo aceptó el cliente?")
            .setItems(canales) { _, i ->
                val metodo = canales[i].uppercase(Locale.getDefault())
                runCatching { EvidenciaManager.marcarComoAceptado(this, ev, metodo) }
                    .onSuccess {
                        Toast.makeText(this, "Aceptación registrada ($metodo)", Toast.LENGTH_SHORT).show()
                        EvidenciaManager.subirEvidenciaFirestore(this, ev)
                    }
                    .onFailure {
                        Toast.makeText(this, "No se pudo registrar: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Verifica un PDF: recalcula su huella y la compara con las evidencias guardadas. */
    private fun verificarPdf(uri: Uri) {
        runCatching {
            val hash = contentResolver.openInputStream(uri)?.use {
                EvidenciaManager.calcularHashStream(it)
            } ?: throw IllegalStateException("No se pudo leer el PDF")
            val ev = EvidenciaManager.buscarPorHash(this, hash)
            if (ev != null) {
                val aceptado = if (ev.estado == "ACEPTADO")
                    "\nAceptado: ${ev.fechaAceptacion} (${ev.metodoAceptacion})" else ""
                mostrarDialogo(
                    "✅ Documento íntegro",
                    "Coincide con una evidencia guardada:\n\n" +
                        "Folio: ${ev.idPresupuesto}\nCliente: ${ev.cliente}\n" +
                        "Total: S/ ${ev.total}\nGenerado: ${ev.fechaGeneracion}\n" +
                        "Estado: ${ev.estado}$aceptado"
                )
            } else {
                mostrarDialogo(
                    "❌ Sin coincidencia",
                    "Este PDF no coincide con ninguna evidencia guardada en este equipo. " +
                        "Pudo haber sido modificado, o no se generó aquí."
                )
            }
        }.onFailure {
            Toast.makeText(this, "No se pudo verificar: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /** Respalda todas las evidencias (JSON) en un .zip para compartir o guardar fuera del equipo. */
    private fun exportarEvidencias() {
        val archivos = EvidenciaManager.archivosEvidencia(this)
        if (archivos.isEmpty()) {
            Toast.makeText(this, "No hay evidencias para respaldar", Toast.LENGTH_SHORT).show()
            return
        }
        runCatching {
            val dir = File(cacheDir, "respaldos").apply { mkdirs() }
            val sello = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val zip = File(dir, "evidencias_$sello.zip")
            ZipOutputStream(zip.outputStream()).use { zos ->
                archivos.forEach { f ->
                    zos.putNextEntry(ZipEntry(f.name))
                    f.inputStream().use { it.copyTo(zos) }
                    zos.closeEntry()
                }
            }
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", zip)
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "application/zip"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }, "Respaldar evidencias (${archivos.size})"))
        }.onFailure {
            Toast.makeText(this, "No se pudo respaldar: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarDocumentoActual() {
        refrescar()
        val prefijo = if (modoDocumento == ModoDocumento.RECIBO) "recibo" else "contrato"
        val clienteArchivo = cliente.nombre.ifBlank { "sin_cliente" }.replace(Regex("[^A-Za-z0-9_-]"), "_")
        val fechaArchivo = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val archivo = File(filesDir, "${prefijo}_${clienteArchivo}_$fechaArchivo.txt")
        runCatching { archivo.writeText(tvContrato.text.toString()) }
            .onSuccess { Toast.makeText(this, "Guardado en Baúl: ${archivo.name}", Toast.LENGTH_LONG).show() }
            .onFailure { Toast.makeText(this, "No se pudo guardar: ${it.message}", Toast.LENGTH_SHORT).show() }
    }

    // ═══════════════════════════ Piezas de la vista ═══════════════════════════

    private fun mostrarDialogo(titulo: String, mensaje: String) {
        AlertDialog.Builder(this).setTitle(titulo).setMessage(mensaje).setPositiveButton("OK", null).show()
    }

    private fun campo(hint: String, valor: String = ""): EditText = EditText(this).apply {
        setText(valor)
        this.hint = hint
        setSingleLine(true)
        textSize = 15f
        setPadding(dp(4), dp(10), dp(4), dp(10))
        setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.negro))
    }

    /** Convierte un EditText en selector de fecha: al tocarlo abre un calendario. */
    private fun configurarSelectorFecha(et: EditText) {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        et.isFocusable = false
        et.isClickable = true
        et.setOnClickListener {
            val cal = Calendar.getInstance()
            et.text?.toString()?.takeIf { it.isNotBlank() }?.let { txt ->
                runCatching { formato.parse(txt) }.getOrNull()?.let { cal.time = it }
            }
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    cal.set(year, month, day)
                    et.setText(formato.format(cal.time))
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun boton(texto: String, onClick: (View) -> Unit): Button = Button(this).apply {
        text = texto
        isAllCaps = false
        textSize = 13f
        setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.blanco))
        background = ContextCompat.getDrawable(this@ContratoActivity, R.drawable.fondo_boton_azul)
        setOnClickListener(onClick)
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            .apply { setMargins(dp(4), dp(2), dp(4), dp(2)) }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private enum class ModoDocumento { CONTRATO, RECIBO }

    companion object {
        const val EXTRA_CLIENTE = "contrato_cliente"
        /** El id del cliente en el registro (Room), si se eligió de ahí en la lista. */
        const val EXTRA_CLIENTE_ID = "contrato_cliente_id"
        /** El membrete de la tienda, en JSON: el mismo de la proforma. */
        const val EXTRA_MEMBRETE = "contrato_membrete"
        const val EXTRA_LISTA = "contrato_lista"
        const val EXTRA_TOTAL = "contrato_total"
        const val EXTRA_METROS = "contrato_metros"
        const val EXTRA_PIES = "contrato_pies"
        private const val CLAVE_CONDICIONES = "condiciones_contrato"
    }
}
