package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.hypot

/**
 * La ventana vista desde arriba, sacada de su diseño.
 *
 * Es el perfil que hay que extruir para verla en tres dimensiones, así que tiene que ser
 * geometría de verdad: aquí se comprueba en centímetros, sin pantalla de por medio. Lo que hoy se
 * dibuja finge la profundidad con alas y un ángulo fijo de 90; esto sabe de verdad hacia dónde va
 * cada pared.
 */
class PlantaDelDisenoTest {

    private fun tramo(ancho: Float, pliegue: String? = null, flecha: Float = 0f) = NovaTramo(
        ancho = ancho,
        franjas = listOf(NovaFranja(esSistema = true, alto = 0f, modulos = listOf(NovaModulo('f')))),
        alto = 0f,
        pliegue = pliegue,
        flecha = flecha
    )

    private fun ventana(vararg tramos: NovaTramo) =
        DisenoNova(acabado = "ina", ancho = tramos.first().ancho, alto = 160f, tramos = tramos.toList())

    /** Una ventana plana es una pared y ya: recta, de punta a punta. */
    @Test
    fun una_ventana_plana_es_una_sola_pared() {
        val planta = PlantaDelDiseno.de(ventana(tramo(240f)))
        assertEquals(1, planta.paredes.size)
        val pared = planta.paredes[0]
        assertEquals(0f, pared.desde.x, 0.01f)
        assertEquals(0f, pared.desde.y, 0.01f)
        assertEquals("no avanzó lo que mide", 240f, pared.hasta.x, 0.01f)
        assertEquals("una pared recta no se hunde", 0f, pared.hasta.y, 0.01f)
        assertEquals(240f, planta.desarrolloCm, 0.01f)
    }

    /** En L: la segunda pared dobla en escuadra y se va hacia dentro. */
    @Test
    fun en_l_la_segunda_pared_dobla_en_escuadra() {
        val planta = PlantaDelDiseno.de(ventana(tramo(150f), tramo(120f, pliegue = "A<90>")))
        assertEquals(2, planta.paredes.size)
        val segunda = planta.paredes[1]
        assertEquals("la esquina no está donde acaba la primera", 150f, segunda.desde.x, 0.01f)
        assertEquals(0f, segunda.desde.y, 0.01f)
        // Doblando 90° desde la horizontal, la segunda pared se hunde: misma x, y = su ancho.
        assertEquals("la segunda pared no dobló", 150f, segunda.hasta.x, 0.5f)
        assertEquals("la segunda pared no se hundió", 120f, segunda.hasta.y, 0.5f)
        assertEquals(90f, segunda.giroGrados, 0.01f)
    }

    /**
     * Un ángulo abierto dobla menos y uno cerrado más, y se nota en sitios distintos.
     *
     * Lo que se hunde la pared (su y) crece hasta la escuadra y a partir de ahí vuelve a bajar: a
     * 90° la pared está de canto y es lo más hondo que puede ir. Lo que distingue un ángulo
     * CERRADO es que la pared se va hacia atrás, con la x retrocediendo.
     */
    @Test
    fun cada_angulo_dobla_lo_suyo() {
        fun pared(grados: String): ParedEnPlanta =
            PlantaDelDiseno.de(ventana(tramo(150f), tramo(100f, pliegue = "A<$grados>")))
                .paredes[1]

        val abierto = pared("135")
        val escuadra = pared("90")
        val cerrado = pared("54")
        assertEquals("135° tiene que hundirse lo que va a 45°", 70.7f, abierto.hasta.y, 0.5f)
        assertEquals("90° se hunde su ancho entero", 100f, escuadra.hasta.y, 0.5f)
        assertTrue("un ángulo abierto no puede hundirse como la escuadra", abierto.hasta.y < escuadra.hasta.y)

        // La escuadra deja la pared justo debajo de su esquina; la abierta sigue avanzando y la
        // cerrada retrocede. Ahí es donde se ven los tres distintos.
        assertTrue("135° tiene que seguir avanzando", abierto.hasta.x > abierto.desde.x)
        assertEquals("90° no avanza ni retrocede", escuadra.desde.x, escuadra.hasta.x, 0.5f)
        assertTrue("54° tiene que volver sobre sus pasos", cerrado.hasta.x < cerrado.desde.x)
    }

    /** Y el signo dice hacia dónde: en menos, la ventana abre hacia afuera. */
    @Test
    fun el_signo_del_pliegue_dice_hacia_donde_dobla() {
        val adentro = PlantaDelDiseno.de(ventana(tramo(150f), tramo(120f, pliegue = "A<90>")))
        val afuera = PlantaDelDiseno.de(ventana(tramo(150f), tramo(120f, pliegue = "A<-90>")))
        assertEquals(120f, adentro.paredes[1].hasta.y, 0.5f)
        assertEquals("hacia afuera tiene que ir al otro lado", -120f, afuera.paredes[1].hasta.y, 0.5f)
    }

    /**
     * La pared curva ocupa en el suelo su CUERDA, no su desarrollo, y gira ella sola.
     *
     * Un cuarto de círculo de radio 100: 157.1 de desarrollo, 29.3 de panza, 141.4 de cuerda y 90°
     * de giro. La ventana tiene que salir de la curva ya doblada en escuadra.
     */
    @Test
    fun la_pared_curva_ocupa_su_cuerda_y_gira_ella_sola() {
        val planta = PlantaDelDiseno.de(
            ventana(tramo(150f), tramo(157.1f, flecha = 29.3f), tramo(120f, pliegue = "A<90>"))
        )
        assertEquals(3, planta.paredes.size)
        val curva = planta.paredes[1]
        assertTrue("no la reconoce como curva", curva.esCurva)
        assertEquals("la curva no ocupa su cuerda", 141.4f, curva.cuerdaCm, 1.5f)
        assertEquals("la curva no gira lo que dice su arco", 90f, curva.giroGrados, 1f)
        assertEquals(
            "en el desarrollo sí cuenta lo que se corta",
            150f + 157.1f + 120f, planta.desarrolloCm, 0.1f
        )

        // Y la pared de después sale perpendicular a la primera: el giro lo hizo el arco, y el
        // pliegue que viene detrás de una curva no cuenta otra vez.
        val ultima = planta.paredes[2]
        val largo = hypot(ultima.hasta.x - ultima.desde.x, ultima.hasta.y - ultima.desde.y)
        assertEquals("la última pared cambió de medida", 120f, largo, 0.5f)
        assertEquals("la última pared no quedó perpendicular", ultima.desde.x, ultima.hasta.x, 1.5f)
        assertTrue("la última pared no se hunde", ultima.hasta.y > ultima.desde.y)
    }

    /** La ventana que empieza en la curva: el primer paño ya es el arco. */
    @Test
    fun la_ventana_puede_empezar_en_la_curva() {
        val planta = PlantaDelDiseno.de(
            ventana(tramo(80f, flecha = 10.8f), tramo(220f, pliegue = "A<90>"))
        )
        assertEquals(2, planta.paredes.size)
        assertTrue("el primer paño no es la curva", planta.paredes[0].esCurva)
        assertEquals("la curva no arranca en el origen", 0f, planta.paredes[0].desde.x, 0.01f)
        assertEquals(80f + 220f, planta.desarrolloCm, 0.1f)
    }

    /** Y una C se cierra como una C: las dos paredes de los lados miran hacia dentro. */
    @Test
    fun una_c_mira_hacia_dentro_por_los_dos_lados() {
        val planta = PlantaDelDiseno.de(
            ventana(tramo(100f), tramo(200f, pliegue = "A<90>"), tramo(100f, pliegue = "A<90>"))
        )
        val recorrido = planta.recorrido()
        assertEquals(4, recorrido.size)
        // Con dos escuadras seguidas: la primera pared va a lo ancho, la del medio se hunde y la
        // tercera vuelve en paralelo a la primera, retrocediendo.
        assertEquals("la primera pared no va a lo ancho", recorrido[0].y, recorrido[1].y, 0.5f)
        assertEquals("la del medio no se hunde recta", recorrido[1].x, recorrido[2].x, 0.5f)
        assertEquals("la tercera no vuelve en paralelo", recorrido[2].y, recorrido[3].y, 0.5f)
        assertTrue("la C no se cierra: la tercera pared sigue de largo", recorrido[3].x < recorrido[2].x)
        assertEquals("la C no se hunde lo que mide su pared del medio", 200f, recorrido[2].y, 0.5f)
    }

    /**
     * La ventana curva de la calculadora: UNA panza para toda la ventana, no una por tramo.
     *
     * El curvo de Nova escribe el tag `U<flecha>` para el conjunto y parte la ventana en tramos
     * que son trozos del mismo arco. Sin entenderlo, sus tramos salían rectos y la ventana se veía
     * plana en tres dimensiones aunque el cálculo estuviera bien.
     */
    @Test
    fun la_ventana_curva_entera_curva_todos_sus_tramos() {
        val d = DisenoNova(
            acabado = "ina",
            ancho = 180f,
            alto = 160f,
            tramos = listOf(tramo(60f), tramo(60f), tramo(60f)),
            etiquetas = listOf("U<20>")
        )
        val planta = PlantaDelDiseno.de(d)
        assertEquals(3, planta.paredes.size)
        assertTrue("los tramos salieron rectos", planta.paredes.all { it.esCurva })
        assertEquals("el desarrollo cambió", 180f, planta.desarrolloCm, 0.1f)

        // Los tres son trozos del MISMO arco: giran lo mismo y ocupan la misma cuerda.
        val giros = planta.paredes.map { it.giroGrados }
        assertEquals("los trozos no giran lo mismo", giros[0], giros[1], 0.5f)
        assertEquals("los trozos no giran lo mismo", giros[1], giros[2], 0.5f)
        assertTrue("no giran nada: $giros", giros[0] > 5f)

        // Y la ventana entera gira lo que le toca a su arco: 180 de desarrollo con 20 de panza.
        val giroEntero = giros.sum()
        val arcoEntero = crystal.crystal.taller.ArcoEsquina.deDesarrolloYFlecha(180f, 20f)!!
        assertEquals("la ventana no gira lo que dice su arco", arcoEntero.anguloGrados, giroEntero, 1f)
    }

    /** Y si los tramos traen la suya, manda la suya: ahí cada arco es distinto. */
    @Test
    fun la_panza_de_cada_tramo_manda_sobre_la_del_conjunto() {
        val d = DisenoNova(
            acabado = "ina",
            ancho = 180f,
            alto = 160f,
            tramos = listOf(tramo(90f, flecha = 8f), tramo(90f, flecha = 20f)),
            etiquetas = listOf("U<50>")
        )
        val planta = PlantaDelDiseno.de(d)
        assertEquals(8f, planta.paredes[0].flechaCm, 0.01f)
        assertEquals(20f, planta.paredes[1].flechaCm, 0.01f)
        assertTrue(
            "los dos arcos giran lo mismo, pero tienen panzas distintas",
            planta.paredes[1].giroGrados > planta.paredes[0].giroGrados + 5f
        )
    }
}
