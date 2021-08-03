package com.brainwellnessspa.utility

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class MyValueFormatter : ValueFormatter(), IValueFormatter {
    private val mFormat: DecimalFormat = DecimalFormat("###,###,##0.00")
    override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
        return mFormat.format(value.toDouble())
    }
}