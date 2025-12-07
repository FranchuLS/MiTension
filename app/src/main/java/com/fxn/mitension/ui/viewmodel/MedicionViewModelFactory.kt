package com.fxn.mitension.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fxn.mitension.data.MedicionRepository

// Esta factoría sabe cómo crear una instancia de MedicionViewModel.
// Recibe el repositorio como dependencia.
class MedicionViewModelFactory(
    private val repository: MedicionRepository,
    private val mensajeError: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Comprueba si la clase que se pide es MedicionViewModel
        if (modelClass.isAssignableFrom(MedicionViewModel::class.java)) {
            // Si lo es, crea y devuelve una nueva instancia con el repositorio.
            @Suppress("UNCHECKED_CAST")
            return MedicionViewModel(repository) as T
        }
        // Si se pide otro tipo de ViewModel, lanza un error.
        throw IllegalArgumentException(mensajeError)
    }
}
