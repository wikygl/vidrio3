package crystal.crystal.optimizadores

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityVidrioCurvoBinding
import kotlin.math.abs
import kotlin.math.asin

/**
 * Pantalla para disenar un vidrio curvo a partir de su desarrollo (arco), cuerda,
 * flecha y largo. Valida la coherencia geometrica entre desarrollo, cuerda y flecha,
 * y delega el dibujo isometrico a [VistaVidrioCurvo].
 */
class VidrioCurvoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVidrioCurvoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVidrioCurvoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btDibujar.setOnClickListener { dibujar() }
    }

    private fun dibujar() {
        val desarrollo = binding.etDesarrollo.text.toString().aFloat()
        val cuerda = binding.etCuerda.text.toString().aFloat()
        val flecha = binding.etFlecha.text.toString().aFloat()
        val largo = binding.etLargo.text.toString().aFloat()

        if (cuerda <= 0f || flecha <= 0f) {
            binding.tvMensaje.text = "Ingresa cuerda y flecha mayores a cero"
            return
        }

        val radio = (cuerda * cuerda) / (8f * flecha) + flecha / 2f
        val angulo = 2f * asin((cuerda / (2f * radio)).coerceIn(-1f, 1f))
        val desarrolloCalculado = radio * angulo
        val diferencia = abs(desarrollo - desarrolloCalculado)

        binding.tvMensaje.text = if (desarrollo > 0f && diferencia > 0.5f) {
            "El desarrollo no coincide con la cuerda y la flecha.\n" +
                "Para cuerda ${fmt(cuerda)} y flecha ${fmt(flecha)}, " +
                "el desarrollo deberia ser ${fmt(desarrolloCalculado)} (diferencia ${fmt(diferencia)})."
        } else {
            "Geometria valida"
        }

        binding.vistaCurvo.actualizarMedidas(desarrollo, cuerda, flecha, largo)
    }

    // Un decimal: son medidas, no dinero.
    private fun fmt(valor: Float): String = String.format(java.util.Locale.US, "%.1f", valor)

    private fun String.aFloat(): Float = trim().replace(",", ".").toFloatOrNull() ?: 0f
}
