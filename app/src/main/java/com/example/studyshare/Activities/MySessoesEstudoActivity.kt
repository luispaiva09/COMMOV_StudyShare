package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyshare.Adapters.SessaoEstudoAdapter
import com.example.studyshare.R
import com.example.studyshare.Repositories.SessaoEstudoRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.SessaoEstudoViewModelFactory
import com.example.studyshare.ViewModels.SessaoEstudoViewModel
import com.example.studyshare.databinding.ActivityMySessoesEstudoBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MySessoesEstudoActivity : BaseActivity() {

    private lateinit var binding: ActivityMySessoesEstudoBinding

    private val sessaoEstudoRepository = SessaoEstudoRepository(RetrofitClient.api)

    private val sessaoEstudoViewModel: SessaoEstudoViewModel by viewModels {
        SessaoEstudoViewModelFactory(sessaoEstudoRepository)
    }

    private lateinit var sessaoAdapter: SessaoEstudoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMySessoesEstudoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            drawerLayout = binding.drawerLayoutMySessoesEstudo,
            headerLayout = binding.headerLayout.root
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val criadorId = sharedPref.getInt("userId", -1)

        if (criadorId == -1) {
            Toast.makeText(this, "Erro: usuário não autenticado!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.navigationViewMySessoesEstudo.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
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
                else -> false
            }
        }

        setupRecyclerView()

        lifecycleScope.launch {
            sessaoEstudoViewModel.carregarSessoesByCriador(criadorId)
        }

        lifecycleScope.launch {
            sessaoEstudoViewModel.sessoes.collectLatest { sessoes ->
                sessaoAdapter.submitList(sessoes)
            }
        }

        lifecycleScope.launch {
            sessaoEstudoViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@MySessoesEstudoActivity, "Erro: $it", Toast.LENGTH_LONG).show()
                    Log.e("MySessoesEstudoActivity", "Erro ao carregar sessões", Throwable(it))
                }
            }
        }
    }

    private fun setupRecyclerView() {
        sessaoAdapter = SessaoEstudoAdapter { sessao ->
            Log.d("SessaoClick", "Sessão ID: ${sessao.id}")
            val intent = Intent(this, SessaoEstudoDetalheActivity::class.java).apply {
                putExtra("sessao_id", sessao.id)
                putExtra("titulo", sessao.titulo)
                putExtra("descricao", sessao.descricao)
                putExtra("data_hora", sessao.data_hora)
                putExtra("estado_sessao", sessao.estado_sessao)
                putExtra("criador_id", sessao.criador_id)
            }
            startActivity(intent)
        }
        binding.recyclerViewSessoesEstudo.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSessoesEstudo.adapter = sessaoAdapter
    }
}
