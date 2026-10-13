package crystal.crystal.tienda

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import crystal.crystal.R
import crystal.crystal.Suscripcion
import crystal.crystal.databinding.ActivityPreciosReferenciaBinding
import kotlinx.coroutines.launch

/**
 * Precios de referencia por ciudad (docs/TIENDA_ANUNCIOS_PLAN.md, fase 3), del plan FULL: el
 * servidor junta los anuncios que casan con lo buscado y devuelve mínimo, mediana y máximo por
 * unidad, solo con 3 vendedores distintos o más. La compuerta de la app es cortesía; la que manda
 * está en el servidor (`PLAN_FULL`).
 */
class PreciosReferenciaActivity : AppCompatActivity() {

    private lateinit var b: ActivityPreciosReferenciaBinding
    private val repo = TiendaRepositorio()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPreciosReferenciaBinding.inflate(layoutInflater)
        setContentView(b.root)
        if (repo.uid == null) {
            finish()
            return
        }
        b.btnAtras.setOnClickListener { finish() }
        b.etCiudad.setText(MiCiudad.get(this))
        b.btnConsultar.setOnClickListener { consultar() }
        b.etTexto.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_SEARCH) { consultar(); true } else false
        }
        if (!Suscripcion.avanzadoActivo()) invitar()
    }

    private fun invitar() {
        Suscripcion.invitarFull(this, "Los precios de referencia por ciudad son parte del plan FULL.")
    }

    private fun consultar() {
        val texto = b.etTexto.text.toString().trim()
        val ciudad = b.etCiudad.text.toString().trim()
        if (TiendaReglas.palabraDeBusqueda(texto) == null) {
            Toast.makeText(this, "Escriba qué busca, por ejemplo «templado 8».", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Suscripcion.exigir(this, Suscripcion.avanzadoActivo(), "Los precios de referencia por ciudad son parte del plan FULL.")) return
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(b.etTexto.windowToken, 0)
        b.pbCargando.visibility = View.VISIBLE
        b.btnConsultar.isEnabled = false
        b.contenedorResultados.removeAllViews()
        lifecycleScope.launch {
            try {
                val r = repo.preciosReferencia(texto, ciudad)
                pintar(r, texto, ciudad)
            } catch (e: Exception) {
                if (TiendaRepositorio.esFaltaPlan(e)) invitar()
                else Toast.makeText(this@PreciosReferenciaActivity, TiendaRepositorio.mensaje(e), Toast.LENGTH_LONG).show()
            } finally {
                b.pbCargando.visibility = View.GONE
                b.btnConsultar.isEnabled = true
            }
        }
    }

    private fun pintar(r: TiendaRepositorio.Referencia, texto: String, ciudad: String) {
        val c = b.contenedorResultados
        if (ciudad.isNotBlank()) {
            c.addView(seccion("EN ${ciudad.uppercase()}"))
            if (r.ciudad.isEmpty()) c.addView(nota("Todavía no hay suficientes vendedores de «$texto» en $ciudad."))
            r.ciudad.forEach { c.addView(renglon(it)) }
        }
        c.addView(seccion("EN TODO EL PAÍS"))
        if (r.pais.isEmpty()) c.addView(nota("Todavía no hay suficientes vendedores de «$texto» en la Tienda."))
        r.pais.forEach { c.addView(renglon(it)) }
        c.addView(nota("Revisados ${r.revisados} anuncios. Es lo que se PIDE en los anuncios, no lo que se cobró."))
    }

    private fun seccion(t: String) = TextView(this).apply {
        text = t
        textSize = 12f
        setTypeface(typeface, Typeface.BOLD)
        letterSpacing = 0.08f
        setTextColor(0xFF7A7A7A.toInt())
        setPadding(dp(4), dp(16), 0, dp(4))
    }

    private fun nota(t: String) = TextView(this).apply {
        text = t
        textSize = 13f
        setTextColor(0xFF7A7A7A.toInt())
        setPadding(dp(4), dp(4), dp(4), dp(4))
    }

    /** La mediana en grande ("Por m²: S/ 98.00") y debajo el rango y cuántos vendedores. */
    private fun renglon(p: PrecioReferencia) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = ContextCompat.getDrawable(context, R.drawable.bg_control_panel)
        setPadding(dp(12), dp(10), dp(12), dp(10))
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            .apply { setMargins(0, dp(4), 0, dp(4)) }
        val s = { cent: Long -> PublicarAnuncioActivity.soles(cent) }
        addView(TextView(context).apply {
            text = "Por ${p.unidad}: ${s(p.medianaCent)}"
            textSize = 17f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, R.color.negro))
        })
        addView(TextView(context).apply {
            text = "Desde ${s(p.minCent)} hasta ${s(p.maxCent)} · ${p.vendedores} vendedores"
            textSize = 14f
            setTextColor(0xFF5A5A5A.toInt())
        })
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    companion object {
        fun intent(ctx: Context) = Intent(ctx, PreciosReferenciaActivity::class.java)
    }
}
