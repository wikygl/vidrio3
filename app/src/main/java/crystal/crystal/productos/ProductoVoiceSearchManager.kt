package crystal.crystal.productos

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import java.util.*

/**
 * Manager para búsqueda por voz de PRODUCTOS
 * Similar a VoiceSearchManager de clientes pero adaptado
 */
class ProductoVoiceSearchManager(
    private val activity: Activity,
    private val repository: ProductoRepository
) {

    companion object {
        const val REQUEST_CODE_SPEECH_PRODUCTO = 1002
        private const val TAG = "ProductoVoiceSearch"
    }

    /**
     * Iniciar búsqueda por voz
     */
    fun iniciarBusquedaPorVoz() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "PE"))
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Di el producto a buscar")
        }

        activity.startActivityForResult(intent, REQUEST_CODE_SPEECH_PRODUCTO)
    }

    /**
     * Procesar resultado de búsqueda por voz
     */
    suspend fun procesarResultadoVoz(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        onResultado: (ResultadoBusqueda) -> Unit
    ) {
        if (requestCode != REQUEST_CODE_SPEECH_PRODUCTO) return

        if (resultCode != Activity.RESULT_OK || data == null) {
            onResultado(ResultadoBusqueda.Cancelado)
            return
        }

        val resultados = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val textoVoz = resultados?.firstOrNull()?.lowercase(Locale("es", "PE"))

        if (textoVoz.isNullOrBlank()) {
            onResultado(ResultadoBusqueda.Cancelado)
            return
        }

        Log.d(TAG, "🎤 Texto reconocido: '$textoVoz'")

        // Procesar búsqueda
        buscarProducto(textoVoz, onResultado)
    }

    /**
     * Buscar producto con el texto reconocido
     */
    private suspend fun buscarProducto(
        textoVoz: String,
        onResultado: (ResultadoBusqueda) -> Unit
    ) {
        try {
            // Limpiar texto
            val consulta = limpiarConsulta(textoVoz)

            if (consulta.isEmpty()) {
                onResultado(ResultadoBusqueda.NoEncontrado)
                return
            }

            Log.d(TAG, "🔍 Buscando: '$consulta'")

            // Buscar en base de datos
            val productos = repository.buscar(consulta, 5)

            when (productos.size) {
                0 -> {
                    Log.d(TAG, "❌ Sin resultados")
                    onResultado(ResultadoBusqueda.NoEncontrado)
                }
                1 -> {
                    Log.d(TAG, "✅ Producto único: ${productos[0].nombre}")
                    onResultado(ResultadoBusqueda.ProductoUnico(productos[0]))
                }
                else -> {
                    Log.d(TAG, "📋 ${productos.size} productos encontrados")
                    onResultado(ResultadoBusqueda.ProductosMultiples(productos))
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error buscando", e)
            onResultado(ResultadoBusqueda.NoEncontrado)
        }
    }

    /**
     * Limpiar consulta de búsqueda
     */
    private fun limpiarConsulta(texto: String): String {
        var consulta = texto.lowercase(Locale("es", "PE")).trim()

        // Remover prefijos comunes
        val prefijos = listOf(
            "producto ",
            "buscar producto ",
            "buscar ",
            "vidrio ",
            "dame ",
            "quiero "
        )

        for (prefijo in prefijos) {
            if (consulta.startsWith(prefijo)) {
                consulta = consulta.removePrefix(prefijo)
            }
        }

        return consulta.trim()
    }

    /**
     * Resultado de búsqueda por voz
     */
    sealed class ResultadoBusqueda {
        data class ProductoUnico(val producto: Producto) : ResultadoBusqueda()
        data class ProductosMultiples(val productos: List<Producto>) : ResultadoBusqueda()
        object NoEncontrado : ResultadoBusqueda()
        object Cancelado : ResultadoBusqueda()
    }
}