package com.bs.sriwilis.nasabah.ui.order

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bs.sriwilis.nasabah.ui.order.completed.OrderCompletedFragment
import com.bs.sriwilis.nasabah.ui.order.delivered.OrderDeliveredFragment
import com.bs.sriwilis.nasabah.ui.order.failed.OrderFailedFragment
import com.bs.sriwilis.nasabah.ui.order.process.OrderProcessFragment


class OrderPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OrderProcessFragment()
            1 -> OrderDeliveredFragment()
            2 -> OrderFailedFragment()
            3 -> OrderCompletedFragment()
            else -> Fragment()
        }
    }
}