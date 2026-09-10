package crystal.crystal.Diseno.nova

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Pruebas que corren EN EL CELULAR: abren la pantalla de diseño de verdad y pulsan sus botones.
 *
 * Las de escritorio comprueban el modelo, pero no ven la pantalla: por eso una edición podía no
 * hacer nada y las pruebas seguían en verde. Aquí se comprueba lo que el vidriero ve.
 */
@RunWith(AndroidJUnit4::class)
class DisenoNovaPantallaTest {

    private fun intentCon(paquete: String?): Intent {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        return Intent(ctx, DisenoNovaActivity::class.java).apply {
            // Tarea nueva y limpia: si la app quedó abierta en el diseño, Android restaura esa
            // pantalla y la prueba acabaría mirando la del usuario en vez de la suya.
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            if (paquete != null) putExtra(DisenoNovaActivity.EXTRA_PAQUETE, paquete)
        }
    }

    /** Deja que la pantalla termine de dibujar antes de mirar. */
    private fun esperar(ms: Long = 600) {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Thread.sleep(ms)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private fun <T> enPantalla(escenario: ActivityScenario<DisenoNovaActivity>, bloque: (DisenoNovaActivity) -> T): T {
        var resultado: T? = null
        val latch = CountDownLatch(1)
        escenario.onActivity { act -> resultado = bloque(act); latch.countDown() }
        latch.await(5, TimeUnit.SECONDS)
        @Suppress("UNCHECKED_CAST")
        return resultado as T
    }

    @Test
    fun conserva_el_diseno_de_tres_tramos_que_se_le_carga() {
        val paquete = "{nova,ina,[650,160:Tl<234.5>(s<114.2>(f<58.6>c<58.6>c<58.6>f<58.6>);m<43.3>(f<117.2>f<117.2>))" +
            " P<2.5> Tl<175.9>(s<114.2>(f<58.6>c<58.6>f<58.6>);m<43.3>(f<175.9>))" +
            " P<2.5> Tl<234.5>(s<114.2>(f<58.6>c<58.6>c<58.6>f<58.6>);m<43.3>(f<117.2>f<117.2>))]}"
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(paquete) }
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas()) }
            assertNotNull("la pantalla no conserva el diseño que recibió", d)
            assertEquals(3, d!!.nTramos)
            assertEquals(11, d.nModulos)
        }
    }

    @Test
    fun crear_desde_cero_no_arranca_con_un_solo_fijo() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas()) }
            assertNotNull(d)
            assertTrue(
                "empezar de cero sigue dando un solo módulo: ${d!!.aPaquete()}",
                d.nModulos > 1
            )
        }
    }

    // ---- Panel de cotas: agregar y quitar tramos y franjas ----
    private val tresTramos =
        "{nova,ina,[650,160:Tl<234.5>(s<114.2>(f<58.6>c<58.6>c<58.6>f<58.6>);m<43.3>(f<117.2>f<117.2>))" +
            " P<2.5> Tl<175.9>(s<114.2>(f<58.6>c<58.6>f<58.6>);m<43.3>(f<175.9>))" +
            " P<2.5> Tl<234.5>(s<114.2>(f<58.6>c<58.6>c<58.6>f<58.6>);m<43.3>(f<117.2>f<117.2>))]}"

    @Test
    fun el_mas_de_tramos_agrega_un_tramo() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            enPantalla(esc) { it.pulsarEstructuraParaPruebas(fila = 0, mas = true) }
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el + de tramos no agregó nada", 4, d.nTramos)
        }
    }

    @Test
    fun el_menos_de_tramos_quita_un_tramo() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            enPantalla(esc) { it.pulsarEstructuraParaPruebas(fila = 0, mas = false) }
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el − de tramos no quitó nada", 2, d.nTramos)
        }
    }

    @Test
    fun el_mas_y_el_menos_de_franjas_mueven_la_mocheta_en_todos_los_tramos() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            enPantalla(esc) { it.pulsarEstructuraParaPruebas(fila = 1, mas = true) }
            esperar()
            val conMas = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertTrue(
                "el + de franjas no agregó en todos los tramos: ${conMas.aPaquete()}",
                conMas.tramos.all { it.franjas.size == 3 }
            )

            enPantalla(esc) { it.pulsarEstructuraParaPruebas(fila = 1, mas = false) }
            esperar()
            val conMenos = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertTrue(
                "el − de franjas no quitó en todos los tramos: ${conMenos.aPaquete()}",
                conMenos.tramos.all { it.franjas.size == 2 }
            )
        }
    }
}
