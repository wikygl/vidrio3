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

        // ✅ USAR EL NOMBRE QUE YA VIENE DE FIREBASE
        holder.binding.chatNameText.text = chat.name

        // Mostrar último mensaje o placeholder
        holder.binding.usersTextView.text = if (chat.lastMessageText.isNotBlank()) {
            chat.lastMessageText
        } else {
            "Sin mensajes aún"
        }

        // Cargar foto de perfil del otro usuario
        val otherUserId = chat.users.firstOrNull { it != currentUserId }

        if (otherUserId != null && otherUserId != currentUserId) {
            // Es un chat con otra persona, cargar su foto
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
                        .error(R.drawable.ic_mensajesno)
                        .into(holder.binding.ivFoto)
                }
                .addOnFailureListener {
                    // Si falla cargar foto, usar imagen por defecto
                    Glide.with(holder.itemView.context)
                        .load(R.drawable.ic_mensajesno)
                        .circleCrop()
                        .into(holder.binding.ivFoto)
                }
        } else {
            // Es "Mensajes guardados" (self-chat), usar icono especial
            Glide.with(holder.itemView.context)
                .load(R.drawable.ic_chckr)
                .circleCrop()
                .into(holder.binding.ivFoto)
        }

        // Badge de mensajes no leídos
        if (chat.unreadCount > 0) {
            holder.binding.tvUnreadCount.apply {
                text = chat.unreadCount.toString()
                visibility = View.VISIBLE
            }
        } else {
            holder.binding.tvUnreadCount.visibility = View.GONE
        }

        // Click handler
        holder.binding.root.setOnClickListener {
            chatClick(chat)
        }
    }

    override fun getItemCount(): Int = chats.size

    class ChatViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root)
}