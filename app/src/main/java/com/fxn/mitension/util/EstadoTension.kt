package com.fxn.mitension.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class EstadoTension {
    CRISIS_HIPERTENSIVA,
    ALTA_2,
    ALTA_1,
    ELEVADA,
    NORMAL,
    BAJA
}

/**
 * Clasifica la tensión arterial según las guías de la AHA.
 * @param sistolica El valor de la presión sistólica.
 * @param diastolica El valor de la presión diastólica.
 * @return El [EstadoTension] correspondiente.
 */
fun clasificarTension(sistolica: Int, diastolica: Int): EstadoTension {
    return when {
        // La crisis hipertensiva es la más grave, se comprueba primero
        sistolica > 180 || diastolica > 120 -> EstadoTension.CRISIS_HIPERTENSIVA

        // Hipertensión Estadio 2
        sistolica >= 140 || diastolica >= 90 -> EstadoTension.ALTA_2

        // Hipertensión Estadio 1
        sistolica >= 130 || diastolica >= 80 -> EstadoTension.ALTA_1

        // Elevada
        sistolica >= 120 && diastolica < 80 -> EstadoTension.ELEVADA

        // Hipotensión (Baja)
        sistolica < 90 || diastolica < 60 -> EstadoTension.BAJA

        // Si no es ninguna de las anteriores, es Normal
        else -> EstadoTension.NORMAL
    }
}

/**
 * Devuelve un color específico basado en el estado de la tensión.
 * @param estado El [EstadoTension] a evaluar.
 * @return El [Color] para la UI.
 */
@Composable
fun obtenerColorPorEstado(estado: EstadoTension): Color {
    return when (estado) {
        EstadoTension.BAJA -> Color.Blue
        EstadoTension.NORMAL -> Color(0xFF008000) // Un verde más oscuro
        EstadoTension.ELEVADA -> Color(0xFFFFA500) // Naranja
        EstadoTension.ALTA_1 -> Color.Red
        EstadoTension.ALTA_2 -> Color(0xFFDC143C) // Carmesí
        EstadoTension.CRISIS_HIPERTENSIVA -> Color(0xFF8B0000) // Rojo oscuro
    }
}