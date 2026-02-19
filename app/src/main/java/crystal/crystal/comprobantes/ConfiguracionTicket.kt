package crystal.crystal.comprobantes

import com.google.firebase.Timestamp

/**
 * Configuración personalizable del diseño del ticket
 * Almacenado en Firestore: usuarios/{uid}/empresa/disenoTicket
 */
data class ConfiguracionTicket(
    // Logo
    val mostrarLogo: Boolean = true,
    val logoUrl: String? = null,
    val tamanoLogo: TamanoLogo = TamanoLogo.MEDIANO,
    val posicionLogo: PosicionLogo = PosicionLogo.CENTRO,

    // Encabezado
    val mostrarNombreEmpresa: Boolean = true,
    val tamanoNombre: Int = 16,

    val mostrarRUC: Boolean = true,
    val mostrarDireccion: Boolean = true,
    val mostrarTelefono: Boolean = true,
    val mostrarEmail: Boolean = false,

    // Información de venta
    val formatoFechaHora: FormatoFechaHora = FormatoFechaHora.COMPLETO,
    val mostrarVendedor: Boolean = true,
    val mostrarTerminal: Boolean = false,

    // Items
    val mostrarCodigo: Boolean = false,
    val mostrarDimensiones: Boolean = true,

    // Totales
    val mostrarSubtotal: Boolean = true,
    val mostrarIGV: Boolean = true,
    val desglosarIGV: Boolean = true,
    val tamanoTotal: Int = 14,

    // Pie de página
    val mensajeDespedida: String = "¡Gracias por su compra!",
    val mostrarMensajeDespedida: Boolean = true,
    val textoPersonalizado: String? = null,
    val mostrarTextoPersonalizado: Boolean = false,

    // Dimensiones
    val anchoPapel: AnchoPapel = AnchoPapel.TERMICO_80MM,

    // Control
    val fechaCreacion: Timestamp = Timestamp.now(),
    val fechaActualizacion: Timestamp = Timestamp.now()
)

enum class TamanoLogo {
    PEQUENO,    // 40x40 mm
    MEDIANO,    // 60x60 mm
    GRANDE      // 80x80 mm
}

enum class PosicionLogo {
    IZQUIERDA,
    CENTRO,
    DERECHA
}

enum class FormatoFechaHora {
    SOLO_FECHA,           // 31/12/2025
    SOLO_HORA,            // 14:30
    COMPLETO,             // 31/12/2025 14:30
    COMPLETO_CON_SEGUNDOS // 31/12/2025 14:30:45
}

enum class AnchoPapel {
    TERMICO_58MM(58),
    TERMICO_80MM(80),
    A4(210);

    val anchoMilimetros: Int

    constructor(ancho: Int) {
        this.anchoMilimetros = ancho
    }
}

/**
 * Configuración por defecto
 */
object ConfiguracionTicketDefecto {
    fun obtener(): ConfiguracionTicket {
        return ConfiguracionTicket(
            mostrarLogo = true,
            tamanoLogo = TamanoLogo.MEDIANO,
            posicionLogo = PosicionLogo.CENTRO,
            mostrarNombreEmpresa = true,
            tamanoNombre = 16,
            mostrarRUC = true,
            mostrarDireccion = true,
            mostrarTelefono = true,
            mostrarEmail = false,
            formatoFechaHora = FormatoFechaHora.COMPLETO,
            mostrarVendedor = true,
            mostrarTerminal = false,
            mostrarCodigo = false,
            mostrarDimensiones = true,
            mostrarSubtotal = true,
            mostrarIGV = true,
            desglosarIGV = true,
            mensajeDespedida = "¡Gracias por su compra!",
            mostrarMensajeDespedida = true,
            anchoPapel = AnchoPapel.TERMICO_80MM
        )
    }
}

// Modelo de datos de empresa

data class DatosEmpresa(
    val ruc: String,
    val razonSocial: String,
    val nombreComercial: String? = null,
    val direccion: String,
    val telefono: String,
    val email: String? = null,
    val logoUrl: String? = null,
    val logoPath: String? = null  // ✅ NUEVO: Ruta local del logo
)

// Modelo de item de venta para tickets

data class ItemVenta(
    val codigo: String? = null,
    val nombre: String,
    val cantidad: Float,
    val unidad: String,
    val precio: Float,
    val ancho: Float? = null,
    val alto: Float? = null,
    val etiqueta: String? = null
) {
    val importe: Float
        get() = cantidad * precio
}