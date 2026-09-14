package crystal.crystal.Diseno.nova

import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
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

    /**
     * La ventana curva tal como la escribe la calculadora.
     *
     * OJO al formato: la panza de toda la ventana va DENTRO de la franja de sistema del primer
     * tramo (`s<120>(fU<20>)`), no suelta al final. Escrita suelta, la prueba pasaba y la ventana
     * seguía saliendo plana en el celular.
     */
    @Test
    fun retrato_de_la_ventana_curva_de_la_calculadora() {
        val paquete = "{nova,ina,[180,160:Tl<60>(H<160>;m<40>(f);s<120>(fU<20>))" +
            " P<2.5> Tl<60>(H<160>;m<40>(f);s<120>(f))" +
            " P<2.5> Tl<60>(H<160>;m<40>(f);s<120>(f))]}"
        assertEquals(
            "no encuentra la panza donde la escribe la calculadora",
            20f, PlantaDelDiseno.panzaDelPaquete(paquete), 0.01f
        )
        retrato("vol_curva_entera.png", paquete)

        // Y lo que de verdad importa, preguntándoselo A LA VISTA y no a la geometría suelta: que
        // el volumen que arma salga curvo. Comprobándolo por fuera, la prueba pasaba en verde
        // mientras en el celular la ventana seguía saliendo plana.
        val v = vista()
        v.mostrar(paquete)
        assertTrue("la ventana curva se armó plana", v.carasCurvasParaPruebas() > 0)
    }

    /**
     * Una ventana plana partida por un parante: 364.6 en dos tramos de tres hojas cada uno.
     *
     * Es la que se veía vacía en el celular —ni el parante ni una raya—, así que aquí se retrata
     * para mirarla.
     */
    @Test
    fun retrato_de_la_plana_con_parante() {
        retrato(
            "vol_parante.png",
            "{nova,ina,[364.6,160:Tl<181.05>(H<160>;m<40>(f);s<120>(fcc))" +
                " P<2.5> Tl<181.05>(H<160>;m<40>(f);s<120>(fcc))]}"
        )
    }

    /**
     * La curva de 364.6 en seis divisiones con su parante al centro, como la escribe Nova.
     *
     * OJO al formato: en las geometrías compuestas —en L, en C, curva— el parante NO va entre
     * tramos sino DENTRO de la franja, como `;P;`, porque el `P<2.5>` de siempre se mezclaría con
     * los `A<90>` que parten los lados. Escrito así, el 3D salía sin parante: ni una raya.
     */
    @Test
    fun retrato_de_la_curva_con_parante_dentro() {
        val paquete = "{nova,ina,[364.6,160:Tl<364.6>(H<160>;m<40>(f<181.05>f<181.05>);" +
            "s<120>(f<60.35>c<60.35>f<60.35>;P;f<60.35>c<60.35>f<60.35>U<25>))]}"

        // Primero, que el modelo NO se coma el parante al leer.
        val d = DisenoNova.desdePaquete(paquete)!!
        val sistema = d.tramos[0].franjas.first { it.esSistema }
        assertEquals("se comió el parante de dentro de la franja", listOf(2), sistema.parantes)
        assertEquals("perdió o inventó módulos", 6, sistema.modulos.size)

        // Y que de ida y vuelta siga estando donde estaba.
        val otra = DisenoNova.desdePaquete(d.aPaquete())!!
        assertEquals(
            "el parante no sobrevivió a escribir y volver a leer",
            listOf(2), otra.tramos[0].franjas.first { it.esSistema }.parantes
        )

        retrato("vol_curva_parante.png", paquete)
    }
}
