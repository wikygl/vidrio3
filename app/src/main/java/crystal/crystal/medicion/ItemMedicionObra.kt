package crystal.crystal.medicion

data class ItemMedicionObra(
    val id: String,
    val numero: Int,
    val clienteId: String?,
    val clienteNombre: String?,
    val categoria: String,
    val sistema: String,
    val tipoApertura: String,
    val geometria: String,
    val encuentros: String,
    val modelo: String,
    val acabado: String,
    val especificaciones: Map<String, String>,
    val accesorios: List<String>,
    val anchoCm: Float,
    val altoCm: Float,
    val alturaPuenteCm: Float,
    val unidadCaptura: UnidadMedida,
    val ubicacionObra: String?,
    val notasObra: String?,
    val evidencias: List<String>,
    val estado: EstadoMedicion,
    val pendienteSincronizar: Boolean,
    val medidoPor: String,
    val dispositivoId: String,
    val creadoEn: Long,
    val actualizadoEn: Long
) {
    fun descripcionCorta(): String {
        return "$categoria $numero - ${anchoCm.toInt()}x${altoCm.toInt()} cm"
    }
}

