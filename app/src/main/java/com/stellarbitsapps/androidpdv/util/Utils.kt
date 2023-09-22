package com.stellarbitsapps.androidpdv.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.print.PrintHelper
import com.elotouch.AP80.sdkhelper.AP80PrintHelper
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.ui.initialcash.InitialCashFragment
import com.stellarbitsapps.androidpdv.ui.startscreen.StartScreenFragment
import com.stellarbitsapps.androidpdv.ui.tokens.TokensFragment
import com.stellarbitsapps.androidpdv.ui.tokens.TokensViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar

class Utils {
    companion object {
        fun formatCashTextMask(s: CharSequence, editText: EditText, watcher: TextWatcher): String {
            var current = ""

            if (s.toString() != current) {
                editText.removeTextChangedListener(watcher)

                val cleanString: String = s.replace("""[R$,.]""".toRegex(), "").trim()

                val parsed = cleanString.toDouble()
                val formatted = NumberFormat.getCurrencyInstance().format((parsed / 100))

                current = formatted
                editText.setText(formatted)
                editText.setSelection(formatted.length)

                editText.addTextChangedListener(watcher)
            }

            return current
        }

        private fun createBitmapFromConstraintLayout(inflatedLayout: View): Bitmap {
            val constraintLayout = inflatedLayout.findViewById<View>(R.id.token_layout) as ConstraintLayout

            constraintLayout.isDrawingCacheEnabled = true

            constraintLayout.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            constraintLayout.layout(0, 0, constraintLayout.measuredWidth, constraintLayout.measuredHeight)
            constraintLayout.buildDrawingCache(true)

            return constraintLayout.drawingCache
        }

        fun tokenPayment(
            viewModel: TokensViewModel,
            paymentMethodArray: Array<Int>,
            selectedTokensList: ArrayList<Tokens>,
            printHelper: AP80PrintHelper,
            fragment: TokensFragment
        ) {

            selectedTokensList.forEach { token ->

                // Update report in database
                val reportToBeUpdated = Report(
                    cashOneTokensSold = token.cashOne,
                    cashTwoTokensSold = token.cashTwo,
                    cashFourTokensSold = token.cashFour,
                    cashFiveTokensSold = token.cashFive,
                    cashSixTokensSold = token.cashSix,
                    cashEightTokensSold = token.cashEight,
                    cashTenTokensSold = token.cashTen,
                    paymentMethodCash = paymentMethodArray[0],
                    paymentMethodPix = paymentMethodArray[1],
                    paymentMethodDebit = paymentMethodArray[2],
                    paymentMethodCredit = paymentMethodArray[3]
                )

                // TODO Fix fragment being recreated.
                viewModel.updateReportTokens(reportToBeUpdated)

                // Print tokens
                val auxTokensList = listOf(
                    Pair(token.cashOne, "R$ 1,00"),
                    Pair(token.cashTwo, "R$ 2,00"),
                    Pair(token.cashFour, "R$ 4,00"),
                    Pair(token.cashFive, "R$ 5,00"),
                    Pair(token.cashSix, "R$ 6,00"),
                    Pair(token.cashEight, "R$ 8,00"),
                    Pair(token.cashTen, "R$ 10,00")
                )

                auxTokensList.forEach { tokensPair ->
                    if (tokensPair.first > 0) {
                        for (i in 1..tokensPair.first) {
                            printToken(tokensPair.second, printHelper, fragment)
                        }
                    }
                }
            }
        }

        @SuppressLint("SimpleDateFormat")
        private fun printToken(tokenValue: String, printHelper: AP80PrintHelper, fragment: TokensFragment) {
            val calendar = Calendar.getInstance()
            val format = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            val date = format.format(calendar.time)

            val imgFile = File(Environment.getExternalStorageDirectory().absolutePath + "/PDV/img_small.jpg")
            val myBitmap = BitmapFactory.decodeFile(imgFile.toString())

            val tokenLayout = fragment.layoutInflater.inflate(R.layout.token_layout, null)

            tokenLayout.findViewById<TextView>(R.id.tv_token_value).text = tokenValue
            tokenLayout.findViewById<ImageView>(R.id.img_token_image).setImageBitmap(myBitmap)

            val bitmap = createBitmapFromConstraintLayout(tokenLayout)

            printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
            printHelper.printData("FESTA DE SÃO JUDAS TADEU 2023", 35, 0, false, 1, 80, 0)
            printHelper.printData("VALE $tokenValue", 80, 0, false, 1, 80, 0)
            printHelper.printBitmap(bitmap, 2, 80)
            printHelper.printData(date, 30, 0, false, 0, 80, 0)
            printHelper.printData("AGRADECEMOS SUA PRESENÇA!", 40, 0, false, 0, 80, 0)
            printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
            printSpace(2, printHelper)
            printHelper.printStart()
            printHelper.cutPaper(1)
        }

        private fun printSpace(spaceSize: Int, printHelper: AP80PrintHelper) {
            if (spaceSize < 0) {
                return
            }
            val strSpace = StringBuilder()
            for (i in 0 until spaceSize) {
                strSpace.append("\n")
            }
            printHelper.printData(strSpace.toString(), 32, 0, false, 1, 80, 0)
        }
    }
}