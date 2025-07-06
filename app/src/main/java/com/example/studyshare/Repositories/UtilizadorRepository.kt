package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Utilizador
import com.example.studyshare.LoginRequest
import com.example.studyshare.LoginResponse

class UtilizadorRepository(private val api: ApiService) {

    suspend fun registarUtilizador(utilizador: Utilizador) =
        api.createUtilizador(utilizador)

    suspend fun verificarUsername(username: String): Boolean {
        val resultado = api.verificarUsernameExistente(username)
        return resultado.isNotEmpty()
    }

    suspend fun verificarEmail(email: String): Boolean {
        val resultado = api.verificarEmailExistente(email)
        return resultado.isNotEmpty()
    }

    suspend fun loginUtilizador(username: String, password: String): LoginResponse? {
        val response = api.loginUtilizador(LoginRequest(p_username = username, p_password = password))
        return response.firstOrNull()
    }

    suspend fun getUtilizadores(): List<Utilizador> =
        api.getUtilizadores()

    suspend fun getUtilizadorById(id: Int): Utilizador? {
        val lista = api.getUtilizadorById("eq.$id")
        return lista.firstOrNull()
    }

    suspend fun updateUtilizadorParcial(id: Int, updates: Map<String, Any>): Boolean {
        val listaAtualizada = api.updateUtilizadorParcial(id.toString(), updates)
        return listaAtualizada.isNotEmpty()
    }

    suspend fun updateUtilizador(id: Int, utilizador: Utilizador): Boolean {
        val updates = mutableMapOf<String, Any>()

        utilizador.username?.let { updates["username"] = it }
        utilizador.email?.let { updates["email"] = it }
        utilizador.password?.let { updates["password"] = it }
        utilizador.tipo_utilizador?.let { updates["tipo_utilizador"] = it }
        utilizador.estado?.let { updates["estado"] = it }
        utilizador.nome?.let { updates["nome"] = it }
        utilizador.n_telemovel?.let { updates["n_telemovel"] = it }
        utilizador.data_registo?.let { updates["data_registo"] = it }
        utilizador.morada?.let { updates["morada"] = it }
        utilizador.ultimo_login?.let { updates["ultimo_login"] = it }
        utilizador.foto_perfil_url?.let { updates["foto_perfil_url"] = it }

        val listaAtualizada = api.updateUtilizadorParcial(id.toString(), updates)
        return listaAtualizada.isNotEmpty()
    }

    suspend fun deleteUtilizador(id: Int) =
        api.deleteUtilizador(id.toString())

    suspend fun alterarPassword(id: Int, oldPassword: String, newPassword: String): Boolean {
        val request = com.example.studyshare.AlterarPasswordRequest(
            p_user_id = id,
            p_old_password = oldPassword,
            p_new_password = newPassword
        )
        val response = api.alterarPassword(request)
        return response.isSuccessful
    }
}
