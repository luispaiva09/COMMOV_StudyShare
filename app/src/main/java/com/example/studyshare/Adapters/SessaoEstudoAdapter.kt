package com.example.studyshare.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studyshare.DataClasses.SessaoEstudo
import com.example.studyshare.databinding.ItemSessaoEstudoBinding

class SessaoEstudoAdapter(
    private val onItemClick: (SessaoEstudo) -> Unit
) : ListAdapter<SessaoEstudo, SessaoEstudoAdapter.SessaoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessaoViewHolder {
        val binding = ItemSessaoEstudoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SessaoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessaoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SessaoViewHolder(private val binding: ItemSessaoEstudoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sessao: SessaoEstudo) {
            binding.tvTitulo.text = sessao.titulo ?: "Sem título"
            binding.tvDescricao.text = sessao.descricao ?: "Sem descrição"
            binding.tvDataHora.text = sessao.data_hora ?: "Data/Hora não disponível"
            binding.tvEstadoSessao.text = sessao.estado_sessao ?: "Estado desconhecido"

            binding.root.setOnClickListener {
                onItemClick(sessao)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SessaoEstudo>() {
        override fun areItemsTheSame(oldItem: SessaoEstudo, newItem: SessaoEstudo) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SessaoEstudo, newItem: SessaoEstudo) =
            oldItem == newItem
    }
}
