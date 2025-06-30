package com.example.studyshare

data class SyncOffline(
    val id: Int? = null,
    val utilizador_id: Int,
    val acao: String,
    val data_hora: String? = null,
    val descricao_acao: String? = null
)
