package crystal.crystal.taller.nova

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import crystal.crystal.databinding.ActivityNovaCorredizaBinding
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * "polis", tal como el usuario lo archivó el 2026-09-26: dos tramos invertidos, una viga sobre el
 * fijo de la izquierda y el alféizar subido a la derecha. En la pantalla de Nova:
 * - el fijo bajo la viga mide 94.4 de hoja (vidrio 61.8 x 93.2) y el fijo corredizo de su tramo
 *   solo llega hasta la viga (190.1);
 * - las Referencias dicen que la mocheta es inferior y mide 53.6 (N-31), no "mocheta sup: 56.1";
 * - la cadena simbólica dice forma poligonal y modelo invertido: G<p,p,m,i>, no G<p,r,m,n>.
 */
@RunWith(AndroidJUnit4::class)
class PolisEnNovaTest {

    private val polis = "{nova,apa,[506.5,189.7:Tl<252>(m<56.1>(f<126>f<126>);s<133.6>(f<62>c<63.3>c<63.3>f<63.3>))" +
        "P<2.5>Tl<252>(m<56.1>(f<252>);s<133.6>(f<63>c<63>c<63>f<63>))" +
        "V<0/39.2|0/189.7|302.9/189.7|302.9/133.6|506.5/133.2|506.5/0|61.8/0|61.8/39.2>]}"

    private fun campo(o: Any, n: String) = o.javaClass.getDeclaredField(n).apply { isAccessible = true }
    private fun llamar(o: Any, n: String, vararg args: Any) =
        o.javaClass.getDeclaredMethod(n, *args.map { it.javaClass }.toTypedArray())
            .apply { isAccessible = true }.invoke(o, *args)

    @Test
    fun polis_2_la_columna_en_la_pantalla_de_nova() {
        // La columna en el costado del fijo: la U sigue la pared (37.9, 37.6, 97.2, 15.7, 18.4).
        val polis2 = "{nova,apa,[229.5,223.4:Tl<229.5>(m<43.7>(f<114.7>f<114.7>);s<136>(f<76.5>c<76.5>f<76.5>);" +
            "m<43.7>(f<114.7>f<114.7>))V<0/0|229.5/0.6|229.5/223.4|0.3/223.4|0.3/158.3|36.4/158.3|36.4/61.1|0/61.1>]}"
        ActivityScenario.launch(NovaCorrediza::class.java).use { sc ->
            sc.onActivity { a ->
                val b = campo(a, "binding").get(a) as ActivityNovaCorredizaBinding
                campo(a, "texto").set(a, "nn")
                campo(a, "textoModelo").set(a, "nn")
                campo(a, "tipoNova").set(a, NovaCorrediza.TipoNova.APA)
                campo(a, "puente").set(a, "tubo 2⅜ x 1"); campo(a, "tubo").set(a, 2.5f)
                b.etU.setText("1.5"); b.etCruce.setText("0.7")
                llamar(a, "recibirDisenoEditado", polis2)
                llamar(a, "calcularDesigual")
                llamar(a, "aplicarMotor")
                val u = b.txU.text.toString()
                listOf("37.9 = 1", "37.6 = 1", "97.2 = 1", "15.7 = 1", "18.4 = 1", "134.3 = 1")
                    .forEach { assertTrue("$it\n$u", u.lines().any { l -> l.trim() == it }) }
            }
        }
    }

    @Test
    fun la_ficha_del_proyecto_dibuja_la_forma() {
        // Pedido del usuario (2026-09-26): la ficha del proyecto mostraba "polis" ventana 2 como un
        // rectángulo, porque el SVG manual no recorta la silueta. Con forma sale como imagen.
        val ctx = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        fun svg(p: String): String {
            val vista = crystal.crystal.Diseno.nova.VistaDiseno(ctx).apply { actualizarDesdePaquete(p, 0f, 0f, 0f) }
            vista.layout(0, 0, 1080, 1920)
            return vista.exportarSoloDisenoSVG(paddingPx = 4)
        }
        val polis2 = "{nova,apa,[229.5,223.4:Tl<229.5>(m<43.7>(f<114.7>f<114.7>);s<136>(f<76.5>c<76.5>f<76.5>);" +
            "m<43.7>(f<114.7>f<114.7>))V<0/0|229.5/0.6|229.5/223.4|0.3/223.4|0.3/158.3|36.4/158.3|36.4/61.1|0/61.1>]}"
        assertTrue(svg(polis2).contains("data:image/png"))
        val rectangulo = "{nova,apa,[150,120:Tl<150>(s<85.7>(f<75>c<75>);m<34.3>(f<150>))]}"
        assertTrue(!svg(rectangulo).contains("data:image/png"))
    }

    @Test
    fun polis_en_la_pantalla_de_nova() {
        ActivityScenario.launch(NovaCorrediza::class.java).use { sc ->
            sc.onActivity { a ->
                val b = campo(a, "binding").get(a) as ActivityNovaCorredizaBinding
                campo(a, "texto").set(a, "nn")
                campo(a, "textoModelo").set(a, "nn")
                campo(a, "tipoNova").set(a, NovaCorrediza.TipoNova.APA)
                campo(a, "puente").set(a, "tubo 2⅜ x 1"); campo(a, "tubo").set(a, 2.5f)
                b.etU.setText("1"); b.etCruce.setText("0.7")
                llamar(a, "recibirDisenoEditado", polis)
                llamar(a, "calcularDesigual")
                llamar(a, "aplicarMotor")

                val vidrios = b.txV.text.toString()
                assertTrue(vidrios, vidrios.contains("61.8 x 93.2"))
                val fijoCorre = b.txFc.text.toString()
                assertTrue(fijoCorre, fijoCorre.contains("190.1"))
                val refs = b.txReferencias.text.toString()
                assertTrue(refs, refs.contains("mocheta inf: 53.6") && !refs.contains("mocheta sup"))
                val v2 = llamar(a, "disenoSimbolicoV2", 1) as String
                // volumen plano, forma poligonal, (encuentro), modelo invertido
                assertTrue(v2, Regex("-G<p,p,[^,>]*,i>-").containsMatchIn(v2))
            }
        }
    }
}
