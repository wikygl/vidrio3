package crystal.crystal.taller.melamina

import kotlin.math.ceil

enum class ClaseDePuerta(val nombre: String) {
    PUERTA("Puerta"),
    PUERTA_MALETERO("Puerta maletero"),
    FRENTE_CAJON("Frente cajón"),
    HOJA_CORREDIZA("Hoja corrediza")
}

/**
 * Una hoja puesta en su sitio (cm desde la esquina de abajo a la izquierda del mueble), ya con
 * la gruña descontada: lo que se corta (antes de descontar el tapacanto) y lo que se pinta.
 * [tiradorX] es dónde va el tirador; [tumbado], si va horizontal (frentes, maletero).
 */
data class Hoja(
    val clase: ClaseDePuerta,
    val x0: Float,
    val y0: Float,
    val x1: Float,
    val y1: Float,
    val tiradorX: Float,
    val tumbado: Boolean,
    /** En corredizas, la hoja que corre por delante (se pinta un tono distinto). */
    val delante: Boolean = false
) {
    val ancho: Float get() = x1 - x0
    val alto: Float get() = y1 - y0
}

/** Las hojas del ropero y los rieles de las corredizas (cada largo, en cm; van de a dos). */
data class PuertasDelRopero(val hojas: List<Hoja>, val rielesCm: List<Float>)

/**
 * Dónde va cada puerta, frente de cajón y hoja corrediza del ropero: una sola cuenta para el
 * cálculo (que las corta) y el dibujo (que las pinta).
 *
 * - Batientes frontales: cada cuerpo tapa su hueco y media división a cada lado (medio lateral
 *   en las puntas), del zócalo al techo, tapando los tableros. Interiores: dentro del hueco, de
 *   cara a cara. Una hoja hasta 60 cm de luz, dos si es más ancha (o las pedidas). Gruña de 3 mm.
 * - El maletero con sus puertas aparte, por cuerpo o por compartimento si tiene los suyos.
 * - Cajones a la vista: los frentes en el plano de las puertas, y la puerta arranca sobre la tapa.
 * - Corredizas del ropero: por dentro del armazón, a todo lo ancho y alto, montadas 5 cm.
 * - Un cuerpo o una columna con puertas propias: las suyas en su hueco (batientes o corredizas);
 *   y con "puertas por casillero", cada casillero (y el colgador) con las suyas en el suyo.
 */
object RoperoPuertas {

    /** La gruña entre hojas (y contra el armazón): 3 mm en total, la mitad a cada lado de la hoja. */
    const val LUZ_CM = 0.3f
    private const val MEDIA_LUZ = LUZ_CM / 2f
    const val MONTA_CORREDIZA_CM = 5f

    fun de(r: Ropero): PuertasDelRopero {
        val hojas = mutableListOf<Hoja>()
        val rieles = mutableListOf<Float>()
        val e = r.espesorCm
        if (r.puertas == TipoPuertas.CORREDIZAS) {
            // Las del ropero entero, por dentro del armazón.
            corredizasEn(Hueco(e, r.anchoCm - e, r.zocaloCm + e, r.altoCm - e), RoperoCalculo.hojasCorredizas(r), hojas, rieles)
        } else {
            r.cuerpos.forEachIndexed { i, c ->
                val h = RoperoGeometria.huecoDeCuerpo(r, i)
                val tipo = c.puertasPropias ?: r.puertas
                puertasDelHueco(r, c, h, tipo, hojas, rieles)
                // Las del maletero, por cuerpo (si no tiene los suyos).
                if (r.maleteroCm > 0f && !r.maleteroPropio && tipo == TipoPuertas.BATIENTES) {
                    val hm = Hueco(h.x0, h.x1, h.y1 + e, RoperoGeometria.techoY(r, i))
                    batientesEn(r, c, hm, ClaseDePuerta.PUERTA_MALETERO, tumbado = true, hojas = hojas, frontalTapaAbajo = false)
                }
            }
            if (r.maleteroPropio && r.puertas == TipoPuertas.BATIENTES) {
                val tope = RoperoGeometria.topeBajo(r) + e
                RoperoGeometria.maleterosX(r).forEach { (x0, x1) ->
                    val comp = Cuerpo(anchoCm = x1 - x0, hojasBatientes = r.maleteroHojas)
                    batientesEn(r, comp, Hueco(x0, x1, tope, RoperoGeometria.techoY(r)), ClaseDePuerta.PUERTA_MALETERO, tumbado = true, hojas = hojas, frontalTapaAbajo = false)
                }
            }
        }
        return PuertasDelRopero(hojas, rieles)
    }

    /** Las puertas de un hueco (cuerpo o columna) con lo que lleve dentro, y las de sus columnas. */
    private fun puertasDelHueco(r: Ropero, c: Cuerpo, h: Hueco, tipo: TipoPuertas, hojas: MutableList<Hoja>, rieles: MutableList<Float>) {
        val frontal = !r.puertasInteriores
        val conCajones = c.cajonesALaVista && c.cajonesEfectivos > 0
        if (conCajones) frentesALaVista(r, c, h).forEach { (y0, y1) ->
            val luz = luzDelHueco(r, h)
            val x0 = if (frontal) h.x0 - r.espesorCm / 2f else h.x0
            hojas.add(Hoja(ClaseDePuerta.FRENTE_CAJON, x0 + MEDIA_LUZ, y0 + MEDIA_LUZ, x0 + luz - MEDIA_LUZ, y1 - MEDIA_LUZ, x0 + luz / 2f, tumbado = true))
        }
        if (c.puertasPorCasillero) {
            RoperoGeometria.casilleros(r, c, h).forEach { hk ->
                when (tipo) {
                    TipoPuertas.BATIENTES -> batientesEn(r, c, hk, ClaseDePuerta.PUERTA, tumbado = hk.alto < 40f, hojas = hojas, frontalTapaAbajo = true)
                    TipoPuertas.CORREDIZAS -> corredizasEn(hk, hojasCorredizasPorAncho(hk.ancho), hojas, rieles)
                    TipoPuertas.SIN -> Unit
                }
            }
        } else when (tipo) {
            TipoPuertas.BATIENTES -> {
                val (desde, hasta) = puertaBaja(r, c, h)
                batientesEn(r, c, Hueco(h.x0, h.x1, desde, hasta), ClaseDePuerta.PUERTA, tumbado = hasta - desde < 40f, hojas = hojas, frontalTapaAbajo = false, yaConTableros = true)
            }
            TipoPuertas.CORREDIZAS -> corredizasEn(h, hojasCorredizasPorAncho(h.ancho), hojas, rieles)
            TipoPuertas.SIN -> Unit
        }
        // Las columnas de los casilleros partidos, cada una con las suyas si las lleva.
        RoperoGeometria.casilleros(r, c, h).forEachIndexed { k, hk ->
            RoperoGeometria.columnasDeCasillero(r, c, hk, k).forEachIndexed { j, hj ->
                val col = c.columnasDe(k)[j]
                puertasDelHueco(r, col, hj, col.puertasPropias ?: TipoPuertas.SIN, hojas, rieles)
            }
        }
    }

    /**
     * Hojas batientes sobre un hueco. Frontales tapan media división a cada lado; [yaConTableros]
     * dice que el hueco ya viene con lo que tapa arriba y abajo (la puerta baja de un cuerpo);
     * si no, frontal tapa también medio tablero arriba y abajo ([frontalTapaAbajo]) o solo arriba
     * (el maletero, que abajo tapa la repisa entera y arriba el techo: ya viene en el hueco).
     */
    private fun batientesEn(
        r: Ropero, c: Cuerpo, h: Hueco, clase: ClaseDePuerta, tumbado: Boolean, hojas: MutableList<Hoja>,
        frontalTapaAbajo: Boolean, yaConTableros: Boolean = false
    ) {
        val e = r.espesorCm
        val frontal = !r.puertasInteriores
        val luz = luzDelHueco(r, h)
        val x0 = if (frontal) h.x0 - e / 2f else h.x0
        val y0 = if (frontal && !yaConTableros && frontalTapaAbajo) h.y0 - e / 2f else h.y0
        val y1 = when {
            !frontal || yaConTableros -> h.y1
            clase == ClaseDePuerta.PUERTA_MALETERO -> h.y1 + e     // tapa el techo
            else -> h.y1 + e / 2f
        }
        val n = RoperoCalculo.hojasBatientes(Cuerpo(anchoCm = h.ancho, hojasBatientes = c.hojasBatientes), if (frontal) e else 0f)
        val ancho = luz / n
        for (k in 0 until n) {
            val hx0 = x0 + k * ancho + MEDIA_LUZ
            val hx1 = x0 + (k + 1) * ancho - MEDIA_LUZ
            // El tirador junto al canto de abrir: en una hoja sola a la derecha; en dos, al medio.
            val tx = if (n == 1) hx1 - 4f else if (k == 0) hx1 - 3f else hx0 + 3f
            hojas.add(Hoja(clase, hx0, y0 + MEDIA_LUZ, hx1, y1 - MEDIA_LUZ, if (tumbado) (hx0 + hx1) / 2f else tx, tumbado))
        }
    }

    /** Hojas corredizas por dentro de un hueco, montadas 5 cm, con sus dos rieles. */
    private fun corredizasEn(h: Hueco, n: Int, hojas: MutableList<Hoja>, rieles: MutableList<Float>) {
        val ancho = (h.ancho + (n - 1) * MONTA_CORREDIZA_CM) / n
        val y0 = h.y0 + 1.5f
        val y1 = h.y1 - 2f
        for (k in 0 until n) {
            val x0 = h.x0 + k * (ancho - MONTA_CORREDIZA_CM)
            val delante = k % 2 == 1
            val tx = if (delante) x0 + 5f else x0 + ancho - 5f
            hojas.add(Hoja(ClaseDePuerta.HOJA_CORREDIZA, x0, y0, x0 + ancho, y1, tx, tumbado = false, delante = delante))
        }
        rieles.add(h.ancho); rieles.add(h.ancho)
    }

    /** Lo que tapa una puerta a lo ancho: su hueco, y media división a cada lado si es frontal. */
    private fun luzDelHueco(r: Ropero, h: Hueco): Float = h.ancho + (if (r.puertasInteriores) 0f else r.espesorCm)

    /**
     * De dónde a dónde va la puerta baja de un cuerpo (sin gruña). Frontal: del zócalo (o de
     * media tapa sobre los cajones a la vista) hasta la cara de arriba de la repisa del maletero
     * o del techo, tapando los tableros. Interior: de cara a cara del hueco.
     */
    fun puertaBaja(r: Ropero, c: Cuerpo, h: Hueco): Pair<Float, Float> {
        val e = r.espesorCm
        val conCajones = c.cajonesALaVista && c.cajonesEfectivos > 0
        return if (r.puertasInteriores) {
            (if (conCajones) RoperoGeometria.topeDeCajones(r, c, h) + e else h.y0) to h.y1
        } else {
            (if (conCajones) RoperoGeometria.topeDeCajones(r, c, h) + e / 2f else h.y0 - e) to h.y1 + e
        }
    }

    /**
     * De dónde a dónde va cada frente de cajón a la vista (sin gruña), de abajo arriba. Frontal:
     * el de abajo arranca en el zócalo (tapa el piso), cada uno acaba donde acaba su cajón, y el
     * de arriba llega a media tapa, donde arranca la puerta. Interior: del piso al tope de los cajones.
     */
    fun frentesALaVista(r: Ropero, c: Cuerpo, h: Hueco): List<Pair<Float, Float>> {
        val altos = RoperoGeometria.altosDeCajones(r, c, h)
        if (altos.isEmpty()) return emptyList()
        val topes = altos.runningFold(h.y0) { acc, alto -> acc + alto }
        val interior = r.puertasInteriores
        return altos.indices.map { k ->
            val y0 = if (k == 0 && !interior) h.y0 - r.espesorCm else topes[k]
            val y1 = if (k == altos.lastIndex && !interior) topes[k + 1] + r.espesorCm / 2f else topes[k + 1]
            y0 to y1
        }
    }

    fun bisagrasPorAlto(altoCm: Float): Int = when {
        altoCm <= 90f -> 2
        altoCm <= 150f -> 3
        altoCm <= 200f -> 4
        else -> 5
    }

    /** Cuántas hojas corredizas tocan por ancho: dos hasta 240, tres más allá (una por cada 120). */
    fun hojasCorredizasPorAncho(anchoCm: Float): Int = if (anchoCm <= 240f) 2 else ceil(anchoCm / 120f).toInt().coerceAtLeast(3)
}
