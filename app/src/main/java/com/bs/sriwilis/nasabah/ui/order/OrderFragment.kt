package com.bs.sriwilis.nasabah.ui.order

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bs.sriwilis.nasabah.databinding.FragmentOrderBinding
import com.bs.sriwilis.nasabah.ui.order.pickupwaste.PickUpWasteActivity
import com.bs.sriwilis.nasabah.ui.order.transactionwaste.TransactionWasteActivity
class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            cvTransaksiMasuk.setOnClickListener{
                navigateToTransaksiMasuk()
            }
            cvPenjemputanSampah.setOnClickListener{
                navigateToPenjemputanPesanan()
            }
        }
    }

    private fun navigateToTransaksiMasuk() {
        val intent = Intent(requireContext(), TransactionWasteActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToPenjemputanPesanan() {
        val intent = Intent(requireContext(), PickUpWasteActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}