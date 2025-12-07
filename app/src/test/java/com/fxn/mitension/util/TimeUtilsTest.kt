package com.fxn.mitension.util

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class TimeUtilsTest {

    @Test
    fun `obtenerPeriodoActual devuelve MAÑANA para las 10 AM`() {
        val calendario = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
        }
        val periodo = obtenerPeriodoParaCalendario(calendario)
        assertEquals(PeriodoDelDia.MAÑANA, periodo)
    }

    @Test
    fun `obtenerPeriodoActual devuelve TARDE para las 14 PM`() {
        val calendario = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
        }

        val periodo = obtenerPeriodoParaCalendario(calendario)
        assertEquals(PeriodoDelDia.TARDE, periodo)
    }

    @Test
    fun `obtenerPeriodoActual devuelve NOCHE para las 21 PM`() {
        val calendario = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 0)
        }

        val periodo = obtenerPeriodoParaCalendario(calendario)
        assertEquals(PeriodoDelDia.NOCHE, periodo)
    }

    private fun obtenerPeriodoParaCalendario(calendario: Calendar): PeriodoDelDia {
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minuto = calendario.get(Calendar.MINUTE)
        val tiempoEnMinutos = hora * 60 + minuto

        return when {
            tiempoEnMinutos in 1..750 -> PeriodoDelDia.MAÑANA
            tiempoEnMinutos in 751..1140 -> PeriodoDelDia.TARDE
            else -> PeriodoDelDia.NOCHE
        }
    }
}
