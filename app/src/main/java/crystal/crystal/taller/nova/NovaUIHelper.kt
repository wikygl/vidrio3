package crystal.crystal.taller.nova

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import crystal.crystal.R
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager

/**
 * Clase helper para funciones de UI comunes entre NovaApa y NovaIna
 * Mantiene la lÃ³gica original pero centralizada
 */
object NovaUIHelper {

    // ==================== FUNCIÃ“N DE VALIDACIÃ“N ====================

    fun esValido(ly: LinearLayout): Boolean {
        return ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
    }

    // ==================== FUNCIONES DE CLIENTE ====================

    @SuppressLint("SetTextI18n")
    fun configurarCliente(
        intent: Intent,
        lyCliente: LinearLayout,
        tvTitulo: android.widget.TextView,
        clienteEditxt: android.widget.EditText,
        btGo: android.widget.Button,
        tipoVentana: String = "nova aparente"
    ) {
        lyCliente.visibility = View.GONE
        val paqueteR = intent.extras
        var cliente = paqueteR?.getString("rcliente")

        val tituloFinal = if (cliente != null) {
            "$tipoVentana $cliente"
        } else {
            tipoVentana
        }

        tvTitulo.text = tituloFinal

        tvTitulo.setOnClickListener {
            lyCliente.visibility = View.VISIBLE
            clienteEditxt.setText(cliente ?: "")

            btGo.setOnClickListener {
                cliente = clienteEditxt.text.toString()
                tvTitulo.text = "$tipoVentana $cliente"
                lyCliente.visibility = View.GONE
            }
        }
    }

    // ==================== FUNCIONES DE DISEÃ‘O ====================

    fun obtenerRecursoDiseno(divisiones: Int, siNoMoch: Int, tipoVentana: String = "apa", prefijo: String = "ic_fichad"): Int {
        val nombreRecurso = obtenerNombreDiseno(divisiones, siNoMoch, tipoVentana, prefijo)

        return when (nombreRecurso) {
            // NovaApa (aparente) - con sufijo "a" cuando hay mocheta
            "ic_fichad1a" -> R.drawable.ic_fichad1a
            "ic_fichad2a" -> R.drawable.ic_fichad2a
            "ic_fichad3a" -> R.drawable.ic_fichad3a
            "ic_fichad4a" -> R.drawable.ic_fichad4a
            "ic_fichad5a" -> R.drawable.ic_fichad5a
            "ic_fichad6a" -> R.drawable.ic_fichad6a
            "ic_fichad7a" -> R.drawable.ic_fichad7a
            "ic_fichad8a" -> R.drawable.ic_fichad8a
            "ic_fichad9a" -> R.drawable.ic_fichad9a
            "ic_fichad10a" -> R.drawable.ic_fichad10a
            "ic_fichad11a" -> R.drawable.ic_fichad11a
            "ic_fichad12a" -> R.drawable.ic_fichad12a
            "ic_fichad13a" -> R.drawable.ic_fichad13a
            "ic_fichad14a" -> R.drawable.ic_fichad14a
            "ic_fichad15a" -> R.drawable.ic_fichad15a

            // NovaIna (inaparente) - sin sufijo cuando hay mocheta
            "ic_fichad1" -> R.drawable.ic_fichad1
            "ic_fichad2" -> R.drawable.ic_fichad2
            "ic_fichad3" -> R.drawable.ic_fichad3
            "ic_fichad4" -> R.drawable.ic_fichad4
            "ic_fichad5" -> R.drawable.ic_fichad5
            "ic_fichad6" -> R.drawable.ic_fichad6
            "ic_fichad7" -> R.drawable.ic_fichad7
            "ic_fichad8" -> R.drawable.ic_fichad8
            "ic_fichad9" -> R.drawable.ic_fichad9
            "ic_fichad10" -> R.drawable.ic_fichad10
            "ic_fichad11" -> R.drawable.ic_fichad11
            "ic_fichad12" -> R.drawable.ic_fichad12
            "ic_fichad13" -> R.drawable.ic_fichad13
            "ic_fichad14" -> R.drawable.ic_fichad14
            "ic_fichad15" -> R.drawable.ic_fichad15

            // Ambos - con sufijo "c" cuando NO hay mocheta
            "ic_fichad1c" -> R.drawable.ic_fichad1
            "ic_fichad2c" -> R.drawable.ic_fichad2c
            "ic_fichad3c" -> R.drawable.ic_fichad3c
            "ic_fichad4c" -> R.drawable.ic_fichad4c
            "ic_fichad5c" -> R.drawable.ic_fichad5c
            "ic_fichad6c" -> R.drawable.ic_fichad6c
            "ic_fichad7c" -> R.drawable.ic_fichad7c
            "ic_fichad8c" -> R.drawable.ic_fichad8c
            "ic_fichad9c" -> R.drawable.ic_fichad9c
            "ic_fichad10c" -> R.drawable.ic_fichad10c
            "ic_fichad11c" -> R.drawable.ic_fichad11c
            "ic_fichad12c" -> R.drawable.ic_fichad12c
            "ic_fichad13c" -> R.drawable.ic_fichad13c
            "ic_fichad14c" -> R.drawable.ic_fichad14c
            "ic_fichad15c" -> R.drawable.ic_fichad15c

            else -> R.drawable.ic_fichad5
        }
    }

    fun obtenerNombreDiseno(divisiones: Int, siNoMoch: Int, tipoVentana: String = "apa", prefijo: String = "ic_fichad"): String {
        // LÃ“GICA ORIGINAL: siNoMoch = 1 â†’ sin sufijo, siNoMoch = 0 â†’ sufijo "c"
        // Esto es igual para AMBAS ventanas (NovaApa y NovaIna)

        return when (siNoMoch) {
            1 -> {
                // Con mocheta - segÃºn cÃ³digo original
                when (tipoVentana) {
                    "apa" -> "$prefijo${divisiones}a"  // NovaApa: ic_fichad3a
                    "ina" -> "$prefijo$divisiones"     // NovaIna: ic_fichad3
                    else -> "$prefijo$divisiones"
                }
            }
            0 -> {
                // Sin mocheta - igual para ambas
                "$prefijo${divisiones}c"  // Ambas: ic_fichad3c
            }
            else -> "$prefijo$divisiones"
        }
    }

    // ==================== FUNCIÃ“N DE PROCESAMIENTO DE PROYECTO ====================

    fun procesarIntentProyecto(
        context: Context,
        intent: Intent,
        onProyectoActivado: (String) -> Unit
    ) {
        val nombreProyecto = intent.getStringExtra("proyecto_nombre")
        val crearNuevo = intent.getBooleanExtra("crear_proyecto", false)
        val descripcionProyecto = intent.getStringExtra("proyecto_descripcion") ?: ""

        if (crearNuevo && !nombreProyecto.isNullOrEmpty()) {
            if (MapStorage.crearProyecto(context, nombreProyecto, descripcionProyecto)) {
                ProyectoManager.setProyectoActivo(context, nombreProyecto)
                onProyectoActivado(nombreProyecto)
                Toast.makeText(context, "Proyecto '$nombreProyecto' creado y activado", Toast.LENGTH_SHORT).show()
            }
        } else if (!nombreProyecto.isNullOrEmpty()) {
            if (MapStorage.existeProyecto(context, nombreProyecto)) {
                ProyectoManager.setProyectoActivo(context, nombreProyecto)
                onProyectoActivado(nombreProyecto)
                Toast.makeText(context, "Proyecto '$nombreProyecto' activado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ==================== FUNCIÃ“N DE REFERENCIAS ====================

    @SuppressLint("SetTextI18n")
    fun generarReferencias(
        ancho: Float,
        alto: Float,
        altoHoja: Float,
        divisiones: Int,
        nFijos: Int,
        nCorredizas: Int,
        siNoMoch: Int,
        puntosU: String = ""
    ): String {
        val altoPuenteTexto = if (siNoMoch == 1) {
            NovaCalculos.df1(altoHoja)
        } else {
            "sin puente"
        }

        val referenciasBase = "An: ${NovaCalculos.df1(ancho)}  x  Al: ${NovaCalculos.df1(alto)}\n" +
                "Altura de puente: $altoPuenteTexto\n" +
                "Divisiones: $divisiones -> fjs: $nFijos;czs: $nCorredizas"

        return if (divisiones > 4 && puntosU.isNotEmpty()) {
            "$referenciasBase\nPuntos: $puntosU"
        } else {
            referenciasBase
        }
    }
//{nova,apa,[150,120:m<30.6>(fccf);s<80>(fcf);m<9.4>(cfc)}]

    fun generarDiseno(ancho: Float, alto: Float, altoHoja: Float,
                      divisiones: Int, siNoMoch: Int, texto: String,anchMota:Int): String {

       val mo = NovaCalculos.altoMocheta(alto, altoHoja, tubo = 2.5f)
       val moDos = (alto - altoHoja)/2

        val altoPuenteTexto = if (siNoMoch == 1) {
        NovaCalculos.df1(altoHoja)
        } else {""}

        val referenciasBase =
            when(texto){
                "nn" -> "${NovaCalculos.df1(ancho)},${NovaCalculos.df1(alto)}:" +
                        "s<$altoPuenteTexto>(${NovaCalculos.ordenDivis(divisiones, ancho)});m<${NovaCalculos.df1(mo)}>" +
                        "(${NovaCalculos.ordenMochetas(anchMota, ancho)})"
                "nr" -> "${NovaCalculos.df1(ancho)},${NovaCalculos.df1(alto)}:" +
                        "m<${NovaCalculos.df1(mo)}>(f);s<${NovaCalculos.df1(altoHoja)}>(${NovaCalculos.ordenDivis(divisiones,ancho)})"
                "np" -> "${NovaCalculos.df1(ancho)},${NovaCalculos.df1(alto)}:" +
                        "m<${NovaCalculos.df1(moDos)}>(f);s<${NovaCalculos.df1(altoHoja)}>(${NovaCalculos.ordenDivis(divisiones,ancho)});" +
                        "m<${NovaCalculos.df1(moDos)}>(f)"
                "ncc" -> "${NovaCalculos.df1(ancho)},${NovaCalculos.df1(alto)}:" +
                        "s<$altoPuenteTexto>(cc);m<${NovaCalculos.df1(mo)}>(f)"
                "n3c" -> "${NovaCalculos.df1(ancho)},${NovaCalculos.df1(alto)}:" +
                        "s<$altoPuenteTexto>(ccc);m<${NovaCalculos.df1(mo)}>(f)"
                "ncfc" -> "${NovaCalculos.df1(ancho)},${NovaCalculos.df1(alto)}:" +
                        "s<$altoPuenteTexto>(cfc);m<${NovaCalculos.df1(mo)}>(f)"
                "nl" -> "150,150:m<25>(f<100>f<50>);s<100>(fcf);m<25>(cfc)"
                "nu" -> "150,120:m<30.6>(fccf);s<80>(fcf);m<9.4>(cfc)"
                "ns" -> "150,120:m<30.6>(fccf);s<80>(fcf);m<9.4>(cfc)"
                "ncu" ->"150,120:m<30.6>(fccf);s<80>(fcf);m<9.4>(cfc)"
                "nci" ->"150,120:m<30.6>(fccf);s<80>(fcf);m<9.4>(cfc)"

                else -> {""}
            }
                return referenciasBase

    }


    fun mostrarSpinner(lySpinner: View) {
        lySpinner.alpha = 0f
        lySpinner.visibility = View.VISIBLE
        lySpinner.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }

    fun ocultarSpinner(lySpinner: View) {
        lySpinner.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                lySpinner.visibility = View.GONE
            }
            .start()
    }

    // ==================== FUNCIÃ“N PARA ROTACIÃ“N DE DISEÃ‘O ====================

    fun rotarDiseno(
        ivDiseno: android.widget.ImageView,
        gradosActuales: Int,
        onGradosActualizados: (Int) -> Unit
    ) {
        ivDiseno.animate().rotationBy(180f).setDuration(500).start()
        val nuevosGrados = (gradosActuales + 180) % 360
        onGradosActualizados(nuevosGrados)
    }}