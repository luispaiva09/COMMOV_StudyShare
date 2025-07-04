package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.SyncOffline
import retrofit2.Response

class SyncOfflineRepository(private val api: ApiService) {

    suspend fun getSyncsOffline(): List<SyncOffline> {
        return api.getSyncsOffline()
    }

    suspend fun getSyncOfflineById(id: Int): SyncOffline? {
        val lista = api.getSyncOfflineById("eq.$id")
        return lista.firstOrNull()
    }

    suspend fun criarSyncOffline(syncOffline: SyncOffline): List<SyncOffline> {
        return api.createSyncOffline(syncOffline)
    }

    suspend fun apagarSyncOffline(id: Int): Response<Unit> {
        return api.deleteSyncOffline("eq.$id")
    }

}
