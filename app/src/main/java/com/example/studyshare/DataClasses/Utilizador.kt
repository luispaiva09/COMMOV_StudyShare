package com.example.studyshare.DataClasses

data class Utilizador(
    val id: Int? = null,
    val username: String,
    val email: String,
    val password: String,
    val tipo_utilizador: String,
    val data_registo: String? = null,
    val morada: String? = null,
    val nome: String? = null,
    val n_telemovel: Int? = null,
    val estado: String,
    val ultimo_login: String? = null
)


