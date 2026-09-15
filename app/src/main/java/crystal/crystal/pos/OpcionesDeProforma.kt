package crystal.crystal.pos

import crystal.crystal.Listado

/**
 * Una opción de un ítem del presupuesto: el mismo producto medido, ofrecido con otro material.
 *
 * Es lo que se lleva a una proforma de ELECCIÓN: la misma ventana en vidrio arenado laminado, en
 * policarbonato o en serie 80, cada una con su precio, para que el cliente escoja una. Por eso una
 * proforma de opciones no suma: sumar las tres sería cobrarlas todas.
 *
 * [precio] es el precio por la unidad de la escala del ítem —el m², el pie cuadrado, el metro
 * lineal o la unidad—, igual que el `precio` de siempre de [Listado]: así el importe de cada opción
 * sale con la misma cuenta y no hay dos maneras de calcular lo mismo.
 */
data class OpcionDeProforma(val producto: String, val precio: Float)

/**
 * Las opciones de un ítem, guardadas en su propia línea del presupuesto.
 *
 * Van como TEXTO dentro de [Listado.opciones] —`producto|precio;producto|precio`— y no como una
 * lista de objetos, por lo mismo que el diseño simbólico de Nova es una cadena: el presupuesto se
 * guarda con serialización Java y con Gson, y un texto viaja por los dos sin estrenar clases en la
 * foto de lo guardado. Los presupuestos de antes, que no lo llevan, se leen igual: sin opciones.
 */
object OpcionesDeProforma {

    private const val ENTRE_OPCIONES = ";"
    private const val ENTRE_CAMPOS = "|"

    /** Las opciones de ese ítem, en el orden en que se escribieron. Vacío si no tiene. */
    fun de(item: Listado): List<OpcionDeProforma> = desdeTexto(item.opciones)

    fun desdeTexto(texto: String?): List<OpcionDeProforma> {
        if (texto.isNullOrBlank()) return emptyList()
        return texto.split(ENTRE_OPCIONES).mapNotNull { trozo ->
            val partes = trozo.split(ENTRE_CAMPOS)
            if (partes.size < 2) return@mapNotNull null
            val producto = partes[0].trim()
            val precio = partes[1].trim().replace(",", ".").toFloatOrNull()
            if (producto.isEmpty() || precio == null || precio < 0f) null
            else OpcionDeProforma(producto, precio)
        }
    }

    /** null cuando no hay ninguna: así el ítem se guarda como se guardaba antes. */
    fun aTexto(opciones: List<OpcionDeProforma>): String? {
        if (opciones.isEmpty()) return null
        return opciones.joinToString(ENTRE_OPCIONES) { opcion ->
            // El nombre no puede llevar los separadores, o al leerlo se partiría por donde no es.
            val limpio = opcion.producto.replace(ENTRE_OPCIONES, " ").replace(ENTRE_CAMPOS, " ").trim()
            "$limpio$ENTRE_CAMPOS${opcion.precio}"
        }
    }

    /** Guarda las opciones en el ítem. */
    fun guardar(item: Listado, opciones: List<OpcionDeProforma>) {
        item.opciones = aTexto(opciones)
    }

    /** ¿Hay alguna línea del presupuesto con opciones? Es lo que decide si cabe la proforma. */
    fun hayEnLaLista(lista: List<Listado>): Boolean = lista.any { de(it).isNotEmpty() }

    /**
     * Lo que cuesta UNA unidad del ítem con esa opción.
     *
     * Se calcula como el costo de siempre: por la escala en la que se midió. En "uni" el precio ya
     * es el de la unidad; en las demás, lo que mida una pieza por su precio.
     */
    fun precioUnitario(item: Listado, opcion: OpcionDeProforma): Float {
        val cantidad = if (item.canti > 0f) item.canti else 1f
        return when (item.escala) {
            "p2" -> (item.piescua / cantidad) * opcion.precio
            "m2" -> (item.metcua / cantidad) * opcion.precio
            "ml" -> (item.metli / cantidad) * opcion.precio
            "m3" -> (item.metcub / cantidad) * opcion.precio
            else -> opcion.precio
        }
    }

    /** Y lo que cuestan todas las que se pidieron de ese ítem con esa opción. */
    fun precioPorLaCantidad(item: Listado, opcion: OpcionDeProforma): Float {
        val cantidad = if (item.canti > 0f) item.canti else 1f
        return precioUnitario(item, opcion) * cantidad
    }

    /** La opción del ítem tal como está apuntado hoy: su producto y su precio de siempre. */
    fun comoEstaApuntado(item: Listado) = OpcionDeProforma(item.producto, item.precio)
}
