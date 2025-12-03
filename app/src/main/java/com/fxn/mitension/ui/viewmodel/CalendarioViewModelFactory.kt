package com.fxn.mitension.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fxn.mitension.data.MedicionRepository

/**
 * Factoría para crear una instancia de CalendarioViewModel, inyectándole
 * el MedicionRepository necesario para obtener los datos del mes.
 */
class CalendarioViewModelFactory(
    private val repository: MedicionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Comprueba si la clase que se pide es CalendarioViewModel
        if (modelClass.isAssignableFrom(CalendarioViewModel::class.java)) {
            // crea y devuelve una nueva instancia con el repositorio.
            @Suppress("UNCHECKED_CAST")
            return CalendarioViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
