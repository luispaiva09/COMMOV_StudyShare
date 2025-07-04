package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.ParticipanteSessao
import retrofit2.Response

class ParticipanteSessaoRepository(private val api: ApiService) {

    suspend fun getParticipantes(): List<ParticipanteSessao> {
        return api.getParticipantesSessao()
    }

    suspend fun getParticipanteById(id: Int): ParticipanteSessao? {
        val lista = api.getParticipanteSessaoById("eq.$id")
        return lista.firstOrNull()
    }

    suspend fun criarParticipante(participanteSessao: ParticipanteSessao): List<ParticipanteSessao> {
        return api.createParticipanteSessao(participanteSessao)
    }

    suspend fun apagarParticipante(id: Int): Response<Unit> {
        return api.deleteParticipanteSessao("eq.$id")
    }


}