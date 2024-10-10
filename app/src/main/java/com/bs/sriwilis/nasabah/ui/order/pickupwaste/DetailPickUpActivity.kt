package com.bs.sriwilis.nasabah.ui.order.pickupwaste

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityDetailPickUpBinding
import com.bs.sriwilis.nasabah.utils.ViewModelFactory
import com.bs.sriwilis.nasabah.helper.Result

@Suppress("DEPRECATION")
class PesananDetailActivity : AppCompatActivity() {

    private val pesananDetailViewModel by viewModels<PesananDetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailPickUpBinding
    private lateinit var adapter: ListDetailPesananAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPickUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val idPesanan = intent.getStringExtra("ID_PESANAN")


        setupDetailData()
        setupRecyclerView()
        if (idPesanan != null) {
            pesananDetailViewModel.getPesananSampah(idPesanan)
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
            pesananDetailViewModel.getDataDetailPesananSampahKeranjang(idPesanan)

            pesananDetailViewModel.pesanans.observe(this) { result ->
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
                        binding.tvStatusPesanan.text = detail.status_pesanan

                        if(detail.status_pesanan.lowercase() == "selesai diantar"){
                            binding.tvStatusPesanan.text = "Selesai"
                            binding.tvStatusPesanan.setBackgroundColor(ContextCompat.getColor(this, R.color.green_label))
                        }else if(detail.status_pesanan.lowercase() == "pending"){
                            binding.tvStatusPesanan.text = "Pending"
                            binding.tvStatusPesanan.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_primary))
                        }else if(detail.status_pesanan.lowercase() == "sudah dijadwalkan"){
                            binding.tvStatusPesanan.text = "Diproses"
                            binding.tvStatusPesanan.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_calm))
                        }else{
                            binding.tvStatusPesanan.text = "Gagal"
                            binding.tvStatusPesanan.setBackgroundColor(ContextCompat.getColor(this, R.color.red_primary))
                        }

                        binding.btnMapsDetailPesanan.setOnClickListener {
                            openMaps(detail.lat.toDouble(), detail.lng.toDouble())
                        }
                        binding.tvAlamatDetailPesanan.text = detail.alamat_nasabah
                    }
                    is Result.Error -> {
                        binding.progressBarDetail.visibility = View.GONE
                        Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openMaps(lat: Double, lng: Double) {
        val latString = lat.toString()
        val lngString = lng.toString()
        val mapUri = Uri.parse("https://maps.google.com/maps?daddr=$latString,$lngString")
        val intent = Intent(Intent.ACTION_VIEW, mapUri)
        startActivity(intent)
    }

    private fun convertDateToText(date: String): String {
        // Example function to convert date to a string with month name
        // Assuming input date format is "yyyy-MM-dd"
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


    private fun isPackageExisted(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun onDestroy() {
        pesananDetailViewModel.pesanans.removeObservers(this)
        pesananDetailViewModel.pesananSampah.removeObservers(this)
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        this.adapter = ListDetailPesananAdapter(
            emptyList(),
            this,
            pesananDetailViewModel
        )
        binding.rvPesanan.layoutManager = LinearLayoutManager(this)
        binding.rvPesanan.adapter = adapter
    }

    private fun observeViewModel() {
        pesananDetailViewModel.pesananSampah.observe(this) { result ->
            if (result is Result.Success) {
                result.data.let { adapter.updatePesanan(it) }
            }
        }
    }

}