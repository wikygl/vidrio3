package crystal.crystal.Diseno

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import crystal.crystal.R
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityBarandaBinding
import crystal.crystal.taller.ModoMasivoHelper
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class BarandaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBarandaBinding
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var primerClickArchivarRealizado = false
    private var ultimoResultado: ResultadoBaranda? = null

    private data class EntradaBaranda(
        val anchoTotal: Float,
        val altoTotal: Float,
        val cantidadVidrios: Int,
        val espesorTuboParante: Float,
        val espesorTuboCabezal: Float,
        val cuello: Float
    )

    private data class ResultadoBaranda(
        val entrada: EntradaBaranda,
        val anchoVidrio: Float,
        val altoVidrio: Float,
        val altoParante: Float,
        val cantidadParantes: Int,
        val cantidadSapitos: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ProyectoManager.inicializarDesdeStorage(this)
        proyectoCallback = ProyectoUIHelper.crearCallbackConActualizacionUI(
            context = this,
            textViewProyecto = binding.tvProyectoActivo,
            activity = this
        )
        ProyectoUIHelper.configurarVisorProyectoActivo(this, binding.tvProyectoActivo)
        procesarIntentProyecto(intent)

        configurarValoresIniciales()
        configurarListeners()
        configurarCliente()
    }

    private fun configurarValoresIniciales() {
        intent.getFloatExtra("ancho", -1f).takeIf { it > 0f }?.let { binding.anchoTotalEditText.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).takeIf { it > 0f }?.let { binding.altoTotalEditText.setText(df1(it)) }
        intent.getFloatExtra("cantidad", -1f).takeIf { it > 0f }?.let {
            binding.cantidadVidriosEditText.setText(it.toInt().coerceAtLeast(1).toString())
        }
        if (binding.cantidadVidriosEditText.text.isNullOrBlank()) binding.cantidadVidriosEditText.setText("1")
        if (binding.espesorTuboParanteEditText.text.isNullOrBlank()) binding.espesorTuboParanteEditText.setText("3.8")
        if (binding.espesorTuboCabezalEditText.text.isNullOrBlank()) binding.espesorTuboCabezalEditText.setText("3.8")
        if (binding.etCuello.text.isNullOrBlank()) binding.etCuello.setText("6")
    }

    private fun configurarListeners() {
        binding.generarButton.setOnClickListener {
            calcularYMostrar()
        }
        binding.btArchivar.setOnClickListener {
            // Candado de suscripción PRIMERO: bloquear antes de avanzar numeración o dar el toast.
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeArchivar(),
                    "Archivar es una función de pago. Renueva para guardar tus proyectos.")) {
                return@setOnClickListener
            }
            if (ultimoResultado == null && !calcularYMostrar()) return@setOnClickListener
            ejecutarFlujoArchivado()
        }
    }

    private fun leerEntrada(): EntradaBaranda? {
        val entrada = EntradaBaranda(
            anchoTotal = binding.anchoTotalEditText.text.toString().toFloatOrNull() ?: 0f,
            altoTotal = binding.altoTotalEditText.text.toString().toFloatOrNull() ?: 0f,
            cantidadVidrios = binding.cantidadVidriosEditText.text.toString().toIntOrNull() ?: 0,
            espesorTuboParante = binding.espesorTuboParanteEditText.text.toString().toFloatOrNull() ?: 0f,
            espesorTuboCabezal = binding.espesorTuboCabezalEditText.text.toString().toFloatOrNull() ?: 0f,
            cuello = binding.etCuello.text.toString().toFloatOrNull() ?: 0f
        )
        return if (validarEntrada(entrada)) entrada else null
    }

    private fun validarEntrada(entrada: EntradaBaranda): Boolean {
        when {
            entrada.anchoTotal <= 0f -> return campoInvalido(binding.anchoTotalEditText, "Ingresa ancho total")
            entrada.altoTotal <= 0f -> return campoInvalido(binding.altoTotalEditText, "Ingresa alto total")
            entrada.cantidadVidrios <= 0 -> return campoInvalido(binding.cantidadVidriosEditText, "Ingresa cantidad de vidrios")
            entrada.espesorTuboParante <= 0f -> return campoInvalido(binding.espesorTuboParanteEditText, "Ingresa tubo parante")
            entrada.espesorTuboCabezal <= 0f -> return campoInvalido(binding.espesorTuboCabezalEditText, "Ingresa tubo cabezal")
            entrada.cuello <= 0f -> return campoInvalido(binding.etCuello, "Ingresa tubo cuello")
        }

        val parante = max(entrada.espesorTuboParante, entrada.cuello)
        val anchoVidrio = calcularAnchoVidrio(entrada, parante)
        val altoVidrio = entrada.altoTotal - entrada.espesorTuboCabezal - 20f
        if (anchoVidrio <= 0f || altoVidrio <= 0f) {
            Toast.makeText(this, "Las medidas no dejan espacio util para vidrio", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun campoInvalido(view: View, mensaje: String): Boolean {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        view.requestFocus()
        return false
    }

    private fun calcularYMostrar(): Boolean {
        val entrada = leerEntrada() ?: return false
        return runCatching {
            val resultado = calcular(entrada)
            ultimoResultado = resultado
            mostrarPreview(resultado)
            mostrarResultados(resultado)
            true
        }.getOrElse {
            Toast.makeText(this, "Error al generar baranda: ${it.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun calcular(entrada: EntradaBaranda): ResultadoBaranda {
        val parante = max(entrada.espesorTuboParante, entrada.cuello)
        return ResultadoBaranda(
            entrada = entrada,
            anchoVidrio = calcularAnchoVidrio(entrada, parante),
            altoVidrio = entrada.altoTotal - entrada.espesorTuboCabezal - 20f,
            altoParante = entrada.altoTotal - entrada.espesorTuboCabezal,
            cantidadParantes = entrada.cantidadVidrios + 1,
            cantidadSapitos = entrada.cantidadVidrios * 4
        )
    }

    private fun calcularAnchoVidrio(entrada: EntradaBaranda, parante: Float): Float {
        val margenCm = 10f
        val sapitoCm = 3f
        val interiorCm = entrada.anchoTotal - 2 * margenCm
        val seccionCm = interiorCm / entrada.cantidadVidrios
        return seccionCm - parante - (2 * sapitoCm)
    }

    private fun mostrarPreview(resultado: ResultadoBaranda) {
        binding.barandaPreviewContainer.removeAllViews()
        binding.barandaPreviewContainer.post {
            binding.barandaPreviewContainer.removeAllViews()
            binding.barandaPreviewContainer.addView(crearVistaBaranda(resultado.entrada))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarResultados(resultado: ResultadoBaranda) {
        val entrada = resultado.entrada
        binding.txReferencias.text = "A:${df1(entrada.anchoTotal)} H:${df1(entrada.altoTotal)} " +
            "V:${entrada.cantidadVidrios} Par:${df1(entrada.espesorTuboParante)} " +
            "Cab:${df1(entrada.espesorTuboCabezal)} Cuello:${df1(entrada.cuello)}"
        binding.txCabezal.text = "${df1(entrada.anchoTotal)} = 1"
        binding.txParante.text = "${df1(resultado.altoParante)} = ${resultado.cantidadParantes}"
        binding.txCuello.text = "10 = ${resultado.cantidadParantes}"
        binding.txVidrios.text = "${df1(resultado.anchoVidrio)} X ${df1(resultado.altoVidrio)} = ${entrada.cantidadVidrios}"
        binding.txAccesorios.text = "Sapito = ${resultado.cantidadSapitos}"
        binding.lyResultados.visibility = View.VISIBLE
    }

    private fun crearVistaBaranda(entrada: EntradaBaranda): ViewGroup {
        val contW = binding.barandaPreviewContainer.width.toFloat().takeIf { it > 0f } ?: 1f
        val contH = binding.barandaPreviewContainer.height.toFloat().takeIf { it > 0f } ?: 1f
        val escala = min(contW / entrada.anchoTotal, contH / entrada.altoTotal) * 0.9f

        val anchoPx = (entrada.anchoTotal * escala).toInt()
        val altoPx = (entrada.altoTotal * escala).toInt()
        val cabezalAltPx = (entrada.espesorTuboCabezal * escala).toInt()
        val cuerpoAltPx = altoPx - cabezalAltPx

        val cabezal = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(anchoPx, cabezalAltPx)
            setBackgroundColor(ContextCompat.getColor(this@BarandaActivity, android.R.color.darker_gray))
        }

        val area = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(anchoPx, altoPx)
            orientation = LinearLayout.VERTICAL
        }
        area.addView(cabezal)

        val margenCm = 10f
        val margenPx = (margenCm * escala).toInt()
        val interiorCm = entrada.anchoTotal - 2 * margenCm
        val interiorPxF = interiorCm * escala
        val segmentF = interiorPxF / entrada.cantidadVidrios

        val paranteCm = max(entrada.espesorTuboParante, entrada.cuello)
        val parantePx = (paranteCm * escala).toInt()
        val sapitoCm = 3f
        val sapitoPx = (sapitoCm * escala).toInt()
        val alturaCollarPx = (10f * escala).toInt()
        val altoVidPx = ((entrada.altoTotal - entrada.espesorTuboCabezal - 20f) * escala).toInt()
        val colorTubo = ContextCompat.getColor(this, android.R.color.darker_gray)
        val colorVidrio = ContextCompat.getColor(this, android.R.color.holo_blue_light)
        val sapitoDrawable = ContextCompat.getDrawable(this, R.drawable.sapito)!!

        val cuerpo = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(anchoPx, cuerpoAltPx)
        }

        val posiciones = FloatArray(entrada.cantidadVidrios + 1) { i ->
            if (i == entrada.cantidadVidrios) {
                margenPx + interiorPxF - paranteCm * escala
            } else {
                margenPx + segmentF * i
            }
        }

        for (i in 0..entrada.cantidadVidrios) {
            val xPar = posiciones[i].roundToInt()
            val contPar = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = FrameLayout.LayoutParams(parantePx, cuerpoAltPx).apply {
                    leftMargin = xPar
                }
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            }
            contPar.addView(View(this).apply {
                layoutParams = LinearLayout.LayoutParams((entrada.cuello * escala).toInt(), alturaCollarPx)
                setBackgroundColor(colorTubo)
            })
            contPar.addView(View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (entrada.espesorTuboParante * escala).toInt(),
                    cuerpoAltPx - alturaCollarPx
                )
                setBackgroundColor(colorTubo)
            })
            cuerpo.addView(contPar)

            if (i < entrada.cantidadVidrios) {
                val nextX = if (i + 1 == entrada.cantidadVidrios) {
                    (margenPx + interiorPxF).roundToInt()
                } else {
                    posiciones[i + 1].roundToInt()
                }
                val anchoSeccion = nextX - xPar - parantePx
                val contVid = FrameLayout(this).apply {
                    layoutParams = FrameLayout.LayoutParams(anchoSeccion, cuerpoAltPx).apply {
                        leftMargin = xPar + parantePx
                    }
                }
                contVid.addView(View(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        anchoSeccion - 2 * sapitoPx,
                        altoVidPx
                    ).apply {
                        leftMargin = sapitoPx
                        topMargin = alturaCollarPx
                    }
                    alpha = 0.3f
                    setBackgroundColor(colorVidrio)
                })

                val anchoImg = (6f * escala).toInt()
                val altoImg = (4f * escala).toInt()
                val offsetY = (2.5f * escala).toInt()
                listOf(
                    sapitoPx to alturaCollarPx + offsetY,
                    anchoSeccion - sapitoPx - anchoImg to alturaCollarPx + offsetY,
                    sapitoPx to alturaCollarPx + altoVidPx - offsetY - altoImg,
                    anchoSeccion - sapitoPx - anchoImg to alturaCollarPx + altoVidPx - offsetY - altoImg
                ).forEachIndexed { idx, (lm, tm) ->
                    contVid.addView(ImageView(this).apply {
                        layoutParams = FrameLayout.LayoutParams(anchoImg, altoImg).apply {
                            leftMargin = lm
                            topMargin = tm
                        }
                        setImageDrawable(sapitoDrawable)
                        if (idx % 2 == 1) rotation = 180f
                    })
                }
                cuerpo.addView(contVid)
            }
        }

        area.addView(cuerpo)
        return area
    }

    private fun ejecutarFlujoArchivado() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            devolverResultadoMasivo()
            return
        }
        if (!primerClickArchivarRealizado) {
            abrirDialogoSeleccionProyectoParaArchivar()
            return
        }
        if (!ProyectoManager.hayProyectoActivo()) {
            primerClickArchivarRealizado = false
            Toast.makeText(this, "No hay proyecto activo. Selecciona uno.", Toast.LENGTH_SHORT).show()
            abrirDialogoSeleccionProyectoParaArchivar()
            return
        }
        archivarMapas()
    }

    private fun abrirDialogoSeleccionProyectoParaArchivar() {
        DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, callbackSeleccionProyectoParaArchivar())
    }

    private fun callbackSeleccionProyectoParaArchivar(): DialogosProyecto.ProyectoCallback {
        return object : DialogosProyecto.ProyectoCallback {
            override fun onProyectoSeleccionado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                val mapExistente = MapStorage.cargarProyecto(this@BarandaActivity, nombreProyecto)
                mapListas.clear()
                if (mapExistente != null) mapListas.putAll(mapExistente)
                archivarMapas()
            }

            override fun onProyectoCreado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                archivarMapas()
            }

            override fun onProyectoEliminado(nombreProyecto: String) {
                refrescarProyectoActivoUI()
            }
        }
    }

    private fun archivarMapas() {
        val resultado = ultimoResultado ?: return
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
            mapListas.clear()
            if (mapExistente != null) mapListas.putAll(mapExistente)
        }

        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        var ultimoID = ""
        repeat(cant) {
            val siguienteNumero = ProyectoManager.obtenerSiguienteContadorPorPrefijo(this, PREFIJO)
            val paqueteID = "$PREFIJO$siguienteNumero"
            ultimoID = paqueteID

            archivarConNombre("Referencias", binding.txReferencias.text.toString(), paqueteID, esReferencia = true)
            archivarConNombre("Tubo Cabezal", binding.txCabezal.text.toString(), paqueteID)
            archivarConNombre("Tubo Parante", binding.txParante.text.toString(), paqueteID)
            archivarConNombre("Tubo Cuello", binding.txCuello.text.toString(), paqueteID)
            archivarConNombre("Vidrios", binding.txVidrios.text.toString(), paqueteID)
            archivarConNombre("Accesorios", binding.txAccesorios.text.toString(), paqueteID)

            ProyectoManager.actualizarContadorPorPrefijo(this, PREFIJO, siguienteNumero)
        }

        MapStorage.guardarMap(this, mapListas)
        refrescarProyectoActivoUI()
        Toast.makeText(this, "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()
        binding.anchoTotalEditText.setText("")
        ultimoResultado = resultado
    }

    private fun archivarConNombre(nombre: String, texto: String, paqueteID: String, esReferencia: Boolean = false) {
        if (texto.isBlank()) return
        val entradas = if (esReferencia) {
            mutableListOf(mutableListOf(texto, "", paqueteID, clienteActual()))
        } else {
            parsearTexto(texto, paqueteID)
        }
        if (entradas.isNotEmpty()) {
            mapListas.getOrPut(nombre) { mutableListOf() }.addAll(entradas)
        }
    }

    private fun parsearTexto(texto: String, paqueteID: String): MutableList<MutableList<String>> {
        val resultado = mutableListOf<MutableList<String>>()
        for (linea in texto.split("\n")) {
            val l = linea.trim()
            if (l.isEmpty()) continue
            val partes = l.split("=")
            if (partes.size == 2) {
                val valor = partes[0].trim()
                val cantidad = partes[1].trim()
                if (valor.isBlank() || cantidad.isBlank()) continue
                val cantidadNum = cantidad.toIntOrNull()
                if (cantidadNum == null || cantidadNum == 0) continue
                val valorNum = valor.toFloatOrNull()
                if (valorNum != null && valorNum == 0f) continue
                resultado.add(mutableListOf(valor, cantidad, paqueteID, clienteActual()))
            } else {
                resultado.add(mutableListOf(l, "", paqueteID, clienteActual()))
            }
        }
        return resultado
    }

    private fun devolverResultadoMasivo() {
        if (ultimoResultado == null && !calcularYMostrar()) return
        val perfiles = mapOf(
            "Tubo Cabezal" to texto(binding.txCabezal),
            "Tubo Parante" to texto(binding.txParante),
            "Tubo Cuello" to texto(binding.txCuello)
        ).filter { it.value.isNotBlank() }
        val accesorios = mapOf(
            "Accesorios" to texto(binding.txAccesorios)
        ).filter { it.value.isNotBlank() }
        ModoMasivoHelper.devolverResultado(
            activity = this,
            calculadora = "Baranda",
            perfiles = perfiles,
            vidrios = texto(binding.txVidrios),
            accesorios = accesorios,
            referencias = texto(binding.txReferencias)
        )
    }

    private fun texto(textView: TextView): String = textView.text?.toString()?.trim().orEmpty()

    private fun configurarCliente() {
        val cliente = clienteActual()
        val proyectoActual = ProyectoManager.getProyectoActivo() ?: ""
        if (proyectoActual.isNotEmpty()) primerClickArchivarRealizado = true
        if (cliente.isBlank()) return
        if (proyectoActual.contains(cliente, ignoreCase = true)) return

        val callbackCliente = object : DialogosProyecto.ProyectoCallback {
            override fun onProyectoSeleccionado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                refrescarProyectoActivoUI()
            }

            override fun onProyectoCreado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                refrescarProyectoActivoUI()
            }

            override fun onProyectoEliminado(nombreProyecto: String) {
                refrescarProyectoActivoUI()
            }
        }

        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoCrearProyecto(this, callbackCliente, cliente)
        } else {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cliente: $cliente")
                .setMessage("Proyecto activo: \"$proyectoActual\".\nQue deseas hacer?")
                .setPositiveButton("Mantener") { d, _ ->
                    primerClickArchivarRealizado = true
                    d.dismiss()
                }
                .setNegativeButton("Crear nuevo") { _, _ ->
                    DialogosProyecto.mostrarDialogoCrearProyecto(this, callbackCliente, cliente)
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun refrescarProyectoActivoUI() {
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let { ProyectoUIHelper.agregarOpcionesMenuProyecto(it) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val manejado = ProyectoUIHelper.manejarSeleccionMenu(
            context = this,
            itemId = item.itemId,
            callback = proyectoCallback,
            onProyectoCambiado = { refrescarProyectoActivoUI() }
        )
        return if (manejado) true else super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        procesarIntentProyecto(intent)
    }

    override fun onResume() {
        super.onResume()
        refrescarProyectoActivoUI()
    }

    private fun procesarIntentProyecto(intent: Intent) {
        val nombreProyecto = intent.getStringExtra("proyecto_nombre")
        val crearNuevo = intent.getBooleanExtra("crear_proyecto", false)
        val descripcionProyecto = intent.getStringExtra("proyecto_descripcion") ?: ""
        if (crearNuevo && !nombreProyecto.isNullOrEmpty()) {
            if (MapStorage.crearProyecto(this, nombreProyecto, descripcionProyecto)) {
                ProyectoManager.setProyectoActivo(this, nombreProyecto)
                refrescarProyectoActivoUI()
                Toast.makeText(this, "Proyecto '$nombreProyecto' creado y activado", Toast.LENGTH_SHORT).show()
            }
        } else if (!nombreProyecto.isNullOrEmpty()) {
            if (MapStorage.existeProyecto(this, nombreProyecto)) {
                ProyectoManager.setProyectoActivo(this, nombreProyecto)
                refrescarProyectoActivoUI()
                Toast.makeText(this, "Proyecto '$nombreProyecto' activado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clienteActual(): String = intent.getStringExtra("rcliente").orEmpty()

    private fun df1(v: Float): String {
        return if (v % 1 == 0f) v.toInt().toString()
        else "%.1f".format(v).replace(",", ".")
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            devolverResultadoMasivo()
            return
        }
        super.onBackPressed()
    }

    companion object {
        private const val PREFIJO = "B"
    }
}
