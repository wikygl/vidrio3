package crystal.crystal.taller.nova

/**
 * Clase que maneja todos los cÃ¡lculos relacionados con vidrios
 * Centraliza la lÃ³gica comÃºn entre NovaApa y NovaIna
 */
object NovaVidriosHelper {

    // ==================== FUNCIONES DE VIDRIOS FIJOS ====================

    fun calcularVidriosFijos(
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
            "aparente" -> calcularVidriosFijosAparente(uFijos, altoVidrio, nFijos, divisiones)
            "inaparente" -> calcularVidriosFijosInaparente(uFijos, altoVidrio, nFijos, divisiones)
            else -> calcularVidriosFijosAparente(uFijos, altoVidrio, nFijos, divisiones)
        }
    }

    private fun calcularVidriosFijosAparente(
        uFijos: Float,
        altoVidrio: Float,
        nFijos: Int,
        divisiones: Int
    ): String {
        return when {
            divisiones == 1 -> {
                "${NovaCalculos.df1(uFijos - 0.6f)} x ${NovaCalculos.df1(altoVidrio)} = $nFijos"
            }
            divisiones < 5 -> {
                "${NovaCalculos.df1(uFijos - 0.4f)} x ${NovaCalculos.df1(altoVidrio)} = $nFijos"
            }
            divisiones % 2 == 0 && divisiones < 14 -> {
                "${NovaCalculos.df1(uFijos - 0.4f)} x ${NovaCalculos.df1(altoVidrio)} = $nFijos"
            }
            divisiones == 14 -> {
                "${NovaCalculos.df1(uFijos - 0.4f)} x ${NovaCalculos.df1(altoVidrio)} = 6\n" +
                        "${NovaCalculos.df1(uFijos)} x ${NovaCalculos.df1(altoVidrio)} = 2"
            }
            else -> {
                "${NovaCalculos.df1(uFijos - 0.4f)} x ${NovaCalculos.df1(altoVidrio)} = 2\n" +
                        "${NovaCalculos.df1(uFijos)} x ${NovaCalculos.df1(altoVidrio)} = ${nFijos - 2}"
            }
        }
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
            divisiones < 5 -> {
                "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = $nFijos"
            }
            divisiones == 6 || divisiones == 8 -> {
                "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = 2\n" +
                        "${NovaCalculos.df1(uFijos2)} x ${NovaCalculos.df1(altoVidrio)} = 2"
            }
            divisiones == 10 -> {
                "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = 2\n" +
                        "${NovaCalculos.df1(uFijos2)} x ${NovaCalculos.df1(altoVidrio)} = 2\n" +
                        "${NovaCalculos.df1(uFijos)} x ${NovaCalculos.df1(altoVidrio)} = 2"
            }
            divisiones == 12 -> {
                "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = 2\n" +
                        "${NovaCalculos.df1(uFijos2)} x ${NovaCalculos.df1(altoVidrio)} = 4"
            }
            divisiones == 14 -> {
                "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = 2\n" +
                        "${NovaCalculos.df1(uFijos2)} x ${NovaCalculos.df1(altoVidrio)} = 4\n" +
                        "${NovaCalculos.df1(uFijos)} x ${NovaCalculos.df1(altoVidrio)} = 2"
            }
            else -> {
                "${NovaCalculos.df1(uFijos4)} x ${NovaCalculos.df1(altoVidrio)} = 2\n" +
                        "${NovaCalculos.df1(uFijos)} x ${NovaCalculos.df1(altoVidrio)} = ${nFijos - 2}"
            }
        }
    }

    // ==================== FUNCIONES DE VIDRIOS CORREDIZOS ====================

    fun calcularVidriosCorredizos(
        hache: Float,
        altoHoja: Float,
        nCorredizas: Int
    ): String {
        val anchoVidrio = hache - 1.4f
        val altoVidrio = altoHoja - 3.5f
        return "${NovaCalculos.df1(anchoVidrio)} x ${NovaCalculos.df1(altoVidrio)} = $nCorredizas"
    }

    // ==================== FUNCIONES DE VIDRIOS MOCHETA ====================

    fun calcularVidriosMocheta(
        divisiones: Int,
        alto: Float,
        ancho: Float,
        altoHoja: Float,
        mPuentes1: Float,
        mPuentes2: Float,
        nVidriosMoch: Int,
        nVidriosMoch2: Int,
        anchMota: Int,
        anchMota2: Int,
        tipoVentana: String = "aparente"
    ): String {

        return when (tipoVentana) {
            "aparente" -> calcularVidriosMochetaAparente(
                divisiones, mPuentes1, mPuentes2, nVidriosMoch, nVidriosMoch2, anchMota, anchMota2
            )
            "inaparente" -> calcularVidriosMochetaInaparente(
                divisiones, alto, ancho, altoHoja, nVidriosMoch, anchMota
            )
            else -> calcularVidriosMochetaAparente(
                divisiones, mPuentes1, mPuentes2, nVidriosMoch, nVidriosMoch2, anchMota, anchMota2
            )
        }
    }

    private fun calcularVidriosMochetaAparente(
        divisiones: Int,
        mPuentes1: Float,
        mPuentes2: Float,
        nVidriosMoch: Int,
        nVidriosMoch2: Int,
        anchMota: Int,
        anchMota2: Int
    ): String {
        val vAltoMocheta = calcularVAltoMocheta() // NecesitarÃ¡s pasar los parÃ¡metros necesarios
        val vAnchoMocheta1 = calcularVAnchoMocheta1(mPuentes1, nVidriosMoch, anchMota)
        val vidrioAnchoMocheta2 = calcularVidrioAnchoMocheta2(mPuentes2, nVidriosMoch2, anchMota2)

        return if (divisiones == 1) {
            "${NovaCalculos.df1(vAnchoMocheta1)} x ${NovaCalculos.df1(vAltoMocheta)} = $anchMota"
        } else if (divisiones == 10 || divisiones == 14) {
            "${NovaCalculos.df1(vAnchoMocheta1)} x ${NovaCalculos.df1(vAltoMocheta)} = $nVidriosMoch\n" +
                    "${NovaCalculos.df1(vidrioAnchoMocheta2)} x ${NovaCalculos.df1(vAltoMocheta)} = $nVidriosMoch2"
        } else {
            "${NovaCalculos.df1(vAnchoMocheta1)} x ${NovaCalculos.df1(vAltoMocheta)} = $nVidriosMoch"
        }
    }

    private fun calcularVidriosMochetaInaparente(
        divisiones: Int,
        alto: Float,
        ancho: Float,
        altoHoja: Float,
        nVidriosMoch: Int,
        anchMota: Int
    ): String {
        val mas1 = (alto - altoHoja) + 1
        // AquÃ­ necesitarÃ­as implementar la lÃ³gica especÃ­fica para inaparente
        // basÃ¡ndose en tu cÃ³digo original
        return "Implementar lógica inaparente"
    }

    // ==================== FUNCIONES AUXILIARES ====================

    private fun calcularVAltoMocheta(
        altoMocheta: Float = 0f,
        puente: String = "Múltiple",
        us: Float = 0f
    ): Float {
        val holgura = 0.36f
        return if (puente == "Múltiple" || puente == "gorrito") {
            altoMocheta - holgura
        } else {
            altoMocheta - us - holgura
        }
    }

    private fun calcularVAnchoMocheta1(
        mPuentes1: Float,
        nVidriosMoch: Int,
        anchMota: Int
    ): Float {
        val holgura = (nVidriosMoch + 1) * 0.36f
        return (mPuentes1 - holgura) / anchMota
    }

    private fun calcularVidrioAnchoMocheta2(
        mPuentes2: Float,
        nVidriosMoch2: Int,
        anchMota2: Int
    ): Float {
        val holgura = (nVidriosMoch2 + 1) * 0.36f
        return (mPuentes2 - holgura) / anchMota2
    }

    // ==================== FUNCIÃ“N PRINCIPAL PARA GENERAR TEXTO DE VIDRIOS ====================

    fun generarTextoVidrios(
        texto: String,
        alto: Float,
        hoja: Float,
        divisiones: Int,
        vidriosFijos: String,
        vidrioCorre: String,
        vidrioMocheta: String
    ): String {
        return when (texto) {
            "nn", "nl" -> {
                if (hoja < alto) {
                    if (divisiones > 1) {
                        "$vidriosFijos\n$vidrioCorre\n$vidrioMocheta"
                    } else {
                        "$vidriosFijos\n$vidrioMocheta"
                    }
                } else {
                    if (divisiones > 1) {
                        "$vidriosFijos\n$vidrioCorre"
                    } else {
                        vidriosFijos
                    }
                }
            }
            "ncc", "n3c" -> {
                if (hoja < alto) {
                    "$vidrioCorre\n$vidrioMocheta"
                } else {
                    vidrioCorre
                }
            }
            "ncfc" -> {
                if (hoja < alto) {
                    "$vidriosFijos\n$vidrioCorre\n$vidrioMocheta"
                } else {
                    "$vidriosFijos\n$vidrioCorre"
                }
            }
            "nu", "ns", "ncu", "nci" -> ""
            else -> ""
        }
    }
}