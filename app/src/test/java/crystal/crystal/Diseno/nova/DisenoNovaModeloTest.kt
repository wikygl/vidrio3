package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El modelo tiene que leer y volver a escribir los paquetes REALES sin perder nada. Los dos
 * primeros son ventanas de verdad, tal como salieron de la app.
 */
class DisenoNovaModeloTest {

    // Ventana de Jorge: 385.9 x 246.8, aparente, doble puente (mocheta arriba y abajo).
    private val jorge =
        "{nova,apa,[385.9,246.8:Tl<219>(m<48.4>(f<109.5>f<109.5>);s<150>(f<54.7>c<54.7>c<54.7>f<54.7>);m<48.4>(f<109.5>f<109.5>))" +
            " P<2.5> Tl<164.3>(m<48.4>(f<164.3>);s<150>(f<54.7>c<54.7>f<54.7>);m<48.4>(f<164.3>))]}"

    // Ventana de Abel: 650 x 160, inaparente, remate normal, tres tramos [4,3,4].
    private val abel =
        "{nova,ina,[650,160:Tl<234.5>(s<114.2>(f<58.6>c<58.6>c<58.6>f<58.6>);m<43.3>(f<117.2>f<117.2>))" +
            " P<2.5> Tl<175.9>(s<114.2>(f<58.6>c<58.6>f<58.6>);m<43.3>(f<175.9>))" +
            " P<2.5> Tl<234.5>(s<114.2>(f<58.6>c<58.6>c<58.6>f<58.6>);m<43.3>(f<117.2>f<117.2>))]}"

    @Test
    fun `lee la ventana de dos tramos con doble puente`() {
        val d = DisenoNova.desdePaquete(jorge)
        assertNotNull(d); d!!
        assertEquals("apa", d.acabado)
        assertEquals(385.9f, d.ancho, 0.01f)
        assertEquals(246.8f, d.alto, 0.01f)
        assertEquals(2, d.nTramos)
        // Doble puente: mocheta, sistema y mocheta en cada tramo.
        assertEquals(3, d.tramos[0].franjas.size)
        assertEquals(2, d.tramos[0].mochetas.size)
        assertEquals(4, d.tramos[0].nModulosSistema)
        assertEquals(3, d.tramos[1].nModulosSistema)
        // El sistema del primer tramo es f c c f.
        assertEquals(listOf('f', 'c', 'c', 'f'), d.tramos[0].sistema!!.modulos.map { it.tipo })
        assertEquals(7, d.nModulos)
        assertEquals(4, d.nFijos)
        assertEquals(3, d.nCorredizas)
    }

    @Test
    fun `lee la ventana de tres tramos`() {
        val d = DisenoNova.desdePaquete(abel)
        assertNotNull(d); d!!
        assertEquals("ina", d.acabado)
        assertEquals(3, d.nTramos)
        assertEquals(11, d.nModulos)
        assertEquals(listOf(4, 3, 4), d.tramos.map { it.nModulosSistema })
        assertEquals(2, d.nParantes)
        // El ancho útil descuenta los dos parantes.
        assertEquals(645f, d.anchoUtil(), 0.01f)
        // La mocheta del tramo del medio es un solo paño de todo el ancho del tramo.
        assertEquals(1, d.tramos[1].mochetas[0].modulos.size)
    }

    @Test
    fun `lo que lee lo vuelve a escribir igual`() {
        for (paquete in listOf(jorge, abel)) {
            val d = DisenoNova.desdePaquete(paquete)!!
            val ida = d.aPaquete()
            val vuelta = DisenoNova.desdePaquete(ida)
            assertNotNull("no se pudo releer:\n$ida", vuelta)
            // Comparar el modelo, no el texto: los espacios pueden variar.
            assertEquals(d, vuelta)
        }
    }

    @Test
    fun `el pliegue se queda con la pared que dobla`() {
        val conGiro = "{nova,apa,[300,200:Tl<148.7>(s<150>(f<74.3>c<74.3>)) A<90> Tl<148.7>(s<150>(f<74.3>c<74.3>))]}"
        val d = DisenoNova.desdePaquete(conGiro)
        assertNotNull(d); d!!
        assertEquals(2, d.nTramos)
        // El pliegue NO es una etiqueta del conjunto: es un sitio, dice entre qué dos paredes
        // dobla la ventana. Guardado en la lista suelta se reescribía al final del paquete, o
        // sea en ninguna parte, y la ventana en L salía rectangular en cuanto se editaba.
        assertEquals(emptyList<String>(), d.etiquetas)
        assertEquals("A<90>", d.tramos[1].pliegue)
    }

    @Test
    fun `un tramo suelto sin bloque T tambien se lee`() {
        val simple = "{nova,ina,[120,150:Tl<120>(s<100>(f<60>c<60>);m<50>(f<120>))]}"
        val d = DisenoNova.desdePaquete(simple)
        assertNotNull(d); d!!
        assertEquals(1, d.nTramos)
        assertEquals(0, d.nParantes)
        assertEquals(120f, d.anchoUtil(), 0.01f)
    }

    @Test
    fun `lo que no es un paquete de nova devuelve null`() {
        assertNull(DisenoNova.desdePaquete(""))
        assertNull(DisenoNova.desdePaquete("cualquier cosa"))
        assertNull(DisenoNova.desdePaquete("{nova,apa,[sin dos puntos]}"))
    }

    @Test
    fun `los modulos sin ancho se admiten`() {
        val sinAnchos = "{nova,apa,[240,200:Tl<240>(s<150>(fccf))]}"
        val d = DisenoNova.desdePaquete(sinAnchos)
        assertNotNull(d); d!!
        assertEquals(4, d.nModulos)
        assertTrue(d.tramos[0].sistema!!.modulos.all { it.ancho == null })
        // Y al escribirlo salen sin medida, no con cero.
        assertTrue(d.aPaquete().contains("(fccf)"))
    }

    /**
     * La pared de una esquina que no es un rectángulo viaja con su silueta en su tramo (`L<…>`),
     * relativa a esa pared, y sobrevive a la ida y vuelta, al reparto de anchos y a partir la
     * pared con un parante (se queda en el primer trozo).
     */
    @Test
    fun la_silueta_de_una_pared_viaja_en_su_tramo() {
        val l = "{nova,apa,[280,220: Tl<280>(L<0/60|145/0|280/40|280/220|0/220>;s(f c c)) A<90> Tl<64.5>(s(f))]}"
        val d = DisenoNova.desdePaquete(l)
        assertNotNull(d)
        assertEquals(2, d!!.tramos.size)
        assertEquals(5, d.tramos[0].contorno.size)
        assertEquals(145f to 0f, d.tramos[0].contorno[1])
        assertTrue("la pared recta no lleva silueta", d.tramos[1].contorno.isEmpty())
        assertTrue(d.doblaEnEsquina)

        val ida = d.aPaquete()
        assertTrue("el tag no se escribe: $ida", ida.contains("L<0/60|145/0|280/40|280/220|0/220>"))
        assertEquals(d, DisenoNova.desdePaquete(ida))

        val repartido = d.conAnchosRepartidos()
        assertEquals(5, repartido.tramos[0].contorno.size)
        val partido = d.conTramoPartido(0, 1)
        assertEquals(3, partido.tramos.size)
        assertEquals(5, partido.tramos[0].contorno.size)
        assertTrue("el segundo trozo no lleva la silueta", partido.tramos[1].contorno.isEmpty())
        assertEquals(5, partido.conTramosUnidos(0).tramos[0].contorno.size)
    }

    /**
     * Lo que trae el apunte de una esquina armada sobre figuras se pone por lado: la silueta en
     * el primer tramo del lado y los parantes en la frontera de hojas más cercana a donde se
     * marcaron. En una ventana de 280 con tres hojas de 93.3, un parante marcado a 100 cae
     * entre la primera y la segunda.
     */
    @Test
    fun lo_medido_de_cada_pared_se_pone_en_su_lado() {
        val l = "{nova,apa,[280,220: Tl<280>(s(f c c)) A<90> Tl<64.5>(s(f))]}"
        val d = DisenoNova.desdePaquete(l)!!
        val silueta = listOf(0f to 60f, 145f to 0f, 280f to 40f, 280f to 220f, 0f to 220f)
        val con = d.conSiluetasYParantesPorLado(listOf(silueta, null), listOf(listOf(100f), emptyList()))
        assertEquals(silueta, con.tramos[0].contorno)
        assertTrue(con.tramos[1].contorno.isEmpty())
        assertEquals(listOf(0), con.tramos[0].sistema!!.parantes)
        assertTrue(con.tramos[1].sistema!!.parantes.isEmpty())
        assertTrue("el parante no se escribe: ${con.aPaquete()}", con.aPaquete().contains("f;P;cc"))
        assertEquals(con, DisenoNova.desdePaquete(con.aPaquete()))
        // Sin nada que poner, el diseño no se toca.
        assertEquals(d, d.conSiluetasYParantesPorLado(listOf(null, null), listOf(emptyList(), emptyList())))
    }

    /**
     * Cada pared con su alto: la de al lado, más baja, cuelga más abajo del dintel y mide lo suyo;
     * la ventana mide lo que la pared más alta. El 3D sube cada una hasta su alto.
     */
    @Test
    fun cada_lado_con_su_alto() {
        val l = "{nova,apa,[280,221.9: Tl<280>(s(f c c)) A<90> Tl<64.5>(s(f))]}"
        val d = DisenoNova.desdePaquete(l)!!
        val con = d.conAltosDeLado(listOf(221.9f, 162f))
        assertEquals(221.9f, con.alto, 0.01f)
        assertEquals("el primer lado no cambia", 0f, con.tramos[0].alto, 0.01f)
        assertEquals(162f, con.tramos[1].alto, 0.01f)
        assertEquals("cuelga lo que le falta", 59.9f, con.tramos[1].caida, 0.01f)
        val planta = PlantaDelDiseno.de(con)
        assertEquals(listOf(221.9f, 162f), planta.paredes.map { it.altoCm })
        // Si la segunda es la alta, la ventana crece hasta ella y la primera es la que cuelga.
        val alReves = d.conAltosDeLado(listOf(221.9f, 250f))
        assertEquals(250f, alReves.alto, 0.01f)
        assertEquals(221.9f, alReves.tramos[0].alto, 0.01f)
        assertEquals(0f, alReves.tramos[1].alto, 0.01f)
        // Iguales: no se toca nada.
        assertEquals(d, d.conAltosDeLado(listOf(221.9f, 221.9f)))
    }

    // ---------- Pedido del usuario (2026-09-26): el trapecio de 208.6 × 156.8 ----------

    /** Un tramo con el sistema abajo y la mocheta arriba, sin alturas escritas (tras Limpiar y +). */
    private fun trapecioSinAlturas() = DisenoNova(
        "apa", 208.6f, 156.8f,
        listOf(NovaTramo(208.6f, listOf(
            NovaFranja(true, 0f, listOf(NovaModulo('f', 69.5f), NovaModulo('c', 69.5f), NovaModulo('f', 69.6f))),
            NovaFranja(false, 0f, listOf(NovaModulo('f', 104.3f), NovaModulo('f', 104.3f)))
        )))
    ).conContornoVano(listOf(0f to 0f, 208.6f to 0f, 208.6f to 156.8f, 75.4f to 156.8f))

    @Test
    fun sin_alturas_escritas_el_sistema_es_la_hoja_de_la_casa() {
        // N-29: 5/7 del alto (112) y la mocheta el resto (44.8), como lo dibuja la vista. Antes se
        // repartía por igual y Nova no encontraba la hoja: la ventana le llegaba sin puente.
        val d = trapecioSinAlturas()
        val a = HerramientasDiseno.alturasDeFranjas(d, 0)
        assertEquals(112f, a[0], 0.05f)
        assertEquals(44.8f, a[1], 0.05f)
        // Y se escriben en el paquete, que es lo que lee Nova.
        val p = d.aPaquete()
        assertTrue(p, p.contains("s<112>") && p.contains("m<44.8>"))
        // Con la mocheta escrita, el sistema es lo que queda (no 5/7).
        val conMocheta = d.copy(tramos = listOf(d.tramos[0].copy(franjas = listOf(
            d.tramos[0].franjas[0], d.tramos[0].franjas[1].copy(alto = 40f)))))
        assertEquals(116.8f, HerramientasDiseno.alturasDeFranjas(conMocheta, 0)[0], 0.05f)
    }

    @Test
    fun agregar_un_modulo_en_una_franja_no_mueve_los_anchos_de_la_otra() {
        // El vidriero escribió 50 en el primer módulo del sistema; agrega un paño en la mocheta: el
        // sistema conserva sus anchos. Antes volvían a repartirse por igual.
        val base = trapecioSinAlturas()
        val t = base.tramos[0]
        val editado = base.copy(tramos = listOf(t.copy(franjas = listOf(
            t.franjas[0].copy(modulos = listOf(NovaModulo('f', 50f), NovaModulo('c', 79.3f), NovaModulo('f', 79.3f))),
            t.franjas[1]
        ))))
        val d = editado.conModuloAgregado(0, 1, 1, 'f')
        assertEquals(3, d.tramos[0].franjas[1].modulos.size)
        assertEquals(listOf(50f, 79.3f, 79.3f), d.tramos[0].franjas[0].modulos.map { Math.round(it.ancho!! * 10) / 10f })
        // La mocheta sí se reparte: tres paños iguales.
        d.tramos[0].franjas[1].modulos.forEach { assertEquals(208.6f / 3f, it.ancho!!, 0.05f) }
    }

    @Test
    fun con_lado_inclinado_los_modulos_se_reparten_lo_que_deja_el_vano() {
        // Vna4 (dibujo del usuario, 2026-09-26): el sistema mide 187.1 en su borde de arriba y cada
        // módulo 62.4; lo que queda fuera del vano (21.5) va al fijo inclinado, que abajo mide 8.5 (el dibujo, con 62.4 redondeado, dice 8.4).
        // Antes se repartía el ancho del tramo: 69.5 cada uno, y el parante caía fuera del alféizar.
        val d = trapecioSinAlturas().conAnchosRepartidos()
        val sistema = d.tramos[0].franjas[0].modulos.map { it.ancho!! }
        assertEquals(21.54f + 62.35f, sistema[0], 0.05f)
        assertEquals(62.35f, sistema[1], 0.05f)
        assertEquals(62.35f, sistema[2], 0.05f)
        assertEquals(62.35f, HerramientasDiseno.geometriaDeModulos(d, 0, 0)[0].contenido, 0.05f)
        assertEquals(133.2f - 2 * 62.35f, HerramientasDiseno.geometriaDeModulos(d, 0, 0)[0].abajo, 0.1f)
        // La mocheta llega de lado a lado: sigue en dos paños iguales.
        d.tramos[0].franjas[1].modulos.forEach { assertEquals(104.3f, it.ancho!!, 0.05f) }
    }

    @Test
    fun el_ancho_escrito_de_un_modulo_es_lo_que_mide_dentro_del_vano() {
        // El diálogo decía 55.9 y 75.2 de módulos de 62.4: achicaba en proporción a la franja.
        val d = trapecioSinAlturas().conAnchosRepartidos()
        val (hueco, fuera) = HerramientasDiseno.huecoDeFranja(d, 0, 0)!!
        assertEquals(187.06f, hueco, 0.05f)
        assertEquals(21.54f, fuera[0], 0.05f)
        // El fijo de la derecha pasa a 50: los otros dos se reparten 137.1 del hueco y el fijo
        // inclinado conserva lo que tiene fuera del vano.
        val e = HerramientasDiseno.conAnchoDeModuloEnElVano(d, 0, 0, 2, 50f)!!
        val geo = HerramientasDiseno.geometriaDeModulos(e, 0, 0)
        assertEquals(50f, geo[2].contenido, 0.05f)
        assertEquals(68.53f, geo[1].contenido, 0.05f)
        assertEquals(68.53f, geo[0].contenido, 0.05f)
        assertEquals(208.6f, e.tramos[0].franjas[0].modulos.sumOf { it.ancho!!.toDouble() }.toFloat(), 0.05f)
    }

    @Test
    fun al_editar_otro_modulo_el_ya_escrito_conserva_su_ancho() {
        // Pedido del usuario (2026-09-26): primero el fijo de la derecha a 50, después el del medio
        // a 60. El de la derecha sigue en 50; solo el no editado (el fijo inclinado) absorbe.
        val d = trapecioSinAlturas().conAnchosRepartidos()
        val e = HerramientasDiseno.conAnchoDeModuloEnElVano(d, 0, 0, 2, 50f)!!
        val f = HerramientasDiseno.conAnchoDeModuloEnElVano(e, 0, 0, 1, 60f, editados = setOf(2))!!
        val geo = HerramientasDiseno.geometriaDeModulos(f, 0, 0)
        assertEquals(50f, geo[2].contenido, 0.05f)
        assertEquals(60f, geo[1].contenido, 0.05f)
        assertEquals(187.06f - 110f, geo[0].contenido, 0.05f)
        // Si ya no queda sitio para el que falta, no se aplica.
        assertEquals(null, HerramientasDiseno.conAnchoDeModuloEnElVano(e, 0, 0, 1, 140f, editados = setOf(2)))
    }

    @Test
    fun el_circulo_sobrevive_al_cambiar_una_medida() {
        // Pedido del usuario (2026-09-27): al cambiar el alto del sistema el vano circular volvía
        // rectángulo, porque la marca `O<1>` va dentro del sistema y los módulos no la leían.
        val nci = "{nova,apa,[200,180:Tl<200>(s<128.6>(f<50>c<50>c<50>f<50>O<1>);m<51.4>(f<100>f<100>))]}"
        val d = DisenoNova.desdePaquete(nci)!!
        assertTrue(d.etiquetas.contains("O<1>"))
        val e = d.conAlturaDeFranja(0, 0, 120f)
        val otra = DisenoNova.desdePaquete(e.aPaquete())!!
        assertEquals(1, otra.etiquetas.count { it == "O<1>" })
    }

    @Test
    fun la_silueta_del_vano_de_cada_forma() {
        // La vista 3D recorta las paredes con ella (pedido del usuario, 2026-09-27).
        val elipse = DisenoNova.desdePaquete(
            "{nova,apa,[200,180:Tl<200>(s<128.6>(f<50>c<50>c<50>f<50>O<1>);m<51.4>(f<100>f<100>))]}"
        )!!.siluetaDelVano()
        assertEquals(48, elipse.size)
        assertEquals(0f, elipse.minOf { it.first }, 0.01f)
        assertEquals(200f, elipse.maxOf { it.first }, 0.01f)
        assertEquals(0f, elipse.minOf { it.second }, 0.01f)
        assertEquals(180f, elipse.maxOf { it.second }, 0.01f)

        val triangulo = DisenoNova.desdePaquete("{nova,apa,[200,160:Tl<200>(s(f)) V<0/0|200/0|100/160>]}")!!
        assertEquals(listOf(0f to 0f, 200f to 0f, 100f to 160f), triangulo.siluetaDelVano())

        val escalon = DisenoNova.desdePaquete("{nova,apa,[300,180:Tl<150>(s(fc)) P<2.5> Tl<147.5>(H<120>;s(fc))]}")!!
        assertTrue(escalon.siluetaDelVano().size >= 5)

        val rectangulo = DisenoNova.desdePaquete("{nova,apa,[200,180:Tl<200>(s(fccf))]}")!!
        assertTrue(rectangulo.siluetaDelVano().isEmpty())
    }

    @Test
    fun la_mocheta_nueva_en_un_sistema_que_llena_el_tramo_tiene_su_alto() {
        // Pedido del usuario (2026-09-27): en un escalonado el sistema viene con el alto entero del
        // tramo, la mocheta nueva salía de 0 y el puente no se veía. La hoja pasa a 5/7 (N-29).
        val d = DisenoNova.desdePaquete(
            "{nova,apa,[446.3,160:Tl<280.3>(s<160>(f<140.1>c<140.1>)) P<2.5> Tl<166>(H<106.2>;s<106.2>(f<83>c<83>))]}"
        )!!
        val bajo = d.conFranjaAgregadaEnTramo(1).tramos[1].franjas
        assertEquals(106.2f * 5f / 7f, bajo.first { it.esSistema }.alto, 0.05f)
        assertEquals(106.2f * 2f / 7f, bajo.first { !it.esSistema }.alto, 0.05f)
        val alto = d.conFranjaAgregadaEnTramo(0).tramos[0].franjas
        assertEquals(160f * 5f / 7f, alto.first { it.esSistema }.alto, 0.05f)
        assertEquals(160f * 2f / 7f, alto.first { !it.esSistema }.alto, 0.05f)
        // Un sistema que ya deja sitio lo conserva.
        val conSitio = DisenoNova.desdePaquete("{nova,apa,[200,160:Tl<200>(s<120>(fc))]}")!!
            .conFranjaAgregadaEnTramo(0).tramos[0].franjas
        assertEquals(120f, conSitio.first { it.esSistema }.alto, 0.05f)
        assertEquals(40f, conSitio.first { !it.esSistema }.alto, 0.05f)
    }

    @Test
    fun crear_mochetas_sigue_n100() {
        // Dictado del usuario (2026-09-28), en un tramo de 160.
        fun alturas(d: DisenoNova) = HerramientasDiseno.alturasDeFranjas(d, 0).map { Math.round(it * 100) / 100f }
        val solo = DisenoNova.desdePaquete("{nova,apa,[200,160:Tl<200>(s(fccf))]}")!!

        // 2. La primera mocheta: sistema 5/7 y la mocheta el resto.
        val una = solo.conFranjaAgregadaEnTramo(0)
        assertEquals(listOf(114.29f, 45.71f), alturas(una))

        // 3. Otra antes de editar la anterior: el tramo a partes iguales (la pantalla lo sabe y lo pide).
        val dos = una.conFranjaAgregadaEnTramo(0, partesIguales = true)
        assertEquals(listOf(53.33f, 53.33f, 53.33f), alturas(dos))

        // 4. Con una altura editada antes de crear: lo editado se conserva y el cambio lo absorben
        //    las mochetas, aunque la mocheta haya sido editada.
        val mochetaEditada = DisenoNova.desdePaquete("{nova,apa,[200,160:Tl<200>(s(fccf);m<40>(ff))]}")!!
        assertEquals(listOf(120f, 20f, 20f), alturas(mochetaEditada.conFranjaAgregadaEnTramo(0)))
        val sistemaEditado = DisenoNova.desdePaquete("{nova,apa,[200,160:Tl<200>(s<100>(fccf);m<60>(ff))]}")!!
        assertEquals(listOf(100f, 30f, 30f), alturas(sistemaEditado.conFranjaAgregadaEnTramo(0)))

        // 1. La altura de puente que trae la medida se respeta.
        val conPuente = DisenoNova.desdePaquete("{nova,apa,[200,160:Tl<200>(s<110>(fccf))]}")!!
        assertEquals(listOf(110f, 50f), alturas(conPuente.conFranjaAgregadaEnTramo(0)))
    }

    @Test
    fun sin_alturas_escritas_dos_franjas_5_7_y_tres_a_partes_iguales() {
        // Una sola regla para el panel, el paquete, el motor, el dibujo y la 3D (N-100).
        assertEquals(listOf(100f, 40f), HerramientasDiseno.repartirAlto(listOf(0f, 0f), listOf(true, false), 140f))
        val tres = HerramientasDiseno.repartirAlto(listOf(0f, 0f, 0f), listOf(false, true, false), 141f)
        assertEquals(listOf(47f, 47f, 47f), tres)
    }
}
