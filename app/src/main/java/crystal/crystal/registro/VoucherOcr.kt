package crystal.crystal.registro

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Utilitario para extraer campos de vouchers (Yape / PLIN) a partir de texto OCR.
 */
object VoucherOcr {

    data class Resultado(
        val monto: Double?,          // S/
        val codigo: String?,         // Código (operación / seguridad)
        val telefono: String?,       // 9 dígitos (Perú)
        val tsOperacion: Long?,      // millis si se pudo parsear fecha/hora
        val bruto: String,           // texto OCR completo normalizado
        val tipoVoucher: String? = null // "yape_yape", "plin_yape", "yape_plin", etc.
    )

    /**
     * Punto de entrada: entrega un Resultado con los campos detectados.
     */
    fun parseVoucher(textoCrudo: String): Resultado {
        // Texto normalizado (una sola línea, espacios colapsados)
        val t = normalizar(textoCrudo)
        val tLower = t.lowercase()

        val monto = parseMonto(t)
        val codigo = parseCodigo(t)
        val telefono = parseTelefono(t)
        val ts = parseFechaHora(t)

        val tipoVoucher = clasificarTipoVoucherDesdeOcr(tLower, t)

        Log.d(
            "VoucherOcr", """
            ═══════════════════════════════════
            📋 RESULTADO DEL PARSEO:
            - Monto: $monto
            - Código: ${codigo ?: "sin código"}
            - Teléfono: ${telefono ?: "sin teléfono"}
            - Fecha: ${if (ts != null) Date(ts) else "sin fecha"}
            - Tipo: $tipoVoucher
            ═══════════════════════════════════
        """.trimIndent()
        )

        return Resultado(
            monto = monto,
            codigo = codigo,
            telefono = telefono,
            tsOperacion = ts,
            bruto = t,
            tipoVoucher = tipoVoucher
        )
    }

    // ================== parsers ==================

    fun parseMonto(t: String): Double? {
        // 1) "S/ 15.00" | "S/15" | "S/. 15,50"
        val r1 = Regex("""S[/.\s]*([0-9]{1,4}(?:[.,][0-9]{1,2})?)""", RegexOption.IGNORE_CASE)
        r1.find(t)?.let { m ->
            return normalizarNumero(m.groupValues[1])
        }

        // 2) fallback: "Monto: 15.00" o "Importe 12,5"
        val r2 = Regex(
            """(monto|importe)[:\s]*([0-9]{1,4}(?:[.,][0-9]{1,2})?)""",
            RegexOption.IGNORE_CASE
        )
        r2.find(t)?.let { m ->
            return normalizarNumero(m.groupValues[2])
        }
        return null
    }

    /** Código operación / seguridad: después de "Operación", "Operación N°", "CÓDIGO", etc. 3-12 dígitos/alfa. */
    // ⭐ SOLO REEMPLAZAR LA FUNCIÓN parseCodigo() EN TU VoucherOcr.kt

    /**
     * ⭐ CORREGIDO: Detecta código de seguridad con dígitos separados
     * Código operación / seguridad: después de "Operación", "Operación N°", "CÓDIGO", etc. 3-12 dígitos/alfa.
     */
    fun parseCodigo(t: String): String? {
        // ⭐ PRIORIDAD 1: Código de seguridad (Yape → Yape)
        // Busca después de "código de seguridad" y permite dígitos separados por espacios
        // Ejemplo: "CÓDIGO DE SEGURIDAD ... 2 5 6" → "256"
        val rSeguridad = Regex(
            """c[oó]d(?:\.|igo)?\s+(?:de\s+)?seguridad.*?([\d\s]{3,20})""",
            RegexOption.IGNORE_CASE
        )
        rSeguridad.find(t)?.let { m ->
            // Extraer solo los dígitos (quitar espacios)
            val codigoConEspacios = m.groupValues[1]
            val codigoLimpio = codigoConEspacios.filter { it.isDigit() }

            // Debe tener entre 3 y 8 dígitos (códigos de seguridad típicos)
            if (codigoLimpio.length in 3..8) {
                Log.d(
                    "VoucherOcr",
                    "✅ Código de seguridad detectado: '$codigoConEspacios' → '$codigoLimpio'"
                )
                return codigoLimpio
            }
        }

        // ⭐ PRIORIDAD 2: Código de operación (plin_yape, etc.)
        // "Código de operación: 123456" | "Operación N° 49079460" | "Nro. de operación b0d95b22"
        // Este NO cambia, sigue funcionando igual para plin_yape ✅
        val r1 = Regex(
            """(c[oó]digo(?:\s+de\s+operaci[oó]n)?|operaci[oó]n\s*(n°|nº|num\.|nro\.)?)\s*[:#]?\s*([0-9A-Za-z]{3,12})""",
            RegexOption.IGNORE_CASE
        )
        r1.find(t)?.let { m ->
            return m.groupValues[3]
        }
        return null
    }

    /** Teléfono: 9 dígitos comenzando en 9 (Perú). */
    fun parseTelefono(t: String): String? {
        val r = Regex("""(?:\+?51)?\s*(9\d{8})""")
        r.find(t)?.let { m ->
            return m.groupValues[1]
        }
        return null
    }

    /** Fecha/hora: intenta varios formatos típicos de vouchers.
     *  ⭐ CORREGIDO: Ahora incluye formatos con AM/PM
     *  ⭐ MEJORADO: Normaliza caracteres comunes del OCR antes de parsear
     */
    fun parseFechaHora(t: String): Long? {
        // ⭐ NORMALIZAR caracteres comunes del OCR
        var tNormalizado = t
            .replace("O7:", "07:")  // O (letra) → 0 (número) en hora
            .replace("O8:", "08:")
            .replace("O9:", "09:")
            .replace("o7:", "07:")  // o minúscula también
            .replace("o8:", "08:")
            .replace("o9:", "09:")
            .replace("l7:", "17:")  // l (ele) → 1 en hora
            .replace("l8:", "18:")
            .replace("l9:", "19:")
            .replace("I7:", "17:")  // I (i mayúscula) → 1
            .replace("I8:", "18:")
            .replace("I9:", "19:")

        Log.d("VoucherOcr", "📅 Texto original para fecha: ${t.take(150)}")
        if (tNormalizado != t) {
            Log.d("VoucherOcr", "✏️ Texto normalizado: ${tNormalizado.take(150)}")
        }

        // Patrones actualizados con AM/PM y locale español
        val patrones = listOf(
            Pair(
                "dd MMM yyyy hh:mm a",
                Regex(
                    """(\d{1,2}\s+\w{3}\s+\d{4}\s+\d{1,2}:\d{2}\s+[AP]M)""",
                    RegexOption.IGNORE_CASE
                )
            ),
            Pair("dd/MM/yyyy HH:mm", Regex("""(\d{1,2}/\d{1,2}/\d{2,4}\s+\d{1,2}:\d{2})""")),
            Pair("dd-MM-yyyy HH:mm", Regex("""(\d{1,2}-\d{1,2}-\d{2,4}\s+\d{1,2}:\d{2})""")),
            Pair(
                "dd MMM yyyy | HH:mm",
                Regex("""(\d{1,2}\s+\w{3}\s+\d{4}\s+\|\s+\d{1,2}:\d{2})""")
            ),
            Pair("dd MMM yyyy HH:mm", Regex("""(\d{1,2}\s+\w{3}\s+\d{4}\s+\d{1,2}:\d{2})"""))
        )

        for ((patron, regex) in patrones) {
            try {
                val m = regex.find(tNormalizado) ?: continue
                val fechaStr = m.groupValues[1]

                // ⭐ CRÍTICO: Usar Locale.US para patrones con AM/PM (en inglés)
                // Nov, PM, AM son términos en inglés, no español
                val locale = if (patron.contains("a")) Locale.US else Locale("es", "PE")
                val sdf = SimpleDateFormat(patron, locale)
                sdf.timeZone = TimeZone.getTimeZone("America/Lima")

                val d: Date? = sdf.parse(fechaStr)
                if (d != null) {
                    Log.d("VoucherOcr", "✅ Fecha parseada: $fechaStr → ${Date(d.time)}")
                    return d.time
                }
            } catch (e: Exception) {
                Log.w("VoucherOcr", "Error parseando con patrón $patron: ${e.message}")
            }
        }

        Log.w("VoucherOcr", "⚠️ No se pudo parsear fecha del texto: ${tNormalizado.take(200)}")
        return null
    }

    // ================== clasificación tipoVoucher MEJORADA ==================

    /**
     * ¿El texto menciona un "cód. de seguridad" típico de Yape?
     */
    private fun tieneCodigoSeguridadYape(t: String): Boolean {
        val r = Regex(
            """c[oó]d(?:\.|igo)?\s+(?:de\s+)?seguridad""",
            RegexOption.IGNORE_CASE
        )
        return r.containsMatchIn(t)
    }

    /**
     * ⭐ MEJORADO: Detecta "plin" de forma robusta
     * Maneja variaciones comunes del OCR
     */
    private fun contienePlin(t: String): Boolean {
        // Normalizar para búsqueda más flexible
        val tNorm = t.lowercase()
            .replace("ı", "i")  // i sin punto (unicode) → i normal
            .replace("l", "l")  // por si hay confusión
            .replace("1", "i")  // número 1 confundido con i

        // Búsquedas directas
        if (tNorm.contains("plin")) return true
        if (tNorm.contains("pl1n")) return true
        if (tNorm.contains("plın")) return true

        // Regex más flexible (pl + i/1/ı + n)
        val regex = Regex("""pl[i1ı]n""", RegexOption.IGNORE_CASE)
        if (regex.containsMatchIn(t)) return true

        return false
    }

    /**
     * ⭐ MEJORADO: Detecta "interbank" de forma robusta
     */
    private fun contieneInterbank(t: String): Boolean {
        return t.contains("interbank", ignoreCase = true) ||
                t.contains("1nterbank", ignoreCase = true) ||  // 1 confundido con I
                t.contains("ínterbank", ignoreCase = true)
    }

    /**
     * ⭐ MEJORADO: Detecta app de DESTINO de forma robusta
     * Devuelve "yape", "plin" o null.
     */
    private fun detectarDestinoApp(t: String): String? {
        // Buscar bloque "Destino:" o "DestinO:" (OCR puede confundir)
        val idxDestino = t.indexOfAny(listOf("destino:", "destino", "dest1no"), ignoreCase = true)

        if (idxDestino >= 0) {
            // Extraer subsección después de "destino"
            val fin = if (idxDestino + 120 < t.length) idxDestino + 120 else t.length
            val sub = t.substring(idxDestino, fin).lowercase()

            Log.d("VoucherOcr", "🔍 Bloque destino: ${sub.take(80)}")

            // Buscar "yape" primero (más específico)
            if (sub.contains("yape")) {
                Log.d("VoucherOcr", "✅ Destino detectado: YAPE")
                return "yape"
            }

            // Buscar "plin" con variaciones
            if (contienePlin(sub)) {
                Log.d("VoucherOcr", "✅ Destino detectado: PLIN")
                return "plin"
            }
        }

        // Fallbacks generales si no hay sección "destino:"
        if (t.contains("yape", ignoreCase = true)) {
            // Verificar que no sea solo "yapeaste" sin destino
            if (t.contains("destino", ignoreCase = true)) {
                Log.d("VoucherOcr", "✅ Destino detectado (fallback): YAPE")
                return "yape"
            }
        }

        Log.d("VoucherOcr", "⚠️ Destino NO detectado")
        return null
    }

    /**
     * ⭐ MEJORADO: Detecta app de ORIGEN de forma robusta
     * Devuelve "yape", "plin" o null.
     */
    private fun detectarOrigenApp(t: String): String? {
        // Caso 1: Interbank + plin (origen PLIN)
        // Ejemplo: "Interbank plın" o "interbank PLIN"
        if (contieneInterbank(t) && contienePlin(t)) {
            Log.d("VoucherOcr", "✅ Origen detectado: PLIN (Interbank + plin)")
            return "plin"
        }

        // Caso 2: Solo Interbank (también es PLIN)
        if (contieneInterbank(t)) {
            Log.d("VoucherOcr", "✅ Origen detectado: PLIN (Interbank)")
            return "plin"
        }

        // Caso 3: Yape → vouchers "¡Yapeaste!"
        if (t.contains("¡yapeaste!", ignoreCase = true) ||
            t.contains("yapeaste!", ignoreCase = true)
        ) {
            Log.d("VoucherOcr", "✅ Origen detectado: YAPE (¡Yapeaste!)")
            return "yape"
        }

        // Caso 4: Confirmación de Pago (típico de Yape app)
        if (t.contains("confirmación de pago", ignoreCase = true) ||
            t.contains("confirmacion de pago", ignoreCase = true)
        ) {
            Log.d("VoucherOcr", "✅ Origen detectado: YAPE (Confirmación de pago)")
            return "yape"
        }

        Log.d("VoucherOcr", "⚠️ Origen NO detectado")
        return null
    }

    /**
     * ⭐ MEJORADO: Aplica la política origen_destino → tipoVoucher
     * Ahora con logs detallados
     */
    private fun clasificarTipoVoucherDesdeOcr(tLower: String, tOriginal: String): String {
        Log.d(
            "VoucherOcr", """
            ═══════════════════════════════════
            🔍 CLASIFICANDO TIPO DE VOUCHER
            Texto (primeros 200 chars):
            ${tOriginal.take(200)}
            ═══════════════════════════════════
        """.trimIndent()
        )

        val destino = detectarDestinoApp(tLower)
        val origen = detectarOrigenApp(tLower)
        val haySeguridad = tieneCodigoSeguridadYape(tLower)

        Log.d(
            "VoucherOcr", """
            📊 Análisis:
            - Origen: ${origen ?: "NO DETECTADO"}
            - Destino: ${destino ?: "NO DETECTADO"}
            - Cód. Seguridad: ${if (haySeguridad) "SÍ" else "NO"}
        """.trimIndent()
        )

        // 1) plin → yape (CASO MÁS COMÚN DE TU ERROR)
        if (origen == "plin" && destino == "yape") {
            Log.d("VoucherOcr", "✅ CLASIFICADO: plin_yape")
            return "plin_yape"
        }

        // 2) yape → plin
        if (origen == "yape" && destino == "plin") {
            Log.d("VoucherOcr", "✅ CLASIFICADO: yape_plin")
            return "yape_plin"
        }

        // 3) yape → yape (con código de seguridad)
        if (haySeguridad && (destino == "yape" || origen == "yape")) {
            Log.d("VoucherOcr", "✅ CLASIFICADO: yape_yape (tiene código seguridad)")
            return "yape_yape"
        }

        // 4) Fallback: si detectamos plin sin destino claro, asumimos plin_yape
        if (origen == "plin" && destino == null) {
            Log.d("VoucherOcr", "⚠️ FALLBACK: plin_yape (origen plin, destino no claro)")
            return "plin_yape"
        }

        // 5) Fallback: si detectamos solo destino yape, verificar origen
        if (destino == "yape" && origen == null) {
            // Si no hay código de seguridad, probablemente es plin_yape
            if (!haySeguridad) {
                Log.d("VoucherOcr", "⚠️ FALLBACK: plin_yape (destino yape, sin cód. seguridad)")
                return "plin_yape"
            } else {
                Log.d("VoucherOcr", "⚠️ FALLBACK: yape_yape (destino yape, con cód. seguridad)")
                return "yape_yape"
            }
        }

        // 6) Si nada calza claramente
        Log.w("VoucherOcr", "⚠️ CLASIFICADO: desconocido (requiere revisión manual)")
        return "desconocido"
    }

    // ================== helpers ==================

    private fun normalizar(s: String): String {
        // Quita dobles espacios, convierte a una línea legible, homogeneiza separadores
        return s.replace("\r", " ")
            .replace("\n", " ")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun normalizarNumero(num: String): Double? {
        val s = num.trim()

        return try {
            when {
                // Formato tipo "1.234,56" (punto miles, coma decimales)
                s.contains('.') && s.contains(',') -> {
                    val sinPuntos = s.replace(".", "")
                    sinPuntos.replace(",", ".").toDouble()
                }

                // Solo coma → la tratamos como punto decimal
                s.contains(',') -> {
                    s.replace(",", ".").toDouble()
                }

                // Solo punto o solo enteros → usamos directo
                else -> {
                    s.toDouble()
                }
            }
        } catch (_: Exception) {
            null
        }
    }
}