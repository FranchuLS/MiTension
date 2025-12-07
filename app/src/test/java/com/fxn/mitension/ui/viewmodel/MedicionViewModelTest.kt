package com.fxn.mitension.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.fxn.mitension.data.MedicionRepository
import io.mockk.coEvery
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
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MedicionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // CAMBIO 1: Usar coAnswers para una configuración de mock más robusta
        coEvery { repository.contarMedicionesEnRango(any(), any()) } coAnswers { 0 }
        viewModel = MedicionViewModel(repository)
        // CAMBIO 2: Aseguramos que cualquier corrutina del 'init' se complete
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init carga el número de medición correcto (1 si no hay datos)`() = runTest {
        // La comprobación ahora es más simple porque el setUp ya se encarga de la corrutina
        assertEquals(1, viewModel.uiState.value.numeroMedicion)
    }

    @Test
    fun `guardarMedicion emite error si los campos están vacíos`() = runTest {
        viewModel.guardarMedicion("Campos obligatorios", "", "")
        viewModel.evento.test {
            val evento = awaitItem()
            assertTrue(evento is MedicionViewModel.UiEvento.MostrarMensaje)
            assertEquals("Campos obligatorios", (evento as MedicionViewModel.UiEvento.MostrarMensaje).mensaje)
        }
        coVerify(exactly = 0) { repository.insertarMedicion(any()) }
    }

    @Test
    fun `guardarMedicion emite error si el cupo del período está lleno`() = runTest {
        // GIVEN: Simulamos que el cupo está lleno
        viewModel.onGuardadoExitoso() // num = 2
        viewModel.onGuardadoExitoso() // num = 3
        viewModel.onGuardadoExitoso() // num = 4
        assertEquals(4, viewModel.uiState.value.numeroMedicion)

        // WHEN
        viewModel.onSistolicaChanged("120")
        viewModel.onDiastolicaChanged("80")
        viewModel.guardarMedicion("", "Cupo lleno", "")

        // THEN
        viewModel.evento.test {
            val evento = awaitItem()
            assertTrue(evento is MedicionViewModel.UiEvento.MostrarMensaje)
            assertTrue((evento as MedicionViewModel.UiEvento.MostrarMensaje).mensaje.contains("Cupo lleno"))
        }
        coVerify(exactly = 0) { repository.insertarMedicion(any()) }
    }

    @Test
    fun `guardarMedicion guarda con éxito si los campos son válidos`() = runTest {
        viewModel.onSistolicaChanged("120")
        viewModel.onDiastolicaChanged("80")
        viewModel.guardarMedicion("", "", "Éxito")
        viewModel.evento.test {
            assertTrue(awaitItem() is MedicionViewModel.UiEvento.GuardadoConExito)
        }
        coVerify(exactly = 1) { repository.insertarMedicion(any()) }
    }

    // --- TEST CORREGIDO ---
    @Test
    fun `onGuardadoExitoso limpia campos y actualiza contador`() = runTest {
        // GIVEN
        viewModel.onSistolicaChanged("130")
        viewModel.onDiastolicaChanged("85")
        // El estado inicial es 1 gracias al setup
        assertEquals(1, viewModel.uiState.value.numeroMedicion)

        // WHEN
        viewModel.onGuardadoExitoso()
        // CAMBIO 3: Nos aseguramos de que cualquier corrutina interna que se lance se complete
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN
        val newState = viewModel.uiState.value
        assertEquals("", newState.sistolica)
        assertEquals("", newState.diastolica)
        assertEquals(2, newState.numeroMedicion)
    }
}
