package com.stellarbitsapps.androidpdv.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface LayoutSettingsDao {
    @Query("SELECT * FROM layoutsettings ORDER BY id ASC")
    fun getAll(): Flow<List<LayoutSettings>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg layoutSettings: LayoutSettings)
}