package com.fxn.mitension.ui.viewmodel


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.YearMonth

data class CalendarioUiState(
    val fechaSeleccionada: LocalDate = LocalDate.now()
) {
    val anioMes: YearMonth = YearMonth.from(fechaSeleccionada)
}

class CalendarioViewModel : ViewModel() {
    private val _uiState = mutableStateOf(CalendarioUiState())
    val uiState: State<CalendarioUiState> = _uiState

    fun mesSiguiente() {
        _uiState.value = _uiState.value.copy(
            fechaSeleccionada = _uiState.value.fechaSeleccionada.plusMonths(1)
        )
    }

    fun mesAnterior() {
        _uiState.value = _uiState.value.copy(
            fechaSeleccionada = _uiState.value.fechaSeleccionada.minusMonths(1)
        )
    }
}