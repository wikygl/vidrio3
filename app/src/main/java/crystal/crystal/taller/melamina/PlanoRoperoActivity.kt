package crystal.crystal.taller.melamina

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import crystal.crystal.Diseno.PlanoZoomView
import java.io.File
import java.io.FileOutputStream

/**
 * El plano técnico del ropero ([PlanoRopero]): lámina por lámina, con zoom y paneo, y todas juntas
 * a PDF para mandarlas al taller o al cliente.
 */
class PlanoRoperoActivity : AppCompatActivity() {

    private lateinit var ropero: Ropero
    private lateinit var materiales: MaterialesRopero
    private lateinit var encabezado: PlanoRopero.Encabezado
    private lateinit var zoom: PlanoZoomView
    private lateinit var tvLamina: TextView
    private var lamina = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ropero = Ropero.desdeJson(intent.getStringExtra(EXTRA_ROPERO)) ?: run {
            Toast.makeText(this, "No hay ropero para el plano", Toast.LENGTH_SHORT).show(); finish(); return
        }
        materiales = RoperoCalculo.calcular(ropero)
        encabezado = PlanoRopero.Encabezado(
            codigo = intent.getStringExtra(EXTRA_CODIGO).orEmpty(),
            proyecto = intent.getStringExtra(EXTRA_PROYECTO).orEmpty(),
            cliente = intent.getStringExtra(EXTRA_CLIENTE).orEmpty()
        )
        val dp = resources.displayMetrics.density
        fun boton(t: String, accion: () -> Unit) = Button(this).apply {
            text = t; isAllCaps = false; textSize = 12f; setOnClickListener { accion() }
        }
        tvLamina = TextView(this).apply { textSize = 13f; gravity = Gravity.CENTER; setTextColor(Color.BLACK) }
        val barra = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding((8 * dp).toInt(), (4 * dp).toInt(), (8 * dp).toInt(), (4 * dp).toInt())
            addView(boton("◀") { ir(lamina - 1) })
            addView(tvLamina, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            addView(boton("▶") { ir(lamina + 1) })
            addView(boton("PDF") { compartirPdf() })
            addView(boton("Piezas") { ProduccionRoperoActivity.abrir(this@PlanoRoperoActivity, ropero, encabezado.codigo) })
        }
        zoom = PlanoZoomView(this).apply { setBackgroundColor(Color.parseColor("#9E9E9E")) }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            addView(barra)
            addView(zoom, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
        })
        title = "Plano técnico" + encabezado.codigo.takeIf { it.isNotBlank() }?.let { " $it" }.orEmpty()
        ir(0)
    }

    private fun ir(i: Int) {
        val total = PlanoRopero.laminas(materiales)
        lamina = i.coerceIn(0, total - 1)
        tvLamina.text = "Lámina ${lamina + 1} de $total"
        zoom.setBitmapAjustado(renderizar(lamina))
    }

    /** La lámina rasterizada al triple, para que al acercar se lean las cotas. */
    private fun renderizar(i: Int): Bitmap {
        val k = 3f
        val bmp = Bitmap.createBitmap((PlanoRopero.ANCHO_PT * k).toInt(), (PlanoRopero.ALTO_PT * k).toInt(), Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.scale(k, k)
        PlanoRopero.dibujarLamina(c, i, ropero, materiales, encabezado)
        return bmp
    }

    private fun compartirPdf() {
        // Exportar a PDF es de pago, como en el resto de la app.
        if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeExportarPdf(),
                "Exportar el plano a PDF es una función de pago.")) return
        runCatching {
            val doc = PdfDocument()
            repeat(PlanoRopero.laminas(materiales)) { i ->
                val info = PdfDocument.PageInfo.Builder(PlanoRopero.ANCHO_PT.toInt(), PlanoRopero.ALTO_PT.toInt(), i + 1).create()
                val pagina = doc.startPage(info)
                PlanoRopero.dibujarLamina(pagina.canvas, i, ropero, materiales, encabezado)
                doc.finishPage(pagina)
            }
            val dir = File(cacheDir, "pdfs").apply { mkdirs() }
            val nombre = "plano_ropero_" + (encabezado.codigo.ifBlank { "${ropero.anchoCm.toInt()}x${ropero.altoCm.toInt()}" })
                .replace(Regex("[^A-Za-z0-9_-]"), "_")
            val file = File(dir, "$nombre.pdf")
            FileOutputStream(file).use { doc.writeTo(it) }
            doc.close()
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Plano técnico del ropero ${encabezado.codigo}".trim())
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }, "Compartir plano PDF"))
        }.onFailure { Toast.makeText(this, "No se pudo hacer el PDF: ${it.message}", Toast.LENGTH_LONG).show() }
    }

    companion object {
        const val EXTRA_ROPERO = "plano_ropero_json"
        const val EXTRA_CODIGO = "plano_ropero_codigo"
        const val EXTRA_PROYECTO = "plano_ropero_proyecto"
        const val EXTRA_CLIENTE = "plano_ropero_cliente"

        fun abrir(context: Context, ropero: Ropero, codigo: String = "", proyecto: String = "", cliente: String = "") {
            context.startActivity(Intent(context, PlanoRoperoActivity::class.java)
                .putExtra(EXTRA_ROPERO, ropero.aJson())
                .putExtra(EXTRA_CODIGO, codigo)
                .putExtra(EXTRA_PROYECTO, proyecto)
                .putExtra(EXTRA_CLIENTE, cliente))
        }
    }
}
