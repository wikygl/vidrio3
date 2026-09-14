package crystal.crystal.taller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * La cota a escuadra: desde la esquina de un corte hasta el lado de enfrente.
 *
 * Todo en centímetros, sin pantalla: el imán, a qué lado llega la escuadra y qué arista empuja al
 * escribir otra medida. Si esto está bien, dibujarla es pintar una línea.
 */
class CotaAEscuadraTest {

    /**
     * Un rectángulo de 200 x 160 con un corte arriba a la derecha: le falta un trozo de 60 de
     * ancho y 40 de alto. La y crece hacia abajo, como en el apunte.
     *
     *  (0,0) ────────────── (140,0)
     *    │                     │
     *    │                (140,40) ── (200,40)
     *    │                                │
     *  (0,160) ──────────────────────  (200,160)
     */
    private val conCorte = listOf(
        0f to 0f,
        140f to 0f,
        140f to 40f,
        200f to 40f,
        200f to 160f,
        0f to 160f
    )

    /** El imán coge la esquina que tiene más cerca, y solo si está a tiro. */
    @Test
    fun el_iman_coge_la_esquina_de_al_lado() {
        assertEquals(2, CotaAEscuadra.nodoMasCerca(conCorte, 145f to 44f, 20f))
        assertEquals(3, CotaAEscuadra.nodoMasCerca(conCorte, 196f to 38f, 20f))
        assertNull("cogió una esquina que estaba lejos", CotaAEscuadra.nodoMasCerca(conCorte, 100f to 100f, 20f))
    }

    /**
     * Desde la esquina de dentro del corte, la escuadra llega al lado de abajo: 160 − 40 = 120.
     *
     * No llega al de arriba, que es el más cercano en línea recta pero no se alcanza a escuadra
     * desde ahí —la perpendicular caería en su prolongación, fuera del lado—.
     */
    @Test
    fun desde_la_esquina_del_corte_la_escuadra_llega_al_lado_de_abajo() {
        val medida = CotaAEscuadra.desdeNodo(conCorte, 2)
        assertNotNull("no encontró lado al que llegar", medida)
        assertEquals("no mide lo que hay hasta el suelo", 120f, medida!!.distanciaCm, 0.01f)
        assertEquals("el pie de la escuadra no cae debajo de la esquina", 140f, medida.pie.first, 0.01f)
        assertEquals(160f, medida.pie.second, 0.01f)
        assertEquals("el lado de enfrente no es el de abajo", 4, medida.ladoOpuesto)
    }

    /**
     * Y la arista que empuja es la del corte que va PARALELA al suelo: la de (140,40)–(200,40).
     *
     * La otra que sale de esa esquina es vertical; moverla no cambiaría la altura del corte, lo
     * torcería.
     */
    @Test
    fun empuja_la_arista_que_va_paralela_al_lado_de_enfrente() {
        val medida = CotaAEscuadra.desdeNodo(conCorte, 2)!!
        assertEquals("no empuja la arista del corte", 2, medida.aristaQueEmpuja)
    }

    /** Escribir otra medida sube el corte entero, sin torcerlo y sin mover el resto. */
    @Test
    fun escribir_la_medida_sube_el_corte_entero() {
        val medida = CotaAEscuadra.desdeNodo(conCorte, 2)!!
        val nuevo = CotaAEscuadra.conDistancia(conCorte, medida, 140f)

        // Las dos puntas de la arista del corte suben 20, de y=40 a y=20.
        assertEquals(20f, nuevo[2].second, 0.01f)
        assertEquals(20f, nuevo[3].second, 0.01f)
        assertEquals("la arista se torció", nuevo[2].second, nuevo[3].second, 0.01f)
        assertEquals("se movió a lo ancho", 140f, nuevo[2].first, 0.01f)
        assertEquals("se movió a lo ancho", 200f, nuevo[3].first, 0.01f)

        // El resto del contorno se queda donde estaba.
        assertEquals(conCorte[0], nuevo[0])
        assertEquals(conCorte[1], nuevo[1])
        assertEquals(conCorte[4], nuevo[4])
        assertEquals(conCorte[5], nuevo[5])

        // Y la cota vuelve a medir lo que se escribió.
        val despues = CotaAEscuadra.desdeNodo(nuevo, 2)!!
        assertEquals(140f, despues.distanciaCm, 0.01f)
    }

    /** Y al revés: una medida más corta lo baja. */
    @Test
    fun una_medida_mas_corta_baja_el_corte() {
        val medida = CotaAEscuadra.desdeNodo(conCorte, 2)!!
        val nuevo = CotaAEscuadra.conDistancia(conCorte, medida, 100f)
        assertEquals(60f, nuevo[2].second, 0.01f)
        assertEquals(60f, nuevo[3].second, 0.01f)
        assertEquals(100f, CotaAEscuadra.desdeNodo(nuevo, 2)!!.distanciaCm, 0.01f)
    }

    /**
     * Un corte hondo por el COSTADO empuja de lado, no hacia arriba.
     *
     * Rectángulo de 200 x 160 al que le falta una lengua por el costado derecho, entre y=60 e
     * y=100, que entra hasta x=40. Desde su esquina de dentro, lo más cercano a escuadra es el
     * costado izquierdo, a 40, y lo que se empuja es la arista vertical del corte.
     */
    @Test
    fun un_corte_hondo_por_el_costado_empuja_de_lado() {
        val corteLateral = listOf(
            0f to 0f,
            200f to 0f,
            200f to 60f,
            40f to 60f,
            40f to 100f,
            200f to 100f,
            200f to 160f,
            0f to 160f
        )
        val medida = CotaAEscuadra.desdeNodo(corteLateral, 3)!!
        assertEquals("no mide hasta el costado izquierdo", 40f, medida.distanciaCm, 0.01f)
        assertEquals("el pie no cae en el costado", 0f, medida.pie.first, 0.01f)
        assertEquals("no empuja la arista vertical del corte", 3, medida.aristaQueEmpuja)

        val nuevo = CotaAEscuadra.conDistancia(corteLateral, medida, 90f)
        assertEquals("el corte no se movió de lado", 90f, nuevo[3].first, 0.01f)
        assertEquals("el corte no se movió entero", 90f, nuevo[4].first, 0.01f)
        assertEquals("el corte se movió de alto", 60f, nuevo[3].second, 0.01f)
        assertEquals(100f, nuevo[4].second, 0.01f)
    }

    /**
     * En un rectángulo sin cortes no hay cota a escuadra que tomar, y está bien que no la haya.
     *
     * Desde cualquiera de sus esquinas, la escuadra cae justo en otra esquina: eso no es medir
     * contra una pared de enfrente, es volver por el canto que ya se tiene. Y no habría nada que
     * empujar sin torcer la forma. Para eso están las cotas de los lados, que ya existen.
     */
    @Test
    fun un_rectangulo_no_tiene_nada_que_medir_a_escuadra() {
        val rect = listOf(0f to 0f, 200f to 0f, 200f to 160f, 0f to 160f)
        assertNull(CotaAEscuadra.desdeNodo(rect, 0))
        assertNull(CotaAEscuadra.desdeNodo(rect, 2))
    }

    /** Sin contorno no hay cota que sacar. */
    @Test
    fun sin_contorno_no_hay_cota() {
        assertNull(CotaAEscuadra.desdeNodo(emptyList(), 0))
        assertNull(CotaAEscuadra.desdeNodo(listOf(0f to 0f, 10f to 0f), 0))
        assertNull(CotaAEscuadra.desdeNodo(conCorte, 99))
    }
}
