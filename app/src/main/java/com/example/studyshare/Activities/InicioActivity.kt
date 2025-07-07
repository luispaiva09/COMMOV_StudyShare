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

        setupRecyclerViews()

        lifecycleScope.launch {
            materialViewModel.carregarUltimosMateriais()
        }

        lifecycleScope.launch {
            materialViewModel.materiais.collectLatest { materiais ->
                materialAdapter.submitList(materiais)
            }
        }

        lifecycleScope.launch {
            discussaoViewModel.carregarUltimasDiscussoes()
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
