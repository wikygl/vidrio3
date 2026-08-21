package crystal.crystal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.gson.Gson
import java.io.ByteArrayOutputStream

/** Cliente guardado para reutilizar en contratos/recibos, junto con su firma (opcional). */
data class ClienteContrato(
    val id: String,
    var nombre: String,
    var documento: String,
    var direccion: String,
    var telefono: String,
    var firmaBase64: String = ""   // PNG de la firma en base64, opcional
)

object ClienteContratoStore {
    private const val PREFS = "ContratoPrefs"
    private const val KEY = "clientes_contrato"
    private val gson = Gson()

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun listar(context: Context): MutableList<ClienteContrato> {
        val json = prefs(context).getString(KEY, null) ?: return mutableListOf()
        return runCatching {
            gson.fromJson(json, Array<ClienteContrato>::class.java).toMutableList()
        }.getOrDefault(mutableListOf())
    }

    private fun guardarLista(context: Context, lista: List<ClienteContrato>) {
        prefs(context).edit().putString(KEY, gson.toJson(lista)).apply()
    }

    fun guardar(context: Context, c: ClienteContrato) {
        val lista = listar(context)
        // Actualizar si coincide el documento (no vacío); si no, por id; si no, agregar.
        val porDoc = if (c.documento.isNotBlank())
            lista.indexOfFirst { it.documento.isNotBlank() && it.documento.equals(c.documento, true) } else -1
        val i = if (porDoc >= 0) porDoc else lista.indexOfFirst { it.id == c.id }
        if (i >= 0) lista[i] = c.copy(id = lista[i].id) else lista.add(c)
        guardarLista(context, lista)
    }

    fun eliminar(context: Context, id: String) {
        guardarLista(context, listar(context).filter { it.id != id })
    }

    fun nuevoId(): String = System.currentTimeMillis().toString()

    fun bitmapABase64(bmp: Bitmap?): String {
        if (bmp == null) return ""
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
    }

    fun base64ABitmap(s: String): Bitmap? {
        if (s.isBlank()) return null
        return runCatching {
            val bytes = Base64.decode(s, Base64.NO_WRAP)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }
}
