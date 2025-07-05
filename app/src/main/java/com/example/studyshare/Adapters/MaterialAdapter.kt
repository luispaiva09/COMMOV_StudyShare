package com.example.studyshare.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studyshare.DataClasses.MaterialDidatico
import com.example.studyshare.databinding.ItemMaterialBinding

class MaterialAdapter : ListAdapter<MaterialDidatico, MaterialAdapter.MaterialViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val binding = ItemMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MaterialViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MaterialViewHolder(private val binding: ItemMaterialBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(material: MaterialDidatico) {
            binding.tvTitulo.text = material.titulo
            binding.tvDescricao.text = material.descricao ?: "Sem descrição"

            if (!material.imagem_capa_url.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(material.imagem_capa_url)
                    .into(binding.ivImagemCapa)
            } else {
                binding.ivImagemCapa.setImageResource(android.R.color.darker_gray)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MaterialDidatico>() {
        override fun areItemsTheSame(oldItem: MaterialDidatico, newItem: MaterialDidatico) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MaterialDidatico, newItem: MaterialDidatico) =
            oldItem == newItem
    }
}
