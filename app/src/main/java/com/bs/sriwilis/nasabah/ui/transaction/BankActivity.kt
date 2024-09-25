package com.bs.sriwilis.nasabah.ui.transaction

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityBankBinding

@Suppress("DEPRECATION")
class BankActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBankBinding
    private var selectedBank: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        setupBankSpinner()
        setupEditTextListener()
        updateConfirmButtonState()
    }

    private fun setupBankSpinner() {
        val bankArray = resources.getStringArray(R.array.jenis_bank)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bankArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerBank.adapter = adapter

        binding.spinnerBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedBank = parent.getItemAtPosition(position).toString()
                updateConfirmButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedBank = null
                updateConfirmButtonState()
            }
        }
    }

    private fun setupEditTextListener() {
        binding.etAccountNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateConfirmButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etNominal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateConfirmButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun isEditTextValid(): Boolean {
        val accountNo = binding.etAccountNumber.text.toString()
        val nominal = binding.etNominal.text.toString().toDoubleOrNull() ?: 0.0

        return accountNo.isNotEmpty() && nominal >= 50000 && selectedBank != null
    }

    private fun updateConfirmButtonState() {
        val isValid = isEditTextValid()
        binding.btnConfirm.isEnabled = isValid
        if (isValid) {
            binding.btnConfirm.setBackgroundColor(getColor(R.color.blue_primary))
        } else {
            binding.btnConfirm.setBackgroundColor(getColor(R.color.grey_primary))
        }
    }
}
