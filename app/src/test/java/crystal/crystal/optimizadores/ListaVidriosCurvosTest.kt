package crystal.crystal.optimizadores

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** La lista de vidrios curvos para el curvador o el horno (2026-10-01). */
class ListaVidriosCurvosTest {

    /** Dos ventanas de Pipa: la curva (Vna5) con su tipo de vidrio y la plana (Vna2), que no entra. */
    private val pipa: Map<String, List<List<String>>> = mapOf(
        "Vidrios" to listOf(
            listOf("60.4 x 126.9", "4", "Vna5", "", "curvo", "59.4", "4.5"),
            listOf("30 x 21.9", "2", "Vna5", "", "curvo", "29.9", "1.1"),
            listOf("60.4 x 126.9", "2", "Vna2")
        ),
        "u-13" to listOf(listOf("118", "2", "Vna5", "", "curvo", "102.8", "14.2")),
        "MetadatosProduccion" to listOf(listOf("x-MAT<alu:negro;vid:templado_8mm>", "", "Vna5"))
    )

    @Test
    fun junta_solo_los_vidrios_curvos_con_sus_datos() {
        val lista = ListaVidriosCurvos.de(mapOf("Pipa" to pipa))
        assertEquals(2, lista.size)
        val v = lista[0]
        assertEquals("Pipa", v.proyecto)
        assertEquals("Vna5", v.ventana)
        assertEquals("templado 8mm", v.tipo)
        assertEquals(60.4f, v.desarrollo, 0.001f)
        assertEquals(126.9f, v.alto, 0.001f)
        assertEquals(4, v.cantidad)
        // Radio de cuerda 59.4 y flecha 4.5: (59.4² / 4 + 4.5²) / 9 = 100.26 (en la lista, 100.2: df1 trunca).
        assertEquals(100.3f, v.radio!!, 0.05f)
    }

    @Test
    fun el_texto_para_el_horno() {
        val texto = ListaVidriosCurvos.texto(ListaVidriosCurvos.de(mapOf("Pipa" to pipa)), "2026-10-01")
        assertEquals(
            "VIDRIOS CURVOS - 2026-10-01\n" +
                "Medidas en cm. Desarrollo = ancho del vidrio sobre la curva.\n" +
                "La panza de cada arco, mirando desde adentro.\n" +
                "\nProyecto: Pipa\n" +
                "- Vna5: desarrollo 60.4 x alto 126.9 = 4 (templado 8mm)\n" +
                "  Cuerda 59.4 · Flecha 4.5 · Radio 100.2\n" +
                "- Vna5: desarrollo 30 x alto 21.9 = 2 (templado 8mm)\n" +
                "  Cuerda 29.9 · Flecha 1.1 · Radio 102.1\n" +
                "\nTotal: 6 vidrios",
            texto
        )
    }

    /** Una S de reflejante: cada arco con su panza y su espejo; las cóncavas juntas y las convexas juntas. */
    @Test
    fun la_s_de_reflejante_dice_como_queda_el_espejo() {
        val s = mapOf(
            "Vidrios" to listOf(
                listOf("60 x 120", "2", "Vna3", "", "curvo", "59.1", "4.4", "afuera", "espejo convexa"),
                listOf("60 x 120", "2", "Vna3", "", "curvo", "59.1", "4.4", "adentro", "espejo cóncava"),
                listOf("60 x 120", "1", "Vna1", "", "curvo", "59.1", "4.4", "afuera", "sin caras")
            )
        )
        val lista = ListaVidriosCurvos.de(mapOf("Ana" to s))
        assertEquals(listOf("espejo convexa", "espejo cóncava", "sin caras"), lista.map { it.cara })
        // Radio de cuerda 59.1 y flecha 4.4: (59.1² / 4 + 4.4²) / 8.8 = 101.43.
        assertEquals(
            "Vna3  60 x 120 = 2\n   Cuerda 59.1 · Flecha 4.4 · Radio 101.4\n   Panza hacia adentro · espejo CÓNCAVA",
            ListaVidriosCurvos.linea(lista[1])
        )
        assertEquals("Panza hacia afuera · Sin caras (se puede voltear)", ListaVidriosCurvos.linea(lista[2]).lines().last().trim())
    }

    /**
     * Cambiar de cara sin recalcular: con la panza hacia afuera, la cara de afuera es la convexa; si el
     * espejo es convexo, afuera está el espejo, y al tocarlo pasa el color afuera y el espejo queda
     * cóncavo. Con la panza hacia adentro, al revés.
     */
    @Test
    fun cambiar_de_cara_sin_recalcular() {
        fun v(panza: String, cara: String) = ListaVidriosCurvos.VidrioCurvo(
            "Ana", "Vna3", "", 60f, 120f, 2, 59.1f, 4.4f, panza = panza, cara = cara
        )
        val afueraConvexo = v("afuera", "espejo convexa")
        assertEquals("espejo", ListaVidriosCurvos.caraAfuera(afueraConvexo))
        assertEquals("espejo cóncava", ListaVidriosCurvos.alternarCara(afueraConvexo))
        assertEquals("color", ListaVidriosCurvos.caraAfuera(v("afuera", "espejo cóncava")))

        val adentroConvexo = v("adentro", "espejo convexa")
        assertEquals("color", ListaVidriosCurvos.caraAfuera(adentroConvexo))
        assertEquals("espejo", ListaVidriosCurvos.caraAfuera(v("adentro", "espejo cóncava")))

        // Sin caras dichas: al tocar, empieza con el espejo afuera.
        assertNull(ListaVidriosCurvos.caraAfuera(v("afuera", "sin caras")))
        assertEquals("espejo convexa", ListaVidriosCurvos.alternarCara(v("afuera", "sin caras")))
        assertEquals("espejo cóncava", ListaVidriosCurvos.alternarCara(v("adentro", "")))
    }

    /** Cada vidrio sabe dónde está archivado, para guardarle la cara nueva en su lugar. */
    @Test
    fun cada_vidrio_sabe_donde_esta_archivado() {
        val lista = ListaVidriosCurvos.de(mapOf("Pipa" to pipa))
        assertEquals(listOf("Vidrios" to 0, "Vidrios" to 1), lista.map { it.lista to it.indice })
    }

    @Test
    fun un_vidrio_archivado_sin_cuerda_ni_flecha_lo_dice() {
        val viejo = mapOf("Vidrios" to listOf(listOf("60.4 x 126.9", "4", "Vna7", "", "curvo")))
        val v = ListaVidriosCurvos.de(mapOf("Ana" to viejo)).single()
        assertNull(v.radio)
        assertEquals("Vna7  60.4 x 126.9 = 4\n   Cuerda y flecha: sin dato (archivado antes del 2026-10-01)", ListaVidriosCurvos.linea(v))
    }
}
