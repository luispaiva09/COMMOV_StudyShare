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

    private val _utilizadorPerfil = MutableStateFlow<Utilizador?>(null)
    val utilizadorPerfil: StateFlow<Utilizador?> = _utilizadorPerfil

    private val _updateSucesso = MutableStateFlow<Boolean?>(null)
    val updateSucesso: StateFlow<Boolean?> = _updateSucesso

    private val _utilizadores = MutableStateFlow<List<Utilizador>>(emptyList())
    val utilizadores: StateFlow<List<Utilizador>> = _utilizadores

    // Função para registar utilizador
    fun registarUtilizador(utilizador: Utilizador) {
        viewModelScope.launch {
            try {
                val usernameExiste = repository.verificarUsername(utilizador.username)
                if (usernameExiste) {
                    _registoSucesso.value = false
                    _erroMensagem.value = "Username já existe."
                    return@launch
                }

                val emailExiste = repository.verificarEmail(utilizador.email)
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

    // Função para login
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

    // Buscar utilizador por id
    fun getUtilizadorById(id: Int) {
        viewModelScope.launch {
            try {
                val utilizador = repository.getUtilizadorById(id)
                _utilizadorPerfil.value = utilizador
                _erroMensagem.value = null
            } catch (e: Exception) {
                _utilizadorPerfil.value = null
                _erroMensagem.value = e.message ?: "Erro ao carregar utilizador."
            }
        }
    }

    // Buscar lista de utilizadores
    fun getUtilizadores() {
        viewModelScope.launch {
            try {
                val lista = repository.getUtilizadores()
                _utilizadores.value = lista
                _erroMensagem.value = null
            } catch (e: Exception) {
                _erroMensagem.value = e.message ?: "Erro ao carregar utilizadores."
            }
        }
    }

    fun updateUtilizador(id: Int, utilizador: Utilizador) {
        viewModelScope.launch {
            try {
                val sucesso = repository.updateUtilizador(id, utilizador)
                _updateSucesso.value = sucesso
                if (sucesso) {
                    _utilizadorPerfil.value = utilizador
                    _erroMensagem.value = null
                } else {
                    _erroMensagem.value = "Falha ao atualizar utilizador."
                }
            } catch (e: Exception) {
                _updateSucesso.value = false
                _erroMensagem.value = e.message ?: "Erro ao atualizar utilizador."
            }
        }
    }

    // Função auxiliar para atualizar campos específicos do utilizador
    fun atualizarUtilizador(
        id: Int,
        nome: String,
        username: String,
        email: String,
        telefone: Int
    ) {
        val utilizadorAtual = _utilizadorPerfil.value
        if (utilizadorAtual == null) {
            _erroMensagem.value = "Utilizador não carregado."
            _updateSucesso.value = false
            return
        }

        val utilizadorAtualizado = Utilizador(
            id = id,
            username = username,
            email = email,
            password = utilizadorAtual.password,
            tipo_utilizador = utilizadorAtual.tipo_utilizador,
            estado = utilizadorAtual.estado,
            nome = nome,
            n_telemovel = telefone,
            data_registo = utilizadorAtual.data_registo,
            morada = utilizadorAtual.morada,
            ultimo_login = utilizadorAtual.ultimo_login,
            foto_perfil_url = utilizadorAtual.foto_perfil_url
        )
        updateUtilizador(id, utilizadorAtualizado)
    }

    fun updateUtilizadorParcial(id: Int, updates: Map<String, Any>) {
        viewModelScope.launch {
            try {
                val sucesso = repository.updateUtilizadorParcial(id, updates)
                _updateSucesso.value = sucesso
                if (sucesso) {
                    // Atualiza o utilizador local, recarregando do backend por exemplo
                    getUtilizadorById(id)
                    _erroMensagem.value = null
                } else {
                    _erroMensagem.value = "Falha ao atualizar utilizador."
                }
            } catch (e: Exception) {
                _updateSucesso.value = false
                _erroMensagem.value = e.message ?: "Erro ao atualizar utilizador."
            }
        }
    }


    // Resetar estados
    fun resetEstado() {
        _registoSucesso.value = null
        _updateSucesso.value = null
        _erroMensagem.value = null
    }
}
