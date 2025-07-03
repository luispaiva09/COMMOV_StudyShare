package com.example.studyshare.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.studyshare.databinding.ActivityInicioBinding

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username") ?: "Utilizador"
        binding.textViewWelcome.text = "Bem-vindo, $username!"

        val userId = intent.getIntExtra("userId", -1)

        binding.buttonContinuar.setOnClickListener {
            val intent = Intent(this, AddMaterialActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        binding.buttonCategoria.setOnClickListener {
            val intent = Intent(this, AddCategoriaActivity::class.java)
            startActivity(intent)
        }

    }

}
