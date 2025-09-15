package crystal.crystal.casilla

import android.content.Context
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

object ProyectoUIHelper {

    // Configurar TextView para mostrar el proyecto activo
    fun configurarVisorProyectoActivo(context: Context, textView: TextView) {
        actualizarVisorProyectoActivo(context, textView)

        // ✅ MODIFICADO: Clic para mostrar gestión avanzada (diálogos jerárquicos)
        textView.setOnClickListener {
            if (ProyectoManager.hayProyectoActivo()) {
                // Si hay proyecto activo, mostrar gestión avanzada
                val callback = crearCallbackConActualizacionUI(context, textView, null)
                DialogosProyecto.mostrarDialogoGestionAvanzada(context, callback)
            } else {
                // Si no hay proyecto activo, mostrar información básica
                DialogosProyecto.mostrarInfoProyectoActivo(context)
            }
        }
    }
    // Actualizar el TextView con la informaciÃ³n del proyecto activo
    fun actualizarVisorProyectoActivo(context: Context, textView: TextView) {
        val proyectoActivo = ProyectoManager.getProyectoActivo()

        if (proyectoActivo != null) {
            // Contar paquetes reales del proyecto
            val totalPaquetes = ProyectoManager.obtenerTotalPaquetes(context)
            textView.text = "$proyectoActivo ($totalPaquetes paquetes)"
            textView.setTextColor(0xFF4CAF50.toInt()) // Verde
        } else {
            textView.text = "Sin proyecto activo"
            textView.setTextColor(0xFFFF9800.toInt()) // Naranja
        }
    }
    // Verificar si hay proyecto activo y mostrar diÃ¡logo si es necesario
    fun verificarProyectoActivo(context: Context, callback: DialogosProyecto.ProyectoCallback): Boolean {
        if (!ProyectoManager.hayProyectoActivo()) {
            Toast.makeText(context, "Seleccione un proyecto primero", Toast.LENGTH_SHORT).show()
            DialogosProyecto.mostrarDialogoGestionProyectos(context, callback)
            return false
        }
        return true
    }
    // Agregar opciones de menÃº para gestiÃ³n de proyectos
    fun agregarOpcionesMenuProyecto(menu: Menu) {
        menu.add(0, MENU_GESTION_PROYECTOS, 0, "Gestionar Proyectos")
        menu.add(0, MENU_INFO_PROYECTO, 1, "Info Proyecto Activo")
        menu.add(0, MENU_GESTION_AVANZADA, 2, "Gestión Avanzada") // Nueva opción
    }
    // Manejar selecciÃ³n de opciones del menÃº
    fun manejarSeleccionMenu(context: Context,itemId: Int,callback: DialogosProyecto.ProyectoCallback,
        onProyectoCambiado: (() -> Unit)? = null
    ): Boolean {
        return when (itemId) {
            MENU_GESTION_PROYECTOS -> {
                DialogosProyecto.mostrarDialogoGestionProyectos(context, object : DialogosProyecto.ProyectoCallback {
                    override fun onProyectoSeleccionado(nombreProyecto: String) {
                        callback.onProyectoSeleccionado(nombreProyecto)
                        onProyectoCambiado?.invoke()
                    }
                    override fun onProyectoCreado(nombreProyecto: String) {
                        callback.onProyectoCreado(nombreProyecto)
                        onProyectoCambiado?.invoke()
                    }
                    override fun onProyectoEliminado(nombreProyecto: String) {
                        callback.onProyectoEliminado(nombreProyecto)
                        onProyectoCambiado?.invoke()
                    }
                })
                true
            }
            MENU_INFO_PROYECTO -> {
                DialogosProyecto.mostrarInfoProyectoActivo(context)
                true
            }
            MENU_GESTION_AVANZADA -> {
                // Nueva opción para gestión avanzada desde menú
                DialogosProyecto.mostrarDialogoGestionAvanzada(context, object : DialogosProyecto.ProyectoCallback {
                    override fun onProyectoSeleccionado(nombreProyecto: String) {
                        callback.onProyectoSeleccionado(nombreProyecto)
                        onProyectoCambiado?.invoke()
                    }
                    override fun onProyectoCreado(nombreProyecto: String) {
                        callback.onProyectoCreado(nombreProyecto)
                        onProyectoCambiado?.invoke()
                    }
                    override fun onProyectoEliminado(nombreProyecto: String) {
                        callback.onProyectoEliminado(nombreProyecto)
                        onProyectoCambiado?.invoke()
                    }
                })
                true
            }
            else -> false
        }
    }
    // Callback que actualiza UI automÃ¡ticamente
    fun crearCallbackConActualizacionUI(context: Context,textViewProyecto: TextView? = null,
        activity: AppCompatActivity? = null
    ): DialogosProyecto.ProyectoCallback {
        return object : DialogosProyecto.ProyectoCallback {
            override fun onProyectoSeleccionado(nombreProyecto: String) {
                textViewProyecto?.let { actualizarVisorProyectoActivo(context, it) }
            }

            override fun onProyectoCreado(nombreProyecto: String) {
                textViewProyecto?.let { actualizarVisorProyectoActivo(context, it) }
            }

            override fun onProyectoEliminado(nombreProyecto: String) {
                textViewProyecto?.let { actualizarVisorProyectoActivo(context, it) }

                // Si no hay más proyectos, forzar selección
                if (MapStorage.obtenerListaProyectos(context).isEmpty()) {
                    DialogosProyecto.mostrarDialogoCrearProyecto(context, this)
                }
            }
        }
    }
    fun mostrarInfoRapidaProyecto(context: Context) {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val totalPaquetes = ProyectoManager.obtenerTotalPaquetes(context)
            val listaPaquetes = ProyectoManager.obtenerListaPaquetes(context)
            val ultimosPaquetes = listaPaquetes.takeLast(3).joinToString(", ")

            val mensaje = if (listaPaquetes.size > 3) {
                "$proyectoActivo\n$totalPaquetes paquetes\nÚltimos: ..., $ultimosPaquetes"
            } else {
                "$proyectoActivo\n$totalPaquetes paquetes\nPaquetes: $ultimosPaquetes"
            }

            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "No hay proyecto activo", Toast.LENGTH_SHORT).show()
        }
    }
    /*** Verificar si un proyecto necesita limpieza (elementos huérfanos, etc.)*/
    fun verificarIntegridadProyecto(context: Context, nombreProyecto: String): Boolean {
        val proyectoOriginal = ProyectoManager.getProyectoActivo()
        ProyectoManager.setProyectoActivo(context, nombreProyecto)

        val mapListas = MapStorage.cargarProyecto(context, nombreProyecto)
        var elementosValidos = 0
        var elementosInvalidos = 0

        mapListas?.let { map ->
            for ((_, lista) in map) {
                for (elemento in lista) {
                    if (elemento.size >= 3 && elemento[2].isNotEmpty()) {
                        elementosValidos++
                    } else {
                        elementosInvalidos++
                    }
                }
            }
        }

        // Restaurar proyecto original
        if (proyectoOriginal != null) {
            ProyectoManager.setProyectoActivo(context, proyectoOriginal)
        } else {
            ProyectoManager.limpiarProyectoActivo(context)
        }

        if (elementosInvalidos > 0) {
            Toast.makeText(
                context,
                "Proyecto '$nombreProyecto' tiene $elementosInvalidos elementos con problemas",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        return true
    }
    // ==================== CONSTANTES ====================
    const val MENU_GESTION_PROYECTOS = 9001
    const val MENU_INFO_PROYECTO = 9002
    const val MENU_GESTION_AVANZADA = 9003 //
}