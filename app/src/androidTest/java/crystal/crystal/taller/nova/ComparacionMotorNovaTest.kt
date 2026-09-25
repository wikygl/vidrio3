package crystal.crystal.taller.nova

import android.view.View
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import crystal.crystal.databinding.ActivityNovaCorredizaBinding
import crystal.crystal.taller.nova.motor.Acabado
import crystal.crystal.taller.nova.motor.DisenoAutomatico
import crystal.crystal.taller.nova.motor.MaterialesNova
import crystal.crystal.taller.nova.motor.ModeloMocheta
import crystal.crystal.taller.nova.motor.MotorNova
import crystal.crystal.taller.nova.motor.PerfilU
import crystal.crystal.taller.nova.motor.Puente
import crystal.crystal.taller.nova.motor.VentanaNova
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Fase 2 del motor único (docs/AUDITORIA_NOVA.md): la pantalla de Nova de hoy contra [MotorNova],
 * ventana por ventana y pieza por pieza. No falla: escribe `nova_comparacion.txt` en los archivos
 * de la app con cada diferencia, para explicarla con una regla o investigarla.
 */
@RunWith(AndroidJUnit4::class)
class ComparacionMotorNovaTest {

    private data class Caso(
        val acabado: Acabado, val modelo: ModeloMocheta, val modulacion: String,
        val ancho: Float, val alto: Float, val hoja: Float, val puente: Puente
    ) {
        override fun toString() =
            "${acabado.name} ${modelo.name.lowercase()} $modulacion ${ancho}x$alto hoja $hoja ${puente.nombre}"
    }

    private val multiple = Puente("Múltiple", 1.5f, 5f)
    private val tubo238 = Puente("tubo 2⅜ x 1", 2.5f, 6f)
    private val us = 1.5f

    private fun casos(): List<Caso> {
        val out = mutableListOf<Caso>()
        val anchos = listOf(100f, 145f, 199.9f, 250f, 330f, 420f, 560f, 700f)
        for (acabado in Acabado.values()) for (modelo in ModeloMocheta.values())
            for (mod in listOf("nn", "nfc", "nff", "ncfc", "ncc", "n3c")) for (ancho in anchos)
                for (hoja in listOf(100f, 150f)) {
                    // Invertido, bandera e INA no admiten Múltiple (N-03).
                    val puentes = if (acabado == Acabado.APA && modelo == ModeloMocheta.NORMAL) listOf(multiple, tubo238) else listOf(tubo238)
                    puentes.forEach { out.add(Caso(acabado, modelo, mod, ancho, 145f, hoja, it)) }
                }
        return out
    }

    private fun campo(o: Any, n: String) = o.javaClass.getDeclaredField(n).apply { isAccessible = true }
    private fun llamar(o: Any, n: String) = o.javaClass.getDeclaredMethod(n).apply { isAccessible = true }.invoke(o)

    private fun visible(tx: TextView, contenedor: View?): String =
        if (contenedor == null || contenedor.visibility == View.VISIBLE) tx.text?.toString().orEmpty() else ""

    private fun lineas(t: String): List<String> = t.lines().map { it.trim() }
        .filter { it.isNotBlank() && it.substringAfter("=", "").trim() != "0" }.sorted()

    private fun motor(c: Caso): Map<String, String> {
        val lado = DisenoAutomatico.lado(DisenoAutomatico.Entrada(
            anchoMedido = c.ancho, anchoCalculo = c.ancho, alto = c.alto, hoja = c.hoja,
            acabado = c.acabado, puente = c.puente, modulacion = c.modulacion
        ))
        val m = MotorNova.calcular(VentanaNova(c.acabado, c.modelo, listOf(lado), PerfilU(1f, us), c.puente))
        return mapOf(
            "U" to MaterialesNova.texto(m.u), "puente" to MaterialesNova.texto(m.puente),
            "riel" to MaterialesNova.texto(m.riel), "riel sup" to MaterialesNova.texto(m.rielSuperior),
            "portafelpa" to MaterialesNova.texto(m.portafelpa), "tee" to MaterialesNova.texto(m.tee),
            "tope" to MaterialesNova.texto(m.tope), "hache" to MaterialesNova.texto(m.hache),
            "parante" to MaterialesNova.texto(m.parante), "vidrios" to MaterialesNova.textoVidrios(m.vidrios)
        )
    }

    /**
     * Fase 3: con el motor encendido, lo que muestra la pantalla tiene que ser exactamente lo del
     * motor. Prueba también el paso de los campos de la pantalla a la ventana del motor.
     */
    @Test
    fun con_el_motor_encendido_la_pantalla_muestra_el_motor() {
        val fallos = StringBuilder()
        ActivityScenario.launch(NovaCorrediza::class.java).use { sc ->
            sc.onActivity { a ->
                val b = campo(a, "binding").get(a) as ActivityNovaCorredizaBinding
                campo(a, "usarMotor").set(a, true)
                for (c in casos()) {
                    NovaCalculos.repartoManual = null; NovaCalculos.corredizasPorTramoNfc = 0
                    campo(a, "tipoNova").set(a, if (c.acabado == Acabado.APA) NovaCorrediza.TipoNova.APA else NovaCorrediza.TipoNova.INA)
                    campo(a, "textoModelo").set(a, c.modulacion)
                    campo(a, "modeloRemate").set(a, when (c.modelo) { ModeloMocheta.NORMAL -> "nn"; ModeloMocheta.INVERTIDO -> "nr"; ModeloMocheta.BANDERA -> "np" })
                    campo(a, "texto").set(a, "nn")
                    campo(a, "puente").set(a, c.puente.nombre); campo(a, "tubo").set(a, c.puente.altura)
                    campo(a, "metaEncuentro").set(a, "1111")
                    b.etAncho.setText(NovaCalculos.df1(c.ancho)); b.etAlto.setText(NovaCalculos.df1(c.alto))
                    b.etHoja.setText(NovaCalculos.df1(c.hoja)); b.etPartes.setText("0")
                    b.etU.setText(NovaCalculos.df1(us)); b.etCruce.setText("0")
                    llamar(a, "uTexto"); llamar(a, "otrosAluminios"); llamar(a, "vidriosTexto"); llamar(a, "aplicarMotor")
                    val nuevo = motor(c)
                    val pantalla = mapOf(
                        "U" to b.txU.text.toString(), "puente" to b.txP.text.toString(), "riel" to b.txR.text.toString(),
                        "riel sup" to b.txFc.text.toString(), "portafelpa" to b.txPf.text.toString(), "tee" to b.txTe.text.toString(),
                        "tope" to b.txTo.text.toString(), "hache" to b.txH.text.toString(), "parante" to b.txT.text.toString(),
                        "vidrios" to b.txV.text.toString()
                    )
                    for ((pieza, visto) in pantalla) {
                        if (lineas(visto) != lineas(nuevo[pieza].orEmpty())) fallos.append("$c | $pieza: ${lineas(visto)} ≠ ${lineas(nuevo[pieza].orEmpty())}\n")
                    }
                }
            }
        }
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        java.io.File(ctx.getExternalFilesDir(null), "nova_motor_encendido.txt").writeText(fallos.ifEmpty { "sin diferencias\n" }.toString())
        org.junit.Assert.assertEquals("", fallos.toString().take(2000))
    }

    /** Ventanas en L y en C: la pantalla (calcularMaterialesNl/Nu) contra el motor. */
    private fun esquinas(informe: StringBuilder, resumen: MutableMap<String, Int>): Int {
        var n = 0
        val medidas = listOf(listOf(120f, 150f), listOf(122f, 40f), listOf(200f, 300f), listOf(90f, 180f, 90f), listOf(150f, 250f, 120f))
        ActivityScenario.launch(NovaCorrediza::class.java).use { sc ->
            sc.onActivity { a ->
                val b = campo(a, "binding").get(a) as ActivityNovaCorredizaBinding
                val claseMedida = Class.forName("crystal.crystal.taller.nova.NovaCorrediza\$MedidaNl")
                val ctor = claseMedida.declaredConstructors.first { it.parameterTypes.size == 4 }.apply { isAccessible = true }
                fun medida(ancho: Float) = ctor.newInstance(ancho, 145f, 100f, 0)
                for (acabado in Acabado.values()) for (lados in medidas) {
                    n++
                    val puente = tubo238
                    NovaCalculos.repartoManual = null; NovaCalculos.ncfcUnTramo = false; NovaCalculos.nfcUnTramo = false
                    campo(a, "tipoNova").set(a, if (acabado == Acabado.APA) NovaCorrediza.TipoNova.APA else NovaCorrediza.TipoNova.INA)
                    campo(a, "textoModelo").set(a, "nn"); campo(a, "modeloRemate").set(a, "nn")
                    campo(a, "puente").set(a, puente.nombre); campo(a, "tubo").set(a, puente.altura)
                    campo(a, "metaEncuentro").set(a, "1111"); campo(a, "metaEsquinero").set(a, "tubo 2⅜ x 1")
                    b.etU.setText("1.5"); b.etCruce.setText("0")
                    listOf(b.etAncho2, b.etAlto2, b.etPuente2, b.etDivi2, b.etAncho3, b.etAlto3, b.etPuente3, b.etDivi3).forEach { it.setText("") }
                    if (lados.size == 2) {
                        campo(a, "texto").set(a, "nl")
                        campo(a, "primeraMedidaNl").set(a, medida(lados[0]))
                    } else {
                        campo(a, "texto").set(a, "nu")
                        campo(a, "primeraMedidaNu").set(a, medida(lados[0]))
                        campo(a, "segundaMedidaNu").set(a, medida(lados[1]))
                    }
                    b.etAncho.setText(NovaCalculos.df1(lados.last())); b.etAlto.setText("145"); b.etHoja.setText("100"); b.etPartes.setText("0")
                    val pantalla = try {
                        llamar(a, if (lados.size == 2) "calcularMaterialesNl" else "calcularMaterialesNu")
                        mapOf(
                            "U" to b.txU.text.toString(), "puente" to b.txP.text.toString(), "riel" to b.txR.text.toString(),
                            "riel sup" to b.txFc.text.toString(), "portafelpa" to b.txPf.text.toString(), "tee" to b.txTe.text.toString(),
                            "tope" to b.txTo.text.toString(), "hache" to b.txH.text.toString(), "parante" to b.txT.text.toString(),
                            "vidrios" to b.txV.text.toString()
                        )
                    } catch (e: Exception) {
                        informe.append("!! $acabado $lados: la pantalla falló: ${e.cause ?: e}\n"); continue
                    }
                    val v = crystal.crystal.taller.nova.motor.VentanaAutomatica.ventana(
                        crystal.crystal.taller.nova.motor.VentanaAutomatica.Entrada(
                            acabado, ModeloMocheta.NORMAL, "nn",
                            lados.map { crystal.crystal.taller.nova.motor.VentanaAutomatica.LadoMedido(it, 145f, 100f) },
                            PerfilU(1f, us), puente, esquinero = tubo238
                        )
                    )
                    val m = MotorNova.calcular(v)
                    val nuevo = mapOf(
                        "U" to MaterialesNova.texto(m.u), "puente" to MaterialesNova.texto(m.puente),
                        "riel" to MaterialesNova.texto(m.riel), "riel sup" to MaterialesNova.texto(m.rielSuperior),
                        "portafelpa" to MaterialesNova.texto(m.portafelpa), "tee" to MaterialesNova.texto(m.tee),
                        "tope" to MaterialesNova.texto(m.tope), "hache" to MaterialesNova.texto(m.hache),
                        "parante" to MaterialesNova.texto(m.parante), "vidrios" to MaterialesNova.textoVidrios(m.vidrios)
                    )
                    for ((pieza, viejo) in pantalla) {
                        val lv = lineas(viejo); val ln = lineas(nuevo[pieza].orEmpty())
                        if (lv != ln) {
                            val clave = "$pieza · $acabado esquina ${if (lados.size == 2) "L" else "C"}"
                            resumen[clave] = (resumen[clave] ?: 0) + 1
                            informe.append("$acabado ${if (lados.size == 2) "L" else "C"} $lados | $pieza\n   pantalla: ${lv.joinToString("; ")}\n   motor:    ${ln.joinToString("; ")}\n")
                        }
                    }
                }
            }
        }
        return n
    }

    @Test
    fun la_pantalla_contra_el_motor() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val informe = StringBuilder()
        val resumen = linkedMapOf<String, Int>()
        var total = 0
        ActivityScenario.launch(NovaCorrediza::class.java).use { sc ->
            sc.onActivity { a ->
                val b = campo(a, "binding").get(a) as ActivityNovaCorredizaBinding
                for (c in casos()) {
                    total++
                    NovaCalculos.repartoManual = null
                    NovaCalculos.corredizasPorTramoNfc = 0
                    NovaCalculos.ncfcUnTramo = c.acabado == Acabado.INA && c.modulacion == "ncfc"
                    NovaCalculos.nfcUnTramo = c.acabado == Acabado.INA && c.modulacion == "nfc"
                    campo(a, "tipoNova").set(a, if (c.acabado == Acabado.APA) NovaCorrediza.TipoNova.APA else NovaCorrediza.TipoNova.INA)
                    campo(a, "textoModelo").set(a, c.modulacion)
                    campo(a, "modeloRemate").set(a, when (c.modelo) { ModeloMocheta.NORMAL -> "nn"; ModeloMocheta.INVERTIDO -> "nr"; ModeloMocheta.BANDERA -> "np" })
                    campo(a, "texto").set(a, "nn")
                    campo(a, "puente").set(a, c.puente.nombre)
                    campo(a, "tubo").set(a, c.puente.altura)
                    campo(a, "metaEncuentro").set(a, "1111")
                    b.etAncho.setText(NovaCalculos.df1(c.ancho))
                    b.etAlto.setText(NovaCalculos.df1(c.alto))
                    b.etHoja.setText(NovaCalculos.df1(c.hoja))
                    // La misma cantidad de hojas en los dos (N-20: ncc 2, n3c 3, ncfc 3), para comparar piezas.
                    b.etPartes.setText(DisenoAutomatico.divisiones(c.ancho, 0, c.modulacion).toString())
                    b.etU.setText(NovaCalculos.df1(us))
                    b.etCruce.setText("0")
                    val pantalla = try {
                        llamar(a, "uTexto"); llamar(a, "otrosAluminios"); llamar(a, "vidriosTexto")
                        mapOf(
                            "U" to b.txU.text.toString(), "puente" to visible(b.txP, b.mulLayout),
                            "riel" to visible(b.txR, b.lyRiel), "riel sup" to visible(b.txFc, b.fcLayout),
                            "portafelpa" to visible(b.txPf, b.lyPf), "tee" to visible(b.txTe, b.tLayout),
                            "tope" to visible(b.txTo, b.lyTo), "hache" to visible(b.txH, b.lyH),
                            "parante" to visible(b.txT, b.lyTubo), "vidrios" to b.txV.text.toString()
                        )
                    } catch (e: Exception) {
                        informe.append("!! $c: la pantalla falló: ${e.cause ?: e}\n"); continue
                    }
                    val nuevo = motor(c)
                    for ((pieza, viejo) in pantalla) {
                        val lv = lineas(viejo); val ln = lineas(nuevo[pieza].orEmpty())
                        if (lv != ln) {
                            val clave = "$pieza · ${c.acabado} ${c.modelo.name.lowercase()} ${c.modulacion}"
                            resumen[clave] = (resumen[clave] ?: 0) + 1
                            informe.append("$c | $pieza\n   pantalla: ${lv.joinToString("; ")}\n   motor:    ${ln.joinToString("; ")}\n")
                        }
                    }
                }
            }
        }
        total += esquinas(informe, resumen)
        val cabecera = StringBuilder("Casos: $total\nDiferencias por pieza y variante (casos):\n")
        resumen.forEach { (k, n) -> cabecera.append("  $n  $k\n") }
        val archivo = java.io.File(ctx.getExternalFilesDir(null), "nova_comparacion.txt")
        archivo.writeText(cabecera.append("\n").append(informe).toString())
    }
}
