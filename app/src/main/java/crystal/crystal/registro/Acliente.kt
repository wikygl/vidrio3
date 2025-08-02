package crystal.crystal.registro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.MainActivity
import crystal.crystal.databinding.ActivityAclienteBinding

class Acliente : AppCompatActivity() {

    lateinit var binding: ActivityAclienteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAclienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btGo.setOnClickListener {
            if (binding.etCliente.text.isNotEmpty()) {
                val id = Bundle().apply {
                    putString("id",binding.etCliente.text.toString())
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtras(id)
                startActivity(intent)
            }

        }
    }
}