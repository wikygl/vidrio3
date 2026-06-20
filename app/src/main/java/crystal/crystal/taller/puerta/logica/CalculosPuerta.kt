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

    // --------- Formateo ---------
    fun df1(valor: Float): String {
        val texto = String.format(Locale.US, "%.1f", valor)
        return when {
            texto == "-0.0" -> "0"
            texto.endsWith(".0") -> texto.dropLast(2)
            else -> texto
        }
    }

    fun df2(valor: Float): String = String.format(Locale.US, "%.2f", valor)

    private fun df2Piso(valor: Float): String {
        val floored = floor(valor * 100f) / 100f
        return String.format(Locale.US, "%.2f", floored)
    }

    // --------- Cálculos base ---------
    fun marcoSuperior(ancho: Float, marco: Float) = ancho - (2 * marco)

    fun tubo(ancho: Float, marco: Float, mocheta: Float): String =
        if (mocheta > 0f) df1(ancho - (2 * marco)) else ""

    fun paflon(ancho: Float, marco: Float, bastidor: Float, holgura: Float = 1f): Float =
        ((ancho - (2 * marco)) - holgura) - (2 * bastidor)
    fun parante(hPuente: Float, piso: Float, holgura: Float = 1f): Float =
        if (piso == 0f) hPuente - holgura else (hPuente - (holgura / 2)) - piso

    fun paranteInterno(parante: Float, nZocalo: Int, bastidor: Float): Float =
        parante - ((nZocalo + 1) * bastidor)

    fun divisiones(parante: Float, nZocalo: Int, divi2: Float, bastidor: Float): Float {
        val base = if (nZocalo > 1) parante - zocalo(nZocalo, bastidor) else parante
        val nbas = if (nZocalo > 1) divi2 * bastidor else (divi2 + 1) * bastidor
        return (base - nbas) / divi2
    }

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
        val holgura = if (jun == 0f) 0.2f else 0.4f
        val anchv = df1(paflon - holgura).toFloat()
        val altv = df1(divisiones - holgura).toFloat()
        return "${df1(anchv)} x ${df1(altv)}"
    }

    fun vidrioV(paflon: Float, bastidor: Float, divi: Int, paranteInterno: Float, jun: Float): String {
        val holgura = if (jun == 0f) 0.2f else 0.4f
        val anchv = ((paflon - (bastidor * (divi - 1))) / divi) - holgura
        val altv = paranteInterno - holgura
        return "${df1(anchv)} x ${df1(altv)}"
    }

    fun vidrioM(marcoSuperior: Float, mocheta: Float, jun: Float): String {
        val holgura = if (jun == 0f) 0.2f else 0.4f
        val uno = df1(marcoSuperior - holgura).toFloat()
        val dos = df1(mocheta - holgura).toFloat()
        return "${df1(uno)} x ${df1(dos)}"
    }

    fun referen(ancho: Float, alto: Float, hPuente: Float, mocheta: Float): String =
        if (mocheta > 0f) "anch ${df1(ancho)} x alt ${df1(alto)}\nAlto hoja = ${df1(hPuente)}" else "anch ${df1(ancho)} x alt ${df1(alto)}"

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
                arista1.esRecortadaEnTapaCon(arista2) -> df2Piso(max(longitud1, longitud2))
                arista1.tocaTapa() && arista2.cruzaAncho() && compensacionEsquina1 > 0f -> {
                    df2(arista1.longitud + extraCorte + compensacionEsquina1)
                }
                arista2.tocaTapa() && arista1.cruzaAncho() && compensacionEsquina2 > 0f -> {
                    df2(arista2.longitud + extraCorte + compensacionEsquina2)
                }
                else -> df2(((longitud1 + longitud2) / 2f) + extraCorte)
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
                val clave = df2Piso(pieza.largo)
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
        marcoSuperior: Float
    ): String {
        return when (variante) {
            "Mari h" -> if (mocheta < 0f) {
                "${df1(divisiones(paranteInterno + zocalo(1, bastidor), 1, (nPfvcal - 1).toFloat(), bastidor) - (2 * jun))} = ${(nPfvcal - 1) * 2}\n${df1(paflon)} = ${(nPfvcal - 1) * 2}"
            } else {
                "${df1(divisiones(paranteInterno + zocalo(1, bastidor), 1, (nPfvcal - 1).toFloat(), bastidor) - (2 * jun))} = ${(nPfvcal - 1) * 2}\n" +
                        "${df1(paflon)} = ${(nPfvcal - 1) * 2}\n" +
                        "${df1(marcoSuperior)} = 2\n" +
                        "${df1(mocheta - (2 * jun))} = 2"
            }
            "Mari v" -> {
                val anchoDiv = (paflon - (bastidor * (nDiv - 1))) / nDiv
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
        divisiones: Float,
        bastidor: Float,
        nDiv: Int,
        paranteInterno: Float,
        marcoSuperior: Float,
        mocheta: Float,
        angulo: Float = 0f
    ): String {
        return when (variante) {
            "Mari h" -> if (mocheta < 0f) {
                "${vidrioH(paflon, divisiones, jun)} = ${nPfvcal(nDiv) - 1}"
            } else {
                "${vidrioH(paflon, divisiones, jun)} = ${nPfvcal(nDiv) - 1}\n${vidrioM(marcoSuperior, mocheta, jun)} = 1"
            }
            "Mari v" -> if (mocheta < 0f) {
                "${vidrioV(paflon, bastidor, nDiv, paranteInterno, jun)} = ${nDiv}"
            } else {
                "${vidrioV(paflon, bastidor, nDiv, paranteInterno, jun)} = ${nDiv}\n${vidrioM(marcoSuperior, mocheta, jun)} = 1"
            }
            "Mari d" -> {
                val vidrios = textoVidriosMariD(paflon, paranteInterno, nDiv, bastidor, angulo)
                if (mocheta < 0f) {
                    vidrios
                } else {
                    val vidrioMocheta = vidrioRectangularConHolgura(marcoSuperior, mocheta)
                    listOf(vidrios, "$vidrioMocheta = 1").filter { it.isNotBlank() }.joinToString("\n")
                }
            }
            else -> ""
        }
    }

    fun textoVidriosMariD(
        paflon: Float,
        paranteInterno: Float,
        nDiv: Int,
        bastidor: Float,
        angulo: Float
    ): String {
        val nBarras = maxOf(0, nDiv - 1)
        if (nDiv <= 0 || paflon <= 0f || paranteInterno <= 0f) return ""

        val holgura = 0.4f
        if (nBarras == 0) {
            return "${vidrioRectangularConHolgura(paflon, paranteInterno)} = 1"
        }

        val barras = barrasRotadasMariD(paflon, paranteInterno, nDiv, bastidor, angulo)
        val altoDiagonal = altoPaflonRotadoMariD(bastidor, angulo)
        val anchoVidrio = (paflon - holgura).coerceAtLeast(0f)
        if (barras.all { it.arista1.cruzaAncho() && it.arista2.cruzaAncho() }) {
            val altoVidrio = (paranteInterno - (holgura * nDiv) - (altoDiagonal * nBarras)).coerceAtLeast(0f)
            return "${df1VidrioMariD(anchoVidrio)} x ${df1VidrioMariD(altoVidrio)} = 1"
        }

        if (barras.all { it.arista1.tocaBastidor() && it.arista2.tocaBastidor() }) {
            val altoADescontar = altoPaflonesVidrioUnicoMariD(barras, paranteInterno, altoDiagonal, holgura, angulo)
            val altoVidrio = (paranteInterno - (holgura * nDiv) - altoADescontar).coerceAtLeast(0f)
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

        val tanAng = abs(tan(Math.toRadians(angulo.toDouble())).toFloat())
        val ajusteHolguraEnRemate = if (tanAng < 1f) holgura * (1f - tanAng) else 0f
        return (descuento - ajusteHolguraEnRemate).coerceIn(0f, altoDiagonal)
    }

    private data class PuntoPlano(val x: Float, val y: Float)

    private fun dimensionesVacioMariD(
        ancho: Float,
        alto: Float,
        angulo: Float,
        desde: Float,
        hasta: Float
    ): Pair<Float, Float>? {
        val rectangulo = listOf(
            PuntoPlano(0f, 0f),
            PuntoPlano(ancho, 0f),
            PuntoPlano(ancho, alto),
            PuntoPlano(0f, alto)
        )
        val recortadoDesde = recortarPorProyeccionMariD(rectangulo, ancho, alto, angulo, desde, mantenerMayor = true)
        val poligono = recortarPorProyeccionMariD(recortadoDesde, ancho, alto, angulo, hasta, mantenerMayor = false)
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

    private fun vidrioRectangularConHolgura(ancho: Float, alto: Float): String {
        val holgura = 0.4f
        return "${df1VidrioMariD((ancho - holgura).coerceAtLeast(0f))} x ${df1VidrioMariD((alto - holgura).coerceAtLeast(0f))}"
    }

    private fun df1VidrioMariD(valor: Float): String {
        return df1(valor + 0.001f)
    }
}
