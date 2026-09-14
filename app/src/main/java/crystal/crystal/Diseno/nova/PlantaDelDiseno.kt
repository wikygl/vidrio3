package crystal.crystal.Diseno.nova

import crystal.crystal.taller.ArcoEsquina
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/** Un punto de la planta, en centímetros. */
data class PuntoPlanta(val x: Float, val y: Float)

/**
 * Una pared de la ventana vista DESDE ARRIBA.
 *
 * [desde] y [hasta] son sus dos puntas en el suelo. En una pared recta la distancia entre ellas es
 * su ancho; en una curva es su CUERDA, porque el ancho de una curva es su desarrollo —lo que se
 * corta— y eso es más largo que lo que ocupa en el suelo.
 *
 * [giroGrados] es cuánto ha girado la ventana al llegar a esta pared: en una recta, lo que dobló
 * su esquina; en una curva, lo que dobla el arco de punta a punta.
 */
data class ParedEnPlanta(
    val indiceTramo: Int,
    val desde: PuntoPlanta,
    val hasta: PuntoPlanta,
    val anchoCm: Float,
    val altoCm: Float,
    val flechaCm: Float,
    val giroGrados: Float
) {
    val esCurva: Boolean get() = flechaCm > 0f

    /** De punta a punta por el suelo: el ancho en una pared recta, la cuerda en una curva. */
    val cuerdaCm: Float
        get() = kotlin.math.hypot(hasta.x - desde.x, hasta.y - desde.y)
}

/**
 * La ventana vista desde arriba, reconstruida desde su diseño.
 *
 * Es el perfil que hay que extruir para verla en tres dimensiones: cada pared sube su alto y se
 * convierte en una cara. Aquí no hay dibujo ni pantalla, solo centímetros, para poder comprobarla
 * en frío.
 *
 * Arranca en el origen yendo hacia la derecha (+x) y se hunde hacia +y, que es como se mira una
 * planta puesta debajo de su alzada.
 */
data class PlantaDelDiseno(val paredes: List<ParedEnPlanta>) {

    val vacia: Boolean get() = paredes.isEmpty()

    /** Lo que mide la ventana estirada: la suma de lo que se corta, curvas por su desarrollo. */
    val desarrolloCm: Float get() = paredes.sumOf { it.anchoCm.toDouble() }.toFloat()

    /** Las puntas del recorrido, en orden. */
    fun recorrido(): List<PuntoPlanta> {
        if (paredes.isEmpty()) return emptyList()
        return listOf(paredes.first().desde) + paredes.map { it.hasta }
    }

    companion object {
        /**
         * Arma la planta recorriendo los tramos del diseño.
         *
         * En cada pliegue la ventana gira lo que le FALTA al ángulo para seguir recta: `A<180>` no
         * dobla, `A<90>` dobla en escuadra. El signo dice hacia dónde —en menos, hacia afuera—.
         *
         * Una pared curva gira ella sola, a lo largo de su arco, así que el pliegue que viene
         * DETRÁS de una curva no cuenta: está en el paquete para que el dibujo sepa que el paño
         * siguiente va en perspectiva, pero el giro ya lo hizo la curva. Contándolo otra vez, la
         * ventana doblaba el doble en cada esquina redondeada.
         */
        /**
         * La panza de toda la ventana, buscada en el paquete tal cual.
         *
         * La calculadora curva NO la escribe como etiqueta suelta: la mete DENTRO de la franja de
         * sistema del primer tramo (`s<120>(fU<20>)`), donde el modelo no la ve porque ahí solo
         * lee módulos. Por eso hay que buscarla en el texto, como ya se hace con la silueta del
         * vano por la misma clase de motivo.
         */
        fun panzaDelPaquete(paquete: String?): Float =
            RE_ARCO_ENTERO.find(paquete.orEmpty())
                ?.groupValues?.get(1)?.replace(",", ".")?.toFloatOrNull()
                ?.coerceAtLeast(0f) ?: 0f

        private val RE_ARCO_ENTERO = Regex("""[uU]\s*<\s*([\d.,]+)\s*>""")

        fun de(diseno: DisenoNova, panzaDeLaVentana: Float = 0f): PlantaDelDiseno {

            val paredes = mutableListOf<ParedEnPlanta>()
            var x = 0f
            var y = 0f
            var rumbo = 0.0                       // radianes; 0 = hacia la derecha
            var veniaDeCurva = false
            val radioEntero = radioDeLaVentanaCurva(diseno, panzaDeLaVentana)

            diseno.tramos.forEachIndexed { i, tramo ->
                val grados = tramo.pliegue?.let { gradosDePliegue(it) }
                if (grados != null && !veniaDeCurva) {
                    val giro = 180f - abs(grados)
                    rumbo += Math.toRadians((if (grados < 0f) -giro else giro).toDouble())
                }
                val alto = if (tramo.alto > 0f) tramo.alto else diseno.alto
                val desde = PuntoPlanta(x, y)
                val giroDeLaPared: Float

                // La panza de este tramo: la suya, o la que le toca si la ventana entera es una
                // curva sola —la calculadora curva escribe UNA panza para toda la ventana, no una
                // por tramo, y sin esto sus tramos salían rectos y el volumen se veía plano—.
                val flecha = if (tramo.flecha > 0f) tramo.flecha
                else radioEntero?.let { r -> panzaDeUnTrozo(tramo.ancho, r) } ?: 0f

                if (flecha > 0f) {
                    // La curva: de su desarrollo y su panza sale cuánto gira y cuánta cuerda tiene.
                    // La cuerda sale a mitad de camino del giro —esa es la dirección de punta a
                    // punta de un arco—, y al final el rumbo queda girado el arco entero.
                    val arco = ArcoEsquina.deDesarrolloYFlecha(tramo.ancho, flecha)
                    val angulo = arco?.anguloGrados ?: 0f
                    val cuerda = arco?.cuerda ?: tramo.ancho
                    val mitad = Math.toRadians((angulo / 2f).toDouble())
                    x += (cos(rumbo + mitad) * cuerda).toFloat()
                    y += (sin(rumbo + mitad) * cuerda).toFloat()
                    rumbo += Math.toRadians(angulo.toDouble())
                    giroDeLaPared = angulo
                    veniaDeCurva = true
                } else {
                    x += (cos(rumbo) * tramo.ancho).toFloat()
                    y += (sin(rumbo) * tramo.ancho).toFloat()
                    giroDeLaPared = grados?.let { 180f - abs(it) } ?: 0f
                    veniaDeCurva = false
                }

                paredes.add(
                    ParedEnPlanta(
                        indiceTramo = i,
                        desde = desde,
                        hasta = PuntoPlanta(x, y),
                        anchoCm = tramo.ancho,
                        altoCm = alto,
                        flechaCm = flecha,
                        giroGrados = giroDeLaPared
                    )
                )
            }
            return PlantaDelDiseno(paredes)
        }

        /**
         * El radio de una ventana que es UNA curva entera, o null si no lo es.
         *
         * La calculadora curva escribe una sola panza para toda la ventana —el tag `U<…>`— en vez
         * de una por tramo. Sus tramos son trozos de ese mismo arco, así que del desarrollo entero
         * y de esa panza sale el radio, y de ahí lo que curva cada trozo.
         */
        private fun radioDeLaVentanaCurva(diseno: DisenoNova, panzaDeLaVentana: Float): Float? {
            if (diseno.tramos.any { it.flecha > 0f }) return null
            val flecha = if (panzaDeLaVentana > 0f) panzaDeLaVentana
            else diseno.etiquetas
                .firstOrNull { it.trim().startsWith("U<", ignoreCase = true) }
                ?.substringAfter("<")?.substringBefore(">")
                ?.trim()?.replace(",", ".")?.toFloatOrNull()
                ?: return null
            if (flecha <= 0.05f) return null
            val desarrollo = diseno.tramos.sumOf { it.ancho.toDouble() }.toFloat()
            return ArcoEsquina.deDesarrolloYFlecha(desarrollo, flecha)?.radio
        }

        /** Lo que curva un trozo de arco de [radio] que mide [desarrollo] estirado. */
        private fun panzaDeUnTrozo(desarrollo: Float, radio: Float): Float {
            if (radio <= 0.01f || desarrollo <= 0.01f) return 0f
            val angulo = desarrollo / radio
            return (radio * (1.0 - cos(angulo / 2.0))).toFloat()
        }

        /** Los grados de un pliegue `A<90>`; el menos delante es que dobla hacia afuera. */
        private fun gradosDePliegue(pliegue: String): Float? =
            pliegue.substringAfter("<", "").substringBefore(">", "")
                .trim().replace(",", ".").toFloatOrNull()
    }
}
