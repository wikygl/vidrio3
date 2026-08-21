package crystal.crystal

import java.io.Serializable

data class Listado(
    var escala  : String,
    var uni     : String,
    var medi1   : Float,
    var medi2   : Float,
    var medi3   : Float,
    var canti   : Float,
    var piescua : Float,
    var precio  : Float,
    var costo   : Float,
    var producto: String,
    val peri    : Float,
    var metcua  : Float,
    var metli   : Float,
    var metcub  : Float,
    var color   : Int,
    var uri     : String,
    /**
     * Producto del inventario de Ventas al que corresponde esta línea, si se eligió del catálogo.
     * Es el vínculo que permite descontar stock al vender; queda null en presupuestos de taller,
     * que no mueven inventario.
     */
    var productoId: String? = null
) : Serializable {

    companion object {
        /**
         * FIJO Y NO SE CAMBIA. Los presupuestos se guardan con serialización Java
         * (`ObjectOutputStream`), que compara este número al leer: si no coincide con el del
         * archivo, la lectura falla con `InvalidClassException` y el presupuestos guardado deja de
         * abrirse.
         *
         * Cuando no se declara, Java lo calcula a partir de los campos y métodos de la clase, así
         * que **agregar un campo lo cambia** y rompe todo lo ya guardado. Este es el valor que
         * tenía la clase antes de sumarle [productoId]; al fijarlo, los archivos viejos se siguen
         * leyendo y el campo nuevo simplemente queda en null.
         *
         * Si en el futuro se agregan más campos, hay que dejar este número tal cual y darles
         * siempre un valor por defecto.
         */
        private const val serialVersionUID: Long = 5462438865809499361L
    }
}
