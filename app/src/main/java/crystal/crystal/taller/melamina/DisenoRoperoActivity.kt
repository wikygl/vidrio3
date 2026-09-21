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
        binding.vistaDiseno.alTocarCota = { pedirAltoDeTrozo(it) }
        // Arrastrar una repisa o un cajón: la repisa cambia de altura; el cajón cambia su alto
        // (se lleva el canto de arriba, el de abajo queda donde apoya).
        binding.vistaDiseno.alArrastrar = { el, _, yCm -> arrastrar(el, yCm) }
        binding.vistaDiseno.alSoltarArrastre = { seleccion?.let { s -> seleccionar(RoperoGeometria.elementos(ropero).firstOrNull { it.esElMismo(s) }) } }
    }

    private fun arrastrar(el: ElementoRopero, yCm: Float) {
        val c = ropero.cuerpoEn(el.cuerpo, el.ruta) ?: return
        val hueco = RoperoGeometria.huecoDe(ropero, el.cuerpo, el.ruta) ?: return
        val piso = hueco.y0
        when (el.tipo) {
            TipoElemento.ENTREPANO -> {
                val repartidas = RoperoGeometria.alturasDeEntrepanos(ropero, c, hueco)
                val nueva = (yCm - piso).coerceIn(5f, hueco.alto - ropero.espesorCm - 5f)
                ropero = ropero.conCuerpoEn(el.cuerpo, el.ruta, c.conAlturaDeEntrepano(el.indice, redondear(nueva), repartidas))
            }
            TipoElemento.CAJON -> {
                val alto = (yCm - el.y0).coerceIn(8f, 80f)
                ropero = ropero.conCuerpoEn(el.cuerpo, el.ruta, c.conAltoDeCajon(el.indice, redondear(alto), ropero.altoCajonCm))
            }
            else -> return
        }
        binding.vistaDiseno.ropero = ropero
        seleccion = RoperoGeometria.elementos(ropero).firstOrNull { it.esElMismo(el) }
        binding.vistaDiseno.elementoResaltado = seleccion
        binding.tvInfoSeleccion.text = descripcion(seleccion)
    }

    /** Al medio centímetro: lo que se corta. */
    private fun redondear(v: Float): Float = (v * 2f).roundToInt() / 2f

    /** La celda que espera a que se toque su vecina para unirlas. */
    private var uniendo: ElementoRopero? = null

    private fun seleccionar(el: ElementoRopero?) {
        val primera = uniendo
        if (primera != null && el != null && !el.esElMismo(primera)) {
            uniendo = null
            val (unido, motivo) = RoperoUnion.unir(ropero, primera, el)
            if (unido == null) { Toast.makeText(this, motivo, Toast.LENGTH_LONG).show() }
            else {
                ropero = unido
                binding.vistaDiseno.ropero = ropero
                // Queda elegida la celda unida: la que ahora ocupa el medio de las dos.
                val xm = (minOf(primera.x0, el.x0) + maxOf(primera.x1, el.x1)) / 2f
                val ym = (minOf(primera.y0, el.y0) + maxOf(primera.y1, el.y1)) / 2f
                seleccionar(RoperoGeometria.elementoEn(ropero, xm, ym))
                return
            }
        }
        seleccion = el
        binding.vistaDiseno.elementoResaltado = el
        binding.tvInfoSeleccion.text = descripcion(el)
        armarMando(el)
    }

    private fun descripcion(el: ElementoRopero?): String {
        if (el == null) return "Toca un cajón, un casillero, el colgador, una repisa, el tubo o el maletero; el cuerpo entero, en su cota de abajo"
        val c = ropero.cuerpoEn(el.cuerpo, el.ruta) ?: return ""
        val piso = RoperoGeometria.huecoDe(ropero, el.cuerpo, el.ruta)?.y0 ?: RoperoGeometria.pisoY(ropero)
        val donde = if (el.ruta.isEmpty()) "del cuerpo ${el.cuerpo + 1}" else "de la columna ${el.ruta.last() + 1} (casillero ${el.ruta[el.ruta.size - 2] + 1}, cuerpo ${el.cuerpo + 1})"
        return when (el.tipo) {
            TipoElemento.CUERPO -> "Cuerpo ${el.cuerpo + 1}: ${c.tipo.etiqueta.lowercase()}, ${fmt(c.anchoCm)} de ancho"
            TipoElemento.CAJON -> "Cajón ${el.indice + 1} $donde: ${fmt(el.y1 - el.y0)} de alto (arranca a ${fmt(el.y0 - piso)} del piso)"
            TipoElemento.ENTREPANO -> "Repisa ${el.indice + 1} $donde: a ${fmt(el.y0 - piso)} del piso"
            TipoElemento.REPISA_MALETERO -> "Repisa del maletero: ${fmt(ropero.maleteroCm)} libres arriba"
            TipoElemento.TUBO -> "Tubo del colgador $donde: a ${fmt(ropero.tuboBajoTopeCm)} bajo el tope, ${fmt((el.y0 + el.y1) / 2f - piso)} del piso"
            TipoElemento.CASILLERO -> "Casillero ${el.indice + 1} $donde: ${fmt(el.y1 - el.y0)} de alto libre" + (if (c.columnasDe(el.indice).isNotEmpty()) ", partido en ${c.columnasDe(el.indice).size} columnas" else "")
            TipoElemento.TAPA_CAJONES -> "Tapa sobre los cajones $donde: a ${fmt(el.y0 - piso)} del piso (sube con los cajones)"
            TipoElemento.ZONA_CAJONES -> "Cajones $donde: ${c.cajonesEfectivos} en ${fmt(el.y1 - el.y0)} de alto" + (if (!c.tapaSobreCajones) ", sin tapa (unidos al casillero de encima)" else "")
            TipoElemento.COLGADOR -> "Colgador $donde: ${fmt(el.y1 - el.y0)} libres, el tubo a ${fmt(ropero.tuboBajoTopeCm)} del tope"
            TipoElemento.DIVISION_COLUMNA -> "División entre columnas del casillero ${el.ruta.last() + 1}, cuerpo ${el.cuerpo + 1}"
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

    /** Una fila de mando. Cada casilla de texto va con su rótulo encima: así se sabe qué se edita. */
    private fun fila(vararg vistas: View) = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        vistas.forEach { v ->
            val vista = if (v is EditText) LinearLayout(this@DisenoRoperoActivity).apply {
                orientation = LinearLayout.VERTICAL
                addView(TextView(this@DisenoRoperoActivity).apply { text = v.hint; textSize = 10f; setTextColor(0xFF666666.toInt()) })
                addView(v)
            } else v
            addView(vista, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        }
    }

    private fun num(et: EditText, porDefecto: Float) = et.text.toString().replace(",", ".").toFloatOrNull() ?: porDefecto

    @SuppressLint("SetTextI18n")
    private fun armarMando(el: ElementoRopero?) {
        val mando = binding.contenedorFlotante
        mando.removeAllViews()
        if (el == null) { mando.visibility = View.GONE; return }
        val c = ropero.cuerpoEn(el.cuerpo, el.ruta) ?: run { mando.visibility = View.GONE; return }
        val hueco = RoperoGeometria.huecoDe(ropero, el.cuerpo, el.ruta) ?: run { mando.visibility = View.GONE; return }
        val piso = hueco.y0
        /** El cuerpo o la columna de este elemento, cambiado. */
        fun conEste(nuevo: Cuerpo) = ropero.conCuerpoEn(el.cuerpo, el.ruta, nuevo)
        when (el.tipo) {
            TipoElemento.DIVISION_COLUMNA -> Unit
            TipoElemento.CAJON -> {
                val etAlto = campo("Alto de este cajón (cm)", fmt(el.y1 - el.y0))
                mando.addView(fila(
                    etAlto,
                    boton("Este") { aplicar(conEste(c.conAltoDeCajon(el.indice, num(etAlto, el.y1 - el.y0), ropero.altoCajonCm))) },
                    boton("Todo el cuerpo") {
                        val alto = num(etAlto, el.y1 - el.y0).coerceIn(8f, 80f)
                        aplicar(conEste(c.copy(altosCajonesCm = List(c.cajonesEfectivos) { alto })))
                    },
                    boton("Todos") {
                        val alto = num(etAlto, el.y1 - el.y0).coerceIn(8f, 80f)
                        aplicar(ropero.copy(altoCajonCm = alto, cuerpos = ropero.cuerpos.map { it.copy(altosCajonesCm = emptyList()) }))
                    }
                ))
                // El espacio de cajones del cuerpo: cuántos y si se ven desde fuera.
                val etCuantos = campo("Cajones del cuerpo", c.cajonesEfectivos.toString(), entero = true)
                val cbALaVista = CheckBox(this).apply { text = "A la vista"; textSize = 12f; isChecked = c.cajonesALaVista }
                mando.addView(fila(etCuantos, cbALaVista, boton("Poner") {
                    val cuantos = (etCuantos.text.toString().toIntOrNull() ?: c.cajonesEfectivos).coerceIn(1, 10)
                    aplicar(conEste(c.copy(cajones = cuantos, cajonesALaVista = cbALaVista.isChecked)))
                }))
            }
            TipoElemento.ENTREPANO -> {
                val etAltura = campo("Altura de esta repisa desde el piso (cm)", fmt(el.y0 - piso))
                val repartidas = RoperoGeometria.alturasDeEntrepanos(ropero, c, hueco)
                mando.addView(fila(
                    etAltura,
                    boton("Poner") { aplicar(conEste(c.conAlturaDeEntrepano(el.indice, num(etAltura, el.y0 - piso), repartidas))) },
                    boton("Repartir de nuevo") { aplicar(conEste(c.copy(alturasEntrepanosCm = emptyList()))) }
                ))
            }
            TipoElemento.CASILLERO -> {
                // El alto libre del casillero. "Poner" fija este y reparte de nuevo los de arriba
                // (los de abajo se quedan): así los de abajo salen chicos y el resto iguales.
                // "Solo esta" mueve nada más la repisa de encima (o la de abajo en el último).
                val etAlto = campo("Alto libre de este casillero (cm)", fmt(el.y1 - el.y0))
                val repartidas = RoperoGeometria.alturasDeEntrepanos(ropero, c, hueco)
                mando.addView(fila(
                    etAlto,
                    boton("Poner") { aplicar(RoperoGeometria.conAltoDeTrozo(ropero, el, num(etAlto, el.y1 - el.y0))) },
                    boton("Solo esta") {
                        val alto = num(etAlto, el.y1 - el.y0).coerceAtLeast(5f)
                        val nuevoCuerpo = if (el.indice < repartidas.size) {
                            c.conAlturaDeEntrepano(el.indice, el.y0 - piso + alto, repartidas)
                        } else if (el.indice > 0) {
                            c.conAlturaDeEntrepano(el.indice - 1, (el.y1 - alto - ropero.espesorCm - piso).coerceAtLeast(5f), repartidas)
                        } else c
                        aplicar(conEste(nuevoCuerpo))
                    },
                    boton("Repartir de nuevo") { aplicar(conEste(c.copy(alturasEntrepanosCm = emptyList()))) }
                ))
                mando.addView(filaDeRepisas(el, c))
                mando.addView(filaDeUnir(el))
                // El casillero partido en columnas, cada una un cuerpo con lo suyo.
                val etColumnas = campo("Partir en columnas (1 = sin partir)", c.columnasDe(el.indice).size.coerceAtLeast(1).toString(), entero = true)
                mando.addView(fila(etColumnas, boton("Partir") {
                    val n = (etColumnas.text.toString().toIntOrNull() ?: 1).coerceIn(1, 6)
                    aplicar(conEste(c.conCasilleroPartido(el.indice, n, el.x1 - el.x0, ropero.espesorCm)))
                }))
                if (el.ruta.isNotEmpty()) mando.addView(filaDeColumna(el, c))
            }
            TipoElemento.ZONA_CAJONES -> {
                val etAlto = campo("Alto de todos los cajones (cm)", fmt(el.y1 - el.y0))
                val etCuantos = campo("Cajones del cuerpo", c.cajonesEfectivos.toString(), entero = true)
                val cbALaVista = CheckBox(this).apply { text = "A la vista"; textSize = 12f; isChecked = c.cajonesALaVista }
                mando.addView(fila(etAlto, etCuantos, cbALaVista, boton("Poner") {
                    val cuantos = (etCuantos.text.toString().toIntOrNull() ?: c.cajonesEfectivos).coerceIn(1, 10)
                    val conCuantos = conEste(c.copy(cajones = cuantos, cajonesALaVista = cbALaVista.isChecked))
                    aplicar(RoperoGeometria.conAltoDeTrozo(conCuantos, el, num(etAlto, el.y1 - el.y0)))
                }))
                mando.addView(filaDeUnir(el))
                if (el.ruta.isNotEmpty()) mando.addView(filaDeColumna(el, c))
            }
            TipoElemento.COLGADOR -> {
                // El colgador: el tubo, y las repisas que van debajo de la ropa.
                val etTubo = campo("Tubo bajo el tope (cm)", fmt(ropero.tuboBajoTopeCm))
                mando.addView(fila(etTubo, boton("Poner") { aplicar(ropero.copy(tuboBajoTopeCm = num(etTubo, ropero.tuboBajoTopeCm).coerceIn(2f, 40f))) }))
                mando.addView(filaDeRepisas(el, c))
                mando.addView(filaDeUnir(el))
                if (el.ruta.isNotEmpty()) mando.addView(filaDeColumna(el, c))
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
                    aplicar(conEste(c.copy(altosCajonesCm = List(c.cajonesEfectivos) { alto })))
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
                val spPuertas = desplegableDePuertas(c, "Puertas: las del ropero")
                val cbPorCasillero = CheckBox(this).apply { text = "Puertas por casillero"; textSize = 12f; isChecked = c.puertasPorCasillero }
                mando.addView(fila(spTipo, etAncho, etAltoLado))
                mando.addView(fila(etRepisas, etCajones, spHojas, cbALaVista))
                mando.addView(fila(spPuertas, cbPorCasillero))
                mando.addView(fila(boton("Aplicar al cuerpo") {
                    val nuevo = c.copy(
                        tipo = tipos[spTipo.selectedItemPosition.coerceIn(0, tipos.lastIndex)],
                        entrepanos = etRepisas.text.toString().toIntOrNull() ?: 0,
                        cajones = etCajones.text.toString().toIntOrNull() ?: 0,
                        hojasBatientes = spHojas.selectedItemPosition,
                        alturasEntrepanosCm = emptyList(),
                        altoCm = num(etAltoLado, 0f).let { if (it >= 30f) it else 0f },
                        cajonesALaVista = cbALaVista.isChecked,
                        puertasPropias = puertasElegidas(spPuertas),
                        puertasPorCasillero = cbPorCasillero.isChecked
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

    /** La cota de alto de un trozo (casillero, colgador, maletero, cajones) se toca y se escribe. */
    private fun pedirAltoDeTrozo(el: ElementoRopero) {
        seleccionar(el)
        val et = campo("Alto libre (cm)", fmt(el.y1 - el.y0))
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(descripcion(el))
            .setView(fila(et))
            .setPositiveButton("Poner") { _, _ -> aplicar(RoperoGeometria.conAltoDeTrozo(ropero, el, num(et, el.y1 - el.y0))) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Cuántas repisas lleva el cuerpo o la columna (los casilleros son una más): se reparten de nuevo al cambiarlas. */
    private fun filaDeRepisas(el: ElementoRopero, c: Cuerpo): View {
        val et = campo(if (el.ruta.isEmpty()) "Repisas del cuerpo" else "Repisas de la columna", c.entrepanosEfectivos.toString(), entero = true)
        return fila(et, boton("Poner") {
            val n = (et.text.toString().toIntOrNull() ?: c.entrepanosEfectivos).coerceIn(0, 12)
            aplicar(ropero.conCuerpoEn(el.cuerpo, el.ruta, c.copy(entrepanos = n, alturasEntrepanosCm = emptyList())))
        })
    }

    /** Unir esta celda con una vecina (se toca después), y desunirla a lo ancho o a lo alto. */
    private fun filaDeUnir(el: ElementoRopero): View {
        val botones = mutableListOf<View>(boton("Unir con…") {
            uniendo = el
            binding.tvInfoSeleccion.text = "Toca la celda vecina (de la misma altura si es al lado, del mismo ancho si es encima o debajo) para unirlas"
        })
        if (el.tipo != TipoElemento.ZONA_CAJONES) {
            botones.add(boton("Desunir a lo ancho") { aplicar(RoperoUnion.desunirAncho(ropero, el)) })
            botones.add(boton("Desunir a lo alto") { aplicar(RoperoUnion.desunirAlto(ropero, el)) })
        }
        return fila(*botones.toTypedArray())
    }

    private val opcionesDePuertas = listOf(null, TipoPuertas.SIN, TipoPuertas.BATIENTES, TipoPuertas.CORREDIZAS)

    /** Las puertas propias de un cuerpo o columna: las heredadas, ninguna, batientes o corredizas. */
    private fun desplegableDePuertas(c: Cuerpo, heredadas: String) = Spinner(this).apply {
        adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, listOf(heredadas, "Sin puertas", "Batientes propias", "Corredizas propias"))
        setSelection(opcionesDePuertas.indexOf(c.puertasPropias).coerceAtLeast(0))
    }

    private fun puertasElegidas(sp: Spinner): TipoPuertas? = opcionesDePuertas[sp.selectedItemPosition.coerceIn(0, 3)]

    /**
     * La columna en que está este elemento (dentro de un casillero partido): su tipo, ancho
     * (que se fija), cajones, puertas… Lo que en un cuerpo entero se edita por su cota.
     */
    private fun filaDeColumna(el: ElementoRopero, c: Cuerpo): View {
        val caja = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val k = el.ruta[el.ruta.size - 2]
        val j = el.ruta.last()
        val rutaPadre = el.ruta.dropLast(2)
        val padre = ropero.cuerpoEn(el.cuerpo, rutaPadre) ?: return caja
        val huecoPadre = RoperoGeometria.huecoDe(ropero, el.cuerpo, rutaPadre) ?: return caja
        val casillero = RoperoGeometria.casilleros(ropero, padre, huecoPadre).getOrNull(k) ?: return caja
        val tipos = TipoCuerpo.values()
        val spTipo = Spinner(this).apply {
            adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, tipos.map { "Columna: ${it.etiqueta}" })
            setSelection(tipos.indexOf(c.tipo))
        }
        val anchoHoy = RoperoGeometria.columnasDeCasillero(ropero, padre, casillero, k).getOrNull(j)?.ancho ?: c.anchoCm
        val etAncho = campo("Ancho de la columna (cm)", fmt(anchoHoy))
        val etCajones = campo("Cajones de la columna", c.cajones.toString(), entero = true)
        val spPuertas = desplegableDePuertas(c, "Puertas: sin")
        val cbPorCasillero = CheckBox(this).apply { text = "Por casillero"; textSize = 12f; isChecked = c.puertasPorCasillero }
        caja.addView(fila(spTipo, etAncho, etCajones))
        caja.addView(fila(spPuertas, cbPorCasillero, boton("Aplicar a la columna") {
            val nueva = c.copy(
                tipo = tipos[spTipo.selectedItemPosition.coerceIn(0, tipos.lastIndex)],
                cajones = etCajones.text.toString().toIntOrNull() ?: 0,
                alturasEntrepanosCm = emptyList(),
                puertasPropias = puertasElegidas(spPuertas),
                puertasPorCasillero = cbPorCasillero.isChecked
            )
            var padreNuevo = padre.conCuerpoEn(listOf(k, j), nueva)
            val ancho = num(etAncho, anchoHoy)
            if (kotlin.math.abs(ancho - anchoHoy) > 0.05f) padreNuevo = padreNuevo.conAnchoDeColumna(k, j, ancho, casillero.ancho, ropero.espesorCm)
            aplicar(ropero.conCuerpoEn(el.cuerpo, rutaPadre, padreNuevo))
        }))
        return caja
    }

    /** Un cambio: se guarda, se redibuja y se vuelve a elegir lo mismo (si sigue existiendo). */
    private fun aplicar(nuevo: Ropero) {
        ropero = nuevo
        binding.vistaDiseno.ropero = ropero
        val s = seleccion
        seleccionar(if (s == null) null else RoperoGeometria.elementos(ropero).firstOrNull { it.esElMismo(s) })
    }

    // ==================== OPCIONES GENERALES ====================

    private fun armarOpciones() {
        val caja = binding.contenedorOpciones
        caja.removeAllViews()
        fun titulo(t: String) = TextView(this).apply {
            text = t; textSize = 12f; setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        fun desplegable(opciones: List<String>, elegida: Int) = Spinner(this).apply {
            adapter = ArrayAdapter(this@DisenoRoperoActivity, android.R.layout.simple_spinner_dropdown_item, opciones)
            setSelection(elegida.coerceIn(0, opciones.lastIndex))
        }
        val grosores = listOf(0.45f, 1f, 2f, 3f)
        fun grosor(mm: Float) = grosores.indexOfFirst { kotlin.math.abs(it - mm) < 0.01f }.coerceAtLeast(0)

        // ---- El hueco ----
        val etAncho = campo("Ancho (cm)", fmt(ropero.anchoCm))
        val etAlto = campo("Alto (cm)", fmt(ropero.altoCm))
        val etFondoCm = campo("Fondo (cm)", fmt(ropero.fondoCm))
        val etCuerpos = campo("Cuerpos", ropero.cuerpos.size.toString(), entero = true)
        // ---- Zócalo ----
        val etZocalo = campo("Zócalo (cm)", fmt(ropero.zocaloCm))
        val spZocalo = desplegable(listOf("Zócalo delante (plano de puertas)", "Zócalo bajo el piso"), if (ropero.zocaloDelante) 0 else 1)
        // ---- Maletero ----
        val etMaletero = campo("Maletero (cm, 0 = sin)", fmt(ropero.maleteroCm))
        val etMaleteroCuerpos = campo("Compartimentos (0 = como abajo)", ropero.maleteroCuerpos.toString(), entero = true)
        val spHojasMal = desplegable(listOf("Hojas maletero: las que tocan", "1 hoja", "2 hojas"), ropero.maleteroHojas)
        // ---- Cajones y colgador ----
        val etAltoCajon = campo("Alto de cajón por defecto (cm)", fmt(ropero.altoCajonCm))
        val etTubo = campo("Tubo bajo el tope (cm)", fmt(ropero.tuboBajoTopeCm))
        // ---- Puertas ----
        val puertas = TipoPuertas.values()
        val spPuertas = desplegable(puertas.map { it.etiqueta }, puertas.indexOf(ropero.puertas))
        val spPosicion = desplegable(listOf("Frontales (sobre el armazón)", "Interiores (dentro del armazón)"), if (ropero.puertasInteriores) 1 else 0)
        val spHojas = desplegable(listOf("Hojas corredizas: las que tocan", "2", "3", "4", "5", "6"), if (ropero.hojasCorredizas in 2..6) ropero.hojasCorredizas - 1 else 0)
        val spTapacantoPuertas = desplegable(grosores.map { "Tapacanto puertas ${fmt(it)} mm" }, grosor(ropero.tapacantoPuertasMm))
        // ---- Materiales ----
        val spEspesor = desplegable(listOf("Melamina 18 mm", "Melamina 15 mm"), if (ropero.espesorMm <= 15) 1 else 0)
        val fondos = listOf(3f, 5.5f, 0f)
        val spFondo = desplegable(listOf("Fondo nordex 3 mm", "Fondo MDF 5.5 mm", "Sin fondo"), if (!ropero.conFondo) 2 else if (ropero.espesorFondoMm >= 5f) 1 else 0)
        val spTapacanto = desplegable(grosores.map { "Tapacanto interior ${fmt(it)} mm" }, grosor(ropero.tapacantoGrosorMm))

        caja.addView(titulo("Hueco"))
        caja.addView(fila(etAncho, etAlto, etFondoCm, etCuerpos))
        caja.addView(titulo("Zócalo"))
        caja.addView(fila(etZocalo, spZocalo))
        caja.addView(titulo("Maletero"))
        caja.addView(fila(etMaletero, etMaleteroCuerpos, spHojasMal))
        caja.addView(titulo("Cajones y colgador"))
        caja.addView(fila(etAltoCajon, etTubo))
        caja.addView(titulo("Puertas"))
        caja.addView(fila(spPuertas, spPosicion))
        caja.addView(fila(spHojas, spTapacantoPuertas))
        caja.addView(titulo("Materiales"))
        caja.addView(fila(spEspesor, spFondo, spTapacanto))
        caja.addView(boton("Aplicar opciones") {
            val fondoElegido = fondos[spFondo.selectedItemPosition.coerceIn(0, 2)]
            var nuevo = ropero.copy(
                zocaloCm = num(etZocalo, ropero.zocaloCm).coerceIn(0f, 30f),
                zocaloDelante = spZocalo.selectedItemPosition == 0,
                maleteroCm = num(etMaletero, ropero.maleteroCm).coerceIn(0f, 120f),
                maleteroCuerpos = (etMaleteroCuerpos.text.toString().toIntOrNull() ?: 0).coerceIn(0, 8),
                maleteroHojas = spHojasMal.selectedItemPosition,
                altoCajonCm = num(etAltoCajon, ropero.altoCajonCm).coerceIn(8f, 60f),
                tuboBajoTopeCm = num(etTubo, ropero.tuboBajoTopeCm).coerceIn(2f, 40f),
                puertas = puertas[spPuertas.selectedItemPosition.coerceIn(0, puertas.lastIndex)],
                puertasInteriores = spPosicion.selectedItemPosition == 1,
                hojasCorredizas = if (spHojas.selectedItemPosition == 0) 0 else spHojas.selectedItemPosition + 1,
                tapacantoPuertasMm = grosores[spTapacantoPuertas.selectedItemPosition.coerceIn(0, grosores.lastIndex)],
                espesorMm = if (spEspesor.selectedItemPosition == 1) 15 else 18,
                conFondo = fondoElegido > 0f,
                espesorFondoMm = if (fondoElegido > 0f) fondoElegido else ropero.espesorFondoMm,
                tapacantoGrosorMm = grosores[spTapacanto.selectedItemPosition.coerceIn(0, grosores.lastIndex)]
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
                cuerpos = ropero.cuerpos.map { it.copy(altosCajonesCm = emptyList(), alturasEntrepanosCm = emptyList(), hojasBatientes = 0, altoCm = 0f, cajonesALaVista = false, anchoFijo = false, partes = emptyMap(), puertasPropias = null, puertasPorCasillero = false, tapaSobreCajones = true) },
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
