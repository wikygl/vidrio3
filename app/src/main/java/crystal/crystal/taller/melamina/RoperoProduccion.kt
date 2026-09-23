package crystal.crystal.taller.melamina

import java.util.Locale
import kotlin.math.abs

/**
 * Lo que el taller necesita para producir el ropero sin dictar pieza por pieza:
 *
 * 1. CORTE Y ENCHAPE, como la hoja de Excel que se manda a la distribuidora: cantidad, ancho, alto,
 *    vetas, canto, ranura, router y etiqueta, más el código simbólico de cada pieza para que otra
 *    máquina lo lea: `1,30,63.2,v63.2,d30blanco,g30cedro,g63.2cedro,g63.2cedro,r1.8:63.2`
 *    (cantidad, ancho, alto, veta a lo largo de…, un canto por lado —delgado o grueso, el largo
 *    del lado y su color—, y la ranura a 1.8 del canto a lo largo de…).
 * 2. MARCAS Y AGUJEROS de cada tablero que recibe a otro: la marca (negro) es la cara de la pieza
 *    que llega y el agujero (rosado) va al centro de su espesor (repisa en 25 → agujero en 25.9);
 *    a lo hondo, dos agujeros a 5 cm de cada canto, o tres (y uno al centro) si la unión pasa de
 *    40. Los rieles de cajón al centro de cada cajón en su espacio, y las bisagras de cazoleta
 *    (7 cm de alto) a 10 cm de las puntas, corridas si chocan con una melamina.
 */
object RoperoProduccion {

    // ==================== Colores y vetas ====================

    /** El color de la melamina de la pieza: el de fuera, el de dentro, o el material si no es melamina. */
    fun colorDe(r: Ropero, material: MaterialPlancha): String = when {
        material == MaterialPlancha.NORDEX_3 || material == MaterialPlancha.MDF_55 -> material.etiqueta
        esDeColor(r, material) -> r.colorExterior.ifBlank { "Color" }
        else -> r.colorInterior.ifBlank { "Blanco" }
    }

    private fun esDeColor(r: Ropero, material: MaterialPlancha): Boolean =
        material == MaterialPlancha.MELAMINA_18_COLOR || material == MaterialPlancha.MELAMINA_15_COLOR ||
            (!r.interiorBlanco && (material == MaterialPlancha.MELAMINA_18 || material == MaterialPlancha.MELAMINA_15))

    /** Si la pieza lleva veta: la melamina que la tiene (el cedro sí, el blanco no); el nordex y el MDF, nunca. */
    fun tieneVeta(r: Ropero, material: MaterialPlancha): Boolean = when (material) {
        MaterialPlancha.NORDEX_3, MaterialPlancha.MDF_55 -> false
        else -> if (esDeColor(r, material)) r.vetaExterior else r.vetaInterior
    }

    /** El color del tapacanto: el fino de dentro, del color de dentro; el fino de color y el grueso, el de fuera (o el que se pidió). */
    fun colorDeCanto(r: Ropero, canto: TipoCanto): String = when (canto) {
        TipoCanto.FINO -> r.colorInterior.ifBlank { "Blanco" }
        else -> r.colorTapacanto.ifBlank { r.colorExterior.ifBlank { "Color" } }
    }

    /** El nombre de la melamina como encabeza su bloque en la hoja: "melamina Cedro 18mm". */
    fun nombreDeMaterial(r: Ropero, material: MaterialPlancha): String = when (material) {
        MaterialPlancha.NORDEX_3 -> "mdf o nordex 3mm"
        MaterialPlancha.MDF_55 -> "mdf 5.5mm"
        MaterialPlancha.MELAMINA_15, MaterialPlancha.MELAMINA_15_COLOR -> "melamina ${colorDe(r, material)} 15mm"
        else -> "melamina ${colorDe(r, material)} 18mm"
    }

    // ==================== Corte y enchape ====================

    /** Una pieza como va a la distribuidora: las columnas de la hoja de corte y su código. */
    data class FichaCorte(
        val numero: Int,
        val pieza: PiezaMelamina,
        val material: String,
        val vetas: String,
        val canto: String,
        val ranura: String,
        val router: String,
        val etiqueta: String,
        val codigo: String
    ) {
        val anchoCm: Float get() = pieza.anchoMm / 10f
        val altoCm: Float get() = pieza.altoMm / 10f
    }

    /** Las piezas del mueble con los mismos números del plano (despiece y lista de corte). */
    fun fichasDeCorte(r: Ropero, m: MaterialesRopero, codigoMueble: String): List<FichaCorte> =
        PlanoRopero.piezasNumeradas(m).map { (n, p) ->
            val ancho = p.anchoMm / 10f
            val alto = p.altoMm / 10f
            val veta = tieneVeta(r, p.material)
            FichaCorte(
                numero = n,
                pieza = p,
                material = nombreDeMaterial(r, p.material),
                vetas = if (veta) fmt(alto) else "",
                canto = textoDeCanto(r, p),
                ranura = if (p.ranuraCm > 0f) "${fmt(p.ranuraCm)} en ${fmt(ancho)}, cajonería" else "",
                router = "",
                etiqueta = etiqueta(codigoMueble, p),
                codigo = codigoSimbolico(r, p)
            )
        }

    /** Mueble, zona y pieza: "Rm3 · cuerpo 2 · Entrepaño". */
    fun etiqueta(codigoMueble: String, p: PiezaMelamina): String =
        listOfNotNull(codigoMueble.takeIf { it.isNotBlank() }, p.zonas.joinToString(" / ").takeIf { it.isNotBlank() }, p.nombre).joinToString(" · ")

    /** Los lados con canto: primero los que corren a lo ancho, después los que corren a lo alto. */
    private fun ladosConCanto(p: PiezaMelamina): List<Float> =
        List(p.cantosEnAncho) { p.anchoMm / 10f } + List(p.cantosEnAlto) { p.altoMm / 10f }

    /**
     * El canto como se escribe en la hoja: "delgado en O" (todo el contorno), en C (dos cortos y un
     * largo), en U (dos largos y un corto), en L (un largo y un corto), o las medidas de los lados
     * ("delgado en 57.9", "grueso en 58.5, 58.5"). El color, solo si no es el de la melamina.
     */
    fun textoDeCanto(r: Ropero, p: PiezaMelamina): String {
        val lados = ladosConCanto(p)
        if (lados.isEmpty()) return ""
        val tipo = if (p.canto == TipoCanto.GRUESO) "grueso" else "delgado"
        val colorCanto = colorDeCanto(r, p.canto)
        val color = if (colorCanto.equals(colorDe(r, p.material), ignoreCase = true)) "" else " $colorCanto"
        val anchoLargo = p.anchoMm >= p.altoMm
        val largos = if (anchoLargo) p.cantosEnAncho else p.cantosEnAlto
        val cortos = if (anchoLargo) p.cantosEnAlto else p.cantosEnAncho
        val forma = when {
            largos == 2 && cortos == 2 -> "O"
            largos == 1 && cortos == 2 -> "C"
            largos == 2 && cortos == 1 -> "U"
            largos == 1 && cortos == 1 -> "L"
            else -> lados.joinToString(", ") { fmt(it) }
        }
        return "$tipo$color en $forma"
    }

    /** El código de la pieza para otra máquina: `cantidad,ancho,alto[,v<largo>],<canto por lado>…[,r<distancia>:<largo>]`. */
    fun codigoSimbolico(r: Ropero, p: PiezaMelamina): String {
        val ancho = p.anchoMm / 10f
        val alto = p.altoMm / 10f
        val partes = mutableListOf("${p.cantidad}", fmt(ancho), fmt(alto))
        if (tieneVeta(r, p.material)) partes.add("v${fmt(alto)}")
        val letra = if (p.canto == TipoCanto.GRUESO) "g" else "d"
        val color = colorDeCanto(r, p.canto).lowercase(Locale.ROOT).trim().replace(Regex("\\s+"), "_")
        ladosConCanto(p).forEach { partes.add("$letra${fmt(it)}$color") }
        if (p.ranuraCm > 0f) partes.add("r${fmt(p.ranuraCm)}:${fmt(ancho)}")
        return partes.joinToString(",")
    }

    /** Un mueble para la hoja de corte: su código (Rm3) y el ropero. */
    data class Mueble(val codigo: String, val ropero: Ropero)

    /** Las columnas de la hoja de corte: las de la hoja de Excel del taller, más la pieza y su código. */
    val COLUMNAS = listOf("cantidad", "ancho", "alto", "vetas", "canto", "ranura", "router", "etiqueta", "pieza", "codigo")

    /** Las piezas de varios muebles en bloques por melamina, en el orden del plano. */
    fun bloquesDeCorte(muebles: List<Mueble>): Map<String, List<FichaCorte>> =
        muebles.flatMap { mu -> fichasDeCorte(mu.ropero, RoperoCalculo.calcular(mu.ropero), mu.codigo) }.groupBy { it.material }

    /**
     * La hoja de corte de varios muebles en CSV (comas, punto decimal), con la estructura de la
     * hoja de Excel: un bloque por melamina con su título y su encabezado, y la cantidad total.
     * Al final de cada fila van la pieza y su código simbólico. Es la que leerá una máquina; para
     * abrirla en Excel, Hojas de cálculo o WPS está [HojaDeCorteXlsx].
     */
    fun csv(muebles: List<Mueble>): String = buildString {
        bloquesDeCorte(muebles).forEach { (material, fs) ->
            appendLine(campo(material))
            appendLine(COLUMNAS.joinToString(","))
            fs.forEach { f ->
                appendLine(listOf("${f.pieza.cantidad}", fmt(f.anchoCm), fmt(f.altoCm), f.vetas, f.canto, f.ranura, f.router, f.etiqueta, f.pieza.nombre, f.codigo)
                    .joinToString(",") { campo(it) })
            }
            appendLine("${fs.sumOf { it.pieza.cantidad }}")
            appendLine()
        }
    }

    /** Un campo de CSV: entre comillas si lleva coma, comillas o salto. */
    private fun campo(t: String): String =
        if (t.any { it == ',' || it == '"' || it == '\n' }) "\"" + t.replace("\"", "\"\"") + "\"" else t

    // ==================== Marcas y agujeros ====================

    enum class TipoMarca(val etiqueta: String) { UNION("unión"), RIEL("riel de cajón"), BISAGRA("base de bisagra"), CAZOLETA("cazoleta") }

    /**
     * Una marca en un tablero. [a] es la marca (la cara de la pieza que llega, o el centro del riel
     * o de la bisagra) medida desde el canto de referencia del tablero, y [agujero] el centro del
     * agujero en esa misma medida (null si no se agujerea ahí). [hondo]: dónde van los agujeros a lo
     * hondo, desde el frente (o, en una puerta, desde el canto de las bisagras). [cara]: de qué lado
     * del tablero (izquierda, derecha, desde abajo, desde arriba, interior).
     */
    data class Marca(val tipo: TipoMarca, val cara: String, val a: Float, val agujero: Float?, val hondo: List<Float>, val nota: String = "")

    /**
     * Un tablero con sus marcas. [vertical]: sus marcas se miden a lo alto desde su canto de abajo
     * (laterales, divisiones, puertas); si no, a lo ancho desde su punta izquierda (piso, techo,
     * repisas). [ancho] x [alto] es su medida de sitio, en el orden de siempre (ancho x alto).
     */
    data class Tablero(
        val nombre: String,
        val zona: String,
        val ancho: Float,
        val alto: Float,
        val vertical: Boolean,
        val marcas: List<Marca>,
        val referencia: String
    )

    /** Un tablero puesto en el mueble (cm desde abajo a la izquierda). */
    private data class Pieza2D(val nombre: String, val zona: String, val x0: Float, val x1: Float, val y0: Float, val y1: Float, val hondo: Float, val frente: Float = 0f)

    /** Dos agujeros a 5 cm de cada canto, o tres (con uno al centro) si la unión pasa de 40. */
    fun agujerosAlHondo(hondo: Float, desdeFrente: Float = 0f): List<Float> =
        if (hondo > 40f) listOf(desdeFrente + 5f, desdeFrente + hondo / 2f, desdeFrente + hondo - 5f)
        else listOf(desdeFrente + 5f, desdeFrente + hondo - 5f)

    private const val TOL = 0.1f
    /** La bisagra mide 7 de alto: su centro no puede quedar a menos de 3.5 de una melamina. */
    const val MEDIA_BISAGRA_CM = 3.5f
    const val BISAGRA_DESDE_PUNTA_CM = 10f
    const val CAZOLETA_DESDE_CANTO_CM = 2.2f
    const val BASE_DESDE_FRENTE_CM = 3.7f

    /** Todos los tableros del mueble que llevan marcas, con ellas. */
    fun tableros(r: Ropero): List<Tablero> {
        val e = r.espesorCm
        val xs = RoperoGeometria.cuerposX(r)
        val n = r.cuerpos.size
        val pasantes = RoperoGeometria.divisionesPasantes(r).toSet()
        val arm = r.fondoArmazonCm
        val util = r.fondoInteriorCm
        val atras = arm - util

        // Lo vertical: laterales, divisiones, divisiones del maletero y de casillero.
        val verticales = mutableListOf<Pieza2D>()
        verticales.add(Pieza2D("Lateral izquierdo", "armazón", 0f, e, 0f, r.altoDeCuerpo(0), arm))
        verticales.add(Pieza2D("Lateral derecho", "armazón", r.anchoCm - e, r.anchoCm, 0f, r.altoDeCuerpo(n - 1), arm))
        for (d in 0 until n - 1) {
            val x0 = xs[d].second
            val alto = maxOf(r.altoDeCuerpo(d), r.altoDeCuerpo(d + 1))
            if (d in pasantes) verticales.add(Pieza2D("División pasante", "entre cuerpo ${d + 1} y ${d + 2}", x0, x0 + e, 0f, alto, arm))
            else {
                val tope = if (r.maleteroPropio) RoperoGeometria.topeBajo(r, d) else maxOf(RoperoGeometria.techoY(r, d), RoperoGeometria.techoY(r, d + 1))
                verticales.add(Pieza2D("División", "entre cuerpo ${d + 1} y ${d + 2}", x0, x0 + e, RoperoGeometria.pisoY(r), tope, arm))
            }
        }
        if (r.maleteroPropio) {
            val tope = RoperoGeometria.topeBajo(r) + e
            val caras = pasantes.map { xs[it].second }
            RoperoGeometria.maleterosX(r).dropLast(1).forEachIndexed { k, (_, x1) ->
                if (caras.none { abs(it - x1) < TOL }) verticales.add(Pieza2D("División maletero", "maletero ${k + 1}-${k + 2}", x1, x1 + e, tope, RoperoGeometria.techoY(r), util, atras))
            }
        }
        val elementos = RoperoGeometria.elementos(r)
        elementos.filter { it.tipo == TipoElemento.DIVISION_COLUMNA }.forEach { el ->
            verticales.add(Pieza2D("División de casillero", zonaDe(el), el.x0, el.x1, el.y0, el.y1, util, atras))
        }

        // Lo horizontal: piso y techo por trozos, repisas del maletero, entrepaños y tapas.
        val horizontales = mutableListOf<Pieza2D>()
        RoperoGeometria.tramosDePiso(r).forEachIndexed { i, (x0, x1) -> horizontales.add(Pieza2D("Piso", "tramo ${i + 1}", x0, x1, r.bajoPisoCm, r.pisoArribaCm, arm)) }
        RoperoGeometria.tramosDeTecho(r).forEachIndexed { i, t -> horizontales.add(Pieza2D("Techo", "tramo ${i + 1}", t.x0, t.x1, t.alto - e, t.alto, arm)) }
        if (r.maleteroCm > 0f) {
            if (r.maleteroPropio) RoperoGeometria.tramosDePiso(r).forEachIndexed { i, (x0, x1) ->
                val y = RoperoGeometria.topeBajo(r); horizontales.add(Pieza2D("Repisa maletero", "tramo ${i + 1}", x0, x1, y, y + e, arm))
            } else xs.forEachIndexed { i, (x0, x1) ->
                val y = RoperoGeometria.topeBajo(r, i); horizontales.add(Pieza2D("Repisa maletero", "cuerpo ${i + 1}", x0, x1, y, y + e, util, atras))
            }
        }
        elementos.filter { it.tipo == TipoElemento.ENTREPANO || it.tipo == TipoElemento.TAPA_CAJONES }.forEach { el ->
            horizontales.add(Pieza2D(if (el.tipo == TipoElemento.ENTREPANO) "Entrepaño" else "Tapa de cajones", zonaDe(el), el.x0, el.x1, el.y0, el.y1, util, atras))
        }

        val marcasV = verticales.associateWith { mutableListOf<Marca>() }
        val marcasH = horizontales.associateWith { mutableListOf<Marca>() }
        // Uniones de lo horizontal contra la cara de lo vertical: la marca es la cara de abajo de la tabla.
        for (v in verticales) for (h in horizontales) {
            if (h.y0 < v.y0 - TOL || h.y1 > v.y1 + TOL) continue
            val cara = when { abs(h.x1 - v.x0) < TOL -> "cara izquierda"; abs(h.x0 - v.x1) < TOL -> "cara derecha"; else -> continue }
            val a = h.y0 - v.y0
            marcasV.getValue(v).add(Marca(TipoMarca.UNION, cara, a, a + e / 2f, agujerosAlHondo(h.hondo, h.frente), h.nombre))
        }
        // Lo vertical que se apoya en una tabla o llega bajo ella: la tabla lleva la marca.
        for (h in horizontales) for (v in verticales) {
            if (v.x0 < h.x0 - TOL || v.x1 > h.x1 + TOL) continue
            val cara = when { abs(v.y0 - h.y1) < TOL -> "desde abajo"; abs(v.y1 - h.y0) < TOL -> "desde arriba"; else -> continue }
            val a = v.x0 - h.x0
            marcasH.getValue(h).add(Marca(TipoMarca.UNION, cara, a, a + e / 2f, agujerosAlHondo(v.hondo, v.frente), v.nombre))
        }
        // Rieles de cajón: al centro de cada cajón en su espacio, en las dos caras que lo encierran.
        elementos.filter { it.tipo == TipoElemento.CAJON }.forEach { c ->
            val centro = (c.y0 + c.y1) / 2f
            for (v in verticales) {
                if (centro < v.y0 || centro > v.y1) continue
                val cara = when { abs(v.x1 - c.x0) < TOL -> "cara derecha"; abs(v.x0 - c.x1) < TOL -> "cara izquierda"; else -> continue }
                marcasV.getValue(v).add(Marca(TipoMarca.RIEL, cara, centro - v.y0, null, emptyList(), "cajón ${c.indice + 1}"))
            }
        }

        // Puertas batientes: bisagras a 10 de las puntas y las del medio repartidas; si una choca con
        // una melamina de la cara donde va su base, se corre lo justo para librarla.
        val puertas = mutableListOf<Tablero>()
        RoperoPuertas.de(r).hojas.filter { it.clase == ClaseDePuerta.PUERTA || it.clase == ClaseDePuerta.PUERTA_MALETERO }.forEachIndexed { i, hoja ->
            val bisagraIzq = hoja.tiradorX > (hoja.x0 + hoja.x1) / 2f
            val canto = if (bisagraIzq) hoja.x0 else hoja.x1
            val v = verticales.filter { canto >= it.x0 - 1f && canto <= it.x1 + 1f && it.y0 <= hoja.y1 && it.y1 >= hoja.y0 }
                .minByOrNull { abs((it.x0 + it.x1) / 2f - canto) }
            val caraBase = if (bisagraIzq) "cara derecha" else "cara izquierda"
            val tablas = if (v == null) emptyList() else horizontales.filter { h ->
                (if (bisagraIzq) abs(h.x0 - v.x1) < TOL else abs(h.x1 - v.x0) < TOL) && h.y1 > hoja.y0 && h.y0 < hoja.y1
            }
            val alturas = alturasDeBisagras(hoja.y0, hoja.y1, RoperoPuertas.bisagrasPorAlto(hoja.alto), tablas.map { it.y0 to it.y1 })
            val cazoletas = alturas.map { y -> Marca(TipoMarca.CAZOLETA, "interior", y - hoja.y0, y - hoja.y0, listOf(CAZOLETA_DESDE_CANTO_CM), "Ø35, a ${fmt(CAZOLETA_DESDE_CANTO_CM)} del canto ${if (bisagraIzq) "izquierdo" else "derecho"}") }
            puertas.add(Tablero(hoja.clase.nombre, RoperoCalculo.zonaDeX(r, (hoja.x0 + hoja.x1) / 2f) + ", hoja ${i + 1}" + if (bisagraIzq) " (abre a la derecha)" else " (abre a la izquierda)",
                hoja.ancho, hoja.alto, true, cazoletas, "desde su canto de abajo; medidas con el canto puesto"))
            if (v != null) alturas.forEach { y ->
                marcasV.getValue(v).add(Marca(TipoMarca.BISAGRA, caraBase, y - v.y0, y - v.y0, listOf(BASE_DESDE_FRENTE_CM), "base de la ${hoja.clase.nombre.lowercase()}"))
            }
        }

        // Dos uniones a la misma altura en las dos caras de una división no pueden atornillarse
        // de lado a lado: se avisa para correr una.
        fun conAvisos(ms: List<Marca>): List<Marca> = ms.map { m ->
            val choca = m.tipo == TipoMarca.UNION && ms.any { o -> o !== m && o.tipo == TipoMarca.UNION && o.cara != m.cara && abs((o.agujero ?: o.a) - (m.agujero ?: m.a)) < 3f }
            if (choca) m.copy(nota = m.nota + " · ojo: hay otra unión a la misma altura en la otra cara, correr una") else m
        }.sortedBy { it.a }

        val salen = mutableListOf<Tablero>()
        verticales.forEach { v -> val ms = marcasV.getValue(v); if (ms.isNotEmpty()) salen.add(Tablero(v.nombre, v.zona, v.hondo, v.y1 - v.y0, true, conAvisos(ms), "desde su canto de abajo${if (v.y0 < 0.05f) " (el suelo)" else ""}; a lo hondo desde el frente")) }
        horizontales.forEach { h -> val ms = marcasH.getValue(h); if (ms.isNotEmpty()) salen.add(Tablero(h.nombre, h.zona, h.x1 - h.x0, h.hondo, false, ms.sortedBy { it.a }, "desde su punta izquierda; a lo hondo desde el frente")) }
        return salen + puertas
    }

    /**
     * Las alturas (desde el suelo) de las bisagras de una hoja de [y0] a [y1]: a 10 de las puntas y
     * las del medio repartidas; la que choque con una tabla ([tablas], cada una de abajo a arriba)
     * se corre lo mínimo para que sus 7 cm queden libres, sin salirse de la hoja.
     */
    fun alturasDeBisagras(y0: Float, y1: Float, cuantas: Int, tablas: List<Pair<Float, Float>>): List<Float> {
        val primera = y0 + BISAGRA_DESDE_PUNTA_CM
        val ultima = y1 - BISAGRA_DESDE_PUNTA_CM
        val base = if (cuantas <= 1) listOf((y0 + y1) / 2f) else (0 until cuantas).map { primera + (ultima - primera) * it / (cuantas - 1) }
        return base.map { y ->
            val choque = tablas.firstOrNull { (a, b) -> y + MEDIA_BISAGRA_CM > a && y - MEDIA_BISAGRA_CM < b } ?: return@map y
            val arriba = choque.second + MEDIA_BISAGRA_CM + 0.5f
            val abajo = choque.first - MEDIA_BISAGRA_CM - 0.5f
            val caben = listOf(arriba, abajo).filter { it - MEDIA_BISAGRA_CM >= y0 + 1f && it + MEDIA_BISAGRA_CM <= y1 - 1f }
            caben.minByOrNull { abs(it - y) } ?: y
        }
    }

    private fun zonaDe(el: ElementoRopero): String {
        val base = "cuerpo ${el.cuerpo + 1}"
        if (el.ruta.size < 2) return base
        return base + el.ruta.chunked(2).filter { it.size == 2 }.joinToString("") { (k, j) -> ", casillero ${k + 1} col ${j + 1}" }
    }

    fun fmt(v: Float): String =
        if (abs(v - Math.round(v)) < 0.05f) Math.round(v).toString() else String.format(Locale.US, "%.1f", v)
}
