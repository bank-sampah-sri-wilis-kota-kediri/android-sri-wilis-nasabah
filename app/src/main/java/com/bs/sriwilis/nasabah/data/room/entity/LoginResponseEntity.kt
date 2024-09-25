package com.bs.sriwilis.nasabah.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "login_response_table")
data class LoginResponseEntity(
    @PrimaryKey val id: Int,
    val success: Boolean?,
    val message: String?,
    val access_token: String?,
    val nama_nasabah: String?,
    val alamat_nasabah: String?,
    val saldo_nasabah: String?,
    val is_dapat_jasa: String?,
    val jasa: String?,
    val gambar_nasabah: String?,
    val no_hp_nasabah: String
)