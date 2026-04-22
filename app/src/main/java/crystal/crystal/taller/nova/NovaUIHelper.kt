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

    // ==================== FUNCIÓN DE VALIDACIÓN ====================

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
        puntosU: String = "",
        modelo: String = "nn",
        alturaPuente: Float = 2.5f
    ): String {
        val altoPuenteTexto = if (siNoMoch == 1) {
            NovaCalculos.df1(altoHoja)
        } else {
            "sin puente"
        }

        val (mochetaInf, mochetaSup) = if (siNoMoch == 0) {
            0f to 0f
        } else {
            when (modelo) {
                "nr" -> 0f to (alto - altoHoja).coerceAtLeast(0f)
                "np" -> {
                    val alturas = NovaInaCalculos.alturasMochetasPorModelo(
                        modelo = modelo,
                        alto = alto,
                        altoHoja = altoHoja,
                        alturaPuente = alturaPuente
                    )
                    val inf = alturas.inferior ?: alturas.superior
                    inf.coerceAtLeast(0f) to alturas.superior.coerceAtLeast(0f)
                }
                else -> (alto - altoHoja).coerceAtLeast(0f) to 0f
            }
        }

        val referenciasBase = "An: ${NovaCalculos.df1(ancho)}  x  Al: ${NovaCalculos.df1(alto)}\n" +
                "Altura de puente: $altoPuenteTexto\n" +
                "mocheta inf: ${NovaCalculos.df1(mochetaInf)}\n" +
                "mocheta sup: ${NovaCalculos.df1(mochetaSup)}\n" +
                "Divisiones: $divisiones -> fjs: $nFijos;czs: $nCorredizas"

        return if (divisiones > 4 && puntosU.isNotEmpty()) {
            "$referenciasBase\nPuntos: $puntosU"
        } else {
            referenciasBase
        }
    }
//{nova,apa,[150,120:m<30.6>(fccf);s<80>(fcf);m<9.4>(cfc)}]

    /**
     * Genera la cadena de tramos para una ventana: "Tb<w>(s<h>(mods);m<hm>(mods)) P<2.5> Tb<w2>(...)"
     * Sin prefijo de dimensiones. Úsalo cuando necesitas solo el cuerpo de tramos.
     */
    fun generarTramos(
        ancho: Float,
        alto: Float,
        altoHoja: Float,
        divisiones: Int,
        siNoMoch: Int,
        texto: String
    ): String {
        val mo       = NovaCalculos.altoMocheta(alto, altoHoja, tubo = 2.5f)
        val moDos    = (alto - altoHoja).coerceAtLeast(0f) / 2f
        val altoPuente = if (siNoMoch == 1) NovaCalculos.df1(altoHoja) else ""
        val altoSisTxt = NovaCalculos.df1(altoHoja)
        val moTxt    = NovaCalculos.df1(mo)
        val moDosT   = NovaCalculos.df1(moDos)

        val grupos   = NovaCalculos.gruposDivisionesMochetaPorModelo(ancho, divisiones, texto)
        val nTramos  = grupos.size
        val anchoUtil   = if (nTramos > 1) ancho - (nTramos - 1) * 2.5f else ancho
        val anchoPorDiv = if (divisiones > 0) anchoUtil / divisiones else ancho

        val bloques = grupos.map { nDiv ->
            val w    = anchoPorDiv * nDiv
            val wTxt = NovaCalculos.df1(w)
            val am   = NovaCalculos.anchMota(w)
            val wMochTxt = NovaCalculos.df1(w / am)
            val modsM = (1..am).joinToString("") { "f<$wMochTxt>" }

            val modsS = when (texto) {
                "ncc"  -> { val h = NovaCalculos.df1(w / 2); "c<$h>c<$h>" }
                "n3c"  -> { val h = NovaCalculos.df1(w / 3); "c<$h>c<$h>c<$h>" }
                "ncfc" -> { val h = NovaCalculos.df1(w / 3); "c<$h>f<$h>c<$h>" }
                else   -> NovaCalculos.ordenDivis(nDiv, w)
            }

            val contenido = when (texto) {
                "nr" -> {
                    val s = "s<$altoSisTxt>($modsS)"
                    if (siNoMoch == 1) "m<$moTxt>($modsM);$s" else s
                }
                "np", "nci" -> {
                    val s = "s<$altoSisTxt>($modsS)"
                    if (siNoMoch == 1) "m<$moDosT>($modsM);$s;m<$moDosT>($modsM)" else s
                }
                else -> {
                    val s = "s<$altoPuente>($modsS)"
                    if (siNoMoch == 1) "$s;m<$moTxt>($modsM)" else s
                }
            }
            "Tl<$wTxt>($contenido)"
        }
        return bloques.joinToString(" P<2.5> ")
    }

    /**
     * Como generarTramos pero siempre produce un único Tl<ancho>(...) con ;P; internos.
     * Necesario para geometrías compuestas (nl/nu/ns/ncu/nci): evita que los separadores
     * P<2.5> entre tramos se mezclen con los A<90> entre paneles en parsearConTramos.
     */
    fun generarTramosConsolidado(
        ancho: Float,
        alto: Float,
        altoHoja: Float,
        divisiones: Int,
        siNoMoch: Int,
        texto: String
    ): String {
        val mo       = NovaCalculos.altoMocheta(alto, altoHoja, tubo = 2.5f)
        val moDos    = (alto - altoHoja).coerceAtLeast(0f) / 2f
        val altoPuente = if (siNoMoch == 1) NovaCalculos.df1(altoHoja) else ""
        val altoSisTxt = NovaCalculos.df1(altoHoja)
        val moTxt    = NovaCalculos.df1(mo)
        val moDosT   = NovaCalculos.df1(moDos)

        val grupos   = NovaCalculos.gruposDivisionesMochetaPorModelo(ancho, divisiones, texto)
        val nTramos  = grupos.size
        val anchoUtil   = if (nTramos > 1) ancho - (nTramos - 1) * 2.5f else ancho
        val anchoPorDiv = if (divisiones > 0) anchoUtil / divisiones else ancho

        if (nTramos <= 1) {
            return generarTramos(ancho, alto, altoHoja, divisiones, siNoMoch, texto)
        }

        val listaSistMods = mutableListOf<String>()
        val listaMochMods = mutableListOf<String>()
        for (nDiv in grupos) {
            val w = anchoPorDiv * nDiv
            val am = NovaCalculos.anchMota(w)
            val wMochTxt = NovaCalculos.df1(w / am)
            listaMochMods.add((1..am).joinToString("") { "f<$wMochTxt>" })
            listaSistMods.add(when (texto) {
                "ncc"  -> { val h = NovaCalculos.df1(w / 2); "c<$h>c<$h>" }
                "n3c"  -> { val h = NovaCalculos.df1(w / 3); "c<$h>c<$h>c<$h>" }
                "ncfc" -> { val h = NovaCalculos.df1(w / 3); "c<$h>f<$h>c<$h>" }
                else   -> NovaCalculos.ordenDivis(nDiv, w)
            })
        }

        val sistMods = listaSistMods.joinToString(";P;")
        val mochMods = listaMochMods.joinToString(";P;")
        val anchoTxt = NovaCalculos.df1(ancho)

        val contenido = when (texto) {
            "nr" -> {
                val s = "s<$altoSisTxt>($sistMods)"
                if (siNoMoch == 1) "m<$moTxt>($mochMods);$s" else s
            }
            "np", "nci" -> {
                val s = "s<$altoSisTxt>($sistMods)"
                if (siNoMoch == 1) "m<$moDosT>($mochMods);$s;m<$moDosT>($mochMods)" else s
            }
            else -> {
                val s = "s<$altoPuente>($sistMods)"
                if (siNoMoch == 1) "$s;m<$moTxt>($mochMods)" else s
            }
        }
        return "Tl<$anchoTxt>($contenido)"
    }

    fun generarDisenoConsolidado(
        ancho: Float,
        alto: Float,
        altoHoja: Float,
        divisiones: Int,
        siNoMoch: Int,
        texto: String
    ): String {
        val encabezado = "${NovaCalculos.df1(ancho)},${NovaCalculos.df1(alto)}:"
        return encabezado + generarTramosConsolidado(ancho, alto, altoHoja, divisiones, siNoMoch, texto)
    }

    fun generarDiseno(
        ancho: Float,
        alto: Float,
        altoHoja: Float,
        divisiones: Int,
        siNoMoch: Int,
        texto: String
    ): String {
        val encabezado = "${NovaCalculos.df1(ancho)},${NovaCalculos.df1(alto)}:"
        return encabezado + generarTramos(ancho, alto, altoHoja, divisiones, siNoMoch, texto)
    }

    fun generarPuntosU(
        ancho: Float,
        divisManual: Int,
        cruce: Float,
        tipoCalculo: String
    ): String {
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val partes = NovaCalculos.uFijos(ancho, divisiones, cruce, tipoCalculo)

        val punto1 = NovaCalculos.df1((partes * 2) - cruce * 2).toFloat()
        val punto2 = NovaCalculos.df1((partes * 4) - cruce * 4).toFloat()
        val punto3 = NovaCalculos.df1((partes * 6) - cruce * 6).toFloat()

        return when (divisiones) {
            5, 6 -> NovaCalculos.df1((partes * 2) - cruce * 2)
            8, 12 -> NovaCalculos.df1((partes * 3) - cruce * 2)
            7, 10, 14 -> "${NovaCalculos.df1(punto1)}_${NovaCalculos.df1(punto2)}"
            9, 11, 13, 15 -> "${NovaCalculos.df1(punto1)}_${NovaCalculos.df1(punto2)}_${NovaCalculos.df1(punto3)}"
            else -> ""
        }
    }

    fun generarPaqueteSimbolico(
        tipoNova: String,
        ancho: Float,
        alto: Float,
        altoHoja: Float,
        divisiones: Int,
        texto: String
    ): String {
        val diseno = generarDiseno(ancho, alto, altoHoja, divisiones, siNoMoch = 1, texto)
        return "{nova,$tipoNova,[$diseno]}"
    }
}
