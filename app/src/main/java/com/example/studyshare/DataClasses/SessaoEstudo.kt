package com.example.studyshare.DataClasses

data class SessaoEstudo(
    val id: Int? = null,
    val titulo: String? = null,
    val descricao: String? = null,
    val data_hora: String? = null,
    val criador_id: Int,
    val estado_sessao: String,
    val videochamada_url: String? = null
)
