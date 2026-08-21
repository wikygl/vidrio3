package crystal.crystal.taller.puerta

// Descriptor de una puerta: todos los parámetros necesarios para REDIBUJARLA desde cero
// (equivalente al "diseño simbólico" de las novas). Se guarda al archivar y se usa para
// regenerar el gráfico/plano en FichaActivity sin almacenar imágenes.
object PuertaDescriptor {

    const val CLAVE = "DisenoPuerta"

    data class Datos(
        val modelo: String,
        val variante: String,
        val ancho: Float,
        val alto: Float,
        val hoja: Float,        // entrada "altura de puente" (hHoja)
        val piso: Float,
        val zocalos: Int,
        val divisiones: Int,
        val angulo: Float,
        val marco: Float,
        val puente: Float,
        val inox: Float,
        val ventanaIzq: Boolean,
        val ventanaDer: Boolean,
        val cliente: String = "",
        // Aluminio del interior del bastidor (Viky). Va al final para no correr las posiciones de
        // los descriptores ya archivados, que no lo traen y asumen bastidor.
        val interior: Float = crystal.crystal.taller.puerta.logica.CalculosPuerta.BASTIDOR,
        // Rejilla de Jeny: columnas y filas que se repiten dentro de cada paño.
        val rejillaCols: Int = 3,
        val rejillaFilas: Int = 3,
        // Juego entre la hoja y el marco, a lo ancho. Es el centímetro que siempre se descontaba a
        // ojo; las puertas archivadas antes de que fuera editable no lo traen y asumen ese 1.
        val holgura: Float = 1f
    )

    // Formato posicional separado por '|' (los textos no usan ese carácter).
    fun serializar(d: Datos): String = listOf(
        d.modelo,
        d.variante,
        d.ancho, d.alto, d.hoja, d.piso,
        d.zocalos, d.divisiones, d.angulo,
        d.marco, d.puente, d.inox,
        if (d.ventanaIzq) 1 else 0,
        if (d.ventanaDer) 1 else 0,
        d.cliente.replace("|", " "),
        d.interior,
        d.rejillaCols,
        d.rejillaFilas,
        d.holgura
    ).joinToString("|")

    fun parsear(s: String): Datos? {
        val p = s.split("|")
        if (p.size < 14) return null
        return try {
            Datos(
                modelo = p[0],
                variante = p[1],
                ancho = p[2].toFloat(),
                alto = p[3].toFloat(),
                hoja = p[4].toFloat(),
                piso = p[5].toFloat(),
                zocalos = p[6].toInt(),
                divisiones = p[7].toInt(),
                angulo = p[8].toFloat(),
                marco = p[9].toFloat(),
                puente = p[10].toFloat(),
                inox = p[11].toFloat(),
                ventanaIzq = p[12] == "1",
                ventanaDer = p[13] == "1",
                cliente = p.getOrNull(14) ?: "",
                interior = p.getOrNull(15)?.toFloatOrNull()
                    ?: crystal.crystal.taller.puerta.logica.CalculosPuerta.BASTIDOR,
                rejillaCols = p.getOrNull(16)?.toIntOrNull() ?: 3,
                rejillaFilas = p.getOrNull(17)?.toIntOrNull() ?: 3,
                holgura = p.getOrNull(18)?.toFloatOrNull() ?: 1f
            )
        } catch (e: Exception) {
            null
        }
    }
}
