package com.fxn.mitension.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fxn.mitension.data.Medicion
import com.fxn.mitension.data.MedicionRepository
import com.fxn.mitension.ui.AppDestinations
import com.fxn.mitension.util.PeriodoDelDia
import com.fxn.mitension.util.obtenerPeriodoParaTimestamp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

data class DiaDetalleUiState(
    val medicionesAgrupadas: Map<PeriodoDelDia, List<Medicion>> = emptyMap(),
    val dia: Int = 0,
    val mes: Int = 0,
    val anio: Int = 0
)

class DiaDetalleViewModel(
    private val repository: MedicionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dia: Int = savedStateHandle[AppDestinations.DIA_ARG] ?: 0
    private val mes: Int = savedStateHandle[AppDestinations.MES_ARG] ?: 0
    private val anio: Int = savedStateHandle[AppDestinations.ANIO_ARG] ?: 0
    private val fechaSeleccionada = LocalDate.of(anio, mes, dia)
    private val inicioDelDia = fechaSeleccionada.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    private val finDelDia = fechaSeleccionada.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val uiState: StateFlow<DiaDetalleUiState> = repository.obtenerMedicionesEnRango(inicioDelDia, finDelDia)
        .map { mediciones ->
            // Agrupamos las mediciones por período
            DiaDetalleUiState(
                medicionesAgrupadas = mediciones.groupBy { obtenerPeriodoParaTimestamp(it.timestamp) },
                dia = dia,
                mes = mes,
                anio = anio
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DiaDetalleUiState(dia = dia, mes = mes, anio = anio)
        )
}