package crystal.crystal.Diseno

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import crystal.crystal.R
import crystal.crystal.databinding.ActivityBarandaBinding
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class BarandaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBarandaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarandaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        binding.generarButton.setOnClickListener {
            if (validarCampos()) generarBaranda()
        }
    }

    private fun validarCampos(): Boolean {
        val campos = listOf(
            binding.anchoTotalEditText to "Ancho Total",
            binding.altoTotalEditText to "Alto Total",
            binding.cantidadVidriosEditText to "Cantidad de Vidrios",
            binding.espesorTuboParanteEditText to "Espesor Tubo Parante",
            binding.espesorTuboCabezalEditText to "Espesor Tubo Cabezal",
            binding.etCuello to "Espesor Cuello"
        )
        for ((campo, nombre) in campos) {
            if (campo.text.isNullOrEmpty()) {
                Toast.makeText(this, "Por favor ingrese $nombre", Toast.LENGTH_SHORT).show()
                campo.requestFocus()
                return false
            }
        }
        val cantidad = binding.cantidadVidriosEditText.text.toString().toIntOrNull() ?: 0
        if (cantidad <= 0) {
            Toast.makeText(this, "La cantidad de vidrios debe ser mayor a cero", Toast.LENGTH_SHORT).show()
            binding.cantidadVidriosEditText.requestFocus()
            return false
        }
        return true
    }

    private fun generarBaranda() {
        try {
            val anchoTotal      = binding.anchoTotalEditText.text.toString().toFloat()
            val altoTotal       = binding.altoTotalEditText.text.toString().toFloat()
            val cantidadVidrios = binding.cantidadVidriosEditText.text.toString().toInt()
            val espParante      = binding.espesorTuboParanteEditText.text.toString().toFloat()
            val espCabezal      = binding.espesorTuboCabezalEditText.text.toString().toFloat()
            val cuello          = binding.etCuello.text.toString().toFloat()

            binding.barandaPreviewContainer.removeAllViews()
            binding.barandaPreviewContainer.addView(
                crearVistaBaranda(anchoTotal, altoTotal, cantidadVidrios, espParante, espCabezal, cuello)
            )
        } catch (e: Exception) {
            Toast.makeText(this, "Error al generar baranda: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun crearVistaBaranda(
        anchoTotal: Float,
        altoTotal: Float,
        cantidadVidrios: Int,
        espesorTuboParante: Float,
        espesorTuboCabezal: Float,
        cuello: Float
    ): ViewGroup {
        // 1) Escala al contenedor
        val contW = binding.barandaPreviewContainer.width.toFloat()
        val contH = binding.barandaPreviewContainer.height.toFloat()
        val escala = min(contW / anchoTotal, contH / altoTotal) * 0.9f

        // 2) Pasar cm a px
        val anchoPx       = (anchoTotal * escala).toInt()
        val altoPx        = (altoTotal * escala).toInt()
        val cabezalAltPx  = (espesorTuboCabezal * escala).toInt()
        val cuerpoAltPx   = altoPx - cabezalAltPx

        // 3) Crear el cabezal
        val cabezal = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(anchoPx, cabezalAltPx)
            setBackgroundColor(ContextCompat.getColor(this@BarandaActivity, android.R.color.darker_gray))
        }

        // 4) Contenedor vertical global
        val area = LinearLayout(this).apply {
            layoutParams  = LinearLayout.LayoutParams(anchoPx, altoPx)
            orientation   = LinearLayout.VERTICAL
        }
        area.addView(cabezal)

        // 5) Parámetros de margen y segmentación
        val margenCm      = 10f
        val margenPx      = (margenCm * escala).toInt()
        val interiorCm    = anchoTotal - 2 * margenCm
        val interiorPxF   = interiorCm * escala
        val segmentF      = interiorPxF / cantidadVidrios

        // 6) Dimensiones fijas en px
        val paranteCm     = max(espesorTuboParante, cuello)
        val parantePx     = (paranteCm * escala).toInt()
        val sapitoCm      = 3f
        val sapitoPx      = (sapitoCm * escala).toInt()
        val alturaCollarPx= (10f * escala).toInt()
        val altoVidPx     = ((altoTotal - espesorTuboCabezal - 2 * 10f) * escala).toInt()
        val colorTubo     = ContextCompat.getColor(this, android.R.color.darker_gray)
        val colorVidrio   = ContextCompat.getColor(this, android.R.color.holo_blue_light)
        val sapitoDrawable= ContextCompat.getDrawable(this, R.drawable.sapito)!!

        // 7) FrameLayout para posicionamiento absoluto del cuerpo
        val cuerpo = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(anchoPx, cuerpoAltPx)
        }

        // 8) Calcular X de cada parante
        val posiciones = FloatArray(cantidadVidrios + 1) { i ->
            if (i == cantidadVidrios)
            // última barra justo a cuerpoAltPx - parantePx, para no salirse del margen
                margenPx + interiorPxF - paranteCm * escala
            else
                margenPx + segmentF * i
        }

        // 9) Dibujar parantes (i=0..n) y, entre ellos, los vidrios (i<n)
        for (i in 0 .. cantidadVidrios) {
            // 9.1) Parante
            val xPar = posiciones[i].roundToInt()
            val contPar = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = FrameLayout.LayoutParams(parantePx, cuerpoAltPx).apply {
                    leftMargin = xPar
                }
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            }
            // cuello
            contPar.addView(View(this).apply {
                layoutParams = LinearLayout.LayoutParams((cuello * escala).toInt(), alturaCollarPx)
                setBackgroundColor(colorTubo)
            })
            // resto del tubo
            contPar.addView(View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (espesorTuboParante * escala).toInt(),
                    cuerpoAltPx - alturaCollarPx
                )
                setBackgroundColor(colorTubo)
            })
            cuerpo.addView(contPar)

            // 9.2) Vidrio + sapitos (solo si i < cantidadVidrios)
            if (i < cantidadVidrios) {
                // ancho hasta la siguiente barra
                val nextX = if (i + 1 == cantidadVidrios)
                    (margenPx + interiorPxF).roundToInt()
                else
                    posiciones[i + 1].roundToInt()

                val anchoSeccion = nextX - xPar - parantePx
                val contVid = FrameLayout(this).apply {
                    layoutParams = FrameLayout.LayoutParams(anchoSeccion, cuerpoAltPx).apply {
                        leftMargin = xPar + parantePx
                    }
                }
                // vidrio semitransparente
                contVid.addView(View(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        anchoSeccion - 2 * sapitoPx,
                        altoVidPx
                    ).apply {
                        leftMargin = sapitoPx
                        topMargin  = alturaCollarPx
                    }
                    alpha = 0.3f
                    setBackgroundColor(colorVidrio)
                })
                // los 4 sapitos
                val anchoImg = (6f  * escala).toInt()
                val altoImg  = (4f  * escala).toInt()
                val offsetY  = (2.5f* escala).toInt()
                listOf(
                    sapitoPx to alturaCollarPx + offsetY,
                    anchoSeccion - sapitoPx - anchoImg to alturaCollarPx + offsetY,
                    sapitoPx to alturaCollarPx + altoVidPx - offsetY - altoImg,
                    anchoSeccion - sapitoPx - anchoImg to alturaCollarPx + altoVidPx - offsetY - altoImg
                ).forEachIndexed { idx, (lm, tm) ->
                    contVid.addView(ImageView(this).apply {
                        layoutParams = FrameLayout.LayoutParams(anchoImg, altoImg).apply {
                            leftMargin = lm
                            topMargin  = tm
                        }
                        setImageDrawable(sapitoDrawable)
                        if (idx % 2 == 1) rotation = 180f
                    })
                }
                cuerpo.addView(contVid)
            }
        }

        // 10) Devolver todo
        area.addView(cuerpo)
        return area
    }

}


