package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.DataClasses.Categoria
import com.example.studyshare.DataClasses.MaterialDidatico
import com.example.studyshare.Repositories.CategoriaRepository
import com.example.studyshare.Repositories.MaterialDidaticoRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.CategoriaViewModelFactory
import com.example.studyshare.ViewModelFactories.MaterialDidaticoViewModelFactory
import com.example.studyshare.ViewModels.CategoriaViewModel
import com.example.studyshare.ViewModels.MaterialDidaticoViewModel
import com.example.studyshare.databinding.ActivityAddMaterialBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMaterialBinding

    private val materialRepository = MaterialDidaticoRepository(RetrofitClient.api)
    private val categoriaRepository = CategoriaRepository(RetrofitClient.api)

    private val materialViewModel: MaterialDidaticoViewModel by viewModels {
        MaterialDidaticoViewModelFactory(materialRepository)
    }

    private val categoriaViewModel: CategoriaViewModel by viewModels {
        CategoriaViewModelFactory(categoriaRepository)
    }

    private var listaCategorias: List<Categoria> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val autorId = sharedPref.getInt("userId", -1)

        lifecycleScope.launch {
            materialViewModel.materialCriado.collectLatest { sucesso ->
                if (sucesso == true) {
                    Toast.makeText(this@AddMaterialActivity, "Material adicionado com sucesso!", Toast.LENGTH_LONG).show()
                    limparCampos()
                    val intent = Intent(this@AddMaterialActivity, InicioActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            materialViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@AddMaterialActivity, "Erro: $it", Toast.LENGTH_LONG).show()
                    Log.e("AddMaterialActivity", "Erro ao criar material", Throwable(it))
                }
            }
        }

        lifecycleScope.launch {
            categoriaViewModel.carregarCategorias()
        }

        lifecycleScope.launch {
            categoriaViewModel.categorias.collectLatest { categorias ->
                listaCategorias = categorias
                val nomesCategorias = categorias.map { it.nome }
                val adapter = ArrayAdapter(this@AddMaterialActivity, android.R.layout.simple_spinner_item, nomesCategorias)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategorias.adapter = adapter
            }
        }

        binding.buttonSubmeter.setOnClickListener {
            val titulo = binding.etTitulo.text.toString().trim()
            val descricao = binding.etDescricao.text.toString().trim().ifEmpty { null }
            val imagemCapaUrl = binding.etImagemCapa.text.toString().trim().ifEmpty { null }
            val tipo = binding.etTipo.text.toString().trim()
            val ficheiroUrl = binding.etFicheiroUrl.text.toString().trim()
            val privado = binding.checkBoxPrivado.isChecked

            if (titulo.isEmpty() || tipo.isEmpty() || ficheiroUrl.isEmpty() || autorId == null) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoriaSelecionada = listaCategorias.getOrNull(binding.spinnerCategorias.selectedItemPosition)
            if (categoriaSelecionada == null) {
                Toast.makeText(this, "Selecione uma categoria!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val novoMaterial = MaterialDidatico(
                titulo = titulo,
                descricao = descricao,
                imagem_capa_url = imagemCapaUrl,
                tipo = tipo,
                categoria_id = categoriaSelecionada.id,
                autor_id = autorId,
                ficheiro_url = ficheiroUrl,
                privado = privado
            )

            materialViewModel.criarMaterial(novoMaterial)
        }
    }

    private fun limparCampos() {
        binding.etTitulo.text?.clear()
        binding.etDescricao.text?.clear()
        binding.etImagemCapa.text?.clear()
        binding.etTipo.text?.clear()
        binding.etFicheiroUrl.text?.clear()
        binding.checkBoxPrivado.isChecked = false
        binding.spinnerCategorias.setSelection(0)
        materialViewModel.resetErro()
    }
}
