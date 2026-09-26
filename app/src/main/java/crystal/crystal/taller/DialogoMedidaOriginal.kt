package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

/**
 * La medida original de MedidaActivity (el boceto con todas sus cotas), en grande, con pellizco
 * para zoom y arrastre para desplazar.
 *
 * El diseño de una calculadora redondea y oculta medidas para no ensuciarse; esto deja ver la
 * medida tal como se tomó. La usan Nova (doble toque sobre el dibujo) y novaDiseño (su chip).
 */
object DialogoMedidaOriginal {

    /** Carga el boceto guardado en [ruta]; null si no hay o no se puede leer. */
    fun cargar(context: Context, ruta: String?): Drawable? {
        if (ruta.isNullOrBlank()) return null
        val bmp = runCatching { BitmapFactory.decodeFile(ruta) }.getOrNull() ?: return null
        return BitmapDrawable(context.resources, bmp)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun mostrar(context: Context, original: Drawable?) {
        if (original == null) {
            Toast.makeText(context, "No hay medida original para mostrar", Toast.LENGTH_SHORT).show()
            return
        }
        val imagen = ImageView(context).apply {
            setImageDrawable(original)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setBackgroundColor(Color.WHITE)
            minimumHeight = (context.resources.displayMetrics.heightPixels * 0.6f).toInt()
        }

        var escala = 1f
        val zoom = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(d: ScaleGestureDetector): Boolean {
                escala = (escala * d.scaleFactor).coerceIn(1f, 6f)
                imagen.scaleX = escala
                imagen.scaleY = escala
                if (escala == 1f) {
                    imagen.translationX = 0f
                    imagen.translationY = 0f
                }
                return true
            }
        })
        var ultimoX = 0f
        var ultimoY = 0f
        imagen.setOnTouchListener { _, e ->
            zoom.onTouchEvent(e)
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    ultimoX = e.rawX
                    ultimoY = e.rawY
                }
                MotionEvent.ACTION_MOVE -> if (escala > 1f && e.pointerCount == 1) {
                    imagen.translationX += e.rawX - ultimoX
                    imagen.translationY += e.rawY - ultimoY
                    ultimoX = e.rawX
                    ultimoY = e.rawY
                }
            }
            true
        }

        AlertDialog.Builder(context)
            .setTitle("Medida original")
            .setView(imagen)
            .setPositiveButton("Cerrar", null)
            .show()
    }
}
