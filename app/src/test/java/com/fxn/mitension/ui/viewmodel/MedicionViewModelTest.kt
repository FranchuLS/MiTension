package com.fxn.mitension.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.fxn.mitension.data.MedicionRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MedicionViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private val repository: MedicionRepository = mockk(relaxed = true)

    private lateinit var viewModel: MedicionViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MedicionViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Limpiamos el dispatcher
    }

    @Test
    fun `guardarMedicion emite error si los campos están vacíos`() = runTest {
        viewModel.guardarMedicion("Campos obligatorios", "", "")
        viewModel.evento.test {
            val evento = awaitItem()
            assertTrue(evento is MedicionViewModel.UiEvento.MostrarMensaje)
            assertEquals(
                "Campos obligatorios", (evento as MedicionViewModel.UiEvento.MostrarMensaje).mensaje
            )
            cancelAndConsumeRemainingEvents()
        }

        coVerify(exactly = 0) { repository.insertarMedicion(any()) }
    }

    @Test
    fun `guardarMedicion guarda con éxito si los campos son válidos`() = runTest {
        viewModel.onSistolicaChanged("120")
        viewModel.onDiastolicaChanged("80")
        viewModel.guardarMedicion("", "", "Éxito")
        viewModel.evento.test {
            val evento = awaitItem()
            assertTrue(evento is MedicionViewModel.UiEvento.GuardadoConExito)
            assertEquals("Éxito", (evento as MedicionViewModel.UiEvento.GuardadoConExito).mensaje)
            cancelAndConsumeRemainingEvents()
        }

        coVerify(exactly = 1) { repository.insertarMedicion(any()) }
    }

    @Test
    fun `onGuardadoExitoso limpia los campos y actualiza el contador`() = runTest {
        // GIVEN: El estado inicial tiene valores
        viewModel.onSistolicaChanged("130")
        viewModel.onDiastolicaChanged("85")

        // Y el número de medición es 1 (lo obtenemos del estado inicial que carga el ViewModel)
        // Para hacer el test más robusto, podemos esperar a que el estado inicial se cargue.
        testDispatcher.scheduler.advanceUntilIdle() // Avanza las corrutinas pendientes (como el init)
        assertEquals(1, viewModel.uiState.value.numeroMedicion)

        // WHEN: Se llama a onGuardadoExitoso
        viewModel.onGuardadoExitoso()
        testDispatcher.scheduler.advanceUntilIdle() // Nos aseguramos de que la corrutina interna termine

        // THEN: Los campos se limpian y el contador se incrementa
        val newState = viewModel.uiState.value
        assertEquals("", newState.sistolica)
        assertEquals("", newState.diastolica)
        assertEquals(2, newState.numeroMedicion)
    }
}
