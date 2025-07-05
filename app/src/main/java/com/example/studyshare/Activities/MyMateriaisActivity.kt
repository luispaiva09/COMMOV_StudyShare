package com.example.studyshare.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyshare.Adapters.MaterialAdapter
import com.example.studyshare.Repositories.MaterialDidaticoRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.MaterialDidaticoViewModelFactory
import com.example.studyshare.ViewModels.MaterialDidaticoViewModel
import com.example.studyshare.databinding.ActivityMyMateriaisBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyMateriaisActivity : BaseActivity() {

    private lateinit var binding: ActivityMyMateriaisBinding

    private val materialRepository = MaterialDidaticoRepository(RetrofitClient.api)

    private val materialViewModel: MaterialDidaticoViewModel by viewModels {
        MaterialDidaticoViewModelFactory(materialRepository)
    }

    private lateinit var materialAdapter: MaterialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyMateriaisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(binding.headerLayout.root, null)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val autorId = sharedPref.getInt("userId", -1)

        if (autorId == -1) {
            Toast.makeText(this, "Erro: usuário não autenticado!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()

        lifecycleScope.launch {
            materialViewModel.carregarMateriaisDoAutor(autorId)
        }

        lifecycleScope.launch {
            materialViewModel.materiais.collectLatest { materiais ->
                materialAdapter.submitList(materiais)
            }
        }

        lifecycleScope.launch {
            materialViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@MyMateriaisActivity, "Erro: $it", Toast.LENGTH_LONG).show()
                    Log.e("MyMateriaisActivity", "Erro ao carregar materiais", Throwable(it))
                }
            }
        }
    }

    private fun setupRecyclerView() {
        materialAdapter = MaterialAdapter()
        binding.recyclerViewMateriais.apply {
            layoutManager = LinearLayoutManager(this@MyMateriaisActivity)
            adapter = materialAdapter
        }
    }
}
