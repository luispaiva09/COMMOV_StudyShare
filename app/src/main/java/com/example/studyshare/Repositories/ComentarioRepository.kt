package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Comentario
import retrofit2.Response

class ComentarioRepository(private val api: ApiService) {

    suspend fun getComentarios(): List<Comentario> {
        return api.getComentarios()
    }

    suspend fun getComentarioById(id: Int): Comentario? {
        val lista = api.getComentarioById("eq.$id")
        return lista.firstOrNull()
    }

    suspend fun criarComentario(comentario: Comentario): List<Comentario> {
        return api.createComentario(comentario)
    }

    suspend fun atualizarComentario(id: Int, comentario: Comentario): Comentario? {
        val lista = api.updateComentario("eq.$id", comentario)
        return lista.firstOrNull()
    }

    suspend fun apagarComentario(id: Int): Response<Unit> {
        return api.deleteComentario("eq.$id")
    }
}
