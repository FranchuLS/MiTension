package com.fxn.mitension.data

import kotlinx.coroutines.flow.Flow

// El repositorio actúa como intermediario entre la fuente de datos (DAO) y el resto de la app (ViewModel).
// Recibe el DAO como una dependencia en su constructor.
class MedicionRepository(private val medicionDao: MedicionDao) {

    /**
     * Inserta una nueva medición en la base de datos.
     * Esta es una función 'suspend' porque el DAO la ejecuta en un hilo de fondo.
     */
    suspend fun insertarMedicion(medicion: Medicion) {
        medicionDao.insertar(medicion)
    }

    /**
     * Llama al DAO para contar las mediciones en un rango de tiempo.
     */
    suspend fun contarMedicionesEnRango(inicio: Long, fin: Long): Int {
        return medicionDao.contarMedicionesEnRango(inicio, fin)
    }

    /**
     * Obtiene un Flow con la lista de mediciones para un rango de tiempo.
     * Ideal para observar los cambios de un día en tiempo real.
     */
    fun obtenerMedicionesEnRango(inicio: Long, fin: Long): Flow<List<Medicion>> {
        return medicionDao.obtenerMedicionesPorDia(inicio, fin)
    }

    /**
     * Llama al DAO para obtener un resumen diario para un rango de meses.
     */
    fun obtenerResumenMensual(inicioDelMes: Long, finDelMes: Long): Flow<List<ResumenDiario>> {
        return medicionDao.obtenerResumenMensual(inicioDelMes, finDelMes)
    }
}
