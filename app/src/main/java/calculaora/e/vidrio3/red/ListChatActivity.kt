package calculaora.e.vidrio3.red

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import calculaora.e.vidrio3.databinding.ActivityListChatBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ListChatActivity : AppCompatActivity() {
    private var user = ""
    private var db = Firebase.firestore

    private lateinit var binding:ActivityListChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra("user")?.let {user = it }
        if (user. isNotEmpty()){initViews()}
    }
    private fun initViews(){
        binding.btnEnviar.setOnClickListener { nuevoChat() }

        binding.rvChatList.layoutManager = LinearLayoutManager(this)
        binding.rvChatList.adapter =
            ChatAdapter { chat ->
                chatSeleccionado(chat)
            }

        val userRef = db.collection("users").document(user)

        userRef.collection("chats")
            .get()
            .addOnSuccessListener { chats ->
                val listChats = chats.toObjects(Chat::class.java)

                (binding.rvChatList.adapter as ChatAdapter).setData(listChats)
            }

        userRef.collection("chats")
            .addSnapshotListener { chats, error ->
                if(error == null){
                    chats?.let {
                        val listChats = it.toObjects(Chat::class.java)

                        (binding.rvChatList.adapter as ChatAdapter).setData(listChats)
                    }
                }
            }
    }

    private fun chatSeleccionado(chat: Chat){
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", chat.id)
        intent.putExtra("user", user)
        startActivity(intent)
    }

    private fun nuevoChat(){
        val chatId = UUID.randomUUID().toString()
        val otherUser = binding.etNuevoMsm.text.toString()
        val users = listOf(user, otherUser)

        val chat = Chat(
            id = chatId,
            name = "Chat con $otherUser",
            users = users
        )

        db.collection("chats").document(chatId).set(chat)
        db.collection("users").document(user).collection("chats").document(chatId).set(chat)
        db.collection("users").document(otherUser).collection("chats").document(chatId).set(chat)

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", chatId)
        intent.putExtra("user", user)
        startActivity(intent)
    }
}