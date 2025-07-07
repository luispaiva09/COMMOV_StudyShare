package com.example.studyshare.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyshare.DataClasses.MaterialDidatico
import com.example.studyshare.Repositories.MaterialDidaticoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MaterialDidaticoViewModel(private val repository: MaterialDidaticoRepository) : ViewModel() {

    private val _materiais = MutableStateFlow<List<MaterialDidatico>>(emptyList())
    val materiais: StateFlow<List<MaterialDidatico>> = _materiais

    private val _materialSelecionado = MutableStateFlow<MaterialDidatico?>(null)
    val materialSelecionado: StateFlow<MaterialDidatico?> = _materialSelecionado

    private val _erroMensagem = MutableStateFlow<String?>(null)
    val erroMensagem: StateFlow<String?> = _erroMensagem

    private val _materialCriado = MutableStateFlow<Boolean?>(null)
    val materialCriado: StateFlow<Boolean?> = _materialCriado

    fun carregarMateriais() {
        viewModelScope.launch {
            try {
                _materiais.value = repository.getMateriais()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar materiais: ${e.message}"
            }
        }
    }

    fun carregarMaterialPorId(id: Int) {
        viewModelScope.launch {
            try {
                _materialSelecionado.value = repository.getMaterialById(id)
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar material: ${e.message}"
            }
        }
    }

    fun criarMaterial(material: MaterialDidatico) {
        viewModelScope.launch {
            try {
                repository.criarMaterial(material)
                _materialCriado.value = true
                carregarMateriais()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao criar material: ${e.message}"
                _materialCriado.value = false
            }
        }
    }

    fun atualizarMaterial(id: Int, material: MaterialDidatico) {
        viewModelScope.launch {
            try {
                repository.atualizarMaterial(id, material)
                carregarMateriais()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao atualizar material: ${e.message}"
            }
        }
    }

    fun apagarMaterial(id: Int) {
        viewModelScope.launch {
            try {
                repository.apagarMaterial(id)
                carregarMateriais()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao apagar material: ${e.message}"
            }
        }
    }

    fun carregarMateriaisDoAutor(autorId: Int) {
        viewModelScope.launch {
            try {
                val materiaisDoAutor = repository.getMaterialByAutor(autorId)
                _materiais.value = materiaisDoAutor
            } catch (e: Exception) {
                _erroMensagem.value = e.message ?: "Erro desconhecido"
            }
        }
    }

    fun carregarUltimosMateriaisDoAutor(autorId: Int) {
        viewModelScope.launch {
            try {
                val materiaisDoAutor = repository.getUltimosMateriaisDoAutor(autorId)
                _materiais.value = materiaisDoAutor
            } catch (e: Exception) {
                _erroMensagem.value = e.message
            }
        }
    }

    fun resetErro() {
        _erroMensagem.value = null
    }
}
