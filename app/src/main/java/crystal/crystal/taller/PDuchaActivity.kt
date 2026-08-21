package crystal.crystal.taller

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import crystal.crystal.R
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityPduchaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@Suppress("NAME_SHADOWING")
class PDuchaActivity : AppCompatActivity() {

    private var cant = 4
    private var ducha: SerieDucha?=null
    private var indice = 0

    private val mapDuchas: LinkedHashMap<SerieDucha, MutableList<DoorData>> = LinkedHashMap()
// Cada vez que pulsemos “Calcular”, meteremos los datos en mapDuchas[serie], de modo
// que cada clave (p.ej. “A005”, “A010”, “C1”…) tenga lista de DoorData acumulados.

    private var combinedBitmap: Bitmap? = null
// Este es el Bitmap “en memoria” que muestra todo el documento actual.
// Cada vez que recalculamos, lo regeneramos 100% desde mapDuchas.

    private val archivos: MutableList<Bitmap> = mutableListOf()
// Aquí vamos guardando copias del combinedBitmap cada vez que se pulsa (click normal) btArchivar.

    // Diseño (paños + perforaciones) de cada puerta calculada; una página del PDF por entrada.
    private val disenosPorPuerta: MutableList<Bitmap> = mutableListOf()

    private var ultimaSerie: String? = null
// Para verificar si la serie que estamos añadiendo es la MISMA que antes o cambió.

    private lateinit var binding: ActivityPduchaBinding
    private lateinit var controladorCola: ControladorColaMedidas

    // Archivado a proyecto activo (igual que las otras calculadoras).
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var metaColorAluminio: String = ""
    private var metaTipoVidrio: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPduchaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Proyecto activo (mismo patrón que Nova/Vitrovén/ventanas).
        ProyectoManager.inicializarDesdeStorage(this)
        ProyectoUIHelper.configurarVisorProyectoActivo(this, binding.tvProyectoActivo)
        procesarIntentProyecto(intent)
        metaColorAluminio = intent.getStringExtra("color_aluminio")?.trim().orEmpty()

        // 1) Al pulsar “Calcular”
        binding.btCalcular.setOnClickListener {
            controladorCola.onCalcular()
            // Validamos que haya ancho/alto
            if (binding.etAncho1.text.isBlank() ||
                binding.etAlto.text.isBlank() ||
                // Sólo para C1/A001 validamos etAncho2
                ( (ducha?.nombre == "C1" || ducha?.nombre == "A001") && binding.etAncho2.text.isBlank() )
            ) {
                Toast.makeText(this@PDuchaActivity, "Por favor ingresa todas las medidas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            agregarDoorToDocument()
            limpiarMedidasConHint()
        }

        // 2) Al pulsar “Archivar” (click normal): archiva al proyecto activo, igual que las otras.
        binding.btArchivar.setOnClickListener {
            // Candado de suscripción PRIMERO.
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeArchivar(),
                    "Archivar es una función de pago. Renueva para guardar tus proyectos.")) {
                return@setOnClickListener
            }
            // Pedir color de aluminio y tipo de vidrio antes de archivar.
            mostrarDialogoMetadatosProduccion {
                archivarMapas()
            }
        }

        // 3) Al hacer LARGO click en “Archivar” → Generar PDF con todo lo de `archivos`
        binding.btArchivar.setOnLongClickListener {
            if (mapDuchas.isEmpty()) {
                Toast.makeText(this, "No hay nada que exportar. Calcula primero.", Toast.LENGTH_SHORT).show()
            } else {
                generarPdfDesdeArchivos()
            }
            true
        }

        // 4) Pulsar sobre la imagen de modelo (“ivModelo”) para cambiar de serie
        binding.ivModelo.setOnClickListener {
            actualizarDuchas()   // Solo cambia la variable `ducha` y el texto de tvDucha
        }

        // Inicializamos la serie por primera vez (texto en tvDucha):
        actualizarDuchas()

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAncho1.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAlto.setText(df1(it)) }

        controladorCola = ControladorColaMedidas(
            activity = this,
            claseActual = PDuchaActivity::class.java,
            etAncho = binding.etAncho1,
            etAlto = binding.etAlto,
            ivDiseno = binding.ivVidrios,
            formato = ::df1
        )
        controladorCola.inicializar()
    }

    private fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    /** Tras calcular: deja los EditText de medidas en blanco y pone lo borrado como hint,
     *  para reingresar rápido viendo la medida anterior de referencia. */
    private fun limpiarMedidasConHint() {
        listOf(binding.etAncho1, binding.etAncho2, binding.etAlto).forEach { et ->
            val v = et.text?.toString()?.trim().orEmpty()
            if (v.isNotEmpty()) et.hint = v
            et.text?.clear()
        }
    }

    /** Un paño de vidrio a producir: qué hoja es, y sus medidas en cm. */
    data class PanelVidrio(val tipo: String, val anchoCm: Float, val altoCm: Float)

    /**
     * Fuente ÚNICA del despiece de vidrios: dado la serie y las medidas de vano de una puerta,
     * devuelve los paños (fijo/corrediza) con sus medidas en cm, según las fórmulas del catálogo.
     * Lo usan tanto el texto de resultados (`vidrios()`) como la tabla del diseño (`crearBitmapPorSerie`).
     */
    private fun panelesDe(serie: SerieDucha, d: DoorData): List<PanelVidrio> {
        if (serie.marca == "Corrales") return panelesCorrales(serie.nombre, d)
        val p = parametrosPorSerie[serie.nombre] ?: return emptyList()
        val altoTotal = d.alto
        val hF = altoTotal - p.altoOffsetFijo
        val hC = altoTotal - p.altoOffsetCorrediza
        return when (serie.nombre) {
            // P2: dos corredizos idénticos = (ancho+3)/2 x (alto-6)
            "P2" -> {
                val a = (d.ancho1 + 3f) / 2f
                listOf(PanelVidrio("Corrediza", a, hC), PanelVidrio("Corrediza", a, hC))
            }
            // A010: dos corredizos iguales = (ancho+5)/2 x (alto-0.8)
            "A010" -> {
                val a = (d.ancho1 + 5f) / 2f
                listOf(PanelVidrio("Corrediza", a, hC), PanelVidrio("Corrediza", a, hC))
            }
            // F1: fijo = ancho/2 x (alto-5)  y corrediza = (ancho/2+5) x (alto-2)
            "F1" -> {
                val f = d.ancho1 / 2f
                listOf(PanelVidrio("Fija", f, hF), PanelVidrio("Corrediza", f + 5f, hC))
            }
            // A005 y A007: fijo = ancho/2  y corrediza = ancho/2+4
            "A005", "A007" -> {
                val f = d.ancho1 / 2f
                listOf(PanelVidrio("Fija", f, hF), PanelVidrio("Corrediza", f + 4f, hC))
            }
            // A001 y C1: dos vanos distintos, cada uno con fijo + corrediza (A001: +4, C1: +5)
            "A001", "C1" -> {
                val f1 = d.ancho1 / 2f
                val f2 = d.ancho2 / 2f
                val off = if (serie.nombre == "A001") 4f else 5f
                listOf(
                    PanelVidrio("Fija", f1, hF), PanelVidrio("Corrediza", f1 + off, hC),
                    PanelVidrio("Fija", f2, hF), PanelVidrio("Corrediza", f2 + off, hC)
                )
            }
            // Resto (ej. Plegable): un fijo y un corrediza (+4 asumido)
            else -> {
                val f = d.ancho1 / 2f
                listOf(PanelVidrio("Fija", f, hF), PanelVidrio("Corrediza", f + 4f, hC))
            }
        }
    }

    /**
     * Despiece de vidrios de las series marca CORRALES (fórmulas de las fichas Corrales).
     * Las fichas están en mm; la app trabaja en cm, por eso: 5mm→0.5, 20mm→2.0, 22mm→2.2,
     * 67mm→6.7, 72mm→7.2.
     */
    private fun panelesCorrales(nombre: String, d: DoorData): List<PanelVidrio> = when (nombre) {
        // P2 Corrales: dos corredizos. A = ancho lado/2 + 20mm ; H = Alto - 67mm.
        "P2" -> {
            val a = d.ancho1 / 2f + 2.0f
            val h = d.alto - 6.7f
            listOf(PanelVidrio("Corrediza", a, h), PanelVidrio("Corrediza", a, h))
        }
        // F1 Corrales: un fijo + un corredizo. A = ancho lado/2 + 9mm (ambos);
        //   fijo H = Alto - 72mm ; corredizo H = Alto - 22mm.
        "F1" -> {
            val a = d.ancho1 / 2f + 0.9f
            listOf(PanelVidrio("Fija", a, d.alto - 7.2f), PanelVidrio("Corrediza", a, d.alto - 2.2f))
        }
        // C1 Corrales: esquina, dos lados (ancho1 y ancho2), cada lado con fijo + corredizo.
        //   A = ancho lado/2 - 5mm (ambos); fijo H = Alto - 72mm ; corredizo H = Alto - 22mm.
        "C1" -> {
            val a1 = d.ancho1 / 2f - 0.5f
            val a2 = d.ancho2 / 2f - 0.5f
            listOf(
                PanelVidrio("Fija", a1, d.alto - 7.2f), PanelVidrio("Corrediza", a1, d.alto - 2.2f),
                PanelVidrio("Fija", a2, d.alto - 7.2f), PanelVidrio("Corrediza", a2, d.alto - 2.2f)
            )
        }
        else -> emptyList()
    }

    /** DoorData con las medidas actualmente ingresadas en pantalla. */
    private fun doorDataDeEntrada(serie: SerieDucha): DoorData =
        DoorData(ancho1(), if (serie.nombre == "C1" || serie.nombre == "A001") ancho2() else 0f, alto())

    @SuppressLint("SetTextI18n")
    private fun vidrios() {
        val serie = ducha ?: return
        val paneles = panelesDe(serie, doorDataDeEntrada(serie))
        binding.tvVidrios.text = paneles.joinToString("\n") { "${df1(it.anchoCm)} x ${df1(it.altoCm)}" }
        cant = paneles.size
    }

    //FUNCIONES PARA GENRAR DISEÑO

    @SuppressLint("SetTextI18n")
    private fun dibujarTodos() {
        // 1) obtenemos fijos y corredizas
        val (fijos, corredizas) = obtenerLineas()

        // 2) limpiamos el contenedor
        binding.lyDisenos.removeAllViews()

        // 3) dibujamos fijos y corredizas
        fijos.forEach { agregarBitmap(crearBitmapParaLinea(it, false)) }
        corredizas.forEach { agregarBitmap(crearBitmapParaLinea(it, true)) }
    }

    @SuppressLint("SetTextI18n")
    private fun agregarDoorToDocument() {
        val serie = ducha ?: return

        // Acumula la puerta (medidas de vano) en el mapa: es la fuente de verdad del documento.
        val a2 = if (serie.nombre == "C1" || serie.nombre == "A001") ancho2() else 0f
        mapDuchas.getOrPut(serie) { mutableListOf() }.add(DoorData(ancho1(), a2, alto()))

        // Reconstruye el resultado como antes: por cada serie, tabla de medidas de vidrio + altura de
        // tiradores + imagen de referencia (no a escala) que da la info de producción.
        rebuildCombinedBitmap()

        // Y refleja el despiece de vidrios en el TextView de resultados.
        try { vidrios() } catch (_: Exception) {}
    }

    /** Diseño de la puerta actual: título de la serie + cada paño (fijo/corrediza) con sus cotas y
     *  perforaciones, usando las fórmulas del catálogo (obtenerLineas + crearBitmapParaLinea). */
    private fun crearDisenoPuertaActual(): Bitmap? {
        val serie = ducha?.nombre ?: return null
        val (fijos, corredizas) = obtenerLineas()
        if (fijos.isEmpty() && corredizas.isEmpty()) return null
        val bloques = mutableListOf<Bitmap>()
        bloques.add(crearTituloSerie(serie))
        fijos.forEach { bloques.add(crearBitmapParaLinea(it, false)) }
        corredizas.forEach { bloques.add(crearBitmapParaLinea(it, true)) }
        return combinarBitmapsVerticalmente(bloques)
    }

    private fun crearTituloSerie(serie: String): Bitmap {
        val d = resources.displayMetrics.density
        val w = resources.displayMetrics.widthPixels
        val h = (36f * d).toInt()
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.parseColor("#DDDDDD"))
        val p = Paint().apply {
            color = Color.BLACK; textSize = 18f * d
            textAlign = Paint.Align.CENTER; isAntiAlias = true
        }
        c.drawText("Vidrio Puerta Ducha ${serie.uppercase(Locale.getDefault())}", w / 2f, h * 0.65f, p)
        return bmp
    }

    private fun rebuildCombinedBitmap() {
        // 1) Para cada entrada (serie → lista<DoorData>), creamos un Bitmap detalle
        val listaBitmaps = mutableListOf<Bitmap>()
        mapDuchas.forEach { (serie, listaPuertas) ->
            // crear un bloque con título + tabla + mini-imagen
            val bmpSerie = crearBitmapPorSerie(serie, listaPuertas)
            listaBitmaps.add(bmpSerie)
        }

        // 2) Combinar verticalmente todos esos bitmaps
        combinedBitmap = combinarBitmapsVerticalmente(listaBitmaps)
        // 3) Asignar al ImageView
        binding.ivVidrios.setImageBitmap(combinedBitmap)
    }

    private fun combinarBitmapsVerticalmente(bitmaps: List<Bitmap>): Bitmap {
        // Si no hay nada, devolvemos un bitmap vacío muy pequeño
        if (bitmaps.isEmpty()) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }

        // Determinar ancho máximo y suma de alturas
        val ancho = bitmaps.maxOf { it.width }
        var alturaTotal = 0
        for (b in bitmaps) alturaTotal += b.height

        // Crear un bitmap grande
        val combined = Bitmap.createBitmap(ancho, alturaTotal, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combined)
        var desplazY = 0f

        // Pintar cada uno uno debajo de otro
        for (b in bitmaps) {
            canvas.drawBitmap(b, 0f, desplazY, null)
            desplazY += b.height.toFloat()
        }
        return combined
    }

    /**
     * Dibuja un bloque completo para una sola serie:
     *   - Título ("Puerta Ducha <serie>")
     *   - Luego una tabla donde cada fila es: [#puerta, tipoHoja, ancho mm, alto mm, alto tirador (si aplica)]
     *   - Al final, opcionalmente, dibuja una pequeñísima “imagen/icono” representativa de la serie
     */
    @SuppressLint("DefaultLocale", "SetTextI18n", "DiscouragedApi")
    private fun crearBitmapPorSerie(
        serie: SerieDucha,
        datos: List<DoorData>
    ): Bitmap {
        // ── Métricas y geometría ──────────────────────────────────────────────
        val d = resources.displayMetrics.density
        val margen = (10f * d)
        val screenW = resources.displayMetrics.widthPixels.toFloat()
        val anchoTabla = screenW - 2 * margen
        val xL = margen                 // borde izquierdo del contenido
        val xR = margen + anchoTabla    // borde derecho
        val padX = 12f * d              // sangría interna de celdas

        // Alturas de cada franja
        val altoTitulo = 42f * d
        val altoPuerta = 54f * d        // 2 líneas: vano en cm y en mm
        val altoCol    = 28f * d
        val altoFila   = 32f * d
        val sepPuertas = 14f * d

        // Paños de vidrio por puerta (fuente: cálculo de vidrios)
        val panelesPorPuerta = datos.map { panelesDe(serie, it) }
        // El tirador solo aplica a algunas series Alutemp; si no, no dibujamos esa columna.
        val hayTirador = serie.nombre == "A005" || serie.nombre == "A001"

        // ── Imagen de referencia: decodificar para reservar su altura REAL ────
        val resId = when {
            serie.marca == "Corrales" -> {
                // Nombre propio por serie para no chocar con las imágenes Alutemp (c1/f1/p2).
                val n = "corrales_${serie.nombre.lowercase(Locale.getDefault())}"
                resources.getIdentifier(n, "mipmap", packageName)
            }
            else -> when (serie.nombre.uppercase(Locale.getDefault())) {
                "A005" -> R.mipmap.a005
                "A001" -> R.mipmap.a001
                "A007" -> R.mipmap.a007
                "A010" -> R.mipmap.a010
                "F1"   -> R.mipmap.f1
                "P2"   -> R.mipmap.p2
                "C1"   -> R.mipmap.c1
                "PLEGABLE" -> R.mipmap.plegable
                else   -> 0
            }
        }
        val icon = if (resId != 0) BitmapFactory.decodeResource(resources, resId) else null
        var imgDrawW: Float
        var imgDrawH: Float
        if (icon != null) {
            val ratio = icon.height.toFloat() / icon.width.toFloat()
            imgDrawW = if (serie.marca == "Corrales" || ratio < 0.7f) anchoTabla else anchoTabla / 2f
            imgDrawH = imgDrawW * ratio
            // Tope de altura: que la imagen no domine el scroll y quede cerca del despiece.
            val maxImgH = anchoTabla * 0.62f
            if (imgDrawH > maxImgH) { imgDrawH = maxImgH; imgDrawW = imgDrawH / ratio }
        } else {
            imgDrawW = anchoTabla / 2f
            imgDrawH = 80f * d
        }

        // ── Altura total ─────────────────────────────────────────────────────
        var alturaTablas = 0f
        panelesPorPuerta.forEach { alturaTablas += altoPuerta + altoCol + altoFila * it.size + sepPuertas }
        val totalAlto = (margen + altoTitulo + alturaTablas + margen + imgDrawH + margen).toInt()

        val bmp = Bitmap.createBitmap(anchoTabla.toInt() + 2 * margen.toInt(), totalAlto, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)

        // ── Paints ───────────────────────────────────────────────────────────
        val bold = android.graphics.Typeface.DEFAULT_BOLD
        val pTitulo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; textSize = 17f * d; textAlign = Paint.Align.CENTER; typeface = bold
        }
        val pPuerta = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#12324F"); textSize = 15f * d; typeface = bold
        }
        val pMm = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#5B6B79"); textSize = 12.5f * d
        }
        val pCol = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#5B6B79"); textSize = 12f * d; typeface = bold
        }
        val pDato = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1B2A38"); textSize = 15f * d
        }
        val pLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#C8D2DC"); strokeWidth = 1f * d
        }
        val fTitulo = Paint().apply { color = Color.parseColor("#2E4A62") }
        val fPuerta = Paint().apply { color = Color.parseColor("#DCE7F0") }
        val fCol    = Paint().apply { color = Color.parseColor("#EEF2F5") }
        val fZebra  = Paint().apply { color = Color.parseColor("#F5F8FA") }

        // Anclas de columna: Hoja a la izquierda; Ancho/Alto/Tirador alineados a la derecha.
        val xHoja = xL + padX
        val xAnchoR = if (hayTirador) xL + anchoTabla * 0.52f else xL + anchoTabla * 0.60f
        val xAltoR  = if (hayTirador) xL + anchoTabla * 0.76f else xL + anchoTabla * 0.90f
        val xTirR   = xR - padX

        var y = margen

        // ── Barra de título ──────────────────────────────────────────────────
        canvas.drawRect(xL, y, xR, y + altoTitulo, fTitulo)
        canvas.drawText("Ducha ${serie.nombre} · ${serie.marca}", xL + anchoTabla / 2f, y + altoTitulo * 0.64f, pTitulo)
        y += altoTitulo

        // ── Por cada puerta: cabecera con medida de vano + tabla de paños ────
        panelesPorPuerta.forEachIndexed { idx, paneles ->
            val door = datos[idx]
            val dosVanos = serie.nombre == "C1" || serie.nombre == "A001"

            // Cabecera de puerta: "Puerta N" a la izquierda; vano en cm (arriba) y mm (abajo) a la derecha.
            canvas.drawRect(xL, y, xR, y + altoPuerta, fPuerta)
            val vanoCm = if (dosVanos)
                "${df1(door.ancho1)} / ${df1(door.ancho2)} × ${df1(door.alto)} cm"
            else
                "${df1(door.ancho1)} × ${df1(door.alto)} cm"
            val vanoMm = if (dosVanos)
                "${df1(door.ancho1 * 10f)} / ${df1(door.ancho2 * 10f)} × ${df1(door.alto * 10f)} mm"
            else
                "${df1(door.ancho1 * 10f)} × ${df1(door.alto * 10f)} mm"
            pPuerta.textAlign = Paint.Align.LEFT
            canvas.drawText("Puerta ${idx + 1}", xL + padX, y + altoPuerta * 0.60f, pPuerta)
            pPuerta.textAlign = Paint.Align.RIGHT
            canvas.drawText(vanoCm, xR - padX, y + altoPuerta * 0.42f, pPuerta)
            pMm.textAlign = Paint.Align.RIGHT
            canvas.drawText(vanoMm, xR - padX, y + altoPuerta * 0.82f, pMm)
            y += altoPuerta

            // Encabezado de columnas
            canvas.drawRect(xL, y, xR, y + altoCol, fCol)
            val cyCol = y + altoCol * 0.70f
            pCol.textAlign = Paint.Align.LEFT
            canvas.drawText("Hoja", xHoja, cyCol, pCol)
            pCol.textAlign = Paint.Align.RIGHT
            canvas.drawText("Ancho (mm)", xAnchoR, cyCol, pCol)
            canvas.drawText("Alto (mm)", xAltoR, cyCol, pCol)
            if (hayTirador) canvas.drawText("Tirador", xTirR, cyCol, pCol)
            y += altoCol

            // Filas de paños (una por vidrio) con zebra suave
            paneles.forEachIndexed { j, panel ->
                if (j % 2 == 1) canvas.drawRect(xL, y, xR, y + altoFila, fZebra)
                val cy = y + altoFila * 0.66f
                pDato.textAlign = Paint.Align.LEFT
                canvas.drawText(panel.tipo, xHoja, cy, pDato)
                pDato.textAlign = Paint.Align.RIGHT
                canvas.drawText(df1(panel.anchoCm * 10f), xAnchoR, cy, pDato)
                canvas.drawText(df1(panel.altoCm * 10f), xAltoR, cy, pDato)
                if (hayTirador) {
                    canvas.drawText(if (panel.tipo == "Corrediza") "944" else "—", xTirR, cy, pDato)
                }
                y += altoFila
            }

            // Separador entre puertas
            y += sepPuertas / 2f
            canvas.drawLine(xL, y, xR, y, pLinea)
            y += sepPuertas / 2f
        }

        // ── Imagen de referencia ─────────────────────────────────────────────
        y += margen
        if (icon != null) {
            val leftImg = xL + (anchoTabla - imgDrawW) / 2f
            canvas.drawBitmap(icon, null, android.graphics.RectF(leftImg, y, leftImg + imgDrawW, y + imgDrawH), null)
        } else {
            val rectImg = android.graphics.RectF(xL + anchoTabla / 4f, y, xR - anchoTabla / 4f, y + imgDrawH)
            val paintMarco = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE; strokeWidth = 2f * d; color = Color.parseColor("#C8D2DC")
            }
            canvas.drawRect(rectImg, paintMarco)
            pMm.textAlign = Paint.Align.CENTER
            canvas.drawText("Sin imagen", rectImg.centerX(), rectImg.centerY(), pMm)
        }


        return bmp
    }


    private fun generarPdfDesdeArchivos() {
        // 1) Crear un objeto PdfDocument
        val pdf = PdfDocument()

        // 2) Una página por serie: el mismo bloque tabla de vidrios + imagen de referencia.
        mapDuchas.entries.forEachIndexed { index, (serie, lista) ->
            val bmp = crearBitmapPorSerie(serie, lista)
            val pageInfo = PdfDocument.PageInfo.Builder(bmp.width, bmp.height, index + 1).create()
            val page = pdf.startPage(pageInfo)
            page.canvas.drawBitmap(bmp, 0f, 0f, null)
            pdf.finishPage(page)
        }

        // 3) Guardar el PDF en caché (cacheDir/pdfshare/<timestamp>.pdf)
        val cachePdfDir = File(cacheDir, "pdfshare").apply { mkdirs() }
        val fileName = "DiseñosPuertas_${System.currentTimeMillis()}.pdf"
        val file = File(cachePdfDir, fileName)
        FileOutputStream(file).use { outStream ->
            pdf.writeTo(outStream)
        }
        pdf.close()

        // 4) Obtener URI vía FileProvider
        val pdfUri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            file
        )

        // 5) Lanzar chooser para compartir
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(Intent.EXTRA_SUBJECT, "Diseño de Puertas Ducha")
            putExtra(Intent.EXTRA_TEXT, "Adjunto el PDF con los diseños acumulados.")
        }
        startActivity(Intent.createChooser(shareIntent, "Compartir PDF"))
    }


    private fun agregarBitmap(bmp: Bitmap) {
        val d = resources.displayMetrics.density
        // Mantener 8dp de separación horizontal, pero reducir el espacio vertical a 4dp
        val marginHorizontal = (8 * d).toInt()
        val marginVertical   = (-63 * d).toInt()
        ImageView(this).apply {
            setImageBitmap(bmp)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                // left = 0, top = marginVertical, right = marginHorizontal, bottom = marginVertical
                setMargins(0, marginVertical, marginHorizontal, marginVertical)
            }
        }.also { binding.lyDisenos.addView(it) }
    }

    private fun obtenerLineas(): Pair<List<String>, List<String>> {
        // Opción 1: usando emptyList con genérico
        val duchaActual = ducha ?: return emptyList<String>() to emptyList()
        val p = parametrosPorSerie[duchaActual.nombre]
            ?: error("Faltan parámetros para ${duchaActual.nombre}")
        val altoTotal = alto()

        return when (duchaActual.nombre) {
            "P2" -> {
                val a = (ancho1() + 3f) / 2f
                val h = altoTotal - p.altoOffsetCorrediza
                emptyList<String>() to listOf(
                    "${df1(a)} x ${df1(h)} = 1",
                    "${df1(a)} x ${df1(h)} = 1"
                )
            }
            "F1" -> {
                val f = ancho1() / 2f
                val hF = altoTotal - p.altoOffsetFijo
                val c = f + 5f
                val hC = altoTotal - p.altoOffsetCorrediza
                listOf("${df1(f)} x ${df1(hF)} = 1") to
                        listOf("${df1(c)} x ${df1(hC)} = 1")
            }
            "A010" -> {
                val a = (ancho1() + 5f) / 2f
                val h = altoTotal - p.altoOffsetCorrediza
                emptyList<String>() to listOf(
                    "${df1(a)} x ${df1(h)} = 1",
                    "${df1(a)} x ${df1(h)} = 1"
                )
            }
            "A005", "A007" -> {
                val f = ancho1() / 2f
                val hF = altoTotal - p.altoOffsetFijo
                val c = f + 4f
                val hC = altoTotal - p.altoOffsetCorrediza
                listOf("${df1(f)} x ${df1(hF)} = 1") to
                        listOf("${df1(c)} x ${df1(hC)} = 1")
            }
            "A001", "C1" -> {
                val an1 = ancho1()
                val an2 = ancho2()
                val hF = altoTotal - p.altoOffsetFijo
                val hC = altoTotal - p.altoOffsetCorrediza
                val f1 = an1 / 2f
                val f2 = an2 / 2f
                val corrOffset = if (duchaActual.nombre == "A001") 4f else 5f
                val c1 = f1 + corrOffset
                val c2 = f2 + corrOffset

                listOf(
                    "${df1(f1)} x ${df1(hF)} = 1",
                    "${df1(f2)} x ${df1(hF)} = 1"
                ) to listOf(
                    "${df1(c1)} x ${df1(hC)} = 1",
                    "${df1(c2)} x ${df1(hC)} = 1"
                )
            }
            else -> {
                val f = ancho1() / 2f
                val hF = altoTotal - p.altoOffsetFijo
                val c = f + 4f
                val hC = altoTotal - p.altoOffsetCorrediza
                listOf("${df1(f)} x ${df1(hF)} = 1") to
                        listOf("${df1(c)} x ${df1(hC)} = 1")
            }
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun crearBitmapParaLinea(linea: String, esCorrediza: Boolean): Bitmap {
        // 1) Obtener la serie y parámetros
        val ducha = ducha ?: error("Ducha no seleccionada")
        val p = parametrosPorSerie[ducha.nombre]
            ?: error("Faltan parámetros para ${ducha.nombre}")

        // 2) Métricas de pantalla
        val d = resources.displayMetrics.density
        val tamTxt = 14f * d
        val labelSize = tamTxt * 1.5f
        val margen = 8f * d
        val tick = 4f * d

        // 3) Parsear "W x H = cnt"
        val (wh, cnt) = linea.split("=").map { it.trim() }
        val (wStr, hStr) = wh.split("x").map { it.trim() }
        val wCm = wStr.toFloat()
        val hCm = hStr.toFloat()

        // 4) Márgenes para cotas
        val extH = tamTxt
        val leftM = margen + labelSize + tamTxt + tick + margen
        val rightM = margen + labelSize + margen
        val topM = margen + labelSize * 2 + tamTxt + extH + tick + margen

        // 5) Escala dinámica (hasta 3dp/cm)
        val basePorCm = 3f * d
        val screenW = resources.displayMetrics.widthPixels.toFloat()
        val porCm = minOf(basePorCm, (screenW - leftM - rightM) / wCm)

        // 6) Dimensiones en píxeles
        val wPx = wCm * porCm
        val hPx = hCm * porCm

        // 7) Paints
        val stroke = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f * d
            color = Color.BLACK
            isAntiAlias = true
        }
        val textP = Paint().apply {
            textSize = tamTxt
            color = Color.BLACK
            isAntiAlias = true
        }
        val labelP = Paint().apply {
            textSize = labelSize
            color = Color.BLACK
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        // 8) Crear bitmap y canvas
        val bmpW = (leftM + wPx + rightM).toInt()
        val bmpH = (topM + hPx + margen + extH + margen).toInt()
        val bmp = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888)
        val cv = Canvas(bmp)

        // 9) Etiqueta y cantidad
        val centerX = leftM + wPx / 2f
        val yLabel = topM - labelSize * 2.5f
        val etiqueta = if (esCorrediza) "paño corrediza" else "paño fijo"
        val piezas = if (cnt == "1") "1 pieza" else "$cnt piezas"
        cv.drawText(etiqueta, centerX, yLabel, labelP)
        val lw = labelP.measureText(etiqueta)
        val uy = yLabel + 4f * d
        cv.drawLine(centerX - lw / 2f, uy, centerX + lw / 2f, uy, stroke)
        cv.drawText(piezas, centerX, uy - labelSize, labelP)

        // 10) Marco
        val l = leftM
        val t = topM
        val r = l + wPx
        val b = t + hPx
        cv.drawRect(l, t, r, b, stroke)

        // 11) Cotas internas (ancho y alto)
        textP.textAlign = Paint.Align.CENTER
        cv.drawText("$wStr cm", (l + r) / 2f, b + margen + tamTxt, textP)
        textP.textAlign = Paint.Align.LEFT
        listOf(hStr, "cm").forEachIndexed { i, line ->
            val y = t + hPx / 2f + (i - 0.5f) * tamTxt
            cv.drawText(line, r + margen, y, textP)
        }

        // 12) Taladros superiores
        if (ducha.nombre == "A010") {
            // A010: dos rieles, cada uno con orificios a 8 cm de cada lado
            val offX = p.offXsupC1 * porCm
            val y1 = t + p.offYsupC1 * porCm
            val y2 = t + p.offYsupC2 * porCm
            val rSup = p.diamSupCorrediza * porCm / 2f
            listOf(y1, y2).forEach { ry ->
                cv.drawCircle(l + offX, ry, rSup, stroke)
                cv.drawCircle(r - offX, ry, rSup, stroke)
            }
        } else if (!esCorrediza) {
            // Fijo genérico: dos orificios simétricos
            val offX = p.offXsupFijo * porCm
            val offY = p.offYsupFijo * porCm
            val rSup = p.diamSupFijo * porCm / 2f
            cv.drawCircle(l + offX, t + offY, rSup, stroke)
            cv.drawCircle(r - offX, t + offY, rSup, stroke)
            textP.textAlign = Paint.Align.CENTER
            cv.drawText(
                "⌀${(p.diamSupFijo * 10).toInt()} mm",
                (l + offX + r - offX) / 2f,
                t + offY + margen + tamTxt,
                textP
            )
        } else {
            // Corrediza genérica: dos orificios independientes
            val rSup = p.diamSupCorrediza * porCm / 2f
            val simb = "⌀${(p.diamSupCorrediza * 10).toInt()} mm"
            val x1 = l + p.offXsupC1 * porCm
            val y1 = t + p.offYsupC1 * porCm
            val x2 = l + p.offXsupC2 * porCm
            val y2 = t + p.offYsupC2 * porCm
            cv.drawCircle(x1, y1, rSup, stroke)
            cv.drawCircle(x2, y2, rSup, stroke)
            textP.textAlign = Paint.Align.CENTER
            cv.drawText(simb, (x1 + x2) / 2f, (y1 + y2) / 2f + margen, textP)
        }

        // 13) Taladros inferiores
        if (ducha.nombre == "A010") {
            // Toallero: 44 cm centro‐centro, centrado verticalmente
            val yHole = t + hPx / 2f
            val sepPx = p.offXinferior * porCm
            val x1 = l + wPx / 2f - sepPx / 2f
            val x2 = l + wPx / 2f + sepPx / 2f
            val rInf = p.diamInferior * porCm / 2f
            cv.drawCircle(x1, yHole, rInf, stroke)
            cv.drawCircle(x2, yHole, rInf, stroke)
        } else if (esCorrediza && p.diamInferior > 0f) {
            // Genérico inferior
            val xi = l + p.offXinferior * porCm
            val yi = t + p.offYinferior * porCm
            val rInf = p.diamInferior * porCm / 2f
            cv.drawCircle(xi, yi, rInf, stroke)
            textP.textAlign = Paint.Align.LEFT
            cv.drawText(
                "⌀${(p.diamInferior * 10).toInt()} mm",
                xi + rInf + tick,
                yi + tamTxt / 2f,
                textP
            )
        }

        // 14) Cotas externas
        val bottom = b

        if (ducha.nombre == "A010") {
            // 14a) Dos cotas horizontales de 80 mm (izq. y der.)
            val yCotaSup = t - extH / 2f
            val xHoleL = l + p.offXsupC1 * porCm
            val xHoleR = r - p.offXsupC1 * porCm
            listOf(l to xHoleL, xHoleR to r).forEach { (start, end) ->
                cv.drawLine(start, yCotaSup, end, yCotaSup, stroke)
                listOf(start, end).forEach { xx ->
                    cv.drawLine(xx, yCotaSup - tick / 2, xx, yCotaSup + tick / 2, stroke)
                }
                textP.textAlign = Paint.Align.CENTER
                cv.drawText("80 mm", (start + end) / 2f, yCotaSup - tick, textP)
            }

            // 14b) Cotas verticales de 28 mm y 88 mm (costado derecho)
            val xDimDer = r + margen
            listOf(p.offYsupC1 to "28 mm", p.offYsupC2 to "88 mm").forEach { (offY, lbl) ->
                val yPx = t + offY * porCm
                cv.drawLine(r, yPx, xDimDer, yPx, stroke)
                cv.drawLine(xDimDer - tick / 2, yPx - tick / 2, xDimDer + tick / 2, yPx + tick / 2, stroke)
                cv.drawLine(xDimDer - tick / 2, yPx + tick / 2, xDimDer + tick / 2, yPx - tick / 2, stroke)
                textP.textAlign = Paint.Align.LEFT
                cv.drawText(lbl, xDimDer + tick, yPx + tamTxt / 2f, textP)
            }

            // 14c) Cota horizontal del toallero (440 mm) dentro
            val yHole2 = t + hPx / 2f
            val sepPx2 = p.offXinferior * porCm
            val x1_2 = l + wPx / 2f - sepPx2 / 2f
            val x2_2 = l + wPx / 2f + sepPx2 / 2f
            cv.drawLine(x1_2, yHole2, x2_2, yHole2, stroke)
            listOf(x1_2, x2_2).forEach { xx ->
                cv.drawLine(xx, yHole2 - tick / 2, xx, yHole2 + tick / 2, stroke)
            }
            textP.textAlign = Paint.Align.CENTER
            cv.drawText("440 mm", (x1_2 + x2_2) / 2f, yHole2 - tick, textP)

        } else {
            // Genérico cotas externas horizontales
            val yDimH = t - extH
            val baseX = if (!esCorrediza) p.offXsupFijo else p.offXsupC1
            listOf(l to l + baseX * porCm, r - baseX * porCm to r).forEach { (s, e) ->
                cv.drawLine(s, t, s, yDimH, stroke)
                cv.drawLine(e, t, e, yDimH, stroke)
                cv.drawLine(s, yDimH, e, yDimH, stroke)
                cv.drawLine(s, yDimH - tick / 2, s, yDimH + tick / 2, stroke)
                cv.drawLine(e, yDimH - tick / 2, e, yDimH + tick / 2, stroke)
                textP.textAlign = Paint.Align.CENTER
                cv.drawText("${baseX.toInt()} cm", (s + e) / 2f, yDimH - tick, textP)
            }
        }

        // 15) Cotas externas verticales genéricas
        val ySup = p.offYsupFijo * porCm
        val xDimIzq = l - tamTxt
        cv.drawLine(l, t, xDimIzq, t, stroke)
        cv.drawLine(l, t + ySup, xDimIzq, t + ySup, stroke)
        cv.drawLine(xDimIzq, t, xDimIzq, t + ySup, stroke)
        cv.drawLine(xDimIzq - tick / 2, t, xDimIzq + tick / 2, t, stroke)
        cv.drawLine(xDimIzq - tick / 2, t + ySup, xDimIzq + tick / 2, t + ySup, stroke)
        textP.textAlign = Paint.Align.RIGHT
        cv.drawText(
            "${p.offYsupFijo.toInt()} cm",
            xDimIzq - tick,
            t + ySup / 2f + tamTxt / 2f,
            textP
        )

        return bmp
    }


    //FUNCION COMPARTIR IMAGEN

    private suspend fun bitmapToPdfUri(bmp: Bitmap): Uri = withContext(Dispatchers.IO) {
        // 1) Creamos el documento y la página
        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bmp.width, bmp.height, 1).create()
        val page = pdf.startPage(pageInfo)
        page.canvas.drawBitmap(bmp, 0f, 0f, null)
        pdf.finishPage(page)

        // 2) Guardamos el PDF en cache/pdfshare/design.pdf
        val cachePdfDir = File(cacheDir, "pdfshare").apply { mkdirs() }
        val file = File(cachePdfDir, "design.pdf")
        FileOutputStream(file).use { out ->
            pdf.writeTo(out)
        }
        pdf.close()

        // 3) Obtenemos URI via FileProvider
        FileProvider.getUriForFile(
            this@PDuchaActivity,                // tu Activity real
            "$packageName.fileprovider",
            file
        )
    }

    /**
     * Lanza el chooser para compartir un PDF generado a partir de layout.
     */
    private fun sharePdfOfLayout(layout: View) {
        layout.post {
            lifecycleScope.launch {
                // 1) Capturamos el layout como bitmap
                val bmp = layout.drawToBitmap()
                // 2) Convertimos a PDF y obtenemos URI
                val pdfUri = bitmapToPdfUri(bmp)
                // 3) Construimos el intent
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, pdfUri)
                    putExtra(Intent.EXTRA_SUBJECT, "Diseño de vidrios")
                    putExtra(Intent.EXTRA_TEXT, "Adjunto el PDF con el diseño")
                }
                startActivity(Intent.createChooser(shareIntent, "Compartir PDF"))
            }
        }
    }

    private fun ancho1():Float{
        val ancho1=binding.etAncho1.text.toString().toFloat()

        return ancho1
    }
    private fun ancho2():Float{
        val ancho2=binding.etAncho2.text.toString().toFloat()
        return ancho2
    }
    private fun alto():Float{
        val alto=binding.etAlto.text.toString().toFloat()
        return alto
    }

    //  CAMBIOS DE SERIE
    @SuppressLint("SetTextI18n")
    private fun actualizarDuchas() {
        if (listaDuchas.isNotEmpty()) {
            // 1) Actualiza la variable y el texto
            ducha = listaDuchas[indice]
            binding.tvDucha.text = "Ducha ${ducha!!.nombre} [${ducha!!.marca}]"

            // 1b) Ficha técnica / formatos de referencia (texto), según la serie.
            binding.tvReferencias.text = fichaTecnicaDe(ducha!!)

            // 1c) Link al PDF completo (Firebase), si la serie tiene URL cargada.
            val pdfUrl = fichaPdfUrlDe(ducha!!)
            if (pdfUrl != null) {
                binding.tvFichaPdf.visibility = View.VISIBLE
                binding.tvFichaPdf.setOnClickListener { abrirUrl(pdfUrl) }
            } else {
                binding.tvFichaPdf.visibility = View.GONE
                binding.tvFichaPdf.setOnClickListener(null)
            }

            // 2) Toggle visibilidad de lyAncho2 **inmediatamente** según el nombre
            binding.lyAncho2.visibility =
                if (ducha!!.nombre in listOf("A001", "C1")) View.VISIBLE
                else View.GONE

            // 3) Prepara el siguiente índice
            indice = (indice + 1) % listaDuchas.size
        } else {
            binding.tvDucha.text = "No disponibles"
            binding.tvReferencias.text = ""
            binding.tvFichaPdf.visibility = View.GONE
            binding.lyAncho2.visibility = View.GONE
        }
    }

    /**
     * URL del PDF de ficha técnica COMPLETA por serie (alojado en Firebase Storage). Vacío = sin
     * link (el enlace queda oculto). Pegar aquí los enlaces de descarga que entrega Firebase Storage.
     */
    private fun fichaPdfUrlDe(serie: SerieDucha): String? {
        val url = when ("${serie.marca}/${serie.nombre}") {
            // ── Corrales ──
            "Corrales/C1" -> ""
            "Corrales/F1" -> ""
            "Corrales/P2" -> ""
            // ── Alutemp ──
            "Alutemp/P2" -> ""
            "Alutemp/F1" -> ""
            "Alutemp/C1" -> ""
            "Alutemp/A010" -> ""
            "Alutemp/A005" -> ""
            "Alutemp/A007" -> ""
            "Alutemp/A001" -> ""
            "Alutemp/Plegable" -> ""
            else -> ""
        }
        return url.ifBlank { null }
    }

    /** Abre una URL (PDF de ficha) en el visor/navegador del teléfono. */
    private fun abrirUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Ficha técnica + Formatos de referencia por serie, siguiendo la estructura del catálogo
     * (Formatos: modelo · espesor · medida · acabado; más la nota de Ficha Técnica). Se muestra en
     * el recuadro de referencia al elegir la serie.
     */
    private fun fichaTecnicaDe(serie: SerieDucha): String = when (serie.marca) {
        "Corrales" -> when (serie.nombre) {
            "C1" -> "Formatos: En esquina, 2 fijos + 2 corredizos · Vidrio 6 mm · 1.20 m ancho · alto máx 1.90 m · Negro / Cromado\n" +
                    "Ficha técnica: Perforación Ø14 mm · cristal templado 6 mm."
            "F1" -> "Formatos: Frontal, 1 fijo + 1 móvil · Vidrio 6 mm · 1.50 m ancho · alto máx 1.90 m · Negro / Cromado\n" +
                    "Ficha técnica: Perforación Ø14 mm · cristal templado 6 mm."
            "P2" -> "Formatos: Frontal, 2 hojas móviles · Vidrio 8 mm · 2.00 m ancho · alto máx 1.90 m · Negro / Cromado\n" +
                    "Ficha técnica: Perforación Ø14 mm · cristal templado 8 mm."
            else -> ""
        }
        else -> when (serie.nombre) {   // Alutemp
            "P2" -> "Formatos: Frontal, 2 hojas móviles · Vidrio 6 y 8 mm · 1.95×1.20 / 1.95×1.80 m · Plateado / Negro\n" +
                    "Ficha técnica: Las perforaciones del gráfico son para vidrio de 6 y 8 mm."
            "F1" -> "Formatos: Frontal, 1 fijo + 1 móvil · Vidrio 6 y 8 mm · 1.95×1.20 / 1.95×1.80 m · Plateado / Negro\n" +
                    "Ficha técnica: Perforación del rodamiento: 12 mm (vidrio 6 mm), 14 mm (vidrio 8 mm)."
            "C1" -> "Formatos: En esquina, 2 fijos + 2 corredizos · Vidrio 6 y 8 mm · 1.95 alto × 1.20×1.20 ancho m · Plateado / Negro\n" +
                    "Ficha técnica: Perforaciones para vidrio de 6 mm; para 8 mm la perforación es 14 mm."
            "A010" -> "Formatos: Frontal, 2 hojas móviles · Vidrio 8 mm · 2.00 alto × 1.50 ancho m · Plateado / Negro\n" +
                    "Ficha técnica: Se recomienda instalar con vidrio templado de 8 mm."
            "A005" -> "Formatos: Frontal, 1 fijo + 1 corredizo · Vidrio 8 mm · 1.20 / 2 m · Cromado / Negro / Dorado\n" +
                    "Ficha técnica: Se recomienda instalar con vidrio de 8 mm."
            "A007" -> "Formatos: Frontal, 1 fijo + 1 corredizo · Vidrio 8 mm · 2 m · Cromado\n" +
                    "Ficha técnica: Se recomienda instalar con vidrio templado de 8 mm."
            "A001" -> "Formatos: En esquina, 2 fijos + 2 corredizos · Vidrio 8 mm · 2 m · Negro / Cromado\n" +
                    "Ficha técnica: Kit en acero #304 cromado y cristal templado."
            "Plegable" -> "Formatos: Plegable con bisagras · Vidrio 6 y 8 mm · A medida · Acero #304 cromado\n" +
                    "Ficha técnica: Bisagras BP-320 / BP-321 en acero #304 para vidrio de 6 y 8 mm."
            else -> ""
        }
    }

    // ─── Archivado a proyecto activo (igual que las otras calculadoras) ────────────────────────
    private fun etiqueta(nombre: String) = TextView(this).apply { text = nombre }

    private fun conValor(tv: TextView): Boolean {
        val t = tv.text.toString().trim()
        return t.isNotEmpty() && t != "0.0" && t != "0"
    }

    private fun archivarMapas() {
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        val referencias = ListaCasilla.ItemArchivable(
            binding.tvReferencias, binding.tvReferencias, conValor(binding.tvReferencias)
        )
        val items = listOf(
            ListaCasilla.ItemArchivable(etiqueta("Vidrios"), binding.tvVidrios, conValor(binding.tvVidrios))
        )
        val ultimoID = ListaCasilla.archivarEnProyectoActivo(
            this, mapListas, "Vpd", cant,
            referencias = referencias,
            items = items,
            // Metadato de producción por ventana (mismo formato que Nova), para que el corte de
            // planchas separe las listas por tipo de vidrio.
            paquetesPorNumero = { _ -> metadatosProduccionPaquete() }
        )
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
        val proyecto = ProyectoManager.getProyectoActivo()
        val msg = when {
            proyecto == null -> "No hay proyecto activo; datos no archivados en proyecto"
            cant > 1 -> "Archivadas $cant unidades en proyecto: $proyecto"
            else -> "Datos archivados como $ultimoID en proyecto: $proyecto"
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        controladorCola.ofrecerSiguiente()
    }

    private fun mostrarDialogoMetadatosProduccion(onContinuar: () -> Unit) {
        val pad = (16 * resources.displayMetrics.density).toInt()
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, 0)
        }
        val etColor = EditText(this).apply { hint = "Color aluminio (ej: negro)"; setText(metaColorAluminio) }
        val etVidrio = EditText(this).apply { hint = "Tipo vidrio (ej: incoloro 6mm)"; setText(metaTipoVidrio) }
        contenedor.addView(etColor)
        contenedor.addView(etVidrio)
        AlertDialog.Builder(this)
            .setTitle("Color de aluminio y vidrio")
            .setView(contenedor)
            .setPositiveButton("Guardar y archivar") { _, _ ->
                metaColorAluminio = etColor.text?.toString()?.trim().orEmpty()
                metaTipoVidrio = etVidrio.text?.toString()?.trim().orEmpty()
                onContinuar()
            }
            .setNeutralButton("Omitir") { _, _ -> onContinuar() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun metadatosProduccionPaquete(): Map<String, String> {
        val alu = escaparCampoV2(metaColorAluminio.ifBlank { "null" })
        val vid = escaparCampoV2(metaTipoVidrio.ifBlank { "null" })
        if (alu == "null" && vid == "null") return emptyMap()
        return mapOf("MetadatosProduccion" to "-MAT<alu:$alu;vid:$vid>")
    }

    private fun escaparCampoV2(raw: String): String =
        raw.replace("\n", " / ").replace("\r", " ").replace("-", "_")
            .replace("<", "(").replace(">", ")").replace(";", ",").trim()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        procesarIntentProyecto(intent)
    }

    override fun onResume() {
        super.onResume()
        refrescarProyectoActivoUI()
    }

    private fun refrescarProyectoActivoUI() {
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            ModoMasivoHelper.devolverResultado(
                activity = this,
                calculadora = "Puerta Ducha",
                perfiles = emptyMap(),
                vidrios = ModoMasivoHelper.texto(binding.tvVidrios),
                accesorios = emptyMap(),
                referencias = ""
            )
            return
        }
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}

data class SerieDucha(val nombre: String, val marca: String = "Alutemp")

val listaDuchas= listOf(
    SerieDucha("P2"),
    SerieDucha("F1"),
    SerieDucha("C1"),
    SerieDucha("A010"),
    SerieDucha("A005"),
    SerieDucha("A007"),
    SerieDucha("A001"),
    SerieDucha("Plegable"),
    // Series marca Corrales: fórmulas e imágenes de referencia propias.
    SerieDucha("C1", "Corrales"),
    SerieDucha("F1", "Corrales"),
    SerieDucha("P2", "Corrales")
)

// 0) Data class con TODAS las posiciones que necesitas
data class ParametrosSerie(
    val altoOffsetFijo: Float,                   // cm
    val altoOffsetCorrediza: Float,              // cm
    val formulaAnchoFijo: (Float) -> Float,      // A fijo = anchoTotal / 2
    val formulaAnchoCorrediza: (Float) -> Float, // A corrediza = anchoFijo + 4

    // Taladros superiores FIJOS (siempre dos simétricos)
    val offXsupFijo: Float,   // cm desde el borde izq para el primer taladro
    val offYsupFijo: Float,   // cm desde el borde superior
    val diamSupFijo: Float,   // cm de diámetro

    // Taladros superiores CORREDIZA (dos posiciones, pueden no ser simétricas)
    val offXsupC1: Float,     // cm para el primer taladro corrediza
    val offYsupC1: Float,
    val offXsupC2: Float,     // cm para el segundo taladro corrediza
    val offYsupC2: Float,
    val diamSupCorrediza: Float,

    // Taladro(es) inferiores (solo corrediza)
    val offXinferior: Float,
    val offYinferior: Float,
    val diamInferior: Float
)

// --------------------------------------------------
// 2) Mapa con parámetros de cada serie
//    (completa con los valores de tus imágenes)
// --------------------------------------------------
private val parametrosPorSerie = mapOf(

    // ------------ A005 ------------
    "A005" to ParametrosSerie(
        altoOffsetFijo        = 0.4f,           // 4 mm
        altoOffsetCorrediza   = 1.3f,           // 13 mm
        formulaAnchoFijo      = { it / 2f },
        formulaAnchoCorrediza = { fijo -> fijo + 4f },

        // taladros superiores FIJO (2 simétricos)
        offXsupFijo     = 12f,  // 120 mm
        offYsupFijo     =  9f,  //  90 mm
        diamSupFijo     =  1.2f, //  12 mm

        // taladros superiores CORREDIZA (ambos a la misma altura)
        offXsupC1       = 10f,  // 100 mm
        offYsupC1       =  6f,  //  60 mm
        offXsupC2       = 10f,  // 100 mm
        offYsupC2       =  6f,  //  60 mm
        diamSupCorrediza=  1.6f, //  16 mm

        // taladro inferior CORREDIZA
        offXinferior    =  5f,  //  50 mm
        offYinferior    =  7f,  //  70 mm
        diamInferior    =  1.0f  //  10 mm
    ),

    // ------------ A001 ------------
    "A001" to ParametrosSerie(
        altoOffsetFijo        = 0.4f,           // 4 mm
        altoOffsetCorrediza   = 1.3f,           // 13 mm
        formulaAnchoFijo      = { it / 2f },
        formulaAnchoCorrediza = { fijo -> fijo + 4f },

        // FIJO
        offXsupFijo     = 12f,  // 120 mm
        offYsupFijo     =  9f,  //  90 mm
        diamSupFijo     =  1.2f, //  12 mm

        // CORREDIZA (solo UNA altura de taladro superior)
        offXsupC1       =  8f,  //  80 mm
        offYsupC1       =  6f,  //  60 mm
        offXsupC2       =  8f,  //  80 mm
        offYsupC2       =  6f,  //  60 mm
        diamSupCorrediza=  1.6f, //  16 mm

        // taladro inferior CORREDIZA (asa/manilla)
        offXinferior    =  6f,  //  60 mm
        offYinferior    =  6f,  //  60 mm  (aprox.; no aparecía explícito)
        diamInferior    =  5f   //  50 mm
    ),

    // ------------ A007 ------------
    "A007" to ParametrosSerie(
        altoOffsetFijo        = 0.4f,
        altoOffsetCorrediza   = 1.3f,
        formulaAnchoFijo      = { it / 2f },
        formulaAnchoCorrediza = { fijo -> fijo + 4f },

        // FIJO
        offXsupFijo     = 12f,  // 120 mm
        offYsupFijo     = 12f,  // 120 mm
        diamSupFijo     =  2.8f, //  28 mm

        // CORREDIZA (2 alturas distintas)
        offXsupC1       = 10f,  // 100 mm
        offYsupC1       =  7.8f,//  78 mm
        offXsupC2       = 10f,  // 100 mm
        offYsupC2       =  6f,  //  60 mm
        diamSupCorrediza=  2.8f, //  28 mm

        // inferior
        offXinferior    =  5f,  //  50 mm
        offYinferior    =  7f,  //  70 mm
        diamInferior    =  1.0f  //  10 mm
    ),

    // ------------ A010 ------------
    "A010" to ParametrosSerie(
        altoOffsetFijo        = 0.8f,   //  H = (Altura puerta) - 8 mm
        altoOffsetCorrediza   = 0.8f,   //  todos corredizos
        formulaAnchoFijo      = { it     }, // no hay fijos
        formulaAnchoCorrediza = { anchoT -> (anchoT + 5f) / 2f }, // (Ancho total+50 mm)/2

        // FIJO
        offXsupFijo     = 0f,  // 120 mm
        offYsupFijo     = 0f,  // 120 mm
        diamSupFijo     =  0f, //  28 mm
        // solo CORREDIZA (2 orificios sup. + 2 inf. centrales)
        offXsupC1       =  8f,  //  80 mm
        offYsupC1       =  2.8f,//  28 mm
        offXsupC2       =  8f,  //  80 mm
        offYsupC2       =  8.8f,//  88 mm
        diamSupCorrediza=  1.7f, //  17 mm

        offXinferior    = 44f,  // 440 mm (centro a centro)
        offYinferior    = 90f,  //  no se especifica
        diamInferior    =  1.0f  //  10 mm
    ),

    // ------------ F1 ------------
    "F1" to ParametrosSerie(
        altoOffsetFijo        = 5f,     // h = (alto fijo) - 50 mm
        altoOffsetCorrediza   = 2f,     // h = (alto móvil) - 20 mm
        formulaAnchoFijo      = { it / 2f },
        formulaAnchoCorrediza = { fijo -> fijo + 5f }, // ancho fijo + 50 mm

        // FIJO: sin taladros
        offXsupFijo     = 0f,
        offYsupFijo     = 0f,
        diamSupFijo     = 0f,

        // CORREDIZA
        offXsupC1       =  5f,  //  50 mm
        offYsupC1       =  2f,  //  20 mm
        offXsupC2       =  5f,  //  50 mm
        offYsupC2       =  2f,  //  20 mm
        diamSupCorrediza=  1.2f, //  12 mm

        offXinferior    =  0f,  // no hay inferior
        offYinferior    =  0f,
        diamInferior    =  0f
    ),

    // ------------ P2 ------------
    "P2" to ParametrosSerie(
        altoOffsetFijo        = 6f,     // h = (alto móvil) - 60 mm
        altoOffsetCorrediza   = 6f,     // idem para fijo/descuento
        formulaAnchoFijo      = { it + 3f }, // (ancho móvil + 30 mm)/2
        formulaAnchoCorrediza = { it + 3f },

        // DESCUENTO DE VIDRIOS (fijo equivalente)
        offXsupFijo     =  0f,
        offYsupFijo     =  0f,
        diamSupFijo     =  0f,

        // PERFORACIONES
        offXsupC1       =  5f,  //  50 mm
        offYsupC1       =  2f,  //  20 mm
        offXsupC2       =  5f,  //  50 mm
        offYsupC2       =  2f,  //  20 mm
        diamSupCorrediza=  1.2f, //  12 mm

        offXinferior    =  4.8f,// 480 mm entre centros → 48 cm
        offYinferior    =  0f,  // no se indica vertical
        diamInferior    =  1.2f //  12 mm
    ),
    "C1" to ParametrosSerie(
        altoOffsetFijo        = 5f,     // h = (alto móvil) - 50 mm
        altoOffsetCorrediza   = 2f,     // h = (alto fijo) - 20 mm
        formulaAnchoFijo      = { it + 3f }, // (ancho móvil + 30 mm)/2
        formulaAnchoCorrediza = { it + 3f },
        // DESCUENTO DE VIDRIOS (fijo equivalente)
        offXsupFijo     =  0f,
        offYsupFijo     =  0f,
        diamSupFijo     =  0f,
        // PERFORACIONES
        offXsupC1       =  5f,  //  50 mm
        offYsupC1       =  2f,  //  20
        offXsupC2       =  5f,  //  50 mm
        offYsupC2       =  2f,  //  20 mm
        diamSupCorrediza=  1.2f, //  12 mm
        offXinferior    =  0f,  // no hay inferior
        offYinferior    =  0f,
        diamInferior    =  0f
    )

    // Si en el futuro añades "C1" o "Plegable", basta con seguir este mismo patrón.
)

// 1) Data class para almacenar los datos de cada puerta calculada
data class DoorData(
    val ancho1: Float,
    val ancho2: Float,    // en algunos casos no se usa (p. ej. P2), pero lo dejamos para C1/A001
    val alto: Float
)






