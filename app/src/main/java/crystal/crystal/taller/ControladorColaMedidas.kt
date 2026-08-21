package crystal.crystal.taller

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

/**
 * Controla, dentro de una calculadora, el flujo de la cola de medidas que viene de MedidaActivity.
 *
 * - Prellena ancho/alto con la medida entrante.
 * - Si la calculadora tiene [ivDiseno], muestra el gráfico original de la medida; con doble toque se
 *   ve en grande (con zoom por pellizco y arrastre), el arrastre horizontal alterna en el thumbnail
 *   entre diseño calculado y medida original, y se conservan el toque simple / toque largo originales.
 * - Instala el chip de [NavegadorCola], que permite abrir, omitir o editar cualquier medida del
 *   paquete sin tener que archivar la actual.
 * - Tras archivar, ofrece abrir la siguiente pendiente: si es de la misma calculadora, repuebla los
 *   campos; si es de otra, cierra esta y abre la que corresponde.
 *
 * Cada calculadora solo debe: construir el controlador en onCreate y llamar [inicializar]; llamar
 * [ofrecerSiguiente] al terminar de archivar; y (si tiene ivDiseno) llamar [onCalcular] al pulsar
 * Calcular. Es inofensivo cuando la calculadora NO fue abierta desde MedidaActivity ([activo] = false).
 */
class ControladorColaMedidas(
    private val activity: AppCompatActivity,
    private val claseActual: Class<out AppCompatActivity>,
    private val etAncho: EditText?,
    private val etAlto: EditText?,
    private val ivDiseno: ImageView? = null,
    private val onToqueSimple: (() -> Unit)? = null,
    private val onToqueLargo: (() -> Unit)? = null,
    private val formato: (Float) -> String = { f ->
        if (f % 1f == 0f) f.toInt().toString() else String.format(Locale.US, "%.1f", f)
    }
) {
    private var bocetoOriginal: Drawable? = null
    private var disenoGuardado: Drawable? = null
    private var mostrandoOriginal = false
    private var downX = 0f
    private var downY = 0f

    val activo: Boolean get() = ColaCalculadoras.desdeMedidas(activity)

    private val navegador by lazy {
        NavegadorCola(activity, claseActual, formato) { item, indice -> cargarMedida(item, indice) }
    }

    fun inicializar() {
        if (!activo) return
        activity.intent.getFloatExtra("ancho", -1f).let { if (it > 0) etAncho?.setText(formato(it)) }
        activity.intent.getFloatExtra("alto", -1f).let { if (it > 0) etAlto?.setText(formato(it)) }
        navegador.instalarChip()
        // El gráfico original y sus gestos solo aplican cuando la medida trae un boceto (viene de
        // MedidaActivity). Los presupuestos que llegan desde Taller/MainActivity no traen imagen, así
        // que ahí se conserva el comportamiento normal de ivDiseno.
        val path = ColaCalculadoras.bocetoPath(activity)
        if (ivDiseno != null && path.isNotBlank()) {
            configurarGestos(ivDiseno)
            cargarYMostrarOriginal(path)
        }
    }

    /** Llamar al inicio de "Calcular": vuelve del gráfico original al diseño calculado. */
    fun onCalcular() {
        restaurarDiseno()
    }

    /** Opcional: llamar cuando la calculadora fija un nuevo diseño en ivDiseno. */
    fun onDisenoActualizado() {
        mostrandoOriginal = false
    }

    /** Llamar justo después de completar el archivado. */
    fun ofrecerSiguiente() {
        navegador.ofrecerSiguiente()
    }

    /** Abre la lista de medidas del paquete (lo mismo que el chip flotante). */
    fun mostrarListaCola() {
        navegador.mostrarLista()
    }

    @SuppressLint("SetTextI18n")
    private fun cargarMedida(item: ColaCalculadoras.MedidaCalc, indice: Int) {
        activity.intent.putExtra(ColaCalculadoras.EXTRA_INDICE, indice)
        activity.intent.putExtra(ColaCalculadoras.EXTRA_BOCETO_PATH, item.bocetoArchivo)
        // Actualizar también cantidad y producto para que el archivado respete la cantidad correcta.
        activity.intent.putExtra("cantidad", item.cantidad)
        activity.intent.putExtra("producto", item.producto)
        etAncho?.setText(formato(item.ancho))
        etAlto?.setText(formato(item.alto))
        if (ivDiseno != null && item.bocetoArchivo.isNotBlank()) cargarYMostrarOriginal(item.bocetoArchivo)
        etAncho?.requestFocus()
        Toast.makeText(activity, "Medida cargada: revisa y calcula", Toast.LENGTH_SHORT).show()
    }

    // ---------------- gráfico original en ivDiseno ----------------

    private fun cargarYMostrarOriginal(path: String) {
        val iv = ivDiseno ?: return
        val bmp = if (path.isNotBlank()) BitmapFactory.decodeFile(path) else null
        bocetoOriginal = bmp?.let { BitmapDrawable(activity.resources, it) }
        if (bocetoOriginal == null) {
            mostrandoOriginal = false
            return
        }
        disenoGuardado = iv.drawable
        mostrarOriginal()
    }

    private fun mostrarOriginal() {
        val iv = ivDiseno ?: return
        val d = bocetoOriginal ?: return
        iv.scaleType = ImageView.ScaleType.FIT_CENTER
        iv.setImageDrawable(d)
        iv.visibility = View.VISIBLE
        mostrandoOriginal = true
    }

    private fun restaurarDiseno() {
        val iv = ivDiseno ?: return
        if (!mostrandoOriginal) return
        disenoGuardado?.let { iv.setImageDrawable(it) }
        mostrandoOriginal = false
    }

    private fun alternar() {
        val iv = ivDiseno ?: return
        if (bocetoOriginal == null) return
        if (mostrandoOriginal) {
            restaurarDiseno()
        } else {
            disenoGuardado = iv.drawable
            mostrarOriginal()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configurarGestos(iv: ImageView) {
        val umbral = 60f * activity.resources.displayMetrics.density
        val detector = GestureDetector(activity, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent) = true
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                onToqueSimple?.invoke()
                return true
            }
            override fun onDoubleTap(e: MotionEvent): Boolean {
                mostrarDialogoGrande()
                return true
            }
            override fun onLongPress(e: MotionEvent) {
                onToqueLargo?.invoke()
            }
        })
        iv.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    val dx = event.x - downX
                    val dy = event.y - downY
                    if (kotlin.math.abs(dx) > umbral && kotlin.math.abs(dx) > kotlin.math.abs(dy) * 1.5f) {
                        alternar()
                    }
                }
            }
            true
        }
    }

    /** Muestra la medida original en grande, con pellizco para zoom y arrastre para desplazar. */
    @SuppressLint("ClickableViewAccessibility")
    private fun mostrarDialogoGrande() {
        val d = bocetoOriginal ?: run {
            Toast.makeText(activity, "No hay medida original para mostrar", Toast.LENGTH_SHORT).show()
            return
        }
        val imagen = ImageView(activity).apply {
            setImageDrawable(d)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setBackgroundColor(Color.WHITE)
            minimumHeight = (activity.resources.displayMetrics.heightPixels * 0.6f).toInt()
        }

        var escala = 1f
        val zoom = ScaleGestureDetector(activity, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(dd: ScaleGestureDetector): Boolean {
                escala = (escala * dd.scaleFactor).coerceIn(1f, 6f)
                imagen.scaleX = escala
                imagen.scaleY = escala
                if (escala == 1f) {
                    imagen.translationX = 0f
                    imagen.translationY = 0f
                }
                return true
            }
        })
        var ux = 0f
        var uy = 0f
        imagen.setOnTouchListener { _, e ->
            zoom.onTouchEvent(e)
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    ux = e.rawX
                    uy = e.rawY
                }
                MotionEvent.ACTION_MOVE -> if (escala > 1f && e.pointerCount == 1) {
                    imagen.translationX += e.rawX - ux
                    imagen.translationY += e.rawY - uy
                    ux = e.rawX
                    uy = e.rawY
                }
            }
            true
        }

        AlertDialog.Builder(activity)
            .setTitle("Medida original")
            .setView(imagen)
            .setPositiveButton("Cerrar", null)
            .show()
    }
}
