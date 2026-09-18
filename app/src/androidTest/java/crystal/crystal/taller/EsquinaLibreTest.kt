package crystal.crystal.taller

import android.graphics.PointF
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * La ventana de esquina armada sobre figuras dibujadas a mano: dos paredes de frente, una en L y
 * otra rectangular, con una línea gruesa horizontal (puente) y otra vertical (parante). Al
 * convertir, las paredes quedan pegadas con el pie común, el marco a trazos las abraza, y lo que
 * sale para Nova es lado por lado: medidas, puente, ángulo, contorno de la pared que no es
 * rectángulo y parantes.
 */
@RunWith(AndroidJUnit4::class)
class EsquinaLibreTest {

    private fun vista(): SketchMedidasView {
        val v = SketchMedidasView(ApplicationProvider.getApplicationContext())
        v.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1080, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(1600, android.view.View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 1600)
        return v
    }

    /** Lo que mide en píxeles algo que la vista dio en cm, para comparar con lo dibujado. */
    private fun px(v: SketchMedidasView, cm: Float) = v.cmAPixelesParaPruebas(cm)

    private fun armar(v: SketchMedidasView): Int {
        // Pared 1: una L de 500 x 300 (rectángulo de 300 x 300 y otro de 200 x 200 pegado abajo a
        // la derecha), soldada. Pared 2: un rectángulo de 200 x 250, dibujado separado y más arriba.
        val a = v.agregarRectanguloParaPruebas(100f, 100f, 400f, 400f)
        val b = v.agregarRectanguloParaPruebas(400f, 200f, 600f, 400f)
        v.seleccionarParaPruebas(a, b)
        assertTrue(v.weldSelected())
        val pared2 = v.agregarRectanguloParaPruebas(700f, 120f, 900f, 370f)
        // El puente de la pared 2 y un parante en la pared 1, como líneas gruesas.
        val puente = v.agregarLineaParaPruebas(710f, 250f, 890f, 250f)
        val parante = v.agregarLineaParaPruebas(250f, 130f, 250f, 380f)
        v.seleccionarParaPruebas(puente, parante)
        assertTrue(v.aplicarEstiloASeleccion(2f, null))

        v.seleccionarParaPruebas(0, pared2)
        var marco: Int? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            marco = v.convertirSeleccionEnEsquina()
        }
        assertNotNull("no se armó la esquina", marco)
        return marco!!
    }

    @Test
    fun los_lados_salen_de_las_paredes_con_su_puente_y_su_angulo() {
        val v = vista()
        armar(v)
        val esquina = v.esquinaPrincipalEnCm()
        assertNotNull("la esquina no se lee", esquina)
        assertEquals(2, esquina!!.lados.size)
        assertEquals("ancho de la pared 1", 500f, px(v, esquina.lados[0].ancho), 2f)
        assertEquals("alto de la pared 1", 300f, px(v, esquina.lados[0].alto), 2f)
        assertEquals("la pared 1 no tiene puente", 0f, esquina.lados[0].puente, 0.01f)
        assertEquals("ancho de la pared 2", 200f, px(v, esquina.lados[1].ancho), 2f)
        assertEquals("alto de la pared 2", 250f, px(v, esquina.lados[1].alto), 2f)
        // El puente estaba a 120 px del pie de su pared (370 - 250).
        assertEquals("puente de la pared 2", 120f, px(v, esquina.lados[1].puente), 3f)
        assertEquals(listOf("90"), esquina.angulos)
        assertEquals("nl", esquina.geometria)
    }

    @Test
    fun la_pared_en_l_viaja_con_su_contorno_y_la_rectangular_sin_el() {
        val v = vista()
        armar(v)
        val contornos = v.contornosDeLadosEnCm()
        assertEquals(2, contornos.size)
        assertNotNull("la L tiene que llevar su contorno", contornos[0])
        assertEquals("la L tiene seis esquinas", 6, contornos[0]!!.size)
        assertNull("la pared rectangular no lleva contorno", contornos[1])
        // Relativo a su propia esquina de arriba a la izquierda: empieza en (0, 0) y llega a 500 x 300.
        assertTrue(contornos[0]!!.any { (x, y) -> x < 0.2f && y < 0.2f })
        assertEquals(500f, px(v, contornos[0]!!.maxOf { it.first }), 2f)
        assertEquals(300f, px(v, contornos[0]!!.maxOf { it.second }), 2f)
    }

    @Test
    fun el_parante_grueso_viaja_con_su_pared_y_las_paredes_quedan_pegadas() {
        val v = vista()
        armar(v)
        val parantes = v.parantesDeLadosEnCm()
        assertEquals(2, parantes.size)
        assertEquals("la pared 1 tiene un parante", 1, parantes[0].size)
        assertEquals("a 150 px del canto de la pared 1", 150f, px(v, parantes[0][0]), 2f)
        assertTrue("la pared 2 no tiene parantes", parantes[1].isEmpty())
        // Y la pared 2 se pegó a la 1: arranca en x = 600, con el pie en y = 400.
        val cajas = v.cajasDeParedesParaPruebas()
        assertEquals(600f, cajas[1].left, 1f)
        assertEquals(400f, cajas[1].bottom, 1f)
    }

    @Test
    fun el_texto_de_la_cola_va_y_vuelve() {
        val v = vista()
        armar(v)
        val contornos = LadosLibres.contornosDesdeTexto(LadosLibres.contornosATexto(v.contornosDeLadosEnCm()))
        assertEquals(2, contornos.size)
        assertEquals(6, contornos[0]!!.size)
        assertNull(contornos[1])
        val parantes = LadosLibres.parantesDesdeTexto(LadosLibres.parantesATexto(v.parantesDeLadosEnCm()))
        assertEquals(listOf(1, 0), parantes.map { it.size })
    }

    /**
     * El desarrollo dibujado como UNA figura: una L de 500 x 300 con dos líneas gruesas verticales
     * (a 250 y a 400) y un puente en el trozo de la izquierda. Sin seleccionar nada, el botón la
     * parte por las líneas; la primera arista se deja en 180 (no dobla: es un parante) y la
     * segunda en 90. Salen dos lados: 300 x 300 con su parante a 150, y 200 x 200.
     */
    @Test
    fun una_sola_figura_se_parte_por_sus_lineas_gruesas() {
        val v = vista()
        val a = v.agregarRectanguloParaPruebas(100f, 100f, 400f, 400f)
        val b = v.agregarRectanguloParaPruebas(400f, 200f, 600f, 400f)
        v.seleccionarParaPruebas(a, b)
        assertTrue(v.weldSelected())
        val corte1 = v.agregarLineaParaPruebas(250f, 110f, 250f, 390f)
        val corte2 = v.agregarLineaParaPruebas(400f, 110f, 400f, 390f)
        val puente = v.agregarLineaParaPruebas(110f, 300f, 240f, 300f)
        v.seleccionarParaPruebas(corte1, corte2, puente)
        assertTrue(v.aplicarEstiloASeleccion(2f, null))
        v.seleccionarParaPruebas()

        var marco: Int? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync { marco = v.convertirSeleccionEnEsquina() }
        assertNotNull("no se armó la esquina desde una sola figura", marco)
        assertEquals("tenían que salir tres trozos", 3, v.cajasDeParedesParaPruebas().size)

        v.anguloDeEsquinaParaPruebas(0, 180f)
        v.anguloDeEsquinaParaPruebas(1, 90f)
        val esquina = v.esquinaPrincipalEnCm()
        assertNotNull(esquina)
        assertEquals("la arista a 180 no dobla: son dos lados", 2, esquina!!.lados.size)
        assertEquals(300f, px(v, esquina.lados[0].ancho), 2f)
        assertEquals(300f, px(v, esquina.lados[0].alto), 2f)
        assertEquals("el puente del lado 1", 100f, px(v, esquina.lados[0].puente), 3f)
        assertEquals(200f, px(v, esquina.lados[1].ancho), 2f)
        assertEquals(200f, px(v, esquina.lados[1].alto), 2f)
        assertEquals(listOf("90"), esquina.angulos)

        val parantes = v.parantesDeLadosEnCm()
        assertEquals("la arista recta es un parante del lado 1", 1, parantes[0].size)
        assertEquals(150f, px(v, parantes[0][0]), 2f)
        assertTrue(parantes[1].isEmpty())
        // Los dos trozos del lado 1 vuelven a ser un rectángulo: no hace falta contorno.
        assertEquals(listOf(null, null), v.contornosDeLadosEnCm())
    }

    /**
     * La pared de al lado dibujada en perspectiva, como la dibuja casi todo el mundo: un
     * paralelogramo de 150 de caja que sube 80 entre canto y canto (la diagonal mide 170) y 300
     * de alto, con su puente también en diagonal. Al convertir se endereza: pasa a ser el
     * rectángulo de 170 x 300 pegado a la primera pared, y el puente queda horizontal.
     */
    @Test
    fun la_pared_en_perspectiva_se_endereza_a_sus_medidas_reales() {
        val v = vista()
        v.agregarRectanguloParaPruebas(100f, 100f, 500f, 400f)
        // El paralelogramo: cantos en x = 500 y x = 650, el de la derecha 80 más arriba.
        val lado = v.agregarCuadrilateroParaPruebas(
            PointF(500f, 100f), PointF(650f, 20f), PointF(650f, 320f), PointF(500f, 400f)
        )
        // Su puente, en la misma perspectiva: a 100 del pie, de canto a canto.
        val puente = v.agregarLineaParaPruebas(505f, 297f, 645f, 222f)
        v.seleccionarParaPruebas(puente)
        assertTrue(v.aplicarEstiloASeleccion(2f, null))
        v.seleccionarParaPruebas(0, lado)
        var marco: Int? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync { marco = v.convertirSeleccionEnEsquina() }
        assertNotNull(marco)

        val esquina = v.esquinaPrincipalEnCm()!!
        assertEquals(2, esquina.lados.size)
        assertEquals("el ancho real es la diagonal", 170f, px(v, esquina.lados[1].ancho), 2f)
        assertEquals("el alto real es el canto", 300f, px(v, esquina.lados[1].alto), 2f)
        assertEquals("el puente enderezado, a 100 del pie", 100f, px(v, esquina.lados[1].puente), 4f)
        assertNull("enderezada es un rectángulo: sin contorno", v.contornosDeLadosEnCm()[1])
        val caja = v.cajasDeParedesParaPruebas()[1]
        assertEquals("pegada a la primera pared", 500f, caja.left, 1f)
        assertEquals("con el pie común", 400f, caja.bottom, 1f)
    }
}
