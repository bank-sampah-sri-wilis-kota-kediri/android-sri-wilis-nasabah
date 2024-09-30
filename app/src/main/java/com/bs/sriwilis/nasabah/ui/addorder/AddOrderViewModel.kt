package com.bs.sriwilis.nasabah.ui.addorder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs.sriwilis.nasabah.data.model.CartOrder
import com.bs.sriwilis.nasabah.data.model.Category
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.data.repository.MainRepository
import com.bs.sriwilis.nasabah.data.response.PesananSampahItemResponse
import kotlinx.coroutines.launch

class AddOrderViewModel(private val repository: MainRepository) : ViewModel() {
    private val _categoryNames = MutableLiveData<Result<List<String>>>()
    val categoryNames: LiveData<Result<List<String>>> get() = _categoryNames

    private val _categories = MutableLiveData<Result<List<Category>>>()
    val categories: LiveData<Result<List<Category>>> get() = _categories

    private val _orderResult = MutableLiveData<Result<PesananSampahItemResponse?>>()
    val orderResult: LiveData<Result<PesananSampahItemResponse?>> = _orderResult

    data class Category(val name: String, val basePrice: Double)


    fun getCategoryByPosition(position: Int): Category? {
        val result = _categories.value
        return when (result) {
            is Result.Success -> result.data.getOrNull(position)
            is Result.Error -> null
            Result.Loading -> null
            null -> null
        }
    }

    fun getCategoryDetails() {
        viewModelScope.launch {
            _categories.postValue(Result.Loading)
            val result = repository.getAllCategoriesDao()
            if (result is Result.Success) {
                val categoryList = result.data.map { categoryItem ->
                    Category(
                        name = categoryItem.nama_kategori ?: "Unknown",
                        basePrice = categoryItem.harga_kategori?.toDouble() ?: 0.00
                    )
                }
                _categories.postValue(Result.Success(categoryList))

                val categoryNames = categoryList.map { it.name }
                _categoryNames.postValue(Result.Success(categoryNames))
            } else if (result is Result.Error) {
                _categories.postValue(Result.Error(result.error))
            }
        }
    }

    fun addCartOrder(lat: String, long: String, transaksi_sampah: List<CartOrder>) {
        viewModelScope.launch {
            _orderResult.value = Result.Loading
            val result = repository.addCartOrder(lat,long, transaksi_sampah)
            _orderResult.value = result
        }
    }

}