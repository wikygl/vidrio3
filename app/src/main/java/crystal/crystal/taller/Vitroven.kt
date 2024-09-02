package crystal.crystal.taller

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.R
import kotlinx.android.synthetic.main.activity_vitroven.*

@Suppress("IMPLICIT_CAST_TO_ANY")
class Vitroven : AppCompatActivity() {


    private var ancho = 0F
    private var alto = 0F
    private var dire = 0
    private lateinit var imagen: ImageView
    private var clip = 9.6
    private var cruce = 0.6
    private val holgura = 1

    @SuppressLint("SetTextI18n", "ResourceAsColor", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vitroven)

    //imagen = findViewById(R.id.img_v)

        btn_calcular_v.setOnClickListener {
            try {
                vidriostxt_v.text = medVidrios()
                jambatxt_v.text = jamba()
                angtxt_v.text = divisiones1().toString()
                umitxt_v.text = residuo().toString()
                plattxt_v.text = medi1()
                dir_v.hint = if (direccion() == alto) {"Vert"} else {"Horz"}
                //fichaT_layout.setBackgroundColor(R.color.violeta)
                //img_v.setImageResource(R.drawble.ic_vitrofijo)
                pruebastxt_v.text = enteros().toString()
                pruebastxt.text= df(medi2())
                giros()

            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

// FUNCIONES MATERIALES

    private fun medVidrios(): String {
        ancho = med1_v.text.toString().toFloat()
        val k = 4.5f
        val ancVid = ancho - k
        val altVid = mResiduo() - 0.1
        val res1 = if ("$ancVid".endsWith(".0")) {"$ancVid".replace(".0", "")}
        else { "%.1f".format(ancVid) }
        val cambiazo1 = res1.replace(",", ".")
        val res2 = if ("$altVid".endsWith(".0")) {"$altVid".replace(".0", "")}
        else {"%.1f".format(altVid)}
        val cambiazo2 = res2.replace(",", ".")
        return if (cambiazo2 == "10.1") {"$cambiazo1 x 10.1 = ${enteros() + 1}"}
        else {"$cambiazo1 x 10.1 = ${enteros()}\n$cambiazo1 x $cambiazo2 = 1"}
    }

    private fun jamba(): String {
        val medida = medi1()
        val cantidad = if (divisiones1() < 3) {2} else {divisiones1() - 1}
        return "$medida = $cantidad"
    }

    //FUNCIONES DE MEDIDAS
    //Medidas bruto
    private fun medi1(): String {
        val rean = if ("${medi2()}".endsWith(".0")) {"${medi2()}".replace(".0", "")}
        else {"%.1f".format(medi2())}
        return rean.replace(",", ".")
    }

    private fun medi2(): Float {
        return direccion() / divisiones1()
    }
    private fun xxx():Float{
        alto=med2_v.text.toString().toFloat()
        val x = 9.6f
        val division = alto/x
        return division
    }

    //FUNCIONES GENERALES DE CALCULO VITROVEN

    private fun enteros(): Int {
        alto = med2_v.text.toString().toFloat()
        val div=div.text.toString().toInt()
        val clips=Clips.text.toString().toInt()
        val control = if(alto<clips*clip){alto/clip}else{clips}.toInt()
        //Toast.makeText(this,"No es posible, se suguiere lo siguiente",Toast.LENGTH_SHORT).show()
        val k = if(clips==0){alto/clip}else{control}
        return k as Int
    }
    private fun nEnteros(): Int {
        alto = med2_v.text.toString().toFloat()
        return if (enteros() * clip + 2 * cruce > alto) {enteros() - 1} else {enteros()}
    }
    private fun mEnteros(): Float {
        val divi=Clips.text.toString().toInt()
        val div=div.text.toString().toInt()
        val mEnteros =if(div==0&&divi==0){(nEnteros() * clip + 2 * cruce)}
        else if(div==2){(nEnteros() * clip + cruce)}else{nEnteros() * clip}
        return mEnteros.toFloat()
    }
    private fun residuo(): Float {
        alto = med2_v.text.toString().toFloat()
        return alto - mEnteros()
    }

    private fun mResiduo(): Float {
        val resta = (clip + residuo()) - holgura
        return resta.toFloat()
    }

    //FUNCION DE DIVISIONES Y DIRECCIONES
    //eleción: vertical...horizontal...
    private fun direccion(): Float {
        ancho = med1_v.text.toString().toFloat()
        alto = med2_v.text.toString().toFloat()
        dire = dir_v.text.toString().toInt()
        return if (dire == 0) {alto} else {ancho}
    }

    private fun direcVano() {
        ancho = med1_v.text.toString().toFloat()
        alto = med2_v.text.toString().toFloat()
        if (direccion() == ancho) {ancho}
    }

    private fun divisiones1(): Int {
        ancho = med1_v.text.toString().toFloat()
        val divis = div.text.toString().toInt()

        val med = direccion()
        val div = if (divis == 0) {
            when {
                med <= 60 -> 1
                med in 60.0..120.0 -> 2
                med in 120.0..180.0 -> 3
                med in 180.0..240.0 -> 4
                med in 240.0..300.0 -> 5
                else -> divis
            }
        } else {divis}
        return div
    }

    private fun divisionesAncho(): Int {
        ancho = med1_v.text.toString().toFloat()
        val divan = div.text.toString().toInt()
        return if (divan == 0) {
            when (ancho) {
                in 0.0..60.0 -> 1
                in 60.0..120.0 -> 2
                in 120.0..180.0 -> 3
                in 180.0..240.0 -> 4
                in 240.0..300.0 -> 5
                in 300.0..360.0 ->6
                in 360.0..420.0 -> 7
                in 420.0..480.0 ->8
                in 480.0..540.0 ->9
                in 540.0..600.0 ->10
                else -> {divan}}
        } else {divan}
    }

    private fun divisionesAlto(): Int {
        alto = med2_v.text.toString().toFloat()
        val dival = Clips.text.toString().toInt()
        return if (dival == 0) {1} else {dival}
    }

    private fun giros() {
        if (dire == 1) rotarImagen(imagen)
        else mRotarImagen(imagen)
    }

    private fun rotarImagen(view: View) {
        val animation = RotateAnimation(
            0F, 90f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 0
        animation.repeatCount = Animation.INFINITE

        view.startAnimation(animation)
    }

    private fun mRotarImagen(view: View) {
        val animation = RotateAnimation(
            90F, 0f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 0
        animation.repeatCount = Animation.INFINITE

        view.startAnimation(animation)
    }
    private fun df(df: Float):String{
        val result = df.toString()
        val ret =if(result.endsWith(".0")){ result.replace(".0","") }
        else { "%.1f".format(result)}
        return ret.replace(",", ".")
    }

}
// resultadoText.text = if("$result".endsWith(".0"))
// { "$result".replace(".0","") } else { "%.2f".format(result) }






