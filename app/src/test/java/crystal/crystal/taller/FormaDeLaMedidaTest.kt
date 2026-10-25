package crystal.crystal.taller

import crystal.crystal.Diseno.nova.ContornoEnTramos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La forma de las medidas reales del celular de pruebas (revisión del 2026-09-29), como la reconoce
 * cada calculadora que no es Nova.
 */
class FormaDeLaMedidaTest {

    private fun forma(contorno: String) = FormaDeLaMedida.deContorno(ContornoEnTramos.desdeTexto(contorno))

    /** Pipa, la ventana de 182.4 × 70.8: el dintel baja 10.8 en los últimos 26 cm. */
    @Test
    fun la_ventana_de_pipa_es_escalonada() {
        val f = forma("0,0;156,0;156,10.8;182.1,10.8;182.1,70.8;0,70.8")
        assertEquals(FormaDeLaMedida.Tipo.ESCALONADO, f.tipo)
        assertEquals(2, f.escalones.size)
        assertEquals(156f, f.escalones[0].first, 0.05f)
        assertEquals(70.8f, f.escalones[0].second, 0.05f)
        assertEquals(60f, f.escalones[1].second, 0.05f)
        assertTrue(f.conForma)
    }

    /** Isabel, la puerta de 97.8 × 255.2 con el dintel cortado en diagonal. */
    @Test
    fun la_puerta_de_isabel_tiene_lados_inclinados() {
        val f = forma("0,0;84,0;69.9,51.6;97.8,21.2;97.8,255.2;0,255.2")
        assertEquals(FormaDeLaMedida.Tipo.POLIGONAL, f.tipo)
    }

    /** Pipa, el muro cortina de 201.4 × 232: una muesca en el costado y el piso inclinado. */
    @Test
    fun el_muro_de_pipa_es_poligonal() {
        assertEquals(FormaDeLaMedida.Tipo.POLIGONAL, forma("0,0;201.4,0;201.4,153.9;184.6,140;184.6,166.8;201.4,166.8;201.4,217.3;0,232").tipo)
    }

    /** Un rectángulo que se desvía menos de 2.5 sigue siendo rectangular (N-06), y no avisa. */
    @Test
    fun el_descuadre_no_es_forma() {
        val f = forma("0,0;229.5,0.6;229.5,223.4;0,223.4")
        assertEquals(FormaDeLaMedida.Tipo.DESCUADRE, f.tipo)
        assertFalse(f.conForma)
        assertEquals(FormaDeLaMedida.Tipo.RECTANGULAR, forma("0,0;100,0;100,50;0,50").tipo)
    }

    /** La columna de Ele: un solo rectángulo con algo que entra por el costado. */
    @Test
    fun la_columna_es_un_recorte() {
        val f = forma("0,0;229.5,0.6;229.5,223.4;0.3,223.4;0.3,158.3;36.4,158.3;36.4,61.1;0,61.1")
        assertEquals(FormaDeLaMedida.Tipo.POLIGONAL, f.tipo)
        assertTrue(f.descripcion, f.descripcion.contains("recorte"))
    }

    /** Sin contorno ni esquina, rectangular; una esquina se nombra con sus lados. */
    @Test
    fun medidas_sin_contorno_y_esquinas() {
        val base = ColaCalculadoras.MedidaCalc("Ventana", 100f, 120f, 1f, "x", "")
        assertEquals(FormaDeLaMedida.Tipo.RECTANGULAR, FormaDeLaMedida.de(base).tipo)
        val l = FormaDeLaMedida.de(base.copy(esquina = "150,150,160,160,90;120,120,160,160,90@c90|82|160|90"))
        assertEquals(FormaDeLaMedida.Tipo.ESQUINA, l.tipo)
        assertTrue(l.descripcion, l.descripcion.contains("150 + 120") && l.descripcion.contains("curva"))
        assertEquals(FormaDeLaMedida.Tipo.CIRCULAR, FormaDeLaMedida.de(base.copy(forma = "circular")).tipo)
    }

    /**
     * El vano arqueado (2026-10-21): el contorno unido de líneas y un arco llega con sus arcos, y la
     * forma se dice por su nombre de taller, no "con lados inclinados".
     */
    @Test
    fun el_vano_arqueado_dice_su_arco() {
        val arcos = listOf(FormaDeLaMedida.ArcoDelVano("izquierda", 164f, 39f, 187.7f))
        val texto = FormaDeLaMedida.arcosATexto(arcos)
        assertEquals(arcos, FormaDeLaMedida.arcosDesdeTexto(texto))
        val item = ColaCalculadoras.MedidaCalc("Ventana", 205.6f, 164f, 1f, "x", "", arcos = texto)
        val f = FormaDeLaMedida.de(item)
        assertEquals(FormaDeLaMedida.Tipo.ARQUEADO, f.tipo)
        assertTrue(f.descripcion, f.descripcion.contains("arco rebajado a la izquierda"))
        assertEquals("de medio punto (semicircular)", FormaDeLaMedida.ArcoDelVano("arriba", 100f, 50f, 157.1f).nombre)
        assertEquals("de herradura", FormaDeLaMedida.ArcoDelVano("arriba", 100f, 70f, 200f).nombre)
    }
}
