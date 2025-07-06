package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.DataClasses.SessaoEstudo
import com.example.studyshare.R
import com.example.studyshare.Repositories.SessaoEstudoRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.SessaoEstudoViewModelFactory
import com.example.studyshare.ViewModels.SessaoEstudoViewModel
import com.example.studyshare.databinding.ActivityAddSessaoEstudoBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddSessaoEstudoActivity : BaseActivity() {

    private lateinit var binding: ActivityAddSessaoEstudoBinding

    private val sessaoRepository = SessaoEstudoRepository(RetrofitClient.api)

    private val sessaoViewModel: SessaoEstudoViewModel by viewModels {
        SessaoEstudoViewModelFactory(sessaoRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSessaoEstudoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            drawerLayout = binding.drawerLayoutAddSessao,
            headerLayout = binding.headerLayoutAddSessao.root
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val criadorId = sharedPref.getInt("userId", -1)

        binding.navigationViewAddSessao.setNavigationItemSelectedListener { menuItem ->
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

                else -> false
            }
        }

        lifecycleScope.launch {
            sessaoViewModel.sessaoCriada.collectLatest { sucesso ->
                if (sucesso == true) {
                    Toast.makeText(this@AddSessaoEstudoActivity, "Sessão criada com sucesso!", Toast.LENGTH_LONG).show()
                    limparCampos()
                    startActivity(Intent(this@AddSessaoEstudoActivity, InicioActivity::class.java))
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            sessaoViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@AddSessaoEstudoActivity, "Erro: $it", Toast.LENGTH_LONG).show()
                    Log.e("AddSessaoEstudoActivity", "Erro ao criar sessão", Throwable(it))
                }
            }
        }

        binding.buttonSubmeterSessao.setOnClickListener {
            val titulo = binding.etTituloSessao.text.toString().trim()
            val descricao = binding.etDescricaoSessao.text.toString().trim().ifEmpty { null }
            val dataHora = binding.etDataHoraSessao.text.toString().trim()

            if (titulo.isEmpty() || dataHora.isEmpty() || criadorId == -1) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val estadoSessao = "planeada"
            val nomeSalaVideo: String? = null

            val novaSessao = SessaoEstudo(
                titulo = titulo,
                descricao = descricao,
                data_hora = dataHora,
                criador_id = criadorId,
                estado_sessao = estadoSessao,
                videochamada_url = nomeSalaVideo
            )

            lifecycleScope.launch {
                sessaoViewModel.criarSessao(novaSessao)
            }
        }
    }

    private fun limparCampos() {
        binding.etTituloSessao.text?.clear()
        binding.etDescricaoSessao.text?.clear()
        binding.etDataHoraSessao.text?.clear()
        sessaoViewModel.resetErro()
    }
}
