package crystal.crystal.taller.nova.motor

import crystal.crystal.Diseno.nova.ContornoEnTramos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Revisión del 2026-09-29: las medidas de Nova guardadas en Medidas (proyectos Formas, Ele, Pipa…),
 * por el camino entero —el contorno de la medida, el diseño sugerido, el motor—, como las recibe el
 * vidriero. Cada caso es una medida real del celular de pruebas y cada número sale de las reglas de
 * docs/REGLAS_NOVA.md.
 */
class MedidasDeObraTest {

    private val u38 = PerfilU(1f, 1f)
    private val multiple = Puente("Múltiple", 1.5f, 5f)

    private fun puntos(texto: String) = ContornoEnTramos.desdeTexto(texto)

    private fun desdeContorno(contorno: String, acabado: String = "apa"): MaterialesNova {
        val d = ContornoEnTramos.disenoDesdeContorno(puntos(contorno), acabado, 0f)!!
        return MotorNova.calcular(DesdeDiseno.ventana(d.aPaquete(), u38, multiple)!!)
    }

    private fun lineas(t: String) = t.lines().map { it.trim() }

    /**
     * Formas: tres escalones (103.3 × 77.2, 149.3 × 144.5, 80.7 × 109.4; N-101). Cada mocheta es la
     * de SU tramo (N-31: alto del tramo − hoja − puente). Antes los tramos bajos tomaban el alto de
     * la ventana: el tramo 1 salía con un vidrio de mocheta de 102.8 x 87.3 y U de 85.8.
     *
     * Tramo 1: hoja 77.2 × 5/7 = 55.14, mocheta 22.06, espacio 20.56 → vidrio (Múltiple) 102.8 x 20,
     * U de mocheta 20.56 − 1 = 19.5 (con Múltiple una sola U, dictado el 2026-10-01). Tramo 3: hoja
     * 78.14, espacio 29.76 → 80.2 x 29.2, U 28.7.
     */
    @Test
    fun formas_cada_escalon_con_su_mocheta() {
        val m = desdeContorno("0,144.5;256.4,144.5;256.4,109.5;338.4,108.5;338.4,0;104.6,0.1;104.6,67.3;0,67.3")
        val vidrios = lineas(MaterialesNova.textoVidrios(m.vidrios))
        val u = lineas(MaterialesNova.texto(m.u))
        assertTrue(vidrios.toString(), "102.8 x 20 = 1" in vidrios)
        assertTrue(vidrios.toString(), "148.8 x 39.2 = 1" in vidrios)
        assertTrue(vidrios.toString(), "80.2 x 29.2 = 1" in vidrios)
        assertFalse(vidrios.toString(), vidrios.any { it.startsWith("102.8 x 87") })
        assertTrue(u.toString(), "19.5 = 2" in u)
        assertTrue(u.toString(), "28.7 = 2" in u)
    }

    /**
     * Formas 2: el alféizar sube 46 cm a la derecha (140.6 × 71.9 al lado de 277.4 × 117.9). Hoja del
     * tramo bajo 71.9 × 5/7 = 51.36, espacio 71.9 − 51.36 − 1.5 = 19.04 → vidrio 140.1 x 18.5; antes
     * 140.1 x 64.5.
     */
    @Test
    fun formas_2_el_tramo_del_alfeizar_alto() {
        val m = desdeContorno("0,0;0,117.9;278.7,117.9;278.7,71.9;420.6,71.9;420.6,0")
        val vidrios = lineas(MaterialesNova.textoVidrios(m.vidrios))
        assertTrue(vidrios.toString(), "140.1 x 18.5 = 1" in vidrios)
        assertFalse(vidrios.toString(), "140.1 x 64.5 = 1" in vidrios)
    }

    /**
     * Un costado casi vertical que se desvía menos de 2.5 es descuadre (N-06), no un escalón. Formas
     * 11 (un lado que se va 1 cm en 66 de alto) armaba dos tramos de 1 cm, y el costado de la columna
     * de Ele (se desvía 0.3) otro, con vidrios de 0.2 x 42.4. Formas 11 son tres escalones: 43.7,
     * 214.9 y 177.7, que con sus dos parantes suman el vano de 441.2.
     */
    @Test
    fun un_costado_que_se_desvia_poco_no_arma_un_tramo_de_1_cm() {
        val formas11 = ContornoEnTramos.disenoDesdeContorno(
            puntos("44.9,1;45,38.9;0,37;0,168.9;261.3,168.9;262.3,103;441.2,103;441.2,1;263.3,0"), "apa", 0f)!!
        assertTrue(formas11.tramos.map { it.ancho }.toString(), formas11.tramos.size == 3 && formas11.tramos.all { it.ancho > 40f })
        assertTrue(kotlin.math.abs(formas11.tramos.sumOf { it.ancho.toDouble() } + 2 * 2.5 - 441.2) < 0.1)
        val columna = ContornoEnTramos.disenoDesdeContorno(
            puntos("0,0;229.5,0.6;229.5,223.4;0.3,223.4;0.3,158.3;36.4,158.3;36.4,61.1;0,61.1"), "apa", 0f)!!
        assertTrue(columna.tramos.map { it.ancho }.toString(), columna.tramos.all { it.ancho > 10f })
    }

    /**
     * La columna de Ele (36.1 × 97.2 desde la pared izquierda, de 61.1 a 158.3 del dintel) en el
     * diseño sugerido: un solo tramo `f c c f`, sistema 5/7 (desde 63.8 del dintel). La columna sube
     * más allá del puente, que la cierra; dentro del sistema la U sigue la pared (N-98): su cara de
     * abajo 36.1 + 1 = 37.1, su cara vertical 158.3 − 63.8 = 94.4 y la pared de abajo 65.1 − 2 = 63.1.
     * El puente llega hasta la columna: 229.5 − 36.4 = 193.1.
     */
    @Test
    fun ele_la_u_rodea_la_columna_que_sube_al_puente() {
        val m = desdeContorno("0,0;229.5,0.6;229.5,223.4;0.3,223.4;0.3,158.3;36.4,158.3;36.4,61.1;0,61.1")
        val u = lineas(MaterialesNova.texto(m.u))
        assertTrue(u.toString(), listOf("37.1 = 1", "94.4 = 1", "63.1 = 1").all { it in u })
        assertTrue(lineas(MaterialesNova.texto(m.puente)).toString(), "193.1 = 1" in lineas(MaterialesNova.texto(m.puente)))
    }

    /**
     * Formas 13, la L de 70 + 189 (alto 120), con la altura de puente editada en novaDiseño en los
     * dos lados: 100 en el primero y 80 en el segundo. Cada lado calcula con la suya (portafelpa =
     * hoja − 1.6): 98.4 y 78.4.
     */
    @Test
    fun la_l_editada_calcula_cada_lado_con_su_puente() {
        val p = "{nova,apa,[64,120:Tl<64>(s<100>(f<32>c<32>);m<18.5>(f<64>)) A<90> " +
            "Tl<186.5>(s<80>(f<46.6>c<46.6>c<46.6>f<46.6>);m<37.5>(f<93.2>f<93.2>))]}"
        val m = MotorNova.calcular(DesdeDiseno.ventana(p, u38, multiple, esquinero = tubo238)!!)
        val pf = lineas(MaterialesNova.texto(m.portafelpa))
        assertTrue(pf.toString(), pf.any { it.startsWith("98.4 = ") } && pf.any { it.startsWith("78.4 = ") })
    }

    /**
     * Formas en inaparente: la U de cada borde sigue al vidrio y solo va en una pieza lo que apoya
     * seguido (N-93). Arriba el tramo 1 tiene el dintel 67.3 más abajo: su U es la suya, 103.3 + medio
     * parante = 104.5, y la de los tramos 2 y 3 va aparte, 1.25 + 149.34 + 2.5 + 80.72 = 233.8 (los tramos del vano de 338.4). Antes
     * salía una sola de 338.4. Abajo, el fijo del tramo 2 y el del tramo 3 están a distinta altura
     * (144.5 y 109.4) y ya no se juntan en una U de 92.2.
     */
    @Test
    fun formas_en_inaparente_la_u_se_corta_en_cada_escalon() {
        val m = desdeContorno("0,144.5;256.4,144.5;256.4,109.5;338.4,108.5;338.4,0;104.6,0.1;104.6,67.3;0,67.3", "ina")
        val u = lineas(MaterialesNova.texto(m.u))
        assertTrue(u.toString(), "104.5 = 1" in u && "233.8 = 1" in u)
        assertFalse(u.toString(), u.any { it.startsWith("338.4") || it.startsWith("92.2") })
        // Y el fijo junto a cada pared de escalón lleva su U parante (N-43): la pared de 67.3 sobre
        // el tramo 1 (67.3 − 2 = 65.3) y la de 35.1 bajo el tramo 3 (35.1 − 2 = 33.1).
        assertTrue(u.toString(), "65.3 = 1" in u && "33.1 = 1" in u)
    }

    /** Full fijos en inaparente invertido: el fijo va de piso a techo y lleva su U arriba y abajo (N-47). */
    @Test
    fun full_fijos_inaparente_invertido_lleva_sus_u() {
        val lado = DisenoAutomatico.lado(DisenoAutomatico.Entrada(anchoMedido = 160f, anchoCalculo = 160f, alto = 130f,
            hoja = 0f, acabado = Acabado.INA, puente = tubo238, modulacion = "nff"))
        val m = MotorNova.calcular(VentanaNova(Acabado.INA, ModeloMocheta.INVERTIDO, listOf(lado), u38, tubo238))
        assertTrue(lineas(MaterialesNova.texto(m.u)).toString(), "160 = 2" in lineas(MaterialesNova.texto(m.u)))
    }

    /**
     * La L de Ele en inaparente (80 + 210.9, la segunda cara con el dintel que baja 4.6): la cara con
     * forma mide su puente sobre la pared, pero en inaparente la estructura llega hasta el parante de
     * esquina (N-75), con el reparto de N-38: la cara ancha pierde 2.5 → 208.4, la angosta 6 → 74.
     * Salía 210.9 (2026-09-30).
     */
    @Test
    fun ele_inaparente_la_cara_con_forma_tambien_llega_al_parante_de_esquina() {
        val p = "{nova,ina,[80,166:Tl<80>(Q<14.9>;s<120>(f<40>c<40>);m<43.5>(f<80>)) A<90.1> Tl<210.9>(H<166,161.4>;D<0,4.6>;" +
            "L<0/0|210.9/4.6|210.9/166|0/166>;s<120>(f<52.7>c<52.7>c<52.7>f<52.7>);m<46>(f<105.4>f<105.4>))]}"
        val m = MotorNova.calcular(DesdeDiseno.ventana(p, u38, tubo238, esquinero = tubo238)!!)
        val puente = lineas(MaterialesNova.texto(m.puente))
        assertTrue(puente.toString(), "208.4 = 1" in puente && "74 = 1" in puente)
    }

    /**
     * El trapecio en el diseño sugerido, como Vna4 (N-99): un tramo, el fijo contra el lado inclinado
     * y las corredizas en el rectángulo (N-67). La cara de Formas 13 (120 abajo, 189 arriba, alto
     * 120): el rectángulo de 120 lleva 2 módulos (N-15) y el costado inclinado su fijo, `f c f` de
     * 60, 60 y 69. Antes salía un tramo de triángulo con sistema de alto 0 y una corrediza dentro.
     */
    @Test
    fun el_trapecio_sugerido_pone_la_corrediza_en_el_rectangulo() {
        val cara = ContornoEnTramos.disenoDesdeContorno(puntos("0,0;189,0;120,120;0,120"), "apa", 90f)!!
        assertTrue(cara.tramos.size == 1)
        val mods = cara.tramos[0].sistema!!.modulos
        assertTrue(mods.joinToString("") { it.tipo.toString() }, mods.joinToString("") { it.tipo.toString() } == "fcf")
        assertTrue(mods.map { it.ancho }.toString(), kotlin.math.abs(mods[2].ancho!! - 69f) < 0.1f && kotlin.math.abs(mods[0].ancho!! - 60f) < 0.1f)
        val m = MotorNova.calcular(DesdeDiseno.ventana(cara.aPaquete(), u38, multiple)!!)
        // Ninguna pieza de una hoja que no existe: el puente de 1.5, portafelpas de un palmo.
        assertFalse(lineas(MaterialesNova.texto(m.puente)).toString(), m.puente.any { it.medida < 10f })
        assertFalse(lineas(MaterialesNova.texto(m.portafelpa)).toString(), m.portafelpa.any { it.medida < 60f })
    }

    private val tubo238 = Puente("tubo 2⅜ x 1", 2.5f, 6f)

    /**
     * Ele, L con la esquina resuelta en curva: 150 + curva de 90 (desarrollo) + 120, alto 160, hoja
     * 90, aparente con Múltiple. La curva es otro trozo, con su desarrollo, entre dos parantes (N-84).
     * Antes la curva no entraba: faltaban sus dos hojas, su puente de 90 y sus vidrios, y la L salía
     * con un solo parante. N-104 (2026-10-02): cada pared descuenta entero el parante de su punta de
     * curva (2.5 con el tubo 2⅜) y calcula con 147.5 y 117.5; la curva se queda con sus 90.
     */
    @Test
    fun ele_la_curva_de_la_esquina_entra_en_el_calculo() {
        val m = MotorNova.calcular(eleConCurva(tubo238))
        val puente = lineas(MaterialesNova.texto(m.puente))
        assertTrue(puente.toString(), listOf("147.5 = 1", "90 = 1", "117.5 = 1").all { it in puente })
        assertTrue(lineas(MaterialesNova.texto(m.riel)).toString(),
            listOf("147.4 = 1", "89.9 = 1", "117.4 = 1").all { it in lineas(MaterialesNova.texto(m.riel)) })
        assertTrue(lineas(MaterialesNova.texto(m.parante)).toString(), "160 = 2" in lineas(MaterialesNova.texto(m.parante)))
        // El fijo de la curva: (90 + 0.7) / 2 = 45.35 − 0.5 de pared (el parante) = 44.8 x (90 − 1.2).
        val vidrios = lineas(MaterialesNova.textoVidrios(m.vidrios))
        assertTrue(vidrios.toString(), "44.8 x 88.8 = 1" in vidrios)
        // Las hojas: 3 en la pared de 150, 2 en la curva, 2 en la de 120.
        assertTrue(lineas(MaterialesNova.texto(m.hache)).toString(), m.hache.sumOf { it.cantidad } == 3)
    }

    /** N-104 con paflón 1½ de esquinero: cada pared pierde 3.8 → 146.2 y 116.2; la curva, nada. */
    @Test
    fun ele_curva_con_paflon_descuenta_3_8_por_pared() {
        val m = MotorNova.calcular(eleConCurva(Puente("paflon 1½", 3.8f, 8.25f)))
        val puente = lineas(MaterialesNova.texto(m.puente))
        assertTrue(puente.toString(), listOf("146.2 = 1", "90 = 1", "116.2 = 1").all { it in puente })
    }

    /** Las divisiones de la curva escritas en Nova (2026-10-02): con 3, la curva de 90 lleva 3 hojas. */
    @Test
    fun ele_curva_con_sus_divisiones_escritas() {
        val curva = eleConCurva(tubo238, divisionesCurva = 3).lados.single { it.curvaDeEsquina }
        assertEquals(3, curva.tramos.sumOf { it.modulos.size })
        val automatica = eleConCurva(tubo238).lados.single { it.curvaDeEsquina }
        assertEquals(2, automatica.tramos.sumOf { it.modulos.size })
    }

    private fun eleConCurva(esquinero: Puente, divisionesCurva: Int = 0) = VentanaAutomatica.ventana(VentanaAutomatica.Entrada(
        Acabado.APA, ModeloMocheta.NORMAL, "nn",
        listOf(VentanaAutomatica.LadoMedido(150f, 160f, 90f), VentanaAutomatica.LadoMedido(120f, 160f, 90f)),
        u38, multiple, esquinero = esquinero,
        curvas = listOf(VentanaAutomatica.LadoMedido(90f, 160f, 90f, divisionesCurva))
    ))

    /**
     * La L inaparente de 120 + 40 (N-42, N-52b): el vidrio y sus U no descuentan la esquina (la U
     * superior del lado de 120 es 120 + 1 = 121), pero la estructura sí llega hasta el parante de
     * esquina (N-75): el lado mayor pierde la parte menor, 2.5, y su puente mide 117.5 y su riel
     * 117.4. El de 40 pierde la parte mayor del tubo 2⅜ (6): 34. Antes salían 120 y 40.
     */
    @Test
    fun l_inaparente_la_estructura_llega_al_parante_de_esquina() {
        val v = VentanaAutomatica.ventana(VentanaAutomatica.Entrada(
            Acabado.INA, ModeloMocheta.NORMAL, "nn",
            listOf(VentanaAutomatica.LadoMedido(120f, 100f, 80f, 2), VentanaAutomatica.LadoMedido(40f, 100f, 80f, 1)),
            PerfilU(1f, 1.5f), tubo238, esquinero = tubo238
        ))
        val m = MotorNova.calcular(v)
        val puente = lineas(MaterialesNova.texto(m.puente))
        assertTrue(puente.toString(), "117.5 = 1" in puente && "34 = 1" in puente)
        assertTrue(lineas(MaterialesNova.texto(m.riel)).toString(), "117.4 = 1" in lineas(MaterialesNova.texto(m.riel)))
        val u = lineas(MaterialesNova.texto(m.u))
        assertTrue(u.toString(), "121 = 1" in u && "41 = 2" in u)
    }

    /**
     * La L inaparente en ángulo de 120 + 120, dos hojas por lado (dictado el 2026-10-02):
     * - una corrediza nunca va en la esquina: salía `fc P fc` y va `cf P fc`, el fijo en la esquina;
     * - la hache lleva su parte del ancho de la U, como la U de fijo: (120 + 0.7 + 1) / 2 = 60.8; salía
     *   60.3, sin el ancho de la U.
     */
    @Test
    fun l_inaparente_sin_corrediza_en_la_esquina_y_la_hache_con_el_ancho_de_la_u() {
        val v = VentanaAutomatica.ventana(VentanaAutomatica.Entrada(
            Acabado.INA, ModeloMocheta.NORMAL, "nn",
            listOf(VentanaAutomatica.LadoMedido(120f, 100f, 80f, 2), VentanaAutomatica.LadoMedido(120f, 100f, 80f, 2)),
            PerfilU(1f, 1.5f), tubo238, esquinero = tubo238
        ))
        val orden = v.lados.joinToString(" P ") { l -> l.modulos.joinToString("") { if (it.corrediza) "c" else "f" } }
        assertEquals("cf P fc", orden)
        val m = MotorNova.calcular(v)
        assertEquals("60.8 = 2", MaterialesNova.texto(m.hache))
        assertTrue(lineas(MaterialesNova.texto(m.u)).toString(), "60.8 = 2" in lineas(MaterialesNova.texto(m.u)))
        // Los vidrios salen de la U de fijo (60.85), no del módulo sin el ancho de la U. En la esquina se
        // encuentran los dos fijos: el del lado más ancho (aquí iguales, el primero) descuenta 0.5 y el
        // otro 0.2, el de vidrio con vidrio: 60.3 y 60.6. Las corredizas, 60.35 − 1.4 + 0.5 = 59.4.
        val vidrios = lineas(MaterialesNova.textoVidrios(m.vidrios))
        assertTrue(vidrios.toString(), "60.3 x 98.3 = 1" in vidrios && "60.6 x 98.3 = 1" in vidrios)
        assertTrue(vidrios.toString(), "59.4 x 76.5 = 2" in vidrios)
        // La mocheta sobre la corrediza también lleva su parte: 120 − 60.35 + 0.5 − 0.6 = 59.5 (salía 59).
        assertTrue(vidrios.toString(), "59.5 x 21 = 2" in vidrios)
    }

    /**
     * En inaparente el fijo va de piso a techo de SU tramo: en Formas el tramo 1 mide 77.2, así que
     * su vidrio fijo es 77.2 − 1.2 = 76 de alto y su U parante 77.2 − 2 = 75.2, no los 143.3 / 142.5
     * del vano entero.
     */
    @Test
    fun formas_en_inaparente_el_fijo_del_escalon_bajo() {
        val m = desdeContorno("0,144.5;256.4,144.5;256.4,109.5;338.4,108.5;338.4,0;104.6,0.1;104.6,67.3;0,67.3", "ina")
        val vidrios = lineas(MaterialesNova.textoVidrios(m.vidrios))
        val u = lineas(MaterialesNova.texto(m.u))
        assertTrue(vidrios.toString(), vidrios.any { it.endsWith(" x 76 = 1") })
        assertTrue(u.toString(), "75.2 = 1" in u)
    }
}
