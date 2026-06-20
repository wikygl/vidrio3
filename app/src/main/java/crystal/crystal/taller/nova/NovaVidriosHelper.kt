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
        tipoVentana: String = "aparente",
        modelo: String = "nn"
    ): String {
        val holgura = if (us == 0f) 1f else 0.2f
        val altoVidrio = alto - (us + holgura)

        return when (tipoVentana) {
            "aparente" -> calcularVidriosFijosAparente(ancho, uFijos, altoVidrio, divisiones, modelo)
            "inaparente" -> calcularVidriosFijosInaparente(uFijos, altoVidrio, nFijos, divisiones)
            else -> calcularVidriosFijosAparente(ancho, uFijos, altoVidrio, divisiones, modelo)
        }
    }

    private fun calcularVidriosFijosAparente(
        ancho: Float,
        uFijos: Float,
        altoVidrio: Float,
        divisiones: Int,
        modelo: String = "nn"
    ): String {
        if (divisiones <= 0) return ""

        // Full fijos: todos los paños son fijos e iguales; una sola línea con cantidad = divisiones.
        if (modelo == "nff") {
            return "${NovaCalculos.df1(uFijos - 0.4f)} x ${NovaCalculos.df1(altoVidrio)} = $divisiones"
        }

        // ncfc: ancho de cada fijo por COLINDANCIA (pared/parante 0.4, fijo 0.2, corrediza 0, por
        // lado). Un fijo entre dos corredizas queda lleno; pegado a otro fijo descuenta 0.2; etc.
        if (modelo == "ncfc") {
            val tramos = NovaCalculos.patronModulosNcfc(ancho, divisiones)
            return NovaCalculos.descuentosVidrioFijos(tramos).entries.joinToString("\n") { (desc, cant) ->
                "${NovaCalculos.df1(uFijos - desc)} x ${NovaCalculos.df1(altoVidrio)} = $cant"
            }
        }

        val grupos = NovaCalculos.gruposDivisionesPorTramo(ancho, divisiones)
        var cantidadUfijos = 0
        var cantidadUfijosMenos04 = 0

        for (nDivTramo in grupos) {
            val fijosTramo = if (modelo == "nff") nDivTramo else {
                NovaCalculos.ordenDivis(nDivTramo, nDivTramo.toFloat()).count { it == 'f' }
            }
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
        modelo: String = "nn",
        mochetaInferior: Float = 0f,
        remate: String = modelo
    ): String {
        // `modelo` = modulación (patrón); `remate` = remate de mochetas (nn/nr/np).
        val holguraAncho = 0.5f
        val holguraAlto = 0.3f
        val alturasMochetas = if (remate == "np") {
            NovaInaCalculos.alturasMochetasPorModelo(
                modelo = remate,
                alto = alto,
                altoHoja = altoHoja,
                alturaPuente = tubo,
                mochetaInferior = mochetaInferior
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
            // anchoSeccion es el ancho ÚTIL del tramo (ya descontados parantes y esquina); ese
            // es el que decide cuántos paños se parte la mocheta (anchMota).
            val anchoSeccion = anchosTramoAjustado[idx]
            val anchMotaTramo = NovaCalculos.anchMota(anchoSeccion)
            val anchoVidrio = (anchoSeccion / anchMotaTramo) - holguraAncho
            for (altoMocheta in alturasMochetas) {
                val vAltoMocheta = if (puente == "Múltiple" || puente == "gorrito") {
                    altoMocheta - (holguraAlto+0.2f)
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
