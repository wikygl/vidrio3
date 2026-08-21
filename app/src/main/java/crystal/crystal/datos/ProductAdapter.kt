package crystal.crystal.datos

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import crystal.crystal.R

class ProductAdapter(
    private val onClick: ((Product) -> Unit)? = null
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var productList: List<Product> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Product>) {
        productList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener { onClick?.invoke(product) }
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDescription: TextView = itemView.findViewById(R.id.tvItemDescription)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvItemPrice)
        private val tvImages: TextView = itemView.findViewById(R.id.tvItemImages)
        private val ivPreview: ImageView = itemView.findViewById(R.id.ivItemPreview)

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            val imagenes = product.imagenes()
            tvDescription.text = product.nombre
            tvPrice.text = "S/ ${String.format("%.2f", product.price)}"
            tvImages.text = "Imagenes: ${imagenes.size}"
            // Glide en vez de setImageURI: una URI content:// inaccesible (permiso perdido tras
            // reinstalar) NO debe crashear el listado; Glide falla en silencio y muestra vacío.
            if (imagenes.isNotEmpty()) {
                Glide.with(itemView)
                    .load(Uri.parse(imagenes.first()))
                    .into(ivPreview)
            } else {
                Glide.with(itemView).clear(ivPreview)
                ivPreview.setImageResource(android.R.color.transparent)
            }
        }
    }
}
