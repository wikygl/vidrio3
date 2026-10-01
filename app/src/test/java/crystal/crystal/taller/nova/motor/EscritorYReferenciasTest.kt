package crystal.crystal.taller.nova.motor

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * El paquete y las referencias escritos desde el motor (fase 4 del motor único, 2026-09-30).
 * Comparados con lo que escribía el código viejo antes de retirarlo: el reparto de hojas y tramos
 * salió igual en nn, nff y nfc; cambió donde el viejo no seguía las reglas (N-20: doble corrediza 2
 * hojas y ncfc 3, que el viejo repartía por la regla de los 60; la mocheta como espacio con el
 * puente elegido, N-31, y las de bandera con sus dos puentes, N-34).
 */
class EscritorYReferenciasTest {

    private val multiple = Puente("Múltiple", 1.5f, 5f)

    private fun ventana(ancho: Float, alto: Float, puente: Puente = multiple, modelo: ModeloMocheta = ModeloMocheta.NORMAL): VentanaNova {
        val lado = DisenoAutomatico.lado(DisenoAutomatico.Entrada(anchoMedido = ancho, anchoCalculo = ancho,
            alto = alto, hoja = 0f, acabado = Acabado.APA, puente = puente))
        return VentanaNova(Acabado.APA, modelo, listOf(lado), PerfilU(1f, 1f), puente)
    }

    /** Fide, 214.5 × 157.5 con Múltiple: 4 hojas de 53.6, hoja 112.5, mocheta 157.5 − 112.5 − 1.5 = 43.5. */
    @Test
    fun el_paquete_sale_de_la_ventana_del_motor() {
        assertEquals(
            "{nova,apa,[214.5,157.5:Tl<214.5>(s<112.5>(f<53.6>c<53.6>c<53.6>f<53.6>);m<43.5>(f<107.2>f<107.2>))]}",
            EscritorNova.paquetePlano(ventana(214.5f, 157.5f), "apa")
        )
    }

    @Test
    fun las_referencias_salen_de_la_ventana_del_motor() {
        assertEquals(
            "An: 214.5  x  Al: 157.5\nAltura de puente: 112.5\nmocheta sup: 43.5\nDivisiones: 4 -> fjs: 2;czs: 2",
            ReferenciasNova.texto(ventana(214.5f, 157.5f))
        )
    }

    /** Xxxx, 726 × 160 en 13 hojas: los puntos de siempre sobre la U de fijo (55.892): 110.3_220.7_331.1. */
    @Test
    fun los_puntos_con_mas_de_4_divisiones() {
        val r = ReferenciasNova.texto(ventana(726f, 160f))
        assertEquals(r, "Puntos: 110.3_220.7_331.1", r.lines().last())
    }

    /** Bandera con tubo: las dos mochetas como espacio, (180 − 128.6 − 2 × 2.5) / 2 = 23.2 (N-34). */
    @Test
    fun las_mochetas_de_bandera_son_espacio() {
        val v = ventana(160f, 180f, Puente("tubo 2⅜ x 1", 2.5f, 6f), ModeloMocheta.BANDERA)
        val r = ReferenciasNova.texto(v)
        assertEquals(r, listOf("mocheta inf: 23.2", "mocheta sup: 23.2"), r.lines().filter { it.startsWith("mocheta") })
        val p = EscritorNova.paquetePlano(v, "apa")
        assertEquals(p, 2, Regex("m<23.2>").findAll(p).count())
    }
}
