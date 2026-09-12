package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El vano tal como se dibuja en la medida, convertido en tramos.
 *
 * El caso de verdad es la medida de la foto: 446.3 de ancho, dintel corrido, y el alféizar que
 * sube 53.8 en los 166 finales.
 */
class ContornoEnTramosTest {

    /** El contorno de la medida real, en cm y con la Y hacia abajo (0 es el dintel). */
    private val escalonada = listOf(
        0f to 0f,
        446.3f to 0f,
        446.3f to 106.2f,
        280.3f to 106.2f,
        280.3f to 160f,
        0f to 160f
    )

    private val recta = listOf(
        0f to 0f,
        300f to 0f,
        300f to 200f,
        0f to 200f
    )

    @Test
    fun `un vano recto da un solo tramo`() {
        val bandas = ContornoEnTramos.bandas(recta)
        assertEquals(1, bandas.size)
        assertEquals(300f, bandas[0].anchoCm, 0.05f)
        assertEquals(200f, bandas[0].altoCm, 0.05f)
    }

    @Test
    fun `el vano escalonado da dos tramos con sus medidas`() {
        val bandas = ContornoEnTramos.bandas(escalonada)
        assertEquals(2, bandas.size)
        assertEquals(280.3f, bandas[0].anchoCm, 0.05f)
        assertEquals(160f, bandas[0].altoCm, 0.05f)
        assertEquals(166f, bandas[1].anchoCm, 0.05f)
        assertEquals(106.2f, bandas[1].altoCm, 0.05f)
        // Y las dos suman el ancho del vano.
        assertEquals(446.3f, bandas.sumOf { it.anchoCm.toDouble() }.toFloat(), 0.05f)
    }

    @Test
    fun `el diseño sale con el dintel corrido y el escalón marcado`() {
        val d = ContornoEnTramos.disenoDesdeContorno(escalonada, acabado = "apa", altoHoja = 110f)
        assertNotNull(d)
        assertEquals(2, d!!.nTramos)
        assertEquals("el alto de la ventana no es el del tramo más alto", 160f, d.alto, 0.05f)
        assertEquals(160f, d.altoDeTramo(0), 0.05f)
        assertEquals(106.2f, d.altoDeTramo(1), 0.05f)
        assertTrue("no quedó marcada como escalonada", d.esEscalonada)
        // El tramo alto no lleva alto propio: cuelga del dintel y llega al suelo como la ventana.
        assertEquals(0f, d.tramos[0].alto, 0.01f)
        // Y cada tramo tiene módulos de verdad, repartidos con la regla de los 60.
        assertTrue(d.tramos[0].nModulosSistema >= 1)
        assertTrue(d.tramos[1].nModulosSistema >= 1)
    }

    @Test
    fun `el diseño del vano escalonado se escribe y se relee igual`() {
        val d = ContornoEnTramos.disenoDesdeContorno(escalonada)!!
        val ida = d.aPaquete()
        val vuelta = DisenoNova.desdePaquete(ida)!!
        assertEquals(ida, vuelta.aPaquete())
        assertEquals(106.2f, vuelta.altoDeTramo(1), 0.05f)
    }

    @Test
    fun `un contorno que no es un vano no da nada`() {
        assertTrue(ContornoEnTramos.bandas(listOf(0f to 0f, 10f to 0f)).isEmpty())
    }

    @Test
    fun `el escalón se lee igual dibujado al revés`() {
        // Mismo vano, recorrido en el otro sentido: las bandas salen iguales.
        val alReves = escalonada.reversed()
        val bandas = ContornoEnTramos.bandas(alReves)
        assertEquals(2, bandas.size)
        assertEquals(160f, bandas[0].altoCm, 0.05f)
        assertEquals(106.2f, bandas[1].altoCm, 0.05f)
    }

    @Test
    fun `el contorno va y vuelve como texto`() {
        val texto = ContornoEnTramos.aTexto(escalonada)
        assertTrue("no parece un contorno: $texto", texto.startsWith("0,0;446.3,0;"))
        val vuelta = ContornoEnTramos.desdeTexto(texto)
        assertEquals(escalonada.size, vuelta.size)
        val bandas = ContornoEnTramos.bandas(vuelta)
        assertEquals(2, bandas.size)
        assertEquals(106.2f, bandas[1].altoCm, 0.05f)
    }

    @Test
    fun `un texto roto no tumba nada`() {
        assertTrue(ContornoEnTramos.desdeTexto("").isEmpty())
        assertTrue(ContornoEnTramos.desdeTexto("cualquier cosa").isEmpty())
        // Un punto suelto mal escrito se descarta y los demás se leen.
        assertEquals(2, ContornoEnTramos.desdeTexto("0,0;mal;10,5").size)
    }

    /** El mismo vano pero con el escalón ARRIBA: el dintel baja 53.8 en los 166 finales. */
    private val escalonArriba = listOf(
        0f to 0f,
        280.3f to 0f,
        280.3f to 53.8f,
        446.3f to 53.8f,
        446.3f to 160f,
        0f to 160f
    )

    @Test
    fun `el escalón de arriba se lee como caída del dintel`() {
        val bandas = ContornoEnTramos.bandas(escalonArriba)
        assertEquals(2, bandas.size)
        assertEquals(280.3f, bandas[0].anchoCm, 0.05f)
        assertEquals(160f, bandas[0].altoCm, 0.05f)
        assertEquals(0f, bandas[0].caidaCm, 0.05f)
        assertEquals(166f, bandas[1].anchoCm, 0.05f)
        assertEquals("el tramo de abajo no mide lo que queda", 106.2f, bandas[1].altoCm, 0.05f)
        assertEquals("no se leyó lo que baja el dintel", 53.8f, bandas[1].caidaCm, 0.05f)
    }

    @Test
    fun `el diseño del escalón de arriba lleva la caída`() {
        val d = ContornoEnTramos.disenoDesdeContorno(escalonArriba)!!
        assertEquals(160f, d.alto, 0.05f)
        assertEquals(0f, d.caidaDeTramo(0), 0.05f)
        assertEquals(53.8f, d.caidaDeTramo(1), 0.05f)
        assertEquals(106.2f, d.altoDeTramo(1), 0.05f)
        assertTrue(d.esEscalonada)
        // Y sobrevive al paquete.
        val ida = d.aPaquete()
        assertTrue("falta la caída en el paquete: $ida", ida.contains("D<53.8>"))
        val vuelta = DisenoNova.desdePaquete(ida)!!
        assertEquals(53.8f, vuelta.caidaDeTramo(1), 0.05f)
        assertEquals(ida, vuelta.aPaquete())
    }

    @Test
    fun `un vano recortado por arriba y por abajo lleva las dos medidas`() {
        // El trozo del medio empieza 30 más abajo y acaba 20 más arriba.
        val doble = listOf(
            0f to 0f,
            300f to 0f,
            300f to 30f,
            500f to 30f,
            500f to 180f,
            300f to 180f,
            300f to 200f,
            0f to 200f
        )
        val bandas = ContornoEnTramos.bandas(doble)
        assertEquals(2, bandas.size)
        assertEquals(200f, bandas[0].altoCm, 0.05f)
        assertEquals(30f, bandas[1].caidaCm, 0.05f)
        assertEquals(150f, bandas[1].altoCm, 0.05f)
    }

    @Test
    fun `el escalón del costado es una banda más`() {
        // Vano con una muesca a la IZQUIERDA: el trozo de la izquierda arranca 40 más abajo.
        val muescaIzquierda = listOf(
            0f to 40f,
            120f to 40f,
            120f to 0f,
            400f to 0f,
            400f to 200f,
            0f to 200f
        )
        val bandas = ContornoEnTramos.bandas(muescaIzquierda)
        assertEquals(2, bandas.size)
        assertEquals(120f, bandas[0].anchoCm, 0.05f)
        assertEquals(40f, bandas[0].caidaCm, 0.05f)
        assertEquals(160f, bandas[0].altoCm, 0.05f)
        assertEquals(280f, bandas[1].anchoCm, 0.05f)
        assertEquals(0f, bandas[1].caidaCm, 0.05f)
        assertEquals(200f, bandas[1].altoCm, 0.05f)
    }

    @Test
    fun `un vano con escalones en los dos costados da tres tramos`() {
        // Alto en el medio, bajo a los dos lados: lo que en obra sale con una viga en el centro.
        val dosCostados = listOf(
            0f to 50f,
            100f to 50f,
            100f to 0f,
            300f to 0f,
            300f to 50f,
            400f to 50f,
            400f to 220f,
            0f to 220f
        )
        val bandas = ContornoEnTramos.bandas(dosCostados)
        assertEquals(3, bandas.size)
        assertEquals(50f, bandas[0].caidaCm, 0.05f)
        assertEquals(0f, bandas[1].caidaCm, 0.05f)
        assertEquals(50f, bandas[2].caidaCm, 0.05f)
        assertEquals(220f, bandas[1].altoCm, 0.05f)
        assertEquals(170f, bandas[0].altoCm, 0.05f)
        assertEquals(170f, bandas[2].altoCm, 0.05f)
    }

    // ==================== FORMAS POLIGONALES ====================
    // El vano con un lado inclinado: el dintel caído, el triángulo, el paralelogramo. El vidrio
    // sigue la forma, y cada tramo pasa a ser un cuadrilátero con sus dos lados.

    @Test
    fun `un dintel inclinado da un tramo con los dos lados distintos`() {
        // 300 de ancho; el dintel baja de 0 a 60 de izquierda a derecha, el alféizar es corrido.
        val dintelInclinado = listOf(
            0f to 0f,
            300f to 60f,
            300f to 200f,
            0f to 200f
        )
        val bandas = ContornoEnTramos.bandas(dintelInclinado)
        assertEquals(1, bandas.size)
        val b = bandas[0]
        assertTrue("no se leyó como inclinada", b.esInclinada)
        assertEquals(300f, b.anchoCm, 0.05f)
        // A la izquierda el vano mide los 200; a la derecha, 60 menos.
        assertEquals(200f, b.altoCm, 3f)
        assertEquals(140f, b.altoDerCm, 3f)
        assertEquals(0f, b.caidaCm, 3f)
        assertEquals(60f, b.caidaDerCm, 3f)
    }

    @Test
    fun `el tramo inclinado sobrevive al paquete`() {
        val d = ContornoEnTramos.disenoDesdeContorno(
            listOf(0f to 0f, 300f to 60f, 300f to 200f, 0f to 200f)
        )!!
        val ida = d.aPaquete()
        assertTrue("el paquete no lleva los dos lados: $ida", ida.contains("H<") && ida.contains(","))
        val vuelta = DisenoNova.desdePaquete(ida)!!
        assertEquals(ida, vuelta.aPaquete())
        assertTrue("al releer se perdió la inclinación", vuelta.tramos[0].esInclinado)
    }

    @Test
    fun `un triángulo punta abajo da dos tramos que bajan a cero`() {
        // Punta en el centro y abajo: los dos lados caen hacia el medio.
        val triangulo = listOf(
            0f to 0f,
            300f to 0f,
            150f to 200f
        )
        val bandas = ContornoEnTramos.bandas(triangulo)
        assertEquals(2, bandas.size)
        // La punta está abajo y en el centro, así que el vano es un pico en cada esquina de
        // arriba: el primer tramo crece hacia el centro y el segundo se cierra hacia la derecha.
        assertTrue("el primero no crece hacia el centro", bandas[0].altoDerCm > bandas[0].altoCm)
        assertTrue("el segundo no se cierra", bandas[1].altoCm > bandas[1].altoDerCm)
        // Y en el centro los dos valen lo mismo: es la punta.
        assertEquals(bandas[0].altoDerCm, bandas[1].altoCm, 10f)
    }

    @Test
    fun `partir un tramo inclinado corta a media pendiente`() {
        // Tramo con el dintel caído, cuatro módulos: al partirlo por la mitad, el corte queda a
        // media altura y cada trozo se lleva su parte.
        val inclinado = DisenoNova.desdePaquete(
            "{nova,apa,[300,200:Tl<300>(H<200,140>;D<0,60>;s<200>(f<75>c<75>c<75>f<75>))]}"
        )!!
        assertTrue(inclinado.tramos[0].esInclinado)
        val d = inclinado.conTramoPartido(0, despuesDelModulo = 1)
        assertEquals(2, d.nTramos)
        // El borde nuevo cae a mitad de la pendiente: 170 de alto y 30 de caída.
        assertEquals(170f, d.tramos[0].altoDerecho, 1f)
        assertEquals(30f, d.tramos[0].caidaDerecha, 1f)
        assertEquals(170f, d.tramos[1].alto, 1f)
        assertEquals(30f, d.tramos[1].caida, 1f)
        // Y los extremos siguen como estaban.
        assertEquals(200f, d.tramos[0].alto, 1f)
        assertEquals(140f, d.tramos[1].altoDerecho, 1f)
    }

    // La corrediza va en su rectángulo: donde el vano se cierra, la hoja no entra y van fijos.

    /** Triángulo invertido: base arriba, punta abajo en el centro. 200 de ancho por 160 de alto. */
    private val trianguloInvertido = listOf(0f to 0f, 200f to 0f, 100f to 160f)

    @Test
    fun `el triángulo invertido deja la corrediza en el rectángulo del medio`() {
        val d = ContornoEnTramos.disenoDesdeContorno(trianguloInvertido, altoHoja = 110f)!!
        // El vano se parte donde la hoja de 110 deja de entrar: las dos puntas y el medio.
        assertTrue("no se partió por donde la hoja deja de caber: ${d.aPaquete()}", d.nTramos >= 3)
        assertTrue("el vano se quedó sin corrediza: ${d.aPaquete()}", d.nCorredizas >= 1)
        assertEquals("la punta izquierda lleva corrediza", 0, d.tramos.first().sistema?.nCorredizas)
        assertEquals("la punta derecha lleva corrediza", 0, d.tramos.last().sistema?.nCorredizas)
        // Y el ancho del vano no se pierde al partirlo.
        assertEquals(200f, d.tramos.sumOf { it.ancho.toDouble() }.toFloat(), 0.5f)
    }

    @Test
    fun `ningún tramo del triángulo sale con una hoja de nada`() {
        val d = ContornoEnTramos.disenoDesdeContorno(trianguloInvertido, altoHoja = 110f)!!
        // La punta baja a cero, y con el alto del lado corto la franja de sistema salía de un palmo:
        // la calculadora tomaba esa como el alto de hoja de toda la ventana.
        d.tramos.forEachIndexed { i, t ->
            val alto = t.sistema?.alto ?: 0f
            assertTrue("el tramo $i trae una franja de sistema de $alto: ${d.aPaquete()}", alto >= 100f)
        }
    }

    @Test
    fun `el tramo del escalón conserva su hoja`() {
        // La medida de la foto: el trozo bajo mide 106.2 y la hoja pedida es 110. Ese tramo no se
        // convierte en un fijo: su hoja se acorta al alto del escalón, como siempre.
        val d = ContornoEnTramos.disenoDesdeContorno(escalonada, acabado = "apa", altoHoja = 110f)!!
        assertEquals(2, d.nTramos)
        assertTrue("el tramo alto perdió sus corredizas", (d.tramos[0].sistema?.nCorredizas ?: 0) >= 1)
        assertTrue("el escalón se quedó sin corrediza: ${d.aPaquete()}",
            (d.tramos[1].sistema?.nCorredizas ?: 0) >= 1)
    }

    @Test
    fun `la silueta del vano viaja con el diseño y sobrevive al paquete`() {
        val d = ContornoEnTramos.disenoDesdeContorno(trianguloInvertido, altoHoja = 110f)!!
        assertEquals("el diseño no se llevó la silueta", trianguloInvertido, d.contornoVano)
        // Y vuelve entera del paquete, con o sin los espacios que otros le quitan por el camino.
        val ida = d.aPaquete()
        assertEquals(trianguloInvertido, DisenoNova.desdePaquete(ida)!!.contornoVano)
        assertEquals(trianguloInvertido, DisenoNova.desdePaquete(ida.replace(" ", ""))!!.contornoVano)
    }

    @Test
    fun `la silueta también se saca de los tramos`() {
        // Los diseños hechos a mano traen la forma en los altos de sus tramos, sin etiqueta.
        val escalon = DisenoNova.desdePaquete(
            "{nova,apa,[446.3,160:Tl<280.3>(s<160>(f<280.3>)) P<2.5> Tl<166>(H<106.2>;s<106.2>(f<166>))]}"
        )!!
        val silueta = escalon.contornoDesdeTramos()
        // El dintel es corrido y el alféizar sube en el último tramo: seis vértices.
        assertEquals("la silueta no es la del escalón: $silueta", 6, silueta.size)
        assertEquals(0f to 0f, silueta.first())
        assertTrue("no aparece el salto del alféizar", silueta.any { it.second == 106.2f })
        assertEquals("el vano no llega a lo ancho", 446.3f, silueta.maxOf { it.first }, 0.05f)
        assertEquals("el vano no llega a lo alto", 160f, silueta.maxOf { it.second }, 0.05f)
    }

    @Test
    fun `un dintel que roza el límite no se parte`() {
        // 300 de ancho, el dintel baja 60: la hoja no entra por poco en el último palmo. Meter ahí
        // un parante y un tramo de nada no es lo que haría el vidriero: se acorta la hoja.
        val d = ContornoEnTramos.disenoDesdeContorno(
            listOf(0f to 0f, 300f to 60f, 300f to 200f, 0f to 200f)
        )!!
        assertEquals("se partió un vano que es un solo tramo: ${d.aPaquete()}", 1, d.nTramos)
        assertTrue("el tramo se quedó sin corredizas", d.nCorredizas >= 1)
    }
}
