package crystal.crystal.taller.puerta.logica

import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

object CalculosPuerta {
    const val HOJA_REF = 199f
    const val MARCO = 2.2f
    const val BASTIDOR = 8.25f
    const val UNO_MEDIO = 3.8f

    /**
     * Holgura del vidrio cuando la puerta lleva junquillo. Sin junquillo son 0.2, porque el vidrio
     * entra directo contra el aluminio y no hay dónde esconder más juego.
     */
    const val HOLGURA_VIDRIO = 0.5f

    // --------- Formateo ---------
    fun df1(valor: Float): String {
        val texto = String.format(Locale.US, "%.1f", valor)
        return when {
            texto == "-0.0" -> "0"
            texto.endsWith(".0") -> texto.dropLast(2)
            else -> texto
        }
    }

    /**
     * Dos decimales. NO se usa para mostrar nada: todo lo que se lee —materiales, rótulos del plano
     * y reglas de cotas— va con [df1]. Queda solo como clave para comparar puntos e intersecciones
     * y descartar repetidos, donde el segundo decimal es la tolerancia, no una medida.
     */
    fun df2(valor: Float): String = String.format(Locale.US, "%.2f", valor)

    // --------- Cálculos base ---------
    fun marcoSuperior(ancho: Float, marco: Float) = ancho - (2 * marco)

    fun tubo(ancho: Float, marco: Float, mocheta: Float): String =
        if (mocheta > 0f) df1(ancho - (2 * marco)) else ""

    /**
     * Ancho útil de la hoja: el vano menos los dos marcos y menos el juego que se le deja para que
     * entre y gire. Los marcos van por separado porque cuando la puerta se pega a una ventana ese
     * lado no lleva canal sino tubo.
     *
     * La holgura era un 1 fijo escondido en la fórmula. Ahora se ingresa, y está acá para que
     * ningún modelo nuevo se olvide de descontarla.
     */
    fun anchoHoja(ancho: Float, marcoIzq: Float, marcoDer: Float, holgura: Float = 1f): Float =
        ancho - (marcoIzq + marcoDer + holgura)

    fun paflon(ancho: Float, marco: Float, bastidor: Float, holgura: Float = 1f): Float =
        anchoHoja(ancho, marco, marco, holgura) - (2 * bastidor)
    fun parante(hPuente: Float, piso: Float, holgura: Float = 1f): Float =
        if (piso == 0f) hPuente - holgura else (hPuente - (holgura / 2)) - piso

    fun paranteInterno(parante: Float, nZocalo: Int, bastidor: Float): Float =
        parante - ((nZocalo + 1) * bastidor)

    fun divisiones(parante: Float, nZocalo: Int, divi2: Float, bastidor: Float): Float {
        val base = if (nZocalo > 1) parante - zocalo(nZocalo, bastidor) else parante
        val nbas = if (nZocalo > 1) divi2 * bastidor else (divi2 + 1) * bastidor
        return (base - nbas) / divi2
    }

    /**
     * Alto de cada paño: el vacío repartido entre las divisiones, descontando los perfiles que las
     * separan. Da lo mismo que [divisiones] sobre el parante completo, pero sin volver a razonar los
     * zócalos —ya vienen descontados en `paranteInterno`—, así que sirve con cualquier cantidad.
     *
     * `perfil` es el aluminio que hace de divisor: el bastidor en Mari, Adel y Jeny, y el perfil del
     * interior en Viky. Es la ÚNICA cuenta del alto de paño: cada modelo la derivaba por su lado y
     * así fue como el junquillo de Mari h terminó midiendo un bastidor menos que su propio vidrio.
     */
    fun altoPano(paranteInterno: Float, nDiv: Int, perfil: Float): Float =
        if (nDiv <= 0) 0f else ((paranteInterno - (nDiv - 1) * perfil) / nDiv).coerceAtLeast(0f)

    fun nPfvcal(divi2: Int) = divi2 + 1

    fun nPaflones(divi2: Int, nBast: Int) = if (nBast > 1) divi2 + nBast else divi2 + 1

    fun nZocalo(nBast: Int) = if (nBast == 0) 1 else nBast

    fun zocalo(nZocalo: Int, bastidor: Float): Float {
        val holgura = 0.009f
        val total = (nZocalo + holgura) * bastidor
        return df1(total).toFloat()
    }

    fun mocheta(alto: Float, hPuente: Float, marco: Float, tubo: Float = 2.5f): Float =
        alto - (hPuente + marco + tubo)

    fun hPuente(alto: Float, hHoja: Float, pisoG: Float, hojaRef: Float, marco: Float): Float {
        val piso = if (pisoG == 0f) pisoG else pisoG - 0.5f
        return when {
            hHoja == 0f -> when {
                alto > 210f && (hojaRef + piso) < alto - 5.3 -> hojaRef + piso
                alto <= 210f && alto > hojaRef -> 190f + piso
                alto <= hojaRef -> (alto - marco)
                (hojaRef + piso) > alto - 5.3 -> (alto - marco)
                else -> (alto - marco) + piso
            }
            alto <= hHoja || (hHoja + piso) > alto - 5.3 -> (alto - marco)
            else -> hHoja + piso
        }
    }

    // --------- Vidrios ---------
    fun vidrioH(paflon: Float, divisiones: Float, jun: Float): String {
        val holgura = if (jun == 0f) 0.2f else HOLGURA_VIDRIO
        val anchv = df1(paflon - holgura).toFloat()
        val altv = df1(divisiones - holgura).toFloat()
        return "${df1(anchv)} x ${df1(altv)}"
    }

    fun vidrioV(paflon: Float, bastidor: Float, divi: Int, paranteInterno: Float, jun: Float): String {
        val holgura = if (jun == 0f) 0.2f else HOLGURA_VIDRIO
        val anchv = ((paflon - (bastidor * (divi - 1))) / divi) - holgura
        val altv = paranteInterno - holgura
        return "${df1(anchv)} x ${df1(altv)}"
    }

    fun vidrioM(marcoSuperior: Float, mocheta: Float, jun: Float): String {
        val holgura = if (jun == 0f) 0.2f else HOLGURA_VIDRIO
        val uno = df1(marcoSuperior - holgura).toFloat()
        val dos = df1(mocheta - holgura).toFloat()
        return "${df1(uno)} x ${df1(dos)}"
    }

    /**
     * Los datos ingresados se devuelven TAL CUAL, sin redondear: acá no se está midiendo una pieza
     * sino repitiendo lo que se escribió. Si alguien puso 70.25, tiene que leer 70.25 y no 70.3.
     */
    fun dato(valor: Float): String =
        String.format(Locale.US, "%.2f", valor).trimEnd('0').trimEnd('.')

    fun referen(ancho: Float, alto: Float, hPuente: Float, mocheta: Float): String =
        if (mocheta > 0f) "anch ${dato(ancho)} x alt ${dato(alto)}\nAlto hoja = ${dato(hPuente)}" else "anch ${dato(ancho)} x alt ${dato(alto)}"

    // --------- Paños / ensayos ---------
    fun textoPanos(z: Float, nPanosSup: Int, tamPano: Float, bastidor: Float): String {
        val n = nPanosSup - 1
        val j = df1(tamPano).toFloat()
        val b = bastidor
        val g = j + b
        val lista = if (n in 1..17) List(n) { idx -> (z + j + (idx * (j + b))) } else listOf(((1 * g) + z) - b)
        return buildString {
            append(df1(z)); append('\n')
            append(lista.joinToString("\n") { df1(it) })
        }
    }

    // ── Cotas acumuladas (ensayo de paños) — sumas/restas EXACTAS en doble precisión ──────────
    // Sin holgura ni redondeo intermedio (el zócalo es nZ·bastidor exacto, no zocalo() que redondea).

    /**
     * Mari h: cotas verticales desde la base (zócalo + cada línea de división horizontal).
     *
     * El cuadro de la hoja —zócalos y bastidor superior— va en `bastidor` y las divisiones en
     * `interior`. Con los dos iguales da lo mismo de siempre.
     */
    fun cotasPanos(
        parante: Float, nZocalo: Int, nDiv: Int, bastidor: Float, interior: Float = bastidor
    ): List<Float> {
        if (nDiv <= 0 || parante <= 0f) return emptyList()
        val b = bastidor.toDouble()
        val i = interior.toDouble()
        val p = parante.toDouble()
        val zoc = nZocalo * b
        val div = (p - zoc - b - (nDiv - 1) * i) / nDiv
        val lista = mutableListOf(zoc)
        for (idx in 0 until nDiv) lista.add(zoc + div + idx * (div + i))
        return lista.map { it.toFloat() }
    }

    // Mari v: cotas horizontales desde la izquierda (cada divisor vertical). Sin zócalo.
    fun cotasPanosVertical(paflon: Float, nDiv: Int, bastidor: Float, interior: Float = bastidor): List<Float> {
        if (nDiv <= 0 || paflon <= 0f) return emptyList()
        val i = interior.toDouble()
        val anchoDiv = (paflon.toDouble() - (nDiv - 1) * i) / nDiv
        return (0 until nDiv).map { k -> (anchoDiv + k * (anchoDiv + i)).toFloat() }
    }

    fun textoPanosVertical(paflon: Float, nDiv: Int, bastidor: Float): String =
        cotasPanosVertical(paflon, nDiv, bastidor).joinToString("\n") { df1(it) }

    // Mari d: dónde cruza cada diagonal el bastidor. izq/der = alturas (desde la base de la hoja)
    // en el parante izquierdo y derecho; sup/inf = posiciones horizontales (desde el borde interior
    // izquierdo) en el paflon superior e inferior. Misma geometría rotada que el dibujo de Mari d.
    data class CrucesMariD(
        val izq: List<Float>, val der: List<Float>, val sup: List<Float>, val inf: List<Float>
    )

    fun cotasPanosDiagonal(
        paflon: Float, paranteInterno: Float, nZocalo: Int, nDiv: Int, bastidor: Float, angulo: Float,
        // Perfil de las barras diagonales; el zócalo sigue siendo del bastidor.
        interior: Float = bastidor
    ): CrucesMariD {
        val nBarras = maxOf(0, nDiv - 1)
        if (nBarras == 0 || paflon <= 0f || paranteInterno <= 0f)
            return CrucesMariD(emptyList(), emptyList(), emptyList(), emptyList())
        val base = nZocalo * bastidor   // desde la base de la hoja hasta el fondo del interior
        val barras = barrasRotadasMariD(paflon, paranteInterno, nDiv, interior, angulo)
        val izq = mutableListOf<Float>(); val der = mutableListOf<Float>()
        val sup = mutableListOf<Float>(); val inf = mutableListOf<Float>()
        for (barra in barras) {
            for (arista in listOf(barra.arista1, barra.arista2)) {
                for (p in arista.puntos) {
                    when (p.lado) {
                        'L' -> izq.add(base + (paranteInterno - p.y))   // altura desde la base
                        'R' -> der.add(base + (paranteInterno - p.y))
                        'T' -> sup.add(p.x)                             // posición horizontal (interior)
                        'B' -> inf.add(p.x)
                    }
                }
            }
        }
        return CrucesMariD(izq.sorted(), der.sorted(), sup.sorted(), inf.sorted())
    }

    fun textoPaflonesMariD(
        paflon: Float,
        paranteInterno: Float,
        nDiv: Int,
        bastidor: Float,
        angulo: Float
    ): String {
        val nBarras = maxOf(0, nDiv - 1)
        if (nBarras == 0 || paflon <= 0f || paranteInterno <= 0f) return ""

        val anguloRad = Math.toRadians(angulo.toDouble())
        val extraCorte = abs((bastidor * tan(anguloRad)).toFloat())
        val espesorParaUbicar = bastidor
        val alcance = alcancePerpendicularRotado(paflon, paranteInterno, angulo)
        val espacio = ((alcance * 2f) - (nBarras * espesorParaUbicar)) / (nBarras + 1)
        val inicio = (paranteInterno / 2f) - alcance

        val medidas = List(nBarras) { indice ->
            val y1 = inicio + espacio + indice * (espesorParaUbicar + espacio)
            val y2 = y1 + espesorParaUbicar
            val arista1 = aristaRotada(paflon, paranteInterno, y1, angulo)
            val arista2 = aristaRotada(paflon, paranteInterno, y2, angulo)
            val longitud1 = longitudPaflonMariD(arista1, paflon, bastidor, angulo)
            val longitud2 = longitudPaflonMariD(arista2, paflon, bastidor, angulo)
            val compensacionEsquina1 = compensacionEsquinaPaflonMariD(arista1, paflon, bastidor, angulo)
            val compensacionEsquina2 = compensacionEsquinaPaflonMariD(arista2, paflon, bastidor, angulo)

            when {
                arista1.esRecortadaEnTapaCon(arista2) -> df1(max(longitud1, longitud2))
                arista1.tocaTapa() && arista2.cruzaAncho() && compensacionEsquina1 > 0f -> {
                    df1(arista1.longitud + extraCorte + compensacionEsquina1)
                }
                arista2.tocaTapa() && arista1.cruzaAncho() && compensacionEsquina2 > 0f -> {
                    df1(arista2.longitud + extraCorte + compensacionEsquina2)
                }
                else -> df1(((longitud1 + longitud2) / 2f) + extraCorte)
            }
        }

        return medidas
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.key.toFloat() }
            .joinToString("\n") { "${it.key} = ${it.value}" }
    }

    private fun longitudPaflonMariD(
        arista: AristaRotada,
        ancho: Float,
        bastidor: Float,
        angulo: Float
    ): Float {
        if (!arista.cruzaDosTapas()) return arista.longitud

        return arista.longitud + compensacionEsquinaPaflonMariD(arista, ancho, bastidor, angulo)
    }

    private fun compensacionEsquinaPaflonMariD(
        arista: AristaRotada,
        ancho: Float,
        bastidor: Float,
        angulo: Float
    ): Float {
        val distanciaEsquina = arista.puntos
            .filter { it.lado == 'T' || it.lado == 'B' }
            .flatMap { punto -> listOf(punto.x, ancho - punto.x) }
            .filter { it > 0.001f && it <= bastidor }
            .minOrNull()
            ?: return 0f

        val rad = Math.toRadians(angulo.toDouble())
        return distanciaEsquina * abs(cos(rad).toFloat())
    }

    fun textoJunkillosMariD(
        paflon: Float,
        paranteInterno: Float,
        nDiv: Int,
        bastidor: Float,
        junki: Float,
        angulo: Float
    ): String {
        val nBarras = maxOf(0, nDiv - 1)
        if (nBarras == 0 || paflon <= 0f || paranteInterno <= 0f || junki < 0f) return ""

        val geometria = GeometriaMariD(
            ancho = paflon,
            alto = paranteInterno,
            angulo = angulo,
            barras = barrasRotadasMariD(paflon, paranteInterno, nDiv, bastidor, angulo)
        )
        val piezas = piezasJunkilloMariD(geometria, junki)
        return agruparJunkillosMariD(piezas)
    }

    private data class GeometriaMariD(
        val ancho: Float,
        val alto: Float,
        val angulo: Float,
        val barras: List<BarraRotada>
    )

    private data class PiezaJunkilloMariD(
        val largo: Float,
        val orden: Int,
        val grupo: Int
    )

    private fun piezasJunkilloMariD(
        geometria: GeometriaMariD,
        junki: Float
    ): List<PiezaJunkilloMariD> {
        return piezasHorizontalesMariD(geometria, junki) +
            piezasDiagonalesMariD(geometria, junki) +
            piezasVerticalesMariD(geometria, junki)
    }

    private fun piezasHorizontalesMariD(
        geometria: GeometriaMariD,
        junki: Float
    ): List<PiezaJunkilloMariD> {
        return listOf('T', 'B').flatMap { lado ->
            segmentosBordeMariD(lado, geometria).mapNotNull { segmento ->
                val largo = segmento.fin - segmento.inicio
                if (largo <= 0.05f) return@mapNotNull null

                val avanzaContraDiagonal = debeAvanzarHorizontalContraDiagonalMariD(segmento, geometria)
                val ajuste = if (avanzaContraDiagonal) avanceHorizontalContraDiagonalMariD(junki, geometria.angulo) else 0f
                PiezaJunkilloMariD(
                    largo = largo + ajuste,
                    orden = 0,
                    grupo = if (avanzaContraDiagonal) 0 else 1
                )
            }
        }
    }

    private fun piezasDiagonalesMariD(
        geometria: GeometriaMariD,
        junki: Float
    ): List<PiezaJunkilloMariD> {
        return geometria.barras.flatMap { barra ->
            listOf(
                PiezaJunkilloMariD(
                    largo = longitudDiagonalJunkilloMariD(barra.arista1, esPrimeraArista = true, junki, geometria.angulo),
                    orden = 1,
                    grupo = 0
                ),
                PiezaJunkilloMariD(
                    largo = longitudDiagonalJunkilloMariD(barra.arista2, esPrimeraArista = false, junki, geometria.angulo),
                    orden = 1,
                    grupo = 0
                )
            )
        }
    }

    private fun piezasVerticalesMariD(
        geometria: GeometriaMariD,
        junki: Float
    ): List<PiezaJunkilloMariD> {
        return listOf('L', 'R').flatMap { lado ->
            segmentosBordeMariD(lado, geometria).mapNotNull { segmento ->
                val inicioDiagonal = segmento.inicio > 0.05f
                val finDiagonal = segmento.fin < geometria.alto - 0.05f
                val entreDiagonales = inicioDiagonal && finDiagonal

                val largo = if (junki == 0f) {
                    segmento.fin - segmento.inicio
                } else when {
                    !inicioDiagonal && !finDiagonal -> {
                        (segmento.fin - segmento.inicio - (2f * junki)).coerceAtLeast(0f)
                    }
                    entreDiagonales -> {
                        longitudTramoVerticalEntreDiagonalesMariD(
                            inicio = segmento.inicio,
                            fin = segmento.fin,
                            junki = junki,
                            angulo = geometria.angulo
                        ) ?: 0f
                    }
                    else -> {
                        longitudTramoVerticalMariD(
                            lado = lado,
                            inicio = segmento.inicio,
                            fin = segmento.fin,
                            cortaContraDiagonalArriba = inicioDiagonal,
                            cortaContraDiagonalAbajo = finDiagonal,
                            junki = junki,
                            angulo = geometria.angulo
                        ) ?: 0f
                    }
                }

                PiezaJunkilloMariD(
                    largo = largo,
                    orden = 2,
                    grupo = if (entreDiagonales) 1 else 0
                )
            }
        }
    }

    private fun agruparJunkillosMariD(piezas: List<PiezaJunkilloMariD>): String {
        val acumulado = linkedMapOf<String, Int>()

        piezas
            .sortedWith(compareBy<PiezaJunkilloMariD> { it.orden }.thenBy { it.grupo }.thenBy { it.largo })
            .forEach { pieza ->
                if (pieza.largo <= 0.05f) return@forEach
                val clave = df1(pieza.largo)
                acumulado[clave] = (acumulado[clave] ?: 0) + 1
            }
        return acumulado.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    private data class BarraRotada(
        val arista1: AristaRotada,
        val arista2: AristaRotada,
        val linea1: Float,
        val linea2: Float
    )

    private data class SegmentoBorde(
        val lado: Char,
        val inicio: Float,
        val fin: Float,
        val barraInicio: Int?,
        val barraFin: Int?
    )

    private data class CorteBorde(val posicion: Float, val barra: Int?)

    private fun segmentosBordeMariD(
        lado: Char,
        geometria: GeometriaMariD
    ): List<SegmentoBorde> {
        val largoLado = if (lado == 'T' || lado == 'B') geometria.ancho else geometria.alto
        val cortes = geometria.barras.flatMapIndexed { indice, barra ->
            listOf(barra.arista1, barra.arista2)
                .flatMap { it.puntos }
                .filter { it.lado == lado }
                .map { punto ->
                    CorteBorde(
                        posicion = if (lado == 'T' || lado == 'B') punto.x else punto.y,
                        barra = indice
                    )
                }
        }
            .filter { it.posicion in 0f..largoLado }
            .let { listOf(CorteBorde(0f, null)) + it + listOf(CorteBorde(largoLado, null)) }
            .distinctBy { df2(it.posicion) }
            .sortedBy { it.posicion }

        return cortes.zipWithNext().mapNotNull { (a, b) ->
            if (b.posicion <= a.posicion + 0.05f) return@mapNotNull null

            val medio = (a.posicion + b.posicion) / 2f
            val punto = puntoEnBordeMariD(lado, medio, geometria.ancho, geometria.alto)
            if (puntoDentroDeAlgunaBarra(punto.x, punto.y, geometria)) {
                null
            } else {
                SegmentoBorde(lado, a.posicion, b.posicion, a.barra, b.barra)
            }
        }
    }

    private fun puntoEnBordeMariD(lado: Char, posicion: Float, ancho: Float, alto: Float): PuntoRotado {
        return when (lado) {
            'T' -> PuntoRotado(posicion, 0f, lado)
            'B' -> PuntoRotado(posicion, alto, lado)
            'L' -> PuntoRotado(0f, posicion, lado)
            else -> PuntoRotado(ancho, posicion, lado)
        }
    }

    private fun avanceHorizontalContraDiagonalMariD(junki: Float, angulo: Float): Float {
        val rad = Math.toRadians(angulo.toDouble())
        val t = abs(tan(rad).toFloat())
        return if (t <= 0.0001f) 0f else junki / t
    }

    private fun debeAvanzarHorizontalContraDiagonalMariD(
        segmento: SegmentoBorde,
        geometria: GeometriaMariD
    ): Boolean {
        if (segmento.lado != 'T' && segmento.lado != 'B') return false

        val tocaIzquierda = segmento.inicio <= 0.05f
        val tocaDerecha = segmento.fin >= geometria.ancho - 0.05f
        val tocaDiagonal = segmento.inicio > 0.05f || segmento.fin < geometria.ancho - 0.05f
        if (!tocaDiagonal) return false

        val sinAng = sin(Math.toRadians(geometria.angulo.toDouble())).toFloat()
        val tocaEsquinaDeApertura = if (sinAng >= 0f) {
            (segmento.lado == 'T' && tocaIzquierda) || (segmento.lado == 'B' && tocaDerecha)
        } else {
            (segmento.lado == 'T' && tocaDerecha) || (segmento.lado == 'B' && tocaIzquierda)
        }
        val diagonalMasVertical = abs(tan(Math.toRadians(geometria.angulo.toDouble())).toFloat()) > 1f
        val largo = segmento.fin - segmento.inicio
        val tocaEsquina = tocaIzquierda || tocaDerecha
        val esTramoDeBordeQueAbre = tocaEsquina &&
            largo < (geometria.ancho / 2f) &&
            !tocaAristaExteriorMariD(segmento, geometria)
        val esTramoInterno = !tocaIzquierda && !tocaDerecha
        return tocaEsquinaDeApertura ||
            esTramoDeBordeQueAbre ||
            (esTramoInterno && diagonalMasVertical)
    }

    private fun tocaAristaExteriorMariD(segmento: SegmentoBorde, geometria: GeometriaMariD): Boolean {
        if (geometria.barras.size <= 1) return false
        val barraDiagonal = segmento.barraInicio ?: segmento.barraFin ?: return false
        return barraDiagonal == 0 || barraDiagonal == geometria.barras.lastIndex
    }

    private fun puntoDentroDeAlgunaBarra(
        x: Float,
        y: Float,
        geometria: GeometriaMariD
    ): Boolean {
        val rad = Math.toRadians(geometria.angulo.toDouble())
        val sinAng = sin(rad).toFloat()
        val cosAng = cos(rad).toFloat()
        val proyeccion = (geometria.alto / 2f) +
            (((geometria.ancho / 2f) - x) * sinAng) +
            ((y - (geometria.alto / 2f)) * cosAng)

        return geometria.barras.any { barra ->
            proyeccion in (minOf(barra.linea1, barra.linea2) - 0.05f)..(maxOf(barra.linea1, barra.linea2) + 0.05f)
        }
    }

    private fun barrasRotadasMariD(
        paflon: Float,
        paranteInterno: Float,
        nDiv: Int,
        bastidor: Float,
        angulo: Float
    ): List<BarraRotada> {
        val nBarras = maxOf(0, nDiv - 1)
        val alcance = alcancePerpendicularRotado(paflon, paranteInterno, angulo)
        val espacio = ((alcance * 2f) - (nBarras * bastidor)) / (nBarras + 1)
        val inicio = (paranteInterno / 2f) - alcance

        return List(nBarras) { indice ->
            val y1 = inicio + espacio + indice * (bastidor + espacio)
            val y2 = y1 + bastidor
            BarraRotada(
                arista1 = aristaRotada(paflon, paranteInterno, y1, angulo),
                arista2 = aristaRotada(paflon, paranteInterno, y2, angulo),
                linea1 = y1,
                linea2 = y2
            )
        }
    }

    private fun longitudTramoVerticalMariD(
        lado: Char,
        inicio: Float,
        fin: Float,
        cortaContraDiagonalArriba: Boolean,
        cortaContraDiagonalAbajo: Boolean,
        junki: Float,
        angulo: Float
    ): Float? {
        if (fin <= inicio + 0.05f) return null

        val desde = inicio + if (cortaContraDiagonalArriba) {
            retiroCorteVerticalContraDiagonalMariD(lado, tocaDiagonalAbajo = false, junki, angulo)
        } else {
            0f
        }
        val hasta = fin - if (cortaContraDiagonalAbajo) {
            retiroCorteVerticalContraDiagonalMariD(lado, tocaDiagonalAbajo = true, junki, angulo)
        } else {
            0f
        }
        val usaCorteCorto = (cortaContraDiagonalArriba && !esRetiroVerticalLargoMariD(lado, tocaDiagonalAbajo = false, angulo)) ||
            (cortaContraDiagonalAbajo && !esRetiroVerticalLargoMariD(lado, tocaDiagonalAbajo = true, angulo))
        return (hasta - desde + ajusteCentecimaAnguloAbierto(angulo, usaCorteCorto)).coerceAtLeast(0f)
    }

    private fun longitudTramoVerticalEntreDiagonalesMariD(
        inicio: Float,
        fin: Float,
        junki: Float,
        angulo: Float
    ): Float? {
        if (fin <= inicio + 0.05f) return null

        val rad = Math.toRadians(angulo.toDouble())
        val cosAng = abs(cos(rad).toFloat())
        val tanAng = abs(tan(rad).toFloat())
        val diagonalJunkillo = if (cosAng <= 0.0001f) junki else junki / cosAng
        val ladoApertura = junki * tanAng
        val descuento = (2f * diagonalJunkillo) - ladoApertura
        return (fin - inicio - descuento + ajusteCentecimaAnguloAbierto(angulo, true)).coerceAtLeast(0f)
    }

    private fun retiroCorteVerticalContraDiagonalMariD(
        lado: Char,
        tocaDiagonalAbajo: Boolean,
        junki: Float,
        angulo: Float
    ): Float {
        val corteLargo = esRetiroVerticalLargoMariD(lado, tocaDiagonalAbajo, angulo)
        val cosAng = abs(cos(Math.toRadians(angulo.toDouble())).toFloat())
        val base = if (cosAng <= 0.0001f) junki else junki / cosAng
        return if (corteLargo) {
            base + junki
        } else {
            base + avanceCorteVerticalCortoMariD(junki, angulo)
        }
    }

    private fun esRetiroVerticalLargoMariD(
        lado: Char,
        tocaDiagonalAbajo: Boolean,
        angulo: Float
    ): Boolean {
        val rad = Math.toRadians(angulo.toDouble())
        val sinAng = sin(rad).toFloat()
        return if (sinAng >= 0f) {
            (lado == 'L' && !tocaDiagonalAbajo) || (lado == 'R' && tocaDiagonalAbajo)
        } else {
            (lado == 'L' && tocaDiagonalAbajo) || (lado == 'R' && !tocaDiagonalAbajo)
        }
    }

    private fun avanceCorteVerticalCortoMariD(junki: Float, angulo: Float): Float {
        val rad = Math.toRadians(angulo.toDouble())
        val avance = junki * (1f - abs(tan(rad).toFloat()))
        if (abs(avance) <= 0.0001f) return 0f

        return if (avance > 0f) avance - 0.005f else avance
    }

    private fun extraDiagonalJunkillo(junki: Float, angulo: Float): Float {
        val rad = Math.toRadians(angulo.toDouble())
        return abs((junki * tan(rad)).toFloat())
    }

    private fun longitudDiagonalJunkilloMariD(
        arista: AristaRotada,
        esPrimeraArista: Boolean,
        junki: Float,
        angulo: Float
    ): Float {
        if (junki == 0f) return arista.longitud

        return when {
            arista.cruzaDosTapas() -> arista.longitud - descuentoDiagonalEntreHorizontales(junki, angulo)
            arista.tocaTapa() -> {
                val descuento = descuentoContraHorizontal(junki, angulo)
                if (diagonalAbreContraHorizontalMariD(arista, esPrimeraArista, angulo)) {
                    arista.longitud + descuento +
                        compensacionRetornosHorizontalesMariD(junki, angulo) +
                        ajusteCentecimaAnguloAbierto(angulo, true)
                } else {
                    arista.longitud - (descuento - ajusteCentecimaAnguloAbierto(angulo, true))
                }
            }
            arista.cruzaAncho() -> arista.longitud + extraDiagonalJunkillo(junki, angulo) +
                ajusteCentecimaAnguloAbierto(angulo, true)
            else -> arista.longitud
        }
    }

    private fun diagonalAbreContraHorizontalMariD(
        arista: AristaRotada,
        esPrimeraArista: Boolean,
        angulo: Float
    ): Boolean {
        val sinAng = sin(Math.toRadians(angulo.toDouble())).toFloat()
        val tocaTapaSuperior = 'T' in arista.lados
        val tocaTapaInferior = 'B' in arista.lados
        return if (sinAng >= 0f) {
            (tocaTapaSuperior && !esPrimeraArista) || (tocaTapaInferior && esPrimeraArista)
        } else {
            (tocaTapaSuperior && esPrimeraArista) || (tocaTapaInferior && !esPrimeraArista)
        }
    }

    private fun compensacionRetornosHorizontalesMariD(junki: Float, angulo: Float): Float {
        val retorno = avanceHorizontalContraDiagonalMariD(junki, angulo)
        return if (retorno < (junki / 2f)) 4f * retorno else 0f
    }

    private fun ajusteCentecimaAnguloAbierto(angulo: Float, aplica: Boolean): Float {
        if (!aplica) return 0f
        val t = abs(tan(Math.toRadians(angulo.toDouble())).toFloat())
        return if (t > 1f) 0.005f else 0f
    }

    private fun descuentoContraHorizontal(junki: Float, angulo: Float): Float {
        val rad = Math.toRadians(angulo.toDouble())
        val s = abs(sin(rad).toFloat())
        return if (s <= 0.0001f) junki else junki / s
    }

    private fun descuentoDiagonalEntreHorizontales(junki: Float, angulo: Float): Float {
        val rad = Math.toRadians(angulo.toDouble())
        val s = abs(sin(rad).toFloat())
        val t = abs(tan(rad).toFloat())
        val contraHorizontales = if (s <= 0.0001f) 2f * junki else (2f * junki) / s
        val retornoEsquina = if (t <= 0.0001f) 0f else junki / t
        return contraHorizontales - retornoEsquina - 0.005f
    }

    private fun alcancePerpendicularRotado(ancho: Float, alto: Float, angulo: Float): Float {
        val rad = Math.toRadians(angulo.toDouble())
        return ((ancho * abs(sin(rad).toFloat())) + (alto * abs(cos(rad).toFloat()))) / 2f
    }

    private fun longitudAristaRotada(ancho: Float, alto: Float, y: Float, angulo: Float): Float {
        return aristaRotada(ancho, alto, y, angulo).longitud
    }

    private data class PuntoRotado(val x: Float, val y: Float, val lado: Char)

    private data class AristaRotada(val longitud: Float, val puntos: List<PuntoRotado>) {
        val lados: Set<Char> = puntos.map { it.lado }.toSet()

        fun cruzaAncho(): Boolean = 'L' in lados && 'R' in lados
        fun cruzaDosTapas(): Boolean = 'T' in lados && 'B' in lados
        fun tocaTapa(): Boolean = 'T' in lados || 'B' in lados
        fun tocaBastidor(): Boolean = puntos.size >= 2

        private fun tapas(): Set<Char> = lados.filter { it == 'T' || it == 'B' }.toSet()

        fun esRecortadaEnTapaCon(otra: AristaRotada): Boolean {
            return !cruzaAncho() && !otra.cruzaAncho() && tapas().intersect(otra.tapas()).isNotEmpty()
        }
    }

    private fun aristaRotada(ancho: Float, alto: Float, y: Float, angulo: Float): AristaRotada {
        val rad = Math.toRadians(angulo.toDouble())
        val sinAng = sin(rad).toFloat()
        val cosAng = cos(rad).toFloat()
        val cx = ancho / 2f
        val cy = alto / 2f
        val d = y - cy
        val puntos = mutableListOf<PuntoRotado>()

        if (abs(cosAng) > 0.0001f) {
            val yIzq = cy + (d - sinAng * cx) / cosAng
            if (yIzq in 0f..alto) puntos += PuntoRotado(0f, yIzq, 'L')

            val yDer = cy + (d + sinAng * (ancho - cx)) / cosAng
            if (yDer in 0f..alto) puntos += PuntoRotado(ancho, yDer, 'R')
        }

        if (abs(sinAng) > 0.0001f) {
            val xSup = cx - (d + cosAng * cy) / sinAng
            if (xSup in 0f..ancho) puntos += PuntoRotado(xSup, 0f, 'T')

            val xInf = cx - (d - cosAng * (alto - cy)) / sinAng
            if (xInf in 0f..ancho) puntos += PuntoRotado(xInf, alto, 'B')
        }

        val unicos = puntos.distinctBy { "${df2(it.x)}:${df2(it.y)}:${it.lado}" }
        if (unicos.size < 2) return AristaRotada(0f, emptyList())

        val p1 = unicos[0]
        val p2 = unicos[1]
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        return AristaRotada(
            longitud = sqrt(dx * dx + dy * dy),
            puntos = listOf(p1, p2)
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Dora: junquillos y vidrios
    //  Geometría: 3 columnas de ancho colW. La izquierda es una rejilla de nSec
    //  secciones (separadas por inox de 2.5). Centro y derecha llevan una cuña de
    //  dos paflones a ±45° cuyo vértice está en el borde IZQUIERDO de la columna
    //  centro, a media altura; los brazos suben/bajan hacia la derecha. El divisor
    //  vertical entre centro y derecha corta los brazos, dejando 3 vidrios por
    //  columna: trapecio superior, trapecio inferior y la pieza media (triángulo en
    //  el centro, trapecio en la derecha). Todo a 45° → cada tramo diagonal por
    //  columna mide colW·√2.
    // ─────────────────────────────────────────────────────────────────────────
    fun textoJunkillosDora(
        paflon: Float,
        alturaZona: Float,
        bastidor: Float,
        junki: Float,
        nInoxIzq: Int,
        mocheta: Float,
        marcoSup: Float
    ): String {
        if (paflon <= 0f || alturaZona <= 0f) return ""
        val colW = (paflon - 2f * bastidor) / 3f
        if (colW <= 0f) return ""
        val raiz2 = sqrt(2f)
        val diagBase = colW * raiz2            // lado del triángulo sin junki → 14.9
        val franja = bastidor * raiz2 / 2f     // media franja diagonal (bastidor·√2 / 2 ≈ 5.83)
        val jp = junki * raiz2                 // proyección del junki a 45°
        val hDiag = alturaZona - 2f * bastidor // zona de diagonales (enmarcada arriba/abajo)
        val h2 = hDiag / 2f
        // La ley de Mari d: primero horizontales, luego diagonales, al final verticales.
        val orden = linkedMapOf<String, Int>()
        fun add(largo: Float, n: Int) {
            if (largo <= 0.05f || n <= 0) return
            val k = df1(largo)
            orden[k] = (orden[k] ?: 0) + n
        }

        // ── Horizontales ──────────────────────────────────────────────────────
        add(colW, 2)            // izquierda (1 vidrio detrás de los inox)
        add(colW, 4)            // topes/bases de los trapecios de centro + derecha

        // ── Diagonales (a 45°) ────────────────────────────────────────────────
        // 6 caras llevan +junki de remate. Las 2 caras internas del triángulo
        // central nacen en el vértice a 90°: una queda en su valor original
        // (colW·√2 = 14.9, no necesita junki) y a la otra se le descuenta un junki
        // porque ambas diagonales chocan en el vértice y no pueden solaparse (13.9).
        add(diagBase + junki, 6)    // 15.9
        add(diagBase, 1)            // 14.9
        add(diagBase - junki, 1)    // 13.9

        // ── Verticales ────────────────────────────────────────────────────────
        // Centro/derecha: arriba/abajo (×2) en las 4 aristas; las aristas derechas
        // de cada columna llevan un junki extra de remate.
        val xs = listOf(0f, colW, colW + bastidor, 2f * colW + bastidor)
        xs.forEachIndexed { i, x ->
            val extra = if (i % 2 == 1) junki else 0f
            add(h2 - x - franja + extra, 2)
        }
        // Centro/derecha: piezas del medio (×1) entre las dos diagonales.
        // En el lado angosto del trapecio los ángulos se abren (obtusos): el junquillo
        // necesita +2·junki para alcanzar las diagonales que se separan.
        add(2f * colW - 2f * jp, 1)                            // 18.2 base del triángulo (ángulos 45° agudos)
        add(2f * (colW + bastidor) - 2f * jp + 2f * junki, 1)  // 36.7 lado angosto del trapecio (obtuso → +2 junki)
        add(2f * (2f * colW + bastidor) - 2f * jp, 1)          // 55.8 lado ancho del trapecio
        // Izquierda: 1 vidrio, alto completo
        add(alturaZona - 2f * junki, 2)

        // ── Mocheta ───────────────────────────────────────────────────────────
        if (mocheta > 0f) {
            add(marcoSup, 2)
            add(mocheta - 2f * junki, 2)
        }

        return orden.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    fun textoVidriosDora(
        paflon: Float,
        alturaZona: Float,
        bastidor: Float,
        junki: Float,
        nInoxIzq: Int,
        mocheta: Float,
        marcoSup: Float
    ): String {
        if (paflon <= 0f || alturaZona <= 0f) return ""
        val colW = (paflon - 2f * bastidor) / 3f
        if (colW <= 0f) return ""
        val raiz2 = sqrt(2f)
        val franja = bastidor * raiz2 / 2f     // media franja diagonal (≈ 5.83)
        val hDiag = alturaZona - 2f * bastidor // zona de diagonales (enmarcada arriba/abajo)
        val h2 = hDiag / 2f
        val b = bastidor
        val holgura = if (junki == 0f) 0.2f else HOLGURA_VIDRIO
        val ancho = colW - holgura             // todas las columnas miden colW de ancho
        val orden = linkedMapOf<String, Int>()
        // Cada vidrio se lista como caja (bounding box): ancho × alto. El vidrio NO usa junquillos:
        // se mide directo de la geometría (a diferencia del cálculo de junquillos).
        fun pieza(alto: Float, n: Int) {
            if (ancho <= 0.05f || alto <= 0.05f || n <= 0) return
            val k = "${df1(ancho)} x ${df1(alto)}"
            orden[k] = (orden[k] ?: 0) + n
        }

        // Izquierda: 1 vidrio rectangular (detrás de los inox), alto completo
        pieza(alturaZona - holgura, 1)
        // Centro: 2 trapecios (lado largo, junto al vértice).
        pieza(h2 - franja + 2f * junki, 2)
        // Centro: triángulo (base entre las dos diagonales, en el divisor).
        pieza(2f * colW - holgura, 1)
        // Derecha: 2 trapecios (lado largo, junto al bastidor).
        pieza(h2 - (colW + b) - franja + 2f * junki, 2)
        // Derecha: trapecio medio (lado ancho, junto al bastidor).
        pieza(2f * (2f * colW + b) - holgura, 1)
        // Mocheta
        if (mocheta > 0f && marcoSup - holgura > 0.05f && mocheta - holgura > 0.05f) {
            val k = "${df1(marcoSup - holgura)} x ${df1(mocheta - holgura)}"
            orden[k] = (orden[k] ?: 0) + 1
        }

        return orden.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    // --------- Viky ---------
    // Diseño como Mari h pero con un paflon vertical (de bastidor a bastidor) que divide el interior:
    // una columna conserva las divisiones horizontales (cuadrados perfectos) y la otra es una sola
    // pieza alta. El lado del cuadrado es la división exacta; el ancho de la pieza alta es el resto.

    // El INTERIOR del bastidor puede llevar otro aluminio (un tubo más delgado, por ejemplo). Los
    // divisores —el vertical y los horizontales— usan ese perfil; el marco de la hoja siempre el
    // bastidor. Por omisión los dos son 8.25 y todo queda como cuando solo se usaba paflón.

    // Lado del cuadrado (= división horizontal exacta, sin holguras ni redondeos).
    fun ladoCuadradoViky(parante: Float, nZocalo: Int, nDiv: Int, bastidor: Float, interior: Float = bastidor): Float =
        if (nDiv <= 0) 0f
        else (parante - (nZocalo + 1) * bastidor - (nDiv - 1).coerceAtLeast(0) * interior) / nDiv

    /** Marco de la hoja: los parantes y los horizontales de ancho completo (bastidor + zócalos). */
    fun textoPaflonViky(paflon: Float, parante: Float, nZocalo: Int, nDiv: Int, bastidor: Float): String {
        if (nDiv <= 0) return "${df1(parante)} = 2"
        return "${df1(parante)} = 2\n${df1(paflon)} = ${nZocalo + 1}"
    }

    /** Piezas de adentro: el divisor vertical y los divisores de la columna de cuadrados. */
    fun textoInteriorViky(
        parante: Float, nZocalo: Int, nDiv: Int, bastidor: Float, interior: Float = bastidor
    ): String {
        if (nDiv <= 0) return ""
        val sq = ladoCuadradoViky(parante, nZocalo, nDiv, bastidor, interior)
        val zoneH = paranteInterno(parante, nZocalo, bastidor)
        val nInternos = (nDiv - 1).coerceAtLeast(0)
        return buildString {
            append("${df1(zoneH)} = 1")                            // divisor vertical, de bastidor a bastidor
            if (nInternos > 0) append("\n${df1(sq)} = $nInternos") // divisores de la columna de cuadrados
        }
    }

    fun textoJunkillosViky(
        paflon: Float, parante: Float, nZocalo: Int, nDiv: Int,
        bastidor: Float, jun: Float, mocheta: Float, marcoSup: Float, interior: Float = bastidor
    ): String {
        if (nDiv <= 0) return ""
        val sq = ladoCuadradoViky(parante, nZocalo, nDiv, bastidor, interior)
        val tallW = paflon - sq - interior
        val zoneH = paranteInterno(parante, nZocalo, bastidor)
        return buildString {
            // Cuadrados: par horizontal a tope (sq) + par vertical descontando 2 junquillos.
            append("${df1(sq)} = ${nDiv * 2}\n")
            append("${df1(sq - 2 * jun)} = ${nDiv * 2}\n")
            // Pieza alta: par horizontal (tallW) + par vertical (alto de zona) descontando 2 junquillos.
            append("${df1(tallW)} = 2\n")
            append("${df1(zoneH - 2 * jun)} = 2")
            if (mocheta > 0f) {
                append("\n${df1(marcoSup)} = 2\n${df1(mocheta - 2 * jun)} = 2")
            }
        }
    }

    fun textoVidriosViky(
        paflon: Float, parante: Float, nZocalo: Int, nDiv: Int,
        bastidor: Float, jun: Float, mocheta: Float, marcoSup: Float, interior: Float = bastidor
    ): String {
        if (nDiv <= 0) return ""
        val sq = ladoCuadradoViky(parante, nZocalo, nDiv, bastidor, interior)
        val tallW = paflon - sq - interior
        val zoneH = paranteInterno(parante, nZocalo, bastidor)
        val holg = if (jun == 0f) 0.2f else HOLGURA_VIDRIO
        return buildString {
            append("${df1(sq - holg)} x ${df1(sq - holg)} = $nDiv\n")
            append("${df1(tallW - holg)} x ${df1(zoneH - holg)} = 1")
            if (mocheta > 0f) append("\n${vidrioM(marcoSup, mocheta, jun)} = 1")
        }
    }

    // --------- Jeny ---------
    // El cuerpo se divide en paños como Mari h —bastidor de paflón, junquillo y vidrio por paño— y
    // encima del vidrio de cada paño va una rejilla de 2.5 que no toca el cálculo del vacío: va
    // montada por delante. Por eso la rejilla se lista aparte y el resto sale de las fórmulas de
    // Mari h.

    /**
     * Junquillos de Jeny: por cada paño, el par acostado a tope y el par de pie descontando los dos
     * junquillos. Con junquillo en 0 el alto es el del paño tal cual.
     *
     * No se reusa la fórmula de Mari h: esa reparte el alto a partir de `parante - bastidor`, un
     * arrastre suyo que aquí daba 82.5 donde el paño mide 86.6.
     */
    fun textoJunkillosJeny(
        paflon: Float, altoPano: Float, nDiv: Int, jun: Float, mocheta: Float, marcoSup: Float
    ): String {
        if (nDiv <= 0 || paflon <= 0f || altoPano <= 0f) return ""
        return buildString {
            append("${df1(altoPano - 2f * jun)} = ${nDiv * 2}\n")
            append("${df1(paflon)} = ${nDiv * 2}")
            if (mocheta > 0f) {
                append("\n${df1(marcoSup)} = 2\n${df1(mocheta - 2f * jun)} = 2")
            }
        }
    }

    /** Ancho de cada columna de la rejilla dentro del paño. */
    fun anchoCeldaJeny(paflon: Float, cols: Int, rejilla: Float = REJILLA_JENY): Float =
        if (cols <= 0) 0f else ((paflon - (cols - 1) * rejilla) / cols).coerceAtLeast(0f)

    /**
     * Alturas de las piezas ACOSTADAS de la rejilla, acumuladas desde la base de la hoja y repartidas
     * en dos juegos, porque no todas las columnas las llevan a la misma altura y en una sola regla se
     * pisarían: el primero va en el parante izquierdo y el segundo en el derecho.
     *
     *  - "Jeny": todas las columnas iguales, así que el segundo juego va vacío.
     *  - "Jeny c": izquierda las de las columnas impares, derecha las de las pares.
     *  - "Jeny r": izquierda el arranque y el final del recuadro, derecha los amarres del costado.
     *
     * Todo se repite en cada paño, con el paño y su recuadro recalculados para ese tramo.
     */
    fun cotasAcostadasJeny(
        variante: String, parante: Float, nZocalo: Int, nDiv: Int, filas: Int,
        bastidor: Float, rejilla: Float = REJILLA_JENY
    ): Pair<List<Float>, List<Float>> {
        val paranteInterno = paranteInterno(parante, nZocalo, bastidor)
        if (nDiv <= 0 || paranteInterno <= 0f) return emptyList<Float>() to emptyList()
        val altoPano = altoPano(paranteInterno, nDiv, bastidor)
        val zocalo = nZocalo * bastidor

        // Alturas dentro de un paño, medidas desde su base: n piezas repartidas en n+1 tramos.
        fun repartidas(n: Int): List<Float> {
            if (n <= 0) return emptyList()
            val celda = (altoPano - n * rejilla) / (n + 1)
            return (0 until n).map { f -> (f + 1) * celda + f * rejilla }
        }

        val (enPanoIzq, enPanoDer) = when (variante) {
            "Jeny r" -> {
                val altoRec = altoRecuadroJenyR(altoPano)
                val margen = (altoPano - altoRec) / 2f
                listOf(margen, margen + altoRec) to
                    listOf(margen + altoRec * 0.3f, margen + altoRec * 0.7f)
            }
            "Jeny c" -> repartidas(filas - 1) to repartidas(filas - 2)
            else -> repartidas(filas - 1) to emptyList()
        }

        fun absolutas(enPano: List<Float>): List<Float> =
            (0 until nDiv).flatMap { i ->
                val base = zocalo + i * (altoPano + bastidor)
                enPano.map { base + it }
            }

        return absolutas(enPanoIzq) to absolutas(enPanoDer)
    }

    /**
     * Cotas horizontales del plano de Jeny, repartidas en dos reglas: la primera se mide desde el
     * parante izquierdo y la segunda desde el derecho.
     *
     * En "Jeny r" va una a cada lado —el arranque del recuadro y el tubo de pie que lo amarra al
     * bastidor—, porque juntas se amontonan contra el cero de la regla vertical y el otro costado
     * queda vacío. Da igual desde qué parante se midan: el recuadro va centrado.
     *
     * En las otras variantes la regla izquierda lleva el arranque de cada pieza de pie y el ancho
     * interior, que cruzan todo el vacío y no se juntan con nada.
     */
    fun cotasRejillaJeny(
        variante: String, paflon: Float, cols: Int, rejilla: Float = REJILLA_JENY
    ): Pair<List<Float>, List<Float>> {
        if (paflon <= 0f) return emptyList<Float>() to emptyList()
        if (variante == "Jeny r") {
            val margen = (paflon - anchoRecuadroJenyR(paflon)) / 2f
            val ejeTubo = (paflon - rejilla) / 2f
            return listOf(margen) to listOf(ejeTubo)
        }
        val celda = anchoCeldaJeny(paflon, cols, rejilla)
        val barras = (0 until (cols - 1).coerceAtLeast(0)).map { c ->
            (c + 1) * celda + c * rejilla
        }
        return (barras + paflon) to emptyList()
    }

    // Recuadro suspendido de "Jeny r": cinco octavos del ancho del paño y siete décimos del alto.
    // Es la proporción del diseño, fija por ahora; de ella salen también los amarres, que miden
    // justo lo que sobra a cada lado.
    const val ANCHO_RECUADRO_JENY = 0.625f
    const val ALTO_RECUADRO_JENY = 0.7f

    fun anchoRecuadroJenyR(paflon: Float): Float = (paflon * ANCHO_RECUADRO_JENY).coerceAtLeast(0f)
    fun altoRecuadroJenyR(altoPano: Float): Float = (altoPano * ALTO_RECUADRO_JENY).coerceAtLeast(0f)

    /**
     * Dónde se amarra cada tubo al recuadro, medido desde su esquina INFERIOR IZQUIERDA hasta el
     * canto del tubo: los dos del costado hacia arriba y el de pie hacia la derecha. Es la referencia
     * con la que se marca el recuadro ya armado, que es una pieza suelta hasta que se monta.
     *
     * Al lado se le restan los tubos que lleva y lo que queda se reparte en tramos iguales: con un
     * tubo al medio de 30.1 son (30.1 - 2.5) / 2 = 13.8; con dos tubos en 60.6, (60.6 - 5) / 3 =
     * 18.55 el primero y 18.55 + 2.5 + 18.55 = 39.6 el segundo.
     */
    data class AmarresJenyR(val costadoBajo: Float, val costadoAlto: Float, val dePie: Float)

    fun amarresJenyR(paflon: Float, altoPano: Float, rejilla: Float = REJILLA_JENY): AmarresJenyR {
        val alto = altoRecuadroJenyR(altoPano)
        val ancho = anchoRecuadroJenyR(paflon)
        val tramo = ((alto - 2f * rejilla) / 3f).coerceAtLeast(0f)
        return AmarresJenyR(
            costadoBajo = tramo,
            costadoAlto = (2f * tramo) + rejilla,
            dePie = ((ancho - rejilla) / 2f).coerceAtLeast(0f)
        )
    }

    /**
     * Piezas de la rejilla, para todos los paños juntos.
     *
     * Las de pie corren enteras a lo alto del paño y las acostadas entran entre ellas, del ancho de
     * su columna. Lo que cambia por variante es cuántas acostadas lleva cada columna:
     *  - "Jeny": cuadrícula pareja, (filas - 1) en todas.
     *  - "Jeny c": intercaladas — (filas - 1) en las columnas impares y una menos en las pares, que
     *    quedan corridas a media altura entre las otras.
     *  - "Jeny r": no lleva cuadrícula sino el recuadro suspendido y sus seis amarres. Ahí las
     *    columnas y las filas no intervienen: el recuadro es proporción fija del paño.
     */
    fun textoRejillaJeny(
        variante: String, paflon: Float, altoPano: Float, nDiv: Int, cols: Int, filas: Int,
        rejilla: Float = REJILLA_JENY
    ): String {
        if (nDiv <= 0 || paflon <= 0f || altoPano <= 0f) return ""
        val nPanos = nDiv
        if (variante == "Jeny r") return textoRecuadroJenyR(paflon, altoPano, nPanos)
        val dePie = (cols - 1).coerceAtLeast(0)
        val porImpar = (filas - 1).coerceAtLeast(0)
        val porPar = when (variante) {
            "Jeny c" -> (filas - 2).coerceAtLeast(0)
            else -> porImpar
        }
        val impares = (cols + 1) / 2
        val pares = cols / 2
        val acostadas = impares * porImpar + pares * porPar

        val acumulado = linkedMapOf<String, Int>()
        fun sumar(medida: Float, cantidad: Int) {
            if (cantidad <= 0) return
            val clave = df1(medida)
            acumulado[clave] = (acumulado[clave] ?: 0) + cantidad
        }
        sumar(altoPano, dePie * nPanos)
        sumar(anchoCeldaJeny(paflon, cols, rejilla), acostadas * nPanos)
        return acumulado.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    /**
     * Recuadro suspendido de "Jeny r", por paño: los cuatro lados del marco y los seis amarres —dos
     * de pie y cuatro acostados— que miden justo lo que sobra entre el recuadro y el bastidor.
     *
     * Los cuatro lados van ENTEROS, sin descontar el perfil del lado contrario: este marco se corta
     * a 45° y las piezas se encuentran en la esquina. Descontarlas —como sí corresponde en el
     * bastidor de la hoja, donde los horizontales entran entre los parantes— dejaría el hueco del
     * aluminio a la vista.
     */
    private fun textoRecuadroJenyR(paflon: Float, altoPano: Float, nPanos: Int): String {
        val anchoRecuadro = anchoRecuadroJenyR(paflon)
        val altoRecuadro = altoRecuadroJenyR(altoPano)
        val amarreAcostado = ((paflon - anchoRecuadro) / 2f).coerceAtLeast(0f)
        val amarreDePie = ((altoPano - altoRecuadro) / 2f).coerceAtLeast(0f)

        val acumulado = linkedMapOf<String, Int>()
        fun sumar(medida: Float, cantidad: Int) {
            if (cantidad <= 0 || medida <= 0f) return
            val clave = df1(medida)
            acumulado[clave] = (acumulado[clave] ?: 0) + cantidad
        }
        sumar(altoRecuadro, 2 * nPanos)
        sumar(anchoRecuadro, 2 * nPanos)
        sumar(amarreDePie, 2 * nPanos)
        sumar(amarreAcostado, 4 * nPanos)
        return acumulado.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    // --------- Mili ---------
    // Molinete: dos tubos verticales y dos horizontales, girados 180° entre sí, que parten el vacío
    // en tres columnas y seis filas. El vacío queda en cinco paños: dos altos a los costados, dos
    // anchos arriba y abajo, y uno al medio.

    /** Tubo interno de Mili. El bastidor sigue siendo paflón; adentro va este perfil. */
    const val TUBO_MILI = 3.8f

    /** Perfil de la rejilla de Jeny, que va montada sobre el vidrio: 2.5 es el máximo que entra. */
    const val REJILLA_JENY = 2.5f

    /** Tubo con el que Taly reemplaza a los parantes de paflón cuando el vidrio no queda en rango. */
    const val TUBO_TALY = 3.8f

    // --------- Taly ---------
    // El vidrio no ocupa el paflón entero: se le suman parantes de a pares desde los costados hasta
    // que el vacío que queda entra en rango. El ancho de vidrio tiene que quedar entre 9 y 18: por
    // debajo de 9 es una tira inútil y por encima de 18 el paño queda demasiado abierto.
    //
    // Con parantes de paflón el vacío baja de a 16.5, un salto grueso que a veces cae fuera de esa
    // ventana. Cuando pasa, se corrige con UN SOLO par de tubos de 3.8 —los de adentro, los que van
    // pegados al vidrio—; el resto de los parantes siguen siendo paflón:
    //
    //  - si el vacío pasa de 18, se agrega ese par de tubos y baja 7.6 más;
    //  - si quedó por debajo de 9, el último par de paflón se cambia por el de tubo, o sea que el
    //    vacío sube 8.9.
    //
    // Cualquiera de las dos correcciones aterriza siempre dentro de la ventana, así que nunca hace
    // falta un segundo par de tubos.

    /**
     * Cotas verticales del plano de Dora, acumuladas desde la base de la hoja: dónde termina la pila
     * de zócalos —que alterna paflón e inox—, el arranque de cada inox de la columna izquierda y el
     * cierre del vacío arriba.
     */
    fun cotasPanosDora(
        parante: Float, nZocalo: Int, nInoxIzq: Int, bastidor: Float, inox: Float
    ): List<Float> {
        val nZdora = if (nZocalo % 2 == 0) maxOf(1, nZocalo - 1) else nZocalo
        val zocaloInox = (nZdora - 1) / 2
        val paflonH = 1 + (nZdora + 1) / 2
        val alturaZona = parante - bastidor * paflonH - inox * zocaloInox
        if (alturaZona <= 0f) return emptyList()

        val pilaZocalo = (paflonH - 1) * bastidor + zocaloInox * inox
        val secciones = nInoxIzq.coerceAtLeast(0) + 1
        val secH = (alturaZona - nInoxIzq * inox) / secciones
        val divisores = (0 until nInoxIzq.coerceAtLeast(0)).map { i ->
            pilaZocalo + secH + i * (secH + inox)
        }
        return listOf(pilaZocalo) + divisores + listOf(pilaZocalo + alturaZona)
    }

    /** Cotas horizontales de Dora: dónde arranca cada divisor de columna y el ancho interior. */
    fun cotasColumnasDora(paflon: Float, bastidor: Float): List<Float> {
        if (paflon <= 0f) return emptyList()
        val colW = (paflon - 2f * bastidor) / 3f
        if (colW <= 0f) return emptyList()
        return listOf(colW, 2f * colW + bastidor, paflon)
    }

    /**
     * Cotas verticales del plano de Taly, desde la base de la hoja: el zócalo, el horizontal que
     * cierra el vacío abajo, las divisiones y el que lo cierra arriba.
     *
     * Con ángulo las divisiones no son alturas sino cruces contra los parantes, y cada lado corta a
     * distinta altura, así que salen repartidas en dos reglas —izquierda y derecha—, igual que en
     * Mari d.
     */
    fun cotasPanosTaly(
        parante: Float, nZocalo: Int, nDiv: Int, bastidor: Float, zonaAncho: Float, angulo: Float
    ): Pair<List<Float>, List<Float>> {
        val zonaBase = (nZocalo + 1) * bastidor            // zócalos + el horizontal de abajo
        val zonaAlto = parante - zonaBase - 2f * bastidor  // hasta el horizontal de arriba
        if (zonaAlto <= 0f) return emptyList<Float>() to emptyList()

        val marco = listOf(nZocalo * bastidor, zonaBase, zonaBase + zonaAlto)
        if (nDiv <= 1) return marco to emptyList()

        if (angulo != 0f) {
            val cruces = cotasPanosDiagonal(zonaAncho, zonaAlto, nZocalo + 1, nDiv, bastidor, angulo)
            return (marco + cruces.izq).sorted() to cruces.der
        }

        val barras = nDiv - 1
        val gap = (zonaAlto - barras * bastidor) / (barras + 1)
        val divisiones = (0 until barras).map { zonaBase + gap + it * (gap + bastidor) }
        return (marco + divisiones).sorted() to emptyList()
    }

    /** Cotas horizontales: dónde arranca el vidrio, dónde termina y el ancho interior. */
    fun cotasParantesTaly(paflon: Float, bastidor: Float): List<Float> {
        if (paflon <= 0f) return emptyList()
        val zona = zonaVidrioTaly(paflon, bastidor)
        val izquierda = zona.paresPaflon * bastidor + zona.paresTubo * zona.tubo
        return listOf(izquierda, izquierda + zona.anchoVidrio, paflon)
    }

    /** Reparto de los parantes interiores de Taly: qué vacío queda y con cuántos pares de cada perfil. */
    data class ZonaTaly(val anchoVidrio: Float, val paresPaflon: Int, val paresTubo: Int, val tubo: Float)

    fun zonaVidrioTaly(paflon: Float, bastidor: Float, tubo: Float = TUBO_TALY): ZonaTaly {
        var vacio = paflon
        var pares = 0
        while (vacio > 20f && vacio >= bastidor * 2f) {
            pares++
            vacio -= 2f * bastidor
        }
        return when {
            vacio > 18f && vacio >= tubo * 2f -> ZonaTaly(vacio - 2f * tubo, pares, 1, tubo)
            vacio < 9f && pares > 0 -> ZonaTaly(vacio + 2f * bastidor - 2f * tubo, pares - 1, 1, tubo)
            else -> ZonaTaly(vacio, pares, 0, tubo)
        }
    }

    fun anchoColumnaMili(paflon: Float, tubo: Float = TUBO_MILI): Float =
        ((paflon - 2f * tubo) / 3f).coerceAtLeast(0f)

    fun altoFilaMili(paranteInterno: Float, tubo: Float = TUBO_MILI): Float =
        ((paranteInterno - 2f * tubo) / 6f).coerceAtLeast(0f)

    /** Los cinco paños del vacío, como (ancho, alto). */
    private fun panosMili(paflon: Float, paranteInterno: Float, tubo: Float): List<Pair<Float, Float>> {
        val col = anchoColumnaMili(paflon, tubo)
        val fila = altoFilaMili(paranteInterno, tubo)
        val altoLargo = (paranteInterno - fila - tubo).coerceAtLeast(0f)
        val anchoLargo = (2f * col) + tubo
        val altoCentro = (paranteInterno - (2f * fila) - (2f * tubo)).coerceAtLeast(0f)
        return listOf(
            col to altoLargo,        // paño alto izquierdo
            col to altoLargo,        // paño alto derecho
            anchoLargo to fila,      // paño ancho superior
            anchoLargo to fila,      // paño ancho inferior
            col to altoCentro        // paño del medio
        )
    }

    /**
     * Cotas verticales del plano: el zócalo y el canto de arranque de cada tubo horizontal, medidos
     * desde la base de la hoja. El canto opuesto no se marca: mide lo que el tubo.
     */
    fun cotasPanosMili(parante: Float, nZocalo: Int, bastidor: Float, tubo: Float = TUBO_MILI): List<Float> {
        val paranteInterno = paranteInterno(parante, nZocalo, bastidor)
        if (paranteInterno <= 0f) return emptyList()
        val zoc = nZocalo * bastidor
        val fila = altoFilaMili(paranteInterno, tubo)
        return listOf(zoc, zoc + fila, zoc + paranteInterno - fila - tubo)
    }

    /** Cotas horizontales: el arranque de cada tubo vertical y el ancho interior total. */
    fun cotasColumnasMili(paflon: Float, tubo: Float = TUBO_MILI): List<Float> {
        if (paflon <= 0f) return emptyList()
        val col = anchoColumnaMili(paflon, tubo)
        return listOf(col, (2f * col) + tubo, paflon)
    }

    /**
     * Marco de la hoja: los parantes y los horizontales de ancho completo, que son el bastidor
     * superior y los zócalos. Las divisiones no generan piezas de paflón —el molinete se arma con
     * tubo, y va en su propio renglón—, así que el conteo lo manda el número de zócalos.
     */
    fun textoPaflonMili(paflon: Float, parante: Float, nZocalo: Int): String =
        "${df1(parante)} = 2\n${df1(paflon)} = ${nZocalo + 1}"

    /**
     * Tubos internos: los dos verticales miden lo mismo que el alto de los paños largos y los dos
     * horizontales, lo mismo que el ancho de los paños anchos. El molinete es simétrico.
     */
    fun textoTubosMili(paflon: Float, paranteInterno: Float, tubo: Float = TUBO_MILI): String {
        if (paflon <= 0f || paranteInterno <= 0f) return ""
        val fila = altoFilaMili(paranteInterno, tubo)
        val col = anchoColumnaMili(paflon, tubo)
        val largoVertical = paranteInterno - fila - tubo
        val largoHorizontal = (2f * col) + tubo
        return "${df1(largoVertical)} = 2\n${df1(largoHorizontal)} = 2"
    }

    /** Por cada paño, el par horizontal a tope y el par vertical descontando los dos junquillos. */
    fun textoJunkillosMili(
        paflon: Float, paranteInterno: Float, jun: Float, mocheta: Float, marcoSup: Float,
        tubo: Float = TUBO_MILI
    ): String {
        if (paflon <= 0f || paranteInterno <= 0f) return ""
        val acumulado = linkedMapOf<String, Int>()
        fun sumar(medida: String, cantidad: Int) {
            acumulado[medida] = (acumulado[medida] ?: 0) + cantidad
        }
        panosMili(paflon, paranteInterno, tubo).forEach { (ancho, alto) ->
            sumar(df1(ancho), 2)
            sumar(df1(alto - 2f * jun), 2)
        }
        if (mocheta > 0f) {
            sumar(df1(marcoSup), 2)
            sumar(df1(mocheta - 2f * jun), 2)
        }
        return acumulado.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    fun textoVidriosMili(
        paflon: Float, paranteInterno: Float, jun: Float, mocheta: Float, marcoSup: Float,
        tubo: Float = TUBO_MILI
    ): String {
        if (paflon <= 0f || paranteInterno <= 0f) return ""
        val holg = if (jun == 0f) 0.2f else HOLGURA_VIDRIO
        val acumulado = linkedMapOf<String, Int>()
        panosMili(paflon, paranteInterno, tubo).forEach { (ancho, alto) ->
            val medida = "${df1(ancho - holg)} x ${df1(alto - holg)}"
            acumulado[medida] = (acumulado[medida] ?: 0) + 1
        }
        return buildString {
            append(acumulado.entries.joinToString("\n") { "${it.key} = ${it.value}" })
            if (mocheta > 0f) append("\n${vidrioM(marcoSup, mocheta, jun)} = 1")
        }
    }

    // --------- Adel ---------
    // Un separador vertical parte el vacío en dos columnas: la derecha (1/4) lleva las divisiones de
    // vidrio y la izquierda (3/4) el relleno propio de cada variante (p1 horizontal, p3 vertical,
    // p2 un vidrio entero). Las medidas salen del dibujo: el separador es un bastidor más.

    /** Ancho de la columna de divisiones (la derecha): un cuarto del vacío sin el separador. */
    fun anchoDivisionesAdel(paflon: Float, bastidor: Float): Float =
        ((paflon - bastidor) / 4f).coerceAtLeast(0f)

    /** Ancho de la columna de relleno (la izquierda): los tres cuartos restantes. */
    fun anchoRellenoAdel(paflon: Float, bastidor: Float): Float =
        (paflon - bastidor - anchoDivisionesAdel(paflon, bastidor)).coerceAtLeast(0f)

    /**
     * Cuántos paflones enteros entran pegados uno contra otro en un tramo. Sin gruma: la gruma es de
     * Lina. Si el tramo no es múltiplo del bastidor sobra un resto, que no se reparte.
     */
    fun nRellenoApiladoAdel(tramo: Float, bastidor: Float): Int =
        if (bastidor <= 0f || tramo <= 0f) 0
        else floor((tramo / bastidor) + 0.001f).toInt()

    /**
     * Paflones de Adel. Los horizontales de ancho completo son el bastidor superior y los zócalos
     * —uno por zócalo, ni uno por división—; los que generan las divisiones son más cortos, van solo
     * en la columna derecha, y por eso llevan su propio renglón.
     *
     * Lo que cambia entre variantes es el relleno de la columna izquierda:
     *  - p1: paflones horizontales pegados uno sobre otro. Largo = el ancho de la columna, cantidad
     *    = los que entran en el alto del vacío.
     *  - p3: los mismos paflones pero de pie. Largo = el alto del vacío, cantidad = los que entran
     *    en el ancho de la columna.
     *  - v y p2: la columna es un vidrio entero, así que no aportan piezas.
     */
    fun textoPaflonAdel(
        variante: String, paflon: Float, parante: Float, paranteInterno: Float,
        nZocalo: Int, nDiv: Int, bastidor: Float
    ): String {
        val nBarras = (nDiv - 1).coerceAtLeast(0)
        val anchoRelleno = anchoRellenoAdel(paflon, bastidor)
        return buildString {
            append("${df1(parante)} = 2\n")               // parantes de la hoja
            append("${df1(paflon)} = ${nZocalo + 1}\n")   // bastidor superior + zócalos
            append("${df1(paranteInterno)} = 1")          // separador vertical entre las dos columnas
            if (nBarras > 0) append("\n${df1(anchoDivisionesAdel(paflon, bastidor))} = $nBarras")
            when (variante) {
                "Adel p1" -> {
                    val n = nRellenoApiladoAdel(paranteInterno, bastidor)
                    if (n > 0) append("\n${df1(anchoRelleno)} = $n")
                }
                "Adel p3" -> {
                    val n = nRellenoApiladoAdel(anchoRelleno, bastidor)
                    if (n > 0) append("\n${df1(paranteInterno)} = $n")
                }
            }
        }
    }

    /**
     * Junquillos de Adel: un par horizontal a tope y un par vertical descontando los dos junquillos,
     * por cada vidrio. Los vidrios son las divisiones de la columna derecha y, en las variantes de
     * vidrio entero (v y p2), también la columna izquierda completa.
     */
    fun textoJunkillosAdel(
        variante: String, paflon: Float, paranteInterno: Float, nDiv: Int,
        bastidor: Float, jun: Float, mocheta: Float, marcoSup: Float
    ): String {
        if (nDiv <= 0) return ""
        val anchoDiv = anchoDivisionesAdel(paflon, bastidor)
        val altoDiv = altoPano(paranteInterno, nDiv, bastidor)
        return buildString {
            append("${df1(anchoDiv)} = ${nDiv * 2}\n")
            append("${df1(altoDiv - 2 * jun)} = ${nDiv * 2}")
            if (variante == "Adel v" || variante == "Adel p2") {
                append("\n${df1(anchoRellenoAdel(paflon, bastidor))} = 2")
                append("\n${df1(paranteInterno - 2 * jun)} = 2")
            }
            if (mocheta > 0f) {
                append("\n${df1(marcoSup)} = 2\n${df1(mocheta - 2 * jun)} = 2")
            }
        }
    }

    /**
     * Vidrios de Adel: las divisiones de la columna derecha y, en las variantes de vidrio entero
     * (v y p2), el paño grande de la izquierda. Cada uno es su vacío menos la holgura, como en el
     * resto de los modelos.
     */
    fun textoVidriosAdel(
        variante: String, paflon: Float, paranteInterno: Float, nDiv: Int,
        bastidor: Float, jun: Float, mocheta: Float, marcoSup: Float
    ): String {
        if (nDiv <= 0) return ""
        val holg = if (jun == 0f) 0.2f else HOLGURA_VIDRIO
        val anchoDiv = anchoDivisionesAdel(paflon, bastidor)
        val altoDiv = altoPano(paranteInterno, nDiv, bastidor)
        return buildString {
            append("${df1(anchoDiv - holg)} x ${df1(altoDiv - holg)} = $nDiv")
            if (variante == "Adel v" || variante == "Adel p2") {
                append("\n${df1(anchoRellenoAdel(paflon, bastidor) - holg)} x ${df1(paranteInterno - holg)} = 1")
            }
            if (mocheta > 0f) append("\n${vidrioM(marcoSup, mocheta, jun)} = 1")
        }
    }

    // --------- Viky c ---------
    // Cuadrícula pareja: un paflón vertical al medio parte el ancho interior en DOS columnas iguales,
    // y el alto interior se reparte en nDiv filas iguales. Todas las celdas miden lo mismo, a
    // diferencia de "Viky", donde una columna son cuadrados y la otra una sola pieza alta.

    /** Ancho de cada columna: el interior menos el divisor del medio, partido en dos. */
    fun anchoColumnaVikyC(paflon: Float, interior: Float): Float =
        ((paflon - interior) / 2f).coerceAtLeast(0f)

    /** Marco de la hoja: los parantes y los horizontales de ancho completo (bastidor + zócalos). */
    fun textoPaflonVikyC(paflon: Float, parante: Float, nZocalo: Int, nDiv: Int): String {
        if (nDiv <= 0) return "${df1(parante)} = 2"
        return "${df1(parante)} = 2\n${df1(paflon)} = ${nZocalo + 1}"
    }

    /** Piezas de adentro: el divisor vertical y los horizontales, que van cortados por columna. */
    fun textoInteriorVikyC(
        paflon: Float, paranteInterno: Float, nDiv: Int, interior: Float
    ): String {
        if (nDiv <= 0) return ""
        val anchoCol = anchoColumnaVikyC(paflon, interior)
        val nInternos = (nDiv - 1).coerceAtLeast(0)
        return buildString {
            append("${df1(paranteInterno)} = 1")   // divisor vertical, de bastidor a bastidor
            if (nInternos > 0) append("\n${df1(anchoCol)} = ${nInternos * 2}")
        }
    }

    fun textoJunkillosVikyC(
        paflon: Float, paranteInterno: Float, nDiv: Int,
        interior: Float, jun: Float, mocheta: Float, marcoSup: Float
    ): String {
        if (nDiv <= 0) return ""
        val anchoCol = anchoColumnaVikyC(paflon, interior)
        val altoFila = altoPano(paranteInterno, nDiv, interior)
        val nCeldas = nDiv * 2
        return buildString {
            // Por celda: el par horizontal va a tope y el vertical descuenta los dos junquillos.
            append("${df1(anchoCol)} = ${nCeldas * 2}\n")
            append("${df1(altoFila - 2 * jun)} = ${nCeldas * 2}")
            if (mocheta > 0f) {
                append("\n${df1(marcoSup)} = 2\n${df1(mocheta - 2 * jun)} = 2")
            }
        }
    }

    fun textoVidriosVikyC(
        paflon: Float, paranteInterno: Float, nDiv: Int,
        interior: Float, jun: Float, mocheta: Float, marcoSup: Float
    ): String {
        if (nDiv <= 0) return ""
        val anchoCol = anchoColumnaVikyC(paflon, interior)
        val altoFila = altoPano(paranteInterno, nDiv, interior)
        val holg = if (jun == 0f) 0.2f else HOLGURA_VIDRIO
        return buildString {
            append("${df1(anchoCol - holg)} x ${df1(altoFila - holg)} = ${nDiv * 2}")
            if (mocheta > 0f) append("\n${vidrioM(marcoSup, mocheta, jun)} = 1")
        }
    }

    fun partesV(paflon: Float, unoMedio: Float) = (paflon - (unoMedio * 2)) / 3
    fun parteH(divisiones: Float, unoMedio: Float) = (divisiones - (unoMedio * 5)) / 6

    fun textoResumenArray(
        altoOriginal: Float,
        marcoSuperior: Float,
        tubo: String,
        paflon: Float,
        parante: Float,
        zocalo: Float,
        nDiv: Int,
        hPuente: Float,
        partesV: Float,
        parteH: Float,
        vidrioM: String
    ): String {
        val n = nDiv
        return buildString {
            append("Marco = ${df1(altoOriginal)} = 2\n")
            append("${df1(marcoSuperior)} = 1\n")
            append("tubo = ${if (tubo.isEmpty()) "-" else tubo} = 1\n")
            append("paflon = ${df1(paflon)} = 2\n")
            append("${df1(parante)} = 2\n")
            append("${parante - (zocalo + BASTIDOR)} = 1\n")
            append("15 = ${n - 1}\n")
            append("Tope = ${df1(marcoSuperior)} = 1\n")
            append("${df1(hPuente)} = 2\n")
            append("Vidrio = ${df1((partesV * 2) + 3.4f)} x ${df1(parteH - 0.4f)} = 2\n")
            append("${df1(divisiones(parante, nZocalo(0), n.toFloat(), BASTIDOR) - (parteH + 3.8f) - 0.4f)} x ${df1(partesV - 0.4f)} = 4\n")
            append("$vidrioM = 1\n")
            append("puntosT= ${df1(partesV)}, ${df1((partesV * 2) + 3.8f)}\n")
            append("puntosP= ${df1(parteH + 8.2f)}")
        }
    }

    // --------- Textos específicos (junkillos, vidrios) ---------
    fun textoJunkillos(
        variante: String,
        jun: Float,
        mocheta: Float,
        nPfvcal: Int,
        paflon: Float,
        bastidor: Float,
        nDiv: Int,
        paranteInterno: Float,
        marcoSuperior: Float,
        // Aluminio que hace de divisor. Por omisión, el mismo perfil del bastidor.
        interior: Float = bastidor
    ): String {
        return when (variante) {
            "Mari h" -> {
                // El alto del paño sale del vacío menos los paflones divisores. Antes se recalculaba
                // como `paranteInterno + un zócalo` repartido de nuevo, que se comía un bastidor: en
                // una 70x240 con 2 divisiones daba 82.5 donde el paño mide 86.6, y no coincidía con
                // el vidrio de ese mismo paño, que siempre usó la medida correcta.
                val altoPano = altoPano(paranteInterno, nDiv, interior)
                val piezas = (nPfvcal - 1) * 2
                buildString {
                    append("${df1(altoPano - (2 * jun))} = $piezas\n")
                    append("${df1(paflon)} = $piezas")
                    if (mocheta >= 0f) {
                        append("\n${df1(marcoSuperior)} = 2\n${df1(mocheta - (2 * jun))} = 2")
                    }
                }
            }
            "Mari v" -> {
                val anchoDiv = (paflon - (interior * (nDiv - 1))) / nDiv
                val alt = paranteInterno - (2 * jun)
                "${df1(anchoDiv)} = ${nDiv * 2}\n${df1(alt)} = ${nDiv * 2}" + (if (mocheta >= 0f) "\n${df1(marcoSuperior)} = 2\n${df1(mocheta - (2 * jun))} = 2" else "")
            }
            else -> ""
        }
    }

    fun textoVidrios(
        variante: String,
        jun: Float,
        paflon: Float,
        bastidor: Float,
        nDiv: Int,
        paranteInterno: Float,
        marcoSuperior: Float,
        mocheta: Float,
        angulo: Float = 0f,
        // Aluminio que hace de divisor. Por omisión, el mismo perfil del bastidor.
        interior: Float = bastidor
    ): String {
        return when (variante) {
            // El alto del paño se saca del vacío menos los divisores, la misma cuenta del junquillo:
            // así los dos siguen coincidiendo aunque el interior sea otro perfil.
            "Mari h" -> {
                val altoPano = altoPano(paranteInterno, nDiv, interior)
                if (mocheta < 0f) {
                    "${vidrioH(paflon, altoPano, jun)} = ${nPfvcal(nDiv) - 1}"
                } else {
                    "${vidrioH(paflon, altoPano, jun)} = ${nPfvcal(nDiv) - 1}\n${vidrioM(marcoSuperior, mocheta, jun)} = 1"
                }
            }
            "Mari v" -> if (mocheta < 0f) {
                "${vidrioV(paflon, interior, nDiv, paranteInterno, jun)} = ${nDiv}"
            } else {
                "${vidrioV(paflon, interior, nDiv, paranteInterno, jun)} = ${nDiv}\n${vidrioM(marcoSuperior, mocheta, jun)} = 1"
            }
            // La mocheta de "Mari d" no va aquí: tiene su propia fila (ver [textoVidrioMocheta]),
            // porque este texto convive con el detalle pieza por pieza y saldría repetida en los dos.
            "Mari d" -> textoVidriosMariD(paflon, paranteInterno, nDiv, interior, angulo, holguraVidrio(jun))
            else -> ""
        }
    }

    /** La holgura que le toca al vidrio según haya junquillo o no. */
    fun holguraVidrio(jun: Float): Float = if (jun == 0f) 0.2f else HOLGURA_VIDRIO

    /**
     * Detalle pieza por pieza. Solo "Mari d" lo tiene: en el resto de variantes los vidrios ya se
     * listan uno por uno en [textoVidrios] y la fila del detalle se oculta.
     */
    fun textoVidriosDetalle(
        variante: String,
        paflon: Float,
        bastidor: Float,
        nDiv: Int,
        paranteInterno: Float,
        angulo: Float,
        jun: Float = 0f,
        interior: Float = bastidor
    ): String {
        if (variante != "Mari d") return ""
        return textoVidriosDetalleMariD(paflon, paranteInterno, nDiv, interior, angulo, holguraVidrio(jun))
    }

    /**
     * Vidrio de la mocheta de "Mari d", separado del resto.
     *
     * Es un rectángulo común y no participa ni del apilado ni del detalle de las piezas diagonales:
     * en la pantalla iba repetido en las dos listas, y al archivar tenía que poder guardarse aunque
     * se destilde cualquiera de ellas. Por eso lleva su propia fila.
     */
    fun textoVidrioMocheta(variante: String, marcoSuperior: Float, mocheta: Float, jun: Float = 0f): String {
        if (variante != "Mari d" || mocheta < 0f) return ""
        return "${vidrioRectangularConHolgura(marcoSuperior, mocheta, holguraVidrio(jun))} = 1"
    }

    /**
     * `holgura` es lo que se le quita a cada lado del vidrio. En "Mari d" son 0.4; "Taly d" usa la
     * misma geometría pero su propio descuento, porque ahí el vidrio entra por dentro del junquillo.
     */
    fun textoVidriosMariD(
        paflon: Float,
        paranteInterno: Float,
        nDiv: Int,
        bastidor: Float,
        angulo: Float,
        holgura: Float = 0.4f
    ): String {
        val nBarras = maxOf(0, nDiv - 1)
        if (nDiv <= 0 || paflon <= 0f || paranteInterno <= 0f) return ""

        if (nBarras == 0) {
            return "${vidrioRectangularConHolgura(paflon, paranteInterno, holgura)} = 1"
        }

        val barras = barrasRotadasMariD(paflon, paranteInterno, nDiv, bastidor, angulo)
        val altoDiagonal = altoPaflonRotadoMariD(bastidor, angulo)
        val anchoVidrio = (paflon - holgura).coerceAtLeast(0f)
        if (barras.all { it.arista1.cruzaAncho() && it.arista2.cruzaAncho() }) {
            val altoVidrio = (paranteInterno - (holgura * vidriosQueApilanMariD(paflon, paranteInterno, nDiv, bastidor, angulo)) - (altoDiagonal * nBarras))
                .coerceAtLeast(0f)
            return "${df1VidrioMariD(anchoVidrio)} x ${df1VidrioMariD(altoVidrio)} = 1"
        }

        if (barras.all { it.arista1.tocaBastidor() && it.arista2.tocaBastidor() }) {
            val altoADescontar = altoPaflonesVidrioUnicoMariD(barras, paranteInterno, altoDiagonal, holgura, angulo)
            val altoVidrio = (paranteInterno - (holgura * vidriosQueApilanMariD(paflon, paranteInterno, nDiv, bastidor, angulo)) - altoADescontar)
                .coerceAtLeast(0f)
            return "${df1VidrioMariD(anchoVidrio)} x ${df1VidrioMariD(altoVidrio)} = 1"
        }

        val alcance = alcancePerpendicularRotado(paflon, paranteInterno, angulo)
        val espacio = ((alcance * 2f) - (nBarras * bastidor)) / (nBarras + 1)
        val inicio = (paranteInterno / 2f) - alcance

        val medidas = (0 until nDiv).mapNotNull { indice ->
            val desde = inicio + indice * (bastidor + espacio)
            val hasta = desde + espacio
            dimensionesVacioMariD(paflon, paranteInterno, angulo, desde, hasta)?.let { (ancho, alto) ->
                "${df1VidrioMariD((ancho - holgura).coerceAtLeast(0f))} x ${df1VidrioMariD((alto - holgura).coerceAtLeast(0f))}"
            }
        }

        val acumulado = linkedMapOf<String, Int>()
        medidas.forEach { medida ->
            acumulado[medida] = (acumulado[medida] ?: 0) + 1
        }
        return acumulado.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    /**
     * Medida de CADA vidrio por separado, para cortarlos uno por uno (el apilado sigue en
     * [textoVidriosMariD], que es lo que se compra).
     *
     * Las piezas de "Mari d" son trapecios en las esquinas y paralelogramos en el medio. Lo que se
     * lista es el rectángulo de MENOR ÁREA que envuelve a cada pieza, que es el retazo que el
     * vidriero corta antes de rebajar las puntas:
     *
     * - trapecio de esquina: dos de sus lados son las tapas del paflón, así que gana la caja
     *   alineada a los ejes (48.1 x 48.6 = 2340 cm² contra 68.4 x 34.4 = 2354 de la girada);
     * - paralelogramo del medio: no tiene lados rectos contra el bastidor, así que gana la caja
     *   girada al ángulo del corte (102.4 x 34.4 = 3523 cm² contra 48.1 x 96.7 = 4654).
     *
     * La holgura de 0.4 se descuenta a los dos lados de cada pieza. Aquí no corre el conteo de
     * [vidriosQueApilanMariD]: eso es del apilado, donde las puntas se pierden en el vacío de la
     * pieza opuesta y por eso no aportan su holgura.
     */
    fun textoVidriosDetalleMariD(
        paflon: Float,
        paranteInterno: Float,
        nDiv: Int,
        bastidor: Float,
        angulo: Float,
        holgura: Float = 0.4f
    ): String {
        if (nDiv <= 0 || paflon <= 0f || paranteInterno <= 0f) return ""

        val nBarras = maxOf(0, nDiv - 1)
        if (nBarras == 0) {
            return "${vidrioRectangularConHolgura(paflon, paranteInterno, holgura)} = 1"
        }

        val alcance = alcancePerpendicularRotado(paflon, paranteInterno, angulo)
        val espacio = ((alcance * 2f) - (nBarras * bastidor)) / (nBarras + 1)
        val inicio = (paranteInterno / 2f) - alcance

        val medidas = (0 until nDiv).mapNotNull { indice ->
            val desde = inicio + indice * (bastidor + espacio)
            val poligono = poligonoVacioMariD(paflon, paranteInterno, angulo, desde, desde + espacio)
            if (poligono.size < 3) return@mapNotNull null
            val (lado1, lado2) = rectanguloMinimoMariD(poligono)
            // df1 pelado, sin el empujón de 0.001 de df1VidrioMariD: ese parche es del apilado y
            // aquí cruza límites de redondeo (la esquina de 4 divisiones mide 48.2497 y debe leerse
            // 48.2, no 48.3).
            "${df1((lado1 - holgura).coerceAtLeast(0f))} x ${df1((lado2 - holgura).coerceAtLeast(0f))}"
        }

        val acumulado = linkedMapOf<String, Int>()
        medidas.forEach { medida ->
            acumulado[medida] = (acumulado[medida] ?: 0) + 1
        }
        return acumulado.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    /**
     * Rectángulo de menor área que envuelve al polígono, probando la caja alineada a los ejes y la
     * caja girada a cada arista (el mínimo siempre apoya en una arista).
     *
     * Si gana la alineada se devuelve `ancho x alto`, que es como se lee la pieza contra el marco;
     * si gana una girada se devuelve `largo x ancho`, porque ahí "alto" y "ancho" ya no significan
     * nada: es un retazo inclinado. El empate se lo lleva la alineada.
     */
    private fun rectanguloMinimoMariD(poligono: List<PuntoPlano>): Pair<Float, Float> {
        val xs = poligono.map { it.x }
        val ys = poligono.map { it.y }
        var mejor = (xs.max() - xs.min()) to (ys.max() - ys.min())
        var mejorArea = mejor.first * mejor.second

        poligono.indices.forEach { indice ->
            val a = poligono[indice]
            val b = poligono[(indice + 1) % poligono.size]
            val largoArista = sqrt(((b.x - a.x) * (b.x - a.x)) + ((b.y - a.y) * (b.y - a.y)))
            if (largoArista <= 0.001f) return@forEach

            val ux = (b.x - a.x) / largoArista
            val uy = (b.y - a.y) / largoArista
            if (abs(ux) <= 0.001f || abs(uy) <= 0.001f) return@forEach // ya cubierta por la caja alineada

            val us = poligono.map { (it.x * ux) + (it.y * uy) }
            val vs = poligono.map { (-it.x * uy) + (it.y * ux) }
            val lado1 = us.max() - us.min()
            val lado2 = vs.max() - vs.min()
            val area = lado1 * lado2
            if (area < mejorArea - 0.01f) {
                mejorArea = area
                mejor = max(lado1, lado2) to kotlin.math.min(lado1, lado2)
            }
        }
        return mejor
    }

    private fun altoPaflonRotadoMariD(bastidor: Float, angulo: Float): Float {
        val cosAng = abs(cos(Math.toRadians(angulo.toDouble())).toFloat())
        return if (cosAng <= 0.0001f) bastidor else bastidor / cosAng
    }

    private fun altoPaflonesVidrioUnicoMariD(
        barras: List<BarraRotada>,
        alto: Float,
        altoDiagonal: Float,
        holgura: Float,
        angulo: Float
    ): Float {
        return barras.sumOf { barra ->
            altoPaflonVidrioUnicoMariD(barra, alto, altoDiagonal, holgura, angulo).toDouble()
        }.toFloat()
    }

    private fun altoPaflonVidrioUnicoMariD(
        barra: BarraRotada,
        alto: Float,
        altoDiagonal: Float,
        holgura: Float,
        angulo: Float
    ): Float {
        val aristas = listOf(barra.arista1, barra.arista2)
        if (aristas.all { it.cruzaAncho() }) return altoDiagonal
        if (aristas.all { 'T' in it.lados }) return 0f
        if (aristas.all { 'B' in it.lados }) return 0f

        val ysLaterales = aristas
            .flatMap { it.puntos }
            .filter { it.lado == 'L' || it.lado == 'R' }
            .map { it.y }
        if (ysLaterales.isEmpty()) return altoDiagonal

        val tocaArriba = aristas.any { 'T' in it.lados }
        val tocaAbajo = aristas.any { 'B' in it.lados }
        val descuento = when {
            tocaArriba -> ysLaterales.minOrNull() ?: altoDiagonal
            tocaAbajo -> alto - (ysLaterales.maxOrNull() ?: alto)
            else -> altoDiagonal
        }

        // Aquí había un ajuste de holgura —`holgura * (1 - tan angulo)` cuando el ángulo era menor a
        // 45°— que ACHICABA este descuento y por lo tanto agrandaba el vidrio. Estaba de más por dos
        // razones: es una corrección de holgura metida dentro del cálculo GEOMÉTRICO (las holguras
        // se descuentan aparte, una vez), y ensuciaba la medida cruda en ángulos tendidos.
        //
        // Verificado en taller y en CorelDRAW (paflón 48.1, parante interno 181.5, 6 divisiones,
        // 32°): la suma cruda de los espacios de vidrio es 138.666. Con el ajuste daba 138.966.
        // A 45° el término valía cero, por eso los casos de ese ángulo nunca lo delataron.
        return descuento.coerceIn(0f, altoDiagonal)
    }

    private data class PuntoPlano(val x: Float, val y: Float)

    private fun poligonoVacioMariD(
        ancho: Float,
        alto: Float,
        angulo: Float,
        desde: Float,
        hasta: Float
    ): List<PuntoPlano> {
        val rectangulo = listOf(
            PuntoPlano(0f, 0f),
            PuntoPlano(ancho, 0f),
            PuntoPlano(ancho, alto),
            PuntoPlano(0f, alto)
        )
        val recortadoDesde = recortarPorProyeccionMariD(rectangulo, ancho, alto, angulo, desde, mantenerMayor = true)
        return recortarPorProyeccionMariD(recortadoDesde, ancho, alto, angulo, hasta, mantenerMayor = false)
    }

    private fun dimensionesVacioMariD(
        ancho: Float,
        alto: Float,
        angulo: Float,
        desde: Float,
        hasta: Float
    ): Pair<Float, Float>? {
        val poligono = poligonoVacioMariD(ancho, alto, angulo, desde, hasta)
        if (poligono.size < 3) return null

        val xs = poligono.map { it.x }
        val ys = poligono.map { it.y }
        return (xs.maxOrNull()!! - xs.minOrNull()!!) to (ys.maxOrNull()!! - ys.minOrNull()!!)
    }

    private fun recortarPorProyeccionMariD(
        poligono: List<PuntoPlano>,
        ancho: Float,
        alto: Float,
        angulo: Float,
        limite: Float,
        mantenerMayor: Boolean
    ): List<PuntoPlano> {
        if (poligono.isEmpty()) return emptyList()

        val resultado = mutableListOf<PuntoPlano>()
        poligono.forEachIndexed { indice, actual ->
            val siguiente = poligono[(indice + 1) % poligono.size]
            val actualDentro = dentroDeLimiteProyeccionMariD(actual, ancho, alto, angulo, limite, mantenerMayor)
            val siguienteDentro = dentroDeLimiteProyeccionMariD(siguiente, ancho, alto, angulo, limite, mantenerMayor)

            when {
                actualDentro && siguienteDentro -> resultado += siguiente
                actualDentro && !siguienteDentro -> resultado += interseccionProyeccionMariD(actual, siguiente, ancho, alto, angulo, limite)
                !actualDentro && siguienteDentro -> {
                    resultado += interseccionProyeccionMariD(actual, siguiente, ancho, alto, angulo, limite)
                    resultado += siguiente
                }
            }
        }
        return resultado
    }

    private fun dentroDeLimiteProyeccionMariD(
        punto: PuntoPlano,
        ancho: Float,
        alto: Float,
        angulo: Float,
        limite: Float,
        mantenerMayor: Boolean
    ): Boolean {
        val proyeccion = proyeccionRotadaMariD(punto.x, punto.y, ancho, alto, angulo)
        return if (mantenerMayor) proyeccion >= limite - 0.001f else proyeccion <= limite + 0.001f
    }

    private fun interseccionProyeccionMariD(
        a: PuntoPlano,
        b: PuntoPlano,
        ancho: Float,
        alto: Float,
        angulo: Float,
        limite: Float
    ): PuntoPlano {
        val pa = proyeccionRotadaMariD(a.x, a.y, ancho, alto, angulo)
        val pb = proyeccionRotadaMariD(b.x, b.y, ancho, alto, angulo)
        val divisor = pb - pa
        if (abs(divisor) <= 0.0001f) return a

        val t = ((limite - pa) / divisor).coerceIn(0f, 1f)
        return PuntoPlano(
            x = a.x + (b.x - a.x) * t,
            y = a.y + (b.y - a.y) * t
        )
    }

    private fun proyeccionRotadaMariD(
        x: Float,
        y: Float,
        ancho: Float,
        alto: Float,
        angulo: Float
    ): Float {
        val rad = Math.toRadians(angulo.toDouble())
        val sinAng = sin(rad).toFloat()
        val cosAng = cos(rad).toFloat()
        return (alto / 2f) + (((ancho / 2f) - x) * sinAng) + ((y - (alto / 2f)) * cosAng)
    }

    private fun vidrioRectangularConHolgura(ancho: Float, alto: Float, holgura: Float = 0.4f): String =
        "${df1VidrioMariD((ancho - holgura).coerceAtLeast(0f))} x ${df1VidrioMariD((alto - holgura).coerceAtLeast(0f))}"

    private fun df1VidrioMariD(valor: Float): String {
        return df1(valor + 0.001f)
    }

    /**
     * Cuántos vidrios SUMAN altura al apilarlos, que son los únicos que aportan su holgura.
     *
     * Los vidrios de "Mari d" son triángulos y paralelogramos. Para cortarlos de una plancha se
     * apilan uno sobre otro y se toma el rectángulo que los envuelve. Solo los que llegan al ANCHO
     * COMPLETO del paflón hacen crecer ese rectángulo: los de las puntas son más angostos y encajan
     * en el vacío que dejan los otros, así que no suman medida ni holgura.
     *
     * Antes se descontaba `holgura * nDiv`, dando de menos. Y no puede ser un "-2" fijo: cuántos
     * quedan angostos depende de la geometría. Verificado en taller (puerta 70x240, ángulo 45°,
     * bastidor 8.25, paflón 48.1):
     *
     * | divisiones | anchos de las piezas            | apilan | alto  |
     * |------------|---------------------------------|--------|-------|
     * | 4 (zóc. 3) | 44.5 · 48.1 ×2 · 44.5           | 2      | 136.4 |
     * | 6          | 28.5 · 48.1 ×4 · 28.5           | 4      | 144.9 |
     * | 7          | 22.8 · 48.1 ×5 · 22.8           | 5      | 132.8 |
     * | 8          | 18.5 · 48.1 ×6 · 18.5           | 6      | 120.8 |
     * | 11         | 10.3 · 32.2 · 48.1 ×7 · 32.2 ·  | 7      | 108.7 |
     */
    private fun vidriosQueApilanMariD(
        paflon: Float,
        paranteInterno: Float,
        nDiv: Int,
        bastidor: Float,
        angulo: Float
    ): Int {
        val nBarras = maxOf(0, nDiv - 1)
        if (nBarras == 0) return nDiv
        val alcance = alcancePerpendicularRotado(paflon, paranteInterno, angulo)
        val espacio = ((alcance * 2f) - (nBarras * bastidor)) / (nBarras + 1)
        val inicio = (paranteInterno / 2f) - alcance
        // Tolerancia de un milímetro: el recorte geométrico no cae exacto en el ancho del paflón.
        val anchoCompleto = paflon - 0.1f
        val apilan = (0 until nDiv).count { indice ->
            val desde = inicio + indice * (bastidor + espacio)
            val pieza = dimensionesVacioMariD(paflon, paranteInterno, angulo, desde, desde + espacio)
            pieza != null && pieza.first >= anchoCompleto
        }
        // Si la geometría no reconoce ninguno (ángulos extremos), se conserva el conteo anterior.
        return if (apilan > 0) apilan else nDiv
    }
}
