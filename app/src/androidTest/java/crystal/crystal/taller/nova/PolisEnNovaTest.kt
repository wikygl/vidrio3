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
