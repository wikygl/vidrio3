package crystal.crystal.taller.puerta

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import crystal.crystal.taller.ControladorColaMedidas
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
import crystal.crystal.taller.puerta.datos.ConfigPuerta
import crystal.crystal.taller.puerta.datos.PuertaRepositorio
import crystal.crystal.taller.puerta.dibujo.DibujoPuerta
import crystal.crystal.taller.puerta.logica.CalculosLina
import crystal.crystal.taller.puerta.logica.CalculosPuerta
import crystal.crystal.taller.puerta.logica.PlanoRotado
import crystal.crystal.taller.puerta.modelos.Puerta
import crystal.crystal.taller.puerta.modelos.Variante
import crystal.crystal.taller.puerta.ui.VariantesAdapter

class PuertasActivity : AppCompatActivity() {

    private data class AjusteTere6(
        val hPuente: Float,
        val mocheta: Float,
        val parante: Float,
        val paranteInterno: Float,
        val cantidadTubos: Int,
        val altoPila: Float
    )

    // Constantes (conservadas del original)
    private val hojaRef = CalculosPuerta.HOJA_REF
    // Marco editable desde el diálogo de variantes (default CalculosPuerta.MARCO = 2.2). Aplica a
    // todos los modelos (antes solo Lina lo cambiaba con su propio EditText).
    private var marco = CalculosPuerta.MARCO
    // Puente (tubo) editable desde el diálogo de variantes (default 2.5). Aplica a la mocheta.
    private var puente = 2.5f
    // Inox (tubo de 1") editable desde el diálogo (default 2.5). Aplica a Dora: zócalos pares
    // y divisores de la columna izquierda. Cambia la altura de la zona de columnas y, con ella,
    // se recalculan paflones, junquillos y vidrios.
    private var inox = 2.5f
    // Los dos perfiles de la hoja, editables desde el diálogo de variantes. El BASTIDOR es el cuadro
    // exterior (paflón 8.25 por omisión) y el INTERIOR, todo lo que va dentro de él: los divisores de
    // Viky y de Mari, la estructura de Lina, el relleno de Tere 6. Con el interior cambian el tamaño
    // de los paños y, detrás de ellos, junquillos y vidrios. Cada variante puede arrancar con el
    // suyo — ver [interiorPorOmision] —, y mientras sea igual al bastidor todo se cuenta junto.
    private var bastidor = CalculosPuerta.BASTIDOR
    private var interior = CalculosPuerta.BASTIDOR
    // Juego entre la hoja y el marco, a lo ancho: el ancho de la hoja es el vano menos los dos
    // marcos menos esta holgura. Era un 1 fijo escondido en la fórmula; ahora se ingresa, porque
    // no todos los marcos ni todas las bisagras piden el mismo juego.
    private var holgura = 1f
    private val unoMedio = CalculosPuerta.UNO_MEDIO

    // Estado
    private var clienteActual: String = ""
    private var indicePuerta = 0
    private var puertaActual: Puerta? = null
    private var varianteSeleccionada: String = "Mari h"

    private lateinit var binding: ActivityPuertaPanoBinding
    private lateinit var controladorCola: ControladorColaMedidas
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
    private var metaAcabadoSuperficial: String = ""
    private var metaObservaciones: String = ""

    // Qué lista de vidrios se archiva cuando hay dos ("Mari d": detalle y apilado). Se eligen en el
    // diálogo de metadatos y normalmente van las dos.
    private var archivarVidrioDetalle: Boolean = true
    private var archivarVidrioApilado: Boolean = true

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
        cargarUltimaConfiguracion()
        indicePuerta = 0
        inicializarClienteYTipos()
        configurarListenersUI()
        mostrarVariantes()

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etMed1.setText(it.toString()) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etMed2.setText(it.toString()) }

        controladorCola = ControladorColaMedidas(
            activity = this,
            claseActual = PuertasActivity::class.java,
            etAncho = binding.etMed1,
            etAlto = binding.etMed2,
            ivDiseno = binding.ivModelo,
            onToqueSimple = { abrirDialogoVariantes() },
            onToqueLargo = {
                when (puertaActual?.nombre) {
                    "Dora", "Mari", "Viky", "Adel", "Mili", "jeny", "Taly", "Lina" -> mostrarPlano()
                    else -> startActivity(Intent(this, MedidaActivity::class.java))
                }
            }
        )
        controladorCola.inicializar()
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
            // Viky usa 5 divisiones horizontales por defecto.
            if (puertaActual?.nombre == "Viky") binding.etDivi.setText("5")
            // Lina reparte 5 paneles por omisión, que es el fallback que ya usaban sus fórmulas. Con
            // el 1 que traía el campo salían 0 rellenos, porque los rellenos son las divisiones - 1.
            if (puertaActual?.nombre == "Lina") binding.etDivi.setText("5")
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

    private fun zonaTaly(paflon: Float) = CalculosPuerta.zonaVidrioTaly(paflon, bastidor)

    /**
     * Elige un perfil del catálogo del taller y escribe su medida en el campo.
     *
     * La lista es la misma que usa Nova Corrediza —`NovaSpinnerData`—, para no mantener dos
     * catálogos. Cada perfil trae sus dos medidas: la de la CARA, que es con la que se calculan los
     * bastidores y los rellenos —el paflón 1½ entra como 8.25 y el tubo 2⅜ x 1 como 6—, y la del
     * CANTO, que es la que manda el puente: del mismo paflón, 3.8.
     */
    private fun elegirPerfil(destino: android.widget.EditText, usarCanto: Boolean, conCanales: Boolean = false) {
        // "Múltiple" y "gorrito" son perfiles de corrediza: no entran en ninguna puerta.
        val fueraDePuertas = setOf("Múltiple", "gorrito")
        val tubos = crystal.crystal.taller.nova.NovaSpinnerData.obtenerOpcionesTubo()
            .filterNot { it.text in fueraDePuertas }
            .map { it to if (usarCanto) it.valor else it.valorEsquina }

        // Los canales se AGREGAN a los tubos en el campo del marco, no los reemplazan. De ellos entra
        // la altura de la U, que es lo que el marco le come al vano.
        val canales = if (conCanales) canalesMarco.map { canal ->
            crystal.crystal.taller.nova.NovaSpinnerData.SpinnerTubos(
                canal.imagen,
                "${canal.nombre}  ${CalculosPuerta.dato(canal.base)} x ${CalculosPuerta.dato(canal.altura)}",
                canal.altura, canal.altura
            ) to canal.altura
        } else emptyList()

        val opciones = tubos + canales
        val adapter = crystal.crystal.taller.nova.NovaSpinnerData.AdaptadorSpinner(this, opciones.map { it.first })
        AlertDialog.Builder(this)
            .setTitle(if (usarCanto) "Perfil (canto)" else "Perfil (cara)")
            .setAdapter(adapter) { _, i -> destino.setText(valorEditable(opciones[i].second)) }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    /**
     * El marco no es un tubo sino un canal, así que tiene su propio catálogo: la U que recibe la
     * hoja. Lo que entra al cálculo es su ALTURA, que es lo que el marco le come al vano — 2.2 el
     * canal 2⅜ y 2.5 el canal paflón, justo los dos valores con los que ya trabajaba la app.
     */
    private data class CanalMarco(val imagen: Int, val nombre: String, val base: Float, val altura: Float)

    private val canalesMarco = listOf(
        CanalMarco(R.drawable.canaldostresochos, "canal 2⅜", 6f, 2.2f),
        CanalMarco(R.drawable.canalpaflon, "canal paflón", 8.25f, 2.5f)
    )

    /**
     * Interior de arranque de una variante; null = se conserva el que haya. Solo aparecen las que
     * NO se arman con paflón adentro: las demás heredan el bastidor y así el interior no se separa
     * en su propio renglón mientras el vidriero no lo cambie.
     */
    private fun interiorPorOmision(variante: String): Float? = when (variante) {
        "Tere 6" -> 6f                    // el interior se llena con tubos de 6
        "Lina b", "Lina c" -> 3.8f        // estructura de tubo detrás del panel
        else -> CalculosPuerta.BASTIDOR
    }

    /**
     * Un valor para escribir en un campo, sin perder precisión: 8.25 sigue siendo 8.25 y no 8.3.
     * Se quitan los ceros de sobra para que 2.20 se lea 2.2 y 8 no se lea 8.00.
     */
    private fun valorEditable(valor: Float): String =
        String.format(java.util.Locale.US, "%.2f", valor).trimEnd('0').trimEnd('.')

    // ---------------------- Configuraciones guardadas ----------------------

    /** Arranca con lo último que se usó, para no reconfigurar en cada medición. */
    private fun cargarUltimaConfiguracion() {
        val v = ConfigPuerta.ultima(this)
        marco = v.marco; puente = v.puente; inox = v.inox
        bastidor = v.bastidor; interior = v.interior; holgura = v.holgura
    }

    /**
     * Lista las configuraciones guardadas para aplicar una. `alElegir` recibe los valores para que
     * el diálogo de variantes los escriba en sus campos; el estado se actualiza al aceptar, como
     * con cualquier otro cambio hecho a mano.
     */
    private fun elegirConfiguracion(alElegir: (ConfigPuerta.Valores) -> Unit) {
        val guardadas = ConfigPuerta.guardadas(this)
        // La primera de la lista es siempre la de fábrica: es la salida. Sin ella, una vez cargada
        // una configuración no había cómo volver a los valores de siempre salvo escribirlos a mano.
        val nombres = listOf(ConfigPuerta.PREDETERMINADA) + guardadas.keys
        AlertDialog.Builder(this)
            .setTitle("Configuraciones")
            .setItems(nombres.toTypedArray()) { _, i ->
                val elegida = if (i == 0) ConfigPuerta.Valores() else guardadas[nombres[i]]
                elegida?.let(alElegir)
            }
            .apply {
                if (guardadas.isNotEmpty()) {
                    setNeutralButton("Borrar…") { _, _ -> borrarConfiguracion(guardadas.keys.toList()) }
                }
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    /** Solo las guardadas: la predeterminada es de fábrica y no se borra. */
    private fun borrarConfiguracion(nombres: List<String>) {
        AlertDialog.Builder(this)
            .setTitle("Borrar configuración")
            .setItems(nombres.toTypedArray()) { _, i ->
                ConfigPuerta.borrar(this, nombres[i])
                android.widget.Toast.makeText(
                    this, "Se borró \"${nombres[i]}\"", android.widget.Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Guarda con un nombre lo que hay en los campos. Con el mismo nombre, se reemplaza. */
    private fun guardarConfiguracion(valores: ConfigPuerta.Valores) {
        val et = android.widget.EditText(this).apply {
            hint = "Nombre de la configuración"
            setSingleLine()
        }
        AlertDialog.Builder(this)
            .setTitle("Guardar configuración")
            .setMessage(
                "Marco ${valorEditable(valores.marco)}   Puente ${valorEditable(valores.puente)}   " +
                    "Inox ${valorEditable(valores.inox)}\n" +
                    "Bastidor ${valorEditable(valores.bastidor)}   Interior ${valorEditable(valores.interior)}   " +
                    "Holgura ${valorEditable(valores.holgura)}"
            )
            .setView(et)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = et.text.toString().trim()
                if (nombre.isBlank()) return@setPositiveButton
                ConfigPuerta.guardar(this, nombre, valores)
                android.widget.Toast.makeText(
                    this, "Guardada como \"$nombre\"", android.widget.Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** En Taly el vidrio entra por dentro del junquillo, con 0.5 de holgura. */
    private fun holguraVidrioTaly(junki: Float): Float = 2f * junki + 0.5f

    // Rejilla de Jeny: columnas y filas que se repiten dentro de cada paño.
    private fun rejillaCols(): Int = binding.etRejCol.text.toString().toIntOrNull()?.coerceAtLeast(1) ?: 3
    private fun rejillaFilas(): Int = binding.etRejFil.text.toString().toIntOrNull()?.coerceAtLeast(1) ?: 3

    // Variantes que usan el ángulo para generar diseño y cálculos (por ahora solo las diagonales).
    private fun varianteUsaAngulo(): Boolean =
        varianteSeleccionada == "Mari d" || varianteSeleccionada == "Taly d"

    private fun actualizarVisibilidades() {
        val nombre = puertaActual?.nombre ?: ""
        // "Ancho divisiones" (lyAD) queda oculto: hoy no controla nada. Se leía y se pasaba al dibujo
        // de Adel, que ignoraba el valor, y su etiqueta y su comentario decían cosas distintas. Vuelve
        // cuando se decida qué mide — el candidato es el ancho de la columna de divisiones de Adel.
        binding.lyAD.visibility = View.GONE
        // La rejilla (columnas y filas dentro de cada paño) es propia de Jeny.
        binding.lyRejilla.visibility = if (nombre == "jeny") View.VISIBLE else View.GONE
        // El ángulo solo se ingresa en variantes que lo usan para diseño/cálculo (por ahora Mari d / Taly d).
        binding.lyAngulo.visibility = if (varianteUsaAngulo()) View.VISIBLE else View.GONE
        // El marco se edita ahora en el diálogo de variantes (para todos los modelos), no aquí.
        binding.lyMarcoVar.visibility = View.GONE
        binding.lyPanelDelgadoLina.visibility = if (nombre == "Lina" && varianteSeleccionada in setOf("Lina h", "Lina b")) View.VISIBLE else View.GONE
        if (nombre == "Lina") {
            binding.txZocalo.text = "Gruña"
            binding.etZocalo.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            val valor = binding.etZocalo.text?.toString()?.trim().orEmpty()
            if (valor.isBlank() || valor == "0" || valor == "1") binding.etZocalo.setText("0.8")
        } else {
            binding.txZocalo.text = getString(R.string.z_calo)
            binding.etZocalo.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            if (binding.etZocalo.text?.toString()?.trim() == "0.8") binding.etZocalo.setText("1")
        }
    }

    // ---------------------- Listeners de UI ----------------------
    private fun configurarListenersUI() {
        configurarNavegacionEnterEditTexts()

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
            when (puertaActual?.nombre) {
                "Dora", "Mari", "Viky", "Adel", "Mili", "jeny", "Taly", "Lina" -> mostrarPlano()
                else -> startActivity(Intent(this, MedidaActivity::class.java))
            }
            true
        }

        binding.btCalcular.setOnClickListener {
            controladorCola.onCalcular()
            ejecutarCalculoCompleto()
        }

        binding.btArchivar.setOnClickListener {
            // Modelo en desarrollo: no se archiva (sus cálculos aún no son válidos, solo hay aviso).
            if (crystal.crystal.FeaturesV1.AVISO_PUERTAS_EN_DESARROLLO &&
                !crystal.crystal.taller.puerta.datos.PuertaRepositorio.estaTerminada(puertaActual?.nombre ?: "")) {
                Toast.makeText(this, "Este modelo está en desarrollo; aún no se puede archivar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Candado de suscripción PRIMERO: bloquear antes de avanzar numeración o dar el toast.
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeArchivar(),
                    "Archivar es una función de pago. Renueva para guardar tus proyectos.")) {
                return@setOnClickListener
            }
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
                            limpiarMedidasYEnfocarAncho()
                        }
                    }

                    override fun onProyectoCreado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true

                        mostrarDialogoMetadatosProduccion {
                            archivarMapas()
                            limpiarMedidasYEnfocarAncho()
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
                                limpiarMedidasYEnfocarAncho()
                            }
                        }

                        override fun onProyectoCreado(nombreProyecto: String) {
                            primerClickArchivarRealizado = true

                            mostrarDialogoMetadatosProduccion {
                                archivarMapas()
                                limpiarMedidasYEnfocarAncho()
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
                    limpiarMedidasYEnfocarAncho()
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

    private fun configurarNavegacionEnterEditTexts() {
        val campos = listOf(
            binding.etMed1,
            binding.etMed2,
            binding.etHoja,
            binding.etMarcoVar,
            binding.etDivi,
            binding.etJunki,
            binding.etPiso,
            binding.etZocalo,
            binding.etPanelDelgadoLina,
            binding.etAD,
            binding.etAngulo
        )

        campos.forEach { campo ->
            campo.imeOptions = EditorInfo.IME_ACTION_NEXT
            campo.setSingleLine(true)
            campo.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) (view as? EditText)?.moverCursorAlFinal()
            }
            campo.setOnEditorActionListener { view, actionId, event ->
                val enterFisico = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP
                val accionSiguiente = actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                if (!enterFisico && !accionSiguiente) return@setOnEditorActionListener false

                val actual = campos.indexOf(view)
                val siguiente = campos
                    .drop(actual + 1)
                    .firstOrNull { it.isShown && it.isEnabled }

                if (siguiente != null) {
                    siguiente.requestFocus()
                    siguiente.moverCursorAlFinal()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun EditText.moverCursorAlFinal() {
        post {
            setSelection(text?.length ?: 0)
        }
    }

    private fun limpiarMedidasYEnfocarAncho() {
        binding.etMed1.setText("")
        binding.etMed2.setText("")
        binding.etMed1.requestFocus()
        binding.etMed1.moverCursorAlFinal()
    }

    // ---------------------- Calcular y renderizar ----------------------
    @SuppressLint("SetTextI18n")
    private fun ejecutarCalculoLina() {
        val ancho = binding.etMed1.text.toString().toFloat()
        val alto  = binding.etMed2.text.toString().toFloat()
        val hHoja = binding.etHoja.text.toString().toFloatOrNull() ?: 0f
        val marcoVar = marco  // marco del diálogo de variantes (aplica a todos los modelos)
        val nDivLina = binding.etDivi.text.toString().toIntOrNull()?.takeIf { it > 0 } ?: 5
        val gruna = binding.etZocalo.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: 0.8f
        val junki = binding.etJunki.text.toString().toFloatOrNull() ?: 0f
        val piso = binding.etPiso.text.toString().toFloatOrNull() ?: 0f
        val panelDelgadoLina = binding.etPanelDelgadoLina.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: 0f

        val esPlegado = varianteSeleccionada == "Lina p"
        // "Lina c" arma el mismo bastidor de paflón que "Lina b", así que mide la hoja igual.
        val hH = if (varianteSeleccionada == "Lina b" || varianteSeleccionada == "Lina c") {
            CalculosLina.hojaHConPiso(alto, hHoja, piso, esPlegado, marcoVar)
        } else {
            CalculosLina.hojaH(alto, hHoja, esPlegado, marcoVar)
        }
        val hV = CalculosLina.hojaV(ancho, marcoVar, holgura)
        val refV = CalculosLina.panelRefV(hH)
        binding.tvTope.text = CalculosLina.topeComun(ancho, hH, marcoVar)
        // Solo "Mari d" separa detalle, apilado y mocheta.
        binding.lyVidriosApilado.visibility = View.GONE
        binding.lyVidrioMocheta.visibility = View.GONE

        when (varianteSeleccionada) {
            "Lina h" -> {
                val refH = CalculosLina.panelRefH_h(ancho, marcoVar, gruna, panelDelgadoLina, holgura)
                binding.tvMarco.text  = CalculosLina.canal(ancho, alto, marcoVar)
                binding.tvTubo.text   = CalculosLina.tuboPuente(ancho, marcoVar)
                binding.tvPaflon.text = CalculosLina.tres(hH, ancho, refH, marcoVar, holgura)
                binding.txMel.text = "Interno"
                binding.tvMel.text = ""
                binding.tvJunki.text  = CalculosLina.junquilloMocheta(ancho, alto, hH, marcoVar, junki)
                val vid = CalculosLina.vidrioH(hH, ancho, alto, marcoVar)
                binding.tvMela.text = CalculosLina.panelH(hH, refV, refH, ancho, marcoVar, gruna, panelDelgadoLina)
                binding.tvVidrios.text = vid
                binding.lyTubo.visibility = View.VISIBLE
            }
            "Lina b" -> {
                binding.tvMarco.text  = CalculosLina.canal(ancho, alto, marcoVar)
                binding.tvTubo.text   = CalculosLina.tuboPuente(ancho, marcoVar)
                binding.tvPaflon.text = CalculosLina.paflonBBastidor(hH, ancho, marcoVar, piso, holgura)
                binding.txMel.text = "Interior ${CalculosPuerta.df1(interior)}"
                binding.tvMel.text = CalculosLina.paflonBInterno(
                    hH, ancho, marcoVar, nDivLina, gruna, interior, piso, panelDelgadoLina, bastidor,
                    holgura
                )
                binding.tvJunki.text  = CalculosLina.junquilloMocheta(ancho, alto, hH, marcoVar, junki)
                val vid = CalculosLina.vidrioH(hH, ancho, alto, marcoVar)
                binding.tvMela.text = CalculosLina.panelB(hH, ancho, marcoVar, nDivLina, gruna, piso, panelDelgadoLina, holgura)
                binding.tvVidrios.text = vid
                binding.lyTubo.visibility = View.VISIBLE
            }
            "Lina c" -> {
                // Un panel entero sobre el bastidor: sin gruma, sin divisores y sin vidrio en la
                // hoja. Lo único que queda del vidrio es la mocheta, si la puerta la lleva.
                binding.tvMarco.text  = CalculosLina.canal(ancho, alto, marcoVar)
                binding.tvTubo.text   = CalculosLina.tuboPuente(ancho, marcoVar)
                binding.tvPaflon.text = CalculosLina.paflonBBastidor(hH, ancho, marcoVar, piso, holgura)
                // Es contraplacada: adentro lleva travesaños, tantos como hagan falta para que
                // ningún tramo pase de 40. La plancha los tapa, así que solo se ven en el plano.
                binding.txMel.text = "Interior ${CalculosPuerta.df1(interior)}"
                binding.tvMel.text = CalculosLina.estructuraContraplacado(
                    hH, ancho, marcoVar, piso, interior, holgura = holgura
                )
                binding.tvJunki.text  = CalculosLina.junquilloMocheta(ancho, alto, hH, marcoVar, junki)
                binding.tvMela.text = CalculosLina.panelCompleto(hH, ancho, marcoVar, piso, holgura)
                binding.tvVidrios.text = CalculosLina.vidrioH(hH, ancho, alto, marcoVar)
                binding.lyTubo.visibility = View.VISIBLE
            }
            "Lina p" -> {
                val refH = CalculosLina.panelRefH_p(hV)
                binding.tvMarco.text  = "${CalculosLina.marcoPlegado(ancho, alto)}\n${CalculosLina.contraMarco(ancho, alto, marcoVar)}"
                binding.tvTubo.text   = ""
                binding.tvPaflon.text = "${CalculosLina.bandejas(hH, ancho, alto, marcoVar, hV)}\n${CalculosLina.fierroTresDos(hH, ancho, marcoVar, hV)}\n${CalculosLina.platina(hH)}"
                binding.txMel.text = "Interno"
                binding.tvMel.text = ""
                binding.tvJunki.text  = CalculosLina.junquilloMocheta(ancho, alto, hH, marcoVar, junki)
                binding.tvMela.text = "${CalculosLina.panelP(hH, ancho, alto, refV, refH)}\n${CalculosLina.plancha(hH, ancho, alto, marcoVar, hV)}"
                binding.tvVidrios.text = ""
                binding.lyTubo.visibility = View.GONE
            }
        }

        val mochetaLina = alto - (hH + marcoVar + puente)
        binding.txRefe.text = if (varianteSeleccionada == "Lina b") {
            CalculosPuerta.referen(ancho, alto, hH, mochetaLina)
        } else {
            "anch ${CalculosPuerta.dato(ancho)} x alt ${CalculosPuerta.dato(alto)}\nAlto hoja = ${CalculosPuerta.dato(hH)}"
        }
        binding.txCliente.text = clienteActual
        binding.tvEnsayo.text  = ""
        binding.tvEnsayo2.text = ""
        renderizarModeloActual()
    }

    private fun ejecutarCalculoCompleto() {
        // v1: los modelos aún NO terminados muestran su diseño/plano, pero en los resultados avisan
        // "en desarrollo" en vez de calcular. Ver FeaturesV1.AVISO_PUERTAS_EN_DESARROLLO.
        val modeloActual = puertaActual?.nombre ?: ""
        if (crystal.crystal.FeaturesV1.AVISO_PUERTAS_EN_DESARROLLO &&
            !crystal.crystal.taller.puerta.datos.PuertaRepositorio.estaTerminada(modeloActual)) {
            renderizarModeloActual()        // el diseño SÍ se dibuja
            mostrarAvisoEnDesarrollo()      // los resultados muestran el aviso
            return
        }
        try {
            if (varianteSeleccionada.startsWith("Lina")) {
                ejecutarCalculoLina()
                return
            }
            // Entradas
            val ancho = binding.etMed1.text.toString().toFloat()
            val alto = binding.etMed2.text.toString().toFloat()
            val nZocalos = binding.etZocalo.text.toString().toIntOrNull() ?: 0
            val nDiv = binding.etDivi.text.toString().toIntOrNull() ?: 0
            val junki = binding.etJunki.text.toString().toFloatOrNull() ?: 0f
            val piso = binding.etPiso.text.toString().toFloatOrNull() ?: 0f
            val hHoja = binding.etHoja.text.toString().toFloatOrNull() ?: 0f
            val angulo = binding.etAngulo.text.toString().toFloatOrNull() ?: 0f
            binding.tvMela.text = ""
            binding.tvMel.text = ""

            // Marco lateral según conexión a ventana (canal=2.2, tubo=2.5)
            val tuboW = 2.5f
            val marcoIzq = if (ventanaIzquierda) tuboW else marco
            val marcoDer = if (ventanaDerecha) tuboW else marco
            val totalMarco = marcoIzq + marcoDer

            // Cálculos base
            var hPuente = CalculosPuerta.hPuente(alto, hHoja, piso, hojaRef, marco)
            var mocheta = CalculosPuerta.mocheta(alto, hPuente, marco, puente)
            val marcoSup = ancho - totalMarco
            var tubo = if (mocheta > 0f) CalculosPuerta.df1(marcoSup) else ""
            val paflon = CalculosPuerta.anchoHoja(ancho, marcoIzq, marcoDer, holgura) - (2f * bastidor)
            val nZ = CalculosPuerta.nZocalo(nZocalos)
            var parante = CalculosPuerta.parante(hPuente, piso)
            var paranteInt = CalculosPuerta.paranteInterno(parante, nZ, bastidor)
            val ajusteTere6 = if (varianteSeleccionada == "Tere 6") {
                calcularAjusteTere6(alto, hPuente, piso, nZ).also {
                    hPuente = it.hPuente
                    mocheta = it.mocheta
                    parante = it.parante
                    paranteInt = it.paranteInterno
                    tubo = if (mocheta > 0f) CalculosPuerta.df1(marcoSup) else ""
                }
            } else {
                null
            }
            val divisTam = CalculosPuerta.divisiones(parante, nZ, nDiv.toFloat(), bastidor)
            val nPfvcal = CalculosPuerta.nPfvcal(nDiv)
            val nPaflones = CalculosPuerta.nPaflones(nDiv, nZocalos)
            val zocalo = CalculosPuerta.zocalo(nZ, bastidor)
            val esAdel = puertaActual?.nombre == "Adel"
            val esMili = puertaActual?.nombre == "Mili"
            val esJeny = puertaActual?.nombre == "jeny"
            val esVikyC = varianteSeleccionada == "Viky c"
            val esViky = puertaActual?.nombre == "Viky" && !esVikyC
            // El aluminio del interior hace de divisor solo donde el modelo lo aprovecha. En Mari son
            // las divisiones —horizontales, verticales o diagonales—; el resto sigue con el bastidor
            // hasta que a cada modelo le toque su turno.
            val interiorDivisor = if (modeloActual == "Mari") interior else bastidor

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
                if (ajusteTere6 != null) {
                    if (isNotEmpty()) append("\n")
                    append("${CalculosPuerta.df1(paflon)} = ${ajusteTere6.cantidadTubos}")
                }
            }

            // Dora: zócalos alternados paflon / tubo inoxidable de 2.5 (la cantidad es siempre impar)
            val nZdora = if (nZ % 2 == 0) maxOf(1, nZ - 1) else nZ
            val zocaloInox = (nZdora - 1) / 2   // posiciones pares = tubos inoxidables de 2.5
            // Inox de la columna izquierda (divisores de 2.5), controlado por "divisiones" (igual al dibujo)
            val nInoxIzq = nDiv.takeIf { it >= 1 } ?: 9
            // Geometría Dora: bastidores horizontales (superior + zócalos impares) y altura de la zona
            // de columnas (resto de la hoja tras restar horizontales y tubos 2.5).
            val paflonHDora = 1 + (nZdora + 1) / 2
            val paranteIntDora = parante - bastidor * paflonHDora - inox * zocaloInox

            // Paflones según variante
            // Viky: las piezas de adentro pueden ser otro aluminio. Si es el mismo perfil que el
            // bastidor van en la misma lista (se suman); si es distinto, se muestran aparte en la
            // fila "Interior", porque son otro material y no se pueden mezclar al pedirlos.
            val esMari = modeloActual == "Mari"
            val interiorPropio = (esViky || esVikyC || esMari) && interior != bastidor
            val piezasInteriores = when {
                esVikyC -> CalculosPuerta.textoInteriorVikyC(paflon, paranteInt, nDiv, interior)
                esViky -> CalculosPuerta.textoInteriorViky(parante, nZ, nDiv, bastidor, interior)
                // En Mari las piezas de adentro son las divisiones: horizontales de ancho de paflón,
                // verticales de alto interior o las diagonales, según la variante.
                esMari -> when (varianteSeleccionada) {
                    "Mari h" -> if (nDiv > 1) "${CalculosPuerta.df1(paflon)} = ${nDiv - 1}" else ""
                    "Mari v" -> if (nDiv > 1) "${CalculosPuerta.df1(paranteInt)} = ${nDiv - 1}" else ""
                    "Mari d" -> CalculosPuerta.textoPaflonesMariD(paflon, paranteInt, nDiv, interiorDivisor, angulo)
                    else -> ""
                }
                else -> ""
            }
            binding.tvPaflon.text = deduplicar(if (esMili) {
                CalculosPuerta.textoPaflonMili(paflon, parante, nZ)
            } else if (esAdel) {
                CalculosPuerta.textoPaflonAdel(varianteSeleccionada, paflon, parante, paranteInt, nZ, nDiv, bastidor)
            } else if (esVikyC) {
                val marcoHoja = CalculosPuerta.textoPaflonVikyC(paflon, parante, nZ, nDiv)
                if (interiorPropio) marcoHoja else listOf(marcoHoja, piezasInteriores).filter { it.isNotBlank() }.joinToString("\n")
            } else if (esViky) {
                val marcoHoja = CalculosPuerta.textoPaflonViky(paflon, parante, nZ, nDiv, bastidor)
                if (interiorPropio) marcoHoja else listOf(marcoHoja, piezasInteriores).filter { it.isNotBlank() }.joinToString("\n")
            } else when (varianteSeleccionada) {
                "Tere 6" -> "${CalculosPuerta.df1(paflon)} = ${nZ + 1}\n${CalculosPuerta.df1(parante)} = 2"
                // Las divisiones se cuentan acá solo mientras sean del mismo perfil que el bastidor.
                // Si el interior es otro aluminio salen en su propia fila y el cuadro se queda con
                // lo suyo: los horizontales del marco de la hoja y los dos parantes.
                "Mari h" -> {
                    val horizontales = if (interiorPropio) nZ + 1 else nPaflones
                    "${CalculosPuerta.df1(paflon)} = $horizontales\n${CalculosPuerta.df1(parante)} = 2"
                }
                "Mari v" -> buildString {
                    append("${CalculosPuerta.df1(paflon)} = ${nZ}\n${CalculosPuerta.df1(parante)} = 2")
                    if (!interiorPropio && nDiv > 1) append("\n${CalculosPuerta.df1(paranteInt)} = ${nDiv - 1}")
                }
                "Mari d" -> {
                    val diagonales = if (interiorPropio) "" else
                        CalculosPuerta.textoPaflonesMariD(paflon, paranteInt, nDiv, interiorDivisor, angulo)
                    buildString {
                        append("${CalculosPuerta.df1(paflon)} = ${nZ + 1}")
                        if (diagonales.isNotBlank()) append("\n$diagonales")
                        append("\n${CalculosPuerta.df1(parante)} = 2")
                    }
                }
                "Taly h", "Taly d" -> {
                    // Parantes interiores de a pares hasta que el vidrio entra en rango. Si quedan de
                    // tubo, no van en esta lista: son otro perfil y salen en la fila "Interno".
                    val zona = zonaTaly(paflon)
                    val gapTaly = zona.anchoVidrio
                    val altInterno = paranteInt
                    val nDivisores = maxOf(0, nDiv - 1)
                    buildString {
                        append("${CalculosPuerta.df1(parante)} = 2\n")
                        append("${CalculosPuerta.df1(paflon)} = 2")
                        if (zona.paresPaflon > 0) {
                            append("\n${CalculosPuerta.df1(altInterno)} = ${zona.paresPaflon * 2}")
                        }
                        append("\n${CalculosPuerta.df1(gapTaly)} = 2")  // topInner + botInner
                        if (nZ > 1) append("\n${CalculosPuerta.df1(gapTaly)} = ${nZ - 1}")  // zócalo extra
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
                "Dora" -> {
                    // Horizontales: paflones (superior + zócalos impares). Verticales internos: 2 divisores
                    // de columna (largo paranteIntDora). Diagonales: 4 piezas a 45° = colW·√2 + un lado
                    // (bastidor): a 45° el extra de corte de MariD (bastidor·tan(ángulo)) vale el bastidor.
                    val colW = (paflon - 2f * bastidor) / 3f
                    val diagonal = colW * kotlin.math.sqrt(2f) + bastidor
                    buildString {
                        append("${CalculosPuerta.df1(paflon)} = $paflonHDora\n")
                        append("${CalculosPuerta.df1(parante)} = 2\n")
                        append("${CalculosPuerta.df1(paranteIntDora)} = 2\n")
                        append("${CalculosPuerta.df1(diagonal)} = 4")
                    }
                }
                else -> "${CalculosPuerta.df1(paflon)} = $nPaflones\n${CalculosPuerta.df1(parante)} = 2"
            })

            // Jeny: la rejilla es perfil de 2.5 montado sobre el vidrio, otro material que el
            // bastidor, así que va en su propia fila.
            if (esJeny) {
                val rejilla = CalculosPuerta.textoRejillaJeny(
                    varianteSeleccionada, paflon, divisTam, nDiv, rejillaCols(), rejillaFilas()
                )
                binding.txMel.text = "Rejilla ${CalculosPuerta.df1(CalculosPuerta.REJILLA_JENY)}"
                binding.tvMel.text = deduplicar(rejilla)
            }

            // Taly: cuando los parantes interiores se rehacen con tubo, van en su propia fila.
            if (varianteSeleccionada.startsWith("Taly")) {
                val zona = zonaTaly(paflon)
                if (zona.paresTubo > 0) {
                    binding.txMel.text = "Interno ${CalculosPuerta.df1(zona.tubo)}"
                    binding.tvMel.text = "${CalculosPuerta.df1(paranteInt)} = ${zona.paresTubo * 2}"
                } else {
                    binding.txMel.text = "Interno"
                }
            }

            // Mili arma el molinete con tubo de 3.8, que no es el paflón del bastidor: va aparte.
            if (esMili) {
                binding.txMel.text = "Interno ${CalculosPuerta.df1(CalculosPuerta.TUBO_MILI)}"
                binding.tvMel.text = deduplicar(CalculosPuerta.textoTubosMili(paflon, paranteInt))
            }

            // Fila "Interior": solo cuando el aluminio de adentro es distinto del bastidor.
            if (interiorPropio && piezasInteriores.isNotBlank()) {
                binding.txMel.text = "Interior ${CalculosPuerta.df1(interior)}"
                binding.tvMel.text = deduplicar(piezasInteriores)
            } else if (esViky || esVikyC || esMari) {
                binding.txMel.text = "Interno"
            }

            // Inoxidable (lista propia): tubos de 2.5. (a) Zócalos pares: largo = paflon del zócalo
            // (ocupan ese ancho). (b) Divisores de la columna izquierda: largo = ancho de columna colW.
            binding.tvInox.text = if (varianteSeleccionada == "Dora") {
                val colWInox = (paflon - 2f * bastidor) / 3f
                deduplicar(buildString {
                    if (zocaloInox > 0) append("${CalculosPuerta.df1(paflon)} = $zocaloInox")
                    if (nInoxIzq > 0) {
                        if (isNotEmpty()) append("\n")
                        append("${CalculosPuerta.df1(colWInox)} = $nInoxIzq")
                    }
                })
            } else ""

            // Junkillos y vidrios
            binding.tvJunki.text = if (esJeny) {
                CalculosPuerta.textoJunkillosJeny(paflon, divisTam, nDiv, junki, mocheta, marcoSup)
            } else if (esMili) {
                CalculosPuerta.textoJunkillosMili(paflon, paranteInt, junki, mocheta, marcoSup)
            } else if (esAdel) {
                CalculosPuerta.textoJunkillosAdel(varianteSeleccionada, paflon, paranteInt, nDiv, bastidor, junki, mocheta, marcoSup)
            } else if (esVikyC) {
                CalculosPuerta.textoJunkillosVikyC(paflon, paranteInt, nDiv, interior, junki, mocheta, marcoSup)
            } else if (esViky) {
                CalculosPuerta.textoJunkillosViky(paflon, parante, nZ, nDiv, bastidor, junki, mocheta, marcoSup, interior)
            } else if (varianteSeleccionada == "Dora") {
                CalculosPuerta.textoJunkillosDora(paflon, paranteIntDora, bastidor, junki, nInoxIzq, mocheta, marcoSup)
            } else if (varianteSeleccionada == "Mari d") {
                CalculosPuerta.textoJunkillosMariD(paflon, paranteInt, nDiv, interiorDivisor, junki, angulo)
            } else if (varianteSeleccionada == "Taly h" || varianteSeleccionada == "Taly d") {
                val gapTalyJ = zonaTaly(paflon).anchoVidrio
                val zoneHcm = paranteInt - 2f * bastidor
                buildString {
                    if (varianteSeleccionada == "Taly d" && angulo != 0f) {
                        // Con los paños repartidos como en Mari d, sus junquillos ya no son todos
                        // iguales: cada tramo tiene el suyo. Se calcula con la misma geometría en vez
                        // de repetir una medida por sección, que es lo que hacía antes.
                        append(CalculosPuerta.textoJunkillosMariD(gapTalyJ, zoneHcm, nDiv, bastidor, junki, angulo))
                    } else {
                        // El par horizontal va a tope y el vertical entra entre ellos, descontando
                        // los dos junquillos. Antes se descontaban los dos pares y el marco quedaba
                        // corto por los cuatro lados, dejando el hueco a la vista en las esquinas.
                        val barrasJ = maxOf(0, nDiv - 1)
                        val gapSeccion = if (nDiv > 0) (zoneHcm - barrasJ * bastidor) / nDiv else zoneHcm
                        append("${CalculosPuerta.df1(gapTalyJ)} = ${nDiv * 2}\n")
                        append("${CalculosPuerta.df1(gapSeccion - 2f * junki)} = ${nDiv * 2}")
                    }
                    // Mocheta
                    if (mocheta > 0f) {
                        if (isNotEmpty()) append("\n")
                        append("${CalculosPuerta.df1(marcoSup)} = 2\n")
                        append("${CalculosPuerta.df1(mocheta - 2f * junki)} = 2")
                    }
                }
            } else {
                CalculosPuerta.textoJunkillos(
                    varianteSeleccionada, junki, mocheta, nPfvcal, paflon, bastidor, nDiv, paranteInt,
                    marcoSup, interior = interiorDivisor
                )
            }
            val vidriosApilados = if (esJeny) {
                // Jeny reparte el alto como Mari h, pero sus divisiones son de bastidor.
                CalculosPuerta.textoVidrios("Mari h", junki, paflon, bastidor, nDiv, paranteInt, marcoSup, mocheta, angulo)
            } else if (esMili) {
                CalculosPuerta.textoVidriosMili(paflon, paranteInt, junki, mocheta, marcoSup)
            } else if (esAdel) {
                CalculosPuerta.textoVidriosAdel(varianteSeleccionada, paflon, paranteInt, nDiv, bastidor, junki, mocheta, marcoSup)
            } else if (esVikyC) {
                CalculosPuerta.textoVidriosVikyC(paflon, paranteInt, nDiv, interior, junki, mocheta, marcoSup)
            } else if (esViky) {
                CalculosPuerta.textoVidriosViky(paflon, parante, nZ, nDiv, bastidor, junki, mocheta, marcoSup, interior)
            } else if (varianteSeleccionada == "Dora") {
                CalculosPuerta.textoVidriosDora(paflon, paranteIntDora, bastidor, junki, nInoxIzq, mocheta, marcoSup)
            } else if (varianteSeleccionada == "Taly d" && angulo != 0f) {
                // Los vidrios diagonales se reparten con la geometría de Mari d: cada pieza sale del
                // recorte real de la zona, no de una sola medida para todas. Aquí se lista el
                // APILADO —el rectángulo del que se cortan— y el detalle va en la fila de arriba.
                CalculosPuerta.textoVidriosMariD(
                    zonaTaly(paflon).anchoVidrio, paranteInt - 2f * bastidor, nDiv, bastidor, angulo,
                    holgura = holguraVidrioTaly(junki)
                )
            } else if (varianteSeleccionada == "Taly h" || varianteSeleccionada == "Taly d") {
                val gapTalyV = zonaTaly(paflon).anchoVidrio
                val zoneHv = paranteInt - 2f * bastidor
                val barrasV = maxOf(0, nDiv - 1)
                val gapSeccionV = if (nDiv > 0) (zoneHv - barrasV * bastidor) / nDiv else zoneHv
                val descuento = holguraVidrioTaly(junki)
                "${CalculosPuerta.df1(gapTalyV - descuento)} x ${CalculosPuerta.df1(gapSeccionV - descuento)} = $nDiv"
            } else {
                CalculosPuerta.textoVidrios(
                    varianteSeleccionada, junki, paflon, bastidor, nDiv, paranteInt, marcoSup, mocheta,
                    angulo, interior = interiorDivisor
                )
            }

            // "Mari d" corta cada vidrio por separado (trapecios y paralelogramos), así que la fila
            // de arriba lleva el detalle pieza por pieza y el apilado —lo que se compra— baja a su
            // propia fila. En el resto de variantes solo existe una lista y la fila extra se oculta.
            val detalleVidrios = if (varianteSeleccionada == "Taly d" && angulo != 0f) {
                CalculosPuerta.textoVidriosDetalleMariD(
                    zonaTaly(paflon).anchoVidrio, paranteInt - 2f * bastidor, nDiv, bastidor, angulo,
                    holgura = holguraVidrioTaly(junki)
                )
            } else {
                CalculosPuerta.textoVidriosDetalle(
                    varianteSeleccionada, paflon, bastidor, nDiv, paranteInt, angulo, junki,
                    interior = interiorDivisor
                )
            }
            if (detalleVidrios.isNotBlank()) {
                binding.tvVidrios.text = detalleVidrios
                binding.tvVidriosApilado.text = vidriosApilados
                binding.lyVidriosApilado.visibility = View.VISIBLE
            } else {
                binding.tvVidrios.text = vidriosApilados
                binding.lyVidriosApilado.visibility = View.GONE
            }

            // La mocheta va aparte: es un rectángulo común, no entra en el detalle ni en el apilado
            // y se archiva siempre, aunque se destilde alguna de las otras dos listas.
            val vidrioMocheta = CalculosPuerta.textoVidrioMocheta(varianteSeleccionada, marcoSup, mocheta, junki)
            binding.tvVidrioMocheta.text = vidrioMocheta
            binding.lyVidrioMocheta.visibility = if (vidrioMocheta.isBlank()) View.GONE else View.VISIBLE

            // Referencias
            binding.txRefe.text = CalculosPuerta.referen(ancho, alto, hPuente, mocheta)
            binding.lyTubo.visibility = if (binding.tvTubo.text.isNullOrBlank()) View.GONE else View.VISIBLE

            // Texto ensayo de paños (como original)
            binding.tvEnsayo.text = if (ajusteTere6 != null) {
                "Tubo ${CalculosPuerta.dato(interior)} = ${ajusteTere6.cantidadTubos}\nPila = ${CalculosPuerta.df1(ajusteTere6.altoPila)}"
            } else if (varianteSeleccionada == "Mari v") {
                CalculosPuerta.textoPanosVertical(paflon, nDiv, bastidor)
            } else if (varianteSeleccionada == "Mari h") {
                CalculosPuerta.cotasPanos(parante, nZ, nDiv, bastidor).joinToString("\n") { CalculosPuerta.df1(it) }
            } else if (varianteSeleccionada == "Mari d") {
                val pi = CalculosPuerta.paranteInterno(parante, nZ, bastidor)
                val cr = CalculosPuerta.cotasPanosDiagonal(paflon, pi, nZ, nDiv, bastidor, angulo)
                val zocList = (1..nZ).map { it * bastidor }
                val izqE = (zocList + cr.izq).sorted()
                val derE = (zocList + cr.der).sorted()
                buildString {
                    append("Izq:\n"); append(izqE.joinToString("\n") { CalculosPuerta.df1(it) })
                    append("\nDer:\n"); append(derE.joinToString("\n") { CalculosPuerta.df1(it) })
                    if (cr.sup.isNotEmpty()) { append("\nSup:\n"); append(cr.sup.joinToString("\n") { CalculosPuerta.df1(it) }) }
                    if (cr.inf.isNotEmpty()) { append("\nInf:\n"); append(cr.inf.joinToString("\n") { CalculosPuerta.df1(it) }) }
                }
            } else {
                CalculosPuerta.textoPanos(zocalo, nPfvcal, divisTam, bastidor)
            }
            binding.tvEnsayo2.text = if (ajusteTere6 != null) {
                ""
            } else {
                CalculosPuerta.textoResumenArray(
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
            }

            // Actualizar el TextView del cliente con el valor actual
            binding.txCliente.text = clienteActual

            // Dibujo
            renderizarModeloActual()

        } catch (e: Exception) {
            Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * El interior se llena con tubos del RELLENO elegido en el diálogo de variantes. Por omisión son
     * 6, que es lo que llevaba fijo antes, pero el valor ingresado manda.
     */
    private fun calcularAjusteTere6(alto: Float, hPuenteBase: Float, piso: Float, nZ: Int, tubo6: Float = interior): AjusteTere6 {
        val gruma = 0.5f
        val paranteBase = CalculosPuerta.parante(hPuenteBase, piso)
        val interiorBase = (CalculosPuerta.paranteInterno(paranteBase, nZ, bastidor)).coerceAtLeast(tubo6)

        fun altoPila(cantidad: Int): Float {
            val c = cantidad.coerceAtLeast(1)
            return (c * tubo6) + ((c - 1) * gruma)
        }

        val cantidadSuperior = kotlin.math.ceil(((interiorBase + gruma) / (tubo6 + gruma)).toDouble()).toInt().coerceAtLeast(1)
        val pilaSuperior = altoPila(cantidadSuperior)
        val hPuenteSuperior = hPuenteBase + (pilaSuperior - interiorBase)
        val maxHoja = alto - marco

        val usarSuperior = hPuenteSuperior <= maxHoja + 0.05f
        val cantidadFinal = if (usarSuperior) {
            cantidadSuperior
        } else {
            kotlin.math.floor(((interiorBase + gruma) / (tubo6 + gruma)).toDouble()).toInt().coerceAtLeast(1)
        }
        val pilaFinal = altoPila(cantidadFinal)
        val hPuenteFinal = if (usarSuperior) {
            hPuenteSuperior
        } else {
            hPuenteBase - (interiorBase - pilaFinal)
        }
        val paranteFinal = CalculosPuerta.parante(hPuenteFinal, piso)
        val mochetaFinal = CalculosPuerta.mocheta(alto, hPuenteFinal, marco, puente)

        return AjusteTere6(
            hPuente = hPuenteFinal,
            mocheta = mochetaFinal,
            parante = paranteFinal,
            paranteInterno = CalculosPuerta.paranteInterno(paranteFinal, nZ, bastidor),
            cantidadTubos = cantidadFinal,
            altoPila = pilaFinal
        )
    }

    // Plano con medidas de Dora (long-click en ivModelo): se muestra en un diálogo con scroll.
    // Genera y muestra el plano (con cotas) del modelo actual, reusando PuertaRender.
    private fun mostrarPlano() {
        if (binding.etMed1.text.toString().toFloatOrNull() == null ||
            binding.etMed2.text.toString().toFloatOrNull() == null) {
            Toast.makeText(this, "Realice el cálculo primero", Toast.LENGTH_SHORT).show()
            return
        }
        val datos = descriptorDatosActual()
        val bmp = PuertaRender.dibujarPlano(this, datos) ?: return
        mostrarPlanoDialog(bmp, "Plano ${datos.variante.ifBlank { datos.modelo }}".trim())
    }

    // Muestra el plano (bitmap a alta resolución) en DisenoActivity, a pantalla completa.
    private fun mostrarPlanoDialog(bmp: Bitmap, titulo: String) {
        DibujoPuerta.guardarPlanoEnCache(this, bmp)
        val intent = Intent(this, crystal.crystal.Diseno.DisenoActivity::class.java).apply {
            putExtra(crystal.crystal.Diseno.DisenoActivity.EXTRA_PLANO, true)
            putExtra(crystal.crystal.Diseno.DisenoActivity.EXTRA_PLANO_TITULO, titulo)
        }
        startActivity(intent)
    }

    // Plano con medidas originales de Mari h (long-click en ivModelo).
    /** Modelo en desarrollo: el plano ya se dibuja, pero los resultados muestran un aviso. */
    private fun mostrarAvisoEnDesarrollo() {
        val aviso = "🚧 En desarrollo"
        listOf(
            binding.tvMarco, binding.tvTope, binding.tvTubo, binding.tvPaflon,
            binding.tvMel, binding.tvMela, binding.tvJunki, binding.tvVidrios,
            binding.tvInox, binding.tvEnsayo, binding.tvEnsayo2
        ).forEach { it.text = aviso }
        binding.lyVidriosApilado.visibility = View.GONE
        binding.lyVidrioMocheta.visibility = View.GONE
        binding.txMel.text = ""
        binding.txRefe.text = "🚧 Modelo en desarrollo — el diseño ya funciona; los cálculos estarán disponibles muy pronto."
    }

    private fun renderizarModeloActual() {
        // Solo los modelos con renderizado implementado generan bitmap
        val nombreModelo = puertaActual?.nombre ?: return
        if (nombreModelo != "Mari" && nombreModelo != "Taly" && nombreModelo != "Lina" && nombreModelo != "Adel" && nombreModelo != "Mili" && nombreModelo != "jeny" && nombreModelo != "Dora" && nombreModelo != "Tere" && nombreModelo != "Viky") return
        val anchoPuertaCm = binding.etMed1.text.toString().toFloatOrNull() ?: return
        val altoPuertaCm = binding.etMed2.text.toString().toFloatOrNull() ?: return
        val nZocalos = binding.etZocalo.text.toString().toIntOrNull() ?: 0
        val nDiv = binding.etDivi.text.toString().toIntOrNull() ?: 0
        val tipoDivision = PuertaRender.tipoDivision(nombreModelo, varianteSeleccionada)

        // Hoja — ancho depende del tipo de marco lateral (canal 2.2 o tubo 2.5)
        val marcoIzqRender = if (ventanaIzquierda) 2.5f else marco
        val marcoDerRender = if (ventanaDerecha) 2.5f else marco
        val anchoHojaCm = CalculosPuerta.anchoHoja(anchoPuertaCm, marcoIzqRender, marcoDerRender, holgura)
        var altoHojaCm = CalculosPuerta.hPuente(
            altoPuertaCm,
            binding.etHoja.text.toString().toFloatOrNull() ?: 0f,
            binding.etPiso.text.toString().toFloatOrNull() ?: 0f,
            hojaRef,
            marco
        )
        val nZRender = CalculosPuerta.nZocalo(nZocalos)
        val ajusteTere6 = if (nombreModelo == "Tere" && varianteSeleccionada == "Tere 6") {
            calcularAjusteTere6(altoPuertaCm, altoHojaCm, binding.etPiso.text.toString().toFloatOrNull() ?: 0f, nZRender).also {
                altoHojaCm = it.hPuente
            }
        } else {
            null
        }

        val anchoContenedor = anchoPuertaCm * 3
        val altoContenedor = altoPuertaCm * 3
        val angulo = binding.etAngulo.text.toString().toFloatOrNull() ?: 0f
        val pisoG = binding.etPiso.text.toString().toFloatOrNull() ?: 0f
        // Gap visual = hPuente - parante (ya incluye la holgura correcta)
        val gapPisoCm = altoHojaCm - CalculosPuerta.parante(altoHojaCm, pisoG)

        val bmp: Bitmap = if (nombreModelo == "Tere" && varianteSeleccionada == "Tere 6" && ajusteTere6 != null) {
            DibujoPuerta.generarBitmapTere6(
                context = this,
                marcoCm = marco,
                puenteCm = puente,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = altoHojaCm,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                pisoCm = gapPisoCm,
                nZocalo = nZRender,
                cantidadTubos = ajusteTere6.cantidadTubos,
                tuboCm = interior
            )
        } else if (nombreModelo == "Tere") {
            DibujoPuerta.generarBitmapTere(
                context = this,
                marcoCm = marco,
                puenteCm = puente,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = altoHojaCm,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                pisoCm = gapPisoCm,
                nZocalo = nZRender
            )
        } else if (nombreModelo == "Dora") {
            DibujoPuerta.generarBitmapDora(
                context = this,
                marcoCm = marco,
                puenteCm = puente,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = altoHojaCm,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                pisoCm = gapPisoCm,
                nZocalo = CalculosPuerta.nZocalo(nZocalos),
                nInoxIzq = nDiv.takeIf { it >= 1 } ?: 9,
                inoxCm = inox
            )
        } else if (nombreModelo == "jeny") {
            DibujoPuerta.generarBitmapJeny(
                context = this,
                marcoCm = marco,
                puenteCm = puente,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = altoHojaCm,
                nDivisiones = nDiv.takeIf { it >= 1 } ?: 1,
                rejillaCols = rejillaCols(),
                rejillaFilas = rejillaFilas(),
                variante = varianteSeleccionada,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                pisoCm = gapPisoCm,
                nZocalo = CalculosPuerta.nZocalo(nZocalos)
            )
        } else if (nombreModelo == "Mili") {
            DibujoPuerta.generarBitmapMili(
                context = this,
                marcoCm = marco,
                puenteCm = puente,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = altoHojaCm,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                pisoCm = gapPisoCm,
                nZocalo = CalculosPuerta.nZocalo(nZocalos)
            )
        } else if (nombreModelo == "Adel") {
            val hHojaAdel = CalculosLina.hojaH(altoPuertaCm, binding.etHoja.text.toString().toFloatOrNull() ?: 0f, false)
            val nDivAdel  = nDiv.takeIf { it >= 1 } ?: 3        // divisiones de vidrio (columna derecha)
            DibujoPuerta.generarBitmapAdel(
                context = this,
                marcoCm = marco,
                puenteCm = puente,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = hHojaAdel,
                nDivisiones = nDivAdel,
                variante = varianteSeleccionada,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                pisoCm = gapPisoCm,
                nZocalo = CalculosPuerta.nZocalo(nZocalos)
            )
        } else if (nombreModelo == "Lina") {
            val marcoLina = marco  // marco del diálogo de variantes
            val grunaLina = binding.etZocalo.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: 0.8f
            val panelDelgadoLina = binding.etPanelDelgadoLina.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: 0f
            val hHojaInputLina = binding.etHoja.text.toString().toFloatOrNull() ?: 0f
            val hHojaLina = if (varianteSeleccionada == "Lina b") {
                CalculosLina.hojaHConPiso(altoPuertaCm, hHojaInputLina, pisoG, false, marcoLina)
            } else {
                CalculosLina.hojaH(altoPuertaCm, hHojaInputLina, varianteSeleccionada == "Lina p", marcoLina)
            }
            val pisoLina = if (varianteSeleccionada == "Lina b") {
                hHojaLina - CalculosPuerta.parante(hHojaLina, pisoG)
            } else {
                gapPisoCm
            }
            val nPanelesLina = nDiv.takeIf { it >= 3 } ?: 5
            DibujoPuerta.generarBitmapLinaH(
                context = this,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = hHojaLina,
                nPaneles = nPanelesLina,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                marcoCm = marcoLina,
                pisoCm = pisoLina,
                grumaCm = grunaLina,
                panelDelgadoCm = panelDelgadoLina,
                mostrarVidrioCentral = varianteSeleccionada != "Lina b",
                puenteCm = puente,
                panelCompleto = varianteSeleccionada == "Lina c",
                bastidorCm = bastidor,
                estructuraCm = interior
            )
        } else if (varianteSeleccionada.startsWith("Taly")) {
            DibujoPuerta.generarBitmapTaly(
                context = this,
                marcoCm = marco,
                puenteCm = puente,
                anchoPuertaCm = anchoPuertaCm,
                altoPuertaCm = altoPuertaCm,
                anchoHojaCm = anchoHojaCm,
                altoHojaCm = altoHojaCm,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                numeroDivisiones = nDiv,
                anguloGrados = if (varianteSeleccionada == "Taly d") angulo else 0f,
                marcoCmIzq = marcoIzqRender,
                marcoCmDer = marcoDerRender,
                nZocalo = CalculosPuerta.nZocalo(nZocalos),
                pisoCm = gapPisoCm,
                bastidorCm = bastidor,
            )
        } else {
            DibujoPuerta.generarBitmapPuerta(
                context = this,
                marcoCm = marco,
                puenteCm = puente,
                anchoPuertaCm = anchoPuertaCm,
                altoPuertaCm = altoPuertaCm,
                anchoHojaCm = anchoHojaCm,
                altoHojaCm = altoHojaCm,
                numeroZocalos = CalculosPuerta.nZocalo(nZocalos),
                numeroDivisiones = nDiv,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                tipoDivision = tipoDivision,
                anguloGrados = angulo,
                marcoCmIzq = marcoIzqRender,
                marcoCmDer = marcoDerRender,
                pisoCm = gapPisoCm,
                bastidorCm = bastidor,
                // Viky y Mari arman sus divisiones con el aluminio del interior. El resto de los
                // modelos todavía los dibuja con el bastidor.
                interiorCm = if (nombreModelo == "Viky" || nombreModelo == "Mari") interior else bastidor
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
        "Lina" -> listOf(
            Variante("Lina h", R.drawable.pjalina),
            Variante("Lina b", R.drawable.pjose),
            Variante("Lina c", R.drawable.pjlinac),
            Variante("Lina p", R.drawable.pjosed)
        )
        "Mili" -> listOf(Variante("Mili", R.drawable.pmili))
        // Las dos variantes nuevas todavía dibujan la rejilla parametrizada; sus patrones propios
        // entran cuando lleguen los SVG del taller.
        "jeny" -> listOf(
            Variante("Jeny", R.drawable.pjenny),
            Variante("Jeny c", R.drawable.pjenyc),
            Variante("Jeny r", R.drawable.pjenyr)
        )
        "Dora" -> listOf(Variante("Dora", R.drawable.pdora))
        "Tere" -> listOf(
            Variante("Tere", R.drawable.ptere),
            Variante("Tere 6", R.drawable.tere6)
        )
        "Viky" -> listOf(
            Variante("Viky", R.drawable.pvicky),
            Variante("Viky c", R.drawable.pvickyc)
        )
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

        // Filas [ etiqueta ] [ campo corto ] [ ▾ ] para los valores que aplican a TODOS los modelos.
        val dp = resources.displayMetrics.density
        fun filaValor(
            etiqueta: String, valor: Float, usarCanto: Boolean = false, esMarco: Boolean = false,
            // La holgura no es un perfil del catálogo: no lleva desplegable, solo el hueco para que
            // los campos sigan alineados con los demás.
            conPerfiles: Boolean = true
        ): Pair<android.widget.LinearLayout, android.widget.EditText> {
            val fila = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding(24, 4, 24, 4)
            }
            // La etiqueta se queda con el espacio libre; el número es corto y no lo necesita.
            fila.addView(android.widget.TextView(this).apply {
                text = etiqueta
                setPadding(0, 0, 16, 0)
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            val et = android.widget.EditText(this).apply {
                inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                // Sin redondear: acá el número no se muestra, se EDITA. Con df1 el bastidor de 8.25
                // aparecía como 8.3 y al aceptar el diálogo quedaba en 8.3, moviendo todo el cálculo.
                setText(valorEditable(valor))
                setEms(4)   // entra "8.25" y nada más
                gravity = android.view.Gravity.END
            }
            fila.addView(et)
            // Desplegable con los perfiles del taller: elegir uno escribe su medida en el campo.
            fila.addView(android.widget.TextView(this).apply {
                text = if (conPerfiles) "▾" else ""
                textSize = 26f
                gravity = android.view.Gravity.CENTER
                minWidth = (48 * dp).toInt()
                minHeight = (48 * dp).toInt()
                if (conPerfiles) setOnClickListener { elegirPerfil(et, usarCanto, conCanales = esMarco) }
            })
            return fila to et
        }
        // En el marco y el puente manda el canto del perfil; el marco además suma los canales.
        val (filaMarco, etMarco) = filaValor("Marco (cm)", marco, usarCanto = true, esMarco = true)
        val (filaPuente, etPuente) = filaValor("Puente (cm)", puente, usarCanto = true)
        val (filaInox, etInox) = filaValor("Inox (cm)", inox)
        // Los dos perfiles de la hoja: el cuadro de afuera y lo que va adentro. Valen para todos los
        // modelos, así que no hay filas por modelo.
        val (filaBastidor, etBastidor) = filaValor("Bastidor (cm)", bastidor)
        val (filaInterior, etInterior) = filaValor("Interior (cm)", interior)
        // Juego hoja–marco: no es un perfil, es cuánto se le deja a la hoja para que entre y gire.
        val (filaHolgura, etHolgura) = filaValor("Holgura (cm)", holgura, conPerfiles = false)

        // Lo que hay escrito en los campos ahora mismo. Sirve tanto para aplicar como para guardar
        // la configuración, así se puede guardar sin tener que aceptar el diálogo primero.
        fun valoresDeCampos() = ConfigPuerta.Valores(
            marco = etMarco.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: marco,
            puente = etPuente.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: puente,
            inox = etInox.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: inox,
            bastidor = etBastidor.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: bastidor,
            interior = etInterior.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: interior,
            // La holgura sí puede ser 0: hay marcos que reciben la hoja sin juego.
            holgura = etHolgura.text.toString().toFloatOrNull()?.takeIf { it >= 0f } ?: holgura
        )

        fun escribirEnCampos(v: ConfigPuerta.Valores) {
            etMarco.setText(valorEditable(v.marco))
            etPuente.setText(valorEditable(v.puente))
            etInox.setText(valorEditable(v.inox))
            etBastidor.setText(valorEditable(v.bastidor))
            etInterior.setText(valorEditable(v.interior))
            etHolgura.setText(valorEditable(v.holgura))
        }

        fun aplicarValores() {
            val v = valoresDeCampos()
            marco = v.marco; puente = v.puente; inox = v.inox
            bastidor = v.bastidor; interior = v.interior; holgura = v.holgura
            // Se recuerda lo último usado para no volver a configurarlo en la próxima medición.
            ConfigPuerta.recordarUltima(this, v)
        }

        // Fila de configuraciones guardadas: cargar una o guardar la actual con un nombre.
        val filaConfig = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(24, 8, 24, 8)
            addView(android.widget.TextView(this@PuertasActivity).apply {
                text = "Configuración ▾"
                textSize = 16f
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener { elegirConfiguracion { v -> escribirEnCampos(v) } }
            })
            addView(android.widget.TextView(this@PuertasActivity).apply {
                text = "Guardar"
                textSize = 16f
                setPadding((12 * dp).toInt(), (8 * dp).toInt(), (12 * dp).toInt(), (8 * dp).toInt())
                setOnClickListener { guardarConfiguracion(valoresDeCampos()) }
            })
        }

        val rv = androidx.recyclerview.widget.RecyclerView(this).apply {
            layoutManager = GridLayoutManager(this@PuertasActivity, 2)
            setPadding(16, 8, 16, 16)
            // Va dentro de un ScrollView: la grilla se muestra entera y quien desplaza es el diálogo.
            isNestedScrollingEnabled = false
        }
        val contenedor = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            addView(filaVentana)
            addView(filaConfig)
            addView(filaMarco)
            addView(filaPuente)
            addView(filaInox)
            addView(filaBastidor)
            addView(filaInterior)
            addView(filaHolgura)
            addView(rv)
        }
        // Con los campos de perfiles el diálogo ya no entra en una pantalla chica.
        val scroll = android.widget.ScrollView(this).apply { addView(contenedor) }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Variantes — ${puertaActual?.nombre}")
            .setView(scroll)
            .setPositiveButton("OK") { _, _ ->
                ventanaIzquierda = cbIzq.isChecked
                ventanaDerecha = cbDer.isChecked
                aplicarValores()
            }
            .setNegativeButton("Cerrar", null)
            .create()

        rv.adapter = VariantesAdapter(variantes) { v ->
            val cambioDeVariante = v.nombre != varianteSeleccionada
            varianteSeleccionada = v.nombre
            binding.ivModelo.setImageResource(v.imagen)
            ventanaIzquierda = cbIzq.isChecked
            ventanaDerecha = cbDer.isChecked
            aplicarValores()
            // Cada variante arranca con el interior que le corresponde, pero SOLO al cambiar de
            // variante: si se toca la que ya estaba, manda lo que el vidriero acaba de escribir.
            if (cambioDeVariante) interiorPorOmision(v.nombre)?.let { interior = it }
            actualizarVisibilidades()
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

    private fun escaparCampoArchivo(raw: String): String {
        return raw
            .replace("\n", " / ")
            .replace("\r", " ")
            .replace("-", "_")
            .replace("<", "(")
            .replace(">", ")")
            .trim()
    }

    private fun etiquetaPaquete(prefijo: String, numero: Int): String {
        // El id del paquete usa el PROYECTO activo, nunca el cliente de tvTitulo/clienteActual.
        val proyecto = ProyectoManager.getProyectoActivo().orEmpty().ifBlank { "sin proyecto" }
        return "$prefijo$numero, $proyecto"
    }

    private fun sufijoMetadatosProduccion(): String {
        return "-MAT<alu:${metaColorAluminio.ifBlank { "null" }};" +
            "vid:${metaTipoVidrio.ifBlank { "null" }};" +
            "acabado_sup:${metaAcabadoSuperficial.ifBlank { "null" }};" +
            "obs:${metaObservaciones.ifBlank { "null" }}>"
    }

    // Descriptor de la puerta actual (parámetros para regenerar el gráfico/plano).
    private fun descriptorDatosActual(): PuertaDescriptor.Datos = PuertaDescriptor.Datos(
        modelo = puertaActual?.nombre.orEmpty(),
        variante = varianteSeleccionada,
        ancho = binding.etMed1.text.toString().toFloatOrNull() ?: 0f,
        alto = binding.etMed2.text.toString().toFloatOrNull() ?: 0f,
        hoja = binding.etHoja.text.toString().toFloatOrNull() ?: 0f,
        piso = binding.etPiso.text.toString().toFloatOrNull() ?: 0f,
        zocalos = binding.etZocalo.text.toString().toIntOrNull() ?: 0,
        divisiones = binding.etDivi.text.toString().toIntOrNull() ?: 0,
        angulo = if (varianteUsaAngulo()) binding.etAngulo.text.toString().toFloatOrNull() ?: 0f else 0f,
        marco = marco,
        puente = puente,
        inox = inox,
        ventanaIzq = ventanaIzquierda,
        ventanaDer = ventanaDerecha,
        cliente = ProyectoManager.getProyectoActivo().orEmpty(),
        interior = interior,
        rejillaCols = rejillaCols(),
        rejillaFilas = rejillaFilas(),
        holgura = holgura
    )

    private fun descriptorPuertaActual(): String = PuertaDescriptor.serializar(descriptorDatosActual())

    private fun disenoSimbolicoV2(numeroProducto: Int): String {
        val cliente = escaparCampoArchivo(
            clienteActual.ifBlank {
                binding.txCliente.text?.toString()?.trim().orEmpty()
            }.ifBlank {
                ProyectoManager.getProyectoActivo().orEmpty()
            }.ifBlank {
                "sin cliente"
            }
        )
        val ancho = binding.etMed1.text?.toString()?.toFloatOrNull() ?: 0f
        val alto = binding.etMed2.text?.toString()?.toFloatOrNull() ?: 0f
        val hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
        val cantidad = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        val modelo = escaparCampoArchivo("${puertaActual?.nombre.orEmpty()} ${varianteSeleccionada}".trim()).ifBlank { "x" }
        return buildString {
            append("C<").append(cliente).append(">")
            append("-M<").append(CalculosPuerta.df1(ancho)).append(",").append(CalculosPuerta.df1(alto)).append(",").append(CalculosPuerta.df1(hoja))
            append(",null,null,").append(cantidad).append(">")
            append("-P<P,p,a,p,").append(numeroProducto).append(">")
            append("-G<p,r,m,").append(modelo).append(">")
            append(sufijoMetadatosProduccion())
        }
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
        val etAcabadoSup = android.widget.EditText(this).apply {
            hint = "Acabado superficial (opcional)"
            setText(metaAcabadoSuperficial)
        }
        val etObs = android.widget.EditText(this).apply {
            hint = "Observaciones (opcional)"
            setText(metaObservaciones)
        }
        contenedor.addView(etColor)
        contenedor.addView(etVidrio)
        contenedor.addView(etAcabadoSup)
        contenedor.addView(etObs)

        // Cuando hay dos listas de vidrio (detalle pieza por pieza y apilado) se elige aquí cuál se
        // archiva. Las dos vienen marcadas: destildar una es lo excepcional.
        val hayDosListas = binding.lyVidriosApilado.visibility == View.VISIBLE
        val chkDetalle = android.widget.CheckBox(this).apply {
            text = "Archivar vidrios (detalle por pieza)"
            isChecked = archivarVidrioDetalle
        }
        val chkApilado = android.widget.CheckBox(this).apply {
            text = "Archivar vidrio apilado"
            isChecked = archivarVidrioApilado
        }
        if (hayDosListas) {
            contenedor.addView(chkDetalle)
            contenedor.addView(chkApilado)
        }

        fun guardarSeleccionVidrios() {
            // Sin las dos listas no hay nada que elegir: se archiva lo de siempre, para que una
            // casilla destildada en "Mari d" no deje sin vidrios a las demás variantes.
            archivarVidrioDetalle = !hayDosListas || chkDetalle.isChecked
            archivarVidrioApilado = !hayDosListas || chkApilado.isChecked
        }

        AlertDialog.Builder(this)
            .setTitle("Metadatos de producción")
            .setView(contenedor)
            .setPositiveButton("Guardar y archivar") { _, _ ->
                metaColorAluminio = etColor.text?.toString()?.trim().orEmpty()
                metaTipoVidrio = etVidrio.text?.toString()?.trim().orEmpty()
                metaAcabadoSuperficial = etAcabadoSup.text?.toString()?.trim().orEmpty()
                metaObservaciones = etObs.text?.toString()?.trim().orEmpty()
                guardarSeleccionVidrios()
                onContinuar()
            }
            .setNeutralButton("Omitir") { _, _ ->
                guardarSeleccionVidrios()
                onContinuar()
            }
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
            val identificadorPaquete = etiquetaPaquete(prefijo, siguienteNumero)
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
            if (esValido(binding.lyInox)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txInox, binding.tvInox, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyJunki)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txJunki, binding.tvJunki, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyTope)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txTope, binding.tvTope, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyMela)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txMela, binding.tvMela, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyMel)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txMel, binding.tvMel, mapListas, identificadorPaquete)
            }
            if (archivarVidrioDetalle && esValido(binding.lyVidrios)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txVidrios, binding.tvVidrios, mapListas, identificadorPaquete)
            }
            if (archivarVidrioApilado && esValido(binding.lyVidriosApilado)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txVidriosApilado, binding.tvVidriosApilado, mapListas, identificadorPaquete)
            }
            // Sin check: la mocheta se archiva siempre que exista.
            if (esValido(binding.lyVidrioMocheta)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txVidrioMocheta, binding.tvVidrioMocheta, mapListas, identificadorPaquete)
            }
            val paqueteV2 = disenoSimbolicoV2(siguienteNumero)
            mapListas.getOrPut("DisenoSimbolicoV2") { mutableListOf() }
                .add(mutableListOf(paqueteV2, "", identificadorPaquete))

            // Descriptor para REGENERAR el gráfico de la puerta (sin guardar imágenes).
            mapListas.getOrPut(PuertaDescriptor.CLAVE) { mutableListOf() }
                .add(mutableListOf(descriptorPuertaActual(), "", identificadorPaquete))

            ProyectoManager.actualizarContadorPorPrefijo(this, prefijo, siguienteNumero)
        }

        MapStorage.guardarMap(this, mapListas)
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)

        val msg = if (cant > 1) "Archivadas $cant unidades en proyecto: ${ProyectoManager.getProyectoActivo()}"
                  else "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        controladorCola.ofrecerSiguiente()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            val perfiles = mapOf(
                "Marco" to ModoMasivoHelper.texto(binding.tvMarco),
                "Tubo" to ModoMasivoHelper.texto(binding.tvTubo),
                "Paflón" to ModoMasivoHelper.texto(binding.tvPaflon),
                "Inoxidable" to ModoMasivoHelper.texto(binding.tvInox)
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
