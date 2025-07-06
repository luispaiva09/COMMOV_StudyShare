package com.example.studyshare.Activities

import android.content.Intent
import android.net.Uri
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
import com.example.studyshare.databinding.ActivitySessaoEstudoDetalheBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle

class SessaoEstudoDetalheActivity : BaseActivity() {

    private lateinit var binding: ActivitySessaoEstudoDetalheBinding

    private val sessaoRepository = SessaoEstudoRepository(RetrofitClient.api)
    private val sessaoViewModel: SessaoEstudoViewModel by viewModels {
        SessaoEstudoViewModelFactory(sessaoRepository)
    }

    private var sessaoId: Int = -1
    private var userId: Int = -1
    private var sessao: SessaoEstudo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessaoEstudoDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            drawerLayout = binding.drawerLayoutSessaoDetalhe,
            headerLayout = binding.headerLayout.root
        )

        sessaoId = intent.getIntExtra("sessao_id", -1)
        Log.d("SessaoDetalhe", "sessaoId recebido: $sessaoId")
        if (sessaoId == -1) {
            Toast.makeText(this, "Erro: Sessão inválida!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("userId", -1)
        if (userId == -1) {
            Toast.makeText(this, "Erro: Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.navigationViewSessaoDetalhe.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    sharedPref.edit().clear().apply()
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

        observarSessao()
        sessaoViewModel.carregarSessaoById(sessaoId)  // Busca só a sessão específica

        binding.buttonEntrarVideochamada.setOnClickListener {
            sessao?.videochamada_url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } ?: Toast.makeText(this, "Nenhum link de videochamada disponível", Toast.LENGTH_SHORT).show()
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }


    private fun observarSessao() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sessaoViewModel.sessaoDetalhe.collect { s ->
                    if (s != null) {
                        sessao = s
                        mostrarDetalhes(s)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sessaoViewModel.erroMensagem.collect { erro ->
                    erro?.let {
                        Toast.makeText(this@SessaoEstudoDetalheActivity, "Erro: $it", Toast.LENGTH_SHORT).show()
                        sessaoViewModel.resetErro()
                    }
                }
            }
        }
    }

    private fun mostrarDetalhes(sessao: SessaoEstudo) {
        binding.tvTituloSessao.text = sessao.titulo ?: "Sem Título"
        binding.tvDescricaoSessao.text = sessao.descricao ?: "Sem Descrição"
        binding.tvDataHoraSessao.text = sessao.data_hora ?: "Data/Hora não definida"
        binding.tvEstadoSessao.text = "Estado: ${sessao.estado_sessao}"
        binding.buttonEntrarVideochamada.isEnabled = !sessao.videochamada_url.isNullOrEmpty()
    }
}
