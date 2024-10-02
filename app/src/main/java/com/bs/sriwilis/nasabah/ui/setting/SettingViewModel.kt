package com.bs.sriwilis.nasabah.ui.setting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs.sriwilis.nasabah.data.model.LoggedAccount
import com.bs.sriwilis.nasabah.data.repository.MainRepository
import com.bs.sriwilis.nasabah.data.response.ChangeProfileResponseDTO
import com.bs.sriwilis.nasabah.data.response.RegisterResponseDTO
import com.bs.sriwilis.nasabah.data.room.entity.LoginResponseEntity
import com.bs.sriwilis.nasabah.helper.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: MainRepository): ViewModel() {
    private val _loggedInAccount = MutableLiveData<Result<LoginResponseEntity?>>()
    val loggedInAccount: LiveData<Result<LoginResponseEntity?>> get() = _loggedInAccount

    private val _changeProfileResult = MutableLiveData<Result<ChangeProfileResponseDTO>>()
    val changeProfileResult: LiveData<Result<ChangeProfileResponseDTO>> = _changeProfileResult

    private val _changePasswordResult = MutableLiveData<Result<ChangeProfileResponseDTO>>()
    val changePasswordResult: LiveData<Result<ChangeProfileResponseDTO>> = _changePasswordResult

    fun getLoggedInAccount() {
        viewModelScope.launch {
            _loggedInAccount.postValue(Result.Loading)
            val result = repository.getLoggedAccount()
            _loggedInAccount.postValue(result)
        }
    }

    fun changeProfile(phonePath: String, name: String, phone: String, address: String, gambar: String) {
        viewModelScope.launch {
            _changeProfileResult.value = Result.Loading
            val result = repository.changeProfile(phonePath, name, phone, address, gambar)
            _changeProfileResult.value = result

            if (result is Result.Success) {
                repository.syncDataNasabah()
                repository.updatePhoneNumberInDb(name, phone, address, gambar)
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _changePasswordResult.value = Result.Loading
            val result = repository.changePassword(oldPassword, newPassword)
            _changePasswordResult.value = result
        }
    }

    fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.logout()
        }
    }

}