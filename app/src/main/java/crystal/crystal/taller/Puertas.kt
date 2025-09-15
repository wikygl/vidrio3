package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.Diseno.DisenoActivity
import crystal.crystal.R
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityPuertaPanoBinding
import java.io.File
import java.io.FileOutputStream
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class Puertas : AppCompatActivity() {

    private val hoja = 199f
    private val marco = 2.2f
    private val bastidor = 8.25f
    private val unoMedio = 3.8f
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()

    private var client = ""
    private var indice = 0
    private var puertaActual: Puerta? = null
    private var varianteSeleccionada: String = "Mari h"
    private lateinit var binding: ActivityPuertaPanoBinding

    // ==================== NUEVAS VARIABLES PARA SISTEMA DE PROYECTOS ====================
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuertaPanoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ==================== CONFIGURACIÓN DEL SISTEMA DE PROYECTOS ====================

        // Inicializar el manager de proyectos
        ProyectoManager.inicializarDesdeStorage(this)

        // Configurar callback para cambios de proyecto
        proyectoCallback = ProyectoUIHelper.crearCallbackConActualizacionUI(
            context = this,
            textViewProyecto = binding.tvProyectoActivo, // Necesitas agregar este TextView al layout
            activity = this
        )

        // Verificar si hay proyecto activo al inicio
        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoGestionProyectos(this, proyectoCallback)
        }

        // Procesar proyecto enviado desde MainActivity si existe
        procesarIntentProyecto(intent)

        // ==================== CONFIGURACIÓN ORIGINAL ====================

        // Inicializa la vista con el primer elemento de listaPuertas y el cliente (si existe)
        indice = 0
        cliente()

        // Configura otros listeners, por ejemplo, para cambiar de puerta al hacer clic en ivModelo
        binding.ivModelo.setOnClickListener {
            if (listaPuertas.isNotEmpty()) {
                indice = (indice + 1) % listaPuertas.size
                tipos()  // Actualiza la vista con la siguiente puerta y agrega el cliente
            }
        }

        mostrarVariantes()

        // ==================== LISTENERS MODIFICADOS CON VERIFICACIÓN DE PROYECTO ====================

        binding.btCalcular.setOnClickListener {
            // Verificar proyecto activo antes de calcular
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) {
                return@setOnClickListener
            }

            try {
                val marcop = binding.etMed2.text.toString().toFloat()

                //OPCIONES DE VISIBILIDAD
                modelos()
                panos()
                array()

                // MATERIALES
                binding.tvMarco.text = "${df1(marcop)} = 2\n${df1(marcoSuperior())} = 1"

                paflonRes()
                junkillos()

                binding.tvTope.text = "${df1(marcoSuperior())} = 1\n${df1(hPuente())} = 2"

                vidrio()

                binding.txRefe.text = referen()
                if (tubo() == "") {
                    binding.lyTubo.visibility = View.GONE
                } else {
                    binding.lyTubo.visibility = View.VISIBLE
                }

                binding.tvTubo.text = "${tubo()} = 1"

                // Supongamos que ya tienes los datos del plano (por ejemplo, tras rotarlos)
                val angulo = binding.etAngulo.text.toString().toFloat()
                val datosRotados = rotarDatosPlano(generarDatosPlano(), angulo)

                // Obtén el String con las distancias
                val textoDistancias = obtenerTextoDistancias(datosRotados)

                // Asigna el resultado al TextView
                binding.tvMela.text = textoDistancias

                val resultado = calcularTriangulo(cateto = bastidor, anguloGrados = binding.etAngulo.text.toString().toFloat(), esAdyacente = true)

                val catetoConocido = resultado.first
                val catetoCalculado = resultado.second
                val hipotenusa = resultado.third

                binding.tvMel.text="$catetoConocido\n${df1(catetoCalculado)}\n${df1(hipotenusa)}"

            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btArchivar.setOnClickListener{
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) {
                return@setOnClickListener
            }
            if (binding.etMed1.text.toString()!=""){archivarMapas()
                Toast.makeText(this, "Archivado", Toast.LENGTH_SHORT).show()}
            else{
                Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
            }
            binding.etMed1.setText("")
            binding.etMed2.setText("")
        }

        binding.btArchivar.setOnLongClickListener {
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) {
                return@setOnLongClickListener true
            }

            // El guardado ahora usa automáticamente el proyecto activo
            MapStorage.guardarMap(this, mapListas)
            Toast.makeText(this, "Map guardado en proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()

            // Actualizar el visor del proyecto
            ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
            true
        }

        binding.ivModelo.setOnLongClickListener {
            startActivity(Intent(this, DisenoActivity::class.java))
            true
        }
    }

    // ==================== NUEVO MENÚ DE OPCIONES ====================

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

    // ==================== NUEVA FUNCIÓN ARCHIVAR PARA SISTEMA DE PROYECTOS ====================


    private fun archivarMapas() {
        // Usar el nuevo método que incluye context y proyecto activo
        ListaCasilla.incrementarContadorVentanas(this)

        // Caso especial para txReferencias
        /* if (esValido(binding.lyReferencias)) {
           ListaCasilla.procesarReferencias(this, binding.tvReferencias, binding.txReferencias, mapListas)
         }*/

        // Usar la clase ListaCasilla para procesar y archivar solo los TextView válidos
        if (esValido(binding.lyMarco)) {
            ListaCasilla.procesarArchivar(this, binding.txMarco, binding.tvMarco, mapListas) // marco
        }
        if (esValido(binding.lyPaflon)) {
            ListaCasilla.procesarArchivar(this, binding.txPaflon, binding.tvPaflon, mapListas) // paflon
        }
        if (esValido(binding.lyTubo)) {
            ListaCasilla.procesarArchivar(this, binding.txTubo, binding.tvTubo, mapListas) // ensayo
        }

        if (esValido(binding.lyJunki)) {
            ListaCasilla.procesarArchivar(this, binding.txJunki, binding.tvJunki, mapListas) // porta
        }
        if (esValido(binding.lyTope)) {
            ListaCasilla.procesarArchivar(this, binding.txTope, binding.tvTope, mapListas) // tope
        }
        if (esValido(binding.lyVidrios)) {
            ListaCasilla.procesarArchivar(this, binding.tvVidrios, binding.txVidrios, mapListas) // vidrios
        }


        // Actualizar el visor del proyecto después de archivar
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)

        // Aquí puedes hacer algo con `mapListas`, como mostrarlo o guardarlo
        println("Datos agregados al proyecto: ${ProyectoManager.getProyectoActivo()}")
        println(mapListas)
    }

    // Función para verificar si un Layout es visible o tiene estado GONE
    private fun esValido(ly: LinearLayout): Boolean {
        return ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
    }

    // ==================== FUNCIONES ORIGINALES SIN CAMBIOS ====================

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    private fun cliente() {
        binding.lyCliente.visibility = View.GONE
        val paqueteR = intent.extras
        // Intenta obtener el cliente desde el Intent (o de SharedPreferences)
        val clienteRecibido = paqueteR?.getString("rcliente")
        if (clienteRecibido != null) {
            client = clienteRecibido
        }
        // Actualiza la vista con el cliente actual
        tipos()

        // Si se hace clic en el título, se muestra el layout para editar el cliente
        binding.tvTitulo.setOnClickListener {
            binding.lyCliente.visibility = View.VISIBLE
            binding.clienteEditxt.setText(client)
            binding.btGo.setOnClickListener {
                // Actualiza el valor de 'client' con el texto introducido
                client = binding.clienteEditxt.text.toString()
                // Refresca la vista para que se muestre el nuevo valor
                tipos()
                binding.lyCliente.visibility = View.GONE
            }
        }
    }

    //FUNCIONES DE VISIBILIDAD
    @SuppressLint("SetTextI18n")
    private fun tipos() {
        if (listaPuertas.isNotEmpty()) {
            // Actualiza la puerta actual según el índice actual
            puertaActual = listaPuertas[indice]
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
            // Actualiza el título incorporando el cliente, si no es cadena vacía
            binding.tvTitulo.text = "Puerta ${puertaActual?.nombre}${if (client.isNotEmpty()) " ($client)" else ""}"
        }
        visibles()
    }

    private fun visibles() {
        // Se obtiene el nombre de la puerta actual (si existe)
        val nombre = puertaActual?.nombre ?: ""

        // Se establece la visibilidad de lyAD según el nombre
        binding.lyAD.visibility = if (nombre == "Viky" || nombre == "Adel") {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun mariModeos() {
        val anchoPuertaCm = binding.etMed1.text.toString().toFloatOrNull() ?: 0f
        val altoPuertaCm = binding.etMed2.text.toString().toFloatOrNull() ?: 0f
        val numeroZocalos = binding.etZocalo.text.toString().toIntOrNull() ?: 0
        val numeroDivisiones = binding.etDivi.text.toString().toIntOrNull() ?: 0

        val anchoContenedor = anchoPuertaCm * 3
        val altoContenedor = altoPuertaCm * 3

        // Supongamos que tienes una variable o lógica que determina la variante seleccionada,
        // por ejemplo, una cadena: "Mari h", "Mari v" o "Mari d".
        // Aquí se asume que la variable variante contiene ese valor.
        val variante = obtenerVarianteSeleccionada() // Debe devolver "Mari h", "Mari v" o "Mari d"

        // Seleccionar el tipo de división según la variante
        val tipoDivision = when (variante) {
            "Mari v" -> "V"
            "Mari d" -> "D"
            else -> "H"  // "Mari" o "Mari h" se asumen horizontales
        }

        val bitmapPuerta = generarBitmapPuerta(
            anchoPuertaCm = anchoPuertaCm,
            altoPuertaCm = altoPuertaCm,
            anchoHojaCm = anchoPuertaCm - ((marco * 2) + 1),
            altoHojaCm = hPuente(), // Asume que hPuente() calcula la altura de la hoja
            numeroZocalos = numeroZocalos,
            numeroDivisiones = numeroDivisiones,
            anchoContenedor = anchoContenedor,
            altoContenedor = altoContenedor,
            tipoDivision = tipoDivision
        )

        binding.ivModelo.setImageBitmap(bitmapPuerta)
        guardarBitmapEnCache(this, bitmapPuerta)
    }

    private fun tereModeos() {
        TODO("Not yet implemented")
    }

    private fun linaModeos() {
        TODO("Not yet implemented")
    }

    private fun vikyModeos(): String {
        val alto = binding.etDivi.text.toString().toInt()
        val hoja = if (mocheta() > 1) {
            ""
        } else {
            "c"
        }.toString()
        val div = "$alto$hoja"
        val drawableResource = when (div) {
            "1" -> R.drawable.pvicky
            else -> R.drawable.pvicky
        }

        // Cargar el drawable en el ImageView usando ViewBinding
        binding.ivModelo.setImageResource(drawableResource)

        // Retornar el nombre del drawable
        return when (div) {
            "1" -> "ic_fichad1a"
            else -> "ic_fichad5"
        }
    }

    private fun talyModeos() {
        TODO("Not yet implemented")
    }

    private fun miliModeos() {
        TODO("Not yet implemented")
    }

    private fun adelModeos() {
        TODO("Not yet implemented")
    }

    private fun jenyModeos() {
        TODO("Not yet implemented")
    }

    private fun doraModeos() {
        TODO("Not yet implemented")
    }

    // FUNCIONES REDONDEOS
    private fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    //FUNCIONES DE ALUMINIOS
    private fun junkillos() {
        val jun = binding.etJunki.text.toString().toFloat()
        val mJunki = df1(divisiones() - (2 * jun)).toFloat()
        val nombre = obtenerVarianteSeleccionada()

        binding.tvJunki.text = when(nombre){
            "Mari h" ->if (mocheta() < 0f) {
                "${df1(mJunki)} = ${(nPfvcal() - 1) * 2}\n${
                    df1(paflon())
                } = ${(nPfvcal() - 1) * 2}"
            } else {
                "${df1(mJunki)} = ${(nPfvcal() - 1) * 2}" +
                        "\n${df1(paflon())} = ${(nPfvcal() - 1) * 2}\n" +
                        "${df1(marcoSuperior())} = 2\n" +
                        "${df1(mocheta() - (2 * jun))} = 2"
            }
            "Mari v" -> if (mocheta() < 0f){
                "${df1((paflon()-(bastidor*(divi()-1)))/divi())} = ${divi()*2}" +
                        "\n${df1(paranteInterno()-(2*jun))} = ${divi()*2}"
            } else{"${df1((paflon()-(bastidor*(divi()-1)))/divi())} = ${divi()*2}" +
                    "\n${df1(paranteInterno()-(2*jun))} = ${divi()*2}" +
                    "\n${df1(marcoSuperior())} = 2\n" +
                    "${df1(mocheta() - (2 * jun))} = 2"}

            else -> {""}
        }
    }

    private fun marcoSuperior(): Float {
        val ancho = binding.etMed1.text.toString().toFloat()
        return ancho - (2 * marco)
    }

    private fun tubo(): String {
        val ancho = binding.etMed1.text.toString().toFloat()
        return if (mocheta() > 0f) {
            df1(ancho - (2 * marco))
        } else {
            ""
        }
    }

    private fun paflon(): Float {
        val ancho = binding.etMed1.text.toString().toFloat()
        val holgura = 1f
        return ((ancho - (2 * marco)) - holgura) - (2 * bastidor)
    }

    private fun parante(): Float {
        val holgura = 1f
        val piso = binding.etPiso.text.toString().toFloat()
        return if (piso == 0f) {
            hPuente() - holgura
        } else {
            (hPuente() - (holgura / 2)) - piso
        }
    }

    private fun paranteInterno(): Float {
        val p = parante()
        val parante = p- ((nZocalo()+1)*bastidor)
        return parante
    }

    //FUNCIONES VIDRIOS
    private fun vidrio() {
        val nombre = obtenerVarianteSeleccionada()

        binding.tvVidrios.text = when(nombre){
            "Mari h" ->if (mocheta() < 0f) {
                "${vidrioH()} = ${nPfvcal() - 1}"
            } else {
                "${vidrioH()} =${nPfvcal() - 1}\n" +
                        "${vidrioM()} = 1"
            }
            "Mari v" -> if (mocheta() < 0f){
                "${vidrioV()} = ${divi()}"
            } else {
                "${vidrioV()} = ${divi()}" +
                        "\n${vidrioM()} = 1"
            }

            else -> {""}
        }
    }

    private fun vidrioH(): String {
        val jun = binding.etJunki.text.toString().toFloat()
        val holgura = if (jun == 0f) {
            0.2f
        } else {
            0.4f
        }
        val anchv = df1((paflon() - holgura)).toFloat()
        val altv = df1(divisiones() - holgura).toFloat()
        return "${df1(anchv)} x ${df1(altv)}"
    }

    private fun vidrioV(): String {
        val jun = binding.etJunki.text.toString().toFloat()
        val holgura = if (jun == 0f) {
            0.2f
        } else {
            0.4f
        }
        val anchv = ((paflon()-(bastidor*(divi()-1)))/divi()-holgura)
        val altv = paranteInterno()-holgura
        return "${df1(anchv)} x ${df1(altv)}"
    }

    private fun vidrioM(): String {
        val jun = binding.etJunki.text.toString().toFloat()
        val holgura = if (jun == 0f) {
            0.2f
        } else {
            0.4f
        }
        val uno = df1(marcoSuperior() - holgura).toFloat()
        val dos = df1(mocheta() - holgura).toFloat()
        return "${df1(uno)} x ${df1(dos)}"
    }

    private fun referen(): String {
        val ancho = binding.etMed1.text.toString().toFloat()
        val alto = binding.etMed2.text.toString().toFloat()
        val hPuente = df1(hPuente())
        return if (mocheta() > 0f) {
            "anch ${df1(ancho)} x alt ${df1(alto)}\nAlto hoja = $hPuente"
        } else {
            "anch ${df1(ancho)} x alt ${df1(alto)}"
        }
    }

    private fun mocheta(): Float {
        val alto = binding.etMed2.text.toString().toFloat()
        val tubo = 2.5f
        return alto - (hPuente() + marco + tubo)
    }

    //FUNCIONES GENERALES
    private fun hPuente(): Float {
        val alto = binding.etMed2.text.toString().toFloat()
        val hHoja = binding.etHoja.text.toString().toFloat()
        val pisog = binding.etPiso.text.toString().toFloat()
        val piso = if (pisog == 0f) {
            pisog
        } else {
            pisog - 0.5f
        }
        return when {
            hHoja == 0f -> when {
                alto > 210f && (hoja + piso) < alto - 5.3 -> {
                    hoja + piso
                }

                alto <= 210f && alto > hoja -> {
                    190f + piso
                }

                alto <= hoja -> {
                    (alto - marco)
                }

                (hoja + piso) > alto - 5.3 -> {
                    (alto - marco)
                }

                else -> {
                    (alto - marco) + piso
                }
            }

            alto <= hHoja || (hHoja + piso) > alto - 5.3 -> {
                (alto - marco)
            }

            else -> {
                hHoja + piso
            }
        }
    }

    private fun divisiones(): Float {
        val divi2 = binding.etDivi.text.toString().toFloat()
        val nZoca = binding.etZocalo.text.toString().toInt()
        val mZoca = zocalo()
        val divis = if (nZoca > 1) {
            parante() - mZoca
        } else {
            parante()
        }
        val nbas = if (nZoca > 1) {
            divi2 * bastidor
        } else {
            (divi2 + 1) * bastidor
        }
        return (divis - nbas) / divi2
    }

    private fun divi():Int{
        val divi2 = binding.etDivi.text.toString().toInt()
        return divi2
    }

    private fun nPfvcal(): Int {
        val divi2 = binding.etDivi.text.toString().toInt()
        return (divi2 + 1)
    }

    private fun nPaflones(): Int {
        val divi2 = binding.etDivi.text.toString().toInt()
        val nBast = binding.etZocalo.text.toString().toInt()
        return if (nBast > 1) {
            divi2 + nBast
        } else {
            divi2 + 1
        }
    }

    private fun nZocalo(): Int {
        val nBast = binding.etZocalo.text.toString().toInt()
        return if (nBast == 0) {
            1
        } else {
            nBast
        }
    }

    private fun zocalo(): Float {
        val holgura = if (nZocalo() < 3) 0.009f else 0.009f // Ajusta según tus necesidades
        val mzoca = df1((nZocalo() + holgura) * bastidor).toFloatOrNull() ?: 0f
        return df1(mzoca).toFloatOrNull() ?: 0f
    }

    @SuppressLint("SetTextI18n")
    private fun panos(): String {
        val z = zocalo()
        val n = (nPfvcal() - 1)//cantidad paflones superiores y cantidad de paños
        val j = df1(divisiones()).toFloat()//tamaño de cada paño
        val b = bastidor
        val g = j + b
        val mPanos = when (n) {
            in 1..17 -> {
                List(n) { index -> (z + j + (index * (j + b))).toString() }
            }

            else -> listOf(((1 * g) + z) - b).map { it.toString() }
        }.toTypedArray()

        var x = ""
        for (i in mPanos) {
            x += "${df1(i.toFloat())}\n"
        }
        binding.tvEnsayo.text = "$z\n$x"
        binding.tvEnsayo.text = binding.tvEnsayo.text.substring(0, binding.tvEnsayo.text.length - 1)
        return binding.tvEnsayo.text as String
    }

    private fun array() {
        val marcopInput = binding.etMed2.text.toString()
        val marcop = marcopInput.toFloatOrNull() ?: 0f
        val resultado = StringBuilder().apply {
            append("Marco = ${df1(marcop)} = 2\n")
                .append("${df1(marcoSuperior())} = 1\n")
                .append("tubo = ${tubo()} = 1\n")
                .append("paflon = ${df1(paflon())} = 2\n")
                .append("${df1(parante())} = 2\n")
                .append("${parante() - (zocalo() + bastidor)} = 1\n")
                .append("15 = ${binding.etDivi.text.toString().toInt() - 1}\n")
                .append("Tope = ${df1(marcoSuperior())} = 1\n")
                .append("${df1(hPuente())} = 2\n")
                .append("Vidrio = ${df1((partesV() * 2) + 3.4f)} x ${df1(parteH() - 0.4f)} = 2\n")
                .append("${df1(divisiones() - (parteH() + 3.8f) - 0.4f)} x ${df1(partesV() - 0.4f)} = 4\n")
                .append("${vidrioM()} = 1\n")
                .append("puntosT= ${df1(partesV())}, ${df1((partesV() * 2) + 3.8f)}\n")
                .append("puntosP= ${df1(parteH() + 8.2f)}")
        }.toString()

        binding.tvEnsayo2.text = resultado
    }

    private fun partesV(): Float {
        return (paflon() - (unoMedio * 2)) / 3
    }

    private fun parteH(): Float {
        return (divisiones() - (unoMedio * 5)) / 6
    }

    fun calcularHipotenusa(): Float {
        val cateto1 = calcularMedida()+11.6f
        val cateto2 = calcularMedida() + 11.6f

        return sqrt(cateto1.pow(2) + cateto2.pow(2))
    }

    private fun calcularMedida(): Float {
        // Paso 1: Mitad de la división (ambos catetos)
        val mitadDivision = divisiones() / 2

        // Paso 2: Calcular hipotenusa (teorema de Pitágoras)
        val hipotenusa = sqrt(mitadDivision.pow(2) + mitadDivision.pow(2))

        // Paso 3: Restar la mitad de la hipotenusa a la mitad de la división
        val resto = mitadDivision - (hipotenusa / 2)

        // Paso 4: Sumar el resto a la mitad del ancho
        val resultado = (paflon()/ 2) + resto

        return resultado
    }

    // Función que genera los datos del plano según la descripción
    private fun generarDatosPlano(): DatosPlano {
        // Se obtienen las dimensiones del plano usando las funciones proporcionadas
        val ancho = paflon()
        val alto = paranteInterno()

        // El eje de rotación es el centro del plano
        val eje = Punto(ancho / 2, alto / 2)

        // Se calcula la cantidad de pares de líneas a generar: divi() - 1
        val cantidadPares = divi() - 1
        val paresLineas = mutableListOf<ParLineas>()

        // Se generan los pares de líneas. Cada par consta de dos líneas horizontales:
        // La primera línea del par: coordenada Y = divisiones() * i + bastidor() * (i - 1)
        // La segunda línea del par: coordenada Y = divisiones() * i + bastidor() * i
        for (i in 1..cantidadPares) {
            val yLinea1 = divisiones() * i + bastidor * (i - 1)
            val yLinea2 = divisiones() * i + bastidor * i

            val linea1 = Linea(Punto(0f, yLinea1), Punto(ancho, yLinea1))
            val linea2 = Linea(Punto(0f, yLinea2), Punto(ancho, yLinea2))
            paresLineas.add(ParLineas(linea1, linea2))
        }

        return DatosPlano(eje, paresLineas)
    }

    fun calcularIntersecciones(
        d: Float,
        angulo: Float,
        centro: Punto,
        ancho: Float,
        alto: Float
    ): List<Punto> {
        val rad = Math.toRadians(angulo.toDouble())
        val sinAng = sin(rad).toFloat()
        val cosAng = cos(rad).toFloat()
        val intersecciones = mutableListOf<Punto>()

        // Intersección con x = 0
        if (cosAng != 0f) {
            val y = centro.y + (d - sinAng * centro.x) / cosAng
            if (y in 0f..alto) {
                intersecciones.add(Punto(0f, y))
            }
        }

        // Intersección con x = ancho
        if (cosAng != 0f) {
            val y = centro.y + (d + sinAng * (ancho - centro.x)) / cosAng
            if (y in 0f..alto) {
                intersecciones.add(Punto(ancho, y))
            }
        }

        // Intersección con y = 0
        if (sinAng != 0f) {
            val x = centro.x - (d + cosAng * centro.y) / sinAng
            if (x in 0f..ancho) {
                intersecciones.add(Punto(x, 0f))
            }
        }

        // Intersección con y = alto
        if (sinAng != 0f) {
            val x = centro.x - (d - cosAng * (alto - centro.y)) / sinAng
            if (x in 0f..ancho) {
                intersecciones.add(Punto(x, alto))
            }
        }

        return intersecciones.distinctBy { Pair(it.x, it.y) }
    }

    private fun rotarDatosPlano(datosPlano: DatosPlano, angulo: Float): DatosPlano {
        val ancho = paflon()
        val alto = paranteInterno()
        val eje = datosPlano.eje
        val nuevosPares = mutableListOf<ParLineas>()

        // Para cada par de líneas originales
        datosPlano.paresLineas.forEach { par ->
            // Para cada línea del par, se obtiene la coordenada Y original (la línea es horizontal)
            val dLinea1 = par.linea1.inicio.y - eje.y  // d = y_original - eje.y
            val dLinea2 = par.linea2.inicio.y - eje.y

            // Se calculan las intersecciones para cada línea
            val interLinea1 = calcularIntersecciones(dLinea1, angulo, eje, ancho, alto)
            val interLinea2 = calcularIntersecciones(dLinea2, angulo, eje, ancho, alto)

            // Se espera que cada línea retorne 2 intersecciones válidas dentro del plano.
            // Si se obtienen 2 puntos, se forma la nueva línea; de lo contrario se omite.
            val nuevaLinea1 = if (interLinea1.size >= 2) {
                // Se seleccionan los dos puntos (en caso de haber más, se escogen los dos primeros)
                Linea(interLinea1[0], interLinea1[1])
            } else null

            val nuevaLinea2 = if (interLinea2.size >= 2) {
                Linea(interLinea2[0], interLinea2[1])
            } else null

            if (nuevaLinea1 != null && nuevaLinea2 != null) {
                nuevosPares.add(ParLineas(nuevaLinea1, nuevaLinea2))
            }
        }

        return DatosPlano(eje, nuevosPares)
    }

    fun calcularDistancia(linea: Linea): Float {
        return sqrt((linea.fin.x - linea.inicio.x).pow(2) + (linea.fin.y - linea.inicio.y).pow(2))
    }

    private fun obtenerTextoDistancias(datosPlano: DatosPlano): String {
        val sb = StringBuilder()
        datosPlano.paresLineas.forEachIndexed { indice, par ->
            val distanciaLinea1 = calcularDistancia(par.linea1)
            val distanciaLinea2 = calcularDistancia(par.linea2)
            sb.append("Par de líneas ${indice + 1}:\n")
            sb.append(" línea 1: ${df1(distanciaLinea1)} cm\n")
            sb.append(" línea 2: ${df1(distanciaLinea2)} cm\n")
        }
        return sb.toString()
    }

    private fun agruparMedidasDesdeTexto(): String {
        val angulo = binding.etAngulo.text.toString().toFloat()
        val datosRotados = rotarDatosPlano(generarDatosPlano(), angulo)

// Obtén el String con las distancias
        val textoDistancias = obtenerTextoDistancias(datosRotados)
        // 1) Extraer todas las medidas formateadas (p.ej. "62.4", "79.3") en orden
        val regexMedida = """\b([\d]+(?:\.[\d]+)?)\s*cm""".toRegex()
        val todasMedidas = regexMedida.findAll(textoDistancias)
            .map { it.groupValues[1] }
            .toList()

        // 2) De cada par (dos medidas consecutivas) escoger la mayor
        val mayores = mutableListOf<String>()
        for (i in 0 until todasMedidas.size step 2) {
            if (i + 1 < todasMedidas.size) {
                val m1 = todasMedidas[i].toFloat()
                val m2 = todasMedidas[i + 1].toFloat()
                // Escoge la medida mayor, pero conserva su forma textual
                mayores += if (m1 >= m2) todasMedidas[i] else todasMedidas[i + 1]
            }
        }

        // 3) Agrupar las medidas mayores y contar frecuencias
        val frecuencias: Map<String, Int> = mayores.groupingBy { it }.eachCount()

        // 4) Construir el resultado ordenado por valor numérico ascendente
        return frecuencias
            .entries
            .sortedBy { it.key.toFloat() }
            .joinToString(separator = "\n") { "${it.key} = ${it.value}" }

    }

    fun calcularTriangulo(cateto: Float, anguloGrados: Float, esAdyacente: Boolean): Triple<Float, Float, Float> {
        val anguloRadianes = Math.toRadians(anguloGrados.toDouble())

        val catetoCalculado = if (esAdyacente) {
            (cateto * tan(anguloRadianes)).toFloat()
        } else {
            (cateto / tan(anguloRadianes)).toFloat()
        }

        val hipotenusa = hypot(cateto, catetoCalculado)

        return Triple(cateto, catetoCalculado, hipotenusa)
    }

    //  LOGICA PUERTA VICKY
    private fun paflonRes() {

// 2) Agrupa y cuenta las medidas mayores de cada par:
        val resumen = agruparMedidasDesdeTexto()

        // Se obtiene el nombre de la puerta actual (si existe)
        val nombre = obtenerVarianteSeleccionada()

        binding.tvPaflon.text = when (nombre) {
            "Mari h" -> "${df1(paflon())} = ${nPaflones()}\n${df1(parante())} = 2"
            "Mari v" -> "${df1(paflon())} = ${nZocalo()}\n${df1(parante())} = 2\n" +
                    "${df1(paranteInterno())} = ${(binding.etDivi.text.toString().toInt() - 1)}"
            "Mari d" ->{"${df1(paflon())} = ${nZocalo() +1}" +
                    "\n$resumen\n${df1(parante())} = 2"}
            "Dora" -> "${df1(paflon())} = ${nPaflones()}\n${df1(parante())} = 777"//solo prueba
            "Adel" -> "${df1(paflon())} = ${nPaflones()}\n${df1(parante())} = 2"
            "Mili" -> "${df1(paflon())} = ${nPaflones()}\n${df1(parante())} = 2"
            "jeny" -> "${df1(paflon())} = ${nPaflones()}\n${df1(parante())} = 2"
            "Taly" -> "${df1(paflon())} = ${nPaflones()}\n${df1(parante())} = 2"
            "Viky" -> "${df1(paflon())} = ${nPaflones()}\n${df1(parante())} = 2" +
                    "\n${df1(parante() - ((nZocalo() + 1) * bastidor))} = 1" +
                    "\n${binding.etAD.text} = ${(binding.etDivi.text.toString().toInt() - 1)}"

            "Lina" -> "${df1(paflon())} = ${nPaflones()}\n${df1(parante())} = 2"
            "Tere" -> "${df1(paflon())} = ${nPaflones()}\n${df1(parante())} = 2"

            else -> {
                ""
            }
        }
    }

    private fun modelos() {
        when (puertaActual?.nombre) {
            "Mari" -> mariModeos()
            "Dora" -> doraModeos()
            "Adel" -> adelModeos()
            "Mili" -> miliModeos()
            "jeny" -> jenyModeos()
            "Taly" -> talyModeos()
            "Viky" -> vikyModeos()
            "Lina" -> linaModeos()
            "Tere" -> tereModeos()
            else -> {
                // Si no coincide con ninguno, puedes usar una función por defecto
                mariModeos()
            }
        }
    }

    //FUNCIONES DISEÑO

    private fun mostrarVariantes() {
        binding.txMedCant.setOnClickListener {
            // Se muestra el contenedor de variantes
            binding.lyVariantes.visibility = View.VISIBLE

            // Se asume que listaPuertas ya fue cargada y puertaActual se actualizó previamente
            if (listaPuertas.isNotEmpty()) {
                puertaActual = listaPuertas[indice]
                val nombrePuerta = puertaActual?.nombre ?: ""

                // Define la lista de variantes según el tipo exacto de puerta,
                // utilizando objetos Variante (con nombre e imagen)
                val variantes: List<Variante> = when (nombrePuerta) {
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
                    "Mili" -> listOf(
                        Variante("Variante Única", R.drawable.pvicky)
                    )
                    "Viky" -> listOf(
                        Variante("Variante Única", R.drawable.pvicky)
                    )
                    else -> listOf(
                        Variante("Error", R.drawable.ic_dormido),
                        Variante("Error 2", R.drawable.peligro2),
                        Variante("Eliminar", R.drawable.eliminar)
                    )
                }

                // Configura el RecyclerView horizontal
                binding.rvVariantes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvVariantes.adapter = VariantesAdapter(variantes) { variante ->
                    // Actualiza la variable global con el nombre de la variante seleccionada
                    actualizarVarianteSeleccionada(variante.nombre)
                    // Actualiza la imagen del modelo u otra acción necesaria
                    binding.ivModelo.setImageResource(variante.imagen)
                    // ...y se oculta el contenedor de variantes
                    binding.lyVariantes.visibility = View.GONE
                }
            }
        }

        // Si se toca fuera del RecyclerView (en el contenedor), se oculta el contenedor sin actualizar nada
        binding.lyVariantes.setOnClickListener {
            binding.lyVariantes.visibility = View.GONE
        }
    }

    private fun obtenerVarianteSeleccionada(): String {
        return varianteSeleccionada
    }

    private fun actualizarVarianteSeleccionada(nuevaVariante: String) {
        varianteSeleccionada = nuevaVariante
    }

    // 1. Función para dibujar el marco externo (los laterales y el superior)
    private fun dibujarMarcoExterno(
        canvas: Canvas,
        anchoPuertaPx: Float,
        altoPuertaPx: Float,
        marcoPx: Float,
        pinturaMarco: Paint,
        pinturaLinea: Paint
    ) {
        // Marco vertical izquierdo
        val rectMarcoIzq = RectF(0f, 0f, marcoPx, altoPuertaPx)
        canvas.drawRect(rectMarcoIzq, pinturaMarco)
        canvas.drawRect(rectMarcoIzq, pinturaLinea)

        // Marco vertical derecho
        val rectMarcoDer = RectF(anchoPuertaPx - marcoPx, 0f, anchoPuertaPx, altoPuertaPx)
        canvas.drawRect(rectMarcoDer, pinturaMarco)
        canvas.drawRect(rectMarcoDer, pinturaLinea)

        // Marco superior
        val rectMarcoSup = RectF(marcoPx, 0f, anchoPuertaPx - marcoPx, marcoPx)
        canvas.drawRect(rectMarcoSup, pinturaMarco)
        canvas.drawRect(rectMarcoSup, pinturaLinea)
    }

    // 2. Función para dibujar la mocheta (pieza horizontal adicional entre el marco superior y la hoja)
    private fun dibujarMocheta(
        canvas: Canvas,
        anchoPuertaPx: Float,
        marcoPx: Float,
        topHoja: Float, // posición superior de la hoja
        factorEscala: Float,
        pinturaMarco: Paint,
        pinturaInterior: Paint,
        pinturaLinea: Paint
    ) {
        val gapBelowFrameCm = 0.5f
        val horizontalFrameHeightCm = 2.5f
        val gapBelowFramePx = gapBelowFrameCm * factorEscala
        val horizontalFrameHeightPx = horizontalFrameHeightCm * factorEscala

        // La mocheta se coloca justo 0.5 cm por encima de la hoja
        val yFrameBottom = topHoja - gapBelowFramePx   // borde inferior del nuevo marco horizontal
        val yFrameTop = yFrameBottom - horizontalFrameHeightPx  // borde superior del nuevo marco horizontal

        // (a) Pintar de blanco el espacio de la mocheta
        val rectMochetaBlanco = RectF(marcoPx, marcoPx, anchoPuertaPx - marcoPx, yFrameTop)
        canvas.drawRect(rectMochetaBlanco, pinturaInterior)
        canvas.drawRect(rectMochetaBlanco, pinturaLinea)

        // (b) Dibujar el marco horizontal de la mocheta
        val rectMochetaFrame = RectF(marcoPx, yFrameTop, anchoPuertaPx - marcoPx, yFrameBottom)
        canvas.drawRect(rectMochetaFrame, pinturaMarco)
        canvas.drawRect(rectMochetaFrame, pinturaLinea)
    }

    // Función para dibujar el área interna de la hoja, con tres modos de división:
    // "H" para horizontal, "V" para vertical y "D" para diagonal.
    private fun dibujarAreaInternaHoja(
        canvas: Canvas,
        anchoHojaPx: Float,
        altoHojaPx: Float,
        paflonPx: Float,
        numeroZocalos: Int,
        numeroDivisiones: Int,
        pinturaInterior: Paint,
        pinturaPaflon: Paint,
        pinturaLinea: Paint,
        tipoDivision: String = "H"  // "H" (horizontal) por defecto, "V" vertical, "D" diagonal
    ) {
        // Definir el área interna sin incluir el paflón superior ni los zócalos.
        val topInterior = paflonPx
        val bottomInterior = altoHojaPx - numeroZocalos * paflonPx
        val rightInterior = anchoHojaPx - paflonPx
        val rectInterior = RectF(paflonPx, topInterior, rightInterior, bottomInterior)

        // Pintar el fondo del área interna y su contorno.
        canvas.drawRect(rectInterior, pinturaInterior)
        canvas.drawRect(rectInterior, pinturaLinea)

        when (tipoDivision) {
            "V" -> {
                // Divisiones verticales
                if (numeroDivisiones > 1) {
                    val numDivBars = numeroDivisiones - 1
                    val widthInterior = rectInterior.width()
                    if (widthInterior > 0) {
                        val gapUniformX = (widthInterior - numDivBars * paflonPx) / (numDivBars + 1)
                        var currentX = paflonPx + gapUniformX
                        for (i in 1..numDivBars) {
                            val rectDivision =

                                RectF(currentX, topInterior, currentX + paflonPx, bottomInterior)
                            canvas.drawRect(rectDivision, pinturaPaflon)
                            canvas.drawRect(rectDivision, pinturaLinea)
                            currentX += paflonPx + gapUniformX
                        }
                    }
                }
            }
            "D" -> {
                // Divisiones diagonales "rotadas":
                val angulo = binding.etAngulo.text.toString().toFloat()
                val numDivBars = numeroDivisiones - 1
                if (numDivBars > 0) {
                    // 1) Definir el rectángulo interno
                    val widthInterior = rightInterior - paflonPx
                    val heightInterior = bottomInterior - topInterior

                    // 2) Hacemos un clip al rectInterior para recortar todo lo que se salga.
                    canvas.save()
                    canvas.clipRect(rectInterior)

                    // 3) Trasladar y rotar el canvas de modo que al dibujar barras horizontales,
                    //    queden diagonales al final.
                    //    a) Trasladamos el origen al centro del rectángulo interior
                    val cx = rectInterior.centerX()
                    val cy = rectInterior.centerY()
                    canvas.translate(cx, cy)
                    //    b) Rotamos el canvas 45° (o el ángulo que quieras)
                    canvas.rotate(angulo)
                    //    c) Movemos el origen de nuevo para que (0,0) quede en la esquina sup izq del "nuevo" rect
                    //
                    //
                    //
                    //
                    //
                    //
                    //
                    //
                    //
                    //
                    //
                    //
                    //
                    //       (en el sistema rotado). En este caso, podemos dejarlo centrado y dibujar desde -someValue.

                    canvas.translate(-cx, -cy)

                    // 4) Ahora, en este sistema rotado, el rectInterior no es un rect normal,
                    //    sino un "rombo". Para simplificar, dibujamos barras horizontales
                    //    en un área mayor (p.ej. un rect grande que cubra el bounding box).
                    //    El grosor de cada barra es paflonPx.
                    val thickness = paflonPx
                    // Calculemos la altura total que "simularemos" para las barras en horizontal.
                    // Por ejemplo, usaremos la altura del rectángulo interior (sin rotar) como referencia.
                    // Dividimos esa altura en (numDivs+1) "huecos" y numDivs barras.
                    val totalHeight = heightInterior // Podés usar algo mayor si querés
                    val gap = (totalHeight - (numDivBars * thickness)) / (numDivBars + 1)

                    // 5) Dibujar las barras horizontales en este sistema rotado
                    var currentY = topInterior + gap
                    for (i in 1..numDivBars) {
                        // Vamos a dibujar un rect horizontal grande que cubra de izq a der "sobrado"
                        // para que, al recortar con clipRect, solo se vea lo que cae dentro.
                        val leftBar = paflonPx - widthInterior // algo que sobre
                        val rightBar = rightInterior + widthInterior
                        val rectBar = RectF(leftBar, currentY, rightBar, currentY + thickness)
                        canvas.drawRect(rectBar, pinturaPaflon)
                        canvas.drawRect(rectBar, pinturaLinea)

                        currentY += thickness + gap
                    }

                    // 6) Restaurar para salir del clip y la rotación
                    canvas.restore()
                }
            }

            else -> {
                // Divisiones horizontales (por defecto)
                if (numeroDivisiones > 1) {
                    val numDivBars = numeroDivisiones - 1
                    val heightInterior = bottomInterior - topInterior
                    if (heightInterior > 0) {
                        val gapUniformY = (heightInterior - numDivBars * paflonPx) / (numDivBars + 1)
                        var currentY = topInterior + gapUniformY
                        for (i in 1..numDivBars) {
                            val rectDivision =
                                RectF(paflonPx, currentY, rightInterior, currentY + paflonPx)
                            canvas.drawRect(rectDivision, pinturaPaflon)
                            canvas.drawRect(rectDivision, pinturaLinea)
                            currentY += paflonPx + gapUniformY
                        }
                    }
                }
            }
        }
    }

    // Función que dibuja el bastidor de la hoja (laterales, paflón superior y zócalos)
    private fun dibujarBastidorHoja(
        canvas: Canvas,
        anchoHojaPx: Float,
        altoHojaPx: Float,
        paflonPx: Float,
        numeroZocalos: Int,
        pinturaMarco: Paint,
        pinturaPaflon: Paint,
        pinturaLinea: Paint
    ) {
        // Dibujar el contorno completo de la hoja
        val rectBastidor = RectF(0f, 0f, anchoHojaPx, altoHojaPx)
        canvas.drawRect(rectBastidor, pinturaMarco)
        canvas.drawRect(rectBastidor, pinturaLinea)

        // Dibujar laterales (paflones verticales)
        val rectLateralIzq = RectF(0f, 0f, paflonPx, altoHojaPx)
        canvas.drawRect(rectLateralIzq, pinturaPaflon)
        canvas.drawRect(rectLateralIzq, pinturaLinea)
        val rectLateralDer = RectF(anchoHojaPx - paflonPx, 0f, anchoHojaPx, altoHojaPx)
        canvas.drawRect(rectLateralDer, pinturaPaflon)
        canvas.drawRect(rectLateralDer, pinturaLinea)

        // Dibujar el paflón superior
        val rectSuperior = RectF(paflonPx, 0f, anchoHojaPx - paflonPx, paflonPx)
        canvas.drawRect(rectSuperior, pinturaPaflon)
        canvas.drawRect(rectSuperior, pinturaLinea)

        // Dibujar los zócalos en la parte inferior
        for (i in 0 until numeroZocalos) {
            val yZocalo = altoHojaPx - (i + 1) * paflonPx
            val rectZocalo = RectF(paflonPx, yZocalo, anchoHojaPx - paflonPx, yZocalo + paflonPx)
            canvas.drawRect(rectZocalo, pinturaPaflon)
            canvas.drawRect(rectZocalo, pinturaLinea)
        }
    }

    // Función que agrupa el dibujo de la hoja completa (bastidor + área interna)
// Se añade el parámetro dividirVerticalmente para decidir el tipo de división en el área interna.
    private fun dibujarHojaCompleta(
        canvas: Canvas,
        hojaRect: RectF,
        numeroZocalos: Int,
        numeroDivisiones: Int,
        paflonPx: Float,
        pinturaMarco: Paint,
        pinturaPaflon: Paint,
        pinturaInterior: Paint,
        pinturaLinea: Paint,
        tipoDivision: String = "H"  // "H" horizontal, "V" vertical, "D" diagonal
    ) {
        canvas.save()
        canvas.translate(hojaRect.left, hojaRect.top)
        val anchoHojaPx = hojaRect.width()
        val altoHojaPx = hojaRect.height()

        // Dibujar el bastidor (contorno de la hoja: laterales, paflón superior y zócalos)
        dibujarBastidorHoja(canvas, anchoHojaPx, altoHojaPx, paflonPx, numeroZocalos, pinturaMarco, pinturaPaflon, pinturaLinea)

        // Dibujar el área interna con el tipo de división especificado
        dibujarAreaInternaHoja(
            canvas,
            anchoHojaPx,
            altoHojaPx,
            paflonPx,
            numeroZocalos,
            numeroDivisiones,
            pinturaInterior,
            pinturaPaflon,
            pinturaLinea,
            tipoDivision
        )
        canvas.restore()
    }

    // Función principal para generar el Bitmap de la puerta.
// Se añade el parámetro dividirVerticalmente, el cual se activará para la variante "Mari v".
    private fun generarBitmapPuerta(
        anchoPuertaCm: Float,   // ancho total de la puerta (en cm)
        altoPuertaCm: Float,    // alto total de la puerta (en cm)
        anchoHojaCm: Float,     // ancho de la hoja
        altoHojaCm: Float,      // alto de la hoja
        numeroZocalos: Int,
        numeroDivisiones: Int,
        anchoContenedor: Float, // tamaño del contenedor (en píxeles)
        altoContenedor: Float,
        tipoDivision: String = "H"  // "H" horizontal (default), "V" vertical, "D" diagonal
    ): Bitmap {
        // 1) Calcular el factor de escala para convertir de cm a píxeles
        val factorEscala = minOf(anchoContenedor / anchoPuertaCm, altoContenedor / altoPuertaCm)

        // 2) Convertir dimensiones de cm a píxeles
        val anchoPuertaPx = anchoPuertaCm * factorEscala
        val altoPuertaPx = altoPuertaCm * factorEscala
        val anchoHojaPx = anchoHojaCm * factorEscala
        val altoHojaPx = altoHojaCm * factorEscala
        val marcoPx = marco * factorEscala  // 'marco' es la variable global (por ejemplo, 2.2f)

        // 3) Crear el Bitmap y pintar el fondo (por ejemplo, rojo)
        val bitmap = Bitmap.createBitmap(anchoContenedor.toInt(), altoContenedor.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.RED)

        // 4) Calcular el offset para centrar la puerta en el contenedor
        val offsetX = (anchoContenedor - anchoPuertaPx) / 2f
        val offsetY = (altoContenedor - altoPuertaPx) / 2f
        canvas.save()
        canvas.translate(offsetX, offsetY)

        // 5) Definir las pinturas
        val pinturaMarco = Paint().apply {
            color = Color.GRAY
            style = Paint.Style.FILL
        }
        val pinturaPaflon = Paint().apply {
            color = ContextCompat.getColor(this@Puertas, R.color.aluminio)
            style = Paint.Style.FILL
        }
        val pinturaInterior = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val pinturaLinea = Paint().apply {
            color = Color.BLACK
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }

        // 6) Dibujar el marco externo de la puerta
        dibujarMarcoExterno(canvas, anchoPuertaPx, altoPuertaPx, marcoPx, pinturaMarco, pinturaLinea)

        // 7) Calcular la posición de la hoja
        val offsetHojaBottomCm = 1f  // separación de 1 cm al piso
        val offsetHojaBottomPx = offsetHojaBottomCm * factorEscala
        val bottomHoja = altoPuertaPx - offsetHojaBottomPx
        val topHoja = bottomHoja - altoHojaPx
        val leftHoja = (anchoPuertaPx - anchoHojaPx) / 2f
        val rectHoja = RectF(leftHoja, topHoja, leftHoja + anchoHojaPx, bottomHoja)

        // 8) Dibujar la hoja completa (bastidor + área interna) usando el tipo de división indicado
        val paflonPx = 8.25f * factorEscala  // grosor fijo para los elementos internos
        dibujarHojaCompleta(
            canvas,
            rectHoja,
            numeroZocalos,
            numeroDivisiones,
            paflonPx,
            pinturaMarco,
            pinturaPaflon,
            pinturaInterior,
            pinturaLinea,
            tipoDivision
        )

        // 9) Dibujar la mocheta
        dibujarMocheta(canvas, anchoPuertaPx, marcoPx, topHoja, factorEscala, pinturaMarco, pinturaInterior, pinturaLinea)

        canvas.restore()
        return bitmap
    }

    private fun guardarBitmapEnCache(context: Context, bitmap: Bitmap) {
        try {
            val directorioCache = context.cacheDir
            val nombreArchivo = "imagen_puerta.png"
            val archivo = File(directorioCache, nombreArchivo)
            val salida = FileOutputStream(archivo)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)
            salida.flush()
            salida.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

data class Puerta(val nombre: String, val medida: String, val zocalo: String)

val listaPuertas = listOf(
    Puerta("Mari", "3", "8"),
    Puerta("Dora", "3.6", "8"),
    Puerta("Adel", "7", "8."),
    Puerta("Mili", "3.9", "8."),
    Puerta("jeny", "4.4", "8"),
    Puerta("Taly", "8", "8"),
    Puerta("Viky", "8", "8"),
    Puerta("Lina", "8", "8"),
    Puerta("Tere", "8", "8")
)

class VariantesAdapter(
    private val variantes: List<Variante>,
    private val onItemClick: (Variante) -> Unit
) : RecyclerView.Adapter<VariantesAdapter.VariantesViewHolder>() {

    inner class VariantesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgVariante: ImageView = itemView.findViewById(R.id.imgVariante)
        val tvNombre: TextView = itemView.findViewById(R.id.tvVariante)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.variantes_puertas, parent, false)
        return VariantesViewHolder(view)
    }

    override fun onBindViewHolder(holder: VariantesViewHolder, position: Int) {
        val variante = variantes[position]
        holder.imgVariante.setImageResource(variante.imagen)
        holder.tvNombre.text = variante.nombre
        holder.itemView.setOnClickListener {
            onItemClick(variante)
        }
    }

    override fun getItemCount(): Int = variantes.size
}

data class Variante(
    val nombre: String,
    val imagen: Int
)

// Clases de datos para estructurar la información del plano
data class Punto(val x: Float, val y: Float)
data class Linea(val inicio: Punto, val fin: Punto)
data class ParLineas(val linea1: Linea, val linea2: Linea)
data class DatosPlano(val eje: Punto, val paresLineas: List<ParLineas>)