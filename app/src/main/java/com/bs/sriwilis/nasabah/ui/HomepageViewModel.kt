package com.bs.sriwilis.nasabah.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bs.sriwilis.nasabah.data.repository.MainRepository
import kotlinx.coroutines.launch

class HomepageViewModel(private val repository: MainRepository): ViewModel() {

}