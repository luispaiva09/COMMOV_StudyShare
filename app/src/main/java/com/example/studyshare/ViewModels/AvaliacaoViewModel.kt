package com.example.studyshare.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyshare.DataClasses.Avaliacao
import com.example.studyshare.Repositories.AvaliacaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AvaliacaoViewModel(private val repository: AvaliacaoRepository) : ViewModel() {

    private val _avaliacoes = MutableStateFlow<List<Avaliacao>>(emptyList())
    val avaliacoes: StateFlow<List<Avaliacao>> = _avaliacoes

    private val _avaliacaoSelecionada = MutableStateFlow<Avaliacao?>(null)
    val avaliacaoSelecionada: StateFlow<Avaliacao?> = _avaliacaoSelecionada

    private val _erroMensagem = MutableStateFlow<String?>(null)
    val erroMensagem: StateFlow<String?> = _erroMensagem

    private val _avaliacaoCriada = MutableStateFlow<Boolean?>(null)
    val avaliacaoCriada: StateFlow<Boolean?> = _avaliacaoCriada

    fun carregarAvaliacoes() {
        viewModelScope.launch {
            try {
                _avaliacoes.value = repository.getAvaliacoes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar avaliações: ${e.message}"
            }
        }
    }

    fun carregarAvaliacaoPorId(id: Int) {
        viewModelScope.launch {
            try {
                _avaliacaoSelecionada.value = repository.getAvaliacaoById(id)
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar avaliação: ${e.message}"
            }
        }
    }

    fun criarAvaliacao(avaliacao: Avaliacao) {
        viewModelScope.launch {
            try {
                repository.criarAvaliacao(avaliacao)
                _avaliacaoCriada.value = true
                carregarAvaliacoes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao criar avaliação: ${e.message}"
                _avaliacaoCriada.value = false
            }
        }
    }

    fun atualizarAvaliacao(id: Int, avaliacao: Avaliacao) {
        viewModelScope.launch {
            try {
                repository.atualizarAvaliacao(id, avaliacao)
                carregarAvaliacoes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao atualizar avaliação: ${e.message}"
            }
        }
    }

    fun apagarAvaliacao(id: Int) {
        viewModelScope.launch {
            try {
                repository.apagarAvaliacao(id)
                carregarAvaliacoes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao apagar avaliação: ${e.message}"
            }
        }
    }

    fun resetErro() {
        _erroMensagem.value = null
    }
}
