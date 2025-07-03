package com.example.studyshare.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.DataClasses.MaterialDidatico
import com.example.studyshare.Repositories.MaterialDidaticoRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.MaterialDidaticoViewModelFactory
import com.example.studyshare.ViewModels.MaterialDidaticoViewModel
import com.example.studyshare.databinding.ActivityAddMaterialBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMaterialBinding

    private val repository = MaterialDidaticoRepository(RetrofitClient.api)
    private val viewModel: MaterialDidaticoViewModel by viewModels {
        MaterialDidaticoViewModelFactory(repository)
    }

    // Guarda o id do utilizador autenticado recebido pela Intent
    private var autorId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recebe o autorId da Intent (deve ser enviado pela InicioActivity)
        autorId = intent.getIntExtra("userId", -1).takeIf { it != -1 }

        lifecycleScope.launch {
            viewModel.materialCriado.collectLatest { sucesso ->
                if (sucesso == true) {
                    Toast.makeText(this@AddMaterialActivity, "Material adicionado com sucesso!", Toast.LENGTH_LONG).show()
                    limparCampos()
                    finish() // opcional: voltar à atividade anterior
                }
            }
        }

        lifecycleScope.launch {
            viewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@AddMaterialActivity, "Erro: $it", Toast.LENGTH_LONG).show()
                }
            }
        }

        val userId = intent.getIntExtra("id", "") ?: "Utilizador"

        binding.buttonSubmeter.setOnClickListener {
            val titulo = binding.etTitulo.text.toString().trim()
            val descricao = binding.etDescricao.text.toString().trim().ifEmpty { null }
            val tipo = binding.etTipo.text.toString().trim()
            val categoriaId: Int? = null
            val autorId = userId
            val ficheiroUrl = binding.etFicheiroUrl.text.toString().trim()
            val privado = binding.checkBoxPrivado.isChecked

            // Usa o autorId recebido da Intent, não o da UI
            if (titulo.isEmpty() || tipo.isEmpty() || autorId == null || ficheiroUrl.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val novoMaterial = MaterialDidatico(
                titulo = titulo,
                descricao = descricao,
                tipo = tipo,
                categoria_id = categoriaId,
                autor_id = autorId,
                ficheiro_url = ficheiroUrl,
                privado = privado
            )

            viewModel.criarMaterial(novoMaterial)
        }
    }

    private fun limparCampos() {
        binding.etTitulo.text?.clear()
        binding.etDescricao.text?.clear()
        binding.etTipo.text?.clear()
        binding.etFicheiroUrl.text?.clear()
        binding.checkBoxPrivado.isChecked = false
        viewModel.resetErro()
    }
}
