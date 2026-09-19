package crystal.crystal.taller.melamina

import android.content.Intent
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import crystal.crystal.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * La pantalla del ropero: abre con el último ropero (o el de fábrica), calcula al arrancar, y al
 * cambiar el hueco y los cuerpos vuelve a repartir y a calcular.
 */
@RunWith(AndroidJUnit4::class)
class RoperoActivityTest {

    private fun intent() = Intent(ApplicationProvider.getApplicationContext(), RoperoActivity::class.java)

    @Test
    fun arranca_calculando_y_arma_las_listas() {
        ActivityScenario.launch<RoperoActivity>(intent()).use { esc ->
            esc.onActivity { a ->
                val referencias = a.findViewById<TextView>(R.id.txReferencias).text.toString()
                assertTrue("sin referencias: $referencias", referencias.contains("Ropero empotrado"))
                val corte = a.findViewById<TextView>(R.id.txCorte).text.toString()
                assertTrue("sin lista de corte", corte.contains("Lateral"))
                val materiales = a.findViewById<android.widget.LinearLayout>(R.id.lyMateriales)
                val titulos = (0 until materiales.childCount step 2).map { (materiales.getChildAt(it) as TextView).text.toString() }
                assertTrue("faltan listas: $titulos", titulos.any { it.startsWith("Melamina") } && titulos.any { it.startsWith("Tapacanto") } && titulos.contains("Accesorios melamina"))
            }
        }
    }

    @Test
    fun cambiar_el_hueco_y_los_cuerpos_recalcula() {
        ActivityScenario.launch<RoperoActivity>(intent()).use { esc ->
            esc.onActivity { a ->
                a.findViewById<android.widget.EditText>(R.id.etAncho).setText("300")
                a.findViewById<android.widget.EditText>(R.id.etCuerpos).setText("3")
                a.findViewById<android.widget.Button>(R.id.btCalcular).performClick()
                val referencias = a.findViewById<TextView>(R.id.txReferencias).text.toString()
                assertTrue("no tomó el ancho: $referencias", referencias.contains("Ropero empotrado 300 x"))
                val vista = a.findViewById<VistaRopero>(R.id.vistaRopero)
                assertEquals(3, vista.ropero.cuerpos.size)
                // 300 - 3.6 - 2 * 1.8 = 292.8 entre tres: 97.6 cada uno.
                assertEquals(97.6f, vista.ropero.cuerpos[0].anchoCm, 0.05f)
                // Las puertas se ven y se quitan con el botón.
                a.findViewById<android.widget.Button>(R.id.btPuertas).performClick()
                assertTrue(vista.mostrarPuertas)
            }
        }
    }

    /** Deja una captura de la pantalla entera en la carpeta de archivos de la app, para mirarla desde la PC. */
    @Test
    fun guarda_captura_de_la_pantalla() {
        ActivityScenario.launch<RoperoActivity>(intent()).use { esc ->
            Thread.sleep(800)
            esc.onActivity { a ->
                val raiz = a.window.decorView
                if (raiz.width == 0 || raiz.height == 0) return@onActivity
                val bmp = android.graphics.Bitmap.createBitmap(raiz.width, raiz.height, android.graphics.Bitmap.Config.ARGB_8888)
                raiz.draw(android.graphics.Canvas(bmp))
                val dir = a.getExternalFilesDir(null) ?: return@onActivity
                java.io.FileOutputStream(java.io.File(dir, "ropero_pantalla.png")).use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, it) }
            }
        }
    }
}
