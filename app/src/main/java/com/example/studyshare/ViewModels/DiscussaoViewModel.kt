package com.example.studyshare.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyshare.DataClasses.Discussao
import com.example.studyshare.Repositories.DiscussaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiscussaoViewModel(private val repository: DiscussaoRepository) : ViewModel() {

    private val _discussoes = MutableStateFlow<List<Discussao>>(emptyList())
    val discussoes: StateFlow<List<Discussao>> = _discussoes

    private val _discussaoSelecionada = MutableStateFlow<Discussao?>(null)
    val discussaoSelecionada: StateFlow<Discussao?> = _discussaoSelecionada

    private val _erroMensagem = MutableStateFlow<String?>(null)
    val erroMensagem: StateFlow<String?> = _erroMensagem

    private val _discussaoCriada = MutableStateFlow<Boolean?>(null)
    val discussaoCriada: StateFlow<Boolean?> = _discussaoCriada

    fun carregarDiscussoes() {
        viewModelScope.launch {
            try {
                _discussoes.value = repository.getDiscussoes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar discussões: ${e.message}"
            }
        }
    }

    fun carregarDiscussaoPorId(id: Int) {
        viewModelScope.launch {
            try {
                _discussaoSelecionada.value = repository.getDiscussaoById(id)
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar discussão: ${e.message}"
            }
        }
    }

    fun criarDiscussao(discussao: Discussao) {
        viewModelScope.launch {
            try {
                repository.criarDiscussao(discussao)
                _discussaoCriada.value = true
                carregarDiscussoes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao criar discussão: ${e.message}"
                _discussaoCriada.value = false
            }
        }
    }

    fun atualizarDiscussao(id: Int, discussao: Discussao) {
        viewModelScope.launch {
            try {
                repository.atualizarDiscussao(id, discussao)
                carregarDiscussoes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao atualizar discussão: ${e.message}"
            }
        }
    }

    fun apagarDiscussao(id: Int) {
        viewModelScope.launch {
            try {
                repository.apagarDiscussao(id)
                carregarDiscussoes()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao apagar discussão: ${e.message}"
            }
        }
    }


    fun carregarDiscussoesByCriador(criadorId: Int) {
        viewModelScope.launch {
            try {
                val discussoesDoCriador = repository.getDiscussoesByCriador(criadorId)
                _discussoes.value = discussoesDoCriador
            } catch (e: Exception) {
                _erroMensagem.value = e.message ?: "Erro desconhecido"
            }
        }
    }

    fun carregarUltimasDiscussoes() {
        viewModelScope.launch {
            try {
                val ultimasDiscussoes = repository.getDiscussoes() // busca lista geral de discussões
                _discussoes.value = ultimasDiscussoes
            } catch (e: Exception) {
                _erroMensagem.value = e.message ?: "Erro desconhecido"
            }
        }
    }

    fun carregarUltimasDiscussoesDoCriador(criadorId: Int) {
        viewModelScope.launch {
            try {
                val discussoesDoCriador = repository.getUltimasDiscussoesDoCriador(criadorId)
                _discussoes.value = discussoesDoCriador
            } catch (e: Exception) {
                _erroMensagem.value = e.message
            }
        }
    }

    fun resetErro() {
        _erroMensagem.value = null
    }
}
