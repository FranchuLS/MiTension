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

    /**
     * Consulta SQL avanzada que devuelve un resumen de las medias para cada día de un mes.
     * Utiliza una Common Table Expression (CTE) para calcular el período de cada medición
     * y luego agrega los resultados agrupando por día.
     */
    @Query("""
        WITH MedicionesConPeriodo AS (
            SELECT
                *,
                CASE
                    WHEN (CAST(strftime('%H', timestamp / 1000, 'unixepoch') AS INTEGER) * 60 + CAST(strftime('%M', timestamp / 1000, 'unixepoch') AS INTEGER)) BETWEEN 1 AND 750 THEN 'MAÑANA'
                    WHEN (CAST(strftime('%H', timestamp / 1000, 'unixepoch') AS INTEGER) * 60 + CAST(strftime('%M', timestamp / 1000, 'unixepoch') AS INTEGER)) BETWEEN 751 AND 1140 THEN 'TARDE'
                    ELSE 'NOCHE'
                END AS periodo
            FROM Medicion
            WHERE timestamp >= :inicioDelMes AND timestamp < :finDelMes
        )
        SELECT
            CAST(strftime('%d', timestamp / 1000, 'unixepoch') AS INTEGER) AS dia,
            AVG(CASE WHEN periodo = 'MAÑANA' THEN sistolica ELSE NULL END) as mediaSistolicaManana,
            AVG(CASE WHEN periodo = 'MAÑANA' THEN diastolica ELSE NULL END) as mediaDiastolicaManana,
            AVG(CASE WHEN periodo = 'TARDE' THEN sistolica ELSE NULL END) as mediaSistolicaTarde,
            AVG(CASE WHEN periodo = 'TARDE' THEN diastolica ELSE NULL END) as mediaDiastolicaTarde,
            AVG(CASE WHEN periodo = 'NOCHE' THEN sistolica ELSE NULL END) as mediaSistolicaNoche,
            AVG(CASE WHEN periodo = 'NOCHE' THEN diastolica ELSE NULL END) as mediaDiastolicaNoche
        FROM MedicionesConPeriodo
        GROUP BY dia
    """)
    fun obtenerResumenMensual(inicioDelMes: Long, finDelMes: Long): Flow<List<ResumenDiario>>
}
