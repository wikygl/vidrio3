package crystal.crystal.taller.drywall

import android.content.Intent
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import crystal.crystal.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** La calculadora de drywall: un tabique de 3 x 2.4 calculado, y el cielo raso dibujado. Deja capturas. */
@RunWith(AndroidJUnit4::class)
class DrywallActivityTest {

    private fun intent() = Intent(ApplicationProvider.getApplicationContext(), DrywallActivity::class.java)

    private fun captura(a: DrywallActivity, nombre: String) {
        val raiz = a.window.decorView
        if (raiz.width == 0 || raiz.height == 0) return
        val bmp = android.graphics.Bitmap.createBitmap(raiz.width, raiz.height, android.graphics.Bitmap.Config.ARGB_8888)
        raiz.draw(android.graphics.Canvas(bmp))
        a.getExternalFilesDir(null)?.let { dir ->
            java.io.FileOutputStream(java.io.File(dir, nombre)).use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, it) }
        }
    }

    @Test
    fun el_tabique_se_calcula_y_el_cielo_raso_se_dibuja() {
        ActivityScenario.launch<DrywallActivity>(intent()).use { esc ->
            Thread.sleep(400)
            esc.onActivity { a ->
                a.findViewById<Spinner>(R.id.spTipo).setSelection(0)
                a.findViewById<EditText>(R.id.etAncho).setText("3")
                a.findViewById<EditText>(R.id.etAlto).setText("2.4")
                a.findViewById<EditText>(R.id.etVanos).setText("0")
                a.findViewById<EditText>(R.id.etNumVanos).setText("0")
                a.findViewById<EditText>(R.id.etEsquinas).setText("0")
                a.findViewById<android.widget.CheckBox>(R.id.cbLana).isChecked = false
                a.findViewById<Spinner>(R.id.spSeparacion).setSelection(0)
                a.findViewById<Spinner>(R.id.spPlancha).setSelection(0)
                a.findViewById<Spinner>(R.id.spPerfil).setSelection(0)
            }
            Thread.sleep(300)
            esc.onActivity { a ->
                a.findViewById<android.view.View>(R.id.btCalcular).performClick()
                assertEquals("6 und", a.findViewById<TextView>(R.id.txPlanchas).text.toString())
                val perfiles = a.findViewById<TextView>(R.id.txPerfiles).text.toString()
                assertTrue(perfiles, perfiles.contains("Parante 89 x 3.05 m: 8 und"))
                assertTrue(perfiles, perfiles.contains("Riel 89 x 3.05 m: 2 und"))
                assertTrue(a.findViewById<TextView>(R.id.txFijaciones).text.toString().contains("Tornillo drywall 6 x 1\": 180 und"))
                assertTrue(a.findViewById<TextView>(R.id.txReferencias).text.toString().startsWith("Tabique"))
            }
            Thread.sleep(400)   // que la pantalla se acomode al texto antes de la captura
            esc.onActivity { a -> captura(a, "drywall_tabique.png"); a.findViewById<android.view.View>(R.id.btVerPlanchas).performClick() }
            Thread.sleep(300)
            esc.onActivity { a -> captura(a, "drywall_planchas.png"); a.findViewById<Spinner>(R.id.spTipo).setSelection(2); a.findViewById<android.view.View>(R.id.btVerEstructura).performClick() }
            Thread.sleep(400)
            esc.onActivity { a ->
                a.findViewById<android.view.View>(R.id.btCalcular).performClick()
                assertTrue(a.findViewById<TextView>(R.id.txPerfiles).text.toString().contains("Ángulo perimetral"))
            }
            Thread.sleep(400)
            esc.onActivity { a -> captura(a, "drywall_cielo.png") }
        }
    }
}
