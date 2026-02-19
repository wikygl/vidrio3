package crystal.crystal.clientes

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Gestor de búsqueda por voz - CORREGIDO
 */
class VoiceSearchManager(
    private val activity: AppCompatActivity,
    private val clienteRepository: ClienteRepository
) {

    companion object {
        private const val TAG = "VoiceSearchManager"
        const val REQUEST_CODE_VOICE = 1001
        private val PALABRAS_CLIENTE = listOf("cliente", "clientes")
    }

    sealed class ResultadoBusqueda {
        data class ClienteUnico(val cliente: Cliente) : ResultadoBusqueda()
        data class ClientesMultiples(val clientes: List<Cliente>) : ResultadoBusqueda()
        data class Producto(val nombre: String) : ResultadoBusqueda()
        object NoEncontrado : ResultadoBusqueda()
        object Cancelado : ResultadoBusqueda()
    }

    private var onResultadoCallback: ((ResultadoBusqueda) -> Unit)? = null

    fun iniciarBusquedaPorVoz(onResultado: (ResultadoBusqueda) -> Unit) {
        this.onResultadoCallback = onResultado

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-PE")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Di el nombre del cliente o producto")
        }

        try {
            activity.startActivityForResult(intent, REQUEST_CODE_VOICE)
        } catch (e: Exception) {
            Log.e(TAG, "Error iniciando voz", e)
        }
    }

    fun procesarResultadoVoz(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode != REQUEST_CODE_VOICE) return false

        if (resultCode != Activity.RESULT_OK || data == null) {
            onResultadoCallback?.invoke(ResultadoBusqueda.Cancelado)
            return true
        }

        val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val textoCompleto = results?.get(0) ?: ""

        Log.d(TAG, "🎤 Reconocido: $textoCompleto")

        procesarComando(textoCompleto)
        return true
    }

    private fun procesarComando(texto: String) {
        val textoLimpio = texto.lowercase(Locale("es", "PE")).trim()

        val esCliente = PALABRAS_CLIENTE.any { textoLimpio.startsWith(it) }

        if (esCliente) {
            val consulta = PALABRAS_CLIENTE.fold(textoLimpio) { acc, palabra ->
                acc.removePrefix(palabra).trim()
            }

            Log.d(TAG, "🔍 Buscando CLIENTE: $consulta")
            buscarCliente(consulta)
        } else {
            Log.d(TAG, "🔍 Buscando PRODUCTO: $textoLimpio")
            onResultadoCallback?.invoke(ResultadoBusqueda.Producto(textoLimpio))
        }
    }

    private fun buscarCliente(consulta: String) {
        if (consulta.isEmpty()) {
            onResultadoCallback?.invoke(ResultadoBusqueda.NoEncontrado)
            return
        }

        activity.lifecycleScope.launch(Dispatchers.IO) {
            try {
                // ✅ CORREGIDO: Sin parámetro "limite", usa valor por defecto
                val resultados = clienteRepository.buscar(consulta, 5)

                withContext(Dispatchers.Main) {
                    when (resultados.size) {
                        0 -> {
                            Log.d(TAG, "❌ No se encontró: $consulta")
                            onResultadoCallback?.invoke(ResultadoBusqueda.NoEncontrado)
                        }
                        1 -> {
                            Log.d(TAG, "✅ Cliente único: ${resultados[0].nombreCompleto}")
                            onResultadoCallback?.invoke(ResultadoBusqueda.ClienteUnico(resultados[0]))
                        }
                        else -> {
                            Log.d(TAG, "📋 ${resultados.size} clientes encontrados")
                            onResultadoCallback?.invoke(ResultadoBusqueda.ClientesMultiples(resultados))
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error buscando cliente", e)
                withContext(Dispatchers.Main) {
                    onResultadoCallback?.invoke(ResultadoBusqueda.NoEncontrado)
                }
            }
        }
    }
}