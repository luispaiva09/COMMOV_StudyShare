package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.studyshare.R
import com.example.studyshare.databinding.ActivityInicioBinding
import com.google.android.material.navigation.NavigationView

class InicioActivity : BaseActivity() {

    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)
        val username = sharedPref.getString("username", null)

        binding.textViewWelcome.text = "Bem-vindo, $username!"

        binding.buttonContinuar.setOnClickListener {
            val intent = Intent(this, AddMaterialActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        binding.buttonCategoria.setOnClickListener {
            val intent = Intent(this, AddCategoriaActivity::class.java)
            startActivity(intent)
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val buttonMenu = findViewById<ImageButton>(R.id.buttonMenu)
        val buttonProfile = findViewById<ImageButton>(R.id.buttonPerfil)

        buttonMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        buttonProfile.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    val editor = sharedPref.edit()
                    editor.clear()
                    editor.apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_materiais -> {
                    startActivity(Intent(this, MyMateriaisActivity::class.java))
                    true
                }
                else -> true
            }
        }
    }
}
