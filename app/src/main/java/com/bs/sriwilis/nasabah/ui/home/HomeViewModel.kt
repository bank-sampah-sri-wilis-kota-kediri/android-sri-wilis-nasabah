package com.bs.sriwilis.nasabah.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs.sriwilis.nasabah.data.model.Catalog
import com.bs.sriwilis.nasabah.data.model.Category
import com.bs.sriwilis.nasabah.data.model.LoggedAccount
import com.bs.sriwilis.nasabah.data.repository.MainRepository
import com.bs.sriwilis.nasabah.data.room.entity.LoginResponseEntity
import kotlinx.coroutines.launch
import com.bs.sriwilis.nasabah.helper.Result

class HomeViewModel(private val repository: MainRepository): ViewModel() {
    private val _loggedInAccount = MutableLiveData<Result<LoginResponseEntity?>>()
    val loggedInAccount: LiveData<Result<LoginResponseEntity?>> get() = _loggedInAccount

    private val _categories = MutableLiveData<Result<List<Category>?>>()
    val categories: LiveData<Result<List<Category>?>> get() = _categories

    private val _catalog = MutableLiveData<Result<List<Catalog>?>>()
    val catalog: LiveData<Result<List<Catalog>?>> get() = _catalog

    fun getLoggedInAccount() {
        viewModelScope.launch {
            _loggedInAccount.postValue(Result.Loading)
            val result = repository.getLoggedAccount()
            _loggedInAccount.postValue(result)
        }
    }

    suspend fun getCatalog() {
        _catalog.postValue(Result.Loading)
        val result = repository.getAllCatalogDao()
        _catalog.postValue(result)
    }

    suspend fun syncData(): Result<Unit> {
        return repository.syncData()
    }

    fun filterData(filter: String) {
        viewModelScope.launch {
            _categories.postValue(Result.Loading)
            val filteredData = when (val result = repository.getAllCategoriesDao()) {
                is Result.Success -> {
                    val filteredList = when (filter) {
                        "plastik" -> result.data.filter { it.jenis_kategori?.lowercase() == "plastik" }
                        "kaleng" -> result.data.filter { it.jenis_kategori?.lowercase() == "kaleng" }
                        "kertas" -> result.data.filter { it.jenis_kategori?.lowercase() == "kertas" }
                        else -> result.data
                    }
                    Result.Success(filteredList)
                }
                is Result.Error -> result
                Result.Loading -> result
            }

            _categories.postValue(filteredData)
        }
    }

}