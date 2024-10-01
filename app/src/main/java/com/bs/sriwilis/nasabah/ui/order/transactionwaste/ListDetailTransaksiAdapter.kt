package com.bs.sriwilis.nasabah.ui.order.transactionwaste

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bs.sriwilis.nasabah.data.model.CardDetailPesanan
import com.bs.sriwilis.nasabah.databinding.CardTransaksiSampahDetailBinding

class ListDetailTransaksiAdapter (
    private var transaksiSampah: List<CardDetailPesanan>,
    private val context: Context,
    private val viewModel: TransactionWasteDetailViewModel
) : RecyclerView.Adapter<ListDetailTransaksiAdapter.TransactionWasteDetailViewHolder>() {

    inner class TransactionWasteDetailViewHolder(private val binding: CardTransaksiSampahDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(transaksiSampah: CardDetailPesanan) {
            with(binding) {
                tvKategoriPesanan.text = transaksiSampah.nama_kategori
                tvBeratPesanan.text = "${transaksiSampah.berat} Kg"
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListDetailTransaksiAdapter.TransactionWasteDetailViewHolder {
        val binding = CardTransaksiSampahDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionWasteDetailViewHolder(binding)
    }

    override fun getItemCount(): Int{
        return transaksiSampah.size
    }

    override fun onBindViewHolder(holder: ListDetailTransaksiAdapter.TransactionWasteDetailViewHolder, position: Int){
        holder.bind(transaksiSampah[position])
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updatePesanan(newPesananSampah: List<CardDetailPesanan>) {
        this.transaksiSampah = newPesananSampah
        notifyDataSetChanged()
    }

}
