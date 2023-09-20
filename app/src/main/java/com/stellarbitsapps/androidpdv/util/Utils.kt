package com.stellarbitsapps.androidpdv.util

import android.graphics.Bitmap
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.stellarbitsapps.androidpdv.R
import java.text.NumberFormat

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

        fun createBitmapFromConstraintLayout(inflatedLayout: View): Bitmap {
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
    }
}