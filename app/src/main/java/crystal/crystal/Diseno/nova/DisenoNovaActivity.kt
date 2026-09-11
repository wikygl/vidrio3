package crystal.crystal.Diseno.nova

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.InputType
import android.text.Spanned
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.R
import crystal.crystal.databinding.ActivityDisenoNovaBinding
import kotlin.math.abs
import kotlin.math.max

class DisenoNovaActivity : AppCompatActivity() {

    companion object {
        /** Marca la fila de módulos de cada tramo en el panel de cotas, para poder pulsarla. */
        private const val TAG_MODULOS = "cotas_modulos_"
        private const val TAG_FLOTANTE = "flotante_modulos"
        const val EXTRA_PAQUETE = "extra_paquete_diseno"
        const val EXTRA_MOCHETA_LATERAL_CM = "extra_mocheta_lateral_cm"
        const val EXTRA_HEADLESS = "extra_headless"
        const val EXTRA_RET_PADDING_PX = "extra_ret_padding_px"
        const val EXTRA_OUTPUT_FORMAT = "extra_output_format" // "svg" | "png"
        const val EXTRA_US_CM = "extra_us_cm"
        const val EXTRA_ENCUENTRO_VACIO = "extra_encuentro_vacio"
        const val EXTRA_DIRECCION = "extra_direccion"
        const val RESULT_URI = "resultado_uri_imagen"
        const val RESULT_PAQUETE = "resultado_paquete"
    }

    // ----------------- Estado del diseño (Float) -----------------
    private var clase: String = "nova"
    private var tipo: TipoEnsamble = TipoEnsamble.APA
    private var anchoCm: Float = 150f
    private var altoCm: Float = 120f
    private var mochetaLateralCm: Float = 0f
    private var encuentroVacio: String = "1111"
    private var direccion: String = "adentro"
    private var usCm: Float = 1.5f
    private var corteVerticalCm: Float? = null
    /** El mando flotante de módulos que sale junto a la franja tocada. */
    private var flotanteModulos: View? = null
    /** Dónde quedó el mando, para no perderlo mientras el dibujo vuelve a medirse. */
    private var flotantePos: Pair<Float, Float>? = null
    private var paqueteOriginal: String = ""
    private var estructuraEditada: Boolean = false
    private val franjas: MutableList<Franja> = mutableListOf() // orden: abajo→arriba
    private var indiceFranjaActiva: Int = -1
    private var indiceTramoActivo: Int = -1
    private var indiceModuloActivo: Int = -1
    private val anchoParanteCm = 2.5f
    private val tramosBlockeados = mutableListOf<Boolean>()

    // ----------------- Binding -----------------
    private lateinit var binding: ActivityDisenoNovaBinding

    // ----------------- Tipos -----------------
    enum class TipoEnsamble { INA, APA }
    enum class TipoModulo { FIJO, CORREDIZA }
    data class Franja(
        var esSistema: Boolean,             // true = s ; false = m
        var alturaCm: Float = 0f,           // 0 => auto
        val modulos: MutableList<TipoModulo> = mutableListOf(),
        val parantes: MutableList<Int> = mutableListOf() // índices donde hay ;P; (antes del módulo en esa posición)
    )

    // =========================================================================================
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ---- MODO HEADLESS (genera imagen y termina) ----
        // Sin diseño de entrada se empieza de cero: en vez del fijo suelto que había escrito aquí
        // a mano (150x120, un módulo), la ventana sale ya repartida con las reglas de siempre.
        val paqueteIntent = intent.getStringExtra(EXTRA_PAQUETE)
            ?: runCatching { DisenoNova.nuevo("ina", 150f, 120f, altoHoja = 120f).aPaquete() }
                .getOrElse { "{nova,ina,[150,120:Tl<150>(s(f))]}" }
        mochetaLateralCm = intent.getFloatExtra(EXTRA_MOCHETA_LATERAL_CM, 0f)
        encuentroVacio = intent.getStringExtra(EXTRA_ENCUENTRO_VACIO) ?: "1111"
        direccion = intent.getStringExtra(EXTRA_DIRECCION) ?: "adentro"
        usCm = intent.getFloatExtra(EXTRA_US_CM, 1.5f)
        val paddingPx = intent.getIntExtra(EXTRA_RET_PADDING_PX, 0)
        val headless = intent.getBooleanExtra(EXTRA_HEADLESS, false)
        val formato = intent.getStringExtra(EXTRA_OUTPUT_FORMAT) ?: "png"

        if (headless) {
            val vista = VistaDiseno(this).apply {
                actualizarDesdePaquete(paqueteIntent, 0f, 0f, mochetaLateralCm)
                setEncuentroVacio(encuentroVacio)
                setDireccion(direccion)
            }
            val dm = resources.displayMetrics
            val w = dm.widthPixels.coerceAtLeast(720)
            val h = dm.heightPixels.coerceAtLeast(720)
            vista.layout(0, 0, w, h)

            val uri = if (formato == "svg") {
                val svg = vista.exportarSoloDisenoSVG(paddingPx = paddingPx)
                guardarTextoEnCacheYUri(svg, "diseno_${System.currentTimeMillis()}.svg")
            } else {
                val bmp = vista.exportarSoloDisenoBitmap(paddingPx = paddingPx)
                guardarPngEnCacheYUri(bmp, "diseno_${System.currentTimeMillis()}.png")
            }

            setResult(RESULT_OK, Intent().apply {
                putExtra(RESULT_URI, uri.toString())
                data = uri
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            })
            finish()
            return
        }

        // ---- MODO INTERACTIVO ----
        binding = ActivityDisenoNovaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.vistaDiseno.setEncuentroVacio(encuentroVacio)
        binding.vistaDiseno.setDireccion(direccion)
        cargarDesdePaquete(paqueteIntent)

        // Toque directo en el lienzo para seleccionar franja (sin diálogos)
        binding.vistaDiseno.alClicFranja = { idx ->
            indiceFranjaActiva = idx
            indiceModuloActivo = -1
            binding.vistaDiseno.resaltarFranja(idx, indiceTramoActivo)
            actualizarInfoSeleccion()
            actualizarVista()
        }
        binding.vistaDiseno.alClicFranjaTramo = { tramo, franja ->
            indiceTramoActivo = tramo
            indiceFranjaActiva = franja
            indiceModuloActivo = -1
            binding.vistaDiseno.resaltarFranja(franja, tramo)
            actualizarInfoSeleccion()
            actualizarVista()
            // El mando de módulos sale junto a la franja recién tocada.
            mostrarFlotanteModulos()
        }
        // Doble click en módulo para seleccionarlo
        binding.vistaDiseno.alDobleClicModulo = { franja, modulo ->
            if (indiceFranjaActiva == franja && indiceModuloActivo == modulo && indiceTramoActivo >= 0) {
                // Mismo módulo pulsado de nuevo → editar ancho
                dialogoEditarAnchoModulo()
            } else {
                indiceFranjaActiva = franja
                indiceModuloActivo = modulo
                binding.vistaDiseno.resaltarModulo(franja, modulo)
                actualizarInfoSeleccion()
            }
        }

        // Tocar el lienzo (fuera del panel) cierra los paneles de contenido abiertos.
        binding.vistaDiseno.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN && cerrarPanelesContenido()) {
                v.performClick()
                true
            } else {
                false
            }
        }

        // El botón de la izquierda abre el menú de opciones del diseño.
        binding.btnMostrarOcultar.setOnClickListener { mostrarMenuEditar() }
        binding.btnMedidasRapidas.setOnClickListener { dialogoCambiarMedidas() }
        binding.btnCotasPlanos.setOnClickListener { togglePanelCotasPlanos() }
        binding.btnTipoEnsamble.setOnClickListener { alternarEnsamble() }
        // El botón de la calculadora hace lo que hacía el "Volver" de abajo: envía el diseño a
        // NovaCorrediza y cierra. El botón Atrás sigue saliendo SIN aplicar los cambios.
        binding.btnEnviarCalculadora.setOnClickListener {
            prepararResultadoDiseno()
            finish()
        }
        binding.btnLimpiarDiseno.setOnClickListener { limpiarDiseno() }

        // El lienzo se sube o se baja solo, según lo que ocupe el bloque de controles: al abrir
        // el panel de cotas con varios tramos el dibujo se encoge en vez de quedar tapado.
        binding.overlayControles.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->
            if (bottom - top != oldBottom - oldTop) subirLienzoSobreLosControles()
        }

        actualizarInfoSeleccion()
        actualizarVista()
    }

    // ===================================== MENÚ EDITAR =====================================

    // Menú con las opciones que NO tienen botón propio. (Ensamble, medidas y limpiar viven en sus
    // botones: btnTipoEnsamble, btnMedidasRapidas, btnLimpiarDiseno.)
    private fun mostrarMenuEditar() {
        val opciones = arrayOf(
            "Seleccionar franja activa",
            "Editar altura de franja activa",     // solo altura (sin cambiar tipo)
            "Cambiar tipo S↔M (franja activa)",   // sin diálogo
            "Seleccionar módulo de franja activa",
            "Ver / copiar paquete"
        )
        AlertDialog.Builder(this)
            .setTitle("Opciones")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> dialogoSeleccionarFranjaActiva()
                    1 -> dialogoEditarFranjaAlturaSolo()
                    2 -> alternarTipoFranjaActiva()
                    3 -> dialogoSeleccionarModulo()
                    4 -> dialogoVerPaquete()
                }
            }
            .show()
    }

    /** Cierra el panel de cotas y el mando flotante si están a la vista. */
    private fun cerrarPanelesContenido(): Boolean {
        var cerro = false
        if (flotanteModulos != null) { cerrarFlotanteModulos(); cerro = true }
        if (binding.panelCotasPlanos.visibility == View.VISIBLE) {
            binding.panelCotasPlanos.visibility = View.GONE; cerro = true
        }
        return cerro
    }

    private fun togglePanelCotasPlanos() {
        val panel = binding.panelCotasPlanos
        if (panel.visibility == View.VISIBLE) {
            panel.visibility = View.GONE
            return
        }
        actualizarPanelCotas()
        panel.visibility = View.VISIBLE
    }

    // --- Ver / copiar paquete ---
    private fun dialogoVerPaquete() {
        val paquete = paqueteActualLectura()
        AlertDialog.Builder(this)
            .setTitle("Paquete")
            .setMessage(paquete)
            .setPositiveButton("Copiar") { _, _ -> copiarAlPortapapeles(paquete) }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun copiarAlPortapapeles(texto: String) {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("paquete", texto))
        Toast.makeText(this, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
    }

    // =========================== ACCIONES: FRANJAS / MÓDULOS ===========================

    /**
     * Agrega una franja al tramo [indiceTramo] eligiendo qué es y cuánto mide. El toque simple
     * del + agrega una mocheta en automático; esto es para cuando hace falta decidirlo.
     */
    private fun dialogoAgregarFranjaEnTramo(indiceTramo: Int) {
        var esSistema = false
        val opciones = arrayOf("m (mocheta)", "s (sistema)")
        AlertDialog.Builder(this)
            .setTitle("Agregar franja al tramo ${indiceTramo + 1}")
            .setSingleChoiceItems(opciones, 0) { _, which -> esSistema = which == 1 }
            .setPositiveButton("Siguiente") { dlg, _ ->
                dlg.dismiss()
                val et = EditText(this).apply {
                    hint = "Altura en cm (0 = auto)"
                    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    setText(df1(0f))
                    setPadding(32, 24, 32, 16)
                }
                AlertDialog.Builder(this)
                    .setTitle("Altura de la franja")
                    .setView(et)
                    .setPositiveButton("Agregar") { _, _ ->
                        var alto = max(0f, et.text.toString().aNumeroSeguro())
                        if (alto > altoCm) {
                            Toast.makeText(this, "Altura > alto total. Se agrega en auto.", Toast.LENGTH_SHORT).show()
                            alto = 0f
                        }
                        cambiarEstructura { it.conFranjaAgregadaEnTramo(indiceTramo, esSistema, alto) }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Camino único de edición de franjas: aplica [transform] a la lista de franja-tokens
     * (s<>/m<>) de CADA bloque de tramo, manteniendo la cadena simbólica de tramos como
     * fuente de verdad, y recarga el modelo desde ella. Devuelve false si el paquete actual
     * no está en forma de bloques T<> (en cuyo caso el llamador usa el camino heredado).
     */
    private fun editarFranjasEnTramos(transform: (MutableList<String>) -> Unit): Boolean {
        val bloques = parsearBloquesTramo()
        if (bloques.isEmpty()) return false
        val nuevos = bloques.map { b ->
            val tokens = splitTopLevelSemicolon(b.contenido).toMutableList()
            transform(tokens)
            if (tokens.isEmpty()) tokens.add("s(f)") // nunca dejar un tramo sin franjas
            b.copy(contenido = tokens.joinToString(";"))
        }
        cargarDesdePaquete(reconstruirPaqueteConBloques(nuevos))
        actualizarVista()
        return true
    }


    /**
     * Escala todas las anotaciones <w> de los módulos de un bloque por [factor].
     * Si ningún módulo tiene anotación, el bloque se devuelve sin cambios.
     */
    private fun escalarAnchosModulosEnBloque(bloque: BloqueTramo, factor: Float): BloqueTramo {
        val tokens = splitTopLevelSemicolon(bloque.contenido).toMutableList()
        var changed = false
        tokens.forEachIndexed { idx, token ->
            val trimmed = token.trim()
            val openP = trimmed.indexOf('('); if (openP < 0) return@forEachIndexed
            val franjaHead = trimmed.substring(0, openP)
            val modStr = extraerBloqueModulosFranja(trimmed) ?: return@forEachIndexed
            val mods = parsearModsSegmento(modStr)
            if (mods.none { it.medida != null && it.medida > 0f }) return@forEachIndexed
            val newModStr = buildString {
                mods.forEach { mod ->
                    append(if (mod.tipo == 'c') "c" else "f")
                    val w = if (mod.medida != null && mod.medida > 0f) mod.medida * factor else 0f
                    if (w > 0.05f) append("<${df1(w)}>")
                }
            }.ifEmpty { "f" }
            tokens[idx] = "${franjaHead}(${newModStr})"
            changed = true
        }
        return if (changed) bloque.copy(contenido = tokens.joinToString(";")) else bloque
    }

    /**
     * Reescribe los <w> de TODAS las franjas del bloque para que sumen [nuevoAncho]:
     * proporcional a los anchos actuales si los hay, o equitativo si están sin medida.
     * Siempre escribe medidas explícitas (a diferencia de escalarAnchosModulosEnBloque,
     * que salta las franjas sin <w>). Así el tramo que cambia/absorbe queda consistente.
     */
    private fun reescalarFranjasATramo(bloque: BloqueTramo, nuevoAncho: Float): BloqueTramo {
        val tokens = splitTopLevelSemicolon(bloque.contenido).toMutableList()
        tokens.forEachIndexed { idx, token ->
            val t = token.trim()
            val openP = t.indexOf('('); if (openP < 0) return@forEachIndexed
            val head = t.substring(0, openP)
            val interior = extraerBloqueModulosFranja(t) ?: return@forEachIndexed
            val grupos = interior.split(Regex("""(?i)\s*;\s*p\s*;\s*"""))
            val modsPorGrupo = grupos.map { parsearModsSegmento(it) }
            val totalMedidas = modsPorGrupo.flatten().sumOf { (it.medida ?: 0f).toDouble() }.toFloat()
            val nMods = modsPorGrupo.flatten().size.coerceAtLeast(1)
            // Reparto proporcional (o equitativo si no hay medidas). El ancho del tramo es lo
            // autoritativo: NovaCorrediza re-deriva los módulos desde él a precisión completa,
            // así que aquí basta con dejar las proporciones correctas en df1.
            val nuevoInterior = modsPorGrupo.joinToString(";P;") { mods ->
                mods.joinToString("") { m ->
                    val w = if (totalMedidas > 0.05f && m.medida != null) m.medida / totalMedidas * nuevoAncho
                            else nuevoAncho / nMods
                    "${m.tipo}<${df1(w)}>"
                }
            }.ifEmpty { "f<${df1(nuevoAncho)}>" }
            tokens[idx] = "$head($nuevoInterior)"
        }
        return bloque.copy(contenido = tokens.joinToString(";"))
    }

    /** Los tramos cuyo ancho fijó el vidriero: el reparto no los toca. */
    private fun tramosLibresBloqueados(): Set<Int> =
        tramosBlockeados.indices.filter { tramosBlockeados[it] }.toSet()

    /**
     * Aplica una operación sobre el MODELO del diseño y recarga la pantalla con el resultado.
     *
     * Devuelve false si el paquete no se pudo leer como modelo, para que quien llame pueda seguir
     * por el camino viejo de cirugía de texto. Es la red mientras se van convirtiendo las
     * operaciones una por una.
     */
    /**
     * El tramo activo, ya usable como índice. Vale -1 mientras no se haya tocado ninguno, y el
     * modelo con -1 no encuentra el tramo y devuelve el diseño sin tocar: la edición no hacía
     * nada. El camino viejo lo toleraba porque `inicioDelTramoActivo` trata el -1 como 0.
     */
    private fun tramoActivoSeguro(): Int = indiceTramoActivo.coerceAtLeast(0)

    private fun aplicarAlModelo(operacion: (DisenoNova) -> DisenoNova): Boolean {
        // Cualquier fallo del modelo devuelve false y la pantalla sigue por el camino viejo: en
        // plena migración, una edición no puede tumbar la app.
        val modelo = runCatching { DisenoNova.desdePaquete(paqueteActualLectura()) }
            .getOrNull() ?: return false
        val nuevo = runCatching { operacion(modelo) }.getOrNull() ?: return false
        if (nuevo === modelo) return true   // la operación no aplicaba; no se toca nada
        cargarDesdePaquete(nuevo.aPaquete())
        actualizarVista()
        return true
    }


    /** Elegir franja de la lista, dentro del tramo activo. */
    private fun dialogoSeleccionarFranjaActiva() {
        val bloques = parsearBloquesTramo()
        if (bloques.isEmpty()) {
            Toast.makeText(this, "No hay franjas.", Toast.LENGTH_SHORT).show(); return
        }
        val idxTramo = if (indiceTramoActivo in bloques.indices) indiceTramoActivo else 0
        val tokens = splitTopLevelSemicolon(bloques[idxTramo].contenido)
        val franjasTramo = franjasDelBloque(bloques[idxTramo])
        if (franjasTramo.isEmpty()) {
            Toast.makeText(this, "No hay franjas.", Toast.LENGTH_SHORT).show(); return
        }
        val items = franjasTramo.mapIndexed { i, (tipo, alto) ->
            val patron = (extraerBloqueModulosFranja(tokens.getOrElse(i) { "" }) ?: "")
                .filter { it.lowercaseChar() == 'f' || it.lowercaseChar() == 'c' }
            "[$i] ${tipo.uppercaseChar()}  h=${df1(alto)}  ($patron)"
        }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Franja del tramo ${idxTramo + 1}")
            .setSingleChoiceItems(items, indiceFranjaActiva.coerceAtLeast(0)) { d, which ->
                indiceTramoActivo = idxTramo
                indiceFranjaActiva = which
                indiceModuloActivo = -1
                binding.vistaDiseno.resaltarFranja(which, idxTramo)
                d.dismiss()
                actualizarVista()
            }
            .show()
    }

    /**
     * Las franjas del tramo activo, tal como están en el paquete. Es lo que hay que mirar para
     * cualquier acción sobre una franja: la lista global de la pantalla es la del primer tramo y
     * se queda corta en cuanto los tramos tienen franjas distintas.
     */
    private fun franjasDelTramoActivo(): List<Pair<Char, Float>> {
        val bloques = parsearBloquesTramo()
        if (bloques.isEmpty()) return emptyList()
        val idx = if (indiceTramoActivo in bloques.indices) indiceTramoActivo else 0
        return franjasDelBloque(bloques[idx])
    }

    /** ¿Hay una franja elegida, y existe en el tramo activo? */
    private fun hayFranjaElegida(): Boolean =
        indiceFranjaActiva in franjasDelTramoActivo().indices

    private fun dialogoEditarFranjaAlturaSolo() {
        val franjas = franjasDelTramoActivo()
        if (indiceFranjaActiva !in franjas.indices) {
            Toast.makeText(this, "Selecciona una franja primero.", Toast.LENGTH_SHORT).show()
            return
        }
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 8)
        }
        val etAltura = EditText(this).apply {
            hint = "Altura (cm)  0 = auto"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(df1(franjas[indiceFranjaActiva].second))
        }
        cont.addView(etAltura)

        AlertDialog.Builder(this)
            .setTitle("Altura de franja")
            .setView(cont)
            .setPositiveButton("Aplicar") { _, _ ->
                val alt = etAltura.text.toString().aNumeroSeguro().coerceAtLeast(0f)
                if (alt > altoCm) {
                    Toast.makeText(this, "Altura > alto total. Se mantiene la anterior.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                // La misma regla que en el panel: lo que sobra lo absorbe el resto del tramo.
                aplicarAlModelo { it.conAlturaDeFranja(tramoActivoSeguro(), indiceFranjaActiva, alt) }
                actualizarPanelCotas()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // =========================== FLOTANTE DE MÓDULOS ===========================

    /**
     * El mando que sale junto a la franja tocada: poner, quitar y cambiar sus módulos.
     *
     * Va pegado a la franja y no en el panel porque el panel solo llega a la franja de sistema:
     * con varias mochetas por tramo hacía falta un sitio donde el módulo que se toca sea el de
     * esa franja y no el de otra.
     */
    private fun mostrarFlotanteModulos() {
        cerrarFlotanteModulos()
        val mods = modulosDeLaFranjaElegida()
        if (mods.isEmpty()) return
        val tramo = tramoActivoSeguro()
        // Tras editar, el dibujo aún no ha vuelto a medir sus bandas: en ese caso el mando se
        // queda donde estaba en vez de desaparecer.
        val banda = binding.vistaDiseno.bandaDeFranja(tramo, indiceFranjaActiva)
        val ancho = binding.vistaDiseno.anchoDeTramo(tramo)
        if (banda == null && flotantePos == null) return

        val dp = resources.displayMetrics.density
        val dp4 = (4 * dp).toInt()
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundResource(R.drawable.bg_control_panel)
            elevation = 10 * dp
            setPadding(dp4, dp4, dp4, dp4)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        fila.addView(botonPanel("−", dp, mods.size > 1) { quitarModuloDeLaFranja() })
        fila.addView(botonPanel("+", dp, true) { agregarModuloALaFranja('f') })
        mods.forEachIndexed { j, tipo ->
            val esCorrediza = tipo == 'c'
            fila.addView(TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams((32 * dp).toInt(), (32 * dp).toInt())
                    .also { it.marginStart = (3 * dp).toInt() }
                gravity = Gravity.CENTER
                text = if (esCorrediza) "C" else "F"
                textSize = 13f
                setTypeface(null, Typeface.BOLD)
                setTextColor(if (esCorrediza) Color.parseColor("#1565C0") else Color.parseColor("#37474F"))
                setBackgroundResource(R.drawable.bg_opcion_seleccionada)
                tag = "flotante_modulo_$j"
                setOnClickListener { alternarTipoModuloDeLaFranja(j) }
            })
        }
        fila.tag = TAG_FLOTANTE
        binding.contenedorPrincipal.addView(fila)
        flotanteModulos = fila

        // Se coloca después de medir: centrado en la franja y pegado al tramo, sin salirse.
        fila.post {
            val pos = if (banda != null && ancho != null) {
                val xLienzo = binding.vistaDiseno.x
                val yLienzo = binding.vistaDiseno.y
                val x = (xLienzo + ancho.first).coerceAtMost(
                    binding.contenedorPrincipal.width - fila.width.toFloat() - dp4
                ).coerceAtLeast(dp4.toFloat())
                val y = (yLienzo + (banda.first + banda.second) / 2f - fila.height / 2f)
                    .coerceIn(dp4.toFloat(), (binding.contenedorPrincipal.height - fila.height - dp4).toFloat())
                x to y
            } else flotantePos!!
            flotantePos = pos
            fila.x = pos.first
            fila.y = pos.second
        }
    }

    private fun quitarFlotanteModulos() {
        flotanteModulos?.let { binding.contenedorPrincipal.removeView(it) }
        flotanteModulos = null
    }

    /** Cierra el mando y olvida dónde estaba. */
    private fun cerrarFlotanteModulos() {
        quitarFlotanteModulos()
        flotantePos = null
    }

    /** Vuelve a montarlo tras cada cambio, para que las letras y el − sigan al día. */
    private fun refrescarFlotanteModulos() {
        if (flotanteModulos != null) binding.vistaDiseno.post { mostrarFlotanteModulos() }
    }

    private fun agregarModuloALaFranja(tipo: Char) {
        val mods = modulosDeLaFranjaElegida()
        val bloqueados = tramosLibresBloqueados()
        aplicarAlModelo {
            it.conModuloAgregado(tramoActivoSeguro(), indiceFranjaActiva, mods.lastIndex, tipo, bloqueados)
        }
        actualizarPanelCotas()
        refrescarFlotanteModulos()
    }

    private fun quitarModuloDeLaFranja() {
        val mods = modulosDeLaFranjaElegida()
        if (mods.size <= 1) {
            Toast.makeText(this, "Debe quedar al menos un módulo.", Toast.LENGTH_SHORT).show()
            return
        }
        val bloqueados = tramosLibresBloqueados()
        aplicarAlModelo {
            it.conModuloQuitado(tramoActivoSeguro(), indiceFranjaActiva, mods.lastIndex, bloqueados)
        }
        actualizarPanelCotas()
        refrescarFlotanteModulos()
    }

    private fun alternarTipoModuloDeLaFranja(indiceModulo: Int) {
        val bloqueados = tramosLibresBloqueados()
        aplicarAlModelo {
            it.conTipoCambiado(tramoActivoSeguro(), indiceFranjaActiva, indiceModulo, bloqueados)
        }
        actualizarPanelCotas()
        refrescarFlotanteModulos()
    }

    /** Los módulos de la franja elegida del tramo activo, como letras. */
    private fun modulosDeLaFranjaElegida(): List<Char> {
        val bloques = parsearBloquesTramo()
        if (bloques.isEmpty()) return emptyList()
        val idxTramo = if (indiceTramoActivo in bloques.indices) indiceTramoActivo else 0
        val tokens = splitTopLevelSemicolon(bloques[idxTramo].contenido)
        val token = tokens.getOrNull(indiceFranjaActiva) ?: return emptyList()
        val mods = extraerBloqueModulosFranja(token) ?: return emptyList()
        return Regex("""[fcFC]""").findAll(mods).map { it.value.first().lowercaseChar() }.toList()
    }

    private fun dialogoSeleccionarModulo() {
        val mods = modulosDeLaFranjaElegida()
        if (mods.isEmpty()) {
            Toast.makeText(this, "Selecciona una franja primero.", Toast.LENGTH_SHORT).show()
            return
        }
        val items = mods.mapIndexed { idx, m ->
            "[$idx] " + if (m == 'c') "c (corrediza)" else "f (fijo)"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Módulo en franja [$indiceFranjaActiva]")
            .setItems(items) { _, which ->
                dialogoEditarModulo(which)
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    /**
     * Alternar, insertar o eliminar el módulo de la franja elegida.
     *
     * El índice es el del módulo DENTRO de su franja y su tramo, que es lo que entiende el
     * modelo: antes se restaba el arranque del tramo a un índice que ya era relativo, y con
     * franjas distintas por tramo eso caía fuera.
     */
    private fun dialogoEditarModulo(indiceModulo: Int) {
        val mods = modulosDeLaFranjaElegida()
        if (indiceModulo !in mods.indices) return

        val opciones = arrayOf(
            "Alternar f ↔ c",
            "Insertar f antes",
            "Insertar c antes",
            "Insertar f después",
            "Insertar c después",
            "Eliminar módulo"
        )
        AlertDialog.Builder(this)
            .setTitle("Editar módulo [$indiceModulo] en franja [$indiceFranjaActiva]")
            .setItems(opciones) { _, which ->
                val bloqueados = tramosLibresBloqueados()
                if (which == 5 && mods.size <= 1) {
                    Toast.makeText(this, "Debe quedar al menos un módulo.", Toast.LENGTH_SHORT).show()
                    return@setItems
                }
                val t = tramoActivoSeguro()
                aplicarAlModelo { d ->
                    when (which) {
                        0 -> d.conTipoCambiado(t, indiceFranjaActiva, indiceModulo, bloqueados)
                        1 -> d.conModuloAgregado(t, indiceFranjaActiva, indiceModulo - 1, 'f', bloqueados)
                        2 -> d.conModuloAgregado(t, indiceFranjaActiva, indiceModulo - 1, 'c', bloqueados)
                        3 -> d.conModuloAgregado(t, indiceFranjaActiva, indiceModulo, 'f', bloqueados)
                        4 -> d.conModuloAgregado(t, indiceFranjaActiva, indiceModulo, 'c', bloqueados)
                        5 -> d.conModuloQuitado(t, indiceFranjaActiva, indiceModulo, bloqueados)
                        else -> d
                    }
                }
                actualizarPanelCotas()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // =========================== EDITAR ANCHO DE MÓDULO ===========================

    /**
     * El ancho del módulo seleccionado.
     *
     * Todo sale del TRAMO y de la franja que se tocó, por su índice: la lista global de franjas
     * de la pantalla es la del primer tramo, y con franjas distintas por tramo se quedaba corta
     * —las de arriba ni abrían el diálogo—. Y buscar la franja por su letra devolvía siempre la
     * primera `m`, así que en una bandera las dos mochetas editaban la misma.
     */
    private fun dialogoEditarAnchoModulo(): Boolean {
        val bloques = parsearBloquesTramo()
        if (bloques.isEmpty()) return false
        val tramoIdx = if (indiceTramoActivo in bloques.indices) indiceTramoActivo else 0
        val bloque = bloques[tramoIdx]

        val franjaTokens = splitTopLevelSemicolon(bloque.contenido).toMutableList()
        val franjaIdxInBloque = indiceFranjaActiva
        if (franjaIdxInBloque !in franjaTokens.indices) return false
        val franjaToken = franjaTokens[franjaIdxInBloque].trim()
        val esSistema = franjaToken.startsWith("s", ignoreCase = true)
        val openP = franjaToken.indexOf('(')
        if (openP < 0) return false
        val franjaHead = franjaToken.substring(0, openP)
        val modStr = extraerBloqueModulosFranja(franjaToken) ?: return false
        val mods = parsearModsSegmento(modStr)
        if (indiceModuloActivo !in mods.indices) return false

        val tramoAncho = bloque.ancho
        val modActual = mods[indiceModuloActivo]
        val anchoActual = modActual.medida ?: (tramoAncho / mods.size.coerceAtLeast(1))

        // Altura del vidrio (informativo): la de ESA franja, no la del primer tramo.
        val franjaAltura = franjasDelBloque(bloque).getOrNull(franjaIdxInBloque)?.second
            ?.takeIf { it > 0f } ?: altoCm
        val altoVidrio = if (esSistema) (franjaAltura - usCm - 0.2f).coerceAtLeast(0f)
                         else franjaAltura

        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 8)
        }
        val etAncho = EditText(this).apply {
            hint = "Ancho del módulo (cm)"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(df1(anchoActual))
        }
        cont.addView(etAncho)
        cont.addView(TextView(this).apply {
            text = "Alto vidrio: ${df1(altoVidrio)} cm · Tramo: ${df1(tramoAncho)} cm"
            textSize = 11f
            setPadding(0, 8, 0, 0)
        })

        val nMod = indiceModuloActivo
        AlertDialog.Builder(this)
            .setTitle("Módulo ${nMod + 1}/${mods.size} · ${if (modActual.tipo == 'c') "Corrediza" else "Fijo"}")
            .setView(cont)
            .setPositiveButton("Aplicar") { _, _ ->
                val nuevoAncho = etAncho.text.toString().aNumeroSeguro()
                if (nuevoAncho <= 0f || nuevoAncho >= tramoAncho) {
                    Toast.makeText(
                        this,
                        "El ancho debe ser mayor que 0 y menor que el tramo (${df1(tramoAncho)} cm).",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }
                // Módulo editado recibe nuevoAncho; el resto se reparte equitativamente
                val nMods = mods.size
                val anchoRestante = (tramoAncho - nuevoAncho).coerceAtLeast(0f)
                val anchoOtros = if (nMods > 1) anchoRestante / (nMods - 1) else 0f

                val newModStr = buildString {
                    mods.forEachIndexed { idx, mod ->
                        append(if (mod.tipo == 'c') "c" else "f")
                        val w = if (idx == nMod) nuevoAncho else anchoOtros
                        if (w > 0.05f) append("<${df1(w)}>")
                    }
                }.ifEmpty { "f" }

                franjaTokens[franjaIdxInBloque] = "${franjaHead}(${newModStr})"
                val newBloques = bloques.toMutableList()
                newBloques[tramoIdx] = BloqueTramo(bloque.letra, bloque.ancho, franjaTokens.joinToString(";"))
                indiceModuloActivo = -1
                binding.vistaDiseno.resaltarModulo(indiceFranjaActiva, -1)
                cargarDesdePaquete(reconstruirPaqueteConBloques(newBloques))
                actualizarVista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
        return true
    }

    // =========================== MEDIDAS / ENSAMBLE ===========================

    /** Cambia la franja elegida de sistema a mocheta y al revés, solo en su tramo. */
    private fun alternarTipoFranjaActiva() {
        val franjas = franjasDelTramoActivo()
        if (indiceFranjaActiva !in franjas.indices) {
            Toast.makeText(this, "Selecciona una franja primero.", Toast.LENGTH_SHORT).show()
            return
        }
        val eraSistema = franjas[indiceFranjaActiva].first == 's'
        aplicarAlModelo { it.conTipoDeFranjaCambiado(tramoActivoSeguro(), indiceFranjaActiva) }
        actualizarPanelCotas()
        Toast.makeText(
            this,
            if (eraSistema) "Franja cambiada a Mocheta (M)" else "Franja cambiada a Sistema (S)",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun alternarEnsamble() {
        // Solo cambia el tipo en el encabezado {nova,<tipo>,[...]}, parcheando la cadena
        // para conservar tramos y anchos. No usar aPaquete() (colapsaría a un solo Tl).
        val nuevoTipo = if (tipo == TipoEnsamble.APA) "ina" else "apa"
        val actual = paqueteActualLectura()
        val c1 = actual.indexOf(',')
        val c2 = actual.indexOf(',', c1 + 1)
        if (c1 in 1 until c2) {
            val nuevo = actual.substring(0, c1 + 1) + nuevoTipo + actual.substring(c2)
            cargarDesdePaquete(nuevo)
        } else {
            tipo = if (tipo == TipoEnsamble.APA) TipoEnsamble.INA else TipoEnsamble.APA
        }
        actualizarVista()
    }

    private fun dialogoCambiarMedidas() {
        val base = paqueteActualLectura()
        val anchosActuales = obtenerAnchosTramo(base)
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 8)
        }
        val scroll = ScrollView(this).apply {
            addView(cont)
        }
        val etAncho = EditText(this).apply {
            hint = "Ancho (cm)"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(df1(anchoCm))
        }
        val etAlto = EditText(this).apply {
            hint = "Alto (cm)"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(df1(altoCm))
        }
        val etMoch = EditText(this).apply {
            hint = "Mocheta lateral (cm)"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(df1(mochetaLateralCm))
        }
        cont.addView(etAncho); cont.addView(etAlto); cont.addView(etMoch)

        val tituloTramos = TextView(this).apply {
            text = "Medidas de tramos (cm)"
            setPadding(0, 16, 0, 8)
        }
        cont.addView(tituloTramos)

        val etTramos = anchosActuales.mapIndexed { i, ancho ->
            EditText(this).apply {
                hint = "Tramo ${i + 1} (cm)"
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                setText(df1(ancho))
            }.also { cont.addView(it) }
        }

        AlertDialog.Builder(this)
            .setTitle("Cambiar medidas")
            .setView(scroll)
            .setPositiveButton("Aplicar") { _, _ ->
                anchoCm = etAncho.text.toString().aNumeroSeguro().takeIf { it > 0 } ?: anchoCm
                altoCm = etAlto.text.toString().aNumeroSeguro().takeIf { it > 0 } ?: altoCm
                mochetaLateralCm = etMoch.text.toString().aNumeroSeguro().coerceAtLeast(0f)
                val medidasTramo = etTramos.map { it.text.toString().aNumeroSeguro() }
                val validas = medidasTramo.size == anchosActuales.size && medidasTramo.all { it > 0f }
                if (validas && medidasTramo.size >= 2) {
                    val medidas = medidasTramo.toMutableList()
                    val suma = medidas.sum()
                    val dif = kotlin.math.abs(suma - anchoCm)
                    if (dif > 0.2f) {
                        // Escalar todos los tramos proporcionalmente al nuevo ancho total
                        val factor = anchoCm / suma
                        for (i in medidas.indices) medidas[i] = medidas[i] * factor
                        Toast.makeText(this, "Tramos ajustados proporcionalmente al nuevo ancho.", Toast.LENGTH_SHORT).show()
                    }
                    corteVerticalCm = medidas[0].takeIf { it > 0f && it < anchoCm }
                }
                // Rehacer el diseño con la medida nueva. Sin esto los tramos se quedaban con el
                // ancho viejo y `reconstruirPaqueteConBloques` devolvía `anchoCm` a la suma de
                // esos tramos: al tocar cualquier otro botón, el ancho volvía al de antes.
                val rehecho = runCatching {
                    DisenoNova.desdePaquete(paqueteActualLectura())
                        ?.conAnchosRepartidos(anchoParanteCm)
                        ?.aPaquete()
                }.getOrNull()
                if (rehecho != null) cargarDesdePaquete(rehecho)
                actualizarVista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun contarTramosDesdePaqueteActual(): Int {
        val base = if (paqueteOriginal.isNotBlank()) paqueteOriginal else paqueteBase()
        val limpio = base.replace(" ", "")
        val idxColon = limpio.indexOf(':')
        val idxClose = limpio.lastIndexOf(']')
        if (idxColon < 0 || idxClose <= idxColon) return 1
        val cuerpo = limpio.substring(idxColon + 1, idxClose)
        val franjasTop = splitTopLevelSemicolon(cuerpo)
        val primeraFranja = franjasTop.firstOrNull() ?: return 1
        val modulos = extraerBloqueModulosFranja(primeraFranja) ?: return 1
        val segmentos = modulos.split(Regex("""(?i)\s*;\s*p\s*;\s*""")).filter { it.isNotBlank() }
        return segmentos.size.coerceAtLeast(1)
    }

    private fun paqueteActualLectura(): String {
        return if (paqueteOriginal.isNotBlank()) {
            paqueteConDimensionesActualizadas(paqueteOriginal)
        } else {
            paqueteBase()
        }
    }

    private fun obtenerAnchosTramo(base: String): List<Float> {
        val nDetectado = contarTramosDesdePaqueteActual().coerceAtLeast(1)
        val corte = corteVerticalCm
        if (nDetectado == 2 && corte != null && corte > 0f && corte < anchoCm) {
            return listOf(corte, (anchoCm - corte).coerceAtLeast(0f))
        }

        val limpio = base.replace(" ", "")
        val idxColon = limpio.indexOf(':')
        val idxClose = limpio.lastIndexOf(']')
        if (idxColon < 0 || idxClose <= idxColon) return listOf(anchoCm)
        val cuerpo = limpio.substring(idxColon + 1, idxClose)
        val franjasTop = splitTopLevelSemicolon(cuerpo)
        val franjaSistema = franjasTop.firstOrNull { it.trim().startsWith("s", true) } ?: franjasTop.firstOrNull()
        val modulos = franjaSistema?.let { extraerBloqueModulosFranja(it) }
        if (modulos != null) {
            val segmentos = modulos.split(Regex("""(?i)\s*;\s*p\s*;\s*""")).filter { it.isNotBlank() }
            val sumas = segmentos.map { seg -> sumarMedidasModulos(seg) }
            if (sumas.isNotEmpty() && sumas.all { it > 0f }) return sumas
        }
        val n = nDetectado
        val anchoUtil = (anchoCm - (n - 1) * anchoParanteCm).coerceAtLeast(anchoCm / n)
        val cuota = anchoUtil / n
        return List(n) { cuota }
    }

    private fun sumarMedidasModulos(segmento: String): Float {
        var suma = 0f
        val re = Regex("""([fcFC])\s*(?:<\s*(-?\d+(?:[.,]\d+)?)\s*>|\(\s*(-?\d+(?:[.,]\d+)?)\s*\))?""")
        re.findAll(segmento).forEach { m ->
            val medida = (m.groupValues[2].ifBlank { m.groupValues[3] }).aNumeroSeguro()
            if (medida > 0f) suma += medida
        }
        return suma
    }

    private data class ConteoMod(val tipo: Char, val medida: Float?)

    private fun parsearModsSegmento(segmento: String): List<ConteoMod> {
        val out = mutableListOf<ConteoMod>()
        val re = Regex("""([fcFC])\s*(?:<\s*(-?\d+(?:[.,]\d+)?)\s*>|\(\s*(-?\d+(?:[.,]\d+)?)\s*\))?""")
        re.findAll(segmento).forEach { m ->
            val tipo = m.groupValues[1].lowercase().firstOrNull() ?: 'f'
            val medidaRaw = m.groupValues[2].ifBlank { m.groupValues[3] }
            val medida = medidaRaw.aNumeroSeguro().takeIf { it > 0f }
            out.add(ConteoMod(tipo, medida))
        }
        return out
    }




    // ---- Datos de tramo extraídos del paquete ----
    private data class InfoTramo(val ancho: Float, val sistemaAltura: Float, val mochetaAltura: Float)

    private fun extraerInfoTramos(): List<InfoTramo> {
        val paquete = paqueteActualLectura()
        val t = paquete.replace(" ", "")
        val idxColon = t.indexOf(':')
        val idxClose = t.lastIndexOf(']')
        if (idxColon < 0 || idxClose <= idxColon) return emptyList()
        val cuerpo = t.substring(idxColon + 1, idxClose)
        val reT = Regex("""(?i)t[a-z]?<\s*(-?\d+(?:[.,]\d+)?)\s*>""")
        val reSis = Regex("""(?i)s<\s*(-?\d+(?:[.,]\d+)?)\s*>""")
        val reMoch = Regex("""(?i)m<\s*(-?\d+(?:[.,]\d+)?)\s*>""")
        val result = mutableListOf<InfoTramo>()
        var searchFrom = 0
        while (searchFrom < cuerpo.length) {
            val tMatch = reT.find(cuerpo, searchFrom) ?: break
            val ancho = tMatch.groupValues[1].aNumeroSeguro()
            val openParen = cuerpo.indexOf('(', tMatch.range.last + 1)
            if (openParen < 0) break
            var depth = 0; var closeParen = openParen
            for (j in openParen until cuerpo.length) {
                when (cuerpo[j]) {
                    '(' -> depth++
                    ')' -> { depth--; if (depth == 0) { closeParen = j; break } }
                }
            }
            val contenido = cuerpo.substring(openParen + 1, closeParen)
            val sistemaAltura = reSis.find(contenido)?.groupValues?.get(1)?.aNumeroSeguro() ?: 0f
            val mochetaAltura = reMoch.find(contenido)?.groupValues?.get(1)?.aNumeroSeguro() ?: 0f
            if (ancho > 0f) result.add(InfoTramo(ancho, sistemaAltura, mochetaAltura))
            searchFrom = closeParen + 1
        }
        return result
    }

    private fun obtenerAnchoModuloDelPaquete(tramoIdx: Int, esSistema: Boolean, moduloIdx: Int): ConteoMod? {
        val t = paqueteActualLectura().replace(" ", "")
        val idxColon = t.indexOf(':')
        val idxClose = t.lastIndexOf(']')
        if (idxColon < 0 || idxClose <= idxColon) return null
        val cuerpo = t.substring(idxColon + 1, idxClose)
        val reT = Regex("""(?i)t[a-z]?<\s*-?\d+(?:[.,]\d+)?\s*>""")
        var searchFrom = 0; var tramoCount = 0; var contenidoTramo: String? = null
        while (searchFrom < cuerpo.length) {
            val tMatch = reT.find(cuerpo, searchFrom) ?: break
            val openParen = cuerpo.indexOf('(', tMatch.range.last + 1)
            if (openParen < 0) break
            var depth = 0; var closeParen = openParen
            for (j in openParen until cuerpo.length) {
                when (cuerpo[j]) {
                    '(' -> depth++
                    ')' -> { depth--; if (depth == 0) { closeParen = j; break } }
                }
            }
            if (tramoCount == tramoIdx) { contenidoTramo = cuerpo.substring(openParen + 1, closeParen); break }
            tramoCount++; searchFrom = closeParen + 1
        }
        val contenido = contenidoTramo ?: return null
        val prefix = if (esSistema) "s" else "m"
        val franjaToken = splitTopLevelSemicolon(contenido)
            .firstOrNull { it.trimStart().startsWith(prefix, ignoreCase = true) } ?: return null
        val bloque = extraerBloqueModulosFranja(franjaToken) ?: return null
        return parsearModsSegmento(bloque).getOrNull(moduloIdx)
    }

    private fun actualizarInfoSeleccion() {
        val tramoInfo = extraerInfoTramos()
        val ssb = SpannableStringBuilder()

        // Línea ventana (siempre, sin negrita)
        ssb.append("ventana = ${df1(anchoCm)} × ${df1(altoCm)}")

        // tramoSel: índice del tramo seleccionado (no depende de indiceFranjaActiva)
        val tramoSel = if (indiceTramoActivo in tramoInfo.indices) indiceTramoActivo else -1

        if (tramoInfo.isNotEmpty()) {
            // Líneas de tramos — negrita el seleccionado
            for (i in tramoInfo.indices) {
                ssb.append("\n")
                val start = ssb.length
                ssb.append("tramo${i + 1} = ${df1(tramoInfo[i].ancho)} × ${df1(altoCm)}")
                if (i == tramoSel) {
                    ssb.setSpan(StyleSpan(Typeface.BOLD), start, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }

        // Línea de franja — siempre que haya una franja seleccionada, sin importar si hay tramo
        if (indiceFranjaActiva in franjas.indices) {
            val fr = franjas[indiceFranjaActiva]
            val esSis = fr.esSistema
            val franjaLabel = if (esSis) "sistema" else "mocheta"
            val franjaAncho = if (tramoSel >= 0) tramoInfo[tramoSel].ancho else anchoCm
            val franjaAltura = when {
                tramoSel >= 0 && esSis  -> tramoInfo[tramoSel].sistemaAltura
                tramoSel >= 0 && !esSis -> tramoInfo[tramoSel].mochetaAltura
                fr.alturaCm > 0f        -> fr.alturaCm
                else                    -> altoCm
            }
            ssb.append("\n")
            val franjaStart = ssb.length
            ssb.append("$franjaLabel ${df1(franjaAncho)} × ${df1(franjaAltura)}")
            ssb.setSpan(StyleSpan(Typeface.BOLD), franjaStart, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Línea de módulo — misma tarjeta, una línea más
            if (indiceModuloActivo >= 0) {
                runCatching {
                    val tramoIdx = tramoSel.coerceAtLeast(0)
                    val mod = obtenerAnchoModuloDelPaquete(tramoIdx, esSis, indiceModuloActivo)
                    if (mod != null) {
                        // Si no hay anotación <w> (tras add/remove), calcular desde tramoAncho / nMódulos
                        val anchoMod = mod.medida ?: run {
                            val prefix = if (esSis) "s" else "m"
                            val blq = parsearBloquesTramo().getOrNull(tramoIdx)
                            val ftok = blq?.let { splitTopLevelSemicolon(it.contenido)
                                .firstOrNull { tk -> tk.trim().startsWith(prefix, ignoreCase = true) } }
                            val n = ftok?.let { extraerBloqueModulosFranja(it) }
                                ?.let { parsearModsSegmento(it).size }?.coerceAtLeast(1) ?: 1
                            franjaAncho / n
                        }
                        val altoVidrio = if (esSis) {
                            (franjaAltura - usCm - 0.2f).coerceAtLeast(0f)
                        } else {
                            franjaAltura
                        }
                        ssb.append("\n")
                        val modStart = ssb.length
                        ssb.append("vidrio ${df1(anchoMod)} × ${df1(altoVidrio)}")
                        ssb.setSpan(StyleSpan(Typeface.BOLD), modStart, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
        }

        binding.tvInfoSeleccion.text = ssb
    }

    private fun splitTopLevelSemicolon(texto: String): List<String> {
        val out = mutableListOf<String>()
        val sb = StringBuilder()
        var depthParen = 0
        for (ch in texto) {
            when (ch) {
                '(' -> {
                    depthParen++
                    sb.append(ch)
                }
                ')' -> {
                    depthParen = (depthParen - 1).coerceAtLeast(0)
                    sb.append(ch)
                }
                ';' -> {
                    if (depthParen == 0) {
                        out.add(sb.toString())
                        sb.clear()
                    } else {
                        sb.append(ch)
                    }
                }
                else -> sb.append(ch)
            }
        }
        if (sb.isNotEmpty()) out.add(sb.toString())
        return out.filter { it.isNotBlank() }
    }

    private fun extraerBloqueModulosFranja(franjaToken: String): String? {
        val t = franjaToken.trim()
        if (t.isEmpty()) return null
        val grupos = mutableListOf<String>()
        var start = -1
        var depth = 0
        for (i in t.indices) {
            val ch = t[i]
            if (ch == '(') {
                if (depth == 0) start = i + 1
                depth++
            } else if (ch == ')') {
                depth--
                if (depth == 0 && start in 0..i) {
                    grupos.add(t.substring(start, i))
                    start = -1
                }
            }
        }
        if (grupos.isEmpty()) return null
        if (grupos.size >= 2 && grupos[0].matches(Regex("""^-?\d+(?:[.,]\d+)?$"""))) {
            return grupos[1]
        }
        return grupos[0]
    }


    // =========================== PARSEO (entrada tolerante) ===========================

    private fun extraerDimensionesDelPaquete(paquete: String): Pair<Float, Float>? {
        val t = paquete.replace(" ", "")
        val idxBracketOpen = t.indexOf('[')
        val idxColon = t.indexOf(':', idxBracketOpen + 1)
        if (idxBracketOpen < 0 || idxColon < 0) return null
        val dimsTxt = t.substring(idxBracketOpen + 1, idxColon)
        val partes = dimsTxt.split(',')
        if (partes.size < 2) return null
        val w = partes[0].aNumeroSeguro()
        val h = partes[1].aNumeroSeguro()
        if (w <= 0f || h <= 0f) return null
        return Pair(w, h)
    }

    private fun cargarDesdePaquete(p: String?) {
        if (p.isNullOrBlank()) return
        paqueteOriginal = p
        estructuraEditada = false
        extraerDimensionesDelPaquete(p)?.let { (w, h) ->
            anchoCm = w
            altoCm = h
        }
        try {
            val t = p.replace(" ", "")
            if (!t.startsWith("{") || !t.endsWith("}")) return
            val c1 = t.indexOf(',')
            val c2 = t.indexOf(',', c1 + 1)
            if (c1 <= 1 || c2 <= c1 + 1) return

            clase = t.substring(1, c1)
            tipo = if (t.substring(c1 + 1, c2).equals("ina", true)) TipoEnsamble.INA else TipoEnsamble.APA

            val idxBracketOpen = t.indexOf('[', c2 + 1)
            val idxColon = t.indexOf(':', idxBracketOpen + 1)
            val idxBracketClose = t.lastIndexOf(']')
            if (idxBracketOpen < 0 || idxColon < 0 || idxBracketClose < idxColon) return

            val dimsTxt = t.substring(idxBracketOpen + 1, idxColon)
            val dimsPartes = dimsTxt.split(',')
            if (dimsPartes.size >= 2) {
                anchoCm = dimsPartes[0].aNumeroSeguro()
                altoCm = dimsPartes[1].aNumeroSeguro()
            }

            franjas.clear()
            val cuerpoRaw = t.substring(idxColon + 1, idxBracketClose)
            // Nuevo formato T<>: fusiona todos los bloques T<>() con ;P; entre tramos
            val cuerpo = if (cuerpoRaw.trimStart().lowercase().startsWith("t")) {
                val s = cuerpoRaw.replace(" ", "")
                val fusionado = fusionarBloquesTEnCuerpo(s)
                if (fusionado != null) {
                    fusionado
                } else {
                    // Bloque T único: extraer contenido interior
                    val openParen = s.indexOf('(')
                    if (openParen >= 0) {
                        var depth = 0
                        var result = s
                        for (j in openParen until s.length) {
                            when (s[j]) {
                                '(' -> depth++
                                ')' -> { depth--; if (depth == 0) { result = s.substring(openParen + 1, j); break } }
                            }
                        }
                        result
                    } else s
                }
            } else cuerpoRaw
            if (cuerpo.isNotEmpty()) {
                val reAltura = Regex("""^[ms]\s*<\s*(-?\d+(?:[.,]\d+)?)\s*>""", RegexOption.IGNORE_CASE)
                splitTopLevelSemicolon(cuerpo).forEach { token ->
                    val trimmed = token.trim()
                    if (trimmed.isEmpty()) return@forEach
                    val esS = trimmed.startsWith("s", ignoreCase = true)
                    val alt = reAltura.find(trimmed)?.groupValues?.get(1)?.aNumeroSeguro() ?: 0f
                    val bloque = extraerBloqueModulosFranja(trimmed) ?: return@forEach
                    val tramos = bloque.split(Regex("""(?i)\s*;\s*p\s*;\s*"""))
                    val mods = mutableListOf<TipoModulo>()
                    val parantes = mutableListOf<Int>()
                    tramos.forEachIndexed { idx, tramo ->
                        parsearModsSegmento(tramo).forEach { m ->
                            mods.add(if (m.tipo == 'c') TipoModulo.CORREDIZA else TipoModulo.FIJO)
                        }
                        if (idx < tramos.lastIndex) parantes.add(mods.size)
                    }
                    if (mods.isEmpty()) mods.add(TipoModulo.FIJO)
                    franjas.add(Franja(esSistema = esS, alturaCm = alt, modulos = mods, parantes = parantes))
                }
            }
            if (franjas.isEmpty()) {
                franjas.add(
                    Franja(
                        esSistema = true,
                        alturaCm = 0f,
                        modulos = mutableListOf(TipoModulo.FIJO)
                    )
                )
            }
            // Mantener la franja activa si sigue siendo válida tras el reload. Se mide contra las
            // franjas del TRAMO activo: `franjas` es la lista del primer tramo y, si ese tiene
            // menos, la selección de los demás se perdía en cada recarga —el mando flotante
            // desaparecía al primer cambio.
            val nFranjasDelTramo = franjasDelTramoActivo().size.takeIf { it > 0 } ?: franjas.size
            if (indiceFranjaActiva !in 0 until nFranjasDelTramo) {
                indiceFranjaActiva = nFranjasDelTramo - 1
            }
        } catch (_: Exception) {
            // si falla el parseo, se mantiene el estado anterior
            if (franjas.isEmpty()) {
                franjas.add(
                    Franja(
                        esSistema = true,
                        alturaCm = 0f,
                        modulos = mutableListOf(TipoModulo.FIJO)
                    )
                )
                indiceFranjaActiva = franjas.lastIndex
            }
        }
    }

    // =========================== VISTA / REDIBUJO ===========================

    /** Paquete mínimo válido (un tramo, un fijo) usado como base/fallback en cadena. */
    /**
     * El diseño de arranque cuando no viene ninguno de la calculadora.
     *
     * Antes era un solo fijo en un solo tramo (`Tl<ancho>(s(f))`) y había que armar la ventana
     * entera a mano. Ahora sale ya repartido con las reglas de siempre: divisiones por la regla
     * de los 60, tramos de hasta 5 módulos y el patrón clásico de fijos y corredizas.
     *
     * Sin mocheta: la pantalla no lleva altura de puente, así que la franja del sistema ocupa
     * todo el alto y la mocheta se agrega con su botón si hace falta.
     */
    private fun paqueteBase(): String {
        val tipoTxt = if (tipo == TipoEnsamble.APA) "apa" else "ina"
        return runCatching {
            DisenoNova.nuevo(tipoTxt, anchoCm, altoCm, altoHoja = altoCm).aPaquete()
        }.getOrElse {
            "{nova,${tipoTxt},[${df1(anchoCm)},${df1(altoCm)}:Tl<${df1(anchoCm)}>(s(f))]}"
        }
    }

    private fun paqueteConDimensionesActualizadas(base: String): String {
        val t = base.replace(" ", "")
        if (!t.startsWith("{") || !t.endsWith("}")) return paqueteBase()
        val c1 = t.indexOf(',')
        if (c1 <= 1) return paqueteBase()
        val c2 = t.indexOf(',', c1 + 1)
        if (c2 <= c1 + 1) return paqueteBase()
        val idxBracketOpen = t.indexOf('[', c2 + 1)
        val idxColon = t.indexOf(':', idxBracketOpen + 1)
        val idxBracketClose = t.lastIndexOf(']')
        if (idxBracketOpen < 0 || idxColon < 0 || idxBracketClose < idxColon) return paqueteBase()

        val claseBase = t.substring(1, c1)
        val tipoBase = t.substring(c1 + 1, c2)
        val cuerpo = t.substring(idxColon + 1, idxBracketClose)
        return "{${claseBase},${tipoBase},[${df1(anchoCm)},${df1(altoCm)}:${cuerpo}]}"
    }

    private fun actualizarVista() {
        val tipoTxt = if (tipo == TipoEnsamble.APA) "apa" else "ina"
        val paqueteSeguro = "{nova,${tipoTxt},[${df1(anchoCm)},${df1(altoCm)}:Tl<${df1(anchoCm)}>(s(f))]}"
        val paquete = if (paqueteOriginal.isNotBlank()) {
            paqueteConDimensionesActualizadas(paqueteOriginal)
        } else {
            paqueteSeguro
        }
        val aplicado = runCatching {
            binding.vistaDiseno.actualizarDesdePaquete(paquete, 0f, 0f, mochetaLateralCm)
        }.isSuccess
        if (!aplicado) {
            // Render de emergencia SOLO para la vista; NUNCA se altera paqueteOriginal (la fuente
            // de verdad). Antes esto colapsaba el diseño a un solo Tl vía aPaquete().
            runCatching { binding.vistaDiseno.actualizarDesdePaquete(paqueteSeguro, 0f, 0f, mochetaLateralCm) }
        }

        binding.vistaDiseno.actualizarCorteVertical(corteVerticalCm)
        // resalta si hay franja activa conocida
        binding.vistaDiseno.resaltarFranja(indiceFranjaActiva, indiceTramoActivo)
        actualizarInfoSeleccion()
        binding.vistaDiseno.invalidate()
    }

    // ==================== PANEL COTAS / TRAMOS ====================

    private data class BloqueTramo(val letra: String, val ancho: Float, val contenido: String)

    private fun parsearBloquesTramo(): List<BloqueTramo> {
        val paquete = paqueteActualLectura()
        val t = paquete.replace(" ", "")
        val idxColon = t.indexOf(':')
        val idxClose = t.lastIndexOf(']')
        if (idxColon < 0 || idxClose <= idxColon) return emptyList()
        val cuerpo = t.substring(idxColon + 1, idxClose)
        val result = mutableListOf<BloqueTramo>()
        var i = 0
        while (i < cuerpo.length) {
            if (cuerpo[i].lowercaseChar() == 't') {
                i++
                val letra = if (i < cuerpo.length && cuerpo[i].lowercaseChar() in 'a'..'z' && cuerpo[i] != '<') {
                    val l = cuerpo[i].toString(); i++; l
                } else ""
                if (i >= cuerpo.length || cuerpo[i] != '<') continue
                val ltIdx = i
                val gtIdx = cuerpo.indexOf('>', ltIdx + 1)
                if (gtIdx < 0) break
                val ancho = cuerpo.substring(ltIdx + 1, gtIdx).replace(',', '.').toFloatOrNull() ?: 0f
                i = gtIdx + 1
                if (i >= cuerpo.length || cuerpo[i] != '(') continue
                val openParen = i; var depth = 0; var closeParen = openParen
                for (j in openParen until cuerpo.length) {
                    when (cuerpo[j]) {
                        '(' -> depth++
                        ')' -> { depth--; if (depth == 0) { closeParen = j; break } }
                    }
                }
                result.add(BloqueTramo("T$letra", ancho, cuerpo.substring(openParen + 1, closeParen)))
                i = closeParen + 1
            } else {
                i++
            }
        }
        return result
    }

    private fun reconstruirPaqueteConBloques(bloques: List<BloqueTramo>): String {
        val tipoTxt = if (tipo == TipoEnsamble.APA) "apa" else "ina"
        val nParantes = (bloques.size - 1).coerceAtLeast(0)
        val totalAncho = bloques.sumOf { it.ancho.toDouble() }.toFloat() + nParantes * anchoParanteCm
        anchoCm = totalAncho
        val sb = StringBuilder()
        sb.append("{${clase},${tipoTxt},[${df1(totalAncho)},${df1(altoCm)}:")
        bloques.forEachIndexed { idx, bloque ->
            sb.append("${bloque.letra}<${df1(bloque.ancho)}>(${bloque.contenido})")
            if (idx < bloques.lastIndex) sb.append("P<${df1(anchoParanteCm)}>")
        }
        sb.append("]}")
        return sb.toString()
    }

    private fun aplicarCambiosCotas(
        bloques: List<BloqueTramo>,
        nuevosAnchos: List<Float>,
        nuevoAncho: Float,
        nuevoAlto: Float,
        nuevasAlturas: List<List<Float>>
    ) {
        if (bloques.isEmpty()) return
        // El ancho y el alto de la ventana entera mandan sobre lo demás: se fijan antes de repartir,
        // y los tramos que el vidriero no tocó absorben la diferencia.
        if (nuevoAncho > 0f) anchoCm = nuevoAncho
        if (nuevoAlto > 0f) altoCm = nuevoAlto
        val n = bloques.size
        val nParantes = (n - 1).coerceAtLeast(0)
        val totalUtil = anchoCm - nParantes * anchoParanteCm

        // Identificar tramos que el usuario cambió explícitamente
        val changedIdx = (0 until n).filter { abs(nuevosAnchos.getOrElse(it) { bloques[it].ancho } - bloques[it].ancho) > 0.1f }
        val lockedNoChangedIdx = (0 until n).filter { tramosBlockeados.getOrElse(it) { false } && it !in changedIdx }
        val absorbIdx = (0 until n).filter { it !in changedIdx && it !in lockedNoChangedIdx }

        val sumChanged = changedIdx.sumOf { nuevosAnchos.getOrElse(it) { bloques[it].ancho }.toDouble() }.toFloat().coerceAtLeast(0f)
        val sumLockedNoChanged = lockedNoChangedIdx.sumOf { bloques[it].ancho.toDouble() }.toFloat()
        val remainingForAbsorb = (totalUtil - sumChanged - sumLockedNoChanged).coerceAtLeast(0f)
        val totalAbsorb = absorbIdx.sumOf { bloques[it].ancho.toDouble() }.toFloat()

        val anchosFinal = (0 until n).map { i ->
            when {
                i in lockedNoChangedIdx -> bloques[i].ancho
                i in changedIdx -> nuevosAnchos.getOrElse(i) { bloques[i].ancho }.coerceAtLeast(5f)
                totalAbsorb > 0f -> (bloques[i].ancho / totalAbsorb * remainingForAbsorb).coerceAtLeast(5f)
                else -> bloques[i].ancho
            }
        }

        val bloquesFinal = bloques.mapIndexed { i, bloque ->
            var bt = BloqueTramo(bloque.letra, anchosFinal[i], bloque.contenido)
            // Reescribir los <w> de los módulos para que su suma sea el nuevo ancho del tramo,
            // en TODOS los tramos que cambian (editado y los que absorben). Si no, el motor
            // desigual deriva el puente de una suma de módulos desactualizada.
            if (kotlin.math.abs(anchosFinal[i] - bloque.ancho) > 0.05f) {
                bt = reescalarFranjasATramo(bt, anchosFinal[i])
            }
            bt
        }

        cargarDesdePaquete(reconstruirPaqueteConBloques(bloquesFinal))
        aplicarAlturasDeFranjas(nuevasAlturas)
        actualizarVista()
        actualizarPanelCotas()
    }

    /**
     * Las alturas de las franjas, tramo a tramo y franja a franja, por el modelo.
     *
     * Va por el modelo y no reescribiendo el texto porque cambiar una franja cambia también las
     * otras del tramo —el resto absorbe para que sigan sumando el alto de la ventana—, y el
     * reemplazo de `s<…>` no lo hacía: dejaba las alturas sumando más que la ventana.
     */
    private fun aplicarAlturasDeFranjas(alturas: List<List<Float>>) {
        if (alturas.all { fila -> fila.all { it <= 0f } }) return
        val nuevo = runCatching {
            var d = DisenoNova.desdePaquete(paqueteActualLectura()) ?: return@runCatching null
            alturas.forEachIndexed { iTramo, fila ->
                fila.forEachIndexed { iFranja, alto ->
                    if (alto > 0f) d = d.conAlturaDeFranja(iTramo, iFranja, alto)
                }
            }
            d.aPaquete()
        }.getOrNull() ?: return
        cargarDesdePaquete(nuevo)
    }

    private fun actualizarPanelCotas() {
        val bloques = parsearBloquesTramo()
        binding.tvTituloCotas.text = "Medidas"
        binding.contenedorCotas.removeAllViews()

        val dp = resources.displayMetrics.density
        val dp4 = (4 * dp).toInt()
        val dp8 = (8 * dp).toInt()

        // El panel ocupa todo el ancho, y ese sitio se usa para que quepan MÁS datos, no para
        // estirar las casillas: todo va en dos columnas, incluidos los tramos, que de dos en dos
        // ocupan la mitad de alto y tapan menos el dibujo.
        val (vistaAncho, etAnchoVentana) = campoConEtiqueta("Ancho ventana:", df1(anchoCm), dp4)
        val (vistaAlto, etAlto) = campoConEtiqueta("Alto ventana:", df1(altoCm), dp4)
        etAnchoVentana.tag = "cotas_ancho"
        etAlto.tag = "cotas_alto"
        binding.contenedorCotas.addView(enDosColumnas(vistaAncho, vistaAlto, dp4))

        // Agregar y quitar tramos. Las franjas ya no van aquí: cada tramo lleva las suyas, que es
        // lo que permite una bandera en un tramo y ninguna mocheta en el de al lado.
        binding.contenedorCotas.addView(enDosColumnas(
            filaMasMenos(
                etiqueta = "Tramos",
                dp = dp,
                puedeQuitar = bloques.size > 1,
                onQuitar = { cambiarEstructura { it.conTramoQuitado() } },
                onAgregar = { cambiarEstructura { it.conTramoAgregado() } }
            ).apply { tag = "cotas_tramos" },
            null,
            dp4
        ))

        if (bloques.isEmpty()) {
            binding.contenedorCotas.addView(TextView(this).apply {
                text = "Sin tramos"; textSize = 11f
            })
            limitarAltoDelPanel()
            return
        }

        // Sync tramosBlockeados
        while (tramosBlockeados.size < bloques.size) tramosBlockeados.add(false)
        while (tramosBlockeados.size > bloques.size) tramosBlockeados.removeAt(tramosBlockeados.lastIndex)

        val etAnchos = mutableListOf<EditText>()
        val etAlturas = mutableListOf<List<EditText>>()
        val etAltosDeTramo = mutableListOf<EditText>()
        val capturedBloques = bloques.toList()

        // Los tramos, uno debajo de otro: cada tarjeta ya se reparte por dentro.
        bloques.forEachIndexed { i, bloque ->
            binding.contenedorCotas.addView(
                vistaDeTramo(i, bloque, dp, dp4, etAnchos, etAlturas, etAltosDeTramo)
            )
        }

        binding.contenedorCotas.addView(Button(this).apply {
            text = "Aplicar"
            tag = "cotas_aplicar"
            textSize = 11f; isAllCaps = false
            setBackgroundColor(Color.parseColor("#1976D2"))
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                it.setMargins(0, dp8, 0, 0)
            }
            setOnClickListener {
                val nuevosAnchos = etAnchos.map { it.text.toString().aNumeroSeguro() }
                val nuevoAncho = etAnchoVentana.text.toString().aNumeroSeguro()
                // El alto sale de la casilla que se haya tocado: la de arriba o la de cualquier
                // tarjeta, que muestran el mismo alto de la ventana.
                val nuevoAlto = (listOf(etAlto) + etAltosDeTramo)
                    .map { it.text.toString().aNumeroSeguro() }
                    .firstOrNull { it > 0f && abs(it - altoCm) > 0.05f } ?: altoCm
                // Solo las alturas que el vidriero TOCÓ. Si se mandan todas, la última pisa a las
                // anteriores: al subir el puente, la mocheta con su valor de antes lo devolvía a
                // donde estaba.
                val alturasDeAntes = capturedBloques.map { b -> franjasDelBloque(b).map { it.second } }
                val nuevasAlturas = etAlturas.mapIndexed { iTramo, fila ->
                    fila.mapIndexed { iFranja, et ->
                        val valor = et.text.toString().aNumeroSeguro()
                        val antes = alturasDeAntes.getOrNull(iTramo)?.getOrNull(iFranja) ?: 0f
                        if (abs(valor - antes) > 0.05f) valor else 0f
                    }
                }
                aplicarCambiosCotas(capturedBloques, nuevosAnchos, nuevoAncho, nuevoAlto, nuevasAlturas)
            }
        })

        limitarAltoDelPanel()
    }

    /**
     * Un tramo en su tarjeta: arriba sus datos —nombre, ancho y alto—, y abajo el espacio partido
     * en dos: a la izquierda sus franjas, cada una con su altura, y a la derecha sus módulos como
     * recuadros que se tocan para cambiar de fijo a corrediza.
     */
    private fun vistaDeTramo(
        i: Int,
        bloque: BloqueTramo,
        dp: Float,
        dp4: Int,
        etAnchos: MutableList<EditText>,
        etAlturas: MutableList<List<EditText>>,
        etAltosDeTramo: MutableList<EditText>
    ): View {
        val tarjeta = androidx.cardview.widget.CardView(this).apply {
            radius = 6 * dp
            cardElevation = 2 * dp
            useCompatPadding = true
            setContentPadding(dp4, dp4, dp4, dp4)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, dp4 / 2, 0, dp4 / 2) }
        }
        val columna = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        // ---- Arriba: los datos del tramo ----
        val cabecera = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        cabecera.addView(TextView(this).apply {
            text = "tramo ${i + 1}"
            textSize = 11f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setPadding(0, dp4, 0, dp4)
        })
        val bloqueado = tramosBlockeados.getOrElse(i) { false }
        cabecera.addView(Button(this).apply {
            text = if (bloqueado) "Bloq." else "Libre"
            textSize = 9f
            isAllCaps = false
            setPadding(dp4, 0, dp4, 0)
            setBackgroundColor(if (bloqueado) Color.parseColor("#E53935") else Color.parseColor("#78909C"))
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (22 * dp).toInt()).also {
                it.marginStart = dp4
            }
            setOnClickListener {
                tramosBlockeados[i] = !tramosBlockeados[i]
                actualizarPanelCotas()
            }
        })
        columna.addView(cabecera)

        val (vistaAncho, etAncho) = campoConEtiqueta("Ancho:", df1(bloque.ancho), dp4)
        etAncho.tag = "cotas_ancho_$i"
        etAnchos.add(etAncho)
        // El alto es el de la ventana: todos los tramos llegan de piso a techo. Se muestra aquí
        // para tener las dos medidas del tramo juntas, y editarlo cambia el alto de la ventana.
        val (vistaAltoTramo, etAltoTramo) = campoConEtiqueta("Alto:", df1(altoCm), dp4)
        etAltoTramo.tag = "cotas_alto_tramo_$i"
        etAltosDeTramo.add(etAltoTramo)
        columna.addView(enDosColumnas(vistaAncho, vistaAltoTramo, dp4))

        // ---- Abajo: a la izquierda las franjas, a la derecha los módulos ----
        val franjas = franjasDelBloque(bloque)
        val ladoFranjas = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        ladoFranjas.addView(filaBotones(
            etiqueta = "Franjas",
            dp = dp,
            botonPanel("−", dp, franjas.size > 1) { cambiarEstructura { d -> d.conFranjaQuitadaEnTramo(i) } },
            botonPanel("+", dp, true, accionLarga = { dialogoAgregarFranjaEnTramo(i) }) {
                cambiarEstructura { d -> d.conFranjaAgregadaEnTramo(i) }
            }
        ).apply { tag = "cotas_franjas_$i" })
        val campos = mutableListOf<EditText>()
        franjas.forEachIndexed { j, (tipo, altoFranja) ->
            // El sistema es el puente; las mochetas se numeran para poder distinguirlas.
            val nombre = if (tipo == 's') "puente:" else "mocheta ${franjas.take(j + 1).count { it.first != 's' }}:"
            val (vista, et) = campoConEtiqueta(nombre, df1(altoFranja), dp4)
            et.tag = "cotas_franja_${i}_$j"
            campos.add(et)
            ladoFranjas.addView(vista)
        }
        etAlturas.add(campos)

        val ladoModulos = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val modulosSistema = modulosDelSistema(bloque)
        ladoModulos.addView(filaBotones(
            etiqueta = "Módulos",
            dp = dp,
            botonPanel("−", dp, modulosSistema.size > 1) { quitarModuloEnTramo(i) },
            botonPanel("+", dp, true) { agregarModuloEnTramo(i, 'f') }
        ).apply { tag = "$TAG_MODULOS$i" })
        ladoModulos.addView(recuadrosDeModulos(i, modulosSistema, dp))

        columna.addView(enDosColumnas(ladoFranjas, ladoModulos, dp4))
        tarjeta.addView(columna)
        return tarjeta
    }

    /**
     * Los módulos del tramo como recuadros con su letra. Un toque cambia fijo por corrediza y al
     * revés, sin diálogos, igual que en el editor de la mampara.
     *
     * Van dentro de un scroll horizontal y con el cuadro siempre del mismo tamaño: repartiendo el
     * ancho, con muchos módulos acertar al que se quiere es una lotería.
     */
    private fun recuadrosDeModulos(indiceTramo: Int, modulos: List<Char>, dp: Float): View {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        modulos.forEachIndexed { j, tipo ->
            val esCorrediza = tipo == 'c'
            fila.addView(TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams((34 * dp).toInt(), (34 * dp).toInt())
                    .also { it.marginEnd = (3 * dp).toInt() }
                gravity = Gravity.CENTER
                text = if (esCorrediza) "C" else "F"
                textSize = 14f
                setTypeface(null, Typeface.BOLD)
                // La corrediza se distingue por color además de por la letra: en obra se mira rápido.
                setTextColor(if (esCorrediza) Color.parseColor("#1565C0") else Color.parseColor("#37474F"))
                setBackgroundResource(R.drawable.bg_opcion_seleccionada)
                tag = "cotas_modulo_${indiceTramo}_$j"
                setOnClickListener { alternarTipoModuloEnTramo(indiceTramo, j) }
            })
        }
        return android.widget.HorizontalScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, (4 * dp).toInt(), 0, 0) }
            isHorizontalScrollBarEnabled = true
            addView(fila)
        }
    }

    /** Cambia un fijo por corrediza y al revés, en la franja de sistema de ese tramo. */
    private fun alternarTipoModuloEnTramo(indiceTramo: Int, indiceModulo: Int) {
        val bloqueados = tramosLibresBloqueados()
        aplicarAlModelo { d ->
            val tramo = d.tramos.getOrNull(indiceTramo) ?: return@aplicarAlModelo d
            val iSistema = tramo.franjas.indexOfFirst { it.esSistema }.takeIf { it >= 0 } ?: 0
            d.conTipoCambiado(indiceTramo, iSistema, indiceModulo, bloqueados)
        }
        actualizarPanelCotas()
    }

    /** Las franjas de un tramo tal como están en el paquete: tipo (`s`/`m`) y altura. */
    private fun franjasDelBloque(bloque: BloqueTramo): List<Pair<Char, Float>> =
        splitTopLevelSemicolon(bloque.contenido).map { token ->
            val t = token.trim()
            val tipo = if (t.firstOrNull()?.lowercaseChar() == 'm') 'm' else 's'
            val alto = if (t.length > 1 && t[1] == '<') {
                t.substringAfter('<').substringBefore('>').aNumeroSeguro()
            } else 0f
            tipo to alto
        }

    /** Un `etiqueta [casilla]`, suelto para poder meterlo en una columna. */
    private fun campoConEtiqueta(label: String, valor: String, dp4: Int): Pair<View, EditText> {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, 1, 0, 1) }
        }
        fila.addView(TextView(this).apply {
            text = label; textSize = 10f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.45f)
            setSingleLine(true)
            setPadding(0, dp4, dp4, dp4)
        })
        val et = EditText(this).apply {
            setText(valor); textSize = 10f; setSingleLine(true)
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.55f)
            setPadding(dp4, 2, dp4, 2)
        }
        fila.addView(et)
        return fila to et
    }

    /** Dos vistas lado a lado, a mitades. Si la segunda falta, la primera se queda con su mitad. */
    private fun enDosColumnas(izquierda: View, derecha: View?, dp4: Int): View {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, dp4 / 2, 0, dp4 / 2) }
        }
        fun mitad(v: View) = v.apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                .also { it.marginStart = dp4 / 2; it.marginEnd = dp4 / 2 }
        }
        fila.addView(mitad(izquierda))
        // Sin pareja se deja el hueco: así las columnas no bailan de una fila a otra.
        fila.addView(mitad(derecha ?: View(this)))
        return fila
    }

    /**
     * El panel de cotas crece hacia arriba, y con cuatro o cinco tramos tapaba el diseño entero.
     *
     * `android:maxHeight` no sirve para esto —ScrollView no lo respeta—, así que se mide lo que
     * ocupa el contenido y, si pasa de poco menos de la mitad de la pantalla, se le fija esa
     * altura y lo demás se desplaza dentro. Mientras quepa, el panel sigue siendo del tamaño de
     * lo que muestra.
     */
    private fun limitarAltoDelPanel() {
        val scroll = binding.scrollCotas
        scroll.post {
            val maximo = (resources.displayMetrics.heightPixels * 0.42f).toInt()
            // El hijo de un ScrollView se mide sin límite, así que su alto es el del contenido
            // entero aunque el scroll ya esté recortado.
            val contenido = binding.contenedorCotas.height
            val alto = if (contenido > maximo) maximo else ViewGroup.LayoutParams.WRAP_CONTENT
            if (scroll.layoutParams.height != alto) {
                scroll.layoutParams = scroll.layoutParams.apply { height = alto }
            }
        }
    }

    /**
     * Deja el lienzo justo encima del bloque de controles, sea cual sea su altura.
     *
     * El margen fijo del layout valía mientras el panel medía siempre lo mismo; con el panel de
     * cotas creciendo por tramos, el dibujo quedaba debajo y no se veía. Ahora el dibujo se
     * encoge lo que haga falta y siempre se ve entero.
     */
    private fun subirLienzoSobreLosControles() {
        val hueco = (8 * resources.displayMetrics.density).toInt()
        // El bloque de controles tiene su propio margen contra el botón de abajo: si no se cuenta,
        // el lienzo acaba justo esos milímetros por debajo de donde empieza el panel.
        val margenControles =
            (binding.overlayControles.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
        val alto = binding.overlayControles.height + margenControles + hueco
        val lp = binding.vistaDiseno.layoutParams as? ViewGroup.MarginLayoutParams ?: return
        if (lp.bottomMargin == alto) return
        lp.bottomMargin = alto
        binding.vistaDiseno.layoutParams = lp
    }

    /**
     * Un botón cuadrado del panel, con la forma de los del editor de la mampara: tamaño fijo,
     * azul cuando se puede pulsar y apagado cuando no, para que se vea de un vistazo que ya no
     * queda nada que quitar.
     */
    private fun botonPanel(
        simbolo: String,
        dp: Float,
        activo: Boolean,
        accionLarga: (() -> Unit)? = null,
        accion: () -> Unit
    ): TextView = TextView(this).apply {
        layoutParams = LinearLayout.LayoutParams((34 * dp).toInt(), (30 * dp).toInt())
            .also { it.marginStart = (3 * dp).toInt() }
        gravity = Gravity.CENTER
        text = simbolo
        textSize = 15f
        setTypeface(null, Typeface.BOLD)
        setTextColor(if (activo) Color.parseColor("#1565C0") else Color.parseColor("#BBBBBB"))
        setBackgroundResource(R.drawable.bg_opcion_seleccionada)
        isEnabled = activo
        if (activo) setOnClickListener { accion() }
        if (accionLarga != null) setOnLongClickListener { accionLarga(); true }
    }

    /** Una fila `etiqueta  [botones…]` del panel de cotas. */
    private fun filaBotones(etiqueta: String, dp: Float, vararg botones: View): View {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, (2 * dp).toInt(), 0, (2 * dp).toInt()) }
        }
        fila.addView(TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = etiqueta
            textSize = 11f
            setSingleLine(true)
            ellipsize = android.text.TextUtils.TruncateAt.END
            setTypeface(null, Typeface.BOLD)
        })
        botones.forEach { fila.addView(it) }
        return fila
    }

    /** La fila `etiqueta [−][+]` de tramos y franjas. */
    private fun filaMasMenos(
        etiqueta: String,
        dp: Float,
        puedeQuitar: Boolean,
        onQuitar: () -> Unit,
        onAgregar: () -> Unit,
        onAgregarLargo: (() -> Unit)? = null
    ): View = filaBotones(
        etiqueta, dp,
        botonPanel("−", dp, puedeQuitar, accion = onQuitar),
        botonPanel("+", dp, true, accionLarga = onAgregarLargo, accion = onAgregar)
    )

    /** Los módulos de la franja de sistema del tramo, como letras: `f c c f`. */
    private fun modulosDelSistema(bloque: BloqueTramo): List<Char> {
        val tokens = splitTopLevelSemicolon(bloque.contenido)
        val sistema = tokens.firstOrNull { it.trim().startsWith("s", true) } ?: tokens.firstOrNull()
        val mods = sistema?.let { extraerBloqueModulosFranja(it) } ?: return emptyList()
        return Regex("""[fcFC]""").findAll(mods).map { it.value.first().lowercaseChar() }.toList()
    }

    /**
     * Agrega un fijo o una corrediza al final de la franja de sistema del tramo [indiceTramo].
     *
     * No suelta los bloqueos, al revés que [cambiarEstructura]: los tramos siguen siendo los
     * mismos y en el mismo orden, así que el ancho que el vidriero fijó sigue valiendo. Lo que
     * hace el reparto es repartir de nuevo lo que queda entre los tramos libres.
     */
    private fun agregarModuloEnTramo(indiceTramo: Int, tipo: Char) {
        val bloqueados = tramosLibresBloqueados()
        aplicarAlModelo { d ->
            val tramo = d.tramos.getOrNull(indiceTramo) ?: return@aplicarAlModelo d
            val iSistema = tramo.franjas.indexOfFirst { it.esSistema }.takeIf { it >= 0 } ?: 0
            val ultimo = tramo.franjas[iSistema].modulos.lastIndex
            d.conModuloAgregado(indiceTramo, iSistema, ultimo, tipo, bloqueados)
        }
        actualizarPanelCotas()
    }

    /** Quita el último módulo de la franja de sistema del tramo. Siempre queda uno. */
    private fun quitarModuloEnTramo(indiceTramo: Int) {
        val bloqueados = tramosLibresBloqueados()
        aplicarAlModelo { d ->
            val tramo = d.tramos.getOrNull(indiceTramo) ?: return@aplicarAlModelo d
            val iSistema = tramo.franjas.indexOfFirst { it.esSistema }.takeIf { it >= 0 } ?: 0
            d.conModuloQuitado(indiceTramo, iSistema, tramo.franjas[iSistema].modulos.lastIndex, bloqueados)
        }
        actualizarPanelCotas()
    }

    /**
     * Cambio de estructura del diseño —tramos o franjas— pasando por el modelo.
     *
     * Agregar o quitar corre los índices de los tramos, así que los bloqueos dejarían de apuntar
     * a donde apuntaban: se sueltan todos, igual que al partir un tramo.
     */
    private fun cambiarEstructura(operacion: (DisenoNova) -> DisenoNova) {
        tramosBlockeados.clear()
        indiceModuloActivo = -1
        if (!aplicarAlModelo(operacion)) {
            Toast.makeText(this, "No se pudo leer el diseño.", Toast.LENGTH_SHORT).show()
            return
        }
        actualizarPanelCotas()
    }

    // =========================== UTILIDADES ===========================

    /**
     * Primer índice (absoluto en fr.modulos) del tramo activo.
     * Tramo 0 siempre empieza en 0. Tramo N empieza donde termina el parante N-1.
     */
    private fun inicioDelTramoActivo(fr: Franja): Int {
        if (fr.parantes.isEmpty() || indiceTramoActivo <= 0) return 0
        val parantes = fr.parantes.sorted()
        return parantes.getOrElse(indiceTramoActivo - 1) { 0 }
    }

    /**
     * Índice de inserción al final del tramo activo dentro de fr.modulos.
     * En diseño de un solo tramo (sin parantes) devuelve fr.modulos.size.
     * En multi-tramo devuelve la posición justo ANTES del parante siguiente,
     * que corresponde al límite derecho del tramo [indiceTramoActivo].
     */
    private fun finDelTramoActivo(fr: Franja): Int {
        if (fr.parantes.isEmpty() || indiceTramoActivo < 0) return fr.modulos.size
        val parantes = fr.parantes.sorted()
        return parantes.getOrElse(indiceTramoActivo) { fr.modulos.size }
    }

    /**
     * Si el cuerpo contiene múltiples bloques T<>(...), los fusiona en un único
     * cuerpo de franjas con ;P; entre tramos (igual que el formato de bloque único).
     * Retorna null cuando solo hay un bloque T (el llamador usa su lógica habitual).
     */
    private fun fusionarBloquesTEnCuerpo(s: String): String? {
        data class BloqueT(val contenido: String)
        val bloques = mutableListOf<BloqueT>()
        var i = 0
        while (i < s.length) {
            if (s[i].lowercaseChar() == 't') {
                val iT = i; i++
                if (i < s.length && s[i].lowercaseChar() in 'a'..'z' && s[i] != '<') i++
                if (i >= s.length || s[i] != '<') { i = iT + 1; continue }
                val gtIdx = s.indexOf('>', i); if (gtIdx < 0) break
                i = gtIdx + 1
                if (i >= s.length || s[i] != '(') continue
                val openParen = i; var depth = 0; var closeParen = openParen
                for (j in openParen until s.length) {
                    when (s[j]) { '(' -> depth++; ')' -> { depth--; if (depth == 0) { closeParen = j; break } } }
                }
                bloques.add(BloqueT(s.substring(openParen + 1, closeParen)))
                i = closeParen + 1
            } else { i++ }
        }
        if (bloques.size <= 1) return null

        // Por cada tipo de franja del primer bloque, concatenar los módulos de todos los bloques con ;P;
        val primerasFranjas = splitTopLevelSemicolon(bloques[0].contenido)
        return primerasFranjas.map { primerToken ->
            val trimmed = primerToken.trim()
            val prefix = trimmed.firstOrNull()?.lowercaseChar()?.toString() ?: return@map primerToken
            val reAltura = Regex("""^[ms]\s*<\s*(-?\d+(?:[.,]\d+)?)\s*>""", RegexOption.IGNORE_CASE)
            val alt = reAltura.find(trimmed)?.groupValues?.get(1)
            val heightTag = if (alt != null) "<$alt>" else ""
            val modsTramos = bloques.mapNotNull { bloque ->
                val tokens = splitTopLevelSemicolon(bloque.contenido)
                val tk = tokens.firstOrNull { it.trim().startsWith(prefix, ignoreCase = true) }
                tk?.let { extraerBloqueModulosFranja(it) }
            }
            if (modsTramos.isEmpty()) primerToken
            else "$prefix$heightTag(${modsTramos.joinToString(";P;")})"
        }.joinToString(";")
    }

    private fun String.aNumeroSeguro(): Float {
        val s = trim().replace(',', '.')
        return s.toFloatOrNull() ?: 0f
    }

    /** 0 o 1 decimal, siempre con punto. */
    private fun df1(defo: Float): String {
        return if (defo % 1f == 0f) defo.toInt().toString()
        else "%.1f".format(defo).replace(",", ".")
    }

    // =========================== ARCHIVOS (headless) ===========================

    private fun guardarPngEnCacheYUri(bmp: android.graphics.Bitmap, nombre: String): android.net.Uri {
        val f = java.io.File(cacheDir, nombre)
        java.io.FileOutputStream(f).use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        return androidx.core.content.FileProvider.getUriForFile(this, "$packageName.fileprovider", f)
    }

    private fun guardarTextoEnCacheYUri(texto: String, nombre: String): android.net.Uri {
        val f = java.io.File(cacheDir, nombre)
        f.writeText(texto, Charsets.UTF_8)
        return androidx.core.content.FileProvider.getUriForFile(this, "$packageName.fileprovider", f)
    }

    /**
     * Diseño en blanco: un solo tramo con un paño, sin reparto ni mochetas. Deja la pantalla como
     * una hoja en blanco para empezar de cero, venga el diseño de donde venga.
     */
    private fun paqueteEnBlanco(): String {
        val tipoTxt = if (tipo == TipoEnsamble.APA) "apa" else "ina"
        return "{nova,${tipoTxt},[${df1(anchoCm)},${df1(altoCm)}:Tl<${df1(anchoCm)}>(s(f))]}"
    }

    /** Borra el diseño actual y deja el lienzo en blanco. */
    private fun limpiarDiseno() {
        indiceFranjaActiva = -1
        indiceTramoActivo = -1
        indiceModuloActivo = -1
        corteVerticalCm = null
        tramosBlockeados.clear()
        cerrarFlotanteModulos()
        cargarDesdePaquete(paqueteEnBlanco())
        actualizarVista()
    }

    /**
     * Envía el diseño actual a NovaCorrediza. Lo usa SOLO el botón de la calculadora; el botón
     * Atrás del sistema NO lo llama, así que Atrás vuelve sin aplicar cambios al diseño.
     */
    private fun prepararResultadoDiseno() {
        val paqueteSalida = if (paqueteOriginal.isNotBlank()) {
            paqueteConDimensionesActualizadas(paqueteOriginal)
        } else {
            paqueteBase()
        }
        setResult(RESULT_OK, Intent().apply {
            putExtra(RESULT_PAQUETE, paqueteSalida)
        })
    }

    // ==================== ENGANCHES PARA LAS PRUEBAS EN EL CELULAR ====================
    // Sirven para que la prueba instrumentada pueda leer el diseño y pulsar los botones sin
    // depender de coordenadas de pantalla. No los usa la app.

    @androidx.annotation.VisibleForTesting
    fun paqueteParaPruebas(): String = paqueteActualLectura()

    @androidx.annotation.VisibleForTesting
    fun estadoParaPruebas(): String =
        "ancho=$anchoCm alto=$altoCm franjas=${franjas.size} orig=[$paqueteOriginal]"

    /**
     * Carga un diseño en la pantalla ya abierta. Las pruebas lo usan en vez de mandar el diseño
     * por el intent: el celular restaura la pantalla que quedó abierta con SUS extras, y entonces
     * la prueba acabaría midiendo el diseño del usuario en lugar del suyo.
     */
    /**
     * ¿La VISTA acepta este paquete? Devuelve null si sí, y el motivo si no.
     *
     * `actualizarVista` se traga el fallo y dibuja un diseño de emergencia de un solo fijo, así
     * que un paquete rechazado se ve como "no pasa nada" aunque el diseño sea correcto.
     */
    @androidx.annotation.VisibleForTesting
    fun vistaRechazaParaPruebas(paquete: String): String? =
        runCatching { binding.vistaDiseno.actualizarDesdePaquete(paquete, 0f, 0f, mochetaLateralCm) }
            .exceptionOrNull()?.let { "${it::class.simpleName}: ${it.message}" }

    /** El dibujo tal cual se está viendo, para poder mirarlo desde fuera del celular. */
    @androidx.annotation.VisibleForTesting
    fun dibujoParaPruebas(): android.graphics.Bitmap =
        binding.vistaDiseno.exportarSoloDisenoBitmap(paddingPx = 8)

    @androidx.annotation.VisibleForTesting
    fun cargarParaPruebas(paquete: String) {
        cargarDesdePaquete(paquete)
        actualizarVista()
    }

    @androidx.annotation.VisibleForTesting
    fun seleccionarParaPruebas(franja: Int, tramo: Int, modulo: Int) {
        indiceFranjaActiva = franja
        indiceTramoActivo = tramo
        indiceModuloActivo = modulo
    }

    /**
     * Pulsa el + o el − de la fila marcada del panel de cotas: `cotas_tramos` o `cotas_franjas`.
     *
     * Va por las vistas de verdad, no por el modelo, porque el fallo que interesa cazar es
     * justamente el de un botón que no hace nada.
     */
    @androidx.annotation.VisibleForTesting
    fun pulsarEstructuraParaPruebas(que: String, mas: Boolean): Boolean {
        actualizarPanelCotas()
        return pulsarEnFilaParaPruebas(que, if (mas) "+" else "−")
    }

    /**
     * Pulsa uno de los botones de módulos del tramo [indiceTramo] en el panel de cotas:
     * `−`, `F` o `C`. Devuelve false si esa fila o ese botón no están.
     */
    @androidx.annotation.VisibleForTesting
    fun pulsarModuloParaPruebas(indiceTramo: Int, simbolo: String): Boolean {
        actualizarPanelCotas()
        return pulsarEnFilaParaPruebas("$TAG_MODULOS$indiceTramo", simbolo)
    }


    /** Las bandas de las franjas de un tramo, tal como las ve el toque. */
    @androidx.annotation.VisibleForTesting
    fun bandasDeFranjaParaPruebas(indiceTramo: Int) =
        binding.vistaDiseno.bandasDeFranjaParaPruebas(indiceTramo)

    /** Diagnóstico del dibujo. */
    @androidx.annotation.VisibleForTesting
    fun diagnosticoDibujoParaPruebas() = binding.vistaDiseno.diagnosticoParaPruebas()

    /** El ancho en pantalla de cada tramo. */
    @androidx.annotation.VisibleForTesting
    fun anchosDeTramoParaPruebas() = binding.vistaDiseno.anchosDeTramoParaPruebas()

    /** Toca el lienzo en ese punto y devuelve el (tramo, franja) que quedan seleccionados. */
    @androidx.annotation.VisibleForTesting
    fun tocarLienzoParaPruebas(x: Float, y: Float): Pair<Int, Int> {
        val t = android.os.SystemClock.uptimeMillis()
        val abajo = android.view.MotionEvent.obtain(t, t, android.view.MotionEvent.ACTION_DOWN, x, y, 0)
        val arriba = android.view.MotionEvent.obtain(t, t + 10, android.view.MotionEvent.ACTION_UP, x, y, 0)
        binding.vistaDiseno.dispatchTouchEvent(abajo)
        binding.vistaDiseno.dispatchTouchEvent(arriba)
        abajo.recycle()
        arriba.recycle()
        return indiceTramoActivo to indiceFranjaActiva
    }
    /** Abre el ancho del módulo elegido; devuelve false si no llegó a abrirse. */
    @androidx.annotation.VisibleForTesting
    fun abrirAnchoModuloParaPruebas(): Boolean = dialogoEditarAnchoModulo()

    /** El módulo seleccionado ahora mismo, o -1. */
    @androidx.annotation.VisibleForTesting
    fun moduloActivoParaPruebas(): Int = indiceModuloActivo

    /** ¿Está a la vista el mando flotante de módulos? */
    @androidx.annotation.VisibleForTesting
    fun hayFlotanteParaPruebas(): Boolean = flotanteModulos != null

    /** Pulsa un botón del mando flotante: `−`, `+` o el recuadro de un módulo por su índice. */
    @androidx.annotation.VisibleForTesting
    fun pulsarFlotanteParaPruebas(que: String): Boolean {
        val fila = flotanteModulos as? LinearLayout ?: return false
        val boton = (0 until fila.childCount).map { fila.getChildAt(it) }.firstOrNull {
            it.tag == "flotante_modulo_$que" || (it is TextView && it.text.toString() == que)
        } ?: return false
        return boton.performClick()
    }

    /** Toca el recuadro de un módulo, que es lo que cambia fijo por corrediza. */
    @androidx.annotation.VisibleForTesting
    fun tocarRecuadroModuloParaPruebas(indiceTramo: Int, indiceModulo: Int): Boolean {
        actualizarPanelCotas()
        val recuadro = binding.contenedorCotas
            .findViewWithTag<View>("cotas_modulo_${indiceTramo}_$indiceModulo") ?: return false
        return recuadro.performClick()
    }

    /** Busca la fila por su marca —esté donde esté, que ahora van en columnas— y pulsa su botón. */
    private fun pulsarEnFilaParaPruebas(marca: String, simbolo: String): Boolean {
        val fila = binding.contenedorCotas.findViewWithTag<LinearLayout>(marca) ?: return false
        val boton = (0 until fila.childCount)
            .map { fila.getChildAt(it) }
            .firstOrNull { it is TextView && it.text.toString() == simbolo } ?: return false
        return boton.performClick()
    }

    /** Abre el panel de cotas, como el botón de la cota. */
    @androidx.annotation.VisibleForTesting
    fun abrirCotasParaPruebas() {
        if (binding.panelCotasPlanos.visibility != View.VISIBLE) togglePanelCotasPlanos()
    }

    /** Alto del panel de cotas en pantalla y alto de la pantalla, para comprobar que no la tapa. */
    @androidx.annotation.VisibleForTesting
    fun altoDelPanelParaPruebas(): Pair<Int, Int> =
        binding.scrollCotas.height to resources.displayMetrics.heightPixels

    /**
     * Dónde acaba el lienzo y dónde empieza el bloque de controles, en pantalla. Si el primero
     * pasa del segundo, el panel está tapando el dibujo.
     */
    @androidx.annotation.VisibleForTesting
    fun bordesLienzoYControlesParaPruebas(): Pair<Int, Int> {
        val lienzo = IntArray(2)
        binding.vistaDiseno.getLocationOnScreen(lienzo)
        val controles = IntArray(2)
        binding.overlayControles.getLocationOnScreen(controles)
        return (lienzo[1] + binding.vistaDiseno.height) to controles[1]
    }

    /** Escribe en una casilla del panel de cotas: `cotas_ancho` o `cotas_alto`. */
    @androidx.annotation.VisibleForTesting
    fun escribirMedidaParaPruebas(cual: String, valor: String): Boolean {
        val et = binding.contenedorCotas.findViewWithTag<EditText>(cual) ?: return false
        et.setText(valor)
        return true
    }

    /** Pulsa el Aplicar del panel de cotas. */
    @androidx.annotation.VisibleForTesting
    fun pulsarAplicarCotasParaPruebas(): Boolean =
        binding.contenedorCotas.findViewWithTag<View>("cotas_aplicar")?.performClick() ?: false
}
