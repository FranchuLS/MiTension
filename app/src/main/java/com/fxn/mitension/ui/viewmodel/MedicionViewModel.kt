package com.fxn.mitension.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.fxn.mitension.util.PeriodoDelDia
import com.fxn.mitension.util.obtenerPeriodoActual
import java.util.Locale

// Estado de la UI que el ViewModel expondrá a la vista
data class MedicionUiState(
    val sistolica: String = "",
    val diastolica: String = "",
    val periodo: PeriodoDelDia = obtenerPeriodoActual(),
    val numeroMedicion: Int = 1 // De 1 a 3
)

class MedicionViewModel : ViewModel() {

    private val _uiState = mutableStateOf(MedicionUiState())
    val uiState: State<MedicionUiState> = _uiState

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

    fun getTitulo(): String {
        val periodoStr = _uiState.value.periodo.name.lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        return "$periodoStr - Medición ${_uiState.value.numeroMedicion}/3"
    }

    // Formatea el valor para mostrarlo en la UI principal con coma
    fun formatDisplayValue(valor: String): String {
        return if (valor.length == 3) {
            "${valor.substring(0, 2)},${valor.substring(2)}"
        } else {
            valor
        }
    }
}