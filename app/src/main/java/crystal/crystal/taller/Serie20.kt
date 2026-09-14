package crystal.crystal.taller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivitySerie20Binding

class Serie20 : AppCompatActivity() {
    private lateinit var binding:ActivitySerie20Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySerie20Binding.inflate(layoutInflater)
        setContentView(binding.root)
        // Tocar "Referencias y Cálculos" abre la calculadora flotante.
        crystal.crystal.calculadora.CalculadoraFlotante.instalarEnReferencias(this)
    }
    //serie 20 2 hojas
    // vidrio = anccho/2 -5 x alto - 10.7
    //zócalo y cabezal = ancho - 6.4
    //riel sup e inf = ancho- 1.2
    //parante, traslape = alto - 2.5
}