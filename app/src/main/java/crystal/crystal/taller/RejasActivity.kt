package crystal.crystal.taller

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityRejasBinding

class RejasActivity : AppCompatActivity() {
    lateinit var binding: ActivityRejasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRejasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalcularNfcfs.setOnClickListener { res() }

    }

    private fun med1 ():Float{
        return binding.etMed1.text.toString().toFloat()
    }
    private fun med2 ():Float{
        return binding.etMed2.text.toString().toFloat()
    }
    private fun tubo ():Float {
        return binding.etTubo.text.toString().toFloat()
    }
    private fun divisiones ():Int {
        return binding.etDivi.text.toString().toInt()
    }
    private fun mDivisiones ():Float {
        val div = binding.etDivi.text.toString().toFloat()
        val nP= (med1()-(tubo()*(div+1)))/div

        return nP
    }
    @SuppressLint("SetTextI18n")
    private fun res () {
        val x = mDivisiones()-0.1f
        binding.txTubo.text= "${df1(med1())} = 2\n" +
                "${df1(med2())} = 2\n" +
                "${df1(med2()-(tubo()*2))} = ${divisiones()}\n" +
                "${df1(x)} = ${divisiones()}"
    }
    // FUNCIONES REDONDEOS
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
