package crystal.crystal.catalogo

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import crystal.crystal.databinding.ActivitySubirBinding
import kotlinx.android.synthetic.main.activity_subir.*

class SubirActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2

    private lateinit var binding : ActivitySubirBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySubirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ...

        buttonChooseImage.setOnClickListener {
            val options = arrayOf<CharSequence>("Tomar foto", "Elegir de la galería")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Elige una opción")
            builder.setItems(options) { dialog, item ->
                when (item) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            builder.show()
        }
        binding.imageViewPreview.setOnClickListener {
            if (frameLayoutEdit.visibility == View.VISIBLE) {
                frameLayoutEdit.visibility = View.INVISIBLE
            } else {
                frameLayoutEdit.visibility = View.VISIBLE
            }
        }

        // ...
    }

    // ...

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    if (resultCode == RESULT_OK) {
                        val imageBitmap = data?.extras?.get("data") as Bitmap
                        // Hacer algo con la imagen capturada desde la cámara
                    }
                }
                REQUEST_IMAGE_GALLERY -> {
                    if (resultCode == RESULT_OK && data != null) {
                        val selectedImageUri: Uri? = data.data

                        if (selectedImageUri != null) {
                            // Carga la imagen utilizando Glide en imageViewPreview
                            Glide.with(this)
                                .load(selectedImageUri)
                                .into(imageViewPreview)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Manejo de errores: muestra un mensaje de error o realiza alguna acción adecuada
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
