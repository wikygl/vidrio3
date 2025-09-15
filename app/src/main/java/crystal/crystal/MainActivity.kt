package crystal.crystal

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Chunk
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfPageEventHelper
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import crystal.crystal.catalogo.CatalogoActivity
import crystal.crystal.databinding.ActivityMainBinding
import crystal.crystal.red.ListChatActivity
import crystal.crystal.registro.InicioActivity
import crystal.crystal.registro.Registro
import crystal.crystal.taller.Taller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "DEPRECATION")
class MainActivity : AppCompatActivity() {


    private var lista: MutableList<Listado> = mutableListOf()
    companion object {private const val RECEIVE_PRESUPUESTO_REQUEST = 3
        private const val DICTADO_REQUEST_CODE = 200
        private const val CODIGO_SOLICITUD_OCR = 300 }
    private val auth = FirebaseAuth.getInstance()
    private lateinit var currentUserId: String
    private var db = Firebase.firestore

    private lateinit var usados: Spinner
    private lateinit var unidades: Spinner
    private var colorSeleccionado: Int = 0
    private val RECORD_REQUEST_CODE = 101
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private var selectedPosition: Int = -1
    private val retaso = 1.8f

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    // Mapa para almacenar el contador de mensajes no leídos por chat
    private val unreadCountByChat = mutableMapOf<String, Int>()

    @SuppressLint("NewApi", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar SharedPreferences
        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding.clienteEditxt.setText(cargarDatosGuardados())

        // Cargar los datos guardados
        cargarDatosGuardados()

        fotoUsuario()

        cliente()

        uni1()

        uni2()

        eliminar()

        abrir()

        manejarPresupuestoRecibido()

        binding.btnLimpiar.setOnClickListener {
            try {
                if (binding.usTxt.text != "uni") {
                    binding.med1Editxt.requestFocus()
                } else {
                    binding.cantEditxt.requestFocus()
                }

                binding.precioUnitario.text = "0.0"
                binding.precioCantidad.text = "0.0"
                binding.pcTxt.text = "0.0"
                binding.mcTxt.text = "0.0"
                binding.med1Editxt.setText(if (binding.usTxt.text=="uni"){"1"}else{""})
                binding.med2Editxt.setText(if (binding.usTxt.text=="ml"||binding.usTxt.text=="uni"){"1"}else{""})
                binding.cantEditxt.setText("")
                binding.precioEditxt.setText("")
                binding.proEditxt.setText("")
                binding.clienteEditxt.setText("")
                binding.med1Editxt.hint = ""
                binding.med2Editxt.hint = ""
                binding.cantEditxt.hint = ""
                binding.precioTotal.text = "0.0"
                binding.piesTotal.text = "0.0"
                binding.metrosTotal.text = "0.0"
                binding.per.text = "0.0"
                binding.prueTxt.text= ""
                binding.tvpCliente.text = "Presupuesto"
                lista.clear()
                binding.list.onRemoteAdapterConnected()
                actualizar()
            } catch (_: Exception) {
            }
        }

        binding.btBuscar.setOnClickListener {
            startActivity(Intent(this,BaulActivity::class.java))
        }

        binding.btnCalcular.setOnLongClickListener {
            openPdf()
            return@setOnLongClickListener true
        }

        binding.btUser.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }

        binding.precioTotal.setOnClickListener {
            startActivity(Intent(this, VendePapa::class.java).putExtra(
                "monto", binding.precioTotal.text.toString()))}

        binding.listadoTxt.setOnClickListener {
            startActivity(Intent(this, ListaActivity::class.java).
            putExtra("monto", binding.per.text.toString()))}

        binding.tallerCal.setOnClickListener {
            if (binding.clienteEditxt.text.isNotEmpty()) {
                val paquete = Bundle().apply {
                    putString("cliente", binding.clienteEditxt.text.toString())
                }
                val intent = Intent(this, Taller::class.java)
                intent.putExtras(paquete)
                startActivity(intent)
            } else {
                startActivity(Intent(this, Taller::class.java))
            }
        }

        // Supongamos que el usuario ya está autenticado
        currentUserId = obtenerUsuarioActual()

        setupUnreadMessagesListener()

        // Al presionar btnChat, se redirige a la ListChatActivity
        binding.btnChat.setOnClickListener {
            val intent = Intent(this, ListChatActivity::class.java)
            intent.putExtra("usuario", currentUserId)
            startActivity(intent)
        }

        binding.btnCatalogo.setOnClickListener {
            startActivity(Intent(this, CatalogoActivity::class.java))}

        // Agregar OnLongClickListener al textView prodtxt
        binding.prodtxt.setOnLongClickListener {
            // Agregar el TextWatcher al EditText pro_editxt
            binding.proEditxt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // No se utiliza en este caso
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Filtrar la lista con el texto ingresado en el EditText
                    val criterio = s.toString()
                    filtrarLista(criterio)
                }

                override fun afterTextChanged(s: Editable?) {
                    // No se utiliza en este caso
                }
            })

            // Devolver true para indicar que se ha manejado el evento de clic largo
            true
        }

        binding.btnCalcular.setOnClickListener {
            try {
                //Zona real
                if (binding.usTxt.text!="uni") {
                    binding.med1Editxt.requestFocus()} else {
                    binding.cantEditxt.requestFocus()
                }

                agregarListado()

                actualizar()

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "ingrese un número válido", Toast.LENGTH_LONG).show()
            }
        }

        binding.btMicro.setOnClickListener {
            val intent = Intent(this, crystal.crystal.dictado.DictadoActivity::class.java)
            startActivityForResult(intent, DICTADO_REQUEST_CODE)
        }

        binding.btScan.setOnClickListener {
            val opciones = arrayOf<CharSequence>(
                "📷 Tomar foto",
                "🖼️ Elegir de la galería",
                "🔍 OCR - Extraer medidas"  // ← NUEVA OPCIÓN
            )
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Elige una opción")
            builder.setItems(opciones) { dialog, item ->
                when (item) {
                    0 -> openCamera()                    // ← MANTENER tu función existente
                    1 -> openGallery()                   // ← MANTENER tu función existente
                    2 -> {                               // ← NUEVA FUNCIONALIDAD OCR
                        val intent = Intent(this, crystal.crystal.ocr.OcrActivity::class.java)
                        startActivityForResult(intent, CODIGO_SOLICITUD_OCR)
                    }
                }
            }
            builder.show()
        }

        binding.txtRetaso.setOnClickListener {
            binding.prueTxt.text= cargarDatosGuardados()
        }

        binding.tvpCliente.setOnLongClickListener {
            mostrarMenuPresupuesto()

            true
        }

        binding.listadoTxt.setOnLongClickListener {
            abrirSelectorPresupuesto()
            true
        }

        // En tu onCreate() o donde inicialices la interfaz
        binding.txCostoTotal.setOnLongClickListener {
            mostrarDialogoPrecioPactado()
            true
        }

    }

    override fun onPause() {
        super.onPause()
        // Guardar los datos antes de que la aplicación pase a segundo plano
        guardarDatos()
    }

    override fun onStart() {
        super.onStart()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        currentUser?.reload()?.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Si la recarga falla, es probable que el usuario haya sido eliminado
                auth.signOut()
                startActivity(Intent(this, InicioActivity::class.java))
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fotoUsuario(){
        val currentUser = auth.currentUser

        if (currentUser?.photoUrl != null) {
            Glide.with(this)
                .load(currentUser.photoUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_usuario4)  // Imagen mientras se carga
                .error(R.drawable.ic_usuario4)       // En caso de error
                .into(binding.btUser)
        } else {
            // Si no hay foto de perfil, cargamos la imagen predeterminada
            Glide.with(this)
                .load(R.drawable.ic_usuario4)
                .circleCrop()
                .into(binding.btUser)
        }

        if (currentUser?.displayName != null && currentUser.displayName!!.isNotEmpty()) {
            // Separa el nombre completo por espacios y toma el primer elemento
            val firstName = currentUser.displayName!!.split(" ").firstOrNull() ?: "Usuario"
            binding.txUser.text = firstName
        } else {
            // Si no hay displayName, asigna un valor predeterminado
            binding.txUser.text = "Usuario"
        }

    }
    @SuppressLint("SetTextI18n")

    private fun agregarListado() {
        // escalas, unidades y colores
        val escala = binding.usTxt.text.toString()
        val uni   = binding.prTxt.text.toString()
        val color = colorSeleccionado
        // datos ingresados por el usuario
        val medida1 = med1()
        val medida2 = med2()
        val medida3 = med3()
        val cantidad = binding.cantEditxt.text.toString().toFloat()
        val precio = binding.precioEditxt.text.toString().toFloat()
        val producto = binding.proEditxt.text.toString().ifBlank { "..." }
        // resultados de calculos
        val piescua = pies(medida1,medida2)
        val metroscua = metroCua(medida1,medida2)
        val ml = mLineales(medida1,medida2)
        val cub = mCubicos(medida1,medida2,medida3)
        // calculo de cantidades
        val mlcant = ml * cantidad
        val piescant = piescua * cantidad
        val metroscant= metroCua(medida1,medida2) * cantidad
        val cubcant  = cub * cantidad
        val peri = perim()
        // vinculacion de fotos
        val uri = ""
        // calculo de precios

        val costounitario = when (escala){
            "p2" ->piescua * precio
            "m2" ->metroscua * precio
            "ml" ->ml * precio
            "m3" ->cub * precio
            else -> { precio}
        }
        val costocantidad = when (escala){
            "p2" ->piescant * precio
            "m2" ->metroscant * precio
            "ml" ->mlcant * precio
            "m3" ->cubcant * precio
            else -> {cantidad * precio}
        }

        binding.pcTxt.text = df1(piescant)
        binding.mcTxt.text = df1(metroscua)
        binding.precioUnitario.text = df2(costounitario)
        binding.precioCantidad.text = df2(costocantidad)
        binding.prueTxt.text= med3().toString()

        binding.med1Editxt.hint = df1(medida1)
        binding.med2Editxt.hint = df1(medida2)
        binding.med3Editxt.hint = df1(medida3)

        binding.med1Editxt.setText(if (binding.usTxt.text=="uni"){"1"}else{""})
        binding.med2Editxt.setText(if (binding.usTxt.text=="ml"||binding.usTxt.text=="uni"){"1"}else{""})
        binding.med3Editxt.text?.clear()
        binding.cantEditxt.text?.clear()

        val medidas = Listado(
            escala, uni, medida1, medida2, medida3, cantidad, piescant, precio, costocantidad,
            producto, peri, metroscua, mlcant, cub, color,uri
        )

        lista.add(medidas)
    }


    @SuppressLint("SetTextI18n")
    private fun actualizar() {
        // Creamos un nuevo adapter con los datos de la lista
        val adapter = adaptadores()

        // Configuramos el adapter para el ListView
        binding.list.adapter = adapter

        // Calculamos los totales
        var costoTotal = 0F
        var metroscuaTotal = 0F
        var piescuaTotal = 0F
        var periTotal = 0F
        for (medida in lista) {
            costoTotal += medida.costo
            metroscuaTotal += medida.metcua
            piescuaTotal += medida.piescua
            periTotal += medida.peri
        }

        // Actualizamos los TextView con los totales
        binding.precioTotal.text = df2(costoTotal)
        binding.metrosTotal.text = df1(metroscuaTotal)
        binding.piesTotal.text = df1(piescuaTotal)
        binding.per.text = "${df1(periTotal)} m.."

        // Notificamos al adapter que se actualizaron los datos
        adapter.notifyDataSetChanged()
    }

    private fun adaptadores(): ArrayAdapter<SpannableString> {
        val clipCodigo = 0x1F4CE
        val clip = String(Character.toChars(clipCodigo))

        val adapter = ArrayAdapter(
            this, R.layout.lista_cal,
            lista.map { datos ->
                // Acortar el URI solo para mostrarlo en la interfaz
                val uriAcortado = if (datos.uri.length > 20) "...${datos.uri.takeLast(20)}" else datos.uri

                val text = when (datos.escala) {
                    "p2" -> {
                        "(${df1(datos.medi1)} x ${df1(datos.medi2)} x ${df1(datos.canti)} = " +
                                "${df1(datos.piescua)}(${datos.escala}) " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto} " +
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    "m2" -> {
                        "(${df1(datos.medi1)} x ${df1(datos.medi2)} x ${df1(datos.canti)} = " +
                                "${df1(datos.metcua)}(${datos.escala}) " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    "m3" -> {
                        "(${df1(datos.medi1)} x ${df1(datos.medi2)} x ${df1(datos.medi3)} x ${df1(datos.canti)} = " +
                                "${df1(datos.metcub)}(${datos.escala}) " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    "ml" -> {
                        "(${df1(datos.medi1)} x ${df1(datos.canti)} = ${df1(datos.metli)}(${datos.escala}) " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    "uni" -> {
                        "${df1(datos.canti)}(${datos.escala}) = " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                    else -> {
                        "(${df1(datos.medi1)} x ${df1(datos.medi2)} x ${df1(datos.canti)} = ${df1(datos.piescua)} " +
                                "x S/${df2(datos.precio)} == S/${df2(datos.costo)} -> ${datos.producto}"+
                                ",$clip $uriAcortado" // Mostrar el URI acortado
                    }
                }

                val spannableString = SpannableString(text)
                spannableString.setSpan(
                    ForegroundColorSpan(datos.color),
                    text.indexOf("(${datos.escala})"),
                    text.indexOf("(${datos.escala})") + datos.escala.length + 2,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString
            }
        )

        return adapter
    }

    @SuppressLint("SetTextI18n")
    private fun cliente() {

        val paqueteR = intent.extras
        val clienteIntent = paqueteR?.getString("rcliente")
        val clienteRecup = cargarDatosGuardados()

        if (clienteIntent != null) {
            // Si hay un cliente en los extras del intent, priorizarlo
            sharedPreferences.edit().putString("cliente", clienteIntent).apply()
            binding.tvpCliente.text = "Presupuesto de $clienteIntent"
            binding.clienteEditxt.setText(clienteIntent)

            // Ajustar la visibilidad: ocultar lyCuello y mostrar lyCuerpo
            binding.lyCuello.visibility = View.GONE
            binding.lyCuerpo.visibility = View.VISIBLE
        } else {
            // Si no hay cliente en los extras, usar el valor de SharedPreferences
            if (clienteRecup.isNotEmpty()) {
                binding.tvpCliente.text = "Presupuesto de $clienteRecup"
                binding.clienteEditxt.setText(clienteRecup)

                // Ajustar la visibilidad: ocultar lyCuello y mostrar lyCuerpo
                binding.lyCuello.visibility = View.GONE
                binding.lyCuerpo.visibility = View.VISIBLE
            } else {
                binding.tvpCliente.text = "Presupuesto"
                binding.clienteEditxt.setText("")

                // Ajustar la visibilidad: mostrar lyCuello y ocultar lyCuerpo
                binding.lyCuello.visibility = View.VISIBLE
                binding.lyCuerpo.visibility = View.GONE
            }
        }

        binding.tvpCliente.setOnClickListener {
            binding.lyCuello.visibility = View.VISIBLE
            binding.lyCuerpo.visibility = View.GONE
        }

        binding.btGo.setOnClickListener {
            val clientet = binding.clienteEditxt.text.toString().trim()
            binding.tvpCliente.text = if (clientet.isNotEmpty()) {
                "Presupuesto de $clientet"
            } else {
                "Presupuesto"
            }

            if (clientet.isNotEmpty()) {
                // Si hay un cliente, ocultar lyCuello y mostrar lyCuerpo
                binding.lyCuello.visibility = View.GONE
                binding.lyCuerpo.visibility = View.VISIBLE

                // Guardar el cliente en SharedPreferences
                sharedPreferences.edit().putString("cliente", clientet).apply()
                Toast.makeText(this, "Cliente guardado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                // Si no hay cliente, mostrar lyCuello y ocultar lyCuerpo
                binding.lyCuello.visibility = View.GONE
                binding.lyCuerpo.visibility = View.VISIBLE

                // Eliminar el cliente de SharedPreferences
                sharedPreferences.edit().remove("cliente").apply()
                // No mostrar toast al eliminar
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "WrongViewCast", "MissingInflatedId")
    private fun eliminar() {
        binding.list.setOnItemClickListener { parent, view, position, id ->
            try {
                val dialogo = AlertDialog.Builder(this)
                val modelo = layoutInflater.inflate(R.layout.dialogo, null)
                val eliminar = modelo.findViewById<Button>(R.id.btn_dialogo_eliminar)
                val editar = modelo.findViewById<Button>(R.id.btn_dialogo_editar)
                val irlista = modelo.findViewById<Button>(R.id.btnGuardar)
                val abrir = modelo.findViewById<Button>(R.id.btAbrir)
                val enviar = modelo.findViewById<Button>(R.id.btnEnviarJson)
                val datos = modelo.findViewById<TextView>(R.id.tvEscala)
                val pro = modelo.findViewById<TextView>(R.id.etdProducto)
                datos.text = "${lista[position].producto} ${lista[position].escala} en ${lista[position].uni}"

                enviar.setOnClickListener {
                    enviarPresupuestoPorChat()
                }
                abrir.visibility = View.VISIBLE
                dialogo.setView(modelo)
                val dialogoPer = dialogo.create()
                dialogoPer.show()

                eliminar.setOnClickListener {
                    lista.removeAt(position)
                    actualizar()
                    dialogoPer.dismiss()
                }

                editar.setOnClickListener {
                    val otro = modelo.findViewById<View>(R.id.lyEdit)
                    val btnDiaOk = modelo.findViewById<View>(R.id.btnDiaOk)
                    val m1 = modelo.findViewById<TextView>(R.id.etdMed1)
                    val m2 = modelo.findViewById<TextView>(R.id.etdMed2)
                    val m3 = modelo.findViewById<TextView>(R.id.etdMed3)
                    val ca = modelo.findViewById<TextView>(R.id.etdCant)
                    val pre = modelo.findViewById<TextView>(R.id.etdPrecio)

                    otro.visibility = View.VISIBLE
                    irlista.visibility = View.GONE
                    eliminar.visibility = View.INVISIBLE
                    editar.visibility = View.INVISIBLE
                    btnDiaOk.visibility = View.VISIBLE

                    m1.text = df1(lista[position].medi1)
                    m2.text = df1(lista[position].medi2)
                    m3.text = df1(lista[position].medi3)
                    ca.text = df1(lista[position].canti)
                    pre.text = df1(lista[position].precio)
                    pro.text = lista[position].producto

                    val lyMed1 = modelo.findViewById<View>(R.id.lyMed1)
                    val lyMed2 = modelo.findViewById<View>(R.id.lyMed2)
                    val lyMed3 = modelo.findViewById<View>(R.id.lyMed3)

                    val escala = lista[position].escala

                    when (escala) {
                        "p2" -> {
                            lyMed3.visibility = View.GONE
                            lyMed1.visibility = View.VISIBLE
                            lyMed2.visibility = View.VISIBLE
                        }
                        "m2" -> {
                            lyMed3.visibility = View.GONE
                            lyMed1.visibility = View.VISIBLE
                            lyMed2.visibility = View.VISIBLE
                        }
                        "ml" -> {
                            lyMed1.visibility = View.VISIBLE
                            lyMed2.visibility = View.GONE
                            lyMed3.visibility = View.GONE
                        }
                        "m3" -> {
                            lyMed1.visibility = View.VISIBLE
                            lyMed2.visibility = View.VISIBLE
                            lyMed3.visibility = View.VISIBLE
                        }
                        "uni" -> {
                            lyMed1.visibility = View.GONE
                            lyMed2.visibility = View.GONE
                            lyMed3.visibility = View.GONE
                            eliminar.visibility=View.GONE
                        }
                    }

                    btnDiaOk.setOnClickListener {
                        try {
                            val medi1 = m1.text.toString().toFloat()
                            val medi2 = m2.text.toString().toFloat()
                            val medi3 = m3.text.toString().toFloat()
                            val cantidad = ca.text.toString().toFloat()
                            val precio = pre.text.toString().toFloat()
                            var pi = 0f
                            var co = 0f

                            when (escala) {
                                "p2" -> {
                                    pi = pies(medi1, medi2) * cantidad
                                    co = pi * precio
                                }
                                "m2" -> {
                                    pi = metroCua(medi1, medi2) * cantidad
                                    co = pi * precio
                                }
                                "ml" -> {
                                    pi = mLineales(medi1, medi2) * cantidad
                                    co = pi * precio
                                }
                                "m3" -> {
                                    pi = mCubicos(medi1, medi2, medi3) * cantidad
                                    co = pi * precio
                                }
                                "uni" -> {
                                    pi = cantidad
                                    co = pi * precio
                                }
                            }

                            lista[position].medi1 = medi1
                            lista[position].medi2 = medi2
                            lista[position].medi3 = medi3
                            lista[position].canti = cantidad
                            lista[position].precio = precio
                            lista[position].producto = pro.text.toString()
                            lista[position].piescua = pi
                            lista[position].metcua = pi
                            lista[position].metli = pi
                            lista[position].metcub = pi
                            lista[position].costo = co

                            actualizar()
                            dialogoPer.dismiss()
                            Toast.makeText(this, "Se editó correctamente", Toast.LENGTH_SHORT).show()
                        } catch (e: NumberFormatException) {
                            Toast.makeText(this, "Error: Ingresa valores numéricos válidos", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Cuando ya se tiene un URI seleccionado:
                    val uriCompleto = lista[position].uri
                    val uriAcortado = if (uriCompleto.length > 20) "...${uriCompleto.takeLast(20)}" else uriCompleto
                    usoImagenUri(uriAcortado)
                }

                irlista.setOnClickListener {
                    guardar()
                    dialogoPer.dismiss()
                }
                abrir.setOnClickListener {
                    selectedPosition = position // Guardar la posición seleccionada
                    openGallery()
                    dialogoPer.dismiss()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error al mostrar el diálogo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filtrarLista(criterio: String) {
        val listaFiltrada = lista.filter { item ->
            item.producto.contains(criterio, ignoreCase = true)
        }

        val adaptador = ArrayAdapter(this, R.layout.lista_cal, listaFiltrada.map { it.toString() })
        binding.list.adapter = adaptador
    }

    //shared
    private fun guardarDatos() {
        // Obtener los datos que deseas guardar
        val dato1 = binding.med1Editxt.text.toString()
        val dato2 = binding.med2Editxt.text.toString()
        val dato3 = binding.med3Editxt.text.toString()
        val cantidad = binding.cantEditxt.text.toString()
        val precio = binding.precioEditxt.text.toString()
        val producto = binding.proEditxt.text.toString()
        val cliente = binding.clienteEditxt.text.toString()

        // Guardar los datos en SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("dato1", dato1)
        editor.putString("dato2", dato2)
        editor.putString("dato3", dato3)
        editor.putString("cantidad", cantidad)
        editor.putString("precio", precio)
        editor.putString("producto", producto)
        editor.putString("cliente",cliente)

        // Convertir la lista en una cadena JSON
        val gson = Gson()
        val listaString = gson.toJson(lista)
        editor.putString("lista", listaString)

        editor.apply()
    }

    private fun cargarDatosGuardados(): String {
        // Obtener los datos guardados desde SharedPreferences
        val dato1 = sharedPreferences.getString("dato1", "")
        val dato2 = sharedPreferences.getString("dato2", "")
        val dato3 = sharedPreferences.getString("dato3", "")
        val cantidad = sharedPreferences.getString("cantidad", "")
        val precio = sharedPreferences.getString("precio", "")
        val producto = sharedPreferences.getString("producto", "")
        val cliente = sharedPreferences.getString("cliente", "")

        // Actualizar los campos de la interfaz con los datos cargados (excepto clienteEditxt)
        binding.med1Editxt.setText(dato1)
        binding.med2Editxt.setText(dato2)
        binding.med3Editxt.setText(dato3)
        binding.cantEditxt.setText(cantidad)
        binding.precioEditxt.setText(precio)
        binding.proEditxt.setText(producto)
        // **Eliminar o comentar la siguiente línea para evitar sobrescribir clienteEditxt**
        // binding.clienteEditxt.setText(cliente)

        // Cargar la lista desde SharedPreferences
        val listaString = sharedPreferences.getString("lista", null)
        if (!listaString.isNullOrEmpty()) {
            val gson = Gson()
            val tipoLista = object : TypeToken<List<Listado>>() {}.type
            lista = gson.fromJson(listaString, tipoLista)
        }
        return cliente.toString()
    }

    // guardar completo
    @RequiresApi(Build.VERSION_CODES.O)
    private fun guardar() {
        val cliente = binding.clienteEditxt.text
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        val currentDateAndTime = sdf.format(Date())

        val nombreArchivo = if (binding.tvpCliente.text == "Presupuesto"){
            "Presupuesto ->${"($cliente)"} ${currentDateAndTime}.dat"}else
        {"Presupuesto ->${"($cliente)"}\n ${currentDateAndTime}.dat"}
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput(nombreArchivo, Context.MODE_PRIVATE)
            ObjectOutputStream(fileOutputStream).use { it.writeObject(lista) }
            Toast.makeText(this, "Archivo guardado con éxito", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar archivo", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }


    private fun abrir() {
        val paquete = intent.extras
        val li = paquete?.getSerializable("lista") as? List<*>
        val listaRecibida = li?.filterIsInstance<Listado>()?.toMutableList() ?: mutableListOf()

        if (lista.isNotEmpty()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmación")
            builder.setMessage("La lista actual no está vacía. ¿Deseas sumar los datos guardados a la lista existente?")
            builder.setPositiveButton("Sumar") { _, _ ->
                lista.addAll(listaRecibida)
                actualizar()
            }
            builder.setNegativeButton("Reemplazar") { _, _ ->
                lista.clear()
                lista.addAll(listaRecibida)
                actualizar()
            }
            builder.setNeutralButton("Cancelar", null)
            builder.show()
        } else {
            lista.addAll(listaRecibida)
            actualizar()
        }
    }

    //FUNCIONES DE DICTADO

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido. Puedes iniciar el dictado.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permiso denegado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            when (requestCode) {
                // ✅ MANTENER: Captura de imagen (tu código existente)
                REQUEST_IMAGE_CAPTURE -> {
                    if (resultCode == RESULT_OK) {
                        data?.extras?.get("data") as Bitmap
                    } else {
                        Toast.makeText(this, "Error al capturar la imagen", Toast.LENGTH_SHORT).show()
                    }
                }

                // ✅ MANTENER: Selección de galería (tu código existente)
                REQUEST_IMAGE_GALLERY -> {
                    if (resultCode == RESULT_OK && data != null) {
                        val selectedImageUri: Uri? = data.data

                        if (selectedImageUri != null) {
                            val imageUriString = selectedImageUri.toString()

                            // Guardar el URI completo
                            val uriCompleto = imageUriString
                            val uriAcortado = if (uriCompleto.length > 20) "...${uriCompleto.takeLast(20)}" else uriCompleto

                            // Guardar el URI completo en el objeto Listado
                            lista[selectedPosition].uri = uriCompleto

                            // Mostrar la versión acortada en la interfaz
                            usoImagenUri(uriAcortado)

                            // Cargar la imagen utilizando Glide en ivScan
                            Glide.with(this)
                                .load(selectedImageUri)

                        } else {
                            Toast.makeText(this, "Error: URI de imagen es nulo", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Error al seleccionar la imagen", Toast.LENGTH_SHORT).show()
                    }
                }

                // ✅ NUEVO: OCR - Solo agregar este caso
                CODIGO_SOLICITUD_OCR -> {
                    if (resultCode == RESULT_OK && data != null) {
                        @Suppress("UNCHECKED_CAST")
                        val elementosOcr = data.getSerializableExtra("elementos_ocr") as? ArrayList<Listado>

                        elementosOcr?.let { elementos ->
                            if (elementos.isNotEmpty()) {
                                // Agregar elementos a la lista principal
                                lista.addAll(elementos)

                                // Actualizar interfaz
                                actualizar()

                                // Mostrar confirmación
                                val mensaje = if (elementos.size == 1) {
                                    "✅ 1 elemento agregado desde imagen"
                                } else {
                                    "✅ ${elementos.size} elementos agregados desde imagen"
                                }

                                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

                                // Log para depuración
                                Log.d("OCR", "Elementos recibidos: ${elementos.size}")
                                elementos.forEachIndexed { indice, elemento ->
                                    Log.d("OCR", "Elemento $indice: ${elemento.medi1} x ${elemento.medi2} = ${elemento.canti}, ${elemento.producto}")
                                }
                            } else {
                                Toast.makeText(this, "No se encontraron elementos en la imagen", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "OCR cancelado", Toast.LENGTH_SHORT).show()
                    }
                }

                // ✅ MANTENER: Dictado (tu código existente)
                DICTADO_REQUEST_CODE -> {
                    if (resultCode == RESULT_OK && data != null) {
                        @Suppress("UNCHECKED_CAST")
                        val elementosDictados = data.getSerializableExtra("elementos_dictados") as? ArrayList<Listado>

                        elementosDictados?.let { elementos ->
                            lista.addAll(elementos)
                            actualizar()

                            val mensaje = if (elementos.size == 1) {
                                "✅ 1 elemento agregado por dictado"
                            } else {
                                "✅ ${elementos.size} elementos agregados por dictado"
                            }

                            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

                            Log.d("DICTADO", "Elementos recibidos: ${elementos.size}")
                            elementos.forEachIndexed { index, elemento ->
                                Log.d("DICTADO", "Elemento $index: ${elemento.medi1} x ${elemento.medi2} = ${elemento.canti}, ${elemento.producto}")
                            }
                        }
                    } else if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "Dictado cancelado", Toast.LENGTH_SHORT).show()
                    }
                }

                // ✅ MANTENER: Reconocimiento de voz anterior (tu código existente)
                RECORD_REQUEST_CODE -> {
                    if (resultCode == RESULT_OK && data != null) {
                        val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        binding.txMetroTot.text = result?.get(0)
                    }
                }

                // ✅ MANTENER: Presupuestos (tu código existente)
                RECEIVE_PRESUPUESTO_REQUEST -> {
                    if (resultCode == RESULT_OK && data != null) {
                        data.data?.let { uri ->
                            manejarArchivoPresupuesto(uri)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al procesar la solicitud: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    //FUNCIONES SCAN
// ✅ MANTENER ESTAS FUNCIONES (las necesitas para agregar imágenes a elementos)
    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("IntentReset")
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    private fun usoImagenUri(imageUriString: String) {
        if (selectedPosition != -1) {
            // Solo actualizar la interfaz con el URI acortado
            // No modifiques lista[selectedPosition].uriCompleto
            // Simplemente actualiza el texto o la representación acortada en la interfaz
            actualizar()
        }
        Log.d("Image URI", imageUriString)
    }

    //FUNCIONES PDF
    /*private fun openPdf() {
   generarPdf()
   val pdfFile = File(getExternalFilesDir(null), "Presupuesto_${clienteEditxt.text}.pdf")
   if (pdfFile.exists() && pdfFile.length() > 0) {
       val target = Intent(Intent.ACTION_VIEW)
       target.setDataAndType(Uri.fromFile(pdfFile), "application/pdf")
       target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
       val intent = Intent.createChooser(target, "Abrir archivo")
       startActivity(intent)
   } else {
       Toast.makeText(this, "Error al generar el archivo PDF", Toast.LENGTH_SHORT).show()
   }
}*/

    private fun openPdf() {
        generarPdf()
        val pdfFile = File(getExternalFilesDir(null), "Presupuesto_${binding.clienteEditxt.text}.pdf")
        if (pdfFile.exists() && pdfFile.length() > 0) {
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", pdfFile)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "application/pdf"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val intent = Intent.createChooser(shareIntent, "Compartir archivo")
            startActivity(intent)
        } else {
            Toast.makeText(this, "Error al generar el archivo PDF", Toast.LENGTH_SHORT).show()
        }
    }

    // Clase para gestionar el evento de numeración de páginas
    class PageNumeration : PdfPageEventHelper() {
        override fun onEndPage(writer: PdfWriter, document: Document) {
            val cb = writer.directContent
            val pageSize = document.pageSize

            // Fuente para la numeración
            val pageNumberFont = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)
            val phrase = Phrase("Página ${writer.pageNumber}", pageNumberFont)

            // Posición de la numeración (pie de página)
            ColumnText.showTextAligned(
                cb,
                Element.ALIGN_RIGHT,
                phrase,
                pageSize.right - document.rightMargin(),
                pageSize.bottom + 18f, // Ajustar la altura si es necesario
                0f
            )
        }
    }


    @SuppressLint("ResourceType")
    private fun generarPdf() {
        val cliente = binding.clienteEditxt.text.toString()
        val pdfFileName = "Presupuesto_${cliente}.pdf"
        val pdfFile = File(getExternalFilesDir(null), pdfFileName)

        val document = Document(PageSize.A4, 36f, 36f, 36f, 36f)
        val writer = PdfWriter.getInstance(document, FileOutputStream(pdfFile))

        // Añadir el evento para la numeración de páginas
        val event = PageNumeration()
        writer.pageEvent = event

        document.open()

        val tituloFont = Font(Font.FontFamily.HELVETICA, 27f, Font.BOLD)
        document.add(Paragraph("Proforma $cliente", tituloFont))

        if (lista.isEmpty()) {
            Toast.makeText(this, "La lista está vacía", Toast.LENGTH_SHORT).show()
            return
        }

        var itemNum = 0 // Para numerar los ítems

        for (item in lista) {
            itemNum++

            // Crear un bloque de contenido que se mantiene junto (KeepTogether)
            val block = PdfPTable(1)
            block.keepTogether = true // Esto evita que el ítem se corte entre páginas

            val itemTitleFont = Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD)
            val tituloItem = "Ítem $itemNum: ${item.producto}"
            val tituloItemParagraph = Paragraph(tituloItem, itemTitleFont)

            val cellTitulo = PdfPCell(tituloItemParagraph)
            cellTitulo.border = Rectangle.NO_BORDER
            block.addCell(cellTitulo)

            val table = PdfPTable(2)
            table.widthPercentage = 100f

            val ancho = item.medi1
            val alto = item.medi2
            val fondo = item.medi3
            val cantidad = item.canti
            val costo = item.costo
            val anexo = item.uri

            val textoMedidas = when (item.escala) {
                "p2", "m2" -> "Ancho: ${df1(ancho)}\nAlto: ${df1(alto)}\nCantidad: ${df1(cantidad)}"
                "ml" -> "Metros: ${df1(ancho)}\nCantidad: ${df1(cantidad)}"
                "m3" -> "Ancho: ${df1(ancho)}\nAlto: ${df1(alto)}\nFondo: ${df1(fondo)}\nCantidad: ${df1(cantidad)}"
                "uni" -> "Cantidad: ${df1(cantidad)}"
                else -> ""
            }

            val costoFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
            val textoCosto = Chunk("Costo: ${df2(costo)}", costoFont)

            val textoCompleto = Paragraph(textoMedidas)
            textoCompleto.add(Chunk("\n"))
            textoCompleto.add(textoCosto)

            val textCell = PdfPCell(textoCompleto)
            textCell.border = Rectangle.NO_BORDER
            textCell.setPadding(9f)
            table.addCell(textCell)

            var imageCell: PdfPCell

            if (anexo.isEmpty()) {
                // Si el anexo es nulo o vacío, dejamos la celda en blanco
                imageCell = PdfPCell(Paragraph(""))
            } else {
                try {
                    val imageUri = Uri.parse(anexo)
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))

                    if (bitmap != null) {
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val image = Image.getInstance(stream.toByteArray())
                        image.scaleToFit(270f, 270f)
                        imageCell = PdfPCell(image)
                    } else {
                        imageCell = PdfPCell(Paragraph("Imagen no disponible"))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    imageCell = PdfPCell(Paragraph("Imagen no disponible"))
                }
            }

            imageCell.border = Rectangle.NO_BORDER
            imageCell.setPadding(9f)
            table.addCell(imageCell)

            block.addCell(PdfPCell(table).apply { border = Rectangle.NO_BORDER })

            document.add(block) // Añadir el bloque completo al documento

            // Añadir una línea separadora entre los elementos
            val separator = LineSeparator()
            separator.lineColor = BaseColor.GRAY // Color de la línea
            separator.lineWidth = 1f // Grosor de la línea
            document.add(Chunk(separator))

            document.add(Paragraph("\n")) // Salto de línea entre ítems
        }

        // Agregar el cuadro del precio total
        val totalBox = Rectangle(10f, 50f, 550f, 100f)
        totalBox.borderWidth = 1f
        totalBox.borderColor = BaseColor.BLACK
        document.add(totalBox)

        val precioTotal = binding.precioTotal.text.toString()
        val tituloTotal = "Precio total: S/.$precioTotal"
        val tituloTotalItem = Paragraph(tituloTotal, Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD))
        tituloTotalItem.alignment = Element.ALIGN_RIGHT
        document.add(tituloTotalItem)

        document.close()

        Toast.makeText(this, "PDF generado: ${pdfFile.absolutePath}", Toast.LENGTH_LONG).show()
    }

    // FUNCIONES PARA ENVIAR Y ABRIR PERUSPUESTOS

    // Función para manejar presupuestos recibidos:
    @SuppressLint("NewApi")
    private fun manejarPresupuestoRecibido() {
        manejarMensajeMedidas()
        // Manejar contenido JSON directo (nuevo método)
        val jsonContent = intent.getStringExtra("cargar_presupuesto_json")
        val nombreArchivo = intent.getStringExtra("cargar_presupuesto_nombre")

        if (jsonContent != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Presupuesto Recibido")
            builder.setMessage("Has recibido el presupuesto: $nombreArchivo\n¿Deseas cargarlo?")
            builder.setPositiveButton("Cargar") { _, _ ->
                cargarPresupuestoDesdeJson(jsonContent)
            }
            builder.setNegativeButton("Cancelar", null)
            builder.show()
            return
        }

        // Método anterior para URIs locales (mantener por compatibilidad)
        val presupuestoUri = intent.getStringExtra("cargar_presupuesto_uri")
        val nombreArchivoUri = intent.getStringExtra("cargar_presupuesto_nombre")

        if (presupuestoUri != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Presupuesto Recibido")
            builder.setMessage("Has recibido el presupuesto: $nombreArchivoUri\n¿Deseas cargarlo?")
            builder.setPositiveButton("Cargar") { _, _ ->
                val uri = Uri.parse(presupuestoUri)
                manejarArchivoPresupuesto(uri)
            }
            builder.setNegativeButton("Cancelar", null)
            builder.show()
        }
    }
    // Agregar onNewIntent para manejar intents cuando la app ya está abierta:
    override fun onNewIntent(intent: Intent) {
        intent.let { super.onNewIntent(it) }
        setIntent(intent)
        manejarPresupuestoRecibido()
    }
    // Función para mostrar menú de opciones del presupuesto:

    private fun mostrarMenuPresupuesto() {
        val opciones = if (lista.isEmpty()) {
            arrayOf("Cargar presupuesto desde archivo")
        } else {
            arrayOf(
                "Enviar por chat",
                "🔧 Edición masiva",  // ✅ AGREGAR ESTA LÍNEA
                "Cargar presupuesto desde archivo",
                "Guardar como archivo JSON",
                "Compartir como archivo"
            )
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Opciones de Presupuesto")
        builder.setItems(opciones) { _, which ->
            Log.d("DEBUG", "Opción menú principal: $which")
            when (which) {
                0 -> if (lista.isEmpty()) {
                    abrirSelectorPresupuesto()
                } else {
                    enviarPresupuestoPorChat()
                }
                1 -> {
                    Log.d("DEBUG", "Llamando a mostrarMenuEdicionMasiva()")
                    mostrarMenuEdicionMasiva()
                }
                2 -> abrirSelectorPresupuesto()
                3 -> if (lista.isNotEmpty()) guardarComoJSON()
                4 -> if (lista.isNotEmpty()) compartirPresupuesto()
            }
        }

        builder.show()
    }
    // Función para guardar como JSON:
    private fun guardarComoJSON() {
        val cliente = binding.clienteEditxt.text.toString().takeIf { it.isNotEmpty() } ?: "Sin nombre"
        val presupuesto = PresupuestoCompleto(
            cliente = cliente,
            fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            elementos = lista.toList(),
            precioTotal = binding.precioTotal.text.toString(),
            metrosTotal = binding.metrosTotal.text.toString(),
            piesTotal = binding.piesTotal.text.toString(),
            perimetroTotal = binding.per.text.toString()
        )

        val gson = Gson()
        val jsonString = gson.toJson(presupuesto)

        val fileName = "presupuesto_${cliente}_${System.currentTimeMillis()}.json"

        // ✅ CREAR ESTRUCTURA DE CARPETAS:
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val crystalDir = File(downloadsDir, "Crystal")
        val presupuestosDir = File(crystalDir, "PresupuestosJ")

        // Crear las carpetas si no existen
        if (!presupuestosDir.exists()) {
            presupuestosDir.mkdirs() // Crea toda la estructura de carpetas
        }

        // Archivo final en la carpeta correcta
        val file = File(presupuestosDir, fileName)

        try {
            file.writeText(jsonString)
            Toast.makeText(this, "✅ Presupuesto guardado en:\nDescargas/Crystal/PresupuestosJ/\n$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    // Función para compartir presupuesto:
    private fun compartirPresupuesto() {
        val cliente = binding.clienteEditxt.text.toString().takeIf { it.isNotEmpty() } ?: "Sin nombre"
        val presupuesto = PresupuestoCompleto(
            cliente = cliente,
            fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            elementos = lista.toList(),
            precioTotal = binding.precioTotal.text.toString(),
            metrosTotal = binding.metrosTotal.text.toString(),
            piesTotal = binding.piesTotal.text.toString(),
            perimetroTotal = binding.per.text.toString()
        )

        val gson = Gson()
        val jsonString = gson.toJson(presupuesto)

        val fileName = "presupuesto_${cliente}_${System.currentTimeMillis()}.json"
        val file = File(cacheDir, fileName)

        try {
            file.writeText(jsonString)
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Presupuesto - $cliente")
                putExtra(Intent.EXTRA_TEXT, "Presupuesto generado desde Crystal App")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Compartir presupuesto"))

        } catch (e: Exception) {
            Toast.makeText(this, "Error al compartir: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    // Modificar la función cargarPresupuestoDesdeJson para mejor UX:
    private fun cargarPresupuestoDesdeJson(jsonString: String): Boolean {
        return try {
            val gson = Gson()
            val presupuesto = gson.fromJson(jsonString, PresupuestoCompleto::class.java)

            // Validar que el presupuesto sea válido
            if (presupuesto.elementos.isEmpty()) {
                Toast.makeText(this, "El presupuesto no contiene elementos válidos", Toast.LENGTH_SHORT).show()
                return false
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("📋 Cargar Presupuesto")
            builder.setMessage(
                "👤 Cliente: ${presupuesto.cliente}\n" +
                        "📅 Fecha: ${presupuesto.fechaCreacion}\n" +
                        "📦 Elementos: ${presupuesto.elementos.size}\n" +
                        "💰 Total: S/${presupuesto.precioTotal}\n" +
                        "📐 Metros²: ${presupuesto.metrosTotal}\n" +
                        "📏 Pies²: ${presupuesto.piesTotal}\n\n" +
                        "¿Deseas cargar este presupuesto?"
            )

            builder.setPositiveButton("✅ Cargar") { _, _ ->
                if (lista.isNotEmpty()) {
                    mostrarOpcionesCargar(presupuesto)
                } else {
                    cargarPresupuestoDirecto(presupuesto)
                }
            }

            builder.setNegativeButton("❌ Cancelar", null)
            builder.show()

            true
        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error al leer presupuesto: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }
    // Funciones auxiliares para cargar presupuesto:
    @SuppressLint("NewApi", "SetTextI18n")
    private fun mostrarOpcionesCargar(presupuesto: PresupuestoCompleto) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("⚙️ ¿Cómo cargar?")
        builder.setMessage("Ya tienes elementos en tu presupuesto actual.")

        builder.setPositiveButton("➕ Sumar") { _, _ ->
            lista.addAll(presupuesto.elementos)
            val clienteActual = binding.clienteEditxt.text.toString()
            val nuevoCliente = if (clienteActual.isNotEmpty()) {
                "$clienteActual + ${presupuesto.cliente}"
            } else {
                presupuesto.cliente
            }
            binding.clienteEditxt.setText(nuevoCliente)
            binding.tvpCliente.text = "Presupuesto de $nuevoCliente"
            actualizar()
            Toast.makeText(this, "✅ Presupuesto sumado correctamente", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("🔄 Reemplazar") { _, _ ->
            cargarPresupuestoDirecto(presupuesto)
        }

        builder.setNeutralButton("❌ Cancelar", null)
        builder.show()
    }
    @SuppressLint("NewApi", "SetTextI18n")
    private fun cargarPresupuestoDirecto(presupuesto: PresupuestoCompleto) {
        lista.clear()
        lista.addAll(presupuesto.elementos)
        binding.clienteEditxt.setText(presupuesto.cliente)
        binding.tvpCliente.text = "Presupuesto de ${presupuesto.cliente}"

        // Ajustar visibilidad
        binding.lyCuello.visibility = View.GONE
        binding.lyCuerpo.visibility = View.VISIBLE

        actualizar()
        Toast.makeText(this, "✅ Presupuesto cargado correctamente", Toast.LENGTH_SHORT).show()
    }
    private fun enviarPresupuestoPorChat() {
        if (lista.isEmpty()) {
            Toast.makeText(this, "No hay elementos en el presupuesto para enviar", Toast.LENGTH_SHORT).show()
            return
        }

        val cliente = binding.clienteEditxt.text.toString().takeIf { it.isNotEmpty() } ?: "Sin nombre"

        // Crear objeto completo del presupuesto
        val presupuesto = PresupuestoCompleto(
            cliente = cliente,
            fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            elementos = lista.toList(),
            precioTotal = binding.precioTotal.text.toString(),
            metrosTotal = binding.metrosTotal.text.toString(),
            piesTotal = binding.piesTotal.text.toString(),
            perimetroTotal = binding.per.text.toString()
        )

        // Serializar a JSON
        val gson = Gson()
        val jsonPresupuesto = gson.toJson(presupuesto)

        // Crear archivo temporal
        val fileName = "presupuesto_${cliente}_${System.currentTimeMillis()}.json"
        val file = File(cacheDir, fileName)

        try {
            file.writeText(jsonPresupuesto)
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )

            // Abrir ListChatActivity con el archivo
            val intent = Intent(this, ListChatActivity::class.java)
            intent.putExtra("usuario", currentUserId)
            intent.putExtra("enviar_presupuesto", uri.toString())
            intent.putExtra("nombre_presupuesto", fileName)
            startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(this, "Error al preparar presupuesto: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    // Función para manejar archivos de presupuesto recibidos
    private fun manejarArchivoPresupuesto(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val jsonString = inputStream?.bufferedReader().use { it?.readText() }

            if (jsonString != null) {
                cargarPresupuestoDesdeJson(jsonString)
            } else {
                Toast.makeText(this, "No se pudo leer el archivo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al abrir archivo: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun abrirSelectorPresupuesto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            // ✅ AGREGAR ESTO para empezar en la carpeta correcta:
            putExtra("android.provider.extra.INITIAL_URI",
                Uri.parse("content://com.android.externalstorage.documents/document/primary%3ADownload%2FCrystal%2FPresupuestosJ"))
        }
        startActivityForResult(intent, RECEIVE_PRESUPUESTO_REQUEST)
    }
    //Funciones Para Abrir e Enviar Presupuestos Por Mensaje Texto
    // Función para detectar si un mensaje tiene formato de medidas
    private fun esFormatoMedidas(mensaje: String): Boolean {
        val lineas = mensaje.trim().split("\n").filter { it.isNotBlank() }

        if (lineas.size < 2) return false

        // Regex para detectar medidas: número x número = número (con decimales)
        val regexMedida = Regex("""^\s*\d+(\.\d+)?\s*[xX]\s*\d+(\.\d+)?\s*=\s*\d+(\.\d+)?\s*$""")

        // Buscar la primera línea que sea una medida
        var primeraMedida = -1
        for (i in lineas.indices) {
            if (regexMedida.matches(lineas[i].trim())) {
                primeraMedida = i
                break
            }
        }

        // Debe haber al menos una línea antes (producto) y debe encontrar medidas
        if (primeraMedida <= 0) return false

        // Desde la primera medida en adelante, TODAS deben ser medidas
        for (i in primeraMedida until lineas.size) {
            if (!regexMedida.matches(lineas[i].trim())) {
                return false
            }
        }

        return true
    }
    // Función para parsear mensaje con medidas
    private fun parsearMensajeMedidas(mensaje: String): Pair<String, List<Triple<Float, Float, Float>>>? {
        try {
            val lineas = mensaje.trim().split("\n").filter { it.isNotBlank() }

            if (!esFormatoMedidas(mensaje)) return null

            val regexMedida = Regex("""^\s*\d+(\.\d+)?\s*[xX]\s*\d+(\.\d+)?\s*=\s*\d+(\.\d+)?\s*$""")

            // Encontrar donde empiezan las medidas
            var inicieMedidas = -1
            for (i in lineas.indices) {
                if (regexMedida.matches(lineas[i].trim())) {
                    inicieMedidas = i
                    break
                }
            }

            // ✅ TODO ANTES DE LA PRIMERA MEDIDA = PRODUCTO
            val producto = lineas.take(inicieMedidas).joinToString(" ").trim()
            val medidas = mutableListOf<Triple<Float, Float, Float>>()

            // Procesar todas las medidas
            for (i in inicieMedidas until lineas.size) {
                val linea = lineas[i].trim()

                val partes = linea.split("=")
                if (partes.size == 2) {
                    val cantidad = partes[1].trim().toFloatOrNull()
                    val medidaParte = partes[0].trim()
                    val medidasSplit = medidaParte.split(Regex("[xX]"))

                    if (medidasSplit.size == 2 && cantidad != null) {
                        val med1 = medidasSplit[0].trim().toFloatOrNull()
                        val med2 = medidasSplit[1].trim().toFloatOrNull()

                        if (med1 != null && med2 != null) {
                            medidas.add(Triple(med1, med2, cantidad))
                        }
                    }
                }
            }

            return if (medidas.isNotEmpty()) Pair(producto, medidas) else null

        } catch (e: Exception) {
            return null
        }
    }
    // Función para importar medidas parseadas
    @SuppressLint("NewApi")
    private fun importarMedidasParseadas(producto: String, medidas: List<Triple<Float, Float, Float>>) {
        if (medidas.isEmpty()) {
            Toast.makeText(this, "No se encontraron medidas válidas", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("📐 Importar Medidas")
        builder.setMessage(
            "Producto: $producto\n" +
                    "Elementos encontrados: ${medidas.size}\n" +
                    "Unidad: Pies cuadrados (p2)\n\n" +
                    "Ejemplos:\n" +
                    medidas.take(3).joinToString("\n") { "${it.first} x ${it.second} = ${it.third}" } +
                    if (medidas.size > 3) "\n..." else "" +
                            "\n\n¿Deseas buscar el precio en la base de datos?"
        )

        builder.setPositiveButton("💰 Buscar precio") { _, _ ->
            // Buscar precio en base de datos
            buscarPrecioEnBaseDatos(producto, medidas)
        }

        builder.setNegativeButton("📋 Sin precio") { _, _ ->
            // Importar directamente con precio 0
            importarMedidasConPrecio(producto, medidas, 0.0)
        }

        builder.setNeutralButton("❌ Cancelar", null)
        builder.show()
    }
    // 4. Función para manejar mensaje de medidas recibido (llamada desde ChatActivity)

    private fun manejarMensajeMedidas() {
        val mensajeTexto = intent.getStringExtra("importar_medidas_texto")

        if (mensajeTexto != null) {
            val resultado = parsearMensajeMedidas(mensajeTexto)

            if (resultado != null) {
                val (producto, medidas) = resultado
                importarMedidasParseadas(producto, medidas)
            } else {
                Toast.makeText(this, "❌ No se pudo parsear el mensaje de medidas", Toast.LENGTH_SHORT).show()
            }

            // Limpiar el extra para evitar procesarlo de nuevo
            intent.removeExtra("importar_medidas_texto")
        }
    }
    // 1. Función para buscar precios en la base de datos
    @SuppressLint("NewApi")
    private fun buscarPrecioEnBaseDatos(producto: String, medidas: List<Triple<Float, Float, Float>>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = DatabaseProvider.getInstance(this@MainActivity)

                // Buscar productos similares
                val productosEncontrados = db.productDao().searchProductsByDescription("%$producto%")

                withContext(Dispatchers.Main) {
                    if (productosEncontrados.isNotEmpty()) {
                        // Mostrar opciones de productos encontrados
                        mostrarOpcionesProductos(productosEncontrados, producto, medidas)
                    } else {
                        // No se encontraron productos, continuar con precio 0
                        Toast.makeText(this@MainActivity, "❌ No se encontraron productos similares en la base de datos", Toast.LENGTH_SHORT).show()
                        importarMedidasConPrecio(producto, medidas, 0.0)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error al buscar en BD: ${e.message}", Toast.LENGTH_SHORT).show()
                    importarMedidasConPrecio(producto, medidas, 0.0)
                }
            }
        }
    }

    // 2. Función para mostrar opciones de productos encontrados
    @SuppressLint("DefaultLocale")

    private fun mostrarOpcionesProductos(productos: List<Product>, productoOriginal: String, medidas: List<Triple<Float, Float, Float>>) {
        val opcionesLista = mutableListOf<String>()

        productos.forEach { producto ->
            opcionesLista.add("💰 ${producto.description}\n   Precio: S/ ${String.format("%.2f", producto.price)}")
        }

        opcionesLista.add("📋 Sin precio (S/ 0.00)")
        opcionesLista.add("❌ Cancelar")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("💰 Seleccionar Producto (${productos.size} encontrados)")

        builder.setItems(opcionesLista.toTypedArray()) { _, which ->
            when {
                which < productos.size -> {
                    val productoSeleccionado = productos[which]
                    Toast.makeText(this, "🔄 Seleccionado: ${productoSeleccionado.description}", Toast.LENGTH_SHORT).show()
                    importarMedidasConPrecio(productoOriginal, medidas, productoSeleccionado.price)
                }
                which == productos.size -> {
                    Toast.makeText(this, "📋 Sin precio", Toast.LENGTH_SHORT).show()
                    importarMedidasConPrecio(productoOriginal, medidas, 0.0)
                }
                else -> {
                    Toast.makeText(this, "❌ Cancelado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.show()
    }
    @SuppressLint("SetTextI18n", "DefaultLocale")

    private fun importarMedidasConPrecio(producto: String, medidas: List<Triple<Float, Float, Float>>, precio: Double) {
        var elementosAgregados = 0

        for ((med1, med2, cantidad) in medidas) {
            try {
                // Configurar temporalmente la unidad para que conver() funcione
                val unidadOriginal = binding.prTxt.text.toString()
                binding.prTxt.text = "Centímetros"

                // Calcular correctamente con conver()
                val piescua = pies(med1, med2)
                val metroscua = metroCua(med1, med2)
                val ml = mLineales(med1, med2)
                val cub = mCubicos(med1, med2, 1f)
                val peri = ((conver(med1) * 2) + (conver(med2) * 2))

                // Restaurar unidad original
                binding.prTxt.text = unidadOriginal

                // Calcular costo total
                val costoTotal = (piescua * cantidad) * precio.toFloat()

                val elemento = Listado(
                    escala = "p2",
                    uni = "Centímetros",
                    medi1 = med1,
                    medi2 = med2,
                    medi3 = 1f,
                    canti = cantidad,
                    piescua = piescua * cantidad,
                    precio = precio.toFloat(),
                    costo = costoTotal,
                    producto = producto,
                    peri = peri,
                    metcua = metroscua,
                    metli = ml * cantidad,
                    metcub = cub,
                    color = ContextCompat.getColor(this, R.color.color),
                    uri = ""
                )

                lista.add(elemento)
                elementosAgregados++

            } catch (e: Exception) {
                continue
            }
        }

        if (elementosAgregados > 0) {
            actualizar()
            val mensajePrecio = if (precio > 0) {
                "con precio S/ ${String.format("%.2f", precio)}"
            } else {
                "sin precio (completar manualmente)"
            }
            Toast.makeText(this, "✅ $elementosAgregados elementos importados $mensajePrecio", Toast.LENGTH_LONG).show()

            if (precio == 0.0) {
                binding.precioEditxt.requestFocus()
            }
        } else {
            Toast.makeText(this, "❌ No se pudo importar ningún elemento", Toast.LENGTH_SHORT).show()
        }
    }

    //FUNCIONES PARA EDITAR MASIVAMENTE
    // VERSIÓN COMPATIBLE CON API 21 - REEMPLAZAR FUNCIONES ANTERIORES

    // 1. Función principal para mostrar menú (sin problemas de API)
    private fun mostrarMenuEdicionMasiva() {
        if (lista.isEmpty()) {
            Toast.makeText(this, "No hay elementos para editar", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("🔧 Edición Masiva")
        // ← QUITAR esta línea: builder.setMessage("Selecciona qué quieres hacer:")

        val opciones = arrayOf(
            "📝 Editar precios por producto",
            "🏷️ Cambiar nombre de producto",
            "💰 Aplicar descuento general",
            "🔄 Recalcular todos los costos"
        )

        builder.setItems(opciones) { _, which ->
            when (which) {
                0 -> mostrarEdicionPreciosPorProductoCompatible()
                1 -> mostrarCambioNombreProductoCompatible()
                2 -> mostrarAplicarDescuentoCompatible()
                3 -> recalcularTodosLosCostosCompatible()
            }
        }

        builder.setNegativeButton("❌ Cancelar", null)
        builder.show()
    }

    // 2. Función compatible para agrupar productos (sin groupBy)
    @SuppressLint("NewApi")
    private fun mostrarEdicionPreciosPorProductoCompatible() {
        // Crear mapa manual compatible con API 21
        val productosUnicos = mutableMapOf<String, MutableList<Listado>>()

        // Agrupar manualmente (compatible API 21)
        for (elemento in lista) {
            val producto = elemento.producto
            if (productosUnicos.containsKey(producto)) {
                productosUnicos[producto]!!.add(elemento)
            } else {
                productosUnicos[producto] = mutableListOf(elemento)
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("💰 Editar Precios por Producto")

        // Crear lista de opciones
        val opcionesLista = mutableListOf<String>()
        val productosOrdenados = mutableListOf<String>()

        for ((producto, elementos) in productosUnicos) {
            val precioActual = elementos[0].precio
            val cantidad = elementos.size

            opcionesLista.add("🏷️ $producto\n💰 Precio: S/ ${formatearPrecio(precioActual)}\n📦 $cantidad elementos")
            productosOrdenados.add(producto)
        }

        builder.setItems(opcionesLista.toTypedArray()) { _, which ->
            val productoSeleccionado = productosOrdenados[which]
            val elementosDelProducto = productosUnicos[productoSeleccionado]!!
            mostrarDialogoEditarPrecioCompatible(productoSeleccionado, elementosDelProducto)
        }

        builder.setNegativeButton("🔙 Volver", null)
        builder.show()
    }

    // 3. Función para formatear precio compatible
    private fun formatearPrecio(precio: Float): String {
        return "%.2f".format(precio)
    }

    // 4. Diálogo para editar precio compatible
    @SuppressLint("SetTextI18n")
    private fun mostrarDialogoEditarPrecioCompatible(producto: String, elementos: List<Listado>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("💰 Editar Precio")

        val precioActual = elementos[0].precio
        val mensaje = "🏷️ Producto: $producto\n" +
                "📦 Elementos: ${elementos.size}\n" +
                "💰 Precio actual: S/ ${formatearPrecio(precioActual)}\n\n" +
                "Ingresa el nuevo precio:"

        builder.setMessage(mensaje)

        // Input para nuevo precio
        val input = EditText(this)
        input.setText(precioActual.toString())
        input.selectAll()
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        builder.setView(input)

        builder.setPositiveButton("✅ Aplicar") { _, _ ->
            val nuevoPrecio = input.text.toString().toFloatOrNull()

            if (nuevoPrecio != null && nuevoPrecio > 0) {
                aplicarNuevoPrecioCompatible(elementos, nuevoPrecio)
            } else {
                Toast.makeText(this, "❌ Precio inválido", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("❌ Cancelar", null)
        builder.show()

        // Mostrar teclado
        input.requestFocus()
    }

    // 5. Aplicar nuevo precio compatible
    private fun aplicarNuevoPrecioCompatible(elementos: List<Listado>, nuevoPrecio: Float) {
        var elementosActualizados = 0

        for (elemento in elementos) {
            // Encontrar índice en lista principal
            var index = -1
            for (i in 0 until lista.size) {
                if (lista[i] === elemento) { // Comparación por referencia
                    index = i
                    break
                }
            }

            if (index != -1) {
                // Actualizar precio
                lista[index].precio = nuevoPrecio

                // Recalcular costo según escala
                val nuevoCosto = when (lista[index].escala) {
                    "p2" -> lista[index].piescua * nuevoPrecio
                    "m2" -> lista[index].metcua * nuevoPrecio
                    "ml" -> lista[index].metli * nuevoPrecio
                    "m3" -> lista[index].metcub * nuevoPrecio
                    "uni" -> lista[index].canti * nuevoPrecio
                    else -> lista[index].piescua * nuevoPrecio
                }

                lista[index].costo = nuevoCosto
                elementosActualizados++
            }
        }

        // Actualizar interfaz
        actualizar()

        Toast.makeText(this, "✅ $elementosActualizados elementos actualizados con precio S/ ${formatearPrecio(nuevoPrecio)}", Toast.LENGTH_LONG).show()
    }

    // 6. Cambio de nombre compatible
    private fun mostrarCambioNombreProductoCompatible() {
        // Agrupar productos manualmente
        val productosUnicos = mutableMapOf<String, MutableList<Listado>>()

        for (elemento in lista) {
            val producto = elemento.producto
            if (productosUnicos.containsKey(producto)) {
                productosUnicos[producto]!!.add(elemento)
            } else {
                productosUnicos[producto] = mutableListOf(elemento)
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("🏷️ Cambiar Nombre de Producto")

        val opcionesLista = mutableListOf<String>()
        val productosOrdenados = mutableListOf<String>()

        for ((producto, elementos) in productosUnicos) {
            opcionesLista.add("🏷️ $producto (${elementos.size} elementos)")
            productosOrdenados.add(producto)
        }

        builder.setItems(opcionesLista.toTypedArray()) { _, which ->
            val productoSeleccionado = productosOrdenados[which]
            val elementosDelProducto = productosUnicos[productoSeleccionado]!!
            mostrarDialogoCambiarNombreCompatible(productoSeleccionado, elementosDelProducto)
        }

        builder.setNegativeButton("🔙 Volver", null)
        builder.show()
    }

    // 7. Diálogo cambiar nombre compatible
    private fun mostrarDialogoCambiarNombreCompatible(productoActual: String, elementos: List<Listado>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("🏷️ Cambiar Nombre")
        builder.setMessage("Producto actual: $productoActual\nElementos: ${elementos.size}\n\nNuevo nombre:")

        val input = EditText(this)
        input.setText(productoActual)
        input.selectAll()

        builder.setView(input)

        builder.setPositiveButton("✅ Cambiar") { _, _ ->
            val nuevoNombre = input.text.toString().trim()

            if (nuevoNombre.isNotEmpty()) {
                // Cambiar nombre en todos los elementos
                for (elemento in elementos) {
                    var index = -1
                    for (i in 0 until lista.size) {
                        if (lista[i] === elemento) {
                            index = i
                            break
                        }
                    }
                    if (index != -1) {
                        lista[index].producto = nuevoNombre
                    }
                }

                actualizar()
                Toast.makeText(this, "✅ Nombre cambiado a: $nuevoNombre", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "❌ Nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("❌ Cancelar", null)
        builder.show()
    }

    // 8. Descuento compatible
    private fun mostrarAplicarDescuentoCompatible() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("💰 Aplicar Descuento General")
        builder.setMessage("Aplicar descuento a TODOS los elementos:\n\nIngresa el porcentaje de descuento:")

        val input = EditText(this)
        input.hint = "Ejemplo: 10 (para 10%)"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        builder.setView(input)

        builder.setPositiveButton("✅ Aplicar") { _, _ ->
            val porcentaje = input.text.toString().toFloatOrNull()

            if (porcentaje != null && porcentaje > 0 && porcentaje <= 100) {
                val factor = 1 - (porcentaje / 100)

                var elementosActualizados = 0
                for (i in 0 until lista.size) {
                    val nuevoPrecio = lista[i].precio * factor
                    lista[i].precio = nuevoPrecio

                    // Recalcular costo
                    val nuevoCosto = when (lista[i].escala) {
                        "p2" -> lista[i].piescua * nuevoPrecio
                        "m2" -> lista[i].metcua * nuevoPrecio
                        "ml" -> lista[i].metli * nuevoPrecio
                        "m3" -> lista[i].metcub * nuevoPrecio
                        "uni" -> lista[i].canti * nuevoPrecio
                        else -> lista[i].piescua * nuevoPrecio
                    }

                    lista[i].costo = nuevoCosto
                    elementosActualizados++
                }

                actualizar()
                Toast.makeText(this, "✅ Descuento del $porcentaje% aplicado a $elementosActualizados elementos", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(this, "❌ Porcentaje inválido (debe ser entre 1 y 100)", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("❌ Cancelar", null)
        builder.show()
    }

    // 9. Recalcular compatible
    private fun recalcularTodosLosCostosCompatible() {
        for (i in 0 until lista.size) {
            val nuevoCosto = when (lista[i].escala) {
                "p2" -> lista[i].piescua * lista[i].precio
                "m2" -> lista[i].metcua * lista[i].precio
                "ml" -> lista[i].metli * lista[i].precio
                "m3" -> lista[i].metcub * lista[i].precio
                "uni" -> lista[i].canti * lista[i].precio
                else -> lista[i].piescua * lista[i].precio
            }
            lista[i].costo = nuevoCosto
        }

        actualizar()
        Toast.makeText(this, "✅ Todos los costos recalculados", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarDialogoPrecioPactado() {
        if (lista.isEmpty()) {
            Toast.makeText(this, "No hay elementos para ajustar", Toast.LENGTH_SHORT).show()
            return
        }

        // Calcular costo total actual
        val costoTotalActual = lista.sumOf { it.costo.toDouble() }.toFloat()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("💰 Precio Pactado")

        val mensaje = "💰 Costo actual: S/ ${df2(costoTotalActual)}\n\n" +
                "Ingresa el precio pactado con el cliente:"

        builder.setMessage(mensaje)

        // Input para el precio pactado
        val input = EditText(this)
        input.hint = "Ejemplo: 1500.00"
        input.setText(costoTotalActual.toString())
        input.selectAll()
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        builder.setView(input)

        builder.setPositiveButton("✅ Aplicar") { _, _ ->
            val precioPactado = input.text.toString().toFloatOrNull()

            if (precioPactado != null && precioPactado > 0) {
                aplicarPrecioPactado(costoTotalActual, precioPactado)
            } else {
                Toast.makeText(this, "❌ Precio inválido", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("❌ Cancelar", null)
        builder.show()

        // Mostrar teclado
        input.requestFocus()
    }

    private fun aplicarPrecioPactado(costoActual: Float, precioPactado: Float) {
        // Calcular factor de descuento/aumento
        val factor = precioPactado / costoActual

        val porcentajeDescuento = if (factor < 1) {
            ((1 - factor) * 100)
        } else {
            0f
        }

        var elementosActualizados = 0

        for (i in 0 until lista.size) {
            // Aplicar factor al precio
            val nuevoPrecio = lista[i].precio * factor
            lista[i].precio = nuevoPrecio

            // Recalcular costo con el nuevo precio
            val nuevoCosto = when (lista[i].escala) {
                "p2" -> lista[i].piescua * nuevoPrecio
                "m2" -> lista[i].metcua * nuevoPrecio
                "ml" -> lista[i].metli * nuevoPrecio
                "m3" -> lista[i].metcub * nuevoPrecio
                "uni" -> lista[i].canti * nuevoPrecio
                else -> lista[i].piescua * nuevoPrecio
            }

            lista[i].costo = nuevoCosto
            elementosActualizados++
        }

        // Actualizar interfaz
        actualizar()

        // Mensaje informativo
        val mensaje = if (factor < 1) {
            "✅ Precio pactado: S/ ${df2(precioPactado)}\n" +
                    "📉 Descuento aplicado: ${df2(porcentajeDescuento)}%\n" +
                    "📦 $elementosActualizados elementos actualizados"
        } else {
            "✅ Precio pactado: S/ ${df2(precioPactado)}\n" +
                    "📈 Aumento aplicado: ${df2((factor - 1) * 100)}%\n" +
                    "📦 $elementosActualizados elementos actualizados"
        }

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    //FUNCIONES DE CHAT

    private fun setupUnreadMessagesListener() {
        // Escuchar los chats de la colección personal del usuario
        val userChatsRef = db.collection("usuarios").document(currentUserId).collection("chats")
        userChatsRef.addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                // Para cada chat, establecer listener a la subcolección de mensajes para contar los no leídos
                for (chatDoc in snapshot.documents) {
                    val chatId = chatDoc.id
                    val chatRef = db.collection("chats").document(chatId)
                    chatRef.collection("messages")
                        .whereEqualTo("leido", false)
                        .whereNotEqualTo("from", currentUserId)
                        .addSnapshotListener { msgsSnap, _ ->
                            if (msgsSnap != null) {
                                val count = msgsSnap.size()
                                unreadCountByChat[chatId] = count
                                actualizarBadgeChat()
                            }
                        }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarBadgeChat() {
        val totalUnread = unreadCountByChat.values.sum()
        if (totalUnread > 0) {
            // Cambiar el ícono a la versión roja y mostrar el badge
            binding.btnChat.setImageResource(R.drawable.ic_mensajesno) // Reemplaza por tu recurso de ícono rojo
            binding.tvBadge.text = totalUnread.toString()
            binding.tvBadge.visibility = View.VISIBLE
        } else {
            binding.btnChat.setImageResource(R.drawable.ic_mensajes) // Versión blanca por defecto
            binding.tvBadge.visibility = View.GONE
        }
    }

    private fun obtenerUsuarioActual(): String {
        // Implementa la lógica para obtener el id del usuario autenticado (por ejemplo, usando FirebaseAuth)
        return "usuarioActualEjemplo"
    }

    //FUNCIONES GENERALES

    private fun df1(defo: Float): String {
        val resultado =if ("$defo".endsWith(".0")) {"$defo".replace(".0", "")}
        else { "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }
    private fun df2(defo: Float): String {
        return "%.2f".format(defo).replace(".", ",")
    }
    private fun uni1() {
        usados = findViewById(R.id.spinner_usa)
        val listaUsados = arrayOf("p2", "m2", "ml", "m3", "uni")
        val colores = arrayOf(R.color.color, R.color.fucsia, R.color.verde, R.color.violeta, R.color.naranja)
        val adaptadorU = object : ArrayAdapter<String>(
            this,
            R.layout.lista_spinner, listaUsados) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(getColor(position))
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(getColor(position))
                return view
            }

            fun getColor(position: Int): Int {
                return ContextCompat.getColor(this@MainActivity, colores[position])
            }
        }
        usados.adapter = adaptadorU
        usados.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View?,position: Int, p3: Long) {
                binding.usTxt.text = usados.selectedItem.toString()
                colorSeleccionado = adaptadorU.getColor(position)
                when (binding.usTxt.text) {
                    "p2" -> {
                        binding.med3Lay.visibility = View.GONE
                        binding.med2Lay.visibility = View.VISIBLE
                        binding.med1Lay.visibility = View.VISIBLE
                        binding.spinnerUni.visibility = View.VISIBLE
                        binding.med1Editxt.setText("")
                        binding.med2Editxt.setText("")
                        binding.med1Editxt.requestFocus() }
                    "m2" -> {
                        binding.med3Lay.visibility = View.GONE
                        binding.med2Lay.visibility = View.VISIBLE
                        binding.med1Lay.visibility = View.VISIBLE
                        binding.spinnerUni.visibility = View.VISIBLE
                        binding.med1Editxt.setText("")
                        binding.med2Editxt.setText("")
                        binding.med1Editxt.requestFocus() }
                    "ml" -> {
                        binding.med3Lay.visibility = View.GONE
                        binding.med2Lay.visibility = View.GONE
                        binding.med1Lay.visibility = View.VISIBLE
                        binding.spinnerUni.visibility = View.VISIBLE
                        binding.med1Editxt.setText("")
                        binding.med2Editxt.setText("1")
                        binding.med1Editxt.requestFocus() }
                    "m3" -> {
                        binding.med3Lay.visibility = View.VISIBLE
                        binding.med2Lay.visibility = View.VISIBLE
                        binding.med1Lay.visibility = View.VISIBLE
                        binding.spinnerUni.visibility = View.VISIBLE
                        binding.med1Editxt.setText("")
                        binding.med2Editxt.setText("")
                        binding.med1Editxt.requestFocus() }
                    "uni" -> {
                        binding.med3Lay.visibility = View.GONE
                        binding.med2Lay.visibility = View.GONE
                        binding.med1Lay.visibility = View.GONE
                        binding.spinnerUni.visibility = View.GONE
                        binding.med1Editxt.setText("1")
                        binding.med2Editxt.setText("1")
                        binding.cantEditxt.requestFocus() }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.usTxt.text = listaUsados[0]
            }
        }
    }
    private fun uni2(){
        unidades = findViewById(R.id.spinner_uni)
        val listaUnidades = arrayOf("Centímetros", "Metros", "Milímetros", "Pulgadas")
        val adaptador: ArrayAdapter<String> = ArrayAdapter(
            this, R.layout.lista_spinner, listaUnidades)
        unidades.adapter = adaptador
        unidades.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int,
                p3: Long) { binding.prTxt.text = unidades.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.prTxt.text = listaUnidades[0] }
        }
    }
    private fun conver(med: Float?): Float {
        if (med == null || med.isNaN()) {
            return 1f
        }
        return when (binding.prTxt.text) {
            "Centímetros" -> med / 100
            "Metros" -> med
            "Milímetros" -> med / 1000
            "Pulgadas" -> med / 39.37f
            else -> med
        }
    }
    private fun pies(medida1: Float, medida2: Float): Float {
        return (conver(medida1)) * (conver(medida2)) * 11.1f
    }
    private fun metroCua(medida1: Float, medida2: Float): Float {
        val medida1 = conver(medida1).toString().toFloat()
        val medida2 = conver(medida2).toString().toFloat()
        return medida1 * medida2
    }
    private fun mCubicos(medida1: Float, medida2: Float, medida3: Float): Float {

        return conver(medida1) * conver(medida2) * conver(medida3)
    }
    private fun mLineales(medida1: Float, medida2: Float): Float {
        return conver(medida1) * medida2
    }
    private fun perim(): Float {
        val medida1 = binding.med1Editxt.text.toString().toFloat()
        val medida2 = binding.med2Editxt.text.toString().toFloat()
        return when (binding.prTxt.text) {
            "Centímetros" -> (((medida1) * 2 + (medida2) * 2)) / 100
            "Metros" -> (medida1) * 2 + (medida2) * 2
            "Milímetros" -> (((medida1) * 2 + (medida2) * 2)) / 1000
            "Pulgadas" -> ((((medida1) * 2.54f) * 2 + ((medida2) * 2.54f) * 2)) / 100
            else -> {(((medida1) * 2 + (medida2) * 2)) / 100 }
        }
    }
    private fun med1(x: View? = null): Float {
        val editText = if (x!= null) {
            x.findViewById<EditText>(R.id.etdMed1)
        } else {
            findViewById(R.id.med1_editxt)
        }
        val med = editText.text.toString()
        return med.toFloatOrNull() ?: 1f
    }
    private fun med2(modelo: View? = null): Float {
        val editText = if (modelo != null) {
            modelo.findViewById<EditText>(R.id.etdMed2)
        } else {
            findViewById(R.id.med2_editxt)
        }
        val med = editText.text.toString()
        return med.toFloatOrNull() ?: 1f
    }
    private fun med3(modelo: View? = null): Float {
        val editText = if (modelo != null) {
            modelo.findViewById<EditText>(R.id.etdMed3)
        } else {
            findViewById(R.id.med3_editxt)
        }
        val med = editText.text.toString()
        return med.toFloatOrNull() ?: 1f
    }

}
