package com.example.studyshare.DataClasses

data class Comentario(
    val id: Int? = null,
    val material_id: Int,
    val autor_id: Int,
    val mensagem: String,
    val data: String? = null,
    val avaliacao_pontos: Float? = null
)
