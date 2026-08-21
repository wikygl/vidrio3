package crystal.crystal.pos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Venta::class], version = 1, exportSchema = false)
abstract class VentaDatabase : RoomDatabase() {

    abstract fun ventaDao(): VentaDao

    companion object {
        @Volatile
        private var INSTANCE: VentaDatabase? = null

        fun getDatabase(context: Context): VentaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VentaDatabase::class.java,
                    "venta_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
