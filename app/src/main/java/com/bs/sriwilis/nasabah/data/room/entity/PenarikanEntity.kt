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
    val nominal: Double,
    val tanggal: String,
    val nomor_meteran: Int?,
    val nomor_rekening: Int?,
    val jenis_bank: String?,
    val status_penarikan: String
)