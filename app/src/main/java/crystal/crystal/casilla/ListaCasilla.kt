package crystal.crystal.casilla

import android.widget.TextView

object ListaCasilla {

    var contadorVentanas = 0 // Contador global de ventanas

    // Función para obtener el nombre de la lista desde cualquier TextView
    fun obtenerNombreLista(tv: TextView): String {
        return tv.text.toString()
    }

    // Función para procesar el contenido de cualquier TextView (datos)
    fun procesarTextoTxU(tv: TextView): MutableList<MutableList<String>> {
        val lista = mutableListOf<MutableList<String>>()

        // Separar el texto por saltos de línea
        val lineas = tv.text.toString().split("\n")

        // Procesar cada línea dividiendo por el símbolo "="
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

    // Función para agregar el tercer elemento sin incrementar el contador
    fun agregarTercerElemento(lista: MutableList<MutableList<String>>, contador: Int) {
        val tercerElemento = "v$contador" // Usa el contador actual

        for (sublista in lista) {
            sublista[2] = tercerElemento
        }
    }

    // Función que incrementa el contador cuando se hace clic en `btAbrir`
    fun incrementarContadorVentanas() {
        contadorVentanas++ // Incrementa solo cuando se hace clic en btAbrir
    }

    // Función principal que procesa y guarda las listas en el Map
    fun procesarArchivar(
        tvNombre: TextView,
        tvDatos: TextView,
        mapListas: MutableMap<String, MutableList<MutableList<String>>>
    ) {
        val nombreLista = obtenerNombreLista(tvNombre) // Nombre de la lista
        val listaProcesada = procesarTextoTxU(tvDatos) // Procesar los datos del TextView
        agregarTercerElemento(listaProcesada, contadorVentanas) // Usar el contador actual

        // Verificar si la lista ya existe en el Map
        if (mapListas.containsKey(nombreLista)) {
            // Si existe, agregar los nuevos datos a la lista existente
            mapListas[nombreLista]?.addAll(listaProcesada)
        } else {
            // Si no existe, crear una nueva entrada en el Map
            mapListas[nombreLista] = listaProcesada        }
    }

    // Función especial para procesar txReferencias sin el signo "="
    fun procesarReferencias(
        tvNombre: TextView,
        txReferencias: TextView,
        mapListas: MutableMap<String, MutableList<MutableList<String>>>
    ) {
        val nombreLista = obtenerNombreLista(tvNombre) // Obtener el nombre de la lista desde tvNombre
        val listaProcesada = mutableListOf(mutableListOf(txReferencias.text.toString(), "v${contadorVentanas}")) // Crear la lista de [txReferencias, contadorVentanas]

        // Verificar si ya existe una lista con ese nombre en el Map
        if (mapListas.containsKey(nombreLista)) {
            // Si existe, agregar los nuevos datos a la lista existente
            mapListas[nombreLista]?.addAll(listaProcesada)
        } else {
            // Si no existe, crear una nueva entrada en el Map
            mapListas[nombreLista] = listaProcesada
        }
    }
}




