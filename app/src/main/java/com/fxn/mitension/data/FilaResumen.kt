package com.fxn.mitension.data

import java.time.LocalDate

data class FilaResumen(
    val fecha: LocalDate,
    val sistolicaManana: Int? = null,
    val diastolicaManana: Int? = null,
    val pulsoManana: Int? = null,
    val sistolicaNoche: Int? = null,
    val diastolicaNoche: Int? = null,
    val pulsoNoche: Int? = null
)
