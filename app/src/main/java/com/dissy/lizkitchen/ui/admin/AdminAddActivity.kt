package com.dissy.lizkitchen.ui.admin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.dissy.lizkitchen.R
import com.dissy.lizkitchen.databinding.ActivityAdminAddBinding
import com.dissy.lizkitchen.utility.createCustomTempFile
import com.dissy.lizkitchen.utility.uriToFile
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class AdminAddActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var photoPath: String
    val storage = Firebase.storage
    private var file: File? = null
    private val binding by lazy { ActivityAdminAddBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Logika Currency Formatter
        val etHargaKue: EditText = binding.etHarga
        etHargaKue.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (isUpdating) {
                    return
                }

                isUpdating = true

                val originalText = s.toString()

                // Hapus semua tanda titik sebelum memformat angka
                val cleanText = originalText.replace(".", "")

                // Format ulang angka dengan menambahkan titik setiap 3 angka
                val formattedText = formatCurrency(cleanText)

                // Set teks yang telah diformat ke EditText
                etHargaKue.setText(formattedText)

                // Posisikan kursor di akhir teks
                etHargaKue.setSelection(formattedText.length)

                isUpdating = false
            }

            private fun formatCurrency(value: String): String {
                // Hapus tanda minus jika ada
                var isNegative = false
                var cleanValue = value
                if (cleanValue.startsWith("-")) {
                    isNegative = true
                    cleanValue = cleanValue.substring(1)
                }

                // Format ulang angka dengan menambahkan titik setiap 3 angka
                val stringBuilder = StringBuilder(cleanValue)
                val length = stringBuilder.length
                var i = length - 3
                while (i > 0) {
                    stringBuilder.insert(i, ".")
                    i -= 3
                }

                // Tambahkan tanda minus kembali jika angka negatif
                if (isNegative) {
                    stringBuilder.insert(0, "-")
                }

                return stringBuilder.toString()
            }
        })

        binding.btnCamera.setOnClickListener {
            startCamera()
        }

        binding.btnGaleri.setOnClickListener {
            startGallery()
        }

        binding.btnToHome.setOnClickListener {
            Intent(this, AdminCakeActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnUpdateData.setOnClickListener {
            val namaKue = binding.etNamaKue.text.toString()
            val harga = binding.etHarga.text.toString()
            val stok = binding.etStok.text.toString()
            val gambar = file
            if (gambar != null && namaKue.isNotEmpty() && harga.isNotEmpty() && stok.isNotEmpty()) {
                uploadImageAndGetUrl(namaKue, harga, stok.toLong(), gambar)
            } else {
                Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageAndGetUrl(namaKue: String, harga: String, stok: Long, gambar: File) {
        binding.apply {
            progressBar2.visibility = View.VISIBLE
            etNamaKue.isEnabled = false
            etHarga.isEnabled = false
            etStok.isEnabled = false
            btnCamera.isEnabled = false
            btnGaleri.isEnabled = false
        }
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${namaKue}")
        val uploadTask = imageRef.putFile(Uri.fromFile(gambar))

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val url = uri.toString()
                Log.d("AdminAddActivity", "URL: $url")
                val data = hashMapOf(
                    "namaKue" to namaKue,
                    "harga" to harga,
                    "stok" to stok,
                    "imageUrl" to url
                )
                db.collection("cakes")
                    .add(data)
                    .addOnSuccessListener { documentReference ->
                        val generatedDocumentId = documentReference.id
                        db.collection("cakes").document(generatedDocumentId)
                            .update("documentId", generatedDocumentId)
                            .addOnSuccessListener {
                                binding.apply {
                                    progressBar2.visibility = View.GONE
                                    etNamaKue.isEnabled = true
                                    etHarga.isEnabled = true
                                    etStok.isEnabled = true
                                    btnCamera.isEnabled = true
                                    btnGaleri.isEnabled = true
                                }
                                Log.d(
                                    "AdminAddActivity",
                                    "DocumentSnapshot added with ID: $generatedDocumentId"
                                )
                                Toast.makeText(
                                    this,
                                    "Data berhasil ditambahkan",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Intent(this, AdminCakeActivity::class.java).also {
                                    startActivity(it)
                                    finish()
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w("AdminAddActivity", "Error updating document", e)
                                Toast.makeText(
                                    this,
                                    "Error updating document: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        Intent(this, AdminCakeActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w("AdminAddActivity", "Error adding document", e)
                        Toast.makeText(
                            this,
                            "Error adding document: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    //Permission Function
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 123
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    //Camera Function
    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this, "com.dissy.lizkitchen", it
            )
            photoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(photoPath)
            file = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            Glide.with(this)
                .load(result)
                .into(binding.ivBanner)
        }
    }

    //Gallery Function
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            file = myFile
            Glide.with(this)
                .load(selectedImg)
                .into(binding.ivBanner)
        }
    }
}