package com.bs.sriwilis.nasabah.data.response

data class CatalogResponseDTO (
	val data: List<CatalogData>?,
	val success: Boolean?,
	val message: String?
)

data class CatalogData(
	val id: Int,
	val judul_katalog: String?,
	val deskripsi_katalog: String?,
	val harga_katalog: String?,
	val no_wa: String?,
	val shopee_link: String?,
	val gambar_katalog: String?,
)
