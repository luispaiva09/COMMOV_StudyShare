package com.example.studyshare.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studyshare.DataClasses.Comentario
import com.example.studyshare.DataClasses.Utilizador
import com.example.studyshare.databinding.ItemComentarioBinding

class ComentarioAdapter(
    private var comentarios: List<Comentario>,
    private var utilizadores: Map<Int, Utilizador>
) : RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val binding = ItemComentarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComentarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentarios[position]
        val autor = utilizadores[comentario.autor_id]
        holder.bind(comentario, autor)
    }

    override fun getItemCount(): Int = comentarios.size

    fun atualizarLista(novaLista: List<Comentario>, novosUtilizadores: Map<Int, Utilizador>) {
        comentarios = novaLista
        utilizadores = novosUtilizadores
        notifyDataSetChanged()
    }

    inner class ComentarioViewHolder(private val binding: ItemComentarioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comentario: Comentario, autor: Utilizador?) {
            binding.tvAutor.text = autor?.username ?: "Autor desconhecido"
            binding.tvMensagem.text = comentario.mensagem
            binding.tvData.text = comentario.data ?: ""
            binding.ratingBar.rating = comentario.avaliacao_pontos?.toFloat() ?: 0f
        }
    }
}
