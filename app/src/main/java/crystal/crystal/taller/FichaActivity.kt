package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.TableRow.LayoutParams as TRLayoutParams
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caverock.androidsvg.SVG
import crystal.crystal.Diseno.nova.DisenoNovaActivity
import crystal.crystal.R
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
            val mapListas = MapStorage.cargarMap(this)

            if (mapListas != null && mapListas.isNotEmpty()) {
                val ventanasMap = mutableMapOf<String, MutableList<Pair<String, List<Pair<String, String>>>>>()

                for ((nombreLista, listas) in mapListas) {
                    if (nombreLista == "Pedido") continue
                    for (lista in listas) {
                        if (lista.size >= 3) {
                            val dato1 = lista[0].trim()
                            val dato2 = lista[1].trim()
                            val ventana = lista[2]

                            // Filtrar valores 0 o cantidad 0 (excepto referencias/diseño/grados/diseno_paquete)
                            if (nombreLista != "Referencias" && nombreLista != "Diseño"
                                && nombreLista != "Grados" && nombreLista != "DisenoPaquete") {
                                val cantNum = dato2.toIntOrNull()
                                if (dato2.isBlank() || cantNum == null || cantNum == 0) continue
                                val valNum = dato1.toFloatOrNull()
                                if (valNum != null && valNum == 0f) continue
                            }

                            if (!ventanasMap.containsKey(ventana)) {
                                ventanasMap[ventana] = mutableListOf()
                            }
                            ventanasMap[ventana]?.add(Pair(nombreLista, listOf(Pair(dato1, dato2))))
                        }
                    }
                }

                val adapter = VentanaAdapter(ventanasMap, this)
                binding.rvModelo.layoutManager = LinearLayoutManager(this)
                binding.rvModelo.adapter = adapter
            } else {
                Toast.makeText(this, "No se encontraron datos para mostrar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btMaterial.setOnClickListener {
            val mapListas = MapStorage.cargarMap(this)

            if (mapListas != null && mapListas.isNotEmpty()) {
                val materialesFiltrados = mutableMapOf<String, MutableList<String>>()

                mapListas.forEach { (nombreLista, listas) ->
                    // Excluir listas internas que no son materiales
                    if (nombreLista == "Diseño" || nombreLista == "DisenoPaquete"
                        || nombreLista == "Grados") return@forEach
                    if (nombreLista == "Pedido" || nombreLista == "Referencias") {
                        // Pedido/Referencias: texto descriptivo, no numérico
                        val items = listas.filter { it.size >= 2 }.map { lista ->
                            val ventana = lista.getOrElse(2) { "" }
                            val cliente = lista.getOrElse(3) { "" }
                            val partes = mutableListOf(lista[0])
                            if (ventana.isNotBlank()) partes.add(ventana)
                            if (cliente.isNotBlank()) partes.add(cliente)
                            partes.joinToString(" -> ")
                        }
                        if (items.isNotEmpty()) {
                            materialesFiltrados[nombreLista] = items.toMutableList()
                        }
                        return@forEach
                    }
                    val listasValidas = listas.filter { lista ->
                        lista.size >= 3
                    }.filter { lista ->
                        val dato1 = lista[0].trim()
                        val dato2 = lista[1].trim()
                        // Excluir entradas con letras en cantidad
                        if (dato2.matches(Regex(".*[a-zA-Z].*"))) return@filter false
                        // Excluir entradas con valor 0 o cantidad vacía/0
                        val cantidadNum = dato2.toIntOrNull()
                        if (dato2.isBlank() || cantidadNum == null || cantidadNum == 0) return@filter false
                        val valorNum = dato1.toFloatOrNull()
                        if (valorNum != null && valorNum == 0f) return@filter false
                        true
                    }.map { lista ->
                        val dato1 = lista[0]
                        val dato2 = lista[1]
                        val ventana = lista[2]

                        val cliente = lista.getOrElse(3) { "" }
                        if (cliente.isNotBlank()) {
                            "$dato1 = $dato2 -> $ventana, $cliente"
                        } else {
                            "$dato1 = $dato2 -> $ventana"
                        }
                    }

                    if (listasValidas.isNotEmpty()) {
                        materialesFiltrados[nombreLista] = listasValidas.toMutableList()
                    }
                }

                if (materialesFiltrados.isNotEmpty()) {
                    val ordenado = ordenarMateriales(materialesFiltrados)
                    val adapter = MaterialAdapter(ordenado, this)
                    binding.rvModelo.layoutManager = LinearLayoutManager(this)
                    binding.rvModelo.adapter = adapter
                } else {
                    Toast.makeText(this, "No se encontraron materiales que coincidan", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No se encontraron datos para mostrar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btCliente.setOnClickListener {
            val mapListas = MapStorage.cargarMap(this)

            if (mapListas != null && mapListas.isNotEmpty()) {
                val clientesMap = mutableMapOf<String, MutableMap<String, MutableList<String>>>()
                // Pedido por cliente: lista de descripciones del pedido
                val pedidoPorCliente = mutableMapOf<String, MutableList<String>>()

                mapListas.forEach { (nombreLista, listas) ->
                    if (nombreLista == "Referencias" || nombreLista == "Diseño"
                        || nombreLista == "DisenoPaquete" || nombreLista == "Grados") return@forEach

                    if (nombreLista == "Pedido") {
                        // Agrupar items de pedido por cliente
                        listas.filter { it.size >= 2 }.forEach { lista ->
                            val descripcion = lista[0]
                            val cliente = lista.getOrElse(3) { "Sin cliente" }
                            pedidoPorCliente.getOrPut(cliente) { mutableListOf() }.add(descripcion)
                        }
                        return@forEach
                    }

                    listas.filter { it.size >= 3 }.filter { lista ->
                        val d1 = lista[0].trim()
                        val d2 = lista[1].trim()
                        if (d2.matches(Regex(".*[a-zA-Z].*"))) return@filter false
                        val cantNum = d2.toIntOrNull()
                        if (d2.isBlank() || cantNum == null || cantNum == 0) return@filter false
                        val valNum = d1.toFloatOrNull()
                        if (valNum != null && valNum == 0f) return@filter false
                        true
                    }.forEach { lista ->
                        val dato1 = lista[0]
                        val dato2 = lista[1]
                        val ventana = lista[2]
                        val cliente = lista.getOrElse(3) { "Sin cliente" }

                        val materialesDelCliente = clientesMap.getOrPut(cliente) { mutableMapOf() }
                        val listaDelMaterial = materialesDelCliente.getOrPut(nombreLista) { mutableListOf() }

                        val linea = "$dato1 = $dato2 -> $ventana"
                        listaDelMaterial.add(linea)
                    }
                }

                // Asegurar que clientes con pedido pero sin materiales también aparezcan
                for (cliente in pedidoPorCliente.keys) {
                    clientesMap.getOrPut(cliente) { mutableMapOf() }
                }

                if (clientesMap.isNotEmpty()) {
                    val adapter = ClienteAdapter(clientesMap, pedidoPorCliente, this)
                    binding.rvModelo.layoutManager = LinearLayoutManager(this)
                    binding.rvModelo.adapter = adapter
                } else {
                    Toast.makeText(this, "No se encontraron datos por cliente", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No se encontraron datos para mostrar", Toast.LENGTH_SHORT).show()
            }
        }

    }

    /**
     * Ordena las listas de materiales:
     * 1. Referencias primero
     * 2. Luego agrupa por nombre base similar (ej: todos los "u-13" juntos, todos los "Riel" juntos)
     * 3. Dentro de cada grupo, ordena alfabéticamente
     */
    private fun ordenarMateriales(
        materiales: MutableMap<String, MutableList<String>>
    ): MutableMap<String, MutableList<String>> {
        val ordenado = linkedMapOf<String, MutableList<String>>()

        // 1. Pedido primero, luego Referencias
        materiales["Pedido"]?.let { ordenado["Pedido"] = it }
        materiales["Referencias"]?.let { ordenado["Referencias"] = it }

        // 2. Resto ordenado por nombre base (parte antes del color)
        val resto = materiales.filter { it.key != "Pedido" && it.key != "Referencias" }
        val ordenadoPorBase = resto.keys.sortedWith(compareBy { nombreBase(it) })
        for (clave in ordenadoPorBase) {
            ordenado[clave] = materiales[clave]!!
        }

        return ordenado
    }

    /** Extrae el nombre base para agrupar similares: "u-13 negro" → "u-13", "Riel plateado" → "riel" */
    private fun nombreBase(nombre: String): String {
        // Colores conocidos que pueden aparecer como sufijo
        val colores = listOf(
            "negro", "blanco", "plateado", "bronce", "natural", "madera",
            "champagne", "gris", "dorado", "mate", "brillante", "anodizado"
        )
        val lower = nombre.lowercase().trim()
        for (color in colores) {
            if (lower.endsWith(" $color")) {
                return lower.removeSuffix(" $color")
            }
        }
        return lower
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

            // Ordenar: Grados primero (para rotación), luego Referencias, luego el resto
            var tieneDisenoPaquete = false
            val sortedListas = listas?.sortedWith(compareBy {
                when {
                    it.first == "Grados" -> 0
                    it.first == "Referencias" -> 1
                    it.first.startsWith("Vidrios", ignoreCase = true) -> 3
                    it.first == "DisenoPaquete" -> 4
                    it.first == "Diseño" -> 5
                    it.first == "Pedido" -> 6
                    else -> 2 // Perfiles de aluminio y demás materiales
                }
            })

            sortedListas?.forEach { (nombreLista, datos) ->
                var contieneLetra = false // Bandera para saber si la lista contiene letras después del igual

                if (nombreLista == "Grados") {
                    // Extraer el valor de grados
                    datos.forEach { (clave, _) ->
                        grados = clave.trim().toIntOrNull() ?: 0
                    }
                }

                // Diseño: generar imagen desde DisenoPaquete si existe, sino usar drawable
                if (nombreLista == "DisenoPaquete") {
                    datos.forEach { (paquete, _) ->
                        val paq = paquete.trim()
                        if (paq.isNotBlank()) {
                            try {
                                val vista = crystal.crystal.Diseno.nova.VistaDiseno(context).apply {
                                    actualizarDesdePaquete(paq, 0f, 0f, 0f)
                                }
                                val dm = context.resources.displayMetrics
                                val w = dm.widthPixels.coerceAtLeast(720)
                                val h = dm.heightPixels.coerceAtLeast(720)
                                vista.layout(0, 0, w, h)
                                val svgStr = vista.exportarSoloDisenoSVG(paddingPx = 4)
                                val svg = SVG.getFromString(svgStr)
                                val picture = svg.renderToPicture()
                                val drawable = PictureDrawable(picture)
                                holder.disenoImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                                holder.disenoImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                                holder.disenoImageView.setImageDrawable(drawable)
                                holder.disenoImageView.scaleY = if (grados == 180) -1f else 1f
                                holder.drawableNameTextView.text = paq
                                tieneDisenoPaquete = true
                            } catch (e: Exception) {
                                holder.drawableNameTextView.text = "Error diseño: ${e.message}"
                            }
                        }
                    }
                } else if (nombreLista == "Diseño" && !tieneDisenoPaquete) {
                    datos.forEach { (clave, _) ->
                        val diseno = clave.trim()
                        holder.drawableNameTextView.text = diseno
                        val drawableId = context.resources.getIdentifier(diseno, "drawable", context.packageName)
                        if (drawableId != 0) {
                            holder.disenoImageView.setImageResource(drawableId)
                            holder.disenoImageView.scaleY = if (grados == 180) -1f else 1f
                        } else {
                            holder.drawableNameTextView.text = "No se encontró: $diseno"
                        }
                    }
                } else {
                    // Procesar los datos
                    datos.forEach { (dato1, dato2) ->
                        if (dato2.matches(Regex(".*[a-zA-Z].*")) || nombreLista == "Pedido" || nombreLista == "Referencias") {
                            contieneLetra = true

                            val textView = TextView(context).apply {
                                val spannableString = SpannableString("$nombreLista\n$dato1")
                                spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, nombreLista.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                text = spannableString
                                layoutParams = TRLayoutParams(0, TRLayoutParams.WRAP_CONTENT, 1f)
                            }

                            val row = TableRow(context)
                            row.addView(textView)
                            holder.referenciasTly.addView(row)
                        } else {
                            // Mostrar el nombre de la lista en materialesTly si no contiene letras
                            if (nombreLista != lastNombreLista) {
                                val rowHeader = TableRow(context)
                                val headerTextView = TextView(context).apply {
                                    text = nombreLista
                                    setTypeface(null, Typeface.BOLD)
                                    layoutParams = TRLayoutParams(0, TRLayoutParams.WRAP_CONTENT, 1f)
                                }
                                rowHeader.addView(headerTextView)
                                holder.materialesTly.addView(rowHeader)
                                lastNombreLista = nombreLista
                            }

                            val row = TableRow(context)
                            val tv = TextView(context).apply {
                                text = if (dato2.isNotBlank()) "$dato1 = $dato2" else dato1
                                layoutParams = TRLayoutParams(0, TRLayoutParams.WRAP_CONTENT, 1f)
                            }
                            row.addView(tv)

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

    class ClienteAdapter(
        private val clientesMap: Map<String, Map<String, List<String>>>,
        private val pedidoPorCliente: Map<String, List<String>>,
        private val context: Context
    ) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

        private val clientes = clientesMap.keys.toList()

        class ClienteViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val materialTextView: TextView = view.findViewById(R.id.materialTextView)
            val materialesTly: TableLayout = view.findViewById(R.id.tlyMateriales)
            val barras: TextView = view.findViewById(R.id.txBarras)
            val deleteButton: Button = view.findViewById(R.id.deleteButton)
            val exportButton: Button = view.findViewById(R.id.btExportar)
            val todoButton: Button = view.findViewById(R.id.btExporTodo)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_material, parent, false)
            return ClienteViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
            val cliente = clientes[position]
            val materialesDelCliente = clientesMap[cliente] ?: return

            holder.materialTextView.text = cliente
            holder.materialesTly.removeAllViews()
            holder.barras.text = ""
            holder.deleteButton.visibility = View.GONE
            holder.exportButton.visibility = View.GONE
            holder.todoButton.visibility = View.GONE

            // Pedido: resumen de lo contratado por este cliente
            val pedido = pedidoPorCliente[cliente]
            if (!pedido.isNullOrEmpty()) {
                val rowHeader = TableRow(context)
                val headerTv = TextView(context).apply {
                    text = "Pedido"
                    setTypeface(null, Typeface.BOLD)
                    setPadding(0, 8, 0, 4)
                    layoutParams = TRLayoutParams(0, TRLayoutParams.WRAP_CONTENT, 1f)
                }
                rowHeader.addView(headerTv)
                holder.materialesTly.addView(rowHeader)

                for (descripcion in pedido) {
                    val row = TableRow(context)
                    val tv = TextView(context).apply {
                        text = descripcion
                        setPadding(8, 2, 0, 2)
                        layoutParams = TRLayoutParams(0, TRLayoutParams.WRAP_CONTENT, 1f)
                    }
                    row.addView(tv)
                    holder.materialesTly.addView(row)
                }
            }

            for ((nombreMaterial, lineas) in materialesDelCliente) {
                val rowHeader = TableRow(context)
                val headerTv = TextView(context).apply {
                    text = nombreMaterial
                    setTypeface(null, Typeface.BOLD)
                    setPadding(0, 16, 0, 4)
                    layoutParams = TRLayoutParams(0, TRLayoutParams.WRAP_CONTENT, 1f)
                }
                rowHeader.addView(headerTv)
                holder.materialesTly.addView(rowHeader)

                val valoresBarras = mutableListOf<Pair<Float, Int>>()
                for (linea in lineas) {
                    val row = TableRow(context)
                    val tv = TextView(context).apply {
                        text = linea
                        layoutParams = TRLayoutParams(0, TRLayoutParams.WRAP_CONTENT, 1f)
                    }
                    row.addView(tv)
                    holder.materialesTly.addView(row)

                    val partes = linea.split("=", "->")
                    if (partes.size >= 2) {
                        val valor = partes[0].trim().toFloatOrNull() ?: 0f
                        val cantidad = partes[1].trim().toIntOrNull() ?: 0
                        valoresBarras.add(Pair(valor, cantidad))
                    }
                }

                // Barras por material
                if (valoresBarras.isNotEmpty()) {
                    val suma = valoresBarras.fold(0f) { acc, (v, c) -> acc + (v * c) }
                    val barras = kotlin.math.ceil(suma * 1.05f / 600).toInt()
                    val rowBarras = TableRow(context)
                    val tvBarras = TextView(context).apply {
                        text = "  $barras barras"
                        setTypeface(null, Typeface.ITALIC)
                        layoutParams = TRLayoutParams(0, TRLayoutParams.WRAP_CONTENT, 1f)
                    }
                    rowBarras.addView(tvBarras)
                    holder.materialesTly.addView(rowBarras)
                }
            }
        }

        override fun getItemCount() = clientes.size
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
                val row = TableRow(context)
                val materialTextView = TextView(context).apply {
                    text = datosFormateados
                    layoutParams = TRLayoutParams(0, TRLayoutParams.WRAP_CONTENT, 1f)
                }
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

