package crystal.crystal.taller

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.hypot

// ==================== EL 3D QUE SALE DE TODAS LAS VISTAS (2026-10-19) ====================
// Medidas 3D tenía cada vista como un dibujo aparte: lo de una vista se veía en las otras solo
// como cajas a trazos, y la perspectiva solo sabía armar la ventana de esquina. Ahora todo lo
// dibujado en la frontal, la planta y las laterales se junta en una sola escena de caras y
// aristas, en cm, y de ella salen lo que cada vista enseña de las demás y la perspectiva.
//   Ejes: X a lo ancho (de izquierda a derecha), Y hacia arriba desde el suelo, Z hacia el fondo
//   desde el frente. La frontal está en Z = 0, la izquierda en X = 0 y la derecha en X = ancho.
//   Lo de las alzadas es un plano (un vidrio, un panel) en su pared; lo de la planta, una pared
//   que sube desde el suelo hasta su altura.

/** Un punto del espacio, en cm. */
data class P3(val x: Float, val y: Float, val z: Float)

/** Una cara plana, con sus puntos en orden, y de qué vista ([vista] = ordinal) y elemento salió. */
class Cara3d(val puntos: List<P3>, val vista: Int, val indice: Int)

/** Un borde que se dibuja. Las líneas internas de una pared curva no lo son. */
class Arista3d(val a: P3, val b: P3, val vista: Int, val indice: Int)

class Escena3d(val caras: List<Cara3d>, val aristas: List<Arista3d>) {
    val vacia: Boolean get() = caras.isEmpty() && aristas.isEmpty()

    /** Lo que ocupa la escena: mínimo y máximo en cada eje. Null si está vacía. */
    fun limites(): Pair<P3, P3>? {
        val puntos = caras.flatMap { it.puntos } + aristas.flatMap { listOf(it.a, it.b) }
        if (puntos.isEmpty()) return null
        return P3(puntos.minOf { it.x }, puntos.minOf { it.y }, puntos.minOf { it.z }) to
            P3(puntos.maxOf { it.x }, puntos.maxOf { it.y }, puntos.maxOf { it.z })
    }

    /** Lo que mide (ancho × alto × fondo) lo que salió de ese elemento. */
    fun medidasDe(vista: Int, indice: Int): Triple<Float, Float, Float>? {
        val puntos = caras.filter { it.vista == vista && it.indice == indice }.flatMap { it.puntos } +
            aristas.filter { it.vista == vista && it.indice == indice }.flatMap { listOf(it.a, it.b) }
        if (puntos.isEmpty()) return null
        return Triple(
            puntos.maxOf { it.x } - puntos.minOf { it.x },
            puntos.maxOf { it.y } - puntos.minOf { it.y },
            puntos.maxOf { it.z } - puntos.minOf { it.z }
        )
    }
}

/** Arma caras y aristas: planos de las alzadas y paredes que suben desde la planta. */
class ArmadorDeEscena {
    val caras = mutableListOf<Cara3d>()
    val aristas = mutableListOf<Arista3d>()

    fun escena() = Escena3d(caras.toList(), aristas.toList())

    /**
     * Un trazo de una alzada, ya en el espacio: cerrado es una cara (con su borde); abierto, solo
     * sus aristas (una línea gruesa, un parante dibujado suelto).
     */
    fun plano(puntos: List<P3>, cerrado: Boolean, vista: Int, indice: Int) {
        val limpios = sinRepetidos(puntos, cerrado)
        if (limpios.size < 2) return
        if (cerrado && limpios.size >= 3) caras.add(Cara3d(limpios, vista, indice))
        val tramos = if (cerrado) limpios.size else limpios.size - 1
        for (i in 0 until tramos) aristas.add(Arista3d(limpios[i], limpios[(i + 1) % limpios.size], vista, indice))
    }

    /**
     * Un trazo de la planta ([base] en (x, z)) que sube de [desde] a [hasta]: una pared por cada
     * tramo. Las aristas son el pie, la cabeza y las verticales en las puntas y en las esquinas;
     * en una curva (muchos tramos cortos que apenas doblan) no hay verticales en medio. Cerrado,
     * además lleva tapa arriba y abajo: es un cuerpo (una columna, un mueble).
     */
    fun pared(base: List<Pair<Float, Float>>, cerrado: Boolean, desde: Float, hasta: Float, vista: Int, indice: Int) {
        val puntos = sinRepetidosXZ(base, cerrado)
        if (puntos.size < 2 || hasta - desde < 0.01f) return
        fun abajo(p: Pair<Float, Float>) = P3(p.first, desde, p.second)
        fun arriba(p: Pair<Float, Float>) = P3(p.first, hasta, p.second)
        val tramos = if (cerrado) puntos.size else puntos.size - 1
        for (i in 0 until tramos) {
            val a = puntos[i]
            val b = puntos[(i + 1) % puntos.size]
            caras.add(Cara3d(listOf(abajo(a), abajo(b), arriba(b), arriba(a)), vista, indice))
            aristas.add(Arista3d(abajo(a), abajo(b), vista, indice))
            aristas.add(Arista3d(arriba(a), arriba(b), vista, indice))
        }
        puntos.indices.forEach { i ->
            val punta = !cerrado && (i == 0 || i == puntos.lastIndex)
            val esquina = when {
                cerrado -> esEsquina(puntos[(i - 1 + puntos.size) % puntos.size], puntos[i], puntos[(i + 1) % puntos.size])
                i in 1 until puntos.lastIndex -> esEsquina(puntos[i - 1], puntos[i], puntos[i + 1])
                else -> false
            }
            if (punta || esquina) aristas.add(Arista3d(abajo(puntos[i]), arriba(puntos[i]), vista, indice))
        }
        if (cerrado && puntos.size >= 3) {
            caras.add(Cara3d(puntos.map { abajo(it) }, vista, indice))
            caras.add(Cara3d(puntos.map { arriba(it) }, vista, indice))
        }
    }

    companion object {
        /** Lo que tiene que doblar un trazo para que su vértice cuente como esquina, en grados. */
        const val GRADOS_DE_ESQUINA = 25f

        fun esEsquina(a: Pair<Float, Float>, b: Pair<Float, Float>, c: Pair<Float, Float>): Boolean {
            val ux = b.first - a.first
            val uz = b.second - a.second
            val vx = c.first - b.first
            val vz = c.second - b.second
            val lu = hypot(ux, uz)
            val lv = hypot(vx, vz)
            if (lu < 1e-4f || lv < 1e-4f) return false
            val cos = ((ux * vx + uz * vz) / (lu * lv)).coerceIn(-1f, 1f)
            return Math.toDegrees(acos(cos.toDouble())) > GRADOS_DE_ESQUINA
        }

        private fun sinRepetidos(puntos: List<P3>, cerrado: Boolean): List<P3> {
            val r = mutableListOf<P3>()
            puntos.forEach { p ->
                val u = r.lastOrNull()
                if (u == null || abs(u.x - p.x) + abs(u.y - p.y) + abs(u.z - p.z) > 0.01f) r.add(p)
            }
            if (cerrado && r.size > 2) {
                val a = r.first()
                val b = r.last()
                if (abs(a.x - b.x) + abs(a.y - b.y) + abs(a.z - b.z) <= 0.01f) r.removeAt(r.lastIndex)
            }
            return r
        }

        private fun sinRepetidosXZ(puntos: List<Pair<Float, Float>>, cerrado: Boolean): List<Pair<Float, Float>> =
            sinRepetidos(puntos.map { P3(it.first, 0f, it.second) }, cerrado).map { it.x to it.z }
    }
}
