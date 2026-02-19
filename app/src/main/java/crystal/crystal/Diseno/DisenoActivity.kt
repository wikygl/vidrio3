package crystal.crystal.Diseno

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import crystal.crystal.BaulActivity
import crystal.crystal.R
import crystal.crystal.casilla.MaterialData
import crystal.crystal.casilla.TipoMaterial
import crystal.crystal.databinding.ActivityDisenoBinding

class DisenoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisenoBinding
    private lateinit var adapter: MatAdapter

    private val lanzarBaul = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode != RESULT_OK) return@registerForActivityResult
        val data = res.data ?: return@registerForActivityResult
        procesarSeleccionBaul(data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisenoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MatAdapter { }
        binding.rvDiseno.apply {
            layoutManager = LinearLayoutManager(this@DisenoActivity)
            adapter = this@DisenoActivity.adapter
        }

        binding.btProducto.setOnClickListener {
            adapter.submitList(MaterialData.listaMat.filter { it.tipo == TipoMaterial.PRODUCTOS })
            binding.rvDiseno.visibility = View.VISIBLE
            binding.scMedidas.visibility = View.GONE
        }

        binding.btMaterial.setOnClickListener {
            adapter.submitList(MaterialData.listaMat.filter { it.tipo == TipoMaterial.ALUMINIO })
            binding.rvDiseno.visibility = View.VISIBLE
            binding.scMedidas.visibility = View.GONE
        }

        binding.btEdit.setOnClickListener {
            binding.rvDiseno.visibility = View.GONE
            binding.scMedidas.visibility = View.VISIBLE
        }

        binding.btBaulMedida.setOnClickListener { abrirBaul() }

        binding.txTitulo.setOnClickListener { view ->
            view.animate()
                .scaleX(0.9f).scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    view.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()

            binding.lyTitulo.visibility =
                if (binding.lyTitulo.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        cuadricula()
        elementoSelecto()
        actualizarEstadoMedidaAbierta(cliente = "", nombre = "")
    }

    private fun abrirBaul() {
        val intent = Intent(this, BaulActivity::class.java).apply {
            putExtra("desde_taller", true)
        }
        lanzarBaul.launch(intent)
    }

    private fun procesarSeleccionBaul(data: Intent) {
        val cliente = data.getStringExtra("medicion_proyecto_cliente")
            ?: data.getStringExtra("rcliente")
            ?: "sin cliente"

        val nombre = data.getStringExtra("medicion_proyecto_nombre")
            ?: data.getStringExtra("medicion_proyecto_archivo")
            ?: ""

        if (nombre.isNotBlank()) {
            binding.txTitulo.text = "Templado Dise\u00f1ado con: $cliente ($nombre)"
            val detalle = construirDetalleSeleccion(data, cliente, nombre)
            actualizarEstadoMedidaAbierta(cliente, nombre, detalle)
            Toast.makeText(this, "Medida cargada desde Ba\u00fal", Toast.LENGTH_SHORT).show()
        } else {
            binding.txTitulo.text = "Templado Dise\u00f1ado con: $cliente"
            val detalle = construirDetalleSeleccion(data, cliente = "sin cliente", nombre = "")
            actualizarEstadoMedidaAbierta(cliente = "", nombre = "", detalle = detalle)
            Toast.makeText(this, "No se recibi\u00f3 medida desde Ba\u00fal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun construirDetalleSeleccion(data: Intent, cliente: String, nombre: String): String {
        val archivo = data.getStringExtra("medicion_proyecto_archivo").orEmpty()
        val ruta = data.getStringExtra("medicion_proyecto_ruta").orEmpty()
        val json = data.getStringExtra("medicion_proyecto_json").orEmpty()
        val contenido = data.getStringExtra("medicion_proyecto_contenido").orEmpty()
        val lista = data.extras?.get("lista")

        val lineas = mutableListOf<String>()
        lineas += "Abierto: ${if (nombre.isNotBlank()) nombre else "sin nombre"}"
        lineas += "Cliente: $cliente"
        if (archivo.isNotBlank()) lineas += "Archivo: $archivo"
        if (ruta.isNotBlank()) lineas += "Ruta: $ruta"
        if (json.isNotBlank()) lineas += "JSON: $json"
        if (contenido.isNotBlank()) {
            lineas += "Contenido:"
            lineas += contenido
        }
        if (lista is ArrayList<*>) lineas += "Lista: ${lista.size} elementos"

        data.extras?.keySet()
            ?.filterNot {
                it == "medicion_proyecto_nombre" ||
                    it == "medicion_proyecto_cliente" ||
                    it == "medicion_proyecto_archivo" ||
                    it == "medicion_proyecto_ruta" ||
                    it == "medicion_proyecto_json" ||
                    it == "medicion_proyecto_contenido" ||
                    it == "rcliente" ||
                    it == "lista"
            }
            ?.forEach { key ->
                val valor = data.extras?.get(key)
                lineas += "$key: ${valor ?: "null"}"
            }

        return lineas.joinToString(separator = "\n")
    }

    private fun actualizarEstadoMedidaAbierta(cliente: String, nombre: String, detalle: String? = null) {
        binding.tvMedidaAbierta.text = when {
            !detalle.isNullOrBlank() -> detalle
            nombre.isNotBlank() -> "Abierto: $nombre - $cliente"
            else -> "Sin medida abierta"
        }
    }

    private fun elementoSelecto() {
        val gruposFiltros = mapOf(
            R.id.txVidTemplado to listOf(
                binding.contenedorMedidas.lyAncho,
                binding.contenedorMedidas.lyAlto,
                binding.contenedorMedidas.lyTiradorA,
                binding.contenedorMedidas.lyTiradorD,
                binding.contenedorMedidas.lyTiradorM,
                binding.contenedorMedidas.lyAgujero
            ),
            R.id.txVidCurvo to listOf(
                binding.contenedorMedidas.lyAlto,
                binding.contenedorMedidas.lyCuerda,
                binding.contenedorMedidas.lyFlecha,
                binding.contenedorMedidas.lyDesarrollo,
                binding.contenedorMedidas.lyRadio,
                binding.contenedorMedidas.lyAngulo
            )
        )

        val todasVistas = gruposFiltros.values.flatten()

        val escuchaFiltros = View.OnClickListener { vista ->
            val mostrar = gruposFiltros[vista.id] ?: return@OnClickListener

            if (vista.tag == "activo") {
                todasVistas.forEach { it.visibility = View.GONE }
                vista.tag = null
            } else {
                todasVistas.forEach { it.visibility = View.GONE }
                mostrar.forEach { it.visibility = View.VISIBLE }
                binding.txVidTemplado.tag = null
                binding.txVidCurvo.tag = null
                vista.tag = "activo"
            }

            binding.lyTitulo.visibility = View.GONE
            animarElementoSeleccionado(vista)
        }

        binding.txVidTemplado.setOnClickListener(escuchaFiltros)
        binding.txVidCurvo.setOnClickListener(escuchaFiltros)
    }

    private fun animarElementoSeleccionado(vista: View) {
        val sombraPx = 16f * resources.displayMetrics.density
        vista.animate()
            .translationZ(sombraPx)
            .setDuration(150)
            .withEndAction {
                vista.animate()
                    .translationZ(0f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun cuadricula() {
        binding.btDibujar.setOnClickListener {
            val anchoTotal = binding.contenedorMedidas.etAncho1.text.toString().toFloatOrNull() ?: 0f
            val altoTotal = binding.contenedorMedidas.etAlto.text.toString().toFloatOrNull() ?: 0f
            val partesH = binding.contenedorMedidas.etPartesH.text.toString().toIntOrNull() ?: 1
            val partesV = binding.contenedorMedidas.etPartesV.text.toString().toIntOrNull() ?: 1
            val bastidorH = binding.contenedorMedidas.etBastidorH.text.toString().toFloatOrNull() ?: 0f
            val bastidorM = binding.contenedorMedidas.etBastidorM.text.toString().toFloatOrNull() ?: 0f
            val agujeroX = binding.contenedorMedidas.etAgujero.text.toString().toFloatOrNull() ?: 5f

            val anchosColumnas = MutableList(partesH) { anchoTotal / partesH }
            val alturasPorColumna = MutableList(partesH) { MutableList(partesV) { altoTotal / partesV } }

            binding.gridDibujo.configurarParametros(
                anchoTotal = anchoTotal,
                altoTotal = altoTotal,
                anchosColumnas = anchosColumnas,
                alturasFilasPorColumna = alturasPorColumna,
                bastidorH = bastidorH,
                bastidorM = bastidorM,
                agujeroX = agujeroX
            )

            binding.gridDibujo.invalidate()
        }
    }
}
