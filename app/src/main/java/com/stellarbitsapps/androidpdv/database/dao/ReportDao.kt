package com.stellarbitsapps.androidpdv.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellarbitsapps.androidpdv.database.entity.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM report WHERE (initial_cash > 0 and final_cash == 0)")
    fun getAll(): Flow<Report>

    @Query("SELECT (initial_cash > 0 and final_cash == 0) FROM report ORDER BY id DESC LIMIT 1")
    fun cashRegisterIsOpen(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReport(vararg data: Report)

    @Query("UPDATE report SET " +
            "cash_one_tokens_sold = cash_one_tokens_sold + :cashOne," +
            "cash_two_tokens_sold = cash_two_tokens_sold + :cashTwo," +
            "cash_four_tokens_sold = cash_four_tokens_sold + :cashFour," +
            "cash_five_tokens_sold = cash_five_tokens_sold + :cashFive," +
            "cash_six_tokens_sold = cash_six_tokens_sold + :cashSix," +
            "cash_eight_tokens_sold = cash_eight_tokens_sold + :cashEight," +
            "cash_ten_tokens_sold = cash_ten_tokens_sold + :cashTen," +
            "payment_method_cash = payment_method_cash + :paymentCash," +
            "payment_method_pix = payment_method_pix + :paymentPix," +
            "payment_method_debit = payment_method_debit + :paymentDebit," +
            "payment_method_credit = payment_method_credit + :paymentCredit" +
            " WHERE (initial_cash > 0 and final_cash == 0)")
    suspend fun updateReportTokens(
        cashOne: Int,
        cashTwo: Int,
        cashFour: Int,
        cashFive: Int,
        cashSix: Int,
        cashEight: Int,
        cashTen: Int,
        paymentCash: Float,
        paymentPix: Float,
        paymentDebit: Float,
        paymentCredit: Float,
    )

    @Query("UPDATE report SET final_cash = :finalCash WHERE (initial_cash > 0 and final_cash == 0)")
    suspend fun updateReportFinalValue(finalCash: Float)

    @Query("DELETE FROM report WHERE (initial_cash > 0 and final_cash == 0)")
    suspend fun deleteReport()
}