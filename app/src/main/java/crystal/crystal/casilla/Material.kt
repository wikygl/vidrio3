package crystal.crystal.casilla

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import crystal.crystal.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. Dominio: modelo de Material
enum class TipoMaterial { ALUMINIO, VIDRIO, ACCESORIOS, PRODUCTOS, OTROS }

data class Material(
    val id: Int,
    val drawableId: Int,
    val nombre: String,
    val tipo: TipoMaterial
)

// 2. Capa de almacenamiento: Entity para Room
@Entity(tableName = "materiales")
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "drawable_id")
    val drawableId: Int,
    val nombre: String,
    @ColumnInfo(name = "tipo")
    val tipo: String
) {
    fun toDomain() = Material(
        id = id,
        drawableId = drawableId,
        nombre = nombre,
        tipo = TipoMaterial.valueOf(tipo)
    )
    companion object {
        fun fromDomain(m: Material) = MaterialEntity(
            id = m.id,
            drawableId = m.drawableId,
            nombre = m.nombre,
            tipo = m.tipo.name
        )
    }
}

// 3. DAO: consultas especÃ­ficas
@Dao
interface MaterialDao {
    @Query("SELECT * FROM materiales WHERE tipo = :tipo")
    fun getByTipo(tipo: String): Flow<List<MaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(materiales: List<MaterialEntity>)

    @Query("DELETE FROM materiales")
    suspend fun clearAll()
}

// 4. Base de datos Room
@Database(
    entities = [MaterialEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun materialDao(): MaterialDao

    companion object {
        const val DB_NAME = "app_database"
    }
}

// 5. Repositorio: interfaz y su implementaciÃ³n
interface MaterialRepository {
    fun getMateriales(tipo: TipoMaterial): Flow<List<Material>>
    suspend fun recargar(materiales: List<Material>)
}

class RoomMaterialRepository(
    private val dao: MaterialDao
) : MaterialRepository {

    override fun getMateriales(tipo: TipoMaterial): Flow<List<Material>> =
        dao.getByTipo(tipo.name)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun recargar(materiales: List<Material>) {
        dao.clearAll()
        dao.insertAll(materiales.map { MaterialEntity.fromDomain(it) })
    }
}


object MaterialData {
    /** Lista completa de materiales con todos los campos que necesites */
    val listaMat = listOf(
        Material(id = 0, drawableId = R.drawable.ic_vitrobasic, nombre = "VITROVEN",    tipo = TipoMaterial.PRODUCTOS),
        Material(id = 0, drawableId = R.drawable.ic_fichad3,   nombre = "NOVA",     tipo = TipoMaterial.PRODUCTOS),
        Material(id = 0, drawableId = R.drawable.pvicky,       nombre = "PUERTA",  tipo = TipoMaterial.PRODUCTOS),
        Material(id = 0, drawableId = R.drawable.ma_multi,       nombre = "MULTIPLE",   tipo = TipoMaterial.ALUMINIO),
        Material(id = 0, drawableId = R.drawable.mpcorre,      nombre = "Material Corre",  tipo = TipoMaterial.OTROS),
        Material(id = 0, drawableId = R.drawable.mpfijof,      nombre = "Fijo Futuro",     tipo = TipoMaterial.OTROS),
        Material(id = 0, drawableId = R.drawable.baranda,       nombre = "BARANDA INOX",     tipo = TipoMaterial.PRODUCTOS)

        // â€¦aÃ±ade todos los que necesitesâ€¦
    )
}
