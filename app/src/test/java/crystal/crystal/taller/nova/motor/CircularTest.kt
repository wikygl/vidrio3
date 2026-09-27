package crystal.crystal.taller.nova.motor

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Forma circular (N-01, sección 11 de las reglas), el ejemplo dictado: un círculo de 180 × 180. En
 * automático APA pasa a bandera y lleva tubo (N-03). Hoja 5/7 = 128.6, mochetas (180 − 128.6 − 5) / 2
 * = 23.2; la flecha (mocheta + puente) 25.7 y su cuerda 126 = el puente.
 */
class CircularTest {

    private val tubo = Puente("tubo 2⅜ x 1", 2.5f, 6f)

    private fun circulo(acabado: Acabado): MaterialesNova {
        val lado = DisenoAutomatico.lado(DisenoAutomatico.Entrada(
            anchoMedido = 180f, anchoCalculo = 180f, alto = 180f, hoja = 180f / 7f * 5f,
            acabado = acabado, puente = tubo, modulacion = "nn"
        ))
        return MotorNova.calcular(VentanaNova(acabado, ModeloMocheta.BANDERA, listOf(lado), PerfilU(1f, 1f), tubo, circular = true))
    }

    private fun t(p: List<Pieza>) = MaterialesNova.texto(p)

    @Test
    fun la_geometria_del_circulo() {
        val c = Circulo(180f, 180f)
        assertEquals(125.97f, c.cuerda(25.714f), 0.02f)
        assertEquals(565.49f, c.perimetro(), 0.02f)
        assertEquals(180f * Math.PI.toFloat() / 2f, c.arcoArriba(90f), 0.02f)   // media vuelta
        assertEquals(143.2f, c.arcoLado(25.714f, 154.286f), 0.05f)
        assertEquals(169.7f, c.altoEntre(0f, 60f), 0.05f)
    }

    @Test
    fun aparente_180_en_bandera() {
        val m = circulo(Acabado.APA)
        // U de fijo 60.5 − 27 (lo de fuera del círculo sobre el puente de abajo); U de mocheta, el
        // arco por el borde de afuera del puente (132.2) y la recta sobre él (tubo); U parante, el
        // arco entre los puentes 143.2 − 1.2.
        assertEquals("33.4 = 2\n132.2 = 2\n120.6 = 2\n142 = 2", t(m.u))
        assertEquals("125.9 = 2", t(m.puente))
        assertEquals("125.9 = 1", t(m.riel))
        assertEquals("125.9 = 1", t(m.rielSuperior))
        assertEquals("60.4 = 1", t(m.hache))
        assertEquals("126.9 = 4", t(m.portafelpa))
        assertEquals("59.9 x 127.3 = 2\n59 x 125 = 1\n120.1 x 21.9 = 2", MaterialesNova.textoVidrios(m.vidrios))
    }

    @Test
    fun inaparente_180_la_u_es_el_perimetro() {
        val m = circulo(Acabado.INA)
        assertEquals("565.4 = 1", t(m.u))
        assertEquals("125.9 = 2", t(m.puente))
        // El fijo junto a la pared: su rectángulo llega a 169.7, no a 180.
        assertEquals("59.9 x 168.5 = 2\n59 x 125 = 1\n58.4 x 26.7 = 1\n58.4 x 25.2 = 1", MaterialesNova.textoVidrios(m.vidrios))
    }
}
