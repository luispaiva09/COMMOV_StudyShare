package com.example.studyshare.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyshare.DataClasses.SyncOffline
import com.example.studyshare.Repositories.SyncOfflineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SyncOfflineViewModel(private val repository: SyncOfflineRepository) : ViewModel() {

    private val _syncsOffline = MutableStateFlow<List<SyncOffline>>(emptyList())
    val syncsOffline: StateFlow<List<SyncOffline>> = _syncsOffline

    private val _syncOfflineSelecionado = MutableStateFlow<SyncOffline?>(null)
    val syncOfflineSelecionado: StateFlow<SyncOffline?> = _syncOfflineSelecionado

    private val _erroMensagem = MutableStateFlow<String?>(null)
    val erroMensagem: StateFlow<String?> = _erroMensagem

    private val _syncCriado = MutableStateFlow<Boolean?>(null)
    val syncCriado: StateFlow<Boolean?> = _syncCriado

    fun carregarSyncsOffline() {
        viewModelScope.launch {
            try {
                _syncsOffline.value = repository.getSyncsOffline()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar sincronizações: ${e.message}"
            }
        }
    }

    fun carregarSyncOfflinePorId(id: Int) {
        viewModelScope.launch {
            try {
                _syncOfflineSelecionado.value = repository.getSyncOfflineById(id)
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar sincronização: ${e.message}"
            }
        }
    }

    fun criarSyncOffline(syncOffline: SyncOffline) {
        viewModelScope.launch {
            try {
                repository.criarSyncOffline(syncOffline)
                _syncCriado.value = true
                carregarSyncsOffline()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao criar sincronização: ${e.message}"
                _syncCriado.value = false
            }
        }
    }

    fun apagarSyncOffline(id: Int) {
        viewModelScope.launch {
            try {
                repository.apagarSyncOffline(id)
                carregarSyncsOffline()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao apagar sincronização: ${e.message}"
            }
        }
    }

    fun resetErro() {
        _erroMensagem.value = null
    }
}
