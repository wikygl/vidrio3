package crystal.crystal.Diseno

import crystal.crystal.R

object MaterialRepository {
    val materialesBase = listOf(
        R.drawable.ic_vitrobasic,
        R.drawable.ic_fichad3,
        R.drawable.pvicky
    )

    val materialesEspeciales = listOf(
        R.drawable.mpfijo,
        R.drawable.mpcorre,
        R.drawable.mpfijof
    )

    // Funciones para obtener diferentes listas
    fun obtenerMaterialesBase(): List<Int> {
        return materialesBase
    }

    fun obtenerMaterialesEspeciales(): List<Int> {
        return materialesEspeciales
    }

    // También puedes tener una función genérica
    fun obtenerMateriales(tipo: String): List<Int> {
        return when(tipo) {
            "base" -> materialesBase
            "especiales" -> materialesEspeciales
            else -> emptyList()
        }
    }
}

