package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * El junquillo de "Mari h" enmarca el vidrio de cada paño, así que su alto tiene que ser el mismo
 * paño que mide el vidrio.
 *
 * Venía calculándose aparte —`paranteInterno + un zócalo`, repartido otra vez con nZocalo fijo en 1—
 * y se comía un bastidor: en una 70 x 240 con 2 divisiones daba 82.5 donde el paño mide 86.6. El
 * vidrio de ese mismo paño siempre salió bien (86.4 con 0.2 de holgura), o sea que las dos listas de
 * la misma puerta no coincidían. El error estaba desde el primer commit del archivo.
 *
 * Caso: puerta 70 x 240, paflón 48.1, parante 198, bastidor 8.25.
 */
class JunquillosMariHTest {

    private val paflon = 48.1f
    private val parante = 198f
    private val bastidor = CalculosPuerta.BASTIDOR

    private fun junquillos(nDiv: Int, nZocalo: Int, jun: Float, mocheta: Float = -1f): String =
        CalculosPuerta.textoJunkillos(
            variante = "Mari h",
            jun = jun,
            mocheta = mocheta,
            nPfvcal = CalculosPuerta.nPfvcal(nDiv),
            paflon = paflon,
            bastidor = bastidor,
            nDiv = nDiv,
            paranteInterno = CalculosPuerta.paranteInterno(parante, nZocalo, bastidor),
            marcoSuperior = 65.6f
        )

    /** Dos paños de (181.5 - 8.25) / 2 = 86.625, que es lo que mide el vidrio antes de la holgura. */
    @Test
    fun elAltoEsElDelPano() {
        assertEquals(86.625f, CalculosPuerta.altoPano(181.5f, nDiv = 2, perfil = bastidor), 0.001f)
        assertEquals("86.6 = 4\n48.1 = 4", junquillos(nDiv = 2, nZocalo = 1, jun = 0f))
    }

    @Test
    fun conJunquilloSeDescuentanLosDos() {
        assertEquals("84.2 = 4\n48.1 = 4", junquillos(nDiv = 2, nZocalo = 1, jun = 1.2f))
    }

    /** Con más zócalos el vacío baja y el paño con él; antes el conteo de zócalos se ignoraba. */
    @Test
    fun losZocalosAchicanElPano() {
        // Parante interno con 3 zócalos = 198 - 4·8.25 = 165; dos paños de (165 - 8.25) / 2 = 78.375.
        assertEquals("78.4 = 4\n48.1 = 4", junquillos(nDiv = 2, nZocalo = 3, jun = 0f))
    }

    @Test
    fun conMochetaSeAgreganSusDosPares() {
        assertEquals(
            "86.6 = 4\n48.1 = 4\n65.6 = 2\n36.3 = 2",
            junquillos(nDiv = 2, nZocalo = 1, jun = 0f, mocheta = 36.3f)
        )
    }

    /** Una sola división: el paño es todo el vacío. */
    @Test
    fun sinDivisionesElPanoEsElVacio() {
        assertEquals("181.5 = 2\n48.1 = 2", junquillos(nDiv = 1, nZocalo = 1, jun = 0f))
    }
}
