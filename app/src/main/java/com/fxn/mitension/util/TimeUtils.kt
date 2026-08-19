package com.fxn.mitension.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Devuelve un par de Longs (inicio, fin) con los timestamps para un período específico.
 * Implementa el "Día Lógico": el día comienza a las 04:00 AM.
 */
fun obtenerRangoTimestamps(periodo: PeriodoDelDia): Pair<Long, Long> {
    val ahora = ZonedDateTime.now(ZoneId.systemDefault())
    // Si son menos de las 4 AM, el "hoy" para el usuario es ayer
    val fechaReferencia = if (ahora.hour < 4) ahora.minusDays(1).toLocalDate() else ahora.toLocalDate()
    
    return when (periodo) {
        PeriodoDelDia.MAÑANA -> {
            val inicio = fechaReferencia.atTime(4, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val fin = fechaReferencia.atTime(12, 30).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            Pair(inicio, fin)
        }
        PeriodoDelDia.TARDE -> {
            val inicio = fechaReferencia.atTime(12, 31).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val fin = fechaReferencia.atTime(19, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            Pair(inicio, fin)
        }
        PeriodoDelDia.NOCHE -> {
            val inicio = fechaReferencia.atTime(19, 1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val fin = fechaReferencia.plusDays(1).atTime(3, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            Pair(inicio, fin)
        }
    }
}

/**
 * Calcula el tiempo restante hasta el inicio del próximo período.
 */
fun obtenerTiempoRestanteParaSiguientePeriodo(periodoActual: PeriodoDelDia): String {
    val ahora = Instant.now().toEpochMilli()
    val proximoTimestamp = when (periodoActual) {
        PeriodoDelDia.MAÑANA -> obtenerRangoTimestamps(PeriodoDelDia.TARDE).first
        PeriodoDelDia.TARDE -> obtenerRangoTimestamps(PeriodoDelDia.NOCHE).first
        PeriodoDelDia.NOCHE -> {
            val zdt = ZonedDateTime.now(ZoneId.systemDefault())
            val fechaBase = if (zdt.hour >= 4) zdt.plusDays(1).toLocalDate() else zdt.toLocalDate()
            fechaBase.atTime(4, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }

    val diff = proximoTimestamp - ahora
    if (diff <= 0) return "un momento"
    val horas = diff / 3600000
    val minutos = (diff % 3600000) / 60000
    return if (horas > 0) "${horas}h y ${minutos}m" else "${minutos}m"
}

/**
 * Devuelve el Período del Día para un timestamp basándose en el "Día Lógico" (inicio 04:00 AM).
 */
fun obtenerPeriodoParaTimestamp(timestamp: Long): PeriodoDelDia {
    val zdt = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
    val mins = zdt.hour * 60 + zdt.minute

    return when {
        mins in 240..750 -> PeriodoDelDia.MAÑANA  // 04:00 - 12:30
        mins in 751..1140 -> PeriodoDelDia.TARDE // 12:31 - 19:00
        else -> PeriodoDelDia.NOCHE              // 19:01 - 03:59 (del día siguiente)
    }
}
