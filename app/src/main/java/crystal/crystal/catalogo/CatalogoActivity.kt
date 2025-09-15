package crystal.crystal.catalogo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import crystal.crystal.MainActivity
import crystal.crystal.databinding.ActivityCatalogoBinding
import java.io.File

class CatalogoActivity : AppCompatActivity() {

    private var productoSeleccionado: String? = null
    private lateinit var binding: ActivityCatalogoBinding
    private lateinit var storage: FirebaseStorage

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Storage
        storage = FirebaseStorage.getInstance()

        configureAdapter()

        binding.micro.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.btnCatalogo.setOnClickListener {
            startActivity(Intent(this, SubirActivity::class.java))
        }
    }

    private fun configureAdapter() {
        binding.recyclerView.adapter = Adapter(ListadoCat.productos,
            clicado = { data ->
                productoSeleccionado = data.nombre
                verificarColeccionEnStorage(data.nombre)
            },
            longClick = { data ->
                mostrarDialogoOpciones(data.nombre)
            }
        )
    }

    private fun verificarColeccionEnStorage(nombreProducto: String) {
        val storageRef = storage.reference.child(nombreProducto)

        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                // Verificar también si hay imágenes locales
                val imagenesLocales = obtenerImagenesLocales(nombreProducto)

                if (listResult.items.isEmpty() && imagenesLocales.isEmpty()) {
                    // No hay imágenes ni en Storage ni locales
                    Toast.makeText(this, "Colección '$nombreProducto' vacía. Selecciona dónde subir", Toast.LENGTH_SHORT).show()
                    mostrarOpcionesSubida(nombreProducto)
                } else {
                    // Hay imágenes, ir a ver galería
                    val totalStorage = listResult.items.size
                    val totalLocal = imagenesLocales.size
                    Toast.makeText(this, "Storage: $totalStorage | Local: $totalLocal imágenes", Toast.LENGTH_SHORT).show()
                    irAFotos(nombreProducto)
                }
            }
            .addOnFailureListener {
                // Error en Storage, verificar solo locales
                val imagenesLocales = obtenerImagenesLocales(nombreProducto)
                if (imagenesLocales.isEmpty()) {
                    Toast.makeText(this, "Creando nueva colección: $nombreProducto", Toast.LENGTH_SHORT).show()
                    mostrarOpcionesSubida(nombreProducto)
                } else {
                    Toast.makeText(this, "Mostrando ${imagenesLocales.size} imágenes locales", Toast.LENGTH_SHORT).show()
                    irAFotos(nombreProducto)
                }
            }
    }

    private fun mostrarOpcionesSubida(nombreProducto: String) {
        val opciones = arrayOf("Subir a Storage", "Subir al equipo local")

        AlertDialog.Builder(this)
            .setTitle("¿Dónde quieres subir las imágenes?")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> irASubirActivity(nombreProducto, esLocal = false)
                    1 -> irASubirActivity(nombreProducto, esLocal = true)
                }
            }
            .show()
    }

    private fun obtenerImagenesLocales(categoria: String): List<String> {
        return try {
            val directorioLocal = File(filesDir, "catalogo_local/$categoria")
            if (directorioLocal.exists()) {
                directorioLocal.listFiles()?.filter {
                    it.isFile && (it.extension == "jpg" || it.extension == "jpeg" || it.extension == "png")
                }?.map { it.absolutePath } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun mostrarDialogoOpciones(nombreProducto: String) {
        val opciones = arrayOf("Subir a Storage", "Subir al equipo local", "Ver galería")

        AlertDialog.Builder(this)
            .setTitle("Opciones para $nombreProducto")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> irASubirActivity(nombreProducto, esLocal = false)
                    1 -> irASubirActivity(nombreProducto, esLocal = true)
                    2 -> irAFotos(nombreProducto)
                }
            }
            .show()
    }

    private fun irASubirActivity(nombreProducto: String, esLocal: Boolean = false) {
        val intent = Intent(this, SubirActivity::class.java)
        intent.putExtra("categoria", nombreProducto)
        intent.putExtra("esLocal", esLocal)
        startActivity(intent)
    }

    private fun irAFotos(nombreProducto: String) {
        val intent = Intent(this, Fotos::class.java)
        intent.putExtra("categoria", nombreProducto)
        startActivity(intent)
    }
}