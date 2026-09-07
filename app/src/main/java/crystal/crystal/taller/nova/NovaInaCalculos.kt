package crystal.crystal.taller.nova

/**
 * Objeto con todos los cálculos específicos de Nova Inaparente
 * Lógica trasladada directamente de vidrio3/NovaIna.kt
 */
object NovaInaCalculos {

    // ==================== FUNCIONES DE FORMATO ====================
    fun df1(defo: Float): String = NovaCalculos.df1(defo)

    data class AlturasMochetas(
        val superior: Float,
        val inferior: Float? = null
    ) {
        fun lista(): List<Float> = listOfNotNull(superior, inferior).filter { it > 0f }
        fun listaConUbicacion(): List<AlturaMocheta> {
            return buildList {
                if (superior > 0f) add(AlturaMocheta(superior, inferior = false))
                val inf = inferior
                if (inf != null && inf > 0f) add(AlturaMocheta(inf, inferior = true))
            }
        }
    }

    data class AlturaMocheta(
        val valor: Float,
        val inferior: Boolean
    )

    fun alturasMochetasPorModelo(
        modelo: String,
        alto: Float,
        altoHoja: Float,
        alturaPuente: Float,
        mochetaInferior: Float = 0f,
        // APA descuenta del alto los perfiles de puente; INA no (el puente no le come altura a
        // la mocheta). Por eso el descuento es opcional y lo decide quien llama.
        descontarPuentes: Boolean = true
    ): AlturasMochetas {
        return if (modelo == "np") {
            // "Doble puente" tiene DOS puentes. En APA el espacio disponible para las mochetas
            // descuenta ambos (altoMocheta con 2*alturaPuente); en INA el disponible es el alto
            // libre completo. Tanto el reparto por defecto como la mocheta superior parten de ahí.
            //
            // Versión anterior (descontaba siempre, y en INA dejaba los vidrios 2.5 cortos):
            //     val disponible = NovaCalculos.altoMocheta(alto, altoHoja, 2f * alturaPuente)
            //         .coerceAtLeast(0f)
            val disponible = if (descontarPuentes) {
                NovaCalculos.altoMocheta(alto, altoHoja, 2f * alturaPuente).coerceAtLeast(0f)
            } else {
                (alto - altoHoja).coerceAtLeast(0f)
            }
            val mochetaDefault = disponible / 2f
            val inferior = if (mochetaInferior > 0f) {
                mochetaInferior.coerceAtMost(disponible)
            } else {
                mochetaDefault
            }
            val superior = if (mochetaInferior > 0f) {
                (disponible - inferior).coerceAtLeast(0f)
            } else {
                mochetaDefault
            }
            AlturasMochetas(superior = superior, inferior = inferior)
        } else {
            AlturasMochetas(superior = (alto - altoHoja).coerceAtLeast(0f))
        }
    }

    // ==================== FUNCIONES DE U ====================
    fun uFijos(ancho: Float, divisiones: Int, cruce: Float): Float =
        NovaCalculos.uFijos(ancho, divisiones, cruce, "ina")
    fun uParante(alto: Float, us: Float): Float {
        return alto - (2 * us)
    }
    fun uParante2(alto: Float, altoHoja: Float, us: Float): Float {
        return ((alto - altoHoja) - us) + 1.5f
    }
    fun uSuperior(ancho: Float): Float {
        return ancho
    }

    // ==================== FUNCIONES DE CRUCE ====================
    fun cruce(cruceExacto: Float, divisiones: Int): Float =
        NovaCalculos.calcularCruce(cruceExacto, divisiones)

    // ==================== FUNCIONES DE PUENTES ====================
    fun nPuentes(divisiones: Int): Int = NovaCalculos.nPuentes(divisiones)
    fun nPuentes(divisiones: Int, ancho: Float): Int = NovaCalculos.nPuentesEfectivos(ancho, divisiones)
    fun mPuentes(ancho: Float, divisiones: Int): Float = NovaCalculos.mPuentes1(ancho, divisiones, "ina")
    fun mPuentes2(ancho: Float, divisiones: Int): Float = NovaCalculos.mPuentes2(ancho, divisiones, "ina")

    // ==================== FUNCIONES DE RIELES ====================
    fun rieles(alto: Float, hoja: Float, ancho: Float, divisiones: Int): String {
        val mPuentesVal = mPuentes(ancho, divisiones)
        val mPuentes6 = df1(mPuentesVal - 0.06f).toFloat()
        val mPuentesRounded = df1(mPuentesVal).toFloat()
        val mPuentes2Val = df1(mPuentes2(ancho, divisiones)).toFloat()
        val ancho6 = df1(ancho - 0.06f).toFloat()
        val nPuentesVal = nPuentes(divisiones, ancho)

        return if (alto >= hoja) {
            if (divisiones != 14) {
                "${df1(mPuentes6)} = $nPuentesVal"
            } else {
                "${df1(mPuentesRounded)} = ${nPuentesVal - 1}\n" +
                        "${df1(mPuentes2Val)} = ${nPuentesVal - 2}"
            }
        } else {
            "${df1(ancho6)} = 1"
        }
    }
    /**
     * [filasPuente] = cuántas corridas horizontales de puente lleva la ventana: 1 en el remate
     * normal (una mocheta) y 2 en doble puente (mocheta arriba y abajo). Solo multiplica las
     * medidas horizontales; el parante vertical entre tramos (la línea de [alto]) es uno solo,
     * corre de piso a techo y no se duplica.
     *
     * Versión original (una sola corrida, sin [filasPuente]):
     *     divisiones 6..12 par -> "$mPuentes6 = $nPuentesVal" + "$alto = ${nPuentesVal - 1}"
     *     divisiones == 14     -> "$mPuentes6 = ${nPuentesVal - 1}" +
     *                             "$mPuentes2Val = ${nPuentesVal - 2}" +
     *                             "$alto = ${nPuentesVal - 1}"
     *     resto                -> "$mPuentes6 = $nPuentesVal"
     */
    fun puentes(alto: Float, ancho: Float, divisiones: Int, puentesExtra: Int = 0, filasPuente: Int = 1): String {
        val mPuentesVal = mPuentes(ancho, divisiones)
        val mPuentes6 = df1(mPuentesVal - 0.06f).toFloat()
        val mPuentes2Val = df1(mPuentes2(ancho, divisiones)).toFloat()
        val nPuentesVal = nPuentes(divisiones, ancho) + puentesExtra
        val filas = filasPuente.coerceAtLeast(1)

        return when {
            divisiones in 6..12 && divisiones % 2 == 0 -> {
                "${df1(mPuentes6)} = ${nPuentesVal * filas}\n" +
                        "${df1(alto)} = ${nPuentesVal - 1}"
            }
            divisiones == 14 -> {
                "${df1(mPuentes6)} = ${(nPuentesVal - 1) * filas}\n" +
                        "${df1(mPuentes2Val)} = ${(nPuentesVal - 2) * filas}\n" +
                        "${df1(alto)} = ${nPuentesVal - 1}"
            }
            else -> {
                "${df1(mPuentes6)} = ${nPuentesVal * filas}"
            }
        }
    }

    // ==================== FUNCIONES DE OTROS PERFILES ====================
    fun portafelpa(altoHoja: Float): Float = NovaCalculos.portafelpa(altoHoja)
    fun hache(ancho: Float, divisiones: Int, cruce: Float): Float =
        NovaCalculos.uFijos(ancho, divisiones, cruce, "ina")
    fun divDePortas(divisiones: Int, nCorredizas: Int): String {
        if (divisiones <= 1 || nCorredizas <= 0) return ""
        return NovaCalculos.cantidadPortafelpas(divisiones).toString()
    }

    // ==================== FUNCIONES DE CONSTANTES ====================
    fun altoHoja(alto: Float, hoja: Float): Float = NovaCalculos.altoHoja(alto, hoja)
    fun nFijos(divisiones: Int): Int = NovaCalculos.nFijos(divisiones)
    fun nCorredizas(divisiones: Int): Int = NovaCalculos.nCorredizas(divisiones)
    fun nFijos(ancho: Float, divisiones: Int): Int = NovaCalculos.nFijos(ancho, divisiones)
    fun nCorredizas(ancho: Float, divisiones: Int): Int = NovaCalculos.nCorredizas(ancho, divisiones)
    fun fijoUParante(divisiones: Int): Int {
        return when (divisiones) {
            2 -> 1
            in 1..Int.MAX_VALUE -> 2
            else -> 0
        }
    }
    fun divisiones(ancho: Float, divisManual: Int): Int {
        return if (divisManual == 0) {
            when {
                ancho <= 60 -> 1
                ancho in 60.0..120.0 -> 2
                ancho in 120.0..180.0 -> 3
                ancho in 180.0..240.0 -> 4
                ancho in 240.0..300.0 -> 5
                ancho in 300.0..360.0 -> 6
                ancho in 360.0..420.0 -> 7
                ancho in 420.0..480.0 -> 8
                ancho in 480.0..540.0 -> 9
                ancho in 540.0..600.0 -> 10
                ancho in 600.0..660.0 -> 11
                ancho in 660.0..720.0 -> 12
                ancho in 720.0..780.0 -> 13
                ancho in 780.0..840.0 -> 14
                ancho in 840.0..900.0 -> 15
                else -> divisManual
            }
        } else {
            divisManual
        }
    }

    // ==================== FUNCIONES DE VIDRIOS ====================
    fun vidrioFijo(ancho: Float, alto: Float, us: Float, divisiones: Int, cruce: Float, esCfc: Boolean = false): String {
        val holgura = if (us == 0f) 1f else 0.2f
        val uFijosVal = uFijos(ancho, divisiones, cruce)
        val altDes = df1(alto - (us + holgura)).toFloat()
        // ncfc: ancho de vidrio de fijos por COLINDANCIA (pared/parante 0.4, fijo 0.2, corrediza 0).
        if (esCfc) {
            val tramos = NovaCalculos.patronModulosNcfc(ancho, divisiones)
            return NovaCalculos.descuentosVidrioFijos(tramos).entries.joinToString("\n") { (desc, cant) ->
                "${df1(uFijosVal - desc)} x ${df1(altDes)} = $cant"
            }
        }
        val orden = NovaCalculos.ordenDivisConParantes(divisiones, ancho)
        val tramos = orden
            .split(";P;")
            .map { tramo -> tramo.filter { it == 'f' || it == 'c' } }
            .filter { it.isNotEmpty() }

        if (tramos.isEmpty()) {
            val nFijosVal = nFijos(divisiones)
            return "${df1(uFijosVal - 0.4f)} x ${df1(altDes)} = $nFijosVal"
        }

        data class FijoTramo(val tramo: Int, val pos: Int, var descuento: Float)
        val fijos = mutableListOf<FijoTramo>()

        tramos.forEachIndexed { idxTramo, sec ->
            val posFijos = sec.mapIndexedNotNull { idx, ch -> if (ch == 'f') idx else null }
            if (posFijos.isEmpty()) return@forEachIndexed

            // Regla franja/tramo 5: uno queda en uFijo y los otros descuentan 0.4.
            if (sec.length == 5) {
                val fijoCentral = posFijos[posFijos.size / 2]
                posFijos.forEach { pos ->
                    val descuento = if (pos == fijoCentral) 0f else 0.4f
                    fijos.add(FijoTramo(idxTramo, pos, descuento))
                }
            } else {
                posFijos.forEach { pos -> fijos.add(FijoTramo(idxTramo, pos, 0.4f)) }
            }
        }

        // Regla fPf: si dos fijos colindan separados por parante, ambos descuentan 0.2.
        for (i in 0 until tramos.lastIndex) {
            val izq = tramos[i]
            val der = tramos[i + 1]
            if (izq.lastOrNull() == 'f' && der.firstOrNull() == 'f') {
                val fijoIzq = fijos.lastOrNull { it.tramo == i && it.pos == (izq.length - 1) }
                val fijoDer = fijos.firstOrNull { it.tramo == (i + 1) && it.pos == 0 }
                if (fijoIzq != null) fijoIzq.descuento = 0.2f
                if (fijoDer != null) fijoDer.descuento = 0.2f
            }
        }

        val lineas = linkedMapOf<String, Int>()
        for (f in fijos) {
            val medida = (uFijosVal - f.descuento).coerceAtLeast(0f)
            val key = "${df1(medida)} x ${df1(altDes)}"
            lineas[key] = (lineas[key] ?: 0) + 1
        }
        return lineas.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }
    fun vidrioCorre(ancho: Float, altoHoja: Float, divisiones: Int, cruce: Float, esCfc: Boolean = false): String {
        val hacheVal = hache(ancho, divisiones, cruce)
        val anchoVidrio = df1(hacheVal - 1.4f).toFloat()
        val altoVidrio = df1(altoHoja - 3.5f).toFloat()
        val nCorredizasVal = if (esCfc) NovaCalculos.nCorredizas(divisiones, "ncfc") else nCorredizas(ancho, divisiones)
        return "${df1(anchoVidrio)} x ${df1(altoVidrio)} = $nCorredizasVal"
    }
    fun vidrioMocheta(
        ancho: Float,
        alto: Float,
        hoja: Float,
        altoHoja: Float,
        divisiones: Int,
        cruce: Float,
        modelo: String = "nn",
        alturaPuente: Float = 2.5f,
        mochetaInferior: Float = 0f,
        mochetaInvertidaIna: Boolean = false,
        remate: String = modelo
    ): String {
        // `remate` (nn/nr/np) decide el reparto de mochetas; `modelo` es la modulación.
        val nFijosVal = nFijos(ancho, divisiones)
        val nCorredizasVal = nCorredizas(ancho, divisiones)
        val uFijosVal = uFijos(ancho, divisiones, cruce)

        val alturasMochetas = alturasMochetasPorModelo(
            remate, alto, altoHoja, alturaPuente, mochetaInferior, descontarPuentes = false
        ).listaConUbicacion()
        if (alturasMochetas.isEmpty()) return ""

        // ncfc (un tramo en INA): una mocheta por cada CORRIDA de corredizas adyacentes; el ancho
        // es (nº corredizas de la corrida) × ancho de corrediza. Así cuenta tanto las que van sobre
        // una sola corrediza como las que van sobre dos (o más) juntas.
        if (modelo == "ncfc") {
            val nF = NovaCalculos.nFijos(divisiones, "ncfc")
            val nC = NovaCalculos.nCorredizas(divisiones, "ncfc")
            if (nC <= 0) return ""
            val anchoPorCorr = (ancho - (nF * uFijosVal)) / nC
            val runs = NovaCalculos.corridasCorredizasNcfc(ancho, divisiones)
            val lineas = linkedMapOf<String, Int>()
            for (alturaMocheta in alturasMochetas) {
                val altoV = altoVidrioMochetaIna(alto, altoHoja, alturaMocheta.valor, alturaMocheta.inferior, mochetaInvertidaIna)
                for (run in runs) {
                    val w = df1((run * anchoPorCorr - 0.6f).coerceAtLeast(0f))
                    val key = "${df1(altoV)} x $w"
                    lineas[key] = (lineas[key] ?: 0) + 1
                }
            }
            return lineas.entries.joinToString("\n") { "${it.key} = ${it.value}" }
        }

        val axnfxuf = df1(((ancho - (nFijosVal * uFijosVal))) - 0.6f).toFloat()
        val axnfxuf2 = df1(((ancho - (nFijosVal * uFijosVal)) / 2) - 0.6f).toFloat()
        val axnfxuf3 = df1(((ancho - (nFijosVal * uFijosVal)) / 3) - 0.6f).toFloat()
        val axnfxufn = df1(((ancho - (nFijosVal * uFijosVal)) / nCorredizasVal) - 0.6f).toFloat()

        if (divisiones > 1 && alto > hoja && divisiones % 2 != 0) {
            val orden = NovaCalculos.ordenDivisConParantes(divisiones, ancho)
            val tramos = orden
                .split(";P;")
                .map { tramo -> tramo.filter { it == 'f' || it == 'c' } }
                .filter { it.isNotEmpty() }
            if (tramos.isEmpty()) return ""

            val anchoPorDivision = ancho / divisiones
            val lineas = linkedMapOf<String, Int>()
            for (tramo in tramos) {
                val divTramo = tramo.length
                val fijosTramo = tramo.count { it == 'f' }
                val baseTramo = (anchoPorDivision * divTramo) - (fijosTramo * uFijosVal)
                val cantidadBase = if (divTramo == 5) 2 else 1
                val medida = if (divTramo == 5) {
                    (baseTramo / 2f) - 0.5f
                } else {
                    baseTramo - 0.5f
                }
                val medidaAncho = medida.coerceAtLeast(0f)
                for (alturaMocheta in alturasMochetas) {
                    val altoVidrioMocheta = altoVidrioMochetaIna(
                        alto = alto,
                        altoHoja = altoHoja,
                        alturaMocheta = alturaMocheta.valor,
                        mochetaInferior = alturaMocheta.inferior,
                        mochetaInvertidaIna = mochetaInvertidaIna
                    )
                    val key = "${df1(altoVidrioMocheta)} x ${df1(medidaAncho)}"
                    lineas[key] = (lineas[key] ?: 0) + cantidadBase
                }
            }
            return lineas.entries.joinToString("\n") { "${it.key} = ${it.value}" }
        }

        if (divisiones <= 1 || alto <= hoja) return ""

        val cantidadesPorAncho = when (divisiones) {
            4 -> listOf(axnfxuf to 1)
            8 -> listOf(axnfxuf2 to 2)
            12 -> listOf(axnfxuf3 to 3)
            14 -> listOf(axnfxuf2 to 1, axnfxuf to 4)
            else -> listOf(axnfxufn to nCorredizasVal)
        }

        val lineas = linkedMapOf<String, Int>()
        for (alturaMocheta in alturasMochetas) {
            val altoVidrioMocheta = altoVidrioMochetaIna(
                alto = alto,
                altoHoja = altoHoja,
                alturaMocheta = alturaMocheta.valor,
                mochetaInferior = alturaMocheta.inferior,
                mochetaInvertidaIna = mochetaInvertidaIna
            )
            for ((anchoMocheta, cantidadBase) in cantidadesPorAncho) {
                val key = "${df1(altoVidrioMocheta)} x ${df1(anchoMocheta)}"
                lineas[key] = (lineas[key] ?: 0) + cantidadBase
            }
        }
        return lineas.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    private fun altoVidrioMochetaIna(
        alto: Float,
        altoHoja: Float,
        alturaMocheta: Float,
        mochetaInferior: Boolean,
        mochetaInvertidaIna: Boolean
    ): Float {
        return if (mochetaInvertidaIna) {
            (alto - altoHoja - 0.5f).coerceAtLeast(0f)
        } else if (mochetaInferior) {
            (alturaMocheta - 0.5f).coerceAtLeast(0f)
        } else {
            (alturaMocheta + 1f).coerceAtLeast(0f)
        }
    }

    // ==================== FUNCIONES DE TEXTO U ====================
    fun calcularTextoU(
        ancho: Float,
        alto: Float,
        hoja: Float,
        us: Float,
        divisiones: Int,
        cruceExacto: Float,
        incluirUFijos: Boolean = true,
        esFullFijos: Boolean = false,
        esCfc: Boolean = false
    ): String {
        val cruce = cruce(cruceExacto, divisiones)
        val altoHojaVal = altoHoja(alto, hoja)

        val uFijosVal = df1(uFijos(ancho, divisiones, cruce)).toFloat()
        val uParanteVal = df1(uParante(alto, us)).toFloat()
        val uParante2Val = df1(uParante2(alto, altoHojaVal, us)).toFloat()
        val uSuperiorVal = df1(uSuperior(ancho)).toFloat()
        val nFijosVal = nFijos(divisiones)
        // ncfc: U parante solo en fijos al borde del tramo (pared/parante); 0 = no se muestra.
        val fijoUParanteVal = if (esCfc) NovaCalculos.uParanteFijosNcfc(ancho, divisiones) else fijoUParante(divisiones)
        val textoUFijos = if (!incluirUFijos) {
            ""
        } else if (esFullFijos) {
            // Full fijos en inaparente: la U de fijos es el ancho total, 2 piezas (arriba y abajo).
            "${df1(ancho)} = 2"
        } else if (esCfc) {
            // ncfc: 1 U por módulo fijo; fijos adyacentes suman su medida en un solo perfil.
            NovaCalculos.uFijosRunsNcfc(ancho, divisiones, uFijosVal)
        } else {
            NovaCalculos.textoUFijosColindantes(ancho, divisiones, uFijosVal)
                .ifBlank { "${df1(uFijosVal)} = $nFijosVal" }
        }

        fun unirLineas(vararg lineas: String): String {
            return lineas.filter { it.isNotBlank() }.joinToString("\n")
        }

        // ncfc: las corredizas que colindan con la pared llevan U parante de mocheta (uParante2).
        val lineaMochetaParanteCfc = if (esCfc) {
            val n = NovaCalculos.uParanteMochetaCorredizasNcfc(ancho, divisiones)
            if (n > 0) "${df1(uParante2Val)} = $n" else ""
        } else ""

        val resultadoU = if (alto > altoHojaVal && us != 0F) {
            when {
                divisiones == 2 -> {
                    unirLineas(
                        textoUFijos,
                        "${df1(uParanteVal)} = $fijoUParanteVal",
                        "${df1(uParante2Val)} = 1",
                        "${df1(uSuperiorVal)} = 1"
                    )
                }
                divisiones == 1 -> {
                    unirLineas(
                        if (incluirUFijos) "${df1(uFijosVal)} = 2" else "",
                        "${df1(uParanteVal)} = 2"
                    )
                }
                else -> {
                    unirLineas(
                        textoUFijos,
                        "${df1(uParanteVal)} = $fijoUParanteVal",
                        lineaMochetaParanteCfc,
                        "${df1(uSuperiorVal)} = 1"
                    )
                }
            }
        } else if (alto > altoHojaVal && us == 0F) {
            when {
                divisiones == 2 -> {
                    unirLineas(
                        textoUFijos,
                        "${df1(uSuperiorVal)} = 1"
                    )
                }
                divisiones == 1 -> if (incluirUFijos) "${df1(uFijosVal)} = 2" else ""
                else -> {
                    unirLineas(
                        textoUFijos,
                        "${df1(uSuperiorVal)} = 1"
                    )
                }
            }
        } else if (alto <= altoHojaVal && us != 0F) {
            when {
                divisiones == 2 -> {
                    unirLineas(
                        textoUFijos,
                        "${df1(uParanteVal)} = $fijoUParanteVal",
                        "${df1(uParante2Val)} = 1"
                    )
                }
                divisiones == 1 -> {
                    unirLineas(
                        if (incluirUFijos) "${df1(uFijosVal)} = 2" else "",
                        "${df1(uParanteVal)} = 2"
                    )
                }
                else -> {
                    unirLineas(
                        textoUFijos,
                        "${df1(uParanteVal)} = $fijoUParanteVal"
                    )
                }
            }
        } else {
            when {
                divisiones == 2 -> textoUFijos
                divisiones == 1 -> if (incluirUFijos) "${df1(uFijosVal)} = 2" else ""
                else -> textoUFijos
            }
        }
        // ncfc: la U parante puede dar 0 (fijo que no colinda con pared/parante); no mostrarla.
        return if (esCfc) {
            resultadoU.lines().filterNot { it.substringAfter("=", "").trim() == "0" }.joinToString("\n")
        } else resultadoU
    }

    // ==================== FUNCIÓN COMPLETA DE OTROS ALUMINIOS ====================
    fun calcularOtrosAluminios(ancho: Float, alto: Float, hoja: Float, divisiones: Int, cruceExacto: Float,
                               puentesExtra: Int = 0,
                               // Remate de mochetas: "np" (doble puente) lleva dos corridas de puente.
                               remate: String = "nn"
    ): OtrosAluminiosResult {
        val cruce = cruce(cruceExacto, divisiones)
        val altoHojaVal = altoHoja(alto, hoja)
        val nCorredizasVal = nCorredizas(ancho, divisiones)
        val nPuentesVal = nPuentes(divisiones, ancho)
        val uFijosVal = uFijos(ancho, divisiones, cruce)
        val hacheVal = hache(ancho, divisiones, cruce)
        val portafelpaVal = portafelpa(altoHojaVal)
        val divDePortasVal = divDePortas(divisiones, nCorredizasVal)

        val filasPuente = if (remate == "np") 2 else 1
        val textoPuentes = puentes(alto, ancho, divisiones, puentesExtra, filasPuente)
        val textoRieles = if (divisiones == 1) "" else rieles(alto, hoja, ancho, divisiones)
        val textoUFelpero = if (divisiones == 1) "" else rieles(alto, hoja, ancho, divisiones)
        val textoHache = "${df1(hacheVal)} = $nCorredizasVal"
        val textoAngTope = if (divisiones == 2) "${df1(altoHojaVal - 0.9f)} = 1" else ""
        val textoPortafelpa = "${df1(portafelpaVal)} = $divDePortasVal"

        return OtrosAluminiosResult(
            puentes = textoPuentes,
            rieles = textoRieles,
            uFelpero = textoUFelpero,
            hache = textoHache,
            angTope = textoAngTope,
            portafelpa = textoPortafelpa,
            mostrarAngTope = divisiones == 2,
            mostrarHache = divisiones != 1,
            mostrarUFelpero = divisiones != 1 && alto > hoja
        )
    }

    // ==================== FUNCIÓN COMPLETA DE VIDRIOS ====================
    fun calcularVidrios(
        ancho: Float,
        alto: Float,
        hoja: Float,
        us: Float,
        divisiones: Int,
        cruceExacto: Float,
        modelo: String = "nn",
        alturaPuente: Float = 2.5f,
        mochetaInferior: Float = 0f,
        mochetaInvertidaIna: Boolean = false,
        remate: String = modelo
    ): String {
        val cruce = cruce(cruceExacto, divisiones)
        val altoHojaVal = altoHoja(alto, hoja)

        val esCfc = modelo == "ncfc"
        val vidriosFijos = vidrioFijo(ancho, alto, us, divisiones, cruce, esCfc)
        val vidriosCorre = vidrioCorre(ancho, altoHojaVal, divisiones, cruce, esCfc)
        val vidriosMocheta = vidrioMocheta(
            ancho = ancho,
            alto = alto,
            hoja = hoja,
            altoHoja = altoHojaVal,
            divisiones = divisiones,
            cruce = cruce,
            modelo = modelo,
            alturaPuente = alturaPuente,
            mochetaInferior = mochetaInferior,
            mochetaInvertidaIna = mochetaInvertidaIna,
            remate = remate
        )

        return if (divisiones > 1) {
            if (alto > hoja && vidriosMocheta.isNotEmpty()) {
                "$vidriosFijos\n$vidriosCorre\n$vidriosMocheta"
            } else {
                "$vidriosFijos\n$vidriosCorre"
            }
        } else {
            vidriosFijos
        }
    }

    // ==================== DATA CLASS PARA RESULTADOS ====================
    data class OtrosAluminiosResult(
        val puentes: String,
        val rieles: String,
        val uFelpero: String,
        val hache: String,
        val angTope: String,
        val portafelpa: String,
        val mostrarAngTope: Boolean,
        val mostrarHache: Boolean,
        val mostrarUFelpero: Boolean
    )
}
