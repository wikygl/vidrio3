package crystal.crystal.tienda

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Una ficha de la vitrina de proveedores (`proveedores/{uid}`, docs/TIENDA_ANUNCIOS_PLAN.md fase 2).
 * La escribe solo el servidor: `suscribirProveedor` cobra el período y `guardarProveedor` la cambia.
 */
data class Proveedor(
    val uid: String,
    val nombre: String,
    val ciudad: String,
    val zonas: List<String>,
    val rubros: List<String>,
    val descripcion: String,
    val logo: String,
    val estado: String,
    val vigenteHastaMs: Long
) {
    fun vigente(ahoraMs: Long): Boolean = estado == ESTADO_ACTIVO && vigenteHastaMs > ahoraMs

    /** "Huancayo · El Tambo, Chilca" */
    val lugar: String get() = (listOf(ciudad) + zonas).filter { it.isNotBlank() }.joinToString(", ")

    companion object {
        const val ESTADO_ACTIVO = "activo"
        const val ESTADO_BLOQUEADO = "bloqueado"

        fun desde(doc: DocumentSnapshot): Proveedor? {
            if (!doc.exists()) return null
            fun lista(campo: String) = (doc.get(campo) as? List<*>)?.filterIsInstance<String>().orEmpty()
            return Proveedor(
                uid = doc.id,
                nombre = doc.getString("nombre").orEmpty(),
                ciudad = doc.getString("ciudad").orEmpty(),
                zonas = lista("zonas"),
                rubros = lista("rubros"),
                descripcion = doc.getString("descripcion").orEmpty(),
                logo = doc.getString("logo").orEmpty(),
                estado = doc.getString("estado").orEmpty(),
                vigenteHastaMs = (doc.get("vigenteHasta") as? Timestamp)?.toDate()?.time ?: 0L
            )
        }

        /** Los que venden algo de [rubros] van primero; los demás, después y por nombre. */
        fun ordenarPara(lista: List<Proveedor>, rubros: List<String>): List<Proveedor> =
            lista.sortedWith(compareBy<Proveedor> { p -> if (p.rubros.any { it in rubros }) 0 else 1 }.thenBy { it.nombre.lowercase() })
    }
}

/**
 * Un pedido que un cliente final dejó en la web (`pedidos_clientes/{id}`, fase 3). Lo público: qué
 * necesita, dónde y el primer nombre. El teléfono solo lo recibe quien lo toma ([TomaPedido]).
 */
data class PedidoCliente(
    val id: String,
    val que: String,
    val ciudad: String,
    val detalle: String,
    val medidas: String,
    val nombreCorto: String,
    val estado: String,
    val creadoEnMs: Long,
    val venceEnMs: Long,
    val tomas: Int,
    val maxTalleres: Int,
    val tomadoPor: List<String>,
    val paraUid: String,
    val anuncioTitulo: String
) {
    val dirigido: Boolean get() = paraUid.isNotEmpty()

    /** "Mampara de baño", o el anuncio por el que preguntó el cliente. */
    val titulo: String get() = when {
        anuncioTitulo.isNotBlank() -> "Por su anuncio: $anuncioTitulo"
        que.isNotBlank() -> que
        else -> "Pedido de cliente"
    }

    fun disponiblePara(uid: String, ahoraMs: Long): Boolean =
        estado == ESTADO_ABIERTO && venceEnMs > ahoraMs && tomas < maxTalleres && (paraUid.isEmpty() || paraUid == uid)

    companion object {
        const val ESTADO_ABIERTO = "abierto"

        fun desde(doc: DocumentSnapshot): PedidoCliente? {
            if (!doc.exists()) return null
            fun ms(campo: String) = (doc.get(campo) as? Timestamp)?.toDate()?.time ?: 0L
            return PedidoCliente(
                id = doc.id,
                que = doc.getString("que").orEmpty(),
                ciudad = doc.getString("ciudad").orEmpty(),
                detalle = doc.getString("detalle").orEmpty(),
                medidas = doc.getString("medidas").orEmpty(),
                nombreCorto = doc.getString("nombreCorto").orEmpty(),
                estado = doc.getString("estado").orEmpty(),
                creadoEnMs = ms("creadoEn"),
                venceEnMs = ms("venceEn"),
                tomas = (doc.getLong("tomas") ?: 0L).toInt(),
                maxTalleres = (doc.getLong("maxTalleres") ?: 1L).toInt(),
                tomadoPor = (doc.get("tomadoPor") as? List<*>)?.filterIsInstance<String>().orEmpty(),
                paraUid = doc.getString("paraUid").orEmpty(),
                anuncioTitulo = doc.getString("anuncioTitulo").orEmpty()
            )
        }
    }
}

/** Lo que recibe el taller al tomar un pedido: con quién hablar. */
data class TomaPedido(val nombre: String, val telefono: String, val reportado: Boolean = false) {
    /** El enlace de WhatsApp: el servidor ya dejó el número con su código de país. */
    val whatsapp: String get() = "https://wa.me/" + telefono.filter { it.isDigit() }
}

/** Un renglón de los precios de referencia: lo que se pide por una unidad. */
data class PrecioReferencia(
    val unidad: String,
    val vendedores: Int,
    val minCent: Long,
    val medianaCent: Long,
    val maxCent: Long
) {
    companion object {
        fun lista(valor: Any?): List<PrecioReferencia> = (valor as? List<*>)?.mapNotNull { f ->
            val m = f as? Map<*, *> ?: return@mapNotNull null
            fun n(k: String) = (m[k] as? Number)?.toLong() ?: 0L
            PrecioReferencia(m["unidad"]?.toString().orEmpty(), n("vendedores").toInt(), n("minCent"), n("medianaCent"), n("maxCent"))
        }.orEmpty()
    }
}
