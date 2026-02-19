package crystal.crystal.Diseno

import crystal.crystal.clientes.Cliente

/**
 * Representa un ítem completamente configurado en el flujo de diseño
 */
data class ItemDisenoConfiguracion(
    val numero: Int,
    val cliente: Cliente?,
    val categoria: String,
    val sistema: String,
    val tipoApertura: String,
    val geometria: String,
    val encuentros: String,
    val modelo: String,
    val acabado: String,
    val especificaciones: Map<String, String>,
    val accesorios: List<String>,
    val anchoProducto: Float,
    val altoProducto: Float,
    val alturaPuente: Float
) {
    fun getDescripcionCorta(): String {
        return "$categoria $numero - ${anchoProducto.toInt()}×${altoProducto.toInt()}cm"
    }

    fun getDescripcionCompleta(): String {
        val sb = StringBuilder()
        sb.append("$categoria $numero")
        if (cliente != null) {
            sb.append(" - ${cliente.nombreCompleto}")
        }
        sb.append("\n$sistema ${tipoApertura.uppercase()}, ${geometria}, ${encuentros}")
        sb.append("\n${anchoProducto.toInt()}×${altoProducto.toInt()}×${alturaPuente.toInt()}cm")
        return sb.toString()
    }
}
