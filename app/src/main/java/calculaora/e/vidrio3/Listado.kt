package calculaora.e.vidrio3

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
    var color   : Int
):Serializable