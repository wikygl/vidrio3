package crystal.crystal.taller.nova.motor

/**
 * La descripción única de una Nova corrediza, de la que sale todo el cálculo (docs/AUDITORIA_NOVA.md,
 * sección 3). La llenan dos caminos —el diseño automático (Nova con ceil) y el diseño hecho a mano
 * (Nova desigual)— y desde aquí las dos se calculan igual, en [MotorNova].
 *
 * No depende de la pantalla: se puede construir y probar sin Android.
 */

enum class Acabado { APA, INA }

/** Dónde van las mochetas (N-33). En el código viejo, `modeloRemate`: nn, nr, np. */
enum class ModeloMocheta { NORMAL, INVERTIDO, BANDERA }

/**
 * Con qué termina un lado de la ventana. MURO, ACOPLE y LIBRE vienen de Medidas (N-78); ESQUINA
 * es la unión con el lado siguiente en una L, una C o una serie.
 */
enum class Borde { MURO, ACOPLE, LIBRE, ESQUINA_ANGULO, ESQUINA_CURVA;
    val esEsquina: Boolean get() = this == ESQUINA_ANGULO || this == ESQUINA_CURVA
}

/** Perfil U (G-02): el alto es lo que se descuenta; el ancho es el aumento de esquina en INA. */
data class PerfilU(val ancho: Float, val alto: Float)

/** Puente elegido (tabla de la sección 8.1 de las reglas). */
data class Puente(val nombre: String, val altura: Float, val esquinero: Float) {
    val esMultipleOGorrito: Boolean
        get() = nombre.lowercase().let {
            it.contains("multi") || it.contains("múlt") || it.contains("ltiple") || it.contains("gorrito")
        }

    val esMultiple: Boolean
        get() = nombre.lowercase().let { it.contains("multi") || it.contains("múlt") || it.contains("ltiple") }

    /** Parante entre tramos (N-27): 2.5 por defecto; depende del puente elegido. */
    val anchoParante: Float get() = if (esMultipleOGorrito) 2.5f else altura
}

/** Un módulo del sistema. [ancho] es el ancho del diseño, ya neto de parantes. */
data class Modulo(
    val corrediza: Boolean,
    val ancho: Float,
    /**
     * Cuánto le quita el vano a este módulo por arriba o por abajo, en un vano con forma (N-96,
     * dictado el 2026-09-26): en "polis" el techo baja 39.2 sobre el fijo de la izquierda, que
     * mide 94.4 y no 133.6. Menos de un descuadre (2.5) no cuenta.
     */
    val recorteArriba: Float = 0f,
    val recorteAbajo: Float = 0f,
    /**
     * Una columna que entra por el costado del módulo, del lado de la pared (dictado el 2026-09-26,
     * "polis" ventana 2): su U parante sigue la pared alrededor de ella. Null = pared recta.
     */
    val columna: Columna? = null,
    /**
     * Si el vano lo corta en diagonal (un lado inclinado, dictado el 2026-09-26, Vna4): el ancho del
     * rectángulo que lo contiene (su vidrio entero, se corta en obra), su borde de abajo (su U de
     * fijo, "como 2 o 3 cm") y el largo del lado inclinado por cada costado. Null = rectángulo.
     */
    val inclinado: Inclinado? = null
)

/** Un módulo contra un lado inclinado del vano (ver [Modulo.inclinado]). */
/**
 * [faltaContenido] y [faltaAbajo] son lo que al módulo le falta respecto de su ancho: se guardan así
 * para que valgan igual con el módulo ya ajustado al ancho de la ventana.
 */
data class Inclinado(val faltaContenido: Float, val faltaAbajo: Float, val diagonalIzq: Float, val diagonalDer: Float)

/**
 * Una columna que entra en un fijo por su costado. [caraArriba] y [caraAbajo] son sus caras
 * horizontales (desde la pared), [alto] su cara vertical, [paredArriba] y [paredAbajo] la pared que
 * queda encima y debajo dentro del sistema. En "polis" ventana 2: 36.4, 36.1, 97.2, 17.4 y 21.4.
 */
data class Columna(
    val caraArriba: Float, val caraAbajo: Float, val alto: Float,
    val paredArriba: Float, val paredAbajo: Float
)

/**
 * Una franja de un tramo dibujado a mano, de abajo arriba (N-33). [alto] es el del dibujo: para las
 * mochetas se usa solo como proporción, porque unos paquetes lo guardan con el puente y otros sin
 * él (N-80); el espacio real sale de alto del tramo − hoja.
 */
data class FranjaTramo(
    val sistema: Boolean,
    val alto: Float,
    /** Sus módulos. Hacen falta en una franja de sistema que no es la principal (N-88). */
    val modulos: List<Modulo> = emptyList(),
    /**
     * El ancho de su rectángulo en un vano con forma (N-96), si no es el del tramo. En Formas la
     * mocheta de abajo del tramo 2 mide 151.7 y el tramo 233.7. Null = el ancho del tramo.
     */
    val ancho: Float? = null,
    /**
     * Cuánto se alarga un costado inclinado por cada cm de alto (1 / coseno de la inclinación), a
     * la izquierda y a la derecha; 0 = costado vertical. Con esto la U de ese costado de la mocheta
     * es una diagonal (Vna4: 43.3 de mocheta → 48.1 de lado).
     */
    val inclinacionIzq: Float = 0f,
    val inclinacionDer: Float = 0f
)

/**
 * Un tramo: los módulos entre dos parantes. [panosMocheta] = anchos de los paños de mocheta si
 * vienen del diseño; null = se reparten solos (N-62). [franjas] = sus franjas propias cuando el
 * diseño es compuesto (N-89: cada tramo con su modelo y su altura de hoja); null = las de la ventana.
 */
data class Tramo(
    val modulos: List<Modulo>,
    val panosMocheta: List<Float>? = null,
    val franjas: List<FranjaTramo>? = null,
    /**
     * El puente de este tramo, si no es el de la ventana (N-03 por tramo, C-24): un tramo invertido
     * o en bandera no admite Múltiple y lleva tubo. Null = el de la ventana.
     */
    val puente: Puente? = null,
    /**
     * Lo que mide cada corrida de puente del tramo en un vano con forma, de abajo arriba (N-97): en
     * Formas el puente del tramo 2 entra en la mocheta y mide 151.7, no 233.7. Null = el ancho del tramo.
     */
    val largosPuente: List<Float>? = null,
    /**
     * Lo que mide el borde de arriba del sistema cuando el vano lo corta (dictado el 2026-09-26): el
     * fijo corredizo solo llega hasta donde empieza el corte. En "polis", 190.2 en un tramo de 252.
     * Null = el ancho del tramo.
     */
    val anchoRielSuperior: Float? = null
) {
    val ancho: Float get() = modulos.sumOf { it.ancho.toDouble() }.toFloat()
    val corredizas: Int get() = modulos.count { it.corrediza }
    val fijos: Int get() = modulos.count { !it.corrediza }
}

/**
 * Un lado de la ventana. Una ventana plana tiene uno; una L, dos; una C, tres.
 *
 * [anchoMedido] es el que se midió (va a las referencias y decide cuántas hojas, N-17);
 * [anchoCalculo] es el ancho con los descuentos de aparente ya hechos (lados libres, esquinero).
 * [alto] es el alto de cálculo (APA ya sin el falso puente); [altoMedido], el medido, que es el
 * largo de los parantes verticales.
 */
data class LadoNova(
    val anchoMedido: Float,
    val anchoCalculo: Float,
    val alto: Float,
    val altoMedido: Float,
    val altoHoja: Float,
    val tramos: List<Tramo>,
    val izquierda: Borde,
    val derecha: Borde,
    /** Modulación del lado (nn, nfc, nff, ncfc, ncc, n3c): decide perfiles propios como la doble corrediza. */
    val modulacion: String = "nn",
    /**
     * Lo que mide cada parante entre tramos en un vano con forma (N-94): el alto del vano donde
     * queda. En Formas, con el parante dentro, 77.2. Null = el alto medido del lado.
     */
    val largosParante: List<Float>? = null
) {
    val hayMocheta: Boolean get() = altoHoja < alto
    val modulos: List<Modulo> get() = tramos.flatMap { it.modulos }
    val divisiones: Int get() = modulos.size
}

data class VentanaNova(
    val acabado: Acabado,
    val modelo: ModeloMocheta,
    val lados: List<LadoNova>,
    val u: PerfilU,
    val puente: Puente,
    val cruce: Float = 0.7f,
    /** Bandera: mocheta inferior escrita a mano; 0 = mitad y mitad (N-34). */
    val mochetaInferior: Float = 0f,
    /** Lados arriba/abajo sin pared (N-78): llevan falso puente. */
    val libresVerticales: Int = 0,
    /** Perfil que cierra los lados libres (parante o falso puente). */
    val perfilLibre: Float = 2.5f,
    /** Esquinero de las L y C: su parante vertical; null = "ninguno". */
    val esquinero: Puente? = null,
    /**
     * Forma circular (N-01, sección 11 de las reglas): el vano es el círculo (o la elipse) inscrito
     * en ancho × alto. Los puentes son cuerdas y las U de los bordes, arcos. Ver [Circulo].
     */
    val circular: Boolean = false
)
