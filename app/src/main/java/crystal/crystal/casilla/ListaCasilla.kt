package crystal.crystal.casilla

import android.content.Context
import android.widget.TextView

object ListaCasilla {

    var contadorVentanas = 0

    fun obtenerNombreLista(tv: TextView): String {
        return tv.text.toString()
    }

    fun procesarTextoTxU(tv: TextView): MutableList<MutableList<String>> {
        val lista = mutableListOf<MutableList<String>>()

        val lineas = tv.text.toString().split("\n")

        for (linea in lineas) {
            val partes = linea.split("=")
            if (partes.size == 2) {
                val primerElemento = partes[0].trim()
                val segundoElemento = partes[1].trim()
                if (esEntradaVacia(primerElemento, segundoElemento)) continue
                lista.add(mutableListOf(primerElemento, segundoElemento, ""))
            }
        }
        return lista
    }

    /**
     * Descarta entradas con valor 0 o cantidad vacía/0.
     * Valores tipo "45x30" (vidrios) NO se descartan.
     */
    private fun esEntradaVacia(valor: String, cantidad: String): Boolean {
        if (valor.isBlank() || cantidad.isBlank()) return true
        val cantidadNum = cantidad.toIntOrNull()
        if (cantidadNum == null || cantidadNum == 0) return true
        // Solo filtrar por valor 0 si es un número simple (no dimensiones tipo "45x30")
        val valorNum = valor.toFloatOrNull()
        if (valorNum != null && valorNum == 0f) return true
        return false
    }

    fun agregarTercerElemento(context: Context, lista: MutableList<MutableList<String>>) {
        val contadorActual = ProyectoManager.getContadorVentanas()
        val tercerElemento = "v$contadorActual"

        for (sublista in lista) {
            sublista[2] = tercerElemento
        }
    }

    fun incrementarContadorVentanas(context: Context) {
        ProyectoManager.incrementarContadorVentanas(context)

        // Mantener sincronizado el contador global para compatibilidad
        contadorVentanas = ProyectoManager.getContadorVentanas()
    }

    fun procesarArchivar(
        context: Context,
        tvNombre: TextView,
        tvDatos: TextView,
        mapListas: MutableMap<String, MutableList<MutableList<String>>>
    ) {
        val nombreLista = obtenerNombreLista(tvNombre)
        val listaProcesada = procesarTextoTxU(tvDatos)
        agregarTercerElemento(context, listaProcesada)

        // Verificar si la lista ya existe en el Map
        if (mapListas.containsKey(nombreLista)) {
            mapListas[nombreLista]?.addAll(listaProcesada)
        } else {
            mapListas[nombreLista] = listaProcesada
        }
    }

    // Función especial para procesar txReferencias sin el signo "="
    fun procesarReferencias(
        context: Context,
        tvNombre: TextView,
        txReferencias: TextView,
        mapListas: MutableMap<String, MutableList<MutableList<String>>>
    ) {
        val nombreLista = obtenerNombreLista(tvNombre)
        val contadorActual = ProyectoManager.getContadorVentanas()
        val listaProcesada = mutableListOf(mutableListOf(txReferencias.text.toString(), "v${contadorActual}"))

        if (mapListas.containsKey(nombreLista)) {
            mapListas[nombreLista]?.addAll(listaProcesada)
        } else {
            mapListas[nombreLista] = listaProcesada
        }
    }

    @Deprecated("Usar procesarArchivar(context, tvNombre, tvDatos, mapListas) en su lugar")
    fun procesarArchivar(
        tvNombre: TextView,
        tvDatos: TextView,
        mapListas: MutableMap<String, MutableList<MutableList<String>>>
    ) {
        val nombreLista = obtenerNombreLista(tvNombre)
        val listaProcesada = procesarTextoTxU(tvDatos)
        agregarTercerElementoLegacy(listaProcesada, contadorVentanas)

        if (mapListas.containsKey(nombreLista)) {
            mapListas[nombreLista]?.addAll(listaProcesada)
        } else {
            mapListas[nombreLista] = listaProcesada
        }
    }

    fun procesarArchivarConPrefijo(
        context: Context,
        tvNombre: TextView,
        tvDatos: TextView,
        mapListas: MutableMap<String, MutableList<MutableList<String>>>,
        identificadorPaquete: String,
        color: String = ""
    ) {
        val nombreBase = obtenerNombreLista(tvNombre)
        val nombreLista = if (color.isNotBlank()) "$nombreBase $color" else nombreBase
        val listaProcesada = procesarTextoTxU(tvDatos)
        if (listaProcesada.isEmpty()) return // No crear entrada vacía
        agregarIdentificadorPaquete(listaProcesada, identificadorPaquete)

        if (mapListas.containsKey(nombreLista)) {
            mapListas[nombreLista]?.addAll(listaProcesada)
        } else {
            mapListas[nombreLista] = listaProcesada
        }
    }

    fun procesarReferenciasConPrefijo(
        context: Context,
        tvNombre: TextView,
        txReferencias: TextView,
        mapListas: MutableMap<String, MutableList<MutableList<String>>>,
        identificadorPaquete: String
    ) {
        // El recycler (FichaActivity) y todo el pipeline reconocen las referencias por el
        // nombre literal "Referencias". Si se usara la etiqueta del título (p. ej.
        // "Referencias y Cálculos") la lista se descarta al filtrar y no se muestra.
        // Por eso el nombre se fija aquí, no se toma de la etiqueta del título.
        val nombreLista = "Referencias"
        val listaProcesada = mutableListOf(mutableListOf(txReferencias.text.toString(), "", identificadorPaquete))

        if (mapListas.containsKey(nombreLista)) {
            mapListas[nombreLista]?.addAll(listaProcesada)
        } else {
            mapListas[nombreLista] = listaProcesada
        }
    }

    /** Un material a archivar: su etiqueta (nombre de lista), su valor y si está visible. */
    class ItemArchivable(val tvNombre: TextView, val tvDatos: TextView, val visible: Boolean)

    /**
     * Archivado project-aware compartido (estilo Nova). Sirve para cualquier calculadora
     * (Nova, Vitroven, ...) y evita duplicar el bucle de archivado:
     *  1. Carga el proyecto activo en [mapListas].
     *  2. Por cada unidad genera un identificador (prefijo + contador por prefijo).
     *  3. Archiva referencias y cada material visible con ese identificador.
     *  4. Añade el cliente y los paquetes de diseño que se pasen.
     *  5. Guarda el map (que respalda a disco) y devuelve el último identificador generado.
     */
    fun archivarEnProyectoActivo(
        context: Context,
        mapListas: MutableMap<String, MutableList<MutableList<String>>>,
        prefijo: String,
        cantidad: Int,
        referencias: ItemArchivable?,
        items: List<ItemArchivable>,
        cliente: String? = null,
        paquetesPorNumero: (numero: Int) -> Map<String, String> = { emptyMap() }
    ): String {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val existente = MapStorage.cargarProyecto(context, proyectoActivo)
            mapListas.clear()
            if (existente != null) mapListas.putAll(existente)
        }
        var ultimoID = ""
        val n = cantidad.coerceAtLeast(1)
        for (u in 1..n) {
            val numero = ProyectoManager.obtenerSiguienteContadorPorPrefijo(context, prefijo)
            val id = "$prefijo$numero"
            ultimoID = id
            referencias?.let {
                if (it.visible) procesarReferenciasConPrefijo(context, it.tvNombre, it.tvDatos, mapListas, id)
            }
            for (item in items) {
                if (item.visible) procesarArchivarConPrefijo(context, item.tvNombre, item.tvDatos, mapListas, id)
            }
            if (!cliente.isNullOrBlank()) {
                mapListas.getOrPut("Cliente") { mutableListOf() }.add(mutableListOf(cliente, "", id))
            }
            for ((clave, valor) in paquetesPorNumero(numero)) {
                if (valor.isNotBlank()) {
                    mapListas.getOrPut(clave) { mutableListOf() }.add(mutableListOf(valor, "", id))
                }
            }
            ProyectoManager.actualizarContadorPorPrefijo(context, prefijo, numero)
        }
        MapStorage.guardarMap(context, mapListas)
        return ultimoID
    }

    private fun agregarIdentificadorPaquete(lista: MutableList<MutableList<String>>, identificadorPaquete: String) {
        for (sublista in lista) {
            // Asegurar que la sublista tenga al menos 3 elementos
            while (sublista.size < 3) {
                sublista.add("")
            }
            sublista[2] = identificadorPaquete
        }
    }

    fun eliminarElementoDelMap(
        mapListas: MutableMap<String, MutableList<MutableList<String>>>,
        categoria: String,
        indiceElemento: Int
    ): Boolean {
        val lista = mapListas[categoria] ?: return false

        return if (indiceElemento >= 0 && indiceElemento < lista.size) {
            lista.removeAt(indiceElemento)

            // Si la categoría queda vacía, eliminarla completamente
            if (lista.isEmpty()) {
                mapListas.remove(categoria)
            }
            true
        } else {
            false
        }
    }

    fun editarElementoEnMap(
        mapListas: MutableMap<String, MutableList<MutableList<String>>>,
        categoria: String,
        indiceElemento: Int,
        nuevosValores: MutableList<String>
    ): Boolean {
        val lista = mapListas[categoria] ?: return false

        return if (indiceElemento >= 0 && indiceElemento < lista.size) {
            // Asegurar que nuevosValores tenga al menos 3 elementos
            while (nuevosValores.size < 3) {
                nuevosValores.add("")
            }
            lista[indiceElemento] = nuevosValores
            true
        } else {
            false
        }
    }

    fun obtenerElementosCategoriaPaquete(
        context: Context,
        identificadorPaquete: String,
        categoria: String
    ): MutableList<MutableList<String>> {
        val elementosPaquete = ProyectoManager.obtenerElementosPaquete(context, identificadorPaquete)
        return elementosPaquete[categoria] ?: mutableListOf()
    }


    fun incrementarContadorVentanas() {
        contadorVentanas++
    }

    @Deprecated("Usar agregarTercerElemento(context, lista) en su lugar")
    fun agregarTercerElemento(lista: MutableList<MutableList<String>>, contador: Int) {
        agregarTercerElementoLegacy(lista, contador)
    }

    private fun agregarTercerElementoLegacy(lista: MutableList<MutableList<String>>, contador: Int) {
        val tercerElemento = "v$contador"
        for (sublista in lista) {
            while (sublista.size < 3) {
                sublista.add("")
            }
            sublista[2] = tercerElemento
        }
    }
}