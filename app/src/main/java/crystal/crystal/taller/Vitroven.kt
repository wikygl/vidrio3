package crystal.crystal.taller

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.R
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.databinding.ActivityVitrovenBinding
import kotlin.math.round

class Vitroven : AppCompatActivity() {

    private var dire = 0
    private val clip = 9.6f
    private val jArmado = 2.25f
    private var texto =""
    private var diseno:String =""
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()

    private lateinit var binding: ActivityVitrovenBinding

    @SuppressLint("SetTextI18n", "ResourceAsColor", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVitrovenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelos()

        binding.btCalcular.setOnClickListener {
            try {
                vidrios()
                jamba()
                u()
                platina()
                tubo()
                tope()
                binding.tvClip.text="Pares = ${nClips()}"
                binding.txPruebas.text=clips().toString()
                binding.tvPruebas.text=diseno
                binding.tvDiseno.text= "$diseno = 0r"

            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btArchivar.setOnLongClickListener {
            // Llamar a la función para guardar el Map
            MapStorage.guardarMap(this, mapListas)

            // Mostrar un mensaje de confirmación
            Toast.makeText(this, "Map guardado correctamente", Toast.LENGTH_SHORT).show()

            true // Retorna true para indicar que el evento fue manejado
        }
        binding.btArchivar.setOnClickListener {
            archivarMapas()
        }
        }

    private fun modelos(){

        binding.imagen.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.vitrovf)
            texto="vf"
            diseno = "vitrovf"
        }

        binding.btVitrov.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.ic_vitrobasic)
            texto="v"
            diseno = "ic_vitrobasic"
        }
        binding.btVitrovv.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.vitroven2v)
            texto="vv"
            diseno = "vitroven2v"
        }

        binding.btVitrofvf.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.vitroven3v)
            texto="fvf"
            diseno = "vitroven3v"
        }
        binding.btVitrobv.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.vitroven4)
            texto="vb"
            diseno = "vitroven4"
        }
        binding.btVitrobvs.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.vitroven5)
            texto="bvm"
            diseno = "vitroven5"
        }
        binding.ivDiseno.setOnClickListener {
            binding.ivDiseno.visibility = View.GONE
            binding.svModelos.visibility = View.VISIBLE
            if (binding.ivDiseno.rotation %360==90f) {binding.ivDiseno.rotation += -90f}
        }
        binding.ivDiseno.setOnLongClickListener {
            binding.ivDiseno.rotation += 90f
            dire = 90
            true}
           }
// FUNCIONES MATERIALES
    private fun vidrios () {
        //Estas variables es para calcular los vidrios fijos.
        val x= df1(med2()-(altoVitro()+2.5f+uM()+0.3f+1)).toFloat()
        val z=  df1((med2() - (altoVitro() + 6.6f + (2*uM()))) /2).toFloat()
        //esta variable es para calcular el ancho de los vidrios vitroven
        val anchoV= when(texto){
            "v" ->  anchoVitro() - (jArmado*2)
            "vf" -> anchoVitro() - (df1(jArmado+0.7f)).toFloat()
            "vv" -> anchoVitro() - (4)
            "fvf"-> anchoVitro() - 0.6f
            "vb" -> anchoVitro() - (jArmado*2)
            "bvm"-> anchoVitro() - (jArmado*2)
            else -> {anchoVitro() - (jArmado*2)}
        }

        val vidrioR= if (residuo()<=4.5){"${df1(anchoV)} x ${df1((10.1f + residuo()-1.2f))} = 1\n" +
                "${df1(anchoV)} x 10.1 = ${nClips()-1}"} else {
            "${df1(anchoV)} x 10.1 = ${nClips()}\n" +
                    "${df1(anchoV)} x ${df1(residuo()-0.5f)} = 1"
        }
    val vidrioVv= if (residuo()<=4.5){"${df1(anchoV)} x ${df1((10.1f + residuo()-1.2f))} = 2\n" +
            "${df1(anchoV)} x 10.1 = ${(nClips()-1)*2}"} else {
        "${df1(anchoV)} x 10.1 = ${nClips()*2}\n" +
                "${df1(anchoV)} x ${df1(residuo()-0.5f)} = 2"
    }
    val vidrio2= if (residuo()<=4.5){"${df1(anchoV)} x ${df1((10.1f + residuo()-1.2f))} = 1\n" +
            "${df1(anchoV)} x 10.1 = ${nClips()-1}"} else {
        "${df1(anchoV)} x 10.1 = ${nClips()}\n" +
                "${df1(anchoV)} x ${df1(residuo()-0.5f)} = 1"}
    val vidrioVb="${df1(med1()-0.6f)} x ${df1(x)} = 1\n" +
            "${df1(anchoV)} x 10.1 = ${nClips()}"
    val vidrioBvm="${df1(med1()-0.6f)} x ${df1(z)} = 2\n" +
            "${df1(anchoV)} x 10.1 = ${nClips()}"

    val vidrio= when (texto){
        "v" -> vidrioR
        "vf" -> "${df1(anchoVitro()-0.4f)} x ${df1(med2()-1.8f)} = 1\n" +
                vidrio2
        "vv" -> vidrioVv
        "fvf" -> "${df1(anchoV)} x ${df1(med2() - 1.8f)} = 2\n$vidrio2"
        "vb" -> vidrioVb
        "bvm"-> vidrioBvm
            else -> {"${residuo()}"}
        }
    binding.tvVidrio.text = vidrio
    }

    @SuppressLint("SetTextI18n")
    private fun jamba(){
        val holg= df1(altoVitro()+1.2f).toFloat()
        binding.tvJamba.text= when(texto){
            "vv" -> "${df1(altoVitro())} = 4"
            "vb" -> "${df1(holg)} = 2"
            "bvm"-> "${df1(holg)} = 2"
            else -> {"${df1(altoVitro())} = 2"}
        }
    }
    private fun platina(){
        val nP = (((altoVitro()/clip)-0.1f)*clip)+3.6
        val cuadre = if (altoVitro()>= nP ){10} else {0}
        binding.tvPlatina.text= when(texto){
            "vv" -> "${((nClips()-1)*10)+cuadre} = 4"
            else -> {"${((nClips()-1)*10)+cuadre} = 2"}
        }
    }

    @SuppressLint("SetTextI18n")
    private fun u(){
        val alto = med2()
        val ancho = med1()/2
        val altoVb=df1(med2()-(altoVitro()+ 3.7f+(2*uM()))).toFloat()
        val altoBvm=df1((alto-(altoVitro()+(4*uM())+6.2f))/2).toFloat()
        binding.tvU.text=when(texto) {
            "vf" ->"${df1(ancho)} = 2\n${df1(alto - (2*uM()))} = 1"
            "v" -> ""
            "vv" -> ""
            "fvf" -> "${df1(anchoVitro())} =4\n${df1(altoVitro()-(2*uM()))} = 2"
            "vb" -> "${df1(anchoVitro())} = 2\n${df1(altoVb)} = 2"
            "bvm"-> "${df1(anchoVitro())} = 4\n${df1(altoBvm)} = 4"
            else -> {""}
        }
    }

    private fun tope(){
        binding.tvTope.text=when(texto){
            "vf" -> "${df1(anchoVitro()-2)} = 2"
            "v" -> "${df1(med1()-3.9f)} = 2"
            "vv" -> "${df1((med1()-8.8f)/2)} = 4"
            "fvf" -> "${df1(anchoVitro()-0.5f)} = 2"
            "vb" -> "${df1(med1())} = 2"
            "bvm"-> "${df1(med1())} = 2"
            else -> {""}
        }
    }

    private fun tubo(){
        binding.tvTubo.text=when(texto){
            "vf" -> "${df1(med2())} = 1"
            "v" -> ""
            "vv" -> "${df1(med2())} = 1"
            "fvf" -> "${df1(med2())} = 2"
            "vb" -> "${df1(med1())} = 1"
            "bvm"-> "${df1(med1())} = 2"
            else -> {""}
        }
    }

    //FUNCIONES DE CONTROL
    private fun residuo(): Float {
        return altoVitro()- (clip*nClips())
    }
    private fun clips():Int{
        val n= round((altoVitro()/clip)-0.1f)
        return n.toInt()
    }
    private fun nClips():Int{
        val n= (altoVitro()/clip)
        return n.toInt()
    }

    //FUNCIONES DE ARCHIVO
    private fun archivarMapas() {
        ListaCasilla.incrementarContadorVentanas(this)

        // Caso especial para txReferencias
        if (esValido(binding.lyReferencias)) {
            ListaCasilla.procesarReferencias(this,binding.txReferencias, binding.tvReferencias, mapListas) // referencias
        }
        // Usar la clase ListaCasilla para procesar y archivar solo los TextView válidos

        if (esValido(binding.lyJamba)){
            ListaCasilla.procesarArchivar(this,binding.txJamba, binding.tvJamba, mapListas) // jamba
        }

        if (esValido(binding.lyPlatina)){
            ListaCasilla.procesarArchivar(this,binding.txPlatina, binding.tvPlatina, mapListas) // platina
        }

        if (esValido(binding.lyUmarco)) {
            ListaCasilla.procesarArchivar(this,binding.txU, binding.tvU, mapListas) // u
        }

        if (esValido(binding.lyTubo)) {
            ListaCasilla.procesarArchivar(this,binding.txTubo, binding.txTubo, mapListas) // tubo
        }

        if (esValido(binding.lyTope)) {
            ListaCasilla.procesarArchivar(this,binding.txTope, binding.tvTope, mapListas) // tope
        }

        if (esValido(binding.lyVidrio)) {
            ListaCasilla.procesarArchivar(this,binding.txVidrio, binding.tvVidrio, mapListas) // vidrios
        }
        if (esValido(binding.lyClip)){
            ListaCasilla.procesarArchivar(this,binding.txClip,binding.tvClip,mapListas) // clips
        }
        //DATOS DE REFERENCIA
        if (esValido(binding.lyClient)) {
            ListaCasilla.procesarArchivar(this,binding.tvC, binding.txC, mapListas) // cliente
        }
        if (esValido(binding.lyAncho)) {
            ListaCasilla.procesarArchivar(this,binding.tvAncho, binding.txAncho, mapListas) // ancho
        }
        if (esValido(binding.lyAlto)) {
            ListaCasilla.procesarArchivar(this,binding.tvAlto, binding.txAlto, mapListas) // alto
        }
        if (esValido(binding.lyPuente)) {
            ListaCasilla.procesarArchivar(this,binding.tvPuente, binding.txPuente, mapListas) // altura Puente
        }
        if (esValido(binding.lyDivisiones)) {
            ListaCasilla.procesarArchivar(this,binding.tvDivisiones, binding.txDivisiones, mapListas) // divisiones
        }
        if (esValido(binding.lyFijos)) {
            ListaCasilla.procesarArchivar(this,binding.tvFijos, binding.txFijos, mapListas) // nFijos
        }
        if (esValido(binding.lyCorredizas)) {
            ListaCasilla.procesarArchivar(this,binding.tvCorredizas, binding.txCorredizas, mapListas) // nCorredizas
        }
        if (esValido(binding.lyDiseno)) {
            ListaCasilla.procesarArchivar(this,binding.txDiseno, binding.tvDiseno, mapListas) // diseño
        }
        if (esValido(binding.lyGrados)) {
            ListaCasilla.procesarArchivar(this,binding.tvGrados, binding.txGrados, mapListas) // grados
        }
        if(esValido(binding.lyTipo)){
            ListaCasilla.procesarArchivar(this,binding.tvTipo,binding.txTipo,mapListas) // tipo de ventana
        }

        // Aquí puedes hacer algo con `mapListas`, como mostrarlo o guardarlo
        binding.txPruebas.text = mapListas.toString()
        println(mapListas)
    }
    // Función para verificar si un Layout es visible o tiene estado GONE
    private fun esValido(ly: LinearLayout): Boolean {
        return ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
    }

    //FUNCIONES GENERALES
    private fun med1():Float{
        return binding.etAncho.text.toString().toFloat()

    }
    private fun med2():Float{
        return binding.etAlto.text.toString().toFloat()
    }
    private fun uM():Float{
        return binding.etU.text.toString().toFloat()
    }
    private fun altoVitro():Float{
        val alto = med2()
        val altoVb=(((alto/3)*2)/clip).toInt()
        val altoVb2=altoVb*clip
        val altoBvm= (((alto/4)*2)/clip).toInt()
        val altoBvm2=altoBvm*clip
        return when(texto){
            "v" ->  alto
            "vf" -> alto
            "vv" -> alto
            "fvf"-> alto
            "vb" -> altoVb2
            "bvm"-> altoBvm2
            else -> {alto}
        }
    }
    private fun anchoVitro():Float{
         val ancho = med1()
         return when(texto){
             "v" ->  ancho
             "vf" -> ancho/2
             "vv" -> ancho/2
             "fvf"-> ancho/3
             "vb" -> ancho
             "bvm"-> ancho
             else -> {ancho}
         }
     }

    private fun df1(defo: Float): String {
        return if (defo % 1 == 0f) {
            // Si es un número entero, muestra sin decimales
            defo.toInt().toString()
        } else {
            // Si tiene decimales, formatea con un decimal
            "%.1f".format(defo).replace(",", ".")
        }
    }

}






