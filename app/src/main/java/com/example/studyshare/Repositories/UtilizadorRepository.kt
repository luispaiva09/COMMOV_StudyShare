package com.example.studyshare.Repositories
import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Utilizador

class UtilizadorRepository(private val api: ApiService) {

    suspend fun registarUtilizador(utilizador: Utilizador): Utilizador {
        return api.createUtilizador(utilizador)
    }

}
