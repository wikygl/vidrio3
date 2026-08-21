package crystal.crystal

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.ObjectStreamClass

/**
 * Los presupuestos se guardan con serialización Java (`ObjectOutputStream`), que compara el
 * `serialVersionUID` al leer. Si cambia, **todos los presupuestos ya guardados dejan de abrirse**
 * con `InvalidClassException` — y eso pasa solo con agregar un campo a [Listado].
 *
 * Estos tests fijan ese número y comprueban que un archivo escrito con la forma ANTERIOR de la
 * clase (sin `productoId`) se siga leyendo.
 */
class ListadoSerializacionTest {

    /** El que tenía la clase antes de sumarle productoId. No se cambia. */
    private val UID_ORIGINAL = 5462438865809499361L

    private fun ejemplo() = Listado(
        escala = "m2", uni = "cm", medi1 = 120f, medi2 = 60f, medi3 = 0f,
        canti = 2f, piescua = 15.9f, precio = 50f, costo = 100f,
        producto = "Templado 8mm", peri = 3.6f, metcua = 1.44f,
        metli = 0f, metcub = 0f, color = 0, uri = ""
    )

    @Test
    fun elIdentificadorDeSerieNoDebeCambiarNunca() {
        assertEquals(
            "Cambió el serialVersionUID de Listado: los presupuestos guardados dejarían de abrirse. " +
                "Si agregaste un campo, dale valor por defecto y NO toques este número.",
            UID_ORIGINAL,
            ObjectStreamClass.lookup(Listado::class.java).serialVersionUID
        )
    }

    @Test
    fun unaListaSeEscribeYSeVuelveALeer() {
        val original = mutableListOf(ejemplo(), ejemplo().copy(producto = "Crudo 6mm"))
        val bytes = ByteArrayOutputStream().also { salida ->
            ObjectOutputStream(salida).use { it.writeObject(original) }
        }.toByteArray()

        @Suppress("UNCHECKED_CAST")
        val leida = ObjectInputStream(ByteArrayInputStream(bytes)).use {
            it.readObject() as MutableList<Listado>
        }
        assertEquals(2, leida.size)
        assertEquals("Templado 8mm", leida[0].producto)
        assertEquals(120f, leida[0].medi1, 0.001f)
        assertEquals("Crudo 6mm", leida[1].producto)
    }

    @Test
    fun elCampoNuevoViajaCuandoEstaPresente() {
        val conProducto = ejemplo().copy(productoId = "abc-123")
        val bytes = ByteArrayOutputStream().also { salida ->
            ObjectOutputStream(salida).use { it.writeObject(conProducto) }
        }.toByteArray()
        val leido = ObjectInputStream(ByteArrayInputStream(bytes)).use { it.readObject() as Listado }
        assertEquals("abc-123", leido.productoId)
    }

    /**
     * Un archivo viejo no trae `productoId`. Java rellena con null los campos que están en la clase
     * pero no en el flujo, siempre que el identificador de serie coincida — que es justo lo que
     * garantiza [elIdentificadorDeSerieNoDebeCambiarNunca].
     */
    @Test
    fun sinProductoIdQuedaEnNulo() {
        val bytes = ByteArrayOutputStream().also { salida ->
            ObjectOutputStream(salida).use { it.writeObject(ejemplo()) }
        }.toByteArray()
        val leido = ObjectInputStream(ByteArrayInputStream(bytes)).use { it.readObject() as Listado }
        assertNull("sin producto del catálogo, no se descuenta stock", leido.productoId)
    }
}
