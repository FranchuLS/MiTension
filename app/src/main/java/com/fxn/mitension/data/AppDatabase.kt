package com.fxn.mitension.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Medicion::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicionDao(): MedicionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Definimos la migración de la versión 1 a la 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Añadimos la columna 'pulso' a la tabla existente
                // Al ser 'INTEGER' sin 'NOT NULL', por defecto los registros antiguos tendrán NULL
                db.execSQL("ALTER TABLE Medicion ADD COLUMN pulso INTEGER")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mitension_database"
                )
                .addMigrations(MIGRATION_1_2) // Añadimos la migración aquí
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
