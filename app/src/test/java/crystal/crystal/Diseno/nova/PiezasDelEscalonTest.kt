package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Las piezas que aparecen solo por el escalón. Son las que hoy no cuenta nadie y por las que una
 * ventana escalonada se cotiza corta.
 */
class PiezasDelEscalonTest {

    private val escalonada = DisenoNova.desdePaquete(
        "{nova,apa,[446.3,160:Tl<280.3>(s<120>(f<140.1>c<140.1>);m<40>(f<280.3>))" +
            " P<2.5> Tl<166>(H<106.2>;s<106.2>(f<166>))]}"
    )!!

    private val recta = DisenoNova.desdePaquete(
        "{nova,apa,[300,200:Tl<300>(s<160>(f<150>c<150>);m<40>(f<300>))]}"
    )!!

    @Test
    fun `una ventana recta no tiene piezas de escalón`() {
        assertTrue(PiezasDelEscalon.de(recta).isEmpty())
    }

    @Test
    fun `el alféizar se parte en uno por tramo`() {
        val piezas = PiezasDelEscalon.de(escalonada)
        val alfeizares = piezas.filter { it.nombre.startsWith("alféizar") }
        assertEquals(2, alfeizares.size)
        assertEquals(280.3f, alfeizares[0].medidaCm, 0.05f)
        assertEquals(166f, alfeizares[1].medidaCm, 0.05f)
    }

    @Test
    fun `cada escalón lleva su jamba, del alto que sube`() {
        val jambas = PiezasDelEscalon.de(escalonada).filter { it.nombre.startsWith("jamba") }
        assertEquals(1, jambas.size)
        assertEquals("la jamba no mide lo que sube el alféizar", 53.8f, jambas[0].medidaCm, 0.05f)
    }

    @Test
    fun `con dos escalones salen dos jambas`() {
        val dosEscalones = DisenoNova.desdePaquete(
            "{nova,apa,[600,200:Tl<200>(s<200>(f<200>))" +
                " P<2.5> Tl<200>(H<150>;s<150>(f<200>))" +
                " P<2.5> Tl<200>(H<100>;s<100>(f<200>))]}"
        )!!
        val piezas = PiezasDelEscalon.de(dosEscalones)
        val jambas = piezas.filter { it.nombre.startsWith("jamba") }
        assertEquals(2, jambas.size)
        assertEquals(50f, jambas[0].medidaCm, 0.05f)
        assertEquals(50f, jambas[1].medidaCm, 0.05f)
        assertEquals(3, piezas.count { it.nombre.startsWith("alféizar") })
    }
}
