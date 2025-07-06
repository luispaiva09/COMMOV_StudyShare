package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.SessaoEstudo
import retrofit2.Response

class SessaoEstudoRepository(private val api: ApiService) {

    suspend fun getSessoes(): List<SessaoEstudo> {
        return api.getSessoesEstudo()
    }

    suspend fun getSessaoById(id: Int): SessaoEstudo? {
        val lista = api.getSessaoEstudoById("eq.$id")
        return lista.firstOrNull()
    }

    suspend fun criarSessao(sessaoEstudo: SessaoEstudo): List<SessaoEstudo> {
        return api.createSessaoEstudo(sessaoEstudo)
    }

    suspend fun atualizarSessao(id: Int, sessaoEstudo: SessaoEstudo): SessaoEstudo? {
        val lista =  api.updateSessaoEstudo("eq.$id", sessaoEstudo)
        return lista.firstOrNull()
    }

    suspend fun apagarSessao(id: Int): Response<Unit> {
        return api.deleteSessaoEstudo("eq.$id")
    }

    suspend fun getSessoesByCriador(criadorId: Int): List<SessaoEstudo> {
        return api.getSessoesEstudoByCriador("eq.$criadorId")
    }

}