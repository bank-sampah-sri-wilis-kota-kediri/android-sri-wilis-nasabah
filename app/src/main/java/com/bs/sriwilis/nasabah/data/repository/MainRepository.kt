package com.bs.sriwilis.nasabah.data.repository

import android.util.Log
import com.bs.sriwilis.nasabah.data.mapping.MappingCategory
import com.bs.sriwilis.nasabah.data.mapping.MappingNasabah
import com.bs.sriwilis.nasabah.data.mapping.MappingPenarikan
import com.bs.sriwilis.nasabah.data.model.CartOrder
import com.bs.sriwilis.nasabah.data.model.Category
import com.bs.sriwilis.nasabah.data.model.LoggedAccount
import com.bs.sriwilis.nasabah.data.model.PenarikanData
import com.bs.sriwilis.nasabah.data.network.ApiServiceMain
import com.bs.sriwilis.nasabah.data.response.AddPenarikanDTO
import com.bs.sriwilis.nasabah.data.response.CartOrderRequest
import com.bs.sriwilis.nasabah.data.response.ChangeProfileResponseDTO
import com.bs.sriwilis.nasabah.data.response.Penarikan
import com.bs.sriwilis.nasabah.data.response.PesananSampahItem
import com.bs.sriwilis.nasabah.data.response.PesananSampahItemResponse
import com.bs.sriwilis.nasabah.data.response.RegisterResponseDTO
import com.bs.sriwilis.nasabah.data.room.AppDatabase
import com.bs.sriwilis.nasabah.data.room.entity.CategoryEntity
import com.bs.sriwilis.nasabah.data.room.entity.LoginResponseEntity
import com.bs.sriwilis.nasabah.data.room.entity.NasabahEntity
import com.bs.sriwilis.nasabah.data.room.entity.PenarikanEntity
import com.bs.sriwilis.nasabah.helper.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(
    private val apiService: ApiServiceMain,
    private val appDatabase: AppDatabase
) {

    private val mappingPesananSampah = MappingNasabah()
    private val mappingKategori = MappingCategory()
    private val mappingPenarikan = MappingPenarikan()

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            appDatabase.loginResponseDao().deleteAll()
        }
    }

    suspend fun getToken(): String? {
        val loginResponse = appDatabase.loginResponseDao().getLoginResponseById(1)
        return loginResponse?.access_token
    }

    private suspend fun getPhoneLoggedAccount(): String? {
        val loginResponse = appDatabase.loginResponseDao().getLoginResponseById(1)
        return loginResponse?.no_hp_nasabah
    }


    suspend fun syncLoggedAccount() {
        withContext(Dispatchers.IO) {
            val phone = getPhoneLoggedAccount()

            phone?.let {
                val nasabahEntity = appDatabase.nasabahDao().getNasabahByPhone(it)
                nasabahEntity.let { nasabah ->
                    appDatabase.loginResponseDao().updateLoggedAccount(
                        name = nasabah.nama_nasabah ?: "",
                        phone = nasabah.no_hp_nasabah ?: "",
                        address = nasabah.alamat_nasabah ?: "",
                        gambar = nasabah.gambar_nasabah ?: "",
                        saldo = nasabah.saldo_nasabah ?: ""
                    )
                }
            }
        }
    }



    private suspend fun getAllNasabah(): Result<List<NasabahEntity>> {
        return try {
            val token = getToken() ?: return Result.Error("Token is null")

            val response = apiService.getAllNasabah("Bearer $token")

            if (response.isSuccessful) {
                val responseBody = response.body() ?: return Result.Error("Response body is null")

                val nasabahEntities = mappingPesananSampah.mapNasabahResponseDtoToEntity(responseBody)

                withContext(Dispatchers.IO) {
                    appDatabase.nasabahDao().insertAllNasabah(nasabahEntities)
                }

                Result.Success(nasabahEntities)
            } else {
                Result.Error("Failed to fetch data: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error("Error occurred: ${e.message}")
        }
    }

    suspend fun changeProfile(phonePath: String, name: String, phone: String, address: String, gambar: String): Result<ChangeProfileResponseDTO> {
        return try {
            val token = getToken() ?: return Result.Error("Token is null")
            val response = apiService.editNasabah(phonePath,"Bearer $token" ,name, phone, address, gambar)

            if (response.isSuccessful) {
                val changeProfileResponseDTO = response.body()
                if (changeProfileResponseDTO != null) {
                    withContext(Dispatchers.IO) {
                        appDatabase.nasabahDao().updateNasabahByPhone(
                            phone = phone,
                            name = name,
                            address = address,
                            gambar = gambar
                        )
                    }
                    Result.Success(changeProfileResponseDTO)
                } else {
                    Result.Error("Empty Response Body")
                }
            } else {
                Log.d("cek change profile error", response.code().toString())
                Result.Error("Failed to Change Profile: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Error: ${e.message}")
        }
    }

    suspend fun updatePhoneNumberInDb(name: String, phone: String, address: String, gambar: String) {
        appDatabase.loginResponseDao().updatePhoneNumber(name, phone, address, gambar)
    }

    suspend fun getLoggedAccount(): Result<LoginResponseEntity?> {
        return withContext(Dispatchers.IO) {
            try {
                val phoneNumber = getPhoneLoggedAccount()
                if (phoneNumber != null) {
                    val loggedInAccount = appDatabase.loginResponseDao().getLoginResponseById(1)
                    Result.Success(loggedInAccount)
                } else {
                    Result.Error("Phone number is null")
                }
            } catch (e: Exception) {
                Result.Error("Error occurred: ${e.message}")
            }
        }
    }


    // category

    suspend fun getAllCategoriesDao(): Result<List<Category>> {
        return withContext(Dispatchers.IO) {
            try {
                val categoryData = appDatabase.categoryDao().getAllCategory()
                Result.Success(categoryData)
            } catch (e: Exception) {
                Result.Error("Error occured: ${e.message}")
            }
        }
    }

    suspend fun getCategory(): Result<List<CategoryEntity>> {
        return try {
            val token = getToken() ?: return Result.Error("Token is null")
            val response = apiService.getAllCategory("Bearer $token")

            if (response.isSuccessful) {
                val responseBody = response.body() ?: return Result.Error("Response body is null")

                val categoryEntities = mappingKategori.mapCategoryResponseDtoToEntity(responseBody)

                withContext(Dispatchers.IO) {
                    appDatabase.categoryDao().insert(categoryEntities)
                }

                Result.Success(categoryEntities)
            } else {
                Result.Error("Failed to fetch data: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error("Error occurred: ${e.message}")
        }
    }

    // end of category


    // penarikan

    suspend fun getAllPenarikanDao(): Result<List<PenarikanData>> {
        return withContext(Dispatchers.IO) {
            try {
                val penarikanData = appDatabase.penarikanDao().getAllPenarikan()
                Result.Success(penarikanData)
            } catch (e: Exception) {
                Result.Error("Error occured: ${e.message}")
            }
        }
    }

    suspend fun getPenarikan(): Result<List<PenarikanEntity>> {
        return try {
            val phone = getPhoneLoggedAccount()
            val token = getToken() ?: return Result.Error("Token is null")
            val response = apiService.getAllPenarikan(phone,"Bearer $token")

            if (response.isSuccessful) {
                val responseBody = response.body() ?: return Result.Error("Response body is null")

                // Mapping dari DTO ke Entitas Room
                val penarikanEntities = mappingPenarikan.mapPenarikanResponseDtoToEntity(responseBody)

                // Simpan data ke database Room (opsional, jika perlu disimpan)
                withContext(Dispatchers.IO) {
                    appDatabase.penarikanDao().insert(penarikanEntities)
                }

                Result.Success(penarikanEntities)
            } else {
                Result.Error("Failed to fetch data: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error("Error occurred: ${e.message}")
        }
    }

    suspend fun addPenarikanCash(nominal: Long): Result<AddPenarikanDTO> {
        return try {
            val no_hp_nasabah = getPhoneLoggedAccount()
            val token = getToken() ?: return Result.Error("Token is null")
            val response = apiService.addPenarikanCash("Bearer $token", no_hp_nasabah, "Tunai", nominal)
            if (response.isSuccessful) {
                val addPenarikanResponseDTO = response.body()
                if (addPenarikanResponseDTO != null) {
                    Result.Success(addPenarikanResponseDTO)
                } else {
                    Result.Error("Empty Penarikan Body Response")
                }
            } else {
                Result.Error("Failed to Add Penarikan: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Error: ${e.message}")
        }
    }

    suspend fun addPenarikanPLN(nominal: Long, nomorMeteran: Long): Result<AddPenarikanDTO> {
        return try {
            val no_hp_nasabah = getPhoneLoggedAccount()
            val token = getToken() ?: return Result.Error("Token is null")
            val response = apiService.addPenarikanPLN("Bearer $token", no_hp_nasabah, "PLN", nominal, nomorMeteran)
            if (response.isSuccessful) {
                val addPenarikanResponseDTO = response.body()
                if (addPenarikanResponseDTO != null) {
                    Result.Success(addPenarikanResponseDTO)
                } else {
                    Result.Error("Empty Penarikan Body Response")
                }
            } else {
                Result.Error("Failed to Add Penarikan: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Error: ${e.message}")
        }
    }

    suspend fun addPenarikanTransfer(nominal: Long, nomorRekening: Long, jenisBank: String): Result<AddPenarikanDTO> {
        return try {
            val no_hp_nasabah = getPhoneLoggedAccount()
            val token = getToken() ?: return Result.Error("Token is null")
            val response = apiService.addPenarikanTransfer("Bearer $token", no_hp_nasabah, "Transfer", nominal, nomorRekening, jenisBank)
            if (response.isSuccessful) {
                val addPenarikanResponseDTO = response.body()
                if (addPenarikanResponseDTO != null) {
                    Result.Success(addPenarikanResponseDTO)
                } else {
                    Result.Error("Empty Penarikan Body Response")
                }
            } else {
                Result.Error("Failed to Add Penarikan: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Error: ${e.message}")
        }
    }



    // end of penarikan

    suspend fun syncData(): Result<Unit> {
        return try {
            val nasabahResult = getAllNasabah()
            if (nasabahResult is Result.Error) {
                Log.d("cek error sync nasabah", nasabahResult.error)
                return Result.Error("Failed to sync nasabah: ${nasabahResult.error}")
            }
            syncLoggedAccount()

            val categoryResult = getCategory()
            if (categoryResult is Result.Error) {
                return Result.Error("Failed to sync category: ${categoryResult.error}")
            }

            val penarikanResult = getPenarikan()
            if (penarikanResult is Result.Error) {
                Log.d("tes penarikan result if empy", penarikanResult.error)
                return Result.Error("Failed to sync penarikan: ${penarikanResult.error}")
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error occurred during synchronization: ${e.message}")
        }
    }

    suspend fun addCartOrder(
        lat: String,
        long: String,
        pesanan_sampah: List<CartOrder> // Keep using CartTransaction
    ): Result<PesananSampahItemResponse?> {

        val token = getToken() ?: return Result.Error("Token is null")
        return try {
            // Prepare the transaction items for the request
            val pesananSampahItems = pesanan_sampah.map { cartTransaction ->
                PesananSampahItem(
                    gambar = cartTransaction.gambar,
                    harga_perkiraan = cartTransaction.harga_perkiraan,
                    berat_perkiraan = cartTransaction.berat_perkiraan,
                    kategori = cartTransaction.kategori,
                )
            }

            // Create the request body
            val cartOrderRequest = CartOrderRequest(
                pesananSampah = pesananSampahItems,
                noHpNasabah = getPhoneLoggedAccount().toString(),
                idPetugas = "1",
                lat = lat,
                long = long,
                tanggal = "0001-01-01"
            )

            // Make the API request
            val response = apiService.addCartOrder("Bearer $token", cartOrderRequest)

            if (response.isSuccessful) {
                Result.Success(response.body())
            } else {
                val statusCode = response.code()
                val errorBody = response.errorBody()?.string()
                Log.d("cek error", response.body().toString())
                Result.Error("Failed to add cart Order: Status code $statusCode, Error body: $errorBody")
            }

        } catch (e: Exception) {
            Log.d("cek error", e.toString())
            Result.Error(e.toString())
        }
    }


    companion object {
        @Volatile
        private var instance: MainRepository? = null

        fun getInstance(apiServiceMain: ApiServiceMain, appDatabase: AppDatabase): MainRepository {
            return instance ?: synchronized(this) {
                instance ?: MainRepository(apiServiceMain, appDatabase).also { instance = it }
            }
        }
    }
}
