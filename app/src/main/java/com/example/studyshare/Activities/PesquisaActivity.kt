package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyshare.Adapters.DiscussaoAdapter
import com.example.studyshare.Adapters.MaterialAdapter
import com.example.studyshare.Adapters.SessaoEstudoAdapter
import com.example.studyshare.R
import com.example.studyshare.Repositories.DiscussaoRepository
import com.example.studyshare.Repositories.MaterialDidaticoRepository
import com.example.studyshare.Repositories.SessaoEstudoRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.DiscussaoViewModelFactory
import com.example.studyshare.ViewModelFactories.MaterialDidaticoViewModelFactory
import com.example.studyshare.ViewModelFactories.SessaoEstudoViewModelFactory
import com.example.studyshare.ViewModels.DiscussaoViewModel
import com.example.studyshare.ViewModels.MaterialDidaticoViewModel
import com.example.studyshare.ViewModels.SessaoEstudoViewModel
import com.example.studyshare.databinding.ActivityPesquisaBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PesquisaActivity : BaseActivity() {

    private lateinit var binding: ActivityPesquisaBinding

    // Repositórios
    private val materialRepository = MaterialDidaticoRepository(RetrofitClient.api)
    private val discussaoRepository = DiscussaoRepository(RetrofitClient.api)
    private val sessaoRepository = SessaoEstudoRepository(RetrofitClient.api)

    // ViewModels
    private val materialViewModel: MaterialDidaticoViewModel by viewModels {
        MaterialDidaticoViewModelFactory(materialRepository)
    }
    private val discussaoViewModel: DiscussaoViewModel by viewModels {
        DiscussaoViewModelFactory(discussaoRepository)
    }
    private val sessaoViewModel: SessaoEstudoViewModel by viewModels {
        SessaoEstudoViewModelFactory(sessaoRepository)
    }

    // Adapters
    private lateinit var materialAdapter: MaterialAdapter
    private lateinit var discussaoAdapter: DiscussaoAdapter
    private lateinit var sessaoAdapter: SessaoEstudoAdapter

    // Controles para paginação local
    private var materiaisCount = 5
    private var discussoesCount = 5
    private var sessoesCount = 5

    // Listas completas armazenadas localmente
    private var listaMateriais = emptyList<com.example.studyshare.DataClasses.MaterialDidatico>()
    private var listaDiscussoes = emptyList<com.example.studyshare.DataClasses.Discussao>()
    private var listaSessoes = emptyList<com.example.studyshare.DataClasses.SessaoEstudo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesquisaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            drawerLayout = binding.drawerLayoutPesquisa,
            headerLayout = binding.headerLayout.root
        )

        binding.navigationViewPesquisa.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    sharedPref.edit().clear().apply()
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

        setupRecyclerViews()

        lifecycleScope.launch { materialViewModel.carregarMateriais() }
        lifecycleScope.launch { discussaoViewModel.carregarDiscussoes() }
        lifecycleScope.launch { sessaoViewModel.carregarSessoes() }

        lifecycleScope.launch {
            materialViewModel.materiais.collectLatest { materiais ->
                listaMateriais = materiais
                atualizarListaMateriais()
            }
        }

        lifecycleScope.launch {
            discussaoViewModel.discussoes.collectLatest { discussoes ->
                listaDiscussoes = discussoes
                atualizarListaDiscussoes()
            }
        }

        lifecycleScope.launch {
            sessaoViewModel.sessoes.collectLatest { sessoes ->
                listaSessoes = sessoes
                atualizarListaSessoes()
            }
        }

        lifecycleScope.launch {
            materialViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@PesquisaActivity, "Erro ao carregar materiais: $it", Toast.LENGTH_LONG).show()
                    Log.e("PesquisaActivity", it)
                }
            }
        }
        lifecycleScope.launch {
            discussaoViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@PesquisaActivity, "Erro ao carregar discussões: $it", Toast.LENGTH_LONG).show()
                    Log.e("PesquisaActivity", it)
                }
            }
        }
        lifecycleScope.launch {
            sessaoViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@PesquisaActivity, "Erro ao carregar sessões: $it", Toast.LENGTH_LONG).show()
                    Log.e("PesquisaActivity", it)
                }
            }
        }

        binding.btnExpandMateriais.setOnClickListener {
            materiaisCount += 5
            atualizarListaMateriais()
        }
        binding.btnExpandDiscussoes.setOnClickListener {
            discussoesCount += 5
            atualizarListaDiscussoes()
        }
        binding.btnExpandSessoes.setOnClickListener {
            sessoesCount += 5
            atualizarListaSessoes()
        }
    }

    private fun setupRecyclerViews() {
        materialAdapter = MaterialAdapter { material ->
            val intent = Intent(this, MaterialDetalheActivity::class.java).apply {
                putExtra("material_id", material.id)
                putExtra("titulo", material.titulo)
                putExtra("descricao", material.descricao)
                putExtra("imagem_capa_url", material.imagem_capa_url)
                putExtra("ficheiro_url", material.ficheiro_url)
                putExtra("privado", material.privado)
            }
            startActivity(intent)
        }
        binding.recyclerViewMateriais.apply {
            layoutManager = LinearLayoutManager(this@PesquisaActivity)
            adapter = materialAdapter
        }

        discussaoAdapter = DiscussaoAdapter { discussao ->
            val intent = Intent(this, DiscussaoDetalheActivity::class.java).apply {
                putExtra("discussao_id", discussao.id ?: -1)
            }
            startActivity(intent)
        }
        binding.recyclerViewDiscussoes.apply {
            layoutManager = LinearLayoutManager(this@PesquisaActivity)
            adapter = discussaoAdapter
        }

        sessaoAdapter = SessaoEstudoAdapter { sessao ->
            val intent = Intent(this, SessaoEstudoDetalheActivity::class.java).apply {
                putExtra("sessao_id", sessao.id ?: -1)
            }
            startActivity(intent)
        }
        binding.recyclerViewSessoes.apply {
            layoutManager = LinearLayoutManager(this@PesquisaActivity)
            adapter = sessaoAdapter
        }
    }

    private fun atualizarListaMateriais() {
        val sublist = if (listaMateriais.size > materiaisCount) listaMateriais.subList(0, materiaisCount) else listaMateriais
        materialAdapter.submitList(sublist)
    }

    private fun atualizarListaDiscussoes() {
        val sublist = if (listaDiscussoes.size > discussoesCount) listaDiscussoes.subList(0, discussoesCount) else listaDiscussoes
        discussaoAdapter.submitList(sublist)
    }

    private fun atualizarListaSessoes() {
        val sublist = if (listaSessoes.size > sessoesCount) listaSessoes.subList(0, sessoesCount) else listaSessoes
        sessaoAdapter.submitList(sublist)
    }
}
