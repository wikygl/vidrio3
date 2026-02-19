package crystal.crystal.taller

import java.io.Serializable

data class ResultadoCalculo(
    val calculadora: String,
    val producto: String,
    val cliente: String,
    val ancho: Float,
    val alto: Float,
    val perfiles: Map<String, String>,
    val vidrios: String,
    val accesorios: Map<String, String>,
    val referencias: String,
    val colorAluminio: String = "",
    val tipoVidrio: String = "",
    val sigla: String = "",
    val divisiones: Int = 0,
    val fijos: Int = 0,
    val corredizas: Int = 0,
    val alturaPuente: Float = 0f,
    val disenoPaquete: String = "",
    val cantidad: Int = 1
) : Serializable
