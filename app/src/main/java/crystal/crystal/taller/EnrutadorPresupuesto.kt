package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.Listado
import crystal.crystal.taller.nova.NovaCorrediza
import crystal.crystal.taller.puerta.PuertasActivity

class EnrutadorPresupuesto(private val activity: AppCompatActivity) {

    data class ItemEnrutado(
        val listado: Listado,
        var destino: Class<out AppCompatActivity>?,
        val cliente: String,
        val indice: Int,
        var colorAluminio: String = "",
        var tipoVidrio: String = ""
    ) : java.io.Serializable

    // Mapeo ordenado: los más específicos primero
    private val mapeoProductos = listOf(
        "nova aparente" to NovaCorrediza::class.java,
        "nova apa" to NovaCorrediza::class.java,
        "nova inaparente" to NovaCorrediza::class.java,
        "nova ina" to NovaCorrediza::class.java,
        "nova" to NovaCorrediza::class.java,
        "division de bano" to DivisionBanoActivity::class.java,
        "division de baño" to DivisionBanoActivity::class.java,
        "division bano" to DivisionBanoActivity::class.java,
        "division baño" to DivisionBanoActivity::class.java,
        "puerta ducha" to PDuchaActivity::class.java,
        "ducha" to PDuchaActivity::class.java,
        "puerta" to PuertasActivity::class.java,
        "mampara paflon" to MamparaPaflon::class.java,
        "mampara paflón" to MamparaPaflon::class.java,
        "mampara fc" to MamparaFC::class.java,
        "mampara vidrio" to MamparaVidrioActivity::class.java,
        "mampara" to MamparaFC::class.java,
        "vitroventana" to Vitroven::class.java,
        "vitroven" to Vitroven::class.java,
        "ventana" to VentanaAl::class.java,
        "muro cortina" to Muro::class.java,
        "muro" to Muro::class.java,
        "pivot" to PivotAl::class.java,
        "reja" to RejasActivity::class.java,
        "rejas" to RejasActivity::class.java,
    )

    // Nombres legibles para el diálogo de asignación
    private val calculadorasDisponibles = listOf(
        "Nova Corrediza" to NovaCorrediza::class.java,
        "Puerta" to PuertasActivity::class.java,
        "Mampara FC" to MamparaFC::class.java,
        "Mampara Paflón" to MamparaPaflon::class.java,
        "Mampara Vidrio" to MamparaVidrioActivity::class.java,
        "Ventana Aluminio" to VentanaAl::class.java,
        "Vitroven" to Vitroven::class.java,
        "Muro" to Muro::class.java,
        "Pivot" to PivotAl::class.java,
        "Puerta Ducha" to PDuchaActivity::class.java,
        "Rejas" to RejasActivity::class.java,
        "Division Baño" to DivisionBanoActivity::class.java,
    )

    fun clasificar(items: List<Listado>, cliente: String): List<ItemEnrutado> {
        return items.mapIndexed { indice, item ->
            ItemEnrutado(
                listado = item,
                destino = buscarDestino(item.producto),
                cliente = cliente,
                indice = indice
            )
        }
    }

    private fun buscarDestino(producto: String): Class<out AppCompatActivity>? {
        val productoLower = producto.lowercase().trim()
        if (productoLower.isEmpty() || productoLower == "...") return null

        for ((clave, destino) in mapeoProductos) {
            if (productoLower.contains(clave)) {
                return destino
            }
        }
        return null
    }

    fun obtenerSinDestino(items: List<ItemEnrutado>) = items.filter { it.destino == null }
    fun obtenerConDestino(items: List<ItemEnrutado>) = items.filter { it.destino != null }

    /**
     * Muestra diálogo con TODOS los items sin destino en una lista.
     * Al tocar un item se abre un segundo diálogo para elegir calculadora.
     * "Volver" cancela y devuelve null para que Taller restaure el card.
     * "Procesar" finaliza la asignación con lo que se haya asignado + conDestino.
     */
    @SuppressLint("SetTextI18n")
    fun mostrarDialogoAsignacion(
        sinDestino: List<ItemEnrutado>,
        conDestino: List<ItemEnrutado>,
        onCompleto: (List<ItemEnrutado>?) -> Unit
    ) {
        if (sinDestino.isEmpty()) {
            onCompleto(conDestino)
            return
        }

        mostrarListaItems(sinDestino, conDestino, onCompleto)
    }

    private fun mostrarListaItems(
        sinDestino: List<ItemEnrutado>,
        conDestino: List<ItemEnrutado>,
        onCompleto: (List<ItemEnrutado>?) -> Unit
    ) {
        // Construir líneas descriptivas para cada item
        val lineas = sinDestino.map { item ->
            val l = item.listado
            val asignado = if (item.destino != null) " ✓" else ""
            val prod = if (l.producto.isNotBlank() && l.producto != "...") " (${l.producto})" else ""
            "${l.medi1} x ${l.medi2} = ${l.canti}$prod$asignado"
        }.toTypedArray()

        val asignados = sinDestino.count { it.destino != null }
        val titulo = "Items sin producto ($asignados/${sinDestino.size} asignados)"

        AlertDialog.Builder(activity)
            .setTitle(titulo)
            .setItems(lineas) { _, which ->
                mostrarOpcionesCalculadora(sinDestino[which]) {
                    mostrarListaItems(sinDestino, conDestino, onCompleto)
                }
            }
            .setPositiveButton("Procesar") { _, _ ->
                val resultado = conDestino.toMutableList()
                resultado.addAll(sinDestino.filter { it.destino != null })
                onCompleto(resultado)
            }
            .setNegativeButton("Volver") { _, _ ->
                onCompleto(null)
            }
            .setCancelable(false)
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarOpcionesCalculadora(item: ItemEnrutado, onVolver: () -> Unit) {
        val l = item.listado
        val titulo = buildString {
            append("${l.medi1} x ${l.medi2} = ${l.canti}")
            if (item.destino != null) {
                val nombreActual = calculadorasDisponibles.find { it.second == item.destino }?.first ?: "?"
                append("\n→ $nombreActual")
            }
        }

        val opciones = mutableListOf("-- Quitar asignación --")
        opciones.addAll(calculadorasDisponibles.map { it.first })

        AlertDialog.Builder(activity)
            .setTitle(titulo)
            .setItems(opciones.toTypedArray()) { _, which ->
                if (which == 0) {
                    item.destino = null
                } else {
                    item.destino = calculadorasDisponibles[which - 1].second
                }
                onVolver()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                onVolver()
            }
            .show()
    }

    /**
     * Crea el Intent para abrir una calculadora con datos pre-cargados.
     */
    fun crearIntentCalculadora(item: ItemEnrutado): Intent {
        val intent = Intent(activity, item.destino)
        intent.putExtra("rcliente", item.cliente)
        intent.putExtra("ancho", item.listado.medi1)
        intent.putExtra("alto", item.listado.medi2)
        intent.putExtra("cantidad", item.listado.canti)
        intent.putExtra("producto", item.listado.producto)
        intent.putExtra("modo_masivo", true)
        intent.putExtra("color_aluminio", item.colorAluminio)
        intent.putExtra("tipo_vidrio", item.tipoVidrio)
        return intent
    }
}
