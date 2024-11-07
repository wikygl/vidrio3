package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.ListaActivity
import crystal.crystal.R
import crystal.crystal.databinding.ActivityPuertaPanoBinding

/*class Puertas : AppCompatActivity() {
    private lateinit var binding: ActivityPuertaPanoBinding

    private val hoja: Float = 199f
    private val bastidor = 8.25f
    private val marco = 2.2f

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuertaPanoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cliente()

        binding.btCalcular.setOnClickListener {
            try {
                val marcop = binding.etMed2.text.toString().toFloat()

                //OPCIONES DE VISIBILIDAD

                panos()

                // MATERIALES

                binding.tvMarco.text = "${df1(marcop)} = 2\n${df1(marcoSuperior())} = 1"

                binding.tvPaflon.text =
                    "${df1(paflon())} = ${nPaflones()}\n${df1(parante())} = 2"

                junkillos()

                binding.angtxtP.text =
                    "${df1(marcoSuperior())} = 1\n${df1(hPuente())} = 2"

                binding.vidriostxtP.text =if (mocheta()< 0f){ "${vidrioH()} = ${nPfvcal()-1}"}
                else{ "${vidrioH()} =${nPfvcal()-1}\n" +
                        "${vidrioM()} = 1"}


            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnArchivarp.setOnClickListener {

            if (binding.etMed1.text.isNotEmpty()){
                val paquete = Bundle().apply {
                    putString("mat",binding.txMarco.toString())
                    putString("res", binding.tvMarco.text.toString())
                }
                val intent = Intent(this, ListaActivity::class.java)
                intent.putExtras(paquete)
                startActivity(intent)
            }
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                binding.tvMarco.text.toString()))

            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                binding.tvPaflon.text.toString()))

            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                binding.junkitxtP.text.toString()))

            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                binding.angtxtP.text.toString()))

            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                binding.vidriostxtP.text.toString()))
        }

    }

    @SuppressLint("SetTextI18n")
    private fun cliente(){

        binding.lyCliente.visibility=View.GONE

        val paqueteR=intent.extras
        //VARIABLES DE MATERIALES

        var cliente= paqueteR?.getString("rcliente")

        if (cliente!=null) {binding.tvTitulo.text= "puerta de paños $cliente"}

        else  {binding.tvTitulo.text= "puerta de paños"}

        binding.tvTitulo.setOnClickListener {
            binding.lyCliente.visibility=View.VISIBLE
            binding.clienteEditxt.setText("$cliente")

            binding.btGo.setOnClickListener {
                cliente = binding.clienteEditxt.text.toString()
                binding.tvTitulo.text="puerta de paños $cliente"

                binding.lyCliente.visibility=View.GONE }
        }
    }

        @SuppressLint("DefaultLocale")
        private fun df1(defo: Float): String {
            return if (defo % 1 == 0f) {
                defo.toInt().toString()
            } else {
                String.format("%.1f", defo).replace(',', '.')
            }
        }

        @SuppressLint("SetTextI18n")
        private fun junkillos() {
            val junInput = binding.etJunki.text.toString()
            val jun = junInput.toFloatOrNull() ?: 0f
            val divisionesValor = divisiones()
            val mJunki = df1(divisionesValor - (2 * jun))

            binding.junkitxtP.text = mJunki
        }
        private fun marcoSuperior(): Float {
            val anchoInput = binding.etMed1.text.toString()
            val ancho = anchoInput.toFloatOrNull() ?: 0f
            return ancho - (2 * bastidor)
        }

        private fun paflon(): Float {
            val anchoInput = binding.etMed1.text.toString()
            val ancho = anchoInput.toFloatOrNull() ?: 0f
            return ((ancho - (bastidor * 2)) - 1.0f) - (2 * bastidor)
        }
        private fun nPaflones(): Int {
        val divi2 = binding.etDivi.text.toString().toInt()
        val nBast = binding.etZocalo.text.toString().toInt()
        return if (nBast > 1) {divi2 + nBast } else {divi2 + 1}
    }
        private fun parante(): Float {
            val pisoInput = binding.etPiso.text.toString()
            val piso = pisoInput.toFloatOrNull() ?: 0f
            return if (piso == 0f) {
                hPuente() - 1.0f
            } else {
                (hPuente() - 0.5f) - piso
            }
        }
        private fun hPuente(): Float {
            val alto = binding.etMed2.text.toString().toFloatOrNull() ?: 0f
            val hHoja = binding.etHoja.text.toString().toFloatOrNull() ?: 0f
            val pisoInput = binding.etPiso.text.toString()
            val piso = pisoInput.toFloatOrNull() ?: 0f

            return if (hHoja == 0f) {
                when {
                    alto > 210f -> {
                        val hojaPlusPiso = hoja + piso
                        if (hojaPlusPiso < alto - 5.3f) hojaPlusPiso else alto - bastidor
                    }
                    alto > hoja && (hoja + piso) <= alto - 5.3f -> (alto - bastidor) + piso
                    else -> alto - bastidor
                }
            } else {
                if (alto <= hHoja || (hHoja + piso) > alto - 5.3f) {
                    alto - bastidor
                } else {
                    hHoja + piso
                }
            }
        }
    private fun divisiones(): Float {
        val divi2 = binding.etDivi.text.toString().toFloat()
        val nZoca = binding.etZocalo.text.toString().toInt()
        val mZoca = zocalo()
        val divis = if (nZoca>1){parante()-mZoca}else{parante()}
        val nbas = if(nZoca>1){divi2 * bastidor}else{(divi2 + 1) * bastidor}
        return (divis - nbas) / divi2
    }
    private fun nPfvcal(): Int {
            val divi2 = binding.etDivi.text.toString().toIntOrNull() ?: 0
            return divi2 + 1
        }
        private fun nZocalo(): Int {
            val nBast = binding.etZocalo.text.toString().toInt()
            return if (nBast == 0) 1 else nBast
        }
        private fun zocalo(): Float {
            val mzoca = (nZocalo()) * bastidor
            return df1(mzoca).toFloatOrNull() ?: 0f
        }
        private fun mocheta(): Float {
            val alto = binding.etMed2.text.toString().toFloat()
            return alto - (hPuente() + bastidor)
        }
        private fun vidrioH(): String {
            val jun = binding.etJunki.text.toString().toFloatOrNull() ?: 0f
            val holgura = if (jun == 0f) 0.4f else 0.6f
            val anchv = paflon() - holgura
            val altv = divisiones() - holgura
            return "${df1(anchv)} x ${df1(altv)}"
        }
        private fun vidrioM(): String {
            val jun = binding.etJunki.text.toString().toFloatOrNull() ?: 0f
            val holgura = if (jun == 0f) 0.4f else 0.6f
            val uno = marcoSuperior() - holgura
            val dos = mocheta() - holgura
            return "${df1(uno)} x ${df1(dos)}"
        }
        @SuppressLint("SetTextI18n")
        private fun panos():String{
        val z = zocalo()
        val n = (nPfvcal()-1)//cantidad paflones superiores y cantidad de paños
        val j=df1(divisiones()).toFloat()//tamaño de cada paño
        val b = bastidor
        val g = j+b
        val mPanos=when (n) {
            in 1..17 -> {
                List(n) { index -> (z + j + (index * (j + b))).toString() }
            }
            else -> listOf(((1 * g) + z) - b).map { it.toString() }
        }.toTypedArray()

        var x = ""
        for (i in mPanos){
            x+="${df1(i.toFloat())}\n"
        }
        binding.binding.tvEnsayo.text= "$z\n$x"
        binding.binding.tvEnsayo.text =binding.binding.tvEnsayo.text.substring(0, binding.binding.tvEnsayo.text.length-1)
        return binding.binding.tvEnsayo.text as String
    }
        /*private fun tali(): String {
            val nBast = binding.etZocalo.text.toString().toInt()
            val nv = binding.etDivi.text.toString().toFloatOrNull() ?: 0f
            val ancho1 = paflon()
            val divisor = (ancho1 / bastidor).toInt()
            val par = if (divisor % 2 != 0) divisor - 1 else divisor
            val x = ancho1 - (par * bastidor)
            val t = if (x <= 9.9f) par - 2 else par
            val m = ancho1 - (t * bastidor)
            val z = if (m > 18.0f) m - (cuad1med * 2) else m
            val h = sqrt((z * z) + (z * z))

            return "${df1(z)}= ${df1(nv + 1)}\n${df1(paflon())} = ${nBast + 1}\n${df1(parante())} = 2\n" +
                    "${df1(parante() - ((nBast + 1) * bastidor))} = $t\n${df1(h)} = ${df1(1 + nv)}"
        }*/
        private fun adelin(): String {
            val jun = binding.etJunki.text.toString().toFloatOrNull() ?: 0f
            val paranteValor = parante()
            val vid = ((paranteValor - (bastidor * 4)) - (bastidor * 6)) / 7
            val vod = vid - (jun * 2)
            val vi = df1(vid)
            val vo = df1(vod)
            val v = if (vi == vo) {
                "$vi = 28"
            } else {
                "$vi = 14\n${df1(vid - jun)} = 14"
            }
            val resto = paflon() - (bastidor + vid)
            return "${df1(paflon())} = 2\n${df1(bastidor - (jun * 1))} = 2\n$v\n${df1(resto)} = 2\n" +
                    "${df1(parante() - ((bastidor * 1) + (jun * 1)))} = 2\n${df1(marcoSuperior())} = 2\n${df1(mocheta() - (1 * jun))} = 2"
        }
        @SuppressLint("SetTextI18n")
        private fun mela() {
            val paflonValor = paflon()
            val paranteValor = parante()
            val vid = paflonValor - (((paranteValor - (bastidor * 4)) - (6 * bastidor)) / 7 + bastidor)
            val alt = parante() - (bastidor * 4)
            binding.tvMarcoela.text = "${df1(vid - 0.3f)}x ${df1(alt - 0.3f)} = 1\n${df1(paflon() - 0.3f)} X ${df1(bastidor - 0.3f)}"
        }
    }*/

class Puertas : AppCompatActivity() {

    private val hoja = 199f
    private val marco = 2.2f
    private val bastidor = 8.25f
    private val unoMedio = 3.8f

    private var indice = 0
    private val tipos = listOf("Mari","Dora","Adel","Mili","jeny","Taly","Viky","Lina","Tere")


    private lateinit var binding: ActivityPuertaPanoBinding

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPuertaPanoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cliente()
        tipos()

        binding.btCalcular.setOnClickListener {
            try {
                val marcop = binding.etMed2.text.toString().toFloat()

                //OPCIONES DE VISIBILIDAD

                dVisible()
                panos()
                array()

                // MATERIALES

                binding.tvMarco.text = "${df1(marcop)} = 2\n${df1(marcoSuperior())} = 1"

                binding.tvPaflon.text = "${df1(paflon())} = ${nPaflones()}\n${df1(parante())} = 2"

                junkillos()

                binding.tvTope.text = "${df1(marcoSuperior())} = 1\n${df1(hPuente())} = 2"

                binding.tvVidrios.text =if (mocheta()< 0f){ "${vidrioH()} = ${nPfvcal()-1}"}
                else{ "${vidrioH()} =${nPfvcal()-1}\n" +
                        "${vidrioM()} = 1"}

                binding.txRefe.text = referen()
                if(tubo()==""){binding.lyTubo.visibility = View.GONE}else{binding.lyTubo.visibility = View.VISIBLE}

                binding.tvTubo.text="${tubo()} = 1"

            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btArchivar.setOnClickListener {

            if (binding.etMed1.text.isNotEmpty()){
                val paquete = Bundle().apply {
                    putString("mat",binding.tvMarco.toString())
                    putString("res", binding.tvMarco.text.toString())
                }
                val intent = Intent(this, ListaActivity::class.java)
                intent.putExtras(paquete)
                startActivity(intent)
            }
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                binding.tvMarco.text.toString()))

            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                binding.tvPaflon.text.toString()))

            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                binding.tvJunkillo.text.toString()))

            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                binding.tvTope.text.toString()))

            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                binding.tvVidrios.text.toString()))
        }
    }
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    private fun cliente(){

        binding.lyCliente.visibility=View.GONE

        val paqueteR=intent.extras
        //VARIABLES DE MATERIALES

        var cliente= paqueteR?.getString("rcliente")

            if (cliente!=null) {binding.tvTitulo.text= "puerta de paños $cliente"}

            else  {binding.tvTitulo.text= "puerta de paños"}

        binding.tvTitulo.setOnClickListener {
            binding.lyCliente.visibility=View.VISIBLE
            binding.clienteEditxt.setText("$cliente")

        binding.btGo.setOnClickListener {
            cliente = binding.clienteEditxt.text.toString()
            binding.tvTitulo.text="puerta de paños $cliente"

            binding.lyCliente.visibility=View.GONE }
        }
    }

    //FUNCIONES DE VISIBILIDAD
    private fun dVisible(): String {
        val alto = binding.etDivi.text.toString().toInt()
        val hoja = if (mocheta() > 1) { "" } else { "c" }.toString()
        val div = "$alto$hoja"

        val drawableResource = when (div) {
            "1" -> R.drawable.ic_pp1
            "1c" -> R.drawable.ic_pps1
            "2" -> R.drawable.ic_pp2
            "2c" -> R.drawable.ic_pps2
            "3" -> R.drawable.ic_pp3
            "3c" -> R.drawable.ic_pps3
            "4" -> R.drawable.ic_pp4
            "4c" -> R.drawable.ic_pps4
            "5" -> R.drawable.ic_pp5
            "5c" -> R.drawable.ic_pps5
            "6" -> R.drawable.ic_pp6
            "6c" -> R.drawable.ic_pps6
            "7" -> R.drawable.ic_pp7
            "7c" -> R.drawable.ic_pps7
            "8" -> R.drawable.ic_pp8
            "8c" -> R.drawable.ic_pps8
            "9" -> R.drawable.ic_pp9
            "9c" -> R.drawable.ic_pps9
            "10" -> R.drawable.ic_pp10
            "10c" -> R.drawable.ic_pps10
            "11" -> R.drawable.ic_pp11
            "11c" -> R.drawable.ic_pps11
            "12" -> R.drawable.ic_pp12
            "12c" -> R.drawable.ic_pps12
            "13" -> R.drawable.ic_fichad13a
            "13c" -> R.drawable.ic_fichad13c
            "14" -> R.drawable.ic_fichad14a
            "14c" -> R.drawable.ic_fichad14c
            "15" -> R.drawable.ic_fichad15a
            "15c" -> R.drawable.ic_fichad15c
            else -> R.drawable.ic_fichad5
        }

        // Cargar el drawable en el ImageView usando ViewBinding
        binding.ivModelo.setImageResource(drawableResource)

        // Retornar el nombre del drawable
        return when (div) {
            "1" -> "ic_fichad1a"
            "1c" -> "ic_fichad1"
            "2" -> "ic_fichad2a"
            "2c" -> "ic_fichad2c"
            "3" -> "ic_fichad3a"
            "3c" -> "ic_fichad3c"
            "4" -> "ic_fichad4a"
            "4c" -> "ic_fichad4c"
            "5" -> "ic_fichad5a"
            "5c" -> "ic_fichad5c"
            "6" -> "ic_fichad6a"
            "6c" -> "ic_fichad6c"
            "7" -> "ic_fichad7a"
            "7c" -> "ic_fichad7c"
            "8" -> "ic_fichad8a"
            "8c" -> "ic_fichad8c"
            "9" -> "ic_fichad9a"
            "9c" -> "ic_fichad9c"
            "10" -> "ic_fichad10a"
            "10c" -> "ic_fichad10c"
            "11" -> "ic_fichad11a"
            "11c" -> "ic_fichad11c"
            "12" -> "ic_fichad12a"
            "12c" -> "ic_fichad12c"
            "13" -> "ic_fichad13a"
            "13c" -> "ic_fichad13c"
            "14" -> "ic_fichad14a"
            "14c" -> "ic_fichad14c"
            "15" -> "ic_fichad15a"
            "15c" -> "ic_fichad15c"
            else -> "ic_fichad5"
        }
    }
    private fun tipos(){
        binding.ivModelo.setOnClickListener {
            indice = (indice + 1) % tipos.size
            binding.tvTitulo.text = tipos[indice]

            val img = when(binding.tvTitulo.text){
                "Mari"->R.drawable.ic_pp2
                "Dora"->R.drawable.pdora
                "Adel"->R.drawable.padelina
                "Mili"->R.drawable.pmili
                "jeny"->R.drawable.pjenny
                "Taly"->R.drawable.pthalia
                "Viky"->R.drawable.pvicky
                "Lina"-> R.drawable.ic_pps12
                "Tere"->R.drawable.ptere
                else -> R.drawable.pjenny
            }
            binding.ivModelo.setImageResource(img)
        }
    }
    // FUNCIONES REDONDEOS
    private fun df1(defo: Float): String {
        val resultado =if ("$defo".endsWith(".0")) {"$defo".replace(".0", "")}
        else { "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }
    //FUNCIONES DE ALUMINIOS
    private fun junkillos(){
        val jun=binding.etJunki.text.toString().toFloat()
        val mJunki=df1(divisiones()-(2*jun)).toFloat()

        binding.tvJunkillo.text = if (mocheta()< 0f){"${df1(mJunki)} = ${(nPfvcal()-1)*2}\n${
            df1(paflon())} = ${(nPfvcal()-1)*2}"}
        else{"${df1(mJunki)} = ${(nPfvcal()-1)*2}" +
                "\n${df1(paflon())} = ${(nPfvcal()-1)*2}\n" +
                "${df1(marcoSuperior())} = 2\n" +
                "${df1(mocheta()-(2*jun))} = 2"}
    }
    private fun marcoSuperior():Float{
        val ancho = binding.etMed1.text.toString().toFloat()
        return ancho-(2*marco)
    }
    private fun tubo(): String {
        val ancho = binding.etMed1.text.toString().toFloat()
        return if (mocheta()> 0f){df1(ancho-(2*marco))
        }else{""}
    }
    private fun paflon(): Float {
        val ancho = binding.etMed1.text.toString().toFloat()
        val holgura = 1f
        return ((ancho - (2 * marco)) - holgura) - (2*bastidor)
    }
    private fun parante():Float{
        val holgura= 1f
        val piso=binding.etPiso.text.toString().toFloat()
        return if(piso==0f){hPuente()-holgura}else{(hPuente()-(holgura/2))-piso}
    }

    private fun vidrioH(): String {
        val jun=binding.etJunki.text.toString().toFloat()
        val holgura = if(jun==0f){0.2f}else{0.4f}
        val anchv = df1((paflon() - holgura)).toFloat()
        val altv = df1(divisiones()-holgura).toFloat()
        return "${df1(anchv)} x ${df1(altv)}"
    }
    private fun vidrioM(): String {
        val jun=binding.etJunki.text.toString().toFloat()
        val holgura = if(jun==0f){0.2f}else{0.4f}
        val uno= df1(marcoSuperior()-holgura).toFloat()
        val dos = df1(mocheta()-holgura).toFloat()
        return "${df1(uno)} x ${df1(dos)}"
    }
    private fun referen():String{
        val ancho=binding.etMed1.text.toString().toFloat()
        val alto=binding.etMed2.text.toString().toFloat()
        val hPuente = df1(hPuente())
        return if(mocheta()> 0f)
        {"anch ${df1(ancho)} x alt ${df1(alto)}\nAlto hoja = $hPuente"}
        else{"anch ${df1(ancho)} x alt ${df1(alto)}"}
    }
    private fun mocheta():Float{
        val alto=binding.etMed2.text.toString().toFloat()
        val tubo =2.5f
        return alto - (hPuente()+marco+tubo)
    }
    private fun hPuente():Float{
        val alto=binding.etMed2.text.toString().toFloat()
        val hHoja=binding.etHoja.text.toString().toFloat()
        val pisog=binding.etPiso.text.toString().toFloat()
        val piso = if (pisog==0f){pisog}else{pisog-0.5f}
        return when {
            hHoja==0f -> when{
                alto>210f && (hoja+piso)< alto-5.3-> {hoja+piso}
                alto<=210f&&alto>hoja->{190f+piso}
                alto<=hoja -> {(alto-marco)}
                (hoja+piso)> alto-5.3-> {(alto-marco)}
                else -> {(alto-marco)+piso}}

            alto<=hHoja || (hHoja+piso)> alto-5.3-> {(alto-marco)}
            else -> {hHoja+piso}
        }
    }
    private fun divisiones(): Float {
        val divi2 = binding.etDivi.text.toString().toFloat()
        val nZoca = binding.etZocalo.text.toString().toInt()
        val mZoca = zocalo()
        val divis = if (nZoca>1){parante()-mZoca}else{parante()}
        val nbas = if(nZoca>1){divi2 * bastidor}else{(divi2 + 1) * bastidor}
        return (divis - nbas) / divi2
    }
    private fun nPfvcal(): Int {
        val divi2 = binding.etDivi.text.toString().toInt()
        return (divi2 + 1)
    }
    private fun nPaflones(): Int {
        val divi2 = binding.etDivi.text.toString().toInt()
        val nBast = binding.etZocalo.text.toString().toInt()
        return if (nBast > 1) {divi2 + nBast } else {divi2 + 1}
    }
    private fun nZocalo(): Int {
        val nBast = binding.etZocalo.text.toString().toInt()
        return if (nBast == 0) {1} else {nBast}
    }
    private fun zocalo(): Float {
        val holgura = if (nZocalo() < 3) 0.009f else 0.009f // Ajusta según tus necesidades
        val mzoca = df1((nZocalo() + holgura) * bastidor).toFloatOrNull() ?: 0f
        return df1(mzoca).toFloatOrNull() ?: 0f
    }

    @SuppressLint("SetTextI18n")
    private fun panos():String{
        val z = zocalo()
        val n = (nPfvcal()-1)//cantidad paflones superiores y cantidad de paños
        val j=df1(divisiones()).toFloat()//tamaño de cada paño
        val b = bastidor
        val g = j+b
        val mPanos=when (n) {
            in 1..17 -> {
                List(n) { index -> (z + j + (index * (j + b))).toString() }
            }
            else -> listOf(((1 * g) + z) - b).map { it.toString() }
        }.toTypedArray()

        var x = ""
        for (i in mPanos){
            x+="${df1(i.toFloat())}\n"
        }
        binding.tvEnsayo.text= "$z\n$x"
        binding.tvEnsayo.text =binding.tvEnsayo.text.substring(0, binding.tvEnsayo.text.length-1)
        return binding.tvEnsayo.text as String
    }

    private fun array() {
        val marcopInput = binding.etMed2.text.toString()
        val marcop = marcopInput.toFloatOrNull() ?: 0f
        val resultado = StringBuilder().apply {
            append("Marco = ${df1(marcop)} = 2\n")
                .append("${df1(marcoSuperior())} = 1\n")
                .append("tubo = ${tubo()} = 1\n")
                .append("paflon = ${df1(paflon())} = 2\n")
                .append("${df1(parante())} = 2\n")
                .append("cuad = ${df1((partesV() * 2) + 3.8f)} = 2\n")
                .append("${df1(divisiones() - (parteH() + 3.8f))} = 2\n")
                .append("Tope = ${df1(marcoSuperior())} = 1\n")
                .append("${df1(hPuente())} = 2\n")
                .append("Vidrio = ${df1((partesV() * 2) + 3.4f)} x ${df1(parteH() - 0.4f)} = 2\n")
                .append("${df1(divisiones() - (parteH() + 3.8f) - 0.4f)} x ${df1(partesV() - 0.4f)} = 4\n")
                .append("${vidrioM()} = 1\n")
                .append("puntosT= ${df1(partesV())}, ${df1((partesV() * 2) + 3.8f)}\n")
                .append("puntosP= ${df1(parteH() + 8.2f)}")
        }.toString()

        binding.tvEnsayo2.text = resultado
    }
    private fun partesV(): Float {
        return (paflon() - (unoMedio * 2)) / 3
    }
    private fun parteH(): Float {
        return (divisiones() - (unoMedio * 5)) / 6
    }

    /* fun esMultiplo(n1: Int, n2: Int): Boolean {
        return if (n1 % n2 == 0) true else false
    }*/
}

