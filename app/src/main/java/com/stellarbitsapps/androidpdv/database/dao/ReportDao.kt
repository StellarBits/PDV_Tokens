package com.stellarbitsapps.androidpdv.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.stellarbitsapps.androidpdv.database.entity.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM report ORDER BY date ASC")
    fun getAll(): Flow<List<Report>>
}