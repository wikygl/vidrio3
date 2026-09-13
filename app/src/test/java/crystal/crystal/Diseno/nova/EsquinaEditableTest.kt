package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Una ventana de esquina se puede editar sin perder la esquina.
 *
 * El editor lee el paquete, lo cambia y lo vuelve a escribir. El pliegue (`A<90>`) y la panza de
 * la pared curva (`Q<29.3>`) tienen que sobrevivir a ese viaje: son lo que hace que la ventana sea
 * una L y no un rectángulo, y hasta ahora se perdían por el camino —los pliegues acababan todos
 * juntos al final del paquete, que es como decir en ninguna parte—.
 */
class EsquinaEditableTest {

    /** Una L: pared, pliegue, pared. */
    private val enL =
        "{nova,ina,[150,120:Tl<150>(H<120>;m<30>(f);s(fc)) A<90> Tl<120>(H<120>;m<30>(f);s(fc))]}"

    /** La misma L con la esquina redondeada: pared, pared curva, pliegue, pared. */
    private val enLConCurva =
        "{nova,ina,[150,120:Tl<150>(H<120>;m<30>(f);s(fc))" +
            " Tl<157.1>(H<120>;Q<29.3>;m<30>(f);s(fc))" +
            " A<90> Tl<120>(H<120>;m<30>(f);s(fc))]}"

    @Test
    fun el_pliegue_se_queda_entre_sus_dos_paredes() {
        val d = DisenoNova.desdePaquete(enL)!!
        assertEquals(2, d.tramos.size)
        assertNull("la primera pared no dobla delante de ella", d.tramos[0].pliegue)
        assertEquals("el pliegue no llegó a su pared", "A<90>", d.tramos[1].pliegue)

        // Y al reescribirlo vuelve a su sitio, entre las dos, no al final del paquete.
        val paquete = d.aPaquete()
        val posPliegue = paquete.indexOf("A<90>")
        assertTrue("el pliegue desapareció: $paquete", posPliegue > 0)
        assertTrue(
            "el pliegue se fue al final, fuera de las paredes: $paquete",
            posPliegue < paquete.lastIndexOf("Tl<")
        )
    }

    @Test
    fun editar_un_modulo_no_endereza_la_ventana() {
        val d = DisenoNova.desdePaquete(enL)!!
        // Lo que hace el vidriero en la pantalla: meterle un módulo a la segunda pared.
        val editado = d.conModuloAgregado(1, 1, 0, 'c')
        val vuelta = DisenoNova.desdePaquete(editado.aPaquete())!!
        assertEquals("se perdió una pared", 2, vuelta.tramos.size)
        assertEquals("la ventana se enderezó al editarla", "A<90>", vuelta.tramos[1].pliegue)
        assertEquals(
            "el módulo no entró",
            d.tramos[1].nModulosSistema + 1, vuelta.tramos[1].nModulosSistema
        )
    }

    @Test
    fun la_pared_curva_conserva_su_panza() {
        val d = DisenoNova.desdePaquete(enLConCurva)!!
        assertEquals(3, d.tramos.size)
        assertEquals("la pared recta salió curva", 0f, d.tramos[0].flecha, 0.01f)
        assertEquals("la curva perdió su panza", 29.3f, d.tramos[1].flecha, 0.01f)
        assertNull("la pared entra en la curva, no dobla contra ella", d.tramos[1].pliegue)
        assertEquals("detrás de la curva la pared va girada", "A<90>", d.tramos[2].pliegue)

        val vuelta = DisenoNova.desdePaquete(d.aPaquete())!!
        assertEquals(29.3f, vuelta.tramos[1].flecha, 0.01f)
        assertEquals("A<90>", vuelta.tramos[2].pliegue)
    }

    /**
     * Lo que pidió el vidriero: que la curva sea un vidrio fijo entero, de piso a techo.
     *
     * Es la edición más común en una esquina redondeada, y tiene que poder hacerse sin que la
     * ventana deje de ser una L con su curva.
     */
    @Test
    fun la_curva_puede_quedarse_en_un_fijo_de_piso_a_techo() {
        val d = DisenoNova.desdePaquete(enLConCurva)!!
        val soloFijo = NovaTramo(
            ancho = d.tramos[1].ancho,
            franjas = listOf(NovaFranja(esSistema = true, alto = 0f, modulos = listOf(NovaModulo('f')))),
            alto = d.tramos[1].alto,
            pliegue = d.tramos[1].pliegue,
            flecha = d.tramos[1].flecha
        )
        val editado = d.copy(tramos = d.tramos.toMutableList().also { it[1] = soloFijo })

        val vuelta = DisenoNova.desdePaquete(editado.aPaquete())!!
        assertEquals(3, vuelta.tramos.size)
        assertEquals("la curva se quedó sin su panza", 29.3f, vuelta.tramos[1].flecha, 0.01f)
        assertEquals("no quedó un solo módulo", 1, vuelta.tramos[1].nModulosSistema)
        assertEquals("y tiene que ser fijo", 1, vuelta.tramos[1].sistema?.nFijos)
        assertEquals("se quedó sin mocheta, que era la idea", 1, vuelta.tramos[1].franjas.size)
        assertEquals("las paredes rectas cambiaron", 2, vuelta.tramos[0].nModulosSistema)
        assertEquals("la ventana dejó de doblar", "A<90>", vuelta.tramos[2].pliegue)
    }

    /**
     * Editar lado por lado: cada uno sale de frente, se edita como una ventana normal y al
     * juntarlos vuelve a ser la esquina.
     *
     * Es la manera de que el editor no tenga que acordarse de la esquina en cada uno de los
     * caminos por los que reescribe el paquete: mientras se edita, no hay esquina que perder.
     */
    @Test
    fun la_ventana_se_separa_en_lados_y_se_vuelve_a_juntar() {
        val d = DisenoNova.desdePaquete(enLConCurva)!!
        assertTrue(d.doblaEnEsquina)

        val lados = d.separarEnLados()
        assertEquals("no salieron los tres lados", 3, lados.size)
        assertEquals("el primer lado no mide lo suyo", 150f, lados[0].ancho, 0.5f)
        assertEquals("la curva no mide su desarrollo", 157.1f, lados[1].ancho, 0.5f)
        assertEquals("el último lado no mide lo suyo", 120f, lados[2].ancho, 0.5f)
        // Cada lado sale de frente: ni pliegue ni panza, para que el editor lo trate como a
        // cualquier ventana.
        lados.forEach { lado ->
            assertTrue("un lado salió doblado", !lado.doblaEnEsquina)
            assertEquals("un lado salió curvo", 0f, lado.tramos[0].flecha, 0.01f)
        }

        // Y al juntarlos vuelve la esquina, con el ancho del primer lado como el de la ventana.
        val esquina = d.esquinaDeCadaLado()
        val vuelta = d.conLados(lados, esquina)
        assertEquals(3, vuelta.tramos.size)
        assertEquals("la curva perdió su panza al juntar", 29.3f, vuelta.tramos[1].flecha, 0.01f)
        assertEquals("la ventana dejó de doblar", "A<90>", vuelta.tramos[2].pliegue)
        assertEquals("el ancho no es el del primer lado", 150f, vuelta.ancho, 0.5f)
        assertEquals("el paquete no volvió a ser el mismo", d.aPaquete(), vuelta.aPaquete())
    }

    /** Y editando un lado, lo editado vuelve y la esquina sigue en su sitio. */
    @Test
    fun lo_editado_en_un_lado_vuelve_con_la_esquina_puesta() {
        val d = DisenoNova.desdePaquete(enLConCurva)!!
        val lados = d.separarEnLados().toMutableList()
        // El vidriero deja la curva en un fijo de piso a techo, con el lado de frente.
        lados[1] = lados[1].copy(
            tramos = listOf(
                lados[1].tramos[0].copy(
                    franjas = listOf(
                        NovaFranja(esSistema = true, alto = 0f, modulos = listOf(NovaModulo('f')))
                    )
                )
            )
        )
        val vuelta = d.conLados(lados, d.esquinaDeCadaLado())
        assertEquals("no quedó un solo módulo", 1, vuelta.tramos[1].nModulosSistema)
        assertEquals("y tiene que ser fijo", 1, vuelta.tramos[1].sistema?.nFijos)
        assertEquals("se quedó sin mocheta", 1, vuelta.tramos[1].franjas.size)
        assertEquals("la curva perdió su panza", 29.3f, vuelta.tramos[1].flecha, 0.01f)
        assertEquals("la ventana se enderezó", "A<90>", vuelta.tramos[2].pliegue)

        // Y el paquete que sale se vuelve a leer entero.
        val leida = DisenoNova.desdePaquete(vuelta.aPaquete())!!
        assertEquals(3, leida.tramos.size)
        assertEquals(29.3f, leida.tramos[1].flecha, 0.01f)
        assertEquals("A<90>", leida.tramos[2].pliegue)
    }

    /** Un lado partido en dos con un parante sigue siendo UN lado. */
    @Test
    fun partir_un_lado_no_lo_convierte_en_dos_lados() {
        val d = DisenoNova.desdePaquete(enL)!!
        val partida = d.conTramoPartido(0, 0)
        assertEquals("no se partió el tramo", 3, partida.tramos.size)
        // Tres tramos, pero dos lados: el pliegue sigue siendo uno.
        val lados = partida.separarEnLados()
        assertEquals("el parante se contó como esquina", 2, lados.size)
        assertEquals("el lado partido perdió un trozo", 2, lados[0].tramos.size)
        assertEquals(1, lados[1].tramos.size)

        val vuelta = partida.conLados(lados, partida.esquinaDeCadaLado())
        assertEquals(3, vuelta.tramos.size)
        assertEquals("el pliegue cambió de sitio", null, vuelta.tramos[1].pliegue)
        assertEquals("la ventana se enderezó", "A<90>", vuelta.tramos[2].pliegue)
    }

    /** Una ventana plana es un solo lado: separarla y juntarla la deja igual. */
    @Test
    fun una_ventana_plana_es_un_solo_lado() {
        val d = DisenoNova.nuevo("ina", 150f, 120f, 120f)
        assertTrue(!d.doblaEnEsquina)
        val lados = d.separarEnLados()
        assertEquals(1, lados.size)
        assertEquals(d.aPaquete(), d.conLados(lados, d.esquinaDeCadaLado()).aPaquete())
    }


    /**
     * Lo que Nova mira al volver del editor para NO tocar las medidas de los lados.
     *
     * El ancho de la cabecera de una esquina es el del primer lado, no el de la ventana: si Nova
     * lo escribe en el campo del ancho, el lado 2 sale con la medida del lado 1. Y los anchos de
     * dentro del diseño son los útiles, ya descontados; en los campos va lo que se midió.
     */
    @Test
    fun una_esquina_se_reconoce_por_su_pliegue() {
        val deEsquina = DisenoNova.desdePaquete(
            "{nova,ina,[147.5,160:Tl<147.5>(H<160>;s(fc)) Tl<90>(H<160>;Q<20>;s(f))" +
                " A<90> Tl<114>(H<160>;s(fc))]}"
        )!!
        assertTrue("no reconoce la ventana de esquina", deEsquina.doblaEnEsquina)

        val plana = DisenoNova.desdePaquete("{nova,ina,[240,160:Tl<240>(H<160>;s(fc))]}")!!
        assertTrue("una ventana plana no dobla", !plana.doblaEnEsquina)

        // Y una ventana de varios tramos sin pliegues tampoco: un parante no es una esquina.
        val tresTramos = DisenoNova.desdePaquete(
            "{nova,ina,[240,160:Tl<80>(s(f)) P<2.5> Tl<80>(s(f)) P<2.5> Tl<80>(s(f))]}"
        )!!
        assertTrue("un parante se tomó por una esquina", !tresTramos.doblaEnEsquina)
    }
    @Test
    fun un_diseno_normal_sigue_saliendo_igual() {
        // Sin pliegues ni panzas, el paquete se escribe como toda la vida: tramos separados por
        // P<2.5> y nada más.
        val d = DisenoNova.nuevo("ina", 150f, 120f, 120f)
        val paquete = d.aPaquete()
        assertTrue("apareció un pliegue de la nada: $paquete", !paquete.contains("A<"))
        assertTrue("apareció una panza de la nada: $paquete", !paquete.contains("Q<"))
        assertNotNull(DisenoNova.desdePaquete(paquete))
    }
}
