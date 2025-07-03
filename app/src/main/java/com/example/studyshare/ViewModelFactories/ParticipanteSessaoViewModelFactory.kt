package com.example.studyshare.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studyshare.Repositories.ParticipanteSessaoRepository
import com.example.studyshare.ViewModels.ParticipanteSessaoViewModel

class ParticipanteSessaoViewModelFactory(
    private val repository: ParticipanteSessaoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParticipanteSessaoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParticipanteSessaoViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe de ViewModel desconhecida")
    }
}
