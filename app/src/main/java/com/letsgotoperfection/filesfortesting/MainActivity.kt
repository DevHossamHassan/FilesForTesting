package com.letsgotoperfection.filesfortesting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var btnVCards: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FilesForTesting_Light)
        setContentView(R.layout.activity_main)
        btnVCards = findViewById(R.id.btnVCards)
        btnVCards.setOnClickListener {
            startActivity(Intent(this, VCardsActivity::class.java))
        }
    }
}