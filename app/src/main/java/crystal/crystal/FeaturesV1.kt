package crystal.crystal

/**
 * Interruptores de versión. Poner cada flag en false vuelve a habilitar su función.
 */
object FeaturesV1 {
    // Crystal Ventas, Patrón y Terminales (POS multi-terminal). ABIERTO: los proveedores/ventas son
    // parte de la ventaja competitiva, así que se muestran junto al resto de la app.
    const val OCULTAR_VENTAS = false

    // Wallet / planes / recargas: se ABREN para poder cobrar FULL (suscripción del propio usuario).
    // Es un flag separado de OCULTAR_VENTAS para no destapar Ventas/Terminales.
    const val OCULTAR_WALLET = false

    // Taller: enviar un presupuesto a las calculadoras (enrutado por ítem) está incompleto en v1.
    // Bloqueado; el presupuesto entrante se guarda en filesDir/taller_pendientes para verlo después.
    const val BLOQUEAR_TALLER_PRESUPUESTO = true

    // Puertas: ABIERTO. Todos los modelos calculan sus materiales y se pueden archivar. Antes, los
    // que no estaban terminados dibujaban el diseño/plano pero mostraban "en desarrollo" en vez de
    // los resultados. Ponerlo en true vuelve a ese aviso, según PuertaRepositorio.estaTerminada.
    const val AVISO_PUERTAS_EN_DESARROLLO = false

    // Calculadoras no terminadas (todas menos Nova Corrediza, Puertas, Mampara Paflón, VentanaAl):
    // al entrar muestran un banner "🚧 En desarrollo" (descartable). Ver AvisoDesarrollo.
    const val AVISO_CALCULADORAS_EN_DESARROLLO = true
}
