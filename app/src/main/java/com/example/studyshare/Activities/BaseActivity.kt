package com.example.studyshare.Activities

import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.studyshare.R
import com.google.android.material.navigation.NavigationView

open class BaseActivity : AppCompatActivity() {

    protected fun setupHeader(headerLayout: View, drawerLayout: DrawerLayout? = null, navigationView: NavigationView? = null) {
        val logo = headerLayout.findViewById<ImageView>(R.id.imageViewLogo)
        val perfilButton = headerLayout.findViewById<ImageButton>(R.id.buttonPerfil)
        val menuButton = headerLayout.findViewById<ImageButton>(R.id.buttonMenu)

        logo.setOnClickListener {
            if (this !is InicioActivity) {
                val intent = Intent(this, InicioActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        perfilButton.setOnClickListener {
            if (this !is PerfilActivity) {
                val intent = Intent(this, PerfilActivity::class.java)
                startActivity(intent)
            } else {
                drawerLayout?.closeDrawer(GravityCompat.END)
            }
        }

        menuButton.setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.END)
        }

        navigationView?.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
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
                R.id.nav_pesquisar -> {
                    startActivity(Intent(this, PesquisaActivity::class.java))
                    true
                }
                else -> false
            }.also {
                drawerLayout?.closeDrawer(GravityCompat.END)
            }
        }
    }
}
