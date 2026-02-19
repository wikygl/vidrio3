package crystal.crystal.medicion

import kotlin.math.roundToInt

object MedicionReglas {
    fun validarMedidas(
        categoria: String,
        anchoCm: Float,
        altoCm: Float,
        alturaPuenteCm: Float
    ): String? {
        if (anchoCm <= 0f || altoCm <= 0f || alturaPuenteCm <= 0f) {
            return "Las medidas deben ser mayores a 0"
        }
        if (anchoCm > 1200f || altoCm > 1200f || alturaPuenteCm > 1200f) {
            return "Medida fuera de rango permitido (max 1200 cm)"
        }
        when (categoria.lowercase()) {
            "ventana" -> {
                if (altoCm < 40f) return "Ventana: alto mínimo 40 cm"
                if (anchoCm < 40f) return "Ventana: ancho mínimo 40 cm"
            }
            "puerta" -> {
                if (altoCm < 160f) return "Puerta: alto mínimo 160 cm"
                if (anchoCm < 55f) return "Puerta: ancho mínimo 55 cm"
            }
            "mampara" -> {
                if (anchoCm < 60f) return "Mampara: ancho mínimo 60 cm"
                if (altoCm < 100f) return "Mampara: alto mínimo 100 cm"
            }
        }
        if (alturaPuenteCm > altoCm) {
            return "La altura de puente no puede ser mayor al alto"
        }
        return null
    }

    fun convertirACm(valor: Float, unidad: UnidadMedida): Float {
        return when (unidad) {
            UnidadMedida.CM -> valor
            UnidadMedida.MM -> valor / 10f
            UnidadMedida.PULG -> valor * 2.54f
        }
    }

    fun convertirDesdeCm(valorCm: Float, unidad: UnidadMedida): Float {
        return when (unidad) {
            UnidadMedida.CM -> valorCm
            UnidadMedida.MM -> valorCm * 10f
            UnidadMedida.PULG -> valorCm / 2.54f
        }
    }

    fun formatear(valor: Float): String {
        val redondeado = (valor * 10f).roundToInt() / 10f
        return if (redondeado % 1f == 0f) redondeado.toInt().toString() else redondeado.toString()
    }
}

