package com.bs.sriwilis.nasabah.ui.home.category

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityCategoryBinding
import com.bs.sriwilis.nasabah.ui.home.HomeViewModel
import com.bs.sriwilis.nasabah.utils.ViewModelFactory
import com.bs.sriwilis.nasabah.helper.Result
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var categoryLauncher: ActivityResultLauncher<Intent>
    private lateinit var categoryAdapter: CategoryAdapter

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        categoryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch {
                    viewModel.filterData("semua")
                }
            }
        }

        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryAdapter = CategoryAdapter(emptyList(), this)

        lifecycleScope.launch {
            observeViewModel()
        }

        setupRecyclerView()

        binding.btnFilterCategory.setOnClickListener {
            showPopupMenu(binding.btnFilterCategory)
        }

        binding.btnBack.setOnClickListener{
            onBackPressed()
            finish()
        }

        super.onCreate(savedInstanceState)
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_filter_category, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.filter_all -> {
                    viewModel.filterData("semua")
                    true
                }
                R.id.plastik -> {
                    viewModel.filterData("plastik")
                    true
                }
                R.id.kaleng -> {
                    viewModel.filterData("kaleng")
                    true
                }
                R.id.kertas -> {
                    viewModel.filterData("kertas")
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun setupRecyclerView() {
        binding.rvCategory.layoutManager = LinearLayoutManager(this)
        binding.rvCategory.adapter = categoryAdapter
    }

    private fun observeViewModel() {
        viewModel.categories.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val categoryDetails = result.data
                    if (categoryDetails != null) {
                        categoryAdapter.updateCategory(categoryDetails)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle("Gagal!")
                        setMessage("Gagal memuat data")
                        setPositiveButton("OK", null)
                        create()
                        show()
                    }
                }
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Log.d("Loading", "Loading")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
}