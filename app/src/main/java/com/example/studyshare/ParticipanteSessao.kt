package com.example.studyshare

data class ParticipanteSessao(
    val id: Int? = null,
    val sessao_id: Int,
    val utilizador_id: Int,
    val presente: Boolean = false
)
