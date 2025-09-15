package crystal.crystal.dictado

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import crystal.crystal.Listado
import crystal.crystal.R
import crystal.crystal.databinding.ActivityDictadoBinding
import java.util.Locale

@Suppress("UNREACHABLE_CODE")
class DictadoActivity : AppCompatActivity(), RecognitionListener {

    private lateinit var binding: ActivityDictadoBinding
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent

    // Estados del dictado
    private enum class EstadoDictado {
        ESPERANDO_INICIO,    // Esperando "Iniciar grabación"
        GRABANDO,            // Procesando comandos de medidas
        CONFIRMANDO,         // Confirmando elemento capturado
        ESPERANDO_APROBADO   // Esperando "Aprobado" para finalizar
    }

    private var estadoActual = EstadoDictado.ESPERANDO_INICIO
    private var isListening = false

    // Lista de elementos capturados
    private val elementosCapturados = ArrayList<Listado>()

    // Elemento temporal en construcción
    private var elementoTemporal: ElementoTemporal? = null

    // Constantes
    private companion object {
        const val RECORD_REQUEST_CODE = 101
    }

    // Clase para manejar elemento en construcción
    private data class ElementoTemporal(
        var medida1: Float? = null,
        var medida2: Float? = null,
        var cantidad: Float? = null,
        var producto: String = "",
        var textoCompleto: String = ""
    ) {
        fun estaCompleto(): Boolean {
            return medida1 != null && medida2 != null && cantidad != null && producto.isNotEmpty()
        }

        fun toListado(): Listado {
            return Listado(
                escala = TODO(),
                uni = TODO(),
                medi1 = TODO(),
                medi2 = TODO(),
                medi3 = TODO(),
                canti = TODO(),
                piescua = TODO(),
                precio = TODO(),
                costo = TODO(),
                producto = TODO(),
                peri = TODO(),
                metcua = TODO(),
                metli = TODO(),
                metcub = TODO(),
                color = TODO(),
                uri = TODO()
            ).apply {
                medi1 = medida1 ?: 0f
                medi2 = medida2 ?: 0f
                canti = cantidad ?: 0f
                producto = this@ElementoTemporal.producto
                precio = 0f
                piescua = medi1 * medi2
                costo = piescua * precio
                escala = "p2" // Por defecto pies cuadrados
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDictadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        checkPermissions()
        initializeSpeechRecognizer()
    }

    private fun setupUI() {
        // Botón volver
        binding.btnVolver.setOnClickListener {
            finish()
        }

        // Botón micrófono
        binding.btnMicrofono.setOnClickListener {
            toggleListening()
        }

        // Botón cancelar
        binding.btnCancelar.setOnClickListener {
            cancelarDictado()
        }

        // Botón finalizar
        binding.btnFinalizar.setOnClickListener {
            finalizarDictado()
        }

        actualizarUI()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_REQUEST_CODE
            )
        }
    }

    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer.setRecognitionListener(this)

            speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
        } else {
            Toast.makeText(this, "Reconocimiento de voz no disponible", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun toggleListening() {
        if (isListening) {
            stopListening()
        } else {
            startListening()
        }
    }

    private fun startListening() {
        if (!isListening) {
            speechRecognizer.startListening(speechRecognizerIntent)
            isListening = true
            actualizarUI()
        }
    }

    private fun stopListening() {
        if (isListening) {
            speechRecognizer.stopListening()
            isListening = false
            actualizarUI()
        }
    }

    private fun restartListening() {
        if (isListening) {
            speechRecognizer.cancel()
            speechRecognizer.startListening(speechRecognizerIntent)
        }
    }

    private fun actualizarUI() {
        when (estadoActual) {
            EstadoDictado.ESPERANDO_INICIO -> {
                binding.tvEstado.text = if (isListening) "🎤 Escuchando... Di 'Iniciar grabación'" else "🔴 Presiona para iniciar"
                binding.tvInstrucciones.text = "Di 'Iniciar grabación' para comenzar"
                binding.btnMicrofono.setImageResource(if (isListening) R.drawable.ic_mic_active else R.drawable.ic_mic)
            }
            EstadoDictado.GRABANDO -> {
                binding.tvEstado.text = "🟢 Grabando medidas"
                binding.tvInstrucciones.text = "Di: '[número] por [número] igual [cantidad], [producto]'"
                binding.btnMicrofono.setImageResource(R.drawable.ic_mic_active)
            }
            EstadoDictado.CONFIRMANDO -> {
                binding.tvEstado.text = "⚠️ Confirma el elemento"
                binding.tvInstrucciones.text = "Di 'Sí' para confirmar o 'No' para descartar"
                binding.btnMicrofono.setImageResource(R.drawable.ic_mic_active)
            }
            EstadoDictado.ESPERANDO_APROBADO -> {
                binding.tvEstado.text = "✅ Di 'Aprobado' para finalizar"
                binding.tvInstrucciones.text = "O continúa dictando más elementos"
                binding.btnMicrofono.setImageResource(R.drawable.ic_mic_active)
            }
        }

        // Actualizar botón finalizar
        binding.btnFinalizar.isEnabled = elementosCapturados.isNotEmpty()

        // Actualizar lista de elementos
        actualizarListaElementos()
    }

    private fun actualizarListaElementos() {
        if (elementosCapturados.isEmpty()) {
            binding.tvElementosCapturados.text = "Ningún elemento capturado aún"
        } else {
            val texto = StringBuilder()
            elementosCapturados.forEachIndexed { index, elemento ->
                texto.append("${index + 1}. ${elemento.medi1} x ${elemento.medi2} = ${elemento.canti}, ${elemento.producto}\n")
            }
            binding.tvElementosCapturados.text = texto.toString()
        }
    }

    private fun procesarTextoReconocido(texto: String) {
        binding.tvTextoReconocido.text = texto

        when (estadoActual) {
            EstadoDictado.ESPERANDO_INICIO -> {
                if (contieneFrase(texto, listOf("iniciar grabación", "iniciar grabacion", "empezar", "comenzar"))) {
                    estadoActual = EstadoDictado.GRABANDO
                    elementoTemporal = ElementoTemporal()
                    Toast.makeText(this, "✅ Iniciando grabación", Toast.LENGTH_SHORT).show()
                    actualizarUI()
                    restartListening()
                }
            }

            EstadoDictado.GRABANDO -> {
                if (contieneFrase(texto, listOf("aprobado", "finalizar", "terminar"))) {
                    estadoActual = EstadoDictado.ESPERANDO_APROBADO
                    actualizarUI()
                } else {
                    procesarComandoMedida(texto)
                }
            }

            EstadoDictado.CONFIRMANDO -> {
                if (contieneFrase(texto, listOf("sí", "si", "correcto", "está bien", "ok"))) {
                    confirmarElemento()
                } else if (contieneFrase(texto, listOf("no", "incorrecto", "mal", "cancelar"))) {
                    descartarElemento()
                }
            }

            EstadoDictado.ESPERANDO_APROBADO -> {
                if (contieneFrase(texto, listOf("aprobado", "finalizar", "terminar"))) {
                    finalizarDictado()
                } else {
                    // Permitir dictar más elementos
                    estadoActual = EstadoDictado.GRABANDO
                    elementoTemporal = ElementoTemporal()
                    procesarComandoMedida(texto)
                }
            }
        }
    }

    private fun procesarComandoMedida(texto: String) {
        try {
            val textoLimpio = texto.lowercase().trim()

            // Buscar patrón: número + por/x + número + igual + número + producto
            val regex = """(\d+(?:[.,]\d+)?)\s*(?:por|x|×)\s*(\d+(?:[.,]\d+)?)\s*(?:igual|=)\s*(\d+(?:[.,]\d+)?)[,\s]*(.*)""".toRegex()
            val match = regex.find(textoLimpio)

            if (match != null) {
                val medida1 = match.groupValues[1].replace(",", ".").toFloatOrNull()
                val medida2 = match.groupValues[2].replace(",", ".").toFloatOrNull()
                val cantidad = match.groupValues[3].replace(",", ".").toFloatOrNull()
                val producto = match.groupValues[4].trim()

                if (medida1 != null && medida2 != null && cantidad != null && producto.isNotEmpty()) {
                    elementoTemporal = ElementoTemporal(
                        medida1 = medida1,
                        medida2 = medida2,
                        cantidad = cantidad,
                        producto = producto,
                        textoCompleto = texto
                    )

                    mostrarConfirmacion()
                }
            } else {
                // Intentar procesar elementos individuales
                procesarElementoIndividual(textoLimpio)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error procesando comando: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun procesarElementoIndividual(texto: String) {
        // Buscar números en el texto
        val numeros = """(\d+(?:[.,]\d+)?)""".toRegex().findAll(texto).map {
            it.value.replace(",", ".").toFloatOrNull()
        }.filterNotNull().toList()

        if (elementoTemporal == null) {
            elementoTemporal = ElementoTemporal()
        }

        elementoTemporal?.let { elemento ->
            when {
                elemento.medida1 == null && numeros.isNotEmpty() -> {
                    elemento.medida1 = numeros[0]
                    Toast.makeText(this, "Primera medida: ${numeros[0]}", Toast.LENGTH_SHORT).show()
                }
                elemento.medida2 == null && numeros.isNotEmpty() -> {
                    elemento.medida2 = numeros[0]
                    Toast.makeText(this, "Segunda medida: ${numeros[0]}", Toast.LENGTH_SHORT).show()
                }
                elemento.cantidad == null && numeros.isNotEmpty() -> {
                    elemento.cantidad = numeros[0]
                    Toast.makeText(this, "Cantidad: ${numeros[0]}", Toast.LENGTH_SHORT).show()
                }
                elemento.producto.isEmpty() -> {
                    val palabrasProducto = texto.split(" ").filter {
                        !it.matches("""\d+(?:[.,]\d+)?""".toRegex()) &&
                                !it.contains("por") && !it.contains("igual")
                    }
                    if (palabrasProducto.isNotEmpty()) {
                        elemento.producto = palabrasProducto.joinToString(" ")
                        Toast.makeText(this, "Producto: ${elemento.producto}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if (elemento.estaCompleto()) {
                mostrarConfirmacion()
            }
        }
    }

    private fun mostrarConfirmacion() {
        elementoTemporal?.let { elemento ->
            estadoActual = EstadoDictado.CONFIRMANDO
            val mensaje = "Escuché: ${elemento.medida1} por ${elemento.medida2} igual ${elemento.cantidad}, ${elemento.producto}. ¿Es correcto?"

            // Usar TTS para confirmar (opcional)
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

            actualizarUI()
            restartListening()
        }
    }

    private fun confirmarElemento() {
        elementoTemporal?.let { elemento ->
            val listado = elemento.toListado()
            elementosCapturados.add(listado)

            Toast.makeText(this, "✅ Elemento agregado", Toast.LENGTH_SHORT).show()

            estadoActual = EstadoDictado.ESPERANDO_APROBADO
            elementoTemporal = null

            actualizarUI()
            restartListening()
        }
    }

    private fun descartarElemento() {
        elementoTemporal = null
        estadoActual = EstadoDictado.GRABANDO

        Toast.makeText(this, "❌ Elemento descartado", Toast.LENGTH_SHORT).show()

        actualizarUI()
        restartListening()
    }

    private fun contieneFrase(texto: String, frases: List<String>): Boolean {
        val textoLimpio = texto.lowercase().trim()
        return frases.any { frase -> textoLimpio.contains(frase.lowercase()) }
    }

    private fun cancelarDictado() {
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun finalizarDictado() {
        if (elementosCapturados.isNotEmpty()) {
            val intent = Intent()
            intent.putExtra("elementos_dictados", elementosCapturados)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            Toast.makeText(this, "No hay elementos para enviar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
    }

    // Implementación de RecognitionListener
    override fun onReadyForSpeech(params: Bundle?) {
        // Ready to listen
    }

    override fun onBeginningOfSpeech() {
        // Speech input has begun
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Volume level changed
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        // Audio buffer received
    }

    override fun onEndOfSpeech() {
        // User stopped speaking
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
            SpeechRecognizer.ERROR_CLIENT -> "Error del cliente"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permisos insuficientes"
            SpeechRecognizer.ERROR_NETWORK -> "Error de red"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Timeout de red"
            SpeechRecognizer.ERROR_NO_MATCH -> "No se encontraron coincidencias"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconocedor ocupado"
            SpeechRecognizer.ERROR_SERVER -> "Error del servidor"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Timeout de voz"
            else -> "Error desconocido: $error"
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()

        // Reintentar si es un error temporal
        if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
            restartListening()
        }
    }

    override fun onResults(results: Bundle?) {
        results?.let { bundle ->
            val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                procesarTextoReconocido(matches[0])
            }
        }
        restartListening()
    }

    override fun onPartialResults(partialResults: Bundle?) {
        partialResults?.let { bundle ->
            val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                binding.tvTextoReconocido.text = "Parcial: ${matches[0]}"
            }
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        // Speech event
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso denegado. No se puede usar el dictado.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}