package crystal.crystal.Diseno.nova

import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * La ventana en tres dimensiones, dibujada.
 *
 * La geometría ya se comprueba en frío (PlantaDelDisenoTest, VolumenDelDisenoTest); aquí solo se
 * mira que el dibujo salga y se guardan los retratos para poder verlos. Es una vista de mirar: no
 * se toca nada en ella.
 */
@RunWith(AndroidJUnit4::class)
class VistaVolumenNovaTest {

    private fun vista(): VistaVolumenNova {
        val v = VistaVolumenNova(ApplicationProvider.getApplicationContext())
        v.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(700, android.view.View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1000, 700)
        return v
    }

    private fun retrato(nombre: String, paquete: String, giro: Float? = null) {
        val v = vista()
        assertTrue("no se pudo armar el volumen de $nombre", v.mostrar(paquete))
        giro?.let { v.giroGrados = it }
        val bmp = Bitmap.createBitmap(1000, 700, Bitmap.Config.ARGB_8888)
        bmp.eraseColor(android.graphics.Color.WHITE)
        v.draw(android.graphics.Canvas(bmp))
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val f = File(ctx.getExternalFilesDir(null), nombre)
        f.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue("no se guardó $nombre", f.exists() && f.length() > 0)
    }

    /** La L con la esquina curva: dos paredes y el arco girando entre ellas. */
    @Test
    fun retrato_de_la_l_con_esquina_curva() {
        retrato(
            "vol_l_curva.png",
            "{nova,ina,[150,120:Tl<150>(H<120>;m<30>(f);s<90>(fc))" +
                " Tl<157.1>(H<120>;Q<29.3>;m<30>(f);s<90>(fc))" +
                " A<90> Tl<120>(H<120>;m<30>(f);s<90>(fc))]}"
        )
    }

    /** La C con las dos esquinas curvas, que es la que no había manera de leer en la alzada. */
    @Test
    fun retrato_de_la_c_con_esquinas_curvas() {
        retrato(
            "vol_c_curva.png",
            "{nova,ina,[100,160:Tl<100>(H<160>;m<40>(f);s<120>(fc))" +
                " Tl<60>(H<160>;Q<8>;m<40>(f);s<120>(f)) A<90> Tl<200>(H<160>;m<40>(f);s<120>(fcc))" +
                " Tl<60>(H<160>;Q<8>;m<40>(f);s<120>(f)) A<90> Tl<100>(H<160>;m<40>(f);s<120>(fc))]}"
        )
    }

    /** Y una con ángulos distintos, que es lo que en la alzada salía todo igual. */
    @Test
    fun retrato_de_los_angulos_distintos() {
        retrato(
            "vol_angulos.png",
            "{nova,ina,[120,160:Tl<120>(H<160>;s<120>(fc))" +
                " A<135> Tl<120>(H<160>;s<120>(fc))" +
                " A<90> Tl<120>(H<160>;s<120>(fc))" +
                " A<54> Tl<120>(H<160>;s<120>(fc))]}"
        )
    }

    /** Y la misma, mirada desde otro lado: girarla es lo que salva al isométrico. */
    @Test
    fun retrato_girado() {
        retrato(
            "vol_girado.png",
            "{nova,ina,[100,160:Tl<100>(H<160>;s<120>(fc))" +
                " Tl<60>(H<160>;Q<8>;s<120>(f)) A<90> Tl<200>(H<160>;s<120>(fcc))" +
                " Tl<60>(H<160>;Q<8>;s<120>(f)) A<90> Tl<100>(H<160>;s<120>(fc))]}",
            giro = 60f
        )
    }
}
