package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityMamparaVidrioBinding

class MamparaVidrioActivity : AppCompatActivity() {
   
private lateinit var binding: ActivityMamparaVidrioBinding
private val hoja: Float = 199.0f
private var contadorVentana: Int = 1
private val ventanas: MutableMap<String, MutableMap<String, Any>> = LinkedHashMap()

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMamparaVidrioBinding.inflate(layoutInflater)
    setContentView(binding.root)

    cliente()
    calcular()

    binding.lyU.visibility = View.GONE
    binding.u38layout.visibility = View.GONE
    binding.ulayout.visibility = View.VISIBLE

    binding.btArchivar.setOnClickListener {
        //bloqueDatos()
        binding.med2.setText("")
        binding.med1.setText("")
    }

    binding.btArchivar.setOnLongClickListener {
        val intent = Intent(this, FichaActivity::class.java)
        intent.putExtra("materiales", HashMap(ventanas))
        startActivity(intent)
        true
    }
}

private fun cliente() {
    binding.lyCliente.visibility = View.GONE
    /*val paqueteR = intent.extras
    val cliente = Ref.ObjectRef<String?>()
    cliente.element = paqueteR?.getString("rcliente")
    if (cliente.element != null) {
        binding.tvId.text = "mampara " + cliente.element
    } else {
        binding.tvId.text = "mampara"
    }
    binding.tvId.setOnClickListener {
        binding.lyCliente.visibility = View.VISIBLE
        binding.clienteEditxt.setText(cliente.element ?: "")
        binding.btGo.setOnClickListener { _ ->
            cliente.element = binding.clienteEditxt.text.toString()
            binding.tvId.text = "mampara " + cliente.element
            binding.lyCliente.visibility = View.GONE
        }
    }*/
}

private fun calcular() {
    binding.btCalcular.setOnClickListener {
        try {
            uVisible()
            aVisible()

            u13()
            u38()
            uOtros()
            otrosAluminios()
            vidrios()
            referencias()


        } catch (e: Exception) {
            Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
        }
        binding.u38tx.text = "Zócalo"
        binding.txPr.text= binding.med2.text.toString()
    }
}

private fun mostrarDatosEnTextView(textView: TextView) {
    val sb = StringBuilder()
    for ((etiquetaVentana, ventana) in ventanas) {
        Log.d("MostrarDatos", "Procesando ventana: $etiquetaVentana")
        sb.append("$etiquetaVentana:\n")
        for ((clave, valor) in ventana) {
            Log.d("MostrarDatos", "Procesando dato: $clave")
            if (valor is Map<*, *>) {
                sb.append("$clave:\n")
                for ((claveSub, valorSub) in valor) {
                    sb.append("$claveSub: $valorSub\n")
                }
            } else {
                sb.append("$clave: $valor\n")
            }
        }
        sb.append("\n")
    }
    textView.text = sb.toString()
}

/*private fun bloqueDatos() {
    binding.btArchivar.setOnClickListener {
        val clienteStr = binding.txCliente.text.toString()
        val medida = binding.txMedidaAncho.text.toString()
        val etiquetaVentana = "Ventana $contadorVentana"
        val ventanaActual: MutableMap<String, Any> = LinkedHashMap()
        ventanaActual["Cliente"] = clienteStr
        ventanaActual["Medida"] = medida

        val materiales: MutableMap<String, Any> = LinkedHashMap()
        if (binding.u38layout.visibility == View.VISIBLE) {
            val listaU38 = binding.u38tx.let { tv ->
                listaSplit(tv).flatten().map { it.toFloat() }.toMutableList()
            }
            materiales["U38"] = listaU38
        }
        if (binding.lyU.visibility == View.VISIBLE) {
            val listaU13 = binding.txU.let { tv ->
                listaSplit(tv).flatten().map { it.toFloat() }.toMutableList()
            }
            materiales["U13"] = listaU13
        }
        if (binding.vidrioLayout.visibility == View.VISIBLE) {
            val listaVidrio = binding.txV.let { tv ->
                listaSplit(tv).flatten().map { it.toFloat() }.toMutableList()
            }
            materiales["Vidrio"] = listaVidrio
        }
        ventanaActual["Materiales"] = materiales
        ventanas[etiquetaVentana] = ventanaActual
        contadorVentana++
        binding.med1.setText("")
        binding.med2.setText("")
    }
}*/

private fun listaSplit(textView: TextView): List<List<Float>> {
    val texto = textView.text.toString()
    val lineas = texto.split("\n")
    val listas = ArrayList<List<Float>>()
    for (linea in lineas) {
        val parts = linea.split("x", "=").map { it.trim().toFloat() }
        listas.add(parts)
    }
    return listas
}

private fun puntosU(): String {
    val partes = uFijos()
    val cruce = cruce()
    val punto1 = df1((partes * 2) - (cruce * 2)).toFloat()
    val punto2 = df1((partes * 4) - (cruce * 4)).toFloat()
    val punto3 = df1((partes * 6) - (cruce * 6)).toFloat()
    return when (divisiones()) {
        5, 6 -> df1((partes * 2) - (cruce * 2))
        7, 10, 14 -> "${df1(punto1)}_${df1(punto2)}"
        8, 12 -> df1((3 * partes) - (cruce * 2))
        9, 11, 13, 15 -> "${df1(punto1)}_${df1(punto2)}_${df1(punto3)}"
        else -> ""
    }
}

@SuppressLint("SetTextI18n")
private fun referencias() {
    val ancho = binding.med1.text.toString().toFloat()
    val alto = binding.med2.text.toString().toFloat()
    val hojaVal = binding.hojatx.text.toString().toFloat()
    binding.txReferencias.text =
        "An: ${df1(ancho)}  x  Al: ${df1(alto)}\nAltura de puente:" +
                if (alto > hojaVal) df1(altoHoja()) else "sin puente" +
                        "\nDivisiones: ${divisiones()} -> Fs: ${nFijos()};Cs: ${nCorredizas()}" +
                        if (divisiones() > 4) "\nPuntos: ${puntosU()}" else ""
}

private fun u13() {
    val alto = binding.med2.text.toString().toFloat()
    val hojaVal = binding.hojatx.text.toString().toFloat()
    val uFijosVal = df1(uFijos()).toFloat()
    val uParanteVal = df1(uParante()).toFloat()
    val uParante2Val = df1(uParante2()).toFloat()
    val uSuperiorVal = df1(uSuperior()).toFloat()
    binding.txU.text = if (alto > hojaVal) {
        if (divisiones() == 2)
            "${df1(uFijosVal)} = ${nFijos()}\n${df1(uParanteVal)} = ${fijoUParante()}\n${df1(uParante2Val)} = 1\n${df1(uSuperiorVal)} = 1"
        else if (divisiones() == 1)
            "${df1(uFijosVal)} = 2\n${df1(uParanteVal)} =2"
        else
            "${df1(uFijosVal)} = ${nFijos()}\n${df1(uParanteVal)} = ${fijoUParante()}\n${df1(uSuperiorVal)} = 1"
    } else {
        if (divisiones() == 2)
            "${df1(uFijosVal)} = ${nFijos()}\n${df1(uParanteVal)} = ${fijoUParante()}"
        else if (divisiones() == 1)
            "${df1(uFijosVal)} = 2\n${df1(uParanteVal)} =2"
        else
            "${df1(uFijosVal)} = ${nFijos()}\n${df1(uParanteVal)} = ${fijoUParante()}"
    }
}

@SuppressLint("SetTextI18n")
private fun u38() {
    val uFijosVal = df1(uFijos()).toFloat()
    binding.u38tx.text = "${df1(uFijosVal)} = ${nFijos() + nCorredizas()}"
}

private fun uOtros() {
    val alto = binding.med2.text.toString().toFloat()
    val hojaVal = binding.hojatx.text.toString().toFloat()
    val us = binding.ueditx.text.toString().toFloat()
    val uFijosVal = df1(uFijos()).toFloat()
    val uParanteVal = df1(uParante()).toFloat()
    val uParante2Val = df1(uParante2()).toFloat()
    val uSuperiorVal = df1(uSuperior()).toFloat()
    if (alto > hojaVal) {
        if (us != 0f)
            binding.uxtx.text = if (divisiones() == 2)
                "${df1(uFijosVal)} = ${nFijos()}\n${df1(uParanteVal)} = ${fijoUParante()}\n${df1(uParante2Val)} = 1\n${df1(uSuperiorVal)} = 1"
            else if (divisiones() == 1)
                "${df1(uFijosVal)} = 2\n${df1(uParanteVal)} =2"
            else
                "${df1(uFijosVal)} = ${nFijos()}\n${df1(uSuperiorVal)} = 1"
    } else {
        if (us != 0f)
            binding.uxtx.text = if (divisiones() == 2)
                "${df1(uFijosVal)} = ${nFijos()}\n${df1(uParanteVal)} = ${fijoUParante()}\n${df1(uParante2Val)} = 1"
            else if (divisiones() == 1)
                "${df1(uFijosVal)} = 2\n${df1(uParanteVal)} =2"
            else
                "${df1(uFijosVal)} = ${nFijos()}\n${df1(uParanteVal)} = ${fijoUParante()}"
        else
            binding.uxtx.text = if (divisiones() == 2)
                "${df1(uFijosVal)} = ${nFijos()}"
            else if (divisiones() == 1)
                "${df1(uFijosVal)} = 2"
            else
                "${df1(uFijosVal)} = ${nFijos()}"
    }
}

@SuppressLint("SetTextI18n")
private fun otrosAluminios() {
    binding.txT.text = puentes()
    binding.txR.text = if (divisiones() != 1) rieles() else ""
    binding.txUf.text = if (divisiones() != 1) rieles() else ""
    binding.txFc.text = if (divisiones() != 1) rieles() else ""
    binding.txH.text = "${df1(hache())} = ${nCorredizas()}"
    if (divisiones() == 2)
        binding.txTo.text = "${df1(altoHoja() - 0.9f)} = 1"
    else
        binding.lyTo.visibility = View.GONE
    binding.txPf.text = "${df1(portafelpa())} = ${divDePortas()}"
}

private fun vidrios() {
    val vidrioFijo = if (divisiones() > 1)
        "${vidrioFijo()}\n${vidrioCorre()}\n${vidrioMocheta()}"
    else
        vidrioFijo()
    binding.txV.text = vidrioFijo
}

private fun uVisible() {
    val parseFloat = binding.ueditx.text.toString().toFloat()
    if (parseFloat == 1.0f) {
        binding.lyU.visibility = View.GONE
        binding.u38layout.visibility = View.VISIBLE
        binding.ulayout.visibility = View.GONE
    } else if (parseFloat == 1.5f) {
        binding.lyU.visibility = View.VISIBLE
        binding.u38layout.visibility = View.VISIBLE
        binding.ulayout.visibility = View.GONE
    } else {
        binding.lyU.visibility = View.GONE
        binding.u38layout.visibility = View.VISIBLE
        binding.ulayout.visibility = View.VISIBLE
    }
}

private fun aVisible() {
    val alto = binding.med2.text.toString().toFloat()
    val hojaVal = binding.hojatx.text.toString().toFloat()
    binding.lyH.visibility = if (divisiones() == 1) View.GONE else View.VISIBLE
    binding.lyTo.visibility = if (divisiones() == 2) View.VISIBLE else View.GONE
    binding.lyPf.visibility = if (divisiones() == 1) View.GONE else View.VISIBLE
    binding.lyUf.visibility = if (divisiones() == 1 || alto <= hojaVal) View.GONE else View.VISIBLE
    binding.lyFijoCorre.visibility = if (alto <= hojaVal && divisiones() > 1) View.VISIBLE else View.GONE
    binding.lyTubo.visibility = if (alto <= hojaVal) View.GONE else View.VISIBLE
}

/*private fun dVisible() {
    // NOTA: La implementación de este método fue decompilada de forma incorrecta en el código Java original.
    // Se debe implementar la lógica correspondiente aquí.
    throw UnsupportedOperationException("dVisible no está implementado")
}*/

@SuppressLint("DefaultLocale")
private fun df1(defo: Float): String {
    val str = defo.toString()
    val resultado = if (str.endsWith(".0")) {
        str.replace(".0", "")
    } else {
        String.format("%.1f", defo)
    }
    return resultado.replace(",", ".")
}

private fun uFijos(): Float {
    val ancho = binding.med1.text.toString().toFloat()
    val divs = when (divisiones()) {
        2, 3, 5, 7, 9, 11, 13, 15 -> divisiones() - 1
        4, 6, 10 -> divisiones() - 2
        8, 12 -> divisiones() / 2
        14 -> divisiones() - 4
        else -> divisiones() - 1
    }
    val cruce = divs * cruce()
    val partes = (ancho + cruce) / divisiones()
    return if (divisiones() == 1) ancho else partes
}

private fun uParante(): Float {
    val alto = binding.med2.text.toString().toFloat()
    return alto - 6
}

private fun uParante2(): Float {
    val alto = binding.med2.text.toString().toFloat()
    val us = binding.ueditx.text.toString().toFloat()
    return ((alto - altoHoja()) - us) + 1.5f
}

private fun uSuperior(): Float {
    return binding.med1.text.toString().toFloat()
}

private fun rieles(): String {
    val alto = binding.med2.text.toString().toFloat()
    val hojaVal = binding.hojatx.text.toString().toFloat()
    val ancho = binding.med1.text.toString().toFloat()
    val mPuentes6 = df1(mPuentes() - 0.06f)
    val mPuentesVal = df1(mPuentes())
    val mPuentes2Val = df1(mPuentes2())
    val ancho6 = df1(ancho - 0.06f)
    return if (alto >= hojaVal) {
        if (divisiones() != 14)
            "$mPuentes6 = ${nPuentes()}"
        else
            "$mPuentesVal = ${nPuentes() - 1}\n$mPuentes2Val = ${nPuentes() - 2}"
    } else {
        "$ancho6 = 1"
    }
}

private fun puentes(): String {
    val alto = binding.med2.text.toString().toFloat()
    val mPuentes6 = df1(mPuentes() - 0.06f)
    val mPuentes2Val = df1(mPuentes2())
    return if (divisiones() in 6 until 13 && divisiones() % 2 == 0)
        "$mPuentes6 = ${nPuentes()}\n${df1(alto)} = ${nPuentes() - 1}"
    else if (divisiones() == 14)
        "$mPuentes6 = ${nPuentes() - 1}\n$mPuentes2Val = ${nPuentes() - 2}\n${df1(alto)} = ${nPuentes() - 1}"
    else
        "$mPuentes6 = ${nPuentes()}"
}

private fun portafelpa(): Float {
    return altoHoja() - 5
}

private fun hache(): Float {
    return df1(uFijos()).toFloat()
}

    private fun vidrioFijo(): String {
        val alto = altoHoja()
        val uFijosVal = df1(uFijos()).toFloat()
        val uFijos4 = df1(uFijos() - 0.4f)
        val uFijos2 = df1(uFijos() - 0.2f)
        val altDes = df1(alto - 6)
        val div = divisiones()
        return when (div) {
            in 0..4 -> "$uFijos4 x $altDes = ${nFijos()}"
            6, 8 -> "$uFijos4 x $altDes = 2\n$uFijos2 x $altDes = 2"
            10 -> "$uFijos4 x $altDes = 2\n$uFijos2 x $altDes = 2\n$uFijosVal x $altDes = 2"
            12 -> "$uFijos4 x $altDes = 2\n$uFijos2 x $altDes = 4"
            14 -> "$uFijos4 x $altDes = 2\n$uFijos2 x $altDes = 4\n$uFijosVal x $altDes = 2"
            else -> "$uFijos4 x $altDes = 2\n$uFijosVal x $altDes = ${nFijos() - 2}"
        }
    }


private fun vidrioCorre(): String {
    val ancho = df1(hache() - 1.2f).toFloat()
    val alto = df1(altoHoja() - 7.0f).toFloat()
    return "${df1(ancho)} x ${df1(alto)} = ${nCorredizas()}"
}

private fun vidrioMocheta(): String {
    val alto = binding.med2.text.toString().toFloat()
    val ancho = binding.med1.text.toString().toFloat()
    val hojaVal = binding.hojatx.text.toString().toFloat()
    val mas1 = df1((alto - altoHoja()) + 1)
    val axnfxuf = df1((ancho - (nFijos() * uFijos())) - 0.6f)
    val axnfxuf2 = df1(((ancho - (nFijos() * uFijos())) / 2) - 0.6f)
    val axnfxuf3 = df1(((ancho - (nFijos() * uFijos())) / 3) - 0.6f)
    val axnfxufn = df1(((ancho - (nFijos() * uFijos())) / nCorredizas()) - 0.6f)
    return if (divisiones() <= 1 || alto <= hojaVal) ""
    else if (divisiones() == 4)
        "${df1(mas1.toFloat())} x ${df1(axnfxuf.toFloat())} = 1"
    else if (divisiones() == 8)
        "${df1(mas1.toFloat())} x ${df1(axnfxuf2.toFloat())} = 2"
    else if (divisiones() == 12)
        "${df1(mas1.toFloat())} x ${df1(axnfxuf3.toFloat())} = 3"
    else if (divisiones() == 14)
        "${df1(mas1.toFloat())} x ${df1(axnfxuf2.toFloat())} = 1\n${df1(mas1.toFloat())} x ${df1(axnfxuf.toFloat())} = 4"
    else
        "${df1(mas1.toFloat())} x ${df1(axnfxufn.toFloat())} = ${nCorredizas()}"
}

// Métodos "stub" o básicos. Se deben completar según la lógica original.
private fun altoHoja(): Float{
    val alto=binding.med2.text.toString().toFloat()
    val hHoja=binding.hojatx.text.toString().toFloat()
    return when {
        hHoja==0f -> when{
            alto>210f && (hoja)< alto-5.3-> {hoja}
            alto<=210f&&alto>hoja->{190f}
            alto<=hoja -> {(alto)}
            (hoja)> alto-5.3-> {(alto)}
            else -> {(alto)}}

        alto<=hHoja || (hHoja)> alto-5.3-> {(alto)}
        else -> {hHoja}
    }
}
    private fun nFijos(): Int {
        return when (divisiones()) {
            1 -> 1
            2 -> 1
            3 -> 2
            4 -> 2
            5 -> 3
            6 -> 4
            7 -> 4
            8 -> 4
            9 -> 5
            10 -> 6
            11 -> 6
            12 -> 6
            13 -> 7
            14 -> 8
            15 -> 8
            else -> 0
        }
    }
    private fun nCorredizas(): Int {
        return when (divisiones()) {
            1 -> 0
            2 -> 1
            3 -> 1
            4 -> 2
            5 -> 2
            6 -> 2
            7 -> 3
            8 -> 4
            9 -> 4
            10 -> 4
            11 -> 5
            12 -> 6
            13 -> 6
            14 -> 6
            15 -> 7
            else -> 0
        }
    }
private fun nPuentes(): Int = 0
private fun mPuentes(): Float = 0f
private fun mPuentes2(): Float = 0f
    private fun divDePortas(): String {
        return when (divisiones()) {
            1 -> ""
            2, 4, 8, 12 -> "${nCorredizas() * 3}"
            14 -> "${(nCorredizas() * 4) - 2}"
            else -> "${nCorredizas() * 4}"
        }
    }
    private fun fijoUParante(): Int {
        return when (divisiones()) {
            1 -> 2
            2 -> 1
            in 3..15 -> 2
            else -> 0
        }
    }

private fun cruce(): Float {
    val exacto = binding.etCruce.text.toString().toFloat()
    val cruceValue = if (divisiones() == 4 || divisiones() == 8 || divisiones() > 12) 0.8f else 0.7f
    return if (exacto == 0f) cruceValue else exacto
}

private fun f15(): String {
    val mF = portafelpa()
    val nF = divDePortas().toFloatOrNull() ?: 0f
    val xF = "$mF = $nF"
    val totalF = df1(mF * nF)
    return "$xF\n$totalF"
}

private fun f10(): String {
    val mF = rieles().toFloatOrNull() ?: 0f
    val nF = rieles().toFloatOrNull() ?: 0f
    val xf = "$mF = ${df1(2 * nF)}"
    val totalF = df1((xf.toFloatOrNull() ?: 0f) * nF)
    return "$xf\n$totalF"
}

private fun tarugos() {
    // Implementar según sea necesario.
}

private fun divisiones(): Int {
    val ancho = binding.med1.text.toString().toFloat()
    val divis = binding.partesNfcfi.text.toString().toIntOrNull() ?: 0
    if (divis == 0) {
        return when {
            ancho <= 120f -> 1
            ancho in 120f..240f -> 2
            ancho in 240f..360f -> 3
            ancho in 360f..480f -> 4
            ancho in 480f..600f -> 5
            ancho in 600f..720f -> 6
            ancho in 720f..840f -> 7
            ancho in 840f..960f -> 8
            ancho in 480f..540f -> 9
            ancho in 960f..1080f -> 10
            ancho in 1080f..1200f -> 11
            ancho in 1200f..1320f -> 12
            ancho in 1320f..1400f -> 13
            ancho in 1400f..1560f -> 14
            ancho in 1560f..1600f -> 15
            else -> divis
        }
    }
    return divis
}

}
