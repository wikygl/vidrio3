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
}
