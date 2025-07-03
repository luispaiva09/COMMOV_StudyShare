package com.example.studyshare.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studyshare.Repositories.AvaliacaoRepository
import com.example.studyshare.ViewModels.AvaliacaoViewModel

class AvaliacaoViewModelFactory(private val repository: AvaliacaoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AvaliacaoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AvaliacaoViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe de ViewModel desconhecida")
    }
}
