package crystal.crystal.taller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.hypot

/**
 * El polígono se traza a dedo con el lápiz imán y después se escriben las medidas de verdad. Los
 * rectos salen exactos del imán; los inclinados llevan el ángulo del dedo, y con las medidas
 * reales hay que recalcularlo para que la figura cierre exacta.
 *
 * Los casos son los polígonos de prueba P2, P3 y P5: lados inclinados con ternas pitagóricas, así
 * que la dirección correcta se conoce de antemano.
 */
class CierrePoligonoTest {

    private class Boceto(vararg puntos: Pair<Float, Float>) {
        val n = puntos.size
        val dirX = FloatArray(n)
        val dirY = FloatArray(n)
        init {
            for (i in 0 until n) {
                val a = puntos[i]
                val b = puntos[(i + 1) % n]
                val d = hypot(b.first - a.first, b.second - a.second)
                dirX[i] = (b.first - a.first) / d
                dirY[i] = (b.second - a.second) / d
            }
        }
    }

    private fun resolver(b: Boceto, vararg largos: Float) =
        CierrePoligono.resolver(b.dirX, b.dirY, largos.toList().toFloatArray(), tolerancia = 1f)

    /** Recorrer todos los lados tiene que devolver al punto de partida. */
    private fun cierra(r: CierrePoligono.Resultado) {
        var x = 0f
        var y = 0f
        for (i in r.largo.indices) {
            x += r.dirX[i] * r.largo[i]
            y += r.dirY[i] * r.largo[i]
        }
        assertEquals("no cierra en x", 0f, x, 0.01f)
        assertEquals("no cierra en y", 0f, y, 0.01f)
    }

    private fun direccion(r: CierrePoligono.Resultado, lado: Int, dx: Float, dy: Float) {
        assertEquals("dirección x del lado $lado", dx, r.dirX[lado], 0.001f)
        assertEquals("dirección y del lado $lado", dy, r.dirY[lado], 0.001f)
    }

    // P2: escalón con chaflán de 50 (30 × 40). En el boceto el chaflán salió a otro ángulo.
    private val p2 = Boceto(
        0f to 0f, 230f to 0f, 230f to 80f, 140f to 80f, 110f to 105f, 110f to 200f, 0f to 200f
    )

    @Test
    fun un_inclinado_toma_la_direccion_que_cierra_y_los_rectos_no_se_tocan() {
        val r = resolver(p2, 240.5f, 90f, 90f, 50f, 80.5f, 120.5f, 210.5f)
        assertNotNull(r)
        cierra(r!!)
        direccion(r, 3, -0.6f, 0.8f)
        assertEquals(50f, r.largo[3], 0.01f)
        assertEquals(240.5f, r.largo[0], 0f)
        assertEquals(210.5f, r.largo[6], 0f)
        assertTrue("no había nada incoherente", r.incoherentes.isEmpty())
    }

    @Test
    fun un_inclinado_mal_escrito_es_el_unico_senalado_y_la_figura_cierra_igual() {
        val r = resolver(p2, 240.5f, 90f, 90f, 45f, 80.5f, 120.5f, 210.5f)!!
        cierra(r)
        assertEquals(1, r.incoherentes.size)
        val inc = r.incoherentes[0]
        assertEquals(3, inc.lado)
        assertEquals(45f, inc.escrito, 0f)
        assertEquals(50f, inc.correcto, 0.01f)
        assertEquals(50f, r.largo[3], 0.01f)
    }

    @Test
    fun unos_milimetros_en_el_inclinado_se_callan() {
        val r = resolver(p2, 240.5f, 90f, 90f, 49.5f, 80.5f, 120.5f, 210.5f)!!
        cierra(r)
        assertTrue(r.incoherentes.isEmpty())
    }

    // P3: dos inclinados, 130 (50 × 120) y 50 (40 × 30), separados por el lado de arriba.
    private val p3 = Boceto(0f to 0f, 180f to 0f, 180f to 80f, 120f to 190f, 40f to 190f, 0f to 160f)

    @Test
    fun dos_inclinados_salen_de_los_dos_circulos_con_la_solucion_parecida_al_boceto() {
        val r = resolver(p3, 180f, 80f, 130f, 90f, 50f, 170f)!!
        cierra(r)
        direccion(r, 2, -50f / 130f, 120f / 130f)
        direccion(r, 4, -0.8f, -0.6f)
        assertEquals(130f, r.largo[2], 0f)
        assertEquals(50f, r.largo[4], 0f)
        assertTrue(r.incoherentes.isEmpty())
    }

    @Test
    fun dos_inclinados_que_no_alcanzan_se_senalan_los_dos() {
        val r = resolver(p3, 180f, 80f, 60f, 90f, 50f, 170f)!!
        cierra(r)
        assertEquals(setOf(2, 4), r.incoherentes.map { it.lado }.toSet())
        // Cierre pendiente (-90, 90): 127.3 entre los dos, a proporción de 60 y 50.
        val f = hypot(90f, 90f) / 110f
        assertEquals(60f * f, r.incoherentes.first { it.lado == 2 }.correcto, 0.01f)
        assertEquals(50f * f, r.incoherentes.first { it.lado == 4 }.correcto, 0.01f)
    }

    // P5: un solo recto y tres inclinados; el más corto (50) conserva la inclinación del boceto.
    private val p5 = Boceto(0f to 0f, 120f to 0f, 160f to 30f, 70f to 150f, 0f to 80f)

    @Test
    fun tres_inclinados_cierran_con_los_dos_mas_largos_y_el_corto_queda_como_se_dibujo() {
        val r = resolver(p5, 120f, 50f, 150f, 100f, 80f)!!
        cierra(r)
        direccion(r, 1, 0.8f, 0.6f)
        assertEquals(150f, r.largo[2], 0f)
        assertEquals(100f, r.largo[3], 0f)
        assertTrue(r.incoherentes.isEmpty())
    }

    // P5 con sus dos medidas de apoyo: ancho total 150 (de C a la recta de EA) y alto total 160
    // (de D a la recta de AB). Con ellas la forma queda determinada: C en (150, 40), D en (60, 160).
    private val apoyoP5 = listOf(
        CierrePoligono.Escuadra(nodo = 2, lado = 4, distancia = 150f),
        CierrePoligono.Escuadra(nodo = 3, lado = 0, distancia = 160f)
    )

    private fun vertices(r: CierrePoligono.Resultado): List<Pair<Float, Float>> {
        var x = 0f
        var y = 0f
        return r.largo.indices.map { i ->
            val p = x to y
            x += r.dirX[i] * r.largo[i]
            y += r.dirY[i] * r.largo[i]
            p
        }
    }

    @Test
    fun con_las_cotas_de_apoyo_tres_inclinados_quedan_exactos() {
        val r = CierrePoligono.resolver(p5.dirX, p5.dirY, floatArrayOf(120f, 50f, 150f, 100f, 80f), 1f, apoyoP5)!!
        cierra(r)
        val v = vertices(r)
        assertEquals(150f, v[2].first, 0.05f)
        assertEquals(40f, v[2].second, 0.05f)
        assertEquals(60f, v[3].first, 0.05f)
        assertEquals(160f, v[3].second, 0.05f)
        assertEquals(50f, r.largo[1], 0.05f)
        assertEquals(150f, r.largo[2], 0.05f)
        assertEquals(100f, r.largo[3], 0.05f)
        assertTrue(r.incoherentes.isEmpty())
        assertTrue(r.escuadrasIncoherentes.isEmpty())
    }

    @Test
    fun una_cota_de_apoyo_mal_escrita_se_senala_con_lo_que_sale() {
        val apoyo = listOf(apoyoP5[0], CierrePoligono.Escuadra(3, 0, 170f))
        val r = CierrePoligono.resolver(p5.dirX, p5.dirY, floatArrayOf(120f, 50f, 150f, 100f, 80f), 1f, apoyo)!!
        cierra(r)
        // Algo tiene que ceder y se dice qué: la escuadra mal escrita está entre lo señalado
        // (el reparto del error puede salpicar a alguna otra medida, pero nunca callarse).
        val mal = r.escuadrasIncoherentes.firstOrNull { it.lado == 1 }
        assertNotNull("la escuadra de 170 no se señaló: " + r.escuadrasIncoherentes + " " + r.incoherentes, mal)
        assertEquals(170f, mal!!.escrito, 0f)
        assertTrue("lo que sale tiene que acercarse a 160", mal.correcto in 155f..168f)
    }

    @Test
    fun con_apoyo_un_solo_inclinado_sigue_saliendo_exacto() {
        // P2 con el alto total 210.5 medido de E (esquina del chaflán) a la recta de abajo: 130.
        val apoyo = listOf(CierrePoligono.Escuadra(nodo = 4, lado = 0, distancia = 130f))
        val r = CierrePoligono.resolver(p2.dirX, p2.dirY, floatArrayOf(240.5f, 90f, 90f, 50f, 80.5f, 120.5f, 210.5f), 1f, apoyo)!!
        cierra(r)
        direccion(r, 3, -0.6f, 0.8f)
        assertEquals(50f, r.largo[3], 0.05f)
        assertTrue(r.incoherentes.isEmpty())
        assertTrue(r.escuadrasIncoherentes.isEmpty())
    }

    @Test
    fun rectangulo_girado_los_lados_opuestos_van_y_vuelven_por_la_misma_recta() {
        val c = 0.8660254f
        val s = 0.5f
        val dirX = floatArrayOf(c, -s, -c, s)
        val dirY = floatArrayOf(s, c, -s, -c)
        val r = CierrePoligono.resolver(dirX, dirY, floatArrayOf(200f, 120f, 200f, 120f), 1f)!!
        cierra(r)
        direccion(r, 0, c, s)
        direccion(r, 2, -c, -s)
        assertTrue(r.incoherentes.isEmpty())
    }

    @Test
    fun sin_inclinados_no_hay_nada_que_resolver() {
        val rect = Boceto(0f to 0f, 100f to 0f, 100f to 60f, 0f to 60f)
        assertNull(resolver(rect, 100f, 60f, 100f, 60f))
    }
}
