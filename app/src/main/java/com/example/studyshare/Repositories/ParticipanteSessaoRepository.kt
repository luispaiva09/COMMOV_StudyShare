package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.ParticipanteSessao

class ParticipanteSessaoRepository(private val api: ApiService) {

    suspend fun getParticipantes(): List<ParticipanteSessao> {
        return api.getParticipantesSessao()
    }

    suspend fun getParticipanteById(id: Int): ParticipanteSessao {
        return api.getParticipanteSessaoById(id)
    }

    suspend fun criarParticipante(participanteSessao: ParticipanteSessao): ParticipanteSessao {
        return api.createParticipanteSessao(participanteSessao)
    }

    suspend fun apagarParticipante(id: Int) {
        api.deleteParticipanteSessao(id)
    }
}