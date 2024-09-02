package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.ListaActivity
import crystal.crystal.databinding.ActivityMamparaPaflonBinding
import kotlinx.android.synthetic.main.activity_mampara_paflon.*


@Suppress("IMPLICIT_CAST_TO_ANY")
class MamparaPaflon : AppCompatActivity() {

  private val marco= 2.5f
  private val pAnch= 8.25f
  private val pAlt=3.8f

  private lateinit var binding: ActivityMamparaPaflonBinding

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding= ActivityMamparaPaflonBinding.inflate(layoutInflater)
    setContentView(binding.root)

    btn_calcular_fccfss.setOnClickListener {
      try {
        val ancho = med1_mpa.text.toString().toFloat()
        val alto = med2_mpa.text.toString().toFloat()
        val altoHoja = altohoja_mpa.text.toString().toFloat()
        val bast = bast_mfccfss.text.toString().toFloat()
        val jun = junk_mfccfss.text.toString().toFloat()
        val anchoUtil = ancho - (2 * marco)

        referencias()


        marcotxt_mfccfss.text = "${df1(alto)} = 2\n${df1(anchoUtil)} = 1"

        rieltxt_mfccfss.text = "${df1(anchoUtil)} = 1"

        angtxt_mfccfss.text = "${df1(anchoUtil)} = 2\n${altoHoja - 3.3} = 1"

        porttxt_mfccfss.text = "${altoHoja - 1.5} = 2"

        bastitxt_mfccfss.text = "${df1(zocaloFijo())} = 2\n" +
                "${df1(zocaloCorrediza())} = 4\n" +
                "${df1(paranteCorredizo())} = 4\n" +
                "${df1(paranteFijo())} = 2\n" +
                "${df1(paranteMocheta())} = 1\n" +
                "${df1(anchoUtil)} = 1"

        if (jun == 0f) {
          junkitxt_mfccfss.text = "${df1(zocaloFijo())} = 4" +
                  "\n${df1(zocaloCorrediza())} = 4" +
                  "\n${df1(paranteCorredizo() - (2 * bast))} = 4" +
                  "\n${df1(paranteFijo() - bast)} = 4" +
                  "\n${df1(paranteMocheta())} = 4" +
                  "\n${df1(anchoMocheta())}= 4"
        } else {
          junkitxt_mfccfss.text = "${df1(zocaloFijo())} = 4" +
                  "\n${df1(zocaloCorrediza())} = 4" +
                  "\n${df1((paranteCorredizo() - (2 * bast)) - (2 * jun))} = 4" +
                  "\n${df1((paranteFijo() - bast) - (2 * jun))} = 4" +
                  "\n${df1(paranteMocheta() - (2 * jun))} = 4" +
                  "\n${df1(anchoMocheta())}= 4"
        }

        vidriostxt_mfccfss.text =
          "${df1(zocaloFijo() - 0.4f)} x ${altoVidrioFijo()} = 2\n" +
                  "${df1(zocaloCorrediza() - 0.4f)} x" +
                  " ${df1(altoVidrioCorredizo())} = 2\n" +
                  "${df1(paranteMocheta() - 0.4f)} x " +
                  "${df1(anchoMocheta()- 0.4f) } = 2"
      }catch (e: Exception) {
        Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()}
    }

    btn_archivar_fccfss.setOnClickListener{
      startActivity(Intent(this, ListaActivity::class.java).putExtra(
        "monto",marcotxt_mfccfss.text.toString()))
      startActivity(Intent(this, ListaActivity::class.java).putExtra(
        "monto",bastitxt_mfccfss.text.toString()))
      startActivity(Intent(this, ListaActivity::class.java).putExtra(
        "monto",rieltxt_mfccfss.text.toString()))
      startActivity(Intent(this, ListaActivity::class.java).putExtra(
        "monto",junkitxt_mfccfss.text.toString()))
      startActivity(Intent(this, ListaActivity::class.java).putExtra(
        "monto",angtxt_mfccfss.text.toString()))
      startActivity(Intent(this, ListaActivity::class.java).putExtra(
        "monto",porttxt_mfccfss.text.toString()))
      startActivity(Intent(this, ListaActivity::class.java).putExtra(
        "monto",vidriostxt_mfccfss.text.toString()))
    }
  }

  // FUNCIONES REDONDEOS
  private fun df1(defo: Float): String {
    val resultado =if ("$defo".endsWith(".0")) {"$defo".replace(".0", "")}
    else { "%.1f".format(defo)
    }
    return resultado.replace(",", ".")
  }
 // FUNCIONES REFERENCIAS
  
  @SuppressLint("SetTextI18n")
  private fun referencias(){
   val ancho = med1_mpa.text.toString().toFloat()
   val alto = med2_mpa.text.toString().toFloat()
    tvReferencias.text= "Ancho ${df1(ancho)}; Alto ${df1(alto)}\n Altura de puente ${df1(altoHoja())}" +
            "\nPartes ${divisiones()}"
  }
  //FUNCIONES ALUMINIOS

  private fun zocaloFijo(): Float {
    val ancho = med1_mpa.text.toString().toFloat()
    val anchoUtil=  ancho - (2*marco)
    return when(divisiones()){
      1->anchoUtil
      2->((anchoUtil+pAnch)/2)-pAnch
      3->((anchoUtil+ 2*pAnch)/3 )-2*pAnch
      4->(anchoUtil-4*pAnch)/4

      else -> {anchoUtil}
    }
  }
  private fun zocaloCorrediza(): Float {
    val ancho = med1_mpa.text.toString().toFloat()
    val anchoUtil=  ancho - (2*marco)
    return when(divisiones()){
      1->(anchoUtil-2*pAnch)
      2->((anchoUtil+pAnch)/2)-2*pAnch
      3->((anchoUtil+ 2*pAnch)/3 )-2*pAnch
      4->((anchoUtil+2*pAnch)/4)-2*pAnch

      else -> {anchoUtil}
    }
  }
  private fun paranteCorredizo():Float{
    return altoHoja()-2.2f
  }
  private fun paranteFijo(): Float {
    return altoHoja()
  }
  private fun paranteMocheta():Float {
    val divis = partes_mpa.text.toString().toFloat()
    val bastidor= bast_mfccfss.text.toString().toFloat()
    val altoHoja=altohoja_mpa.text.toString().toFloat()
    val alto = med2_mpa.text.toString().toFloat()
    return ((alto-altoHoja)- (divis+bastidor))
  }
  private fun anchoMocheta():Float{
    val ancho = med1_mpa.text.toString().toFloat()
    val bastidor= bast_mfccfss.text.toString().toFloat()
    val anchoUtil=  ancho - (2*marco)
    return (anchoUtil-bastidor)/2
  }
  private fun altoMocheta(): Float {
    val alto = med2_mpa.text.toString().toFloat()
    return alto - (marco + altoHoja() + pAlt)
  }
  //FUNCIONES VIDRIOS
  private fun altoVidrioFijo(): Float {
    val bastidor= bast_mfccfss.text.toString().toFloat()
    val altoHoja=altohoja_mpa.text.toString().toFloat()
    val holgura = 0.4f
    return altoHoja-(bastidor+holgura)
  }
  private fun altoVidrioCorredizo(): Float {
    val bastidor= bast_mfccfss.text.toString().toFloat()
    val holgura = 0.4f
    return paranteCorredizo()-((2*bastidor)+holgura)
  }

  //FUNCIONES GENERALES
  private fun altoHoja():Float {
    val alto=med2_mpa.text.toString().toFloat()
    val hoja= altohoja_mpa.text.toString().toFloat()
    val corre = if (hoja >= alto || alto <= 198f) {alto-marco}else{hoja}
    return if (hoja==0f){198f}else{corre}
  }
  private fun nParantes():Float{
    val parantes= when (divisiones()){
      1 -> 0   2 -> 2   3 -> 2   4 -> 4
      5 -> 4   6 -> 5   7 -> 6
      8 -> 9   9 -> 8   10 -> 9  11 -> 10
      12 -> 14 13 -> 12 14 -> 14 15 -> 14
      else -> 0
    }
    return parantes*pAnch
  }
  private fun divisiones(): Int {
    val ancho = med1_mpa.text.toString().toFloat()
    val divis = partes_mpa.text.toString().toInt()
    return if (divis == 0) {
      when {
        ancho <= 90 -> 1
        ancho in 90.0..180.0 -> 2
        ancho in 180.0..270.0 -> 3
        ancho in 270.0..360.0 -> 4
        ancho in 360.0..450.0 -> 5
        ancho in 450.0..540.0 -> 6
        ancho in 540.0..630.0 -> 7
        ancho in 630.0..720.0 -> 8
        ancho in 720.0..810.0 -> 9
        ancho in 810.0..900.0 -> 10
        ancho in 900.0..990.0 -> 11
        ancho in 990.0..1080.0 -> 12
        ancho in 1080.0..1170.0 -> 13
        ancho in 1170.0..1260.0 -> 14
        ancho in 1260.0..1350.0 -> 15
        else -> divis
      }} else {divis}
  }
}