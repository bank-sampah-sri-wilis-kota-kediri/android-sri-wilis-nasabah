package com.bs.sriwilis.nasabah.data.response

data class LoginResponseDTO(
    val success: Boolean?,
    val message: String?,
    val data: DataNasabahDTO?
)

data class DataNasabahDTO(
    val access_token: String?,
    val nama_nasabah: String?,
    val alamat_nasabah: String?,
    val saldo_nasabah: String?,
    val is_dapat_jasa: String?,
    val jasa: String?,
    val gambar_nasabah: String?,
    val no_hp_nasabah: String
)
