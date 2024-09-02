package crystal.crystal.taller

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityVentanaAlBinding
import kotlinx.android.synthetic.main.activity_ventana_al.*


class VentanaAl : AppCompatActivity() {

    private lateinit var bindin : ActivityVentanaAlBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindin= ActivityVentanaAlBinding.inflate(layoutInflater)
        setContentView(bindin.root)

        bindin.btnCalcularE.setOnClickListener {
            marco()
            parante()
            zocalo()
            vidrios()
            riel()
            tope()
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

    // FUNCIONES ALUMINIOS

    @SuppressLint("SetTextI18n")
    private fun parante(){
        val pe=paran()+1
        bindin.tvParante.text= if (divisiones()==0){"${(df1(paran()))} = 4"}
        else{"${(df1(paran()))} = 4\n${df1(pe)} = 4"}
    }

    @SuppressLint("SetTextI18n")
    private fun marco(){
        val alto = etAlto.text.toString().toFloat()
        tvMarcoResult.text= "${df1(alto)} = 2\n${df1(anchUtil())} = 2"

    }
    @SuppressLint("SetTextI18n")
    private fun zocalo(){
        val z=df1(zoc()).toFloat()
        bindin.tvZocaloE.text= "${df1(z)} = 4"
    }

    @SuppressLint("SetTextI18n")
    private fun riel(){
        val riel= anchUtil()
        bindin.tvRielE.text = "${df1(riel)} = 2"
    }
    
    @SuppressLint("SetTextI18n")
    private fun tope(){
        val t= altoUtil()
        val tc= paran()
        bindin.tvAngE.text = if (divisiones()==0){"${df1(t)} = 2"}else{"${df1(tc)} = 1"}
    }

    // FUNCIONES VIDRIOS

    @SuppressLint("SetTextI18n")
    private fun vidrios(){
        val ancho = zoc() + 1.5f
        val alto = paran()- 7f
        val ale = alto+1
        bindin.tvVidriosR.text = if (divisiones()==0){"${df1(ancho)} x ${df1(alto)} = 2"}else
        {"${df1(ancho)} x ${df1(alto)} = 2\n${df1(ancho)} x ${df1(ale)} = 2"}
    }

    //   FUNCIONES GENERALES

    private fun anchUtil(): Float {
        val ancho = etAncho.text.toString().toFloat()
        val marco = etMarco.text.toString().toFloat()
        return ancho - (2 * marco)

    }
    private fun altoUtil():Float{
        val alto = etAlto.text.toString().toFloat()
        val marco=etMarco.text.toString().toFloat()
        return alto - (2 * marco)
    }
    private fun zoc():Float{
        val div = etPartes.text.toString().toFloat()
        return if(divisiones()==0){(anchUtil()-(3f*3))/2}else{(anchUtil()-(2.9f*6))/div}
    }

    private fun paran():Float{
        return altoUtil()-1f
    }

    private fun divisiones(): Int {
        return etPartes.text.toString().toInt()
    }
}