package com.example.studyshare.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.studyshare.DataClasses.Categoria
import com.example.studyshare.DataClasses.MaterialDidatico
import com.example.studyshare.R
import com.example.studyshare.Repositories.CategoriaRepository
import com.example.studyshare.Repositories.MaterialDidaticoRepository
import com.example.studyshare.RetrofitClient
import com.example.studyshare.ViewModelFactories.CategoriaViewModelFactory
import com.example.studyshare.ViewModelFactories.MaterialDidaticoViewModelFactory
import com.example.studyshare.ViewModels.CategoriaViewModel
import com.example.studyshare.ViewModels.MaterialDidaticoViewModel
import com.example.studyshare.databinding.ActivityAddMaterialBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class AddMaterialActivity : BaseActivity() {

    private lateinit var binding: ActivityAddMaterialBinding

    private val materialRepository = MaterialDidaticoRepository(RetrofitClient.api)
    private val categoriaRepository = CategoriaRepository(RetrofitClient.api)

    private val materialViewModel: MaterialDidaticoViewModel by viewModels {
        MaterialDidaticoViewModelFactory(materialRepository)
    }

    private val categoriaViewModel: CategoriaViewModel by viewModels {
        CategoriaViewModelFactory(categoriaRepository)
    }

    private var listaCategorias: List<Categoria> = emptyList()

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            drawerLayout = binding.drawerLayoutAddMaterial,
            headerLayout = binding.headerLayout.root
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val autorId = sharedPref.getInt("userId", -1)

        binding.navigationViewAddMaterial.setNavigationItemSelectedListener { menuItem ->
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
            materialViewModel.materialCriado.collectLatest { sucesso ->
                if (sucesso == true) {
                    Toast.makeText(this@AddMaterialActivity, "Material adicionado com sucesso!", Toast.LENGTH_LONG).show()
                    limparCampos()
                    val intent = Intent(this@AddMaterialActivity, InicioActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            materialViewModel.erroMensagem.collectLatest { erro ->
                erro?.let {
                    Toast.makeText(this@AddMaterialActivity, "Erro: $it", Toast.LENGTH_LONG).show()
                    Log.e("AddMaterialActivity", "Erro ao criar material", Throwable(it))
                }
            }
        }

        lifecycleScope.launch {
            categoriaViewModel.carregarCategorias()
        }

        lifecycleScope.launch {
            categoriaViewModel.categorias.collectLatest { categorias ->
                listaCategorias = categorias
                val nomesCategorias = categorias.map { it.nome }
                val adapter = ArrayAdapter(this@AddMaterialActivity, android.R.layout.simple_spinner_item, nomesCategorias)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategorias.adapter = adapter
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
            val tipo = binding.etTipo.text.toString().trim()
            val ficheiroUrl = binding.etFicheiroUrl.text.toString().trim()
            val privado = binding.checkBoxPrivado.isChecked

            if (titulo.isEmpty() || tipo.isEmpty() || ficheiroUrl.isEmpty() || autorId == -1) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoriaSelecionada = listaCategorias.getOrNull(binding.spinnerCategorias.selectedItemPosition)
            if (categoriaSelecionada == null) {
                Toast.makeText(this, "Selecione uma categoria!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                var imagemCapaUrl: String? = null

                if (imageUri != null) {
                    imagemCapaUrl = withContext(Dispatchers.IO) {
                        uploadImagemParaSupabase(imageUri!!)
                    }
                    Log.d("UploadImagem", "URL da imagem após upload: $imagemCapaUrl")
                }

                val novoMaterial = MaterialDidatico(
                    titulo = titulo,
                    descricao = descricao,
                    imagem_capa_url = imagemCapaUrl,
                    tipo = tipo,
                    categoria_id = categoriaSelecionada.id,
                    autor_id = autorId,
                    ficheiro_url = ficheiroUrl,
                    privado = privado
                )

                materialViewModel.criarMaterial(novoMaterial)
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
        binding.etTipo.text?.clear()
        binding.etFicheiroUrl.text?.clear()
        binding.checkBoxPrivado.isChecked = false
        binding.spinnerCategorias.setSelection(0)
        imageUri = null
        materialViewModel.resetErro()
    }

    private fun uploadImagemParaSupabase(uri: Uri): String? {
        return try {
            val contentResolver = contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val fileName = "capa_${System.currentTimeMillis()}.jpg"

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    fileName,
                    bytes.toRequestBody("image/jpeg".toMediaType())
                )
                .build()

            val request = Request.Builder()
                .url("https://zktwurzgnafkwxqfwmjj.supabase.co/storage/v1/object/imagens-capa/$fileName")
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InprdHd1cnpnbmFma3d4cWZ3bWpqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEyMTY4MDAsImV4cCI6MjA2Njc5MjgwMH0.ivWULQ1yq0B-I3rLqEsF7Xrfzr4lIKFOb5Q-PR-XIx0")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InprdHd1cnpnbmFma3d4cWZ3bWpqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEyMTY4MDAsImV4cCI6MjA2Njc5MjgwMH0.ivWULQ1yq0B-I3rLqEsF7Xrfzr4lIKFOb5Q-PR-XIx0")
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                "https://zktwurzgnafkwxqfwmjj.supabase.co/storage/v1/object/public/imagens-capa/$fileName"
            } else {
                Log.e("UploadImagem", "Erro: ${response.code}: ${response.message}")
                null
            }
        } catch (e: Exception) {
            Log.e("UploadImagem", "Erro na exceção: ${e.message}")
            null
        }
    }
}
