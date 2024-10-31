package com.example.ourbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ourbook.databinding.ActivityAboutBookBinding
import com.example.ourbook.databinding.ActivityMainBinding

class AboutBook : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonback.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}