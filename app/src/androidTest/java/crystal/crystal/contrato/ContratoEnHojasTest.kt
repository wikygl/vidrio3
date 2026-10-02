package crystal.crystal.contrato

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import crystal.crystal.Listado
import crystal.crystal.comprobantes.DatosEmpresa
import crystal.crystal.pos.AmbientesDeProforma
import crystal.crystal.pos.MembreteDeProforma
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Date

/**
 * El contrato armado de verdad, para mirar cómo queda en el papel: el membrete, la tabla de los
 * trabajos, las cláusulas y las firmas juntas en la misma hoja. Se guarda como
 * `contrato_de_prueba.pdf` en la carpeta de la app.
 */
@RunWith(AndroidJUnit4::class)
class ContratoEnHojasTest {

    private fun item(producto: String, ambiente: String, costo: Float, escala: String = "m2") =
        Listado(
            escala = escala, uni = "Centímetros", medi1 = 180f, medi2 = 210f, medi3 = 0f,
            canti = 1f, piescua = 0f, precio = 0f, costo = costo, producto = producto,
            peri = 0f, metcua = 0f, metli = 0f, metcub = 0f, color = Color.BLUE, uri = ""
        ).also { AmbientesDeProforma.guardar(it, ambiente) }

    private fun firma(): Bitmap {
        val bmp = Bitmap.createBitmap(500, 180, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE)
        val p = Paint().apply { strokeWidth = 5f; color = Color.BLACK; style = Paint.Style.STROKE }
        c.drawLine(40f, 140f, 200f, 40f, p); c.drawLine(200f, 40f, 260f, 150f, p); c.drawLine(260f, 150f, 460f, 60f, p)
        return bmp
    }

    @Test
    fun el_contrato_sale_en_el_papel() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val d = DatosDelContrato(
            fecha = Date(),
            folio = "CRY-20261001124255-9113",
            cliente = ParteDelContrato(
                "Clínica Bilbao S.A.C.", "20601234567", "Av. Giráldez 450, Huancayo", "964 111 222",
                "Ana Ruiz Salazar", "40123456"
            ),
            contratista = ParteDelContrato(
                "Crystal Vidriería", "41888817", "Jr. San Pablo N° 200, barrio San Pablo, Sapallanga", "928 878 578"
            ),
            direccionObra = "",
            lista = listOf(
                item("Puerta corrediza de aluminio comercial 0.9 mm con policarbonato", "Sala de partos", 950f),
                item("Ventana corrediza de aluminio económico, vidrio arenado", "Sala de partos", 480f),
                item("División y puerta corrediza de melamina blanco 18 mm", "Consultorio 2", 820f),
                item("División y puerta corrediza de melamina blanco 18 mm", "Consultorio 3", 820f),
                item("Policarbonato alveolar 6 mm opaco, aluminio 0.9 mm", "Patio", 430f, "ml")
            ),
            plazoDias = 15,
            adelanto = 1750.0,
            fechaSaldo = "",
            observaciones = "Incluye el mantenimiento de las mayólicas despegadas del consultorio 2.",
            condiciones = CondicionesContrato(ciudad = "Huancayo", igv = Igv.INCLUIDO)
        )
        val membrete = MembreteDeProforma(
            empresa = DatosEmpresa("10418888171", "Crystal Vidriería", null, "Jr. San Pablo 200", "928878578"),
            tecnico = "", mostrarMembrete = true, selloRuta = "", mostrarSello = false, nota = ""
        )
        val pdf = ContratoPdf(ctx, membrete)
        val contrato = File(ctx.getExternalFilesDir(null), "contrato_de_prueba.pdf")
        pdf.contrato(contrato, d, firma(), firma())
        val recibo = File(ctx.getExternalFilesDir(null), "recibo_de_prueba.pdf")
        pdf.recibo(recibo, d, null, firma())
        assertTrue(contrato.length() > 0)
        assertTrue(recibo.length() > 0)
    }
}
