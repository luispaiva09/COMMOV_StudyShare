package com.example.studyshare.DataClasses

sealed class PesquisaItem {
    abstract fun getTitulo(): String

    data class MaterialItem(val material: MaterialDidatico) : PesquisaItem() {
        override fun getTitulo() = material.titulo
    }

    data class DiscussaoItem(val discussao: Discussao) : PesquisaItem() {
        override fun getTitulo() = discussao.titulo ?: ""
    }

    data class SessaoEstudoItem(val sessao: SessaoEstudo) : PesquisaItem() {
        override fun getTitulo() = sessao.titulo ?: ""
    }
}