package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.MensagemDiscussao

class MensagemDiscussaoRepository(private val api: ApiService) {

    suspend fun getMensagens(): List<MensagemDiscussao> {
        return api.getMensagensDiscussao()
    }

    suspend fun getMensagemById(id: Int): MensagemDiscussao {
        return api.getMensagemDiscussaoById(id)
    }

    suspend fun criarMensagem(mensagem: MensagemDiscussao): MensagemDiscussao {
        return api.createMensagemDiscussao(mensagem)
    }

    suspend fun atualizarMensagem(id: Int, mensagem: MensagemDiscussao): MensagemDiscussao {
        return api.updateMensagemDiscussao(id, mensagem)
    }

    suspend fun apagarMensagem(id: Int) {
        api.deleteMensagemDiscussao(id)
    }
}
