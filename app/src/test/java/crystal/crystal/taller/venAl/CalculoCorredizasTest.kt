package crystal.crystal.taller.venAl

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Cortes de las series corredizas contra las hojas de cálculo del proveedor (2026-10-16). Los
 * valores salen de evaluar las fórmulas del Excel en la medida de cada caso (mm → cm, df1).
 */
class CalculoCorredizasTest {

    private fun calc(serie: String, ancho: Float, alto: Float, patron: String, div: Int = 0) =
        CalculoCorredizas.calcular(serie, ancho, alto, patron, div)!!

    private fun lista(r: CalculoCorredizas.Resultado, nombre: String) =
        r.listas.firstOrNull { it.first == nombre }?.second

    @Test
    fun serie20_dosHojas() {
        val r = calc("Serie 20", 153.7f, 121.3f, "fc")
        assertEquals(listOf("152.5 = 1"), lista(r, "Riel Sup."))
        assertEquals(listOf("152.5 = 1"), lista(r, "Riel Inf."))
        // Antes: una jamba por corrediza (1 en fc). Excel: siempre 2.
        assertEquals(listOf("121.3 = 2"), lista(r, "Jamba"))
        assertEquals(listOf("76.9 = 2"), lista(r, "Cabezal"))
        assertEquals(listOf("76.5 = 1", "76.9 = 1"), lista(r, "Zócalo"))
        assertEquals(listOf("118.8 = 2"), lista(r, "Pierna"))
        assertEquals(listOf("118.8 = 2"), lista(r, "Traslape"))
        assertEquals(listOf("71.6 x 111.5 = 2"), r.vidrios)
    }

    @Test
    fun serie20_cccc_vidrioMenorQueLaHojaYUnAdaptador() {
        val r = calc("Serie 20", 153.7f, 121.3f, "cccc")
        // Antes: A/4 + 4.9 = 43.3 (el signo cambiado del Excel) y el adaptador ×2 solo en fccf.
        assertEquals(listOf("33.5 x 111.5 = 4"), r.vidrios)
        assertEquals(listOf("117 = 1"), lista(r, "Adaptador de hoja"))
        assertEquals(listOf("121.3 = 2"), lista(r, "Jamba"))
    }

    @Test
    fun serie25_ccccLlevaAdaptador() {
        val r = calc("Serie 25", 153.7f, 121.3f, "cccc")
        assertEquals(listOf("117.8 = 1"), lista(r, "Adaptador de hoja"))
        assertEquals(listOf("39.2 = 4"), lista(r, "Cabezal"))
        assertEquals(listOf("32.4 x 108.8 = 4"), r.vidrios)
    }

    @Test
    fun serie62_cortavientoYMarcoPorTipo() {
        // 5 divisiones: un tramo de 3 (marco triple) y uno de 2 (marco doble).
        val r = calc("Serie 62 europea", 300f, 200f, "tramos", 5)
        assertEquals(listOf("fcc", "fc"), r.tramos)
        // Módulo (300 − 2.5) / 5 = 59.5: tramo de 178.5 y de 119.
        assertEquals(listOf("119 = 2", "200 = 2"), lista(r, "Marco"))
        assertEquals(listOf("178.5 = 2", "200 = 2"), lista(r, "Marco triple corrediza"))
        assertEquals(listOf("3.4 = 6"), lista(r, "Cortaviento"))
        assertEquals(listOf("200 = 1"), lista(r, CalculoCorredizas.PARANTE))
    }

    @Test
    fun serie80_tresHojasDosCortavientos() {
        val r = calc("Serie 80", 153.7f, 121.3f, "fcc")
        assertEquals(listOf("3.3 = 2", "4.3 = 2"), lista(r, "Cortaviento"))
        assertEquals(listOf("54.9 = 6", "115.7 = 6"), lista(r, "Bastidor"))
        assertEquals(listOf("41.5 x 102.3 = 3"), r.vidrios)
    }

    @Test
    fun serie46_pañoFijo() {
        val r = calc("Serie 46", 153.7f, 121.3f, "f")
        assertEquals(listOf("153.7 = 2", "121.3 = 2"), lista(r, "Marco fijo"))
        assertEquals(listOf("148.2 = 2", "112 = 2"), lista(r, "Perfil VL4608"))
        assertEquals(listOf("147 x 114.6 = 1"), r.vidrios)
    }

    @Test
    fun modeloDeOtraSerie_noCalcula() {
        assertNull(CalculoCorredizas.calcular("Serie 62 europea", 150f, 120f, "cfc", 0))
        assertNull(CalculoCorredizas.serie("Clásica serie 84"))
    }
}
