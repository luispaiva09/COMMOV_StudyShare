package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.studyshare.R

open class BaseActivity : AppCompatActivity() {

    protected fun setupHeader(headerLayout: View, drawerLayout: DrawerLayout? = null) {
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
    }
}
