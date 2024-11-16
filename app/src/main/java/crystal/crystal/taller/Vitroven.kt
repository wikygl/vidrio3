package crystal.crystal.taller

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.R
import crystal.crystal.databinding.ActivityVitrovenBinding

@Suppress("IMPLICIT_CAST_TO_ANY")
class Vitroven : AppCompatActivity() {

    private var dire = 0
    private lateinit var imagen: ImageView
    private var clip = 9.6f
    private var cruce = 0.6
    private val holgura = 17
    private val jArmado = 2.25f
    var texto =""

    private lateinit var binding: ActivityVitrovenBinding

    @SuppressLint("SetTextI18n", "ResourceAsColor", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVitrovenBinding.inflate(layoutInflater)
        setContentView(binding.root)

    modelos()

        binding.btnCalcularV.setOnClickListener {
            try {
                vidrios()
                jamba()
                u()

            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }


        }

    private fun modelos(){

        binding.imagen.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.vitrovf)
            texto="vf"
        }

        binding.btVitrov.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.ic_vitrobasic)
            texto="v"
        }
        binding.btVitrovv.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.vitroven2v)
            texto="vv"}

        binding.btVitrofvf.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.vitroven3v)
            texto="fvf"
        }
        binding.btVitrobv.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.vitroven4)
            texto="vb"
        }
        binding.btVitrobvs.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.vitroven5)
            texto="bvm"
        }
        binding.ivDiseno.setOnClickListener {
            binding.ivDiseno.visibility = View.GONE
            binding.svModelos.visibility = View.VISIBLE
            if (binding.ivDiseno.rotation %360==90f) {binding.ivDiseno.rotation += -90f}
        }
        binding.ivDiseno.setOnLongClickListener {
            binding.ivDiseno.rotation += 90f
            dire =90
            true}
           }


// FUNCIONES MATERIALES
    private fun vidrios () {

        val vidrioR= if (residuo()<=4.5){"${df1(med1() - (jArmado*2))} x ${df1((10.1f + residuo()-1))} = 1\n" +
                "${df1(med1() - (jArmado*2))} x 10.1 = ${nClips()-1}"} else {
            "${df1(med1() - (jArmado*2))} x 10.1 = ${nClips()}\n" +
                    "${df1(med1() - (jArmado*2))} x ${df1(residuo()-1)} = 1"
        }
    val vidrio2= if (residuo()<=4.5){"${df1((med1()/2) - (jArmado))} x ${df1((10.1f + residuo()-1))} = 1\n" +
            "${df1((med1()/2) - (jArmado))} x 10.1 = ${nClips()-1}"} else {
        "${df1((med1()/2) - (jArmado))} x 10.1 = ${nClips()}\n" +
                "${df1((med1()/2) - (jArmado))} x ${df1(residuo()-1)} = 1"}

    val vidrio= when (texto){
        "v" -> vidrioR
        "vf" -> "${df1(med1()/2)} x ${df1(med2()-1.8f)} = 1\n" +
                vidrio2
            else -> {"${residuo()}"}
        }
    binding.tvVidrio.text = vidrio
    }

    @SuppressLint("SetTextI18n")
    private fun jamba(){
        val alto = med2()
        val altoVb=(alto/5)*3
        val altoBvm= (alto/4)*2
        binding.tvJamba.text= when(texto) {
            "v" ->  "${df1(alto)} = 2"
            "vf" -> "${df1(alto)} = 2"
            "vv" -> "${df1(alto)} = 4"
            "fvf"-> "${df1(alto)} = 2"
            "vb" -> "${df1(altoVb)} = 2"
            "bvm"-> "${df1(altoBvm)} = 2"
            else -> {""}
        }
    }

    @SuppressLint("SetTextI18n")
    private fun u(){
        val alto = med2()
        val ancho = med1()/2
        binding.tvU.text=when(texto) {
           "vf" ->"${df1(ancho)} = 2\n${df1(alto - 3f)} = 1"
            else -> {""}
        }
    }

    //FUNCIONES DE CONTROL
    private fun residuo(): Float {
        return med2()- (clip*nClips())
    }
    private fun nClips():Int{
        val n= (med2()/clip).toInt()
        return n
    }

    //FUNCIONES GENERALES
    private fun med1():Float{
        return binding.etAncho.text.toString().toFloat()

    }
    private fun med2():Float{
        return binding.etAlto.text.toString().toFloat()
    }

    private fun df1(defo: Float): String {
        return if (defo % 1 == 0f) {
            // Si es un número entero, muestra sin decimales
            defo.toInt().toString()
        } else {
            // Si tiene decimales, formatea con un decimal
            "%.1f".format(defo).replace(",", ".")
        }
    }

}







