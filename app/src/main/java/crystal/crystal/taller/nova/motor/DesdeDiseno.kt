package crystal.crystal.taller.nova.motor

import crystal.crystal.Diseno.nova.DisenoNova
import crystal.crystal.Diseno.nova.HerramientasDiseno
import crystal.crystal.Diseno.nova.NovaFranja
import kotlin.math.abs

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

        // Lados: un A<90> delante de un tramo abre un lado nuevo. Se guardan los índices del diseño:
        // la geometría de cada tramo (su vano, sus rectángulos) se mide sobre el diseño entero.
        val grupos = mutableListOf<MutableList<Int>>()
        d.tramos.forEachIndexed { k, t -> if (grupos.isEmpty() || t.pliegue != null) grupos.add(mutableListOf()); grupos.last().add(k) }
        // Un vano con forma (N-96): cada tramo se identifica entero y se parte en rectángulos.
        val conForma = d.contornoVano.size >= 3
        // ¿Todos los módulos de sistema de la ventana miden lo mismo (como en el diseño automático)?
        // Decide cómo se recupera la décima que pierde el paquete al truncar (ver tramoDelMotor).
        val anchosSistema = d.tramos.flatMap { t -> t.sistema?.modulos.orEmpty().mapNotNull { it.ancho } }
        val modulosIguales = anchosSistema.isEmpty() ||
            (anchosSistema.maxOrNull()!! - anchosSistema.minOrNull()!!) <= 0.1f

        // Una cara de la esquina con su silueta (`L<…>`, una cara asimétrica o armada sobre figuras):
        // se calcula como una ventana con forma, con las reglas de los vanos con forma (N-96, N-99).
        // Hallado el 2026-09-29 (Formas 13): la cara de 120 abajo y 189 arriba se calculaba como su
        // rectángulo de 189.
        val ladosDelDiseno = if (grupos.size > 1) d.separarEnLados() else emptyList()
        val ladoConForma: List<LadoNova?> = grupos.mapIndexed { i, indices ->
            val silueta = d.tramos[indices.first()].contorno.takeIf { it.size >= 3 } ?: return@mapIndexed null
            val lado = ladosDelDiseno.getOrNull(i) ?: return@mapIndexed null
            // La silueta es la de la pared; en aparente los tramos ya perdieron el esquinero por el
            // lado de la esquina, así que la pared empieza antes que el primer tramo.
            val sobra = (silueta.maxOf { it.first } - silueta.minOf { it.first } - lado.ancho).coerceAtLeast(0f)
            val corrimiento = when {
                i == 0 -> 0f
                i == grupos.lastIndex -> sobra
                else -> sobra / 2f
            }
            val conSuVano = lado.conContornoVano(silueta.map { (x, y) -> x - corrimiento to y })
            ventana(conSuVano.aPaquete(), u, puente, cruce, modulacion = modulacion)?.lados?.singleOrNull()
        }
        val leidosPorLado = grupos.mapIndexed { i, indices ->
            ladoConForma[i]?.tramos ?: indices.flatMap { tramoDelMotor(d, it, puente, conForma, modulosIguales) }
        }
        fun anchoDe(tramos: List<Tramo>) = tramos.sumOf { it.ancho.toDouble() }.toFloat() + puente.anchoParante * (tramos.size - 1)
        // Inaparente en L, C y serie: el paquete trae cada lado entero (el vidrio no descuenta nada),
        // pero su estructura llega hasta el parante de esquina (N-75), con el mismo reparto que en
        // aparente (N-38). Antes el puente y el riel del lado salían del lado entero.
        val esquinaIna = if (acabado == Acabado.INA && grupos.size > 1)
            VentanaAutomatica.descuentoEsquinas(leidosPorLado.map { anchoDe(it) }, esquinero) else null
        val lados = grupos.mapIndexed { i, indices ->
            val tramosLado = indices.map { d.tramos[it] }
            val tramosLeidos = leidosPorLado[i]
            val (pierdeIzq, pierdeDer) = esquinaIna?.get(i) ?: (0f to 0f)
            // El paquete guarda los anchos truncados a un decimal (143 / 3 → f<47.6>, que suman
            // 142.8). En un lado solo, el ancho de la cabecera es el verdadero: se reescalan los
            // módulos para que sumen el ancho útil, como los reparte el editor.
            val anchoLado = if (grupos.size == 1) d.ancho else anchoDe(tramosLeidos)
            val tramos = when {
                grupos.size == 1 -> reescalar(tramosLeidos, d.ancho - puente.anchoParante * (tramosLeidos.size - 1))
                pierdeIzq + pierdeDer > 0f -> reescalar(tramosLeidos, anchoLado - pierdeIzq - pierdeDer - puente.anchoParante * (tramosLeidos.size - 1))
                    // Lo que se midió sobre la pared (el puente y el riel superior de una cara con
                    // forma) también llega solo hasta el parante de esquina (N-75): en la L de Ele la
                    // cara con el dintel inclinado salía con el puente de la pared entera, 210.9.
                    .mapIndexed { k, t ->
                        val pierde = (if (k == 0) pierdeIzq else 0f) + (if (k == tramosLeidos.lastIndex) pierdeDer else 0f)
                        if (pierde <= 0f) t else t.copy(
                            largosPuente = t.largosPuente?.map { (it - pierde).coerceAtLeast(0f) },
                            anchoRielSuperior = t.anchoRielSuperior?.let { (it - pierde).coerceAtLeast(0f) }
                        )
                    }
                else -> tramosLeidos
            }
            val sistema = tramosLado.first().sistema
            val altoHoja = sistema?.alto?.takeIf { it > 0f && it < d.alto } ?: d.alto
            LadoNova(
                anchoMedido = anchoLado, anchoCalculo = anchoLado,
                alto = d.alto, altoMedido = d.alto, altoHoja = altoHoja,
                tramos = tramos,
                izquierda = if (i == 0) izquierda else Borde.ESQUINA_ANGULO,
                derecha = if (i == grupos.lastIndex) derecha else Borde.ESQUINA_ANGULO,
                modulacion = modulacion ?: modulacionDe(tramos),
                // Cada parante entre tramos mide el alto del vano donde queda (N-94).
                largosParante = if (conForma && grupos.size == 1)
                    (0 until d.tramos.size - 1).map { HerramientasDiseno.largoDelParante(d, it, puente.anchoParante) }
                        .takeIf { l -> l.all { it != null } }?.map { it!! }
                else ladoConForma[i]?.largosParante,
                estructuraIzq = pierdeIzq, estructuraDer = pierdeDer
            )
        }
        // Circular (N-01): el paquete lleva la marca O<1>.
        return VentanaNova(acabado, modelo, lados, u, puente, cruce, esquinero = esquinero, circular = paquete.contains("O<1>"))
    }

    /** El preestablecido de todo lo que no es aparente normal (N-03), como lo entiende el motor. */
    private val TUBO = Puente("tubo 2⅜ x 1", 2.5f, 6f)

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
    private fun tramoDelMotor(d: DisenoNova, indice: Int, puente: Puente, conForma: Boolean, modulosIguales: Boolean): List<Tramo> {
        val t = d.tramos[indice]
        // Primero qué es el tramo entero y qué puente admite (N-03 por tramo, dictado el
        // 2026-09-26): un invertido o una bandera no admiten Múltiple y llevan tubo.
        val elegido = HerramientasDiseno.PuenteTramo(puente.nombre, puente.altura)
        val puenteTramo = HerramientasDiseno.puenteDeTramo(d, indice, elegido)
            ?.takeIf { it.nombre != puente.nombre }?.let { TUBO.takeIf { t2 -> t2.nombre == it.nombre } ?: Puente(it.nombre, it.alto, it.alto) }
        // En un vano con forma: las alturas reales de las franjas (las que se ven) y el rectángulo
        // de cada una (N-96); sin forma, las dibujadas, que valen como proporción.
        val alturas = if (conForma) HerramientasDiseno.alturasDeFranjas(d, indice) else null
        val anchosRect = if (conForma) t.franjas.indices.map { k ->
            HerramientasDiseno.rectanguloDeFranja(d, indice, k, puente.anchoParante)?.first?.takeIf { abs(it - t.ancho) > 0.05f }
        } else null
        val largosPuente = if (conForma && t.franjas.size >= 2) (0 until t.franjas.size - 1).map { k ->
            HerramientasDiseno.largoDelPuente(d, indice, k, (puenteTramo ?: puente).altura, puente.anchoParante)
        }.takeIf { l -> l.all { it != null } }?.map { it!! } else null
        val s = t.sistema ?: return emptyList()
        val sinAncho = s.modulos.count { it.ancho == null }
        val conAncho = s.modulos.sumOf { (it.ancho ?: 0f).toDouble() }.toFloat()
        val resto = if (sinAncho > 0) ((t.ancho - conAncho) / sinAncho).coerceAtLeast(0f) else 0f
        // Los módulos vienen truncados a un decimal (51.05 → f<51>): se ajustan al ancho de SU tramo,
        // que es un solo truncado, antes de ajustar los tramos a la ventana. Si no, la décima perdida
        // se repartía entre todos los tramos y en Formas la U de fijos del tramo 1 daba 51.3 en vez
        // de 51.4. Antes: los módulos tal cual y solo el reescalado del lado.
        val leidos = s.modulos.map { Modulo(!it.esFijo, it.ancho ?: resto) }
        val sumaLeida = leidos.sumOf { it.ancho.toDouble() }.toFloat()
        // Solo si los módulos de la ventana no son todos iguales: con módulos iguales (el diseño
        // automático) lo exacto es repartir la décima entre todos ellos, que es lo que hace después
        // el reescalado del lado; ajustarlos antes a su tramo, también truncado, lo estropeaba (420
        // en 4 + 3: el riel daba 238.4 en vez de 238.5).
        val ajustados = if (!modulosIguales && s.parantes.isEmpty() && sumaLeida > 0f && abs(sumaLeida - t.ancho) in 0.001f..0.5f)
            leidos.map { it.copy(ancho = it.ancho * t.ancho / sumaLeida) } else leidos
        // En un vano con forma, lo que el vano le quita a cada módulo (una viga encima, un escalón
        // debajo): el sistema se parte también por módulos (dictado el 2026-09-26, "polis").
        val recortes = if (conForma) HerramientasDiseno.recortesDelSistema(d, indice, puente.anchoParante) else emptyList()
        // Y una columna que entra por un costado (dictado el 2026-09-26): va en el fijo de ese lado.
        val (colIzq, colDer) = if (conForma) HerramientasDiseno.cortesLateralesDelSistema(d, indice, puente.anchoParante) else null to null
        fun columna(c: HerramientasDiseno.CorteLateral?) = c?.let { Columna(it.caraArriba, it.caraAbajo, it.alto, it.paredArriba, it.paredAbajo) }
        val iSistema = t.franjas.indexOfFirst { it.esSistema }
        val geometria = if (conForma && iSistema >= 0) HerramientasDiseno.geometriaDeModulos(d, indice, iSistema, puente.anchoParante) else emptyList()
        val modulos = ajustados.mapIndexed { k, m ->
            val r = recortes.getOrNull(k)
            val col = when (k) {
                0 -> columna(colIzq)
                ajustados.lastIndex -> columna(colDer)
                else -> null
            }
            // Contra un lado inclinado: su rectángulo contenedor, su borde de abajo y el largo del lado
            // (dictado el 2026-09-26, Vna4). La geometría se mide módulo por módulo.
            val g = geometria.getOrNull(k)?.takeIf { it.inclinado }
            m.copy(
                recorteArriba = r?.first ?: m.recorteArriba, recorteAbajo = r?.second ?: m.recorteAbajo,
                columna = col,
                inclinado = g?.let {
                    // Lo que le falta, medido con el ancho del DISEÑO, que es con el que se midió.
                    val w = s.modulos.getOrNull(k)?.ancho ?: m.ancho
                    Inclinado((w - it.contenido).coerceAtLeast(0f), (w - it.abajo).coerceAtLeast(0f), it.diagonalIzq, it.diagonalDer)
                }
            )
        }
        val anchoArriba = if (conForma) HerramientasDiseno.anchoArribaDelSistema(d, indice, puente.anchoParante) else null
        // Paños de mocheta dibujados: los de la primera mocheta del tramo.
        val panos = t.mochetas.firstOrNull()?.modulos?.map { it.ancho ?: 0f }?.takeIf { p -> p.all { it > 0f } }
        // Las franjas del tramo, de abajo arriba, con su alto dibujado (N-33, N-89). Una mocheta con
        // una corrediza pasa a ser franja de sistema (N-88), con sus módulos.
        val franjas = t.franjas.mapIndexed { k, f ->
            val mods = f.modulos.map { Modulo(!it.esFijo, it.ancho ?: (t.ancho / f.modulos.size)) }
            val alto = alturas?.getOrNull(k) ?: f.alto
            // Cuánto se alargan sus costados si son inclinados, por cm de alto (su U en diagonal).
            val geo = if (conForma) HerramientasDiseno.geometriaDeModulos(d, indice, k, puente.anchoParante) else emptyList()
            val incIzq = geo.firstOrNull()?.diagonalIzq?.takeIf { it > 0f && alto > 0f }?.let { it / alto } ?: 0f
            val incDer = geo.lastOrNull()?.diagonalDer?.takeIf { it > 0f && alto > 0f }?.let { it / alto } ?: 0f
            // La mocheta de arriba de un tramo con el dintel inclinado: cuánto baja su borde de arriba
            // en cada costado respecto de su lado más alto, que es con el que se mide [alto].
            val dintelInclinado = conForma && k == t.franjas.lastIndex && !f.esSistema && abs(t.caidaDerecha - t.caida) > 0.05f
            // Y la mocheta de abajo de un tramo con el alféizar inclinado (invertido, el sistema
            // cuelga del dintel recto, N-66): cuánto sube su borde de abajo en cada costado respecto de
            // su punto más bajo (2026-10-01, el vano de Fide al revés). El motor lo trata igual.
            fun piso(propio: Float, arriba: Float) = if (propio > 0f) arriba + propio else d.alto
            val pisoIzq = piso(t.alto, t.caida)
            val pisoDer = piso(t.altoDerecho, t.caidaDerecha)
            val alfeizarInclinado = conForma && k == 0 && t.franjas.size > 1 && !f.esSistema && abs(pisoDer - pisoIzq) > 0.05f
            val (bajaIzq, bajaDer) = when {
                dintelInclinado -> {
                    val arriba = d.caidaDeTramo(indice)
                    (t.caida - arriba) to (t.caidaDerecha - arriba)
                }
                alfeizarInclinado -> {
                    val abajo = maxOf(pisoIzq, pisoDer)
                    (abajo - pisoIzq) to (abajo - pisoDer)
                }
                else -> 0f to 0f
            }
            FranjaTramo(f.esSistema || mods.any { it.corrediza }, alto, mods, anchosRect?.getOrNull(k), incIzq, incDer, bajaIzq, bajaDer)
        }
        // En un vano con forma, el alto de ESTE tramo (un escalón puede ser más bajo que la ventana):
        // es el que se reparten sus franjas en el motor.
        val altoTramo = if (conForma) d.altoDeTramo(indice).takeIf { it > 0f && abs(it - d.alto) > 0.05f } else null
        val caidaTramo = if (conForma) d.caidaDeTramo(indice).coerceAtLeast(0f) else 0f
        if (s.parantes.isEmpty()) return listOf(Tramo(modulos, panos, franjas, puenteTramo, largosPuente, anchoArriba, alto = altoTramo, caida = caidaTramo))
        val cortes = s.parantes.map { it + 1 }.filter { it in 1 until modulos.size }.distinct().sorted()
        val out = mutableListOf<Tramo>()
        var desde = 0
        var x = 0f
        for (c in cortes + modulos.size) {
            val trozo = modulos.subList(desde, c)
            val anchoTrozo = trozo.sumOf { it.ancho.toDouble() }.toFloat()
            // Con el dintel inclinado cada trozo baja lo suyo: la pendiente se reparte por donde cae
            // (como el largo de un recto, dictado el 2026-10-01).
            fun bajaEn(f: FranjaTramo, xx: Float) =
                if (t.ancho > 0f) f.bajaIzq + (f.bajaDer - f.bajaIzq) * (xx / t.ancho).coerceIn(0f, 1f) else f.bajaIzq
            val suyas = franjas.map { f ->
                if (f.bajaIzq == 0f && f.bajaDer == 0f) f
                else f.copy(bajaIzq = bajaEn(f, x), bajaDer = bajaEn(f, x + anchoTrozo))
            }
            out.add(Tramo(trozo, franjas = suyas, puente = puenteTramo, alto = altoTramo, caida = caidaTramo))   // sus paños de mocheta se reparten solos (N-62)
            x += anchoTrozo + puente.anchoParante
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
