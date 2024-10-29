package com.bs.sriwilis.nasabah.helper

sealed class ResultAuth<out R> private constructor(){
    data class Success<out T>(val data: T) : ResultAuth<T>()
    data class Error(val code: Int, val message: String) : ResultAuth<Nothing>()
    data object Loading : ResultAuth<Nothing>()
}