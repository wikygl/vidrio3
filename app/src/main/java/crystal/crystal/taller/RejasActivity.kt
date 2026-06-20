package crystal.crystal.taller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityRejasBinding

class RejasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRejasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRejasBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}