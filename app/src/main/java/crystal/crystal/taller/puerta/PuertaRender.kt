package crystal.crystal.taller.puerta

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import crystal.crystal.taller.puerta.dibujo.DibujoPuerta
import crystal.crystal.taller.puerta.logica.CalculosLina
import crystal.crystal.taller.puerta.logica.CalculosPuerta
import kotlin.math.sqrt

// Redibuja una puerta a partir de su descriptor (sin depender de la UI). Misma lógica de dispatch
// que PuertasActivity.renderizarModeloActual, para reusarla en FichaActivity y la galería de planos.
// Modelos soportados: Tere, Dora, jeny, Mili, Mari (h/v/d), Taly, Adel y Lina. Tere 6 → null.
object PuertaRender {

    /**
     * Cómo se reparte el interior de la hoja. Vive solo acá: lo usan el render en vivo de la
     * calculadora y el que regenera una casilla archivada, y cuando estaba escrito en los dos lados
     * cada variante nueva había que agregarla dos veces.
     */
    fun tipoDivision(modelo: String, variante: String): String = when {
        variante == "Viky c" -> "VICKYC"
        modelo == "Viky" -> "VICKY"
        variante == "Mari v" -> "V"
        variante == "Mari d" -> "D"
        else -> "H"
    }

    fun dibujar(context: Context, d: PuertaDescriptor.Datos, anchoCont: Float, altoCont: Float): Bitmap? {
        if (d.ancho <= 0f || d.alto <= 0f) return null
        val hojaRef = CalculosPuerta.HOJA_REF
        val marcoIzq = if (d.ventanaIzq) 2.5f else d.marco
        val marcoDer = if (d.ventanaDer) 2.5f else d.marco
        val anchoHoja = CalculosPuerta.anchoHoja(d.ancho, marcoIzq, marcoDer, d.holgura)
        val altoHoja = CalculosPuerta.hPuente(d.alto, d.hoja, d.piso, hojaRef, d.marco)
        val nZ = CalculosPuerta.nZocalo(d.zocalos)
        val gapPiso = altoHoja - CalculosPuerta.parante(altoHoja, d.piso)
        val tipoDivision = tipoDivision(d.modelo, d.variante)

        return when (d.modelo) {
            "Tere" -> if (d.variante == "Tere 6") null else DibujoPuerta.generarBitmapTere(
                context = context, marcoCm = d.marco, puenteCm = d.puente,
                anchoCm = d.ancho, altoCm = d.alto, altoHojaCm = altoHoja,
                anchoContenedor = anchoCont, altoContenedor = altoCont,
                pisoCm = gapPiso, nZocalo = nZ
            )
            "Dora" -> DibujoPuerta.generarBitmapDora(
                context = context, marcoCm = d.marco, puenteCm = d.puente,
                anchoCm = d.ancho, altoCm = d.alto, altoHojaCm = altoHoja,
                anchoContenedor = anchoCont, altoContenedor = altoCont,
                pisoCm = gapPiso, nZocalo = nZ,
                nInoxIzq = d.divisiones.takeIf { it >= 1 } ?: 9, inoxCm = d.inox
            )
            "jeny" -> DibujoPuerta.generarBitmapJeny(
                context = context, marcoCm = d.marco, puenteCm = d.puente,
                anchoCm = d.ancho, altoCm = d.alto, altoHojaCm = altoHoja,
                nDivisiones = d.divisiones.takeIf { it >= 1 } ?: 1,
                rejillaCols = d.rejillaCols, rejillaFilas = d.rejillaFilas,
                variante = d.variante,
                anchoContenedor = anchoCont, altoContenedor = altoCont,
                pisoCm = gapPiso, nZocalo = nZ
            )
            "Mili" -> DibujoPuerta.generarBitmapMili(
                context = context, marcoCm = d.marco, puenteCm = d.puente,
                anchoCm = d.ancho, altoCm = d.alto, altoHojaCm = altoHoja,
                anchoContenedor = anchoCont, altoContenedor = altoCont,
                pisoCm = gapPiso, nZocalo = nZ
            )
            "Taly" -> DibujoPuerta.generarBitmapTaly(
                context = context, marcoCm = d.marco, puenteCm = d.puente,
                anchoPuertaCm = d.ancho, altoPuertaCm = d.alto,
                anchoHojaCm = anchoHoja, altoHojaCm = altoHoja,
                anchoContenedor = anchoCont, altoContenedor = altoCont,
                numeroDivisiones = d.divisiones,
                anguloGrados = if (d.variante == "Taly d") d.angulo else 0f,
                marcoCmIzq = marcoIzq, marcoCmDer = marcoDer, nZocalo = nZ, pisoCm = gapPiso,
            )
            "Adel" -> DibujoPuerta.generarBitmapAdel(
                context = context, marcoCm = d.marco, puenteCm = d.puente,
                anchoCm = d.ancho, altoCm = d.alto, altoHojaCm = altoHoja,
                anchoContenedor = anchoCont, altoContenedor = altoCont,
                nDivisiones = d.divisiones.takeIf { it >= 1 } ?: 3,
                variante = d.variante, nZocalo = nZ, pisoCm = gapPiso
            )
            // El diseño NORMAL de Lina, que es lo que va en la ficha del producto. El plano con la
            // estructura interior es otra cosa y solo se genera cuando se pide el plano.
            "Lina" -> {
                val esPlegado = d.variante == "Lina p"
                val altoHojaLina = if (d.variante == "Lina b" || d.variante == "Lina c") {
                    CalculosLina.hojaHConPiso(d.alto, d.hoja, d.piso, esPlegado, d.marco)
                } else {
                    CalculosLina.hojaH(d.alto, d.hoja, esPlegado, d.marco)
                }
                val pisoLina = altoHojaLina - CalculosPuerta.parante(altoHojaLina, d.piso)
                DibujoPuerta.generarBitmapLinaH(
                    context = context, anchoCm = d.ancho, altoCm = d.alto, altoHojaCm = altoHojaLina,
                    nPaneles = d.divisiones.takeIf { it >= 3 } ?: 5,
                    anchoContenedor = anchoCont, altoContenedor = altoCont,
                    marcoCm = d.marco, pisoCm = pisoLina, puenteCm = d.puente,
                    mostrarVidrioCentral = d.variante != "Lina b",
                    panelCompleto = d.variante == "Lina c"
                )
            }
            "Mari", "Viky" -> DibujoPuerta.generarBitmapPuerta(
                context = context, marcoCm = d.marco, puenteCm = d.puente,
                anchoPuertaCm = d.ancho, altoPuertaCm = d.alto,
                anchoHojaCm = anchoHoja, altoHojaCm = altoHoja,
                numeroZocalos = nZ, numeroDivisiones = d.divisiones,
                anchoContenedor = anchoCont, altoContenedor = altoCont,
                tipoDivision = tipoDivision, anguloGrados = d.angulo,
                marcoCmIzq = marcoIzq, marcoCmDer = marcoDer, pisoCm = gapPiso,
                // Viky y Mari arman sus divisiones con el aluminio del interior.
                interiorCm = d.interior
            )
            else -> null
        }
    }

    // Plano con cotas (medidas originales) + cabecera con cliente, número y todos los datos.
    // Dora y Mari llevan cotas; el resto, el diseño simple.
    fun dibujarPlano(
        context: Context, d: PuertaDescriptor.Datos,
        numeracion: String = "", aluminio: String = "", vidrio: String = "", cabecera: Boolean = true
    ): Bitmap? {
        if (d.ancho <= 0f || d.alto <= 0f) return null
        val base = when (d.modelo) {
            "Dora" -> planoDora(context, d)
            "Mari" -> planoMari(context, d)
            "Viky" -> if (d.variante == "Viky c") planoVikyC(context, d) else planoViky(context, d)
            "Adel" -> planoAdel(context, d)
            "Mili" -> planoMili(context, d)
            "jeny" -> planoJeny(context, d)
            "Taly" -> planoTaly(context, d)
            "Lina" -> when (d.variante) {
                "Lina c" -> planoLinaC(context, d)
                "Lina b" -> planoLinaB(context, d)
                else -> null
            }
            else -> dibujar(context, d, d.ancho * 5f, d.alto * 5f)
        } ?: return null
        return if (cabecera) conCabecera(base, d, numeracion, aluminio, vidrio) else base
    }

    // Texto del plano (reutilizado por la cabecera del bitmap y por la tarjeta de la galería).
    fun tituloPlano(d: PuertaDescriptor.Datos, numeracion: String): String =
        listOf(d.cliente, numeracion).filter { it.isNotBlank() }.joinToString(", ")

    fun datosPlano(d: PuertaDescriptor.Datos, aluminio: String, vidrio: String): String {
        val nombre = d.variante.ifBlank { d.modelo }
            .split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        val vIzq = if (d.ventanaIzq) "sí" else "no"
        val vDer = if (d.ventanaDer) "sí" else "no"
        // La cabecera repite los datos ingresados, así que van sin redondear: un perfil de 8.25 no
        // puede quedar documentado como 8.3.
        val dato = { v: Float -> CalculosPuerta.dato(v) }
        val perfiles = buildString {
            append("Marco ${dato(d.marco)}   Puente ${dato(d.puente)}")
            if (d.modelo == "Dora") append("   Inox ${dato(d.inox)}")
            append("   Ventana izq:$vIzq der:$vDer")
        }
        val usaAngulo = d.variante == "Mari d" || d.variante == "Taly d"
        val angTxt = if (usaAngulo) "   Áng ${dato(d.angulo)}" else ""
        return "Modelo $nombre        ${dato(d.ancho)} x ${dato(d.alto)}\n" +
            "Hoja ${dato(d.hoja)}   Piso ${dato(d.piso)}   Zócalo ${d.zocalos}   Div ${d.divisiones}$angTxt\n" +
            "$perfiles\n" +
            "Aluminio: ${aluminio.ifBlank { "—" }}        Vidrio: ${vidrio.ifBlank { "—" }}"
    }

    // Agrega arriba del plano una cabecera con cliente, número de producto y todos los datos ingresados.
    private fun conCabecera(
        door: Bitmap, d: PuertaDescriptor.Datos, numeracion: String, aluminio: String, vidrio: String
    ): Bitmap {
        val titulo = tituloPlano(d, numeracion)
        val datos = datosPlano(d, aluminio, vidrio).split("\n")
        val pBold = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK; textSize = door.height * 0.026f; typeface = Typeface.DEFAULT_BOLD
        }
        val pTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; textSize = door.height * 0.018f }
        var pad = pTxt.textSize * 0.7f
        // La cabecera NO ensancha el bitmap: el texto se reduce para caber en el ancho del dibujo.
        val maxW = maxOf(pBold.measureText(titulo), datos.maxOf { pTxt.measureText(it) })
        if (maxW + 2f * pad > door.width) {
            val f = door.width / (maxW + 2f * pad)
            pBold.textSize *= f; pTxt.textSize *= f; pad = pTxt.textSize * 0.7f
        }
        val lineHTit = pBold.textSize * 1.4f
        val lineH = pTxt.textSize * 1.45f
        val headerH = (pad * 2f + lineHTit + lineH * datos.size).toInt()
        val out = Bitmap.createBitmap(door.width, door.height + headerH, Bitmap.Config.ARGB_8888)
        val c = Canvas(out)
        c.drawColor(Color.WHITE)
        var y = pad + pBold.textSize
        c.drawText(titulo, pad, y, pBold)
        y += lineHTit
        for (l in datos) { c.drawText(l, pad, y, pTxt); y += lineH }
        val pLn = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.LTGRAY; strokeWidth = 2f }
        c.drawLine(0f, headerH.toFloat(), door.width.toFloat(), headerH.toFloat(), pLn)
        c.drawBitmap(door, 0f, headerH.toFloat(), null)
        return out
    }

    private fun dd(v: Float) = CalculosPuerta.df1(v)

    private fun planoDora(context: Context, d: PuertaDescriptor.Datos): Bitmap {
        val bastidor = CalculosPuerta.BASTIDOR
        val hojaRef = CalculosPuerta.HOJA_REF
        val altoHojaCm = CalculosPuerta.hPuente(d.alto, d.hoja, d.piso, hojaRef, d.marco)
        val gapPisoCm = altoHojaCm - CalculosPuerta.parante(altoHojaCm, d.piso)
        val totalMarco = (if (d.ventanaIzq) 2.5f else d.marco) + (if (d.ventanaDer) 2.5f else d.marco)
        val paflon = CalculosPuerta.anchoHoja(d.ancho, totalMarco, 0f, d.holgura) - 2f * bastidor
        val colW = (paflon - 2f * bastidor) / 3f
        val nZ = CalculosPuerta.nZocalo(d.zocalos)
        val nZdora = if (nZ % 2 == 0) maxOf(1, nZ - 1) else nZ
        val zocaloInox = (nZdora - 1) / 2
        val paflonHDora = 1 + (nZdora + 1) / 2
        val parante = CalculosPuerta.parante(altoHojaCm, d.piso)
        val paranteIntDora = parante - bastidor * paflonHDora - d.inox * zocaloInox
        val nInoxIzq = d.divisiones.takeIf { it >= 1 } ?: 9
        val raiz2 = sqrt(2f)
        val secH = (paranteIntDora - nInoxIzq * d.inox) / (nInoxIzq + 1)
        val a = paranteIntDora / 2f - bastidor * raiz2
        val xCol = colW; val xDivR = colW + bastidor; val xBast = 2f * colW + bastidor
        fun topSeg(x: Float) = a - x
        fun midSeg(x: Float) = 2f * x
        // Todos los rótulos con un decimal: dicen cuánto mide cada pieza, así que son medidas de
        // material. Los dos decimales quedan para las reglas de cotas, que son posiciones de trazo.
        // Los anchos de columna no se rotulan: iban repetidos tres veces, que es una medida y no la
        // acotación del plano. Eso ahora lo dice la regla de abajo, acumulada desde el parante.
        val etiquetas = mapOf(
            "gen" to "${dd(d.ancho)} x ${dd(d.alto)}",
            "lA_t" to dd(topSeg(0f)), "lB_t" to dd(topSeg(xCol)), "lC_t" to dd(topSeg(xDivR)), "lD_t" to dd(topSeg(xBast)),
            "lA_b" to dd(topSeg(0f)), "lB_b" to dd(topSeg(xCol)), "lC_b" to dd(topSeg(xDivR)), "lD_b" to dd(topSeg(xBast)),
            "lB_m" to dd(midSeg(xCol)), "lC_m" to dd(midSeg(xDivR)), "lD_m" to dd(midSeg(xBast)),
            "secc" to dd(secH), "inox" to "inox ${dd(d.inox)}",
            "parante" to dd(parante), "div" to dd(paranteIntDora), "diag" to dd(colW * raiz2),
            "zoc" to "${(nZdora + 1) / 2}x${dd(bastidor)} + ${zocaloInox}x${dd(d.inox)}"
        )
        val altoCont = 2400f
        val anchoCont = altoCont * (d.ancho / d.alto) + 700f
        return DibujoPuerta.generarBitmapDora(
            context = context, marcoCm = d.marco, puenteCm = d.puente,
            anchoCm = d.ancho, altoCm = d.alto, altoHojaCm = altoHojaCm,
            anchoContenedor = anchoCont, altoContenedor = altoCont,
            pisoCm = gapPisoCm, nZocalo = nZ, nInoxIzq = nInoxIzq, inoxCm = d.inox,
            etiquetas = etiquetas,
            cotas = CalculosPuerta.cotasPanosDora(parante, nZ, nInoxIzq, bastidor, d.inox),
            cotasH = CalculosPuerta.cotasColumnasDora(paflon, bastidor),
            escalaInterna = 0.84f
        )
    }

    private fun planoMari(context: Context, d: PuertaDescriptor.Datos): Bitmap {
        val bastidor = CalculosPuerta.BASTIDOR
        val hojaRef = CalculosPuerta.HOJA_REF
        val altoHojaCm = CalculosPuerta.hPuente(d.alto, d.hoja, d.piso, hojaRef, d.marco)
        val gapPisoCm = altoHojaCm - CalculosPuerta.parante(altoHojaCm, d.piso)
        val marcoIzq = if (d.ventanaIzq) 2.5f else d.marco
        val marcoDer = if (d.ventanaDer) 2.5f else d.marco
        val anchoHojaCm = CalculosPuerta.anchoHoja(d.ancho, marcoIzq, marcoDer, d.holgura)
        val tipoDivision = when (d.variante) { "Mari v" -> "V"; "Mari d" -> "D"; else -> "H" }
        val nZ = CalculosPuerta.nZocalo(d.zocalos)
        val parante = CalculosPuerta.parante(altoHojaCm, d.piso)
        val paflon = anchoHojaCm - 2f * bastidor
        val nDiv = d.divisiones
        val etiquetas = mapOf("gen" to "${dd(d.ancho)} x ${dd(d.alto)}", "ancho" to dd(paflon))
        val esV = tipoDivision == "V"; val esD = tipoDivision == "D"
        var cotas: List<Float>? = null; var cotasH: List<Float>? = null
        var cotasDer: List<Float>? = null; var cotasSup: List<Float>? = null; var cotasInf: List<Float>? = null
        when {
            esV -> cotasH = CalculosPuerta.cotasPanosVertical(paflon, nDiv, bastidor, d.interior)
            esD -> {
                val pi = CalculosPuerta.paranteInterno(parante, nZ, bastidor)
                val cr = CalculosPuerta.cotasPanosDiagonal(paflon, pi, nZ, nDiv, bastidor, d.angulo, d.interior)
                val zocList = (1..nZ).map { it * bastidor }
                cotas = (zocList + cr.izq).sorted(); cotasDer = (zocList + cr.der).sorted()
                cotasSup = cr.sup; cotasInf = cr.inf
            }
            else -> cotas = CalculosPuerta.cotasPanos(parante, nZ, nDiv, bastidor, d.interior)
        }
        val altoCont = if (esV) 2400f else 2200f
        val ratio = d.ancho / d.alto
        val anchoCont = when {
            esV -> altoCont * ratio * 0.82f
            esD -> altoCont * ratio + 760f
            else -> altoCont * ratio + 520f
        }
        return DibujoPuerta.generarBitmapPuerta(
            context = context, marcoCm = d.marco, puenteCm = d.puente,
            anchoPuertaCm = d.ancho, altoPuertaCm = d.alto,
            anchoHojaCm = anchoHojaCm, altoHojaCm = altoHojaCm,
            numeroZocalos = nZ, numeroDivisiones = nDiv,
            anchoContenedor = anchoCont, altoContenedor = altoCont,
            tipoDivision = tipoDivision, anguloGrados = if (esD) d.angulo else 0f,
            marcoCmIzq = marcoIzq, marcoCmDer = marcoDer, pisoCm = gapPisoCm,
            etiquetas = etiquetas, cotas = cotas, cotasH = cotasH, cotasDer = cotasDer,
            cotasSup = cotasSup, cotasInf = cotasInf, alturaPuente = altoHojaCm,
            interiorCm = d.interior
        )
    }

    // Plano de Viky: como Mari h pero con el paflon vertical divisor. Lleva LAS DOS cotas:
    //  - vertical (regla izquierda): alturas acumuladas de los paflones horizontales + zócalo.
    //  - horizontal (margen inferior, estilo Mari v): del borde interior del bastidor al divisor
    //    (= ancho del cuadrado) y al otro extremo (= pieza alta).
    // Se reduce la escala del dibujo (escalaInterna) para dejar margen a ambas cotas.
    private fun planoViky(context: Context, d: PuertaDescriptor.Datos): Bitmap {
        val bastidor = CalculosPuerta.BASTIDOR
        val hojaRef = CalculosPuerta.HOJA_REF
        val altoHojaCm = CalculosPuerta.hPuente(d.alto, d.hoja, d.piso, hojaRef, d.marco)
        val gapPisoCm = altoHojaCm - CalculosPuerta.parante(altoHojaCm, d.piso)
        val marcoIzq = if (d.ventanaIzq) 2.5f else d.marco
        val marcoDer = if (d.ventanaDer) 2.5f else d.marco
        val anchoHojaCm = CalculosPuerta.anchoHoja(d.ancho, marcoIzq, marcoDer, d.holgura)
        val nZ = CalculosPuerta.nZocalo(d.zocalos)
        val parante = CalculosPuerta.parante(altoHojaCm, d.piso)
        val paflon = anchoHojaCm - 2f * bastidor
        val nDiv = d.divisiones
        val sq = CalculosPuerta.ladoCuadradoViky(parante, nZ, nDiv, bastidor, d.interior)
        val cotas = CalculosPuerta.cotasPanos(parante, nZ, nDiv, bastidor, d.interior)
        // Solo el inicio del divisor (= ancho del cuadrado) y el ancho total. El fin del divisor no se
        // marca: a 90° su ancho es el del bastidor; solo haría falta si tocara el bastidor en ángulo.
        val cotasH = listOf(sq, paflon)
        val etiquetas = mapOf("gen" to "${dd(d.ancho)} x ${dd(d.alto)}")
        val altoCont = 2400f
        val anchoCont = altoCont * (d.ancho / d.alto) + 700f
        return DibujoPuerta.generarBitmapPuerta(
            context = context, marcoCm = d.marco, puenteCm = d.puente,
            anchoPuertaCm = d.ancho, altoPuertaCm = d.alto,
            anchoHojaCm = anchoHojaCm, altoHojaCm = altoHojaCm,
            numeroZocalos = nZ, numeroDivisiones = nDiv,
            anchoContenedor = anchoCont, altoContenedor = altoCont,
            tipoDivision = "VICKY", anguloGrados = 0f,
            marcoCmIzq = marcoIzq, marcoCmDer = marcoDer, pisoCm = gapPisoCm,
            interiorCm = d.interior,
            etiquetas = etiquetas, cotas = cotas, cotasH = cotasH,
            alturaPuente = altoHojaCm, escalaInterna = 0.84f
        )
    }

    // Plano de Adel. Dos reglas, las mismas que Mari y Viky:
    //  - vertical (izquierda): zócalos y las barras de las divisiones, acumuladas desde la base. Se
    //    reparten igual que en Mari h, así que sirven las cotas de cotasPanos.
    //  - horizontal (abajo): dónde arranca el separador —o sea el ancho de la columna de relleno— y
    //    el ancho interior total. El otro canto del separador no se marca: mide lo que el bastidor.
    private fun planoAdel(context: Context, d: PuertaDescriptor.Datos): Bitmap {
        val bastidor = CalculosPuerta.BASTIDOR
        val hojaRef = CalculosPuerta.HOJA_REF
        val altoHojaCm = CalculosPuerta.hPuente(d.alto, d.hoja, d.piso, hojaRef, d.marco)
        val gapPisoCm = altoHojaCm - CalculosPuerta.parante(altoHojaCm, d.piso)
        val marcoIzq = if (d.ventanaIzq) 2.5f else d.marco
        val marcoDer = if (d.ventanaDer) 2.5f else d.marco
        val anchoHojaCm = CalculosPuerta.anchoHoja(d.ancho, marcoIzq, marcoDer, d.holgura)
        val nZ = CalculosPuerta.nZocalo(d.zocalos)
        val parante = CalculosPuerta.parante(altoHojaCm, d.piso)
        val paflon = anchoHojaCm - 2f * bastidor
        val nDiv = d.divisiones.takeIf { it >= 1 } ?: 3
        val altoCont = 2400f
        val anchoCont = altoCont * (d.ancho / d.alto) + 700f
        return DibujoPuerta.generarBitmapAdel(
            context = context, marcoCm = d.marco, puenteCm = d.puente,
            anchoCm = d.ancho, altoCm = d.alto, altoHojaCm = altoHojaCm,
            anchoContenedor = anchoCont, altoContenedor = altoCont,
            nDivisiones = nDiv, variante = d.variante, nZocalo = nZ, pisoCm = gapPisoCm,
            cotas = CalculosPuerta.cotasPanos(parante, nZ, nDiv, bastidor),
            cotasH = listOf(CalculosPuerta.anchoRellenoAdel(paflon, bastidor), paflon),
            escalaInterna = 0.84f
        )
    }

    // Plano de "Lina b": el interior del bastidor. Muestra el parante que separa las dos columnas y
    // los rellenos horizontales que caen detrás de cada junta de panel, sin dibujar las planchas.
    //
    // La regla de la izquierda lleva las alturas de esos rellenos desde la base y la de abajo, dónde
    // arranca el parante interior y el ancho del vacío.
    private fun planoLinaB(context: Context, d: PuertaDescriptor.Datos): Bitmap {
        val bastidor = CalculosPuerta.BASTIDOR
        val relleno = CalculosPuerta.TUBO_TALY   // 3.8, el mismo valor por omisión del campo Relleno
        val gruna = 0.8f
        val nDiv = d.divisiones.takeIf { it >= 1 } ?: 5
        val altoHojaCm = CalculosLina.hojaHConPiso(d.alto, d.hoja, d.piso, false, d.marco)
        val alturas = CalculosLina.alturasRellenoB(altoHojaCm, nDiv, gruna, d.piso)
        val parante = CalculosLina.parantePosicionB(d.ancho, d.marco, gruna, relleno, 0f, bastidor, d.holgura)
        val vacioAncho = CalculosPuerta.anchoHoja(d.ancho, d.marco, d.marco, d.holgura) - 2f * bastidor
        val altoCont = 2400f
        val anchoCont = altoCont * (d.ancho / d.alto) + 700f
        return DibujoPuerta.generarBitmapLinaEstructura(
            context = context, anchoCm = d.ancho, altoCm = d.alto, altoHojaCm = altoHojaCm,
            anchoContenedor = anchoCont, altoContenedor = altoCont,
            marcoCm = d.marco, pisoCm = d.piso, puenteCm = d.puente,
            bastidorCm = bastidor, rellenoCm = relleno,
            paranteDesdeIzq = parante, alturasRelleno = alturas,
            cotas = listOf(bastidor) + alturas + listOf(bastidor + (CalculosLina.altoBastidor(altoHojaCm, d.piso) - 2f * bastidor)),
            cotasH = listOf(parante, parante + relleno, vacioAncho)
        )
    }

    // Plano de "Lina c". La puerta es contraplacada: la plancha tapa todo, así que el plano no la
    // dibuja y muestra la estructura de adentro —el bastidor y sus travesaños—, que es lo único que
    // el técnico no puede ver una vez armada. La regla lleva sus alturas desde la base.
    private fun planoLinaC(context: Context, d: PuertaDescriptor.Datos): Bitmap {
        val estructura = CalculosPuerta.TUBO_TALY  // 3.8 por omisión; el selector de Lina puede cambiarlo
        val altoHojaCm = CalculosLina.hojaHConPiso(d.alto, d.hoja, d.piso, false, d.marco)
        val travesanos = (CalculosLina.divisionesContraplacado(
            CalculosLina.interiorContraplacado(altoHojaCm, d.ancho, d.marco, d.piso, d.holgura).second, estructura
        ) - 1).coerceAtLeast(0)
        val altoCont = 2400f
        val anchoCont = altoCont * (d.ancho / d.alto) + 700f
        return DibujoPuerta.generarBitmapLinaH(
            context = context, anchoCm = d.ancho, altoCm = d.alto, altoHojaCm = altoHojaCm,
            nPaneles = 1, anchoContenedor = anchoCont, altoContenedor = altoCont,
            marcoCm = d.marco, pisoCm = d.piso, puenteCm = d.puente,
            panelCompleto = true, estructuraCm = estructura, travesanos = travesanos,
            cotas = CalculosLina.cotasContraplacado(altoHojaCm, d.ancho, d.marco, d.piso, estructura, holgura = d.holgura),
            escalaInterna = 0.84f
        )
    }

    // Plano de Taly. La regla izquierda lleva el zócalo, los dos horizontales que cierran el vacío y
    // las divisiones; en "Taly d" esas divisiones son los cruces de cada diagonal contra el parante
    // izquierdo, y los del derecho van en la regla de ese lado. La de abajo marca dónde empieza y
    // termina el vidrio —o sea cuánto ocupan los parantes interiores— y el ancho interior.
    private fun planoTaly(context: Context, d: PuertaDescriptor.Datos): Bitmap {
        val bastidor = CalculosPuerta.BASTIDOR
        val hojaRef = CalculosPuerta.HOJA_REF
        val altoHojaCm = CalculosPuerta.hPuente(d.alto, d.hoja, d.piso, hojaRef, d.marco)
        val gapPisoCm = altoHojaCm - CalculosPuerta.parante(altoHojaCm, d.piso)
        val marcoIzq = if (d.ventanaIzq) 2.5f else d.marco
        val marcoDer = if (d.ventanaDer) 2.5f else d.marco
        val anchoHojaCm = CalculosPuerta.anchoHoja(d.ancho, marcoIzq, marcoDer, d.holgura)
        val nZ = CalculosPuerta.nZocalo(d.zocalos)
        val parante = CalculosPuerta.parante(altoHojaCm, d.piso)
        val paflon = anchoHojaCm - 2f * bastidor
        val nDiv = d.divisiones.takeIf { it >= 1 } ?: 1
        val angulo = if (d.variante == "Taly d") d.angulo else 0f
        val zonaAncho = CalculosPuerta.zonaVidrioTaly(paflon, bastidor).anchoVidrio
        val (izquierda, derecha) = CalculosPuerta.cotasPanosTaly(parante, nZ, nDiv, bastidor, zonaAncho, angulo)
        val altoCont = 2400f
        val anchoCont = altoCont * (d.ancho / d.alto) + 700f
        return DibujoPuerta.generarBitmapTaly(
            context = context, marcoCm = d.marco, puenteCm = d.puente,
            anchoPuertaCm = d.ancho, altoPuertaCm = d.alto,
            anchoHojaCm = anchoHojaCm, altoHojaCm = altoHojaCm,
            anchoContenedor = anchoCont, altoContenedor = altoCont,
            numeroDivisiones = d.divisiones, anguloGrados = angulo,
            marcoCmIzq = marcoIzq, marcoCmDer = marcoDer, nZocalo = nZ, pisoCm = gapPisoCm,
            cotas = izquierda,
            cotasDer = derecha.takeIf { it.isNotEmpty() },
            cotasH = CalculosPuerta.cotasParantesTaly(paflon, bastidor),
            escalaInterna = 0.84f
        )
    }

    // Plano de Jeny, con tres reglas porque la rejilla no cabe en dos:
    //  - izquierda: el zócalo, el arranque de cada paflón divisor y las acostadas de las columnas
    //    impares (en "Jeny r", el arranque y el final del recuadro);
    //  - derecha: el segundo juego de alturas, que en "Jeny c" son las acostadas de las columnas
    //    pares —van corridas respecto de las otras— y en "Jeny r" los amarres del costado. En la
    //    variante base queda vacía, porque todas las columnas van a la misma altura;
    //  - abajo: dónde va cada pieza de pie y el ancho interior.
    private fun planoJeny(context: Context, d: PuertaDescriptor.Datos): Bitmap {
        val bastidor = CalculosPuerta.BASTIDOR
        val hojaRef = CalculosPuerta.HOJA_REF
        val altoHojaCm = CalculosPuerta.hPuente(d.alto, d.hoja, d.piso, hojaRef, d.marco)
        val gapPisoCm = altoHojaCm - CalculosPuerta.parante(altoHojaCm, d.piso)
        val marcoIzq = if (d.ventanaIzq) 2.5f else d.marco
        val marcoDer = if (d.ventanaDer) 2.5f else d.marco
        val anchoHojaCm = CalculosPuerta.anchoHoja(d.ancho, marcoIzq, marcoDer, d.holgura)
        val nZ = CalculosPuerta.nZocalo(d.zocalos)
        val parante = CalculosPuerta.parante(altoHojaCm, d.piso)
        val paflon = anchoHojaCm - 2f * bastidor
        val nDiv = d.divisiones.takeIf { it >= 1 } ?: 1
        val (acostadasIzq, acostadasDer) = CalculosPuerta.cotasAcostadasJeny(
            d.variante, parante, nZ, nDiv, d.rejillaFilas, bastidor
        )
        val cotasHorizontales = CalculosPuerta.cotasRejillaJeny(d.variante, paflon, d.rejillaCols)
        val altoCont = 2400f
        val anchoCont = altoCont * (d.ancho / d.alto) + 700f
        return DibujoPuerta.generarBitmapJeny(
            context = context, marcoCm = d.marco, puenteCm = d.puente,
            anchoCm = d.ancho, altoCm = d.alto, altoHojaCm = altoHojaCm,
            nDivisiones = nDiv, rejillaCols = d.rejillaCols, rejillaFilas = d.rejillaFilas,
            variante = d.variante,
            anchoContenedor = anchoCont, altoContenedor = altoCont,
            nZocalo = nZ, pisoCm = gapPisoCm,
            cotas = (CalculosPuerta.cotasPanos(parante, nZ, nDiv, bastidor) + acostadasIzq).sorted(),
            cotasDer = acostadasDer.takeIf { it.isNotEmpty() },
            cotasH = cotasHorizontales.first,
            cotasHDer = cotasHorizontales.second.takeIf { it.isNotEmpty() },
            rotulos = if (d.variante == "Jeny r") {
                val altoPano = CalculosPuerta.altoPano(
                    CalculosPuerta.paranteInterno(parante, nZ, bastidor), nDiv, bastidor
                )
                val amarres = CalculosPuerta.amarresJenyR(paflon, altoPano)
                mapOf(
                    "amarreBajo" to dd(amarres.costadoBajo),
                    "amarreAlto" to dd(amarres.costadoAlto),
                    "amarreEje" to dd(amarres.dePie)
                )
            } else null,
            escalaInterna = 0.84f
        )
    }

    // Plano de Mili: el molinete. La regla vertical marca el zócalo y el arranque de cada tubo
    // horizontal; la horizontal, el arranque de cada tubo vertical y el ancho interior. En los dos
    // casos se marca un solo canto: el otro está a un tubo de distancia.
    private fun planoMili(context: Context, d: PuertaDescriptor.Datos): Bitmap {
        val bastidor = CalculosPuerta.BASTIDOR
        val hojaRef = CalculosPuerta.HOJA_REF
        val altoHojaCm = CalculosPuerta.hPuente(d.alto, d.hoja, d.piso, hojaRef, d.marco)
        val gapPisoCm = altoHojaCm - CalculosPuerta.parante(altoHojaCm, d.piso)
        val marcoIzq = if (d.ventanaIzq) 2.5f else d.marco
        val marcoDer = if (d.ventanaDer) 2.5f else d.marco
        val anchoHojaCm = CalculosPuerta.anchoHoja(d.ancho, marcoIzq, marcoDer, d.holgura)
        val nZ = CalculosPuerta.nZocalo(d.zocalos)
        val parante = CalculosPuerta.parante(altoHojaCm, d.piso)
        val paflon = anchoHojaCm - 2f * bastidor
        val altoCont = 2400f
        val anchoCont = altoCont * (d.ancho / d.alto) + 700f
        return DibujoPuerta.generarBitmapMili(
            context = context, marcoCm = d.marco, puenteCm = d.puente,
            anchoCm = d.ancho, altoCm = d.alto, altoHojaCm = altoHojaCm,
            anchoContenedor = anchoCont, altoContenedor = altoCont,
            nZocalo = nZ, pisoCm = gapPisoCm,
            cotas = CalculosPuerta.cotasPanosMili(parante, nZ, bastidor),
            cotasH = CalculosPuerta.cotasColumnasMili(paflon),
            escalaInterna = 0.84f
        )
    }

    // Plano de "Viky c": la cuadrícula pareja. Mismas dos reglas que Viky:
    //  - vertical (izquierda): alturas acumuladas de los divisores horizontales + zócalo. Sirven las
    //    de cotasPanos tal cual, porque las filas se reparten igual que en Mari h.
    //  - horizontal (abajo): el divisor vertical (= ancho de columna) y el ancho interior total.
    //    El otro canto del divisor no se marca: a 90° mide lo que el bastidor.
    private fun planoVikyC(context: Context, d: PuertaDescriptor.Datos): Bitmap {
        val bastidor = CalculosPuerta.BASTIDOR
        val hojaRef = CalculosPuerta.HOJA_REF
        val altoHojaCm = CalculosPuerta.hPuente(d.alto, d.hoja, d.piso, hojaRef, d.marco)
        val gapPisoCm = altoHojaCm - CalculosPuerta.parante(altoHojaCm, d.piso)
        val marcoIzq = if (d.ventanaIzq) 2.5f else d.marco
        val marcoDer = if (d.ventanaDer) 2.5f else d.marco
        val anchoHojaCm = CalculosPuerta.anchoHoja(d.ancho, marcoIzq, marcoDer, d.holgura)
        val nZ = CalculosPuerta.nZocalo(d.zocalos)
        val parante = CalculosPuerta.parante(altoHojaCm, d.piso)
        val paflon = anchoHojaCm - 2f * bastidor
        val nDiv = d.divisiones
        val anchoCol = CalculosPuerta.anchoColumnaVikyC(paflon, d.interior)
        val cotas = CalculosPuerta.cotasPanos(parante, nZ, nDiv, bastidor, d.interior)
        val cotasH = listOf(anchoCol, paflon)
        val etiquetas = mapOf("gen" to "${dd(d.ancho)} x ${dd(d.alto)}")
        val altoCont = 2400f
        val anchoCont = altoCont * (d.ancho / d.alto) + 700f
        return DibujoPuerta.generarBitmapPuerta(
            context = context, marcoCm = d.marco, puenteCm = d.puente,
            anchoPuertaCm = d.ancho, altoPuertaCm = d.alto,
            anchoHojaCm = anchoHojaCm, altoHojaCm = altoHojaCm,
            numeroZocalos = nZ, numeroDivisiones = nDiv,
            anchoContenedor = anchoCont, altoContenedor = altoCont,
            tipoDivision = "VICKYC", anguloGrados = 0f,
            marcoCmIzq = marcoIzq, marcoCmDer = marcoDer, pisoCm = gapPisoCm,
            interiorCm = d.interior,
            etiquetas = etiquetas, cotas = cotas, cotasH = cotasH,
            alturaPuente = altoHojaCm, escalaInterna = 0.84f
        )
    }
}
