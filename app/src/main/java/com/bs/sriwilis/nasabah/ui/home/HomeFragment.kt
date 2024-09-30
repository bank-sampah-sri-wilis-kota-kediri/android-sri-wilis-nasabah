package com.bs.sriwilis.nasabah.ui.home

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.FragmentHomeBinding
import com.bs.sriwilis.nasabah.utils.ViewModelFactory
import kotlinx.coroutines.launch
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.ui.home.category.CategoryActivity
import com.bs.sriwilis.nasabah.ui.setting.profile.ChangeProfileActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            icProfile.setOnClickListener {
                val intent = Intent(requireContext(), ChangeProfileActivity::class.java)
                startActivity(intent)
            }

            cvHomepageGuide2.setOnClickListener {
                val intent = Intent(requireContext(), CategoryActivity::class.java)
                startActivity(intent)
            }
        }

        val factory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        lifecycleScope.launch {
            viewModel.getLoggedInAccount()
        }


        observeViewModel()
    }


    private fun observeViewModel() {
        viewModel.loggedInAccount.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val loggedAccount = result.data
                    val doubleBalance = loggedAccount?.saldo_nasabah?.toDoubleOrNull() ?: 0.0
                    val intBalance = doubleBalance.toInt()
                    val formattedBalance = "Rp $intBalance"
                    binding.tvBalance.text = formattedBalance

                    if (loggedAccount != null) {
                        if (!loggedAccount.gambar_nasabah.isNullOrEmpty()) {
                            val bitmap = decodeBase64ToBitmap(loggedAccount.gambar_nasabah)
                            if (bitmap != null) {
                                val tempFile = saveBitmapToFile(bitmap)
                                if (tempFile != null) {
                                    Glide.with(this).clear(binding.icProfile)
                                    Glide.with(this)
                                        .load(tempFile)
                                        .signature(ObjectKey(System.currentTimeMillis()))
                                        .into(binding.icProfile)
                                } else {
                                    binding.icProfile.setImageResource(R.drawable.ic_profile)
                                }
                            }
                        }
                    }
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Gagal memuat data: ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    Log.d("Loading", "Loading")
                }
            }
        }
    }


    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun saveBitmapToFile(bitmap: Bitmap): File? {
        return try {
            val tempFile = File(requireContext().cacheDir, "temp_image.jpg")
            val fileOutputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.getLoggedInAccount()
    }



}