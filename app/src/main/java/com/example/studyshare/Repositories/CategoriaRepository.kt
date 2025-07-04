package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Categoria
import retrofit2.Response

class CategoriaRepository(private val api: ApiService) {

    suspend fun getCategorias(): List<Categoria> {
        return api.getCategorias()
    }

    suspend fun getCategoriaById(id: Int): Categoria? {
        val lista = api.getCategoriaById("eq.$id")
        return lista.firstOrNull()
    }

    suspend fun criarCategoria(categoria: Categoria): List<Categoria> {
        return api.createCategoria(categoria)
    }

    suspend fun atualizarCategoria(id: Int, categoria: Categoria): Categoria? {
        val lista = api.updateCategoria("eq.$id", categoria)
        return lista.firstOrNull()
    }

    suspend fun apagarCategoria(id: Int): Response<Unit> {
        return api.deleteCategoria("eq.$id")
    }
}