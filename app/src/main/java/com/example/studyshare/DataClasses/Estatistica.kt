package com.example.studyshare.DataClasses

data class Estatistica(
    val id: Int? = null,
    val utilizador_id: Int,
    val materiais_partilhados: Int = 0,
    val materiais_visualizados: Int = 0,
    val comentarios_feitos: Int = 0,
    val horas_em_sessoes: Double = 0.0
)
