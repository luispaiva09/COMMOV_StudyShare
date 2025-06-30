package com.example.studyshare.DataClasses

data class ParticipanteSessao(
    val id: Int? = null,
    val sessao_id: Int,
    val utilizador_id: Int,
    val presente: Boolean = false
)
