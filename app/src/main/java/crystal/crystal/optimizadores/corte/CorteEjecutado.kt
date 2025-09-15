package crystal.crystal.optimizadores.corte

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Representa los cortes que se ejecutaron en la vida real
 */
@Parcelize
data class CorteEjecutado(
    val longitud: Float,
    val referencia: String,
    val cantidad: Int = 1 // Cuántas veces se cortó esta medida/referencia
) : Parcelable

/**
 * Representa una varilla que fue cortada en la vida real
 */
@Parcelize
data class VarillaCortada(
    val longitudVarilla: Float,
    val cortesEjecutados: List<CorteEjecutado>
) : Parcelable