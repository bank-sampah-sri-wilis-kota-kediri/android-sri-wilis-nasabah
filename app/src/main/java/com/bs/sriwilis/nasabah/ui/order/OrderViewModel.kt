package com.bs.sriwilis.nasabah.ui.order

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs.sriwilis.nasabah.data.model.CardPesanan
import com.bs.sriwilis.nasabah.data.model.CardTransaksi
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.data.repository.MainRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class OrderViewModel(private val repository: MainRepository): ViewModel() {
    private val _pickUpPesanans = MutableLiveData<Result<List<CardPesanan>>>()
    val pickUpPesanans: LiveData<Result<List<CardPesanan>>> get() = _pickUpPesanans
    private val _transactioWastePesanans = MutableLiveData<Result<List<CardTransaksi>>>()
    val transactioWastePesanans: LiveData<Result<List<CardTransaksi>>> get() = _transactioWastePesanans

    fun getCombinedPesananData() {
        viewModelScope.launch {
            _pickUpPesanans.postValue(Result.Loading)
            val result = repository.getCombinedPesananData()
            _pickUpPesanans.postValue(result)
        }
    }

    fun getCombinedTransactionWaste() {
        viewModelScope.launch {
            _transactioWastePesanans.postValue(Result.Loading)
            val result = repository.getCombinedTransaksiData()
            _transactioWastePesanans.postValue(result)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterTransactionWaste(filter: String) {
        viewModelScope.launch {
            _transactioWastePesanans.postValue(Result.Loading)

            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val currentDate = LocalDate.now()

            val filteredData = when (val result = repository.getCombinedTransaksiData()) {
                is Result.Success -> {
                    val filteredList = when (filter) {
                        "today" -> result.data.filter {
                            val transactionDate = LocalDate.parse(it.tanggal, dateFormatter)
                            transactionDate.isEqual(currentDate)
                        }
                        "week" -> result.data.filter {
                            val transactionDate = LocalDate.parse(it.tanggal, dateFormatter)
                            val startOfWeek = currentDate.with(ChronoUnit.DAYS.addTo(currentDate, -(currentDate.dayOfWeek.value - 1).toLong()))
                            transactionDate.isAfter(startOfWeek) || transactionDate.isEqual(startOfWeek)
                        }
                        "month" -> result.data.filter {
                            val transactionDate = LocalDate.parse(it.tanggal, dateFormatter)
                            transactionDate.month == currentDate.month && transactionDate.year == currentDate.year
                        }
                        else -> result.data
                    }

                    Result.Success(filteredList)
                }
                is Result.Error -> result
                Result.Loading -> result
            }

            _transactioWastePesanans.postValue(filteredData)
        }
    }
}