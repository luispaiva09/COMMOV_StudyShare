package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import com.example.studyshare.R
import com.example.studyshare.databinding.ActivityInicioBinding

class InicioActivity : BaseActivity() {

    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader(
            drawerLayout = binding.drawerLayoutInicio,
            headerLayout = binding.headerLayoutInicio.root
        )

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)
        val username = sharedPref.getString("username", null)

        binding.textViewWelcome.text = "Bem-vindo, $username!"

        binding.buttonContinuar.setOnClickListener {
            val intent = Intent(this, AddMaterialActivity::class.java)
            startActivity(intent)
        }

        binding.buttonCategoria.setOnClickListener {
            val intent = Intent(this, AddCategoriaActivity::class.java)
            startActivity(intent)
        }

        binding.buttonAddDiscussao.setOnClickListener {
            val intent = Intent(this, AddDiscussaoActivity::class.java)
            startActivity(intent)
        }

        binding.buttonAddSessaoEstudo.setOnClickListener {
            val intent = Intent(this, AddSessaoEstudoActivity::class.java)
            startActivity(intent)
        }

        binding.navigationViewInicio.setNavigationItemSelectedListener { menuItem ->
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
                else -> true
            }
        }
    }
}
