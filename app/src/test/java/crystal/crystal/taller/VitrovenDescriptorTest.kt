package crystal.crystal.taller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * El descriptor es lo que permite redibujar la vitroventana en la ficha de Producto sin guardar
 * imágenes. Si no sobrevive la ida y vuelta, la ventana se archiva sin diseño (que era el problema).
 */
class VitrovenDescriptorTest {

    private val datos = VitrovenDescriptor.Datos(
        ancho = 120f,
        alto = 210.5f,
        clips = 6,
        clasificacion = "fvf",
        simbolico = "{vitroven[120.0,210.5:f<30.0>v<60.0>f<30.0>]}",
        direccionVertical = false
    )

    @Test
    fun sobreviveLaIdaYVuelta() {
        val recuperado = VitrovenDescriptor.parsear(VitrovenDescriptor.serializar(datos))
            ?: error("no se pudo parsear lo que se acaba de serializar")
        assertEquals(datos.ancho, recuperado.ancho, 0.001f)
        assertEquals(datos.alto, recuperado.alto, 0.001f)
        assertEquals(datos.clips, recuperado.clips)
        assertEquals(datos.clasificacion, recuperado.clasificacion)
        assertEquals(datos.simbolico, recuperado.simbolico)
        assertEquals(datos.direccionVertical, recuperado.direccionVertical)
    }

    @Test
    fun conservaLaDireccionVertical() {
        val vertical = datos.copy(direccionVertical = true)
        val recuperado = VitrovenDescriptor.parsear(VitrovenDescriptor.serializar(vertical))
        assertEquals(true, recuperado?.direccionVertical)
    }

    @Test
    fun elSimbolicoNoSeCortaAunqueTraigaSeparadores() {
        // El '|' es el separador del formato: si apareciera en el símbolico partiría el descriptor.
        val conBarra = datos.copy(simbolico = "{vitroven[1,2:v<1>|f<2>]}")
        val recuperado = VitrovenDescriptor.parsear(VitrovenDescriptor.serializar(conBarra))
            ?: error("no se pudo parsear")
        assertEquals("{vitroven[1,2:v<1> f<2>]}", recuperado.simbolico)
        assertEquals(conBarra.ancho, recuperado.ancho, 0.001f)
    }

    @Test
    fun untextoInvalidoNoRompe() {
        assertNull(VitrovenDescriptor.parsear(""))
        assertNull(VitrovenDescriptor.parsear("cualquier cosa"))
        assertNull(VitrovenDescriptor.parsear("120|210|no-es-numero|fvf|0|{}"))
    }
}
