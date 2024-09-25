package com.bs.sriwilis.nasabah.data.response

import java.util.Date

data class PenarikanResponseDTO(
    val success: Boolean?,
    val message: String?,
    val data: List<Penarikan>?
)

data class Penarikan(
    val id: Int,
    val id_nasabah: Int,
    val jenis_penarikan: String,
    val nominal: Double,
    val tanggal: String,
    val nomor_meteran: Int?,
    val nomor_rekening: Int?,
    val jenis_bank: String?,
    val status_penarikan: String
)
