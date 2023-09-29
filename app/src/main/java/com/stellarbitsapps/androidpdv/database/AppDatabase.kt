package com.stellarbitsapps.androidpdv.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stellarbitsapps.androidpdv.database.dao.LayoutSettingsDao
import com.stellarbitsapps.androidpdv.database.dao.ReportDao
import com.stellarbitsapps.androidpdv.database.dao.SangriaDao
import com.stellarbitsapps.androidpdv.database.dao.TokensDao
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.Sangria
import com.stellarbitsapps.androidpdv.database.entity.Tokens

@Database(entities = [Tokens::class, Report::class, LayoutSettings::class, Sangria::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun tokensDao(): TokensDao
    abstract fun reportDao(): ReportDao
    abstract fun layoutSettingsDao(): LayoutSettingsDao
    abstract fun sangriaDao(): SangriaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "pdv.db")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}