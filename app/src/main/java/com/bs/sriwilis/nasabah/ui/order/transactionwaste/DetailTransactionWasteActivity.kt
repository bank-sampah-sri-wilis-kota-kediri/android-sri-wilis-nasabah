package com.bs.sriwilis.nasabah.ui.order.transactionwaste

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bs.sriwilis.nasabah.databinding.ActivityDetailTransactionWasteBinding
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.utils.ViewModelFactory

@Suppress("DEPRECATION")
class DetailTransactionWasteActivity : AppCompatActivity() {
    private val viewModel by viewModels<TransactionWasteDetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailTransactionWasteBinding
    private lateinit var adapter: ListDetailTransaksiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransactionWasteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val idPesanan = intent.getStringExtra("ID_PESANAN")


        setupDetailData()
        setupRecyclerView()
        if (idPesanan != null) {
            viewModel.getTransaksiSampah(idPesanan)
        }



        binding.btnBack.setOnClickListener{
            this.onBackPressed()
        }
        observeViewModel()
    }

    @SuppressLint("QueryPermissionsNeeded", "SetTextI18n")
    fun setupDetailData(){
        val idPesanan = intent.getStringExtra("ID_PESANAN")

        if(idPesanan!=null){
            viewModel.getDataDetailKeranjangTransaksi(idPesanan)

            viewModel.pesanans.observe(this) { result ->
                when(result) {
                    is Result.Loading -> {
                        binding.progressBarDetail.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBarDetail.visibility = View.GONE
                        val detail = result.data
                        binding.tvNamaDetailPesanan.text = detail.nama_nasabah
                        binding.tvTanggalDetailPesanan.text = convertDateToText(detail.tanggal)
                        binding.tvBeratDetailPesanan.text = "${detail.total_berat} Kg"
                        binding.tvNomorwaDetailPesanan.text = detail.no_hp_nasabah
                        binding.tvAlamatDetailPesanan.text = detail.alamat_nasabah
                        binding.tvNominalTransaksi.text = "+Rp${detail.nominal_transaksi}"
                    }
                    is Result.Error -> {
                        binding.progressBarDetail.visibility = View.GONE
                        Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun convertDateToText(date: String): String {
        val months = arrayOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus",
            "September", "Oktober", "November", "Desember"
        )
        val parts = date.split("-")
        val day = parts[2]
        val month = months[parts[1].toInt() - 1]
        val year = parts[0]
        return "$day $month $year"
    }


    override fun onDestroy() {
        viewModel.pesanans.removeObservers(this)
        viewModel.pesananSampah.removeObservers(this)
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        this.adapter = ListDetailTransaksiAdapter(
            emptyList(),
            this,
            viewModel
        )
        binding.rvPesanan.layoutManager = LinearLayoutManager(this)
        binding.rvPesanan.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.pesananSampah.observe(this) { result ->
            if (result is Result.Success) {
                result.data.let { adapter.updatePesanan(it) }
            }
        }
    }

}