package crystal.crystal

import java.io.Serializable

data class PresupuestoCompleto(
    val cliente: String = "",
    val fechaCreacion: String = "",
    val elementos: List<Listado> = emptyList(),
    val precioTotal: String = "0.0",
    val metrosTotal: String = "0.0",
    val piesTotal: String = "0.0",
    val perimetroTotal: String = "0.0",
    val version: String = "1.0", // Para futuras compatibilidades
    val descripcion: String = "Presupuesto generado desde Crystal App"
) : Serializable