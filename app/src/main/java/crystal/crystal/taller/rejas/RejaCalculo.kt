package crystal.crystal.taller.rejas

import crystal.crystal.taller.puerta.logica.CalculosPuerta
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.sqrt

/** Los modelos de reja de tubo cuadrado que se hacen en el taller. */
enum class ModeloReja(val etiqueta: String) {
    /** Parantes y travesaños: la grilla de siempre, editable. */
    CUADRICULA("Cuadrícula"),
    /** Barrotes verticales enteros, con travesaños partidos entre ellos si se piden. */
    BARROTES_VERTICALES("Barrotes verticales"),
    /** Travesaños horizontales enteros, con parantes partidos entre ellos si se piden. */
    BARROTES_HORIZONTALES("Barrotes horizontales"),
    /** Dos familias de diagonales a 45° que se cruzan: una entera y la otra partida en los cruces. */
    ROMBOS("Rombos"),
    /** Un parante al medio y diagonales a 45° que suben hacia él desde los dos lados (chevron). */
    ESPINA("Espina de pescado"),
    /**
     * Parantes enteros y, entre ellos, travesaños cortos a distintas alturas alternando columna a
     * columna, como ladrillos trabados: las columnas impares parten su alto en n tramos iguales y
     * las pares van corridas medio tramo.
     */
    TRABADO("Trabado (ladrillo)")
}

/** Fija (un paño con su marco) o puerta (marco fijo, hoja que abre y, encima del puente, la sobreluz). */
enum class TipoReja(val etiqueta: String) {
    FIJA("Fija"),
    PUERTA("Puerta")
}

/** El relleno de la mocheta de una reja de puerta: su modelo ([modelo] null = sin relleno), su paso y sus intermedios. */
data class DisenoMocheta(val modelo: ModeloReja?, val pasoCm: Float = 15f, val intermedios: Int = 0)

/** Un tubo de la reja: dónde va (cm desde la esquina de abajo a la izquierda, ejes) y cómo se corta. */
data class TuboReja(
    val nombre: String,
    val x0: Float, val y0: Float, val x1: Float, val y1: Float,
    /** Cortes a 45° en las puntas (diagonales) o rectos. */
    val a45: Boolean = false
) {
    /** El largo del eje. */
    val largo: Float get() = sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0))
}

/**
 * Los tubos de una reja: el marco perimetral y lo de dentro según el modelo. Una sola cuenta
 * para el dibujo y para la lista de corte.
 *
 * Reglas (las del taller, hasta que se corrijan):
 * - Marco: dos verticales de alto entero y dos horizontales de ancho − 2·marco, entre ellos.
 * - Lo de dentro va entre las caras interiores del marco. Los tubos "enteros" van de cara a
 *   cara; los "partidos" se cortan entre los enteros descontando el grueso del tubo que cruzan.
 * - El paso es el hueco máximo: se reparte ceil(medida / paso) tramos iguales.
 * - Diagonales a 45°: el largo es el del eje entre caras; las puntas se cortan a 45°. En los
 *   rombos, la familia que va de abajo-izquierda a arriba-derecha va entera y la otra se parte
 *   en cada cruce descontando el grueso del tubo (las dos familias son perpendiculares, así
 *   que un tubo cruzado ocupa justo su grueso a lo largo del otro).
 */
object RejaCalculo {

    data class Reja(
        val anchoCm: Float,
        val altoCm: Float,
        val marcoCm: Float = 3.8f,
        val tuboCm: Float = 3.8f,
        val pasoCm: Float = 15f,
        val modelo: ModeloReja = ModeloReja.CUADRICULA,
        /** Travesaños intermedios en los barrotes verticales (o parantes en los horizontales); en el trabado, los tramos de las columnas impares (3 si es 0). */
        val intermedios: Int = 0,
        val tipo: TipoReja = TipoReja.FIJA,
        /** Puerta: la altura del puente, como en la calculadora de puertas; 0 = la de fábrica ([alturaPuente]). */
        val puenteCm: Float = 0f,
        /** Puerta: el juego que se le deja a la hoja para que entre y gire (el de Puerta, 1 de fábrica). */
        val holguraCm: Float = 1f,
        /**
         * Puerta: el diseño de la mocheta (la sobreluz sobre el puente), aparte del de la hoja
         * (pedido del usuario, 2026-10-10: copiar el de la hoja en un hueco tan bajo lo dejaba
         * reducido). Null = el mismo de la hoja.
         */
        val mocheta: DisenoMocheta? = null
    ) {
        val interiorAncho: Float get() = anchoCm - 2 * marcoCm
        val interiorAlto: Float get() = altoCm - 2 * marcoCm
    }

    /** Cuántos tramos salen de repartir [medida] en huecos de [paso] como mucho (uno al menos). */
    fun tramos(medida: Float, paso: Float): Int = ceil(medida / paso.coerceAtLeast(1f)).toInt().coerceAtLeast(1)

    /** Los tubos del marco: verticales enteros, horizontales entre ellos. */
    fun marco(r: Reja): List<TuboReja> {
        val m = r.marcoCm
        return listOf(
            TuboReja("Marco", m / 2f, 0f, m / 2f, r.altoCm),
            TuboReja("Marco", r.anchoCm - m / 2f, 0f, r.anchoCm - m / 2f, r.altoCm),
            TuboReja("Marco", m, m / 2f, r.anchoCm - m, m / 2f),
            TuboReja("Marco", m, r.altoCm - m / 2f, r.anchoCm - m, r.altoCm - m / 2f)
        )
    }

    /** Lo de dentro del marco según el modelo (sin el marco). */
    fun interior(r: Reja): List<TuboReja> = when (r.modelo) {
        ModeloReja.CUADRICULA -> cuadricula(r, tramos(r.interiorAncho, r.pasoCm), tramos(r.interiorAlto, r.pasoCm))
        ModeloReja.BARROTES_VERTICALES -> cuadricula(r, tramos(r.interiorAncho, r.pasoCm), r.intermedios.coerceAtLeast(0) + 1)
        ModeloReja.BARROTES_HORIZONTALES -> cuadriculaHorizontal(r, tramos(r.interiorAlto, r.pasoCm), r.intermedios.coerceAtLeast(0) + 1)
        ModeloReja.ROMBOS -> rombos(r)
        ModeloReja.ESPINA -> espina(r)
        ModeloReja.TRABADO -> trabado(r, tramos(r.interiorAncho, r.pasoCm), if (r.intermedios > 0) r.intermedios else 3)
    }

    fun todos(r: Reja): List<TuboReja> = when (r.tipo) {
        TipoReja.FIJA -> marco(r) + interior(r)
        TipoReja.PUERTA -> puerta(r)
    }

    // ==================== REJA DE PUERTA (2026-10-10) ====================
    // Como la calculadora de puertas: un marco fijo, la hoja hasta la altura del puente y, si arriba
    // queda sitio, el puente y la sobreluz fija. Las cuentas de la hoja y del puente son las de
    // Puerta (CalculosPuerta), no copias. Asumido, por revisar con el usuario: el marco fijo no lleva
    // tubo abajo (es puerta, no hay umbral); el puente y el marco de la hoja son del grueso del marco;
    // la sobreluz y la hoja se rellenan con el mismo modelo.

    /**
     * La altura del puente: la escrita o, en 0, la de fábrica de Puerta (199 si el alto pasa de 210…).
     * Sin sitio para la sobreluz, la hoja llega al cabezal: alto − marco.
     */
    fun alturaPuente(r: Reja): Float {
        val h = CalculosPuerta.hPuente(r.altoCm, r.puenteCm, 0f, CalculosPuerta.HOJA_REF, r.marcoCm)
        return if (altoSobreluz(r, h) > 2f) h else r.altoCm - r.marcoCm
    }

    /** El alto libre de la sobreluz: lo de Puerta (mocheta), con el puente del grueso del marco. */
    private fun altoSobreluz(r: Reja, puente: Float): Float = CalculosPuerta.mocheta(r.altoCm, puente, r.marcoCm, r.marcoCm)

    /** ¿La puerta lleva puente y sobreluz encima de la hoja? */
    fun conSobreluz(r: Reja): Boolean = r.tipo == TipoReja.PUERTA && alturaPuente(r) < r.altoCm - r.marcoCm - 0.01f

    /** La hoja de la puerta: ancho y alto como en Puerta (vano menos marcos y holgura; puente menos holgura). */
    fun hoja(r: Reja): Reja = r.copy(
        anchoCm = CalculosPuerta.anchoHoja(r.anchoCm, r.marcoCm, r.marcoCm, r.holguraCm),
        altoCm = CalculosPuerta.parante(alturaPuente(r), 0f, r.holguraCm),
        tipo = TipoReja.FIJA
    )

    private fun puerta(r: Reja): List<TuboReja> {
        val m = r.marcoCm
        val salen = mutableListOf(
            // El marco fijo: las dos jambas enteras y el cabezal entre ellas; abajo no lleva nada.
            TuboReja("Marco", m / 2f, 0f, m / 2f, r.altoCm),
            TuboReja("Marco", r.anchoCm - m / 2f, 0f, r.anchoCm - m / 2f, r.altoCm),
            TuboReja("Marco", m, r.altoCm - m / 2f, r.anchoCm - m, r.altoCm - m / 2f)
        )
        val puente = alturaPuente(r)
        if (conSobreluz(r)) {
            salen.add(TuboReja("Puente", m, puente + m / 2f, r.anchoCm - m, puente + m / 2f))
            // La mocheta: su propio diseño entre las caras de las jambas, el puente y el cabezal.
            val diseno = r.mocheta ?: DisenoMocheta(r.modelo, r.pasoCm, r.intermedios)
            val modeloMocheta = diseno.modelo
            if (modeloMocheta != null) {
                val sobreluz = r.copy(
                    anchoCm = r.anchoCm - 2 * m, altoCm = altoSobreluz(r, puente), marcoCm = 0f, tipo = TipoReja.FIJA,
                    modelo = modeloMocheta, pasoCm = diseno.pasoCm, intermedios = diseno.intermedios
                )
                salen.addAll(interior(sobreluz).map { it.copy(nombre = "${it.nombre} mocheta").corrido(m, puente + m) })
            }
        }
        // La hoja, con su propio marco, centrada en el hueco del marco fijo y bajo el puente.
        val h = hoja(r)
        val dx = m + r.holguraCm / 2f
        val dy = r.holguraCm / 2f
        salen.addAll(marco(h).map { it.copy(nombre = "Marco hoja").corrido(dx, dy) })
        salen.addAll(interior(h).map { it.corrido(dx, dy) })
        return salen
    }

    private fun TuboReja.corrido(dx: Float, dy: Float) = copy(x0 = x0 + dx, y0 = y0 + dy, x1 = x1 + dx, y1 = y1 + dy)

    /**
     * Parantes enteros (columnas − 1) de cara a cara del marco, y travesaños partidos entre
     * parantes (filas − 1 por columna), descontando el grueso de lo que tocan.
     */
    private fun cuadricula(r: Reja, columnas: Int, filas: Int): List<TuboReja> {
        val m = r.marcoCm; val t = r.tuboCm
        val w = r.interiorAncho; val h = r.interiorAlto
        val salen = mutableListOf<TuboReja>()
        val anchoTramo = (w - (columnas - 1) * t) / columnas
        val altoTramo = (h - (filas - 1) * t) / filas
        for (c in 1 until columnas) {
            val x = m + c * anchoTramo + (c - 0.5f) * t
            salen.add(TuboReja("Parante", x, m, x, r.altoCm - m))
        }
        for (c in 0 until columnas) {
            val x0 = m + c * (anchoTramo + t)
            val x1 = x0 + anchoTramo
            for (f in 1 until filas) {
                val y = m + f * altoTramo + (f - 0.5f) * t
                salen.add(TuboReja("Travesaño", x0, y, x1, y))
            }
        }
        return salen
    }

    /** Travesaños enteros (filas − 1) de cara a cara, y parantes partidos entre ellos (columnas − 1 por fila). */
    private fun cuadriculaHorizontal(r: Reja, filas: Int, columnas: Int): List<TuboReja> {
        val m = r.marcoCm; val t = r.tuboCm
        val w = r.interiorAncho; val h = r.interiorAlto
        val salen = mutableListOf<TuboReja>()
        val anchoTramo = (w - (columnas - 1) * t) / columnas
        val altoTramo = (h - (filas - 1) * t) / filas
        for (f in 1 until filas) {
            val y = m + f * altoTramo + (f - 0.5f) * t
            salen.add(TuboReja("Travesaño", m, y, r.anchoCm - m, y))
        }
        for (f in 0 until filas) {
            val y0 = m + f * (altoTramo + t)
            val y1 = y0 + altoTramo
            for (c in 1 until columnas) {
                val x = m + c * anchoTramo + (c - 0.5f) * t
                salen.add(TuboReja("Parante", x, y0, x, y1))
            }
        }
        return salen
    }

    /**
     * Trabado: parantes enteros (columnas − 1) y travesaños cortos entre ellos. Las columnas
     * impares (1.ª, 3.ª…) llevan sus travesaños en los cortes de partir el alto en [tramos]
     * iguales (tramos − 1 travesaños); las pares van corridas medio tramo (tramos travesaños),
     * como las juntas de una pared de ladrillo.
     */
    private fun trabado(r: Reja, columnas: Int, tramos: Int): List<TuboReja> {
        val m = r.marcoCm; val t = r.tuboCm
        val w = r.interiorAncho; val h = r.interiorAlto
        val salen = mutableListOf<TuboReja>()
        val anchoTramo = (w - (columnas - 1) * t) / columnas
        for (c in 1 until columnas) {
            val x = m + c * anchoTramo + (c - 0.5f) * t
            salen.add(TuboReja("Parante", x, m, x, r.altoCm - m))
        }
        val paso = h / tramos
        for (c in 0 until columnas) {
            val x0 = m + c * (anchoTramo + t)
            val x1 = x0 + anchoTramo
            val alturas = if (c % 2 == 0) (1 until tramos).map { it * paso } else (0 until tramos).map { (it + 0.5f) * paso }
            alturas.forEach { y -> salen.add(TuboReja("Travesaño", x0, m + y, x1, m + y)) }
        }
        return salen
    }

    /**
     * Rombos: diagonales a 45° cada [Reja.pasoCm] (medido a lo largo del marco) en las dos
     * direcciones. Las que suben a la derecha (/) van enteras; las que bajan (\) se parten en
     * cada cruce, descontando el grueso del tubo (las familias son perpendiculares).
     */
    private fun rombos(r: Reja): List<TuboReja> {
        val m = r.marcoCm; val w = r.interiorAncho; val h = r.interiorAlto
        val paso = r.pasoCm.coerceAtLeast(1f)
        val salen = mutableListOf<TuboReja>()
        // Cada diagonal / es la recta x − y = d (en el interior, con el origen en la esquina de
        // abajo a la izquierda del interior); recorre desde x = max(0, d) hasta x = min(w, d + h).
        val ds = diagonales(w, h, paso)
        for (d in ds) {
            val xa = maxOf(0f, d); val xb = minOf(w, d + h)
            if (xb - xa < 0.5f) continue
            salen.add(TuboReja("Diagonal", m + xa, m + xa - d, m + xb, m + xb - d, a45 = true))
        }
        // Las \ son x + y = s, partidas en los cruces con las /: en la recta \ el cruce con la / de
        // parámetro d está en x = (s + d) / 2.
        // Medio grueso por cada lado del cruce; a lo largo de x, dividido por √2.
        val descuentoX = r.tuboCm / 2f / sqrt(2f)
        for (s in diagonales(w, h, paso).map { it + h }) {   // s recorre (0, w + h) con el mismo paso
            val xa = maxOf(0f, s - h); val xb = minOf(w, s)
            if (xb - xa < 0.5f) continue
            val cruces = ds.map { (s + it) / 2f }.filter { it > xa + 0.01f && it < xb - 0.01f }.sorted()
            val puntos = listOf(xa) + cruces + listOf(xb)
            for (i in 0 until puntos.size - 1) {
                // Cada trozo va de cruce a cruce (o al marco): en x se le quita medio descuento por cada cruce que toca.
                val x0 = puntos[i] + (if (i > 0) descuentoX else 0f)
                val x1 = puntos[i + 1] - (if (i + 1 < puntos.size - 1) descuentoX else 0f)
                if (x1 - x0 < 0.5f) continue
                salen.add(TuboReja("Diagonal partida", m + x0, m + (s - x0), m + x1, m + (s - x1), a45 = true))
            }
        }
        return salen
    }

    /** Los parámetros d de las diagonales x − y = d que caben en el interior, cada [paso], centradas. */
    private fun diagonales(w: Float, h: Float, paso: Float): List<Float> {
        val desde = -h; val hasta = w
        val n = ((hasta - desde) / paso).toInt()
        val sobra = (hasta - desde) - n * paso
        return (1..n).map { desde + sobra / 2f + (it - 0.5f) * paso }.filter { it > desde + 0.5f && it < hasta - 0.5f }
    }

    /**
     * Espina de pescado: un parante entero al medio y, a cada lado, diagonales a 45° que suben
     * hacia el centro cada [Reja.pasoCm], enteras (del marco al parante, cortadas a 45°).
     */
    private fun espina(r: Reja): List<TuboReja> {
        val m = r.marcoCm; val t = r.tuboCm; val w = r.interiorAncho; val h = r.interiorAlto
        val paso = r.pasoCm.coerceAtLeast(1f)
        val salen = mutableListOf<TuboReja>()
        val xc = m + w / 2f
        salen.add(TuboReja("Parante", xc, m, xc, r.altoCm - m))
        val mitad = (w - t) / 2f          // el ancho libre de cada lado
        // Lado izquierdo: rectas / que van de (x, y) a (x + Δ, y + Δ) dentro del rectángulo mitad × h.
        for (d in diagonales(mitad, h, paso)) {
            val xa = maxOf(0f, d); val xb = minOf(mitad, d + h)
            if (xb - xa < 0.5f) continue
            salen.add(TuboReja("Diagonal", m + xa, m + xa - d, m + xb, m + xb - d, a45 = true))
            // Lado derecho, en espejo: \ hacia el centro.
            val xd0 = m + w - xa; val xd1 = m + w - xb
            salen.add(TuboReja("Diagonal", xd0, m + xa - d, xd1, m + xb - d, a45 = true))
        }
        return salen
    }

    /** La lista de corte: `largo = cantidad` por nombre, con "(45°)" en las diagonales. */
    fun listaDeCorte(tubos: List<TuboReja>): Map<String, Map<String, Int>> {
        val porNombre = linkedMapOf<String, MutableMap<String, Int>>()
        tubos.forEach { tubo ->
            val clave = if (tubo.a45) "${fmt(tubo.largo)} (45°)" else fmt(tubo.largo)
            val mapa = porNombre.getOrPut(tubo.nombre) { linkedMapOf() }
            mapa[clave] = (mapa[clave] ?: 0) + 1
        }
        return porNombre
    }

    /** Las líneas `largo = cantidad` de un grupo de nombres (marco, o todo lo de dentro), de mayor a menor. */
    fun lineas(tubos: List<TuboReja>, vararg nombres: String): String {
        val mapa = linkedMapOf<String, Int>()
        tubos.filter { nombres.isEmpty() || it.nombre in nombres }.forEach { tubo ->
            val clave = if (tubo.a45) "${fmt(tubo.largo)} (45°)" else fmt(tubo.largo)
            mapa[clave] = (mapa[clave] ?: 0) + 1
        }
        return mapa.entries.sortedByDescending { it.key.substringBefore(" ").toFloatOrNull() ?: 0f }.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    /** Los metros de tubo (ejes), redondeados al décimo. */
    fun metros(tubos: List<TuboReja>): Float = (tubos.sumOf { it.largo.toDouble() } / 100.0 * 10.0).roundToInt() / 10f

    fun fmt(v: Float): String {
        val d = (v * 10f).roundToInt() / 10f
        return if (abs(d - d.toInt()) < 0.001f) d.toInt().toString() else String.format(java.util.Locale.US, "%.1f", d)
    }
}
