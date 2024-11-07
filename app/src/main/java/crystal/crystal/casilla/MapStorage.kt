package crystal.crystal.casilla

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MapStorage {

    // Función para guardar el Map en SharedPreferences
    fun guardarMap(context: Context, mapListas: MutableMap<String, MutableList<MutableList<String>>>) {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convertir el Map a JSON
        val gson = Gson()
        val json = gson.toJson(mapListas)

        // Guardar el JSON en SharedPreferences
        editor.putString("mapListas", json)
        editor.apply()
    }

    // Función para cargar el Map desde SharedPreferences
    fun cargarMap(context: Context): MutableMap<String, MutableList<MutableList<String>>>? {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)

        // Obtener el JSON guardado
        val json = sharedPreferences.getString("mapListas", null)

        return if (json != null) {
            // Convertir el JSON de vuelta a un Map
            val gson = Gson()
            val type = object : TypeToken<MutableMap<String, MutableList<MutableList<String>>>>() {}.type
            gson.fromJson<MutableMap<String, MutableList<MutableList<String>>>>(json, type)
        } else {
            null // No hay datos guardados
        }
    }
}
