package com.example.studyshare.DataClasses

data class Discussao(
    val id: Int? = null,
    val titulo: String? = null,
    val descricao: String? = null,
    val criador_id: Int,
    val data_criacao: String? = null,
    val imagem_discussao_url: String? = null
)
