package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyshare.Adapters.DiscussaoAdapter
import com.example.studyshare.R
import com.example.studyshare.Repositories.DiscussaoRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.DiscussaoViewModelFactory
import com.example.studyshare.ViewModels.DiscussaoViewModel
import com.example.studyshare.databinding.ActivityMyDiscussoesBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyDiscussoesActivity : BaseActivity() {

    private lateinit var binding: ActivityMyDiscussoesBinding

    private val discussaoRepository = DiscussaoRepository(RetrofitClient.api)

    private val discussaoViewModel: DiscussaoViewModel by viewModels {
        DiscussaoViewModelFactory(discussaoRepository)
    }

    private lateinit var discussaoAdapter: DiscussaoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyDiscussoesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            drawerLayout = binding.drawerLayoutMyDiscussoes,
            headerLayout = binding.headerLayout.root
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val criadorId = sharedPref.getInt("userId", -1)

        if (criadorId == -1) {
            Toast.makeText(this, "Erro: usuário não autenticado!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.navigationViewMyDiscussoes.setNavigationItemSelectedListener { menuItem ->
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
                else -> false
            }
        }

        setupRecyclerView()

        lifecycleScope.launch {
            discussaoViewModel.carregarDiscussoesByCriador(criadorId)
        }

        lifecycleScope.launch {
            discussaoViewModel.discussoes.collectLatest { discussoes ->
                discussaoAdapter.submitList(discussoes)
            }
        }

        lifecycleScope.launch {
            discussaoViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@MyDiscussoesActivity, "Erro: $it", Toast.LENGTH_LONG).show()
                    Log.e("MyDiscussoesActivity", "Erro ao carregar discussões", Throwable(it))
                }
            }
        }
    }

    private fun setupRecyclerView() {
        discussaoAdapter = DiscussaoAdapter { discussao ->
            val intent = Intent(this, DiscussaoDetalheActivity::class.java).apply {
                putExtra("discussao_id", discussao.id)
                putExtra("titulo", discussao.titulo)
                putExtra("descricao", discussao.descricao)
                putExtra("criador_id", discussao.criador_id)
                putExtra("imagem_discussao_url", discussao.imagem_discussao_url)
            }
            startActivity(intent)
        }

        binding.recyclerViewDiscussoes.apply {
            layoutManager = LinearLayoutManager(this@MyDiscussoesActivity)
            adapter = discussaoAdapter
        }
    }
}
