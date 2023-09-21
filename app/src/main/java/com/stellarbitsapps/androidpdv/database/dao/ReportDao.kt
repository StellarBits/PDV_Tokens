package com.stellarbitsapps.androidpdv.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellarbitsapps.androidpdv.database.entity.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM report ORDER BY date ASC")
    fun getAll(): Flow<List<Report>>

    @Query("SELECT final_cash FROM Report ORDER BY id DESC LIMIT 1")
    fun getLastFinalValue(): Float

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
            "payment_method_cash = payment_method_cash + :paymentMethodCash," +
            "payment_method_pix = payment_method_pix + :paymentMethodPix," +
            "payment_method_debit = payment_method_debit + :paymentMethodDebit," +
            "payment_method_credit = payment_method_credit + :paymentMethodCredit" +
            " WHERE date(date / 1000, 'unixepoch', 'localtime') == date('now', 'localtime')")
    suspend fun updateReportTokens(
        cashOne: Int,
        cashTwo: Int,
        cashFour: Int,
        cashFive: Int,
        cashSix: Int,
        cashEight: Int,
        cashTen: Int,
        paymentMethodCash: Int,
        paymentMethodPix: Int,
        paymentMethodDebit: Int,
        paymentMethodCredit: Int,
    )
}