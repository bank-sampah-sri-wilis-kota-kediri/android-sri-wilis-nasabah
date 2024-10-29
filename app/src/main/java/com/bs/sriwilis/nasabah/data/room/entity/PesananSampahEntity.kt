package com.bs.sriwilispetugas.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "pesanan_sampah_table",
    foreignKeys = [ForeignKey(
        entity = PesananSampahKeranjangEntity::class,
        parentColumns = ["id_pesanan"],
        childColumns = ["id_pesanan_sampah_keranjang"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PesananSampahEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val id_pesanan_sampah_keranjang: String,
    val kategori: String?,
    val berat_perkiraan: Float?,
    val harga_perkiraan: String?,
    val gambar: String?,
    val created_at: String?,
    val updated_at: String?
)
