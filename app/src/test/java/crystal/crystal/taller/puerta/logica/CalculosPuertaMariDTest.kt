package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculosPuertaMariDTest {

    @Test
    fun paflonesMariD_clasificaLateralesRecortadosEnTapa() {
        val resultado = CalculosPuerta.textoPaflonesMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 5,
            bastidor = 8.25f,
            angulo = 60f
        )

        assertEquals(
            """
            110.49 = 2
            64.96 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun paflonesMariD_distribuyeTodasLasBarrasEnProyeccionRotada() {
        val resultado = CalculosPuerta.textoPaflonesMariD(
            paflon = 28.1f,
            paranteInterno = 181.5f,
            nDiv = 9,
            bastidor = 8.25f,
            angulo = 60f
        )

        val cantidades = Regex("""=\s*(\d+)""")
            .findAll(resultado)
            .sumOf { it.groupValues[1].toInt() }

        assertEquals(8, cantidades)
        assertTrue(resultado.lines().none { it.startsWith("0.00") })
    }

    @Test
    fun paflonesMariD_compensaAristaQueLlegaCercaDeEsquina() {
        val resultado = CalculosPuerta.textoPaflonesMariD(
            paflon = 58.1f,
            paranteInterno = 148.5f,
            nDiv = 7,
            bastidor = 8.25f,
            angulo = 72f
        )

        assertEquals(
            """
            156.46 = 2
            106.35 = 2
            53.17 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun paflonesMariD_noPromediaAristaDeEsquinaConAristaCompleta() {
        val resultado = CalculosPuerta.textoPaflonesMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 4,
            bastidor = 8.25f,
            angulo = 49f
        )

        assertEquals(
            """
            82.81 = 1
            78.61 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_generaResultadoConGeometriaRotada() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 5,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 60f
        )

        assertTrue(resultado.isNotBlank())
        assertTrue(resultado.lines().all { it.contains("=") })
    }

    @Test
    fun junkillosMariD_conJunkilloCeroCalculaSinDescuentos() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 2,
            bastidor = 8.25f,
            junki = 0f,
            angulo = 45f
        )

        assertEquals(
            """
            48.10 = 2
            68.02 = 2
            60.86 = 2
            108.96 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_conJunkilloCeroMideInterseccionAInterseccion() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 38.1f,
            paranteInterno = 181.5f,
            nDiv = 5,
            bastidor = 8.25f,
            junki = 0f,
            angulo = 45f
        )

        assertEquals(
            """
            34.58 = 10
            48.91 = 2
            53.88 = 6
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun vidriosMariD_mideEspaciosVaciosConHolguraFija() {
        val resultado = CalculosPuerta.textoVidriosMariD(
            paflon = 38.1f,
            paranteInterno = 181.5f,
            nDiv = 5,
            bastidor = 8.25f,
            angulo = 45f
        )

        assertEquals(
            """
            37.7 x 139.8 = 1
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun vidriosMariD_optimizaUnSoloVidrioSiTodosLosDiagonalesTocanLados() {
        val resultado = CalculosPuerta.textoVidriosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 3,
            bastidor = 8.25f,
            angulo = 45f
        )

        assertEquals(
            """
            47.7 x 156.9 = 1
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun vidriosMariD_optimizaConAlturaDePaflonesRotados() {
        val resultado = CalculosPuerta.textoVidriosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 5,
            bastidor = 8.25f,
            angulo = 36f
        )

        assertEquals(
            """
            47.7 x 138.7 = 1
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun vidriosMariD_descuentaDosDiagonalesMenosCuandoLlegaATapas() {
        val resultado = CalculosPuerta.textoVidriosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 6,
            bastidor = 8.25f,
            angulo = 45f
        )

        assertEquals(
            """
            47.7 x 144 = 1
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun vidriosMariD_descuentaRematesParcialesSegunAngulo() {
        val resultado = CalculosPuerta.textoVidriosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 6,
            bastidor = 8.25f,
            angulo = 32f
        )

        assertEquals(
            """
            47.7 x 136.5 = 1
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun vidriosMariD_descuentaDosDiagonalesMenosEnCincoDivisionesCuandoLlegaATapas() {
        val resultado = CalculosPuerta.textoVidriosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 5,
            bastidor = 8.25f,
            angulo = 45f
        )

        assertEquals(
            """
            47.7 x 155.8 = 1
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun vidriosMariD_descuentaDosDiagonalesMenosEnOchoDivisionesCuandoLlegaATapas() {
        val resultado = CalculosPuerta.textoVidriosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 8,
            bastidor = 8.25f,
            angulo = 45f
        )

        assertEquals(
            """
            47.7 x 119.9 = 1
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_proyectaPaflonRotadoSobreLaterales() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 2,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 45f
        )

        assertEquals(
            """
            48.10 = 2
            69.52 = 2
            58.74 = 2
            105.34 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_compensaCorteVerticalCortoEnAnguloMenorA45() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 2,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 30f
        )

        assertEquals(
            """
            48.10 = 2
            56.40 = 2
            69.74 = 2
            96.64 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_compensaCorteVerticalCortoEnAnguloMayorA45() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 2,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 60f
        )

        assertEquals(
            """
            48.10 = 2
            98.80 = 2
            38.94 = 2
            119.65 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_noCuentaTramosOcupadosCuandoDiagonalLlegaAEsquina() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 2,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 75f
        )

        assertEquals(
            """
            44.09 = 2
            168.82 = 2
            157.27 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_mideHuecosDeCuatroLadosCuandoDiagonalCruzaHorizontales() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 2,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 80f
        )

        assertEquals(
            """
            4.12 = 2
            35.86 = 2
            181.52 = 2
            178.50 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_tramosDeEsquinaCambianConElAltoInterno() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 48.1f,
            paranteInterno = 185.5f,
            nDiv = 2,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 80f
        )

        assertEquals(
            """
            3.77 = 2
            36.21 = 2
            185.58 = 2
            182.50 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_avanzaTramoDeEsquinaPorAperturaNoPorTamano() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 60f,
            paranteInterno = 150f,
            nDiv = 2,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 80f
        )

        assertEquals(
            """
            12.85 = 2
            39.03 = 2
            149.53 = 2
            147.00 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_verticalEntreDiagonalesRespetaOrdenDeArmado() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 48.1f,
            paranteInterno = 181.5f,
            nDiv = 3,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 45f
        )

        assertEquals(
            """
            48.10 = 2
            69.52 = 4
            18.53 = 2
            65.13 = 2
            66.01 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_sieteDivisionesTresZocalos() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 48.1f,
            paranteInterno = 165f,
            nDiv = 7,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 60f
        )

        assertEquals(
            """
            5.28 = 2
            13.18 = 2
            12.31 = 2
            22.90 = 2
            45.41 = 2
            66.58 = 2
            89.10 = 2
            98.80 = 4
            11.78 = 2
            16.83 = 2
            17.93 = 6
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_cincoDivisionesCincoZocalosAngulo72() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 48.1f,
            paranteInterno = 148.5f,
            nDiv = 5,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 72f
        )

        assertEquals(
            """
            6.57 = 2
            12.81 = 2
            12.33 = 2
            38.33 = 2
            71.50 = 2
            106.30 = 2
            139.47 = 2
            17.47 = 2
            31.59 = 2
            32.86 = 2
            """.trimIndent(),
            resultado
        )
    }

    @Test
    fun junkillosMariD_horizontalesInternosAvanzanSegunApertura() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 48.1f,
            paranteInterno = 148.5f,
            nDiv = 7,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 72f
        )
        val cantidades = Regex("""^(\d+\.\d{2}) = (\d+)$""", RegexOption.MULTILINE)
            .findAll(resultado)
            .associate { it.groupValues[1] to it.groupValues[2].toInt() }

        assertEquals(4, cantidades["6.81"])
        assertEquals(2, cantidades["6.32"])
    }

    @Test
    fun junkillosMariD_horizontalesInternosAvanzanConAnchoMayor() {
        val resultado = CalculosPuerta.textoJunkillosMariD(
            paflon = 58.1f,
            paranteInterno = 148.5f,
            nDiv = 8,
            bastidor = 8.25f,
            junki = 1.5f,
            angulo = 72f
        )
        val cantidades = Regex("""^(\d+\.\d{2}) = (\d+)$""", RegexOption.MULTILINE)
            .findAll(resultado)
            .associate { it.groupValues[1] to it.groupValues[2].toInt() }

        assertEquals(6, cantidades["6.19"])
        assertEquals(2, cantidades["5.70"])
    }
}
