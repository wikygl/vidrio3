package crystal.crystal.taller.nova.motor

import crystal.crystal.Diseno.nova.ContornoEnTramos
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
     * U de mocheta 18.5. Tramo 3: hoja 78.14, espacio 29.76 → 80.2 x 29.2, U 27.7.
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
        assertTrue(u.toString(), "18.5 = 2" in u)
        assertTrue(u.toString(), "27.7 = 2" in u)
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

    private val tubo238 = Puente("tubo 2⅜ x 1", 2.5f, 6f)

    /**
     * Ele, L con la esquina resuelta en curva: 150 + curva de 90 (desarrollo) + 120, alto 160, hoja
     * 90, aparente con Múltiple. La curva es otro trozo, con su desarrollo, entre dos parantes (N-84),
     * y en la esquina curva no se descuenta nada (G-09). Antes la curva no entraba: faltaban sus dos
     * hojas, su puente de 90 y sus vidrios, y la L salía con un solo parante.
     */
    @Test
    fun ele_la_curva_de_la_esquina_entra_en_el_calculo() {
        val v = VentanaAutomatica.ventana(VentanaAutomatica.Entrada(
            Acabado.APA, ModeloMocheta.NORMAL, "nn",
            listOf(VentanaAutomatica.LadoMedido(150f, 160f, 90f), VentanaAutomatica.LadoMedido(120f, 160f, 90f)),
            u38, multiple, esquinero = tubo238,
            curvas = listOf(VentanaAutomatica.LadoMedido(90f, 160f, 90f))
        ))
        val m = MotorNova.calcular(v)
        val puente = lineas(MaterialesNova.texto(m.puente))
        assertTrue(puente.toString(), listOf("150 = 1", "90 = 1", "120 = 1").all { it in puente })
        assertTrue(lineas(MaterialesNova.texto(m.parante)).toString(), "160 = 2" in lineas(MaterialesNova.texto(m.parante)))
        // El fijo de la curva: (90 + 0.7) / 2 = 45.35 − 0.5 de pared (el parante) = 44.8 x (90 − 1.2).
        val vidrios = lineas(MaterialesNova.textoVidrios(m.vidrios))
        assertTrue(vidrios.toString(), "44.8 x 88.8 = 1" in vidrios)
        // Las hojas: 3 en la pared de 150, 2 en la curva, 2 en la de 120.
        assertTrue(lineas(MaterialesNova.texto(m.hache)).toString(), m.hache.sumOf { it.cantidad } == 3)
    }

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
