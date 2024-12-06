package com.bs.sriwilis.nasabah.ui.addorder

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.data.model.CartOrder
import com.bumptech.glide.Glide

class CartOrderAdapter(
    private val cartItems: MutableList<CartOrder>,
    private val onDeleteItem: (CartOrder) -> Unit
) : RecyclerView.Adapter<CartOrderAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCartOrderPreview: ImageView = itemView.findViewById(R.id.iv_cart_order_preview)
        private val tvOrderName: TextView = itemView.findViewById(R.id.tv_order_name_cart)
        private val tvWeight: TextView = itemView.findViewById(R.id.tv_weight_order_cart)
        private val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete)

        @SuppressLint("SetTextI18n")
        fun bind(cartOrder: CartOrder) {
            tvOrderName.text = cartOrder.kategori
            tvWeight.text = cartOrder.berat_perkiraan.toString() + " kg"

            val imageUri = cartOrder.gambar

            if (!imageUri.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(imageUri)
                    .into(ivCartOrderPreview)
            }

            btnDelete.setOnClickListener {
                onDeleteItem(cartOrder)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_order_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    fun addCartOrder(cartOrder: CartOrder) {
        cartItems.add(cartOrder)
        notifyItemInserted(cartItems.size - 1)
    }

    fun removeItem(cartOrder: CartOrder) {
        val position = cartItems.indexOf(cartOrder)
        if (position != -1) {
            cartItems.removeAt(position)
            notifyItemRemoved(position)
        }

        Log.d("kontol kuda", "$cartItems")
    }
}