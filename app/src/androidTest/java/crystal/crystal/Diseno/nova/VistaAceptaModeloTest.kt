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
 * El dibujo tiene que aceptar lo que escribe el modelo.
 *
 * `actualizarVista` se traga el fallo y dibuja un diseño de emergencia de un solo fijo, así que un
 * paquete rechazado se ve en pantalla como "no pasa nada" aunque el diseño interno sea correcto.
 * Estas pruebas miran justo eso, que es lo que las anteriores no veían.
 */
@RunWith(AndroidJUnit4::class)
class VistaAceptaModeloTest {

    private fun escenario(): ActivityScenario<DisenoNovaActivity> {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val i = Intent(ctx, DisenoNovaActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        return ActivityScenario.launch(i)
    }

    private fun esperar(ms: Long = 500) {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Thread.sleep(ms)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private fun <T> en(esc: ActivityScenario<DisenoNovaActivity>, bloque: (DisenoNovaActivity) -> T): T {
        var r: T? = null
        val latch = CountDownLatch(1)
        esc.onActivity { r = bloque(it); latch.countDown() }
        latch.await(5, TimeUnit.SECONDS)
        @Suppress("UNCHECKED_CAST")
        return r as T
    }

    /**
     * Y acepta una ventana de esquina con la pared curva metida entre dos rectas.
     *
     * La curva es un paño más —su ancho es el desarrollo, que es el aluminio que se corta— con el
     * tag del arco dentro de su sistema y SIN pliegue a los lados: la pared no dobla contra la
     * curva, entra en ella. Si el dibujo rechazara ese paquete, en pantalla saldría el diseño de
     * emergencia y parecería que no pasa nada.
     */
    /**
     * Una ventana de esquina se abre por lados, se edita cada uno de frente y al enviarla vuelve
     * entera, con su esquina.
     *
     * Es la estrategia buena: mientras se edita no hay esquina que perder, así que da igual por
     * cuál de sus caminos reescriba la pantalla el paquete.
     */
    @Test
    fun la_esquina_se_edita_lado_a_lado_y_vuelve_entera() {
        val conCurva = "{nova,ina,[150,120:Tl<150>(H<120>;m<30>(f);s(fc))" +
            " Tl<157.1>(H<120>;Q<29.3>;m<30>(f);s(fc))" +
            " A<90> Tl<120>(H<120>;m<30>(f);s(fc))]}"
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = Intent(ctx, DisenoNovaActivity::class.java)
            .putExtra(DisenoNovaActivity.EXTRA_PAQUETE, conCurva)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        ActivityScenario.launch<DisenoNovaActivity>(intent).use { esc ->
            esperar()
            assertEquals("no se abrió por lados", 3, en(esc) { it.ladosParaPruebas() })

            // Cada lado se ve de frente: un solo tramo, sin pliegue ni panza.
            val lado1 = DisenoNova.desdePaquete(en(esc) { it.paqueteParaPruebas() })!!
            assertEquals("el primer lado no está solo", 1, lado1.tramos.size)
            assertEquals("el primer lado no mide lo suyo", 150f, lado1.ancho, 0.5f)
            assertNull("un lado suelto no dobla", lado1.tramos[0].pliegue)

            // Se pasa a la pared curva y se deja en un fijo de piso a techo, de frente.
            en(esc) { it.irAlLadoParaPruebas(1) }
            esperar(300)
            val curva = DisenoNova.desdePaquete(en(esc) { it.paqueteParaPruebas() })!!
            assertEquals("la pared curva no mide su desarrollo", 157.1f, curva.ancho, 0.5f)
            assertEquals("la curva se abrió curvada", 0f, curva.tramos[0].flecha, 0.01f)
            // La tarjeta de arriba tiene que hablar del lado que se está editando, no del anterior.
            val info = en(esc) { it.infoParaPruebas() }
            assertTrue("la tarjeta se quedó en el lado anterior: $info", info.contains("157.1"))
            assertTrue("la tarjeta sigue con el lado anterior: $info", !info.contains("150"))
            // Y el panel de medidas, si está abierto, habla del lado de ahora: escribir un ancho
            // en las casillas del lado anterior es la manera más rápida de equivocar una pared.
            en(esc) { it.abrirPanelCotasParaPruebas() }
            esperar(300)
            val anchosLado2 = en(esc) { it.anchosDelPanelParaPruebas() }
            assertEquals("el panel no trae un solo tramo: $anchosLado2", 1, anchosLado2.size)
            assertTrue(
                "el panel se quedó en el lado anterior: $anchosLado2",
                anchosLado2[0].contains("157")
            )
            en(esc) { it.irAlLadoParaPruebas(2) }
            esperar(300)
            val anchosLado3 = en(esc) { it.anchosDelPanelParaPruebas() }
            assertTrue(
                "el panel no siguió al lado nuevo: $anchosLado3",
                anchosLado3.isNotEmpty() && anchosLado3[0].contains("120")
            )
            en(esc) { it.irAlLadoParaPruebas(1) }
            esperar(300)
            en(esc) { it.cargarParaPruebas("{nova,ina,[157.1,120:Tl<157.1>(H<120>;s(f))]}") }
            esperar(300)

            // El botón de armar: la ventana entera, sin salir del editor, para poder mirarla.
            en(esc) { it.alternarVistaEnteraParaPruebas() }
            esperar(300)
            val armada = DisenoNova.desdePaquete(en(esc) { it.paqueteParaPruebas() })
            assertNotNull("no armó la ventana", armada)
            assertEquals("no salieron los tres lados armados", 3, armada!!.tramos.size)
            assertEquals("la esquina no está en la armada", "A<90>", armada.tramos[2].pliegue)
            assertEquals("la panza no está en la armada", 29.3f, armada.tramos[1].flecha, 0.1f)

            // Y vuelve al lado que se estaba editando, con lo editado en su sitio.
            en(esc) { it.alternarVistaEnteraParaPruebas() }
            esperar(300)
            val deVuelta = DisenoNova.desdePaquete(en(esc) { it.paqueteParaPruebas() })!!
            assertEquals("no volvió a un lado suelto", 1, deVuelta.tramos.size)
            assertEquals("volvió a otro lado", 157.1f, deVuelta.ancho, 0.5f)
            assertEquals(
                "se perdió lo editado al mirar la ventana",
                1, deVuelta.tramos[0].nModulosSistema
            )

            // Y al enviarla, la ventana vuelve entera: los tres lados, la esquina y la panza.
            val entera = en(esc) { it.paqueteDeLaEsquinaParaPruebas() }
            assertNotNull("no devolvió la ventana entera", entera)
            val d = DisenoNova.desdePaquete(entera!!)
            assertNotNull("lo que devolvió no se lee: $entera", d)
            assertEquals("se perdió un lado: $entera", 3, d!!.tramos.size)
            assertEquals("la curva perdió su panza: $entera", 29.3f, d.tramos[1].flecha, 0.1f)
            assertEquals("la ventana se enderezó: $entera", "A<90>", d.tramos[2].pliegue)
            assertEquals("no se guardó lo editado: $entera", 1, d.tramos[1].nModulosSistema)
            assertEquals("el ancho no es el del primer lado: $entera", 150f, d.ancho, 0.5f)
        }
    }

    /**
     * La pantalla puede editar una ventana de esquina sin enderezarla.
     *
     * Se carga, se lee lo que la pantalla tiene, y la esquina sigue ahí: el pliegue entre sus dos
     * paredes y la panza de la curva. Antes el editor devolvía los tramos en fila, con los
     * pliegues amontonados al final, y al aplicar el diseño la ventana salía rectangular.
     */
    @Test
    fun la_pantalla_edita_la_esquina_sin_enderezarla() {
        val conCurva = "{nova,ina,[150,120:Tl<150>(H<120>;m<30>(f);s(fc))" +
            " Tl<157.1>(H<120>;Q<29.3>;m<30>(f);s(fc))" +
            " A<90> Tl<120>(H<120>;m<30>(f);s(fc))]}"
        escenario().use { esc ->
            esperar()
            en(esc) { it.cargarParaPruebas(conCurva) }
            esperar(300)

            val flechas = en(esc) { it.flechasDeTramoParaPruebas() }
            assertEquals("no son tres paños: $flechas", 3, flechas.size)
            assertEquals("la curva salió recta en la pantalla", 29.3f, flechas[1], 0.1f)

            val devuelto = en(esc) { it.paqueteParaPruebas() }
            val modelo = DisenoNova.desdePaquete(devuelto)
            assertNotNull("la pantalla devolvió algo que no se lee: $devuelto", modelo)
            assertEquals("se perdió una pared: $devuelto", 3, modelo!!.tramos.size)
            assertEquals("la curva perdió su panza: $devuelto", 29.3f, modelo.tramos[1].flecha, 0.1f)
            assertEquals("la ventana se enderezó: $devuelto", "A<90>", modelo.tramos[2].pliegue)

            // Y por el camino de "Aplicar" del panel de cotas, que parte el paquete en tramos y
            // lo vuelve a escribir: ahí se perdía la esquina y el lado en perspectiva se ponía
            // de frente.
            val rearmado = en(esc) { it.rearmarPaqueteParaPruebas() }
            val tras = DisenoNova.desdePaquete(rearmado)
            assertNotNull("el rearmado no se lee: $rearmado", tras)
            assertEquals("se perdió una pared al aplicar: $rearmado", 3, tras!!.tramos.size)
            assertEquals(
                "la esquina desapareció al aplicar: $rearmado",
                "A<90>", tras.tramos[2].pliegue
            )
            assertEquals(
                "la curva se enderezó al aplicar: $rearmado",
                29.3f, tras.tramos[1].flecha, 0.1f
            )
            // Y sin cambiar de tamaño: en una ventana de esquina cada tramo se mide contra SU
            // pared, así que el ancho de la ventana es el del primer lado, no la suma de todos.
            // Sumándolos, la ventana pasaba de 150 a 432 y el dibujo se reescalaba al aplicar.
            assertEquals("la ventana cambió de ancho al aplicar: $rearmado", 150f, tras.ancho, 0.5f)
            assertEquals("el primer lado cambió: $rearmado", 150f, tras.tramos[0].ancho, 0.5f)
            assertEquals("la curva cambió: $rearmado", 157.1f, tras.tramos[1].ancho, 0.5f)
            assertEquals("el último lado cambió: $rearmado", 120f, tras.tramos[2].ancho, 0.5f)
        }
    }

    @Test
    fun la_vista_acepta_la_esquina_curva() {

        fun tramos(ancho: Float): String = crystal.crystal.taller.nova.NovaUIHelper
            .generarTramosConsolidado(
                ancho = ancho,
                alto = 120f,
                altoHoja = crystal.crystal.taller.nova.NovaCalculos.altoHoja(120f, 90f),
                divisiones = crystal.crystal.taller.nova.NovaCalculos.divisiones(ancho, 0, "nn"),
                siNoMoch = crystal.crystal.taller.nova.NovaCalculos.siNoMoch(120f, 90f),
                texto = "nn"
            )
        // El tag del arco, dentro de la franja de sistema, igual que lo pone la calculadora.
        val sistema = Regex("s(?:<[^>]*>)?\\([^)]*\\)", RegexOption.IGNORE_CASE)
        fun conArco(t: String): String {
            val m = sistema.find(t) ?: return t
            val cierre = m.value.lastIndexOf(')')
            return t.replaceRange(
                m.range,
                m.value.substring(0, cierre) + "Q<29.3>" + m.value.substring(cierre)
            )
        }
        val curva = conArco(tramos(157.1f))
        val casos = linkedMapOf(
            "en L con la esquina curva" to
                "{nova,ina,[150,120:${tramos(150f)} $curva A<90> ${tramos(120f)}]}",
            "en C con una esquina de cada clase" to
                "{nova,ina,[120,120:${tramos(120f)} A<90> ${tramos(150f)} $curva A<90> ${tramos(120f)}]}"
        )
        escenario().use { esc ->
            esperar()
            val problemas = mutableListOf<String>()
            for ((nombre, paquete) in casos) {
                val error = en(esc) { it.vistaRechazaParaPruebas(paquete) }
                if (error != null) problemas.add("$nombre -> $error\n    $paquete")
            }
            assertNull(
                "la vista rechaza la esquina curva:\n" + problemas.joinToString("\n"),
                problemas.takeIf { it.isNotEmpty() }?.joinToString("\n")
            )

            // Y la panza queda donde tiene que estar: solo en el paño de la curva. Aceptar el
            // paquete no basta, que un tag que no se entiende se ignora sin quejarse y la pared
            // saldría recta.
            en(esc) { it.cargarParaPruebas(casos.values.first()) }
            esperar(300)
            val flechas = en(esc) { it.flechasDeTramoParaPruebas() }
            assertEquals("no son tres paños: $flechas", 3, flechas.size)
            assertEquals("el primer paño se curvó", 0f, flechas[0], 0.01f)
            assertEquals("el paño de la curva salió recto", 29.3f, flechas[1], 0.1f)
            assertEquals("el último paño se curvó", 0f, flechas[2], 0.01f)

            // Y un retrato, para poder mirarlo.
            val bmp = en(esc) { it.dibujoParaPruebas() }
            val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
            java.io.File(ctx.getExternalFilesDir(null), "nova_esquina_curva.png")
                .outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    @Test
    fun la_vista_acepta_lo_que_escribe_el_modelo() {

        val casos = linkedMapOf(
            "recién creado inaparente" to DisenoNova.nuevo("ina", 150f, 120f, 120f),
            "recién creado aparente" to DisenoNova.nuevo("apa", 240f, 200f, 200f),
            "con mocheta" to DisenoNova.nuevo("ina", 650f, 160f, 114.2f),
            "tres tramos" to DisenoNova.nuevo("ina", 650f, 160f, 114.2f),
            "tras agregar un módulo" to DisenoNova.nuevo("ina", 150f, 120f, 120f)
                .conModuloAgregado(0, 0, 0, 'c'),
            "tras partir un tramo" to DisenoNova.nuevo("apa", 240f, 200f, 150f)
                .conTramoPartido(0, 1)
        )
        escenario().use { esc ->
            esperar()
            val problemas = mutableListOf<String>()
            for ((nombre, d) in casos) {
                val paquete = d.aPaquete()
                val error = en(esc) { it.vistaRechazaParaPruebas(paquete) }
                if (error != null) problemas.add("$nombre -> $error\n    $paquete")
            }
            assertNull(
                "la vista rechaza el diseño del modelo:\n" + problemas.joinToString("\n"),
                problemas.takeIf { it.isNotEmpty() }?.joinToString("\n")
            )
        }
    }

    /**
     * De frente se dibuja el paño MÁS ANCHO, no el primero.
     *
     * La ventana que empieza en su curva tiene delante un paño corto: dibujándolo de frente, todo
     * lo demás —la pared larga— quedaba escorzado y el dibujo no se entendía.
     */
    @Test
    fun de_frente_va_el_pano_mas_ancho() {
        val empiezaEnCurva = "{nova,ina,[80,166:Tl<80>(H<166>;Q<10.8>;s(f))" +
            " A<90> Tl<220>(H<166>;s(fcc))]}"
        val empiezaPorElLargo = "{nova,ina,[220,166:Tl<220>(H<166>;s(fcc))" +
            " A<90> Tl<80>(H<166>;s(f))]}"
        escenario().use { esc ->
            esperar()
            en(esc) { it.cargarParaPruebas(empiezaEnCurva) }
            esperar(300)
            val a = en(esc) { it.tramosFrontalesParaPruebas() }
            assertEquals("no son dos paños: $a", 2, a.size)
            assertTrue("el paño corto se puso de frente: $a", !a[0])
            assertTrue("la pared larga no quedó de frente: $a", a[1])

            // Y al revés, con el largo delante, de frente sigue yendo el largo.
            en(esc) { it.cargarParaPruebas(empiezaPorElLargo) }
            esperar(300)
            val b = en(esc) { it.tramosFrontalesParaPruebas() }
            assertTrue("la pared larga dejó de ir de frente: $b", b[0])
            assertTrue("el paño corto se puso de frente: $b", !b[1])
        }
    }

    /**
     * Y un retrato de la ventana que empieza en la curva, con la pared larga de frente.
     *
     * La curva queda en perspectiva, pero tiene que seguir viéndose curva: no es un pliegue.
     */
    @Test
    fun retrato_de_la_ventana_que_empieza_en_curva() {
        val empiezaEnCurva = "{nova,ina,[80,166:Tl<80>(H<166>;Q<10.8>;s(f))" +
            " A<90> Tl<210>(H<166>;s(fcc))]}"
        escenario().use { esc ->
            esperar()
            en(esc) { it.cargarParaPruebas(empiezaEnCurva) }
            esperar(300)
            val bmp = en(esc) { it.dibujoParaPruebas() }
            val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
            java.io.File(ctx.getExternalFilesDir(null), "nova_empieza_en_curva.png")
                .outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
            assertEquals(2, en(esc) { it.tramosFrontalesParaPruebas() }.size)
        }
    }

    /**
     * Una C con las dos esquinas curvas tiene que leerse como una C.
     *
     * De frente el paño central, las dos paredes de los lados alejándose —la de delante doblando
     * hacia la izquierda— y las curvas girando en cada esquina.
     */
    @Test
    fun la_c_con_esquinas_curvas_se_lee_como_una_c() {
        val enC = "{nova,ina,[100,160:Tl<100>(H<160>;s(fc))" +
            " Tl<60>(H<160>;Q<8>;s(f)) A<90> Tl<200>(H<160>;s(fcc))" +
            " Tl<60>(H<160>;Q<8>;s(f)) A<90> Tl<100>(H<160>;s(fc))]}"
        escenario().use { esc ->
            esperar()
            en(esc) { it.cargarParaPruebas(enC) }
            esperar(300)
            val frontales = en(esc) { it.tramosFrontalesParaPruebas() }
            assertEquals("no son cinco paños: $frontales", 5, frontales.size)
            assertEquals(
                "de frente tiene que ir el paño central: $frontales",
                2, frontales.indexOfFirst { it }
            )
            assertEquals("hay más de un paño de frente: $frontales", 1, frontales.count { it })

            val flechas = en(esc) { it.flechasDeTramoParaPruebas() }
            assertEquals("las esquinas perdieron su curva: $flechas", 2, flechas.count { it > 0f })

            val bmp = en(esc) { it.dibujoParaPruebas() }
            val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
            java.io.File(ctx.getExternalFilesDir(null), "nova_c_curva.png")
                .outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    /**
     * En el panel de medidas, el ancho de una ventana de esquina es la SUMA de sus lados, curvas
     * incluidas.
     *
     * Salía el de la cabecera del paquete, que es el del PRIMER lado: en una C de 100+60+200+60+100
     * ponía 100 donde tienen que ir 520.
     */
    @Test
    fun el_ancho_de_una_esquina_es_la_suma_de_sus_lados() {
        val enC = "{nova,ina,[100,160:Tl<100>(H<160>;s(fc))" +
            " Tl<60>(H<160>;Q<8>;s(f)) A<90> Tl<200>(H<160>;s(fcc))" +
            " Tl<60>(H<160>;Q<8>;s(f)) A<90> Tl<100>(H<160>;s(fc))]}"
        escenario().use { esc ->
            esperar()
            en(esc) { it.cargarParaPruebas(enC) }
            esperar(300)
            en(esc) { it.abrirPanelCotasParaPruebas() }
            esperar(300)
            val ancho = en(esc) { it.anchoDeVentanaDelPanelParaPruebas() }
            assertEquals("el ancho no es la suma de los lados", "520", ancho)
        }

        // Y una ventana plana sigue diciendo el suyo, que ahí sí es el de la ventana. En pantalla
        // aparte: el panel se arma al abrirlo, así que cargar otro diseño encima no lo rehace.
        escenario().use { esc ->
            esperar()
            en(esc) { it.cargarParaPruebas("{nova,ina,[240,160:Tl<240>(H<160>;s(fc))]}") }
            esperar(300)
            en(esc) { it.abrirPanelCotasParaPruebas() }
            esperar(300)
            assertEquals("240", en(esc) { it.anchoDeVentanaDelPanelParaPruebas() })
        }
    }
}
