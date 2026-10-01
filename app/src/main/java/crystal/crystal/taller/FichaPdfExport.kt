package crystal.crystal.taller

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Exporta a PDF lo que se está viendo en la ficha (productos, materiales, clientes o planos), para
 * mandárselo a quien no tiene Crystal instalado.
 *
 * En vez de rearmar el documento con otro formato, se dibuja **el mismo adaptador** que ya está en
 * pantalla: cada fila se infla fuera de la vista, se mide y se pinta en la página. Así el PDF
 * muestra exactamente lo que el usuario vio —incluidos los diseños de cada ventana— y no hay dos
 * maquetaciones que mantener sincronizadas.
 *
 * Antes todo corría en el hilo de la pantalla y una ficha larga, con el celular justo de memoria,
 * lo trababa más de 5 segundos: Android mostraba "Crystal no responde" (ANR del 2026-10-01 en el
 * moto, dentro de `PdfDocument.writeTo`). Ahora en el hilo de la pantalla solo se infla y dibuja
 * cada fila, de a una; el PDF se arma y se escribe en segundo plano.
 */
object FichaPdfExport {

    // A4 en puntos (72 dpi), que es la unidad de PdfDocument.
    private const val ANCHO_PAGINA = 595
    private const val ALTO_PAGINA = 842
    private const val MARGEN = 24f
    /**
     * Se rasteriza más grande que la página para que el texto no salga borroso al imprimir. Era 2;
     * con 1.5 cada imagen pesa casi la mitad y el texto sigue nítido en A4.
     */
    private const val ESCALA_RENDER = 1.5f

    fun exportarYCompartir(
        activity: AppCompatActivity,
        adapter: RecyclerView.Adapter<*>,
        titulo: String,
        nombreBase: String
    ) {
        // Candado: exportar PDF es de pago, igual que en el resto de la app.
        if (!crystal.crystal.Suscripcion.exigir(
                activity, crystal.crystal.Suscripcion.puedeExportarPdf(),
                "Exportar la ficha a PDF es una función de pago."
            )
        ) return

        if (adapter.itemCount == 0) {
            Toast.makeText(activity, "No hay nada que exportar", Toast.LENGTH_SHORT).show()
            return
        }
        val total = adapter.itemCount
        val dialogo = AlertDialog.Builder(activity)
            .setTitle("Generando PDF")
            .setMessage("Fila 0 de $total")
            .setCancelable(false)
            .show()

        activity.lifecycleScope.launch {
            try {
                val file = generar(activity, adapter, titulo, nombreBase) { hecho ->
                    dialogo.setMessage("Fila $hecho de $total")
                }
                dialogo.dismiss()
                compartir(activity, file)
            } catch (e: CancellationException) {
                dialogo.dismiss()
                throw e
            } catch (e: Exception) {
                dialogo.dismiss()
                Toast.makeText(activity, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Las páginas se dibujan y se escriben en segundo plano, una tras otra; al hilo de la
     * pantalla solo se vuelve para pasar una fila a imagen (las vistas no se tocan desde otro hilo).
     */
    private suspend fun generar(
        context: Context,
        adapter: RecyclerView.Adapter<*>,
        titulo: String,
        nombreBase: String,
        avance: (Int) -> Unit
    ): File = withContext(Dispatchers.Default) {
        val pdf = PdfDocument()
        try {
            val anchoContenido = ANCHO_PAGINA - 2 * MARGEN
            val anchoRender = (anchoContenido * ESCALA_RENDER).toInt()
            val contenedor = withContext(Dispatchers.Main) { FrameLayout(context) }

            val pTitulo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#0099FF"); textSize = 14f; isFakeBoldText = true
            }
            val pPie = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#607D8B"); textSize = 9f
            }

            var numeroPagina = 0
            var pagina: PdfDocument.Page? = null
            var y = 0f

            fun abrirPagina() {
                numeroPagina++
                val info = PdfDocument.PageInfo.Builder(ANCHO_PAGINA, ALTO_PAGINA, numeroPagina).create()
                pagina = pdf.startPage(info)
                y = MARGEN + 14f
                pagina!!.canvas.drawText(titulo, MARGEN, y, pTitulo)
                y += 12f
            }

            fun cerrarPagina() {
                pagina?.let { p ->
                    p.canvas.drawText(
                        "Pagina $numeroPagina", ANCHO_PAGINA - MARGEN - 46f, ALTO_PAGINA - 12f, pPie
                    )
                    pdf.finishPage(p)
                }
                pagina = null
            }

            abrirPagina()

            val total = withContext(Dispatchers.Main) { adapter.itemCount }
            for (posicion in 0 until total) {
                val bmp = withContext(Dispatchers.Main) {
                    avance(posicion + 1)
                    // La lista pudo cambiar mientras se generaba.
                    if (posicion < adapter.itemCount) rasterizar(adapter, contenedor, posicion, anchoRender) else null
                } ?: continue
                // De píxeles de render a puntos de página.
                var ancho = anchoContenido
                var alto = bmp.height.toFloat() / ESCALA_RENDER

                val disponibleTotal = ALTO_PAGINA - 2 * MARGEN - 26f
                if (alto > disponibleTotal) {
                    // Una fila más alta que la página completa: se reduce para que entre entera.
                    val factor = disponibleTotal / alto
                    alto *= factor
                    ancho *= factor
                }
                if (y + alto > ALTO_PAGINA - MARGEN - 14f) {
                    cerrarPagina()
                    abrirPagina()
                }
                pagina!!.canvas.drawBitmap(bmp, null, RectF(MARGEN, y, MARGEN + ancho, y + alto), null)
                y += alto + 8f
                bmp.recycle()
            }

            cerrarPagina()

            val dir = File(context.cacheDir, "pdfs").apply { mkdirs() }
            val safe = nombreBase.replace(Regex("[^A-Za-z0-9_-]+"), "_").ifBlank { "ficha" }
            val sello = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(dir, "${safe}_$sello.pdf")
            FileOutputStream(file).use { pdf.writeTo(it) }
            file
        } finally {
            pdf.close()
        }
    }

    /**
     * Infla y dibuja una fila fuera de pantalla. Se usa el propio adaptador para que la fila salga
     * idéntica a la de la lista; devuelve null si la fila queda sin alto (nada que mostrar).
     */
    private fun rasterizar(
        adapter: RecyclerView.Adapter<*>,
        contenedor: ViewGroup,
        posicion: Int,
        anchoPx: Int
    ): Bitmap? {
        @Suppress("UNCHECKED_CAST")
        val adaptadorGenerico = adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
        val holder = adaptadorGenerico.createViewHolder(
            contenedor, adaptadorGenerico.getItemViewType(posicion)
        )
        adaptadorGenerico.bindViewHolder(holder, posicion)

        val vista = holder.itemView
        vista.measure(
            View.MeasureSpec.makeMeasureSpec(anchoPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val alto = vista.measuredHeight
        if (alto <= 0) return null
        vista.layout(0, 0, anchoPx, alto)

        // Sin canal de transparencia (la fila va sobre blanco): la mitad de memoria y el PDF no
        // tiene que guardar una máscara aparte por cada imagen.
        val bmp = Bitmap.createBitmap(anchoPx, alto, Bitmap.Config.RGB_565)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)
        vista.draw(canvas)
        return bmp
    }

    private fun compartir(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir ficha PDF"))
    }
}
