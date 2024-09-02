package crystal.crystal

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.databinding.ActivityListaBinding
import kotlinx.android.synthetic.main.activity_lista.*


class ListaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityListaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val monto: Intent = intent
        val cantidad = monto.getStringExtra("monto")
        etml.setText ("$cantidad")

        // obtener la imagen del PDF de la intent
        val bitmap = intent.getParcelableExtra<Bitmap>("pdf_image")

        // mostrar la imagen en el ImageView
        binding.imgPdf.setImageBitmap(bitmap)

    }}