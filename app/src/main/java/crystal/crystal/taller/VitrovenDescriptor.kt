package crystal.crystal.taller

// Descriptor de una vitroventana: todo lo necesario para REDIBUJARLA desde cero, igual que
// PuertaDescriptor hace con las puertas. Se guarda al archivar y FichaActivity lo usa para
// regenerar el gráfico sin almacenar imágenes.
//
// El diseño simbólico por sí solo no alcanza: trae las medidas y los módulos, pero no los clips
// (que se dibujan) ni la dirección de segmentado, y esos no se pueden deducir del texto.
object VitrovenDescriptor {

    const val CLAVE = "DisenoVitroven"

    data class Datos(
        val ancho: Float,
        val alto: Float,
        val clips: Int,
        val clasificacion: String,
        val simbolico: String,
        val direccionVertical: Boolean
    )

    // Formato posicional separado por '|'. El simbólico va último porque es el único campo que
    // podría crecer; los demás son valores simples.
    fun serializar(d: Datos): String = listOf(
        d.ancho,
        d.alto,
        d.clips,
        d.clasificacion.replace("|", " "),
        if (d.direccionVertical) 1 else 0,
        d.simbolico.replace("|", " ")
    ).joinToString("|")

    fun parsear(s: String): Datos? {
        val p = s.split("|")
        if (p.size < 6) return null
        return try {
            Datos(
                ancho = p[0].toFloat(),
                alto = p[1].toFloat(),
                clips = p[2].toInt(),
                clasificacion = p[3],
                direccionVertical = p[4] == "1",
                simbolico = p[5]
            )
        } catch (_: Exception) {
            null
        }
    }
}
