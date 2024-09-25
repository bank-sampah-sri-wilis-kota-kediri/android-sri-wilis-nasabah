package com.bs.sriwilis.nasabah.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bs.sriwilis.nasabah.data.room.entity.LoginResponseEntity


@Dao
interface LoginResponseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(loginResponse: LoginResponseEntity)

    @Query("SELECT * FROM login_response_table WHERE id = :id")
    suspend fun getLoginResponseById(id: Int): LoginResponseEntity?

    @Query("DELETE FROM login_response_table")
    suspend fun deleteAll()

    @Query("""
        UPDATE login_response_table
        SET nama_nasabah = :name, 
            no_hp_nasabah = :phone,
            alamat_nasabah = :address, 
            gambar_nasabah = :gambar 
        WHERE id = 1
    """)
    suspend fun updatePhoneNumber(name: String, phone: String, address: String, gambar: String)
}