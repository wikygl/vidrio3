package crystal.crystal.casilla

import android.app.AlertDialog
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

object DialogosProyecto {

    // Interfaz para callbacks
    interface ProyectoCallback {
        fun onProyectoSeleccionado(nombreProyecto: String)
        fun onProyectoCreado(nombreProyecto: String)
        fun onProyectoEliminado(nombreProyecto: String)
    }

    // ==================== DIÁLOGO PARA SELECCIONAR PROYECTO AL ARCHIVAR ====================

    /**
     * Diálogo que aparece al hacer clic en btArchivar
     * Muestra opción de "Crear nuevo" + lista de proyectos existentes
     */
    fun mostrarDialogoSeleccionarParaArchivar(context: Context, callback: ProyectoCallback) {
        val proyectos = MapStorage.obtenerListaProyectosConMetadata(context)
        val opciones = arrayListOf<String>()

        // Primera opción: Crear nuevo proyecto
        opciones.add("✚ Crear Nuevo Proyecto")

        // Agregar proyectos existentes - USAR LA MISMA FUNCIÓN QUE EL TEXTVIEW
        for (metadata in proyectos) {
            val proyectoOriginal = ProyectoManager.getProyectoActivo()
            ProyectoManager.setProyectoActivo(context, metadata.nombre)
            val totalPaquetes = ProyectoManager.obtenerTotalPaquetes(context)

            // Restaurar proyecto original
            if (proyectoOriginal != null) {
                ProyectoManager.setProyectoActivo(context, proyectoOriginal)
            } else {
                ProyectoManager.limpiarProyectoActivo(context)
            }

            opciones.add("${metadata.nombre} ($totalPaquetes paquetes)")
        }

        AlertDialog.Builder(context)
            .setTitle("Seleccionar Proyecto para Archivar")
            .setItems(opciones.toTypedArray()) { _, position ->
                if (position == 0) {
                    // Crear nuevo proyecto
                    mostrarDialogoCrearProyecto(context, callback)
                } else {
                    // Seleccionar proyecto existente
                    val proyectoSeleccionado = proyectos[position - 1].nombre
                    ProyectoManager.setProyectoActivo(context, proyectoSeleccionado)
                    callback.onProyectoSeleccionado(proyectoSeleccionado)
                    Toast.makeText(context, "Archivando en proyecto '$proyectoSeleccionado'", Toast.LENGTH_SHORT).show()
                }
            }
            .setCancelable(false) // ✅ NO se puede cancelar tocando fuera
            .create()
            .apply {
                setCanceledOnTouchOutside(false) // ✅ NO se cancela tocando fuera
                show()
            }
    }

    // ==================== DIÁLOGOS JERÁRQUICOS PARA GESTIÓN AVANZADA ====================

    /**
     * NIVEL 1: Mostrar todos los proyectos (al hacer clic en tvProyectoActivo)
     */
    fun mostrarDialogoGestionAvanzada(context: Context, callback: ProyectoCallback) {
        val proyectos = MapStorage.obtenerListaProyectosConMetadata(context)

        if (proyectos.isEmpty()) {
            Toast.makeText(context, "No hay proyectos guardados", Toast.LENGTH_SHORT).show()
            return
        }

        val nombresProyectos = proyectos.map { metadata ->
            val totalPaquetes = contarPaquetesEnProyecto(context, metadata.nombre)
            // ✅ FORMATO MEJORADO: Todo en una línea clara
            "${metadata.nombre} (${totalPaquetes} paquetes) - ${metadata.fechaModificacion}"
        }.toTypedArray()

        AlertDialog.Builder(context)
            .setTitle("Gestión Avanzada de Proyectos")
            .setItems(nombresProyectos) { _, position ->
                val proyectoSeleccionado = proyectos[position].nombre
                mostrarDialogoOpcionesProyecto(context, proyectoSeleccionado, callback)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * NIVEL 2: Opciones para un proyecto específico (Eliminar/Editar/Poner Activo/Crear Nuevo)
     */
    private fun mostrarDialogoOpcionesProyecto(
        context: Context,
        nombreProyecto: String,
        callback: ProyectoCallback
    ) {
        val opciones = arrayOf(
            "🗑️ Eliminar Proyecto",
            "✏️ Editar Contenido",
            "✅ Poner Activo",
            "➕ Crear Nuevo Proyecto"
        )

        AlertDialog.Builder(context)
            .setTitle("Opciones: $nombreProyecto")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> {
                        // Confirmar eliminación
                        AlertDialog.Builder(context)
                            .setTitle("Confirmar Eliminación")
                            .setMessage("¿Está seguro de eliminar el proyecto '$nombreProyecto'?\n\nEsta acción no se puede deshacer.")
                            .setPositiveButton("Eliminar") { _, _ ->
                                val eliminado = MapStorage.eliminarProyecto(context, nombreProyecto)
                                if (eliminado) {
                                    callback.onProyectoEliminado(nombreProyecto)

                                    // ✅ CORRECCIÓN: Si había proyecto activo eliminado, activar otro automáticamente
                                    if (ProyectoManager.getProyectoActivo() == null) {
                                        val proyectosRestantes = MapStorage.obtenerListaProyectos(context)
                                        if (proyectosRestantes.isNotEmpty()) {
                                            val primerProyecto = proyectosRestantes.first()
                                            ProyectoManager.setProyectoActivo(context, primerProyecto)
                                            callback.onProyectoSeleccionado(primerProyecto)
                                            Toast.makeText(context, "Proyecto '$nombreProyecto' eliminado. '$primerProyecto' ahora está activo", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "Proyecto '$nombreProyecto' eliminado", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Proyecto '$nombreProyecto' eliminado", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Error al eliminar el proyecto", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                    1 -> {
                        // Mostrar elementos del proyecto
                        mostrarDialogoElementosProyecto(context, nombreProyecto, callback)
                    }
                    2 -> {
                        // Poner activo - cambiar proyecto de trabajo
                        ProyectoManager.setProyectoActivo(context, nombreProyecto)
                        callback.onProyectoSeleccionado(nombreProyecto)
                        Toast.makeText(context, "Proyecto '$nombreProyecto' ahora está activo", Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        // Crear nuevo proyecto
                        mostrarDialogoCrearProyecto(context, callback)
                    }
                }
            }
            .setNegativeButton("Atrás", null)
            .show()
    }

    /**
     * NIVEL 3: Mostrar paquetes completos como elementos clickeables para eliminar
     */
    private fun mostrarDialogoElementosProyecto(
        context: Context,
        nombreProyecto: String,
        callback: ProyectoCallback
    ) {
        // Establecer temporalmente el proyecto como activo para obtener sus paquetes
        val proyectoOriginal = ProyectoManager.getProyectoActivo()
        ProyectoManager.setProyectoActivo(context, nombreProyecto)

        val paquetes = ProyectoManager.obtenerListaPaquetes(context)

        if (paquetes.isEmpty()) {
            // Restaurar proyecto original
            if (proyectoOriginal != null) {
                ProyectoManager.setProyectoActivo(context, proyectoOriginal)
            } else {
                ProyectoManager.limpiarProyectoActivo(context)
            }
            Toast.makeText(context, "El proyecto '$nombreProyecto' está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear lista de paquetes con su contenido completo
        val elementosLista = mutableListOf<String>()
        val mapaPaquetes = mutableMapOf<Int, String>()

        // Agregar encabezado
        elementosLista.add("📋 Resumen del Proyecto:")
        elementosLista.add("Total: ${paquetes.size} paquetes")
        elementosLista.add("Toca un paquete para eliminarlo:")
        elementosLista.add("") // Línea vacía

        var indiceActual = 4

        // Procesar cada paquete completo
        paquetes.forEach { paquete ->
            val tipoActivity = extraerTipoActivity(paquete)
            val elementosPaquete = ProyectoManager.obtenerElementosPaquete(context, paquete)

            // Crear resumen del paquete completo
            val contenidoPaquete = StringBuilder()
            contenidoPaquete.append("🗑️ $paquete ($tipoActivity):\n")

            if (elementosPaquete.isNotEmpty()) {
                for ((categoria, elementos) in elementosPaquete) {
                    val resumenCategoria = elementos.joinToString(", ") { elemento ->
                        val valor1 = if (elemento.size > 0) elemento[0] else ""
                        val valor2 = if (elemento.size > 1) elemento[1] else ""
                        "($valor1=$valor2)"
                    }
                    contenidoPaquete.append("• $categoria: $resumenCategoria\n")
                }
            } else {
                contenidoPaquete.append("• Sin datos\n")
            }

            elementosLista.add(contenidoPaquete.toString().trim())
            mapaPaquetes[indiceActual] = paquete
            indiceActual++

            elementosLista.add("") // Línea vacía entre paquetes
            indiceActual++
        }

        // Restaurar proyecto original
        if (proyectoOriginal != null) {
            ProyectoManager.setProyectoActivo(context, proyectoOriginal)
        } else {
            ProyectoManager.limpiarProyectoActivo(context)
        }

        // Crear diálogo con paquetes clickeables
        AlertDialog.Builder(context)
            .setTitle("📋 Contenido: $nombreProyecto")
            .setItems(elementosLista.toTypedArray()) { _, position ->
                // Verificar si se clickeó un paquete
                val paqueteSeleccionado = mapaPaquetes[position]
                if (paqueteSeleccionado != null) {
                    mostrarConfirmacionEliminarPaqueteCompleto(context, nombreProyecto, paqueteSeleccionado, callback)
                }
            }
            .setNegativeButton("Atrás") { _, _ ->
                mostrarDialogoOpcionesProyecto(context, nombreProyecto, callback)
            }
            .show()
    }

    /**
     * NIVEL 3B: Mostrar lista de paquetes clickeables para eliminar
     */
    private fun mostrarDialogoPaquetesParaEliminar(
        context: Context,
        nombreProyecto: String,
        paquetes: List<String>,
        callback: ProyectoCallback
    ) {
        val elementosDescripcion = paquetes.map { paquete ->
            val tipoActivity = extraerTipoActivity(paquete)
            "$paquete ($tipoActivity)"
        }.toTypedArray()

        AlertDialog.Builder(context)
            .setTitle("Seleccionar Paquete para Eliminar")
            .setMessage("${paquetes.size} paquetes encontrados\nClick en el paquete que deseas eliminar:")
            .setItems(elementosDescripcion) { _, position ->
                val paqueteSeleccionado = paquetes[position]

                // Confirmación de eliminación
                AlertDialog.Builder(context)
                    .setTitle("Confirmar Eliminación")
                    .setMessage("¿Está seguro de eliminar el paquete '$paqueteSeleccionado'?\n\nEsta acción no se puede deshacer.")
                    .setPositiveButton("Eliminar") { _, _ ->
                        // Establecer temporalmente el proyecto
                        val proyectoTemp = ProyectoManager.getProyectoActivo()
                        ProyectoManager.setProyectoActivo(context, nombreProyecto)

                        val eliminado = ProyectoManager.eliminarPaquete(context, paqueteSeleccionado)

                        // Restaurar proyecto original
                        if (proyectoTemp != null) {
                            ProyectoManager.setProyectoActivo(context, proyectoTemp)
                        } else {
                            ProyectoManager.limpiarProyectoActivo(context)
                        }

                        if (eliminado) {
                            Toast.makeText(context, "Paquete '$paqueteSeleccionado' eliminado", Toast.LENGTH_SHORT).show()
                            // Actualizar callback para refrescar UI
                            callback.onProyectoSeleccionado(nombreProyecto)

                            // Volver a mostrar el contenido actualizado
                            mostrarDialogoElementosProyecto(context, nombreProyecto, callback)
                        } else {
                            Toast.makeText(context, "Error al eliminar el paquete", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancelar") { _, _ ->
                        // Volver al diálogo de selección de paquetes
                        mostrarDialogoPaquetesParaEliminar(context, nombreProyecto, paquetes, callback)
                    }
                    .show()
            }
            .setNegativeButton("Atrás") { _, _ ->
                mostrarDialogoElementosProyecto(context, nombreProyecto, callback)
            }
            .show()
    }

    /**
     * NIVEL 4: Opciones para un paquete específico (Eliminar/Explorar)
     */
    private fun mostrarDialogoOpcionesPaquete(
        context: Context,
        nombreProyecto: String,
        paquete: String,
        callback: ProyectoCallback
    ) {
        val opciones = arrayOf("🗑️ Eliminar Paquete", "🔍 Explorar Contenido")

        AlertDialog.Builder(context)
            .setTitle("Opciones: $paquete")
            .setMessage("Proyecto: $nombreProyecto")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> {
                        // Confirmar eliminación del paquete
                        AlertDialog.Builder(context)
                            .setTitle("Confirmar Eliminación")
                            .setMessage("¿Está seguro de eliminar el paquete '$paquete'?\n\nEsta acción no se puede deshacer.")
                            .setPositiveButton("Eliminar") { _, _ ->
                                // Establecer temporalmente el proyecto
                                val proyectoOriginal = ProyectoManager.getProyectoActivo()
                                ProyectoManager.setProyectoActivo(context, nombreProyecto)

                                val eliminado = ProyectoManager.eliminarPaquete(context, paquete)

                                // Restaurar proyecto original
                                if (proyectoOriginal != null) {
                                    ProyectoManager.setProyectoActivo(context, proyectoOriginal)
                                } else {
                                    ProyectoManager.limpiarProyectoActivo(context)
                                }

                                if (eliminado) {
                                    Toast.makeText(context, "Paquete '$paquete' eliminado", Toast.LENGTH_SHORT).show()
                                    // Actualizar callback para refrescar UI si es necesario
                                    if (nombreProyecto == ProyectoManager.getProyectoActivo()) {
                                        callback.onProyectoSeleccionado(nombreProyecto)
                                    }
                                } else {
                                    Toast.makeText(context, "Error al eliminar el paquete", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                    1 -> {
                        // Explorar contenido del paquete
                        mostrarDialogoContenidoPaquete(context, nombreProyecto, paquete, callback)
                    }
                }
            }
            .setNegativeButton("Atrás") { _, _ ->
                mostrarDialogoElementosProyecto(context, nombreProyecto, callback)
            }
            .show()
    }

    /**
     * NIVEL 5: Mostrar contenido completo de un paquete con opciones de edición
     */
    private fun mostrarDialogoContenidoPaquete(
        context: Context,
        nombreProyecto: String,
        paquete: String,
        callback: ProyectoCallback
    ) {
        // Establecer temporalmente el proyecto como activo
        val proyectoOriginal = ProyectoManager.getProyectoActivo()
        ProyectoManager.setProyectoActivo(context, nombreProyecto)

        val elementosPaquete = ProyectoManager.obtenerElementosPaquete(context, paquete)

        // Restaurar proyecto original
        if (proyectoOriginal != null) {
            ProyectoManager.setProyectoActivo(context, proyectoOriginal)
        } else {
            ProyectoManager.limpiarProyectoActivo(context)
        }

        if (elementosPaquete.isEmpty()) {
            Toast.makeText(context, "El paquete '$paquete' está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear texto de contenido para mostrar
        val contenidoTexto = StringBuilder()
        var totalElementos = 0

        for ((categoria, elementos) in elementosPaquete) {
            contenidoTexto.append("📁 $categoria:\n")
            elementos.forEach { elemento ->
                val valor1 = if (elemento.size > 0) elemento[0] else ""
                val valor2 = if (elemento.size > 1) elemento[1] else ""
                contenidoTexto.append("   • $valor1 = $valor2\n")
                totalElementos++
            }
            contenidoTexto.append("\n")
        }

        AlertDialog.Builder(context)
            .setTitle("📋 Contenido: $paquete")
            .setMessage("Proyecto: $nombreProyecto\nTotal elementos: $totalElementos\n\n$contenidoTexto")
            .setPositiveButton("✏️ Editar") { _, _ ->
                Toast.makeText(context, "Función de edición detallada en desarrollo", Toast.LENGTH_LONG).show()
                // TODO: Implementar editor detallado por elementos individuales
            }
            .setNeutralButton("🗑️ Eliminar Todo") { _, _ ->
                AlertDialog.Builder(context)
                    .setTitle("Confirmar Eliminación")
                    .setMessage("¿Eliminar todo el contenido del paquete '$paquete'?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        val proyectoTemp = ProyectoManager.getProyectoActivo()
                        ProyectoManager.setProyectoActivo(context, nombreProyecto)

                        val eliminado = ProyectoManager.eliminarPaquete(context, paquete)

                        if (proyectoTemp != null) {
                            ProyectoManager.setProyectoActivo(context, proyectoTemp)
                        } else {
                            ProyectoManager.limpiarProyectoActivo(context)
                        }

                        if (eliminado) {
                            Toast.makeText(context, "Paquete '$paquete' eliminado completamente", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            .setNegativeButton("Atrás") { _, _ ->
                mostrarDialogoOpcionesPaquete(context, nombreProyecto, paquete, callback)
            }
            .show()
    }

    // ==================== DIÁLOGOS ORIGINALES ====================

    /**
     * Diálogo para seleccionar proyecto existente
     */
    fun mostrarDialogoSeleccionarProyecto(context: Context, callback: ProyectoCallback) {
        val proyectos = MapStorage.obtenerListaProyectosConMetadata(context)

        if (proyectos.isEmpty()) {
            Toast.makeText(context, "No hay proyectos guardados. Cree uno nuevo.", Toast.LENGTH_SHORT).show()
            mostrarDialogoCrearProyecto(context, callback)
            return
        }

        val nombresProyectos = proyectos.map {
            "${it.nombre}\n${it.descripcion}\nVentanas: ${it.contadorVentanas} | ${it.fechaModificacion}"
        }
        val adapter = ArrayAdapter(context, android.R.layout.select_dialog_item, nombresProyectos)

        AlertDialog.Builder(context)
            .setTitle("Seleccionar Proyecto")
            .setAdapter(adapter) { _, position ->
                val proyectoSeleccionado = proyectos[position].nombre
                ProyectoManager.setProyectoActivo(context, proyectoSeleccionado)
                callback.onProyectoSeleccionado(proyectoSeleccionado)
                Toast.makeText(context, "Proyecto '$proyectoSeleccionado' activado", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Nuevo Proyecto") { _, _ ->
                mostrarDialogoCrearProyecto(context, callback)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Diálogo para crear nuevo proyecto
     */
    fun mostrarDialogoCrearProyecto(context: Context, callback: ProyectoCallback, nombreInicial: String = "") {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val labelNombre = TextView(context).apply {
            text = "Nombre del proyecto:"
            textSize = 16f
        }

        val editNombre = EditText(context).apply {
            hint = "Ej: Casa Juan Pérez"
            if (nombreInicial.isNotEmpty()) {
                setText(nombreInicial)
                setSelection(nombreInicial.length)
            }
        }

        val labelDescripcion = TextView(context).apply {
            text = "Descripción (opcional):"
            textSize = 16f
            setPadding(0, 20, 0, 0)
        }

        val editDescripcion = EditText(context).apply {
            hint = "Ej: Mamparas para sala y cocina"
        }

        layout.addView(labelNombre)
        layout.addView(editNombre)
        layout.addView(labelDescripcion)
        layout.addView(editDescripcion)

        AlertDialog.Builder(context)
            .setTitle("Crear Nuevo Proyecto")
            .setView(layout)
            .setPositiveButton("Crear") { _, _ ->
                val nombre = editNombre.text.toString().trim()
                val descripcion = editDescripcion.text.toString().trim()

                if (nombre.isEmpty()) {
                    Toast.makeText(context, "El nombre del proyecto es obligatorio", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (MapStorage.existeProyecto(context, nombre)) {
                    Toast.makeText(context, "Ya existe un proyecto con ese nombre", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val creado = MapStorage.crearProyecto(context, nombre, descripcion)
                if (creado) {
                    ProyectoManager.setProyectoActivo(context, nombre)
                    callback.onProyectoCreado(nombre)
                    Toast.makeText(context, "Proyecto '$nombre' creado y activado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error al crear el proyecto", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Diálogo de gestión completa (seleccionar, crear, eliminar)
     */
    fun mostrarDialogoGestionProyectos(context: Context, callback: ProyectoCallback) {
        val opciones = arrayOf("Seleccionar Proyecto", "Crear Nuevo Proyecto", "Eliminar Proyecto")

        AlertDialog.Builder(context)
            .setTitle("Gestión de Proyectos")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogoSeleccionarProyecto(context, callback)
                    1 -> mostrarDialogoCrearProyecto(context, callback)
                    2 -> mostrarDialogoEliminarProyecto(context, callback)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Diálogo para eliminar proyecto
     */
    fun mostrarDialogoEliminarProyecto(context: Context, callback: ProyectoCallback) {
        val proyectos = MapStorage.obtenerListaProyectosConMetadata(context)

        if (proyectos.isEmpty()) {
            Toast.makeText(context, "No hay proyectos para eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        val nombresProyectos = proyectos.map { "${it.nombre} (${it.contadorVentanas} ventanas)" }.toTypedArray()

        AlertDialog.Builder(context)
            .setTitle("Eliminar Proyecto")
            .setItems(nombresProyectos) { _, position ->
                val nombreProyecto = proyectos[position].nombre

                // Confirmación de eliminación
                AlertDialog.Builder(context)
                    .setTitle("Confirmar Eliminación")
                    .setMessage("¿Está seguro de eliminar el proyecto '$nombreProyecto'?\n\nEsta acción no se puede deshacer.")
                    .setPositiveButton("Eliminar") { _, _ ->
                        val eliminado = MapStorage.eliminarProyecto(context, nombreProyecto)
                        if (eliminado) {
                            callback.onProyectoEliminado(nombreProyecto)
                            Toast.makeText(context, "Proyecto '$nombreProyecto' eliminado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al eliminar el proyecto", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Diálogo para mostrar información del proyecto activo
     */
    fun mostrarInfoProyectoActivo(context: Context) {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        val metadata = ProyectoManager.getMetadataActual()

        if (proyectoActivo == null || metadata == null) {
            Toast.makeText(context, "No hay proyecto activo", Toast.LENGTH_SHORT).show()
            return
        }

        val totalPaquetes = ProyectoManager.obtenerTotalPaquetes(context)
        val listaPaquetes = ProyectoManager.obtenerListaPaquetes(context)

        val mensaje = """
            Proyecto: ${metadata.nombre}
            Descripción: ${metadata.descripcion.ifEmpty { "Sin descripción" }}
            
            Creado: ${metadata.fechaCreacion}
            Modificado: ${metadata.fechaModificacion}
            Total paquetes: $totalPaquetes
            
            Paquetes: ${listaPaquetes.joinToString(", ")}
        """.trimIndent()

        AlertDialog.Builder(context)
            .setTitle("Proyecto Activo")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
    }



    /**
     * Mostrar confirmación para eliminar un paquete completo
     */
    private fun mostrarConfirmacionEliminarPaqueteCompleto(
        context: Context,
        nombreProyecto: String,
        paquete: String,
        callback: ProyectoCallback
    ) {
        val tipoActivity = extraerTipoActivity(paquete)

        AlertDialog.Builder(context)
            .setTitle("Confirmar Eliminación de Paquete")
            .setMessage("¿Está seguro de eliminar el paquete completo?\n\n" +
                    "Paquete: $paquete ($tipoActivity)\n" +
                    "Proyecto: $nombreProyecto\n\n" +
                    "Esta acción eliminará todo el contenido del paquete y no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarPaqueteCompleto(context, nombreProyecto, paquete, callback)
            }
            .setNegativeButton("Cancelar") { _, _ ->
                // Volver a mostrar el diálogo de elementos
                mostrarDialogoElementosProyecto(context, nombreProyecto, callback)
            }
            .show()
    }

    /**
     * Eliminar un paquete completo
     */
    private fun eliminarPaqueteCompleto(
        context: Context,
        nombreProyecto: String,
        paquete: String,
        callback: ProyectoCallback
    ) {
        // Establecer temporalmente el proyecto
        val proyectoOriginal = ProyectoManager.getProyectoActivo()
        ProyectoManager.setProyectoActivo(context, nombreProyecto)

        try {
            val eliminado = ProyectoManager.eliminarPaquete(context, paquete)

            if (eliminado) {
                Toast.makeText(context, "Paquete '$paquete' eliminado correctamente", Toast.LENGTH_SHORT).show()

                // Actualizar callback para refrescar UI
                callback.onProyectoSeleccionado(nombreProyecto)

                // Volver a mostrar el contenido actualizado
                mostrarDialogoElementosProyecto(context, nombreProyecto, callback)
            } else {
                Toast.makeText(context, "Error al eliminar el paquete", Toast.LENGTH_SHORT).show()
                // Volver a mostrar el diálogo
                mostrarDialogoElementosProyecto(context, nombreProyecto, callback)
            }

        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            // Volver a mostrar el diálogo
            mostrarDialogoElementosProyecto(context, nombreProyecto, callback)
        } finally {
            // Restaurar proyecto original
            if (proyectoOriginal != null) {
                ProyectoManager.setProyectoActivo(context, proyectoOriginal)
            } else {
                ProyectoManager.limpiarProyectoActivo(context)
            }
        }
    }

    // ==================== FUNCIONES AUXILIARES PRIVADAS ====================

    /**
     * Función auxiliar para contar paquetes en un proyecto específico
     */
    private fun contarPaquetesEnProyecto(context: Context, nombreProyecto: String): Int {
        val proyectoOriginal = ProyectoManager.getProyectoActivo()
        ProyectoManager.setProyectoActivo(context, nombreProyecto)

        val total = ProyectoManager.obtenerTotalPaquetes(context)

        if (proyectoOriginal != null) {
            ProyectoManager.setProyectoActivo(context, proyectoOriginal)
        } else {
            ProyectoManager.limpiarProyectoActivo(context)
        }

        return total
    }
    /**
     * Función auxiliar para determinar el tipo de activity basado en el prefijo del paquete
     */
    private fun extraerTipoActivity(paquete: String): String {
        return when {
            paquete.contains("NA") -> "NovaCorrediza"
            paquete.contains("NI") -> "NovaCorrediza"
            paquete.startsWith("p") && paquete.contains("PD") -> "PDucha"
            paquete.startsWith("p") && !paquete.contains("PD") -> "Puertas"
            paquete.contains("MP") -> "MamparaPaflon"
            paquete.contains("VA") -> "VentanaAl"
            paquete.contains("MV") -> "MamparaVidrio"
            paquete.contains("MU") -> "Muro"
            else -> "Desconocido"
        }
    }

    /**
     * Data class para representar elementos clickeables en el diálogo
     */
    data class ElementoClickeable(
        val texto: String,
        val paquete: String,
        val categoria: String,
        val elementoCompleto: MutableList<String>,
        val esClickeable: Boolean
    )
}