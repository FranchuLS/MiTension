package com.fxn.mitension.util

import android.icu.util.Calendar

enum class PeriodoDelDia {
    MAÑANA,
    TARDE,
    NOCHE
}

fun obtenerPeriodoActual(): PeriodoDelDia {
    val calendario = Calendar.getInstance()
    val hora = calendario.get(Calendar.HOUR_OF_DAY)
    val minuto = calendario.get(Calendar.MINUTE)

    val tiempoEnMinutos = hora * 60 + minuto

    return when {
        // Mañana: 00:01 (1) a 12:30 (750)
        tiempoEnMinutos in 1..750 -> PeriodoDelDia.MAÑANA
        // Tarde: 12:31 (751) a 19:00 (1140)
        tiempoEnMinutos in 751..1140 -> PeriodoDelDia.TARDE
        // Noche: 19:01 (1141) a 00:00 (0 o 1440)
        else -> PeriodoDelDia.NOCHE
    }
}