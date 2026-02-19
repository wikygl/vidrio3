package crystal.crystal.taller

/**
 * Agrupa los resultados de múltiples calculadoras por material/perfil.
 * Permite ver todos los perfiles del mismo tipo juntos, sin importar de qué calculadora vengan.
 */
object AgrupadorResultados {

    data class ItemAgrupado(
        val nombre: String,        // Ej: "Marco", "Riel", "Vidrios"
        val calculadora: String,   // De dónde viene
        val texto: String          // Medidas y cantidades
    )

    /**
     * Agrupa todos los perfiles por nombre.
     */
    fun porPerfil(resultados: List<ResultadoCalculo>): Map<String, List<ItemAgrupado>> {
        val items = mutableListOf<ItemAgrupado>()
        for (r in resultados) {
            for ((nombre, texto) in r.perfiles) {
                if (texto.isNotBlank()) {
                    items.add(ItemAgrupado(nombre, "${r.calculadora} (${r.ancho}x${r.alto})", texto))
                }
            }
        }
        return items.groupBy { it.nombre }
    }

    /**
     * Agrupa todos los vidrios.
     */
    fun vidrios(resultados: List<ResultadoCalculo>): List<ItemAgrupado> {
        return resultados
            .filter { it.vidrios.isNotBlank() }
            .map { ItemAgrupado("Vidrios", "${it.calculadora} (${it.ancho}x${it.alto})", it.vidrios) }
    }

    /**
     * Agrupa todos los accesorios por nombre.
     */
    fun porAccesorio(resultados: List<ResultadoCalculo>): Map<String, List<ItemAgrupado>> {
        val items = mutableListOf<ItemAgrupado>()
        for (r in resultados) {
            for ((nombre, texto) in r.accesorios) {
                if (texto.isNotBlank()) {
                    items.add(ItemAgrupado(nombre, "${r.calculadora} (${r.ancho}x${r.alto})", texto))
                }
            }
        }
        return items.groupBy { it.nombre }
    }

    // ==================== FORMATO POR MATERIAL ====================

    /**
     * Genera texto agrupado por material.
     * Formato:
     *   U-13 negro
     *   • 48.3 = 2 -> Vni1, Dora
     *   • 142 = 2 -> Vni1, Dora
     */
    fun generarTextoAgrupado(resultados: List<ResultadoCalculo>): String {
        val siglas = asignarSiglas(resultados)
        return buildString {
            // Perfiles agrupados por nombre+color
            val perfilesPorNombreColor = mutableMapOf<String, MutableList<String>>()
            for ((i, r) in resultados.withIndex()) {
                val sigla = siglas[i]
                for ((nombre, texto) in r.perfiles) {
                    if (texto.isBlank()) continue
                    val clave = if (r.colorAluminio.isNotBlank()) "$nombre ${r.colorAluminio}" else nombre
                    val lista = perfilesPorNombreColor.getOrPut(clave) { mutableListOf() }
                    for (linea in texto.split("\n")) {
                        val l = linea.trim()
                        if (l.isEmpty()) continue
                        lista.add("• $l -> $sigla, ${r.cliente}")
                    }
                }
            }

            if (perfilesPorNombreColor.isNotEmpty()) {
                for ((clave, lineas) in perfilesPorNombreColor) {
                    append("$clave\n")
                    for (linea in lineas) {
                        append("$linea\n")
                    }
                }
                append("\n")
            }

            // Vidrios agrupados por tipo
            val vidriosPorTipo = mutableMapOf<String, MutableList<String>>()
            for ((i, r) in resultados.withIndex()) {
                if (r.vidrios.isBlank()) continue
                val sigla = siglas[i]
                val tipoKey = if (r.tipoVidrio.isNotBlank()) "Vidrio ${r.tipoVidrio}" else "Vidrio"
                val lista = vidriosPorTipo.getOrPut(tipoKey) { mutableListOf() }
                for (linea in r.vidrios.split("\n")) {
                    val l = linea.trim()
                    if (l.isEmpty()) continue
                    lista.add("• $l -> $sigla, ${r.cliente}")
                }
            }

            if (vidriosPorTipo.isNotEmpty()) {
                for ((tipo, lineas) in vidriosPorTipo) {
                    append("$tipo\n")
                    for (linea in lineas) {
                        append("$linea\n")
                    }
                }
                append("\n")
            }

            // Accesorios
            val accPorNombre = mutableMapOf<String, MutableList<String>>()
            for ((i, r) in resultados.withIndex()) {
                val sigla = siglas[i]
                for ((nombre, texto) in r.accesorios) {
                    if (texto.isBlank()) continue
                    val lista = accPorNombre.getOrPut(nombre) { mutableListOf() }
                    for (linea in texto.split("\n")) {
                        val l = linea.trim()
                        if (l.isEmpty()) continue
                        lista.add("• $l -> $sigla, ${r.cliente}")
                    }
                }
            }

            if (accPorNombre.isNotEmpty()) {
                for ((nombre, lineas) in accPorNombre) {
                    append("$nombre\n")
                    for (linea in lineas) {
                        append("$linea\n")
                    }
                }
            }
        }
    }

    // ==================== FORMATO POR CLIENTE ====================

    /**
     * Genera texto agrupado por cliente.
     * Formato:
     *   Cliente: Dora
     *   Referencias
     *   Ventana Nova Inaparente 1 (Vni1) ...
     *   Aluminio
     *   U-13 negro
     *   • 48.3 = 2 → Vni1
     */
    fun generarTextoPorCliente(resultados: List<ResultadoCalculo>): String {
        val siglas = asignarSiglas(resultados)
        val porCliente = mutableMapOf<String, MutableList<Int>>()
        for ((i, r) in resultados.withIndex()) {
            val cliente = r.cliente.ifBlank { "Sin cliente" }
            porCliente.getOrPut(cliente) { mutableListOf() }.add(i)
        }

        return buildString {
            for ((cliente, indices) in porCliente) {
                append("Cliente: $cliente\n")
                append("Referencias\n")

                // Listar las referencias de cada producto
                for (idx in indices) {
                    val r = resultados[idx]
                    val sigla = siglas[idx]
                    append("${r.calculadora} ($sigla)\n")
                    append("Tipo: ${r.producto.ifBlank { r.calculadora.lowercase() }}\n")
                    append("Cliente: $cliente\n")
                    if (r.colorAluminio.isNotBlank()) append("Aluminio: ${r.colorAluminio}\n")
                    if (r.tipoVidrio.isNotBlank()) append("Vidrio: ${r.tipoVidrio}\n")
                    append("Ancho: ${r.ancho}\n")
                    append("Alto: ${r.alto}\n")
                    if (r.alturaPuente > 0) append("Altura de puente: ${r.alturaPuente}\n")
                    if (r.divisiones > 0) append("Divisiones: ${r.divisiones}\n")
                    if (r.fijos > 0) append("Fijos: ${r.fijos}\n")
                    if (r.corredizas > 0) append("Corredizas: ${r.corredizas}\n")
                    append("\n")
                }

                // Aluminio agrupado por perfil+color
                append("Aluminio\n")
                val perfilesPorClave = mutableMapOf<String, MutableList<String>>()
                for (idx in indices) {
                    val r = resultados[idx]
                    val sigla = siglas[idx]
                    for ((nombre, texto) in r.perfiles) {
                        if (texto.isBlank()) continue
                        val clave = if (r.colorAluminio.isNotBlank()) "$nombre ${r.colorAluminio}" else nombre
                        val lista = perfilesPorClave.getOrPut(clave) { mutableListOf() }
                        for (linea in texto.split("\n")) {
                            val l = linea.trim()
                            if (l.isEmpty()) continue
                            lista.add("• $l → $sigla")
                        }
                    }
                }
                for ((clave, lineas) in perfilesPorClave) {
                    append("$clave\n")
                    for (linea in lineas) append("$linea\n")
                }

                // Vidrio
                append("Vidrio\n")
                for (idx in indices) {
                    val r = resultados[idx]
                    if (r.vidrios.isBlank()) continue
                    val sigla = siglas[idx]
                    val tipoKey = if (r.tipoVidrio.isNotBlank()) "Vidrio ${r.tipoVidrio}" else "Vidrio"
                    append("$tipoKey\n")
                    for (linea in r.vidrios.split("\n")) {
                        val l = linea.trim()
                        if (l.isEmpty()) continue
                        append("$l → $sigla\n")
                    }
                }
                append("\n\n")
            }
        }
    }

    // ==================== FORMATO POR PRODUCTO ====================

    /**
     * Genera texto individual por producto.
     * Formato:
     *   Ventana Nova Aparente 1 (Vna1)
     *   Referencia
     *   Tipo: nova aparente
     *   Cliente: Dora
     *   Aluminio: bronce
     *   ...
     *   Aluminio
     *   U-3/8 bronce
     *   • 49 = 2
     *   Vidrio
     *   ...
     */
    fun generarTextoPorProducto(resultados: List<ResultadoCalculo>): String {
        val siglas = asignarSiglas(resultados)
        return buildString {
            for ((i, r) in resultados.withIndex()) {
                val sigla = siglas[i]
                append("${r.calculadora} ($sigla)\n")
                append("Referencia\n")
                append("Tipo: ${r.producto.ifBlank { r.calculadora.lowercase() }}\n")
                append("Cliente: ${r.cliente}\n")
                if (r.colorAluminio.isNotBlank()) append("Aluminio: ${r.colorAluminio}\n")
                if (r.tipoVidrio.isNotBlank()) append("Vidrio: ${r.tipoVidrio}\n")
                append("Ancho: ${r.ancho}\n")
                append("Alto: ${r.alto}\n")
                if (r.alturaPuente > 0) append("Altura de puente: ${r.alturaPuente}\n")
                if (r.divisiones > 0) append("Divisiones: ${r.divisiones}\n")
                if (r.fijos > 0) append("Fijos: ${r.fijos}\n")
                if (r.corredizas > 0) append("Corredizas: ${r.corredizas}\n")
                append("\n")

                // Aluminio
                if (r.perfiles.isNotEmpty()) {
                    append("Aluminio\n")
                    for ((nombre, texto) in r.perfiles) {
                        if (texto.isBlank()) continue
                        val clave = if (r.colorAluminio.isNotBlank()) "$nombre ${r.colorAluminio}" else nombre
                        append("$clave\n")
                        for (linea in texto.split("\n")) {
                            val l = linea.trim()
                            if (l.isEmpty()) continue
                            append("• $l\n")
                        }
                    }
                }

                // Vidrio
                if (r.vidrios.isNotBlank()) {
                    append("Vidrio\n")
                    val tipoKey = if (r.tipoVidrio.isNotBlank()) "Vidrio ${r.tipoVidrio}" else "Vidrio"
                    append("$tipoKey\n")
                    for (linea in r.vidrios.split("\n")) {
                        val l = linea.trim()
                        if (l.isEmpty()) continue
                        append("$l\n")
                    }
                }

                // Accesorios
                if (r.accesorios.isNotEmpty()) {
                    for ((nombre, texto) in r.accesorios) {
                        if (texto.isBlank()) continue
                        append("$nombre\n")
                        for (linea in texto.split("\n")) {
                            val l = linea.trim()
                            if (l.isEmpty()) continue
                            append("• $l\n")
                        }
                    }
                }

                append("Diseño\nImagen\n\n\n")
            }
        }
    }

    /**
     * Genera texto individual por calculadora (vista legacy "por item").
     */
    fun generarTextoPorItem(resultados: List<ResultadoCalculo>): String {
        return generarTextoPorProducto(resultados)
    }

    // ==================== HELPERS ====================

    /**
     * Asigna siglas secuenciales por tipo a cada resultado.
     * Ej: [Vna1, Vni1, Vna2, Pam1]
     */
    private fun asignarSiglas(resultados: List<ResultadoCalculo>): List<String> {
        val contadores = mutableMapOf<String, Int>()
        return resultados.map { r ->
            val base = if (r.sigla.isNotBlank()) r.sigla else siglaPorCalculadora(r.calculadora)
            val num = (contadores[base] ?: 0) + 1
            contadores[base] = num
            "$base$num"
        }
    }

    private fun siglaPorCalculadora(calculadora: String): String {
        val c = calculadora.lowercase()
        return when {
            c.contains("nova ina") -> "Vni"
            c.contains("nova apa") -> "Vna"
            c.contains("nova piv") -> "Vnp"
            "clásica" in c && "econ" in c -> "Vce"
            "clasica" in c && "econ" in c -> "Vce"
            "clásica" in c && "com" in c -> "Vcc"
            "clasica" in c && "com" in c -> "Vcc"
            c.contains("8425") -> "V84"
            c.contains("serie 20") -> "V20"
            c.contains("serie 25") -> "V25"
            c.contains("serie 80") -> "V80"
            "ventana" in c && "pvc" in c -> "Vvc"
            c.contains("ventana") -> "V"
            c.contains("puerta ducha") -> "PD"
            "puerta" in c && "mary" in c -> "Pam"
            "puerta" in c && "ade" in c -> "Paa"
            "puerta" in c && "tere" in c -> "Pat"
            "puerta" in c && "pvc" in c -> "Pvc"
            "puerta" in c && "vidrio" in c -> "Pv"
            c.contains("puerta") -> "Pa"
            "mampara" in c && "corrediza" in c -> "Mnc"
            "mampara" in c && "pivotante" in c -> "Mnp"
            c.contains("mampara fc") -> "Mce"
            "mampara" in c && "80" in c -> "M80"
            c.contains("mampara paf") -> "Mnp"
            c.contains("mampara vid") -> "Mvc"
            c.contains("mampara") -> "M"
            c.contains("baranda") -> "B"
            c.contains("muro") -> "MC"
            c.contains("pivot") -> "Vnp"
            c.contains("vitroven") -> "Evm"
            c.contains("espejo") -> "Es"
            c.contains("porton") || c.contains("portón") -> "Pos"
            c.contains("reja") -> "Ral"
            c.contains("drywall") -> "D"
            c.contains("celosía") || c.contains("celosia") -> "C"
            c.contains("cielo") -> "CR"
            c.contains("techo") -> "T"
            c.contains("melamina") -> "Me"
            else -> "X"
        }
    }
}
