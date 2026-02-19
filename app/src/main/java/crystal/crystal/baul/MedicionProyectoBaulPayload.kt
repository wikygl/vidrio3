package crystal.crystal.baul

import java.io.Serializable

data class MedicionProyectoBaulPayload(
    val version: Int = 1,
    val origen: String = "medida_activity",
    val nombreProyecto: String,
    val clienteNombre: String?,
    val itemsJson: String,
    val fechaGuardado: Long
) : Serializable

