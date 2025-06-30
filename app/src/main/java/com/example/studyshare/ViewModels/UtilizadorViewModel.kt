package com.example.studyshare.ViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyshare.DataClasses.Utilizador
import com.example.studyshare.Repositories.UtilizadorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UtilizadorViewModel(private val repository: UtilizadorRepository) : ViewModel() {

    private val _registoSucesso = MutableStateFlow<Boolean?>(null)
    val registoSucesso: StateFlow<Boolean?> = _registoSucesso

    private val _erroMensagem = MutableStateFlow<String?>(null)
    val erroMensagem: StateFlow<String?> = _erroMensagem

    fun registarUtilizador(utilizador: Utilizador) {
        viewModelScope.launch {
            try {
                repository.registarUtilizador(utilizador)
                _registoSucesso.value = true
                _erroMensagem.value = null
            } catch (e: Exception) {
                _registoSucesso.value = false
                _erroMensagem.value = e.message ?: "Erro desconhecido"
            }
        }
    }

    fun resetEstado() {
        _registoSucesso.value = null
        _erroMensagem.value = null
    }
}
