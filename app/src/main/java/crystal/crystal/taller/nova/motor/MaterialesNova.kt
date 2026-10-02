package crystal.crystal.taller.nova.motor

import crystal.crystal.taller.nova.NovaCalculos

/** G-01: en las listas va primero lo horizontal, después lo diagonal, al final lo vertical. */
enum class Orientacion { HORIZONTAL, DIAGONAL, VERTICAL }

/**
 * Un trozo curvo de una pieza (N-102): su largo sobre un arco de [radio]. El radio lleva signo: en
 * una S, negativo es la panza hacia adentro.
 */
data class Arco(val largo: Float, val radio: Float)

data class Pieza(
    val medida: Float,
    val cantidad: Int,
    val orientacion: Orientacion = Orientacion.HORIZONTAL,
    /** El tramo del lado donde va; −1 si es del lado entero. Sirve para saber en qué arco se curva. */
    val tramo: Int = -1,
    /** Si va curvada (N-102), sus trozos, cada uno sobre su arco. Vacío: recta. */
    val curva: List<Arco> = emptyList()
)

data class Vidrio(
    val ancho: Float,
    val alto: Float,
    val cantidad: Int = 1,
    val tramo: Int = -1,
    /** Si va curvado, por su ancho (N-102). */
    val curva: List<Arco> = emptyList()
)

/** Cómo se llama el riel superior (N-52, N-52b). */
enum class RielSuperior(val etiqueta: String) {
    FIJO_CORREDIZO("fijo corredizo"), U_FELPERO("U felpero"), DOBLE_CORREDIZA("doble corrediza")
}

/**
 * Lo que sale de [MotorNova]: piezas por perfil, sin texto todavía. El texto de la pantalla y del
 * archivo se arma al final con [texto] y [textoVidrios], con el mismo formato de siempre
 * (`medida = cantidad`, vidrios `ancho x alto = cantidad`).
 */
data class MaterialesNova(
    val u: List<Pieza> = emptyList(),
    val puente: List<Pieza> = emptyList(),
    val riel: List<Pieza> = emptyList(),
    val rielSuperior: List<Pieza> = emptyList(),
    val nombreRielSuperior: RielSuperior = RielSuperior.FIJO_CORREDIZO,
    /**
     * Perfil doble corrediza de los tramos de solo corredizas, cuando conviven con tramos clásicos
     * en la misma ventana (un diseño editado: `fccf` y `ccc`). Si todos los tramos son de solo
     * corredizas, la doble corrediza va en [rielSuperior] con su nombre y esta lista queda vacía.
     */
    val dobleCorrediza: List<Pieza> = emptyList(),
    val portafelpa: List<Pieza> = emptyList(),
    val tee: List<Pieza> = emptyList(),
    val tope: List<Pieza> = emptyList(),
    val hache: List<Pieza> = emptyList(),
    /** Parantes verticales: entre tramos, de lados libres y de esquina. */
    val parante: List<Pieza> = emptyList(),
    /** Falso puente aparte ("tubo 2.5") cuando el puente es Múltiple (N-78). */
    val falsoPuente: List<Pieza> = emptyList(),
    val vidrios: List<Vidrio> = emptyList()
) {
    /** Suma los materiales de dos lados (L, C, serie). */
    operator fun plus(o: MaterialesNova) = MaterialesNova(
        u = u + o.u, puente = puente + o.puente, riel = riel + o.riel,
        rielSuperior = rielSuperior + o.rielSuperior,
        nombreRielSuperior = if (rielSuperior.isEmpty()) o.nombreRielSuperior else nombreRielSuperior,
        dobleCorrediza = dobleCorrediza + o.dobleCorrediza,
        portafelpa = portafelpa + o.portafelpa, tee = tee + o.tee, tope = tope + o.tope,
        hache = hache + o.hache, parante = parante + o.parante,
        falsoPuente = falsoPuente + o.falsoPuente, vidrios = vidrios + o.vidrios
    )

    /** Las mismas piezas, cambiadas una por una (para marcar su tramo o su curva). */
    fun cadaPieza(p: (Pieza) -> Pieza, vidrio: (Vidrio) -> Vidrio) = copy(
        u = u.map(p), puente = puente.map(p), riel = riel.map(p), rielSuperior = rielSuperior.map(p),
        dobleCorrediza = dobleCorrediza.map(p), portafelpa = portafelpa.map(p), tee = tee.map(p),
        tope = tope.map(p), hache = hache.map(p), parante = parante.map(p),
        falsoPuente = falsoPuente.map(p), vidrios = vidrios.map(vidrio)
    )

    /** Todo es del tramo [i] (lo que se calculó como un sistema aparte dentro de él). */
    fun enTramo(i: Int) = cadaPieza({ it.copy(tramo = i) }, { it.copy(tramo = i) })

    /** Si algo de la ventana va curvado (N-102). */
    val hayCurva: Boolean
        get() = vidrios.any { it.curva.isNotEmpty() } ||
            listOf(u, puente, riel, rielSuperior, dobleCorrediza, portafelpa, tee, tope, hache, parante, falsoPuente)
                .any { l -> l.any { it.curva.isNotEmpty() } }

    companion object {
        /**
         * Una lista de piezas como texto: suma las que se ven iguales (df1, un decimal truncado) y
         * ordena horizontal → diagonal → vertical (G-01), respetando el orden de aparición dentro
         * de cada grupo.
         */
        fun texto(piezas: List<Pieza>): String {
            val cuenta = linkedMapOf<Pair<Orientacion, String>, Int>()
            for (p in piezas) {
                if (p.cantidad <= 0 || p.medida <= 0f) continue
                val k = p.orientacion to NovaCalculos.df1(p.medida)
                cuenta[k] = (cuenta[k] ?: 0) + p.cantidad
            }
            return cuenta.entries
                .sortedBy { it.key.first.ordinal }
                .joinToString("\n") { "${it.key.second} = ${it.value}" }
        }

        fun textoVidrios(vidrios: List<Vidrio>): String {
            val cuenta = linkedMapOf<String, Int>()
            for (v in vidrios) {
                if (v.cantidad <= 0 || v.ancho <= 0f || v.alto <= 0f) continue
                val k = "${NovaCalculos.df1(v.ancho)} x ${NovaCalculos.df1(v.alto)}"
                cuenta[k] = (cuenta[k] ?: 0) + v.cantidad
            }
            return cuenta.entries.joinToString("\n") { "${it.key} = ${it.value}" }
        }
    }
}
