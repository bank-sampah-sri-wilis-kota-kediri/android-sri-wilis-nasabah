package com.bs.sriwilis.nasabah.data.response

data class CategoryResponseDTO (
    val data: List<CategoryItem>?,
    val success: Boolean?,
    val message: String?
)

data class CategoryItem(
    val id: Int,
    val nama_kategori: String?,
    val harga_kategori: String?,
    val jenis_kategori: String?,
    val gambar_kategori: String?,
)