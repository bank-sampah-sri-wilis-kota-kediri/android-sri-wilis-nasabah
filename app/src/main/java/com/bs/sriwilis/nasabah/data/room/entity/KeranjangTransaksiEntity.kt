package com.bs.sriwilispetugas.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keranjang_transaksi_table")
data class KeranjangTransaksiEntity(
    @PrimaryKey val id: Int,
    val id_nasabah: Int?,
    val nominal_transaksi: String?,
    val tanggal: String?,
    val status_transaksi: String?,
    val created_at: String?,
    val updated_at: String?
)
