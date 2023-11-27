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
import com.dissy.lizkitchen.databinding.ActivityCakeDetailBinding
import com.dissy.lizkitchen.utility.createCustomTempFile
import com.dissy.lizkitchen.utility.uriToFile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class CakeDetailActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var photoPath: String
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private var file: File? = null
    private var cakeImage: String? = null
    private val cakeCollection = db.collection("cakes")
    private var isImageChanged = false
    private val binding by lazy { ActivityCakeDetailBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val cakeId = intent.getStringExtra("documentId")

        if (cakeId != null) {
            binding.apply {
                progressBar2.visibility = View.VISIBLE
                etHarga.isEnabled = false
                etNamaKue.isEnabled = false
                etStok.isEnabled = false
                btnCamera.isEnabled = false
                btnGaleri.isEnabled = false
            }
            db.collection("cakes")
                .document(cakeId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("Firestore", "Listen failed.", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        binding.apply {
                            progressBar2.visibility = View.GONE
                            etHarga.isEnabled = true
                            etNamaKue.isEnabled = true
                            etStok.isEnabled = true
                            btnCamera.isEnabled = true
                            btnGaleri.isEnabled = true
                        }
                        val namaKue = snapshot.getString("namaKue")
                        val harga = snapshot.getString("harga")
                        val stok = snapshot.getString("stok")
                        val imageUrl = snapshot.getString("imageUrl")
                        file = File(imageUrl)
                        binding.apply {
                            etNamaKue.setText(namaKue)
                            etHarga.setText(harga)
                            etStok.setText(stok)
                            ivBanner.takeIf { !isDestroyed }?.let {
                                Glide.with(this@CakeDetailActivity)
                                    .load(imageUrl)
                                    .into(it)
                            }
                        }
                    } else {
                        Log.d("Firestore", "Current data: null")
                    }
                }
        }

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
            isImageChanged = true
        }

        binding.btnGaleri.setOnClickListener {
            startGallery()
            isImageChanged = true
        }

        binding.btnToHome.setOnClickListener {
            Intent(this, AdminActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnUpdateData.setOnClickListener {
            val namaKue = binding.etNamaKue.text.toString()
            val harga = binding.etHarga.text.toString()
            val stok = binding.etStok.text.toString()
            val imageUrl = file
            Log.d("CakeDetailActivity", "$namaKue, $harga, $stok, $imageUrl")
            if (namaKue.isNotEmpty() || harga.isNotEmpty() || stok.isNotEmpty() && imageUrl != null) {
                file?.let { it1 ->
                    uploadImageAndGetUrl(
                        cakeId as String, namaKue, harga, stok,
                        it1
                    )
                }
            } else {
                Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            isImageChanged = false
        }

        binding.btnDeleteData.setOnClickListener {
            if (cakeId != null) {
                cakeCollection.document(cakeId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                        Intent(this, AdminActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            this,
                            "Gagal menghapus data: ${exception.message}",
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

    private fun uploadImageAndGetUrl(
        cakeId: String,
        namaKue: String,
        harga: String,
        stok: String,
        gambar: File
    ) {
        binding.apply {
            progressBar2.visibility = View.VISIBLE
            etNamaKue.isEnabled = false
            etHarga.isEnabled = false
            etStok.isEnabled = false
            btnCamera.isEnabled = false
            btnGaleri.isEnabled = false
        }
        if (isImageChanged) {
            // Jika gambar berubah, upload gambar baru ke Firebase Storage
            val storageRef = storage.reference
            val imageRef = storageRef.child("images/${namaKue}")
            val uploadTask = imageRef.putFile(Uri.fromFile(gambar))
            Log.d("CakeDetailActivity", "uploadImageAndGetUrl: $gambar")
            uploadTask.addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val url = uri.toString()
                    // Jika gambar berhasil diupload, perbarui data di Firestore
                    updateDataInFirestore(cakeId, namaKue, harga, stok, url)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Gagal mengupload gambar: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (gambar is File) {
            // Jika gambar tidak berubah dan berupa File, gunakan URL gambar yang sudah ada di Firestore
            Log.d("CakeDetailActivity", "else 2 uploadImageAndGetUrl: $gambar")
            updateDataInFirestore(cakeId, namaKue, harga, stok, gambar.path)
        } else {
            // Kasus lainnya, misalnya jika gambar berupa URL String yang sudah ada di Firestore
            Log.d("CakeDetailActivity", "else 3 uploadImageAndGetUrl: $gambar")
            updateDataInFirestore(cakeId, namaKue, harga, stok, gambar as String)
        }
        binding.apply {
            progressBar2.visibility = View.GONE
            etNamaKue.isEnabled = true
            etHarga.isEnabled = true
            etStok.isEnabled = true
            btnCamera.isEnabled = true
            btnGaleri.isEnabled = true
        }
    }

    private fun updateDataInFirestore(
        cakeId: String,
        namaKue: String,
        harga: String,
        stok: String,
        imageUrl: String
    ) {
        val data = hashMapOf(
            "namaKue" to namaKue,
            "harga" to harga,
            "stok" to stok,
            "imageUrl" to imageUrl
        )

        // Perbarui data di Firestore
        cakeCollection.document(cakeId)
            .update(data as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                Intent(this, AdminActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Gagal mengupdate data: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}