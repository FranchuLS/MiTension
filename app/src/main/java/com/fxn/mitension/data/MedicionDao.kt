package com.fxn.mitension.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(medicion: Medicion)

    @Query("SELECT * FROM Medicion WHERE timestamp >= :inicio AND timestamp < :fin ORDER BY timestamp ASC")
    fun obtenerMedicionesEnRango(inicio: Long, fin: Long): Flow<List<Medicion>>

    @Query("SELECT * FROM Medicion WHERE timestamp >= :inicioDelDia AND timestamp < :finDelDia ORDER BY timestamp DESC")
    fun obtenerMedicionesPorDia(inicioDelDia: Long, finDelDia: Long): Flow<List<Medicion>>

    @Query("SELECT COUNT(id) FROM Medicion WHERE timestamp >= :inicio AND timestamp < :fin")
    suspend fun contarMedicionesEnRango(inicio: Long, fin: Long): Int

    /**
     * Consulta SQL para el resumen mensual.
     * Como las mediciones de madrugada se guardan con la fecha del día anterior,
     * aquí agrupamos simplemente por el día calendario (hora local).
     */
    @Query("""
        WITH MedicionesProcesadas AS (
            SELECT
                *,
                /* Calculamos los minutos del día en hora local */
                (CAST(strftime('%H', timestamp / 1000, 'unixepoch', 'localtime') AS INTEGER) * 60 + 
                 CAST(strftime('%M', timestamp / 1000, 'unixepoch', 'localtime') AS INTEGER)) AS mins_local,
                /* Obtenemos el día del mes en hora local */
                CAST(strftime('%d', timestamp / 1000, 'unixepoch', 'localtime') AS INTEGER) AS dia_local
            FROM Medicion
            WHERE timestamp >= :inicioDelMes AND timestamp < :finDelMes
        ),
        MedicionesConPeriodo AS (
            SELECT
                *,
                CASE
                    WHEN mins_local BETWEEN 240 AND 750 THEN 'MAÑANA'
                    WHEN mins_local BETWEEN 751 AND 1140 THEN 'TARDE'
                    ELSE 'NOCHE'
                END AS periodo
            FROM MedicionesProcesadas
        )
        SELECT
            dia_local AS dia,
            AVG(CASE WHEN periodo = 'MAÑANA' THEN sistolica ELSE NULL END) as mediaSistolicaManana,
            AVG(CASE WHEN periodo = 'MAÑANA' THEN diastolica ELSE NULL END) as mediaDiastolicaManana,
            AVG(CASE WHEN periodo = 'TARDE' THEN sistolica ELSE NULL END) as mediaSistolicaTarde,
            AVG(CASE WHEN periodo = 'TARDE' THEN diastolica ELSE NULL END) as mediaDiastolicaTarde,
            AVG(CASE WHEN periodo = 'NOCHE' THEN sistolica ELSE NULL END) as mediaSistolicaNoche,
            AVG(CASE WHEN periodo = 'NOCHE' THEN diastolica ELSE NULL END) as mediaDiastolicaNoche
        FROM MedicionesConPeriodo
        GROUP BY dia_local
    """)
    fun obtenerResumenMensual(inicioDelMes: Long, finDelMes: Long): Flow<List<ResumenDiario>>
}
