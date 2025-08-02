package crystal.crystal.Diseno

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import crystal.crystal.R
import crystal.crystal.casilla.MaterialData
import crystal.crystal.casilla.TipoMaterial
import crystal.crystal.databinding.ActivityDisenoBinding

class DisenoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisenoBinding
    private lateinit var adapter: MatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisenoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1) Inicializa el RecyclerView con tu adapter
        adapter = MatAdapter { /* callback si hace falta */ }
        binding.rvDiseno.apply {
            layoutManager = LinearLayoutManager(this@DisenoActivity)
            adapter = this@DisenoActivity.adapter
        }

        // 2) Botones PRODUCTOS, ALUMINIO y EDITAR
        binding.btProducto.setOnClickListener {
            adapter.submitList(
                MaterialData.listaMat.filter { it.tipo == TipoMaterial.PRODUCTOS }
            )
            binding.rvDiseno.visibility = View.VISIBLE
            binding.scMedidas.visibility = View.GONE
        }
        binding.btMaterial.setOnClickListener {
            adapter.submitList(
                MaterialData.listaMat.filter { it.tipo == TipoMaterial.ALUMINIO }
            )
            binding.rvDiseno.visibility = View.VISIBLE
            binding.scMedidas.visibility = View.GONE
        }
        binding.btEdit.setOnClickListener {
            binding.rvDiseno.visibility = View.GONE
            binding.scMedidas.visibility = View.VISIBLE
        }
        binding.txTitulo.setOnClickListener { view ->
            // Animación de “rebote”
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

            // Toggle de visibilidad
            binding.lyTitulo.visibility =
                if (binding.lyTitulo.visibility == View.VISIBLE) View.GONE
                else View.VISIBLE
        }

        // 3) Inicializa la lógica de dibujo de la cuadrícula
        cuadricula()
        elementoSelecto()
    }

    private fun elementoSelecto() {
        // 1) Mapeo local: id del TextView → lista de vistas a mostrar
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
            // … añade más grupos si los necesitas
        )

        // 2) Lista con todas las vistas que pueden ocultarse
        val todasVistas = gruposFiltros.values.flatten()

        // 3) Listener único que hace toggle, oculta lyTitulo, y anima sombra
        val escuchaFiltros = View.OnClickListener { vista ->
            val mostrar = gruposFiltros[vista.id] ?: return@OnClickListener

            // Toggle: si ya era el filtro activo, ocultamos todo
            if (vista.tag == "activo") {
                todasVistas.forEach { it.visibility = View.GONE }
                vista.tag = null
            } else {
                // Si es un filtro nuevo, ocultar todo y luego mostrar sólo este grupo
                todasVistas.forEach { it.visibility = View.GONE }
                mostrar.forEach { it.visibility = View.VISIBLE }
                // Limpiar tags de otros filtros
                binding.txVidTemplado.tag = null
                binding.txVidCurvo   .tag = null
                vista.tag = "activo"
            }

            // 4) Ocultar lyTitulo al hacer clic en cualquier filtro
            binding.lyTitulo.visibility = View.GONE

            // 5) Animar sombra en el TextView clicado
            animarElementoSeleccionado(vista)
        }

        // 4) Asignar el mismo listener a todos los TextView de filtro
        binding.txVidTemplado.setOnClickListener(escuchaFiltros)
        binding.txVidCurvo .setOnClickListener(escuchaFiltros)
        // … repite para cada TextView de filtro que tengas
    }

    private fun animarElementoSeleccionado(vista: View) {
        // Aumentamos temporalmente la translationZ para simular sombra
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
    /**
     * Configura el listener de btDibujar para leer todos los parámetros
     * y llamar a gridDibujo.configurarParametros(...)
     */
    private fun cuadricula() {
        binding.btDibujar.setOnClickListener {
            // Lectura de dimensiones y bastidores
            val anchoTotal = binding.contenedorMedidas.etAncho1.text
                .toString().toFloatOrNull() ?: 0f
            val altoTotal  = binding.contenedorMedidas.etAlto.text
                .toString().toFloatOrNull() ?: 0f
            val partesH    = binding.contenedorMedidas.etPartesH.text
                .toString().toIntOrNull()   ?: 1
            val partesV    = binding.contenedorMedidas.etPartesV.text
                .toString().toIntOrNull()   ?: 1
            val bastidorH  = binding.contenedorMedidas.etBastidorH.text
                .toString().toFloatOrNull() ?: 0f
            val bastidorM  = binding.contenedorMedidas.etBastidorM.text
                .toString().toFloatOrNull() ?: 0f

            // NUEVO: posición horizontal del agujero en cm
            val agujeroX   = binding.contenedorMedidas.etAgujero.text
                .toString().toFloatOrNull() ?: 5f

            // Generamos lista de anchos y alturas
            val anchosColumnas = MutableList(partesH) { anchoTotal / partesH }
            val alturasPorColumna = MutableList(partesH) {
                MutableList(partesV) { altoTotal / partesV }
            }

            // Configuramos la vista de cuadrícula
            binding.gridDibujo.configurarParametros(
                anchoTotal             = anchoTotal,
                altoTotal              = altoTotal,
                anchosColumnas         = anchosColumnas,
                alturasFilasPorColumna = alturasPorColumna,
                bastidorH              = bastidorH,
                bastidorM              = bastidorM,
                agujeroX               = agujeroX
            )

            // Forzamos el redraw
            binding.gridDibujo.invalidate()
        }
    }
}




