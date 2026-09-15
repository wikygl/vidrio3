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
    var productoId: String? = null,
    /**
     * Las OPCIONES de este ítem: el mismo producto medido, ofrecido con otro material y a otro
     * precio —arenado laminado, policarbonato, serie 80—. Van como texto
     * (`producto|precio;producto|precio`); las lee y las escribe [crystal.crystal.pos.OpcionesDeProforma].
     *
     * En null, el ítem es lo que era: una línea con un producto y un precio. Con opciones, además,
     * puede salir en una proforma de elección, que no suma porque el cliente escoge una.
     */
    var opciones: String? = null,
    /**
     * El AMBIENTE donde va este ítem: la sala, el consultorio, el piso. En un trabajo de una obra
     * entera —una clínica, un hotel— la proforma se lee por ambientes, no como una lista corrida:
     * "sala de partos" con lo suyo, "sala sexto piso" con lo suyo.
     *
     * En null, el ítem no está en ningún ambiente y sale al final, sin encabezado.
     */
    var ambiente: String? = null
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
         * leyendo y el campo nuevo simplemente queda en null. Con [opciones] se hizo lo mismo: se
         * añadió con valor por defecto y este número NO se tocó.
         *
         * Si en el futuro se agregan más campos, hay que dejar este número tal cual y darles
         * siempre un valor por defecto.
         */
        private const val serialVersionUID: Long = 5462438865809499361L
    }
}
