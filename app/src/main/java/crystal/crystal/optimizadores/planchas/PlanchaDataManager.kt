package crystal.crystal.optimizadores.planchas

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlanchaDataManager(context: Context) {

    private val prefs = context.getSharedPreferences("PlanchaPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun guardarDatos(lista: List<ItemListaPlanchas>, lista2: List<PlanchaDisponible>) {
        prefs.edit()
            .putString("lista_items", gson.toJson(lista))
            .putString("lista_planchas", gson.toJson(lista2))
            .apply()
    }

    fun recuperarDatos(): Pair<MutableList<ItemListaPlanchas>, MutableList<PlanchaDisponible>> {
        val tipoItems = object : TypeToken<MutableList<ItemListaPlanchas>>() {}.type
        val tipoPlanchas = object : TypeToken<MutableList<PlanchaDisponible>>() {}.type
        val items: MutableList<ItemListaPlanchas> =
            prefs.getString("lista_items", null)?.let { gson.fromJson(it, tipoItems) }
                ?: mutableListOf()
        val planchas: MutableList<PlanchaDisponible> =
            prefs.getString("lista_planchas", null)?.let { gson.fromJson(it, tipoPlanchas) }
                ?: mutableListOf()
        return items to planchas
    }

    fun guardarItems(lista: List<ItemListaPlanchas>) {
        prefs.edit().putString("lista_items", gson.toJson(lista)).apply()
    }

    fun guardarPlanchasDisp(lista2: List<PlanchaDisponible>) {
        prefs.edit().putString("lista_planchas", gson.toJson(lista2)).apply()
    }

    fun guardarEspesorDisco(valor: Float) {
        prefs.edit().putFloat("espesor_disco_cm", valor).apply()
    }

    fun recuperarEspesorDisco(): Float =
        prefs.getFloat("espesor_disco_cm", 0f)

    fun guardarRestringirRotacion(valor: Boolean) {
        prefs.edit().putBoolean("restringir_rotacion", valor).apply()
    }

    fun recuperarRestringirRotacion(): Boolean =
        prefs.getBoolean("restringir_rotacion", false)

    fun guardarResultado(resultado: ResultadoOptimizacionPlanchas) {
        prefs.edit().putString("resultado_planchas", gson.toJson(resultado)).apply()
    }

    fun recuperarResultado(): ResultadoOptimizacionPlanchas? {
        val json = prefs.getString("resultado_planchas", null) ?: return null
        return try { gson.fromJson(json, ResultadoOptimizacionPlanchas::class.java) } catch (e: Exception) { null }
    }

    fun guardarPiezasEjecutadas(piezas: List<PiezaPlanchaEjecutada>) {
        prefs.edit().putString("piezas_ejecutadas_planchas", gson.toJson(piezas)).apply()
    }

    fun recuperarPiezasEjecutadas(): List<PiezaPlanchaEjecutada> {
        val json = prefs.getString("piezas_ejecutadas_planchas", null) ?: return emptyList()
        val tipo = object : TypeToken<List<PiezaPlanchaEjecutada>>() {}.type
        return try { gson.fromJson(json, tipo) ?: emptyList() } catch (e: Exception) { emptyList() }
    }

    fun limpiarPiezasEjecutadas() {
        prefs.edit().remove("piezas_ejecutadas_planchas").apply()
    }
}
