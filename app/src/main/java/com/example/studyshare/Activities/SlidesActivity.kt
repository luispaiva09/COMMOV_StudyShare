package com.example.studyshare.Activities

import SlideFragment
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.studyshare.Adapters.SlideAdapter
import com.example.studyshare.R

class SlidesActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        if (prefs.getBoolean("onboardingFinished", false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        setContentView(R.layout.activity_slides)

        viewPager = findViewById(R.id.viewPager)
        btnNext = findViewById(R.id.btnNext)

        val adapter = SlideAdapter(this)
        adapter.addFragment(SlideFragment.newInstance(R.drawable.logo, "Bem-vindo à StudyShare!", "Partilhe e Estude materiais educativos com outros estudantes!"))
        adapter.addFragment(SlideFragment.newInstance(R.drawable.logo2, "Diversidade de Perspetivas", "Com perspetivas diferentes de vários estudantes, ampliamos a visão sobre o estudo que estamos a realizar!"))
        adapter.addFragment(SlideFragment.newInstance(R.drawable.logo3, "Acesso a Recursos Complemetares", "Com a partilha de recursos entre vários estudantes, temos um maior escopo de material ao qual podemos utilizar o qual não estaria disponível individualmente!"))
        adapter.addFragment(SlideFragment.newInstance(R.drawable.logo4, "Aumento da produtividade", "Ao trabalhar em conjunto, podemos realizar trabalhos e projetos mais rapidamente devido à cooperação e divisão de tarefas!"))

        viewPager.adapter = adapter

        btnNext.visibility = View.GONE

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == adapter.itemCount - 1) {
                    btnNext.visibility = View.VISIBLE
                } else {
                    btnNext.visibility = View.GONE
                }
            }
        })

        btnNext.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem += 1
            } else {
                prefs.edit().putBoolean("onboardingFinished", true).apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}