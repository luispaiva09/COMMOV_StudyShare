package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Estatistica

class EstatisticaRepository(private val api: ApiService) {

    suspend fun getEstatisticas(): List<Estatistica> {
        return api.getEstatisticas()
    }

    suspend fun getEstatisticaById(id: Int): Estatistica {
        return api.getEstatisticaById(id)
    }

    suspend fun criarEstatistica(estatistica: Estatistica): Estatistica {
        return api.createEstatistica(estatistica)
    }

    suspend fun apagarEstatistica(id: Int) {
        api.deleteEstatistica(id)
    }
}
