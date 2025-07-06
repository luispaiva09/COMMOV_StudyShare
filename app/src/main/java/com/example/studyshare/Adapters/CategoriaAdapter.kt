package com.example.studyshare.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studyshare.DataClasses.Categoria
import com.example.studyshare.databinding.ItemCategoriaBinding

class CategoriaAdapter : ListAdapter<Categoria, CategoriaAdapter.CategoriaViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoriaViewHolder(private val binding: ItemCategoriaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoria: Categoria) {
            binding.tvNomeCategoria.text = categoria.nome
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Categoria>() {
        override fun areItemsTheSame(oldItem: Categoria, newItem: Categoria) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Categoria, newItem: Categoria) =
            oldItem == newItem
    }
}
