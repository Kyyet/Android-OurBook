package com.example.ourbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: BookDatabaseHelper
    private lateinit var booksAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        db = BookDatabaseHelper(this)
        booksAdapter = BookAdapter(db.getAllBooks(), this)
        setContentView(binding.root)

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = booksAdapter

        binding.buttonadd.setOnClickListener{
            val intent = Intent(this, AddBook::class.java)
            startActivity(intent)
            finish()
        }

        binding.buttonask.setOnClickListener{
            val intent = Intent(this, AboutBook::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        booksAdapter.refreshData(db.getAllBooks())
    }
}