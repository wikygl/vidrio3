package crystal.crystal.medicion

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import java.util.UUID

class MedicionRepository(
    private val dao: ItemMedicionObraDao
) {
    val itemsLive: LiveData<List<ItemMedicionObra>> =
        dao.observarTodos().map { lista ->
            lista.map { it.toDomain() }
        }

    suspend fun guardar(item: ItemMedicionObra): ItemMedicionObra {
        val now = System.currentTimeMillis()
        val preparado = item.copy(
            id = item.id.ifBlank { UUID.randomUUID().toString() },
            actualizadoEn = now,
            creadoEn = if (item.creadoEn <= 0L) now else item.creadoEn,
            pendienteSincronizar = true
        )
        dao.guardar(preparado.toEntity())
        return preparado
    }

    suspend fun eliminar(id: String) {
        dao.eliminarPorId(id)
    }

    suspend fun cambiarEstado(id: String, estado: EstadoMedicion) {
        dao.actualizarEstado(id, estado.name)
    }

    suspend fun buscar(texto: String): List<ItemMedicionObra> {
        if (texto.isBlank()) return dao.obtenerTodos().map { it.toDomain() }
        return dao.buscar(texto).map { it.toDomain() }
    }

    suspend fun duplicar(item: ItemMedicionObra): ItemMedicionObra {
        val nuevo = item.copy(
            id = UUID.randomUUID().toString(),
            numero = item.numero + 1,
            estado = EstadoMedicion.BORRADOR,
            creadoEn = System.currentTimeMillis(),
            actualizadoEn = System.currentTimeMillis(),
            pendienteSincronizar = true
        )
        dao.guardar(nuevo.toEntity())
        return nuevo
    }
}
