package crystal.crystal.taller.puerta.logica

object CalculosPuerta {
    const val HOJA_REF = 199f
    const val MARCO = 2.2f
    const val BASTIDOR = 8.25f
    const val UNO_MEDIO = 3.8f

    // --------- Formateo ---------
    fun df1(valor: Float): String {
        val s = if ("$valor".endsWith(".0")) "$valor".replace(".0", "") else "%.1f".format(valor)
        return s.replace(",", ".")
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
        mocheta: Float
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
            else -> ""
        }
    }
}
