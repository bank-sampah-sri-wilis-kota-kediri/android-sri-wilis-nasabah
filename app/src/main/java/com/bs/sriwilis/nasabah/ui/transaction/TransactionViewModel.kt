package com.bs.sriwilis.nasabah.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs.sriwilis.nasabah.data.repository.MainRepository
import com.bs.sriwilis.nasabah.data.response.AddPenarikanDTO
import com.bs.sriwilis.nasabah.data.response.Penarikan
import kotlinx.coroutines.launch
import com.bs.sriwilis.nasabah.helper.Result

class TransactionViewModel(private val repository: MainRepository) : ViewModel() {
    private val _penarikanResult = MutableLiveData<Result<AddPenarikanDTO>>()
    val penarikanResult: LiveData<Result<AddPenarikanDTO>> = _penarikanResult

    fun addPenarikanCash(nominal: Int) {
        viewModelScope.launch {
            _penarikanResult.value = Result.Loading
            val result = repository.addPenarikanCash(nominal)
            _penarikanResult.value = result
        }
    }
}