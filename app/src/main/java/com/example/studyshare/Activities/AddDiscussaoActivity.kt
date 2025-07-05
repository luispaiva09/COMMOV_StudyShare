package com.example.studyshare.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.DataClasses.Discussao
import com.example.studyshare.R
import com.example.studyshare.Repositories.DiscussaoRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.DiscussaoViewModelFactory
import com.example.studyshare.ViewModels.DiscussaoViewModel
import com.example.studyshare.databinding.ActivityAddDiscussaoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class AddDiscussaoActivity : BaseActivity() {

    private lateinit var binding: ActivityAddDiscussaoBinding

    private val discussaoRepository = DiscussaoRepository(RetrofitClient.api)
    private val discussaoViewModel: DiscussaoViewModel by viewModels {
        DiscussaoViewModelFactory(discussaoRepository)
    }

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDiscussaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            drawerLayout = binding.drawerLayoutAddDiscussao,
            headerLayout = binding.headerLayoutAddDiscussao.root
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val criadorId = sharedPref.getInt("userId", -1)

        binding.navigationViewAddDiscussao.setNavigationItemSelectedListener { menuItem ->
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

        lifecycleScope.launch {
            discussaoViewModel.discussaoCriada.collectLatest { sucesso ->
                if (sucesso == true) {
                    Toast.makeText(this@AddDiscussaoActivity, "Discussão criada com sucesso!", Toast.LENGTH_LONG).show()
                    limparCampos()
                    startActivity(Intent(this@AddDiscussaoActivity, InicioActivity::class.java))
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            discussaoViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@AddDiscussaoActivity, "Erro: $it", Toast.LENGTH_LONG).show()
                    Log.e("AddDiscussaoActivity", "Erro ao criar discussão", Throwable(it))
                }
            }
        }

        binding.buttonEscolherImagem.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.buttonSubmeter.setOnClickListener {
            val titulo = binding.etTitulo.text.toString().trim()
            val descricao = binding.etDescricao.text.toString().trim().ifEmpty { null }

            if (titulo.isEmpty() || criadorId == -1) {
                Toast.makeText(this, "Preencha o título!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                var imagemDiscussaoUrl: String? = null

                if (imageUri != null) {
                    imagemDiscussaoUrl = withContext(Dispatchers.IO) {
                        uploadImagemParaSupabase(imageUri!!)
                    }
                    Log.d("UPLOAD_SUPABASE", "URL da imagem após upload: $imagemDiscussaoUrl")
                }

                val novaDiscussao = Discussao(
                    titulo = titulo,
                    descricao = descricao,
                    imagem_discussao_url = imagemDiscussaoUrl,
                    criador_id = criadorId
                )

                Log.d("DEBUG_NOVA_DISCUSSAO", "Discussão a enviar: $novaDiscussao")

                discussaoViewModel.criarDiscussao(novaDiscussao)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Toast.makeText(this, "Imagem selecionada!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limparCampos() {
        binding.etTitulo.text?.clear()
        binding.etDescricao.text?.clear()
        imageUri = null
        discussaoViewModel.resetErro()
    }

    private fun uploadImagemParaSupabase(uri: Uri): String? {
        return try {
            val contentResolver = contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val fileName = "discussao_${System.currentTimeMillis()}.jpg"

            val request = Request.Builder()
                .url("https://zktwurzgnafkwxqfwmjj.supabase.co/storage/v1/object/imagens-discussao/$fileName")
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InprdHd1cnpnbmFma3d4cWZ3bWpqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEyMTY4MDAsImV4cCI6MjA2Njc5MjgwMH0.ivWULQ1yq0B-I3rLqEsF7Xrfzr4lIKFOb5Q-PR-XIx0")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InprdHd1cnpnbmFma3d4cWZ3bWpqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEyMTY4MDAsImV4cCI6MjA2Njc5MjgwMH0.ivWULQ1yq0B-I3rLqEsF7Xrfzr4lIKFOb5Q-PR-XIx0")
                .addHeader("Content-Type", "image/jpeg")
                .put(bytes.toRequestBody("image/jpeg".toMediaType()))
                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val imageUrl = "https://zktwurzgnafkwxqfwmjj.supabase.co/storage/v1/object/public/imagens-discussao/$fileName"
                Log.d("UPLOAD_SUPABASE", "Imagem enviada com sucesso: $imageUrl")
                imageUrl
            } else {
                Log.e("UPLOAD_SUPABASE", "Erro: ${response.code}: ${response.message}")
                null
            }
        } catch (e: Exception) {
            Log.e("UPLOAD_SUPABASE", "Erro na exceção: ${e.message}")
            null
        }
    }
}
