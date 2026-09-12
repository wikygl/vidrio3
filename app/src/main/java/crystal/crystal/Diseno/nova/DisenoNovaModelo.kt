package crystal.crystal.Diseno.nova

import crystal.crystal.taller.nova.NovaCalculos

/**
 * El diseño de una ventana Nova como MODELO, no como cadena de texto.
 *
 * Hoy el diseño vive en la cadena del paquete: cada edición la parsea, la reescribe y la vuelve a
 * parsear, y las reglas quedan repartidas en la cirugía de texto. Esta es la misma estrategia de
 * `MamparaModulos`: un objeto que es la ÚNICA fuente de la geometría, del que salen tanto el
 * dibujo como los conteos, y donde la cadena es solo la forma de guardarlo.
 *
 * Formato de la cadena (ver `docs/REGLAS_NOVA.md` §13.3):
 *
 * ```
 * {nova,<acabado>,[<ancho>,<alto>: Tl<w>(m<h>(f<w>f<w>);s<h>(f<w>c<w>)) P<2.5> Tl<w>(…) ]}
 * ```
 *
 * - Tramos separados por `P<2.5>`; el parante es vertical y parte el tramo ENTERO, con todas sus
 *   franjas. `A<90>` separa lados en L, C y serie.
 * - Franjas dentro del tramo, separadas por `;`: `s<alto>` sistema, `m<alto>` mocheta.
 * - Módulos dentro de la franja: `f<ancho>` fijo, `c<ancho>` corrediza. Un `;P;` dentro de una
 *   franja es un parante que parte solo esa franja, que el formato admite aunque el parante
 *   normal parta el tramo completo.
 *
 * Todavía no lo usa nadie: se construye primero, con sus pruebas de ida y vuelta contra paquetes
 * reales, para poder cambiar la pantalla encima sin romper lo que ya funciona.
 */

/** Un módulo: fijo o corrediza. [ancho] nulo = "el que salga" al repartir el tramo. */
data class NovaModulo(val tipo: Char, val ancho: Float? = null) {
    val esFijo: Boolean get() = tipo != 'c'
}

/** Una franja horizontal del tramo: la del sistema o una de mocheta. */
data class NovaFranja(
    val esSistema: Boolean,
    val alto: Float,
    val modulos: List<NovaModulo>
) {
    val nFijos: Int get() = modulos.count { it.esFijo }
    val nCorredizas: Int get() = modulos.count { !it.esFijo }
}
/**
 * Un tramo: el trozo de ventana entre dos parantes, con TODAS sus franjas. Un parante parte el
 * tramo entero, así que las franjas de un tramo empiezan y acaban juntas.
 *
 * Cuatro medidas propias lo colocan dentro del vano, y todas en 0 son lo normal —el tramo ocupa
 * el alto entero de la ventana:
 *
 * - [caida]: cuánto BAJA su dintel respecto al de la ventana. Es el escalón de arriba, el que sale
 *   cuando una viga o un dintel más bajo come parte del vano.
 * - [alto]: lo que mide el tramo desde su propio dintel. Es el escalón de abajo, el del alféizar
 *   que sube en un trozo del vano.
 * - [caidaDer] y [altoDer]: lo mismo del lado derecho. En null valen lo que el lado izquierdo, que
 *   es como se describe un tramo rectangular o un escalón.
 *
 * Con los dos lados distintos el tramo deja de ser un rectángulo y pasa a ser un cuadrilátero: así
 * se describen el dintel inclinado, el triángulo y el paralelogramo. La franja de sistema se queda
 * recta de todas formas —una hoja corrediza corre por el riel y tiene que ser rectangular—; lo
 * inclinado se lo reparten las franjas de encima, que son fijas.
 */
data class NovaTramo(
    val ancho: Float,
    val franjas: List<NovaFranja>,
    val alto: Float = 0f,
    val caida: Float = 0f,
    val altoDer: Float? = null,
    val caidaDer: Float? = null
) {
    val sistema: NovaFranja? get() = franjas.firstOrNull { it.esSistema }
    val mochetas: List<NovaFranja> get() = franjas.filter { !it.esSistema }
    val nModulosSistema: Int get() = sistema?.modulos?.size ?: 0

    /** El alto del lado derecho: el suyo si lo tiene, y si no el del izquierdo. */
    val altoDerecho: Float get() = altoDer ?: alto

    /** Lo que baja el dintel del lado derecho. */
    val caidaDerecha: Float get() = caidaDer ?: caida

    /** ¿Tiene los dos lados distintos? Entonces es un tramo inclinado, no un rectángulo. */
    val esInclinado: Boolean
        get() = kotlin.math.abs(altoDerecho - alto) > 0.05f ||
            kotlin.math.abs(caidaDerecha - caida) > 0.05f
}

/** El diseño completo. [etiquetas] son los tags sueltos del paquete (`A<90>`, `U<…>`, `O<1>`). */
data class DisenoNova(
    val acabado: String,
    val ancho: Float,
    val alto: Float,
    val tramos: List<NovaTramo>,
    val etiquetas: List<String> = emptyList()
) {
    val nTramos: Int get() = tramos.size
    val nModulos: Int get() = tramos.sumOf { it.nModulosSistema }
    val nFijos: Int get() = tramos.sumOf { t -> t.sistema?.nFijos ?: 0 }
    val nCorredizas: Int get() = tramos.sumOf { t -> t.sistema?.nCorredizas ?: 0 }

    /** Parantes entre tramos: uno menos que los tramos. */
    val nParantes: Int get() = (tramos.size - 1).coerceAtLeast(0)


    /** El alto de un tramo: el suyo si lo tiene, y si no el de la ventana. */
    fun altoDeTramo(indice: Int): Float =
        tramos.getOrNull(indice)?.alto?.takeIf { it > 0f } ?: alto

    /** Cuánto baja el dintel de un tramo respecto al de la ventana. */
    fun caidaDeTramo(indice: Int): Float = tramos.getOrNull(indice)?.caida ?: 0f

    /** ¿Hay tramos recortados por arriba o por abajo? Es la ventana escalonada. */
    val esEscalonada: Boolean
        get() = tramos.any { (it.alto > 0f && it.alto != alto) || it.caida > 0f }

    /**
     * ¿La ventana deja de ser el rectángulo de siempre? Escalonada o con algún lado inclinado.
     *
     * Un triángulo con la punta a un costado no es escalonado —su único tramo llega de dintel a
     * alféizar— pero tampoco es un rectángulo: preguntando solo por [esEscalonada] se colaba como
     * tal y el vano perdía su forma antes de llegar al cálculo.
     */
    val esIrregular: Boolean
        get() = esEscalonada || tramos.any { it.esInclinado } || contornoVano.isNotEmpty()

    /**
     * El contorno del vano, si el diseño lo trae: la silueta que se midió en obra.
     *
     * Viaja como etiqueta `V<…>` y es independiente del reparto: los tramos se pueden borrar y
     * rehacer enteros, que la forma del hueco sigue siendo la misma. Vacío = el vano es el
     * rectángulo de siempre.
     */
    val contornoVano: List<Pair<Float, Float>>
        get() = etiquetas.firstOrNull { it.startsWith("V<", ignoreCase = true) }
            ?.let { ContornoEnTramos.desdeEtiqueta(it) }
            .orEmpty()

    /**
     * La silueta que dibujan los tramos: su contorno, de la esquina de arriba a la izquierda y en
     * el sentido de las agujas del reloj.
     *
     * Sirve para los diseños que traen la forma en los tramos pero sin la etiqueta `V<…>`: los
     * hechos a mano con las alturas del panel, y los guardados antes de que la forma viajara
     * aparte. Un vano rectangular devuelve su rectángulo.
     */
    fun contornoDesdeTramos(): List<Pair<Float, Float>> {
        if (tramos.isEmpty()) return emptyList()
        fun arriba(t: NovaTramo, derecha: Boolean) = if (derecha) t.caidaDerecha else t.caida
        fun abajo(t: NovaTramo, derecha: Boolean): Float {
            val propio = if (derecha) t.altoDerecho else t.alto
            return if (propio > 0f) arriba(t, derecha) + propio else alto
        }
        val techo = mutableListOf<Pair<Float, Float>>()
        val piso = mutableListOf<Pair<Float, Float>>()
        var x = 0f
        for (t in tramos) {
            techo.add(x to arriba(t, false))
            piso.add(x to abajo(t, false))
            x += t.ancho
            techo.add(x to arriba(t, true))
            piso.add(x to abajo(t, true))
        }
        // Del techo de izquierda a derecha y del piso de vuelta, sin repetir los puntos que ya
        // están: dos tramos a la misma altura no necesitan un vértice entre ellos.
        val vuelta = (techo + piso.reversed())
        val limpio = mutableListOf<Pair<Float, Float>>()
        for (p in vuelta) {
            val ultimo = limpio.lastOrNull()
            if (ultimo != null && kotlin.math.abs(ultimo.first - p.first) < 0.05f &&
                kotlin.math.abs(ultimo.second - p.second) < 0.05f
            ) continue
            limpio.add(p)
        }
        // Y fuera los vértices que no doblan: la frontera entre dos tramos a la misma altura está
        // en medio de un lado recto, y como vértice no dice nada.
        return sinPuntosEnLinea(limpio)
    }

    /** Quita los puntos que caen en la recta de sus vecinos. */
    private fun sinPuntosEnLinea(puntos: List<Pair<Float, Float>>): List<Pair<Float, Float>> {
        if (puntos.size < 3) return puntos
        val out = mutableListOf<Pair<Float, Float>>()
        for (i in puntos.indices) {
            val a = puntos[(i - 1 + puntos.size) % puntos.size]
            val b = puntos[i]
            val c = puntos[(i + 1) % puntos.size]
            val cruz = (b.first - a.first) * (c.second - a.second) -
                (b.second - a.second) * (c.first - a.first)
            if (kotlin.math.abs(cruz) > 0.5f) out.add(b)
        }
        return if (out.size >= 3) out else puntos
    }

    /** El mismo diseño con esa silueta de vano. Una lista vacía la quita. */
    fun conContornoVano(puntos: List<Pair<Float, Float>>): DisenoNova {
        val otras = etiquetas.filterNot { it.startsWith("V<", ignoreCase = true) }
        val nuevas = if (puntos.size < 3) otras else otras + ContornoEnTramos.aEtiqueta(puntos)
        return copy(etiquetas = nuevas)
    }

    /**
     * Cambia el alto de UN tramo —el escalón— y estira o encoge sus franjas en la misma
     * proporción, para que sigan llenándolo.
     *
     * [alto] igual al de la ventana, o 0, devuelve el tramo a ras de los demás.
     */
    fun conAltoDeTramo(indice: Int, altoTramo: Float): DisenoNova {
        val tramo = tramos.getOrNull(indice) ?: return this
        val nuevo = altoTramo.coerceAtLeast(0f)
        val efectivo = if (nuevo <= 0f || kotlin.math.abs(nuevo - alto) < 0.05f) 0f else nuevo
        if (kotlin.math.abs(efectivo - tramo.alto) < 0.05f) return this
        val antes = altoDeTramo(indice)
        val ahora = if (efectivo > 0f) efectivo else alto
        val k = if (antes > 0f) ahora / antes else 1f
        val franjas = tramo.franjas.map { if (it.alto > 0f) it.copy(alto = it.alto * k) else it }
        val nuevos = tramos.toMutableList()
        nuevos[indice] = tramo.copy(franjas = franjas, alto = efectivo)
        return copy(tramos = nuevos)
    }
    /** El ancho que queda para los módulos, descontando los parantes entre tramos. */
    fun anchoUtil(anchoParante: Float = 2.5f): Float =
        (ancho - nParantes * anchoParante).coerceAtLeast(0f)

    // ==================== OPERACIONES ====================
    // Todas devuelven un diseño NUEVO; el modelo no se muta. Si los índices no valen devuelven
    // el mismo diseño sin tocar, para que la pantalla no tenga que validar antes de llamar.

    /**
     * Reparte los anchos: el ancho útil (sin los parantes entre tramos) entre todos los módulos
     * del sistema, cada tramo se queda con los suyos, y dentro de cada franja los módulos se
     * reparten el ancho del tramo por igual.
     *
     * Es la regla de siempre: los tramos y las medidas salen de contar módulos.
     *
     * [bloqueados] son los tramos cuyo ancho el vidriero fijó y no se tocan: conservan su medida
     * y el resto absorbe la diferencia, repartiéndose lo que queda. Si se bloquean todos no hay
     * nada que repartir y el diseño se devuelve tal cual.
     */
    fun conAnchosRepartidos(
        anchoParante: Float = 2.5f,
        bloqueados: Set<Int> = emptySet()
    ): DisenoNova {
        val totalModulos = nModulos
        if (totalModulos <= 0) return this
        val fijos = bloqueados.filter { it in tramos.indices }.toSet()
        val libres = tramos.indices.filter { it !in fijos }
        if (libres.isEmpty()) return this

        val anchoFijo = fijos.sumOf { tramos[it].ancho.toDouble() }.toFloat()
        val disponible = (anchoUtil(anchoParante) - anchoFijo).coerceAtLeast(0f)
        val modulosLibres = libres.sumOf { tramos[it].nModulosSistema }
        val porModulo = if (modulosLibres > 0) disponible / modulosLibres else 0f

        return copy(tramos = tramos.mapIndexed { i, tramo ->
            val anchoTramo = if (i in fijos) tramo.ancho else porModulo * tramo.nModulosSistema
            // Se conserva el alto propio del tramo: repartir anchos no tiene por qué borrar el
            // escalón, y al perderlo la ventana volvía a salir rectangular en cuanto se editaba.
            NovaTramo(
                ancho = anchoTramo,
                franjas = tramo.franjas.map { fr ->
                    val w = if (fr.modulos.isEmpty()) anchoTramo else anchoTramo / fr.modulos.size
                    fr.copy(modulos = fr.modulos.map { it.copy(ancho = w) })
                },
                alto = tramo.alto,
                caida = tramo.caida,
                altoDer = tramo.altoDer,
                caidaDer = tramo.caidaDer
            )
        })
    }

    /**
     * Parte el tramo [indice] en dos con un parante, justo después del módulo
     * [despuesDelModulo] de su franja de sistema.
     *
     * El parante es vertical: parte el tramo ENTERO. Cada franja se reparte en la misma
     * proporción que la de sistema, con al menos un módulo a cada lado; si una franja tenía un
     * solo módulo —una mocheta corrida— cada tramo se queda con uno.
     */
    fun conTramoPartido(indice: Int, despuesDelModulo: Int): DisenoNova {
        val tramo = tramos.getOrNull(indice) ?: return this
        val nSistema = tramo.nModulosSistema
        val corte = despuesDelModulo + 1
        if (corte !in 1 until nSistema) return this

        val izq = mutableListOf<NovaFranja>()
        val der = mutableListOf<NovaFranja>()
        for (fr in tramo.franjas) {
            val n = fr.modulos.size
            if (n <= 1) {
                izq.add(fr)
                der.add(fr)
            } else {
                val en = Math.round(n * corte / nSistema.toFloat()).coerceIn(1, n - 1)
                izq.add(fr.copy(modulos = fr.modulos.subList(0, en).toList()))
                der.add(fr.copy(modulos = fr.modulos.subList(en, n).toList()))
            }
        }
        val nuevos = tramos.toMutableList()
        // Los dos trozos siguen siendo el mismo tramo de vano: conservan sus medidas. Si el tramo
        // iba inclinado, el corte cae a media pendiente y cada trozo se queda con su parte.
        val t = if (nSistema > 0) corte / nSistema.toFloat() else 0.5f
        val altoMedio = tramo.alto + (tramo.altoDerecho - tramo.alto) * t
        val caidaMedia = tramo.caida + (tramo.caidaDerecha - tramo.caida) * t
        nuevos[indice] = NovaTramo(tramo.ancho, izq, tramo.alto, tramo.caida, altoMedio, caidaMedia)
        nuevos.add(
            indice + 1,
            NovaTramo(tramo.ancho, der, altoMedio, caidaMedia, tramo.altoDer, tramo.caidaDer)
        )
        return copy(tramos = nuevos).conAnchosRepartidos()
    }

    /**
     * Quita el parante entre el tramo [indice] y el siguiente: los dos vuelven a ser uno.
     *
     * Ni esta ni [conTramoPartido] admiten tramos bloqueados: al partir o unir, los índices de
     * los tramos se corren y el bloqueo dejaría de apuntar a donde apuntaba. Quien las llame
     * tiene que soltar los bloqueos.
     */
    fun conTramosUnidos(indice: Int): DisenoNova {
        val a = tramos.getOrNull(indice) ?: return this
        val b = tramos.getOrNull(indice + 1) ?: return this
        // Las franjas se emparejan por orden y tipo; las que no tengan pareja se conservan.
        val franjas = mutableListOf<NovaFranja>()
        val pendientes = b.franjas.toMutableList()
        for (fr in a.franjas) {
            val pareja = pendientes.firstOrNull { it.esSistema == fr.esSistema }
            if (pareja != null) {
                pendientes.remove(pareja)
                franjas.add(fr.copy(modulos = fr.modulos + pareja.modulos))
            } else {
                franjas.add(fr)
            }
        }
        franjas.addAll(pendientes)
        val nuevos = tramos.toMutableList()
        // Al unir dos tramos de distinta altura, el resultado llega hasta donde llegaba el más
        // alto: el escalón que había entre ellos desaparece con el parante.
        val altoUnido = if (a.alto <= 0f || b.alto <= 0f) 0f else maxOf(a.alto, b.alto)
        val caidaUnida = minOf(a.caida, b.caida)
        nuevos[indice] = NovaTramo(
            a.ancho + b.ancho, franjas, altoUnido, caidaUnida,
            // El cuadrilátero unido va del lado izquierdo del primero al derecho del segundo.
            altoDer = b.altoDer, caidaDer = b.caidaDer
        )
        nuevos.removeAt(indice + 1)
        return copy(tramos = nuevos).conAnchosRepartidos()
    }

    /**
     * Agrega un tramo al final, con las mismas franjas que el último pero con un solo módulo en
     * cada una.
     *
     * Un tramo nuevo empieza con lo mínimo que se sostiene —un fijo por franja—, igual que en la
     * mampara: el vidriero le añade después lo que necesite. El ancho lo pone el reparto, que
     * respeta los tramos [bloqueados]: esos conservan su medida y el resto absorbe.
     */
    fun conTramoAgregado(bloqueados: Set<Int> = emptySet()): DisenoNova {
        val ultimo = tramos.lastOrNull()
        val franjas = ultimo?.franjas?.map { it.copy(modulos = listOf(NovaModulo('f'))) }
            ?: listOf(NovaFranja(esSistema = true, alto = 0f, modulos = listOf(NovaModulo('f'))))
        return copy(tramos = tramos + NovaTramo(0f, franjas))
            .conAnchosRepartidos(bloqueados = bloqueados)
    }

    /**
     * Quita el tramo [indice] entero —por defecto el último—, con todas sus franjas. Siempre
     * queda al menos un tramo.
     *
     * Los [bloqueados] anteriores al quitado conservan su ancho; los de después ya no valen,
     * porque al quitar uno los índices se corren.
     */
    fun conTramoQuitado(
        indice: Int = tramos.lastIndex,
        bloqueados: Set<Int> = emptySet()
    ): DisenoNova {
        if (tramos.size <= 1 || indice !in tramos.indices) return this
        return copy(tramos = tramos.filterIndexed { i, _ -> i != indice })
            .conAnchosRepartidos(bloqueados = bloqueados.filter { it < indice }.toSet())
    }

    /**
     * Agrega una franja al tramo [indiceTramo], y solo a ese, con un módulo del ancho del tramo.
     *
     * Cada tramo lleva las franjas que le hagan falta: uno con dos mochetas —bandera—, el de al
     * lado con una, el siguiente con ninguna. Eso es lo que el diseño simbólico no sabe decir.
     *
     * Si el sistema de ese tramo tiene altura fija, las mochetas se reparten lo que sobra del
     * alto de la ventana, así la franja nueva nace con medida en vez de con 0. No toca los
     * anchos: una franja más no cambia el ancho de ningún tramo, y volver a repartir borraría
     * los anchos fijados a mano.
     */
    fun conFranjaAgregadaEnTramo(
        indiceTramo: Int,
        esSistema: Boolean = false,
        altoFranja: Float = 0f,
        arriba: Boolean = true
    ): DisenoNova {
        val tramo = tramos.getOrNull(indiceTramo) ?: return this
        val nueva = NovaFranja(esSistema, altoFranja.coerceAtLeast(0f), listOf(NovaModulo('f', tramo.ancho)))
        // Las franjas se guardan de abajo arriba: al final del todo va la de arriba y al principio
        // la de abajo. Sin poder elegirlo, la mocheta salía siempre encima y para ponerla debajo
        // había que dar un rodeo.
        val franjas = if (arriba) tramo.franjas + nueva else listOf(nueva) + tramo.franjas
        val nuevos = tramos.toMutableList()
        nuevos[indiceTramo] = tramo.copy(franjas = franjas)
        val conFranja = copy(tramos = nuevos)
        // Una franja con altura pedida se respeta; la automática se lleva lo que sobra del puente.
        return if (esSistema || altoFranja > 0f) conFranja else conFranja.conAlturasRepartidas(indiceTramo)
    }

    /**
     * Quita una franja del tramo [indiceTramo]: por defecto su última mocheta. La del sistema no
     * se toca —sin ella no hay ventana— y siempre queda al menos una franja en el tramo.
     */
    fun conFranjaQuitadaEnTramo(indiceTramo: Int, indice: Int = -1): DisenoNova {
        val tramo = tramos.getOrNull(indiceTramo) ?: return this
        if (tramo.franjas.size <= 1) return this
        val idx = if (indice >= 0) indice else tramo.franjas.indexOfLast { !it.esSistema }
        if (idx !in tramo.franjas.indices || tramo.franjas[idx].esSistema) return this
        val nuevos = tramos.toMutableList()
        nuevos[indiceTramo] = tramo.copy(franjas = tramo.franjas.filterIndexed { i, _ -> i != idx })
        return copy(tramos = nuevos).conAlturasRepartidas(indiceTramo)
    }

    /**
     * Reparte el alto dentro de un tramo: el puente se queda con el suyo y las mochetas de ese
     * tramo con lo que sobra, a partes iguales.
     *
     * Nunca agrega ni quita franjas —al revés que [conPuenteCambiado], que sí puede crear la
     * mocheta—: si al tramo no le queda ninguna, el sistema se queda con el alto entero.
     */
    private fun conAlturasRepartidas(indiceTramo: Int): DisenoNova {
        val tramo = tramos.getOrNull(indiceTramo) ?: return this
        val sistema = tramo.franjas.firstOrNull { it.esSistema } ?: return this
        val altoT = altoDeTramo(indiceTramo)
        if (sistema.alto <= 0f || altoT <= 0f) return this
        val mochetas = tramo.franjas.count { !it.esSistema }
        val franjas = if (mochetas == 0) {
            tramo.franjas.map { if (it.esSistema) it.copy(alto = altoT) else it }
        } else {
            val porMocheta = (altoT - sistema.alto).coerceAtLeast(0f) / mochetas
            tramo.franjas.map { if (it.esSistema) it else it.copy(alto = porMocheta) }
        }
        if (franjas == tramo.franjas) return this
        val nuevos = tramos.toMutableList()
        nuevos[indiceTramo] = tramo.copy(franjas = franjas)
        return copy(tramos = nuevos)
    }

    /**
     * Cambia el alto del puente —la franja de sistema— del tramo [indice], y solo de ese tramo.
     *
     * Es lo que hace falta para diseñar a mano lo que el diseño simbólico no da: un tramo con el
     * puente a 130, el de al lado sin puente, y el siguiente con otra medida. Cada tramo lleva su
     * altura porque el puente es horizontal dentro del tramo, no de la ventana entera.
     *
     * - [altoPuente] a 0 o menos: no se toca nada; la franja se sigue repartiendo sola.
     * - [altoPuente] igual o mayor que el alto de la ventana: el sistema ocupa el tramo entero y
     *   ese tramo se queda **sin mocheta**.
     * - Entre medias: el sistema se queda con esa altura y las mochetas de ese tramo se reparten
     *   lo que sobra. Si el tramo no tenía mocheta, se le agrega una con el resto.
     */
    fun conPuenteCambiado(indice: Int, altoPuente: Float, altoVentana: Float = 0f): DisenoNova {
        val tramo = tramos.getOrNull(indice) ?: return this
        if (altoPuente <= 0f) return this
        val sistema = tramo.franjas.firstOrNull { it.esSistema } ?: return this
        val total = if (altoVentana > 0f) altoVentana else altoDeTramo(indice)
        if (total <= 0f) return this

        val franjas: List<NovaFranja> = if (altoPuente >= total) {
            listOf(sistema.copy(alto = total))
        } else {
            val resto = total - altoPuente
            val mochetas = tramo.franjas.count { !it.esSistema }
            if (mochetas == 0) {
                // El tramo no tenía mocheta: la que aparece se queda con lo que sobra, con un
                // paño del ancho del tramo.
                tramo.franjas.map { if (it.esSistema) it.copy(alto = altoPuente) else it } +
                    NovaFranja(false, resto, listOf(NovaModulo('f', tramo.ancho)))
            } else {
                val porMocheta = resto / mochetas
                tramo.franjas.map {
                    if (it.esSistema) it.copy(alto = altoPuente) else it.copy(alto = porMocheta)
                }
            }
        }
        if (franjas == tramo.franjas) return this
        val nuevos = tramos.toMutableList()
        nuevos[indice] = tramo.copy(franjas = franjas)
        return copy(tramos = nuevos)
    }

    /**
     * Cambia el alto de UNA franja del tramo [indiceTramo]. Lo que sobra o falta lo absorbe el
     * resto del tramo, para que las franjas sigan sumando el alto de la ventana:
     *
     * - si es la del sistema, absorben las mochetas a partes iguales (es [conPuenteCambiado]);
     * - si es una mocheta, absorbe el puente, que es el que puede crecer o encoger sin que
     *   cambie nada más.
     *
     * Un alto de 0 o menos no se toca: esa franja se sigue repartiendo sola.
     */
    fun conAlturaDeFranja(indiceTramo: Int, indiceFranja: Int, altoFranja: Float): DisenoNova {
        val tramo = tramos.getOrNull(indiceTramo) ?: return this
        val franja = tramo.franjas.getOrNull(indiceFranja) ?: return this
        val altoT = altoDeTramo(indiceTramo)
        if (altoFranja <= 0f || altoT <= 0f) return this
        if (franja.esSistema) return conPuenteCambiado(indiceTramo, altoFranja, altoT)

        val otrasMochetas = tramo.franjas.filterIndexed { i, f -> i != indiceFranja && !f.esSistema }
            .sumOf { it.alto.toDouble() }.toFloat()
        val puente = (altoT - altoFranja - otrasMochetas).coerceAtLeast(0f)
        val franjas = tramo.franjas.mapIndexed { i, f ->
            when {
                i == indiceFranja -> f.copy(alto = altoFranja)
                f.esSistema -> f.copy(alto = puente)
                else -> f
            }
        }
        if (franjas == tramo.franjas) return this
        val nuevos = tramos.toMutableList()
        nuevos[indiceTramo] = tramo.copy(franjas = franjas)
        return copy(tramos = nuevos)
    }

    /**
     * Cambia una franja de sistema a mocheta y al revés, en un solo tramo.
     *
     * Solo en ese tramo: con franjas distintas por tramo, cambiarlas todas a la vez era lo que
     * hacía el camino viejo y ya no vale.
     */
    fun conTipoDeFranjaCambiado(indiceTramo: Int, indiceFranja: Int): DisenoNova {
        val tramo = tramos.getOrNull(indiceTramo) ?: return this
        val franja = tramo.franjas.getOrNull(indiceFranja) ?: return this
        val franjas = tramo.franjas.toMutableList()
        franjas[indiceFranja] = franja.copy(esSistema = !franja.esSistema)
        val nuevos = tramos.toMutableList()
        nuevos[indiceTramo] = tramo.copy(franjas = franjas)
        return copy(tramos = nuevos)
    }

    /** Agrega un módulo [tipo] justo después del módulo [despuesDe] de esa franja. */
    fun conModuloAgregado(indiceTramo: Int, indiceFranja: Int, despuesDe: Int, tipo: Char, bloqueados: Set<Int> = emptySet()): DisenoNova =
        conFranjaCambiada(indiceTramo, indiceFranja, bloqueados) { mods ->
            val pos = (despuesDe + 1).coerceIn(0, mods.size)
            mods.toMutableList().apply { add(pos, NovaModulo(tipo)) }
        }

    /** Quita un módulo. Siempre queda al menos uno en la franja. */
    fun conModuloQuitado(indiceTramo: Int, indiceFranja: Int, indice: Int, bloqueados: Set<Int> = emptySet()): DisenoNova =
        conFranjaCambiada(indiceTramo, indiceFranja, bloqueados) { mods ->
            if (mods.size <= 1 || indice !in mods.indices) mods
            else mods.toMutableList().apply { removeAt(indice) }
        }

    /** Cambia un fijo por corrediza y al revés. */
    fun conTipoCambiado(indiceTramo: Int, indiceFranja: Int, indice: Int, bloqueados: Set<Int> = emptySet()): DisenoNova =
        conFranjaCambiada(indiceTramo, indiceFranja, bloqueados) { mods ->
            if (indice !in mods.indices) mods
            else mods.toMutableList().apply {
                this[indice] = this[indice].copy(tipo = if (this[indice].esFijo) 'c' else 'f')
            }
        }

    private fun conFranjaCambiada(
        indiceTramo: Int,
        indiceFranja: Int,
        bloqueados: Set<Int>,
        cambio: (List<NovaModulo>) -> List<NovaModulo>
    ): DisenoNova {
        val tramo = tramos.getOrNull(indiceTramo) ?: return this
        val franja = tramo.franjas.getOrNull(indiceFranja) ?: return this
        val nuevos = cambio(franja.modulos)
        if (nuevos == franja.modulos) return this
        val franjas = tramo.franjas.toMutableList()
        franjas[indiceFranja] = franja.copy(modulos = nuevos)
        val ts = tramos.toMutableList()
        ts[indiceTramo] = tramo.copy(franjas = franjas)
        return copy(tramos = ts).conAnchosRepartidos(bloqueados = bloqueados)
    }

    fun aPaquete(): String {
        val cuerpo = tramos.joinToString(" $SEPARADOR_TRAMO ") { tramo ->
            val franjas = tramo.franjas.joinToString(";") { fr ->
                val cabeza = if (fr.esSistema) "s" else "m"
                val mods = fr.modulos.joinToString("") { m ->
                    if (m.ancho != null) "${m.tipo}<${df(m.ancho)}>" else m.tipo.toString()
                }
                // Una franja sin altura se escribe sin `<alto>`, como venía.
                if (fr.alto > 0f) "$cabeza<${df(fr.alto)}>($mods)" else "$cabeza($mods)"
            }
            // El alto propio del tramo va como tag `H<…>` dentro, delante de las franjas: los
            // parsers viejos parten por `;` y solo miran los tokens que empiezan por s o m, así
            // que lo ignoran sin romperse. Con los dos lados distintos van las dos medidas
            // separadas por coma: `H<160,106.2>` es izquierda y derecha.
            val cabezaAlto = when {
                tramo.alto <= 0f && tramo.altoDerecho <= 0f -> ""
                tramo.esInclinado -> "H<${df(tramo.alto)},${df(tramo.altoDerecho)}>;"
                else -> "H<${df(tramo.alto)}>;"
            }
            // Y lo que baja su dintel, si baja: el escalón de arriba.
            val cabezaCaida = when {
                tramo.caida <= 0f && tramo.caidaDerecha <= 0f -> ""
                tramo.esInclinado -> "D<${df(tramo.caida)},${df(tramo.caidaDerecha)}>;"
                else -> "D<${df(tramo.caida)}>;"
            }
            "Tl<${df(tramo.ancho)}>($cabezaAlto$cabezaCaida$franjas)"
        }
        val tags = if (etiquetas.isEmpty()) "" else " " + etiquetas.joinToString(" ")
        return "{nova,$acabado,[${df(ancho)},${df(alto)}:$cuerpo$tags]}"
    }

    companion object {
        const val SEPARADOR_TRAMO = "P<2.5>"

        private fun df(v: Float) = NovaCalculos.df1(v)

        // OJO con los cierres: hay que escapar `]` y `}` aunque en la JVM se acepten sueltos. El
        // motor de Android (ICU) los rechaza y revienta al inicializar la clase, no al usarla, así
        // que la app se cae con ExceptionInInitializerError y las pruebas de escritorio no lo ven.
        /** El alto propio de un tramo: `H<106.2>`. Sin él, el tramo llega al alto de la ventana. */
        private val RE_ALTO_TRAMO = Regex("""^[hH]\s*<\s*([\d.,-]+)\s*>""")
        /** Lo que baja el dintel de un tramo: `D<20>`. Sin él, el tramo arranca en el dintel. */
        private val RE_CAIDA_TRAMO = Regex("""^[dD]\s*<\s*([\d.,-]+)\s*>""")
        private val RE_CABECERA = Regex("""\{nova\s*,\s*([a-z]+)\s*,\s*\[(.*)\]\}""", RegexOption.IGNORE_CASE)
        // La altura es OPCIONAL: los diseños viejos y el de arranque escriben la franja como
        // `s(f)`, sin `<alto>`. Exigirla hacía que el modelo no pudiera leerlos, y entonces las
        // ediciones no hacían nada.
        private val RE_FRANJA = Regex("""^([smSM])\s*(?:<\s*([\d.,-]+)\s*>)?\s*\(""")
        private val RE_MODULO = Regex("""([fcFC])\s*(?:<\s*([\d.,-]+)\s*>)?""")
        private val RE_TRAMO = Regex("""^t[a-z]?\s*<\s*([\d.,-]+)\s*>""", RegexOption.IGNORE_CASE)
        private val RE_ETIQUETA = Regex("""^[AUO]<[^>]*>$""", RegexOption.IGNORE_CASE)

        /**
         * El contorno del vano, `V<…>`, esté donde esté del cuerpo.
         *
         * Se busca suelto y no como trozo separado porque el paquete pasa por manos que quitan los
         * espacios —la pantalla de diseño lo hace al reescribir las medidas—, y ahí la etiqueta se
         * queda pegada al último tramo. Buscándola así, la forma del vano sobrevive igual.
         */
        private val RE_VANO = Regex("""[vV]<[^>]*>""")

        /**
         * Un tag de tramo con una o dos medidas: `160` vale para los dos lados, `160,106.2` es
         * izquierda y derecha. La segunda en null quiere decir "igual que la primera".
         */
        private fun dosMedidas(texto: String): Pair<Float, Float?> {
            val partes = texto.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val izq = num(partes.getOrElse(0) { "0" })
            val der = partes.getOrNull(1)?.let { num(it) }
            return izq to der
        }

        private fun num(s: String): Float = s.replace(",", ".").toFloatOrNull() ?: 0f

        /**
         * Un diseño NUEVO, para empezar de cero, armado con las reglas de la calculadora en vez
         * de dejar al vidriero un solo fijo que tenga que partir a mano.
         *
         * - Divisiones: las que pida [divisiones], o la regla de los 60 si viene en 0.
         * - Tramos: el reparto de siempre, máximo 5 módulos por tramo.
         * - Módulos: el patrón clásico de fijos y corredizas de cada tramo.
         * - Mocheta: si la hoja no llega al alto, una franja de mocheta por tramo, con los paños
         *   que quepan (ninguno pasa de 180 de ancho).
         *
         * [altoHoja] es la altura de puente. Si llega al alto de la ventana, no hay mocheta.
         */
        fun nuevo(
            acabado: String,
            ancho: Float,
            alto: Float,
            altoHoja: Float,
            divisiones: Int = 0,
            anchoParante: Float = 2.5f
        ): DisenoNova {
            val anchoSeguro = ancho.coerceAtLeast(1f)
            val divs = NovaCalculos.divisiones(anchoSeguro, divisiones.coerceAtLeast(0))
            val grupos = NovaCalculos.gruposDivisionesPorTramo(anchoSeguro, divs)
                .ifEmpty { listOf(divs.coerceAtLeast(1)) }
            val hojaSegura = altoHoja.coerceIn(0f, alto)
            val hayMocheta = hojaSegura > 0f && hojaSegura < alto
            val altoSistema = if (hayMocheta) hojaSegura else alto
            val altoMocheta = (alto - hojaSegura).coerceAtLeast(0f)

            val util = (anchoSeguro - (grupos.size - 1) * anchoParante).coerceAtLeast(1f)
            val porModulo = util / divs.coerceAtLeast(1)

            val tramos = grupos.map { nMods ->
                val anchoTramo = porModulo * nMods
                val franjas = mutableListOf<NovaFranja>()
                // Sistema: el patrón clásico de fijos y corredizas del tramo.
                val tipos = NovaCalculos.ordenDivis(nMods, anchoTramo)
                    .filter { it == 'f' || it == 'c' }
                    .ifEmpty { "f" }
                franjas.add(
                    NovaFranja(
                        esSistema = true,
                        alto = altoSistema,
                        modulos = tipos.map { NovaModulo(it, anchoTramo / tipos.length) }
                    )
                )
                if (hayMocheta) {
                    val panos = NovaCalculos.anchMota(anchoTramo).coerceAtLeast(1)
                    franjas.add(
                        NovaFranja(
                            esSistema = false,
                            alto = altoMocheta,
                            modulos = List(panos) { NovaModulo('f', anchoTramo / panos) }
                        )
                    )
                }
                NovaTramo(anchoTramo, franjas)
            }
            return DisenoNova(acabado, anchoSeguro, alto, tramos)
        }

        /** Devuelve null si la cadena no es un paquete de Nova. */
        fun desdePaquete(paquete: String): DisenoNova? {
            val m = RE_CABECERA.find(paquete.trim()) ?: return null
            val acabado = m.groupValues[1].lowercase()
            val dentro = m.groupValues[2]
            val idx = dentro.indexOf(':')
            if (idx < 0) return null
            val dims = dentro.substring(0, idx).split(",")
            if (dims.size < 2) return null
            val ancho = num(dims[0])
            val alto = num(dims[1])

            val tramos = mutableListOf<NovaTramo>()
            val etiquetas = mutableListOf<String>()
            // El contorno del vano se saca antes de partir: no es un tramo ni una franja, y puede
            // venir pegado al último tramo si alguien quitó los espacios por el camino.
            var cuerpo = dentro.substring(idx + 1)
            RE_VANO.find(cuerpo)?.let { m ->
                etiquetas.add(m.value)
                cuerpo = cuerpo.removeRange(m.range)
            }
            for (trozo in partirPorTramos(cuerpo)) {
                val t = trozo.trim()
                if (t.isEmpty()) continue
                if (RE_ETIQUETA.matches(t)) { etiquetas.add(t); continue }
                val cab = RE_TRAMO.find(t)
                val anchoTramo = cab?.groupValues?.get(1)?.let { num(it) } ?: ancho
                val interior = interiorDeParentesis(t) ?: continue
                val tokens = partirNivelSuperior(interior, ';')
                // `H<160>` vale para los dos lados; `H<160,106.2>` es izquierda y derecha, que es
                // como se describe un tramo inclinado. Igual con la caída.
                val alturas = tokens.firstNotNullOfOrNull { tk ->
                    RE_ALTO_TRAMO.find(tk.trim())?.groupValues?.get(1)?.let { dosMedidas(it) }
                }
                val caidas = tokens.firstNotNullOfOrNull { tk ->
                    RE_CAIDA_TRAMO.find(tk.trim())?.groupValues?.get(1)?.let { dosMedidas(it) }
                }
                val franjas = tokens.mapNotNull { franjaDesdeTexto(it) }
                if (franjas.isNotEmpty()) tramos.add(
                    NovaTramo(
                        ancho = anchoTramo,
                        franjas = franjas,
                        alto = alturas?.first ?: 0f,
                        caida = caidas?.first ?: 0f,
                        altoDer = alturas?.second,
                        caidaDer = caidas?.second
                    )
                )
            }
            if (tramos.isEmpty()) return null
            return DisenoNova(acabado, ancho, alto, tramos, etiquetas)
        }

        private fun franjaDesdeTexto(texto: String): NovaFranja? {
            val t = texto.trim()
            val cab = RE_FRANJA.find(t) ?: return null
            val esSistema = cab.groupValues[1].lowercase() == "s"
            // Sin `<alto>` la franja vale 0 y se escribe igual: es lo que hace el diseño viejo.
            val alto = cab.groupValues[2].takeIf { it.isNotBlank() }?.let { num(it) } ?: 0f
            val interior = interiorDeParentesis(t) ?: return null
            val modulos = RE_MODULO.findAll(interior).map { mm ->
                val tipo = mm.groupValues[1].lowercase().first()
                val ancho = mm.groupValues[2].takeIf { it.isNotBlank() }?.let { num(it) }
                NovaModulo(tipo, ancho)
            }.toList()
            if (modulos.isEmpty()) return null
            return NovaFranja(esSistema, alto, modulos)
        }

        /** Corta el cuerpo por los separadores de tramo, respetando los paréntesis anidados. */
        private fun partirPorTramos(cuerpo: String): List<String> {
            val out = mutableListOf<String>()
            val sb = StringBuilder()
            var prof = 0
            var i = 0
            while (i < cuerpo.length) {
                val c = cuerpo[i]
                when {
                    c == '(' -> { prof++; sb.append(c) }
                    c == ')' -> { prof--; sb.append(c) }
                    prof == 0 && cuerpo.startsWith(SEPARADOR_TRAMO, i) -> {
                        out.add(sb.toString()); sb.clear(); i += SEPARADOR_TRAMO.length - 1
                    }
                    prof == 0 && c == ' ' -> { out.add(sb.toString()); sb.clear() }
                    else -> sb.append(c)
                }
                i++
            }
            out.add(sb.toString())
            return out.filter { it.isNotBlank() }
        }

        private fun partirNivelSuperior(texto: String, sep: Char): List<String> {
            val out = mutableListOf<String>()
            val sb = StringBuilder()
            var prof = 0
            for (c in texto) {
                when {
                    c == '(' -> { prof++; sb.append(c) }
                    c == ')' -> { prof--; sb.append(c) }
                    c == sep && prof == 0 -> { out.add(sb.toString()); sb.clear() }
                    else -> sb.append(c)
                }
            }
            out.add(sb.toString())
            return out.filter { it.isNotBlank() }
        }

        /** El contenido del primer paréntesis equilibrado, o null si no hay. */
        private fun interiorDeParentesis(texto: String): String? {
            val abre = texto.indexOf('(')
            if (abre < 0) return null
            var prof = 0
            for (i in abre until texto.length) {
                when (texto[i]) {
                    '(' -> prof++
                    ')' -> { prof--; if (prof == 0) return texto.substring(abre + 1, i) }
                }
            }
            return null
        }
    }
}
