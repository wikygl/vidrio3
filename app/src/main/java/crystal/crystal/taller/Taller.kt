package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.BaulActivity
import crystal.crystal.Diseno.BarandaActivity
import crystal.crystal.Diseno.DisenoActivity
import crystal.crystal.Diseno.nova.DisenoNovaActivity
import crystal.crystal.Listado
import crystal.crystal.MainActivity
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.databinding.ActivityTallerBinding
import crystal.crystal.optimizadores.NcurvosActivity
import crystal.crystal.optimizadores.corte.CorteActivity
import crystal.crystal.taller.nova.NovaCorrediza
import crystal.crystal.taller.puerta.PuertasActivity

class Taller : AppCompatActivity() {

    private lateinit var binding: ActivityTallerBinding
    private lateinit var enrutador: EnrutadorPresupuesto
    private var itemsEnrutados: MutableList<EnrutadorPresupuesto.ItemEnrutado> = mutableListOf()
    private var indiceActual: Int = 0
    private val resultados: MutableList<ResultadoCalculo> = mutableListOf()
    private val presupuestosPendientes = mutableListOf<Pair<String, List<Listado>>>()

    companion object {
        private const val REQUEST_CALCULADORA = 500
        private const val REQUEST_BAUL = 501

        private val COLORES_ALUMINIO = listOf(
            "aluminio", "plateado", "negro", "bronce", "champán",
            "dorado", "madera", "blanco", "madera tacto"
        )
        private val TIPOS_VIDRIO = listOf(
            "transparente", "incoloro", "bronce", "gris", "celeste", "verde",
            "reflejante bronce", "reflejante gris", "reflejante celeste", "reflejante azul"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTallerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enrutador = EnrutadorPresupuesto(this)

        val paqueteR = intent.extras
        val cliente = paqueteR?.getString("cliente")
        if (cliente != null) { binding.cliente.text = "$cliente" }

        configurarBotones()
        configurarCardPresupuesto()

        @Suppress("UNCHECKED_CAST", "DEPRECATION")
        val listaPresupuesto = paqueteR?.getSerializable("lista_presupuesto") as? ArrayList<Listado>
        if (!listaPresupuesto.isNullOrEmpty()) {
            agregarPresupuesto(cliente ?: "Sin cliente", listaPresupuesto)
        }
    }

    // ==================== CARD FLOTANTE ====================

    private fun configurarCardPresupuesto() {
        binding.btnProcesar.setOnClickListener {
            if (presupuestosPendientes.isEmpty()) return@setOnClickListener
            iniciarProcesamientoAcumulado()
        }
        binding.btnCerrarCard.setOnClickListener {
            presupuestosPendientes.clear()
            ocultarCard()
        }
        binding.btnAgregarBaul.setOnClickListener {
            val intent = Intent(this, BaulActivity::class.java)
            intent.putExtra("desde_taller", true)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, REQUEST_BAUL)
        }
    }

    private fun agregarPresupuesto(cliente: String, lista: List<Listado>) {
        presupuestosPendientes.add(cliente to lista)
        actualizarCard()
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarCard() {
        if (presupuestosPendientes.isEmpty()) { ocultarCard(); return }
        val totalItems = presupuestosPendientes.sumOf { it.second.size }
        val n = presupuestosPendientes.size
        binding.tvCardTitulo.text = if (n == 1) presupuestosPendientes[0].first else "$n presupuestos"
        binding.tvCardDetalle.text = "$totalItems items pendientes"
        binding.cardPresupuesto.visibility = View.VISIBLE
        binding.cardPresupuesto.alpha = 0f
        binding.cardPresupuesto.animate().alpha(1f).setDuration(300).start()
    }

    private fun ocultarCard() {
        binding.cardPresupuesto.animate().alpha(0f).setDuration(200)
            .withEndAction { binding.cardPresupuesto.visibility = View.GONE }.start()
    }

    // ==================== PROCESAMIENTO ====================

    private fun iniciarProcesamientoAcumulado() {
        val todosClasificados = mutableListOf<EnrutadorPresupuesto.ItemEnrutado>()
        for ((cliente, lista) in presupuestosPendientes) {
            todosClasificados.addAll(enrutador.clasificar(lista, cliente))
        }

        val conDestino = enrutador.obtenerConDestino(todosClasificados)
        val sinDestino = enrutador.obtenerSinDestino(todosClasificados)

        if (sinDestino.isNotEmpty()) {
            enrutador.mostrarDialogoAsignacion(sinDestino, conDestino) { listaFinal ->
                if (listaFinal == null) {
                    // Volver: restaurar el card flotante con los presupuestos
                    actualizarCard()
                    return@mostrarDialogoAsignacion
                }
                if (listaFinal.isEmpty()) {
                    presupuestosPendientes.clear()
                    Toast.makeText(this, "No hay items para procesar", Toast.LENGTH_SHORT).show()
                    return@mostrarDialogoAsignacion
                }
                presupuestosPendientes.clear()
                ocultarCard()
                itemsEnrutados = listaFinal.toMutableList()
                indiceActual = 0
                procesarSiguiente()
            }
        } else {
            presupuestosPendientes.clear()
            ocultarCard()
            itemsEnrutados = conDestino.toMutableList()
            indiceActual = 0
            procesarSiguiente()
        }
    }

    private fun procesarSiguiente() {
        if (indiceActual >= itemsEnrutados.size) {
            Toast.makeText(this, "Completado: ${resultados.size}/${itemsEnrutados.size} calculados", Toast.LENGTH_LONG).show()
            if (resultados.isNotEmpty()) {
                archivarResultadosEnProyecto()
                startActivity(Intent(this, FichaActivity::class.java))
            }
            return
        }

        val item = itemsEnrutados[indiceActual]
        Toast.makeText(this, "Item ${indiceActual + 1}/${itemsEnrutados.size}: ${item.listado.producto}", Toast.LENGTH_SHORT).show()

        // Preguntar color aluminio → tipo vidrio → abrir calculadora
        mostrarDialogoColor(item) {
            mostrarDialogoVidrio(item) {
                val intent = enrutador.crearIntentCalculadora(item)
                @Suppress("DEPRECATION")
                startActivityForResult(intent, REQUEST_CALCULADORA)
            }
        }
    }

    // ==================== DIÁLOGOS COLOR / VIDRIO ====================

    private fun mostrarDialogoColor(item: EnrutadorPresupuesto.ItemEnrutado, onListo: () -> Unit) {
        val opciones = mutableListOf("Escribir otro...")
        opciones.addAll(COLORES_ALUMINIO)

        val dlg = AlertDialog.Builder(this)
            .setTitle("Color aluminio\n${item.listado.producto}")
            .setItems(opciones.toTypedArray()) { _, which ->
                if (which == 0) {
                    val input = EditText(this)
                    input.hint = "Ej: champán mate"
                    AlertDialog.Builder(this)
                        .setTitle("Color aluminio")
                        .setView(input)
                        .setPositiveButton("OK") { _, _ ->
                            item.colorAluminio = input.text.toString().trim().ifEmpty { "aluminio" }
                            onListo()
                        }
                        .setNegativeButton("Cancelar") { _, _ ->
                            mostrarDialogoColor(item, onListo)
                        }
                        .setOnCancelListener { mostrarDialogoColor(item, onListo) }
                        .show()
                } else {
                    item.colorAluminio = opciones[which]
                    onListo()
                }
            }
            .setNegativeButton("Saltar item") { _, _ ->
                indiceActual++
                procesarSiguiente()
            }
            .create()
        dlg.setOnCancelListener {
            indiceActual++
            procesarSiguiente()
        }
        dlg.show()
    }

    private fun mostrarDialogoVidrio(item: EnrutadorPresupuesto.ItemEnrutado, onListo: () -> Unit) {
        val opciones = mutableListOf("Escribir otro...")
        opciones.addAll(TIPOS_VIDRIO)

        val dlg = AlertDialog.Builder(this)
            .setTitle("Tipo de vidrio\n${item.listado.producto}")
            .setItems(opciones.toTypedArray()) { _, which ->
                if (which == 0) {
                    val input = EditText(this)
                    input.hint = "Ej: catedral flora ámbar, 5mm"
                    AlertDialog.Builder(this)
                        .setTitle("Tipo de vidrio")
                        .setView(input)
                        .setPositiveButton("OK") { _, _ ->
                            item.tipoVidrio = input.text.toString().trim().ifEmpty { "incoloro" }
                            onListo()
                        }
                        .setNegativeButton("Cancelar") { _, _ ->
                            mostrarDialogoVidrio(item, onListo)
                        }
                        .setOnCancelListener { mostrarDialogoVidrio(item, onListo) }
                        .show()
                } else {
                    item.tipoVidrio = opciones[which]
                    onListo()
                }
            }
            .setNegativeButton("Volver a color") { _, _ ->
                mostrarDialogoColor(item, onListo)
            }
            .create()
        dlg.setOnCancelListener {
            mostrarDialogoColor(item, onListo)
        }
        dlg.show()
    }

    // ==================== ARCHIVAR EN MAPSTORAGE ====================

    private fun archivarResultadosEnProyecto() {
        if (!ProyectoManager.hayProyectoActivo()) {
            Toast.makeText(this, "No hay proyecto activo, resultados no archivados", Toast.LENGTH_SHORT).show()
            return
        }

        val mapListas = MapStorage.cargarMap(this) ?: mutableMapOf()
        val contadorPorSigla = mutableMapOf<String, Int>()

        // Inicializar contadores con los máximos existentes en el proyecto
        for (r in resultados) {
            val prefijo = prefijoPorCalculadora(r.calculadora)
            if (!contadorPorSigla.containsKey(prefijo)) {
                val maxExistente = ProyectoManager.obtenerSiguienteContadorPorPrefijo(this, prefijo) - 1
                contadorPorSigla[prefijo] = maxExistente
            }
        }

        for (r in resultados) {
            val prefijo = prefijoPorCalculadora(r.calculadora)
            val cant = r.cantidad.coerceAtLeast(1)

            // Repetir por cada unidad para generar IDs distintos (Vni1, Vni2, etc.)
            for (u in 1..cant) {
                val num = (contadorPorSigla[prefijo] ?: 0) + 1
                contadorPorSigla[prefijo] = num
                val paqueteID = "${prefijo}${num}"

                // Perfiles → [valor, cantidad, paqueteID, cliente]
                for ((nombre, texto) in r.perfiles) {
                    if (texto.isBlank()) continue
                    val clave = if (r.colorAluminio.isNotBlank()) "$nombre ${r.colorAluminio}" else nombre
                    val entradas = parsearTextoMedidas(texto, paqueteID, r.cliente)
                    if (entradas.isNotEmpty()) {
                        mapListas.getOrPut(clave) { mutableListOf() }.addAll(entradas)
                    }
                }

                // Vidrios
                if (r.vidrios.isNotBlank()) {
                    val clave = if (r.tipoVidrio.isNotBlank()) "Vidrios ${r.tipoVidrio}" else "Vidrios"
                    val entradas = parsearTextoMedidas(r.vidrios, paqueteID, r.cliente)
                    if (entradas.isNotEmpty()) {
                        mapListas.getOrPut(clave) { mutableListOf() }.addAll(entradas)
                    }
                }

                // Accesorios
                for ((nombre, texto) in r.accesorios) {
                    if (texto.isBlank()) continue
                    val clave = if (r.colorAluminio.isNotBlank()) "$nombre ${r.colorAluminio}" else nombre
                    val entradas = parsearTextoMedidas(texto, paqueteID, r.cliente)
                    if (entradas.isNotEmpty()) {
                        mapListas.getOrPut(clave) { mutableListOf() }.addAll(entradas)
                    }
                }

                // Referencias
                if (r.referencias.isNotBlank()) {
                    val entrada = mutableListOf(r.referencias, "", paqueteID, r.cliente)
                    mapListas.getOrPut("Referencias") { mutableListOf() }.add(entrada)
                }

                // Pedido
                val descripcionPedido = buildString {
                    append(r.calculadora)
                    append(" ${r.ancho} x ${r.alto} = $cant")
                    if (r.colorAluminio.isNotBlank()) append(", aluminio ${r.colorAluminio}")
                    if (r.tipoVidrio.isNotBlank()) append(", vidrio ${r.tipoVidrio}")
                }
                // Solo agregar Pedido una vez (en la primera unidad)
                if (u == 1) {
                    val entradaPedido = mutableListOf(descripcionPedido, "", paqueteID, r.cliente)
                    mapListas.getOrPut("Pedido") { mutableListOf() }.add(entradaPedido)
                }

                // DisenoPaquete
                if (r.disenoPaquete.isNotBlank()) {
                    val entradaDiseno = mutableListOf(r.disenoPaquete, "", paqueteID)
                    mapListas.getOrPut("DisenoPaquete") { mutableListOf() }.add(entradaDiseno)
                }
            }
        }

        MapStorage.guardarMap(this, mapListas)
        Toast.makeText(this, "Archivado en proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()
    }

    private fun parsearTextoMedidas(
        texto: String, paqueteID: String, cliente: String
    ): MutableList<MutableList<String>> {
        val resultado = mutableListOf<MutableList<String>>()
        for (linea in texto.split("\n")) {
            val l = linea.trim()
            if (l.isEmpty()) continue
            val partes = l.split("=")
            if (partes.size == 2) {
                val valor = partes[0].trim()
                val cantidad = partes[1].trim()
                // Descartar si cantidad es vacía/0
                if (cantidad.isBlank()) continue
                val cantidadNum = cantidad.toIntOrNull()
                if (cantidadNum == null || cantidadNum == 0) continue
                // Descartar si valor es 0 (pero no dimensiones tipo "45x30")
                val valorNum = valor.toFloatOrNull()
                if (valorNum != null && valorNum == 0f) continue
                if (valor.isBlank()) continue
                resultado.add(mutableListOf(valor, cantidad, paqueteID, cliente))
            } else {
                resultado.add(mutableListOf(l, "", paqueteID, cliente))
            }
        }
        return resultado
    }

    private fun prefijoPorCalculadora(calculadora: String): String {
        val c = calculadora.lowercase()
        return when {
            // Ventanas
            c.contains("nova ina") -> "Vni"
            c.contains("nova apa") -> "Vna"
            c.contains("nova piv") -> "Vnp"
            "clásica" in c && "econ" in c -> "Vce"
            "clasica" in c && "econ" in c -> "Vce"
            "clásica" in c && "com" in c -> "Vcc"
            "clasica" in c && "com" in c -> "Vcc"
            c.contains("8425") -> "V84"
            c.contains("serie 20") -> "V20"
            c.contains("serie 25") -> "V25"
            c.contains("serie 80") -> "V80"
            "ventana" in c && "pvc" in c -> "Vvc"
            c.contains("ventana") -> "V"
            c.contains("vitroven") -> "Evm"
            // Puertas
            c.contains("puerta ducha") -> "PD"
            "puerta" in c && "mary" in c -> "Pam"
            "puerta" in c && "ade" in c -> "Paa"
            "puerta" in c && "tere" in c -> "Pat"
            "puerta" in c && "pvc" in c -> "Pvc"
            "puerta" in c && "vidrio" in c -> "Pv"
            c.contains("puerta") -> "Pa"
            // Mamparas
            "mampara" in c && "corrediza" in c -> "Mnc"
            "mampara" in c && "pivotante" in c -> "Mnp"
            c.contains("mampara fc") -> "Mce"
            "mampara" in c && "80" in c -> "M80"
            c.contains("mampara paf") -> "Mnp"
            c.contains("mampara vid") -> "Mvc"
            c.contains("mampara") -> "M"
            // Barandas
            "baranda" in c && "pared" in c -> "Bpp"
            "baranda" in c && "rect" in c -> "Bar"
            "baranda" in c && "balaustre" in c -> "Bab"
            "baranda" in c && "inox" in c && "est" in c -> "Bie"
            "baranda" in c && "inox" in c && "vid" in c -> "Biv"
            "baranda" in c && "templado" in c -> "Bvt"
            c.contains("baranda") -> "B"
            // Muro cortina
            "muro" in c && "modular" in c -> "MCm"
            "muro" in c && "stick" in c -> "MCp"
            "muro" in c && "spider" in c -> "MCa"
            c.contains("muro") -> "MC"
            // Otros
            c.contains("pivot") -> "Vnp"
            c.contains("portón") || c.contains("porton") -> "Pos"
            c.contains("reja") -> "Ral"
            c.contains("espejo") -> "Es"
            c.contains("drywall") -> "D"
            c.contains("celosía") || c.contains("celosia") -> "C"
            c.contains("cielo") -> "CR"
            c.contains("techo") -> "T"
            c.contains("melamina") -> "Me"
            else -> "X"
        }
    }

    // ==================== ACTIVITY RESULT ====================

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CALCULADORA -> {
                @Suppress("DEPRECATION")
                val resultado = data?.getSerializableExtra("resultado") as? ResultadoCalculo
                if (resultado != null) resultados.add(resultado)
                indiceActual++
                procesarSiguiente()
            }
            REQUEST_BAUL -> {
                if (resultCode == RESULT_OK && data != null) {
                    val cliente = data.getStringExtra("rcliente") ?: "Sin cliente"
                    @Suppress("UNCHECKED_CAST", "DEPRECATION")
                    val lista = data.getSerializableExtra("lista") as? ArrayList<Listado>
                    if (!lista.isNullOrEmpty()) {
                        agregarPresupuesto(cliente, lista)
                        Toast.makeText(this, "Presupuesto de $cliente agregado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // ==================== BOTONES ====================

    private fun configurarBotones() {
        binding.puerta.setOnClickListener { lanzarCalculadora(PuertasActivity::class.java) }
        binding.micro.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString()) }
            startActivity(Intent(this, MainActivity::class.java).putExtras(paquete))
        }
        binding.btnNovaina.setOnClickListener { lanzarCalculadora(NovaCorrediza::class.java) }
        binding.btnMpaflon.setOnClickListener { lanzarCalculadora(MamparaPaflon::class.java) }
        binding.btnMfc.setOnClickListener { lanzarCalculadora(MamparaFC::class.java) }
        binding.btnV.setOnClickListener { lanzarCalculadora(Vitroven::class.java) }
        binding.btnEstructura.setOnClickListener { lanzarCalculadora(VentanaAl::class.java) }
        binding.btnPivotE.setOnClickListener { lanzarCalculadora(PivotAl::class.java) }
        binding.btnMc.setOnClickListener { lanzarCalculadora(Muro::class.java) }
        binding.btMvidrio.setOnClickListener { lanzarCalculadora(MamparaVidrioActivity::class.java) }
        binding.btnA001.setOnClickListener { lanzarCalculadora(PDuchaActivity::class.java) }
        binding.btCurvo.setOnClickListener { startActivity(Intent(this, NcurvosActivity::class.java)) }
        binding.btOptiLineal.setOnClickListener { startActivity(Intent(this, CorteActivity::class.java)) }
        binding.tbX.setOnClickListener { startActivity(Intent(this, DisenoActivity::class.java)) }
        binding.btUnidades.setOnClickListener { lanzarCalculadora(RejasActivity::class.java) }
        binding.btOpti.setOnClickListener { startActivity(Intent(this, BarandaActivity::class.java)) }
        binding.btFichas.setOnClickListener { startActivity(Intent(this, FichaActivity::class.java)) }
        binding.btMedTecnica.setOnClickListener{startActivity(Intent(this,MedidaActivity::class.java))}
        binding.btDivisionBano.setOnClickListener { lanzarCalculadora(DivisionBanoActivity::class.java) }
    }

    private fun lanzarCalculadora(clase: Class<out AppCompatActivity>) {
        val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString()) }
        startActivity(Intent(this, clase).putExtras(paquete))
    }
}
