package crystal.crystal.dictado

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import java.util.Locale

class DictadoMedidas {

    companion object {
        const val REQUEST_CODE = 2001
        private const val TAG = "DictadoMedidas"

        private val PALABRAS_NUMEROS = mapOf(
            "cero" to 0f, "uno" to 1f, "una" to 1f, "dos" to 2f, "tres" to 3f,
            "cuatro" to 4f, "cinco" to 5f, "seis" to 6f, "siete" to 7f,
            "ocho" to 8f, "nueve" to 9f, "diez" to 10f, "once" to 11f,
            "doce" to 12f, "trece" to 13f, "catorce" to 14f, "quince" to 15f,
            "dieciséis" to 16f, "dieciseis" to 16f, "diecisiete" to 17f,
            "dieciocho" to 18f, "diecinueve" to 19f, "veinte" to 20f,
            "veintiuno" to 21f, "veintiún" to 21f, "veintidós" to 22f, "veintidos" to 22f,
            "veintitrés" to 23f, "veintitres" to 23f, "veinticuatro" to 24f,
            "veinticinco" to 25f, "veintiséis" to 26f, "veintiseis" to 26f,
            "veintisiete" to 27f, "veintiocho" to 28f, "veintinueve" to 29f,
            "treinta" to 30f, "cuarenta" to 40f, "cincuenta" to 50f,
            "sesenta" to 60f, "setenta" to 70f, "ochenta" to 80f,
            "noventa" to 90f, "cien" to 100f, "ciento" to 100f,
            "doscientos" to 200f, "trescientos" to 300f, "cuatrocientos" to 400f,
            "quinientos" to 500f, "seiscientos" to 600f, "setecientos" to 700f,
            "ochocientos" to 800f, "novecientos" to 900f, "mil" to 1000f
        )
    }

    // Texto acumulado entre reaperturas del diálogo
    private var textoAcumulado = ""

    data class Medida(
        val medida1: Float,
        val medida2: Float,
        val cantidad: Float,
        val producto: String?
    )

    sealed class Resultado {
        data class Exito(val medida: Medida, val textoOriginal: String) : Resultado()
        data class Incompleto(val textoAcumulado: String) : Resultado()
        object Cancelado : Resultado()
    }

    fun lanzar(activity: Activity) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-PE")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-PE")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 15000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L)
            if (textoAcumulado.isNotEmpty()) {
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Continúa... acumulado: $textoAcumulado")
            } else {
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Ej: 30 por 40 igual 5 vidrio siguiente")
            }
        }
        try {
            activity.startActivityForResult(intent, REQUEST_CODE)
        } catch (e: Exception) {
            Log.e(TAG, "Error lanzando reconocimiento: ${e.message}")
        }
    }

    fun resetear() {
        textoAcumulado = ""
    }

    fun procesarResultado(requestCode: Int, resultCode: Int, data: Intent?): Resultado? {
        if (requestCode != REQUEST_CODE) return null

        if (resultCode != Activity.RESULT_OK || data == null) {
            // Usuario canceló con botón atrás
            textoAcumulado = ""
            return Resultado.Cancelado
        }

        val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val textoNuevo = results?.firstOrNull() ?: return Resultado.Cancelado

        Log.d(TAG, "Texto nuevo: '$textoNuevo'")

        // Acumular texto
        textoAcumulado = if (textoAcumulado.isEmpty()) {
            textoNuevo
        } else {
            "$textoAcumulado $textoNuevo"
        }

        Log.d(TAG, "Texto acumulado: '$textoAcumulado'")

        // Verificar si contiene "siguiente"
        if (!textoAcumulado.lowercase().contains("siguiente") &&
            !textoAcumulado.lowercase().contains("listo")) {
            // No dijo "siguiente", devolver Incompleto para que reabra el diálogo
            return Resultado.Incompleto(textoAcumulado)
        }

        // Tiene "siguiente", parsear todo el texto acumulado
        val resultado = parsearTexto(textoAcumulado)
        textoAcumulado = "" // Resetear para la siguiente medida
        return resultado
    }

    private fun parsearTexto(texto: String): Resultado {
        // Primero normalizar (convertir palabras a números)
        val normalizado = normalizarTexto(texto.lowercase().trim())
        Log.d(TAG, "Normalizado: '$normalizado'")

        // Patrón: NUM por NUM igual NUM [todo lo que sea producto] siguiente
        // El producto puede contener números, letras, lo que sea
        val regex = """(\d+(?:[.,]\d+)?)\s*(?:por|x|×)\s*(\d+(?:[.,]\d+)?)\s*(?:igual|=)\s*(\d+(?:[.,]\d+)?)\s*(.*?)\s*(?:siguiente|listo)""".toRegex(RegexOption.IGNORE_CASE)
        val match = regex.find(normalizado)

        if (match != null) {
            val m1 = match.groupValues[1].replace(",", ".").toFloatOrNull()
            val m2 = match.groupValues[2].replace(",", ".").toFloatOrNull()
            val cant = match.groupValues[3].replace(",", ".").toFloatOrNull()
            val prod = limpiarProducto(match.groupValues[4])

            if (m1 != null && m2 != null && cant != null) {
                Log.d(TAG, "EXITO: $m1 x $m2 = $cant, prod=$prod")
                return Resultado.Exito(Medida(m1, m2, cant, prod), texto)
            }
        }

        // Fallback: buscar 3 números y todo lo que está entre el 3er número y "siguiente" es producto
        val resultado = parsearFallback(normalizado, texto)
        if (resultado != null) return resultado

        // Último intento con texto original sin normalizar
        val numerosDirectos = extraerNumerosDeTexto(texto.lowercase().trim())
        if (numerosDirectos.size >= 3) {
            return Resultado.Exito(
                Medida(numerosDirectos[0], numerosDirectos[1], numerosDirectos[2], null),
                texto
            )
        }

        return Resultado.Exito(Medida(0f, 0f, 0f, null), texto)
    }

    private fun parsearFallback(normalizado: String, textoOriginal: String): Resultado? {
        // Buscar posiciones de los primeros 3 números
        val matchesNumeros = """(\d+(?:[.,]\d+)?)""".toRegex().findAll(normalizado).toList()
        if (matchesNumeros.size < 3) return null

        val m1 = matchesNumeros[0].value.replace(",", ".").toFloatOrNull() ?: return null
        val m2 = matchesNumeros[1].value.replace(",", ".").toFloatOrNull() ?: return null
        val cant = matchesNumeros[2].value.replace(",", ".").toFloatOrNull() ?: return null

        // Todo lo que está entre el final del 3er número y "siguiente" es producto
        val finTercerNumero = matchesNumeros[2].range.last + 1
        val inicioSiguiente = normalizado.lowercase().indexOf("siguiente").takeIf { it >= 0 }
            ?: normalizado.lowercase().indexOf("listo").takeIf { it >= 0 }
            ?: normalizado.length

        val productoRaw = if (finTercerNumero < inicioSiguiente) {
            normalizado.substring(finTercerNumero, inicioSiguiente)
        } else ""

        val prod = limpiarProducto(productoRaw)

        Log.d(TAG, "EXITO fallback: $m1 x $m2 = $cant, prod=$prod")
        return Resultado.Exito(Medida(m1, m2, cant, prod), textoOriginal)
    }

    private fun normalizarTexto(texto: String): String {
        var resultado = texto
        resultado = resolverCompuestos(resultado)

        for ((palabra, numero) in PALABRAS_NUMEROS.entries.sortedByDescending { it.key.length }) {
            val valorEntero = numero.toInt()
            val reemplazo = if (numero == valorEntero.toFloat()) valorEntero.toString() else numero.toString()
            resultado = resultado.replace(palabra, reemplazo)
        }

        return resultado.replace("""\s+""".toRegex(), " ").trim()
    }

    private fun resolverCompuestos(texto: String): String {
        var resultado = texto

        val decenas = mapOf(
            "treinta" to 30, "cuarenta" to 40, "cincuenta" to 50,
            "sesenta" to 60, "setenta" to 70, "ochenta" to 80, "noventa" to 90
        )
        val unidades = mapOf(
            "uno" to 1, "una" to 1, "dos" to 2, "tres" to 3, "cuatro" to 4,
            "cinco" to 5, "seis" to 6, "siete" to 7, "ocho" to 8, "nueve" to 9
        )

        for ((decPalabra, decValor) in decenas) {
            for ((uniPalabra, uniValor) in unidades) {
                resultado = resultado.replace("$decPalabra y $uniPalabra", (decValor + uniValor).toString())
            }
        }

        return resultado
    }

    private fun extraerNumerosDeTexto(texto: String): List<Float> {
        val numeros = mutableListOf<Float>()
        val palabras = texto.split("""\s+""".toRegex())

        var i = 0
        while (i < palabras.size) {
            val palabra = palabras[i]

            palabra.replace(",", ".").toFloatOrNull()?.let {
                numeros.add(it)
                i++
                return@let
            } ?: run {
                val valorDirecto = PALABRAS_NUMEROS[palabra]
                if (valorDirecto != null) {
                    if (i + 2 < palabras.size && palabras[i + 1] == "y") {
                        val valorUnidad = PALABRAS_NUMEROS[palabras[i + 2]]
                        if (valorUnidad != null && valorDirecto >= 30 && valorUnidad < 10) {
                            numeros.add(valorDirecto + valorUnidad)
                            i += 3
                            return@run
                        }
                    }
                    numeros.add(valorDirecto)
                }
                i++
            }
        }
        return numeros
    }

    private fun contienePalabra(texto: String, palabras: List<String>): Boolean {
        return palabras.any { texto.contains(it) }
    }

    private fun limpiarProducto(texto: String): String? {
        val limpio = texto
            .replace("""[*×=+\-/\\|<>{}()\[\]#@!¡¿?,;:'"]+""".toRegex(), " ")
            .trim()
            .replace("""\s+""".toRegex(), " ")

        return limpio.takeIf { it.length >= 2 && it.any { c -> c.isLetter() } }
    }
}
