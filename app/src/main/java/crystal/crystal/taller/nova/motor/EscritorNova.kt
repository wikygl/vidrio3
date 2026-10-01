package crystal.crystal.taller.nova.motor

import crystal.crystal.taller.nova.NovaCalculos

/**
 * El diseño simbólico (el paquete de novaDiseño, del dibujo y del archivo) escrito desde la MISMA
 * ventana que calcula el motor (fase 4 del motor único, 2026-09-30).
 *
 * Antes el paquete lo armaba otro código (`NovaUIHelper.generarTramosConsolidado`), que decidía por
 * su cuenta cuántos tramos y hojas llevaba la ventana y con qué medidas: el dibujo y los materiales
 * podían no coincidir (la curva en S dibujada en 139.1 + 139.1 y calculada en 165.4 + 110.2). Ahora
 * los dos salen de [VentanaNova].
 *
 * Formato de siempre (sección 14.4 de docs/REGLAS_NOVA.md):
 * `Tl<ancho>(s<hoja>(f<w>c<w>…);m<mocheta>(f<w>…)) P<2.5> Tl<…>(…)`. La mocheta se escribe como
 * espacio, sin el puente (N-31; C-25). Solo el reparto sale de aquí: los adornos que trae la medida
 * (la esquina `A<90>`, la panza de la curva, el círculo, la silueta de cada cara) los pone la pantalla.
 */
object EscritorNova {

    /**
     * Los tramos de un lado. [consolidado]: un solo `Tl` con los parantes dentro (`;P;`), como lo
     * leen las geometrías compuestas (L, C, serie, curva, círculo); si no, un `Tl` por tramo unidos
     * por `P<2.5>`.
     */
    fun tramos(v: VentanaNova, l: LadoNova, consolidado: Boolean): String {
        if (l.tramos.isEmpty()) return ""
        val escala = escalaDeDibujo(v, l)
        val bloques = l.tramos.map { t ->
            val anchos = t.modulos.map { it.ancho * escala }
            val ancho = anchos.sum()
            val mods = t.modulos.zip(anchos).joinToString("") { (m, w) -> "${if (m.corrediza) 'c' else 'f'}<${df(w)}>" }
            val n = NovaCalculos.anchMota(ancho)
            val panos = (1..n).joinToString("") { "f<${df(ancho / n)}>" }
            Triple(ancho, mods, panos) to MotorNova.verticalDe(v, l, t)
        }
        if (!consolidado || bloques.size == 1) {
            return bloques.joinToString(" P<2.5> ") { (b, vt) ->
                "Tl<${df(b.first)}>(${franjas(vt, l, b.second, b.third)})"
            }
        }
        // Consolidado: la vertical es la misma en todos los tramos del lado (Nova con ceil).
        val vt = bloques.first().second
        val mods = bloques.joinToString(";P;") { it.first.second }
        val panos = bloques.joinToString(";P;") { it.first.third }
        val ancho = bloques.sumOf { it.first.first.toDouble() }.toFloat() + 2.5f * (bloques.size - 1)
        return "Tl<${df(ancho)}>(${franjas(vt, l, mods, panos)})"
    }

    /** Cada tramo del lado por separado (`Tl<…>(…)`), para ponerle a cada uno su propio tag. */
    fun tramosPorSeparado(v: VentanaNova, l: LadoNova): List<String> {
        val solo = l.copy(tramos = emptyList())
        return l.tramos.map { t -> tramos(v, solo.copy(tramos = listOf(t), anchoCalculo = t.ancho * escalaDeDibujo(v, l)), false) }
    }

    /** El paquete de una ventana plana: `{nova,acabado,[ancho,alto:tramos]}`. */
    fun paquetePlano(v: VentanaNova, acabado: String, consolidado: Boolean = false, tag: String = ""): String {
        val l = v.lados.first()
        val cuerpo = tramos(v, l, consolidado).let { if (tag.isBlank()) it else conTagEnElSistema(it, tag) }
        return "{nova,$acabado,[${df(l.anchoMedido)},${df(l.altoMedido)}:$cuerpo]}"
    }

    private val TUBO = Puente("tubo 2⅜ x 1", 2.5f, 6f)

    private fun ventanaAutomatica(
        acabado: String, ancho: Float, alto: Float, altoHoja: Float, divisiones: Int, modulacion: String,
        remate: String, mochetaInferior: Float, puente: Puente
    ): VentanaNova {
        val a = if (acabado == "apa") Acabado.APA else Acabado.INA
        val lado = DisenoAutomatico.lado(DisenoAutomatico.Entrada(anchoMedido = ancho, anchoCalculo = ancho, alto = alto,
            hoja = altoHoja, acabado = a, puente = puente, divisManual = divisiones, modulacion = modulacion))
        val modelo = when (remate) { "nr" -> ModeloMocheta.INVERTIDO; "np", "nci" -> ModeloMocheta.BANDERA; else -> ModeloMocheta.NORMAL }
        return VentanaNova(a, modelo, listOf(lado), PerfilU(1f, 1.5f), puente, mochetaInferior = mochetaInferior)
    }

    /**
     * El paquete de una ventana plana de Nova con ceil, con los datos sueltos (lo usan las pruebas que
     * antes lo pedían a `NovaUIHelper.generarPaqueteSimbolico`).
     */
    fun paqueteAutomatico(
        acabado: String, ancho: Float, alto: Float, altoHoja: Float, divisiones: Int, modulacion: String,
        mochetaInferior: Float = 0f, remate: String = "nn", puente: Puente = TUBO
    ): String = paquetePlano(ventanaAutomatica(acabado, ancho, alto, altoHoja, divisiones, modulacion, remate, mochetaInferior, puente), acabado)

    /** Los tramos consolidados de un lado de Nova con ceil (antes `NovaUIHelper.generarTramosConsolidado`). */
    fun tramosAutomaticos(
        ancho: Float, alto: Float, altoHoja: Float, divisiones: Int, modulacion: String = "nn",
        remate: String = "nn", acabado: String = "apa", puente: Puente = TUBO
    ): String {
        val v = ventanaAutomatica(acabado, ancho, alto, altoHoja, divisiones, modulacion, remate, 0f, puente)
        return tramos(v, v.lados.first(), consolidado = true)
    }

    /** Las franjas de un tramo, de abajo arriba (N-33): la mocheta de abajo, el sistema, la de arriba. */
    private fun franjas(vt: MotorNova.VerticalTramo, l: LadoNova, mods: String, panos: String): String {
        if (!vt.hayMocheta) return "s<>($mods)"
        val partes = mutableListOf<String>()
        vt.mochetas.filter { !it.first }.forEach { partes.add("m<${df(it.second)}>($panos)") }
        partes.add("s<${df(vt.altoHoja)}>($mods)")
        vt.mochetas.filter { it.first }.forEach { partes.add("m<${df(it.second)}>($panos)") }
        return partes.joinToString(";")
    }

    /**
     * En inaparente la estructura de una esquina llega hasta el parante de esquina (N-75), pero el
     * dibujo muestra el lado entero, que es como se lee el paquete: los módulos se escriben con el
     * ancho del vidrio. En aparente, tal cual.
     */
    private fun escalaDeDibujo(v: VentanaNova, l: LadoNova): Float {
        if (v.acabado == Acabado.APA) return 1f
        val suma = l.tramos.sumOf { it.ancho.toDouble() }.toFloat()
        val lleno = l.anchoCalculo - 2.5f * (l.tramos.size - 1)
        return if (suma > 0f && lleno > 0f) lleno / suma else 1f
    }

    /** Mete un tag (el arco `U<…>`, el círculo `O<1>`) al final de la primera franja de sistema. */
    fun conTagEnElSistema(tramos: String, tag: String): String {
        val m = Regex("s<[^>]*>\\(").find(tramos) ?: return tramos
        var nivel = 0
        for (i in m.range.last until tramos.length) {
            when (tramos[i]) {
                '(' -> nivel++
                ')' -> { nivel--; if (nivel == 0) return tramos.substring(0, i) + tag + tramos.substring(i) }
            }
        }
        return tramos
    }

    private fun df(v: Float): String = NovaCalculos.df1(v)
}
