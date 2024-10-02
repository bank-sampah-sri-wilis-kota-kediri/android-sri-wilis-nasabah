package com.bs.sriwilis.nasabah.ui.setting

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.FragmentSettingBinding
import com.bs.sriwilis.nasabah.ui.authorization.login.LoginActivity
import com.bs.sriwilis.nasabah.ui.setting.profile.ChangePasswordActivity
import com.bs.sriwilis.nasabah.ui.setting.profile.ChangeProfileActivity
import com.bs.sriwilis.nasabah.utils.ViewModelFactory

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SettingViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.apply {
            cvProfile.setOnClickListener {
                val intent = Intent(context, ChangeProfileActivity::class.java)
                startActivity(intent)
            }
            cvChangePassword.setOnClickListener {
                val intent = Intent(context, ChangePasswordActivity::class.java)
                startActivity(intent)
            }
            cvLogout.setOnClickListener {
                showLogoutDialog()
            }
        }
    }

    fun showLogoutDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Keluar Akun")
        builder.setMessage("Yakin ingin keluar dari akun?")

        builder.setPositiveButton("Ya") { dialogInterface, _ ->
            viewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("Tidak") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}