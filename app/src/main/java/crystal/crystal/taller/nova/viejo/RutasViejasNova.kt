package crystal.crystal.taller.nova.viejo

/*
 * Rutas viejas de cálculo de Nova corrediza, retiradas de NovaCorrediza.kt el 2026-09-30 (fase 4
 * del motor único, pedida por el usuario: "no quiero ningún motor aparte del que está en
 * desarrollo"). Desde entonces materiales, referencias y diseño salen de taller/nova/motor.
 *
 * Quedan aquí SOLO para leerlas y comparar si aparece un error: no se compilan ni se llaman. Para
 * volver a correrlas tal como estaban, la etiqueta de git `rutas-viejas-nova` (commit e020bc2).
 */

/*
    private fun lineaTieneValorCero(linea: String): Boolean {
        if (!linea.contains("=")) {
            val valorUnico = linea.replace(",", ".").toFloatOrNull()
            return valorUnico != null && kotlin.math.abs(valorUnico) < 0.0001f
        }
        val medida = linea.substringBefore("=").trim()
        val cantidad = linea.substringAfter("=", "").trim().toIntOrNull()
        if (cantidad == 0) return true

        val numeros = Regex("""-?\d+(?:[.,]\d+)?""").findAll(medida)
            .mapNotNull { it.value.replace(",", ".").toFloatOrNull() }
            .toList()
        return numeros.isNotEmpty() && numeros.any { kotlin.math.abs(it) < 0.0001f }
    }

    private fun combinarTextoCantidades(vararg textos: String): String {
        val orden = mutableListOf<String>()
        val cantidades = linkedMapOf<String, Int>()
        val literales = mutableListOf<String>()
        val patron = Regex("^(.+?)\\s*=\\s*(\\d+)\\s*$")

        for (texto in textos) {
            texto.lineSequence()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .forEach { linea ->
                    val match = patron.matchEntire(linea)
                    if (match == null) {
                        if (linea !in literales) literales.add(linea)
                    } else {
                        val medida = match.groupValues[1].trim()
                        val cantidad = match.groupValues[2].toIntOrNull() ?: 0
                        if (!cantidades.containsKey(medida)) orden.add(medida)
                        cantidades[medida] = (cantidades[medida] ?: 0) + cantidad
                    }
                }
        }

        val lineas = mutableListOf<String>()
        orden.forEach { medida -> lineas.add("$medida = ${cantidades[medida] ?: 0}") }
        lineas.addAll(literales)
        return lineas.joinToString("\n")
    }

    private fun filtrarLineasValorCero(texto: String): String {
        return texto.lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() && !lineaTieneValorCero(it) }
            .joinToString("\n")
    }

    private fun usarFcComoUFelpero(alto: Float, altoHoja: Float): Boolean {
        return !esPuenteMultiple() && altoHoja < alto && texto != "nr"
    }

    private fun escalarCantidadesTexto(texto: String, factor: Int): String {
        if (factor <= 1 || texto.isBlank()) return texto
        val patron = Regex("^(.*=\\s*)(\\d+)\\s*$")
        return texto.lines().joinToString("\n") { linea ->
            val m = patron.matchEntire(linea.trim())
            if (m != null) {
                val prefijo = m.groupValues[1]
                val cantidad = m.groupValues[2].toIntOrNull() ?: return@joinToString linea
                "$prefijo${cantidad * factor}"
            } else {
                linea
            }
        }
    }

    private fun combinarReferenciasOriginalesNl(vararg referencias: String): String {
        return referencias
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString("\n")
    }

    private fun capturarCamposCalculo(): EstadoCamposCalculo = EstadoCamposCalculo(
        ancho = binding.etAncho.text?.toString().orEmpty(),
        alto = binding.etAlto.text?.toString().orEmpty(),
        hoja = binding.etHoja.text?.toString().orEmpty(),
        divis = binding.etPartes.text?.toString().orEmpty()

    private fun restaurarCamposCalculo(estado: EstadoCamposCalculo) {
        binding.etAncho.setText(estado.ancho)
        binding.etAlto.setText(estado.alto)
        binding.etHoja.setText(estado.hoja)
        binding.etPartes.setText(estado.divis)
    }

    private fun aplicarMedidaEnCampos(medida: MedidaNl) {
        binding.etAncho.setText(df1(medida.ancho))
        binding.etAlto.setText(df1(medida.alto))
        binding.etHoja.setText(df1(medida.hoja))
        binding.etPartes.setText(medida.divisManual.toString())
    }

    private fun capturarMateriales(): EstadoMateriales = EstadoMateriales(
        u = binding.txU.text?.toString().orEmpty(),
        p = binding.txP.text?.toString().orEmpty(),
        r = binding.txR.text?.toString().orEmpty(),
        fc = binding.txFc.text?.toString().orEmpty(),
        pf = binding.txPf.text?.toString().orEmpty(),
        te = binding.txTe.text?.toString().orEmpty(),
        tope = binding.txTo.text?.toString().orEmpty(),
        hache = binding.txH.text?.toString().orEmpty(),
        tubo = binding.txT.text?.toString().orEmpty(),
        vidrio = binding.txV.text?.toString().orEmpty(),
        referencias = binding.txReferencias.text?.toString().orEmpty()

    private fun combinarMateriales(a: EstadoMateriales, b: EstadoMateriales): EstadoMateriales = EstadoMateriales(
        u = combinarTextoCantidades(a.u, b.u),
        p = combinarTextoCantidades(a.p, b.p),
        r = combinarTextoCantidades(a.r, b.r),
        fc = combinarTextoCantidades(a.fc, b.fc),
        pf = combinarTextoCantidades(a.pf, b.pf),
        te = combinarTextoCantidades(a.te, b.te),
        tope = combinarTextoCantidades(a.tope, b.tope),
        hache = combinarTextoCantidades(a.hache, b.hache),
        tubo = combinarTextoCantidades(a.tubo, b.tubo),
        vidrio = combinarTextoCantidades(a.vidrio, b.vidrio),
        referencias = listOf(a.referencias, b.referencias)
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString("\n")

    private fun filtrarMaterialesValorCero(estado: EstadoMateriales): EstadoMateriales = estado.copy(
        u = filtrarLineasValorCero(estado.u),
        p = filtrarLineasValorCero(estado.p),
        r = filtrarLineasValorCero(estado.r),
        fc = filtrarLineasValorCero(estado.fc),
        pf = filtrarLineasValorCero(estado.pf),
        te = filtrarLineasValorCero(estado.te),
        tope = filtrarLineasValorCero(estado.tope),
        hache = filtrarLineasValorCero(estado.hache),
        tubo = filtrarLineasValorCero(estado.tubo),
        vidrio = filtrarLineasValorCero(estado.vidrio)

    private fun mostrarFijoCorre(textoResultado: String, etiquetaResId: Int = R.string.fijo_corre) {
        val textoLimpio = filtrarLineasValorCero(textoResultado)
        binding.txFc.text = textoLimpio
        binding.tvFc.setText(etiquetaResId)
        binding.fcLayout.visibility = if (textoLimpio.isNotBlank()) View.VISIBLE else View.GONE
    }

    private fun ocultarFijoCorre() {
        binding.txFc.text = ""
        binding.fcLayout.visibility = View.GONE
    }

    private fun leerEntradasCalculo(): EntradasCalculo {
        val ancho = binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f
        val alto = binding.etAlto.text?.toString()?.toFloatOrNull() ?: 0f
        val hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
        val divisManual = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
        return EntradasCalculo(ancho, alto, hoja, divisManual, cruce())
    }

    private fun tipoCalculoPrincipal(): String = if (tipoNova == TipoNova.APA) "apa" else "ina"

    /**
     * Corridas horizontales de puente: 1 en el remate normal y 2 en doble puente (una bajo la
     * mocheta superior y otra sobre la inferior). Se multiplica con [escalarCantidadesTexto].
     */
    private fun filasDePuente(): Int = if (esModeloDoblePuente()) 2 else 1

    private fun esPuenteMultipleOGorrito(): Boolean {
        val p = puente.lowercase().trim()
        return p.contains("multi") || p.contains("múlt") || p.contains("múlt") || p.contains("gorrito") || p.contains("ltiple")
    }

    private fun mostrarFijoCorredizo(alto: Float, altoHoja: Float): Boolean {
        return (altoHoja >= alto) || (modeloRemate == "nr") || usarFcComoUFelpero(alto, altoHoja)
    }

    private fun esModeloInvertidoApa(): Boolean {
        return tipoNova == TipoNova.APA && modeloRemate == "nr"
    }

    private fun esModeloInvertidoIna(): Boolean {
        return tipoNova == TipoNova.INA && modeloRemate == "nr"
    }

    private fun textoUMochetaDoblePuente(alturasMochetas: List<Float>, us: Float, cantidadBase: Int): String {
        if (alturasMochetas.isEmpty() || cantidadBase <= 0) return ""
        val lineas = linkedMapOf<String, Int>()
        for (altura in alturasMochetas) {
            val key = NovaCalculos.df1(altura - (2f * us))
            lineas[key] = (lineas[key] ?: 0) + cantidadBase
        }
        return lineas.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    private fun mostrarUFelperoIna(alto: Float, altoHoja: Float): Boolean {
        return esPuenteMultiple() && altoHoja < alto
    }

    private fun textoFijoCorreInaInvertido(ancho: Float): String {
        return "${NovaCalculos.df1(ancho)} = 1"
    }

    /**
     * Suma [cantidad] a la línea cuya medida coincide con [medida] ("medida = N");
     * si no existe esa línea, añade una nueva. Usado para el falso puente con perfil
     * elegido (se suma a la cantidad del puente mostrado, mismo perfil y medida = ancho).
     */
    private fun agregarPuenteFalso(texto: String, medida: String, cantidad: Int): String {
        if (cantidad <= 0) return texto
        val lineas = texto.lines().toMutableList()
        val patron = Regex("^\\s*${Regex.escape(medida)}\\s*=\\s*(\\d+)\\s*$")
        for (i in lineas.indices) {
            val m = patron.matchEntire(lineas[i])
            if (m != null) {
                val n = (m.groupValues[1].toIntOrNull() ?: 0) + cantidad
                lineas[i] = "$medida = $n"
                return lineas.joinToString("\n")
            }
        }
        val base = if (texto.isBlank()) "" else "$texto\n"
        return "$base$medida = $cantidad"
    }

    private fun construirTagAleta(
        lado: String,
        anchoAleta: Float,
        disenoAleta: String

    private fun insertarAletaEnPrimerSistema(
        disenoBase: String,
        tagsAleta: List<String>
    ): String {
        if (tagsAleta.isEmpty()) return disenoBase
        val idx = disenoBase.indexOf(':')
        if (idx <= 0 || idx >= disenoBase.lastIndex) return disenoBase
        val cabecera = disenoBase.substring(0, idx)
        val cuerpo = disenoBase.substring(idx + 1)
        val patronSistema = Regex("s(?:<[^>]*>)?\\([^)]*\\)", RegexOption.IGNORE_CASE)
        val match = patronSistema.find(cuerpo) ?: return disenoBase
        val segmento = match.value
        val posCierre = segmento.lastIndexOf(')')
        if (posCierre <= 0) return disenoBase
        val tags = tagsAleta.joinToString("")
        val segmentoNuevo = segmento.substring(0, posCierre) + tags + segmento.substring(posCierre)
        val cuerpoNuevo = cuerpo.replaceRange(match.range, segmentoNuevo)
        return "$cabecera:$cuerpoNuevo"
    }

    private fun insertarTagSimpleEnPrimerSistema(
        disenoBase: String,
        tag: String
    ): String {
        if (tag.isBlank()) return disenoBase
        val idx = disenoBase.indexOf(':')
        if (idx <= 0 || idx >= disenoBase.lastIndex) return disenoBase
        val cabecera = disenoBase.substring(0, idx)
        val cuerpo = disenoBase.substring(idx + 1)
        val cuerpoNuevo = conTagEnElSistema(cuerpo, tag)
        if (cuerpoNuevo == cuerpo) return disenoBase
        return "$cabecera:$cuerpoNuevo"
    }

    private fun calcularMaterialesNl() {
        val primera = obtenerPrimeraMedidaNlEditable() ?: run {
            uTexto()
            otrosAluminios()
            vidriosTexto()
            referencias()
            return
        }
        val camposLado2 = capturarCamposCalculo()
        val lado2Medida = MedidaNl(
            ancho = camposLado2.ancho.toFloatOrNull() ?: 0f,
            alto = camposLado2.alto.toFloatOrNull() ?: 0f,
            hoja = camposLado2.hoja.toFloatOrNull() ?: 0f,
            divisManual = camposLado2.divis.toIntOrNull() ?: 0
        )
        val (primeraCalculo, lado2Calculo) = medidasCalculoNlApa(primera, lado2Medida)

        // Los campos llevan el ancho ÚTIL (con el descuento de esquina) para que los conteos y el
        // corte coincidan con el dibujo; la medida ORIGINAL solo se muestra en la referencia.
        aplicarMedidaEnCampos(conDivisionesDeLaMedidaReal(lado2Medida, lado2Calculo))
        uTexto()
        otrosAluminios()
        vidriosTexto()
        referencias(anchoRealMostrar = lado2Medida.ancho)
        val lado2 = capturarMateriales()

        aplicarMedidaEnCampos(conDivisionesDeLaMedidaReal(primera, primeraCalculo))
        uTexto()
        otrosAluminios()
        vidriosTexto()
        referencias(anchoRealMostrar = primera.ancho)
        val lado1 = capturarMateriales()

        val referenciasLado2 = lado2.referencias
        val referenciasLado1 = lado1.referencias

        restaurarCamposCalculo(camposLado2)
        val combinados = combinarMateriales(lado1, lado2)
        val altoParanteEsquina = maxOf(primera.alto, lado2Medida.alto)
        val combinadosConEsquina = combinados.copy(
            tubo = if (opcionEsquinero() != null) combinarTextoCantidades(
                combinados.tubo,
                "${df1(altoParanteEsquina)} = 1"
            ) else combinados.tubo,
            referencias = combinarReferenciasOriginalesNl(referenciasLado1, referenciasLado2)
        )
        val combinadosFiltrados = filtrarMaterialesValorCero(combinadosConEsquina)
        materialesNlArchivables = combinadosFiltrados
        aplicarMateriales(combinadosFiltrados)
    }

    private fun calcularMaterialesNu() {
        val ladoIzq = obtenerPrimeraMedidaNuEditable()
        val ladoCentro = obtenerSegundaMedidaNuEditable()
        if (ladoIzq == null || ladoCentro == null) {
            materialesNlArchivables = null
            uTexto()
            otrosAluminios()
            vidriosTexto()
            referencias()
            return
        }

        val camposActuales = capturarCamposCalculo()
        val ladoDer = MedidaNl(
            ancho = camposActuales.ancho.toFloatOrNull() ?: 0f,
            alto = camposActuales.alto.toFloatOrNull() ?: 0f,
            hoja = camposActuales.hoja.toFloatOrNull() ?: 0f,
            divisManual = camposActuales.divis.toIntOrNull() ?: 0
        )

        // Descuentos de esquina (solo aplican en APA). En C hay dos esquinas:
        // izquierda↔centro y centro↔derecha, por lo que el centro descuenta ambas.
        val (izqCalculo, centroEsquinaIzq) = medidasCalculoNlApa(ladoIzq, ladoCentro)
        val (centroEsquinaDer, derCalculo) = medidasCalculoNlApa(ladoCentro, ladoDer)
        val descuentoCentro = (ladoCentro.ancho - centroEsquinaIzq.ancho) +
            (ladoCentro.ancho - centroEsquinaDer.ancho)
        val centroCalculo = ladoCentro.copy(
            ancho = (ladoCentro.ancho - descuentoCentro).coerceAtLeast(0f)
        )

        // Los campos llevan el ancho ÚTIL (con los descuentos de esquina) para que los conteos y
        // el corte coincidan con el dibujo; la medida ORIGINAL solo se muestra en la referencia.
        aplicarMedidaEnCampos(conDivisionesDeLaMedidaReal(ladoDer, derCalculo))
        uTexto()
        otrosAluminios()
        vidriosTexto()
        referencias(anchoRealMostrar = ladoDer.ancho)
        val matDer = capturarMateriales()

        aplicarMedidaEnCampos(conDivisionesDeLaMedidaReal(ladoCentro, centroCalculo))
        uTexto()
        otrosAluminios()
        vidriosTexto()
        referencias(anchoRealMostrar = ladoCentro.ancho)
        val matCentro = capturarMateriales()

        aplicarMedidaEnCampos(conDivisionesDeLaMedidaReal(ladoIzq, izqCalculo))
        uTexto()
        otrosAluminios()
        vidriosTexto()
        referencias(anchoRealMostrar = ladoIzq.ancho)
        val matIzq = capturarMateriales()

        val referenciasDer = matDer.referencias
        val referenciasCentro = matCentro.referencias
        val referenciasIzq = matIzq.referencias

        restaurarCamposCalculo(camposActuales)

        val combinados = combinarMateriales(combinarMateriales(matIzq, matCentro), matDer)
        // Dos parantes de esquina (una por cada esquina de la C).
        val altoParanteEsquinaIzq = maxOf(ladoIzq.alto, ladoCentro.alto)
        val altoParanteEsquinaDer = maxOf(ladoCentro.alto, ladoDer.alto)
        val combinadosConEsquina = combinados.copy(
            tubo = if (opcionEsquinero() != null) combinarTextoCantidades(
                combinados.tubo,
                "${df1(altoParanteEsquinaIzq)} = 1",
                "${df1(altoParanteEsquinaDer)} = 1"
            ) else combinados.tubo,
            referencias = combinarReferenciasOriginalesNl(
                referenciasIzq, referenciasCentro, referenciasDer
            )
        )
        val combinadosFiltrados = filtrarMaterialesValorCero(combinadosConEsquina)
        materialesNlArchivables = combinadosFiltrados
        aplicarMateriales(combinadosFiltrados)
    }

    @SuppressLint("SetTextI18n")
    private fun calcularDesigual() {
        val ancho = ancho()
        val alto = alto()
        val altoHoja = if (altoHojaDesigual > 0f) altoHojaDesigual else altoHoja()
        val us = binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f
        val tipoCalculo = when (tipoNova) {
            TipoNova.APA -> "apa"
            TipoNova.INA -> "ina"
            TipoNova.PIV -> "piv"
        }
        val cruce = when (tipoNova) {
            TipoNova.APA -> {
                val exacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
                if (exacto == 0f) 0.7f else exacto
            }
            else -> {
                val exacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
                NovaInaCalculos.cruce(exacto, modulosDesiguales.size)
            }
        }

        actualizarTxPrConDisenoV2()

        // U perfiles
        binding.tvU.text = NovaPerfilesHelper.obtenerEtiquetaU(us)
        binding.txU.text = NovaCalculosDesiguales.calcularU(
            modulosDesiguales,
            parantesDesiguales,
            ancho,
            alto,
            altoHoja,
            us,
            cruce,
            tipoCalculo,
            tubo,
            puente,
            incluirUFijos = !esModeloInvertidoIna()
        )

          // Puentes y Rieles
          val pr = NovaCalculosDesiguales.calcularPuentesYRieles(
              modulosDesiguales, parantesDesiguales, ancho, alto, altoHoja, tipoCalculo
          )
          val textoTramosDesigual = if (tipoNova == TipoNova.APA && alto > altoHoja) {
              NovaCalculosDesiguales.textoUAnchoMochetaPorTramos(
                  modulosDesiguales, parantesDesiguales, ancho, tipoCalculo, tubo, puente
              )
          } else ""
          val textoTramosUnitDesigual = if (tipoNova == TipoNova.APA && alto > altoHoja) {
              NovaCalculosDesiguales.textoTramosUnitario(
                  modulosDesiguales, parantesDesiguales, ancho, tipoCalculo, tubo, puente
              )
          } else ""
          if (textoTramosDesigual.isNotBlank()) {
              binding.txP.text = textoTramosUnitDesigual
              binding.txR.text = textoTramosUnitDesigual
              binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else View.VISIBLE
              binding.lyRiel.visibility = View.VISIBLE
          } else {
              binding.txP.text = pr.puentes
              binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else if (modulosDesiguales.size > 1) View.VISIBLE else View.GONE
              binding.txR.text = pr.rieles
              binding.lyRiel.visibility = if (modulosDesiguales.count { it.tipo == 'c' } > 0) View.VISIBLE else View.GONE
          }

          val mostrarFcDesigual = mostrarFijoCorredizo(alto, altoHoja)
          if (mostrarFcDesigual) {
              val textoFcDesigual = if (textoTramosDesigual.isNotBlank()) textoTramosUnitDesigual else pr.rieles
              mostrarFijoCorre(textoFcDesigual)
          } else {
              ocultarFijoCorre()
          }

        // Vidrios
        binding.txV.text = NovaCalculosDesiguales.calcularVidrios(
            modulosDesiguales, mochetaDesigual, parantesDesiguales,
            ancho, alto, altoHoja, us, cruce, tipoCalculo, tubo, texto,
            mochetaInferior = mochetaInferiorDoblePuente(),
            mochetaInvertidaIna = esModeloInvertidoIna(),
            puente = puente
        )

        // Otros perfiles
        val otros = NovaCalculosDesiguales.calcularOtros(
            modulosDesiguales, parantesDesiguales, ancho, alto, altoHoja, us, cruce, tipoCalculo, tubo, puente, texto,
            mochetaInferior = mochetaInferiorDoblePuente(),
            mocheta = mochetaDesigual
        )
        binding.txPf.text = otros.portafelpa
        binding.lyPf.visibility = if (otros.portafelpa.isNotBlank()) View.VISIBLE else View.GONE
        binding.txH.text = otros.hache
        binding.lyH.visibility = if (otros.hache.isNotBlank()) View.VISIBLE else View.GONE
        binding.txTo.text = otros.tope
        binding.lyTo.visibility = if (otros.tope.isNotBlank()) View.VISIBLE else View.GONE
          val nParantesDes = parantesDesiguales.size
          val textoParantesDes = if (nParantesDes > 0) "${df1(alto)} = $nParantesDes" else ""
          binding.txT.text = textoParantesDes
          binding.lyTubo.visibility = if (textoParantesDes.isNotBlank()) View.VISIBLE else View.GONE

        if (tipoNova == TipoNova.APA) {
            binding.txTe.text = otros.tee
            binding.tLayout.visibility = if (otros.tee.isNotBlank()) View.VISIBLE else View.GONE
            binding.lyUf.visibility = View.GONE
        } else {
            val textoTramosUnitInaDesigual = if (tipoNova == TipoNova.INA && alto > altoHoja) {
                NovaCalculosDesiguales.textoTramosUnitario(
                    modulosDesiguales, parantesDesiguales, ancho, tipoCalculo, tubo, puente
                )
            } else {
                ""
            }
            if (textoTramosUnitInaDesigual.isNotBlank()) {
                binding.txP.text = textoTramosUnitInaDesigual
                binding.txR.text = textoTramosUnitInaDesigual
                binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else View.VISIBLE
                binding.lyRiel.visibility = View.VISIBLE

                val mostrarFcIna = mostrarFijoCorredizo(alto, altoHoja)
                if (mostrarFcIna) {
                    val mostrarUfEnFc = mostrarUFelperoIna(alto, altoHoja) || usarFcComoUFelpero(alto, altoHoja)
                    mostrarFijoCorre(
                        textoTramosUnitInaDesigual,
                        if (mostrarUfEnFc) R.string.u_felpero else R.string.fijo_corre
                    )
                } else {
                    ocultarFijoCorre()
                }
            }
            if (esModeloInvertidoIna()) {
                val textoFcInvertido = textoFijoCorreInaInvertido(ancho)
                mostrarFijoCorre(textoFcInvertido)
            }
            binding.txUf.text = ""
            binding.lyUf.visibility = View.GONE
            binding.tLayout.visibility = View.GONE
            val nParantesDiseno = NovaCalculos.nParantesDiseno(ancho, modulosDesiguales.size)
            val textoParantes = if (nParantesDiseno > 0) "${NovaCalculos.df1(alto)} = $nParantesDiseno" else ""
            binding.txT.text = textoParantes
            binding.lyTubo.visibility = if (textoParantes.isNotBlank()) View.VISIBLE else View.GONE
            binding.txTe.text = ""
        }

        // Referencias
        val nFijos = modulosDesiguales.count { it.tipo == 'f' }
        val nCorredizas = modulosDesiguales.count { it.tipo == 'c' }
        binding.txReferencias.text = NovaUIHelper.generarReferencias(
            ancho, alto, altoHoja, modulosDesiguales.size, nFijos, nCorredizas,
            if (alto > altoHoja) 1 else 0,
            modelo = texto,
            alturaPuente = tubo,
            mochetaInferior = mochetaInferiorDoblePuente(),
            // El remate sale del diseño: "polis" es invertido aunque la pantalla diga normal.
            remate = remateDelDiseno() ?: modeloRemate,
            // N-31: la mocheta es la misma en aparente e inaparente (antes solo aparente descontaba).
            descontarPuentes = true
        )

        // Diseño visual: usar ultimoPaquete (no regenerar)
        if (ultimoPaquete.isNotBlank()) {
            val intent = Intent(this, DisenoNovaActivity::class.java).apply {
                putExtra(DisenoNovaActivity.EXTRA_PAQUETE, ultimoPaquete)
                putExtra(DisenoNovaActivity.EXTRA_HEADLESS, true)
                putExtra(DisenoNovaActivity.EXTRA_OUTPUT_FORMAT, if (texto == "nl" || texto == "nu" || texto == "ns" || texto == "ncu" || texto == "nci" || ultimoPaquete.contains("P<2.5>") || modulosDesiguales.isNotEmpty()) "png" else "svg")
                putExtra(DisenoNovaActivity.EXTRA_RET_PADDING_PX, 4)
                putExtra(DisenoNovaActivity.EXTRA_MOCHETA_LATERAL_CM, mochetaLateralDisenoNl())
                putExtra(DisenoNovaActivity.EXTRA_ENCUENTRO_VACIO, metaEncuentro)
                putExtra(DisenoNovaActivity.EXTRA_DIRECCION, metaDireccion)
                putExtra(DisenoNovaActivity.EXTRA_US_CM, binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f)
            }
            lanzarDiseno.launch(intent)
            binding.textView28.setText(R.string.referencias_y_c_lculos)
        }
    }

    // ==================== U TEXTO ====================
    @SuppressLint("SetTextI18n")
    private fun uTexto() {
        val entrada = leerEntradasCalculo()
        val alto = entrada.alto - descuentoAltoVertical()
        val hoja = entrada.hoja
        val us = binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f
        val divisManual = entrada.divisManual
        val cruce = entrada.cruce

        // Arco de la curva: lo que se reparte/cuenta/corta. Si hay cuerda + flecha se
        // calcula con esos (más exacto); si no, el 'ancho' introducido ya es el arco.
        val ancho = arcoCurvo(entrada.ancho) - descuentoAnchoLateralApa()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val mochetaInferior = mochetaInferiorDoblePuente()

        if (tipoNova == TipoNova.APA) {
            // === APARENTE ===
            val nFijos = when {
                textoModelo == "nff" -> divisiones
                // Full corredizas: no hay fijos → U de fijos = 0.
                textoModelo == "nfc" -> 0
                // ncfc: cantidad real de fijos del patrón invertido (no la del clásico).
                textoModelo == "ncfc" -> NovaCalculos.nFijos(divisiones, "ncfc")
                else -> NovaCalculos.nFijos(ancho, divisiones)
            }
            // Full fijos con 2 divisiones: sin corredizas no hay tope; esa posición se reemplaza
            // con U, por lo que la U parante son 2 (no 1 como en la lógica de corrediza).
            val fijoUParante = when {
                textoModelo == "nff" && divisiones == 2 -> 2
                // Full corredizas: no hay fijos → no hay U parante de fijo.
                textoModelo == "nfc" -> 0
                // ncfc: U parante solo por lados del fijo que colindan con pared o parante.
                textoModelo == "ncfc" -> NovaCalculos.uParanteFijosNcfc(ancho, divisiones)
                divisiones == 2 -> 1
                else -> NovaCalculos.fijoUParante(divisiones, ancho)
            }
            val alturasDoblePuente = if (esModeloDoblePuente()) {
                NovaInaCalculos.alturasMochetasPorModelo(
                    modelo = modeloRemate,
                    alto = alto,
                    altoHoja = altoHoja,
                    alturaPuente = tubo,
                    mochetaInferior = mochetaInferior
                ).lista()
            } else {
                emptyList()
            }
            val factorMochetas = if (esModeloDoblePuente()) alturasDoblePuente.size.coerceAtLeast(1) else 1
            val mochetaUParanteBase = NovaCalculos.mochetaUParante(divisiones, ancho)
            val mochetaUParante = mochetaUParanteBase * factorMochetas
            val nPuentes = NovaCalculos.nPuentesEfectivos(ancho, divisiones)
            val cruceModelo = if (textoModelo == "nff") 0f else cruce
            val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruceModelo, "apa", valorParanteUFijosApa())
            // Full fijos: la U de fijos horizontal es el MARCO continuo del tramo (arriba y abajo),
            // no se reparte entre paños ni descuenta el parante interno; solo descuenta el puente
            // entre tramos. Por eso vale el ancho completo del tramo (ej. ancho 120 con 2 paños →
            // 120, no 117.5). Son 2 piezas por tramo (una arriba y otra abajo).
            val nTramosNff = nPuentes.coerceAtLeast(1)
            val uFijosTexto = if (textoModelo == "nff") (ancho - (nTramosNff - 1) * 2.5f) / nTramosNff else uFijos
            val nFijosTextoU = when {
                textoModelo == "nff" -> 2 * nTramosNff
                // ncfc: 1 U por módulo fijo (la línea se reemplaza luego por corridas de fijos).
                else -> nFijos
            }
            val uParante = altoHoja - (2*us)
            val altoMocheta = if (esModeloDoblePuente()) {
                alturasDoblePuente.firstOrNull() ?: 0f
            } else {
                NovaCalculos.altoMocheta(alto, altoHoja, tubo)
            }
              val uMocheta = altoMocheta - (2f * us)
            val mPuentes1 = NovaCalculos.mPuentes1(ancho, divisiones, "apa")
            val mPuentes2 = NovaCalculos.mPuentes2(ancho, divisiones, "apa")

            binding.tvU.text = NovaPerfilesHelper.obtenerEtiquetaU(us)
              // Se usa "nn" para conservar exactamente el comportamiento previo de esta pantalla.
              val textoBaseU = NovaPerfilesHelper.generarTextoU(
                  texto = "nn",
                  alto = alto,
                  hoja = altoHoja,
                  us = us,
                divisiones = divisiones,
                uFijos = uFijosTexto,
                uParante = uParante,
                uMocheta = uMocheta,
                  uSuperior = mPuentes1,
                  uSuperior2 = mPuentes2,
                nFijos = nFijosTextoU,
                fijoUParante = fijoUParante,
                mochetaUParante = mochetaUParante,
                  nPuentes = nPuentes,
                  puente = puente
              )

              val textoUMochetaTramosBase = if (alto > altoHoja && us != 0f) {
                  NovaCalculos.textoUMochetaPorTramosAparente(ancho, divisiones, puente, tubo, texto)
              } else {
                  ""
              }
              val textoUMochetaTramosEscalado = escalarCantidadesTexto(textoUMochetaTramosBase, factorMochetas)
              // Full fijos: la U de la franja sistema tiene la MISMA medida por tramo que la
              // mocheta y se SUMA a esa línea (la línea propia de U de fijos se filtra por
              // coincidir con mPuentes1). El Múltiple incorpora su U en la línea del puente, así
              // que la franja aporta solo 1/tramo con Múltiple y 2/tramo en lo demás (gorrito
              // incluido). Con la mocheta (1/tramo en Múltiple y gorrito; 2/tramo en lo demás)
              // el total por tramo queda: Múltiple 2, gorrito 3, otros 4.
              val textoUMochetaTramos = if (textoModelo == "nff" && textoUMochetaTramosBase.isNotBlank()) {
                  val franjaPorTramo = if (esPuenteMultiple()) 1 else 2
                  val franjaTramos = escalarCantidadesTexto(
                      NovaCalculos.textoTramosUnitarioAparente(ancho, divisiones, puente, tubo, texto), franjaPorTramo
                  )
                  combinarTextoCantidades(textoUMochetaTramosEscalado, franjaTramos)
              } else {
                  textoUMochetaTramosEscalado
              }

              // Este filtro quita la línea de U de fijos cuando su valor coincide con el puente
              // (mPuentes1/2), porque CON mocheta esa U se reincorpora fundida en la línea de
              // mocheta por tramos. SIN mocheta no hay nada que la reincorpore, así que filtrarla
              // la haría desaparecer (dejaría solo el alto). Por eso solo se filtra si hay mocheta.
              val hayMocheta = alto > altoHoja
              val patronUSuperior1 = Regex("^${Regex.escape(NovaCalculos.df1(mPuentes1))}\\s*=\\s*\\d+\\s*$")
              val patronUSuperior2 = Regex("^${Regex.escape(NovaCalculos.df1(mPuentes2))}\\s*=\\s*\\d+\\s*$")
              val baseSinAnchoIgual = textoBaseU
                  .lineSequence()
                  .map { it.trimEnd() }
                  .filter {
                      it.isNotBlank() &&
                              (!hayMocheta ||
                                      (!patronUSuperior1.matches(it.trim()) &&
                                              !patronUSuperior2.matches(it.trim())))
                  }
                  .toMutableList()

              if (textoUMochetaTramos.isNotBlank()) {
                  val idxParante = baseSinAnchoIgual.indexOfFirst { it == "${NovaCalculos.df1(uParante)} = $fijoUParante" }
                  val lineasMocheta = textoUMochetaTramos.lines().filter { it.isNotBlank() }
                  if (idxParante >= 0) {
                      baseSinAnchoIgual.addAll(idxParante + 1, lineasMocheta)
                  } else {
                      baseSinAnchoIgual.addAll(lineasMocheta)
                  }
              }
              val textoUMochetasDoble = if (esModeloDoblePuente()) {
                  textoUMochetaDoblePuente(alturasDoblePuente, us, mochetaUParanteBase)
              } else {
                  ""
              }
              if (textoUMochetasDoble.isNotBlank() && textoUMochetasDoble.lines().size > 1) {
                  val patronUMocheta = Regex("^${Regex.escape(NovaCalculos.df1(uMocheta))}\\s*=\\s*\\d+\\s*$")
                  val idxUMocheta = baseSinAnchoIgual.indexOfFirst { patronUMocheta.matches(it.trim()) }
                  val lineasUMocheta = textoUMochetasDoble.lines().filter { it.isNotBlank() }
                  if (idxUMocheta >= 0) {
                      baseSinAnchoIgual.removeAt(idxUMocheta)
                      baseSinAnchoIgual.addAll(idxUMocheta, lineasUMocheta)
                  } else {
                      baseSinAnchoIgual.addAll(lineasUMocheta)
                  }
              }
              var textoFinalU = baseSinAnchoIgual.joinToString("\n")
              // Full fijos con un solo paño: la U ya sale correcta desde la base como dos medidas
              // (ancho = 2 y alto = 2, con su descuento). Esta corrección es del modelo clásico "nn"
              // (aplasta la U a una sola línea uFijos = 4); aplicarla a "nff" borraría el alto y
              // dejaría solo "U fijos". Por eso se excluye full fijos.
              if (divisiones == 1 && textoModelo != "nff") {
                  val cantidadUFijos = if (esPuenteMultipleOGorrito()) 2 else 4
                  val lineaUFijos = "${NovaCalculos.df1(uFijos)} = $cantidadUFijos"
                  val patronLineaUFijos = Regex("^${Regex.escape(NovaCalculos.df1(uFijos))}\\s*=\\s*\\d+\\s*$")
                  val lineas = textoFinalU.lines().filter { it.isNotBlank() }.toMutableList()
                  val idx = lineas.indexOfFirst { patronLineaUFijos.matches(it.trim()) }
                  if (idx >= 0) {
                      lineas[idx] = lineaUFijos
                  } else {
                      lineas.add(0, lineaUFijos)
                  }
                  textoFinalU = lineas.joinToString("\n")
              }
              if (textoModelo == "ncfc") {
                  // Reemplazar la línea de U de fijos por corridas: fijos adyacentes suman su medida.
                  val runText = NovaCalculos.uFijosRunsNcfc(ancho, divisiones, uFijos)
                  val patronUFijo = Regex("^${Regex.escape(NovaCalculos.df1(uFijos))}\\s*=\\s*\\d+\\s*$")
                  val lineas = textoFinalU.lines().toMutableList()
                  val idx = lineas.indexOfFirst { patronUFijo.matches(it.trim()) }
                  if (idx >= 0) {
                      lineas.removeAt(idx)
                      if (runText.isNotBlank()) lineas.addAll(idx, runText.lines())
                  } else if (runText.isNotBlank()) {
                      lineas.add(0, runText)
                  }
                  textoFinalU = lineas.joinToString("\n")
                  // La U parante puede dar 0 (fijo que no colinda con pared/parante); no mostrarla.
                  textoFinalU = filtrarLineasValorCero(textoFinalU)
              }
              // Full corredizas: la U son las U horizontales (mismo valor que los puentes, por
              // tramo) MÁS la U parante de mocheta (vertical) = 2 por tramo. Sin mocheta no van.
              if (textoModelo == "nfc") {
                  textoFinalU = if (alto > altoHoja && us != 0f) {
                      val nTramosNfc = NovaCalculos.gruposDivisionesMochetaPorModelo(ancho, divisiones, "nfc").size
                      val vertical = "${NovaCalculos.df1(uMocheta)} = ${2 * nTramosNfc}"
                      if (textoUMochetaTramos.isNotBlank()) "$textoUMochetaTramos\n$vertical" else vertical
                  } else ""
              }

              binding.txU.text = textoFinalU
        } else {
            // === INAPARENTE/PIVOTANTE: lógica de vidrio3/NovaIna ===
            binding.tvU.text = NovaPerfilesHelper.obtenerEtiquetaU(us)
            val divisionesU = if (tipoNova == TipoNova.INA && esModeloDoblePuente()) 1 else divisiones
            if (textoModelo == "nfc") {
                // Full corredizas INA: la U son solo las del marco de mocheta, con las mismas
                // reglas que el resto de INA: la superior es uSuperior(ancho) = 1 (arriba) y los
                // costados son uParante2 = 2. Sin mocheta (altoHoja>=alto) no va.
                binding.txU.text = if (alto > altoHoja && us != 0f) {
                    val superior = NovaInaCalculos.uSuperior(ancho)
                    val vertical = NovaInaCalculos.uParante2(alto, altoHoja, us, esModeloInvertidoIna())
                    "${NovaCalculos.df1(superior)} = 1\n${NovaCalculos.df1(vertical)} = 2"
                } else ""
            } else {
                binding.txU.text = NovaInaCalculos.calcularTextoU(
                    ancho = ancho,
                    alto = alto,
                    hoja = hoja,
                    us = us,
                    divisiones = divisionesU,
                    cruceExacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f,
                    incluirUFijos = !esModeloInvertidoIna(),
                    esFullFijos = textoModelo == "nff",
                    esCfc = textoModelo == "ncfc",
                    invertido = esModeloInvertidoIna()
                )
            }
        }
    }

    // ==================== OTROS ALUMINIOS ====================
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    private fun otrosAluminios() {
        // En plano el parante no es el esquinero: etiqueta por defecto.
        binding.tvT.text = getString(R.string.parante)
        val entrada = leerEntradasCalculo()
        val alto = entrada.alto - descuentoAltoVertical()
        val hoja = entrada.hoja
        val divisManual = entrada.divisManual
        val cruce = entrada.cruce

        // Arco de la curva: lo que se reparte/cuenta/corta. Si hay cuerda + flecha se
        // calcula con esos (más exacto); si no, el 'ancho' introducido ya es el arco.
        val ancho = arcoCurvo(entrada.ancho) - descuentoAnchoLateralApa()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val nCorredizas = if (textoModelo == "nff") 0 else NovaCalculos.nCorredizas(ancho, divisiones)
        // Falso puente (APA, sin pared arriba/abajo): si el puente es Múltiple es un "tubo 2.5"
        // aparte; con otro perfil (p. ej. paflón) se suma a la cantidad del puente elegido.
        val nFalsoPuente = if (tipoNova == TipoNova.APA) ladosVaciosVerticales() else 0
        val falsoPuenteEsTubo = esPuenteMultipleOGorrito()
        val nPuentes = NovaCalculos.nPuentesEfectivos(ancho, divisiones)
        val portafelpa = NovaCalculos.portafelpa(altoHoja)
        val divDePortas = NovaCalculos.divDePortas(divisiones, nCorredizas)

        // Tipo de ventana para fórmulas
        val tipoCalculo = tipoCalculoPrincipal()
        val mPuentes1 = NovaCalculos.mPuentes1(ancho, divisiones, tipoCalculo)
        val mPuentes2 = NovaCalculos.mPuentes2(ancho, divisiones, tipoCalculo)
        val paranteApa = if (tipoNova == TipoNova.APA) valorParanteUFijosApa() else 2.5f
        val cruceModelo = if (textoModelo == "nff") 0f else cruce
        val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruceModelo, tipoCalculo, paranteApa)

          if (tipoNova == TipoNova.APA) {
              val us = binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f
                val resultado = NovaPerfilesHelper.calcularOtrosAparente(
                  ancho = ancho,
                  alto = alto,
                  hoja = hoja,
                altoHoja = altoHoja,
                divisiones = divisiones,
                nCorredizas = nCorredizas,
                nPuentes = nPuentes,
                mPuentes1 = mPuentes1,
                mPuentes2 = mPuentes2,
                portafelpa = portafelpa,
                  divDePortas = divDePortas,
                    uFijos = uFijos,
                    tubo = tubo,
                      us = us,
                      puente = puente,
                      modelo = texto,
                      mochetaInferior = mochetaInferiorDoblePuente(),
                      remate = modeloRemate
                )

                val textoTramos = if (alto > altoHoja) {
                    NovaCalculos.textoUMochetaPorTramosAparente(ancho, divisiones, puente, tubo, texto)
                } else {
                    ""
                }
                val textoTramosUnitBase = if (alto > altoHoja) {
                    NovaCalculos.textoTramosUnitarioAparente(ancho, divisiones, puente, tubo, texto)
                } else {
                    ""
                }
              // Original (932353b), cuando "np" era un valor de `texto` y el bloque era uno solo
              // para APA e INA:
              //     val factorMochetas = if (texto == "np") 2 else 1
              //     val textoTramosUnit = escalarCantidadesTexto(textoTramosUnitBase, factorMochetas)
              //     binding.txP.text = textoTramosUnit
              //     binding.txR.text = textoTramosUnit   // el riel también iba x2
              // Cambió dos veces: el remate salió a su propio eje (`modeloRemate`, por eso
              // `esModeloDoblePuente()` y no `texto`) y el riel volvió a uno por tramo (va en el
              // puente de abajo; el felpero en el de arriba). El x2 queda solo para el puente.
              val textoTramosUnitPuente = escalarCantidadesTexto(textoTramosUnitBase, filasDePuente())
              val textoTramosUnitPerfiles = textoTramosUnitBase

              if (textoTramos.isNotBlank()) {
                  binding.txP.text = textoTramosUnitPuente
                  binding.txR.text = textoTramosUnitPerfiles
                  binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else View.VISIBLE
                  binding.lyRiel.visibility = View.VISIBLE
              } else {
                  binding.txP.text = resultado.puentes
                  binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else if (resultado.mostrarPuentes) View.VISIBLE else View.GONE
                  binding.txR.text = resultado.rieles
                  binding.lyRiel.visibility = if (resultado.mostrarRieles) View.VISIBLE else View.GONE
              }
              // Falso puente con perfil elegido (no Múltiple): medida = ancho.
              if (nFalsoPuente > 0 && !falsoPuenteEsTubo) {
                  binding.txP.text = if (alto > altoHoja) {
                      // Hay puente real: se suma el falso a la cantidad del puente mostrado.
                      agregarPuenteFalso(binding.txP.text?.toString().orEmpty(), NovaCalculos.df1(ancho), nFalsoPuente)
                  } else {
                      // No hay puente real (altoHoja >= alto): se muestra solo el falso puente.
                      "${NovaCalculos.df1(ancho)} = $nFalsoPuente"
                  }
                  binding.mulLayout.visibility = View.VISIBLE
              }
              val mostrarFc = mostrarFijoCorredizo(alto, altoHoja)
              if (mostrarFc) {
                  val textoFc = if (textoTramos.isNotBlank()) textoTramosUnitPerfiles else resultado.rieles
                  mostrarFijoCorre(textoFc)
              } else {
                  ocultarFijoCorre()
              }
              // ncfc: riel y u felpera / fijo-corredizo = 1 pieza por tramo, medida = ancho del tramo.
              if (textoModelo == "ncfc") {
                  val nCorrNcfc = NovaCalculos.nCorredizas(divisiones, "ncfc")
                  if (nCorrNcfc > 0) {
                      val textoTramoNcfc = NovaCalculos.anchosTramoNcfc(ancho, divisiones)
                          .groupingBy { NovaCalculos.df1(it) }.eachCount()
                          .entries.joinToString("\n") { "${it.key} = ${it.value}" }
                      binding.txR.text = textoTramoNcfc
                      binding.lyRiel.visibility = View.VISIBLE
                      mostrarFijoCorre(textoTramoNcfc)
                  } else {
                      binding.txR.text = ""
                      binding.lyRiel.visibility = View.GONE
                      ocultarFijoCorre()
                  }
              }
              // Full corredizas: el riel es 1 por cada corrediza; por tramo la medida es el ancho
              // del tramo y la cantidad = corredizas de ese tramo.
              if (textoModelo == "nfc") {
                  val rielNfc = NovaCalculos.rielPorCorredizasNfc(ancho, divisiones, puente, tubo)
                  binding.txR.text = rielNfc
                  binding.lyRiel.visibility = if (rielNfc.isNotBlank()) View.VISIBLE else View.GONE
                  // Riel superior = doble-c: 1 cada 2 corredizas (⌈/2⌉), misma medida por tramo.
                  val dobleC = NovaCalculos.rielSuperiorNfc(ancho, divisiones, puente, tubo)
                  if (dobleC.isNotBlank()) {
                      mostrarFijoCorre(dobleC)
                      binding.tvFc.text = "doble-c"
                  } else {
                      ocultarFijoCorre()
                  }
              }
                  val nParantesDiseno = (when (textoModelo) {
                      "ncc" -> NovaCalculos.nParantesCadaDosCorredizas(divisiones)
                      "n3c" -> NovaCalculos.nParantesCadaTresCorredizas(divisiones)
                      "ncfc" -> NovaCalculos.nParantesDiseno(ancho, divisiones)
                      // Full corredizas: parantes = nº de tramos − 1 (según corredizas por tramo).
                      "nfc" -> (NovaCalculos.gruposDivisionesMochetaPorModelo(ancho, divisiones, "nfc").size - 1).coerceAtLeast(0)
                      else -> NovaCalculos.nParantesDiseno(ancho, divisiones)
                  }) + ladosVaciosLaterales()  // APA: +1 parante por lado lateral al vacío
                  val textoParantes = if (nParantesDiseno > 0) "${NovaCalculos.df1(entrada.alto)} = $nParantesDiseno" else ""
                  binding.txT.text = textoParantes
                  binding.lyTubo.visibility = if (textoParantes.isNotBlank()) View.VISIBLE else View.GONE
            if (textoModelo == "nfc") {
                // Full corredizas: 2 portafelpas por corrediza (todo es corrediza).
                val cantPf = 2 * NovaCalculos.nCorredizas(divisiones, "nfc")
                binding.txPf.text = if (cantPf > 0) "${NovaCalculos.df1(portafelpa)} = $cantPf" else ""
                binding.lyPf.visibility = if (cantPf > 0) View.VISIBLE else View.GONE
            } else if (textoModelo == "ncfc") {
                // ncfc: 2 portafelpas por corrediza + 1 por cada lado de fijo que colinda con corrediza.
                val nCorrNcfc = NovaCalculos.nCorredizas(divisiones, "ncfc")
                val cantPf = 2 * nCorrNcfc + NovaCalculos.portafelpaFijosNcfc(ancho, divisiones)
                binding.txPf.text = if (cantPf > 0) "${NovaCalculos.df1(portafelpa)} = $cantPf" else ""
                binding.lyPf.visibility = if (cantPf > 0) View.VISIBLE else View.GONE
            } else {
                binding.txPf.text = resultado.portafelpaTxt
                binding.lyPf.visibility = if (resultado.mostrarPortafelpa) View.VISIBLE else View.GONE
            }
            binding.txTe.text = resultado.teeTxt
            binding.tLayout.visibility = if (resultado.mostrarTee) View.VISIBLE else View.GONE
            if (textoModelo in modulacionesConTopePropio) {
                // N-56: 1 por cada corrediza que toca el inicio o el final del tramo (pared o
                // parante). ncfc según su patrón; full, doble y triple corrediza: 2 por tramo.
                val nTope = NovaCalculos.anguloTopeCantidad(ancho, divisiones, textoModelo)
                binding.txTo.text = if (nTope > 0) "${NovaCalculos.df1(altoHoja - 0.9f)} = $nTope" else ""
                binding.lyTo.visibility = if (nTope > 0) View.VISIBLE else View.GONE
            } else {
                binding.txTo.text = resultado.topeTxt
                binding.lyTo.visibility = if (resultado.mostrarTope) View.VISIBLE else View.GONE
            }
            if (textoModelo == "ncfc" || textoModelo == "nfc") {
                // H = 1 por cada módulo corrediza (misma medida que la H existente).
                val nCorr = NovaCalculos.nCorredizas(divisiones, textoModelo)
                binding.txH.text = if (nCorr > 0) "${NovaCalculos.df1(uFijos)} = $nCorr" else ""
                binding.lyH.visibility = if (nCorr > 0) View.VISIBLE else View.GONE
            } else {
                binding.txH.text = resultado.hacheTxt
                binding.lyH.visibility = if (resultado.mostrarHache) View.VISIBLE else View.GONE
            }

            // Ocultar lyUf en aparente
            binding.lyUf.visibility = View.GONE
            if (divisiones == 1) {
                // Sin corredizas (1 división) no hay "fijo corrediza": la U ya lo reemplaza.
                binding.txR.text = ""
                binding.txPf.text = ""
                binding.txTe.text = ""
                binding.lyRiel.visibility = View.GONE
                ocultarFijoCorre()
                binding.lyUf.visibility = View.GONE
                binding.tLayout.visibility = View.GONE
                binding.lyPf.visibility = View.GONE
            }
        } else {
            // === INAPARENTE/PIVOTANTE: lógica de vidrio3/NovaIna ===
            val cruceExacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
            val resultado = NovaInaCalculos.calcularOtrosAluminios(
                ancho = ancho,
                alto = alto,
                hoja = hoja,
                divisiones = divisiones,
                cruceExacto = cruceExacto,
                puentesExtra = ladosVaciosVerticales(),
                remate = modeloRemate
            )

                val textoTramos = if (alto > altoHoja) {
                  NovaCalculos.textoUMochetaPorTramosAparente(ancho, divisiones, puente, tubo, texto)
                } else {
                    ""
                }
                val textoTramosUnit = if (alto > altoHoja) {
                  NovaCalculos.textoTramosUnitarioAparente(ancho, divisiones, puente, tubo, texto)
                } else {
                    ""
                }

              // PUENTE. Mismo criterio que la rama aparente: el x2 del doble puente sale de
              // `escalarCantidadesTexto` (ver el original de 932353b comentado allá arriba).
              if (textoTramos.isNotBlank()) {
                  binding.txP.text = escalarCantidadesTexto(textoTramosUnit, filasDePuente())
                  binding.txR.text = textoTramosUnit
                  binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else View.VISIBLE
                  binding.lyRiel.visibility = View.VISIBLE
              } else {
                  binding.txP.text = resultado.puentes
                  binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else View.VISIBLE
                  binding.txR.text = resultado.rieles
                  binding.lyRiel.visibility = if (resultado.rieles.isNotBlank()) View.VISIBLE else View.GONE
              }

            val mostrarFc = mostrarFijoCorredizo(alto, altoHoja)
            if (mostrarFc) {
                val textoFc = when {
                    esModeloInvertidoIna() -> textoFijoCorreInaInvertido(ancho)
                    textoTramos.isNotBlank() -> textoTramosUnit
                    else -> resultado.rieles
                }
                val mostrarUfEnFc = mostrarUFelperoIna(alto, altoHoja) || usarFcComoUFelpero(alto, altoHoja)
                val textoUf = if (textoTramos.isNotBlank()) textoTramosUnit else resultado.uFelpero
                val textoPrincipal = if (!esModeloInvertidoIna() && mostrarUfEnFc) textoUf else textoFc
                mostrarFijoCorre(
                    textoPrincipal,
                    if (!esModeloInvertidoIna() && mostrarUfEnFc) R.string.u_felpero else R.string.fijo_corre
                )
            } else {
                ocultarFijoCorre()
            }

            // ncfc INA = un solo tramo: puente, U felpera y riel son al ANCHO TOTAL (1 c/u). El
            // descuento de parante lateral por encuentro sin check ya está incluido en `ancho`.
            if (textoModelo == "ncfc") {
                val medidaTotal = "${NovaCalculos.df1(ancho)} = 1"
                // Riel (siempre que haya corredizas) al ancho total.
                binding.txR.text = medidaTotal
                binding.lyRiel.visibility = View.VISIBLE
                // U felpera al ancho total.
                mostrarFijoCorre(medidaTotal, R.string.u_felpero)
                // Puente al ancho total, solo si existe puente (hay mocheta: altoHoja < alto).
                if (altoHoja < alto) {
                    binding.txP.text = medidaTotal
                    binding.mulLayout.visibility = View.VISIBLE
                } else {
                    binding.mulLayout.visibility = View.GONE
                }
            }

            // Full corredizas INA (un tramo): riel = 1 por corrediza; doble-c (era u felpera) =
            // 1 cada 2 corredizas; puente al ancho total. Medidas por tramo (= ancho, un solo tramo).
            if (textoModelo == "nfc") {
                val rielNfc = NovaCalculos.rielPorCorredizasNfc(ancho, divisiones, puente, tubo)
                binding.txR.text = rielNfc
                binding.lyRiel.visibility = if (rielNfc.isNotBlank()) View.VISIBLE else View.GONE
                val dobleC = NovaCalculos.rielSuperiorNfc(ancho, divisiones, puente, tubo)
                if (dobleC.isNotBlank()) {
                    mostrarFijoCorre(dobleC)
                    binding.tvFc.text = "doble-c"
                } else ocultarFijoCorre()
                if (altoHoja < alto) {
                    binding.txP.text = "${NovaCalculos.df1(ancho)} = 1"
                    binding.mulLayout.visibility = View.VISIBLE
                } else binding.mulLayout.visibility = View.GONE
            }

            // U FELPERO
            binding.txUf.text = ""

            // HACHE
            if (textoModelo == "ncfc" || textoModelo == "nfc") {
                // H = 1 por cada módulo corrediza (misma medida = hache inaparente).
                val nCorr = NovaCalculos.nCorredizas(divisiones, textoModelo)
                val hacheIna = NovaInaCalculos.hache(ancho, divisiones, NovaInaCalculos.cruce(cruceExacto, divisiones))
                binding.txH.text = if (nCorr > 0) "${NovaCalculos.df1(hacheIna)} = $nCorr" else ""
            } else {
                binding.txH.text = resultado.hache
            }

            // ÁNGULO TOPE (N-56): 1 por cada corrediza que colinda con pared o parante.
            if (textoModelo in modulacionesConTopePropio) {
                val nTope = NovaCalculos.anguloTopeCantidad(ancho, divisiones, textoModelo)
                binding.txTo.text = if (nTope > 0) "${NovaCalculos.df1(altoHoja - 0.9f)} = $nTope" else ""
                binding.lyTo.visibility = if (nTope > 0) View.VISIBLE else View.GONE
            } else {
                binding.txTo.text = resultado.angTope
                binding.lyTo.visibility = if (resultado.mostrarAngTope) View.VISIBLE else View.GONE
            }

            // PORTAFELPA
            if (textoModelo == "ncfc") {
                // Contada por módulos sobre el patrón real: 2 por corrediza + 1 por cada lado de
                // fijo que colinda con corrediza.
                val cantPf = NovaCalculos.portafelpaTotalNcfc(ancho, divisiones)
                binding.txPf.text = if (cantPf > 0) "${NovaCalculos.df1(NovaCalculos.portafelpa(altoHoja))} = $cantPf" else ""
                binding.lyPf.visibility = if (cantPf > 0) View.VISIBLE else View.GONE
            } else if (textoModelo == "nfc") {
                // Full corredizas: 2 portafelpas por corrediza.
                val cantPf = 2 * NovaCalculos.nCorredizas(divisiones, "nfc")
                binding.txPf.text = if (cantPf > 0) "${NovaCalculos.df1(NovaCalculos.portafelpa(altoHoja))} = $cantPf" else ""
                binding.lyPf.visibility = if (cantPf > 0) View.VISIBLE else View.GONE
            } else {
                binding.txPf.text = resultado.portafelpa
            }

              // Visibilidad inaparente
              binding.lyH.visibility = if (textoModelo == "ncfc" || textoModelo == "nfc") {
                  if (binding.txH.text.isNotBlank()) View.VISIBLE else View.GONE
              } else if (resultado.mostrarHache) View.VISIBLE else View.GONE
              binding.lyUf.visibility = View.GONE
              binding.tLayout.visibility = View.GONE
              val nParantesDiseno = (when (textoModelo) {
                  "ncc" -> NovaCalculos.nParantesCadaDosCorredizas(divisiones)
                  "n3c" -> NovaCalculos.nParantesCadaTresCorredizas(divisiones)
                  // ncfc/nfc en INA es un solo tramo: no hay parantes que dividan.
                  "ncfc", "nfc" -> 0
                  else -> NovaCalculos.nParantesDiseno(ancho, divisiones)
              }) + ladosVaciosLaterales()  // INA: +1 parante por lado lateral al vacío
              val textoParantes = if (nParantesDiseno > 0) "${NovaCalculos.df1(entrada.alto)} = $nParantesDiseno" else ""
              binding.txT.text = textoParantes
              binding.lyTubo.visibility = if (textoParantes.isNotBlank()) View.VISIBLE else View.GONE

              // TEE vacío en ina
              binding.txTe.text = ""
              if (divisiones == 1) {
                binding.txR.text = ""
                binding.txFc.text = ""
                binding.txPf.text = ""
                binding.lyRiel.visibility = View.GONE
                  binding.fcLayout.visibility = View.GONE
                  binding.lyUf.visibility = View.GONE
                  binding.tLayout.visibility = View.GONE
                  binding.lyPf.visibility = View.GONE
                  binding.lyTubo.visibility = View.GONE
              }
          }

        // Full fijos (nff): no hay corredizas, así que se oculta todo lo relacionado a ellas
        // (riel, fijo corrediza, U felpero, H, portafelpa, tope). El Tee es de mochetas (no de
        // corredizas) y el puente sigue su lógica principal (se muestra si altoHoja < alto).
        if (textoModelo == "nff") {
            binding.txR.text = ""
            binding.txPf.text = ""
            binding.txH.text = ""
            binding.txTo.text = ""
            ocultarFijoCorre()
            binding.lyRiel.visibility = View.GONE
            binding.lyUf.visibility = View.GONE
            binding.lyH.visibility = View.GONE
            binding.lyPf.visibility = View.GONE
            binding.lyTo.visibility = View.GONE
        }

        // Falso puente como pieza aparte "tubo 2.5" solo cuando el puente es Múltiple;
        // con otro perfil ya se sumó a la cantidad del puente (nPuentes), sin línea aparte.
        if (nFalsoPuente > 0 && falsoPuenteEsTubo) {
            binding.txFalsoPuente.text = "${NovaCalculos.df1(ancho)} = $nFalsoPuente"
            binding.lyFalsoPuente.visibility = View.VISIBLE
        } else {
            binding.lyFalsoPuente.visibility = View.GONE
        }
      }

    // ==================== VIDRIOS ====================
    @SuppressLint("SetTextI18n")
    private fun vidriosTexto() {
        val alto = alto() - descuentoAltoVertical()
        val hoja = altoHoja()
        val us = binding.etU.text.toString().toFloat()
        val divisManual = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
        val cruce = cruce()

        // Arco de la curva (ver arcoCurvo): cuerda + flecha si se indican, si no el ancho.
        val ancho = arcoCurvo(binding.etAncho.text.toString().toFloat()) - descuentoAnchoLateralApa()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)

        if (tipoNova == TipoNova.APA) {
            // === APARENTE: lógica original ===
          val nFijos = when {
              textoModelo == "nff" -> divisiones
              textoModelo == "nfc" -> 0   // full corredizas: sin fijos
              textoModelo == "ncfc" -> NovaCalculos.nFijos(divisiones, "ncfc")
              else -> NovaCalculos.nFijos(ancho, divisiones)
          }
          // ncfc: la cantidad de vidrios de corrediza usa el conteo del patrón invertido.
          val nCorredizas = when {
              textoModelo == "nff" -> 0
              textoModelo == "nfc" -> divisiones   // full corredizas: todas son corredizas
              textoModelo == "ncfc" -> NovaCalculos.nCorredizas(divisiones, "ncfc")
              else -> NovaCalculos.nCorredizas(ancho, divisiones)
          }
            val cruceModelo = if (textoModelo == "nff") 0f else cruce
            val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruceModelo, "apa", valorParanteUFijosApa())

            // Usar textoModelo (modulación) y no texto (geometría): en L/C/serie texto es "nl"/etc
            // y el vidrio de fijos necesita la modulación para aplicar la lógica de full fijos.
            // Full corredizas: sin vidrio de fijo.
            val vidriosFijos = if (textoModelo == "nfc") "" else
                NovaVidriosHelper.calcularVidriosFijos(ancho, uFijos, altoHoja, us, nFijos, divisiones, "aparente", textoModelo)
            // Full corredizas: cada corrediza tiene ancho = uFijos (= H); cantidad = nº de corredizas.
            val vidrioCorre = if (textoModelo == "nfc")
                "${NovaCalculos.df1(uFijos)} x ${NovaCalculos.df1(altoHoja - 3.5f)} = $nCorredizas"
            else NovaVidriosHelper.calcularVidriosCorredizos(uFijos, altoHoja, nCorredizas)
              val vidrioMocheta = NovaVidriosHelper.calcularVidrioMochetaAparente(
                  ancho = ancho,
                  alto = alto,
                  altoHoja = altoHoja,
                  divisiones = divisiones,
                  tubo = tubo,
                  puente = puente,
                  us = us,
                  // modelo = modulación (patrón); el remate (np/nr) va aparte para combinarlos.
                  modelo = textoModelo,
                  mochetaInferior = mochetaInferiorDoblePuente(),
                  remate = modeloRemate
              )

            // Full fijos: sin corredizas no se muestra el vidrio de corrediza (el del -1.4).
            val mostrarVidrioCorre = textoModelo != "nff" && divisiones > 1
            val partesVidrio = mutableListOf(vidriosFijos)
            if (mostrarVidrioCorre) partesVidrio.add(vidrioCorre)
            if (hoja < alto) partesVidrio.add(vidrioMocheta)
            binding.txV.text = partesVidrio.filter { it.isNotBlank() }.joinToString("\n")
        } else {
            // === INAPARENTE/PIVOTANTE: lógica de vidrio3/NovaIna ===
            val cruceExacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
            if (textoModelo == "nfc") {
                // Full corredizas INA: cada corrediza = uFijos (= H) de ancho, cantidad = nº de
                // corredizas (igual que en APA), más la mocheta.
                val cruceIna = NovaInaCalculos.cruce(cruceExacto, divisiones)
                val uFijosIna = NovaInaCalculos.uFijos(ancho, divisiones, cruceIna)
                val corr = "${NovaCalculos.df1(uFijosIna)} x ${NovaCalculos.df1(altoHoja - 3.5f)} = $divisiones"
                val mocheta = NovaInaCalculos.vidrioMocheta(
                    ancho = ancho, alto = alto, hoja = hoja, altoHoja = altoHoja,
                    divisiones = divisiones, cruce = cruceIna, modelo = "nfc",
                    alturaPuente = tubo, mochetaInferior = mochetaInferiorDoblePuente(),
                    mochetaInvertidaIna = esModeloInvertidoIna(), remate = modeloRemate
                )
                binding.txV.text = listOf(corr, mocheta).filter { it.isNotBlank() }.joinToString("\n")
            } else {
              binding.txV.text = NovaInaCalculos.calcularVidrios(
                  ancho = ancho,
                  alto = alto,
                  hoja = hoja,
                  us = us,
                  divisiones = divisiones,
                  cruceExacto = cruceExacto,
                  // modelo = modulación (patrón); el remate (np/nr) va aparte para combinarlos.
                  modelo = textoModelo,
                  alturaPuente = tubo,
                  mochetaInferior = mochetaInferiorDoblePuente(),
                  mochetaInvertidaIna = esModeloInvertidoIna(),
                  remate = modeloRemate
              )
            }
          }
      }

    // ==================== REFERENCIAS ====================
    /**
     * [anchoRealMostrar] = medida que se midió, SOLO para la línea "An:" de la referencia. En L y
     * en C los campos ya traen el ancho útil (con el descuento de esquina de `medidasCalculoNlApa`,
     * que es el que se corta y se dibuja) y la medida original se pasa por aquí. Sin este
     * parámetro habría que volver a llamar a `referencias()` con la medida original, y entonces
     * las divisiones, los fijos y las corredizas saldrían del ancho sin descontar: un lado de 122
     * cuenta 3 divisiones sin descuento y 2 con él, que es lo que dibuja y corta el resto.
     */
    private fun referencias(anchoRealMostrar: Float? = null) {
        val anchoEntrada = binding.etAncho.text.toString().toFloat()
        val altoReal = binding.etAlto.text.toString().toFloat()
        val alto = altoReal - descuentoAltoVertical()
        val hoja = binding.etHoja.text.toString().toFloat()
        val divisManual = binding.etPartes.text.toString().toInt()

        // Arco de la curva (ver arcoCurvo): cuerda + flecha si se indican, si no el ancho.
        // anchoReal = medida sin descuento de encuentro (lo que se muestra/archiva en referencia);
        // ancho = útil (con descuento) para los cálculos de corte.
        val anchoCampo = arcoCurvo(anchoEntrada)
        val anchoReal = anchoRealMostrar ?: anchoCampo
        val ancho = anchoCampo - descuentoAnchoLateralApa()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val siNoMoch = NovaCalculos.siNoMoch(alto, hoja)
        val nFijos = NovaCalculos.nFijos(ancho, divisiones)
        val nCorredizas = NovaCalculos.nCorredizas(ancho, divisiones)
        val tipoCalculo = tipoCalculoPrincipal()
        val puntosU = if (divisiones > 4) {
            NovaUIHelper.generarPuntosU(ancho, divisManual, cruce(), tipoCalculo)
        } else {
            ""
        }

        val referenciasBase = NovaUIHelper.generarReferencias(
            ancho, alto, altoHoja, divisiones, nFijos, nCorredizas, siNoMoch, puntosU,
            modelo = texto,
            alturaPuente = tubo,
            mochetaInferior = mochetaInferiorDoblePuente(),
            remate = modeloRemate,
            // N-31: la mocheta es la misma en aparente e inaparente (antes solo aparente descontaba).
            descontarPuentes = true,
            anchoReal = anchoReal,
            altoReal = altoReal
        )
        // Con varios arcos, las referencias van arco por arco: la curva equivalente no dice nada.
        arcosDeLaMedida()?.let { binding.txReferencias.text = "$referenciasBase\n${resumenDeVariosArcos(it)}"; return }
        val geometria = geometriaCurva(ancho, divisiones)
        binding.txReferencias.text = if (geometria != null) {
            "$referenciasBase\n${resumenGeometriaCurva(geometria)}"
        } else {
            referenciasBase
        }
    }

*/
