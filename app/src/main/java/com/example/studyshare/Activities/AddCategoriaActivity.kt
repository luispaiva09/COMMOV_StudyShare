package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.DataClasses.Categoria
import com.example.studyshare.Repositories.CategoriaRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.CategoriaViewModelFactory
import com.example.studyshare.ViewModels.CategoriaViewModel
import com.example.studyshare.databinding.ActivityAddCategoriaBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddCategoriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCategoriaBinding

    private val repository = CategoriaRepository(RetrofitClient.api)
    private val viewModel: CategoriaViewModel by viewModels { CategoriaViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        lifecycleScope.launch {
            viewModel.categoriaCriada.collectLatest { sucesso ->
                sucesso?.let {
                    if (it) {
                        Toast.makeText(this@AddCategoriaActivity, "Categoria criada com sucesso!", Toast.LENGTH_LONG).show()
                        limparCampos()
                        val intent = Intent(this@AddCategoriaActivity, InicioActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@AddCategoriaActivity, "Erro: $it", Toast.LENGTH_LONG).show()
                    Log.e("AddCategoriaActivity", "Erro ao criar categoria", Throwable(it))
                }
            }
        }

        binding.btnAdicionarCategoria.setOnClickListener {
            val nome = binding.etNomeCategoria.text.toString().trim()

            if (nome.isEmpty()) {
                Toast.makeText(this, "Por favor, insira o nome da categoria!", Toast.LENGTH_LONG).show()
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
