package crystal.crystal.taller.nova.motor

import crystal.crystal.taller.nova.NovaCalculos

/** G-01: en las listas va primero lo horizontal, después lo diagonal, al final lo vertical. */
enum class Orientacion { HORIZONTAL, DIAGONAL, VERTICAL }

data class Pieza(val medida: Float, val cantidad: Int, val orientacion: Orientacion = Orientacion.HORIZONTAL)

data class Vidrio(val ancho: Float, val alto: Float, val cantidad: Int = 1)

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
        portafelpa = portafelpa + o.portafelpa, tee = tee + o.tee, tope = tope + o.tope,
        hache = hache + o.hache, parante = parante + o.parante,
        falsoPuente = falsoPuente + o.falsoPuente, vidrios = vidrios + o.vidrios
    )

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
