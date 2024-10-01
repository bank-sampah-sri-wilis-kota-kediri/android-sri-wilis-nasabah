package com.bs.sriwilis.nasabah.ui.transaction

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bs.sriwilis.nasabah.data.model.PenarikanData
import com.bs.sriwilis.nasabah.databinding.CardMutationHistoryBinding


class TransactionAdapter(
    private var penarikan: List<PenarikanData?>,
    private val context: Context

) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(private val binding: CardMutationHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(penarikan: PenarikanData?) {
            with(binding) {
                tvMutationTitle.text = "Penarikan " + penarikan?.jenis_penarikan.toString()
                tvMutationDate.text = penarikan?.tanggal.toString()
                tvMutationNominal.text = "-" + penarikan?.nominal.toString()
                tvMutationStatus.text = penarikan?.status_penarikan.toString()

                if (penarikan?.jenis_penarikan == "PLN") {
                    tvToken.visibility = View.VISIBLE
                    tvToken.text = "Token: -"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = CardMutationHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return penarikan.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(penarikan[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTransaction(newPenarikans: List<PenarikanData?>) {
        Log.d("TransactionAdapter", "Jumlah data penarikan: ${penarikan.size}")
        this.penarikan = newPenarikans
        notifyDataSetChanged()
    }

}