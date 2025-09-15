package crystal.crystal.catalogo

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.storage.FirebaseStorage
import crystal.crystal.databinding.ActivityFotosBinding
import crystal.crystal.red.VisorArchivoActivity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class Fotos : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    private lateinit var binding: ActivityFotosBinding
    private lateinit var storage: FirebaseStorage
    private var categoria: String? = null
    private var imagenesColeccion = mutableListOf<Data>()
    private var primeraVez = true
    private var imagenPendienteGuardar: Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFotosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Storage
        storage = FirebaseStorage.getInstance()

        // Obtener categoría del Intent
        categoria = intent.getStringExtra("categoria")

        // Configurar título
        title = categoria ?: "Galería"

        configurarRecyclerFotos()
        configurarBotonesFiltro()
        cargarTodasLasImagenes()

        // Botón para agregar más imágenes
        binding.btnAgregarFoto?.setOnClickListener {
            mostrarOpcionesSubida()
        }
    }

    private fun configurarRecyclerFotos() {
        // Usar GridLayoutManager para mostrar en cuadrícula
        binding.recyclerFotos.layoutManager = GridLayoutManager(this, 2)

        // Usar el mismo Adapter que CatalogoActivity
        binding.recyclerFotos.adapter = Adapter(imagenesColeccion,
            clicado = { data ->
                // Click normal: ir a visor de imagen
                irAVisorImagen(data)
            },
            longClick = { data ->
                // Long click: mostrar opciones de eliminación
                mostrarOpcionesImagen(data)
            }
        )
    }

    private fun configurarBotonesFiltro() {
        // Botones para filtrar qué tipo de imágenes mostrar
        binding.btnMostrarStorage?.setOnClickListener {
            cargarImagenesDeStorage()
        }

        binding.btnMostrarLocal?.setOnClickListener {
            cargarImagenesLocales()
        }

        binding.btnMostrarTodas?.setOnClickListener {
            cargarTodasLasImagenes()
        }
    }

    private fun cargarTodasLasImagenes() {
        imagenesColeccion.clear()

        // Primero cargar las de Storage
        categoria?.let { nombreCategoria ->
            val storageRef = storage.reference.child(nombreCategoria)

            storageRef.listAll()
                .addOnSuccessListener { listResult ->
                    var contadorStorage = 0
                    val totalStorage = listResult.items.size

                    if (totalStorage > 0) {
                        listResult.items.forEach { item ->
                            item.downloadUrl.addOnSuccessListener { uri ->
                                imagenesColeccion.add(Data(uri.toString(), "Storage: ${item.name}"))
                                contadorStorage++

                                if (contadorStorage == totalStorage) {
                                    // Terminó de cargar Storage, cargar locales
                                    cargarImagenesLocalesAdicionales()
                                }
                            }.addOnFailureListener {
                                contadorStorage++
                                if (contadorStorage == totalStorage) {
                                    cargarImagenesLocalesAdicionales()
                                }
                            }
                        }
                    } else {
                        // No hay imágenes en Storage, cargar solo locales
                        cargarImagenesLocalesAdicionales()
                    }
                }
                .addOnFailureListener {
                    // Error en Storage, cargar solo locales
                    cargarImagenesLocalesAdicionales()
                }
        }
    }

    private fun cargarImagenesLocalesAdicionales() {
        categoria?.let { nombreCategoria ->
            val directorioLocal = File(filesDir, "catalogo_local/$nombreCategoria")

            if (directorioLocal.exists()) {
                val archivosLocales = directorioLocal.listFiles()?.filter {
                    it.isFile && (it.extension == "jpg" || it.extension == "jpeg" || it.extension == "png")
                }

                archivosLocales?.forEach { archivo ->
                    imagenesColeccion.add(Data("file://${archivo.absolutePath}", "Local: ${archivo.name}"))
                }
            }

            // Actualizar RecyclerView con todas las imágenes
            binding.recyclerFotos.adapter?.notifyDataSetChanged()

            val totalStorage = imagenesColeccion.count { it.nombre.startsWith("Storage:") }
            val totalLocal = imagenesColeccion.count { it.nombre.startsWith("Local:") }
            Toast.makeText(this, "Storage: $totalStorage | Local: $totalLocal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarImagenesDeStorage() {
        imagenesColeccion.clear()

        categoria?.let { nombreCategoria ->
            val storageRef = storage.reference.child(nombreCategoria)

            storageRef.listAll()
                .addOnSuccessListener { listResult ->
                    if (listResult.items.isEmpty()) {
                        Toast.makeText(this, "No hay imágenes en Storage para esta categoría", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    var imagenesCargadas = 0
                    val totalImagenes = listResult.items.size

                    listResult.items.forEach { item ->
                        item.downloadUrl.addOnSuccessListener { uri ->
                            imagenesColeccion.add(Data(uri.toString(), "Storage: ${item.name}"))
                            imagenesCargadas++

                            if (imagenesCargadas == totalImagenes) {
                                // Todas las imágenes se cargaron, actualizar RecyclerView
                                binding.recyclerFotos.adapter?.notifyDataSetChanged()
                                Toast.makeText(this, "Cargadas $totalImagenes imágenes de Storage", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener {
                            imagenesCargadas++
                            if (imagenesCargadas == totalImagenes) {
                                binding.recyclerFotos.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error al cargar imágenes de Storage: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun cargarImagenesLocales() {
        imagenesColeccion.clear()

        categoria?.let { nombreCategoria ->
            val directorioLocal = File(filesDir, "catalogo_local/$nombreCategoria")

            if (directorioLocal.exists()) {
                val archivosLocales = directorioLocal.listFiles()?.filter {
                    it.isFile && (it.extension == "jpg" || it.extension == "jpeg" || it.extension == "png")
                }

                archivosLocales?.forEach { archivo ->
                    imagenesColeccion.add(Data("file://${archivo.absolutePath}", "Local: ${archivo.name}"))
                }

                binding.recyclerFotos.adapter?.notifyDataSetChanged()
                Toast.makeText(this, "Mostrando ${imagenesColeccion.size} imágenes locales", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No hay imágenes locales en esta categoría", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarOpcionesImagen(data: Data) {
        val esImagenStorage = data.nombre.startsWith("Storage:")
        val esImagenLocal = data.nombre.startsWith("Local:")

        val opciones = if (esImagenStorage) {
            arrayOf("Guardar en mi dispositivo", "Eliminar de Storage")
        } else if (esImagenLocal) {
            arrayOf("Eliminar del equipo local")
        } else {
            arrayOf("Guardar en mi dispositivo", "Eliminar imagen")
        }

        AlertDialog.Builder(this)
            .setTitle("Opciones para imagen")
            .setItems(opciones) { _, which ->
                when {
                    esImagenStorage -> {
                        when (which) {
                            0 -> guardarImagenEnDispositivo(data)
                            1 -> confirmarEliminarImagen(data)
                        }
                    }
                    esImagenLocal -> {
                        when (which) {
                            0 -> eliminarImagenLocal(data)
                        }
                    }
                    else -> {
                        when (which) {
                            0 -> guardarImagenEnDispositivo(data)
                            1 -> confirmarEliminarImagen(data)
                        }
                    }
                }
            }
            .show()
    }

    private fun guardarImagenEnDispositivo(data: Data) {
        // Verificar permisos primero
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ no necesita permisos de escritura para MediaStore
            procederConDescarga(data)
        } else {
            // Android 9 y menor necesita permiso de escritura
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                procederConDescarga(data)
            } else {
                // Pedir permiso
                imagenPendienteGuardar = data
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun procederConDescarga(data: Data) {
        Toast.makeText(this, "Descargando imagen...", Toast.LENGTH_SHORT).show()

        // Usar Glide para descargar la imagen como Bitmap
        Glide.with(this)
            .asBitmap()
            .load(data.productos) // URL de Firebase Storage
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Imagen descargada exitosamente, ahora guardarla
                    guardarBitmapEnGaleria(resource, data.nombre)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // No hacer nada
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    Toast.makeText(this@Fotos, "Error al descargar la imagen", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun guardarBitmapEnGaleria(bitmap: Bitmap, nombreImagen: String) {
        try {
            val filename = "CatalogoPersonal_${categoria}_${System.currentTimeMillis()}.jpg"
            var saved = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Para Android 10 y superior - usar MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CatalogoPersonal")
                }

                val resolver = contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    val outputStream: OutputStream? = resolver.openOutputStream(it)
                    outputStream?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        saved = true
                    }
                }
            } else {
                // Para Android 9 y inferior - método tradicional
                val imagesDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "CatalogoPersonal"
                )

                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }

                val imageFile = File(imagesDir, filename)
                val outputStream = FileOutputStream(imageFile)

                outputStream.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    saved = true
                }

                // Notificar a la galería que se agregó una nueva imagen
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }

            if (saved) {
                Toast.makeText(this, "Imagen guardada en Galería > CatalogoPersonal", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, proceder con la descarga
                imagenPendienteGuardar?.let { procederConDescarga(it) }
            } else {
                Toast.makeText(this, "Permiso necesario para guardar imágenes", Toast.LENGTH_SHORT).show()
            }
            imagenPendienteGuardar = null
        }
    }

    private fun eliminarImagenLocal(data: Data) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar imagen local")
            .setMessage("¿Estás seguro de eliminar esta imagen del equipo local?")
            .setPositiveButton("Sí") { _, _ ->
                try {
                    // Extraer el path del URI file://
                    val filePath = data.productos.removePrefix("file://")
                    val archivo = File(filePath)

                    if (archivo.exists() && archivo.delete()) {
                        Toast.makeText(this, "Imagen local eliminada", Toast.LENGTH_SHORT).show()
                        imagenesColeccion.remove(data)
                        binding.recyclerFotos.adapter?.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this, "Error al eliminar imagen local", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun confirmarEliminarImagen(data: Data) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar imagen de Storage")
            .setMessage("¿Estás seguro de eliminar esta imagen de Firebase Storage?")
            .setPositiveButton("Sí") { _, _ ->
                eliminarImagen(data)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun eliminarImagen(data: Data) {
        val storageRef = storage.getReferenceFromUrl(data.productos)
        storageRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Imagen eliminada de Storage", Toast.LENGTH_SHORT).show()
                // Remover de la lista y actualizar adapter
                imagenesColeccion.remove(data)
                binding.recyclerFotos.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar imagen de Storage", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarOpcionesSubida() {
        val opciones = arrayOf("Subir a Storage", "Subir al equipo local")

        AlertDialog.Builder(this)
            .setTitle("¿Dónde quieres subir la imagen?")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> irASubirActivity(esLocal = false)
                    1 -> irASubirActivity(esLocal = true)
                }
            }
            .show()
    }

    private fun irASubirActivity(esLocal: Boolean = false) {
        val intent = Intent(this, SubirActivity::class.java)
        intent.putExtra("categoria", categoria)
        intent.putExtra("esLocal", esLocal)
        startActivity(intent)
    }

    private fun irAVisorImagen(data: Data) {
        try {
            val uri = Uri.parse(data.productos)
            VisorArchivoActivity.abrir(this, uri, "image/*")
        } catch (e: Exception) {
            Toast.makeText(this, "Error al abrir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Solo recargar imágenes si no es la primera vez (cuando volvamos de SubirActivity)
        if (!primeraVez) {
            cargarTodasLasImagenes()
        }
        primeraVez = false
    }
}