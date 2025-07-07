package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Estatistica
import retrofit2.Response

class EstatisticaRepository(private val api: ApiService) {

    suspend fun getEstatisticas(): List<Estatistica> {
        return api.getEstatisticas()
    }

    suspend fun getEstatisticaById(utilizadorId: Int): Estatistica? {
        val lista = api.getEstatisticaById("eq.$utilizadorId")
        return lista.firstOrNull()
    }

    suspend fun updateEstatistica(userId: Int, estatistica: Estatistica) {
        val response = api.updateEstatistica(userId, estatistica)
        if (!response.isSuccessful) {
            throw Exception("Falha ao atualizar estatística: ${response.code()}")
        }
    }

    suspend fun createEstatistica(estatistica: Estatistica): Estatistica {
        return api.createEstatistica(estatistica)
    }
}
