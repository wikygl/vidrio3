package crystal.crystal.catalogo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityFotosBinding

class Fotos:AppCompatActivity() {

    private lateinit var bindin: ActivityFotosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindin = ActivityFotosBinding.inflate(layoutInflater)
        setContentView(bindin.root)

    }
}
