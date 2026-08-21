package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityDivisionBanoBinding

class DivisionBanoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDivisionBanoBinding
    private lateinit var controladorCola: ControladorColaMedidas
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var primerClickArchivarRealizado = false
    private data class EntradaCalculo(
        val ancho: Float,
        val alto: Float,
        val nCubiculos: Int,
        val profundidad: Float,
        val anchoPuerta: Float,
        val alturaDesague: Float,
        val uMarco: Float
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDivisionBanoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ProyectoManager.inicializarDesdeStorage(this)
        proyectoCallback = ProyectoUIHelper.crearCallbackConActualizacionUI(
            context = this,
            textViewProyecto = binding.tvProyectoActivo,
            activity = this
        )
        ProyectoUIHelper.configurarVisorProyectoActivo(this, binding.tvProyectoActivo)
        procesarIntentProyecto(intent)

        configurarCalcular()
        configurarArchivar()
        configurarCliente()

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAncho.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAltoTotal.setText(df1(it)) }

        controladorCola = ControladorColaMedidas(
            activity = this,
            claseActual = DivisionBanoActivity::class.java,
            etAncho = binding.etAncho,
            etAlto = binding.etAltoTotal,
            ivDiseno = binding.ivDiseno,
            formato = ::df1
        )
        controladorCola.inicializar()
    }

    // ==================== OBTENER VALORES ====================

    private fun leerEntradaCalculo(): EntradaCalculo {
        return EntradaCalculo(
            ancho = binding.etAncho.text.toString().toFloatOrNull() ?: 0f,
            alto = binding.etAltoTotal.text.toString().toFloatOrNull() ?: 0f,
            nCubiculos = binding.etCantidadCubiculos.text.toString().toIntOrNull() ?: 0,
            profundidad = binding.etProfundidad.text.toString().toFloatOrNull() ?: 0f,
            anchoPuerta = binding.etAnchoPuerta.text.toString().toFloatOrNull() ?: 0f,
            alturaDesague = binding.etAlturaDesague.text.toString().toFloatOrNull() ?: 0f,
            uMarco = binding.etMedidaAluminio.text.toString().toFloatOrNull() ?: 1.5f
        )
    }

    private fun refrescarProyectoActivoUI() {
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
    }

    // ==================== CALCULAR ====================

    @SuppressLint("SetTextI18n")
    private fun configurarCalcular() {
        binding.btCalcular.setOnClickListener {
            try {
                controladorCola.onCalcular()
                if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnClickListener

                val entrada = leerEntradaCalculo()

                if (
                    entrada.ancho <= 0 || entrada.alto <= 0 || entrada.nCubiculos <= 0 ||
                    entrada.profundidad <= 0 || entrada.anchoPuerta <= 0 || entrada.alturaDesague <= 0
                ) {
                    Toast.makeText(this, "Ingrese datos válidos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val resultado = calcular(entrada.ancho, entrada.alto, entrada.nCubiculos)
                mostrarResultados(
                    r = resultado,
                    ancho = entrada.ancho,
                    alto = entrada.alto,
                    profundidad = entrada.profundidad,
                    nCubiculos = entrada.nCubiculos,
                    anchoPuerta = entrada.anchoPuerta,
                    alturaDesague = entrada.alturaDesague,
                    tubo = entrada.uMarco
                )
                mostrarReferencias(
                    ancho = entrada.ancho,
                    alto = entrada.alto,
                    profundidad = entrada.profundidad,
                    nCubiculos = entrada.nCubiculos,
                    anchoPuerta = entrada.anchoPuerta,
                    alturaDesague = entrada.alturaDesague,
                    medidaAluminio = entrada.uMarco
                )
                generarDisenoTop(entrada)
            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarResultados(
        r: Resultado,
        ancho: Float,
        alto: Float,
        profundidad: Float,
        nCubiculos: Int,
        anchoPuerta: Float,
        alturaDesague: Float,
        tubo: Float
    ) {
        val df = ::df1
        val anchoCubiculo = calcularAnchoCubiculo(
            anchoTotal = ancho,
            nCubiculos = nCubiculos,
            anchoParante = tubo
        )

        val qtyAltoMenosTubo = if (anchoPuerta >= anchoCubiculo) nCubiculos else nCubiculos * 2
        val mostrarLineaCubiculoPuerta = anchoPuerta < anchoCubiculo

        binding.tvParante.text = "Tubo"
        binding.txParante.text = buildString {
            append("${df(ancho - tubo)} = 1")
            append("\n${df(alto)} = ${nCubiculos + 1}")
            append("\n${df(alto - tubo)} = $qtyAltoMenosTubo")
            append("\n${df(alto - alturaDesague - tubo - 0.5f)} = ${nCubiculos * 2}")
            append("\n${df(profundidad - (2 * tubo))} = ${nCubiculos * 2}")
            append("\n${df(anchoPuerta - ((2 * tubo) + 1))} = ${nCubiculos * 2}")
            if (mostrarLineaCubiculoPuerta) {
                append("\n${df(anchoCubiculo - (anchoPuerta + tubo))} = $nCubiculos")
            }
        }
        binding.tvRielSup.text = "Tope"
        binding.txRielSup.text = buildString {
            append("${df(alto - (alturaDesague - tubo))} = ${nCubiculos * 2}")
            append("\n${df(anchoPuerta)} = $nCubiculos")
        }
        binding.txRielInf.text = "${df(r.rielInferiorMedida)} = 1"

        val qtyJunkilloAlto = if (anchoPuerta < anchoCubiculo) nCubiculos * 4 else nCubiculos * 2
        binding.tvU.text = "Junkillo"
        binding.txU.text = buildString {
            append("${df(alto - (alturaDesague + (2 * tubo)))} = $qtyJunkilloAlto")
            append("\n${df(alto - (alturaDesague + (3 * tubo) + 0.5f))} = ${nCubiculos * 2}")
            append("\n${df(profundidad - (2 * tubo))} = ${nCubiculos * 2}")
            append("\n${df(anchoPuerta - ((2 * tubo) + 1))} = ${nCubiculos * 2}")
            if (anchoPuerta < anchoCubiculo) {
                append("\n${df(anchoCubiculo - (anchoPuerta + tubo))} = ${nCubiculos * 2}")
            }
        }
        binding.txFelpa.text = "${df(r.felpaMetros)} mts"
        binding.tvV.text = "Panel"
        binding.txV.text = buildString {
            append("${df(alto - (alturaDesague + (2 * tubo) + 0.5f))} X ${df(profundidad - ((2 * tubo) + 0.5f))} = $nCubiculos")
            append("\n${df(anchoPuerta - ((2 * tubo) + 1 + 0.5f))} X ${df(alto - (alturaDesague + (3 * tubo) + 0.5f + 0.5f))} = $nCubiculos")
            if (anchoPuerta < anchoCubiculo) {
                append("\n${df(alto - (alturaDesague + (2 * tubo) + 0.5f))} X ${df(anchoCubiculo - (anchoPuerta + tubo + 0.5f))} = $nCubiculos")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarReferencias(
        ancho: Float,
        alto: Float,
        profundidad: Float,
        nCubiculos: Int,
        anchoPuerta: Float,
        alturaDesague: Float,
        medidaAluminio: Float
    ) {
        val df = ::df1
        val anchoCubiculo = calcularAnchoCubiculo(
            anchoTotal = ancho,
            nCubiculos = nCubiculos,
            anchoParante = medidaAluminio
        )
        val profundidadCubiculo = profundidad - (2 * medidaAluminio)
        binding.txReferencias.text = "A:${df(ancho)} H:${df(alto)} P:${df(profundidad)} " +
                "C:$nCubiculos Puerta:${df(anchoPuerta)} D:${df(alturaDesague)} Tubo:${df(medidaAluminio)} " +
                "Cubículo:${df(anchoCubiculo)} x ${df(profundidadCubiculo)}"
    }

    // ==================== DISEÑO TOP (vista superior) ====================

    /**
     * Vista en planta de la división de baño. Cada cubículo aloja un inodoro; los separa un muro de
     * aluminio de 3.8 que se une, al frente, a columnas del mismo ancho. Si la puerta es >= al ancho
     * del cubículo va una sola columna (la puerta ocupa el frente); si es menor, se agrega una columna
     * intermedia y un panel fijo que completa el frente.
     */
    private fun generarDisenoTop(e: EntradaCalculo) {
        val muro = ANCHO_PARANTE            // 3.8 (aluminio)
        val n = e.nCubiculos
        if (n <= 0 || e.ancho <= 0 || e.profundidad <= 0) { binding.ivDiseno.setImageResource(crystal.crystal.R.drawable.divbano); return }
        val anchoCub = (e.ancho - (n + 1) * muro) / n
        if (anchoCub <= 0) { binding.ivDiseno.setImageResource(crystal.crystal.R.drawable.divbano); return }
        val sep = muro                       // 3.8: columna/espacio entre la separación y la puerta
        val dispo = anchoCub - sep           // frente disponible desde la columna de bisagra
        val fixedW = dispo - e.anchoPuerta - sep
        val conFijo = e.anchoPuerta < anchoCub && fixedW > 2f
        val puertaW = if (conFijo) minOf(e.anchoPuerta, dispo) else dispo

        val d = resources.displayMetrics.density
        val margen = 10f * d
        val padLbl = 40f * d
        val titAlto = 34f * d
        val screenW = resources.displayMetrics.widthPixels.toFloat()
        val availW = screenW - 2 * margen - 2 * padLbl
        val maxDepthPx = screenW * 0.70f
        val scale = minOf(availW / e.ancho, maxDepthPx / e.profundidad)
        val planW = e.ancho * scale
        val planH = e.profundidad * scale

        val bmpW = (planW + 2 * padLbl + 2 * margen).toInt().coerceAtLeast(1)
        val bmpH = (titAlto + planH + 2 * padLbl + margen).toInt().coerceAtLeast(1)
        val bmp = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888)
        val cv = Canvas(bmp); cv.drawColor(Color.WHITE)

        val ox = margen + padLbl
        val oy = titAlto + padLbl * 0.4f
        fun px(x: Float) = ox + x * scale
        fun py(y: Float) = oy + y * scale     // y=0 fondo (arriba), y=prof frente (abajo)
        val yFront = e.profundidad

        // Paints
        val pTit = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#2E4A62"); textSize = 15f * d; typeface = android.graphics.Typeface.DEFAULT_BOLD; textAlign = Paint.Align.CENTER }
        val pFloor = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F5F8FA") }
        val pAlum = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#AEB6BD") }
        val pAlumBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#6B7580"); style = Paint.Style.STROKE; strokeWidth = 1f * d }
        val pCol = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#54606B") }
        val pDoor = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); style = Paint.Style.STROKE; strokeWidth = 2.4f * d }
        val pArc = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#90CAF9"); style = Paint.Style.STROKE; strokeWidth = 1.2f * d }
        val pFijo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#2E7D32"); style = Paint.Style.STROKE; strokeWidth = 3.6f * d }
        val pToi = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#ECEFF1") }
        val pToiB = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#78909C"); style = Paint.Style.STROKE; strokeWidth = 1.6f * d }
        val pBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#90A4AE"); style = Paint.Style.STROKE; strokeWidth = 1.4f * d }
        val pDim = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#37474F"); strokeWidth = 1f * d; textSize = 11f * d }

        cv.drawText("División de baño · vista superior", bmpW / 2f, titAlto * 0.62f, pTit)

        // Cubículos: piso + inodoro
        for (i in 0 until n) {
            val cubL = (i + 1) * muro + i * anchoCub
            cv.drawRect(px(cubL), py(0f), px(cubL + anchoCub), py(yFront), pFloor)
            dibujarInodoro(cv, px(cubL + anchoCub / 2f), py(0f), scale, anchoCub, e.profundidad, pToi, pToiB)
        }

        // Muros (divisores) a toda la profundidad + columna al frente
        for (j in 0..n) {
            val wL = j * (anchoCub + muro)
            cv.drawRect(px(wL), py(0f), px(wL + muro), py(yFront), pAlum)
            cv.drawRect(px(wL), py(0f), px(wL + muro), py(yFront), pAlumBorde)
            cv.drawRect(px(wL), py(yFront - muro), px(wL + muro), py(yFront), pCol)  // columna
        }

        // Borde del contorno (fondo + laterales; el frente lo definen columnas/puerta/panel)
        cv.drawLine(px(0f), py(0f), px(e.ancho), py(0f), pBorde)
        cv.drawLine(px(0f), py(0f), px(0f), py(yFront), pBorde)
        cv.drawLine(px(e.ancho), py(0f), px(e.ancho), py(yFront), pBorde)

        // Columna de 3.8 entre la separación y la puerta; puerta con arco de giro; y (si la puerta es
        // menor que el cubículo) columna intermedia + panel fijo que completa el frente.
        val rDraw = minOf(puertaW, e.profundidad * 0.85f) * scale
        for (i in 0 until n) {
            val cubL = (i + 1) * muro + i * anchoCub
            val doorL = cubL + sep
            cv.drawRect(px(cubL), py(yFront - muro), px(cubL + muro), py(yFront), pCol) // columna de bisagra (3.8)
            val hx = px(doorL); val hy = py(yFront)
            cv.drawArc(RectF(hx - rDraw, hy - rDraw, hx + rDraw, hy + rDraw), 0f, -90f, false, pArc)
            cv.drawLine(hx, hy, hx, hy - rDraw, pDoor) // hoja abierta hacia adentro
            if (conFijo) {
                val colIx = doorL + puertaW
                cv.drawRect(px(colIx), py(yFront - muro), px(colIx + muro), py(yFront), pCol) // columna intermedia
                cv.drawLine(px(colIx + muro), py(yFront), px(cubL + anchoCub), py(yFront), pFijo) // panel fijo
            }
        }

        // ── Cotas ──
        pDim.textAlign = Paint.Align.CENTER
        val c0L = muro; val c0R = muro + anchoCub
        val yc = py(yFront) + 16f * d
        cv.drawLine(px(c0L), yc, px(c0R), yc, pDim)
        cv.drawText("Cubículo ${df1(anchoCub)}", (px(c0L) + px(c0R)) / 2f, yc + 13f * d, pDim)
        val yp = py(yFront) + 32f * d
        val d0 = muro + sep   // inicio de la puerta del primer cubículo
        cv.drawLine(px(d0), yp, px(d0 + puertaW), yp, pDim)
        cv.drawText("Puerta ${df1(puertaW)}", (px(d0) + px(d0 + puertaW)) / 2f, yp + 13f * d, pDim)
        val xd = px(0f) - 16f * d
        val midY = (py(0f) + py(yFront)) / 2f
        cv.drawLine(xd, py(0f), xd, py(yFront), pDim)
        cv.save(); cv.rotate(-90f, xd, midY)
        cv.drawText("Prof ${df1(e.profundidad)}", xd, midY - 5f * d, pDim)
        cv.restore()

        binding.ivDiseno.setImageBitmap(bmp)
    }

    /** Inodoro en vista superior (tanque + taza), escalado para caber en el cubículo. */
    private fun dibujarInodoro(cv: Canvas, cx: Float, backY: Float, sc: Float, cubAncho: Float, prof: Float, fill: Paint, borde: Paint) {
        val gapCm = 6f; val tanqueCm = 15f; val tazaCm = 40f
        val ts = minOf(1f, (cubAncho * 0.62f) / 36f, (prof * 0.82f) / (gapCm + tanqueCm + tazaCm))
        if (ts <= 0f) return
        val tanqueW = 36f * sc * ts; val tanqueH = tanqueCm * sc * ts
        val tazaW = 33f * sc * ts; val tazaH = tazaCm * sc * ts
        val top = backY + gapCm * sc * ts
        val tq = RectF(cx - tanqueW / 2f, top, cx + tanqueW / 2f, top + tanqueH)
        cv.drawRoundRect(tq, 4f, 4f, fill); cv.drawRoundRect(tq, 4f, 4f, borde)
        val tz = RectF(cx - tazaW / 2f, top + tanqueH, cx + tazaW / 2f, top + tanqueH + tazaH)
        cv.drawOval(tz, fill); cv.drawOval(tz, borde)
    }

    // ==================== ARCHIVAR ====================
    private fun callbackSeleccionProyectoParaArchivar(): DialogosProyecto.ProyectoCallback {
        return object : DialogosProyecto.ProyectoCallback {
            override fun onProyectoSeleccionado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                val mapExistente = MapStorage.cargarProyecto(this@DivisionBanoActivity, nombreProyecto)
                if (mapExistente != null) {
                    mapListas.clear()
                    mapListas.putAll(mapExistente)
                }
                ejecutarArchivado()
            }

            override fun onProyectoCreado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                ejecutarArchivado()
            }

            override fun onProyectoEliminado(nombreProyecto: String) {
                refrescarProyectoActivoUI()
            }
        }
    }

    private fun abrirDialogoSeleccionProyectoParaArchivar() {
        DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, callbackSeleccionProyectoParaArchivar())
    }

    private fun configurarArchivar() {
        binding.btArchivar.setOnClickListener {
            // Candado de suscripción PRIMERO: bloquear antes de avanzar numeración o dar el toast.
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeArchivar(),
                    "Archivar es una función de pago. Renueva para guardar tus proyectos.")) {
                return@setOnClickListener
            }
            if (binding.etAncho.text.toString().isEmpty()) {
                Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!primerClickArchivarRealizado) {
                abrirDialogoSeleccionProyectoParaArchivar()
            } else {
                if (!ProyectoManager.hayProyectoActivo()) {
                    primerClickArchivarRealizado = false
                    Toast.makeText(this, "No hay proyecto activo. Selecciona uno.", Toast.LENGTH_SHORT).show()
                    abrirDialogoSeleccionProyectoParaArchivar()
                    return@setOnClickListener
                }
                ejecutarArchivado()
            }
        }
    }

    private fun ejecutarArchivado() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            devolverResultadoMasivo()
            return
        }
        archivarMapas()
        refrescarProyectoActivoUI()
        Toast.makeText(this, "Archivado", Toast.LENGTH_SHORT).show()
        binding.etAncho.setText("")
        controladorCola.ofrecerSiguiente()
    }

    private fun procesarLineaArchivado(
        contenedor: View,
        titulo: TextView,
        texto: TextView,
        identificadorPaquete: String,
        esReferencia: Boolean = false
    ) {
        if (contenedor.visibility == View.GONE) return
        if (esReferencia) {
            ListaCasilla.procesarReferenciasConPrefijo(this, titulo, texto, mapListas, identificadorPaquete)
        } else {
            ListaCasilla.procesarArchivarConPrefijo(this, titulo, texto, mapListas, identificadorPaquete)
        }
    }

    private fun archivarMapas() {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
            mapListas.clear()
            if (mapExistente != null) mapListas.putAll(mapExistente)
        }

        val prefijo = "Db"
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        var ultimoID = ""

        for (u in 1..cant) {
            val siguienteNumero = ProyectoManager.obtenerSiguienteContadorPorPrefijo(this, prefijo)
            val identificadorPaquete = "$prefijo$siguienteNumero"
            ultimoID = identificadorPaquete

            procesarLineaArchivado(binding.lyReferencias, binding.tvReferencias, binding.txReferencias, identificadorPaquete, esReferencia = true)
            procesarLineaArchivado(binding.lyParante, binding.tvParante, binding.txParante, identificadorPaquete)
            procesarLineaArchivado(binding.lyRielSup, binding.tvRielSup, binding.txRielSup, identificadorPaquete)
            procesarLineaArchivado(binding.lyRielInf, binding.tvRielInf, binding.txRielInf, identificadorPaquete)
            procesarLineaArchivado(binding.lyU, binding.tvU, binding.txU, identificadorPaquete)
            procesarLineaArchivado(binding.lyFelpa, binding.tvFelpa, binding.txFelpa, identificadorPaquete)
            procesarLineaArchivado(binding.lyVidrios, binding.tvV, binding.txV, identificadorPaquete)

            ProyectoManager.actualizarContadorPorPrefijo(this, prefijo, siguienteNumero)
        }

        MapStorage.guardarMap(this, mapListas)
        refrescarProyectoActivoUI()
        val msg = if (cant > 1) "Archivadas $cant unidades en proyecto: ${ProyectoManager.getProyectoActivo()}"
                  else "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun devolverResultadoMasivo() {
        val perfiles = mapOf(
            ModoMasivoHelper.texto(binding.tvParante) to ModoMasivoHelper.texto(binding.txParante),
            ModoMasivoHelper.texto(binding.tvRielSup) to ModoMasivoHelper.texto(binding.txRielSup),
            ModoMasivoHelper.texto(binding.tvRielInf) to ModoMasivoHelper.texto(binding.txRielInf),
            ModoMasivoHelper.texto(binding.tvU) to ModoMasivoHelper.texto(binding.txU)
        ).filter { it.value.isNotBlank() }
        val accesorios = mapOf(
            "Felpa" to ModoMasivoHelper.texto(binding.txFelpa)
        ).filter { it.value.isNotBlank() }
        ModoMasivoHelper.devolverResultado(
            activity = this,
            calculadora = "Division Baño",
            perfiles = perfiles,
            vidrios = ModoMasivoHelper.texto(binding.txV),
            accesorios = accesorios,
            referencias = ModoMasivoHelper.texto(binding.txReferencias)
        )
    }

    // ==================== CLIENTE ====================

    private fun callbackProyectoCliente(): DialogosProyecto.ProyectoCallback {
        return object : DialogosProyecto.ProyectoCallback {
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
    }

    @SuppressLint("SetTextI18n")
    private fun configurarCliente() {
        val clienteNombre = intent.extras?.getString("rcliente")
        val cliente = clienteNombre ?: ""
        val proyectoActual = ProyectoManager.getProyectoActivo() ?: ""
        if (proyectoActual.isNotEmpty()) {
            primerClickArchivarRealizado = true
        }
        if (cliente.isEmpty()) return
        if (proyectoActual.contains(cliente, ignoreCase = true)) return

        val callbackCliente = callbackProyectoCliente()

        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoCrearProyecto(this, callbackCliente, cliente)
        } else {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cliente: $cliente")
                .setMessage("Proyecto activo: \"$proyectoActual\".\n¿Qué deseas hacer?")
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

    // ==================== PROYECTO ====================

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
                refrescarProyectoActivoUI()
            }
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

    // ==================== CALCULOS ====================

    data class Resultado(
        val anchoPanelVidrio: Float,
        val altoPanelVidrio: Float,
        val numPaneles: Int,
        val paranteMedida: Float,
        val paranteQty: Int,
        val rielSuperiorMedida: Float,
        val rielInferiorMedida: Float,
        val uMedida: Float,
        val uQty: Int,
        val felpaMetros: Float
    )

    private fun calcularAnchoCubiculo(
        anchoTotal: Float,
        nCubiculos: Int,
        anchoParante: Float,
        parantesExtra: Int = 0
    ): Float {
        if (nCubiculos <= 0) return 0f
        val totalParantes = nCubiculos + parantesExtra
        return (anchoTotal - totalParantes * anchoParante) / nCubiculos
    }

    private fun calcular(anchoTotal: Float, alto: Float, numPaneles: Int): Resultado {
        val numParantes = numPaneles + 1
        val anchoPanelBruto = calcularAnchoCubiculo(
            anchoTotal = anchoTotal,
            nCubiculos = numPaneles,
            anchoParante = ANCHO_PARANTE,
            parantesExtra = 1
        )
        val anchoPanelVidrio = anchoPanelBruto - 2 * HOLGURA
        val altoPanelVidrio = alto - ALTO_RIEL - ALTO_RIEL - 2 * HOLGURA

        val paranteMedida = alto
        val rielSuperior = anchoTotal
        val rielInferior = anchoTotal
        val uMedida = anchoPanelBruto
        val uQty = numPaneles * 2

        val perimetroPorPanel = 2 * (anchoPanelBruto + alto)
        val felpaMetros = perimetroPorPanel * numPaneles / 100f

        return Resultado(
            anchoPanelVidrio = anchoPanelVidrio,
            altoPanelVidrio = altoPanelVidrio,
            numPaneles = numPaneles,
            paranteMedida = paranteMedida,
            paranteQty = numParantes,
            rielSuperiorMedida = rielSuperior,
            rielInferiorMedida = rielInferior,
            uMedida = uMedida,
            uQty = uQty,
            felpaMetros = felpaMetros
        )
    }

    private fun df1(v: Float): String {
        return if (v % 1 == 0f) v.toInt().toString()
        else "%.1f".format(v).replace(",", ".")
    }

    // ==================== BACK ====================

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            devolverResultadoMasivo()
            return
        }
        super.onBackPressed()
    }

    companion object {
        private const val ANCHO_PARANTE = 3.8f
        private const val ALTO_RIEL = 3f
        private const val HOLGURA = 0.5f
    }
}








