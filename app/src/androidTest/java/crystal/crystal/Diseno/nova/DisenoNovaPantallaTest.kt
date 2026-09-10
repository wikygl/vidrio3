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

    @Test
    fun la_F_agrega_un_fijo_al_tramo() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            val antes = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            enPantalla(esc) { it.pulsarModuloParaPruebas(indiceTramo = 1, simbolo = "F") }
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("la F no agregó nada", antes.nModulos + 1, d.nModulos)
            assertEquals("no lo agregó al tramo pulsado", 4, d.tramos[1].nModulosSistema)
            assertTrue("el módulo nuevo no es fijo", d.tramos[1].sistema!!.modulos.last().esFijo)
        }
    }

    @Test
    fun la_C_agrega_una_corrediza_al_tramo() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            val antes = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            enPantalla(esc) { it.pulsarModuloParaPruebas(indiceTramo = 0, simbolo = "C") }
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("la C no agregó nada", antes.nCorredizas + 1, d.nCorredizas)
            assertTrue("el módulo nuevo no es corrediza", !d.tramos[0].sistema!!.modulos.last().esFijo)
        }
    }

    @Test
    fun el_menos_de_modulos_quita_el_ultimo_del_tramo() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            enPantalla(esc) { it.pulsarModuloParaPruebas(indiceTramo = 2, simbolo = "−") }
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el − no quitó nada", 3, d.tramos[2].nModulosSistema)
            // Los otros tramos no se tocan.
            assertEquals(4, d.tramos[0].nModulosSistema)
            assertEquals(3, d.tramos[1].nModulosSistema)
        }
    }

    @Test
    fun el_panel_de_cotas_no_se_come_la_pantalla() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            enPantalla(esc) { it.abrirCotasParaPruebas() }
            esperar()
            // Seis tramos: con el panel sin límite, esto tapaba el diseño entero.
            repeat(3) {
                enPantalla(esc) { it.pulsarEstructuraParaPruebas(fila = 0, mas = true) }
                esperar(300)
            }
            esperar()
            val (alto, pantalla) = enPantalla(esc) { it.altoDelPanelParaPruebas() }
            assertTrue("el panel ni se ve: $alto", alto > 0)
            assertTrue(
                "el panel ocupa $alto de $pantalla: se come la pantalla",
                alto <= (pantalla * 0.45f).toInt()
            )
        }
    }

    @Test
    fun el_lienzo_se_sube_por_encima_del_panel() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            enPantalla(esc) { it.abrirCotasParaPruebas() }
            esperar()
            repeat(3) {
                enPantalla(esc) { it.pulsarEstructuraParaPruebas(fila = 0, mas = true) }
                esperar(300)
            }
            esperar()
            val (abajoLienzo, arribaControles) = enPantalla(esc) { it.bordesLienzoYControlesParaPruebas() }
            assertTrue(
                "el panel tapa el dibujo: el lienzo acaba en $abajoLienzo y los controles empiezan en $arribaControles",
                abajoLienzo <= arribaControles
            )
        }
    }

    @Test
    fun cambiar_el_ancho_y_el_alto_de_la_ventana_desde_el_panel() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            enPantalla(esc) { it.abrirCotasParaPruebas() }
            esperar()
            enPantalla(esc) { it.escribirMedidaParaPruebas("cotas_ancho", "500") }
            enPantalla(esc) { it.escribirMedidaParaPruebas("cotas_alto", "180") }
            esperar(300)
            enPantalla(esc) { it.pulsarAplicarCotasParaPruebas() }
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el ancho de la ventana no cambió", 500f, d.ancho, 0.2f)
            assertEquals("el alto de la ventana no cambió", 180f, d.alto, 0.2f)
            // Y los tramos se reparten el ancho nuevo: su suma más los parantes lo cierra.
            val suma = d.tramos.sumOf { it.ancho.toDouble() }.toFloat() + d.nParantes * 2.5f
            assertEquals("los tramos no cierran el ancho nuevo", 500f, suma, 0.5f)
        }
    }
}
