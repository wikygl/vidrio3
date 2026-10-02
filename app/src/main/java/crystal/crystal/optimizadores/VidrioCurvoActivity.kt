package crystal.crystal.optimizadores

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import crystal.crystal.R
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.databinding.ActivityVidrioCurvoBinding
import crystal.crystal.red.ListChatActivity
import crystal.crystal.red.interop.ChatInteropIntents
import kotlin.math.abs
import kotlin.math.asin

/**
 * Vidrios curvos, con el estilo de Corte Planchas (2026-10-01).
 *
 * - **Listado** (izquierda): los vidrios archivados como curvos (N-102) en los proyectos que se
 *   elijan, para mandarlos al curvador o al horno. Como en planchas, tocar una fila la marca o la
 *   desmarca (solo las marcadas se envían); el toque largo la pasa al dibujo.
 * - **Medidas** (derecha): desarrollo (arco), cuerda, flecha y alto. Valida que desarrollo, cuerda
 *   y flecha sean el mismo arco, y delega el dibujo isométrico a [VistaVidrioCurvo].
 * - **Cabecera**: el botón de mensajes envía la lista (chat de Crystal o compartir); el de la
 *   calculadora abre la calculadora flotante, como en los optimizadores.
 */
class VidrioCurvoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVidrioCurvoBinding

    /** Los vidrios del listado y si van en el envío. */
    private val lista = mutableListOf<Pair<ListaVidriosCurvos.VidrioCurvo, Boolean>>()
    private var proyectosElegidos: List<String> = emptyList()
    /** El vidrio de la lista que está en el dibujo y en la vista de planta (para cambiarle la cara). */
    private var seleccionado: ListaVidriosCurvos.VidrioCurvo? = null
    private val adaptador by lazy { Adaptador() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVidrioCurvoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ProyectoManager.inicializarDesdeStorage(this)

        binding.listaCurvos.adapter = adaptador
        binding.listaCurvos.setOnItemClickListener { _, _, pos, _ ->
            lista[pos] = lista[pos].first to !lista[pos].second
            adaptador.notifyDataSetChanged()
            actualizarTitulo()
        }
        binding.listaCurvos.setOnItemLongClickListener { _, _, pos, _ ->
            pasarAlDibujo(lista[pos].first)
            true
        }
        binding.tvListado.setOnClickListener { elegirProyectos() }
        binding.tvProyectos.setOnClickListener { elegirProyectos() }

        binding.btDibujar.setOnClickListener { dibujar() }
        binding.btLimpiar.setOnClickListener { limpiar() }
        binding.vistaCaras.alAlternar = { cambiarCara() }
        binding.btMensajes.setOnClickListener { menuEnviar() }
        binding.btCalculadora.setOnClickListener { crystal.crystal.calculadora.CalculadoraFlotante.alternar(this) }

        // Al entrar se ven los del proyecto activo, si hay uno.
        ProyectoManager.getProyectoActivo()
            ?.takeIf { it in MapStorage.obtenerListaProyectos(this) }
            ?.let { cargar(listOf(it)) }
    }

    // ==================== LISTADO ====================

    /** Qué proyectos entran en el listado; los que ya están vienen marcados (o el activo). */
    private fun elegirProyectos() {
        val proyectos = MapStorage.obtenerListaProyectos(this)
        if (proyectos.isEmpty()) {
            Toast.makeText(this, "No hay proyectos archivados", Toast.LENGTH_SHORT).show()
            return
        }
        val yaElegidos = proyectosElegidos.ifEmpty { listOfNotNull(ProyectoManager.getProyectoActivo()) }
        val marcados = BooleanArray(proyectos.size) { proyectos[it] in yaElegidos }
        AlertDialog.Builder(this)
            .setTitle("Vidrios curvos de…")
            .setMultiChoiceItems(proyectos.toTypedArray(), marcados) { _, i, marcado -> marcados[i] = marcado }
            .setPositiveButton("Cargar") { _, _ ->
                val elegidos = proyectos.filterIndexed { i, _ -> marcados[i] }
                if (elegidos.isEmpty()) {
                    Toast.makeText(this, "Elija al menos un proyecto", Toast.LENGTH_SHORT).show()
                } else {
                    cargar(elegidos)
                    if (lista.isEmpty()) Toast.makeText(
                        this,
                        "No hay vidrios curvos archivados ahí. Se marcan al archivar una Nova curva o la curva de una esquina.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cargar(proyectos: List<String>) {
        proyectosElegidos = proyectos
        val mapas = proyectos.associateWith { MapStorage.cargarProyecto(this, it).orEmpty() }
        lista.clear()
        lista.addAll(ListaVidriosCurvos.de(mapas).map { it to true })
        adaptador.notifyDataSetChanged()
        binding.tvProyectos.text = proyectos.joinToString(", ")
        actualizarTitulo()
    }

    private fun actualizarTitulo() {
        val marcados = lista.filter { it.second }.sumOf { it.first.cantidad }
        binding.tvListado.text = if (lista.isEmpty()) "Vidrios curvos" else "Vidrios curvos: $marcados"
    }

    /** Las filas como en planchas: la marca y el texto en celeste. */
    private inner class Adaptador : BaseAdapter() {
        override fun getCount() = lista.size
        override fun getItem(pos: Int) = lista[pos]
        override fun getItemId(pos: Int) = pos.toLong()
        override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
            val v = convertView ?: LayoutInflater.from(this@VidrioCurvoActivity)
                .inflate(R.layout.item_pieza_corte, parent, false)
            val (vidrio, marcado) = lista[pos]
            v.findViewById<ImageView>(R.id.ivEstado)
                .setImageResource(if (marcado) R.drawable.ic_chckr else R.drawable.ic_chck)
            v.findViewById<TextView>(R.id.tvTexto).apply {
                text = if (proyectosElegidos.size > 1) "${vidrio.proyecto} · ${ListaVidriosCurvos.linea(vidrio)}"
                else ListaVidriosCurvos.linea(vidrio)
                setTextColor(Color.parseColor("#159EC2"))
            }
            return v
        }
    }

    // ==================== ENVIAR AL CURVADOR O AL HORNO ====================

    private fun menuEnviar() {
        PopupMenu(this, binding.btMensajes).apply {
            menu.add(0, 1, 0, "Ir a chat")
            menu.add(0, 2, 1, "Enviar lista por el chat de Crystal")
            menu.add(0, 3, 2, "Compartir lista (WhatsApp, correo…)")
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> { startActivity(Intent(this@VidrioCurvoActivity, ListChatActivity::class.java)); true }
                    2 -> { textoParaEnviar()?.let { enviarPorChat(it) }; true }
                    3 -> { textoParaEnviar()?.let { compartir(it) }; true }
                    else -> false
                }
            }
            show()
        }
    }

    /** El texto de los vidrios marcados; null (y lo avisa) si no hay ninguno. */
    private fun textoParaEnviar(): String? {
        val marcados = lista.filter { it.second }.map { it.first }
        if (marcados.isEmpty()) {
            Toast.makeText(this, "No hay vidrios marcados para enviar", Toast.LENGTH_SHORT).show()
            return null
        }
        val fecha = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        return ListaVidriosCurvos.texto(marcados, fecha)
    }

    private fun enviarPorChat(texto: String) {
        startActivity(Intent(this, ListChatActivity::class.java).apply {
            putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_TEXT, texto)
        })
    }

    private fun compartir(texto: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, texto.lineSequence().first())
            putExtra(Intent.EXTRA_TEXT, texto)
        }
        startActivity(Intent.createChooser(intent, "Enviar al curvador o al horno"))
    }

    // ==================== DIBUJO ====================

    // ==================== CAMBIAR DE CARA ====================

    /**
     * La cara del vidrio elegido al revés, sin rehacer el cálculo (2026-10-01): se toca el nombre de
     * la cara de afuera en la vista de planta y se guarda en el proyecto, en la pieza archivada. La
     * lista, la etiqueta de planchas y la ficha leen de ahí.
     */
    private fun cambiarCara() {
        val v = seleccionado ?: return
        if (v.panza.isBlank()) {
            Toast.makeText(this, "Este vidrio se archivó sin la panza: vuelva a archivarlo desde Nova", Toast.LENGTH_LONG).show()
            return
        }
        val l = crystal.crystal.casilla.ListaCasilla
        val mapa = MapStorage.cargarProyecto(this, v.proyecto)
        val entrada = mapa?.get(v.lista)?.getOrNull(v.indice)
        // Que sea la misma pieza: el proyecto pudo cambiar mientras la pantalla estaba abierta.
        if (mapa == null || entrada == null || !l.esCurva(entrada) ||
            entrada.getOrNull(2).orEmpty().trim().split(',', ' ').first() != v.ventana
        ) {
            Toast.makeText(this, "No se encontró ese vidrio en el proyecto; vuelva a cargar la lista", Toast.LENGTH_LONG).show()
            return
        }
        while (entrada.size <= l.POS_CARA) entrada.add("")
        entrada[l.POS_CARA] = ListaVidriosCurvos.alternarCara(v)
        MapStorage.guardarProyecto(this, v.proyecto, mapa)

        // Se recarga la lista conservando las marcas y el vidrio elegido.
        fun clave(x: ListaVidriosCurvos.VidrioCurvo) = Triple(x.proyecto, x.lista, x.indice)
        val marcas = lista.associate { clave(it.first) to it.second }
        cargar(proyectosElegidos)
        for (i in lista.indices) lista[i] = lista[i].first to (marcas[clave(lista[i].first)] ?: true)
        adaptador.notifyDataSetChanged()
        actualizarTitulo()
        seleccionado = lista.firstOrNull { clave(it.first) == clave(v) }?.first
        binding.vistaCaras.mostrar(seleccionado)
        seleccionado?.let { s ->
            Toast.makeText(this, "Afuera: ${ListaVidriosCurvos.caraAfuera(s)} · ${ListaVidriosCurvos.caraEspecial(s)} " +
                if (ListaVidriosCurvos.esConvexa(s) == true) "convexa" else "cóncava", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pasarAlDibujo(v: ListaVidriosCurvos.VidrioCurvo) {
        seleccionado = v
        binding.vistaCaras.mostrar(v)
        binding.etDesarrollo.setText(fmt(v.desarrollo))
        binding.etCuerda.setText(v.cuerda?.let { fmt(it) }.orEmpty())
        binding.etFlecha.setText(v.flecha?.let { fmt(it) }.orEmpty())
        binding.etLargo.setText(fmt(v.alto))
        dibujar()
    }

    private fun limpiar() {
        listOf(binding.etDesarrollo, binding.etCuerda, binding.etFlecha, binding.etLargo).forEach { it.setText("") }
        seleccionado = null
        binding.vistaCaras.mostrar(null)
        mensaje("Dibujo", error = false)
        binding.vistaCurvo.actualizarMedidas(0f, 0f, 0f, 0f)
    }

    private fun dibujar() {
        val desarrollo = binding.etDesarrollo.text.toString().aFloat()
        val cuerda = binding.etCuerda.text.toString().aFloat()
        val flecha = binding.etFlecha.text.toString().aFloat()
        val largo = binding.etLargo.text.toString().aFloat()

        if (cuerda <= 0f || flecha <= 0f) {
            mensaje("Ingrese cuerda y flecha mayores a cero", error = true)
            return
        }

        val radio = (cuerda * cuerda) / (8f * flecha) + flecha / 2f
        val angulo = 2f * asin((cuerda / (2f * radio)).coerceIn(-1f, 1f))
        val desarrolloCalculado = radio * angulo
        val diferencia = abs(desarrollo - desarrolloCalculado)

        if (desarrollo > 0f && diferencia > 0.5f) {
            mensaje(
                "El desarrollo no coincide con la cuerda y la flecha: " +
                    "con cuerda ${fmt(cuerda)} y flecha ${fmt(flecha)} sería ${fmt(desarrolloCalculado)} " +
                    "(diferencia ${fmt(diferencia)}).",
                error = true
            )
        } else {
            mensaje("Geometría válida · Radio ${fmt(radio)}", error = false)
        }

        binding.vistaCurvo.actualizarMedidas(desarrollo, cuerda, flecha, largo)
    }

    /** La franja de arriba del dibujo: color de Crystal si está bien, rojo si algo no cuadra. */
    private fun mensaje(texto: String, error: Boolean) {
        binding.tvMensaje.text = texto
        binding.tvMensaje.backgroundTintList = android.content.res.ColorStateList.valueOf(
            ContextCompat.getColor(this, if (error) R.color.rojo else R.color.color)
        )
    }

    // Un decimal: son medidas, no dinero.
    private fun fmt(valor: Float): String = String.format(java.util.Locale.US, "%.1f", valor)

    private fun String.aFloat(): Float = trim().replace(",", ".").toFloatOrNull() ?: 0f
}
