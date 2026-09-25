package crystal.crystal.taller.nova.motor

/**
 * Arma la [VentanaNova] de Nova con ceil a partir de lo que se escribe en la pantalla: los lados
 * medidos, cómo terminan y los perfiles. Aquí viven los descuentos de aparente que antes estaban
 * repartidos en la pantalla (N-37, N-38, N-78):
 *
 * - Lado lateral libre (APA): el ancho pierde el perfil que lo cierra.
 * - Lado arriba o abajo libre (APA): el alto pierde el falso puente.
 * - Esquina en ángulo (APA): cada lado pierde su parte del esquinero, con reparto invertido.
 *
 * Inaparente no descuenta nada; su esquina suma el ancho de la U, y eso lo hace el motor.
 */
object VentanaAutomatica {

    data class LadoMedido(val ancho: Float, val alto: Float, val hoja: Float, val divisManual: Int = 0)

    data class Entrada(
        val acabado: Acabado,
        val modelo: ModeloMocheta,
        val modulacion: String,
        val lados: List<LadoMedido>,
        val u: PerfilU,
        val puente: Puente,
        val cruce: Float = 0.7f,
        val mochetaInferior: Float = 0f,
        val izquierda: Borde = Borde.MURO,
        val derecha: Borde = Borde.MURO,
        val libresVerticales: Int = 0,
        /** Perfil que cierra un lado libre (su ancho o su alto). */
        val perfilLibre: Float = 2.5f,
        /** Esquinero de las L y C; null = "ninguno" (ni descuento ni parante de esquina). */
        val esquinero: Puente? = null,
        /** Esquina curva entre lados (curva de esquina): ni aumento ni descuento. */
        val esquinaCurva: Boolean = false,
        val repartoManual: List<Int>? = null,
        val corredizasPorTramoNfc: Int = 0
    )

    fun ventana(e: Entrada): VentanaNova {
        val apa = e.acabado == Acabado.APA
        val n = e.lados.size
        val anchosEsquina = if (apa && !e.esquinaCurva) descuentoEsquinas(e.lados.map { it.ancho }, e.esquinero)
                            else List(n) { 0f }
        val esquina = if (e.esquinaCurva) Borde.ESQUINA_CURVA else Borde.ESQUINA_ANGULO
        val lados = e.lados.mapIndexed { i, l ->
            val izq = if (i == 0) e.izquierda else esquina
            val der = if (i == n - 1) e.derecha else esquina
            val libresLaterales = listOf(izq, der).count { it == Borde.LIBRE }
            val anchoCalculo = l.ancho - anchosEsquina[i] - (if (apa) libresLaterales * e.perfilLibre else 0f)
            val altoCalculo = l.alto - (if (apa) e.libresVerticales * e.perfilLibre else 0f)
            DisenoAutomatico.lado(DisenoAutomatico.Entrada(
                anchoMedido = l.ancho, anchoCalculo = anchoCalculo.coerceAtLeast(0f),
                alto = altoCalculo, altoMedido = l.alto, hoja = l.hoja,
                acabado = e.acabado, puente = e.puente, divisManual = l.divisManual,
                modulacion = e.modulacion, repartoManual = e.repartoManual,
                corredizasPorTramoNfc = e.corredizasPorTramoNfc,
                izquierda = izq, derecha = der
            ))
        }
        return VentanaNova(
            e.acabado, e.modelo, lados, e.u, e.puente, e.cruce, e.mochetaInferior,
            libresVerticales = e.libresVerticales, perfilLibre = e.perfilLibre,
            esquinero = if (n > 1) e.esquinero else null
        )
    }

    /**
     * N-38: en cada esquina de aparente los dos lados se reparten el esquinero. Si su medida normal
     * y la de esquina son iguales, cada lado descuenta esa; si no, reparto invertido: el lado más
     * ancho se lleva la parte menor. El lado del medio de una C descuenta sus dos esquinas.
     */
    fun descuentoEsquinas(anchos: List<Float>, esquinero: Puente?): List<Float> {
        val out = FloatArray(anchos.size)
        if (esquinero == null) return out.toList()
        val normal = esquinero.altura
        val deEsquina = esquinero.esquinero
        for (i in 0 until anchos.size - 1) {
            if (kotlin.math.abs(deEsquina - normal) < 0.001f) {
                out[i] += normal; out[i + 1] += normal
            } else {
                val mayor = maxOf(normal, deEsquina)
                val menor = minOf(normal, deEsquina)
                if (anchos[i] >= anchos[i + 1]) { out[i] += menor; out[i + 1] += mayor }
                else { out[i] += mayor; out[i + 1] += menor }
            }
        }
        return out.toList()
    }
}
