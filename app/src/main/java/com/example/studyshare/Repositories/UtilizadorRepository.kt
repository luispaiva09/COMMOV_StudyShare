package com.example.studyshare.Repositories
import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Utilizador
import retrofit2.Response

class UtilizadorRepository(private val api: ApiService) {

    suspend fun registarUtilizador(utilizador: Utilizador): Response<Unit> {
        return api.createUtilizador(utilizador)
    }

}
