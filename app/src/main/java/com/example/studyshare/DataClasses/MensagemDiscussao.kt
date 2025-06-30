package com.example.studyshare.DataClasses

data class MensagemDiscussao(
    val id: Int? = null,
    val discussao_id: Int,
    val autor_id: Int,
    val mensagem: String? = null,
    val data_envio: String? = null,
    val imagem_url: String? = null
)
