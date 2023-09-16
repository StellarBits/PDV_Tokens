package com.stellarbitsapps.androidpdv.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
data class Report(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "date") val value: Long,
    @ColumnInfo(name = "initial_cash") val initialCash: Float,
    @ColumnInfo(name = "final_cash") val finalCash: Float,
    @ColumnInfo(name = "cash_one_tokens_sold") val cashOneTokensSold: Int,
    @ColumnInfo(name = "cash_two_tokens_sold") val cashTwoTokensSold: Int,
    @ColumnInfo(name = "cash_four_tokens_sold") val cashFourTokensSold: Int,
    @ColumnInfo(name = "cash_five_tokens_sold") val cashFiveTokensSold: Int,
    @ColumnInfo(name = "cash_six_tokens_sold") val cashSixTokensSold: Int,
    @ColumnInfo(name = "cash_eight_tokens_sold") val cashEightTokensSold: Int,
    @ColumnInfo(name = "cash_ten_tokens_sold") val cashTenTokensSold: Int
)