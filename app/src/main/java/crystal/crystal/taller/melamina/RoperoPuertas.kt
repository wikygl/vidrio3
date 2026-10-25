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
 * - Batientes frontales: cada cuerpo tapa su hueco y media división a cada lado (el lateral
 *   entero en las puntas), del zócalo al techo, tapando los tableros. Interiores: dentro del hueco, de
 *   cara a cara. Una hoja hasta 60 cm de luz, dos si es más ancha (o las pedidas). Gruña de 3 mm entre
 *   hojas y ninguna en los cantos del mueble (ver [conGrunaAloAncho]).
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
    /** La gruña de 2.5 mm, para cuando las hojas cortadas al milímetro no caben con la de 3. */
    const val LUZ_JUSTA_CM = 0.25f
    /** Lo que puede sobrar a lo ancho de una fila por cortar las hojas al milímetro. */
    const val TOLERANCIA_CM = 0.2f
    /** El traslape de antes de los sistemas Ducasse (a ojo); ahora [SistemaCorredizo.traslapeCm]. */
    const val MONTA_CORREDIZA_CM = 5f

    fun de(r: Ropero): PuertasDelRopero {
        val hojas = mutableListOf<Hoja>()
        val rieles = mutableListOf<Float>()
        val e = r.espesorCm
        if (r.puertas == TipoPuertas.CORREDIZAS) {
            // Las del ropero entero, por dentro del armazón.
            corredizasEn(r, Hueco(e, r.anchoCm - e, r.pisoArribaCm, r.altoCm - e), RoperoCalculo.hojasCorredizas(r), hojas, rieles)
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
        return PuertasDelRopero(conGrunaAloAncho(r, hojas), rieles)
    }

    /**
     * La gruña a lo alto (abajo, arriba). Encima del armazón, la de 3 mm entera abajo de cada hoja
     * (contra el zócalo, el frente o la hoja de debajo) y nada arriba, donde topa en la de encima o
     * en el canto del mueble (el usuario, 2026-10-12: 6.5 + 202.2 + 30.9 + 6 cantos + 2 gruñas =
     * 242; antes 0.15 arriba y abajo). Interiores, dentro de su hueco: la mitad a cada lado.
     */
    private fun grunaAloAlto(r: Ropero): Pair<Float, Float> =
        if (r.puertasInteriores) MEDIA_LUZ to MEDIA_LUZ else LUZ_CM to 0f

    /**
     * La gruña a lo ancho, por filas (hojas del mismo alto que se tocan, de un cuerpo o de varios):
     * [LUZ_CM] entre hoja y hoja y nada en los cantos del mueble, que las de encima llegan al borde
     * del lateral; contra el armazón (interiores) o contra una hoja de otro alto, media. Lo que se
     * pierde se reparte por igual entre las hojas de la fila, cada una se corta al milímetro y lo
     * que sobre, hasta [TOLERANCIA_CM], queda en las juntas; si no caben, gruña de [LUZ_JUSTA_CM].
     * El usuario, 2026-10-11: 157 con tres cuerpos de una hoja y canto de 3 mm → 51.8 = 2 y
     * 50.9 = 1 (antes 51.7 y 50.8, con 0.15 de gruña también en los cantos y 0.4 perdidos).
     * Las corredizas no pasan por aquí.
     */
    private fun conGrunaAloAncho(r: Ropero, hojas: List<Hoja>): List<Hoja> {
        val salida = hojas.toMutableList()
        val t2 = 2f * r.tapacantoPuertasCm
        val filas = hojas.indices.filter { hojas[it].clase != ClaseDePuerta.HOJA_CORREDIZA }
            .groupBy { Math.round(hojas[it].y0 * 100f) to Math.round(hojas[it].y1 * 100f) }.values
        for (fila in filas) {
            // Las tiras de hojas que se tocan, de izquierda a derecha.
            val tiras = mutableListOf<MutableList<Int>>()
            fila.sortedBy { hojas[it].x0 }.forEach { i ->
                val ultima = tiras.lastOrNull()
                if (ultima != null && kotlin.math.abs(hojas[ultima.last()].x1 - hojas[i].x0) < 0.01f) ultima.add(i) else tiras.add(mutableListOf(i))
            }
            for (tira in tiras) {
                val a = hojas[tira.first()].x0
                val b = hojas[tira.last()].x1
                val izq = if (!r.puertasInteriores && a <= 0.01f) 0f else MEDIA_LUZ
                val der = if (!r.puertasInteriores && b >= r.anchoCm - 0.01f) 0f else MEDIA_LUZ
                val m = tira.size
                // Los anchos terminados (corte al milímetro + canto) con esa gruña, y lo que sobra.
                fun intento(luz: Float, alMm: (Float) -> Float): Pair<List<Float>, Float> {
                    val cada = (izq + der + (m - 1) * luz) / m
                    val anchos = tira.map { i -> alMm(hojas[i].ancho - cada - t2) / 10f + t2 }
                    return anchos to (b - a) - anchos.sum() - izq - der - (m - 1) * luz
                }
                // El medio milímetro, siempre hacia arriba (sin el ruido de los Float, que dejaba
                // dos hojas iguales de 57.7 y 57.8); si no caben, hacia abajo.
                val redondo = { v: Float -> kotlin.math.floor(v * 10f + 0.5f + 0.001f) }
                val haciaAbajo = { v: Float -> kotlin.math.floor(v * 10f + 0.001f) }
                val (luz, prueba) = listOf(
                    LUZ_CM to intento(LUZ_CM, redondo),
                    LUZ_CM to intento(LUZ_CM, haciaAbajo),
                    LUZ_JUSTA_CM to intento(LUZ_JUSTA_CM, redondo),
                    LUZ_JUSTA_CM to intento(LUZ_JUSTA_CM, haciaAbajo)
                ).firstOrNull { (_, p) -> p.second >= -0.001f && p.second <= TOLERANCIA_CM + 0.001f }
                    ?: (LUZ_CM to intento(LUZ_CM, haciaAbajo))
                val (anchos, sobra) = prueba
                // Lo que sobra, repartido en las juntas (con una hoja sola, a sus dos lados).
                val junta = if (m > 1) luz + sobra / (m - 1) else 0f
                var x = a + izq + if (m > 1) 0f else sobra / 2f
                tira.forEachIndexed { k, i ->
                    val h = hojas[i]
                    val x0 = x
                    val x1 = x + anchos[k]
                    // El tirador, a la misma distancia del canto que tenía cerca (o al medio, tumbado).
                    val tx = when {
                        h.tumbado -> (x0 + x1) / 2f
                        h.tiradorX > (h.x0 + h.x1) / 2f -> x1 - (h.x1 - h.tiradorX)
                        else -> x0 + (h.tiradorX - h.x0)
                    }
                    salida[i] = h.copy(x0 = x0, x1 = x1, tiradorX = tx)
                    x = x1 + junta
                }
            }
        }
        return salida
    }

    /** Las puertas de un hueco (cuerpo o columna) con lo que lleve dentro, y las de sus columnas. */
    private fun puertasDelHueco(r: Ropero, c: Cuerpo, h: Hueco, tipo: TipoPuertas, hojas: MutableList<Hoja>, rieles: MutableList<Float>) {
        val conCajones = c.cajonesALaVista && c.cajonesEfectivos > 0
        if (conCajones) frentesALaVista(r, c, h).forEach { (y0, y1) ->
            val (x0, xFin) = tapaAloAncho(r, h)
            val luz = xFin - x0
            val (g0, g1) = grunaAloAlto(r)
            hojas.add(Hoja(ClaseDePuerta.FRENTE_CAJON, x0, y0 + g0, x0 + luz, y1 - g1, x0 + luz / 2f, tumbado = true))
        }
        if (c.puertasPorCasillero) {
            RoperoGeometria.casilleros(r, c, h).forEach { hk ->
                when (tipo) {
                    TipoPuertas.BATIENTES -> batientesEn(r, c, hk, ClaseDePuerta.PUERTA, tumbado = hk.alto < 40f, hojas = hojas, frontalTapaAbajo = true)
                    TipoPuertas.CORREDIZAS -> corredizasEn(r, hk, hojasCorredizasPorAncho(hk.ancho), hojas, rieles)
                    TipoPuertas.SIN -> Unit
                }
            }
        } else when (tipo) {
            TipoPuertas.BATIENTES -> {
                val (desde, hasta) = puertaBaja(r, c, h)
                batientesEn(r, c, Hueco(h.x0, h.x1, desde, hasta), ClaseDePuerta.PUERTA, tumbado = hasta - desde < 40f, hojas = hojas, frontalTapaAbajo = false, yaConTableros = true)
            }
            TipoPuertas.CORREDIZAS -> corredizasEn(r, h, hojasCorredizasPorAncho(h.ancho), hojas, rieles)
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
     * Hojas batientes sobre un hueco. Frontales tapan media división a cada lado (el lateral entero en las puntas); [yaConTableros]
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
        val (x0, xFin) = tapaAloAncho(r, h)
        val luz = xFin - x0
        // Abajo tapa medio tablero, también sobre el piso: el zócalo de delante llega a su mitad.
        val y0 = if (frontal && !yaConTableros && frontalTapaAbajo) h.y0 - e / 2f else h.y0
        val y1 = when {
            !frontal || yaConTableros -> h.y1
            clase == ClaseDePuerta.PUERTA_MALETERO -> h.y1 + e     // tapa el techo
            else -> h.y1 + e / 2f
        }
        val n = RoperoCalculo.hojasBatientes(Cuerpo(anchoCm = h.ancho, hojasBatientes = c.hojasBatientes), if (frontal) e else 0f)
        val ancho = luz / n
        for (k in 0 until n) {
            // A lo ancho, sin gruña: la pone [conGrunaAloAncho] por filas.
            val hx0 = x0 + k * ancho
            val hx1 = x0 + (k + 1) * ancho
            // El tirador junto al canto de abrir: en una hoja sola a la derecha; en dos, al medio.
            val tx = if (n == 1) hx1 - 4f else if (k == 0) hx1 - 3f else hx0 + 3f
            val (g0, g1) = grunaAloAlto(r)
            hojas.add(Hoja(clase, hx0, y0 + g0, hx1, y1 - g1, if (tumbado) (hx0 + hx1) / 2f else tx, tumbado))
        }
    }

    /**
     * Hojas corredizas dentro de un hueco, según su sistema ([SistemaCorredizo]): montadas su
     * traslape (3 cm) y con su luz arriba y abajo (D-25, 0.7 en total; D-52, 1.2). El D-52 lleva
     * riel abajo y guía arriba, del ancho del hueco; el D-25 no lleva riel (sus ranuras van en
     * piso y techo). Antes: 5 cm de traslape, 1.5 abajo y 2 arriba, dos rieles en todas.
     */
    private fun corredizasEn(r: Ropero, h: Hueco, n: Int, hojas: MutableList<Hoja>, rieles: MutableList<Float>) {
        val s = r.sistemaCorredizo
        val ancho = (h.ancho + (n - 1) * s.traslapeCm) / n
        val y0 = h.y0 + s.luzAbajoCm
        val y1 = h.y1 - s.luzArribaCm
        for (k in 0 until n) {
            val x0 = h.x0 + k * (ancho - s.traslapeCm)
            val delante = k % 2 == 1
            val tx = if (delante) x0 + 5f else x0 + ancho - 5f
            hojas.add(Hoja(ClaseDePuerta.HOJA_CORREDIZA, x0, y0, x0 + ancho, y1, tx, tumbado = false, delante = delante))
        }
        if (s == SistemaCorredizo.D52) { rieles.add(h.ancho); rieles.add(h.ancho) }
    }

    /**
     * De dónde a dónde tapa una puerta a lo ancho (sin gruña). Interior: su hueco, de cara a cara.
     * Frontal: media división a cada lado y, en las puntas, el lateral ENTERO, como tapa el techo
     * y el piso enteros: las hojas cubren todo el frente del mueble (155 con cuatro hojas:
     * 155 / 4 - 0.3 de gruña = 38.45, que canteada con 3 mm se corta a 37.85).
     */
    private fun tapaAloAncho(r: Ropero, h: Hueco): Pair<Float, Float> {
        if (r.puertasInteriores) return h.x0 to h.x1
        val e = r.espesorCm
        val izq = if (h.x0 <= e + 0.01f) 0f else h.x0 - e / 2f
        val der = if (h.x1 >= r.anchoCm - e - 0.01f) r.anchoCm else h.x1 + e / 2f
        return izq to der
    }

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
            // Abajo arranca a medio piso: el zócalo de delante tapa la mitad de abajo y la puerta
            // la de arriba, que le hace de tope y no deja ver hueco bajo la puerta.
            (if (conCajones) RoperoGeometria.topeDeCajones(r, c, h) + e / 2f else h.y0 - e / 2f) to h.y1 + e
        }
    }
    /**
     * De dónde a dónde va cada frente de cajón a la vista (sin gruña), de abajo arriba. Frontal:
     * los frentes van de medio piso (la otra mitad la tapa el zócalo de delante) a media tapa,
     * donde arranca la puerta; esos dos medios tableros se reparten a partes iguales entre todos,
     * así un casillero de 80 con cuatro cajones da cuatro frentes iguales de 81.8 / 4 = 20.45
     * (19.55 cortados). Interior: cada frente del alto de su cajón, del piso al tope de los cajones.
     */
    fun frentesALaVista(r: Ropero, c: Cuerpo, h: Hueco): List<Pair<Float, Float>> {
        val altos = RoperoGeometria.altosDeCajones(r, c, h)
        if (altos.isEmpty()) return emptyList()
        val e = r.espesorCm
        val debajo = if (r.puertasInteriores) 0f else e / 2f
        val cada = if (r.puertasInteriores) 0f else e / altos.size
        var y = h.y0 - debajo
        return altos.map { alto -> val y0 = y; y += alto + cada; y0 to y }
    }

    /**
     * Cuántas bisagras de cazoleta lleva una hoja por su alto: dos hasta un metro; más alta, tres,
     * y cuatro desde 1.60 (la regla del taller del usuario: de un metro para arriba, tres o cuatro).
     */
    fun bisagrasPorAlto(altoCm: Float): Int = when {
        altoCm <= 100f -> 2
        altoCm < 160f -> 3
        else -> 4
    }

    /** Cuántas hojas corredizas tocan por ancho: dos hasta 240, tres más allá (una por cada 120). */
    fun hojasCorredizasPorAncho(anchoCm: Float): Int = if (anchoCm <= 240f) 2 else ceil(anchoCm / 120f).toInt().coerceAtLeast(3)
}
