package crystal.crystal.taller.nova

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.Diseno.nova.DisenoNovaActivity
import crystal.crystal.FichaActivity
import crystal.crystal.R
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityNovaInaBinding
import crystal.crystal.taller.nova.NovaUIHelper.esValido

class NovaIna : AppCompatActivity() {

    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private lateinit var binding: ActivityNovaInaBinding
    private var grados: Int = 0
    private var diseno: String = ""
    private var texto: String = ""
    private var otros: Boolean = false
    private var contadorLado = 1
    private var maxLados = -1
    private var primerClickArchivarRealizado = false

    // ==================== VARIABLES PARA SISTEMA DE PROYECTOS ====================
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback
    private val lanzarDiseno = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == RESULT_OK) {
            val uri = res.data?.data ?: return@registerForActivityResult
            if (uri.toString().endsWith(".svg")) {
                // Mostrar SVG (vector) en el ImageView
                val input = contentResolver.openInputStream(uri)!!
                val svg = com.caverock.androidsvg.SVG.getFromInputStream(input)
                val picture = svg.renderToPicture()
                val drawable = android.graphics.drawable.PictureDrawable(picture)
                // Importante: deshabilitar HW para PictureDrawable
                binding.ivDiseno.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                binding.ivDiseno.scaleType = ImageView.ScaleType.FIT_CENTER
                binding.ivDiseno.setImageDrawable(drawable)
            } else {
                // PNG
                binding.ivDiseno.setImageURI(uri)
            }
        }
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaInaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el manager de proyectos
        ProyectoManager.inicializarDesdeStorage(this)

        // Configurar callback para cambios de proyecto
        proyectoCallback = ProyectoUIHelper.crearCallbackConActualizacionUI(
            context = this,
            textViewProyecto = binding.tvProyectoActivo,
            activity = this
        )
        // Asegurar inicialización
        texto = "nn"
        diseno = "ic_fichad3a"
        otros = false

        modelos()

        // Configurar el TextView que muestra el proyecto activo
        ProyectoUIHelper.configurarVisorProyectoActivo(this, binding.tvProyectoActivo)

        // Verificar si hay proyecto activo al inicio
        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoGestionProyectos(this, proyectoCallback)
        }

        // Procesar proyecto enviado desde MainActivity si existe
        procesarIntentProyecto(intent)

        // ==================== CONFIGURACIÃ“N ORIGINAL USANDO CLASES UTILITARIAS ====================

        // Configurar cliente usando la funciÃ³n centralizada
        NovaUIHelper.configurarCliente(
            intent = intent,
            lyCliente = binding.lyCliente,
            tvTitulo = binding.tvId,
            clienteEditxt = binding.clienteEditxt,
            btGo = binding.btGo,
            tipoVentana = "nova inaparente"
        )

        // ==================== CONFIGURACIÃ“N INICIAL OPTIMIZADA ====================

        // Solo mantener lyU visible - los otros layouts se eliminarÃ¡n del XML
        binding.lyU.visibility = View.VISIBLE

        calcular()

        // ==================== LISTENERS MODIFICADOS CON VERIFICACIÃ“N DE PROYECTO ====================

        binding.textView7.setOnClickListener {
            // Verificar proyecto activo antes de navegar a FichaActivity
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) {
                return@setOnClickListener
            }

            if (binding.etAncho.text.isNotEmpty()) {
                navegarAFicha()
            } else {
                Toast.makeText(this, "Olvidaste ingresar datos", Toast.LENGTH_LONG).show()
            }
        }

        binding.btArchivar.setOnClickListener {
            // Validar que se hayan ingresado nuevos datos antes de archivar
            if (binding.etAncho.text.toString().isEmpty() || binding.etAncho.text.toString() == "") {
                Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!primerClickArchivarRealizado) {
                // Primera vez - mostrar diálogo
                DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                    override fun onProyectoSeleccionado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true

                        // CARGAR Map existente del proyecto seleccionado
                        val mapExistente = MapStorage.cargarProyecto(this@NovaIna, nombreProyecto)
                        if (mapExistente != null) {
                            mapListas.clear()
                            mapListas.putAll(mapExistente)
                        }

                        archivarMapas()
                        ProyectoUIHelper.actualizarVisorProyectoActivo(this@NovaIna, binding.tvProyectoActivo)
                        Toast.makeText(this@NovaIna, "Archivado", Toast.LENGTH_SHORT).show()
                        binding.etAncho.setText("")
                        binding.etAlto.setText("")
                    }

                    override fun onProyectoCreado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true

                        // Para proyecto nuevo, Map inicia vacío (correcto)
                        archivarMapas()
                        ProyectoUIHelper.actualizarVisorProyectoActivo(this@NovaIna, binding.tvProyectoActivo)
                        Toast.makeText(this@NovaIna, "Archivado", Toast.LENGTH_SHORT).show()
                        binding.etAncho.setText("")
                        binding.etAlto.setText("")
                    }

                    override fun onProyectoEliminado(nombreProyecto: String) {
                        ProyectoUIHelper.actualizarVisorProyectoActivo(this@NovaIna, binding.tvProyectoActivo)
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
                            val mapExistente = MapStorage.cargarProyecto(this@NovaIna, nombreProyecto)
                            if (mapExistente != null) {
                                mapListas.clear()
                                mapListas.putAll(mapExistente)
                            }

                            archivarMapas()
                            ProyectoUIHelper.actualizarVisorProyectoActivo(this@NovaIna, binding.tvProyectoActivo)
                            Toast.makeText(this@NovaIna, "Archivado", Toast.LENGTH_SHORT).show()
                            binding.etAncho.setText("")
                            binding.etAlto.setText("")
                        }

                        override fun onProyectoCreado(nombreProyecto: String) {
                            primerClickArchivarRealizado = true

                            archivarMapas()
                            ProyectoUIHelper.actualizarVisorProyectoActivo(this@NovaIna, binding.tvProyectoActivo)
                            Toast.makeText(this@NovaIna, "Archivado", Toast.LENGTH_SHORT).show()
                            binding.etAncho.setText("")
                            binding.etAlto.setText("")
                        }

                        override fun onProyectoEliminado(nombreProyecto: String) {
                            ProyectoUIHelper.actualizarVisorProyectoActivo(this@NovaIna, binding.tvProyectoActivo)
                        }
                    })
                    return@setOnClickListener
                }

                // Ya se realizó el primer click Y hay proyecto activo - archivar directamente
                archivarMapas()
                Toast.makeText(this, "Archivado", Toast.LENGTH_SHORT).show()
                binding.etAncho.setText("")
                binding.etAlto.setText("")
            }
        }

        binding.btArchivar.setOnLongClickListener {
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) {
                return@setOnLongClickListener true
            }

            // Guardar en el proyecto activo
            MapStorage.guardarMap(this, mapListas)
            Toast.makeText(this, "Map guardado en proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()

            // Actualizar el visor del proyecto
            ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
            true
        }

       /* binding.ivDiseno.setOnLongClickListener  {
            startActivity(Intent(this, FichaActivity::class.java))
            true
        }*/

        binding.ivDiseno.setOnClickListener {
            modelos()
            startActivity(
                Intent(this, DisenoNovaActivity::class.java).apply {
                    putExtra(DisenoNovaActivity.EXTRA_PAQUETE, disenoSimbolico())
                    putExtra(DisenoNovaActivity.EXTRA_MOCHETA_LATERAL_CM, 0f)
                    putExtra(DisenoNovaActivity.EXTRA_HEADLESS, false)
                }
            )

        }

        binding.ivDiseno.setOnLongClickListener { view ->
            view.animate().rotationBy(180f).setDuration(500).start()
            grados = (grados + 180) % 360
            Toast.makeText(this, "Grados: $grados", Toast.LENGTH_SHORT).show()
            true
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let { ProyectoUIHelper.agregarOpcionesMenuProyecto(it) }
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val manejado = ProyectoUIHelper.manejarSeleccionMenu(
            context = this,
            itemId = item.itemId,
            callback = proyectoCallback,
            onProyectoCambiado = {
                ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
            }
        )

        return if (manejado) true else super.onOptionsItemSelected(item)
    }
    // ==================== FUNCIONES PARA RECIBIR PROYECTO DESDE MAINACTIVITY ====================

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        procesarIntentProyecto(intent)
    }
    override fun onResume() {
        super.onResume()
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
    }
    private fun procesarIntentProyecto(intent: Intent) {
        NovaUIHelper.procesarIntentProyecto(this, intent) { nombreProyecto ->
            ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
        }
    }
    //FUNCIONES CLICKS
    // 2) Tu función 'calcular()' lanzando la actividad de dibujo
    private fun calcular() {
        binding.btCalcular.setOnClickListener {
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnClickListener
            try {
                configurarU()
                aVisible()
                dVisible()
                otrosAluminios()
                vidrios()
                referencias()
                val intent = Intent(this, DisenoNovaActivity::class.java).apply {
                    putExtra(DisenoNovaActivity.EXTRA_PAQUETE, disenoSimbolico())
                    putExtra(DisenoNovaActivity.EXTRA_HEADLESS, true)
                    putExtra(DisenoNovaActivity.EXTRA_OUTPUT_FORMAT, "svg") // 👈 vector
                    putExtra(DisenoNovaActivity.EXTRA_RET_PADDING_PX, 4)
                }
                lanzarDiseno.launch(intent)

                binding.textView28.text= disenoSimbolico()

            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //FUNCIONES PUBLICADAS USANDO CLASES UTILITARIAS
    private fun puntosU(): String {
        val ancho = binding.etAncho.text.toString().toFloat()
        val divisManual = binding.partesNfcfi.text.toString().toInt()
        val cruce = cruce()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val partes = NovaCalculos.uFijos(ancho, divisiones, cruce)

        val punto1 = NovaCalculos.df1((partes * 2) - cruce * 2).toFloat()
        val punto2 = NovaCalculos.df1((partes * 4) - cruce * 4).toFloat()
        val punto3 = NovaCalculos.df1((partes * 6) - cruce * 6).toFloat()

        return when (divisiones) {
            5, 6 -> NovaCalculos.df1(((partes * 2) - cruce * 2))
            8, 12 -> NovaCalculos.df1(((partes * 3) - cruce * 2))
            7, 10, 14 -> "${NovaCalculos.df1(punto1)}_${NovaCalculos.df1(punto2)}"
            9, 11, 13, 15 -> "${NovaCalculos.df1(punto1)}_${NovaCalculos.df1(punto2)}_${NovaCalculos.df1(punto3)}"
            else -> ""
        }
    }
    private fun disenoSimbolico(): String {
        val ancho = binding.etAncho.text.toString().toFloat()
        val alto = binding.etAlto.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val divisManual = binding.partesNfcfi.text.toString().toInt()

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val diseno = NovaUIHelper.generarDiseno(ancho,alto,altoHoja,divisiones,siNoMoch=1,texto, anchMota=10)

        return "{nova,ina,[$diseno]}"
    }
    @SuppressLint("SetTextI18n")
    private fun modelos() {
        binding.ivDiseno.visibility = View.VISIBLE
        binding.svModelos.visibility = View.GONE
        binding.ivDiseno.setImageResource(R.drawable.ic_fichad3a)
        texto = "nn"
        diseno = "ic_fichad3a"
        otros = false

        binding.btNovan.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.ic_fichad3a)
            texto = "nn"
            diseno = "ic_fichad3a"
            otros = false
        }
        binding.btNovar.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novair)
            texto = "nr"
            diseno = "novair"
            otros = false
        }
        binding.btNovaP2.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.nova2p)
            texto = "np"
            diseno = "nova2p"
            otros = false
        }

        binding.btNovacc.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novacc)
            texto = "ncc"
            diseno = "novacc"
            otros = true
        }

        binding.btNova3c.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.nova3c)
            texto = "n3c"
            diseno = "nova3c"
            otros = true
        }

        binding.btNovacfc.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novacfc)
            texto = "ncfc"
            diseno = "novacfc"
            otros = true
        }

        binding.btNoval.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.noval)
            texto = "nl"
            diseno = "noval"
            binding.lyAncho2.visibility = View.VISIBLE
            binding.lyDivi2.visibility = View.VISIBLE
            binding.lyFlecha.visibility = View.VISIBLE
            maxLados = 2
            contadorLado = 1
            binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
            binding.btAgregar.visibility = View.VISIBLE
            binding.btAgregar.isEnabled = true
            otros = true
        }

        binding.btNovau.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novau)
            texto = "nu"
            diseno = "novau"
            maxLados = 3
            contadorLado = 1
            binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
            binding.btAgregar.visibility = View.VISIBLE
            binding.btAgregar.isEnabled = true
            otros = true
        }

        binding.btNovas.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novas)
            texto = "ns"
            diseno = "novas"
            maxLados = -1
            contadorLado = 1
            binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
            binding.btAgregar.visibility = View.VISIBLE
            binding.btAgregar.isEnabled = true
            otros = true
        }

        binding.btNovacu.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novacu)
            texto = "ncu"
            diseno = "novacu"
            binding.lyFlecha.visibility = View.VISIBLE
            otros = true
        }

        binding.btNovaci.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novaci)
            texto = "nci"
            diseno = "novaci"
            otros = true
        }

        binding.ivDiseno.setOnClickListener {
            binding.ivDiseno.visibility = View.GONE
            binding.svModelos.visibility = View.VISIBLE
            binding.lyAncho2.visibility = View.GONE
            binding.lyDivi2.visibility = View.GONE
            binding.lyFlecha.visibility = View.GONE
            binding.tvMedidas.text = "Medidas y Cantidad"
            binding.btAgregar.visibility = View.GONE
            contadorLado = 1
            otros = false
        }
    }
    @SuppressLint("SetTextI18n")
    private fun referencias() {
        val ancho = binding.etAncho.text.toString().toFloat()
        val alto = binding.etAlto.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val divisManual = binding.partesNfcfi.text.toString().toInt()

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val siNoMoch = NovaCalculos.siNoMoch(alto, hoja)
        val nFijos = NovaCalculos.nFijos(divisiones)
        val nCorredizas = NovaCalculos.nCorredizas(divisiones)
        val puntosU = if (divisiones > 4) puntosU() else ""

        binding.txReferencias.text = NovaUIHelper.generarReferencias(
            ancho, alto, altoHoja, divisiones, nFijos, nCorredizas, siNoMoch, puntosU
        )
    }
    @SuppressLint("SetTextI18n")
    private fun otrosAluminios() {
        val ancho = binding.etAncho.text.toString().toFloat()
        val alto = binding.etAlto.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val divisManual = binding.partesNfcfi.text.toString().toInt()
        val cruce = cruce()

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val nPuentes = NovaCalculos.nPuentes(divisiones)
        val mPuentes = NovaCalculos.mPuentes1(ancho, divisiones)
        val mPuentes2 = NovaCalculos.mPuentes2(ancho, divisiones)
        val nCorredizas = NovaCalculos.nCorredizas(divisiones)
        val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruce)
        val hache = NovaPerfilesHelper.calcularHache(uFijos)
        val portafelpa = NovaCalculos.portafelpa(altoHoja)

        // PUENTE
        binding.txT.text = NovaPerfilesHelper.calcularPuentes(alto, mPuentes, mPuentes2, nPuentes, divisiones)

        // RIEL
        binding.txR.text = if (divisiones == 1) {
            ""
        } else {
            NovaPerfilesHelper.calcularRieles(alto, hoja, ancho, mPuentes, mPuentes2, nPuentes, divisiones)
        }

        // U FELPERO
        binding.txUf.text = if (divisiones == 1) {
            ""
        } else {
            NovaPerfilesHelper.calcularRieles(alto, hoja, ancho, mPuentes, mPuentes2, nPuentes, divisiones)
        }

        // HACHE
        binding.txH.text = "${NovaCalculos.df1(hache)} = $nCorredizas"

        //ÃNGULO TOPE
        if (divisiones == 2) {
            binding.txTo.text = "${NovaCalculos.df1(altoHoja - 0.9f)} = 1"
        } else {
            binding.lyTo.visibility = View.GONE
        }

        // PORTAFELPA
        val divDePortas = NovaCalculos.divDePortas(divisiones, nCorredizas)
        binding.txPf.text = "${NovaCalculos.df1(portafelpa)} = $divDePortas"
    }
    private fun vidrios() {
        val ancho = binding.etAncho.text.toString().toFloat()
        val alto = binding.etAlto.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val us = binding.ueditxtNfcfi.text.toString().toFloat()
        val divisManual = binding.partesNfcfi.text.toString().toInt()
        val cruce = cruce()

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val nFijos = NovaCalculos.nFijos(divisiones)
        val nCorredizas = NovaCalculos.nCorredizas(divisiones)
        val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruce)
        val hache = NovaPerfilesHelper.calcularHache(uFijos)

        val vidrioFijo = NovaVidriosHelper.calcularVidriosFijos(
            uFijos, alto, us, nFijos, divisiones, "inaparente"
        )
        val vidrioCorre = NovaVidriosHelper.calcularVidriosCorredizos(hache, altoHoja, nCorredizas)
        val vidrioMocheta = calcularVidrioMochetaInaparente(ancho, alto, hoja, altoHoja, divisiones, nFijos, uFijos, nCorredizas)

        binding.txV.text = if (divisiones > 1) {
            "$vidrioFijo\n$vidrioCorre\n$vidrioMocheta"
        } else {
            vidrioFijo
        }
    }

    //FUNCIONES VISIBILIDAD OPTIMIZADAS - SOLO CAMBIA TEXTOS, NO LAYOUTS
    private fun configurarU() {
        val ancho = binding.etAncho.text.toString().toFloat()
        val alto = binding.etAlto.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val us = binding.ueditxtNfcfi.text.toString().toFloat()
        val divisManual = binding.partesNfcfi.text.toString().toInt()
        val cruce = cruce()

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)

        // Usar la funciÃ³n optimizada que maneja solo UN tipo de U
        NovaPerfilesHelper.calcularYConfigurarTextosU(
            us = us,
            ancho = ancho,
            alto = alto,
            hoja = hoja,
            divisiones = divisiones,
            cruce = cruce,
            altoHoja = altoHoja,
            tipoVentana = "ina",
            bindings = NovaPerfilesHelper.BindingsU(
                tvU = binding.tvU,  // Solo este TextView para la etiqueta
                txU = binding.txU   // Solo este TextView para los valores
                // Ya no necesitamos tvU38, txU38, tvUOtros, txUOtros
            )
        )
    }
    private fun aVisible() {
        val ancho = binding.etAncho.text.toString().toFloat()
        val alto = binding.etAlto.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val divisManual = binding.partesNfcfi.text.toString().toInt()

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)

        NovaSpinnerData.configurarVisibilidadInaparente(
            divisiones = divisiones,
            alto = alto,
            hoja = hoja,
            layouts = NovaSpinnerData.LayoutsInaparente(
                lyH = binding.lyH,
                lyTo = binding.lyTo,
                lyPf = binding.lyPf,
                lyUf = binding.lyUf,
                lyFijoCorre = binding.lyFijoCorre,
                lyTubo = binding.lyTubo
            )
        )
    }
    private fun dVisible() {
        val ancho = binding.etAncho.text.toString().toFloat()
        val alto = binding.etAlto.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val divisManual = binding.partesNfcfi.text.toString().toInt()

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val siNoMoch = NovaCalculos.siNoMoch(alto, hoja)
        val recursoDiseno = NovaUIHelper.obtenerRecursoDiseno(divisiones, siNoMoch, "ina", "ic_fichad")

        binding.ivDiseno.setImageResource(recursoDiseno)
    }

    //FUNCIONES VIDRIOS ESPECÍFICAS PARA INAPARENTE
    private fun calcularVidrioMochetaInaparente(
        ancho: Float, alto: Float, hoja: Float, altoHoja: Float,
        divisiones: Int, nFijos: Int, uFijos: Float, nCorredizas: Int
    ): String {
        val mas1 = NovaCalculos.df1((alto - altoHoja) + 1).toFloat()
        val axnfxuf = NovaCalculos.df1(((ancho - (nFijos * uFijos))) - 0.6f).toFloat()
        val axnfxuf2 = NovaCalculos.df1(((ancho - (nFijos * uFijos)) / 2) - 0.6f).toFloat()
        val axnfxuivDiseno = NovaCalculos.df1(((ancho - (nFijos * uFijos)) / 3) - 0.6f).toFloat()
        val axnfxufn = NovaCalculos.df1(((ancho - (nFijos * uFijos)) / nCorredizas) - 0.6f).toFloat()

        return when {
            divisiones <= 1 || alto <= hoja -> ""
            divisiones == 4 -> "${NovaCalculos.df1(mas1)} x ${NovaCalculos.df1(axnfxuf)} = 1"
            divisiones == 8 -> "${NovaCalculos.df1(mas1)} x ${NovaCalculos.df1(axnfxuf2)} = 2"
            divisiones == 12 -> "${NovaCalculos.df1(mas1)} x ${NovaCalculos.df1(axnfxuivDiseno)} = 3"
            divisiones == 14 -> {
                "${NovaCalculos.df1(mas1)} x ${NovaCalculos.df1(axnfxuf2)} = 1\n" +
                        "${NovaCalculos.df1(mas1)} x ${NovaCalculos.df1(axnfxuf)} = 4"
            }
            else -> "${NovaCalculos.df1(mas1)} x ${NovaCalculos.df1(axnfxufn)} = $nCorredizas"
        }
    }

    // FUNCIONES PARA ARCHIVAR MAPAS USANDO LISTACASILLA
    private fun obtenerPrefijo(): String {
        return "NI" // NovaApa - en el futuro puede variar
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
        val identificadorPaquete = "v${siguienteNumero}${prefijo}"

        if (esValido(binding.lyReferencias)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvReferencias, binding.txReferencias, mapListas, identificadorPaquete)
        }
        if (esValido(binding.lyU)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvU, binding.txU, mapListas, identificadorPaquete) // u
        }
        if (esValido(binding.lyUf)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvUf, binding.txUf, mapListas, identificadorPaquete) // u felpero
        }
        if (esValido(binding.lyFijoCorre)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvFc, binding.txFc, mapListas, identificadorPaquete) // fijo corredizo
        }
        if (esValido(binding.lyRiel)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvR, binding.txR, mapListas, identificadorPaquete) // riel
        }
        if (esValido(binding.lyTubo)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvT, binding.txT, mapListas, identificadorPaquete) // tubo
        }
        if (esValido(binding.lyPf)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvPf, binding.txPf, mapListas, identificadorPaquete) // portafelpa
        }
        if (esValido(binding.lyTo)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvTo, binding.txTo, mapListas, identificadorPaquete) // tope
        }
        if (esValido(binding.lyH)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvH, binding.txH, mapListas, identificadorPaquete) // h
        }
        if (esValido(binding.lyVidrios)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvV, binding.txV, mapListas, identificadorPaquete) // vidrios
        }
        if (esValido(binding.lyClient)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvC, binding.txC, mapListas, identificadorPaquete) // cliente
        }
        if (esValido(binding.lyAncho)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvAncho, binding.txAncho, mapListas, identificadorPaquete) // ancho
        }
        if (esValido(binding.lyAlto)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvAlto, binding.txAlto, mapListas, identificadorPaquete) // alto
        }
        if (esValido(binding.lyPuente)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvPuente, binding.txPuente, mapListas, identificadorPaquete) // altura Puente
        }
        if (esValido(binding.lyDivisiones)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvDivisiones, binding.txDivisiones, mapListas, identificadorPaquete) // divisiones
        }
        if (esValido(binding.lyFijos)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvFijos, binding.txFijos, mapListas, identificadorPaquete) // nFijos
        }
        if (esValido(binding.lyCorredizas)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvCorredizas, binding.txCorredizas, mapListas, identificadorPaquete) // nCorredizas
        }
        if (esValido(binding.lyDiseno)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvDiseno, binding.txDiseno, mapListas, identificadorPaquete) // diseÃ±o
        }
        if (esValido(binding.lyGrados)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvGrados, binding.txGrados, mapListas, identificadorPaquete) // grados
        }
        if (esValido(binding.lyTipo)) {
            ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvTipo, binding.txTipo, mapListas, identificadorPaquete) // tipo de ventana
        }

        ProyectoManager.actualizarContadorPorPrefijo(this, prefijo, siguienteNumero)
        MapStorage.guardarMap(this, mapListas)
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
        binding.txPr.text = mapListas.toString()

        Toast.makeText(this, "Datos archivados como $identificadorPaquete en proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()

    }
    private fun navegarAFicha() {
        val paquete = Bundle().apply {
            // Ahora todos los valores de U vienen del mismo TextView
            putString("u38", binding.txU.text.toString())
            putString("u13", binding.txU.text.toString())
            putString("uMarco", binding.txU.text.toString())
            putString("tubo", binding.txT.text.toString())
            putString("riel", binding.txR.text.toString())
            putString("uFel", binding.txUf.text.toString())
            putString("fCo", binding.txFc.text.toString())
            putString("porta", binding.txPf.text.toString())
            putString("angT", binding.txTo.text.toString())
            putString("hache", binding.txH.text.toString())
            putString("vidrios", binding.txV.text.toString())
            putString("u", binding.ueditxtNfcfi.text.toString())
            putString("div", binding.partesNfcfi.text.toString())
            putString("ref", binding.txReferencias.text.toString())
            putString("si", NovaCalculos.siNoMoch(
                binding.etAlto.text.toString().toFloat(),
                binding.hojatxtNfcfsi.text.toString().toFloat()
            ).toString())
        }
        val intent = Intent(this, FichaActivity::class.java)
        intent.putExtras(paquete)
        startActivity(intent)
    }
    private fun cruce(): Float {
        val exacto = binding.etCruce.text.toString().toFloatOrNull() ?: 0f
        val ancho = binding.etAncho.text.toString().toFloat()
        val divisManual = binding.partesNfcfi.text.toString().toInt()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)

        return NovaCalculos.calcularCruce(exacto, divisiones)
    }
    // FUNCIÓN PARA GENERAR DISEÑO SIMBOLICO
    private fun f15(): String {
        val ancho = binding.etAncho.text.toString().toFloat()
        val alto = binding.etAlto.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val divisManual = binding.partesNfcfi.text.toString().toInt()

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val nCorredizas = NovaCalculos.nCorredizas(divisiones)
        val mF = NovaCalculos.portafelpa(altoHoja)
        val divDePortas = NovaCalculos.divDePortas(divisiones, nCorredizas)
        val xF = "$mF = $divDePortas"
        val totalF = NovaCalculos.df1(mF * divDePortas)
        return "$xF\n$totalF"
    }
}