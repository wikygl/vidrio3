package crystal.crystal.taller.nova

/**
 * Objeto con todos los cálculos específicos de Nova Inaparente
 * Lógica trasladada directamente de vidrio3/NovaIna.kt
 */
object NovaInaCalculos {

    // ==================== FUNCIONES DE FORMATO ====================
    fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    // ==================== FUNCIONES DE U ====================
    fun uFijos(ancho: Float, divisiones: Int, cruce: Float): Float {
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

    fun uParante(alto: Float, us: Float): Float {
        return alto - (2 * us)
    }

    fun uParante2(alto: Float, altoHoja: Float, us: Float): Float {
        return ((alto - altoHoja) - us) + 1.5f
    }

    fun uSuperior(ancho: Float): Float {
        return ancho
    }

    // ==================== FUNCIONES DE CRUCE ====================
    fun cruce(cruceExacto: Float, divisiones: Int): Float {
        val cruceDefault = if (divisiones == 4 || divisiones == 8 || divisiones > 12) {
            0.8f
        } else {
            0.7f
        }
        return if (cruceExacto == 0f) cruceDefault else cruceExacto
    }

    // ==================== FUNCIONES DE PUENTES ====================
    fun nPuentes(divisiones: Int): Int {
        return when (divisiones) {
            1, 2, 3, 4, 5, 7, 9, 11, 13, 15 -> 1
            6, 8, 10 -> 2
            12, 14 -> 3
            else -> 0
        }
    }

    fun mPuentes(ancho: Float, divisiones: Int): Float {
        val parantes = 2.5f
        return when (divisiones) {
            1, 2, 3, 4, 5, 7, 9, 11, 13, 15 -> ancho
            6, 8, 10 -> (ancho - parantes) / 2
            12 -> (ancho - (2 * parantes)) / 3
            14 -> (ancho - (2 * parantes)) / divisiones * 5
            else -> 0f
        }
    }

    fun mPuentes2(ancho: Float, divisiones: Int): Float {
        val parantes = 2.5f
        return when (divisiones) {
            14 -> (ancho - (2 * parantes)) / divisiones * 4
            else -> 0f
        }
    }

    // ==================== FUNCIONES DE RIELES ====================
    fun rieles(alto: Float, hoja: Float, ancho: Float, divisiones: Int): String {
        val mPuentesVal = mPuentes(ancho, divisiones)
        val mPuentes6 = df1(mPuentesVal - 0.06f).toFloat()
        val mPuentesRounded = df1(mPuentesVal).toFloat()
        val mPuentes2Val = df1(mPuentes2(ancho, divisiones)).toFloat()
        val ancho6 = df1(ancho - 0.06f).toFloat()
        val nPuentesVal = nPuentes(divisiones)

        return if (alto >= hoja) {
            if (divisiones != 14) {
                "${df1(mPuentes6)} = $nPuentesVal"
            } else {
                "${df1(mPuentesRounded)} = ${nPuentesVal - 1}\n" +
                        "${df1(mPuentes2Val)} = ${nPuentesVal - 2}"
            }
        } else {
            "${df1(ancho6)} = 1"
        }
    }

    fun puentes(alto: Float, ancho: Float, divisiones: Int): String {
        val mPuentesVal = mPuentes(ancho, divisiones)
        val mPuentes6 = df1(mPuentesVal - 0.06f).toFloat()
        val mPuentes2Val = df1(mPuentes2(ancho, divisiones)).toFloat()
        val nPuentesVal = nPuentes(divisiones)

        return when {
            divisiones in 6..12 && divisiones % 2 == 0 -> {
                "${df1(mPuentes6)} = $nPuentesVal\n" +
                        "${df1(alto)} = ${nPuentesVal - 1}"
            }
            divisiones == 14 -> {
                "${df1(mPuentes6)} = ${nPuentesVal - 1}\n" +
                        "${df1(mPuentes2Val)} = ${nPuentesVal - 2}\n" +
                        "${df1(alto)} = ${nPuentesVal - 1}"
            }
            else -> {
                "${df1(mPuentes6)} = $nPuentesVal"
            }
        }
    }

    // ==================== FUNCIONES DE OTROS PERFILES ====================
    fun portafelpa(altoHoja: Float): Float {
        return df1(altoHoja - 1.6f).toFloat()
    }

    fun hache(ancho: Float, divisiones: Int, cruce: Float): Float {
        return df1(uFijos(ancho, divisiones, cruce)).toFloat()
    }

    fun divDePortas(divisiones: Int, nCorredizas: Int): String {
        return when (divisiones) {
            1 -> ""
            2, 4, 8, 12 -> "${nCorredizas * 3}"
            14 -> "${(nCorredizas * 4) - 2}"
            else -> "${nCorredizas * 4}"
        }
    }

    // ==================== FUNCIONES DE CONSTANTES ====================
    fun altoHoja(alto: Float, hoja: Float): Float {
        val corre = if (hoja > alto) alto else hoja
        return if (hoja == 0f) alto / 7 * 5 else corre
    }

    fun nFijos(divisiones: Int): Int {
        return when (divisiones) {
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
    }

    fun nCorredizas(divisiones: Int): Int {
        return when (divisiones) {
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
    }

    fun fijoUParante(divisiones: Int): Int {
        return when (divisiones) {
            1 -> 2
            2 -> 1
            in 3..15 -> 2
            else -> 0
        }
    }

    fun divisiones(ancho: Float, divisManual: Int): Int {
        return if (divisManual == 0) {
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
                else -> divisManual
            }
        } else {
            divisManual
        }
    }

    // ==================== FUNCIONES DE VIDRIOS ====================
    fun vidrioFijo(ancho: Float, alto: Float, us: Float, divisiones: Int, cruce: Float): String {
        val holgura = if (us == 0f) 1f else 0.2f
        val uFijosVal = uFijos(ancho, divisiones, cruce)
        val uFijos = df1(uFijosVal).toFloat()
        val uFijos4 = df1(uFijosVal - 0.4f).toFloat()
        val uFijos2 = df1(uFijosVal - 0.2f).toFloat()
        val altDes = df1(alto - (us + holgura)).toFloat()
        val nFijosVal = nFijos(divisiones)

        return when {
            divisiones < 5 -> {
                "${df1(uFijos4)} x ${df1(altDes)} = $nFijosVal"
            }
            divisiones == 6 || divisiones == 8 -> {
                "${df1(uFijos4)} x ${df1(altDes)} = 2\n" +
                        "${df1(uFijos2)} x ${df1(altDes)} = 2"
            }
            divisiones == 10 -> {
                "${df1(uFijos4)} x ${df1(altDes)} = 2\n" +
                        "${df1(uFijos2)} x ${df1(altDes)} = 2\n" +
                        "${df1(uFijos)} x ${df1(altDes)} = 2"
            }
            divisiones == 12 -> {
                "${df1(uFijos4)} x ${df1(altDes)} = 2\n" +
                        "${df1(uFijos2)} x ${df1(altDes)} = 4"
            }
            divisiones == 14 -> {
                "${df1(uFijos4)} x ${df1(altDes)} = 2\n" +
                        "${df1(uFijos2)} x ${df1(altDes)} = 4\n" +
                        "${df1(uFijos)} x ${df1(altDes)} = 2"
            }
            else -> {
                "${df1(uFijos4)} x ${df1(altDes)} = 2\n" +
                        "${df1(uFijos)} x ${df1(altDes)}= ${nFijosVal - 2}"
            }
        }
    }

    fun vidrioCorre(ancho: Float, altoHoja: Float, divisiones: Int, cruce: Float): String {
        val hacheVal = hache(ancho, divisiones, cruce)
        val anchoVidrio = df1(hacheVal - 1.4f).toFloat()
        val altoVidrio = df1(altoHoja - 3.5f).toFloat()
        val nCorredizasVal = nCorredizas(divisiones)
        return "${df1(anchoVidrio)} x ${df1(altoVidrio)} = $nCorredizasVal"
    }

    fun vidrioMocheta(ancho: Float, alto: Float, hoja: Float, altoHoja: Float, divisiones: Int, cruce: Float): String {
        val nFijosVal = nFijos(divisiones)
        val nCorredizasVal = nCorredizas(divisiones)
        val uFijosVal = uFijos(ancho, divisiones, cruce)

        val mas1 = df1((alto - altoHoja) + 1).toFloat()
        val axnfxuf = df1(((ancho - (nFijosVal * uFijosVal))) - 0.6f).toFloat()
        val axnfxuf2 = df1(((ancho - (nFijosVal * uFijosVal)) / 2) - 0.6f).toFloat()
        val axnfxuf3 = df1(((ancho - (nFijosVal * uFijosVal)) / 3) - 0.6f).toFloat()
        val axnfxufn = df1(((ancho - (nFijosVal * uFijosVal)) / nCorredizasVal) - 0.6f).toFloat()

        return when {
            divisiones <= 1 || alto <= hoja -> ""
            divisiones == 4 -> "${df1(mas1)} x ${df1(axnfxuf)} = 1"
            divisiones == 8 -> "${df1(mas1)} x ${df1(axnfxuf2)} = 2"
            divisiones == 12 -> "${df1(mas1)} x ${df1(axnfxuf3)} = 3"
            divisiones == 14 -> {
                "${df1(mas1)} x ${df1(axnfxuf2)} = 1\n" +
                        "${df1(mas1)} x ${df1(axnfxuf)} = 4"
            }
            else -> "${df1(mas1)} x ${df1(axnfxufn)} = $nCorredizasVal"
        }
    }

    // ==================== FUNCIONES DE TEXTO U ====================
    fun calcularTextoU(
        ancho: Float,
        alto: Float,
        hoja: Float,
        us: Float,
        divisiones: Int,
        cruceExacto: Float
    ): String {
        val cruce = cruce(cruceExacto, divisiones)
        val altoHojaVal = altoHoja(alto, hoja)

        val uFijosVal = df1(uFijos(ancho, divisiones, cruce)).toFloat()
        val uParanteVal = df1(uParante(alto, us)).toFloat()
        val uParante2Val = df1(uParante2(alto, altoHojaVal, us)).toFloat()
        val uSuperiorVal = df1(uSuperior(ancho)).toFloat()
        val nFijosVal = nFijos(divisiones)
        val fijoUParanteVal = fijoUParante(divisiones)

        return if (alto > altoHojaVal && us != 0F) {
            when {
                divisiones == 2 -> {
                    "${df1(uFijosVal)} = $nFijosVal\n" +
                            "${df1(uParanteVal)} = $fijoUParanteVal\n" +
                            "${df1(uParante2Val)} = 1\n" +
                            "${df1(uSuperiorVal)} = 1"
                }
                divisiones == 1 -> {
                    "${df1(uFijosVal)} = 2\n" +
                            "${df1(uParanteVal)} = 2"
                }
                else -> {
                    "${df1(uFijosVal)} = $nFijosVal\n" +
                            "${df1(uParanteVal)} = $fijoUParanteVal\n" +
                            "${df1(uSuperiorVal)} = 1"
                }
            }
        } else if (alto > altoHojaVal && us == 0F) {
            when {
                divisiones == 2 -> {
                    "${df1(uFijosVal)} = $nFijosVal\n" +
                            "${df1(uSuperiorVal)} = 1"
                }
                divisiones == 1 -> "${df1(uFijosVal)} = 2"
                else -> {
                    "${df1(uFijosVal)} = $nFijosVal\n" +
                            "${df1(uSuperiorVal)} = 1"
                }
            }
        } else if (alto <= altoHojaVal && us != 0F) {
            when {
                divisiones == 2 -> {
                    "${df1(uFijosVal)} = $nFijosVal\n" +
                            "${df1(uParanteVal)} = $fijoUParanteVal\n" +
                            "${df1(uParante2Val)} = 1"
                }
                divisiones == 1 -> {
                    "${df1(uFijosVal)} = 2\n" +
                            "${df1(uParanteVal)} = 2"
                }
                else -> {
                    "${df1(uFijosVal)} = $nFijosVal\n" +
                            "${df1(uParanteVal)} = $fijoUParanteVal"
                }
            }
        } else {
            when {
                divisiones == 2 -> "${df1(uFijosVal)} = $nFijosVal"
                divisiones == 1 -> "${df1(uFijosVal)} = 2"
                else -> "${df1(uFijosVal)} = $nFijosVal"
            }
        }
    }

    // ==================== FUNCIÓN COMPLETA DE OTROS ALUMINIOS ====================
    fun calcularOtrosAluminios(
        ancho: Float,
        alto: Float,
        hoja: Float,
        divisiones: Int,
        cruceExacto: Float
    ): OtrosAluminiosResult {
        val cruce = cruce(cruceExacto, divisiones)
        val altoHojaVal = altoHoja(alto, hoja)
        val nCorredizasVal = nCorredizas(divisiones)
        val nPuentesVal = nPuentes(divisiones)
        val uFijosVal = uFijos(ancho, divisiones, cruce)
        val hacheVal = hache(ancho, divisiones, cruce)
        val portafelpaVal = portafelpa(altoHojaVal)
        val divDePortasVal = divDePortas(divisiones, nCorredizasVal)

        val textoPuentes = puentes(alto, ancho, divisiones)
        val textoRieles = if (divisiones == 1) "" else rieles(alto, hoja, ancho, divisiones)
        val textoUFelpero = if (divisiones == 1) "" else rieles(alto, hoja, ancho, divisiones)
        val textoHache = "${df1(hacheVal)} = $nCorredizasVal"
        val textoAngTope = if (divisiones == 2) "${df1(altoHojaVal - 0.9f)} = 1" else ""
        val textoPortafelpa = "${df1(portafelpaVal)} = $divDePortasVal"

        return OtrosAluminiosResult(
            puentes = textoPuentes,
            rieles = textoRieles,
            uFelpero = textoUFelpero,
            hache = textoHache,
            angTope = textoAngTope,
            portafelpa = textoPortafelpa,
            mostrarAngTope = divisiones == 2,
            mostrarHache = divisiones != 1,
            mostrarUFelpero = divisiones != 1 && alto > hoja
        )
    }

    // ==================== FUNCIÓN COMPLETA DE VIDRIOS ====================
    fun calcularVidrios(
        ancho: Float,
        alto: Float,
        hoja: Float,
        us: Float,
        divisiones: Int,
        cruceExacto: Float
    ): String {
        val cruce = cruce(cruceExacto, divisiones)
        val altoHojaVal = altoHoja(alto, hoja)

        val vidriosFijos = vidrioFijo(ancho, alto, us, divisiones, cruce)
        val vidriosCorre = vidrioCorre(ancho, altoHojaVal, divisiones, cruce)
        val vidriosMocheta = vidrioMocheta(ancho, alto, hoja, altoHojaVal, divisiones, cruce)

        return if (divisiones > 1) {
            if (alto > hoja && vidriosMocheta.isNotEmpty()) {
                "$vidriosFijos\n$vidriosCorre\n$vidriosMocheta"
            } else {
                "$vidriosFijos\n$vidriosCorre"
            }
        } else {
            vidriosFijos
        }
    }

    // ==================== DATA CLASS PARA RESULTADOS ====================
    data class OtrosAluminiosResult(
        val puentes: String,
        val rieles: String,
        val uFelpero: String,
        val hache: String,
        val angTope: String,
        val portafelpa: String,
        val mostrarAngTope: Boolean,
        val mostrarHache: Boolean,
        val mostrarUFelpero: Boolean
    )
}
