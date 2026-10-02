package crystal.crystal.contrato

import crystal.crystal.Listado
import crystal.crystal.pos.AmbientesDeProforma
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Cómo trata el contrato el IGV del precio. Sin mención, el contrato no dice nada de impuestos. */
enum class Igv { SIN_MENCION, INCLUIDO, NO_INCLUIDO }

/**
 * Las condiciones que se repiten de un contrato a otro: el taller las apunta una vez y se recuerdan.
 * El plazo y el adelanto son los de partida; en cada contrato se pueden cambiar.
 *
 * Todo lleva valor por defecto para que una versión guardada sin algún campo se lea igual.
 */
data class CondicionesContrato(
    val plazoDias: Int = 15,
    val adelantoPorcentaje: Int = 50,
    val garantiaMeses: Int = 12,
    val diasParaObservar: Int = 3,
    /** Donde se firma y cuyos jueces resuelven. Vacío: el lugar donde se hacen los trabajos. */
    val ciudad: String = "",
    val igv: Igv = Igv.SIN_MENCION
)

/**
 * Una de las dos partes: el cliente o el contratista.
 *
 * Si es una empresa (RUC), firma por ella su representante, que también va identificado.
 */
data class ParteDelContrato(
    val nombre: String = "",
    val documento: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val representante: String = "",
    val documentoRepresentante: String = ""
) {
    private val soloDigitos get() = documento.filter { it.isDigit() }

    /**
     * Una empresa, que firma por medio de su representante: un RUC de 11 dígitos que no empieza
     * por 10. El RUC 10 es de una persona con negocio, que firma ella misma.
     */
    val esEmpresa: Boolean
        get() = soloDigitos.length == 11 && !soloDigitos.startsWith("10")

    /**
     * Cómo se identifica en el texto: `RUC N° 20123456789`, `DNI N° 41888817`.
     *
     * Si el documento ya trae su tipo delante —así llega del registro de clientes— se respeta.
     */
    fun identificacion(): String {
        val doc = documento.trim()
        if (doc.isEmpty()) return "documento N° $BLANCO"
        if (doc.first().isLetter()) {
            val tipo = doc.takeWhile { !it.isDigit() }.trim().trimEnd('N', '°', 'º', '.', ' ')
            val numero = doc.dropWhile { !it.isDigit() }.trim()
            return if (numero.isEmpty()) doc else "$tipo N° $numero"
        }
        val tipo = when (soloDigitos.length) {
            11 -> "RUC"
            8 -> "DNI"
            else -> "documento"
        }
        return "$tipo N° $doc"
    }
}

/** Una fila del detalle: lo que se fabrica, dónde va, cuánto mide y cuánto cuesta. */
data class FilaDelDetalle(
    val numero: Int,
    val ambiente: String,
    val descripcion: String,
    val medidas: String,
    val cantidad: String,
    val importe: Double
)

/** Una cláusula: su número en letras, su título y su texto. */
data class Clausula(
    val ordinal: String,
    val titulo: String,
    val texto: String,
    /** La del objeto lleva debajo el detalle de los trabajos. */
    val llevaDetalle: Boolean = false
)

/** Todo lo que dice un contrato. Se arma en la pantalla y de aquí salen el texto y el PDF. */
data class DatosDelContrato(
    val fecha: Date,
    val folio: String,
    val cliente: ParteDelContrato,
    val contratista: ParteDelContrato,
    /** Donde se instalan los trabajos. Vacío: el domicilio del cliente. */
    val direccionObra: String,
    val lista: List<Listado>,
    val plazoDias: Int,
    val adelanto: Double,
    /** Cuándo se paga el saldo (dd/MM/yyyy). Vacío: a la entrega e instalación. */
    val fechaSaldo: String,
    val observaciones: String,
    val condiciones: CondicionesContrato
) {
    /** El total es la suma de los ítems, la misma cuenta que hace la lista: no se lee de un texto. */
    val total: Double get() = redondear(lista.sumOf { it.costo.toDouble() })
    val adelantoReal: Double get() = redondear(adelanto.coerceIn(0.0, total))
    val saldo: Double get() = redondear(total - adelantoReal)
    val obra: String get() = direccionObra.trim().ifEmpty { cliente.direccion.trim() }

    private fun redondear(v: Double) = Math.round(v * 100) / 100.0
}

const val BLANCO = "____________"

/**
 * El texto del contrato de fabricación e instalación, cláusula por cláusula.
 *
 * Sale de aquí tanto lo que se lee en la pantalla como lo que se imprime en el PDF, así que los
 * dos dicen siempre lo mismo.
 */
object ClausulasContrato {

    private val ORDINALES = listOf(
        "PRIMERA", "SEGUNDA", "TERCERA", "CUARTA", "QUINTA", "SEXTA", "SÉPTIMA", "OCTAVA",
        "NOVENA", "DÉCIMA", "UNDÉCIMA", "DUODÉCIMA", "DECIMOTERCERA", "DECIMOCUARTA"
    )

    const val TITULO = "CONTRATO DE FABRICACIÓN E INSTALACIÓN"
    const val TITULO_RECIBO = "RECIBO DE ADELANTO"

    /** `3500.0` → `3,500.00`. */
    fun soles(v: Double): String = "%,.2f".format(Locale.US, v)

    fun fechaLarga(fecha: Date): String =
        SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "PE")).format(fecha)

    fun fechaCorta(fecha: Date): String = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(fecha)

    private fun o(texto: String) = texto.trim().ifEmpty { BLANCO }

    /** Un número de medida con un decimal, sin el `.0` sobrante: 120, 120.5. */
    private fun df1(v: Float): String {
        val r = Math.round(v * 10) / 10.0
        return if (r == Math.floor(r)) r.toLong().toString() else "%.1f".format(Locale.US, r)
    }

    private fun unidad(uni: String): String = when (uni.trim().lowercase(Locale.ROOT)) {
        "centímetros", "centimetros" -> "cm"
        "metros" -> "m"
        "milímetros", "milimetros" -> "mm"
        "pulgadas" -> "pulg"
        else -> uni.trim()
    }

    /** Las medidas de un ítem como se leen en obra: `120 × 150 cm`. Por unidad no hay medida. */
    fun medidas(item: Listado): String {
        val u = unidad(item.uni)
        return when (item.escala) {
            "p2", "m2" -> "${df1(item.medi1)} × ${df1(item.medi2)} $u"
            "ml" -> "${df1(item.medi1)} $u"
            "m3" -> "${df1(item.medi1)} × ${df1(item.medi2)} × ${df1(item.medi3)} $u"
            else -> "—"
        }.trim()
    }

    /**
     * El detalle de los trabajos, ítem por ítem y en el orden de la proforma —por ambientes, si los
     * hay—, para que el número de cada ítem sea el mismo en los dos papeles.
     *
     * Cada ítem va en su fila con su medida: juntarlos por nombre perdía que eran medidas distintas.
     */
    fun detalle(lista: List<Listado>): List<FilaDelDetalle> {
        var numero = 0
        return AmbientesDeProforma.agrupar(lista).flatMap { (ambiente, items) ->
            items.map { item ->
                numero++
                FilaDelDetalle(
                    numero = numero,
                    ambiente = ambiente,
                    descripcion = item.producto.trim().ifEmpty { "Producto sin nombre" },
                    medidas = medidas(item),
                    cantidad = df1(item.canti),
                    importe = item.costo.toDouble()
                )
            }
        }
    }

    /** La presentación de las partes, antes de las cláusulas. */
    fun encabezado(d: DatosDelContrato): String {
        val c = d.cliente
        val k = d.contratista
        val representanteCliente = if (c.esEmpresa) {
            ", debidamente representada por ${o(c.representante)}, identificado(a) con DNI N° " +
                o(c.documentoRepresentante)
        } else ""
        val telefonoCliente = c.telefono.trim().takeIf { it.isNotEmpty() }?.let { ", teléfono $it" }.orEmpty()
        val telefonoContratista = k.telefono.trim().takeIf { it.isNotEmpty() }?.let { ", teléfono $it" }.orEmpty()
        return "Conste por el presente documento el contrato de fabricación e instalación que " +
            "celebran, de una parte, ${o(c.nombre)}, identificado(a) con ${c.identificacion()}, " +
            "con domicilio en ${o(c.direccion)}$telefonoCliente$representanteCliente, a quien en " +
            "adelante se le denominará EL CLIENTE; y de la otra parte, ${o(k.nombre)}, " +
            "identificado(a) con ${k.identificacion()}, con domicilio en ${o(k.direccion)}" +
            "$telefonoContratista, a quien en adelante se le denominará EL CONTRATISTA; en los " +
            "términos y condiciones siguientes:"
    }

    /** Las cláusulas, numeradas en orden; las que no aplican no salen y no dejan hueco. */
    fun clausulas(d: DatosDelContrato): List<Clausula> {
        val cond = d.condiciones
        val piezas = mutableListOf<Triple<String, String, Boolean>>()

        piezas += Triple(
            "OBJETO",
            "EL CONTRATISTA se obliga a fabricar e instalar, en el inmueble ubicado en " +
                "${o(d.obra)}, los trabajos que se detallan a continuación, con los materiales, " +
                "medidas y cantidades indicados:",
            true
        )

        val igv = when (cond.igv) {
            Igv.INCLUIDO -> ", monto que incluye el IGV"
            Igv.NO_INCLUIDO -> ", monto que no incluye el IGV"
            Igv.SIN_MENCION -> ""
        }
        piezas += Triple(
            "PRECIO",
            "El precio total pactado es de S/ ${soles(d.total)} (${MontoEnLetras.soles(d.total)})" +
                "$igv. Este precio corresponde únicamente a los trabajos detallados en la cláusula " +
                "primera.",
            false
        )

        val cuandoSaldo = if (d.fechaSaldo.isBlank()) "a la entrega e instalación de los trabajos"
        else "a más tardar el ${d.fechaSaldo.trim()}"
        val pago = when {
            d.total <= 0.0 -> "La forma de pago será la que acuerden las partes."
            d.saldo <= 0.0 ->
                "EL CLIENTE paga el íntegro del precio a la firma del presente contrato."
            d.adelantoReal <= 0.0 ->
                "EL CLIENTE pagará el íntegro del precio $cuandoSaldo."
            else ->
                "EL CLIENTE pagará a la firma del presente contrato un adelanto de " +
                    "S/ ${soles(d.adelantoReal)} (${MontoEnLetras.soles(d.adelantoReal)}), y el " +
                    "saldo de S/ ${soles(d.saldo)} (${MontoEnLetras.soles(d.saldo)}) $cuandoSaldo. " +
                    "La fabricación se inicia una vez recibido el adelanto."
        }
        piezas += Triple("FORMA DE PAGO", pago, false)

        val plazo = if (d.plazoDias > 0) "${d.plazoDias} días hábiles" else "$BLANCO días hábiles"
        piezas += Triple(
            "PLAZO DE ENTREGA",
            "EL CONTRATISTA entregará los trabajos instalados en un plazo de $plazo, contados " +
                "desde el pago del adelanto y la verificación de las medidas en obra. El plazo se " +
                "suspende mientras el inmueble no esté listo para la instalación, por demora en " +
                "los pagos o por cambios solicitados por EL CLIENTE, y se reanuda cuando cesa la causa.",
            false
        )

        piezas += Triple(
            "MEDIDAS Y CAMBIOS",
            "Los trabajos se fabrican a medida, con las medidas tomadas en obra por EL " +
                "CONTRATISTA. Cualquier cambio de medidas, modelo, color, material o accesorios " +
                "que EL CLIENTE solicite después de la firma se presupuestará por separado y podrá " +
                "modificar el precio y el plazo.",
            false
        )

        piezas += Triple(
            "OBLIGACIONES DE EL CLIENTE",
            "EL CLIENTE se obliga a: a) permitir el acceso al inmueble en las fechas coordinadas; " +
                "b) entregar los vanos y superficies terminados (tarrajeo, pisos y acabados) y " +
                "aptos para la instalación; c) contar con energía eléctrica en el lugar de trabajo; " +
                "y d) pagar el precio en la forma pactada.",
            false
        )

        piezas += Triple(
            "TRABAJOS NO INCLUIDOS",
            "No están incluidos los trabajos de albañilería, resanes, pintura, ni el retiro o " +
                "desmontaje de estructuras existentes, salvo que figuren expresamente en el " +
                "detalle de la cláusula primera.",
            false
        )

        if (cond.garantiaMeses > 0) {
            val meses = if (cond.garantiaMeses == 1) "un (1) mes" else
                "${MontoEnLetras.letras(cond.garantiaMeses.toLong())} (${cond.garantiaMeses}) meses"
            piezas += Triple(
                "GARANTÍA",
                "EL CONTRATISTA garantiza los trabajos por $meses desde su entrega, contra " +
                    "defectos de fabricación e instalación (perfiles, accesorios, ajuste y " +
                    "funcionamiento). La garantía no cubre roturas de vidrio por golpes, mal uso, " +
                    "manipulación o modificaciones hechas por terceros, ni daños causados por " +
                    "asentamientos o movimientos de la estructura del inmueble.",
                false
            )
        }

        val dias = cond.diasParaObservar.coerceAtLeast(1)
        piezas += Triple(
            "ENTREGA Y CONFORMIDAD",
            "Terminada la instalación, EL CLIENTE revisará los trabajos y firmará su conformidad. " +
                "Las observaciones deberán comunicarse dentro de los $dias días hábiles siguientes " +
                "a la entrega; vencido ese plazo sin observaciones, los trabajos se tendrán por " +
                "recibidos conforme.",
            false
        )

        piezas += Triple(
            "DESISTIMIENTO E INCUMPLIMIENTO",
            "Por tratarse de trabajos fabricados a medida, si EL CLIENTE desiste del contrato una " +
                "vez iniciada la fabricación, el adelanto quedará a favor de EL CONTRATISTA para " +
                "cubrir los materiales cortados y el trabajo realizado. Si EL CONTRATISTA no " +
                "entrega los trabajos en el plazo pactado sin causa justificada, EL CLIENTE podrá " +
                "resolver el contrato y exigir la devolución del adelanto.",
            false
        )

        if (d.observaciones.isNotBlank()) {
            piezas += Triple(
                "ACUERDOS ADICIONALES",
                "Las partes acuerdan además lo siguiente: ${d.observaciones.trim()}",
                false
            )
        }

        val jueces = cond.ciudad.trim().ifEmpty { null }?.let { "de $it" }
            ?: "del lugar donde se ejecutan los trabajos"
        piezas += Triple(
            "JURISDICCIÓN",
            "Para cualquier controversia derivada del presente contrato, las partes se someten a " +
                "la competencia de los jueces y tribunales $jueces.",
            false
        )

        return piezas.mapIndexed { i, (titulo, texto, detalle) ->
            Clausula(ORDINALES.getOrElse(i) { "${i + 1}ª" }, titulo, texto, detalle)
        }
    }

    /** El párrafo final, antes de las firmas. */
    fun cierre(d: DatosDelContrato): String {
        val lugar = d.condiciones.ciudad.trim().takeIf { it.isNotEmpty() }?.let { "en $it, " }.orEmpty()
        return "En señal de conformidad, las partes firman el presente contrato en dos ejemplares " +
            "de igual valor, ${lugar}el ${fechaLarga(d.fecha)}."
    }

    /** Lo que falta para que el contrato sirva; vacío si está completo. */
    fun faltantes(d: DatosDelContrato): List<String> = buildList {
        if (d.cliente.nombre.isBlank()) add("Nombre del cliente")
        if (d.cliente.documento.isBlank()) add("DNI o RUC del cliente")
        if (d.cliente.esEmpresa && d.cliente.representante.isBlank()) add("Representante del cliente")
        if (d.cliente.esEmpresa && d.cliente.documentoRepresentante.isBlank()) add("DNI del representante")
        if (d.obra.isBlank()) add("Dirección de la obra")
        if (d.contratista.nombre.isBlank()) add("Nombre del contratista")
        if (d.contratista.documento.isBlank()) add("DNI o RUC del contratista")
        if (d.lista.isEmpty()) add("Trabajos (la lista está vacía)")
        if (d.plazoDias <= 0) add("Plazo de entrega")
    }

    /** El contrato entero como texto, para leerlo en la pantalla o guardarlo en el Baúl. */
    fun textoPlano(d: DatosDelContrato): String = buildString {
        appendLine(TITULO)
        appendLine("Folio ${d.folio} · ${fechaCorta(d.fecha)}")
        appendLine()
        appendLine(encabezado(d))
        clausulas(d).forEach { c ->
            appendLine()
            appendLine("${c.ordinal}. — ${c.titulo}")
            appendLine(c.texto)
            if (c.llevaDetalle) {
                appendLine()
                var ambiente = ""
                detalle(d.lista).forEach { f ->
                    if (f.ambiente.isNotEmpty() && f.ambiente != ambiente) {
                        ambiente = f.ambiente
                        appendLine("[${f.ambiente}]")
                    }
                    appendLine(
                        "${f.numero}. ${f.descripcion} — ${f.medidas} — cant. ${f.cantidad} — " +
                            "S/ ${soles(f.importe)}"
                    )
                }
                appendLine("Total: S/ ${soles(d.total)}")
            }
        }
        appendLine()
        appendLine(cierre(d))
        appendLine()
        appendLine("EL CLIENTE: ${o(firmante(d.cliente))}")
        appendLine("EL CONTRATISTA: ${o(firmante(d.contratista))}")
    }

    /** Quién firma por una parte: la persona, o el representante en nombre de la empresa. */
    fun firmante(p: ParteDelContrato): String =
        if (p.esEmpresa && p.representante.isNotBlank()) "${p.representante.trim()} p. ${p.nombre.trim()}"
        else p.nombre.trim()

    /** El recibo de adelanto, párrafo por párrafo. */
    fun recibo(d: DatosDelContrato): List<String> {
        val cuandoSaldo = if (d.fechaSaldo.isBlank()) "a la entrega e instalación de los trabajos"
        else "a más tardar el ${d.fechaSaldo.trim()}"
        return listOf(
            "Recibí de ${o(d.cliente.nombre)}, identificado(a) con ${d.cliente.identificacion()}, " +
                "la suma de S/ ${soles(d.adelantoReal)} (${MontoEnLetras.soles(d.adelantoReal)}), " +
                "por concepto de adelanto para la fabricación e instalación de los trabajos " +
                "detallados a continuación, a instalarse en ${o(d.obra)}.",
            "Precio total: S/ ${soles(d.total)}. Adelanto recibido: S/ ${soles(d.adelantoReal)}. " +
                "Saldo pendiente: S/ ${soles(d.saldo)}, a pagarse $cuandoSaldo.",
            "${d.condiciones.ciudad.trim().takeIf { it.isNotEmpty() }?.let { "$it, " }.orEmpty()}" +
                fechaLarga(d.fecha) + "."
        )
    }
}
