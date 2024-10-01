package com.bs.sriwilis.nasabah.ui.order.transactionwaste

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityTransactionWasteBinding
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.ui.order.OrderViewModel
import com.bs.sriwilis.nasabah.utils.ViewModelFactory
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class TransactionWasteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionWasteBinding
    private lateinit var adapter: TransactionWasteAdapter

    private val viewModel by viewModels<OrderViewModel> {
        ViewModelFactory.getInstance(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionWasteBinding.inflate(layoutInflater)

        setupRecyclerView()
        lifecycleScope.launch {
            viewModel.getCombinedTransactionWaste()
        }

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.syncData()
                viewModel.getCombinedTransactionWaste()
            }
        }
        viewModel.filterTransactionWaste("semua")

        observeViewModel()

        binding.menuIcon.setOnClickListener {
            showPopupMenu(binding.menuIcon)
        }

        setContentView(binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_filter_transaction_waste, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.filter_all -> {
                    viewModel.filterTransactionWaste("semua")
                    true
                }
                R.id.today-> {
                    viewModel.filterTransactionWaste("today")
                    true
                }
                R.id.week-> {
                    viewModel.filterTransactionWaste("week")
                    true
                }R.id.month -> {
                viewModel.filterTransactionWaste("month")
                true
                }
                    else -> false
                }
        }
        popupMenu.show()
    }

    private fun setupRecyclerView() {
        adapter = TransactionWasteAdapter(
            emptyList(),
            this,
            object : TransactionWasteAdapter.OnApproveClickListener {
                override fun onApproveClick(idPesanan: String) {

                }

            }
        )
        binding.rvTransaksiSampah.layoutManager = LinearLayoutManager(this)
        binding.rvTransaksiSampah.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.transactioWastePesanans.observe(this) { result ->
            binding.swipeRefreshLayout.isRefreshing = false
            when (result) {
                is Result.Success -> {
                    result.data.let { adapter.updatePesanan(it) }
                }
                is Result.Error -> {
                    Toast.makeText(this, "Gagal memuat data: ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    Log.d("Loading", "Loading")
                }
            }
        }
    }
}