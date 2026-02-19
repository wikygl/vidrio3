package crystal.crystal.taller.nova

import kotlin.math.ceil

object NovaCalculos {

    // ==================== FUNCIONES DE FORMATO ====================
    fun df1(defo: Float): String {
        return if (defo % 1 == 0f) {
            defo.toInt().toString()
        } else {
            "%.1f".format(defo).replace(",", ".")
        }
    }
    // ==================== FUNCIONES DE DIVISIÖN Y ESTRUCTURA ====================
    fun divisiones(ancho: Float, divisManual: Int, tipo: String = "nn"): Int {
        return when (tipo) {
            "nn", "nl" -> return if (divisManual == 0) {
                when {
                    ancho <= 60 -> 1
                    ancho in 60.0..120.0 -> 2
                    ancho in 120.0..180.0 -> 3
                    ancho in 180.0..240.0 -> 4
                    ancho in 240.0..300.0 -> 5
                    ancho in 300.0..360.0 -> 6
                    ancho in 360.0..420.0 -> 7
                    ancho in 420.0..480.0 -> 8
                    ancho in 480.0..540.0 -> 9
                    ancho in 540.0..600.0 -> 10
                    ancho in 600.0..660.0 -> 11
                    ancho in 660.0..720.0 -> 12
                    ancho in 720.0..780.0 -> 13
                    ancho in 780.0..840.0 -> 14
                    ancho in 840.0..900.0 -> 15
                    else -> 1
                }
            } else {
                divisManual
            }
            "ncc" -> 2
            "n3c", "ncfc" -> 3
            else -> 0
        }
    }
    fun nFijos(divisiones: Int, tipo: String = "nn"): Int {
        return when (tipo) {
            "nn", "nl" -> when (divisiones) {
                1 -> 1
                2 -> 1
                3 -> 2
                4 -> 2
                5 -> 3
                6 -> 4
                7 -> 4
                8 -> 4
                9 -> 5
                10 -> 6
                11 -> 6
                12 -> 6
                13 -> 7
                14 -> 8
                15 -> 8
                else -> 0
            }
            "ncc" -> 0
            "n3c" -> 0
            "ncfc" -> 1
            else -> 0
        }
    }
    fun nCorredizas(divisiones: Int, tipo: String = "nn"): Int {
        return when (tipo) {
            "nn", "nl" -> when (divisiones) {
                1 -> 0
                2 -> 1
                3 -> 1
                4 -> 2
                5 -> 2
                6 -> 2
                7 -> 3
                8 -> 4
                9 -> 4
                10 -> 4
                11 -> 5
                12 -> 6
                13 -> 6
                14 -> 6
                15 -> 7
                else -> 0
            }
            "ncc" -> 2
            "n3c" -> 3
            "ncfc" -> 2
            else -> 0
        }
    }
    fun ordenDivis(divisiones: Int,ancho: Float): String {
        val f = "f<${df1(ancho/divisiones)}>"
        val c = "c<${df1(ancho/divisiones)}>"
         return when (divisiones) {
            1 -> f
            2 -> "$f$c"
            3 -> "$f$c$f"
            4 -> "$f$c$c$f"
            5 -> "$f$c$f$c$f"
            6 -> "$f$c$f$f$c$f"
            7 -> "$f$c$f$c$f$c$f"
            8 -> "$f$c$c$f$f$c$c$f"
            9 -> "$f$c$f$c$f$c$f$c$f"
            10 -> "$f$c$f$c$f$f$c$f$c$f"
            11 -> "$f$c$f$c$f$c$f$c$f$c$f"
            12 -> "$f$c$c$f$f$c$c$f$f$c$c$f"
            13 -> "$f$c$f$c$f$c$f$c$f$c$f$c$f"
            14 -> "$f$c$f$c$f$f$c$c$f$f$c$f$c$f"
            15 -> "$f$c$f$c$f$c$f$c$f$c$f$c$f$c$f"
            else -> f
        }
    }
    fun ordenDivisConParantes(divisiones: Int, ancho: Float): String {
        val nP = nPuentes(divisiones)
        if (nP <= 1) return ordenDivis(divisiones, ancho)
        val f = "f<${df1(ancho / divisiones)}>"
        val c = "c<${df1(ancho / divisiones)}>"
        val grupos = when (divisiones) {
            6  -> listOf("$f$c$f", "$f$c$f")
            8  -> listOf("$f$c$c$f", "$f$c$c$f")
            10 -> listOf("$f$c$f$c$f", "$f$c$f$c$f")
            12 -> listOf("$f$c$c$f", "$f$c$c$f", "$f$c$c$f")
            14 -> listOf("$f$c$f$c$f", "$f$c$c$f", "$f$c$f$c$f")
            else -> return ordenDivis(divisiones, ancho)
        }
        return grupos.joinToString(";P;")
    }
    fun ordenMochetas(totalMochetas: Int, ancho: Float): String {
        if (totalMochetas <= 0) return "f<${df1(ancho)}>"
        val m = "f<${df1(ancho / totalMochetas)}>"
        return (1..totalMochetas).joinToString("") { m }
    }
    fun ordenMochetasConParantes(divisiones: Int, ancho: Float): String {
        val nP = nPuentes(divisiones)
        if (nP <= 1) {
            val am = anchMota(ancho)
            return ordenMochetas(am, ancho)
        }
        val gruposDivs = when (divisiones) {
            6  -> listOf(3, 3)
            8  -> listOf(4, 4)
            10 -> listOf(5, 5)
            12 -> listOf(4, 4, 4)
            14 -> listOf(5, 4, 5)
            else -> return ordenMochetas(1, ancho)
        }
        val anchoPorDiv = ancho / divisiones
        val sb = StringBuilder()
        for (nDivs in gruposDivs) {
            val anchoSeccion = nDivs * anchoPorDiv
            val am = anchMota(anchoSeccion)
            val anchoVidrio = anchoSeccion / am
            repeat(am) { sb.append("f<${df1(anchoVidrio)}>") }
        }
        return sb.toString()
    }
    fun altoHoja(alto: Float, hoja: Float): Float {
        val corre = if (hoja >= alto) alto else hoja
        return if (hoja == 0f) {
            alto / 7 * 5
        } else {
            corre
        }
    }
    fun siNoMoch(alto: Float, hoja: Float): Int {
        return if (hoja >= alto) 0 else 1
    }
    fun altoMocheta(alto: Float, altoHoja: Float, tubo: Float): Float {
        return alto - (altoHoja + tubo)
    }
    fun nPuentes(divisiones: Int): Int {
        return when (divisiones) {
            1, 2, 3, 4, 5, 7, 9, 11, 13, 15 -> 1
            6, 8, 10 -> 2
            12, 14 -> 3
            else -> 0
        }
    }
    /**
     * Calcula medida de puentes para APARENTE
     */
    fun mPuentes1Aparente(ancho: Float, divisiones: Int): Float {
        val parantes = 2.5f
        return when (divisiones) {
            1, 2, 3, 4, 5, 7, 9, 11, 13, 15 -> ancho
            6, 8 -> (ancho - parantes) / 2
            10 -> (ancho - (2 * parantes)) / divisiones * 3
            12 -> (ancho - (2 * parantes)) / 3
            14 -> (ancho - (2 * parantes)) / divisiones * 5
            else -> 0f
        }
    }

    /**
     * Calcula medida de puentes para INAPARENTE/PIVOTANTE
     */
    fun mPuentes1Inaparente(ancho: Float, divisiones: Int): Float {
        val parantes = 2.5f
        return when (divisiones) {
            1, 2, 3, 4, 5, 7, 9, 11, 13, 15 -> ancho
            6, 8, 10 -> (ancho - parantes) / 2
            12 -> (ancho - (2 * parantes)) / 3
            14 -> (ancho - (2 * parantes)) / divisiones * 5
            else -> 0f
        }
    }

    /**
     * Wrapper para compatibilidad
     */
    fun mPuentes1(ancho: Float, divisiones: Int, tipoVentana: String = "apa"): Float {
        return when (tipoVentana) {
            "apa" -> mPuentes1Aparente(ancho, divisiones)
            "ina", "piv" -> mPuentes1Inaparente(ancho, divisiones)
            else -> mPuentes1Aparente(ancho, divisiones)
        }
    }

    fun mPuentes2Aparente(ancho: Float, divisiones: Int): Float {
        val parantes = 2.5f
        return when (divisiones) {
            10 -> (ancho - (2 * parantes)) / divisiones * 4
            14 -> (ancho - (2 * parantes)) / divisiones * 4
            else -> 0f
        }
    }

    fun mPuentes2Inaparente(ancho: Float, divisiones: Int): Float {
        val parantes = 2.5f
        return when (divisiones) {
            14 -> (ancho - (2 * parantes)) / divisiones * 4
            else -> 0f  // En INA, división 10 no usa mPuentes2
        }
    }

    fun mPuentes2(ancho: Float, divisiones: Int, tipoVentana: String = "apa"): Float {
        return when (tipoVentana) {
            "apa" -> mPuentes2Aparente(ancho, divisiones)
            "ina", "piv" -> mPuentes2Inaparente(ancho, divisiones)
            else -> mPuentes2Aparente(ancho, divisiones)
        }
    }
    // ==================== FUNCIONES DE U ====================
    /**
     * Calcula U fijos para APARENTE
     * Fórmula: ((ancho - (2.5 * (nPuentes - 1))) + cruceTotal) / divisiones
     */
    fun uFijosAparente(ancho: Float, divisiones: Int, cruce: Float): Float {
        val cruceTotal = when (divisiones) {
            2, 3, 5, 7, 9, 11, 13, 15 -> divisiones - 1
            4, 6, 10 -> divisiones - 2
            8, 12 -> divisiones / 2
            14 -> divisiones - 4
            else -> divisiones - 1
        } * cruce
        val partes = ((ancho - (2.5f * (nPuentes(divisiones) - 1))) + cruceTotal) / divisiones
        return if (divisiones == 1) ancho else partes
    }

    /**
     * Calcula U fijos para INAPARENTE/PIVOTANTE
     * Fórmula: (ancho + cruceTotal) / divisiones
     */
    fun uFijosInaparente(ancho: Float, divisiones: Int, cruce: Float): Float {
        val cruceTotal = when (divisiones) {
            2, 3, 5, 7, 9, 11, 13, 15 -> divisiones - 1
            4, 6, 10 -> divisiones - 2
            8, 12 -> divisiones / 2
            14 -> divisiones - 4
            else -> divisiones - 1
        } * cruce
        val partes = (ancho + cruceTotal) / divisiones
        return if (divisiones == 1) ancho else partes
    }

    /**
     * Wrapper para compatibilidad - usa tipo para elegir fórmula
     */
    fun uFijos(ancho: Float, divisiones: Int, cruce: Float, tipoVentana: String = "apa"): Float {
        return when (tipoVentana) {
            "apa" -> uFijosAparente(ancho, divisiones, cruce)
            "ina", "piv" -> uFijosInaparente(ancho, divisiones, cruce)
            else -> uFijosAparente(ancho, divisiones, cruce)
        }
    }
    fun fijoUParante(divisiones: Int): Int {
        return when (divisiones) {
            1 -> 2
            2 -> 1
            3, 4, 5, 7, 9, 11, 13, 15 -> 2
            6, 8, 10, 12, 14 -> when (divisiones) {
                6, 8 -> 4
                10, 14 -> 6
                12 -> 6
                else -> 2
            }
            else -> 0
        }
    }
    fun mochetaUParante(divisiones: Int): Int {
        return when (divisiones) {
            1, 2, 3, 4, 5, 7, 9, 11, 13, 15 -> 2
            6, 8 -> 4
            10, 12, 14 -> 6
            else -> 0
        }
    }
    // ==================== FUNCIONES DE PORTAFELPA ====================
    fun divDePortas(divisiones: Int, nCorredizas: Int): Int {
        return when (divisiones) {
            1 -> 0
            2, 4, 8, 12 -> nCorredizas * 3
            14 -> (nCorredizas * 4) - 2
            else -> nCorredizas * 4
        }
    }
    fun portafelpa(altoHoja: Float): Float {
        return altoHoja - 1.6f
    }
    // ==================== FUNCIONES DE MOCHETA ====================
    fun anchMota(mPuentes: Float): Int {
        return when (mPuentes) {
            in 0.0..180.0 -> 1
            in 180.0..360.0 -> 2
            in 360.0..540.0 -> 3
            in 540.0..720.0 -> 4
            in 720.0..900.0 -> 5
            else -> 0
        }
    }
    fun diviMocheta(x: Float, nMochetasManual: Int): Int {
        require(x > 0) { "El valor debe ser positivo y mayor que cero." }
        return if (nMochetasManual == 0) {
            ceil(x / 240.0).toInt()
        } else {
            nMochetasManual
        }
    }
    fun calcularCruce(cruceExacto: Float, divisiones: Int): Float {
        val cruceDefault = if (divisiones == 4 || divisiones == 8 || divisiones > 12) {
            0.8f
        } else {
            0.7f
        }
        return if (cruceExacto == 0f) cruceDefault else cruceExacto
    }
    fun ancho (ancho: Float): Float {
        return ancho
    }
    fun alto (alto: Float): Float {
        return alto
    }
}