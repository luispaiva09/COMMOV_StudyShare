package com.example.studyshare.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studyshare.Repositories.SessaoEstudoRepository
import com.example.studyshare.ViewModels.SessaoEstudoViewModel

class SessaoEstudoViewModelFactory(private val repository: SessaoEstudoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessaoEstudoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessaoEstudoViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe de ViewModel desconhecida")
    }
}
