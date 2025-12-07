package com.fxn.mitension.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Medicion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sistolica: Int,
    val diastolica: Int,
    val timestamp: Long = System.currentTimeMillis()
)

