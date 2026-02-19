package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.R
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.Diseno.vitroven.ParametrosVitroven
import crystal.crystal.Diseno.vitroven.RenderVitroven
import crystal.crystal.Diseno.vitroven.VitroSimbolicoParser
import crystal.crystal.Diseno.vitroven.VitroActivity
import crystal.crystal.databinding.ActivityVitrovenBinding
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt

class Vitroven : AppCompatActivity() {

    private data class ModuloMedida(
        val tipo: Char,
        val ancho: Float,
        val alto: Float
    )

    private val clip = 9.6f
    private val jArmado = 2.25f
    private var texto =""
    private var diseno:String =""
    private var disenoSimbolicoManual: String = ""
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private val renderVitroven = RenderVitroven()
    private val lanzarEditorVitro = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == RESULT_OK) {
            val data = res.data ?: return@registerForActivityResult
            val disenoEditado = data.getStringExtra(VitroActivity.RESULT_DISENO_SIMBOLICO).orEmpty()
            val direccionVertical = data.getBooleanExtra(VitroActivity.RESULT_DIRECCION_VERTICAL, esDireccionVertical())
            val clipsEditados = data.getIntExtra(VitroActivity.RESULT_CLIPS, nClips()).coerceAtLeast(0)

            disenoSimbolicoManual = disenoEditado
            binding.etDisenoSimbolico.setText("")
            binding.etDireccion.hint = if (direccionVertical) "Vertical" else "Horizontal"
            actualizarDiseno(med1(), med2(), clipsEditados)
        }
    }

    private lateinit var binding: ActivityVitrovenBinding

    private fun seleccionarModelo(codigo: String, nombreDiseno: String, drawableRes: Int) {
        binding.ivDiseno.visibility = View.VISIBLE
        binding.svModelos.visibility = View.GONE
        binding.ivDiseno.setImageResource(drawableRes)
        texto = codigo
        diseno = nombreDiseno
        disenoSimbolicoManual = ""
        binding.etDisenoSimbolico.setText("")
        binding.tvDiseno.text = "$codigo = 0r"
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVitrovenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelos()

        binding.btCalcular.setOnClickListener {
            try {
                val disenoSimbolico = disenoSimbolicoOperativo()
                val usaSimbolico = VitroSimbolicoParser.parse(disenoSimbolico).modulos.isNotEmpty()
                val clipsRender: Int
                if (usaSimbolico) {
                    calcularMaterialesSimbolicos(disenoSimbolico)
                    clipsRender = clipsPorVitrovenSimbolico(disenoSimbolico)
                    binding.tvDiseno.text = descripcionModulosSimbolicos(disenoSimbolico)
                } else {
                    vidrios()
                    jamba()
                    u()
                    platina()
                    tubo()
                    tope()
                    clipsRender = nClips()
                    binding.tvClip.text="Pares = $clipsRender"
                }
                binding.txPruebas.text = binding.tvClip.text
                    .toString()
                    .substringAfter("=")
                    .trim()
                binding.tvPruebas.text=diseno
                if (!usaSimbolico) {
                    binding.tvDiseno.text= "$diseno = 0r"
                }
                actualizarDiseno(med1(), med2(), clipsRender)

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

        binding.etDireccion.setOnClickListener {
            val actual = binding.etDireccion.hint?.toString()?.lowercase().orEmpty()
            binding.etDireccion.hint = if (actual == "vertical") "Horizontal" else "Vertical"
        }

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAncho.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAlto.setText(df1(it)) }
    }

    fun actualizarDiseno(anchoCm: Float, altoCm: Float, clips: Int) {
        val anchoSeguro = anchoCm.coerceAtLeast(1f)
        val altoSeguro = altoCm.coerceAtLeast(1f)
        val clipsSeguro = clips.coerceAtLeast(0)

        binding.ivDiseno.post {
            val anchoBmp = binding.ivDiseno.width.coerceAtLeast(1)
            val altoBmp = binding.ivDiseno.height.coerceAtLeast(1)
            val parametros = ParametrosVitroven(
                anchoTotalCm = anchoSeguro,
                altoTotalCm = altoSeguro,
                clips = clipsSeguro,
                clasificacion = texto,
                disenoSimbolico = disenoSimbolicoOperativo(),
                direccionVertical = binding.etDireccion.hint?.toString()?.equals("Vertical", ignoreCase = true) == true
            )
            val bmp = renderVitroven.renderizarBitmap(anchoBmp, altoBmp, parametros)
            binding.ivDiseno.setImageBitmap(bmp)
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
        }
    }

    private fun actualizarDisenoDesdeEntradas() {
        val ancho = binding.etAncho.text.toString().toFloatOrNull() ?: return
        val alto = binding.etAlto.text.toString().toFloatOrNull() ?: return
        val clipsActuales = nClips().coerceAtLeast(0)
        actualizarDiseno(ancho, alto, clipsActuales)
    }

    private fun modelos(){
        seleccionarModelo("vf", "vitrovf", R.drawable.vitrovf)

        binding.imagen.setOnClickListener { seleccionarModelo("vf", "vitrovf", R.drawable.vitrovf) }
        binding.btVitrov.setOnClickListener { seleccionarModelo("v", "ic_vitrobasic", R.drawable.ic_vitrobasic) }
        binding.btVitrovv.setOnClickListener { seleccionarModelo("vv", "vitroven2v", R.drawable.vitroven2v) }
        binding.btVitrofvf.setOnClickListener { seleccionarModelo("fvf", "vitroven3v", R.drawable.vitroven3v) }
        binding.btVitrobv.setOnClickListener { seleccionarModelo("vb", "vitroven4", R.drawable.vitroven4) }
        binding.btVitrobvs.setOnClickListener { seleccionarModelo("bvm", "vitroven5", R.drawable.vitroven5) }
        binding.ivDiseno.setOnClickListener {
            binding.ivDiseno.visibility = View.GONE
            binding.svModelos.visibility = View.VISIBLE
            if (binding.ivDiseno.rotation %360==90f) {binding.ivDiseno.rotation += -90f}
        }
        binding.ivDiseno.setOnLongClickListener {
            val anchoActual = binding.etAncho.text?.toString()?.toFloatOrNull() ?: return@setOnLongClickListener true
            val altoActual = binding.etAlto.text?.toString()?.toFloatOrNull() ?: return@setOnLongClickListener true
            val intent = Intent(this, VitroActivity::class.java).apply {
                putExtra(VitroActivity.EXTRA_ANCHO, anchoActual)
                putExtra(VitroActivity.EXTRA_ALTO, altoActual)
                putExtra(VitroActivity.EXTRA_CLIPS, clipsTotalesActuales())
                putExtra(VitroActivity.EXTRA_CLASIFICACION, texto)
                putExtra(VitroActivity.EXTRA_DISENO_SIMBOLICO, disenoSimbolicoOperativo())
                putExtra(VitroActivity.EXTRA_DIRECCION_VERTICAL, esDireccionVertical())
            }
            lanzarEditorVitro.launch(intent)
            true
        }
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
        "vf" -> "${df1(anchoFijoVf()-0.4f)} x ${df1(med2()-1.8f)} = 1\n" +
                vidrio2
        "vv" -> vidrioVv
        "fvf" -> "${df1(anchoFijoFvf()-0.6f)} x ${df1(med2() - 1.8f)} = 2\n$vidrio2"
        "vb" -> vidrioVb
        "bvm"-> vidrioBvm
            else -> {"${residuo()}"}
        }
    binding.tvVidrio.text = vidrio
    }

    @SuppressLint("SetTextI18n")
    private fun jamba(){
        val largoJambaBase = largoJambaSegunDireccion(anchoVitro(), altoVitro())
        val holg= df1(largoJambaBase+1.2f).toFloat()
        binding.tvJamba.text= when(texto){
            "vv" -> "${df1(largoJambaBase)} = 4"
            "vb" -> "${df1(holg)} = 2"
            "bvm"-> "${df1(holg)} = 2"
            else -> {"${df1(largoJambaBase)} = 2"}
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
        val ancho = anchoFijoVf()
        val altoVb=df1(med2()-(altoVitro()+ 3.7f+(2*uM()))).toFloat()
        val altoBvm=df1((alto-(altoVitro()+(4*uM())+6.2f))/2).toFloat()
        binding.tvU.text=when(texto) {
            "vf" ->"${df1(ancho)} = 2\n${df1(alto - (2*uM()))} = 1"
            "v" -> ""
            "vv" -> ""
            "fvf" -> "${df1(anchoFijoFvf())} =4\n${df1(altoVitro()-(2*uM()))} = 2"
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
    private fun esDireccionVertical(): Boolean {
        return binding.etDireccion.hint?.toString()?.equals("Vertical", ignoreCase = true) == true
    }

    private fun baseMedidaClips(): Float {
        return medidaBaseClipsPorParante(anchoVitro(), altoVitro())
    }

    private fun residuo(): Float {
        return baseMedidaClips() - (clip * nClips())
    }
    private fun clips():Int{
        val n= round((baseMedidaClips()/clip)-0.1f)
        return n.toInt()
    }
    private fun nClips():Int{
        return clipsEnteros(baseMedidaClips())
    }

    private fun clipsEnteros(base: Float): Int {
        if (base <= 0f) return 0
        val cociente = (base / clip) + 0.0001f
        return floor(cociente.toDouble()).toInt().coerceAtLeast(0)
    }

    private fun obtenerDisenoSimbolico(): String {
        return disenoSimbolicoManual.trim()
    }

    private fun disenoSimbolicoOperativo(): String {
        val actual = obtenerDisenoSimbolico()
        val actualEsEspecifico = actual.contains("<") ||
                actual.contains("vitroven[", ignoreCase = true) ||
                actual.contains("vitrovén[", ignoreCase = true)
        if (actualEsEspecifico && VitroSimbolicoParser.parse(actual).modulos.isNotEmpty()) {
            return actual
        }
        if (binding.etAncho.text.isNullOrBlank() || binding.etAlto.text.isNullOrBlank()) {
            return actual
        }
        return when (texto.lowercase()) {
            "v" -> "{vitroven[${df1(med1())},${df1(med2())}:v<${df1(anchoVitro())}>]}"
            "vv" -> "{vitroven[${df1(med1())},${df1(med2())}:v<${df1(anchoVitro())}>v<${df1(anchoVitro())}>]}"
            "vf" -> "{vitroven[${df1(med1())},${df1(med2())}:v<${df1(anchoVitro())}>f<${df1(anchoFijoVf())}>]}"
            "fvf" -> "{vitroven[${df1(med1())},${df1(med2())}:f<${df1(anchoFijoFvf())}>v<${df1(anchoVitro())}>f<${df1(anchoFijoFvf())}>]}"
            "vb" -> {
                val altoV = altoVitro()
                val altoF = (med2() - altoV).coerceAtLeast(0.1f)
                "{vitroven[${df1(med1())},${df1(med2())}:V<${df1(altoV)}>F<${df1(altoF)}>]}"
            }
            "bvm" -> {
                val altoV = altoVitro()
                val altoF = ((med2() - altoV) / 2f).coerceAtLeast(0.1f)
                "{vitroven[${df1(med1())},${df1(med2())}:F<${df1(altoF)}>V<${df1(altoV)}>F<${df1(altoF)}>]}"
            }
            else -> actual
        }
    }

    private fun clipsTotalesActuales(): Int {
        val simbolicoCrudo = disenoSimbolicoOperativo()
        if (simbolicoCrudo.isBlank()) return nClips()
        val parse = VitroSimbolicoParser.parse(simbolicoCrudo)
        val cantidadV = parse.modulos.count { it.tipo.lowercaseChar() == 'v' }
        if (cantidadV <= 0) return 0
        return clipsPorVitrovenSimbolico(simbolicoCrudo) * cantidadV
    }

    private fun clipsPorVitrovenSimbolico(disenoCrudo: String): Int {
        val modulos = resolverModulosSimbolicos(disenoCrudo)
        val moduloV = modulos.firstOrNull { it.tipo == 'v' } ?: return 0
        val baseClips = medidaBaseClipsPorParante(moduloV.ancho, moduloV.alto)
        return clipsEnteros(baseClips)
    }

    private fun resolverModulosSimbolicos(disenoCrudo: String): List<ModuloMedida> {
        val parse = VitroSimbolicoParser.parse(disenoCrudo)
        val simbolico = parse.modulos.map { it.tipo.lowercaseChar() }
        val medidas = parse.modulos.map { it.medida }
        if (simbolico.isEmpty()) return emptyList()

        val segmentadoVertical = parse.segmentadoVertical
        val modulos = simbolico.size.toFloat().coerceAtLeast(1f)
        val anchoModuloBase = if (segmentadoVertical) med1() else med1() / modulos
        val altoModuloBase = if (segmentadoVertical) med2() / modulos else med2()
        val totalAncho = med1()
        val cantidadV = simbolico.count { it == 'v' }
        val cantidadF = simbolico.count { it == 'f' }
        val hayFijoAlCostado = simbolico.indices.any { i ->
            simbolico[i] == 'v' &&
                    ((i > 0 && simbolico[i - 1] == 'f') || (i < simbolico.lastIndex && simbolico[i + 1] == 'f'))
        }

        var anchoModuloV = anchoModuloBase
        var anchoModuloF = anchoModuloBase
        if (!segmentadoVertical && esDireccionVertical() && cantidadV > 0 && cantidadF > 0 && hayFijoAlCostado) {
            val anchoVForzado = multiploCercanoClip(anchoModuloBase)
            val maximoVPermitido = ((totalAncho - (cantidadF * 0.1f)) / cantidadV.toFloat()).coerceAtLeast(0.1f)
            anchoModuloV = min(anchoVForzado, maximoVPermitido)
            anchoModuloF = ((totalAncho - (anchoModuloV * cantidadV)) / cantidadF.toFloat()).coerceAtLeast(0.1f)
        }

        return simbolico.mapIndexed { index, tipo ->
            val medidaExplicita = medidas.getOrNull(index)?.coerceAtLeast(0.1f)
            val anchoCalculado = if (tipo == 'v') anchoModuloV else anchoModuloF
            if (segmentadoVertical) {
                ModuloMedida(
                    tipo = tipo,
                    ancho = anchoModuloBase,
                    alto = medidaExplicita ?: altoModuloBase
                )
            } else {
                ModuloMedida(
                    tipo = tipo,
                    ancho = medidaExplicita ?: anchoCalculado,
                    alto = altoModuloBase
                )
            }
        }
    }

    private fun descripcionModulosSimbolicos(disenoCrudo: String): String {
        val modulos = resolverModulosSimbolicos(disenoCrudo)
        if (modulos.isEmpty()) return "$diseno = 0r"
        val parse = VitroSimbolicoParser.parse(disenoCrudo)
        val vertical = parse.segmentadoVertical
        val detalle = modulos.joinToString("") { modulo ->
            val tipo = if (vertical) modulo.tipo.uppercaseChar() else modulo.tipo.lowercaseChar()
            val medida = if (vertical) modulo.alto else modulo.ancho
            "$tipo<${df1(medida)}>"
        }
        return "{vitroven[${df1(med1())},${df1(med2())}:$detalle]}"
    }

    private fun largoJambaSegunDireccion(anchoRef: Float, altoRef: Float): Float {
        return if (esDireccionVertical()) anchoRef else altoRef
    }

    private fun medidaBaseClipsPorParante(anchoRef: Float, altoRef: Float): Float {
        return largoJambaSegunDireccion(anchoRef, altoRef)
    }

    private fun multiploCercanoClip(valor: Float): Float {
        if (valor <= 0f) return 0f
        val factor = (valor / clip).roundToInt().coerceAtLeast(1)
        return factor * clip
    }

    private fun anchoVitroVfForzado(): Float {
        val mitad = med1() / 2f
        val objetivo = multiploCercanoClip(mitad)
        // Evita que el fijo quede negativo en medidas pequeñas.
        return objetivo.coerceAtMost((med1() - 0.1f).coerceAtLeast(0.1f))
    }

    private fun anchoVitroFvfForzado(): Float {
        val tercio = med1() / 3f
        val objetivo = multiploCercanoClip(tercio)
        // Deja espacio para dos fijos (al menos 0.1 cada uno).
        return objetivo.coerceAtMost((med1() - 0.2f).coerceAtLeast(0.1f))
    }

    private fun anchoFijoVf(): Float {
        return if (texto == "vf" && esDireccionVertical()) {
            (med1() - anchoVitroVfForzado()).coerceAtLeast(0.1f)
        } else {
            med1() / 2f
        }
    }

    private fun anchoFijoFvf(): Float {
        return if (texto == "fvf" && esDireccionVertical()) {
            ((med1() - anchoVitroFvfForzado()) / 2f).coerceAtLeast(0.1f)
        } else {
            med1() / 3f
        }
    }

    private fun calcularMaterialesSimbolicos(disenoCrudo: String): Int {
        val simbolico = disenoCrudo.lowercase()
        if (simbolico.isBlank()) return 0

        val modulos = resolverModulosSimbolicos(disenoCrudo)
        val u = uM()

        val jamba = linkedMapOf<String, Int>()
        val uMarco = linkedMapOf<String, Int>()
        val platina = linkedMapOf<String, Int>()
        val tope = linkedMapOf<String, Int>()
        val tubo = linkedMapOf<String, Int>()
        val vidrio = linkedMapOf<String, Int>()

        var totalPares = 0

        fun sumarLinea(acumulado: MutableMap<String, Int>, largo: Float, cantidad: Int) {
            if (cantidad <= 0) return
            if (largo <= 0f) return
            val clave = df1(largo)
            acumulado[clave] = (acumulado[clave] ?: 0) + cantidad
        }

        fun sumarVidrio(ancho: Float, alto: Float, cantidad: Int) {
            if (cantidad <= 0) return
            if (ancho <= 0f || alto <= 0f) return
            val clave = "${df1(ancho)} x ${df1(alto)}"
            vidrio[clave] = (vidrio[clave] ?: 0) + cantidad
        }

        fun nClipsModulo(base: Float): Int = clipsEnteros(base)
        fun residuoModulo(base: Float, n: Int): Float = base - (clip * n)

        for (modulo in modulos) {
            if (modulo.tipo == 'v') {
                val anchoModulo = modulo.ancho
                val altoVitroModulo = modulo.alto
                val baseClipsModulo = medidaBaseClipsPorParante(anchoModulo, altoVitroModulo)
                val n = nClipsModulo(baseClipsModulo)
                val residuo = residuoModulo(baseClipsModulo, n)
                val anchoVidrioV = anchoModulo - (jArmado * 2f)
                val largoJambaV = largoJambaSegunDireccion(anchoModulo, altoVitroModulo)
                totalPares += n

                sumarLinea(jamba, largoJambaV, 2)

                if (n > 0) {
                    if (residuo <= 4.5f) {
                        sumarVidrio(anchoVidrioV, 10.1f + residuo - 1.2f, 1)
                        sumarVidrio(anchoVidrioV, 10.1f, n - 1)
                    } else {
                        sumarVidrio(anchoVidrioV, 10.1f, n)
                        sumarVidrio(anchoVidrioV, residuo - 0.5f, 1)
                    }
                }

                val nP = (((baseClipsModulo / clip) - 0.1f) * clip) + 3.6f
                val cuadre = if (baseClipsModulo >= nP) 10f else 0f
                val largoPlatina = (((n - 1) * 10f) + cuadre).coerceAtLeast(0f)
                sumarLinea(platina, largoPlatina, 2)
                sumarLinea(tope, anchoModulo - 3.9f, 2)
            } else if (modulo.tipo == 'f') {
                val anchoModulo = modulo.ancho
                val altoModulo = modulo.alto
                sumarLinea(uMarco, anchoModulo, 2)
                sumarLinea(uMarco, altoModulo - (2f * u), 1)
                sumarLinea(tubo, altoModulo, 1)
                sumarVidrio(anchoModulo - 0.6f, altoModulo - 1.8f, 1)
            }
        }

        binding.tvJamba.text = jamba.entries.joinToString("\n") { "${it.key} = ${it.value}" }
        binding.tvU.text = uMarco.entries.joinToString("\n") { "${it.key} = ${it.value}" }
        binding.tvPlatina.text = platina.entries.joinToString("\n") { "${it.key} = ${it.value}" }
        binding.tvTope.text = tope.entries.joinToString("\n") { "${it.key} = ${it.value}" }
        binding.tvTubo.text = tubo.entries.joinToString("\n") { "${it.key} = ${it.value}" }
        binding.tvVidrio.text = vidrio.entries.joinToString("\n") { "${it.key} = ${it.value}" }
        binding.tvClip.text = "Pares = $totalPares"
        return totalPares
    }

    //FUNCIONES DE ARCHIVO
    private fun archivarMapas() {
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)

        for (u in 1..cant) {
            ListaCasilla.incrementarContadorVentanas(this)

            if (esValido(binding.lyReferencias)) {
                ListaCasilla.procesarReferencias(this, binding.txReferencias, binding.tvReferencias, mapListas)
            }
            if (esValido(binding.lyJamba)) {
                ListaCasilla.procesarArchivar(this, binding.txJamba, binding.tvJamba, mapListas)
            }
            if (esValido(binding.lyPlatina)) {
                ListaCasilla.procesarArchivar(this, binding.txPlatina, binding.tvPlatina, mapListas)
            }
            if (esValido(binding.lyUmarco)) {
                ListaCasilla.procesarArchivar(this, binding.txU, binding.tvU, mapListas)
            }
            if (esValido(binding.lyTubo)) {
                ListaCasilla.procesarArchivar(this, binding.txTubo, binding.txTubo, mapListas)
            }
            if (esValido(binding.lyTope)) {
                ListaCasilla.procesarArchivar(this, binding.txTope, binding.tvTope, mapListas)
            }
            if (esValido(binding.lyVidrio)) {
                ListaCasilla.procesarArchivar(this, binding.txVidrio, binding.tvVidrio, mapListas)
            }
            if (esValido(binding.lyClip)) {
                ListaCasilla.procesarArchivar(this, binding.txClip, binding.tvClip, mapListas)
            }
            if (esValido(binding.lyClient)) {
                ListaCasilla.procesarArchivar(this, binding.tvC, binding.txC, mapListas)
            }
            if (esValido(binding.lyAncho)) {
                ListaCasilla.procesarArchivar(this, binding.tvAncho, binding.txAncho, mapListas)
            }
            if (esValido(binding.lyAlto)) {
                ListaCasilla.procesarArchivar(this, binding.tvAlto, binding.txAlto, mapListas)
            }
            if (esValido(binding.lyPuente)) {
                ListaCasilla.procesarArchivar(this, binding.tvPuente, binding.txPuente, mapListas)
            }
            if (esValido(binding.lyDivisiones)) {
                ListaCasilla.procesarArchivar(this, binding.tvDivisiones, binding.txDivisiones, mapListas)
            }
            if (esValido(binding.lyFijos)) {
                ListaCasilla.procesarArchivar(this, binding.tvFijos, binding.txFijos, mapListas)
            }
            if (esValido(binding.lyCorredizas)) {
                ListaCasilla.procesarArchivar(this, binding.tvCorredizas, binding.txCorredizas, mapListas)
            }
            if (esValido(binding.lyDiseno)) {
                ListaCasilla.procesarArchivar(this, binding.txDiseno, binding.tvDiseno, mapListas)
            }
            if (esValido(binding.lyGrados)) {
                ListaCasilla.procesarArchivar(this, binding.tvGrados, binding.txGrados, mapListas)
            }
            if (esValido(binding.lyTipo)) {
                ListaCasilla.procesarArchivar(this, binding.tvTipo, binding.txTipo, mapListas)
            }
        }

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
             "vf" -> if (esDireccionVertical()) anchoVitroVfForzado() else ancho/2
             "fvf" -> if (esDireccionVertical()) anchoVitroFvfForzado() else ancho/3
             "vv" -> ancho/2
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            val perfiles = mapOf(
                "Jamba" to ModoMasivoHelper.texto(binding.tvJamba),
                "Tubo" to ModoMasivoHelper.texto(binding.tvTubo),
                "U" to ModoMasivoHelper.texto(binding.tvU)
            ).filter { it.value.isNotBlank() }

            val accesorios = mapOf(
                "Platina" to ModoMasivoHelper.texto(binding.tvPlatina),
                "Tope" to ModoMasivoHelper.texto(binding.tvTope)
            ).filter { it.value.isNotBlank() }

            ModoMasivoHelper.devolverResultado(
                activity = this,
                calculadora = "Vitroven",
                perfiles = perfiles,
                vidrios = ModoMasivoHelper.texto(binding.tvVidrio),
                accesorios = accesorios,
                referencias = ""
            )
            return
        }
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}
