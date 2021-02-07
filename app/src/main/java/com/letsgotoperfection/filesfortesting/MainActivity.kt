package com.letsgotoperfection.filesfortesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FilesForTesting_Light)
        setContentView(R.layout.activity_main)
    }
}