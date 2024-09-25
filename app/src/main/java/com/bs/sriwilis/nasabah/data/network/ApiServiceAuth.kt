package com.bs.sriwilis.nasabah.data.network

import com.bs.sriwilis.nasabah.data.response.LoginResponseDTO
import com.bs.sriwilis.nasabah.data.response.RegisterResponseDTO
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiServiceAuth {
    @FormUrlEncoded
    @POST("nasabah/login")
    suspend fun login(
        @Header("Accept") accept: String = "application/json",
        @Header("Content-Type") contentType: String = "application/x-www-form-urlencoded",
        @Field("no_hp_nasabah") no_hp_nasabah: String,
        @Field("password_nasabah") password_nasabah: String
    ): Response<LoginResponseDTO>


    @FormUrlEncoded
    @POST("nasabah/register")
    suspend fun register(
        @Field("nama_nasabah") nama_nasabah: String,
        @Field("alamat_nasabah") alamat_nasabah: String,
        @Field("no_hp_nasabah") no_hp_nasabah: String,
        @Field("password_nasabah") password_nasabah: String
    ): Response<RegisterResponseDTO>



}