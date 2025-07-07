package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.studyshare.Adapters.ComentarioAdapter
import com.example.studyshare.Adapters.FicheiroAdapter
import com.example.studyshare.DataClasses.Comentario
import com.example.studyshare.R
import com.example.studyshare.Repositories.ComentarioRepository
import com.example.studyshare.Repositories.UtilizadorRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.ComentarioViewModelFactory
import com.example.studyshare.ViewModelFactories.UtilizadorViewModelFactory
import com.example.studyshare.ViewModels.ComentarioViewModel
import com.example.studyshare.ViewModels.UtilizadorViewModel
import com.example.studyshare.databinding.ActivityMaterialDetalheBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MaterialDetalheActivity : BaseActivity() {

    private lateinit var binding: ActivityMaterialDetalheBinding
    private var isExpanded = false

    private lateinit var comentarioAdapter: ComentarioAdapter
    private lateinit var comentarioViewModel: ComentarioViewModel
    private lateinit var utilizadorViewModel: UtilizadorViewModel

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

        val materialId = intent.getIntExtra("material_id", -1)
        if (materialId == -1) {
            Toast.makeText(this, "Erro: material inválido!", Toast.LENGTH_SHORT).show()
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
                R.id.nav_pesquisar -> {
                    startActivity(Intent(this, PesquisaActivity::class.java))
                    true
                }
                R.id.nav_estatisticas -> {
                    startActivity(Intent(this, EstatisticaActivity::class.java))
                    true
                }
                else -> false
            }
        }

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
        binding.recyclerViewFicheiros.visibility = View.GONE

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

        binding.btnVoltar.setOnClickListener {
            finish()
        }

        // Repositórios e ViewModels
        val comentarioRepository = ComentarioRepository(RetrofitClient.api)
        comentarioViewModel = ViewModelProvider(this, ComentarioViewModelFactory(comentarioRepository))
            .get(ComentarioViewModel::class.java)

        val utilizadorRepository = UtilizadorRepository(RetrofitClient.api)
        utilizadorViewModel = ViewModelProvider(this, UtilizadorViewModelFactory(utilizadorRepository))
            .get(UtilizadorViewModel::class.java)

        utilizadorViewModel.getUtilizadores()

        binding.recyclerViewComentarios.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    utilizadorViewModel.utilizadores.collectLatest { listaUtilizadores ->
                        val utilizadorMap: Map<Int, com.example.studyshare.DataClasses.Utilizador> =
                            listaUtilizadores
                                .filter { it.id != null }
                                .associateBy { it.id!! }

                        comentarioAdapter = ComentarioAdapter(emptyList(), utilizadorMap)
                        binding.recyclerViewComentarios.adapter = comentarioAdapter

                        comentarioViewModel.comentarios.collectLatest { listaComentarios ->
                            val filtrados = listaComentarios.filter { it.material_id == materialId }
                            comentarioAdapter.atualizarLista(filtrados, utilizadorMap)
                        }
                    }
                }

                launch {
                    comentarioViewModel.erroMensagem.collectLatest { erro ->
                        erro?.let {
                            Toast.makeText(this@MaterialDetalheActivity, it, Toast.LENGTH_SHORT).show()
                            comentarioViewModel.resetErro()
                        }
                    }
                }
            }
        }

        binding.btnEnviarComentario.setOnClickListener {
            val mensagem = binding.etMensagemComentario.text.toString().trim()
            if (mensagem.isEmpty()) {
                Toast.makeText(this, "Digite a mensagem do comentário", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val avaliacao = binding.ratingBarComentario.rating

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val dataAtual = sdf.format(Date())

            val novoComentario = Comentario(
                id = null,
                material_id = materialId,
                autor_id = autorId,
                mensagem = mensagem,
                data = dataAtual,
                avaliacao_pontos = if (avaliacao > 0f) avaliacao else null
            )

            comentarioViewModel.criarComentario(novoComentario)

            // Limpar inputs
            binding.etMensagemComentario.text.clear()
            binding.ratingBarComentario.rating = 0f
        }
    }
}
