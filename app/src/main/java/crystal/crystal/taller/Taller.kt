package crystal.crystal.taller

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.MainActivity
import crystal.crystal.databinding.ActivityTallerBinding
import crystal.crystal.optimizadores.Corte
import crystal.crystal.optimizadores.Ncurvos
import kotlinx.android.synthetic.main.activity_taller.*

class Taller : AppCompatActivity() {


    private lateinit var binding: ActivityTallerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTallerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val paqueteR=intent.extras
        val cliente= paqueteR?.getString("cliente")
        if (cliente!=null){binding.cliente.text="$cliente"}

        puerta.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}

            val intent = Intent(this, PuertaPano::class.java)
            intent.putExtras(paquete)
            startActivity(intent)}

        micro.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtras(paquete)
            startActivity(intent)}

        btn_novaina.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}

                val intent = Intent(this, NovaIna::class.java)
                intent.putExtras(paquete)
            startActivity(intent)
        }

        btn_novaapa.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, NovaApa::class.java)
            intent.putExtras(paquete)
            startActivity(intent)}

        btn_mpaflon.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, MamparaPaflon::class.java)
            intent.putExtras(paquete)
            startActivity(intent)}

        btn_mfc.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, MamparaFC::class.java)
            intent.putExtras(paquete)
            startActivity(intent)}

        btn_v.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, Vitroven::class.java)
            intent.putExtras(paquete)
            startActivity(intent)}

        btn_Estructura.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, VentanaAl::class.java)
            intent.putExtras(paquete)
            startActivity(intent) }

        btn_PivotE.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, PivotAl::class.java)
            intent.putExtras(paquete)
            startActivity(intent)
        }
        btnMc.setOnClickListener {

            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, Muro::class.java)
            intent.putExtras(paquete)
            startActivity(intent) }

        btCurvo.setOnClickListener { startActivity(Intent(this, Ncurvos::class.java)) }
        btOptiLineal.setOnClickListener { startActivity(Intent(this, Corte::class.java)) }

    }

}