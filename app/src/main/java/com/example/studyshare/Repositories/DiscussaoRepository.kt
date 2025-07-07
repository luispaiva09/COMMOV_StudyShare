package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Discussao
import retrofit2.Response

class DiscussaoRepository(private val api: ApiService) {

    suspend fun getDiscussoes(): List<Discussao> {
        return api.getDiscussoes()
    }

    suspend fun getDiscussaoById(id: Int): Discussao? {
        val lista = api.getDiscussaoById("eq.$id")
        return lista.firstOrNull()
    }

    suspend fun criarDiscussao(discussao: Discussao): List<Discussao> {
        return api.createDiscussao(discussao)
    }

    suspend fun atualizarDiscussao(id: Int, discussao: Discussao): Discussao? {
        val lista = api.updateDiscussao("eq.$id", discussao)
        return lista.firstOrNull()
    }

    suspend fun apagarDiscussao(id: Int): Response<Unit> {
        return api.deleteDiscussao("eq.$id")
    }

    suspend fun getDiscussoesByCriador(criadorId: Int): List<Discussao> {
        return api.getDiscussoesByCriador("eq.$criadorId")
    }

    suspend fun getUltimasDiscussoesDoCriador(criadorId: Int): List<Discussao> {
        return api.getUltimasDiscussoesDoCriador("eq.$criadorId")
    }

}
