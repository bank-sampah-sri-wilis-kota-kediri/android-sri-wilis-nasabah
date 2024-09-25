package com.bs.sriwilis.nasabah.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bs.sriwilis.nasabah.data.model.LoggedAccount
import com.bs.sriwilis.nasabah.data.room.entity.NasabahEntity

@Dao
interface NasabahDao {

    // Insert a single NasabahApiResponseEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNasabah(nasabah: NasabahEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNasabah(nasabahList: List<NasabahEntity>)

    // Get all Nasabah from the table
    @Query("SELECT * FROM nasabah_table")
    suspend fun getAllNasabah(): List<NasabahEntity>

    // Get a single Nasabah by its ID
    @Query("SELECT * FROM nasabah_table WHERE id = :id")
    suspend fun getNasabahById(id: Int): NasabahEntity?// Get a single Nasabah by its ID

    @Query("SELECT * FROM nasabah_table WHERE no_hp_nasabah = :no_hp_nasabah")
    suspend fun getNasabahByPhone(no_hp_nasabah: String): LoggedAccount

    // Update a Nasabah
    @Update
    suspend fun updateNasabah(nasabah: NasabahEntity)

    // Delete a Nasabah
    @Delete
    suspend fun deleteNasabah(nasabah: NasabahEntity)

    // Delete all Nasabah records
    @Query("DELETE FROM nasabah_table")
    suspend fun deleteAllNasabah()

    // Update Nasabah by phone number
    @Query("""
        UPDATE nasabah_table 
        SET nama_nasabah = :name, 
            alamat_nasabah = :address, 
            gambar_nasabah = :gambar 
        WHERE no_hp_nasabah = :phone
    """)
    suspend fun updateNasabahByPhone(
        phone: String,
        name: String,
        address: String,
        gambar: String
    )
}