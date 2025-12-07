package com.fxn.mitension.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fxn.mitension.data.MedicionRepository
import com.fxn.mitension.data.ResumenDiario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

data class CalendarioUiState(
    val fechaSeleccionada: LocalDate = LocalDate.now(),
    val resumenMensual: Map<Int, ResumenDiario> = emptyMap()
) {
    val anioMes: YearMonth = YearMonth.from(fechaSeleccionada)
}

class CalendarioViewModel(private val repository: MedicionRepository) : ViewModel() {

    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CalendarioUiState> = _fechaSeleccionada
        .flatMapLatest { fecha ->
            // Cuando la fecha cambia, hacemos una nueva consulta a la DB
            val inicioDelMes = fecha.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val finDelMes = fecha.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            repository.obtenerResumenMensual(inicioDelMes, finDelMes).map { resumenList ->
                CalendarioUiState(
                    fechaSeleccionada = fecha,
                    // Convertimos la lista en un mapa para un acceso rápido
                    resumenMensual = resumenList.associateBy { it.dia }
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CalendarioUiState()
        )

    fun mesSiguiente() {
        _fechaSeleccionada.value = _fechaSeleccionada.value.plusMonths(1)
    }

    fun mesAnterior() {
        _fechaSeleccionada.value = _fechaSeleccionada.value.minusMonths(1)
    }
}