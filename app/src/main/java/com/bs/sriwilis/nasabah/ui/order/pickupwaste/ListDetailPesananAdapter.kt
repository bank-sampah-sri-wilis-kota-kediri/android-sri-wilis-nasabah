package com.bs.sriwilis.nasabah.ui.order.pickupwaste

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bs.sriwilis.nasabah.data.model.CardDetailPesanan
import com.bs.sriwilis.nasabah.databinding.CardPesananDetailBinding
import com.bs.sriwilis.nasabah.ui.order.OrderViewModel

class ListDetailPesananAdapter(
    private var pesananSampah: List<CardDetailPesanan>,
    private val context: Context,
    private val viewModel: PesananDetailViewModel
) : RecyclerView.Adapter<ListDetailPesananAdapter.HomePageViewHolder>() {

    inner class HomePageViewHolder(private val binding: CardPesananDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(pesananSampah: CardDetailPesanan) {
            with(binding) {
                tvKategoriPesanan.text = pesananSampah.nama_kategori
                tvBeratPesanan.text = "${pesananSampah.berat} Kg"
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListDetailPesananAdapter.HomePageViewHolder {
        val binding = CardPesananDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomePageViewHolder(binding)
    }

    override fun getItemCount(): Int{
        return pesananSampah.size
    }

    override fun onBindViewHolder(holder: ListDetailPesananAdapter.HomePageViewHolder, position: Int){
        holder.bind(pesananSampah[position])
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updatePesanan(newPesananSampah: List<CardDetailPesanan>) {
        this.pesananSampah = newPesananSampah
        notifyDataSetChanged()
    }

}