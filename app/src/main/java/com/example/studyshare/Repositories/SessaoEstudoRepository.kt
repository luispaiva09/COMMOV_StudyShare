package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.SessaoEstudo

class SessaoEstudoRepository(private val api: ApiService) {

    suspend fun getSessoes(): List<SessaoEstudo> {
        return api.getSessoesEstudo()
    }

    suspend fun getSessaoById(id: Int): SessaoEstudo {
        return api.getSessaoEstudoById(id)
    }

    suspend fun criarSessao(sessaoEstudo: SessaoEstudo): SessaoEstudo {
        return api.createSessaoEstudo(sessaoEstudo)
    }

    suspend fun atualizarSessao(id: Int, sessaoEstudo: SessaoEstudo): SessaoEstudo {
        return api.updateSessaoEstudo(id, sessaoEstudo)
    }

    suspend fun apagarSessao(id: Int) {
        api.deleteSessaoEstudo(id)
    }
}