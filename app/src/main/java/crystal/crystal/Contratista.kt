package crystal.crystal

import android.content.Context
import com.google.gson.Gson

/** Un perfil de contratista (p. ej. Personal, Empresa) usado en los contratos/recibos. */
data class DatosContratista(
    val id: String,
    var etiqueta: String,
    var nombre: String,
    var documento: String,
    var direccion: String,
    var telefono: String,
    var predeterminado: Boolean = false
)

/** Guarda varios perfiles de contratista en SharedPreferences (ContratoPrefs). */
object ContratistaStore {
    private const val PREFS = "ContratoPrefs"
    private const val KEY = "perfiles_contratista"
    private val gson = Gson()

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun listar(context: Context): MutableList<DatosContratista> {
        val json = prefs(context).getString(KEY, null) ?: return mutableListOf()
        return runCatching {
            gson.fromJson(json, Array<DatosContratista>::class.java).toMutableList()
        }.getOrDefault(mutableListOf())
    }

    private fun guardarLista(context: Context, lista: List<DatosContratista>) {
        prefs(context).edit().putString(KEY, gson.toJson(lista)).apply()
    }

    fun guardar(context: Context, d: DatosContratista) {
        val lista = listar(context)
        val i = lista.indexOfFirst { it.id == d.id }
        if (i >= 0) lista[i] = d else lista.add(d)
        if (lista.size == 1) lista[0].predeterminado = true   // el primero queda predeterminado
        guardarLista(context, lista)
    }

    fun eliminar(context: Context, id: String) {
        val lista = listar(context).filter { it.id != id }.toMutableList()
        if (lista.isNotEmpty() && lista.none { it.predeterminado }) lista[0].predeterminado = true
        guardarLista(context, lista)
    }

    fun establecerPredeterminado(context: Context, id: String) {
        val lista = listar(context)
        lista.forEach { it.predeterminado = (it.id == id) }
        guardarLista(context, lista)
    }

    fun predeterminado(context: Context): DatosContratista? {
        val lista = listar(context)
        return lista.firstOrNull { it.predeterminado } ?: lista.firstOrNull()
    }

    fun nuevoId(): String = System.currentTimeMillis().toString()
}
