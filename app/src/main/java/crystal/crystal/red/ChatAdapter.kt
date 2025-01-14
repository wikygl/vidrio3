package crystal.crystal.red

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.R
import kotlinx.android.synthetic.main.item_chat.view.*

class ChatAdapter(val chatClick: (Chat) -> Unit): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private var chats: List<Chat> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Chat>){
        chats = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_chat,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.itemView.chatNameText.text = chats[position].name
        holder.itemView.usersTextView.text = chats[position].users.toString()

        holder.itemView.setOnClickListener {
            chatClick(chats[position])
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}