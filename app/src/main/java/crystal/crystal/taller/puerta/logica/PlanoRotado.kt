package crystal.crystal.taller.puerta.logica

import crystal.crystal.taller.puerta.modelos.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object PlanoRotado {

    fun generarDatosPlano(paflon: Float, paranteInterno: Float, nDiv: Int, bastidor: Float): DatosPlano {
        val ancho = paflon
        val alto = paranteInterno
        val eje = Punto(ancho / 2, alto / 2)
        val cantidadPares = nDiv - 1
        val pares = mutableListOf<ParLineas>()
        for (i in 1..cantidadPares) {
            val y1 = divisiones(i, bastidor)
            val y2 = divisiones(i, bastidor, segundo = true)
            val l1 = Linea(Punto(0f, y1), Punto(ancho, y1))
            val l2 = Linea(Punto(0f, y2), Punto(ancho, y2))
            pares.add(ParLineas(l1, l2))
        }
        return DatosPlano(eje, pares)
    }

    private fun divisiones(i: Int, bastidor: Float, segundo: Boolean = false): Float {
        // Replica: y = divisiones()*i + bastidor*(i-1 o i)
        // Aquí usamos i * 1f como "tamaño de división" genérico; si necesitas el real pásalo como parámetro.
        val tamDiv = 1f
        val base = tamDiv * i
        val sum = if (segundo) bastidor * i else bastidor * (i - 1)
        return base + sum
    }

    private fun calcularIntersecciones(d: Float, angulo: Float, centro: Punto, ancho: Float, alto: Float): List<Punto> {
        val rad = Math.toRadians(angulo.toDouble())
        val sinAng = sin(rad).toFloat()
        val cosAng = cos(rad).toFloat()
        val puntos = mutableListOf<Punto>()
        if (cosAng != 0f) {
            val y0 = centro.y + (d - sinAng * centro.x) / cosAng
            if (y0 in 0f..alto) puntos.add(Punto(0f, y0))
            val yAncho = centro.y + (d + sinAng * (ancho - centro.x)) / cosAng
            if (yAncho in 0f..alto) puntos.add(Punto(ancho, yAncho))
        }
        if (sinAng != 0f) {
            val x0 = centro.x - (d + cosAng * centro.y) / sinAng
            if (x0 in 0f..ancho) puntos.add(Punto(x0, 0f))
            val xAlto = centro.x - (d - cosAng * (alto - centro.y)) / sinAng
            if (xAlto in 0f..ancho) puntos.add(Punto(xAlto, alto))
        }
        return puntos.distinctBy { it.x to it.y }
    }

    fun rotarDatosPlano(datos: DatosPlano, angulo: Float, ancho: Float, alto: Float): DatosPlano {
        val eje = datos.eje
        val nuevos = mutableListOf<ParLineas>()
        datos.paresLineas.forEach { par ->
            val d1 = par.linea1.inicio.y - eje.y
            val d2 = par.linea2.inicio.y - eje.y
            val p1 = calcularIntersecciones(d1, angulo, eje, ancho, alto)
            val p2 = calcularIntersecciones(d2, angulo, eje, ancho, alto)
            val nl1 = if (p1.size >= 2) Linea(p1[0], p1[1]) else null
            val nl2 = if (p2.size >= 2) Linea(p2[0], p2[1]) else null
            if (nl1 != null && nl2 != null) nuevos.add(ParLineas(nl1, nl2))
        }
        return DatosPlano(eje, nuevos)
    }

    private fun distancia(l: Linea): Float = sqrt((l.fin.x - l.inicio.x) * (l.fin.x - l.inicio.x) + (l.fin.y - l.inicio.y) * (l.fin.y - l.inicio.y))

    fun obtenerTextoDistancias(datos: DatosPlano): String = buildString {
        datos.paresLineas.forEachIndexed { idx, par ->
            append("Par de líneas ${idx + 1}:\n")
            append(" línea 1: ${CalculosPuerta.df1(distancia(par.linea1))} cm\n")
            append(" línea 2: ${CalculosPuerta.df1(distancia(par.linea2))} cm\n")
        }
    }

    fun agruparMedidasDesdeTexto(texto: String): String {
        val regex = """\b(\d+(?:\.\d+)?)\s*cm""".toRegex()
        val todas = regex.findAll(texto).map { it.groupValues[1] }.toList()
        val mayores = mutableListOf<String>()
        for (i in todas.indices step 2) {
            if (i + 1 < todas.size) {
                val m1 = todas[i].toFloat(); val m2 = todas[i + 1].toFloat()
                mayores += if (m1 >= m2) todas[i] else todas[i + 1]
            }
        }
        val frecuencias = mayores.groupingBy { it }.eachCount()
        return frecuencias.entries.sortedBy { it.key.toFloat() }
            .joinToString("\n") { "${it.key} = ${it.value}" }
    }
}