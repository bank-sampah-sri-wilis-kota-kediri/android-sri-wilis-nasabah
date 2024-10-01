package com.bs.sriwilis.nasabah.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catalog_table")
data class CatalogEntity(
    @PrimaryKey val id: Int,
    val judul_katalog: String?,
    val deskripsi_katalog: String?,
    val harga_katalog: String?,
    val no_wa: String?,
    val shopee_link: String?,
    val gambar_katalog: String?,
)
