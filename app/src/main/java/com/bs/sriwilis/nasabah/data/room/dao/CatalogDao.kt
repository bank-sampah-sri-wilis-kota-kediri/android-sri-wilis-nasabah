package com.bs.sriwilis.nasabah.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bs.sriwilis.nasabah.data.model.Catalog
import com.bs.sriwilis.nasabah.data.model.Category
import com.bs.sriwilis.nasabah.data.room.entity.CatalogEntity

@Dao
interface CatalogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryEntity: List<CatalogEntity>)

    @Query("SELECT * FROM catalog_table")
    suspend fun getAllCatalog(): List<Catalog>

    @Query("DELETE FROM catalog_table")
    suspend fun deleteAllCatalog()
}