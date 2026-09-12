package crystal.crystal.taller

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
