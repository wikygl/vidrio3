package crystal.crystal.taller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
     * Escribir otra medida sube ESA ESQUINA sola, y nada más.
     *
     * La otra punta del corte —la de (200,40)— se queda donde estaba, así que el corte queda más
     * alto de un lado que del otro. Eso es lo que pasa en obra, y es justo lo que no se podía
     * apuntar cuando se empujaba la arista entera: el corte subía siempre a plomo.
     */
    @Test
    fun escribir_la_medida_sube_solo_esa_esquina() {
        val medida = CotaAEscuadra.desdeNodo(conCorte, 2)!!
        val nuevo = CotaAEscuadra.conDistancia(conCorte, medida, 140f)

        // La esquina sube 20, de y=40 a y=20, sin irse a lo ancho.
        assertEquals(20f, nuevo[2].second, 0.01f)
        assertEquals("se movió a lo ancho", 140f, nuevo[2].first, 0.01f)

        // Y la otra punta del corte no se entera.
        assertEquals("arrastró la otra punta del corte", conCorte[3], nuevo[3])

        // El resto del contorno, igual.
        assertEquals(conCorte[0], nuevo[0])
        assertEquals(conCorte[1], nuevo[1])
        assertEquals(conCorte[4], nuevo[4])
        assertEquals(conCorte[5], nuevo[5])

        // Y la cota vuelve a medir lo que se escribió. Se la vuelve a pedir HACIA ABAJO, que es
        // por donde iba: movida la esquina, el lado más cercano puede ser ya otro —el costado— y
        // preguntar por el más cercano sería preguntar por una cota distinta.
        val despues = CotaAEscuadra.haciaDonde(nuevo, 2, 0f to 1f)!!
        assertEquals(140f, despues.distanciaCm, 0.01f)
    }

    /** Y al revés: una medida más corta baja esa esquina. */
    @Test
    fun una_medida_mas_corta_baja_esa_esquina() {
        val medida = CotaAEscuadra.desdeNodo(conCorte, 2)!!
        val nuevo = CotaAEscuadra.conDistancia(conCorte, medida, 100f)
        assertEquals(60f, nuevo[2].second, 0.01f)
        assertEquals("arrastró la otra punta del corte", conCorte[3], nuevo[3])
        assertEquals(100f, CotaAEscuadra.haciaDonde(nuevo, 2, 0f to 1f)!!.distanciaCm, 0.01f)
    }

    /**
     * Un corte hondo por el COSTADO empuja de lado, no hacia arriba.
     *
     * Rectángulo de 200 x 160 al que le falta una lengua por el costado derecho, entre y=60 e
     * y=100, que entra hasta x=40. Desde su esquina de dentro, lo más cercano a escuadra es el
     * costado izquierdo, a 40, y al escribir otra medida esa esquina se va de lado, no de alto.
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

        val nuevo = CotaAEscuadra.conDistancia(corteLateral, medida, 90f)
        assertEquals("la esquina no se movió de lado", 90f, nuevo[3].first, 0.01f)
        assertEquals("la esquina se movió de alto", 60f, nuevo[3].second, 0.01f)
        assertEquals("arrastró la otra punta del corte", corteLateral[4], nuevo[4])
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

    /**
     * Esa esquina no tiene UN lado de enfrente, tiene dos: el suelo y el costado.
     *
     * Desde la esquina de dentro del corte se llega a escuadra al lado de abajo (120) y también al
     * costado izquierdo (140). Las dos son medidas buenas; cuál se quiere lo dice el que mide.
     */
    @Test
    fun una_esquina_tiene_mas_de_un_lado_de_enfrente() {
        val salen = CotaAEscuadra.candidatasDesdeNodo(conCorte, 2)
        assertEquals("no vio los dos lados de enfrente", 2, salen.size)
        assertEquals("la más corta no es la del suelo", 120f, salen[0].distanciaCm, 0.01f)
        assertEquals("la otra no es la del costado", 140f, salen[1].distanciaCm, 0.01f)
    }

    /** Arrastrando hacia abajo sale la del suelo; hacia el costado, la del costado. */
    @Test
    fun el_arrastre_escoge_el_lado() {
        val abajo = CotaAEscuadra.haciaDonde(conCorte, 2, 0f to 50f)!!
        assertEquals("arrastrando hacia abajo no cogió el suelo", 120f, abajo.distanciaCm, 0.01f)
        assertEquals(4, abajo.ladoOpuesto)

        val alCostado = CotaAEscuadra.haciaDonde(conCorte, 2, -50f to 0f)!!
        assertEquals("arrastrando al costado no cogió el costado", 140f, alCostado.distanciaCm, 0.01f)
        assertEquals(5, alCostado.ladoOpuesto)
    }

    /** Un toque sin arrastre, o un tirón más corto que el mínimo, se queda con la más corta. */
    @Test
    fun sin_arrastre_manda_la_mas_corta() {
        assertEquals(120f, CotaAEscuadra.haciaDonde(conCorte, 2, 0f to 0f)!!.distanciaCm, 0.01f)
        val flojo = CotaAEscuadra.haciaDonde(conCorte, 2, -3f to 0f, minimo = 10f)!!
        assertEquals("un tirón flojo escogió lado", 120f, flojo.distanciaCm, 0.01f)
    }

    /** Y cada cota empuja por SU recta: la del costado mueve la esquina de lado, no de alto. */
    @Test
    fun cada_cota_empuja_por_su_recta() {
        val alCostado = CotaAEscuadra.haciaDonde(conCorte, 2, -50f to 0f)!!
        val nuevo = CotaAEscuadra.conDistancia(conCorte, alCostado, 100f)
        assertEquals("la esquina no se movió de lado", 100f, nuevo[2].first, 0.01f)
        assertEquals("la esquina se movió de alto", 40f, nuevo[2].second, 0.01f)
        assertEquals("arrastró la esquina de arriba del corte", conCorte[1], nuevo[1])
        assertEquals("arrastró la otra punta del corte", conCorte[3], nuevo[3])
    }

    /** Sin esquina que valga no hay candidatas, y eso no revienta. */
    @Test
    fun sin_candidatas_no_hay_a_donde_ir() {
        val rect = listOf(0f to 0f, 200f to 0f, 200f to 160f, 0f to 160f)
        assertTrue(CotaAEscuadra.candidatasDesdeNodo(rect, 0).isEmpty())
        assertNull(CotaAEscuadra.haciaDonde(rect, 0, 0f to 50f))
        assertTrue(CotaAEscuadra.candidatasDesdeNodo(emptyList(), 0).isEmpty())
    }

    /**
     * La cota al lado que NO llega: desde la esquina de fuera del corte (200, 40) hacia arriba, la
     * escuadra cae en (200, 0), que está más allá de donde termina el lado de arriba (140, 0). A
     * escuadra normal ese lado no cuenta; con prolongación sí, y dice desde dónde va la sombra.
     */
    @Test
    fun la_prolongacion_llega_al_lado_que_no_alcanza() {
        val sinProlongar = CotaAEscuadra.candidatasDesdeNodo(conCorte, 3)
        assertTrue("sin prolongación no debería ver el lado de arriba", sinProlongar.none { it.ladoOpuesto == 0 })

        val arriba = CotaAEscuadra.haciaDonde(conCorte, 3, 0f to -50f, conProlongacion = true)!!
        assertEquals(0, arriba.ladoOpuesto)
        assertEquals(40f, arriba.distanciaCm, 0.01f)
        assertTrue("tenía que ser prolongada", arriba.prolongado)
        assertEquals(200f, arriba.pie.first, 0.01f)
        assertEquals(0f, arriba.pie.second, 0.01f)
        assertEquals("la sombra sale de la punta del lado", 140f to 0f, arriba.desde)
    }

    /**
     * Una línea suelta cruzada por delante también se mide, y el dedo va pasando líneas: hasta
     * la primera que no ha pasado. Desde la esquina de dentro del corte (140, 40) hacia abajo hay
     * una línea suelta a 40 (en y = 80) y el suelo a 120.
     */
    @Test
    fun el_dedo_va_pasando_lineas_una_por_una() {
        val linea = listOf((100f to 80f) to (180f to 80f))
        val corto = CotaAEscuadra.haciaDonde(conCorte, 2, 0f to 30f, bordesExtra = linea, porRecorrido = true)!!
        assertEquals("con poco arrastre tenía que coger la línea suelta", 40f, corto.distanciaCm, 0.01f)
        assertTrue("la línea suelta va con índice negativo", corto.ladoOpuesto < 0)

        val pasada = CotaAEscuadra.haciaDonde(conCorte, 2, 0f to 100f, bordesExtra = linea, porRecorrido = true)!!
        assertEquals("pasada la línea, toca el suelo", 120f, pasada.distanciaCm, 0.01f)

        val lejos = CotaAEscuadra.haciaDonde(conCorte, 2, 0f to 300f, bordesExtra = linea, porRecorrido = true)!!
        assertEquals("pasadas todas, se queda con la última", 120f, lejos.distanciaCm, 0.01f)

        // Volviendo a medir una cota ya puesta, manda la que mide lo más parecido a lo que medía.
        val remedida = CotaAEscuadra.haciaDonde(conCorte, 2, 0f to 40f, bordesExtra = linea)!!
        assertEquals(40f, remedida.distanciaCm, 0.01f)
    }
}
