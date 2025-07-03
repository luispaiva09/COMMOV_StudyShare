package com.example.studyshare.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studyshare.Repositories.ComentarioRepository
import com.example.studyshare.ViewModels.ComentarioViewModel

class ComentarioViewModelFactory(private val repository: ComentarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComentarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComentarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe de ViewModel desconhecida")
    }
}
