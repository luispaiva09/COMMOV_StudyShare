package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.Categoria

class CategoriaRepository(private val api: ApiService) {

    suspend fun getCategorias(): List<Categoria> {
        return api.getCategorias()
    }

    suspend fun getCategoriaById(id: Int): Categoria {
        return api.getCategoriaById(id)
    }

    suspend fun criarCategoria(categoria: Categoria): Categoria {
        return api.createCategoria(categoria)
    }

    suspend fun atualizarCategoria(id: Int, categoria: Categoria): Categoria {
        return api.updateCategoria(id, categoria)
    }

    suspend fun apagarCategoria(id: Int) {
        api.deleteCategoria(id)
    }
}