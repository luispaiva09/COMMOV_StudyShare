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

    suspend fun getUtilizadores(): List<Utilizador> {
        return api.getUtilizadores()
    }

    suspend fun getUtilizadorById(id: Int): Utilizador? {
        val lista = api.getUtilizadorById("eq.$id")
        return lista.firstOrNull()
    }

    suspend fun updateUtilizador(id: Int, utilizador: Utilizador): Utilizador? {
        val lista = api.updateUtilizador("eq.$id", utilizador)
        return lista.firstOrNull()
    }

    suspend fun deleteUtilizador(id: Int): Response<Unit> {
        return api.deleteUtilizador("eq.$id")
    }

}
