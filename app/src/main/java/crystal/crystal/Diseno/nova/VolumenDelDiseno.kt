package crystal.crystal.Diseno.nova

import crystal.crystal.taller.ArcoEsquina
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/** Un punto en el espacio, en centímetros: [x] a lo ancho, [y] hacia el fondo, [z] hacia arriba. */
data class Punto3D(val x: Float, val y: Float, val z: Float)

/** Un punto ya en el papel, listo para dibujar. */
data class PuntoPlano(val x: Float, val y: Float)

/**
 * Una cara del volumen: un trozo de pared levantado del suelo.
 *
 * Una pared recta es UNA cara; una curva son varias, una por trozo del arco, que es como se
 * factea una superficie curva para dibujarla.
 *
 * [desdeU] y [hastaU] dicen qué parte del tramo ocupa esta cara a lo largo de su DESARROLLO, de 0
 * a 1. Son las que permitirán colocar encima los módulos y las franjas: cada uno sabe en qué
 * trozo del ancho cae, y con estas dos sabe sobre qué cara va y en qué parte de ella.
 */
data class CaraDelVolumen(
    val indiceTramo: Int,
    val abajoIzq: Punto3D,
    val abajoDer: Punto3D,
    val arribaDer: Punto3D,
    val arribaIzq: Punto3D,
    val desdeU: Float,
    val hastaU: Float,
    val esCurva: Boolean
) {
    val esquinas: List<Punto3D> get() = listOf(abajoIzq, abajoDer, arribaDer, arribaIzq)
}

/**
 * La ventana levantada del suelo: su planta extruida a lo alto de cada pared.
 *
 * Es lo que hay que dibujar para verla en tres dimensiones. Aquí no hay pantalla ni pinceles: son
 * centímetros, para poder comprobarla en frío antes de pintar nada.
 */
data class VolumenDelDiseno(val caras: List<CaraDelVolumen>) {

    /**
     * Las caras en el orden en que hay que pintarlas: de la más lejana a la más cercana.
     *
     * Es el método del pintor, que para una ventana —una cinta de paredes, sin nada cerrado— basta
     * y sobra: lo de atrás se tapa solo cuando se le pinta lo de delante encima.
     */
    fun deLejosACerca(
        giroGrados: Float = GIRO_POR_DEFECTO,
        elevacionGrados: Float = ELEVACION_POR_DEFECTO
    ): List<CaraDelVolumen> =
        caras.sortedByDescending { cara ->
            cara.esquinas.map { profundidad(it, giroGrados, elevacionGrados) }.average()
        }

    /** El centro del volumen: desde donde se mide la distancia del ojo en perspectiva. */
    fun centro(): Punto3D {
        val puntos = caras.flatMap { it.esquinas }
        if (puntos.isEmpty()) return Punto3D(0f, 0f, 0f)
        return Punto3D(
            puntos.map { it.x }.average().toFloat(),
            puntos.map { it.y }.average().toFloat(),
            puntos.map { it.z }.average().toFloat()
        )
    }

    companion object {
        /** Desde dónde se mira por defecto: girada un poco, para que se vean dos caras a la vez. */
        const val GIRO_POR_DEFECTO = 25f

        /** Cuánto se mira desde arriba por defecto: la isométrica de siempre, con el suelo a 30°. */
        const val ELEVACION_POR_DEFECTO = 30f

        /** En cuántos trozos se parte una pared curva para dibujarla. */
        const val TROZOS_DE_CURVA = 10

        /**
         * Levanta la planta a lo alto de cada pared.
         *
         * Cada pared recta da una cara; cada curva, [trozosDeCurva]. El alto de la ventana no se
         * reparte: cada pared sube el suyo, que es como se miden —una puede ser más baja que la de
         * al lado sin que eso cambie nada de las demás—.
         */
        fun de(planta: PlantaDelDiseno, trozosDeCurva: Int = TROZOS_DE_CURVA): VolumenDelDiseno {
            val caras = mutableListOf<CaraDelVolumen>()
            planta.paredes.forEach { pared ->
                val puntos = if (pared.esCurva) puntosDelArco(pared, trozosDeCurva)
                else listOf(pared.desde, pared.hasta)
                val trozos = (puntos.size - 1).coerceAtLeast(1)
                for (k in 0 until trozos) {
                    val a = puntos[k]
                    val b = puntos[k + 1]
                    caras.add(
                        CaraDelVolumen(
                            indiceTramo = pared.indiceTramo,
                            abajoIzq = Punto3D(a.x, a.y, 0f),
                            abajoDer = Punto3D(b.x, b.y, 0f),
                            arribaDer = Punto3D(b.x, b.y, pared.altoCm),
                            arribaIzq = Punto3D(a.x, a.y, pared.altoCm),
                            desdeU = k / trozos.toFloat(),
                            hastaU = (k + 1) / trozos.toFloat(),
                            esCurva = pared.esCurva
                        )
                    )
                }
            }
            return VolumenDelDiseno(caras)
        }

        /**
         * Los puntos de una pared curva, repartidos a lo largo de su arco.
         *
         * El rumbo con el que entra el arco se saca de su propia cuerda: la cuerda de un arco
         * apunta a mitad de camino de lo que gira, así que el rumbo de entrada es el de la cuerda
         * menos medio giro. De ahí se va avanzando en cuerdas pequeñas, todas iguales.
         */
        private fun puntosDelArco(pared: ParedEnPlanta, trozos: Int): List<PuntoPlanta> {
            val n = trozos.coerceAtLeast(1)
            val arco = ArcoEsquina.deDesarrolloYFlecha(pared.anchoCm, pared.flechaCm)
                ?: return listOf(pared.desde, pared.hasta)
            val giro = Math.toRadians(arco.anguloGrados.toDouble())
            val rumboCuerda = atan2(
                (pared.hasta.y - pared.desde.y).toDouble(),
                (pared.hasta.x - pared.desde.x).toDouble()
            )
            var rumbo = rumboCuerda - giro / 2.0
            val paso = giro / n
            // Cada trocito del arco es una cuerda de su mismo radio: 2·R·sen(paso/2).
            val cuerdita = 2.0 * arco.radio * sin(paso / 2.0)
            val puntos = mutableListOf(pared.desde)
            var x = pared.desde.x
            var y = pared.desde.y
            repeat(n) {
                val dir = rumbo + paso / 2.0
                x += (cos(dir) * cuerdita).toFloat()
                y += (sin(dir) * cuerdita).toFloat()
                puntos.add(PuntoPlanta(x, y))
                rumbo += paso
            }
            return puntos
        }

        /**
         * Del espacio al papel.
         *
         * Por defecto es la isométrica de siempre: sin punto de fuga, lo que está lejos se dibuja
         * igual de grande que lo que está cerca, el suelo inclinado 30° y la vertical vertical.
         * Para un plano de taller es lo que conviene —las medidas se pueden leer— y quita de en
         * medio el problema de la perspectiva falseada, que no distingue un ángulo de otro.
         *
         * [giroGrados] hace girar la ventana sobre el suelo, para mirarla desde otro lado;
         * [elevacionGrados] dice cuánto se mira desde arriba (0 = de frente, 90 = la planta). Con
         * [distanciaCm] mayor que cero pasa a ser perspectiva de verdad: el ojo a esa distancia
         * del [centro] del volumen, con las paralelas juntándose y lo lejano más chico. Ahí las
         * medidas ya no se leen del dibujo, pero se ve como en obra.
         */
        fun proyectar(
            punto: Punto3D,
            giroGrados: Float = GIRO_POR_DEFECTO,
            elevacionGrados: Float = ELEVACION_POR_DEFECTO,
            distanciaCm: Float = 0f,
            centro: Punto3D = Punto3D(0f, 0f, 0f)
        ): PuntoPlano {
            val g = Math.toRadians(giroGrados.toDouble())
            val e = Math.toRadians(elevacionGrados.toDouble().coerceIn(0.0, 89.9))
            val xr = punto.x * cos(g) - punto.y * sin(g)
            val yr = punto.x * sin(g) + punto.y * cos(g)
            val coseno30 = 0.8660254
            // El papel: a lo ancho la diagonal del suelo; a lo alto, el suelo escorzado por la
            // elevación y la vertical entera. Con 30° es exactamente la isométrica de antes.
            val x = (xr - yr) * coseno30
            val y = (xr + yr) * sin(e) - punto.z * cos(e) / coseno30
            if (distanciaCm <= 0f) return PuntoPlano(x.toFloat(), y.toFloat())
            // Perspectiva: lo que se aleja del ojo se achica. El ojo está a [distanciaCm] del
            // centro, mirando por la dirección de la vista; lo que cae más atrás se divide más.
            val cxr = centro.x * cos(g) - centro.y * sin(g)
            val cyr = centro.x * sin(g) + centro.y * cos(g)
            val cx = (cxr - cyr) * coseno30
            val cy = (cxr + cyr) * sin(e) - centro.z * cos(e) / coseno30
            val fondo = profundidad(punto, giroGrados, elevacionGrados) - profundidad(centro, giroGrados, elevacionGrados)
            val k = distanciaCm / (distanciaCm + fondo).coerceAtLeast(distanciaCm * 0.1)
            return PuntoPlano((cx + (x - cx) * k).toFloat(), (cy + (y - cy) * k).toFloat())
        }

        /** Lo lejos que cae un punto mirando desde donde se mira: cuanto más, más al fondo. */
        fun profundidad(
            punto: Punto3D,
            giroGrados: Float = GIRO_POR_DEFECTO,
            elevacionGrados: Float = ELEVACION_POR_DEFECTO
        ): Double {
            val g = Math.toRadians(giroGrados.toDouble())
            val e = Math.toRadians(elevacionGrados.toDouble().coerceIn(0.0, 89.9))
            val xr = punto.x * cos(g) - punto.y * sin(g)
            val yr = punto.x * sin(g) + punto.y * cos(g)
            // Hacia el fondo va la diagonal del suelo, aplastada por la elevación, y hacia abajo
            // la vertical: mirando desde arriba, lo alto queda más cerca del ojo.
            return (xr + yr) * cos(e) - punto.z * sin(e)
        }
    }
}
