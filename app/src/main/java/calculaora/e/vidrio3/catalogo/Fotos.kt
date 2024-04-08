package calculaora.e.vidrio3.catalogo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import calculaora.e.vidrio3.databinding.ActivityFotosBinding

class Fotos:AppCompatActivity() {

    private lateinit var bindin: ActivityFotosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindin = ActivityFotosBinding.inflate(layoutInflater)
        setContentView(bindin.root)

    }
}
