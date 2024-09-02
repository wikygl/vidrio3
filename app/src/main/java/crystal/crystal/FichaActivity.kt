package crystal.crystal

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityFichaBinding


class FichaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFichaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFichaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val paqueteR=intent.extras
        //VARIABLES DE MATERIALES
        val u38= paqueteR?.getString("u38")
        val u13= paqueteR?.getString("u13")
        val uMarco= paqueteR?.getString("uMarco")
        val tubo= paqueteR?.getString("tubo")
        val riel= paqueteR?.getString("riel")
        val uFel= paqueteR?.getString("uFel")
        val fCo= paqueteR?.getString("fCo")
        val porta= paqueteR?.getString("porta")
        val angt= paqueteR?.getString("angT")
        val hache= paqueteR?.getString("hache")
        val vidrios= paqueteR?.getString("vidrios")

        //VARIABLES DE TUTORIALES
        val ref= paqueteR?.getString("ref")

        //VARIABLES DE CONTROL.
        val u = paqueteR?.getString("u")
        val div=paqueteR?.getString("div")
        val si=paqueteR?.getString("si")


        binding.tvMedidasF.text=ref

        binding.tvVidriosF.text = vidrios
        when(u){
            "1"->{
                binding.tvu13F.visibility=View.GONE
                binding.tvuMarcoF.visibility=View.GONE
                binding.tvu38F.visibility=View.VISIBLE
                binding.tvuF.text=u38
            }
            "1.5" -> {
                binding.tvu13F.visibility=View.VISIBLE
                binding.tvuMarcoF.visibility=View.GONE
                binding.tvu38F.visibility=View.GONE
                binding.tvuF.text=u13
            }

            else->  {
                binding.tvu13F.visibility=View.GONE
                binding.tvuMarcoF.visibility=View.VISIBLE
                binding.tvu38F.visibility=View.GONE
                binding.tvuF.text= uMarco
        }
    }//aluminio u
        binding.tvRielF.text=riel
        when(si){
            "0"->{
                binding.tvufF.visibility=View.GONE
                binding.tvfcF.visibility=View.VISIBLE
                binding.tvufelF.text=uFel
                binding.tvtubo.visibility=View.GONE
                binding.tvtuboF.text=""
            }
            "1"->{
                binding.tvufF.visibility=View.VISIBLE
                binding.tvfcF.visibility=View.GONE
                binding.tvufelF.text=fCo
                binding.tvtubo.visibility=View.VISIBLE
                binding.tvtuboF.text=tubo
            }
        }//aluminio fc,uf,tubo
        binding.tvPortaF.text=porta
        binding.tvhacheF.text=hache
        when(div){
            "2"->{
                binding.tvangTF.text=angt
                binding.tvangTF.visibility=View.VISIBLE
                binding.tvang.visibility=View.VISIBLE
            }
            else ->{
                binding.tvangTF.visibility=View.GONE
                binding.tvang.visibility=View.GONE}
        }//aluminio ang tope

        fun nDiv():String{
            val nDiv = if (si == "1") {div} else {div + "c"}
            return nDiv.toString()
        }
        binding.igDiseno.setImageResource(
            when(nDiv()){

                "1"-> R.drawable.ic_fichad1   "1c"-> R.drawable.ic_fichad1
                "2"-> R.drawable.ic_fichad2   "2c"-> R.drawable.ic_fichad2c
                "3"-> R.drawable.ic_fichad3   "3c"-> R.drawable.ic_fichad3c
                "4"-> R.drawable.ic_fichad4   "4c"-> R.drawable.ic_fichad4c
                "5"-> R.drawable.ic_fichad5   "5c"-> R.drawable.ic_fichad5c
                "6"-> R.drawable.ic_fichad6   "6c"-> R.drawable.ic_fichad6c
                "7"-> R.drawable.ic_fichad7   "7c"-> R.drawable.ic_fichad7c
                "8"-> R.drawable.ic_fichad8   "8c"-> R.drawable.ic_fichad8c
                "9"-> R.drawable.ic_fichad9   "9c"-> R.drawable.ic_fichad9c
                "10"-> R.drawable.ic_fichad10 "10c"-> R.drawable.ic_fichad10c
                "11"-> R.drawable.ic_fichad11 "11c"-> R.drawable.ic_fichad11c
                "12"-> R.drawable.ic_fichad12 "12c"-> R.drawable.ic_fichad12c
                "13"-> R.drawable.ic_fichad13 "13c"-> R.drawable.ic_fichad13c
                "14"-> R.drawable.ic_fichad14 "14c"-> R.drawable.ic_fichad14c
                "15"-> R.drawable.ic_fichad15 "15c"-> R.drawable.ic_fichad15c
                else -> {
                    R.drawable.ic_fichad5}
            })

}
}

