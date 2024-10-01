package com.bs.sriwilis.nasabah.ui.transaction

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.room.InvalidationTracker
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.databinding.ActivityElectricBinding
import com.bs.sriwilis.nasabah.utils.ViewModelFactory
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class ElectricActivity : AppCompatActivity() {

    private lateinit var binding: ActivityElectricBinding
    private var selectedOption: String? = null

    private val viewModel by viewModels<TransactionViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElectricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnConfirm.isEnabled = false
            btnConfirm.setOnClickListener{
                val meterNo = etMeterNo.text.toString().toLong()
                selectedOption?.let { it1 -> addPenarikanPLN(it1.toLong(), meterNo) }
            }

            btnBack.setOnClickListener{
                onBackPressed()
            }
        }

        setupCardViewListeners()
        setupEditTextListener()
        updateConfirmButtonState()
        observeViewModel()
    }

    private fun setupCardViewListeners() {
        val options = listOf(
            binding.option20000 to "22500",
            binding.option50000 to "52500",
            binding.option100000 to "102500",
            binding.option200000 to "202500"
        )

        for ((cardView, value) in options) {
            cardView.setOnClickListener {
                handleCardSelection(cardView, value, options)
            }
        }
    }

    private fun setupEditTextListener() {
        binding.etMeterNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateConfirmButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun handleCardSelection(selectedCard: View, value: String, options: List<Pair<View, String>>) {
        for ((cardView, _) in options) {
            cardView.backgroundTintList = getColorStateList(R.color.white)
        }
        selectedCard.backgroundTintList = getColorStateList(R.color.blue_calm)

        selectedOption = value
        updateConfirmButtonState()
    }

    private fun isEditTextValid(): Boolean {
        val input = binding.etMeterNo.text.toString()
        return input.length == 11 || input.length == 12
    }

    private fun addPenarikanPLN(nominal: Long, meteranNo: Long) {
        viewModel.addPenarikanPLN(nominal, meteranNo)
    }

    private fun updateConfirmButtonState() {
        val isValid = isEditTextValid() && selectedOption != null
        binding.btnConfirm.isEnabled = isValid
        if(isValid){
            binding.btnConfirm.setBackgroundColor(getColor(R.color.blue_primary))
        }else{
            binding.btnConfirm.setBackgroundColor(getColor(R.color.grey_primary))
        }

    }

    private fun observeViewModel() {
        viewModel.penarikanResult.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    lifecycleScope.launch {
                        viewModel.syncDataPenarikan()
                        binding.progressBar.visibility = View.GONE
                        AlertDialog.Builder(this@ElectricActivity).apply {
                            setTitle("Sukses!")
                            setMessage("Penarikan Token PLN berhasil ditambahkan, menunggu konfirmasi Admin.")
                            setPositiveButton("OK") { _, _ -> finish() }
                            create()
                            show()
                        }
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle("Gagal!")
                        setMessage("Penarikan Token PLN gagal ditambahkan. ${result.error}")
                        setPositiveButton("OK", null)
                        create()
                        show()
                    }
                }
            }
        })
    }

}
