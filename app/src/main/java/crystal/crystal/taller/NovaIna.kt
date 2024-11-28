package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.FichaActivity
import crystal.crystal.R
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.databinding.ActivityNovaInaBinding

class NovaIna : AppCompatActivity() {

    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private lateinit var binding: ActivityNovaInaBinding
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNovaInaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cliente()

        binding.lyU.visibility = View.GONE
        binding.u38layout.visibility = View.GONE
        binding.ulayout.visibility = View.VISIBLE
        calcular()

        binding.textView7.setOnClickListener {
            if (binding.med1Nfcfi.text.isNotEmpty()) {
                val paquete = Bundle().apply {
                    putString("u38", binding.u38txtNfcfi.text.toString())
                    putString("u13", binding.txU.text.toString())
                    putString("uMarco", binding.uxxtxtNfcfi.text.toString())
                    putString("tubo", binding.txT.text.toString())
                    putString("riel", binding.txR.text.toString())
                    putString("uFel", binding.txUf.text.toString())
                    putString("fCo", binding.txFc.text.toString())
                    putString("porta", binding.txPf.text.toString())
                    putString("angT", binding.txTo.text.toString())
                    putString("hache", binding.txH.text.toString())
                    putString("vidrios", binding.txV.text.toString())
                    putString("u", binding.ueditxtNfcfi.text.toString())
                    putString("div", divisiones().toString())
                    putString("ref", binding.referenciasNfcfi.text.toString())
                    putString("si", siNoMoch().toString())

                }
                val intent = Intent(this, FichaActivity::class.java)
                intent.putExtras(paquete)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Olvidaste ingresar datos", Toast.LENGTH_LONG).show()
            }
        }
        binding.btArchivar.setOnClickListener {
            archivarMapas()
        }
        binding.btArchivar.setOnLongClickListener {
            // Llamar a la función para guardar el Map
            MapStorage.guardarMap(this, mapListas)

            // Mostrar un mensaje de confirmación
            Toast.makeText(this, "Map guardado correctamente", Toast.LENGTH_SHORT).show()

            true // Retorna true para indicar que el evento fue manejado
        }
        binding.f3.setOnClickListener {

            startActivity(Intent(this, FichaActivity::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun cliente(){

        binding.lyCliente.visibility=View.GONE

        val paqueteR=intent.extras
        var cliente= paqueteR?.getString("rcliente")
        if (cliente!=null) {binding.tvId.text= "nova inaparente $cliente"}
        else  {binding.tvId.text= "nova inaparente"}

        binding.tvId.setOnClickListener {
            binding.lyCliente.visibility=View.VISIBLE
            binding.clienteEditxt.setText("$cliente")

            binding.btGo.setOnClickListener {
                cliente = binding.clienteEditxt.text.toString()
                binding.tvId.text="nova inaparente $cliente"

                binding.lyCliente.visibility=View.GONE }
        }
    }

    //FUNCIONES CLICKS
    private fun calcular() {
        binding.btnCalcularNfcfs.setOnClickListener {
            try {

                //VISIBILIDAD MODELOS
                uVisible()
                aVisible()
                dVisible()

                u13()
                u38()
                uOtros()
                otrosAluminios()
                vidrios()
                referencias()

                //binding.txPr.text = f15()

            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //FUNCIONES PUBLICADAS
    private fun puntosU(): String {
        val partes = uFijos()
        val cruce = cruce()
        val punto1 = df1((partes * 2) - cruce * 2).toFloat()
        val punto2 = df1((partes * 4) - cruce * 4).toFloat()
        val punto3 = df1((partes * 6) - cruce * 6).toFloat()
        return when (divisiones()) {
            5, 6 -> df1(((partes * 2) - cruce * 2))
            8, 12 -> df1(((partes * 3) - cruce * 2))
            7, 10, 14 -> "${df1(punto1)}_${df1(punto2)}"
            9, 11, 13, 15 -> "${df1(punto1)}_${df1(punto2)}_${df1(punto3)}"
            else -> {
                ""
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun referencias() {
        val ancho = binding.med1Nfcfi.text.toString().toFloat()
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        binding.referenciasNfcfi.text = "An: ${df1(ancho)}  x  Al: ${df1(alto)}\n" +
                "Altura de puente:${
                    if (alto > hoja) {
                        df1(altoHoja())
                    } else {
                        "sin puente"
                    }
                }\n" +
                "Divisiones: ${divisiones()} -> Fs: ${nFijos()};Cs: ${nCorredizas()}" +
                if (divisiones() > 4) {
                    "\nPuntos: ${puntosU()}"
                } else {
                    ""
                }
    }
    private fun u13() {
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val uFijos = df1(uFijos()).toFloat()
        val uParante = df1(uParante()).toFloat()
        val uParante2 = df1(uParante2()).toFloat()
        val uSuperior = df1(uSuperior()).toFloat()

        if (alto > hoja) {
            binding.txU.text = when {
                divisiones() == 2 -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uParante)} = ${fijoUParante()}\n" +
                            "${df1(uParante2)} = 1\n" +
                            "${df1(uSuperior)} = 1"
                }
                divisiones() == 1 -> {
                    "${df1(uFijos)} = 2\n" +
                            "${df1(uParante)} =2"
                }
                else -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uParante)} = ${fijoUParante()}\n" +
                            "${df1(uSuperior)} = 1"
                }
            }
        } else {
            binding.txU.text = when {
                divisiones() == 2 -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uParante)} = ${fijoUParante()}"
                }
                divisiones() == 1 -> {
                    "${df1(uFijos)} = 2\n" +
                            "${df1(uParante)} =2"
                }
                else -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uParante)} = ${fijoUParante()}"
                }
            }
        }
    }
    private fun u38() {
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val uFijos = df1(uFijos()).toFloat()
        val uParante = df1(uParante()).toFloat()
        val uParante2 = df1(uParante2()).toFloat()
        val uSuperior = df1(uSuperior()).toFloat()

        if (alto > hoja) {
            binding.u38txtNfcfi.text = when {
                divisiones() == 2 -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uParante)} = ${fijoUParante()}\n" +
                            "${df1(uParante2)} = 1\n" +
                            "${df1(uSuperior)} = 1"
                }
                divisiones() == 1 -> {
                    "${df1(uFijos)} = 2\n" +
                            "${df1(uParante)} =2"
                }
                else -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uParante)} = ${fijoUParante()}\n" +
                            "${df1(uSuperior)} = 1"
                }
            }
        } else {
            binding.u38txtNfcfi.text = when {
                divisiones() == 2 -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uParante)} = ${fijoUParante()}"
                }
                divisiones() == 1 -> {
                    "${df1(uFijos)} = 2\n" +
                            "${df1(uParante)} =2"
                }
                else -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uParante)} = ${fijoUParante()}"
                }
            }
        }
    }
    private fun uOtros() {
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val us = binding.ueditxtNfcfi.text.toString().toFloat()
        val uFijos = df1(uFijos()).toFloat()
        val uParante = df1(uParante()).toFloat()
        val uParante2 = df1(uParante2()).toFloat()
        val uSuperior = df1(uSuperior()).toFloat()

        if (alto > hoja && us != 0F) {
            binding.uxxtxtNfcfi.text = when {
                divisiones() == 2 -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uParante)} = ${fijoUParante()}\n" +
                            "${df1(uParante2)} = 1\n" +
                            "${df1(uSuperior)} = 1"
                }
                divisiones() == 1 -> {
                    "${df1(uFijos)} = 2\n" +
                            "${df1(uParante)} =2"
                }
                else -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uParante)} = ${fijoUParante()}\n" +
                            "${df1(uSuperior)} = 1"
                }
            }
        } else if (alto > hoja && us == 0F) {
            binding.uxxtxtNfcfi.text = when {
                divisiones() == 2 -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uSuperior)} = 1"
                }
                divisiones() == 1 -> {
                    "${df1(uFijos)} = 2"
                }
                else -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uSuperior)} = 1"
                }
            }
        } else if (alto <= hoja && us != 0F) {
            binding.uxxtxtNfcfi.text = when {
                divisiones() == 2 -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uParante)} = ${fijoUParante()}\n" +
                            "${df1(uParante2)} = 1"
                }
                divisiones() == 1 -> {
                    "${df1(uFijos)} = 2\n${df1(uParante())} =2"
                }
                else -> {
                    "${df1(uFijos)} = ${nFijos()}\n" +
                            "${df1(uParante)} = ${fijoUParante()}"
                }
            }
        } else {
            binding.uxxtxtNfcfi.text = when {
                divisiones() == 2 -> {
                    "${df1(uFijos)} = ${nFijos()}"
                }
                divisiones() == 1 -> {
                    "${df1(uFijos)} = 2"
                }
                else -> {
                    "${df1(uFijos)} = ${nFijos()}"
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun otrosAluminios() {
        // PUENTE
        binding.txT.text = puentes()

        // RIEL
        binding.txR.text = if (divisiones() == 1) {
            ""
        } else {
            rieles()
        }

        // U FELPERO, FIJO COREDIZO
        binding.txUf.text = if (divisiones() == 1) {
            ""
        } else {
            rieles()
        }

        binding.txR.text = if (divisiones() == 1) {
            ""
        } else {
            rieles()
        }

        // HACHE
        binding.txH.text = "${df1(hache())} = ${nCorredizas()}"

        //ÁNGULO TOPE
        if (divisiones() == 2) {
            binding.txTo.text = "${df1(altoHoja() - 0.9f)} = 1"
        } else {
            binding.lyTo.visibility = View.GONE
        }

        // PORTAFELPA
        binding.txPf.text = "${df1(portafelpa())} = ${divDePortas()}"
    }
    private fun vidrios() {
        binding.txV.text = if (divisiones() > 1) {
            "${vidrioFijo()}\n${vidrioCorre()}\n${vidrioMocheta()}"
        } else {
            vidrioFijo()
        }
    }

    //FUNCIONES VISIBILIDAD
    private fun uVisible() {
        when (binding.ueditxtNfcfi.text.toString().toFloat()) {
            1f -> {
                binding.lyU.visibility = View.GONE
                binding.u38layout.visibility = View.VISIBLE
                binding.ulayout.visibility = View.GONE
            }
            1.5f -> {
                binding.lyU.visibility = View.VISIBLE
                binding.u38layout.visibility = View.GONE
                binding.ulayout.visibility = View.GONE
            }
            else -> {
                binding.lyU.visibility = View.GONE
                binding.u38layout.visibility = View.GONE
                binding.ulayout.visibility = View.VISIBLE
            }
        }
    }
    private fun aVisible() {
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()

        //OPCIONES DE VISIBILIDAD

        if (divisiones() == 1) {
            binding.lyH.visibility = View.GONE
        } else {
            binding.lyH.visibility = View.VISIBLE
        }

        if (divisiones() != 2) {
            binding.lyTo.visibility = View.GONE
        } else {
            binding.lyTo.visibility = View.VISIBLE
        }

        if (divisiones() == 1) {
            binding.lyPf.visibility = View.GONE
        } else {
            binding.lyPf.visibility = View.VISIBLE
        }

        if (divisiones() == 1 || alto <= hoja) {
            binding.lyUf.visibility = View.GONE
        } else {
            binding.lyUf.visibility = View.VISIBLE
        }

        if (alto <= hoja && divisiones() > 1) {
            binding.lyFijoCorre.visibility = View.VISIBLE
        } else {
            binding.lyFijoCorre.visibility = View.GONE
        }

        if (alto <= hoja) {
            binding.lyTubo.visibility = View.GONE
        } else {
            binding.lyTubo.visibility = View.VISIBLE
        }
    }
    private fun dVisible() {
        val alto = divisiones().toString()
        val hoja = if (siNoMoch()==1){""}else{"c"}.toString()
        val div = "$alto$hoja"
        binding.f3.setImageResource(when(div){
            "1"-> R.drawable.ic_fichad1   "1c"-> R.drawable.ic_fichad1
            "2"-> R.drawable.ic_fichad2   "2c"-> R.drawable.ic_fichad2c
            "3"-> R.drawable.ic_fichad3   "3c"-> R.drawable.ic_fichad3c
            "4"-> R.drawable.ic_fichad4   "4c"-> R.drawable.ic_fichad4c
            "5"-> R.drawable.ic_fichad5   "5c"-> R.drawable.ic_fichad5c
            "6"-> R.drawable.ic_fichad6   "6c"-> R.drawable.ic_fichad6c
            "7"-> R.drawable.ic_fichad7   "7c"-> R.drawable.ic_fichad7c
            "8"-> R.drawable.ic_fichad8   "8c"-> R.drawable.ic_fichad8c
            "9"-> R.drawable.ic_fichad9   "9c"-> R.drawable.ic_fichad9c
            "10"-> R.drawable.ic_fichad10 "10c"-> R.drawable.ic_fichad10c
            "11"-> R.drawable.ic_fichad11 "11c"-> R.drawable.ic_fichad11c
            "12"-> R.drawable.ic_fichad12 "12c"-> R.drawable.ic_fichad12c
            "13"-> R.drawable.ic_fichad13 "13c"-> R.drawable.ic_fichad13c
            "14"-> R.drawable.ic_fichad14 "14c"-> R.drawable.ic_fichad14c
            "15"-> R.drawable.ic_fichad15 "15c"-> R.drawable.ic_fichad15c
            else -> {R.drawable.ic_fichad5}
        })

    }

    // FUNCIONES REDONDEOS
    private fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    // FUNCIONES U
    private fun uFijos(): Float {
        val ancho = binding.med1Nfcfi.text.toString().toFloat()
        val cruce = when (divisiones()) {
            2, 3, 5, 7, 9, 11, 13, 15 -> divisiones() - 1
            4, 6, 10 -> divisiones() - 2
            8, 12 -> divisiones() / 2
            14 -> divisiones() - 4
            else -> divisiones() - 1
        } * cruce()
        val partes = (ancho + cruce) / divisiones()
        return if (divisiones() == 1) {
            ancho
        } else {
            partes
        }
    }
    private fun uParante(): Float {
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val us = binding.ueditxtNfcfi.text.toString().toFloat()
        return alto - (2 * us)
    }
    private fun uParante2(): Float {
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val us = binding.ueditxtNfcfi.text.toString().toFloat()
        return ((alto - altoHoja()) - us) + 1.5f
    }
    private fun uSuperior(): Float {
        return binding.med1Nfcfi.text.toString().toFloat()
    }

    // FUNCIONES OTROS PERFILES
    private fun rieles(): String {
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val ancho = binding.med1Nfcfi.text.toString().toFloat()
        val mPuentes6 = df1(mPuentes() - 0.06f).toFloat()
        val mPuentes = df1(mPuentes()).toFloat()
        val mPuentes2 = df1(mPuentes2()).toFloat()
        val ancho6 = df1(ancho - 0.06f).toFloat()

        return if (alto >= hoja) if (divisiones() != 14) {
            "${df1(mPuentes6)} = ${nPuentes()}"
        } else {
            "${df1(mPuentes)} = ${nPuentes() - 1}\n" +
                    "${df1(mPuentes2)} = ${nPuentes() - 2}"
        }
        else {
            "${df1(ancho6)} = 1"
        }
    }
    private fun puentes(): String {
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val mPuentes6 = df1(mPuentes() - 0.06f).toFloat()
        val mPuentes2 = df1(mPuentes2()).toFloat()
        return when {
            divisiones() in 6..12 && divisiones() % 2 == 0 -> {
                "${df1(mPuentes6)} = ${nPuentes()}\n" +
                        "${df1(alto)} = ${nPuentes() - 1}"
            }
            divisiones() == 14 -> {
                "${df1(mPuentes6)} = ${nPuentes() - 1}\n" +
                        "${df1(mPuentes2)} = ${nPuentes() - 2}\n" +
                        "${df1(alto)} = ${nPuentes() - 1}"
            }
            else -> {
                "${df1(mPuentes6)} = ${nPuentes()}"
            }
        }
    }
    private fun portafelpa(): Float {
        return df1(altoHoja() - 1.6f).toFloat()
    }
    private fun hache(): Float {
        return df1(uFijos()).toFloat()
    }

    //FUNCI0NES VIDRIOS
    private fun vidrioFijo(): String {
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val us = binding.ueditxtNfcfi.text.toString().toFloat()
        val holgura = if (us == 0f) {
            1f
        } else {
            0.2f
        }
        val uFijos = df1(uFijos()).toFloat()
        val uFijos4 = df1(uFijos() - 0.4f).toFloat()
        val uFijos2 = df1(uFijos() - 0.2f).toFloat()
        val altDes = df1(alto - (us + holgura)).toFloat()


        return when {
            divisiones() < 5 -> {
                "${df1(uFijos4)} x ${df1(altDes)} = ${nFijos()}"
            }
            divisiones() == 6 || divisiones() == 8 -> {
                "${df1(uFijos4)} x ${df1(altDes)} = 2\n" +
                        "${df1(uFijos2)} x ${df1(altDes)} = 2"
            }
            divisiones() == 10 -> {
                "${df1(uFijos4)} x ${df1(altDes)} = 2\n" +
                        "${df1(uFijos2)} x ${df1(altDes)} = 2\n" +
                        "${df1(uFijos)} x ${df1(altDes)} = 2"
            }
            divisiones() == 12 -> {
                "${df1(uFijos4)} x ${df1(altDes)} = 2\n" +
                        "${df1(uFijos2)} x ${df1(altDes)} = 4"
            }
            divisiones() == 14 -> {
                "${df1(uFijos4)} x ${df1(altDes)} = 2\n" +
                        "${df1(uFijos2)} x ${df1(altDes)} = 4\n" +
                        "${df1(uFijos)} x ${df1(altDes)} = 2"
            }

            else -> {
                "${df1(uFijos4)} x ${df1(altDes)} = 2\n" +
                        "${df1(uFijos)} x ${df1(altDes)}= ${nFijos() - 2}"
            }
        }
    }
    private fun vidrioCorre(): String {
        val ancho = df1(hache() - 1.4f).toFloat()
        val alto = df1(altoHoja() - 3.5f).toFloat()
        return "${df1(ancho)} x ${df1(alto)} = ${nCorredizas()}"
    }
    private fun vidrioMocheta(): String {
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val ancho = binding.med1Nfcfi.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val mas1 = df1((alto - altoHoja()) + 1).toFloat()
        val axnfxuf = df1(((ancho - (nFijos() * uFijos()))) - 0.6f).toFloat()
        val axnfxuf2 = df1(((ancho - (nFijos() * uFijos())) / 2) - 0.6f).toFloat()
        val axnfxuf3 = df1(((ancho - (nFijos() * uFijos())) / 3) - 0.6f).toFloat()
        val axnfxufn = df1(((ancho - (nFijos() * uFijos())) / nCorredizas()) - 0.6f).toFloat()

        return when {
            divisiones() <= 1 || alto <= hoja -> {
                ""
            }
            divisiones() == 4 -> {
                "${df1(mas1)} x ${df1(axnfxuf)} = 1"
            }
            divisiones() == 8 -> {
                "${df1(mas1)} x ${df1(axnfxuf2)} = 2"
            }
            divisiones() == 12 -> {
                "${df1(mas1)} x ${df1(axnfxuf3)} = 3"
            }
            divisiones() == 14 -> {
                "${df1(mas1)} x ${df1(axnfxuf2)} = 1\n" +
                        "${df1(mas1)} x ${df1(axnfxuf)} = 4"
            }
            else -> {
                "${df1(mas1)} x ${df1(axnfxufn)} = ${(nCorredizas())}"
            }
        }
    }

    //FUNCIONES DE ARCHIVO
    private fun archivarMapas() {
        ListaCasilla.incrementarContadorVentanas()

        // Caso especial para txReferencias
       /* if (esValido(binding.lyReferencias)) {
            ListaCasilla.procesarReferencias(binding.tvReferencias, binding.txReferencias, mapListas) // referencias
        }*/
        // Usar la clase ListaCasilla para procesar y archivar solo los TextView válidos
        if (esValido(binding.lyU)) {
            ListaCasilla.procesarArchivar(binding.tvU, binding.txU, mapListas) // u
        }
       if (esValido(binding.lyUf)){
           ListaCasilla.procesarArchivar(binding.tvUf,binding.txUf,mapListas) // u felpero
       }
        if (esValido(binding.lyFijoCorre)) {
            ListaCasilla.procesarArchivar(binding.tvFc, binding.txFc, mapListas) // fijo corredizo
        }
        if (esValido(binding.lyRiel)) {
            ListaCasilla.procesarArchivar(binding.tvR, binding.txR, mapListas) // riel
        }
        if (esValido(binding.lyTubo)) {
            ListaCasilla.procesarArchivar(binding.tvT, binding.txT, mapListas) // tubo
        }
        if (esValido(binding.lyPf)) {
            ListaCasilla.procesarArchivar(binding.tvPf, binding.txPf, mapListas) // portafelpa
        }
        
        if (esValido(binding.lyTo)) {
            ListaCasilla.procesarArchivar(binding.tvTo, binding.txTo, mapListas) // tope
        }
        if (esValido(binding.lyH)) {
            ListaCasilla.procesarArchivar(binding.tvH, binding.txH, mapListas) // h
        }
        if (esValido(binding.lyVidrios)) {
            ListaCasilla.procesarArchivar(binding.tvV, binding.txV, mapListas) // vidrios
        }
        if (esValido(binding.lyClient)) {
            ListaCasilla.procesarArchivar(binding.tvC, binding.txC, mapListas) // cliente
        }
        if (esValido(binding.lyAncho)) {
            ListaCasilla.procesarArchivar(binding.tvAncho, binding.txAncho, mapListas) // ancho
        }
        if (esValido(binding.lyAlto)) {
            ListaCasilla.procesarArchivar(binding.tvAlto, binding.txAlto, mapListas) // alto
        }
        if (esValido(binding.lyPuente)) {
            ListaCasilla.procesarArchivar(binding.tvPuente, binding.txPuente, mapListas) // altura Puente
        }
        if (esValido(binding.lyDivisiones)) {
            ListaCasilla.procesarArchivar(binding.tvDivisiones, binding.txDivisiones, mapListas) // divisiones
        }
        if (esValido(binding.lyFijos)) {
            ListaCasilla.procesarArchivar(binding.tvFijos, binding.txFijos, mapListas) // nFijos
        }
        if (esValido(binding.lyCorredizas)) {
            ListaCasilla.procesarArchivar(binding.tvCorredizas, binding.txCorredizas, mapListas) // nCorredizas
        }
        if (esValido(binding.lyDiseno)) {
            ListaCasilla.procesarArchivar(binding.tvDiseno, binding.txDiseno, mapListas) // diseño
        }
        if (esValido(binding.lyGrados)) {
            ListaCasilla.procesarArchivar(binding.tvGrados, binding.txGrados, mapListas) // grados
        }
        if(esValido(binding.lyTipo)){
            ListaCasilla.procesarArchivar(binding.tvTipo,binding.txTipo,mapListas) // tipo de ventana
        }

        // Aquí puedes hacer algo con `mapListas`, como mostrarlo o guardarlo
        binding.txPr.text = mapListas.toString()
        println(mapListas)
    }
    // Función para verificar si un Layout es visible o tiene estado GONE
    private fun esValido(ly: LinearLayout): Boolean {
        return ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
    }

    //FUNCIONES GENERALES
    private fun altoHoja(): Float {
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        val corre = if (hoja > alto) {
            alto
        } else {
            hoja
        }
        return if (hoja == 0f) {
            alto / 7 * 5
        } else {
            corre
        }
    }
    private fun siNoMoch(): Int {
        val alto = binding.med2Nfcfi.text.toString().toFloat()
        val hoja = binding.hojatxtNfcfsi.text.toString().toFloat()
        return if (hoja >= alto) {0} else {1}
    }
    private fun divisiones(): Int {
        val ancho = binding.med1Nfcfi.text.toString().toFloat()
        val divis = binding.partesNfcfi.text.toString().toInt()
        return if (divis == 0) {
            when {
                ancho <= 60 -> 1
                ancho in 60.0..120.0 -> 2
                ancho in 120.0..180.0 -> 3
                ancho in 180.0..240.0 -> 4
                ancho in 240.0..300.0 -> 5
                ancho in 300.0..360.0 -> 6
                ancho in 360.0..420.0 -> 7
                ancho in 420.0..480.0 -> 8
                ancho in 480.0..540.0 -> 9
                ancho in 540.0..600.0 -> 10
                ancho in 600.0..660.0 -> 11
                ancho in 660.0..720.0 -> 12
                ancho in 720.0..780.0 -> 13
                ancho in 780.0..840.0 -> 14
                ancho in 840.0..900.0 -> 15
                else -> divis
            }
        } else {
            divis
        }
    }
    private fun nFijos(): Int {
        return when (divisiones()) {
            1 -> 1
            2 -> 1
            3 -> 2
            4 -> 2
            5 -> 3
            6 -> 4
            7 -> 4
            8 -> 4
            9 -> 5
            10 -> 6
            11 -> 6
            12 -> 6
            13 -> 7
            14 -> 8
            15 -> 8
            else -> 0
        }
    }
    private fun nCorredizas(): Int {
        return when (divisiones()) {
            1 -> 0
            2 -> 1
            3 -> 1
            4 -> 2
            5 -> 2
            6 -> 2
            7 -> 3
            8 -> 4
            9 -> 4
            10 -> 4
            11 -> 5
            12 -> 6
            13 -> 6
            14 -> 6
            15 -> 7
            else -> 0
        }
    }
    private fun fijoUParante(): Int {
        return when (divisiones()) {
            1 -> 2
            2 -> 1
            in 3..15 -> 2
            else -> 0
        }
    }
    private fun nPuentes(): Int {
        return when (divisiones()) {
            1 -> 1
            2 -> 1
            3 -> 1
            4 -> 1
            5 -> 1
            7 -> 1
            9 -> 1
            11 -> 1
            13 -> 1
            15 -> 1
            6 -> 2
            8 -> 2
            10 -> 2
            12 -> 3
            14 -> 3
            else -> 0
        }
    }
    private fun mPuentes(): Float {
        val ancho = binding.med1Nfcfi.text.toString().toFloat()
        val parantes = 2.5f
        return when (divisiones()) {
            1 -> ancho
            2 -> ancho
            3 -> ancho
            4 -> ancho
            5 -> ancho
            7 -> ancho
            9 -> ancho
            11 -> ancho
            13 -> ancho
            15 -> ancho
            6 -> (ancho - parantes) / 2
            8 -> (ancho - parantes) / 2
            10 -> (ancho - parantes) / 2
            12 -> (ancho - (2 * parantes)) / 3
            14 -> (ancho - (2 * parantes)) / divisiones() * 5
            else -> 0f
        }
    }
    private fun mPuentes2(): Float {
        val ancho = binding.med1Nfcfi.text.toString().toFloat()
        val parantes = 2.5f
        return when (divisiones()) {
            14 -> (ancho - (2 * parantes)) / divisiones() * 4
            else -> 0f
        }
    }
    private fun divDePortas(): String {
        return when (divisiones()) {
            1 -> ""
            2, 4, 8, 12 -> "${nCorredizas() * 3}"
            14 -> "${(nCorredizas() * 4) - 2}"
            else -> "${nCorredizas() * 4}"
        }
    }
    private fun cruce(): Float {
        val exacto = binding.etCruce.text.toString().toFloat()
        val cruce = if (divisiones() == 4 || divisiones() == 8 || divisiones() > 12) {
            0.8f
        } else {
            0.7f
        }
        return if (exacto == 0f) {
            cruce
        } else {
            exacto
        }
    }

    //FUNCIONES ACCESORIOS
    private fun f15(): String {
        val mF = df1(portafelpa()).toFloat()
        val nF = divDePortas().toFloat()
        val xF = "$mF = $nF"
        val totalF = df1(mF * nF)
        return "$xF\n$totalF"
    }
    private fun f10(): String {
        val mF = rieles().toFloat()
        val nF = rieles().toFloat()
        val xf = "$mF=${df1(nF * 2)}"
        val totalF = df1(xf.toFloat() * nF)
        return "$xf\n$totalF"
    }
    private fun tarugos() {
    }
}
