package crystal.crystal.datos

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
        listAllProducts()
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
        }
    }

    private fun updateProduct() {
        val product = leerProductoFormulario() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            val dao = DatabaseProvider.getInstance(this@ListaActivity).productDao()
            selectedProduct?.takeIf { it.nombre != product.nombre }?.let { dao.deleteProduct(it) }
            dao.insertProduct(product)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ListaActivity, "Actualizado", Toast.LENGTH_SHORT).show()
                clearCrudFields()
                listAllProducts()
            }
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
            binding.img.setImageURI(Uri.parse(selectedImages.first()))
        } else {
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
}
