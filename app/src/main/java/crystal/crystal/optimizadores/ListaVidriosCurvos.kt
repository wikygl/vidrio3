package crystal.crystal.optimizadores

import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MetadatosProduccion

/**
 * La lista de vidrios curvos para mandar al curvador o al horno (pedido del usuario el 2026-10-01).
 *
 * Junta, de los proyectos elegidos, cada vidrio que se archivó con la marca de curvo (N-102:
 * `60.4 x 126.9 = 4 curvo C59.4 F4.5`): su desarrollo (el ancho, que es el arco), su alto, la
 * cantidad, la cuerda, la flecha y el radio, el tipo de vidrio de su ventana y de qué ventana y
 * proyecto es.
 */
object ListaVidriosCurvos {

    data class VidrioCurvo(
        val proyecto: String,
        val ventana: String,
        val tipo: String,
        val desarrollo: Float,
        val alto: Float,
        val cantidad: Int,
        val cuerda: Float?,
        val flecha: Float?,
        /** Hacia dónde va la panza, mirando desde adentro: "afuera", "adentro" o vacío si no se sabe. */
        val panza: String = "",
        /** Cómo queda la cara especial ("espejo convexa"), "sin caras", o vacío si no se dijo al archivar. */
        val cara: String = "",
        /** Dónde está archivado, para cambiarle la cara sin recalcular: la lista y su posición. */
        val lista: String = "",
        val indice: Int = -1
    ) {
        /** El radio, de la cuerda y la flecha. */
        val radio: Float?
            get() = if (cuerda != null && flecha != null && flecha > 0f) (cuerda * cuerda / 4f + flecha * flecha) / (2f * flecha) else null
    }

    private val dosMedidas = Regex("""^\s*([\d.,]+)\s*[xX×]\s*([\d.,]+)\s*$""")

    private fun numero(s: String?) = s?.trim()?.replace(",", ".")?.toFloatOrNull()

    /** Los vidrios curvos de cada proyecto ([proyectos]: nombre → sus listas archivadas). */
    fun de(proyectos: Map<String, Map<String, List<List<String>>>>): List<VidrioCurvo> =
        proyectos.flatMap { (proyecto, listas) ->
            val tipos = MetadatosProduccion.mapaPorVentana(listas)
            listas.flatMap { (nombreLista, entradas) ->
                entradas.mapIndexedNotNull { i, e ->
                    if (!ListaCasilla.esCurva(e)) return@mapIndexedNotNull null
                    val m = dosMedidas.find(e.getOrNull(0).orEmpty()) ?: return@mapIndexedNotNull null
                    val cantidad = e.getOrNull(1)?.trim()?.toIntOrNull()?.takeIf { it > 0 } ?: return@mapIndexedNotNull null
                    val id = e.getOrNull(2).orEmpty().trim()
                    VidrioCurvo(
                        proyecto = proyecto,
                        // Solo el número de la ventana: los ids viejos traen el cliente o el proyecto detrás.
                        ventana = id.split(',', ' ').first().trim(),
                        tipo = tipos[id]?.second.orEmpty(),
                        desarrollo = numero(m.groupValues[1]) ?: return@mapIndexedNotNull null,
                        alto = numero(m.groupValues[2]) ?: return@mapIndexedNotNull null,
                        cantidad = cantidad,
                        cuerda = numero(e.getOrNull(ListaCasilla.POS_CUERDA)),
                        flecha = numero(e.getOrNull(ListaCasilla.POS_FLECHA)),
                        panza = e.getOrNull(ListaCasilla.POS_PANZA).orEmpty().trim(),
                        cara = e.getOrNull(ListaCasilla.POS_CARA).orEmpty().trim(),
                        lista = nombreLista,
                        indice = i
                    )
                }
            }
        // Por cara (las cóncavas juntas, las convexas juntas): son piezas distintas para el curvador.
        }.sortedWith(compareBy({ it.proyecto }, { it.cara }, { numeroDeVentana(it.ventana) }, { it.ventana }))

    // ==================== CAMBIAR DE CARA ====================
    // Si al archivar se dijo mal qué cara se ve desde adentro, se corrige aquí sin rehacer el cálculo
    // (pedido del usuario el 2026-10-01): en la vista de planta se toca el nombre de la cara de
    // afuera y pasa a la otra; con eso la cara especial cambia de cóncava a convexa o al revés.

    const val SIN_CARAS = "sin caras"

    /** La cara especial ("espejo"); null si el vidrio no tiene caras dichas. */
    fun caraEspecial(v: VidrioCurvo): String? =
        v.cara.takeIf { it.isNotBlank() && it != SIN_CARAS }?.substringBeforeLast(' ')?.takeIf { esConvexa(v) != null }

    /** Si la cara especial queda convexa; null si no se sabe. */
    fun esConvexa(v: VidrioCurvo): Boolean? = when (v.cara.substringAfterLast(' ')) {
        "convexa" -> true
        "cóncava" -> false
        else -> null
    }

    /** La otra cara del vidrio: del espejo, el color. */
    fun otraCara(especial: String): String = if (especial.equals("espejo", ignoreCase = true)) "color" else "otra cara"

    /**
     * La cara que queda afuera. Con la panza hacia afuera la de afuera es la convexa; hacia adentro,
     * la cóncava. Null si el vidrio no tiene caras dichas.
     */
    fun caraAfuera(v: VidrioCurvo): String? {
        val especial = caraEspecial(v) ?: return null
        val convexa = esConvexa(v) ?: return null
        val afueraEsConvexa = v.panza != "adentro"
        return if (convexa == afueraEsConvexa) especial else otraCara(especial)
    }

    /**
     * La cara al revés: la especial pasa al otro lado y su clasificación cambia (la panza es la misma).
     * Sin caras dichas, empieza con el espejo afuera.
     */
    fun alternarCara(v: VidrioCurvo): String {
        val especial = caraEspecial(v)
        val convexa = esConvexa(v)
        if (especial == null || convexa == null) return "espejo ${if (v.panza != "adentro") "convexa" else "cóncava"}"
        return "$especial ${if (convexa) "cóncava" else "convexa"}"
    }

    /** "Vna12" → 12, para que la Vna2 vaya antes que la Vna10. */
    private fun numeroDeVentana(v: String) = Regex("""\d+""").find(v)?.value?.toIntOrNull() ?: 0

    /** Una línea por vidrio, para la pantalla. */
    fun linea(v: VidrioCurvo): String = buildString {
        append("${v.ventana}  ${df1(v.desarrollo)} x ${df1(v.alto)} = ${v.cantidad}")
        if (v.tipo.isNotBlank()) append("  (${v.tipo})")
        append("\n   ")
        append(curvatura(v))
        caras(v)?.let { append("\n   $it") }
    }

    private fun curvatura(v: VidrioCurvo): String =
        if (v.cuerda == null || v.flecha == null) "Cuerda y flecha: sin dato (archivado antes del 2026-10-01)"
        else "Cuerda ${df1(v.cuerda)} · Flecha ${df1(v.flecha)}" + (v.radio?.let { " · Radio ${df1(it)}" } ?: "")

    /**
     * Hacia dónde va la panza (mirando desde adentro) y cómo queda la cara especial, en mayúsculas
     * porque es lo que no se puede confundir: "Panza hacia afuera · espejo CONVEXA". Sin caras, el
     * vidrio se voltea; si no se dijo al archivar, se avisa. Null si no hay nada que decir.
     */
    private fun caras(v: VidrioCurvo): String? {
        val panza = v.panza.takeIf { it.isNotBlank() }?.let { "Panza hacia $it" }
        val cara = when {
            v.cara.isBlank() -> if (panza != null) "Caras: sin dato" else null
            v.cara == "sin caras" -> "Sin caras (se puede voltear)"
            else -> v.cara.substringBeforeLast(' ') + " " + v.cara.substringAfterLast(' ').uppercase()
        }
        return listOfNotNull(panza, cara).joinToString(" · ").ifBlank { null }
    }

    /**
     * El texto para el curvador o el horno: por proyecto, cada vidrio con su desarrollo, alto,
     * cantidad, cuerda, flecha, radio, panza y cara; al final, el total de vidrios.
     */
    fun texto(lista: List<VidrioCurvo>, fecha: String): String = buildString {
        append("VIDRIOS CURVOS - $fecha\n")
        append("Medidas en cm. Desarrollo = ancho del vidrio sobre la curva.\n")
        append("La panza de cada arco, mirando desde adentro.\n")
        lista.groupBy { it.proyecto }.forEach { (proyecto, vidrios) ->
            append("\nProyecto: $proyecto\n")
            vidrios.forEach { v ->
                append("- ${v.ventana}: desarrollo ${df1(v.desarrollo)} x alto ${df1(v.alto)} = ${v.cantidad}")
                if (v.tipo.isNotBlank()) append(" (${v.tipo})")
                append("\n  ${curvatura(v)}\n")
                caras(v)?.let { append("  $it\n") }
            }
        }
        append("\nTotal: ${lista.sumOf { it.cantidad }} vidrios")
    }

    // Un decimal truncado, como en todas las listas de material (df1).
    private fun df1(x: Float): String = crystal.crystal.taller.nova.NovaCalculos.df1(x)
}
