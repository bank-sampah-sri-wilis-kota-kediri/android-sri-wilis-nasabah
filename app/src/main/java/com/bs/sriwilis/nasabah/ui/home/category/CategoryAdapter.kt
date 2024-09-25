package com.bs.sriwilis.nasabah.ui.home.category

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.data.model.Category
import com.bs.sriwilis.nasabah.databinding.CardForCategoryBinding
import com.bs.sriwilis.nasabah.ui.home.HomeViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class CategoryAdapter(
    private var category: List<Category?>,
    private val context: Context

) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: CardForCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(category: Category?) {
            with(binding) {
                category?.gambar_kategori?.let { gambarKategori ->
                    val imageBytes = Base64.decode(gambarKategori, Base64.DEFAULT)
                    Glide.with(itemView.context)
                        .load(imageBytes)
                        .into(ivCategoryListPreview)
                } ?: run {
                    ivCategoryListPreview.setImageResource(R.drawable.iv_waste_box)
                }

                tvCategoryListName.text = category?.nama_kategori
                tvCategoryListType.text = category?.jenis_kategori
                tvCategoryItemPrice.text = "Rp" + category?.harga_kategori.toString() + "/Kg"

//                itemView.setOnClickListener {
//                    category?.id?.let { id ->
//                        onItemClick?.invoke(id)
//                        val intent = Intent(itemView.context, DetailCategoryActivity::class.java)
//                        intent.putExtra("id", id)
//                        itemView.context.startActivity(intent)
//                    }
//                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CardForCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return category.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(category[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCategory(newCategories: List<Category?>) {
        this.category = newCategories
        notifyDataSetChanged()
    }

}