package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyshare.Adapters.DiscussaoAdapter
import com.example.studyshare.Adapters.MaterialAdapter
import com.example.studyshare.R
import com.example.studyshare.Repositories.DiscussaoRepository
import com.example.studyshare.Repositories.MaterialDidaticoRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.DiscussaoViewModelFactory
import com.example.studyshare.ViewModelFactories.MaterialDidaticoViewModelFactory
import com.example.studyshare.ViewModels.DiscussaoViewModel
import com.example.studyshare.ViewModels.MaterialDidaticoViewModel
import com.example.studyshare.databinding.ActivityInicioBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InicioActivity : BaseActivity() {

    private lateinit var binding: ActivityInicioBinding

    private val materialRepository = MaterialDidaticoRepository(RetrofitClient.api)
    private val discussaoRepository = DiscussaoRepository(RetrofitClient.api)

    private val materialViewModel: MaterialDidaticoViewModel by viewModels {
        MaterialDidaticoViewModelFactory(materialRepository)
    }

    private val discussaoViewModel: DiscussaoViewModel by viewModels {
        DiscussaoViewModelFactory(discussaoRepository)
    }

    private lateinit var materialAdapter: MaterialAdapter
    private lateinit var discussaoAdapter: DiscussaoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            drawerLayout = binding.drawerLayoutInicio,
            headerLayout = binding.headerLayoutInicio.root
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val autorId = sharedPref.getInt("userId", -1)
        val criadorId = sharedPref.getInt("userId", -1)

        if (autorId == -1) {
            Toast.makeText(this, "Erro: usuário não autenticado!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (criadorId == -1) {
            Toast.makeText(this, "Erro: usuário não autenticado!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.navigationViewInicio.setNavigationItemSelectedListener { menuItem ->
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

        setupRecyclerViews()

        lifecycleScope.launch {
            materialViewModel.carregarUltimosMateriaisDoAutor(autorId)
        }

        lifecycleScope.launch {
            materialViewModel.materiais.collectLatest { materiais ->
                materialAdapter.submitList(materiais)
            }
        }

        lifecycleScope.launch {
            discussaoViewModel.carregarUltimasDiscussoesDoCriador(criadorId)
        }

        lifecycleScope.launch {
            discussaoViewModel.discussoes.collectLatest { discussoes ->
                discussaoAdapter.submitList(discussoes)
            }
        }

        lifecycleScope.launch {
            materialViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@InicioActivity, "Erro materiais: $it", Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            discussaoViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@InicioActivity, "Erro discussões: $it", Toast.LENGTH_LONG).show()
                }
            }
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

        discussaoAdapter = DiscussaoAdapter { discussao ->
            val intent = Intent(this, DiscussaoDetalheActivity::class.java).apply {
                putExtra("discussao_id", discussao.id)
                putExtra("titulo", discussao.titulo)
                putExtra("descricao", discussao.descricao)
                // outras informações que quiser passar
            }
            startActivity(intent)
        }

        binding.rvUltimosMateriais.apply {
            layoutManager = LinearLayoutManager(this@InicioActivity)
            adapter = materialAdapter
        }

        binding.rvUltimasDiscussoes.apply {
            layoutManager = LinearLayoutManager(this@InicioActivity)
            adapter = discussaoAdapter
        }
    }
}
