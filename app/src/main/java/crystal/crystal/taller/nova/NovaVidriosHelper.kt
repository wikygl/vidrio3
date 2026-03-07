package crystal.crystal.taller.nova

/**
 * Calculos de vidrios usados por NovaCorrediza.
 * Mantiene solo rutas activas para evitar codigo muerto.
 */
object NovaVidriosHelper {

    fun calcularVidriosFijos(
        ancho: Float,
        uFijos: Float,
        alto: Float,
        us: Float,
        nFijos: Int,
        divisiones: Int,
        tipoVentana: String = "aparente"
    ): String {
        val holgura = if (us == 0f) 1f else 0.2f
        val altoVidrio = alto - (us + holgura)

        return when (tipoVentana) {
            "aparente" -> calcularVidriosFijosAparente(ancho, uFijos, altoVidrio, divisiones)
            "inaparente" -> calcularVidriosFijosInaparente(uFijos, altoVidrio, nFijos, divisiones)
            else -> calcularVidriosFijosAparente(ancho, uFijos, altoVidrio, divisiones)
        }
    }

    private fun calcularVidriosFijosAparente(
        ancho: Float,
        uFijos: Float,
        altoVidrio: Float,
        divisiones: Int
    ): String {
        if (divisiones <= 0) return ""

        val grupos = NovaCalculos.gruposDivisionesPorTramo(ancho, divisiones)
        var cantidadUfijos = 0
        var cantidadUfijosMenos04 = 0

        for (nDivTramo in grupos) {
            val fijosTramo = NovaCalculos.ordenDivis(nDivTramo, nDivTramo.toFloat()).count { it == 'f' }
            if (nDivTramo == 5) {
                if (fijosTramo > 0) {
                    cantidadUfijos += 1
                    cantidadUfijosMenos04 += (fijosTramo - 1).coerceAtLeast(0)
                }
            } else {
                cantidadUfijosMenos04 += fijosTramo
            }
        }

        val lineas = mutableListOf<String>()
        if (cantidadUfijosMenos04 > 0) {
            lineas.add("${NovaCalculos.df1(uFijos - 0.4f)} x ${NovaCalculos.df1(altoVidrio)} = $cantidadUfijosMenos04")
        }
        if (cantidadUfijos > 0) {
            lineas.add("${NovaCalculos.df1(uFijos)} x ${NovaCalculos.df1(altoVidrio)} = $cantidadUfijos")
        }
        return lineas.joinToString("\n")
    }

    private fun calcularVidriosFijosInaparente(
        uFijos: Float,
        altoVidrio: Float,
        nFijos: Int,
        divisiones: Int
    ): String {
        val uFijos4 = uFijos - 0.4f
        val uFijos2 = uFijos - 0.2f

        return when {
            divisiones < 5 -> "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = $nFijos"
            // Regla explícita por franja 5: 3 fijos = 2 (u-0.4) + 1 (u)
            divisiones == 5 -> "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = 2\n${NovaCalculos.df1(uFijos)} x ${NovaCalculos.df1(altoVidrio)} = 1"
            divisiones == 6 || divisiones == 8 -> "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = 2\n${NovaCalculos.df1(uFijos2)} x ${NovaCalculos.df1(altoVidrio)} = 2"
            divisiones == 10 -> "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = 2\n${NovaCalculos.df1(uFijos2)} x ${NovaCalculos.df1(altoVidrio)} = 2\n${NovaCalculos.df1(uFijos)} x ${NovaCalculos.df1(altoVidrio)} = 2"
            divisiones == 12 -> "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = 2\n${NovaCalculos.df1(uFijos2)} x ${NovaCalculos.df1(altoVidrio)} = 4"
            divisiones == 14 -> "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = 2\n${NovaCalculos.df1(uFijos2)} x ${NovaCalculos.df1(altoVidrio)} = 4\n${NovaCalculos.df1(uFijos)} x ${NovaCalculos.df1(altoVidrio)} = 2"
            else -> "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = 2\n${NovaCalculos.df1(uFijos)} x ${NovaCalculos.df1(altoVidrio)} = ${nFijos - 2}"
        }
    }

    fun calcularVidriosCorredizos(
        hache: Float,
        altoHoja: Float,
        nCorredizas: Int
    ): String {
        val anchoVidrio = hache - 1.4f
        val altoVidrio = altoHoja - 3.5f
        return "${NovaCalculos.df1(anchoVidrio)} x ${NovaCalculos.df1(altoVidrio)} = $nCorredizas"
    }

    fun calcularVidrioMochetaAparente(
        ancho: Float,
        alto: Float,
        altoHoja: Float,
        divisiones: Int,
        tubo: Float,
        puente: String,
        us: Float,
        modelo: String = "nn"
    ): String {
        val holguraAncho = 0.4f
        val holguraAlto = 0.36f
        val alturasMochetas = if (modelo == "np") {
            NovaInaCalculos.alturasMochetasPorModelo(
                modelo = modelo,
                alto = alto,
                altoHoja = altoHoja,
                alturaPuente = tubo
            ).lista()
        } else {
            listOf(NovaCalculos.altoMocheta(alto, altoHoja, tubo))
        }

        val gruposDivs = NovaCalculos.gruposDivisionesMochetaPorModelo(ancho, divisiones, modelo)
        val anchoPorDiv = ancho / divisiones
        val anchosTramoBruto = gruposDivs.map { nDivs -> nDivs * anchoPorDiv }
        val nParantes = (gruposDivs.size - 1).coerceAtLeast(0)
        val paranteAncho = if (puente == "Múltiple" || puente == "gorrito") 2.5f else tubo
        val anchoTotalAjustado = (ancho - (nParantes * paranteAncho)).coerceAtLeast(0f)
        val sumaBruta = anchosTramoBruto.sum()
        val anchosTramoAjustado = if (sumaBruta > 0f) {
            anchosTramoBruto.map { (it / sumaBruta) * anchoTotalAjustado }
        } else {
            anchosTramoBruto
        }

        val lineas = linkedMapOf<String, Int>()
        for (idx in gruposDivs.indices) {
            val anchoSeccion = anchosTramoAjustado[idx]
            val anchMotaTramo = NovaCalculos.anchMota(anchoSeccion)
            val anchoVidrio = (anchoSeccion / anchMotaTramo) - holguraAncho
            for (altoMocheta in alturasMochetas) {
                val vAltoMocheta = if (puente == "Múltiple" || puente == "gorrito") {
                    altoMocheta - holguraAlto
                } else {
                    altoMocheta - us - holguraAlto
                }
                val key = "${NovaCalculos.df1(anchoVidrio)} x ${NovaCalculos.df1(vAltoMocheta)}"
                lineas[key] = (lineas[key] ?: 0) + anchMotaTramo
            }
        }

        return lineas.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }
}
