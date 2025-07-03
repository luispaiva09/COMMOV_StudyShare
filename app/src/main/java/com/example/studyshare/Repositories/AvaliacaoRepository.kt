package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Avaliacao

class AvaliacaoRepository(private val api: ApiService) {

    suspend fun getAvaliacoes(): List<Avaliacao> {
        return api.getAvaliacoes()
    }

    suspend fun getAvaliacaoById(id: Int): Avaliacao {
        return api.getAvaliacaoById(id)
    }

    suspend fun criarAvaliacao(avaliacao: Avaliacao): Avaliacao {
        return api.createAvaliacao(avaliacao)
    }

    suspend fun atualizarAvaliacao(id: Int, avaliacao: Avaliacao): Avaliacao {
        return api.updateAvaliacao(id, avaliacao)
    }

    suspend fun apagarAvaliacao(id: Int) {
        api.deleteAvaliacao(id)
    }
}