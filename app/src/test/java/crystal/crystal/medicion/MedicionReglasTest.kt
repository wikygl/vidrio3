package crystal.crystal.medicion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MedicionReglasTest {
    @Test
    fun `valida medidas base`() {
        val error = MedicionReglas.validarMedidas(
            categoria = "ventana",
            anchoCm = 120f,
            altoCm = 100f,
            alturaPuenteCm = 90f
        )
        assertNull(error)
    }

    @Test
    fun `rechaza puente mayor al alto`() {
        val error = MedicionReglas.validarMedidas(
            categoria = "mampara",
            anchoCm = 120f,
            altoCm = 110f,
            alturaPuenteCm = 130f
        )
        assertNotNull(error)
    }

    @Test
    fun `convierte pulgadas a cm`() {
        val cm = MedicionReglas.convertirACm(10f, UnidadMedida.PULG)
        assertEquals(25.4f, cm, 0.001f)
    }

    @Test
    fun `convierte mm a cm`() {
        val cm = MedicionReglas.convertirACm(250f, UnidadMedida.MM)
        assertEquals(25f, cm, 0.001f)
    }
}

