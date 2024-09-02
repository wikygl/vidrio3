package crystal.crystal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.registro.InicioActtivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, InicioActtivity::class.java)
        startActivity(intent)
        finish()
    }
}