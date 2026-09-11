package crystal.crystal.taller

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * Cola de medidas que se envían desde MedidaActivity a las calculadoras según su clasificación.
 *
 * Flujo: MedidaActivity clasifica las medidas archivadas de un cliente, deja elegir cuáles enviar,
 * arma la cola y lanza la primera en su calculadora correspondiente. Dentro de la calculadora la
 * cola se navega con [NavegadorCola]: se puede abrir cualquier medida, omitirla o editarla sin tener
 * que archivarla, y al archivar se ofrece la siguiente pendiente.
 *
 * La cola viaja en el Intent, así que cada pantalla que lanza otra debe pasar la lista actualizada
 * (lo hace [lanzar]) para que el estado de cada medida no se pierda.
 */
object ColaCalculadoras {

    const val EXTRA_DESDE_MEDIDAS = "cola_desde_medidas"
    const val EXTRA_COLA = "cola_medidas_items"
    const val EXTRA_INDICE = "cola_medidas_indice"
    const val EXTRA_BOCETO_PATH = "cola_medidas_boceto_path"

    /** En qué situación está una medida dentro de la cola. */
    enum class EstadoCola { PENDIENTE, HECHA, OMITIDA }

    /** Una medida individual dentro de la cola. */
    data class MedidaCalc(
        val producto: String,
        val ancho: Float,
        val alto: Float,
        val cantidad: Float,
        val cliente: String,
        /** Ruta absoluta del boceto (json) para recuperar el gráfico original en ivDiseno. */
        val bocetoArchivo: String,
        /**
         * El contorno del vano en cm, `x,y;x,y;…`, con la Y hacia abajo. Vacío para los vanos
         * rectos, que con el ancho y el alto ya quedan descritos.
         *
         * Va aquí porque un vano escalonado —el alféizar que sube en un trozo— no cabe en dos
         * números, y es la calculadora la que tiene que saberlo para armar los tramos.
         */
        val contorno: String = "",
        val estado: EstadoCola = EstadoCola.PENDIENTE
    ) : java.io.Serializable

    /** true cuando la calculadora fue abierta desde MedidaActivity con una cola de medidas. */
    fun desdeMedidas(activity: Activity): Boolean =
        activity.intent.getBooleanExtra(EXTRA_DESDE_MEDIDAS, false)

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    fun cola(activity: Activity): ArrayList<MedidaCalc> =
        (activity.intent.getSerializableExtra(EXTRA_COLA) as? ArrayList<MedidaCalc>) ?: ArrayList()

    fun indice(activity: Activity): Int =
        activity.intent.getIntExtra(EXTRA_INDICE, 0)

    fun bocetoPath(activity: Activity): String =
        activity.intent.getStringExtra(EXTRA_BOCETO_PATH).orEmpty()

    /** Guarda la cola en el Intent de la actividad, para que viaje a la siguiente calculadora. */
    fun guardarCola(activity: Activity, cola: List<MedidaCalc>) {
        activity.intent.putExtra(EXTRA_COLA, ArrayList(cola))
    }

    fun guardarIndice(activity: Activity, indice: Int) {
        activity.intent.putExtra(EXTRA_INDICE, indice)
    }

    /** Devuelve una copia de [cola] con la medida [indice] reemplazada. */
    fun reemplazar(cola: List<MedidaCalc>, indice: Int, item: MedidaCalc): ArrayList<MedidaCalc> {
        val copia = ArrayList(cola)
        if (indice in copia.indices) copia[indice] = item
        return copia
    }

    fun marcar(cola: List<MedidaCalc>, indice: Int, estado: EstadoCola): ArrayList<MedidaCalc> {
        val item = cola.getOrNull(indice) ?: return ArrayList(cola)
        return reemplazar(cola, indice, item.copy(estado = estado))
    }

    /**
     * Primera medida PENDIENTE después de [desde], dando la vuelta al final de la lista para
     * recoger las que se dejaron para más tarde. Devuelve -1 si ya no queda ninguna.
     */
    fun siguientePendiente(cola: List<MedidaCalc>, desde: Int): Int {
        if (cola.isEmpty()) return -1
        for (paso in 1..cola.size) {
            val i = Math.floorMod(desde + paso, cola.size)
            if (cola[i].estado == EstadoCola.PENDIENTE) return i
        }
        return -1
    }

    /**
     * Lanza la medida en la posición [indice] de la cola, en la calculadora que le corresponde.
     * Devuelve false si el índice está fuera de rango o el producto no tiene calculadora asociada.
     */
    fun lanzar(origen: Context, cola: List<MedidaCalc>, indice: Int): Boolean {
        val item = cola.getOrNull(indice) ?: return false
        val destino = EnrutadorPresupuesto.destinoPara(item.producto) ?: return false
        val intent = Intent(origen, destino).apply {
            putExtra("rcliente", item.cliente)
            putExtra("ancho", item.ancho)
            putExtra("alto", item.alto)
            putExtra("cantidad", item.cantidad)
            putExtra("producto", item.producto)
            putExtra(EXTRA_DESDE_MEDIDAS, true)
            putExtra(EXTRA_BOCETO_PATH, item.bocetoArchivo)
            putExtra(EXTRA_COLA, ArrayList(cola))
            putExtra(EXTRA_INDICE, indice)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        origen.startActivity(intent)
        return true
    }
}
