package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyshare.Adapters.PesquisaAdapter
import com.example.studyshare.DataClasses.PesquisaItem
import com.example.studyshare.R
import com.example.studyshare.Repositories.CategoriaRepository
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
    private val categoriaRepository = CategoriaRepository(RetrofitClient.api)

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

    // Adapter
    private lateinit var pesquisaAdapter: PesquisaAdapter

    // Guarda todos os itens carregados
    private var allItems: List<PesquisaItem> = listOf()
    private var filteredItems: List<PesquisaItem> = listOf()

    // Filtros
    private var filtroSelecionado: String = "Todos"
    private var categoriaSelecionada: String = "Todas"

    private var listaCategorias: List<com.example.studyshare.DataClasses.Categoria> = listOf()

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

        setupRecyclerView()
        setupFiltros()

        // Carregar dados
        lifecycleScope.launch { materialViewModel.carregarMateriais() }
        lifecycleScope.launch { discussaoViewModel.carregarDiscussoes() }
        lifecycleScope.launch { sessaoViewModel.carregarSessoes() }
        lifecycleScope.launch { listaCategorias = categoriaRepository.getCategorias() }

        // Carregar categorias e configurar spinner
        lifecycleScope.launch {
            try {
                val categorias = categoriaRepository.getCategorias()
                val nomesCategorias = listOf("Todas") + categorias.map { it.nome }
                val categoriaAdapter = ArrayAdapter(
                    this@PesquisaActivity,
                    android.R.layout.simple_spinner_item,
                    nomesCategorias
                )
                categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategoria.adapter = categoriaAdapter
            } catch (e: Exception) {
                Toast.makeText(this@PesquisaActivity, "Erro ao carregar categorias", Toast.LENGTH_SHORT).show()
            }
        }

        // Atualizar lista ao receber dados
        lifecycleScope.launch {
            materialViewModel.materiais.collectLatest { atualizarLista() }
        }
        lifecycleScope.launch {
            discussaoViewModel.discussoes.collectLatest { atualizarLista() }
        }
        lifecycleScope.launch {
            sessaoViewModel.sessoes.collectLatest { atualizarLista() }
        }

        // Mostrar erros
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

        // Configurar SearchView
        binding.searchViewPesquisa.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarLista(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarLista(newText ?: "")
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        pesquisaAdapter = PesquisaAdapter(
            onMaterialClick = { material ->
                val intent = Intent(this, MaterialDetalheActivity::class.java).apply {
                    putExtra("material_id", material.id)
                    putExtra("titulo", material.titulo)
                    putExtra("descricao", material.descricao)
                    putExtra("imagem_capa_url", material.imagem_capa_url)
                    putExtra("ficheiro_url", material.ficheiro_url)
                    putExtra("privado", material.privado)
                }
                startActivity(intent)
            },
            onDiscussaoClick = { discussao ->
                val intent = Intent(this, DiscussaoDetalheActivity::class.java).apply {
                    putExtra("discussao_id", discussao.id ?: -1)
                }
                startActivity(intent)
            },
            onSessaoClick = { sessao ->
                val intent = Intent(this, SessaoEstudoDetalheActivity::class.java).apply {
                    putExtra("sessao_id", sessao.id ?: -1)
                }
                startActivity(intent)
            }
        )
        binding.recyclerViewPesquisa.apply {
            layoutManager = LinearLayoutManager(this@PesquisaActivity)
            adapter = pesquisaAdapter
        }
    }

    private fun setupFiltros() {
        val filtros = listOf("Todos", "Materiais", "Discussões", "Sessões")
        val filtroAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            filtros
        )
        filtroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFiltro.adapter = filtroAdapter

        binding.spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                filtroSelecionado = parent.getItemAtPosition(position) as String

                binding.spinnerCategoria.visibility =
                    if (filtroSelecionado == "Materiais") View.VISIBLE else View.GONE

                filtrarLista(binding.searchViewPesquisa.query.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        lifecycleScope.launch {
            listaCategorias = categoriaRepository.getCategorias()
            val nomesCategorias = mutableListOf("Todas")
            nomesCategorias.addAll(listaCategorias.map { it.nome })
            val adapterCategoria = ArrayAdapter(this@PesquisaActivity, android.R.layout.simple_spinner_item, nomesCategorias)
            adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategoria.adapter = adapterCategoria

            binding.spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    filtrarLista(binding.searchViewPesquisa.query.toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun atualizarLista() {
        val materiais = materialViewModel.materiais.value
            .filter { it.privado != true }
            .map { PesquisaItem.MaterialItem(it) }
        val discussoes = discussaoViewModel.discussoes.value.map { PesquisaItem.DiscussaoItem(it) }
        val sessoes = sessaoViewModel.sessoes.value.map { PesquisaItem.SessaoEstudoItem(it) }

        allItems = (materiais + discussoes + sessoes).sortedBy { it.getTitulo().lowercase() }

        filtrarLista(binding.searchViewPesquisa.query.toString())
    }

    private fun filtrarLista(query: String) {
        val filtroSelecionado = binding.spinnerFiltro.selectedItem?.toString() ?: "Todos"
        val categoriaSelecionada = binding.spinnerCategoria.selectedItem?.toString() ?: "Todas"

        filteredItems = allItems.filter { item ->
            val tituloOk = item.getTitulo().lowercase().contains(query.lowercase())
            val tipoOk = when (filtroSelecionado) {
                "Materiais" -> item is PesquisaItem.MaterialItem
                "Discussões" -> item is PesquisaItem.DiscussaoItem
                "Sessões" -> item is PesquisaItem.SessaoEstudoItem
                else -> true
            }
            val filtroCategoriaOk = if (filtroSelecionado == "Materiais" && categoriaSelecionada != "Todas") {
                val categoriaId = (item as? PesquisaItem.MaterialItem)?.material?.categoria_id
                val nomeCategoria = listaCategorias.find { it.id == categoriaId }?.nome
                nomeCategoria == categoriaSelecionada
            } else true

            tituloOk && tipoOk && filtroCategoriaOk
        }
        pesquisaAdapter.submitList(filteredItems)
    }
}
