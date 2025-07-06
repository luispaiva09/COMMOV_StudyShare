package com.example.studyshare.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.Repositories.UtilizadorRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.UtilizadorViewModelFactory
import com.example.studyshare.ViewModels.UtilizadorViewModel
import com.example.studyshare.databinding.ActivityEditarPerfilBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class EditarPerfilActivity : BaseActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding

    private val repository = UtilizadorRepository(RetrofitClient.api)
    private val viewModel: UtilizadorViewModel by viewModels { UtilizadorViewModelFactory(repository) }

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            headerLayout = binding.headerLayoutEditarPerfil.root,
            drawerLayout = binding.drawerLayoutEditarPerfil,
            navigationView = binding.navigationViewEditarPerfil
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("userId", -1)

        if (userId != -1) {
            viewModel.getUtilizadorById(userId)
        }

        lifecycleScope.launch {
            viewModel.utilizadorPerfil.collect { utilizador ->
                utilizador?.let {
                    binding.editTextEditarNome.setText(it.nome ?: "")
                    binding.editTextEditarUsername.setText(it.username ?: "")
                    binding.editTextEditarEmail.setText(it.email ?: "")
                    binding.editTextEditarTelefone.setText(it.n_telemovel?.toString() ?: "")
                }
            }
        }

        binding.buttonGuardarPerfil.setOnClickListener {
            val nome = binding.editTextEditarNome.text.toString()
            val username = binding.editTextEditarUsername.text.toString()
            val email = binding.editTextEditarEmail.text.toString()
            val telefone = binding.editTextEditarTelefone.text.toString().toIntOrNull()

            if (nome.isNotBlank() && username.isNotBlank() && email.isNotBlank() && telefone != null) {
                viewModel.atualizarUtilizador(userId, nome, username, email, telefone)

                Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Preenche todos os campos corretamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
