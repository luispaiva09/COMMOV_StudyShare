package com.example.studyshare.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyshare.DataClasses.MensagemDiscussao
import com.example.studyshare.Repositories.MensagemDiscussaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MensagemDiscussaoViewModel(private val repository: MensagemDiscussaoRepository) : ViewModel() {

    private val _mensagens = MutableStateFlow<List<MensagemDiscussao>>(emptyList())
    val mensagens: StateFlow<List<MensagemDiscussao>> = _mensagens

    private val _mensagemSelecionada = MutableStateFlow<MensagemDiscussao?>(null)
    val mensagemSelecionada: StateFlow<MensagemDiscussao?> = _mensagemSelecionada

    private val _erroMensagem = MutableStateFlow<String?>(null)
    val erroMensagem: StateFlow<String?> = _erroMensagem

    private val _mensagemCriada = MutableStateFlow<Boolean?>(null)
    val mensagemCriada: StateFlow<Boolean?> = _mensagemCriada

    fun carregarMensagens() {
        viewModelScope.launch {
            try {
                _mensagens.value = repository.getMensagens()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar mensagens: ${e.message}"
            }
        }
    }

    fun carregarMensagemPorId(id: Int) {
        viewModelScope.launch {
            try {
                _mensagemSelecionada.value = repository.getMensagemById(id)
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar mensagem: ${e.message}"
            }
        }
    }

    fun criarMensagem(mensagem: MensagemDiscussao) {
        viewModelScope.launch {
            try {
                repository.criarMensagem(mensagem)
                _mensagemCriada.value = true
                carregarMensagens()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao criar mensagem: ${e.message}"
                _mensagemCriada.value = false
            }
        }
    }

    fun atualizarMensagem(id: Int, mensagem: MensagemDiscussao) {
        viewModelScope.launch {
            try {
                repository.atualizarMensagem(id, mensagem)
                carregarMensagens()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao atualizar mensagem: ${e.message}"
            }
        }
    }

    fun apagarMensagem(id: Int) {
        viewModelScope.launch {
            try {
                repository.apagarMensagem(id)
                carregarMensagens()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao apagar mensagem: ${e.message}"
            }
        }
    }

    fun resetErro() {
        _erroMensagem.value = null
    }
}
