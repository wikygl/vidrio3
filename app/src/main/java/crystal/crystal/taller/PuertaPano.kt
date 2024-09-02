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
import crystal.crystal.databinding.ActivityPuertaPanoBinding
import kotlinx.android.synthetic.main.activity_puerta_pano.*


@Suppress("IMPLICIT_CAST_TO_ANY")
class PuertaPano : AppCompatActivity() {

    private val hoja = 199f
    private val marco = 2.2f
    private val bastidor = 8.25f

    private lateinit var binding: ActivityPuertaPanoBinding

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPuertaPanoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cliente()

        btn_calcularp.setOnClickListener {
            try {
                val marcop = med2_p.text.toString().toFloat()

                //OPCIONES DE VISIBILIDAD

                modelos()
                panos()
                array()

                // MATERIALES

                marcotxt_p.text = "${df1(marcop)} = 2\n${df1(marcoSuperior())} = 1"

                bastitxt_p.text =
                    "${df1(paflon())} = ${nPaflones()}\n${df1(parante())} = 2"

                junkillos()

                angtxt_p.text =
                    "${df1(marcoSuperior())} = 1\n${df1(hPuente())} = 2"

                vidriostxt_p.text =if (mocheta()< 0f){ "${vidrioH()} = ${nPfvcal()-1}"}
                else{ "${vidrioH()} =${nPfvcal()-1}\n" +
                        "${vidrioM()} = 1"}

                referencias_p.text = referen()
                if(tubo()==""){tubolayout.visibility = View.GONE}else{tubolayout.visibility = View.VISIBLE}

                pruebas.text="${tubo()} = 1"

            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }
        btn_archivarp.setOnClickListener {

            if (binding.med1P.text.isNotEmpty()){
                val paquete = Bundle().apply {
                    putString("mat",binding.marcot.toString())
                    putString("res", binding.marcotxtP.text.toString())
                }
                val intent = Intent(this, ListaActivity::class.java)
                intent.putExtras(paquete)
                startActivity(intent)
            }
            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                marcotxt_p.text.toString()))

            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                bastitxt_p.text.toString()))

            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                junkitxt_p.text.toString()))

            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                angtxt_p.text.toString()))

            startActivity(Intent(this, ListaActivity::class.java).putExtra("monto",
                vidriostxt_p.text.toString()))
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
            tvTitulo.text="puerta de paños $cliente"

            binding.lyCliente.visibility=View.GONE }
        }
    }

    //FUNCIONES DE VISIBILIDAD
    private fun modelos(){
        if(nPfvcal()-1==1 && mocheta()>0){p1.visibility =View.VISIBLE}else{p1.visibility =View.GONE}
        if(nPfvcal()-1==1 && mocheta()<=0){p1s.visibility =View.VISIBLE}else{p1s.visibility =View.GONE}
        if(nPfvcal()-1==2 && mocheta()>0){p2.visibility =View.VISIBLE}else{p2.visibility =View.GONE}
        if(nPfvcal()-1==2 && mocheta()<=0){p2s.visibility =View.VISIBLE}else{p2s.visibility =View.GONE}
        if(nPfvcal()-1==3 && mocheta()>0){p3.visibility =View.VISIBLE}else{p3.visibility =View.GONE}
        if(nPfvcal()-1==3 && mocheta()<=0){p3s.visibility =View.VISIBLE}else{p3s.visibility =View.GONE}
        if(nPfvcal()-1==4 && mocheta()>0){p4.visibility =View.VISIBLE}else{p4.visibility=View.GONE}
        if(nPfvcal()-1==4 && mocheta()<=0){p4s.visibility =View.VISIBLE}else{p4s.visibility =View.GONE}
        if(nPfvcal()-1==5 && mocheta()>0){p5.visibility =View.VISIBLE}else{p5.visibility=View.GONE}
        if(nPfvcal()-1==5 && mocheta()<=0){p5s.visibility =View.VISIBLE}else{p5s.visibility =View.GONE}
        if(nPfvcal()-1==6 && mocheta()>0){p6.visibility =View.VISIBLE}else{p6.visibility =View.GONE}
        if(nPfvcal()-1==6 && mocheta()<=0){p6s.visibility =View.VISIBLE}else{p6s.visibility =View.GONE}
        if(nPfvcal()-1==7 && mocheta()>0){p7.visibility =View.VISIBLE}else{p7.visibility=View.GONE}
        if(nPfvcal()-1==7 && mocheta()<=0){p7s.visibility =View.VISIBLE}else{p7s.visibility =View.GONE}
        if(nPfvcal()-1==8 && mocheta()>0){p8.visibility =View.VISIBLE}else{p8.visibility =View.GONE}
        if(nPfvcal()-1==8 && mocheta()<=0){p8s.visibility =View.VISIBLE}else{p8s.visibility =View.GONE}
        if(nPfvcal()-1==9 && mocheta()>0){p9.visibility =View.VISIBLE}else{p9.visibility =View.GONE}
        if(nPfvcal()-1==9 && mocheta()<=0){p9s.visibility =View.VISIBLE}else{p9s.visibility =View.GONE}
        if(nPfvcal()-1==10 && mocheta()>0){p10.visibility =View.VISIBLE}else{p10.visibility =View.GONE}
        if(nPfvcal()-1==10 && mocheta()<=0){p10s.visibility =View.VISIBLE}else{p10s.visibility =View.GONE}
        if(nPfvcal()-1==11 && mocheta()>0){p11.visibility =View.VISIBLE}else{p11.visibility =View.GONE}
        if(nPfvcal()-1==11 && mocheta()<=0){p11s.visibility =View.VISIBLE}else{p11s.visibility =View.GONE}
        if(nPfvcal()-1==12 && mocheta()>0){p12.visibility =View.VISIBLE}else{p12.visibility=View.GONE}
        if(nPfvcal()-1==12 && mocheta()<=0){p12s.visibility =View.VISIBLE}else{p12s.visibility =View.GONE}
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
        val jun=junk_p.text.toString().toFloat()
        val mJunki=df1(divisiones()-(2*jun)).toFloat()

        junkitxt_p.text = if (mocheta()< 0f){"${df1(mJunki)} = ${(nPfvcal()-1)*2}\n${
            df1(paflon())} = ${(nPfvcal()-1)*2}"}
        else{"${df1(mJunki)} = ${(nPfvcal()-1)*2}" +
                "\n${df1(paflon())} = ${(nPfvcal()-1)*2}\n" +
                "${df1(marcoSuperior())} = 2\n" +
                "${df1(mocheta()-(2*jun))} = 2"}
    }

    private fun marcoSuperior():Float{
        val ancho = med1_p.text.toString().toFloat()
        return ancho-(2*marco)
    }
    private fun tubo(): String {
        val ancho = med1_p.text.toString().toFloat()
        return if (mocheta()> 0f){df1(ancho-(2*marco))
        }else{""}
    }
    private fun paflon(): Float {
        val ancho = med1_p.text.toString().toFloat()
        val holgura = 1f
        return ((ancho - (2 * marco)) - holgura) - (2*bastidor)
    }
    private fun parante():Float{
        val holgura= 1f
        val piso=piso_editext.text.toString().toFloat()
        return if(piso==0f){hPuente()-holgura}else{(hPuente()-(holgura/2))-piso}
    }

    private fun vidrioH(): String {
        val jun=junk_p.text.toString().toFloat()
        val holgura = if(jun==0f){0.4f}else{0.6f}
        val anchv = df1((paflon() - holgura)).toFloat()
        val altv = df1(divisiones()-holgura).toFloat()
        return "${df1(anchv)} x ${df1(altv)}"
    }

    private fun vidrioM(): String {
        val jun=junk_p.text.toString().toFloat()
        val holgura = if(jun==0f){0.4f}else{0.6f}
        val uno= df1(marcoSuperior()-holgura).toFloat()
        val dos = df1(mocheta()-holgura).toFloat()
        return "${df1(uno)} x ${df1(dos)}"
    }
    private fun referen():String{
        val ancho=med1_p.text.toString().toFloat()
        val alto=med2_p.text.toString().toFloat()
        val hPuente = df1(hPuente())
        return if(mocheta()> 0f)
        {"anch ${df1(ancho)} x alt ${df1(alto)}\nAlto hoja = $hPuente"}
        else{"anch ${df1(ancho)} x alt ${df1(alto)}"}
    }
    private fun mocheta():Float{
        val alto=med2_p.text.toString().toFloat()
        val tubo =2.5f
        return alto - (hPuente()+marco+tubo)
    }
    private fun hPuente():Float{
        val alto=med2_p.text.toString().toFloat()
        val hHoja=hoja_editxt.text.toString().toFloat()
        val pisog=piso_editext.text.toString().toFloat()
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
        val divi2 = divi_editext.text.toString().toFloat()
        val nZoca = zoca_editext.text.toString().toInt()
        val mZoca = zocalo()
        val divis = if (nZoca>1){parante()-mZoca}else{parante()}
        val nbas = if(nZoca>1){divi2 * bastidor}else{(divi2 + 1) * bastidor}
        return (divis - nbas) / divi2
    }
    private fun nPfvcal(): Int {
        val divi2 = divi_editext.text.toString().toInt()
        return (divi2 + 1)
    }
    private fun nPaflones(): Int {
        val divi2 = divi_editext.text.toString().toInt()
        val nBast = zoca_editext.text.toString().toInt()
        return if (nBast > 1) {divi2 + nBast } else {divi2 + 1}
    }
    private fun nZocalo(): Int {
        val nBast = zoca_editext.text.toString().toInt()
        return if (nBast == 0) {1} else {nBast}
    }
    private fun zocalo(): Float {
        val holgura = if (nZocalo()<3){0f}else{0.009f}
        val mzoca =df1((nZocalo()+holgura) * bastidor).toFloat()
        return df1(mzoca).toFloat()
    }

    @SuppressLint("SetTextI18n")
    private fun panos():String{
        val z = zocalo()
        val n = (nPfvcal()-1)//cantidad paflones superiores y cantidad de paños
        val j=df1(divisiones()).toFloat()//tamaño de cada paño
        val b = bastidor
        val g = j+b
        val mPanos=when(n){
            1-> arrayOf("${z+j}")
            2-> arrayOf("${z+j}","${z+j+j+b}")
            3-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}")
            4-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}")
            5-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}","${z+j+4*(j+b)}")
            6-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}")
            7-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}","${z+j+6*(j+b)}")
            8-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}","${z+j+6*(j+b)}","${z+j+7*(j+b)}")
            9-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}","${z+j+6*(j+b)}","${z+j+7*(j+b)}","${z+j+8*(j+b)}")
            10-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}","${z+j+6*(j+b)}","${z+j+7*(j+b)}","${z+j+8*(j+b)}","${z+j+9*(j+b)}")
            11-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}","${z+j+6*(j+b)}","${z+j+7*(j+b)}","${z+j+8*(j+b)}","${z+j+9*(j+b)}"
                ,"${z+j+10*(j+b)}")
            12-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}","${z+j+6*(j+b)}","${z+j+7*(j+b)}","${z+j+8*(j+b)}","${z+j+9*(j+b)}"
                ,"${z+j+10*(j+b)}","${z+j+11*(j+b)}")
            13-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}","${z+j+6*(j+b)}","${z+j+7*(j+b)}","${z+j+8*(j+b)}","${z+j+9*(j+b)}"
                ,"${z+j+10*(j+b)}","${z+j+11*(j+b)}","${z+j+12*(j+b)}")
            14-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}","${z+j+6*(j+b)}","${z+j+7*(j+b)}","${z+j+8*(j+b)}","${z+j+9*(j+b)}"
                ,"${z+j+10*(j+b)}","${z+j+11*(j+b)}","${z+j+12*(j+b)}","${z+j+13*(j+b)}")
            15-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}","${z+j+6*(j+b)}","${z+j+7*(j+b)}","${z+j+8*(j+b)}","${z+j+9*(j+b)}"
                ,"${z+j+10*(j+b)}","${z+j+11*(j+b)}","${z+j+12*(j+b)}","${z+j+13*(j+b)}", "${z+j+14*(j+b)}")
            16 -> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}","${z+j+6*(j+b)}","${z+j+7*(j+b)}","${z+j+8*(j+b)}","${z+j+9*(j+b)}"
                ,"${z+j+10*(j+b)}","${z+j+11*(j+b)}","${z+j+12*(j+b)}","${z+j+13*(j+b)}", "${z+j+14*(j+b)}",
                "${z+j+15*(j+b)}")
            17-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}","${z+j+6*(j+b)}","${z+j+7*(j+b)}","${z+j+8*(j+b)}","${z+j+9*(j+b)}"
                ,"${z+j+10*(j+b)}","${z+j+11*(j+b)}","${z+j+12*(j+b)}","${z+j+13*(j+b)}", "${z+j+14*(j+b)}",
                "${z+j+15*(j+b)}","${z+j+16*(j+b)}")
            18-> arrayOf("${z+j}","${z+j+j+b}","${z+j+2*(j+b)}","${z+j+3*(j+b)}",
                "${z+j+4*(j+b)}","${z+j+5*(j+b)}","${z+j+6*(j+b)}","${z+j+7*(j+b)}","${z+j+8*(j+b)}","${z+j+9*(j+b)}"
                ,"${z+j+10*(j+b)}","${z+j+11*(j+b)}","${z+j+12*(j+b)}","${z+j+13*(j+b)}", "${z+j+14*(j+b)}",
                "${z+j+15*(j+b)}","${z+j+16*(j+b)}","${z+j+17*(j+b)}")

            else -> arrayOf("${(z+(g*1))-b}")
        }
        var x = ""
        for (i in mPanos){
            x+="${df1(i.toFloat())}\n"
        }
        tvEnsayo.text= "$z\n$x"
        tvEnsayo.text =tvEnsayo.text.substring(0, tvEnsayo.text.length-1)
        return tvEnsayo.text as String
    }
    private fun array(){
        val p = panos()
        val a:List<String> = p.split("\n")

        tvEnsayo2.text= a.last().toString()
    }
    /* fun esMultiplo(n1: Int, n2: Int): Boolean {
        return if (n1 % n2 == 0) true else false
    }*/

}

