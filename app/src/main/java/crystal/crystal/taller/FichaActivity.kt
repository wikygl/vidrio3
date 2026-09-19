package crystal.crystal.taller

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import crystal.crystal.R
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.databinding.ActivityFichaBinding
import java.io.File
import java.io.IOException


class FichaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFichaBinding

    /** Qué vista está cargada en el recycler, para titular y nombrar bien el PDF. */
    private var vistaActual: String = "Ficha"

    /**
     * Toque largo en cualquiera de los botones = exportar a PDF lo que está en pantalla, para
     * mandárselo a quien no tiene Crystal. Va en el toque largo porque la barra ya tiene sus cuatro
     * botones y es el gesto que la app usa para las acciones secundarias.
     */
    private fun configurarExportacionPdf() {
        val exportar = View.OnLongClickListener {
            val adapter = binding.rvModelo.adapter
            if (adapter == null || adapter.itemCount == 0) {
                Toast.makeText(this, "Primero abre una lista para exportarla", Toast.LENGTH_SHORT).show()
            } else {
                val proyecto = ProyectoManager.getProyectoActivo().orEmpty()
                val titulo = if (proyecto.isBlank()) vistaActual else "$vistaActual - $proyecto"
                Toast.makeText(this, "Generando PDF...", Toast.LENGTH_SHORT).show()
                FichaPdfExport.exportarYCompartir(
                    context = this,
                    adapter = adapter,
                    titulo = titulo,
                    nombreBase = "${vistaActual}_${proyecto.ifBlank { "crystal" }}"
                )
            }
            true
        }
        binding.btAbrir.setOnLongClickListener(exportar)
        binding.btCliente.setOnLongClickListener(exportar)
        binding.btMaterial.setOnLongClickListener(exportar)
        binding.btPlanos.setOnLongClickListener(exportar)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFichaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ProyectoManager.inicializarDesdeStorage(this)
        configurarExportacionPdf()

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
                                && nombreLista != "Grados" && nombreLista != "DisenoPaquete"
                                && nombreLista != "DisenoSimbolicoV2" && nombreLista != "DisenoPuerta"
                                && nombreLista != "DisenoVentanaAl"
                                && nombreLista != crystal.crystal.taller.melamina.RoperoActivity.CLAVE_DISENO
                                && nombreLista != crystal.crystal.taller.VitrovenDescriptor.CLAVE
                                && nombreLista != "Color aluminio" && nombreLista != "Tipo vidrio"
                                && nombreLista != "DisenoMampara") {
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
                vistaActual = "Productos"
            } else {
                Toast.makeText(this, "No se encontraron datos para mostrar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btMaterial.setOnClickListener {
            mostrarSelectorProyectos { proyectosElegidos ->
                val mapListas = mergearProyectos(proyectosElegidos)
                if (mapListas.isEmpty()) {
                    Toast.makeText(this, "No se encontraron datos para mostrar", Toast.LENGTH_SHORT).show()
                    return@mostrarSelectorProyectos
                }
                @Suppress("UNCHECKED_CAST")
                val materialesFiltrados = procesarMateriales(mapListas as Map<String, List<List<String>>>)
                if (materialesFiltrados.isNotEmpty()) {
                    val ordenado = ordenarMateriales(materialesFiltrados)
                    val adapter = MaterialAdapter(ordenado, this)
                    binding.rvModelo.layoutManager = LinearLayoutManager(this)
                    binding.rvModelo.adapter = adapter
                    vistaActual = "Materiales"
                } else {
                    Toast.makeText(this, "No se encontraron materiales que coincidan", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btCliente.setOnClickListener {
            mostrarSelectorProyectos { proyectosElegidos ->
                val mapListas = mergearProyectos(proyectosElegidos)
                if (mapListas.isEmpty()) {
                    Toast.makeText(this, "No se encontraron datos para mostrar", Toast.LENGTH_SHORT).show()
                    return@mostrarSelectorProyectos
                }
                val (clientesMap, pedidoPorCliente) = procesarPorCliente(mapListas)
                if (clientesMap.isNotEmpty()) {
                    val adapter = ClienteAdapter(clientesMap, pedidoPorCliente, this)
                    binding.rvModelo.layoutManager = LinearLayoutManager(this)
                    binding.rvModelo.adapter = adapter
                    vistaActual = "Clientes"
                } else {
                    Toast.makeText(this, "No se encontraron datos por cliente", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btPlanos.setOnClickListener {
            mostrarSelectorProyectos { proyectosElegidos ->
                // Se recorre proyecto por proyecto para fijar el cliente = nombre del proyecto de forma
                // explícita (no se usa el cliente archivado en el descriptor ni el campo 3).
                val items = mutableListOf<PlanoItem>()
                for (proyecto in proyectosElegidos) {
                    val map = MapStorage.cargarProyecto(this, proyecto) ?: continue
                    // Color de aluminio y tipo de vidrio por producto (id), desde DisenoSimbolicoV2.
                    val colorPorId = mutableMapOf<String, Pair<String, String>>()
                    map["DisenoSimbolicoV2"]?.forEach { l ->
                        val paq = l.getOrNull(0)?.trim() ?: return@forEach
                        val id = l.getOrNull(2) ?: return@forEach
                        colorPorId[id] = extraerCampoMat(paq, "alu") to extraerCampoMat(paq, "vid")
                    }
                    map[crystal.crystal.taller.puerta.PuertaDescriptor.CLAVE]?.forEach { lista ->
                        val d = lista.getOrNull(0)?.let { crystal.crystal.taller.puerta.PuertaDescriptor.parsear(it.trim()) }
                        if (d != null) {
                            val id = lista.getOrNull(2) ?: ""               // id completo "P10, abel" (para color)
                            val numDisplay = id.substringBefore(",").trim() // solo el número "P10" (para mostrar)
                            val (alu, vid) = colorPorId[id] ?: ("" to "")
                            items.add(PlanoItem(d.copy(cliente = proyecto), numDisplay, alu, vid))
                        }
                    }
                }
                if (items.isEmpty()) {
                    Toast.makeText(this, "No hay planos de puerta archivados", Toast.LENGTH_SHORT).show()
                } else {
                    binding.rvModelo.layoutManager = LinearLayoutManager(this)
                    binding.rvModelo.adapter = PlanoAdapter(items, this)
                    vistaActual = "Planos"
                }
            }
        }

    }

    private fun mostrarSelectorProyectos(onSeleccionados: (List<String>) -> Unit) {
        val proyectos = MapStorage.obtenerListaProyectos(this)
        if (proyectos.isEmpty()) {
            Toast.makeText(this, "No hay proyectos disponibles", Toast.LENGTH_SHORT).show()
            return
        }
        val seleccionados = BooleanArray(proyectos.size) { false }
        AlertDialog.Builder(this)
            .setTitle("Seleccionar proyectos")
            .setMultiChoiceItems(proyectos.toTypedArray(), seleccionados) { _, i, checked ->
                seleccionados[i] = checked
            }
            .setPositiveButton("Mostrar") { _, _ ->
                val elegidos = proyectos.filterIndexed { i, _ -> seleccionados[i] }
                if (elegidos.isNotEmpty()) onSeleccionados(elegidos)
                else Toast.makeText(this, "Selecciona al menos un proyecto", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mergearProyectos(
        proyectos: List<String>
    ): MutableMap<String, MutableList<MutableList<String>>> {
        val merged = mutableMapOf<String, MutableList<MutableList<String>>>()
        for (proyecto in proyectos) {
            val map = MapStorage.cargarProyecto(this, proyecto) ?: continue
            for ((clave, entradas) in map) {
                val lista = merged.getOrPut(clave) { mutableListOf() }
                for (entrada in entradas) {
                    val e = entrada.toMutableList()
                    while (e.size < 4) e.add("")
                    if (e[3].isBlank()) e[3] = proyecto
                    lista.add(e)
                }
            }
        }
        return merged
    }

    private fun procesarPorCliente(
        mapListas: MutableMap<String, MutableList<MutableList<String>>>
    ): Pair<MutableMap<String, MutableMap<String, MutableList<String>>>, MutableMap<String, MutableList<String>>> {
        val clientesMap = mutableMapOf<String, MutableMap<String, MutableList<String>>>()
        val pedidoPorCliente = mutableMapOf<String, MutableList<String>>()
        val excluidas = setOf(
            "Referencias", "Diseño", "DisenoPaquete", "DisenoSimbolicoV2", "Grados",
            "DisenoMampara", "DisenoPuerta", crystal.crystal.taller.VitrovenDescriptor.CLAVE,
            crystal.crystal.taller.melamina.RoperoActivity.CLAVE_DISENO
        )

        // Build ventana→color lookup for color-differentiated keys
        val ventanaColorMap = mutableMapOf<String, Pair<String, String>>()
        mapListas["DisenoSimbolicoV2"]?.forEach { lista ->
            val paquete = lista.getOrNull(0)?.trim() ?: return@forEach
            val ventana = lista.getOrElse(2) { "" }.ifBlank { null } ?: return@forEach
            val colorAlu = extraerCampoMat(paquete, "alu")
            val tipoVidrio = extraerCampoMat(paquete, "vid")
            if (colorAlu.isNotBlank() || tipoVidrio.isNotBlank()) {
                ventanaColorMap[ventana] = Pair(colorAlu, tipoVidrio)
            }
        }

        mapListas.forEach { (nombreLista, listas) ->
            if (nombreLista in excluidas) return@forEach

            if (nombreLista == "Pedido") {
                listas.filter { it.size >= 2 }.forEach { lista ->
                    val descripcion = lista[0]
                    val cliente = lista.getOrElse(3) { "" }.ifBlank { "Sin cliente" }
                    pedidoPorCliente.getOrPut(cliente) { mutableListOf() }.add(descripcion)
                }
                return@forEach
            }

            listas.filter { it.size >= 3 }.filter { lista ->
                val d2 = lista[1].trim()
                if (d2.matches(Regex(".*[a-zA-Z].*"))) return@filter false
                val cantNum = d2.toIntOrNull()
                if (d2.isBlank() || cantNum == null || cantNum == 0) return@filter false
                val valNum = lista[0].trim().toFloatOrNull()
                if (valNum != null && valNum == 0f) return@filter false
                true
            }.forEach { lista ->
                val dato1 = lista[0]
                val dato2 = lista[1]
                val ventanaId = lista[2]                              // id completo (para color)
                val ventana = ventanaId.split(',', ' ').first().trim() // solo el número (para mostrar)
                val cliente = lista.getOrElse(3) { "" }.ifBlank { "Sin cliente" }

                val esVidrio = nombreLista.startsWith("Vidrio", ignoreCase = true)
                val (colorAlu, tipoVidrio) = ventanaColorMap[ventanaId] ?: Pair("", "")
                val sufijo = if (esVidrio) tipoVidrio else colorAlu
                val clave = if (sufijo.isNotBlank() && !nombreLista.contains(sufijo, ignoreCase = true)) {
                    "$nombreLista [$sufijo]"
                } else {
                    nombreLista
                }

                clientesMap.getOrPut(cliente) { mutableMapOf() }
                    .getOrPut(clave) { mutableListOf() }
                    .add("$dato1 = $dato2 -> $ventana")
            }
        }

        for (cliente in pedidoPorCliente.keys) {
            clientesMap.getOrPut(cliente) { mutableMapOf() }
        }
        return Pair(clientesMap, pedidoPorCliente)
    }

    private fun procesarMateriales(
        mapListas: Map<String, List<List<String>>>
    ): MutableMap<String, MutableList<String>> {
        val materialesFiltrados = mutableMapOf<String, MutableList<String>>()

        val ventanaColorMap = mutableMapOf<String, Pair<String, String>>()
        mapListas["DisenoSimbolicoV2"]?.forEach { lista ->
            val paquete = lista.getOrNull(0)?.trim() ?: return@forEach
            val ventana = lista.getOrElse(2) { "" }.ifBlank { null } ?: return@forEach
            val colorAlu = extraerCampoMat(paquete, "alu")
            val tipoVidrio = extraerCampoMat(paquete, "vid")
            if (colorAlu.isNotBlank() || tipoVidrio.isNotBlank())
                ventanaColorMap[ventana] = Pair(colorAlu, tipoVidrio)
        }

        mapListas.forEach { (nombreLista, listas) ->
            if (nombreLista == "Diseño" || nombreLista == "DisenoPaquete"
                || nombreLista == "DisenoSimbolicoV2" || nombreLista == "Grados"
                || nombreLista == "DisenoMampara" || nombreLista == "DisenoPuerta"
                || nombreLista == crystal.crystal.taller.melamina.RoperoActivity.CLAVE_DISENO
                || nombreLista == crystal.crystal.taller.VitrovenDescriptor.CLAVE) return@forEach

            if (nombreLista == "Pedido" || nombreLista == "Referencias") {
                val items = listas.filter { it.size >= 2 }.map { lista ->
                    val ventana = lista.getOrElse(2) { "" }.split(',', ' ').first().trim()
                    val cliente = lista.getOrElse(3) { "" }
                    val partes = mutableListOf(lista[0])
                    if (ventana.isNotBlank()) partes.add(ventana)
                    if (cliente.isNotBlank()) partes.add(cliente)
                    partes.joinToString(" -> ")
                }
                if (items.isNotEmpty()) materialesFiltrados[nombreLista] = items.toMutableList()
                return@forEach
            }

            listas.filter { lista ->
                if (lista.size < 3) return@filter false
                val dato2 = lista[1].trim()
                if (dato2.matches(Regex(".*[a-zA-Z].*"))) return@filter false
                val cantNum = dato2.toIntOrNull()
                if (dato2.isBlank() || cantNum == null || cantNum == 0) return@filter false
                val valNum = lista[0].trim().toFloatOrNull()
                if (valNum != null && valNum == 0f) return@filter false
                true
            }.forEach { lista ->
                val dato1 = lista[0]
                val dato2 = lista[1]
                val ventanaId = lista[2]                              // id completo (para color)
                val ventana = ventanaId.split(',', ' ').first().trim() // solo el número (para mostrar)
                val cliente = lista.getOrElse(3) { "" }
                    .ifBlank { ProyectoManager.getProyectoActivo() ?: "" }

                val (colorAlu, tipoVidrio) = ventanaColorMap[ventanaId] ?: Pair("", "")
                val esVidrio = nombreLista.startsWith("Vidrio", ignoreCase = true)
                val sufijo = if (esVidrio) tipoVidrio else colorAlu
                val clave = if (sufijo.isNotBlank() && !nombreLista.contains(sufijo, ignoreCase = true))
                    "$nombreLista [$sufijo]" else nombreLista

                val linea = if (cliente.isNotBlank()) "$dato1 = $dato2 -> $ventana, $cliente"
                            else "$dato1 = $dato2 -> $ventana"
                materialesFiltrados.getOrPut(clave) { mutableListOf() }.add(linea)
            }
        }

        return materialesFiltrados
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

    /** Extrae el nombre base para agrupar similares: "u-13 negro" → "u-13", "u-13 [negro]" → "u-13" */
    private fun nombreBase(nombre: String): String {
        val lower = nombre.lowercase().trim()
        // Strip bracket suffix e.g. "u-13 [negro]"
        val sinBracket = lower.replace(Regex("\\s*\\[[^\\]]*\\]$"), "").trim()
        val colores = listOf(
            "negro", "blanco", "plateado", "bronce", "natural", "madera",
            "champagne", "gris", "dorado", "mate", "brillante", "anodizado"
        )
        for (color in colores) {
            if (sinBracket.endsWith(" $color")) {
                return sinBracket.removeSuffix(" $color")
            }
        }
        return sinBracket
    }

    private fun extraerCampoMat(paquete: String, campo: String): String {
        val matMatch = Regex("-MAT<([^>]*)>").find(paquete) ?: return ""
        val matContent = matMatch.groupValues[1]
        return matContent.split(";")
            .find { it.startsWith("$campo:", ignoreCase = true) }
            ?.substringAfter(":")
            ?.replace("_", " ")
            ?.trim()
            ?.takeIf { it != "null" } ?: ""
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
            // Texto clicable con el color: al tocarlo muestra el orden de módulos por tramo.
            val tvOrdenModulos: TextView = view.findViewById(R.id.tvOrdenModulos)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentanaViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ventana, parent, false)
            return VentanaViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: VentanaViewHolder, position: Int) {
            val ventana = ventanas.keys.toList()[position]
            val listas = ventanas[ventana]

            // Tipo de producto (según sus listas) + número + proyecto activo (nunca el cliente).
            // Primer token del id: descarta ", cliente" (puerta) o " proyecto" (nova).
            val numeroProd = ventana.split(',', ' ').first().trim()
            val proyectoActivo = crystal.crystal.casilla.ProyectoManager.getProyectoActivo().orEmpty()
            val serieAl = listas?.firstOrNull { it.first == "DisenoVentanaAl" }
                ?.second?.firstOrNull()?.first
                ?.let { crystal.crystal.taller.venAl.VentanaAlDescriptor.parsear(it)?.serie }
            val tipo = when {
                listas?.any { it.first == "DisenoPuerta" } == true -> "Puerta"
                serieAl != null -> "Ventana Aluminio - $serieAl"
                listas?.any { it.first == "DisenoPaquete" } == true -> "Ventana Nova"
                listas?.any { it.first == crystal.crystal.taller.VitrovenDescriptor.CLAVE } == true -> "Vitroventana"
                listas?.any { it.first == crystal.crystal.taller.melamina.RoperoActivity.CLAVE_DISENO } == true -> "Ropero Melamina"
                else -> "Producto"
            }
            holder.ventanaTextView.text =
                if (proyectoActivo.isNotBlank()) "$tipo: $numeroProd, $proyectoActivo"
                else "$tipo: $numeroProd"

            // Limpiar los TableLayouts para evitar duplicaciones
            holder.materialesTly.removeAllViews()
            holder.referenciasTly.removeAllViews()
            // Resetear el diseño: sin esto, el ViewHolder reciclado arrastra la imagen/el texto del
            // item anterior (p. ej. una puerta) cuando este item no fija su propio diseño.
            holder.disenoImageView.setImageDrawable(null)
            holder.disenoImageView.scaleY = 1f
            holder.drawableNameTextView.text = ""

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
                    it.first == "DisenoPuerta" -> 4
                    it.first == "DisenoMampara" -> 4
                    it.first == crystal.crystal.taller.VitrovenDescriptor.CLAVE -> 4
                    it.first == crystal.crystal.taller.melamina.RoperoActivity.CLAVE_DISENO -> 4
                    it.first == "DisenoVentanaAl" -> 4
                    it.first == "DisenoSimbolicoV2" -> 5
                    it.first == "Diseño" -> 6
                    it.first == "Pedido" -> 7
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
                } else if (nombreLista == "DisenoPuerta") {
                    // Puerta: regenerar el gráfico desde su descriptor (sin imagen guardada).
                    datos.forEach { (desc, _) ->
                        val d = crystal.crystal.taller.puerta.PuertaDescriptor.parsear(desc.trim())
                        if (d != null) {
                            val bmp = crystal.crystal.taller.puerta.PuertaRender.dibujar(
                                context, d, d.ancho * 4f, d.alto * 4f
                            )
                            if (bmp != null) {
                                holder.disenoImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                                holder.disenoImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                                holder.disenoImageView.setImageBitmap(bmp)
                                holder.drawableNameTextView.text = "${d.modelo} ${d.variante}".trim()
                                tieneDisenoPaquete = true
                            }
                        }
                    }
                } else if (nombreLista == "DisenoVentanaAl") {
                    // Ventana de aluminio: regenerar el diseño desde su descriptor.
                    datos.forEach { (desc, _) ->
                        val d = crystal.crystal.taller.venAl.VentanaAlDescriptor.parsear(desc.trim())
                        if (d != null) {
                            val bmp = crystal.crystal.taller.venAl.VentanaAlRender.dibujar(context, d)
                            if (bmp != null) {
                                holder.disenoImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                                holder.disenoImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                                holder.disenoImageView.setImageBitmap(bmp)
                                holder.disenoImageView.scaleY = 1f
                                holder.drawableNameTextView.text = d.serie
                                tieneDisenoPaquete = true
                            }
                        }
                    }
                } else if (nombreLista == "DisenoMampara") {
                    // Mampara: regenerar el dibujo desde el descriptor simbólico. Para archivados
                    // antiguos (cuando se guardaba la ruta de un PNG) se decodifica ese archivo.
                    datos.forEach { (valor, _) ->
                        val v = valor.trim()
                        val desc = crystal.crystal.taller.mamparas.MamparaPaflonDescriptor.parsear(v)
                        val bmp = if (desc != null)
                            crystal.crystal.taller.mamparas.MamparaPaflonRender.dibujar(context, desc)
                        else
                            android.graphics.BitmapFactory.decodeFile(v)
                        if (bmp != null) {
                            holder.disenoImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                            holder.disenoImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                            holder.disenoImageView.setImageBitmap(bmp)
                            holder.drawableNameTextView.text = "Mampara"
                            tieneDisenoPaquete = true
                        }
                    }
                } else if (nombreLista == crystal.crystal.taller.VitrovenDescriptor.CLAVE) {
                    // Vitroventana: regenerar el dibujo desde su descriptor (sin imagen guardada),
                    // igual que puerta y mampara.
                    datos.forEach { (valor, _) ->
                        val d = crystal.crystal.taller.VitrovenDescriptor.parsear(valor.trim())
                        if (d != null) {
                            runCatching {
                                val dm = context.resources.displayMetrics
                                val w = dm.widthPixels.coerceAtLeast(720)
                                val h = (w * 0.75f).toInt().coerceAtLeast(480)
                                val bmp = crystal.crystal.Diseno.vitroven.RenderVitroven().renderizarBitmap(
                                    w, h,
                                    crystal.crystal.Diseno.vitroven.ParametrosVitroven(
                                        anchoTotalCm = d.ancho,
                                        altoTotalCm = d.alto,
                                        clips = d.clips,
                                        clasificacion = d.clasificacion,
                                        disenoSimbolico = d.simbolico,
                                        direccionVertical = d.direccionVertical
                                    )
                                )
                                holder.disenoImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                                holder.disenoImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                                holder.disenoImageView.setImageBitmap(bmp)
                                holder.disenoImageView.scaleY = if (grados == 180) -1f else 1f
                                holder.drawableNameTextView.text = "Vitroventana"
                                tieneDisenoPaquete = true
                            }.onFailure {
                                holder.drawableNameTextView.text = "Error diseño: ${it.message}"
                            }
                        }
                    }
                } else if (nombreLista == crystal.crystal.taller.melamina.RoperoActivity.CLAVE_DISENO) {
                    // Ropero de melamina: el dibujo se rehace desde su paquete con la misma vista
                    // de la calculadora, de frente y con lo que lleva cada cuerpo.
                    datos.forEach { (valor, _) ->
                        val ropero = crystal.crystal.taller.melamina.Ropero.desdeJson(valor.trim()) ?: return@forEach
                        runCatching {
                            val dm = context.resources.displayMetrics
                            val w = dm.widthPixels.coerceAtLeast(720)
                            val h = (w * 0.8f).toInt().coerceAtLeast(480)
                            val vista = crystal.crystal.taller.melamina.VistaRopero(context)
                            vista.measure(
                                View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY)
                            )
                            vista.layout(0, 0, w, h)
                            vista.ropero = ropero
                            val bmp = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
                            vista.draw(android.graphics.Canvas(bmp))
                            holder.disenoImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                            holder.disenoImageView.scaleType = ImageView.ScaleType.FIT_CENTER
                            holder.disenoImageView.setImageBitmap(bmp)
                            holder.drawableNameTextView.text = "Ropero de melamina"
                            tieneDisenoPaquete = true
                        }.onFailure {
                            holder.drawableNameTextView.text = "Error diseño: ${it.message}"
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
                } else if (nombreLista == "DisenoSimbolicoV2") {
                    // No se muestra crudo aquí; sus metadatos van en "Descripción" (abajo).
                } else if (nombreLista == "Color aluminio" || nombreLista == "Tipo vidrio") {
                    // No se muestran como fila de material; van en "Descripción".
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

            // Descripción: metadatos de producción (color de aluminio, vidrio, acabado, observaciones)
            // que se ingresan antes de archivar. Se colocan debajo de Referencias.
            val paqueteV2 = listas?.firstOrNull { it.first == "DisenoSimbolicoV2" }
                ?.second?.firstOrNull()?.first?.trim().orEmpty()
            // VentanaAl guarda color/vidrio como listas propias; se usan como respaldo del formato Nova.
            fun primerDato(nombre: String) = listas?.firstOrNull { it.first == nombre }
                ?.second?.firstOrNull()?.first?.trim().orEmpty()
            val campos = listOf(
                "Aluminio" to extraerMat(paqueteV2, "alu").ifBlank { primerDato("Color aluminio") },
                "Vidrio" to extraerMat(paqueteV2, "vid").ifBlank { primerDato("Tipo vidrio") },
                "Acabado" to extraerMat(paqueteV2, "acabado_sup"),
                "Observaciones" to extraerMat(paqueteV2, "obs")
            ).filter { it.second.isNotBlank() }
            if (campos.isNotEmpty()) {
                val rowHeader = TableRow(context)
                rowHeader.addView(TextView(context).apply {
                    text = "Descripción"
                    setTypeface(null, Typeface.BOLD)
                    setPadding(0, 16, 0, 4)
                    layoutParams = TRLayoutParams(0, TRLayoutParams.WRAP_CONTENT, 1f)
                })
                holder.referenciasTly.addView(rowHeader)
                for ((etq, valor) in campos) {
                    val row = TableRow(context)
                    row.addView(TextView(context).apply {
                        text = "$etq: $valor"
                        layoutParams = TRLayoutParams(0, TRLayoutParams.WRAP_CONTENT, 1f)
                    })
                    holder.referenciasTly.addView(row)
                }
            }

            // Texto clicable (color) que muestra el orden de módulos por tramo. Solo para ventanas de
            // aluminio con modulación (cualquier serie excepto 84); en el resto se oculta.
            val descVAl = listas?.firstOrNull { it.first == "DisenoVentanaAl" }
                ?.second?.firstOrNull()?.first
            val dVAl = descVAl?.let { crystal.crystal.taller.venAl.VentanaAlDescriptor.parsear(it.trim()) }
            val patronesTramo = dVAl?.let { crystal.crystal.taller.venAl.VentanaAlRender.patronesPorTramo(it) }.orEmpty()
            if (dVAl != null && patronesTramo.isNotEmpty()) {
                val color = listas?.firstOrNull { it.first == "Color aluminio" }
                    ?.second?.firstOrNull()?.first?.trim().orEmpty()
                holder.tvOrdenModulos.text =
                    if (color.isNotBlank()) "$color — ver orden de módulos" else "Ver orden de módulos"
                holder.tvOrdenModulos.visibility = View.VISIBLE
                holder.tvOrdenModulos.setOnClickListener { mostrarOrdenTramos(context, dVAl) }
            } else {
                holder.tvOrdenModulos.visibility = View.GONE
                holder.tvOrdenModulos.setOnClickListener(null)
            }
        }

        // Diálogo con el orden de colocación de módulos por tramo (una imagen por tramo, etiquetada).
        private fun mostrarOrdenTramos(context: android.content.Context, d: crystal.crystal.taller.venAl.VentanaAlDescriptor) {
            val patrones = crystal.crystal.taller.venAl.VentanaAlRender.patronesPorTramo(d)
            if (patrones.isEmpty()) return
            val dens = context.resources.displayMetrics.density
            val pad = (12 * dens).toInt()
            val box = android.widget.LinearLayout(context).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(pad, pad, pad, pad)
            }
            patrones.forEachIndexed { i, pat ->
                box.addView(TextView(context).apply {
                    text = if (patrones.size > 1) "Tramo ${i + 1}: $pat" else "Modulación: $pat"
                    setTypeface(typeface, Typeface.BOLD)
                    setPadding(0, (8 * dens).toInt(), 0, (2 * dens).toInt())
                })
                val resId = crystal.crystal.taller.venAl.VentanaAlRender.imagenOrdenModulos(context, d.serie, pat)
                if (resId != 0) box.addView(ImageView(context).apply {
                    setImageResource(resId); adjustViewBounds = true
                }) else box.addView(TextView(context).apply { text = "(sin imagen para $pat)" })
            }
            androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Orden de módulos — ${d.serie}")
                .setView(android.widget.ScrollView(context).apply { addView(box) })
                .setPositiveButton("Cerrar", null)
                .show()
        }

        // Extrae un campo de los metadatos de producción "-MAT<alu:..;vid:..;...>" del paquete.
        private fun extraerMat(paquete: String, campo: String): String {
            if (paquete.isBlank()) return ""
            val m = Regex("-MAT<([^>]*)>").find(paquete) ?: return ""
            return m.groupValues[1].split(";")
                .find { it.startsWith("$campo:", ignoreCase = true) }
                ?.substringAfter(":")?.replace("_", " ")?.trim()
                ?.takeIf { it != "null" } ?: ""
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

    data class PlanoItem(
        val datos: crystal.crystal.taller.puerta.PuertaDescriptor.Datos,
        val num: String, val alu: String, val vid: String
    )

    // Galería de planos de puertas archivadas: regenera cada plano (con cotas) desde su descriptor.
    class PlanoAdapter(
        private val items: List<PlanoItem>,
        private val context: Context
    ) : RecyclerView.Adapter<PlanoAdapter.VH>() {

        class VH(view: View) : RecyclerView.ViewHolder(view) {
            val titulo: TextView = view.findViewById(R.id.tvTituloPlano)
            val datos: TextView = view.findViewById(R.id.tvDatosPlano)
            val iv: ImageView = view.findViewById(R.id.ivPlano)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = android.view.LayoutInflater.from(context).inflate(R.layout.item_plano, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            // Texto a tamaño fijo (XML); el plano (sin cabecera) se adecúa al espacio de la imagen.
            holder.titulo.text = crystal.crystal.taller.puerta.PuertaRender.tituloPlano(item.datos, item.num)
            holder.datos.text = crystal.crystal.taller.puerta.PuertaRender.datosPlano(item.datos, item.alu, item.vid)
            val plano = crystal.crystal.taller.puerta.PuertaRender.dibujarPlano(
                context, item.datos, item.num, item.alu, item.vid, cabecera = false
            )
            holder.iv.setImageBitmap(plano?.let { b -> escalar(b, 1400) })
            holder.iv.setOnClickListener {
                // A pantalla completa: con cabecera incluida en el bitmap.
                val bmp = crystal.crystal.taller.puerta.PuertaRender.dibujarPlano(
                    context, item.datos, item.num, item.alu, item.vid, cabecera = true
                ) ?: return@setOnClickListener
                crystal.crystal.taller.puerta.dibujo.DibujoPuerta.guardarPlanoEnCache(context, bmp)
                context.startActivity(android.content.Intent(context, crystal.crystal.Diseno.DisenoActivity::class.java).apply {
                    putExtra(crystal.crystal.Diseno.DisenoActivity.EXTRA_PLANO, true)
                    putExtra(crystal.crystal.Diseno.DisenoActivity.EXTRA_PLANO_TITULO, "${item.datos.variante.ifBlank { item.datos.modelo }} ${item.num}".trim())
                })
            }
        }

        override fun getItemCount() = items.size

        private fun escalar(bm: android.graphics.Bitmap, maxDim: Int): android.graphics.Bitmap {
            val s = maxDim.toFloat() / maxOf(bm.width, bm.height)
            if (s >= 1f) return bm
            return android.graphics.Bitmap.createScaledBitmap(bm, (bm.width * s).toInt(), (bm.height * s).toInt(), true)
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



