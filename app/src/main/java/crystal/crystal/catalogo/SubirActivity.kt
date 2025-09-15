package crystal.crystal.catalogo

import android.annotation.SuppressLint
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
import com.google.firebase.storage.FirebaseStorage
import crystal.crystal.databinding.ActivitySubirBinding
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Suppress("DEPRECATION")
class SubirActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2

    private lateinit var binding: ActivitySubirBinding
    private lateinit var storage: FirebaseStorage
    private var categoriaSeleccionada: String? = null
    private var imagenSeleccionada: Uri? = null
    private var esLocal: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Storage
        storage = FirebaseStorage.getInstance()

        // Obtener categoría del Intent
        categoriaSeleccionada = intent.getStringExtra("categoria")
        esLocal = intent.getBooleanExtra("esLocal", false)

        // Mostrar categoría seleccionada y tipo de subida
        val tipoSubida = if (esLocal) "Equipo Local" else "Firebase Storage"
        binding.tvCategoriaSeleccionada?.text = "Categoría: ${categoriaSeleccionada ?: "Ninguna"}\nDestino: $tipoSubida"

        configurarBotones()

        configurarFirebase()
    }

    private fun configurarBotones() {
        binding.btElegir.setOnClickListener {
            val options = arrayOf<CharSequence>("Tomar foto", "Elegir de la galería")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Elige una opción")
            builder.setItems(options) { _, item ->
                when (item) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            builder.show()
        }

        binding.imgVistaPrevia.setOnClickListener {
            if (binding.frameLayoutEdit.visibility == View.VISIBLE) {
                binding.frameLayoutEdit.visibility = View.INVISIBLE
            } else {
                binding.frameLayoutEdit.visibility = View.VISIBLE
            }
        }

        // Botón para subir imagen
        binding.btnSubirImagen?.setOnClickListener {
            if (esLocal) {
                guardarImagenLocal()
            } else {
                subirImagenAFirebase()
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    @SuppressLint("IntentReset")
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
                        binding.imgVistaPrevia.setImageBitmap(imageBitmap)
                        // Para implementar subida desde cámara, necesitarías convertir bitmap a URI
                    }
                }
                REQUEST_IMAGE_GALLERY -> {
                    if (resultCode == RESULT_OK && data != null) {
                        imagenSeleccionada = data.data

                        if (imagenSeleccionada != null) {
                            // Carga la imagen utilizando Glide en imageViewPreview
                            Glide.with(this)
                                .load(imagenSeleccionada)
                                .into(binding.imgVistaPrevia)
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

    private fun subirImagenAFirebase() {
        val categoria = categoriaSeleccionada
        val imagen = imagenSeleccionada

        if (categoria == null) {
            Toast.makeText(this, "No hay categoría seleccionada", Toast.LENGTH_SHORT).show()
            return
        }

        if (imagen == null) {
            Toast.makeText(this, "No hay imagen seleccionada", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear nombre único para la imagen
        val nombreImagen = "${categoria}_${UUID.randomUUID()}.jpg"

        // Referencia a la carpeta de la categoría en Firebase Storage
        val storageRef = storage.reference.child(categoria).child(nombreImagen)

        // Mostrar progreso
        binding.progressBar?.visibility = View.VISIBLE
        binding.btnSubirImagen?.isEnabled = false

        storageRef.putFile(imagen)
            .addOnSuccessListener {
                Toast.makeText(this, "Imagen subida exitosamente a $categoria", Toast.LENGTH_SHORT).show()
                limpiarFormulario()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al subir imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                binding.progressBar?.visibility = View.GONE
                binding.btnSubirImagen?.isEnabled = true
            }
    }

    private fun guardarImagenLocal() {
        val categoria = categoriaSeleccionada
        val imagen = imagenSeleccionada

        if (categoria == null) {
            Toast.makeText(this, "No hay categoría seleccionada", Toast.LENGTH_SHORT).show()
            return
        }

        if (imagen == null) {
            Toast.makeText(this, "No hay imagen seleccionada", Toast.LENGTH_SHORT).show()
            return
        }

        // Mostrar progreso
        binding.progressBar?.visibility = View.VISIBLE
        binding.btnSubirImagen?.isEnabled = false

        try {
            // Crear directorio local para la categoría
            val directorioCategoria = File(filesDir, "catalogo_local/$categoria")
            if (!directorioCategoria.exists()) {
                directorioCategoria.mkdirs()
            }

            // Crear nombre único para la imagen
            val nombreImagen = "${categoria}_${UUID.randomUUID()}.jpg"
            val archivoImagen = File(directorioCategoria, nombreImagen)

            // Convertir URI a Bitmap y guardar
            val inputStream = contentResolver.openInputStream(imagen)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val outputStream = FileOutputStream(archivoImagen)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()

            Toast.makeText(this, "Imagen guardada localmente en $categoria", Toast.LENGTH_SHORT).show()
            limpiarFormulario()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } finally {
            binding.progressBar?.visibility = View.GONE
            binding.btnSubirImagen?.isEnabled = true
        }
    }

    private fun limpiarFormulario() {
        imagenSeleccionada = null
        binding.imgVistaPrevia.setImageResource(android.R.color.transparent)
        Toast.makeText(this, "¿Subir otra imagen?", Toast.LENGTH_SHORT).show()
    }

    private fun configurarFirebase() {
        storage = FirebaseStorage.getInstance()
        // Verificar que está inicializado correctamente
        try {
            val ref = storage.reference
            android.util.Log.d("Firebase", "Storage inicializado correctamente")
        } catch (e: Exception) {
            android.util.Log.e("Firebase", "Error al inicializar Storage: ${e.message}")
        }
    }
}