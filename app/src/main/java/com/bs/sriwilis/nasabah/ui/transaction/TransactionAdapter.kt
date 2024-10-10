package com.bs.sriwilis.nasabah.ui.transaction

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.NotificationCompat.getColor
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bs.sriwilis.nasabah.R
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
                // Set text values
                tvMutationTitle.text = "Penarikan " + penarikan?.jenis_penarikan.toString()
                tvMutationDate.text = penarikan?.tanggal.toString()
                tvMutationNominal.text = "-" + penarikan?.nominal.toString()
                tvMutationStatus.text = penarikan?.status_penarikan.toString()

                // Set status background color
                val statusPenarikan = penarikan?.status_penarikan?.lowercase()
                when (statusPenarikan) {
                    "diproses" -> tvMutationStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.blue_calm)
                    )
                    "gagal" -> tvMutationStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.red_primary)
                    )
                    "berhasil" -> tvMutationStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.green_label)
                    )
                }

                if(penarikan?.keterangan.isNullOrEmpty() && penarikan?.nomor_token == null){
                    vGaris.visibility = View.GONE
                }else{
                    vGaris.visibility = View.VISIBLE
                }

                if (penarikan?.keterangan.isNullOrEmpty()) {
                    tvContentKeterangan.visibility = View.GONE
                    tvTitleKeterangan.visibility = View.GONE
                    ConstraintSet().apply {
                        clone(binding.clCard)
                        connect(tvToken.id, ConstraintSet.TOP, vGaris.id, ConstraintSet.BOTTOM, 8)
                        applyTo(binding.clCard)
                    }
                } else {
                    tvContentKeterangan.visibility = View.VISIBLE
                    tvTitleKeterangan.visibility = View.VISIBLE
                    tvContentKeterangan.text = penarikan?.keterangan ?: ""
                    ConstraintSet().apply {
                        clone(binding.clCard)
                        connect(tvToken.id, ConstraintSet.TOP, tvContentKeterangan.id, ConstraintSet.BOTTOM, 8)
                        applyTo(binding.clCard)
                    }
                }

                if (penarikan?.jenis_penarikan == "PLN" && penarikan.nomor_token != null) {
                    tvToken.visibility = View.VISIBLE
                    tvToken.text = "Token: ${penarikan.nomor_token}"
                    root.setOnClickListener { showTokenPopup(penarikan.nomor_token) }
                } else {
                    tvToken.visibility = View.GONE
                    root.setOnClickListener(null)
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
        this.penarikan = newPenarikans
        notifyDataSetChanged()
    }

    private fun showTokenPopup(token: String?) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Token PLN Anda")
        builder.setMessage("$token")

        builder.setNeutralButton("Salin Token") { _, _ ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Token PLN", token)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Token disalin ke clipboard", Toast.LENGTH_SHORT).show()
        }

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


}