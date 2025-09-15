package crystal.crystal.optimizadores.corte

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Maneja toda la persistencia de datos en SharedPreferences
 * Extraído del código original de Corte.kt
 */
class CorteDataManager(private val context: Context) {

    private val gson = Gson()
    private val PREFS_NAME = "CortePreferences"

    private val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Guarda las listas de piezas, varillas y resultado
     */
    fun guardarDatos(
        lista: MutableList<PiezaCorte>,
        lista2: MutableList<PiezaCorte>,
        textoResultado: String
    ) {
        with(sharedPref.edit()) {
            // Serializar y guardar 'lista' (piezas requeridas)
            val listaJson = gson.toJson(lista)
            putString("lista_piezas", listaJson)

            // Serializar y guardar 'lista2' (varillas disponibles)
            val lista2Json = gson.toJson(lista2)
            putString("lista_varillas", lista2Json)

            // Guardar el texto de 'tvResultado'
            putString("texto_resultado", textoResultado)

            apply() // Guardar de forma asíncrona
        }
    }

    /**
     * Recupera las listas guardadas
     * Retorna Triple con (lista, lista2, textoResultado)
     */
    fun recuperarDatos(): Triple<MutableList<PiezaCorte>, MutableList<PiezaCorte>, String> {
        var lista = mutableListOf<PiezaCorte>()
        var lista2 = mutableListOf<PiezaCorte>()
        var textoResultado = ""

        // Recuperar y deserializar 'lista' (piezas requeridas)
        val listaJson = sharedPref.getString("lista_piezas", null)
        if (listaJson != null) {
            val type = object : TypeToken<MutableList<PiezaCorte>>() {}.type
            lista = gson.fromJson(listaJson, type)
        }

        // Recuperar y deserializar 'lista2' (varillas disponibles)
        val lista2Json = sharedPref.getString("lista_varillas", null)
        if (lista2Json != null) {
            val type2 = object : TypeToken<MutableList<PiezaCorte>>() {}.type
            lista2 = gson.fromJson(lista2Json, type2)
        }

        // Recuperar el texto de 'tvResultado'
        textoResultado = sharedPref.getString("texto_resultado", "") ?: ""

        return Triple(lista, lista2, textoResultado)
    }

    /**
     * Guarda solo las piezas requeridas (lista)
     */
    fun guardarPiezas(lista: MutableList<PiezaCorte>) {
        val listaJson = gson.toJson(lista)
        sharedPref.edit()
            .putString("lista_piezas", listaJson)
            .apply()
    }

    /**
     * Guarda solo las varillas disponibles (lista2)
     */
    fun guardarVarillas(lista2: MutableList<PiezaCorte>) {
        val lista2Json = gson.toJson(lista2)
        sharedPref.edit()
            .putString("lista_varillas", lista2Json)
            .apply()
    }

    /**
     * Guarda solo el texto resultado
     */
    fun guardarResultado(textoResultado: String) {
        sharedPref.edit()
            .putString("texto_resultado", textoResultado)
            .apply()
    }

    /**
     * Guarda las varillas usadas en la optimización
     */
    fun guardarVarillasUsadas(varillasUsadas: List<CorteOptimizer.VarillaConReferencias>) {
        val varillasJson = gson.toJson(varillasUsadas)
        sharedPref.edit()
            .putString("varillas_usadas", varillasJson)
            .apply()
    }

    /**
     * Recupera las varillas usadas
     */
    fun recuperarVarillasUsadas(): List<CorteOptimizer.VarillaConReferencias> {
        val varillasJson = sharedPref.getString("varillas_usadas", null)
        return if (varillasJson != null) {
            val type = object : TypeToken<List<CorteOptimizer.VarillaConReferencias>>() {}.type
            gson.fromJson(varillasJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * Guarda los estados de las varillas (cortadas o no)
     */
    fun guardarEstadosVarillas(estados: List<Boolean>) {
        val estadosJson = gson.toJson(estados)
        sharedPref.edit()
            .putString("estados_varillas", estadosJson)
            .apply()
    }

    /**
     * Recupera los estados de las varillas
     */
    fun recuperarEstadosVarillas(): List<Boolean> {
        val estadosJson = sharedPref.getString("estados_varillas", null)
        return if (estadosJson != null) {
            val type = object : TypeToken<List<Boolean>>() {}.type
            gson.fromJson(estadosJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    // ===== NUEVAS FUNCIONES PARA CORTES EJECUTADOS =====

    /**
     * Guarda los cortes que fueron ejecutados (varillas cortadas)
     */
    fun guardarCortesEjecutados(cortesEjecutados: List<VarillaCortada>) {
        val cortesJson = gson.toJson(cortesEjecutados)
        sharedPref.edit()
            .putString("cortes_ejecutados", cortesJson)
            .apply()
    }

    /**
     * Recupera los cortes ejecutados
     */
    fun recuperarCortesEjecutados(): List<VarillaCortada> {
        val cortesJson = sharedPref.getString("cortes_ejecutados", null)
        return if (cortesJson != null) {
            val type = object : TypeToken<List<VarillaCortada>>() {}.type
            gson.fromJson(cortesJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * Limpia los cortes ejecutados (usar después de procesarlos)
     */
    fun limpiarCortesEjecutados() {
        sharedPref.edit()
            .remove("cortes_ejecutados")
            .apply()
    }
}