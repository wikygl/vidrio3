package crystal.crystal.red

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import crystal.crystal.databinding.ActivityChatBinding
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    private var chatId = ""
    private var user = ""

    private var db = Firebase.firestore

    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra("chatId")?.let { chatId = it }
        intent.getStringExtra("user")?.let { user = it }

        if(chatId.isNotEmpty() && user.isNotEmpty()) {
            initViews()
        }
    }

    private fun initViews(){
        binding.rvMensajes.layoutManager = LinearLayoutManager(this)
        rvMensajes.adapter = MessageAdapter(user)

        binding.btEnviar.setOnClickListener { enviarMensaje() }

        val chatRef = db.collection("chats").document(chatId)

        chatRef.collection("messages").orderBy("dob", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { messages ->
                val listMessages = messages.toObjects(Message::class.java)
                (rvMensajes.adapter as MessageAdapter).setData(listMessages)
            }

        chatRef.collection("messages").orderBy("dob", Query.Direction.ASCENDING)
            .addSnapshotListener { messages, error ->
                if(error == null){
                    messages?.let {
                        val listMessages = it.toObjects(Message::class.java)
                        (rvMensajes.adapter as MessageAdapter).setData(listMessages)
                    }
                }
            }
    }

    private fun enviarMensaje(){
        val message = Message(
            message = messageTextField.text.toString(),
            from = user
        )
        db.collection("chats").document(chatId).collection("messages").document().set(message)
        messageTextField.setText("")
    }
    private fun eliminarMensaje(){
        binding.rvMensajes

    }

}