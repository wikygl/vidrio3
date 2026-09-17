package crystal.crystal.taller

/**
 * Cómo va el trazo de una línea del apunte. Es lo que se guarda en `Element.Shape.trazo` y lo
 * que elige el diálogo "Estilo de línea"; el grosor va aparte, como múltiplo del normal.
 */
object EstiloDeLinea {
    const val CONTINUO = "continuo"
    const val PUNTEADO = "punteado"
    const val TRAZOS = "trazos"
    const val TRAZO_PUNTO = "trazo_punto"
}
