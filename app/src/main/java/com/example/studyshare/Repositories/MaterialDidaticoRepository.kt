package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.MaterialDidatico
import retrofit2.Response

class MaterialDidaticoRepository(private val api: ApiService) {

    suspend fun getMateriais(): List<MaterialDidatico> {
        return api.getMateriaisDidaticos()
    }

    suspend fun getMaterialById(id: Int): MaterialDidatico? {
        val lista = api.getMaterialDidaticoById("eq.$id")
        return lista.firstOrNull()
    }

    suspend fun criarMaterial(material: MaterialDidatico): List<MaterialDidatico> {
        return api.createMaterialDidatico(material)
    }

    suspend fun atualizarMaterial(id: Int, material: MaterialDidatico): MaterialDidatico? {
        val lista = api.updateMaterialDidatico("eq.$id", material)
        return lista.firstOrNull()
    }

    suspend fun apagarMaterial(id: Int): Response<Unit> {
        return api.deleteMaterialDidatico("eq.$id")
    }

    suspend fun getMaterialByAutor(autor_id: Int): List<MaterialDidatico> {
        return api.getMateriaisByAutor("eq.$autor_id")
    }

    suspend fun getUltimosMateriaisDoAutor(autorId: Int): List<MaterialDidatico> {
        return api.getUltimosMateriaisDoAutor("eq.$autorId")
    }

}
