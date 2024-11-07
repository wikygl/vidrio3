package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.ListaActivity
import crystal.crystal.R
import kotlinx.android.synthetic.main.activity_mampara_fc.*

@Suppress("IMPLICIT_CAST_TO_ANY")
class MamparaFC : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mampara_fc)

        btn_calcular.setOnClickListener {
            try {
                val corrediza = marco.text.toString().toFloat()
                val jun = junk.text.toString().toFloat()
                val alto = med2.text.toString().toFloat()

                val bastidor = if (bast.text.toString().toFloat()==0f){8f}
                else{bast.text.toString().toFloat()}

                if (corrediza == 0f) {
                    bastitxt.text = "${df1(zocaloTechoFijo())} = 4\n" +
                            "${df1(paranteCorredizo())} = 2\n" +
                            "${df1(paranteFijo())} = 2"
                } else {
                    bastitxt.text = "${df1(zocaloTechoFijo())} = 2\n" +
                            "${df1(zocaloTechoCorre())} = 2\n" +
                            "${df1(paranteCorredizo())} = 2\n" +
                            "${df1(paranteFijo())} = 2"
                }

                if (jun == 0f) {
                    if (corrediza == 0f) {
                        junkitxt.text = "${df1(zocaloTechoFijo())} = 8\n" +
                                "${df1(paranteCorredizo() - (2 * bastidor))} = 4\n" +
                                "${df1(paranteFijo() - (2 * bastidor))} = 4"
                    } else {
                        junkitxt.text = "${df1(zocaloTechoFijo())} = 4\n" +
                                "${df1(zocaloTechoCorre())} = 4\n" +
                                "${df1(paranteCorredizo() - (2 * bastidor))} = 4\n" +
                                "${df1(paranteFijo() - (2 * bastidor))} = 4"
                    }
                } else {
                    if (corrediza == 0f) {
                        junkitxt.text = "${df1(zocaloTechoFijo())} = 8\n" +
                                "${df1(paranteCorredizo() - junquilloAlto())} = 4\n" +
                                "${df1(paranteFijo() - junquilloAlto())} = 4"
                    } else {
                        junkitxt.text = "${df1(zocaloTechoFijo())} = 4\n" +
                                "${df1(zocaloTechoCorre())} = 4\n" +
                                "${df1(paranteCorredizo() - junquilloAlto())} = 4\n" +
                                "${df1(paranteFijo() - junquilloAlto())} = 4"
                    }
                }

                marcotxt.text = "${df1(alto)} = 2\n${df1(marcoSuperior())} = 1"

                angtxt.text = "${df1(marcoSuperior())} = 2\n${df1(paranteFijo() - 1.5f)} = 1"

                rieltxt.text = "${df1(marcoSuperior())} = 1"

                vidriostxt.text = "${vidrioF()} = 1\n${vidrioC()} = 1"

                referencias.text = referen()

                porttxt.text = "${df1(paranteFijo()-1.5f)} = 1"

                txTe.text = "${angTapa()} = 1"

            }catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()}
        }

        btn_archivar.setOnClickListener {
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",marcotxt.text.toString()))
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",bastitxt.text.toString()))
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",rieltxt.text.toString()))
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",junkitxt.text.toString()))
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",angtxt.text.toString()))
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",porttxt.text.toString()))
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",vidriostxt.text.toString()))
        }
        //btn_ficha.setOnClickListener{(startActivity(Intent(this,FichaFC::class.java)))}
    }

    // FUNCIONES REDONDEOS
    private fun df1(defo: Float): String {
        val resultado =if ("$defo".endsWith(".0")) {"$defo".replace(".0", "")}
        else { "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }
    private fun zocaloTechoFijo(): Float {
        val ancho = med1.text.toString().toFloat()
        val corrediza = marco.text.toString().toFloat()

        val marco = 2.5f
        val bastidor = if (bast.text.toString().toFloat()==0f){8f}
        else{bast.text.toString().toFloat()}
        val espesor=0.3f

        val anchoHoja = (ancho - (2 * marco))-espesor
        return when (corrediza) {
            0F -> {((anchoHoja + bastidor) / 2) - (2 * bastidor)}
            else -> {(anchoHoja - corrediza) - (2 * bastidor)}
        }
    }
    private fun zocaloTechoCorre(): Float {
        val ancho = med1.text.toString().toFloat()
        val corrediza = marco.text.toString().toFloat()
        val marco = 2.5f
        val bastidor = if (bast.text.toString().toFloat()==0f){8f}
        else{bast.text.toString().toFloat()}
        val espesor=0.3f

        val anchoHoja = (ancho - (2 * marco))-espesor
        return when (corrediza) {
            0F -> {((anchoHoja + bastidor) / 2) - (2 * bastidor)}
            else -> {corrediza - (bastidor)}
        }
    }
    private fun paranteFijo():Float{
        val alto= med2.text.toString().toFloat()
        val marco = 2.5f
        return alto-marco
    }
    private fun paranteCorredizo():Float{
        val alto= med2.text.toString().toFloat()
        val descuento =4.5f
        return alto-descuento
    }
    private fun junquilloAlto():Float{
        val jun=junk.text.toString().toFloat()
        val bastidor = if (bast.text.toString().toFloat()==0f){8f}
        else{bast.text.toString().toFloat()}

        return (2*bastidor)+(2*jun)
    }
    private fun marcoSuperior():Float{
        val ancho = med1.text.toString().toFloat()
        val marco = 2.5f
        return ancho-(2*marco)
    }
    private fun vidrioF(): String {
        val jun=junk.text.toString().toFloat()
        val holgura = if(jun==0f){0.4f}else{0.6f}
        val anchfij = df1((zocaloTechoFijo() - holgura))
        val altfij = df1((paranteFijo() - 16.5f)-holgura)
        return "$anchfij x $altfij"
    }
    private fun vidrioC(): String {
        val jun=junk.text.toString().toFloat()
        val holgura = if(jun==0f){0.4f}else{0.6f}
        val anchcorre = df1((zocaloTechoCorre() - holgura))
        val altcorr = df1((paranteCorredizo() - 16.5f)-holgura)
        return "$anchcorre x $altcorr"
    }
    private fun referen():String{
        val ancho=med1.text.toString()
        val alto=med2.text.toString()
        val corrediza = marco.text.toString()
        return "anch:$ancho x alt:$alto -> Cdza:$corrediza"
    }
    private fun angTapa():String {
        val bastidor = if (bast.text.toString().toFloat()==0f){8f}
        else{bast.text.toString().toFloat()}
        return df1(zocaloTechoCorre()+(2*bastidor))

    }
}