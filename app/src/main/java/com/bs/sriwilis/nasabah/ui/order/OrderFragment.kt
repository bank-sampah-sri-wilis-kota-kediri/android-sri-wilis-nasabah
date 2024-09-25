package com.bs.sriwilis.nasabah.ui.order

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.FragmentOrderBinding
import com.google.android.material.tabs.TabLayoutMediator

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderPagerAdapter: OrderPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderPagerAdapter = OrderPagerAdapter(this)
        binding.viewPager.adapter = orderPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tv_order_status_process)
                1 -> getString(R.string.tv_order_status_delivered)
                2 -> getString(R.string.tv_order_status_failed)
                3 -> getString(R.string.tv_order_status_success)
                else -> null
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}