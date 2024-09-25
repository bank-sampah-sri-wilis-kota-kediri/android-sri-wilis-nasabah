package com.bs.sriwilis.nasabah.data.response

data class NasabahResponseDTO(
    val success: Boolean?,
    val message: String?,
    val data: List<Nasabah>?
)

data class Nasabah(
    val id: Int,
    val nama_nasabah: String?,
    val no_hp_nasabah: String?,
    val alamat_nasabah: String?,
    val saldo_nasabah: String?,
    val jasa: String?,
    val is_dapat_jasa: String?,
    val gambar_nasabah: String?,
    val created_at: String?,
    val updated_at: String?
)