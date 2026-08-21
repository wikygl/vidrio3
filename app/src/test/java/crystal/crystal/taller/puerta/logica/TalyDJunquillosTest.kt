package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Los junquillos de "Taly d" siguen a sus vidrios: como los paños diagonales no son todos iguales
 * —las puntas son triángulos y el medio paralelogramos—, sus junquillos tampoco.
 *
 * Antes se listaba una medida por sección repetida nDiv veces, el mismo defecto que tenía el vidrio.
 * Ahora se calculan con la geometría de Mari d sobre la zona de vidrio de Taly.
 */
class TalyDJunquillosTest {

    private val bastidor = CalculosPuerta.BASTIDOR
    private val junki = 1.2f

    private fun junquillos(anchoZona: Float, altoZona: Float, nDiv: Int, angulo: Float) =
        CalculosPuerta.textoJunkillosMariD(anchoZona, altoZona, nDiv, bastidor, junki, angulo)

    /** En el taller se mide con un decimal: dos no se pueden marcar. */
    @Test
    fun lasMedidasVanConUnDecimal() {
        junquillos(15.1f, 165f, nDiv = 4, angulo = 45f).lines().forEach { linea ->
            val medida = linea.substringBefore(" =")
            val decimales = medida.substringAfter('.', "").length
            assert(decimales <= 1) { "la medida $medida tiene más de un decimal" }
        }
    }

    private fun piezas(texto: String): Int =
        texto.lines().filter { it.isNotBlank() }.sumOf { it.substringAfterLast("=").trim().toInt() }

    /**
     * Un vacío de 15.1 x 165 (puerta 70) con 4 divisiones a 45°. Cada paño lleva su marco de
     * junquillo, así que salen más de una medida y ninguna se repite para todos.
     */
    @Test
    fun noSaleUnaSolaMedidaParaTodos() {
        val texto = junquillos(15.1f, 165f, nDiv = 4, angulo = 45f)
        assert(texto.lines().size >= 2) { "esperaba varias medidas, salió: $texto" }
    }

    /** Sin divisiones no hay diagonales que enmarcar. */
    @Test
    fun sinDivisionesNoHayJunquilloDiagonal() {
        assertEquals("", junquillos(15.1f, 165f, nDiv = 1, angulo = 45f))
    }

    /** El conteo tiene que crecer con las divisiones, no quedarse fijo. */
    @Test
    fun aMasDivisionesMasPiezas() {
        val tres = piezas(junquillos(15.1f, 165f, nDiv = 3, angulo = 45f))
        val seis = piezas(junquillos(15.1f, 165f, nDiv = 6, angulo = 45f))
        assert(seis > tres) { "con 6 divisiones salieron $seis piezas y con 3, $tres" }
    }
}
