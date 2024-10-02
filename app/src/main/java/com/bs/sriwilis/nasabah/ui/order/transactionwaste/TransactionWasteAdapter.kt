package com.bs.sriwilis.nasabah.ui.order.transactionwaste

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bs.sriwilis.nasabah.data.model.CardTransaksi
import com.bs.sriwilis.nasabah.databinding.CardTransaksiBinding

class TransactionWasteAdapter (
    private var pesanans: List<CardTransaksi>,
    private val context: Context,
    private val listener: OnApproveClickListener
) : RecyclerView.Adapter<TransactionWasteAdapter.TransactionWasteViewHolder>() {


        interface OnApproveClickListener {
            fun onApproveClick(idPesanan: String)
        }

        inner class TransactionWasteViewHolder(private val binding: CardTransaksiBinding) : RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("SetTextI18n")
            fun bind(pesanan: CardTransaksi) {
                with(binding) {
                    tvNomorWaPesanan.text = pesanan.no_hp_nasabah
                    tvTanggalPesanan.text = convertDateToText(pesanan.tanggal)
                    val totalBerat = "${pesanan.total_berat}Kg"
                    tvBeratTransaksi.text = totalBerat.toString()

                    tvNamaPesanan.text = pesanan.nama_nasabah ?: "Nama tidak ditemukan"
                    tvStatus.text = "+Rp${pesanan.nominal_transaksi}"

                    root.setOnClickListener {
                        val intent = Intent(context, DetailTransactionWasteActivity::class.java)
                        intent.putExtra("ID_PESANAN", pesanan.id)
                        context.startActivity(intent)
                    }
                }
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionWasteAdapter.TransactionWasteViewHolder {
            val binding = CardTransaksiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TransactionWasteViewHolder(binding)
        }

        override fun getItemCount(): Int{
            return pesanans.size
        }

        override fun onBindViewHolder(holder: TransactionWasteAdapter.TransactionWasteViewHolder, position: Int){
            holder.bind(pesanans[position])
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

        @SuppressLint("NotifyDataSetChanged")
        fun updatePesanan(newPesanans: List<CardTransaksi>) {
            this.pesanans = newPesanans
            notifyDataSetChanged()
        }
}