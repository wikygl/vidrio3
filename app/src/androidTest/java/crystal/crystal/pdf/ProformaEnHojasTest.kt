package crystal.crystal.pdf

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import crystal.crystal.Listado
import crystal.crystal.pos.OpcionDeProforma
import crystal.crystal.pos.OpcionesDeProforma
import crystal.crystal.pos.AmbientesDeProforma
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * La proforma armada de verdad, para mirar cómo quedan las hojas.
 *
 * Aquí no se comprueba una cuenta: se genera el PDF con unos cuantos ítems, sus opciones y sus
 * imágenes, y se guarda para poder abrirlo y ver si algún ítem sale partido o si alguna hoja queda
 * casi vacía. Es el equivalente a los retratos del apunte.
 */
@RunWith(AndroidJUnit4::class)
class ProformaEnHojasTest {

    private fun foto(nombre: String, color: Int): String {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val f = File(ctx.getExternalFilesDir(null), nombre)
        val bmp = Bitmap.createBitmap(600, 400, Bitmap.Config.ARGB_8888)
        Canvas(bmp).drawColor(color)
        f.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 90, it) }
        bmp.recycle()
        return android.net.Uri.fromFile(f).toString()
    }

    private fun item(producto: String, ambiente: String, imagen: String, opciones: Int): Listado {
        val it = Listado(
            escala = "p2", uni = "Centímetros", medi1 = 328.2f, medi2 = 373.3f, medi3 = 0f,
            canti = 1f, piescua = 137.4f, precio = 45f, costo = 6182.96f, producto = producto,
            peri = 14f, metcua = 12.3f, metli = 0f, metcub = 0f, color = Color.BLUE, uri = imagen
        )
        AmbientesDeProforma.guardar(it, ambiente)
        if (opciones > 0) {
            OpcionesDeProforma.guardar(
                it,
                (1..opciones).map { n ->
                    OpcionDeProforma("Material $n con nombre largo", 45f + n * 12f, imagen)
                }
            )
        }
        return it
    }

    @Test
    fun retrato_de_la_proforma_de_opciones() {
        val azul = foto("f_azul.png", Color.rgb(90, 140, 200))
        val verde = foto("f_verde.png", Color.rgb(120, 190, 130))
        val lista = listOf(
            item("Ventana arenada", "Consultorio dientes", azul, 2),
            item("Mampara", "Consultorio dientes", verde, 3),
            item("Puerta", "Sala de partos", azul, 1),
            item("Ventana alta", "Sala de partos", verde, 4),
            item("Espejo", "Sala sexto piso", azul, 2),
            item("Ventana baño", "Sala sexto piso", verde, 0),
            item("Mampara ducha", "Admisión", azul, 3),
            item("Ventana pasillo", "Admisión", verde, 2)
        )

        val generador = PdfGenerator(ApplicationProvider.getApplicationContext<android.content.Context>())
        val archivo = generador.generarOpciones("Clínica Bilbao", lista)
        assertTrue("no salió el PDF", archivo?.exists() == true && archivo.length() > 0)
    }
}
