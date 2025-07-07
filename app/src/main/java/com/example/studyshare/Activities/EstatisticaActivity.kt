package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.R
import com.example.studyshare.Repositories.EstatisticaRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.EstatisticaViewModelFactory
import com.example.studyshare.ViewModels.EstatisticaViewModel
import com.example.studyshare.databinding.ActivityEstatisticaBinding
import kotlinx.coroutines.launch

class EstatisticaActivity : BaseActivity() {

    private lateinit var binding: ActivityEstatisticaBinding

    private val repository = EstatisticaRepository(RetrofitClient.api)
    private val viewModel: EstatisticaViewModel by viewModels { EstatisticaViewModelFactory(repository) }

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEstatisticaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        // Setup do cabeçalho
        setupHeader(
            headerLayout = binding.headerLayoutEstatistica.root,
            drawerLayout = binding.drawerLayoutEstatistica
        )

        // Setup do Navigation Drawer (copiado do AddCategoriaActivity)
        binding.navigationViewEstatistica.setNavigationItemSelectedListener { menuItem ->
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

        // Obter userId e carregar estatísticas
        userId = sharedPref.getInt("userId", -1)

        if (userId != -1) {
            viewModel.carregarEstatisticaPorId(userId)
        } else {
            Toast.makeText(this, "Utilizador não autenticado.", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Observar estatísticas
        lifecycleScope.launch {
            viewModel.estatisticaSelecionada.collect { estatistica ->
                estatistica?.let {
                    binding.textViewMateriaisPartilhados.text = it.materiais_partilhados.toString()
                    binding.textViewMateriaisVisualizados.text = it.materiais_visualizados.toString()
                    binding.textViewComentariosFeitos.text = it.comentarios_feitos.toString()
                    binding.textViewHorasSessoes.text = String.format("%.2f", it.horas_em_sessoes)
                }
            }
        }

        // Observar erros
        lifecycleScope.launch {
            viewModel.erroMensagem.collect { erro ->
                erro?.let {
                    Toast.makeText(this@EstatisticaActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.resetErro()
                }
            }
        }
    }
}
