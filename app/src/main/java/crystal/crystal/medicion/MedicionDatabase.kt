package crystal.crystal.medicion

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ItemMedicionObraEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MedicionTypeConverters::class)
abstract class MedicionDatabase : RoomDatabase() {
    abstract fun itemMedicionObraDao(): ItemMedicionObraDao

    companion object {
        @Volatile
        private var INSTANCE: MedicionDatabase? = null

        fun getDatabase(context: Context): MedicionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedicionDatabase::class.java,
                    "medicion_obra_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

