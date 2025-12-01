package com.fxn.mitension.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fxn.mitension.data.Medicion
import com.fxn.mitension.data.MedicionRepository
import com.fxn.mitension.util.PeriodoDelDia
import com.fxn.mitension.util.obtenerPeriodoActual
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.Locale
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import com.fxn.mitension.util.obtenerRangoTimestamps
import com.fxn.mitension.util.obtenerTiempoRestanteParaSiguientePeriodo

data class MedicionUiState(
    val sistolica: String = "",
    val diastolica: String = "",
    val periodo: PeriodoDelDia = obtenerPeriodoActual(),
    val numeroMedicion: Int = 1 // De 1 a 3
)

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
                numeroMedicion = conteo + 1 // Si hay 0, estamos en la 1. Si hay 1, en la 2, etc.
            )
        }
    }
    fun onSistolicaChanged(valor: String) {
        // Permitimos solo números y hasta 3 dígitos
        if (valor.length <= 3 && valor.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(sistolica = valor)
        }
    }

    fun onDiastolicaChanged(valor: String) {
        if (valor.length <= 3 && valor.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(diastolica = valor)
        }
    }

    fun guardarMedicion(mensajeErrorCampos: String, mensajeErrorPeriodoLleno: String, mensajeExito: String) {
        viewModelScope.launch {
            val sistolica = _uiState.value.sistolica
            val diastolica = _uiState.value.diastolica

            // Validación 1: Campos vacíos
            if (_uiState.value.sistolica.isBlank() || _uiState.value.diastolica.isBlank()) {
                _evento.emit(UiEvento.MostrarMensaje(mensajeErrorCampos))
                return@launch
            }

            // Validación 2: Período lleno
            if (_uiState.value.numeroMedicion > 3) {
                val tiempoRestante = obtenerTiempoRestanteParaSiguientePeriodo(_uiState.value.periodo)
                // Formateamos el string del error con el tiempo restante
                val mensajeFormateado = String.format(mensajeErrorPeriodoLleno, tiempoRestante)
                _evento.emit(UiEvento.MostrarMensaje(mensajeFormateado))
                return@launch
            }

            try {
                // Creamos el objeto Medicion con los datos de la UI
                val nuevaMedicion = Medicion(
                    sistolica = sistolica.toInt(),
                    diastolica = diastolica.toInt(),
                    // El timestamp se genera por defecto en el constructor de Medicion
                )

                // Le pedimos al repositorio que inserte la nueva medición
                repository.insertarMedicion(nuevaMedicion)
                _evento.emit(UiEvento.GuardadoConExito(mensajeExito))

            } catch (e: NumberFormatException) {
                // Esto es un seguro por si algo muy raro pasa y el texto no es un número
                _evento.emit(UiEvento.MostrarMensaje("Error: Invalid numeric value."))
            }
            println("Guardado: Sistólica=${sistolica}, Diastólica=${diastolica}")
        }
    }

    fun onGuardadoExitoso() {
        // Incrementamos el número de medición para la UI
        val nuevoNumero = _uiState.value.numeroMedicion + 1
        _uiState.value = _uiState.value.copy(
            sistolica = "",
            diastolica = "",
            numeroMedicion = nuevoNumero
        )
    }

    sealed class UiEvento {
        data class MostrarMensaje(val mensaje: String) : UiEvento()
        data class GuardadoConExito(val mensaje: String) : UiEvento()
    }
}