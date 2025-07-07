package com.example.studyshare.ViewModels

import android.util.Log
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

    fun carregarEstatisticaPorId(userId: Int) {
        viewModelScope.launch {
            try {
                val estat = repository.getEstatisticaById(userId)
                Log.d("EstatisticaVM", "Estatística carregada: $estat")
                _estatisticaSelecionada.value = estat
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao carregar estatística: ${e.message}"
            }
        }
    }

    private suspend fun garantirEstatistica(userId: Int): Estatistica {
        val estatisticaExistente = repository.getEstatisticaById(userId)
        if (estatisticaExistente == null) {
            val nova = Estatistica(
                utilizador_id = userId,
                comentarios_feitos = 0,
                materiais_partilhados = 0,
                materiais_visualizados = 0,
                horas_em_sessoes = 0.0
            )
            return repository.createEstatistica(nova)
        }
        return estatisticaExistente
    }

    fun incrementarComentariosFeitos(userId: Int) {
        viewModelScope.launch {
            try {
                val atual = garantirEstatistica(userId)
                val nova = atual.copy(comentarios_feitos = atual.comentarios_feitos + 1)
                repository.updateEstatistica(userId, nova)
                _estatisticaSelecionada.value = nova
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao incrementar comentários: ${e.message}"
            }
        }
    }

    fun incrementarMateriaisPartilhados(userId: Int) {
        viewModelScope.launch {
            try {
                val atual = garantirEstatistica(userId)
                val nova = atual.copy(materiais_partilhados = atual.materiais_partilhados + 1)
                repository.updateEstatistica(userId, nova)
                _estatisticaSelecionada.value = nova
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao incrementar materiais partilhados: ${e.message}"
            }
        }
    }

    fun incrementarMateriaisVisualizados(userId: Int) {
        viewModelScope.launch {
            try {
                val atual = garantirEstatistica(userId)
                val nova = atual.copy(materiais_visualizados = atual.materiais_visualizados + 1)
                repository.updateEstatistica(userId, nova)
                _estatisticaSelecionada.value = nova
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao incrementar materiais visualizados: ${e.message}"
            }
        }
    }

    fun incrementarHorasEmSessoes(userId: Int, horasASomar: Double) {
        viewModelScope.launch {
            try {
                val atual = garantirEstatistica(userId)
                val nova = atual.copy(horas_em_sessoes = atual.horas_em_sessoes + horasASomar)
                repository.updateEstatistica(userId, nova)
                _estatisticaSelecionada.value = nova
            } catch (e: Exception) {
                _erroMensagem.value = "Erro ao incrementar horas em sessões: ${e.message}"
            }
        }
    }

    fun resetErro() {
        _erroMensagem.value = null
    }
}

