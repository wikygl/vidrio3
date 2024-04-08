package calculaora.e.vidrio3.registro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import calculaora.e.vidrio3.MainActivity
import calculaora.e.vidrio3.R
import kotlinx.android.synthetic.main.activity_acliente.*

class Acliente : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acliente)

        btGo.setOnClickListener {
            if (etCliente.text.isNotEmpty()) {
                val id = Bundle().apply {
                    putString("id",etCliente.text.toString())
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtras(id)
                startActivity(intent)
            }

        }
    }
}