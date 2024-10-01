package com.bs.sriwilispetugas.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaksi_sampah_table",
    foreignKeys = [ForeignKey(
        entity = PesananSampahKeranjangEntity::class,
        parentColumns = ["id"],
        childColumns = ["id_keranjang_transaksi"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TransaksiSampahEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val id_keranjang_transaksi: String,
    val kategori: String?,
    val berat: Int?,
    val harga: String?,
    val gambar: String?,
    val created_at: String?,
    val updated_at: String?
)
