package com.example.studyshare

data class MensagemDiscussao(
    val id: Int? = null,
    val discussao_id: Int,
    val autor_id: Int,
    val mensagem: String,
    val data_envio: String? = null
)
