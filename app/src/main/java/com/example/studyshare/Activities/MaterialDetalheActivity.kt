package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.studyshare.Adapters.FicheiroAdapter
import com.example.studyshare.R
import com.example.studyshare.databinding.ActivityMaterialDetalheBinding

class MaterialDetalheActivity : BaseActivity() {

    private lateinit var binding: ActivityMaterialDetalheBinding
    private var isExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            drawerLayout = binding.drawerLayoutMaterialDetalhe,
            headerLayout = binding.headerLayout.root
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val autorId = sharedPref.getInt("userId", -1)

        if (autorId == -1) {
            Toast.makeText(this, "Erro: usuário não autenticado!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.navigationViewMaterialDetalhe.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    val editor = sharedPref.edit()
                    editor.clear()
                    editor.apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_inicio -> {
                    startActivity(Intent(this, InicioActivity::class.java))
                    true
                }
                R.id.nav_materiais -> {
                    startActivity(Intent(this, MyMateriaisActivity::class.java))
                    true
                }
                R.id.nav_discussoes -> {
                    startActivity(Intent(this, MyDiscussoesActivity::class.java))
                    true
                }
                R.id.nav_sessoes -> {
                    startActivity(Intent(this, MySessoesEstudoActivity::class.java))
                    true
                }
                R.id.nav_categorias -> {
                    startActivity(Intent(this, AllCategoriasActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Dados da Intent
        val titulo = intent.getStringExtra("titulo")
        val descricao = intent.getStringExtra("descricao")
        val imagemCapaUrl = intent.getStringExtra("imagem_capa_url")
        val ficheiroUrl = intent.getStringExtra("ficheiro_url")
        val privado = intent.getBooleanExtra("privado", false)

        binding.tvTitulo.text = titulo
        binding.tvDescricao.text = descricao ?: "Sem descrição"
        binding.tvPrivado.text = "Privado: ${if (privado) "Sim" else "Não"}"

        if (!imagemCapaUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagemCapaUrl)
                .into(binding.ivImagemCapa)
        } else {
            binding.ivImagemCapa.setImageResource(android.R.color.darker_gray)
        }

        val ficheiroUrls = ficheiroUrl?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

        val ficheiroAdapter = FicheiroAdapter(this, ficheiroUrls)
        binding.recyclerViewFicheiros.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFicheiros.adapter = ficheiroAdapter

        // Inicia fechado
        binding.recyclerViewFicheiros.visibility = View.GONE

        // Botão expandir/recolher
        binding.btnExpandirFicheiros.setOnClickListener {
            isExpanded = !isExpanded
            if (isExpanded) {
                binding.recyclerViewFicheiros.visibility = View.VISIBLE
                binding.btnExpandirFicheiros.text = "Recolher ficheiros"
            } else {
                binding.recyclerViewFicheiros.visibility = View.GONE
                binding.btnExpandirFicheiros.text = "Ver ficheiros (${ficheiroUrls.size})"
            }
        }

        // Botão voltar
        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }
}
