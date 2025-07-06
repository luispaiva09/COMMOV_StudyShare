package com.example.studyshare.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.studyshare.R
import com.example.studyshare.Repositories.UtilizadorRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.UtilizadorViewModelFactory
import com.example.studyshare.ViewModels.UtilizadorViewModel
import com.example.studyshare.databinding.ActivityPerfilBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class PerfilActivity : BaseActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private val repository = UtilizadorRepository(RetrofitClient.api)
    private val viewModel: UtilizadorViewModel by viewModels { UtilizadorViewModelFactory(repository) }

    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let {
                binding.imageViewPerfil.setImageURI(it)
                Toast.makeText(this, "Foto de perfil selecionada.", Toast.LENGTH_SHORT).show()

                lifecycleScope.launch {
                    // Upload da imagem
                    uploadedImageUrl = withContext(Dispatchers.IO) {
                        uploadImagemParaSupabase(it)
                    }

                    if (uploadedImageUrl != null) {
                        Toast.makeText(this@PerfilActivity, "Imagem carregada com sucesso!", Toast.LENGTH_SHORT).show()
                        Log.d("PerfilActivity", "URL da imagem: $uploadedImageUrl")

                        val utilizador = viewModel.utilizadorPerfil.value
                        if (utilizador != null) {
                            val atualizado = utilizador.copy(foto_perfil_url = uploadedImageUrl)
                            utilizador.id?.let { id ->
                                // Atualiza o utilizador no backend
                                val dadosUpdate = mapOf("foto_perfil_url" to uploadedImageUrl!!)
                                viewModel.updateUtilizadorParcial(id, dadosUpdate)

                                // Aguarda um pouco para garantir que a atualização foi processada (ouça eventos no ViewModel para isso)
                                // Aqui só uma chamada para recarregar
                                viewModel.getUtilizadorById(id)
                            }
                        }
                    } else {
                        Toast.makeText(this@PerfilActivity, "Erro ao carregar imagem.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(binding.headerLayoutPerfil.root, binding.drawerLayoutPerfil, binding.navigationViewPerfil)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        if (userId != -1) {
            viewModel.getUtilizadorById(userId)
        }

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

                    it.foto_perfil_url?.let { url ->
                        Glide.with(this@PerfilActivity)
                            .load(url)
                            .into(binding.imageViewPerfil)
                    }
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

        binding.imageViewPerfil.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.buttonEditarPerfil.setOnClickListener {
            startActivity(Intent(this, EditarPerfilActivity::class.java))
        }

        binding.buttonAlterarPassword.setOnClickListener {
            startActivity(Intent(this, AlterarPasswordActivity::class.java))
        }

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
                else -> false
            }
        }
    }

    private fun uploadImagemParaSupabase(uri: Uri): String? {
        return try {
            val contentResolver = contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("UploadImagem", "Não foi possível abrir InputStream do Uri")
                return null
            }
            val bytes = inputStream.readBytes()
            inputStream.close()

            val fileName = "perfil_${System.currentTimeMillis()}.jpg"

            val mediaType = "image/jpeg".toMediaTypeOrNull()
            if (mediaType == null) {
                Log.e("UploadImagem", "MediaType inválido")
                return null
            }
            val requestBody = bytes.toRequestBody(mediaType)

            // Endpoint para upload (PUT) - URL do objeto no bucket "imagens-perfil"
            val url = "https://zktwurzgnafkwxqfwmjj.supabase.co/storage/v1/object/imagens-perfil/$fileName"

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InprdHd1cnpnbmFma3d4cWZ3bWpqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEyMTY4MDAsImV4cCI6MjA2Njc5MjgwMH0.ivWULQ1yq0B-I3rLqEsF7Xrfzr4lIKFOb5Q-PR-XIx0")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InprdHd1cnpnbmFma3d4cWZ3bWpqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEyMTY4MDAsImV4cCI6MjA2Njc5MjgwMH0.ivWULQ1yq0B-I3rLqEsF7Xrfzr4lIKFOb5Q-PR-XIx0")
                .addHeader("Content-Type", "image/jpeg")
                .put(requestBody)
                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                // URL pública para acessar imagem
                "https://zktwurzgnafkwxqfwmjj.supabase.co/storage/v1/object/public/imagens-perfil/$fileName"
            } else {
                val errorBody = response.body?.string()
                Log.e("UploadImagem", "Erro no upload: Código ${response.code} - $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("UploadImagem", "Exceção durante upload: ${e.localizedMessage ?: e.message}")
            null
        }
    }
}
