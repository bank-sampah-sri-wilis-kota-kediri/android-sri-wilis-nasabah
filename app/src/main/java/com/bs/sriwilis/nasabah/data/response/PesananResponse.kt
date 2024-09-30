package com.bs.sriwilis.nasabah.data.response

import com.google.gson.annotations.SerializedName

data class PesananSampahItemResponse(
    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class PesananSampahItem(

    @field:SerializedName("harga_perkiraan")
    val harga_perkiraan: Double,

    @field:SerializedName("berat_perkiraan")
    val berat_perkiraan: Int? = null,

    @field:SerializedName("kategori")
    val kategori: String? = null,

    @field:SerializedName("gambar")
    val gambar: String? = null
)

// Request

data class CartOrderRequest(
    @SerializedName("pesanan_sampah")
    val pesananSampah: List<PesananSampahItem>,

    @SerializedName("no_hp_nasabah")
    val noHpNasabah: String,

    @SerializedName("id_petugas")
    val idPetugas: String,

    @SerializedName("lat")
    val lat: String,

    @SerializedName("long")
    val long: String,

    @SerializedName("tanggal")
    val tanggal: String
)
