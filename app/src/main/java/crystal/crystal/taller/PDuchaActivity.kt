package crystal.crystal.taller

import android.annotation.SuppressLint
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.lifecycle.lifecycleScope
import crystal.crystal.R
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

    private val mapDuchas: LinkedHashMap<String, MutableList<DoorData>> = LinkedHashMap()
// Cada vez que pulsemos “Calcular”, meteremos los datos en mapDuchas[serie], de modo
// que cada clave (p.ej. “A005”, “A010”, “C1”…) tenga lista de DoorData acumulados.

    private var combinedBitmap: Bitmap? = null
// Este es el Bitmap “en memoria” que muestra todo el documento actual.
// Cada vez que recalculamos, lo regeneramos 100% desde mapDuchas.

    private val archivos: MutableList<Bitmap> = mutableListOf()
// Aquí vamos guardando copias del combinedBitmap cada vez que se pulsa (click normal) btArchivar.

    private var ultimaSerie: String? = null
// Para verificar si la serie que estamos añadiendo es la MISMA que antes o cambió.

    private lateinit var binding: ActivityPduchaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPduchaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) Al pulsar “Calcular”
        binding.btCalcular.setOnClickListener {
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
        }

        // 2) Al pulsar “Archivar” (click normal)
        binding.btArchivar.setOnClickListener {
            combinedBitmap?.let { archivos.add(it) }
            Toast.makeText(this, "Documento archivado localmente", Toast.LENGTH_SHORT).show()
        }

        // 3) Al hacer LARGO click en “Archivar” → Generar PDF con todo lo de `archivos`
        binding.btArchivar.setOnLongClickListener {
            if (archivos.isEmpty()) {
                Toast.makeText(this, "No hay nada que exportar.", Toast.LENGTH_SHORT).show()
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
    }

    private fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    @SuppressLint("SetTextI18n")
    private fun vidrios() {
        val ducha = ducha ?: return
        val p = parametrosPorSerie[ducha.nombre]
            ?: error("Faltan parámetros para ${ducha.nombre}")
        val altoTotal = alto()

        val texto = when (ducha.nombre) {
            // P2: dos corredizos idénticos = (ancho+3)/2  x (alto-6)
            "P2" -> {
                val a = (ancho1() + 3f) / 2f
                val h = altoTotal - p.altoOffsetCorrediza
                val dim = "${df1(a)} x ${df1(h)}"
                listOf(dim, dim).joinToString("\n")
            }

            // F1: un fijo = ancho/2 x (alto-5)  y un corredizo = (ancho/2+5) x (alto-2)
            "F1" -> {
                val f = ancho1() / 2f
                val hF = altoTotal - p.altoOffsetFijo
                val c = f + 5f
                val hC = altoTotal - p.altoOffsetCorrediza
                "${df1(f)} x ${df1(hF)}\n${df1(c)} x ${df1(hC)}"
            }

            // A010: dos corredizos iguales = (ancho+5)/2 x (alto-0.8)
            "A010" -> {
                val a = (ancho1() + 5f) / 2f
                val h = altoTotal - p.altoOffsetCorrediza
                val dim = "${df1(a)} x ${df1(h)}"
                listOf(dim, dim).joinToString("\n")
            }

            // A005 y A007: un fijo = ancho/2 x (alto-0.4)  y un corredizo = (ancho/2+4) x (alto-1.3)
            "A005", "A007" -> {
                val f = ancho1() / 2f
                val hF = altoTotal - p.altoOffsetFijo
                val c = f + 4f
                val hC = altoTotal - p.altoOffsetCorrediza
                "${df1(f)} x ${df1(hF)}\n${df1(c)} x ${df1(hC)}"
            }

            // A001 y C1: dos anchos distintos, cada uno fijo y corrediza
            // Para A001: corrediza = fijo+4; para C1: corrediza = fijo+5
            "A001", "C1" -> {
                val an1 = ancho1()
                val an2 = ancho2()
                val hF = altoTotal - p.altoOffsetFijo
                val hC = altoTotal - p.altoOffsetCorrediza

                // fijo
                val f1 = an1 / 2f
                val f2 = an2 / 2f

                // corrediza
                val corrOffset = if (ducha.nombre == "A001") 4f else 5f
                val c1 = f1 + corrOffset
                val c2 = f2 + corrOffset

                listOf(
                    "${df1(f1)} x ${df1(hF)}",
                    "${df1(c1)} x ${df1(hC)}",
                    "${df1(f2)} x ${df1(hF)}",
                    "${df1(c2)} x ${df1(hC)}"
                ).joinToString("\n")
            }

            // El resto (ej. Plegable…): un fijo y un corrediza
            else -> {
                val f = ancho1() / 2f
                val hF = altoTotal - p.altoOffsetFijo
                val c = f + 4f      // asumimos +4 para corrediza
                val hC = altoTotal - p.altoOffsetCorrediza
                "${df1(f)} x ${df1(hF)}\n${df1(c)} x ${df1(hC)}"
            }
        }

        binding.tvVidrios.text = texto
        // contar solo líneas de dimensiones
        cant = texto.lines().size
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
        val s = ducha ?: return
        val serie = s.nombre

        // 1) Leer las medidas de entrada (convertir a Float)
        val a1 = binding.etAncho1.text.toString().toFloat()
        val h  = binding.etAlto.text.toString().toFloat()
        // Ancho2 solo si la serie es C1 o A001, en el resto lo ignoramos
        val a2 = if (serie == "C1" || serie == "A001") {
            binding.etAncho2.text.toString().toFloat()
        } else {
            0f
        }

        // 2) Insertar en el map
        val listaExistente = mapDuchas.getOrPut(serie) { mutableListOf() }
        listaExistente.add(DoorData(a1, a2, h))

        // 3) Reconstruir COMBINED bitmap: hay que iterar el mapDuchas en orden de inserción
        rebuildCombinedBitmap()

        // 4) Mostrar texto de cuántas puertas hay en total (opcional, si lo necesitas)
        val totalPuertas = mapDuchas.values.sumBy { it.size }
        binding.tvParante.text = totalPuertas.toString()
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
        serie: String,
        datos: List<DoorData>
    ): Bitmap {
        // 1) Métricas de pantalla
        val d = resources.displayMetrics.density
        val tamTxt = 14f * d
        val labelSize = tamTxt * 1.5f
        val margen = (8f * d).toInt()
        val filaAltoPx = (24f * d).toInt()
        val tick = 4f * d

        // 2) Altura de cada sección
        val tituloAlto = (32f * d).toInt()
        val filas = datos.size
        val espacioTabla = (8f * d).toInt()
        val imgAlto = (80f * d).toInt()

        // 3) Ancho de la tabla: ancho de pantalla menos márgenes
        val screenW = resources.displayMetrics.widthPixels
        val anchoTabla = screenW - 2 * margen

        // 4) Calcular altura total: margen superior + título + espacio + (filas × altoFila) + espacio + altoImagen + margen inferior
        val totalAlto = margen +
                tituloAlto +
                espacioTabla +
                filaAltoPx * filas +
                espacioTabla +
                imgAlto +
                margen

        // 5) Crear bitmap y canvas
        val bmpW = anchoTabla + 2 * margen
        val bmpH = totalAlto
        val bmp = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)

        // 6) Configurar paints
        val paintTexto = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            textSize = tamTxt
        }
        val paintTitulo = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            textSize = labelSize
            textAlign = Paint.Align.CENTER
        }
        val paintFondo = Paint().apply {
            color = Color.parseColor("#DDDDDD")
        }

        // 7) Dibujar TÍTULO gris con texto centrado
        val rectTitulo = Rect(
            margen,
            margen,
            margen + anchoTabla,
            margen + tituloAlto
        )
        canvas.drawRect(rectTitulo, paintFondo)
        canvas.drawText(
            "Vidrio Puerta Ducha ${serie.toUpperCase()}",
            rectTitulo.left + anchoTabla / 2f,
            rectTitulo.top + tituloAlto / 2f + labelSize / 3f,
            paintTitulo
        )

        // 8) Dibujar encabezado de la tabla
        val yEncabezado = rectTitulo.bottom + espacioTabla
        paintTexto.textAlign = Paint.Align.LEFT
        canvas.drawText(
            "N°   |    Hoja     |   Ancho (mm)   |   Alto (mm)   |   Tirador (mm)",
            margen + (4f * d),
            yEncabezado.toFloat(),
            paintTexto
        )

        // 9) Línea separadora debajo del encabezado
        val yLinea = yEncabezado + (4f * d)
        canvas.drawLine(
            margen + (4f * d),
            yLinea,
            margen + anchoTabla - (4f * d),
            yLinea,
            paintTexto
        )

        // 10) Dibujar cada fila de datos
        var yFila = yLinea + (filaAltoPx.toFloat())
        datos.forEachIndexed { idx, door ->
            val sb = StringBuilder().apply {
                append(String.format("%-4s", "${idx + 1}"))      // N°
                append(" |  ")
                // Determinar “Hoja” según la serie
                when (serie) {
                    "P2" -> append(String.format("%-10s", "Corrediza"))
                    "F1" -> {
                        if (door.ancho2 == 0f) append(String.format("%-10s", "Fija"))
                        else append(String.format("%-10s", "Corrediza"))
                    }
                    "A010", "A005", "A007" -> {
                        // En estos casos asumimos que siempre es “Corrediza”
                        append(String.format("%-10s", "Corrediza"))
                    }
                    "A001", "C1" -> {
                        // Una DoorData agrupa fija+móvil
                        append(String.format("%-10s", "Fija+Móvil"))
                    }
                    else -> {
                        // Cualquier otra serie no listada
                        append(String.format("%-10s", "Corrediza"))
                    }
                }
                append(" |  ")
                // Ancho(s)
                if (serie == "C1" || serie == "A001") {
                    // Convertir a mm multiplicando por 10 (asumiendo que guardas en decenas de cm)
                    append(String.format("%-13s", "${df1(door.ancho1 * 10f)}/${df1(door.ancho2 * 10f)}"))
                    append(" |  ")
                } else {
                    append(String.format("%-13s", df1(door.ancho1 * 10f)))
                    append(" |  ")
                }
                // Alto
                append(String.format("%-12s", df1(door.alto * 10f)))
                append(" |  ")
                // Tirador
                if (serie == "A005" || serie == "A001") {
                    append("944")
                } else {
                    append("-")
                }
            }.toString()

            paintTexto.textAlign = Paint.Align.LEFT
            canvas.drawText(
                sb,
                margen + (4f * d),
                yFila,
                paintTexto
            )
            yFila += filaAltoPx.toFloat()
        }

        // 11) Dibujar la IMAGEN real de la serie (desde drawable/) debajo de la tabla
        val yArribaImg = yLinea + filaAltoPx * filas + espacioTabla

        // 1) Mapear cada nombre de serie a un recurso drawable
        val resId = when (serie.uppercase(Locale.getDefault())) {
            "A005" -> R.mipmap.a005
            "A001" -> R.mipmap.a001
            "A007" -> R.mipmap.a007
            "A010" -> R.mipmap.a010
            "F1"   -> R.mipmap.f1

            //"P2"   -> R.drawable.p2
            //"C1"   -> R.drawable.c1
            //"PLEGABLE" -> R.drawable.plegable
            else   -> 0
        }

        // 2) Si existe el recurso, lo dibujamos; si no, dibujamos un rectángulo “Sin imagen”
        if (resId != 0) {
            val icon = BitmapFactory.decodeResource(resources, resId)
            // Calcular ancho deseado: la mitad del ancho de la tabla, manteniendo proporción
            val anchoDeseado = anchoTabla / 2
            val relacion = icon.height.toFloat() / icon.width.toFloat()
            val altoDeseado = (anchoDeseado * relacion).toInt()
            // Centrar horizontalmente
            val leftImg = margen + (anchoTabla - anchoDeseado) / 2
            val topImg = yArribaImg.toInt()
            val rectImg = Rect(
                leftImg,
                topImg,
                leftImg + anchoDeseado,
                topImg + altoDeseado
            )
            canvas.drawBitmap(icon, null, rectImg, null)
        } else {
            // Si no existe el drawable, dibujamos un rectángulo contorno con “Sin imagen”
            val rectImg = Rect(
                margen + (anchoTabla / 4),
                yArribaImg.toInt(),
                margen + anchoTabla - (anchoTabla / 4),
                yArribaImg.toInt() + imgAlto
            )
            val paintMarco = Paint().apply {
                style = Paint.Style.STROKE
                strokeWidth = 2f * d
                color = Color.BLACK
            }
            canvas.drawRect(rectImg, paintMarco)
            paintTexto.textAlign = Paint.Align.CENTER
            paintTexto.textSize = (16f * d)
            canvas.drawText(
                "Sin imagen",
                rectImg.exactCenterX(),
                rectImg.exactCenterY() + (8f * d),
                paintTexto
            )
        }


        return bmp
    }


    private fun generarPdfDesdeArchivos() {
        // 1) Crear un objeto PdfDocument
        val pdf = PdfDocument()

        // 2) Iterar cada Bitmap en `archivos` y lo ponemos en una página distinta
        archivos.forEachIndexed { index, bmp ->
            // Cada página tendrá el tamaño exacto del bitmap (o podrías escalarlo si quieres)
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
            binding.tvDucha.text = "Puerta Ducha ${ducha!!.nombre}"

            // 2) Toggle visibilidad de lyAncho2 **inmediatamente** según el nombre
            binding.lyAncho2.visibility =
                if (ducha!!.nombre in listOf("A001", "C1")) View.VISIBLE
                else View.GONE

            // 3) Prepara el siguiente índice
            indice = (indice + 1) % listaDuchas.size
        } else {
            binding.tvDucha.text = "No disponibles"
            binding.lyAncho2.visibility = View.GONE
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            val perfiles = mapOf(
                "Parante" to ModoMasivoHelper.texto(binding.tvParante)
            ).filter { it.value.isNotBlank() }

            ModoMasivoHelper.devolverResultado(
                activity = this,
                calculadora = "Puerta Ducha",
                perfiles = perfiles,
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

data class SerieDucha(val nombre: String)

val listaDuchas= listOf(
    SerieDucha("P2"),
    SerieDucha("F1"),
    SerieDucha("C1"),
    SerieDucha("A010"),
    SerieDucha("A005"),
    SerieDucha("A007"),
    SerieDucha("A001"),
    SerieDucha("Plegable")
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






