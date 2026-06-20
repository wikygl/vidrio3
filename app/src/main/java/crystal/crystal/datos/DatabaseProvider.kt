package crystal.crystal.datos

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE products_new (
                    nombre TEXT NOT NULL PRIMARY KEY,
                    price REAL NOT NULL,
                    imagen1 TEXT,
                    imagen2 TEXT,
                    imagen3 TEXT,
                    imagen4 TEXT,
                    imagen5 TEXT
                )
                """.trimIndent()
            )
            database.execSQL(
                """
                INSERT OR REPLACE INTO products_new (nombre, price)
                SELECT description, price
                FROM products
                WHERE TRIM(description) != ''
                """.trimIndent()
            )
            database.execSQL("DROP TABLE products")
            database.execSQL("ALTER TABLE products_new RENAME TO products")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE products_new (
                    nombre TEXT NOT NULL PRIMARY KEY,
                    price REAL NOT NULL,
                    imagenes TEXT NOT NULL DEFAULT ''
                )
                """.trimIndent()
            )
            database.execSQL(
                """
                INSERT OR REPLACE INTO products_new (nombre, price, imagenes)
                SELECT
                    nombre,
                    price,
                    TRIM(
                        COALESCE(NULLIF(imagen1, ''), '') ||
                        CASE WHEN COALESCE(NULLIF(imagen2, ''), '') != '' THEN char(10) || imagen2 ELSE '' END ||
                        CASE WHEN COALESCE(NULLIF(imagen3, ''), '') != '' THEN char(10) || imagen3 ELSE '' END ||
                        CASE WHEN COALESCE(NULLIF(imagen4, ''), '') != '' THEN char(10) || imagen4 ELSE '' END ||
                        CASE WHEN COALESCE(NULLIF(imagen5, ''), '') != '' THEN char(10) || imagen5 ELSE '' END
                    )
                FROM products
                WHERE TRIM(nombre) != ''
                """.trimIndent()
            )
            database.execSQL("DROP TABLE products")
            database.execSQL("ALTER TABLE products_new RENAME TO products")
        }
    }

    fun getInstance(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "mi_base_de_datos"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
            INSTANCE = instance
            instance
        }
    }
}
