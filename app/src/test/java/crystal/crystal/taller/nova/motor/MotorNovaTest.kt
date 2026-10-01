package crystal.crystal.taller.nova.motor

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * El motor único de Nova contra las reglas de docs/REGLAS_NOVA.md. Los casos con número de regla
 * son cuentas hechas a mano con esa regla; el de la L es el ejemplo que dictó el usuario.
 */
class MotorNovaTest {

    private val multiple = Puente("Múltiple", 1.5f, 5f)
    private val tubo238 = Puente("tubo 2⅜ x 1", 2.5f, 6f)
    private val u13 = PerfilU(ancho = 1f, alto = 1.5f)

    private fun plana(acabado: Acabado, ancho: Float, alto: Float, hoja: Float, puente: Puente,
                      modelo: ModeloMocheta = ModeloMocheta.NORMAL, modulacion: String = "nn"): MaterialesNova {
        val lado = DisenoAutomatico.lado(DisenoAutomatico.Entrada(
            anchoMedido = ancho, anchoCalculo = ancho, alto = alto, hoja = hoja,
            acabado = acabado, puente = puente, modulacion = modulacion
        ))
        return MotorNova.calcular(VentanaNova(acabado, modelo, listOf(lado), u13, puente))
    }

    private fun t(p: List<Pieza>) = MaterialesNova.texto(p)
    private fun tv(v: List<Vidrio>) = MaterialesNova.textoVidrios(v)

    @Test
    fun aparente_145_por_140_con_multiple() {
        // 3 hojas fcf. U de fijos (145 + 2 × 0.7) / 3 = 48.8. Mocheta 140 − 100 − 1.5 = 38.5.
        val m = plana(Acabado.APA, 145f, 140f, 100f, multiple)
        // H: U de fijos, U de mocheta del tramo (Múltiple: 1). V: U parante 100 − 1.7, U mocheta
        // 38.5 − 1.5: con Múltiple el costado solo topa la U de arriba (dictado el 2026-10-01; antes − 3).
        assertEquals("48.8 = 2\n145 = 1\n98.3 = 2\n37 = 2", t(m.u))
        assertEquals("145 = 1", t(m.puente))
        assertEquals("144.9 = 1", t(m.riel))       // N-51: puente − 0.06
        assertEquals("", t(m.rielSuperior))        // con Múltiple arriba no va
        assertEquals("48.8 = 1", t(m.hache))
        assertEquals("98.4 = 4", t(m.portafelpa))
        assertEquals("", t(m.tope))                // fcf: ninguna corrediza contra pared
        assertEquals("48.3 x 98.3 = 2\n47.4 x 96.5 = 1\n144.5 x 38 = 1", tv(m.vidrios))
    }

    @Test
    fun inaparente_143_por_145() {
        // INA: U de fijos (143 + 1.4) / 3 = 48.13. Mocheta del vidrio: 145 − 100 = 45 (+1 arriba).
        val m = plana(Acabado.INA, 143f, 145f, 100f, tubo238)
        assertEquals("48.1 = 2\n143 = 1\n142 = 2", t(m.u))
        assertEquals("143 = 1", t(m.puente))
        assertEquals("142.9 = 1", t(m.riel))
        assertEquals("142.9 = 1", t(m.rielSuperior))
        assertEquals(RielSuperior.U_FELPERO, m.nombreRielSuperior)
        // Mocheta: 143 − 2 × 48.133 − 0.6 = 46.13 → 46.1, todo en float (con las U truncadas daba 46.2).
        assertEquals("47.6 x 143.3 = 2\n46.7 x 96.5 = 1\n46.1 x 46 = 1", tv(m.vidrios))
    }

    @Test
    fun cruce_parejo_con_tramos_automaticos() {
        // Dictado 2026-09-25: tramos automáticos y ninguno bloqueado → todos los módulos iguales.
        // 420 en 7 hojas (tramos de 4 y 3): (420 − 2.5 + 4 × 0.7) / 7 = 60.04 para todas.
        val m = plana(Acabado.APA, 420f, 145f, 100f, multiple)
        assertEquals("60 = 3", t(m.hache))
    }

    @Test
    fun vidrio_de_mocheta_inaparente_dictado() {
        // Dictado 2026-09-25: 140 en fcf; el vidrio de mocheta es lo que dejan las U de fijos menos
        // 0.3 a cada lado. Todo en float y df1 solo al presentar (dictado el mismo día): U 47.133 →
        // 140 − 2 × 47.133 − 0.6 = 45.13 → 45.1. Con las U ya truncadas (47.1) daba 45.2.
        val m = plana(Acabado.INA, 140f, 145f, 100f, tubo238)
        assertEquals("47.1 = 2\n140 = 1\n142 = 2", t(m.u))
        assertEquals(true, tv(m.vidrios).lines().any { it.startsWith("45.1 x ") })
    }

    @Test
    fun la_L_inaparente_dictada() {
        // Lado 1 de 120 (corrediza contra el muro, fijo en la esquina); lado 2 de 40 (un fijo),
        // alto 100, hoja 80, U-13, cruce 0.7. Resultado dictado: 60.8 = 1, 121 = 1, 41 = 2, 97 = 1, 20 = 1.
        val lado1 = LadoNova(120f, 120f, 100f, 100f, 80f,
            listOf(Tramo(listOf(Modulo(true, 60f), Modulo(false, 60f)))),
            izquierda = Borde.MURO, derecha = Borde.ESQUINA_ANGULO)
        val lado2 = LadoNova(40f, 40f, 100f, 100f, 80f,
            listOf(Tramo(listOf(Modulo(false, 40f)))),
            izquierda = Borde.ESQUINA_ANGULO, derecha = Borde.MURO)
        val m = MotorNova.calcular(VentanaNova(Acabado.INA, ModeloMocheta.NORMAL, listOf(lado1, lado2), u13, tubo238))
        assertEquals("60.8 = 1\n121 = 1\n41 = 2\n20 = 1\n97 = 1", t(m.u))
    }
}
