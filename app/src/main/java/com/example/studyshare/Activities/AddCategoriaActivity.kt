package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.DataClasses.Categoria
import com.example.studyshare.R
import com.example.studyshare.Repositories.CategoriaRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.CategoriaViewModelFactory
import com.example.studyshare.ViewModels.CategoriaViewModel
import com.example.studyshare.databinding.ActivityAddCategoriaBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddCategoriaActivity : BaseActivity() {

    private lateinit var binding: ActivityAddCategoriaBinding

    private val repository = CategoriaRepository(RetrofitClient.api)
    private val viewModel: CategoriaViewModel by viewModels { CategoriaViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            headerLayout = binding.headerLayout.root,
            drawerLayout = binding.drawerLayoutAddCategoria
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        binding.navigationViewAddCategoria.setNavigationItemSelectedListener { menuItem ->
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


        lifecycleScope.launch {
            viewModel.categoriaCriada.collectLatest { sucesso ->
                sucesso?.let {
                    if (it) {
                        Toast.makeText(this@AddCategoriaActivity, getString(R.string.avisoCatSuccess), Toast.LENGTH_LONG).show()
                        limparCampos()
                        startActivity(Intent(this@AddCategoriaActivity, AllCategoriasActivity::class.java))
                        finish()
                    }
                }
            }
        }


        lifecycleScope.launch {
            viewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    val mensagem = getString(R.string.mensagem_erro, it)
                    Toast.makeText(this@AddCategoriaActivity, mensagem, Toast.LENGTH_LONG).show()
                    Log.e("AddCategoriaActivity", "Erro ao criar categoria", Throwable(it))
                }
            }
        }

        binding.btnAdicionarCategoria.setOnClickListener {
            val nome = binding.etNomeCategoria.text.toString().trim()

            val ButtonAddCategoria = findViewById<Button>(R.id.btnAdicionarCategoria)
            ButtonAddCategoria.text = getString(R.string.botao_AddCategoria)

            if (nome.isEmpty()) {
                Toast.makeText(this, getString(R.string.avisoNomeCat), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val novaCategoria = Categoria(nome = nome)
            viewModel.criarCategoria(novaCategoria)
        }
    }

    private fun limparCampos() {
        binding.etNomeCategoria.text?.clear()
        viewModel.resetErro()
    }
}
