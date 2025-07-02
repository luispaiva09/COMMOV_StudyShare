package com.example.studyshare.Repositories
import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Utilizador
import com.example.studyshare.LoginRequest
import com.example.studyshare.LoginResponse
import retrofit2.Response

class UtilizadorRepository(private val api: ApiService) {

    suspend fun registarUtilizador(utilizador: Utilizador): Response<Unit> {
        return api.createUtilizador(utilizador)
    }

    suspend fun verificarUsername(username: String): Boolean {
        val resultado = api.verificarUsernameExistente("eq.$username")
        return resultado.isNotEmpty()
    }

    suspend fun verificarEmail(email: String): Boolean {
        val resultado = api.verificarEmailExistente("eq.$email")
        return resultado.isNotEmpty()
    }

    suspend fun loginUtilizador(username: String, password: String): LoginResponse? {
        val response = api.loginUtilizador(LoginRequest(p_username = username, p_password = password))
        return if (response.isNotEmpty()) response[0] else null
    }

}
