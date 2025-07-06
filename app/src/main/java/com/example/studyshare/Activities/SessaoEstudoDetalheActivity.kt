package com.example.studyshare.Activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.studyshare.DataClasses.SessaoEstudo
import com.example.studyshare.R
import com.example.studyshare.Repositories.SessaoEstudoRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.SessaoEstudoViewModelFactory
import com.example.studyshare.ViewModels.SessaoEstudoViewModel
import com.example.studyshare.databinding.ActivitySessaoEstudoDetalheBinding
import kotlinx.coroutines.launch
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

class SessaoEstudoDetalheActivity : BaseActivity() {

    private lateinit var binding: ActivitySessaoEstudoDetalheBinding

    private val sessaoRepository = SessaoEstudoRepository(RetrofitClient.api)
    private val sessaoViewModel: SessaoEstudoViewModel by viewModels {
        SessaoEstudoViewModelFactory(sessaoRepository)
    }

    private var sessaoId: Int = -1
    private var userId: Int = -1
    private var sessao: SessaoEstudo? = null

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

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
                R.id.nav_categorias -> {
                    startActivity(Intent(this, AllCategoriasActivity::class.java))
                    true
                }
                else -> false
            }
        }

        observarSessao()
        observarUpdate()  // Novo: observa status do update
        sessaoViewModel.carregarSessaoById(sessaoId)

        binding.buttonEntrarVideochamada.setOnClickListener {
            val roomName = sessao?.videochamada_url
            Log.d("SessaoDetalhe", "Botão clicado, videochamada_url: $roomName")
            if (!roomName.isNullOrEmpty()) {
                if (hasPermissions()) {
                    iniciarVideochamada(roomName)
                } else {
                    requestPermissions()
                }
            } else {
                Toast.makeText(this, "Nenhum link de videochamada disponível", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonCriarVideochamada.setOnClickListener {
            val novoLink = "sessao${sessaoId}_videochamada"
            atualizarVideochamadaUrl(novoLink)
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun atualizarVideochamadaUrl(novoLink: String) {
        sessao?.let {
            val sessaoAtualizada = it.copy(videochamada_url = novoLink)
            it.id?.let { idNaoNulo ->
                sessaoViewModel.atualizarSessao(idNaoNulo, sessaoAtualizada)
            }
        }
    }

    private fun observarSessao() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sessaoViewModel.sessaoDetalhe.collect { s ->
                    if (s != null) {
                        sessao = s
                        Log.d("SessaoDetalhe", "Sessão recebida: $s")
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

    private fun observarUpdate() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sessaoViewModel.updateSuccess.collect { sucesso ->
                    sucesso?.let {
                        if (it) {
                            Toast.makeText(this@SessaoEstudoDetalheActivity, "Videochamada atualizada!", Toast.LENGTH_SHORT).show()
                            sessaoViewModel.carregarSessaoById(sessaoId) // Recarrega dados atualizados
                        } else {
                            Toast.makeText(this@SessaoEstudoDetalheActivity, "Falha ao atualizar videochamada", Toast.LENGTH_SHORT).show()
                        }
                        sessaoViewModel.resetUpdateStatus()
                    }
                }
            }
        }
    }

    private fun hasPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val micPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                micPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                sessao?.videochamada_url?.let { iniciarVideochamada(it) }
            } else {
                Toast.makeText(this, "Permissões de câmera e microfone são necessárias para a videochamada.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun iniciarVideochamada(roomName: String) {
        try {
            val serverURL = URL("https://meet.jit.si")

            val options = JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setRoom(roomName)
                .build()

            JitsiMeetActivity.launch(this@SessaoEstudoDetalheActivity, options)

        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao iniciar videochamada", Toast.LENGTH_SHORT).show()
            Log.e("SessaoDetalhe", "Erro Jitsi: ${e.message}")
        }
    }

    private fun mostrarDetalhes(sessao: SessaoEstudo) {
        Log.d("SessaoDetalhe", "mostrarDetalhes chamado com sessao: $sessao")
        Log.d("SessaoDetalhe", "Titulo: ${sessao.titulo}")
        Log.d("SessaoDetalhe", "Descrição: ${sessao.descricao}")
        Log.d("SessaoDetalhe", "DataHora: ${sessao.data_hora}")
        Log.d("SessaoDetalhe", "Estado: ${sessao.estado_sessao}")
        Log.d("SessaoDetalhe", "Videochamada URL: ${sessao.videochamada_url}")

        binding.tvTituloSessao.text = sessao.titulo ?: "Sem Título"
        binding.tvDescricaoSessao.text = sessao.descricao ?: "Sem Descrição"
        binding.tvDataHoraSessao.text = sessao.data_hora ?: "Data/Hora não definida"
        binding.tvEstadoSessao.text = "Estado: ${sessao.estado_sessao}"
        binding.buttonEntrarVideochamada.isEnabled = !sessao.videochamada_url.isNullOrEmpty()
    }
}
