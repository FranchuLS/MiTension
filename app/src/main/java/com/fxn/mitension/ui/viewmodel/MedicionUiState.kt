package com.fxn.mitension.ui.viewmodel

import com.fxn.mitension.util.PeriodoDelDia
import com.fxn.mitension.util.obtenerPeriodoActual

data class MedicionUiState(
    val sistolica: String = "",
    val diastolica: String = "",
    val pulso: String = "",
    val periodo: PeriodoDelDia = obtenerPeriodoActual(),
    val numeroMedicion: Int = 1 // De 1 a 3
)
