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
import androidx.core.view.GravityCompat
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

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.imageViewPerfil.setImageURI(uri)
                uploadAndSaveProfileImage(uri)
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

        // Observers
        lifecycleScope.launchWhenStarted {
            viewModel.utilizadorPerfil.collect { utilizador ->
                utilizador?.let { updateUI(it) }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.erroMensagem.collect { erro ->
                erro?.let { Toast.makeText(this@PerfilActivity, it, Toast.LENGTH_SHORT).show() }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.updateSucesso.collect { sucesso ->
                sucesso?.let {
                    if (it) Toast.makeText(this@PerfilActivity, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
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

        binding.navigationViewPerfil.setNavigationItemSelectedListener { menuItem ->
            val drawer = binding.drawerLayoutPerfil
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    sharedPref.edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                R.id.nav_inicio -> startActivity(Intent(this, InicioActivity::class.java))
                R.id.nav_materiais -> startActivity(Intent(this, MyMateriaisActivity::class.java))
                R.id.nav_discussoes -> startActivity(Intent(this, MyDiscussoesActivity::class.java))
                R.id.nav_sessoes -> startActivity(Intent(this, MySessoesEstudoActivity::class.java))
                R.id.nav_categorias -> startActivity(Intent(this, AllCategoriasActivity::class.java))
                R.id.nav_pesquisar -> startActivity(Intent(this, PesquisaActivity::class.java))
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun updateUI(utilizador: com.example.studyshare.DataClasses.Utilizador) {
        binding.editTextNumero.setText(utilizador.id.toString())
        binding.editTextUsername.setText(utilizador.username ?: "")
        binding.editTextNome.setText(utilizador.nome ?: "")
        binding.editTextTelefone.setText(utilizador.n_telemovel?.toString() ?: "")
        binding.editTextEmail.setText(utilizador.email ?: "")
        binding.textViewNomePerfil.text = utilizador.username ?: "Perfil"

        utilizador.foto_perfil_url?.let { url ->
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(binding.imageViewPerfil)
        }
    }

    private fun uploadAndSaveProfileImage(uri: Uri) {
        lifecycleScope.launch {
            val imageUrl = withContext(Dispatchers.IO) { uploadImagemParaSupabase(uri) }

            imageUrl?.let { url ->
                viewModel.utilizadorPerfil.value?.id?.let { id ->
                    viewModel.updateUtilizadorParcial(id, mapOf("foto_perfil_url" to url))
                }
            } ?: Toast.makeText(this@PerfilActivity, "Erro ao carregar imagem.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImagemParaSupabase(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val fileName = "perfil_${System.currentTimeMillis()}.jpg"
            val mediaType = "image/jpeg".toMediaTypeOrNull() ?: return null
            val requestBody = bytes.toRequestBody(mediaType)

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
                "https://zktwurzgnafkwxqfwmjj.supabase.co/storage/v1/object/public/imagens-perfil/$fileName"
            } else {
                Log.e("UploadImagem", "Erro: ${response.code} ${response.body?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UploadImagem", "Exceção: ${e.localizedMessage}")
            null
        }
    }
}
