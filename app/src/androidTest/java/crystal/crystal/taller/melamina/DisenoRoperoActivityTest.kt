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
                assertEquals(android.view.View.VISIBLE, a.findViewById<android.view.View>(R.id.scrollMando).visibility)
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
                // El imán: cerca de la cara de arriba del primer cajón del cuerpo 1 (a 20 del piso), se pega a ella.
                vista.alArrastrar?.invoke(movida, (movida.x0 + movida.x1) / 2f, piso + 21.3f)
                val pegada = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.ENTREPANO && it.cuerpo == 1 && it.indice == 0 }
                assertEquals(20f, pegada.y0 - piso, 0.01f)
                // Opciones: tapacanto de 2 mm y 4 hojas corredizas.
                a.findViewById<android.view.View>(R.id.btnOpciones).performClick()
                val opciones = a.findViewById<LinearLayout>(R.id.contenedorOpciones)
                val spinners = mutableListOf<android.widget.Spinner>()
                fun recorrer(v: android.view.View) { if (v is android.widget.Spinner) spinners.add(v); if (v is android.view.ViewGroup) for (i in 0 until v.childCount) recorrer(v.getChildAt(i)) }
                recorrer(opciones)
                // Zócalo, hojas maletero, puertas, posición, hojas corredizas, tapacanto puertas, melamina, fondo, tapacanto interior.
                spinners[8].setSelection(3)   // tapacanto interior 3 mm
                spinners[4].setSelection(3)   // 4 hojas
                botones(opciones).first { it.text == "Aplicar opciones" }.performClick()
                assertEquals(3f, vista.ropero.tapacantoGrosorMm, 0.01f)
                assertEquals(4, vista.ropero.hojasCorredizas)
            }
        }
    }

    @Test
    fun el_casillero_y_el_alto_del_lado_se_escriben_y_las_opciones_traen_la_calculadora() {
        ActivityScenario.launch<DisenoRoperoActivity>(intent()).use { esc ->
            esc.onActivity { a ->
                val vista = a.findViewById<VistaRopero>(R.id.vistaDiseno)
                val mando = a.findViewById<LinearLayout>(R.id.contenedorFlotante)
                val piso = RoperoGeometria.pisoY(vista.ropero)
                // El casillero de abajo del cuerpo 2 (casilleros, 4 repisas): 50 de alto libre desde su mando.
                val cas = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 1 && it.indice == 0 }
                vista.alTocarElemento?.invoke(cas)
                assertTrue(a.findViewById<TextView>(R.id.tvInfoSeleccion).text.startsWith("Casillero 1"))
                campos(mando).first().setText("50")
                botones(mando).first { it.text == "Poner" }.performClick()
                val primera = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.ENTREPANO && it.cuerpo == 1 && it.indice == 0 }
                assertEquals(50f, primera.y0 - piso, 0.01f)
                // El cuerpo 2 con su propio alto: 200. El cuerpo entero se elige tocando su cota de abajo.
                val (cx, cy) = vista.puntoDeCotaDeCuerpo(1)
                assertEquals(1, vista.elementoEnCota(cx, cy)?.cuerpo)
                assertEquals(null, vista.elementoEnCota(cx, cy - 200f))
                val cuerpo = RoperoGeometria.cuerpo(vista.ropero, 1)!!
                vista.alTocarElemento?.invoke(cuerpo)
                campos(mando)[1].setText("200")   // ancho, alto de este lado, repisas, cajones
                botones(mando).first { it.text == "Aplicar al cuerpo" }.performClick()
                assertEquals(200f, vista.ropero.cuerpos[1].altoCm, 0.01f)
                assertTrue(vista.ropero.altosDesiguales)
                assertEquals(200f - 1.8f, RoperoGeometria.techoY(vista.ropero, 1), 0.01f)
                // Las opciones traen lo de la calculadora: ancho 300, tres cuerpos, batientes, melamina 15, sin fondo.
                a.findViewById<android.view.View>(R.id.btnOpciones).performClick()
                val opciones = a.findViewById<LinearLayout>(R.id.contenedorOpciones)
                val spinners = mutableListOf<android.widget.Spinner>()
                fun recorrer(v: android.view.View) { if (v is android.widget.Spinner) spinners.add(v); if (v is android.view.ViewGroup) for (i in 0 until v.childCount) recorrer(v.getChildAt(i)) }
                recorrer(opciones)
                val c = campos(opciones)   // ancho, alto, fondo, cuerpos, zócalo, maletero, alto cajón, tubo
                c[0].setText("300")
                c[3].setText("3")
                spinners[2].setSelection(1)   // batientes
                spinners[6].setSelection(1)   // 15 mm
                spinners[7].setSelection(2)   // sin fondo
                botones(opciones).first { it.text == "Aplicar opciones" }.performClick()
                val r = vista.ropero
                assertEquals(300f, r.anchoCm, 0.01f)
                assertEquals(3, r.cuerpos.size)
                assertEquals(TipoPuertas.BATIENTES, r.puertas)
                assertEquals(15, r.espesorMm)
                assertTrue(!r.conFondo)
            }
        }
    }

    @Test
    fun unir_con_toca_la_vecina_y_las_une() {
        ActivityScenario.launch<DisenoRoperoActivity>(intent()).use { esc ->
            esc.onActivity { a ->
                val vista = a.findViewById<VistaRopero>(R.id.vistaDiseno)
                val mando = a.findViewById<LinearLayout>(R.id.contenedorFlotante)
                val cas = RoperoGeometria.elementos(vista.ropero).filter { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 1 }
                assertEquals(5, cas.size)
                vista.alTocarElemento?.invoke(cas[0])
                botones(mando).first { it.text == "Unir con…" }.performClick()
                vista.alTocarElemento?.invoke(cas[1])
                val despues = RoperoGeometria.elementos(vista.ropero).filter { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 1 }
                assertEquals(4, despues.size)
                assertEquals(cas[1].y1, despues[0].y1, 0.05f)
                // Queda elegida la celda unida.
                assertTrue(a.findViewById<TextView>(R.id.tvInfoSeleccion).text.startsWith("Casillero 1"))
            }
        }
    }

    @Test
    fun deshacer_y_rehacer_vuelven_por_los_cambios() {
        ActivityScenario.launch<DisenoRoperoActivity>(intent()).use { esc ->
            esc.onActivity { a ->
                val vista = a.findViewById<VistaRopero>(R.id.vistaDiseno)
                val mando = a.findViewById<LinearLayout>(R.id.contenedorFlotante)
                val cajon = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.CAJON && it.indice == 0 }
                vista.alTocarElemento?.invoke(cajon)
                campos(mando).first().setText("35")
                botones(mando).first { it.text == "Este" }.performClick()
                fun altoDelPrimero() = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.CAJON && it.indice == 0 }.let { it.y1 - it.y0 }
                assertEquals(35f, altoDelPrimero(), 0.01f)
                a.findViewById<android.view.View>(R.id.btnDeshacer).performClick()
                assertEquals(20f, altoDelPrimero(), 0.01f)
                a.findViewById<android.view.View>(R.id.btnRehacer).performClick()
                assertEquals(35f, altoDelPrimero(), 0.01f)
                // Deshacer dos veces seguidas no pasa de donde se empezó.
                a.findViewById<android.view.View>(R.id.btnDeshacer).performClick()
                a.findViewById<android.view.View>(R.id.btnDeshacer).performClick()
                assertEquals(20f, altoDelPrimero(), 0.01f)
            }
        }
    }

    @Test
    fun igualar_con_pone_la_repisa_a_la_altura_del_cajon_tocado() {
        ActivityScenario.launch<DisenoRoperoActivity>(intent()).use { esc ->
            esc.onActivity { a ->
                val vista = a.findViewById<VistaRopero>(R.id.vistaDiseno)
                val mando = a.findViewById<LinearLayout>(R.id.contenedorFlotante)
                val repisa = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.ENTREPANO && it.cuerpo == 1 && it.indice == 0 }
                vista.alTocarElemento?.invoke(repisa)
                botones(mando).first { it.text == "Igualar con…" }.performClick()
                // Se toca el segundo cajón del cuerpo 1: la repisa se pone en su cara de arriba (a 40 del piso).
                val cajon = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.CAJON && it.cuerpo == 0 && it.indice == 1 }
                vista.alTocarElemento?.invoke(cajon)
                val igualada = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.ENTREPANO && it.cuerpo == 1 && it.indice == 0 }
                assertEquals(cajon.y1, igualada.y0, 0.01f)
                // Y la tapa que remata los cajones se elige aunque el dedo caiga un poco dentro del cajón de abajo.
                val tapa = RoperoGeometria.elementos(vista.ropero).first { it.tipo == TipoElemento.TAPA_CAJONES && it.cuerpo == 0 }
                val tocado = RoperoGeometria.elementoEn(vista.ropero, (tapa.x0 + tapa.x1) / 2f, tapa.y0 - 1.5f)
                assertEquals(TipoElemento.TAPA_CAJONES, tocado!!.tipo)
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

    /** El ropero del plano del taller, por dentro y por fuera: zócalo delante, cajones a la vista, maletero al centro. */
    @Test
    fun guarda_captura_del_ropero_del_plano() {
        val plano = Ropero(anchoCm = 140f, altoCm = 238f, fondoCm = 60f, zocaloCm = 7f, maleteroCm = 40f, maleteroCuerpos = 2, maleteroHojas = 1)
            .conCuerposIguales(3)
            .conCuerpo(0, Cuerpo(tipo = TipoCuerpo.MIXTO, cajones = 4, cajonesALaVista = true, hojasBatientes = 1))
            .conCuerpo(1, Cuerpo(tipo = TipoCuerpo.COLGAR, entrepanos = 2))
            .conCuerpo(2, Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 6))
            .conAnchoDeCuerpo(0, 60f)
        val it = Intent(ApplicationProvider.getApplicationContext(), DisenoRoperoActivity::class.java)
            .putExtra(DisenoRoperoActivity.EXTRA_ROPERO, plano.aJson())
        ActivityScenario.launch<DisenoRoperoActivity>(it).use { esc ->
            listOf(false, true).forEach { conPuertas ->
                esc.onActivity { a -> a.findViewById<VistaRopero>(R.id.vistaDiseno).mostrarPuertas = conPuertas }
                Thread.sleep(600)
                esc.onActivity { a ->
                    val raiz = a.window.decorView
                    if (raiz.width == 0 || raiz.height == 0) return@onActivity
                    val bmp = android.graphics.Bitmap.createBitmap(raiz.width, raiz.height, android.graphics.Bitmap.Config.ARGB_8888)
                    raiz.draw(android.graphics.Canvas(bmp))
                    val dir = a.getExternalFilesDir(null) ?: return@onActivity
                    val nombre = if (conPuertas) "plano_ropero_puertas.png" else "plano_ropero_interior.png"
                    java.io.FileOutputStream(java.io.File(dir, nombre)).use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, it) }
                }
            }
        }
    }
    /** El plano de las columnas: casilleros partidos, cajones a todo lo ancho abajo, corredizas por casillero en el verde. */
    @Test
    fun guarda_captura_del_plano_de_columnas() {
        val e = 1.8f
        var plano = Ropero(anchoCm = 262f, altoCm = 236f, fondoCm = 60f, zocaloCm = 10.6f, puertas = TipoPuertas.SIN, puertasInteriores = true)
            .conCuerposIguales(3)
            .conCuerpo(0, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 2, altosCajonesCm = listOf(22f, 22.5f)))
            .conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 3, altosCajonesCm = listOf(16.2f, 15f, 17f)))
            .conCuerpo(2, Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 1, alturasEntrepanosCm = listOf(51.8f), puertasPropias = TipoPuertas.CORREDIZAS, puertasPorCasillero = true))
            .conAnchoDeCuerpo(0, 97.8f).conAnchoDeCuerpo(1, 59.8f)
        // Cuerpo 1: el casillero sobre los cajones partido en dos (49 | 47); la izquierda con tres
        // casilleros y el del medio partido otra vez (23.6 | el resto), con puerta batiente propia.
        val casA = RoperoGeometria.casilleros(plano, plano.cuerpos[0], RoperoGeometria.huecoDeCuerpo(plano, 0))[0]
        var a = plano.cuerpos[0].conCasilleroPartido(0, 2, casA.ancho, e)
        var gris = Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 2, puertasPropias = TipoPuertas.BATIENTES, hojasBatientes = 1)
        a = a.conCuerpoEn(listOf(0, 0), gris)
        a = a.conCuerpoEn(listOf(0, 1), Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 4))
        plano = plano.conCuerpo(0, a).conAnchoEn(0, listOf(0, 0), 49f)
        val huecoGris = RoperoGeometria.huecoDe(plano, 0, listOf(0, 0))!!
        gris = plano.cuerpoEn(0, listOf(0, 0))!!
        val casGris = RoperoGeometria.casilleros(plano, gris, huecoGris)[1]
        gris = gris.conCasilleroPartido(1, 2, casGris.ancho, e)
        gris = gris.conCuerpoEn(listOf(1, 0), Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 2))
        gris = gris.conCuerpoEn(listOf(1, 1), Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 2))
        plano = plano.conCuerpoEn(0, listOf(0, 0), gris).conAnchoEn(0, listOf(0, 0, 1, 0), 23.6f)
        // Cuerpo 2: el casillero sobre los tres cajones partido en dos de 29, con cuatro repisas cada uno.
        val casB = RoperoGeometria.casilleros(plano, plano.cuerpos[1], RoperoGeometria.huecoDeCuerpo(plano, 1))[0]
        var b = plano.cuerpos[1].conCasilleroPartido(0, 2, casB.ancho, e)
        b = b.conCuerpoEn(listOf(0, 0), Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 4))
        b = b.conCuerpoEn(listOf(0, 1), Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 4))
        plano = plano.conCuerpo(1, b)
        // El paquete guarda y devuelve las columnas.
        val vuelto = Ropero.desdeJson(plano.aJson())!!
        assertEquals(2, vuelto.cuerpos[0].columnasDe(0).size)
        assertEquals(2, vuelto.cuerpoEn(0, listOf(0, 0))!!.columnasDe(1).size)
        assertEquals(TipoPuertas.CORREDIZAS, vuelto.cuerpos[2].puertasPropias)
        assertEquals(49f, vuelto.cuerpos[0].columnasDe(0)[0].anchoCm, 0.05f)
        val it = Intent(ApplicationProvider.getApplicationContext(), DisenoRoperoActivity::class.java)
            .putExtra(DisenoRoperoActivity.EXTRA_ROPERO, plano.aJson())
        ActivityScenario.launch<DisenoRoperoActivity>(it).use { esc ->
            listOf(false, true).forEach { conPuertas ->
                esc.onActivity { a2 -> a2.findViewById<VistaRopero>(R.id.vistaDiseno).mostrarPuertas = conPuertas }
                Thread.sleep(600)
                esc.onActivity { a2 ->
                    val raiz = a2.window.decorView
                    if (raiz.width == 0 || raiz.height == 0) return@onActivity
                    val bmp = android.graphics.Bitmap.createBitmap(raiz.width, raiz.height, android.graphics.Bitmap.Config.ARGB_8888)
                    raiz.draw(android.graphics.Canvas(bmp))
                    val dir = a2.getExternalFilesDir(null) ?: return@onActivity
                    val nombre = if (conPuertas) "plano2_puertas.png" else "plano2_interior.png"
                    java.io.FileOutputStream(java.io.File(dir, nombre)).use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, it) }
                }
            }
        }
    }
}