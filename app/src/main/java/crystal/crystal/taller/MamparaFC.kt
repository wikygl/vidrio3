package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.ListaActivity
import crystal.crystal.databinding.ActivityMamparaFcBinding

class MamparaFC : AppCompatActivity() {
    
    
    lateinit var binding: ActivityMamparaFcBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMamparaFcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalcular.setOnClickListener {
            try {
                val corrediza = binding.marco.text.toString().toFloat()
                val jun = binding.junk.text.toString().toFloat()
                val alto = binding.med2.text.toString().toFloat()

                val bastidor = if (binding.bast.text.toString().toFloat()==0f){8f}
                else{binding.bast.text.toString().toFloat()}

                if (corrediza == 0f) {
                    binding.bastitxt.text = "${df1(zocaloTechoFijo())} = 4\n" +
                            "${df1(paranteCorredizo())} = 2\n" +
                            "${df1(paranteFijo())} = 2"
                } else {
                    binding.bastitxt.text = "${df1(zocaloTechoFijo())} = 2\n" +
                            "${df1(zocaloTechoCorre())} = 2\n" +
                            "${df1(paranteCorredizo())} = 2\n" +
                            "${df1(paranteFijo())} = 2"
                }

                if (jun == 0f) {
                    if (corrediza == 0f) {
                        binding.junkitxt.text = "${df1(zocaloTechoFijo())} = 8\n" +
                                "${df1(paranteCorredizo() - (2 * bastidor))} = 4\n" +
                                "${df1(paranteFijo() - (2 * bastidor))} = 4"
                    } else {
                        binding.junkitxt.text = "${df1(zocaloTechoFijo())} = 4\n" +
                                "${df1(zocaloTechoCorre())} = 4\n" +
                                "${df1(paranteCorredizo() - (2 * bastidor))} = 4\n" +
                                "${df1(paranteFijo() - (2 * bastidor))} = 4"
                    }
                } else {
                    if (corrediza == 0f) {
                        binding.junkitxt.text = "${df1(zocaloTechoFijo())} = 8\n" +
                                "${df1(paranteCorredizo() - junquilloAlto())} = 4\n" +
                                "${df1(paranteFijo() - junquilloAlto())} = 4"
                    } else {
                        binding.junkitxt.text = "${df1(zocaloTechoFijo())} = 4\n" +
                                "${df1(zocaloTechoCorre())} = 4\n" +
                                "${df1(paranteCorredizo() - junquilloAlto())} = 4\n" +
                                "${df1(paranteFijo() - junquilloAlto())} = 4"
                    }
                }

                binding.marcotxt.text = "${df1(alto)} = 2\n${df1(marcoSuperior())} = 1"

                binding.angtxt.text = "${df1(marcoSuperior())} = 2\n${df1(paranteFijo() - 1.5f)} = 1"

                binding.rieltxt.text = "${df1(marcoSuperior())} = 1"

                binding.vidriostxt.text = "${vidrioF()} = 1\n${vidrioC()} = 1"

                binding.referencias.text = referen()

                binding.porttxt.text = "${df1(paranteFijo()-1.5f)} = 1"

                binding.txTe.text = "${angTapa()} = 1"

            }catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()}
        }

        binding.btnArchivar.setOnClickListener {
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",binding.marcotxt.text.toString()))
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",binding.bastitxt.text.toString()))
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",binding.rieltxt.text.toString()))
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",binding.junkitxt.text.toString()))
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",binding.angtxt.text.toString()))
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",binding.porttxt.text.toString()))
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",binding.vidriostxt.text.toString()))
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
        val ancho = binding.med1.text.toString().toFloat()
        val corrediza = binding.marco.text.toString().toFloat()

        val marco = 2.5f
        val bastidor = if (binding.bast.text.toString().toFloat()==0f){8f}
        else{binding.bast.text.toString().toFloat()}
        val espesor=0.3f

        val anchoHoja = (ancho - (2 * marco))-espesor
        return when (corrediza) {
            0F -> {((anchoHoja + bastidor) / 2) - (2 * bastidor)}
            else -> {(anchoHoja - corrediza) - (2 * bastidor)}
        }
    }
    private fun zocaloTechoCorre(): Float {
        val ancho = binding.med1.text.toString().toFloat()
        val corrediza = binding.marco.text.toString().toFloat()
        val marco = 2.5f
        val bastidor = if (binding.bast.text.toString().toFloat()==0f){8f}
        else{binding.bast.text.toString().toFloat()}
        val espesor=0.3f

        val anchoHoja = (ancho - (2 * marco))-espesor
        return when (corrediza) {
            0F -> {((anchoHoja + bastidor) / 2) - (2 * bastidor)}
            else -> {corrediza - (bastidor)}
        }
    }
    private fun paranteFijo():Float{
        val alto= binding.med2.text.toString().toFloat()
        val marco = 2.5f
        return alto-marco
    }
    private fun paranteCorredizo():Float{
        val alto= binding.med2.text.toString().toFloat()
        val descuento =4.5f
        return alto-descuento
    }
    private fun junquilloAlto():Float{
        val jun=binding.junk.text.toString().toFloat()
        val bastidor = if (binding.bast.text.toString().toFloat()==0f){8f}
        else{binding.bast.text.toString().toFloat()}

        return (2*bastidor)+(2*jun)
    }
    private fun marcoSuperior():Float{
        val ancho = binding.med1.text.toString().toFloat()
        val marco = 2.5f
        return ancho-(2*marco)
    }
    private fun vidrioF(): String {
        val jun=binding.junk.text.toString().toFloat()
        val holgura = if(jun==0f){0.4f}else{0.6f}
        val anchfij = df1((zocaloTechoFijo() - holgura))
        val altfij = df1((paranteFijo() - 16.5f)-holgura)
        return "$anchfij x $altfij"
    }
    private fun vidrioC(): String {
        val jun=binding.junk.text.toString().toFloat()
        val holgura = if(jun==0f){0.4f}else{0.6f}
        val anchcorre = df1((zocaloTechoCorre() - holgura))
        val altcorr = df1((paranteCorredizo() - 16.5f)-holgura)
        return "$anchcorre x $altcorr"
    }
    private fun referen():String{
        val ancho=binding.med1.text.toString()
        val alto=binding.med2.text.toString()
        val corrediza = binding.marco.text.toString()
        return "anch:$ancho x alt:$alto -> Cdza:$corrediza"
    }
    private fun angTapa():String {
        val bastidor = if (binding.bast.text.toString().toFloat()==0f){8f}
        else{binding.bast.text.toString().toFloat()}
        return df1(zocaloTechoCorre()+(2*bastidor))

    }
}