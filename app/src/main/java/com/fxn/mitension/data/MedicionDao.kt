package com.fxn.mitension.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para la tabla Medicion.
 * Define los métodos para interactuar con la base de datos.
 */
@Dao
interface MedicionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(medicion: Medicion)
    @Query("SELECT * FROM Medicion WHERE timestamp >= :inicioDelDia AND timestamp < :finDelDia ORDER BY timestamp DESC")
    fun obtenerMedicionesPorDia(inicioDelDia: Long, finDelDia: Long): Flow<List<Medicion>>

    @Query("SELECT COUNT(id) FROM Medicion WHERE timestamp >= :inicio AND timestamp < :fin")
    suspend fun contarMedicionesEnRango(inicio: Long, fin: Long): Int
}
