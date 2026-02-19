package crystal.crystal.clientes

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.R
import java.text.NumberFormat
import java.util.*

/**
 * Diálogo para seleccionar cliente cuando hay múltiples resultados
 * Navegable por número (sin voz por ahora)
 */
class DialogoSeleccionClientes : DialogFragment() {

    companion object {
        private const val ARG_CLIENTES = "clientes"

        fun newInstance(
            clientes: List<Cliente>,
            onClienteSeleccionado: (Cliente) -> Unit
        ): DialogoSeleccionClientes {
            val dialog = DialogoSeleccionClientes()
            dialog.onSeleccionCallback = onClienteSeleccionado
            dialog.clientesLista = ArrayList(clientes)
            return dialog
        }
    }

    private var onSeleccionCallback: ((Cliente) -> Unit)? = null
    private var clientesLista: ArrayList<Cliente> = ArrayList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(
            R.layout.dialog_seleccion_clientes,
            null
        )

        val rvClientes = view.findViewById<RecyclerView>(R.id.rvClientesSeleccion)
        val tvCantidad = view.findViewById<TextView>(R.id.tvCantidad)

        tvCantidad.text = "Encontrados ${clientesLista.size} clientes"

        val adapter = ClienteSeleccionAdapter { cliente ->
            onSeleccionCallback?.invoke(cliente)
            dismiss()
        }

        rvClientes.layoutManager = LinearLayoutManager(requireContext())
        rvClientes.adapter = adapter
        adapter.submitList(clientesLista)

        return AlertDialog.Builder(requireContext())
            .setTitle("Selecciona cliente")
            .setView(view)
            .setNegativeButton("Cancelar", null)
            .create()
    }

    /**
     * Adapter simple para selección
     */
    private class ClienteSeleccionAdapter(
        private val onClienteClick: (Cliente) -> Unit
    ) : RecyclerView.Adapter<ClienteSeleccionAdapter.ViewHolder>() {

        private var clientes: List<Cliente> = emptyList()

        fun submitList(lista: List<Cliente>) {
            clientes = lista
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_cliente_seleccion,
                parent,
                false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(clientes[position], position + 1)
        }

        override fun getItemCount() = clientes.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvNumero = itemView.findViewById<TextView>(R.id.tvNumero)
            private val tvNombre = itemView.findViewById<TextView>(R.id.tvNombreSeleccion)
            private val tvDocumento = itemView.findViewById<TextView>(R.id.tvDocumentoSeleccion)
            private val tvEstadisticas = itemView.findViewById<TextView>(R.id.tvEstadisticasSeleccion)

            fun bind(cliente: Cliente, numero: Int) {
                tvNumero.text = "$numero"
                tvNombre.text = cliente.nombreCompleto
                tvDocumento.text = "${cliente.tipoDocumento.nombre}: ${cliente.numeroDocumento}"

                val formato = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
                tvEstadisticas.text = "${cliente.numeroCompras} compras • ${formato.format(cliente.totalCompras)}"

                itemView.setOnClickListener {
                    onClienteClick(cliente)
                }
            }
        }
    }
}