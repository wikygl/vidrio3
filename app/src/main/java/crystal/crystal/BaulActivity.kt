package crystal.crystal


import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import crystal.crystal.Diseno.DisenoActivity
import crystal.crystal.baul.MedicionProyectoBaulPayload
import crystal.crystal.databinding.ActivityBaulBinding
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

class BaulActivity : AppCompatActivity() {

    private lateinit var archivosGuardados: MutableList<String>
    private val archivosTodos = mutableListOf<ArchivoBaul>()
    private val archivosFiltrados = mutableListOf<ArchivoBaul>()
    private var filtroActual = FiltroBaul.PROFORMAS
    companion object { var estaEnModoSeleccion = false}
    private lateinit var adapter: ArchivosAdapter

    private lateinit var binding: ActivityBaulBinding

    private data class ArchivoBaul(
        val nombre: String,
        val file: File,
        val filtro: FiltroBaul
    )

    private enum class FiltroBaul {
        PROFORMAS, CONTRATOS, OTROS, MEDIDAS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaulBinding.inflate(layoutInflater)
        setContentView(binding.root)

        generarLista()
        binding.btEliminar.setOnClickListener { eliminarArchivo() }
        binding.btCompartir.setOnClickListener { compartirArchivos() }
        binding.btProf.setOnClickListener { cambiarFiltro(FiltroBaul.PROFORMAS) }
        binding.btCont.setOnClickListener { cambiarFiltro(FiltroBaul.CONTRATOS) }
        binding.btOtro.setOnClickListener { cambiarFiltro(FiltroBaul.OTROS) }
        binding.btMedi.setOnClickListener { cambiarFiltro(FiltroBaul.MEDIDAS) }

    }

    private fun generarLista() {
        archivosTodos.clear()
        archivosTodos.addAll(obtenerArchivos())
        aplicarFiltro()
        adapter = ArchivosAdapter(this, R.layout.lista_check, archivosGuardados)

        val listView = findViewById<ListView>(R.id.list_view)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, _, _ ->
            alternarModoSeleccion(adapter)
            true
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            if (estaEnModoSeleccion) {
                actualizarEstadoCheckmark(adapter, position)
            } else {
                val archivoSeleccionado = archivosFiltrados.getOrNull(position) ?: return@setOnItemClickListener
                abrirArchivo(archivoSeleccionado)
            }
        }
    }

    private fun obtenerArchivos(): MutableList<ArchivoBaul> {
        val out = mutableListOf<ArchivoBaul>()
        val internos = File(filesDir.absolutePath).listFiles { file ->
            file.extension == "dat"
        }?.toList().orEmpty()
        internos.forEach { f ->
            val nombre = f.name
            val filtro = when {
                nombre.contains("contrato", ignoreCase = true) -> FiltroBaul.CONTRATOS
                nombre.contains("medida", ignoreCase = true) -> FiltroBaul.MEDIDAS
                // Todo .dat legacy cae en Proformas para mantener comportamiento histórico.
                else -> FiltroBaul.PROFORMAS
            }
            out += ArchivoBaul(nombre = nombre, file = f, filtro = filtro)
        }

        val medidasDir = File(
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Crystal"
            ),
            "Medidas"
        )
        val externos = medidasDir.listFiles { f ->
            f.isFile && (f.extension.equals("json", true) || f.extension.equals("txt", true))
        }?.toList().orEmpty()
        externos.forEach { f ->
            out += ArchivoBaul(nombre = f.name, file = f, filtro = FiltroBaul.MEDIDAS)
        }
        return out
    }

    private fun cambiarFiltro(nuevoFiltro: FiltroBaul) {
        filtroActual = nuevoFiltro
        aplicarFiltro()
        adapter.setData(archivosGuardados)
        if (estaEnModoSeleccion) {
            estaEnModoSeleccion = false
            binding.lyCom.visibility = View.GONE
        }
    }

    private fun aplicarFiltro() {
        archivosFiltrados.clear()
        archivosFiltrados.addAll(archivosTodos.filter { it.filtro == filtroActual })
        archivosGuardados = archivosFiltrados.map { it.nombre }.toMutableList()
    }

    private fun alternarModoSeleccion(adapter: ArchivosAdapter) {
        if (!estaEnModoSeleccion) {
            // Modo de selección: Mostrar el checkmark en todos los elementos
            for (i in 0 until adapter.count) {
                adapter.setSeleccionado(i, true)
            }
            adapter.notifyDataSetChanged()

            // Actualizar el estado y mostrar el layout
            estaEnModoSeleccion = true
            binding.lyCom.visibility = View.VISIBLE
        } else {
            // Salir del modo de selección: Restaurar el estado anterior
            for (i in 0 until adapter.count) {
                adapter.setSeleccionado(i, false)
            }
            adapter.notifyDataSetChanged()

            // Actualizar el estado y ocultar el layout
            estaEnModoSeleccion = false
            binding.lyCom.visibility = View.GONE
        }
    }

    private fun actualizarEstadoCheckmark(adapter: ArchivosAdapter, position: Int) {
        adapter.setSeleccionado(position, !adapter.estaItemSeleccionado(position))
    }

    private fun abrirArchivo(archivoSeleccionado: ArchivoBaul) {
        val archivo = archivoSeleccionado.file

        if (archivo.exists()) {
            try {
                if (archivo.extension.equals("json", true) || archivo.extension.equals("txt", true)) {
                    if (intent.getBooleanExtra("desde_taller", false)) {
                        val contenido = leerContenidoArchivoSeguro(archivo)
                        val resultIntent = Intent().apply {
                            putExtra("medicion_proyecto_nombre", archivo.nameWithoutExtension)
                            putExtra("medicion_proyecto_archivo", archivo.name)
                            putExtra("medicion_proyecto_ruta", archivo.absolutePath)
                            putExtra("medicion_proyecto_cliente", "")
                            putExtra("medicion_proyecto_contenido", contenido)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                        return
                    }
                    val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", archivo)
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, if (archivo.extension.equals("json", true)) "application/json" else "text/plain")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(intent, "Abrir archivo"))
                    return
                }

                val input = ObjectInputStream(FileInputStream(archivo))
                val dataLeida = input.readObject()
                input.close()

                if (dataLeida is MedicionProyectoBaulPayload) {
                    if (intent.getBooleanExtra("desde_taller", false)) {
                        val resultIntent = Intent().apply {
                            putExtra("medicion_proyecto_json", dataLeida.itemsJson)
                            putExtra("medicion_proyecto_nombre", dataLeida.nombreProyecto)
                            putExtra("medicion_proyecto_cliente", dataLeida.clienteNombre)
                            putExtra("medicion_proyecto_archivo", archivo.name)
                            putExtra("medicion_proyecto_ruta", archivo.absolutePath)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                        return
                    }
                    val disenoIntent = Intent(this, DisenoActivity::class.java).apply {
                        putExtra("medicion_proyecto_json", dataLeida.itemsJson)
                        putExtra("medicion_proyecto_nombre", dataLeida.nombreProyecto)
                        putExtra("medicion_proyecto_cliente", dataLeida.clienteNombre)
                    }
                    startActivity(disenoIntent)
                    return
                }

                val listaRecibida = dataLeida as MutableList<*>
                val cliente = archivo.name.substringAfter("(").substringBefore(")").replace("\n", "").trim()

                val paquete = Bundle().apply {
                    putString("rcliente", cliente)
                    putSerializable("lista", ArrayList(listaRecibida))
                }

                // Si viene desde Taller, devolver datos con setResult
                if (intent.getBooleanExtra("desde_taller", false)) {
                    val resultIntent = Intent()
                    resultIntent.putExtras(paquete)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                    return
                }

                // Flujo normal: abrir MainActivity
                val datosIntent = Intent(this, MainActivity::class.java)
                datosIntent.putExtras(paquete)
                startActivity(datosIntent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al abrir el archivo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "El archivo no existe", Toast.LENGTH_SHORT).show()
        }
    }

    private fun leerContenidoArchivoSeguro(archivo: File): String {
        return try {
            val texto = archivo.readText()
            if (texto.length > 20000) {
                texto.take(20000) + "\n\n...[contenido truncado]"
            } else {
                texto
            }
        } catch (e: Exception) {
            "[No se pudo leer contenido: ${e.message}]"
        }
    }

    private fun eliminarArchivo() {
        val indicesSeleccionados = mutableListOf<Int>()
        // Obtener los índices de los elementos que tienen el checkmarkr visible
        for (i in 0 until archivosGuardados.size) {
            if (!adapter.estaItemSeleccionado(i)) {
                indicesSeleccionados.add(i)
            }
        }

        // Eliminar los archivos de la lista de archivosGuardados
        for (index in indicesSeleccionados.reversed()) {
            val item = archivosFiltrados[index]
            val archivo = item.file
            archivo.delete()
            archivosTodos.remove(item)
            archivosGuardados.removeAt(index)
        }
        archivosFiltrados.clear()
        archivosFiltrados.addAll(archivosTodos.filter { it.filtro == filtroActual })

        // Actualizar la vista del ListView
        adapter.setData(archivosGuardados)

        // Salir del modo de selección y ocultar el layout
        estaEnModoSeleccion = false
        binding.lyCom.visibility = View.GONE
    }

    private fun compartirArchivos() {
        val indicesSeleccionados = mutableListOf<Int>()
        // Obtener los índices de los elementos que tienen el checkmarkr visible
        for (i in 0 until archivosGuardados.size) {
            if (!adapter.estaItemSeleccionado(i)) {
                indicesSeleccionados.add(i)
            }
        }

        if (indicesSeleccionados.size == 1) {
            try {
                val archivo = archivosFiltrados[indicesSeleccionados.first()].file
                if (archivo.exists()) {
                    val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", archivo)

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/*"
                        putExtra(Intent.EXTRA_STREAM, uri)
                    }

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(Intent.createChooser(intent, "Compartir archivo"))
                } else {
                    Toast.makeText(this, "El archivo no existe", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error al compartir el archivo", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Por favor, seleccione solo un archivo para compartir", Toast.LENGTH_SHORT).show()
        }
    }

    private class ArchivosAdapter(context: Context, resource: Int, objects: List<String>) :
        ArrayAdapter<String>(context, resource, objects) {

        private val itemsSeleccionados = mutableListOf<Boolean>()
        init {
            // Inicializar la lista de elementos marcados como `false`
            for (i in objects.indices) {
                itemsSeleccionados.add(estaEnModoSeleccion)
            }
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.lista_check,
                parent, false)

            val checkmark = view.findViewById<ImageView>(R.id.checkmark)
            val checkmarkr = view.findViewById<ImageView>(R.id.checkmarkr)
            val itemText = view.findViewById<TextView>(R.id.item_text)

            // Obtener el estado del checkmark para la posición actual
            val estaSeleccionado = estaItemSeleccionado(position)

            // Establecer la visibilidad de los checkmarks según el estado actual
            if (!estaEnModoSeleccion) {
                checkmark.visibility = View.GONE
                checkmarkr.visibility = View.GONE
            } else {
                if (estaSeleccionado) {
                    checkmarkr.visibility = View.GONE
                    checkmark.visibility = View.VISIBLE
                } else {
                    checkmarkr.visibility = View.VISIBLE
                    checkmark.visibility = View.GONE
                }
            }
            // Establecer el texto del elemento
            itemText.text = getItem(position)

            return view
        }

        fun setSeleccionado(position: Int, seleccionado: Boolean) {
            itemsSeleccionados[position] = seleccionado
            notifyDataSetChanged()
        }

        fun estaItemSeleccionado(position: Int): Boolean {
            return itemsSeleccionados[position]
        }

        fun setData(nuevos: List<String>) {
            clear()
            addAll(nuevos)
            itemsSeleccionados.clear()
            for (i in nuevos.indices) {
                itemsSeleccionados.add(estaEnModoSeleccion)
            }
            notifyDataSetChanged()
        }
    }
}








