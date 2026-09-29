package crystal.crystal.taller.nova.motor

import crystal.crystal.taller.nova.NovaCalculos
import crystal.crystal.taller.nova.NovaPerfilesHelper
import kotlin.math.ceil

/**
 * El cálculo único de Nova corrediza (docs/AUDITORIA_NOVA.md, sección 3). Recibe una
 * [VentanaNova] —venga del diseño automático o del hecho a mano— y devuelve sus piezas.
 *
 * Cada pieza se calcula en una sola función y cita su regla de docs/REGLAS_NOVA.md. Lo que las
 * reglas no dicen se dejó como lo hace hoy la pantalla, y está marcado "como la pantalla".
 *
 * Todo lo que depende de la altura (hoja, mochetas, puentes) se calcula **por tramo**: en un diseño
 * compuesto (N-89) cada tramo tiene su modelo y su altura de hoja.
 */
object MotorNova {

    /** Gruña a cada lado del vidrio de mocheta de INA (dictado 2026-09-25; máximo 0.4). */
    private const val GRUNA_MOCHETA_INA = 0.3f

    private enum class Ubicacion { ARRIBA, ABAJO }

    /**
     * Una mocheta de un tramo. [espacio] es la mocheta como espacio (N-31: alto − hoja − puente, lo
     * que usan las U, el tee y el vidrio de APA); [directo] es el atajo de INA (alto − hoja, sin
     * descontar el puente, N-32/N-86).
     */
    private data class Mocheta(
        val ubicacion: Ubicacion, val espacio: Float, val directo: Float,
        /** El ancho de su rectángulo en un vano con forma (N-96); null = el del tramo. */
        val ancho: Float? = null,
        /** Cuánto se alarga cada costado inclinado por cm de alto (ver [FranjaTramo.inclinacionIzq]). */
        val inclinacionIzq: Float = 0f, val inclinacionDer: Float = 0f
    )

    /** Una franja de sistema que no es la principal (N-88): se calcula como un sistema propio. */
    private data class SistemaExtra(val alto: Float, val modulos: List<Modulo>)

    /**
     * La vertical de un tramo: su altura de hoja, sus mochetas, cuántas corridas de puente lleva
     * (una por cada división entre franjas) y las franjas de sistema de más.
     */
    private data class Vertical(
        val altoHoja: Float,
        val mochetas: List<Mocheta>,
        val corridas: Int = mochetas.size,
        val extras: List<SistemaExtra> = emptyList()
    ) {
        val hayMocheta: Boolean get() = mochetas.isNotEmpty()
        val soloAbajo: Boolean get() = mochetas.isNotEmpty() && mochetas.all { it.ubicacion == Ubicacion.ABAJO }
        val bandera: Boolean get() = mochetas.map { it.ubicacion }.toSet().size == 2
    }

    /** Un módulo con todo lo que hace falta para colindancias y medidas. */
    private data class M(
        val tramo: Int, val pos: Int, val global: Int, val corrediza: Boolean,
        val ancho: Float, val anchoVidrio: Float, val u: Float,
        /** Lo que el vano le quita al módulo, arriba y abajo (ver [Modulo.recorteArriba]). */
        val recorteArriba: Float = 0f, val recorteAbajo: Float = 0f,
        /** La columna que entra por su costado de la pared (ver [Columna]). */
        val columna: Columna? = null,
        /** Si el vano lo corta en diagonal (ver [Modulo.inclinado]). */
        val inclinado: Inclinado? = null
    ) {
        val recorte: Float get() = recorteArriba + recorteAbajo
    }

    fun calcular(v: VentanaNova): MaterialesNova {
        var total = MaterialesNova()
        v.lados.forEach { total += lado(v, it) }
        // Parante de esquina (L, C, serie): el alto mayor de los dos lados, uno por esquina.
        if (v.esquinero != null) {
            val esquinas = v.lados.zipWithNext().filter { (a, _) -> a.derecha.esEsquina }
                .map { (a, b) -> Pieza(maxOf(a.altoMedido, b.altoMedido), 1, Orientacion.VERTICAL) }
            total = total.copy(parante = total.parante + esquinas)
        }
        return total
    }

    // ==================== LA VERTICAL DE CADA TRAMO ====================

    /**
     * Hoja y mochetas del tramo. Sin franjas propias salen del modelo de la ventana (N-33, N-34);
     * con franjas propias (diseño compuesto), del dibujo: el sistema da la hoja, y las mochetas se
     * reparten alto − hoja en la proporción dibujada. Cada mocheta pierde el puente que la separa del
     * sistema; un tramo sin mocheta no lleva puente.
     */
    private fun vertical(v: VentanaNova, l: LadoNova, t: Tramo): Vertical {
        // El puente de ESTE tramo (N-03 por tramo): un invertido lleva tubo aunque la ventana tenga Múltiple.
        val h = (t.puente ?: v.puente).altura
        val franjas = t.franjas
        if (franjas == null) {
            if (!l.hayMocheta) return Vertical(l.altoHoja, emptyList())
            val disponible = l.alto - l.altoHoja
            val lista = when (v.modelo) {
                ModeloMocheta.NORMAL -> listOf(Mocheta(Ubicacion.ARRIBA, disponible - h, disponible))
                ModeloMocheta.INVERTIDO -> listOf(Mocheta(Ubicacion.ABAJO, disponible - h, disponible))
                ModeloMocheta.BANDERA -> {
                    // N-34: la inferior escrita manda; si no, mitad y mitad.
                    val espacioTotal = (disponible - 2 * h).coerceAtLeast(0f)
                    val inf = if (v.mochetaInferior > 0f) v.mochetaInferior.coerceAtMost(espacioTotal) else espacioTotal / 2f
                    val sup = (espacioTotal - inf).coerceAtLeast(0f)
                    val infDirecto = if (v.mochetaInferior > 0f) inf.coerceAtMost(disponible) else disponible / 2f
                    listOf(
                        Mocheta(Ubicacion.ARRIBA, sup, (disponible - infDirecto).coerceAtLeast(0f)),
                        Mocheta(Ubicacion.ABAJO, inf, infDirecto)
                    )
                }
            }
            return Vertical(l.altoHoja, lista.filter { it.espacio > 0f })
        }
        // Cada división entre dos franjas es un tubo: una corrida de puente. Se le descuenta a una de
        // las dos (dictado 2026-09-25): entre sistema y mocheta, a la mocheta (N-31); entre dos
        // mochetas o dos sistemas, a la de arriba.
        val cargas = IntArray(franjas.size)
        for (k in 0 until franjas.size - 1) {
            val abajo = franjas[k]; val arriba = franjas[k + 1]
            cargas[if (abajo.sistema != arriba.sistema && !abajo.sistema) k else k + 1]++
        }
        // Un escalón más bajo reparte SU alto, no el de la ventana (ver [Tramo.alto]).
        val altoT = t.alto ?: l.alto
        val iSistema = franjas.indexOfFirst { it.sistema }
        val altoHoja = franjas.getOrNull(iSistema)?.alto?.takeIf { it > 0f && it < altoT } ?: altoT
        // Las franjas de sistema de más (una mocheta con corrediza, N-88) tienen su alto propio.
        val extras = franjas.withIndex().filter { it.value.sistema && it.index != iSistema }
            .map { (k, f) -> SistemaExtra((f.alto - h * cargas[k]).coerceAtLeast(0f), f.modulos) }
        val altoSistemas = altoHoja + franjas.withIndex()
            .filter { it.value.sistema && it.index != iSistema }.sumOf { it.value.alto.toDouble() }.toFloat()
        val disponible = (altoT - altoSistemas).coerceAtLeast(0f)
        val mochetas = franjas.withIndex().filter { !it.value.sistema }
        val suma = mochetas.sumOf { it.value.alto.toDouble() }.toFloat()
        val lista = mochetas.map { (i, f) ->
            val parte = if (suma > 0f) disponible * f.alto / suma else disponible / mochetas.size
            Mocheta(if (i < iSistema) Ubicacion.ABAJO else Ubicacion.ARRIBA, parte - h * cargas[i], parte, f.ancho, f.inclinacionIzq, f.inclinacionDer)
        }
        // Mismo orden que las del modelo (la de arriba primero), para que las listas salgan iguales.
        val ordenadas = lista.sortedBy { it.ubicacion.ordinal }
        return Vertical(
            altoHoja,
            if (disponible > 0f) ordenadas.filter { it.espacio > 0f } else emptyList(),
            corridas = (franjas.size - 1).coerceAtLeast(0),
            extras = extras.filter { it.alto > 0f && it.modulos.isNotEmpty() }
        )
    }

    // ==================== UN LADO ====================

    private fun lado(v: VentanaNova, l: LadoNova): MaterialesNova {
        if (l.modulos.isEmpty()) return MaterialesNova()
        val apa = v.acabado == Acabado.APA
        val us = v.u.alto
        val todoCorredizas = l.modulacion in setOf("nfc", "ncc", "n3c")
        val fullFijos = l.modulacion == "nff" || (l.divisiones == 1 && l.modulos.none { it.corrediza })

        val ms = modulos(v, l)
        val porTramo = ms.groupBy { it.tramo }
        val patronLado = ms.map { if (it.corrediza) 'c' else 'f' }
        val circulo = if (v.circular && l.tramos.size == 1) Circulo(l.anchoCalculo, l.alto) else null
        // Circular (sección 11): cada mocheta es un segmento; su rectángulo, el que lo contiene, va
        // de la cuerda del borde de afuera de su puente (el vidrio entero, se corta en obra, N-99).
        val vert = l.tramos.map { vertical(v, l, it) }.map { vt ->
            if (circulo == null) vt else vt.copy(mochetas = vt.mochetas.map { m ->
                m.copy(ancho = circulo.cuerda(if (m.ubicacion == Ubicacion.ARRIBA) m.espacio else l.alto - m.espacio))
            })
        }
        fun vDe(m: M) = vert[m.tramo]

        // Esquina en ángulo de INA (G-09, N-42): se suma el ancho de la U al ancho del lado y se
        // reparte entre las hojas, como el cruce. Ejemplo dictado: (120 + 0.7 + 1) / 2 = 60.8.
        // Solo en las U; los vidrios no cambian. En esquina curva, nada.
        val esquinasAngulo = if (apa) 0 else listOf(l.izquierda, l.derecha).count { it == Borde.ESQUINA_ANGULO }
        val aumentoLado = v.u.ancho * esquinasAngulo
        val aumentoPorHoja = if (l.divisiones > 0) aumentoLado / l.divisiones else 0f
        fun esMuro(b: Borde) = b == Borde.MURO || b == Borde.ACOPLE || b == Borde.LIBRE

        val u = mutableListOf<Pieza>()
        val puente = mutableListOf<Pieza>()
        val riel = mutableListOf<Pieza>()
        val rielSup = mutableListOf<Pieza>()
        val dobleCorrediza = mutableListOf<Pieza>()
        val parante = mutableListOf<Pieza>()
        val vidrios = mutableListOf<Vidrio>()

        // ---------- U de fijos (N-42, N-47, N-48) ----------
        when {
            l.modulacion == "nff" -> {
                if (apa) l.tramos.forEachIndexed { i, t ->
                    // La franja lleva U abajo y arriba; con Múltiple (o gorrito) la de arriba la trae el puente.
                    val n = if (vert[i].hayMocheta && (t.puente ?: v.puente).esMultipleOGorrito) 1 else 2
                    u.add(Pieza(t.ancho, n))
                } else if (!vert.all { it.soloAbajo }) u.add(Pieza(l.anchoCalculo + aumentoLado, 2))
            }
            fullFijos -> if (apa || !vert[0].soloAbajo) u.add(Pieza(ms.first().u + aumentoLado, 2))
            // Un fijo contra un lado inclinado: su U de fijo es su borde de abajo, "como 2 o 3 cm"
            // (dictado el 2026-09-26, Vna4: 2.7 + su parte de cruce), no el ancho del módulo.
            apa -> rachasDeFijos(ms, cruzaParantes = false).forEach { racha ->
                u.add(Pieza(racha.sumOf { m ->
                    (m.inclinado?.let { m.u - it.faltaAbajo } ?: (m.u + aumentoPorHoja)).toDouble()
                }.toFloat(), 1))
            }
            // INA con corredizas: la U de cada borde (ver uDelBorde), que reemplaza a la U de fijos
            // y a la U superior e inferior de lado a lado.
            ms.any { it.corrediza } -> {
                // Primero el borde de abajo (lo que antes era la U de fijos) y después el de arriba.
                uDelBorde(v, l, ms, vert, arriba = false, aumentoPorHoja).forEach { u.add(Pieza(it, 1)) }
                uDelBorde(v, l, ms, vert, arriba = true, aumentoPorHoja).forEach { u.add(Pieza(it, 1)) }
            }
            else -> rachasDeFijos(ms, cruzaParantes = true).forEach { racha ->
                u.add(Pieza(racha.sumOf { (it.u + aumentoPorHoja).toDouble() }.toFloat(), 1))
            }
        }
        // Antes, en INA: U de fijos por racha (sin ellas en invertido) y además
        //     if (!apa && ms.any { it.corrediza }) {
        //         val arriba = vert.any { vt -> vt.mochetas.any { it.ubicacion == Ubicacion.ARRIBA } }
        //         val abajo = vert.any { vt -> vt.mochetas.any { it.ubicacion == Ubicacion.ABAJO } }
        //         val n = (if (arriba) 1 else 0) + (if (abajo) 1 else 0)
        //         if (n > 0) u.add(Pieza(l.anchoCalculo + aumentoLado, n))
        //     }
        // La Dora editada daba 53.2 = 2 y 370 = 2; lo dictado es 53.2 = 1, 370 = 1 y 213.2 = 1.

        // ---------- U de mocheta horizontal APA (N-45) ----------
        if (apa && us != 0f) {
            // Con el puente de cada tramo (N-03) y el ancho del rectángulo de cada mocheta (N-96): en
            // Formas la mocheta del tramo 2 mide 151.7, no el ancho del tramo.
            l.tramos.forEachIndexed { i, t ->
                val porTramoN = if ((t.puente ?: v.puente).esMultipleOGorrito) 1 else 2
                vert[i].mochetas.forEach { m -> u.add(Pieza(m.ancho ?: t.ancho, porTramoN)) }
            }
        }

        // ---------- U parante (N-43) ----------
        if (us != 0f) {
            if (apa) {
                // APA: cada fijo contra pared o parante (el borde de cada tramo, esquina incluida).
                // Cada fijo con su alto: el que tiene recorte (una viga, un escalón) es más bajo.
                // Antes: n piezas de `altoHoja − (u + 0.2)` por tramo.
                porTramo.forEach { (i, t) ->
                    // El primero y el último, cada uno con su lado de pared (un tramo de un solo fijo:
                    // los dos lados, y la columna solo en el primero).
                    listOfNotNull(
                        t.first().takeIf { !it.corrediza }?.let { Triple(it, it.columna, it.inclinado?.diagonalIzq ?: 0f) },
                        t.last().takeIf { !it.corrediza }?.let { Triple(it, if (t.size > 1) it.columna else null, it.inclinado?.diagonalDer ?: 0f) }
                    ).forEach { (f, col, diagonal) ->
                        val hoja = vert[i].altoHoja - f.recorte
                        // Contra un lado inclinado (dictado el 2026-09-26, Vna4): la U de ese costado es
                        // una diagonal. Primero va la de abajo (la U de fijo, horizontal, G-01) y sobre
                        // ella la diagonal: su largo menos el alto de la U, como el 99 → 98 de G-01.
                        if (diagonal > 0f) {
                            u.add(Pieza(diagonal - us, 1, Orientacion.DIAGONAL))
                            return@forEach
                        }
                        if (col == null) {
                            u.add(Pieza(hoja - (us + 0.2f), 1, Orientacion.VERTICAL))
                            return@forEach
                        }
                        // Una columna en el costado (dictado el 2026-09-26, "polis" ventana 2): la U
                        // sigue la pared. Las horizontales tienen preferencia (G-01) y en la esquina
                        // saliente suman el alto de la U, que si no quedaría vacía (36.4 + 1.5 = 37.9).
                        // La cara de la columna, su largo (97.2). La pared de arriba va entre el fijo
                        // corredizo y la horizontal (− u − 0.2, como N-43); la de abajo entre la
                        // horizontal y la U de fijos (− 2u).
                        // Una cara en 0: la columna sigue más allá del puente o del riel, que la cierra.
                        if (col.caraArriba > 0f) u.add(Pieza(col.caraArriba + us, 1))
                        if (col.caraAbajo > 0f) u.add(Pieza(col.caraAbajo + us, 1))
                        u.add(Pieza(col.alto, 1, Orientacion.VERTICAL))
                        (col.paredArriba - (us + 0.2f)).takeIf { it > 0f }?.let { u.add(Pieza(it, 1, Orientacion.VERTICAL)) }
                        (col.paredAbajo - 2 * us).takeIf { it > 0f }?.let { u.add(Pieza(it, 1, Orientacion.VERTICAL)) }
                    }
                }
                // La U en la cara horizontal del corte, sobre el fijo (dictado el 2026-09-26): del
                // largo de su U de fijo. El fijo corredizo solo llega hasta donde empieza el corte.
                ms.filter { !it.corrediza && it.recorteArriba > 0f }.forEach { f -> u.add(Pieza(f.u, 1)) }
            } else {
                // INA: solo los fijos de los bordes de la ventana contra muro, acople o libre.
                // Cada uno con el alto de su tramo: en un vano escalonado el de la punta puede ser más bajo.
                if (!ms.first().corrediza && esMuro(l.izquierda)) u.add(Pieza(altoDe(l, ms.first()) - 2 * us, 1, Orientacion.VERTICAL))
                if (!ms.last().corrediza && esMuro(l.derecha)) u.add(Pieza(altoDe(l, ms.last()) - 2 * us, 1, Orientacion.VERTICAL))
            }
        }

        // ---------- U vertical de mocheta (N-44, N-45) ----------
        if (us != 0f) {
            if (apa) {
                // Un costado inclinado de la mocheta lleva su U en diagonal: el largo del lado menos el
                // alto de la U (dictado el 2026-09-26, Vna4: 43.3 de mocheta → 48.1 − 1).
                vert.forEach { vt -> vt.mochetas.forEach { m ->
                    listOf(m.inclinacionIzq, m.inclinacionDer).forEach { k ->
                        if (k > 0f) u.add(Pieza(m.espacio * k - us, 1, Orientacion.DIAGONAL))
                        else u.add(Pieza(m.espacio - 2 * us, 1, Orientacion.VERTICAL))
                    }
                } }
            } else {
                // INA: 1 por cada corrediza de los bordes de la ventana contra muro o acople. Arriba
                // +1.5; abajo (invertido y la inferior de bandera) sin él.
                fun enBorde(m: M, borde: Borde) {
                    if (!m.corrediza || !esMuro(borde)) return
                    vDe(m).mochetas.forEach { mo ->
                        val mas = if (mo.ubicacion == Ubicacion.ARRIBA) 1.5f else 0f
                        u.add(Pieza(mo.directo - us + mas, 1, Orientacion.VERTICAL))
                    }
                }
                enBorde(ms.first(), l.izquierda)
                enBorde(ms.last(), l.derecha)
            }
        }

        // ---------- Puente (N-49, N-35) y falso puente (N-78) ----------
        // Una corrida por cada división entre franjas del tramo: normal e invertido 1, bandera 2,
        // sin mocheta 0; en un diseño compuesto, las que tenga.
        // En un vano con forma cada corrida mide lo que el vano le deja donde queda (N-97): en Formas
        // el puente del tramo 2 entra en la mocheta y mide 151.7.
        l.tramos.forEachIndexed { i, t ->
            if (vert[i].corridas <= 0) return@forEachIndexed
            val largos = t.largosPuente
            if (largos != null && largos.size == vert[i].corridas) largos.forEach { puente.add(Pieza(it, 1)) }
            else puente.add(Pieza(t.ancho, vert[i].corridas))
        }
        val falsoPuente = mutableListOf<Pieza>()
        if (v.libresVerticales > 0) {
            if (apa && v.puente.esMultiple) falsoPuente.add(Pieza(l.anchoCalculo, v.libresVerticales))
            else puente.add(Pieza(l.anchoCalculo, v.libresVerticales))
        }

        // ---------- Parantes verticales (N-27, N-78) ----------
        val nParantes = (l.tramos.size - 1) + listOf(l.izquierda, l.derecha).count { it == Borde.LIBRE }
        // En un vano con forma cada parante entre tramos mide el alto del vano donde queda (N-94): en
        // Formas, con el parante dentro, 77.2. Los de los lados libres, el alto medido.
        val largosParante = l.largosParante?.takeIf { it.size == l.tramos.size - 1 }
        if (largosParante != null) {
            largosParante.forEach { parante.add(Pieza(it, 1, Orientacion.VERTICAL)) }
            val libres = nParantes - largosParante.size
            if (libres > 0) parante.add(Pieza(l.altoMedido, libres, Orientacion.VERTICAL))
        } else if (nParantes > 0) parante.add(Pieza(l.altoMedido, nParantes, Orientacion.VERTICAL))

        // ---------- Riel (N-51) y riel superior (N-52, N-52b) ----------
        // Full corredizas (N-51, N-52b): 1 riel por corrediza, y el perfil doble corrediza, 1 por cada
        // 2 corredizas, en lugar de U felpero o fijo corredizo. Se decide por TRAMO, como el cruce: un
        // tramo `ccc` dentro de un diseño editado es full corredizas aunque la ventana sea "nn".
        // Antes solo miraba la modulación de la ventana (`todoCorredizas`) y ese tramo salía con 1
        // riel y fijo corredizo.
        fun esFull(t: Tramo) = todoCorredizas || soloCorredizas(t)
        val tramosClasicos = l.tramos.withIndex().filter { it.value.corredizas > 0 && !esFull(it.value) }
        val vRef = tramosClasicos.firstOrNull()?.let { vert[it.index] } ?: vert[0]
        // Si todos los tramos con corredizas son full, la doble corrediza ocupa el lugar del riel
        // superior de la ventana; si conviven con tramos clásicos, va aparte ([MaterialesNova.dobleCorrediza]).
        val nombreRielSup = when {
            tramosClasicos.isEmpty() -> RielSuperior.DOBLE_CORREDIZA
            !apa && vRef.hayMocheta && !vRef.soloAbajo -> RielSuperior.U_FELPERO
            else -> RielSuperior.FIJO_CORREDIZO
        }
        val dobleAparte = tramosClasicos.isNotEmpty()
        l.tramos.forEachIndexed { i, t ->
            if (t.corredizas == 0) return@forEachIndexed
            val medida = t.ancho - 0.06f
            // El riel va por el borde de abajo del sistema: si un costado es inclinado, del lado
            // inclinado al otro, más lo que su propio alto sube por la pendiente (dictado el
            // 2026-09-26, Vna4: 133.2 + 1.2 × tan = 133.8, − 0.06).
            val medidaRiel = rielAbajo(t, vert[i].altoHoja) - 0.06f
            if (esFull(t)) {
                riel.add(Pieza(medidaRiel, t.corredizas))
                val doble = Pieza(medida, ceil(t.corredizas / 2.0).toInt())
                if (dobleAparte) dobleCorrediza.add(doble) else rielSup.add(doble)
                return@forEachIndexed
            }
            riel.add(Pieza(medidaRiel, 1))
            // Como la pantalla: no va cuando hay mocheta arriba con puente Múltiple, que ya lo trae.
            val lleva = !vert[i].hayMocheta || vert[i].soloAbajo || !(t.puente ?: v.puente).esMultiple
            // Si el vano corta el borde de arriba del sistema (una viga), el fijo corredizo solo llega
            // hasta donde empieza el corte (dictado el 2026-09-26): en "polis", 190.2 − 0.06.
            if (lleva) rielSup.add(Pieza(t.anchoRielSuperior?.let { it - 0.06f } ?: medida, 1))
        }
        // Antes:
        //     riel.add(Pieza(medida, if (todoCorredizas) t.corredizas else 1))
        //     val lleva = todoCorredizas || !vert[i].hayMocheta || vert[i].soloAbajo || !v.puente.esMultiple
        //     if (lleva) rielSup.add(Pieza(medida, if (todoCorredizas) ceil(t.corredizas / 2.0).toInt() else 1))

        // ---------- Hache, portafelpa, tope (N-54, N-55, N-56) ----------
        val hache = ms.filter { it.corrediza }.map { Pieza(it.u, 1) }
        val portafelpa = mutableListOf<Pieza>()
        val tope = mutableListOf<Pieza>()
        porTramo.forEach { (i, t) ->
            if (t.none { it.corrediza }) return@forEach
            val hoja = vert[i].altoHoja
            portafelpa.add(Pieza(NovaCalculos.portafelpa(hoja), t.count { it.corrediza } * 2, Orientacion.VERTICAL))
            // Cada lado de fijo que toca una corrediza, con el alto de ESE fijo (un fijo con recorte
            // es más bajo). Antes todos con el alto de la hoja.
            t.indices.filter { !t[it].corrediza }.forEach { k ->
                val lados = (if (k > 0 && t[k - 1].corrediza) 1 else 0) + (if (k < t.lastIndex && t[k + 1].corrediza) 1 else 0)
                if (lados > 0) portafelpa.add(Pieza(NovaCalculos.portafelpa(hoja - t[k].recorte), lados, Orientacion.VERTICAL))
            }
            // Todo borde de tramo es pared o parante: también la esquina, que lleva su parante de
            // esquina P<90> (N-84), y el lado libre, que lleva el suyo (N-78).
            var nt = (if (t.first().corrediza) 1 else 0) + (if (t.last().corrediza) 1 else 0)
            // Una corrediza junto a un fijo con recorte colinda con la cara del corte (una viga, un
            // escalón): es pared, y lleva tope completo (dictado el 2026-09-26, N-56).
            nt += t.indices.count { k ->
                t[k].corrediza && ((k > 0 && !t[k - 1].corrediza && t[k - 1].recorte > 0f) ||
                    (k < t.lastIndex && !t[k + 1].corrediza && t[k + 1].recorte > 0f))
            }
            if (nt > 0) tope.add(Pieza(hoja - 0.9f, nt, Orientacion.VERTICAL))
        }

        // ---------- Tee de aparente (N-57) ----------
        // El "tubo" de N-53 (el ancho, 1 por mocheta) es el mismo puente: ya va en [puente], con
        // sus dos corridas en bandera. La pantalla vieja lo calculaba aparte y no lo mostraba.
        val tee = mutableListOf<Pieza>()
        if (apa) l.tramos.forEachIndexed { i, t ->
            vert[i].mochetas.forEach { m ->
                val nTees = (panosDe(t, m).size - 1).coerceAtLeast(0)
                if (nTees == 0) return@forEach
                val valor = NovaPerfilesHelper.calcularTe(m.espacio, us, (t.puente ?: v.puente).nombre)
                if (valor > 0f) tee.add(Pieza(valor, nTees, Orientacion.VERTICAL))
            }
        }

        // ---------- Vidrios fijos (N-60, N-61, N-87) ----------
        val holgura = if (us == 0f) 1f else 0.2f
        ms.filter { !it.corrediza }.forEach { f ->
            val descuento = if (fullFijos) NovaCalculos.GRUNA
            else if (apa) NovaCalculos.descuentoColindanciaFijo(porTramo.getValue(f.tramo).map { if (it.corrediza) 'c' else 'f' }, f.pos)
            else NovaCalculos.descuentoColindanciaFijo(patronLado, f.global)
            // Un fijo con recorte (una viga encima, un escalón debajo) es más bajo: en "polis",
            // 133.6 − 39.2 = 94.4 de hoja.
            val alto = (if (apa) vDe(f).altoHoja - f.recorte else altoDe(l, f)) - (us + holgura)
            // Un fijo contra un lado inclinado lleva el vidrio entero, el rectángulo que lo contiene, y
            // se corta en obra (dictado el 2026-09-26, Vna4: 56.5 de ancho, no los 78 del módulo).
            // En un círculo, el fijo junto a la pared no llega de arriba abajo: su rectángulo es lo más
            // alto que llega el círculo sobre él (INA 180 × 180: 169.7, no 180).
            val altoCirculo = circulo?.let { c ->
                val x0 = ms.takeWhile { it !== f }.sumOf { it.ancho.toDouble() }.toFloat()
                minOf(alto, c.altoEntre(x0, x0 + f.ancho) - (us + holgura))
            } ?: alto
            val u = f.inclinado?.let { f.u - it.faltaContenido } ?: f.u
            vidrios.add(Vidrio(u - descuento, altoCirculo))
        }

        // ---------- Vidrios corredizos (N-58, N-59) ----------
        // Full corredizas va sin el −1.4 (N-59). Se mira el tramo, como el cruce: un tramo de solo
        // corredizas dentro de un diseño editado es full corredizas aunque la ventana sea "nn".
        // Doble y triple corrediza siguen con el −1.4. Antes: `if (l.modulacion == "nfc") c.u else c.u − 1.4f`.
        val fullPorTramo = l.modulacion !in setOf("ncc", "n3c")
        ms.filter { it.corrediza }.forEach { c ->
            val full = l.modulacion == "nfc" || (fullPorTramo && soloCorredizas(l.tramos[c.tramo]))
            val ancho = if (full) c.u else c.u - 1.4f
            vidrios.add(Vidrio(ancho, vDe(c).altoHoja - 3.5f))
        }

        // ---------- Vidrios de mocheta (N-62, N-63, N-64) ----------
        if (apa) {
            l.tramos.forEachIndexed { i, t ->
                vert[i].mochetas.forEach { m ->
                    // N-63 (dictado 2026-09-25): con los demás puentes, mocheta − (u + 0.3); también
                    // en bandera. Antes: `m.espacio - us + 0.3f`.
                    val alto = if ((t.puente ?: v.puente).esMultipleOGorrito) m.espacio - 0.5f else m.espacio - (us + 0.3f)
                    panosDe(t, m).forEach { w -> vidrios.add(Vidrio(w - 0.5f, alto)) }
                }
            }
        } else {
            // INA: la mocheta va sobre las corredizas (los fijos van de piso a techo). El ancho es
            // lo que dejan las U de fijos, menos una gruña de 0.3 a cada lado. Alto: arriba +1,
            // abajo −0.5 (N-64). Todo en float; df1 solo al presentar (dictado el 2026-09-25):
            // 140 en fcf, U de fijo 47.133 → 140 − 2 × 47.133 − 0.6 = 45.13 → 45.1. Antes se
            // restaban las U ya truncadas (`NovaCalculos.df1(it.u)`: 47.1 → 45.2).
            porTramo.forEach { (i, t) ->
                val nc = t.count { it.corrediza }
                if (nc == 0 || !vert[i].hayMocheta) return@forEach
                val espacio = t.sumOf { it.anchoVidrio.toDouble() }.toFloat() -
                    t.filter { !it.corrediza }.sumOf { it.u.toDouble() }.toFloat()
                val anchos = rachas(t.map { it.corrediza }, true).map { n -> espacio * n / nc - 2 * GRUNA_MOCHETA_INA }
                vert[i].mochetas.forEach { m ->
                    val alto = if (m.ubicacion == Ubicacion.ARRIBA) m.directo + 1f else m.directo - 0.5f
                    anchos.forEach { w -> vidrios.add(Vidrio(w, alto)) }
                }
            }
        }

        // ---------- Forma circular (N-01, sección 11) ----------
        if (circulo != null) circular(v, l, circulo, ms, vert[0], u, puente, riel, rielSup)

        var total = MaterialesNova(
            u = u, puente = puente, riel = riel, rielSuperior = rielSup, nombreRielSuperior = nombreRielSup,
            dobleCorrediza = dobleCorrediza, portafelpa = portafelpa, tee = tee, tope = tope, hache = hache, parante = parante,
            falsoPuente = falsoPuente, vidrios = vidrios
        )
        // Franjas de sistema de más (N-88: la mocheta con corrediza pasa a ser sistema): cada una se
        // calcula como un sistema propio, del ancho de su tramo y con su alto, sin mocheta. Su
        // puente ya se contó arriba, entre las divisiones del tramo.
        l.tramos.forEachIndexed { i, t ->
            vert[i].extras.forEach { e ->
                val sub = LadoNova(
                    anchoMedido = t.ancho, anchoCalculo = t.ancho, alto = e.alto, altoMedido = e.alto,
                    altoHoja = e.alto, tramos = listOf(Tramo(e.modulos)),
                    izquierda = Borde.MURO, derecha = Borde.MURO
                )
                total += lado(v.copy(libresVerticales = 0), sub)
            }
        }
        return total
    }

    // ==================== AUXILIARES ====================

    /** El alto del tramo de [m]: el suyo en un vano escalonado, si no el del lado (ver [Tramo.alto]). */
    private fun altoDe(l: LadoNova, m: M): Float = l.tramos.getOrNull(m.tramo)?.alto ?: l.alto

    /**
     * Los módulos con su U de cálculo (N-42). El cruce se reparte en todo el lado: decidido el
     * 2026-09-25, con los tramos automáticos y ninguno bloqueado todos los módulos son iguales (420
     * en 4 + 3 hojas da 60 = 3, no 59.9 / 60.1). En inaparente el parante es transparente para el
     * vidrio (N-75): el ancho de vidrio no descuenta parantes.
     */
    private fun modulos(v: VentanaNova, l: LadoNova): List<M> {
        val apa = v.acabado == Acabado.APA
        val sumaW = l.tramos.sumOf { it.ancho.toDouble() }.toFloat()
        val escala = if (!apa && sumaW > 0f) l.anchoCalculo / sumaW else 1f
        val transicionesTramo = l.tramos.map { t -> transiciones(t) }
        val ajusteLado = if (l.divisiones > 0) transicionesTramo.sum() * v.cruce / l.divisiones else 0f
        val out = mutableListOf<M>()
        var g = 0
        l.tramos.forEachIndexed { ti, t ->
            t.modulos.forEachIndexed { pi, m ->
                val anchoVidrio = m.ancho * escala
                val uMod = if (apa) m.ancho + ajusteLado else anchoVidrio + ajusteLado
                out.add(M(ti, pi, g++, m.corrediza, m.ancho, anchoVidrio, uMod, m.recorteArriba, m.recorteAbajo, m.columna, m.inclinado))
            }
        }
        return out
    }

    /**
     * Cruces de un tramo: cada paso fijo↔corrediza. Un tramo de solo corredizas (full, doble,
     * triple) se cuenta como el orden clásico del mismo número de hojas, como la pantalla.
     *
     * Se mira el TRAMO, no la modulación de la ventana: en un diseño editado, un tramo `ccc` junto
     * a uno `fccf` es una ventana "nn", y contado por la modulación se quedaba sin cruces (la U de
     * fijos de la Dora editada daba 52.7 con `ccc` y 52.9 con `fcf`). El mismo tramo tiene que dar
     * lo mismo venga de donde venga.
     *
     * Antes:
     *     val p = if (modulacion in setOf("nfc", "ncc", "n3c")) DisenoAutomatico.patron(t.modulos.size, "nn")
     *             else t.modulos.map { it.corrediza }
     */
    private fun transiciones(t: Tramo): Int {
        val p = if (soloCorredizas(t)) DisenoAutomatico.patron(t.modulos.size, "nn")
                else t.modulos.map { it.corrediza }
        return (0 until p.size - 1).count { p[it] != p[it + 1] }
    }

    /** Un tramo de dos o más hojas, todas corredizas. */
    private fun soloCorredizas(t: Tramo): Boolean = t.modulos.size > 1 && t.modulos.all { it.corrediza }

    /**
     * La U horizontal de un borde en inaparente (dictado el 2026-09-25). La U sigue al vidrio a lo
     * largo del borde, y todo lo que apoya seguido en él va en una sola pieza:
     *
     * - Un tramo con mocheta en ese borde lleva U de punta a punta: su vidrio de mocheta, sus fijos
     *   y el paso de sus corredizas.
     * - Arriba, un tramo sin mocheta arriba (invertido, o normal sin puente) lleva fijo corredizo,
     *   que ya incluye su U: ahí la U se corta.
     * - Abajo, un tramo sin mocheta abajo: cada fijo apoya en su U y las corredizas la cortan (van
     *   sobre el riel).
     * - El parante no la corta: en inaparente el vidrio pasa por delante (N-75). Suma su ancho
     *   cuando la U sigue a los dos lados.
     *
     * Dora editada (370: `fccf` normal, P 2.5, `ccc` en bandera): arriba `370`; abajo `53.2` (el
     * fijo izquierdo) y `213.2` (el fijo derecho 53.2 + parante 2.5 + tramo 157.5). Normal: abajo
     * una por racha de fijos, arriba de lado a lado; invertido: abajo de lado a lado, arriba nada.
     */
    private fun uDelBorde(
        v: VentanaNova, l: LadoNova, ms: List<M>, vert: List<Vertical>,
        arriba: Boolean, aumentoPorHoja: Float
    ): List<Float> {
        val borde = if (arriba) Ubicacion.ARRIBA else Ubicacion.ABAJO
        // De izquierda a derecha: el largo de U de cada elemento, o null donde se corta.
        // La U de un fijo de inaparente llega hasta el centro del parante (el vidrio no descuenta
        // el parante, N-75), y un tramo cubierto se mide con su ancho de tramo, que empieza al otro
        // lado del parante (dictado el 2026-09-25). Así el parante suma:
        //  - entre dos tramos cubiertos, entero (210 + 2.5 + 157.5 = 370);
        //  - entre un fijo y un tramo cubierto, la mitad (Dora: 53.257 + 1.25 + 157.5 = 212.0);
        //  - entre dos fijos, nada: cada U ya lleva su mitad (420 en fccf P fcf: 60.4 + 60.4 = 120.8).
        // Todo en float; df1 solo al presentar.
        val elementos = mutableListOf<Float?>()
        val tipo = mutableListOf<Char>()   // 'f' fijo, 't' tramo cubierto, 'p' parante, '-' corte
        fun poner(e: Float?, t: Char) { elementos.add(e); tipo.add(if (e == null) '-' else t) }
        l.tramos.forEachIndexed { i, t ->
            if (i > 0) poner(v.puente.anchoParante, 'p')
            when {
                vert[i].mochetas.any { it.ubicacion == borde } -> {
                    // Esquina en ángulo (G-09): el tramo del borde suma el ancho de la U.
                    val esquina = (if (i == 0 && l.izquierda == Borde.ESQUINA_ANGULO) v.u.ancho else 0f) +
                        (if (i == l.tramos.lastIndex && l.derecha == Borde.ESQUINA_ANGULO) v.u.ancho else 0f)
                    // La U sigue al vidrio, que pasa por delante del parante de esquina (N-75): recupera lo
                    // que la estructura del tramo perdió en esa punta.
                    val parante = (if (i == 0) l.estructuraIzq else 0f) + (if (i == l.tramos.lastIndex) l.estructuraDer else 0f)
                    poner(t.ancho + parante + esquina, 't')
                }
                arriba && t.corredizas > 0 -> poner(null, '-')   // fijo corredizo
                else -> ms.filter { it.tramo == i }.forEach { m -> poner(if (m.corrediza) null else m.u + aumentoPorHoja, 'f') }
            }
        }
        val out = mutableListOf<Float>()
        var actual = 0f
        var abierta = false
        elementos.forEachIndexed { k, e ->
            val siguiente = tipo.getOrNull(k + 1) ?: '-'
            when {
                tipo[k] == '-' || (tipo[k] == 'p' && siguiente == '-') -> {
                    if (abierta) out.add(actual)
                    actual = 0f; abierta = false
                }
                tipo[k] == 'p' -> if (abierta) {
                    val tramos = (if (tipo[k - 1] == 't') 1 else 0) + (if (siguiente == 't') 1 else 0)
                    actual += e!! * tramos / 2f
                }
                else -> { actual += e!!; abierta = true }
            }
        }
        if (abierta) out.add(actual)
        return out
    }

    /**
     * Forma circular (N-01; sección 11 de las reglas, primer dictado N-65). Hache, portafelpa, tope y
     * los vidrios de fijos y corredizas siguen la lógica rectangular: el ancho por ceil vale para el
     * vidrio, que va entero y se corta en obra. Lo que cambia (180 × 180, hoja 128.6, tubo):
     *
     * - Puente: mocheta + puente es una flecha y la cuerda en esa flecha es el puente (126).
     * - Riel y fijo corredizo: dependen del puente, su misma medida − 0.06 (N-51).
     * - INA: la U es el perímetro, = 1 (565.5).
     * - APA, U de mocheta: el arco entre donde empiezan y terminan los puentes, por el borde de afuera
     *   del puente (132.2); con tubo, además la U recta sobre el puente, como en la rectangular.
     * - APA, U parante: el arco entre el puente inferior y el superior (143.2), − (u + 0.2) como N-43.
     * - APA, U de fijo: desde donde el fijo empieza contra la pared, sobre el puente de abajo
     *   (60.5 − 27 = 33.4).
     */
    private fun circular(
        v: VentanaNova, l: LadoNova, c: Circulo, ms: List<M>, vt: Vertical,
        u: MutableList<Pieza>, puente: MutableList<Pieza>, riel: MutableList<Pieza>, rielSup: MutableList<Pieza>
    ) {
        val pte = l.tramos[0].puente ?: v.puente
        val h = pte.altura
        val us = v.u.alto
        val sup = vt.mochetas.firstOrNull { it.ubicacion == Ubicacion.ARRIBA }
        val inf = vt.mochetas.firstOrNull { it.ubicacion == Ubicacion.ABAJO }
        // Los bordes del sistema, desde arriba: donde terminan las flechas.
        val yArriba = sup?.let { it.espacio + h } ?: 0f
        val yAbajo = inf?.let { l.alto - (it.espacio + h) } ?: l.alto

        puente.clear()
        sup?.let { puente.add(Pieza(c.cuerda(yArriba), 1)) }
        inf?.let { puente.add(Pieza(c.cuerda(yAbajo), 1)) }
        // El riel sobre el puente de abajo: su alto (1.2) sube hacia el centro, donde el círculo es más
        // ancho, y ahí todavía toca el muro (dictado el 2026-09-27).
        riel.replaceAll { it.copy(medida = c.cuerda(yAbajo - ALTO_RIEL) - 0.06f) }
        rielSup.replaceAll { it.copy(medida = c.cuerda(yArriba) - 0.06f) }

        u.clear()
        if (v.acabado != Acabado.APA) {
            u.add(Pieza(c.perimetro(), 1))
            return
        }
        // U de fijo: lo que queda del fijo dentro del círculo sobre el puente de abajo.
        // Se mide donde el perfil todavía toca el muro (dictado el 2026-09-27): su base toca en 33.4,
        // pero su alto sube hacia el centro, donde el círculo es más ancho.
        val fuera = c.fueraEn(yAbajo - us)
        val inicios = ms.runningFold(0f) { x, m -> x + m.ancho }
        rachasDeFijos(ms, cruzaParantes = false).forEach { racha ->
            val x0 = inicios[racha.first().global]
            val x1 = inicios[racha.last().global + 1]
            val corte = (fuera - x0).coerceAtLeast(0f) + (x1 - (l.anchoCalculo - fuera)).coerceAtLeast(0f)
            u.add(Pieza(racha.sumOf { it.u.toDouble() }.toFloat() - corte, 1))
        }
        // U de mocheta: el arco por el borde de afuera del puente; con tubo, la recta sobre él.
        val recta = !pte.esMultipleOGorrito
        sup?.let {
            u.add(Pieza(maxOf(c.arcoArriba(it.espacio), c.adentro(us).arcoArriba(it.espacio - us)), 1))
            if (recta) u.add(Pieza(c.cuerda(it.espacio), 1))
        }
        inf?.let {
            u.add(Pieza(maxOf(c.arcoAbajo(l.alto - it.espacio), c.adentro(us).arcoAbajo(l.alto - it.espacio - us)), 1))
            if (recta) u.add(Pieza(c.cuerda(l.alto - it.espacio), 1))
        }
        // U parante: el arco de cada costado con fijo, entre los dos puentes.
        // El arco más largo del perfil: el de su borde de adentro (radio − u) llega más lejos entre los
        // dos puentes que el del muro (143.7 contra 143.2).
        val lado = maxOf(c.arcoLado(yArriba, yAbajo), c.adentro(us).arcoLado(yArriba - us, yAbajo - us)) - (us + 0.2f)
        val n = (if (!ms.first().corrediza) 1 else 0) + (if (!ms.last().corrediza) 1 else 0)
        if (n > 0) u.add(Pieza(lado, n, Orientacion.VERTICAL))
    }

    /** Rachas de fijos seguidos. En INA atraviesan los parantes (N-42); en APA no. */
    private fun rachasDeFijos(ms: List<M>, cruzaParantes: Boolean): List<List<M>> {
        val out = mutableListOf<List<M>>()
        var actual = mutableListOf<M>()
        var anterior: M? = null
        for (m in ms) {
            // Un hueco en la lista (un módulo que se saltó) también corta la racha.
            val salto = anterior != null && m.global != anterior.global + 1
            val cortaParante = !cruzaParantes && actual.isNotEmpty() && actual.last().tramo != m.tramo
            if (m.corrediza || cortaParante || salto) {
                if (actual.isNotEmpty()) out.add(actual)
                actual = mutableListOf()
            }
            if (!m.corrediza) actual.add(m)
            anterior = m
        }
        if (actual.isNotEmpty()) out.add(actual)
        return out
    }

    /** Largo de cada racha de módulos del tipo pedido. */
    private fun rachas(p: List<Boolean>, corrediza: Boolean): List<Int> {
        val out = mutableListOf<Int>()
        var n = 0
        for (x in p) {
            if (x == corrediza) n++ else if (n > 0) { out.add(n); n = 0 }
        }
        if (n > 0) out.add(n)
        return out
    }

    /** El alto del riel, que en un extremo inclinado lo alarga por la pendiente (dictado el 2026-09-26). */
    private const val ALTO_RIEL = 1.2f

    /**
     * El largo del borde de abajo del sistema para el riel. Sin costados inclinados, el ancho del
     * tramo. Con uno, desde el lado inclinado (lo que el vano deja abajo) más lo que el alto del riel
     * sube por la pendiente: `alto del riel × tangente` de la inclinación respecto de la vertical.
     */
    private fun rielAbajo(t: Tramo, hoja: Float): Float {
        if (hoja <= 0f) return t.ancho
        var ancho = t.ancho
        fun extremo(m: Modulo?, diagonal: Float) {
            val inc = m?.inclinado ?: return
            if (diagonal <= 0f) return
            val horizontal = kotlin.math.sqrt((diagonal * diagonal - hoja * hoja).coerceAtLeast(0f))
            ancho -= inc.faltaAbajo
            ancho += ALTO_RIEL * horizontal / hoja
        }
        extremo(t.modulos.firstOrNull(), t.modulos.firstOrNull()?.inclinado?.diagonalIzq ?: 0f)
        extremo(t.modulos.lastOrNull(), t.modulos.lastOrNull()?.inclinado?.diagonalDer ?: 0f)
        return ancho
    }

    /** Paños de mocheta del tramo (N-62): los del diseño, o ceil(ancho / 180) iguales. */
    private fun panos(t: Tramo): List<Float> =
        t.panosMocheta ?: NovaCalculos.anchMota(t.ancho).let { n -> List(n) { t.ancho / n } }

    /**
     * Paños de una mocheta. Si su rectángulo es más angosto que el tramo (un vano con forma, N-96),
     * los paños del dibujo se ajustan a su ancho, o se reparten con N-62 si el dibujo no los trae.
     */
    private fun panosDe(t: Tramo, m: Mocheta): List<Float> {
        val w = m.ancho ?: return panos(t)
        val dibujados = t.panosMocheta?.takeIf { p -> p.sum() > 0f }
        return dibujados?.let { p -> val s = p.sum(); p.map { it * w / s } }
            ?: NovaCalculos.anchMota(w).let { n -> List(n) { w / n } }
    }
}
