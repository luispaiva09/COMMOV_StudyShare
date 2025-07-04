package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Estatistica
import retrofit2.Response

class EstatisticaRepository(private val api: ApiService) {

    suspend fun getEstatisticas(): List<Estatistica> {
        return api.getEstatisticas()
    }

    suspend fun getEstatisticaById(id: Int): Estatistica? {
        val lista = api.getEstatisticaById("eq.$id")
        return lista.firstOrNull()
    }

    suspend fun criarEstatistica(estatistica: Estatistica): List<Estatistica> {
        return api.createEstatistica(estatistica)
    }

    suspend fun apagarEstatistica(id: Int): Response<Unit> {
        return api.deleteEstatistica("eq.$id")
    }
}
