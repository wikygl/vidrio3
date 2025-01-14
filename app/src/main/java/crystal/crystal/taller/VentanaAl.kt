package crystal.crystal.taller

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityVentanaAlBinding
import kotlinx.android.synthetic.main.activity_ventana_al.*


class VentanaAl : AppCompatActivity() {

    private val series = listOf("Clásica", "Serie 20", "Serie 3825", "Serie 35","Serie Española")
    private var indices = 0

    private lateinit var binding : ActivityVentanaAlBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVentanaAlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalcularE.setOnClickListener {
            marco()
            parante()
            zocalo()
            vidrios()
            riel()
            tope()
            junkillo()
            puente()
            divMocheta(mPuentes1())
            nMocheta()
            binding.tvReferencias.text = "Ancho = ${binding.etAncho.text}, Alto = ${binding.etAlto.text}\n" +
                    "Div=${divisiones()} -> Fjs=${nFijos()} -> Crzas=${nCorredizas()}\n" +
                    "hHoja = ${df1(altoHoja())}"
            binding.tvPruebas.text=divisiones().toString()
        }
        binding.ivModelo.setOnClickListener {
            actualizarVentana()
            //visibleVentana()
        }
        // Opcional: Inicializar txVentana con el primer valor de la lista
        if (series.isNotEmpty()) {
            binding.txVentana.text = "Ventana de aluminio ${series[indices]}"
            indices = (indices + 1) % series.size
        }

    }
    // FUNCIONES PARA RECUPERAR ESTADO DE MODELO
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIndex",indices)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        indices = savedInstanceState.getInt("currentIndex", 0)
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
    private fun parante() {
        val pe = paran() + 1.2f
        binding.tvParante.text = if (divisiones() == 0) {
            "${df1(paran())} = ${nCorredizas() * 2}"
        } else {
            "${df1(pe)} = ${nFijos() * 2}\n${df1(paran())} = ${nCorredizas() * 2}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun marco(){
        val alto = etAlto.text.toString().toFloat()
        tvMarco.text= "${df1(alto)} = 2\n${df1(anchUtil())} = 2"

    }
    @SuppressLint("SetTextI18n")
    private fun zocalo() {
        val z = zoc()
        val adjustedZ = z - (2 * 3.0f)
        binding.tvZocalo.text = "${df1(adjustedZ)} = ${divisiones() * 2}"
    }

    @SuppressLint("SetTextI18n")
    private fun riel(){
        val riel= anchUtil()
        binding.tvRiel.text = "${df1(riel)} = 2"
    }
    
    @SuppressLint("SetTextI18n")
    private fun tope(){
        val t= altoUtil()
        val tc= paran()
        binding.tvTope.text = if (divisiones()==0){"${df1(t)} = 2"}else{"${df1(tc)} = 1"}
    }

    private fun junkillo() {
        val tuboMocheta = altoUtil() - (altoHoja() + 2.5f)
        val jun = binding.etJunki.text.toString().toFloat()
        binding.tvJunki.text = if (divisiones() == 10 || divisiones() == 14) {
            "${df1(divMocheta(mPuentes1()))} = ${(nTuboMocheta(mPuentes1()) + 1) * 2 * (nPuentes() - nTuboMocheta(mPuentes1()))}\n" +
                    "${df1(divMocheta(mPuentes2()))} = ${(nTuboMocheta(mPuentes2()) + 1) * 2}\n" +
                    "${df1(tuboMocheta - (2 * jun))} = ${nPuentes() * nTuboMocheta(mPuentes1()) * 2}"
        } else {
            "${df1(divMocheta(mPuentes1()))} = ${(nTuboMocheta(mPuentes1()) + 1) * 2 * (nPuentes() - nTuboMocheta(mPuentes1()))}\n" +
                    "${df1(tuboMocheta - (2 * jun))} = ${nPuentes() * nTuboMocheta(mPuentes1()) * 2}"
        }
    }

    private fun puente() {
        val tuboMocheta = altoUtil() - (altoHoja() + 2.5f)
        binding.tvTubo.text = when {
            (divisiones() == 10 || (divisiones() == 14 && nTuboMocheta(mPuentes2()) == 0)) -> {
                "${df1(mPuentes1())} = ${nPuentes() - 1}\n" +
                        "${df1(mPuentes2())} = ${nPuentes() - 2}\n" +
                        "${df1(tuboMocheta)} = ${nPuentes() * nTuboMocheta(mPuentes1())}"
            }
            (divisiones() == 10 || (divisiones() == 14 && nTuboMocheta(mPuentes2()) != 0)) -> {
                "${df1(mPuentes1())} = ${nPuentes() - 1}\n" +
                        "${df1(mPuentes2())} = ${nPuentes() - 2}"
            }
            (divisiones() == 10 || (divisiones() == 14 && nTuboMocheta(mPuentes1()) == 0)) -> {
                "${df1(mPuentes1())} = ${nPuentes()}"
            }
            else -> {
                "${df1(mPuentes1())} = ${nPuentes()}\n" +
                        "${df1(tuboMocheta)} = ${nPuentes() * nTuboMocheta(mPuentes1())}"
            }
        }

        binding.lyTubo.visibility = if (altoHoja() >= altoUtil()) View.GONE else View.VISIBLE
        binding.tvPruebas.text = mPuentes1().toString()
        binding.txPruebas.text = mPuentes2().toString()
    }

    // FUNCIONES VIDRIOS

    private fun vidrios() {
        val ancho = (zoc() - (2 * 3.0f)) + 1.5f
        val alto = paran() - 7.0f
        val ale = 1 + alto
        binding.tvVidriosR.text = if (altoHoja() <= altoUtil()) {
            "${df1(ancho)} x ${df1(ale)} = ${nFijos()}\n" +
                    "${df1(ancho)} x ${df1(alto)} = ${nCorredizas()}\n" +
                    "${df1((altoUtil() - altoHoja()) - 2.9f)} x ${divMocheta(mPuentes1())} = ${nPuentes()}"
        } else {
            "${df1(ancho)} x ${df1(ale)} = ${nFijos()}\n" +
                    "${df1(ancho)} x ${df1(alto)} = ${nCorredizas()}"
        }
    }

    //  CAMBIOS DE SERIE
    @SuppressLint("SetTextI18n")
    private fun actualizarVentana() {
        // Verificar que la lista no esté vacía
        if (series.isNotEmpty()) {
            // Actualizar el texto de txVentana con el elemento actual
            binding.txVentana.text = "Ventana de aluminio ${series[indices]}"
            binding.txRiel.text = when (series[indices]){
                "Clásica" -> {
                    "Riel"}
                "Serie 20" -> {
                    "Riel Sup."}
                "Serie 3825" -> {
                    "D. Riel Sup."}
                else -> {""}
            }

            // Incrementar el índice para la próxima vez
            indices = (indices + 1) % series.size
        } else {
            // Manejar el caso en que la lista esté vacía (opcional)
            binding.txVentana.text = "Sin ventanas disponibles"
        }
    }

    private fun visibleVentana(){
        val series = series[indices]
        when (series){
            "Clásica" -> {
                binding.lyMarco.visibility = View.GONE
                binding.lyJamba.visibility = View.GONE
                binding.lyTubo.visibility = View.GONE
                binding.lyParante.visibility = View.GONE
                binding.lyRielI.visibility = View.GONE
                binding.lyZocalo.visibility = View.GONE
                binding.lyRielS.visibility = View.GONE
                binding.lyTraslapo.visibility=View.GONE
                binding.lyCabezal.visibility=View.GONE
                binding.lyAdaptador.visibility=View.GONE
            }
            "Serie 20"-> {
                binding.lyMarco.visibility = View.GONE
                binding.lyJunki.visibility = View.GONE
                binding.lyTubo.visibility = View.GONE
                binding.lyParante.visibility = View.GONE
                binding.lyRiel.visibility = View.GONE
                binding.lyZocalo.visibility = View.GONE
                binding.lyTope.visibility = View.GONE
            }
        "Serie 3825",
        "Serie 35",
        "Serie Española"->{}
        }
    }

    //   FUNCIONES SERIE 2O

    //   FUNCIONES GENERALES
    private fun anchUtil(): Float {
        val ancho = etAncho.text.toString().toFloat()
        val marco = etMarco.text.toString().toFloat()
        return ancho - (2 * marco)

    }

    private fun altoUtil(): Float {
        val alto = binding.etAlto.text.toString().toFloat()
        val marco = binding.etMarco.text.toString().toFloat()
        return alto - (2 * marco)
    }

    private fun zoc(): Float {
        val div = divisiones()
        val cruce = when (div) {
            2, 3, 5, 7, 9, 11, 13, 15 -> div - 1
            4, 6, 10 -> div - 2
            8, 12 -> div / 2
            14 -> div - 4
            else -> div
        }
        val partes = ((anchUtil() - ((nPuentes() - 1) * 2.5f)) + (cruce * 3.2f)) / div
        return if (div == 1) anchUtil() else partes
    }

    private fun paran(): Float {
        return altoHoja() - 1.2f
    }
    private fun nTuboMocheta(p1: Float): Int {
        return (p1 / 120.4f).toInt()
    }

    private fun nFijos():Int {
        return when (divisiones()){
            1 -> 1  2 -> 1
            3 -> 2  4 -> 2
            5 -> 3
            6 -> 4  7 -> 4
            8 -> 4
            9 -> 5
            10 ->6  11 -> 6  12 -> 6
            13 ->7  14 ->8   15 -> 8
            else -> 0
        }
    }
    private fun nCorredizas():Int {
        return when (divisiones()){
            1 -> 0
            2 -> 1   3 -> 1
            4 -> 2   5 -> 2   6 -> 2
            7 -> 3
            8 -> 4   9 -> 4   10 -> 4
            11-> 5
            12-> 6   13-> 6   14 -> 6
            15-> 7
            else -> 0
        }
    }
    private fun nPuentes(): Int {
        return when (divisiones()) {
            1, 2, 3, 4, 5 -> 1
            6, 8 -> 2
            7, 9, 11, 13, 15 -> 1
            10, 12, 14 -> 3
            else -> 0
        }
    }

    private fun mPuentes1(): Float {
        val ancho = anchUtil()
        return when (divisiones()) {
            1, 2, 3, 4, 5, 7, 9, 11, 13, 15 -> ancho
            6, 8 -> (ancho - 2.5f) / 2
            10 -> ((ancho - (2 * 2.5f)) / divisiones()) * 3
            12 -> (ancho - (2 * 2.5f)) / 3
            14 -> ((ancho - (2 * 2.5f)) / divisiones()) * 5
            else -> 0f
        }
    }

    private fun mPuentes2(): Float {
        val ancho = anchUtil()
        return when (divisiones()) {
            10, 14 -> ((ancho - (2 * 2.5f)) / divisiones()) * 4
            else -> 0f
        }
    }
    private fun divMocheta(p1: Float): Float {
        val multi = p1 / 120.4f
        val multiplo = multi.toInt() + 1
        return p1 / multiplo
    }
    private fun nMocheta() {
        val n = binding.etMocheta.text.toString().toFloat()
        val xn = if (n == 0f) { // Reemplazado ColumnText.GLOBAL_SPACE_CHAR_RATIO por 0f
            nTuboMocheta(mPuentes1()).toString()
        } else {
            n.toString()
        }
        binding.txPruebas.text = xn
    }
    private fun altoHoja(): Float {
        val alto = altoUtil()
        val hoja = binding.etAltohoja.text.toString().toFloat()
        val corre = if (hoja > alto) alto else hoja
        return if (hoja == 0f) { // Reemplazado ColumnText.GLOBAL_SPACE_CHAR_RATIO por 0f
            (alto / 7) * 5
        } else {
            corre
        }
    }

    private fun divisiones(): Int {
        val ancho = binding.etAncho.text.toString().toFloatOrNull() ?: 0f
        val divis = binding.etPartes.text.toString().toIntOrNull() ?: 0
        return if (divis == 0) {
            ((ancho / 60f).toInt() + if (ancho % 60f > 0f) 1 else 0)
        } else {
            divis
        }
    }

}
//serie 20 2 hojas
// vidrio = anccho/2 -5 x alto - 10
//zócalo y cabezal = ancho/2 - 6.4
//riel sup e inf = ancho- 1.2
//parante, traslape = alto - 2.7