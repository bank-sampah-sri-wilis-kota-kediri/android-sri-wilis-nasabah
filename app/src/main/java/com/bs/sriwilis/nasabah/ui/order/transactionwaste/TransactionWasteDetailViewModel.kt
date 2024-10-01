package com.bs.sriwilis.nasabah.ui.order.transactionwaste

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs.sriwilis.nasabah.data.model.CardDetailPesanan
import com.bs.sriwilis.nasabah.data.model.CardPesanan
import com.bs.sriwilis.nasabah.data.model.CardTransaksi
import com.bs.sriwilis.nasabah.data.repository.MainRepository
import com.bs.sriwilis.nasabah.helper.Result
import kotlinx.coroutines.launch

class TransactionWasteDetailViewModel(private val repository: MainRepository): ViewModel() {
    private val _pesanans = MutableLiveData<Result<CardTransaksi>>()
    val pesanans: LiveData<Result<CardTransaksi>> get() = _pesanans

    private val _pesananSampah = MutableLiveData<Result<List<CardDetailPesanan>>>()
    val pesananSampah: LiveData<Result<List<CardDetailPesanan>>> get() = _pesananSampah



    fun getDataDetailKeranjangTransaksi(idPesanan: String) {
        viewModelScope.launch {
            _pesanans.postValue(Result.Loading)
            val result = repository.getDataDetailKeranjangTransaksi(idPesanan)
            _pesanans.postValue(result)
        }
    }

    fun getTransaksiSampah(idPesanan: String) {
        viewModelScope.launch {
            _pesananSampah.postValue(Result.Loading)
            val result = repository.getTransaksiSampah(idPesanan)
            _pesananSampah.postValue(result)
        }
    }

}