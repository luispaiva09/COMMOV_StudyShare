package com.example.studyshare.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studyshare.Repositories.MensagemDiscussaoRepository
import com.example.studyshare.ViewModels.MensagemDiscussaoViewModel

class MensagemDiscussaoViewModelFactory(private val repository: MensagemDiscussaoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MensagemDiscussaoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MensagemDiscussaoViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe de ViewModel desconhecida")
    }
}
