package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyshare.Adapters.MensagemDiscussaoAdapter
import com.example.studyshare.DataClasses.MensagemDiscussao
import com.example.studyshare.R
import com.example.studyshare.Repositories.MensagemDiscussaoRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.MensagemDiscussaoViewModelFactory
import com.example.studyshare.ViewModels.MensagemDiscussaoViewModel
import com.example.studyshare.databinding.ActivityDiscussaoDetalheBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DiscussaoDetalheActivity : BaseActivity() {

    private lateinit var binding: ActivityDiscussaoDetalheBinding

    private val mensagemRepository = MensagemDiscussaoRepository(RetrofitClient.api)

    private val mensagemViewModel: MensagemDiscussaoViewModel by viewModels {
        MensagemDiscussaoViewModelFactory(mensagemRepository)
    }

    private lateinit var mensagemAdapter: MensagemDiscussaoAdapter

    private var discussaoId: Int = -1
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiscussaoDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            drawerLayout = binding.drawerLayoutDiscussaoDetalhe,
            headerLayout = binding.headerLayout.root
        )

        discussaoId = intent.getIntExtra("discussao_id", -1)
        if (discussaoId == -1) {
            Toast.makeText(this, "Erro: discussão inválida!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("userId", -1)
        if (userId == -1) {
            Toast.makeText(this, "Erro: usuário não autenticado!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.navigationViewDiscussaoDetalhe.setNavigationItemSelectedListener { menuItem ->
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

        setupRecyclerView()
        observarMensagens()

        // Carrega mensagens da discussão
        carregarMensagensDaDiscussao()

        binding.btnEnviarMensagem.setOnClickListener {
            enviarMensagem()
        }
    }

    private fun setupRecyclerView() {
        mensagemAdapter = MensagemDiscussaoAdapter(userId)
        binding.recyclerViewMensagens.apply {
            layoutManager = LinearLayoutManager(this@DiscussaoDetalheActivity)
            adapter = mensagemAdapter
        }
    }

    private fun observarMensagens() {
        lifecycleScope.launch {
            mensagemViewModel.mensagens.collectLatest { mensagens ->
                val mensagensFiltradas = mensagens.filter { it.discussao_id == discussaoId }
                mensagemAdapter.submitList(mensagensFiltradas)
            }
        }

        lifecycleScope.launch {
            mensagemViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@DiscussaoDetalheActivity, it, Toast.LENGTH_SHORT).show()
                    Log.e("DiscussaoDetalheActivity", "Erro: $it")
                    mensagemViewModel.resetErro()
                }
            }
        }
    }

    private fun carregarMensagensDaDiscussao() {
        mensagemViewModel.carregarMensagens()
    }

    private fun enviarMensagem() {
        val texto = binding.etMensagem.text.toString().trim()
        if (texto.isEmpty()) {
            Toast.makeText(this, "Digite uma mensagem", Toast.LENGTH_SHORT).show()
            return
        }

        val novaMensagem = MensagemDiscussao(
            discussao_id = discussaoId,
            autor_id = userId,
            mensagem = texto
        )

        mensagemViewModel.criarMensagem(novaMensagem)
        binding.etMensagem.text?.clear()
    }
}
