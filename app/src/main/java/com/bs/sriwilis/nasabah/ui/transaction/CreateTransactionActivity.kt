package com.bs.sriwilis.nasabah.ui.transaction

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityCreateTransactionBinding
import com.bs.sriwilis.nasabah.ui.splashscreen.SplashScreenActivity

@Suppress("DEPRECATION")
class CreateTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnBack.setOnClickListener{
                onBackPressed()
            }

            cvHomepageGuide1.setOnClickListener{
                navigateToElectric()
            }
            cvHomepageGuide2.setOnClickListener{
                navigateToBank()
            }
            cvHomepageGuide3.setOnClickListener{
                navigateToCash()
            }
        }
    }

    private fun navigateToElectric() {
        val intent = Intent(this, ElectricActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToBank() {
        val intent = Intent(this, BankActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToCash() {
        val intent = Intent(this, CashActivity::class.java)
        startActivity(intent)
    }
}