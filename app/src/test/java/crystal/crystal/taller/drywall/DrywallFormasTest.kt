package crystal.crystal.taller.drywall

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.roundToInt

/**
 * El drywall por forma en planta (recta, L, C, S, curva), cada una de tabique o de forro
 * (pedido del usuario, 2026-10-11). Los esperados, contados a mano con las reglas de [DrywallCalculo].
 */
class DrywallFormasTest {

    private fun MaterialesDrywall.cantidad(empieza: String): Float =
        (perfiles + fijaciones + acabado + planchas).first { it.nombre.startsWith(empieza) }.cantidad

    /**
     * El caso de la obra: cajón en C de 18 x 18 x 18 para tapar el desagüe, 3.15 de alto, una cara.
     * Reglas del usuario del 2026-10-10 (su dibujo en planta): contra el muro va riel, en las
     * esquinas parante, se resta la plancha (armazón 16.8 y 15.6), riel de piso y techo, amarres a
     * 1.22 y tornillos cada 30.
     */
    @Test
    fun el_cajon_de_la_obra_en_c_de_forro() {
        val d = Drywall(
            tipo = TipoDrywall.FORRO, forma = FormaDrywall.C, largoM = 0.18f, lado2M = 0.18f, lado3M = 0.18f,
            altoM = 3.15f, perfil = PerfilDrywall.P38, esquinas = 2
        )
        val x = DrywallCalculo.despiece(d)
        assertEquals(listOf(0.168f, 0.156f, 0.168f), x.armazonM.map { (it * 1000).roundToInt() / 1000f })
        // Los lados de 18 tapan el canto del frente de 15.6; cada uno en 2.44 y 0.71.
        val piezas = x.piezas.map { "${it.lugar} ${DrywallCalculo.fmt(it.anchoM * 100)} x ${DrywallCalculo.fmt(it.altoM * 100)}" }
        assertEquals(listOf(
            "Lado izq. 18 x 244", "Lado izq. 18 x 71", "Frente 15.6 x 244", "Frente 15.6 x 71", "Lado der. 18 x 244", "Lado der. 18 x 71"
        ), piezas)
        assertEquals(1, x.planchas.size)
        assertEquals(listOf(1.22f, 2.44f), x.amarresM)
        assertEquals(2, x.verticales.count { it.funcion == FuncionVertical.RIEL_A_MURO })
        assertEquals(2, x.verticales.count { it.funcion == FuncionVertical.ESQUINA })

        val m = DrywallCalculo.calcular(d)
        assertEquals(1f, m.planchas.cantidad)
        // 2 parantes de 3.00 + 0.45 de empalme: 3 varillas de 3.00 (con 3.05: 3.05 + 0.40, también 3).
        assertEquals(3f, m.cantidad("Parante 38"))
        // 2 rieles a muro (3.00 + 0.45), piso, techo y 2 amarres de 16.8 + 15.6 + 16.8: 3 varillas. Antes 1.
        assertEquals(3f, m.cantidad("Riel 38"))
        assertEquals(4f, m.cantidad("Anclaje de piso"))      // 2 en el piso y 2 en el techo. Antes 12.
        assertEquals(14f, m.cantidad("Anclaje a muro"))      // 2 rieles x (ceil(3.15 / 0.6) + 1). Antes 12.
        assertEquals(3f, m.cantidad("Ángulo esquinero"))            // 2 x 3.00 y los dos trozos de 0.15 de una (con varillas de 2.44: 2.44 + 0.71, también 3).
        // 4 por vertical (16), por empalme (16) y por amarre (6 x 4 = 24). Antes 32.
        assertEquals(56f, m.cantidad("Tornillo wafer"))
        // Cada pieza en sus 2 bordes, cada 30: (10 + 4) x 2 x 3 tiras. Antes 30 por plancha.
        assertEquals(84f, m.cantidad("Tornillo drywall"))
        assertTrue(m.acabado.first { it.nombre.startsWith("Cinta") }.nota.startsWith("6.82 m"))
        assertTrue(m.despiece.contains("Plancha 1: 18 × 244 | 18 × 244 | 18 × 71 (2) + 15.6 × 71 | 15.6 × 244; sobra 52.4 × 244"))
        // Lo que se archiva y leen los optimizadores (planchas: ancho x alto = cantidad; varillas: largo = cantidad).
        assertEquals("18x244 = 2\n18x71 = 2\n15.6x244 = 1\n15.6x71 = 1", m.piezasPlancha)
        assertEquals(listOf("Parante 38", "Riel 38", "Ángulo esquinero"), m.filasPerfil.map { it.nombre })
        // El ángulo esquinero en las 2 esquinas del frente, de 315: 300 + 15 a tope, en varillas de 3.00 como los demás perfiles (el usuario, 2026-10-11).
        assertEquals("300 = 2\n15 = 2", m.filasPerfil[2].lineas)
        // Antes, con varillas de 3.05: "305 = 2\n40 = 2" y "305 = 2\n40 = 2\n16.8 = 8\n15.6 = 4".
        assertEquals("300 = 2\n45 = 2", m.filasPerfil[0].lineas)
        assertEquals("300 = 2\n45 = 2\n16.8 = 8\n15.6 = 4", m.filasPerfil[1].lineas)
        assertTrue(m.lineasArchivables(m.fijaciones).contains("Tornillo drywall 6 x 1\" = 84"))
        assertTrue(m.lineasArchivables(m.acabado).contains("Masilla para juntas (kg) = 2"))
        assertTrue(m.referencias.endsWith("Compra: 1 plancha, 3 parantes 38, 3 rieles 38 y 3 ángulos esquineros de 3.00 m"))
    }

    @Test
    fun cajon_en_c_de_40_con_lados_de_18() {
        val d = Drywall(tipo = TipoDrywall.FORRO, forma = FormaDrywall.C, largoM = 0.40f, lado2M = 0.18f, lado3M = 0.18f, altoM = 2.40f, esquinas = 2)
        assertEquals(0.76f, d.desarrolloM, 0.001f)
        val m = DrywallCalculo.calcular(d)
        assertEquals(1f, m.planchas.cantidad)
        // Los 2 de las esquinas (antes 4: los de muro también eran parantes).
        assertEquals(2f, m.cantidad("Parante 89"))
        // 2 a muro de 2.40, piso, techo y el amarre a 1.22 de 16.8 + 37.6 + 16.8.
        assertEquals(3f, m.cantidad("Riel 89"))
        assertEquals(4f, m.cantidad("Anclaje de piso"))       // antes 12
        assertEquals(10f, m.cantidad("Anclaje a muro"))       // 2 x (4 + 1); antes 8
        assertEquals(2f, m.cantidad("Ángulo esquinero"))
    }

    @Test
    fun tabique_en_l() {
        val d = Drywall(tipo = TipoDrywall.TABIQUE, forma = FormaDrywall.L, largoM = 3f, lado2M = 1.2f, altoM = 2.4f, esquinas = 1)
        val m = DrywallCalculo.calcular(d)
        // 1 al arranque + ceil(3 / 0.406) = 8 hasta la esquina + ceil(1.2 / 0.406) = 3 hasta el final.
        assertEquals(12f, m.cantidad("Parante 89"))
        // 4.2 x 2.4 x 2 caras = 20.16 m² x 1.1 / 2.98 = 7.4 → 8 (las tiras piden 7).
        assertEquals(8f, m.planchas.cantidad)
        assertTrue(m.referencias.startsWith("Tabique (dos caras), en l: 3 + 1.2 = 4.2 m x 2.4 m"))
    }

    @Test
    fun forro_en_s_tiene_dos_dobleces() {
        val d = Drywall(tipo = TipoDrywall.FORRO, forma = FormaDrywall.S, largoM = 1f, lado2M = 1f, lado3M = 1f)
        assertEquals(2, d.dobleces)
        assertEquals(1, Drywall.esquinasDeFabrica(TipoDrywall.FORRO, FormaDrywall.S))
        assertEquals(2, Drywall.esquinasDeFabrica(TipoDrywall.TABIQUE, FormaDrywall.S))
        // 1 + ceil(1 / 0.406) x 3 = 1 + 3 x 3 = 10.
        assertEquals(10f, DrywallCalculo.calcular(d).cantidad("Parante 89"))
    }

    @Test
    fun la_curva_pone_parantes_mas_juntos_y_curva_el_riel() {
        val d = Drywall(tipo = TipoDrywall.FORRO, forma = FormaDrywall.CURVA, largoM = 2f, lado2M = 0.3f)
        val m = DrywallCalculo.calcular(d)
        // Cada 30.5 como mucho: 1 + ceil(2 / 0.305) = 1 + 7 = 8.
        assertEquals(8f, m.cantidad("Parante 89"))
        assertTrue(m.perfiles.first { it.nombre.startsWith("Riel") }.nota.contains("curvarlo"))
        assertTrue(m.referencias.contains("radio"))
    }

}
