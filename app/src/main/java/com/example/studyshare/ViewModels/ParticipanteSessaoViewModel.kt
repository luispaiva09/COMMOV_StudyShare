package com.example.studyshare.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyshare.DataClasses.ParticipanteSessao
import com.example.studyshare.Repositories.ParticipanteSessaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ParticipanteSessaoViewModel(private val repository: ParticipanteSessaoRepository) : ViewModel() {

    private val _participantes = MutableStateFlow<List<ParticipanteSessao>>(emptyList())
    val participantes: StateFlow<List<ParticipanteSessao>> = _participantes

    private val _participanteSelecionado = MutableStateFlow<ParticipanteSessao?>(null)
    val participanteSelecionado: StateFlow<ParticipanteSessao?> = _participanteSelecionado

    private val _erroMensagem = MutableStateFlow<String?>(null)
    val erroMensagem: StateFlow<String?> = _erroMensagem

    private val _participanteCriado = MutableStateFlow<Boolean?>(null)
    val participanteCriado: StateFlow<Boolean?> = _participanteCriado

    fun carregarParticipantes() {
        viewModelScope.launch {
            try {
                _participantes.value = repository.getParticipantes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar participantes: ${e.message}"
            }
        }
    }

    fun carregarParticipantePorId(id: Int) {
        viewModelScope.launch {
            try {
                _participanteSelecionado.value = repository.getParticipanteById(id)
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar participante: ${e.message}"
            }
        }
    }

    fun criarParticipante(participante: ParticipanteSessao) {
        viewModelScope.launch {
            try {
                repository.criarParticipante(participante)
                _participanteCriado.value = true
                carregarParticipantes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao criar participante: ${e.message}"
                _participanteCriado.value = false
            }
        }
    }

    fun apagarParticipante(id: Int) {
        viewModelScope.launch {
            try {
                repository.apagarParticipante(id)
                carregarParticipantes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao apagar participante: ${e.message}"
            }
        }
    }

    fun resetErro() {
        _erroMensagem.value = null
    }
}
