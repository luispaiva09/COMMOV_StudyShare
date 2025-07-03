package com.example.studyshare.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyshare.DataClasses.Categoria
import com.example.studyshare.Repositories.CategoriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriaViewModel(private val repository: CategoriaRepository) : ViewModel() {

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    private val _categoriaSelecionada = MutableStateFlow<Categoria?>(null)
    val categoriaSelecionada: StateFlow<Categoria?> = _categoriaSelecionada

    private val _erroMensagem = MutableStateFlow<String?>(null)
    val erroMensagem: StateFlow<String?> = _erroMensagem

    private val _categoriaCriada = MutableStateFlow<Boolean?>(null)
    val categoriaCriada: StateFlow<Boolean?> = _categoriaCriada

    fun carregarCategorias() {
        viewModelScope.launch {
            try {
                _categorias.value = repository.getCategorias()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar categorias: ${e.message}"
            }
        }
    }

    fun carregarCategoriaPorId(id: Int) {
        viewModelScope.launch {
            try {
                _categoriaSelecionada.value = repository.getCategoriaById(id)
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar categoria: ${e.message}"
            }
        }
    }

    fun criarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            try {
                repository.criarCategoria(categoria)
                _categoriaCriada.value = true
                carregarCategorias()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao criar categoria: ${e.message}"
                _categoriaCriada.value = false
            }
        }
    }

    fun atualizarCategoria(id: Int, categoria: Categoria) {
        viewModelScope.launch {
            try {
                repository.atualizarCategoria(id, categoria)
                carregarCategorias()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao atualizar categoria: ${e.message}"
            }
        }
    }

    fun apagarCategoria(id: Int) {
        viewModelScope.launch {
            try {
                repository.apagarCategoria(id)
                carregarCategorias()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao apagar categoria: ${e.message}"
            }
        }
    }

    fun resetErro() {
        _erroMensagem.value = null
    }
}
