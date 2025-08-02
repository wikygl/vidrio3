package crystal.crystal.taller
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.Diseno.BarandaActivity
import crystal.crystal.Diseno.DisenoActivity
import crystal.crystal.MainActivity
import crystal.crystal.databinding.ActivityTallerBinding
import crystal.crystal.optimizadores.Corte
import crystal.crystal.optimizadores.Ncurvos

class Taller : AppCompatActivity() {


    private lateinit var binding: ActivityTallerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTallerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val paqueteR=intent.extras
        val cliente= paqueteR?.getString("cliente")
        if (cliente!=null){binding.cliente.text="$cliente"}

        binding.puerta.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}

            val intent = Intent(this, Puertas::class.java)
            intent.putExtras(paquete)
            startActivity(intent)}

        binding.micro.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtras(paquete)
            startActivity(intent)}

        binding.btnNovaina.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}

                val intent = Intent(this, NovaIna::class.java)
                intent.putExtras(paquete)
            startActivity(intent)
        }

        binding.btnNovaapa.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, NovaApa::class.java)
            intent.putExtras(paquete)
            startActivity(intent)}

        binding.btnMpaflon.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, MamparaPaflon::class.java)
            intent.putExtras(paquete)
            startActivity(intent)}

        binding.btnMfc.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, MamparaFC::class.java)
            intent.putExtras(paquete)
            startActivity(intent)}

        binding.btnV.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, Vitroven::class.java)
            intent.putExtras(paquete)
            startActivity(intent)}

        binding.btnEstructura.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, VentanaAl::class.java)
            intent.putExtras(paquete)
            startActivity(intent) }

        binding.btnPivotE.setOnClickListener {
            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, PivotAl::class.java)
            intent.putExtras(paquete)
            startActivity(intent)
        }
        binding.btnMc.setOnClickListener {

            val paquete = Bundle().apply { putString("rcliente", binding.cliente.text.toString())}
            val intent = Intent(this, Muro::class.java)
            intent.putExtras(paquete)
            startActivity(intent) }
        binding.btMvidrio.setOnClickListener { startActivity(Intent(this, MamparaVidrioActivity::class.java)) }
        binding.btnA001.setOnClickListener { startActivity(Intent(this, PDuchaActivity::class.java)) }

        binding.btCurvo.setOnClickListener { startActivity(Intent(this, Ncurvos::class.java)) }
        binding.btOptiLineal.setOnClickListener { startActivity(Intent(this, Corte::class.java)) }
        binding.tbX.setOnClickListener { startActivity(Intent(this, DisenoActivity::class.java)) }
        binding.btUnidades.setOnClickListener { startActivity(Intent(this, RejasActivity::class.java)) }
        binding.btOpti.setOnClickListener { startActivity(Intent(this, BarandaActivity::class.java)) }

    }

}