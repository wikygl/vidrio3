package crystal.crystal.pos

import crystal.crystal.Listado

/**
 * Los ambientes de un presupuesto: la sala, el consultorio, el piso donde va cada ítem.
 *
 * Un trabajo de obra entera —una clínica, un hotel, un edificio— no se lee como una lista corrida
 * de medidas: se lee por sitios. "Sala de partos" con lo que lleva, "sala del sexto piso" con lo
 * suyo, y así. Aquí se agrupan los ítems por su ambiente para poder imprimirlos de esa manera.
 *
 * El orden manda: los ambientes salen como se midieron —el orden en que aparecen en la lista—, no
 * en orden alfabético. Se midió sala por sala y así es como el cliente recorre el papel.
 */
object AmbientesDeProforma {

    /** Lo que se escribe cuando un ítem no está en ningún ambiente. */
    const val SIN_AMBIENTE = ""

    /** El ambiente de ese ítem, ya limpio; vacío si no tiene. */
    fun de(item: Listado): String = item.ambiente?.trim().orEmpty()

    /** Los ambientes que hay en la lista, en el orden en que aparecen y sin repetir. */
    fun enLaLista(lista: List<Listado>): List<String> =
        lista.map { de(it) }.filter { it.isNotEmpty() }.distinct()

    /** ¿Vale la pena imprimir por ambientes? Solo si alguno tiene. */
    fun hayEnLaLista(lista: List<Listado>): Boolean = lista.any { de(it).isNotEmpty() }

    /**
     * Los ítems agrupados por ambiente, en el orden en que se midieron.
     *
     * Los que no tienen ambiente van juntos AL FINAL, con el nombre vacío: son los que se apuntaron
     * sin decir dónde, y perderlos por el camino sería peor que enseñarlos sueltos.
     */
    fun agrupar(lista: List<Listado>): List<Pair<String, List<Listado>>> {
        val orden = enLaLista(lista)
        val grupos = orden.map { ambiente -> ambiente to lista.filter { de(it) == ambiente } }
        val sueltos = lista.filter { de(it).isEmpty() }
        return if (sueltos.isEmpty()) grupos else grupos + (SIN_AMBIENTE to sueltos)
    }

    /** Pone el ambiente a un ítem. En blanco, se lo quita. */
    fun guardar(item: Listado, ambiente: String) {
        item.ambiente = ambiente.trim().takeIf { it.isNotEmpty() }
    }
}
