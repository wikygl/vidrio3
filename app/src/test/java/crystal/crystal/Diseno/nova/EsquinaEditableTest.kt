package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Una ventana de esquina se puede editar sin perder la esquina.
 *
 * El editor lee el paquete, lo cambia y lo vuelve a escribir. El pliegue (`A<90>`) y la panza de
 * la pared curva (`Q<29.3>`) tienen que sobrevivir a ese viaje: son lo que hace que la ventana sea
 * una L y no un rectángulo, y hasta ahora se perdían por el camino —los pliegues acababan todos
 * juntos al final del paquete, que es como decir en ninguna parte—.
 */
class EsquinaEditableTest {

    /** Una L: pared, pliegue, pared. */
    private val enL =
        "{nova,ina,[150,120:Tl<150>(H<120>;m<30>(f);s(fc)) A<90> Tl<120>(H<120>;m<30>(f);s(fc))]}"

    /** La misma L con la esquina redondeada: pared, pared curva, pliegue, pared. */
    private val enLConCurva =
        "{nova,ina,[150,120:Tl<150>(H<120>;m<30>(f);s(fc))" +
            " Tl<157.1>(H<120>;Q<29.3>;m<30>(f);s(fc))" +
            " A<90> Tl<120>(H<120>;m<30>(f);s(fc))]}"

    @Test
    fun el_pliegue_se_queda_entre_sus_dos_paredes() {
        val d = DisenoNova.desdePaquete(enL)!!
        assertEquals(2, d.tramos.size)
        assertNull("la primera pared no dobla delante de ella", d.tramos[0].pliegue)
        assertEquals("el pliegue no llegó a su pared", "A<90>", d.tramos[1].pliegue)

        // Y al reescribirlo vuelve a su sitio, entre las dos, no al final del paquete.
        val paquete = d.aPaquete()
        val posPliegue = paquete.indexOf("A<90>")
        assertTrue("el pliegue desapareció: $paquete", posPliegue > 0)
        assertTrue(
            "el pliegue se fue al final, fuera de las paredes: $paquete",
            posPliegue < paquete.lastIndexOf("Tl<")
        )
    }

    @Test
    fun editar_un_modulo_no_endereza_la_ventana() {
        val d = DisenoNova.desdePaquete(enL)!!
        // Lo que hace el vidriero en la pantalla: meterle un módulo a la segunda pared.
        val editado = d.conModuloAgregado(1, 1, 0, 'c')
        val vuelta = DisenoNova.desdePaquete(editado.aPaquete())!!
        assertEquals("se perdió una pared", 2, vuelta.tramos.size)
        assertEquals("la ventana se enderezó al editarla", "A<90>", vuelta.tramos[1].pliegue)
        assertEquals(
            "el módulo no entró",
            d.tramos[1].nModulosSistema + 1, vuelta.tramos[1].nModulosSistema
        )
    }

    @Test
    fun la_pared_curva_conserva_su_panza() {
        val d = DisenoNova.desdePaquete(enLConCurva)!!
        assertEquals(3, d.tramos.size)
        assertEquals("la pared recta salió curva", 0f, d.tramos[0].flecha, 0.01f)
        assertEquals("la curva perdió su panza", 29.3f, d.tramos[1].flecha, 0.01f)
        assertNull("la pared entra en la curva, no dobla contra ella", d.tramos[1].pliegue)
        assertEquals("detrás de la curva la pared va girada", "A<90>", d.tramos[2].pliegue)

        val vuelta = DisenoNova.desdePaquete(d.aPaquete())!!
        assertEquals(29.3f, vuelta.tramos[1].flecha, 0.01f)
        assertEquals("A<90>", vuelta.tramos[2].pliegue)
    }

    /**
     * Lo que pidió el vidriero: que la curva sea un vidrio fijo entero, de piso a techo.
     *
     * Es la edición más común en una esquina redondeada, y tiene que poder hacerse sin que la
     * ventana deje de ser una L con su curva.
     */
    @Test
    fun la_curva_puede_quedarse_en_un_fijo_de_piso_a_techo() {
        val d = DisenoNova.desdePaquete(enLConCurva)!!
        val soloFijo = NovaTramo(
            ancho = d.tramos[1].ancho,
            franjas = listOf(NovaFranja(esSistema = true, alto = 0f, modulos = listOf(NovaModulo('f')))),
            alto = d.tramos[1].alto,
            pliegue = d.tramos[1].pliegue,
            flecha = d.tramos[1].flecha
        )
        val editado = d.copy(tramos = d.tramos.toMutableList().also { it[1] = soloFijo })

        val vuelta = DisenoNova.desdePaquete(editado.aPaquete())!!
        assertEquals(3, vuelta.tramos.size)
        assertEquals("la curva se quedó sin su panza", 29.3f, vuelta.tramos[1].flecha, 0.01f)
        assertEquals("no quedó un solo módulo", 1, vuelta.tramos[1].nModulosSistema)
        assertEquals("y tiene que ser fijo", 1, vuelta.tramos[1].sistema?.nFijos)
        assertEquals("se quedó sin mocheta, que era la idea", 1, vuelta.tramos[1].franjas.size)
        assertEquals("las paredes rectas cambiaron", 2, vuelta.tramos[0].nModulosSistema)
        assertEquals("la ventana dejó de doblar", "A<90>", vuelta.tramos[2].pliegue)
    }

    @Test
    fun un_diseno_normal_sigue_saliendo_igual() {
        // Sin pliegues ni panzas, el paquete se escribe como toda la vida: tramos separados por
        // P<2.5> y nada más.
        val d = DisenoNova.nuevo("ina", 150f, 120f, 120f)
        val paquete = d.aPaquete()
        assertTrue("apareció un pliegue de la nada: $paquete", !paquete.contains("A<"))
        assertTrue("apareció una panza de la nada: $paquete", !paquete.contains("Q<"))
        assertNotNull(DisenoNova.desdePaquete(paquete))
    }
}
