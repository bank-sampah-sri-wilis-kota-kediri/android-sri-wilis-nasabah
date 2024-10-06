package com.bs.sriwilis.nasabah.data.model

data class PenarikanData(
    val id: String,
    val id_nasabah: String,
    val jenis_penarikan: String,
    val nominal: String,
    val tanggal: String,
    val nomor_meteran: String?,
    val nomor_rekening: String?,
    val jenis_bank: String?,
    val nomor_token: String?,
    val status_penarikan: String
)