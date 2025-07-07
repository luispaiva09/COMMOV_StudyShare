package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyshare.Adapters.CategoriaAdapter
import com.example.studyshare.R
import com.example.studyshare.Repositories.CategoriaRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.CategoriaViewModelFactory
import com.example.studyshare.ViewModels.CategoriaViewModel
import com.example.studyshare.databinding.ActivityAllCategoriasBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AllCategoriasActivity : BaseActivity() {

    private lateinit var binding: ActivityAllCategoriasBinding

    private val repository = CategoriaRepository(RetrofitClient.api)
    private val viewModel: CategoriaViewModel by viewModels { CategoriaViewModelFactory(repository) }

    private lateinit var adapter: CategoriaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllCategoriasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup do cabeçalho
        setupHeader(
            headerLayout = binding.headerLayout.root,
            drawerLayout = binding.drawerLayoutAllCategorias
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        // Setup do Navigation Drawer
        binding.navigationViewAllCategorias.setNavigationItemSelectedListener { menuItem ->
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

        // Configurar RecyclerView
        adapter = CategoriaAdapter()
        binding.recyclerViewCategorias.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCategorias.adapter = adapter

        // Observar categorias
        lifecycleScope.launch {
            viewModel.categorias.collectLatest { categorias ->
                adapter.submitList(categorias)
            }
        }

        // Observar erros
        lifecycleScope.launch {
            viewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@AllCategoriasActivity, "Erro: $it", Toast.LENGTH_LONG).show()
                    Log.e("AllCategoriasActivity", "Erro ao carregar categorias", Throwable(it))
                }
            }
        }

        binding.buttonAddCategoria.setOnClickListener {
            val intent = Intent(this, AddCategoriaActivity::class.java)
            startActivity(intent)
        }

        viewModel.carregarCategorias()
    }
}
