package crystal.crystal.taller.melamina

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import crystal.crystal.R
import crystal.crystal.taller.ColaCalculadoras
import crystal.crystal.taller.SketchMedidasView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * El ropero puesto desde la plantilla del apunte: es el producto del dibujo, se edita por sus
 * cotas (el hueco reparte los cuerpos; un cuerpo reparte a los demás), sus rótulos se dibujan
 * para tocarlos, y sobrevive al guardado del apunte.
 */
@RunWith(AndroidJUnit4::class)
class RoperoEnElApunteTest {

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
    fun la_plantilla_deja_un_ropero_que_es_el_producto_del_dibujo() {
        val v = vista()
        v.insertarPlantillaRopero()
        assertEquals("Ropero", v.productoDelDibujo())
        val r = v.roperoDelDibujo()
        assertNotNull(r)
        assertEquals(240f, r!!.anchoCm, 0.01f)
        assertEquals(2, r.cuerpos.size)
        val medida = v.medidaPrincipal()!!
        assertEquals(240f, medida.anchoCm, 0.01f)
        assertEquals(240f, medida.altoCm, 0.01f)
    }

    @Test
    fun las_cotas_escriben_en_el_ropero() {
        val v = vista()
        v.insertarPlantillaRopero()
        assertTrue(v.escribirCotaDeRoperoParaPruebas("ROPERO_ANCHO", 300f))
        assertTrue(v.escribirCotaDeRoperoParaPruebas("ROPERO_CUERPOS", 3f))
        var r = v.roperoDelDibujo()!!
        assertEquals(300f, r.anchoCm, 0.01f)
        assertEquals(3, r.cuerpos.size)
        assertEquals(97.6f, r.cuerpos[0].anchoCm, 0.05f)
        assertTrue(v.escribirCotaDeRoperoParaPruebas("ROPERO_CUERPO", 80f, cuerpo = 0))
        assertTrue(v.escribirCotaDeRoperoParaPruebas("ROPERO_MALETERO", 40f))
        assertTrue(v.escribirCotaDeRoperoParaPruebas("ROPERO_FONDO", 55f))
        r = v.roperoDelDibujo()!!
        assertEquals(80f, r.cuerpos[0].anchoCm, 0.05f)
        assertEquals((292.8f - 80f) / 2f, r.cuerpos[1].anchoCm, 0.05f)
        assertEquals(40f, r.maleteroCm, 0.01f)
        assertEquals(55f, r.fondoCm, 0.01f)
        // El marco sigue midiendo el hueco.
        assertEquals(300f, v.medidaPrincipal()!!.anchoCm, 0.01f)
    }

    @Test
    fun se_dibujan_sus_cotas_y_rotulos_para_tocarlos() {
        val v = vista()
        v.insertarPlantillaRopero()
        v.escribirCotaDeRoperoParaPruebas("ROPERO_CUERPOS", 3f)
        val tipos = v.cotasDibujadasParaPruebas().map { it.first }
        assertEquals(3, tipos.count { it == "ROPERO_CUERPO" })
        assertEquals(3, tipos.count { it == "ROPERO_TIPO" })
        listOf("ROPERO_ANCHO", "ROPERO_ALTO", "ROPERO_ZOCALO", "ROPERO_MALETERO", "ROPERO_FONDO", "ROPERO_CUERPOS", "ROPERO_PUERTAS")
            .forEach { assertTrue("falta la cota $it", it in tipos) }
    }

    @Test
    fun el_rotulo_de_puertas_va_pasando_por_todas() {
        val v = vista()
        v.insertarPlantillaRopero()
        assertEquals(TipoPuertas.BATIENTES, v.roperoDelDibujo()!!.puertas)
        v.ciclarPuertasDeRoperoParaPruebas()   // batientes interior -> batientes a la vista
        assertTrue(v.roperoDelDibujo()!!.verPuertas)
        v.ciclarPuertasDeRoperoParaPruebas()   // -> corredizas interior
        val r = v.roperoDelDibujo()!!
        assertEquals(TipoPuertas.CORREDIZAS, r.puertas)
        assertTrue(!r.verPuertas)
        v.ciclarPuertasDeRoperoParaPruebas()   // -> corredizas a la vista
        v.ciclarPuertasDeRoperoParaPruebas()   // -> sin puertas
        assertEquals(TipoPuertas.SIN, v.roperoDelDibujo()!!.puertas)
    }

    @Test
    fun el_ropero_sobrevive_al_guardado_del_apunte() {
        val v = vista()
        v.insertarPlantillaRopero()
        v.escribirCotaDeRoperoParaPruebas("ROPERO_ANCHO", 280f)
        v.escribirCotaDeRoperoParaPruebas("ROPERO_MALETERO", 35f)
        val json = v.exportEditableState()
        val otra = vista()
        assertTrue(otra.loadEditableState(json))
        val r = otra.roperoDelDibujo()!!
        assertEquals(280f, r.anchoCm, 0.01f)
        assertEquals(35f, r.maleteroCm, 0.01f)
        assertEquals("Ropero", otra.productoDelDibujo())
    }

    /** Deja una muestra del apunte con el ropero, para mirarla desde la PC. */
    @Test
    fun guarda_muestra_del_apunte() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val dir = ctx.getExternalFilesDir(null) ?: return
        val v = vista()
        val ropero = Ropero(anchoCm = 260f, altoCm = 240f, fondoCm = 60f, maleteroCm = 40f)
            .conCuerposIguales(3)
            .conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 4))
        v.insertarPlantillaRopero(ropero)
        v.fitContentInView()
        fun guardar(nombre: String) {
            val bmp = android.graphics.Bitmap.createBitmap(v.width, v.height, android.graphics.Bitmap.Config.ARGB_8888)
            v.draw(android.graphics.Canvas(bmp))
            java.io.FileOutputStream(java.io.File(dir, nombre)).use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        }
        guardar("apunte_ropero.png")
        v.ciclarPuertasDeRoperoParaPruebas()
        guardar("apunte_ropero_puertas.png")
    }

    /** La cadena entera: el ropero diseñado en el apunte llega a la calculadora con sus cuerpos. */
    @Test
    fun el_ropero_del_apunte_llega_a_la_calculadora_con_sus_cuerpos() {
        val v = vista()
        val ropero = Ropero(anchoCm = 260f, altoCm = 230f, fondoCm = 55f, maleteroCm = 40f, puertas = TipoPuertas.CORREDIZAS)
            .conCuerposIguales(3)
            .conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 4))
        v.insertarPlantillaRopero(ropero)
        val medida = v.medidaPrincipal()!!
        val item = ColaCalculadoras.MedidaCalc(
            producto = "Ropero", ancho = medida.anchoCm, alto = medida.altoCm, cantidad = 1f,
            cliente = "Pruebas", bocetoArchivo = "", disenoRopero = v.roperoDelDibujo()!!.aJson()
        )
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        crystal.crystal.casilla.ProyectoManager.inicializarDesdeStorage(ctx)
        if (!crystal.crystal.casilla.ProyectoManager.hayProyectoActivo()) {
            if (!crystal.crystal.casilla.MapStorage.existeProyecto(ctx, "Pruebas ropero")) crystal.crystal.casilla.MapStorage.crearProyecto(ctx, "Pruebas ropero", "")
            crystal.crystal.casilla.ProyectoManager.setProyectoActivo(ctx, "Pruebas ropero")
        }
        val intent = Intent(ctx, RoperoActivity::class.java).apply {
            putExtra("rcliente", "Pruebas")
            putExtra("ancho", item.ancho)
            putExtra("alto", item.alto)
            putExtra("cantidad", 1f)
            putExtra("producto", item.producto)
            putExtra(ColaCalculadoras.EXTRA_DESDE_MEDIDAS, true)
            putExtra(ColaCalculadoras.EXTRA_BOCETO_PATH, "")
            putExtra(ColaCalculadoras.EXTRA_COLA, arrayListOf(item))
            putExtra(ColaCalculadoras.EXTRA_INDICE, 0)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        ActivityScenario.launch<RoperoActivity>(intent).use { esc ->
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            esc.onActivity { a ->
                val vista = a.findViewById<VistaRopero>(R.id.vistaRopero)
                assertEquals(3, vista.ropero.cuerpos.size)
                assertEquals(TipoCuerpo.CAJONES, vista.ropero.cuerpos[1].tipo)
                assertEquals(40f, vista.ropero.maleteroCm, 0.01f)
                assertEquals(TipoPuertas.CORREDIZAS, vista.ropero.puertas)
                val referencias = a.findViewById<android.widget.TextView>(R.id.txReferencias).text.toString()
                assertTrue("no calculó al llegar: $referencias", referencias.contains("260 x 230 x 55"))
                assertTrue("faltan los rieles corredizos", a.findViewById<android.view.View>(R.id.lyRiel).visibility == android.view.View.VISIBLE)
            }
        }
    }
}
