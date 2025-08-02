package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import crystal.crystal.FichaActivity
import crystal.crystal.R
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.databinding.ActivityMamparaPaflonBinding
import kotlin.math.ceil


class MamparaPaflon : AppCompatActivity() {

  private val marco= 2.5f
  private val pAnch= 8.25f
  private val pAlt=3.8f
  private val hoja = 199f
  private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()

  private var i = 0
  private var width = 0
  private var height = 0
  private lateinit var canvas: Canvas

  private lateinit var binding: ActivityMamparaPaflonBinding

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding= ActivityMamparaPaflonBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.btCalcular.setOnClickListener {
      try {
        val alto = binding.med2.text.toString().toFloat()
        val jun = binding.etJunki.text.toString().toFloat()

        referencias()
        diseno()


        binding.txMarco.text = "${df1(alto)} = 2\n${df1(anchoUtil())} = 1"

        binding.txRiel.text = "${df1(anchoUtil())} = 1"

        binding.txTope.text = "${df1(anchoUtil())} = 2\n${altoHoja() - (jun+0.3)} = 1"

        binding.txPorta.text = "${altoHoja() - 1.5} = 2"

        paflon()
        vidrio()
        junkillo()

        binding.txCalculo.text = diviMocheta(ancho()).toString()
        binding.btDiseno.text = anchoMocheta().toString()

        // datos para archivar
        binding.txAncho.text= "${df1(ancho())} = 0r"
        binding.txAlto.text= "${alto()} = 0r"
        binding.txPuente.text= "${df1(altoHoja())} = 1r"
        binding.txDivisiones.text= "${df1(divisiones().toFloat())} = ${df1(divisiones().toFloat())}r "
        binding.txFijos.text= "${df1(nFijos().toFloat())} = ${df1(nFijos().toFloat())}r"
        binding.txCorredizas.text= "${df1(nCorredizas().toFloat())} = ${df1(nCorredizas().toFloat())}r"
        //binding.txDiseno.text= "${dVisible()} = 0r"
        //binding.txGrados.text="$grados = 1g"
        binding.txTipo.text = "mampara paflon = 0g"
        //binding.txC.text = "$cliente = 0e"

      }catch (e: Exception) {
        Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()}
    }

    binding.btArchivar.setOnClickListener{
      archivarMapas()
    }
    binding.btArchivar.setOnLongClickListener {
      // Llamar a la función para guardar el Map
      MapStorage.guardarMap(this, mapListas)

      // Mostrar un mensaje de confirmación
      Toast.makeText(this, "Map guardado correctamente", Toast.LENGTH_SHORT).show()

      true // Retorna true para indicar que el evento fue manejado
    }
    binding.tvDiseno.setOnClickListener {
      binding.lyNMocheta.visibility = View.VISIBLE
      binding.lyAnHoja.visibility = View.VISIBLE

    }
    binding.imgV.setOnClickListener {
      startActivity(Intent(this,FichaActivity::class.java))
    }
  }

  // FUNCIONES REDONDEOS
  private fun df1(defo: Float): String {
    return if (defo % 1 == 0f) {
      // Si es un número entero, muestra sin decimales
      defo.toInt().toString()
    } else {
      // Si tiene decimales, formatea con un decimal
      "%.1f".format(defo).replace(",", ".")
    }
  }
 // FUNCIONES REFERENCIAS
  
  @SuppressLint("SetTextI18n")
  private fun referencias(){
   val ancho = binding.med1.text.toString().toFloat()
   val alto = binding.med2.text.toString().toFloat()
    binding.tvReferencias.text= "Ancho ${df1(ancho)}; Alto ${df1(alto)}\n Altura de puente ${df1(altoHoja())}" +
            "\nPartes ${divisiones()}"
  }
  //FUNCIONES ALUMINIOS

  @SuppressLint("SetTextI18n")
  private fun paflon() {
    val z = if (paranteMocheta() > 0) "c" else "s"  // z será "c" o "s"

    // Funciones helper para generar las líneas con menos repetición
    fun lineAnchoUtil() = "${df1(anchoUtil())} = 1"
    fun lineZocaloFijo(n: Int) = "${df1(zocaloFijo())} = $n"
    fun lineZocaloCorrediza(n: Int) = "${df1(zocaloCorrediza())} = $n"
    fun lineParanteFijo(n: Int) = "${df1(paranteFijo())} = $n"
    fun lineParanteCorredizo(n: Int) = "${df1(paranteCorredizo())} = $n"
    fun lineParanteMocheta() = "${df1(paranteMocheta())} = ${diviMocheta(ancho())-1}"

    // Determinamos las líneas base dependiendo del número
    val baseLines = when (divisiones()) {
      1 -> listOf(lineAnchoUtil()) // caso "1s" y "1c"
      2 -> listOf(                  // caso "2s" y "2c"
        lineZocaloFijo(1),
        lineZocaloCorrediza(2),
        lineParanteFijo(1),
        lineParanteCorredizo(2),
      )
      3 -> listOf(                  // solo aparece con "3c" en tu código
        lineZocaloFijo(2),
        lineZocaloCorrediza(2),
        lineParanteCorredizo(2),
        lineParanteFijo(2),
      )
      4 -> listOf(                  // solo aparece con "4c"
        lineZocaloFijo(2),
        "${df1(zocaloCorrediza())} = 4", // si quisieras uniformidad, podrías crear una función análoga lineZocaloCorrediza(4)
        lineParanteFijo(2),
        "${df1(paranteCorredizo())} = 4",
      )
      5 -> listOf(                  // solo aparece con "5c"
        lineZocaloFijo(3),
        "${df1(zocaloCorrediza())} = 4",
        lineParanteFijo(4),
        lineParanteCorredizo(4),
      )
      else -> emptyList()
    }

    // Si es "c" agregamos la línea extra
    val finalLines = if (z == "c"&& diviMocheta(ancho())!=1) baseLines + lineAnchoUtil() + lineParanteMocheta()
    else if (z == "c"&& diviMocheta(ancho())==1) baseLines + lineAnchoUtil()
    else baseLines

    // Unimos todas las líneas en un solo texto
    binding.txPaflon.text = finalLines.joinToString("\n")
  }
  @SuppressLint("SetTextI18n")
  private fun junkillo(){
    val jun = binding.etJunki.text.toString().toFloat()
    val bast = binding.etBasti.text.toString().toFloat()
    if (jun == 0f) {
      binding.txJunki.text = "${df1(zocaloFijo())} = ${nFijos()*2}" +
              "\n${df1(zocaloCorrediza())} = 4" +
              "\n${df1(paranteCorredizo() - (2 * bast))} = 4" +
              "\n${df1(paranteFijo() - bast)} = 4" +
              "\n${df1(paranteMocheta())} = 4" +
              "\n${df1(anchoMocheta())}= 4"
    } else {
      binding.txJunki.text = "${df1(zocaloFijo())} = ${nFijos()*2}" +
              "\n${df1(zocaloCorrediza())} = ${nCorredizas()*2}" +
              "\n${df1((paranteCorredizo() - (2 * bast)) - (2 * jun))} = ${nCorredizas()*2}" +
              "\n${df1((paranteFijo() - bast) - (2 * jun))} = ${nFijos()*2}" +
              "\n${df1(paranteMocheta() - (2 * jun))} = ${diviMocheta(anchoUtil())*2}"+
              "\n${df1(anchoMocheta())}= ${diviMocheta(ancho())*2}"
    }
  }
  private fun zocaloFijo(): Float {
    return when(divisiones()){
      1->anchoUtil()
      2->((anchoUtil()+pAnch)/2)-pAnch
      3->((anchoUtil()+ 2*pAnch)/3 )-pAnch
      4->((anchoUtil()+2*pAnch)/4)-pAnch
      5->((anchoUtil()+4*pAnch)/5)-pAnch

      else -> {anchoUtil()}
    }
  }
  private fun zocaloCorrediza(): Float {
    val anchoUtil=  ancho() - (2*marco)
    return when(divisiones()){
      1->(anchoUtil-2*pAnch)
      2->((anchoUtil+pAnch)/2)-2*pAnch
      3->((anchoUtil+ 2*pAnch)/3 )-2*pAnch
      4->((anchoUtil+2*pAnch)/4)-2*pAnch
      5->((anchoUtil()+4*pAnch)/5)-2*pAnch

      else -> {anchoUtil}
    }
  }
  private fun paranteCorredizo():Float{
    return altoHoja()-2.1f
  }
  private fun paranteFijo(): Float {
    return altoHoja()
  }
  private fun paranteMocheta():Float {
    return (alto()- (altoHoja()+pAlt+marco))
  }
  private fun anchoMocheta():Float{
    val divis = diviMocheta(ancho())
    val x = pAlt*(divis-1)
    return (anchoUtil()-x)/divis
  }
  private fun altoMocheta(): Float {
    val alto = binding.med2.text.toString().toFloat()
    return alto - (marco + altoHoja() + pAlt)
  }
  private fun nMocheta() {
  }

  //FUNCIONES VIDRIOS
  @SuppressLint("SetTextI18n")
  private fun vidrio(){
    val z = if (paranteMocheta() > 0) "c" else "s"
    val control = "${divisiones()}$z"
    binding.txVidrio.text = when (control){
      "1c"->
      "${df1(zocaloFijo() - 0.4f)} x ${df1(altoVidrioFijo())} = ${nFijos()}\n" +
              "${df1(paranteMocheta() - 0.4f)} x " +
              "${df1(anchoMocheta()-0.4f)} = ${diviMocheta(ancho())}"
      "1s"->
        "${df1(zocaloFijo() - 0.4f)} x ${df1(altoVidrioFijo())} = ${nFijos()}\n"
      "2c"->
        "${df1(zocaloFijo() - 0.4f)} x ${df1(altoVidrioFijo())} = ${nFijos()}\n" +
                "${df1(zocaloCorrediza() - 0.4f)} x" +
                " ${df1(altoVidrioCorredizo())} = ${nCorredizas()}\n" +
                "${df1(paranteMocheta() - 0.4f)} x " +
                "${df1(anchoMocheta()-0.4f)} = ${diviMocheta(ancho())}"
      "2s"->
        "${df1(zocaloFijo() - 0.4f)} x ${df1(altoVidrioFijo())} = ${nFijos()}\n" +
                "${df1(zocaloCorrediza() - 0.4f)} x" +
                " ${df1(altoVidrioCorredizo())} = ${nCorredizas()}\n"

      else -> {
        val base = "${df1(zocaloFijo() - 0.4f)} x ${df1(altoVidrioFijo())} = ${nFijos()}\n" +
                "${df1(zocaloCorrediza() - 0.4f)} x ${df1(altoVidrioCorredizo())} = ${nCorredizas()}\n"

        if (paranteMocheta() > 0) {
          base + "${df1(paranteMocheta() - 0.4f)} x " +
                  "${df1(anchoMocheta()-0.4f)} = ${diviMocheta(ancho())}"
        } else {
          base
        }
      }
    }
  }
  private fun altoVidrioFijo(): Float {
    val bastidor= binding.etBasti.text.toString().toFloat()
    val holgura = 0.4f
    return altoHoja()-(bastidor+holgura)
  }
  private fun altoVidrioCorredizo(): Float {
    val bastidor= binding.etBasti.text.toString().toFloat()
    val holgura = 0.4f
    return paranteCorredizo()-((2*bastidor)+holgura)
  }

  //FUNCIONES DE ARCHIVO
  private fun archivarMapas() {
    ListaCasilla.incrementarContadorVentanas()

    // Caso especial para txReferencias
   /* if (esValido(binding.lyReferencias)) {
      ListaCasilla.procesarReferencias(binding.tvReferencias, binding.txReferencias, mapListas) // referencias
    }*/
    // Usar la clase ListaCasilla para procesar y archivar solo los TextView válidos
    if (esValido(binding.lyMarco)) {
        ListaCasilla.procesarArchivar(binding.tvMarco, binding.txMarco, mapListas) // marco
    }
    if (esValido(binding.lyPaflon)) {
      ListaCasilla.procesarArchivar(binding.tvPaflon, binding.txPaflon, mapListas) // paflon
    }

    if (esValido(binding.lyRiel)) {
      ListaCasilla.procesarArchivar(binding.tvRiel, binding.txRiel, mapListas) // riel
    }

    if (esValido(binding.lyTope)) {
      ListaCasilla.procesarArchivar(binding.tvTope, binding.txTope, mapListas) // tope
    }
    if (esValido(binding.lyVidrio)) {
      ListaCasilla.procesarArchivar(binding.tvVidrio, binding.txVidrio, mapListas) // vidrios
    }
    if (esValido(binding.lyClient)) {
      ListaCasilla.procesarArchivar(binding.tvC, binding.txC, mapListas) // cliente
    }
    if (esValido(binding.lyAncho)) {
      ListaCasilla.procesarArchivar(binding.tvAncho, binding.txAncho, mapListas) // ancho
    }
    if (esValido(binding.lyAlto)) {
      ListaCasilla.procesarArchivar(binding.tvAlto, binding.txAlto, mapListas) // alto
    }
    if (esValido(binding.lyPuente)) {
      ListaCasilla.procesarArchivar(binding.tvPuente, binding.txPuente, mapListas) // altura Puente
    }
    if (esValido(binding.lyDivisiones)) {
      ListaCasilla.procesarArchivar(binding.tvDivisiones, binding.txDivisiones, mapListas) // divisiones
    }
    if (esValido(binding.lyFijos)) {
      ListaCasilla.procesarArchivar(binding.tvFijos, binding.txFijos, mapListas) // nFijos
    }
    if (esValido(binding.lyCorredizas)) {
      ListaCasilla.procesarArchivar(binding.tvCorredizas, binding.txCorredizas, mapListas) // nCorredizas
    }
    if (esValido(binding.lyDiseno)) {
      ListaCasilla.procesarArchivar(binding.tvDiseno, binding.txDiseno, mapListas) // diseño
    }
    if (esValido(binding.lyGrados)) {
      ListaCasilla.procesarArchivar(binding.tvGrados, binding.txGrados, mapListas) // grados
    }
    if(esValido(binding.lyTipo)){
      ListaCasilla.procesarArchivar(binding.tvTipo,binding.txTipo,mapListas) // tipo de ventana
    }

    // Aquí puedes hacer algo con `mapListas`, como mostrarlo o guardarlo
    //binding.txPr.text = mapListas.toString()
    println(mapListas)
  }
  // Función para verificar si un Layout es visible o tiene estado GONE
  private fun esValido(ly: LinearLayout): Boolean {
    return ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
  }

  //FUNCIONES DE DISEÑO
  private fun diseno() {
    val count = divisiones()

    val fijo = ContextCompat.getDrawable(this, R.drawable.mpfijo) ?: return
    val fijoUnico = ContextCompat.getDrawable(this, R.drawable.mpfijou) ?: return
    val corredizo = ContextCompat.getDrawable(this, R.drawable.mpcorre) ?: return
    val correFinal = ContextCompat.getDrawable(this, R.drawable.mpfijof) ?: return

    width = fijo.intrinsicWidth
    height = fijo.intrinsicHeight

    var combinedBitmap = Bitmap.createBitmap(width * count, height, Bitmap.Config.ARGB_8888)
    canvas = Canvas(combinedBitmap)

    for (index in 0 until count) {
      i = index
      val isFijo = (index % 2 == 0)

      // Determinamos el drawable a usar. Si la rama llama a reflejo(...), devolvemos null
      val drawableToDraw: Drawable? = when (count) {
        1 -> if (isFijo) fijoUnico else corredizo
        2 -> if (isFijo) fijo else correFinal
        4 -> when (index) {
          0 -> fijo
          1 -> correFinal
          2 -> {reflejo(correFinal)
            null
          }
          3 -> {
            reflejo(fijo)
            null
          }
          else -> corredizo}
      6 -> when (index) {
        0 -> fijo
        1 -> corredizo
        2 -> {reflejo(correFinal)
          null
        }

        else -> {fijo}
      }


        else -> {
          // Si count es impar y distinto de 1, y estamos en el último elemento,
          // en lugar de devolver un drawable, reflejamos directamente 'fijo'.
          if (count % 2 != 0 && count != 1 && index == count - 1) {
            reflejo(fijo)
            null
          } else {
            if (isFijo) fijo else corredizo
          }
        }

      }

      // Si drawableToDraw != null, lo dibujamos normal. Si es null, ya se dibujó en reflejo()
      if (drawableToDraw != null) {
        drawableToDraw.setBounds(index * width, 0, (index + 1) * width, height)
        drawableToDraw.draw(canvas)
      }
    }

    // Verificamos mocheta
    val pm = paranteMocheta()
    if (pm > 0) {
      val pmVal = diviMocheta(ancho())
      val mochetaDrawable = ContextCompat.getDrawable(this, R.drawable.mpmocheta) ?: return

      val compWidth = combinedBitmap.width
      val mh = mochetaDrawable.intrinsicHeight

      val newHeight = height + mh
      val finalBitmap = Bitmap.createBitmap(compWidth, newHeight, Bitmap.Config.ARGB_8888)
      val finalCanvas = Canvas(finalBitmap)

      val eachWidth = compWidth / pmVal
      for (j in 0 until pmVal) {
        val left = j * eachWidth
        val right = (j + 1) * eachWidth
        mochetaDrawable.setBounds(left, 0, right, mh)
        mochetaDrawable.draw(finalCanvas)
      }

      finalCanvas.drawBitmap(combinedBitmap, 0f, mh.toFloat(), null)
      combinedBitmap = finalBitmap
    }

    binding.imgV.setImageBitmap(combinedBitmap)
  }

  private fun reflejo(drawable: Drawable) {
    val left = i * width
    val right = (i + 1) * width
    val centerX = (left + right) / 2f
    val centerY = height / 2f

    canvas.save()
    canvas.scale(-1f, 1f, centerX, centerY)
    drawable.setBounds(left, 0, right, height)
    drawable.draw(canvas)
    canvas.restore()
  }

  //FUNCIONES GENERALES
  private fun altoHoja():Float {
    val hHoja=binding.etHoja.text.toString().toFloat()
    val pisog=0f
    val piso = if (pisog==0f){pisog}else{pisog-0.5f}
    return when {
      hHoja==0f -> when{
        alto()>210f && (hoja+piso)< alto()-5.3-> {hoja+piso}
        alto()<=210f&&alto()>hoja->{190f+piso}
        alto()<=hoja -> {(alto()-marco)}
        (hoja+piso)> alto()-5.3-> {(alto()-marco)}
        else -> {(alto()-marco)+piso}}

      alto()<=hHoja || (hHoja+piso)> alto()-5.3-> {(alto()-marco)}
      else -> {hHoja+piso}
    }
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
    val ancho = binding.med1.text.toString().toFloat()
    val divi = binding.etDivi.text.toString().toInt()
    val anHoja = binding.etAnHoja.text.toString().toFloat()

    require(ancho > 0) { "El ancho debe ser positivo y mayor que cero." }

    return if (divi == 0 && anHoja == 0f) {
      // Si no se han asignado divisiones ni alto de hoja, calculamos con base en 90
      ceil(ancho / 90.0).toInt()
    } else {
      // Si divi no es 0 o anHoja no es 0, retornamos divi tal como está
      divi
    }
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

  private fun diviMocheta(x: Float): Int {
    val n = binding.etNmochetas.text.toString().toInt()
    require(x > 0) { "El valor debe ser positivo y mayor que cero." }
    return if (n==0) {
      ceil(x / 180.0).toInt()
    } else {
      n
    }
  }

  private fun ancho(): Float {
    return binding.med1.text.toString().toFloat()
  }
  private fun alto(): Float {
    return binding.med2.text.toString().toFloat()
  }
  private fun anchoUtil():Float{
    return ancho()-(2*marco)
  }

}