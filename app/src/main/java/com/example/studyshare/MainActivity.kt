package com.example.studyshare

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.DataClasses.Utilizador
import com.example.studyshare.Repositories.UtilizadorRepository
import com.example.studyshare.ViewModelFactories.UtilizadorViewModelFactory
import com.example.studyshare.ViewModels.UtilizadorViewModel
import com.example.studyshare.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Criar o repository e o ViewModel com o factory
    private val repository = UtilizadorRepository(RetrofitClient.api)
    private val viewModel: UtilizadorViewModel by viewModels { UtilizadorViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.registoSucesso.collectLatest { sucesso ->
                sucesso?.let {
                    if (it) {
                        Toast.makeText(this@MainActivity, "Registo efetuado com sucesso!", Toast.LENGTH_LONG).show()
                        limparCampos()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Log.e("MainActivity", "Erro ao registar: $it")
                    Toast.makeText(this@MainActivity, "Erro: $it", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnRegistar.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val tipoUtilizador = "estudante"
            val morada = binding.etMorada.text.toString().trim().takeIf { it.isNotEmpty() }
            val nome = binding.etNome.text.toString().trim().takeIf { it.isNotEmpty() }
            val nTelemovel = binding.etNTelemovel.text.toString().toIntOrNull()
            val estado = "ativo"
            val dataRegisto = ""
            val ultimoLogin: String? = null

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val novoUtilizador = Utilizador(
                username = username,
                email = email,
                password = password,
                tipo_utilizador = tipoUtilizador,
                data_registo = dataRegisto,
                morada = morada,
                nome = nome,
                n_telemovel = nTelemovel,
                estado = estado,
                ultimo_login = ultimoLogin
            )

            viewModel.registarUtilizador(novoUtilizador)
        }
    }

    private fun limparCampos() {
        binding.etUsername.text?.clear()
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()
        binding.etMorada.text?.clear()
        binding.etNome.text?.clear()
        binding.etNTelemovel.text?.clear()
        viewModel.resetEstado()
    }
}
