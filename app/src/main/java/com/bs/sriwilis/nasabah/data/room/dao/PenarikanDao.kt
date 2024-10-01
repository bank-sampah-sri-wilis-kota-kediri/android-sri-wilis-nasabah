package com.bs.sriwilis.nasabah.data.room.dao

import androidx.room.*
import com.bs.sriwilis.nasabah.data.model.PenarikanData
import com.bs.sriwilis.nasabah.data.room.entity.PenarikanEntity

@Dao
interface PenarikanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(penarikanEntity: List<PenarikanEntity>)

    @Update
    suspend fun updatePenarikan(penarikan: PenarikanEntity)

    @Delete
    suspend fun deletePenarikan(penarikan: PenarikanEntity)

    @Query("SELECT * FROM penarikan_uang_table WHERE id = :id LIMIT 1")
    suspend fun getPenarikanById(id: Int): PenarikanEntity?

    @Query("SELECT * FROM penarikan_uang_table WHERE id_nasabah = :idNasabah")
    suspend fun getPenarikanByNasabahId(idNasabah: Int): List<PenarikanEntity>

    @Query("SELECT * FROM penarikan_uang_table ORDER BY tanggal DESC")
    suspend fun getAllPenarikan(): List<PenarikanData>

    // Delete all Penarikan records
    @Query("DELETE FROM penarikan_uang_table")
    suspend fun deleteAllPenarikan()
}
