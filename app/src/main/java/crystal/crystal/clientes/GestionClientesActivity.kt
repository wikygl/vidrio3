package crystal.crystal.clientes

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import crystal.crystal.R
import crystal.crystal.databinding.ActivityGestionClientesBinding
import kotlinx.coroutines.launch

/**
 * Activity para gestión de clientes
 * CRUD completo con búsqueda
 */
class GestionClientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestionClientesBinding
    private val viewModel: ClienteViewModel by viewModels()
    private lateinit var adapter: ClienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGestionClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        observarClientes()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Gestión de Clientes"
    }

    private fun setupRecyclerView() {
        adapter = ClienteAdapter(
            onClienteClick = { cliente ->
                mostrarDialogoEditar(cliente)
            },
            onEliminarClick = { cliente ->
                confirmarEliminar(cliente)
            }
        )

        binding.rvClientes.layoutManager = LinearLayoutManager(this)
        binding.rvClientes.adapter = adapter
    }

    private fun setupFab() {
        binding.fabNuevoCliente.setOnClickListener {
            mostrarDialogoNuevo()
        }
    }

    private fun observarClientes() {
        viewModel.todosLosClientes.observe(this) { clientes ->
            adapter.submitList(clientes)

            // Mostrar mensaje si está vacío
            if (clientes.isEmpty()) {
                binding.tvVacio.visibility = android.view.View.VISIBLE
                binding.rvClientes.visibility = android.view.View.GONE
            } else {
                binding.tvVacio.visibility = android.view.View.GONE
                binding.rvClientes.visibility = android.view.View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_gestion_clientes, menu)

        // Configurar SearchView
        val searchItem = menu?.findItem(R.id.action_buscar)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                buscarClientes(newText ?: "")
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_sincronizar -> {
                sincronizar()
                true
            }
            R.id.action_estadisticas -> {
                mostrarEstadisticas()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun buscarClientes(query: String) {
        lifecycleScope.launch {
            val resultados = viewModel.buscar(query)
            adapter.submitList(resultados)
        }
    }

    private fun mostrarDialogoNuevo() {
        DialogoNuevoCliente.newInstance { nuevoCliente ->
            viewModel.crear(nuevoCliente)
            Toast.makeText(this, "Cliente creado", Toast.LENGTH_SHORT).show()
        }.show(supportFragmentManager, "DialogoNuevoCliente")
    }

    private fun mostrarDialogoEditar(cliente: Cliente) {
        DialogoNuevoCliente.newInstance(cliente) { clienteEditado ->
            viewModel.actualizar(clienteEditado)
            Toast.makeText(this, "Cliente actualizado", Toast.LENGTH_SHORT).show()
        }.show(supportFragmentManager, "DialogoEditarCliente")
    }

    private fun confirmarEliminar(cliente: Cliente) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar cliente")
            .setMessage("¿Eliminar a ${cliente.nombreCompleto}?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminar(cliente.id)
                Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun sincronizar() {
        binding.swipeRefresh.isRefreshing = true
        viewModel.sincronizarAhora()

        // Simular finalización (en producción usar callback)
        binding.swipeRefresh.postDelayed({
            binding.swipeRefresh.isRefreshing = false
            Toast.makeText(this, "Sincronizado", Toast.LENGTH_SHORT).show()
        }, 2000)
    }

    private fun mostrarEstadisticas() {
        lifecycleScope.launch {
            val total = viewModel.contarClientes()
            val vendido = viewModel.obtenerTotalVendido()

            val mensaje = """
                📊 Estadísticas
                
                Total clientes: $total
                Total vendido: S/ ${"%.2f".format(vendido)}
            """.trimIndent()

            androidx.appcompat.app.AlertDialog.Builder(this@GestionClientesActivity)
                .setTitle("Estadísticas")
                .setMessage(mensaje)
                .setPositiveButton("Cerrar", null)
                .show()
        }
    }
}