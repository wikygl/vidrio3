package crystal.crystal.Diseno.nova

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
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
    private val franjas: MutableList<Franja> = mutableListOf() // orden: abajo→arriba
    private var indiceFranjaActiva: Int = -1
    private var paqueteOriginal: String = "" // guardar paquete de entrada

    // ----------------- Binding -----------------
    private lateinit var binding: ActivityDisenoNovaBinding

    // ----------------- Tipos -----------------
    enum class TipoEnsamble { INA, APA }
    enum class TipoModulo { FIJO, CORREDIZA }
    data class Franja(
        var esSistema: Boolean,             // true = s ; false = m
        var alturaCm: Float = 0f,           // 0 => auto
        val modulos: MutableList<TipoModulo> = mutableListOf()
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
                binding.vistaDiseno.resaltarFranja(idx)
                actualizarVista()
            }
        }

        // Botones flotantes (columna)
        binding.btnAgregarFranja.setOnClickListener { dialogoAgregarFranja() }
        binding.btnQuitarFranja.setOnClickListener { quitarFranja() }
        binding.btnAgregarModulo.setOnClickListener { dialogoAgregarModulo() }
        binding.btnQuitarModulo.setOnClickListener { quitarUltimoModulo() }
        binding.btnMostrarOcultar.setOnClickListener { alternarColumnaBotones() }
        binding.btnMedidasRapidas.setOnClickListener { dialogoMedidasRapidas() }
        binding.btnCotasPlanos.setOnClickListener { togglePanelCotasPlanos() }
        binding.btnTipoEnsamble.setOnClickListener { alternarEnsamble() }
        binding.btnEnviarCalculadora.setOnClickListener { Toast.makeText(this, "No disponible", Toast.LENGTH_SHORT).show() }
        binding.btnLimpiarDiseno.setOnClickListener { limpiarDiseno() }
        binding.btnProductos.setOnClickListener { togglePanelProductos() }
        binding.btnAplicarMedidasRapidas.setOnClickListener { aplicarMedidasRapidas() }

        // Acciones rápidas (panel productos)
        binding.cardFranja.setOnClickListener { dialogoAgregarFranja() }
        binding.fijo.setOnClickListener { agregarModuloDirecto(TipoModulo.FIJO) }
        binding.corrediza.setOnClickListener { agregarModuloDirecto(TipoModulo.CORREDIZA) }
        binding.parante.setOnClickListener { Toast.makeText(this, "Parante no disponible", Toast.LENGTH_SHORT).show() }
        binding.cardEliminarModulo.setOnClickListener { quitarUltimoModulo() }

        // Menú inferior
        binding.botonEditar.setOnClickListener { mostrarMenuEditar() }

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
            binding.panelMedidasRapidas.visibility = View.GONE
            binding.panelProductos.visibility = View.GONE
            binding.panelCotasPlanos.visibility = View.GONE
        }
        setPanelControlesVisible(mostrar)
    }

    private fun dialogoMedidasRapidas() {
        val panel = binding.panelMedidasRapidas
        if (panel.visibility == View.VISIBLE) {
            panel.visibility = View.GONE
            return
        }
        setPanelControlesVisible(false)
        binding.panelProductos.visibility = View.GONE
        binding.panelCotasPlanos.visibility = View.GONE
        binding.etAnchoRapido.setText(df1(anchoCm))
        binding.etAltoRapido.setText(df1(altoCm))
        binding.etPuenteRapido.setText("0")
        panel.visibility = View.VISIBLE
    }

    private fun aplicarMedidasRapidas() {
        anchoCm = binding.etAnchoRapido.text.toString().aNumeroSeguro().takeIf { it > 0 } ?: anchoCm
        altoCm = binding.etAltoRapido.text.toString().aNumeroSeguro().takeIf { it > 0 } ?: altoCm
        binding.panelMedidasRapidas.visibility = View.GONE
        actualizarVista()
    }

    private fun togglePanelProductos() {
        val panel = binding.panelProductos
        if (panel.visibility == View.VISIBLE) {
            panel.visibility = View.GONE
            return
        }
        setPanelControlesVisible(false)
        binding.panelMedidasRapidas.visibility = View.GONE
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
        binding.panelMedidasRapidas.visibility = View.GONE
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
            franjas.removeLast()
            indiceFranjaActiva = franjas.lastIndex
            actualizarVista()
        }
    }

    private fun repetirModulo(fc: Char, cantidad: Int) {
        if (indiceFranjaActiva !in franjas.indices) return
        val fr = franjas[indiceFranjaActiva]
        val tipo = if (fc == 'c' || fc == 'C') TipoModulo.CORREDIZA else TipoModulo.FIJO
        repeat(cantidad) { fr.modulos.add(tipo) }
        actualizarVista()
    }

    private fun agregarModuloDirecto(tipo: TipoModulo) {
        if (indiceFranjaActiva !in franjas.indices) {
            Toast.makeText(this, "Primero agrega/selecciona una franja.", Toast.LENGTH_SHORT).show()
            return
        }
        val fr = franjas[indiceFranjaActiva]
        fr.modulos.add(tipo)
        actualizarVista()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun quitarUltimoModulo() {
        if (indiceFranjaActiva !in franjas.indices) return
        val fr = franjas[indiceFranjaActiva]
        if (fr.modulos.size <= 1) {
            Toast.makeText(this, "Debe quedar al menos un módulo.", Toast.LENGTH_SHORT).show()
            return
        }
        fr.modulos.removeLast()
        actualizarVista()
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
        fr.esSistema = !fr.esSistema
        Toast.makeText(
            this,
            if (fr.esSistema) "Franja cambiada a Sistema (S)" else "Franja cambiada a Mocheta (M)",
            Toast.LENGTH_SHORT
        ).show()
        actualizarVista()
    }

    private fun alternarEnsamble() {
        tipo = if (tipo == TipoEnsamble.APA) TipoEnsamble.INA else TipoEnsamble.APA
        actualizarVista()
    }

    private fun dialogoCambiarMedidas() {
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 8)
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

        AlertDialog.Builder(this)
            .setTitle("Cambiar medidas")
            .setView(cont)
            .setPositiveButton("Aplicar") { _, _ ->
                anchoCm = etAncho.text.toString().aNumeroSeguro().takeIf { it > 0 } ?: anchoCm
                altoCm = etAlto.text.toString().aNumeroSeguro().takeIf { it > 0 } ?: altoCm
                mochetaLateralCm = etMoch.text.toString().aNumeroSeguro().coerceAtLeast(0f)
                actualizarVista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    /** Serializa con df1() y nunca deja una franja sin módulos (mínimo: f). */
    /** Serializa usando df1(); si altura==0 no escribe "<…>" para dejarla en AUTO. */
    /** Serializa usando df1(); si altura==0 no escribe "<…>" (AUTO),
     * excepto cuando hay una única franja: en ese caso, si altura==0,
     * se fuerza a ocupar el 100% escribiendo <altoCm>. */
    private fun aPaquete(): String {
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
            val patron = f.modulos.joinToString("") { if (it == TipoModulo.CORREDIZA) "c" else "f" }

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

    private fun cargarDesdePaquete(p: String?) {
        if (p.isNullOrBlank()) return
        paqueteOriginal = p // guardar para actualizarVista()
        try {
            val t = p.replace(" ", "")
            val reCabecera = Regex("""^\{([^,]+),([^,]+),\[(-?\d+(?:[.,]\d+)?),(-?\d+(?:[.,]\d+)?):(.*)]}""")
            val m = reCabecera.find(t) ?: return
            clase = m.groupValues[1]
            tipo = if (m.groupValues[2].equals("ina", true)) TipoEnsamble.INA else TipoEnsamble.APA
            anchoCm = m.groupValues[3].aNumeroSeguro()
            altoCm  = m.groupValues[4].aNumeroSeguro()

            franjas.clear()
            val cuerpo = m.groupValues[5]
            if (cuerpo.isNotEmpty()) {
                val partes = cuerpo.split(';')
                val reSeg = Regex("""^\s*([ms])\s*<\s*(-?\d+(?:[.,]\d+)?)\s*>\s*\(\s*([fc]*)\s*\)\s*$""")
                partes.forEach { seg ->
                    val mm = reSeg.find(seg) ?: return@forEach
                    val esS = mm.groupValues[1].equals("s", true)
                    val alt = mm.groupValues[2].aNumeroSeguro()
                    val modsCrudos = mm.groupValues[3]
                    val mods = if (modsCrudos.isEmpty())
                        mutableListOf(TipoModulo.FIJO)
                    else
                        modsCrudos.map {
                            if (it == 'c' || it == 'C') TipoModulo.CORREDIZA else TipoModulo.FIJO
                        }.toMutableList()
                    franjas.add(Franja(esSistema = esS, alturaCm = alt, modulos = mods))
                }
            }
            indiceFranjaActiva = franjas.lastIndex
        } catch (_: Exception) {
            // si falla el parseo, se mantiene el estado anterior
        }
    }

    // =========================== VISTA / REDIBUJO ===========================

    private fun actualizarVista() {
        // Usar paquete original si existe, sino regenerar desde franjas
        val paquete = if (paqueteOriginal.isNotBlank()) paqueteOriginal else aPaquete()
        binding.vistaDiseno.actualizarDesdePaquete(paquete, 0f, 0f, mochetaLateralCm)
        // resalta si hay franja activa conocida
        binding.vistaDiseno.resaltarFranja(indiceFranjaActiva)
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
        franjas.clear()
        indiceFranjaActiva = -1
        actualizarVista()
    }
}
