package com.bs.sriwilis.nasabah.ui.home.catalog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityCatalogListBinding
import com.bs.sriwilis.nasabah.databinding.ActivityCategoryBinding
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.ui.home.HomeViewModel
import com.bs.sriwilis.nasabah.ui.home.category.CategoryAdapter
import com.bs.sriwilis.nasabah.utils.ViewModelFactory
import kotlinx.coroutines.launch

class CatalogListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogListBinding

    private lateinit var catalogAdapter: CatalogAdapter

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCatalogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        catalogAdapter = CatalogAdapter(emptyList(), this)

        lifecycleScope.launch {
            observeViewModel()
        }

        setupRecyclerView()

        binding.btnBack.setOnClickListener{
            onBackPressed()
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.rvCatalog.layoutManager = LinearLayoutManager(this)
        binding.rvCatalog.adapter = catalogAdapter
    }

    private fun observeViewModel() {
        viewModel.catalog.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val catalogDetails = result.data
                    if (catalogDetails != null) {
                        catalogAdapter.updateCatalog(catalogDetails)
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
        lifecycleScope.launch {
            viewModel.getCatalog()
            viewModel.syncData()
        }
    }
}