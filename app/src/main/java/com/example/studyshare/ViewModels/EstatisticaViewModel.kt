package com.example.studyshare.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyshare.DataClasses.Estatistica
import com.example.studyshare.Repositories.EstatisticaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EstatisticaViewModel(private val repository: EstatisticaRepository) : ViewModel() {

    private val _estatisticas = MutableStateFlow<List<Estatistica>>(emptyList())
    val estatisticas: StateFlow<List<Estatistica>> = _estatisticas

    private val _estatisticaSelecionada = MutableStateFlow<Estatistica?>(null)
    val estatisticaSelecionada: StateFlow<Estatistica?> = _estatisticaSelecionada

    private val _erroMensagem = MutableStateFlow<String?>(null)
    val erroMensagem: StateFlow<String?> = _erroMensagem

    private val _estatisticaCriada = MutableStateFlow<Boolean?>(null)
    val estatisticaCriada: StateFlow<Boolean?> = _estatisticaCriada

    fun carregarEstatisticas() {
        viewModelScope.launch {
            try {
                _estatisticas.value = repository.getEstatisticas()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar estatísticas: ${e.message}"
            }
        }
    }

    fun carregarEstatisticaPorId(id: Int) {
        viewModelScope.launch {
            try {
                _estatisticaSelecionada.value = repository.getEstatisticaById(id)
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar estatística: ${e.message}"
            }
        }
    }

    fun criarEstatistica(estatistica: Estatistica) {
        viewModelScope.launch {
            try {
                repository.criarEstatistica(estatistica)
                _estatisticaCriada.value = true
                carregarEstatisticas()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao criar estatística: ${e.message}"
                _estatisticaCriada.value = false
            }
        }
    }

    fun apagarEstatistica(id: Int) {
        viewModelScope.launch {
            try {
                repository.apagarEstatistica(id)
                carregarEstatisticas()
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao apagar estatística: ${e.message}"
            }
        }
    }

    fun resetErro() {
        _erroMensagem.value = null
    }
}
