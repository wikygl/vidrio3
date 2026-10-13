package crystal.crystal.tienda

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import crystal.crystal.databinding.ActivityPedidoClienteBinding
import crystal.crystal.pagos.CanalPagos
import kotlinx.coroutines.launch

/**
 * Un pedido de un cliente de la web (fase 3). Tomarlo cobra del saldo en el servidor
 * (`tomarPedidoCliente`) —gratis si pregunta por un anuncio propio— y entrega el nombre y el
 * teléfono, con Llamar y WhatsApp. Si es falso, se reporta y el panel puede devolver lo cobrado.
 */
class PedidoClienteActivity : AppCompatActivity() {

    private lateinit var b: ActivityPedidoClienteBinding
    private val repo = TiendaRepositorio()
    private lateinit var pedidoId: String
    private var pedido: PedidoCliente? = null
    private var toma: TomaPedido? = null
    private var tarifas = TiendaReglas.Tarifas()
    private var saldoCent = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPedidoClienteBinding.inflate(layoutInflater)
        setContentView(b.root)
        pedidoId = intent.getStringExtra(EXTRA_ID).orEmpty()
        if (pedidoId.isEmpty() || repo.uid == null) {
            finish()
            return
        }
        b.btnAtras.setOnClickListener { finish() }
        b.btnTomar.setOnClickListener { tomar() }
        b.btnLlamar.setOnClickListener { toma?.let { startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:+" + it.telefono))) } }
        b.btnWhatsapp.setOnClickListener { toma?.let { abrirWhatsapp(it) } }
        b.tvReportar.setOnClickListener { reportar() }
        cargar()
    }

    private fun cargar() {
        lifecycleScope.launch {
            val p = runCatching { repo.pedido(pedidoId) }.getOrNull()
            if (p == null) {
                Toast.makeText(this@PedidoClienteActivity, "Este pedido ya no está disponible", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }
            pedido = p
            toma = runCatching { repo.miToma(pedidoId) }.getOrNull()
            runCatching { repo.tarifasYSaldo() }.onSuccess { (t, s) -> tarifas = t; saldoCent = s }
            mostrar(p)
        }
    }

    private fun mostrar(p: PedidoCliente) {
        val ahora = System.currentTimeMillis()
        val yo = repo.uid.orEmpty()
        b.tvQue.text = p.titulo
        b.tvLugar.text = listOf(
            p.nombreCorto, p.ciudad, TiendaReglas.hace(p.creadoEnMs, ahora),
            if (p.dirigido) "" else "${p.tomas} de ${p.maxTalleres} talleres"
        ).filter { it.isNotBlank() }.joinToString(" · ")
        b.tvMedidas.text = p.medidas.ifBlank { "No indicó medidas" }
        b.tvDetalle.text = p.detalle.ifBlank { "Sin detalle" }

        val t = toma
        b.lyContacto.visibility = if (t != null) View.VISIBLE else View.GONE
        if (t != null) {
            b.tvContacto.text = "${t.nombre}\n+${t.telefono}"
            b.tvReportar.text = if (t.reportado) "Ya lo reportó. Lo revisaremos." else "¿Número equivocado o pedido falso? Reportar"
            b.tvReportar.isEnabled = !t.reportado
            b.tvCosto.visibility = View.GONE
            b.btnTomar.visibility = View.GONE
            return
        }
        val disponible = p.disponiblePara(yo, ahora)
        val precio = if (p.dirigido) 0L else tarifas.pedidoCent
        b.tvCosto.visibility = View.VISIBLE
        b.tvCosto.text = when {
            !disponible -> "Este pedido ya lo tomaron otros talleres o venció."
            p.dirigido -> "El cliente preguntó por su anuncio desde la web. Tomarlo es gratis: recibe su nombre y su teléfono."
            precio > 0 -> "Tomar el pedido cuesta ${PublicarAnuncioActivity.soles(precio)}: recibe el nombre y el teléfono del cliente. " +
                "Lo pueden tomar hasta ${p.maxTalleres} talleres. Su saldo: ${PublicarAnuncioActivity.soles(saldoCent)}."
            else -> "Tomar el pedido es gratis por ahora: recibe el nombre y el teléfono del cliente."
        }
        b.btnTomar.visibility = if (disponible) View.VISIBLE else View.GONE
        b.btnTomar.text = if (precio > 0) "Tomar pedido · ${PublicarAnuncioActivity.soles(precio)}" else "Tomar pedido"
    }

    private fun tomar() {
        val p = pedido ?: return
        val precio = if (p.dirigido) 0L else tarifas.pedidoCent
        if (precio > 0 && !CanalPagos.DISPONIBLE_EN_LA_APP) {
            PublicarAnuncioActivity.avisarNoDisponible(this)
            return
        }
        val patron = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("patron_uid", null)
        if (!patron.isNullOrBlank() && patron != repo.uid) {
            Toast.makeText(this, "Los pedidos se toman desde la cuenta del patrón, no desde una terminal.", Toast.LENGTH_LONG).show()
            return
        }
        if (saldoCent < precio) {
            PublicarAnuncioActivity.pedirRecarga(this, precio, saldoCent)
            return
        }
        val ejecutar = {
            b.btnTomar.isEnabled = false
            lifecycleScope.launch {
                runCatching { repo.tomarPedido(p.id) }
                    .onSuccess { t ->
                        toma = t
                        Toast.makeText(this@PedidoClienteActivity, "Pedido tomado. Llame o escriba al cliente.", Toast.LENGTH_LONG).show()
                        cargar()
                    }
                    .onFailure { e ->
                        if (TiendaRepositorio.esSaldoInsuficiente(e)) PublicarAnuncioActivity.pedirRecarga(this@PedidoClienteActivity, precio, saldoCent)
                        else {
                            Toast.makeText(this@PedidoClienteActivity, TiendaRepositorio.mensaje(e), Toast.LENGTH_LONG).show()
                            cargar()
                        }
                    }
                b.btnTomar.isEnabled = true
            }
        }
        if (precio <= 0) {
            ejecutar()
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Tomar pedido")
            .setMessage(
                "Recibe el nombre y el teléfono del cliente.\n\n" +
                    "Cuesta ${PublicarAnuncioActivity.soles(precio)}. Su saldo: ${PublicarAnuncioActivity.soles(saldoCent)} → " +
                    PublicarAnuncioActivity.soles(saldoCent - precio) + "."
            )
            .setPositiveButton("Tomar") { _, _ -> ejecutar() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirWhatsapp(t: TomaPedido) {
        val p = pedido
        val texto = "Hola ${t.nombre.split(" ").first()}, le escribo por su pedido de la Tienda Crystal" +
            (p?.let { ": ${it.que.ifBlank { it.anuncioTitulo }}" } ?: "") + "."
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(t.whatsapp + "?text=" + Uri.encode(texto)))
        runCatching { startActivity(i) }.onFailure {
            Toast.makeText(this, "No se encontró WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    private fun reportar() {
        val t = toma ?: return
        if (t.reportado) return
        val et = EditText(this).apply { hint = "¿Qué pasó? (opcional)"; maxLines = 3 }
        val cont = LinearLayout(this).apply {
            val m = (20 * resources.displayMetrics.density).toInt()
            setPadding(m, 0, m, 0)
            addView(et)
        }
        AlertDialog.Builder(this)
            .setTitle("Reportar pedido")
            .setMessage("Si el número no existe o el pedido es falso, lo revisamos y le devolvemos lo cobrado.")
            .setView(cont)
            .setPositiveButton("Reportar") { _, _ ->
                lifecycleScope.launch {
                    runCatching { repo.reportarPedido(pedidoId, et.text.toString().trim()) }
                        .onSuccess {
                            Toast.makeText(this@PedidoClienteActivity, "Gracias. Lo revisaremos.", Toast.LENGTH_LONG).show()
                            cargar()
                        }
                        .onFailure { Toast.makeText(this@PedidoClienteActivity, TiendaRepositorio.mensaje(it), Toast.LENGTH_LONG).show() }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    companion object {
        const val EXTRA_ID = "pedido_cliente_id"
        fun intent(ctx: Context, id: String) = Intent(ctx, PedidoClienteActivity::class.java).putExtra(EXTRA_ID, id)
    }
}
