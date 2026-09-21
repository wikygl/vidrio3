package crystal.crystal.taller.melamina

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
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
        if (el == null) return "Toca un cajón, un casillero, una repisa, el tubo, un cuerpo o el maletero; arrastra repisas y cajones para moverlos"
        val c = ropero.cuerpos.getOrNull(el.cuerpo) ?: return ""
        val piso = RoperoGeometria.pisoY(ropero)
        return when (el.tipo) {
            TipoElemento.CUERPO -> "Cuerpo ${el.cuerpo + 1}: ${c.tipo.etiqueta.lowercase()}, ${fmt(c.anchoCm)} de ancho"
            TipoElemento.CAJON -> "Cajón ${el.indice + 1} del cuerpo ${el.cuerpo + 1}: ${fmt(el.y1 - el.y0)} de alto (arranca a ${fmt(el.y0 - piso)} del piso)"
            TipoElemento.ENTREPANO -> "Repisa ${el.indice + 1} del cuerpo ${el.cuerpo + 1}: a ${fmt(el.y0 - piso)} del piso"
            TipoElemento.REPISA_MALETERO -> "Repisa del maletero: ${fmt(ropero.maleteroCm)} libres arriba"
            TipoElemento.TUBO -> "Tubo del colgador: a ${fmt(ropero.tuboBajoTopeCm)} bajo el tope, ${fmt(RoperoGeometria.tuboY(ropero, el.cuerpo) - piso)} del piso"
            TipoElemento.CASILLERO -> "Casillero ${el.indice + 1} del cuerpo ${el.cuerpo + 1}: ${fmt(el.y1 - el.y0)} de alto libre"
            TipoElemento.TAPA_CAJONES -> "Tapa sobre los cajones del cuerpo ${el.cuerpo + 1}: a ${fmt(el.y0 - piso)} del piso (sube con los cajones)"
            TipoElemento.MALETERO -> if (ropero.maleteroPropio) "Maletero, compartimento ${el.indice + 1} de ${ropero.maleteroCuerpos}: ${fmt(el.x1 - el.x0)} de ancho, ${fmt(ropero.maleteroCm)} libres"
                                     else "Maletero sobre el cuerpo ${el.cuerpo + 1}: ${fmt(ropero.maleteroCm)} libres (0 compartimentos = sigue a los cuerpos)"
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
                // El espacio de cajones del cuerpo: cuántos y si se ven desde fuera.
                val etCuantos = campo("Cajones del cuerpo", c.cajonesEfectivos.toString(), entero = true)
                val cbALaVista = CheckBox(this).apply { text = "A la vista"; textSize = 12f; isChecked = c.cajonesALaVista }
                mando.addView(fila(etCuantos, cbALaVista, boton("Poner") {
                    val cuantos = (etCuantos.text.toString().toIntOrNull() ?: c.cajonesEfectivos).coerceIn(1, 10)
                    aplicar(ropero.conCuerpo(el.cuerpo, c.copy(cajones = cuantos, cajonesALaVista = cbALaVista.isChecked)))
                }))
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
            TipoElemento.CASILLERO -> {
                // El alto libre del casillero. "Poner" fija este y reparte de nuevo los de arriba
                // (los de abajo se quedan): así los de abajo salen chicos y el resto iguales.
                // "Solo esta" mueve nada más la repisa de encima (o la de abajo en el último).
                val etAlto = campo("Alto libre de este casillero (cm)", fmt(el.y1 - el.y0))
                val repartidas = RoperoGeometria.alturasDeEntrepanos(ropero, c)
                mando.addView(fila(
                    etAlto,
                    boton("Poner") {
                        aplicar(ropero.conCuerpo(el.cuerpo, RoperoGeometria.conAltoDeCasillero(ropero, el.cuerpo, el.indice, num(etAlto, el.y1 - el.y0))))
                    },
                    boton("Solo esta") {
                        val alto = num(etAlto, el.y1 - el.y0).coerceAtLeast(5f)
                        val nuevoCuerpo = if (el.indice < repartidas.size) {
                            c.conAlturaDeEntrepano(el.indice, el.y0 - piso + alto, repartidas)
                        } else if (el.indice > 0) {
                            c.conAlturaDeEntrepano(el.indice - 1, (el.y1 - alto - ropero.espesorCm - piso).coerceAtLeast(5f), repartidas)
                        } else c
                        aplicar(ropero.conCuerpo(el.cuerpo, nuevoCuerpo))
                    },
                    boton("Repartir de nuevo") { aplicar(ropero.conCuerpo(el.cuerpo, c.copy(alturasEntrepanosCm = emptyList()))) }
                ))
            }
            TipoElemento.REPISA_MALETERO, TipoElemento.MALETERO -> {
                val et = campo("Alto libre del maletero (cm)", fmt(ropero.maleteroCm))
                val etCuerpos = campo("Compartimentos (0 = como abajo)", ropero.maleteroCuerpos.toString(), entero = true)
                val spHojasMal = Spinner(this).apply {
                    adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, listOf("Hojas: las que tocan", "1 hoja", "2 hojas"))
                    setSelection(ropero.maleteroHojas.coerceIn(0, 2))
                }
                mando.addView(fila(et, etCuerpos, spHojasMal, boton("Poner") {
                    aplicar(ropero.copy(
                        maleteroCm = num(et, ropero.maleteroCm).coerceIn(0f, 120f),
                        maleteroCuerpos = (etCuerpos.text.toString().toIntOrNull() ?: 0).coerceIn(0, 8),
                        maleteroHojas = spHojasMal.selectedItemPosition
                    ))
                }))
            }
            TipoElemento.TAPA_CAJONES -> {
                // La tapa va donde acaban los cajones: lo que se cambia es el alto de estos.
                val et = campo("Alto de cada cajón de este cuerpo (cm)", fmt(ropero.altoCajonCm))
                mando.addView(fila(et, boton("Poner") {
                    val alto = num(et, ropero.altoCajonCm).coerceIn(8f, 80f)
                    aplicar(ropero.conCuerpo(el.cuerpo, c.copy(altosCajonesCm = List(c.cajonesEfectivos) { alto })))
                }))
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
                val etAltoLado = campo("Alto de este lado (cm)", if (c.altoCm >= 30f) fmt(c.altoCm) else "")
                val etRepisas = campo("Repisas", c.entrepanos.toString(), entero = true)
                val etCajones = campo("Cajones", c.cajones.toString(), entero = true)
                val hojas = listOf("Hojas: las que tocan", "1 hoja", "2 hojas")
                val spHojas = Spinner(this).apply {
                    adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, hojas)
                    setSelection(c.hojasBatientes.coerceIn(0, 2))
                }
                val cbALaVista = CheckBox(this).apply { text = "Cajones a la vista"; textSize = 12f; isChecked = c.cajonesALaVista }
                mando.addView(fila(spTipo, etAncho, etAltoLado))
                mando.addView(fila(etRepisas, etCajones, spHojas, cbALaVista))
                mando.addView(fila(boton("Aplicar al cuerpo") {
                    val nuevo = c.copy(
                        tipo = tipos[spTipo.selectedItemPosition.coerceIn(0, tipos.lastIndex)],
                        entrepanos = etRepisas.text.toString().toIntOrNull() ?: 0,
                        cajones = etCajones.text.toString().toIntOrNull() ?: 0,
                        hojasBatientes = spHojas.selectedItemPosition,
                        alturasEntrepanosCm = emptyList(),
                        altoCm = num(etAltoLado, 0f).let { if (it >= 30f) it else 0f },
                        cajonesALaVista = cbALaVista.isChecked
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
        // Lo de la calculadora: el hueco, los cuerpos, las puertas, la melamina, el fondo.
        val etAncho = campo("Ancho (cm)", fmt(ropero.anchoCm))
        val etAlto = campo("Alto (cm)", fmt(ropero.altoCm))
        val etFondoCm = campo("Fondo (cm)", fmt(ropero.fondoCm))
        val etCuerpos = campo("Cuerpos", ropero.cuerpos.size.toString(), entero = true)
        val puertas = TipoPuertas.values()
        val spPuertas = Spinner(this).apply {
            adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, puertas.map { it.etiqueta })
            setSelection(puertas.indexOf(ropero.puertas))
        }
        val spEspesor = Spinner(this).apply {
            adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, listOf("Melamina 18 mm", "Melamina 15 mm"))
            setSelection(if (ropero.espesorMm <= 15) 1 else 0)
        }
        val grosores = listOf(0.45f, 1f, 2f, 3f)
        val spTapacanto = Spinner(this).apply {
            adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, grosores.map { "Interior ${fmt(it)} mm" })
            setSelection(grosores.indexOfFirst { kotlin.math.abs(it - ropero.tapacantoGrosorMm) < 0.01f }.coerceAtLeast(0))
        }
        val spTapacantoPuertas = Spinner(this).apply {
            adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, grosores.map { "Puertas ${fmt(it)} mm" })
            setSelection(grosores.indexOfFirst { kotlin.math.abs(it - ropero.tapacantoPuertasMm) < 0.01f }.coerceAtLeast(0))
        }
        val etMaleteroCuerpos = campo("Compart. maletero (0 = como abajo)", ropero.maleteroCuerpos.toString(), entero = true)
        val fondos = listOf(3f, 5.5f, 0f)
        val spFondo = Spinner(this).apply {
            adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, listOf("Fondo nordex 3 mm", "Fondo MDF 5.5 mm", "Sin fondo"))
            setSelection(if (!ropero.conFondo) 2 else if (ropero.espesorFondoMm >= 5f) 1 else 0)
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
        caja.addView(fila(etAncho, etAlto, etFondoCm, etCuerpos))
        caja.addView(fila(etZocalo, etMaletero, etAltoCajon, etTubo))
        caja.addView(fila(spPuertas, spHojas))
        caja.addView(fila(spEspesor, spFondo))
        caja.addView(fila(rotulo("Tapacanto"), spTapacanto, spTapacantoPuertas))
        caja.addView(fila(etMaleteroCuerpos))
        caja.addView(boton("Aplicar opciones") {
            val fondoElegido = fondos[spFondo.selectedItemPosition.coerceIn(0, 2)]
            var nuevo = ropero.copy(
                tapacantoGrosorMm = grosores[spTapacanto.selectedItemPosition.coerceIn(0, grosores.lastIndex)],
                tapacantoPuertasMm = grosores[spTapacantoPuertas.selectedItemPosition.coerceIn(0, grosores.lastIndex)],
                maleteroCuerpos = (etMaleteroCuerpos.text.toString().toIntOrNull() ?: 0).coerceIn(0, 8),
                conFondo = fondoElegido > 0f,
                espesorFondoMm = if (fondoElegido > 0f) fondoElegido else ropero.espesorFondoMm,
                hojasCorredizas = if (spHojas.selectedItemPosition == 0) 0 else spHojas.selectedItemPosition + 1,
                altoCajonCm = num(etAltoCajon, ropero.altoCajonCm).coerceIn(8f, 60f),
                tuboBajoTopeCm = num(etTubo, ropero.tuboBajoTopeCm).coerceIn(2f, 40f),
                zocaloCm = num(etZocalo, ropero.zocaloCm).coerceIn(0f, 30f),
                maleteroCm = num(etMaletero, ropero.maleteroCm).coerceIn(0f, 120f),
                puertas = puertas[spPuertas.selectedItemPosition.coerceIn(0, puertas.lastIndex)],
                espesorMm = if (spEspesor.selectedItemPosition == 1) 15 else 18
            )
            // El hueco: cambiar el ancho reparte los cuerpos de nuevo; el alto y el fondo no.
            val ancho = num(etAncho, ropero.anchoCm).coerceAtLeast(30f)
            val alto = num(etAlto, ropero.altoCm).coerceAtLeast(30f)
            val fondo = num(etFondoCm, ropero.fondoCm).coerceAtLeast(20f)
            if (ancho != nuevo.anchoCm || alto != nuevo.altoCm || fondo != nuevo.fondoCm) nuevo = nuevo.conHueco(ancho, alto, fondo)
            val cuerpos = etCuerpos.text.toString().toIntOrNull()?.coerceIn(1, 8) ?: nuevo.cuerpos.size
            if (cuerpos != nuevo.cuerpos.size) nuevo = nuevo.conCuerposIguales(cuerpos)
            aplicar(nuevo)
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
                cuerpos = ropero.cuerpos.map { it.copy(altosCajonesCm = emptyList(), alturasEntrepanosCm = emptyList(), hojasBatientes = 0, altoCm = 0f, cajonesALaVista = false) },
                hojasCorredizas = 0, tuboBajoTopeCm = 6f, tapacantoGrosorMm = 0.45f, tapacantoPuertasMm = 3f, espesorFondoMm = 3f, maleteroCuerpos = 0, maleteroHojas = 0
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
