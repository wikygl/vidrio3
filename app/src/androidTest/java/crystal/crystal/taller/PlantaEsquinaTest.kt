package crystal.crystal.taller

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
}
