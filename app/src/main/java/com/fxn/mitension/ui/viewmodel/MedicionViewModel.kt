package com.fxn.mitension.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fxn.mitension.util.PeriodoDelDia
import com.fxn.mitension.util.obtenerPeriodoActual
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.Locale
import com.fxn.mitension.R
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class MedicionUiState(
    val sistolica: String = "",
    val diastolica: String = "",
    val periodo: PeriodoDelDia = obtenerPeriodoActual(),
    val numeroMedicion: Int = 1 // De 1 a 3
)

class MedicionViewModel : ViewModel() {

    private val _uiState = mutableStateOf(MedicionUiState())
    val uiState: State<MedicionUiState> = _uiState
    private val _evento = MutableSharedFlow<UiEvento>()
    val evento = _evento.asSharedFlow()

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

    fun guardarMedicion() {
        viewModelScope.launch {
            val sistolica = _uiState.value.sistolica
            val diastolica = _uiState.value.diastolica

            // Validación: Comprobamos que ninguno de los dos campos esté vacío
            if (sistolica.isBlank() || diastolica.isBlank()) {
                // Si la validación falla, enviamos un evento a la UI
                _evento.emit(UiEvento.MostrarMensaje(R.string.error_campos_obligatorios))
                return@launch // Detenemos la ejecución de la función
            }

            // TODO: Si la validación es correcta, aquí iría la lógica para guardar en la base de datos
            // Por ahora, podemos simular que se ha guardado con éxito
            println("Guardado: Sistólica=${sistolica}, Diastólica=${diastolica}")

            // TODO: En el futuro, podríamos limpiar los campos o navegar a otra pantalla
        }
    }

    fun getTitulo(): String {
        val periodoStr = _uiState.value.periodo.name.lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        return "$periodoStr - Medición ${_uiState.value.numeroMedicion}/3"
    }

    // Clase para definir los eventos que la UI puede recibir ---
    sealed class UiEvento {
        // Envolvemos el ID del recurso en lugar del String para que la UI lo resuelva
        data class MostrarMensaje(val idRecursoString: Int) : UiEvento()
    }
}