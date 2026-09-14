package crystal.crystal.taller

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
 * La ventana de esquina, vista desde arriba.
 *
 * La alzada dice cómo es cada pared; la planta dice cómo se doblan entre ellas. Los altos que se
 * miden dentro de cada tramo se leen en la planta, sobre el punto donde se tomaron, y en el
 * desarrollo queda solo su marca: con varios tramos, el desarrollo se llenaba de números sin que se
 * supiera cuál era de dónde.
 */
@RunWith(AndroidJUnit4::class)
class PlantaEsquinaTest {

    /** Una vista medida y colocada, que es lo que hace falta para que dibuje. */
    private fun vista(): SketchMedidasView {
        val v = SketchMedidasView(ApplicationProvider.getApplicationContext())
        v.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1080, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(1600, android.view.View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 1600)
        return v
    }

    @Test
    fun el_apunte_de_esquina_se_exporta_con_su_planta() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(
            tramosCm = listOf(150f, 120f),
            altoCm = 120f,
            puenteCm = 90f,
            alfeizarCm = 90f,
            anguloGrados = 90f
        )
        val bmp = v.exportBitmap()
        assertTrue("el apunte salió vacío", bmp.width > 10 && bmp.height > 10)
        // La planta va debajo del desarrollo, así que el papel es más alto que ancho aunque la
        // ventana mida 270 de ancho por 120 de alto.
        assertTrue(
            "la planta no cabe en el papel: ${bmp.width}x${bmp.height}",
            bmp.height > bmp.width * 0.5f
        )

        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val f = java.io.File(ctx.getExternalFilesDir(null), "planta_esquina.png")
        f.outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test
    fun el_ancho_de_cada_tramo_se_toca_tambien_en_la_planta() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        val (total, enPlanta) = v.cotasDeTramoParaPruebas()
        // Una por tramo al pie de la alzada y otra por tramo en la planta.
        assertEquals("no están las cotas de tramo de los dos sitios", 4, total)
        assertEquals("la planta no dejó su ancho tocable", 2, enPlanta)
    }

    /**
     * El ancho escrito en la planta mueve la pared entera: el lado de arriba y el de abajo.
     *
     * En la planta el tramo no es un lado, es la pared; los lados por separado —el descuadre— se
     * apuntan en la alzada. Y ese descuadre no se pierde al escribir aquí: los dos lados se mueven
     * lo mismo, no se igualan.
     */
    @Test
    fun el_ancho_de_la_planta_mueve_los_dos_lados() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        val antes = v.ladosDeTramoParaPruebas(0)!!
        assertEquals("no arrancó a escuadra", antes.first, antes.second, 0.5f)

        v.anchoDeTramoEnPlantaParaPruebas(0, 170f)
        val tras = v.ladosDeTramoParaPruebas(0)!!
        assertEquals("el lado de abajo no tomó la medida", 170f, tras.first, 0.5f)
        assertEquals("el lado de arriba se quedó donde estaba", 170f, tras.second, 0.5f)

        // Con descuadre apuntado: arriba mide 3 más, y al cambiar el ancho lo sigue midiendo.
        v.anchoDeArribaParaPruebas(0, 173f)
        v.anchoDeTramoEnPlantaParaPruebas(0, 160f)
        val conDescuadre = v.ladosDeTramoParaPruebas(0)!!
        assertEquals("se perdió la medida de abajo", 160f, conDescuadre.first, 0.5f)
        assertEquals(
            "el descuadre apuntado se borró: ${conDescuadre.second}",
            163f, conDescuadre.second, 0.6f
        )
    }

    /**
     * La pared puede doblar hacia los dos lados, y lo dice el signo del ángulo: en más hacia
     * adentro, en menos hacia afuera. La que abraza la esquina de un edificio dobla al revés que la
     * que se mete en un rincón.
     */
    @Test
    fun el_signo_del_angulo_dice_hacia_donde_dobla() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)

        v.anguloDeEsquinaParaPruebas(0, 90f)
        val adentro = v.recorridoPlantaParaPruebas()
        assertEquals(3, adentro.size)
        // El primer tramo va a la derecha y el segundo baja: dobla hacia adentro.
        assertEquals("el primer tramo no va a lo ancho", 150f, adentro[1].first, 1f)
        assertTrue("el segundo tramo no dobló hacia adentro: $adentro", adentro[2].second > 100f)
        assertEquals("el segundo tramo se salió de la vertical", adentro[1].first, adentro[2].first, 1f)

        v.anguloDeEsquinaParaPruebas(0, -90f)
        val afuera = v.recorridoPlantaParaPruebas()
        assertEquals(3, afuera.size)
        assertEquals("el primer tramo cambió", 150f, afuera[1].first, 1f)
        assertTrue("el segundo tramo no dobló hacia afuera: $afuera", afuera[2].second < -100f)
    }

    @Test
    fun el_angulo_sale_de_medir_a_los_dos_lados() {
        val v = vista()
        // 10 a cada lado de la esquina: en escuadra, de marca a marca hay 14.1.
        assertEquals(90f, v.anguloPorMedidasParaPruebas(10f, 14.14f)!!, 0.2f)
        // Y en un chaflán de 135°, 18.5.
        assertEquals(135f, v.anguloPorMedidasParaPruebas(10f, 18.48f)!!, 0.2f)
        // Una esquina cerrada a 60° da justo el lado.
        assertEquals(60f, v.anguloPorMedidasParaPruebas(10f, 10f)!!, 0.2f)
        // Y lo que no puede ser, no se calcula: entre las marcas nunca hay más que los dos lados.
        assertNull(v.anguloPorMedidasParaPruebas(10f, 21f))
    }

    @Test
    fun con_tres_tramos_la_planta_dobla_dos_veces() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(
            tramosCm = listOf(120f, 100f, 90f),
            altoCm = 120f,
            anguloGrados = 135f
        )
        val bmp = v.exportBitmap()
        assertTrue("el apunte salió vacío", bmp.width > 10 && bmp.height > 10)
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val f = java.io.File(ctx.getExternalFilesDir(null), "planta_esquina3.png")
        f.outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
    }

    /**
     * La esquina curva: en vez de doblar en punta, la unen con un arco que va de la esquina de una
     * pared a la de la otra. Lo que dobla sale de su desarrollo y su cuerda.
     */
    /**
     * Los rótulos de la banda se vuelven a repartir al cambiar el zoom.
     *
     * Su letra se mide en pantalla para verse siempre igual, pero se colocan en el papel: al
     * reducir, la letra crecía en medidas del dibujo y los sitios no, así que se montaban.
     */
    @Test
    fun los_rotulos_no_se_montan_al_cambiar_el_zoom() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(120f, 100f, 90f), altoCm = 120f)
        assertEquals("ya nacen montados: " + v.diagRotulosParaPruebas(), 0, v.rotulosMontadosParaPruebas())
        // Alejar: la letra pasa a ocupar mucho más en el papel.
        v.zoomParaPruebas(0.35f)
        assertEquals("se montan al alejar", 0, v.rotulosMontadosParaPruebas())
        v.zoomParaPruebas(2.5f)
        assertEquals("se montan al acercar", 0, v.rotulosMontadosParaPruebas())
    }

    /** Un retrato de la pantalla con el apunte corrido, para mirar el identificador. */
    @Test
    fun retrato_de_la_pantalla_con_el_apunte_corrido() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(180f, 150f), altoCm = 120f)
        v.zoomParaPruebas(0.8f)
        v.desplazarParaPruebas(260f, 120f)
        val bmp = v.pantallaParaPruebas()
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val f = java.io.File(ctx.getExternalFilesDir(null), "pantalla_titulo.png")
        f.outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue("no se guardó el retrato", f.exists() && f.length() > 0)
    }

    /** El identificador se recorta a lo que hay de ancho, en vez de salirse del papel. */
    @Test
    fun el_identificador_cabe_en_la_pantalla() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        assertTrue("no cabe de entrada", v.identificadorCabeParaPruebas())
        v.zoomParaPruebas(0.3f)
        assertTrue("no cabe al alejar", v.identificadorCabeParaPruebas())
        v.zoomParaPruebas(3f)
        assertTrue("no cabe al acercar", v.identificadorCabeParaPruebas())
        // Y con el apunte corrido hacia la derecha, que es cuando de verdad se salía: el rótulo
        // arranca en el canto del dibujo, no en el borde de la pantalla.
        v.zoomParaPruebas(0.8f)
        v.desplazarParaPruebas(420f, 0f)
        assertTrue("no cabe con el dibujo corrido a la derecha", v.identificadorCabeParaPruebas())
    }

    /**
     * La curva ocupa su desarrollo en el desarrollo de la ventana: es aluminio que hay que cortar.
     *
     * Se mete entre las dos paredes como un trozo más y el vano crece con él, pero sin ser una
     * esquina: ahí no dobla nada —el giro lo lleva la curva— así que no pide ángulo ninguno.
     */
    @Test
    fun la_curva_suma_su_desarrollo_en_el_alzado() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        assertEquals("el vano no mide la suma de sus paredes", 270f, v.anchoDeLaVentanaParaPruebas(), 1f)
        assertEquals(listOf(150f, 120f), v.anchosDeParedParaPruebas().map { kotlin.math.round(it) })

        // Un cuarto de círculo de radio 100: 157.1 de desarrollo y 141.4 de cuerda.
        v.curvarEsquinaParaPruebas(0, 157.1f, 141.4f)
        assertEquals(
            "el desarrollo de la curva no sumó al vano",
            427.1f, v.anchoDeLaVentanaParaPruebas(), 2f
        )
        val anchos = v.anchosDeParedParaPruebas()
        assertEquals("la curva no entró como un trozo más: $anchos", 3, anchos.size)
        assertEquals("el trozo de la curva no mide su desarrollo", 157.1f, anchos[1], 2f)
        assertEquals("las paredes rectas cambiaron", 150f, anchos[0], 1f)
        assertEquals("las paredes rectas cambiaron", 120f, anchos[2], 1f)

        // Y quitando la curva, el vano vuelve a lo que medían sus paredes.
        v.curvarEsquinaParaPruebas(0, 0f, 0f)
        assertEquals("el vano no volvió a su medida", 270f, v.anchoDeLaVentanaParaPruebas(), 1f)
        assertEquals("el trozo de la curva se quedó", 2, v.anchosDeParedParaPruebas().size)
    }

    @Test
    fun la_esquina_curva_une_las_dos_paredes() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        // Un cuarto de círculo de radio 100: 157.1 de desarrollo y 141.4 de cuerda.
        v.curvarEsquinaParaPruebas(0, 157.1f, 141.4f)

        val arco = v.esquinaCurvaParaPruebas(0)!!
        assertEquals("el desarrollo no es el escrito", 157.1f, arco[0], 0.5f)
        assertEquals("la cuerda no es la escrita", 141.4f, arco[1], 0.5f)
        assertEquals("la flecha no sale del arco", 29.3f, arco[2], 0.5f)
        assertEquals("la curva no dobla los 90°", 90f, arco[3], 1f)

        // En la planta hay un punto más: la curva separa el final de una pared del principio de la
        // otra, y entre esas dos puntas va su cuerda.
        val recorrido = v.recorridoPlantaParaPruebas()
        assertEquals("la curva no se metió entre las paredes", 4, recorrido.size)
        val cuerda = kotlin.math.hypot(
            recorrido[2].first - recorrido[1].first, recorrido[2].second - recorrido[1].second
        )
        assertEquals("entre las dos esquinas no está la cuerda", 141.4f, cuerda, 2f)
        // Y las paredes siguen midiendo lo suyo: la curva no se las come.
        assertEquals("la primera pared cambió", 150f, recorrido[1].first - recorrido[0].first, 1f)
    }

    /** Y un retrato de la esquina curva, para poder mirarla. */
    @Test
    fun retrato_de_la_esquina_curva() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        v.curvarEsquinaParaPruebas(0, 157.1f, 141.4f)
        val bmp = v.exportBitmap()
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val f = java.io.File(ctx.getExternalFilesDir(null), "planta_curva.png")
        f.outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue("no se guardó el retrato", f.exists() && f.length() > 0)
    }

    /** Y el mismo vano doblando hacia afuera, para poder mirarlo. */
    @Test
    fun retrato_de_la_planta_hacia_afuera() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        v.anguloDeEsquinaParaPruebas(0, -90f)
        val bmp = v.exportBitmap()
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val f = java.io.File(ctx.getExternalFilesDir(null), "planta_afuera.png")
        f.outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue("no se guardó el retrato", f.exists() && f.length() > 0)
    }

    /**
     * La ventana que empieza en la curva: un lado de cero, la curva, y el otro lado con medida.
     *
     * Es el vano que arranca redondeado y se engancha a una pared recta. El lado de cero deja de
     * existir en la alzada —no se acota lo que no mide nada— pero sigue estando en la planta, que
     * es por donde se le vuelve a dar medida si hacía falta.
     */
    @Test
    fun la_ventana_puede_empezar_en_la_curva() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        // Un cuarto de círculo de radio 100: 157.1 de desarrollo y 141.4 de cuerda.
        v.curvarEsquinaParaPruebas(0, 157.1f, 141.4f)
        v.anchoDeTramoEnPlantaParaPruebas(0, 0f)

        val anchos = v.anchosDeParedParaPruebas()
        assertEquals("siguen siendo tres trozos: el de cero, la curva y la pared", 3, anchos.size)
        assertEquals("el primer lado no se fue a cero: $anchos", 0f, anchos[0], 0.5f)
        assertEquals("la curva perdió su desarrollo: $anchos", 157.1f, anchos[1], 2f)
        assertEquals("la pared recta cambió: $anchos", 120f, anchos[2], 1f)
        assertEquals(
            "el vano no mide la curva más la pared",
            anchos.sum(), v.anchoDeLaVentanaParaPruebas(), 2f
        )

        // Y a cero por los dos lados: sin descuadre fantasma de un centímetro.
        val lados = v.ladosDeTramoParaPruebas(0)!!
        assertEquals("el pie del lado de cero mide algo", 0f, lados.first, 0.5f)
        assertEquals("el alto del lado de cero mide algo", 0f, lados.second, 0.5f)

        // En la alzada solo se acotan los dos trozos que miden: la curva y la pared.
        val (alPie, arriba) = v.cotasDeAnchoEnAlzadoParaPruebas()
        assertEquals("al pie se acotó el lado de cero", 2, alPie)
        assertEquals("arriba se acotó el lado de cero", 2, arriba)

        // En la planta el recorrido arranca en la curva: la primera pared es un punto, así que sus
        // dos puntas son la misma, y de ahí sale el arco hacia la pared recta.
        val recorrido = v.recorridoPlantaParaPruebas()
        assertEquals("la planta perdió un punto", 4, recorrido.size)
        assertEquals("la pared de cero ocupa sitio", recorrido[0].first, recorrido[1].first, 1f)
        assertEquals("la pared de cero ocupa sitio", recorrido[0].second, recorrido[1].second, 1f)
        val cuerda = kotlin.math.hypot(
            recorrido[2].first - recorrido[1].first, recorrido[2].second - recorrido[1].second
        )
        assertEquals("la curva no sale de la esquina", 141.4f, cuerda, 2f)
        val recta = kotlin.math.hypot(
            recorrido[3].first - recorrido[2].first, recorrido[3].second - recorrido[2].second
        )
        assertEquals("la pared recta no se engancha a la curva", 120f, recta, 1f)
    }

    /** Y su retrato, para poder mirarla. */
    @Test
    fun retrato_de_la_ventana_que_empieza_en_curva() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        v.curvarEsquinaParaPruebas(0, 157.1f, 141.4f)
        v.anchoDeTramoEnPlantaParaPruebas(0, 0f)
        val bmp = v.exportBitmap()
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val f = java.io.File(ctx.getExternalFilesDir(null), "planta_empieza_en_curva.png")
        f.outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue("no se guardó el retrato", f.exists() && f.length() > 0)
    }

    /**
     * El cero se escribe en cualquiera de las tres cotas del tramo y se lleva el lado entero.
     *
     * Da igual tocar el ancho al pie, el de arriba o el de la planta: una pared que no existe no
     * tiene descuadre que apuntar, así que sus dos lados se van a cero. Y se vuelve de ahí sin más
     * que escribirle una medida.
     */
    @Test
    fun el_cero_se_lleva_el_lado_entero_desde_cualquier_cota() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        v.curvarEsquinaParaPruebas(0, 157.1f, 141.4f)

        // Por la cota del pie de la alzada.
        v.anchoDeAbajoParaPruebas(0, 0f)
        var lados = v.ladosDeTramoParaPruebas(0)!!
        assertEquals("el pie no se fue a cero", 0f, lados.first, 0.5f)
        assertEquals("el cabezal se quedó con medida", 0f, lados.second, 0.5f)

        // Y se vuelve: el lado recupera su medida por los dos sitios.
        v.anchoDeTramoEnPlantaParaPruebas(0, 150f)
        lados = v.ladosDeTramoParaPruebas(0)!!
        assertEquals("el lado no volvió", 150f, lados.first, 1f)
        assertEquals("el cabezal no volvió", 150f, lados.second, 1f)

        // Por la cota de arriba, que antes se quejaba del desnivel en vez de borrar el lado.
        v.anchoDeArribaParaPruebas(0, 0f)
        lados = v.ladosDeTramoParaPruebas(0)!!
        assertEquals("el cabezal no se fue a cero", 0f, lados.second, 0.5f)
        assertEquals("el pie se quedó con medida", 0f, lados.first, 0.5f)
    }

    /** Pero no todos a cero: sin ninguna pared no hay ventana que dibujar. */
    @Test
    fun la_ventana_no_se_queda_toda_en_cero() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        v.anchoDeTramoEnPlantaParaPruebas(0, 0f)
        // El aviso sale por Toast, que pide el hilo de la interfaz.
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            v.anchoDeTramoEnPlantaParaPruebas(1, 0f)
        }
        val anchos = v.anchosDeParedParaPruebas()
        assertEquals("la última pared también se fue: $anchos", 120f, anchos[1], 1f)
        assertEquals("el vano se quedó en nada", 120f, v.anchoDeLaVentanaParaPruebas(), 1f)
    }

    /**
     * El apunte de esquina sale lado por lado, que es lo que la calculadora necesita para armarlo
     * en L, en C o en serie.
     */
    @Test
    fun la_ventana_de_esquina_sale_lado_por_lado() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        val enL = v.esquinaPrincipalEnCm()!!
        assertEquals("no salieron dos lados", 2, enL.lados.size)
        assertEquals("nl", enL.geometria)
        assertEquals(150f, enL.lados[0].ancho, 1f)
        assertEquals(120f, enL.lados[1].ancho, 1f)
        assertEquals(120f, enL.lados[0].alto, 1f)
        assertEquals("cada pared lleva su puente", 90f, enL.lados[0].puente, 1f)
        assertEquals("falta el ángulo de la arista", listOf("90"), enL.angulos)

        // Con tres paredes es una C.
        val w = vista()
        w.insertarPlantillaVentanaEsquina(tramosCm = listOf(100f, 150f, 100f), altoCm = 120f)
        val enC = w.esquinaPrincipalEnCm()!!
        assertEquals("nu", enC.geometria)
        assertEquals(3, enC.lados.size)
        assertEquals(2, enC.angulos.size)
    }

    /** La pared curva ocupa su banda en el alzado, pero no es un lado que se corte. */
    @Test
    fun la_pared_curva_no_cuenta_como_lado() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        v.curvarEsquinaParaPruebas(0, 157.1f, 141.4f)
        val medida = v.esquinaPrincipalEnCm()!!
        assertEquals("la curva se coló como pared: ${medida.lados.map { it.ancho }}", 2, medida.lados.size)
        assertEquals("sigue siendo una L", "nl", medida.geometria)
        assertEquals(150f, medida.lados[0].ancho, 1f)
        assertEquals(120f, medida.lados[1].ancho, 1f)
        assertTrue("no avisa de la curva", medida.hayCurva)
        // Y va con lo suyo, que es lo que la calculadora necesita para dibujarla y cortarla.
        val curva = medida.curvaDe(0)!!
        assertEquals("sin desarrollo no hay qué cortar", 157.1f, curva.desarrollo, 1f)
        assertEquals("falta la cuerda", 141.4f, curva.cuerda, 1f)
        assertEquals("la curva no trajo el alto de su paño", 120f, curva.alto, 1f)
        assertEquals("la curva no trajo su puente", 90f, curva.puente, 1f)

        // Y con el primer lado a cero la ventana empieza en la curva: la pared que no existe deja
        // su sitio y lo ocupa la curva, que pasa a ser una pared más. Sigue siendo una esquina.
        v.anchoDeTramoEnPlantaParaPruebas(0, 0f)
        val empezandoEnCurva = v.esquinaPrincipalEnCm()!!
        assertEquals("la curva no ocupó el sitio de la pared", 2, empezandoEnCurva.lados.size)
        assertTrue("la primera pared no es la curva", empezandoEnCurva.lados[0].esCurva)
    }

    /** El descuadre viaja entero, y manda la medida mayor: la ventana tiene que tapar el hueco. */
    @Test
    fun el_lado_descuadrado_manda_su_medida_mayor() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        // El cabezal del primer lado, dos centímetros más largo que su pie.
        v.anchoDeArribaParaPruebas(0, 152f)
        val lado = v.esquinaPrincipalEnCm()!!.lados[0]
        assertEquals("el pie cambió", 150f, lado.anchoAbajo, 1f)
        assertEquals("el cabezal no se apuntó", 152f, lado.anchoArriba, 1f)
        assertEquals("no manda el mayor", 152f, lado.ancho, 1f)
        assertTrue("no avisa del descuadre", lado.descuadrado)
    }

    /**
     * La ventana que empieza en la curva: el primer lado mide cero, así que la curva ocupa su
     * sitio y pasa a ser una pared más.
     *
     * Sin esto quedaba una sola pared —la curva no cuenta como lado— y la medida no llegaba a la
     * calculadora como ventana de esquina: se dibujaba plana.
     */
    @Test
    fun la_curva_ocupa_el_sitio_de_la_pared_que_no_existe() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 220f), altoCm = 166f)
        v.curvarEsquinaParaPruebas(0, 80f, 76f)
        v.anchoDeTramoEnPlantaParaPruebas(0, 0f)

        val medida = v.esquinaPrincipalEnCm()
        assertNotNull("la ventana de esquina no llegó a salir", medida)
        assertEquals("no son dos paredes: ${medida!!.lados.map { it.ancho }}", 2, medida.lados.size)
        assertTrue("la primera pared no es la curva", medida.lados[0].esCurva)
        assertEquals("la curva no mide su desarrollo", 80f, medida.lados[0].ancho, 1f)
        assertEquals("la pared recta cambió", 220f, medida.lados[1].ancho, 1f)
        assertTrue("la pared recta salió curvada", !medida.lados[1].esCurva)
        assertEquals("sigue siendo una L", "nl", medida.geometria)

        // La arista ya no es una curva: la curva es una pared, y ahí lo que hay es su giro.
        assertTrue("la arista sigue marcada como curva", !medida.hayCurva)
        assertEquals("no quedó el giro de la curva", 1, medida.angulos.size)

        // Y el texto la lleva y la trae: las medidas se redondean a un decimal, como todas.
        val vuelta = EsquinaMedida.desdeTexto(EsquinaMedida.aTexto(medida))!!
        assertEquals(2, vuelta.lados.size)
        assertEquals(80f, vuelta.lados[0].ancho, 0.1f)
        assertEquals("la panza no viajó", medida.lados[0].flecha, vuelta.lados[0].flecha, 0.1f)
        assertEquals(220f, vuelta.lados[1].ancho, 0.1f)
        assertTrue("la pared recta volvió curvada", !vuelta.lados[1].esCurva)
    }

    /**
     * Un tramo nuevo nace en ángulo, aunque la esquina de al lado sea curva.
     *
     * La esquina nueva heredaba la etiqueta de la última, así que detrás de una curva salía otra
     * curva sin que nadie la pidiera. El vidriero la curva después si le toca.
     */
    @Test
    fun el_tramo_nuevo_nace_en_angulo_aunque_venga_de_una_curva() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 120f)
        v.curvarEsquinaParaPruebas(0, 157.1f, 141.4f)
        assertTrue("la esquina no quedó curva", v.esquinaPrincipalEnCm()!!.hayCurva)

        v.cambiarTramosParaPruebas(1)
        val medida = v.esquinaPrincipalEnCm()!!
        assertEquals("no entró la pared nueva", 3, medida.lados.size)
        assertEquals("no hay dos esquinas", 2, medida.angulos.size)
        assertTrue("la primera esquina dejó de ser curva", medida.curvaDe(0) != null)
        assertNull("la esquina nueva salió curva", medida.curvaDe(1))
        assertEquals("la esquina nueva no salió en escuadra", 90f, medida.gradosDe(1)!!, 0.5f)
    }

    /**
     * En una ventana en C, curvar la SEGUNDA esquina no toca la primera.
     *
     * Los rótulos de ángulo se ordenaban por dónde están dibujados, y desde que se leen en la
     * planta la segunda esquina de una C cae a la izquierda de la primera: quedaban cruzados, y
     * el desarrollo y la cuerda que se escribían en una curva se le aplicaban a la otra.
     */
    @Test
    fun curvar_la_segunda_esquina_no_toca_la_primera() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(100f, 150f, 100f), altoCm = 120f)
        v.curvarEsquinaParaPruebas(1, 80f, 76f)

        val medida = v.esquinaPrincipalEnCm()!!
        assertEquals("no son tres paredes: ${medida.lados.map { it.ancho }}", 3, medida.lados.size)
        assertEquals("no hay dos esquinas", 2, medida.angulos.size)
        assertNull("la curva se fue a la primera esquina", medida.curvaDe(0))
        assertEquals("la primera esquina dejó de ser la escuadra", 90f, medida.gradosDe(0)!!, 0.5f)
        val curva = medida.curvaDe(1)
        assertNotNull("la segunda esquina no quedó curva", curva)
        assertEquals("el desarrollo no es el escrito", 80f, curva!!.desarrollo, 0.5f)
        assertEquals("la cuerda no es la escrita", 76f, curva.cuerda, 0.5f)

        // Y las paredes siguen midiendo lo suyo: la curva se metió entre la segunda y la tercera.
        assertEquals(100f, medida.lados[0].ancho, 1f)
        assertEquals(150f, medida.lados[1].ancho, 1f)
        assertEquals(100f, medida.lados[2].ancho, 1f)
    }

    /**
     * Los tramos no tienen tope: una ventana en serie lleva los lados que lleve la obra.
     *
     * Había un máximo de cinco escrito a mano, sin motivo ni mensaje que lo explicara.
     */
    @Test
    fun se_pueden_poner_los_tramos_que_haga_falta() {
        val v = vista()
        v.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 160f)
        repeat(6) { v.cambiarTramosParaPruebas(1) }

        val medida = v.esquinaPrincipalEnCm()!!
        assertEquals("se quedó en el tope de antes", 8, medida.lados.size)
        assertEquals("falta el ángulo de alguna esquina", 7, medida.angulos.size)
        assertTrue("alguna esquina salió curva sola", !medida.hayCurva)
        assertEquals("con tantos lados ya es una serie", "ns", medida.geometria)

        // Y se pueden quitar, hasta los dos que hacen falta para doblar. Al llegar al mínimo sale
        // el aviso por Toast, que pide el hilo de la interfaz.
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            repeat(10) { v.cambiarTramosParaPruebas(-1) }
        }
        assertEquals("se quedó sin esquina", 2, v.esquinaPrincipalEnCm()!!.lados.size)
    }

    /**
     * La ventana curva: una fila de arcos, cada uno con su desarrollo y su cuerda.
     *
     * No es la de esquina con una curva en el rincón: aquí no hay tramos rectos. Se parte en
     * divisiones porque una ventana curva de obra casi nunca es el arco de un círculo perfecto.
     */
    @Test
    fun la_ventana_curva_es_una_fila_de_arcos() {
        val v = vista()
        v.insertarPlantillaVentanaCurva(tramosCm = listOf(90f, 90f), altoCm = 160f, flechaCm = 12f)

        val medida = v.esquinaPrincipalEnCm()
        assertNotNull("la ventana curva no llegó a salir", medida)
        assertEquals("no son dos paredes", 2, medida!!.lados.size)
        assertTrue("el primer tramo no es curvo", medida.lados[0].esCurva)
        assertTrue("el segundo tramo no es curvo", medida.lados[1].esCurva)
        assertEquals("el tramo no mide su desarrollo", 90f, medida.lados[0].ancho, 1f)
        assertEquals("la panza no es la que se pidió", 12f, medida.lados[0].flecha, 0.5f)
        // Entre dos arcos no hay esquina en punta: se encuentran sin doblar.
        assertEquals("falta la arista entre los dos arcos", 1, medida.angulos.size)
        assertEquals("dos arcos no doblan en punta", 180f, medida.gradosDe(0)!!, 0.5f)
    }

    /** Y en la planta cada arco gira lo suyo: la ventana sale curvada, no recta. */
    @Test
    fun la_planta_de_una_ventana_curva_gira_en_cada_arco() {
        val v = vista()
        v.insertarPlantillaVentanaCurva(tramosCm = listOf(90f, 90f), altoCm = 160f, flechaCm = 12f)
        val recorrido = v.recorridoPlantaParaPruebas()
        assertEquals("la planta no tiene los dos arcos", 3, recorrido.size)

        // Cada arco ocupa su cuerda, que es más corta que su desarrollo.
        val primera = kotlin.math.hypot(
            recorrido[1].first - recorrido[0].first, recorrido[1].second - recorrido[0].second
        )
        assertTrue("el arco ocupa su desarrollo en vez de su cuerda: $primera", primera < 89f)
        assertTrue("el arco se quedó en nada: $primera", primera > 80f)

        // Y el segundo no sigue de largo: gira respecto al primero.
        val rumbo1 = kotlin.math.atan2(
            recorrido[1].second - recorrido[0].second, recorrido[1].first - recorrido[0].first
        )
        val rumbo2 = kotlin.math.atan2(
            recorrido[2].second - recorrido[1].second, recorrido[2].first - recorrido[1].first
        )
        assertTrue(
            "los dos arcos salieron en línea recta",
            kotlin.math.abs(Math.toDegrees((rumbo2 - rumbo1).toDouble())) > 10.0
        )
    }

    /** Y un retrato de la ventana curva, con su planta, para poder mirarla. */
    @Test
    fun retrato_de_la_ventana_curva() {
        val v = vista()
        v.insertarPlantillaVentanaCurva(tramosCm = listOf(90f, 90f, 90f), altoCm = 160f, flechaCm = 12f)
        val bmp = v.exportBitmap()
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val f = java.io.File(ctx.getExternalFilesDir(null), "ventana_curva.png")
        f.outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue("no se guardó el retrato", f.exists() && f.length() > 0)
    }

    /**
     * Una ventana curva puede ser un solo arco: no hay por qué partirla.
     *
     * La de esquina necesita dos paredes para doblar; una curva no necesita ninguna otra para ser
     * una ventana. Y como no dobla en ningún sitio, en la calculadora abre en curvo, no en L.
     */
    @Test
    fun una_ventana_curva_puede_ser_un_solo_arco() {
        val v = vista()
        v.insertarPlantillaVentanaCurva(tramosCm = listOf(180f), altoCm = 160f, flechaCm = 20f)

        val medida = v.esquinaPrincipalEnCm()
        assertNotNull("un arco solo no llegó a salir", medida)
        assertEquals("no es un solo tramo", 1, medida!!.lados.size)
        assertTrue("el tramo no es curvo", medida.lados[0].esCurva)
        assertEquals("no mide su desarrollo", 180f, medida.lados[0].ancho, 1f)
        assertEquals("una ventana curva no es una L", "ncu", medida.geometria)
        assertTrue("un arco solo no tiene esquinas", medida.angulos.isEmpty())
    }

    /** Y varios arcos siguen siendo una ventana curva, no una C. */
    @Test
    fun varios_arcos_siguen_siendo_una_ventana_curva() {
        val v = vista()
        v.insertarPlantillaVentanaCurva(tramosCm = listOf(90f, 90f, 90f), altoCm = 160f, flechaCm = 12f)
        val medida = v.esquinaPrincipalEnCm()!!
        assertEquals(3, medida.lados.size)
        assertTrue("algún tramo salió recto", medida.lados.all { it.esCurva })
        assertEquals("tres arcos no son una C", "ncu", medida.geometria)
    }

    /** Se le puede quitar un tramo hasta dejarla en uno; a la de esquina no, que necesita dos. */
    @Test
    fun la_curva_baja_hasta_un_tramo_y_la_esquina_se_planta_en_dos() {
        val curva = vista()
        curva.insertarPlantillaVentanaCurva(tramosCm = listOf(90f, 90f), altoCm = 160f)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            repeat(4) { curva.cambiarTramosParaPruebas(-1) }
        }
        assertEquals("la curva no bajó a un solo arco", 1, curva.esquinaPrincipalEnCm()!!.lados.size)

        val esquina = vista()
        esquina.insertarPlantillaVentanaEsquina(tramosCm = listOf(150f, 120f), altoCm = 160f)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            repeat(4) { esquina.cambiarTramosParaPruebas(-1) }
        }
        assertEquals("la esquina se quedó sin doblar", 2, esquina.esquinaPrincipalEnCm()!!.lados.size)
    }

    /**
     * Partir la curva en más trozos le da SU arco a cada uno.
     *
     * Cada trozo va de un punto de medida de alto al siguiente —el primero, desde el canto de la
     * ventana hasta el primer punto de dentro—, y lleva su desarrollo y su flecha. Eso es lo que
     * permite apuntar una curva de obra que no es el arco de un círculo: cada pedazo va a lo suyo.
     *
     * El trozo nuevo nace siguiendo el radio del anterior —la curva no se corta ahí—, y se corrige
     * después con lo medido en la pared.
     */
    @Test
    fun partir_la_curva_le_da_su_arco_a_cada_trozo() {
        val v = vista()
        v.insertarPlantillaVentanaCurva(tramosCm = listOf(180f), altoCm = 160f, flechaCm = 20f)
        assertEquals("no empezó con un solo arco", 1, v.esquinaPrincipalEnCm()!!.lados.size)

        InstrumentationRegistry.getInstrumentation().runOnMainSync { v.cambiarTramosParaPruebas(1) }

        val medida = v.esquinaPrincipalEnCm()!!
        assertEquals("no partió en dos trozos", 2, medida.lados.size)
        assertTrue("el trozo de siempre se quedó sin curva", medida.lados[0].esCurva)
        assertTrue("el trozo nuevo salió recto", medida.lados[1].esCurva)

        // Entre dos arcos no hay punta: se encuentran sin doblar.
        assertEquals("los dos arcos doblan en punta", 180f, medida.gradosDe(0)!!, 0.5f)

        // Y el trozo nuevo sigue la curva del de al lado: mide lo mismo que él, así que le toca
        // la misma panza. Si naciera recto o con una panza cualquiera, esto no cuadraría.
        assertEquals("el trozo nuevo no mide como el anterior", medida.lados[0].ancho, medida.lados[1].ancho, 1f)
        assertEquals("el trozo nuevo no siguió la curva", medida.lados[0].flecha, medida.lados[1].flecha, 1f)
        assertTrue("el trozo nuevo salió sin panza", medida.lados[1].flecha > 1f)
    }

    /** Y quitar un trozo se lleva su arco: no se queda un rótulo suelto contando una curva que no está. */
    @Test
    fun quitar_un_trozo_se_lleva_su_arco() {
        val v = vista()
        v.insertarPlantillaVentanaCurva(tramosCm = listOf(90f, 90f), altoCm = 160f, flechaCm = 12f)
        assertEquals(2, v.esquinaPrincipalEnCm()!!.lados.size)

        InstrumentationRegistry.getInstrumentation().runOnMainSync { v.cambiarTramosParaPruebas(-1) }

        val medida = v.esquinaPrincipalEnCm()!!
        assertEquals("no se quedó con un solo trozo", 1, medida.lados.size)
        assertTrue("el trozo que queda perdió su curva", medida.lados[0].esCurva)
        assertEquals("el trozo que queda cambió de panza", 12f, medida.lados[0].flecha, 0.5f)
        assertEquals("el trozo que queda cambió de desarrollo", 90f, medida.lados[0].ancho, 1f)
    }

    /**
     * El desarrollo del arco sigue al ancho de la ventana.
     *
     * Se escribe 210 en la cota de arriba y el rótulo de la curva se quedaba en 180: la ventana
     * medía una cosa y su curva decía otra. Lo que se conserva es la curvatura, que es lo que se
     * midió contra la pared.
     */
    @Test
    fun el_desarrollo_sigue_al_ancho_de_la_ventana() {
        val v = vista()
        v.insertarPlantillaVentanaCurva(tramosCm = listOf(180f), altoCm = 160f, flechaCm = 20f)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            v.curvaDeTramoParaPruebas(0, desarrolloCm = 180f, cuerdaCm = 160f)
        }
        val radio = crystal.crystal.taller.ArcoEsquina.deDesarrolloYCuerda(180f, 160f)!!.radio

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            v.escribirAnchoDeLaVentanaParaPruebas(210f)
        }

        val lado = v.esquinaPrincipalEnCm()!!.lados[0]
        assertEquals("el arco no siguió al ancho de la ventana", 210f, lado.ancho, 1f)
        assertTrue("el trozo se quedó sin curva", lado.esCurva)
        val arco = crystal.crystal.taller.ArcoEsquina.deDesarrolloYFlecha(lado.ancho, lado.flecha)!!
        assertEquals("la curva cambió de curvatura", radio, arco.radio, radio * 0.05f)
    }

    /**
     * Un trozo más DENTRO de la ventana: se reparte lo que ya mide, no crece.
     *
     * Es la mitad de la pregunta que se hace al añadir un trozo a una curva. La de 180 con un punto
     * de alto al medio son dos trozos de 90, y la cuerda de cada uno sale del círculo: 87.4, que no
     * es la mitad de 160.
     */
    @Test
    fun un_trozo_dentro_reparte_lo_que_ya_mide() {
        val v = vista()
        v.insertarPlantillaVentanaCurva(tramosCm = listOf(180f), altoCm = 160f, flechaCm = 20f)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            v.curvaDeTramoParaPruebas(0, desarrolloCm = 180f, cuerdaCm = 160f)
            v.cambiarTramosParaPruebas(1, dentro = true)
        }
        val medida = v.esquinaPrincipalEnCm()!!
        assertEquals("no partió en dos trozos", 2, medida.lados.size)
        assertEquals("el primer trozo no mide la mitad", 90f, medida.lados[0].ancho, 0.5f)
        assertEquals("el segundo trozo no mide la mitad", 90f, medida.lados[1].ancho, 0.5f)
        assertEquals(
            "la ventana creció al partirla",
            180f, medida.lados.sumOf { it.ancho.toDouble() }.toFloat(), 1f
        )
        val arco = crystal.crystal.taller.ArcoEsquina.deDesarrolloYFlecha(
            medida.lados[0].ancho, medida.lados[0].flecha
        )!!
        assertEquals("la cuerda del trozo no es la del círculo", 87.4f, arco.cuerda, 1f)
    }

    /** Y la otra mitad: AÑADIDO, se pega al último y la ventana crece. */
    @Test
    fun un_trozo_anadido_pega_otra_pared() {
        val v = vista()
        v.insertarPlantillaVentanaCurva(tramosCm = listOf(180f), altoCm = 160f, flechaCm = 20f)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            v.curvaDeTramoParaPruebas(0, desarrolloCm = 180f, cuerdaCm = 160f)
            v.cambiarTramosParaPruebas(1, dentro = false)
        }
        val medida = v.esquinaPrincipalEnCm()!!
        assertEquals("no añadió el trozo", 2, medida.lados.size)
        assertEquals("le cambió la medida al trozo de siempre", 180f, medida.lados[0].ancho, 1f)
        assertEquals("el trozo nuevo no mide como el anterior", 180f, medida.lados[1].ancho, 1f)
    }
}
