package crystal.crystal.Diseno.vitroven

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityVitroBinding
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class VitroActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ANCHO = "extra_ancho"
        const val EXTRA_ALTO = "extra_alto"
        const val EXTRA_CLIPS = "extra_clips"
        const val EXTRA_CLASIFICACION = "extra_clasificacion"
        const val EXTRA_DISENO_SIMBOLICO = "extra_diseno_simbolico"
        const val EXTRA_DIRECCION_VERTICAL = "extra_direccion_vertical"

        const val RESULT_DISENO_SIMBOLICO = "result_diseno_simbolico"
        const val RESULT_DIRECCION_VERTICAL = "result_direccion_vertical"
        const val RESULT_CLIPS = "result_clips"
    }

    private lateinit var binding: ActivityVitroBinding
    private val render = RenderVitroven()
    private val clipCm = 9.6f
    private var anchoBase: Float = 100f
    private var altoBase: Float = 100f
    private var clasificacionBase: String = ""
    private var ultimoTouchX: Float = -1f
    private var ultimoTouchY: Float = -1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVitroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ancho = intent.getFloatExtra(EXTRA_ANCHO, 100f).coerceAtLeast(1f)
        val alto = intent.getFloatExtra(EXTRA_ALTO, 100f).coerceAtLeast(1f)
        val clips = intent.getIntExtra(EXTRA_CLIPS, 0).coerceAtLeast(0)
        val clasificacion = intent.getStringExtra(EXTRA_CLASIFICACION).orEmpty()
        val diseno = intent.getStringExtra(EXTRA_DISENO_SIMBOLICO).orEmpty()
        val direccionVertical = intent.getBooleanExtra(EXTRA_DIRECCION_VERTICAL, false)
        anchoBase = ancho
        altoBase = alto
        clasificacionBase = clasificacion

        val disenoVisible = normalizarDisenoParaEdicion(diseno, clasificacion, ancho, alto)
        binding.etDisenoManual.setText(disenoVisible)
        binding.etClipsManual.setText(clips.toString())
        binding.etDireccionManual.hint = if (direccionVertical) "Vertical" else "Horizontal"

        binding.etDireccionManual.setOnClickListener {
            val actual = binding.etDireccionManual.hint?.toString()?.lowercase().orEmpty()
            binding.etDireccionManual.hint = if (actual == "vertical") "Horizontal" else "Vertical"
            renderizar(ancho, alto, clasificacion)
        }

        binding.btVistaPrevia.setOnClickListener {
            renderizar(ancho, alto, clasificacion)
        }

        binding.btGuardar.setOnClickListener {
            val out = Intent().apply {
                putExtra(RESULT_DISENO_SIMBOLICO, binding.etDisenoManual.text?.toString().orEmpty())
                putExtra(RESULT_DIRECCION_VERTICAL, esDireccionVertical())
                putExtra(RESULT_CLIPS, clipsPorVitrovenDesdeTotal())
            }
            setResult(RESULT_OK, out)
            finish()
        }

        binding.ivDiseno.setOnTouchListener { _, e ->
            if (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_UP) {
                ultimoTouchX = e.x
                ultimoTouchY = e.y
            }
            false
        }
        binding.ivDiseno.setOnClickListener {
            abrirEditorModuloDesdeToque()
        }

        renderizar(ancho, alto, clasificacion)
    }

    private fun normalizarDisenoParaEdicion(
        disenoCrudo: String,
        clasificacion: String,
        ancho: Float,
        alto: Float
    ): String {
        val esEspecifico = disenoCrudo.contains("<") ||
                disenoCrudo.contains("vitroven[", ignoreCase = true) ||
                disenoCrudo.contains("vitrovén[", ignoreCase = true)
        val parseExistente = VitroSimbolicoParser.parse(disenoCrudo)
        if (esEspecifico && parseExistente.modulos.isNotEmpty()) {
            val vertical = parseExistente.segmentadoVertical
            val detalle = parseExistente.modulos.joinToString("") { token ->
                val tipo = if (vertical) token.tipo.uppercaseChar() else token.tipo.lowercaseChar()
                val medida = token.medida ?: if (vertical) (alto / parseExistente.modulos.size.toFloat()) else (ancho / parseExistente.modulos.size.toFloat())
                "$tipo<${df1(medida)}>"
            }
            return "{vitroven[${df1(ancho)},${df1(alto)}:$detalle]}"
        }

        return when (clasificacion.lowercase()) {
            "v" -> "{vitroven[${df1(ancho)},${df1(alto)}:v<${df1(ancho)}>]}"
            "vv" -> {
                val w = ancho / 2f
                "{vitroven[${df1(ancho)},${df1(alto)}:v<${df1(w)}>v<${df1(w)}>]}"
            }
            "vf" -> {
                val w = ancho / 2f
                "{vitroven[${df1(ancho)},${df1(alto)}:v<${df1(w)}>f<${df1(w)}>]}"
            }
            "fvf" -> {
                val w = ancho / 3f
                "{vitroven[${df1(ancho)},${df1(alto)}:f<${df1(w)}>v<${df1(w)}>f<${df1(w)}>]}"
            }
            "vb" -> {
                val altoV = altoVitroReferencia(alto, "vb")
                val altoF = (alto - altoV).coerceAtLeast(0.1f)
                "{vitroven[${df1(ancho)},${df1(alto)}:V<${df1(altoV)}>F<${df1(altoF)}>]}"
            }
            "bvm" -> {
                val altoV = altoVitroReferencia(alto, "bvm")
                val altoF = ((alto - altoV) / 2f).coerceAtLeast(0.1f)
                "{vitroven[${df1(ancho)},${df1(alto)}:F<${df1(altoF)}>V<${df1(altoV)}>F<${df1(altoF)}>]}"
            }
            else -> disenoCrudo
        }
    }

    private fun altoVitroReferencia(alto: Float, clasificacion: String): Float {
        val clip = 9.6f
        return when (clasificacion.lowercase()) {
            "vb" -> ((((alto / 3f) * 2f) / clip).toInt() * clip).coerceAtLeast(0.1f)
            "bvm" -> ((((alto / 4f) * 2f) / clip).toInt() * clip).coerceAtLeast(0.1f)
            else -> alto.coerceAtLeast(0.1f)
        }
    }

    private fun esDireccionVertical(): Boolean {
        return binding.etDireccionManual.hint?.toString()?.equals("Vertical", ignoreCase = true) == true
    }

    private fun clipsActuales(): Int {
        return binding.etClipsManual.text?.toString()?.toIntOrNull()?.coerceAtLeast(0) ?: 0
    }

    private fun clipsPorVitrovenDesdeTotal(): Int {
        val total = clipsActuales()
        val v = VitroSimbolicoParser.parse(
            binding.etDisenoManual.text?.toString().orEmpty()
        ).modulos.count { it.tipo.lowercaseChar() == 'v' }
        if (v <= 0) return total
        return (total.toFloat() / v.toFloat()).roundToInt().coerceAtLeast(0)
    }

    private fun abrirEditorModuloDesdeToque() {
        val parse = VitroSimbolicoParser.parse(binding.etDisenoManual.text?.toString().orEmpty())
        if (parse.modulos.isEmpty()) {
            Toast.makeText(this, "No hay módulos para editar", Toast.LENGTH_SHORT).show()
            return
        }
        val idx = indiceModuloPorToque(parse)
        if (idx < 0) {
            Toast.makeText(this, "Toque sobre un módulo", Toast.LENGTH_SHORT).show()
            return
        }
        mostrarDialogoEditarModulo(parse, idx)
    }

    private fun indiceModuloPorToque(parse: VitroSimbolicoParse): Int {
        val w = binding.ivDiseno.width.toFloat().coerceAtLeast(1f)
        val h = binding.ivDiseno.height.toFloat().coerceAtLeast(1f)
        val padding = min(w, h) * 0.08f
        val escala = min(
            (w - (padding * 2f)) / anchoBase.coerceAtLeast(1f),
            (h - (padding * 2f)) / altoBase.coerceAtLeast(1f)
        ).coerceAtLeast(0.1f)

        val anchoTotalPx = anchoBase * escala
        val altoTotalPx = altoBase * escala
        val x0 = (w - anchoTotalPx) / 2f
        val y0 = (h - altoTotalPx) / 2f
        val x1 = x0 + anchoTotalPx
        val y1 = y0 + altoTotalPx

        val tx = ultimoTouchX
        val ty = ultimoTouchY
        if (tx < x0 || tx > x1 || ty < y0 || ty > y1) return -1

        val n = parse.modulos.size.coerceAtLeast(1)
        val vertical = parse.segmentadoVertical
        val medidas = medidasConFallback(parse, n)
        val total = medidas.sum().coerceAtLeast(0.1f)
        var acum = 0f
        for (i in 0 until n) {
            val fr = medidas[i] / total
            val ini = acum
            val fin = acum + fr
            acum = fin
            val coordRel = if (vertical) {
                ((ty - y0) / (y1 - y0)).coerceIn(0f, 1f)
            } else {
                ((tx - x0) / (x1 - x0)).coerceIn(0f, 1f)
            }
            val dentro = coordRel >= ini && (coordRel < fin || i == n - 1)
            if (dentro) return i
        }
        return max(0, n - 1)
    }

    private fun medidasConFallback(parse: VitroSimbolicoParse, n: Int): List<Float> {
        return parse.modulos.map {
            it.medida?.coerceAtLeast(0.1f)
                ?: if (parse.segmentadoVertical) (altoBase / n.toFloat()) else (anchoBase / n.toFloat())
        }
    }

    private fun mostrarDialogoEditarModulo(parse: VitroSimbolicoParse, indice: Int) {
        val modulos = parse.modulos.toMutableList()
        val modulo = modulos[indice]
        val vertical = parse.segmentadoVertical
        val medidas = medidasConFallback(parse, modulos.size)
        val medidaActual = medidas[indice]
        val tipo = modulo.tipo.lowercaseChar()

        val wrap = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 20, 36, 0)
        }
        val etMedida = EditText(this).apply {
            hint = if (vertical) "Alto módulo" else "Ancho módulo"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(df1(medidaActual))
        }
        wrap.addView(etMedida)

        val etClipsModulo = EditText(this).apply {
            hint = "Clips módulo V"
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(clipsDesdeMedida(medidaActual).toString())
        }
        if (tipo == 'v') wrap.addView(etClipsModulo)

        AlertDialog.Builder(this)
            .setTitle("Módulo ${tipo.uppercaseChar()} ${indice + 1}")
            .setView(wrap)
            .setPositiveButton("Aplicar") { _, _ ->
                val nuevaMedida = etMedida.text?.toString()?.replace(',', '.')?.toFloatOrNull()
                if (nuevaMedida == null || nuevaMedida <= 0f) {
                    Toast.makeText(this, "Medida inválida", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                var medidaFinal = nuevaMedida
                if (tipo == 'v') {
                    val clipsModulo = etClipsModulo.text?.toString()?.toIntOrNull()?.coerceAtLeast(0) ?: 0
                    if (clipsModulo > 0) {
                        // En módulo V, clips gobierna la medida para que también cambie el tamaño.
                        medidaFinal = clipsModulo * clipCm
                    }
                    val vCount = modulos.count { it.tipo.lowercaseChar() == 'v' }.coerceAtLeast(1)
                    binding.etClipsManual.setText((clipsModulo * vCount).toString())
                }
                modulos[indice] = VitroModuloToken(modulo.tipo, medidaFinal)
                reequilibrarModulos(modulos, vertical, indice)
                val nuevoDiseno = construirDisenoCanonico(modulos, vertical, anchoBase, altoBase)
                binding.etDisenoManual.setText(nuevoDiseno)
                renderizar(anchoBase, altoBase, clasificacionBase)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun construirDisenoCanonico(
        modulos: List<VitroModuloToken>,
        vertical: Boolean,
        ancho: Float,
        alto: Float
    ): String {
        val detalle = modulos.joinToString("") { m ->
            val tipo = if (vertical) m.tipo.uppercaseChar() else m.tipo.lowercaseChar()
            val medida = (m.medida ?: 0.1f).coerceAtLeast(0.1f)
            "$tipo<${df1(medida)}>"
        }
        return "{vitroven[${df1(ancho)},${df1(alto)}:$detalle]}"
    }

    private fun clipsDesdeMedida(medida: Float): Int {
        if (medida <= 0f) return 0
        val c = (medida / clipCm) + 0.0001f
        return c.toInt().coerceAtLeast(0)
    }

    private fun reequilibrarModulos(
        modulos: MutableList<VitroModuloToken>,
        vertical: Boolean,
        indiceEditado: Int
    ) {
        if (modulos.isEmpty() || indiceEditado !in modulos.indices) return
        val total = if (vertical) altoBase else anchoBase
        val minModulo = 0.1f
        val tipos = modulos.map { it.tipo.lowercaseChar() }
        val medidaEditada = (modulos[indiceEditado].medida ?: minModulo).coerceAtLeast(minModulo)

        // Caso VF / FV (un fijo + un vitroven): actualizar complementario.
        if (modulos.size == 2 && tipos.count { it == 'v' } == 1 && tipos.count { it == 'f' } == 1) {
            val otro = if (indiceEditado == 0) 1 else 0
            val medidaEditadaClamped = medidaEditada.coerceAtMost((total - minModulo).coerceAtLeast(minModulo))
            val medidaOtro = (total - medidaEditadaClamped).coerceAtLeast(minModulo)
            modulos[indiceEditado] = modulos[indiceEditado].copy(medida = medidaEditadaClamped)
            modulos[otro] = modulos[otro].copy(medida = medidaOtro)
            return
        }

        // Caso FVF / FVF vertical: mantener fijos simétricos y ajustar el complementario.
        if (modulos.size == 3 && tipos.first() == 'f' && tipos[1] == 'v' && tipos.last() == 'f') {
            val idxV = 1
            val idxF1 = 0
            val idxF2 = 2
            if (indiceEditado == idxV) {
                val medidaV = medidaEditada.coerceAtMost((total - (2f * minModulo)).coerceAtLeast(minModulo))
                val medidaF = ((total - medidaV) / 2f).coerceAtLeast(minModulo)
                modulos[idxV] = modulos[idxV].copy(medida = medidaV)
                modulos[idxF1] = modulos[idxF1].copy(medida = medidaF)
                modulos[idxF2] = modulos[idxF2].copy(medida = medidaF)
            } else {
                val medidaF = medidaEditada.coerceAtMost(((total - minModulo) / 2f).coerceAtLeast(minModulo))
                val medidaV = (total - (2f * medidaF)).coerceAtLeast(minModulo)
                modulos[idxV] = modulos[idxV].copy(medida = medidaV)
                modulos[idxF1] = modulos[idxF1].copy(medida = medidaF)
                modulos[idxF2] = modulos[idxF2].copy(medida = medidaF)
            }
        }
    }

    private fun renderizar(ancho: Float, alto: Float, clasificacion: String) {
        binding.ivDiseno.post {
            val bmp = render.renderizarBitmap(
                binding.ivDiseno.width.coerceAtLeast(1),
                binding.ivDiseno.height.coerceAtLeast(1),
                ParametrosVitroven(
                    anchoTotalCm = ancho,
                    altoTotalCm = alto,
                    clips = clipsPorVitrovenDesdeTotal(),
                    clasificacion = clasificacion,
                    disenoSimbolico = binding.etDisenoManual.text?.toString().orEmpty(),
                    direccionVertical = esDireccionVertical()
                )
            )
            binding.ivDiseno.setImageBitmap(bmp)
        }
    }

    private fun df1(defo: Float): String {
        return if (defo % 1 == 0f) {
            defo.toInt().toString()
        } else {
            "%.1f".format(defo).replace(",", ".")
        }
    }
}
