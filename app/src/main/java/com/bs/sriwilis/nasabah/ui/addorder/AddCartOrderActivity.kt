package com.bs.sriwilis.nasabah.ui.addorder

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.data.model.CartOrder
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.databinding.ActivityAddCartOrderBinding
import com.bs.sriwilis.nasabah.utils.ViewModelFactory

@Suppress("DEPRECATION")
class AddCartOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCartOrderBinding
    private val viewModel by viewModels<AddOrderViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var spinner: Spinner

    private var cartTransactions = mutableListOf<CartOrder>()
    private var selectedCategory: String? = null
    private var cartImage: Bitmap? = null
    private var basePrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCartOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spinner = binding.spinnerWasteCategory

        binding.apply {
            btnBack.setOnClickListener { finish() }
            btnUploadPhoto.setOnClickListener { openCamera() }
            btnSave.setOnClickListener { saveCartTransaction() }

            edtWasteWeight.addTextChangedListener {
                updatePrice()
            }

        }
        getCategorySpinner()
    }

    private fun updatePrice() {
        val weight = binding.edtWasteWeight.text.toString().toFloatOrNull() ?: 0.0f
        val calculatedPrice = basePrice * weight
        binding.edtWastePrice.setText(calculatedPrice.toString())
    }


    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            val photo: Bitmap? = data?.extras?.get("data") as? Bitmap
            if (photo != null) {
                cartImage = photo
                binding.ivCatalogPreview.setImageBitmap(photo)
            }
        }
    }

    private fun saveCartTransaction() {
        val selectedCategory = binding.spinnerWasteCategory.selectedItem.toString()
        val weight = binding.edtWasteWeight.text.toString().toFloatOrNull() ?: 0.0f
        val price = binding.edtWastePrice.text.toString().toDoubleOrNull() ?: 0.0
        val encodedImage = cartImage?.let { encodeImageToBase64(it) }

        if (selectedCategory.isEmpty()) {
            Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
        }

        val cartTransactionId = cartTransactions.size + 1

        val cartTransaction = CartOrder(
            kategori = selectedCategory,
            berat_perkiraan = weight,
            harga_perkiraan = price,
            gambar = encodedImage
        )

        cartTransactions.add(cartTransaction)

        val totalWeight = cartTransactions.map { it.berat_perkiraan }.sum()
        val totalPrice = cartTransactions.sumOf { it.harga_perkiraan }

        val intent = Intent().apply {
            putParcelableArrayListExtra("transaksi_sampah", ArrayList(cartTransactions))
            putExtra("total_weight", totalWeight)
            putExtra("total_price", totalPrice)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
    }

    private fun getCategorySpinner() {
        viewModel.categoryNames.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val categoryNames = result.data
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            selectedCategory = categoryNames[position] // This should now be just the name
                            val categoryItem = viewModel.getCategoryByPosition(position) // Retrieve full object
                            basePrice = categoryItem?.basePrice ?: 0.0 // Now retrieve base price if needed
                            Log.d("SelectedCategory", "Category: $selectedCategory, Base Price: $basePrice")

                            updatePrice()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) { }
                    }
                }

                is Result.Error -> {
                    Log.e("Spinner", "Error loading categories: ${result.error}")
                }

                Result.Loading -> {
                }
            }
        }
        viewModel.getCategoryDetails()
    }


    companion object {
        private const val REQUEST_CAMERA = 1001
    }
}