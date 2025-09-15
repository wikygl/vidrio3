package crystal.crystal.optimizadores.corte

import android.content.Context
import android.widget.ListView

/**
 * Maneja el formateo de datos para mostrar en UI
 * Extraído del código original de Corte.kt
 */
class CorteFormatter(private val context: Context) {

    /**
     * Función para formatear números con un decimal - exactamente como en el código original
     * Función pública para usar desde otras clases
     */
    fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    /**
     * Actualiza la lista de piezas requeridas con drawables
     */
    fun actualizarListaPiezas(
        lista: MutableList<PiezaCorte>,
        listView: ListView
    ) {
        val adapter = PiezaCorteAdapter(context, lista, this, true)
        listView.adapter = adapter
    }

    /**
     * Actualiza la lista de varillas disponibles con drawables
     */
    fun actualizarListaVarillas(
        lista2: MutableList<PiezaCorte>,
        listView: ListView
    ) {
        val adapter = PiezaCorteAdapter(context, lista2, this, false)
        listView.adapter = adapter
    }

    /**
     * Valida que los campos de entrada no estén vacíos
     */
    fun validarEntrada(medida: String, cantidad: String, referencia: String): Boolean {
        return medida.isNotEmpty() && cantidad.isNotEmpty() && referencia.isNotEmpty()
    }

    /**
     * Convierte string a Float de manera segura
     */
    fun convertirAFloat(texto: String): Float? {
        return texto.toFloatOrNull()
    }

    /**
     * Convierte string a Int de manera segura
     */
    fun convertirAInt(texto: String): Int? {
        return texto.toIntOrNull()
    }

    /**
     * Limpia los campos de texto
     */
    fun limpiarCampos(vararg campos: String): Array<String> {
        return Array(campos.size) { "" }
    }
}