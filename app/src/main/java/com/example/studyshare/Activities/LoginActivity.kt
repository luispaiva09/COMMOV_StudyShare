package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.Repositories.UtilizadorRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.UtilizadorViewModelFactory
import com.example.studyshare.ViewModels.UtilizadorViewModel
import com.example.studyshare.databinding.ActivityLoginBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val repository = UtilizadorRepository(RetrofitClient.api)
    private val viewModel: UtilizadorViewModel by viewModels { UtilizadorViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.loginUtilizador(username, password)
        }

        binding.btnIrParaRegisto.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            viewModel.utilizadorLogado.collectLatest { utilizador ->
                if (utilizador != null) {
                    Toast.makeText(this@LoginActivity, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putInt("userId", utilizador.id)
                        putString("username", utilizador.username)
                        apply()
                    }
                    val intent = Intent(this@LoginActivity, InicioActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.erroMensagem.collectLatest { mensagem ->
                mensagem?.let {
                    Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
