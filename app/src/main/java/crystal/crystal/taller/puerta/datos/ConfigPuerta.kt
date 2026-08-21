package crystal.crystal.taller.puerta.datos

import android.content.Context
import crystal.crystal.taller.puerta.logica.CalculosPuerta

/**
 * Los perfiles y holguras con los que trabaja el taller. Son los mismos para todas las puertas y
 * casi nunca cambian, así que no tiene sentido volver a escribirlos en cada medición: se guardan
 * con un nombre —"Paflón 1½", "Tubo 3.8", lo que el vidriero decida— y se aplican de un toque.
 *
 * Además del juego de configuraciones con nombre se recuerda LA ÚLTIMA usada, que es la que se
 * carga al abrir el módulo. Así, aunque nadie guarde nada, la app no vuelve a los valores de
 * fábrica cada vez que se entra.
 */
object ConfigPuerta {

    /** La de fábrica: [Valores] sin tocar. No se guarda ni se borra, siempre está para volver. */
    const val PREDETERMINADA = "Predeterminada"

    private const val PREFS = "puertas_config"
    private const val CLAVE_ULTIMA = "ultima"
    private const val CLAVE_GUARDADAS = "guardadas"
    private const val SEP_CAMPOS = "|"
    private const val SEP_REGISTROS = "\n"

    data class Valores(
        val marco: Float = 2.2f,
        val puente: Float = 2.5f,
        val inox: Float = 2.5f,
        val bastidor: Float = CalculosPuerta.BASTIDOR,
        val interior: Float = CalculosPuerta.BASTIDOR,
        // Juego entre la hoja y el marco, a lo ancho: el centímetro de toda la vida.
        val holgura: Float = 1f
    )

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // Formato posicional, igual que el descriptor: los campos nuevos se agregan al final y los
    // registros viejos siguen leyéndose con el valor de siempre.
    private fun serializar(v: Valores): String =
        listOf(v.marco, v.puente, v.inox, v.bastidor, v.interior, v.holgura).joinToString(SEP_CAMPOS)

    private fun parsear(s: String): Valores? {
        val p = s.split(SEP_CAMPOS)
        if (p.size < 5) return null
        return Valores(
            marco = p[0].toFloatOrNull() ?: return null,
            puente = p[1].toFloatOrNull() ?: return null,
            inox = p[2].toFloatOrNull() ?: return null,
            bastidor = p[3].toFloatOrNull() ?: return null,
            interior = p[4].toFloatOrNull() ?: return null,
            holgura = p.getOrNull(5)?.toFloatOrNull() ?: 1f
        )
    }

    /** Lo último que se usó; si nunca se tocó nada, los valores de siempre. */
    fun ultima(context: Context): Valores =
        prefs(context).getString(CLAVE_ULTIMA, null)?.let { parsear(it) } ?: Valores()

    fun recordarUltima(context: Context, v: Valores) {
        prefs(context).edit().putString(CLAVE_ULTIMA, serializar(v)).apply()
    }

    /** Configuraciones con nombre, en el orden en que se guardaron. */
    fun guardadas(context: Context): Map<String, Valores> {
        val crudo = prefs(context).getString(CLAVE_GUARDADAS, "").orEmpty()
        if (crudo.isBlank()) return emptyMap()
        return crudo.split(SEP_REGISTROS).mapNotNull { linea ->
            val corte = linea.indexOf('=')
            if (corte <= 0) return@mapNotNull null
            val nombre = linea.substring(0, corte)
            parsear(linea.substring(corte + 1))?.let { nombre to it }
        }.toMap()
    }

    /** Guarda con ese nombre; si ya existía, lo reemplaza (mismo nombre = misma configuración). */
    fun guardar(context: Context, nombre: String, v: Valores) {
        val limpio = nombre.trim().replace(SEP_REGISTROS, " ").replace("=", " ")
        // Ese nombre está reservado: es la salida a los valores de fábrica y no se puede tapar.
        if (limpio.isBlank() || limpio.equals(PREDETERMINADA, ignoreCase = true)) return
        escribir(context, guardadas(context) + (limpio to v))
    }

    fun borrar(context: Context, nombre: String) {
        escribir(context, guardadas(context) - nombre)
    }

    private fun escribir(context: Context, mapa: Map<String, Valores>) {
        val crudo = mapa.entries.joinToString(SEP_REGISTROS) { "${it.key}=${serializar(it.value)}" }
        prefs(context).edit().putString(CLAVE_GUARDADAS, crudo).apply()
    }
}
