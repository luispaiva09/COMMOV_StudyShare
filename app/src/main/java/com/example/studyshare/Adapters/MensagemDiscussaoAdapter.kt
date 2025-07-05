package com.example.studyshare.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studyshare.DataClasses.MensagemDiscussao
import com.example.studyshare.databinding.ItemMensagemBinding

class MensagemDiscussaoAdapter(private val userId: Int) : RecyclerView.Adapter<MensagemDiscussaoAdapter.MensagemViewHolder>() {

    private var mensagens: List<MensagemDiscussao> = emptyList()

    fun submitList(lista: List<MensagemDiscussao>) {
        mensagens = lista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensagemViewHolder {
        val binding = ItemMensagemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MensagemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MensagemViewHolder, position: Int) {
        holder.bind(mensagens[position])
    }

    override fun getItemCount() = mensagens.size

    inner class MensagemViewHolder(private val binding: ItemMensagemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mensagem: MensagemDiscussao) {
            binding.tvMensagem.text = mensagem.mensagem ?: ""
            binding.tvAutor.text = if (mensagem.autor_id == userId) "Você" else "Usuário ${mensagem.autor_id}"
            binding.tvDataEnvio.text = mensagem.data_envio ?: ""
        }
    }
}
