package com.fxn.mitension.util

import java.util.Calendar
/**
 * Devuelve un par de Longs (inicio, fin) con los timestamps para un período específico del día actual.
 */
fun obtenerRangoTimestamps(periodo: PeriodoDelDia): Pair<Long, Long> {
    val inicio = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    when (periodo) {
        PeriodoDelDia.MAÑANA -> {
            inicio.add(Calendar.MINUTE, 1) // 00:01
            val fin = (inicio.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 30)
            }
            return Pair(inicio.timeInMillis, fin.timeInMillis)
        }
        PeriodoDelDia.TARDE -> {
            inicio.set(Calendar.HOUR_OF_DAY, 12)
            inicio.set(Calendar.MINUTE, 31) // 12:31
            val fin = (inicio.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 19)
                set(Calendar.MINUTE, 0)
            }
            return Pair(inicio.timeInMillis, fin.timeInMillis)
        }
        PeriodoDelDia.NOCHE -> {
            inicio.set(Calendar.HOUR_OF_DAY, 19)
            inicio.set(Calendar.MINUTE, 1) // 19:01
            val fin = (inicio.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
            }
            return Pair(inicio.timeInMillis, fin.timeInMillis)
        }
    }
}

/**
 * Calcula el tiempo restante hasta el inicio del próximo período y lo formatea como un string.
 */
fun obtenerTiempoRestanteParaSiguientePeriodo(periodoActual: PeriodoDelDia): String {
    val ahora = System.currentTimeMillis()
    val proximoPeriodo = when (periodoActual) {
        PeriodoDelDia.MAÑANA -> obtenerRangoTimestamps(PeriodoDelDia.TARDE).first
        PeriodoDelDia.TARDE -> obtenerRangoTimestamps(PeriodoDelDia.NOCHE).first
        PeriodoDelDia.NOCHE -> {
            // Es el inicio de la mañana del día siguiente
            val mananaSiguiente = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 1)
                set(Calendar.SECOND, 0)
            }
            mananaSiguiente.timeInMillis
        }
    }

    val diff = proximoPeriodo - ahora
    if (diff <= 0) return "un momento"

    val horas = diff / (1000 * 60 * 60)
    val minutos = (diff % (1000 * 60 * 60)) / (1000 * 60)

    return when {
        horas > 0 -> "${horas}h y ${minutos}m"
        else -> "${minutos}m"
    }
}


/**
 * Devuelve el Período del Día para un timestamp específico.
 */
fun obtenerPeriodoParaTimestamp(timestamp: Long): PeriodoDelDia {
    val calendario = Calendar.getInstance().apply { timeInMillis = timestamp }
    val hora = calendario.get(Calendar.HOUR_OF_DAY)
    val minuto = calendario.get(Calendar.MINUTE)
    val tiempoEnMinutos = hora * 60 + minuto

    return when {
        tiempoEnMinutos in 1..750 -> PeriodoDelDia.MAÑANA
        tiempoEnMinutos in 751..1140 -> PeriodoDelDia.TARDE
        else -> PeriodoDelDia.NOCHE
    }
}
