package com.bs.sriwilispetugas.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pesanan_sampah_keranjang_table")
data class PesananSampahKeranjangEntity(
    @PrimaryKey val id_pesanan: String,
    val id_nasabah: Int?,
    val id_petugas: Int?,
    val nominal_transaksi: String?,
    val tanggal: String?,
    val lat: String?,
    val lng: String?,
    val status_pesanan: String?,
    val created_at: String?,
    val updated_at: String?
)
