package crystal.crystal.taller

import android.content.Intent
import android.widget.EditText
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
 * La calculadora de rejas: con columnas y filas en 0 se reparte solo cada 15 cm (hacia arriba),
 * marco y tubo de 3.8 de fábrica, y el dibujo va en modo estructura. Deja una captura.
 */
@RunWith(AndroidJUnit4::class)
class RejasActivityTest {

    private fun intent() = Intent(ApplicationProvider.getApplicationContext(), RejasActivity::class.java)
        .putExtra("ancho", 100f).putExtra("alto", 160f)

    /** Los modelos con cuenta propia: rombos y espina se pintan en VistaReja y salen con cortes a 45°. */
    @Test
    fun rombos_y_espina_se_pintan_y_se_calculan_con_la_cuenta_propia() {
        ActivityScenario.launch<RejasActivity>(intent()).use { esc ->
            Thread.sleep(500)
            listOf(3 to "rejas_rombos.png", 4 to "rejas_espina.png", 1 to "rejas_barrotes.png", 5 to "rejas_trabado.png").forEach { (pos, nombre) ->
                esc.onActivity { a -> a.findViewById<android.widget.Spinner>(R.id.spModelo).setSelection(pos) }
                Thread.sleep(700)
                esc.onActivity { a ->
                    assertEquals(android.view.View.VISIBLE, a.findViewById<android.view.View>(R.id.vistaReja).visibility)
                    a.findViewById<android.view.View>(R.id.btnCalcular).performClick()
                    val tubo = a.findViewById<TextView>(R.id.tvTubo).text.toString()
                    if (pos == 3 || pos == 4) assertTrue(tubo, tubo.contains("(45°)"))
                    assertTrue(a.findViewById<TextView>(R.id.tvMarco).text.toString().lines().contains("160 = 2"))
                    val raiz = a.window.decorView
                    if (raiz.width > 0 && raiz.height > 0) {
                        val bmp = android.graphics.Bitmap.createBitmap(raiz.width, raiz.height, android.graphics.Bitmap.Config.ARGB_8888)
                        raiz.draw(android.graphics.Canvas(bmp))
                        a.getExternalFilesDir(null)?.let { dir ->
                            java.io.FileOutputStream(java.io.File(dir, nombre)).use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, it) }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun se_reparte_cada_15_y_calcula_marco_y_tubos() {
        ActivityScenario.launch<RejasActivity>(intent()).use { esc ->
            Thread.sleep(700)   // la actualización automática espera 300 ms tras escribir
            esc.onActivity { a ->
                assertEquals("0", a.findViewById<EditText>(R.id.nCol).text.toString())
                assertEquals("3.8", a.findViewById<EditText>(R.id.etTubo).text.toString())
                assertEquals("3.8", a.findViewById<EditText>(R.id.etMarco).text.toString())
                val vista = a.findViewById<GridDrawingView>(R.id.rectanguloView)
                // 100 / 15 = 6.67 → 7 columnas; 160 / 15 = 10.67 → 11 filas.
                assertEquals(7, vista.getAnchosColumnas().size)
                assertEquals(11, vista.getAlturasFilasPorColumna()[0].size)
                assertEquals(GridDrawingView.ModoVisual.ESTRUCTURA, vista.getModoVisual())
                a.findViewById<android.view.View>(R.id.btnCalcular).performClick()
                val marco = a.findViewById<TextView>(R.id.tvMarco).text.toString()
                val tubo = a.findViewById<TextView>(R.id.tvTubo).text.toString()
                // Marco: dos de 160 y dos de 100 - 2 x 3.8 = 92.4. Tubos: 6 verticales de 160 - 7.6 = 152.4.
                assertTrue(marco, marco.lines().contains("160 = 2"))
                assertTrue(marco, marco.lines().contains("92.4 = 2"))
                assertTrue(tubo, tubo.lines().contains("152.4 = 6"))
                val raiz = a.window.decorView
                if (raiz.width > 0 && raiz.height > 0) {
                    val bmp = android.graphics.Bitmap.createBitmap(raiz.width, raiz.height, android.graphics.Bitmap.Config.ARGB_8888)
                    raiz.draw(android.graphics.Canvas(bmp))
                    a.getExternalFilesDir(null)?.let { dir ->
                        java.io.FileOutputStream(java.io.File(dir, "rejas.png")).use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, it) }
                    }
                }
            }
        }
    }
}
