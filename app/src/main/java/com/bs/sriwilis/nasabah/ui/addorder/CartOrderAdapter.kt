package com.bs.sriwilis.nasabah.ui.addorder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.data.model.CartOrder

class CartOrderAdapter(private val cartItems: MutableList<CartOrder>) : RecyclerView.Adapter<CartOrderAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCartOrderPreview: ImageView = itemView.findViewById(R.id.iv_cart_order_preview)
        private val tvOrderName: TextView = itemView.findViewById(R.id.tv_order_name_cart)
        private val tvWeight: TextView = itemView.findViewById(R.id.tv_weight_order_cart)

        @SuppressLint("SetTextI18n")
        fun bind(cartOrder: CartOrder) {
            tvOrderName.text = cartOrder.kategori
            tvWeight.text = cartOrder.berat_perkiraan.toString() + " kg"
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
}