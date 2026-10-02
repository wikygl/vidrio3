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
data class OpcionDeProforma(
    val producto: String,
    val precio: Float,
    /**
     * La imagen de ESTA opción: casi siempre las hay, porque un arenado laminado y un
     * policarbonato no se parecen en nada y el cliente elige mirando. Vacío si no tiene.
     */
    val imagen: String = ""
)

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
    /**
     * Las imágenes de un anexo van una por línea, y al elegir otra opción las del ítem pasan a ser
     * las de una opción: el salto de línea se guarda con este carácter para que no se pierdan.
     */
    private const val ENTRE_IMAGENES = "\u001F"

    /** Las opciones de ese ítem, en el orden en que se escribieron. Vacío si no tiene. */
    fun de(item: Listado): List<OpcionDeProforma> = desdeTexto(item.opciones)

    fun desdeTexto(texto: String?): List<OpcionDeProforma> {
        if (texto.isNullOrBlank()) return emptyList()
        return texto.split(ENTRE_OPCIONES).mapNotNull { trozo ->
            val partes = trozo.split(ENTRE_CAMPOS)
            if (partes.size < 2) return@mapNotNull null
            val producto = partes[0].trim()
            val precio = partes[1].trim().replace(",", ".").toFloatOrNull()
            // La imagen llegó después: las opciones escritas antes traen solo dos campos y se leen
            // igual, sin ella.
            val imagen = partes.getOrNull(2)?.trim().orEmpty().replace(ENTRE_IMAGENES, "\n")
            if (producto.isEmpty() || precio == null || precio < 0f) null
            else OpcionDeProforma(producto, precio, imagen)
        }
    }

    /** null cuando no hay ninguna: así el ítem se guarda como se guardaba antes. */
    fun aTexto(opciones: List<OpcionDeProforma>): String? {
        if (opciones.isEmpty()) return null
        return opciones.joinToString(ENTRE_OPCIONES) { opcion ->
            // Ni el nombre ni la ruta pueden llevar los separadores, o al leerlo se partiría por
            // donde no es.
            val limpio = sinSeparadores(opcion.producto)
            val imagen = sinSeparadores(opcion.imagen.trim().replace("\n", ENTRE_IMAGENES))
            if (imagen.isEmpty()) "$limpio$ENTRE_CAMPOS${opcion.precio}"
            else "$limpio$ENTRE_CAMPOS${opcion.precio}$ENTRE_CAMPOS$imagen"
        }
    }

    private fun sinSeparadores(texto: String) =
        texto.replace(ENTRE_OPCIONES, " ").replace(ENTRE_CAMPOS, " ").replace("\n", " ").trim()

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

    /**
     * TODAS las opciones del ítem en el orden de la proforma de elección —A, B, C…—, cada una con
     * su imagen. La A es lo que se apuntó al medir; si ya se eligió otra, la escogida está en el
     * ítem y la A guardada en su sitio, y aquí cada una vuelve a su letra.
     */
    fun todas(item: Listado): List<OpcionDeProforma> {
        val guardadas = mutableListOf(comoEstaApuntado(item).copy(imagen = item.uri))
        guardadas.addAll(de(item))
        val k = elegida(item)
        if (k != null && k != 0) java.util.Collections.swap(guardadas, 0, k)
        return guardadas
    }

    /** La letra que escogió el cliente (0 es la A), o null si todavía no eligió. */
    fun elegida(item: Listado): Int? {
        val cuantas = de(item).size
        if (cuantas == 0) return null
        return item.opcionElegida?.takeIf { it in 0..cuantas }
    }

    /**
     * Deja el ítem con [todas] sus opciones y con la [elegida] puesta en él: su producto, su
     * precio, su imagen y su costo son los de la escogida —o los de la A, si no hay—, y las demás
     * quedan guardadas como opciones. Así la proforma que suma, el total y el contrato salen con
     * lo que escogió el cliente sin saber nada de opciones.
     */
    fun ponerTodas(item: Listado, todas: List<OpcionDeProforma>, elegida: Int?) {
        if (todas.isEmpty()) return
        val k = elegida?.takeIf { it in todas.indices && todas.size > 1 }
        val guardadas = todas.toMutableList()
        if (k != null && k != 0) java.util.Collections.swap(guardadas, 0, k)
        val enElItem = guardadas.removeAt(0)
        // El costo solo se rehace si el ítem cambia de material o de precio: uno que llegó de una
        // calculadora con su costo hecho no se toca por añadirle una opción o una foto.
        if (enElItem.producto != item.producto || enElItem.precio != item.precio) {
            item.costo = precioPorLaCantidad(item, enElItem)
        }
        item.producto = enElItem.producto
        item.precio = enElItem.precio
        item.uri = enElItem.imagen
        guardar(item, guardadas)
        item.opcionElegida = k
    }

    /** El cliente escogió la opción [cual] (0 es la A); null deja el ítem sin elegir, con la A. */
    fun elegir(item: Listado, cual: Int?) = ponerTodas(item, todas(item), cual)

    /** Cuántos ítems tienen opciones y todavía no se sabe cuál escogió el cliente. */
    fun faltanElegir(lista: List<Listado>): Int =
        lista.count { de(it).isNotEmpty() && elegida(it) == null }

    /** La letra con que la proforma de elección nombra la opción [orden]: 0 es la A. */
    fun letra(orden: Int): String = ('A' + orden).toString()
}
