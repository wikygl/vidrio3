package crystal.crystal.taller.nova

import android.annotation.SuppressLint

/**
 * Clase que maneja todos los cálculos relacionados con perfiles de aluminio
 * Centraliza la lógica común entre NovaApa y NovaIna
 */
object NovaPerfilesHelper {

    data class OtrosAparenteResult(
        val puentes: String,
        val mostrarPuentes: Boolean,
        val rieles: String,
        val mostrarRieles: Boolean,
        val tuboTxt: String,
        val mostrarTubo: Boolean,
        val portafelpaTxt: String,
        val mostrarPortafelpa: Boolean,
        val teeTxt: String,
        val mostrarTee: Boolean,
        val topeTxt: String,
        val mostrarTope: Boolean,
        val hacheTxt: String,
        val mostrarHache: Boolean
    )

    // ==================== FUNCIONES DE U ====================

    fun calcularUParante(alto: Float, us: Float): Float {
        return alto - (us + 0.2f)
    }

    fun calcularUParanteInaparente(alto: Float, us: Float): Float {
        return alto - (2 * us)
    }

    fun calcularUParante2Inaparente(alto: Float, altoHoja: Float, us: Float): Float {
        return ((alto - altoHoja) - us) + 1.5f
    }

    fun calcularUMocheta(altoMocheta: Float, us: Float): Float {
        return altoMocheta - (2f * us)
    }

    fun calcularUSuperior(mPuentes1: Float): Float {
        return mPuentes1
    }

    fun calcularUSuperior2(mPuentes2: Float): Float {
        return mPuentes2
    }

    // ==================== FUNCIONES DE TEXTO U ====================

    @SuppressLint("SetTextI18n")
    fun generarTextoU(
        texto: String,
        alto: Float,
        hoja: Float,
        us: Float,
        divisiones: Int,
        uFijos: Float,
        uParante: Float,
        uMocheta: Float,
        uSuperior: Float,
        uSuperior2: Float,
        nFijos: Int,
        fijoUParante: Int,
        mochetaUParante: Int,
        nPuentes: Int,
        puente: String = "Múltiple"
    ): String {
        val valorFinal = if (puente == "Múltiple" || puente == "gorrito") 1 else 2

        return when (texto) {
            "nn", "nl" -> {
                if (alto > hoja && us != 0F) {
                    when {
                        divisiones == 1 -> {
                            "${NovaCalculos.df1(uFijos)} = 2\n" +
                                    "${NovaCalculos.df1(uParante)} = 2\n" +
                                    "${NovaCalculos.df1(uMocheta)} = $mochetaUParante"
                        }
                        divisiones == 10 || divisiones == 14 -> {
                            "${NovaCalculos.df1(uFijos)} = $nFijos\n" +
                                    "${NovaCalculos.df1(uParante)} = $fijoUParante\n" +
                                    "${NovaCalculos.df1(uMocheta)} = $mochetaUParante\n" +
                                    "${NovaCalculos.df1(uSuperior)} = ${nPuentes - 1}\n" +
                                    "${NovaCalculos.df1(uSuperior2)} = ${(nPuentes - 2) * valorFinal}"
                        }
                        else -> {
                            "${NovaCalculos.df1(uFijos)} = $nFijos\n" +
                                    "${NovaCalculos.df1(uParante)} = $fijoUParante\n" +
                                    "${NovaCalculos.df1(uMocheta)} = $mochetaUParante\n" +
                                    "${NovaCalculos.df1(uSuperior)} = ${nPuentes * valorFinal}"
                        }
                    }
                } else if (alto > hoja && us == 0F) {
                    when {
                        divisiones == 1 -> "${NovaCalculos.df1(uFijos)} = 2"
                        divisiones == 10 || divisiones == 14 -> {
                            "${NovaCalculos.df1(uFijos)} = $nFijos\n" +
                                    "${NovaCalculos.df1(uSuperior)} = ${nPuentes - 1}\n" +
                                    "${NovaCalculos.df1(uSuperior2)} = ${nPuentes - 2}"
                        }
                        else -> {
                            "${NovaCalculos.df1(uFijos)} = $nFijos\n" +
                                    "${NovaCalculos.df1(uSuperior)} = $nPuentes"
                        }
                    }
                } else if (alto <= hoja && us != 0F) {
                    when {
                        divisiones == 1 -> {
                            "${NovaCalculos.df1(uFijos)} = 2\n" +
                                    "${NovaCalculos.df1(uParante)} = 2"
                        }
                        else -> {
                            "${NovaCalculos.df1(uFijos)} = $nFijos\n" +
                                    "${NovaCalculos.df1(uParante)} = $fijoUParante"
                        }
                    }
                } else {
                    when {
                        divisiones == 1 -> "${NovaCalculos.df1(uFijos)} = 2"
                        else -> "${NovaCalculos.df1(uFijos)} = $nFijos"
                    }
                }
            }
            "ncc", "n3c" -> {
                if (alto > hoja) {
                    "${NovaCalculos.df1(uMocheta)} = $mochetaUParante\n" +
                            "${NovaCalculos.df1(uSuperior)} = ${nPuentes * valorFinal}"
                } else {
                    ""
                }
            }
            "ncfc" -> {
                if (alto > hoja) {
                    "${NovaCalculos.df1(uFijos)} = 1\n" +
                            "${NovaCalculos.df1(uMocheta)} = $mochetaUParante\n" +
                            "${NovaCalculos.df1(uSuperior)} = ${nPuentes * valorFinal}"
                } else {
                    "${NovaCalculos.df1(uFijos)} = 1"
                }
            }
            "nu", "ns", "ncu", "nci" -> ""
            else -> ""
        }
    }

    // ==================== FUNCIONES DE RIELES ====================

    fun calcularRieles(
        alto: Float,
        hoja: Float,
        ancho: Float,
        mPuentes: Float,
        mPuentes2: Float,
        nPuentes: Int,
        divisiones: Int
    ): String {
        val mPuentes6 = mPuentes - 0.06f
        val ancho6 = ancho - 0.06f

        return if (alto >= hoja) {
            if (divisiones != 14) {
                "${NovaCalculos.df1(mPuentes6)} = $nPuentes"
            } else {
                "${NovaCalculos.df1(mPuentes)} = ${nPuentes - 1}\n" +
                        "${NovaCalculos.df1(mPuentes2)} = ${nPuentes - 2}"
            }
        } else {
            "${NovaCalculos.df1(ancho6)} = 1"
        }
    }

    // ==================== FUNCIONES DE PUENTES ====================

    fun calcularPuentes(
        alto: Float,
        mPuentes: Float,
        mPuentes2: Float,
        nPuentes: Int,
        divisiones: Int
    ): String {
        val mPuentes6 = mPuentes - 0.06f

        return when {
            divisiones in 6..12 && divisiones % 2 == 0 -> {
                "${NovaCalculos.df1(mPuentes6)} = $nPuentes\n" +
                        "${NovaCalculos.df1(alto)} = ${nPuentes - 1}"
            }
            divisiones == 14 -> {
                "${NovaCalculos.df1(mPuentes6)} = ${nPuentes - 1}\n" +
                        "${NovaCalculos.df1(mPuentes2)} = ${nPuentes - 2}\n" +
                        "${NovaCalculos.df1(alto)} = ${nPuentes - 1}"
            }
            else -> {
                "${NovaCalculos.df1(mPuentes6)} = $nPuentes"
            }
        }
    }

    // ==================== FUNCIONES DE TE Y ACCESORIOS ====================

    fun calcularNTe(divisiones: Int, nVidriosMoch: Int, nVidriosMoch2: Int): Int {
        return when {
            divisiones % 2 != 0 || divisiones < 6 -> nVidriosMoch - 1
            divisiones == 6 || divisiones == 8 -> nVidriosMoch - 2
            divisiones == 10 || divisiones == 14 -> (nVidriosMoch - 2) + (nVidriosMoch2 - 1)
            divisiones == 12 -> nVidriosMoch - 3
            else -> 0
        }
    }

    fun calcularHache(uFijos: Float): Float {
        return uFijos
    }

    private fun esPuenteMultipleOGorrito(puente: String): Boolean {
        val p = puente.lowercase().trim()
        return p.contains("multi") || p.contains("múlt") || p.contains("múlt") || p.contains("ltiple") || p.contains("gorrito")
    }

    fun calcularTe(altoMocheta: Float, us: Float, puente: String): Float {
        val factor = if (esPuenteMultipleOGorrito(puente)) 1f else 2f
        return altoMocheta - (factor * us)
    }

    fun calcularOtrosAparente(
        ancho: Float,
        alto: Float,
        hoja: Float,
        altoHoja: Float,
        divisiones: Int,
        nCorredizas: Int,
        nPuentes: Int,
        mPuentes1: Float,
        mPuentes2: Float,
        portafelpa: Float,
        divDePortas: Int,
        uFijos: Float,
        tubo: Float,
        us: Float,
        puente: String,
        modelo: String = "nn",
        mochetaInferior: Float = 0f,
        remate: String = modelo
    ): OtrosAparenteResult {
        // `modelo` = modulación (patrón); `remate` = remate de mochetas (nn/nr/np).
        val puentesTxt = when {
            divisiones in 6..12 && divisiones % 2 == 0 ->
                "${NovaCalculos.df1(mPuentes1)} = $nPuentes\n${NovaCalculos.df1(alto)} = ${nPuentes - 1}"
            divisiones == 14 ->
                "${NovaCalculos.df1(mPuentes1)} = ${nPuentes - 1}\n" +
                        "${NovaCalculos.df1(mPuentes2)} = ${nPuentes - 2}\n" +
                        "${NovaCalculos.df1(alto)} = ${nPuentes - 1}"
            divisiones == 1 -> ""
            else -> "${NovaCalculos.df1(mPuentes1)} = $nPuentes"
        }

        val rielesTxt = if (divisiones == 1 || nCorredizas == 0) {
            ""
        } else {
            calcularRieles(alto, hoja, ancho, mPuentes1, mPuentes2, nPuentes, divisiones)
        }

        val tuboTxt = if (alto > altoHoja) "${NovaCalculos.df1(ancho)} = 1" else ""
        val portafelpaTxt = if (divisiones != 1) "${NovaCalculos.df1(portafelpa)} = $divDePortas" else ""

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
        val paranteAnchoMocheta = if (puente == "Múltiple" || puente == "gorrito") 2.5f else tubo
        val nTeesBase = NovaCalculos.cantidadTeePorTramosMocheta(ancho, divisiones, modelo, paranteAnchoMocheta)
        val teeTxt = if (nTeesBase > 0 && alturasMochetas.isNotEmpty()) {
            val lineas = linkedMapOf<String, Int>()
            for (alturaMocheta in alturasMochetas) {
                val teeVal = calcularTe(alturaMocheta, us, puente)
                if (teeVal <= 0f) continue
                val key = NovaCalculos.df1(teeVal)
                lineas[key] = (lineas[key] ?: 0) + nTeesBase
            }
            lineas.entries.joinToString("\n") { "${it.key} = ${it.value}" }
        } else ""

        val topeTxt = if (divisiones == 2) "${NovaCalculos.df1(altoHoja - 0.9f)} = 1" else ""
        val hacheTxt = if (nCorredizas > 0) "${NovaCalculos.df1(uFijos)} = $nCorredizas" else ""

        return OtrosAparenteResult(
            puentes = puentesTxt,
            mostrarPuentes = divisiones != 1,
            rieles = rielesTxt,
            mostrarRieles = divisiones != 1 && nCorredizas > 0,
            tuboTxt = tuboTxt,
            mostrarTubo = alto > altoHoja,
            portafelpaTxt = portafelpaTxt,
            mostrarPortafelpa = divisiones != 1,
            teeTxt = teeTxt,
            mostrarTee = teeTxt.isNotBlank(),
            topeTxt = topeTxt,
            mostrarTope = divisiones == 2,
            hacheTxt = hacheTxt,
            mostrarHache = nCorredizas > 0
        )
    }

    // ==================== FUNCIÓN DE ETIQUETA U ====================

    @SuppressLint("SetTextI18n")
    fun obtenerEtiquetaU(us: Float): String {
        return when (us) {
            1f -> "u-3/8"
            1.5f -> "u-13"
            else -> "u-${NovaCalculos.df1(us)}"
        }
    }

    // ==================== FUNCIÓN DE ETIQUETA U MEJORADA ====================

    @SuppressLint("SetTextI18n")
    fun configurarEtiquetaYTextoU(
        us: Float,
        tvU: android.widget.TextView,
        txU: android.widget.TextView,
        textoUCalculado: String
    ) {
        // Configurar la etiqueta del TextView según el valor de U
        tvU.text = when (us) {
            1f -> "u-3/8"
            1.5f -> "u-13"
            else -> "u-${NovaCalculos.df1(us)}"
        }

        // Configurar el texto calculado
        txU.text = textoUCalculado
    }

    // ==================== FUNCIÓN UNIFICADA PARA MANEJAR TODOS LOS TIPOS DE U ====================

    @SuppressLint("SetTextI18n")
    fun calcularYConfigurarTextosU(
        us: Float,
        ancho: Float,
        alto: Float,
        hoja: Float,
        divisiones: Int,
        cruce: Float,
        altoHoja: Float,
        tipoVentana: String = "ina",
        bindings: BindingsU
    ) {
        val nFijos = NovaCalculos.nFijos(divisiones)
        val fijoUParante = NovaCalculos.fijoUParante(divisiones)
        val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruce, tipoVentana)
        val mPuentes = NovaCalculos.mPuentes1(ancho, divisiones, tipoVentana)
        val uSuperior = calcularUSuperior(mPuentes)

        val uParante = when (tipoVentana) {
            "ina" -> calcularUParanteInaparente(alto, us)
            "apa" -> calcularUParante(altoHoja, us)
            else -> calcularUParanteInaparente(alto, us)
        }

        val uParante2 = if (tipoVentana == "ina") {
            calcularUParante2Inaparente(alto, altoHoja, us)
        } else {
            0f // NovaApa no usa uParante2
        }

        // Calcular texto común para todos los tipos de U
        val textoU = calcularTextoU(
            alto, altoHoja, us, divisiones, uFijos, uParante, uParante2, uSuperior,
            nFijos, fijoUParante, tipoVentana
        )

        // Configurar SOLO los TextViews principales (como en NovaApa)
        configurarEtiquetaYTextoU(us, bindings.tvU, bindings.txU, textoU)

        // Ya no necesitamos configurar u38 ni uOtros - solo UN layout dinámico
    }

    private fun calcularTextoU(
        alto: Float,
        altoHoja: Float,
        us: Float,
        divisiones: Int,
        uFijos: Float,
        uParante: Float,
        uParante2: Float,
        uSuperior: Float,
        nFijos: Int,
        fijoUParante: Int,
        tipoVentana: String
    ): String {
        return if (alto > altoHoja) {
            when {
                divisiones == 2 -> {
                    if (tipoVentana == "ina") {
                        "${NovaCalculos.df1(uFijos)} = $nFijos\n" +
                                "${NovaCalculos.df1(uParante)} = $fijoUParante\n" +
                                "${NovaCalculos.df1(uParante2)} = 1\n" +
                                "${NovaCalculos.df1(uSuperior)} = 1"
                    } else if (tipoVentana == "apa"){""}
                    else {
                        "${NovaCalculos.df1(uFijos)} = $nFijos\n" +
                                "${NovaCalculos.df1(uParante)} = $fijoUParante\n" +
                                "${NovaCalculos.df1(uSuperior)} = 1"
                    }
                }
                divisiones == 1 -> {
                    "${NovaCalculos.df1(uFijos)} = 2\n" +
                            "${NovaCalculos.df1(uParante)} = 2"
                }
                else -> {
                    "${NovaCalculos.df1(uFijos)} = $nFijos\n" +
                            "${NovaCalculos.df1(uParante)} = $fijoUParante\n" +
                            "${NovaCalculos.df1(uSuperior)} = 1"
                }
            }
        } else {
            when {
                divisiones == 2 -> {
                    "${NovaCalculos.df1(uFijos)} = $nFijos\n" +
                            "${NovaCalculos.df1(uParante)} = $fijoUParante"
                }
                divisiones == 1 -> {
                    "${NovaCalculos.df1(uFijos)} = 2\n" +
                            "${NovaCalculos.df1(uParante)} = 2"
                }
                else -> {
                    "${NovaCalculos.df1(uFijos)} = $nFijos\n" +
                            "${NovaCalculos.df1(uParante)} = $fijoUParante"
                }
            }
        }
    }

    // ==================== DATA CLASS SIMPLIFICADA PARA UN SOLO LAYOUT ====================

    data class BindingsU(
        val tvU: android.widget.TextView,   // TextView para la etiqueta (u-13, u-3/8, etc.)
        val txU: android.widget.TextView    // TextView para los valores calculados
    )

    // ==================== FUNCIÓN PARA GENERAR TEXTO COMPLETO DE PERFILES ====================

    @SuppressLint("SetTextI18n")
    fun generarTextoOtrosAluminios(
        divisiones: Int,
        alto: Float,
        hoja: Float,
        ancho: Float,
        altoHoja: Float,
        hache: Float,
        portafelpa: Float,
        nCorredizas: Int,
        divDePortas: Int,
        rieles: String,
        puentes: String
    ): PerfilesTexto {

        val textoRiel = if (divisiones == 1) "" else rieles
        val textoUFelpero = if (divisiones == 1) "" else rieles
        val textoHache = "${NovaCalculos.df1(hache)} = $nCorredizas"
        val textoAngTope = if (divisiones == 2) {
            "${NovaCalculos.df1(altoHoja - 0.9f)} = 1"
        } else ""
        val textoPortafelpa = "${NovaCalculos.df1(portafelpa)} = $divDePortas"

        return PerfilesTexto(
            tubo = puentes,
            riel = textoRiel,
            uFelpero = textoUFelpero,
            hache = textoHache,
            angTope = textoAngTope,
            portafelpa = textoPortafelpa
        )
    }

    // ==================== DATA CLASS PARA ORGANIZAR RESULTADOS ====================

    data class PerfilesTexto(
        val tubo: String,
        val riel: String,
        val uFelpero: String,
        val hache: String,
        val angTope: String,
        val portafelpa: String
    )
}
