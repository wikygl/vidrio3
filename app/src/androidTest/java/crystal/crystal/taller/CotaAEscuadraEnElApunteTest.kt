package crystal.crystal.taller

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * La cota a escuadra, puesta sobre el apunte.
 *
 * La cuenta ya se comprueba en frío (CotaAEscuadraTest); aquí se mira lo otro: que el imán coja la
 * esquina del corte al tocar cerca, que la cota mida lo que hay hasta el lado de enfrente, y que al
 * escribirle otra medida el corte se mueva de verdad en la figura.
 */
@RunWith(AndroidJUnit4::class)
class CotaAEscuadraEnElApunteTest {

    private fun vista(): SketchMedidasView {
        val v = SketchMedidasView(ApplicationProvider.getApplicationContext())
        v.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1080, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(1600, android.view.View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 1600)
        return v
    }

    /** La esquina de dentro del corte de una F1: la que tiene los dos lados del corte. */
    private fun esquinaDelCorte(contorno: List<Pair<Float, Float>>): Int {
        val xMax = contorno.maxOf { it.first }
        val yMax = contorno.maxOf { it.second }
        return contorno.indices.first { i ->
            contorno[i].first < xMax - 1f && contorno[i].second > 1f && contorno[i].second < yMax - 1f
        }
    }

    @Test
    fun el_iman_coge_la_esquina_del_corte_y_mide_hasta_enfrente() {
        val v = vista()
        v.insertarRecurrenteF1()
        val contorno = v.contornoDelCompositeParaPruebas()
        assertTrue("la F1 no salió con su corte: $contorno", contorno.size >= 6)

        val nodo = esquinaDelCorte(contorno)
        val caja = v.cajaDelCompositeParaPruebas()
        // Se toca un poco al lado de la esquina, como haría el dedo.
        var puesta = false
        // Poner la cota avisa por Toast, que pide el hilo de la interfaz.
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            puesta = v.cotaAEscuadraParaPruebas(
                caja.first + v.cmAPixelesParaPruebas(contorno[nodo].first) + 12f,
                caja.second + v.cmAPixelesParaPruebas(contorno[nodo].second) + 12f
            )
        }
        assertTrue("no puso la cota", puesta)

        val medida = v.medidaAEscuadraParaPruebas()
        assertNotNull("la cota no mide nada", medida)
        val alto = contorno.maxOf { it.second }
        assertEquals(
            "no mide lo que hay desde el corte hasta el lado de abajo",
            alto - contorno[nodo].second, medida!!, 1f
        )
    }

    @Test
    fun escribir_la_medida_mueve_el_corte_en_la_figura() {
        val v = vista()
        v.insertarRecurrenteF1()
        val contorno = v.contornoDelCompositeParaPruebas()
        val nodo = esquinaDelCorte(contorno)
        val caja = v.cajaDelCompositeParaPruebas()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            v.cotaAEscuadraParaPruebas(
                caja.first + v.cmAPixelesParaPruebas(contorno[nodo].first) + 12f,
                caja.second + v.cmAPixelesParaPruebas(contorno[nodo].second) + 12f
            )
        }
        val antes = v.medidaAEscuadraParaPruebas()!!

        v.escribirEscuadraParaPruebas(antes + 25f)
        val despues = v.medidaAEscuadraParaPruebas()!!
        assertEquals("la cota no se quedó con lo escrito", antes + 25f, despues, 1.5f)

        // Y el corte se movió en la figura: su esquina subió lo mismo que creció la cota.
        val ahora = v.contornoDelCompositeParaPruebas()
        assertEquals("el contorno perdió puntos", contorno.size, ahora.size)
        assertTrue(
            "la esquina del corte no se movió",
            kotlin.math.abs((contorno[nodo].second - ahora[nodo].second) - 25f) < 2f
        )
    }

    /**
     * Apuntar con el dedo no mueve el lienzo: mientras se elige la esquina, el toque es para eso.
     *
     * Sin esto el dibujo se iba con el dedo al arrastrar para afinar la puntería, y no había manera
     * de acertar la esquina.
     */
    @Test
    fun apuntar_a_la_esquina_no_mueve_el_dibujo() {
        val v = vista()
        v.insertarRecurrenteF1()
        InstrumentationRegistry.getInstrumentation().runOnMainSync { v.activarCotaAEscuadra() }

        val antes = v.contornoDelCompositeParaPruebas()
        val caja = v.cajaDelCompositeParaPruebas()
        val nodo = esquinaDelCorte(antes)
        val x = caja.first + v.cmAPixelesParaPruebas(antes[nodo].first) + 10f
        val y = caja.second + v.cmAPixelesParaPruebas(antes[nodo].second) + 10f

        // Bajar el dedo y arrastrarlo un poco: el lienzo tiene que quedarse quieto.
        val comido = v.toqueParaPruebas(android.view.MotionEvent.ACTION_DOWN, x, y) &&
            v.toqueParaPruebas(android.view.MotionEvent.ACTION_MOVE, x + 40f, y + 40f)
        assertTrue("el toque no se consumió: el lienzo se movería", comido)
        assertEquals("el dibujo se movió al apuntar", antes, v.contornoDelCompositeParaPruebas())

        // Se vuelve sobre la esquina sin soltar —afinar la puntería— y al levantar queda puesta.
        v.toqueParaPruebas(android.view.MotionEvent.ACTION_MOVE, x, y)
        assertEquals("el dibujo se movió al apuntar", antes, v.contornoDelCompositeParaPruebas())
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            v.toqueParaPruebas(android.view.MotionEvent.ACTION_UP, x, y)
        }
        assertNotNull("no puso la cota al levantar el dedo", v.medidaAEscuadraParaPruebas())
    }

    /**
     * Esa esquina tiene dos lados de enfrente, y el arrastre dice cuál.
     *
     * Bajando el dedo en la esquina del corte y tirando HACIA EL COSTADO, la cota no es la de
     * siempre —la que baja al alféizar— sino la que cruza hasta el costado. Es lo que no se podía
     * hacer cuando el programa escogía él solo el lado más cercano.
     */
    @Test
    fun arrastrar_hacia_el_costado_coge_el_otro_lado() {
        val v = vista()
        v.insertarRecurrenteF1()
        val contorno = v.contornoDelCompositeParaPruebas()
        val nodo = esquinaDelCorte(contorno)
        val caja = v.cajaDelCompositeParaPruebas()
        val x = caja.first + v.cmAPixelesParaPruebas(contorno[nodo].first)
        val y = caja.second + v.cmAPixelesParaPruebas(contorno[nodo].second)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            // Se toca la esquina y se tira hacia la izquierda, que es donde está el otro lado.
            v.cotaAEscuadraParaPruebas(x + 10f, y + 10f, x - v.cmAPixelesParaPruebas(40f), y)
        }
        val medida = v.medidaAEscuadraParaPruebas()
        assertNotNull("no puso la cota tirando al costado", medida)
        assertEquals(
            "cogió el lado de abajo en vez del costado",
            contorno[nodo].first, medida!!, 1f
        )

        // Y al escribirle otra medida, el corte se mueve DE LADO, no de alto.
        v.escribirEscuadraParaPruebas(medida - 20f)
        val ahora = v.contornoDelCompositeParaPruebas()
        assertEquals(
            "el corte no se movió de lado",
            contorno[nodo].first - 20f, ahora[nodo].first, 1.5f
        )
        assertEquals(
            "el corte se movió de alto",
            contorno[nodo].second, ahora[nodo].second, 1.5f
        )
    }

    /** Y sin arrastre sigue saliendo la de siempre: la más corta, la que baja al alféizar. */
    @Test
    fun sin_arrastre_sale_la_de_siempre() {
        val v = vista()
        v.insertarRecurrenteF1()
        val contorno = v.contornoDelCompositeParaPruebas()
        val nodo = esquinaDelCorte(contorno)
        val caja = v.cajaDelCompositeParaPruebas()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            v.cotaAEscuadraParaPruebas(
                caja.first + v.cmAPixelesParaPruebas(contorno[nodo].first) + 10f,
                caja.second + v.cmAPixelesParaPruebas(contorno[nodo].second) + 10f
            )
        }
        val alto = contorno.maxOf { it.second }
        assertEquals(
            "sin tirar no cogió la más corta",
            alto - contorno[nodo].second, v.medidaAEscuadraParaPruebas()!!, 1f
        )
    }
}
