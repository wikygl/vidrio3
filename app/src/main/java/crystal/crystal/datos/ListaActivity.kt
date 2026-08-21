package crystal.crystal.datos

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import crystal.crystal.databinding.ActivityListaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaBinding
    private lateinit var productAdapter: ProductAdapter
    private var selectedProduct: Product? = null
    private val selectedImages = mutableListOf<String>()

    companion object {
        private const val REQUEST_IMAGES = 410
        private const val MENU_RESPALDAR = 9001
        private const val MENU_RESTAURAR = 9002
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bitmap = intent.getParcelableExtra<Bitmap>("pdf_image")
        binding.img.setImageBitmap(bitmap)

        productAdapter = ProductAdapter { product -> cargarProductoEnFormulario(product) }
        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(this@ListaActivity)
            adapter = productAdapter
        }

        binding.btnAdd.setOnClickListener { addProduct() }
        binding.btnUpdate.setOnClickListener { updateProduct() }
        binding.btnDelete.setOnClickListener { deleteProduct() }
        binding.btnListAll.setOnClickListener { listAllProducts() }
        binding.btnSearch.setOnClickListener { searchProducts() }
        binding.btnImages.setOnClickListener { seleccionarImagenes() }

        actualizarResumenImagenes()
        cargarInicial()
    }

    private fun addProduct() {
        val product = leerProductoFormulario() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseProvider.getInstance(this@ListaActivity).productDao().insertProduct(product)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ListaActivity, "Guardado", Toast.LENGTH_SHORT).show()
                clearCrudFields()
                listAllProducts()
            }
            BaseLocalBackup.respaldarProducto(this@ListaActivity, product)  // auto-respaldo
        }
    }

    private fun updateProduct() {
        val product = leerProductoFormulario() ?: return
        val anterior = selectedProduct
        CoroutineScope(Dispatchers.IO).launch {
            val dao = DatabaseProvider.getInstance(this@ListaActivity).productDao()
            anterior?.takeIf { it.nombre != product.nombre }?.let { dao.deleteProduct(it) }
            dao.insertProduct(product)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ListaActivity, "Actualizado", Toast.LENGTH_SHORT).show()
                clearCrudFields()
                listAllProducts()
            }
            // Auto-respaldo: si se renombró, borra el remoto viejo; luego respalda el nuevo.
            anterior?.takeIf { it.nombre != product.nombre }?.let { BaseLocalBackup.eliminarRemoto(it.nombre) }
            BaseLocalBackup.respaldarProducto(this@ListaActivity, product)
        }
    }

    private fun deleteProduct() {
        val nombre = binding.editTextDescription.text.toString().trim()
        if (nombre.isBlank()) {
            Toast.makeText(this, "Selecciona o escribe un nombre", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val dao = DatabaseProvider.getInstance(this@ListaActivity).productDao()
            val productToDelete = selectedProduct?.takeIf { it.nombre == nombre }
                ?: dao.getProductByName(nombre)

            withContext(Dispatchers.Main) {
                if (productToDelete == null) {
                    Toast.makeText(this@ListaActivity, "No existe: $nombre", Toast.LENGTH_SHORT).show()
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.deleteProduct(productToDelete)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ListaActivity, "Eliminado", Toast.LENGTH_SHORT).show()
                            clearCrudFields()
                            listAllProducts()
                        }
                        BaseLocalBackup.eliminarRemoto(productToDelete.nombre)  // auto-respaldo
                    }
                }
            }
        }
    }

    private fun listAllProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val productList = DatabaseProvider.getInstance(this@ListaActivity)
                .productDao()
                .getAllProducts()
            withContext(Dispatchers.Main) {
                productAdapter.setData(productList)
            }
        }
    }

    private fun searchProducts() {
        val searchText = binding.editTextSearch.text.toString().trim()
        if (searchText.isBlank()) {
            listAllProducts()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val results = DatabaseProvider.getInstance(this@ListaActivity)
                .productDao()
                .searchProductsByName("%$searchText%")
            withContext(Dispatchers.Main) {
                productAdapter.setData(results)
            }
        }
    }

    private fun leerProductoFormulario(): Product? {
        val nombre = binding.editTextDescription.text.toString().trim()
        val price = binding.editTextPrice.text.toString().trim().toDoubleOrNull() ?: 0.0

        if (nombre.isBlank()) {
            Toast.makeText(this, "Ingresa el nombre", Toast.LENGTH_SHORT).show()
            return null
        }

        return Product(
            nombre = nombre,
            price = price,
            imagenes = selectedImages.distinct().joinToString("\n")
        )
    }

    @SuppressLint("SetTextI18n")
    private fun cargarProductoEnFormulario(product: Product) {
        selectedProduct = product
        selectedImages.clear()
        selectedImages.addAll(product.imagenes())

        binding.editTextDescription.setText(product.nombre)
        binding.editTextPrice.setText(if (product.price == 0.0) "" else product.price.toString())
        binding.tvNombre.text = product.nombre
        binding.tvPrecio.text = "S/ ${String.format("%.2f", product.price)}"
        binding.tvDescripcion.text = "Imagenes guardadas: ${selectedImages.size}"
        actualizarResumenImagenes()
    }

    private fun seleccionarImagenes() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, REQUEST_IMAGES)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_IMAGES || resultCode != RESULT_OK || data == null) return

        val uris = mutableListOf<Uri>()
        data.clipData?.let { clip ->
            for (i in 0 until clip.itemCount) {
                uris += clip.getItemAt(i).uri
            }
        } ?: data.data?.let { uris += it }

        val nuevasImagenes = uris.map { uri ->
            runCatching {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            uri.toString()
        }
        selectedImages.addAll(nuevasImagenes)
        val unicas = selectedImages.distinct()
        selectedImages.clear()
        selectedImages.addAll(unicas)
        actualizarResumenImagenes()
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarResumenImagenes() {
        binding.tvImagenesSeleccionadas.text = "Imagenes: ${selectedImages.size}"
        binding.tvDescripcion.text = if (selectedImages.isEmpty()) {
            "Sin imagenes seleccionadas."
        } else {
            "Imagenes seleccionadas: ${selectedImages.size}"
        }
        if (selectedImages.isNotEmpty()) {
            Glide.with(this).load(Uri.parse(selectedImages.first())).into(binding.img)
        } else {
            Glide.with(this).clear(binding.img)
            binding.img.setImageDrawable(null)
        }
    }

    private fun clearCrudFields() {
        selectedProduct = null
        selectedImages.clear()
        binding.editTextDescription.text.clear()
        binding.editTextPrice.text.clear()
        binding.editTextSearch.text.clear()
        binding.tvNombre.text = "Sin seleccion"
        binding.tvPrecio.text = "S/ 0.00"
        actualizarResumenImagenes()
    }

    // ==================== RESPALDO EN FIREBASE ====================

    /** Carga inicial: si la base local está vacía y hay sesión, la restaura desde la nube. */
    private fun cargarInicial() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = DatabaseProvider.getInstance(this@ListaActivity).productDao()
            var productos = dao.getAllProducts()
            var restaurados = 0
            if (productos.isEmpty() && FirebaseAuth.getInstance().currentUser != null) {
                restaurados = BaseLocalBackup.restaurar(this@ListaActivity)
                if (restaurados > 0) productos = dao.getAllProducts()
            }
            withContext(Dispatchers.Main) {
                productAdapter.setData(productos)
                if (restaurados > 0) {
                    Toast.makeText(this@ListaActivity, "Restaurados $restaurados productos desde la nube", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, MENU_RESPALDAR, 0, "☁ Respaldar en nube")
        menu?.add(0, MENU_RESTAURAR, 1, "⬇ Restaurar de nube")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            MENU_RESPALDAR -> { respaldarTodoManual(); true }
            MENU_RESTAURAR -> { restaurarManual(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun respaldarTodoManual() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(this, "Inicia sesión para respaldar", Toast.LENGTH_SHORT).show(); return
        }
        Toast.makeText(this, "Respaldando…", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            val n = BaseLocalBackup.respaldarTodo(this@ListaActivity)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@ListaActivity,
                    if (n >= 0) "Respaldados $n productos" else "No se pudo respaldar",
                    Toast.LENGTH_SHORT
                ).show()
                listAllProducts()
            }
        }
    }

    private fun restaurarManual() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(this, "Inicia sesión para restaurar", Toast.LENGTH_SHORT).show(); return
        }
        Toast.makeText(this, "Restaurando…", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            val n = BaseLocalBackup.restaurar(this@ListaActivity)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@ListaActivity,
                    if (n >= 0) "Restaurados $n productos" else "No se pudo restaurar",
                    Toast.LENGTH_SHORT
                ).show()
                listAllProducts()
            }
        }
    }
}
