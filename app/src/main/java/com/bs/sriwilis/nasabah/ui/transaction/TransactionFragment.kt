package com.bs.sriwilis.nasabah.ui.transaction

import TabAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.FragmentTransactionBinding
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.ui.home.HomeViewModel
import com.bs.sriwilis.nasabah.ui.home.category.CategoryAdapter
import com.bs.sriwilis.nasabah.ui.setting.SettingViewModel
import com.bs.sriwilis.nasabah.utils.ViewModelFactory
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class TransactionFragment : Fragment() {
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!
    private var selectedTabOption: String = "Semua"
    private var selectedJenisPenarikan: String = "Semua"

    private lateinit var transactionAdapter: TransactionAdapter

    private val viewModel by viewModels<TransactionViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            viewModel.filterDataTransaction(selectedTabOption, selectedJenisPenarikan)
        }

        lifecycleScope.launch {
            observeViewModel()
        }

        lifecycleScope.launch {
            homeViewModel.getLoggedInAccount()
        }

        transactionAdapter = TransactionAdapter(emptyList(), requireContext())
        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabs = listOf("Semua", "Diproses", "Berhasil", "Gagal")
        binding.rvHorizontalTabs.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHorizontalTabs.adapter = TabAdapter(tabs){ selectedValueTab ->
            when (selectedValueTab) {
                0 -> selectedTabOption = "Semua"
                1 -> selectedTabOption = "Diproses"
                2 -> selectedTabOption = "Berhasil"
                3 -> selectedTabOption = "Gagal"
            }
            viewModel.filterDataTransaction(selectedTabOption, selectedJenisPenarikan)
        }

        binding.btnStartMutation.setOnClickListener {
            val intent = Intent(context, CreateTransactionActivity::class.java)
            startActivity(intent)
        }

        binding.btnFilterTransaction.setOnClickListener {
            showPopupMenu(binding.btnFilterTransaction)
        }
    }

    private fun setupRecyclerView() {
        binding.rvMutationHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMutationHistory.adapter = transactionAdapter
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_filter_transaction, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.filter_all -> {
                    selectedJenisPenarikan = "Semua"
                    viewModel.filterDataTransaction(selectedTabOption, selectedJenisPenarikan)
                    true
                }
                R.id.pln -> {
                    selectedJenisPenarikan = "PLN"
                    viewModel.filterDataTransaction(selectedTabOption, selectedJenisPenarikan)
                    true
                }
                R.id.transfer -> {
                    selectedJenisPenarikan = "Transfer"
                    viewModel.filterDataTransaction(selectedTabOption, selectedJenisPenarikan)
                    true
                }
                R.id.tunai -> {
                    selectedJenisPenarikan = "Tunai"
                    viewModel.filterDataTransaction(selectedTabOption, selectedJenisPenarikan)
                    true
                }
                else -> false
            }

        }
        popupMenu.show()
    }

    private fun observeViewModel() {
        viewModel.penarikans.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val penarikanDetails = result.data
                    if (penarikanDetails != null) {
                        transactionAdapter.updateTransaction(penarikanDetails)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(requireContext()).apply {
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

        homeViewModel.loggedInAccount.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val loggedAccount = result.data
                    val doubleBalance = loggedAccount?.saldo_nasabah?.toDoubleOrNull() ?: 0.0
                    val intBalance = doubleBalance.toInt()
                    val formattedBalance = "Rp $intBalance"
                    binding.tvMutationBalance.text = formattedBalance
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Gagal memuat data: ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    Log.d("Loading", "Loading")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.filterDataTransaction(selectedTabOption, selectedJenisPenarikan)
        }
        lifecycleScope.launch {
            observeViewModel()
            homeViewModel.getLoggedInAccount()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
