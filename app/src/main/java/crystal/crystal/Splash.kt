package crystal.crystal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.registro.InicioActivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, InicioActivity::class.java)
        startActivity(intent)
        finish()
    }
}