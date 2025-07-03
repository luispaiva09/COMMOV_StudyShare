package com.example.studyshare.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studyshare.Repositories.DiscussaoRepository
import com.example.studyshare.ViewModels.DiscussaoViewModel

class DiscussaoViewModelFactory(private val repository: DiscussaoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscussaoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiscussaoViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe de ViewModel desconhecida")
    }
}
