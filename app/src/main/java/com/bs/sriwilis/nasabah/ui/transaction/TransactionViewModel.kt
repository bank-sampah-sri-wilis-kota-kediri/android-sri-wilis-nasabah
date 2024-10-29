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
import com.bs.sriwilis.nasabah.helper.ResultAuth

class TransactionViewModel(private val repository: MainRepository) : ViewModel() {
    private val _penarikanResult = MutableLiveData<ResultAuth<AddPenarikanDTO>>()
    val penarikanResult: LiveData<ResultAuth<AddPenarikanDTO>> = _penarikanResult

    private val _penarikans = MutableLiveData<Result<List<PenarikanData>?>>()
    val penarikans: LiveData<Result<List<PenarikanData>?>> get() = _penarikans

    fun addPenarikanCash(nominal: Long) {
        viewModelScope.launch {
            try {
                _penarikanResult.value = ResultAuth.Loading
                val result = repository.addPenarikanCash(nominal)

                if (result is ResultAuth.Error && result.code == 400) {
                    _penarikanResult.value = ResultAuth.Error(result.code, result.message)
                } else {
                    _penarikanResult.value = result
                }
            } catch (e: Exception) {
                _penarikanResult.value = ResultAuth.Error(400, e.message ?: "Unknown error occurred")
            }
        }
    }


    fun addPenarikanPLN(nominal: Long, nomorMeteran: Long) {
        viewModelScope.launch {
            try {
                _penarikanResult.value = ResultAuth.Loading
                val result = repository.addPenarikanPLN(nominal, nomorMeteran)

                // Tangani hasil dari repository
                if (result is ResultAuth.Error && result.code == 400) {
                    _penarikanResult.value = ResultAuth.Error(result.code, result.message)
                } else {
                    _penarikanResult.value = result
                }
            } catch (e: Exception) {
                _penarikanResult.value = ResultAuth.Error(400, e.message ?: "Unknown error occurred")
            }
        }
    }


    fun addPenarikanTransfer(nominal: Long, nomorRekening: Long, jenisBank: String) {
        viewModelScope.launch {
            try {
                _penarikanResult.value = ResultAuth.Loading
                val result = repository.addPenarikanTransfer(nominal, nomorRekening, jenisBank)

                // Tangani hasil dari repository
                if (result is ResultAuth.Error && result.code == 400) {
                    _penarikanResult.value = ResultAuth.Error(result.code, result.message)
                } else {
                    _penarikanResult.value = result
                }
            } catch (e: Exception) {
                _penarikanResult.value = ResultAuth.Error(400, e.message ?: "Unknown error occurred")
            }
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

    suspend fun syncNasabah(): Result<Unit> {
        return repository.syncDataNasabah()
    }

    suspend fun syncData(): Result<Unit> {
        return repository.syncData()
    }
}