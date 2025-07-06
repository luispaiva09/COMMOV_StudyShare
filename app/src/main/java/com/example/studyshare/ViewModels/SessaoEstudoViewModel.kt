package com.example.studyshare.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyshare.DataClasses.SessaoEstudo
import com.example.studyshare.Repositories.SessaoEstudoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SessaoEstudoViewModel(private val repository: SessaoEstudoRepository) : ViewModel() {

    private val _sessoes = MutableStateFlow<List<SessaoEstudo>>(emptyList())
    val sessoes: StateFlow<List<SessaoEstudo>> = _sessoes

    private val _sessaoSelecionada = MutableStateFlow<SessaoEstudo?>(null)
    val sessaoSelecionada: StateFlow<SessaoEstudo?> = _sessaoSelecionada

    private val _erroMensagem = MutableStateFlow<String?>(null)
    val erroMensagem: StateFlow<String?> = _erroMensagem

    private val _sessaoCriada = MutableStateFlow<Boolean?>(null)
    val sessaoCriada: StateFlow<Boolean?> = _sessaoCriada

    private val _sessaoDetalhe = MutableStateFlow<SessaoEstudo?>(null)
    val sessaoDetalhe: StateFlow<SessaoEstudo?> = _sessaoDetalhe

    fun carregarSessoes() {
        viewModelScope.launch {
            try {
                _sessoes.value = repository.getSessoes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar sessões: ${e.message}"
            }
        }
    }

    fun carregarSessaoById(id: Int) {
        viewModelScope.launch {
            try {
                val sessao = repository.getSessaoById(id)
                _sessaoDetalhe.value = sessao
                if (sessao == null) {
                    _erroMensagem.value = "Sessão não encontrada"
                }
            } catch (e: Exception) {
                _erroMensagem.value = e.message ?: "Erro desconhecido"
            }
        }
    }

    fun criarSessao(sessao: SessaoEstudo) {
        viewModelScope.launch {
            try {
                repository.criarSessao(sessao)
                _sessaoCriada.value = true
                carregarSessoes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao criar sessão: ${e.message}"
                _sessaoCriada.value = false
            }
        }
    }

    fun atualizarSessao(id: Int, sessao: SessaoEstudo) {
        viewModelScope.launch {
            try {
                repository.atualizarSessao(id, sessao)
                carregarSessoes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao atualizar sessão: ${e.message}"
            }
        }
    }

    fun apagarSessao(id: Int) {
        viewModelScope.launch {
            try {
                repository.apagarSessao(id)
                carregarSessoes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao apagar sessão: ${e.message}"
            }
        }
    }

    fun carregarSessoesByCriador(criadorId: Int) {
        viewModelScope.launch {
            try {
                _sessoes.value = repository.getSessoesByCriador(criadorId)
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar sessões: ${e.message}"
            }
        }
    }


    fun resetErro() {
        _erroMensagem.value = null
    }
}
