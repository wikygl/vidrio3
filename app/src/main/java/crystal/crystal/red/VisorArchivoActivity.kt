package crystal.crystal.red

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import crystal.crystal.databinding.ActivityVisorArchivoBinding
import java.io.File
import java.net.URL

class VisorArchivoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisorArchivoBinding
    private var reproductor: ExoPlayer? = null
    private var archivoDescriptor: ParcelFileDescriptor? = null
    private var pdfRenderer: PdfRenderer? = null
    private var paginaActualIdx = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisorArchivoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uriArchivo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("uri_archivo", Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("uri_archivo")
        } ?: run {
            finish()
            return
        }
        val tipo = intent.getStringExtra("tipo_archivo").orEmpty()

        when {
            tipo.startsWith("image") || tipo == "imagen" -> mostrarImagen(uriArchivo)
            tipo.startsWith("video") || tipo == "video" || tipo.startsWith("audio") || tipo == "audio" -> reproducirMultimedia(uriArchivo)
            tipo == "application/pdf" || tipo == "pdf" -> mostrarPdf(uriArchivo)
            else -> {
                Toast.makeText(this, "Tipo no soportado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun mostrarImagen(uri: Uri) {
        binding.visorImagen.visibility = View.VISIBLE
        Glide.with(this)
            .load(uri)
            .into(binding.visorImagen)
    }

    private fun reproducirMultimedia(uri: Uri) {
        binding.visorVideo.visibility = View.VISIBLE
        reproductor = ExoPlayer.Builder(this).build().also { player ->
            binding.visorVideo.player = player
            player.setMediaItem(MediaItem.fromUri(uri))
            player.prepare()
            player.play()
        }
    }

    private fun mostrarPdf(uri: Uri) {
        val filePdf = File(cacheDir, "temp_${System.currentTimeMillis()}.pdf")

        Thread {
            try {
                if (uri.scheme == "http" || uri.scheme == "https") {
                    URL(uri.toString()).openStream().use { input ->
                        filePdf.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                } else {
                    contentResolver.openInputStream(uri)?.use { input ->
                        filePdf.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    } ?: error("No se pudo abrir el PDF")
                }

                runOnUiThread {
                    abrirPdfDesdeArchivo(filePdf)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "No se pudo abrir el PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }.start()
    }

    private fun abrirPdfDesdeArchivo(filePdf: File) {
        archivoDescriptor = ParcelFileDescriptor.open(filePdf, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(archivoDescriptor!!)

        binding.visorPdfImagen.visibility = View.VISIBLE
        binding.botoneraPdf.visibility = View.VISIBLE

        binding.btnAnteriorPdf.setOnClickListener {
            if (paginaActualIdx > 0) mostrarPagina(paginaActualIdx - 1)
        }
        binding.btnSiguientePdf.setOnClickListener {
            pdfRenderer?.let {
                if (paginaActualIdx + 1 < it.pageCount) mostrarPagina(paginaActualIdx + 1)
            }
        }

        mostrarPagina(0)
    }

    private fun mostrarPagina(pos: Int) {
        pdfRenderer?.let { renderer ->
            paginaActualIdx = pos
            val page = renderer.openPage(pos)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            binding.visorPdfImagen.setImageBitmap(bitmap)
            binding.txtPaginaPdf.text = "Pagina ${pos + 1}/${renderer.pageCount}"
        }
    }

    override fun onStop() {
        super.onStop()
        reproductor?.release()
        archivoDescriptor?.close()
        pdfRenderer?.close()
    }

    companion object {
        fun abrir(activity: AppCompatActivity, uri: Uri, tipo: String) {
            val intent = Intent(activity, VisorArchivoActivity::class.java).apply {
                putExtra("uri_archivo", uri)
                putExtra("tipo_archivo", tipo)
            }
            activity.startActivity(intent)
        }
    }
}
