package com.bs.sriwilis.nasabah.data.mapping

import com.bs.sriwilis.nasabah.data.response.CategoryResponseDTO
import com.bs.sriwilis.nasabah.data.room.entity.CategoryEntity

class MappingCategory {
    fun mapCategoryResponseDtoToEntity(dto: CategoryResponseDTO): List<CategoryEntity> {
        val categoryEntities = mutableListOf<CategoryEntity>()

        dto.data?.forEach { kategori ->
            val nasabahEntity = CategoryEntity(
                id = kategori.id,
                nama_kategori = kategori.nama_kategori,
                harga_kategori = kategori.harga_kategori,
                jenis_kategori = kategori.jenis_kategori,
                gambar_kategori = kategori.gambar_kategori,
            )
            categoryEntities.add(nasabahEntity)
        }

        return categoryEntities
    }
}