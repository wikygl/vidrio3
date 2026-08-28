package crystal.crystal.taller.mamparas

import crystal.crystal.taller.nova.NovaCalculos
import kotlin.math.ceil

/** Un módulo de la mampara: fijo ('f') o corrediza ('c'), con su ancho de vidrio (cm). */
data class ModuloMampara(val tipo: Char, val ancho: Float) {
    val esFijo: Boolean get() = tipo != 'c'
}

/**
 * Diseño simbólico de la mampara paflón: la secuencia de tramos y módulos (fijo/corrediza) con sus
 * anchos. Es la ÚNICA fuente de geometría: tanto el dibujo (MamparaPaflonRender) como el cálculo de
 * materiales la consumen, para que nunca se desincronicen.
 *
 * Reglas (iguales que el dibujo): los fijos van a los extremos de cada tramo; las corredizas se
 * esconden tras los fijos (1 bastidor) y dos del mismo plano juntan bastidores (2); una corrediza
 * en el extremo conserva su parante; entre tramos va un parante estructural de paflón.
 *
 * Hoy los anchos salen iguales; queda preparado para la **mampara desigual** (futura): bastará con
 * construir los tramos con anchos por módulo en vez de [desde].
 */
class MamparaModulos private constructor(
    val tramos: List<List<ModuloMampara>>,
    val vidrioAncho: Float,
    val bastidor: Float,
    val paranteTramo: Float
) {
    val nModulos: Int get() = tramos.sumOf { it.size }
    val nFijos: Int get() = tramos.sumOf { t -> t.count { it.esFijo } }
    val nCorredizas: Int get() = tramos.sumOf { t -> t.count { !it.esFijo } }
    val nParantesTramo: Int get() = (tramos.size - 1).coerceAtLeast(0)

    /** Uniones fijo-corrediza dentro de los tramos (= parantes de fijo = portafelpas). */
    val nUnionesFC: Int get() = tramos.sumOf { t ->
        (0 until t.size - 1).count { t[it].esFijo != t[it + 1].esFijo }
    }

    /** Lados de corrediza que NO colindan con un fijo (colindan con corrediza, marco o parante de
     *  tramo) = parantes de tope. */
    val nTopeParante: Int get() = (2 * nCorredizas - nUnionesFC).coerceAtLeast(0)

    /** Bastidores visibles de un tramo (encuentros internos + corrediza en un extremo). */
    fun bastVisibles(t: List<ModuloMampara>): Int {
        var b = 0
        for (i in 0 until t.size - 1) b += if (t[i].esFijo != t[i + 1].esFijo) 1 else 2
        if (t.firstOrNull()?.esFijo == false) b += 1
        if (t.lastOrNull()?.esFijo == false) b += 1
        return b
    }

    /** Ancho de un tramo (cm): suma de vidrios + sus bastidores visibles. */
    fun anchoTramo(t: List<ModuloMampara>): Float =
        t.sumOf { it.ancho.toDouble() }.toFloat() + bastVisibles(t) * bastidor

    /** Símbolo de texto: f<w>c<w>...;P;f<w>... (P = parante de tramo). */
    fun simbolo(): String = tramos.joinToString(";P;") { t ->
        t.joinToString("") { "${it.tipo}<${"%.1f".format(it.ancho).replace(",", ".")}>" }
    }

    companion object {
        const val P_ALT = 3.8f // otra medida del paflón (riel y parante de tramo)

        private fun gruposDe(divisiones: Int, maxPorGrupo: Int): List<Int> {
            if (divisiones <= maxPorGrupo) return listOf(divisiones)
            val grupos = ceil(divisiones / maxPorGrupo.toDouble()).toInt().coerceAtLeast(1)
            val base = divisiones / grupos
            val extra = divisiones % grupos
            return (0 until grupos).map { idx -> if (idx < extra) base + 1 else base }
        }

        private fun bastTramoPat(pat: String): Int {
            var b = 0
            for (i in 0 until pat.length - 1) b += if (pat[i] != pat[i + 1]) 1 else 2
            if (pat.firstOrNull() == 'c') b += 1
            if (pat.lastOrNull() == 'c') b += 1
            return b
        }

        /**
         * Patrón por tramos elegido a mano, en la forma `fcf|cf` (tramos separados por `|`).
         *
         * Devuelve null si la cadena no describe una mampara utilizable: así una configuración vieja
         * o corrupta cae al automático en vez de dibujar cualquier cosa.
         */
        fun patronManual(raw: String?): List<String>? {
            val s = raw?.trim().orEmpty()
            if (s.isEmpty()) return null
            val tramos = s.split("|").map { it.trim().lowercase() }
            if (tramos.isEmpty() || tramos.any { t -> t.isEmpty() || t.any { it != 'f' && it != 'c' } }) return null
            return tramos
        }

        /** Cuántos módulos describe un patrón manual — es lo que manda sobre `divisiones`. */
        fun modulosDe(patron: List<String>): Int = patron.sumOf { it.length }

        /** Construye el diseño simbólico (anchos iguales) desde el descriptor. */
        fun desde(d: MamparaPaflonDescriptor): MamparaModulos {
            // El patrón elegido a mano manda sobre el automático: si el usuario dijo `fcc`, no se
            // le devuelve `fcf` porque la tabla de ordenDivis lo prefiera.
            val manual = patronManual(d.patron)
            val nPaneles = manual?.let { modulosDe(it) } ?: d.divisiones.coerceAtLeast(1)
            val anchoUtil = d.ancho - 2 * d.marco
            // Patrón fijo/corrediza por tramo, igual que NovaCorrediza pero con tramos de máximo 4.
            val patrones = manual ?: gruposDe(nPaneles, 4).map { t ->
                NovaCalculos.ordenDivis(t, t.toFloat()).filter { it == 'f' || it == 'c' }
            }
            val nBast = patrones.sumOf { bastTramoPat(it) }
            val nPar = (patrones.size - 1).coerceAtLeast(0)
            val vidrioAncho = (anchoUtil - nBast * d.bastidor - nPar * P_ALT) / nPaneles
            val tramos = patrones.map { pat -> pat.map { ModuloMampara(it, vidrioAncho) } }
            return MamparaModulos(tramos, vidrioAncho, d.bastidor, P_ALT)
        }
    }
}
