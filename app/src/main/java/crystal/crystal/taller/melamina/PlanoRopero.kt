package crystal.crystal.taller.melamina

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.min

/**
 * El plano técnico del ropero, en láminas A4 apaisadas (842 x 595 puntos), para el taller y el
 * cliente. Se pinta sobre cualquier lienzo: la pantalla lo rasteriza para verlo con zoom y el PDF
 * lo recibe tal cual, en vectores.
 *
 * 1. Conjunto: alzado con puertas, alzado interior y corte lateral, a una escala normalizada
 *    (1:10, 1:20, 1:25…), con sus cotas y el cajetín.
 * 2. Despiece: cada pieza numerada con su medida (ancho x alto, como sale de [RoperoCalculo]),
 *    cantidad y material, sus cantos pintados donde van (y de qué tapacanto) y la veta a lo alto.
 * 3. Resumen: la lista de corte con los mismos números, los tapacantos y los accesorios.
 *
 * Todo sale del mismo cálculo que la lista de materiales: lo que se ve es lo que se corta.
 */
object PlanoRopero {

    const val ANCHO_PT = 842f
    const val ALTO_PT = 595f
    private const val MARGEN = 22f
    private const val CAJETIN = 58f
    private const val PT_POR_CM = 72f / 2.54f
    private val ESCALAS = listOf(5, 10, 15, 20, 25, 30, 40, 50, 60, 75, 100, 150)
    private const val COLUMNAS_DESPIECE = 4
    private const val FILAS_DESPIECE = 3

    /** Lo que va en el cajetín, además de lo que sale del ropero. */
    data class Encabezado(val codigo: String = "", val proyecto: String = "", val cliente: String = "")

    /** Una pieza del despiece con su número, el mismo en el despiece y en la lista de corte. */
    data class PiezaNumerada(val numero: Int, val pieza: PiezaMelamina)

    private const val COLOR_COTA = "#0D47A1"
    private val COLOR_CANTO = mapOf(
        TipoCanto.FINO to Color.parseColor("#616161"),
        TipoCanto.FINO_COLOR to Color.parseColor("#EF6C00"),
        TipoCanto.GRUESO to Color.parseColor("#C62828")
    )

    // ==================== Las láminas ====================

    /** Las piezas en el orden del plano: por material y de mayor a menor, numeradas desde 1. */
    fun piezasNumeradas(m: MaterialesRopero): List<PiezaNumerada> =
        m.piezas.sortedWith(compareBy<PiezaMelamina> { it.material.ordinal }.thenByDescending { it.anchoMm.toLong() * it.altoMm })
            .mapIndexed { i, p -> PiezaNumerada(i + 1, p) }

    private fun laminasDespiece(m: MaterialesRopero): Int =
        ceil(m.piezas.size / (COLUMNAS_DESPIECE * FILAS_DESPIECE).toFloat()).toInt().coerceAtLeast(1)

    /** Cuántas láminas tiene el plano: el conjunto, las del despiece y el resumen. */
    fun laminas(m: MaterialesRopero): Int = 1 + laminasDespiece(m) + 1

    /** La escala normalizada a la que entra el conjunto (1:N): la más grande que quepa. */
    fun escala(r: Ropero): Int {
        // Lo que queda para las tres vistas: cuatro huecos de 42 para las cotas de los lados, y
        // arriba y abajo el título de cada vista y dos filas de cotas.
        val anchoUtil = ANCHO_PT - 2 * MARGEN - 4 * 42f
        val altoUtil = ALTO_PT - 2 * MARGEN - CAJETIN - 58f - 34f
        val cabe = min(anchoUtil / (2 * r.anchoCm + r.fondoCm), altoUtil / r.altoMayorCm)
        return ESCALAS.firstOrNull { PT_POR_CM / it <= cabe } ?: ESCALAS.last()
    }

    /** Pinta la lámina [indice] (desde 0) en [c], que mide [ANCHO_PT] x [ALTO_PT]. */
    fun dibujarLamina(c: Canvas, indice: Int, r: Ropero, m: MaterialesRopero, enc: Encabezado) {
        c.drawColor(Color.WHITE)
        val total = laminas(m)
        val nDespiece = laminasDespiece(m)
        val titulo = when {
            indice == 0 -> "CONJUNTO"
            indice <= nDespiece -> "DESPIECE" + if (nDespiece > 1) " ${indice}/$nDespiece" else ""
            else -> "LISTA DE CORTE, CANTOS Y ACCESORIOS"
        }
        marco(c)
        cajetin(c, r, enc, titulo, indice + 1, total)
        when {
            indice == 0 -> conjunto(c, r)
            indice <= nDespiece -> despiece(c, r, m, indice - 1)
            else -> resumen(c, r, m)
        }
    }

    // ==================== Marco y cajetín ====================

    private fun marco(c: Canvas) {
        c.drawRect(MARGEN, MARGEN, ANCHO_PT - MARGEN, ALTO_PT - MARGEN, trazo(1.2f, Color.BLACK))
    }

    private fun cajetin(c: Canvas, r: Ropero, enc: Encabezado, titulo: String, lamina: Int, total: Int) {
        val y0 = ALTO_PT - MARGEN - CAJETIN
        val x0 = MARGEN
        val x1 = ANCHO_PT - MARGEN
        val borde = trazo(1f, Color.BLACK)
        c.drawLine(x0, y0, x1, y0, borde)
        // Columnas: marca | producto | proyecto y cliente | medidas y material | escala y fecha | lámina.
        val cortes = listOf(0f, 120f, 300f, 450f, 640f, 730f, x1 - x0)
        cortes.drop(1).dropLast(1).forEach { cx -> c.drawLine(x0 + cx, y0, x0 + cx, ALTO_PT - MARGEN, borde) }
        val mitad = y0 + CAJETIN / 2f
        (2 until cortes.size - 1).forEach { i -> c.drawLine(x0 + cortes[i], mitad, x0 + cortes[i + 1], mitad, trazo(0.5f, Color.GRAY)) }
        fun celda(i: Int, arriba: Boolean, rotulo: String, valor: String) {
            val cx = x0 + cortes[i] + 5f
            val base = if (arriba) y0 else mitad
            c.drawText(rotulo, cx, base + 9f, texto(6.5f, Color.GRAY))
            c.drawText(recortar(valor, cortes[i + 1] - cortes[i] - 10f, 9f), cx, base + 22f, texto(9f, Color.BLACK, negrita = true))
        }
        c.drawText("CRYSTAL", x0 + 8f, y0 + 24f, texto(16f, Color.parseColor("#1565C0"), negrita = true))
        c.drawText("Plano técnico", x0 + 8f, y0 + 40f, texto(9f, Color.DKGRAY))
        c.drawText("Ropero de melamina", x0 + cortes[1] + 5f, y0 + 18f, texto(11f, Color.BLACK, negrita = true))
        c.drawText(titulo, x0 + cortes[1] + 5f, y0 + 34f, texto(8.5f, Color.DKGRAY))
        if (enc.codigo.isNotBlank()) c.drawText("Código ${enc.codigo}", x0 + cortes[1] + 5f, y0 + 48f, texto(8.5f, Color.DKGRAY))
        celda(2, true, "PROYECTO", enc.proyecto.ifBlank { "—" })
        celda(2, false, "CLIENTE", enc.cliente.ifBlank { "—" })
        celda(3, true, "MEDIDAS (ANCHO x ALTO x FONDO)", "${fmt(r.anchoCm)} x ${fmt(r.altoMayorCm)} x ${fmt(r.fondoCm)} cm")
        celda(3, false, "MATERIAL", "Melamina ${r.espesorMm} mm" + if (r.interiorBlanco) ", interior blanco" else "")
        celda(4, true, "ESCALA", "1:${escala(r)} (A4)")
        celda(4, false, "FECHA", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
        celda(5, true, "LÁMINA", "$lamina de $total")
        celda(5, false, "UNIDAD", "cm")
    }

    // ==================== Lámina 1: el conjunto ====================

    private fun conjunto(c: Canvas, r: Ropero) {
        val n = escala(r)
        val s = PT_POR_CM / n
        // El bloque (título, vistas y dos filas de cotas) centrado en lo que deja el cajetín.
        val arribaUtil = MARGEN + 8f
        val abajoUtil = ALTO_PT - MARGEN - CAJETIN - 6f
        val altoBloque = r.altoMayorCm * s + 70f
        val abajo = arribaUtil + 32f + r.altoMayorCm * s + ((abajoUtil - arribaUtil) - altoBloque) / 2f
        val anchoVista = r.anchoCm * s
        // Tres vistas a lo ancho, cada una con su sitio para cotas a los lados.
        val libre = ANCHO_PT - 2 * MARGEN - (2 * anchoVista + r.fondoCm * s)
        val hueco = libre / 4f
        val xExterior = MARGEN + hueco
        val xInterior = xExterior + anchoVista + hueco
        val xCorte = xInterior + anchoVista + hueco
        val dibujo = RoperoDibujo(0.55f)
        dibujo.dibujar(c, r, xExterior, abajo, s, mostrarPuertas = true, conRotulos = false)
        dibujo.dibujar(c, r, xInterior, abajo, s, mostrarPuertas = false, conRotulos = false)
        corteLateral(c, r, xCorte, abajo, s)

        val arriba = abajo - r.altoMayorCm * s
        tituloVista(c, "ALZADO CON PUERTAS", xExterior, xExterior + anchoVista, arriba)
        tituloVista(c, "ALZADO INTERIOR", xInterior, xInterior + anchoVista, arriba)
        tituloVista(c, "CORTE LATERAL A-A", xCorte, xCorte + r.fondoCm * s, arriba)
        cotasExterior(c, r, xExterior, abajo, s)
        cotasInterior(c, r, xInterior, abajo, s)
        // Por dónde pasa el corte: una línea de trazos sobre el alzado interior, en el primer cuerpo.
        val (izq, der) = RoperoGeometria.cuerposX(r).first()
        val xa = xInterior + (izq + (der - izq) * 0.3f) * s
        val trazos = trazo(0.8f, Color.parseColor("#6A1B9A")).apply { pathEffect = DashPathEffect(floatArrayOf(6f, 3f, 1.5f, 3f), 0f) }
        c.drawLine(xa, arriba - 8f, xa, abajo, trazos)
        c.drawText("A", xa - 3f, arriba - 10f, texto(8f, Color.parseColor("#6A1B9A"), negrita = true))
    }

    private fun tituloVista(c: Canvas, t: String, x0: Float, x1: Float, arriba: Float) {
        val p = texto(8.5f, Color.BLACK, negrita = true).apply { textAlign = Paint.Align.CENTER }
        c.drawText(t, (x0 + x1) / 2f, arriba - 22f, p)
    }

    private fun cotasExterior(c: Canvas, r: Ropero, x0: Float, abajo: Float, s: Float) {
        fun x(cm: Float) = x0 + cm * s
        fun y(cm: Float) = abajo - cm * s
        val hojas = RoperoPuertas.de(r).hojas
        // Las hojas batientes de abajo, una cota por hoja (lo que mide terminada, con su canto).
        val baja = hojas.filter { it.clase == ClaseDePuerta.PUERTA }.distinctBy { (it.x0 * 10).toInt() to (it.x1 * 10).toInt() }.sortedBy { it.x0 }
        if (baja.isNotEmpty() && r.puertas == TipoPuertas.BATIENTES) {
            baja.forEach { h -> cotaH(c, x(h.x0), x(h.x1), abajo + 14f, fmt(h.ancho), abajo) }
        }
        cotaH(c, x(0f), x(r.anchoCm), abajo + 30f, fmt(r.anchoCm), abajo)
        cotaV(c, y(0f), y(r.altoMayorCm), x0 - 14f, fmt(r.altoMayorCm), x0)
        if (r.zocaloCm > 0.5f) cotaV(c, y(0f), y(r.zocaloCm), x(r.anchoCm) + 12f, fmt(r.zocaloCm), x(r.anchoCm))
    }

    private fun cotasInterior(c: Canvas, r: Ropero, x0: Float, abajo: Float, s: Float) {
        fun x(cm: Float) = x0 + cm * s
        fun y(cm: Float) = abajo - cm * s
        RoperoGeometria.cuerposX(r).forEach { (izq, der) -> cotaH(c, x(izq), x(der), abajo + 14f, fmt(der - izq), abajo) }
        cotaH(c, x(0f), x(r.anchoCm), abajo + 30f, fmt(r.anchoCm), abajo)
        cotaV(c, y(0f), y(r.altoMayorCm), x0 - 14f, fmt(r.altoMayorCm), x0)
        // A la derecha: el zócalo (con el piso), lo libre del cuerpo y el maletero.
        val xd = x(r.anchoCm) + 12f
        val piso = RoperoGeometria.pisoY(r)
        val tope = RoperoGeometria.topeBajo(r)
        cotaV(c, y(0f), y(piso), xd, fmt(piso), x(r.anchoCm))
        cotaV(c, y(piso), y(tope), xd, fmt(tope - piso), x(r.anchoCm))
        if (r.maleteroCm > 0f) cotaV(c, y(tope + r.espesorCm), y(RoperoGeometria.techoY(r)), xd, fmt(r.maleteroCm), x(r.anchoCm))
    }

    /**
     * El corte por el primer cuerpo, visto desde el costado (la pared a la izquierda, el frente a
     * la derecha): el fondo de nordex, piso y techo cortados, el lateral que queda detrás, la
     * puerta y el zócalo delante.
     */
    private fun corteLateral(c: Canvas, r: Ropero, x0: Float, abajo: Float, s: Float) {
        fun x(cm: Float) = x0 + cm * s
        fun y(cm: Float) = abajo - cm * s
        val e = r.espesorCm
        val nordex = r.fondoNordexCm
        val arm = r.fondoArmazonCm
        val frente = nordex + arm
        val alto = r.altoDeCuerpo(0)
        val linea = trazo(0.9f, Color.BLACK)
        val fina = trazo(0.5f, Color.DKGRAY)
        val seccion = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#9E9E9E"); style = Paint.Style.FILL }
        val puerta = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#D7C4A8"); style = Paint.Style.FILL }
        // El lateral de detrás, solo su contorno.
        c.drawRect(x(nordex), y(alto), x(frente), y(0f), fina)
        // El fondo de nordex.
        if (nordex > 0f) { val rf = RectF(x(0f), y(alto), x(nordex), y(r.bajoPisoCm)); c.drawRect(rf, puerta); c.drawRect(rf, fina) }
        // Piso, techo y repisa del maletero: cortados, rayados.
        fun tablaCortada(y0: Float, y1: Float, desde: Float = nordex, hasta: Float = frente) {
            val rf = RectF(x(desde), y(y1), x(hasta), y(y0))
            c.drawRect(rf, seccion)
            rayado(c, rf)
            c.drawRect(rf, linea)
        }
        tablaCortada(r.bajoPisoCm, r.pisoArribaCm)
        tablaCortada(alto - e, alto)
        if (r.maleteroCm > 0f) { val t = RoperoGeometria.topeBajo(r); tablaCortada(t, t + e) }
        // Las repisas del primer cuerpo, al fondo útil.
        val h0 = RoperoGeometria.huecoDeCuerpo(r, 0)
        RoperoGeometria.alturasDeEntrepanos(r, r.cuerpos[0], h0).forEach { alt -> tablaCortada(h0.y0 + alt, h0.y0 + alt + e, nordex, nordex + r.fondoInteriorCm) }
        // Las hojas que caen en el corte (puertas, frentes de cajón, maletero), cada una con su alto
        // y su gruña: encima del armazón, o dentro si son interiores.
        if (r.puertas != TipoPuertas.CORREDIZAS) {
            val xc = (h0.x0 + h0.x0 + (h0.x1 - h0.x0) * 0.6f) / 2f
            val delante = !r.puertasInteriores
            RoperoPuertas.de(r).hojas.filter { it.clase != ClaseDePuerta.HOJA_CORREDIZA && xc in it.x0..it.x1 }.forEach { hoja ->
                val rf = if (delante) RectF(x(frente), y(hoja.y1), x(frente + e), y(hoja.y0))
                         else RectF(x(frente - e), y(hoja.y1), x(frente), y(hoja.y0))
                c.drawRect(rf, puerta); c.drawRect(rf, linea)
            }
        }
        if (r.zocaloCm > 0.5f) {
            val rf = if (r.zocaloDelante) RectF(x(frente), y(RoperoCalculo.altoZocalo(r) + r.tapacantoPuertasCm), x(frente + e), y(0f))
                     else RectF(x(frente - e), y(r.bajoPisoCm), x(frente), y(0f))
            c.drawRect(rf, puerta); c.drawRect(rf, linea)
        }
        // Cotas: el fondo repartido (nordex, armazón, puerta) y entero; el alto y el zócalo.
        val yc = abajo + 14f
        if (nordex > 0f) cotaH(c, x(0f), x(nordex), yc, fmt(nordex), abajo, chica = true)
        cotaH(c, x(nordex), x(frente), yc, fmt(arm), abajo)
        if (frente < r.fondoCm - 0.05f) cotaH(c, x(frente), x(r.fondoCm), yc, fmt(r.fondoCm - frente), abajo, chica = true)
        cotaH(c, x(0f), x(r.fondoCm), abajo + 30f, fmt(r.fondoCm), abajo)
        cotaV(c, y(0f), y(alto), x(0f) - 14f, fmt(alto), x(0f))
        c.drawText("pared", x(0f) + 2f, y(alto) - 4f, texto(6f, Color.GRAY))
        c.drawText("frente", x(r.fondoCm) - 18f, y(alto) - 4f, texto(6f, Color.GRAY))
    }

    private fun rayado(c: Canvas, rf: RectF) {
        val p = trazo(0.4f, Color.parseColor("#424242"))
        c.save()
        c.clipRect(rf)
        var d = -rf.height()
        while (d < rf.width()) { c.drawLine(rf.left + d, rf.bottom, rf.left + d + rf.height(), rf.top, p); d += 3f }
        c.restore()
    }

    // ==================== Láminas de despiece ====================

    private fun despiece(c: Canvas, r: Ropero, m: MaterialesRopero, pagina: Int) {
        val porPagina = COLUMNAS_DESPIECE * FILAS_DESPIECE
        val piezas = piezasNumeradas(m).drop(pagina * porPagina).take(porPagina)
        val top = MARGEN + 26f
        val abajo = ALTO_PT - MARGEN - CAJETIN - 6f
        val anchoCelda = (ANCHO_PT - 2 * MARGEN) / COLUMNAS_DESPIECE
        val altoCelda = (abajo - top) / FILAS_DESPIECE
        leyenda(c)
        piezas.forEachIndexed { i, pn ->
            val cx = MARGEN + (i % COLUMNAS_DESPIECE) * anchoCelda
            val cy = top + (i / COLUMNAS_DESPIECE) * altoCelda
            c.drawRect(cx, cy, cx + anchoCelda, cy + altoCelda, trazo(0.4f, Color.LTGRAY))
            celdaDePieza(c, pn, RectF(cx, cy, cx + anchoCelda, cy + altoCelda))
        }
    }

    private fun leyenda(c: Canvas) {
        var x = MARGEN + 8f
        val y = MARGEN + 15f
        c.drawText("Medidas de corte en cm, ancho x alto (el tapacanto ya descontado). Cantos:", x, y, texto(7.5f, Color.DKGRAY))
        x += 265f
        TipoCanto.values().forEach { t ->
            c.drawLine(x, y - 3f, x + 16f, y - 3f, trazo(3f, COLOR_CANTO.getValue(t)))
            c.drawText(t.etiqueta, x + 20f, y, texto(7.5f, Color.DKGRAY))
            x += 20f + texto(7.5f, Color.DKGRAY).measureText(t.etiqueta) + 14f
        }
        flechaVeta(c, x + 4f, y - 9f, y + 1f)
        c.drawText("veta", x + 10f, y, texto(7.5f, Color.DKGRAY))
    }

    private fun celdaDePieza(c: Canvas, pn: PiezaNumerada, celda: RectF) {
        val p = pn.pieza
        val ancho = p.anchoMm / 10f
        val alto = p.altoMm / 10f
        // Arriba: el número en un círculo, el nombre y la medida con la cantidad.
        val cx = celda.left + 14f
        val cy = celda.top + 13f
        c.drawCircle(cx, cy, 8.5f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0") })
        c.drawText("${pn.numero}", cx, cy + 3.2f, texto(8.5f, Color.WHITE, negrita = true).apply { textAlign = Paint.Align.CENTER })
        c.drawText(recortar(p.nombre, celda.width() - 34f, 8.5f), celda.left + 27f, celda.top + 11f, texto(8.5f, Color.BLACK, negrita = true))
        c.drawText("${fmt(ancho)} x ${fmt(alto)}   × ${p.cantidad}", celda.left + 27f, celda.top + 22f, texto(8.5f, Color.parseColor(COLOR_COTA)))
        // Abajo: el material y los cantos.
        val cantos = buildList {
            if (p.cantosEnAncho > 0) add("${p.cantosEnAncho} de ${fmt(ancho)}")
            if (p.cantosEnAlto > 0) add("${p.cantosEnAlto} de ${fmt(alto)}")
        }.joinToString(" + ")
        c.drawText(recortar(p.material.etiqueta, celda.width() - 12f, 7f), celda.left + 6f, celda.bottom - 14f, texto(7f, Color.DKGRAY))
        c.drawText(if (cantos.isBlank()) "sin canto" else "canto ${p.canto.etiqueta}: $cantos", celda.left + 6f, celda.bottom - 5f, texto(7f, Color.DKGRAY))
        // En medio: la pieza, ajustada a la celda (no va a escala), con sus cantos y la veta.
        val caja = RectF(celda.left + 18f, celda.top + 30f, celda.right - 18f, celda.bottom - 36f)
        val k = min(caja.width() / ancho, caja.height() / alto)
        val w = ancho * k
        val h = alto * k
        val rf = RectF(caja.centerX() - w / 2f, caja.centerY() - h / 2f, caja.centerX() + w / 2f, caja.centerY() + h / 2f)
        val relleno = when (p.material) {
            MaterialPlancha.MELAMINA_18_COLOR, MaterialPlancha.MELAMINA_15_COLOR -> "#E6D3B3"
            MaterialPlancha.NORDEX_3, MaterialPlancha.MDF_55 -> "#D7CCC8"
            else -> "#F5F5F5"
        }
        c.drawRect(rf, Paint().apply { color = Color.parseColor(relleno); style = Paint.Style.FILL })
        c.drawRect(rf, trazo(0.7f, Color.parseColor("#424242")))
        // Los cantos: los del ancho abajo (y arriba si son dos), los del alto a la izquierda (y a la derecha).
        val pc = trazo(3f, COLOR_CANTO.getValue(p.canto)).apply { strokeCap = Paint.Cap.BUTT }
        // Con ranura (las piezas de la caja), el canto va arriba, en el borde que se ve, y la ranura abajo.
        if (p.cantosEnAncho >= 1) { val yc = if (p.ranuraCm > 0f && p.cantosEnAncho == 1) rf.top else rf.bottom; c.drawLine(rf.left, yc, rf.right, yc, pc) }
        if (p.cantosEnAncho >= 2) c.drawLine(rf.left, rf.top, rf.right, rf.top, pc)
        if (p.cantosEnAlto >= 1) c.drawLine(rf.left, rf.top, rf.left, rf.bottom, pc)
        if (p.cantosEnAlto >= 2) c.drawLine(rf.right, rf.top, rf.right, rf.bottom, pc)
        // La ranura del fondo del cajón, a su distancia del canto de abajo.
        if (p.ranuraCm > 0f) {
            val yr = rf.bottom - p.ranuraCm * k
            c.drawLine(rf.left, yr, rf.right, yr, trazo(0.8f, Color.parseColor(COLOR_COTA)).apply { pathEffect = DashPathEffect(floatArrayOf(4f, 2f), 0f) })
        }
        // La veta a lo alto, en lo que es melamina.
        val esMelamina = p.material != MaterialPlancha.NORDEX_3 && p.material != MaterialPlancha.MDF_55
        if (esMelamina && h > 14f) flechaVeta(c, rf.centerX(), rf.centerY() - h * 0.3f, rf.centerY() + h * 0.3f)
        // Las medidas junto a la pieza.
        c.drawText(fmt(ancho), rf.centerX(), rf.bottom + 9f, texto(7f, Color.parseColor(COLOR_COTA)).apply { textAlign = Paint.Align.CENTER })
        c.save()
        c.rotate(-90f, rf.left - 4f, rf.centerY())
        c.drawText(fmt(alto), rf.left - 4f, rf.centerY(), texto(7f, Color.parseColor(COLOR_COTA)).apply { textAlign = Paint.Align.CENTER })
        c.restore()
    }

    private fun flechaVeta(c: Canvas, x: Float, y0: Float, y1: Float) {
        val p = trazo(0.9f, Color.parseColor("#8D6E63"))
        c.drawLine(x, y0, x, y1, p)
        val cab = Path().apply {
            moveTo(x - 2.5f, y0 + 4f); lineTo(x, y0); lineTo(x + 2.5f, y0 + 4f)
            moveTo(x - 2.5f, y1 - 4f); lineTo(x, y1); lineTo(x + 2.5f, y1 - 4f)
        }
        c.drawPath(cab, p)
    }

    // ==================== Lámina final: resumen ====================

    private fun resumen(c: Canvas, r: Ropero, m: MaterialesRopero) {
        val top = MARGEN + 16f
        val abajo = ALTO_PT - MARGEN - CAJETIN - 8f
        // A la izquierda, la lista de corte con los números del despiece.
        val x0 = MARGEN + 10f
        val cols = listOf(0f, 22f, 150f, 215f, 245f, 355f)
        var y = top + 4f
        val cab = texto(7.5f, Color.BLACK, negrita = true)
        listOf("N°", "Pieza", "Ancho x alto", "Cant.", "Material", "Cantos").forEachIndexed { i, t -> c.drawText(t, x0 + cols[i], y, cab) }
        y += 4f
        c.drawLine(x0, y, x0 + 470f, y, trazo(0.6f, Color.BLACK))
        val fila = texto(7.5f, Color.BLACK)
        val numeradas = piezasNumeradas(m)
        val alto = min(10f, (abajo - y - 6f) / numeradas.size.coerceAtLeast(1))
        numeradas.forEach { (n, p) ->
            y += alto
            val cantos = listOfNotNull(
                p.cantosEnAncho.takeIf { it > 0 }?.let { "$it×${fmt(p.anchoMm / 10f)}" },
                p.cantosEnAlto.takeIf { it > 0 }?.let { "$it×${fmt(p.altoMm / 10f)}" }
            ).joinToString(" + ").let { if (it.isBlank()) "—" else "$it ${p.canto.etiqueta}" }
            listOf("$n", recortar(p.nombre, 124f, 7.5f), "${fmt(p.anchoMm / 10f)} x ${fmt(p.altoMm / 10f)}", "${p.cantidad}",
                recortar(p.material.etiqueta.replace("Melamina", "Mel."), 106f, 7.5f), recortar(cantos, 115f, 7.5f))
                .forEachIndexed { i, t -> c.drawText(t, x0 + cols[i], y, fila) }
        }
        // A la derecha: tapacantos, accesorios y planchas.
        val xd = x0 + 490f
        var yd = top + 4f
        fun bloque(tituloBloque: String, lineas: String) {
            if (lineas.isBlank()) return
            c.drawText(tituloBloque, xd, yd, cab)
            yd += 11f
            lineas.lines().forEach { l -> if (yd < abajo) { c.drawText(recortar(l, 300f, 7.5f), xd + 6f, yd, fila); yd += 9.5f } }
            yd += 6f
        }
        bloque("${RoperoCalculo.nombreTapacanto(r)} (cm = cantidad)", m.lineasDeTapacanto())
        bloque("${RoperoCalculo.nombreTapacantoColor(r)} (cm = cantidad)", m.lineasDeTapacantoColor())
        bloque("${RoperoCalculo.nombreTapacantoPuertas(r)} (cm = cantidad)", m.lineasDeTapacantoPuertas())
        m.nombresConLargo().forEach { bloque("$it (cm = cantidad)", m.lineasConLargo(it)) }
        bloque("Accesorios", m.lineasDeAccesorios())
        bloque("Planchas (estimado con 15% de merma)", m.planchasEstimadas.entries.joinToString("\n") { (mat, n) -> "${mat.etiqueta}: $n de 244 x 183" })
    }

    // ==================== Cotas ====================

    /** Una cota horizontal de [xa] a [xb] a la altura [y], con líneas de referencia desde [desdeY]. */
    private fun cotaH(c: Canvas, xa: Float, xb: Float, y: Float, t: String, desdeY: Float, chica: Boolean = false) {
        val p = trazo(0.5f, Color.parseColor(COLOR_COTA))
        c.drawLine(xa, desdeY + 2f, xa, y + 3f, p)
        c.drawLine(xb, desdeY + 2f, xb, y + 3f, p)
        c.drawLine(xa, y, xb, y, p)
        marcaCota(c, xa, y, p); marcaCota(c, xb, y, p)
        val tp = texto(if (chica) 6f else 7f, Color.parseColor(COLOR_COTA)).apply { textAlign = Paint.Align.CENTER }
        // Si no cabe entre las marcas, sube un renglón para no pisarlas.
        val cabe = tp.measureText(t) + 6f <= kotlin.math.abs(xb - xa)
        c.drawText(t, (xa + xb) / 2f, if (cabe) y - 2f else y - 9f, tp)
    }

    /** Una cota vertical de [ya] a [yb] en la [x], con líneas de referencia desde [desdeX]. */
    private fun cotaV(c: Canvas, ya: Float, yb: Float, x: Float, t: String, desdeX: Float) {
        val p = trazo(0.5f, Color.parseColor(COLOR_COTA))
        val haciaFuera = if (x < desdeX) -1f else 1f
        c.drawLine(desdeX + 2f * haciaFuera, ya, x + 3f * haciaFuera, ya, p)
        c.drawLine(desdeX + 2f * haciaFuera, yb, x + 3f * haciaFuera, yb, p)
        c.drawLine(x, ya, x, yb, p)
        marcaCota(c, x, ya, p); marcaCota(c, x, yb, p)
        val tp = texto(7f, Color.parseColor(COLOR_COTA)).apply { textAlign = Paint.Align.CENTER }
        c.save()
        c.rotate(-90f, x - 2f, (ya + yb) / 2f)
        c.drawText(t, x - 2f, (ya + yb) / 2f, tp)
        c.restore()
    }

    /** La marca de cota de plano de taller: un trazo oblicuo. */
    private fun marcaCota(c: Canvas, x: Float, y: Float, p: Paint) = c.drawLine(x - 2.5f, y + 2.5f, x + 2.5f, y - 2.5f, p)

    // ==================== Pinceles y textos ====================

    private fun trazo(ancho: Float, color: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = ancho; this.color = color }

    private fun texto(tam: Float, color: Int, negrita: Boolean = false) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = tam; this.color = color
        typeface = if (negrita) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    }

    /** El texto cortado con "…" si no cabe en [ancho]. */
    private fun recortar(t: String, ancho: Float, tam: Float): String {
        val p = texto(tam, Color.BLACK)
        if (p.measureText(t) <= ancho) return t
        var s = t
        while (s.length > 1 && p.measureText("$s…") > ancho) s = s.dropLast(1)
        return "$s…"
    }

    private fun fmt(v: Float): String =
        if (kotlin.math.abs(v - Math.round(v)) < 0.05f) Math.round(v).toString() else String.format(Locale.US, "%.1f", v)
}
