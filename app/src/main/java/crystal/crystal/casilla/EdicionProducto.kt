package crystal.crystal.casilla

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * Volver a abrir un producto archivado (Rm4, Vna2, P10…) en su calculadora y, al archivarlo de
 * nuevo, que REEMPLACE al que había con el mismo número en vez de sumar uno más.
 *
 * La calculadora se abre con [EXTRA_ID] (el id tal como está archivado) y [EXTRA_PROYECTO]. No
 * hace falta tocar cada calculadora: todas piden su número con
 * [ProyectoManager.reservarNumerosPorPrefijo] y guardan con [MapStorage.guardarMap], y ahí se
 * engancha esto:
 *  1. Al reservar, si la actividad viene a editar un id de ese prefijo en el proyecto activo, la
 *     primera copia se queda con su número (las demás, si se archivan varias, siguen en los nuevos).
 *  2. Al guardar, se quitan las filas que ese id tenía guardadas antes, una por una; quedan las
 *     recién archivadas con el mismo id.
 * Solo vale para la actividad que se abrió a editar: otra calculadora archiva como siempre.
 */
object EdicionProducto {

    const val EXTRA_ID = "edicion_producto_id"
    const val EXTRA_PROYECTO = "edicion_producto_proyecto"
    /** El diseño completo del producto (el JSON del ropero, el paquete de Nova…), si lo guarda. */
    const val EXTRA_DISENO = "edicion_producto_diseno"

    /** El id cuyo número se reservó para reemplazar y aún no se guardó. */
    private var pendiente: String? = null
    /** La actividad que lo reservó: solo al guardar desde ella se reemplaza. */
    private var pendienteEn: Int = 0

    fun ponerEn(intent: Intent, id: String, proyecto: String, diseno: String = "") {
        intent.putExtra(EXTRA_ID, id)
        intent.putExtra(EXTRA_PROYECTO, proyecto)
        if (diseno.isNotBlank()) intent.putExtra(EXTRA_DISENO, diseno)
    }

    /** El diseño con que se abrió la calculadora para editar, o null. */
    fun disenoDe(activity: Activity): String? = activity.intent?.getStringExtra(EXTRA_DISENO)?.takeIf { it.isNotBlank() }

    /** El id que esta actividad está editando, si es del proyecto activo; si no, null. */
    fun idEnEdicion(context: Context): String? {
        val intent = (context as? Activity)?.intent ?: return null
        val id = intent.getStringExtra(EXTRA_ID)?.takeIf { it.isNotBlank() } ?: return null
        val proyecto = intent.getStringExtra(EXTRA_PROYECTO).orEmpty()
        return if (proyecto == ProyectoManager.getProyectoActivo().orEmpty()) id else null
    }

    /** El número de "Rm4" para el prefijo "Rm" (el de "P10, abel" para "P"); null si no es de ese prefijo. */
    fun numeroPara(id: String, prefijo: String): Int? {
        val m = Regex("^([A-Za-z]+)(\\d+)").find(id.trim()) ?: return null
        if (!m.groupValues[1].equals(prefijo, ignoreCase = true)) return null
        return m.groupValues[2].toIntOrNull()
    }

    /**
     * Los números para archivar [cantidad] copias: si esta actividad edita un id de este prefijo,
     * el primero es el suyo, y el reemplazo queda pendiente hasta que se guarde.
     */
    fun numerosConEdicion(context: Context, prefijo: String, cantidad: Int, nuevos: (Int) -> List<Int>): List<Int> {
        val id = idEnEdicion(context)
        pendiente = null
        val propio = id?.let { numeroPara(it, prefijo) } ?: return nuevos(cantidad)
        pendiente = id
        pendienteEn = System.identityHashCode(context)
        val resto = if (cantidad > 1) nuevos(cantidad - 1) else emptyList()
        return listOf(propio) + resto
    }

    /**
     * Antes de guardar el proyecto: si hay un reemplazo pendiente, se quitan de [mapListas] las
     * filas que ese id tenía guardadas, una por cada una (las nuevas, con el mismo id, se quedan).
     */
    fun aplicarReemplazo(context: Context, mapListas: MutableMap<String, MutableList<MutableList<String>>>) {
        val id = pendiente ?: return
        if (pendienteEn != System.identityHashCode(context)) return
        pendiente = null
        val proyecto = ProyectoManager.getProyectoActivo() ?: return
        val guardado = MapStorage.cargarProyecto(context, proyecto) ?: return
        for ((lista, filas) in guardado) {
            val destino = mapListas[lista] ?: continue
            filas.filter { it.getOrNull(2) == id }.forEach { vieja ->
                val i = destino.indexOfFirst { it == vieja }
                if (i >= 0) destino.removeAt(i)
            }
            if (destino.isEmpty()) mapListas.remove(lista)
        }
    }
}
