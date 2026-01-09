package crystal.crystal.taller.puerta

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import crystal.crystal.Diseno.DisenoActivity
import crystal.crystal.R
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityPuertaPanoBinding
import crystal.crystal.taller.nova.NovaUIHelper.esValido
import crystal.crystal.taller.puerta.datos.PuertaRepositorio
import crystal.crystal.taller.puerta.dibujo.DibujoPuerta
import crystal.crystal.taller.puerta.logica.CalculosPuerta
import crystal.crystal.taller.puerta.logica.PlanoRotado
import crystal.crystal.taller.puerta.modelos.Puerta
import crystal.crystal.taller.puerta.modelos.Variante
import crystal.crystal.taller.puerta.ui.VariantesAdapter

class PuertasActivity : AppCompatActivity() {

    // Constantes (conservadas del original)
    private val hojaRef = CalculosPuerta.HOJA_REF
    private val marco = CalculosPuerta.MARCO
    private val bastidor = CalculosPuerta.BASTIDOR
    private val unoMedio = CalculosPuerta.UNO_MEDIO

    // Estado
    private var clienteActual: String = ""
    private var indicePuerta = 0
    private var puertaActual: Puerta? = null
    private var varianteSeleccionada: String = "Mari h"

    private lateinit var binding: ActivityPuertaPanoBinding
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback

    // Mapa de listas (persistido por proyecto)
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()

    // Variable para controlar el primer click en archivar
    private var primerClickArchivarRealizado = false

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuertaPanoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ==================== Sistema de Proyectos ====================
        ProyectoManager.inicializarDesdeStorage(this)
        proyectoCallback = ProyectoUIHelper.crearCallbackConActualizacionUI(
            context = this,
            textViewProyecto = binding.tvProyectoActivo,
            activity = this
        )
        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoGestionProyectos(this, proyectoCallback)
        }
        procesarIntentProyecto(intent)

        // ==================== Inicialización UI ====================
        indicePuerta = 0
        inicializarClienteYTipos()
        configurarListenersUI()
        mostrarVariantes()
    }

    // ---------------------- Menú Proyecto ----------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let { ProyectoUIHelper.agregarOpcionesMenuProyecto(it) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val manejado = ProyectoUIHelper.manejarSeleccionMenu(
            context = this,
            itemId = item.itemId,
            callback = proyectoCallback
        ) {
            ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
        }
        return if (manejado) true else super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        procesarIntentProyecto(intent)
    }

    override fun onResume() {
        super.onResume()
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
    }

    private fun procesarIntentProyecto(intent: Intent) {
        val nombreProyecto = intent.getStringExtra("proyecto_nombre")
        val crearNuevo = intent.getBooleanExtra("crear_proyecto", false)
        val descripcionProyecto = intent.getStringExtra("proyecto_descripcion") ?: ""
        if (crearNuevo && !nombreProyecto.isNullOrEmpty()) {
            if (MapStorage.crearProyecto(this, nombreProyecto, descripcionProyecto)) {
                ProyectoManager.setProyectoActivo(this, nombreProyecto)
                ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
                Toast.makeText(this, "Proyecto '$nombreProyecto' creado y activado", Toast.LENGTH_SHORT).show()
            }
        } else if (!nombreProyecto.isNullOrEmpty()) {
            if (MapStorage.existeProyecto(this, nombreProyecto)) {
                ProyectoManager.setProyectoActivo(this, nombreProyecto)
                ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
                Toast.makeText(this, "Proyecto '$nombreProyecto' activado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ---------------------- Inicialización de cliente y puerta ----------------------
    @SuppressLint("SetTextI18n")
    private fun inicializarClienteYTipos() {
        binding.lyCliente.visibility = View.GONE
        intent.extras?.getString("rcliente")?.let { clienteActual = it }
        actualizarPuertaYTitulo()
        binding.tvTitulo.setOnClickListener {
            binding.lyCliente.visibility = View.VISIBLE
            binding.clienteEditxt.setText(clienteActual)
            binding.btGo.setOnClickListener {
                clienteActual = binding.clienteEditxt.text.toString()
                actualizarPuertaYTitulo()
                binding.lyCliente.visibility = View.GONE
                // Actualizar el TextView del cliente para archivado
                binding.txCliente.text = clienteActual
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarPuertaYTitulo() {
        val lista = PuertaRepositorio.listaPuertas
        if (lista.isNotEmpty()) {
            puertaActual = lista[indicePuerta]
            val imagen = when (puertaActual?.nombre) {
                "Mari" -> R.drawable.ic_pp2
                "Dora" -> R.drawable.pdora
                "Adel" -> R.drawable.padelina
                "Mili" -> R.drawable.pmili
                "jeny" -> R.drawable.pjenny
                "Taly" -> R.drawable.pthalia
                "Viky" -> R.drawable.pvicky
                "Lina" -> R.drawable.pjalina
                "Tere" -> R.drawable.ptere
                else -> R.drawable.pjenny
            }
            binding.ivModelo.setImageResource(imagen)
            binding.tvTitulo.text = "Puerta ${puertaActual?.nombre}${if (clienteActual.isNotEmpty()) " ($clienteActual)" else ""}"
        }
        actualizarVisibilidades()
    }

    private fun actualizarVisibilidades() {
        val nombre = puertaActual?.nombre ?: ""
        binding.lyAD.visibility = if (nombre == "Viky" || nombre == "Adel") View.VISIBLE else View.GONE
    }

    // ---------------------- Listeners de UI ----------------------
    private fun configurarListenersUI() {
        binding.ivModelo.setOnClickListener {
            val lista = PuertaRepositorio.listaPuertas
            if (lista.isNotEmpty()) {
                indicePuerta = (indicePuerta + 1) % lista.size
                actualizarPuertaYTitulo()
                renderizarModeloActual() // refresco visual
            }
        }

        binding.ivModelo.setOnLongClickListener {
            val intent = Intent(this, DisenoActivity::class.java)
            startActivity(intent)
            true
        }

        binding.btCalcular.setOnClickListener {
            ejecutarCalculoCompleto()
        }

        binding.btArchivar.setOnClickListener {
            // Validar que se hayan ingresado nuevos datos antes de archivar
            if (binding.etMed1.text.toString().isEmpty() || binding.etMed1.text.toString() == "") {
                Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!primerClickArchivarRealizado) {
                // Primera vez - mostrar diálogo
                DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                    override fun onProyectoSeleccionado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true

                        // CARGAR Map existente del proyecto seleccionado
                        val mapExistente = MapStorage.cargarProyecto(this@PuertasActivity, nombreProyecto)
                        if (mapExistente != null) {
                            mapListas.clear()
                            mapListas.putAll(mapExistente)
                        }

                        archivarMapas()
                        ProyectoUIHelper.actualizarVisorProyectoActivo(this@PuertasActivity, binding.tvProyectoActivo)
                        Toast.makeText(this@PuertasActivity, "Archivado", Toast.LENGTH_SHORT).show()
                        binding.etMed1.setText("")
                        binding.etMed2.setText("")
                    }

                    override fun onProyectoCreado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true

                        // Para proyecto nuevo, Map inicia vacío (correcto)
                        archivarMapas()
                        ProyectoUIHelper.actualizarVisorProyectoActivo(this@PuertasActivity, binding.tvProyectoActivo)
                        Toast.makeText(this@PuertasActivity, "Archivado", Toast.LENGTH_SHORT).show()
                        binding.etMed1.setText("")
                        binding.etMed2.setText("")
                    }

                    override fun onProyectoEliminado(nombreProyecto: String) {
                        ProyectoUIHelper.actualizarVisorProyectoActivo(this@PuertasActivity, binding.tvProyectoActivo)
                    }
                })
            } else {
                // Verificar que hay proyecto activo antes de archivar directamente
                if (!ProyectoManager.hayProyectoActivo()) {
                    // Si no hay proyecto, resetear y mostrar diálogo de nuevo
                    primerClickArchivarRealizado = false
                    Toast.makeText(this, "No hay proyecto activo. Selecciona uno.", Toast.LENGTH_SHORT).show()

                    // Volver a mostrar diálogo
                    DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                        override fun onProyectoSeleccionado(nombreProyecto: String) {
                            primerClickArchivarRealizado = true

                            // CARGAR Map existente del proyecto seleccionado
                            val mapExistente = MapStorage.cargarProyecto(this@PuertasActivity, nombreProyecto)
                            if (mapExistente != null) {
                                mapListas.clear()
                                mapListas.putAll(mapExistente)
                            }

                            archivarMapas()
                            ProyectoUIHelper.actualizarVisorProyectoActivo(this@PuertasActivity, binding.tvProyectoActivo)
                            Toast.makeText(this@PuertasActivity, "Archivado", Toast.LENGTH_SHORT).show()
                            binding.etMed1.setText("")
                            binding.etMed2.setText("")
                        }

                        override fun onProyectoCreado(nombreProyecto: String) {
                            primerClickArchivarRealizado = true

                            archivarMapas()
                            ProyectoUIHelper.actualizarVisorProyectoActivo(this@PuertasActivity, binding.tvProyectoActivo)
                            Toast.makeText(this@PuertasActivity, "Archivado", Toast.LENGTH_SHORT).show()
                            binding.etMed1.setText("")
                            binding.etMed2.setText("")
                        }

                        override fun onProyectoEliminado(nombreProyecto: String) {
                            ProyectoUIHelper.actualizarVisorProyectoActivo(this@PuertasActivity, binding.tvProyectoActivo)
                        }
                    })
                    return@setOnClickListener
                }

                // Ya hay proyecto activo y no es la primera vez
                // CARGAR Map del proyecto activo actual antes de archivar
                val proyectoActivo = ProyectoManager.getProyectoActivo()
                if (proyectoActivo != null) {
                    val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
                    mapListas.clear()
                    if (mapExistente != null) {
                        mapListas.putAll(mapExistente)
                    }
                }

                archivarMapas()
                Toast.makeText(this, "Archivado", Toast.LENGTH_SHORT).show()
                binding.etMed1.setText("")
                binding.etMed2.setText("")
            }
        }

        binding.btArchivar.setOnLongClickListener {
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnLongClickListener true
            MapStorage.guardarMap(this, mapListas)
            Toast.makeText(this, "Map guardado en proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()
            ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
            true
        }
    }

    // ---------------------- Calcular y renderizar ----------------------
    @SuppressLint("SetTextI18n")
    private fun ejecutarCalculoCompleto() {
        try {
            // Entradas
            val ancho = binding.etMed1.text.toString().toFloat()
            val alto = binding.etMed2.text.toString().toFloat()
            val nZocalos = binding.etZocalo.text.toString().toIntOrNull() ?: 0
            val nDiv = binding.etDivi.text.toString().toIntOrNull() ?: 0
            val junki = binding.etJunki.text.toString().toFloatOrNull() ?: 0f
            val piso = binding.etPiso.text.toString().toFloatOrNull() ?: 0f
            val hHoja = binding.etHoja.text.toString().toFloatOrNull() ?: 0f
            val angulo = binding.etAngulo.text.toString().toFloatOrNull() ?: 0f

            // Cálculos base
            val hPuente = CalculosPuerta.hPuente(alto, hHoja, piso, hojaRef, marco)
            val mocheta = CalculosPuerta.mocheta(alto, hPuente, marco)
            val marcoSup = CalculosPuerta.marcoSuperior(ancho, marco)
            val tubo = CalculosPuerta.tubo(ancho, marco, mocheta)
            val paflon = CalculosPuerta.paflon(ancho, marco, bastidor)
            val parante = CalculosPuerta.parante(hPuente, piso)
            val nZ = CalculosPuerta.nZocalo(nZocalos)
            val paranteInt = CalculosPuerta.paranteInterno(parante, nZ, bastidor)
            val divisTam = CalculosPuerta.divisiones(parante, nZ, nDiv.toFloat(), bastidor)
            val nPfvcal = CalculosPuerta.nPfvcal(nDiv)
            val nPaflones = CalculosPuerta.nPaflones(nDiv, nZocalos)
            val zocalo = CalculosPuerta.zocalo(nZ, bastidor)

            // Salidas UI (materiales)
            binding.tvMarco.text = "${CalculosPuerta.df1(alto)} = 2\n${CalculosPuerta.df1(marcoSup)} = 1"
            binding.tvTope.text = "${CalculosPuerta.df1(marcoSup)} = 1\n${CalculosPuerta.df1(hPuente)} = 2"
            binding.tvTubo.text = if (tubo.isEmpty()) "" else "$tubo = 1"

            // Paflones según variante
            val textoPlanoAgrupado = prepararPlanoRotadoYResumen(paflon, paranteInt, nDiv, bastidor, angulo)
            binding.tvPaflon.text = when (varianteSeleccionada) {
                "Mari h" -> "${CalculosPuerta.df1(paflon)} = $nPaflones\n${CalculosPuerta.df1(parante)} = 2"
                "Mari v" -> "${CalculosPuerta.df1(paflon)} = ${nZ}\n${CalculosPuerta.df1(parante)} = 2\n${CalculosPuerta.df1(paranteInt)} = ${nDiv - 1}"
                "Mari d" -> "${CalculosPuerta.df1(paflon)} = ${nZ + 1}\n${textoPlanoAgrupado}\n${CalculosPuerta.df1(parante)} = 2"
                else -> "${CalculosPuerta.df1(paflon)} = $nPaflones\n${CalculosPuerta.df1(parante)} = 2"
            }

            // Junkillos y vidrios
            binding.tvJunki.text = CalculosPuerta.textoJunkillos(varianteSeleccionada, junki, mocheta, nPfvcal, paflon, bastidor, nDiv, paranteInt, marcoSup)
            binding.tvVidrios.text = CalculosPuerta.textoVidrios(varianteSeleccionada, junki, paflon, divisTam, bastidor, nDiv, paranteInt, marcoSup, mocheta)

            // Referencias
            binding.txRefe.text = CalculosPuerta.referen(ancho, alto, hPuente, mocheta)
            binding.lyTubo.visibility = if (tubo.isEmpty()) View.GONE else View.VISIBLE

            // Texto ensayo de paños (como original)
            binding.tvEnsayo.text = CalculosPuerta.textoPanos(zocalo, nPfvcal, divisTam, bastidor)
            binding.tvEnsayo2.text = CalculosPuerta.textoResumenArray(
                alto,
                marcoSup,
                tubo,
                paflon,
                parante,
                zocalo,
                nDiv,
                hPuente,
                CalculosPuerta.partesV(paflon, unoMedio),
                CalculosPuerta.parteH(divisTam, unoMedio),
                CalculosPuerta.vidrioM(marcoSup, mocheta, junki)
            )

            // Actualizar el TextView del cliente con el valor actual
            binding.txCliente.text = clienteActual

            // Dibujo
            renderizarModeloActual()

        } catch (e: Exception) {
            Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderizarModeloActual() {
        val anchoPuertaCm = binding.etMed1.text.toString().toFloatOrNull() ?: return
        val altoPuertaCm = binding.etMed2.text.toString().toFloatOrNull() ?: return
        val nZocalos = binding.etZocalo.text.toString().toIntOrNull() ?: 0
        val nDiv = binding.etDivi.text.toString().toIntOrNull() ?: 0
        val tipoDivision = when (varianteSeleccionada) {
            "Mari v" -> "V"
            "Mari d" -> "D"
            else -> "H"
        }

        // Hoja
        val anchoHojaCm = anchoPuertaCm - ((marco * 2) + 1)
        val altoHojaCm = CalculosPuerta.hPuente(
            altoPuertaCm,
            binding.etHoja.text.toString().toFloatOrNull() ?: 0f,
            binding.etPiso.text.toString().toFloatOrNull() ?: 0f,
            hojaRef,
            marco
        )

        val anchoContenedor = anchoPuertaCm * 3
        val altoContenedor = altoPuertaCm * 3
        val angulo = binding.etAngulo.text.toString().toFloatOrNull() ?: 0f

        val bmp: Bitmap = DibujoPuerta.generarBitmapPuerta(
            context = this,
            anchoPuertaCm = anchoPuertaCm,
            altoPuertaCm = altoPuertaCm,
            anchoHojaCm = anchoHojaCm,
            altoHojaCm = altoHojaCm,
            numeroZocalos = nZocalos,
            numeroDivisiones = nDiv,
            anchoContenedor = anchoContenedor,
            altoContenedor = altoContenedor,
            tipoDivision = tipoDivision,
            anguloGrados = angulo
        )
        binding.ivModelo.setImageBitmap(bmp)
        DibujoPuerta.guardarBitmapEnCache(this, bmp)
    }

    private fun prepararPlanoRotadoYResumen(
        paflon: Float,
        paranteInterno: Float,
        nDiv: Int,
        bastidor: Float,
        angulo: Float
    ): String {
        val datos = PlanoRotado.generarDatosPlano(paflon, paranteInterno, nDiv, bastidor)
        val rotado = PlanoRotado.rotarDatosPlano(datos, angulo, paflon, paranteInterno)
        val texto = PlanoRotado.obtenerTextoDistancias(rotado)
        return PlanoRotado.agruparMedidasDesdeTexto(texto)
    }

    // ---------------------- Variantes ----------------------
    private fun mostrarVariantes() {
        binding.txMedCant.setOnClickListener {
            binding.lyVariantes.visibility = View.VISIBLE
            val nombrePuerta = puertaActual?.nombre ?: ""
            val variantes = when (nombrePuerta) {
                "Mari" -> listOf(
                    Variante("Mari h", R.drawable.ic_pp2),
                    Variante("Mari v", R.drawable.mariv),
                    Variante("Mari d", R.drawable.marid)
                )
                "Adel" -> listOf(
                    Variante("Adel p1", R.drawable.padelina),
                    Variante("Adel v", R.drawable.padelinz),
                    Variante("Adel p2", R.drawable.pvicky),
                    Variante("Adel p3", R.drawable.padelinx)
                )
                "Mili" -> listOf(Variante("Variante Única", R.drawable.pvicky))
                "Viky" -> listOf(Variante("Variante Única", R.drawable.pvicky))
                else -> listOf(
                    Variante("Error", R.drawable.ic_dormido),
                    Variante("Error 2", R.drawable.peligro2),
                    Variante("Eliminar", R.drawable.eliminar)
                )
            }
            binding.rvVariantes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.rvVariantes.adapter = VariantesAdapter(variantes) { v ->
                varianteSeleccionada = v.nombre
                binding.ivModelo.setImageResource(v.imagen)
                binding.lyVariantes.visibility = View.GONE
            }
        }
        binding.lyVariantes.setOnClickListener { binding.lyVariantes.visibility = View.GONE }
    }

    // ==================== FUNCIONES PARA ARCHIVAR ====================
    private fun obtenerPrefijo(): String {
        return "P" // Puertas - prefijo para identificar cálculos de puertas
    }

    private fun archivarMapas() {
        // ✅ SIEMPRE limpiar y cargar el Map del proyecto activo actual
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
            mapListas.clear() // Limpiar datos anteriores
            if (mapExistente != null) {
                mapListas.putAll(mapExistente) // Cargar datos del proyecto correcto
            }
            // Si mapExistente es null, mapListas queda vacío (correcto para proyecto nuevo)
        }

        val prefijo = obtenerPrefijo()
        val siguienteNumero = ProyectoManager.obtenerSiguienteContadorPorPrefijo(this, prefijo)
        val identificadorPaquete = "p${siguienteNumero}${prefijo}"

        // Archivar cada campo si está visible y válido
        if (esValido(binding.lyClienteData)) {
            ListaCasilla.procesarArchivarConPrefijo(this, binding.tvCliente, binding.txCliente, mapListas, identificadorPaquete)
        }
        if (esValido(binding.lyMed1)) {
            ListaCasilla.procesarArchivarConPrefijo(this, binding.txMed1, binding.etMed1, mapListas, identificadorPaquete)
        }
        if (esValido(binding.lyAlto)) {
            ListaCasilla.procesarArchivarConPrefijo(this, binding.txMed2, binding.etMed2, mapListas, identificadorPaquete)
        }
        if (esValido(binding.lyReferencias)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvReferencias, binding.txRefe, mapListas, identificadorPaquete)
        }
        if (esValido(binding.lyMarco)) {
            ListaCasilla.procesarArchivarConPrefijo(this, binding.txMarco, binding.tvMarco, mapListas, identificadorPaquete)
        }
        if (esValido(binding.lyTubo)) {
            ListaCasilla.procesarArchivarConPrefijo(this, binding.txTubo, binding.tvTubo, mapListas, identificadorPaquete)
        }
        if (esValido(binding.lyPaflon)) {
            ListaCasilla.procesarArchivarConPrefijo(this, binding.txPaflon, binding.tvPaflon, mapListas, identificadorPaquete)
        }
        if (esValido(binding.lyJunki)) {
            ListaCasilla.procesarArchivarConPrefijo(this, binding.txJunki, binding.tvJunki, mapListas, identificadorPaquete)
        }
        if (esValido(binding.lyTope)) {
            ListaCasilla.procesarArchivarConPrefijo(this, binding.txTope, binding.tvTope, mapListas, identificadorPaquete)
        }
        if (esValido(binding.lyVidrios)) {
            ListaCasilla.procesarArchivarConPrefijo(this, binding.txVidrios, binding.tvVidrios, mapListas, identificadorPaquete)
        }

        ProyectoManager.actualizarContadorPorPrefijo(this, prefijo, siguienteNumero)
        MapStorage.guardarMap(this, mapListas)
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)

        Toast.makeText(this, "Datos archivados como $identificadorPaquete en proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()
    }
}