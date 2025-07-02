package com.example.studyshare

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val username = intent.getStringExtra("username")
        findViewById<TextView>(R.id.textViewWelcome).text = "Bem-vindo, $username!"

    }
}