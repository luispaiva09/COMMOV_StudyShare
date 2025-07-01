package com.example.studyshare.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studyshare.ViewModels.UtilizadorViewModel
import com.example.studyshare.Repositories.UtilizadorRepository

class UtilizadorViewModelFactory(private val repository: UtilizadorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UtilizadorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UtilizadorViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe de ViewModel Descochecida")
    }
}
