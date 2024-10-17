package com.bs.sriwilis.nasabah.ui.order.pickupwaste

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.data.model.CardPesanan
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.databinding.ActivityPickUpWasteBinding
import com.bs.sriwilis.nasabah.ui.order.OrderViewModel
import com.bs.sriwilis.nasabah.utils.ViewModelFactory
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class PickUpWasteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPickUpWasteBinding
    private lateinit var adapter: PickUpAdapter

    private val viewModel by viewModels<OrderViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickUpWasteBinding.inflate(layoutInflater)

        setupRecyclerView()
        lifecycleScope.launch {
            viewModel.getCombinedPesananData()
        }

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.syncDataPesananSampah()
                viewModel.getCombinedPesananData()
            }
        }
        viewModel.filterData("semua")

        observeViewModel()

        binding.menuIcon.setOnClickListener {
            showPopupMenu(binding.menuIcon)
        }

        setContentView(binding.root)
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_filter_history, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.filter_all -> {
                    viewModel.filterData("semua")
                    true
                }
                R.id.filter_completed -> {
                    viewModel.filterData("selesai diantar")
                    true
                }
                R.id.filter_failed -> {
                    viewModel.filterData("gagal")
                    true
                }R.id.filter_scheduled -> {
                    viewModel.filterData("sudah dijadwalkan")
                    true
                }R.id.filter_pending -> {
                    viewModel.filterData("pending")
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun setupRecyclerView() {
        adapter = PickUpAdapter(
            emptyList(),
            this,
            object : PickUpAdapter.OnApproveClickListener {
                override fun onApproveClick(idPesanan: String) {

                }

            }
        )
        binding.rvPesananSelesai.layoutManager = LinearLayoutManager(this)
        binding.rvPesananSelesai.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.pickUpPesanans.observe(this) { result ->
            binding.swipeRefreshLayout.isRefreshing = false
            when (result) {
                is Result.Success -> {
                    result.data.let { adapter.updatePesanan(it) }
                }
                is Result.Error -> {
                    Toast.makeText(this, "Gagal memuat data: ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                }
            }
        }
    }
}