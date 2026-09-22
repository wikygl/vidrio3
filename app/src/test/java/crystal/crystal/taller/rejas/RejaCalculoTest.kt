package crystal.crystal.taller.rejas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La reja de prueba: 100 x 160, marco y tubo de 3.8, paso 15. Interior 92.4 x 152.4.
 */
class RejaCalculoTest {

    private val base = RejaCalculo.Reja(anchoCm = 100f, altoCm = 160f)

    @Test
    fun el_marco_son_dos_enteros_y_dos_entre_ellos() {
        val marco = RejaCalculo.marco(base)
        assertEquals(4, marco.size)
        assertEquals(2, marco.count { kotlin.math.abs(it.largo - 160f) < 0.01f })
        assertEquals(2, marco.count { kotlin.math.abs(it.largo - 92.4f) < 0.01f })
        assertEquals("160 = 2\n92.4 = 2", RejaCalculo.lineas(marco))
    }

    @Test
    fun la_cuadricula_reparte_cada_15_hacia_arriba() {
        // 92.4 / 15 = 6.16 → 7 columnas (6 parantes enteros de 152.4); 152.4 / 15 → 11 filas (10 travesaños por columna).
        val dentro = RejaCalculo.interior(base)
        val parantes = dentro.filter { it.nombre == "Parante" }
        assertEquals(6, parantes.size)
        parantes.forEach { assertEquals(152.4f, it.largo, 0.01f) }
        val travesanos = dentro.filter { it.nombre == "Travesaño" }
        assertEquals(70, travesanos.size)
        // Cada travesaño: (92.4 - 6 x 3.8) / 7 = 9.94.
        travesanos.forEach { assertEquals((92.4f - 6 * 3.8f) / 7f, it.largo, 0.01f) }
        assertEquals(7, RejaCalculo.tramos(92.4f, 15f))
        assertEquals(1, RejaCalculo.tramos(10f, 15f))
    }

    @Test
    fun barrotes_verticales_y_horizontales() {
        // Verticales con un travesaño al medio: 6 barrotes enteros y 7 trozos de travesaño.
        val v = RejaCalculo.interior(base.copy(modelo = ModeloReja.BARROTES_VERTICALES, intermedios = 1))
        assertEquals(6, v.count { it.nombre == "Parante" && kotlin.math.abs(it.largo - 152.4f) < 0.01f })
        assertEquals(7, v.count { it.nombre == "Travesaño" })
        // Sin travesaños: solo los barrotes.
        assertEquals(6, RejaCalculo.interior(base.copy(modelo = ModeloReja.BARROTES_VERTICALES)).size)
        // Horizontales: 152.4 / 15 → 11 filas: 10 travesaños enteros de 92.4; con 1 parante intermedio, 11 trozos.
        val h = RejaCalculo.interior(base.copy(modelo = ModeloReja.BARROTES_HORIZONTALES, intermedios = 1))
        assertEquals(10, h.count { it.nombre == "Travesaño" && kotlin.math.abs(it.largo - 92.4f) < 0.01f })
        val trozos = h.filter { it.nombre == "Parante" }
        assertEquals(11, trozos.size)
        trozos.forEach { assertEquals((152.4f - 10 * 3.8f) / 11f, it.largo, 0.01f) }
    }

    @Test
    fun los_rombos_van_a_45_una_familia_entera_y_la_otra_partida() {
        val d = RejaCalculo.interior(base.copy(modelo = ModeloReja.ROMBOS, pasoCm = 30f))
        val enteras = d.filter { it.nombre == "Diagonal" }
        val partidas = d.filter { it.nombre == "Diagonal partida" }
        assertTrue(enteras.isNotEmpty() && partidas.isNotEmpty())
        assertTrue(d.all { it.a45 })
        // Todas a 45°: el desplazamiento en x es igual al de y.
        d.forEach { assertEquals(kotlin.math.abs(it.x1 - it.x0), kotlin.math.abs(it.y1 - it.y0), 0.01f) }
        // Las enteras se quedan dentro del interior (3.8 .. 96.2 en x, 3.8 .. 156.2 en y).
        enteras.forEach {
            assertTrue(it.x0 >= 3.79f && it.x1 <= 96.21f && it.y0 >= 3.79f && it.y1 <= 156.21f)
        }
        // La más larga de las enteras cruza el ancho entero: 92.4 · √2.
        assertEquals(92.4f * kotlin.math.sqrt(2f), enteras.maxOf { it.largo }, 0.05f)
        // Con paso 30 a lo largo del marco, los rombos miden 30 de punta a punta y su lado es
        // 30 / √2 = 21.2: un trozo entre dos cruces mide ese lado menos el grueso del tubo, 17.4.
        val entreCruces = partidas.filter { kotlin.math.abs(it.largo - (30f / kotlin.math.sqrt(2f) - 3.8f)) < 0.05f }
        assertTrue("trozos entre cruces: ${partidas.map { RejaCalculo.fmt(it.largo) }}", entreCruces.isNotEmpty())
        // Y la lista dice "(45°)".
        assertTrue(RejaCalculo.lineas(d, "Diagonal").lines().all { it.contains("(45°)") })
    }

    @Test
    fun la_espina_lleva_parante_al_medio_y_diagonales_en_espejo() {
        val e = RejaCalculo.interior(base.copy(modelo = ModeloReja.ESPINA, pasoCm = 30f))
        val parante = e.single { it.nombre == "Parante" }
        assertEquals(50f, parante.x0, 0.01f)
        assertEquals(152.4f, parante.largo, 0.01f)
        val diagonales = e.filter { it.nombre == "Diagonal" }
        assertTrue(diagonales.size >= 2 && diagonales.size % 2 == 0)
        // Por cada / de la izquierda hay una \ igual de larga a la derecha.
        val izq = diagonales.filter { it.x1 > it.x0 }.map { RejaCalculo.fmt(it.largo) }.sorted()
        val der = diagonales.filter { it.x1 < it.x0 }.map { RejaCalculo.fmt(it.largo) }.sorted()
        assertEquals(izq, der)
        // Ninguna pasa del parante: las de la izquierda acaban antes de su cara (50 − 1.9).
        diagonales.filter { it.x1 > it.x0 }.forEach { assertTrue(it.x1 <= 48.11f) }
    }

    @Test
    fun el_trabado_alterna_los_travesanos_columna_a_columna() {
        // 7 columnas, 3 tramos: las impares (1, 3, 5, 7) llevan 2 travesaños a 1/3 y 2/3; las pares (2, 4, 6) llevan 3, a 1/6, 3/6 y 5/6.
        val d = RejaCalculo.interior(base.copy(modelo = ModeloReja.TRABADO, intermedios = 3))
        assertEquals(6, d.count { it.nombre == "Parante" && kotlin.math.abs(it.largo - 152.4f) < 0.01f })
        val travesanos = d.filter { it.nombre == "Travesaño" }
        assertEquals(4 * 2 + 3 * 3, travesanos.size)
        travesanos.forEach { assertEquals((92.4f - 6 * 3.8f) / 7f, it.largo, 0.01f) }
        val paso = 152.4f / 3f
        val primera = travesanos.filter { it.x0 < 3.9f }.map { it.y0 - 3.8f }.sorted()
        assertEquals(listOf(paso, 2 * paso).map { RejaCalculo.fmt(it) }, primera.map { RejaCalculo.fmt(it) })
        val segunda = travesanos.filter { it.x0 > 3.9f && it.x0 < 20f }.map { it.y0 - 3.8f }.sorted()
        assertEquals(listOf(0.5f * paso, 1.5f * paso, 2.5f * paso).map { RejaCalculo.fmt(it) }, segunda.map { RejaCalculo.fmt(it) })
        // Sin intermedios escritos, son 3 tramos.
        assertEquals(travesanos.size, RejaCalculo.interior(base.copy(modelo = ModeloReja.TRABADO)).count { it.nombre == "Travesaño" })
    }

    @Test
    fun metros_y_lineas() {
        val todos = RejaCalculo.todos(base.copy(modelo = ModeloReja.BARROTES_VERTICALES))
        // Marco 2·160 + 2·92.4 = 504.8; barrotes 6·152.4 = 914.4 → 14.2 m.
        assertEquals(14.2f, RejaCalculo.metros(todos), 0.01f)
        assertEquals("152.4 = 6", RejaCalculo.lineas(todos, "Parante", "Travesaño"))
    }
}
