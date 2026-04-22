package crystal.crystal.red.interop

data class MaterialsRequestItem(
    val descripcion: String = "",
    val ancho: Double? = null,
    val alto: Double? = null,
    val cantidad: Double? = null,
    val unidad: String = ""
)

data class MaterialsRequestPayload(
    val projectName: String = "",
    val notes: String = "",
    val items: List<MaterialsRequestItem> = emptyList()
)

data class CrystalBudgetPayload(
    val projectName: String = "",
    val customerName: String = "",
    val itemCount: Int = 0,
    val total: Double? = null,
    val currency: String = "PEN"
)

data class PuntosQuoteItem(
    val descripcion: String = "",
    val cantidad: Double? = null,
    val precioUnitario: Double? = null
)

data class PuntosQuotePayload(
    val quoteNumber: String = "",
    val projectName: String = "",
    val total: Double? = null,
    val currency: String = "PEN",
    val items: List<PuntosQuoteItem> = emptyList()
)
