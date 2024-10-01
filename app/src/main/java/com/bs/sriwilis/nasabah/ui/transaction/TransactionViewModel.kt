package com.bs.sriwilis.nasabah.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs.sriwilis.nasabah.data.model.Category
import com.bs.sriwilis.nasabah.data.model.PenarikanData
import com.bs.sriwilis.nasabah.data.repository.MainRepository
import com.bs.sriwilis.nasabah.data.response.AddPenarikanDTO
import com.bs.sriwilis.nasabah.data.response.Penarikan
import kotlinx.coroutines.launch
import com.bs.sriwilis.nasabah.helper.Result

class TransactionViewModel(private val repository: MainRepository) : ViewModel() {
    private val _penarikanResult = MutableLiveData<Result<AddPenarikanDTO>>()
    val penarikanResult: LiveData<Result<AddPenarikanDTO>> = _penarikanResult

    private val _penarikans = MutableLiveData<Result<List<PenarikanData>?>>()
    val penarikans: LiveData<Result<List<PenarikanData>?>> get() = _penarikans

    fun addPenarikanCash(nominal: Long) {
        viewModelScope.launch {
            _penarikanResult.value = Result.Loading
            val result = repository.addPenarikanCash(nominal)
            _penarikanResult.value = result
        }
    }

    fun addPenarikanPLN(nominal: Long, nomorMeteran: Long) {
        viewModelScope.launch {
            _penarikanResult.value = Result.Loading
            val result = repository.addPenarikanPLN(nominal, nomorMeteran)
            _penarikanResult.value = result
        }
    }

    fun addPenarikanTransfer(nominal: Long, nomorRekening: Long, jenisBank: String) {
        viewModelScope.launch {
            _penarikanResult.value = Result.Loading
            val result = repository.addPenarikanTransfer(nominal, nomorRekening, jenisBank)
            _penarikanResult.value = result
        }
    }

    fun filterDataTransaction(filterStatus: String, filterJenis: String) {
        viewModelScope.launch {
            _penarikans.postValue(Result.Loading)
            val filteredData = when (val result = repository.getAllPenarikanDao()) {
                is Result.Success -> {
                    var filteredList = result.data

                    filteredList = when (filterStatus) {
                        "Diproses" -> filteredList.filter { it.status_penarikan.lowercase() == "diproses" }
                        "Berhasil" -> filteredList.filter { it.status_penarikan.lowercase() == "berhasil" }
                        "Gagal" -> filteredList.filter { it.status_penarikan.lowercase() == "gagal" }
                        else -> filteredList
                    }

                    filteredList = when (filterJenis) {
                        "PLN" -> filteredList.filter { it.jenis_penarikan.lowercase() == "pln" }
                        "Transfer" -> filteredList.filter { it.jenis_penarikan.lowercase() == "transfer" }
                        "Tunai" -> filteredList.filter { it.jenis_penarikan.lowercase() == "tunai" }
                        else -> filteredList
                    }

                    Result.Success(filteredList)
                }
                is Result.Error -> result
                Result.Loading -> result
            }

            _penarikans.postValue(filteredData)
        }
    }

    suspend fun syncDataPenarikan(): Result<Unit> {
        return repository.syncDataPenarikan()
    }

    suspend fun syncData(): Result<Unit> {
        return repository.syncData()
    }
}