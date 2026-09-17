package crystal.crystal.Diseno.nova

import crystal.crystal.taller.nova.NovaUIHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El paquete de una L tal como lo escribe la calculadora, con lo que trajo la medida encima: la
 * silueta de la pared de frente y un parante. Es lo que hace NovaCorrediza en
 * `conLoQueTrajoLaMedida`; si el modelo no leyera el paquete de la calculadora, devolvería el de
 * siempre y la pared saldría rectangular sin que nadie avisara.
 */
class PaqueteDeEsquinaConLoMedidoTest {

    @Test
    fun el_paquete_de_la_calculadora_admite_la_silueta_y_el_parante() {
        val base = NovaUIHelper.generarTramosConsolidado(277.6f, 221.9f, 152f, 3, 1, "nn")
        val aleta = NovaUIHelper.generarTramosConsolidado(62f, 162f, 152f, 1, 1, "nn")
        val paquete = "{nova,apa,[277.6,221.9:$base A<90> $aleta]}"
        val d = DisenoNova.desdePaquete(paquete)
        assertNotNull("el modelo no lee el paquete de la calculadora: $paquete", d)
        assertEquals(2, d!!.tramos.size)

        val silueta = listOf(0f to 60f, 145f to 0f, 280f to 40f, 280f to 220f, 0f to 220f)
        val con = d.conSiluetasYParantesPorLado(listOf(silueta, null), listOf(listOf(100f), emptyList()))
        val ida = con.aPaquete()
        assertTrue("no lleva la silueta: $ida", ida.contains("L<0/60|145/0|280/40|280/220|0/220>"))
        assertTrue("no lleva el parante: $ida", ida.contains(";P;"))
        assertEquals("A<90>", DisenoNova.desdePaquete(ida)!!.tramos[1].pliegue)
    }
}
