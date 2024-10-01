package com.bs.sriwilis.nasabah.data.model

import androidx.room.PrimaryKey

data class Catalog(
    val id: Int,
    val judul_katalog: String?,
    val deskripsi_katalog: String?,
    val harga_katalog: String?,
    val no_wa: String?,
    val shopee_link: String?,
    val gambar_katalog: String?,
)