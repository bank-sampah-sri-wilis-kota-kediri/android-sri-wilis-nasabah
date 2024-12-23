package com.bs.sriwilis.nasabah.data.repository

import android.util.Log
import com.bs.sriwilis.nasabah.data.mapping.MappingCatalog
import com.bs.sriwilis.nasabah.data.mapping.MappingCategory
import com.bs.sriwilis.nasabah.data.mapping.MappingNasabah
import com.bs.sriwilis.nasabah.data.mapping.MappingPenarikan
import com.bs.sriwilis.nasabah.data.mapping.MappingPesanan
import com.bs.sriwilis.nasabah.data.mapping.MappingTransaksi
import com.bs.sriwilis.nasabah.data.model.CardDetailPesanan
import com.bs.sriwilis.nasabah.data.model.CardPesanan
import com.bs.sriwilis.nasabah.data.model.CardTransaksi
import com.bs.sriwilis.nasabah.data.model.CartOrder
import com.bs.sriwilis.nasabah.data.model.Catalog
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
import com.bs.sriwilis.nasabah.data.room.entity.CatalogEntity
import com.bs.sriwilis.nasabah.data.room.entity.CategoryEntity
import com.bs.sriwilis.nasabah.data.room.entity.LoginResponseEntity
import com.bs.sriwilis.nasabah.data.room.entity.NasabahEntity
import com.bs.sriwilis.nasabah.data.room.entity.PenarikanEntity
import com.bs.sriwilis.nasabah.helper.Result
import com.bs.sriwilis.nasabah.helper.ResultAuth
import com.bs.sriwilispetugas.data.room.KeranjangTransaksiEntity
import com.bs.sriwilispetugas.data.room.PesananSampahKeranjangEntity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(
    private val apiService: ApiServiceMain,
    private val appDatabase: AppDatabase
) {

    private val mappingPesananSampah = MappingNasabah()
    private val mappingKategori = MappingCategory()
    private val mappingPesanan = MappingPesanan()
    private val mappingPenarikan = MappingPenarikan()
    private val mappingTransaksi = MappingTransaksi()
    private val mappingKatalog = MappingCatalog()

    suspend fun logoutApi(token: String): Result<RegisterResponseDTO> {
        return try {
            val response = apiService.logout("Bearer $token")
            Log.d("response body logout", response.body().toString())
            if (response.isSuccessful) {
                val registerResponseDTO = response.body()
                if (registerResponseDTO != null) {
                    Result.Success(registerResponseDTO)
                } else {
                    Result.Error("Empty Response Body")
                }
            } else {
                Result.Error("Failed to Register: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Error: ${e.message}")
        }
    }

    data class ErrorResponse(
        val success: Boolean,
        val message: String
    )

    suspend fun logout() {
        val token = getToken() ?: ""
        withContext(Dispatchers.IO) {
            try {
                appDatabase.loginResponseDao().deleteAll()
                appDatabase.pesananSampahKeranjangDao().deleteAllPesananSampahKeranjang()
                appDatabase.pesananSampahDao().deleteAllPesananSampah()
                appDatabase.nasabahDao().deleteAllNasabah()
                appDatabase.keranjangTransaksiDao().deleteAllKeranjangTransaksi()
                appDatabase.transaksiSampahDao().deleteAllTransaksiSampah()
                appDatabase.catalogDao().deleteAllCatalog()
                appDatabase.categoryDao().deleteAllCategory()
                appDatabase.penarikanDao().deleteAllPenarikan()

                logoutApi(token)
            } catch (e: Exception) {
                Log.e("Logout Error", "Error during logout: ${e.message}")
                e.printStackTrace()
            }
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

    suspend fun getAllPesanan(): Result<List<PesananSampahKeranjangEntity>> {
        return try {
            val token = getToken() ?: return Result.Error("Token is null")
            val response = apiService.getAllPesananSampahKeranjang("Bearer $token", getPhoneLoggedAccount())
            if (response.isSuccessful) {
                val responseBody = response.body() ?: return Result.Error("Response body is null")

                Log.d("Body Pesanan", "$responseBody")

                // Mapping dari DTO ke Entitas Room
                val (keranjangEntities, sampahEntities) = mappingPesanan.mapPesananSampahApiResponseDtoToEntities(responseBody)

                // Simpan data ke database Room (opsional, jika perlu disimpan)
                withContext(Dispatchers.IO) {
                    appDatabase.pesananSampahKeranjangDao().insertAllPesananSampahKeranjang(keranjangEntities)
                    appDatabase.pesananSampahDao().insertAllPesananSampah(sampahEntities)
                }
                Result.Success(keranjangEntities)
            } else {
                Result.Error("Failed to fetch data: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error("Error occurred: ${e.message}")
        }
    }

    suspend fun getAllTransaksi(): Result<List<KeranjangTransaksiEntity>> {
        return try {
            val token = getToken() ?: return Result.Error("Token is null")
            val response = apiService.getAllKeranjangTransaksi("Bearer $token", getPhoneLoggedAccount())
            if (response.isSuccessful) {
                val responseBody = response.body() ?: return Result.Error("Response body is null")

                // Mapping dari DTO ke Entitas Room
                val (keranjangEntities, sampahEntities) = mappingTransaksi.mapTransaksiSampahApiResponseDtoToEntities(responseBody)
                Log.d("cek sampah entities", sampahEntities.toString())
                // Simpan data ke database Room (opsional, jika perlu disimpan)
                withContext(Dispatchers.IO) {
                    appDatabase.keranjangTransaksiDao().insertAllKeranjangTransaksi(keranjangEntities)
                    appDatabase.transaksiSampahDao().insertAllTransaksiSampah(sampahEntities)
                }
                Result.Success(keranjangEntities)
            } else {
                Result.Error("Failed to fetch data: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error("Error occurred: ${e.message}")
        }
    }

    private suspend fun getNasabah(): Result<List<NasabahEntity>> {
        return try {
            val token = getToken() ?: return Result.Error("Token is null")
            val phone = getPhoneLoggedAccount()
            val response = apiService.getNasabah(phone,"Bearer $token")

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

    suspend fun getCombinedPesananData(): Result<List<CardPesanan>> {
        return withContext(Dispatchers.IO) {
            try {
                val combinedData = appDatabase.pesananSampahKeranjangDao().getCombinedPesananData()
                Result.Success(combinedData)
            } catch (e: Exception) {
                Result.Error("Error occurred: ${e.message}")
            }
        }
    }

    suspend fun getCombinedTransaksiData(): Result<List<CardTransaksi>> {
        return withContext(Dispatchers.IO) {
            try {
                val combinedData = appDatabase.keranjangTransaksiDao().getCombinedTransaksiData()
                Result.Success(combinedData)
            } catch (e: Exception) {
                Result.Error("Error occurred: ${e.message}")
            }
        }
    }

    suspend fun getPesananSampah(idPesanan: String): Result<List<CardDetailPesanan>> {
        return withContext(Dispatchers.IO) {
            try {
                val combinedData = appDatabase.pesananSampahKeranjangDao().getPesananSampah(idPesanan)
                Result.Success(combinedData)
            } catch (e: Exception) {
                Result.Error("Error occurred: ${e.message}")
            }
        }
    }

    suspend fun getTransaksiSampah(idPesanan: String): Result<List<CardDetailPesanan>> {
        return withContext(Dispatchers.IO) {
            try {
                val combinedData = appDatabase.keranjangTransaksiDao().getTransaksiSampah(idPesanan)
                Result.Success(combinedData)
            } catch (e: Exception) {
                Result.Error("Error occurred: ${e.message}")
            }
        }
    }

    suspend fun getDataDetailPesananSampahKeranjang(idPesanan: String): Result<CardPesanan> {
        return withContext(Dispatchers.IO) {
            try {
                val detailPesananSampahKeranjang = appDatabase.pesananSampahKeranjangDao().getDataDetailPesananSampahKeranjang(idPesanan)
                Result.Success(detailPesananSampahKeranjang)
            } catch (e: Exception) {
                Result.Error("Error occurred: ${e.message}")
            }
        }
    }

    suspend fun getDataDetailKeranjangTransaksi(idPesanan: String): Result<CardTransaksi> {
        return withContext(Dispatchers.IO) {
            try {
                val detailKeranjangTransaksi = appDatabase.keranjangTransaksiDao().getDataDetailKeranjangTransaksi(idPesanan)
                Result.Success(detailKeranjangTransaksi)
            } catch (e: Exception) {
                Result.Error("Error occurred: ${e.message}")
            }
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

    suspend fun changePassword(oldPassword: String, newPassword: String): Result<ChangeProfileResponseDTO> {
        return try {
            val phone = getPhoneLoggedAccount()
            val token = getToken() ?: return Result.Error("Token is null")
            val response = apiService.editPassword(phone, "Bearer $token" , oldPassword, newPassword)

            if (response.isSuccessful) {
                val changeProfileResponseDTO = response.body()
                if (changeProfileResponseDTO != null) {
                    Result.Success(changeProfileResponseDTO)
                } else {
                    Result.Error("Empty Response Body")
                }
            } else {
                when {
                    response.code() == 400 -> {
                        Result.Error("kata sandi lama tidak cocok")
                    }
                    else -> {Result.Error("Failed to Change Password: ${response.code()}")}
                }
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

    suspend fun addPenarikanCash(nominal: Long): ResultAuth<AddPenarikanDTO> {
        return try {
            val no_hp_nasabah = getPhoneLoggedAccount()
            val token = getToken() ?: return ResultAuth.Error(401, "Token is null")

            val response = apiService.addPenarikanCash("Bearer $token", no_hp_nasabah, "Tunai", nominal)

            if (response.isSuccessful) {
                val addPenarikanResponseDTO = response.body()
                if (addPenarikanResponseDTO != null) {
                    ResultAuth.Success(addPenarikanResponseDTO)
                } else {
                    ResultAuth.Error(response.code(), response.message())
                }
            } else {
                val errorBodyString = response.errorBody()?.string()
                Log.d("API_ERROR", "Error Body: $errorBodyString")

                val errorResponse = parseErrorResponse(errorBodyString)
                ResultAuth.Error(response.code(), errorResponse)
            }
        } catch (e: Exception) {
            ResultAuth.Error(500, "Error: ${e.message}")
        }
    }


    suspend fun addPenarikanPLN(nominal: Long, nomorMeteran: Long): ResultAuth<AddPenarikanDTO> {
        return try {
            val no_hp_nasabah = getPhoneLoggedAccount()
            val token = getToken() ?: return ResultAuth.Error(401, "Token is null")

            val response = apiService.addPenarikanPLN("Bearer $token", no_hp_nasabah, "PLN", nominal, nomorMeteran)

            if (response.isSuccessful) {
                val addPenarikanResponseDTO = response.body()
                if (addPenarikanResponseDTO != null) {
                    ResultAuth.Success(addPenarikanResponseDTO)
                } else {
                    ResultAuth.Error(response.code(), response.message())
                }
            } else {
                // Parsing the error message from response body
                val errorResponse = parseErrorResponse(response.errorBody()?.string())
                ResultAuth.Error(response.code(), errorResponse)
            }
        } catch (e: Exception) {
            ResultAuth.Error(500, "Error: ${e.message}")
        }
    }

    suspend fun addPenarikanTransfer(nominal: Long, nomorRekening: Long, jenisBank: String): ResultAuth<AddPenarikanDTO> {
        return try {
            val no_hp_nasabah = getPhoneLoggedAccount()
            val token = getToken() ?: return ResultAuth.Error(401, "Token is null")

            val response = apiService.addPenarikanTransfer("Bearer $token", no_hp_nasabah, "Transfer", nominal, nomorRekening, jenisBank)

            if (response.isSuccessful) {
                val addPenarikanResponseDTO = response.body()
                if (addPenarikanResponseDTO != null) {
                    ResultAuth.Success(addPenarikanResponseDTO)
                } else {
                    ResultAuth.Error(response.code(), response.message())
                }
            } else {
                // Parsing the error message from response body
                val errorResponse = parseErrorResponse(response.errorBody()?.string())
                ResultAuth.Error(response.code(), errorResponse)
            }
        } catch (e: Exception) {
            ResultAuth.Error(500, "Error: ${e.message}")
        }
    }

    // end of penarikan

    suspend fun syncDataKeranjangTransaksi(): Result<Unit> {
        return try {
            appDatabase.keranjangTransaksiDao().deleteAllKeranjangTransaksi()
            appDatabase.transaksiSampahDao().deleteAllTransaksiSampah()

            val transaksiResult = getAllTransaksi()
            if (transaksiResult is Result.Error) {
                return Result.Error("Failed to sync transaksi: ${transaksiResult.error}")
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error occurred during synchronization: ${e.message}")
        }
    }

    suspend fun syncDataPesananSampah(): Result<Unit> {
        return try {
            appDatabase.pesananSampahKeranjangDao().deleteAllPesananSampahKeranjang()
            appDatabase.pesananSampahDao().deleteAllPesananSampah()

            val pesananResult = getAllPesanan()
            if (pesananResult is Result.Error) {
                return Result.Error("Failed to sync pesanan: ${pesananResult.error}")
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error occurred during synchronization: ${e.message}")
        }
    }

    suspend fun syncDataPenarikan(): Result<Unit> {
        return try {
            appDatabase.penarikanDao().deleteAllPenarikan()

            val penarikanResult = getPenarikan()
            if (penarikanResult is Result.Error) {
                return Result.Error("Failed to sync penarikan: ${penarikanResult.error}")
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error occurred during synchronization: ${e.message}")
        }
    }

    suspend fun  syncDataCategory(): Result<Unit> {
        return try {
            appDatabase.categoryDao().deleteAllCategory()

            val categoryResult = getCategory()
            if (categoryResult is Result.Error) {
                return Result.Error("Failed to sync penarikan: ${categoryResult.error}")
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error occurred during synchronization: ${e.message}")
        }
    }

    suspend fun  syncDataCatalog(): Result<Unit> {
        return try {
            appDatabase.catalogDao().deleteAllCatalog()

            val catalogResult = getCatalog()
            if (catalogResult is Result.Error) {
                return Result.Error("Failed to sync penarikan: ${catalogResult.error}")
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error occurred during synchronization: ${e.message}")
        }
    }

    suspend fun syncDataNasabah(): Result<Unit> {
        return try {
            appDatabase.nasabahDao().deleteAllNasabah()

            val nasabahResult = getNasabah()
            if (nasabahResult is Result.Error) {
                return Result.Error("Failed to sync nasabah: ${nasabahResult.error}")
            }
            syncLoggedAccount()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error occurred during synchronization: ${e.message}")
        }
    }

    suspend fun syncData(): Result<Unit> {
        return try {
            appDatabase.pesananSampahKeranjangDao().deleteAllPesananSampahKeranjang()
            appDatabase.pesananSampahDao().deleteAllPesananSampah()
            appDatabase.keranjangTransaksiDao().deleteAllKeranjangTransaksi()
            appDatabase.transaksiSampahDao().deleteAllTransaksiSampah()
            appDatabase.nasabahDao().deleteAllNasabah()
            appDatabase.categoryDao().deleteAllCategory()
            appDatabase.catalogDao().deleteAllCatalog()
            appDatabase.penarikanDao().deleteAllPenarikan()

            val nasabahResult = getNasabah()
            if (nasabahResult is Result.Error) {
                return Result.Error("Failed to sync nasabah: ${nasabahResult.error}")
            }
            syncLoggedAccount()

            val categoryResult = getCategory()
            if (categoryResult is Result.Error) {
                return Result.Error("Failed to sync category: ${categoryResult.error}")
            }

            val penarikanResult = getPenarikan()
            if (penarikanResult is Result.Error) {
                return Result.Error("Failed to sync penarikan: ${penarikanResult.error}")
            }

            val catalogResult = getCatalog()
            if (catalogResult is Result.Error) {
                return Result.Error("Failed to sync penarikan: ${catalogResult.error}")
            }

            val pesananResult = getAllPesanan()
            if (pesananResult is Result.Error) {
                return Result.Error("Failed to sync pesanan: ${pesananResult.error}")
            }

            val transaksiResult = getAllTransaksi()
            if (transaksiResult is Result.Error) {
                return Result.Error("Failed to sync transaksi: ${transaksiResult.error}")
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error occurred during synchronization: ${e.message}")
        }
    }

    suspend fun addCartOrder(lat: String, long: String, pesanan_sampah: List<CartOrder>): Result<PesananSampahItemResponse?> {

        val token = getToken() ?: return Result.Error("Token is null")
        return try {
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

    //catalog

    suspend fun getAllCatalogDao(): Result<List<Catalog>> {
        return withContext(Dispatchers.IO) {
            try {
                val categoryData = appDatabase.catalogDao().getAllCatalog()
                Result.Success(categoryData)
            } catch (e: Exception) {
                Result.Error("Error occured: ${e.message}")
            }
        }
    }

    suspend fun getCatalog(): Result<List<CatalogEntity>> {
        return try {
            val token = getToken() ?: return Result.Error("Token is null")
            val response = apiService.getAllCatalog("Bearer $token")

            if (response.isSuccessful) {
                val responseBody = response.body() ?: return Result.Error("Response body is null")

                val catalogEntities = mappingKatalog.mapCatalogResponseDtoToEntity(responseBody)

                withContext(Dispatchers.IO) {
                    appDatabase.catalogDao().insert(catalogEntities)
                }

                Result.Success(catalogEntities)
            } else {
                Result.Error("Failed to fetch data: ${response.message()} (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error("Error occurred: ${e.message}")
        }
    }

    //end of catalog

    private fun parseErrorResponse(errorBody: String?): String {
        return try {
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            errorResponse?.message ?: "Unknown error"
        } catch (e: JsonSyntaxException) {
            errorBody ?: "Unknown error"
        }
    }
}
