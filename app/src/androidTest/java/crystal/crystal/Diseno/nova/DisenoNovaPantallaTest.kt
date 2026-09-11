package crystal.crystal.Diseno.nova

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
            enPantalla(esc) { it.pulsarEstructuraParaPruebas("cotas_tramos", mas = true) }
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
            enPantalla(esc) { it.pulsarEstructuraParaPruebas("cotas_tramos", mas = false) }
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el − de tramos no quitó nada", 2, d.nTramos)
        }
    }

    @Test
    fun las_franjas_se_agregan_y_se_quitan_tramo_a_tramo() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            // Bandera en el tramo 2: una mocheta más, solo ahí.
            enPantalla(esc) { it.pulsarEstructuraParaPruebas("cotas_franjas_1", mas = true) }
            esperar()
            val conMas = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el + no agregó en su tramo: ${conMas.aPaquete()}", 3, conMas.tramos[1].franjas.size)
            assertEquals("tocó el tramo 1", 2, conMas.tramos[0].franjas.size)
            assertEquals("tocó el tramo 3", 2, conMas.tramos[2].franjas.size)

            // Y el tramo 1 se queda sin mocheta, sin que los otros se enteren.
            enPantalla(esc) { it.pulsarEstructuraParaPruebas("cotas_franjas_0", mas = false) }
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el − no quitó en su tramo: ${d.aPaquete()}", 1, d.tramos[0].franjas.size)
            assertEquals(3, d.tramos[1].franjas.size)
            assertEquals(2, d.tramos[2].franjas.size)

            // Tres tramos con tres formas distintas: el dibujo tiene que aceptarlo.
            val error = enPantalla(esc) { it.vistaRechazaParaPruebas(d.aPaquete()) }
            assertNull("el dibujo rechaza el diseño: $error\n${d.aPaquete()}", error)
        }
    }

    @Test
    fun el_mas_agrega_un_modulo_al_tramo() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            val antes = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            enPantalla(esc) { it.pulsarModuloParaPruebas(indiceTramo = 1, simbolo = "+") }
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el + no agregó nada", antes.nModulos + 1, d.nModulos)
            assertEquals("no lo agregó al tramo pulsado", 4, d.tramos[1].nModulosSistema)
            assertTrue("el módulo nuevo no es fijo", d.tramos[1].sistema!!.modulos.last().esFijo)
        }
    }

    @Test
    fun tocar_el_recuadro_cambia_fijo_por_corrediza() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            val antes = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            // El primer módulo del tramo 1 es un fijo: tocarlo lo convierte en corrediza.
            assertTrue(antes.tramos[0].sistema!!.modulos[0].esFijo)
            enPantalla(esc) { it.tocarRecuadroModuloParaPruebas(indiceTramo = 0, indiceModulo = 0) }
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertTrue("el toque no cambió el módulo", !d.tramos[0].sistema!!.modulos[0].esFijo)
            assertEquals("cambió el número de módulos", antes.nModulos, d.nModulos)

            // Y el toque de vuelta lo deja como estaba.
            enPantalla(esc) { it.tocarRecuadroModuloParaPruebas(indiceTramo = 0, indiceModulo = 0) }
            esperar()
            val vuelta = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertTrue("no vuelve a fijo", vuelta.tramos[0].sistema!!.modulos[0].esFijo)
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
                enPantalla(esc) { it.pulsarEstructuraParaPruebas("cotas_tramos", mas = true) }
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
                enPantalla(esc) { it.pulsarEstructuraParaPruebas("cotas_tramos", mas = true) }
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

    @Test
    fun cada_tramo_con_su_puente_desde_el_panel() {
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tresTramos) }
            esperar()
            enPantalla(esc) { it.abrirCotasParaPruebas() }
            esperar()
            // Tramo 1 con el puente a 130, tramo 2 sin puente (el alto entero), tramo 3 como está.
            enPantalla(esc) { it.escribirMedidaParaPruebas("cotas_franja_0_0", "130") }
            enPantalla(esc) { it.escribirMedidaParaPruebas("cotas_franja_1_0", "160") }
            esperar(300)
            enPantalla(esc) { it.pulsarAplicarCotasParaPruebas() }
            esperar()
            val d = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el puente del tramo 1 no cambió", 130f, d.tramos[0].sistema!!.alto, 0.2f)
            assertEquals("la mocheta del tramo 1 no absorbió el resto", 30f, d.tramos[0].mochetas[0].alto, 0.2f)
            assertTrue("el tramo 2 se quedó con mocheta", d.tramos[1].mochetas.isEmpty())
            assertEquals("el tramo 3 se movió", 114.2f, d.tramos[2].sistema!!.alto, 0.2f)

            // Y el dibujo tiene que tragarse un diseño con tramos de distinta forma.
            val error = enPantalla(esc) { it.vistaRechazaParaPruebas(d.aPaquete()) }
            assertNull("el dibujo rechaza el diseño: $error\n${d.aPaquete()}", error)
        }
    }


    /**
     * El camino de vuelta a la calculadora: al enviar el diseño, NovaCorrediza vuelve a abrir
     * esta pantalla sin interfaz para redibujar la miniatura. Si ese render se cae, se cae el app
     * entera, y con tramos de distinta forma —uno sin mocheta, otro con dos— es justo donde el
     * dibujo no había pisado nunca.
     *
     * Se hace aquí lo mismo que hace el modo headless, pero sin lanzar la actividad: esa se
     * cierra dentro de onCreate y ActivityScenario no llega a verla.
     */
    @Test
    fun el_render_sin_pantalla_aguanta_tramos_de_distinta_forma() {
        val mezcla = "{nova,ina,[650,160:Tl<234.5>(s<160>(f<58.6>c<58.6>c<58.6>f<58.6>))" +
            " P<2.5> Tl<175.9>(s<114.2>(f<58.6>c<58.6>f<58.6>);m<22.9>(f<175.9>);m<22.9>(f<175.9>))" +
            " P<2.5> Tl<234.5>(s<114.2>(f<58.6>c<58.6>c<58.6>f<58.6>);m<43.3>(f<117.2>f<117.2>))]}"
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val fallo = runCatching {
            val vista = VistaDiseno(ctx)
            vista.actualizarDesdePaquete(mezcla, 0f, 0f, 0f)
            vista.layout(0, 0, 1080, 1920)
            vista.exportarSoloDisenoBitmap(paddingPx = 4)
        }.exceptionOrNull()
        assertNull("el render sin pantalla se cae: $fallo", fallo)
    }

    /**
     * Lo que sale de empezar de cero: las franjas no llevan altura, se reparten solas. Al agregar
     * una mocheta a un tramo cuyo sistema tampoco la lleva, queda `m(f)` —una franja sin altura—,
     * y eso el dibujo no lo había visto nunca.
     */
    @Test
    fun el_render_aguanta_franjas_sin_altura() {
        val casos = listOf(
            "{nova,ina,[150,120:Tl<150>(s(f))]}",
            "{nova,ina,[150,120:Tl<150>(s(f);m(f))]}",
            "{nova,ina,[150,120:Tl<73.7>(s(fc);m(f)) P<2.5> Tl<73.7>(s(f))]}",
            "{nova,ina,[150,120:Tl<73.7>(s(fc);m(f);m(f)) P<2.5> Tl<73.7>(s(f);m(f))]}"
        )
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val fallos = casos.mapNotNull { p ->
            runCatching {
                val vista = VistaDiseno(ctx)
                vista.actualizarDesdePaquete(p, 0f, 0f, 0f)
                vista.layout(0, 0, 1080, 1920)
                vista.exportarSoloDisenoBitmap(paddingPx = 4)
            }.exceptionOrNull()?.let { "$p -> ${it::class.simpleName}: ${it.message}" }
        }
        assertTrue("el render se cae:\n" + fallos.joinToString("\n"), fallos.isEmpty())
    }

    /**
     * Con tres franjas en un tramo, la de arriba no se podía seleccionar: el dibujo guardaba las
     * bandas del PRIMER tramo y las usaba para todos, así que la tercera franja del segundo tramo
     * no existía para el toque.
     */
    @Test
    fun se_puede_seleccionar_la_franja_de_arriba_de_cualquier_tramo() {
        // Tramo 1 con dos franjas, tramo 2 con tres.
        val desigual = "{nova,apa,[400,200:Tl<200>(s<160>(f<100>c<100>);m<40>(f<200>))" +
            " P<2.5> Tl<200>(s<120>(f<100>c<100>);m<40>(f<200>);m<40>(f<200>))]}"
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(desigual) }
            esperar()

            val bandas0 = enPantalla(esc) { it.bandasDeFranjaParaPruebas(0) }
            val bandas1 = enPantalla(esc) { it.bandasDeFranjaParaPruebas(1) }
            assertEquals("el tramo 1 no tiene sus dos bandas", 2, bandas0.size)
            assertEquals("el tramo 2 no tiene sus tres bandas", 3, bandas1.size)

            // Y un toque de verdad en la franja de arriba del tramo 2 la selecciona.
            val anchos = enPantalla(esc) { it.anchosDeTramoParaPruebas() }
            val (x0, x1) = anchos[1]
            val (top, bottom) = bandas1[2]
            val seleccion = enPantalla(esc) {
                it.tocarLienzoParaPruebas((x0 + x1) / 2f, (top + bottom) / 2f)
            }
            esperar()
            assertEquals("no seleccionó el tramo tocado", 1, seleccion.first)
            assertEquals("no seleccionó la franja de arriba", 2, seleccion.second)
        }
    }

    /**
     * El segundo toque en una franja tiene que pasar a elegir módulo —es lo que intensifica el
     * color y abre el ancho del módulo—. En la franja de arriba de un tramo con tres no llegaba.
     */
    @Test
    fun el_segundo_toque_en_la_franja_de_arriba_elige_modulo() {
        val casos = mapOf(
            "apa" to ("{nova,apa,[400,200:Tl<200>(s<160>(f<100>c<100>);m<40>(f<200>))" +
                " P<2.5> Tl<200>(s<120>(f<100>c<100>);m<40>(f<200>);m<40>(f<200>))]}"),
            "ina" to ("{nova,ina,[400,200:Tl<200>(s<160>(f<100>c<100>);m<40>(f<200>))" +
                " P<2.5> Tl<200>(s<120>(f<100>c<100>);m<40>(f<200>);m<40>(f<200>))]}")
        )
        val problemas = mutableListOf<String>()
        for ((nombre, paquete) in casos) {
            ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
                esperar()
                enPantalla(esc) { it.cargarParaPruebas(paquete) }
                esperar()
                val bandas = enPantalla(esc) { it.bandasDeFranjaParaPruebas(1) }
                val anchos = enPantalla(esc) { it.anchosDeTramoParaPruebas() }
                if (bandas.size < 3 || anchos.size < 2) {
                    problemas.add("$nombre: bandas=${bandas.size} tramos=${anchos.size}")
                    return@use
                }
                val x = (anchos[1].first + anchos[1].second) / 2f
                val y = (bandas[2].first + bandas[2].second) / 2f

                val primera = enPantalla(esc) { it.tocarLienzoParaPruebas(x, y) }
                esperar(300)
                if (primera != 1 to 2) problemas.add("$nombre: el primer toque dio $primera")

                enPantalla(esc) { it.tocarLienzoParaPruebas(x, y) }
                esperar(300)
                val modulo = enPantalla(esc) { it.moduloActivoParaPruebas() }
                if (modulo < 0) problemas.add("$nombre: el segundo toque no eligió módulo")
            }
        }
        assertTrue(problemas.joinToString("\n"), problemas.isEmpty())
    }

    /**
     * El ancho del módulo tiene que abrirse desde CUALQUIER franja de CUALQUIER tramo. Las dos de
     * arriba no abrían: el diálogo miraba la lista global de franjas de la pantalla —la del primer
     * tramo— y buscaba la franja por su letra, así que la segunda mocheta ni existía.
     */
    @Test
    fun el_ancho_del_modulo_se_abre_en_todas_las_franjas() {
        val mezcla = "{nova,apa,[400,200:Tl<200>(s<160>(f<100>c<100>);m<40>(f<200>))" +
            " P<2.5> Tl<200>(s<120>(f<100>c<100>);m<40>(f<200>);m<40>(f<200>))]}"
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(mezcla) }
            esperar()
            val fallos = mutableListOf<String>()
            // Tramo 1: dos franjas. Tramo 2: tres.
            val aProbar = listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1, 1 to 2)
            for ((tramo, franja) in aProbar) {
                enPantalla(esc) { it.seleccionarParaPruebas(franja = franja, tramo = tramo, modulo = 0) }
                val abrio = enPantalla(esc) { it.abrirAnchoModuloParaPruebas() }
                if (!abrio) fallos.add("tramo $tramo franja $franja")
            }
            assertTrue("no abre el ancho en: " + fallos.joinToString(", "), fallos.isEmpty())
        }
    }

    /**
     * Al tocar una franja sale el mando flotante, y sus botones trabajan sobre ESA franja: poner,
     * quitar y cambiar sus módulos. Se prueba en la mocheta de arriba de un tramo con tres, que
     * es donde el panel no llega —el panel solo maneja la franja de sistema.
     */
    @Test
    fun el_flotante_pone_quita_y_cambia_modulos_de_la_franja_tocada() {
        val mezcla = "{nova,apa,[400,200:Tl<200>(s<160>(f<100>c<100>);m<40>(f<200>))" +
            " P<2.5> Tl<200>(s<120>(f<100>c<100>);m<40>(f<200>);m<40>(f<200>))]}"
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(mezcla) }
            esperar()
            val bandas = enPantalla(esc) { it.bandasDeFranjaParaPruebas(1) }
            val anchos = enPantalla(esc) { it.anchosDeTramoParaPruebas() }
            val x = (anchos[1].first + anchos[1].second) / 2f
            val y = (bandas[2].first + bandas[2].second) / 2f

            enPantalla(esc) { it.tocarLienzoParaPruebas(x, y) }
            esperar()
            assertTrue("no salió el mando flotante", enPantalla(esc) { it.hayFlotanteParaPruebas() })

            // Poner: la mocheta de arriba pasa de un paño a dos, y nadie más se mueve.
            enPantalla(esc) { it.pulsarFlotanteParaPruebas("+") }
            esperar()
            val conMas = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el + no agregó en su franja", 2, conMas.tramos[1].franjas[2].modulos.size)
            assertEquals("tocó la otra mocheta", 1, conMas.tramos[1].franjas[1].modulos.size)
            assertEquals("tocó el sistema", 2, conMas.tramos[1].sistema!!.modulos.size)

            // Cambiar: el segundo paño de esa mocheta pasa a corrediza.
            val toco = enPantalla(esc) { it.pulsarFlotanteParaPruebas("1") }
            assertTrue("no encontró el recuadro 1 en el mando", toco)
            esperar()
            val conCambio = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertTrue(
                "el recuadro no cambió el módulo: ${conCambio.aPaquete()}",
                !conCambio.tramos[1].franjas[2].modulos[1].esFijo
            )

            // Quitar: vuelve a un paño.
            enPantalla(esc) { it.pulsarFlotanteParaPruebas("−") }
            esperar()
            val conMenos = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el − no quitó en su franja", 1, conMenos.tramos[1].franjas[2].modulos.size)
        }
    }

    /**
     * Mantener pulsada una franja abre el mando de franjas: poner y quitar en ESE tramo, sin
     * tocar los demás.
     */
    @Test
    fun el_mando_de_franjas_pone_y_quita_en_su_tramo() {
        val mezcla = "{nova,apa,[400,200:Tl<200>(s<160>(f<100>c<100>);m<40>(f<200>))" +
            " P<2.5> Tl<200>(s<120>(f<100>c<100>);m<40>(f<200>);m<40>(f<200>))]}"
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(mezcla) }
            esperar()
            enPantalla(esc) { it.pulsacionLargaParaPruebas(tramo = 0, franja = 1) }
            esperar()
            assertTrue("no salió el mando de franjas", enPantalla(esc) { it.hayFlotanteParaPruebas() })

            enPantalla(esc) { it.pulsarFlotanteParaPruebas("+") }
            esperar()
            val conMas = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el + no agregó en su tramo", 3, conMas.tramos[0].franjas.size)
            assertEquals("tocó el otro tramo", 3, conMas.tramos[1].franjas.size)

            enPantalla(esc) { it.pulsarFlotanteParaPruebas("−") }
            esperar()
            val conMenos = enPantalla(esc) { DisenoNova.desdePaquete(it.paqueteParaPruebas())!! }
            assertEquals("el − no quitó en su tramo", 2, conMenos.tramos[0].franjas.size)
            assertEquals("tocó el otro tramo", 3, conMenos.tramos[1].franjas.size)
        }
    }

    /**
     * Franjas sin altura: con dos manda la proporción de siempre —5/7 el sistema—, pero con tres
     * o más el alto se reparte en partes iguales. Con la regla vieja la tercera franja salía
     * raquítica.
     */
    @Test
    fun tres_franjas_sin_altura_se_reparten_el_alto_por_igual() {
        val tres = "{nova,apa,[300,200:Tl<300>(s(f<150>c<150>);m(f<300>);m(f<300>))]}"
        val dos = "{nova,apa,[300,200:Tl<300>(s(f<150>c<150>);m(f<300>))]}"
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(null)).use { esc ->
            esperar()
            enPantalla(esc) { it.cargarParaPruebas(tres) }
            esperar()
            val bandas = enPantalla(esc) { it.bandasDeFranjaParaPruebas(0) }
            assertEquals(3, bandas.size)
            val altos = bandas.map { it.second - it.first }
            assertEquals("las tres no son iguales: $altos", altos[0], altos[1], 1f)
            assertEquals("las tres no son iguales: $altos", altos[1], altos[2], 1f)

            enPantalla(esc) { it.cargarParaPruebas(dos) }
            esperar()
            val dosBandas = enPantalla(esc) { it.bandasDeFranjaParaPruebas(0) }
            assertEquals(2, dosBandas.size)
            val sistema = dosBandas[0].second - dosBandas[0].first
            val mocheta = dosBandas[1].second - dosBandas[1].first
            // 5/7 contra 2/7: el sistema mide dos veces y media la mocheta.
            assertEquals("con dos franjas ya no manda 5/7", 2.5f, sistema / mocheta, 0.15f)
        }
    }
}
