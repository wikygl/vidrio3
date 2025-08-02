package crystal.crystal.taller

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityPivotAlBinding

class PivotAl : AppCompatActivity() {

    private lateinit var binding: ActivityPivotAlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPivotAlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalcularP.setOnClickListener {
            marco()
            parante()
            zocalo()
            vidrio()
            portafelpa()
        }
    }
    // FUNCIONES REDONDEOS
    private fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    @SuppressLint("SetTextI18n")
    private fun marco(){
        val alto = binding.etAltoP.text.toString().toFloat()
        binding.tvMarcoResult.text= "${df1(alto)} = 2\n${df1(anchUtil())} = 2"

    }
    @SuppressLint("SetTextI18n")
    private fun parante(){
        val p= paran()
        binding.tvParante.text="${df1(p)} = 2"
    }

    @SuppressLint("SetTextI18n")
    private fun zocalo(){
        val z = zoc()
        binding.tvZocaloE.text="${df1(z)} = 2"
    }
    @SuppressLint("SetTextI18n")
    private fun portafelpa(){
        val alt=altoUtil()
        val anc=anchUtil()-0.6f
        binding.tvPortaP.text="${df1(alt)} = 2\n${df1(anc)} = 2"
    }

    @SuppressLint("SetTextI18n")
    private fun vidrio(){
        val an = paran()-7f
        val al = zoc()+1.5f
        binding.tvVidriosP.text= "${df1(an)} x ${df1(al)} =1"
    }

    //   FUNCIONES GENERALES

    private fun anchUtil(): Float {
        val ancho = binding.etAnchoP.text.toString().toFloat()
        val marco = binding.etMarcoP.text.toString().toFloat()
        return ancho - (2 * marco)

    }
    private fun altoUtil():Float{
        val alto = binding.etAltoP.text.toString().toFloat()
        val marco=binding.etMarcoP.text.toString().toFloat()
        return alto - (2 * marco)
    }

    private fun paran(): Float {
        return anchUtil() - 1.4f
    }

    private fun zoc(): Float {
        return (altoUtil() - 1.4f) - 5.8f
    }

}