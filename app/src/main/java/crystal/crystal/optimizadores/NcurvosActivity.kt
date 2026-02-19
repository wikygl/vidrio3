package crystal.crystal.optimizadores

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import crystal.crystal.R
import kotlin.math.*

class NcurvosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ncurvos)

        val drawingView = findViewById<ArcDrawingView>(R.id.arcDrawingView)
        val tvError = findViewById<AppCompatTextView>(R.id.tvError)
        val etDes = findViewById<AppCompatEditText>(R.id.etDesarrollo)
        val etCue = findViewById<AppCompatEditText>(R.id.etCuerda)
        val etFle = findViewById<AppCompatEditText>(R.id.etFlecha)
        val etLar = findViewById<AppCompatEditText>(R.id.etLargo)
        val btn = findViewById<AppCompatButton>(R.id.btnDibujar)

        btn.setOnClickListener {
            val dIn = etDes.text.toString().toFloatOrNull() ?: 0f
            val cIn = etCue.text.toString().toFloatOrNull() ?: 0f
            val fIn = etFle.text.toString().toFloatOrNull() ?: 0f
            val lIn = etLar.text.toString().toFloatOrNull() ?: 0f

            if (cIn > 0 && fIn > 0) {
                // Fórmulas de arco circular
                // Radio: R = (f/2) + (c^2 / 8f)
                val radio = (fIn / 2) + (cIn.pow(2) / (8 * fIn))
                // Ángulo central en radianes: α = 2 * arcsin(c / 2R)
                val angulo = 2 * asin(cIn / (2 * radio))
                // Desarrollo teórico: D = R * α
                val dTeorico = radio * angulo

                val diferencia = abs(dIn - dTeorico)

                if (dIn > 0 && diferencia > 0.5f) { // Umbral de 0.5 unidades
                    tvError.text = "¡Incongruencia detectada!\nPara C:$cIn y F:$fIn, el Desarrollo debería ser ${"%.2f".format(dTeorico)}. (Error: ${"%.2f".format(diferencia)})"
                } else {
                    tvError.text = "Geometría válida"
                }

                drawingView.updateData(dIn, cIn, fIn, lIn)
            }
        }
    }
}