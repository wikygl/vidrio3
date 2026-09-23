package crystal.crystal.taller.melamina

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.Diseno.PlanoZoomView
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import java.io.File

/**
 * La producción de los roperos del proyecto, pieza por pieza y con buscador: el técnico de corte
 * escribe la medida y el mueble ("57.9 Rm3", "cuerpo 2 entrepaño") y ve qué hacer con cada pieza.
 *
 * - CORTE: cantidad, medida, material, canto (O, C, U, L o por lados), veta, ranura, etiqueta y el
 *   código simbólico; se comparte como CSV con la estructura de la hoja de Excel.
 * - MARCAS Y AGUJEROS: cada tablero con su plano de marcas (negro) y agujeros (rosado).
 *
 * Entran todos los roperos archivados en el proyecto activo y, si se abre desde la calculadora,
 * el que se está armando.
 */
class ProduccionRoperoActivity : AppCompatActivity() {

    private sealed class Fila(val buscable: String) {
        class Corte(val mueble: String, val f: RoperoProduccion.FichaCorte) :
            Fila(listOf(mueble, f.pieza.nombre, f.material, f.canto, f.etiqueta, f.codigo, "${RoperoProduccion.fmt(f.anchoCm)} ${RoperoProduccion.fmt(f.altoCm)}",
                "${RoperoProduccion.fmt(f.anchoCm)}x${RoperoProduccion.fmt(f.altoCm)}", "n${f.numero}").joinToString(" ").lowercase())
        class Agujeros(val mueble: String, val t: RoperoProduccion.Tablero) :
            Fila(listOf(mueble, t.nombre, t.zona, "${RoperoProduccion.fmt(t.ancho)} ${RoperoProduccion.fmt(t.alto)}",
                "${RoperoProduccion.fmt(t.ancho)}x${RoperoProduccion.fmt(t.alto)}").joinToString(" ").lowercase())
    }

    private var muebles: List<RoperoProduccion.Mueble> = emptyList()
    private var cortes: List<Fila> = emptyList()
    private var agujeros: List<Fila> = emptyList()
    private var verAgujeros = false
    private lateinit var etBuscar: EditText
    private lateinit var btCorte: Button
    private lateinit var btAgujeros: Button
    private lateinit var tvCuenta: TextView
    private val adapter = Adaptador()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        muebles = cargarMuebles()
        if (muebles.isEmpty()) {
            Toast.makeText(this, "No hay roperos en el proyecto activo", Toast.LENGTH_LONG).show(); finish(); return
        }
        cortes = muebles.flatMap { mu -> RoperoProduccion.fichasDeCorte(mu.ropero, RoperoCalculo.calcular(mu.ropero), mu.codigo).map { Fila.Corte(mu.codigo, it) } }
        agujeros = muebles.flatMap { mu -> RoperoProduccion.tableros(mu.ropero).map { Fila.Agujeros(mu.codigo, it) } }

        val dp = resources.displayMetrics.density
        fun px(v: Int) = (v * dp).toInt()
        etBuscar = EditText(this).apply {
            hint = "Buscar: medida, mueble, pieza (57.9 Rm3, cuerpo 2…)"
            textSize = 14f
            setSingleLine()
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun afterTextChanged(s: Editable?) = filtrar()
            })
        }
        fun boton(t: String, accion: () -> Unit) = Button(this).apply { text = t; isAllCaps = false; textSize = 12f; setOnClickListener { accion() } }
        btCorte = boton("Corte") { verAgujeros = false; filtrar() }
        btAgujeros = boton("Marcas y agujeros") { verAgujeros = true; filtrar() }
        tvCuenta = TextView(this).apply { textSize = 12f; setTextColor(Color.DKGRAY); setPadding(px(10), 0, px(10), px(4)) }
        val lista = RecyclerView(this).apply { layoutManager = LinearLayoutManager(this@ProduccionRoperoActivity); adapter = this@ProduccionRoperoActivity.adapter }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(px(8), px(8), px(8), 0)
            addView(etBuscar)
            addView(LinearLayout(this@ProduccionRoperoActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                addView(btCorte, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                addView(btAgujeros, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.4f))
                addView(boton("Exportar") { elegirExportacion() }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            })
            addView(tvCuenta)
            addView(lista, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        })
        title = "Producción: " + muebles.joinToString(", ") { it.codigo }
        filtrar()
    }

    /** El ropero que viene de la calculadora (si viene) y los archivados en el proyecto activo. */
    private fun cargarMuebles(): List<RoperoProduccion.Mueble> {
        val salen = mutableListOf<RoperoProduccion.Mueble>()
        val codigoActual = intent.getStringExtra(EXTRA_CODIGO).orEmpty()
        Ropero.desdeJson(intent.getStringExtra(EXTRA_ROPERO))?.let { salen.add(RoperoProduccion.Mueble(codigoActual.ifBlank { "actual" }, it)) }
        val proyecto = ProyectoManager.getProyectoActivo() ?: return salen
        MapStorage.cargarProyecto(this, proyecto)?.get(RoperoActivity.CLAVE_DISENO)?.forEach { fila ->
            val id = fila.getOrNull(2)?.trim().orEmpty()
            if (id.isBlank() || salen.any { it.codigo.equals(id, ignoreCase = true) }) return@forEach
            Ropero.desdeJson(fila.getOrNull(0)?.trim())?.let { salen.add(RoperoProduccion.Mueble(id, it)) }
        }
        return salen
    }

    private fun filtrar() {
        val palabras = etBuscar.text.toString().lowercase().replace(",", ".").split(Regex("\\s+")).filter { it.isNotBlank() }
        val todas = if (verAgujeros) agujeros else cortes
        val vistas = todas.filter { f -> palabras.all { f.buscable.contains(it) } }
        adapter.filas = vistas
        adapter.notifyDataSetChanged()
        btCorte.alpha = if (verAgujeros) 0.55f else 1f
        btAgujeros.alpha = if (verAgujeros) 1f else 0.55f
        tvCuenta.text = "${vistas.size} de ${todas.size} ${if (verAgujeros) "tableros con marcas" else "piezas de corte"}"
    }

    /** La hoja de corte en Excel (para abrirla en Excel, Hojas de cálculo o WPS) o en CSV (para máquinas). */
    private fun elegirExportacion() {
        AlertDialog.Builder(this)
            .setTitle("Exportar hoja de corte")
            .setItems(arrayOf("Excel (.xlsx): para abrir en Excel, Hojas de cálculo o WPS", "CSV por comas: para máquinas")) { _, i ->
                if (i == 0) compartirXlsx() else compartirCsv()
            }
            .show()
    }

    private fun nombreDeArchivo(): String =
        "corte_" + ProyectoManager.getProyectoActivo().orEmpty().ifBlank { "roperos" }.replace(Regex("[^A-Za-z0-9_-]"), "_")

    private fun compartir(file: File, tipo: String, titulo: String) {
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = tipo
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Hoja de corte ${muebles.joinToString(", ") { it.codigo }}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, titulo))
    }

    private fun compartirXlsx() {
        runCatching {
            val dir = File(cacheDir, "pdfs").apply { mkdirs() }
            val file = File(dir, "${nombreDeArchivo()}.xlsx")
            file.writeBytes(HojaDeCorteXlsx.generar(muebles, ProyectoManager.getProyectoActivo().orEmpty().ifBlank { "Corte" }))
            compartir(file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Compartir hoja de corte")
        }.onFailure { Toast.makeText(this, "No se pudo hacer el Excel: ${it.message}", Toast.LENGTH_LONG).show() }
    }

    private fun compartirCsv() {
        runCatching {
            val dir = File(cacheDir, "pdfs").apply { mkdirs() }
            val file = File(dir, "${nombreDeArchivo()}.csv")
            // Con la marca UTF-8 (BOM) delante, para quien lo abra en una hoja de cálculo.
            file.writeText(Char(0xFEFF) + RoperoProduccion.csv(muebles), Charsets.UTF_8)
            compartir(file, "text/csv", "Compartir hoja de corte (CSV)")
        }.onFailure { Toast.makeText(this, "No se pudo hacer el CSV: ${it.message}", Toast.LENGTH_LONG).show() }
    }

    /** La pieza sola, con zoom, y lo que dice de ella. */
    private fun mostrar(fila: Fila) {
        val bmp = when (fila) {
            is Fila.Corte -> PiezaTallerDibujo.corte(fila.f)
            is Fila.Agujeros -> PiezaTallerDibujo.tablero(fila.t)
        }
        val zoom = PlanoZoomView(this).apply { setBackgroundColor(Color.parseColor("#BDBDBD")) }
        val h = (resources.displayMetrics.heightPixels * 0.62f).toInt()
        val caja = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(zoom, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, h))
            if (fila is Fila.Agujeros) addView(TextView(this@ProduccionRoperoActivity).apply {
                textSize = 12f; setPadding(24, 12, 24, 12)
                text = fila.t.marcas.joinToString("\n") { m ->
                    "${m.tipo.etiqueta} ${m.cara}: marca ${RoperoProduccion.fmt(m.a)}" +
                        (m.agujero?.let { " · agujero ${RoperoProduccion.fmt(it)}" } ?: "") +
                        (if (m.hondo.isNotEmpty()) " · a lo hondo ${m.hondo.joinToString(", ") { RoperoProduccion.fmt(it) }}" else "") +
                        (if (m.nota.isNotBlank()) " · ${m.nota}" else "")
                }
            })
        }
        zoom.setBitmapAjustado(bmp)
        val d = AlertDialog.Builder(this).setView(android.widget.ScrollView(this).apply { addView(caja) }).setPositiveButton("Cerrar", null)
        if (fila is Fila.Corte) d.setNeutralButton("Copiar código") { _, _ ->
            (getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager)
                .setPrimaryClip(android.content.ClipData.newPlainText("código", fila.f.codigo))
            Toast.makeText(this, "Código copiado", Toast.LENGTH_SHORT).show()
        }
        d.show()
    }

    private inner class Adaptador : RecyclerView.Adapter<Adaptador.VH>() {
        var filas: List<Fila> = emptyList()

        inner class VH(val tv: TextView) : RecyclerView.ViewHolder(tv)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val tv = TextView(parent.context).apply {
                textSize = 13f
                setTextColor(Color.BLACK)
                setPadding(24, 18, 24, 18)
                layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 6 }
                setBackgroundColor(Color.parseColor("#F3F6FA"))
                gravity = Gravity.START
            }
            return VH(tv)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val fila = filas[position]
            holder.tv.text = when (fila) {
                is Fila.Corte -> {
                    val f = fila.f
                    android.text.SpannableStringBuilder().apply {
                        val titulo = "${fila.mueble} · N° ${f.numero} · ${f.pieza.nombre} × ${f.pieza.cantidad}\n"
                        append(titulo); setSpan(android.text.style.StyleSpan(Typeface.BOLD), 0, titulo.length, 0)
                        append("${RoperoProduccion.fmt(f.anchoCm)} x ${RoperoProduccion.fmt(f.altoCm)} · ${f.material}\n")
                        append("canto: ${f.canto.ifBlank { "sin canto" }}")
                        if (f.vetas.isNotBlank()) append(" · veta ${f.vetas}")
                        if (f.ranura.isNotBlank()) append(" · ranura ${f.ranura}")
                        append("\n${f.etiqueta}\n")
                        val ini = length
                        append(f.codigo)
                        setSpan(android.text.style.TypefaceSpan("monospace"), ini, length, 0)
                        setSpan(android.text.style.ForegroundColorSpan(Color.parseColor("#0D47A1")), ini, length, 0)
                    }
                }
                is Fila.Agujeros -> {
                    val t = fila.t
                    val uniones = t.marcas.filter { it.tipo == RoperoProduccion.TipoMarca.UNION }
                    android.text.SpannableStringBuilder().apply {
                        val titulo = "${fila.mueble} · ${t.nombre} · ${t.zona}\n"
                        append(titulo); setSpan(android.text.style.StyleSpan(Typeface.BOLD), 0, titulo.length, 0)
                        append("${RoperoProduccion.fmt(t.ancho)} x ${RoperoProduccion.fmt(t.alto)} · ${t.marcas.size} marcas\n")
                        if (uniones.isNotEmpty()) append("uniones: " + uniones.joinToString("  ") { "${RoperoProduccion.fmt(it.a)}/${RoperoProduccion.fmt(it.agujero ?: it.a)}" })
                        t.marcas.filter { it.tipo != RoperoProduccion.TipoMarca.UNION }.groupBy { it.tipo }.forEach { (tipo, ms) ->
                            append("\n${tipo.etiqueta}: " + ms.joinToString("  ") { RoperoProduccion.fmt(it.a) })
                        }
                    }
                }
            }
            holder.tv.setOnClickListener { mostrar(fila) }
        }

        override fun getItemCount() = filas.size
    }

    companion object {
        const val EXTRA_ROPERO = "produccion_ropero_json"
        const val EXTRA_CODIGO = "produccion_ropero_codigo"

        fun abrir(context: Context, ropero: Ropero? = null, codigo: String = "") {
            context.startActivity(Intent(context, ProduccionRoperoActivity::class.java).apply {
                ropero?.let { putExtra(EXTRA_ROPERO, it.aJson()) }
                putExtra(EXTRA_CODIGO, codigo)
            })
        }
    }
}
