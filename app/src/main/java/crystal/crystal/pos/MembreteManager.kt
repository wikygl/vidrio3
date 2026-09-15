package crystal.crystal.pos

import android.app.Activity
import android.content.SharedPreferences
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import crystal.crystal.comprobantes.DatosEmpresa

/**
 * El membrete y el sello de agua de las proformas.
 *
 * Los datos de la tienda no se piden aquí: son los mismos que ya usa el ticket —RUC, razón social,
 * dirección, teléfono, logo—, así que el papel dice lo mismo salga por donde salga. Lo que se toca
 * aquí es si se imprimen, cuál es el sello de agua y qué nota va al pie.
 */
class MembreteManager(
    private val activity: Activity,
    private val prefs: SharedPreferences,
    private val empresa: () -> DatosEmpresa?
) {
    /** Le pide a la pantalla que abra el anexador para elegir el sello. */
    var alPedirSello: (() -> Unit)? = null

    /** Para ir a la pantalla donde se apuntan los datos de la tienda. */
    var alPedirDatosDeLaTienda: (() -> Unit)? = null

    fun mostrar() {
        val m = DatosDeLaProforma.membrete(prefs, empresa())
        val quienEs = m.empresa?.let { it.nombreComercial ?: it.razonSocial }.orEmpty()

        val filas = arrayOf(
            (if (m.mostrarMembrete) "☑" else "☐") + "  Poner membrete" +
                (if (quienEs.isNotBlank()) "  ($quienEs)" else "  (sin datos de la tienda)"),
            (if (m.mostrarSello) "☑" else "☐") + "  Poner sello de agua" +
                (if (m.selloRuta.isBlank()) "  (sin imagen)" else ""),
            "🖼  Elegir la imagen del sello",
            "✍  Nota al pie" + (if (m.nota.isBlank()) "" else ": ${m.nota.take(24)}…"),
            "🏪  Datos de la tienda"
        )

        AlertDialog.Builder(activity)
            .setTitle("Membrete y sello")
            .setItems(filas) { _, cual ->
                when (cual) {
                    0 -> {
                        DatosDeLaProforma.ponerMembrete(prefs, !m.mostrarMembrete)
                        mostrar()
                    }
                    1 -> {
                        if (!m.mostrarSello && m.selloRuta.isBlank()) {
                            Toast.makeText(
                                activity,
                                "Primero elige la imagen del sello",
                                Toast.LENGTH_SHORT
                            ).show()
                            alPedirSello?.invoke()
                        } else {
                            DatosDeLaProforma.ponerSello(prefs, !m.mostrarSello)
                            mostrar()
                        }
                    }
                    2 -> if (m.selloRuta.isBlank()) alPedirSello?.invoke() else preguntarPorElSello()
                    3 -> pedirNota()
                    4 -> alPedirDatosDeLaTienda?.invoke()
                }
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun preguntarPorElSello() {
        AlertDialog.Builder(activity)
            .setTitle("Sello de agua")
            .setMessage("Ya hay una imagen puesta. ¿Cambiarla o quitarla?")
            .setPositiveButton("Cambiar") { _, _ -> alPedirSello?.invoke() }
            .setNegativeButton("Quitar") { _, _ ->
                DatosDeLaProforma.quitarSello(prefs)
                DatosDeLaProforma.ponerSello(prefs, false)
                Toast.makeText(activity, "Sello quitado", Toast.LENGTH_SHORT).show()
                mostrar()
            }
            .setNeutralButton("Cancelar", null)
            .show()
    }

    private fun pedirNota() {
        val dp = activity.resources.displayMetrics.density
        val et = EditText(activity).apply {
            hint = "Validez de la proforma, forma de pago, plazo de entrega…"
            setText(DatosDeLaProforma.membrete(prefs, empresa()).nota)
        }
        val cont = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((20 * dp).toInt(), (12 * dp).toInt(), (20 * dp).toInt(), 0)
            addView(TextView(activity).apply {
                text = "Va al pie de la proforma, en letra pequeña."
                textSize = 13f
            })
            addView(et)
        }
        AlertDialog.Builder(activity)
            .setTitle("Nota al pie")
            .setView(cont)
            .setPositiveButton("Guardar") { _, _ ->
                DatosDeLaProforma.guardarNota(prefs, et.text?.toString().orEmpty())
                mostrar()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Guarda el sello elegido. La llama la pantalla al volver del anexador. */
    fun ponerSello(ruta: String) {
        DatosDeLaProforma.guardarSello(prefs, ruta)
        DatosDeLaProforma.ponerSello(prefs, true)
        Toast.makeText(activity, "Sello de agua puesto", Toast.LENGTH_SHORT).show()
        mostrar()
    }
}
