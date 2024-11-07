package crystal.crystal.optimizadores

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import crystal.crystal.R
import crystal.crystal.casilla.MapStorage
import crystal.crystal.databinding.ActivityCorteBinding


class Corte : AppCompatActivity() {

    private var lista = mutableListOf<Triple<Float, Int, String>>()
    private var lista2 = mutableListOf<Triple<Float, Int, String>>()
    private val gson = Gson()
    private val PREFS_NAME = "CortePreferences"


    private lateinit var binding: ActivityCorteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCorteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos los métodos para mostrar diálogos y actualizar listas
        recuperarDatos()
        mostrarDialogo()
        mostrarDialogo2()
        actualizar()
        actualizar2()

        binding.etMedida.requestFocus()

        binding.btAnadir.setOnClickListener {
            try {
                anadir()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al agregar pieza: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.btAnadir.setOnLongClickListener {
            // Llamar a la función para poblar el Spinner
            poblarSpinnerConDatosGuardados()
            true // Indica que el evento ha sido manejado
        }

        binding.btLimpiar.setOnClickListener {
            limpiarPiezas()
        }
        binding.btOpti.setOnClickListener {
            try {
                val piezasRequeridas = lista  // List<Triple<Float, Int, String>>
                val varillasDisponibles = lista2  // List<Triple<Float, Int, String>>
                val grosorDisco = binding.etGrosor.text.toString().toFloatOrNull()
                    ?: binding.etGrosor.hint.toString().toFloat()

                val resultadoOptimizado =
                    optimizarCortes(piezasRequeridas, varillasDisponibles, grosorDisco)
                mostrarResultadoOptimizado(resultadoOptimizado)
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        binding.btAgregar.setOnClickListener {
            abrirDialogoCortes()
        }

        binding.btEliminar.setOnClickListener {
            limpiarVarillas()
        }
        binding.spCortes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val nombreSeleccionado = parent.getItemAtPosition(position).toString()
                listas(nombreSeleccionado) // Llamada a la función 'listas' con el nombre seleccionado
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Opcional: Manejar el caso en que no se selecciona nada
            }
        }

    }

    override fun onPause() {
        super.onPause()
        guardarDatos() // Guardar datos al pausar la actividad
    }

    // Función para agregar una nueva pieza requerida
    private fun anadir() {
        val medi = binding.etMedida.text.toString().toFloatOrNull()
        val cant = binding.etCant.text.toString().toIntOrNull()
        val refe = binding.etRefe.text.toString()

        if (medi != null && cant != null && refe.isNotEmpty()) {
            lista.add(Triple(medi, cant, refe)) // Agregar a la lista
            actualizar() // Actualizar la UI
            guardarDatos() // Guardar los datos

            binding.etMedida.setText("") // Limpiar campos
            binding.etCant.setText("")
            binding.etRefe.setText("")

            binding.etMedida.requestFocus() // Enfocar de nuevo el campo de medida
        } else {
            Toast.makeText(this, "Olvidaste ingresar datos", Toast.LENGTH_LONG).show()
        }
    }

    // Función para limpiar la lista de piezas requeridas
    private fun limpiarPiezas() {
        lista.clear()
        actualizar()

        binding.etMedida.setText("")
        binding.etCant.setText("")
        binding.etRefe.setText("")

        binding.etMedida.requestFocus()
    }

    // Función para limpiar la lista de varillas disponibles
    private fun limpiarVarillas() {
        lista2.clear()
        actualizar2()
    }

    // Función para formatear números con un decimal
    private fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    // Función para actualizar la lista de piezas requeridas
    private fun actualizar() {
        val listaString = mutableListOf<String>()
        for (datos in lista) {
            listaString.add("${df1(datos.first)} cm (${datos.third}) ------ ${datos.second} uni")
        }
        val adaptador = ArrayAdapter(this, R.layout.lista_calg, listaString)
        binding.listCorte.adapter = adaptador

    }

    // Función para actualizar la lista de varillas disponibles
    private fun actualizar2() {
        val listaString2 = mutableListOf<String>()
        for (datos2 in lista2) {
            listaString2.add("${df1(datos2.first)} cm ------ ${datos2.second} uni (${datos2.third})")
        }
        val adaptador = ArrayAdapter(this, R.layout.lista_cal, listaString2)
        binding.listaPerfil.adapter = adaptador
    }

    // Función para abrir el diálogo y agregar varillas disponibles
    private fun abrirDialogoCortes() {
        try {
            val dialogoCortes = Dialog(this)
            dialogoCortes.setContentView(R.layout.dialogo_cortes)

            // Configuramos los elementos del diálogo
            val etdMed1: EditText = dialogoCortes.findViewById(R.id.etdMed1)
            val etdCant: EditText = dialogoCortes.findViewById(R.id.etdCant)
            val etdProducto: EditText = dialogoCortes.findViewById(R.id.etdProducto)
            val btDiAgregar: Button = dialogoCortes.findViewById(R.id.btDiAgregar)
            val btnDiaEli: Button = dialogoCortes.findViewById(R.id.btn_dialogo_eliminar)
            val btnDiaEdi: Button = dialogoCortes.findViewById(R.id.btn_dialogo_editar)
            val lyTxt: LinearLayout = dialogoCortes.findViewById(R.id.lyTxt)

            // Configuramos la visibilidad de los botones
            btDiAgregar.visibility = View.VISIBLE
            btnDiaEli.visibility = View.GONE
            btnDiaEdi.visibility = View.GONE
            lyTxt.visibility = View.VISIBLE

            // Limpiamos los campos
            etdMed1.text = null
            etdCant.text = null
            etdProducto.text = null

            // Mostramos el diálogo y activamos el teclado
            dialogoCortes.show()
            dialogoCortes.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

            // Configuramos el listener del botón agregar
            btDiAgregar.setOnClickListener {
                val med1 = etdMed1.text.toString().toFloatOrNull()
                val cant = etdCant.text.toString().toIntOrNull()
                val producto = etdProducto.text.toString()

                if (med1 != null && cant != null && producto.isNotEmpty()) {
                    lista2.add(Triple(med1, cant, producto))
                    actualizar2()
                    dialogoCortes.dismiss()
                } else {
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al agregar varilla: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Función para mostrar el diálogo al hacer clic en una pieza requerida
    private fun mostrarDialogo() {
        binding.listCorte.setOnItemClickListener { _, _, position, _ ->
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
                val btDiAgregar: Button = dialogo.findViewById(R.id.btDiAgregar)

                // Configuramos la visibilidad de los botones
                btDiAgregar.visibility = View.GONE
                btnDiaOk.visibility = View.GONE
                lyTxt.visibility = View.GONE

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
                    lista.removeAt(position) // Eliminar de la lista
                    actualizar() // Actualizar la UI
                    guardarDatos() // Guardar los datos
                    dialogo.dismiss() // Cerrar el diálogo
                }

                // Configuramos el botón para guardar los cambios editados
                btnDiaOk.setOnClickListener {
                    val nuevaMedida = etdMed1.text.toString().toFloatOrNull()
                    val nuevaCantidad = etdCant.text.toString().toIntOrNull()
                    val nuevaReferencia = etdProducto.text.toString()

                    if (nuevaMedida != null && nuevaCantidad != null && nuevaReferencia.isNotEmpty()) {
                        lista[position] = Triple(nuevaMedida, nuevaCantidad, nuevaReferencia) // Actualizar la lista
                        actualizar() // Actualizar la UI
                        guardarDatos() // Guardar los datos
                        dialogo.dismiss() // Cerrar el diálogo
                    } else {
                        Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show()
                    }
                }

                dialogo.show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al mostrar diálogo: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Función para mostrar el diálogo al hacer clic en una varilla disponible
    private fun mostrarDialogo2() {
        binding.listaPerfil.setOnItemClickListener { _, _, position, _ ->
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
                val btDiAgregar: Button = dialogo.findViewById(R.id.btDiAgregar)

                // Configuramos la visibilidad de los botones
                btDiAgregar.visibility = View.GONE
                btnDiaOk.visibility = View.GONE
                lyTxt.visibility = View.GONE

                // Obtenemos los datos del elemento seleccionado
                val (medida, cantidad, referencia) = lista2[position]

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
                    lista2.removeAt(position)
                    actualizar2()
                    dialogo.dismiss()
                }

                // Configuramos el botón para guardar los cambios editados
                btnDiaOk.setOnClickListener {
                    val nuevaMedida = etdMed1.text.toString().toFloatOrNull()
                    val nuevaCantidad = etdCant.text.toString().toIntOrNull()
                    val nuevaReferencia = etdProducto.text.toString()

                    if (nuevaMedida != null && nuevaCantidad != null && nuevaReferencia.isNotEmpty()) {
                        lista2[position] = Triple(nuevaMedida, nuevaCantidad, nuevaReferencia)
                        actualizar2()
                        dialogo.dismiss()
                    } else {
                        Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show()
                    }
                }

                dialogo.show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al mostrar diálogo: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    //FUNCIONES DE OPTIMIZACION DE CORTES
    // Función de optimización de cortes
    private fun optimizarCortes(
        piezasRequeridas: List<Triple<Float, Int, String>>,
        varillasDisponibles: List<Triple<Float, Int, String>>,
        grosorDisco: Float
    ): List<Varilla> {
        // Expandir las piezas requeridas
        val piezas = mutableListOf<Float>()
        for ((longitud, cantidad, _) in piezasRequeridas) {
            repeat(cantidad) {
                piezas.add(longitud)
            }
        }
        // Ordenar las piezas en orden decreciente
        piezas.sortDescending()

        // Crear las varillas disponibles
        val varillas = mutableListOf<Varilla>()
        for ((longitud, cantidad, _) in varillasDisponibles) {
            repeat(cantidad) {
                varillas.add(Varilla(longitud, mutableListOf(), longitud))
            }
        }
        // Ordenar las varillas en orden decreciente
        varillas.sortByDescending { it.longitud }

        val varillasUsadas = mutableListOf<Varilla>()

        for (pieza in piezas) {
            var asignada = false
            // Intentar asignar la pieza a una varilla existente
            for (varilla in varillasUsadas) {
                // Calculamos el espacio necesario considerando el grosor del disco
                val espacioNecesario = if (varilla.cortes.isEmpty()) pieza else pieza + grosorDisco
                if (varilla.restante >= espacioNecesario) {
                    varilla.cortes.add(pieza)
                    varilla.restante -= espacioNecesario
                    asignada = true
                    break
                }
            }
            // Si no se pudo asignar, usar una nueva varilla
            if (!asignada) {
                if (varillas.isEmpty()) {
                    throw Exception("No hay suficientes varillas disponibles para cortar todas las piezas.")
                }
                val nuevaVarilla = varillas.removeAt(0)
                val espacioNecesario = pieza
                if (nuevaVarilla.restante >= espacioNecesario) {
                    nuevaVarilla.cortes.add(pieza)
                    nuevaVarilla.restante -= espacioNecesario
                    varillasUsadas.add(nuevaVarilla)
                } else {
                    throw Exception("La pieza de longitud ${pieza} no cabe en ninguna varilla disponible.")
                }
            }
        }
        return varillasUsadas
    }

    // Función para mostrar el resultado optimizado
    private fun mostrarResultadoOptimizado(varillasUsadas: List<Varilla>) {
        val resultadoString = StringBuilder()

        for ((index, varilla) in varillasUsadas.withIndex()) {
            resultadoString.append("Varilla ${index + 1} (Longitud ${df1(varilla.longitud)} cm):\n")
            resultadoString.append("  Cortes: ${varilla.cortes.joinToString(", ") { df1(it) }}\n")
            resultadoString.append("  Retazo: ${df1(varilla.restante)} cm\n\n")
        }

        binding.tvResultado.setText (resultadoString.toString())
        guardarDatos() // Guardar los datos después de mostrar el resultado
    }

    // Clase Varilla
    data class Varilla(
        val longitud: Float, // Longitud total de la varilla
        val cortes: MutableList<Float> = mutableListOf(), // Lista de cortes realizados
        var restante: Float = longitud // Espacio restante en la varilla
    )

    //FUNCIONES DE SHARETPREFENTS

    private fun guardarDatos() {
        val sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        with(sharedPref.edit()) {
            // Serializar y guardar 'lista' (piezas requeridas)
            val listaJson = gson.toJson(lista)
            putString("lista_piezas", listaJson)

            // Serializar y guardar 'lista2' (varillas disponibles)
            val lista2Json = gson.toJson(lista2)
            putString("lista_varillas", lista2Json)

            // Guardar el texto de 'tvResultado'
            putString("texto_resultado", binding.tvResultado.text.toString())

            apply() // Guardar de forma asíncrona
        }
    }

    private fun recuperarDatos() {
        val sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Recuperar y deserializar 'lista' (piezas requeridas)
        val listaJson = sharedPref.getString("lista_piezas", null)
        if (listaJson != null) {
            val type = object : TypeToken<MutableList<Triple<Float, Int, String>>>() {}.type
            lista = gson.fromJson(listaJson, type)
            actualizar() // Actualizar la UI
        }

        // Recuperar y deserializar 'lista2' (varillas disponibles)
        val lista2Json = sharedPref.getString("lista_varillas", null)
        if (lista2Json != null) {
            val type2 = object : TypeToken<MutableList<Triple<Float, Int, String>>>() {}.type
            lista2 = gson.fromJson(lista2Json, type2)
            actualizar2() // Actualizar la UI
        }

        // Recuperar el texto de 'tvResultado'
        val textoResultado = sharedPref.getString("texto_resultado", "")
        binding.tvResultado.setText (textoResultado)
    }

    //FUNCIONES DE CARGAR LISTAS
    private fun poblarSpinnerConDatosGuardados() {
        try {
            // Cargar el mapa desde MapStorage
            val mapListas = MapStorage.cargarMap(this)

            if (mapListas != null && mapListas.isNotEmpty()) {
                val nombresListasValidas = mutableListOf<String>() // Lista para almacenar nombres válidos

                // Procesar las listas
                mapListas.forEach { (nombreLista, listas) ->
                    val listasValidas = listas.filter { lista ->
                        lista.size == 3 && !lista[1].matches(Regex(".*[a-zA-Z].*"))
                    }

                    if (listasValidas.isNotEmpty()) {
                        nombresListasValidas.add(nombreLista)
                    }
                }

                // Verificar si hay listas válidas para mostrar
                if (nombresListasValidas.isNotEmpty()) {
                    // Crear un ArrayAdapter con los nombres de las listas válidas
                    val adapter = ArrayAdapter(this, R.layout.lista_spinner, nombresListasValidas)
                    adapter.setDropDownViewResource(R.layout.lista_spinner)

                    // Limpiar adaptador anterior para evitar duplicación
                    binding.spCortes.adapter = null

                    // Asignar el adaptador al Spinner
                    binding.spCortes.adapter = adapter
                } else {
                    Toast.makeText(this, "No se encontraron listas válidas para mostrar.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No se encontraron listas guardadas.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al recuperar datos: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun listas(nombreLista: String) {
        // Cargar el mapa de listas desde MapStorage
        val mapListas = MapStorage.cargarMap(this)

        // Verificar si el mapa contiene la lista seleccionada
        if (mapListas != null && mapListas.containsKey(nombreLista)) {
            val listas = mapListas[nombreLista]

            if (listas != null && listas.isNotEmpty()) {
                // Limpiar la lista existente para evitar duplicaciones
                lista.clear()
                var errores = 0

                // Recorrer las sublistas y convertir los tipos
                listas.forEach { subLista ->
                    if (subLista.size == 3) {
                        val dato1Str = subLista[0].trim()
                        val dato2Str = subLista[1].trim()
                        val ventana = subLista[2].trim()

                        // Convertir los strings a Float e Int de manera segura
                        val dato1 = dato1Str.toFloatOrNull()
                        val dato2 = dato2Str.toIntOrNull()

                        if (dato1 != null && dato2 != null) {
                            // Añadir el Triple a la lista interna
                            lista.add(Triple(dato1, dato2, ventana))
                        } else {
                            // Contar errores para mostrar un solo Toast al final
                            errores++
                        }
                    } else {
                        // Contar errores si la subLista no tiene 3 elementos
                        errores++
                    }
                }

                // Mostrar Toast si hay errores
                if (errores > 0) {
                    Toast.makeText(this, "Hay $errores entradas inválidas en la lista seleccionada.", Toast.LENGTH_SHORT).show()
                }

                // Actualizar el ListView 'listCorte'
                actualizar()
            } else {
                // Si la lista seleccionada está vacía
                lista.clear()
                actualizar()
                Toast.makeText(this, "La lista seleccionada está vacía.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Si el nombre seleccionado no existe en el mapa
            lista.clear()
            actualizar()
            Toast.makeText(this, "No se encontró la lista seleccionada.", Toast.LENGTH_SHORT).show()
        }
    }

}
