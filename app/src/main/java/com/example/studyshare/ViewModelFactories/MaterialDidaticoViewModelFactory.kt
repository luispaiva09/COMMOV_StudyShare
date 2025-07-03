package com.example.studyshare.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studyshare.Repositories.MaterialDidaticoRepository
import com.example.studyshare.ViewModels.MaterialDidaticoViewModel

class MaterialDidaticoViewModelFactory(private val repository: MaterialDidaticoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaterialDidaticoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MaterialDidaticoViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe de ViewModel desconhecida")
    }
}
