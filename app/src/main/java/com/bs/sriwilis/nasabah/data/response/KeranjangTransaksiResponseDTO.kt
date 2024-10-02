package com.bs.sriwilis.nasabah.data.response

data class KeranjangTransaksiResponseDTO(
    val success: Boolean,
    val data: List<TransaksiSampah>
)

data class TransaksiSampah(
    val id: Int,
    val id_nasabah: Int,
    val nominal_transaksi: String,
    val tanggal: String,
    val status_transaksi: String,
    val created_at: String?,
    val updated_at: String?,
    val transaksi_sampah: List<Sampah>?
)

data class Sampah(
    val id: Int,
    val id_keranjang_transaksi: Int,
    val kategori: String,
    val berat: Int,
    val harga: String,
    val gambar: String?,
    val created_at: String?,
    val updated_at: String?
)
