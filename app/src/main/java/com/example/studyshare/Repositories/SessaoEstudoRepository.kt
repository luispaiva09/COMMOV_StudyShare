package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.SessaoEstudo

class SessaoEstudoRepository(private val api: ApiService) {

    suspend fun getSessoes(): List<SessaoEstudo> {
        return api.getSessoesEstudo()
    }

    suspend fun getSessaoById(id: Int): SessaoEstudo? {
        val filtro = "eq.$id"
        val sessoes = api.getSessaoEstudoById(filtro)
        return sessoes.firstOrNull()
    }

    suspend fun criarSessao(sessao: SessaoEstudo): Boolean {
        val resultado = api.createSessaoEstudo(sessao)
        return resultado.isNotEmpty()
    }

    suspend fun atualizarSessao(id: Int, sessao: SessaoEstudo): Boolean {
        val resultado = api.updateSessaoEstudo("eq.$id", sessao)
        return resultado.isNotEmpty()
    }

    suspend fun apagarSessao(id: Int) {
        api.deleteSessaoEstudo("eq.$id")
    }

    suspend fun getSessoesByCriador(filtro: String): List<SessaoEstudo> {
        return api.getSessoesEstudoByCriador(filtro)
    }
}
