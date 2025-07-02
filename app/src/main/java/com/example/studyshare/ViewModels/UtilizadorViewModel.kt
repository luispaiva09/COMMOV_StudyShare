package com.example.studyshare.ViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyshare.DataClasses.Utilizador
import com.example.studyshare.LoginResponse
import com.example.studyshare.Repositories.UtilizadorRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UtilizadorViewModel(private val repository: UtilizadorRepository) : ViewModel() {

    private val _registoSucesso = MutableStateFlow<Boolean?>(null)
    val registoSucesso: StateFlow<Boolean?> = _registoSucesso

    private val _erroMensagem = MutableStateFlow<String?>(null)
    val erroMensagem: StateFlow<String?> = _erroMensagem

    private val _utilizadorLogado = MutableStateFlow<LoginResponse?>(null)
    val utilizadorLogado: StateFlow<LoginResponse?> = _utilizadorLogado

    fun registarUtilizador(utilizador: Utilizador) {
        viewModelScope.launch {
            try {
                val usernameExiste = repository.verificarUsername(utilizador.username)
                val emailExiste = repository.verificarEmail(utilizador.email)

                if (usernameExiste) {
                    _registoSucesso.value = false
                    _erroMensagem.value = "Username já existe."
                    return@launch
                }

                if (emailExiste) {
                    _registoSucesso.value = false
                    _erroMensagem.value = "Email já existe."
                    return@launch
                }

                val response = repository.registarUtilizador(utilizador)

                if (response.isSuccessful) {
                    _registoSucesso.value = true
                    _erroMensagem.value = null
                } else {
                    _registoSucesso.value = false
                    _erroMensagem.value = "Falha ao registar utilizador."
                }

            } catch (e: Exception) {
                _registoSucesso.value = false
                _erroMensagem.value = e.message ?: "Erro desconhecido"
            }
        }
    }

    fun loginUtilizador(username: String, password: String) {
        viewModelScope.launch {
            try {
                val utilizador = repository.loginUtilizador(username, password)
                if (utilizador != null) {
                    _utilizadorLogado.value = utilizador
                    _erroMensagem.value = null
                } else {
                    _utilizadorLogado.value = null
                    _erroMensagem.value = "Credenciais inválidas."
                }
            } catch (e: Exception) {
                _utilizadorLogado.value = null
                _erroMensagem.value = e.message ?: "Erro no login."
            }
        }
    }

    fun resetEstado() {
        _registoSucesso.value = null
        _erroMensagem.value = null
    }
}
