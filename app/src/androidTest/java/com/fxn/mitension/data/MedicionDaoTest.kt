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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class MedicionDaoTest {

    @get:Rule
    var instantTaskExecutorRule = androidx.arch.core.executor.testing.InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: MedicionDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.medicionDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertarYContarMedicion() = runTest {
        val medicion = Medicion(id = 1, sistolica = 120, diastolica = 80, timestamp = 1000L)
        dao.insertar(medicion)
        val count = dao.contarMedicionesEnRango(900L, 1100L)
        assertEquals(1, count)
    }

    @Test
    fun obtenerMedicionesPorDiaDevuelveListaCorrecta() = runTest {
        val medicionDentro = Medicion(id = 1, sistolica = 120, diastolica = 80, timestamp = 1000L)
        val medicionFuera = Medicion(id = 2, sistolica = 130, diastolica = 90, timestamp = 2000L)
        dao.insertar(medicionDentro)
        dao.insertar(medicionFuera)
        val mediciones = dao.obtenerMedicionesPorDia(500L, 1500L).first()
        assertEquals(1, mediciones.size)
        assertEquals(medicionDentro, mediciones[0])
    }
}
