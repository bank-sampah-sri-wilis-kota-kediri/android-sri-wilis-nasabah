package com.bs.sriwilis.nasabah.ui.authorization

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs.sriwilis.nasabah.data.repository.AuthRepository
import com.bs.sriwilis.nasabah.data.response.RegisterResponseDTO
import com.bs.sriwilis.nasabah.data.room.entity.LoginResponseEntity
import com.bs.sriwilis.nasabah.helper.Result
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<LoginResponseEntity>>()
    val loginResult: LiveData<Result<LoginResponseEntity>> = _loginResult

    private val _registerResult = MutableLiveData<Result<RegisterResponseDTO>>()
    val registerResult: LiveData<Result<RegisterResponseDTO>> = _registerResult

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            val result = repository.login(phone, password)
            _loginResult.value = result
        }
    }

    fun register(name: String, phone: String, address: String, password: String) {
        viewModelScope.launch {
            _registerResult.value = Result.Loading
            val result = repository.register(name, address, phone, password)
            _registerResult.value = result
        }
    }
}