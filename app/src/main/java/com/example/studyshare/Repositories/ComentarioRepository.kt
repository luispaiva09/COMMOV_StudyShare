package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Comentario

class ComentarioRepository(private val api: ApiService) {

    suspend fun getComentarios(): List<Comentario> {
        return api.getComentarios()
    }

    suspend fun getComentarioById(id: Int): Comentario {
        return api.getComentarioById(id)
    }

    suspend fun criarComentario(comentario: Comentario): Comentario {
        return api.createComentario(comentario)
    }

    suspend fun atualizarComentario(id: Int, comentario: Comentario): Comentario {
        return api.updateComentario(id, comentario)
    }

    suspend fun apagarComentario(id: Int) {
        api.deleteComentario(id)
    }
}
