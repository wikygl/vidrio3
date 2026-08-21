package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * "Lina c" es contraplacada: la plancha tapa una estructura de travesaños. Como la variante no pide
 * un número de divisiones, se calcula solo — se agregan travesaños hasta que cada tramo baje de 40.
 *
 * Caso: puerta 70 x 240, hoja 199 → bastidor 198, vacío 198 - 2·8.25 = 181.5, estructura 3.8.
 *   1 tramo:  181.5          2 tramos: 88.85       3 tramos: 57.97
 *   4 tramos: 42.53          5 tramos: 33.26 ✓
 */
class ContraplacadoLinaTest {

    private val hH = 199f
    private val ancho = 70f
    private val estructura = 3.8f

    private fun vacio() = CalculosLina.interiorContraplacado(hH, ancho)

    @Test
    fun elVacioEsElBastidorMenosSusPaflones() {
        val (a, alt) = vacio()
        assertEquals(48.1f, a, 0.01f)
        assertEquals(181.5f, alt, 0.01f)
    }

    /** Se agregan travesaños hasta bajar de 40: con 3.8 de estructura salen 5 tramos. */
    @Test
    fun agregaTravesanosHastaBajarDe40() {
        val n = CalculosLina.divisionesContraplacado(181.5f, estructura)
        assertEquals(5, n)
        assertEquals(33.26f, CalculosLina.tramoContraplacado(181.5f, estructura, n), 0.01f)
    }

    /**
     * Con paflón de 8.25 adentro basta un tramo menos: cada travesaño se come más alto, así que el
     * reparto baja de 40 antes. Con 3.8 hacen falta 5 tramos y con 8.25, 4 — de 39.19.
     */
    @Test
    fun conEstructuraMasGruesaBastaUnTramoMenos() {
        val n = CalculosLina.divisionesContraplacado(181.5f, 8.25f)
        assertEquals(4, n)
        assertEquals(39.19f, CalculosLina.tramoContraplacado(181.5f, 8.25f, n), 0.01f)
    }

    /** Ningún tramo puede quedar en 40 o más, con cualquier alto de puerta. */
    @Test
    fun ningunTramoLlegaA40() {
        var alto = 60f
        while (alto <= 300f) {
            val n = CalculosLina.divisionesContraplacado(alto, estructura)
            assert(CalculosLina.tramoContraplacado(alto, estructura, n) < 40f) {
                "con vacío $alto quedó un tramo de ${CalculosLina.tramoContraplacado(alto, estructura, n)}"
            }
            alto += 1f
        }
    }

    @Test
    fun losTravesanosSonElAnchoDelVacio() {
        assertEquals("48.1 = 4", CalculosLina.estructuraContraplacado(hH, ancho, estructura = estructura))
    }

    /** Las cotas arrancan en el horizontal de abajo y terminan en el de arriba, siempre creciendo. */
    @Test
    fun cotasAcumuladasDesdeLaBase() {
        val c = CalculosLina.cotasContraplacado(hH, ancho, estructura = estructura)
        assertEquals(6, c.size)               // abajo + 4 travesaños + arriba
        assertEquals(8.25f, c.first(), 0.01f)
        assertEquals(189.75f, c.last(), 0.01f)
        c.zipWithNext().forEach { (a, b) -> assert(b > a) { "la cota $b no avanza sobre $a" } }
    }
}
