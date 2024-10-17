package com.bs.sriwilis.nasabah.data.network

import com.bs.sriwilis.nasabah.data.response.AddPenarikanDTO
import com.bs.sriwilis.nasabah.data.response.CartOrderRequest
import com.bs.sriwilis.nasabah.data.response.CatalogResponseDTO
import com.bs.sriwilis.nasabah.data.response.CategoryResponseDTO
import com.bs.sriwilis.nasabah.data.response.ChangeProfileResponseDTO
import com.bs.sriwilis.nasabah.data.response.KeranjangTransaksiResponseDTO
import com.bs.sriwilis.nasabah.data.response.NasabahResponseDTO
import com.bs.sriwilis.nasabah.data.response.PenarikanResponseDTO
import com.bs.sriwilis.nasabah.data.response.PesananSampahItemResponse
import com.bs.sriwilis.nasabah.data.response.PesananSampahResponseDTO
import com.bs.sriwilis.nasabah.data.response.RegisterResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServiceMain {

    // nasabah
    @Headers("Content-Type: application/json")
    @GET("nasabah/{phone}")
    suspend fun getNasabah(
        @Path("phone") phone: String?,
        @Header("Authorization") token: String
    ): Response<NasabahResponseDTO>

    @FormUrlEncoded
    @PUT("nasabah/edit-by-phone/{phone}")
    suspend fun editNasabah(
        @Path("phone") phone: String?,
        @Header("Authorization") token: String,
        @Field("nama_nasabah") nama_nasabah: String?,
        @Field("no_hp_nasabah") no_hp_nasabah: String?,
        @Field("alamat_nasabah") alamat_nasabah: String?,
        @Field("gambar_nasabah") gambar_nasabah: String?
    ): Response<ChangeProfileResponseDTO>

    @FormUrlEncoded
    @PUT("nasabah/edit-by-phone/{phone}")
    suspend fun editPassword(
        @Path("phone") phone: String?,
        @Header("Authorization") token: String,
        @Field("old_password_nasabah") old_password: String?,
        @Field("password_nasabah") password_nasabah: String?
    ): Response<ChangeProfileResponseDTO>

    // end of nasabah

    // category

    @GET("kategori/show-all")
    suspend fun getAllCategory(
        @Header("Authorization") token: String,
    ): Response<CategoryResponseDTO>

    // end of category


    // penarikan

    @GET("penarikan/show/{phone}")
    suspend fun getAllPenarikan(
        @Path("phone") phone: String?,
        @Header("Authorization") token: String,
    ): Response<PenarikanResponseDTO>

    @FormUrlEncoded
    @POST("penarikan/add")
    suspend fun addPenarikanCash(
        @Header("Authorization") token: String,
        @Field("no_hp_nasabah") no_hp_nasabah: String?,
        @Field("jenis_penarikan") jenis_penarikan: String?,
        @Field("nominal") nominal: Long?
    ): Response<AddPenarikanDTO>

    @FormUrlEncoded
    @POST("penarikan/add")
    suspend fun addPenarikanPLN(
        @Header("Authorization") token: String,
        @Field("no_hp_nasabah") no_hp_nasabah: String?,
        @Field("jenis_penarikan") jenis_penarikan: String?,
        @Field("nominal") nominal: Long?,
        @Field("nomor_meteran") nomor_meteran: Long?
    ): Response<AddPenarikanDTO>


    @FormUrlEncoded
    @POST("penarikan/add")
    suspend fun addPenarikanTransfer(
        @Header("Authorization") token: String,
        @Field("no_hp_nasabah") no_hp_nasabah: String?,
        @Field("jenis_penarikan") jenis_penarikan: String?,
        @Field("nominal") nominal: Long?,
        @Field("nomor_rekening") nomor_rekening: Long?,
        @Field("jenis_bank") jenis_bank: String?
    ): Response<AddPenarikanDTO>


    // end of penarikan


    // Pesanan Sampah

    @Headers("Content-type: application/json")
    @POST("pesanan/add")
    suspend fun addCartOrder(
        @Header("Authorization") token: String,
        @Body cartTransactionRequest: CartOrderRequest
    ): Response<PesananSampahItemResponse>


    @GET("pesanan/show-all-pesanan-sampah-keranjang/{phone}")
    suspend fun getAllPesananSampahKeranjang(
        @Header("Authorization") token: String,
        @Path("phone") phone: String?
    ): Response<PesananSampahResponseDTO>


    @GET("transaksi/show-all/{phone}")
    suspend fun getAllKeranjangTransaksi(
        @Header("Authorization") token: String,
        @Path("phone") phone: String?
    ): Response<KeranjangTransaksiResponseDTO>


    // End Of Pesanan Sampah

    //Katalog Sampah
    @GET("katalog/show-all")
    suspend fun getAllCatalog(
        @Header("Authorization") token: String,
    ): Response<CatalogResponseDTO>


    // logout
    @Headers("Content-type: application/json")
    @POST("nasabah/logout")
    suspend fun logout(
        @Header("Authorization") token: String,
    ): Response<RegisterResponseDTO>
}