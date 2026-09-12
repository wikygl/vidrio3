package crystal.crystal.taller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Los lados de una ventana de esquina: cómo viajan y qué geometría eligen. */
class LadosDeEsquinaTest {

    private fun lado(ancho: Float, alto: Float, puente: Float = 90f) =
        LadoEsquina(ancho, ancho, alto, alto, puente)

    @Test
    fun la_cantidad_de_lados_elige_la_geometria() {
        assertNull("con un lado no hay esquina", EsquinaMedida(listOf(lado(150f, 120f)), emptyList()).geometria)
        assertEquals("nl", EsquinaMedida(List(2) { lado(150f, 120f) }, listOf("90")).geometria)
        assertEquals("nu", EsquinaMedida(List(3) { lado(150f, 120f) }, listOf("90", "90")).geometria)
        assertEquals("ns", EsquinaMedida(List(4) { lado(150f, 120f) }, listOf("90", "90", "90")).geometria)
    }

    @Test
    fun manda_la_medida_mayor_de_cada_lado() {
        // Pared descuadrada: 150 abajo y 152 arriba, 120 a un canto y 118 al otro.
        val l = LadoEsquina(anchoAbajo = 150f, anchoArriba = 152f, altoIzq = 120f, altoDer = 118f, puente = 90f)
        assertEquals("el ancho no es el mayor", 152f, l.ancho, 0.01f)
        assertEquals("el alto no es el mayor", 120f, l.alto, 0.01f)
        assertTrue("no avisa del descuadre", l.descuadrado)
        assertTrue("una pared a plomo no está descuadrada", !lado(150f, 120f).descuadrado)
    }

    @Test
    fun el_texto_va_y_vuelve_entero() {
        val original = EsquinaMedida(
            listOf(
                LadoEsquina(150f, 152f, 120f, 118f, 90f),
                LadoEsquina(120f, 120f, 118f, 118f, 85.5f)
            ),
            listOf("90")
        )
        val vuelta = EsquinaMedida.desdeTexto(EsquinaMedida.aTexto(original))!!
        assertEquals(original.lados, vuelta.lados)
        assertEquals(original.angulos, vuelta.angulos)
        assertEquals("nl", vuelta.geometria)
    }

    @Test
    fun lo_que_hay_que_avisar_se_ve_en_los_angulos() {
        val curva = EsquinaMedida(List(2) { lado(150f, 120f) }, listOf(EsquinaMedida.CURVA))
        assertTrue("no detecta la curva", curva.hayCurva)
        assertTrue("una curva no es un ángulo torcido", curva.anguloDistinto.isEmpty())

        val torcida = EsquinaMedida(List(3) { lado(150f, 120f) }, listOf("90", "135"))
        assertTrue("una esquina en punta no es curva", !torcida.hayCurva)
        assertEquals(listOf(135f), torcida.anguloDistinto)

        // Doblar hacia afuera son -90: sigue siendo escuadra, no hay nada que avisar.
        val afuera = EsquinaMedida(List(2) { lado(150f, 120f) }, listOf("-90"))
        assertTrue("el -90 no es un ángulo raro", afuera.anguloDistinto.isEmpty())
    }

    @Test
    fun sin_lados_no_hay_medida() {
        assertNull(EsquinaMedida.desdeTexto(""))
        assertNull(EsquinaMedida.desdeTexto("   "))
        assertNull(EsquinaMedida.desdeTexto("150,150@90"))
    }
}
