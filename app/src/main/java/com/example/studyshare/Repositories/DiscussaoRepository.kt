package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Discussao

class DiscussaoRepository(private val api: ApiService) {

    suspend fun getDiscussoes(): List<Discussao> {
        return api.getDiscussoes()
    }

    suspend fun getDiscussaoById(id: Int): Discussao {
        return api.getDiscussaoById(id)
    }

    suspend fun criarDiscussao(discussao: Discussao): Discussao {
        return api.createDiscussao(discussao)
    }

    suspend fun atualizarDiscussao(id: Int, discussao: Discussao): Discussao {
        return api.updateDiscussao(id, discussao)
    }

    suspend fun apagarDiscussao(id: Int) {
        api.deleteDiscussao(id)
    }
}
