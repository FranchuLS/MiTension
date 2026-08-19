package com.fxn.mitension.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fxn.mitension.data.Medicion
import com.fxn.mitension.data.MedicionRepository
import com.fxn.mitension.util.obtenerPeriodoActual
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import com.fxn.mitension.util.obtenerRangoTimestamps
import com.fxn.mitension.util.obtenerTiempoRestanteParaSiguientePeriodo
import java.time.ZoneId
import java.time.ZonedDateTime

class MedicionViewModel(private val repository: MedicionRepository) : ViewModel() {

    private val _uiState = mutableStateOf(MedicionUiState())
    val uiState: State<MedicionUiState> = _uiState
    private val _evento = MutableSharedFlow<UiEvento>()
    val evento = _evento.asSharedFlow()

    init {
        cargarEstadoInicial()
    }

    private fun cargarEstadoInicial() {
        viewModelScope.launch {
            val periodoActual = obtenerPeriodoActual()
            val (inicio, fin) = obtenerRangoTimestamps(periodoActual)
            val conteo = repository.contarMedicionesEnRango(inicio, fin)

            _uiState.value = _uiState.value.copy(
                periodo = periodoActual,
                numeroMedicion = conteo + 1
            )
        }
    }

    fun onSistolicaChanged(valor: String) {
        if (valor.length <= 3 && valor.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(sistolica = valor)
        }
    }

    fun onDiastolicaChanged(valor: String) {
        if (valor.length <= 3 && valor.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(diastolica = valor)
        }
    }

    fun onPulsoChanged(valor: String) {
        if (valor.length <= 3 && valor.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(pulso = valor)
        }
    }

    fun guardarMedicion(mensajeErrorCampos: String, mensajeErrorPeriodoLleno: String, mensajeExito: String) {
        viewModelScope.launch {
            val sistolica = _uiState.value.sistolica
            val diastolica = _uiState.value.diastolica
            val pulso = _uiState.value.pulso

            if (_uiState.value.sistolica.isBlank() || _uiState.value.diastolica.isBlank()) {
                _evento.emit(UiEvento.MostrarMensaje(mensajeErrorCampos))
                return@launch
            }

            if (_uiState.value.numeroMedicion > 3) {
                val tiempoRestante = obtenerTiempoRestanteParaSiguientePeriodo(_uiState.value.periodo)
                val mensajeFormateado = String.format(mensajeErrorPeriodoLleno, tiempoRestante)
                _evento.emit(UiEvento.MostrarMensaje(mensajeFormateado))
                return@launch
            }

            try {
                // Lógica de "Día Lógico": Si se mide entre 00:00 y 04:00, se guarda como las 23:59 del día anterior.
                val ahora = ZonedDateTime.now(ZoneId.systemDefault())
                val timestampFinal = if (ahora.hour < 4) {
                    ahora.minusDays(1)
                        .withHour(23)
                        .withMinute(59)
                        .withSecond(59)
                        .toInstant()
                        .toEpochMilli()
                } else {
                    ahora.toInstant().toEpochMilli()
                }

                val nuevaMedicion = Medicion(
                    sistolica = sistolica.toInt(),
                    diastolica = diastolica.toInt(),
                    pulso = pulso.toIntOrNull(),
                    timestamp = timestampFinal
                )

                repository.insertarMedicion(nuevaMedicion)
                _evento.emit(UiEvento.GuardadoConExito(mensajeExito))

            } catch (e: NumberFormatException) {
                _evento.emit(UiEvento.MostrarMensaje("Error: Invalid numeric value. " + e.message))
            }
        }
    }

    fun onGuardadoExitoso() {
        val nuevoNumero = _uiState.value.numeroMedicion + 1
        _uiState.value = _uiState.value.copy(
            sistolica = "",
            diastolica = "",
            pulso = "",
            numeroMedicion = nuevoNumero
        )
    }

    sealed class UiEvento {
        data class MostrarMensaje(val mensaje: String) : UiEvento()
        data class GuardadoConExito(val mensaje: String) : UiEvento()
    }
}
