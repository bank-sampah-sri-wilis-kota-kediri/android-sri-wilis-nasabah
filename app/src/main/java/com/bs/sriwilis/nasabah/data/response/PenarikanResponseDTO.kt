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
    val nominal: Long,
    val tanggal: String,
    val nomor_meteran: Long?,
    val nomor_rekening: Long?,
    val jenis_bank: String?,
    val nomor_token: String?,
    val status_penarikan: String
)
