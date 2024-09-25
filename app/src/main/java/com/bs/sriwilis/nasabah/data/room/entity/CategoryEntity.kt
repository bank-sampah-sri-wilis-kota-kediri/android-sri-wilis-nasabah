package com.bs.sriwilis.nasabah.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val nama_kategori: String?,
    val harga_kategori: String?,
    val jenis_kategori: String?,
    val gambar_kategori: String?,
)