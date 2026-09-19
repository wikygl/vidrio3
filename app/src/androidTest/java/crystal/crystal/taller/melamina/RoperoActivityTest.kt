package crystal.crystal.taller.melamina

import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import crystal.crystal.R
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * La pantalla del ropero, con el flujo de todas las calculadoras: se abre con el último ropero
 * dibujado, Calcular arma las filas de material, y al cambiar el hueco y los cuerpos se reparte
 * y se calcula de nuevo.
 */
@RunWith(AndroidJUnit4::class)
class RoperoActivityTest {

    private val ctx: Context get() = ApplicationProvider.getApplicationContext()

    private fun intent() = Intent(ctx, RoperoActivity::class.java)

    /** Calcular pide un proyecto activo: se deja uno de pruebas puesto. */
    @Before
    fun conProyecto() {
        ProyectoManager.inicializarDesdeStorage(ctx)
        if (!ProyectoManager.hayProyectoActivo()) {
            val nombre = "Pruebas ropero"
            if (!MapStorage.existeProyecto(ctx, nombre)) MapStorage.crearProyecto(ctx, nombre, "")
            ProyectoManager.setProyectoActivo(ctx, nombre)
        }
    }

    @Test
    fun calcular_arma_las_filas_de_material() {
        ActivityScenario.launch<RoperoActivity>(intent()).use { esc ->
            esc.onActivity { a ->
                // La pantalla abre con el último ropero que se armó: se piden batientes a propósito.
                a.findViewById<android.widget.Spinner>(R.id.spPuertas).setSelection(TipoPuertas.values().indexOf(TipoPuertas.BATIENTES))
                a.findViewById<Button>(R.id.btCalcular).performClick()
                val referencias = a.findViewById<TextView>(R.id.txReferencias).text.toString()
                assertTrue("sin referencias: $referencias", referencias.contains("Ropero empotrado"))
                assertTrue("sin piezas de melamina", a.findViewById<TextView>(R.id.txMelamina).text.contains(" = "))
                assertTrue("sin tapacanto", a.findViewById<TextView>(R.id.txTapacanto).text.contains(" = "))
                assertTrue("sin accesorios", a.findViewById<TextView>(R.id.txAccesorios).text.contains("Tornillo"))
                assertTrue("sin lista de corte", a.findViewById<TextView>(R.id.txCorte).text.contains("Lateral"))
                // Con puertas batientes no hay riel corredizo: su fila va escondida.
                assertEquals(android.view.View.GONE, a.findViewById<android.view.View>(R.id.lyRiel).visibility)
            }
        }
    }

    @Test
    fun cambiar_el_hueco_y_los_cuerpos_recalcula() {
        ActivityScenario.launch<RoperoActivity>(intent()).use { esc ->
            esc.onActivity { a ->
                a.findViewById<EditText>(R.id.etAncho).setText("300")
                a.findViewById<EditText>(R.id.etAlto).setText("240")
                a.findViewById<EditText>(R.id.etFondo).setText("60")
                a.findViewById<EditText>(R.id.etCuerpos).setText("3")
                a.findViewById<Button>(R.id.btCalcular).performClick()
                val referencias = a.findViewById<TextView>(R.id.txReferencias).text.toString()
                assertTrue("no tomó el ancho: $referencias", referencias.contains("Ropero empotrado 300 x"))
                val vista = a.findViewById<VistaRopero>(R.id.vistaRopero)
                assertEquals(3, vista.ropero.cuerpos.size)
                // 300 - 3.6 - 2 * 1.8 = 292.8 entre tres: 97.6 cada uno.
                assertEquals(97.6f, vista.ropero.cuerpos[0].anchoCm, 0.05f)
                // El botón de la ficha va pasando: interior → puertas → 3D.
                a.findViewById<Button>(R.id.btVista).performClick()
                assertTrue(vista.mostrarPuertas)
                a.findViewById<Button>(R.id.btVista).performClick()
                assertTrue(vista.en3d && !vista.mostrarPuertas)
            }
        }
    }

    /** Deja una captura de la pantalla entera en la carpeta de archivos de la app, para mirarla desde la PC. */
    @Test
    fun guarda_captura_de_la_pantalla() {
        ActivityScenario.launch<RoperoActivity>(intent()).use { esc ->
            esc.onActivity { a -> a.findViewById<Button>(R.id.btCalcular).performClick() }
            Thread.sleep(800)
            esc.onActivity { a ->
                val raiz = a.window.decorView
                if (raiz.width == 0 || raiz.height == 0) return@onActivity
                // Con la pantalla apagada no corre ningún cuadro: se mide y se acomoda a mano para
                // que la captura lleve el texto recién puesto y no el de antes.
                raiz.measure(
                    android.view.View.MeasureSpec.makeMeasureSpec(raiz.width, android.view.View.MeasureSpec.EXACTLY),
                    android.view.View.MeasureSpec.makeMeasureSpec(raiz.height, android.view.View.MeasureSpec.EXACTLY)
                )
                raiz.layout(0, 0, raiz.width, raiz.height)
                val bmp = android.graphics.Bitmap.createBitmap(raiz.width, raiz.height, android.graphics.Bitmap.Config.ARGB_8888)
                raiz.draw(android.graphics.Canvas(bmp))
                val dir = a.getExternalFilesDir(null) ?: return@onActivity
                java.io.FileOutputStream(java.io.File(dir, "ropero_pantalla.png")).use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, it) }
            }
        }
    }
}
