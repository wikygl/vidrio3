package crystal.crystal.taller.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Los arcos medidos SIEMBRAN los tramos del diseño; la última palabra es de Nova.
 *
 * Una ventana curva se mide partida en arcos —en obra casi nunca es un círculo perfecto— y esos
 * trozos son el punto de partida. Se entregan como reparto a mano, que en Nova solo vale mientras
 * las divisiones sean las mismas: en cuanto se acepta el parante que sugiere, o se le quita uno,
 * el reparto deja de valer y manda otra vez la regla de Nova.
 *
 * La cuenta es la misma que hace la pantalla; aquí se comprueba en frío.
 */
class RepartoSegunLosArcosTest {

    private fun repartoSegunLosArcos(anchos: List<Float>, divisiones: Int): List<Int>? {
        if (anchos.size < 2 || divisiones < anchos.size) return null
        val total = anchos.sum().takeIf { it > 0f } ?: return null
        val reparto = anchos.map { (divisiones * it / total).toInt().coerceAtLeast(1) }.toMutableList()
        var sobran = divisiones - reparto.sum()
        var vuelta = 0
        while (sobran != 0 && vuelta < 1000) {
            val cual = if (sobran > 0) {
                anchos.indices.maxByOrNull { anchos[it] / reparto[it] } ?: 0
            } else {
                anchos.indices.filter { reparto[it] > 1 }.minByOrNull { anchos[it] / reparto[it] }
                    ?: return null
            }
            reparto[cual] += if (sobran > 0) 1 else -1
            sobran = divisiones - reparto.sum()
            vuelta++
        }
        return if (reparto.sum() == divisiones) reparto else null
    }

    /** Arcos iguales, módulos iguales. */
    @Test
    fun arcos_iguales_se_reparten_por_igual() {
        assertEquals(listOf(3, 3), repartoSegunLosArcos(listOf(90f, 90f), 6))
        assertEquals(listOf(2, 2, 2), repartoSegunLosArcos(listOf(60f, 60f, 60f), 6))
    }

    /** Un arco más largo se lleva más módulos, y la suma sigue siendo la de Nova. */
    @Test
    fun el_arco_mas_largo_se_lleva_mas_modulos() {
        val reparto = repartoSegunLosArcos(listOf(200f, 100f), 6)!!
        assertEquals("la suma tiene que ser la que dice Nova", 6, reparto.sum())
        assertTrue("el arco largo no se llevó más: $reparto", reparto[0] > reparto[1])
    }

    /** Ningún arco se queda sin módulos, por corto que sea. */
    @Test
    fun ningun_arco_se_queda_vacio() {
        val reparto = repartoSegunLosArcos(listOf(300f, 20f), 6)!!
        assertEquals(6, reparto.sum())
        assertTrue("un arco se quedó sin módulos: $reparto", reparto.all { it >= 1 })
    }

    /** Y con menos módulos que arcos no hay reparto que valga: decide Nova. */
    @Test
    fun con_menos_modulos_que_arcos_decide_nova() {
        assertNull(repartoSegunLosArcos(listOf(90f, 90f, 90f), 2))
        assertNull("un arco solo no siembra nada", repartoSegunLosArcos(listOf(180f), 4))
    }
}
