package crystal.crystal.taller.melamina

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * El ropero dibujado: se pinta en un bitmap y se mira que salga el mueble (tablero, interior,
 * puertas) y que tocar el lienzo dé con el cuerpo que toca.
 */
@RunWith(AndroidJUnit4::class)
class VistaRoperoTest {

    private fun vista(ropero: Ropero): VistaRopero {
        val v = VistaRopero(ApplicationProvider.getApplicationContext())
        v.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(800, android.view.View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1000, 800)
        v.ropero = ropero
        return v
    }

    private fun pintar(v: VistaRopero): Bitmap {
        val bmp = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        v.draw(Canvas(bmp))
        return bmp
    }

    private fun cuantosDe(bmp: Bitmap, color: Int): Int {
        var n = 0
        for (y in 0 until bmp.height step 4) for (x in 0 until bmp.width step 4) if (bmp.getPixel(x, y) == color) n++
        return n
    }

    private val ropero = Ropero(anchoCm = 240f, altoCm = 240f, fondoCm = 60f).conCuerposIguales(3)

    @Test
    fun se_dibuja_el_armazon_y_el_interior() {
        val bmp = pintar(vista(ropero))
        val tablero = cuantosDe(bmp, Color.parseColor(RoperoDibujo.COLOR_TABLERO))
        val interior = cuantosDe(bmp, Color.parseColor(RoperoDibujo.COLOR_INTERIOR))
        assertTrue("no se ve el tablero: $tablero", tablero > 200)
        assertTrue("no se ve el interior: $interior", interior > 2000)
    }

    @Test
    fun con_puertas_el_interior_queda_tapado() {
        val v = vista(ropero)
        val sinPuertas = cuantosDe(pintar(v), Color.parseColor(RoperoDibujo.COLOR_INTERIOR))
        v.mostrarPuertas = true
        val bmp = pintar(v)
        val conPuertas = cuantosDe(bmp, Color.parseColor(RoperoDibujo.COLOR_INTERIOR))
        assertTrue("las puertas no taparon nada: $sinPuertas -> $conPuertas", conPuertas < sinPuertas / 3)
        assertTrue("no se ven las puertas", cuantosDe(bmp, Color.parseColor(RoperoDibujo.COLOR_PUERTA)) > 1000)
    }

    @Test
    fun en_3d_aparecen_el_costado_y_el_techo() {
        val v = vista(ropero)
        v.en3d = true
        val bmp = pintar(v)
        assertTrue("no se ve el costado", cuantosDe(bmp, Color.parseColor(RoperoDibujo.COLOR_TABLERO_LADO)) > 100)
        assertTrue("no se ve el techo", cuantosDe(bmp, Color.parseColor(RoperoDibujo.COLOR_TABLERO_TECHO)) > 100)
    }

    @Test
    fun tocar_el_lienzo_da_con_el_cuerpo() {
        val v = vista(ropero)
        pintar(v)
        // El mueble ocupa el ancho del lienzo menos los márgenes: el primer tercio es el cuerpo 0.
        assertEquals(0, v.cuerpoEn(200f))
        assertEquals(1, v.cuerpoEn(500f))
        assertEquals(2, v.cuerpoEn(800f))
        assertNull(v.cuerpoEn(5f))
    }

    /**
     * Deja tres muestras (frente, puertas, 3D) en la carpeta de archivos de la app, para mirarlas
     * desde la PC con adb pull sin tocar la pantalla del teléfono.
     */
    @Test
    fun guarda_muestras_para_mirar() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val dir = ctx.getExternalFilesDir(null) ?: return
        val conCajones = ropero.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 4)).copy(maleteroCm = 40f)
        val v = vista(conCajones)
        fun guardar(nombre: String) {
            java.io.FileOutputStream(java.io.File(dir, nombre)).use { pintar(v).compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
        guardar("ropero_frente.png")
        v.mostrarPuertas = true; guardar("ropero_puertas.png")
        v.mostrarPuertas = false; v.en3d = true; guardar("ropero_3d.png")
        v.ropero = conCajones.copy(puertas = TipoPuertas.CORREDIZAS); v.en3d = false; v.mostrarPuertas = true; guardar("ropero_corredizas.png")
    }
}
