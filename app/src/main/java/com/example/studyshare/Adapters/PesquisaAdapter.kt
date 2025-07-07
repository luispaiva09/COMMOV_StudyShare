package com.example.studyshare.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studyshare.DataClasses.Discussao
import com.example.studyshare.DataClasses.MaterialDidatico
import com.example.studyshare.DataClasses.PesquisaItem
import com.example.studyshare.DataClasses.SessaoEstudo
import com.example.studyshare.databinding.ItemDiscussaoBinding
import com.example.studyshare.databinding.ItemMaterialBinding
import com.example.studyshare.databinding.ItemSessaoEstudoBinding

class PesquisaAdapter(
    private val onMaterialClick: (MaterialDidatico) -> Unit,
    private val onDiscussaoClick: (Discussao) -> Unit,
    private val onSessaoClick: (SessaoEstudo) -> Unit
) : ListAdapter<PesquisaItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_MATERIAL = 0
        private const val TYPE_DISCUSS = 1
        private const val TYPE_SESSAO = 2
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PesquisaItem.MaterialItem -> TYPE_MATERIAL
        is PesquisaItem.DiscussaoItem -> TYPE_DISCUSS
        is PesquisaItem.SessaoEstudoItem -> TYPE_SESSAO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_MATERIAL -> {
                val binding = ItemMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MaterialViewHolder(binding)
            }
            TYPE_DISCUSS -> {
                val binding = ItemDiscussaoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DiscussaoViewHolder(binding)
            }
            TYPE_SESSAO -> {
                val binding = ItemSessaoEstudoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SessaoViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is MaterialViewHolder -> {
                val material = (item as PesquisaItem.MaterialItem).material
                holder.bind(material)
                holder.itemView.setOnClickListener { onMaterialClick(material) }
            }
            is DiscussaoViewHolder -> {
                val discussao = (item as PesquisaItem.DiscussaoItem).discussao
                holder.bind(discussao)
                holder.itemView.setOnClickListener { onDiscussaoClick(discussao) }
            }
            is SessaoViewHolder -> {
                val sessao = (item as PesquisaItem.SessaoEstudoItem).sessao
                holder.bind(sessao)
                holder.itemView.setOnClickListener { onSessaoClick(sessao) }
            }
        }
    }

    class MaterialViewHolder(private val binding: ItemMaterialBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(material: MaterialDidatico) {
            binding.tvTitulo.text = material.titulo
            binding.tvDescricao.text = material.descricao
            if (!material.imagem_capa_url.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(material.imagem_capa_url)
                    .centerCrop()
                    .placeholder(android.R.color.darker_gray)
                    .into(binding.ivImagemCapa)
            } else {
                binding.ivImagemCapa.setImageResource(android.R.color.darker_gray)
            }
        }
    }

    class DiscussaoViewHolder(private val binding: ItemDiscussaoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(discussao: Discussao) {
            binding.tvTitulo.text = discussao.titulo ?: ""
            binding.tvDescricao.text = discussao.descricao ?: ""
            if (!discussao.imagem_discussao_url.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(discussao.imagem_discussao_url)
                    .centerCrop()
                    .placeholder(android.R.color.darker_gray)
                    .into(binding.ivImagemDiscussao)
            } else {
                binding.ivImagemDiscussao.setImageResource(android.R.color.darker_gray)
            }
        }
    }

    class SessaoViewHolder(private val binding: ItemSessaoEstudoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sessao: SessaoEstudo) {
            binding.tvTitulo.text = sessao.titulo ?: ""
            binding.tvDescricao.text = sessao.descricao ?: ""
            binding.tvDataHora.text = sessao.data_hora ?: ""
            binding.tvEstadoSessao.text = sessao.estado_sessao ?: ""
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PesquisaItem>() {
        override fun areItemsTheSame(oldItem: PesquisaItem, newItem: PesquisaItem): Boolean {
            return when {
                oldItem is PesquisaItem.MaterialItem && newItem is PesquisaItem.MaterialItem -> oldItem.material.id == newItem.material.id
                oldItem is PesquisaItem.DiscussaoItem && newItem is PesquisaItem.DiscussaoItem -> oldItem.discussao.id == newItem.discussao.id
                oldItem is PesquisaItem.SessaoEstudoItem && newItem is PesquisaItem.SessaoEstudoItem -> oldItem.sessao.id == newItem.sessao.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: PesquisaItem, newItem: PesquisaItem): Boolean = oldItem == newItem
    }
}
