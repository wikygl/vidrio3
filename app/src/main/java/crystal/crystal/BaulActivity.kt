package crystal.crystal


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import crystal.crystal.databinding.ActivityBaulBinding
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

class BaulActivity : AppCompatActivity() {

    private lateinit var archivosGuardados: MutableList<String>
    companion object { var estaEnModoSeleccion = false}
    private lateinit var adapter: ArchivosAdapter

    private lateinit var binding: ActivityBaulBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaulBinding.inflate(layoutInflater)
        setContentView(binding.root)

        generarLista()
        binding.btEliminar.setOnClickListener { eliminarArchivo() }
        binding.btCompartir.setOnClickListener { compartirArchivos() }

    }

    private fun generarLista() {
        archivosGuardados = obtenerArchivosDat()
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
                val archivoSeleccionado = adapter.getItem(position)
                abrirArchivo(archivoSeleccionado.toString())
            }
        }
    }

    private fun obtenerArchivosDat(): MutableList<String> {
        return File(filesDir.absolutePath).listFiles { file ->
            file.extension == "dat"
        }?.map { it.name }?.toMutableList() ?: mutableListOf()
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

    private fun abrirArchivo(nombreArchivo: String) {
        val archivo = File(filesDir, nombreArchivo)

        if (archivo.exists()) {
            try {
                val input = ObjectInputStream(FileInputStream(archivo))
                val listaRecibida = input.readObject() as MutableList<*>
                input.close()

                // Obtener el nombre del cliente del archivo seleccionado
                val cliente = nombreArchivo.substringAfter("(").substringBefore(")")

                // Enviar el nombre del cliente y la lista recibida a la MainActivity
                val paquete = Bundle().apply {
                    putString("rcliente", cliente)
                    putSerializable("lista", ArrayList(listaRecibida))
                }

                val datosIntent = Intent(this, MainActivity::class.java)
                datosIntent.putExtras(paquete)
                startActivity(datosIntent)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al abrir el archivo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "El archivo no existe", Toast.LENGTH_SHORT).show()
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
            val nombreArchivo = archivosGuardados[index]
            val archivo = File(filesDir, nombreArchivo)
            archivo.delete()
            archivosGuardados.removeAt(index)
        }

        // Actualizar la vista del ListView
        adapter.notifyDataSetChanged()

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
                val nombreArchivo = archivosGuardados[indicesSeleccionados.first()]
                val archivo = File(filesDir, nombreArchivo)
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
    }
}















