package crystal.crystal.optimizadores

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.R
import crystal.crystal.databinding.ActivityCorteBinding
import kotlinx.android.synthetic.main.activity_corte.*
import kotlin.math.ceil


class Corte : AppCompatActivity() {

    private var lista= mutableListOf<Triple<Float, Int, String>>()
    private var lista2= mutableListOf<Triple<Float, Int, String>>()

    private lateinit var binding: ActivityCorteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCorteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mostrarDialogo()
        mostrarDialogo2()


        binding.etMedida.requestFocus()

        binding.btAnadir.setOnClickListener {
            try {
                binding.etMedida.requestFocus()
                anadir()
            }catch (e:Exception){}
        }
        binding.btLimpiar.setOnClickListener {
            binding.etMedida.requestFocus()
            lista.clear()
            actualizar()

            binding.etMedida.setText("")
            binding.etCant.setText("")
            binding.etRefe.setText("")
        }
        binding.btOpti.setOnClickListener {
            val grosorDisco = binding.etGrosor.text.toString().toFloatOrNull() ?:
            binding.etGrosor.hint.toString().toFloat() // Obtener el grosor del disco
            val cortesOrdenados = obtenerCortes(lista, grosorDisco)
            mostrarCortes(cortesOrdenados)
        }

        binding.btAgregar.setOnClickListener {
            abrirDialogoCortes()
        }

        binding.btEliminar.setOnClickListener {
            lista2.clear()
            actualizar2()

        }
    }
    private fun anadir(){
        while (true){
            val medi = binding.etMedida.text.toString().toFloat()
            val cant = binding.etCant.text.toString().toInt()
            val refe = binding.etRefe.text.toString()

            binding.etMedida.setText("")
            binding.etCant.setText("")
            binding.etRefe.setText("")

            if (medi.toString().isNotEmpty()) {
                lista.add(Triple(medi, cant, refe))
                actualizar()
            } else{
                Toast.makeText(this, "Olvidaste ingresar datos", Toast.LENGTH_LONG).show()}
        }
    }
    private fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }
    private fun df2(defo: Float):String{
        val redon= ceil(defo)
        return redon.toInt().toString()
    }
    private fun referencias(){
    }

    private fun obtenerCortes(
        lista: List<Triple<Float, Int, String>>,
        grosorDisco: Float
    ): List<Triple<Float, Int,String>> {
// Obtener medidas de los cortes requeridos y sumar grosor del disco
        val medidas = lista.map { it.first + grosorDisco / 10 }
// Obtener cantidad de cada corte y referencias
        val cantidades = lista.map { it.second }
        val referencias = lista.map {it.third}
// Crear lista de pares de medidas y cantidades ordenadas
        return medidas.indices
            .map { i -> Triple(medidas[i], cantidades[i], referencias[i]) }
            .sortedByDescending { it.first }
    }

    private fun obtenerPerfiles(
        lista2: List<Triple<Float, Int, String>>
    ): List<Triple<Float, Int,String>> {
// Obtener medidas de las medidas
        val medidas = lista2.map { it.first }
// Obtener cantidad de cada corte y referencias
        val cantidades = lista2.map { it.second }
        val referencias = lista2.map {it.third}
// Crear lista de pares de medidas y cantidades ordenadas
        return medidas.indices
            .map { i -> Triple(medidas[i], cantidades[i], referencias[i]) }
            .sortedByDescending { it.first }
    }

    private fun mostrarCortes(cortes: List<Triple<Float, Int, String>>) {
        // Crear string con los cortes ordenados
        val cortesString =cortes.joinToString(separator = "\n") {
            "${it.second} cortes de ${it.first} cm de ${it.third}"
        }
        // Mostrar los cortes en la textView
        binding.tvResultado.text = cortesString
    }

    // funciones para actualizar las listas
    private fun actualizar(){
        val listaString = mutableListOf<String>()
        for (datos in lista){
            listaString.add("${df1(datos.first)} (${datos.third}) ------ ${datos.second} uni")
        }
        val adaptador = ArrayAdapter(this, R.layout.lista_calg, listaString)
        binding.listCorte.adapter = adaptador
    }
    private fun actualizar2() {
        // Crear una lista de strings con los datos de lista2
        val listaString2 = mutableListOf<String>()
        for (datos2 in lista2) {
            listaString2.add("${df1(datos2.first)} (${datos2.second}) ------ ${datos2.third}")
        }

        // Crear un adaptador de lista para mostrar los datos en el ListView
        val adaptador = ArrayAdapter(this, R.layout.lista_cal, listaString2)

        // Asignar el adaptador al ListView
        binding.listaPerfil.adapter = adaptador
    }
    // funcion para abrir dialogo de lista2
    @SuppressLint("CutPasteId")
    private fun abrirDialogoCortes() {
        try {
            // Crear un nuevo diálogo
            val dialogoCortes = Dialog(this)
            // Establecer el layout del diálogo
            dialogoCortes.setContentView(R.layout.dialogo_cortes)

            // Ocultar el botón eliminar, editar
            dialogoCortes.findViewById<Button>(R.id.btn_dialogo_eliminar).visibility = View.GONE
            dialogoCortes.findViewById<Button>(R.id.btn_dialogo_editar).visibility = View.GONE

            // Limpiar el texto del campo de los ediText
            dialogoCortes.findViewById<EditText>(R.id.etdMed1).text = null
            dialogoCortes.findViewById<EditText>(R.id.etdCant).text = null
            dialogoCortes.findViewById<EditText>(R.id.etdProducto).text = null

            // Mostrar el botón de agregar
            dialogoCortes.findViewById<Button>(R.id.btDiAgregar).visibility = View.VISIBLE

            // Mostrar el layout de texto
            dialogoCortes.findViewById<LinearLayout>(R.id.lyTxt).visibility = View.VISIBLE
            // Mostrar el diálogo y activar el teclado
            dialogoCortes.show()
            dialogoCortes.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)


            // Configurar el listener del botón agregar
            dialogoCortes.findViewById<Button>(R.id.btDiAgregar).setOnClickListener {
                // Obtener los valores de los campos de texto
                val med1 = dialogoCortes.findViewById<EditText>(R.id.etdMed1).text.toString().toFloatOrNull()
                val cant = dialogoCortes.findViewById<EditText>(R.id.etdCant).text.toString().toIntOrNull()
                val producto = dialogoCortes.findViewById<EditText>(R.id.etdProducto).text.toString()

                // Verificar que los campos no estén vacíos
                if (med1 == null || cant == null || producto.isBlank()) {
                    // Mostrar un mensaje de error
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show()
                } else {
                    // Agregar los datos a la lista2
                    lista2.add(Triple(med1, cant, producto))

                    actualizar2()

                    // Cerrar el diálogo
                    dialogoCortes.dismiss()
                }
            }

            // Mostrar el diálogo
            dialogoCortes.show()


        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Ingrese un número válido", Toast.LENGTH_LONG).show()
        }
    }
    // funciones para mostrar las listas
    private fun mostrarDialogo() {
        listCorte.setOnItemClickListener { _, _, position, _
            ->
            try {
                val dialogo = Dialog(this)
                dialogo.setContentView(R.layout.dialogo_cortes)
                val lyTxt: LinearLayout = dialogo.findViewById(R.id.lyTxt)
                val etdMed1: EditText = dialogo.findViewById(R.id.etdMed1)
                val etdCant: EditText = dialogo.findViewById(R.id.etdCant)
                val etdProducto: EditText = dialogo.findViewById(R.id.etdProducto)
                val btnDiaOk: Button = dialogo.findViewById(R.id.btnDiaOk)
                val btnDiaEli: Button = dialogo.findViewById(R.id.btn_dialogo_eliminar)
                val btnDiaEdi: Button = dialogo.findViewById(R.id.btn_dialogo_editar)

                // Obtenemos los datos del elemento seleccionado
                val (medida, cantidad, referencia) = lista[position]

                etdMed1.setText(medida.toString())
                etdCant.setText(cantidad.toString())
                etdProducto.setText(referencia)

                // Configuramos el botón para editar
                btnDiaEdi.setOnClickListener {
                    btnDiaOk.visibility = View.VISIBLE
                    btnDiaEli.visibility = View.GONE
                    btnDiaEdi.visibility = View.GONE
                    lyTxt.visibility = View.VISIBLE
                }

                // Configuramos el botón para eliminar
                btnDiaEli.setOnClickListener {
                    lista.removeAt(position)
                    actualizar()
                    dialogo.dismiss()
                }

                // Configuramos el botón para guardar los cambios editados
                btnDiaOk.setOnClickListener {
                    val nuevaMedida = etdMed1.text.toString().toFloat()
                    val nuevaCantidad = etdCant.text.toString().toInt()
                    val nuevaReferencia = etdProducto.text.toString()

                    lista[position] = Triple(nuevaMedida, nuevaCantidad, nuevaReferencia)

                    actualizar()
                    dialogo.dismiss()
                }
                dialogo.show()
            } catch (e: Exception) {
                Toast.makeText(this, "Trabajando", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun mostrarDialogo2() {
        listaPerfil.setOnItemClickListener { _, _, position, _ ->
            try {

                val dialogo = Dialog(this)
                dialogo.setContentView(R.layout.dialogo_cortes)
                val lyTxt: LinearLayout = dialogo.findViewById(R.id.lyTxt)
                val etdMed1: EditText = dialogo.findViewById(R.id.etdMed1)
                val etdCant: EditText = dialogo.findViewById(R.id.etdCant)
                val etdProducto: EditText = dialogo.findViewById(R.id.etdProducto)
                val btnDiaOk: Button = dialogo.findViewById(R.id.btnDiaOk) //Cambiar el botón para agregar por el de Ok
                val btnDiaEli: Button = dialogo.findViewById(R.id.btn_dialogo_eliminar)
                val btnDiaEdi: Button = dialogo.findViewById(R.id.btn_dialogo_editar)

                // Obtenemos los datos del elemento seleccionado
                val (medida, cantidad, referencia) = lista2[position]



                // Ocultar el botón de agregar y el botón eliminar
                btnDiaOk.visibility = View.GONE
                btnDiaEli.visibility = View.VISIBLE

                // Configurar el botón para editar
                btnDiaEdi.setOnClickListener {
                    // Mostrar el botón de agregar y el botón eliminar
                    btnDiaOk.visibility = View.VISIBLE

                    // Ocultar el botón editar
                    btnDiaEdi.visibility = View.INVISIBLE
                    btnDiaEli.visibility = View.GONE

                    // Mostrar los datos del elemento seleccionado en los EditText
                    lyTxt.visibility = View.VISIBLE
                    etdMed1.setText(medida.toString())
                    etdCant.setText(cantidad.toString())
                    etdProducto.setText(referencia)

                }

                // Configurar el botón para eliminar
                btnDiaEli.setOnClickListener {
                    lista2.removeAt(position)
                    actualizar2()

                    // Cerrar el diálogo
                    dialogo.dismiss()
                }

                // Configurar el botón para guardar los cambios editados
                btnDiaOk.setOnClickListener {
                    // Obtener los nuevos valores de los campos de texto
                    val nuevaMedida = etdMed1.text.toString().toFloat()
                    val nuevaCantidad = etdCant.text.toString().toInt()
                    val nuevaReferencia = etdProducto.text.toString()

                    // Actualizar los valores del elemento seleccionado en la lista
                    lista2[position] = Triple(nuevaMedida, nuevaCantidad, nuevaReferencia)

                    // Crear una lista de strings con los datos de lista2
                    val listaString2 = mutableListOf<String>()
                    for (datos2 in lista2) {
                        listaString2.add("${df1(datos2.first)} (${datos2.second}) ------ ${datos2.third}")
                    }

                    // Crear un adaptador de lista para mostrar los datos en el ListView
                    val adaptador = ArrayAdapter(this, R.layout.lista_cal, listaString2)

                    // Asignar el adaptador al ListView
                    binding.listaPerfil.adapter = adaptador

                    // Cerrar el diálogo
                    dialogo.dismiss()
                }

                dialogo.show()
            } catch (e: Exception) {
                Toast.makeText(this, "Trabajando", Toast.LENGTH_LONG).show()
            }
        }
    }


}

/*class OptimizadosActivity : AppCompatActivity() {

    private var lista2= mutableListOf<Triple<Float, Int, String>>()
    private var lista= mutableListOf<Triple<Float, Int, String>>()
    private lateinit var etGrosor: EditText
    private lateinit var tvMaterialesUsados: TextView
    private lateinit var lvCortesRealizados: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_optimizados)

        // Obtener referencias de views
        etGrosor = findViewById(R.id.et_grosor)
        tvMaterialesUsados = findViewById(R.id.tv_materiales_usados)
        lvCortesRealizados = findViewById(R.id.lv_cortes_realizados)

        // Obtener datos ingresados por el usuario
        val grosor = etGrosor.text.toString().toFloatOrNull() ?: etGrosor.hint.toString().toFloat()
         / 10 // Convertir de mm a cm
        val materiales = lista2.map { Material(it.first, it.second, it.third) }
        val cortesRequeridos = lista.map { CorteRequerido(it.first, it.second, it.third) }

        // Optimizar cortes
        val optimizador = OptimizadorCorte(materiales, cortesRequeridos, grosor)
        optimizador.optimizar()

        // Mostrar resultado en views
        tvMaterialesUsados.text = "Materiales usados:\n${optimizador.materialesUsados}"
        lvCortesRealizados.adapter = CortesRealizadosAdapter(this, optimizador.cortesRealizados)
    }

    private class Material(val largo: Float, var cantidad: Int, val referencia: String) {
        var usado = 0f // Largo total usado de este material
    }

    private class CorteRequerido(val largo: Float, val cantidad: Int, val referencia: String) {
        var cortado = 0 // Cantidad de cortes realizados de este largo
    }

    private class CorteRealizado(val material: Material, val largo: Float) {
        val referencia = material.referencia
        val sobrante = material.largo - material.usado - largo
    }

    private class OptimizadorCorte(val materiales: List<Material>, val cortesRequeridos: List<CorteRequerido>,
    val grosor: Float) {

        private val cortesRealizados = mutableListOf<CorteRealizado>()
        private val materialesUsados = mutableListOf<Material>()

        fun optimizar() {
            for (corte in cortesRequeridos) {
                var mejorMaterial: Material? = null
                var mejorSobrante = Float.POSITIVE_INFINITY

                for (material in materiales) {
                    val sobrante = material.largo - material.usado - corte.largo - grosor
                    if (sobrante >= 0 && sobrante < mejorSobrante) {
                        mejorMaterial = material
                        mejorSobrante = sobrante
                    }
                }

                if (mejorMaterial == null) {
                    throw IllegalStateException("

                    Para realizar la optimización de los cortes, puedes utilizar el algoritmo de
                    "corte de stock" o "cutting stock". Este algoritmo consiste en encontrar la
                    forma más eficiente de cortar piezas a partir de un stock determinado.

                    primero se debe encontrar la mejor combinación de medidas de corte que minimicen
                    la cantidad de material desperdiciado. Esto se puede lograr mediante un algoritmo
                     de optimización de corte como el algoritmo de corte óptimo o el algoritmo
                     de corte descendente.

*/