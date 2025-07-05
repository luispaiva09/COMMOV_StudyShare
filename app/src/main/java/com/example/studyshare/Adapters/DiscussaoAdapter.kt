package com.example.studyshare.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studyshare.DataClasses.Discussao
import com.example.studyshare.databinding.ItemDiscussaoBinding

class DiscussaoAdapter(
    private val onItemClick: (Discussao) -> Unit
) : ListAdapter<Discussao, DiscussaoAdapter.DiscussionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscussionViewHolder {
        val binding = ItemDiscussaoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiscussionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscussionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DiscussionViewHolder(private val binding: ItemDiscussaoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(discussao: Discussao) {
            binding.tvTitulo.text = discussao.titulo ?: "Sem título"
            binding.tvDescricao.text = discussao.descricao ?: "Sem descrição"

            if (!discussao.imagem_discussao_url.isNullOrBlank()) {
                Glide.with(binding.root.context)
                    .load(discussao.imagem_discussao_url)
                    .placeholder(android.R.color.darker_gray)
                    .centerCrop()
                    .into(binding.ivImagemDiscussao)
            } else {
                binding.ivImagemDiscussao.setImageResource(android.R.color.darker_gray)
            }

            binding.root.setOnClickListener {
                onItemClick(discussao)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Discussao>() {
        override fun areItemsTheSame(oldItem: Discussao, newItem: Discussao) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Discussao, newItem: Discussao) =
            oldItem == newItem
    }
}
