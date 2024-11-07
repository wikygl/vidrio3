package crystal.crystal

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.casilla.MapStorage
import crystal.crystal.databinding.ActivityFichaBinding
import java.io.File
import java.io.IOException


class FichaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFichaBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFichaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btAbrir.setOnClickListener {
            // Cargar el Map guardado
            val mapListas = MapStorage.cargarMap(this)

            if (mapListas != null && mapListas.isNotEmpty()) {
                val ventanasMap = mutableMapOf<String, MutableList<Pair<String, List<Pair<String, String>>>>>()

                // Agrupar los datos por número de ventana
                for ((nombreLista, listas) in mapListas) {
                    for (lista in listas) {
                        if (lista.size == 3) {
                            val dato1 = lista[0] // Primer dato
                            val dato2 = lista[1] // Segundo dato
                            val ventana = lista[2] // El tercer elemento es el número de ventana

                            // Agregar los datos agrupados por ventana
                            if (!ventanasMap.containsKey(ventana)) {
                                ventanasMap[ventana] = mutableListOf()
                            }
                            ventanasMap[ventana]?.add(Pair(nombreLista, listOf(Pair(dato1, dato2))))
                        }
                    }
                }

                // Configurar el RecyclerView con el adaptador sin convertir a Map
                val adapter = VentanaAdapter(ventanasMap, this) // Pasar el MutableMap
                binding.rvModelo.layoutManager = LinearLayoutManager(this)
                binding.rvModelo.adapter = adapter
            } else {
                Toast.makeText(this, "No se encontraron datos para mostrar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btMaterial.setOnClickListener {
            // Cargar el Map guardado
            val mapListas = MapStorage.cargarMap(this)

            if (mapListas != null && mapListas.isNotEmpty()) {
                val materialesFiltrados = mutableMapOf<String, MutableList<String>>() // Mapa filtrado

                // Procesar las listas
                mapListas.forEach { (nombreLista, listas) ->
                    val listasValidas = listas.filter { lista ->
                        lista.size == 3 // Asegurarnos de que la lista tenga 3 elementos (dato1, dato2, dato3)
                    }.filter { lista ->
                        val dato2 = lista[1]
                        !dato2.matches(Regex(".*[a-zA-Z].*")) // Excluir si el segundo valor contiene letras
                    }.map { lista ->
                        // Acceder a los elementos de la lista correctamente
                        val dato1 = lista[0]  // Primer valor (por ejemplo, 47.7)
                        val dato2 = lista[1]  // Segundo valor (por ejemplo, 2)
                        val ventana = lista[2] // Tercer valor, que es el número de ventana (v1, v2)

                        // Crear el string en el formato correcto
                        "$dato1 = $dato2 -> $ventana"
                    }

                    if (listasValidas.isNotEmpty()) {
                        materialesFiltrados[nombreLista] = listasValidas.toMutableList()
                    }
                }

                // Verificar si hay materiales válidos
                if (materialesFiltrados.isNotEmpty()) {
                    // Configurar el RecyclerView con MaterialAdapter y cargar las listas filtradas
                    val adapter = MaterialAdapter(materialesFiltrados, this) // Usar MaterialAdapter para materiales
                    binding.rvModelo.layoutManager = LinearLayoutManager(this)
                    binding.rvModelo.adapter = adapter
                } else {
                    Toast.makeText(this, "No se encontraron materiales que coincidan", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No se encontraron datos para mostrar", Toast.LENGTH_SHORT).show()
            }
        }


    }

    class VentanaAdapter(private var ventanas: MutableMap<String, MutableList<Pair<String, List<Pair<String, String>>>>>, private val context: Context) :
        RecyclerView.Adapter<VentanaAdapter.VentanaViewHolder>() {

        private var grados: Int = 0

        class VentanaViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val ventanaTextView: TextView = view.findViewById(R.id.ventanaTextView)
            val materialesTly: TableLayout = view.findViewById(R.id.tlyMateriales)
            val referenciasTly: TableLayout = view.findViewById(R.id.tlyReferencias)
            val disenoImageView: ImageView = view.findViewById(R.id.diseno)
            val drawableNameTextView: TextView = view.findViewById(R.id.textViewDrawableName)
            val deleteButton: Button = view.findViewById(R.id.deleteButton) // Botón para eliminar elementos
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentanaViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ventana, parent, false)
            return VentanaViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: VentanaViewHolder, position: Int) {
            val ventana = ventanas.keys.toList()[position]
            val listas = ventanas[ventana]

            // Mostrar el número de ventana
            holder.ventanaTextView.text = "Ventana: $ventana"

            // Limpiar los TableLayouts para evitar duplicaciones
            holder.materialesTly.removeAllViews()
            holder.referenciasTly.removeAllViews()

            var lastNombreLista = "" // Para evitar que el nombre de la lista se repita
            grados = 0 // Reiniciar grados para cada ventana

            // Ordenar las listas para que "Grados" se procese primero
            val sortedListas = listas?.sortedWith(compareBy { if (it.first == "Grados") 0 else 1 })

            sortedListas?.forEach { (nombreLista, datos) ->
                var contieneLetra = false // Bandera para saber si la lista contiene letras después del igual

                if (nombreLista == "Grados") {
                    // Extraer el valor de grados
                    datos.forEach { (clave, _) ->
                        grados = clave.trim().toIntOrNull() ?: 0
                    }
                }

                // Procesar la lista "Diseño" normalmente
                if (nombreLista == "Diseño") {
                    datos.forEach { (clave, _) ->
                        val diseno = clave.trim()
                        // Mostrar el nombre del drawable en el TextView
                        holder.drawableNameTextView.text = diseno

                        // Obtener el ID del recurso drawable a partir del nombre
                        val drawableId = context.resources.getIdentifier(diseno, "drawable", context.packageName)

                        // Cargar el drawable en el ImageView y aplicar la inversión
                        if (drawableId != 0) {
                            holder.disenoImageView.setImageResource(drawableId)
                            // Invertir verticalmente si grados es 180, mantener normal si es 0
                            holder.disenoImageView.scaleY = if (grados == 180) -1f else 1f
                        } else {
                            holder.drawableNameTextView.text = "No se encontró: $diseno"
                        }
                    }
                } else {
                    // Procesar los datos
                    datos.forEach { (dato1, dato2) ->
                        if (dato2.matches(Regex(".*[a-zA-Z].*"))) {
                            contieneLetra = true

                            // Crear el TextView para mostrar el nombre de la lista en negrita y los valores normales
                            val textView = TextView(context)

                            // Mostrar solo nombreLista en negrita
                            val spannableString = SpannableString("$nombreLista\n$dato1")
                            spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, nombreLista.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                            textView.text = spannableString

                            // Añadir la fila a referenciasTly
                            val row = TableRow(context)
                            row.addView(textView)
                            holder.referenciasTly.addView(row)
                        } else {
                            // Mostrar el nombre de la lista en materialesTly si no contiene letras
                            if (nombreLista != lastNombreLista) {
                                val rowHeader = TableRow(context)
                                val headerTextView = TextView(context)
                                headerTextView.text = nombreLista
                                headerTextView.setTypeface(null, Typeface.BOLD)
                                rowHeader.addView(headerTextView)
                                holder.materialesTly.addView(rowHeader)
                                lastNombreLista = nombreLista
                            }

                            // Agregar los valores correspondientes
                            val row = TableRow(context)
                            val dato1TextView = TextView(context)
                            dato1TextView.text = dato1
                            row.addView(dato1TextView)

                            val dato2TextView = TextView(context)
                            dato2TextView.text = dato2
                            row.addView(dato2TextView)

                            holder.materialesTly.addView(row)
                        }
                    }
                }
            }

            // Botón para eliminar la ventana seleccionada
            holder.deleteButton.setOnClickListener {
                eliminarVentana(position)
            }
        }

        // Función para eliminar una ventana y ajustar los números
        @SuppressLint("NotifyDataSetChanged")
        private fun eliminarVentana(position: Int) {
            val ventana = ventanas.keys.toList()[position]
            ventanas.remove(ventana) // Eliminar la ventana seleccionada

            // Ajustar los números de las ventanas
            val nuevasVentanas = mutableMapOf<String, MutableList<Pair<String, List<Pair<String, String>>>>>()
            var contador = 1

            for (entry in ventanas.entries) {
                val nuevaClave = "v$contador" // Crear nueva ventana con el número ajustado
                nuevasVentanas[nuevaClave] = entry.value // Transferir los datos
                contador++
            }

            ventanas = nuevasVentanas // Actualizar el Map con los nuevos números de ventana
            notifyDataSetChanged() // Actualizar el RecyclerView
        }

        override fun getItemCount(): Int {
            return ventanas.size
        }
    }

    class MaterialAdapter(
        private var materiales: MutableMap<String, MutableList<String>>,
        private val context: Context
    ) : RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

        class MaterialViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val materialTextView: TextView = view.findViewById(R.id.materialTextView)
            val materialesTly: TableLayout = view.findViewById(R.id.tlyMateriales)
            val barras: TextView = view.findViewById(R.id.txBarras) // TextView para mostrar las barras
            val deleteButton: Button = view.findViewById(R.id.deleteButton) // Botón para eliminar elementos
            val exportButton: Button = view.findViewById(R.id.btExportar) // Botón para exportar el elemento
            val todoButton: Button = view.findViewById(R.id.btExporTodo) // Botón para exportar todo
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_material, parent, false)
            return MaterialViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
            val nombreLista = materiales.keys.toList()[position]
            val listasDatos = materiales[nombreLista]

            // Mostrar el nombre de la lista (material)
            holder.materialTextView.text = nombreLista

            // Limpiar el TableLayout para evitar duplicaciones
            holder.materialesTly.removeAllViews()

            val valoresBarras = mutableListOf<Pair<Float, Int>>() // Lista para almacenar los pares de valores
            val datosParaCSV = mutableListOf<String>() // Lista para almacenar los valores formateados para CSV

            // Mostrar los datos correctamente
            listasDatos?.forEach { datosFormateados ->
                // Crear una fila para agregar al TableLayout con el formato correcto
                val row = TableRow(context)

                val materialTextView = TextView(context)
                materialTextView.text = datosFormateados // Los valores ya están formateados
                row.addView(materialTextView)

                // Extraer valores de datosFormateados y agregar a valoresBarras
                val partes = datosFormateados.split("=", "->")
                if (partes.size >= 3) {
                    val valorStr = partes[0].trim()
                    val cantidadStr = partes[1].trim()
                    val ventana = partes[2].trim()

                    val valor = valorStr.toFloatOrNull() ?: 0f
                    val cantidad = cantidadStr.toIntOrNull() ?: 0

                    valoresBarras.add(Pair(valor, cantidad))

                    // Si es la lista "Vidrios", procesar para CSV en el formato correcto
                    if (nombreLista == "Vidrios") {
                        val valoresXY = valorStr.split("x")
                        if (valoresXY.size == 2) {
                            val valor1 = valoresXY[0].trim() // Antes de la "x"
                            val valor2 = valoresXY[1].trim() // Después de la "x"
                            datosParaCSV.add("$valor2,$valor1,$cantidadStr,true,$ventana")
                        }
                    }
                }

                holder.materialesTly.addView(row)
            }

            // Calcular el número de barras y mostrarlo
            val numeroBarras = calcularBarras(valoresBarras)
            holder.barras.text = "$numeroBarras barras"

            // Botón para eliminar la lista seleccionada
            holder.deleteButton.setOnClickListener {
                eliminarMaterial(position)
            }

            // Botón para exportar un solo elemento en su respectivo formato
            holder.exportButton.setOnClickListener {
                if (nombreLista == "Vidrios") {
                    // Exportar "Vidrios" en CSV
                    exportarElementoCSV(nombreLista, datosParaCSV)
                } else {
                    // Exportar otras listas en TXT
                    exportarElementoTxt(nombreLista, listasDatos)
                }
            }

            // Botón para exportar todos los elementos
            holder.todoButton.setOnClickListener {
                exportarTodo()
            }
        }

        // Función para eliminar un material y ajustar la lista
        @SuppressLint("NotifyDataSetChanged")
        private fun eliminarMaterial(position: Int) {
            val material = materiales.keys.toList()[position]
            materiales.remove(material) // Eliminar el material seleccionado

            // Actualizar el RecyclerView
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return materiales.size
        }

        // Función para calcular el número de barras usando Float
        private fun calcularBarras(valores: List<Pair<Float, Int>>): Int {
            // 1. Multiplicación y 2. Suma usando fold para Float
            val suma = valores.fold(0f) { acc, (valor, cantidad) -> acc + (valor * cantidad) }

            // 3. Incremento del 5%
            val sumaConIncremento = suma * 1.05f

            // 4. División
            val division = sumaConIncremento / 600

            // 5. Redondeo hacia arriba
            return kotlin.math.ceil(division).toInt()
        }

        // Función para exportar un solo elemento de "Vidrios" a CSV
        private fun exportarElementoCSV(nombreLista: String, datosParaCSV: List<String>) {
            val csvContent = buildString {
                append("$nombreLista\n")
                datosParaCSV.forEach { append("$it\n") }
            }

            guardarArchivo(csvContent, "${nombreLista}_export.csv")
            Toast.makeText(context, "Se exportó $nombreLista en CSV", Toast.LENGTH_SHORT).show()
        }

        // Función para exportar un solo elemento en TXT
        private fun exportarElementoTxt(nombreLista: String, listasDatos: List<String>?) {
            val txtContent = buildString {
                listasDatos?.forEach { datosFormateados ->
                    val partes = datosFormateados.split("=", "->")
                    if (partes.size >= 3) {
                        val valor = partes[0].trim()
                        val cantidad = partes[1].trim()
                        val ventana = partes[2].trim()
                        // Formato: valor:cantidad:ventana
                        append("$valor:$cantidad:$ventana\n")
                    }
                }
            }

            guardarArchivo(txtContent, "$nombreLista.txt")
            Toast.makeText(context, "Se exportó $nombreLista en TXT", Toast.LENGTH_SHORT).show()
        }

        // Función para exportar todos los elementos
        private fun exportarTodo() {
            // Exportar "Vidrios" en CSV
            materiales["Vidrios"]?.let { listasDatos ->
                val datosParaCSV = mutableListOf<String>()
                listasDatos.forEach { datosFormateados ->
                    val partes = datosFormateados.split("=", "->")
                    if (partes.size >= 3) {
                        val valorStr = partes[0].trim()
                        val cantidadStr = partes[1].trim()
                        val ventana = partes[2].trim()
                        val valoresXY = valorStr.split("x")
                        if (valoresXY.size == 2) {
                            val valor1 = valoresXY[0].trim()
                            val valor2 = valoresXY[1].trim()
                            datosParaCSV.add("$valor2,$valor1,$cantidadStr,true,$ventana")
                        }
                    }
                }
                exportarElementoCSV("Vidrios", datosParaCSV)
            }

            // Exportar otras listas en TXT
            materiales.forEach { (nombreLista, listasDatos) ->
                if (nombreLista != "Vidrios") {
                    exportarElementoTxt(nombreLista, listasDatos)
                }
            }
            Toast.makeText(context, "Se exportaron todas las listas", Toast.LENGTH_SHORT).show()
        }

        // Función para guardar contenido en un archivo (CSV o TXT)
        private fun guardarArchivo(contenido: String, nombreArchivo: String) {
            try {
                val file = File(context.getExternalFilesDir(null), nombreArchivo)
                file.writeText(contenido)
                // No mostrar Toast aquí, ya que lo mostramos en las funciones de exportar
            } catch (e: IOException) {
                Toast.makeText(context, "Error al guardar el archivo $nombreArchivo", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

