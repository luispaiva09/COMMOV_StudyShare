package com.example.studyshare.Adapters

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.studyshare.R
import com.example.studyshare.databinding.ItemFicheiroBinding

class FicheiroAdapter(
    private val context: Context,
    private val ficheiros: List<String>
) : RecyclerView.Adapter<FicheiroAdapter.FicheiroViewHolder>() {

    inner class FicheiroViewHolder(val binding: ItemFicheiroBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FicheiroViewHolder {
        val binding = ItemFicheiroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FicheiroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FicheiroViewHolder, position: Int) {
        val url = ficheiros[position]
        val nomeFicheiro = url.substringAfterLast('/')
        holder.binding.tvNomeFicheiro.text = nomeFicheiro

        val iconRes = when {
            nomeFicheiro.endsWith(".pdf", true) -> R.drawable.pdf
            nomeFicheiro.endsWith(".docx", true) -> R.drawable.docx
            nomeFicheiro.endsWith(".pptx", true) -> R.drawable.pptx
            else -> R.drawable.file
        }
        holder.binding.ivIconFicheiro.setImageResource(iconRes)

        holder.binding.btnDownload.setOnClickListener {
            fazerDownload(url, nomeFicheiro)
        }
    }

    override fun getItemCount(): Int = ficheiros.size

    private fun fazerDownload(url: String, nomeFicheiro: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(nomeFicheiro)
            .setDescription("A descarregar ficheiro...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nomeFicheiro)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(context, "Download iniciado: $nomeFicheiro", Toast.LENGTH_SHORT).show()
    }
}
