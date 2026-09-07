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
 * Mantiene la lógica original pero centralizada
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

    // ==================== FUNCIONES DE DISEÑO ====================


    fun obtenerNombreDiseno(divisiones: Int, siNoMoch: Int, tipoVentana: String = "apa", prefijo: String = "ic_fichad"): String {
        // LÓGICA ORIGINAL: siNoMoch = 1 → sin sufijo, siNoMoch = 0 → sufijo "c"
        // Esto es igual para AMBAS ventanas (NovaApa y NovaIna)

        return when (siNoMoch) {
            1 -> {
                // Con mocheta - según código original
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

    // ==================== FUNCIÓN DE PROCESAMIENTO DE PROYECTO ====================

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

    // ==================== FUNCIÓN DE REFERENCIAS ====================

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
        alturaPuente: Float = 2.5f,
        mochetaInferior: Float = 0f,
        // `modelo` = modulación (patrón de hojas); `remate` = nn/nr/np (mochetas). Son ejes
        // independientes: pasar la modulación aquí hace que la rama "np" nunca se ejecute.
        remate: String = modelo,
        // APA descuenta del alto los perfiles de puente al repartir las mochetas; INA no.
        descontarPuentes: Boolean = true,
        // Medida REAL para mostrar en la referencia. El ancho/alto de arriba puede venir con el
        // descuento de encuentro (ancho útil) para los cálculos de corte, pero la referencia debe
        // mostrar la medida real que se midió (y que se archiva). Por defecto = ancho/alto.
        anchoReal: Float = ancho,
        altoReal: Float = alto
    ): String {
        val altoPuenteTexto = if (siNoMoch == 1) {
            NovaCalculos.df1(altoHoja)
        } else {
            "sin puente"
        }

        val (mochetaInf, mochetaSup) = if (siNoMoch == 0) {
            0f to 0f
        } else {
            when (remate) {
                "nr" -> 0f to (alto - altoHoja).coerceAtLeast(0f)
                "np" -> {
                    val alturas = NovaInaCalculos.alturasMochetasPorModelo(
                        modelo = remate,
                        alto = alto,
                        altoHoja = altoHoja,
                        alturaPuente = alturaPuente,
                        mochetaInferior = mochetaInferior,
                        descontarPuentes = descontarPuentes
                    )
                    val inf = alturas.inferior ?: alturas.superior
                    inf.coerceAtLeast(0f) to alturas.superior.coerceAtLeast(0f)
                }
                else -> (alto - altoHoja).coerceAtLeast(0f) to 0f
            }
        }

        val referenciasBase = "An: ${NovaCalculos.df1(anchoReal)}  x  Al: ${NovaCalculos.df1(altoReal)}\n" +
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
        texto: String,
        mochetaInferior: Float = 0f,
        remate: String = texto
    ): String {
        // `texto` = modulación (patrón de hojas del sistema); `remate` = nn/nr/np/nci (mochetas).
        val mo       = NovaCalculos.altoMocheta(alto, altoHoja, tubo = 2.5f)
        val disponible = (alto - altoHoja).coerceAtLeast(0f)
        val moDos    = disponible / 2f
        // Doble puente (np): si se ingresó una mocheta inferior explícita (> 0) se respeta
        // ese valor y la mocheta superior toma el resto; si es 0 se reparte el disponible
        // en dos partes iguales (comportamiento previo).
        val moDosInf = if (remate == "np" && mochetaInferior > 0f) mochetaInferior.coerceAtMost(disponible) else moDos
        val moDosSup = if (remate == "np" && mochetaInferior > 0f) (disponible - moDosInf).coerceAtLeast(0f) else moDos
        val altoPuente = if (siNoMoch == 1) NovaCalculos.df1(altoHoja) else ""
        val altoSisTxt = NovaCalculos.df1(altoHoja)
        val moTxt    = NovaCalculos.df1(mo)
        val moDosInfT = NovaCalculos.df1(moDosInf)
        val moDosSupT = NovaCalculos.df1(moDosSup)

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
                "ncfc" -> NovaCalculos.ordenDivisCfc(nDiv, w)
                "nff"  -> {
                    val h = NovaCalculos.df1(w / nDiv)
                    (1..nDiv).joinToString("") { "f<$h>" }
                }
                "nfc"  -> {
                    val h = NovaCalculos.df1(w / nDiv)
                    (1..nDiv).joinToString("") { "c<$h>" }
                }
                else   -> NovaCalculos.ordenDivis(nDiv, w)
            }

            val contenido = when (remate) {
                "nr" -> {
                    val s = "s<$altoSisTxt>($modsS)"
                    if (siNoMoch == 1) "m<$moTxt>($modsM);$s" else s
                }
                "np", "nci" -> {
                    val s = "s<$altoSisTxt>($modsS)"
                    if (siNoMoch == 1) "m<$moDosInfT>($modsM);$s;m<$moDosSupT>($modsM)" else s
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
        texto: String,
        mochetaInferior: Float = 0f,
        remate: String = texto
    ): String {
        // `texto` = modulación (patrón de hojas del sistema); `remate` = nn/nr/np/nci (mochetas).
        val mo       = NovaCalculos.altoMocheta(alto, altoHoja, tubo = 2.5f)
        val disponible = (alto - altoHoja).coerceAtLeast(0f)
        val moDos    = disponible / 2f
        // Doble puente (np): respeta la mocheta inferior ingresada (> 0) y deja el resto
        // para la superior; con 0 reparte el disponible en dos partes iguales.
        val moDosInf = if (remate == "np" && mochetaInferior > 0f) mochetaInferior.coerceAtMost(disponible) else moDos
        val moDosSup = if (remate == "np" && mochetaInferior > 0f) (disponible - moDosInf).coerceAtLeast(0f) else moDos
        val altoPuente = if (siNoMoch == 1) NovaCalculos.df1(altoHoja) else ""
        val altoSisTxt = NovaCalculos.df1(altoHoja)
        val moTxt    = NovaCalculos.df1(mo)
        val moDosInfT = NovaCalculos.df1(moDosInf)
        val moDosSupT = NovaCalculos.df1(moDosSup)

        val grupos   = NovaCalculos.gruposDivisionesMochetaPorModelo(ancho, divisiones, texto)
        val nTramos  = grupos.size
        // ncfc/nfc en INA: orden por grupos pero SIN parantes que dividan → un solo tramo continuo.
        // No se descuentan parantes y los grupos se unen sin ";P;".
        val unTramo  = (NovaCalculos.ncfcUnTramo && texto == "ncfc") || (NovaCalculos.nfcUnTramo && texto == "nfc")
        val anchoUtil   = if (nTramos > 1 && !unTramo) ancho - (nTramos - 1) * 2.5f else ancho
        val anchoPorDiv = if (divisiones > 0) anchoUtil / divisiones else ancho

        if (nTramos <= 1) {
            return generarTramos(ancho, alto, altoHoja, divisiones, siNoMoch, texto, mochetaInferior, remate)
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
                "ncfc" -> NovaCalculos.ordenDivisCfc(nDiv, w)
                "nff"  -> {
                    val h = NovaCalculos.df1(w / nDiv)
                    (1..nDiv).joinToString("") { "f<$h>" }
                }
                "nfc"  -> {
                    val h = NovaCalculos.df1(w / nDiv)
                    (1..nDiv).joinToString("") { "c<$h>" }
                }
                else   -> NovaCalculos.ordenDivis(nDiv, w)
            })
        }

        val sep = if (unTramo) "" else ";P;"
        val sistMods = listaSistMods.joinToString(sep)
        val mochMods = listaMochMods.joinToString(sep)
        val anchoTxt = NovaCalculos.df1(ancho)

        val contenido = when (remate) {
            "nr" -> {
                val s = "s<$altoSisTxt>($sistMods)"
                if (siNoMoch == 1) "m<$moTxt>($mochMods);$s" else s
            }
            "np", "nci" -> {
                val s = "s<$altoSisTxt>($sistMods)"
                if (siNoMoch == 1) "m<$moDosInfT>($mochMods);$s;m<$moDosSupT>($mochMods)" else s
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
        texto: String,
        mochetaInferior: Float = 0f,
        remate: String = texto
    ): String {
        val encabezado = "${NovaCalculos.df1(ancho)},${NovaCalculos.df1(alto)}:"
        return encabezado + generarTramosConsolidado(ancho, alto, altoHoja, divisiones, siNoMoch, texto, mochetaInferior, remate)
    }

    fun generarDiseno(
        ancho: Float,
        alto: Float,
        altoHoja: Float,
        divisiones: Int,
        siNoMoch: Int,
        texto: String,
        mochetaInferior: Float = 0f,
        remate: String = texto
    ): String {
        val encabezado = "${NovaCalculos.df1(ancho)},${NovaCalculos.df1(alto)}:"
        // INA ncfc/nfc: un solo tramo continuo (sin el parante " P<2.5> " entre grupos). Se usa el
        // consolidado, que con el flag de un-tramo une los grupos en un único Tl sin parante.
        val unTramo = (NovaCalculos.ncfcUnTramo && texto == "ncfc") || (NovaCalculos.nfcUnTramo && texto == "nfc")
        val cuerpo = if (unTramo) {
            generarTramosConsolidado(ancho, alto, altoHoja, divisiones, siNoMoch, texto, mochetaInferior, remate)
        } else {
            generarTramos(ancho, alto, altoHoja, divisiones, siNoMoch, texto, mochetaInferior, remate)
        }
        return encabezado + cuerpo
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
        texto: String,
        mochetaInferior: Float = 0f,
        remate: String = texto
    ): String {
        // El puente (hoja) puede igualar o superar el alto: en ese caso no hay mocheta y la
        // franja sistema ocupa todo el alto. Antes se forzaba siNoMoch = 1, lo que dibujaba una
        // mocheta fantasma (el puente "subido" arriba como línea gruesa) y su U de fijos fantasma.
        val siNoMoch = if (altoHoja >= alto) 0 else 1
        val diseno = generarDiseno(ancho, alto, altoHoja, divisiones, siNoMoch, texto, mochetaInferior, remate)
        return "{nova,$tipoNova,[$diseno]}"
    }
}
