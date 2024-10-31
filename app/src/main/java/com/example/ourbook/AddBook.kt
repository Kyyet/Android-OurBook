package com.example.ourbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ourbook.databinding.ActivityAboutBookBinding
import com.example.ourbook.databinding.ActivityAddBookBinding
import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.ourbook.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddBook : AppCompatActivity() {
    private lateinit var binding: ActivityAddBookBinding
    private lateinit var db: BookDatabaseHelper
    val CAMERA_REQUEST = 100
    val STORAGE_PERMISSION = 101

    val cameraPermissions: Array<String> = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val storagePermissions: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri: Uri? = result.uriContent
            Picasso.get().load(uri).into(binding.addfoto)
        } else {
            val error = result.error
            error?.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = BookDatabaseHelper(this)

        binding.addfoto.setOnClickListener {
            var avatar = 0
            if (avatar == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPersmission()
                } else {
                    pickFromGallery()
                }
            } else if (avatar == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        }

        binding.tanggalLahir.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    binding.tanggalLahir.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        binding.buttonadd.setOnClickListener {
            val name = binding.namalengkap.text.toString()
            val surename = binding.namaPanggilan.text.toString()
            val email = binding.email.text.toString()
            val address = binding.alamat.text.toString()
            val date = binding.tanggalLahir.text.toString()
            val hp = binding.noHp.text.toString()
            val emailPattern = "^[\\w.-]+@[\\w.-]+\\.com$".toRegex()

            when {
                name.isEmpty() || surename.isEmpty() || email.isEmpty() || address.isEmpty() ||
                        date.isEmpty() || hp.isEmpty() -> {
                    Toast.makeText(this, "Book cannot be empty!", Toast.LENGTH_SHORT).show()
                }

                !emailPattern.matches(email) -> {
                    Toast.makeText(
                        this,
                        "Please enter a valid email with '@' and '.com'",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val book = Book(
                        0,
                        name,
                        surename,
                        email,
                        address,
                        date,
                        hp,
                        db.ImageViewToByte(binding.addfoto)
                    )

                    AlertDialog.Builder(this).apply {
                        setTitle("Save Confirmation")
                        setMessage("Are you sure?")
                        setPositiveButton("Yes") { _, _ ->
                        db.insertBook(book)
                        val intent = Intent(this@AddBook, MainActivity::class.java)
                        startActivity(intent)
                            finish()
                        Toast.makeText(this@AddBook, "Book Saved", Toast.LENGTH_SHORT).show()
                    }
                        setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                            Toast.makeText(this@AddBook, "Book not saved", Toast.LENGTH_SHORT).show()
                        }
                    }.show()
                }
            }
        }
    }

    private fun requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_PERMISSION)
    }

    private fun checkStoragePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        return result
    }

    private fun pickFromGallery() {
        cropImageLauncher.launch(CropImageContractOptions(null, CropImageOptions()))
    }

    private fun requestCameraPersmission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST)
    }

    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        val result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED)
        return result && result2
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.size > 0) {
                    val cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccept) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(
                            this,
                            "Enable Camera and Storage Permissions",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            STORAGE_PERMISSION -> {
                if (grantResults.size > 0) {
                    val storegaAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storegaAccept) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(this, "Enable Storage Permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}
