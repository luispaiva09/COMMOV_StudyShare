package com.example.studyshare

data class Avaliacao(
    val id: Int? = null,
    val material_id: Int,
    val avaliador_id: Int,
    val pontuacao: Int
)
