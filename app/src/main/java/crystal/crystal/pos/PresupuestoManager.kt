package crystal.crystal.pos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.gson.Gson
import crystal.crystal.Listado
import crystal.crystal.PresupuestoCompleto
import crystal.crystal.databinding.ActivityMainBinding
import crystal.crystal.red.ListChatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PresupuestoManager(
    private val activity: AppCompatActivity,
    private val binding: ActivityMainBinding,
    private val sharedPreferences: SharedPreferences,
    private val lista: MutableList<Listado>
) {

    companion object {
        const val RECEIVE_PRESUPUESTO_REQUEST = 3
    }

    var onListaModificada: (() -> Unit)? = null
    var obtenerCurrentUserId: (() -> String)? = null
    var edicionMasivaManager: EdicionMasivaManager? = null

    fun mostrarMenuPresupuesto() {
        val opciones = if (lista.isEmpty()) {
            arrayOf("Cargar presupuesto desde archivo")
        } else {
            arrayOf(
                "Enviar por chat",
                "🔧 Edición masiva",
                "Cargar presupuesto desde archivo",
                "Guardar como archivo JSON",
                "Compartir como archivo"
            )
        }

        val builder = AlertDialog.Builder(activity)
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
                    edicionMasivaManager?.mostrarMenuEdicionMasiva()
                }
                2 -> abrirSelectorPresupuesto()
                3 -> if (lista.isNotEmpty()) guardarComoJSON()
                4 -> if (lista.isNotEmpty()) compartirPresupuesto()
            }
        }

        builder.show()
    }

    private fun crearPresupuestoCompleto(): PresupuestoCompleto {
        val cliente = binding.clienteEditxt.text.toString().takeIf { it.isNotEmpty() } ?: "Sin nombre"
        return PresupuestoCompleto(
            cliente = cliente,
            fechaCreacion = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            elementos = lista.toList(),
            precioTotal = binding.precioTotal.text.toString(),
            metrosTotal = binding.metrosTotal.text.toString(),
            piesTotal = binding.piesTotal.text.toString(),
            perimetroTotal = binding.per.text.toString()
        )
    }

    fun guardarComoJSON() {
        val presupuesto = crearPresupuestoCompleto()
        val gson = Gson()
        val jsonString = gson.toJson(presupuesto)

        val fileName = "presupuesto_${presupuesto.cliente}_${System.currentTimeMillis()}.json"

        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val crystalDir = File(downloadsDir, "Crystal")
        val presupuestosDir = File(crystalDir, "PresupuestosJ")

        if (!presupuestosDir.exists()) {
            presupuestosDir.mkdirs()
        }

        val file = File(presupuestosDir, fileName)

        try {
            file.writeText(jsonString)
            Toast.makeText(activity, "✅ Presupuesto guardado en:\nDescargas/Crystal/PresupuestosJ/\n$fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(activity, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun compartirPresupuesto() {
        val presupuesto = crearPresupuestoCompleto()
        val gson = Gson()
        val jsonString = gson.toJson(presupuesto)

        val fileName = "presupuesto_${presupuesto.cliente}_${System.currentTimeMillis()}.json"
        val file = File(activity.cacheDir, fileName)

        try {
            file.writeText(jsonString)
            val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Presupuesto - ${presupuesto.cliente}")
                putExtra(Intent.EXTRA_TEXT, "Presupuesto generado desde Crystal App")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            activity.startActivity(Intent.createChooser(shareIntent, "Compartir presupuesto"))
        } catch (e: Exception) {
            Toast.makeText(activity, "Error al compartir: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun cargarPresupuestoDesdeJson(jsonString: String): Boolean {
        return try {
            val gson = Gson()
            val presupuesto = gson.fromJson(jsonString, PresupuestoCompleto::class.java)

            if (presupuesto.elementos.isEmpty()) {
                Toast.makeText(activity, "El presupuesto no contiene elementos válidos", Toast.LENGTH_SHORT).show()
                return false
            }

            val builder = AlertDialog.Builder(activity)
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
            Toast.makeText(activity, "❌ Error al leer presupuesto: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }

    @SuppressLint("NewApi", "SetTextI18n")
    private fun mostrarOpcionesCargar(presupuesto: PresupuestoCompleto) {
        val builder = AlertDialog.Builder(activity)
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

            sharedPreferences.edit()
                .putString("cliente", nuevoCliente)
                .putString("tvpCliente", "Presupuesto de $nuevoCliente")
                .apply()

            binding.lyCuello.visibility = View.GONE
            binding.lyCuerpo.visibility = View.VISIBLE

            onListaModificada?.invoke()
            Toast.makeText(activity, "✅ Presupuesto sumado correctamente", Toast.LENGTH_SHORT).show()
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

        sharedPreferences.edit()
            .putString("cliente", presupuesto.cliente)
            .putString("tvpCliente", "Presupuesto de ${presupuesto.cliente}")
            .apply()

        binding.lyCuello.visibility = View.GONE
        binding.lyCuerpo.visibility = View.VISIBLE

        onListaModificada?.invoke()
        Toast.makeText(activity, "✅ Presupuesto cargado correctamente", Toast.LENGTH_SHORT).show()
    }

    fun enviarPresupuestoPorChat() {
        if (lista.isEmpty()) {
            Toast.makeText(activity, "No hay elementos en el presupuesto para enviar", Toast.LENGTH_SHORT).show()
            return
        }

        val presupuesto = crearPresupuestoCompleto()
        val gson = Gson()
        val jsonPresupuesto = gson.toJson(presupuesto)

        val fileName = "presupuesto_${presupuesto.cliente}_${System.currentTimeMillis()}.json"
        val file = File(activity.cacheDir, fileName)

        try {
            file.writeText(jsonPresupuesto)
            val uri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                file
            )

            val intent = Intent(activity, ListChatActivity::class.java)
            intent.putExtra("usuario", obtenerCurrentUserId?.invoke() ?: "")
            intent.putExtra("enviar_presupuesto", uri.toString())
            intent.putExtra("nombre_presupuesto", fileName)
            activity.startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(activity, "Error al preparar presupuesto: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun manejarArchivoPresupuesto(uri: Uri) {
        try {
            val inputStream = activity.contentResolver.openInputStream(uri)
            val jsonString = inputStream?.bufferedReader().use { it?.readText() }

            if (jsonString != null) {
                cargarPresupuestoDesdeJson(jsonString)
            } else {
                Toast.makeText(activity, "No se pudo leer el archivo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(activity, "Error al abrir archivo: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("DEPRECATION")
    fun abrirSelectorPresupuesto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra("android.provider.extra.INITIAL_URI",
                Uri.parse("content://com.android.externalstorage.documents/document/primary%3ADownload%2FCrystal%2FPresupuestosJ"))
        }
        activity.startActivityForResult(intent, RECEIVE_PRESUPUESTO_REQUEST)
    }

    fun guardar() {
        val cliente = binding.clienteEditxt.text
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        val currentDateAndTime = sdf.format(Date())

        val nombreArchivo = if (binding.tvpCliente.text == "Presupuesto") {
            "Presupuesto ->${"($cliente)"} ${currentDateAndTime}.dat"
        } else {
            "Presupuesto ->${"($cliente)"}\n ${currentDateAndTime}.dat"
        }

        try {
            val fileOutputStream: FileOutputStream = activity.openFileOutput(nombreArchivo, Context.MODE_PRIVATE)
            ObjectOutputStream(fileOutputStream).use { it.writeObject(lista) }
            Toast.makeText(activity, "Archivo guardado con éxito", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(activity, "Error al guardar archivo", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun abrir() {
        val paquete = activity.intent.extras
        val li = paquete?.getSerializable("lista") as? List<*>
        val listaRecibida = li?.filterIsInstance<Listado>()?.toMutableList() ?: mutableListOf()

        if (lista.isNotEmpty()) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirmación")
            builder.setMessage("La lista actual no está vacía. ¿Deseas sumar los datos guardados a la lista existente?")
            builder.setPositiveButton("Sumar") { _, _ ->
                lista.addAll(listaRecibida)
                onListaModificada?.invoke()
            }
            builder.setNegativeButton("Reemplazar") { _, _ ->
                lista.clear()
                lista.addAll(listaRecibida)
                onListaModificada?.invoke()
            }
            builder.setNeutralButton("Cancelar", null)
            builder.show()
        } else {
            lista.addAll(listaRecibida)
            onListaModificada?.invoke()
        }
    }
}
