package crystal.crystal.taller.mamparas
import crystal.crystal.taller.ControladorColaMedidas
import crystal.crystal.taller.MaterialesTexto
import crystal.crystal.taller.ModoMasivoHelper
import crystal.crystal.taller.FichaActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import crystal.crystal.R
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityMamparaPaflonBinding
import kotlin.math.ceil


class MamparaPaflon : AppCompatActivity() {

  private val marcoDefault = 2.5f
  private val pAnch= 8.25f
  private val pAlt=3.8f
  private val hoja = 199f
  private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
  private var primerClickArchivarRealizado = false
  private var metaColorAluminio: String = ""
  private var metaTipoVidrio: String = ""
  private var metaAcabadoSuperficial: String = ""
  private var metaObservaciones: String = ""

  private var i = 0
  private var width = 0
  private var height = 0
  private lateinit var canvas: Canvas

  private lateinit var binding: ActivityMamparaPaflonBinding
  private lateinit var controladorCola: ControladorColaMedidas

  // ==================== NUEVAS VARIABLES PARA SISTEMA DE PROYECTOS ====================R
  private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding= ActivityMamparaPaflonBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // ==================== CONFIGURACIÓN DEL SISTEMA DE PROYECTOS ====================

    // Inicializar el manager de proyectos
    ProyectoManager.inicializarDesdeStorage(this)

    // Configurar callback para cambios de proyecto
    proyectoCallback = ProyectoUIHelper.crearCallbackConActualizacionUI(
      context = this,
      textViewProyecto = binding.tvProyectoActivo,
      activity = this
    )
    ProyectoUIHelper.configurarVisorProyectoActivo(this, binding.tvProyectoActivo)


    // Verificar si hay proyecto activo al inicio
    if (!ProyectoManager.hayProyectoActivo()) {
      DialogosProyecto.mostrarDialogoGestionProyectos(this, proyectoCallback)
    }

    // Procesar proyecto enviado desde MainActivity si existe
    procesarIntentProyecto(intent)

    // ==================== LISTENERS ORIGINALES CON VERIFICACIONES DE PROYECTO ====================

    binding.btCalcular.setOnClickListener {
      controladorCola.onCalcular()
      // Verificar proyecto activo antes de calcular
      if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) {
        return@setOnClickListener
      }

      try {
        val alto = binding.med2.text.toString().toFloat()
        val jun = binding.etJunki.text.toString().toFloat()

        referencias()
        diseno()

        // Diseño simbólico: única fuente de geometría para todos los materiales.
        val g = geomActual() ?: throw IllegalStateException("Sin geometría")

        binding.txMarco.text = "${df1(alto)} = 2\n${df1(anchoUtil())} = 1"

        // Riel superior, uno por tramo (su propio ancho); se agrupan los de igual medida.
        binding.txRiel.text = MaterialesTexto.agrupar(
          g.tramos.joinToString("\n") { "${df1(g.anchoTramo(it))} = 1" }
        )

        // Ang tope: igual al riel pero doble (2 por tramo). El parante de tope solo va donde la
        // corrediza colinda con otra corrediza o el marco (no con un fijo).
        binding.txTope.text = MaterialesTexto.agrupar(buildString {
          g.tramos.forEach { append("${df1(g.anchoTramo(it))} = 2\n") }
          if (g.nTopeParante > 0) append("${df1(altoHoja() - (jun + 0.3f))} = ${g.nTopeParante}")
        })

        // Portafelpa: uno por cada lado de corrediza que colinda con un fijo (= uniones fijo-corrediza).
        binding.txPorta.text = if (g.nUnionesFC > 0) "${df1(altoHoja() - 1.5f)} = ${g.nUnionesFC}" else ""

        paflon(g)
        vidrio(g)
        junkillo(g)

        // datos para archivar
        binding.txAncho.text= "${df1(ancho())} = 0r"
        binding.txAlto.text= "${df1(alto())} = 0r"
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
      if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) {
        return@setOnClickListener
      }
      if (binding.med1.text.toString()!=""){archivarMapas()
        Toast.makeText(this, "Archivado", Toast.LENGTH_SHORT).show()}
      else{
        Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
      }
      binding.med1.setText("")
        binding.med2.setText("")
    }

    binding.btArchivar.setOnClickListener {
      // Candado de suscripción PRIMERO: bloquear antes de avanzar numeración o dar el toast.
      if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeArchivar(),
              "Archivar es una función de pago. Renueva para guardar tus proyectos.")) {
        return@setOnClickListener
      }
      if (binding.med1.text.toString().isEmpty()) {
        Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }

      val anchoET = binding.med1.text.toString()
      val altoET = binding.med2.text.toString()
      val referencias = binding.tvReferencias.text.toString()
      if (!referencias.contains(anchoET) || !referencias.contains(altoET)) {
        Toast.makeText(this, "Las medidas no coinciden con las referencias. Recalcula.", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }

      if (!primerClickArchivarRealizado) {
        DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
          override fun onProyectoSeleccionado(nombreProyecto: String) {
            primerClickArchivarRealizado = true
            binding.txC.text = nombreProyecto
            val mapExistente = MapStorage.cargarProyecto(this@MamparaPaflon, nombreProyecto)
            mapListas.clear()
            if (mapExistente != null) mapListas.putAll(mapExistente)
            ejecutarArchivado()
          }
          override fun onProyectoCreado(nombreProyecto: String) {
            primerClickArchivarRealizado = true
            binding.txC.text = nombreProyecto
            ejecutarArchivado()
          }
          override fun onProyectoEliminado(nombreProyecto: String) {
            ProyectoUIHelper.actualizarVisorProyectoActivo(this@MamparaPaflon, binding.tvProyectoActivo)
          }
        })
      } else {
        if (!ProyectoManager.hayProyectoActivo()) {
          primerClickArchivarRealizado = false
          Toast.makeText(this, "No hay proyecto activo. Selecciona uno.", Toast.LENGTH_SHORT).show()
          DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
            override fun onProyectoSeleccionado(nombreProyecto: String) {
              primerClickArchivarRealizado = true
              binding.txC.text = nombreProyecto
              val mapExistente = MapStorage.cargarProyecto(this@MamparaPaflon, nombreProyecto)
              mapListas.clear()
              if (mapExistente != null) mapListas.putAll(mapExistente)
              ejecutarArchivado()
            }
            override fun onProyectoCreado(nombreProyecto: String) {
              primerClickArchivarRealizado = true
              binding.txC.text = nombreProyecto
              ejecutarArchivado()
            }
            override fun onProyectoEliminado(nombreProyecto: String) {
              ProyectoUIHelper.actualizarVisorProyectoActivo(this@MamparaPaflon, binding.tvProyectoActivo)
            }
          })
          return@setOnClickListener
        }
        binding.txC.text = ProyectoManager.getProyectoActivo() ?: ""
        ejecutarArchivado()
      }
    }

    binding.btArchivar.setOnLongClickListener {
      if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) {
        return@setOnLongClickListener true
      }

      // El guardado ahora usa automáticamente el proyecto activo
      MapStorage.guardarMap(this, mapListas)
      Toast.makeText(this, "Map guardado en proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()

      // Actualizar el visor del proyecto
      ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
      true
    }

    binding.tvDiseno.setOnClickListener {
      binding.lyNMocheta.visibility = View.VISIBLE
      binding.lyAnHoja.visibility = View.VISIBLE

    }
    binding.imgV.setOnClickListener {
      startActivity(Intent(this, FichaActivity::class.java))
    }
    // Igual que en puerta: con click largo sobre el diseño se abre DisenoActivity para verlo
    // en grande (con zoom y paneo), reusando el plano cacheado.
    binding.imgV.setOnLongClickListener {
      val bmp = (binding.imgV.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
      if (bmp == null) {
        Toast.makeText(this, "Calcule primero para ver el diseño", Toast.LENGTH_SHORT).show()
        return@setOnLongClickListener true
      }
      crystal.crystal.taller.puerta.dibujo.DibujoPuerta.guardarPlanoEnCache(this, bmp)
      val intent = android.content.Intent(this, crystal.crystal.Diseno.DisenoActivity::class.java).apply {
        putExtra(crystal.crystal.Diseno.DisenoActivity.EXTRA_PLANO, true)
        putExtra(crystal.crystal.Diseno.DisenoActivity.EXTRA_PLANO_TITULO, "Mampara paflón")
      }
      startActivity(intent)
      true
    }

    // Pre-carga desde presupuesto
    intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.med1.setText(df1(it)) }
    intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.med2.setText(df1(it)) }

    controladorCola = ControladorColaMedidas(
      activity = this,
      claseActual = MamparaPaflon::class.java,
      etAncho = binding.med1,
      etAlto = binding.med2,
      ivDiseno = binding.imgV,
      onToqueSimple = { binding.imgV.performClick() },
      onToqueLargo = { binding.imgV.performLongClick() },
      formato = ::df1
    )
    controladorCola.inicializar()
  }

  // ==================== NUEVO MENÚ DE OPCIONES ====================

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menu?.let { ProyectoUIHelper.agregarOpcionesMenuProyecto(it) }
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val manejado = ProyectoUIHelper.manejarSeleccionMenu(
      context = this,
      itemId = item.itemId,
      callback = proyectoCallback,
      onProyectoCambiado = {
        // Actualizar UI cuando cambie el proyecto
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
      }
    )

    return if (manejado) true else super.onOptionsItemSelected(item)
  }

  // ==================== FUNCIONES PARA RECIBIR PROYECTO DESDE MAINACTIVITY ====================

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    procesarIntentProyecto(intent)
  }

  override fun onResume() {
    super.onResume()
    // Actualizar visor del proyecto al volver a la actividad
    ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
  }

  private fun procesarIntentProyecto(intent: Intent) {
    val nombreProyecto = intent.getStringExtra("proyecto_nombre")
    val crearNuevo = intent.getBooleanExtra("crear_proyecto", false)
    val descripcionProyecto = intent.getStringExtra("proyecto_descripcion") ?: ""

    if (crearNuevo && !nombreProyecto.isNullOrEmpty()) {
      // Crear proyecto nuevo desde MainActivity
      if (MapStorage.crearProyecto(this, nombreProyecto, descripcionProyecto)) {
        ProyectoManager.setProyectoActivo(this, nombreProyecto)
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
        Toast.makeText(this, "Proyecto '$nombreProyecto' creado y activado", Toast.LENGTH_SHORT).show()
      }
    } else if (!nombreProyecto.isNullOrEmpty()) {
      // Activar proyecto existente desde MainActivity
      if (MapStorage.existeProyecto(this, nombreProyecto)) {
        ProyectoManager.setProyectoActivo(this, nombreProyecto)
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
        Toast.makeText(this, "Proyecto '$nombreProyecto' activado", Toast.LENGTH_SHORT).show()
      }
    }
  }

  // ==================== FUNCIONES REDONDEOS (SIN CAMBIOS) ====================
  private fun df1(defo: Float): String {
    // Se redondea a 1 decimal y, si queda entero (".0"), se muestra sin decimales. Se formatea
    // primero para evitar que la imprecisión del Float (p. ej. 48.00001) deje el ".0".
    val s = "%.1f".format(defo).replace(",", ".")
    return if (s.endsWith(".0")) s.dropLast(2) else s
  }
  // FUNCIONES REFERENCIAS

  @SuppressLint("SetTextI18n")
  private fun referencias(){
    val ancho = binding.med1.text.toString().toFloatOrNull() ?: return
    val alto = binding.med2.text.toString().toFloatOrNull() ?: return
    val hHoja = runCatching { altoHoja() }.getOrNull()
    val puente = if (alto > hoja && hHoja != null) df1(hHoja) else "sin puente"
    val divs = runCatching { divisiones() }.getOrNull()
    val fijos = runCatching { nFijos() }.getOrNull()
    val corr = runCatching { nCorredizas() }.getOrNull()
    binding.tvReferencias.text = buildString {
      append("Ancho ${df1(ancho)}   ·   Alto ${df1(alto)}\n")
      append("Altura de puente: $puente\n")
      append("Divisiones ${divs ?: "-"}   →   Fijos ${fijos ?: "-"} · Corredizas ${corr ?: "-"}")
    }
  }
  //FUNCIONES ALUMINIOS

  // ===== Diseño simbólico: única fuente de geometría (compartida con el dibujo) =====
  private fun geomActual(): MamparaModulos? = descriptorActual()?.let { MamparaModulos.desde(it) }

  @SuppressLint("SetTextI18n")
  private fun paflon(g: MamparaModulos) {
    val lines = mutableListOf<String>()
    // Zócalos (travesaños horizontales), todos del ancho del vidrio: el fijo lleva 1 (inferior) y
    // la corrediza 2 (superior e inferior).
    val nZocalos = g.nFijos + 2 * g.nCorredizas
    if (nZocalos > 0) lines += "${df1(g.vidrioAncho)} = $nZocalos"
    // Parantes de fijo (alto de hoja): uno por cada unión fijo-corrediza.
    if (g.nUnionesFC > 0) lines += "${df1(paranteFijo())} = ${g.nUnionesFC}"
    // Parantes de corrediza (alto de hoja - 2.1): dos por corrediza.
    if (g.nCorredizas > 0) lines += "${df1(paranteCorredizo())} = ${2 * g.nCorredizas}"
    // Parante estructural de paflón entre tramos (de piso a techo): largo = alto - marco.
    if (g.nParantesTramo > 0) lines += "${df1(alto() - marco())} = ${g.nParantesTramo}"
    // Puente (mochetas): divisores verticales por tramo (el riel base va en txRiel).
    if (paranteMocheta() > 0f) {
      val nDiv = g.tramos.sumOf { (diviMocheta(g.anchoTramo(it)) - 1).coerceAtLeast(0) }
      if (nDiv > 0) lines += "${df1(paranteMocheta())} = $nDiv"
    }
    binding.txPaflon.text = MaterialesTexto.agrupar(lines.joinToString("\n"))
  }

  @SuppressLint("SetTextI18n")
  private fun junkillo(g: MamparaModulos) {
    val jun = binding.etJunki.text.toString().toFloatOrNull() ?: 0f
    val bast = g.bastidor
    val lines = mutableListOf<String>()
    // Horizontales (ancho del vidrio): 2 por hoja.
    val nHoriz = 2 * (g.nFijos + g.nCorredizas)
    if (nHoriz > 0) lines += "${df1(g.vidrioAncho)} = $nHoriz"
    // Verticales (alto del vidrio menos junquillo): 2 por hoja.
    if (g.nFijos > 0) lines += "${df1((paranteFijo() - bast) - 2 * jun)} = ${2 * g.nFijos}"
    if (g.nCorredizas > 0) lines += "${df1((paranteCorredizo() - 2 * bast) - 2 * jun)} = ${2 * g.nCorredizas}"
    // Mocheta (puente): verticales (alto) y horizontales (ancho) por tramo.
    if (paranteMocheta() > 0f) {
      val nMoTotal = g.tramos.sumOf { diviMocheta(g.anchoTramo(it)).coerceAtLeast(1) }
      lines += "${df1(paranteMocheta() - 2 * jun)} = ${2 * nMoTotal}"
      g.tramos.forEach { tramo ->
        val nMo = diviMocheta(g.anchoTramo(tramo)).coerceAtLeast(1)
        val anchoMoch = (g.anchoTramo(tramo) - pAlt * (nMo - 1)) / nMo
        lines += "${df1(anchoMoch)} = ${2 * nMo}"
      }
    }
    binding.txJunki.text = MaterialesTexto.agrupar(lines.joinToString("\n"))
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
    val anchoUtil=  ancho() - (2*marco())
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
    return (alto()- (altoHoja()+pAlt+marco()))
  }
  private fun anchoMocheta():Float{
    val divis = diviMocheta(ancho())
    val x = pAlt*(divis-1)
    return (anchoUtil()-x)/divis
  }
  private fun altoMocheta(): Float {
    val alto = binding.med2.text.toString().toFloat()
    return alto - (marco() + altoHoja() + pAlt)
  }
  private fun nMocheta() {
  }

  //FUNCIONES VIDRIOS
  @SuppressLint("SetTextI18n")
  private fun vidrio(g: MamparaModulos) {
    val vw = g.vidrioAncho - 0.6f
    val lines = mutableListOf<String>()
    // Vidrios de hoja: mismo ancho para todos; alto distinto fijo/corrediza.
    if (g.nFijos > 0) lines += "${df1(vw)} x ${df1(altoVidrioFijo())} = ${g.nFijos}"
    if (g.nCorredizas > 0) lines += "${df1(vw)} x ${df1(altoVidrioCorredizo())} = ${g.nCorredizas}"
    // Vidrios de mocheta (puente), por tramo.
    if (paranteMocheta() > 0f) {
      val altoMoch = paranteMocheta() - 0.6f
      g.tramos.forEach { tramo ->
        val nMo = diviMocheta(g.anchoTramo(tramo)).coerceAtLeast(1)
        val anchoMoch = (g.anchoTramo(tramo) - pAlt * (nMo - 1)) / nMo - 0.6f
        lines += "${df1(anchoMoch)} x ${df1(altoMoch)} = $nMo"
      }
    }
    binding.txVidrio.text = MaterialesTexto.agrupar(lines.joinToString("\n"))
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

  // ==================== FUNCIÓN ARCHIVAR MODIFICADA PARA SISTEMA DE PROYECTOS ====================


  private fun obtenerPrefijo(): String = "MP"

  private fun ejecutarArchivado() {
    if (ModoMasivoHelper.esModoMasivo(this)) {
      archivarMapas()
      return
    }

    mostrarDialogoMetadatosProduccion {
      archivarMapas()
      binding.med1.setText("")
      binding.med2.setText("")
      controladorCola.ofrecerSiguiente()
    }
  }

  private fun marco(): Float {
    return binding.etMarco.text?.toString()?.replace(",", ".")?.toFloatOrNull()
      ?.takeIf { it > 0f }
      ?: marcoDefault
  }

  private fun mostrarDialogoMetadatosProduccion(onContinuar: () -> Unit) {
    val pad = (16 * resources.displayMetrics.density).toInt()
    val contenedor = LinearLayout(this).apply {
      orientation = LinearLayout.VERTICAL
      setPadding(pad, pad, pad, 0)
    }
    val etColor = EditText(this).apply {
      hint = "Color aluminio (ej: negro)"
      setText(metaColorAluminio)
    }
    val etVidrio = EditText(this).apply {
      hint = "Tipo vidrio (ej: incoloro 6mm)"
      setText(metaTipoVidrio)
    }
    val etAcabadoSup = EditText(this).apply {
      hint = "Acabado superficial (opcional)"
      setText(metaAcabadoSuperficial)
    }
    val etObs = EditText(this).apply {
      hint = "Observaciones (opcional)"
      setText(metaObservaciones)
    }
    contenedor.addView(etColor)
    contenedor.addView(etVidrio)
    contenedor.addView(etAcabadoSup)
    contenedor.addView(etObs)

    AlertDialog.Builder(this)
      .setTitle("Metadatos de producción")
      .setView(contenedor)
      .setPositiveButton("Guardar y archivar") { _, _ ->
        metaColorAluminio = etColor.text?.toString()?.trim().orEmpty()
        metaTipoVidrio = etVidrio.text?.toString()?.trim().orEmpty()
        metaAcabadoSuperficial = etAcabadoSup.text?.toString()?.trim().orEmpty()
        metaObservaciones = etObs.text?.toString()?.trim().orEmpty()
        onContinuar()
      }
      .setNeutralButton("Omitir") { _, _ -> onContinuar() }
      .setNegativeButton("Cancelar", null)
      .show()
  }

  private fun sufijoMetadatosProduccion(): String {
    return "-MAT<alu:${metaColorAluminio.ifBlank { "null" }};" +
      "vid:${metaTipoVidrio.ifBlank { "null" }};" +
      "acabado_sup:${metaAcabadoSuperficial.ifBlank { "null" }};" +
      "obs:${metaObservaciones.ifBlank { "null" }}>"
  }

  private fun escaparCampoArchivo(raw: String): String {
    return raw
      .replace("\n", " / ")
      .replace("\r", " ")
      .replace("-", "_")
      .replace("<", "(")
      .replace(">", ")")
      .trim()
  }

  private fun etiquetaPaquete(prefijo: String, numero: Int): String {
    val cliente = binding.txC.text?.toString()?.trim()
      ?.ifBlank { ProyectoManager.getProyectoActivo().orEmpty() }
      ?.ifBlank { "sin cliente" }
      ?: "sin cliente"
    return "$prefijo$numero, $cliente"
  }

  private fun disenoSimbolicoV2(numeroProducto: Int): String {
    val cliente = escaparCampoArchivo(
      binding.txC.text?.toString()?.trim()
        ?.ifBlank { ProyectoManager.getProyectoActivo().orEmpty() }
        ?.ifBlank { "sin cliente" }
        ?: "sin cliente"
    )
    return buildString {
      append("C<").append(cliente).append(">")
      append("-M<").append(df1(ancho())).append(",").append(df1(alto())).append(",").append(df1(altoHoja()))
      append(",null,null,").append(intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)).append(">")
      append("-P<M,p,a,c,").append(numeroProducto).append(">")
      append("-G<p,r,m,p>")
      append(sufijoMetadatosProduccion())
    }
  }

  private fun archivarMapas() {
    val proyectoActivo = ProyectoManager.getProyectoActivo()
    if (proyectoActivo != null) {
      val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
      mapListas.clear()
      if (mapExistente != null) mapListas.putAll(mapExistente)
    }

    val prefijo = obtenerPrefijo()
    val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
    var ultimoID = ""

    for (u in 1..cant) {
      val siguienteNumero = ProyectoManager.obtenerSiguienteContadorPorPrefijo(this, prefijo)
      val identificadorPaquete = etiquetaPaquete(prefijo, siguienteNumero)
      ultimoID = identificadorPaquete

      if (esValido(binding.lyMarco)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvMarco, binding.txMarco, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyPaflon)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvPaflon, binding.txPaflon, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyRiel)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvRiel, binding.txRiel, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyJunki)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvJunki, binding.txJunki, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyTope)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvTope, binding.txTope, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyPorta)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvPorta, binding.txPorta, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyVidrio)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvVidrio, binding.txVidrio, mapListas, identificadorPaquete)
      }
      if (binding.tvReferencias.text.toString().isNotBlank()) {
        ListaCasilla.procesarReferenciasConPrefijo(this, binding.textView28, binding.tvReferencias, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyClient)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvC, binding.txC, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyAncho)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvAncho, binding.txAncho, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyAlto)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvAlto, binding.txAlto, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyPuente)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvPuente, binding.txPuente, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyDivisiones)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvDivisiones, binding.txDivisiones, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyFijos)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvFijos, binding.txFijos, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyCorredizas)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvCorredizas, binding.txCorredizas, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyDiseno)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvDiseno, binding.txDiseno, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyGrados)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvGrados, binding.txGrados, mapListas, identificadorPaquete)
      }
      if (esValido(binding.lyTipo)) {
        ListaCasilla.procesarArchivarConPrefijo(this, binding.tvTipo, binding.txTipo, mapListas, identificadorPaquete)
      }

      val paqueteV2 = disenoSimbolicoV2(siguienteNumero)
      mapListas.getOrPut("DisenoSimbolicoV2") { mutableListOf() }
        .add(mutableListOf(paqueteV2, "", identificadorPaquete))

      // Diseño de la mampara como descriptor simbólico: el recycler (FichaActivity) regenera el
      // dibujo con MamparaPaflonRender, sin guardar PNG.
      descriptorActual()?.let { dsc ->
        mapListas.getOrPut(MamparaPaflonDescriptor.CLAVE) { mutableListOf() }
          .add(mutableListOf(dsc.serializar(), "", identificadorPaquete))
      }

      ProyectoManager.actualizarContadorPorPrefijo(this, prefijo, siguienteNumero)
    }

    MapStorage.guardarMap(this, mapListas)
    ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
    val msg = if (cant > 1) "Archivadas $cant unidades en proyecto: ${ProyectoManager.getProyectoActivo()}"
              else "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}"
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
  }

  // Función para verificar si un Layout es visible o tiene estado GONE
  private fun esValido(ly: LinearLayout): Boolean {
    return ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
  }

  //FUNCIONES DE DISEÑO (SIN CAMBIOS)
  // El diseño se arma como descriptor simbólico y se regenera con MamparaPaflonRender (igual que
  // puerta/Nova): no se guarda PNG, así el archivado siempre se ve con la última lógica de dibujo.
  private fun diseno() {
    val d = descriptorActual() ?: return
    MamparaPaflonRender.dibujar(this, d)?.let { binding.imgV.setImageBitmap(it) }
  }

  private fun descriptorActual(): MamparaPaflonDescriptor? {
    val anchoVal = binding.med1.text.toString().toFloatOrNull() ?: return null
    val altoVal = binding.med2.text.toString().toFloatOrNull() ?: return null
    return MamparaPaflonDescriptor(
      ancho = anchoVal,
      alto = altoVal,
      altoHoja = runCatching { altoHoja() }.getOrNull() ?: return null,
      divisiones = (runCatching { divisiones() }.getOrNull() ?: 1).coerceAtLeast(1),
      bastidor = binding.etBasti.text.toString().toFloatOrNull() ?: pAnch,
      marco = marco(),
      nMochetas = binding.etNmochetas.text.toString().toIntOrNull() ?: 0
    )
  }

  private fun disenoAnterior() {
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

  //FUNCIONES GENERALES (SIN CAMBIOS)
  private fun altoHoja():Float {
    val hHoja=binding.etHoja.text.toString().toFloat()
    val pisog=0f
    val piso = if (pisog==0f){pisog}else{pisog-0.5f}
    return when {
      hHoja==0f -> when{
        alto()>210f && (hoja+piso)< alto()-5.3-> {hoja+piso}
        alto()<=210f&&alto()>hoja->{190f+piso}
        alto()<=hoja -> {(alto()-marco())}
        (hoja+piso)> alto()-5.3-> {(alto()-marco())}
        else -> {(alto()-marco())+piso}}

      alto()<=hHoja || (hHoja+piso)> alto()-5.3-> {(alto()-marco())}
      else -> {hHoja+piso}
    }
  }
  private fun nParantes():Float{
    val parantes= when (divisiones()){
      1 -> 0
      2 -> 2
      3 -> 2
      4 -> 4
      5 -> 4
      6 -> 5
      7 -> 6
      8 -> 9
      9 -> 8
      10 -> 9
      11 -> 10
      12 -> 14
      13 -> 12
      14 -> 14
      15 -> 14
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
      1 -> 1
      2 -> 1
      3 -> 2
      4 -> 2
      5 -> 3
      6 -> 4
      7 -> 4
      8 -> 4
      9 -> 5
      10 ->6
      11 -> 6
      12 -> 6
      13 ->7
      14 ->8
      15 -> 8
      else -> 0
    }
  }
  private fun nCorredizas():Int {
    return when (divisiones()){
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
      11-> 5
      12-> 6
      13-> 6
      14 -> 6
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
    return ancho()-(2*marco())
  }

  @Deprecated("Deprecated in Java")
  override fun onBackPressed() {
      if (ModoMasivoHelper.esModoMasivo(this)) {
          val perfiles = mapOf(
              "Marco" to ModoMasivoHelper.texto(binding.txMarco),
              "Riel" to ModoMasivoHelper.texto(binding.txRiel),
              "Paflón" to ModoMasivoHelper.texto(binding.txPaflon)
          ).filter { it.value.isNotBlank() }

          val accesorios = mapOf(
              "Junquillo" to ModoMasivoHelper.texto(binding.txJunki),
              "Tope" to ModoMasivoHelper.texto(binding.txTope),
              "Porta" to ModoMasivoHelper.texto(binding.txPorta)
          ).filter { it.value.isNotBlank() }

          ModoMasivoHelper.devolverResultado(
              activity = this,
              calculadora = "Mampara Paflón",
              perfiles = perfiles,
              vidrios = ModoMasivoHelper.texto(binding.txVidrio),
              accesorios = accesorios,
              referencias = ModoMasivoHelper.texto(binding.txTipo)
          )
          return
      }
      @Suppress("DEPRECATION")
      super.onBackPressed()
  }
}
