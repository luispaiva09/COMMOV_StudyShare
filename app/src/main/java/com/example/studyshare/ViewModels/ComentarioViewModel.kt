package com.example.studyshare.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyshare.DataClasses.Comentario
import com.example.studyshare.Repositories.ComentarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ComentarioViewModel(private val repository: ComentarioRepository) : ViewModel() {

    private val _comentarios = MutableStateFlow<List<Comentario>>(emptyList())
    val comentarios: StateFlow<List<Comentario>> = _comentarios

    private val _comentarioSelecionado = MutableStateFlow<Comentario?>(null)
    val comentarioSelecionado: StateFlow<Comentario?> = _comentarioSelecionado

    private val _erroMensagem = MutableStateFlow<String?>(null)
    val erroMensagem: StateFlow<String?> = _erroMensagem

    private val _comentarioCriado = MutableStateFlow<Boolean?>(null)
    val comentarioCriado: StateFlow<Boolean?> = _comentarioCriado

    fun carregarComentarios() {
        viewModelScope.launch {
            try {
                _comentarios.value = repository.getComentarios()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar comentários: ${e.message}"
            }
        }
    }

    fun carregarComentarioPorId(id: Int) {
        viewModelScope.launch {
            try {
                _comentarioSelecionado.value = repository.getComentarioById(id)
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar comentário: ${e.message}"
            }
        }
    }

    fun criarComentario(comentario: Comentario) {
        viewModelScope.launch {
            try {
                repository.criarComentario(comentario)
                _comentarioCriado.value = true
                carregarComentarios()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao criar comentário: ${e.message}"
                _comentarioCriado.value = false
            }
        }
    }

    fun atualizarComentario(id: Int, comentario: Comentario) {
        viewModelScope.launch {
            try {
                repository.atualizarComentario(id, comentario)
                carregarComentarios()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao atualizar comentário: ${e.message}"
            }
        }
    }

    fun apagarComentario(id: Int) {
        viewModelScope.launch {
            try {
                repository.apagarComentario(id)
                carregarComentarios()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao apagar comentário: ${e.message}"
            }
        }
    }

    fun resetErro() {
        _erroMensagem.value = null
    }
}
