package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Avaliacao
import retrofit2.Response

class AvaliacaoRepository(private val api: ApiService) {

    suspend fun getAvaliacoes(): List<Avaliacao> {
        return api.getAvaliacoes()
    }

    suspend fun getAvaliacaoById(id: Int): Avaliacao? {
        val lista = api.getAvaliacaoById("eq.$id")
        return lista.firstOrNull()
    }

    suspend fun criarAvaliacao(avaliacao: Avaliacao): List<Avaliacao> {
        return api.createAvaliacao(avaliacao)
    }

    suspend fun atualizarAvaliacao(id: Int, avaliacao: Avaliacao): Avaliacao? {
        val lista = api.updateAvaliacao("eq.$id", avaliacao)
        return lista.firstOrNull()
    }

    suspend fun apagarAvaliacao(id: Int): Response<Unit> {
        return api.deleteAvaliacao("eq.$id")
    }
}