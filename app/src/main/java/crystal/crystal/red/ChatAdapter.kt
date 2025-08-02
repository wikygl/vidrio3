package crystal.crystal.red

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import crystal.crystal.R
import crystal.crystal.databinding.ItemChatBinding

class ChatAdapter(
    private val currentUserId: String,
    private val chatClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var chats: List<Chat> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Chat>) {
        chats = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]

        // 1) Poner el nombre como título grande
        holder.binding.chatNameText.text = chat.name

        // 2) Poner el subtítulo: aquí ejemplo fijo “Último mensaje”
        // Ahora muestra el mensaje real o un placeholder si está vacío
        holder.binding.usersTextView.text =
            if (chat.lastMessageText.isNotBlank()) chat.lastMessageText
            else " "  // o "Sin mensajes aún"

        // (Si tienes el texto real del último mensaje, sustitúyelo aquí)

        // 3) Cargar la foto de perfil como antes…
        val otherUserId = chat.users.firstOrNull { it != currentUserId }
        if (otherUserId != null) {
            Firebase.firestore
                .collection("usuarios")
                .document(otherUserId)
                .get()
                .addOnSuccessListener { doc ->
                    val url = doc.getString("imagenPerfil")
                    Glide.with(holder.itemView.context)
                        .load(url.takeUnless { it.isNullOrBlank() } ?: R.drawable.ic_mensajesno)
                        .circleCrop()
                        .placeholder(R.drawable.ic_dormido)
                        .error(R.drawable.ic_peru)
                        .into(holder.binding.ivFoto)
                }
                .addOnFailureListener {
                    Glide.with(holder.itemView.context)
                        .load(R.drawable.ic_chckr)
                        .circleCrop()
                        .into(holder.binding.ivFoto)
                }
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.ic_chckr)
                .circleCrop()
                .into(holder.binding.ivFoto)
        }

        // 4) Badge de mensajes no leídos: un TextView circular a la derecha
        if (chat.unreadCount > 0) {
            holder.binding.tvUnreadCount.apply {
                text = chat.unreadCount.toString()
                visibility = View.VISIBLE
            }
        } else {
            holder.binding.tvUnreadCount.visibility = View.GONE
        }

        // 5) Click handler
        holder.binding.root.setOnClickListener {
            chatClick(chat)
        }
    }


    override fun getItemCount(): Int = chats.size

    class ChatViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root)
}
