package com.fxn.mitension.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class MedicionDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: MedicionDao

    @Before
    fun setup() {
        // Usamos una base de datos en memoria para los tests: se crea y se destruye con cada test.
        // Esto asegura que los tests estén aislados y no interfieran entre sí.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build() // Permitimos consultas en el hilo principal solo para tests
        dao = database.medicionDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertarYContarMedicion() = runTest {
        // GIVEN: Una nueva medición
        val medicion = Medicion(id = 1, sistolica = 120, diastolica = 80, timestamp = 1000L)

        // WHEN: Se inserta en la base de datos
        dao.insertar(medicion)

        // THEN: El conteo de mediciones en ese rango de tiempo debe ser 1
        val count = dao.contarMedicionesEnRango(900L, 1100L)
        assertEquals(1, count)
    }

    @Test
    fun obtenerMedicionesPorDiaDevuelveListaCorrecta() = runTest {
        // GIVEN: Dos mediciones, una dentro del rango y otra fuera
        val medicionDentro = Medicion(id = 1, sistolica = 120, diastolica = 80, timestamp = 1000L)
        val medicionFuera = Medicion(id = 2, sistolica = 130, diastolica = 90, timestamp = 2000L)
        dao.insertar(medicionDentro)
        dao.insertar(medicionFuera)

        // WHEN: Se piden las mediciones del primer rango de tiempo
        // .first() es una función de corrutinas que obtiene el primer valor emitido por el Flow
        val mediciones = dao.obtenerMedicionesPorDia(500L, 1500L).first()

        // THEN: La lista debe contener solo la primera medición
        assertEquals(1, mediciones.size)
        assertEquals(medicionDentro, mediciones[0])
    }
}
