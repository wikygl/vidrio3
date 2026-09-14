package crystal.crystal.taller.nova

import crystal.crystal.taller.ArcoEsquina
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * La flecha del arco que equivale a toda una ventana curva.
 *
 * La calculadora curva trabaja con UN arco: su desarrollo y su flecha. Una ventana curva medida
 * puede venir partida en varios —así se mide en obra, porque casi nunca es un círculo perfecto—,
 * y hay que entregarle el arco que equivale a todos juntos: mismo desarrollo, mismo giro.
 *
 * La cuenta es la misma que hace la pantalla; aquí se comprueba en frío, que allí va metida en
 * medio de un diálogo.
 */
class FlechaDeTodaLaCurvaTest {

    private fun flechaDeTodaLaCurva(desarrolloCm: Float, giroGrados: Float): Float {
        if (desarrolloCm <= 0f || giroGrados <= 0.01f) return 0f
        val giro = Math.toRadians(giroGrados.toDouble())
        val radio = desarrolloCm / giro
        return (radio * (1.0 - kotlin.math.cos(giro / 2.0))).toFloat()
    }

    private fun giroDe(desarrollo: Float, flecha: Float): Float =
        ArcoEsquina.deDesarrolloYFlecha(desarrollo, flecha)?.anguloGrados ?: 0f

    /** Con un arco solo, la flecha del conjunto es la suya: no puede ser otra cosa. */
    @Test
    fun un_arco_solo_conserva_su_flecha() {
        val flecha = flechaDeTodaLaCurva(180f, giroDe(180f, 12f))
        assertEquals(12f, flecha, 0.2f)
    }

    /**
     * Dos arcos iguales seguidos hacen una curva del doble de desarrollo y del doble de giro, y su
     * flecha es MUCHO mayor que la de cada uno: la panza crece con el giro, no se suma.
     */
    @Test
    fun dos_arcos_seguidos_hacen_una_curva_mas_panzuda_que_la_suma() {
        val giroDeUno = giroDe(90f, 8f)
        val flechaJuntos = flechaDeTodaLaCurva(180f, giroDeUno * 2f)
        // Cada arco de 90 con 8 de panza gira unos 20°; los dos juntos, 40° sobre 180 de
        // desarrollo, dan casi 31 de panza: casi el doble de los 16 que darían sumadas.
        assertEquals("la curva entera no tiene la panza que le toca", 31f, flechaJuntos, 0.5f)

        // Y la vuelta: ese desarrollo con esa flecha da el mismo giro que los dos arcos juntos.
        val arcoEntero = ArcoEsquina.deDesarrolloYFlecha(180f, flechaJuntos)!!
        assertEquals("el arco equivalente no gira lo que giran los dos", giroDeUno * 2f, arcoEntero.anguloGrados, 0.5f)
        assertEquals("y tiene que conservar el desarrollo", 180f, arcoEntero.desarrollo, 0.1f)
    }

    /** Sin giro no hay curva: una pared recta no tiene panza. */
    @Test
    fun sin_giro_no_hay_panza() {
        assertEquals(0f, flechaDeTodaLaCurva(180f, 0f), 0.001f)
        assertEquals(0f, flechaDeTodaLaCurva(0f, 30f), 0.001f)
    }
}
