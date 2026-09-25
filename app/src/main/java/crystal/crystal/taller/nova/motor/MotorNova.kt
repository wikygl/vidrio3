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
    private data class Mocheta(val ubicacion: Ubicacion, val espacio: Float, val directo: Float)

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
        val ancho: Float, val anchoVidrio: Float, val u: Float
    )

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
        val h = v.puente.altura
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
        val iSistema = franjas.indexOfFirst { it.sistema }
        val altoHoja = franjas.getOrNull(iSistema)?.alto?.takeIf { it > 0f && it < l.alto } ?: l.alto
        // Las franjas de sistema de más (una mocheta con corrediza, N-88) tienen su alto propio.
        val extras = franjas.withIndex().filter { it.value.sistema && it.index != iSistema }
            .map { (k, f) -> SistemaExtra((f.alto - h * cargas[k]).coerceAtLeast(0f), f.modulos) }
        val altoSistemas = altoHoja + franjas.withIndex()
            .filter { it.value.sistema && it.index != iSistema }.sumOf { it.value.alto.toDouble() }.toFloat()
        val disponible = (l.alto - altoSistemas).coerceAtLeast(0f)
        val mochetas = franjas.withIndex().filter { !it.value.sistema }
        val suma = mochetas.sumOf { it.value.alto.toDouble() }.toFloat()
        val lista = mochetas.map { (i, f) ->
            val parte = if (suma > 0f) disponible * f.alto / suma else disponible / mochetas.size
            Mocheta(if (i < iSistema) Ubicacion.ABAJO else Ubicacion.ARRIBA, parte - h * cargas[i], parte)
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
        val vert = l.tramos.map { vertical(v, l, it) }
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
                    val n = if (vert[i].hayMocheta && v.puente.esMultipleOGorrito) 1 else 2
                    u.add(Pieza(t.ancho, n))
                } else if (!vert.all { it.soloAbajo }) u.add(Pieza(l.anchoCalculo + aumentoLado, 2))
            }
            fullFijos -> if (apa || !vert[0].soloAbajo) u.add(Pieza(ms.first().u + aumentoLado, 2))
            // Como la pantalla: en INA invertido los fijos no llevan U de fijos.
            else -> rachasDeFijos(ms.filter { apa || !vDe(it).soloAbajo }, cruzaParantes = !apa).forEach { racha ->
                u.add(Pieza(racha.sumOf { (it.u + aumentoPorHoja).toDouble() }.toFloat(), 1))
            }
        }

        // ---------- U de mocheta horizontal APA (N-45) ----------
        if (apa && us != 0f) {
            val porTramoN = if (v.puente.esMultipleOGorrito) 1 else 2
            l.tramos.forEachIndexed { i, t -> vert[i].mochetas.forEach { u.add(Pieza(t.ancho, porTramoN)) } }
        }
        // ---------- U superior e inferior INA (N-46) ----------
        // Albergan el vidrio que va delante de la estructura (parantes y puentes), así que no las
        // afectan los tramos: siempre el ancho entero del lado (dictado 2026-09-25: 420 en dos
        // tramos de 4 y 3 sigue siendo 420). Superior si algún tramo tiene mocheta arriba; inferior
        // si alguno la tiene abajo. Un lado de solo fijos no la lleva (en la L dictada, el lado de 40
        // con un fijo lleva solo su U de fijo, 41 = 2).
        if (!apa && ms.any { it.corrediza }) {
            val arriba = vert.any { vt -> vt.mochetas.any { it.ubicacion == Ubicacion.ARRIBA } }
            val abajo = vert.any { vt -> vt.mochetas.any { it.ubicacion == Ubicacion.ABAJO } }
            val n = (if (arriba) 1 else 0) + (if (abajo) 1 else 0)
            if (n > 0) u.add(Pieza(l.anchoCalculo + aumentoLado, n))
        }

        // ---------- U parante (N-43) ----------
        if (us != 0f) {
            if (apa) {
                // APA: cada fijo contra pared o parante (el borde de cada tramo, esquina incluida).
                porTramo.forEach { (i, t) ->
                    val n = (if (!t.first().corrediza) 1 else 0) + (if (!t.last().corrediza) 1 else 0)
                    if (n > 0) u.add(Pieza(vert[i].altoHoja - (us + 0.2f), n, Orientacion.VERTICAL))
                }
            } else {
                // INA: solo los fijos de los bordes de la ventana contra muro, acople o libre.
                val n = (if (!ms.first().corrediza && esMuro(l.izquierda)) 1 else 0) +
                    (if (!ms.last().corrediza && esMuro(l.derecha)) 1 else 0)
                if (n > 0) u.add(Pieza(l.alto - 2 * us, n, Orientacion.VERTICAL))
            }
        }

        // ---------- U vertical de mocheta (N-44, N-45) ----------
        if (us != 0f) {
            if (apa) {
                vert.forEach { vt -> vt.mochetas.forEach { m -> u.add(Pieza(m.espacio - 2 * us, 2, Orientacion.VERTICAL)) } }
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
        l.tramos.forEachIndexed { i, t -> if (vert[i].corridas > 0) puente.add(Pieza(t.ancho, vert[i].corridas)) }
        val falsoPuente = mutableListOf<Pieza>()
        if (v.libresVerticales > 0) {
            if (apa && v.puente.esMultiple) falsoPuente.add(Pieza(l.anchoCalculo, v.libresVerticales))
            else puente.add(Pieza(l.anchoCalculo, v.libresVerticales))
        }

        // ---------- Parantes verticales (N-27, N-78) ----------
        val nParantes = (l.tramos.size - 1) + listOf(l.izquierda, l.derecha).count { it == Borde.LIBRE }
        if (nParantes > 0) parante.add(Pieza(l.altoMedido, nParantes, Orientacion.VERTICAL))

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
            if (esFull(t)) {
                riel.add(Pieza(medida, t.corredizas))
                val doble = Pieza(medida, ceil(t.corredizas / 2.0).toInt())
                if (dobleAparte) dobleCorrediza.add(doble) else rielSup.add(doble)
                return@forEachIndexed
            }
            riel.add(Pieza(medida, 1))
            // Como la pantalla: no va cuando hay mocheta arriba con puente Múltiple, que ya lo trae.
            val lleva = !vert[i].hayMocheta || vert[i].soloAbajo || !v.puente.esMultiple
            if (lleva) rielSup.add(Pieza(medida, 1))
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
            val n = t.count { it.corrediza } * 2 + t.indices.sumOf { k ->
                if (t[k].corrediza) 0
                else (if (k > 0 && t[k - 1].corrediza) 1 else 0) + (if (k < t.lastIndex && t[k + 1].corrediza) 1 else 0)
            }
            portafelpa.add(Pieza(NovaCalculos.portafelpa(vert[i].altoHoja), n, Orientacion.VERTICAL))
            // Todo borde de tramo es pared o parante: también la esquina, que lleva su parante de
            // esquina P<90> (N-84), y el lado libre, que lleva el suyo (N-78).
            val nt = (if (t.first().corrediza) 1 else 0) + (if (t.last().corrediza) 1 else 0)
            if (nt > 0) tope.add(Pieza(vert[i].altoHoja - 0.9f, nt, Orientacion.VERTICAL))
        }

        // ---------- Tee de aparente (N-57) ----------
        // El "tubo" de N-53 (el ancho, 1 por mocheta) es el mismo puente: ya va en [puente], con
        // sus dos corridas en bandera. La pantalla vieja lo calculaba aparte y no lo mostraba.
        val tee = mutableListOf<Pieza>()
        if (apa) l.tramos.forEachIndexed { i, t ->
            val nTees = (panos(t).size - 1).coerceAtLeast(0)
            if (nTees == 0) return@forEachIndexed
            vert[i].mochetas.forEach { m ->
                val valor = NovaPerfilesHelper.calcularTe(m.espacio, us, v.puente.nombre)
                if (valor > 0f) tee.add(Pieza(valor, nTees, Orientacion.VERTICAL))
            }
        }

        // ---------- Vidrios fijos (N-60, N-61, N-87) ----------
        val holgura = if (us == 0f) 1f else 0.2f
        ms.filter { !it.corrediza }.forEach { f ->
            val descuento = if (fullFijos) NovaCalculos.GRUNA
            else if (apa) NovaCalculos.descuentoColindanciaFijo(porTramo.getValue(f.tramo).map { if (it.corrediza) 'c' else 'f' }, f.pos)
            else NovaCalculos.descuentoColindanciaFijo(patronLado, f.global)
            val alto = (if (apa) vDe(f).altoHoja else l.alto) - (us + holgura)
            vidrios.add(Vidrio(f.u - descuento, alto))
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
                    val alto = if (v.puente.esMultipleOGorrito) m.espacio - 0.5f else m.espacio - (us + 0.3f)
                    panos(t).forEach { w -> vidrios.add(Vidrio(w - 0.5f, alto)) }
                }
            }
        } else {
            // INA: la mocheta va sobre las corredizas (los fijos van de piso a techo). El ancho es
            // lo que dejan las U de fijos ya cortadas (con su medida truncada, como salen de la
            // lista), menos una gruña de 0.3 a cada lado. Ejemplo dictado el 2026-09-25: 140 en
            // fcf, U de fijo 47.1 → 140 − 2 × 47.1 − 0.6 = 45.2. Alto: arriba +1, abajo −0.5 (N-64).
            porTramo.forEach { (i, t) ->
                val nc = t.count { it.corrediza }
                if (nc == 0 || !vert[i].hayMocheta) return@forEach
                val espacio = t.sumOf { it.anchoVidrio.toDouble() }.toFloat() -
                    t.filter { !it.corrediza }.sumOf { NovaCalculos.df1(it.u).toDouble() }.toFloat()
                val anchos = rachas(t.map { it.corrediza }, true).map { n -> espacio * n / nc - 2 * GRUNA_MOCHETA_INA }
                vert[i].mochetas.forEach { m ->
                    val alto = if (m.ubicacion == Ubicacion.ARRIBA) m.directo + 1f else m.directo - 0.5f
                    anchos.forEach { w -> vidrios.add(Vidrio(w, alto)) }
                }
            }
        }

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
                out.add(M(ti, pi, g++, m.corrediza, m.ancho, anchoVidrio, uMod))
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

    /** Paños de mocheta del tramo (N-62): los del diseño, o ceil(ancho / 180) iguales. */
    private fun panos(t: Tramo): List<Float> =
        t.panosMocheta ?: NovaCalculos.anchMota(t.ancho).let { n -> List(n) { t.ancho / n } }
}
