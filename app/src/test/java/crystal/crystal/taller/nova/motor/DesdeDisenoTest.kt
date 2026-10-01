package crystal.crystal.taller.nova.motor

import crystal.crystal.taller.nova.NovaCalculos
import crystal.crystal.taller.nova.NovaUIHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Nova con ceil y Nova desigual son el mismo cálculo (N-70, N-81): una ventana automática, escrita
 * como paquete de diseño y leída otra vez por el lector único, tiene que dar los mismos materiales.
 */
class DesdeDisenoTest {

    private val u13 = PerfilU(1f, 1.5f)
    private val puentes = listOf(Puente("Múltiple", 1.5f, 5f), Puente("tubo 2⅜ x 1", 2.5f, 6f))

    @After
    fun limpiar() {
        NovaCalculos.ncfcUnTramo = false
        NovaCalculos.nfcUnTramo = false
    }

    private fun textos(m: MaterialesNova) = listOf(
        "U" to MaterialesNova.texto(m.u), "puente" to MaterialesNova.texto(m.puente),
        "riel" to MaterialesNova.texto(m.riel), "riel sup" to MaterialesNova.texto(m.rielSuperior),
        "portafelpa" to MaterialesNova.texto(m.portafelpa), "tee" to MaterialesNova.texto(m.tee),
        "tope" to MaterialesNova.texto(m.tope), "hache" to MaterialesNova.texto(m.hache),
        "parante" to MaterialesNova.texto(m.parante), "vidrios" to MaterialesNova.textoVidrios(m.vidrios)
    )

    @Test
    fun ida_y_vuelta_por_el_paquete_da_lo_mismo() {
        var casos = 0
        for (acabado in Acabado.values()) for (modelo in ModeloMocheta.values())
            for (mod in listOf("nn", "nfc", "nff", "ncfc", "ncc", "n3c"))
                for (ancho in listOf(100f, 145f, 199.9f, 250f, 330f, 420f, 560f, 700f)) {
                    val puente = if (acabado == Acabado.APA && modelo == ModeloMocheta.NORMAL) puentes[0] else puentes[1]
                    NovaCalculos.ncfcUnTramo = acabado == Acabado.INA && mod == "ncfc"
                    NovaCalculos.nfcUnTramo = false
                    val lado = DisenoAutomatico.lado(DisenoAutomatico.Entrada(
                        anchoMedido = ancho, anchoCalculo = ancho, alto = 145f, hoja = 100f,
                        acabado = acabado, puente = puente, modulacion = mod
                    ))
                    val automatico = MotorNova.calcular(VentanaNova(acabado, modelo, listOf(lado), u13, puente))

                    val remate = when (modelo) { ModeloMocheta.NORMAL -> "nn"; ModeloMocheta.INVERTIDO -> "nr"; ModeloMocheta.BANDERA -> "np" }
                    val paquete = EscritorNova.paqueteAutomatico(
                        if (acabado == Acabado.APA) "apa" else "ina", ancho, 145f, 100f, lado.divisiones, mod, 0f, remate
                    )
                    val v = DesdeDiseno.ventana(paquete, u13, puente, modulacion = mod)
                    assertNotNull("no se leyó: $paquete", v)
                    val desigual = MotorNova.calcular(v!!)
                    textos(automatico).zip(textos(desigual)).forEach { (a, d) ->
                        assertEquals("$acabado $modelo $mod $ancho · ${a.first}\n$paquete", a.second, d.second)
                    }
                    casos++
                }
        assertEquals(288, casos)
    }
}
