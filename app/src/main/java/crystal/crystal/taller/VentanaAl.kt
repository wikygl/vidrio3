package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.Diseno.estructurada.ParametrosEstructurada
import crystal.crystal.Diseno.estructurada.VistaEstructurada
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityVentanaAlBinding
import crystal.crystal.taller.nova.NovaCalculos


class VentanaAl : AppCompatActivity() {

    private var serieActual: Serie? = null
    private var indices = 0
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()

    private var primerClickArchivarRealizado = false
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback
    private var cliente: String = ""

    // Datos estructurados de Serie 3825 (para uso en otras vistas/exportaciones).
    // El campo `corte` no se renderiza en esta actividad.
    private var rielSup3825: MedidaPerfil? = null
    private var rielInf3825: MedidaPerfil? = null
    private var marcoFijo3825: MedidaPerfil? = null
    private var marcoMovil3825: MedidaPerfil? = null
    private var paranteFijo3825: MedidaPerfil? = null
    private var enganche3825: MedidaPerfil? = null
    private var zocalo3825: MedidaPerfil? = null

    private lateinit var binding : ActivityVentanaAlBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVentanaAlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ProyectoManager.inicializarDesdeStorage(this)
        proyectoCallback = ProyectoUIHelper.crearCallbackConActualizacionUI(
            context = this,
            textViewProyecto = binding.tvProyectoActivo,
            activity = this
        )
        ProyectoUIHelper.configurarVisorProyectoActivo(this, binding.tvProyectoActivo)
        procesarIntentProyecto(intent)
        configurarCliente()

        binding.btnCalcularE.setOnClickListener {
            marco()
            parante()
            zocalo()
            vidrios()
            riel()
            tope()
            junkillo()
            puente()
            divMocheta(mPuentes1())
            nMocheta()
            binding.tvReferencias.text = "Ancho = ${binding.etAncho.text}, Alto = ${binding.etAlto.text}\n" +
                    "Div=${divisiones()} -> Fjs=${nFijos()} -> Crzas=${nCorredizas()}\n" +
                    "hHoja = ${df1(altoHoja())}"
            binding.tvPruebas.text=divisiones().toString()

            // Ocultar layouts si sus TextViews están vacíos
            actualizarVisibilidadLayouts()
            renderizarPreviewEstructurada()
        }
        binding.ivModelo.setOnClickListener {
            actualizarVentana()
            renderizarPreviewEstructurada()
        }

        binding.btArchivar.setOnClickListener {
            if (binding.etAncho.text.toString().isEmpty()) {
                Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación: las medidas deben coincidir con las referencias
            val anchoET = binding.etAncho.text.toString()
            val altoET = binding.etAlto.text.toString()
            val referencias = binding.tvReferencias.text.toString()
            if (!referencias.contains(anchoET) || !referencias.contains(altoET)) {
                Toast.makeText(this, "Las medidas no coinciden con las referencias. Recalcula.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!primerClickArchivarRealizado) {
                DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                    override fun onProyectoSeleccionado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true
                        binding.txC.text = nombreProyecto
                        val mapExistente = MapStorage.cargarProyecto(this@VentanaAl, nombreProyecto)
                        if (mapExistente != null) {
                            mapListas.clear()
                            mapListas.putAll(mapExistente)
                        }
                        ejecutarArchivado()
                    }
                    override fun onProyectoCreado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true
                        binding.txC.text = nombreProyecto
                        ejecutarArchivado()
                    }
                    override fun onProyectoEliminado(nombreProyecto: String) {
                        ProyectoUIHelper.actualizarVisorProyectoActivo(this@VentanaAl, binding.tvProyectoActivo)
                    }
                })
            } else {
                if (!ProyectoManager.hayProyectoActivo()) {
                    primerClickArchivarRealizado = false
                    Toast.makeText(this, "No hay proyecto activo. Selecciona uno.", Toast.LENGTH_SHORT).show()
                    DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                        override fun onProyectoSeleccionado(nombreProyecto: String) {
                            primerClickArchivarRealizado = true
                            binding.txC.text = nombreProyecto
                            val mapExistente = MapStorage.cargarProyecto(this@VentanaAl, nombreProyecto)
                            if (mapExistente != null) {
                                mapListas.clear()
                                mapListas.putAll(mapExistente)
                            }
                            ejecutarArchivado()
                        }
                        override fun onProyectoCreado(nombreProyecto: String) {
                            primerClickArchivarRealizado = true
                            binding.txC.text = nombreProyecto
                            ejecutarArchivado()
                        }
                        override fun onProyectoEliminado(nombreProyecto: String) {
                            ProyectoUIHelper.actualizarVisorProyectoActivo(this@VentanaAl, binding.tvProyectoActivo)
                        }
                    })
                    return@setOnClickListener
                }
                binding.txC.text = ProyectoManager.getProyectoActivo() ?: ""
                ejecutarArchivado()
            }
        }

        binding.btArchivar.setOnLongClickListener {
            if (!ProyectoManager.hayProyectoActivo()) {
                Toast.makeText(this, "No hay proyecto activo para guardar", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
            MapStorage.guardarMap(this, mapListas)
            Toast.makeText(this, "Map guardado en proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()
            ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
            true
        }

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAncho.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAlto.setText(df1(it)) }
    }

    // ==================== MENÚ ====================
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let { ProyectoUIHelper.agregarOpcionesMenuProyecto(it) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val manejado = ProyectoUIHelper.manejarSeleccionMenu(
            context = this,
            itemId = item.itemId,
            callback = proyectoCallback,
            onProyectoCambiado = {
                ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
            }
        )
        return if (manejado) true else super.onOptionsItemSelected(item)
    }

    // ==================== PROYECTO ====================
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        procesarIntentProyecto(intent)
    }

    override fun onResume() {
        super.onResume()
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
    }

    private fun procesarIntentProyecto(intent: Intent) {
        val nombreProyecto = intent.getStringExtra("proyecto_nombre")
        val crearNuevo = intent.getBooleanExtra("crear_proyecto", false)
        val descripcionProyecto = intent.getStringExtra("proyecto_descripcion") ?: ""
        if (crearNuevo && !nombreProyecto.isNullOrEmpty()) {
            if (MapStorage.crearProyecto(this, nombreProyecto, descripcionProyecto)) {
                ProyectoManager.setProyectoActivo(this, nombreProyecto)
                ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
                Toast.makeText(this, "Proyecto '$nombreProyecto' creado y activado", Toast.LENGTH_SHORT).show()
            }
        } else if (!nombreProyecto.isNullOrEmpty()) {
            if (MapStorage.existeProyecto(this, nombreProyecto)) {
                ProyectoManager.setProyectoActivo(this, nombreProyecto)
                ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
                Toast.makeText(this, "Proyecto '$nombreProyecto' activado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ==================== CLIENTE ====================
    @SuppressLint("SetTextI18n")
    private fun configurarCliente() {
        val clienteNombre = intent.extras?.getString("rcliente")
        cliente = clienteNombre ?: ""

        val proyectoActual = ProyectoManager.getProyectoActivo() ?: ""
        if (proyectoActual.isNotEmpty()) {
            binding.txC.text = proyectoActual
            primerClickArchivarRealizado = true
        }

        if (cliente.isEmpty()) return
        if (proyectoActual.contains(cliente, ignoreCase = true)) return

        val callbackCliente = object : DialogosProyecto.ProyectoCallback {
            override fun onProyectoSeleccionado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                binding.txC.text = nombreProyecto
                ProyectoUIHelper.actualizarVisorProyectoActivo(this@VentanaAl, binding.tvProyectoActivo)
            }
            override fun onProyectoCreado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                binding.txC.text = nombreProyecto
                ProyectoUIHelper.actualizarVisorProyectoActivo(this@VentanaAl, binding.tvProyectoActivo)
            }
            override fun onProyectoEliminado(nombreProyecto: String) {
                ProyectoUIHelper.actualizarVisorProyectoActivo(this@VentanaAl, binding.tvProyectoActivo)
            }
        }

        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoCrearProyecto(this, callbackCliente, cliente)
        } else {
            AlertDialog.Builder(this)
                .setTitle("Cliente: $cliente")
                .setMessage("Proyecto activo: \"$proyectoActual\".\n¿Qué deseas hacer?")
                .setPositiveButton("Mantener") { d, _ ->
                    primerClickArchivarRealizado = true
                    binding.txC.text = proyectoActual
                    d.dismiss()
                }
                .setNegativeButton("Crear nuevo") { _, _ ->
                    DialogosProyecto.mostrarDialogoCrearProyecto(this, callbackCliente, cliente)
                }
                .setCancelable(false)
                .show()
        }
    }

    // ==================== ARCHIVAR ====================
    private fun obtenerPrefijo(): String = "Va"

    private fun ejecutarArchivado() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            // Modo masivo: la respuesta se devuelve en onBackPressed; aquí basta con archivar.
            archivarMapas()
            return
        }
        archivarMapas()
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
        Toast.makeText(this, "Archivado", Toast.LENGTH_SHORT).show()
        binding.etAncho.setText("")
        binding.etAlto.setText("")
    }

    // FUNCIONES PARA RECUPERAR ESTADO DE MODELO
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIndex",indices)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        indices = savedInstanceState.getInt("currentIndex", 0)
    }

    // FUNCIONES REDONDEOS
    private fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    // FUNCIONES ALUMINIOS

    @SuppressLint("SetTextI18n")
    private fun parante() {
        if (serieActual?.nombre == "Serie 3825") {
            val pf = altoHoja() - 1.2f
            val en = altoHoja() - 2f
            paranteFijo3825 = MedidaPerfil(pf, nFijos() * 2)
            enganche3825 = MedidaPerfil(en, nCorredizas() * 2)
            binding.tvParante.text = if (divisiones() == 0) {
                "${df1(en)} = ${nCorredizas() * 2}"
            } else {
                "${df1(pf)} = ${nFijos() * 2}\n${df1(en)} = ${nCorredizas() * 2}"
            }
            return
        }
        val pe = paran() + 1.4f
        binding.tvParante.text = if (divisiones() == 0) {
            "${df1(paran())} = ${nCorredizas() * 2}"
        } else {
            "${df1(pe)} = ${nFijos() * 2}\n${df1(paran())} = ${nCorredizas() * 2}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun marco(){
        if (serieActual?.nombre == "Serie 3825") {
            val alto = binding.etAlto.text.toString().toFloat()
            val marcoVal = binding.etMarco.text.toString().toFloatOrNull() ?: 0f
            val sinMarco = marcoVal == 0f
            val anchU = anchUtil()                 // ancho - 2 * marco
            val altoU = altoUtil()                 // alto  - 2 * marco
            // Sin marco: el Marco Serie cubre toda la altura interior.
            // Con marco: solo recorre la zona del alto de hoja (la mocheta usa la lógica de Clásica).
            val marcoSerieLat = if (sinMarco) altoU - 1.0f else altoHoja() - 1.0f
            val esPar = divisiones() % 2 == 0

            rielSup3825 = MedidaPerfil(anchU, 1)
            rielInf3825 = MedidaPerfil(anchU, 1)
            if (esPar) {
                marcoFijo3825 = MedidaPerfil(marcoSerieLat, 1, corte = "un lado en 4ª")
                marcoMovil3825 = MedidaPerfil(marcoSerieLat, 1, corte = "un lado en 4ª")
            } else {
                marcoFijo3825 = MedidaPerfil(marcoSerieLat, 2, corte = "un lado en 4ª")
                marcoMovil3825 = null
            }

            if (sinMarco) {
                binding.txMarco.text = "Marco"
                binding.tvMarco.text = ""
            } else {
                binding.txMarco.text = "Marco ${binding.etMarco.text}"
                binding.tvMarco.text = "${df1(alto)} = 2\n${df1(anchU)} = 2"
            }
            binding.txJamba.text = "Marco Serie"
            binding.tvJamba.text = "${df1(marcoSerieLat)} = 2"
            binding.tvRielS.text = "${df1(anchU)} = 1"
            binding.tvRielI.text = "${df1(anchU)} = 1"
            return
        }
        val alto = binding.etAlto.text.toString().toFloat()
        binding.txMarco.text = "Marco"
        binding.tvMarco.text= "${df1(alto)} = 2\n${df1(anchUtil())} = 2"
        binding.txJamba.text = "Jamba"
        binding.tvJamba.text = ""
        binding.tvRielS.text = ""
        binding.tvRielI.text = ""
        rielSup3825 = null
        rielInf3825 = null
        marcoFijo3825 = null
        marcoMovil3825 = null
    }
    @SuppressLint("SetTextI18n")
    private fun zocalo() {
        if (serieActual?.nombre == "Serie 3825") {
            val div = divisiones().coerceAtLeast(1)
            val anchU = anchUtil()
            val z = ((anchU - 1.4f) - (div + 1) * 2.4f) / div
            zocalo3825 = MedidaPerfil(z, div * 2)
            binding.tvZocalo.text = "${df1(z)} = ${div * 2}"
            return
        }
        zocalo3825 = null
        // Verificar queserieActual no sea nula
       serieActual?.let { serieActual ->
            val z = zoc()  // Se asume que zoc() retorna un Float
            // Utiliza la medida de la serie actual para el cálculo
            val adjustedZ = z - (2 * serieActual.medida.toFloat())

            binding.tvZocalo.text = "${df1(adjustedZ)} = ${divisiones() * 2}"
        } ?: run {
            // Manejar el caso en queserieActual sea nula
            binding.tvZocalo.text = "Error: No se ha seleccionado una serie"
        }
    }

    @SuppressLint("SetTextI18n")

    private fun riel(){
        if (serieActual?.nombre == "Serie 3825") {
            // En Serie 3825 los rieles van en lyRielS / lyRielI (ya cargados en marco()).
            binding.tvRiel.text = ""
            return
        }
        val riel= anchUtil()
        binding.tvRiel.text = "${df1(riel)} = 2"
    }
    
    @SuppressLint("SetTextI18n")
    private fun tope(){
        val t= altoUtil()
        val tc= paran()
        binding.tvTope.text = if (divisiones()==0){"${df1(t)} = 2"}else{"${df1(tc)} = 1"}
    }

    private fun junkillo() {
        val jun = binding.etJunki.text.toString().toFloatOrNull() ?: 0f

        if (serieActual?.nombre == "Serie 3825") {
            // En Serie 3825 el junquillo aplica únicamente a las mochetas.
            if (altoHoja() >= altoUtil()) {
                binding.tvJunki.text = ""
                return
            }
            val tuboMocheta = altoUtil() - (altoHoja() + 2.5f)
            val mP1 = mPuentes1()
            val nSub1 = NovaCalculos.anchMota(mP1)         // ceil(mP1 / 180), mín. 1
            val anchoSub1 = if (nSub1 > 0) mP1 / nSub1 else mP1
            // Por cada panel de mocheta: 2 junquillos horizontales (sup+inf) y 2 verticales (izq+der).
            val qtyPanelesPrinc = nPuentes() * nSub1 * 2

            val lineas = mutableListOf<String>()
            if (qtyPanelesPrinc > 0) {
                lineas.add("${df1(anchoSub1)} = $qtyPanelesPrinc")
                lineas.add("${df1(tuboMocheta - (2 * jun))} = $qtyPanelesPrinc")
            }
            if (divisiones() == 10 || divisiones() == 14) {
                val mP2 = mPuentes2()
                if (mP2 > 0f) {
                    val nSub2 = NovaCalculos.anchMota(mP2)
                    val anchoSub2 = if (nSub2 > 0) mP2 / nSub2 else mP2
                    val qtySec = nSub2 * 2
                    if (qtySec > 0) lineas.add("${df1(anchoSub2)} = $qtySec")
                }
            }
            binding.tvJunki.text = lineas.joinToString("\n")
            return
        }

        val tuboMocheta = altoUtil() - (altoHoja() + 2.5f)
        binding.tvJunki.text = if (divisiones() == 10 || divisiones() == 14) {
            "${df1(divMocheta(mPuentes1()))} = ${(nTuboMocheta(mPuentes1()) + 1) * 2 * (nPuentes() - nTuboMocheta(mPuentes1()))}\n" +
                    "${df1(divMocheta(mPuentes2()))} = ${(nTuboMocheta(mPuentes2()) + 1) * 2}\n" +
                    "${df1(tuboMocheta - (2 * jun))} = ${nPuentes() * nTuboMocheta(mPuentes1()) * 2}"
        } else {
            "${df1(divMocheta(mPuentes1()))} = ${(nTuboMocheta(mPuentes1()) + 1) * 2 * (nPuentes() - nTuboMocheta(mPuentes1()))}\n" +
                    "${df1(tuboMocheta - (2 * jun))} = ${nPuentes() * nTuboMocheta(mPuentes1()) * 2}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun puente() {
        val tuboMocheta = altoUtil() - (altoHoja() + 2.5f)

        if (serieActual?.nombre == "Serie 3825") {
            // En Serie 3825 los tubos verticales de mocheta se calculan con la regla de 180.
            val mP1 = mPuentes1()
            val nTubosMocheta = (NovaCalculos.anchMota(mP1) - 1).coerceAtLeast(0)
            val lineas = mutableListOf<String>()
            if (nPuentes() > 0) {
                lineas.add("${df1(mP1)} = ${nPuentes()}")
            }
            val qtyTubos = nPuentes() * nTubosMocheta
            if (qtyTubos > 0) {
                lineas.add("${df1(tuboMocheta)} = $qtyTubos")
            }
            binding.tvTubo.text = lineas.joinToString("\n")
            binding.lyTubo.visibility = if (altoHoja() >= altoUtil()) View.GONE else View.VISIBLE
            binding.tvPruebas.text = mP1.toString()
            binding.txPruebas.text = mPuentes2().toString()
            return
        }

        binding.tvTubo.text = when {
            (divisiones() == 10 || (divisiones() == 14 && nTuboMocheta(mPuentes2()) == 0)) -> {
                "${df1(mPuentes1())} = ${nPuentes() - 1}\n" +
                        "${df1(mPuentes2())} = ${nPuentes() - 2}\n" +
                        "${df1(tuboMocheta)} = ${nPuentes() * nTuboMocheta(mPuentes1())}"
            }
            (divisiones() == 10 || (divisiones() == 14 && nTuboMocheta(mPuentes2()) != 0)) -> {
                "${df1(mPuentes1())} = ${nPuentes() - 1}\n" +
                        "${df1(mPuentes2())} = ${nPuentes() - 2}"
            }
            (divisiones() == 10 || (divisiones() == 14 && nTuboMocheta(mPuentes1()) == 0)) -> {
                "${df1(mPuentes1())} = ${nPuentes()}"
            }
            else -> {
                "${df1(mPuentes1())} = ${nPuentes()}\n" +
                        "${df1(tuboMocheta)} = ${nPuentes() * nTuboMocheta(mPuentes1())}"
            }
        }

        binding.lyTubo.visibility = if (altoHoja() >= altoUtil()) View.GONE else View.VISIBLE
        binding.tvPruebas.text = mPuentes1().toString()
        binding.txPruebas.text = mPuentes2().toString()
    }

    // FUNCIONES VIDRIOS

    private fun vidrios() {
        if (serieActual?.nombre == "Serie 3825") {
            val div = divisiones().coerceAtLeast(1)
            val anchU = anchUtil()
            val zoc3825 = ((anchU - 1.4f) - (div + 1) * 2.4f) / div
            val anchoVidrio = zoc3825 + 1.5f
            val altoFijo = (altoHoja() - 1.2f) - 4.5f
            val altoCorr = (altoHoja() - 2f) - 4.5f
            binding.tvVidriosR.text = if (altoHoja() <= altoUtil()) {
                "${df1(anchoVidrio)} x ${df1(altoFijo)} = ${nFijos()}\n" +
                        "${df1(anchoVidrio)} x ${df1(altoCorr)} = ${nCorredizas()}\n" +
                        "${df1((altoUtil() - altoHoja()) - 2.9f)} x ${df1(divMocheta(mPuentes1()))} = ${nPuentes()}"
            } else {
                "${df1(anchoVidrio)} x ${df1(altoFijo)} = ${nFijos()}\n" +
                        "${df1(anchoVidrio)} x ${df1(altoCorr)} = ${nCorredizas()}"
            }
            return
        }
        val ancho = (zoc() - (2 * 3.0f)) + 1.5f
        val alto = paran() - 7.0f
        val ale = 1 + alto
        binding.tvVidriosR.text = if (altoHoja() <= altoUtil()) {
            "${df1(ancho)} x ${df1(ale)} = ${nFijos()}\n" +
                    "${df1(ancho)} x ${df1(alto)} = ${nCorredizas()}\n" +
                    "${df1((altoUtil() - altoHoja()) - 2.9f)} x ${divMocheta(mPuentes1())} = ${nPuentes()}"
        } else {
            "${df1(ancho)} x ${df1(ale)} = ${nFijos()}\n" +
                    "${df1(ancho)} x ${df1(alto)} = ${nCorredizas()}"
        }
    }

    //  CAMBIOS DE SERIE
    @SuppressLint("SetTextI18n")
    private fun actualizarVentana() {
        if (listaSeries.isNotEmpty()) {
            // Asigna la serie actual
           serieActual = listaSeries[indices]
            binding.txVentana.text = "Ventana de aluminio ${serieActual?.nombre}"
            binding.txRiel.text = when (serieActual?.nombre) {
                "Clásica" -> "Riel"
                "ClásicaG" -> "Riel"
                "Serie 20" -> "Riel Sup."
                "Serie 3825" -> "D. Riel Sup."
                "Serie 35" -> "D. Riel Sup."
                "Serie Española" -> "D. Riel Sup."
                else -> ""
            }

            // Incrementar el índice para la próxima selección (si es necesario)
            indices = (indices + 1) % listaSeries.size
        } else {
            binding.txVentana.text = "Sin ventanas disponibles"
        }
    }

    private fun visibleVentana(){
        val series = listaSeries[indices]
        val nombre = series.nombre
        when (nombre){
            "Clásica" -> {
                binding.lyMarco.visibility = View.GONE
                binding.lyJamba.visibility = View.GONE
                binding.lyTubo.visibility = View.GONE
                binding.lyParante.visibility = View.GONE
                binding.lyRielI.visibility = View.GONE
                binding.lyZocalo.visibility = View.GONE
                binding.lyRielS.visibility = View.GONE
                binding.lyTraslapo.visibility=View.GONE
                binding.lyCabezal.visibility=View.GONE
                binding.lyAdaptador.visibility=View.GONE
            }
            "Serie 20"-> {
                binding.lyMarco.visibility = View.GONE
                binding.lyJunki.visibility = View.GONE
                binding.lyTubo.visibility = View.GONE
                binding.lyParante.visibility = View.GONE
                binding.lyRiel.visibility = View.GONE
                binding.lyZocalo.visibility = View.GONE
                binding.lyTope.visibility = View.GONE
            }
        "Serie 3825",
        "Serie 35",
        "Serie Española"->{}
        }
    }

    //   FUNCIONES SERIE 2O

    //FUNCIONES DE ARCHIVO
    private fun archivarMapas() {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
            mapListas.clear()
            if (mapExistente != null) mapListas.putAll(mapExistente)
        }

        val prefijo = obtenerPrefijo()
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)

        for (u in 1..cant) {
            val siguienteNumero = ProyectoManager.obtenerSiguienteContadorPorPrefijo(this, prefijo)
            val identificadorPaquete = "${prefijo}${siguienteNumero}"

            // En VentanaAl la convención es: tx = etiqueta, tv = datos.
            // procesarArchivar* espera (label, data).
            if (esValido(binding.lyMarco)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txMarco, binding.tvMarco, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyParante)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txParante, binding.tvParante, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyZocalo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txZocalo, binding.tvZocalo, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyRiel)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txRiel, binding.tvRiel, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyRielS)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txRielS, binding.tvRielS, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyRielI)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txRielI, binding.tvRielI, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyJamba)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txJamba, binding.tvJamba, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyTubo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txTubo, binding.tvTubo, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyJunki)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txJunki, binding.tvJunki, mapListas, identificadorPaquete)
            }

            // Cliente: archivar directamente (no usa formato "valor = cantidad")
            val clienteTexto = binding.txC.text.toString()
            if (clienteTexto.isNotBlank()) {
                val entradaCliente = mutableListOf(clienteTexto, "", identificadorPaquete)
                mapListas.getOrPut("Cliente") { mutableListOf() }.add(entradaCliente)
            }
        }

        if (proyectoActivo != null) {
            MapStorage.guardarMap(this, mapListas)
        }
    }
    // Función para verificar si un Layout es visible o tiene estado GONE
    private fun esValido(ly: LinearLayout): Boolean {
        return ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
    }

    // Oculta layouts si el TextView de resultado está vacío
    private fun actualizarVisibilidadLayouts() {
        binding.lyMarco.visibility = if (binding.tvMarco.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyParante.visibility = if (binding.tvParante.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyZocalo.visibility = if (binding.tvZocalo.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyRiel.visibility = if (binding.tvRiel.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyRielS.visibility = if (binding.tvRielS.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyRielI.visibility = if (binding.tvRielI.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyJamba.visibility = if (binding.tvJamba.text.isNullOrBlank()) View.GONE else View.VISIBLE
        // lyTubo ya se maneja en puente() según condición de altura
        binding.lyJunki.visibility = if (binding.tvJunki.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyTope.visibility = if (binding.tvTope.text.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    //   FUNCIONES GENERALES
    private fun anchUtil(): Float {
        val ancho = binding.etAncho.text.toString().toFloat()
        val marco = binding.etMarco.text.toString().toFloat()
        return ancho - (2 * marco)

    }

    private fun altoUtil(): Float {
        val alto = binding.etAlto.text.toString().toFloat()
        val marco = binding.etMarco.text.toString().toFloat()
        return alto - (2 * marco)
    }

    private fun zoc(): Float {
        val div = divisiones()
        val cruce = when (div) {
            2, 3, 5, 7, 9, 11, 13, 15 -> div - 1
            4, 6, 10 -> div - 2
            8, 12 -> div / 2
            14 -> div - 4
            else -> div
        }
        val partes = ((anchUtil() - ((nPuentes() - 1) * 2.5f)) + (cruce * 3.2f)) / div
        return if (div == 1) anchUtil() else partes
    }

    private fun paran(): Float {
        return altoHoja() - 1.4f
    }
    private fun nTuboMocheta(p1: Float): Int {
        return (p1 / 120.4f).toInt()
    }

    private fun nFijos():Int {
        return when (divisiones()){
            1 -> 1  2 -> 1
            3 -> 2  4 -> 2
            5 -> 3
            6 -> 4  7 -> 4
            8 -> 4
            9 -> 5
            10 ->6  11 -> 6  12 -> 6
            13 ->7  14 ->8   15 -> 8
            else -> 0
        }
    }
    private fun nCorredizas():Int {
        return when (divisiones()){
            1 -> 0
            2 -> 1   3 -> 1
            4 -> 2   5 -> 2   6 -> 2
            7 -> 3
            8 -> 4   9 -> 4   10 -> 4
            11-> 5
            12-> 6   13-> 6   14 -> 6
            15-> 7
            else -> 0
        }
    }
    private fun nPuentes(): Int {
        return when (divisiones()) {
            1, 2, 3, 4, 5 -> 1
            6, 8 -> 2
            7, 9, 11, 13, 15 -> 1
            10, 12, 14 -> 3
            else -> 0
        }
    }

    private fun mPuentes1(): Float {
        val ancho = anchUtil()
        return when (divisiones()) {
            1, 2, 3, 4, 5, 7, 9, 11, 13, 15 -> ancho
            6, 8 -> (ancho - 2.5f) / 2
            10 -> ((ancho - (2 * 2.5f)) / divisiones()) * 3
            12 -> (ancho - (2 * 2.5f)) / 3
            14 -> ((ancho - (2 * 2.5f)) / divisiones()) * 5
            else -> 0f
        }
    }

    private fun mPuentes2(): Float {
        val ancho = anchUtil()
        return when (divisiones()) {
            10, 14 -> ((ancho - (2 * 2.5f)) / divisiones()) * 4
            else -> 0f
        }
    }
    private fun divMocheta(p1: Float): Float {
        val multi = p1 / 120.4f
        val multiplo = multi.toInt() + 1
        return p1 / multiplo
    }
    private fun nMocheta() {
        val n = binding.etMocheta.text.toString().toFloat()
        val xn = if (n == 0f) { // Reemplazado ColumnText.GLOBAL_SPACE_CHAR_RATIO por 0f
            nTuboMocheta(mPuentes1()).toString()
        } else {
            n.toString()
        }
        binding.txPruebas.text = xn
    }
    private fun altoHoja(): Float {
        val alto = altoUtil()
        val hoja = binding.etAltohoja.text.toString().toFloat()
        val corre = if (hoja > alto) alto else hoja
        return if (hoja == 0f) { // Reemplazado ColumnText.GLOBAL_SPACE_CHAR_RATIO por 0f
            (alto / 7) * 5
        } else {
            corre
        }
    }

    private fun divisiones(): Int {
        val ancho = binding.etAncho.text.toString().toFloatOrNull() ?: 0f
        val divis = binding.etPartes.text.toString().toIntOrNull() ?: 0
        return if (divis == 0) {
            ((ancho / 60f).toInt() + if (ancho % 60f > 0f) 1 else 0)
        } else {
            divis
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            // Solo incluir perfiles cuyos layouts sean visibles
            val perfiles = mutableMapOf<String, String>()
            if (esValido(binding.lyParante)) perfiles["Parante"] = ModoMasivoHelper.texto(binding.tvParante)
            if (esValido(binding.lyZocalo)) perfiles["Zócalo"] = ModoMasivoHelper.texto(binding.tvZocalo)
            if (esValido(binding.lyRiel)) perfiles["Riel"] = ModoMasivoHelper.texto(binding.tvRiel)
            if (esValido(binding.lyRielS)) perfiles["Riel Sup."] = ModoMasivoHelper.texto(binding.tvRielS)
            if (esValido(binding.lyRielI)) perfiles["Riel Inf."] = ModoMasivoHelper.texto(binding.tvRielI)
            if (esValido(binding.lyJamba)) perfiles[binding.txJamba.text.toString()] = ModoMasivoHelper.texto(binding.tvJamba)
            if (esValido(binding.lyTubo)) perfiles["Tubo"] = ModoMasivoHelper.texto(binding.tvTubo)

            val accesorios = mutableMapOf<String, String>()
            if (esValido(binding.lyTope)) accesorios["Tope"] = ModoMasivoHelper.texto(binding.tvTope)
            if (esValido(binding.lyJunki)) accesorios["Junquillo"] = ModoMasivoHelper.texto(binding.tvJunki)

            ModoMasivoHelper.devolverResultado(
                activity = this,
                calculadora = "Ventana Aluminio",
                perfiles = perfiles,
                vidrios = ModoMasivoHelper.texto(binding.tvVidriosR),
                accesorios = accesorios,
                referencias = ModoMasivoHelper.texto(binding.tvReferencias)
            )
            return
        }
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }

    private fun renderizarPreviewEstructurada() {
        val ancho = binding.etAncho.text?.toString()?.replace(",", ".")?.toFloatOrNull() ?: 0f
        val alto = binding.etAlto.text?.toString()?.replace(",", ".")?.toFloatOrNull() ?: 0f
        if (ancho <= 0f || alto <= 0f) return

        val divsCuerpo = divisiones().coerceAtLeast(1)
        val hojaAlto = runCatching { altoHoja() }.getOrDefault(0f)
        val altoTransom = (alto - hojaAlto).let { if (it <= 8f) alto * 0.18f else it }
        val serie = serieActual?.nombre?.removePrefix("Serie ")?.trim() ?: "3825"

        val params = ParametrosEstructurada(
            anchoCm = ancho,
            altoCm = alto,
            altoTransomCm = altoTransom,
            divisionesTransom = NovaCalculos.anchMota(ancho),
            divisionesCuerpo = divsCuerpo,
            serie = serie,
            vista = null
        )

        val viewW = binding.ivModelo.width.takeIf { it > 0 } ?: (resources.displayMetrics.density * 220).toInt()
        val viewH = binding.ivModelo.height.takeIf { it > 0 } ?: (resources.displayMetrics.density * 90).toInt()
        val vista = VistaEstructurada(this).apply {
            aplicar(params)
            measure(
                View.MeasureSpec.makeMeasureSpec(viewW, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(viewH, View.MeasureSpec.EXACTLY)
            )
            layout(0, 0, viewW, viewH)
        }
        val bitmap = Bitmap.createBitmap(viewW, viewH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        vista.draw(canvas)
        binding.ivModelo.setImageBitmap(bitmap)
    }
}

data class Serie(val nombre: String, val medida: String, val zocalo: String)

/**
 * Medida estructurada de un perfil. `corte` describe un corte especial
 * (p. ej. "un lado en 4ª") y se conserva para usos posteriores; esta
 * actividad no lo muestra al usuario.
 */
data class MedidaPerfil(
    val medida: Float,
    val cantidad: Int,
    val corte: String? = null
)

val listaSeries = listOf(
    Serie("Clásica", "3", "8"),
    Serie("ClásicaG", "3.6", "8"),
    Serie("Serie 20", "7", "8."),
    Serie("Serie 3825", "3.9", "8."),
    Serie("Serie 35", "4.4", "8"),
    Serie("Serie Española", "8", "8")
)


//serie 20 2 hojas
// vidrio = anccho/2 -5 x alto - 10
//zócalo y cabezal = ancho/2 - 6.4
//riel sup e inf = ancho- 1.2
//parante, traslape = alto - 2.7