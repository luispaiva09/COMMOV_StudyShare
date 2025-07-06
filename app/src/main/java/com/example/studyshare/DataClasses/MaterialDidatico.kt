package com.example.studyshare.DataClasses

data class MaterialDidatico(
    val id: Int? = null,
    val titulo: String,
    val descricao: String? = null,
    val categoria_id: Int? = null,
    val autor_id: Int,
    val data_criacao: String? = null,
    val privado: Boolean? = false,
    val ficheiro_url: String,
    val imagem_capa_url: String? = null
)

