package crystal.crystal.taller.venAl

import crystal.crystal.taller.nova.NovaCalculos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Calculo84Test {

    private val eco = 3f

    @Test
    fun conPuente_ventanaDeDosHojas() {
        val r = Calculo84.calcular(Calculo84.Entrada(ancho = 120f, alto = 150f, pierna = eco))
        assertEquals(listOf("fc"), r.tramos)
        assertTrue(r.llevaPuente)
        assertEquals(listOf("115.6 = 2", "150 = 2"), r.marco)
        assertEquals(listOf("115.6 = 1"), r.rielSup)
        assertEquals(listOf("115.6 = 1"), r.rielInf)
        assertEquals(listOf("53.4 = 4"), r.zocalo)
        assertEquals(listOf("104 = 2", "102.6 = 2"), r.pierna)
        // Una mocheta: el puente y ningún separador.
        assertEquals(listOf("115.6 = 1"), r.tubo)
        assertEquals(listOf("115.6 = 2", "39.1 = 2"), r.junquillo)
        // N-14: el vidrio de mocheta también va ancho x alto.
        assertEquals(listOf("54.6 x 96 = 1", "54.6 x 94.6 = 1", "115.1 x 38.7 = 1"), r.vidrios)
        assertEquals(listOf("102.6 = 1"), r.tope)
    }

    @Test
    fun hojaMayorQueElAltoUtil_noLlevaPuente() {
        // Dictado el 2026-10-15: también cuando la hoja llega al alto útil menos el puente (143.1):
        // ahí la mocheta no cabe.
        for (hoja in listOf(150f, 145.6f, 147f, 144f, 143.1f)) {
            val r = Calculo84.calcular(Calculo84.Entrada(ancho = 120f, alto = 150f, altoHoja = hoja, pierna = eco))
            assertFalse("hoja $hoja", r.llevaPuente)
            assertEquals(145.6f, r.altoHoja, 0.001f)
            assertTrue(r.tubo.isEmpty())
            assertTrue(r.junquillo.isEmpty())
            assertEquals(listOf("54.6 x 137.6 = 1", "54.6 x 136.2 = 1"), r.vidrios)
            assertEquals(listOf("145.6 = 2", "144.2 = 2"), r.pierna)
        }
    }

    @Test
    fun sinPuente_elParanteDeTramoSigue() {
        val r = Calculo84.calcular(Calculo84.Entrada(ancho = 400f, alto = 200f, altoHoja = 300f, divisiones = 6))
        assertEquals(listOf("fcf", "fcf"), r.tramos)
        assertFalse(r.llevaPuente)
        // Dictado el 2026-10-15 ("la regla es uniformizar medidas"): el parante va siempre de piso
        // a techo y el marco sale un trozo por tramo: (395.6 − 2.5) / 2 = 196.55.
        assertEquals(listOf("200 = 1"), r.tubo)
        assertEquals(listOf("196.5 = 4", "200 = 2"), r.marco)
    }

    @Test
    fun marco_unTrozoPorTramo() {
        // Dictado el 2026-10-15: 700 sale en 12 hojas, tres tramos de 4 de 230.2. Cabrían 462.9 +
        // 230.2, pero se uniformizan las medidas: cada parante de piso a techo y el marco 230.2 = 6.
        val r = Calculo84.calcular(Calculo84.Entrada(ancho = 700f, alto = 200f))
        assertEquals(12, r.divisiones)
        assertEquals(listOf(4, 4, 4), r.tramos.map { it.length })
        assertEquals(listOf("230.2 = 6", "200 = 2"), r.marco)
        // Puente por tramo; separador de mocheta (2 por tramo de 230.2); los dos parantes enteros.
        assertEquals(listOf("230.2 = 3", "53.3 = 3", "200 = 2"), r.tubo)
    }

    @Test
    fun marco_unTrozoPorTramoAunqueQuepaEntero() {
        // 595 de ancho: el marco entero (590.6) cabría en una barra, pero va uno por tramo:
        // (590.6 − 2.5) / 2 = 294.05.
        val r = Calculo84.calcular(Calculo84.Entrada(ancho = 595f, alto = 200f, diseno = "fc@sm;fc@sm"))
        assertEquals(listOf("294 = 4", "200 = 2"), r.marco)
        assertEquals(listOf("294 = 2", "53.3 = 2", "200 = 1"), r.tubo)
    }

    @Test
    fun hojaConAnchoEscrito_lasDemasSeRepartenElResto() {
        // Hojas: 115.6 + 3.2 (un cruce) = 118.8; el fijo a 70 deja 48.8 a la corrediza.
        val r = Calculo84.calcular(
            Calculo84.Entrada(ancho = 120f, alto = 150f, pierna = eco, diseno = "f<70>c@sm")
        )
        assertTrue(r.manual)
        assertEquals(null, r.aviso)
        assertEquals(listOf("115.6 = 2", "150 = 2"), r.marco)
        assertEquals(listOf("64 = 2", "42.8 = 2"), r.zocalo)
        assertEquals(listOf("65.2 x 96 = 1", "44 x 94.6 = 1", "115.1 x 38.7 = 1"), r.vidrios)
    }

    @Test
    fun hojaQueNoCabe_seRepartePorIgualYAvisa() {
        val r = Calculo84.calcular(
            Calculo84.Entrada(ancho = 120f, alto = 150f, pierna = eco, diseno = "f<200>c@sm")
        )
        assertTrue(r.aviso != null)
        assertEquals(listOf("53.4 = 4"), r.zocalo)
    }

    @Test
    fun bandera_dosMochetasDeIgualAlto() {
        // N-34: hoja 104 y el resto en dos: (145.6 − 2 × 2.5 − 104) / 2 = 18.3.
        val r = Calculo84.calcular(Calculo84.Entrada(ancho = 120f, alto = 150f, pierna = eco, diseno = "fc@msm"))
        assertEquals(listOf("18.3", "104", "18.3"), r.geometria[0].franjas.map { NovaCalculos.df1(it.alto!!) })
        assertEquals(listOf("115.6 = 2"), r.tubo)
        assertEquals(listOf("115.6 = 4", "18.3 = 4"), r.junquillo)
        assertEquals(listOf("54.6 x 96 = 1", "54.6 x 94.6 = 1", "115.1 x 17.9 = 2"), r.vidrios)
    }

    @Test
    fun tramosConFranjasDistintas() {
        // Tramo 1 sin puente (solo sistema); tramo 2 con mocheta de 30 y la hoja con el resto:
        // 145.6 − 2.5 − 30 = 113.1.
        val r = Calculo84.calcular(
            Calculo84.Entrada(ancho = 200f, alto = 150f, pierna = eco, diseno = "fc@s;fc@sm<30>")
        )
        assertEquals(145.6f, r.geometria[0].altoHoja, 0.001f)
        assertEquals(113.1f, r.geometria[1].altoHoja, 0.001f)
        assertTrue(r.llevaPuente)
        // Un solo puente (el del tramo 2, de (195.6 − 2.5) / 2) y el parante de piso a techo.
        assertEquals(listOf("96.5 = 1", "150 = 1"), r.tubo)
    }

    @Test
    fun sinCorredizasNoLlevaTope() {
        val r = Calculo84.calcular(Calculo84.Entrada(ancho = 60f, alto = 100f, diseno = "f@s"))
        assertTrue(r.tope.isEmpty())
    }

    @Test
    fun disenoIdaYVuelta() {
        val texto = "f<70.0>c@m<20.0>sm;fcf@s"
        assertEquals(texto, Calculo84.escribirDiseno(Calculo84.leerDiseno(texto)!!))
        assertEquals(null, Calculo84.leerDiseno("tramos"))
        assertEquals(null, Calculo84.leerDiseno(""))
        assertEquals(null, Calculo84.leerDiseno("fc@mm"))   // sin sistema
        assertEquals(null, Calculo84.leerDiseno("fc@ss"))   // dos sistemas
    }

    @Test
    fun elAutomaticoEscritoAManoDaLoMismo() {
        val auto = Calculo84.calcular(Calculo84.Entrada(ancho = 400f, alto = 200f))
        val mano = Calculo84.calcular(
            Calculo84.Entrada(ancho = 400f, alto = 200f, diseno = auto.tramos.joinToString(";") { "$it@sm" })
        )
        assertEquals(auto.copy(manual = true), mano)
    }

    @Test
    fun sieteHojas_dosTramosComoElDibujo() {
        val r = Calculo84.calcular(Calculo84.Entrada(ancho = 400f, alto = 200f))
        assertEquals(7, r.divisiones)
        assertEquals(2, r.tramos.size)
        assertEquals(7, r.tramos.sumOf { it.length })
        assertEquals(r.tramos.sumOf { it.count { c -> c == 'f' } }, r.nFijos)
        // Un puente por tramo y el parante al final, de piso a techo.
        assertEquals("200 = 1", r.tubo.last())
        val puentes = r.tubo.dropLast(1).sumOf { it.substringAfter("= ").toInt() }
        assertTrue(puentes >= 2)
    }

    @Test
    fun elRepartoAManoDeNovaNoEntra() {
        NovaCalculos.repartoManual = listOf(2, 2, 2)
        try {
            val r = Calculo84.calcular(Calculo84.Entrada(ancho = 400f, alto = 200f, divisiones = 6))
            assertEquals(listOf("fcf", "fcf"), r.tramos)
            assertEquals(listOf(2, 2, 2), NovaCalculos.repartoManual)
        } finally {
            NovaCalculos.repartoManual = null
        }
    }

    @Test
    fun cruces() {
        assertEquals(1, Calculo84.cruces("fc"))
        assertEquals(2, Calculo84.cruces("fcf"))
        assertEquals(2, Calculo84.cruces("fccf"))
        assertEquals(4, Calculo84.cruces("fcfcf"))
    }
}
