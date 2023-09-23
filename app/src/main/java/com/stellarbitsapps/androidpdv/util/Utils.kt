package com.stellarbitsapps.androidpdv.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.android.sublcdlibrary.SubLcdHelper
import com.elotouch.AP80.sdkhelper.AP80PrintHelper
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.ui.initialcash.InitialCashFragment
import com.stellarbitsapps.androidpdv.ui.tokens.TokensFragment
import com.stellarbitsapps.androidpdv.ui.tokens.TokensViewModel
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
            tokenSettings: LayoutSettings,
            tokenValues: Array<Float>,
            selectedTokensList: ArrayList<Tokens>,
            printHelper: AP80PrintHelper,
            fragment: TokensFragment
        ) {
            var tokenPaymentValues = tokenValues

            if (tokenSettings.header.isEmpty() || tokenSettings.footer.isEmpty() || tokenSettings.image.isEmpty()) {
                val builder = AlertDialog.Builder(fragment.requireContext())

                builder.setTitle("Atenção!")
                builder.setMessage("O layout das fichas não foi configurado corretamente." +
                        "\nA operação será cancelada e o caixa reiniciado!")

                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    viewModel.deleteReport()
                    fragment.findNavController().navigate(R.id.configureTokenLayoutFragment)
                }

                val alertDialog = builder.create()
                alertDialog.show()
                return
            }

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
                    paymentCash = tokenPaymentValues[0],
                    paymentPix = tokenPaymentValues[1],
                    paymentDebit = tokenPaymentValues[2],
                    paymentCredit = tokenPaymentValues[3]
                )

                tokenPaymentValues = arrayOf(0f, 0f, 0f, 0f)

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
                            printToken(tokensPair.second, tokenSettings, printHelper, fragment)
                        }
                    }
                }
            }
        }

        fun showInSubDisplay(subLcdHelper: SubLcdHelper, fragment: InitialCashFragment, layoutSettings: LayoutSettings) {

            if (layoutSettings.header.isNotEmpty() && layoutSettings.image.isNotEmpty()) {

                val myBitmap = BitmapFactory.decodeStream(
                    fragment.requireActivity().contentResolver.openInputStream(layoutSettings.image.toUri())
                )

                val subDisplayLayout =
                    fragment.layoutInflater.inflate(R.layout.sub_display_layout, null)

                subDisplayLayout.findViewById<TextView>(R.id.tv_sub_display_header).text =
                    layoutSettings.header
                subDisplayLayout.findViewById<ImageView>(R.id.img_sub_display_image)
                    .setImageBitmap(myBitmap)

                val constraintLayout =
                    subDisplayLayout.findViewById<View>(R.id.sub_display_layout) as ConstraintLayout

                constraintLayout.isDrawingCacheEnabled = true

                constraintLayout.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )

                constraintLayout.layout(0, 0, constraintLayout.measuredWidth, constraintLayout.measuredHeight)
                constraintLayout.buildDrawingCache(true)

                val bmp = subLcdHelper.doRotateBitmap(constraintLayout.drawingCache, 90f)
                subLcdHelper.sendBitmap(bmp)
            }
        }

        @SuppressLint("SimpleDateFormat")
        private fun printToken(
            tokenValue: String,
            tokenSettings: LayoutSettings,
            printHelper: AP80PrintHelper,
            fragment: TokensFragment
        ) {
            val calendar = Calendar.getInstance()
            val format = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            val date = format.format(calendar.time)

            val myBitmap = BitmapFactory.decodeStream(
                fragment.requireActivity().contentResolver.openInputStream(tokenSettings.image.toUri())
            )

            val tokenLayout = fragment.layoutInflater.inflate(R.layout.token_layout, null)

            tokenLayout.findViewById<TextView>(R.id.tv_token_value).text = tokenValue
            tokenLayout.findViewById<ImageView>(R.id.img_token_image).setImageBitmap(myBitmap)

            val bitmap = createBitmapFromConstraintLayout(tokenLayout)

            printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
            printHelper.printData(tokenSettings.header, 35, 0, false, 1, 80, 0)
            printHelper.printData("VALE $tokenValue", 80, 0, false, 1, 80, 0)
            printHelper.printBitmap(bitmap, 2, 80)
            printHelper.printData(date, 30, 0, false, 0, 80, 0)
            printHelper.printData(tokenSettings.footer, 40, 0, false, 0, 80, 0)
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