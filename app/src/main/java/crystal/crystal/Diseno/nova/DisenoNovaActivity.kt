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
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityDisenoNovaBinding
import kotlin.math.max

class DisenoNovaActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PAQUETE = "extra_paquete_diseno"
        const val EXTRA_MOCHETA_LATERAL_CM = "extra_mocheta_lateral_cm"
        const val EXTRA_HEADLESS = "extra_headless"
        const val EXTRA_RET_PADDING_PX = "extra_ret_padding_px"
        const val EXTRA_OUTPUT_FORMAT = "extra_output_format" // "svg" | "png"
        const val RESULT_URI = "resultado_uri_imagen"
        const val RESULT_PAQUETE = "resultado_paquete"
    }

    // ----------------- Estado del diseño (Float) -----------------
    private var clase: String = "nova"
    private var tipo: TipoEnsamble = TipoEnsamble.APA
    private var anchoCm: Float = 150f
    private var altoCm: Float = 120f
    private var mochetaLateralCm: Float = 0f
    private var corteVerticalCm: Float? = null
    private var paqueteOriginal: String = ""
    private var estructuraEditada: Boolean = false
    private val franjas: MutableList<Franja> = mutableListOf() // orden: abajo→arriba
    private var indiceFranjaActiva: Int = -1
    private var indiceTramoActivo: Int = -1
    private var indiceModuloActivo: Int = -1
    private val anchoParanteCm = 2.5f

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
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ---- MODO HEADLESS (genera imagen y termina) ----
        val paqueteIntent = intent.getStringExtra(EXTRA_PAQUETE) ?: "{nova,ina,[150,120:s(f)]}"
        mochetaLateralCm = intent.getFloatExtra(EXTRA_MOCHETA_LATERAL_CM, 0f)
        val paddingPx = intent.getIntExtra(EXTRA_RET_PADDING_PX, 0)
        val headless = intent.getBooleanExtra(EXTRA_HEADLESS, false)
        val formato = intent.getStringExtra(EXTRA_OUTPUT_FORMAT) ?: "png"

        if (headless) {
            val vista = VistaDiseno(this).apply {
                actualizarDesdePaquete(paqueteIntent, 0f, 0f, mochetaLateralCm)
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

        cargarDesdePaquete(paqueteIntent)

        // Toque directo en el lienzo para seleccionar franja (sin diálogos)
        binding.vistaDiseno.alClicFranja = { idx ->
            if (idx in franjas.indices) {
                indiceFranjaActiva = idx
                indiceModuloActivo = -1
                binding.vistaDiseno.resaltarFranja(idx, indiceTramoActivo)
                actualizarInfoSeleccion()
                actualizarVista()
            }
        }
        binding.vistaDiseno.alClicFranjaTramo = { tramo, franja ->
            indiceTramoActivo = tramo
            indiceFranjaActiva = franja
            indiceModuloActivo = -1
            binding.vistaDiseno.resaltarFranja(franja, tramo)
            actualizarInfoSeleccion()
            actualizarVista()
        }
        // Doble click en módulo para seleccionarlo
        binding.vistaDiseno.alDobleClicModulo = { franja, modulo ->
            indiceFranjaActiva = franja
            indiceModuloActivo = modulo
            binding.vistaDiseno.resaltarModulo(franja, modulo)
            actualizarInfoSeleccion()
        }

        // Botones flotantes (columna)
        binding.btnAgregarFranja.setOnClickListener { dialogoAgregarFranja() }
        binding.btnQuitarFranja.setOnClickListener { quitarFranja() }
        binding.btnAgregarModulo.setOnClickListener { dialogoAgregarModulo() }
        binding.btnQuitarModulo.setOnClickListener { quitarModulo() }
        binding.btnMostrarOcultar.setOnClickListener { alternarColumnaBotones() }
        binding.btnMedidasRapidas.setOnClickListener { dialogoCambiarMedidas() }
        binding.btnCotasPlanos.setOnClickListener { togglePanelCotasPlanos() }
        binding.btnTipoEnsamble.setOnClickListener { alternarEnsamble() }
        binding.btnEnviarCalculadora.setOnClickListener { Toast.makeText(this, "No disponible", Toast.LENGTH_SHORT).show() }
        binding.btnLimpiarDiseno.setOnClickListener { limpiarDiseno() }
        binding.btnProductos.setOnClickListener { togglePanelProductos() }

        // Acciones rápidas (panel productos)
        binding.cardFranja.setOnClickListener { dialogoAgregarFranja() }
        binding.cardTramos.setOnClickListener { dialogoTramos() }
        binding.fijo.setOnClickListener { agregarModuloDirecto(TipoModulo.FIJO) }
        binding.corrediza.setOnClickListener { agregarModuloDirecto(TipoModulo.CORREDIZA) }
        binding.parante.setOnClickListener { Toast.makeText(this, "Parante no disponible", Toast.LENGTH_SHORT).show() }
        binding.cardEliminarModulo.setOnClickListener { quitarModulo() }

        // Menú inferior
        binding.botonEditar.setOnClickListener { mostrarMenuEditar() }

        actualizarInfoSeleccion()
        actualizarVista()
    }

    // ===================================== MENÚ EDITAR =====================================

    private fun mostrarMenuEditar() {
        val opciones = arrayOf(
            "Seleccionar franja activa",
            "Editar altura de franja activa",     // solo altura (sin cambiar tipo)
            "Cambiar tipo S↔M (franja activa)",   // sin diálogo
            "Seleccionar módulo de franja activa",
            "Alternar ensamble (INA/APA)",
            "Cambiar medidas (ancho, alto, mocheta lateral)",
            "Ver / copiar paquete",
            "Limpiar diseño"
        )
        AlertDialog.Builder(this)
            .setTitle("Opciones")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> dialogoSeleccionarFranjaActiva()
                    1 -> dialogoEditarFranjaAlturaSolo()
                    2 -> alternarTipoFranjaActiva()
                    3 -> dialogoSeleccionarModulo()
                    4 -> { alternarEnsamble() }
                    5 -> dialogoCambiarMedidas()
                    6 -> dialogoVerPaquete()
                    7 -> limpiarDiseno()
                }
            }
            .show()
    }

    private fun alternarColumnaBotones() {
        val mostrar = binding.panelControles.visibility != View.VISIBLE
        if (mostrar) {
            binding.panelProductos.visibility = View.GONE
            binding.panelCotasPlanos.visibility = View.GONE
        }
        setPanelControlesVisible(mostrar)
    }

    private fun togglePanelProductos() {
        val panel = binding.panelProductos
        if (panel.visibility == View.VISIBLE) {
            panel.visibility = View.GONE
            return
        }
        setPanelControlesVisible(false)
        binding.panelCotasPlanos.visibility = View.GONE
        panel.visibility = View.VISIBLE
    }

    private fun togglePanelCotasPlanos() {
        val panel = binding.panelCotasPlanos
        if (panel.visibility == View.VISIBLE) {
            panel.visibility = View.GONE
            return
        }
        setPanelControlesVisible(false)
        binding.panelProductos.visibility = View.GONE
        actualizarPanelCotas()
        panel.visibility = View.VISIBLE
    }

    private fun setPanelControlesVisible(mostrar: Boolean) {
        binding.panelControles.visibility = if (mostrar) View.VISIBLE else View.GONE
        val vis = if (mostrar) View.VISIBLE else View.INVISIBLE
        binding.btnAgregarModulo.visibility = vis
        binding.btnQuitarModulo.visibility = vis
        binding.btnAgregarFranja.visibility = vis
        binding.btnQuitarFranja.visibility = vis
    }

    // --- Ver / copiar paquete ---
    private fun dialogoVerPaquete() {
        val paquete = aPaquete()
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

    private fun dialogoAgregarFranja() {
        var seleccion = 's'
        val opciones = arrayOf("s (sistema)", "m (mocheta)")
        AlertDialog.Builder(this)
            .setTitle("Agregar franja")
            .setSingleChoiceItems(opciones, 0) { _, which ->
                seleccion = if (which == 0) 's' else 'm'
            }
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
                        val alt = et.text.toString().aNumeroSeguro()
                        agregarFranja(seleccion, alt)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun dialogoAgregarModulo() {
        if (indiceFranjaActiva !in franjas.indices) {
            Toast.makeText(this, "Primero agrega/selecciona una franja.", Toast.LENGTH_SHORT).show()
            return
        }
        var seleccion = 'f'
        val opciones = arrayOf("f (fijo)", "c (corrediza)")
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 16, 24, 8)
        }
        val etCantidad = EditText(this).apply {
            hint = "Cantidad"
            inputType = InputType.TYPE_CLASS_NUMBER
            setText("1")
        }
        cont.addView(etCantidad)

        AlertDialog.Builder(this)
            .setTitle("Agregar módulos")
            .setView(cont)
            .setSingleChoiceItems(opciones, 0) { _, which ->
                seleccion = if (which == 0) 'f' else 'c'
            }
            .setPositiveButton("Agregar") { _, _ ->
                val cant = max(1, etCantidad.text.toString().toIntOrNull() ?: 1)
                repetirModulo(seleccion, cant)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun agregarFranja(ms: Char, altura: Float) {
        estructuraEditada = true
        val esSistema = (ms == 's' || ms == 'S')
        val nueva = Franja(
            esSistema = esSistema,
            alturaCm = max(0f, altura),
            modulos = mutableListOf(TipoModulo.FIJO) // default: siempre al menos un f
        )
        franjas.add(nueva)
        indiceFranjaActiva = franjas.lastIndex

        // Regla: si al agregar la última, su altura explícita supera el alto total,
        // se elimina y la anterior ocupa 100% del alto total.
        normalizarPorExcesoUltimaFranja()

        actualizarVista()
    }

    private fun normalizarPorExcesoUltimaFranja() {
        if (franjas.isEmpty()) return
        val sumaExplicita = franjas.sumOf { it.alturaCm.toDouble() }.toFloat()
        if (sumaExplicita > altoCm + 1e-3f && franjas.size >= 2) {
            // quitar la última y llevar la anterior a 100%
            franjas.removeAt(franjas.lastIndex)
            indiceFranjaActiva = franjas.lastIndex
            if (indiceFranjaActiva >= 0) {
                franjas[indiceFranjaActiva].alturaCm = altoCm
            }
            Toast.makeText(this, "Altura excede el total. Se descartó la última franja y la anterior ocupa 100%.", Toast.LENGTH_LONG).show()
        } else if (sumaExplicita > altoCm + 1e-3f && franjas.size == 1) {
            // solo una franja: clamp
            franjas[0].alturaCm = altoCm
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun quitarFranja() {
        if (franjas.isNotEmpty()) {
            estructuraEditada = true
            franjas.removeLast()
            indiceFranjaActiva = franjas.lastIndex
            actualizarVista()
        }
    }

    private fun repetirModulo(fc: Char, cantidad: Int) {
        if (indiceFranjaActiva !in franjas.indices) return
        estructuraEditada = true
        val fr = franjas[indiceFranjaActiva]
        val tipo = if (fc == 'c' || fc == 'C') TipoModulo.CORREDIZA else TipoModulo.FIJO
        repeat(cantidad) { fr.modulos.add(tipo) }
        recalcularCorteVerticalProporcional()
        actualizarVista()
    }

    private fun agregarModuloDirecto(tipo: TipoModulo) {
        if (indiceFranjaActiva !in franjas.indices) {
            Toast.makeText(this, "Primero agrega/selecciona una franja.", Toast.LENGTH_SHORT).show()
            return
        }
        estructuraEditada = true
        val fr = franjas[indiceFranjaActiva]
        val insertIdx = if (indiceModuloActivo in fr.modulos.indices) indiceModuloActivo + 1
                        else fr.modulos.size
        fr.modulos.add(insertIdx, tipo)
        // ajustar parantes: los que estaban a partir del punto de inserción se corren uno a la derecha
        fr.parantes.replaceAll { p -> if (p >= insertIdx) p + 1 else p }
        indiceModuloActivo = insertIdx
        binding.vistaDiseno.resaltarModulo(indiceFranjaActiva, insertIdx)
        recalcularCorteVerticalProporcional()
        actualizarVista()
    }

    private fun quitarModulo() {
        if (indiceFranjaActiva !in franjas.indices) return
        val fr = franjas[indiceFranjaActiva]
        if (fr.modulos.size <= 1) {
            Toast.makeText(this, "Debe quedar al menos un módulo.", Toast.LENGTH_SHORT).show()
            return
        }
        estructuraEditada = true
        val idx = if (indiceModuloActivo in fr.modulos.indices) indiceModuloActivo
                  else fr.modulos.lastIndex
        fr.modulos.removeAt(idx)
        // ajustar parantes: si estaban después del módulo eliminado, correr uno a la izquierda
        fr.parantes.replaceAll { p -> if (p > idx) p - 1 else p }
        // eliminar parantes que quedaron sin módulos a un lado
        fr.parantes.removeAll { p -> p <= 0 || p >= fr.modulos.size }
        indiceModuloActivo = -1
        binding.vistaDiseno.resaltarModulo(indiceFranjaActiva, -1)
        recalcularCorteVerticalProporcional()
        actualizarVista()
    }

    /**
     * Recalcula [corteVerticalCm] de forma proporcional al conteo de módulos
     * en cada tramo de la franja activa. Así, al agregar/quitar un módulo en
     * cualquier tramo, los anchos de TODOS los tramos se ajustan proporcionalmente
     * y todos los módulos quedan con el mismo ancho relativo.
     *
     * Para tramos de más de 2 secciones pone null para que VistaDiseno
     * distribuya igualmente desde las posiciones de parante.
     */
    private fun recalcularCorteVerticalProporcional() {
        val fr = franjas.getOrNull(indiceFranjaActiva) ?: return
        val parantes = fr.parantes.sorted()
        if (parantes.isEmpty()) {
            // Sin parantes: 1 tramo, no hay corte vertical
            corteVerticalCm = null
            return
        }
        if (parantes.size > 1) {
            // Más de 2 tramos: VistaDiseno distribuye igualmente desde posición de parante
            corteVerticalCm = null
            return
        }
        // Exactamente 1 parante → 2 tramos
        val n1 = parantes[0]                    // módulos en tramo 1
        val n2 = fr.modulos.size - parantes[0]  // módulos en tramo 2
        val total = n1 + n2
        if (total <= 0) { corteVerticalCm = null; return }
        val corte = anchoCm * n1.toFloat() / total.toFloat()
        corteVerticalCm = corte.takeIf { it > 0f && it < anchoCm }
    }

    private fun dialogoSeleccionarFranjaActiva() {
        if (franjas.isEmpty()) {
            Toast.makeText(this, "No hay franjas.", Toast.LENGTH_SHORT).show(); return
        }
        val items = franjas.mapIndexed { i, f ->
            val tipoTxt = if (f.esSistema) "S" else "M"
            val patron = f.modulos.joinToString("") { if (it == TipoModulo.CORREDIZA) "c" else "f" }
            "[$i] $tipoTxt  h=${df1(f.alturaCm)}  ($patron)"
        }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Seleccionar franja")
            .setSingleChoiceItems(items, indiceFranjaActiva.coerceAtLeast(0)) { d, which ->
                indiceFranjaActiva = which
                binding.vistaDiseno.resaltarFranja(which)
                d.dismiss()
                actualizarVista()
            }
            .show()
    }

    private fun dialogoEditarFranjaAlturaSolo() {
        if (indiceFranjaActiva !in franjas.indices) {
            Toast.makeText(this, "Selecciona una franja primero.", Toast.LENGTH_SHORT).show()
            return
        }
        val fr = franjas[indiceFranjaActiva]
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 8)
        }
        val etAltura = EditText(this).apply {
            hint = "Altura (cm)  0 = auto"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(df1(fr.alturaCm))
        }
        cont.addView(etAltura)

        AlertDialog.Builder(this)
            .setTitle("Altura de franja")
            .setView(cont)
            .setPositiveButton("Aplicar") { _, _ ->
                val alt = etAltura.text.toString().aNumeroSeguro().coerceAtLeast(0f)
                if (alt > altoCm && franjas.isNotEmpty()) {
                    Toast.makeText(this, "Altura > alto total. Se mantiene la anterior.", Toast.LENGTH_SHORT).show()
                } else {
                    estructuraEditada = true
                    fr.alturaCm = alt
                }
                actualizarVista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun dialogoSeleccionarModulo() {
        if (indiceFranjaActiva !in franjas.indices) {
            Toast.makeText(this, "Selecciona una franja primero.", Toast.LENGTH_SHORT).show()
            return
        }
        val fr = franjas[indiceFranjaActiva]
        val mods = fr.modulos
        val items = mods.mapIndexed { idx, m ->
            "[$idx] " + if (m == TipoModulo.CORREDIZA) "c (corrediza)" else "f (fijo)"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Módulo en franja [$indiceFranjaActiva]")
            .setItems(items) { _, which ->
                dialogoEditarModulo(which)
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun dialogoEditarModulo(indiceModulo: Int) {
        if (indiceFranjaActiva !in franjas.indices) return
        val fr = franjas[indiceFranjaActiva]
        if (indiceModulo !in fr.modulos.indices) return

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
                when (which) {
                    0 -> fr.modulos[indiceModulo] =
                        if (fr.modulos[indiceModulo] == TipoModulo.CORREDIZA) TipoModulo.FIJO else TipoModulo.CORREDIZA
                    1 -> fr.modulos.add(indiceModulo, TipoModulo.FIJO)
                    2 -> fr.modulos.add(indiceModulo, TipoModulo.CORREDIZA)
                    3 -> fr.modulos.add(indiceModulo + 1, TipoModulo.FIJO)
                    4 -> fr.modulos.add(indiceModulo + 1, TipoModulo.CORREDIZA)
                    5 -> {
                        if (fr.modulos.size <= 1) {
                            Toast.makeText(this, "Debe quedar al menos un módulo.", Toast.LENGTH_SHORT).show()
                            return@setItems
                        }
                        fr.modulos.removeAt(indiceModulo)
                    }
                }
                estructuraEditada = true
                recalcularCorteVerticalProporcional()
                actualizarVista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // =========================== MEDIDAS / ENSAMBLE ===========================

    private fun alternarTipoFranjaActiva() {
        if (indiceFranjaActiva !in franjas.indices) {
            Toast.makeText(this, "Selecciona una franja primero.", Toast.LENGTH_SHORT).show()
            return
        }
        val fr = franjas[indiceFranjaActiva]
        estructuraEditada = true
        fr.esSistema = !fr.esSistema
        Toast.makeText(
            this,
            if (fr.esSistema) "Franja cambiada a Sistema (S)" else "Franja cambiada a Mocheta (M)",
            Toast.LENGTH_SHORT
        ).show()
        actualizarVista()
    }

    private fun alternarEnsamble() {
        estructuraEditada = true
        tipo = if (tipo == TipoEnsamble.APA) TipoEnsamble.INA else TipoEnsamble.APA
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
                actualizarVista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun dialogoTramos() {
        val opciones = arrayOf(
            "Crear/ajustar corte vertical",
            "Editar franjas por tramos (selección acumulativa)"
        )
        AlertDialog.Builder(this)
            .setTitle("Tramo")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> dialogoCorteVerticalTramos()
                    1 -> dialogoSeleccionAcumulativaTramos()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun dialogoCorteVerticalTramos() {
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 8)
        }
        val etMedida = EditText(this).apply {
            hint = "Corte vertical desde la izquierda (cm)"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(corteVerticalCm?.let { df1(it) } ?: "")
        }
        cont.addView(etMedida)

        AlertDialog.Builder(this)
            .setTitle("Tramos")
            .setView(cont)
            .setPositiveButton("Aplicar") { _, _ ->
                val medida = etMedida.text.toString().aNumeroSeguro()
                if (medida <= 0f || medida >= anchoCm) {
                    Toast.makeText(this, "Ingresa una medida mayor a 0 y menor que el ancho.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                corteVerticalCm = medida
                actualizarVista()
            }
            .setNeutralButton("Limpiar") { _, _ ->
                corteVerticalCm = null
                actualizarVista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun dialogoSeleccionAcumulativaTramos() {
        val nTramos = contarTramosDesdePaqueteActual()
        if (nTramos <= 1 || franjas.isEmpty()) {
            Toast.makeText(this, "No hay tramos múltiples para edición acumulativa.", Toast.LENGTH_SHORT).show()
            return
        }

        val etiquetas = mutableListOf<String>()
        val refsFranja = mutableListOf<Int>()
        for (t in 1..nTramos) {
            franjas.forEachIndexed { idx, fr ->
                val tipoTxt = if (fr.esSistema) "Sistema" else "Mocheta"
                etiquetas.add("Tramo $t · $tipoTxt  h=${df1(fr.alturaCm)}")
                refsFranja.add(idx)
            }
        }
        val checks = BooleanArray(etiquetas.size)

        AlertDialog.Builder(this)
            .setTitle("Selecciona franjas")
            .setMultiChoiceItems(etiquetas.toTypedArray(), checks) { _, which, checked ->
                checks[which] = checked
            }
            .setPositiveButton("Aplicar altura") { _, _ ->
                val seleccion = checks.indices
                    .filter { checks[it] }
                    .map { refsFranja[it] }
                    .toSet()
                if (seleccion.isEmpty()) {
                    Toast.makeText(this, "Selecciona al menos una franja.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                dialogoAplicarAlturaAcumulativa(seleccion)
            }
            .setNeutralButton("Igualar seleccionadas") { _, _ ->
                val seleccion = checks.indices
                    .filter { checks[it] }
                    .map { refsFranja[it] }
                    .toSet()
                if (seleccion.size < 2) {
                    Toast.makeText(this, "Selecciona 2 o más para igualar.", Toast.LENGTH_SHORT).show()
                    return@setNeutralButton
                }
                val alturaBase = franjas[seleccion.first()].alturaCm
                estructuraEditada = true
                seleccion.forEach { idx -> franjas[idx].alturaCm = alturaBase }
                actualizarVista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun dialogoAplicarAlturaAcumulativa(indicesFranjas: Set<Int>) {
        val et = EditText(this).apply {
            hint = "Altura (cm) 0 = auto"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        AlertDialog.Builder(this)
            .setTitle("Aplicar altura")
            .setView(et)
            .setPositiveButton("Aplicar") { _, _ ->
                val alt = et.text.toString().aNumeroSeguro().coerceAtLeast(0f)
                estructuraEditada = true
                indicesFranjas.forEach { idx ->
                    if (idx in franjas.indices) franjas[idx].alturaCm = alt
                }
                actualizarVista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun contarTramosDesdePaqueteActual(): Int {
        val base = if (!estructuraEditada && paqueteOriginal.isNotBlank()) paqueteOriginal else aPaquete()
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
        return if (!estructuraEditada && paqueteOriginal.isNotBlank()) {
            paqueteConDimensionesActualizadas(paqueteOriginal)
        } else {
            aPaquete()
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

    private fun resumenFranjaSeleccionada(base: String, tramoIdx: Int, franjaIdx: Int): String {
        val limpio = base.replace(" ", "")
        val idxColon = limpio.indexOf(':')
        val idxClose = limpio.lastIndexOf(']')
        if (idxColon < 0 || idxClose <= idxColon || franjaIdx !in franjas.indices) return ""
        val cuerpo = limpio.substring(idxColon + 1, idxClose)
        val franjasTop = splitTopLevelSemicolon(cuerpo)
        val token = franjasTop.getOrNull(franjaIdx) ?: return ""
        val bloqueMods = extraerBloqueModulosFranja(token) ?: return ""
        val segmentos = bloqueMods.split(Regex("""(?i)\s*;\s*p\s*;\s*""")).filter { it.isNotBlank() }
        val seg = segmentos.getOrNull(tramoIdx) ?: segmentos.firstOrNull().orEmpty()
        val mods = parsearModsSegmento(seg)
        if (mods.isEmpty()) return ""

        fun parte(tipo: Char, etiqueta: String): String {
            val filtrados = mods.filter { it.tipo == tipo }
            if (filtrados.isEmpty()) return ""
            val n = filtrados.size
            val medidas = filtrados.mapNotNull { it.medida }.distinct()
            val txtMed = if (medidas.isEmpty()) "" else {
                if (medidas.size == 1) " de ${df1(medidas[0])} cm"
                else " (${medidas.joinToString("/") { "${df1(it)} cm" }})"
            }
            val plural = if (n == 1) etiqueta else "${etiqueta}s"
            return "$n $plural$txtMed"
        }

        val tipoFranja = if (franjas[franjaIdx].esSistema) "Sistema" else "Mocheta"
        val fijoTxt = parte('f', "fijo")
        val corrTxt = parte('c', "corrediza")
        val detalle = listOf(fijoTxt, corrTxt).filter { it.isNotBlank() }.joinToString(" y ")
        return "$tipoFranja: $detalle"
    }

    private fun resumenFranjaSeleccionadaFallback(base: String, franjaIdx: Int): String {
        val limpio = base.replace(" ", "")
        val idxColon = limpio.indexOf(':')
        val idxClose = limpio.lastIndexOf(']')
        if (idxColon < 0 || idxClose <= idxColon) return ""
        val cuerpo = limpio.substring(idxColon + 1, idxClose)
        val tokens = splitTopLevelSemicolon(cuerpo)
        val token = tokens.getOrNull(franjaIdx) ?: return ""
        val tipoFranja = if (token.trim().startsWith("s", true)) "Sistema" else "Mocheta"
        return "$tipoFranja: sin detalle"
    }

    private fun anchosTramoDesdeModulos(fr: Franja): List<Float> {
        val parantes = fr.parantes.sorted()
        val limites = listOf(0) + parantes + listOf(fr.modulos.size)
        val conteos = (0 until limites.lastIndex).map { limites[it + 1] - limites[it] }
        val totalMods = conteos.sum().coerceAtLeast(1)
        val anchoUtil = (anchoCm - parantes.size * anchoParanteCm).coerceAtLeast(0f)
        return conteos.map { anchoUtil * it / totalMods }
    }

    private fun actualizarInfoSeleccion() {
        if (indiceFranjaActiva !in franjas.indices) {
            binding.tvInfoSeleccion.text = "Selecciona una franja en un tramo"
            return
        }
        runCatching {
            val fr = franjas[indiceFranjaActiva]
            val anchosTramo = if (estructuraEditada) {
                anchosTramoDesdeModulos(fr)
            } else {
                obtenerAnchosTramo(paqueteActualLectura())
            }
            val tramo = indiceTramoActivo.coerceAtLeast(0).coerceAtMost(anchosTramo.lastIndex)
            val anchoTramo = anchosTramo.getOrNull(tramo) ?: anchoCm
            val linea1 = "Tramo ${tramo + 1}  ·  Ancho ${df1(anchoTramo)} cm  ·  Alto ${df1(altoCm)} cm"

            // resumen de módulos del tramo seleccionado
            val parantes = fr.parantes.sorted()
            val limites = listOf(0) + parantes + listOf(fr.modulos.size)
            val inicioTramo = limites.getOrElse(tramo) { 0 }
            val finTramo = limites.getOrElse(tramo + 1) { fr.modulos.size }
            val modsTramo = fr.modulos.subList(inicioTramo, finTramo)
            val nF = modsTramo.count { it == TipoModulo.FIJO }
            val nC = modsTramo.count { it == TipoModulo.CORREDIZA }
            val linea2 = buildString {
                if (nF > 0) append("$nF fijo${if (nF > 1) "s" else ""}")
                if (nF > 0 && nC > 0) append("  ·  ")
                if (nC > 0) append("$nC corrediza${if (nC > 1) "s" else ""}")
            }

            val full = if (linea2.isBlank()) linea1 else "$linea1\n$linea2"
            val ssb = SpannableStringBuilder(full)
            if (linea2.isNotBlank()) {
                ssb.setSpan(
                    StyleSpan(Typeface.BOLD),
                    linea1.length + 1,
                    full.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            binding.tvInfoSeleccion.text = ssb
        }.onFailure {
            binding.tvInfoSeleccion.text = "Selecciona una franja en un tramo"
        }
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


    /** Serializa con df1() y nunca deja una franja sin módulos (mínimo: f). */
    /** Serializa usando df1(); si altura==0 no escribe "<…>" para dejarla en AUTO. */
    /** Serializa usando df1(); si altura==0 no escribe "<…>" (AUTO),
     * excepto cuando hay una única franja: en ese caso, si altura==0,
     * se fuerza a ocupar el 100% escribiendo <altoCm>. */
    private fun aPaquete(): String {
        if (franjas.isEmpty()) {
            val tipoTxt = if (tipo == TipoEnsamble.APA) "apa" else "ina"
            return "{${clase},${tipoTxt},[${df1(anchoCm)},${df1(altoCm)}:s(f)]}"
        }
        val sb = StringBuilder()
        sb.append("{")
            .append(clase).append(",")
            .append(if (tipo == TipoEnsamble.APA) "apa" else "ina")
            .append(",[")
            .append(df1(anchoCm)).append(",")
            .append(df1(altoCm)).append(":")
        val n = franjas.size

        franjas.forEachIndexed { i, f ->
            val pref = if (f.esSistema) "s" else "m"

            // asegurar al menos un módulo
            if (f.modulos.isEmpty()) f.modulos.add(TipoModulo.FIJO)
            val parantesSet = f.parantes.toSet()
            val patron = buildString {
                f.modulos.forEachIndexed { idx, mod ->
                    if (idx in parantesSet) append(";P;")
                    append(if (mod == TipoModulo.CORREDIZA) "c" else "f")
                }
            }

            sb.append(pref)

            val alt = f.alturaCm.coerceAtLeast(0f)
            val debeForzar100 = (n == 1 && alt == 0f)   // único tramo en AUTO → 100%
            when {
                debeForzar100 -> {
                    sb.append("<").append(df1(altoCm)).append(">")
                }
                alt > 0f -> {
                    sb.append("<").append(df1(alt)).append(">")
                }
                else -> {
                    // alt == 0 → AUTO: no escribimos <…>
                }
            }

            sb.append("(").append(patron).append(")")
            if (i < franjas.lastIndex) sb.append(";")
        }

        sb.append("]}")
        return sb.toString()
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
            val cuerpo = t.substring(idxColon + 1, idxBracketClose)
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
            indiceFranjaActiva = franjas.lastIndex
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

    private fun paqueteConDimensionesActualizadas(base: String): String {
        val t = base.replace(" ", "")
        if (!t.startsWith("{") || !t.endsWith("}")) return aPaquete()
        val c1 = t.indexOf(',')
        if (c1 <= 1) return aPaquete()
        val c2 = t.indexOf(',', c1 + 1)
        if (c2 <= c1 + 1) return aPaquete()
        val idxBracketOpen = t.indexOf('[', c2 + 1)
        val idxColon = t.indexOf(':', idxBracketOpen + 1)
        val idxBracketClose = t.lastIndexOf(']')
        if (idxBracketOpen < 0 || idxColon < 0 || idxBracketClose < idxColon) return aPaquete()

        val claseBase = t.substring(1, c1)
        val tipoBase = t.substring(c1 + 1, c2)
        val cuerpo = t.substring(idxColon + 1, idxBracketClose)
        return "{${claseBase},${tipoBase},[${df1(anchoCm)},${df1(altoCm)}:${cuerpo}]}"
    }

    private fun actualizarVista() {
        val paquetePreferido = if (!estructuraEditada && paqueteOriginal.isNotBlank()) {
            paqueteConDimensionesActualizadas(paqueteOriginal)
        } else {
            aPaquete()
        }

        val paqueteFallback = aPaquete()
        val aplicado = runCatching {
            binding.vistaDiseno.actualizarDesdePaquete(paquetePreferido, 0f, 0f, mochetaLateralCm)
        }.isSuccess
        if (!aplicado) {
            val aplicadoFallback = runCatching {
                binding.vistaDiseno.actualizarDesdePaquete(paqueteFallback, 0f, 0f, mochetaLateralCm)
            }.isSuccess
            if (!aplicadoFallback) {
                val tipoTxt = if (tipo == TipoEnsamble.APA) "apa" else "ina"
                val paqueteSeguro = "{nova,${tipoTxt},[${df1(anchoCm)},${df1(altoCm)}:s(f)]}"
                binding.vistaDiseno.actualizarDesdePaquete(paqueteSeguro, 0f, 0f, mochetaLateralCm)
            }
            if (!estructuraEditada) {
                paqueteOriginal = paqueteFallback
                estructuraEditada = true
            }
        }

        binding.vistaDiseno.actualizarCorteVertical(corteVerticalCm)
        // resalta si hay franja activa conocida
        binding.vistaDiseno.resaltarFranja(indiceFranjaActiva, indiceTramoActivo)
        actualizarInfoSeleccion()
        binding.vistaDiseno.invalidate()
    }

    private fun actualizarPanelCotas() {
        binding.tvTituloCotas.text = "Cotas"
        binding.contenedorCotas.removeAllViews()
        if (franjas.isEmpty()) return
        val fr = franjas.getOrNull(indiceFranjaActiva) ?: franjas.first()
        val label = TextView(this).apply {
            text = "Módulos: ${fr.modulos.size}"
        }
        binding.contenedorCotas.addView(label)
    }

    // =========================== UTILIDADES ===========================

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

    private fun limpiarDiseno() {
        estructuraEditada = true
        franjas.clear()
        indiceFranjaActiva = -1
        actualizarVista()
    }

    override fun onBackPressed() {
        val paqueteSalida = if (!estructuraEditada && paqueteOriginal.isNotBlank()) {
            paqueteConDimensionesActualizadas(paqueteOriginal)
        } else {
            aPaquete()
        }
        setResult(RESULT_OK, Intent().apply {
            putExtra(RESULT_PAQUETE, paqueteSalida)
        })
        super.onBackPressed()
    }
}
