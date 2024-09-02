package crystal.crystal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_vende_papa.*

@Suppress("IMPLICIT_CAST_TO_ANY")
class VendePapa : AppCompatActivity() {
    private var num1=0F
    private var num2=0F
    private var num3="0.0"
    private var ope=0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vende_papa)

        cero.setOnClickListener { numPresionado("0") }
        uno.setOnClickListener { numPresionado("1") }
        dos.setOnClickListener { numPresionado("2") }
        tres.setOnClickListener { numPresionado("3") }
        cuatro.setOnClickListener { numPresionado("4") }
        cinco.setOnClickListener { numPresionado("5") }
        seis.setOnClickListener { numPresionado("6") }
        siete.setOnClickListener { numPresionado("7") }
        ocho.setOnClickListener { numPresionado("8") }
        nueve.setOnClickListener { numPresionado("9") }
        punto.setOnClickListener { try { numPresionado(".") } catch (e: Exception){} }
        borrar.setOnClickListener { if(resultado.text.isNotEmpty()){
            resultado.text = resultado.text.substring(0, resultado.text.length-1)}}

        sumar.setOnClickListener {
            opPresionado(suma)
            memo.text = "$num1 +"  }

        restar.setOnClickListener {
            opPresionado(resta)
            memo.text = "$num1 -" }

        multiplicar.setOnClickListener {
            opPresionado(multiplicacion)
            memo.text = "$num1 ×" }

        dividir.setOnClickListener {
            opPresionado(division)
            memo.text = "$num1 ÷"   }

        desc5.setOnClickListener {opPresionado(porcinco) }
        desc10.setOnClickListener {opPresionado(pordiez) }

        reset.setOnClickListener {
            num1 = 0F
            num2 = 0F
            resultado.text = ""
            ope = nada
            memo.text = ""
            resultado.hint = ""
        }
        igual.setOnClickListener {
            num2 = if (resultado.text.isNotEmpty()) {
                resultado.text.toString().toFloat()
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
            resultado.text = resultadoO.toString()
            when (ope) {
                suma -> memo.text = "$num1 + $num2 ="
                resta -> memo.text = "$num1 - $num2 ="
                multiplicacion -> memo.text = "$num1 × $num2 ="
                division -> memo.text = "$num1 ÷ $num2 ="
                porcinco -> memo.text = "$num1 - (5)% ="
                pordiez -> memo.text = "$num1 - (10)% ="
            }
        }
        masMenos.setOnClickListener{
            val resultadom = resultado.text.toString().toFloat()
            resultado.text = (resultadom * -1).toString()
        }
        total.setOnClickListener {
            val resul = resultado.text.toString().toFloat()
            val sub =(resul / 1.18).toFloat()
            hist.text = "sub.Total = ${df1(sub)}\nIGV = ${df1(resul-sub)}\nTotal = ${df1(resul)}"
        }

        traido()
    }
    @SuppressLint("SetTextI18n")
    private fun numPresionado(digito: String) {
        resultado.text = "${resultado.text}$digito"
        if (ope == nada) {
            num1 = if (resultado.text.isNullOrEmpty()) 0F else resultado.text.toString().toFloat()
        } else {
            num2 = resultado.text.toString().toFloat()
        }
    }
    private fun df1(defo: Float): String {
        val resultado =if ("$defo".endsWith(".0")) {"$defo".replace(".0", ".00")}
        else { "%.2f".format(defo)
        }
        return resultado.replace(",", ".")
    }
    private fun opPresionado(operacion: Int) {
        ope = operacion
        num1 = if (resultado.text.isNullOrEmpty()) 0F else resultado.text.toString().toFloat()
        resultado.text = ""
        resultado.hint = "$num1"
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

        resultado.text = cantidadSinEspacios
    }


    /*resultadoText.text = if("$result".endsWith(".0"))
     { "$result".replace(".0","") } else { "%.2f".format(result) }*/

}
