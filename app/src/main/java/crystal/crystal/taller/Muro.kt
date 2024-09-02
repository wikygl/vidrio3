package crystal.crystal.taller

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.R
import crystal.crystal.databinding.ActivityMuroBinding
import kotlin.math.abs

class Muro : AppCompatActivity() {

    private lateinit var binding : ActivityMuroBinding
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMuroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalcular.setOnClickListener {
            vidrios()
            aluminio()
            binding.vidriotxt.text =  "${nFilas().first} x ${nColumnas().first} = " +
                    "${nFilas().second.toInt()*nColumnas().second.toInt()} "
        }
    }

    private fun df(defo: Float): String {
        val resultado =if ("$defo".endsWith(".0")) {"$defo".replace(".0", "")}
        else { "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private fun vidrios(){
        val ancho = binding.med1.text.toString().toFloat()
        val alto = binding.med2.text.toString().toFloat()
        val filas = binding.nFilas.text.toString().toInt()
        val colum = binding.nCol.text.toString().toInt()
        val gruna = binding.etCruce.text.toString().toFloat()

        val nGrunasV = (filas - 1)*gruna
        val nGrunasH = colum*gruna

        val nF = if (filas==0){alto}else{(alto-nGrunasV)/filas}
        val nC = if (colum==0){ancho}else{(ancho-nGrunasH)/colum}

        binding.vidriotxt.text = "${df(nC)} x ${df(nF)}"

        // Obtener la instancia de RectanguloView desde el layout
        val rectanguloView = findViewById<RectanguloView>(R.id.rectanguloView)

        // Pasar los valores de ancho y alto a RectanguloView
        rectanguloView?.setWidth(ancho)
        rectanguloView?.setHeight(alto)
        rectanguloView?.maxWidth = ancho
        rectanguloView?.maxHeight = alto


        // Volver a dibujar la vista
        rectanguloView.invalidate()
    }
    @SuppressLint("SetTextI18n")
    private fun aluminio() {
        val ancho = binding.med1.text.toString().toFloat()
        val alto = binding.med2.text.toString().toFloat()
        val filas = binding.nFilas.text.toString().toInt()
        val colum = binding.nCol.text.toString().toInt()
        val tubo = binding.tubo.text.toString().toFloat()


        val ancUtil=ancho-(tubo*2)
        val altUtil=alto-(tubo*2)
        val ancAl= (ancho-((colum+1)* tubo))/colum
        binding.etTubo.text = "$alto = 2\n$altUtil = ${colum-1}\n$ancUtil = 2\n$ancAl = ${colum*(filas-1)}"

    }
    fun nFilas(): Pair<String, String> {
        val alto = binding.med2.text.toString().toFloat()
        val filas = binding.nFilas.text.toString().toInt()

        val alturaObjetivoParte = 107f
        val alturaMinimaParte = 90f
        val alturaMaximaParte = 135f

        val n = if (filas == 0) {
            var mejorNumeroPartes = 1
            var diferenciaMejor = abs(alto - alturaObjetivoParte * mejorNumeroPartes)

            for (numeroPartes in 2..alto.toInt()) {
                val alturaParte = alto / numeroPartes
                val diferenciaAltura = abs(alturaParte - alturaObjetivoParte)

                if (alturaParte in alturaMinimaParte..alturaMaximaParte && diferenciaAltura < diferenciaMejor) {
                    mejorNumeroPartes = numeroPartes
                    diferenciaMejor = diferenciaAltura }
            }
            val alturaParte = alto / mejorNumeroPartes
            List(mejorNumeroPartes) { alturaParte }
        } else {
            val alturaParte = alto / filas
            List(filas) { alturaParte }
        }
        // Obtener la información en angtxt_nfcfi
        val medidaCantidad = n.joinToString(separator = " cm, ") { it.toString() } + " cm"
        val cantidadPartes = n.size.toString()

        return Pair(medidaCantidad, cantidadPartes)
    }

    fun nColumnas(): Pair<String, String> {
        val ancho = binding.med1.text.toString().toFloat()
        val colum = binding.nCol.text.toString().toInt()

        val alturaObjetivoParte = 72f
        val alturaMinimaParte = 54f
        val alturaMaximaParte = 90f

        val n = if (colum == 0) {
            var mejorNumeroPartes = 1
            var diferenciaMejor = abs(ancho - alturaObjetivoParte * mejorNumeroPartes)

            for (numeroPartes in 2..ancho.toInt()) {
                val alturaParte = ancho / numeroPartes
                val diferenciaAltura = abs(alturaParte - alturaObjetivoParte)

                if (alturaParte in alturaMinimaParte..alturaMaximaParte && diferenciaAltura < diferenciaMejor) {
                    mejorNumeroPartes = numeroPartes
                    diferenciaMejor = diferenciaAltura
                }
            }

            val alturaParte = ancho / mejorNumeroPartes
            List(mejorNumeroPartes) { alturaParte }
        } else {
            val alturaParte = ancho / colum
            List(colum) { alturaParte }
        }

        // Obtener la información en rieltxtNfcfi
        val medidaCantidad = n.joinToString(separator = " cm, ") { it.toString() } + " cm"
        val cantidadPartes = n.size.toString()

        return Pair(medidaCantidad, cantidadPartes)
    }

}