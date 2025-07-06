package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyshare.Adapters.MaterialAdapter
import com.example.studyshare.R
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

        setupHeader(
            drawerLayout = binding.drawerLayoutMyMateriais,
            headerLayout = binding.headerLayout.root
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val autorId = sharedPref.getInt("userId", -1)

        if (autorId == -1) {
            Toast.makeText(this, "Erro: usuário não autenticado!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.navigationViewMyMateriais.setNavigationItemSelectedListener { menuItem ->
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
        materialAdapter = MaterialAdapter { material ->
            // 👉 Abre o detalhe com Intent e putExtra
            val intent = Intent(this, MaterialDetalheActivity::class.java)
            intent.putExtra("material_id", material.id)
            intent.putExtra("titulo", material.titulo)
            intent.putExtra("descricao", material.descricao)
            intent.putExtra("imagem_capa_url", material.imagem_capa_url)
            intent.putExtra("ficheiro_url", material.ficheiro_url)
            intent.putExtra("privado", material.privado)
            // Se quiser passar a categoria, precisa garantir que tenha ela
            startActivity(intent)
        }

        binding.recyclerViewMateriais.apply {
            layoutManager = LinearLayoutManager(this@MyMateriaisActivity)
            adapter = materialAdapter
        }
    }
}
