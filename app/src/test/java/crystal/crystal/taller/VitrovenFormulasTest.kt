package crystal.crystal.taller

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale
import kotlin.math.floor

/**
 * Fija las fórmulas de materiales de Vitroven, que son las verificadas en taller.
 *
 * Réplica exacta de la aritmética de `Vitroven.kt` —incluida la de coma flotante—, porque los
 * descuentos tienen decimales puestos a propósito para que el redondeo caiga donde debe y un
 * cambio mínimo mueve el resultado un milímetro. Ejemplo de referencia: 120 × 60, modelo "vf",
 * dirección horizontal, u13 = 1.5.
 *
 * Antes existía un segundo cálculo "por módulos" con descuentos genéricos que se aplicaba en el uso
 * normal y daba las lamas 1.55 cm más angostas, el fijo 0.2 y el tope 1.9. Estas cifras son las que
 * deben salir.
 */
class VitrovenFormulasTest {

    private val clip = 9.6f
    private val jArmado = 2.25f

    private fun df1(defo: Float): String =
        if (defo % 1 == 0f) defo.toInt().toString() else String.format(Locale.US, "%.1f", defo)

    private fun clipsEnteros(base: Float): Int {
        if (base <= 0f) return 0
        return floor(((base / clip) + 0.0001f).toDouble()).toInt().coerceAtLeast(0)
    }

    // ── Caso de referencia: 120 x 60, "vf", horizontal, u13 = 1.5 ───────────────
    private val med1 = 120f
    private val med2 = 60f
    private val uM = 1.5f

    // "vf" en horizontal: el vitroven toma la mitad del ancho y el fijo la otra mitad.
    private val anchoVitro = med1 / 2f
    private val altoVitro = med2
    private val anchoFijoVf = med1 / 2f

    // En horizontal los clips se cuentan sobre el ALTO (largoJambaSegunDireccion).
    private val baseClips = altoVitro
    private val nClips = clipsEnteros(baseClips)
    private val residuo = baseClips - (clip * nClips)

    @Test
    fun clipsYResiduo() {
        assertEquals("60 / 9.6 entero = 6 clips", 6, nClips)
        assertEquals("sobra 2.4 cm", 2.4f, residuo, 0.01f)
    }

    @Test
    fun u13() {
        assertEquals("60", df1(anchoFijoVf))
        assertEquals("57", df1(med2 - (2 * uM)))
    }

    @Test
    fun jamba() {
        assertEquals("60", df1(baseClips))
    }

    @Test
    fun platina() {
        val nP = (((altoVitro / clip) - 0.1f) * clip) + 3.6
        val cuadre = if (altoVitro >= nP) 10 else 0
        assertEquals(50, ((nClips - 1) * 10) + cuadre)
    }

    @Test
    fun tope() {
        // La fórmula de "vf" descuenta 2, no 3.9 (que es la del modelo "v" suelto).
        assertEquals("58", df1(anchoVitro - 2f))
    }

    /** El alto del vidrio del fijo descuenta el U que se usa más la holgura de armado (0.3). */
    private fun descuentoAltoFijo(u: Float): Float = u + 0.3f

    @Test
    fun vidrioDelFijo() {
        assertEquals("59.6", df1(anchoFijoVf - 0.4f))
        assertEquals("58.2", df1(med2 - descuentoAltoFijo(uM)))
    }

    @Test
    fun elAltoDelFijoSigueAlPerfilU() {
        // Estaba escrito 1.8 fijo, que solo es correcto con u13 (1.5 + 0.3). Con otro U el
        // descuento tiene que moverse con él.
        assertEquals("u13 (1.5) -> 58.2", "58.2", df1(med2 - descuentoAltoFijo(1.5f)))
        assertEquals("u10 (1.0) -> 58.7", "58.7", df1(med2 - descuentoAltoFijo(1.0f)))
        assertEquals("u16 (1.6) -> 58.1", "58.1", df1(med2 - descuentoAltoFijo(1.6f)))
        assertEquals("u20 (2.0) -> 57.7", "57.7", df1(med2 - descuentoAltoFijo(2.0f)))
    }

    @Test
    fun vidrioDeLasLamas() {
        // El descuento se redondea ANTES de restar: df1(2.25 + 0.7) = "3.0" porque en coma flotante
        // la suma da 2.9500000476..., no 2.95 exacto. De ahí sale 57 y no 57.05.
        val descuento = df1(jArmado + 0.7f).toFloat()
        assertEquals("el descuento redondeado es 3.0", 3.0f, descuento, 0.0001f)

        val anchoV = anchoVitro - descuento
        assertEquals("57", df1(anchoV))

        // Con residuo <= 4.5 la última lama absorbe el sobrante y las demás van a 10.1.
        assertEquals("reparto por residuo chico", true, residuo <= 4.5f)
        assertEquals("11.3", df1(10.1f + residuo - 1.2f))
        assertEquals("lamas de 10.1", 5, nClips - 1)
    }

    @Test
    fun elCalculoPorModulosGenericoNoDebeUsarseEnLosModelos() {
        // Deja constancia de cuánto se desviaba, para que se note si alguien lo vuelve a conectar.
        assertEquals("55.5", df1(anchoVitro - (jArmado * 2f)))   // lamas: 1.55 cm menos que 57
        assertEquals("59.4", df1(anchoFijoVf - 0.6f))            // fijo: 0.2 cm menos que 59.6
        assertEquals("56.1", df1(anchoVitro - 3.9f))             // tope: 1.9 cm menos que 58
    }
}
