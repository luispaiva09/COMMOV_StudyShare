package com.example.studyshare.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studyshare.Repositories.EstatisticaRepository
import com.example.studyshare.ViewModels.EstatisticaViewModel

class EstatisticaViewModelFactory(private val repository: EstatisticaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstatisticaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstatisticaViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe de ViewModel desconhecida")
    }
}
