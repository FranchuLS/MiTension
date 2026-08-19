package com.fxn.mitension.util

import android.icu.util.Calendar

enum class PeriodoDelDia {
    MAÑANA,
    TARDE,
    NOCHE
}

/**
 * Devuelve el período actual basado en la hora real del sistema.
 * Mañana: 04:00 a 12:30
 * Mediodía/Tarde: 12:31 a 19:00
 * Noche: 19:01 a 03:59 (del día siguiente)
 */
fun obtenerPeriodoActual(): PeriodoDelDia {
    val calendario = Calendar.getInstance()
    val hora = calendario.get(Calendar.HOUR_OF_DAY)
    val minuto = calendario.get(Calendar.MINUTE)

    val mins = hora * 60 + minuto

    return when {
        // Mañana: 04:00 (240) a 12:30 (750)
        mins in 240..750 -> PeriodoDelDia.MAÑANA
        // Tarde: 12:31 (751) a 19:00 (1140)
        mins in 751..1140 -> PeriodoDelDia.TARDE
        // Noche: 19:01 a 03:59
        else -> PeriodoDelDia.NOCHE
    }
}
