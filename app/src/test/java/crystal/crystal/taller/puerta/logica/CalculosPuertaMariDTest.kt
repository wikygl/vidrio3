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
            110.5 = 2
            65 = 2
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
            156.5 = 2
            106.4 = 2
            53.2 = 2
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
            82.8 = 1
            78.6 = 2
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
            48.1 = 2
            68 = 2
            60.9 = 2
            109 = 2
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
            34.6 = 10
            48.9 = 2
            53.9 = 6
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
            37.7 x 140.7 = 1
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
            47.7 x 157 = 1
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
            47.7 x 144.9 = 1
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
            47.7 x 137.1 = 1
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
            47.7 x 156.7 = 1
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
            47.7 x 120.8 = 1
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
            48.1 = 2
            69.5 = 2
            58.7 = 2
            105.3 = 2
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
            48.1 = 2
            56.4 = 2
            69.7 = 2
            96.6 = 2
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
            48.1 = 2
            98.8 = 2
            38.9 = 2
            119.7 = 2
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
            44.1 = 2
            168.8 = 2
            157.3 = 2
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
            4.1 = 2
            35.9 = 2
            181.5 = 2
            178.5 = 2
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
            3.8 = 2
            36.2 = 2
            185.6 = 2
            182.5 = 2
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
            12.9 = 2
            39 = 2
            149.5 = 2
            147 = 2
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
            48.1 = 2
            69.5 = 4
            18.5 = 2
            65.1 = 2
            66 = 2
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
            5.3 = 2
            13.2 = 2
            12.3 = 2
            22.9 = 2
            45.4 = 2
            66.6 = 2
            89.1 = 2
            98.8 = 4
            11.8 = 2
            16.8 = 2
            17.9 = 6
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
            6.6 = 2
            12.8 = 2
            12.3 = 2
            38.3 = 2
            71.5 = 2
            106.3 = 2
            139.5 = 2
            17.5 = 2
            31.6 = 2
            32.9 = 2
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
        val cantidades = Regex("""^([\d.]+) = (\d+)$""", RegexOption.MULTILINE)
            .findAll(resultado)
            .associate { it.groupValues[1] to it.groupValues[2].toInt() }

        assertEquals(4, cantidades["6.8"])
        assertEquals(2, cantidades["6.3"])
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
        val cantidades = Regex("""^([\d.]+) = (\d+)$""", RegexOption.MULTILINE)
            .findAll(resultado)
            .associate { it.groupValues[1] to it.groupValues[2].toInt() }

        assertEquals(6, cantidades["6.2"])
        assertEquals(2, cantidades["5.7"])
    }
}
