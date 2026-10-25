package crystal.crystal.taller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin

class Escena3dTest {

    @Test
    fun una_pared_en_L_lleva_dos_caras_y_tres_verticales() {
        val armador = ArmadorDeEscena()
        armador.pared(listOf(0f to 0f, 100f to 0f, 100f to 60f), cerrado = false, desde = 0f, hasta = 200f, vista = 1, indice = 0)
        val e = armador.escena()
        assertEquals(2, e.caras.size)
        val verticales = e.aristas.count { it.a.x == it.b.x && it.a.z == it.b.z }
        assertEquals("las dos puntas y la esquina", 3, verticales)
        assertEquals(4 + 3, e.aristas.size)
    }

    @Test
    fun una_pared_curva_solo_lleva_verticales_en_las_puntas() {
        val armador = ArmadorDeEscena()
        // Medio círculo de radio 100 en 12 tramos: cada tramo dobla 15°, menos que una esquina.
        val base = (0..12).map { k ->
            val a = Math.PI * k / 12
            (100f * cos(a).toFloat()) to (100f * sin(a).toFloat())
        }
        armador.pared(base, cerrado = false, desde = 0f, hasta = 250f, vista = 1, indice = 3)
        val e = armador.escena()
        assertEquals(12, e.caras.size)
        assertEquals(2, e.aristas.count { it.a.x == it.b.x && it.a.z == it.b.z })
        val medidas = e.medidasDe(1, 3)
        assertNotNull(medidas)
        val (x, y, z) = medidas!!
        assertEquals(200f, x, 0.01f)
        assertEquals(250f, y, 0.01f)
        assertEquals(100f, z, 0.01f)
    }

    @Test
    fun un_rectangulo_en_la_planta_es_un_cuerpo_con_tapas() {
        val armador = ArmadorDeEscena()
        armador.pared(listOf(0f to 0f, 40f to 0f, 40f to 30f, 0f to 30f, 0f to 0f), cerrado = true, desde = 0f, hasta = 90f, vista = 1, indice = 0)
        val e = armador.escena()
        assertEquals("4 lados y 2 tapas (el punto repetido del cierre no cuenta)", 6, e.caras.size)
        assertEquals(4 * 2 + 4, e.aristas.size)
    }

    @Test
    fun un_plano_cerrado_es_una_cara_y_uno_abierto_solo_aristas() {
        val armador = ArmadorDeEscena()
        armador.plano(listOf(P3(0f, 0f, 0f), P3(100f, 0f, 0f), P3(100f, 210f, 0f), P3(0f, 210f, 0f)), cerrado = true, vista = 0, indice = 0)
        armador.plano(listOf(P3(50f, 0f, 0f), P3(50f, 210f, 0f)), cerrado = false, vista = 0, indice = 1)
        val e = armador.escena()
        assertEquals(1, e.caras.size)
        assertEquals(5, e.aristas.size)
        val (min, max) = e.limites()!!
        assertEquals(P3(0f, 0f, 0f), min)
        assertEquals(P3(100f, 210f, 0f), max)
    }
}
