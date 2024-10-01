package com.bs.sriwilis.nasabah.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs.sriwilis.nasabah.data.model.CardPesanan
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.data.repository.MainRepository
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: MainRepository): ViewModel() {
    private val _pickUpPesanans = MutableLiveData<Result<List<CardPesanan>>>()
    val pickUpPesanans: LiveData<Result<List<CardPesanan>>> get() = _pickUpPesanans

    fun getCombinedPesananData() {
        viewModelScope.launch {
            _pickUpPesanans.postValue(Result.Loading)
            val result = repository.getCombinedPesananData()
            _pickUpPesanans.postValue(result)
        }
    }

    suspend fun syncData(): Result<Unit> {
        return repository.syncData()
    }

    fun filterData(filter: String) {
        viewModelScope.launch {
            _pickUpPesanans.postValue(Result.Loading)
            val filteredData = when (val result = repository.getCombinedPesananData()) {
                is Result.Success -> {
                    val filteredList = when (filter) {
                        "selesai diantar" -> result.data.filter { it.status_pesanan.lowercase() == "selesai diantar" }
                        "gagal" -> result.data.filter { it.status_pesanan.lowercase() == "gagal" }
                        "sudah dijadwalkan" -> result.data.filter { it.status_pesanan.lowercase() == "sudah dijadwalkan" }
                        "pending" -> result.data.filter { it.status_pesanan.lowercase() == "pending" }
                        else -> result.data.filter {it.status_pesanan.lowercase() == "gagal" || it.status_pesanan.lowercase() == "selesai diantar" || it.status_pesanan.lowercase() == "sudah dijadwalkan" || it.status_pesanan.lowercase() == "pending"}
                    }
                    Result.Success(filteredList)
                }
                is Result.Error -> result
                Result.Loading -> result
            }

            _pickUpPesanans.postValue(filteredData)
        }
    }
}