package crystal.crystal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityVendePapaBinding

class VendePapa : AppCompatActivity() {
    private var num1=0F
    private var num2=0F
    private var num3="0.0"
    private var ope=0
    
    lateinit var binding: ActivityVendePapaBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVendePapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cero.setOnClickListener { numPresionado("0") }
        binding.uno.setOnClickListener { numPresionado("1") }
        binding.dos.setOnClickListener { numPresionado("2") }
        binding.tres.setOnClickListener { numPresionado("3") }
        binding.cuatro.setOnClickListener { numPresionado("4") }
        binding.cinco.setOnClickListener { numPresionado("5") }
        binding.seis.setOnClickListener { numPresionado("6") }
        binding.siete.setOnClickListener { numPresionado("7") }
        binding.ocho.setOnClickListener { numPresionado("8") }
        binding.nueve.setOnClickListener { numPresionado("9") }
        binding.punto.setOnClickListener { try { numPresionado(".") } catch (e: Exception){} }
        binding.borrar.setOnClickListener { if(binding.resultado.text.isNotEmpty()){
            binding.resultado.text = binding.resultado.text.substring(0, binding.resultado.text.length-1)}}

        binding.sumar.setOnClickListener {
            opPresionado(suma)
            binding.memo.text = "$num1 +"  }

        binding.restar.setOnClickListener {
            opPresionado(resta)
            binding.memo.text = "$num1 -" }

        binding.multiplicar.setOnClickListener {
            opPresionado(multiplicacion)
            binding.memo.text = "$num1 ×" }

        binding.dividir.setOnClickListener {
            opPresionado(division)
            binding.memo.text = "$num1 ÷"   }

        binding.desc5.setOnClickListener {opPresionado(porcinco) }
        binding.desc10.setOnClickListener {opPresionado(pordiez) }

        binding.reset.setOnClickListener {
            num1 = 0F
            num2 = 0F
            binding.resultado.text = ""
            ope = nada
            binding.memo.text = ""
            binding.resultado.hint = ""
        }
        binding.igual.setOnClickListener {
            num2 = if (binding.resultado.text.isNotEmpty()) {
                binding.resultado.text.toString().toFloat()
            } else {
                0F
            }
            val resultadoO = when (ope) {
                suma -> num1 + num2
                resta -> num1 - num2
                multiplicacion -> num1 * num2
                division -> num1 / num2
                porcinco -> num1 - (num1 * 5 / 100)
                pordiez -> num1 - (num1 * 10 / 100)
                else -> 0
            }
            binding.resultado.text = resultadoO.toString()
            when (ope) {
                suma -> binding.memo.text = "$num1 + $num2 ="
                resta -> binding.memo.text = "$num1 - $num2 ="
                multiplicacion -> binding.memo.text = "$num1 × $num2 ="
                division -> binding.memo.text = "$num1 ÷ $num2 ="
                porcinco -> binding.memo.text = "$num1 - (5)% ="
                pordiez -> binding.memo.text = "$num1 - (10)% ="
            }
        }
        binding.masMenos.setOnClickListener{
            val resultadom = binding.resultado.text.toString().toFloat()
            binding.resultado.text = (resultadom * -1).toString()
        }
        binding.total.setOnClickListener {
            val resul = binding.resultado.text.toString().toFloat()
            val sub =(resul / 1.18).toFloat()
            binding.hist.text = "sub.Total = ${df1(sub)}\nIGV = ${df1(resul-sub)}\nTotal = ${df1(resul)}"
        }

        traido()
    }
    @SuppressLint("SetTextI18n")
    private fun numPresionado(digito: String) {
        binding.resultado.text = "${binding.resultado.text}$digito"
        if (ope == nada) {
            num1 = if (binding.resultado.text.isNullOrEmpty()) 0F else binding.resultado.text.toString().toFloat()
        } else {
            num2 = binding.resultado.text.toString().toFloat()
        }
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
    
    private fun opPresionado(operacion: Int) {
        ope = operacion
        num1 = if (binding.resultado.text.isNullOrEmpty()) 0F else binding.resultado.text.toString().toFloat()
        binding.resultado.text = ""
        binding.resultado.hint = "$num1"
    }

    companion object {
        const val nada = 0
        const val suma = 1
        const val resta = 2
        const val multiplicacion = 3
        const val division = 4
        const val porcinco = 5
        const val pordiez = 6

    }
    private fun traido() {
        val monto: Intent = intent
        val cantidad = monto.getStringExtra("monto")
        val cantidadSinEspacios = cantidad?.replace(" ", "")?.replace(",", ".")

        num1 = try {
            cantidadSinEspacios?.toFloat() ?: 0F
        } catch (e: NumberFormatException) {
            0F
        }

        binding.resultado.text = cantidadSinEspacios
    }


    /*binding.resultadoText.text = if("$result".endsWith(".0"))
     { "$result".replace(".0","") } else { "%.2f".format(result) }*/

}
