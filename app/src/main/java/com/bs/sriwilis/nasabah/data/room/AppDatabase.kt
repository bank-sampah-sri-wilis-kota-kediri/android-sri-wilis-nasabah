package com.bs.sriwilis.nasabah.data.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.bs.sriwilis.nasabah.data.room.dao.CatalogDao
import com.bs.sriwilis.nasabah.data.room.dao.CategoryDao
import com.bs.sriwilis.nasabah.data.room.dao.LoginResponseDao
import com.bs.sriwilis.nasabah.data.room.dao.NasabahDao
import com.bs.sriwilis.nasabah.data.room.dao.PenarikanDao
import com.bs.sriwilis.nasabah.data.room.entity.CatalogEntity
import com.bs.sriwilis.nasabah.data.room.entity.CategoryEntity
import com.bs.sriwilis.nasabah.data.room.entity.LoginResponseEntity
import com.bs.sriwilis.nasabah.data.room.entity.NasabahEntity
import com.bs.sriwilis.nasabah.data.room.entity.PenarikanEntity
import com.bs.sriwilispetugas.data.room.KeranjangTransaksiEntity
import com.bs.sriwilispetugas.data.room.PesananSampahEntity
import com.bs.sriwilispetugas.data.room.PesananSampahKeranjangEntity
import com.bs.sriwilispetugas.data.room.TransaksiSampahEntity
import com.bs.sriwilispetugas.data.room.dao.KeranjangTransaksiDao
import com.bs.sriwilispetugas.data.room.dao.PesananSampahDao
import com.bs.sriwilispetugas.data.room.dao.PesananSampahKeranjangDao
import com.bs.sriwilispetugas.data.room.dao.TransaksiSampahDao

@Database(
    entities = [LoginResponseEntity::class, NasabahEntity::class, CategoryEntity::class, PenarikanEntity::class, PesananSampahKeranjangEntity::class, PesananSampahEntity::class, CatalogEntity::class, KeranjangTransaksiEntity::class, TransaksiSampahEntity::class],
    version = 18,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun loginResponseDao(): LoginResponseDao
    abstract fun nasabahDao(): NasabahDao
    abstract fun categoryDao(): CategoryDao
    abstract fun penarikanDao(): PenarikanDao
    abstract fun pesananSampahKeranjangDao(): PesananSampahKeranjangDao
    abstract fun pesananSampahDao(): PesananSampahDao
    abstract fun transaksiSampahDao(): TransaksiSampahDao
    abstract fun keranjangTransaksiDao(): KeranjangTransaksiDao
    abstract fun catalogDao(): CatalogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "db_sw_nasabah"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}