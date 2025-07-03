package com.example.studyshare.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studyshare.Repositories.SyncOfflineRepository
import com.example.studyshare.ViewModels.SyncOfflineViewModel

class SyncOfflineViewModelFactory(private val repository: SyncOfflineRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SyncOfflineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SyncOfflineViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe de ViewModel desconhecida")
    }
}