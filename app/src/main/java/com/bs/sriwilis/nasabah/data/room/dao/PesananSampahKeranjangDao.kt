package com.bs.sriwilispetugas.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.bs.sriwilis.nasabah.data.model.CardDetailPesanan
import com.bs.sriwilis.nasabah.data.model.CardPesanan
import com.bs.sriwilispetugas.data.room.PesananSampahKeranjangEntity

@Dao
interface PesananSampahKeranjangDao {

    // Insert a single PesananSampahKeranjangEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPesananSampahKeranjang(pesananSampahKeranjang: PesananSampahKeranjangEntity): Long

    // Insert multiple PesananSampahKeranjangEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPesananSampahKeranjang(pesananSampahKeranjangList: List<PesananSampahKeranjangEntity>)

    // Get all PesananSampahKeranjang from the table
    @Query("SELECT * FROM pesanan_sampah_keranjang_table")
    suspend fun getAllPesananSampahKeranjang(): List<PesananSampahKeranjangEntity>

    // Get a single PesananSampahKeranjang by its ID
    @Query("""    
        SELECT n.nama_nasabah, 
       n.no_hp_nasabah as no_hp_nasabah, 
       p.nominal_transaksi, 
       p.tanggal, 
       p.lat, 
       p.lng, 
       p.id_pesanan,
       p.status_pesanan,
       n.alamat_nasabah, 
       SUM(ps.berat_perkiraan) AS total_berat
        FROM pesanan_sampah_keranjang_table AS p
        JOIN nasabah_table AS n ON p.id_nasabah = n.id
        JOIN pesanan_sampah_table AS ps ON p.id_pesanan = ps.id_pesanan_sampah_keranjang
        WHERE p.id_pesanan = :idPesanan
        GROUP BY n.id, p.nominal_transaksi, p.tanggal, p.lat, p.lng, p.status_pesanan
        """)
    suspend fun getDataDetailPesananSampahKeranjang(idPesanan: String): CardPesanan

    // Get List data detail card pesanan
    @Query("""
    SELECT kategori as nama_kategori, berat_perkiraan as berat
    FROM pesanan_sampah_table
    WHERE id_pesanan_sampah_keranjang = :idPesanan
    """)
    suspend fun getPesananSampah(idPesanan: String): List<CardDetailPesanan>

    // Update a PesananSampahKeranjang
    @Update
    suspend fun updatePesananSampahKeranjang(pesananSampahKeranjang: PesananSampahKeranjangEntity)

    // Delete a PesananSampahKeranjang
    @Delete
    suspend fun deletePesananSampahKeranjang(pesananSampahKeranjang: PesananSampahKeranjangEntity)

    // Delete all PesananSampahKeranjang records
    @Query("DELETE FROM pesanan_sampah_keranjang_table")
    suspend fun deleteAllPesananSampahKeranjang()

    @Query("""    
        SELECT n.nama_nasabah, 
       n.no_hp_nasabah as no_hp_nasabah, 
       p.nominal_transaksi, 
       p.tanggal, 
       p.lat, 
       p.lng,
       p.keterangan,
       p.id_pesanan,
       n.alamat_nasabah,
       p.status_pesanan, 
       SUM(ps.berat_perkiraan) AS total_berat
        FROM pesanan_sampah_keranjang_table AS p
        JOIN nasabah_table AS n ON p.id_nasabah = n.id
        JOIN pesanan_sampah_table AS ps ON p.id_pesanan = ps.id_pesanan_sampah_keranjang
        GROUP BY n.id, p.nominal_transaksi, p.tanggal, p.lat, p.lng, p.status_pesanan
        """)
    suspend fun getCombinedPesananData(): List<CardPesanan>


}