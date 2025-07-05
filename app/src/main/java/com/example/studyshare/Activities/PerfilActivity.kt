package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.R
import com.example.studyshare.Repositories.UtilizadorRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.UtilizadorViewModelFactory
import com.example.studyshare.ViewModels.UtilizadorViewModel
import com.example.studyshare.databinding.ActivityPerfilBinding
import kotlinx.coroutines.launch

class PerfilActivity : BaseActivity() {

    private lateinit var binding: ActivityPerfilBinding

    private val repository = UtilizadorRepository(RetrofitClient.api)
    private val viewModel: UtilizadorViewModel by viewModels { UtilizadorViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura o cabeçalho da base
        setupHeader(
            drawerLayout = binding.drawerLayoutPerfil,
            headerLayout = binding.headerLayoutPerfil.root
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        if (userId != -1) {
            viewModel.getUtilizadorById(userId)
        }

        // Observa os dados do utilizador
        lifecycleScope.launch {
            viewModel.utilizadorPerfil.collect { utilizador ->
                utilizador?.let {
                    binding.editTextNumero.setText(it.id.toString())
                    binding.editTextUsername.setText(it.username ?: "")
                    binding.editTextNome.setText(it.nome ?: "")
                    binding.editTextTelefone.setText(it.n_telemovel?.toString() ?: "")
                    binding.editTextEmail.setText(it.email ?: "")
                    binding.editTextPassword.setText("********")

                    binding.textViewNomePerfil.text = it.username ?: "Perfil"
                }
            }
        }

        lifecycleScope.launch {
            viewModel.erroMensagem.collect { erro ->
                erro?.let {
                    Toast.makeText(this@PerfilActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configura o Navigation Drawer
        binding.navigationViewPerfil.setNavigationItemSelectedListener { menuItem ->
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
                else -> false
            }
        }
    }
}
