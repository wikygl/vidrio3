package crystal.crystal.taller.puerta.datos

import crystal.crystal.taller.puerta.modelos.Puerta

object PuertaRepositorio {
    // Orden pensado para v1: primero los TERMINADOS (Mari, Viky), luego los que están por salir.
    val listaPuertas = listOf(
        Puerta("Mari", "3", "8"),
        Puerta("Viky", "8", "8"),
        Puerta("Dora", "3.6", "8"),
        Puerta("Adel", "7", "8."),
        Puerta("Mili", "3.9", "8."),
        Puerta("jeny", "4.4", "8"),
        Puerta("Taly", "8", "8"),
        Puerta("Lina", "8", "8"),
        Puerta("Tere", "8", "8")
    )

    // Modelos TERMINADOS (cálculos listos). El resto muestra aviso "en desarrollo" al Calcular.
    private val terminadas = setOf("Mari", "Viky")

    fun estaTerminada(nombre: String): Boolean = nombre in terminadas
}