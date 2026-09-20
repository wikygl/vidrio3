package crystal.crystal.taller.melamina

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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
 * La pantalla de diseño del ropero: tocar un cajón abre su mando, escribir su alto lo cambia
 * (y empuja a los de arriba), y "enviar a calculadora" devuelve el ropero afinado.
 */
@RunWith(AndroidJUnit4::class)
class DisenoRoperoActivityTest {

    private val ropero = Ropero(anchoCm = 240f, altoCm = 240f, fondoCm = 60f)
        .conCuerposIguales(2)
        .conCuerpo(0, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 3))

    private fun intent() = Intent(ApplicationProvider.getApplicationContext(), DisenoRoperoActivity::class.java)
        .putExtra(DisenoRoperoActivity.EXTRA_ROPERO, ropero.aJson())

    /** Los EditText del mando, en orden. */
    private fun campos(mando: LinearLayout): List<EditText> {
        val salen = mutableListOf<EditText>()
        fun recorrer(v: android.view.View) {
            if (v is EditText) salen.add(v)
            if (v is android.view.ViewGroup) for (i in 0 until v.childCount) recorrer(v.getChildAt(i))
        }
        recorrer(mando)
        return salen
    }

    private fun botones(mando: LinearLayout): List<Button> {
        val salen = mutableListOf<Button>()
        fun recorrer(v: android.view.View) {
            if (v is Button) salen.add(v)
            if (v is android.view.ViewGroup) for (i in 0 until v.childCount) recorrer(v.getChildAt(i))
        }
        recorrer(mando)
        return salen
    }

    @Test
    fun tocar_un_cajon_y_escribirle_el_alto_lo_cambia_y_vuelve_a_la_calculadora() {
        ActivityScenario.launch<DisenoRoperoActivity>(intent()).use { esc ->
            esc.onActivity { a ->
                val vista = a.findViewById<VistaRopero>(R.id.vistaDiseno)
                // El cajón de abajo del primer cuerpo.
                val cajon = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.CAJON && it.indice == 0 }
                vista.alTocarElemento?.invoke(cajon)
                val info = a.findViewById<TextView>(R.id.tvInfoSeleccion).text.toString()
                assertTrue("no describe el cajón: $info", info.startsWith("Cajón 1"))
                val mando = a.findViewById<LinearLayout>(R.id.contenedorFlotante)
                assertEquals(android.view.View.VISIBLE, mando.visibility)
                campos(mando).first().setText("35")
                botones(mando).first { it.text == "Este" }.performClick()
                val cajones = RoperoGeometria.elementos(vista.ropero).filter { it.tipo == TipoElemento.CAJON }
                assertEquals(35f, cajones[0].y1 - cajones[0].y0, 0.01f)
                assertEquals(20f, cajones[1].y1 - cajones[1].y0, 0.01f)
                assertEquals(cajones[0].y1, cajones[1].y0, 0.01f)
                // Lo que vuelve a la calculadora es el ropero afinado (con la pantalla bloqueada no
                // se puede lanzar "for result", así que se mira el paquete que se mandaría).
                val devuelto = Ropero.desdeJson(a.paqueteParaPruebas())!!
                assertEquals(listOf(35f, 20f, 20f), devuelto.cuerpos[0].altosDeCajones(20f))
            }
        }
    }

    @Test
    fun arrastrar_una_repisa_la_sube_y_las_opciones_cambian_el_tapacanto() {
        ActivityScenario.launch<DisenoRoperoActivity>(intent()).use { esc ->
            esc.onActivity { a ->
                val vista = a.findViewById<VistaRopero>(R.id.vistaDiseno)
                val repisa = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.ENTREPANO && it.cuerpo == 1 && it.indice == 0 }
                val piso = RoperoGeometria.pisoY(vista.ropero)
                vista.alArrastrar?.invoke(repisa, (repisa.x0 + repisa.x1) / 2f, piso + 33.2f)
                val movida = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.ENTREPANO && it.cuerpo == 1 && it.indice == 0 }
                assertEquals(33f, movida.y0 - piso, 0.01f)   // al medio centímetro
                // Opciones: tapacanto de 2 mm y 4 hojas corredizas.
                a.findViewById<android.view.View>(R.id.btnOpciones).performClick()
                val opciones = a.findViewById<LinearLayout>(R.id.contenedorOpciones)
                val spinners = mutableListOf<android.widget.Spinner>()
                fun recorrer(v: android.view.View) { if (v is android.widget.Spinner) spinners.add(v); if (v is android.view.ViewGroup) for (i in 0 until v.childCount) recorrer(v.getChildAt(i)) }
                recorrer(opciones)
                spinners[0].setSelection(2)   // 2 mm
                spinners[2].setSelection(3)   // 4 hojas
                botones(opciones).first { it.text == "Aplicar opciones" }.performClick()
                assertEquals(2f, vista.ropero.tapacantoGrosorMm, 0.01f)
                assertEquals(4, vista.ropero.hojasCorredizas)
            }
        }
    }

    /** Deja una captura con un cajón tocado, para mirarla desde la PC. */
    @Test
    fun guarda_captura_de_la_pantalla() {
        ActivityScenario.launch<DisenoRoperoActivity>(intent()).use { esc ->
            esc.onActivity { a ->
                val vista = a.findViewById<VistaRopero>(R.id.vistaDiseno)
                val cajon = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.CAJON && it.indice == 1 }
                vista.alTocarElemento?.invoke(cajon)
            }
            Thread.sleep(600)
            esc.onActivity { a ->
                val raiz = a.window.decorView
                if (raiz.width == 0 || raiz.height == 0) return@onActivity
                raiz.measure(
                    android.view.View.MeasureSpec.makeMeasureSpec(raiz.width, android.view.View.MeasureSpec.EXACTLY),
                    android.view.View.MeasureSpec.makeMeasureSpec(raiz.height, android.view.View.MeasureSpec.EXACTLY)
                )
                raiz.layout(0, 0, raiz.width, raiz.height)
                val bmp = android.graphics.Bitmap.createBitmap(raiz.width, raiz.height, android.graphics.Bitmap.Config.ARGB_8888)
                raiz.draw(android.graphics.Canvas(bmp))
                val dir = a.getExternalFilesDir(null) ?: return@onActivity
                java.io.FileOutputStream(java.io.File(dir, "diseno_ropero.png")).use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, it) }
            }
        }
    }
}
