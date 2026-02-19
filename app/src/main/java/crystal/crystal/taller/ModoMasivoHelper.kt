package crystal.crystal.taller

import android.app.Activity
import android.content.Intent
import android.widget.TextView

/**
 * Helper para que las calculadoras devuelvan resultados en modo masivo.
 */
object ModoMasivoHelper {

    fun esModoMasivo(activity: Activity): Boolean {
        return activity.intent.getBooleanExtra("modo_masivo", false)
    }

    /**
     * Empaqueta los textos de los TextViews de resultado y los devuelve a Taller.
     * Llamar después de calcular, cuando el usuario quiera avanzar al siguiente item.
     */
    fun devolverResultado(
        activity: Activity,
        calculadora: String,
        perfiles: Map<String, String>,
        vidrios: String,
        accesorios: Map<String, String>,
        referencias: String,
        disenoPaquete: String = ""
    ) {
        val intent = activity.intent
        val resultado = ResultadoCalculo(
            calculadora = calculadora,
            producto = intent.getStringExtra("producto") ?: "",
            cliente = intent.getStringExtra("rcliente") ?: "",
            ancho = intent.getFloatExtra("ancho", 0f),
            alto = intent.getFloatExtra("alto", 0f),
            perfiles = perfiles,
            vidrios = vidrios,
            accesorios = accesorios,
            referencias = referencias,
            colorAluminio = intent.getStringExtra("color_aluminio") ?: "",
            tipoVidrio = intent.getStringExtra("tipo_vidrio") ?: "",
            disenoPaquete = disenoPaquete,
            cantidad = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        )
        val data = Intent().putExtra("resultado", resultado)
        activity.setResult(Activity.RESULT_OK, data)
        activity.finish()
    }

    /**
     * Lee el texto de un TextView, devolviendo cadena vacía si está vacío o null.
     */
    fun texto(tv: TextView): String {
        return tv.text?.toString()?.trim() ?: ""
    }
}
