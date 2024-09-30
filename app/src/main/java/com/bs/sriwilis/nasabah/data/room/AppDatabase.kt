package com.bs.sriwilis.nasabah.data.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.bs.sriwilis.nasabah.data.room.dao.CategoryDao
import com.bs.sriwilis.nasabah.data.room.dao.LoginResponseDao
import com.bs.sriwilis.nasabah.data.room.dao.NasabahDao
import com.bs.sriwilis.nasabah.data.room.dao.PenarikanDao
import com.bs.sriwilis.nasabah.data.room.entity.CategoryEntity
import com.bs.sriwilis.nasabah.data.room.entity.LoginResponseEntity
import com.bs.sriwilis.nasabah.data.room.entity.NasabahEntity
import com.bs.sriwilis.nasabah.data.room.entity.PenarikanEntity

@Database(
    entities = [LoginResponseEntity::class, NasabahEntity::class, CategoryEntity::class, PenarikanEntity::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun loginResponseDao(): LoginResponseDao
    abstract fun nasabahDao(): NasabahDao
    abstract fun categoryDao(): CategoryDao
    abstract fun penarikanDao(): PenarikanDao

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