package crystal.crystal.taller

import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

/**
 * La curva de una ventana en esquina, con las tres medidas que se manejan en el taller.
 *
 * - **desarrollo**: lo que mide la pared estirada, que es lo que hay que cortar.
 * - **cuerda**: de punta a punta de la curva, en línea recta.
 * - **flecha**: lo que la curva se separa de esa línea por el medio.
 *
 * Con dos cualesquiera queda definida, y la tercera sale sola: son un arco de círculo. Se trabaja
 * con las tres porque en obra se mide lo que se puede —a veces la cuerda y la flecha con una regla,
 * a veces el desarrollo con la cinta pegada a la pared— y ninguna es más "la buena" que las otras.
 *
 * [anguloGrados] es cuánto dobla la pared de un extremo al otro, que es lo que la planta necesita
 * para dibujarla, y [radio] el de la curva.
 */
data class ArcoEsquina(
    val desarrollo: Float,
    val cuerda: Float,
    val flecha: Float,
    val radio: Float,
    val anguloGrados: Float
) {
    companion object {
        /** Por debajo de esto no hay curva que valga: es una pared recta. */
        private const val MINIMO = 0.05f

        /**
         * Cuerda y flecha: las dos que se miden con la regla contra la pared. Sale cerrado, sin
         * tanteos —el radio de un arco se despeja de ellas— y de ahí el resto.
         */
        fun deCuerdaYFlecha(cuerda: Float, flecha: Float): ArcoEsquina? {
            if (cuerda <= MINIMO || flecha <= MINIMO) return null
            val radio = (cuerda * cuerda / 4f + flecha * flecha) / (2f * flecha)
            if (radio <= MINIMO) return null
            // Con la flecha pasada de la mitad de la cuerda el arco se pasa de media vuelta, y ahí
            // el seno ya no distingue: el ángulo es el otro.
            val mitad = asin((cuerda / (2f * radio)).coerceIn(-1f, 1f))
            val medioAngulo = if (flecha > radio) Math.PI.toFloat() - mitad else mitad
            val angulo = 2f * medioAngulo
            return ArcoEsquina(radio * angulo, cuerda, flecha, radio, grados(angulo))
        }

        /** Desarrollo y flecha: el aluminio que se corta y la panza que hace. */
        fun deDesarrolloYFlecha(desarrollo: Float, flecha: Float): ArcoEsquina? {
            if (desarrollo <= MINIMO || flecha <= MINIMO) return null
            if (flecha >= desarrollo) return null
            // f/L = (1 - cos(a/2)) / a, que sube sin parar con el ángulo: se busca a tientas entre
            // nada y la vuelta entera, partiendo por la mitad cada vez.
            val objetivo = flecha / desarrollo
            val angulo = buscarAngulo(objetivo) { a -> (1f - cos(a / 2f)) / a } ?: return null
            val radio = desarrollo / angulo
            return ArcoEsquina(desarrollo, 2f * radio * sin(angulo / 2f), flecha, radio, grados(angulo))
        }

        /**
         * Desarrollo y radio: el trozo que le toca a una curva que ya se sabe cómo va.
         *
         * Es para partir una curva en trozos, o para estrenar un trozo nuevo detrás de otro: el
         * radio manda y de él sale la panza que le toca a ese pedazo. No es una medida de obra
         * —nadie mide el radio de una pared—, es la manera de seguir la misma curva.
         */
        fun deDesarrolloYRadio(desarrollo: Float, radio: Float): ArcoEsquina? {
            if (desarrollo <= MINIMO || radio <= MINIMO) return null
            val angulo = desarrollo / radio
            if (angulo <= MINIMO || angulo >= 2f * Math.PI.toFloat()) return null
            return ArcoEsquina(
                desarrollo = desarrollo,
                cuerda = 2f * radio * sin(angulo / 2f),
                flecha = radio * (1f - cos(angulo / 2f)),
                radio = radio,
                anguloGrados = grados(angulo)
            )
        }

        /** Desarrollo y cuerda: lo estirado y lo recto. */
        fun deDesarrolloYCuerda(desarrollo: Float, cuerda: Float): ArcoEsquina? {
            if (desarrollo <= MINIMO || cuerda <= MINIMO) return null
            if (cuerda >= desarrollo) return null
            // c/L = 2·sen(a/2) / a, que baja según crece el ángulo: se busca igual, al revés.
            val objetivo = cuerda / desarrollo
            val angulo = buscarAngulo(objetivo, creciente = false) { a -> 2f * sin(a / 2f) / a }
                ?: return null
            val radio = desarrollo / angulo
            return ArcoEsquina(desarrollo, cuerda, radio * (1f - cos(angulo / 2f)), radio, grados(angulo))
        }

        /**
         * El ángulo cuyo [relacion] da [objetivo], buscado partiendo por la mitad.
         *
         * Entre un pelo y la vuelta entera, que es donde vive un arco de ventana. Devuelve null si
         * el valor pedido cae fuera de lo que puede dar un arco.
         */
        private fun buscarAngulo(
            objetivo: Float,
            creciente: Boolean = true,
            relacion: (Float) -> Float
        ): Float? {
            var bajo = 0.0001f
            var alto = (2.0 * Math.PI).toFloat() - 0.0001f
            if (creciente) {
                if (objetivo <= relacion(bajo) || objetivo >= relacion(alto)) return null
            } else {
                if (objetivo >= relacion(bajo) || objetivo <= relacion(alto)) return null
            }
            repeat(80) {
                val medio = (bajo + alto) / 2f
                val valor = relacion(medio)
                val pasado = if (creciente) valor < objetivo else valor > objetivo
                if (pasado) bajo = medio else alto = medio
            }
            val angulo = (bajo + alto) / 2f
            return if (abs(relacion(angulo) - objetivo) < 0.0005f) angulo else null
        }

        private fun grados(radianes: Float): Float = (radianes * 180.0 / Math.PI).toFloat()
    }
}
