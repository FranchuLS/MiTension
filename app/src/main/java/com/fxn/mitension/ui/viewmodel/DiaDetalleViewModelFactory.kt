package com.fxn.mitension.ui.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.fxn.mitension.data.MedicionRepository

class DiaDetalleViewModelFactory(
    private val repository: MedicionRepository
) : AbstractSavedStateViewModelFactory() {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(DiaDetalleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiaDetalleViewModel(repository, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}