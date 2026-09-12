package crystal.crystal.taller

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
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
}
