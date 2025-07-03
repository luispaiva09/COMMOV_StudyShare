package com.example.studyshare.Repositories

import com.example.studyshare.ApiService
import com.example.studyshare.DataClasses.SyncOffline

class SyncOfflineRepository(private val api: ApiService) {

    suspend fun getSyncsOffline(): List<SyncOffline> {
        return api.getSyncsOffline()
    }

    suspend fun getSyncOfflineById(id: Int): SyncOffline {
        return api.getSyncOfflineById(id)
    }

    suspend fun criarSyncOffline(syncOffline: SyncOffline): SyncOffline {
        return api.createSyncOffline(syncOffline)
    }

    suspend fun apagarSyncOffline(id: Int) {
        api.deleteSyncOffline(id)
    }
}
