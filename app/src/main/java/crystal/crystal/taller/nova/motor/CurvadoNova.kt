package crystal.crystal.taller.nova.motor

import crystal.crystal.taller.nova.NovaCalculos

/**
 * El curvado (N-102, dictado el 2026-10-01). Una ventana curva, antes de instalarse, pasa por la
 * roladora: cada perfil horizontal se curva, y para eso hace falta su cuerda y su flecha, y cortarlo
 * más largo para que la roladora lo sujete.
 *
 * - La medida de la pieza es su **arco** (lo que se corta, sección 11 / G-09): 108.
 * - Sobre el radio de su curva, ese arco tiene su **cuerda** (C) y su **flecha** (F).
 * - **Ampliación**: la roladora sujeta el perfil 5 cm de cada punta, así que se corta 108 + 2 × 5 =
 *   118 y, ya curvado, se recorta a 108. Se archiva así, con la marca [MARCA], para que el corte
 *   gaste lo que de verdad gasta y la etiqueta diga a dónde va la pieza después de cortarla.
 * - **Los vidrios** también, por su ancho, pero sin ampliación (no pasan por la roladora).
 *
 * Lo vertical (parantes, U parante, portafelpa, tope) no se curva.
 *
 * Qué se curva lo dice el lado ([LadoNova.radios]): Nova curva entera, la curva de una esquina de
 * L, C o serie, y en una S cada arco con el suyo. Una pieza que pasa por varios arcos se curva por
 * trozos.
 *
 * El radio es el de la línea medida aunque el perfil vaya algo más adentro (dictado el mismo día):
 * correrlo en paralelo no cambia nada y, si cambia, es preferible que la medida quede grande.
 */
object CurvadoNova {

    /** Lo que la roladora necesita para sujetar el perfil en cada punta, en cm. */
    const val AMPLIACION_POR_PUNTA = 5f

    /** Lo que se escribe detrás de la cantidad al archivar una pieza curva: `118 = 1 curvo`. */
    const val MARCA = "curvo"

    data class Curvado(val cuerda: Float, val flecha: Float, val ampliado: Float)

    /** Cuerda y flecha de un arco de largo [arco] sobre un círculo de radio [radio] (el signo no cuenta). */
    fun de(arco: Float, radio: Float): Curvado? {
        val r = kotlin.math.abs(radio)
        if (arco <= 0f || r <= 0f) return null
        val medio = arco / (2.0 * r)
        val cuerda = (2.0 * r * kotlin.math.sin(medio)).toFloat()
        val flecha = (r * (1.0 - kotlin.math.cos(medio))).toFloat()
        return Curvado(cuerda, flecha, arco + 2 * AMPLIACION_POR_PUNTA)
    }

    // ==================== QUÉ SE CURVA ====================

    /** El radio del tramo [tramo] del lado; 0 si es recto. */
    fun radioDe(l: LadoNova, tramo: Int): Float = when {
        l.radios.isEmpty() -> 0f
        l.radios.size == 1 -> l.radios[0]
        else -> l.radios.getOrElse(tramo) { 0f }
    }

    /**
     * Los trozos curvos de una pieza hecha de [trozos] (tramo, largo), seguidos. Los trozos seguidos
     * sobre el mismo radio son un solo arco. Vacío si todo queda recto.
     */
    fun arcos(l: LadoNova, trozos: List<Pair<Int, Float>>): List<Arco> {
        if (l.radios.none { it != 0f }) return emptyList()
        val salida = mutableListOf<Arco>()
        for ((tramo, largo) in trozos) {
            if (largo <= 0f) continue
            val r = radioDe(l, tramo)
            val ultimo = salida.lastOrNull()
            if (ultimo != null && ultimo.radio == r) salida[salida.lastIndex] = ultimo.copy(largo = ultimo.largo + largo)
            else salida.add(Arco(largo, r))
        }
        return if (salida.all { it.radio == 0f }) emptyList() else salida
    }

    /**
     * Marca lo que se curva en los materiales de un lado. Lo horizontal y los vidrios toman el arco
     * de su tramo; lo que es del lado entero (la U de lado a lado), un trozo por tramo, en proporción
     * a su ancho. Lo que ya trae su curva (una racha de fijos que cruza parantes) queda como está.
     */
    fun curvar(m: MaterialesNova, l: LadoNova): MaterialesNova {
        if (l.radios.none { it != 0f }) return m
        fun curvaDe(largo: Float, tramo: Int): List<Arco> = when {
            tramo >= 0 || l.radios.size == 1 -> arcos(l, listOf(maxOf(tramo, 0) to largo))
            else -> {
                val anchos = l.tramos.map { it.ancho }
                val total = anchos.sum()
                if (total <= 0f) arcos(l, listOf(0 to largo))
                else arcos(l, anchos.mapIndexed { i, a -> i to largo * a / total })
            }
        }
        return m.cadaPieza(
            { p -> if (p.orientacion != Orientacion.HORIZONTAL || p.curva.isNotEmpty()) p else p.copy(curva = curvaDe(p.medida, p.tramo)) },
            { v -> if (v.curva.isNotEmpty()) v else v.copy(curva = curvaDe(v.ancho, v.tramo)) }
        )
    }

    // ==================== LA VISTA COMPLETA ====================

    /**
     * El texto completo de una lista de piezas, el que se ve con el ▼: las mismas líneas de
     * [MaterialesNova.texto] (`108 = 1`) y, debajo de cada pieza curva, su curvado (radio 100):
     * ```
     * 108 = 1
     * C = 102.8
     * F = 14.2
     * Ampliación 118 = 1
     * ```
     * Una pieza que pasa por varios arcos (una S) lleva cada arco con su largo, su cuerda y su flecha.
     */
    fun texto(piezas: List<Pieza>): String {
        val grupos = agrupar(piezas.filter { it.cantidad > 0 && it.medida > 0f }, { it.cantidad }) { p ->
            Triple(p.orientacion.ordinal, NovaCalculos.df1(p.medida), firma(p.curva))
        }
        return grupos.sortedBy { it.first.first }.joinToString("\n") { (k, p, n) ->
            val linea = "${k.second} = $n"
            if (p.curva.isEmpty()) linea
            else "$linea\n${lineasDeCurva(p.curva)}\nAmpliación ${NovaCalculos.df1(p.medida + 2 * AMPLIACION_POR_PUNTA)} = $n"
        }
    }

    /** Lo mismo para los vidrios, por su ancho y sin ampliación (radio 100): `60.4 x 126.9 = 4`, `C = 59.4`, `F = 4.5`. */
    fun textoVidrios(vidrios: List<Vidrio>): String {
        val grupos = agrupar(vidrios.filter { it.cantidad > 0 && it.ancho > 0f && it.alto > 0f }, { it.cantidad }) { v ->
            Triple(0, "${NovaCalculos.df1(v.ancho)} x ${NovaCalculos.df1(v.alto)}", firma(v.curva))
        }
        return grupos.joinToString("\n") { (k, v, n) ->
            val linea = "${k.second} = $n"
            if (v.curva.isEmpty()) linea else "$linea\n${lineasDeCurva(v.curva)}"
        }
    }

    private fun lineasDeCurva(curva: List<Arco>): String {
        fun cf(a: Arco) = de(a.largo, a.radio)
            ?.let { "C = ${NovaCalculos.df1(it.cuerda)}\nF = ${NovaCalculos.df1(it.flecha)}" } ?: "Recto"
        if (curva.size == 1) return cf(curva[0])
        // En una S se dice hacia dónde va la panza de cada arco.
        val conLado = curva.map { kotlin.math.sign(it.radio) }.filter { it != 0f }.distinct().size > 1
        return curva.mapIndexed { i, a ->
            val lado = when {
                !conLado || a.radio == 0f -> ""
                a.radio < 0f -> " (hacia adentro)"
                else -> " (hacia afuera)"
            }
            "Arco ${i + 1}: ${NovaCalculos.df1(a.largo)}$lado\n${cf(a)}"
        }.joinToString("\n")
    }

    // ==================== LO QUE SE ARCHIVA ====================

    /**
     * La lista para el proyecto: lo recto como siempre (`108 = 1`) y lo curvo con su ampliación y la
     * marca (`118 = 1 curvo`), que es lo que se corta de la barra.
     */
    fun textoArchivo(piezas: List<Pieza>): String {
        val grupos = agrupar(piezas.filter { it.cantidad > 0 && it.medida > 0f }, { it.cantidad }) { p ->
            val curva = p.curva.isNotEmpty()
            val medida = if (curva) p.medida + 2 * AMPLIACION_POR_PUNTA else p.medida
            Triple(p.orientacion.ordinal, NovaCalculos.df1(medida), marca(p.curva))
        }
        return grupos.sortedBy { it.first.first }.joinToString("\n") { (k, _, n) -> linea(k, n) }
    }

    /**
     * Los vidrios para el proyecto: los curvos con la marca, su misma medida, su cuerda y flecha,
     * hacia dónde va su panza y, si el vidrio tiene caras, cómo queda la cara especial; es lo que el
     * curvador o el horno necesitan (`60.4 x 126.9 = 4 curvo C59.4 F4.5 afuera [espejo convexa]`).
     * Dos vidrios iguales con la cara al revés no se suman: son dos piezas distintas.
     */
    fun textoArchivoVidrios(vidrios: List<Vidrio>, caras: Caras? = null): String {
        val grupos = agrupar(vidrios.filter { it.cantidad > 0 && it.ancho > 0f && it.alto > 0f }, { it.cantidad }) { v ->
            Triple(0, "${NovaCalculos.df1(v.ancho)} x ${NovaCalculos.df1(v.alto)}", marca(v.curva, caras))
        }
        return grupos.joinToString("\n") { (k, _, n) -> linea(k, n) }
    }

    // ==================== LAS CARAS DEL VIDRIO ====================

    /**
     * Las caras de un vidrio (dictado el 2026-10-01): un reflejante tiene color por una cara y espejo
     * por la otra, y al curvarlo una queda cóncava y la otra convexa. En una S, el mismo vidrio sale
     * con el espejo convexo en un arco y cóncavo en el otro.
     *
     * Se pregunta al archivar, porque Medidas no sabe nada del vidrio. [nombre] es la cara especial
     * (espejo, capa, satinado…) y [haciaAdentro] si es la que se ve desde adentro. Sin caras
     * ([SIN_CARAS]) el vidrio da lo mismo de un lado o del otro: se voltea.
     */
    data class Caras(val nombre: String, val haciaAdentro: Boolean) {
        val tiene: Boolean get() = nombre.isNotBlank()
    }

    val SIN_CARAS = Caras("", false)

    /**
     * Hacia dónde va la panza del arco, mirando desde adentro (como se mide, salvo en 3D, que lo trae
     * dicho): `afuera` con radio positivo, `adentro` con negativo.
     */
    fun panza(radio: Float): String = if (radio < 0f) "adentro" else "afuera"

    /**
     * Cómo queda la cara especial. Con la panza hacia afuera, la cara de afuera queda convexa y la de
     * adentro cóncava; con la panza hacia adentro, al revés.
     */
    fun caraDe(radio: Float, caras: Caras): String {
        if (!caras.tiene) return "sin caras"
        val convexa = if (caras.haciaAdentro) radio < 0f else radio > 0f
        return "${caras.nombre} ${if (convexa) "convexa" else "cóncava"}"
    }

    // ==================== AUXILIARES ====================

    /**
     * La marca de lo curvo al archivar: `curvo`, y si es de un solo arco la cuerda y la flecha de la
     * pieza terminada y hacia dónde va su panza (`curvo C102.8 F14.2 afuera`); en un vidrio con las
     * caras dichas, también cómo queda la especial (`[espejo convexa]`). Vacío si es recta.
     */
    private fun marca(curva: List<Arco>, caras: Caras? = null): String {
        if (curva.isEmpty()) return ""
        val arco = curva.singleOrNull() ?: return MARCA
        val c = de(arco.largo, arco.radio) ?: return MARCA
        val cara = caras?.let { " [${caraDe(arco.radio, it)}]" }.orEmpty()
        return "$MARCA C${NovaCalculos.df1(c.cuerda)} F${NovaCalculos.df1(c.flecha)} ${panza(arco.radio)}$cara"
    }

    private fun linea(k: Triple<Int, String, String>, n: Int) =
        if (k.third.isEmpty()) "${k.second} = $n" else "${k.second} = $n ${k.third}"

    private fun firma(curva: List<Arco>) =
        curva.joinToString(";") { "${NovaCalculos.df1(it.largo)}@${NovaCalculos.df1(it.radio)}" }

    /** Agrupa por [clave] en el orden de aparición: (clave, el primero del grupo, la cantidad sumada). */
    private fun <T> agrupar(
        cosas: List<T>,
        cantidad: (T) -> Int,
        clave: (T) -> Triple<Int, String, String>
    ): List<Triple<Triple<Int, String, String>, T, Int>> {
        val grupos = linkedMapOf<Triple<Int, String, String>, Pair<T, Int>>()
        for (c in cosas) {
            val k = clave(c)
            val g = grupos[k]
            grupos[k] = (g?.first ?: c) to ((g?.second ?: 0) + cantidad(c))
        }
        return grupos.map { (k, g) -> Triple(k, g.first, g.second) }
    }
}
