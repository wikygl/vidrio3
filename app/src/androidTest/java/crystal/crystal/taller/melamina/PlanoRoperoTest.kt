package crystal.crystal.taller.melamina

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * El plano técnico de los roperos Rm3 y Rm4 del usuario (tal como están archivados): cada lámina
 * se pinta en un PNG (plano_rm3_1.png…) en los archivos de la app, para mirarlas, y el PDF entero
 * tiene que salir con todas.
 */
@RunWith(AndroidJUnit4::class)
class PlanoRoperoTest {

    private val rm3 = """{"ancho":300,"alto":238,"fondo":60,"espesor":18,"zocalo":7,"maletero":0,"puertas":"BATIENTES","conFondo":true,"altoCajon":18,"verPuertas":false,"tapacanto":0.45,"tapacantoPuertas":3,"espesorFondo":3,"maleteroCuerpos":0,"maleteroHojas":0,"zocaloDelante":true,"puertasInteriores":false,"interiorBlanco":true,"hojasCorredizas":0,"tuboBajoTope":6,"cuerpos":[{"ancho":58,"tipo":"CAJONES_CASILLEROS","entrepanos":1,"cajones":4,"hojasBatientes":1,"alto":0,"aLaVista":true,"altoCajonesFijo":80,"anchoFijo":true,"puertasPorCasillero":false,"tapa":true,"partes":[{"casillero":0,"columnas":[{"ancho":58,"tipo":"COLGAR","entrepanos":0,"cajones":0,"hojasBatientes":0,"alto":0,"aLaVista":false,"altoCajonesFijo":0,"anchoFijo":false,"puertasPorCasillero":false,"tapa":true,"altosCajones":[],"alturasEntrepanos":[]}]}],"altosCajones":[],"alturasEntrepanos":[189.60000610351562]},{"ancho":57.6,"tipo":"ENTREPANOS","entrepanos":5,"cajones":0,"hojasBatientes":0,"alto":0,"aLaVista":true,"altoCajonesFijo":0,"anchoFijo":true,"puertasPorCasillero":false,"tapa":true,"partes":[{"casillero":1,"columnas":[{"ancho":57.6,"tipo":"CAJONES","entrepanos":0,"cajones":2,"hojasBatientes":0,"alto":0,"aLaVista":true,"altoCajonesFijo":0,"anchoFijo":false,"puertasPorCasillero":false,"tapa":true,"altosCajones":[],"alturasEntrepanos":[]}]}],"altosCajones":[],"alturasEntrepanos":[44,80,121.20000457763672,156.60000610351562,189.59999084472656]},{"ancho":57.99997,"tipo":"ENTREPANOS","entrepanos":7,"cajones":0,"hojasBatientes":0,"alto":0,"aLaVista":false,"altoCajonesFijo":0,"anchoFijo":false,"puertasPorCasillero":false,"tapa":true,"altosCajones":[],"alturasEntrepanos":[]},{"ancho":57.6,"tipo":"ENTREPANOS","entrepanos":5,"cajones":0,"hojasBatientes":0,"alto":0,"aLaVista":true,"altoCajonesFijo":0,"anchoFijo":true,"puertasPorCasillero":false,"tapa":true,"partes":[{"casillero":1,"columnas":[{"ancho":57.6,"tipo":"CAJONES","entrepanos":0,"cajones":2,"hojasBatientes":0,"alto":0,"aLaVista":true,"altoCajonesFijo":0,"anchoFijo":false,"puertasPorCasillero":false,"tapa":true,"altosCajones":[],"alturasEntrepanos":[]}]}],"altosCajones":[],"alturasEntrepanos":[44,80,121.20000457763672,156.60000610351562,189.59999084472656]},{"ancho":58,"tipo":"CAJONES_CASILLEROS","entrepanos":1,"cajones":4,"hojasBatientes":1,"alto":0,"aLaVista":true,"altoCajonesFijo":80,"anchoFijo":true,"puertasPorCasillero":false,"tapa":true,"partes":[{"casillero":0,"columnas":[{"ancho":58,"tipo":"COLGAR","entrepanos":0,"cajones":0,"hojasBatientes":0,"alto":0,"aLaVista":false,"altoCajonesFijo":0,"anchoFijo":false,"puertasPorCasillero":false,"tapa":true,"altosCajones":[],"alturasEntrepanos":[]}]}],"altosCajones":[],"alturasEntrepanos":[189.60000610351562]}]}"""
    private val rm4 = """{"ancho":155,"alto":230,"fondo":60,"espesor":18,"zocalo":7,"maletero":0,"puertas":"BATIENTES","conFondo":true,"altoCajon":18,"verPuertas":false,"tapacanto":0.45,"tapacantoPuertas":3,"espesorFondo":3,"maleteroCuerpos":0,"maleteroHojas":0,"zocaloDelante":true,"puertasInteriores":false,"interiorBlanco":true,"hojasCorredizas":0,"tuboBajoTope":6,"cuerpos":[{"ancho":74.799995,"tipo":"COLGAR","entrepanos":0,"cajones":0,"hojasBatientes":0,"alto":0,"aLaVista":false,"altoCajonesFijo":0,"anchoFijo":false,"puertasPorCasillero":false,"tapa":true,"altosCajones":[],"alturasEntrepanos":[]},{"ancho":74.799995,"tipo":"CAJONES","entrepanos":0,"cajones":4,"hojasBatientes":0,"alto":0,"aLaVista":true,"altoCajonesFijo":80,"anchoFijo":false,"puertasPorCasillero":false,"tapa":true,"altosCajones":[],"alturasEntrepanos":[]}]}"""

    private fun guardar(nombre: String, json: String) {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val r = Ropero.desdeJson(json)
        assertNotNull(r)
        val m = RoperoCalculo.calcular(r!!)
        val enc = PlanoRopero.Encabezado(nombre.uppercase().replace("RM", "Rm"), "melas", "Bilbao")
        val dir = ctx.getExternalFilesDir(null)!!
        repeat(PlanoRopero.laminas(m)) { i ->
            val k = 2f
            val bmp = Bitmap.createBitmap((PlanoRopero.ANCHO_PT * k).toInt(), (PlanoRopero.ALTO_PT * k).toInt(), Bitmap.Config.ARGB_8888)
            val c = Canvas(bmp); c.scale(k, k)
            PlanoRopero.dibujarLamina(c, i, r, m, enc)
            java.io.FileOutputStream(java.io.File(dir, "plano_${nombre}_${i + 1}.png")).use { bmp.compress(Bitmap.CompressFormat.PNG, 90, it) }
        }
        val doc = PdfDocument()
        repeat(PlanoRopero.laminas(m)) { i ->
            val p = doc.startPage(PdfDocument.PageInfo.Builder(842, 595, i + 1).create())
            PlanoRopero.dibujarLamina(p.canvas, i, r, m, enc)
            doc.finishPage(p)
        }
        val pdf = java.io.File(dir, "plano_$nombre.pdf")
        java.io.FileOutputStream(pdf).use { doc.writeTo(it) }
        doc.close()
        assertTrue(pdf.length() > 1000)
    }

    @Test
    fun la_pantalla_de_produccion_abre_y_busca() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = android.content.Intent(ctx, ProduccionRoperoActivity::class.java)
            .putExtra(ProduccionRoperoActivity.EXTRA_ROPERO, Ropero.desdeJson(rm4)!!.copy(colorExterior = "Cedro").aJson())
            .putExtra(ProduccionRoperoActivity.EXTRA_CODIGO, "Rm4")
        androidx.test.core.app.ActivityScenario.launch<ProduccionRoperoActivity>(intent).use { sc ->
            sc.onActivity { a ->
                val raiz = a.window.decorView
                val buscar = (raiz.rootView as android.view.ViewGroup).let { v -> buscarEditText(v) }!!
                buscar.setText("52.9")
            }
            Thread.sleep(500)
            sc.onActivity { a ->
                val v = a.window.decorView
                val bmp = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
                v.draw(Canvas(bmp))
                java.io.FileOutputStream(java.io.File(a.getExternalFilesDir(null), "produccion.png")).use { bmp.compress(Bitmap.CompressFormat.PNG, 90, it) }
            }
        }
    }

    private fun buscarEditText(v: android.view.View): android.widget.EditText? = when (v) {
        is android.widget.EditText -> v
        is android.view.ViewGroup -> (0 until v.childCount).firstNotNullOfOrNull { buscarEditText(v.getChildAt(it)) }
        else -> null
    }

    @Test
    fun guarda_las_piezas_de_taller_del_rm4() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val r = Ropero.desdeJson(rm4)!!.copy(colorExterior = "Cedro")
        val dir = ctx.getExternalFilesDir(null)!!
        fun guardar(nombre: String, bmp: Bitmap) = java.io.FileOutputStream(java.io.File(dir, nombre)).use { bmp.compress(Bitmap.CompressFormat.PNG, 90, it) }
        val tableros = RoperoProduccion.tableros(r)
        guardar("taller_lateral_der.png", PiezaTallerDibujo.tablero(tableros.first { it.nombre == "Lateral derecho" }))
        guardar("taller_division.png", PiezaTallerDibujo.tablero(tableros.first { it.nombre == "División" }))
        guardar("taller_puerta.png", PiezaTallerDibujo.tablero(tableros.first { it.nombre == "Puerta" }))
        guardar("taller_piso.png", PiezaTallerDibujo.tablero(tableros.first { it.nombre == "Piso" }))
        val fichas = RoperoProduccion.fichasDeCorte(r, RoperoCalculo.calcular(r), "Rm4")
        guardar("taller_corte_caja.png", PiezaTallerDibujo.corte(fichas.first { it.pieza.nombre == "Lateral cajón" }))
        guardar("taller_corte_lateral.png", PiezaTallerDibujo.corte(fichas.first { it.pieza.nombre == "Lateral" }))
        java.io.File(dir, "corte_rm4.csv").writeText(RoperoProduccion.csv(listOf(RoperoProduccion.Mueble("Rm4", r))))
    }

    @Test
    fun guarda_el_plano_del_rm3() = guardar("rm3", rm3)

    @Test
    fun guarda_el_plano_del_rm4() = guardar("rm4", rm4)
}
