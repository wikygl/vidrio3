package crystal.crystal.red

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import crystal.crystal.R
import crystal.crystal.databinding.ItemChatBinding
import crystal.crystal.red.interop.ChatPlatform

class ChatAdapter(
    private val currentUserId: String,
    private val chatClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var chats: List<Chat> = emptyList()
    private val photoCacheByChatId = mutableMapOf<String, String>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Chat>) {
        list.forEach { chat ->
            if (chat.id.isNotBlank() && chat.photoUrl.isNotBlank()) {
                photoCacheByChatId[chat.id] = chat.photoUrl
            }
        }
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
        val platform = ChatPlatform.fromWireValue(chat.peerPlatform)
        val platformSuffix = when (platform) {
            ChatPlatform.PUNTOS -> " · Puntos"
            else -> ""
        }
        val platformLabel = when (platform) {
            ChatPlatform.PUNTOS -> "Puntos"
            else -> "Crystal"
        }

        holder.binding.chatNameText.text = chat.name + platformSuffix
        holder.binding.usersTextView.text = if (chat.unreadCount > 0) {
            "$platformLabel · ${chat.unreadCount} sin leer"
        } else {
            "$platformLabel · Al día"
        }

        val otherUserId = chat.users.firstOrNull { it != currentUserId }
        if (otherUserId != null && otherUserId != currentUserId) {
            val effectivePhotoUrl = when {
                chat.photoUrl.isNotBlank() -> chat.photoUrl
                chat.id.isNotBlank() -> photoCacheByChatId[chat.id].orEmpty()
                else -> ""
            }

            if (effectivePhotoUrl.isNotBlank()) {
                Glide.with(holder.itemView.context)
                    .load(effectivePhotoUrl)
                    .circleCrop()
                    .dontAnimate()
                    .into(holder.binding.ivFoto)
            } else {
                Glide.with(holder.itemView.context)
                    .load(R.drawable.ic_mensajesno)
                    .circleCrop()
                    .into(holder.binding.ivFoto)
            }
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.ic_chckr)
                .circleCrop()
                .into(holder.binding.ivFoto)
        }

        if (chat.unreadCount > 0) {
            holder.binding.tvUnreadCount.apply {
                text = chat.unreadCount.toString()
                visibility = View.VISIBLE
            }
        } else {
            holder.binding.tvUnreadCount.visibility = View.GONE
        }

        holder.binding.root.setOnClickListener {
            chatClick(chat)
        }
    }

    override fun getItemCount(): Int = chats.size

    class ChatViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root)
}
