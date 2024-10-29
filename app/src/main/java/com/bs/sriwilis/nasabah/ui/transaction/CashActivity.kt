package com.bs.sriwilis.nasabah.ui.transaction

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityCashBinding
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.helper.ResultAuth
import com.bs.sriwilis.nasabah.utils.ViewModelFactory
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class CashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCashBinding

    private val viewModel by viewModels<TransactionViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNominalEditText()

        binding.apply {
            btnConfirm.setBackgroundColor(getColor(R.color.grey_primary))

            btnBack.setOnClickListener {
                onBackPressed()
            }

            btnConfirm.setOnClickListener {
                val nominal = etNominal.text.toString().toLong()
                addPenarikanCash(nominal)
            }
        }

        observeViewModel()
    }

    private fun setupNominalEditText() {
        binding.etNominal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateConfirmButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateConfirmButtonState() {
        val nominalFilled = binding.etNominal.text.isNotEmpty()
        val nominalValid = isNominalValid()

        binding.btnConfirm.isEnabled = nominalFilled && nominalValid
        binding.btnConfirm.setBackgroundColor(
            if (binding.btnConfirm.isEnabled) getColor(R.color.blue_primary) else getColor(R.color.grey_primary)
        )
    }

    private fun isNominalValid(): Boolean {
        val nominalText = binding.etNominal.text.toString()
        return nominalText.isNotEmpty() && nominalText.toInt() >= 50000
    }

    private fun addPenarikanCash(nominal: Long) {
        viewModel.addPenarikanCash(nominal)
    }

    private fun observeViewModel() {
        viewModel.penarikanResult.observe(this, Observer { result ->
            when (result) {
                is ResultAuth.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is ResultAuth.Success -> {
                    lifecycleScope.launch {
                        viewModel.syncDataPenarikan()
                        binding.progressBar.visibility = View.GONE

                        AlertDialog.Builder(this@CashActivity).apply {
                            setTitle("Sukses!")
                            setMessage("Penarikan uang berhasil ditambahkan, menunggu konfirmasi Admin.")
                            setPositiveButton("OK") { _, _ -> finish() }
                            create()
                            show()
                        }
                    }

                }
                is ResultAuth.Error -> {
                    binding.progressBar.visibility = View.GONE
                    AlertDialog.Builder(this).apply {
                        setTitle("Gagal!")
                        setMessage(result.message)
                        setPositiveButton("OK", null)
                        create()
                        show()
                    }
                }
            }
        })
    }
}
