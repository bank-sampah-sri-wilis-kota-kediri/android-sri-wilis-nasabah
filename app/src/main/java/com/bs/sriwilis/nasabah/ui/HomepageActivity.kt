package com.bs.sriwilis.nasabah.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.bs.sriwilis.nasabah.R
import com.bs.sriwilis.nasabah.databinding.ActivityHomepageBinding
import com.bs.sriwilis.nasabah.ui.addorder.AddOrderActivity
import com.bs.sriwilis.nasabah.ui.home.HomeFragment
import com.bs.sriwilis.nasabah.ui.order.OrderFragment
import com.bs.sriwilis.nasabah.ui.setting.SettingFragment
import com.bs.sriwilis.nasabah.ui.setting.profile.ChangeProfileActivity
import com.bs.sriwilis.nasabah.ui.transaction.TransactionFragment

@Suppress("DEPRECATION")
class HomepageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomepageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = true

        replaceFragment(HomeFragment())

        binding.fabAddOrder.setOnClickListener{
            val intent = Intent(this, AddOrderActivity::class.java)
            startActivity(intent)
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    if (!isCurrentFragment(HomeFragment::class.java)) {
                        replaceFragment(HomeFragment())
                    }
                    true
                }
                R.id.order -> {
                    if (!isCurrentFragment(OrderFragment::class.java)) {
                        replaceFragment(OrderFragment())
                    }
                    true
                }
                R.id.transaction -> {
                    if (!isCurrentFragment(TransactionFragment::class.java)) {
                        replaceFragment(TransactionFragment())
                    }
                    true
                }
                R.id.settings -> {
                    if (!isCurrentFragment(SettingFragment::class.java)) {
                        replaceFragment(SettingFragment())
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun isCurrentFragment(fragmentClass: Class<out Fragment>): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        return currentFragment != null && currentFragment::class.java == fragmentClass
    }
}