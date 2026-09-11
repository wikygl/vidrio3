package crystal.crystal.Diseno.nova

import kotlin.math.abs

/**
 * Las piezas que aparecen SOLO porque el vano tiene escalones, y que hoy no cuenta nadie.
 *
 * Una ventana recta lleva una U arriba, una abajo y dos jambas. Cuando el alféizar sube en un
 * trozo del vano —lo normal en obra— aparecen dos cosas más:
 *
 * - el **alféizar se parte**: en vez de una U corrida de lado a lado, una por cada tramo, cada una
 *   a su altura;
 * - y en cada escalón hay una **jamba** vertical, del alto que sube el alféizar.
 *
 * Aquí solo está la geometría del vano: medidas de hueco, sin holguras ni descuentos de perfil,
 * que son los que cambian por sistema y por acabado. Mientras esas reglas no estén escritas, esto
 * no se suma a los materiales: sirve para verlas y para que el cálculo las use cuando se sepan.
 */
object PiezasDelEscalon {

    /** Una pieza del escalón: qué es y cuánto mide, en cm. */
    data class Pieza(val nombre: String, val medidaCm: Float)

    private const val TOLERANCIA = 0.15f

    /**
     * Las piezas del escalón de este diseño, de izquierda a derecha. Una ventana recta no devuelve
     * ninguna.
     */
    fun de(diseno: DisenoNova): List<Pieza> {
        if (!diseno.esEscalonada) return emptyList()
        val piezas = mutableListOf<Pieza>()
        diseno.tramos.indices.forEach { i ->
            piezas.add(Pieza("alféizar tramo ${i + 1}", diseno.tramos[i].ancho))
        }
        for (i in 0 until diseno.tramos.size - 1) {
            val salto = abs(diseno.altoDeTramo(i) - diseno.altoDeTramo(i + 1))
            if (salto > TOLERANCIA) {
                piezas.add(Pieza("jamba del escalón ${i + 1}-${i + 2}", salto))
            }
        }
        return piezas
    }

    /** Lo mismo, en una línea por pieza, para poder mirarlo en la pantalla de resultados. */
    fun texto(diseno: DisenoNova): String =
        de(diseno).joinToString("\n") { "${it.nombre}: ${crystal.crystal.taller.nova.NovaCalculos.df1(it.medidaCm)}" }
}
