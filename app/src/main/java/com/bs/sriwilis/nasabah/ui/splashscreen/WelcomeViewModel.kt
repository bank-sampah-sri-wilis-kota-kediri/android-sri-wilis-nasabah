package com.bs.sriwilis.nasabah.ui.splashscreen

import androidx.lifecycle.ViewModel
import com.bs.sriwilis.nasabah.data.repository.MainRepository
import com.bs.sriwilis.nasabah.helper.Result

class WelcomeViewModel(private val repository: MainRepository) : ViewModel() {

    suspend fun fetchToken(): String? {
        return repository.getToken()
    }

    suspend fun syncData(): Result<Unit> {
        return repository.syncData()
    }
}
