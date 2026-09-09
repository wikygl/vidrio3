package crystal.crystal.taller

/**
 * Arma la cadena con la que cada calculadora archiva un producto. Está aquí y no en cada módulo
 * porque el ORDEN de los campos es la parte que importa y las copias se desincronizaban.
 *
 * ```
 * C<cliente>-P<producto>-G<geometría>-M<medidas>-T<diseño>-MAT<…>-ACC<…>
 * ```
 *
 * El orden no es decorativo, cada campo está donde sirve:
 *
 * 1. **Cliente**: un contrato suele tener varios productos; primero se reconoce de quién es.
 * 2. **Producto** (producto, línea, acabado, tipo, numeración): dice a qué calculadora va y
 *    permite compararlo con los otros productos del mismo cliente para numerarlo.
 * 3. **Geometría** (volumen, forma, encuentro, modelo): ya dentro de la calculadora, decide cómo
 *    tratar los datos.
 * 4. **Medidas** (ancho1, ancho2…, alto1, alto2…, altura de puente, alféizar, dintel, cantidad):
 *    la cantidad de anchos dice en cuántos lados se resuelve la geometría.
 * 5. **Diseño**: los tramos, sin repetir el sistema ni las medidas que ya van arriba.
 * 6. **Material** y 7. **Accesorios**.
 *
 * Ver `docs/REGLAS_NOVA.md` §13.
 */
object PaqueteV2 {

    /**
     * Deja un valor listo para meterlo en un campo: los `<` `>` romperían los delimitadores y el
     * `-` separaría campos de más.
     */
    fun escapar(raw: String): String {
        return raw
            .replace("\n", " / ")
            .replace("\r", " ")
            .replace("-", "_")
            .replace("<", "(")
            .replace(">", ")")
            .trim()
    }

    /**
     * [diseno] va vacío en los productos que todavía no tienen diseño simbólico. [sufijos] es lo
     * que cada módulo ya arma por su cuenta (`-MAT<…>` y, si lo usa, `-ACC<…>`).
     */
    fun construir(
        cliente: String,
        producto: String,
        geometria: String,
        medidas: String,
        diseno: String = "",
        sufijos: String = ""
    ): String = buildString {
        append("C<").append(cliente).append(">")
        append("-P<").append(producto).append(">")
        append("-G<").append(geometria).append(">")
        append("-M<").append(medidas).append(">")
        if (diseno.isNotBlank()) append("-T<").append(diseno).append(">")
        append(sufijos)
    }
}
