package calculaora.e.vidrio3

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import calculaora.e.vidrio3.catalogo.Catalogo
import calculaora.e.vidrio3.databinding.ActivityMainBinding
import calculaora.e.vidrio3.red.ListChatActivity
import calculaora.e.vidrio3.registro.Registro
import calculaora.e.vidrio3.taller.Taller
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.android.synthetic.main.activity_corte.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.cant_editxt
import kotlinx.android.synthetic.main.activity_main.lyCuerpo
import kotlinx.android.synthetic.main.activity_main.med1_editxt
import kotlinx.android.synthetic.main.activity_main.med1_lay
import kotlinx.android.synthetic.main.activity_main.med2_editxt
import kotlinx.android.synthetic.main.activity_main.precio_editxt
import kotlinx.android.synthetic.main.activity_main.pro_editxt
import kotlinx.android.synthetic.main.dialogo.*
import kotlinx.android.synthetic.main.editar.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER")
class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)

    val df = android.icu.text.DecimalFormat("#,###.00").apply {
        isGroupingUsed = true
        decimalFormatSymbols = decimalFormatSymbols.apply {
            groupingSeparator = ' '
        }
    }
    private var lista: MutableList<Listado> = mutableListOf()
    val elementosFiltrados: MutableList<Listado> = mutableListOf()

    private lateinit var usados: Spinner
    private lateinit var unidades: Spinner
    private var colorSeleccionado: Int = 0

    private val retaso = 1.8f

    private lateinit var sharedPreferences: SharedPreferences


    private lateinit var binding: ActivityMainBinding
    @SuppressLint("NewApi", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar SharedPreferences
        sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Cargar los datos guardados
        cargarDatosGuardados()

        cliente()

        uni1()

        uni2()

        eliminar()

        abrir()

        btnLimpiar.setOnClickListener {
            try {
                if (us_txt.text != "uni") {
                    med1_editxt.requestFocus()
                } else {
                    cant_editxt.requestFocus()
                }

                precio_unitario.text = "0.0"
                precio_cantidad.text = "0.0"
                pc_txt.text = "0.0"
                mc_txt.text = "0.0"
                med1_editxt.setText(if (us_txt.text=="uni"){"1"}else{""})
                med2_editxt.setText(if (us_txt.text=="ml"||us_txt.text=="uni"){"1"}else{""})
                cant_editxt.setText("")
                precio_editxt.setText("")
                pro_editxt.setText("")
                clienteEditxt.setText("")
                med1_editxt.hint = ""
                med2_editxt.hint = ""
                cant_editxt.hint = ""
                precio_total.text = "0.0"
                pies_total.text = "0.0"
                metros_total.text = "0.0"
                per.text = "0.0"
                prue_txt.text= ""
                binding.tvpCliente.text = "Presupuesto"
                lista.clear()
                list.onRemoteAdapterConnected()
                actualizar()
            } catch (e: Exception) {
            }
        }

        binding.btBuscar.setOnClickListener {
            startActivity(Intent(this,BaulActivity::class.java))
        }

        binding.sumatorias.setOnClickListener {
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
                "monto", precio_total.text.toString()))}

        binding.listadoTxt.setOnClickListener {
            startActivity(Intent(this, ListaActivity::class.java).
            putExtra("monto", per.text.toString()))}

        binding.tallerCal.setOnClickListener {
            if (clienteEditxt.text.isNotEmpty()) {
                val paquete = Bundle().apply {
                    putString("cliente", clienteEditxt.text.toString())
                }
                val intent = Intent(this, Taller::class.java)
                intent.putExtras(paquete)
                startActivity(intent)
            } else {
                startActivity(Intent(this, Taller::class.java))
            }
        }

        binding.btnChat.setOnClickListener {
            startActivity(Intent(this, ListChatActivity::class.java))
        }

        binding.btnCatalogo.setOnClickListener {
            startActivity(Intent(this, Catalogo::class.java))}

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
                if (us_txt.text!="uni") {
                    med1_editxt.requestFocus()} else {
                    cant_editxt.requestFocus()
                }

                agregarListado()

                actualizar()

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "ingrese un número válido", Toast.LENGTH_LONG).show()
            }
        }

    }
    override fun onPause() {
        super.onPause()
        // Guardar los datos antes de que la aplicación pase a segundo plano
        guardarDatos()
    }

    private fun agregarListado() {
        // escalas, unidades y colores
        val escala = us_txt.text.toString()
        val uni   = pr_txt.text.toString()
        val color = colorSeleccionado
        // datos ingresados por el usuario
        val medida1 = med1()
        val medida2 = med2()
        val medida3 = med3()
        val cantidad = cant_editxt.text.toString().toFloat()
        val precio = precio_editxt.text.toString().toFloat()
        val producto = pro_editxt.text.toString()
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

        pc_txt.text = df1(piescant)
        mc_txt.text = df1(metroscua)
        precio_unitario.text = df.format(costounitario)
        precio_cantidad.text = df.format(costocantidad)
        prue_txt.text= med3().toString()

        med1_editxt.hint = df1(medida1)
        med2_editxt.hint = df1(medida2)
        med3_editxt.hint = df1(medida3)

        med1_editxt.setText(if (us_txt.text=="uni"){"1"}else{""})
        med2_editxt.setText(if (us_txt.text=="ml"||us_txt.text=="uni"){"1"}else{""})
        med3_editxt.text?.clear()
        cant_editxt.text?.clear()

        val medidas = Listado(
            escala,uni, medida1, medida2, medida3, cantidad, piescant, precio, costocantidad,
            producto, peri, metroscua, mlcant, cub, color
        )

        lista.add(medidas)
    }
    @SuppressLint("SetTextI18n")
    private fun actualizar() {
        // Creamos un nuevo adapter con los datos de la lista
        val adapter = adaptadores()

        // Configuramos el adapter para el ListView
        list.adapter = adapter

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
        precio_total.text = df.format(costoTotal)
        metros_total.text = df1(metroscuaTotal)
        pies_total.text = df1(piescuaTotal)
        per.text = "${df1(periTotal)} m.."

        // Notificamos al adapter que se actualizaron los datos
        adapter.notifyDataSetChanged()
    }
    private fun adaptadores(): ArrayAdapter<SpannableString> {
        val adapter = ArrayAdapter(
            this, R.layout.lista_cal,
            lista.map { datos ->
                val text = when (datos.escala) {
                    "p2" -> {
                        "(${df1(datos.medi1)} x ${df1(datos.medi2)} x ${df1(datos.canti)} = " +
                                "${df1(datos.piescua)}(${datos.escala}) " +
                                "x S/${df.format(datos.precio)} == S/${df.format(datos.costo)} -> ${datos.producto}"
                    }
                    "m2" -> {
                        "(${df1(datos.medi1)} x ${df1(datos.medi2)} x ${df1(datos.canti)} = " +
                                "${df1(datos.metcua)}(${datos.escala}) " +
                                "x S/${df.format(datos.precio)} == S/${df.format(datos.costo)} -> ${datos.producto}"
                    }
                    "m3" -> {
                        "(${df1(datos.medi1)} x ${df1(datos.medi2)} x ${df1(datos.medi3)} x ${df1(datos.canti)} = " +
                                "${df1(datos.metcub)}(${datos.escala}) " +
                                "x S/${df.format(datos.precio)} == S/${df.format(datos.costo)} -> ${datos.producto}"
                    }
                    "ml" -> {
                        "(${df1(datos.medi1)} x ${df1(datos.canti)} = ${df1(datos.metli)}(${datos.escala}) " +
                                "x S/${df.format(datos.precio)} == S/${df.format(datos.costo)} -> ${datos.producto}"
                    }
                    "uni" -> {
                        "${df1(datos.canti)}(${datos.escala}) = " +
                                "x S/${df.format(datos.precio)} == S/${df.format(datos.costo)} -> ${datos.producto}"
                    }
                    else -> {
                        "(${df1(datos.medi1)} x ${df1(datos.medi2)} x ${df1(datos.canti)} = ${df1(datos.piescua)} " +
                                "x S/${df.format(datos.precio)} == S/${df.format(datos.costo)} -> ${datos.producto}"
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
    private fun cliente(){
        val clienteRecup=cargarDatosGuardados()
        val paqueteR=intent.extras
        val cliente= paqueteR?.getString("rcliente")
        if (cliente!=null&&clienteRecup==""){binding.tvpCliente.text="Presupuesto de $cliente"}
        else{binding.tvpCliente.text="Presupuesto de $clienteRecup"}

        if(tvpCliente.text=="Presupuesto"){lyCuello.visibility=View.VISIBLE
            lyCuerpo.visibility=View.GONE}
        else{lyCuello.visibility=View.GONE
            lyCuerpo.visibility=View.VISIBLE}

        binding.tvpCliente.setOnClickListener {
            lyCuello.visibility=View.VISIBLE
            lyCuerpo.visibility=View.GONE}
        binding.clienteEditxt.setText(cliente)

        binding.btGo.setOnClickListener {
            val clientet = clienteEditxt.text.toString()
            tvpCliente.text = if(clienteEditxt.text.isNotEmpty()){"Presupuesto de $clientet"}
            else{"Presupuesto"}

            if(tvpCliente.text==null){lyCuello.visibility=View.VISIBLE
                lyCuerpo.visibility=View.GONE}else{lyCuello.visibility=View.GONE
                lyCuerpo.visibility=View.VISIBLE}

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "WrongViewCast")
    private fun eliminar() {
        list.setOnItemClickListener { parent, view, position, id ->
            try {
                val dialogo = AlertDialog.Builder(this)
                val modelo = layoutInflater.inflate(R.layout.dialogo, null)
                val eliminar = modelo.findViewById<Button>(R.id.btn_dialogo_eliminar)
                val editar = modelo.findViewById<Button>(R.id.btn_dialogo_editar)
                val irlista = modelo.findViewById<Button>(R.id.btnGuardar)
                val abrir = modelo.findViewById<Button>(R.id.btAbrir)
                val datos = modelo.findViewById<TextView>(R.id.tvEscala)
                val pro = modelo.findViewById<TextView>(R.id.etdProducto)
                datos.text = "${lista[position].producto} ${lista[position].escala} en ${lista[position].uni}"

                abrir.visibility = View.GONE
                dialogo.setView(modelo)
                val dialogoper = dialogo.create()
                dialogoper.show()

                eliminar.setOnClickListener {
                    lista.removeAt(position)
                    actualizar()
                    dialogoper.dismiss()
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
                            dialogoper.dismiss()
                            Toast.makeText(this, "Se editó correctamente", Toast.LENGTH_SHORT).show()
                        } catch (e: NumberFormatException) {
                            Toast.makeText(this, "Error: Ingresa valores numéricos válidos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                irlista.setOnClickListener {
                    guardar()
                    dialogoper.dismiss()
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
        list.adapter = adaptador
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
    private fun cargarDatosGuardados():String {
        // Obtener los datos guardados desde SharedPreferences
        val dato1 = sharedPreferences.getString("dato1", "")
        val dato2 = sharedPreferences.getString("dato2", "")
        val dato3 = sharedPreferences.getString("dato3", "")
        val cantidad = sharedPreferences.getString("cantidad", "")
        val precio = sharedPreferences.getString("precio", "")
        val producto = sharedPreferences.getString("producto", "")
        val cliente = sharedPreferences.getString("cliente", "")

        // Actualizar los campos de la interfaz con los datos cargados<
        binding.med1Editxt.setText(dato1)
        binding.med2Editxt.setText(dato2)
        binding.med3Editxt.setText(dato3)
        binding.cantEditxt.setText(cantidad)
        binding.precioEditxt.setText(precio)
        binding.proEditxt.setText(producto)
        binding.clienteEditxt.setText(cliente)

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

        val nombreArchivo = if (tvpCliente.text == "Presupuesto"){
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
        val li = paquete?.getSerializable("lista") as? List<Listado>
        val listaRecibida = mutableListOf<Listado>()
        li?.let { listaRecibida.addAll(it) }

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
        val pdfFile = File(getExternalFilesDir(null), "Presupuesto_${clienteEditxt.text}.pdf")
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
    @SuppressLint("ResourceType")
    private fun generarPdf() {
        val cliente = clienteEditxt.text.toString()

        val pdfFileName = "Presupuesto_${cliente}.pdf" // Genera el nombre del archivo PDF

        val pdfFile = File(getExternalFilesDir(null), pdfFileName)
        val document = Document()
        val writer = PdfWriter.getInstance(document, FileOutputStream(pdfFile))
        document.open()

        val tituloFont = Font(Font.FontFamily.HELVETICA, 20f, Font.BOLD) // Tamaño de fuente 20
        document.add(Paragraph("Proforma $cliente", tituloFont))

        var itemNum = 0 // variable para llevar la cuenta del número de ítems

        if (lista.isEmpty()) {
            Toast.makeText(this, "La lista está vacía", Toast.LENGTH_SHORT).show()
            return // Terminar la función si la lista está vacía
        }

        for (item in lista) {
            itemNum++
        // Agregar recuadro para el ítem
            val itemBox = Rectangle(10f, 50f, 550f, 150f)
            itemBox.borderWidth = 1f
            itemBox.borderColor = BaseColor.BLACK
            document.add(itemBox)

            // Agregar título del ítem
            val producto = item.producto
            val titulo = "Ítem $itemNum: $producto"
            val tituloItem = Paragraph(titulo, Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD))
            document.add(tituloItem)

            // Agregar recuadro interno para las medidas, cantidad y costo
            val medidasBox = Rectangle(20f, 100f, 250f, 80f)
            medidasBox.borderWidth = 1f
            medidasBox.borderColor = BaseColor.LIGHT_GRAY
            document.add(medidasBox)

            // Agregar texto de las medidas, cantidad y costo
            val ancho = item.medi1
            val alto = item.medi2
            val fondo = item.medi3
            val cantidad = item.canti
            val costo = item.costo
            val textoMedidas = when (item.escala) {
                "p2", "m2" -> "Ancho:   ${df1(ancho)}\nAlto:   ${df1(alto)}\nCantidad:   ${df1(cantidad)}\nCosto:   ${df.format(costo)}"
                "ml" -> "Metros:${df1(ancho)}\nCantidad:   ${df1(cantidad)}\nCosto:   ${df.format(costo)}"
                "m3" -> "Ancho:   ${df1(ancho)}\nAlto:   ${df1(alto)}\nFondo:   ${df1(fondo)}\nCantidad:   ${df1(cantidad)}\nCosto:   ${df.format(costo)}"
                "uni" -> "Cantidad:   ${df1(cantidad)}\nCosto:   ${df.format(costo)}"
                else -> "" // Manejar caso por defecto si es necesario
            }

            val medidas = Paragraph(textoMedidas)
            document.add(medidas)


            // Agregar recuadro interno para la imagen
            val imageBox = Rectangle(280f, 55f, 270f, 120f)
            imageBox.borderWidth = 1f
            imageBox.borderColor = BaseColor.LIGHT_GRAY
            document.add(imageBox)

            // Agregar imagen (puedes reemplazar "R.drawable.imagen" por la ruta de la imagen que desees mostrar)
            /*Glide.with(this)
                .load(R.mipmap.i_tu2)
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        val bitmap = resource.toBitmap()
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val byteArray = stream.toByteArray()
                        val image = Image.getInstance(byteArray)
                        image.scaleToFit(imageBox.width - 10, imageBox.height - 10)
                        image.setAbsolutePosition(imageBox.left + 5, imageBox.bottom + 5)
                        document.add(image)
                        document.close() // Cerrar el documento antes de mostrar la imagen
                    }
                })*/
            // Agregar salto de línea al final de cada ítem
            document.add(Paragraph("\n"))
        }
        // Agregar recuadro para el precio total
        val totalBox = Rectangle(10f, 50f, 550f, 50f)
        totalBox.borderWidth = 1f
        totalBox.borderColor = BaseColor.BLACK
        document.add(totalBox)

// Agregar título del precio total
        val precioTotal = binding.precioTotal.text.toString()
        val tituloTotal = "Precio total: S/.$precioTotal"
        val tituloTotalItem = Paragraph(tituloTotal, Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD))
        tituloTotalItem.alignment = Element.ALIGN_RIGHT
        document.add(tituloTotalItem)

        document.close()

        Toast.makeText(this, "PDF generado: ${pdfFile.absolutePath}", Toast.LENGTH_LONG).show()
    }
    private fun df1(defo: Float): String {
        val resultado =if ("$defo".endsWith(".0")) {"$defo".replace(".0", "")}
        else { "%.1f".format(defo)
            }
        return resultado.replace(",", ".")
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
                us_txt.text = usados.selectedItem.toString()
                colorSeleccionado = adaptadorU.getColor(position)
                when (us_txt.text) {
                    "p2" -> {
                        med3_lay.visibility = View.GONE
                        med2_lay.visibility = View.VISIBLE
                        med1_lay.visibility = View.VISIBLE
                        spinner_uni.visibility = View.VISIBLE
                        med1_editxt.setText("")
                        med2_editxt.setText("")
                        med1_editxt.requestFocus() }
                    "m2" -> {
                        med3_lay.visibility = View.GONE
                        med2_lay.visibility = View.VISIBLE
                        med1_lay.visibility = View.VISIBLE
                        spinner_uni.visibility = View.VISIBLE
                        med1_editxt.setText("")
                        med2_editxt.setText("")
                        med1_editxt.requestFocus() }
                    "ml" -> {
                        med3_lay.visibility = View.GONE
                        med2_lay.visibility = View.GONE
                        med1_lay.visibility = View.VISIBLE
                        spinner_uni.visibility = View.VISIBLE
                        med1_editxt.setText("")
                        med2_editxt.setText("1")
                        med1_editxt.requestFocus() }
                    "m3" -> {
                        med3_lay.visibility = View.VISIBLE
                        med2_lay.visibility = View.VISIBLE
                        med1_lay.visibility = View.VISIBLE
                        spinner_uni.visibility = View.VISIBLE
                        med1_editxt.setText("")
                        med2_editxt.setText("")
                        med1_editxt.requestFocus() }
                    "uni" -> {
                        med3_lay.visibility = View.GONE
                        med2_lay.visibility = View.GONE
                        med1_lay.visibility = View.GONE
                        spinner_uni.visibility = View.GONE
                        med1_editxt.setText("1")
                        med2_editxt.setText("1")
                        cant_editxt.requestFocus() }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                us_txt.text = listaUsados[0]
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
                p3: Long) { pr_txt.text = unidades.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                pr_txt.text = listaUnidades[0] }
        }
    }
   private fun conver(med: Float?): Float {
        if (med == null || med.isNaN()) {
            return 1f
        }
        return when (pr_txt.text) {
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
        val medida1 = med1_editxt.text.toString().toFloat()
        val medida2 = med2_editxt.text.toString().toFloat()
        return when (pr_txt.text) {
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

