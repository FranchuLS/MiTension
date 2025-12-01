package com.fxn.mitension.data

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
}
