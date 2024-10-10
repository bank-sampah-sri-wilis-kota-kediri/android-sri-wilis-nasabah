package com.bs.sriwilis.nasabah.ui.addorder

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.data.model.CartOrder
import com.bs.sriwilis.nasabah.databinding.ActivityAddOrderBinding
import com.bs.sriwilis.nasabah.utils.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class AddOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddOrderBinding
    private val viewModel by viewModels<AddOrderViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var lat: String? = null
    private var long: String? = null
    private var totalWeight: Int = 0
    private var totalPrice: Float = 0.0f
    private var cartItems = mutableListOf<CartOrder>()

    private lateinit var adapter: CartOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupRecyclerView()
        setupListeners()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_CART && resultCode == Activity.RESULT_OK) {
            val cartTransactions = data?.getParcelableArrayListExtra<CartOrder>("transaksi_sampah")
            cartTransactions?.forEach {
                Log.d("ReceivedTransaction", "Transaction: $it")
                adapter.addCartOrder(it)
                cartItems.add(it)
            }

            calculateTotal(cartItems)
        }
    }

    private fun calculateTotal(cartTransactions: List<CartOrder>) {
        totalWeight = cartTransactions.sumOf { it.berat_perkiraan }
        totalPrice = cartTransactions.sumOf { it.harga_perkiraan.toDouble() }.toFloat()

        Log.d("TotalCalculation", "Total weight: $totalWeight kg, Total price: Rp $totalPrice")

        binding.tvWeightEstimation.text = "$totalWeight kg"
        binding.tvPriceEstimation.text = "Rp $totalPrice"
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.rv_transaction_cart)
        val cartItems = mutableListOf<CartOrder>()
        adapter = CartOrderAdapter(cartItems)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        binding.apply {
            btnAddCart.setOnClickListener {
                val intent = Intent(this@AddOrderActivity, AddCartOrderActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_ADD_CART)
            }
            btnSave.setOnClickListener {
                lifecycleScope.launch { submitTransaction() }
            }
            btnDetectLocation.setOnClickListener {
                checkLocationPermissionAndGetLocation()
            }
            btnBack.setOnClickListener{
                onBackPressed()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkLocationPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getActualLocation()
        }
    }

    @SuppressLint("SetTextI18n", "MissingPermission")
    private fun getActualLocation() {
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) {
                binding.tvLatValue.text = "Lat: ${it.latitude}"
                binding.tvLongValue.text = "Long: ${it.longitude}"
                lat = it.latitude.toString()
                long = it.longitude.toString()
            } else {
                showToast("Failed to retrieve location. Please try again.")
            }
        }.addOnFailureListener {
            showToast("Error retrieving location: ${it.message}")
        }
    }

    private fun submitTransaction() {
        val lat = lat
        val long = long
        if (cartItems.isNotEmpty() && lat != null && long != null) {
            viewModel.addCartOrder(lat, long, cartItems)
            viewModel.orderResult.observe(this) { result ->
                when (result) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        showToast("Order successfully submitted")
                        finish()
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showToast("Error: ${result.error}")
                    }

                    else -> {}
                }
            }
        } else {
            showToast("Tolong tambahkan lokasi dan item sampah ke transaksi!")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getActualLocation()
            } else {
                showToast("Izin lokasi ditolak!")
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_ADD_CART = 1002
        private const val LOCATION_PERMISSION_REQUEST_CODE = 101
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
