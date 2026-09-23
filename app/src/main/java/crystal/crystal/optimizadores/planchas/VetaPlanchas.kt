package crystal.crystal.optimizadores.planchas

/**
 * Hacia dónde tiene que correr la veta de la melamina en cada pieza. Las piezas se escriben
 * siempre ancho x alto (en las que tienen fondo, el fondo hace de ancho): una puerta 37.9 x 223,
 * un lateral 57.9 x 230. La veta de la plancha corre a lo largo de su lado mayor (los 244 de una
 * de 244 x 183).
 *
 * - [LIBRE]: sin veta (vidrio, o melamina lisa): cada pieza se gira como mejor entre.
 * - [ALTO]: el alto de cada pieza a lo largo de la veta (lo común: puertas y laterales con la
 *   veta de arriba abajo).
 * - [ANCHO]: el ancho de cada pieza a lo largo de la veta.
 * Con veta, ninguna pieza se gira.
 */
enum class VetaPlanchas(val etiqueta: String, val descripcion: String) {
    LIBRE("Rotación libre", "Las piezas se giran para acomodar mejor (sin veta)"),
    ALTO("Veta a lo alto", "El alto de cada pieza va a lo largo de la plancha"),
    ANCHO("Veta a lo ancho", "El ancho de cada pieza va a lo largo de la plancha");

    companion object {
        fun desde(nombre: String?): VetaPlanchas = values().firstOrNull { it.name == nombre } ?: LIBRE

        /**
         * Las piezas y las planchas listas para el optimizador. Con veta, cada plancha se pone con
         * su lado mayor a lo ancho (ahí corre la veta) y cada pieza con el lado que va a lo largo
         * de la veta a lo ancho, sin permiso para girarse: a lo alto, se le cambian ancho y alto.
         */
        fun preparar(veta: VetaPlanchas, piezas: List<PiezaPlancha>, stock: List<PlanchaStock>): Pair<List<PiezaPlancha>, List<PlanchaStock>> =
            when (veta) {
                LIBRE -> piezas.map { it.copy(rotacionPermitida = true) } to stock
                ALTO -> piezas.map { it.copy(anchoMm = it.altoMm, altoMm = it.anchoMm, rotacionPermitida = false) } to stock.map(::aLoLargo)
                ANCHO -> piezas.map { it.copy(rotacionPermitida = false) } to stock.map(::aLoLargo)
            }

        /**
         * El resultado con cada pieza de vuelta en su ancho x alto: con la veta a lo alto van
         * todas giradas respecto de como se escribieron (se marcan, y la medida original se lee
         * girándolas), y las que no entraron vuelven como estaban.
         */
        fun devolver(veta: VetaPlanchas, r: ResultadoOptimizacionPlanchas): ResultadoOptimizacionPlanchas {
            if (veta != ALTO) return r
            return r.copy(
                planchas = r.planchas.map { p -> p.copy(cortes = p.cortes.map { it.copy(rotada = !it.rotada) }) },
                piezasSinUbicar = r.piezasSinUbicar.map { it.copy(anchoMm = it.altoMm, altoMm = it.anchoMm) }
            )
        }

        /** La plancha con su lado mayor a lo ancho, que es por donde corre la veta. */
        private fun aLoLargo(p: PlanchaStock): PlanchaStock =
            if (p.altoMm > p.anchoMm) p.copy(anchoMm = p.altoMm, altoMm = p.anchoMm) else p
    }
}
