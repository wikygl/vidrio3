package crystal.crystal.taller.nova.motor

import crystal.crystal.Diseno.nova.DisenoNova
import crystal.crystal.Diseno.nova.NovaFranja

/**
 * Nova desigual: el diseño hecho a mano pasa al mismo motor que el automático (N-70, N-81).
 *
 * La cadena la lee **un solo lector**, [DisenoNova.desdePaquete], el mismo que usan el dibujo y el
 * 3D. Aquí solo se traduce ese modelo a [VentanaNova]:
 *
 * - Cada `A<90>` empieza un lado nuevo (L, C, serie), unido al anterior por una esquina.
 * - Los parantes de dentro de la franja de sistema (`;P;`, el formato de las esquinas) parten el
 *   tramo como un parante normal.
 * - Cada tramo lleva sus franjas: `s;m` normal, `m;s` invertido, `m;s;m` bandera (N-33), y en un
 *   diseño compuesto (N-89) cada tramo el suyo, con su propia altura de hoja. Las alturas
 *   dibujadas de las mochetas valen como proporción (ver [FranjaTramo]).
 * - La hoja es el alto de la franja de sistema. Las mochetas se calculan con la regla (alto − hoja −
 *   puente), no con la medida dibujada, que es aproximada (N-80).
 */
object DesdeDiseno {

    fun ventana(
        paquete: String,
        u: PerfilU,
        puente: Puente,
        cruce: Float = 0.7f,
        izquierda: Borde = Borde.MURO,
        derecha: Borde = Borde.MURO,
        esquinero: Puente? = null,
        /** La modulación que eligió el usuario (el paquete no la guarda); null = deducirla. */
        modulacion: String? = null
    ): VentanaNova? {
        val d = DisenoNova.desdePaquete(paquete) ?: return null
        val acabado = if (d.acabado == "apa") Acabado.APA else Acabado.INA
        // Cada tramo lleva sus propias franjas (compuesto, N-89); el modelo de la ventana es solo
        // el del primer tramo, para lo que no va por tramo.
        val modelo = modeloDe(d.tramos.first().franjas)

        // Lados: un A<90> delante de un tramo abre un lado nuevo.
        val grupos = mutableListOf<MutableList<crystal.crystal.Diseno.nova.NovaTramo>>()
        d.tramos.forEach { t -> if (grupos.isEmpty() || t.pliegue != null) grupos.add(mutableListOf()); grupos.last().add(t) }

        val lados = grupos.mapIndexed { i, tramosLado ->
            val tramosLeidos = tramosLado.flatMap { tramoDelMotor(it) }
            // El paquete guarda los anchos truncados a un decimal (143 / 3 → f<47.6>, que suman
            // 142.8). En un lado solo, el ancho de la cabecera es el verdadero: se reescalan los
            // módulos para que sumen el ancho útil, como los reparte el editor.
            val tramos = if (grupos.size == 1) reescalar(tramosLeidos, d.ancho - puente.anchoParante * (tramosLeidos.size - 1))
                         else tramosLeidos
            val anchoLado = if (grupos.size == 1) d.ancho
                            else tramos.sumOf { it.ancho.toDouble() }.toFloat() + puente.anchoParante * (tramos.size - 1)
            val sistema = tramosLado.first().sistema
            val altoHoja = sistema?.alto?.takeIf { it > 0f && it < d.alto } ?: d.alto
            LadoNova(
                anchoMedido = anchoLado, anchoCalculo = anchoLado,
                alto = d.alto, altoMedido = d.alto, altoHoja = altoHoja,
                tramos = tramos,
                izquierda = if (i == 0) izquierda else Borde.ESQUINA_ANGULO,
                derecha = if (i == grupos.lastIndex) derecha else Borde.ESQUINA_ANGULO,
                modulacion = modulacion ?: modulacionDe(tramos)
            )
        }
        return VentanaNova(acabado, modelo, lados, u, puente, cruce, esquinero = esquinero)
    }

    private fun reescalar(tramos: List<Tramo>, anchoUtil: Float): List<Tramo> {
        val suma = tramos.sumOf { it.ancho.toDouble() }.toFloat()
        if (suma <= 0f || anchoUtil <= 0f) return tramos
        val k = anchoUtil / suma
        return tramos.map { t ->
            val modulos = t.modulos.map { it.copy(ancho = it.ancho * k) }
            val anchoTramo = modulos.sumOf { it.ancho.toDouble() }.toFloat()
            // Los paños de mocheta también vienen truncados: se ajustan al ancho del tramo.
            val panos = t.panosMocheta?.let { p ->
                val sp = p.sum()
                if (sp > 0f) p.map { it * anchoTramo / sp } else p
            }
            t.copy(modulos = modulos, panosMocheta = panos)
        }
    }

    private fun modeloDe(franjas: List<NovaFranja>): ModeloMocheta {
        val orden = franjas.map { if (it.esSistema) 's' else 'm' }.joinToString("")
        return when (orden) {
            "ms" -> ModeloMocheta.INVERTIDO
            "msm" -> ModeloMocheta.BANDERA
            else -> ModeloMocheta.NORMAL
        }
    }

    /** Un tramo del diseño en tramos del motor: los `;P;` de su franja de sistema lo parten. */
    private fun tramoDelMotor(t: crystal.crystal.Diseno.nova.NovaTramo): List<Tramo> {
        val s = t.sistema ?: return emptyList()
        val sinAncho = s.modulos.count { it.ancho == null }
        val conAncho = s.modulos.sumOf { (it.ancho ?: 0f).toDouble() }.toFloat()
        val resto = if (sinAncho > 0) ((t.ancho - conAncho) / sinAncho).coerceAtLeast(0f) else 0f
        val modulos = s.modulos.map { Modulo(!it.esFijo, it.ancho ?: resto) }
        // Paños de mocheta dibujados: los de la primera mocheta del tramo.
        val panos = t.mochetas.firstOrNull()?.modulos?.map { it.ancho ?: 0f }?.takeIf { p -> p.all { it > 0f } }
        // Las franjas del tramo, de abajo arriba, con su alto dibujado (N-33, N-89). Una mocheta con
        // una corrediza pasa a ser franja de sistema (N-88), con sus módulos.
        val franjas = t.franjas.map { f ->
            val mods = f.modulos.map { Modulo(!it.esFijo, it.ancho ?: (t.ancho / f.modulos.size)) }
            FranjaTramo(f.esSistema || mods.any { it.corrediza }, f.alto, mods)
        }
        if (s.parantes.isEmpty()) return listOf(Tramo(modulos, panos, franjas))
        val cortes = s.parantes.map { it + 1 }.filter { it in 1 until modulos.size }.distinct().sorted()
        val out = mutableListOf<Tramo>()
        var desde = 0
        for (c in cortes + modulos.size) {
            val trozo = modulos.subList(desde, c)
            out.add(Tramo(trozo, franjas = franjas))   // sus paños de mocheta se reparten solos (N-62)
            desde = c
        }
        return out
    }

    /**
     * Si no se sabe la modulación, lo único que el motor necesita saber es si son solo corredizas o
     * solo fijos. Solo corredizas se toma como full corredizas (su vidrio va sin el −1.4, N-59).
     */
    private fun modulacionDe(tramos: List<Tramo>): String {
        val mods = tramos.flatMap { it.modulos }
        return when {
            mods.isNotEmpty() && mods.all { it.corrediza } -> "nfc"
            mods.isNotEmpty() && mods.none { it.corrediza } -> "nff"
            else -> "nn"
        }
    }
}
