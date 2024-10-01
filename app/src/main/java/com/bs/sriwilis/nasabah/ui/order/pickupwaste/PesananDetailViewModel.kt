package com.bs.sriwilis.nasabah.ui.order.pickupwaste

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs.sriwilis.nasabah.data.model.CardDetailPesanan
import com.bs.sriwilis.nasabah.data.model.CardPesanan
import com.bs.sriwilis.nasabah.data.repository.MainRepository
import com.bs.sriwilis.nasabah.helper.Result
import kotlinx.coroutines.launch

class PesananDetailViewModel(private val repository: MainRepository): ViewModel() {
    private val _pesanans = MutableLiveData<Result<CardPesanan>>()
    val pesanans: LiveData<Result<CardPesanan>> get() = _pesanans

    private val _pesananSampah = MutableLiveData<Result<List<CardDetailPesanan>>>()
    val pesananSampah: LiveData<Result<List<CardDetailPesanan>>> get() = _pesananSampah



    fun getDataDetailPesananSampahKeranjang(idPesanan: String) {
        viewModelScope.launch {
            _pesanans.postValue(Result.Loading)
            val result = repository.getDataDetailPesananSampahKeranjang(idPesanan)
            _pesanans.postValue(result)
        }
    }

    fun getPesananSampah(idPesanan: String) {
        viewModelScope.launch {
            _pesananSampah.postValue(Result.Loading)
            val result = repository.getPesananSampah(idPesanan)
            _pesananSampah.postValue(result)
        }
    }

}