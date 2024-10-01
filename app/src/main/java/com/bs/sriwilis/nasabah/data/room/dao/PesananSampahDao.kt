package com.bs.sriwilispetugas.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bs.sriwilispetugas.data.room.PesananSampahEntity

@Dao
interface PesananSampahDao {

    // Insert a single PesananSampahEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPesananSampah(pesananSampah: PesananSampahEntity)

    // Insert multiple PesananSampahEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPesananSampah(pesananSampahList: List<PesananSampahEntity>)

    // Get all PesananSampah for a specific DataKeranjang (by id_pesanan_keranjang)
    @Query("SELECT * FROM pesanan_sampah_table WHERE id_pesanan_sampah_keranjang = :idPesananKeranjang")
    suspend fun getPesananSampahByKeranjangId(idPesananKeranjang: String): List<PesananSampahEntity>

    // Update a PesananSampah
    @Update
    suspend fun updatePesananSampah(pesananSampah: PesananSampahEntity)

    @Query("DELETE FROM pesanan_sampah_table")
    suspend fun deleteAllPesananSampah()

    // Delete all PesananSampah records for a specific DataKeranjang
    @Query("DELETE FROM pesanan_sampah_table WHERE id_pesanan_sampah_keranjang = :idPesananKeranjang")
    suspend fun deletePesananSampahByKeranjangId(idPesananKeranjang: String)
}