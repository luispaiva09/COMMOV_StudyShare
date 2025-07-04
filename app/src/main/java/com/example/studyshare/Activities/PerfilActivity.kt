package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.studyshare.ApiService
import com.example.studyshare.R
import com.example.studyshare.Repositories.UtilizadorRepository
import com.example.studyshare.ViewModelFactories.UtilizadorViewModelFactory
import com.example.studyshare.ViewModels.UtilizadorViewModel
import com.google.android.material.navigation.NavigationView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PerfilActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var editTextNumero: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextNome: EditText
    private lateinit var editTextTelefone: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextDataNascimento: EditText
    private lateinit var editTextPassword: EditText

    private lateinit var textViewNomePerfil: TextView

    private val viewModel: UtilizadorViewModel by viewModels {
        UtilizadorViewModelFactory(UtilizadorRepository(getApiService()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        drawerLayout = findViewById(R.id.drawerLayoutPerfil)
        navigationView = findViewById(R.id.navigationViewPerfil)

        val buttonMenu = findViewById<ImageButton>(R.id.buttonMenu)
        val buttonPerfil = findViewById<ImageButton>(R.id.buttonPerfil)
        val buttonEditarPerfil = findViewById<Button>(R.id.buttonEditarPerfil)
        val buttonAlterarPassword = findViewById<Button>(R.id.buttonAlterarPassword)

        editTextNumero = findViewById(R.id.editTextNumero)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextNome = findViewById(R.id.editTextNome)
        editTextTelefone = findViewById(R.id.editTextTelefone)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextDataNascimento = findViewById(R.id.editTextDataNascimento)
        editTextPassword = findViewById(R.id.editTextPassword)

        textViewNomePerfil = findViewById(R.id.textViewNomePerfil)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        if (userId != -1) {
            viewModel.getUtilizadorById(userId)
        }

        // Observar os dados do utilizador
        viewModel.utilizadorPerfil.observe(this) { utilizador ->
            utilizador?.let {
                editTextNumero.setText(it.id.toString())
                editTextUsername.setText(it.username ?: "")
                editTextNome.setText(it.nome ?: "")
                editTextTelefone.setText(it.telemovel ?: "")
                editTextEmail.setText(it.email ?: "")
                editTextDataNascimento.setText(it.data_nascimento ?: "")
                editTextPassword.setText("********")  // Apenas para ocupar espaço

                textViewNomePerfil.text = it.username ?: "Perfil"
            }
        }

        viewModel.erroMensagem.observe(this) { erro ->
            erro?.let {
                // Se quiseres, podes mostrar um Toast ou Snackbar aqui
                // Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        buttonMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        buttonPerfil.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    val editor = sharedPref.edit()
                    editor.clear()
                    editor.apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_inicio -> {
                    startActivity(Intent(this, InicioActivity::class.java))
                    true
                }
                else -> false
            }
        }

        buttonEditarPerfil.setOnClickListener {
            // Exemplo: Abre uma Activity de edição ou desbloqueia os campos aqui
            // startActivity(Intent(this, EditarPerfilActivity::class.java))
        }

        buttonAlterarPassword.setOnClickListener {
            // Exemplo: Abre uma Activity ou Dialog para alterar a password
            // startActivity(Intent(this, AlterarPasswordActivity::class.java))
        }
    }

    private fun getApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://o-teu-backend-url.supabase.co/rest/v1/")  // substitui pela tua URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
