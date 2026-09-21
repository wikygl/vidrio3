package crystal.crystal.taller.melamina

import kotlin.math.abs

/**
 * Unir y desunir casilleros: se eligen dos celdas vecinas y desaparece la melamina que las
 * separa.
 *
 * - A lo alto (una sobre otra en la misma columna): se quita la repisa de en medio.
 * - A lo ancho (dos columnas vecinas, o dos cuerpos vecinos, con la misma altura): la división
 *   se corta en ese tramo. En el árbol eso es una columna nueva que abarca a las dos, con una
 *   repisa donde acaba y donde empieza la celda unida; lo que quedaba por debajo y por encima
 *   sigue partido en las dos columnas de antes, cada una con lo suyo recortado a su tramo.
 *   Si lo que se une son los cajones de abajo, la columna nueva lleva esos cajones a todo lo
 *   ancho y encima el casillero partido en las dos columnas.
 * - Desunir a lo ancho vuelve a partir el casillero en las columnas que tienen sus vecinos de
 *   arriba o abajo (o en dos iguales); desunir a lo alto le pone una repisa al medio.
 */
object RoperoUnion {

    private const val TOLERANCIA = 0.06f

    private val celdas = setOf(TipoElemento.CASILLERO, TipoElemento.COLGADOR, TipoElemento.ZONA_CAJONES)

    /** El ropero con las dos celdas unidas, o el motivo por el que no se puede. */
    fun unir(r: Ropero, a: ElementoRopero, b: ElementoRopero): Pair<Ropero?, String> {
        if (a.tipo !in celdas || b.tipo !in celdas) return null to "Solo se unen casilleros, colgadores o el espacio de los cajones"
        if (a.esElMismo(b)) return null to "Es la misma celda"
        val mismaColumna = a.cuerpo == b.cuerpo && a.ruta == b.ruta
        if (mismaColumna) {
            if (a.tipo == TipoElemento.ZONA_CAJONES || b.tipo == TipoElemento.ZONA_CAJONES) {
                // Los cajones con el casillero de encima: se quita la tapa y quedan dentro de él.
                val cas = if (a.tipo == TipoElemento.ZONA_CAJONES) b else a
                if (cas.tipo == TipoElemento.ZONA_CAJONES || cas.indice != 0) return null to "Los cajones solo se unen con el casillero que tienen justo encima"
                val c = r.cuerpoEn(a.cuerpo, a.ruta) ?: return null to ""
                if (!c.tapaSobreCajones) return null to "Ya están unidos: los cajones no llevan tapa"
                return r.conCuerpoEn(a.cuerpo, a.ruta, c.copy(tapaSobreCajones = false)) to ""
            }
            if (abs(a.indice - b.indice) != 1) return null to "Tienen que estar uno sobre otro, pegados"
            return unirAlto(r, a, b) to ""
        }
        // A lo ancho: la misma altura, pegadas, y hermanas (vecinas en el mismo reparto).
        if (abs(a.y0 - b.y0) > TOLERANCIA || abs(a.y1 - b.y1) > TOLERANCIA) return null to "Para unir a lo ancho tienen que tener la misma altura"
        val (izq, der) = if (a.x0 < b.x0) a to b else b to a
        if (abs(der.x0 - izq.x1 - r.espesorCm) > TOLERANCIA) return null to "Tienen que ser vecinas, con solo una división entre ellas"
        val rutaPadre = izq.ruta.dropLast(2)
        if (izq.cuerpo == der.cuerpo && izq.ruta.size == der.ruta.size && izq.ruta.size >= 2 && rutaPadre == der.ruta.dropLast(2)
            && izq.ruta[izq.ruta.size - 2] == der.ruta[der.ruta.size - 2] && der.ruta.last() == izq.ruta.last() + 1) {
            return unirColumnas(r, izq, der) to ""
        }
        if (izq.ruta.isEmpty() && der.ruta.isEmpty() && der.cuerpo == izq.cuerpo + 1) {
            if (abs(r.altoDeCuerpo(izq.cuerpo) - r.altoDeCuerpo(der.cuerpo)) > TOLERANCIA) return null to "Los dos cuerpos tienen distinto alto"
            return unirCuerpos(r, izq, der) to ""
        }
        return null to "Tienen que ser vecinas del mismo reparto (dos columnas o dos cuerpos pegados)"
    }

    // ==================== A lo alto ====================

    private fun unirAlto(r: Ropero, a: ElementoRopero, b: ElementoRopero): Ropero {
        val c = r.cuerpoEn(a.cuerpo, a.ruta) ?: return r
        val h = RoperoGeometria.huecoDe(r, a.cuerpo, a.ruta) ?: return r
        val quitar = minOf(a.indice, b.indice)     // la repisa entre las dos celdas
        val alturas = RoperoGeometria.alturasDeEntrepanos(r, c, h).toMutableList()
        if (quitar !in alturas.indices) return r
        alturas.removeAt(quitar)
        // Las columnas de las dos celdas unidas se pierden; las demás corren un puesto.
        val partes = c.partes.filterKeys { it != quitar && it != quitar + 1 }
            .mapKeys { (k, _) -> if (k > quitar + 1) k - 1 else k }
        return r.conCuerpoEn(a.cuerpo, a.ruta, c.copy(entrepanos = alturas.size, alturasEntrepanosCm = alturas, partes = partes))
    }

    /** La celda con una repisa al medio: lo contrario de unir a lo alto. Sobre unos cajones sin tapa, les devuelve la tapa. */
    fun desunirAlto(r: Ropero, el: ElementoRopero): Ropero {
        if (el.tipo !in setOf(TipoElemento.CASILLERO, TipoElemento.COLGADOR)) return r
        val c = r.cuerpoEn(el.cuerpo, el.ruta) ?: return r
        if (el.indice == 0 && c.cajonesEfectivos > 0 && !c.tapaSobreCajones) return r.conCuerpoEn(el.cuerpo, el.ruta, c.copy(tapaSobreCajones = true))
        val h = RoperoGeometria.huecoDe(r, el.cuerpo, el.ruta) ?: return r
        val alturas = RoperoGeometria.alturasDeEntrepanos(r, c, h).toMutableList()
        val nueva = (el.y0 + el.y1) / 2f - r.espesorCm / 2f - h.y0
        alturas.add(el.indice.coerceAtMost(alturas.size), nueva)
        val partes = c.partes.mapKeys { (k, _) -> if (k > el.indice) k + 1 else k }
        val tipo = if (c.tipo == TipoCuerpo.CAJONES) TipoCuerpo.CAJONES_CASILLEROS else c.tipo
        return r.conCuerpoEn(el.cuerpo, el.ruta, c.copy(tipo = tipo, entrepanos = alturas.size, alturasEntrepanosCm = alturas, partes = partes))
    }

    // ==================== A lo ancho ====================

    private fun unirColumnas(r: Ropero, izq: ElementoRopero, der: ElementoRopero): Ropero {
        val rutaPadre = izq.ruta.dropLast(2)
        val k = izq.ruta[izq.ruta.size - 2]
        val j = izq.ruta.last()
        val padre = r.cuerpoEn(izq.cuerpo, rutaPadre) ?: return r
        val huecoPadre = RoperoGeometria.huecoDe(r, izq.cuerpo, rutaPadre) ?: return r
        val casillero = RoperoGeometria.casilleros(r, padre, huecoPadre).getOrNull(k) ?: return r
        val huecos = RoperoGeometria.columnasDeCasillero(r, padre, casillero, k)
        val columnas = padre.columnasDe(k)
        if (j + 1 !in columnas.indices) return r
        val unida = columnaUnida(r, columnas[j], huecos[j], columnas[j + 1], huecos[j + 1], izq, casillero, der)
        val nuevas = columnas.take(j) + unida + columnas.drop(j + 2)
        // Si queda una sola columna se deja así: ocupa el casillero entero, con sus tramos.
        return r.conCuerpoEn(izq.cuerpo, rutaPadre, padre.conColumnas(k, nuevas))
    }

    private fun unirCuerpos(r: Ropero, izq: ElementoRopero, der: ElementoRopero): Ropero {
        val i = izq.cuerpo
        val a = r.cuerpos[i]; val b = r.cuerpos[i + 1]
        val ha = RoperoGeometria.huecoDeCuerpo(r, i); val hb = RoperoGeometria.huecoDeCuerpo(r, i + 1)
        val region = Hueco(ha.x0, hb.x1, ha.y0, ha.y1)
        val unido = columnaUnida(r, a, ha, b, hb, izq, region, der)
        return r.copy(cuerpos = r.cuerpos.take(i) + unido + r.cuerpos.drop(i + 2))
    }

    /**
     * La columna que abarca a [a] y [b] con la celda de [izq] (y su vecina) unida: los cajones
     * unidos a todo lo ancho, o repisas donde acaba y empieza la celda unida, y las dos columnas
     * recortadas por debajo y por encima.
     */
    private fun columnaUnida(r: Ropero, a: Cuerpo, ha: Hueco, b: Cuerpo, hb: Hueco, izq: ElementoRopero, region: Hueco, der: ElementoRopero = izq): Cuerpo {
        val e = r.espesorCm
        val ancho = ha.ancho + e + hb.ancho
        val y0 = izq.y0; val y1 = izq.y1
        val fijo = a.anchoFijo || b.anchoFijo
        val zonaIzq = izq.tipo == TipoElemento.ZONA_CAJONES
        val zonaDer = der.tipo == TipoElemento.ZONA_CAJONES
        if (zonaIzq || zonaDer) {
            // Los cajones (los del lado que los tiene) a todo lo ancho; encima, el casillero
            // partido en las dos columnas recortadas. Un casillero vacío de la misma altura se
            // los traga: cajones más anchos.
            val conCajones = if (zonaIzq) a else b
            val altos = RoperoGeometria.altosDeCajones(r, conCajones, if (zonaIzq) ha else hb)
            val tapa = y1 + e
            val arriba = listOfNotNull(recortar(r, a, ha, tapa, region.y1), recortar(r, b, hb, tapa, region.y1))
            var c = Cuerpo(anchoCm = ancho, tipo = TipoCuerpo.CAJONES, cajones = altos.size, altosCajonesCm = altos, cajonesALaVista = conCajones.cajonesALaVista, anchoFijo = fijo)
            if (arriba.size == 2 && region.y1 > tapa + 1f) c = c.conColumnas(0, arriba)
            return c
        }
        val alturas = mutableListOf<Float>()
        val abajo = if (y0 > region.y0 + TOLERANCIA) { alturas.add(y0 - e - region.y0); listOfNotNull(recortar(r, a, ha, region.y0, y0 - e), recortar(r, b, hb, region.y0, y0 - e)) } else emptyList()
        val arriba = if (y1 < region.y1 - TOLERANCIA) { alturas.add(y1 - region.y0); listOfNotNull(recortar(r, a, ha, y1 + e, region.y1), recortar(r, b, hb, y1 + e, region.y1)) } else emptyList()
        var c = Cuerpo(anchoCm = ancho, tipo = TipoCuerpo.ENTREPANOS, entrepanos = alturas.size, alturasEntrepanosCm = alturas, anchoFijo = fijo)
        var k = 0
        if (abajo.size == 2) c = c.conColumnas(k, abajo)
        if (abajo.isNotEmpty()) k++
        k++   // la celda unida
        if (arriba.size == 2) c = c.conColumnas(k, arriba)
        return c
    }

    /**
     * La parte de la columna [c] (que ocupaba [h]) entre [desde] y [hasta]: sus cajones si caben
     * enteros desde abajo, sus repisas de ese tramo (medidas desde el nuevo piso) y las columnas
     * de sus casilleros de ese tramo. Null si no queda nada de alto.
     */
    private fun recortar(r: Ropero, c: Cuerpo, h: Hueco, desde: Float, hasta: Float): Cuerpo? {
        if (hasta - desde < 1f) return null
        val e = r.espesorCm
        val conCajones = c.cajonesEfectivos > 0 && abs(desde - h.y0) < TOLERANCIA && RoperoGeometria.topeDeCajones(r, c, h) + e <= hasta + TOLERANCIA
        val altos = if (conCajones) RoperoGeometria.altosDeCajones(r, c, h) else emptyList()
        val alturas = RoperoGeometria.alturasDeEntrepanos(r, c, h)
        // Las repisas del tramo: su cara de abajo entre desde y hasta (sin contar las que hacen de borde).
        val dentro = alturas.withIndex().filter { (_, alt) -> h.y0 + alt > desde + TOLERANCIA && h.y0 + alt + e < hasta - TOLERANCIA }
        val nuevasAlturas = dentro.map { (_, alt) -> h.y0 + alt - desde }
        // Los casilleros del tramo: el primero de dentro es el que arranca en desde.
        val casilleros = RoperoGeometria.casilleros(r, c, h)
        val primero = casilleros.indexOfFirst { it.y0 >= desde - TOLERANCIA }
        val partes = if (primero < 0) emptyMap() else c.partes.filterKeys { it >= primero && it < primero + nuevasAlturas.size + 1 }.mapKeys { (k, _) -> k - primero }
        val llevaTubo = c.llevaTubo && abs(hasta - h.y1) < TOLERANCIA
        val tipo = when {
            altos.isNotEmpty() && llevaTubo -> TipoCuerpo.MIXTO
            altos.isNotEmpty() && nuevasAlturas.isNotEmpty() -> TipoCuerpo.CAJONES_CASILLEROS
            altos.isNotEmpty() -> TipoCuerpo.CAJONES
            llevaTubo && nuevasAlturas.isNotEmpty() -> TipoCuerpo.COLGAR_CASILLEROS
            llevaTubo -> TipoCuerpo.COLGAR
            else -> TipoCuerpo.ENTREPANOS
        }
        return c.copy(
            tipo = tipo, cajones = altos.size, altosCajonesCm = altos,
            entrepanos = nuevasAlturas.size, alturasEntrepanosCm = nuevasAlturas, partes = partes
        )
    }

    /** La celda vuelta a partir a lo ancho: con las columnas de su vecina de arriba o de abajo, o en dos iguales. */
    fun desunirAncho(r: Ropero, el: ElementoRopero): Ropero {
        if (el.tipo !in setOf(TipoElemento.CASILLERO, TipoElemento.COLGADOR)) return r
        val c = r.cuerpoEn(el.cuerpo, el.ruta) ?: return r
        val vecina = c.columnasDe(el.indice - 1).ifEmpty { c.columnasDe(el.indice + 1) }
        val nuevas = if (vecina.isNotEmpty()) vecina.map { Cuerpo(anchoCm = it.anchoCm, tipo = TipoCuerpo.ENTREPANOS, anchoFijo = it.anchoFijo) } else emptyList()
        val nuevo = if (nuevas.size >= 2) c.conColumnas(el.indice, nuevas) else c.conCasilleroPartido(el.indice, 2, el.x1 - el.x0, r.espesorCm)
        return r.conCuerpoEn(el.cuerpo, el.ruta, nuevo)
    }
}
