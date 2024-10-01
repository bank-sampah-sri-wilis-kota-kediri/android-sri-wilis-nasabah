package com.bs.sriwilis.nasabah.ui.home.catalog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.data.model.Catalog
import com.bs.sriwilis.nasabah.data.model.Category
import com.bs.sriwilis.nasabah.databinding.CardForCategoryBinding
import com.bs.sriwilis.nasabah.databinding.CardWasteCatalogBinding
import com.bs.sriwilis.nasabah.ui.home.HomeViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class CatalogAdapter(
    private var catalog: List<Catalog?>,
    private val context: Context

) : RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder>() {

    inner class CatalogViewHolder(private val binding: CardWasteCatalogBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(catalog: Catalog?) {
            with(binding) {
                catalog?.gambar_katalog?.let { gambarKatalog ->
                    val imageBytes = Base64.decode(gambarKatalog, Base64.DEFAULT)
                    Glide.with(itemView.context)
                        .load(imageBytes)
                        .into(ivCategoryListPreview)
                } ?: run {
                    ivCategoryListPreview.setImageResource(R.drawable.iv_waste_box)
                }

                tvCatalogName.text = catalog?.judul_katalog
                tvCatalogPrice.text = "Rp" + catalog?.harga_katalog.toString() + "/Kg"
                descCatalog.text = catalog?.deskripsi_katalog

                btnClickShopee.setOnClickListener {
                    val shopeeLink = catalog?.shopee_link
                    if (!shopeeLink.isNullOrEmpty()) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(shopeeLink))

                            // Check if there's an app that can handle the intent (like a browser)
                            if (intent.resolveActivity(itemView.context.packageManager) != null) {
                                itemView.context.startActivity(intent)
                            } else {
                                Toast.makeText(itemView.context, "No browser found to open the Shopee link", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(itemView.context, "Error opening Shopee link: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(itemView.context, "No Shopee link available for this item", Toast.LENGTH_SHORT).show()
                    }
                }

                btnClickWa.setOnClickListener {
                    val phoneNumber = catalog?.no_wa?.replace(Regex("[^1-9]"), "")
                    val message = "Halo, Saya ingin memesan katalog sampah anda!"

                    if (!phoneNumber.isNullOrEmpty()) {
                        try {
                            val uri = Uri.parse("https://wa.me/62$phoneNumber?text=" + Uri.encode(message))
                            val intent = Intent(Intent.ACTION_VIEW, uri)

                            // Check if WhatsApp is installed
                            try {
                                itemView.context.packageManager.getPackageInfo("com.whatsapp", 0)
                                itemView.context.startActivity(intent)
                            } catch (e: PackageManager.NameNotFoundException) {
                                Toast.makeText(itemView.context, "WhatsApp belum diinstall", Toast.LENGTH_SHORT).show()
                            }

                        } catch (e: Exception) {
                            Toast.makeText(itemView.context, "Gagal membuka WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(itemView.context, "Nomor WhatsApp tidak tersedia", Toast.LENGTH_SHORT).show()
                    }
                }



            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        val binding = CardWasteCatalogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatalogViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return catalog.size
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        holder.bind(catalog[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCatalog(newCatalog: List<Catalog?>) {
        this.catalog = newCatalog
        Log.d("CategoryAdapter", "Jumlah data category: ${catalog.size}")
        notifyDataSetChanged()
    }

}