package com.fxn.mitension.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.fxn.mitension.data.Medicion
import com.fxn.mitension.data.MedicionRepository
import com.fxn.mitension.ui.AppDestinations
import com.fxn.mitension.util.PeriodoDelDia
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.util.*

@ExperimentalCoroutinesApi
class DiaDetalleViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository: MedicionRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val fecha = LocalDate.now()
        savedStateHandle = SavedStateHandle().apply {
            set(AppDestinations.ANIO_ARG, fecha.year)
            set(AppDestinations.MES_ARG, fecha.monthValue)
            set(AppDestinations.DIA_ARG, fecha.dayOfMonth)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState agrupa las mediciones por período correctamente`() = runTest(testDispatcher) { // <-- CAMBIO 1
        // GIVEN: Una lista de mediciones en diferentes períodos
        val medicionesDePrueba = listOf(
            Medicion(1, 120, 80, obtenerTimestamp(9, 0)),  // Mañana
            Medicion(2, 130, 85, obtenerTimestamp(15, 0)), // Tarde
            Medicion(3, 135, 88, obtenerTimestamp(16, 0)), // Tarde
            Medicion(4, 140, 90, obtenerTimestamp(21, 0))  // Noche
        )
        every { repository.obtenerMedicionesEnRango(any(), any()) } returns flowOf(medicionesDePrueba)

        // WHEN: Creamos el viewModel
        val viewModel = DiaDetalleViewModel(repository, savedStateHandle)

        // THEN: El uiState debe tener las mediciones agrupadas
        viewModel.uiState.test {
            // CAMBIO 2: Ignoramos el primer item, que es el valor inicial y vacío.
            // La primera emisión es el `initialValue` de `stateIn`.
            val estadoInicial = awaitItem()
            Assert.assertTrue(estadoInicial.medicionesAgrupadas.isEmpty())

            // La segunda emisión contiene los datos reales del flow.
            val estadoReal = awaitItem()
            assertEquals(1, estadoReal.medicionesAgrupadas[PeriodoDelDia.MAÑANA]?.size)
            assertEquals(2, estadoReal.medicionesAgrupadas[PeriodoDelDia.TARDE]?.size)
            assertEquals(1, estadoReal.medicionesAgrupadas[PeriodoDelDia.NOCHE]?.size)

            // Cancelamos para no esperar más eventos
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun obtenerTimestamp(hora: Int, minuto: Int): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
        }.timeInMillis
    }
}
