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

        // Separar el texto por saltos de lÃ­nea
        val lineas = tv.text.toString().split("\n")

        // Procesar cada lÃ­nea dividiendo por el sÃ­mbolo "="
        for (linea in lineas) {
            val partes = linea.split("=")
            if (partes.size == 2) {
                val primerElemento = partes[0]
                val segundoElemento = partes[1]
                lista.add(mutableListOf(primerElemento, segundoElemento, ""))
            }
        }
        return lista
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

    // FunciÃ³n especial para procesar txReferencias sin el signo "="
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
        identificadorPaquete: String
    ) {
        val nombreLista = obtenerNombreLista(tvNombre)
        val listaProcesada = procesarTextoTxU(tvDatos)
        agregarIdentificadorPaquete(listaProcesada, identificadorPaquete)

        // Verificar si la lista ya existe en el Map
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
        val nombreLista = obtenerNombreLista(tvNombre)
        val listaProcesada = mutableListOf(mutableListOf(txReferencias.text.toString(), "", identificadorPaquete))

        if (mapListas.containsKey(nombreLista)) {
            mapListas[nombreLista]?.addAll(listaProcesada)
        } else {
            mapListas[nombreLista] = listaProcesada
        }
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