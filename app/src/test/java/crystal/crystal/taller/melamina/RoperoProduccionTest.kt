package crystal.crystal.taller.melamina

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La producción del ropero: el canto como se escribe en la hoja de corte (O, C, U, L o medidas),
 * el código simbólico de cada pieza, el CSV con la estructura de la hoja de Excel, y las marcas y
 * agujeros de cada tablero (negro la cara de lo que llega, rosado el centro de su espesor).
 */
class RoperoProduccionTest {

    private val ropero = Ropero(anchoCm = 155f, altoCm = 230f, fondoCm = 60f, zocaloCm = 7f, colorExterior = "Cedro", vetaExterior = true)
        .conCuerposIguales(2)
        .let { it.conCuerpo(0, Cuerpo(tipo = TipoCuerpo.COLGAR)) }
        .let { it.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 4, altosCajonesCm = List(4) { 18f }, altoCajonesFijoCm = 80f, cajonesALaVista = true)) }

    private fun pieza(ancho: Int, alto: Int, enAncho: Int, enAlto: Int, canto: TipoCanto = TipoCanto.FINO, material: MaterialPlancha = MaterialPlancha.MELAMINA_18) =
        PiezaMelamina("Prueba", ancho, alto, 1, material, 0, enAncho, enAlto, canto)

    @Test
    fun el_canto_se_escribe_como_en_la_hoja() {
        // Todo el contorno: O. Dos cortos y un largo: C. Dos largos y un corto: U. Uno y uno: L.
        assertEquals("delgado en O", RoperoProduccion.textoDeCanto(ropero, pieza(300, 632, 2, 2)))
        assertEquals("delgado en C", RoperoProduccion.textoDeCanto(ropero, pieza(300, 632, 2, 1)))
        assertEquals("delgado en U", RoperoProduccion.textoDeCanto(ropero, pieza(300, 632, 1, 2)))
        assertEquals("delgado en L", RoperoProduccion.textoDeCanto(ropero, pieza(300, 632, 1, 1)))
        // En uno o dos lados, sus medidas.
        assertEquals("delgado en 57.9", RoperoProduccion.textoDeCanto(ropero, pieza(579, 2300, 1, 0)))
        assertEquals("delgado en 230, 230", RoperoProduccion.textoDeCanto(ropero, pieza(579, 2300, 0, 2)))
        // El grueso de fuera, del color de la melamina de fuera: no se repite el color.
        assertEquals("grueso en O", RoperoProduccion.textoDeCanto(ropero, pieza(379, 2230, 2, 2, TipoCanto.GRUESO, MaterialPlancha.MELAMINA_18_COLOR)))
        // Si el tapacanto es de otro color, se dice.
        assertEquals("grueso Negro en O", RoperoProduccion.textoDeCanto(ropero.copy(colorTapacanto = "Negro"), pieza(379, 2230, 2, 2, TipoCanto.GRUESO, MaterialPlancha.MELAMINA_18_COLOR)))
    }

    @Test
    fun el_codigo_simbolico_de_una_pieza() {
        // 1 pieza de 30 x 63.2 de cedro con veta, grueso cedro en U (un 30 y los dos 63.2).
        val p = pieza(300, 632, 1, 2, TipoCanto.GRUESO, MaterialPlancha.MELAMINA_18_COLOR)
        assertEquals("1,30,63.2,v63.2,g30cedro,g63.2cedro,g63.2cedro", RoperoProduccion.codigoSimbolico(ropero, p))
        // El blanco no lleva veta; la ranura del cajón va al final, a su distancia y a lo largo de…
        val caja = PiezaMelamina("Lateral cajón", 529, 180, 2, MaterialPlancha.MELAMINA_18, 0, 1, 0, TipoCanto.FINO, 1.8f)
        assertEquals("2,52.9,18,d52.9blanco,r1.8:52.9", RoperoProduccion.codigoSimbolico(ropero, caja))
    }

    @Test
    fun la_hoja_de_corte_en_csv_con_la_estructura_del_excel() {
        val csv = RoperoProduccion.csv(listOf(RoperoProduccion.Mueble("Rm4", ropero)))
        val lineas = csv.lines()
        assertEquals("melamina Blanco 18mm", lineas[0])
        assertEquals("cantidad,ancho,alto,vetas,canto,ranura,router,etiqueta,pieza,codigo", lineas[1])
        assertTrue(csv.contains("melamina Cedro 18mm"))
        // La puerta de cedro: veta a lo alto y grueso en O; el lateral con canto de color en L.
        assertTrue(lineas.any { it.startsWith("2,37.9,223,223,grueso en O,,,Rm4 · cuerpo 1 · Puerta,Puerta,") })
        // La ranura de la caja, a 1.8 y a lo largo, "cajonería"; va entre comillas por la coma.
        assertTrue(lineas.any { it.contains("\"1.8 en 52.9, cajonería\"") })
    }

    @Test
    fun las_marcas_del_lateral_son_la_cara_de_lo_que_llega_y_el_agujero_su_centro() {
        val lateral = RoperoProduccion.tableros(ropero).first { it.nombre == "Lateral izquierdo" }
        val uniones = lateral.marcas.filter { it.tipo == RoperoProduccion.TipoMarca.UNION }
        // El piso (zócalo 7: de 5.2 a 7): marca 5.2 y agujero 6.1, como en el plano del taller.
        val piso = uniones.first { it.nota == "Piso" }
        assertEquals(5.2f, piso.a, 0.01f)
        assertEquals(6.1f, piso.agujero!!, 0.01f)
        assertEquals("cara derecha", piso.cara)
        // El techo, de 228.2 a 230: agujero en 229.1.
        assertEquals(229.1f, uniones.first { it.nota == "Techo" }.agujero!!, 0.01f)
        // A lo hondo (57.9, más de 40): tres, a 5 de cada canto y al centro.
        assertEquals(listOf(5f, 28.95f, 52.9f), piso.hondo.map { Math.round(it * 100) / 100f })
        assertEquals(listOf(5f, 25f), RoperoProduccion.agujerosAlHondo(30f))
    }

    @Test
    fun los_rieles_van_al_centro_de_cada_cajon_en_su_espacio() {
        // Casillero de 80 desde 7 con cuatro cajones: cada uno de 20, centros a 17, 37, 57 y 77 del suelo.
        val lateral = RoperoProduccion.tableros(ropero).first { it.nombre == "Lateral derecho" }
        val rieles = lateral.marcas.filter { it.tipo == RoperoProduccion.TipoMarca.RIEL }.map { it.a }
        assertEquals(listOf(17f, 37f, 57f, 77f), rieles.map { Math.round(it * 10) / 10f })
        val division = RoperoProduccion.tableros(ropero).first { it.nombre == "División" }
        assertEquals(4, division.marcas.count { it.tipo == RoperoProduccion.TipoMarca.RIEL && it.cara == "cara derecha" })
    }

    @Test
    fun las_bisagras_van_a_10_de_las_puntas_y_esquivan_las_melaminas() {
        // Una hoja de 223 lleva cuatro: a 10 de cada punta y las del medio repartidas.
        val puerta = RoperoProduccion.tableros(ropero).first { it.nombre == "Puerta" && it.alto > 200f }
        val cazoletas = puerta.marcas.filter { it.tipo == RoperoProduccion.TipoMarca.CAZOLETA }.map { it.a }
        assertEquals(4, cazoletas.size)
        assertEquals(10f, cazoletas.first(), 0.05f)
        assertEquals(puerta.alto - 10f, cazoletas.last(), 0.05f)
        // Hasta un metro, dos. Si la de abajo (a 10, ocupa de 6.5 a 13.5) choca con una tabla de 8
        // a 9.8, se corre arriba lo justo: 9.8 + 3.5 + 0.5 = 13.8 (abajo no cabe en la hoja).
        assertEquals(2, RoperoPuertas.bisagrasPorAlto(90f))
        assertEquals(listOf(13.8f, 90f), RoperoProduccion.alturasDeBisagras(0f, 100f, 2, listOf(8f to 9.8f)).map { Math.round(it * 10) / 10f })
    }
}
