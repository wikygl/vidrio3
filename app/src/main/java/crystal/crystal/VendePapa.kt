package crystal.crystal

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityVendePapaBinding
import java.util.Locale

class VendePapa : AppCompatActivity() {

    private lateinit var binding: ActivityVendePapaBinding
    private val prefs by lazy { getSharedPreferences("VendePapaPrefs", MODE_PRIVATE) }
    private val historial = mutableListOf<String>()

    // IGV configurable (por defecto 18% para Perú). Se puede cambiar desde el botón IGV.
    private fun igvPorcentaje(): Double = prefs.getFloat("igv_porcentaje", 18f).toDouble()
    private fun igvFactor(): Double = igvPorcentaje() / 100.0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVendePapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepararTextos()
        configurarCalculadora()
        traido()

        // Los strings de datos ingresados y de operación achican su letra cuando el texto no cabe.
        androidx.core.widget.TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            binding.resultado, 14, 34, 1, android.util.TypedValue.COMPLEX_UNIT_SP
        )
        androidx.core.widget.TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            binding.memo, 12, 22, 1, android.util.TypedValue.COMPLEX_UNIT_SP
        )
        // Tocar el historial muestra los cálculos previos.
        binding.hist.setOnClickListener { mostrarHistorial() }
    }

    @SuppressLint("SetTextI18n")
    private fun prepararTextos() {
        binding.sTo.text = "SubT"
        binding.igv.text = "IGV"
        binding.total.text = "Total"
        binding.c.text = "m2"
        binding.ce.text = "Kg"
        binding.u.text = "p2"
        binding.reset.text = "C"
        binding.borrar.text = "<"
        binding.dividir.text = "/"
        binding.multiplicar.text = "*"
        binding.porciento.text = "%"
        binding.pare.text = "("
        binding.paren.text = ")"
        binding.masMenos.text = "+/-"
        binding.hist.text = "Calculadora de vidrieria"
        ajustarTamanosBotones()
    }

    private fun ajustarTamanosBotones() {
        listOf(
            binding.cero, binding.uno, binding.dos, binding.tres, binding.cuatro,
            binding.cinco, binding.seis, binding.siete, binding.ocho, binding.nueve,
            binding.punto, binding.borrar, binding.sumar, binding.restar,
            binding.multiplicar, binding.dividir, binding.igual, binding.pare,
            binding.paren
        ).forEach {
            it.textSize = 30f
            it.isAllCaps = false
        }

        listOf(
            binding.sTo, binding.igv, binding.total, binding.c, binding.ce,
            binding.u, binding.desc5, binding.desc10, binding.porciento,
            binding.masMenos, binding.reset
        ).forEach {
            it.textSize = 18f
            it.isAllCaps = false
            it.setSingleLine(false)
        }
    }

    private fun configurarCalculadora() {
        binding.cero.setOnClickListener { agregarDigito("0") }
        binding.uno.setOnClickListener { agregarDigito("1") }
        binding.dos.setOnClickListener { agregarDigito("2") }
        binding.tres.setOnClickListener { agregarDigito("3") }
        binding.cuatro.setOnClickListener { agregarDigito("4") }
        binding.cinco.setOnClickListener { agregarDigito("5") }
        binding.seis.setOnClickListener { agregarDigito("6") }
        binding.siete.setOnClickListener { agregarDigito("7") }
        binding.ocho.setOnClickListener { agregarDigito("8") }
        binding.nueve.setOnClickListener { agregarDigito("9") }
        binding.punto.setOnClickListener { agregarPunto() }

        binding.sumar.setOnClickListener { agregarOperador("+") }
        binding.restar.setOnClickListener { agregarOperador("-") }
        binding.multiplicar.setOnClickListener { agregarOperador("*") }
        binding.dividir.setOnClickListener { agregarOperador("/") }
        binding.porciento.setOnClickListener { agregarPorcentaje() }
        binding.pare.setOnClickListener { agregarParentesis("(") }
        binding.paren.setOnClickListener { agregarParentesis(")") }

        binding.borrar.setOnClickListener { borrarUltimo() }
        binding.reset.setOnClickListener { limpiar() }
        binding.igual.setOnClickListener { calcularIgual() }
        binding.masMenos.setOnClickListener { cambiarSigno() }

        binding.desc5.setOnClickListener { aplicarDescuento(5.0) }
        binding.desc10.setOnClickListener { aplicarDescuento(10.0) }
        binding.igv.setOnClickListener { dialogoIgv() }
        binding.total.setOnClickListener { calcularTotalConIgv() }
        binding.sTo.setOnClickListener { calcularSubtotalDesdeTotal() }
        binding.c.setOnClickListener { mostrarDialogoArea() }
        binding.ce.setOnClickListener { mostrarDialogoPesoVidrio() }
        binding.u.setOnClickListener { convertirMetrosAPies() }
    }

    private fun agregarDigito(digito: String) {
        val actual = textoPantalla()
        val nuevo = if (actual == "0") digito else actual + digito
        ponerPantalla(nuevo)
    }

    private fun agregarPunto() {
        val actual = textoPantalla()
        val segmento = actual.substringAfterLastOperator()
        if (!segmento.contains(".")) {
            ponerPantalla(actual + if (segmento.isEmpty() || segmento == "-") "0." else ".")
        }
    }

    private fun agregarOperador(operador: String) {
        val actual = textoPantalla()
        if (actual.isBlank()) {
            if (operador == "-") ponerPantalla("-")
            return
        }

        val nuevo = if (actual.last().isOperator() || actual.last() == '.') {
            actual.dropLast(1) + operador
        } else {
            actual + operador
        }
        ponerPantalla(nuevo)
    }

    private fun agregarPorcentaje() {
        val actual = textoPantalla()
        if (actual.isNotBlank() && actual.last().isDigit()) {
            ponerPantalla("$actual%")
        }
    }

    private fun agregarParentesis(parentesis: String) {
        val actual = textoPantalla()
        if (parentesis == "(") {
            val nuevo = if (actual.lastOrNull()?.isDigit() == true || actual.lastOrNull() == ')') {
                "$actual*("
            } else {
                "$actual("
            }
            ponerPantalla(nuevo)
        } else if (puedeCerrarParentesis(actual)) {
            ponerPantalla("$actual)")
        }
    }

    private fun borrarUltimo() {
        val actual = textoPantalla()
        if (actual.isNotEmpty()) {
            ponerPantalla(actual.dropLast(1))
        }
    }

    private fun limpiar() {
        ponerPantalla("")
        binding.memo.text = ""
        binding.hist.text = "Calculadora de vidrieria"
    }

    private fun calcularIgual() {
        val expresion = textoPantalla()
        if (expresion.isBlank()) return
        val resultado = evaluarActual() ?: return
        binding.memo.text = "$expresion ="
        ponerPantalla(df(resultado))
        historial.add(0, "$expresion = ${df(resultado)}")
        if (historial.size > 50) historial.removeAt(historial.lastIndex)
    }

    private fun cambiarSigno() {
        val actual = textoPantalla()
        if (actual.isBlank()) {
            ponerPantalla("-")
            return
        }
        val valor = evaluarActual() ?: return
        ponerPantalla(df(valor * -1.0))
        binding.memo.text = "Cambio de signo"
    }

    private fun aplicarDescuento(porcentaje: Double) {
        val base = evaluarActual() ?: return
        val descuento = base * porcentaje / 100.0
        val total = base - descuento
        ponerPantalla(df(total))
        binding.memo.text = "Descuento ${df(porcentaje)}%"
        mostrarEnHist("Base = ${df(base)}\nDescuento = ${df(descuento)}\nTotal = ${df(total)}")
    }

    private fun calcularIgv() {
        val base = evaluarActual() ?: return
        val igv = base * igvFactor()
        binding.memo.text = "IGV de ${df(base)}"
        mostrarEnHist("Base = ${df(base)}\nIGV ${df(igvPorcentaje())}% = ${df(igv)}\nTotal = ${df(base + igv)}")
        ponerPantalla(df(igv))
    }

    private fun calcularTotalConIgv() {
        val base = evaluarActual() ?: return
        val igv = base * igvFactor()
        val total = base + igv
        binding.memo.text = "Total con IGV"
        mostrarEnHist("Base = ${df(base)}\nIGV ${df(igvPorcentaje())}% = ${df(igv)}\nTotal = ${df(total)}")
        ponerPantalla(df(total))
    }

    private fun calcularSubtotalDesdeTotal() {
        val total = evaluarActual() ?: return
        val base = total / (1.0 + igvFactor())
        val igv = total - base
        binding.memo.text = "Subtotal desde total"
        mostrarEnHist("Subtotal = ${df(base)}\nIGV ${df(igvPorcentaje())}% = ${df(igv)}\nTotal = ${df(total)}")
        ponerPantalla(df(base))
    }

    private fun convertirMetrosAPies() {
        val metros = evaluarActual() ?: return
        val pies = metros * PIES_POR_M2
        binding.memo.text = "m2 a p2"
        mostrarEnHist("m2 = ${df(metros)}\np2 = ${df(pies)}")
        ponerPantalla(df(pies))
    }

    private fun mostrarDialogoArea() {
        val campos = dialogoMedidas(
            titulo = "Area de vidrio",
            incluirEspesor = false
        )

        AlertDialog.Builder(this)
            .setTitle("Area y pies cuadrados")
            .setView(campos.layout)
            .setPositiveButton("Calcular") { _, _ ->
                val ancho = campos.ancho.valor()
                val alto = campos.alto.valor()
                val cantidad = campos.cantidad.valor(default = 1.0).coerceAtLeast(1.0)
                val retazo = campos.retazo.valor().coerceAtLeast(0.0)
                if (ancho <= 0.0 || alto <= 0.0) {
                    mostrarError("Ingresa ancho y alto validos")
                    return@setPositiveButton
                }

                val areaUnidad = areaM2(ancho, alto, retazo)
                val areaTotal = areaUnidad * cantidad
                val piesTotal = areaTotal * PIES_POR_M2
                binding.memo.text = "Area de vidrio"
                mostrarEnHist(buildString {
                    appendLine("Medida = ${df(ancho)} x ${df(alto)} cm")
                    if (retazo > 0.0) appendLine("Retazo = ${df(retazo)} cm por lado")
                    appendLine("Cantidad = ${df(cantidad)}")
                    appendLine("m2 unidad = ${df(areaUnidad)}")
                    appendLine("m2 total = ${df(areaTotal)}")
                    append("p2 total = ${df(piesTotal)}")
                })
                ponerPantalla(df(areaTotal))
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoPesoVidrio() {
        val campos = dialogoMedidas(
            titulo = "Peso de vidrio",
            incluirEspesor = true
        )

        AlertDialog.Builder(this)
            .setTitle("Peso de vidrio")
            .setView(campos.layout)
            .setPositiveButton("Calcular") { _, _ ->
                val ancho = campos.ancho.valor()
                val alto = campos.alto.valor()
                val espesor = campos.espesor?.valor(default = 6.0) ?: 6.0
                val cantidad = campos.cantidad.valor(default = 1.0).coerceAtLeast(1.0)
                val retazo = campos.retazo.valor().coerceAtLeast(0.0)
                if (ancho <= 0.0 || alto <= 0.0 || espesor <= 0.0) {
                    mostrarError("Ingresa ancho, alto y espesor validos")
                    return@setPositiveButton
                }

                val areaUnidad = areaM2(ancho, alto, retazo)
                val pesoUnidad = areaUnidad * espesor * KG_M2_POR_MM
                val pesoTotal = pesoUnidad * cantidad
                binding.memo.text = "Peso de vidrio"
                mostrarEnHist(buildString {
                    appendLine("Medida = ${df(ancho)} x ${df(alto)} cm")
                    if (retazo > 0.0) appendLine("Retazo = ${df(retazo)} cm por lado")
                    appendLine("Espesor = ${df(espesor)} mm")
                    appendLine("Cantidad = ${df(cantidad)}")
                    appendLine("m2 total = ${df(areaUnidad * cantidad)}")
                    appendLine("Kg unidad = ${df(pesoUnidad)}")
                    append("Kg total = ${df(pesoTotal)}")
                })
                ponerPantalla(df(pesoTotal))
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun dialogoMedidas(titulo: String, incluirEspesor: Boolean): CamposVidrio {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(6), dp(18), 0)
        }
        layout.addView(etiqueta(titulo))

        val ancho = campoDecimal("Ancho en cm", sugerenciaActual())
        val alto = campoDecimal("Alto en cm")
        val espesor = if (incluirEspesor) campoDecimal("Espesor en mm", "6") else null
        val cantidad = campoDecimal("Cantidad", "1")
        val retazo = campoDecimal("Retazo en cm", "0")

        layout.addView(ancho)
        layout.addView(alto)
        espesor?.let { layout.addView(it) }
        layout.addView(cantidad)
        layout.addView(retazo)

        return CamposVidrio(layout, ancho, alto, espesor, cantidad, retazo)
    }

    private fun etiqueta(texto: String): TextView {
        return TextView(this).apply {
            text = texto
            gravity = Gravity.START
            textSize = 14f
        }
    }

    private fun campoDecimal(hint: String, valor: String = ""): EditText {
        return EditText(this).apply {
            this.hint = hint
            setText(valor)
            inputType = InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_FLAG_DECIMAL or
                InputType.TYPE_NUMBER_FLAG_SIGNED
            setSingleLine(true)
            textSize = 16f
        }
    }

    private fun evaluarActual(): Double? {
        val expresion = textoPantalla()
        if (expresion.isBlank() || expresion == "-") return 0.0
        return runCatching {
            ExpressionParser(expresion).parse()
        }.onFailure {
            mostrarError(it.message ?: "Operacion invalida")
        }.getOrNull()
    }

    private fun areaM2(anchoCm: Double, altoCm: Double, retazoCm: Double): Double {
        return ((anchoCm + retazoCm) * (altoCm + retazoCm)) / 10000.0
    }

    private fun textoPantalla(): String = binding.resultado.text?.toString().orEmpty()

    private fun ponerPantalla(texto: String) {
        binding.resultado.text = texto
    }

    private fun sugerenciaActual(): String {
        return evaluarActual()?.takeIf { it > 0.0 }?.let { df(it) }.orEmpty()
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun puedeCerrarParentesis(texto: String): Boolean {
        if (texto.isBlank()) return false
        val abiertos = texto.count { it == '(' }
        val cerrados = texto.count { it == ')' }
        return abiertos > cerrados && (texto.last().isDigit() || texto.last() == ')' || texto.last() == '%')
    }

    private fun traido() {
        val monto: Intent = intent
        val cantidad = monto.getStringExtra("monto")
        val cantidadSinEspacios = cantidad
            ?.replace(" ", "")
            ?.replace(",", ".")
            ?.filter { it.isDigit() || it == '.' || it == '-' }
            .orEmpty()

        if (cantidadSinEspacios.isNotBlank()) {
            ponerPantalla(cantidadSinEspacios)
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun EditText.valor(default: Double = 0.0): Double {
        return text.toString().replace(",", ".").trim().toDoubleOrNull() ?: default
    }

    private fun String.substringAfterLastOperator(): String {
        val index = indexOfLast { it.isOperator() || it == '(' || it == ')' }
        return if (index >= 0) substring(index + 1) else this
    }

    private fun Char.isOperator(): Boolean = this == '+' || this == '-' || this == '*' || this == '/'

    private fun df(value: Double): String {
        val rounded = if (kotlin.math.abs(value) < 0.0000001) 0.0 else value
        val text = if (rounded % 1.0 == 0.0) {
            rounded.toLong().toString()
        } else {
            "%.2f".format(Locale.US, rounded).trimEnd('0').trimEnd('.')
        }
        return text.replace(",", ".")
    }

    private data class CamposVidrio(
        val layout: LinearLayout,
        val ancho: EditText,
        val alto: EditText,
        val espesor: EditText?,
        val cantidad: EditText,
        val retazo: EditText
    )

    private class ExpressionParser(raw: String) {
        private val input = raw
            .replace(",", ".")
            .replace("x", "*", ignoreCase = true)
            .replace("÷", "/")
            .replace("×", "*")
        private var pos = 0

        fun parse(): Double {
            val value = parseExpression()
            skipSpaces()
            if (pos != input.length) {
                throw IllegalArgumentException("Operacion invalida")
            }
            if (!value.isFinite()) {
                throw IllegalArgumentException("Resultado invalido")
            }
            return value
        }

        private fun parseExpression(): Double {
            var value = parseTerm()
            while (true) {
                skipSpaces()
                value = when {
                    match('+') -> value + parseTerm()
                    match('-') -> value - parseTerm()
                    else -> return value
                }
            }
        }

        private fun parseTerm(): Double {
            var value = parseFactor()
            while (true) {
                skipSpaces()
                value = when {
                    match('*') -> value * parseFactor()
                    match('/') -> {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw IllegalArgumentException("No se puede dividir entre cero")
                        value / divisor
                    }
                    else -> return value
                }
            }
        }

        private fun parseFactor(): Double {
            skipSpaces()
            var value = when {
                match('+') -> parseFactor()
                match('-') -> -parseFactor()
                match('(') -> {
                    val inner = parseExpression()
                    if (!match(')')) throw IllegalArgumentException("Falta cerrar parentesis")
                    inner
                }
                else -> parseNumber()
            }

            while (true) {
                skipSpaces()
                if (match('%')) {
                    value /= 100.0
                } else {
                    return value
                }
            }
        }

        private fun parseNumber(): Double {
            skipSpaces()
            val start = pos
            var hasPoint = false
            while (pos < input.length) {
                val c = input[pos]
                when {
                    c.isDigit() -> pos++
                    c == '.' && !hasPoint -> {
                        hasPoint = true
                        pos++
                    }
                    else -> break
                }
            }
            if (start == pos) throw IllegalArgumentException("Numero esperado")
            return input.substring(start, pos).toDoubleOrNull()
                ?: throw IllegalArgumentException("Numero invalido")
        }

        private fun match(char: Char): Boolean {
            skipSpaces()
            if (pos < input.length && input[pos] == char) {
                pos++
                return true
            }
            return false
        }

        private fun skipSpaces() {
            while (pos < input.length && input[pos].isWhitespace()) pos++
        }
    }

    private fun mostrarEnHist(texto: String) {
        binding.hist.text = texto
        if (texto.isNotBlank()) {
            historial.add(0, texto)               // el más reciente primero
            if (historial.size > 50) historial.removeAt(historial.lastIndex)
        }
    }

    private fun mostrarHistorial() {
        if (historial.isEmpty()) {
            Toast.makeText(this, "Aún no hay cálculos en el historial", Toast.LENGTH_SHORT).show()
            return
        }
        val etiquetas = historial.map { it.replace("\n", "  ·  ") }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Historial")
            .setItems(etiquetas) { _, i -> binding.hist.text = historial[i] }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun dialogoIgv() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(df(igvPorcentaje()))
            hint = "Porcentaje de IGV"
        }
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
            addView(input)
        }
        AlertDialog.Builder(this)
            .setTitle("IGV (%)")
            .setMessage("Por defecto 18% (Perú). Ingresa el porcentaje a usar.")
            .setView(cont)
            .setPositiveButton("Guardar") { _, _ ->
                val pct = input.text.toString().replace(",", ".").toFloatOrNull()
                if (pct == null || pct < 0f) {
                    Toast.makeText(this, "Porcentaje inválido", Toast.LENGTH_SHORT).show()
                } else {
                    prefs.edit().putFloat("igv_porcentaje", pct).apply()
                    Toast.makeText(this, "IGV: ${df(pct.toDouble())}%", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    companion object {
        private const val IGV = 0.18
        private const val PIES_POR_M2 = 11.1
        private const val KG_M2_POR_MM = 2.5
    }
}
