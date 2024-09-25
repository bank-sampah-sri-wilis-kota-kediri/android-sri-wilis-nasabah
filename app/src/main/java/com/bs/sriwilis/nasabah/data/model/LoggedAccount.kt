package com.bs.sriwilis.nasabah.data.model

data class LoggedAccount(
    val id: String?,
    val nama_nasabah: String?,
    val no_hp_nasabah: String?,
    val alamat_nasabah: String?,
    val saldo_nasabah: String?,
    val jasa: String?,
    val is_dapat_jasa: String?,
    val gambar_nasabah: String?
)
