package crystal.crystal.taller.puerta

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import crystal.crystal.taller.MedidaActivity
import crystal.crystal.R
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityPuertaPanoBinding
import crystal.crystal.taller.ModoMasivoHelper
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
    // Tipo de ventana seleccionado desde el panel de formas
    private var tipoVentanaPanel: String = ""
    // Conexión a ventana en costados
    private var ventanaIzquierda: Boolean = false
    private var ventanaDerecha: Boolean = false

    // Metadatos de producción (color aluminio / tipo vidrio)
    private var metaColorAluminio: String = ""
    private var metaTipoVidrio: String = ""

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
        ProyectoUIHelper.configurarVisorProyectoActivo(this, binding.tvProyectoActivo)
        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoGestionProyectos(this, proyectoCallback)
        }
        procesarIntentProyecto(intent)

        // ==================== Inicialización UI ====================
        indicePuerta = 0
        inicializarClienteYTipos()
        configurarListenersUI()
        mostrarVariantes()

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etMed1.setText(it.toString()) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etMed2.setText(it.toString()) }
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
            // Sincronizar varianteSeleccionada con la primera variante del nuevo modelo
            val primeraVariante = variantesParaPuerta(puertaActual?.nombre ?: "").firstOrNull()
            if (primeraVariante != null) varianteSeleccionada = primeraVariante.nombre
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
        binding.btModeloPrev.setOnClickListener {
            val lista = PuertaRepositorio.listaPuertas
            if (lista.isNotEmpty()) {
                indicePuerta = (indicePuerta - 1 + lista.size) % lista.size
                actualizarPuertaYTitulo()
                renderizarModeloActual()
            }
        }

        binding.btModeloNext.setOnClickListener {
            val lista = PuertaRepositorio.listaPuertas
            if (lista.isNotEmpty()) {
                indicePuerta = (indicePuerta + 1) % lista.size
                actualizarPuertaYTitulo()
                renderizarModeloActual()
            }
        }

        binding.ivModelo.setOnClickListener {
            abrirDialogoVariantes()
        }

        binding.ivModelo.setOnLongClickListener {
            val intent = Intent(this, MedidaActivity::class.java)
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

                        mostrarDialogoMetadatosProduccion {
                            archivarMapas()
                            binding.etMed1.setText("")
                            binding.etMed2.setText("")
                        }
                    }

                    override fun onProyectoCreado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true

                        mostrarDialogoMetadatosProduccion {
                            archivarMapas()
                            binding.etMed1.setText("")
                            binding.etMed2.setText("")
                        }
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

                            mostrarDialogoMetadatosProduccion {
                                archivarMapas()
                                binding.etMed1.setText("")
                                binding.etMed2.setText("")
                            }
                        }

                        override fun onProyectoCreado(nombreProyecto: String) {
                            primerClickArchivarRealizado = true

                            mostrarDialogoMetadatosProduccion {
                                archivarMapas()
                                binding.etMed1.setText("")
                                binding.etMed2.setText("")
                            }
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

                mostrarDialogoMetadatosProduccion {
                    archivarMapas()
                    binding.etMed1.setText("")
                    binding.etMed2.setText("")
                }
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

            // Marco lateral según conexión a ventana (canal=2.2, tubo=2.5)
            val tuboW = 2.5f
            val marcoIzq = if (ventanaIzquierda) tuboW else marco
            val marcoDer = if (ventanaDerecha) tuboW else marco
            val totalMarco = marcoIzq + marcoDer

            // Cálculos base
            val hPuente = CalculosPuerta.hPuente(alto, hHoja, piso, hojaRef, marco)
            val mocheta = CalculosPuerta.mocheta(alto, hPuente, marco)
            val marcoSup = ancho - totalMarco
            val tubo = if (mocheta > 0f) CalculosPuerta.df1(marcoSup) else ""
            val paflon = ((ancho - totalMarco) - 1f) - (2f * bastidor)
            val parante = CalculosPuerta.parante(hPuente, piso)
            val nZ = CalculosPuerta.nZocalo(nZocalos)
            val paranteInt = CalculosPuerta.paranteInterno(parante, nZ, bastidor)
            val divisTam = CalculosPuerta.divisiones(parante, nZ, nDiv.toFloat(), bastidor)
            val nPfvcal = CalculosPuerta.nPfvcal(nDiv)
            val nPaflones = CalculosPuerta.nPaflones(nDiv, nZocalos)
            val zocalo = CalculosPuerta.zocalo(nZ, bastidor)

            // Salidas UI (materiales)
            val countMarcos = (if (!ventanaIzquierda) 1 else 0) + (if (!ventanaDerecha) 1 else 0)
            binding.tvMarco.text = if (countMarcos > 0)
                "${CalculosPuerta.df1(alto)} = $countMarcos\n${CalculosPuerta.df1(marcoSup)} = 1"
            else
                "${CalculosPuerta.df1(marcoSup)} = 1"
            binding.tvTope.text = "${CalculosPuerta.df1(marcoSup)} = 1\n${CalculosPuerta.df1(hPuente)} = 2"
            val countTubos = (if (ventanaIzquierda) 1 else 0) + (if (ventanaDerecha) 1 else 0)
            binding.tvTubo.text = buildString {
                if (tubo.isNotEmpty()) append("$tubo = 1")
                if (countTubos > 0) {
                    if (tubo.isNotEmpty()) append("\n")
                    append("${CalculosPuerta.df1(alto)} = $countTubos")
                }
            }

            // Paflones según variante
            val textoPlanoAgrupado = prepararPlanoRotadoYResumen(paflon, paranteInt, nDiv, bastidor, angulo)
            binding.tvPaflon.text = deduplicar(when (varianteSeleccionada) {
                "Mari h" -> "${CalculosPuerta.df1(paflon)} = $nPaflones\n${CalculosPuerta.df1(parante)} = 2"
                "Mari v" -> "${CalculosPuerta.df1(paflon)} = ${nZ}\n${CalculosPuerta.df1(parante)} = 2\n${CalculosPuerta.df1(paranteInt)} = ${nDiv - 1}"
                "Mari d" -> "${CalculosPuerta.df1(paflon)} = ${nZ + 1}\n${textoPlanoAgrupado}\n${CalculosPuerta.df1(parante)} = 2"
                "Taly h", "Taly d" -> {
                    // Pares de paflones laterales hasta vacío ≤ 20 cm
                    var gapTaly = paflon
                    var paresTaly = 0
                    while (gapTaly > 20f && gapTaly >= bastidor * 2f) { paresTaly++; gapTaly -= 2f * bastidor }
                    val altInterno = hPuente - 2f * bastidor
                    val nDivisores = maxOf(0, nDiv - 1)
                    buildString {
                        append("${CalculosPuerta.df1(parante)} = 2\n")
                        append("${CalculosPuerta.df1(paflon)} = 2")
                        if (paresTaly > 0) append("\n${CalculosPuerta.df1(altInterno)} = ${paresTaly * 2}")
                        append("\n${CalculosPuerta.df1(gapTaly)} = 2")  // topInner + botInner
                        if (nDivisores > 0) {
                            val divLen = if (varianteSeleccionada == "Taly d" && angulo != 0f) {
                                val rad = Math.toRadians(angulo.toDouble())
                                // Barra cortada vertical: longitud = (gap + bastidor*sin) / cos
                                val rawLen = (gapTaly / Math.cos(rad) + bastidor * Math.tan(rad)).toFloat()
                                val ceiled = kotlin.math.ceil(rawLen * 10).toInt() / 10f
                                if (ceiled == ceiled.toLong().toFloat()) ceiled.toLong().toString()
                                else "%.1f".format(ceiled).replace(",", ".")
                            } else {
                                CalculosPuerta.df1(gapTaly)
                            }
                            append("\n$divLen = $nDivisores")
                        }
                    }
                }
                else -> "${CalculosPuerta.df1(paflon)} = $nPaflones\n${CalculosPuerta.df1(parante)} = 2"
            })

            // Junkillos y vidrios
            binding.tvJunki.text = if (varianteSeleccionada == "Taly h" || varianteSeleccionada == "Taly d") {
                var gapTalyJ = paflon
                while (gapTalyJ > 20f && gapTalyJ >= bastidor * 2f) { gapTalyJ -= 2f * bastidor }
                val zoneHcm = hPuente - 4f * bastidor
                val barrasJ = maxOf(0, nDiv - 1)
                val gapSeccion = if (nDiv > 0) (zoneHcm - barrasJ * bastidor) / nDiv else zoneHcm

                fun ceilFmt(v: Double): String {
                    val c = kotlin.math.ceil(v * 10).toInt() / 10f
                    return if (c == c.toLong().toFloat()) c.toLong().toString()
                    else "%.1f".format(c).replace(",", ".")
                }

                buildString {
                    if (varianteSeleccionada == "Taly h") {
                        // Horizontales (top/bot de cada sección), sin ángulo
                        append("${CalculosPuerta.df1(gapTalyJ - 2f * junki)} = ${nDiv * 2}\n")
                        // Parantes de cada sección
                        append("${CalculosPuerta.df1(gapSeccion - 2f * junki)} = ${nDiv * 2}")
                    } else { // Taly d
                        val rad = Math.toRadians(angulo.toDouble())
                        val extraJ = junki.toDouble() * Math.tan(rad)
                        // Horizontales top/bot (sin ángulo), solo 2 (extremos del vacío)
                        append("${CalculosPuerta.df1(gapTalyJ - 2f * junki)} = 2\n")
                        // Diagonales a lo largo de los divisores: (nDiv-1) divisores × 2 caras
                        if (nDiv > 1) {
                            val diagJ = gapTalyJ / Math.cos(rad) + extraJ
                            append("${ceilFmt(diagJ)} = ${(nDiv - 1) * 2}\n")
                        }
                        // Parantes extremos: 2 largos (contra el bastidor) + resto con adición tangencial
                        append("${CalculosPuerta.df1(gapSeccion - 2f * junki)} = 2\n")
                        // Parantes cortos (extremos interiores) + ambos lados de secciones medias
                        val countConExtra = nDiv * 2 - 2
                        if (countConExtra > 0) {
                            append("${ceilFmt(gapSeccion - 2.0 * junki + extraJ)} = $countConExtra")
                        }
                    }
                    // Mocheta
                    if (mocheta > 0f) {
                        if (isNotEmpty()) append("\n")
                        append("${CalculosPuerta.df1(marcoSup)} = 2\n")
                        append("${CalculosPuerta.df1(mocheta - 2f * junki)} = 2")
                    }
                }
            } else {
                CalculosPuerta.textoJunkillos(varianteSeleccionada, junki, mocheta, nPfvcal, paflon, bastidor, nDiv, paranteInt, marcoSup)
            }
            binding.tvVidrios.text = if (varianteSeleccionada == "Taly h" || varianteSeleccionada == "Taly d") {
                var gapTalyV = paflon
                while (gapTalyV > 20f && gapTalyV >= bastidor * 2f) { gapTalyV -= 2f * bastidor }
                val zoneHv = hPuente - 4f * bastidor
                val barrasV = maxOf(0, nDiv - 1)
                val gapSeccionV = if (nDiv > 0) (zoneHv - barrasV * bastidor) / nDiv else zoneHv
                val holgura = 0.5f
                val anchVf = gapTalyV - 2f * junki - holgura
                val altVBase = gapSeccionV - 2f * junki - holgura
                val altVf = if (varianteSeleccionada == "Taly d" && angulo != 0f) {
                    val rad = Math.toRadians(angulo.toDouble())
                    altVBase + anchVf * Math.tan(rad).toFloat()
                } else altVBase
                "${CalculosPuerta.df1(anchVf)} x ${CalculosPuerta.df1(altVf)} = $nDiv"
            } else {
                CalculosPuerta.textoVidrios(varianteSeleccionada, junki, paflon, divisTam, bastidor, nDiv, paranteInt, marcoSup, mocheta)
            }

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
        // Solo los modelos con renderizado implementado generan bitmap
        val nombreModelo = puertaActual?.nombre ?: return
        if (nombreModelo != "Mari" && nombreModelo != "Taly") return
        val anchoPuertaCm = binding.etMed1.text.toString().toFloatOrNull() ?: return
        val altoPuertaCm = binding.etMed2.text.toString().toFloatOrNull() ?: return
        val nZocalos = binding.etZocalo.text.toString().toIntOrNull() ?: 0
        val nDiv = binding.etDivi.text.toString().toIntOrNull() ?: 0
        val tipoDivision = when (varianteSeleccionada) {
            "Mari v" -> "V"
            "Mari d" -> "D"
            else -> "H"
        }

        // Hoja — ancho depende del tipo de marco lateral (canal 2.2 o tubo 2.5)
        val marcoIzqRender = if (ventanaIzquierda) 2.5f else marco
        val marcoDerRender = if (ventanaDerecha) 2.5f else marco
        val anchoHojaCm = anchoPuertaCm - (marcoIzqRender + marcoDerRender + 1f)
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

        val bmp: Bitmap = if (varianteSeleccionada.startsWith("Taly")) {
            DibujoPuerta.generarBitmapTaly(
                context = this,
                anchoPuertaCm = anchoPuertaCm,
                altoPuertaCm = altoPuertaCm,
                anchoHojaCm = anchoHojaCm,
                altoHojaCm = altoHojaCm,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                numeroDivisiones = nDiv,
                anguloGrados = if (varianteSeleccionada == "Taly d") angulo else 0f,
                marcoCmIzq = marcoIzqRender,
                marcoCmDer = marcoDerRender
            )
        } else {
            DibujoPuerta.generarBitmapPuerta(
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
                anguloGrados = angulo,
                marcoCmIzq = marcoIzqRender,
                marcoCmDer = marcoDerRender
            )
        }
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
    private fun variantesParaPuerta(nombrePuerta: String): List<Variante> = when (nombrePuerta) {
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
        "Taly" -> listOf(
            Variante("Taly h", R.drawable.pthalia),
            Variante("Taly d", R.drawable.ptalyd)
        )
        "Mili" -> listOf(Variante("Variante Única", R.drawable.pvicky))
        "Viky" -> listOf(Variante("Variante Única", R.drawable.pvicky))
        else -> emptyList()
    }

    private fun abrirDialogoVariantes() {
        val variantes = variantesParaPuerta(puertaActual?.nombre ?: "")
        if (variantes.isEmpty()) return

        val dp36 = (36 * resources.displayMetrics.density).toInt()

        // Fila: [cbIzq] [puertaventa] [cbDer] — máx 36dp de alto
        val filaVentana = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(24, 4, 24, 4)
        }
        val cbIzq = android.widget.CheckBox(this).apply { text = "Izq"; isChecked = ventanaIzquierda }
        val ivVentana = android.widget.ImageView(this).apply {
            setImageResource(R.drawable.puertaventa)
            layoutParams = android.widget.LinearLayout.LayoutParams(0, dp36, 1f)
            scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
        }
        val cbDer = android.widget.CheckBox(this).apply { text = "Der"; isChecked = ventanaDerecha }
        filaVentana.addView(cbIzq)
        filaVentana.addView(ivVentana)
        filaVentana.addView(cbDer)

        val rv = androidx.recyclerview.widget.RecyclerView(this).apply {
            layoutManager = GridLayoutManager(this@PuertasActivity, 2)
            setPadding(16, 8, 16, 16)
        }
        val contenedor = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            addView(filaVentana)
            addView(rv)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Variantes — ${puertaActual?.nombre}")
            .setView(contenedor)
            .setPositiveButton("OK") { _, _ ->
                ventanaIzquierda = cbIzq.isChecked
                ventanaDerecha = cbDer.isChecked
            }
            .setNegativeButton("Cerrar", null)
            .create()

        rv.adapter = VariantesAdapter(variantes) { v ->
            varianteSeleccionada = v.nombre
            binding.ivModelo.setImageResource(v.imagen)
            ventanaIzquierda = cbIzq.isChecked
            ventanaDerecha = cbDer.isChecked
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun mostrarVariantes() {

        // Panel derecho: selector de tipo de ventana por forma
        binding.txMedCant.setOnClickListener {
            binding.lyPanelModelos.visibility =
                if (binding.lyPanelModelos.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        data class ModeloPanelP(val drawableRes: Int, val tipo: String)
        val items = listOf(
            binding.itemVplano  to ModeloPanelP(R.drawable.vplano,  "plano"),
            binding.itemVenl    to ModeloPanelP(R.drawable.venl,    "en_l"),
            binding.itemVenc    to ModeloPanelP(R.drawable.venc,    "en_c"),
            binding.itemVcurvo  to ModeloPanelP(R.drawable.vcurvo,  "curvo"),
            binding.itemVserie  to ModeloPanelP(R.drawable.vserie,  "serie")
        )
        for ((view, modelo) in items) {
            view.setOnClickListener {
                tipoVentanaPanel = modelo.tipo
                binding.ivModelo.setImageResource(modelo.drawableRes)
                binding.lyPanelModelos.visibility = View.GONE
            }
        }
    }

    // ==================== FUNCIONES PARA ARCHIVAR ====================
    private fun obtenerPrefijo(): String {
        return "P" // Puertas - prefijo para identificar cálculos de puertas
    }

    private fun mostrarDialogoMetadatosProduccion(onContinuar: () -> Unit) {
        val pad = (16 * resources.displayMetrics.density).toInt()
        val contenedor = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(pad, pad, pad, 0)
        }
        val etColor = android.widget.EditText(this).apply {
            hint = "Color aluminio (ej: negro)"
            setText(metaColorAluminio)
        }
        val etVidrio = android.widget.EditText(this).apply {
            hint = "Tipo vidrio (ej: incoloro 6mm)"
            setText(metaTipoVidrio)
        }
        contenedor.addView(etColor)
        contenedor.addView(etVidrio)

        AlertDialog.Builder(this)
            .setTitle("Metadatos de producción")
            .setView(contenedor)
            .setPositiveButton("Guardar y archivar") { _, _ ->
                metaColorAluminio = etColor.text?.toString()?.trim().orEmpty()
                metaTipoVidrio = etVidrio.text?.toString()?.trim().orEmpty()
                onContinuar()
            }
            .setNeutralButton("Omitir") { _, _ -> onContinuar() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deduplicar(texto: String): String {
        val regex = Regex("""^(.+?)\s*=\s*(\d+)$""")
        val orden = mutableListOf<String>()
        val sumas = linkedMapOf<String, Int>()
        for (linea in texto.lines()) {
            val m = regex.matchEntire(linea.trim())
            if (m != null) {
                val clave = m.groupValues[1].trim()
                val cant = m.groupValues[2].toInt()
                if (clave !in sumas) orden.add(clave)
                sumas[clave] = (sumas[clave] ?: 0) + cant
            } else {
                val k = "\u0000$linea"
                orden.add(k)
                sumas[k] = 0
            }
        }
        return orden.joinToString("\n") { k ->
            if (k.startsWith("\u0000")) k.drop(1) else "$k = ${sumas[k]}"
        }
    }

    private fun archivarMapas() {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
            mapListas.clear()
            if (mapExistente != null) {
                mapListas.putAll(mapExistente)
            }
        }

        val prefijo = obtenerPrefijo()
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        var ultimoID = ""

        for (u in 1..cant) {
            val siguienteNumero = ProyectoManager.obtenerSiguienteContadorPorPrefijo(this, prefijo)
            val matSufijo = if (metaColorAluminio.isNotBlank() || metaTipoVidrio.isNotBlank())
                "-MAT<alu:${metaColorAluminio.ifBlank { "null" }};vid:${metaTipoVidrio.ifBlank { "null" }}>"
            else ""
            val identificadorPaquete = "p${siguienteNumero}${prefijo}${matSufijo}"
            ultimoID = identificadorPaquete

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
        }

        MapStorage.guardarMap(this, mapListas)
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)

        val msg = if (cant > 1) "Archivadas $cant unidades en proyecto: ${ProyectoManager.getProyectoActivo()}"
                  else "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            val perfiles = mapOf(
                "Marco" to ModoMasivoHelper.texto(binding.tvMarco),
                "Tubo" to ModoMasivoHelper.texto(binding.tvTubo),
                "Paflón" to ModoMasivoHelper.texto(binding.tvPaflon)
            ).filter { it.value.isNotBlank() }

            val accesorios = mapOf(
                "Tope" to ModoMasivoHelper.texto(binding.tvTope),
                "Junquillo" to ModoMasivoHelper.texto(binding.tvJunki)
            ).filter { it.value.isNotBlank() }

            ModoMasivoHelper.devolverResultado(
                activity = this,
                calculadora = "Puerta",
                perfiles = perfiles,
                vidrios = ModoMasivoHelper.texto(binding.tvVidrios),
                accesorios = accesorios,
                referencias = ModoMasivoHelper.texto(binding.txRefe)
            )
            return
        }
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}
