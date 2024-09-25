package com.bs.sriwilis.nasabah.ui.transaction

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityElectricBinding

@Suppress("DEPRECATION")
class ElectricActivity : AppCompatActivity() {

    private lateinit var binding: ActivityElectricBinding
    private var selectedOption: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElectricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnConfirm.isEnabled = false
            btnConfirm.setOnClickListener{
            }
        }

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        setupCardViewListeners()
        setupEditTextListener()
        updateConfirmButtonState()
    }

    private fun setupCardViewListeners() {
        val options = listOf(
            binding.option20000 to "20,000",
            binding.option50000 to "50,000",
            binding.option100000 to "100,000",
            binding.option200000 to "200,000"
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

    private fun updateConfirmButtonState() {
        val isValid = isEditTextValid() && selectedOption != null
        binding.btnConfirm.isEnabled = isValid
        if(isValid){
            binding.btnConfirm.setBackgroundColor(getColor(R.color.blue_primary))
        }else{
            binding.btnConfirm.setBackgroundColor(getColor(R.color.grey_primary))
        }

    }

}
