package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot

/**
 * La ventana levantada del suelo, y puesta en el papel en isométrico.
 *
 * Todo en centímetros y comprobado en frío: si la geometría está bien aquí, dibujarla es pintar
 * cuadriláteros. Es lo contrario de lo que hay hoy, donde la profundidad se finge en el propio
 * dibujo y no hay manera de comprobar si un ángulo salió como debía.
 */
class VolumenDelDisenoTest {

    private fun tramo(ancho: Float, pliegue: String? = null, flecha: Float = 0f, alto: Float = 0f) =
        NovaTramo(
            ancho = ancho,
            franjas = listOf(NovaFranja(esSistema = true, alto = 0f, modulos = listOf(NovaModulo('f')))),
            alto = alto,
            pliegue = pliegue,
            flecha = flecha
        )

    private fun volumen(vararg tramos: NovaTramo, altoVentana: Float = 160f): VolumenDelDiseno {
        val diseno = DisenoNova("ina", tramos.first().ancho, altoVentana, tramos.toList())
        return VolumenDelDiseno.de(PlantaDelDiseno.de(diseno))
    }

    /** Una pared recta es una cara: su ancho por su alto, plantada en el suelo. */
    @Test
    fun una_pared_recta_es_una_cara() {
        val v = volumen(tramo(240f))
        assertEquals(1, v.caras.size)
        val cara = v.caras[0]
        assertEquals(0f, cara.abajoIzq.x, 0.01f)
        assertEquals(0f, cara.abajoIzq.z, 0.01f)
        assertEquals("no mide lo que mide la pared", 240f, cara.abajoDer.x, 0.01f)
        assertEquals("no se levantó el alto de la ventana", 160f, cara.arribaIzq.z, 0.01f)
        assertEquals("las dos esquinas de arriba no están a la misma altura",
            cara.arribaIzq.z, cara.arribaDer.z, 0.01f)
        assertEquals("la cara ocupa el tramo entero", 0f, cara.desdeU, 0.01f)
        assertEquals(1f, cara.hastaU, 0.01f)
    }

    /** Cada pared sube SU alto: una más baja no encoge a la de al lado. */
    @Test
    fun cada_pared_sube_su_alto() {
        val v = volumen(tramo(150f), tramo(120f, pliegue = "A<90>", alto = 100f))
        assertEquals(160f, v.caras[0].arribaIzq.z, 0.01f)
        assertEquals("la pared baja no se quedó con su alto", 100f, v.caras[1].arribaIzq.z, 0.01f)
    }

    /** En L, la segunda cara está en otro plano: con el rincón (A<90>) viene hacia quien mira (+y). */
    @Test
    fun en_l_la_segunda_cara_viene_hacia_quien_mira() {
        val v = volumen(tramo(150f), tramo(120f, pliegue = "A<90>"))
        assertEquals(2, v.caras.size)
        val segunda = v.caras[1]
        assertEquals("no arranca donde acaba la primera", 150f, segunda.abajoIzq.x, 0.5f)
        assertEquals("la primera pared no está a ras", 0f, v.caras[0].abajoDer.y, 0.01f)
        assertEquals("la segunda no vino hacia quien mira", 120f, segunda.abajoDer.y, 0.5f)
        assertEquals("la segunda no quedó perpendicular", segunda.abajoIzq.x, segunda.abajoDer.x, 0.5f)
    }

    /**
     * Una pared curva se factea: sale en varios trozos que siguen su arco.
     *
     * Los trozos sumados tienen que dar casi el desarrollo —un poco menos, que es lo que pierde
     * cualquier facetado al cortar por cuerdas— y todos tienen que quedar a la misma altura.
     */
    @Test
    fun una_pared_curva_sale_en_trozos_que_siguen_su_arco() {
        val v = volumen(tramo(150f), tramo(157.1f, flecha = 29.3f), tramo(120f, pliegue = "A<90>"))
        val deLaCurva = v.caras.filter { it.esCurva }
        assertEquals("no se facteó la curva", VolumenDelDiseno.TROZOS_DE_CURVA, deLaCurva.size)
        assertTrue("los trozos no son del mismo tramo", deLaCurva.all { it.indiceTramo == 1 })

        val sumaDeTrozos = deLaCurva.sumOf {
            hypot(it.abajoDer.x - it.abajoIzq.x, it.abajoDer.y - it.abajoIzq.y).toDouble()
        }.toFloat()
        assertTrue(
            "los trozos no siguen el desarrollo de la curva: $sumaDeTrozos",
            sumaDeTrozos > 157.1f * 0.99f && sumaDeTrozos <= 157.1f
        )
        assertTrue("algún trozo se quedó a otra altura", deLaCurva.all { abs(it.arribaIzq.z - 160f) < 0.01f })

        // Y van pegados: donde acaba uno empieza el siguiente.
        deLaCurva.zipWithNext { a, b ->
            assertEquals("la curva salió rota", a.abajoDer.x, b.abajoIzq.x, 0.01f)
            assertEquals("la curva salió rota", a.abajoDer.y, b.abajoIzq.y, 0.01f)
        }
        // El último trozo acaba donde la planta dijo que acababa la pared.
        val finCurva = deLaCurva.last().abajoDer
        val empiezaLaUltima = v.caras.last().abajoIzq
        assertEquals("la curva no entrega la pared siguiente", finCurva.x, empiezaLaUltima.x, 0.5f)
        assertEquals("la curva no entrega la pared siguiente", finCurva.y, empiezaLaUltima.y, 0.5f)
    }

    /** Y cada trozo sabe qué parte del ancho ocupa: es lo que llevará los módulos encima. */
    @Test
    fun cada_trozo_sabe_que_parte_del_tramo_ocupa() {
        val v = volumen(tramo(150f), tramo(157.1f, flecha = 29.3f), tramo(120f, pliegue = "A<90>"))
        val deLaCurva = v.caras.filter { it.esCurva }
        assertEquals(0f, deLaCurva.first().desdeU, 0.01f)
        assertEquals(1f, deLaCurva.last().hastaU, 0.01f)
        deLaCurva.zipWithNext { a, b ->
            assertEquals("los trozos no se reparten el ancho seguido", a.hastaU, b.desdeU, 0.01f)
        }
    }

    // ---------------- del espacio al papel ----------------

    /** El origen se queda en el origen, y lo que sube en el espacio sube en el papel. */
    @Test
    fun lo_que_sube_en_el_espacio_sube_en_el_papel() {
        val suelo = VolumenDelDiseno.proyectar(Punto3D(0f, 0f, 0f))
        assertEquals(0f, suelo.x, 0.01f)
        assertEquals(0f, suelo.y, 0.01f)

        val alto = VolumenDelDiseno.proyectar(Punto3D(0f, 0f, 160f))
        assertEquals("subir no puede moverlo de lado", 0f, alto.x, 0.01f)
        assertEquals("el papel crece hacia abajo: lo alto va en negativo", -160f, alto.y, 0.01f)
    }

    /** Sin punto de fuga: dos paredes iguales se dibujan iguales, esté una más lejos que la otra. */
    @Test
    fun en_isometrico_lo_lejano_no_encoge() {
        fun largo(desde: Punto3D, hasta: Punto3D): Float {
            val a = VolumenDelDiseno.proyectar(desde)
            val b = VolumenDelDiseno.proyectar(hasta)
            return hypot(b.x - a.x, b.y - a.y)
        }
        val cerca = largo(Punto3D(0f, 0f, 0f), Punto3D(100f, 0f, 0f))
        val lejos = largo(Punto3D(0f, 500f, 0f), Punto3D(100f, 500f, 0f))
        assertEquals("lo de lejos encogió: eso es perspectiva, no isométrico", cerca, lejos, 0.01f)
    }

    /**
     * Los ángulos se distinguen por construcción, sin ningún factor que acertar.
     *
     * Lo que los distingue en el papel es hacia DÓNDE apunta cada pared, no cuánto mide: en
     * isométrico dos paredes que apuntan a sitios distintos pueden proyectar el mismo largo —a
     * 90° y a 54° salen casi iguales de largas— pero nunca con la misma inclinación. Y eso es
     * justo lo que no se podía conseguir falseando la profundidad en el dibujo, donde un ángulo y
     * otro acababan con el mismo quiebre.
     */
    @Test
    fun cada_angulo_se_ve_distinto_sin_tener_que_acertar_ningun_factor() {
        fun inclinacionEnElPapel(grados: String): Float {
            val v = volumen(tramo(150f), tramo(100f, pliegue = "A<$grados>"))
            val cara = v.caras[1]
            val a = VolumenDelDiseno.proyectar(cara.abajoIzq)
            val b = VolumenDelDiseno.proyectar(cara.abajoDer)
            return Math.toDegrees(atan2((b.y - a.y).toDouble(), (b.x - a.x).toDouble())).toFloat()
        }
        val abierto = inclinacionEnElPapel("135")
        val escuadra = inclinacionEnElPapel("90")
        val cerrado = inclinacionEnElPapel("54")
        assertTrue("135° y 90° se dibujan igual: $abierto vs $escuadra", abs(abierto - escuadra) > 10f)
        assertTrue("90° y 54° se dibujan igual: $escuadra vs $cerrado", abs(escuadra - cerrado) > 10f)
        assertTrue("135° y 54° se dibujan igual: $abierto vs $cerrado", abs(abierto - cerrado) > 10f)
    }

    /** Y se pinta de atrás hacia delante, que es lo que hace que se tapen bien. */
    @Test
    fun se_pinta_de_lejos_a_cerca() {
        val v = volumen(tramo(150f), tramo(120f, pliegue = "A<90>"))
        val orden = v.deLejosACerca()
        assertEquals(v.caras.size, orden.size)
        val profundidades = orden.map { cara ->
            cara.esquinas.map { VolumenDelDiseno.profundidad(it) }.average()
        }
        assertEquals(
            "no están ordenadas de lejos a cerca: $profundidades",
            profundidades.sortedDescending(), profundidades
        )
    }

    /**
     * La mirada se puede subir y bajar, y la perspectiva de verdad achica lo que se aleja del ojo.
     * Con los valores por defecto sigue siendo la isométrica de siempre (lo prueban las de arriba).
     */
    @Test
    fun la_elevacion_y_la_perspectiva() {
        // Mirando desde arriba del todo (90°) la altura no se ve: es la planta.
        val suelo = VolumenDelDiseno.proyectar(Punto3D(0f, 0f, 0f), 0f, 90f)
        val alto = VolumenDelDiseno.proyectar(Punto3D(0f, 0f, 160f), 0f, 90f)
        assertEquals(suelo.y, alto.y, 0.5f)
        // De frente (0°) el suelo no ocupa nada en vertical: dos puntos del suelo a la misma altura.
        val cerca = VolumenDelDiseno.proyectar(Punto3D(0f, 0f, 0f), 0f, 0f)
        val lejos = VolumenDelDiseno.proyectar(Punto3D(100f, 100f, 0f), 0f, 0f)
        assertEquals(cerca.y, lejos.y, 0.01f)
        // En perspectiva, lo que está detrás del centro se acerca al centro: se ve más chico. Se
        // mira desde arriba, así que atrás es hacia -(x + y): lo que en el papel queda más arriba.
        val centro = Punto3D(0f, 0f, 0f)
        val atras = Punto3D(-200f, -200f, 0f)
        val paralela = VolumenDelDiseno.proyectar(atras, 0f, 30f)
        val conOjo = VolumenDelDiseno.proyectar(atras, 0f, 30f, distanciaCm = 500f, centro = centro)
        assertTrue("lo de atrás tenía que achicarse", kotlin.math.abs(conOjo.y) < kotlin.math.abs(paralela.y))
        val delante = Punto3D(200f, 200f, 0f)
        val delanteConOjo = VolumenDelDiseno.proyectar(delante, 0f, 30f, distanciaCm = 500f, centro = centro)
        assertTrue("lo de delante tenía que agrandarse", kotlin.math.abs(delanteConOjo.y) > kotlin.math.abs(VolumenDelDiseno.proyectar(delante, 0f, 30f).y))
    }
}
