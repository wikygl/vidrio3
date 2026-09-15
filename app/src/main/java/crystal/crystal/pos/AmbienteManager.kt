package crystal.crystal.pos

import android.app.Activity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import crystal.crystal.Listado

/**
 * Poner a cada ítem el ambiente donde va: la sala, el consultorio, el piso.
 *
 * Se mide sala por sala, así que lo normal es apuntar varias medidas seguidas del mismo sitio y
 * ponerles el ambiente de una vez. Por eso, además de ponerlo a un ítem, se puede poner "de aquí
 * en adelante": desde el ítem que se toca hasta el final, que es justo lo que pasó en la obra.
 */
class AmbienteManager(
    private val activity: Activity,
    private val lista: MutableList<Listado>
) {
    var onListaModificada: (() -> Unit)? = null

    /** Abre el ambiente de ese ítem: los que ya se usaron, uno nuevo, o quitárselo. */
    fun mostrar(posicion: Int) {
        val item = lista.getOrNull(posicion) ?: return
        val usados = AmbientesDeProforma.enLaLista(lista)
        val actual = AmbientesDeProforma.de(item)

        val filas = mutableListOf<String>()
        usados.forEach { filas.add(if (it == actual) "✓  $it" else "•  $it") }
        filas.add("➕  Otro ambiente…")
        if (actual.isNotEmpty()) filas.add("🗑  Quitarle el ambiente")

        AlertDialog.Builder(activity)
            .setTitle(if (actual.isEmpty()) "¿En qué ambiente va?" else "Ambiente: $actual")
            .setItems(filas.toTypedArray()) { _, cual ->
                val iOtro = usados.size
                val iQuitar = if (actual.isEmpty()) -1 else usados.size + 1
                when (cual) {
                    iOtro -> pedirAmbiente(posicion)
                    iQuitar -> {
                        AmbientesDeProforma.guardar(item, "")
                        onListaModificada?.invoke()
                    }
                    else -> ponerAmbiente(posicion, usados[cual])
                }
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun pedirAmbiente(posicion: Int) {
        val dp = activity.resources.displayMetrics.density
        val et = EditText(activity).apply {
            hint = "Sala de partos, consultorio 3, sexto piso…"
            setText(AmbientesDeProforma.de(lista[posicion]))
        }
        val cont = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((20 * dp).toInt(), (12 * dp).toInt(), (20 * dp).toInt(), 0)
            addView(et)
        }
        AlertDialog.Builder(activity)
            .setTitle("Ambiente")
            .setView(cont)
            .setPositiveButton("Aceptar") { _, _ ->
                val nombre = et.text?.toString()?.trim().orEmpty()
                if (nombre.isEmpty()) {
                    Toast.makeText(activity, "Escribe el nombre del ambiente", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                ponerAmbiente(posicion, nombre)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Se lo pone al ítem y, si detrás hay más, ofrece ponérselo también a los que siguen.
     *
     * Es lo que pasa al medir: se entra a una sala, se apuntan tres o cuatro medidas y se pasa a la
     * siguiente. Poniéndolo uno a uno se tarda más en clasificar que en medir.
     */
    private fun ponerAmbiente(posicion: Int, nombre: String) {
        val detras = lista.size - posicion - 1
        if (detras <= 0) {
            AmbientesDeProforma.guardar(lista[posicion], nombre)
            onListaModificada?.invoke()
            return
        }
        AlertDialog.Builder(activity)
            .setTitle(nombre)
            .setMessage("¿Solo a este ítem o también a los $detras que vienen detrás?")
            .setPositiveButton("Solo a este") { _, _ ->
                AmbientesDeProforma.guardar(lista[posicion], nombre)
                onListaModificada?.invoke()
            }
            .setNegativeButton("De aquí en adelante") { _, _ ->
                (posicion until lista.size).forEach { AmbientesDeProforma.guardar(lista[it], nombre) }
                onListaModificada?.invoke()
            }
            .setNeutralButton("Cancelar", null)
            .show()
    }

    /** Un resumen de cómo está repartido el presupuesto, para verlo de un vistazo. */
    fun mostrarResumen() {
        val grupos = AmbientesDeProforma.agrupar(lista)
        if (grupos.isEmpty()) {
            Toast.makeText(activity, "La lista está vacía", Toast.LENGTH_SHORT).show()
            return
        }
        val texto = grupos.joinToString("\n") { (ambiente, items) ->
            val nombre = ambiente.ifEmpty { "(sin ambiente)" }
            "• $nombre — ${items.size} ${if (items.size == 1) "ítem" else "ítems"}"
        }
        val dp = activity.resources.displayMetrics.density
        val tv = TextView(activity).apply {
            text = texto
            textSize = 15f
            setPadding((20 * dp).toInt(), (12 * dp).toInt(), (20 * dp).toInt(), 0)
        }
        AlertDialog.Builder(activity)
            .setTitle("Ambientes del presupuesto")
            .setView(tv)
            .setPositiveButton("Cerrar", null)
            .show()
    }
}
