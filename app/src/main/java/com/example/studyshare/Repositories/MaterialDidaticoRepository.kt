package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.MaterialDidatico

class MaterialDidaticoRepository(private val api: ApiService) {

    suspend fun getMateriais(): List<MaterialDidatico> {
        return api.getMateriaisDidaticos()
    }

    suspend fun getMaterialById(id: Int): MaterialDidatico {
        return api.getMaterialDidaticoById(id)
    }

    suspend fun criarMaterial(material: MaterialDidatico): List<MaterialDidatico> {
        return api.createMaterialDidatico(material)
    }

    suspend fun atualizarMaterial(id: Int, material: MaterialDidatico): MaterialDidatico {
        return api.updateMaterialDidatico(id, material)
    }

    suspend fun apagarMaterial(id: Int) {
        api.deleteMaterialDidatico(id)
    }
}
