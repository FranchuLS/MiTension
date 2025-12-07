package com.fxn.mitension.data

data class ResumenDiario(
    val dia: Int,
    val mediaSistolicaManana: Double?,
    val mediaDiastolicaManana: Double?,
    val mediaSistolicaTarde: Double?,
    val mediaDiastolicaTarde: Double?,
    val mediaSistolicaNoche: Double?,
    val mediaDiastolicaNoche: Double?
)
