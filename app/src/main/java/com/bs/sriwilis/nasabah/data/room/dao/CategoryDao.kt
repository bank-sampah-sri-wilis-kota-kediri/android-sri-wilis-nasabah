package com.bs.sriwilis.nasabah.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bs.sriwilis.nasabah.data.model.Category
import com.bs.sriwilis.nasabah.data.room.entity.CategoryEntity
import com.bs.sriwilis.nasabah.data.room.entity.NasabahEntity

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryEntity: List<CategoryEntity>)

    @Query("SELECT * FROM category_table")
    suspend fun getAllCategory(): List<Category>

    @Query("SELECT * FROM category_table WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): Category

    @Query("SELECT * FROM category_table WHERE id = :id")
    suspend fun updateCategoryById(id: String): NasabahEntity

    @Query("DELETE FROM category_table WHERE id = :id")
    suspend fun deleteCategoryById(id: String)
}