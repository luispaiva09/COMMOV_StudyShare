package com.example.studyshare.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.Repositories.UtilizadorRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.UtilizadorViewModelFactory
import com.example.studyshare.ViewModels.UtilizadorViewModel
import com.example.studyshare.databinding.ActivityAlterarPasswordBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class AlterarPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityAlterarPasswordBinding

    private val repository = UtilizadorRepository(RetrofitClient.api)
    private val viewModel: UtilizadorViewModel by viewModels { UtilizadorViewModelFactory(repository) }

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlterarPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            headerLayout = binding.headerLayoutAlterarPassword.root,
            drawerLayout = binding.drawerLayoutAlterarPassword,
            navigationView = binding.navigationViewAlterarPassword
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("userId", -1)

        binding.buttonGuardarPassword.setOnClickListener {
            val oldPassword = binding.editTextPasswordAtual.text.toString()
            val newPassword = binding.editTextNovaPassword.text.toString()
            val confirmPassword = binding.editTextConfirmarPassword.text.toString()

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "As novas palavras-passe não coincidem.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (oldPassword.isNotBlank() && newPassword.isNotBlank()) {
                lifecycleScope.launch {
                    val sucesso = viewModel.alterarPassword(userId, oldPassword, newPassword)
                    if (sucesso) {
                        Toast.makeText(this@AlterarPasswordActivity, "Palavra-passe alterada com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AlterarPasswordActivity, "Falha ao alterar palavra-passe.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Preenche todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
