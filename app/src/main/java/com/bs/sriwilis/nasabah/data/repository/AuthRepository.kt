package com.bs.sriwilis.nasabah.data.repository

import android.util.Log
import com.bs.sriwilis.nasabah.data.network.ApiServiceAuth
import com.bs.sriwilis.nasabah.data.response.LoginResponseDTO
import com.bs.sriwilis.nasabah.data.response.RegisterResponseDTO
import com.bs.sriwilis.nasabah.data.room.AppDatabase
import com.bs.sriwilis.nasabah.data.room.entity.LoginResponseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.helper.ResultAuth

class AuthRepository(
    private val apiService: ApiServiceAuth,
    private val appDatabase: AppDatabase
) {

    suspend fun login(phone: String, password: String): Result<LoginResponseEntity> {
        return try {
            val response = apiService.login(no_hp_nasabah = phone, password_nasabah =  password)
            if (response.isSuccessful) {
                val loginResponseDTO = response.body()
                if (loginResponseDTO?.data != null) {
                    val loginResponseEntity = mapDtoToEntity(loginResponseDTO)
                    saveLoginResponseToRoom(loginResponseEntity)
                    Result.Success(loginResponseEntity)
                } else {
                    Result.Error("Empty Response Body")
                }
            } else {
                Result.Error("Failed to login: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Error: ${e.message}")
        }
    }

    suspend fun register(name: String, address: String, phone: String, password: String): ResultAuth<RegisterResponseDTO> {
        return try {
            val response = apiService.register(name, address, phone, password)
            if (response.isSuccessful) {
                val registerResponseDTO = response.body()
                if (registerResponseDTO != null) {
                    ResultAuth.Success(registerResponseDTO)
                } else {
                    ResultAuth.Error(500,"Empty Response Body")
                }
            } else {
                ResultAuth.Error(500, "Failed to Register: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultAuth.Error(500, "Error: ${e.message}")
        }
    }


    private fun mapDtoToEntity(dto: LoginResponseDTO): LoginResponseEntity {
        return LoginResponseEntity(
            id = 1,
            success = dto.success,
            message = dto.message,
            access_token = dto.data?.access_token,
            nama_nasabah = dto.data?.nama_nasabah,
            alamat_nasabah = dto.data?.alamat_nasabah,
            saldo_nasabah = dto.data?.saldo_nasabah,
            is_dapat_jasa = dto.data?.is_dapat_jasa,
            jasa = dto.data?.jasa,
            gambar_nasabah = dto.data?.gambar_nasabah,
            no_hp_nasabah = dto.data?.no_hp_nasabah ?: ""
        )
    }

    private suspend fun saveLoginResponseToRoom(loginResponse: LoginResponseEntity) {
        withContext(Dispatchers.IO) {
            Log.d("Saving login response", loginResponse.access_token.toString())
            appDatabase.loginResponseDao().insert(loginResponse)
        }
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(apiService: ApiServiceAuth, appDatabase: AppDatabase): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, appDatabase).also { instance = it }
            }
        }
    }
}
