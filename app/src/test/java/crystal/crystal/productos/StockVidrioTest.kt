package crystal.crystal.productos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Modelo de stock de vidrio adoptado de Puntos: el inventario se lleva en planchas físicas Y en
 * área, porque de una plancha salen cortes. Vender por m²/p² tiene que descontar la fracción de
 * plancha que corresponde.
 */
class StockVidrioTest {

    /** Plancha estándar de 2.40 × 3.60 m. */
    private fun vidrio(stockP2: Float = 200f, planchas: Float = 5f) = Producto(
        id = "v1",
        nombre = "Templado 8mm",
        categoria = "Vidrio Templado",
        precioVenta = 50f,
        stock = stockP2,
        unidad = "p2",
        stockPlanchas = planchas,
        anchoPlancha = 240f,
        altoPlancha = 360f,
        planchasPorCaja = 5
    )

    private fun accesorio() = Producto(
        id = "a1",
        nombre = "Jalador",
        categoria = "Accesorios",
        precioVenta = 25f,
        stock = 10f
    )

    @Test
    fun elVidrioSeReconocePorSuCategoria() {
        assertTrue("templado es vidrio", vidrio().esVidrio())
        assertFalse("un accesorio no es vidrio", accesorio().esVidrio())
    }

    @Test
    fun elAreaDeLaPlanchaSaleDeSusMedidas() {
        // 2.40 m × 3.60 m = 8.64 m² → 8.64 × 11.1 = 95.9 p2
        assertEquals(95.9f, vidrio().areaPlanchaP2(), 0.2f)
    }

    @Test
    fun lasMedidasSeAceptanEnCentimetrosOEnMetros() {
        val enCm = vidrio()
        val enMetros = vidrio().copy(anchoPlancha = 2.4f, altoPlancha = 3.6f)
        assertEquals(
            "240 cm y 2.4 m deben dar la misma area",
            enCm.areaPlanchaP2(), enMetros.areaPlanchaP2(), 0.01f
        )
    }

    @Test
    fun vender50P2ConsumeAlgoMasDeMediaPlancha() {
        val p = vidrio()
        // 50 p2 sobre una plancha de 95.9 p2 = 0.52 planchas
        assertEquals(0.52f, p.planchasParaArea(50f), 0.01f)
    }

    @Test
    fun sinMedidasDePlanchaSoloSeDescuentaElArea() {
        val sinMedidas = vidrio().copy(anchoPlancha = 0f, altoPlancha = 0f)
        assertEquals(
            "sin medidas no se puede calcular planchas: debe dar 0, no romper",
            0f, sinMedidas.planchasParaArea(50f), 0.001f
        )
    }

    @Test
    fun lasCajasSalenDeLasPlanchasDisponibles() {
        assertEquals(1f, vidrio(planchas = 5f).cajasDisponibles(), 0.001f)
        assertEquals(0.4f, vidrio(planchas = 2f).cajasDisponibles(), 0.001f)
        assertEquals(
            "sin planchas por caja no se puede calcular",
            0f, vidrio().copy(planchasPorCaja = 0).cajasDisponibles(), 0.001f
        )
    }

    @Test
    fun elStockAdmiteDecimales() {
        // Con stock entero, vender 2.5 m2 era imposible de representar.
        val p = vidrio(stockP2 = 27.75f)
        assertTrue(p.tieneStock(27.75f))
        assertFalse(p.tieneStock(27.76f))
    }

    @Test
    fun elMovimientoGuardaLaDiferenciaNoElTotal() {
        // Es lo que permite sumar en el servidor lo que hicieron varias terminales sin señal.
        val mov = MovimientoInventario(
            productoId = "v1",
            productoNombre = "Templado 8mm",
            tipo = "VENTA",
            cantidad = -50f,
            cantidadPlanchas = -0.52f,
            stockAnterior = 200f,
            stockNuevo = 150f,
            stockPlanchasAnterior = 5f,
            stockPlanchasNuevo = 4.48f,
            referencia = "VENTA-B001-00000042"
        )
        assertTrue("una venta es salida", mov.esSalida())
        assertEquals(
            "la cantidad es la diferencia aplicada",
            mov.stockNuevo - mov.stockAnterior, mov.cantidad, 0.001f
        )
        // Dos terminales vendiendo sin señal: sus movimientos se suman sin importar el orden.
        val otra = mov.copy(id = "m2", cantidad = -30f)
        assertEquals(
            "200 - 50 - 30 = 120",
            120f, 200f + mov.cantidad + otra.cantidad, 0.001f
        )
    }
}
