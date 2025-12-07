package com.fxn.mitension.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TensionUtilsTest {

    @Test
    fun `clasificarTension devuelve NORMAL para 110 sobre 70`() {
        // GIVEN
        val sistolica = 110
        val diastolica = 70
        // WHEN
        val resultado = clasificarTension(sistolica, diastolica)
        // THEN
        assertEquals(EstadoTension.NORMAL, resultado)
    }

    @Test
    fun `clasificarTension devuelve ELEVADA para 125 sobre 79`() {
        // GIVEN
        val sistolica = 125
        val diastolica = 79
        // WHEN
        val resultado = clasificarTension(sistolica, diastolica)
        // THEN
        assertEquals(EstadoTension.ELEVADA, resultado)
    }

    @Test
    fun `clasificarTension devuelve ALTA_1 para 135 sobre 85`() {
        // GIVEN
        val sistolica = 135
        val diastolica = 85
        // WHEN
        val resultado = clasificarTension(sistolica, diastolica)
        // THEN
        assertEquals(EstadoTension.ALTA_1, resultado)
    }

    @Test
    fun `clasificarTension devuelve ALTA_2 para 145 sobre 95`() {
        // GIVEN
        val sistolica = 145
        val diastolica = 95
        // WHEN
        val resultado = clasificarTension(sistolica, diastolica)
        // THEN
        assertEquals(EstadoTension.ALTA_2, resultado)
    }

    @Test
    fun `clasificarTension devuelve CRISIS_HIPERTENSIVA para 181 sobre 121`() {
        // GIVEN
        val sistolica = 181
        val diastolica = 121
        // WHEN
        val resultado = clasificarTension(sistolica, diastolica)
        // THEN
        assertEquals(EstadoTension.CRISIS_HIPERTENSIVA, resultado)
    }

    @Test
    fun `clasificarTension devuelve BAJA para 89 sobre 59`() {
        // GIVEN
        val sistolica = 89
        val diastolica = 59
        // WHEN
        val resultado = clasificarTension(sistolica, diastolica)
        // THEN
        assertEquals(EstadoTension.BAJA, resultado)
    }
}
