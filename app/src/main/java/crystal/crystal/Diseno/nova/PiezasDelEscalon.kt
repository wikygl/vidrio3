package crystal.crystal.Diseno.nova

import kotlin.math.abs

/**
 * Las piezas que aparecen SOLO porque el vano tiene escalones, y que hoy no cuenta nadie.
 *
 * Una ventana recta lleva una U arriba, una abajo y una a cada lado. Cuando el alféizar sube en un
 * trozo del vano —lo normal en obra— aparecen dos cosas más:
 *
 * - el **alféizar se parte**: en vez de una U corrida de lado a lado, una por cada tramo, cada una
 *   a su altura;
 * - y en cada escalón, una **U marco parante escalonado**: la U de marco que cierra en vertical el
 *   salto, del alto que sube el alféizar. Se llama así, y no jamba, porque jamba ya es el nombre
 *   de un perfil de marco en algunas series y confundiría; y lleva "escalonado" para no mezclarla
 *   con los parantes que parten los tramos.
 *
 * Aquí solo está la geometría del vano: medidas de hueco, sin holguras ni descuentos de perfil,
 * que son los que cambian por sistema y por acabado. Mientras esas reglas no estén escritas, esto
 * no se suma a los materiales: sirve para verlas y para que el cálculo las use cuando se sepan.
 */
object PiezasDelEscalon {

    /** Una pieza del escalón: qué es y cuánto mide, en cm. */
    data class Pieza(val nombre: String, val medidaCm: Float)

    /** El nombre del perfil vertical del salto, tal como lo nombra el vidriero. */
    const val PARANTE_ESCALONADO = "u marco parante escalonado"

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
        val saltos = diseno.tramos.indices.drop(1).count {
            abs(diseno.altoDeTramo(it - 1) - diseno.altoDeTramo(it)) > TOLERANCIA
        }
        for (i in 0 until diseno.tramos.size - 1) {
            val salto = abs(diseno.altoDeTramo(i) - diseno.altoDeTramo(i + 1))
            if (salto <= TOLERANCIA) continue
            // Con un solo escalón el nombre va solo; con varios se dice entre qué tramos está.
            val nombre = if (saltos > 1) "$PARANTE_ESCALONADO ${i + 1}-${i + 2}" else PARANTE_ESCALONADO
            piezas.add(Pieza(nombre, salto))
        }
        return piezas
    }

    /** Lo mismo, en una línea por pieza, para poder mirarlo en la pantalla de resultados. */
    fun texto(diseno: DisenoNova): String =
        de(diseno).joinToString("\n") {
            "${it.nombre}: ${crystal.crystal.taller.nova.NovaCalculos.df1(it.medidaCm)}"
        }
}
