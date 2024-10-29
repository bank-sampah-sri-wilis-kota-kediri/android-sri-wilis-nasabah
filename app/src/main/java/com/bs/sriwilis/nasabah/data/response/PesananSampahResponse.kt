package com.bs.sriwilis.nasabah.data.response

data class PesananSampahResponseDTO(
    val success: Boolean,
    val data: List<DataKeranjang>
)

data class DataKeranjang(
    val id: Int,
    val id_pesanan: String,
    val id_nasabah: Int,
    val id_petugas: Int,
    val nominal_transaksi: String,
    val tanggal: String,
    val lat: String,
    val long: String,
    val keterangan: String,
    val status_pesanan: String,
    val created_at: String,
    val updated_at: String,
    val pesanan_sampah: List<PesananSampah>
)

data class PesananSampah(
    val id: Int,
    val id_pesanan_sampah_keranjang: String,
    val kategori: String,
    val berat_perkiraan: Float,
    val harga_perkiraan: String,
    val gambar: String?,
    val created_at: String,
    val updated_at: String
)
