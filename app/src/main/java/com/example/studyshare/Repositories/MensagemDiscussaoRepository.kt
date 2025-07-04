package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.MensagemDiscussao
import retrofit2.Response

class MensagemDiscussaoRepository(private val api: ApiService) {

    suspend fun getMensagens(): List<MensagemDiscussao> {
        return api.getMensagensDiscussao()
    }

    suspend fun getMensagemById(id: Int): MensagemDiscussao? {
        val lista = api.getMensagemDiscussaoById("eq.$id")
        return lista.firstOrNull()
    }

    suspend fun criarMensagem(mensagem: MensagemDiscussao): List<MensagemDiscussao> {
        return api.createMensagemDiscussao(mensagem)
    }

    suspend fun atualizarMensagem(id: Int, mensagem: MensagemDiscussao): MensagemDiscussao? {
        val lista = api.updateMensagemDiscussao("eq.$id", mensagem)
        return lista.firstOrNull()
    }

    suspend fun apagarMensagem(id: Int): Response<Unit> {
        return api.deleteMensagemDiscussao("eq.$id")
    }
}
