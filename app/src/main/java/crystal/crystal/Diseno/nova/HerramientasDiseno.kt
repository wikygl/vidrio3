package crystal.crystal.Diseno.nova

import kotlin.math.abs

/**
 * Las herramientas para diseñar a mano en novaDiseño (pedido del usuario el 2026-09-25): poner un
 * parante o un puente justo donde hace falta, moverlos, y que se peguen solos a lo que tienen
 * cerca —el borde de un escalón, otro puente, otro parante— con el imán.
 *
 * El caso que las pide es el vano escalonado: la calculadora propone un diseño, pero hay otros
 * igual de buenos. Por ejemplo, un parante en el borde del primer escalón, un puente en el segundo
 * hasta ese parante con la mocheta abajo y el sistema invertido, y el último trozo como un sistema
 * largo de cinco hojas. Para armarlo rápido hace falta poner y quitar tramos, puentes y módulos
 * sin cuentas, y que todo se alinee solo.
 *
 * Coordenadas en centímetros, como el contorno del vano: **x** desde el canto izquierdo de la
 * ventana y **y** desde el dintel hacia abajo. Todas las operaciones devuelven un diseño nuevo; si
 * no se puede, el mismo sin tocar.
 */
object HerramientasDiseno {

    /** Hasta dónde atrae el imán: 3 cm de la medida real. */
    const val IMAN_CM = 3f

    /** Lo mínimo que puede quedar de un tramo o de una franja al mover o partir. */
    const val MINIMO_CM = 5f

    /** Hasta dónde un desnivel es descuadre y no pendiente (N-06: una pulgada). */
    const val DESCUADRE_CM = 2.5f

    // ==================== EL IMÁN ====================

    /** Un valor ya pasado por el imán: [guia] es la línea a la que se pegó, o null si quedó suelto. */
    data class Pegado(val valor: Float, val guia: Float?)

    /** Pega [valor] a la guía más cercana si está a [tolerancia] o menos. Con el imán apagado, nada. */
    fun pegar(valor: Float, guias: List<Float>, activo: Boolean = true, tolerancia: Float = IMAN_CM): Pegado {
        if (!activo) return Pegado(valor, null)
        val cerca = guias.minByOrNull { abs(it - valor) } ?: return Pegado(valor, null)
        return if (abs(cerca - valor) <= tolerancia) Pegado(cerca, cerca) else Pegado(valor, null)
    }

    /**
     * Las líneas verticales que atraen: los cantos de la ventana, los vértices del vano (el borde de
     * un escalón) y los ejes de los parantes. [sinParante] deja fuera el que se está moviendo.
     */
    fun guiasVerticales(d: DisenoNova, anchoParante: Float = 2.5f, sinParante: Int? = null): List<Float> {
        val (x0, _) = origenDelVano(d)
        val vano = d.contornoVano.map { it.first - x0 }
        val parantes = ejesDeParantes(d, anchoParante).filterIndexed { i, _ -> i != sinParante }
        return (listOf(0f, d.ancho) + vano + parantes).distinctBy { Math.round(it * 10) }
    }

    /**
     * Las líneas horizontales que atraen: el dintel y el alféizar de la ventana, los vértices del
     * vano y todos los puentes de todos los tramos. [sinPuente] (tramo, límite) deja fuera el que se
     * está moviendo.
     */
    fun guiasHorizontales(d: DisenoNova, sinPuente: Pair<Int, Int>? = null): List<Float> {
        val (_, y0) = origenDelVano(d)
        val vano = d.contornoVano.map { it.second - y0 }
        val puentes = d.tramos.indices.flatMap { i ->
            limitesDePuente(d, i).mapIndexedNotNull { k, y -> if (sinPuente == i to k) null else y }
        }
        return (listOf(0f, d.alto) + vano + puentes).distinctBy { Math.round(it * 10) }
    }

    // ==================== DÓNDE ESTÁ CADA COSA ====================

    /** Dónde empieza cada tramo, en x. Entre tramo y tramo va un parante. */
    fun iniciosDeTramos(d: DisenoNova, anchoParante: Float = 2.5f): List<Float> {
        val out = mutableListOf<Float>()
        var x = 0f
        d.tramos.forEach { t -> out.add(x); x += t.ancho + anchoParante }
        return out
    }

    /** El eje de cada parante entre tramos: el del parante i está entre el tramo i y el i + 1. */
    fun ejesDeParantes(d: DisenoNova, anchoParante: Float = 2.5f): List<Float> {
        val inicios = iniciosDeTramos(d, anchoParante)
        return (0 until d.tramos.size - 1).map { i -> inicios[i] + d.tramos[i].ancho + anchoParante / 2f }
    }

    /** El tramo que ocupa esa x, o null si cae en un parante o fuera. */
    fun tramoEnX(d: DisenoNova, x: Float, anchoParante: Float = 2.5f): Int? {
        val inicios = iniciosDeTramos(d, anchoParante)
        return d.tramos.indices.firstOrNull { i -> x >= inicios[i] && x <= inicios[i] + d.tramos[i].ancho }
    }

    /**
     * Las alturas de las franjas del tramo, de abajo arriba, sumando el alto del tramo. Las que
     * están en 0 ("se reparte sola") se llevan a partes iguales lo que dejan las demás.
     */
    fun alturasDeFranjas(d: DisenoNova, indiceTramo: Int): List<Float> {
        val t = d.tramos.getOrNull(indiceTramo) ?: return emptyList()
        val altoT = d.altoDeTramo(indiceTramo)
        // Un sistema sin alto escrito, con mochetas, es la hoja de la casa: 5/7 del alto (N-29), como
        // lo dibuja la vista. Antes se repartía por igual con las mochetas, el dibujo mostraba 5/7 y
        // Nova no encontraba hoja (el trapecio del usuario llegaba sin puente, 2026-09-26).
        val conMocheta = t.franjas.any { !it.esSistema }
        // Solo si NINGUNA franja tiene alto escrito: con la mocheta escrita, el sistema es lo que queda.
        val sistemaAuto = conMocheta && t.franjas.count { it.esSistema } == 1 && t.franjas.all { it.alto <= 0f }
        val franjas = if (sistemaAuto) t.franjas.map { if (it.esSistema) it.copy(alto = altoT * 5f / 7f) else it } else t.franjas
        val fijas = franjas.filter { it.alto > 0f }.sumOf { it.alto.toDouble() }.toFloat()
        val libres = franjas.count { it.alto <= 0f }
        val porLibre = if (libres > 0) ((altoT - fijas) / libres).coerceAtLeast(0f) else 0f
        val alturas = franjas.map { if (it.alto > 0f) it.alto else porLibre }
        // Si no suman el alto del tramo, se estiran en proporción: así los puentes caen donde se ven.
        val suma = alturas.sum()
        return if (suma > 0f && abs(suma - altoT) > 0.05f) alturas.map { it * altoT / suma } else alturas
    }

    /** La y (desde el dintel) de cada división entre franjas del tramo, de abajo arriba. */
    fun limitesDePuente(d: DisenoNova, indiceTramo: Int): List<Float> {
        val alturas = alturasDeFranjas(d, indiceTramo)
        if (alturas.size < 2) return emptyList()
        val piso = d.caidaDeTramo(indiceTramo) + d.altoDeTramo(indiceTramo)
        var desdeAbajo = 0f
        return (0 until alturas.size - 1).map { k -> desdeAbajo += alturas[k]; piso - desdeAbajo }
    }

    // ==================== PARANTES ====================

    /**
     * Un parante con el eje en [x]: parte el tramo que está ahí en dos. Cada módulo queda del lado
     * donde está su centro (con al menos uno por lado), y los anchos se ajustan a cada trozo.
     */
    fun conParanteEn(d: DisenoNova, x: Float, anchoParante: Float = 2.5f): DisenoNova {
        val i = tramoEnX(d, x, anchoParante) ?: return d
        val t = d.tramos[i]
        val inicio = iniciosDeTramos(d, anchoParante)[i]
        val anchoIzq = x - inicio - anchoParante / 2f
        val anchoDer = t.ancho - anchoIzq - anchoParante
        if (anchoIzq < MINIMO_CM || anchoDer < MINIMO_CM) return d
        val corte = x - inicio   // en coordenadas del tramo
        val izq = mutableListOf<NovaFranja>()
        val der = mutableListOf<NovaFranja>()
        t.franjas.forEach { fr ->
            val (a, b) = partirModulos(fr.modulos, t.ancho, corte)
            izq.add(fr.copy(modulos = ajustar(a, anchoIzq), parantes = emptyList()))
            der.add(fr.copy(modulos = ajustar(b, anchoDer), parantes = emptyList()))
        }
        val nuevos = d.tramos.toMutableList()
        nuevos[i] = t.copy(ancho = anchoIzq, franjas = izq, altoDer = null, caidaDer = null)
        nuevos.add(i + 1, t.copy(ancho = anchoDer, franjas = der, pliegue = null, contorno = emptyList(), altoDer = null, caidaDer = null))
        return conFormaDelVano(d.copy(tramos = nuevos), anchoParante)
    }

    /**
     * Mueve el parante [indice] (entre el tramo [indice] y el siguiente) para que su eje quede en
     * [x]. Los dos tramos cambian de ancho y sus módulos se reajustan; el resto no se toca.
     */
    fun conParanteMovido(d: DisenoNova, indice: Int, x: Float, anchoParante: Float = 2.5f): DisenoNova {
        val a = d.tramos.getOrNull(indice) ?: return d
        val b = d.tramos.getOrNull(indice + 1) ?: return d
        val inicios = iniciosDeTramos(d, anchoParante)
        val inicio = inicios[indice]
        // El paquete guarda los anchos truncados a un decimal (103.35 → 103.3, 232.45 → 232.4) y su
        // suma pierde una décima: en Formas el tramo 2 quedaba en 233.6 y no en 233.7. Si el tramo de
        // la derecha es el último, su borde es el canto de la ventana: manda el ancho de la ventana.
        // Antes: `val total = a.ancho + b.ancho`.
        val total = if (indice + 1 == d.tramos.lastIndex) d.ancho - inicio - anchoParante
                    else inicios[indice + 1] + b.ancho - inicio - anchoParante
        val anchoA = x - inicio - anchoParante / 2f
        val anchoB = total - anchoA
        if (anchoA < MINIMO_CM || anchoB < MINIMO_CM) return d
        val nuevos = d.tramos.toMutableList()
        nuevos[indice] = a.copy(ancho = anchoA, franjas = a.franjas.map { it.copy(modulos = ajustar(it.modulos, anchoA)) })
        nuevos[indice + 1] = b.copy(ancho = anchoB, franjas = b.franjas.map { it.copy(modulos = ajustar(it.modulos, anchoB)) })
        return conFormaDelVano(d.copy(tramos = nuevos), anchoParante)
    }

    // ==================== EL PARANTE EN UN ESCALÓN ====================
    // Dictado por el usuario (2026-09-26): un parante nunca queda centrado en la esquina de un
    // escalón salvo que el usuario lo decida. Va DENTRO (del lado del tramo bajo, el que tiene el
    // escalón encima o debajo) o FUERA (del lado del tramo alto). En el vano de Formas, dentro: parante
    // 77.2, tramo 1 102.1 × 77.2 y tramo 2 233.7 × 109.4; fuera: parante 144.4.

    enum class LadoParante(val nombre: String) { DENTRO("Dentro"), FUERA("Fuera"), CENTRADO("Centrado") }

    /**
     * Una manera de dejar el parante [indice] en su escalón: dónde queda su eje, cuánto mide (el
     * alto del vano donde está, el mayor si pisa los dos lados) y cómo quedan los dos tramos (ancho y
     * alto del rectángulo del sistema, [altoDelSistema]).
     */
    data class OpcionParante(
        val lado: LadoParante, val eje: Float, val largoParante: Float,
        val anchoIzq: Float, val altoIzq: Float, val anchoDer: Float, val altoDer: Float
    )

    /**
     * La x del escalón donde está centrado el parante [indice], o null si no está en uno. Es un
     * escalón si el eje cae en un vértice del vano y el alto del vano cambia de un lado a otro más
     * que un descuadre (N-06).
     */
    fun escalonDelParante(d: DisenoNova, indice: Int, anchoParante: Float = 2.5f): Float? {
        val puntos = puntosDelVano(d)
        if (puntos.size < 3) return null
        val eje = ejesDeParantes(d, anchoParante).getOrNull(indice) ?: return null
        val x = puntos.map { it.first }.filter { abs(it - eje) <= 0.15f }.minByOrNull { abs(it - eje) } ?: return null
        val izq = altoDelVanoEn(puntos, x - 0.5f) ?: return null
        val der = altoDelVanoEn(puntos, x + 0.5f) ?: return null
        return if (abs(izq - der) > DESCUADRE_CM) x else null
    }

    /** Las tres maneras de dejar el parante [indice] en su escalón; vacía si no está en uno. */
    fun opcionesDelParante(d: DisenoNova, indice: Int, anchoParante: Float = 2.5f): List<OpcionParante> {
        val x = escalonDelParante(d, indice, anchoParante) ?: return emptyList()
        val puntos = puntosDelVano(d)
        val bajoALaIzquierda = (altoDelVanoEn(puntos, x - 0.5f) ?: 0f) < (altoDelVanoEn(puntos, x + 0.5f) ?: 0f)
        val medio = anchoParante / 2f
        val ejes = listOf(
            LadoParante.DENTRO to if (bajoALaIzquierda) x - medio else x + medio,
            LadoParante.FUERA to if (bajoALaIzquierda) x + medio else x - medio,
            LadoParante.CENTRADO to x
        )
        return ejes.mapNotNull { (lado, eje) ->
            val m = if (abs(eje - ejesDeParantes(d, anchoParante)[indice]) <= 0.01f) d
                    else conParanteMovido(d, indice, eje, anchoParante).takeIf { it !== d } ?: return@mapNotNull null
            val largo = largoDelParante(m, indice, anchoParante) ?: return@mapNotNull null
            OpcionParante(
                lado, eje, largo,
                m.tramos[indice].ancho, altoDelSistema(m, indice, anchoParante) ?: m.altoDeTramo(indice),
                m.tramos[indice + 1].ancho, altoDelSistema(m, indice + 1, anchoParante) ?: m.altoDeTramo(indice + 1)
            )
        }
    }

    /**
     * Lo que mide el parante [indice] en un vano con forma: el alto del vano donde queda; si pisa
     * los dos lados de un escalón, el mayor (N-94). Null sin vano.
     */
    fun largoDelParante(d: DisenoNova, indice: Int, anchoParante: Float = 2.5f): Float? {
        val puntos = puntosDelVano(d)
        if (puntos.size < 3) return null
        val eje = ejesDeParantes(d, anchoParante).getOrNull(indice) ?: return null
        val medio = anchoParante / 2f
        return listOf(eje - medio + 0.05f, eje, eje + medio - 0.05f)
            .mapNotNull { altoDelVanoEn(puntos, it) }.maxOrNull()
    }

    /**
     * Lo que mide la corrida de puente [indiceLimite] del tramo en un vano con forma: lo que el vano
     * le deja a su altura; si pisa los dos lados de un escalón, el mayor (N-97). El puente va dentro
     * de la mocheta que separa (la de abajo en un invertido). Null sin vano.
     */
    fun largoDelPuente(d: DisenoNova, indiceTramo: Int, indiceLimite: Int, altoPuente: Float = 2.5f, anchoParante: Float = 2.5f): Float? {
        val puntos = puntosDelVano(d)
        if (puntos.size < 3) return null
        val t = d.tramos.getOrNull(indiceTramo) ?: return null
        val deAbajo = t.franjas.getOrNull(indiceLimite) ?: return null
        val deArriba = t.franjas.getOrNull(indiceLimite + 1) ?: return null
        val y = limitesDePuente(d, indiceTramo).getOrNull(indiceLimite) ?: return null
        val mochetaAbajo = !deAbajo.esSistema && deArriba.esSistema
        val arriba = if (mochetaAbajo) y else y - altoPuente
        val inicio = iniciosDeTramos(d, anchoParante)[indiceTramo]
        return listOf(arriba + 0.05f, arriba + altoPuente / 2f, arriba + altoPuente - 0.05f)
            .mapNotNull { anchoDelVanoEn(puntos, it, inicio, inicio + t.ancho) }.maxOrNull()
    }

    /** El diseño con el parante [indice] dejado como dice [opcion]. */
    fun conParanteEnEscalon(d: DisenoNova, indice: Int, opcion: OpcionParante, anchoParante: Float = 2.5f): DisenoNova {
        val eje = ejesDeParantes(d, anchoParante).getOrNull(indice) ?: return d
        return if (abs(opcion.eje - eje) <= 0.01f) d else conParanteMovido(d, indice, opcion.eje, anchoParante)
    }

    /**
     * El alto del rectángulo donde entra el sistema del tramo (N-66): del dintel más bajo al alféizar
     * más alto sobre su ancho. Con un descuadre de hasta 2.5 manda la medida mayor (N-06): en Formas
     * el alféizar del recorte está a 109.4 en un canto y a 108.5 en el otro, y el tramo mide 109.4.
     */
    fun altoDelSistema(d: DisenoNova, indiceTramo: Int, anchoParante: Float = 2.5f): Float? {
        val puntos = puntosDelVano(d)
        val t = d.tramos.getOrNull(indiceTramo) ?: return null
        if (puntos.size < 3) return null
        val inicio = iniciosDeTramos(d, anchoParante)[indiceTramo]
        val cortes = muestrasEnTramo(puntos, inicio, inicio + t.ancho).mapNotNull { bordesEn(puntos, it) }
        if (cortes.isEmpty()) return null
        val techoMasBajo = cortes.maxOf { it.first }
        val techo = cortes.map { it.first }.filter { it >= techoMasBajo - DESCUADRE_CM }.minOrNull() ?: techoMasBajo
        val pisoMasAlto = cortes.minOf { it.second }
        val piso = cortes.map { it.second }.filter { it <= pisoMasAlto + DESCUADRE_CM }.maxOrNull() ?: pisoMasAlto
        return piso - techo
    }

    /**
     * El rectángulo de una franja dentro del vano (ancho × alto): la estrategia de las formas es
     * partir en rectángulos y calcular cada uno (dictado el 2026-09-26). El alto es el de la franja;
     * el ancho, lo que el vano le deja a su altura dentro del tramo. En Formas, la mocheta de abajo
     * del tramo 2 mide 151.7 y no 233.7: el recorte de la derecha le quita 82. Sin vano, el tramo.
     */
    fun rectanguloDeFranja(d: DisenoNova, indiceTramo: Int, indiceFranja: Int, anchoParante: Float = 2.5f): Pair<Float, Float>? {
        val t = d.tramos.getOrNull(indiceTramo) ?: return null
        val alturas = alturasDeFranjas(d, indiceTramo)
        val alto = alturas.getOrNull(indiceFranja) ?: return null
        val puntos = puntosDelVano(d)
        if (puntos.size < 3) return t.ancho to alto
        val (techo, piso) = bandaDeFranja(d, indiceTramo, indiceFranja) ?: return null
        val inicio = iniciosDeTramos(d, anchoParante)[indiceTramo]
        // El rectángulo que la CONTIENE (dictado el 2026-09-26: el vidrio va entero y se corta en
        // obra): lo más a la izquierda y lo más a la derecha que llega el vano en su franja. Antes se
        // medía a media altura, y una mocheta bajo un lado inclinado salía con un promedio (197.8 en
        // vez de 208.6).
        val ancho = extensionEnBanda(puntos, techo, piso, inicio, inicio + t.ancho)?.let { it.second - it.first } ?: t.ancho
        return ancho to alto
    }

    /**
     * Cómo queda cada módulo de una franja dentro del vano (dictado el 2026-09-26: la geometría se
     * mide módulo por módulo; antes se achicaba la franja entera en proporción y el fijo recto de
     * la Vna4 salía de 58.5 cuando mide 65.2).
     *
     * - [contenido]: el ancho del rectángulo que lo contiene (el vidrio entero, se corta en obra);
     * - [abajo] y [arriba]: su borde de abajo y de arriba dentro del vano;
     * - [diagonalIzq] / [diagonalDer]: el largo del lado inclinado, si el vano lo corta en diagonal
     *   por ese costado (0 si es recto). En la Vna4 el fijo de la izquierda: contenido 56.5, abajo
     *   2.7 y diagonal 124.3.
     */
    data class GeometriaModulo(
        val contenido: Float, val abajo: Float, val arriba: Float,
        val diagonalIzq: Float, val diagonalDer: Float
    ) {
        val inclinado: Boolean get() = diagonalIzq > 0f || diagonalDer > 0f
    }

    fun geometriaDeModulos(d: DisenoNova, indiceTramo: Int, indiceFranja: Int, anchoParante: Float = 2.5f): List<GeometriaModulo> {
        val puntos = puntosDelVano(d)
        val t = d.tramos.getOrNull(indiceTramo) ?: return emptyList()
        val fr = t.franjas.getOrNull(indiceFranja) ?: return emptyList()
        if (puntos.size < 3) return emptyList()
        val (techo, piso) = bandaDeFranja(d, indiceTramo, indiceFranja) ?: return emptyList()
        val sinAncho = fr.modulos.count { it.ancho == null }
        val resto = if (sinAncho > 0) ((t.ancho - fr.modulos.sumOf { (it.ancho ?: 0f).toDouble() }.toFloat()) / sinAncho) else 0f
        var x = iniciosDeTramos(d, anchoParante)[indiceTramo]
        val alto = piso - techo
        return fr.modulos.map { m ->
            val w = m.ancho ?: resto
            val x0 = x; val x1 = x + w
            x += w
            val arriba = extensionEn(puntos, techo + 0.05f, x0, x1)
            val medio = extensionEn(puntos, (techo + piso) / 2f, x0, x1)
            val abajo = extensionEn(puntos, piso - 0.05f, x0, x1)
            val contenido = extensionEnBanda(puntos, techo, piso, x0, x1)?.let { it.second - it.first } ?: w
            // Un costado es inclinado si el vano lo corta de arriba a abajo en línea recta (el
            // punto medio cae a mitad de camino). Un escalón o una viga cambian de golpe: esos son
            // recortes (N-96 "polis"), no diagonales.
            fun diagonal(a: Float?, mid: Float?, b: Float?): Float {
                if (a == null || mid == null || b == null) return 0f
                val salto = abs(b - a)
                if (salto <= DESCUADRE_CM) return 0f
                if (abs(mid - (a + b) / 2f) > salto * 0.15f) return 0f
                return kotlin.math.sqrt(salto * salto + alto * alto)
            }
            GeometriaModulo(
                contenido = contenido,
                abajo = abajo?.let { it.second - it.first } ?: 0f,
                arriba = arriba?.let { it.second - it.first } ?: 0f,
                diagonalIzq = diagonal(arriba?.first, medio?.first, abajo?.first),
                diagonalDer = diagonal(arriba?.second, medio?.second, abajo?.second)
            )
        }
    }

    /**
     * Los módulos repartidos por igual se reparten lo que el vano deja de la franja, no el ancho
     * del tramo (dictado el 2026-09-26, Vna4): con el lado inclinado, el sistema mide 187.1 en su
     * borde de arriba y cada módulo 62.4, no 208.6 / 3 = 69.5. Lo que queda fuera del vano va al
     * módulo de la punta (el fijo inclinado: 21.5 + 62.4; abajo le quedan 8.4). Un borde a menos
     * de un descuadre (2.5) cuenta como recto. [franjas] = (tramo, franja) que se repartieron.
     */
    fun conModulosDentroDelVano(d: DisenoNova, franjas: Set<Pair<Int, Int>>, anchoParante: Float = 2.5f): DisenoNova {
        val puntos = puntosDelVano(d)
        if (puntos.size < 3 || franjas.isEmpty()) return d
        val inicios = iniciosDeTramos(d, anchoParante)
        return d.copy(tramos = d.tramos.mapIndexed { i, t ->
            t.copy(franjas = t.franjas.mapIndexed { k, fr ->
                val n = fr.modulos.size
                if ((i to k) !in franjas || n < 2) return@mapIndexed fr
                val (techo, piso) = bandaDeFranja(d, i, k) ?: return@mapIndexed fr
                val ini = inicios[i]
                val fin = ini + t.ancho
                val (a, b) = extensionEnBanda(puntos, techo, piso, ini, fin) ?: return@mapIndexed fr
                val fueraIzq = (a - ini).takeIf { it > DESCUADRE_CM } ?: 0f
                val fueraDer = (fin - b).takeIf { it > DESCUADRE_CM } ?: 0f
                if (fueraIzq == 0f && fueraDer == 0f) return@mapIndexed fr
                val w = (t.ancho - fueraIzq - fueraDer) / n
                fr.copy(modulos = fr.modulos.mapIndexed { j, m ->
                    m.copy(ancho = w + (if (j == 0) fueraIzq else 0f) + (if (j == n - 1) fueraDer else 0f))
                })
            })
        })
    }

    /** Dónde empieza y acaba el vano sobre la horizontal [y], dentro de [xIni]..[xFin] (el trozo más largo). */
    private fun extensionEn(puntos: List<Pair<Float, Float>>, y: Float, xIni: Float, xFin: Float): Pair<Float, Float>? {
        val cruces = mutableListOf<Float>()
        for (i in puntos.indices) {
            val (ax, ay) = puntos[i]
            val (bx, by) = puntos[(i + 1) % puntos.size]
            if (abs(by - ay) <= 0.01f) continue
            if (y < minOf(ay, by) || y >= maxOf(ay, by)) continue
            cruces.add(ax + (y - ay) / (by - ay) * (bx - ax))
        }
        cruces.sort()
        return cruces.chunked(2).filter { it.size == 2 }
            .map { (a, b) -> maxOf(a, xIni) to minOf(b, xFin) }
            .filter { it.second - it.first > 0f }
            .maxByOrNull { it.second - it.first }
    }

    /** Lo más a la izquierda y lo más a la derecha que llega el vano en la banda [techo]..[piso]. */
    private fun extensionEnBanda(puntos: List<Pair<Float, Float>>, techo: Float, piso: Float, xIni: Float, xFin: Float): Pair<Float, Float>? {
        val ys = (listOf(techo + 0.05f, (techo + piso) / 2f, piso - 0.05f) +
            puntos.map { it.second }.filter { it > techo + 0.05f && it < piso - 0.05f }.flatMap { listOf(it - 0.05f, it + 0.05f) })
        val tramos = ys.mapNotNull { extensionEn(puntos, it, xIni, xFin) }
        if (tramos.isEmpty()) return null
        return tramos.minOf { it.first } to tramos.maxOf { it.second }
    }

    /**
     * Cuánto le quita el vano a cada módulo del sistema del tramo, por arriba y por abajo (dictado el
     * 2026-09-26: el sistema también se parte en rectángulos por módulo cuando el vano lo corta). En
     * "polis" el techo baja 39.2 sobre el fijo de la izquierda: (39.2, 0). Se mide en el centro del
     * módulo; menos de un descuadre (2.5) no cuenta. Vacía sin vano.
     */
    fun recortesDelSistema(d: DisenoNova, indiceTramo: Int, anchoParante: Float = 2.5f): List<Pair<Float, Float>> {
        val puntos = puntosDelVano(d)
        val t = d.tramos.getOrNull(indiceTramo) ?: return emptyList()
        val iS = t.franjas.indexOfFirst { it.esSistema }
        if (puntos.size < 3 || iS < 0) return emptyList()
        val (techo, piso) = bandaDeFranja(d, indiceTramo, iS) ?: return emptyList()
        // Un módulo contra un lado inclinado no está recortado: es un trapecio (ver
        // geometriaDeModulos). Antes el fijo de la Vna4 salía "recortado" 75.7 por abajo.
        val inclinados = geometriaDeModulos(d, indiceTramo, iS, anchoParante).map { it.inclinado }
        var x = iniciosDeTramos(d, anchoParante)[indiceTramo]
        val mods = t.franjas[iS].modulos
        val sinAncho = mods.count { it.ancho == null }
        val resto = if (sinAncho > 0) ((t.ancho - mods.sumOf { (it.ancho ?: 0f).toDouble() }.toFloat()) / sinAncho) else 0f
        return mods.mapIndexed { k, m ->
            val w = m.ancho ?: resto
            val centro = x + w / 2f
            x += w
            if (inclinados.getOrNull(k) == true) return@mapIndexed 0f to 0f
            val (arriba, abajo) = bordesEn(puntos, centro) ?: return@mapIndexed 0f to 0f
            val cortaArriba = (arriba - techo).takeIf { it > DESCUADRE_CM } ?: 0f
            val cortaAbajo = (piso - abajo).takeIf { it > DESCUADRE_CM } ?: 0f
            cortaArriba to cortaAbajo
        }
    }

    /**
     * Lo que mide el borde de arriba del sistema del tramo si el vano lo corta (una viga), o null si
     * llega de lado a lado. El fijo corredizo solo llega hasta donde empieza el corte (dictado el
     * 2026-09-26): en "polis", 190.2 de un tramo de 252. Se mide un descuadre por debajo del techo.
     */
    fun anchoArribaDelSistema(d: DisenoNova, indiceTramo: Int, anchoParante: Float = 2.5f): Float? {
        val puntos = puntosDelVano(d)
        val t = d.tramos.getOrNull(indiceTramo) ?: return null
        val iS = t.franjas.indexOfFirst { it.esSistema }
        if (puntos.size < 3 || iS < 0) return null
        val (techo, _) = bandaDeFranja(d, indiceTramo, iS) ?: return null
        val inicio = iniciosDeTramos(d, anchoParante)[indiceTramo]
        val ancho = anchoDelVanoEn(puntos, techo + DESCUADRE_CM + 0.1f, inicio, inicio + t.ancho) ?: return null
        return ancho.takeIf { abs(it - t.ancho) > 0.15f }
    }

    /**
     * Una columna que entra en el sistema por un costado (dictado el 2026-09-26, "polis" ventana 2:
     * 36.4 × 97.2 desde la pared izquierda, a media altura del fijo). [caraArriba] y [caraAbajo]
     * son sus caras horizontales, medidas desde la pared; [alto] su cara vertical; [paredArriba] y
     * [paredAbajo] lo que queda de pared encima y debajo de ella, dentro del sistema.
     */
    data class CorteLateral(
        val caraArriba: Float, val caraAbajo: Float, val alto: Float,
        val paredArriba: Float, val paredAbajo: Float
    )

    /**
     * Las columnas que entran en el sistema del tramo por la izquierda y por la derecha, o null. Una
     * columna son dos lados casi horizontales del vano dentro de la franja del sistema que salen del
     * costado del tramo y entran más que un descuadre (2.5).
     */
    fun cortesLateralesDelSistema(d: DisenoNova, indiceTramo: Int, anchoParante: Float = 2.5f): Pair<CorteLateral?, CorteLateral?> {
        val puntos = puntosDelVano(d)
        val t = d.tramos.getOrNull(indiceTramo) ?: return null to null
        val iS = t.franjas.indexOfFirst { it.esSistema }
        if (puntos.size < 3 || iS < 0) return null to null
        val (techo, piso) = bandaDeFranja(d, indiceTramo, iS) ?: return null to null
        val inicio = iniciosDeTramos(d, anchoParante)[indiceTramo]
        val fin = inicio + t.ancho
        fun lado(izquierdo: Boolean): CorteLateral? {
            val caras = puntos.indices.mapNotNull { i ->
                val (ax, ay) = puntos[i]
                val (bx, by) = puntos[(i + 1) % puntos.size]
                if (abs(by - ay) > 0.5f) return@mapNotNull null
                val y = (ay + by) / 2f
                if (y <= techo + 0.5f || y >= piso - 0.5f) return@mapNotNull null
                val x0 = minOf(ax, bx)
                val x1 = maxOf(ax, bx)
                val sale = if (izquierdo) x0 <= inicio + 0.5f && x1 > inicio + DESCUADRE_CM && x1 < fin
                           else x1 >= fin - 0.5f && x0 < fin - DESCUADRE_CM && x0 > inicio
                if (sale) y to (x1 - x0) else null
            }.sortedBy { it.first }
            if (caras.size != 2) return null
            val (arriba, abajo) = caras
            return CorteLateral(arriba.second, abajo.second, abajo.first - arriba.first, arriba.first - techo, piso - abajo.first)
        }
        return lado(true) to lado(false)
    }

    /** La franja [indiceFranja] del tramo en y: (techo, piso), desde el dintel. */
    private fun bandaDeFranja(d: DisenoNova, indiceTramo: Int, indiceFranja: Int): Pair<Float, Float>? {
        val alturas = alturasDeFranjas(d, indiceTramo)
        val alto = alturas.getOrNull(indiceFranja) ?: return null
        val piso = d.caidaDeTramo(indiceTramo) + d.altoDeTramo(indiceTramo) - alturas.take(indiceFranja).sum()
        return (piso - alto) to piso
    }

    /** El trozo más largo del vano sobre la horizontal [y], dentro de [xIni]..[xFin]. */
    private fun anchoDelVanoEn(puntos: List<Pair<Float, Float>>, y: Float, xIni: Float, xFin: Float): Float? {
        val cruces = mutableListOf<Float>()
        for (i in puntos.indices) {
            val (ax, ay) = puntos[i]
            val (bx, by) = puntos[(i + 1) % puntos.size]
            if (abs(by - ay) <= 0.01f) continue
            if (y < minOf(ay, by) || y >= maxOf(ay, by)) continue
            cruces.add(ax + (y - ay) / (by - ay) * (bx - ax))
        }
        cruces.sort()
        return cruces.chunked(2).filter { it.size == 2 }
            .map { (a, b) -> (minOf(b, xFin) - maxOf(a, xIni)) }
            .filter { it > 0f }.maxOrNull()
    }

    // ==================== QUÉ ES CADA TRAMO ====================
    // Dictado por el usuario (2026-09-26): antes de partir un tramo en rectángulos hay que saber qué
    // es el tramo entero. El tramo 2 de Formas es un invertido (ni) con la mocheta recortada: por ser
    // invertido no admite Múltiple (N-03), y eso decide su puente antes de cualquier cuenta.

    /** El modelo de un tramo, por el orden de sus franjas de abajo arriba (N-33). */
    enum class ModeloTramo(val nombre: String) {
        SIN_PUENTE("sin puente"), NORMAL("normal"), INVERTIDO("invertido"), BANDERA("bandera"), COMPUESTO("compuesto")
    }

    /** Un puente: su nombre y su alto. */
    data class PuenteTramo(val nombre: String, val alto: Float) {
        val esMultiple: Boolean get() = nombre.lowercase().let { it.contains("múlt") || it.contains("multi") }
    }

    /** El preestablecido de todo lo que no es aparente normal (N-03). */
    val TUBO_POR_DEFECTO = PuenteTramo("tubo 2⅜ x 1", 2.5f)

    fun modeloDeTramo(d: DisenoNova, indiceTramo: Int): ModeloTramo? {
        val t = d.tramos.getOrNull(indiceTramo) ?: return null
        return when (t.franjas.joinToString("") { if (it.esSistema) "s" else "m" }) {
            "s" -> ModeloTramo.SIN_PUENTE
            "sm" -> ModeloTramo.NORMAL
            "ms" -> ModeloTramo.INVERTIDO
            "msm" -> ModeloTramo.BANDERA
            else -> ModeloTramo.COMPUESTO
        }
    }

    /**
     * El modelo de la ventana entera, desde su diseño: el de sus tramos si todos son iguales, y si no
     * compuesto (N-89). En "polis" los dos tramos son invertidos: invertido. En Formas uno no tiene
     * mocheta y el otro es invertido: compuesto.
     */
    fun modeloDeLaVentana(d: DisenoNova): ModeloTramo? {
        val modelos = d.tramos.indices.mapNotNull { modeloDeTramo(d, it) }.distinct()
        return when {
            modelos.isEmpty() -> null
            modelos.size == 1 -> modelos.first()
            else -> ModeloTramo.COMPUESTO
        }
    }

    /**
     * El puente de un tramo (N-03): el que se eligió en Nova, salvo que el tramo no lo admita. Solo
     * el aparente normal admite Múltiple; un invertido, una bandera o un compuesto llevan el tubo
     * aunque la ventana tenga Múltiple. Un tramo sin mocheta no lleva puente: null.
     */
    fun puenteDeTramo(d: DisenoNova, indiceTramo: Int, elegido: PuenteTramo): PuenteTramo? {
        val modelo = modeloDeTramo(d, indiceTramo) ?: return null
        if (modelo == ModeloTramo.SIN_PUENTE) return null
        val admiteMultiple = d.acabado == "apa" && modelo == ModeloTramo.NORMAL
        return if (elegido.esMultiple && !admiteMultiple) TUBO_POR_DEFECTO else elegido
    }

    // ==================== EL PUENTE EN UN ESCALÓN ====================
    // Dictado por el usuario (2026-09-26): con el puente pasa lo mismo que con el parante. Si cae en
    // un escalón (en Formas, el alféizar del recorte de la derecha, a 109.4 del dintel), se pregunta
    // dentro, fuera o centrado. DENTRO deja el puente entero del lado angosto (en el tramo invertido
    // de Formas, en la mocheta): el sistema queda un rectángulo limpio de 233.7 × 109.4 y el puente
    // mide lo que la mocheta, 151.7. FUERA lo deja del lado ancho: el sistema baja a 106.9 y el
    // puente mide 233.7.
    //
    // En el modelo el puente va dentro de la mocheta que separa (N-31: la mocheta lo pierde): la
    // franja de la mocheta incluye su alto. Por eso cada opción es una altura del límite.

    /** Un escalón horizontal del vano: a qué [y] (del dintel) está, y si el vano se angosta abajo. */
    data class EscalonHorizontal(val y: Float, val angostoAbajo: Boolean)

    /** Una manera de dejar el puente en su escalón: dónde queda el límite y cómo quedan las piezas. */
    data class OpcionPuente(
        val lado: LadoParante, val y: Float, val largoPuente: Float,
        val sistema: Pair<Float, Float>?, val mocheta: Pair<Float, Float>?
    )

    /**
     * El escalón donde está el puente [indiceLimite] del tramo, o null. Es un lado casi horizontal del
     * vano (con descuadre de hasta 2.5, N-06) a la altura del puente, con el vano más angosto de un
     * lado que del otro. Con descuadre manda la medida mayor: el escalón de Formas va de 108.5 a
     * 109.4, y del lado angosto (abajo) se toma 109.4.
     */
    fun escalonDelPuente(d: DisenoNova, indiceTramo: Int, indiceLimite: Int, anchoParante: Float = 2.5f): EscalonHorizontal? {
        val puntos = puntosDelVano(d)
        if (puntos.size < 3) return null
        val t = d.tramos.getOrNull(indiceTramo) ?: return null
        val y = limitesDePuente(d, indiceTramo).getOrNull(indiceLimite) ?: return null
        val inicio = iniciosDeTramos(d, anchoParante)[indiceTramo]
        val fin = inicio + t.ancho
        for (i in puntos.indices) {
            val (ax, ay) = puntos[i]
            val (bx, by) = puntos[(i + 1) % puntos.size]
            if (abs(bx - ax) <= abs(by - ay) || abs(by - ay) > DESCUADRE_CM) continue
            if (maxOf(ax, bx) <= inicio || minOf(ax, bx) >= fin) continue
            val yMin = minOf(ay, by)
            val yMax = maxOf(ay, by)
            if (y < yMin - 0.15f || y > yMax + 0.15f) continue
            val arriba = anchoDelVanoEn(puntos, yMin - 0.5f, inicio, fin) ?: continue
            val abajo = anchoDelVanoEn(puntos, yMax + 0.5f, inicio, fin) ?: continue
            if (abs(arriba - abajo) <= DESCUADRE_CM) continue
            val angostoAbajo = abajo < arriba
            return EscalonHorizontal(if (angostoAbajo) yMax else yMin, angostoAbajo)
        }
        return null
    }

    /** Las tres maneras de dejar el puente [indiceLimite] en su escalón; vacía si no está en uno. */
    fun opcionesDelPuente(
        d: DisenoNova, indiceTramo: Int, indiceLimite: Int, altoPuente: Float = 2.5f, anchoParante: Float = 2.5f
    ): List<OpcionPuente> {
        val e = escalonDelPuente(d, indiceTramo, indiceLimite, anchoParante) ?: return emptyList()
        val franjas = d.tramos[indiceTramo].franjas
        val deAbajo = franjas.getOrNull(indiceLimite) ?: return emptyList()
        val deArriba = franjas.getOrNull(indiceLimite + 1) ?: return emptyList()
        // El puente va en la mocheta que separa: la de abajo en un invertido, si no la de arriba.
        val mochetaAbajo = !deAbajo.esSistema && deArriba.esSistema
        val h = altoPuente
        val s = e.y
        // La y del límite que deja el puente entero en el lado angosto, en el ancho, o partido.
        val dentro = when {
            e.angostoAbajo && mochetaAbajo -> s
            e.angostoAbajo -> s + h
            mochetaAbajo -> s - h
            else -> s
        }
        val fuera = when {
            e.angostoAbajo && mochetaAbajo -> s - h
            e.angostoAbajo -> s
            mochetaAbajo -> s
            else -> s + h
        }
        val centrado = if (mochetaAbajo) s - h / 2f else s + h / 2f
        val actual = limitesDePuente(d, indiceTramo)[indiceLimite]
        return listOf(LadoParante.DENTRO to dentro, LadoParante.FUERA to fuera, LadoParante.CENTRADO to centrado)
            .mapNotNull { (lado, y) ->
                val m = if (abs(y - actual) <= 0.01f) d
                        else conPuenteMovido(d, indiceTramo, indiceLimite, y).takeIf { it !== d } ?: return@mapNotNull null
                val largo = largoDelPuente(m, indiceTramo, indiceLimite, h, anchoParante) ?: return@mapNotNull null
                val iSistema = if (deArriba.esSistema) indiceLimite + 1 else indiceLimite
                val iMocheta = if (mochetaAbajo) indiceLimite else indiceLimite + 1
                // La mocheta como espacio, sin el puente (N-31).
                val moch = rectanguloDeFranja(m, indiceTramo, iMocheta, anchoParante)?.let { it.first to (it.second - h) }
                OpcionPuente(lado, y, largo, rectanguloDeFranja(m, indiceTramo, iSistema, anchoParante), moch)
            }
    }

    /** El diseño con el puente dejado como dice [opcion]. */
    fun conPuenteEnEscalon(d: DisenoNova, indiceTramo: Int, indiceLimite: Int, opcion: OpcionPuente): DisenoNova {
        val actual = limitesDePuente(d, indiceTramo).getOrNull(indiceLimite) ?: return d
        return if (abs(opcion.y - actual) <= 0.01f) d else conPuenteMovido(d, indiceTramo, indiceLimite, opcion.y)
    }

    // ==================== DESCUADRES DEL VANO ====================
    // Pedido del usuario (2026-09-26): muchas veces quien mide no es quien diseña, y un descuadre
    // que no salta a la vista (en Formas, 9 mm) se pasa por alto. Se marca en rojo en el dibujo.

    /** Lo que se nota por debajo de esto es el trazo del apunte, no un descuadre. */
    const val DESCUADRE_MINIMO_CM = 0.3f

    /** Un lado del vano que debería ser recto y no lo es: de [desde] a [hasta], y cuánto se desvía. */
    data class Descuadre(val desde: Pair<Float, Float>, val hasta: Pair<Float, Float>, val diferencia: Float)

    /**
     * Los lados casi horizontales o casi verticales del vano cuyas puntas no están a la misma altura
     * (o a la misma x): entre 0.3 y 2.5 cm (N-06). Más que eso ya no es descuadre, es una pendiente.
     * En Formas: el alféizar del recorte, a 109.4 en un canto y a 108.5 en el otro (0.9).
     */
    fun descuadres(vano: List<Pair<Float, Float>>): List<Descuadre> {
        if (vano.size < 3) return emptyList()
        return vano.indices.mapNotNull { i ->
            val a = vano[i]
            val b = vano[(i + 1) % vano.size]
            val dx = abs(b.first - a.first)
            val dy = abs(b.second - a.second)
            val desvio = if (dx >= dy) dy else dx
            if (desvio >= DESCUADRE_MINIMO_CM && desvio <= DESCUADRE_CM) Descuadre(a, b, desvio) else null
        }
    }

    // ==================== PUENTES ====================

    /**
     * Un puente a la altura [y] en el tramo [indiceTramo]. Si el tramo no tiene mocheta, nace una:
     * arriba si el puente queda en la mitad de arriba (sistema abajo, modelo normal) o abajo si
     * queda en la de abajo (sistema arriba, invertido). Si ya tiene, se mueve el puente más cercano.
     */
    fun conPuenteEn(d: DisenoNova, indiceTramo: Int, y: Float): DisenoNova {
        val t = d.tramos.getOrNull(indiceTramo) ?: return d
        val limites = limitesDePuente(d, indiceTramo)
        if (limites.isNotEmpty()) {
            val k = limites.indices.minByOrNull { abs(limites[it] - y) } ?: return d
            return conPuenteMovido(d, indiceTramo, k, y)
        }
        val techo = d.caidaDeTramo(indiceTramo)
        val altoT = d.altoDeTramo(indiceTramo)
        val desdeAbajo = techo + altoT - y
        if (desdeAbajo < MINIMO_CM || altoT - desdeAbajo < MINIMO_CM) return d
        val sistema = t.franjas.firstOrNull { it.esSistema } ?: t.franjas.first()
        val mocheta = NovaFranja(false, 0f, listOf(NovaModulo('f', t.ancho)))
        val arriba = desdeAbajo > altoT / 2f
        val franjas = if (arriba) {
            listOf(sistema.copy(alto = desdeAbajo), mocheta.copy(alto = altoT - desdeAbajo))
        } else {
            listOf(mocheta.copy(alto = desdeAbajo), sistema.copy(alto = altoT - desdeAbajo))
        }
        val nuevos = d.tramos.toMutableList()
        nuevos[indiceTramo] = t.copy(franjas = franjas)
        return d.copy(tramos = nuevos)
    }

    /**
     * Mueve el puente [indiceLimite] del tramo (la división entre la franja [indiceLimite] y la de
     * encima, contando de abajo arriba) para que quede a la altura [y]. Solo cambian esas dos
     * franjas; las demás conservan su alto.
     */
    fun conPuenteMovido(d: DisenoNova, indiceTramo: Int, indiceLimite: Int, y: Float): DisenoNova {
        val t = d.tramos.getOrNull(indiceTramo) ?: return d
        val alturas = alturasDeFranjas(d, indiceTramo).toMutableList()
        if (indiceLimite !in 0 until alturas.size - 1) return d
        val piso = d.caidaDeTramo(indiceTramo) + d.altoDeTramo(indiceTramo)
        val debajo = alturas.take(indiceLimite).sum()
        val par = alturas[indiceLimite] + alturas[indiceLimite + 1]
        val abajo = (piso - y) - debajo
        val arriba = par - abajo
        if (abajo < MINIMO_CM || arriba < MINIMO_CM) return d
        alturas[indiceLimite] = abajo
        alturas[indiceLimite + 1] = arriba
        val nuevos = d.tramos.toMutableList()
        nuevos[indiceTramo] = t.copy(franjas = t.franjas.mapIndexed { k, fr -> fr.copy(alto = alturas[k]) })
        return d.copy(tramos = nuevos)
    }

    // ==================== LA FORMA DEL VANO ====================

    /**
     * Cada tramo toma el alto del vano en su ancho (N-66): dónde está el dintel y dónde el alféizar
     * sobre ese trozo. Un tramo que cruza un escalón se lleva el trozo más grande —el sistema tiene
     * que quedar en el rectángulo que entra (ver [rectanguloDelSistema]) y lo irregular es mocheta,
     * que admite cualquier forma—. Sin contorno, el diseño no cambia.
     */
    fun conFormaDelVano(d: DisenoNova, anchoParante: Float = 2.5f): DisenoNova {
        val vano = d.contornoVano
        if (vano.size < 3) return d
        val (x0, y0) = origenDelVano(d)
        val puntos = vano.map { (x, y) -> (x - x0) to (y - y0) }
        val inicios = iniciosDeTramos(d, anchoParante)
        val nuevos = d.tramos.mapIndexed { i, t ->
            val bordes = bordesEnTramo(puntos, inicios[i], inicios[i] + t.ancho) ?: return@mapIndexed t
            val (techo, piso) = bordes.first
            val altoTramo = piso - techo
            val entero = techo <= 0.15f && abs(altoTramo - d.alto) <= 0.15f
            val alto = if (entero) 0f else altoTramo
            val antes = d.altoDeTramo(i)
            val despues = if (alto > 0f) alto else d.alto
            // Las franjas se estiran con el tramo, como en DisenoNova.conAltoDeTramo.
            val k = if (antes > 0f) despues / antes else 1f
            t.copy(
                alto = alto, caida = if (entero) 0f else techo, altoDer = null, caidaDer = null,
                franjas = t.franjas.map { if (it.alto > 0f) it.copy(alto = it.alto * k) else it }
            )
        }
        return d.copy(tramos = nuevos)
    }

    /**
     * El rectángulo donde entra el sistema del tramo (N-66): del dintel más bajo al alféizar más alto
     * sobre su ancho, como (techo, piso) en y desde el dintel. Null si el vano no tiene forma.
     */
    fun rectanguloDelSistema(d: DisenoNova, indiceTramo: Int, anchoParante: Float = 2.5f): Pair<Float, Float>? {
        val vano = d.contornoVano
        val t = d.tramos.getOrNull(indiceTramo) ?: return null
        if (vano.size < 3) return null
        val (x0, y0) = origenDelVano(d)
        val puntos = vano.map { (x, y) -> (x - x0) to (y - y0) }
        val inicio = iniciosDeTramos(d, anchoParante)[indiceTramo]
        return bordesEnTramo(puntos, inicio, inicio + t.ancho)?.second
    }

    /**
     * El diseño limpio que respeta la forma: un tramo por escalón del vano, cada uno con su alto y
     * un solo fijo, en lugar de un rectángulo con la silueta encima. Para un vano recto, un tramo.
     */
    fun limpioConForma(
        acabado: String, ancho: Float, alto: Float, vano: List<Pair<Float, Float>>, anchoParante: Float = 2.5f
    ): DisenoNova {
        val enBlanco = DisenoNova(acabado, ancho, alto,
            listOf(NovaTramo(ancho, listOf(NovaFranja(true, 0f, listOf(NovaModulo('f', ancho)))))))
        if (vano.size < 3) return enBlanco
        val bandas = ContornoEnTramos.bandas(vano)
        // Solo el escalonado (lados rectos) se parte por escalones. Lo inclinado —triángulos,
        // dinteles en pendiente— sigue limpiando a un tramo con su silueta: esas formas se
        // desarrollan aparte, con pruebas (N-67, N-68). Un desnivel de hasta 2.5 cm es descuadre,
        // no pendiente (N-06): el recorte de Formas baja 0.9 y sigue siendo un escalón.
        fun inclinada(b: ContornoEnTramos.Banda) =
            abs(b.altoDerCm - b.altoCm) > DESCUADRE_CM || abs(b.caidaDerCm - b.caidaCm) > DESCUADRE_CM
        if (bandas.size < 2 || bandas.any { inclinada(it) }) return enBlanco.conContornoVano(vano)
        val altoVano = vano.maxOf { it.second } - vano.minOf { it.second }
        val tramos = bandas.mapIndexed { i, b ->
            // Descuadre de hasta 2.5: manda la medida mayor (N-06). El tramo queda recto.
            val altoB = maxOf(b.altoCm, b.altoDerCm)
            val caidaB = minOf(b.caidaCm, b.caidaDerCm)
            val entero = caidaB <= 0.15f && abs(altoB - altoVano) <= 0.15f
            // Cada escalón deja medio parante a cada lado del corte: así el eje del parante cae
            // justo en el borde del escalón, igual que uno puesto a mano con el imán.
            val lados = (if (i > 0) 1 else 0) + (if (i < bandas.lastIndex) 1 else 0)
            val anchoTramo = (b.anchoCm - lados * anchoParante / 2f).coerceAtLeast(1f)
            NovaTramo(
                ancho = anchoTramo,
                franjas = listOf(NovaFranja(true, 0f, listOf(NovaModulo('f', anchoTramo)))),
                alto = if (entero) 0f else altoB,
                caida = if (entero) 0f else caidaB,
                altoDer = null,
                caidaDer = null
            )
        }
        return DisenoNova(acabado, ancho, alto, tramos).conContornoVano(vano)
    }

    // ==================== AUXILIARES ====================

    private fun origenDelVano(d: DisenoNova): Pair<Float, Float> {
        val v = d.contornoVano
        return if (v.isEmpty()) 0f to 0f else v.minOf { it.first } to v.minOf { it.second }
    }

    /**
     * Dónde está el vano sobre el trozo [xIni]..[xFin]: el alcance entero (dintel más alto, alféizar
     * más bajo) y el rectángulo interior (dintel más bajo, alféizar más alto).
     */
    private fun bordesEnTramo(
        puntos: List<Pair<Float, Float>>, xIni: Float, xFin: Float
    ): Pair<Pair<Float, Float>, Pair<Float, Float>>? {
        if (xFin <= xIni) return null
        val cortes = muestrasEnTramo(puntos, xIni, xFin).mapNotNull { bordesEn(puntos, it) }
        if (cortes.isEmpty()) return null
        val entero = cortes.minOf { it.first } to cortes.maxOf { it.second }
        val interior = cortes.maxOf { it.first } to cortes.minOf { it.second }
        return entero to interior
    }

    /** Las x donde se mide el vano sobre un trozo: sus bordes (un pelo hacia dentro) y cada vértice. */
    private fun muestrasEnTramo(puntos: List<Pair<Float, Float>>, xIni: Float, xFin: Float): List<Float> {
        if (xFin <= xIni) return emptyList()
        val margen = ((xFin - xIni) * 0.02f).coerceAtMost(0.5f)
        return (listOf(xIni + margen, xFin - margen) +
            puntos.map { it.first }.filter { it > xIni + margen && it < xFin - margen }
                .flatMap { listOf(it - 0.05f, it + 0.05f) }).sorted()
    }

    /** El vano en coordenadas de la ventana (x desde el canto izquierdo, y desde el dintel). */
    private fun puntosDelVano(d: DisenoNova): List<Pair<Float, Float>> {
        val vano = d.contornoVano
        if (vano.size < 3) return emptyList()
        val (x0, y0) = origenDelVano(d)
        return vano.map { (x, y) -> (x - x0) to (y - y0) }
    }

    /** Cuánto mide el vano de alto en esa vertical, o null si ahí no hay vano. */
    private fun altoDelVanoEn(puntos: List<Pair<Float, Float>>, x: Float): Float? =
        bordesEn(puntos, x)?.let { (arriba, abajo) -> abajo - arriba }

    /** Dónde empieza y acaba el vano en esa vertical (como ContornoEnTramos.bordesEn). */
    private fun bordesEn(puntos: List<Pair<Float, Float>>, x: Float): Pair<Float, Float>? {
        var arriba = Float.MAX_VALUE
        var abajo = -Float.MAX_VALUE
        var hubo = false
        for (i in puntos.indices) {
            val (ax, ay) = puntos[i]
            val (bx, by) = puntos[(i + 1) % puntos.size]
            if (x < minOf(ax, bx) || x > maxOf(ax, bx)) continue
            if (abs(bx - ax) <= 0.15f) continue
            val t = (x - ax) / (bx - ax)
            val y = ay + t * (by - ay)
            arriba = minOf(arriba, y); abajo = maxOf(abajo, y); hubo = true
        }
        return if (hubo) arriba to abajo else null
    }

    /** Reparte los módulos a un lado y otro del [corte] según dónde cae su centro. */
    private fun partirModulos(mods: List<NovaModulo>, anchoTramo: Float, corte: Float): Pair<List<NovaModulo>, List<NovaModulo>> {
        if (mods.isEmpty()) return listOf(NovaModulo('f')) to listOf(NovaModulo('f'))
        val w = mods.map { it.ancho ?: (anchoTramo / mods.size) }
        var x = 0f
        val izq = mutableListOf<NovaModulo>()
        val der = mutableListOf<NovaModulo>()
        mods.forEachIndexed { k, m ->
            val centro = x + w[k] / 2f
            if (centro < corte) izq.add(m) else der.add(m)
            x += w[k]
        }
        if (izq.isEmpty()) izq.add(NovaModulo('f'))
        if (der.isEmpty()) der.add(NovaModulo('f'))
        return izq to der
    }

    /** Ajusta los anchos de los módulos para que llenen [ancho], en la proporción que tenían. */
    private fun ajustar(mods: List<NovaModulo>, ancho: Float): List<NovaModulo> {
        if (mods.isEmpty()) return mods
        val sinAncho = mods.any { it.ancho == null || it.ancho <= 0f }
        if (sinAncho) return mods.map { it.copy(ancho = ancho / mods.size) }
        val suma = mods.sumOf { (it.ancho ?: 0f).toDouble() }.toFloat()
        return mods.map { it.copy(ancho = (it.ancho ?: 0f) * ancho / suma) }
    }
}
