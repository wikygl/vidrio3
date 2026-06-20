package crystal.crystal.optimizadores.planchas

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/** Exporta planchas optimizadas a PDF (una por página) y lo comparte. */
object PlanchaPdfExport {

    private val fmt = PlanchaFormatter()

    fun exportarYCompartir(context: Context, planchas: List<PlanchaOptimizada>, nombreBase: String) {
        if (planchas.isEmpty()) {
            Toast.makeText(context, "No hay planchas para exportar", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val file = generarPdf(context, planchas, nombreBase)
            compartir(context, file)
        } catch (e: Exception) {
            Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun generarPdf(context: Context, planchas: List<PlanchaOptimizada>, nombreBase: String): File {
        val pdf = PdfDocument()
        val pageW = 595   // A4 ~ 72dpi
        val pageH = 842
        val margin = 30f

        planchas.forEachIndexed { idx, p ->
            val info = PdfDocument.PageInfo.Builder(pageW, pageH, idx + 1).create()
            val page = pdf.startPage(info)
            dibujarPlanchaEnPagina(context, page.canvas, p, margin, pageW.toFloat(), pageH.toFloat(), idx + 1, planchas.size)
            pdf.finishPage(page)
        }

        val dir = File(context.cacheDir, "pdfs").apply { mkdirs() }
        val safe = nombreBase.replace(Regex("[^A-Za-z0-9_-]+"), "_").ifBlank { "planchas" }
        val file = File(dir, "${safe}_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { pdf.writeTo(it) }
        pdf.close()
        return file
    }

    private fun dibujarPlanchaEnPagina(
        context: Context, c: Canvas, p: PlanchaOptimizada,
        margin: Float, pageW: Float, pageH: Float, num: Int, total: Int
    ) {
        val contentW = pageW - 2 * margin
        val pTitulo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#0099FF"); textSize = 16f; isFakeBoldText = true }
        val pInfo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#37474F"); textSize = 11f }
        val pLeyenda = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#37474F"); textSize = 10f }

        var y = margin + 16f
        c.drawText(p.nombre, margin, y, pTitulo); y += 18f

        val anchoC = fmt.df1(p.anchoMm / 10f)
        val altoC = fmt.df1(p.altoMm / 10f)
        val areaTotal = p.anchoMm.toLong() * p.altoMm.toLong()
        val efic = if (areaTotal > 0) (p.areaUsadaMm2 * 100f / areaTotal) else 0f
        val tipo = if (p.esRetazoEntrada) "retazo" else "plancha base"
        c.drawText("$anchoC × $altoC cm  •  ${p.cortes.size} pieza(s)  •  $tipo  •  ${"%.1f".format(efic)}% uso", margin, y, pInfo)
        y += 14f

        // Dibujo de la plancha (reusa PlanchaVistaCorte)
        val vista = PlanchaVistaCorte(context).apply {
            setPlancha(p)
            setZoom(1f, contentW.toInt().coerceAtLeast(1))
        }
        vista.measure(
            View.MeasureSpec.makeMeasureSpec(contentW.toInt().coerceAtLeast(1), View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        vista.layout(0, 0, vista.measuredWidth.coerceAtLeast(1), vista.measuredHeight.coerceAtLeast(1))
        val bmp = Bitmap.createBitmap(vista.measuredWidth.coerceAtLeast(1), vista.measuredHeight.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
        val cb = Canvas(bmp)
        cb.drawColor(Color.WHITE)
        vista.draw(cb)

        val maxH = pageH * 0.45f
        val escala = minOf(contentW / bmp.width, maxH / bmp.height)
        val drawW = bmp.width * escala
        val drawH = bmp.height * escala
        c.drawBitmap(bmp, null, RectF(margin, y, margin + drawW, y + drawH), null)
        y += drawH + 16f
        bmp.recycle()

        // Leyenda numerada con cuadro de color
        val paleta = vista.paleta
        for ((i, corte) in p.cortes.withIndex()) {
            if (y > pageH - margin - 4f) break
            val cuadro = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = paleta[i % paleta.size] }
            c.drawRect(margin, y - 8f, margin + 10f, y + 2f, cuadro)
            val ac = fmt.df1(corte.anchoMm / 10f)
            val al = fmt.df1(corte.altoMm / 10f)
            val rot = if (corte.rotada) " (rotada)" else ""
            c.drawText("${i + 1}. ${corte.descripcion}  —  $ac × $al cm$rot", margin + 16f, y, pLeyenda)
            y += 14f
        }

        c.drawText("$num / $total", pageW - margin - 28f, pageH - 14f, pInfo)
    }

    private fun compartir(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir PDF"))
    }
}
