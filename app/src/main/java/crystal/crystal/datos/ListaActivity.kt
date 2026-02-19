package crystal.crystal.datos

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
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
    private lateinit var productAdapter: ProductAdapter  // <-- NUEVO: adaptador

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // EJEMPLO: si recibes algo de otra Activity
        val monto: Intent = intent
        val cantidad = monto.getStringExtra("monto")
        // binding.etml.setText("$cantidad") // si tuvieras un EditText 'etml'

        // Imagen de PDF (ejemplo)
        val bitmap = intent.getParcelableExtra<Bitmap>("pdf_image")
        binding.img.setImageBitmap(bitmap)

        // 1. CONFIGURAR RECYCLER VIEW + ADAPTER
        productAdapter = ProductAdapter()
        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(this@ListaActivity)
            adapter = productAdapter
        }

        // 2. LISTENERS DE LOS BOTONES
        binding.btnAdd.setOnClickListener {
            addProduct()
        }
        binding.btnUpdate.setOnClickListener {
            updateProduct()
        }
        binding.btnDelete.setOnClickListener {
            deleteProduct()
        }
        binding.btnListAll.setOnClickListener {
            listAllProducts()
        }
        binding.btnSearch.setOnClickListener {
            searchProducts()
        }
    }

    // ==============================
    // FUNCIÓN: AGREGAR PRODUCTO
    // ==============================
    private fun addProduct() {
        val desc = binding.editTextDescription.text.toString().trim()
        val price = binding.editTextPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val stock = binding.editTextStock.text.toString().trim().toIntOrNull() ?: 0

        // Si quieres validar campos:
        if (desc.isEmpty()) {
            Toast.makeText(this, "Descripción vacía", Toast.LENGTH_SHORT).show()
            return
        }

        val newProduct = Product(
            description = desc,
            price = price,
            stock = stock
        )

        CoroutineScope(Dispatchers.IO).launch {
            val db = DatabaseProvider.getInstance(this@ListaActivity)
            db.productDao().insertProduct(newProduct)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@ListaActivity, "Producto agregado", Toast.LENGTH_SHORT).show()
                // Limpia campos
                binding.editTextDescription.text.clear()
                binding.editTextPrice.text.clear()
                binding.editTextStock.text.clear()
            }
        }
    }

    // ==============================
    // FUNCIÓN: LISTAR TODOS
    // ==============================
    private fun listAllProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = DatabaseProvider.getInstance(this@ListaActivity)
            val productList = db.productDao().getAllProducts()
            withContext(Dispatchers.Main) {
                productAdapter.setData(productList)
                // Si quieres mostrar algo en lyProducto, puedes hacerlo aquí
            }
        }
    }

    // ==============================
    // FUNCIÓN: BUSCAR POR DESCRIPCIÓN
    // ==============================
    private fun searchProducts() {
        val searchText = binding.editTextSearch.text.toString().trim()
        if (searchText.isBlank()) {
            Toast.makeText(this, "Ingresa texto para buscar", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val db = DatabaseProvider.getInstance(this@ListaActivity)
            // % para buscar coincidencias parciales
            val results = db.productDao().searchProductsByDescription("%$searchText%")

            withContext(Dispatchers.Main) {
                productAdapter.setData(results)
            }
        }
    }

    // ==============================
    // FUNCIÓN: ACTUALIZAR POR ID
    // ==============================
    private fun updateProduct() {
        val idText = binding.editTextId.text.toString().trim()
        val id = idText.toIntOrNull()
        if (id == null) {
            Toast.makeText(this, "ID inválido para actualizar", Toast.LENGTH_SHORT).show()
            return
        }

        // Nuevos valores
        val desc = binding.editTextDescription.text.toString().trim()
        val price = binding.editTextPrice.text.toString().toDoubleOrNull() ?: 0.0
        val stock = binding.editTextStock.text.toString().toIntOrNull() ?: 0

        CoroutineScope(Dispatchers.IO).launch {
            val dao = DatabaseProvider.getInstance(this@ListaActivity).productDao()
            val existingProduct = dao.getProductById(id)
            if (existingProduct != null) {
                // Creamos uno nuevo con los campos actualizados
                val updated = existingProduct.copy(
                    description = desc,
                    price = price,
                    stock = stock
                )
                dao.updateProduct(updated)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ListaActivity, "Producto actualizado", Toast.LENGTH_SHORT).show()
                    clearCrudFields()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ListaActivity, "No existe producto con ID $id", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ==============================
    // FUNCIÓN: ELIMINAR POR ID
    // ==============================
    private fun deleteProduct() {
        val idText = binding.editTextId.text.toString().trim()
        val id = idText.toIntOrNull()
        if (id == null) {
            Toast.makeText(this, "ID inválido para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val dao = DatabaseProvider.getInstance(this@ListaActivity).productDao()
            val productToDelete = dao.getProductById(id)
            if (productToDelete != null) {
                dao.deleteProduct(productToDelete)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ListaActivity, "Producto eliminado", Toast.LENGTH_SHORT).show()
                    clearCrudFields()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ListaActivity, "No se encontró producto con ID $id", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ==============================
    // AUX: Limpiar campos
    // ==============================
    private fun clearCrudFields() {
        binding.editTextId.text.clear()
        binding.editTextDescription.text.clear()
        binding.editTextPrice.text.clear()
        binding.editTextStock.text.clear()
        binding.editTextSearch.text.clear()
    }
}
