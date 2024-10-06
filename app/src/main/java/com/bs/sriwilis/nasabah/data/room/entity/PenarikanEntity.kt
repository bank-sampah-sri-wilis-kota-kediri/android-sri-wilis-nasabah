package com.bs.sriwilis.nasabah.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "penarikan_uang_table")
data class PenarikanEntity(
    @PrimaryKey
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