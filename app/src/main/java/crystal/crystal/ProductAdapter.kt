package crystal.crystal

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var productList: List<Product> = emptyList()

    // Actualiza la lista y notifica cambios
    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Product>) {
        productList = list
        notifyDataSetChanged()
    }

    // Infla el layout de cada ítem
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    // Cantidad de elementos
    override fun getItemCount(): Int = productList.size

    // Liga datos de la lista con cada ViewHolder
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    // ViewHolder que referencia las vistas
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDescription: TextView = itemView.findViewById(R.id.tvItemDescription)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvItemPrice)
        private val tvStock: TextView = itemView.findViewById(R.id.tvItemStock)
        private val tvId : TextView = itemView.findViewById(R.id.tvItemCode)

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            tvDescription.text = product.description
            tvPrice.text = "$${product.price}"
            tvStock.text = "Stock: ${product.stock}"
            tvId.text = "Código: ${product.id}"
        }
    }
}
