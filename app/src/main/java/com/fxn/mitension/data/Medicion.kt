package com.fxn.mitension.data

data class Medicion(
    val sistolica: Int? = null,
    val diastolica: Int? = null,
    //todo val timestamp: Long = System.currentTimeMillis() // Lo añadiremos cuando guardemos datos
)

