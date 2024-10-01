package com.bs.sriwilis.nasabah.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bs.sriwilis.nasabah.helper.InjectionAuth
import com.bs.sriwilis.nasabah.helper.InjectionMain
import com.bs.sriwilis.nasabah.ui.HomepageViewModel
import com.bs.sriwilis.nasabah.ui.addorder.AddOrderViewModel
import com.bs.sriwilis.nasabah.ui.authorization.AuthViewModel
import com.bs.sriwilis.nasabah.ui.home.HomeViewModel
import com.bs.sriwilis.nasabah.ui.order.OrderViewModel
import com.bs.sriwilis.nasabah.ui.order.pickupwaste.PesananDetailViewModel
import com.bs.sriwilis.nasabah.ui.setting.SettingViewModel
import com.bs.sriwilis.nasabah.ui.splashscreen.WelcomeViewModel
import com.bs.sriwilis.nasabah.ui.transaction.TransactionViewModel

class ViewModelFactory private constructor(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                val repository = InjectionAuth.provideRepository(context)
                AuthViewModel(repository) as T
            }
            modelClass.isAssignableFrom(WelcomeViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                WelcomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                SettingViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomepageViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                HomepageViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(TransactionViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                TransactionViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddOrderViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                AddOrderViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PesananDetailViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                PesananDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(OrderViewModel::class.java) -> {
                val repository = InjectionMain.provideRepository(context)
                OrderViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(context).also { instance = it }
            }
    }
}