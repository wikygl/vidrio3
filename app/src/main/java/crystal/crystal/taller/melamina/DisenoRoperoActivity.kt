package crystal.crystal.taller.melamina

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityDisenoRoperoBinding
import kotlin.math.roundToInt

/**
 * La pantalla de diseño del ropero, como la de Nova: el mueble grande, se toca lo que se quiere
 * cambiar y abajo sale su mando. Aquí va lo fino que la calculadora no pregunta: el alto de cada
 * cajón, a qué altura va cada repisa (tocándola o arrastrándola), a cuánto va el tubo, cuántas
 * hojas lleva cada cuerpo, el grosor del tapacanto, el espesor del fondo, las hojas corredizas.
 *
 * Entra con el ropero en [EXTRA_ROPERO] (JSON) y vuelve con [RESULT_ROPERO] solo con el botón
 * de la calculadora; Atrás vuelve sin aplicar nada, como en Nova.
 */
class DisenoRoperoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisenoRoperoBinding
    private var ropero = Ropero()
    private var seleccion: ElementoRopero? = null
    private var conCotas = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisenoRoperoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Diseño del ropero"
        ropero = Ropero.desdeJson(intent.getStringExtra(EXTRA_ROPERO)) ?: Ropero()
        binding.vistaDiseno.ropero = ropero
        binding.vistaDiseno.mostrarPuertas = false
        configurarToques()
        configurarBotones()
        armarOpciones()
    }

    // ==================== EL ELEMENTO TOCADO ====================

    private fun configurarToques() {
        binding.vistaDiseno.alTocarElemento = { el -> seleccionar(el) }
        // Arrastrar una repisa o un cajón: la repisa cambia de altura; el cajón cambia su alto
        // (se lleva el canto de arriba, el de abajo queda donde apoya).
        binding.vistaDiseno.alArrastrar = { el, _, yCm -> arrastrar(el, yCm) }
        binding.vistaDiseno.alSoltarArrastre = { seleccion?.let { s -> seleccionar(RoperoGeometria.elementos(ropero).firstOrNull { it.tipo == s.tipo && it.cuerpo == s.cuerpo && it.indice == s.indice }) } }
    }

    private fun arrastrar(el: ElementoRopero, yCm: Float) {
        val c = ropero.cuerpos.getOrNull(el.cuerpo) ?: return
        val piso = RoperoGeometria.pisoY(ropero)
        when (el.tipo) {
            TipoElemento.ENTREPANO -> {
                val repartidas = RoperoGeometria.alturasDeEntrepanos(ropero, c)
                val nueva = (yCm - piso).coerceIn(5f, RoperoGeometria.topeBajo(ropero) - piso - ropero.espesorCm - 5f)
                ropero = ropero.conCuerpo(el.cuerpo, c.conAlturaDeEntrepano(el.indice, redondear(nueva), repartidas))
            }
            TipoElemento.CAJON -> {
                val alto = (yCm - el.y0).coerceIn(8f, 80f)
                ropero = ropero.conCuerpo(el.cuerpo, c.conAltoDeCajon(el.indice, redondear(alto), ropero.altoCajonCm))
            }
            else -> return
        }
        binding.vistaDiseno.ropero = ropero
        seleccion = RoperoGeometria.elementos(ropero).firstOrNull { it.tipo == el.tipo && it.cuerpo == el.cuerpo && it.indice == el.indice }
        binding.vistaDiseno.elementoResaltado = seleccion
        binding.tvInfoSeleccion.text = descripcion(seleccion)
    }

    /** Al medio centímetro: lo que se corta. */
    private fun redondear(v: Float): Float = (v * 2f).roundToInt() / 2f

    private fun seleccionar(el: ElementoRopero?) {
        seleccion = el
        binding.vistaDiseno.elementoResaltado = el
        binding.tvInfoSeleccion.text = descripcion(el)
        armarMando(el)
    }

    private fun descripcion(el: ElementoRopero?): String {
        if (el == null) return "Toca un cajón, una repisa, el tubo o un cuerpo; arrastra repisas y cajones para moverlos"
        val c = ropero.cuerpos.getOrNull(el.cuerpo) ?: return ""
        val piso = RoperoGeometria.pisoY(ropero)
        return when (el.tipo) {
            TipoElemento.CUERPO -> "Cuerpo ${el.cuerpo + 1}: ${c.tipo.etiqueta.lowercase()}, ${fmt(c.anchoCm)} de ancho"
            TipoElemento.CAJON -> "Cajón ${el.indice + 1} del cuerpo ${el.cuerpo + 1}: ${fmt(el.y1 - el.y0)} de alto (arranca a ${fmt(el.y0 - piso)} del piso)"
            TipoElemento.ENTREPANO -> "Repisa ${el.indice + 1} del cuerpo ${el.cuerpo + 1}: a ${fmt(el.y0 - piso)} del piso"
            TipoElemento.REPISA_MALETERO -> "Repisa del maletero: ${fmt(ropero.maleteroCm)} libres arriba"
            TipoElemento.TUBO -> "Tubo del colgador: a ${fmt(ropero.tuboBajoTopeCm)} bajo el tope, ${fmt(RoperoGeometria.tuboY(ropero) - piso)} del piso"
        }
    }

    // ==================== EL MANDO ====================

    private fun campo(rotulo: String, valor: String, entero: Boolean = false) = EditText(this).apply {
        hint = rotulo
        inputType = if (entero) InputType.TYPE_CLASS_NUMBER else InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        setText(valor)
        setSelectAllOnFocus(true)
        textSize = 13f
    }

    private fun boton(texto: String, accion: () -> Unit) = Button(this).apply {
        text = texto
        isAllCaps = false
        textSize = 11f
        setOnClickListener { accion() }
    }

    private fun fila(vararg vistas: View) = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        vistas.forEach { v ->
            addView(v, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        }
    }

    private fun num(et: EditText, porDefecto: Float) = et.text.toString().replace(",", ".").toFloatOrNull() ?: porDefecto

    @SuppressLint("SetTextI18n")
    private fun armarMando(el: ElementoRopero?) {
        val mando = binding.contenedorFlotante
        mando.removeAllViews()
        if (el == null) { mando.visibility = View.GONE; return }
        val c = ropero.cuerpos.getOrNull(el.cuerpo) ?: run { mando.visibility = View.GONE; return }
        val piso = RoperoGeometria.pisoY(ropero)
        when (el.tipo) {
            TipoElemento.CAJON -> {
                val etAlto = campo("Alto de este cajón (cm)", fmt(el.y1 - el.y0))
                mando.addView(fila(
                    etAlto,
                    boton("Este") { aplicar(ropero.conCuerpo(el.cuerpo, c.conAltoDeCajon(el.indice, num(etAlto, el.y1 - el.y0), ropero.altoCajonCm))) },
                    boton("Todo el cuerpo") {
                        val alto = num(etAlto, el.y1 - el.y0).coerceIn(8f, 80f)
                        aplicar(ropero.conCuerpo(el.cuerpo, c.copy(altosCajonesCm = List(c.cajonesEfectivos) { alto })))
                    },
                    boton("Todos") {
                        val alto = num(etAlto, el.y1 - el.y0).coerceIn(8f, 80f)
                        aplicar(ropero.copy(altoCajonCm = alto, cuerpos = ropero.cuerpos.map { it.copy(altosCajonesCm = emptyList()) }))
                    }
                ))
            }
            TipoElemento.ENTREPANO -> {
                val etAltura = campo("Altura de esta repisa desde el piso (cm)", fmt(el.y0 - piso))
                val repartidas = RoperoGeometria.alturasDeEntrepanos(ropero, c)
                mando.addView(fila(
                    etAltura,
                    boton("Poner") { aplicar(ropero.conCuerpo(el.cuerpo, c.conAlturaDeEntrepano(el.indice, num(etAltura, el.y0 - piso), repartidas))) },
                    boton("Repartir de nuevo") { aplicar(ropero.conCuerpo(el.cuerpo, c.copy(alturasEntrepanosCm = emptyList()))) }
                ))
            }
            TipoElemento.REPISA_MALETERO -> {
                val et = campo("Alto libre del maletero (cm)", fmt(ropero.maleteroCm))
                mando.addView(fila(et, boton("Poner") { aplicar(ropero.copy(maleteroCm = num(et, ropero.maleteroCm).coerceIn(0f, 120f))) }))
            }
            TipoElemento.TUBO -> {
                val et = campo("Tubo bajo el tope (cm)", fmt(ropero.tuboBajoTopeCm))
                mando.addView(fila(et, boton("Poner") { aplicar(ropero.copy(tuboBajoTopeCm = num(et, ropero.tuboBajoTopeCm).coerceIn(2f, 40f))) }))
            }
            TipoElemento.CUERPO -> {
                val tipos = TipoCuerpo.values()
                val spTipo = Spinner(this).apply {
                    adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, tipos.map { it.etiqueta })
                    setSelection(tipos.indexOf(c.tipo))
                }
                val etAncho = campo("Ancho (cm)", fmt(c.anchoCm))
                val etRepisas = campo("Casilleros", c.entrepanos.toString(), entero = true)
                val etCajones = campo("Cajones", c.cajones.toString(), entero = true)
                val hojas = listOf("Hojas: las que tocan", "1 hoja", "2 hojas")
                val spHojas = Spinner(this).apply {
                    adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, hojas)
                    setSelection(c.hojasBatientes.coerceIn(0, 2))
                }
                mando.addView(fila(spTipo, etAncho))
                mando.addView(fila(etRepisas, etCajones, spHojas))
                mando.addView(fila(boton("Aplicar al cuerpo") {
                    val nuevo = c.copy(
                        tipo = tipos[spTipo.selectedItemPosition.coerceIn(0, tipos.lastIndex)],
                        entrepanos = etRepisas.text.toString().toIntOrNull() ?: 0,
                        cajones = etCajones.text.toString().toIntOrNull() ?: 0,
                        hojasBatientes = spHojas.selectedItemPosition,
                        alturasEntrepanosCm = emptyList()
                    )
                    var r = ropero.conCuerpo(el.cuerpo, nuevo)
                    val ancho = num(etAncho, c.anchoCm)
                    if (kotlin.math.abs(ancho - c.anchoCm) > 0.05f) r = r.conAnchoDeCuerpo(el.cuerpo, ancho)
                    aplicar(r)
                }))
            }
        }
        mando.visibility = View.VISIBLE
    }

    /** Un cambio: se guarda, se redibuja y se vuelve a elegir lo mismo (si sigue existiendo). */
    private fun aplicar(nuevo: Ropero) {
        ropero = nuevo
        binding.vistaDiseno.ropero = ropero
        val s = seleccion
        seleccionar(if (s == null) null else RoperoGeometria.elementos(ropero).firstOrNull { it.tipo == s.tipo && it.cuerpo == s.cuerpo && it.indice == s.indice })
    }

    // ==================== OPCIONES GENERALES ====================

    private fun armarOpciones() {
        val caja = binding.contenedorOpciones
        caja.removeAllViews()
        fun rotulo(t: String) = TextView(this).apply { text = t; textSize = 12f }
        val grosores = listOf(0.45f, 1f, 2f)
        val spTapacanto = Spinner(this).apply {
            adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, grosores.map { "${fmt(it)} mm" })
            setSelection(grosores.indexOfFirst { kotlin.math.abs(it - ropero.tapacantoGrosorMm) < 0.01f }.coerceAtLeast(0))
        }
        val fondos = listOf(3f, 5.5f)
        val spFondo = Spinner(this).apply {
            adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, listOf("Nordex 3 mm", "MDF 5.5 mm"))
            setSelection(if (ropero.espesorFondoMm >= 5f) 1 else 0)
        }
        val hojas = listOf("Las que tocan", "2", "3", "4", "5", "6")
        val spHojas = Spinner(this).apply {
            adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, hojas)
            setSelection(if (ropero.hojasCorredizas in 2..6) ropero.hojasCorredizas - 1 else 0)
        }
        val etAltoCajon = campo("Alto de cajón por defecto (cm)", fmt(ropero.altoCajonCm))
        val etTubo = campo("Tubo bajo el tope (cm)", fmt(ropero.tuboBajoTopeCm))
        val etZocalo = campo("Zócalo (cm)", fmt(ropero.zocaloCm))
        val etMaletero = campo("Maletero (cm, 0 = sin)", fmt(ropero.maleteroCm))
        caja.addView(fila(rotulo("Tapacanto"), spTapacanto, rotulo("Fondo"), spFondo))
        caja.addView(fila(rotulo("Hojas corredizas"), spHojas))
        caja.addView(fila(etAltoCajon, etTubo))
        caja.addView(fila(etZocalo, etMaletero))
        caja.addView(boton("Aplicar opciones") {
            aplicar(ropero.copy(
                tapacantoGrosorMm = grosores[spTapacanto.selectedItemPosition.coerceIn(0, grosores.lastIndex)],
                espesorFondoMm = fondos[spFondo.selectedItemPosition.coerceIn(0, 1)],
                hojasCorredizas = if (spHojas.selectedItemPosition == 0) 0 else spHojas.selectedItemPosition + 1,
                altoCajonCm = num(etAltoCajon, ropero.altoCajonCm).coerceIn(8f, 60f),
                tuboBajoTopeCm = num(etTubo, ropero.tuboBajoTopeCm).coerceIn(2f, 40f),
                zocaloCm = num(etZocalo, ropero.zocaloCm).coerceIn(0f, 30f),
                maleteroCm = num(etMaletero, ropero.maleteroCm).coerceIn(0f, 120f)
            ))
            binding.panelOpciones.visibility = View.GONE
        })
    }

    // ==================== BOTONES ====================

    private fun configurarBotones() {
        binding.btnOpciones.setOnClickListener {
            val abrir = binding.panelOpciones.visibility != View.VISIBLE
            if (abrir) armarOpciones()
            binding.panelOpciones.visibility = if (abrir) View.VISIBLE else View.GONE
        }
        binding.btnVista.setOnClickListener {
            val v = binding.vistaDiseno
            when {
                !v.mostrarPuertas && !v.en3d -> { v.mostrarPuertas = true; Toast.makeText(this, "Con puertas", Toast.LENGTH_SHORT).show() }
                v.mostrarPuertas -> { v.mostrarPuertas = false; v.en3d = true; Toast.makeText(this, "En 3D", Toast.LENGTH_SHORT).show() }
                else -> { v.en3d = false; Toast.makeText(this, "Interior", Toast.LENGTH_SHORT).show() }
            }
        }
        binding.btnCotas.setOnClickListener {
            conCotas = !conCotas
            binding.vistaDiseno.conCotasDeElementos = conCotas
        }
        binding.btnEnviarCalculadora.setOnClickListener {
            setResult(RESULT_OK, Intent().apply { putExtra(RESULT_ROPERO, ropero.aJson()) })
            finish()
        }
        binding.btnLimpiarDiseno.setOnClickListener {
            // Vuelve a lo de fábrica en lo fino; el hueco y los cuerpos se quedan.
            aplicar(ropero.copy(
                cuerpos = ropero.cuerpos.map { it.copy(altosCajonesCm = emptyList(), alturasEntrepanosCm = emptyList(), hojasBatientes = 0) },
                hojasCorredizas = 0, tuboBajoTopeCm = 6f, tapacantoGrosorMm = 0.45f, espesorFondoMm = 3f
            ))
            Toast.makeText(this, "Lo fino vuelve a lo de fábrica", Toast.LENGTH_SHORT).show()
        }
        binding.vistaDiseno.conCotasDeElementos = true
    }

    /** El paquete que se mandaría a la calculadora, para las pruebas. */
    @androidx.annotation.VisibleForTesting
    fun paqueteParaPruebas(): String = ropero.aJson()

    /** Al décimo, sin el .0 de los enteros: 20.000002 sale "20", 20.05 sale "20.1". */
    private fun fmt(v: Float): String {
        val d = kotlin.math.round(v * 10f) / 10f
        return if (d == d.toInt().toFloat()) d.toInt().toString() else String.format(java.util.Locale.US, "%.1f", d)
    }

    companion object {
        const val EXTRA_ROPERO = "extra_ropero"
        const val RESULT_ROPERO = "result_ropero"
    }
}
