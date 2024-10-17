package com.bs.sriwilis.nasabah.ui.order.pickupwaste

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.data.model.CardPesanan
import com.bs.sriwilis.nasabah.databinding.CardPenjemputanBinding

class PickUpAdapter(
    private var pesanans: List<CardPesanan>,
    private val context: Context,
    private val listener: OnApproveClickListener
) : RecyclerView.Adapter<PickUpAdapter.PickUpViewHolder>() {


    interface OnApproveClickListener {
        fun onApproveClick(idPesanan: String)
    }

    inner class PickUpViewHolder(private val binding: CardPenjemputanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pesanan: CardPesanan) {
            with(binding) {
                tvNomorWaPesanan.text = pesanan.no_hp_nasabah
                tvTanggalPesanan.text = convertDateToText(pesanan.tanggal)
                val totalBerat = "${pesanan.total_berat} Kg"
                tvBeratTransaksi.text = totalBerat.toString()

                tvNamaPesanan.text = pesanan.nama_nasabah ?: "Nama tidak ditemukan"

                if(pesanan.status_pesanan.lowercase() == "selesai diantar"){
                    tvStatus.text = "Selesai"
                    tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.green_label))
                }else if(pesanan.status_pesanan.lowercase() == "pending"){
                    tvStatus.text = "Pending"
                    tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_primary))
                }else if(pesanan.status_pesanan.lowercase() == "sudah dijadwalkan"){
                    tvStatus.text = "Diproses"
                    tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_calm))
                }else if(pesanan.status_pesanan == "Gagal"){
                    tvStatus.text = "Gagal"
                    tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.red_primary))
                    tvAlasanPenolakan.visibility = View.VISIBLE
                    tvLabelFailReason.visibility = View.VISIBLE
                    tvAlasanPenolakan.text = pesanan?.keterangan
                }

                root.setOnClickListener {
                    val intent = Intent(context, PesananDetailActivity::class.java)
                    intent.putExtra("ID_PESANAN", pesanan.id_pesanan)
                    context.startActivity(intent)
                }
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openMaps(lat: Double, lng: Double) {
        val uri = Uri.parse("http://maps.google.com/maps?q=loc:$lat,$lng")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Google Maps is not installed.", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickUpAdapter.PickUpViewHolder {
        val binding = CardPenjemputanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PickUpViewHolder(binding)
    }

    override fun getItemCount(): Int{
        return pesanans.size
    }

    override fun onBindViewHolder(holder: PickUpAdapter.PickUpViewHolder, position: Int){
        holder.bind(pesanans[position])
    }

    private fun convertDateToText(date: String): String {
        // Assuming input date format is "yyyy-MM-dd"
        val months = arrayOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus",
            "September", "Oktober", "November", "Desember"
        )

        val parts = date.split("-")
        val year = parts[0].toInt()

        if (year < 2000) {
            return "Belum dijadwalkan"
        }

        val day = parts[2]
        val month = months[parts[1].toInt() - 1]

        return "$day $month $year"
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePesanan(newPesanans: List<CardPesanan>) {
        this.pesanans = newPesanans
        notifyDataSetChanged()
    }
}
