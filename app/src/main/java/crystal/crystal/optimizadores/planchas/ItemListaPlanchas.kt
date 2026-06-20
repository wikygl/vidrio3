package crystal.crystal.optimizadores.planchas

data class ItemListaPlanchas(
    val ancho: Float = 0f,
    val alto: Float = 0f,
    val info: String = "",
    val cantidad: Int = 1,
    var activo: Boolean = true
) {
    fun textoMostrar(formatter: PlanchaFormatter): String =
        "${formatter.df1(ancho)} x ${formatter.df1(alto)} = $cantidad ($info)"
}
