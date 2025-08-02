package crystal.crystal.optimizadores

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityNcurvosBinding

class Ncurvos : AppCompatActivity() {

    lateinit var binding: ActivityNcurvosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNcurvosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btCalcular.setOnClickListener {

        }

    }
}